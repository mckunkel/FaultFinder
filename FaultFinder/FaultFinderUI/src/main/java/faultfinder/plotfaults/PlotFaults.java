package faultfinder.plotfaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlab.detector.calib.utils.DatabaseConstantProvider;
import org.jlab.groot.data.H2F;

import faultfinder.service.MainFrameService;
import faultfinder.utils.Coordinate;
import faultfinder.utils.MainFrameServiceManager;

public class PlotFaults {

	private MainFrameService mainFrameService = null;
	private Map<Coordinate, H2F> histMap = null;
	private String trackingDB = null;
	private Map<Integer, List<Integer>> layerMap = null;
	private DatabaseConstantProvider dbprovider = null;

	public PlotFaults() {
		init();
		runDBProvider();
	}

	private void init() {
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.mainFrameService.createNewHistograms();
		this.mainFrameService.setMouseReady();
		this.histMap = mainFrameService.getHistogramMap();
		this.trackingDB = "/calibration/dc/tracking/wire_status";
		this.layerMap = getLayerMap();
		this.dbprovider = new DatabaseConstantProvider(this.mainFrameService.getRunNumber(), "default");
	}

	private void runDBProvider() {
		dbprovider.loadTable(trackingDB);
		dbprovider.disconnect();
		dbprovider.show();
	}

	public void makePlots() {

		for (int i = 0; i < dbprovider.length(trackingDB + "/sector"); i++) {

			int iSec = dbprovider.getInteger(trackingDB + "/sector", i);
			int iLay = dbprovider.getInteger(trackingDB + "/layer", i);
			int component = dbprovider.getInteger(trackingDB + "/component", i);
			int status = dbprovider.getInteger(trackingDB + "/status", i);
			if (status != 0) {
				//
				// System.out.println(" sector " + iSec + " superLayer " +
				// getSuperLayer(getLocalLayer(iLay), iLay)
				// + " layer " + iLay + " localLayer " + getLocalLayer(iLay) + "
				// component " + component);

				this.mainFrameService.getHistogramMap()
						.get(new Coordinate(getSuperLayer(getLocalLayer(iLay), iLay) - 1, iSec - 1))
						.setBinContent(component - 1, getLocalLayer(iLay) - 1, 1);

			}
		}
	}

	private int getLocalLayer(int layer) {
		int retValue = 0;
		for (Map.Entry<Integer, List<Integer>> entry : this.layerMap.entrySet()) {
			Integer key = entry.getKey();
			List<Integer> value = entry.getValue();
			if (value.contains(layer)) {
				retValue = key;
			}
		}
		return retValue;

	}

	private int getSuperLayer(int locLayer, int layer) {
		return (layer - locLayer) / 6 + 1;
	}

	private Map<Integer, List<Integer>> getLayerMap() {
		Map<Integer, List<Integer>> retMap = new HashMap<>();
		for (int i = 1; i < 7; i++) {
			retMap.put(i, new ArrayList<>());
			for (int j = 0; j < 6; j++) {
				retMap.get(i).add(i + j * 6);
			}
		}
		return retMap;
	}

	// public static void main(String[] args) {
	//
	// PlotFaults pFaults = new PlotFaults();
	// pFaults.makePlots();
	//
	// }

}
