/**
 * 
 */
package domain.models;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration.GraphBuilder;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.distribution.TruncatedNormalDistribution;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.CnnLossLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.Upsampling2D;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.impl.ActivationLReLU;
import org.nd4j.linalg.activations.impl.ActivationSigmoid;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * @author m.c.kunkel
 *
 */
public class SegmentationModels {
	public static void resBlock(GraphBuilder graphBuilder, String input, int blockNum, double lReLuVal,
			int... filters) {

		/**
		 * downsample
		 */
		graphBuilder.addLayer("downsampleCNN" + blockNum,
				new ConvolutionLayer.Builder(2, 2).stride(2, 2).nOut(filters[0])
						.activation(new ActivationLReLU(lReLuVal)).convolutionMode(ConvolutionMode.Same).build(),
				input);
		graphBuilder.addLayer("cnn1Block" + blockNum,
				new ConvolutionLayer.Builder(1, 1).stride(1, 1).nOut(filters[1])
						.activation(new ActivationLReLU(lReLuVal)).convolutionMode(ConvolutionMode.Same).build(),
				"downsampleCNN" + blockNum);
		/**
		 * next layer does not get activation, its input into shortcut
		 */
		graphBuilder.addLayer("cnn2Block" + blockNum, new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(filters[2])
				.convolutionMode(ConvolutionMode.Same).build(), "cnn1Block" + blockNum);
		graphBuilder.addVertex("shortcut" + blockNum, new ElementWiseVertex(Op.Add), "downsampleCNN" + blockNum,
				"cnn2Block" + blockNum);
		graphBuilder.addLayer("activationBlock" + blockNum,
				new ActivationLayer.Builder().activation(new ActivationLReLU(lReLuVal)).build(), "shortcut" + blockNum);

	}

	public static void upsample(GraphBuilder graphBuilder, String input, String mergeCNN, int blockNum, double lReLuVal,
			int... filters) {
		graphBuilder.addLayer("up" + blockNum, new Upsampling2D.Builder(2).build(), input)
				.addLayer("cnn1Block" + blockNum, new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(filters[0])
						.convolutionMode(ConvolutionMode.Same).activation(new ActivationLReLU(lReLuVal)).build(),
						"up" + blockNum)
				.addVertex("merge" + blockNum, new MergeVertex(), mergeCNN, "cnn1Block" + blockNum)
				.addLayer("cnn2Block" + blockNum, new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(filters[1])
						.convolutionMode(ConvolutionMode.Same).activation(new ActivationLReLU(lReLuVal)).build(),
						"merge" + blockNum)
				.addLayer("cnn3Block" + blockNum,
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(filters[2])
								.convolutionMode(ConvolutionMode.Same).build(),
						"cnn2Block" + blockNum)
				.addVertex("shortcut" + blockNum, new ElementWiseVertex(Op.Add), "cnn1Block" + blockNum,
						"cnn3Block" + blockNum)
				.addLayer("activationBlock" + blockNum,
						new ActivationLayer.Builder().activation(new ActivationLReLU(lReLuVal)).build(),
						"shortcut" + blockNum);
	}

	public static ComputationGraph KunkelPetersUYolo4SL(int height, int width, int numChannels) {

		double learningRate = 1e-3;
		// goes on AdamUpdater
		// .learningRate(learningRate)
		double l2Rate = 0.0001;
		int seed = 123;
		double leakyReLUVaue = 0.01;

		GraphBuilder graphBuilder = new NeuralNetConfiguration.Builder().seed(seed)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer).gradientNormalizationThreshold(1.0)
				.weightInit(WeightInit.XAVIER).updater(new Adam.Builder().learningRate(learningRate).build()).l2(l2Rate)
				.activation(Activation.IDENTITY).graphBuilder().addInputs("input")
				.setInputTypes(InputType.convolutional(height, width, numChannels))
				.addLayer("beforeCNN", new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(32)
						.activation(new ActivationLReLU(leakyReLUVaue)).convolutionMode(ConvolutionMode.Same).build(),
						"input");
		resBlock(graphBuilder, "beforeCNN", 1, leakyReLUVaue, 64, 128, 64);
		resBlock(graphBuilder, "activationBlock" + 1, 2, leakyReLUVaue, 128, 256, 128);
		upsample(graphBuilder, "activationBlock" + 2, "cnn2Block" + 1, 3, leakyReLUVaue, 256, 512, 256);
		upsample(graphBuilder, "activationBlock" + 3, "beforeCNN", 4, leakyReLUVaue, 128, 256, 128);
		graphBuilder
				.addLayer("lastCNN",
						new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(1).convolutionMode(ConvolutionMode.Same)
								.activation(new ActivationSigmoid()).build(),
						"activationBlock" + 4)
				.addLayer("output", new CnnLossLayer.Builder(LossFunctions.LossFunction.MCXENT)
						.activation(new ActivationSoftmax()).build(), "lastCNN")
				.setOutputs("output");

