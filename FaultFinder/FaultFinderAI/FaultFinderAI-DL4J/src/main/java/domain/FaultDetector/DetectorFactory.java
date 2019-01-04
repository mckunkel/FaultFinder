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
		if (desiredFault.equals(FaultNames.CHANNEL_ONE)) {
			return new Channel1Detector();
		} else if (desiredFault.equals(FaultNames.CHANNEL_TWO)) {
			return new Channel2Detector();
		} else if (desiredFault.equals(FaultNames.CHANNEL_THREE)) {
			return new Channel3Detector();
		} else if (desiredFault.equals(FaultNames.FUSE_A)) {
			return new FuseADetector();
		} else if (desiredFault.equals(FaultNames.FUSE_B)) {
			return new FuseBDetector();
		} else if (desiredFault.equals(FaultNames.FUSE_C)) {
			return new FuseCDetector();
		} else if (desiredFault.equals(FaultNames.CONNECTOR_E)) {
			return new ConnectorEDetector();
		} else if (desiredFault.equals(FaultNames.CONNECTOR_THREE)) {
			return new ConnectorThreeDetector();
		} else if (desiredFault.equals(FaultNames.CONNECTOR_TREE)) {
			return new ConnectorTreeDetector();
		} else if (desiredFault.equals(FaultNames.PIN_SMALL)) {
			return new PinSmallDetector();
		} else if (desiredFault.equals(FaultNames.PIN_BIG)) {
			return new PinBigDetector();
		} else if (desiredFault.equals(FaultNames.DEADWIRE)) {
			return new DeadWireDetector();
		} else if (desiredFault.equals(FaultNames.HOTWIRE)) {
			return new HotWireDetector();
		} else {
			throw new IllegalArgumentException("This detector is not yet implemented");
		}

	}

}
