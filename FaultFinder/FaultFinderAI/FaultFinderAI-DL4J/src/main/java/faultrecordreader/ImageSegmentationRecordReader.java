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

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.metadata.RecordMetaDataURI;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.datavec.image.data.Image;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.util.ArrayUtil;

import clasDC.factories.CLASFactory;
import clasDC.factories.CLASFactoryImpl;
import clasDC.faults.Fault;
import clasDC.objects.CLASObject;
import utils.FaultUtils;

/**
 * @author m.c.kunkel
 *
 */
public class ImageSegmentationRecordReader implements RecordReader {

	protected List<String> labels;

	protected CLASFactory factory = null;
	private CLASObject clasObject;

	protected Image currentImage;

	public ImageSegmentationRecordReader(CLASObject clasObject) {
		this.clasObject = clasObject;
		this.factory = new CLASFactoryImpl(clasObject);
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
		List<Writable> ret = new ArrayList<>();
		INDArray features = factory.getImage().getImage();
		features = FaultUtils.zeroBorder(features);
		features = FaultUtils.zeroXBorder(features);

		features = features.reshape(ArrayUtil.combine(new long[] { 1 }, features.shape()));

		INDArray labels = factory.getSegmentationLabels();
		labels = FaultUtils.zeroBorder(labels);
		labels = FaultUtils.zeroXBorder(labels);

		labels = labels.reshape(ArrayUtil.combine(new long[] { 1 }, labels.shape()));
		labels = labels.reshape(ArrayUtil.combine(new long[] { 1 }, labels.shape()));

		//System.out.println("SHAPE IN RR IS " + features.shapeInfoToString() + " \n " + labels.shapeInfoToString());

		// ok lets try some transformations
		int[] size = { 56 * 3, 3 * 3 };
		int[] kernalSize = { 2, 2 };
		int[] stride = { 2, 2 };
		// features = FaultUtils.upSampleArray(features, size);
		// features = FaultUtils.downSampleArray(features, kernalSize, stride);
		// features = FaultUtils.downSampleArray(features, kernalSize, stride);
		//
		// labels = FaultUtils.upSampleArray(labels, size);
		// labels = FaultUtils.downSampleArray(labels, kernalSize, stride);
		// labels = FaultUtils.downSampleArray(labels, kernalSize, stride);

		ret.add(new NDArrayWritable(features));
		ret.add(new NDArrayWritable(labels));
		this.reset();
		return ret;
	}

	@Override
	public void reset() {
		this.factory = new CLASFactoryImpl(this.clasObject);

	}

	@Override
	public boolean resetSupported() {
		// Why would we need to reset in this type of training?
		return true;
	}

	@Override
	public List<List<Writable>> next(int num) {
		List<List<Writable>> ret = new ArrayList<>();

		for (int i = 0; i < num && hasNext(); i++) {
			ret.add(next());
		}
		return ret;
	}

	@Override
	public Record nextRecord() {
		List<Writable> list = next();
		URI uri = URI.create("FaultFinderAI");
		// return new org.datavec.api.records.impl.Record(list, metaData)
		return new org.datavec.api.records.impl.Record(list,
				new RecordMetaDataURI(null, ImageSegmentationRecordReader.class));

	}

	// the rest below here are not needed, but kept for as need

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
		this.labels = new ArrayList<>();
		for (Fault faults : factory.getFaultList()) {
			labels.add(faults.getFaultName());
		}
		return this.labels;
	}

	@Override
	public List<RecordListener> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(InputSplit arg0) throws IOException {
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

}