		ComputationGraph neuralNetwork = new ComputationGraph(graphBuilder.build());

		// initialize the network
		neuralNetwork.init();
		return neuralNetwork;
	}

	public static ComputationGraph UFault(int height, int width, int numChannels) {

		long seed = 1234;

		int[] inputShape = new int[] { numChannels, height, width };

		WeightInit weightInit = WeightInit.RELU;

		IUpdater updater = new AdaDelta();

		CacheMode cacheMode = CacheMode.NONE;

		WorkspaceMode workspaceMode = WorkspaceMode.ENABLED;

		double leakyReLUVaue = 0.0;

		ComputationGraphConfiguration.GraphBuilder graphBuilder = new NeuralNetConfiguration.Builder().seed(seed)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).updater(updater)
				.weightInit(weightInit).dist(new TruncatedNormalDistribution(0.0, 0.5)).l2(5e-5).miniBatch(false)
				.cacheMode(cacheMode).trainingWorkspaceMode(workspaceMode).inferenceWorkspaceMode(workspaceMode)
				.graphBuilder();

		graphBuilder.addInputs("input").setInputTypes(InputType.convolutional(height, width, numChannels))
				.addLayer("conv1-1",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(64).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"input")
				.addLayer("conv1-2",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(64).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"conv1-1")
				.addLayer("pool1",
						new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).build(),
						"conv1-2")

				.addLayer("conv2-1",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(128).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"pool1")
				.addLayer("conv2-2",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(128).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"conv2-1")
				.addLayer("pool2",
						new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).build(),
						"conv2-2")

				.addLayer("conv3-1",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(256).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"pool2")
				.addLayer("conv3-2",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(256).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"conv3-1")

				// up8
				.addLayer("up8-1", new Upsampling2D.Builder(2).build(), "conv3-2")
				.addLayer("up8-2",
						new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(128).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"up8-1")
				.addVertex("merge8", new MergeVertex(), "conv2-2", "up8-2")
				.addLayer("conv8-1",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(128).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"merge8")
				.addLayer("conv8-2",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(128).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"conv8-1")

				// up9
				.addLayer("up9-1", new Upsampling2D.Builder(2).build(), "conv8-2")
				.addLayer("up9-2",
						new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(64).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"up9-1")
				.addVertex("merge9", new MergeVertex(), "conv1-2", "up9-2")
				.addLayer("conv9-1",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(64).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"merge9")
				.addLayer("conv9-2",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(64).convolutionMode(ConvolutionMode.Same)
								.activation(Activation.RELU).build(),
						"conv9-1")
				.addLayer("conv9-3", new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(2)
						.convolutionMode(ConvolutionMode.Same).activation(Activation.RELU).build(), "conv9-2")

				.addLayer("conv10",
						new ConvolutionLayer.Builder(3, 3).stride(1, 1).nOut(1).convolutionMode(ConvolutionMode.Same)
								.activation(new ActivationSigmoid()).build(),
						"conv9-3")
				.addLayer("output", new CnnLossLayer.Builder(LossFunctions.LossFunction.MCXENT)
						.activation(new ActivationSoftmax()).build(), "conv10")

				.setOutputs("output");
		// LossFunctions.LossFunction.MCXENT
		ComputationGraph neuralNetwork = new ComputationGraph(graphBuilder.build());

		// initialize the network
		neuralNetwork.init();
		return neuralNetwork;
	}

	public static void main(String[] args) {
		// ComputationGraph test = SegmentationModels.UFault(252, 252, 1);
		ComputationGraph test = SegmentationModels.KunkelPetersUYolo4SL(8, 116, 1);

		System.out.println(test.summary(InputType.convolutional(8, 116, 1)));

	}
}
