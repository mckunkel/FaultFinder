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

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import faultfinder.faults.ChannelBundles;
import faultfinder.faults.FuseBundles;
import faultfinder.faults.PinBundles;
import faultfinder.faults.SignalConnectors;
import faultfinder.objects.Status_change_type;
import faultfinder.process.DataProcess;
import faultfinder.service.MainFrameService;
import faultfinder.ui.panels.BottomComparePanel;
import faultfinder.ui.panels.BottomSortPanel;
import faultfinder.ui.panels.CCDBSendPanel;
import faultfinder.ui.panels.DBSendPanel;
import faultfinder.ui.panels.DataPanel;
import faultfinder.ui.panels.FaultPanel;
import faultfinder.ui.panels.HistogramPanel;
import faultfinder.ui.panels.SQLPanel;
import faultfinder.ui.panels.StatusPanel;
import faultfinder.utils.MainFrameServiceManager;
import faultfinder.utils.NumberConstants;
import faultfinder.utils.PanelConstraints;
import faultfinder.utils.StringConstants;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private DataProcess dataProcess = null;
	private MainFrameService mainFrameService = null;

	private SQLPanel sqlPanel;
	private DataPanel dataPanel;
	private HistogramPanel histogramPanel;
	private BottomSortPanel bottomSortPanel;
	private BottomComparePanel bottomComparePanel;

	private JLabel dataLabel;
	private JLabel sqlLabel;
	private JLabel histLabel;

	private StatusPanel statusPanel;

	private RunForm runForm;
	private SortQueryForm sortQueryForm;
	private CompareQueryForm compareRunForm;
	private FaultPanel faultPanel;
	private DBSendPanel dbSendPanel;
	private CCDBSendPanel ccdbSendPanel;

	private JMenuBar menuBar = null;
	private JMenu jMenu = null;

	private FileDialog fileDialog = null;

	private JFileChooser fc = null;
	private File[] fileList = null;
	private ArrayList<String> fileArray = null;

	public MainFrame() {
		super(StringConstants.APP_NAME);
		setJMenuBar(createFrameMenu());

		initializeVariables();
		constructLayout();
		createFileChooser();
		constructAppWindow();

	}

	private JMenuBar createFrameMenu() {
		menuBar = new JMenuBar();

		menuBar.add(createFileMenu());
		// menuBar.add(createOptionsMenu());
		menuBar.add(createRunMenu());

		menuBar.add(createSortMenu());
		// menuBar.add(createTestMenu());
		// menuBar.add(createCompareMenu());

		return menuBar;

	}

	private void initializeVariables() {

		this.dataProcess = new DataProcess();
		this.mainFrameService = MainFrameServiceManager.getSession();

		this.mainFrameService.setBrokenOrFixed(Status_change_type.broke);
		this.mainFrameService.setFault(this.mainFrameService.getFaultNum());

		this.sqlPanel = new SQLPanel();
		this.dataPanel = new DataPanel();
		this.histogramPanel = new HistogramPanel();
		this.bottomSortPanel = new BottomSortPanel(this);
		this.bottomComparePanel = new BottomComparePanel(this);
		this.dataLabel = new JLabel(StringConstants.MAIN_FORM_DATA);
		dataLabel.setFont(new Font(dataLabel.getFont().getName(), Font.PLAIN, 18));
		this.sqlLabel = new JLabel(StringConstants.MAIN_FORM_SQL);
		sqlLabel.setFont(new Font(dataLabel.getFont().getName(), Font.PLAIN, 18));
		this.histLabel = new JLabel(StringConstants.MAIN_FORM_HIST);
		histLabel.setFont(new Font(dataLabel.getFont().getName(), Font.PLAIN, 18));

		this.statusPanel = new StatusPanel();

		this.runForm = new RunForm(this);
		this.sortQueryForm = new SortQueryForm(this);
		this.compareRunForm = new CompareQueryForm(this);
		this.faultPanel = new FaultPanel(this);
		this.dbSendPanel = new DBSendPanel(this);
		this.ccdbSendPanel = new CCDBSendPanel();

		PinBundles.setupBundles();
		SignalConnectors.setupBundles();
		FuseBundles.setupBundles();
		ChannelBundles.setupBundles();

		this.mainFrameService.setDataPanel(this.dataPanel);
		this.mainFrameService.setSQLPanel(this.sqlPanel);
		this.mainFrameService.setDataProcess(this.dataProcess);
		this.mainFrameService.setHistogramPanel(this.histogramPanel);

	}

	private JMenu createFileMenu() {
		jMenu = new JMenu(StringConstants.MAIN_MENU_FILE);
		JMenuItem openItem = new JMenuItem(StringConstants.MAIN_MENU_OPEN);
		JMenuItem exitItem = new JMenuItem(StringConstants.MAIN_MENU_EXIT);

		jMenu.add(openItem);
		jMenu.add(exitItem);

		openItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runForm.clearLists();
				HipoFilter filter = new HipoFilter();
				fileDialog.setFilenameFilter(filter);
				fileDialog.setVisible(true);
				fileList = fileDialog.getFiles();
				String dir = fileDialog.getDirectory();
				fileArray = new ArrayList<String>();
				for (File file : fileList) {
					fileArray.add(file.toString());
				}
				runForm.setDirectory(dir);
				runForm.setFileList(fileArray);
				compareRunForm.setReady();
				sortQueryForm.setReady();

			}
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// runForm.clearLists();
			// HipoFilter filter = new HipoFilter();
			// fc.addChoosableFileFilter(new FileNameExtensionFilter("Hipo
			// Files", "hipo"));
			// fc.showOpenDialog(fc);
			// fileList = fc.getSelectedFiles();
			// String dir = fc.getCurrentDirectory().getAbsolutePath();//
			// fileDialog.getDirectory();
			// fileArray = new ArrayList<String>();
			// for (File file : fileList) {
			// fileArray.add(file.toString());
			// }
			// runForm.setDirectory(dir);
			// runForm.setFileList(fileArray);
			// compareRunForm.setReady();
			// sortQueryForm.setReady();
			//
			// }
		});
		exitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int action = JOptionPane.showConfirmDialog(MainFrame.this, StringConstants.MAIN_MENU_EXIT_TEXT,
						StringConstants.MAIN_MENU_EXIT_TITLE, JOptionPane.OK_CANCEL_OPTION);

				if (action == JOptionPane.OK_OPTION) {
					mainFrameService.shutdown();
					statusPanel.stopTimer();
					System.gc();
					System.exit(0);
				}
			}
		});

		return jMenu;
	}

	private JMenu createOptionsMenu() {
		jMenu = new JMenu(StringConstants.OPTIONS_FORM_TITLE);
		JMenuItem runList = new JMenuItem(StringConstants.FORM_RUN);
		jMenu.add(runList);
		JMenuItem sortList = new JMenuItem(StringConstants.SORT_FORM_TITLE);
		jMenu.add(sortList);

		JMenuItem compareList = new JMenuItem(StringConstants.COMPARE_FORM_COMPARE);
		jMenu.add(compareList);

		runList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				runForm.setVisible(true);
			}
		});
		sortList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sortQueryForm.resetList();
				sortQueryForm.setVisible(true);
			}
		});

		compareList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				compareRunForm.loadData();
				compareRunForm.setVisible(true);
			}
		});
		return jMenu;
	}

	private JMenu createRunMenu() {
		jMenu = new JMenu(StringConstants.RUN_FORM_TITLE);
		JMenuItem sortList = new JMenuItem(StringConstants.FORM_RUN);
		jMenu.add(sortList);

		sortList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				runForm.setVisible(true);
			}
		});
		return jMenu;
	}

	private JMenu createSortMenu() {

		jMenu = new JMenu(StringConstants.MAIN_MENU_SORT);
		JMenuItem sortList = new JMenuItem(StringConstants.SORT_FORM_TITLE);
		jMenu.add(sortList);

		sortList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sortQueryForm.resetList();
				sortQueryForm.setVisible(true);
			}
		});
		return jMenu;
	}

	private JMenu createCompareMenu() {

		jMenu = new JMenu(StringConstants.MAIN_MENU_COMPARE);
		JMenuItem sortList = new JMenuItem(StringConstants.COMPARE_FORM_COMPARE);
		jMenu.add(sortList);

		sortList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				compareRunForm.loadData();
				compareRunForm.setVisible(true);
			}
		});
		return jMenu;
	}

	private void createFileChooser() {
		this.fileDialog = new FileDialog(this, "FileDialog", FileDialog.LOAD);
		fileDialog.setMultipleMode(true);

		this.fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
	}

	private JPanel dataControlsPanel() {
		JPanel dataControlsPanel = new JPanel(new GridBagLayout());
		PanelConstraints.addComponent(dataControlsPanel, dataPanel, 0, 0, 1, 1, GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH, 200, 390);
		// I am removing the ability to compare, until a better AI feature is
		// thought of

		// Border spaceBorder =
		// BorderFactory.createEmptyBorder(NumberConstants.BORDER_SPACING,
		// NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING,
		// NumberConstants.BORDER_SPACING);
		// Border titleBorder =
		// BorderFactory.createTitledBorder(StringConstants.SORTCOMPARE_FORM_SUBTITLE);
		// JTabbedPane aJTabbedPane = new JTabbedPane();
		// aJTabbedPane.setBorder(BorderFactory.createCompoundBorder(spaceBorder,
		// titleBorder));
		//
		// aJTabbedPane.addTab("Sort", null, this.bottomSortPanel, "panel for
		// sorting");
		// aJTabbedPane.addTab("Compare", null, this.bottomComparePanel, "panel
		// for comparing");

		// PanelConstraints.addComponent(dataControlsPanel, aJTabbedPane, 0, 1,
		// 1, 1, GridBagConstraints.PAGE_END,
		// GridBagConstraints.REMAINDER, 0, 30);

		Border spaceBorder = BorderFactory.createEmptyBorder(NumberConstants.BORDER_SPACING,
				NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING, NumberConstants.BORDER_SPACING);
		Border titleBorder = BorderFactory.createTitledBorder(StringConstants.SORT_FORM_SUBTITLE);
		this.bottomSortPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));

		PanelConstraints.addComponent(dataControlsPanel, this.bottomSortPanel, 0, 1, 1, 1, GridBagConstraints.PAGE_END,
				GridBagConstraints.REMAINDER, 0, 70);

		return dataControlsPanel;
	}

	private JPanel sqlControlsPanel() {
		JPanel sqlControlsPanel = new JPanel(new GridBagLayout());
		PanelConstraints.addComponent(sqlControlsPanel, sqlPanel, 0, 0, 1, 1, GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH, 0, 325);

		// PanelConstraints.addComponent(sqlControlsPanel, ccdbSendPanel, 0, 1,
		// 1, 1, GridBagConstraints.PAGE_END,
		// GridBagConstraints.REMAINDER, 0, 0);

		PanelConstraints.addComponent(sqlControlsPanel, dbSendPanel, 0, 1, 1, 1, GridBagConstraints.PAGE_END,
				GridBagConstraints.REMAINDER, 0, 0);
		return sqlControlsPanel;
	}

	private JPanel histogramControlsPanel() {
		JPanel histgramControlsPanel = new JPanel();
		histgramControlsPanel.setLayout(new GridBagLayout());
		PanelConstraints.addComponent(histgramControlsPanel, histogramPanel, 0, 0, 1, 1, GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH, 600, 405);

		// add controls to histogram panel
		PanelConstraints.addComponent(histgramControlsPanel, faultPanel, 0, 1, 1, 1, GridBagConstraints.PAGE_END,
				GridBagConstraints.REMAINDER, 0, 0);
		// faultPanel
		return histgramControlsPanel;

	}

	private void constructLayout() {
		JPanel layoutPanel = new JPanel();
		// layoutPanel.setLayout(new GridLayout(0, 3));
		// layoutPanel.add(dataControlsPanel());
		// layoutPanel.add(sqlControlsPanel());
		// layoutPanel.add(histogramControlsPanel());

		layoutPanel.setLayout(new GridBagLayout());
		PanelConstraints.addComponent(layoutPanel, dataControlsPanel(), 0, 0, 1, 1, 0.25, 1,
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0);
		PanelConstraints.addComponent(layoutPanel, sqlControlsPanel(), 1, 0, 1, 1, 0.5, 1,
				GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, 250, 0);
		PanelConstraints.addComponent(layoutPanel, histogramControlsPanel(), 2, 0, 1, 1, 0.25, 1,
				GridBagConstraints.FIRST_LINE_END, GridBagConstraints.BOTH, 0, 0);

		setLayout(new BorderLayout());
		add(layoutPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.PAGE_END);
	}

	class HipoFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".hipo"));
		}
	}

	private void constructAppWindow() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setSize(NumberConstants.APP_WINDOW_SIZE_WIDTH, NumberConstants.APP_WINDOW_SIZE_HEIGHT);

		setVisible(true);
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
}
