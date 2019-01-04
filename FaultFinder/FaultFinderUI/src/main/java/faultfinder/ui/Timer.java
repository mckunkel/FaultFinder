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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;

public class Timer extends Thread {

	private boolean isRunning;
	private JLabel timeLabel;
	private SimpleDateFormat timeFormat;

	public Timer(JLabel timeLabel) {
		initializeVariables(timeLabel);
	}

	private void initializeVariables(JLabel timeLabel) {
		this.timeLabel = timeLabel;
		this.timeFormat = new SimpleDateFormat("HH:mm:ss  dd-MM-yyyy");
		this.isRunning = true;
	}

	@Override
	public void run() {
		while (isRunning) {
			Calendar calendar = Calendar.getInstance();
			Date currentTime = calendar.getTime();
			timeLabel.setText(timeFormat.format(currentTime));

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
