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

public class ChannelBundles {
	private static List<Pair<Integer, Integer>> hvChannelSegmentation = new ArrayList<Pair<Integer, Integer>>();;

	private static void setuphvChannelSegmentation() {
		hvChannelSegmentation.add(Pair.of(1, 8));
		hvChannelSegmentation.add(Pair.of(9, 16));
		hvChannelSegmentation.add(Pair.of(17, 24));
		hvChannelSegmentation.add(Pair.of(25, 32));
		hvChannelSegmentation.add(Pair.of(33, 48));
		hvChannelSegmentation.add(Pair.of(49, 64));
		hvChannelSegmentation.add(Pair.of(65, 80));
		hvChannelSegmentation.add(Pair.of(81, 112));
	}

	private static int findPlacer(Pair<Integer, Integer> pair) {

		int placer = 0;
		for (Pair<Integer, Integer> temp : hvChannelSegmentation) {
			placer++;
			if (temp.equals(pair)) {
				return placer;
			}
		}
		return placer;

	}

	public static Pair<Integer, Integer> findWireRange(int wire) {
		Pair<Integer, Integer> retValuePair = Pair.of(0, 0);
		for (Pair<Integer, Integer> pair : hvChannelSegmentation) {
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
		setuphvChannelSegmentation();

	}
}
