package de.unihannover.dcsec.eviltwinprevention.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MySQLAccessDAO implements iDAO {
	private Connection con = null;
	private PreparedStatement prepStatementInfos = null;
	private PreparedStatement prepStatementCount = null;

	public static final ArrayList<String> DB_FIELDS = new ArrayList<String>(
			Arrays.asList("id", "timestamp", "duration", "connected.bssid",
					"connected.ssid", "seenNets", "locations.ts",
					"locations.lat", "locations.longi", "locations.acc",
					"cellID", "lac"));

	private final String SQL_GET_INFOS_FOR_DEVICE = "SELECT * FROM detailedLogentries JOIN connected JOIN locations "
			+ "WHERE connectedNet = connected.id AND location = locations.id AND device = ? ORDER BY timestamp";

	private final String SQL_GET_STEP_COUNT_FOR_DEVICE = "SELECT COUNT(*) FROM detailedLogentries "
			+ "WHERE device = ?";

	public MySQLAccessDAO() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void open() {
		try {
			String host = "jdbc:mysql://<INSERT HOST HERE>/";
			String dbname = "<INSERT DB NAME HERE>";
			String user = "<INSERT USERNAME HERE>";
			String password = "<INSERT PASSWORD HERE>";
			con = DriverManager
					.getConnection(host + dbname + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HashMap<String, String>> getDataForDevice(String device) {
		open();
		ResultSet resultSet = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		if (con != null) {
			try {
				prepStatementInfos = con
						.prepareStatement(SQL_GET_INFOS_FOR_DEVICE);
				prepStatementInfos.setString(1, device);
				resultSet = prepStatementInfos.executeQuery();

				if (resultSet != null) {
					while (resultSet.next()) {
						list.add(produceMap(resultSet));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public int getStepCountForDevice(String device) {
		open();
		ResultSet rs = null;
		if (con != null) {
			try {
				prepStatementCount = con
						.prepareStatement(SQL_GET_STEP_COUNT_FOR_DEVICE);
				prepStatementCount.setString(1, device);
				rs = prepStatementCount.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		int count = -1;
		try {
			if (rs != null) {
				rs.next();
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return count;
	}

	private HashMap<String, String> produceMap(ResultSet rs) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (rs != null) {
			try {
				for (String field : MySQLAccessDAO.DB_FIELDS) {
					map.put(field, rs.getString(field));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

}
