package faultfinder.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.ImageIcon;

import org.apache.spark.sql.Dataset;
import org.bytedeco.javacv.Frame;
import org.datavec.image.data.Image;
import org.jlab.groot.data.H2F;

import clasDC.faults.Fault;
import clasDC.faults.FaultNames;
import faultfinder.faults.FaultLogic;
import faultfinder.objects.StatusChangeDB;
import faultfinder.objects.Status_change_type;
import faultfinder.process.DataProcess;
import faultfinder.ui.panels.DataPanel;
import faultfinder.ui.panels.HistogramPanel;
import faultfinder.ui.panels.SQLPanel;
import faultfinder.utils.Coordinate;
import processHipo.FaultDataContainer;

public interface MainFrameService {

	public void setRunNumber(int runNumber);

	public int getRunNumber();

	public void setSelectedSector(int sectorNumber);

	public void setSelectedSuperlayer(int superLayerNumber);

	public int getSelectedSector();

	public int getSelectedSuperlayer();

	public Dataset<StatusChangeDB> getBySectorAndSuperLayer(int sector, int superLayer);

	public Dataset<StatusChangeDB> getComparedBySectorAndSuperLayer(int sector, int superLayer);

	public Map<Coordinate, H2F> getHistogramMap();

	public H2F getHistogramByMap(int superLayer, int sector);

	public Dataset<StatusChangeDB> getDatasetByMap(int superLayer, int sector);

	public Dataset<StatusChangeDB> getComparedDatasetByMap(int superLayer, int sector);

	public Map<Coordinate, Dataset<StatusChangeDB>> getDataSetMap();

	public Map<Coordinate, Dataset<StatusChangeDB>> getComparedDataSetMap();

	public void prepareMYSQLQuery(TreeSet<StatusChangeDB> queryList);

	public TreeSet<StatusChangeDB> getMYSQLQuery();

	public void addToCompleteSQLList(TreeSet<StatusChangeDB> tempList);

	public void clearTempSQLList();

	public TreeSet<StatusChangeDB> getCompleteSQLList();

	public void setListIndices(TreeSet<Integer> intList);

	public TreeSet<Integer> getListIndices();

	public void setSQLList();

	// public int getnEventsInFile();

	// public void setnEventsInFile(int nEventsInFile);

	public boolean sentToDB();

	public void setSentTodb(boolean sentTodb);

	public void prepareAddBackList(TreeSet<StatusChangeDB> queryList);

	public void removeRowFromMYSQLQuery(TreeSet<StatusChangeDB> statusChangeDBs);

	public TreeSet<StatusChangeDB> getAddBackList();

	public void clearAddBackList();

	public void shutdown();

	public FaultLogic getFault();

	public int getFaultNum();

	public void setFault(int fault);

	public int getBundle();

	public void setBundle(int bundle);

	public String getUserName();

	public void setUserName(String userName);

	public void setMouseReady();

	public boolean getMouseReady();

	public void setBrokenOrFixed(Status_change_type brokenOrFixed);

	public Status_change_type getBrokenOrFixed();

	public void setDataPanel(DataPanel dataPanel);

	public DataPanel getDataPanel();

	public void setSQLPanel(SQLPanel sqlPanel);

	public SQLPanel getSQLPanel();

	public void setDataProcess(DataProcess dataProcess);

	public DataProcess getDataProcess();

	public void setHistogramPanel(HistogramPanel histogramPanel);

	public HistogramPanel getHistogramPanel();

	public void setUserPercent(double userPercent);

	public double getUserPercent();

	public void createNewDataSets();

	public void createNewHistograms();

	public void addRunToList(int runNumber);

	public List<Integer> getRunList();

	public void runWriteProcess();

	public ImageIcon getClasIcon();

	public boolean getIsOnJlab();

	public void setWantsToExecute(boolean wantsToExecute);

	public void setVariation(String variation);

	public boolean getWantsToExecute();

	public void runScript() throws IOException, InterruptedException;

	public String getServiceProvided();

	public void setServiceProvided(String serviceProvided);

	public FaultDataContainer getFaultDataContainer();

	public int[][] getData(int sector, int superLayer);

	public Image asImageMartix(int sector, int superLayer);

	public void FaultToFaultLogic(FaultNames faultname);

	/**
	 * @return
	 */
	public Map<Coordinate, List<Fault>> getFaultListMap();

	/**
	 * @return
	 */
	public Map<Coordinate, Frame> getDetectedFrameMap();

	public List<Fault> getFaultListByMap(int superLayer, int sector);

	public Frame getFrameByMap(int superLayer, int sector);

	public void runAI();
}
