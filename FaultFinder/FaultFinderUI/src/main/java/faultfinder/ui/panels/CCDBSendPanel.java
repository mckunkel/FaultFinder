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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import faultfinder.utils.NumberConstants;
import faultfinder.utils.StringConstants;

public class CCDBSendPanel extends JPanel implements ActionListener {

	final int space = NumberConstants.BORDER_SPACING;
	Border spaceBorder = null;
	Border titleBorder = null;
	JComboBox<String> ccdbComboBox = null;

	JButton removeButton = null;
	JButton sendButton = null;

	public CCDBSendPanel() {
		initializeVariables();
		initialLayout();
	}

	private void initializeVariables() {
		this.spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		this.titleBorder = BorderFactory.createTitledBorder(StringConstants.CCDBSEND_FORM_LABEL);
		this.ccdbComboBox = new JComboBox<String>();

		this.removeButton = new JButton(StringConstants.CCDBSEND_FORM_REMOVE);
		this.sendButton = new JButton(StringConstants.CCDBSEND_FORM_SEND);

		this.removeButton.addActionListener(this);
		this.sendButton.addActionListener(this);

	}

	private void initialLayout() {
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new GridLayout(0, 2));
		add(new JLabel(StringConstants.CCDBSEND_FORM_LIST));
		add(ccdbComboBox);
		add(removeButton);
		add(sendButton);

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.sendButton) {
			System.out.println("Will send the query with selected faults and wires from the ccdb panel");
		} else if (event.getSource() == this.removeButton) {

			System.out.println("This will remove from teh list");
		}

	}
}
