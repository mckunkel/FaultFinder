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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import faultfinder.query.DBQuery;
import faultfinder.service.InsertMYSqlQuery;
import faultfinder.service.InsertMYSqlServiceManager;
import faultfinder.service.MainFrameService;
import faultfinder.ui.MainFrame;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;
import spark.utils.SparkManager;

public class DBSendPanel extends JPanel implements ActionListener {
	private InsertMYSqlQuery insertMYSqlQuery = null;
	private MainFrameService mainFrameService = null;
	private DBQuery dbQuery = null;
	final int space = NumberConstants.BORDER_SPACING;
	private Border spaceBorder = null;
	private Border titleBorder = null;
	private JButton sendButton = null;
	private JButton ccDBButton = null;
	private JFrame errorFrame;

	private MainFrame parentFrame = null;
	private boolean processJlab = false;

	public DBSendPanel(MainFrame parentFrame) {
		this.parentFrame = parentFrame;

		initializeVariables();
		initialLayout();

	}

	private void initializeVariables() {
		this.insertMYSqlQuery = InsertMYSqlServiceManager.getSession();
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.dbQuery = new DBQuery();

		this.spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		this.titleBorder = BorderFactory.createTitledBorder(StringConstants.DBSEND_FORM_LABEL);
		this.sendButton = new JButton(StringConstants.DBSEND_FORM_SEND);
		this.ccDBButton = new JButton(StringConstants.CCDBSEND_FORM_SEND);
		this.sendButton.addActionListener(this);
		this.ccDBButton.addActionListener(this);

		this.errorFrame = new JFrame("");

	}

	private void initialLayout() {
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new GridBagLayout());
		PanelConstraints.addComponent(this, sendButton, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				200, 0);
		PanelConstraints.addComponent(this, ccDBButton, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				200, 0);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.sendButton) {
			System.out.println("Will send the query with selected faults and wires from the sql panel");
			if (this.mainFrameService.getCompleteSQLList() == null
					|| this.mainFrameService.getCompleteSQLList().size() == 0) {
				JOptionPane.showMessageDialog(errorFrame, "Nothing processed for MySQL!",
						"Eggs are not supposed to be green.", JOptionPane.ERROR_MESSAGE);
			} else {
				this.insertMYSqlQuery.prepareMYSQLQuery();
				this.mainFrameService.getCompleteSQLList().clear();
				this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());
				this.mainFrameService.setSentTodb(true);
				SparkManager.restart();
				this.mainFrameService.addRunToList(this.mainFrameService.getRunNumber());
			}

		}
		if (event.getSource() == this.ccDBButton) {
			this.mainFrameService.setWantsToExecute(false);

			Object[] choices = { "Cancel", "Manual", "Automatic" };
			int n = JOptionPane.showOptionDialog(errorFrame,
					"Manual or Automatic insertation into the CCDB? \n\tManual: will create scripts for user. \n\tUser will follow standard Jlab protocal to insert values to CCDB \n\n\tAutomatic: assumes user in on Jlab cluster and has permissions to send to CCDB",
					"I do like them Sam-I-Am", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					this.mainFrameService.getClasIcon(), choices, SparkManager.onJlab() ? choices[2] : choices[1]);// showConfirmDialog
			if (n == 1) {// Manual
				runProcess();
			}
			if (n == 2) {// Automatic
				if (SparkManager.onJlab()) {
					this.mainFrameService.setWantsToExecute(true);
					runProcess();
				} else {
					JOptionPane.showMessageDialog(errorFrame, "Your host appears not to be Jlab.",
							"Eggs are not supposed to be green.", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void runProcess() {
		checkVariation();
		if (this.mainFrameService.getRunList() == null || this.mainFrameService.getRunList().size() == 0) {
			showNoDataChoice();
		} else {
			this.mainFrameService.runWriteProcess();
		}
	}

	private void checkVariation() {
		String variation = JOptionPane.showInputDialog(errorFrame,
				"Variation? Leave blank if submitting to default variation");
		this.mainFrameService.setVariation(variation);
	}

	private void showNoDataChoice() {
		int n = JOptionPane.showConfirmDialog(errorFrame,
				"No data has been sent to the MySQL. \nDo you want to create CCDB entries from past MySQL entries?",
				"Eggs are not supposed to be green.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				this.mainFrameService.getClasIcon());
		if (n == 0) {
			if (this.dbQuery.getAllRuns().size() != 0) {
				TestTable testTable = new TestTable(this.parentFrame, this.dbQuery);
				testTable.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(errorFrame, "No past MySQL entries.",
						"Eggs are not supposed to be green.", JOptionPane.ERROR_MESSAGE);
			}

		}
	}
}
