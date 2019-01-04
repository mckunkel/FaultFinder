package faultfinder.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import faultfinder.objects.StatusChangeDB;
import faultfinder.service.MainFrameService;

public class SQLPanelMouseListener implements MouseListener, MouseMotionListener {
	private MainFrameService mainFrameService = MainFrameServiceManager.getSession();
	private TreeSet<StatusChangeDB> queryList = new TreeSet<>();
	private JTable target;
	private int[] selection;

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		// if (e.getButton() == 1 && e.getClickCount() == 1) {
		// this.target = (JTable) e.getSource();
		// this.selection = target.getSelectedRows();
		// }
		// if (SwingUtilities.isRightMouseButton(e) && this.selection != null &&
		// this.selection.length > 0) {

		if (e.getButton() == 1 && e.getClickCount() == 1) {
			this.target = (JTable) e.getSource();
		}
		if (SwingUtilities.isRightMouseButton(e) && target.getSelectedRowCount() > 0) {
			this.selection = target.getSelectedRows();

			int reply = JOptionPane.showConfirmDialog(e.getComponent(), "Remove Selected Fault from DB list?",
					"User Selected Remove from List", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				sendProcedure();
				JOptionPane.showMessageDialog(e.getComponent(),
						"Deleting values selected by user " + this.mainFrameService.getUserName());
				reset();

			} else {
				JOptionPane.showMessageDialog(e.getComponent(), "electrons do not grow on trees");
				reset();

			}

		}

	}

	private void reset() {
		this.selection = new int[0];
		this.target = null;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void sendProcedure() {
		for (int i : selection) {
			StatusChangeDB statusChangeDB = new StatusChangeDB();
			statusChangeDB.setSector(target.getValueAt(i, 1).toString());
			statusChangeDB.setSuperlayer(target.getValueAt(i, 2).toString());
			statusChangeDB.setLoclayer(target.getValueAt(i, 3).toString());
			statusChangeDB.setLocwire(target.getValueAt(i, 4).toString());
			queryList.add(statusChangeDB);
		}
		this.mainFrameService.prepareAddBackList(queryList);
		this.mainFrameService.getSQLPanel().removeItems(queryList);
		this.mainFrameService.getDataPanel().addItems(queryList);
		this.mainFrameService.removeRowFromMYSQLQuery(queryList);
		this.mainFrameService.clearAddBackList();
	}
}
