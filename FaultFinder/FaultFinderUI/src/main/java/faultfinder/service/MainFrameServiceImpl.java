package faultfinder.service;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.ImageIcon;

import org.apache.spark.sql.Dataset;
import org.bytedeco.javacv.Frame;
import org.jlab.groot.data.H2F;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import clasDC.faults.Fault;
import clasDC.faults.FaultNames;
import client.DetectFaults;
import client.DetectFaults2;
import faultfinder.faults.ChannelLogic;
import faultfinder.faults.DeadWireLogic;
import faultfinder.faults.FaultLogic;
import faultfinder.faults.FuseLogic;
import faultfinder.faults.HotWireLogic;
import faultfinder.faults.PinLogic;
import faultfinder.faults.SignalLogic;
import faultfinder.objects.CCDBWireStatusObject;
import faultfinder.objects.StatusChangeDB;
import faultfinder.objects.Status_change_type;
import faultfinder.process.DataProcess;
import faultfinder.query.DBQuery;
import faultfinder.ui.panels.DataPanel;
import faultfinder.ui.panels.HistogramPanel;
import faultfinder.ui.panels.SQLPanel;
import faultfinder.utils.Coordinate;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.StringConstants;
import lombok.Getter;
import processHipo.FaultDataContainer;
import spark.utils.MainFrameQuery;
import spark.utils.SparkManager;
import strategies.FaultRecordScalerStrategy;
import strategies.MinMaxStrategy;
import utils.FaultUtils;

public class MainFrameServiceImpl implements MainFrameService {

	private MainFrameQuery mainFrameQuery;
	private Map<Coordinate, H2F> occupanciesByCoordinate = null;
	private Map<Coordinate, Dataset<StatusChangeDB>> dataSetByCoordinate = null;
	private Map<Coordinate, Dataset<StatusChangeDB>> dataComparedSetByCoordinate = null;

	private TreeSet<StatusChangeDB> queryList = null;
	private TreeSet<StatusChangeDB> addBackList = null;
	private Status_change_type status_change_type = null;
	private TreeSet<StatusChangeDB> completeQueryList = null;
	private TreeSet<Integer> intList = null;
	// private int nEventsInFile;
	private int runNumber;
	private int sectorNumber;
	private int superLayerNumber;
	private boolean sentTodb;

	private int fault;
	private int bundle = -1000;
	private String userName = null;

	private String variation = "";
	private boolean mouseReady;
	private boolean isOnJlab;

	private FaultLogic faultLogic = null;
	// testing passing panels ///I think this is the wrong idea
	private DataPanel dataPanel;
	private SQLPanel sqlPanel;
	private DataProcess dataProcess;
	private HistogramPanel histogramPanel;
	private double userPercent;
	// well lets give a initial fault
	private List<Integer> runsComplete;
	private BufferedWriter writer = null;

	private Image clasImage = null;
	private ImageIcon clasIcon = null;

	public boolean wantsToExecute;
	private String serviceProvided = null;

	// New stuff for the AI
	@Getter
	private FaultDataContainer faultDataContainer = null;
	private FaultRecordScalerStrategy strategy;

	private Map<Coordinate, List<Fault>> faultListByCoordinate = null;
	private Map<Coordinate, Frame> frameByCoordinate = null;
	private DetectFaults2 detectFaults = new DetectFaults2();

	// End new stuff for the AI
	public MainFrameServiceImpl() {
		this.mainFrameQuery = new MainFrameQuery();
		this.occupanciesByCoordinate = new HashMap<Coordinate, H2F>();
		this.dataSetByCoordinate = new HashMap<Coordinate, Dataset<StatusChangeDB>>();
		this.dataComparedSetByCoordinate = new HashMap<Coordinate, Dataset<StatusChangeDB>>();

		this.queryList = new TreeSet<>();
		this.addBackList = new TreeSet<>();
		this.completeQueryList = new TreeSet<>();
		this.intList = new TreeSet<>();
		this.runsComplete = new ArrayList<>();

		this.mouseReady = false;
		this.fault = 4;
		this.isOnJlab = SparkManager.onJlab();
		URL url = getClass().getResource("/images/CLAS-negatif-high.jpg");
		this.clasImage = new ImageIcon(url).getImage();
		this.clasIcon = new ImageIcon(clasImage.getScaledInstance(120, 100, java.awt.Image.SCALE_SMOOTH));

		this.wantsToExecute = false;

		// AI stuff
		this.faultDataContainer = new FaultDataContainer();
		this.strategy = new MinMaxStrategy();
		this.faultListByCoordinate = new HashMap<Coordinate, List<Fault>>();
		this.frameByCoordinate = new HashMap<Coordinate, Frame>();

	}

