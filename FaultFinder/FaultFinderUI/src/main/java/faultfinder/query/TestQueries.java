package faultfinder.query;

import static org.apache.spark.sql.functions.col;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import spark.utils.SparkManager;

public class TestQueries {

	private TestQueries() {

	}

	public static Dataset<Row> compareRun(String str) {
		return SparkManager.mySqlDataset().select("loclayer", "superLayer", "sector", "locwire", "status_change_type")
				.filter(col("runno").equalTo(str));
	}

	public static void main(String[] args) {

		Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.ERROR);
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
		//
		// Dataset<Row> df = compareRun("762");
		// df.createOrReplaceTempView("TestView");
		// df.show(20, false);
		// Dataset<Row> sqlDF = SparkManager.getSession().sql("SELECT * FROM
		// TestView.INFORMATION_SCHEMA.COLUMNS");
		// sqlDF.show();

		SparkSession spSession = SparkManager.getSession();

		spSession.sql("set spark.sql.caseSensitive=false");

		// Dataset<Row> demoDf =
		// spSession.read().format("jdbc").options(jdbcOptions()).option("inferSchema",
		// true)
		// .option("header", true).option("comment", "#").load();
		String[] included_tables = { "stbmap", "wiretopin", "layer", "wiretolocwire", "hvcrateslot", "hvsubslotchansf",
				"hvsubslotchang", "senselayerrange", "hvpintolayer", "hvdbtohvtb" };
		String url = "jdbc:mysql://localhost:3306/dc_chan_status?jdbcCompliantTruncation=false";

		String driver = "com.mysql.jdbc.Driver";
		Properties props = new Properties();
		props.setProperty("user", "root");
		props.setProperty("password", "");

		List<Dataset<Row>> aDatasets = new ArrayList<>();
		// for (String string : included_tables) {
		// aDatasets.add(spSession.read().jdbc(url, string, props));
		// Dataset<Row> df = spSession.read().jdbc(url, string, props);
		// df.createOrReplaceTempView(string);
		// .read().jdbc(url, string, props))
		// .option("inferSchema", true).option("header",
		// true).option("comment", "#").load();
		// }
		
		System.out.println("Opening");

		Dataset<Row> df = spSession.read().jdbc(url, "stbmap", props);
		System.out.println("Opening");

		df.show();
		
//		df.createOrReplaceTempView("stbmapII");
//
//		Dataset<Row> sqlDF = spSession.sql("SELECT * FROM stbmapII");
//		sqlDF.show();

	}

}
