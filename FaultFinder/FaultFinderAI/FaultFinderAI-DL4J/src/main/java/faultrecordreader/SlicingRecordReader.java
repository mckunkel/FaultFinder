package faultrecordreader;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.metadata.RecordMetaDataURI;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.ArrayUtil;

import clasDC.factories.CLASFactory;
import clasDC.factories.CLASFactoryImpl;
import clasDC.factories.CLASSuperlayer;
import clasDC.faults.Fault;
import clasDC.faults.FaultNames;
import clasDC.faults.FaultSlidingInformation;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import utils.FaultUtils;

public class SlicingRecordReader implements RecordReader {

	protected CLASFactory factory = null;
	private CLASObject clasObject;
	private FaultNames desiredFault = null;

	public SlicingRecordReader(CLASObject clasObject) {
		this.clasObject = clasObject;
		this.factory = new CLASFactoryImpl(clasObject);
		this.desiredFault = clasObject.getDesiredFault();
	}

	public SlicingRecordReader(int superLayer, int maxFaults, FaultNames desiredFault, boolean singleFaultGeneration,
			boolean blurredFaults, int nchannels) {
		this.factory = CLASSuperlayer.builder().superlayer(superLayer).nchannels(nchannels).minFaults(8)
				.maxFaults(maxFaults)
				.desiredFaults(Stream
						.of(FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.CONNECTOR_E,
								FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_TREE, FaultNames.FUSE_A,
								FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
								FaultNames.CHANNEL_THREE, FaultNames.DEADWIRE, FaultNames.HOTWIRE)
						.collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(singleFaultGeneration).isScaled(false).desiredFault(desiredFault).build();

		// CLASObject clasObject =
		// SuperLayer.builder().superlayer(1).nchannels(1).minFaults(8).maxFaults(10)
		// .desiredFault(desiredFault)
		// .desiredFaults(Stream
		// .of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE,
		// FaultNames.PIN_BIG, FaultNames.PIN_SMALL, FaultNames.DEADWIRE,
		// FaultNames.HOTWIRE)
		// .collect(Collectors.toCollection(ArrayList::new)))
		// .singleFaultGen(false).isScaled(false).containerType(ContainerType.OBJ).build();

		// this.clasObject = clasObject;
		// this.factory = new CLASFactoryImpl(clasObject);
		// this.desiredFault = clasObject.getDesiredFault();
		this.desiredFault = desiredFault;

	}

	@Override
	public boolean batchesSupported() {
		// I might want to set this to true so that I train in batches, reduces
		// memory
		// will get back to this after impl of modes
		return true;
	}

	@Override
	public boolean hasNext() {
		// since this is batch mode, and the data is generated on the fly, this
		// should always be true
		return true;
	}

	@Override
	public List<Writable> next() {

		return this.next(4).get(0);
	}

	@Override
	public void reset() {
		this.factory = factory.getNewFactory();
	}

	@Override
	public boolean resetSupported() {
		// Why would we need to reset in this type of training?
		return true;
	}

	private void showProof(INDArray slice, INDArray labels, int i, int j) {
		System.out.println("###### Slice" + (i + 1) + "  " + (j + 1));
		System.out.println(labels.getInt(j, i));
		FaultUtils.draw(slice, "Slice" + (i + 1) + "  " + (j + 1), "kRainBow");
		System.out.println(slice);
		System.out.println("#######");
	}

	private void showGroup(INDArray slice, INDArray labels, int i, int j) {
		System.out.println("###### Slice" + (i + 1) + "  " + (j + 1));
		System.out.println(labels);
		FaultUtils.draw(slice, "Slice" + (i + 1) + "  " + (j + 1), "kRainBow");
		System.out.println(slice);
		System.out.println("#######");
	}

	@Override
	public List<List<Writable>> next(int batch) {
		List<List<Writable>> ret = new ArrayList<>();
		INDArray image = factory.getImage().getImage().dup();
		image = image.reshape(ArrayUtil.combine(new long[] { 1 }, image.shape()));

		// FaultUtils.draw(factory.getImage().getImage(), "SuperLayer",
		// "kRainBow");
		// System.out.println("###Location Labels for ");
		// System.out
		// .println(this.desiredFault.getSaveName() + " " +
		// factory.faultLocationLabels().get(this.desiredFault));
		//
		// System.out.println(" transposed " +
		// factory.faultLocationLabels().get(this.desiredFault).transpose());
		// System.out.println("##END###");
		FaultSlidingInformation faultSlidingInformation = this.desiredFault.getFSlidingInformation();
		INDArrayIndex[] indexs;
		// need padding for pin_big
		if (this.desiredFault.equals(FaultNames.PIN_BIG)) {
			image = Nd4j.append(image, 8, 0, image.rank() - 1);
		}
		for (int i = 0; i < faultSlidingInformation.getXLength(); i++) {
			if (faultSlidingInformation.getYInc() == 0) {
				List<Writable> temp = new ArrayList<>();
				indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex
						.interval(faultSlidingInformation.getXStart()[i], faultSlidingInformation.getXEnd()[i]) };
				INDArray slice = image.get(indexs).dup();
				temp.add(new NDArrayWritable(slice));
				temp.add(new IntWritable(factory.faultLocationLabels().get(this.desiredFault).getInt(0, i)));
				ret.add(temp);
				// showProof(slice,
				// factory.faultLocationLabels().get(this.desiredFault), i, 0);
			} else {
				for (int j = 0; j < faultSlidingInformation.getYInc() - 1; j += 2) {
					List<Writable> temp = new ArrayList<>();
					// Here we will do classification (regression) on
					// multiple
					// labels
					indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(),
							NDArrayIndex.interval(j, j + 2), NDArrayIndex.interval(
									faultSlidingInformation.getXStart()[i], faultSlidingInformation.getXEnd()[i]) };
					if (this.desiredFault.equals(FaultNames.HOTWIRE) || this.desiredFault.equals(FaultNames.DEADWIRE)) {
						INDArray test = Nd4j.zeros(2, 113);
						test.put(0, 0, factory.faultLocationLabels().get(this.desiredFault).getRow(j));
						test.put(0, 1, factory.faultLocationLabels().get(this.desiredFault).getRow(j + 1));
						if ((double) factory.faultLocationLabels().get(this.desiredFault).getRow(j)
								.sumNumber() == 0.0) {
							test.putScalar(0, 2, 1.0);
						}

					} else {
						INDArray test = Nd4j.zeros(1, 3);

						test.putScalar(0, 0, factory.faultLocationLabels().get(this.desiredFault).getInt(j, i));
						test.putScalar(0, 1, factory.faultLocationLabels().get(this.desiredFault).getInt(j + 1, i));
						// accounting for no desired fault in slice
						if ((double) test.sumNumber() == 0.0) {
							test.putScalar(0, 2, 1.0);
						}

						INDArray slice = image.get(indexs).dup();
						temp.add(new NDArrayWritable(slice));
						temp.add(new NDArrayWritable(test));
						ret.add(temp);
						// showGroup(slice, test, i, j);
					}
				}
			}
		}

