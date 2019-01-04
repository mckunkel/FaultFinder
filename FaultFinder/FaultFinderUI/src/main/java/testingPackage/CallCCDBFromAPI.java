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
package testingPackage;

import org.jlab.detector.calib.utils.DatabaseConstantProvider;

public class CallCCDBFromAPI {

	public static void main(String[] args) {
		// initialize database connection provider for RUN #=10 and variation =
		// default
		DatabaseConstantProvider dbprovider = new DatabaseConstantProvider(800, "default");

		// load table reads entire table and makes an array of variables for
		// each column in the table.
		// dbprovider.loadTable("/calibration/dc/tracking/wire_status");
		dbprovider.loadTable("/calibration/dc/time_corrections/T0Corrections");

		// disconnect from database. Important to do this after loading tables.

		dbprovider.disconnect();

		// printout names of columns and lengths for each variable
		dbprovider.show();

		for (int i = 0; i < dbprovider.length("/calibration/dc/time_corrections/T0Corrections/Sector"); i++) {
			int iSec = dbprovider.getInteger("/calibration/dc/time_corrections/T0Corrections/Sector", i);
			int iSly = dbprovider.getInteger("/calibration/dc/time_corrections/T0Corrections/Superlayer", i);
			int iSlot = dbprovider.getInteger("/calibration/dc/time_corrections/T0Corrections/Slot", i);
			int iCab = dbprovider.getInteger("/calibration/dc/time_corrections/T0Corrections/Cable", i);
			double t0 = dbprovider.getDouble("/calibration/dc/time_corrections/T0Corrections/T0Correction", i);
			double t0Error = dbprovider.getDouble("/calibration/dc/time_corrections/T0Corrections/T0Error", i);

			System.out.println(iSec + "  " + iSly + "  " + iSlot + "  " + iCab + "  " + t0 + "  " + t0Error);
		}

	}

}
