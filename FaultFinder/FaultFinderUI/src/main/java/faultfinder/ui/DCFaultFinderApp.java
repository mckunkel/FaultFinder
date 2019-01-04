package faultfinder.ui;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import faultfinder.utils.SystemCheck;
import spark.utils.SparkManager;

public class DCFaultFinderApp {

	public static void main(String[] args) {
		SystemCheck.whichSystem();
		if (SparkManager.sqlCorrectConfig() == 3) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN);
				Logger.getLogger("org").setLevel(Level.OFF);
				Logger.getLogger("akka").setLevel(Level.OFF);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					new MainFrame();
				}
			});
		} else if (SparkManager.sqlCorrectConfig() == 2) {

			JOptionPane.showMessageDialog(null, "Your MySQL is started, but the table name is incorrect",
					"Eggs are not supposed to be green.", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} else if (SparkManager.sqlCorrectConfig() == 1) {
			JOptionPane.showMessageDialog(null, "Your MySQL is started, but the database name is incorrect",
					"Eggs are not supposed to be green.", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} else if (SparkManager.sqlCorrectConfig() == 0) {

			JOptionPane op = new JOptionPane("null message", JOptionPane.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
			Color color = op.getBackground();
			JTextArea textarea = new JTextArea(
					"Your MySQL is not started or not configued properly. Please go to your terminal and type \n\t mysql.server start");
			textarea.setBackground(color);

			textarea.setEditable(false);
			JOptionPane.showMessageDialog(null, textarea, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} else {
			throw new IllegalAccessError("Your config is wrong, Please check the Wiki for proper configuration");
		}
	}

}