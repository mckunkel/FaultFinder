package faultfinder.faults;

import java.util.TreeSet;

import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;

import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.StringConstants;

public class DeadWireLogic implements FaultLogic {
	private MainFrameService mainFrameService = MainFrameServiceManager.getSession();
	private int xBin;
	private int yBin;

	@Override
	public void drawLogic(EmbeddedCanvas canvas, H2F mouseH2F, int xBin, int yBin) {
		this.xBin = xBin;
		this.yBin = yBin;
		for (int i = 0; i < mouseH2F.getXAxis().getNBins(); i++) {
			for (int j = 0; j < mouseH2F.getYAxis().getNBins(); j++) {
				if (i == xBin - 1 && j == yBin - 1) {
					mouseH2F.setBinContent(i, j, 0.0);
				} else {
					mouseH2F.setBinContent(i, j,
							canvas.getPad().getDatasetPlotters().get(0).getDataSet().getData(i, j));
				}

			}

		}

		canvas.draw(mouseH2F, "same");
		canvas.update();
	}

	@Override
	public void setFaultToDB() {
		TreeSet<StatusChangeDB> queryList = new TreeSet<>();
		StatusChangeDB statusChangeDB = new StatusChangeDB();
		statusChangeDB.setSector(String.valueOf(this.mainFrameService.getSelectedSector()));
		statusChangeDB.setSuperlayer(String.valueOf(this.mainFrameService.getSelectedSuperlayer()));
		statusChangeDB.setLoclayer(String.valueOf(this.yBin));
		statusChangeDB.setLocwire(String.valueOf(this.xBin));
		statusChangeDB.setProblem_type(StringConstants.PROBLEM_TYPES[this.mainFrameService.getFaultNum() + 1]);
		statusChangeDB.setStatus_change_type(this.mainFrameService.getBrokenOrFixed().toString());
		statusChangeDB.setRunno(this.mainFrameService.getRunNumber());
		queryList.add(statusChangeDB);
		this.mainFrameService.prepareMYSQLQuery(queryList);
		this.mainFrameService.removeRowFromMYSQLQuery(queryList);
		this.mainFrameService.addToCompleteSQLList(queryList);
		this.mainFrameService.getDataPanel().removeItems(queryList);
		this.mainFrameService.clearTempSQLList();
		this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());

	}

	@Override
	public int setBundle(int xBin, int yBin) {
		return xBin * yBin;
	}

}
