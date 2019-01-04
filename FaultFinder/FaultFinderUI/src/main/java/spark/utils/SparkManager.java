package spark.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import faultfinder.objects.CCDBWireStatusObject;
import faultfinder.objects.StatusChangeDB;
import faultfinder.utils.StringConstants;

public enum SparkManager {
	INSTANCE;

	// A name for the spark instance. Can be any string
	private static String appName = StringConstants.APP_NAME;
	// Pointer / URL to the Spark instance - embedded
	private static String sparkMaster = StringConstants.SPARK_MASTER;
	private static String tempDir = StringConstants.TEMP_DIR;

	private static SparkSession sparkSession = null;

	private static Connection mySqlConnection = null;

	// String url = "jdbc:mysql://localhost:3306?jdbcCompliantTruncation=false";
	// String user = "root";
	private static String url = getUrl();
	private static String user = getUser();

	private static void getConnection() {

		if (sparkSession == null) {

			sparkSession = SparkSession.builder().appName(appName).master(sparkMaster)
					.config("spark.sql.warehouse.dir", tempDir).getOrCreate();

			// System.setProperty("hadoop.home.dir", ".");

		}

	}

	public static SparkSession getSession() {
		if (sparkSession == null) {
			getConnection();
		}
		return sparkSession;
	}

	private static Map<String, String> jdbcOptions() {
		return findDomain(getHostName());
	}

	public static String jdbcAppendOptions() {

		return jdbcOptions().get("url") + "&user=" + jdbcOptions().get("user") + "&password="
				+ jdbcOptions().get("password");

	}

	public static boolean isMySQLOpen() {

		try {
			// DriverManager.getConnection(jdbcAppendOptions());
			DriverManager.getConnection(url, user, "");
			setSQLConnection(DriverManager.getConnection(url, user, ""));
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	private static void setSQLConnection(Connection connection) {
		mySqlConnection = connection;
	}

	public static boolean hasDataBase() {
		if (isMySQLOpen()) {
			ResultSet resultSet;
			try {
				resultSet = mySqlConnection.getMetaData().getCatalogs();
				// iterate each catalog in the ResultSet
				while (resultSet.next()) {
					// Get the database name, which is at position 1
					String databaseName = resultSet.getString(1);
					if (databaseName.equals("dc_chan_status")) {
						return true;
					}
				}
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;

	}

	public static boolean hasTable() {
		try {
			DatabaseMetaData md = DriverManager.getConnection(jdbcAppendOptions()).getMetaData();
			ResultSet rs = md.getTables(null, null, jdbcOptions().get("dbtable"), null);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			return false;
		}
	}

	public static Dataset<Row> mySqlDataset() {
		SparkSession spSession = getSession();
		spSession.sql("set spark.sql.caseSensitive=false");
		// Dataset<Row> demoDf =
		// spSession.read().format("jdbc").options(jdbcOptions()).load();

		Dataset<Row> demoDf = spSession.read().format("jdbc").options(jdbcOptions()).option("inferSchema", true)
				.option("header", true).option("comment", "#").load();

		return demoDf;
	}

	public static void hold() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Encoder<StatusChangeDB> statusChangeDBEncoder() {
		return Encoders.bean(StatusChangeDB.class);
	}

	public static Encoder<CCDBWireStatusObject> CCDBWireStatusEncoder() {
		return Encoders.bean(CCDBWireStatusObject.class);
	}

	private static String getHostName() {
		String retString = null;
		try {

			// run the Unix "hostname" command
			// using the Runtime exec method:
			Process p = Runtime.getRuntime().exec("hostname");

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			retString = stdInput.readLine();

		} catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
		return retString;
	}

	public static boolean onJlab() {
		return getHostName().contains("jlab.org") ? true : false;
	}

	private static Map<String, String> findDomain(String str) {
		Map<String, String> jdbcOptions = new HashMap<String, String>();

		if (str.contains("jlab.org")) {
			jdbcOptions.put("url", "jdbc:mysql://clasdb:3306/dc_chan_status?jdbcCompliantTruncation=false");
			jdbcOptions.put("driver", "com.mysql.jdbc.Driver");
			jdbcOptions.put("dbtable", "status_change");
			jdbcOptions.put("user", "clasuser");
			jdbcOptions.put("password", "");
		}
		// else if (str.contains("Mike-Kunkels") || str.contains("Mike")) {
		// jdbcOptions.put("url",
		// "jdbc:mysql://localhost:3306/dc_chan_status?jdbcCompliantTruncation=false");
		// jdbcOptions.put("driver", "com.mysql.jdbc.Driver");
		// jdbcOptions.put("dbtable", "status_change");
		// jdbcOptions.put("user", "root");
		// jdbcOptions.put("password", "");
		// }
		else if (str.contains("ikp")) {
			jdbcOptions.put("url", "jdbc:mysql://localhost:3306/dc_chan_status?jdbcCompliantTruncation=false");
			jdbcOptions.put("driver", "com.mysql.jdbc.Driver");
			jdbcOptions.put("dbtable", "status_change");
			jdbcOptions.put("user", "root");
			jdbcOptions.put("password", "");
		} else {
			jdbcOptions.put("url", "jdbc:mysql://localhost:3306/dc_chan_status?jdbcCompliantTruncation=false");
			jdbcOptions.put("driver", "com.mysql.jdbc.Driver");
			jdbcOptions.put("dbtable", "status_change");
			jdbcOptions.put("user", "root");
			jdbcOptions.put("password", "");
			// if (!isMySQLOpen()) {
			//
			// JOptionPane.showMessageDialog(null, "computer", "Eggs are not
			// supposed to be green.",
			// JOptionPane.ERROR_MESSAGE);
			// // System.exit(-1);
			// }
		}
		return jdbcOptions;
	}

	private static String getUrl() {
		if (getHostName().contains("jlab.org")) {
			return "jdbc:mysql://clasdb:3306?jdbcCompliantTruncation=false";
		} else {
			return "jdbc:mysql://localhost:3306?jdbcCompliantTruncation=false";
		}
	}

	private static String getUser() {
		if (getHostName().contains("jlab.org")) {
			return "clasuser";
		} else {
			return "root";
		}
	}

	public static int sqlCorrectConfig() {

		if (!isMySQLOpen()) { // Mysql not open
			return 0;
		} else { // Mysql open
			if (!hasDataBase()) { // database not defined
				return 1;
			} else { // database defined
				if (!hasTable()) { // table not defined
					return 2;
				} else { // table defined
					return 3;
				}
			}
		}
	}

	public void shutdown() {
		SparkManager.sparkSession.stop();
	}

	public static void restart() {
		SparkSession.clearActiveSession();
	}
}
