package de.unihannover.dcsec.eviltwinprevention;

import java.util.HashMap;
import java.util.List;

public class ETPEngine {
	public static final int RETURNCODE_NOT_SET = -1;
	public static final int RETURNCODE_CONNECTION_OK = 0;
	public static final int RETURNCODE_UNKNOWN_NETWORK_SSID = 1;
	public static final int RETURNCODE_UNKNOWN_BSSID = 2;
	public static final int RETURNCODE_UNKNOWN_LOCATION = 3;
	public static final int RETURNCODE_LOCATION_NOT_AVAILABLE = 4;
	public static final int RETURNCODE_ENVIRONMENT_SUSPICIOUS = 5;

	// Add SSIDs here that will not be analyzed by the recognition engine
	private static final List<String> filterSSIDs = Configuration.FILTER_SSIDS;
	private ConnectedNetworkList cnl;
	private HashMap<String, HashMap<String, String>> confNL;

	private long firstTimestamp = Long.MAX_VALUE;

	public ETPEngine(HashMap<String, HashMap<String, String>> confNL) {
		cnl = new ConnectedNetworkList();
		this.confNL = confNL;

	}

	public int handleNewConnection(String id, HashMap<String, String> hm) {
		return handleNewConnectionV7(id, hm);
	}

	/**
	 * Version 7 of engine
	 * 
	 * @param hm
	 * @return return code
	 */
	public int handleNewConnectionV7(String id, HashMap<String, String> hm) {
		long timestamp = Long.valueOf(hm.get("timestamp"));
		if (timestamp < firstTimestamp) {
			firstTimestamp = timestamp;
		}

		int returncode = RETURNCODE_NOT_SET;
		String foundSSID = Utils.trimQuotesFromString(hm.get("connected.ssid"));

		boolean initialLearning = false;
		if (timestamp - firstTimestamp < Configuration.INITIAL_LEARNING_PHASE_LENGTH) {
			initialLearning = true;
		}

		// Filter out specific networks
		if (filterSSIDs.contains(foundSSID)) {
			return returncode;
		}

		if (!isVulnerableNetwork(hm.get("connected.ssid"), hm.get("configured"))) {
			returncode = RETURNCODE_NOT_SET;
			return returncode;
		}

		String foundBSSID = hm.get("connected.bssid");
		Position pos = new Position(hm.get("locations.lat"),
				hm.get("locations.longi"), hm.get("locations.acc"));

		String cellID = hm.get("cellID");
		String lac = hm.get("lac");

		SeenNetworkList currentEnv = new SeenNetworkList(hm.get("seenNets"));

		// Check if known SSID
		if (cnl.containsNetwork(foundSSID)) {

			// Check if known BSSID for WiFi network
			if (cnl.getNetwork(foundSSID).containsAP(foundBSSID)) {
				AP ap = cnl.getNetwork(foundSSID).getAPList().get(foundBSSID);

				// Check if networkenvironment is known
				if (SeenNetworkList.isEnvironmentOK(ap, currentEnv)) {
					ap.improveEnvironment(timestamp, currentEnv);
					improveCellInfo(ap, cellID, lac);
					returncode = RETURNCODE_CONNECTION_OK;
				} else {
					if (ap.isLearning(timestamp))
						ap.improveEnvironment(timestamp, currentEnv);

					// Check if CellInfo is known
					if (ap.isCellInfoOK(cellID, lac)) {
						ap.improveEnvironment(timestamp, currentEnv);
						improveCellInfo(ap, cellID, lac);
						returncode = RETURNCODE_CONNECTION_OK;
					} else {
						if (initialLearning)
							improveCellInfo(ap, cellID, lac);

						// Check if position is available for this connection
						if (pos.isAvailable()) {
							// Check if position is already known for this
							// network
							if (GeoUtils.isDistanceShortEnough(
									ap.getPosition(), pos)) {
								learnNewLocation(ap, pos);
								returncode = RETURNCODE_CONNECTION_OK;
							} else {
								if (initialLearning)
									learnNewLocation(ap, pos);
								returncode = RETURNCODE_UNKNOWN_LOCATION;
							}
						} else {
							returncode = RETURNCODE_LOCATION_NOT_AVAILABLE;
						}
					}
				}
			} else {
				learnNewAccessPoint(hm);
				returncode = RETURNCODE_UNKNOWN_BSSID;
			}
		} else {
			learnNewNetwork(hm);
			// No warning if new SSID is used
			returncode = RETURNCODE_CONNECTION_OK;
		}

		if (initialLearning)
			returncode = RETURNCODE_CONNECTION_OK;
		return returncode;
	}

