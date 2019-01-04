package faultfinder.faults;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class SignalConnectors {

	private static Map<Integer, Pair<Integer, Integer>> eMap = new HashMap<>();
	private static Map<Integer, Pair<Integer, Integer>> treeMap = new HashMap<>();
	private static Map<Integer, Pair<Integer, Integer>> threeMap = new HashMap<>();

	private static void setupEMap() {
		eMap.put(1, Pair.of(1, 3));
		eMap.put(2, Pair.of(1, 3));
		eMap.put(3, Pair.of(1, 2));
		eMap.put(4, Pair.of(1, 3));
		eMap.put(5, Pair.of(1, 2));
		eMap.put(6, Pair.of(1, 3));

	}

	private static void setupTreeMap() {
		treeMap.put(1, Pair.of(4, 5));
		treeMap.put(2, Pair.of(4, 6));
		treeMap.put(3, Pair.of(3, 5));
		treeMap.put(4, Pair.of(4, 6));
		treeMap.put(5, Pair.of(3, 5));
		treeMap.put(6, Pair.of(4, 5));
	}

	private static void setupThreeMap() {
		threeMap.put(1, Pair.of(6, 8));
		threeMap.put(2, Pair.of(7, 8));
		threeMap.put(3, Pair.of(6, 8));
		threeMap.put(4, Pair.of(7, 8));
		threeMap.put(5, Pair.of(6, 8));
		threeMap.put(6, Pair.of(6, 8));
	}

	public static Map<Integer, Pair<Integer, Integer>> getEMap() {
		return eMap;
	}

	public static Map<Integer, Pair<Integer, Integer>> getTreeMap() {
		return treeMap;
	}

	public static Map<Integer, Pair<Integer, Integer>> getThreeMap() {
		return threeMap;
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

		xPlace = (xBin - 1) / 8 * 8;
		iInc = xPlace / 8;
		placer = (xBin + iInc) % 9;
		switch (placer) {
		case 1:
		case 2:
			return 1;
		case 3:
			if (yBin == 3 || yBin == 5) {
				return 2;
			} else {
				return 1;
			}
		case 4:
		case 5:
			return 2;
		case 6:
			if (yBin == 2 || yBin == 4) {
				return 2;
			} else {
				return 3;
			}
		case 7:
		case 8:
			return 3;
		default:
			return -1000;
		}
	}

	public static Map<Integer, Pair<Integer, Integer>> findWireRange(int xBin, int yBin) {
		int placer;
		int xPlace;
		int iInc;

		xPlace = (xBin - 1) / 8 * 8;
		iInc = xPlace / 8;
		placer = (xBin + iInc) % 9;
		switch (placer) {
		case 1:
			return modifyMap(xPlace, eMap);
		case 2:
			return modifyMap(xPlace, eMap);
		case 3:
			if (yBin == 3 || yBin == 5) {
				return modifyMap(xPlace, treeMap);
			} else {
				return modifyMap(xPlace, eMap);
			}
		case 4:
			return modifyMap(xPlace, treeMap);
		case 5:
			return modifyMap(xPlace, treeMap);
		case 6:
			if (yBin == 2 || yBin == 4) {
				return modifyMap(xPlace, treeMap);
			} else {
				return modifyMap(xPlace, threeMap);
			}
		case 7:
			return modifyMap(xPlace, threeMap);
		case 8:
			return modifyMap(xPlace, threeMap);
		default:
			return null;
		}
	}

	public static void setupBundles() {
		setupEMap();
		setupTreeMap();
		setupThreeMap();

	}
}
