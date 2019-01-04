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
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import faultfinder.objects.StatusChangeDB;
import faultfinder.ui.TableModel;

public class TablePanel extends JPanel {

	private JTable aTable;
	private TableModel tableModel;

	public TablePanel() {
		initializeVariables();
		initialLayout();
		initializeTableAlignment();
		initializeHeaderAlignment();
	}

	private void initializeTableAlignment() {

		DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
		tableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

		this.aTable.getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
		this.aTable.getColumnModel().getColumn(1).setCellRenderer(tableCellRenderer);
		this.aTable.getColumnModel().getColumn(2).setCellRenderer(tableCellRenderer);
		this.aTable.getColumnModel().getColumn(3).setCellRenderer(tableCellRenderer);

	}

	private void initializeHeaderAlignment() {
		DefaultTableCellRenderer headerCellRenderer = new DefaultTableCellRenderer();
		headerCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		this.aTable.getTableHeader().setDefaultRenderer(headerCellRenderer);
	}

	private void initialLayout() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 30, 10, 30));
		add(new JScrollPane(aTable), BorderLayout.CENTER);
	}

	private void initializeVariables() {
		this.tableModel = new TableModel();
		this.aTable = new JTable(tableModel);
	}

	public void setTableModel(List<StatusChangeDB> wireList) {
		this.tableModel.setWireList(wireList);
	}

	public void updateTable() {
		this.tableModel.updateTable();
	}
}
