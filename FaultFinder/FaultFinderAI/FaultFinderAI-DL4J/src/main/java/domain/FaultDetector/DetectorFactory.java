/**
 * 
 */
package domain.FaultDetector;

import clasDC.faults.FaultNames;

/**
 * @author m.c.kunkel
 *
 */
public class DetectorFactory {

	public DetectorFactory() {
	}

	public static FaultDetector getDetector(FaultNames desiredFault) {
		return getDetector(desiredFault, 0);
	}

	public static FaultDetector getDetector(FaultNames desiredFault, int superlayer) {
		if (desiredFault.equals(FaultNames.CHANNEL_ONE)) {
			return new Channel1Detector(superlayer);
		} else if (desiredFault.equals(FaultNames.CHANNEL_TWO)) {
			return new Channel2Detector(superlayer);
		} else if (desiredFault.equals(FaultNames.CHANNEL_THREE)) {
			return new Channel3Detector(superlayer);
		} else if (desiredFault.equals(FaultNames.FUSE_A)) {
			return new FuseADetector(superlayer);
		} else if (desiredFault.equals(FaultNames.FUSE_B)) {
			return new FuseBDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.FUSE_C)) {
			return new FuseCDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.CONNECTOR_E)) {
			return new ConnectorEDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.CONNECTOR_THREE)) {
			return new ConnectorThreeDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.CONNECTOR_TREE)) {
			return new ConnectorTreeDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.PIN_SMALL)) {
			return new PinSmallDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.PIN_BIG)) {
			return new PinBigDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.DEADWIRE)) {
			return new DeadWireDetector(superlayer);
		} else if (desiredFault.equals(FaultNames.HOTWIRE)) {
			return new HotWireDetector(superlayer);
		} else {
			throw new IllegalArgumentException("This detector is not yet implemented");
		}

	}
}
