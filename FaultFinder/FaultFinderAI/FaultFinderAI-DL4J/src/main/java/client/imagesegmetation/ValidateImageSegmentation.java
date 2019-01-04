/**
 * 
 */
package client.imagesegmetation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import clasDC.faults.FaultNames;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import domain.objectDetection.FaultObjectClassifier;
import domain.objectDetection.FaultObjectContainer;
import faultrecordreader.FaultRecorderScaler;
import strategies.FaultRecordScalerStrategy;
import strategies.MinMaxStrategy;
import utils.FaultUtils;

/**
 * @author m.c.kunkel
 *
 */
public class ValidateImageSegmentation {

	public static void main(String[] args) throws IOException {
		FaultRecordScalerStrategy strategy = new MinMaxStrategy();

		CLASObject clasObject = SuperLayer.builder().superlayer(3).nchannels(1).maxFaults(3).desiredFaults(Stream
				.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CONNECTOR_TREE,
						FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
						FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG, FaultNames.PIN_SMALL)
				.collect(Collectors.toCollection(ArrayList::new))).singleFaultGen(false)
				.containerType(ContainerType.SEG).build();

		String fileName = "models/imageSegmentation/SuperLayer0ResNetImpl1Channel.zip"; // 100Kevents

		FaultObjectContainer container = FaultObjectContainer.builder().clasObject(clasObject).build();
		FaultObjectClassifier classifier = new FaultObjectClassifier(fileName);
		RecordReader recordReader = container.getRecordReader();
		DataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, 1).regression(1)
				.maxNumBatches(1000).preProcessor(new FaultRecorderScaler(strategy)).build();
		// INDArray eval = classifier.evaluate(1, 10000, recordReader,
		// strategy);

		INDArray features = iterator.next().getFeatures();
		INDArray labels = iterator.next().getLabels();
		INDArray predictions = classifier.output(features);
		FaultUtils.draw(features);
		FaultUtils.draw(labels);

		FaultUtils.draw(predictions);
		int nonZ = 0;
		for (int i = 0; i < predictions.size(2); i++) {
			for (int j = 0; j < predictions.size(3); j++) {
				if (predictions.getDouble(0, 0, i, j) != 0) {
					nonZ++;
					System.out.println(predictions.getDouble(0, 0, i, j));
				}
			}
		}
		System.out.println(nonZ);
		// System.exit(0);

	}

}
