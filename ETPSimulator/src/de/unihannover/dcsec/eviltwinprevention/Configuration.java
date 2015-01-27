package de.unihannover.dcsec.eviltwinprevention;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
	public static final boolean DEBUG = false;

	// Logfile
	public static final String LOGFILE = "log/log.txt";

	public static final boolean ACCOUNT_UNENCRYPTED = true;
	public static final boolean ACCOUNT_WPA_PSK = false;
	public static final boolean ACCOUNT_WPA_ENTERPRISE = false;

	// How often an AP should be seen before it is added to the list of known
	// APs for this network
	public static final int BSSID_DELETION_THRESHOLD = -3;
	public static final int BSSID_ADDITION_THRESHOLD = 0;

	// How great is the distance in which two locations are handled as equal?
	public static double MAXIMUM_DISTANCE_THRESHOLD = 100.0;

	// How long is the engine in learning mode before predicting (in ms)
	public static final long INITIAL_LEARNING_PHASE_LENGTH = 0 * 24 * 60 * 60
			* 1000;

	// How long the environment is learned for each new AP
	public static long LEARNING_PHASE_NEW_AP_LENGTH = 7 * 24 * 60 * 60 * 1000;

	// Which algorithm is used to compare network environments
	public static boolean USE_JACCARD_ALGORITHM = true;

	// Minimum Jaccard index, if lower, an alarm is raised
	public static double JACCARD_ENVIRONMENT_OK = 0.7;
	public static double JACCARD_MIN_IMPROVEMENT = 0.7;

	// SSIDs that are filtered out, not being processed by ETPEngine
	public static final List<String> FILTER_SSIDS = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			// add("test-ssid1");
			// add("test-ssid2");
		}
	};

}
