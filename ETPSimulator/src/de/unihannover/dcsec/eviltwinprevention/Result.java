package de.unihannover.dcsec.eviltwinprevention;

import java.util.Date;
import java.util.HashMap;

public class Result {

	private HashMap<Integer, Integer> stats;
	private long duration;

	public Result() {
		stats = new HashMap<Integer, Integer>();
		duration = 0l;
	}

	public void addConnectionCode(int code) {
		if (stats.containsKey(code)) {
			int old = stats.get(code);
			stats.put(code, old + 1);
		} else {
			stats.put(code, 1);
		}
	}

	public void setNewDuration(long duration) {
		this.duration = duration;
	}

	public void addResult(Result result) {
		for (int code : result.getStats().keySet()) {
			if (stats.containsKey(code)) {
				int old = stats.get(code);
				stats.put(code, old + result.getStats().get(code));
			} else {
				stats.put(code, 1);
			}
		}

		duration += result.getDuration();
	}

	public HashMap<Integer, Integer> getStats() {
		return stats;
	}

	public long getDuration() {
		return duration;
	}

	public static String diffToWords(long difference) {
		Date start = new Date(0L);
		Date end = new Date(difference);

		long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

		long diff[] = new long[] { 0, 0, 0, 0 };
		/* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60
				: diffInSeconds);
		/* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60
				: diffInSeconds;
		/* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24
				: diffInSeconds;
		/* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));

		return String.format(
				"%d day%s, %d hour%s, %d minute%s, %d second%s ago", diff[0],
				diff[0] > 1 ? "s" : "", diff[1], diff[1] > 1 ? "s" : "",
				diff[2], diff[2] > 1 ? "s" : "", diff[3], diff[3] > 1 ? "s"
						: "");
	}

	public void printStats() {
		LogUtil.getInstance().log("========= STATISTICS ==========");
		LogUtil.getInstance().log(false,
				"Considered duration: " + duration + " = ");
		LogUtil.getInstance().log(Result.diffToWords(duration));
		LogUtil.getInstance().log("code\tcount\tavgTimeDiff\t| description");
		LogUtil.getInstance().log("-------------------------------");
		for (int key : stats.keySet()) {
			LogUtil.getInstance().log(false, key + "\t");
			LogUtil.getInstance().log(false, stats.get(key) + "\t");
			LogUtil.getInstance().log(false,
					Result.diffToWords(duration / stats.get(key)) + "\t");
			LogUtil.getInstance()
					.log("| " + ETPEngine.translateReturnCode(key));

		}
		LogUtil.getInstance().log("===============================");
	}

}
