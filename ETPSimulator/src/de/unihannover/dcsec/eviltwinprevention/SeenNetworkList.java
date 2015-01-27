package de.unihannover.dcsec.eviltwinprevention;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SeenNetworkList implements Iterable<String> {
	private static final boolean DEBUG = Configuration.DEBUG;

	private HashMap<String, SeenNetwork> networkList;

	public SeenNetworkList() {
		networkList = new HashMap<String, SeenNetwork>();
	}

	public SeenNetworkList(String list) {
		this();
		for (String newid : list.split(",")) {
			SeenNetwork sn = ETPSimulation.getSeenNetwork(newid);
			if (sn != null) {
				addNetwork(sn);
			}
		}
	}

	public boolean addNetwork(SeenNetwork sn) {
		if (networkList.containsKey(sn.getID())) {
			return false;
		} else {
			if (DEBUG)
				LogUtil.getInstance().log(
						"Added new network to SeenNetworkList: " + sn.getID());
			networkList.put(sn.getID(), sn);
			return true;
		}
	}

	public void removeNetwork(String id) {
		networkList.remove(id);
	}

	public ArrayList<String> getIDsAsArray() {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = networkList.keySet().iterator();

		while (it.hasNext()) {
			list.add(networkList.get(it.next()).getID());
		}
		return list;
	}

	public boolean containsNetwork(SeenNetwork sn) {
		return networkList.containsKey(sn.getID());
	}

	public boolean containsNetwork(String id) {
		if (networkList.containsKey(id)) {
			return true;
		}
		return false;
	}

	public int size() {
		return networkList.size();
	}

	public SeenNetwork getNetwork(String id) {
		return networkList.get(id);
	}

	public static boolean isEnvironmentOK(AP ap, SeenNetworkList current) {
		if (Configuration.USE_JACCARD_ALGORITHM) {
			return isEnvironmentOK_JaccardIndex(ap, current);
		}
		System.out
				.println("ERROR: There is no environmental algorithm, that can be used...");
		System.exit(1);
		return false;
	}

	/**
	 * Implements the Jaccard coefficient
	 * 
	 * @param ap
	 * @param current
	 * @return
	 */
	public static boolean isEnvironmentOK_JaccardIndex(AP ap,
			SeenNetworkList current) {
		ArrayList<SeenNetworkList> allEnvironments = ap.getAllEnvironments();

		for (SeenNetworkList knownList : allEnvironments) {
			double jaccard = Utils.calculateJaccardIndex(knownList, current);
			if (jaccard >= Configuration.JACCARD_ENVIRONMENT_OK) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<String> iterator() {
		return networkList.keySet().iterator();
	}

	public String toString() {
		Iterator<String> it = networkList.keySet().iterator();
		String str = "[";

		while (it.hasNext()) {
			str += networkList.get(it.next()).getID() + ",";
		}
		return str + "]";
	}
}
