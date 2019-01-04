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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import faultfinder.service.CompareRunFormService;
import faultfinder.service.CompareRunFormServiceImpl;
import faultfinder.service.MainFrameService;
import faultfinder.ui.MainFrame;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class BottomComparePanel extends JPanel implements ActionListener {
	private MainFrameService mainFrameService = null;

	private JButton cancelButton;
	private JButton compareButton;
	private JButton runButton;

	private JLabel runLabel;

	private JComboBox<String> compareRunComboBox;

	private CompareRunFormService compareRunFormService;

	public BottomComparePanel(MainFrame parentFrame) {
		this.mainFrameService = MainFrameServiceManager.getSession();

		initializeVariables();
		loadData();
		constructLayout();

	}

	private void loadData() {

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

	private void initializeVariables() {
		this.compareRunFormService = new CompareRunFormServiceImpl();
		this.compareRunComboBox = new JComboBox<String>();

		this.cancelButton = new JButton(StringConstants.SORT_FORM_CANCEL);
		this.compareButton = new JButton(StringConstants.COMPARE_FORM_COMPARE);
		this.runLabel = new JLabel(StringConstants.COMPARE_FORM_RUN);
		this.runButton = new JButton(StringConstants.COMPARE_FORM_LOADRUN);

		this.cancelButton.addActionListener(this);
		this.compareButton.addActionListener(this);
		this.runButton.addActionListener(this);

	}

	private void constructLayout() {
		JPanel runInfoPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();

		runInfoPanel.setLayout(new GridBagLayout());

		Insets rightPadding = new Insets(0, 0, 0, 15);
		Insets noPadding = new Insets(0, 0, 0, 0);

		PanelConstraints.addComponent(runInfoPanel, runButton, 0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
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
		if (event.getSource() == this.runButton) {
			loadData();
		} else if (event.getSource() == this.compareButton) {

			if (this.mainFrameService.getMouseReady()) {
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
		}
	}
}
