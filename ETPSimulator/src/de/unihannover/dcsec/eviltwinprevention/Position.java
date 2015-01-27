package de.unihannover.dcsec.eviltwinprevention;

public class Position {
	private double latitude;
	private double longitude;
	private double accuracy;

	public Position(double lat, double lon, double acc) {
		latitude = lat;
		longitude = lon;
		accuracy = acc;
	}

	public Position(String latStr, String lonStr, String accStr) {
		latitude = Float.valueOf(latStr);
		longitude = Float.valueOf(lonStr);
		accuracy = Float.valueOf(accStr);
	}

	public boolean isAvailable() {
		if (!(latitude == 999.0f) && !(longitude == 999.0f)) {
			return true;
		}
		return false;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

}
