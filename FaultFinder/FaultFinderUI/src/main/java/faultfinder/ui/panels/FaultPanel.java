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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;

import faultfinder.objects.Status_change_type;
import faultfinder.service.MainFrameService;
import faultfinder.ui.MainFrame;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class FaultPanel extends JPanel implements ActionListener {// implements
	private MainFrame mainFrame = null;
	private MainFrameService mainFrameService = null;

	final int space = NumberConstants.BORDER_SPACING;
	private Border spaceBorder = null;
	private Border titleBorder = null;
	private JLabel nameLabel;
	private JTextField nameField = null;
	private List<JRadioButton> jRadioButton = null;
	private List<JButton> jButtons = null;

	private JButton setNameButton = null;
	private ButtonGroup buttons = null;
	private JCheckBox cb1 = null;
	private JCheckBox cb2 = null;

	public FaultPanel(MainFrame parentFrame) {
		this.mainFrame = parentFrame;
		initializeVariables();
		initialLayout();
	}

	private void initializeVariables() {
		this.mainFrameService = MainFrameServiceManager.getSession();

		this.spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		this.titleBorder = BorderFactory.createTitledBorder(StringConstants.FAULT_FORM_LABEL);

		this.jRadioButton = new ArrayList<>();
		this.jButtons = new ArrayList<>();

		this.buttons = new ButtonGroup();

		// this.cb1 = new JCheckBox("broken");
		this.cb1 = new JCheckBox(Status_change_type.broke.toString());
		this.cb2 = new JCheckBox(Status_change_type.fixed.toString());
		this.nameLabel = new JLabel(StringConstants.FORM_NAME);

		this.nameField = new JTextField(NumberConstants.WINDOW_FIELD_LENGTH);

		this.cb1.addActionListener(this);
		this.cb2.addActionListener(this);

	}

	private void initialLayout() {
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new BorderLayout());

		add(makeSubmitPanel(), BorderLayout.PAGE_START);

		// add(makeSpacerPanel(), BorderLayout.CENTER);

		add(makeButtonGroup(), BorderLayout.PAGE_END);

	}

	private JPanel makeButtonGroup() {
		JPanel jPanel1 = new JPanel();

		jPanel1.setLayout(
				new GridLayout(StringConstants.PROBLEM_TYPES.length / 2, StringConstants.PROBLEM_TYPES.length / 2));
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout());

		for (int i = 1; i < StringConstants.PROBLEM_TYPES.length; i++) {
			JRadioButton button = new JRadioButton(StringConstants.PROBLEM_TYPES[i]);

			JButton aButton = new JButton("?");
			aButton.setBounds(10, 10, 30, 25);
			aButton.setBorder(new RoundedBorder(3)); // 10 is the radius
			aButton.setForeground(Color.BLUE);
			aButton.addActionListener(this);

			button.addActionListener(this);
			jPanel.add(button);
			jPanel.add(aButton);

			jPanel1.add(button);
			// jPanel1.add(jPanel);
			buttons.add(button);
			jRadioButton.add(button);
			jButtons.add(aButton);

		}
		this.jRadioButton.get(this.mainFrameService.getFaultNum()).setSelected(true);
		return jPanel1;
	}

	private JPanel makeSpacerPanel() {
		JPanel aJPanel = new JPanel();
		aJPanel.setLayout(new BorderLayout());
		aJPanel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		return aJPanel;
	}

	private JPanel makeSubmitPanel() {
		JPanel submitPanel = new JPanel();
		submitPanel.setLayout(new GridLayout(2, 1));
		JPanel namePanel = new JPanel();

		namePanel.setLayout(new GridBagLayout());

		PanelConstraints.addComponent(namePanel, nameLabel, 0, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 0);
		PanelConstraints.addComponent(namePanel, nameField, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 45, 0);
		this.setNameButton = new JButton("Set");
		this.setNameButton.setBounds(10, 10, 30, 25);
		this.setNameButton.setBorder(new RoundedBorder(3)); // 10 is the radius
		this.setNameButton.setForeground(Color.BLUE);
		this.setNameButton.addActionListener(e -> {
			if (this.nameField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Name cannot be empty", "Not in a house, not with a mouse",
						JOptionPane.ERROR_MESSAGE);
				System.out.println("Name not set correctly");
			} else {
				this.mainFrameService.setUserName(this.nameField.getText());
				this.setNameButton.setText("Reset");
				this.setNameButton.setForeground(Color.RED);
			}
		});

		PanelConstraints.addComponent(namePanel, setNameButton, 2, 0, 1, 1, GridBagConstraints.LINE_END,
				GridBagConstraints.BOTH, 0, 0);

		// submitPanel.add(checkBoxPanel());
		submitPanel.add(namePanel);
		return submitPanel;

	}

	private JPanel checkBoxPanel() {
		checkBoxGroup();
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridLayout(0, 2));
		checkBoxPanel.add(this.cb1);
		checkBoxPanel.add(this.cb2);

		return checkBoxPanel;

	}

	private ButtonGroup checkBoxGroup() {
		ButtonGroup checkBoxGroup = new ButtonGroup();
		checkBoxGroup.add(this.cb1);
		checkBoxGroup.add(this.cb2);
		this.cb1.setSelected(true);

		return checkBoxGroup;

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// I am removing the ability to set things as fixed
		// when a better idea on how to compare->seeFixed->setFixed is thought
		// of I will reinstate this functionality
		this.mainFrameService.setBrokenOrFixed(Status_change_type.broke);
		// if (cb1.isSelected()) {
		// this.mainFrameService.setBrokenOrFixed(Status_change_type.broke);
		// }
		// if (cb2.isSelected()) {
		// this.mainFrameService.setBrokenOrFixed(Status_change_type.fixed);
		// }
		if (event.getSource() == this.jRadioButton.get(0))
			this.mainFrameService.setFault(0);
		if (event.getSource() == this.jRadioButton.get(1))
			this.mainFrameService.setFault(1);
		if (event.getSource() == this.jRadioButton.get(2))
			this.mainFrameService.setFault(2);
		if (event.getSource() == this.jRadioButton.get(3))
			this.mainFrameService.setFault(3);
		if (event.getSource() == this.jRadioButton.get(4))
			this.mainFrameService.setFault(4);
		if (event.getSource() == this.jRadioButton.get(5))
			this.mainFrameService.setFault(5);

	}

	private static class RoundedBorder implements Border {

		private int radius;

		RoundedBorder(int radius) {
			this.radius = radius;
		}

		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
		}
	}
}
