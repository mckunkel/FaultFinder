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
package faultfinder.faults;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class PinBundles {
	private static List<Pair<Integer, Integer>> hvPinSegmentation = new ArrayList<Pair<Integer, Integer>>();;

	private static void setuphvPinSegmentation() {
		hvPinSegmentation.add(Pair.of(1, 8));
		hvPinSegmentation.add(Pair.of(9, 16));
		hvPinSegmentation.add(Pair.of(17, 24));
		hvPinSegmentation.add(Pair.of(25, 32));
		hvPinSegmentation.add(Pair.of(33, 40));
		hvPinSegmentation.add(Pair.of(41, 48));
		hvPinSegmentation.add(Pair.of(49, 56));
		hvPinSegmentation.add(Pair.of(57, 64));
		hvPinSegmentation.add(Pair.of(65, 72));
		hvPinSegmentation.add(Pair.of(73, 80));
		hvPinSegmentation.add(Pair.of(81, 96));
		hvPinSegmentation.add(Pair.of(97, 112));
	}

	private static int findPlacer(Pair<Integer, Integer> pair) {

		int placer = 0;
		for (Pair<Integer, Integer> temp : hvPinSegmentation) {
			placer++;
			if (temp.equals(pair)) {
				return placer;
			}
		}
		return placer;

	}

	public static Pair<Integer, Integer> findWireRange(int wire) {
		Pair<Integer, Integer> retValuePair = Pair.of(0, 0);
		for (Pair<Integer, Integer> pair : hvPinSegmentation) {
			// placer++;
			if ((wire + 1) <= pair.getRight() && (wire + 1) >= pair.getLeft()) {
				retValuePair = pair;
			}
		}
		return retValuePair;
	}

	public static int getBundle(Pair<Integer, Integer> aPair) {
		return findPlacer(aPair);
	}

	public static void setupBundles() {
		setuphvPinSegmentation();

	}
}
