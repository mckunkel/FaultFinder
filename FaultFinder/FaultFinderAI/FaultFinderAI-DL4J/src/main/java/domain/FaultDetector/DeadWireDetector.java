/**
 * 
 */
package domain.FaultDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.ArrayUtil;

import clasDC.factories.CLASSuperlayer;
import clasDC.faults.FaultNames;
import utils.FaultUtils;

/**
 * @author m.c.kunkel
 *
 */
public class DeadWireDetector extends FaultDetector {

	public DeadWireDetector() {
		this.desiredFault = FaultNames.DEADWIRE;

		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public DeadWireDetector(int superlayer) {
		this.desiredFault = FaultNames.DEADWIRE;
		this.superlayer = 0;
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getClassifierPredictions()
	 */
	@Override
	public INDArray getClassifierPredictions(INDArray data) {
		// shall always assume there is a dead wire. Why? Because the object
		// detector for Deadwire is just a PCA
		return Nd4j.ones(1);
		// return classClassifier.output(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getObjectPredictions()
	 */
	@Override
	public INDArray getObjectPredictions(INDArray data) {
		return predictions(data);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getSliderPredictions()
	 */
	@Override
	public INDArray getSliderPredictions(INDArray data) {
		return predictions(data);

	}

	private INDArray predictions(INDArray data) {
		int[] rowsCols = FaultUtils.getRowsCols(data);

		INDArray array = this.desiredFault.getPossiblePositions().dup();
		INDArrayIndex[] indexs;

		double[] norms = new double[112];
		int placer = 0;
		for (int i = 0; i < rowsCols[1]; i++) {
			indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(),
					NDArrayIndex.interval(i, i + 1) };
			INDArray slice = data.get(indexs).dup();
			int[] rowscols = FaultUtils.getRowsCols(slice);
			int div = 0;

			for (int j = 0; j < rowscols[1]; j++) {
				for (int j2 = 0; j2 < rowscols[0]; j2++) {
					double point = slice.getDouble(0, 0, j2, j);
					if (point != 0.0) {
						div++;
					}
				}
			}
			norms[placer] = (double) slice.sumNumber() / ((double) div);
			placer++;

		}
		double threshold;
		for (int i = 0; i < rowsCols[1]; i++) {
			for (int j = 0; j < rowsCols[0]; j++) {
				if (i < 1) {
					threshold = 5.0;
				}
				// else if (i < 113 && i > 80) {
				// threshold = 0.0;
				// }
				else {
					threshold = 5.5;
				}
				if ((data.getDouble(0, 0, j, i) <= norms[i] * threshold / 100.0)
						|| (data.getDouble(0, 0, j, i) == 0.0)) {
					array.putScalar(j, i, 1.0);
				}
			}
		}

		return array;

	}

	public static void main(String[] args) {
		DeadWireDetector detector = new DeadWireDetector();
		// FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.CONNECTOR_E,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_TREE,
		// FaultNames.FUSE_A,
		// FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CHANNEL_ONE,
		// FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE, FaultNames.DEADWIRE, FaultNames.HOTWIRE
		CLASSuperlayer sl = CLASSuperlayer.builder().superlayer(1).nchannels(1).minFaults(7).maxFaults(10)
				.desiredFaults(Stream.of(FaultNames.DEADWIRE).collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(false).isScaled(false).desiredFault(FaultNames.DEADWIRE).build();

		INDArray data = sl.getImage().getImage();
		FaultUtils.draw(data);

		data = data.reshape(ArrayUtil.combine(new long[] { 1 }, data.shape()));
		INDArray pArray = detector.predictions(data);
		System.out.println(pArray);
		System.out.println("\n");

		System.out.println(sl.faultLocationLabels().get(FaultNames.DEADWIRE));

	}

}
