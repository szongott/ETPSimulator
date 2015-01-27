package de.unihannover.dcsec.eviltwinprevention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.unihannover.dcsec.eviltwinprevention.dao.FileAccessDAO;

public class ETPSimulation {

	// TODO: This list has to be adjusted to the new input data
	private static final List<String> allDevices = Arrays.asList("7de08ac6ad",
			"9c8504c7f8", "ff5ca49ff1", "cf40d98b41", "b6792c0ff8",
			"e4a63b91be", "90ad23bd0d", "75dd3f6581", "ded0a95c75",
			"3ea666603a", "c703424959", "a943470423", "aa2744c58d",
			"d8e5a4dcab", "03737b105c", "c431aa0178", "3a67f2f380",
			"2cf90d0a30", "0cf8a8650f", "a4f1896cf2", "345e8cc7a2",
			"ca17752ec7", "409b49e19a", "69149a9333", "33ab06df76",
			"0d4c37e059", "6b9a5112fe", "8e697a134a", "d173edf154",
			"877da0693c", "4857ca6bd7", "ea14f4f28e", "08ff6ae5f1",
			"4796e84d5c", "8bea2a7837", "883d4d018a", "6d1fa27352",
			"ebf48b47ed", "8c78db9b03", "45e12d2432", "ffa87c3df4",
			"a8a53d7618", "63e7fdf61b", "af1176fe71", "0388a68630",
			"71ac4f9e95", "3176333410", "76894b83e6", "02670f646f",
			"0f0fc6ed36", "9e29faa3e1", "1dd5d9e6a1", "e05a384fd5",
			"524c15360c", "e151831d6d", "47ac9b301d", "6631ee23a7",
			"a6ba29cd7f", "648d4d3900", "8cebad3274", "d576b2c731",
			"5d23ae0c43", "c139444b8b", "5d4953fc45", "80bddadc0d",
			"454fe0add7", "a9b22ff5ac", "7e71aa4c0a", "b1d9447710",
			"5a34796853", "083b2d4d13", "b988a77565", "9545d65c5d",
			"ef3a35ffea", "e6e8066955", "df9ca1b4c3", "0bff596740",
			"f735563d9c", "ec34907910", "f0a7f16e70", "913a28dabd",
			"f0d4a840d7", "275da5f5e0", "8b95f628ea");

	// Device for single device simulation
	private static String deviceID = "69149a9333";

	public static HashMap<String, HashMap<String, String>> seenNetsList;
	public static HashMap<String, HashMap<String, String>> configuredNetsList;

	public static void main(String[] args) {
		FileAccessDAO dao = new FileAccessDAO();
		seenNetsList = dao.getAllSeenNets();
		configuredNetsList = dao.getAllConfiguredNets();

		// Simulate all parameters
		int parameterstudy = 0;

		if (parameterstudy == -1) {
			// simulate only one device
			simulateOneDevice(deviceID);
		}
		if (parameterstudy == 0) {
			// Simulate basic configuration
			simulateOneParameterSet();
		} else if (parameterstudy == 2) {
			for (int maxDistance = 0; maxDistance <= 2000; maxDistance += 10) {
				Configuration.MAXIMUM_DISTANCE_THRESHOLD = maxDistance;
				LogUtil.getInstance().setOverallLogFilename(
						"overall_distance_" + maxDistance + ".log");
				System.out.println("overall_distance_" + maxDistance + ".log");
				simulateOneParameterSet();
			}

		} else if (parameterstudy == 3) {
			int steps = 10;
			for (int j = 0; j <= steps; j++) {
				double jaccard = (double) j / (double) steps;

				Configuration.JACCARD_ENVIRONMENT_OK = jaccard;
				LogUtil.getInstance().setOverallLogFilename(
						"overall_jaccard_" + jaccard + ".log");
				System.out.println("overall_jaccard_" + jaccard + ".log");
				simulateOneParameterSet();
			}
		} else if (parameterstudy == 4) {
			for (int i = 0; i <= 14; i++) {
				Configuration.LEARNING_PHASE_NEW_AP_LENGTH = i * 24 * 60 * 60
						* 1000;
				LogUtil.getInstance().setOverallLogFilename(
						"overall_apLearningPhase_" + i + ".log");
				System.out.println("overall_apLearningPhase_" + i + ".log");
				simulateOneParameterSet();
			}
		}
	}

	private static void simulateOneDevice(String deviceID) {
		FileAccessDAO dao = new FileAccessDAO();
		Simulator sim = new Simulator(configuredNetsList);

		HashMap<String, ArrayList<HashMap<String, String>>> allData = new HashMap<String, ArrayList<HashMap<String, String>>>();
		allData.put(deviceID, dao.getDataForDevice(deviceID));
		sim.setData(allData.get(deviceID));
		Result resultOneSim = sim.start();
		// LogUtil.getInstance().printSumTable();
		// resultOneSim.printStats();

	}

	private static void simulateOneParameterSet() {
		// MySQLAccessDAO dao = new MySQLAccessDAO();
		FileAccessDAO dao = new FileAccessDAO();

		// Get everything in memory
		HashMap<String, ArrayList<HashMap<String, String>>> allData = new HashMap<String, ArrayList<HashMap<String, String>>>();
		for (String id : allDevices) {
			allData.put(id, dao.getDataForDevice(id));
		}

		int i = 0;
		for (String id : allDevices) {
			LogUtil.getInstance().setDevice(id);
			Simulator sim = new Simulator(configuredNetsList);
			sim.setData(allData.get(id));
			sim.setID(id);
			sim.start();
			LogUtil.getInstance().createSumTable();
			// LogUtil.getInstance().printSumTable();
			// result.printStats();

			i++;
			System.out.println(i + "/" + allDevices.size() + " done");
			LogUtil.getInstance().printSumTable();
			LogUtil.getInstance().clear();
		}
		LogUtil.getInstance().printOverallSumTable();
		LogUtil.getInstance().clearOverall();
	}

	public static SeenNetwork getSeenNetwork(String idFromLogentries) {
		SeenNetwork sn = null;
		HashMap<String, String> rawSN = seenNetsList.get(idFromLogentries);
		if (seenNetsList.containsKey(idFromLogentries)) {
			sn = new SeenNetwork(idFromLogentries, rawSN.get("ssid"),
					rawSN.get("bssid"), rawSN.get("level"),
					rawSN.get("capabilities"), rawSN.get("frequency"));
		}
		return sn;
	}
}
