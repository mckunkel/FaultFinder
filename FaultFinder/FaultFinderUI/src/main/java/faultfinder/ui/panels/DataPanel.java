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
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.spark.sql.Dataset;

import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;
import faultfinder.ui.TableModel;
import faultfinder.utils.DataPanelMouseListener;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.StringConstants;

public class DataPanel extends JPanel {
	private MainFrameService mainFrameService = null;

	private JTable aTable;
	private TableModel tableModel;

	final int space = NumberConstants.BORDER_SPACING;
	Border spaceBorder = null;
	Border titleBorder = null;

	public DataPanel() {
		initializeVariables();
		initializeTableAlignment();
		initializeHeaderAlignment();
		constructLayout();
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

	private void constructLayout() {
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new BorderLayout());
		add(new JScrollPane(aTable), BorderLayout.CENTER);
		// TableConstraints.setJTableColumnsWidth(this.aTable, 200, 70, 10, 10,
		// 10);
	}

	private void initializeVariables() {
		this.mainFrameService = MainFrameServiceManager.getSession();

		this.spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		this.titleBorder = BorderFactory.createTitledBorder(StringConstants.DATA_FORM_LABEL);
		this.tableModel = new TableModel();
		this.aTable = new JTable(tableModel);

		this.aTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.aTable.setRowSelectionAllowed(true);
		this.aTable.getTableHeader().setReorderingAllowed(false);
		this.aTable.addMouseListener(new DataPanelMouseListener());
	}

	public void setTableModel(Dataset<StatusChangeDB> wireSet) {
		this.tableModel.setWireSet(wireSet);
	}

	public void updateTable() {
		this.tableModel.updateTable();
	}

	public void loadData() {
		this.aTable.removeAll();
	}

	public void removeItems(TreeSet<StatusChangeDB> statusChangeDBs) {
		this.tableModel.removeRow(statusChangeDBs);
	}

	public void addItems(TreeSet<StatusChangeDB> statusChangeDBs) {
		this.tableModel.addRow(statusChangeDBs);
	}

	public void compareWithSQLPanel(Dataset<StatusChangeDB> wireSet) {
		this.tableModel.compareWithSQLPanel(wireSet, this.mainFrameService.getCompleteSQLList());
	}
}
