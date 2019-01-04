/*  
@author m.c.kunkel
*/
package faultfinder.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.jlab.groot.data.H2F;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;
import faultfinder.utils.Coordinate;
import faultfinder.utils.MainFrameServiceManager;
import spark.utils.SparkManager;

public class DataProcess {
	private MainFrameService mainFrameService = null;
	private SparkSession spSession = null;
	private List<StatusChangeDB> emptyDataPoints = null;
	private HipoDataSource reader = null;
	private HipoDataSource[] readers = null;
	private int nEvents = 0;
	private List<String> fileList = null;// = new ArrayList<>();

	public DataProcess() {

	}

	public void openFile(String str) {
		this.reader = new HipoDataSource();
		this.reader.open(str);
		init();

	}

	public void setFileList(List<String> list, int runNumber) {
		this.readers = new HipoDataSource[list.size()];
		init(runNumber);
		this.fileList = list;
	}

	private void init() {
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.spSession = SparkManager.getSession();
		this.emptyDataPoints = new ArrayList<StatusChangeDB>();
		this.mainFrameService.setRunNumber(getRunNumber());
		// setMainFrameServiceNEvents();
	}

	private void init(int runNumber) {
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.spSession = SparkManager.getSession();
		this.emptyDataPoints = new ArrayList<StatusChangeDB>();
		this.mainFrameService.setRunNumber(runNumber);
		// setMainFrameServiceNEvents();

	}

	private void checkNEvents() {
		if (this.nEvents == 0) {
			this.nEvents = this.reader.getSize();
		}
		System.out.println("Will process " + nEvents + " events");
	}

	public void processFile() {

		// checkNEvents();

		for (String string : fileList) {
			this.reader = new HipoDataSource();
			this.reader.open(string);
			if (getRunNumber() != this.mainFrameService.getRunNumber()) {
				System.out.println("We have a problem with the run numbers");
				break;
			}
			fillData();
			this.reader.close();
		}
		createDataset();
	}

	private void fillData() {
		int counter = 0;

		while (this.reader.hasEvent()) {// && counter < 400 &&
			// counter < nEvents
			if (counter % 10000 == 0) {
				System.out.println("done " + counter + " events");
			}
			DataEvent event = reader.getNextEvent();
			counter++;
			if (event.hasBank("TimeBasedTrkg::TBHits")) {
				processTBHits(event);
			}
		}
	}

	private void processTBHits(DataEvent event) {
		DataBank bnkHits = event.getBank("TimeBasedTrkg::TBHits");
		for (int i = 0; i < bnkHits.rows(); i++) {
			this.mainFrameService.getHistogramMap()
					.get(new Coordinate(bnkHits.getInt("superlayer", i) - 1, bnkHits.getInt("sector", i) - 1))
					.fill(bnkHits.getInt("wire", i), bnkHits.getInt("layer", i));

			this.mainFrameService.getFaultDataContainer().increment(bnkHits.getInt("sector", i),
					bnkHits.getInt("superlayer", i), bnkHits.getInt("wire", i), bnkHits.getInt("layer", i));

		}
	}

	private void createDataset() {
		for (int i = 0; i < 6; i++) {// superLayer
			for (int j = 0; j < 6; j++) { // sector
				int xbins = this.mainFrameService.getHistogramMap().get(new Coordinate(i, j)).getXAxis().getNBins();
				int ybins = this.mainFrameService.getHistogramMap().get(new Coordinate(i, j)).getYAxis().getNBins();

				double normalization = getHistNormalization(
						this.mainFrameService.getHistogramMap().get(new Coordinate(i, j)));
				for (int k = 0; k < ybins; k++) {
					for (int l = 0; l < xbins; l++) {
						double content = this.mainFrameService.getHistogramMap().get(new Coordinate(i, j))
								.getBinContent(l, k);
						// if (content == 0) {
						if (content <= normalization * this.mainFrameService.getUserPercent() / 100.0) {

							StatusChangeDB statusChangeDB = new StatusChangeDB();
							statusChangeDB.setSector(String.valueOf(j + 1));
							statusChangeDB.setSuperlayer(String.valueOf(i + 1));
							statusChangeDB.setLoclayer(String.valueOf(k + 1));
							statusChangeDB.setLocwire(String.valueOf(l + 1));

							emptyDataPoints.add(statusChangeDB);

						}

					}

				}
				Dataset<StatusChangeDB> df = spSession.createDataset(emptyDataPoints,
						SparkManager.statusChangeDBEncoder());
				// df.select("sector", "superlayer", "loclayer",
				// "locwire").show();//
				this.mainFrameService.getDataSetMap().put(new Coordinate(i, j), df);
				emptyDataPoints.clear();

				// AI STUFF
				// INDArray featureArray = this.mainFrameService.asImageMartix(j
				// + 1, i + 1).getImage();
				// DetectFaults dFaults = new DetectFaults(featureArray);
				// this.mainFrameService.getFaultListMap().put(new Coordinate(i,
				// j), dFaults.getFaultList());

				// this.mainFrameService.getDetectedFrameMap().put(new
				// Coordinate(i, j), dFaults.detectedObjectsCanvas());

			}
		}
	}

	private double getHistNormalization(H2F aH2f) {
		int xbins = aH2f.getXAxis().getNBins();
		int ybins = aH2f.getYAxis().getNBins();
		double normalization = 0.0;
		for (int k = 0; k < ybins; k++) {
			for (int l = 0; l < xbins; l++) {
				normalization += aH2f.getBinContent(l, k);
			}
		}

		double val = normalization / (xbins * ybins);
		double retValue = 1.0 / (val * Math.log(1.0 / val));
		return val;
	}

	public int getRunNumber() {
		checkNEvents();

		int retValue = 0;
		for (int i = 0; i < this.nEvents; i++) {

			if (reader.gotoEvent(i).hasBank("TimeBasedTrkg::TBHits")) {
				retValue = this.reader.gotoEvent(i).getBank("RUN::config").getInt("run", 0);
				break;
			}
		}
		return retValue;
	}

	public void setNEvents(int nEvents) {
		this.nEvents = nEvents;
	}

	// private void setMainFrameServiceNEvents() {
	// this.mainFrameService.setnEventsInFile(reader.getSize());
	//
	// }

}
