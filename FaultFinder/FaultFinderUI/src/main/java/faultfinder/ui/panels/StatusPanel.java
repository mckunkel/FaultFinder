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

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import faultfinder.ui.Timer;
import faultfinder.utils.StringConstants;

public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel statusLabel;
	private JLabel timeLabel;

	private Timer timer;

	public StatusPanel() {
		initializeVariables();
		constructLayout();
		startTimer();
	}

	private void constructLayout() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(statusLabel);// , FlowLayout.CENTER
		add(timeLabel);
	}

	public void startTimer() {
		this.timer.start();
	}

	public void stopTimer() {
		this.timer.setRunning(false);
	}
	// public void stopTimer() {
	// this.timer.stop();
	// }

	private void initializeVariables() {
		this.statusLabel = new JLabel();
		this.timeLabel = new JLabel();
		this.statusLabel.setText(StringConstants.STATUS_PANEL_TEXT);
		this.timer = new Timer(timeLabel);
	}

}
