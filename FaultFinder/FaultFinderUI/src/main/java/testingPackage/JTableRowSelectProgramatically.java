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
package testingPackage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

public class JTableRowSelectProgramatically extends JPanel {
	public JTableRowSelectProgramatically() {
		initializePanel();
	}

	private void initializePanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(475, 150));

		final JTable table = new JTable(new MyTableModel());
		table.setFillsViewportHeight(true);
		JScrollPane pane = new JScrollPane(table);

		JLabel label1 = new JLabel("From row: ");
		final JTextField field1 = new JTextField(3);
		JLabel label2 = new JLabel("To row: ");
		final JTextField field2 = new JTextField(3);
		JButton select = new JButton("Select");
		JButton clear = new JButton("Clear");
		JButton add = new JButton("Add another one");

		//
		// Enables row selection mode and disable column selection
		// mode.
		//
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);

		//
		// Select rows based on the input.
		//
		select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int index1 = 0;
				int index2 = 0;
				try {
					index1 = Integer.valueOf(field1.getText());
					index2 = Integer.valueOf(field2.getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				if (index1 < 0 || index2 < 0 || index1 >= table.getRowCount() || index2 >= table.getRowCount()) {
					JOptionPane.showMessageDialog(table, "Selection out of range!");
				} else {
					table.setRowSelectionInterval(index1, index2);
				}
			}
		});

		//
		// Clears row selection
		//
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.removeRowSelectionInterval(0, table.getRowCount() - 1);
				field1.setText("");
				field2.setText("");
			}
		});

		//
		// Add one more row after the last selected row.
		//
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int index2 = 0;
				try {
					index2 = Integer.valueOf(field2.getText());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				index2 = index2 + 1;
				table.addRowSelectionInterval(index2, index2);
				field2.setText(String.valueOf(index2));
			}
		});

		JPanel command = new JPanel(new FlowLayout());
		command.add(label1);
		command.add(field1);
		command.add(label2);
		command.add(field2);
		command.add(select);
		command.add(clear);
		command.add(add);

		add(pane, BorderLayout.CENTER);
		add(command, BorderLayout.SOUTH);
	}

	public static void showFrame() {
		JPanel panel = new JTableRowSelectProgramatically();
		panel.setOpaque(true);

		JFrame frame = new JFrame("JTable Row Selection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JTableRowSelectProgramatically.showFrame();
			}
		});
	}

	public class MyTableModel extends AbstractTableModel {
		private String[] columns = { "ID", "NAME", "AGE", "A STUDENT?" };
		private Object[][] data = { { 1, "Alice", 20, Boolean.FALSE }, { 2, "Bob", 10, Boolean.TRUE },
				{ 3, "Carol", 15, Boolean.TRUE }, { 4, "Mallory", 25, Boolean.FALSE } };

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return columns.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		//
		// This method is used by the JTable to define the default
		// renderer or editor for each cell. For example if you have
		// a boolean data it will be rendered as a check box. A
		// number value is right aligned.
		//
		@Override
		public Class getColumnClass(int columnIndex) {
			return data[0][columnIndex].getClass();
		}
	}
}