	public void setRunNumber(int runNumber) {
		this.runNumber = runNumber;
	}

	public int getRunNumber() {
		return this.runNumber;
	}

	public void setSelectedSector(int sectorNumber) {
		this.sectorNumber = sectorNumber;
	}

	public void setSelectedSuperlayer(int superLayerNumber) {
		this.superLayerNumber = superLayerNumber;
	}

	public int getSelectedSector() {
		return this.sectorNumber;
	}

	public int getSelectedSuperlayer() {
		return this.superLayerNumber;
	}

	public Dataset<StatusChangeDB> getBySectorAndSuperLayer(int sector, int superLayer) {
		this.mainFrameQuery.setDataset(getDatasetByMap(superLayer, sector));
		return this.mainFrameQuery.getBySectorAndSuperLayer(sector, superLayer);
	}

	public Dataset<StatusChangeDB> getComparedBySectorAndSuperLayer(int sector, int superLayer) {
		this.mainFrameQuery.setDataset(getComparedDatasetByMap(superLayer, sector));
		return this.mainFrameQuery.getBySectorAndSuperLayer(sector, superLayer);
	}

	public Map<Coordinate, H2F> getHistogramMap() {
		return this.occupanciesByCoordinate;
	}

	private void createHistograms() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				occupanciesByCoordinate.put(new Coordinate(i, j),
						new H2F("Occupancy all hits SL" + i + "sector" + j, "", NumberConstants.xBins,
								NumberConstants.xMin, NumberConstants.xMax, NumberConstants.yBins, NumberConstants.yMin,
								NumberConstants.yMax));
				occupanciesByCoordinate.get(new Coordinate(i, j)).setTitleX("Wire");
				occupanciesByCoordinate.get(new Coordinate(i, j)).setTitleY("Layer");
				occupanciesByCoordinate.get(new Coordinate(i, j))
						.setTitle("Sector " + (j + 1) + " Superlayer" + (i + 1));
			}
		}
	}

	private void createDatasets() {
		for (int i = 0; i < 6; i++) {// superLayer
			for (int j = 0; j < 6; j++) {// sector
				Dataset<StatusChangeDB> test = null;
				List<Fault> aList = null;
				Frame frame = null;
				dataSetByCoordinate.put(new Coordinate(i, j), test);
				dataComparedSetByCoordinate.put(new Coordinate(i, j), test);
				faultListByCoordinate.put(new Coordinate(i, j), aList);
				frameByCoordinate.put(new Coordinate(i, j), frame);
			}
		}
	}

	public H2F getHistogramByMap(int superLayer, int sector) {
		return this.occupanciesByCoordinate.get(new Coordinate(superLayer - 1, sector - 1));
	}

	public Dataset<StatusChangeDB> getDatasetByMap(int superLayer, int sector) {
		return this.dataSetByCoordinate.get(new Coordinate(superLayer - 1, sector - 1));
	}

	public Dataset<StatusChangeDB> getComparedDatasetByMap(int superLayer, int sector) {
		return this.dataComparedSetByCoordinate.get(new Coordinate(superLayer - 1, sector - 1));
	}

	public Map<Coordinate, Dataset<StatusChangeDB>> getDataSetMap() {
		return this.dataSetByCoordinate;
	}

	public Map<Coordinate, Dataset<StatusChangeDB>> getComparedDataSetMap() {
		return this.dataComparedSetByCoordinate;
	}

	// for inserting into MYSQL
	public void prepareMYSQLQuery(TreeSet<StatusChangeDB> queryList) {
		this.queryList = queryList;
	}

	public TreeSet<StatusChangeDB> getMYSQLQuery() {
		return this.queryList;
	}

	public void addToCompleteSQLList(TreeSet<StatusChangeDB> tempList) {
		this.completeQueryList.addAll(tempList);
		this.completeQueryList.descendingSet();
	};

	public TreeSet<StatusChangeDB> getCompleteSQLList() {
		return this.completeQueryList;
	};

	public void setListIndices(TreeSet<Integer> intList) {
		this.intList = intList;
	}

	public TreeSet<Integer> getListIndices() {
		return this.intList;
	}

	public void clearTempSQLList() {
		this.queryList.clear();
	};

	public void setSQLList() {

	};

	// public int getnEventsInFile() {
	// return nEventsInFile;
	// }

	// public void setnEventsInFile(int nEventsInFile) {
	// this.nEventsInFile = nEventsInFile;
	// }

	public void setSentTodb(boolean sentTodb) {
		this.sentTodb = sentTodb;
	}

	public boolean sentToDB() {
		return this.sentTodb;
	}

	public void prepareAddBackList(TreeSet<StatusChangeDB> queryList) {
		this.addBackList = queryList;
	}

	public TreeSet<StatusChangeDB> getAddBackList() {
		return this.addBackList;
	}

	public void removeRowFromMYSQLQuery(TreeSet<StatusChangeDB> statusChangeDBs) {
		ArrayList<StatusChangeDB> listToRemove = new ArrayList<>();
		// System.out.println("removeRow was called in MainFrameServiceImpl");
		for (StatusChangeDB ro : statusChangeDBs) {
			for (StatusChangeDB statusChangeDB : this.completeQueryList) {
				if (statusChangeDB.getSector().equals(ro.getSector())
						&& statusChangeDB.getSuperlayer().equals(ro.getSuperlayer())
						&& statusChangeDB.getLoclayer().equals(ro.getLoclayer())
						&& statusChangeDB.getLocwire().equals(ro.getLocwire())) {
					listToRemove.add(statusChangeDB);
					// System.out.println(statusChangeDB.getSector() + " " +
					// statusChangeDB.getSuperlayer() + " "
					// + statusChangeDB.getLoclayer() + " " +
					// statusChangeDB.getLocwire());
				}
			}
		}
		this.completeQueryList.removeAll(listToRemove);
	}

	public void clearAddBackList() {
		this.addBackList.clear();

	}

	@Override
	public FaultLogic getFault() {
		return this.faultLogic;
	}

	@Override
	public int getFaultNum() {
		return this.fault;
	}

	@Override
	public void setFault(int fault) {
		this.fault = fault;
		switch (fault) {
		case 0:
			this.faultLogic = new ChannelLogic();
			break;
		case 1:
			this.faultLogic = new PinLogic();
			break;
		case 2:
			this.faultLogic = new FuseLogic();
			break;
		case 3:
			this.faultLogic = new SignalLogic();
			break;
		case 4:
			this.faultLogic = new DeadWireLogic();
			break;
		case 5:
			this.faultLogic = new HotWireLogic();
			break;
		default:
			break;
		}
	}

	/**
	 * A wrapper for Fault to FaultLogic for the UI and AI to communicate
	 * properly
	 * 
	 * @return
	 */
	@Override
	public void FaultToFaultLogic(FaultNames faultname) {
		if (faultname.equals(FaultNames.PIN_BIG) || faultname.equals(FaultNames.PIN_SMALL)) {
			this.fault = 1;
		} else if (faultname.equals(FaultNames.CHANNEL_ONE) || faultname.equals(FaultNames.CHANNEL_TWO)
				|| faultname.equals(FaultNames.CHANNEL_THREE)) {
			this.fault = 0;
		} else if (faultname.equals(FaultNames.FUSE_A) || faultname.equals(FaultNames.FUSE_B)
				|| faultname.equals(FaultNames.FUSE_C)) {
			this.fault = 2;
		} else if (faultname.equals(FaultNames.CONNECTOR_E) || faultname.equals(FaultNames.CONNECTOR_THREE)
				|| faultname.equals(FaultNames.CONNECTOR_TREE)) {
			this.fault = 3;
		} else if (faultname.equals(FaultNames.HOTWIRE)) {
			this.fault = 5;
		} else if (faultname.equals(FaultNames.DEADWIRE)) {
			this.fault = 4;
		} else {
			throw new IllegalArgumentException("This " + faultname.getSaveName() + " is not recognized");
		}

	}

	@Override
	public int getBundle() {
		return bundle;
	}

	@Override
	public void setBundle(int bundle) {
		this.bundle = bundle;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void shutdown() {
		this.mainFrameQuery.shutdown();
	}

	public void setMouseReady() {
		this.mouseReady = true;
	}

	public boolean getMouseReady() {
		return this.mouseReady;
	}

	@Override
	public void setBrokenOrFixed(Status_change_type brokenOrFixed) {
		this.status_change_type = brokenOrFixed;
	}

	public Status_change_type getBrokenOrFixed() {
		return this.status_change_type;
	}

	public void setDataPanel(DataPanel dataPanel) {
		this.dataPanel = dataPanel;
	}

	public DataPanel getDataPanel() {
		return this.dataPanel;
	}

	public void setSQLPanel(SQLPanel sqlPanel) {
		this.sqlPanel = sqlPanel;
	}

	public SQLPanel getSQLPanel() {
		return this.sqlPanel;
	}

	public void setDataProcess(DataProcess dataProcess) {
		this.dataProcess = dataProcess;
	}

	public DataProcess getDataProcess() {
		return this.dataProcess;
	}

	public void setHistogramPanel(HistogramPanel histogramPanel) {
		this.histogramPanel = histogramPanel;
	}

	public HistogramPanel getHistogramPanel() {
		return this.histogramPanel;
	}

	// histogramPanel
	public void setUserPercent(double userPercent) {
		this.userPercent = userPercent;
	}

	public double getUserPercent() {
		return this.userPercent;
	}

	public void createNewDataSets() {
		this.createDatasets();
	}

	public void createNewHistograms() {
		this.createHistograms();
	}

	@Override
	public void addRunToList(int runNumber) {
		this.runsComplete.add(runNumber);

	}

	public List<Integer> getRunList() {
		return this.runsComplete;
	}

	// public int getInitialFault() {
	// return this.intialFault;
	// }
	private void openFile() {
		try {
			this.writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream("SubmitStatusTablesToCCDB.sh"), "utf-8"));
		} catch (Exception e) {
		}
	}

	private void appendFile() {
		try {
			this.writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream("SubmitStatusTablesToCCDB.sh", true), "utf-8"));
		} catch (Exception e) {
		}
	}

	private void makeFileHeader() {
		try {
			writer.write("#!/bin/csh");
			writer.newLine();
			writer.write("source /group/clas12/gemc/environment.csh 4a.2.3");
			writer.newLine();
			writer.write("setenv CCDB_CONNECTION mysql://clas12writer:geom3try@clasdb.jlab.org/clas12");
			writer.newLine();
		} catch (Exception e) {
		}
	}

	private void closeFile() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fileDelete() {
		File file = new File("SubmitStatusTablesToCCDB.sh");
		if (file.exists() && !file.isDirectory()) {
			file.delete();
		}
	}

	private boolean fileExist() {
		File file = new File("SubmitStatusTablesToCCDB.sh");
		return file.exists();
	}

	private void processCCDBRequest(List<Integer> runList) {
		DBQuery dbQuery = new DBQuery();
		// for (Integer i : this.mainFrameService.getRunList()) {

		for (Integer i : runList) {
			System.out.println(i + " in processCCDB");
			List<CCDBWireStatusObject> aList = dbQuery.getBadComponentList(i);
			List<CCDBWireStatusObject> allComponents = new ArrayList<>();

			for (int sector = 1; sector <= 6; sector++) {
				for (int superLayer = 1; superLayer <= 6; superLayer++) {
					for (int locLayer = 1; locLayer <= 6; locLayer++) {
						int layer = (superLayer - 1) * 6 + locLayer;
						for (int wire = 1; wire <= 112; wire++) {
							allComponents.add(findLike(sector, layer, wire, aList));
						}
					}
				}
			}

			createCCDBFile(i, allComponents);
			addToQueryFile(i);
		}

	}

	private void addToQueryFile(Integer i) {

		try {
			if (this.variation.isEmpty()) {
				writer.write("ccdb add calibration/dc/tracking/wire_status -r " + i + "-" + i + " Run_" + i
						+ ".txt #Adding run " + i);
			} else {
				writer.write("ccdb add calibration/dc/tracking/wire_status -v " + this.variation + " -r " + i + "-" + i
						+ " Run_" + i + ".txt #Adding run " + i);
			}
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private CCDBWireStatusObject findLike(int sector, int layer, int wire, List<CCDBWireStatusObject> aList) {
		for (int status = 1; status <= 7; status++) {
			if (aList.contains(new CCDBWireStatusObject(sector, layer, wire, status))) {
				return new CCDBWireStatusObject(sector, layer, wire, status);
			}
		}
		return new CCDBWireStatusObject(sector, layer, wire, 0);
	}

	private void createCCDBFile(Integer i, List<CCDBWireStatusObject> allComponents) {
		try (BufferedWriter runWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("Run_" + i + ".txt"), "utf-8"))) {
			runWriter.write("#& sector layer component status");
			runWriter.newLine();
			for (CCDBWireStatusObject ccdbWireStatusObject : allComponents) {
				runWriter.write(ccdbWireStatusObject.getSector() + " " + ccdbWireStatusObject.getLayer() + " "
						+ ccdbWireStatusObject.getComponent() + " " + ccdbWireStatusObject.getStatus());
				runWriter.newLine();
			}
			runWriter.close();
		} catch (

		UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runWriteProcess() {
		fileDelete();
		openFile();
		makeFileHeader();
		processCCDBRequest(getRunList());
		closeFile();
		this.runsComplete.clear();
		if (getWantsToExecute()) {
			try {
				runScript();
			} catch (IOException | InterruptedException e) {
				System.err.println("This error is in the running at the Jlab cluster. Do you have permissions?");
				e.printStackTrace();
			}
		}
	}

	public ImageIcon getClasIcon() {
		return clasIcon;
	}

	public boolean getIsOnJlab() {
		return isOnJlab;
	}

	public void setWantsToExecute(boolean wantsToExecute) {
		this.wantsToExecute = wantsToExecute;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}

	public boolean getWantsToExecute() {
		return wantsToExecute;
	}

	public void runScript() throws IOException, InterruptedException {
		String scriptName = "SubmitStatusTablesToCCDB.sh";// Hello.bash
		// String scriptName = "Hello.bash";

		File file = new File(scriptName);

		if (file.exists()) {
			if (!file.canExecute()) {
				file.setExecutable(true);
				file.setReadable(true);
				file.setWritable(true);
			}

			String command = "./" + scriptName;

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			// Sets the source and destination for subprocess standard I/O to be
			// the
			// same as those of the current Java process.
			processBuilder.inheritIO();
			Process process = processBuilder.start();

			int exitValue = process.waitFor();
			if (exitValue != 0) {
				// check for errors
				new BufferedInputStream(process.getErrorStream());
				throw new RuntimeException("execution of script failed!");
			}
		}
	}

	public String getServiceProvided() {
		return serviceProvided;
	}

	public void setServiceProvided(String serviceProvided) {
		this.serviceProvided = serviceProvided;
	}

	// AI Stuff
	public int[][] getData(int sector, int superLayer) {
		return faultDataContainer.getData(sector, superLayer);
	}

	public INDArray getFeatureVectorAsMatrix(int sector, int superLayer, FaultRecordScalerStrategy strategy) {
		INDArray array = Nd4j.create(FaultUtils.convertToDouble(this.getData(sector, superLayer)));
		strategy.normalize(array);

		return array;
	}

	public org.datavec.image.data.Image asImageMartix(int sector, int superLayer) {
		return FaultUtils.asImageMatrix(1, getFeatureVectorAsMatrix(sector, superLayer, this.strategy));

	}

	@Override
	public Map<Coordinate, List<Fault>> getFaultListMap() {
		return this.faultListByCoordinate;

	}

	@Override
	public Map<Coordinate, Frame> getDetectedFrameMap() {
		return this.frameByCoordinate;
	}

	public List<Fault> getFaultListByMap(int superLayer, int sector) {
		return this.faultListByCoordinate.get(new Coordinate(superLayer - 1, sector - 1));
	}

	public Frame getFrameByMap(int superLayer, int sector) {
		return this.frameByCoordinate.get(new Coordinate(superLayer - 1, sector - 1));
	}

	public void runAI() {
		TreeSet<StatusChangeDB> queryList = new TreeSet<>();

		// AI Stuff
		int sector = this.getSelectedSector();
		int superLayer = this.getSelectedSuperlayer();
		INDArray featureArray = this.asImageMartix(sector, superLayer).getImage();
		DetectFaults dFaults = new DetectFaults(featureArray, superLayer);
		List<Fault> faults = detectFaults.runDetection(featureArray, superLayer);

		for (Fault fault : faults) {
			int xMin = (int) fault.getFaultCoordinates().getXMin();
			int xMax = (int) fault.getFaultCoordinates().getXMax();
			int yMin = (int) fault.getFaultCoordinates().getYMin();
			int yMax = (int) fault.getFaultCoordinates().getYMax();

			for (int i = xMin; i < xMax; i++) {
				for (int j = yMin; j < yMax; j++) {
					StatusChangeDB statusChangeDB = new StatusChangeDB();
					statusChangeDB.setSector(Integer.toString(sector));
					statusChangeDB.setSuperlayer(Integer.toString(superLayer));
					statusChangeDB.setLoclayer(Integer.toString(j + 1));
					statusChangeDB.setLocwire(Integer.toString(i + 1));
					this.FaultToFaultLogic(fault.getSubFaultName());
					statusChangeDB.setProblem_type(StringConstants.PROBLEM_TYPES[this.getFaultNum() + 1]);
					statusChangeDB.setStatus_change_type(Status_change_type.broke.toString());
					// statusChangeDB.setStatus_change_type(Status_change_type.broke.toString());
					statusChangeDB.setRunno(this.getRunNumber());
					queryList.add(statusChangeDB);
				}
			}
		}
		this.prepareMYSQLQuery(queryList);
		this.addToCompleteSQLList(queryList);
		this.getDataPanel().removeItems(queryList);
		this.clearTempSQLList();
		this.getSQLPanel().setTableModel(this.getCompleteSQLList());

	}

}
