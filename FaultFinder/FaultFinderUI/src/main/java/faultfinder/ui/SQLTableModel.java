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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import faultfinder.objects.StatusChangeDB;
import faultfinder.utils.NumberConstants;

public class SQLTableModel extends AbstractTableModel {

	private List<StatusChangeDB> wireList;
	// private String[] colNames = { "Run Number", "Sector", "SuperLayer",
	// "Layer", "Wire", "Problem Type",
	// "Status Change" };
	private String[] colNames = { "Run", "Sector", "SuperLayer", "Layer", "Wire", "Problem" };

	public SQLTableModel() {
		this.wireList = new ArrayList<StatusChangeDB>();
	}

	public int getRowCount() {
		return this.wireList.size();
	}

	public int getColumnCount() {
		return NumberConstants.NUM_OF_COLUMNS + 2;
	}

	public String getColumnName(int column) {
		return this.colNames[column];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		// StatusChangeDB dataPoint = this.wireList.get(rowIndex);
		StatusChangeDB dataPoint = this.wireList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return dataPoint.getRunno();
		case 1:
			return dataPoint.getSector();
		case 2:
			return dataPoint.getSuperlayer();
		case 3:
			return dataPoint.getLoclayer();
		case 4:
			return dataPoint.getLocwire();
		case 5:
			return dataPoint.getProblem_type();
		case 6:
			return dataPoint.getStatus_change_type();
		default:
			return null;
		}
	}

	public void setWireSet(TreeSet<StatusChangeDB> wireDF) {
		TreeSet<StatusChangeDB> test = new TreeSet<>();
		test.addAll(wireDF);
		List<StatusChangeDB> list = new ArrayList<>(test);
		setWireList(list);
		updateTable();
	}

	public void setWireList(List<StatusChangeDB> wireList) {
		this.wireList = wireList;
	}

	public void removeRow(TreeSet<StatusChangeDB> statusChangeDBs) {
		ArrayList<StatusChangeDB> listToRemove = new ArrayList<>();
		for (StatusChangeDB ro : statusChangeDBs) {
			for (StatusChangeDB statusChangeDB : this.wireList) {
				if (statusChangeDB.getSector().equals(ro.getSector())
						&& statusChangeDB.getSuperlayer().equals(ro.getSuperlayer())
						&& statusChangeDB.getLoclayer().equals(ro.getLoclayer())
						&& statusChangeDB.getLocwire().equals(ro.getLocwire())) {
					listToRemove.add(statusChangeDB);
					// System.out.println(statusChangeDB.getSector() + " " +
					// statusChangeDB.getSuperlayer() + " "
					// + statusChangeDB.getLoclayer() + " " +
					// statusChangeDB.getLocwire());

				}
			}
		}

		this.wireList.removeAll(listToRemove);
		updateTable();
	}

	public void updateTable() {
		fireTableDataChanged();
	}

}
