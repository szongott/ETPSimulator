package de.unihannover.dcsec.eviltwinprevention;

import java.util.ArrayList;

public class AP {
	private static final boolean DEBUG = false;

	private String bssid;
	private Position pos;

	private ArrayList<SeenNetworkList> snl;
	private ArrayList<String> cellIDs = new ArrayList<String>();
	private ArrayList<String> lacs = new ArrayList<String>();

	private long firstSeen;

	public AP(String bssid, Position pos, long timestamp, String cellID,
			String lac) {
		this.bssid = bssid;
		this.pos = pos;
		this.firstSeen = timestamp;
		this.snl = new ArrayList<SeenNetworkList>();

		this.cellIDs.add(cellID);
		this.lacs.add(lac);
	}

	public boolean isLearning(long timestamp) {
		return (timestamp - this.firstSeen < Configuration.LEARNING_PHASE_NEW_AP_LENGTH);
	}

	public ArrayList<SeenNetworkList> getAllEnvironments() {
		return snl;
	}

	public void setEnvironment(SeenNetworkList snl) {
		this.snl.add(snl);
	}

	public String getBSSID() {
		return bssid;
	}

	public Position getPosition() {
		return this.pos;
	}

	public void mergeNewPosition(Position pos) {
		Position newPos = new Position(
				(this.pos.getLatitude() + pos.getLatitude()) / 2,
				(this.pos.getLongitude() + pos.getLongitude()) / 2,
				(this.pos.getAccuracy() + pos.getAccuracy()) / 2);

		this.pos = newPos;
	}

	public boolean deleteSeenNet(int profile, String id) {
		snl.get(profile).removeNetwork(id);
		return true;
	}

	public boolean addSeenNet(int profile, String id) {
		SeenNetwork sn = ETPSimulation.getSeenNetwork(id);
		if (sn != null) {
			snl.get(profile).addNetwork(sn);
		}
		return true;
	}

	public String toString() {
		return "";
	}

	public long getLastSighting() {
		// TODO: Look up oldest timestamp from sightings and return it
		return 0l;
	}

	public boolean isCellInfoOK(String cellID, String lac) {
		boolean result = false;

		if (cellIDs.contains(cellID) && lacs.contains(lac)) {
			result = true;
		}

		return result;
	}

	public void improveCellInfo(String cellID, String lac) {
		if (!cellIDs.contains(cellID)) {
			cellIDs.add(cellID);
		}
		if (!lacs.contains(lac)) {
			lacs.add(lac);
		}
	}

	public void improveEnvironment(long timestamp, SeenNetworkList current) {
		ArrayList<SeenNetworkList> allEnvironments = getAllEnvironments();
		ArrayList<Double> jaccards = new ArrayList<Double>();

		// Initialize jaccard array
		for (int i = 0; i < getAllEnvironments().size(); i++) {
			jaccards.add(-1.0);
		}

		for (int i = 0; i < allEnvironments.size(); i++) {
			SeenNetworkList snl = allEnvironments.get(i);
			double jaccard = Utils.calculateJaccardIndex(snl, current);
			if (jaccard >= Configuration.JACCARD_MIN_IMPROVEMENT) {
				jaccards.add(i, jaccard);
			}
		}

		// Find maximum Jaccard
		double max = 0;
		int indexMax = -1;
		for (int i = 0; i < jaccards.size(); i++) {
			if (jaccards.get(i) > max) {
				max = jaccards.get(i);
				indexMax = i;
			}
		}

		if (indexMax != -1) {

			SeenNetworkList known = allEnvironments.get(indexMax);
			ArrayList<String> k = known.getIDsAsArray();
			ArrayList<String> c = current.getIDsAsArray();

			for (String id : k) {
				if (!c.contains(id)) {
					deleteSeenNet(indexMax, id);
				}
			}

			for (String id : c) {
				if (!k.contains(id)) {
					addSeenNet(indexMax, id);
				}
			}
		} else {
			if (isLearning(timestamp)) {
				this.snl.add(current);
			}
		}
	}
}
