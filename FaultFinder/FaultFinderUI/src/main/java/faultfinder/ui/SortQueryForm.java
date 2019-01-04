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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import faultfinder.service.MainFrameService;
import faultfinder.ui.panels.SortPanel;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.StringConstants;

public class SortQueryForm extends JDialog implements ActionListener {// implements
	// ActionListener
	private MainFrameService mainFrameService = null;
	private SortPanel sortPanel;
	private JButton cancelButton;
	private JButton sortButton;
	private boolean isReady = false;

	public SortQueryForm(MainFrame parentFrame) {
		super(parentFrame, StringConstants.SORT_FORM_TITLE, false);

		this.mainFrameService = MainFrameServiceManager.getSession();
		this.sortPanel = new SortPanel();

		initializeVariables();
		constructLayout();
		setWindow(parentFrame);
		pack();
	}

	private void setWindow(JFrame parentFrame) {
		setSize(NumberConstants.SORT_FORM_WINDOW_SIZE_WIDTH, NumberConstants.SORT_FORM_WINDOW_SIZE_HEIGHT);
		setLocationRelativeTo(parentFrame);
	}

	private void initializeVariables() {
		this.cancelButton = new JButton(StringConstants.SORT_FORM_CANCEL);
		this.sortButton = new JButton(StringConstants.SORT_FORM_SAVE);
		this.cancelButton.addActionListener(this);
		this.sortButton.addActionListener(this);

	}

	private void constructLayout() {
		JPanel buttonsPanel = new JPanel();

		// ////////// Buttons Panel ///////////////
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(sortButton);
		buttonsPanel.add(cancelButton);

		Dimension btnSize = cancelButton.getPreferredSize();
		sortButton.setPreferredSize(btnSize);

		// Add sub panels to dialog
		setLayout(new BorderLayout());
		add(this.sortPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);

	}

	public void resetList() {
		this.sortPanel.resetList();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.cancelButton) {
			setVisible(false);
		} else if (event.getSource() == this.sortButton) {
			if (isReady) {

				if (this.sortPanel.getSelectedSector().isEmpty()
						|| this.sortPanel.getSelectedSector().equalsIgnoreCase("")) {
					JFrame errorFrame = new JFrame("");
					JOptionPane.showMessageDialog(errorFrame, "Please enter a sector to sort",
							"Missing Sector Selection", JOptionPane.ERROR_MESSAGE);
				} else if (this.sortPanel.getSelectedSuperLayer().isEmpty()
						|| this.sortPanel.getSelectedSuperLayer().equalsIgnoreCase("")) {
					JFrame errorFrame = new JFrame("");
					JOptionPane.showMessageDialog(errorFrame, "Please enter a superlayer to sort",
							"Missing Superlayer Selection", JOptionPane.ERROR_MESSAGE);

				} else {
					int sectorString = Integer.parseInt(this.sortPanel.getSectorList().getSelectedItem().toString());
					int superLayerString = Integer
							.parseInt(this.sortPanel.getSuperLayerList().getSelectedItem().toString());
					this.mainFrameService.setSelectedSector(sectorString);
					this.mainFrameService.setSelectedSuperlayer(superLayerString);
					updateQuery(sectorString, superLayerString);

				}
			} else {
				JFrame errorFrame = new JFrame("");
				JOptionPane.showMessageDialog(errorFrame, "Would you like some green eggs to go with that ham?",
						"Please choose a file", JOptionPane.ERROR_MESSAGE);
				this.setVisible(false);
			}
		}
	}

	public void setReady() {
		isReady = true;
	}

	public boolean getIsReady() {
		return isReady;
	}

	protected void updateQuery(int sectorSelection, int superLayerSelection) {

		this.setVisible(false);
		this.mainFrameService.getDataPanel().compareWithSQLPanel(
				this.mainFrameService.getBySectorAndSuperLayer(sectorSelection, superLayerSelection));
		this.mainFrameService.getHistogramPanel().updateCanvas(superLayerSelection, sectorSelection);

	}

}
