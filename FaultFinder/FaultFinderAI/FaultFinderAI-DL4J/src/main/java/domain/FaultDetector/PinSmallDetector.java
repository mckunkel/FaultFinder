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
 * @author m.c.kunkel
 *
 */
public class PinSmallDetector extends FaultDetector {

	public PinSmallDetector() {
		this(0);
	}

	public PinSmallDetector(int superlayer) {
		this.desiredFault = FaultNames.PIN_SMALL;
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
		return classClassifier.output(data);
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
		INDArray ret = objectClassifier.output(slice);
		return ret.reshape(new int[] { 6, 10 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getSliderPredictions()
	 */
	@Override
	public INDArray getSliderPredictions(INDArray data) {
		INDArray ret = this.desiredFault.getPossiblePositions().dup();
		for (int i = 0; i < faultSlidingInformation.getXLength(); i++) {
			for (int j = 0; j < faultSlidingInformation.getYInc() - 1; j += 2) {

				INDArrayIndex[] indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(),
						NDArrayIndex.interval(j, j + 2), NDArrayIndex.interval(faultSlidingInformation.getXStart()[i],
								faultSlidingInformation.getXEnd()[i]) };

				INDArray slice = data.get(indexs).dup();
				// FaultUtils.draw(slice);
				INDArray sliderPredictions = slidingClassifier.output(slice);
				ret.putScalar(j, i, sliderPredictions.getDouble(0, 0));
				ret.putScalar(j + 1, i, sliderPredictions.getDouble(0, 1));
			}
		}
		return ret;
	}

}
