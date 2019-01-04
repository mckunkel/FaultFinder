/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package faultfinder.service;

import static org.apache.spark.sql.functions.col;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import faultfinder.objects.StatusChangeDB;
import faultfinder.query.DBQuery;
import faultfinder.utils.DCConversions;
import faultfinder.utils.MainFrameServiceManager;
import spark.utils.SparkManager;

public class InsertMYSqlQueryImpl implements InsertMYSqlQuery {
	private MainFrameService mainFrameService = null;
	private DBQuery dbQuery = null;

	public InsertMYSqlQueryImpl() {
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.dbQuery = new DBQuery();

	}

	@Override
	public void prepareMYSQLQuery() {
		// well we need to "trick" the system. The Spark encoder does not handle
		// the enum properly. So we
		// will copy a generic StatusChangeDB from the MYSQL database with the
		// appropriate call for Status_change_type

		List<StatusChangeDB> aList = new ArrayList<>();
		Dataset<StatusChangeDB> dbQuery = this.dbQuery.compareRunII("0");

		Timestamp timestamp = java.sql.Timestamp.from(java.time.Instant.now());
		for (StatusChangeDB statusChangeDB : this.mainFrameService.getCompleteSQLList()) {
			StatusChangeDB aChangeDBTest = null;
			if (statusChangeDB.getStatus_change_type().equals("broke")) {
				aChangeDBTest = dbQuery.filter(col("status_change_type").equalTo("broke")).first();
			} else if (statusChangeDB.getStatus_change_type().equals("fixed")) {
				aChangeDBTest = dbQuery.filter(col("status_change_type").equalTo("fixed")).first();
			} else {
				System.err.println(
						"There is no default for this status_change_type of " + statusChangeDB.getStatus_change_type());
			}
			aChangeDBTest.setStatchangeid(0);
			// StatusChangeDB aChangeDBTest = new StatusChangeDB();
			aChangeDBTest.setDateofentry(timestamp);
			aChangeDBTest.setRegion(DCConversions.getRegion(statusChangeDB.getSuperlayer()));

			aChangeDBTest.setProblem_type(statusChangeDB.getProblem_type());
			aChangeDBTest.setRunno(statusChangeDB.getRunno());
			aChangeDBTest.setSector(statusChangeDB.getSector());
			aChangeDBTest.setSuperlayer(statusChangeDB.getSuperlayer());
			aChangeDBTest.setLoclayer(statusChangeDB.getLoclayer());
			aChangeDBTest.setLocwire(statusChangeDB.getLocwire());
			// aChangeDBTest.setStatus_change_type(statusChangeDB.getStatus_change_type());
			aList.add(aChangeDBTest);

		}

		// aList.addAll(this.mainFrameService.getCompleteSQLList());
		Dataset<Row> changeDF = SparkManager.getSession().createDataset(aList, SparkManager.statusChangeDBEncoder())
				.toDF();
		insertMYSQLQuery(changeDF);
	}

	private void insertMYSQLQuery(Dataset<Row> changeDF) {
		try {
			changeDF.write().mode(SaveMode.Append).jdbc(SparkManager.jdbcAppendOptions(), "status_change",
					new java.util.Properties());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
