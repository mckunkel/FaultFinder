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

public enum InsertMYSqlServiceManager {
	INSTANCE;
	private static InsertMYSqlQuery insertMYSqlQuery = null;

	private static void getService() {
		insertMYSqlQuery = new InsertMYSqlQueryImpl();
	}

	public static InsertMYSqlQuery getSession() {
		if (insertMYSqlQuery == null) {
			getService();
		}
		return insertMYSqlQuery;
	}

}
