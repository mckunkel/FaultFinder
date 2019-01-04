package faultfinder.plotfaults;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import faultfinder.service.MainFrameService;
import faultfinder.ui.panels.BottomSortPanel;
import faultfinder.ui.panels.HistogramPanel;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

public class DisplayFaults extends JFrame {

	private PlotFaults plotFaults = null;
	private MainFrameService mainFrameService = null;
	private HistogramPanel histogramPanel = null;
	private BottomSortPanel bottomSortPanel = null;
	private JLabel histLabel;
	private int runno;

	public DisplayFaults(int runno) {
		super(StringConstants.APP_NAME4DISPLAY);

		this.runno = runno;
		initializeVariables();
		constructLayout();
		constructAppWindow();
	}

	private void initializeVariables() {
		this.mainFrameService = MainFrameServiceManager.getSession();
		this.mainFrameService.setServiceProvided("Plotting");
		this.mainFrameService.setFault(0);
		this.mainFrameService.setRunNumber(this.runno);
		this.histogramPanel = new HistogramPanel();
		this.histogramPanel.removeMouseListeners();
		this.histogramPanel.setPalette("kVisibleSpectrum");
		this.bottomSortPanel = new BottomSortPanel(this);
		this.histLabel = new JLabel(StringConstants.MAIN_FORM_HIST);
		histLabel.setFont(new Font(histLabel.getFont().getName(), Font.PLAIN, 18));
		this.mainFrameService.setHistogramPanel(this.histogramPanel);
		this.plotFaults = new PlotFaults();
		runProcess();
	}

	private void runProcess() {
		plotFaults.makePlots();
		this.histogramPanel.updateCanvas(1, 1);
	}

	private JPanel histogramControlsPanel() {
		JPanel histgramControlsPanel = new JPanel();
		histgramControlsPanel.setLayout(new GridBagLayout());
		PanelConstraints.addComponent(histgramControlsPanel, histogramPanel, 0, 0, 1, 1, GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH, 300, 300);

		// add controls to histogram panel
		PanelConstraints.addComponent(histgramControlsPanel, bottomSortPanel, 0, 1, 1, 1, GridBagConstraints.PAGE_END,
				GridBagConstraints.REMAINDER, 0, 50);
		// faultPanel
		return histgramControlsPanel;

	}

	private void constructLayout() {
		JPanel layoutPanel = new JPanel();

		layoutPanel.setLayout(new GridBagLayout());
		PanelConstraints.addComponent(layoutPanel, histogramControlsPanel(), 2, 0, 1, 1, 0.25, 1,
				GridBagConstraints.FIRST_LINE_END, GridBagConstraints.BOTH, 0, 0);

		setLayout(new BorderLayout());
		add(layoutPanel, BorderLayout.CENTER);
	}

	private void constructAppWindow() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setSize(NumberConstants.APP_WINDOW_SIZE_WIDTH, NumberConstants.APP_WINDOW_SIZE_HEIGHT);

		setVisible(true);
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public static void main(String[] args) {
		new DisplayFaults(3923);
	}
}
