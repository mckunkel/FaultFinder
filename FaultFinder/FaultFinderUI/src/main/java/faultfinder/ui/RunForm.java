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
package faultfinder.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.nd4j.linalg.api.ndarray.INDArray;

import clasDC.faults.Fault;
import client.DetectFaults;
import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;
import faultfinder.ui.panels.SortPanel;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class RunForm extends JDialog implements ActionListener {
	private MainFrameService mainFrameService = null;
	private SortPanel sortPanel = null;

	private JButton okButton;
	private JTextField percentField;
	private JLabel percentLabel;
	private JComboBox<String> fileComboBox;
	private JLabel fileLabel;

	private ArrayList<String> fileList = null;
	private String dirLocation = null;

	private JFrame errorFrame;

	private Map<String, List<String>> aMap;

	public RunForm(MainFrame parentFrame) {
		super(parentFrame, StringConstants.RUN_FORM_TITLE, false);
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.sortPanel = new SortPanel();

		initializeVariables();
		constructLayout();
		setWindow(parentFrame);
		pack();

	}

	public void clearLists() {
		this.fileList.clear();
		this.fileComboBox.removeAllItems();
		this.aMap.clear();
	}

	public void setFileList(ArrayList<String> fileList) {
		// this.fileList = fileList;
		// ok lets attempt to use multiple files

		for (String s : fileList) {
			String aPlacer = s.substring(s.indexOf("clas_") + 5, s.indexOf(".evio"));
			if (aMap.containsKey(aPlacer)) {
				aMap.get(aPlacer).add(s);
			} else {
				aMap.put(aPlacer, new ArrayList<String>());
				aMap.get(aPlacer).add(s);
			}
		}
		for (Map.Entry<String, List<String>> entry : aMap.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();
			this.fileList.add("Run " + key + "\t\t\t ::: \t\t  N files: " + value.size());
		}
		loadData();
	}

	public void setDirectory(String str) {
		this.dirLocation = str;
	}

	private boolean checkValidFile() {
		if (this.fileList.size() == 0 || this.fileList == null) {
			return false;
		} else {
			return true;
		}
	}

	private void setWindow(JFrame parentFrame) {
		setSize(NumberConstants.SORT_FORM_WINDOW_SIZE_WIDTH, NumberConstants.SORT_FORM_WINDOW_SIZE_HEIGHT);
		setLocationRelativeTo(parentFrame);
	}

	private void initializeVariables() {
		this.okButton = new JButton(StringConstants.FORM_RUN);

		this.okButton.addActionListener(this);
		this.fileList = new ArrayList<String>();

		this.fileComboBox = new JComboBox<String>();
		this.fileLabel = new JLabel(StringConstants.FILE_FORM);

		this.errorFrame = new JFrame("");

		this.percentField = new JTextField(NumberConstants.PERCENT_FORM_WINDOW_FIELD_LENGTH);
		this.percentLabel = new JLabel(StringConstants.RUNPERCENT);
		this.percentField.setText(Double.toString(NumberConstants.DEFAULT_USER_PERCENTAGE));
		this.aMap = new TreeMap();

	}

	public void loadData() {

		for (String str : fileList) {
			this.fileComboBox.addItem(str);
		}
	}

	private void constructLayout() {

		JPanel fileInfoPanel = new JPanel();
		Border spaceBorder = BorderFactory.createEmptyBorder(NumberConstants.BORDER_SPACING,
				NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING);
		Border titleBorder = BorderFactory.createTitledBorder(StringConstants.FILE_FORM_SELECT);
		fileInfoPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		fileInfoPanel.setLayout(new GridBagLayout());

		// Insets rightPadding = new Insets(0, 0, 0, 15);
		Insets leftPadding = new Insets(0, 0, 0, 0);
		Insets rightPadding = new Insets(0, -300, 0, 0);
		Insets noPadding = new Insets(0, 0, 0, 0);

		///// First row /////////////////////////////
		PanelConstraints.addComponent(fileInfoPanel, fileLabel, 0, 0, 1, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.BOTH, leftPadding, 0, 0);
		PanelConstraints.addComponent(fileInfoPanel, fileComboBox, 1, 0, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.BOTH, rightPadding, 0, 0);
		PanelConstraints.addComponent(fileInfoPanel, sortPanel, 0, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, noPadding, 0, 0);
		// PanelConstraints.addComponent(fileInfoPanel, getPercentagePanel(), 0,
		// 2, 1, 1, GridBagConstraints.CENTER,
		// GridBagConstraints.BOTH, 0, 0);
		// ////////// Buttons Panel ///////////////
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		Dimension btnSize = okButton.getPreferredSize();
		okButton.setPreferredSize(btnSize);
		buttonsPanel.add(okButton);

		// make percentage panel

		// Add sub panels to dialog

		setLayout(new GridBagLayout());// BorderLayout
		PanelConstraints.addComponent(this, fileInfoPanel, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 0);
		// PanelConstraints.addComponent(this, getPercentagePanel(), 0, 1, 1, 1,
		// GridBagConstraints.CENTER,
		// GridBagConstraints.BOTH, 0, 0);
		PanelConstraints.addComponent(this, buttonsPanel, 1, 1, 1, 1, GridBagConstraints.LAST_LINE_END,
				GridBagConstraints.BOTH, 0, 0);
		// add(fileInfoPanel, BorderLayout.CENTER);
		// add(getPercentagePanel(), BorderLayout.SOUTH);
		// add(buttonsPanel, BorderLayout.SOUTH);

	}

	private JPanel getPercentagePanel() {
		JPanel fileInfoPanel = new JPanel();

		fileInfoPanel.setLayout(new GridBagLayout());
		Insets rightPadding = new Insets(0, 0, 0, 15);
		Insets noPadding = new Insets(0, 0, 0, 0);

		///// First row /////////////////////////////
		PanelConstraints.addComponent(fileInfoPanel, percentLabel, 0, 0, 0, 0, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, rightPadding, 0, 0);
		PanelConstraints.addComponent(fileInfoPanel, percentField, 1, 0, 0, 0, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, noPadding, 0, 0);

		return fileInfoPanel;
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.okButton) {
			if (checkValidFile()) {

				// System.out.println("you entered " +
				// getUserPercent(percentField.getText()));
				// this.mainFrameService.setUserPercent(getUserPercent(percentField.getText()));
				this.mainFrameService.setUserPercent(0.0);

				String str = (String) this.fileComboBox.getSelectedItem();
				String runChosen = str.substring(str.indexOf("Run ") + 4, str.indexOf("\t")).replaceAll(" ", "");
				this.mainFrameService.createNewDataSets();
				this.mainFrameService.createNewHistograms();
				// this.mainFrameService.getDataProcess().openFile(dirLocation +
				// str);

				this.mainFrameService.getDataProcess().setFileList(aMap.get(runChosen), Integer.parseInt(runChosen));
				this.mainFrameService.setMouseReady();
				setVisible(false);

				processCommands();

			} else {
				System.out.println("Problem");
				JOptionPane.showMessageDialog(errorFrame, "Eggs are not supposed to be green.", "Please choose a file",
						JOptionPane.ERROR_MESSAGE);
				setVisible(false);

			}
			// setVisible(false);
			// processCommands();
		}
	}

	private double getUserPercent(String str) {
		double retValue = 0.0;
		try {
			retValue = Double.parseDouble(str);
		} catch (Exception e) {
			retValue = 0.0;
		}
		return retValue;

	}

	private void processCommands() {
		this.mainFrameService.setSentTodb(false);
		this.mainFrameService.getCompleteSQLList().clear();
		this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());
		this.mainFrameService.getDataProcess().processFile();

		int sector = Integer.parseInt(this.sortPanel.getSelectedSector());
		int superLayer = Integer.parseInt(this.sortPanel.getSelectedSuperLayer());

		this.mainFrameService.setSelectedSector(sector);
		this.mainFrameService.setSelectedSuperlayer(superLayer);

		this.mainFrameService.getDataPanel()
				.setTableModel(this.mainFrameService.getBySectorAndSuperLayer(sector, superLayer));
		this.mainFrameService.getHistogramPanel().updateCanvas(superLayer, sector);

		sendProcedure();

	}

	private void sendProcedure() {

		TreeSet<StatusChangeDB> queryList = new TreeSet<>();

		// AI Stuff
		int sector = this.mainFrameService.getSelectedSector();
		int superLayer = this.mainFrameService.getSelectedSuperlayer();

		INDArray featureArray = this.mainFrameService.asImageMartix(sector, superLayer).getImage();
		DetectFaults dFaults = new DetectFaults(featureArray);
		// this.mainFrameService.getFaultListMap().put(new Coordinate(i, j),
		// dFaults.getFaultList());

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
		//

	}

}
