/**
 * 
 */
package client.imagesegmetation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;

import clasDC.faults.FaultNames;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import domain.objectDetection.FaultObjectClassifier;
import domain.objectDetection.FaultObjectContainer;
import strategies.FaultRecordScalerStrategy;
import strategies.MinMaxStrategy;

/**
 * @author m.c.kunkel
 *
 */
public class TrainImageSegmentation {
	public static void main(String args[]) throws IOException {
		// the model is stored here
		int scoreIterations = 100;
		/**
		 * Create a CLASObject for the container
		 */
		// , FaultNames.PIN_SMALL,FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE,
		// FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		CLASObject clasObject = SuperLayer.builder().superlayer(3).nchannels(1).maxFaults(3)
				.desiredFaults(Stream.of(FaultNames.FUSE_A).collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(true).containerType(ContainerType.SEG).build();
		int numLabels = clasObject.getDesiredFaults().size() + 1;
		System.out.println(numLabels);
		/**
		 * FaultObjectContainer contains all the necessaries to run the model
		 */
		FaultObjectContainer container = FaultObjectContainer.builder().clasObject(clasObject).build();

		String fileName = "models/imageSegmentation/" + clasObject.getObjectType() + "0FaultNet.zip"; // 100Kevents
		// events
		FaultObjectClassifier classifier;
		// check if a saved model exists
		if ((new File(fileName)).exists()) {
			System.out.println("remodel");
			// initialize the classifier with the saved model
			classifier = new FaultObjectClassifier(fileName);
		} else {
			// Get the model that belongs to the FaultObjectContainer
			classifier = new FaultObjectClassifier(container);
		}
		// Need to normalize the data
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

		int checkPoints = 5;
		for (int i = 0; i < checkPoints; i++) {
			// train the classifier
			classifier.train(numLabels, 1, 10000, 1, recordReader, strategy);// 10000

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
			LocalDateTime now = LocalDateTime.now();

			// save the trained model
			String saveName = "models/imageSegmentation/" + clasObject.getObjectType() + i + "FaultNet.zip";

			classifier.save(saveName);

			System.out.println("#############################################");
			System.out.println("Last checkpoint " + i + " at " + dtf.format(now));
			System.out.println("#############################################");

		}

		// evaluate the classifier
		// Evaluation evaluation = classifier.evaluate(2, 1, 10000,
		// recordReader, strategy);
		// System.out.println(evaluation.stats());
		System.exit(0);
	}
}