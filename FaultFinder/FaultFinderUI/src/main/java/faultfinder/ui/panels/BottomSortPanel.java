/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package faultfinder.ui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.nd4j.linalg.api.ndarray.INDArray;

import clasDC.faults.Fault;
import client.DetectFaults;
import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class BottomSortPanel extends JPanel implements ActionListener {
	private MainFrameService mainFrameService = null;
	private JButton sortButton;

	private JLabel sectorLabel;
	private JLabel superLayerLabel;

	private String[] numberStrings = { "1", "2", "3", "4", "5", "6" };
	private JComboBox<String> sectorList;
	private JComboBox<String> superLayerList;

	public BottomSortPanel(JFrame parentFrame) {
		this.mainFrameService = MainFrameServiceManager.getSession();

		initializeVariables();
		initialLayout();

	}

	private void initializeVariables() {
		this.sortButton = new JButton(StringConstants.SORT_FORM_SAVE);
		this.sortButton.addActionListener(this);

		this.sectorLabel = new JLabel(StringConstants.SORT_FORM_SECTOR);
		this.superLayerLabel = new JLabel(StringConstants.SORT_FORM_SUPERLAYER);
		this.sectorList = new JComboBox(numberStrings);
		this.superLayerList = new JComboBox(numberStrings);
		this.sectorList.setSelectedIndex(0);
		this.superLayerList.setSelectedIndex(0);

	}

	private void initialLayout() {

		setLayout(new GridBagLayout());
		// // ///// First row ////////////////////////////

		PanelConstraints.addComponent(this, sectorLabel, 0, 0, 1, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		PanelConstraints.addComponent(this, sectorList, 1, 0, 1, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

		// ////// Next row ////////////////////////////
		PanelConstraints.addComponent(this, superLayerLabel, 0, 1, 1, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		PanelConstraints.addComponent(this, superLayerList, 1, 1, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		PanelConstraints.addComponent(this, sortButton, 1, 2, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

	}

	public void resetList() {
		this.sectorList.setSelectedIndex(0);
		this.superLayerList.setSelectedIndex(0);
	}

	public JComboBox<String> getSectorList() {
		return sectorList;
	}

	public JComboBox<String> getSuperLayerList() {
		return superLayerList;
	}

	public String getSelectedSector() {
		return this.sectorList.getSelectedItem().toString();
	}

	public String getSelectedSuperLayer() {
		return this.superLayerList.getSelectedItem().toString();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.sortButton) {
			if (getIsReady()) {

				if (this.getSelectedSector().isEmpty() || this.getSelectedSector().equalsIgnoreCase("")) {
					JFrame errorFrame = new JFrame("");
					JOptionPane.showMessageDialog(errorFrame, "Please enter a sector to sort",
							"Missing Sector Selection", JOptionPane.ERROR_MESSAGE);
				} else if (this.getSelectedSuperLayer().isEmpty()
						|| this.getSelectedSuperLayer().equalsIgnoreCase("")) {
					JFrame errorFrame = new JFrame("");
					JOptionPane.showMessageDialog(errorFrame, "Please enter a superlayer to sort",
							"Missing Superlayer Selection", JOptionPane.ERROR_MESSAGE);

				} else {
					int sectorString = Integer.parseInt(this.getSectorList().getSelectedItem().toString());
					int superLayerString = Integer.parseInt(this.getSuperLayerList().getSelectedItem().toString());
					this.mainFrameService.setSelectedSector(sectorString);
					this.mainFrameService.setSelectedSuperlayer(superLayerString);
					updateQuery(sectorString, superLayerString);

				}
			} else {
				JFrame errorFrame = new JFrame("");
				JOptionPane.showMessageDialog(errorFrame, "Would you like some green eggs to go with that ham?",
						"Please choose a file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public boolean getIsReady() {
		return this.mainFrameService.getMouseReady();
	}

	protected void updateQuery(int sectorSelection, int superLayerSelection) {

		if (this.mainFrameService.getServiceProvided() == null) {
			this.mainFrameService.getDataPanel().compareWithSQLPanel(
					this.mainFrameService.getBySectorAndSuperLayer(sectorSelection, superLayerSelection));

		}

		this.mainFrameService.getHistogramPanel().updateCanvas(superLayerSelection, sectorSelection);
		this.mainFrameService.runAI();
	}

	private void sendProcedure() {
		TreeSet<StatusChangeDB> queryList = new TreeSet<>();

		// AI Stuff
		int sector = this.mainFrameService.getSelectedSector();
		int superLayer = this.mainFrameService.getSelectedSuperlayer();
		INDArray featureArray = this.mainFrameService.asImageMartix(sector, superLayer).getImage();
		DetectFaults dFaults = new DetectFaults(featureArray, superLayer);

		// List<Fault> faults =
		// this.mainFrameService.getFaultListByMap(superLayer, sector);
		List<Fault> faults = dFaults.getFaultList();

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
					this.mainFrameService.FaultToFaultLogic(fault.getSubFaultName());
					statusChangeDB
							.setProblem_type(StringConstants.PROBLEM_TYPES[this.mainFrameService.getFaultNum() + 1]);

					// statusChangeDB.setStatus_change_type(Status_change_type.broke.toString());
					statusChangeDB.setRunno(this.mainFrameService.getRunNumber());
					queryList.add(statusChangeDB);
				}
			}
		}
		this.mainFrameService.prepareMYSQLQuery(queryList);
		this.mainFrameService.addToCompleteSQLList(queryList);
		this.mainFrameService.getDataPanel().removeItems(queryList);
		this.mainFrameService.clearTempSQLList();
		this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());

	}

}
