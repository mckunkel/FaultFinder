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
 * @author m.c.kunkel
 *
 */
public class MultiClassRecordReader implements RecordReader {

	protected CLASFactory factory = null;
	private CLASObject clasObject;
	private FaultNames desiredFault = null;
	private boolean isSliding;

	public MultiClassRecordReader(CLASObject clasObject) {
		this(clasObject, false);
	}

	public MultiClassRecordReader(CLASObject clasObject, boolean isSliding) {
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
		List<Writable> ret = new ArrayList<>();

		INDArray image = factory.getImage().getImage().dup();
		image = image.reshape(ArrayUtil.combine(new long[] { 1 }, image.shape()));

		FaultSlidingInformation faultSlidingInformation = this.desiredFault.getFSlidingInformation();
		INDArrayIndex[] indexs;
		indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(),
				NDArrayIndex.interval(faultSlidingInformation.getXStart()[0],
						faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength() - 1]) };
		INDArray slice = image.get(indexs).dup();
		ret.add(new NDArrayWritable(slice));
		INDArray labels = factory.faultLocationLabels().get(this.desiredFault);
		ret.add(new NDArrayWritable(Nd4j.toFlattened(labels)));
		// System.out.println(faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength()
		// - 1]
		// - faultSlidingInformation.getXStart()[0] + " "
		// +
		// faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength()
		// - 1] + " "
		// + faultSlidingInformation.getXStart()[0]);
		// FaultUtils.draw(slice);
		// System.out.println("\n" + labels + " label \nlength " +
		// labels.length() + " " + Nd4j.toFlattened(labels));
		this.reset();
		return ret;
	}

	@Override
	public List<List<Writable>> next(int batch) {
		List<List<Writable>> ret = new ArrayList<>();
		for (int i = 0; i < batch; i++) {
			ret.add(next());
		}
		return ret;
	}

	private void showProof(INDArray slice, INDArray labels) {
		System.out.println("###### Slice");
		System.out.println(labels);
		FaultUtils.draw(slice, "Slice", "kRainBow");
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
		return new org.datavec.api.records.impl.Record(list, new RecordMetaDataURI(null, MultiClassRecordReader.class));

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
		CLASObject object = SuperLayer.builder().superlayer(3).randomSuperlayer(true).nchannels(1).minFaults(1)
				.maxFaults(4).singleFaultGen(false).containerType(ContainerType.MULTICLASS)
				.desiredFaults(Stream
						.of(FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C, FaultNames.CONNECTOR_TREE,
								FaultNames.CONNECTOR_THREE, FaultNames.CONNECTOR_E, FaultNames.CHANNEL_ONE,
								FaultNames.CHANNEL_TWO, FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
								FaultNames.PIN_SMALL, FaultNames.DEADWIRE, FaultNames.HOTWIRE)
						.collect(Collectors.toCollection(ArrayList::new)))
				.desiredFault(FaultNames.CHANNEL_THREE).desiredFaultGenRate(0.5).build();
		// FaultNames.FUSE_A, FaultNames.FUSE_B, FaultNames.FUSE_C,
		// FaultNames.CONNECTOR_TREE, FaultNames.CONNECTOR_THREE,
		// FaultNames.CONNECTOR_E,
		// FaultNames.CHANNEL_ONE, FaultNames.CHANNEL_TWO,
		// FaultNames.CHANNEL_THREE, FaultNames.PIN_BIG,
		// FaultNames.PIN_SMALL
		// FaultNames.DEADWIRE, FaultNames.HOTWIRE

		// .desiredFaults(Stream.of(FaultNames.CHANNEL_ONE).collect(Collectors.toCollection(ArrayList::new)))
		RecordReader recordReader = new MultiClassRecordReader(object);

		recordReader.next(5);
	}

}
