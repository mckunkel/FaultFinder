package faultfinder.query;

import static org.apache.spark.sql.functions.asc;
import static org.apache.spark.sql.functions.col;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

import faultfinder.objects.CCDBWireStatusObject;
import faultfinder.objects.StatusChangeDB;
import spark.utils.SparkManager;

public class DBQuery {

	public DBQuery() {
	}

	public List<String> getAllRuns() {
		Dataset<Row> dataDF = SparkManager.mySqlDataset().select("runno").filter("runno!=0").sort(asc("runno"))
				.distinct();
		// Dataset<Row> dataDF =
		// SparkManager.mySqlDataset().select("runno").sort(asc("runno")).distinct();
		return dataDF.map(row -> row.mkString(), Encoders.STRING()).collectAsList();
	}

	public Dataset<Row> getAllRunsDataset() {
		return SparkManager.mySqlDataset().select("runno").sort(asc("runno")).distinct();
	}

	public Dataset<Row> compareRun(String str) {
		return SparkManager.mySqlDataset().select("loclayer", "superLayer", "sector", "locwire", "status_change_type")
				.filter(col("runno").equalTo(str));
	}

	public Dataset<StatusChangeDB> compareRunII(String str) {
		Dataset<Row> tempDF = SparkManager.mySqlDataset().filter(col("runno").equalTo(str));
		return tempDF.as(SparkManager.statusChangeDBEncoder());

	}

	public List<String> getAllProblems() {
		Dataset<Row> dataDF = SparkManager.mySqlDataset().select("problem_type").sort(asc("problem_type")).distinct();
		return dataDF.map(row -> row.mkString(), Encoders.STRING()).collectAsList();
	}

	public Dataset<Row> getAllProblemsDataset() {
		return SparkManager.mySqlDataset().select("problem_type").sort(asc("problem_type")).distinct();

	}

	private Dataset<StatusChangeDB> getBadWires(String str) {
		Dataset<Row> tempDF = SparkManager.mySqlDataset();
		Dataset<StatusChangeDB> typedDataset = tempDF.as(SparkManager.statusChangeDBEncoder())
				.filter((FilterFunction<StatusChangeDB>) response -> response.getRunno() == Integer.parseInt(str))
				.filter((FilterFunction<StatusChangeDB>) response -> response.getStatus_change_type().equals("broke"));
		return typedDataset;

	}

	public List<StatusChangeDB> getBadWireList(String str) {
		List<StatusChangeDB> alist = getBadWires(str).collectAsList();
		return alist;
	}

	public List<StatusChangeDB> getBadWireList(int runno) {
		List<StatusChangeDB> alist = getBadWires(runno).collectAsList();
		return alist;
	}

	public List<CCDBWireStatusObject> getBadComponentList(int runno) {
		List<StatusChangeDB> alist = getBadWires(runno).collectAsList();
		List<CCDBWireStatusObject> retList = new ArrayList<>();
		for (StatusChangeDB statusChangeDB : alist) {
			int superLayer = Integer.parseInt(statusChangeDB.getSuperlayer());
			int locLayer = Integer.parseInt(statusChangeDB.getLoclayer());
			int layer = (superLayer - 1) * 6 + locLayer;
			int component = Integer.parseInt(statusChangeDB.getLocwire());
			int sector = Integer.parseInt(statusChangeDB.getSector());

			int status = problemType(statusChangeDB.getProblem_type());
			retList.add(new CCDBWireStatusObject(sector, layer, component, status));
		}

		return retList;
	}

	private int problemType(String problem_type) {
		// 1 - HV channel
		// 2 - HV pin
		// 3 - HV (other)
		// 4 - lv fuse
		// 5 - signal connector
		// 6 - individual hot wire
		// 7 - individual dead wire
		// 8 - unknown
		int retValue = 7;
		if (problem_type.equals("hvchannel")) {
			retValue = 1;
		} else if (problem_type.equals("hvpin")) {
			retValue = 2;
		} else if (problem_type.equals("lvfuse")) {
			retValue = 3;
		} else if (problem_type.equals("signalconnector")) {
			retValue = 4;
		} else if (problem_type.equals("ind.wire_hot")) {
			retValue = 5;
		} else if (problem_type.equals("ind.wire_dead")) {
			retValue = 6;
		} else {
			retValue = 7;
		}
		return retValue;
	}

	private Dataset<StatusChangeDB> getBadWires(int runnoNumber) {
		Dataset<Row> tempDF = SparkManager.mySqlDataset();
		Dataset<StatusChangeDB> typedDataset = tempDF.as(SparkManager.statusChangeDBEncoder())
				.filter((FilterFunction<StatusChangeDB>) response -> response.getRunno() == runnoNumber)
				.filter((FilterFunction<StatusChangeDB>) response -> response.getStatus_change_type().equals("broke"));
		return typedDataset;

	}

	public static void main(String[] args) {
		Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN);
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
		// List<String> test = DBQuery.getAllRuns();
		// System.out.println("######################");
		// System.out.println(test.size());
		// for (String str : test) {
		// System.out.println(str + " is the run number");
		// }
		// System.out.println("######################");
		// Dataset<Row> dataDF = DBQuery.getAllRunsDataset();
		// dataDF.foreach((ForeachFunction<Row>) row -> System.out.println("Run
		// from Query " + row.get(0) + ""));
		DBQuery dbQuery = new DBQuery();
		Dataset<Row> dataDF = dbQuery.compareRun("762");
		dataDF.show();

		Dataset<StatusChangeDB> dataset = dbQuery.compareRunII("762");
		dataset.show();
		// dataDF.foreach((ForeachFunction<Row>) row -> System.out
		// .println("Run from Query " + row.get(0) + " " + row.get(1) + " " +
		// row.get(2) + " "));

	}
}
