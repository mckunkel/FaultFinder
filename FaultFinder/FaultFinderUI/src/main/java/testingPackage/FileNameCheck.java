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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileNameCheck {

	public static void main(String[] args) {
		String s1 = "out_clas_003923.evio.80.hipo";
		String s2 = "out_clas_003923.evio.81.hipo";
		String s3 = "out_clas_003922.evio.81.hipo";

		List<String> fileList = new ArrayList();
		Map<Integer, List<String>> aMap = new HashMap<>();

		fileList.add(s1);
		fileList.add(s2);
		fileList.add(s3);

		for (String s : fileList) {

			String newS = s.substring(s.indexOf("clas_") + 5, s.indexOf(".evio"));
			int aplacer = Integer.parseInt(newS);
			System.out.println(newS + " in runform " + Integer.parseInt(newS));
			if (aMap.containsKey(aplacer)) {
				aMap.get(aplacer).add(s);
			} else {
				aMap.put(aplacer, new ArrayList<String>());
				aMap.get(aplacer).add(s);
			}
		}

		for (Map.Entry<Integer, List<String>> entry : aMap.entrySet()) {
			Integer key = entry.getKey();
			List<String> value = entry.getValue();
			System.out.println(key + "  " + value.size());
		}

	}
}
