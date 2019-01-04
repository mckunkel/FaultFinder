/**
 * 
 */
package faultfinder.objects;

import java.util.Comparator;

/**
 * @author m.c.kunkel
 *
 */
public class StatusChangeDBComp implements Comparator<StatusChangeDB> {

	@Override
	public int compare(StatusChangeDB e1, StatusChangeDB e2) {
		if (Integer.parseInt(e1.getSuperlayer()) == Integer.parseInt(e2.getSuperlayer())
				&& Integer.parseInt(e1.getLoclayer()) == Integer.parseInt(e2.getLoclayer())
				&& Integer.parseInt(e1.getSector()) == Integer.parseInt(e2.getSector())
				&& Integer.parseInt(e1.getLocwire()) == Integer.parseInt(e2.getLocwire())) {
			return 1;
		} else if (Integer.parseInt(e1.getSuperlayer()) > Integer.parseInt(e2.getSuperlayer())) {
			return 1;
		} else if (Integer.parseInt(e1.getLoclayer()) > Integer.parseInt(e2.getLoclayer())) {
			return 1;
		} else if (Integer.parseInt(e1.getSector()) > Integer.parseInt(e2.getSector())) {
			return 1;
		} else if (Integer.parseInt(e1.getLocwire()) > Integer.parseInt(e2.getLocwire())) {
			return 1;
		} else {
			return -1;
		}
	}
}
