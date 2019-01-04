package client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import clasDC.faults.FaultNames;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import domain.objectDetection.FaultObjectClassifier;
import lombok.Builder;

public class ViewModelStructure {

	private CLASObject clasObject;

	/**
	 * @throws IOException
	 * 
	 */
	@Builder
	private ViewModelStructure(CLASObject clasObject, int iterations, int scoreIterations, int numBatches)
			throws IOException {
		this.clasObject = clasObject;
		run();
	}

	private void run() throws IOException {
		FaultNames desiredFault = this.clasObject.getDesiredFault();
		ContainerType containerType = this.clasObject.getContainerType();
		System.out.println("Setting up " + desiredFault);

		/**
		 * FaultObjectContainer contains all the necessaries to run the model
		 */
		// FaultObjectContainer container =
		// FaultObjectContainer.builder().clasObject(clasObject).build();

		// the model is stored here
		String fileName;
		if (containerType.equals(ContainerType.OBJ)) {
			fileName = "models/ObjectDetectors/" + desiredFault + ".zip";
		} else if (containerType.equals(ContainerType.CLASS)) {
			fileName = "models/ClassifiersII/" + desiredFault + ".zip";
		} else if (containerType.equals(ContainerType.MULTICLASS)) {
			fileName = "models/MultiClass/" + desiredFault + ".zip";
		} else if (containerType.equals(ContainerType.SEG)) {
			fileName = "models/Segmentation/" + desiredFault + ".zip";

		} else {
			throw new IllegalArgumentException("Type unkown for ContainerType " + containerType);

		}
		System.out.println(fileName);
		FaultObjectClassifier classifier;
		// check if a saved model exists
		if ((new File(fileName)).exists()) {
			System.out.println("remodel");
			// initialize the classifier with the saved model
			classifier = new FaultObjectClassifier(fileName, containerType);
			System.out.println(classifier.getModel().summary());
		} else {
			System.out.println("Model Does Not Exist");
		}
	}

	public static void main(String args[]) throws IOException {

		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE,
		// FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
		// FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		// FaultNames.PIN_SMALL,
		// FaultNames.DEADWIRE, FaultNames.HOTWIRE

		List<FaultNames> aList = Stream.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
				FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
				FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG, FaultNames.PIN_SMALL,
				FaultNames.DEADWIRE, FaultNames.HOTWIRE).collect(Collectors.toCollection(ArrayList::new));

		for (FaultNames faultNames : aList) {
			CLASObject clasObject = SuperLayer.builder().superlayer(6).nchannels(1).minFaults(40).maxFaults(56)
					.desiredFault(faultNames)
					.desiredFaults(Stream
							.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CONNECTOR_TREE,
									FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E,
									FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE,
									FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.DEADWIRE,
									FaultNames.HOTWIRE)
							.collect(Collectors.toCollection(ArrayList::new)))
					.singleFaultGen(false).isScaled(false).containerType(ContainerType.MULTICLASS).build();

			ViewModelStructure sTest = ViewModelStructure.builder().clasObject(clasObject).build();

		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Press enter to exit.");
		sc.nextLine();

		System.exit(0);
	}
}