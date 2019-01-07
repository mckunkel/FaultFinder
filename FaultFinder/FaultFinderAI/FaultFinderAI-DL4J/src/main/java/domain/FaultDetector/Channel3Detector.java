/**
 * 
 */
package domain.FaultDetector;

import java.io.IOException;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import clasDC.faults.FaultNames;

/**
 * @author m.c.kunkel <br>
 *         Channel 3 detector If Channel 3 is detected, it can only occur ONE
 *         place in the data
 */
public class Channel3Detector extends FaultDetector {

	public Channel3Detector() {
		this(0);
	}

	public Channel3Detector(int superlayer) {
		this.desiredFault = FaultNames.CHANNEL_THREE;
		this.superlayer = superlayer;
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
		return getObjectPredictions(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getObjectPredictions()
	 */
	@Override
	public INDArray getObjectPredictions(INDArray data) {
		INDArrayIndex[] indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(),
				NDArrayIndex.interval(faultSlidingInformation.getXStart()[0],
						faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength() - 1]) };
		INDArray slice = data.get(indexs).dup();
		return objectClassifier.output(slice);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getSliderPredictions()
	 */
	@Override
	public INDArray getSliderPredictions(INDArray data) {

		// INDArray ret = this.desiredFault.getPossiblePositions().dup();
		// for (int i = 0; i < faultSlidingInformation.getXLength(); i++) {
		// INDArrayIndex[] indexs = new INDArrayIndex[] { NDArrayIndex.all(),
		// NDArrayIndex.all(), NDArrayIndex.all(),
		// NDArrayIndex.interval(faultSlidingInformation.getXStart()[i],
		// faultSlidingInformation.getXEnd()[i]) };
		// INDArray slice = data.get(indexs).dup();
		// INDArray sliderPredictions = slidingClassifier.output(slice);
		// System.out.println(sliderPredictions);
		// ret.putScalar(0, i, sliderPredictions.getDouble(1));
		// }
		return getObjectPredictions(data);

	}

}
