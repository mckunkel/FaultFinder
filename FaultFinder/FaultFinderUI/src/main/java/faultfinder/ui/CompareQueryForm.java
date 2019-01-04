package faultfinder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import faultfinder.service.CompareRunFormService;
import faultfinder.service.CompareRunFormServiceImpl;
import faultfinder.service.MainFrameService;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class CompareQueryForm extends JDialog implements ActionListener {// implements
	// ActionListener
	private MainFrameService mainFrameService = null;

	private JButton cancelButton;
	private JButton compareButton;
	private JLabel runLabel;

	private JComboBox<String> compareRunComboBox;

	private CompareRunFormService compareRunFormService;
	private boolean isReady = false;

	public CompareQueryForm(MainFrame parentFrame) {
		super(parentFrame, StringConstants.COMPARE_FORM_TITLE, false);
		this.mainFrameService = MainFrameServiceManager.getSession();

		initializeVariables();
		loadData();
		constructLayout();
		setWindow(parentFrame);
		pack();

	}

	private void setWindow(JFrame parentFrame) {
		setSize(NumberConstants.SORT_FORM_WINDOW_SIZE_WIDTH, NumberConstants.SORT_FORM_WINDOW_SIZE_HEIGHT);
		setLocationRelativeTo(parentFrame);
	}

	public void loadData() {

		this.compareRunComboBox.removeAllItems();
		List<String> runs = this.compareRunFormService.getAllRuns();
		for (String str : runs) {
			this.compareRunComboBox.addItem(str);
		}
		// I am removing the 1st element because these are test elements
		// for copying the object StatusChangeDB to add the queries properly
		// Spark encoder does not like the enum :(
		// this.compareRunComboBox.removeItemAt(0);
	}

	public void setReady() {
		isReady = true;
	}

	public boolean getIsReady() {
		return isReady;
	}

	private void initializeVariables() {
		this.compareRunFormService = new CompareRunFormServiceImpl();
		this.compareRunComboBox = new JComboBox<String>();

		this.cancelButton = new JButton(StringConstants.SORT_FORM_CANCEL);
		this.compareButton = new JButton(StringConstants.COMPARE_FORM_COMPARE);
		this.runLabel = new JLabel(StringConstants.COMPARE_FORM_RUN);

		this.cancelButton.addActionListener(this);
		this.compareButton.addActionListener(this);

	}

	private void constructLayout() {
		JPanel runInfoPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		// ////////// Buttons Panel ///////////////
		int space = 15;
		Border spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		Border titleBorder = BorderFactory.createTitledBorder(StringConstants.COMPARE_FORM_SUBTITLE);

		runInfoPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));

		runInfoPanel.setLayout(new GridBagLayout());

		Insets rightPadding = new Insets(0, 0, 0, 15);
		Insets noPadding = new Insets(0, 0, 0, 0);

		PanelConstraints.addComponent(runInfoPanel, runLabel, 0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, rightPadding, 0, 0);
		PanelConstraints.addComponent(runInfoPanel, compareRunComboBox, 1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE, noPadding, 0, 0);
		// ////////// Buttons Panel ///////////////

		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(compareButton);
		buttonsPanel.add(cancelButton);

		Dimension btnSize = compareButton.getPreferredSize();
		cancelButton.setPreferredSize(btnSize);

		// Add sub panels to dialog
		setLayout(new BorderLayout());
		add(runInfoPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);

	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.cancelButton) {
			setVisible(false);
		} else if (event.getSource() == this.compareButton) {

			if (isReady) {
				String str = (String) this.compareRunComboBox.getSelectedItem();
				this.compareRunFormService.compareRun(str);
				this.mainFrameService.getDataPanel()
						.setTableModel(this.mainFrameService.getComparedBySectorAndSuperLayer(
								this.mainFrameService.getSelectedSector(),
								this.mainFrameService.getSelectedSuperlayer()));
				this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());

			} else {
				JFrame errorFrame = new JFrame("");
				System.out.println("Problem");
				JOptionPane.showMessageDialog(errorFrame, "Do you like green eggs and ham?", "Please choose a file",
						JOptionPane.ERROR_MESSAGE);
			}

			// this.compareRunFormService.compareRun(str);

			this.setVisible(false);
		}
	}

}
