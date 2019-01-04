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
package faultfinder.utils;

public class StringConstants {

	private StringConstants() {

	}

	// things for spark
	public static final String APP_NAME = "DC Database Management Application";
	public static final String TEMP_DIR = "spark-warehouse";
	public static final String SPARK_MASTER = "local[*]";// "spark://134.94.180.35:7077";//
	// "local[4]";//
	// "spark://Mike-Kunkels-MacBook.local:7077";//
	// "local[2]";

	// things for menu
	public static final String MAIN_MENU_FILE = "File";
	public static final String MAIN_MENU_OPEN = "Open";
	public static final String MAIN_MENU_EXIT = "Exit";
	public static final String OPTIONS_FORM_TITLE = "Options";

	public static final String MAIN_MENU_SORT = "Sort";
	public static final String MAIN_MENU_COMPARE = "Compare";

	public static final String MAIN_MENU_EXIT_TEXT = "Do you really want to exit?";
	public static final String MAIN_MENU_EXIT_TITLE = "Confirm Exit";
	public static final String STATUS_PANEL_TEXT = "mkunkel@jlab.org";

	public static final String FORM_CANCEL = "Cancel";
	public static final String FORM_SAVE = "Save";

	// For sort query
	public static final String SORT_FORM_TITLE = "Sort By:";
	public static final String SORT_FORM_CANCEL = "Cancel";
	public static final String SORT_FORM_SAVE = "Sort";
	public static final String SORT_FORM_SECTOR = "Sector";
	public static final String SORT_FORM_SUPERLAYER = "Superlayer";
	public static final String SORT_FORM_SUBTITLE = "Sort By:";

	public static final String CHOOSEFILE_FORM_TITLE = "PLEASE CHOOSE A FILE";
	public static final String FORM_OK = "OK";
	public static final String COMPARE_FORM_TITLE = "Compare";
	public static final String COMPARE_FORM_COMPARE = "Compare Run";
	public static final String COMPARE_FORM_RUN = "Run";
	public static final String COMPARE_FORM_SUBTITLE = "Compare with Database";
	public static final String RUN_FORM_TITLE = "Run";
	public static final String FORM_RUN = "Run";
	public static final String FILE_FORM_SELECT = "Select File to Analyze";
	public static final String FILE_FORM = "Hipo File:";
	public static final String MAIN_FORM_DATA = "Data Run:";
	public static final String MAIN_FORM_SQL = "SQL Query Run:";
	public static final String MAIN_FORM_HIST = "Plot For Run:";

	public static final String[] PROBLEM_TYPES = { "", "hvchannel", "hvpin", "lvfuse", "signalconnector",
			"ind.wire_dead", "ind.wire_hot" };

	public static final String FAULT_FORM_LABEL = "Type of Fault:";
	public static final String FAULT_FORM_SET = "Set Fault";
	public static final String FAULT_FORM_APPLY = "Apply";
	public static final String FAULT_FORM_SEND = "Send to List";

	public static final String OTHER_FORM_TITLE = "Other";
	public static final String OTHER_FORM_INPUT = "Specify other:";
	public static final String DATA_FORM_LABEL = "From Data:";
	public static final String HISTOGRAM_FORM_LABEL = "Histogram For Data:";
	public static final String SQL_FORM_LABEL = "SQL Query";
	public static final String DBSEND_FORM_REMOVE = "Remove Selected";
	public static final String DBSEND_FORM_SEND = "Send to MySql";
	public static final String DBSEND_FORM_LABEL = "Send to Database";
	public static final String CCDBSEND_FORM_LABEL = "CCDB Entry";
	public static final String CCDBSEND_FORM_REMOVE = "Remove from Local List";
	public static final String CCDBSEND_FORM_SEND = "Send to CCDB";
	public static final String CCDBSEND_FORM_LIST = "Local List";
	public static final String FORM_NAME = "UserName";
	public static final String SORTCOMPARE_FORM_SUBTITLE = "Sort/Compare";
	public static final String RUNPERCENT = "Percent Inefficiency";
	public static final String COMPARE_FORM_LOADRUN = "Load";
	public static final String SEND_SCRIPT = "Make Script";
	public static final String EXECUTE_SCRIPT = "Send to CCDB";

	public static final String RUNS_SELECTED = "Select Run(s)";
	public static final String APP_NAME4DISPLAY = "DC CCDB FAULTS";

}