		this.reset();
		return ret;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Configuration getConf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConf(Configuration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getLabels() {
		List<String> labels = new ArrayList<>();
		for (Fault faults : factory.getFaultList()) {
			labels.add(faults.getFaultName());
		}
		return null;
	}

	@Override
	public List<RecordListener> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(InputSplit arg0) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(Configuration arg0, InputSplit arg1) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Record loadFromMetaData(RecordMetaData arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Record> loadFromMetaData(List<RecordMetaData> arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record nextRecord() {
		List<Writable> list = next();
		URI uri = URI.create("FaultFinder");
		// return new org.datavec.api.records.impl.Record(list, metaData)
		return new org.datavec.api.records.impl.Record(list, new RecordMetaDataURI(null, SlicingRecordReader.class));

	}

	@Override
	public List<Writable> record(URI arg0, DataInputStream arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setListeners(RecordListener... arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListeners(Collection<RecordListener> arg0) {
		// TODO Auto-generated method stub

	}

	public void initialize() {

	}

	public static void main(String[] args) throws IOException {
		CLASObject object = SuperLayer.builder().superlayer(3).nchannels(1).minFaults(1).maxFaults(4)
				.desiredFaults(Stream.of(FaultNames.PIN_BIG).collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(false).containerType(ContainerType.OBJ).desiredFault(FaultNames.CHANNEL_ONE).build();
		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE,
		// FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		// FaultNames.PIN_SMALL
		// FaultNames.DEADWIRE, FaultNames.HOTWIRE
		RecordReader recordReader = new SlicingRecordReader(object);

		// RecordReader recordReader = new SlicingRecordReader(2, 10,
		// FaultNames.CHANNEL_ONE, true, true, 1);
		for (int i = 0; i < 1; i++) {

			List<List<Writable>> aList = recordReader.next(6);
			for (List<Writable> list : aList) {
				// System.out.println(list.get(0));
				System.out.println(list.get(1));

			}
			System.out.println("######");
		}
		recordReader.close();
	}
}
