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

public class CCDBPanelMouseListener implements MouseListener, MouseMotionListener {
	private MainFrameService mainFrameService = MainFrameServiceManager.getSession();
	private TreeSet<StatusChangeDB> queryList = new TreeSet<>();
	private JTable target;
	private int[] selection;

	@Override
	public void mouseDragged(MouseEvent e) {

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

		if (e.getButton() == 1 && e.getClickCount() == 1) {
			this.target = (JTable) e.getSource();
		}
		if (SwingUtilities.isRightMouseButton(e) && target.getSelectedRowCount() > 0) {

			this.selection = target.getSelectedRows();
			int reply = JOptionPane.showConfirmDialog(e.getComponent(), "Add Selected Fault to DB list?",
					"User Selected Add to List", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				sendProcedure();
				JOptionPane.showMessageDialog(e.getComponent(),
						"Entering values selected by user " + this.mainFrameService.getUserName());
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
			statusChangeDB.setSector(target.getValueAt(i, 0).toString());
			statusChangeDB.setSuperlayer(target.getValueAt(i, 1).toString());
			statusChangeDB.setLoclayer(target.getValueAt(i, 2).toString());
			statusChangeDB.setLocwire(target.getValueAt(i, 3).toString());

			statusChangeDB.setProblem_type(StringConstants.PROBLEM_TYPES[this.mainFrameService.getFaultNum() + 1]);

			statusChangeDB.setStatus_change_type(this.mainFrameService.getBrokenOrFixed().toString());
			statusChangeDB.setRunno(this.mainFrameService.getRunNumber());
			queryList.add(statusChangeDB);
			// System.out.println(target.getValueAt(i, 1).toString());

		}
		this.mainFrameService.prepareMYSQLQuery(queryList);
		this.mainFrameService.addToCompleteSQLList(queryList);
		this.mainFrameService.getDataPanel().removeItems(queryList);
		this.mainFrameService.clearTempSQLList();
		this.mainFrameService.getSQLPanel().setTableModel(this.mainFrameService.getCompleteSQLList());
	}

}
