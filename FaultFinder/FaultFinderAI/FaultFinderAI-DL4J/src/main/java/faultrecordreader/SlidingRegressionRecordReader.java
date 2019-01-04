/**
 * 
 */
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
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.ArrayUtil;

import clasDC.factories.CLASFactory;
import clasDC.factories.CLASFactoryImpl;
import clasDC.faults.FaultNames;
import clasDC.faults.FaultSlidingInformation;
import clasDC.objects.CLASObject;
import clasDC.objects.CLASObject.ContainerType;
import clasDC.objects.SuperLayer;
import utils.FaultUtils;

/**
 * @author m.c.kunkel This is for using regression for a sliding window
 *         classification scheme
 *
 */
public class SlidingRegressionRecordReader implements RecordReader {

	protected CLASFactory factory = null;
	private CLASObject clasObject;
	private FaultNames desiredFault = null;
	private boolean isSliding;

	public SlidingRegressionRecordReader(CLASObject clasObject) {
		this(clasObject, false);
	}

	public SlidingRegressionRecordReader(CLASObject clasObject, boolean isSliding) {
		this.clasObject = clasObject;
		this.factory = new CLASFactoryImpl(clasObject);
		this.desiredFault = clasObject.getDesiredFault();
		this.isSliding = isSliding;
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

	// int placer = 0;

	@Override
	public List<Writable> next() {
		return next(4).get(0);
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
		INDArray labels = factory.faultLocationLabels().get(this.desiredFault);

		if (faultSlidingInformation.getYInc() == 0) {
			for (int i = 0; i < faultSlidingInformation.getXLength(); i++) {
				List<Writable> temp = new ArrayList<>();
				indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex
						.interval(faultSlidingInformation.getXStart()[i], faultSlidingInformation.getXEnd()[i]) };
				INDArray slice = image.get(indexs).dup();
				temp.add(new NDArrayWritable(slice));
				INDArray test = Nd4j.zeros(1, 2);
				if (labels.getInt(0, i) == 1) {
					test.putScalar(0, 0, 1);
				} else {
					test.putScalar(0, 1, 1);
				}
				temp.add(new NDArrayWritable(test));
				ret.add(temp);

			}
		}
		this.reset();
		return ret;
	}

	private void showProof(INDArray slice, INDArray labels, int i, int j) {
		System.out.println("###### Slice" + (i + 1) + "  " + (j + 1));
		System.out.println(labels.getInt(j, i));
		FaultUtils.draw(slice, "Slice" + (i + 1) + "  " + (j + 1), "kRainBow");
		System.out.println(slice);
		System.out.println("#######");
	}

	@Override
	public void reset() {
		this.factory = this.factory.getNewFactory();
	}

	@Override
	public boolean resetSupported() {
		// Why would we need to reset in this type of training?
		return true;
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
		return new org.datavec.api.records.impl.Record(list,
				new RecordMetaDataURI(null, SlidingRegressionRecordReader.class));

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

	public static void main(String[] args) {
		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE,
		// FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE,
		// FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		// FaultNames.PIN_SMALL, FaultNames.DEADWIRE, FaultNames.HOTWIRE
		CLASObject object = SuperLayer.builder().superlayer(3).nchannels(1).minFaults(1).maxFaults(4)
				.desiredFaults(Stream.of(FaultNames.CHANNEL_ONE).collect(Collectors.toCollection(ArrayList::new)))
				.singleFaultGen(false).containerType(ContainerType.OBJ).desiredFault(FaultNames.CHANNEL_ONE).build();
		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE,
		// FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		// FaultNames.PIN_SMALL
		// FaultNames.DEADWIRE, FaultNames.HOTWIRE
		RecordReader recordReader = new SlidingRegressionRecordReader(object);

		recordReader.next(5);
	}

}
