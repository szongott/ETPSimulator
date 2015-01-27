package de.unihannover.dcsec.eviltwinprevention;

import java.util.HashMap;

public class ConnectedNetwork {
	private static final boolean DEBUG = Configuration.DEBUG;

	private HashMap<String, AP> apList;
	private String ssid;

	public ConnectedNetwork(String ssid) {
		this.ssid = ssid;
		apList = new HashMap<String, AP>();
	}

	public String getSSID() {
		return ssid;
	}

	public void addAP(AP ap) {
		if (!apList.containsKey(ap.getBSSID())) {
			apList.put(ap.getBSSID(), ap);
			if (DEBUG)
				LogUtil.getInstance().log(
						"DBG: Added new Accesspoint " + ap.getBSSID()
								+ " to network " + this.getSSID());
		}
	}

	public boolean containsAP(String bssid) {
		return apList.containsKey(bssid);
	}

	public HashMap<String, AP> getAPList() {
		return apList;
	}
}
