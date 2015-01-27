package de.unihannover.dcsec.eviltwinprevention;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Simulator {
	private final boolean DEBUG = Configuration.DEBUG;
	private ETPEngine etpEngine;
	private ArrayList<HashMap<String, String>> listOfData;

	private String id;

	private boolean isFirstStep;

	private boolean cancelSimulation = false;

	private Result result;

	private HashMap<String, HashMap<String, String>> confNL;

	public Simulator(HashMap<String, HashMap<String, String>> confNL) {
		this.result = new Result();
		this.isFirstStep = true;
		this.confNL = confNL;
		etpEngine = new ETPEngine(confNL);
	}

	public Result start() {
		Iterator<HashMap<String, String>> it = listOfData.iterator();

		while (it.hasNext() && !cancelSimulation) {
			processNextStep(it.next());
		}
		return result;
	}

	private void processNextStep(HashMap<String, String> hm) {
		if (isFirstStep) {
			isFirstStep = false;
			LogUtil.getInstance().setFirstTimestamp(
					Long.valueOf(hm.get("timestamp")));
		}

		// Get duration count for this step
		long duration = Long.parseLong(hm.get("duration"));
		result.setNewDuration(duration);

		// Ask ETPEngine
		int returncode = -1;
		returncode = etpEngine.handleNewConnection(id, hm);

		result.addConnectionCode(returncode);

		LogUtil.getInstance().logConnectionAttempt(hm.get("timestamp"),
				returncode);

		if (DEBUG) {
			Timestamp stamp = new Timestamp(Long.parseLong(hm.get("timestamp")));
			Date date = new Date(stamp.getTime());
			System.out.print("Detailed" + hm.get("timestamp") + ": ");
			LogUtil.getInstance().log(false,
					hm.get("timestamp") + "=" + date + ": ");
			LogUtil.getInstance()
					.log(ETPEngine.translateReturnCode(returncode));
			LogUtil.getInstance().log("========================");
		}

	}

	public void setData(ArrayList<HashMap<String, String>> dataForDevice) {
		listOfData = dataForDevice;
	}

	public void setID(String id) {
		this.id = id;
	}

}
