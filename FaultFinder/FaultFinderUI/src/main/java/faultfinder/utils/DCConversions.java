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

public class DCConversions {

	public enum status_change_type {
		BROKEN, FIXED
	}

	public static String getRegion(String superLayer) {

		switch (superLayer) {
		case "1":
			return "1";
		case "2":
			return "1";
		case "3":
			return "2";
		case "4":
			return "2";
		case "5":
			return "3";
		case "6":
			return "3";
		default:
			return null;
		}

	}

}
