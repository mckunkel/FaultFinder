package faultfinder.faults;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class FuseBundles {

	private static Map<Integer, Pair<Integer, Integer>> bundleA = new HashMap<>();
	private static Map<Integer, Pair<Integer, Integer>> bundleB = new HashMap<>();
	private static Map<Integer, Pair<Integer, Integer>> bundleC = new HashMap<>();

	private static void setupBundleA() {
		Map<Integer, Pair<Integer, Integer>> eMap = SignalConnectors.getEMap();
		Map<Integer, Pair<Integer, Integer>> treeMap = SignalConnectors.getTreeMap();
		for (int i = 1; i <= 6; i++) {
			bundleA.put(i, Pair.of(eMap.get(i).getLeft(), treeMap.get(i).getRight()));
		}
	}

	private static void setupBundleB() {
		Map<Integer, Pair<Integer, Integer>> threeMap = SignalConnectors.getThreeMap();
		Map<Integer, Pair<Integer, Integer>> eMap = SignalConnectors.getEMap();
		for (int i = 1; i <= 6; i++) {
			bundleB.put(i, Pair.of(threeMap.get(i).getLeft(), 8 + eMap.get(i).getRight()));
		}
	}

	private static void setupBundleC() {
		Map<Integer, Pair<Integer, Integer>> treeMap = SignalConnectors.getTreeMap();
		Map<Integer, Pair<Integer, Integer>> threeMap = SignalConnectors.getThreeMap();
		for (int i = 1; i <= 6; i++) {
			bundleC.put(i, Pair.of(8 + treeMap.get(i).getLeft(), 8 + threeMap.get(i).getRight()));
		}
	}

	private static Map<Integer, Pair<Integer, Integer>> modifyMap(int xPlace,
			Map<Integer, Pair<Integer, Integer>> aMap) {

		Map<Integer, Pair<Integer, Integer>> aNewMap = new HashMap<>();

		for (Map.Entry<Integer, Pair<Integer, Integer>> entry : aMap.entrySet()) {
			Integer key = entry.getKey();
			Pair<Integer, Integer> value = entry.getValue();
			aNewMap.put(key, Pair.of(value.getLeft() + xPlace, value.getRight() + xPlace));
		}
		return aNewMap;
	}

	public static int getBundle(int xBin, int yBin) {
		int placer;
		int xPlace;
		int iInc;

		xPlace = (xBin - 1) / 16 * 16;
		iInc = xPlace / 8;
		placer = (xBin + iInc) % 18;
		switch (placer) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return 1;
		case 6:
			if (yBin == 2 || yBin == 4) {
				return 1;
			} else {
				return 2;
			}
		case 7:
		case 8:
		case 9:
		case 10:
			return 2;
		case 11:
			if (yBin == 3 || yBin == 5) {
				return 3;
			} else {
				return 2;
			}
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
			return 3;
		default:
			return -1000;
		}
	}

	public static Map<Integer, Pair<Integer, Integer>> findWireRange(int xBin, int yBin) {
		int placer;
		int xPlace;
		int iInc;

		xPlace = (xBin - 1) / 16 * 16;
		iInc = xPlace / 8;
		placer = (xBin + iInc) % 18;
		switch (placer) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return modifyMap(xPlace, bundleA);
		case 6:
			if (yBin == 2 || yBin == 4) {
				return modifyMap(xPlace, bundleA);
			} else {
				return modifyMap(xPlace, bundleB);
			}
		case 7:
		case 8:
		case 9:
		case 10:
			return modifyMap(xPlace, bundleB);
		case 11:
			if (yBin == 3 || yBin == 5) {
				return modifyMap(xPlace, bundleC);
			} else {
				return modifyMap(xPlace, bundleB);
			}
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
			return modifyMap(xPlace, bundleC);
		default:
			return null;
		}
	}

	public static void setupBundles() {
		setupBundleA();
		setupBundleB();
		setupBundleC();

	}
}
