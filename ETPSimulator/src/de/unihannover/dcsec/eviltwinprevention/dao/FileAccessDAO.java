package de.unihannover.dcsec.eviltwinprevention.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.unihannover.dcsec.eviltwinprevention.Utils;

public class FileAccessDAO implements iDAO {
	public static final ArrayList<String> DB_FIELDS_LOGENTRIES = new ArrayList<String>(
			Arrays.asList("id", "timestamp", "duration", "connected.bssid",
					"connected.ssid", "seenNets", "locations.ts",
					"locations.lat", "locations.longi", "locations.acc",
					"cellID", "lac", "configured"));

	public static final ArrayList<String> DB_FIELDS_SEENNETS = new ArrayList<String>(
			Arrays.asList("id", "bssid", "ssid", "level", "capabilities",
					"frequency"));

	public FileAccessDAO() {
	}

	public ArrayList<HashMap<String, String>> getDataForDevice(String device) {
		String file = "deviceLogs/" + device + ".result";

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				HashMap<String, String> hm = new HashMap<String, String>();

				// Generating line array
				line = line.replace("\\", "");
				String[] la = line.split("\\|", -1);

				hm.put("id", Utils.trimQuotesFromString(la[0]));
				hm.put("timestamp", Utils.trimQuotesFromString(la[1]));
				hm.put("duration", Utils.trimQuotesFromString(la[2]));
				hm.put("connected.bssid", Utils.trimQuotesFromString(la[20]));
				hm.put("connected.ssid", Utils.trimQuotesFromString(la[21]));
				hm.put("seenNets", Utils.trimQuotesFromString(la[10]));
				hm.put("locations.ts", Utils.trimQuotesFromString(la[26]));
				hm.put("locations.lat", Utils.trimQuotesFromString(la[29]));
				hm.put("locations.longi", Utils.trimQuotesFromString(la[27]));
				hm.put("locations.acc", Utils.trimQuotesFromString(la[28]));
				hm.put("cellID", Utils.trimQuotesFromString(la[16]));
				hm.put("lac", Utils.trimQuotesFromString(la[17]));
				hm.put("configured", Utils.trimQuotesFromString(la[18]));

				list.add(hm);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	public HashMap<String, HashMap<String, String>> getAllSeenNets() {
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader("seenNetsDB/seenNets.dat"));
			String line = null;
			while ((line = br.readLine()) != null) {
				HashMap<String, String> hm = new HashMap<String, String>();

				// Generating line array
				line = line.replace("\\", "");
				String[] la = line.split("\\|", -1);

				String id = Utils.trimQuotesFromString(la[0]);

				hm.put("bssid", Utils.trimQuotesFromString(la[1]));
				hm.put("ssid", Utils.trimQuotesFromString(la[2]));
				hm.put("level", Utils.trimQuotesFromString(la[3]));
				hm.put("capabilities", Utils.trimQuotesFromString(la[4]));
				hm.put("frequency", Utils.trimQuotesFromString(la[5]));

				map.put(id, hm);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, HashMap<String, String>> getAllConfiguredNets() {
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(
					"configuredNetsDB/configured.dat"));
			String line = null;
			while ((line = br.readLine()) != null) {
				HashMap<String, String> hm = new HashMap<String, String>();

				// Generating line array
				line = line.replace("\\", "");
				String[] la = line.split("\\|", -1);

				String id = Utils.trimQuotesFromString(la[0]);

				hm.put("keyManagement", Utils.trimQuotesFromString(la[1]));
				hm.put("ssid", Utils.trimQuotesFromString(la[2]));
				hm.put("groupCiphers", Utils.trimQuotesFromString(la[3]));

				map.put(id, hm);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}
