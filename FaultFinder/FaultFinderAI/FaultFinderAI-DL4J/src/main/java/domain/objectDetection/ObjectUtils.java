/**
 * 
 */
package domain.objectDetection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import clasDC.faults.FaultNames;

/**
 * @author m.c.kunkel
 *
 */
public class ObjectUtils {

	private ObjectUtils() {

	}

	public static List<FaultNames> detectableFaults = Stream
			.of(FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.CONNECTOR_E, FaultNames.CONNECTOR_THREE,
					FaultNames.CONNECTOR_TREE, FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
					FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE)
			.collect(Collectors.toCollection(ArrayList::new));

	public static List<FaultNames> sixLayerFault = Stream.of(FaultNames.CONNECTOR_E, FaultNames.CONNECTOR_THREE,
			FaultNames.CONNECTOR_TREE, FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CHANNEL_ONE,
			FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE).collect(Collectors.toCollection(ArrayList::new));

	public static List<FaultNames> oneLayerFault = Stream.of(FaultNames.PIN_BIG, FaultNames.PIN_SMALL)
			.collect(Collectors.toCollection(ArrayList::new));
}
