/**
 * 
 */
package domain.FaultDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import clasDC.faults.Fault;
import clasDC.faults.FaultNames;
import clasDC.faults.FaultSetter;
import clasDC.faults.FaultSlidingInformation;
import clasDC.objects.CLASObject.ContainerType;
import domain.objectDetection.FaultObjectClassifier;
import lombok.Getter;

/**
 * @author m.c.kunkel
 *
 */
public abstract class FaultDetector {
	protected FaultNames desiredFault;
	@Getter
	protected FaultSlidingInformation faultSlidingInformation;

	protected String classifyFile;
	protected String objectFile;
	protected String slidingFile;

	protected FaultObjectClassifier classClassifier;
	protected FaultObjectClassifier objectClassifier;
	protected FaultObjectClassifier slidingClassifier;

	protected void init() throws IOException {
		this.classifyFile = "models/Classifiers/" + desiredFault + ".zip";
		this.objectFile = "models/MultiClass/" + desiredFault + ".zip";
		this.slidingFile = "models/ObjectDetectors/" + desiredFault + ".zip";

		this.faultSlidingInformation = this.desiredFault.getFSlidingInformation();
		this.classClassifier = new FaultObjectClassifier(classifyFile, ContainerType.CLASS);
		this.objectClassifier = new FaultObjectClassifier(objectFile);
		this.slidingClassifier = new FaultObjectClassifier(slidingFile);

	}

	public abstract INDArray getClassifierPredictions(INDArray data);

	public abstract INDArray getObjectPredictions(INDArray data);

	public abstract INDArray getSliderPredictions(INDArray data);

	public Map<String, INDArray> getAllPredictions(INDArray data) {
		Map<String, INDArray> ret = new HashMap<>();
		ret.put("Classification", getClassifierPredictions(data));
		ret.put("Sliding", getSliderPredictions(data));
		ret.put("Object", getObjectPredictions(data));

		return ret;

	}

	public List<Fault> getFaults(INDArray data) {
		List<Fault> ret = new ArrayList<>();
		// System.out.println("##########CLASSIFICATION################");
		// System.out.println(this.getClassifierPredictions(data).dup().getDouble(0)
		// + " " + this.desiredFault);
		// System.out.println("##########################");

		if (this.getClassifierPredictions(data).dup().getDouble(0) > 0.5) {

			INDArray objectPredictions = this.getObjectPredictions(data);
			// System.out.println("##########OBJECT################");
			// System.out.println(desiredFault);
			// System.out.println(objectPredictions);
			// System.out.println("##########################");

			double threshold = 0.2;
			// if (this.desiredFault.equals(FaultNames.DEADWIRE)) {
			// threshold = 0.95;
			// } else {
			// threshold = 0.35;
			// }
			for (int i = 0; i < objectPredictions.rows(); i++) {
				for (int j = 0; j < objectPredictions.columns(); j++) {

					if (objectPredictions.getDouble(i, j) > threshold) {
						ret.add(FaultSetter.getFault(desiredFault, j, i));
					}
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		INDArray data = Nd4j.zeros(6, 112);
		FaultDetector detector = new Channel1Detector();
		FaultSlidingInformation fSI = detector.getFaultSlidingInformation();
		System.out.println(fSI.getXLength());

	}

}
