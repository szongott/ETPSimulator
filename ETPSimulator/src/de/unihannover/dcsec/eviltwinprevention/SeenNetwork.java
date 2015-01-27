package de.unihannover.dcsec.eviltwinprevention;

public class SeenNetwork {

	private String id;

	private String ssid;
	private String bssid;
	private String level;
	private String capabilities;
	private String frequency;

	public SeenNetwork(String idFromLogentries, String ssid, String bssid,
			String level, String capabilities, String frequency) {
		this.id = ssid + bssid + capabilities;
		this.ssid = ssid;
		this.bssid = bssid;
		this.level = level;
		this.capabilities = capabilities;
		this.frequency = frequency;

	}

	public String getID() {
		return id;
	}
}
