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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class MyTable extends JPanel implements ActionListener {
	// private DBQuery dbQuery = new DBQuery();
	private JButton scriptButton;
	private JButton executeButton;
	private JButton cancelButton;

	private JPanel panel = null;

	private JTable jTable = null;

	private String[] columnNames = { "Runs" };
	private DataModel dataModel = null;
	private DefaultListSelectionModel selectionModel = null;

	public MyTable() {
		initializeVariables();
	}

	private void initializeVariables() {

		this.dataModel = new DataModel(makeData(), columnNames);
		// this.jTable = createTable();
		makeTable();

		this.scriptButton = new JButton(StringConstants.SEND_SCRIPT);
		this.scriptButton.addActionListener(this);
		this.scriptButton.setToolTipText(
				"Used to create a script that the user\nwill use to manully tranposrt and\nexecute on the Jlab cluster");
		this.executeButton = new JButton(StringConstants.EXECUTE_SCRIPT);
		this.executeButton.addActionListener(this);
		this.executeButton.setToolTipText(
				"Used to have this application send to CCDB\nAssumes that user is running on\nJlab cluster and has CCDB permissions.");
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);

		constructLayout();
		setWindow();
		// pack();

	}

	private void setWindow() {
		setSize(NumberConstants.SORT_FORM_WINDOW_SIZE_WIDTH, NumberConstants.SORT_FORM_WINDOW_SIZE_HEIGHT);
		// setLocationRelativeTo(parentFrame);
	}

	private void constructLayout() {

		JPanel fileInfoPanel = new JPanel();
		Border spaceBorder = BorderFactory.createEmptyBorder(NumberConstants.BORDER_SPACING,
				NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING);
		Border titleBorder = BorderFactory.createTitledBorder(StringConstants.FILE_FORM_SELECT);
		fileInfoPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		fileInfoPanel.setLayout(new GridBagLayout());

		Insets leftPadding = new Insets(0, 0, 0, 0);

		PanelConstraints.addComponent(fileInfoPanel, new JScrollPane(createTable()), 0, 0, 1, 1,
				GridBagConstraints.LINE_START, GridBagConstraints.BOTH, leftPadding, 0, 0);
		// ////////// Buttons Panel ///////////////
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		Dimension btnSize = scriptButton.getPreferredSize();
		scriptButton.setPreferredSize(btnSize);
		buttonsPanel.add(scriptButton);
		buttonsPanel.add(executeButton);
		buttonsPanel.add(cancelButton);

		setLayout(new GridBagLayout());// BorderLayout
		PanelConstraints.addComponent(this, fileInfoPanel, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 100);
		PanelConstraints.addComponent(this, buttonsPanel, 1, 1, 1, 1, GridBagConstraints.LAST_LINE_END,
				GridBagConstraints.BOTH, 0, 0);
	}

	private void makeTable() {

		this.jTable = new JTable(this.dataModel);
		this.jTable.setAutoCreateRowSorter(true);
		this.jTable.setRowSelectionAllowed(true);
		this.jTable.setColumnSelectionAllowed(false);
		this.jTable.setCellSelectionEnabled(true);
		this.jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.selectionModel = (DefaultListSelectionModel) jTable.getSelectionModel();
	}

	private JTable createTable() {
		// List<String> runList = dbQuery.getAllRuns();
		// Object[][] data = new Object[runList.size()][runList.size()];
		// for (int i = 0; i < runList.size(); i++) {
		// for (int j = 0; j < runList.size(); j++) {
		// data[i][j] = runList.get(i);
		// }
		// }
		// Object[][] data = { { "3105" }, { "3222" } };
		String[] columnNames = { "Runs" };

		Object[][] data = new Object[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				data[i][j] = i;
			}
		}
		DefaultTableModel model = new DefaultTableModel(data, columnNames) {

			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		return table;
	}

	private Object[][] makeData() {
		// List<String> runList = dbQuery.getAllRuns();
		// Object[][] data = new Object[runList.size()][runList.size()];
		// for (int i = 0; i < runList.size(); i++) {
		// for (int j = 0; j < runList.size(); j++) {
		// data[i][j] = runList.get(i);
		// }
		// }
		// Object[][] data = { { "3105" }, { "3222" } };

		Object[][] data = new Object[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				data[i][j] = i;
			}
		}
		return data;
	}

	private class DataModel extends DefaultTableModel {

		public DataModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return getValueAt(0, column).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame("My Table");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(new MyTable());
				frame.setSize(new Dimension(400, 230));
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.cancelButton) {
			setVisible(false);
		} else if (e.getSource() == this.scriptButton) {
			TableModel model = jTable.getModel();
			for (int i = 0; i < model.getRowCount(); i++) {
				System.out.println(selectionModel.isSelectedIndex(i));
				if (selectionModel.isSelectedIndex(i)) {
					System.out.println(model.getValueAt(i, 0));
				}
			}
			System.out.println("HERE!!" + model.getRowCount());
			for (int o : jTable.getSelectedRows()) {
				System.out.println("this is sparta + " + jTable.getValueAt(o, 0));
			}
		}
	}
}
