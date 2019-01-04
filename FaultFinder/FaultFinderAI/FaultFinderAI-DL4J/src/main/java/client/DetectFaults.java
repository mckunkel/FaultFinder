/**
 * 
 */
package client;

import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_DUPLEX;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.util.ArrayUtil;

import clasDC.factories.CLASSuperlayer;
import clasDC.faults.Fault;
import clasDC.faults.FaultCoordinates;
import clasDC.faults.FaultNames;
import domain.FaultDetector.DetectorFactory;
import lombok.Getter;
import utils.FaultUtils;

/**
 * @author m.c.kunkel
 *
 */
public class DetectFaults {

	List<FaultNames> availableDetection;
	INDArray data;
	@Getter
	Mat image;
	@Getter
	Frame frame;
	@Getter
	List<Fault> faultList;

	public DetectFaults(INDArray data) {
		this.data = data;
		init();
	}

	private void init() {

		this.availableDetection = Stream.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
				FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
				FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG, FaultNames.PIN_SMALL,
				FaultNames.DEADWIRE, FaultNames.HOTWIRE).collect(Collectors.toCollection(ArrayList::new));

		setList();
		// setMat();
		// setFrame();
	}

	private void setList() {
		this.faultList = runDetection();
	}

	private void setFrame() {
		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
		this.frame = converter.convert(this.image);
	}

	private void setMat() {
		this.image = getListandMat().getRight();
	}

	public List<Fault> runDetection() {
		List<Fault> ret = new ArrayList<>();
		for (FaultNames fault : availableDetection) {
			ret.addAll(DetectorFactory.getDetector(fault).getFaults(data));
		}

		// time to clean faults because some wires might show up in other faults
		List<Fault> wires = new ArrayList<>();
		List<Fault> notWires = new ArrayList<>();

		for (Fault fault : ret) {
			if (fault.getSubFaultName().equals(FaultNames.DEADWIRE)
					|| fault.getSubFaultName().equals(FaultNames.HOTWIRE)) {
				wires.add(fault);
			} else {
				notWires.add(fault);
			}
		}
		List<Fault> wiresCopy = new ArrayList<>();
		wiresCopy.addAll(wires);

		for (Fault fault : wires) {
			Map<Integer, Pair<Integer, Integer>> aMap = fault.getWireInfo();

			for (Fault notwire : notWires) {
				Map<Integer, Pair<Integer, Integer>> notwireMap = notwire.getWireInfo();
				for (Map.Entry<Integer, Pair<Integer, Integer>> entry : notwireMap.entrySet()) {
					Integer key = entry.getKey();
					Pair<Integer, Integer> value = entry.getValue();
					if (aMap.containsKey(key)) {
						if (aMap.get(key).getLeft() >= value.getLeft()
								&& aMap.get(key).getRight() <= value.getRight()) {
							wiresCopy.remove(fault);
							// System.out.println("Removing");
							// sfault.printWireInformation();
						}
					}
				}
			}

		}

		List<Fault> finalList = new ArrayList<>();
		finalList.addAll(notWires);
		finalList.addAll(wiresCopy);
		// finalList.addAll(wires);
		return finalList;
	}

	public void detectedObjects() {

		CanvasFrame frame = new CanvasFrame("Valididate");
		frame.setTitle(" Fault - Valididation");
		frame.setCanvasSize(448, 450);
		frame.showImage(detectedObjectsCanvas());
	}

	public Image getDetectedImage() {
		Java2DFrameConverter converter = new Java2DFrameConverter();

		Frame frame = detectedObjectsCanvas();
		return converter.getBufferedImage(frame,
				converter.getBufferedImageType(frame) == BufferedImage.TYPE_CUSTOM ? 1.0 : 1.0, false, null);

	}

	public Frame detectedObjectsCanvas() {
		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
		Pair<List<Fault>, Mat> aPair = getListandMat();
		return converter.convert(aPair.getRight());

	}

	public Pair<List<Fault>, Frame> getListandFrame() {

		Pair<List<Fault>, Mat> aPair = getListandMat();

		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

		return Pair.of(aPair.getLeft(), converter.convert(aPair.getRight()));

	}

	public Pair<List<Fault>, Mat> getListandMat() {

		List<Fault> ret = runDetection();
		List<Fault> toRet = new ArrayList<>();
		for (Fault fault : ret) {
			toRet.add(fault);
		}

		int[] preferredImageSize = { 448, 450 };
		INDArray a = FaultUtils.toColor(3, FaultUtils.scaleImage(data, preferredImageSize,
				FaultUtils.getRowsCols(data)[0], FaultUtils.getRowsCols(data)[1]), "kDefault");
		int[] widthheight = FaultUtils.getRowsCols(a);
		int w = widthheight[1];// 112 * 4;
		int h = widthheight[0];// 6 * 100;
		int gridWidth = widthheight[1];
		int gridHeight = widthheight[0];

		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

		NativeImageLoader imageLoader = new NativeImageLoader();
		Mat mat = imageLoader.asMat(a, 0);
		Mat convertedMat = new Mat();
		mat.convertTo(convertedMat, 0, 1, 0);
		Mat image = new Mat();

		resize(convertedMat, image, new Size(w, h));
		List<Fault> aFaults = scaleFaultList(ret, "CLASSuperlayer", preferredImageSize);

		for (Fault fault : aFaults) {
			// fault.printWireInformation();
			FaultCoordinates c = fault.getFaultCoordinates();
			double[] xy1 = new double[] { c.getXCenterPixels() - (c.getXMax() - c.getXMin()) / 2.0,
					c.getYCenterPixels() - (c.getYMax() - c.getYMin()) / 2.0 };
			double[] xy2 = new double[] { c.getXCenterPixels() + (c.getXMax() - c.getXMin()) / 2.0,
					c.getYCenterPixels() + (c.getYMax() - c.getYMin()) / 2.0 };
			String label = fault.getSubFaultName().getFamilyOf();
			int x1 = (int) Math.round((double) w * (c.getXMin()) / (double) gridWidth);
			int y1 = (int) Math.round((double) h * (c.getYMin()) / (double) gridHeight);
			int x2 = (int) Math.round((double) w * ((c.getXMax())) / (double) gridWidth);
			int y2 = (int) Math.round((double) h * ((c.getYMax())) / (double) gridHeight);
			int centerX = (int) Math.round((double) h * (c.getXCenterPixels()) / (double) gridHeight);
			int centerY = (int) Math.round((double) h * (c.getYCenterPixels()) / (double) gridHeight);

			// System.out.println(x1 + " " + " " + y1 + " " + x2 + " " + y2);
			rectangle(image, new Point(x1, y1), new Point(x2, y2), Scalar.BLACK);
			rectangle(image, new Point(centerX, centerY), new Point(centerX, centerY), Scalar.BLACK);

			putText(image, label,
					new Point(x1,
							ThreadLocalRandom.current().nextInt(0, 2) == 1 ? centerY
									: ThreadLocalRandom.current().nextInt(y1, y2 + 1)),
					FONT_HERSHEY_DUPLEX, 0.5, Scalar.YELLOW);
		}

		return Pair.of(toRet, image);

	}

	public List<Fault> scaleFaultList(List<Fault> faults, String comp, int preferredImageSize[]) {
		List<Fault> aFaults = new ArrayList<>();
		for (Fault fault : faults) {
			fault.scaleFaultCoodinates(comp, preferredImageSize);
			aFaults.add(fault);
		}
		return aFaults;

	}

	public static void main(String[] args) throws IOException {

		// FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.CONNECTOR_E,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_TREE,
		// FaultNames.FUSE_A,
		// FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CHANNEL_ONE,
		// FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE, FaultNames.DEADWIRE, FaultNames.HOTWIRE

		FaultNames desiredFault = FaultNames.PIN_BIG;
		CLASSuperlayer sl = CLASSuperlayer.builder().superlayer(1).nchannels(1).minFaults(1).maxFaults(1)
				.desiredFaults(Stream
						.of(FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.CONNECTOR_E,
								FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_TREE, FaultNames.FUSE_A,
								FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
								FaultNames.CHANNEL_THREE, FaultNames.DEADWIRE, FaultNames.HOTWIRE)
						.collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(false).isScaled(false).desiredFault(desiredFault).build();

		int numToGen = 1;
		for (int ij = 0; ij < numToGen; ij++) {
			INDArray data = sl.getImage().getImage();
			data = data.reshape(ArrayUtil.combine(new long[] { 1 }, data.shape()));
			DetectFaults dFaults = new DetectFaults(data);

			// List<FaultNames> aList = sl.getDesiredFaults();

			// FaultUtils.draw(data);
			// for (FaultNames faultNames : aList) {
			// System.out.println("########### TRUE LABELS ##### " + faultNames
			// + " ######");
			System.out.println(sl.faultLocationLabels().get(desiredFault));
			// System.out.println("###################################");
			// }
			// dFaults.detectedObjects(data);
			Pair<List<Fault>, Frame> pair = dFaults.getListandFrame();
			Frame frame = pair.getRight();
			// Frame frame = dFaults.detectedObjectsCanvas(data);

			// for (Fault fault : pair.getLeft()) {
			// fault.printWireInformation();
			// }
			CanvasFrame canvas = new CanvasFrame("Valididate");
			canvas.setTitle(" Fault - Valididation");
			canvas.setCanvasSize(448, 450);
			canvas.showImage(frame);

			// frame.getGraphics();
			sl = sl.getNewSuperLayer(3);
			// System.out.println("\n \n ############ NEW EVENT ############");
		}
	}

}
