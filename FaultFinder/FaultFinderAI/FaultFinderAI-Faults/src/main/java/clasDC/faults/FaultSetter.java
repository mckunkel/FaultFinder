/**
 * 
 */
package clasDC.faults;

import java.util.ArrayList;
import java.util.List;

import clasDC.factories.CLASComponent;

/**
 * @author m.c.kunkel
 *
 */
public class FaultSetter {

	private FaultSetter() {

	}

	public static Fault getFault(FaultNames type, int xplace, int yplace) {
		Fault retFault = null;

		if (type.equals(FaultNames.PIN_SMALL)) {
			retFault = new HVPinFault(xplace, yplace + 1).getInformation();
		} else if (type.equals(FaultNames.PIN_BIG)) {
			retFault = new HVPinFault(xplace + 10, yplace + 1).getInformation();
		} else if (type.equals(FaultNames.CHANNEL_ONE)) {
			retFault = new HVChannelFault(xplace).getInformation();
		} else if (type.equals(FaultNames.CHANNEL_TWO)) {
			retFault = new HVChannelFault(xplace + 4).getInformation();
		} else if (type.equals(FaultNames.CHANNEL_THREE)) {
			retFault = new HVChannelFault(xplace + 7).getInformation();
		} else if (type.equals(FaultNames.CONNECTOR_E)) {
			retFault = new HVConnectorFault(FaultNames.CONNECTOR_E, xplace).getInformation();
		} else if (type.equals(FaultNames.CONNECTOR_THREE)) {
			retFault = new HVConnectorFault(FaultNames.CONNECTOR_THREE, xplace).getInformation();
		} else if (type.equals(FaultNames.CONNECTOR_TREE)) {
			retFault = new HVConnectorFault(FaultNames.CONNECTOR_TREE, xplace).getInformation();
		} else if (type.equals(FaultNames.FUSE_A)) {
			retFault = new HVFuseFault(FaultNames.FUSE_A, xplace).getInformation();
		} else if (type.equals(FaultNames.FUSE_B)) {
			retFault = new HVFuseFault(FaultNames.FUSE_B, xplace).getInformation();
		} else if (type.equals(FaultNames.FUSE_C)) {
			retFault = new HVFuseFault(FaultNames.FUSE_C, xplace).getInformation();
		} else if (type.equals(FaultNames.DEADWIRE)) {
			retFault = new HVDeadWire(xplace, yplace).getInformation();
		} else if (type.equals(FaultNames.HOTWIRE)) {
			retFault = new HVHotWire(xplace, yplace).getInformation();
		}
		return retFault;
	}

	public static void scaleFaults(List<Fault> faults, CLASComponent comp, int preferredImageSize) {
		List<Fault> aFaults = new ArrayList<>();
		for (Fault fault : faults) {
			fault.scaleFaultCoodinates(comp, preferredImageSize);
			aFaults.add(fault);
		}
		faults = aFaults;

	}

	public static void scaleFaults(List<Fault> faults, CLASComponent comp, int preferredImageSize[]) {
		List<Fault> aFaults = new ArrayList<>();
		for (Fault fault : faults) {
			fault.scaleFaultCoodinates(comp, preferredImageSize);
			aFaults.add(fault);
		}
		faults = aFaults;

	}

	public static void scaleFaults(List<Fault> faults, String comp, int preferredImageSize[]) {
		List<Fault> aFaults = new ArrayList<>();
		for (Fault fault : faults) {
			fault.scaleFaultCoodinates(comp, preferredImageSize);
			aFaults.add(fault);
		}
		faults = aFaults;

	}

	public static List<Fault> scaleFaultList(List<Fault> faults, String comp, int preferredImageSize[]) {
		List<Fault> aFaults = new ArrayList<>();
		for (Fault fault : faults) {
			fault.scaleFaultCoodinates(comp, preferredImageSize);
			aFaults.add(fault);
		}
		return aFaults;

	}
	// public static final String[] PROBLEM_TYPES = { "", "hvchannel", "hvpin",
	// "lvfuse", "signalconnector",
	// "ind.wire_dead", "ind.wire_hot" };

}
