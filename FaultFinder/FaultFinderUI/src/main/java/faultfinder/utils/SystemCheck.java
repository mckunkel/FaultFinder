package faultfinder.utils;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;

public class SystemCheck {

	public static void whichSystem() {
		if (SystemUtils.IS_OS_WINDOWS) {
			JOptionPane.showMessageDialog(null, "Windows OS not supported.\nPlease see documentation", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

}