	private void learnNewNetwork(HashMap<String, String> hm) {
		// Learn new network
		Position pos = new Position(hm.get("locations.lat"),
				hm.get("locations.longi"), hm.get("locations.acc"));
		pos = new Position(hm.get("locations.lat"), hm.get("locations.longi"),
				hm.get("locations.acc"));
		long timestamp = Long.valueOf(hm.get("timestamp"));

		String cellID = hm.get("cellID");
		String lac = hm.get("lac");

		AP ap = new AP(hm.get("connected.bssid"), pos, timestamp, cellID, lac);
		SeenNetworkList snl = new SeenNetworkList(hm.get("seenNets"));
		ap.setEnvironment(snl);
		ConnectedNetwork n = new ConnectedNetwork(Utils.trimQuotesFromString(hm
				.get("connected.ssid")));
		n.addAP(ap);

		cnl.addNetwork(n);
	}

	private void learnNewAccessPoint(HashMap<String, String> hm) {
		// Learn new access point
		String foundSSID = Utils.trimQuotesFromString(hm.get("connected.ssid"));
		String foundBSSID = hm.get("connected.bssid");
		Position pos = new Position(hm.get("locations.lat"),
				hm.get("locations.longi"), hm.get("locations.acc"));
		SeenNetworkList snl = new SeenNetworkList(hm.get("seenNets"));

		long timestamp = Long.valueOf(hm.get("timestamp"));

		String cellID = hm.get("cellID");
		String lac = hm.get("lac");

		try {
			AP ap = new AP(foundBSSID, pos, timestamp, cellID, lac);
			ap.setEnvironment(snl);
			cnl.getNetwork(foundSSID).addAP(ap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void improveCellInfo(AP ap, String cellID, String lac) {
		ap.improveCellInfo(cellID, lac);
	}

	private void learnNewLocation(AP ap, Position pos) {
		ap.mergeNewPosition(pos);
	}

	@Deprecated
	private boolean isEncryptedNetwork(String connectedSSID,
			String configuredIDList) {

		for (String singleID : configuredIDList.split(",")) {
			HashMap<String, String> confNet = confNL.get(singleID);
			if (confNet != null) {
				String ssid = confNet.get("ssid");
				if (Utils.trimQuotesFromString(connectedSSID).equals(
						Utils.trimQuotesFromString(ssid))
						&& !confNet.get("keyManagement").contains("0")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isVulnerableNetwork(String connectedSSID,
			String configuredIDList) {

		for (String singleID : configuredIDList.split(",")) {
			HashMap<String, String> confNet = confNL.get(singleID);
			if (confNet != null) {
				String ssid = confNet.get("ssid");

				if (Configuration.ACCOUNT_UNENCRYPTED) {
					if (Utils.trimQuotesFromString(connectedSSID).equals(
							Utils.trimQuotesFromString(ssid))
							&& confNet.get("keyManagement").contains("0")) {
						return true;
					}
				}

				if (Configuration.ACCOUNT_WPA_ENTERPRISE) {
					if (Utils.trimQuotesFromString(connectedSSID).equals(
							Utils.trimQuotesFromString(ssid))
							&& (confNet.get("keyManagement").contains("2") || confNet
									.get("keyManagement").contains("3"))) {
						return true;
					}
				}

				if (Configuration.ACCOUNT_WPA_PSK) {
					if (Utils.trimQuotesFromString(connectedSSID).equals(
							Utils.trimQuotesFromString(ssid))
							&& (confNet.get("keyManagement").contains("1"))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String translateReturnCode(int handleNewConnection) {
		switch (handleNewConnection) {
		case -1:
			return "Not set";
		case 0:
			return "Connection OK";
		case 1:
			return "Unknown SSID (Alert: Unknown Network [SSID])";
		case 2:
			return "Unknown BSSID for known SSID (Alert: Unknown AP [BSSID] for this network)";
		case 3:
			return "Unknown Location for known SSID and BSSID (Alert: Unknown location)";
		case 4:
			return "Location is not available (Warning: Location not available)";
		case 5:
			return "Suscpicious Network Environment (Warning: Network Environment)";
		default:
			return "!!! UNKNOWN STATE !!!";
		}
	}

}
