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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class SortPanel extends JPanel {

	private JLabel sectorLabel;
	private JLabel superLayerLabel;

	private String[] numberStrings = { "1", "2", "3", "4", "5", "6" };
	private JComboBox<String> sectorList;
	private JComboBox<String> superLayerList;

	public SortPanel() {
		initializeVariables();
		constructLayout();
	}

	private void initializeVariables() {
		this.sectorLabel = new JLabel(StringConstants.SORT_FORM_SECTOR);
		this.superLayerLabel = new JLabel(StringConstants.SORT_FORM_SUPERLAYER);
		this.sectorList = new JComboBox(numberStrings);
		this.superLayerList = new JComboBox(numberStrings);
		this.sectorList.setSelectedIndex(0);
		this.superLayerList.setSelectedIndex(0);
	}

	private void constructLayout() {
		Border spaceBorder = BorderFactory.createEmptyBorder(NumberConstants.BORDER_SPACING,
				NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING);
		Border titleBorder = BorderFactory.createTitledBorder(StringConstants.SORT_FORM_SUBTITLE);
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new GridBagLayout());

		Insets leftPadding = new Insets(0, 100, 0, 0);
		Insets rightPadding = new Insets(0, 0, 0, 100);
		//
		// // ///// First row ////////////////////////////

		PanelConstraints.addComponent(this, sectorLabel, 0, 0, 1, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, leftPadding, 0, 0);
		PanelConstraints.addComponent(this, sectorList, 1, 0, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, rightPadding, 0, 0);

		// ////// Next row ////////////////////////////
		PanelConstraints.addComponent(this, superLayerLabel, 0, 1, 1, 1, GridBagConstraints.LINE_START,
				GridBagConstraints.NONE, leftPadding, 0, 0);
		PanelConstraints.addComponent(this, superLayerList, 1, 1, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.NONE, rightPadding, 0, 0);

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
}
