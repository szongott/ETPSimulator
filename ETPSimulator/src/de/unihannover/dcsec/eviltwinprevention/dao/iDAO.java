package de.unihannover.dcsec.eviltwinprevention.dao;

import java.util.ArrayList;
import java.util.HashMap;

public interface iDAO {

	public ArrayList<HashMap<String, String>> getDataForDevice(String device);

}
