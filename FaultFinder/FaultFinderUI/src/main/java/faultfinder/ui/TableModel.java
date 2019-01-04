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

import org.apache.spark.sql.Dataset;

import faultfinder.objects.StatusChangeDB;
import faultfinder.utils.NumberConstants;

public class TableModel extends AbstractTableModel {

	private List<StatusChangeDB> wireList;
	private String[] colNames = { "Sector", "SuperLayer", "Layer", "Wire" };

	public TableModel() {
		this.wireList = new ArrayList<StatusChangeDB>();
	}

	public int getRowCount() {
		return this.wireList.size();
	}

	public int getColumnCount() {
		return NumberConstants.NUM_OF_COLUMNS;
	}

	public String getColumnName(int column) {
		return this.colNames[column];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		StatusChangeDB dataPoint = this.wireList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return dataPoint.getSector();
		case 1:
			return dataPoint.getSuperlayer();
		case 2:
			return dataPoint.getLoclayer();
		case 3:
			return dataPoint.getLocwire();
		default:
			return null;
		}
	}

	public void setWireSet(Dataset<StatusChangeDB> wireDF) {
		setWireList(wireDF.collectAsList());
		updateTable();
	}

	public void setWireList(List<StatusChangeDB> wireList) {
		// with tree set it should be only unique values
		TreeSet<StatusChangeDB> testSet = new TreeSet<>();
		testSet.addAll(wireList);
		this.wireList.clear();
		this.wireList.addAll(testSet);
		// printTable(this.wireList);

	}

	public List<StatusChangeDB> getWireList() {
		return this.wireList;
	}

	public void compareWithSQLPanel(Dataset<StatusChangeDB> wireDF, TreeSet<StatusChangeDB> sqlList) {
		List<StatusChangeDB> wireList = new ArrayList<StatusChangeDB>();
		wireList.addAll(wireDF.collectAsList());
		ArrayList<StatusChangeDB> listToRemove = new ArrayList<>();

		for (StatusChangeDB ro : sqlList) {
			for (StatusChangeDB statusChangeDB : wireList) {

				if (statusChangeDB.getSector().equals(ro.getSector())
						&& statusChangeDB.getSuperlayer().equals(ro.getSuperlayer())
						&& statusChangeDB.getLoclayer().equals(ro.getLoclayer())
						&& statusChangeDB.getLocwire().equals(ro.getLocwire())) {
					listToRemove.add(statusChangeDB);
				}
			}
		}
		// printTable(wireList);
		wireList.removeAll(listToRemove);
		setWireList(wireList);
		updateTable();

	}

	public void removeRow(TreeSet<StatusChangeDB> statusChangeDBs) {
		ArrayList<StatusChangeDB> listToRemove = new ArrayList<>();

		for (StatusChangeDB ro : statusChangeDBs) {
			for (StatusChangeDB statusChangeDB : wireList) {
				if (statusChangeDB.getSector().equals(ro.getSector())
						&& statusChangeDB.getSuperlayer().equals(ro.getSuperlayer())
						&& statusChangeDB.getLoclayer().equals(ro.getLoclayer())
						&& statusChangeDB.getLocwire().equals(ro.getLocwire())) {
					listToRemove.add(statusChangeDB);
				}
			}
		}
		this.wireList.removeAll(listToRemove);
		updateTable();
	}

	public void addRow(TreeSet<StatusChangeDB> statusChangeDBs) {
		// with test set, value should be unique
		TreeSet<StatusChangeDB> testSet = new TreeSet<>();
		testSet.addAll(this.wireList);
		testSet.addAll(statusChangeDBs);
		this.wireList.clear();
		this.wireList.addAll(testSet);
		updateTable();

	}

	private void printTable(List<StatusChangeDB> wireList) {

		for (StatusChangeDB statusChangeDB : wireList) {
			System.out.println(statusChangeDB.getSector() + " " + statusChangeDB.getSuperlayer() + " "
					+ statusChangeDB.getLoclayer() + " " + statusChangeDB.getLocwire());
		}
	}

	public void updateTable() {
		fireTableDataChanged();
	}

}
