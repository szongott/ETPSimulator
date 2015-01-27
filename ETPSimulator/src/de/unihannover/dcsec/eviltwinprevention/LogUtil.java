package de.unihannover.dcsec.eviltwinprevention;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LogUtil {

	private boolean writeToDisk = true;
	private boolean printToScreen = true;

	private boolean absoluteValues = false;

	private String device;
	private String filename;
	private long firstTimestamp;
	private final int SECONDS_PER_DAY = 86400;

	private static LogUtil instance = null;

	private static List<LogRow> logTable;
	private static HashMap<Long, ReturncodeArray> sumTable;
	private static HashMap<Long, ReturncodeArray> overallSumTable;
	private static HashMap<Long, Set<String>> devicesPerDay;

	private final boolean PRINT_SUM_TO_FILE = true;

	private String overallLogFilename = null;

	// Timestamp of start time of study, no entries can be newer than that
	private long startOfStudyTimestamp = 1387238400000l;

	private LogUtil() {

	}

	public static LogUtil getInstance() {
		if (instance == null) {
			instance = new LogUtil();
		}
		return instance;
	}

	public void setOverallLogFilename(String str) {
		this.overallLogFilename = str;
	}

	public void clear() {
		device = null;
		filename = null;
		firstTimestamp = 0l;
		logTable = null;
		sumTable = null;
	}

	public void clearOverall() {
		device = null;
		filename = null;
		firstTimestamp = 0l;
		logTable = null;
		sumTable = null;
		overallSumTable = null;
		devicesPerDay = null;
	}

	public void setDevice(String device) {
		this.device = device;
		this.filename = "log/" + device + ".log";
	}

	public void setFirstTimestamp(long ts) {
		this.firstTimestamp = ts;
	}

	public void logConnectionAttempt(String tsString, int returncode) {
		if (logTable == null) {
			logTable = new ArrayList<LogUtil.LogRow>();
		}

		long timestamp = Long.valueOf(tsString);
		// Calculating the day of usage
		// if firstTimestamp is used normally, each device will be counted from
		// 0 to max usage.
		// if firstTimestamp is set to startOfStudyTimestamp, all devices will
		// have the same calculation of time and specific events like holidays
		// might be visible in the output data

		// firstTimestamp = startOfStudyTimestamp;
		long day = ((timestamp - firstTimestamp) / (SECONDS_PER_DAY * 1000)) + 1;
		if (Configuration.DEBUG) {
			LogUtil.getInstance().log(
					":" + day + "," + timestamp + "," + returncode);
		}
		LogRow r = new LogRow(day, timestamp, returncode);
		logTable.add(r);
	}

	public void createSumTable() {
		// if not existing --> create
		if (overallSumTable == null) {
			overallSumTable = new HashMap<Long, LogUtil.ReturncodeArray>();
		}

		if (devicesPerDay == null) {
			devicesPerDay = new HashMap<Long, Set<String>>();
		}

		if (sumTable == null) {
			sumTable = new HashMap<Long, LogUtil.ReturncodeArray>();
		}

		for (LogRow r : logTable) {

			// Add device to this day
			Set<String> s = null;
			if (devicesPerDay.containsKey(r.day)) {
				s = devicesPerDay.get(r.day);
			} else {
				s = new HashSet<String>();
			}
			s.add(device);
			devicesPerDay.put(r.day, s);

			// For sumTable
			if (sumTable.containsKey(r.day)) {
				ReturncodeArray retA = sumTable.get(r.day);
				retA.increaseRC(r.returncode);
				sumTable.put(r.day, retA);
			} else {
				ReturncodeArray retA = new ReturncodeArray();
				retA.increaseRC(r.returncode);
				sumTable.put(r.day, retA);
			}

			// For OverallSumTable
			if (overallSumTable.containsKey(r.day)) {
				ReturncodeArray retA = overallSumTable.get(r.day);
				retA.increaseRC(r.returncode);
				overallSumTable.put(r.day, retA);
			} else {
				ReturncodeArray retA = new ReturncodeArray();
				retA.increaseRC(r.returncode);
				overallSumTable.put(r.day, retA);
			}
		}
		// printDevicesPerDayTable();
	}

	public void printDevicesPerDayTable() {
		Object[] keys = devicesPerDay.keySet().toArray();
		Arrays.sort(keys);

		for (Object key : keys) {
			System.out.print(key + "=");
			Set<String> set = devicesPerDay.get(key);
			for (String s : set) {
				System.out.print(s + ",");
			}
			System.out.println();
		}
	}

	public void printSumTable() {
		// createSumTable();

		BufferedWriter writer = null;

		Object[] keys = sumTable.keySet().toArray();
		Arrays.sort(keys);
		for (Object key : keys) {
			System.out.println((Long) key);
		}

		for (long i = 1; i <= 74; i++) {
			if (sumTable.get(i) != null) {
				System.out.print(sumTable.get(i).sumOfAllWarnings() + "\t");
			} else {
				System.out.print("-\t");
			}
		}
		System.out.println();

		if (PRINT_SUM_TO_FILE) {
			try {
				writer = new BufferedWriter(new FileWriter(
						"log/allWarnings.dat", true));

				writer.write(device + "\t");
				for (long i = 1; i <= 74; i++) {
					if (sumTable.get(i) != null) {
						writer.write(sumTable.get(i).sumOfAllWarnings() + "\t");
					} else {
						writer.write("-\t");
					}
				}

				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void printOverallSumTable() {
		BufferedWriter writer = null;

		Object[] keys = overallSumTable.keySet().toArray();
		Arrays.sort(keys);

		for (Object key : keys) {
			int nrDevices = devicesPerDay.get(key).size();
			if (absoluteValues) {
				System.out.println((Long) key + "\t" + nrDevices + "\t"
						+ overallSumTable.get(key).divideBy(1));
			} else {
				System.out.println((Long) key + "\t" + nrDevices + "\t"
						+ overallSumTable.get(key).divideBy(nrDevices));
			}

			if (PRINT_SUM_TO_FILE) {
				try {
					String filename = overallLogFilename == null ? "log/overall.log"
							: "log/" + overallLogFilename;

					writer = new BufferedWriter(new FileWriter(filename, true));
					if (absoluteValues) {
						writer.write((Long) key + "\t" + nrDevices + "\t"
								+ overallSumTable.get(key).divideBy(1));
					} else {
						writer.write((Long) key + "\t" + nrDevices + "\t"
								+ overallSumTable.get(key).divideBy(nrDevices));
					}
					writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void log(boolean linebreak, String str) {
		BufferedWriter writer = null;
		if (writeToDisk) {
			try {
				writer = new BufferedWriter(new FileWriter(filename, true));
				if (linebreak) {
					writer.write(str);
					writer.newLine();
				} else {
					writer.write(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (printToScreen) {
			if (linebreak) {
				System.out.println(str);
			} else {
				System.out.print(str);
			}
		}

	}

	public void log(String str) {
		log(true, str);

	}

	private class LogRow {
		public long day;
		public long timestamp;
		public int returncode;

		public LogRow(long day, long timestamp, int returncode) {
			this.day = day;
			this.timestamp = timestamp;
			this.returncode = returncode;
		}

		public String toString() {
			return "(" + day + "," + timestamp + "," + returncode + ")";
		}
	}

	public class ReturncodeArray {

		private final int NR_OF_DIFFERENT_CODES = 7;
		public int[] ret;

		public ReturncodeArray() {
			ret = new int[NR_OF_DIFFERENT_CODES];
			for (int i = 0; i < NR_OF_DIFFERENT_CODES; i++) {
				ret[i] = 0;
			}
		}

		public void increaseRC(int returnCode) {
			if (returnCode >= 0) {
				ret[returnCode]++;
			}
		}

		public int sumOfAllWarnings() {
			return ret[1] + ret[2] + ret[3] + ret[4];
		}

		public String toString() {
			String str = "";
			for (int i = 0; i < NR_OF_DIFFERENT_CODES; i++) {
				if (i == NR_OF_DIFFERENT_CODES - 1) {
					str += ret[i];
				} else {
					str += ret[i] + "\t";
				}
			}
			return str;
		}

		public String divideBy(int val) {
			String str = "";
			for (int i = 0; i < NR_OF_DIFFERENT_CODES; i++) {
				if (i == NR_OF_DIFFERENT_CODES - 1) {
					str += String.format(Locale.US, "%.3f", (double) ret[i]
							/ (double) val);
				} else {
					str += String.format(Locale.US, "%.3f", (double) ret[i]
							/ (double) val)
							+ "\t";
				}
			}
			return str;
		}
	}

}
