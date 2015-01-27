package de.unihannover.dcsec.eviltwinprevention;

import java.util.HashMap;

public class ConnectedNetworkList {
	private static final boolean DEBUG = Configuration.DEBUG;
	private HashMap<String, ConnectedNetwork> connectedNetworkList;

	public ConnectedNetworkList() {
		connectedNetworkList = new HashMap<String, ConnectedNetwork>();
	}

	public boolean addNetwork(ConnectedNetwork n) {
		if (connectedNetworkList.containsKey(n.getSSID())) {
			return false;
		} else {
			if (DEBUG)
				LogUtil.getInstance().log(
						"Learning new network: " + n.getSSID());
			connectedNetworkList.put(n.getSSID(), n);
			return true;
		}
	}

	public boolean containsNetwork(ConnectedNetwork n) {
		return connectedNetworkList.containsKey(n.getSSID());
	}

	public boolean containsNetwork(String ssid) {
		if (connectedNetworkList.containsKey(ssid)) {
			return true;
		}
		return false;
	}

	public ConnectedNetwork getNetwork(String ssid) {
		return connectedNetworkList.get(ssid);
	}
}
