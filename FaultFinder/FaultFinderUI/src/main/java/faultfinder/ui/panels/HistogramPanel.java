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
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jlab.groot.graphics.EmbeddedCanvas;

import faultfinder.service.MainFrameService;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.MouseOverrideCanvasListener;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.StringConstants;

public class HistogramPanel extends JPanel implements UpdatePanel {
	private MainFrameService mainFrameService = null;
	private final MouseOverrideCanvasListener listener;

	private EmbeddedCanvas canvas = null;
	// private Map<Coordinate, H2F> occupanciesByCoordinate = null;

	int updateTime = NumberConstants.CANVAS_UPDATE;

	final int space = NumberConstants.BORDER_SPACING;
	Border spaceBorder = null;
	Border titleBorder = null;

	public HistogramPanel() {
		listener = new MouseOverrideCanvasListener();

		initializeVariables();
		constructLayout();
	}

	private void constructLayout() {
		setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));
		setLayout(new BorderLayout());

		JPanel aTestPanel = new JPanel();
		int thisSpace = 0;
		aTestPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(thisSpace, thisSpace, thisSpace, thisSpace),
				BorderFactory.createTitledBorder("")));
		aTestPanel.setLayout(new BorderLayout());
		canvas.setAxisTitleSize(10);
		canvas.setAxisFontSize(50);
		canvas.setAxisLabelSize(10);

		aTestPanel.add(canvas, BorderLayout.CENTER);

		add(aTestPanel, BorderLayout.CENTER);

	}

	private void initializeVariables() {
		this.mainFrameService = MainFrameServiceManager.getSession();

		this.canvas = new EmbeddedCanvas();
		removeListeners();
		this.canvas.initTimer(updateTime);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		this.spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		this.titleBorder = BorderFactory.createTitledBorder(StringConstants.HISTOGRAM_FORM_LABEL);
	}

	public void updateCanvas(int superLayer, int sector) {
		this.canvas.draw(this.mainFrameService.getHistogramByMap(superLayer, sector));
		this.canvas.update();
	}

	private void removeListeners() {
		MouseListener[] mouseListener = canvas.getMouseListeners();
		for (int i = 0; i < mouseListener.length; i++) {
			MouseListener mouseListener2 = mouseListener[i];
			if (mouseListener2 instanceof MouseListener) {
				canvas.removeMouseListener(mouseListener2);
			}
		}

	}

	public void removeMouseListeners() {
		this.removeListeners();
	}

	@Override
	public void updatePanel(int superLayer, int sector) {
		this.updateCanvas(superLayer, sector);
	}

	public void setPalette(String str) {
		this.canvas.getPad().setPalette(str);
	}

}
