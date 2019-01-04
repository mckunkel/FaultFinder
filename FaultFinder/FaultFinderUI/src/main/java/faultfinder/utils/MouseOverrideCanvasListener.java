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
package faultfinder.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.tuple.Pair;
import org.jlab.groot.data.H2F;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.graphics.EmbeddedCanvas;

import faultfinder.service.MainFrameService;

public class MouseOverrideCanvasListener implements ActionListener, MouseListener, MouseMotionListener {
	private EmbeddedCanvas canvas;
	private H2F mouseH2F;
	private IDataSet ds;

	private double xMax;
	private double xMin;
	private double yMax;
	private double yMin;
	private double yRange;
	private double xSpan;
	private double ySpan;

	private int xBins;
	private int yBins;
	private MainFrameService mainFrameService = null;
	private Pair<Integer, Integer> aPair;
	private int currentBundle = -1000;

	private int popupPad = 0;
	private JPopupMenu popup = null;

	public MouseOverrideCanvasListener() {
		this.mainFrameService = MainFrameServiceManager.getSession();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			// System.out.println("I was clicked");
			// double xposNew = e.getX() - xMin;
			// double yposNew = yRange - e.getY();
			// findBin(xposNew, yposNew);
		}
		if (e.getButton() == 1 && e.getClickCount() == 2 && this.mainFrameService.getMouseReady()) {

			int reply = JOptionPane.showConfirmDialog(e.getComponent(), "Add Selected Fault to DB list?",
					"User Selected Add to List", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				this.mainFrameService.getFault().setFaultToDB();
				JOptionPane.showMessageDialog(e.getComponent(),
						"Entering values selected by user " + this.mainFrameService.getUserName());
			} else {
				JOptionPane.showMessageDialog(e.getComponent(), "electrons do not grow on trees");
			}
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			popupPad = canvas.getPadByXY(e.getX(), e.getY());
			// System.out.println("POP-UP coordinates = " + e.getX() + " " +
			// e.getY() + " pad = " + popupPad);
			createPopupMenu();
			popup.show(canvas, e.getX(), e.getY());
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println(canvas.getCanvasPads().size() + " canvas pads");
		if (this.mainFrameService.getMouseReady()) {

			canvas = (EmbeddedCanvas) e.getComponent();

			ds = canvas.getPad(0).getDatasetPlotters().get(0).getDataSet();

			xBins = ds.getDataSize(0);
			yBins = ds.getDataSize(1);

			xMax = canvas.getPad().getAxisFrame().getAxisX().getDimension().getMax();
			xMin = canvas.getPad().getAxisFrame().getAxisX().getDimension().getMin();
			yMax = canvas.getPad().getAxisFrame().getAxisY().getDimension().getMax();
			yMin = canvas.getPad().getAxisFrame().getAxisY().getDimension().getMin();
			yRange = yMax + yMin;
			xSpan = xMax - xMin;
			ySpan = yMin - yMax;

			double xMinx = this.mainFrameService.getHistogramByMap(this.mainFrameService.getSelectedSuperlayer(),
					this.mainFrameService.getSelectedSector()).getXAxis().min();
			// int xMaxx = 113;
			double xMaxx = this.mainFrameService.getHistogramByMap(this.mainFrameService.getSelectedSuperlayer(),
					this.mainFrameService.getSelectedSector()).getXAxis().max();
			int yMiny = 1;
			int yMaxy = 7;

			mouseH2F = new H2F("", xBins, xMinx, xMaxx, yBins, yMiny, yMaxy);
			double xpos = e.getX() - xMin;
			double ypos = yRange - e.getY();
			// Pair<Integer, Integer> aPair = getBinFromMouse(xpos, ypos);
			this.aPair = getBinFromMouse(xpos, ypos);

			setBundle(aPair.getLeft(), aPair.getRight());

		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	// private void findBin(double xpos, double ypos) {
	// if (inBounds(xpos, ypos)) {
	// Pair<Integer, Integer> bins = getBinFromMouse(xpos, ypos);
	// System.out.println(" xBin = " + bins.getLeft() + " yBin = " +
	// bins.getRight());
	// } else {
	// System.out.println("not in range. will do nothing");
	// }
	// }

	private Pair<Integer, Integer> getBinFromMouse(double xposNew, double yposNew) {
		int xBin = -100;
		int yBin = -100;

		double xmouseplace = xSpan / xBins;
		double ymouseplace = ySpan / yBins;

		for (int i = 0; i < xBins; i++) {
			double divisions = (double) (i + 1) * xmouseplace;
			if (Math.abs(xposNew - divisions) <= xmouseplace) {
				xBin = i;
			}
		}
		for (int i = 0; i < yBins; i++) {
			double divisions = (double) (i + 1) * ymouseplace;
			if (Math.abs(yposNew - divisions) <= ymouseplace) {
				yBin = i;
			}
		}
		Pair<Integer, Integer> retValue = Pair.of(xBin + 1, yBin + 1);

		return retValue;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (this.mainFrameService.getMouseReady()) {
			double xpos = e.getX() - xMin;
			double ypos = yRange - e.getY();

			// Pair<Integer, Integer> aPair = getBinFromMouse(xpos, ypos);
			this.aPair = getBinFromMouse(xpos, ypos);

			setBundle(aPair.getLeft(), aPair.getRight());

			if (inBounds(xpos, ypos) && bundleChange()) {// && bundleChange()
				this.mainFrameService.getFault().drawLogic(canvas, mouseH2F, aPair.getLeft(), aPair.getRight());
			}
		}

	}

	private void setBundle(int xBin, int yBin) {
		this.currentBundle = this.mainFrameService.getFault().setBundle(xBin, yBin);
	}

	private boolean inBounds(double xpos, double ypos) {
		boolean retValue = false;
		if (xpos >= 0.0 && ypos >= 0.0 && xpos <= xSpan && ypos <= ySpan) {
			retValue = true;
		}
		return retValue;

	}

	private boolean bundleChange() {
		boolean retValue = false;
		if (this.mainFrameService.getBundle() == this.currentBundle) {
			retValue = false;
		} else {
			this.mainFrameService.setBundle(this.currentBundle);
			retValue = true;
		}
		return retValue;
	}

	private void createPopupMenu() {
		this.popup = new JPopupMenu();

		JMenuItem itemSave = new JMenuItem("Save");
		JMenuItem itemSaveAs = new JMenuItem("Save As...");

		itemSave.addActionListener(this);
		itemSaveAs.addActionListener(this);

		this.popup.add(itemSave);
		this.popup.add(new JSeparator());
		this.popup.add(itemSaveAs);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Save")) {
			System.out.println("Still have to implement \"Save\" action");
		}
		if (e.getActionCommand().equals("Save As...")) {
			System.out.println("Still have to implement \"Save As...\" action");

		}
	}
}
