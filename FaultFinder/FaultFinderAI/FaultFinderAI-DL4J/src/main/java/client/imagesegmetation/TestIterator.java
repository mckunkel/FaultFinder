/**
 * 
 */
package client.imagesegmetation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bytedeco.javacv.CanvasFrame;
import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.jlab.groot.base.ColorPalette;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import clasDC.faults.FaultNames;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import domain.objectDetection.FaultObjectContainer;
import faultrecordreader.FaultRecorderScaler;
import strategies.FaultRecordScalerStrategy;
import strategies.MinMaxStrategy;
import utils.FaultUtils;

/**
 * @author m.c.kunkel
 *
 */
public class TestIterator {
	/**
	 * 
	 * @param arr
	 * @return {nRows, nCols}
	 */
	public static int[] getRowsCols(INDArray arr) {
		int rank = arr.rank();
		if (rank == 2) {
			return new int[] { (int) arr.size(0), (int) arr.size(1) };
		} else {
			return new int[] { (int) arr.size(rank == 3 ? 1 : 2), (int) arr.size(rank == 3 ? 2 : 3) };
		}
	}

	public static void draw(INDArray arr) {
		int rank = arr.rank();
		int rows = getRowsCols(arr)[0];
		int cols = getRowsCols(arr)[1];
		int nchannels = 1;
		if (rank != 2) {
			nchannels = (int) arr.size(rank == 3 ? 0 : 1);
		}
		double dataMin = (double) arr.minNumber();
		double dataMax = (double) arr.maxNumber();
		// System.out.println(rank + " rank from FaultUtils");

		BufferedImage b = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		ColorPalette palette = new ColorPalette();
		// System.out.println(arr.shapeInfoToString() + " shape");
		if (nchannels == 1) {

			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					Color weightColor = null;
					if (rank == 2) {
						weightColor = palette.getColor3D(arr.getDouble(y, x), dataMax - dataMin, false);
					} else {
						weightColor = palette.getColor3D(rank == 3 ? arr.getDouble(0, y, x) : arr.getDouble(0, 0, y, x),
								dataMax - dataMin, false);
					}

					int red = weightColor.getRed();
					int green = weightColor.getGreen();
					int blue = weightColor.getBlue();
					int rgb = (red * 65536) + (green * 256) + blue;

					b.setRGB(x, rows - y - 1, rgb);

				}
			}
		} else if (nchannels == 3) {
			System.out.println("CHANNELS = 3!!");
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {

					double red = rank == 3 ? arr.getDouble(0, y, x) : arr.getDouble(0, 0, y, x);
					double green = rank == 3 ? arr.getDouble(1, y, x) : arr.getDouble(0, 1, y, x);
					double blue = rank == 3 ? arr.getDouble(2, y, x) : arr.getDouble(0, 2, y, x);

					// double red = arr.getDouble(0, 0, y, x);
					// double green = arr.getDouble(0, 1, y, x);
					// double blue = arr.getDouble(0, 2, y, x);
					double rgb = ((red * 65536) + (green * 256) + blue);

					b.setRGB(x, rows - y - 1, (int) rgb);

				}
			}
		} else {
			throw new ArithmeticException("Number of channels must be 1 or 3");
		}
		CanvasFrame cframe = new CanvasFrame("FaultUtils Plotted Me");
		cframe.setTitle("FU Plot");
		cframe.setCanvasSize(800, 600);
		cframe.showImage(b);
	}

	public static void main(String[] args) {
		CLASObject clasObject = SuperLayer.builder().superlayer(3).nchannels(1).maxFaults(3).desiredFaults(Stream
				.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CONNECTOR_TREE,
						FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
						FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG, FaultNames.PIN_SMALL)
				.collect(Collectors.toCollection(ArrayList::new))).singleFaultGen(false)
				.containerType(ContainerType.SEG).build();

		FaultObjectContainer container = FaultObjectContainer.builder().clasObject(clasObject).build();
		RecordReader recordReader = container.getRecordReader();
		FaultRecordScalerStrategy strategy = new MinMaxStrategy();

		DataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, 1).regression(1)
				.maxNumBatches(4).preProcessor(new FaultRecorderScaler(strategy)).build();

		// while (iterator.hasNext()) {
		DataSet dSet = iterator.next();
		INDArray features = dSet.getFeatures();
		INDArray labels = dSet.getLabels();
		int[] rowcols = getRowsCols(features);

		System.out.println(rowcols[0] + "   " + rowcols[1] + "   shape " + features.shapeInfoToString());
		FaultUtils.draw(features);
		// FaultUtils.draw(features, 1);
		// FaultUtils.draw(features, 2);
		// FaultUtils.draw(features, 3);

		FaultUtils.draw(labels);
		// }
		for (int i = 0; i < labels.size(2); i++) {
			for (int j = 0; j < labels.size(3); j++) {
				// if (labels.getDouble(0, 0, i, j) != 0) {
				// System.out.print(labels.getDouble(0, 0, i, j) + "\t");
				System.out.print(new DecimalFormat("#0.0000").format(labels.getDouble(0, 0, i, j)) + "\t");
				// }
			}
			System.out.println();
		}

	}

}
