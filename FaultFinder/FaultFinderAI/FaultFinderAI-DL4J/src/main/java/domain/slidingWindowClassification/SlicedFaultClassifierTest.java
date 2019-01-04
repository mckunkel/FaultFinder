package domain.slidingWindowClassification;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;

import clasDC.faults.FaultNames;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import domain.objectDetection.FaultObjectClassifier;
import domain.objectDetection.FaultObjectContainer;
import lombok.Builder;
import strategies.FaultRecordScalerStrategy;
import strategies.MinMaxStrategy;

public class SlicedFaultClassifierTest {

	private CLASObject clasObject;
	private int iterations = 5;
	private int scoreIterations = 1000;
	private int numBatches = 10000;

	/**
	 * @throws IOException
	 * 
	 */
	@Builder
	private SlicedFaultClassifierTest(CLASObject clasObject, int iterations, int scoreIterations, int numBatches)
			throws IOException {
		this.clasObject = clasObject;
		this.iterations = iterations;
		this.scoreIterations = scoreIterations;
		this.numBatches = numBatches;
		run();
	}

	private void run() throws IOException {
		FaultNames desiredFault = this.clasObject.getDesiredFault();
		ContainerType containerType = this.clasObject.getContainerType();
		System.out.println("Setting up " + desiredFault);

		/**
		 * FaultObjectContainer contains all the necessaries to run the model
		 */
		FaultObjectContainer container = FaultObjectContainer.builder().clasObject(clasObject).build();

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
			throw new IllegalArgumentException("Type unkown for ContainerType " + container);

		}
		System.out.println(fileName);
		FaultObjectClassifier classifier;
		// check if a saved model exists
		if ((new File(fileName)).exists()) {
			System.out.println("remodel");
			// initialize the classifier with the saved model
			classifier = new FaultObjectClassifier(fileName, container);
		} else {
			classifier = new FaultObjectClassifier(container);
		}
		FaultRecordScalerStrategy strategy = new MinMaxStrategy();

		// set up a local web-UI to monitor the training available at
		// localhost:9000
		UIServer uiServer = UIServer.getInstance();
		StatsStorage statsStorage = new InMemoryStatsStorage();
		// additionally print the score on every iteration
		classifier.setListeners(new StatsListener(statsStorage), new ScoreIterationListener(scoreIterations));
		uiServer.attach(statsStorage);

		// train the classifier for a number of checkpoints and save the model
		// after each checkpoint
		RecordReader recordReader = container.getRecordReader();

		// System.out.println(clasObject.getNLabels() + " " +
		// clasObject.getBatchSize());
		for (int i = 0; i < iterations; i++) {
			// train the classifier
			classifier.train(clasObject.getNLabels(), clasObject.getBatchSize(), numBatches, 1, recordReader, strategy);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
			LocalDateTime now = LocalDateTime.now();

			// save the trained model
			classifier.save(fileName);
			// String altFileName = DomainUtils.getDropboxLocal() +
			// dtf.format(now) + "smallTests.zip";

			// classifier.save(altFileName);

			System.out.println("#############################################");
			System.out.println("Last checkpoint " + i + " at " + dtf.format(now));
			System.out.println("#############################################");

		}

		// evaluate the classifier
		Evaluation evaluation = classifier.evaluate(clasObject.getNLabels(), 2, 10000, recordReader, strategy);
		System.out.println("Evalation for " + desiredFault);
		System.out.println(evaluation.stats());
	}

	public static void main(String args[]) throws IOException {

		List<FaultNames> aList = Stream.of(FaultNames.HOTWIRE).collect(Collectors.toCollection(ArrayList::new));

		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE,
		// FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.DEADWIRE,
		// FaultNames.HOTWIRE

		for (FaultNames faultNames : aList) {
			CLASObject clasObject = SuperLayer.builder().superlayer(3).nchannels(1).minFaults(40).maxFaults(56)
					.desiredFault(faultNames)
					.desiredFaults(Stream.of(FaultNames.DEADWIRE).collect(Collectors.toCollection(ArrayList::new)))
					.singleFaultGen(false).isScaled(false).containerType(ContainerType.MULTICLASS).build();

			SlicedFaultClassifierTest sTest = SlicedFaultClassifierTest.builder().clasObject(clasObject).iterations(1)
					.scoreIterations(1000).numBatches(10000).build();

		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Press enter to exit.");
		sc.nextLine();

		System.exit(0);
	}
}
