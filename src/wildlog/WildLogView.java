package wildlog;

import wildlog.ui.dialogs.WildLogAboutBox;
import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.wrappers.SightingWrapper;
import wildlog.html.utils.UtilsHTML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.WildLogFileType;
import wildlog.mapping.kml.utils.UtilsKML;
import wildlog.ui.panels.PanelElement;
import wildlog.ui.panels.PanelLocation;
import wildlog.ui.dialogs.MergeElementsDialog;
import wildlog.ui.dialogs.MoveVisitDialog;
import wildlog.ui.panels.PanelSighting;
import wildlog.ui.panels.PanelVisit;
import wildlog.ui.dialogs.ReportingDialog;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.astro.AstroCalculator;
import wildlog.utils.WildLogPaths;
import wildlog.mapping.utils.UtilsGps;
import wildlog.movies.utils.UtilsMovies;
import wildlog.ui.dialogs.SunMoonDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilPanelGenerator;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.utils.UtilsFileProcessing;
import wildlog.ui.helpers.WildLogTreeCellRenderer;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;

/**
 * The application's main frame.
 */
public final class WildLogView extends FrameView implements PanelNeedsRefreshWhenSightingAdded {
    // This section contains all the custom initializations that needs to happen...
    private WildLogApp app;
    private Element searchElement;
    private Element searchElementBrowseTab;
    private Location searchLocation;
    private int imageIndex = 0;

    private void init() {
        app = (WildLogApp) Application.getInstance();
        searchElement = new Element();
        searchLocation = new Location();
    }

    public WildLogView(SingleFrameApplication app) {
        super(app);
        init();
        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    messageTimer.stop();
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                    messageTimer.restart();
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.stop();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        // Setup the tab headers
        setupTabHeaderHome();
        setupTabHeaderFoto();
        setupTabHeaderLocation();
        setupTabHeaderElement();

        // Prevent reordering of the tables' columns
        tblElement.getTableHeader().setReorderingAllowed(false);
        tblElement_LocTab.getTableHeader().setReorderingAllowed(false);
        tblLocation.getTableHeader().setReorderingAllowed(false);
        tblLocation_EleTab.getTableHeader().setReorderingAllowed(false);
        tblVisit.getTableHeader().setReorderingAllowed(false);

        // Add key listeners to table to allow the selection of rows based on key events.
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation_EleTab);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement_LocTab);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);
        // Add key listener for textfields to auto search the tables
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);

        // Set the minimum size of the frame
        this.getFrame().setMinimumSize(new Dimension(1024, 705));

        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtSearch);
        UtilsUI.attachClipboardPopup(txtBrowseInfo, true);
    }

    public void setupTabHeaderHome() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon.gif"))));
        tabHeader.add(new JLabel(""));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTabComponentAt(0, tabHeader);
    }

    public void setupTabHeaderFoto() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/FotoList.gif"))));
        tabHeader.add(new JLabel("Browse"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTabComponentAt(1, tabHeader);
    }

    public void setupTabHeaderLocation() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/LocationList.gif"))));
        tabHeader.add(new JLabel("Locations"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTabComponentAt(2, tabHeader);
    }

    public void setupTabHeaderElement() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/ElementList.gif"))));
        tabHeader.add(new JLabel("Creatures"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTabComponentAt(3, tabHeader);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tabbedPanel = new javax.swing.JTabbedPane();
        tabHome = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblLocations = new javax.swing.JLabel();
        lblVisits = new javax.swing.JLabel();
        lblSightings = new javax.swing.JLabel();
        lblCreatures = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        lblWorkspace = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        tabFoto = new javax.swing.JPanel();
        rdbBrowseLocation = new javax.swing.JRadioButton();
        rdbBrowseElement = new javax.swing.JRadioButton();
        rdbBrowseDate = new javax.swing.JRadioButton();
        imgBrowsePhotos = new org.jdesktop.swingx.JXImageView();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtBrowseInfo = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        treBrowsePhoto = new javax.swing.JTree();
        btnGoBrowseSelection = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        btnViewImage = new javax.swing.JButton();
        btnBrowsePrev = new javax.swing.JButton();
        btnBrowseNext = new javax.swing.JButton();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        btnRefreshDates = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        btnDefault = new javax.swing.JButton();
        cmbElementTypesBrowseTab = new javax.swing.JComboBox();
        chkElementTypeBrowseTab = new javax.swing.JCheckBox();
        btnRotate = new javax.swing.JButton();
        btnViewEXIF = new javax.swing.JButton();
        tabLocation = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        btnGoLocation_LocTab = new javax.swing.JButton();
        btnGoElement_LocTab = new javax.swing.JButton();
        btnAddLocation = new javax.swing.JButton();
        btnDeleteLocation = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblElement_LocTab = new javax.swing.JTable();
        btnGoVisit_LocTab = new javax.swing.JButton();
        lblImage_LocTab = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tabElement = new javax.swing.JPanel();
        scrlElement = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        btnGoElement = new javax.swing.JButton();
        btnAddElement = new javax.swing.JButton();
        btnDeleteElement = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblLocation_EleTab = new javax.swing.JTable();
        cmbType = new javax.swing.JComboBox();
        btnGoLocation = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        workspaceMenu = new javax.swing.JMenu();
        mnuChangeWorkspaceMenuItem = new javax.swing.JMenuItem();
        mnuCleanWorkspace = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem mnuAboutMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        backupMenu = new javax.swing.JMenu();
        mnuBackupMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        csvExportMenuItem = new javax.swing.JMenuItem();
        htmlExportMenuItem1 = new javax.swing.JMenuItem();
        kmlExportMenuItem = new javax.swing.JMenuItem();
        importMenu = new javax.swing.JMenu();
        csvImportMenuItem = new javax.swing.JMenuItem();
        bulkImportMenuItem = new javax.swing.JMenuItem();
        advancedMenu = new javax.swing.JMenu();
        calcSunMoonMenuItem = new javax.swing.JMenuItem();
        moveVisitsMenuItem = new javax.swing.JMenuItem();
        linkElementsMenuItem = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        mappingMenu = new javax.swing.JMenu();
        chkMnuUseWMS = new javax.swing.JCheckBoxMenuItem();
        mnuMapStartMenuItem = new javax.swing.JMenuItem();
        slideshowMenu = new javax.swing.JMenu();
        mnuSetSlideshowSpeed = new javax.swing.JMenuItem();
        mnuSetSlideshowSize = new javax.swing.JMenuItem();
        otherMenu = new javax.swing.JMenu();
        mnuGPSInput = new javax.swing.JMenuItem();
        extraMenu = new javax.swing.JMenu();
        mnuExifMenuItem = new javax.swing.JMenuItem();
        mnuCreateSlideshow = new javax.swing.JMenuItem();
        mnuSunAndMoon = new javax.swing.JMenuItem();
        subMenu1 = new javax.swing.JMenu();
        mnuDBConsole = new javax.swing.JMenuItem();
        subMenu3 = new javax.swing.JMenu();
        mnuOpenMapApp = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusAnimationLabel = new javax.swing.JLabel();

        mainPanel.setMaximumSize(new java.awt.Dimension(2500, 1300));
        mainPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(2500, 1300));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.LINE_AXIS));

        tabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(WildLogView.class);
        tabbedPanel.setToolTipText(resourceMap.getString("tabbedPanel.toolTipText")); // NOI18N
        tabbedPanel.setFocusable(false);
        tabbedPanel.setMaximumSize(new java.awt.Dimension(3500, 1800));
        tabbedPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.setName("tabbedPanel"); // NOI18N
        tabbedPanel.setPreferredSize(new java.awt.Dimension(1000, 630));

        tabHome.setBackground(resourceMap.getColor("tabHome.background")); // NOI18N
        tabHome.setMaximumSize(new java.awt.Dimension(1000, 630));
        tabHome.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabHome.setName("tabHome"); // NOI18N
        tabHome.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabHome.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabHomeComponentShown(evt);
            }
        });

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setForeground(resourceMap.getColor("jLabel11.foreground")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setForeground(resourceMap.getColor("jLabel15.foreground")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel3.setIcon(resourceMap.getIcon("jLabel3.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblLocations.setForeground(resourceMap.getColor("lblLocations.foreground")); // NOI18N
        lblLocations.setText(resourceMap.getString("lblLocations.text")); // NOI18N
        lblLocations.setName("lblLocations"); // NOI18N

        lblVisits.setForeground(resourceMap.getColor("lblVisits.foreground")); // NOI18N
        lblVisits.setText(resourceMap.getString("lblVisits.text")); // NOI18N
        lblVisits.setName("lblVisits"); // NOI18N

        lblSightings.setForeground(resourceMap.getColor("lblSightings.foreground")); // NOI18N
        lblSightings.setText(resourceMap.getString("lblSightings.text")); // NOI18N
        lblSightings.setName("lblSightings"); // NOI18N

        lblCreatures.setForeground(resourceMap.getColor("lblCreatures.foreground")); // NOI18N
        lblCreatures.setText(resourceMap.getString("lblCreatures.text")); // NOI18N
        lblCreatures.setName("lblCreatures"); // NOI18N

        jSeparator5.setBackground(resourceMap.getColor("jSeparator5.background")); // NOI18N
        jSeparator5.setForeground(resourceMap.getColor("jSeparator5.foreground")); // NOI18N
        jSeparator5.setName("jSeparator5"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setForeground(resourceMap.getColor("jLabel5.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lblWorkspace.setFont(resourceMap.getFont("lblWorkspace.font")); // NOI18N
        lblWorkspace.setForeground(resourceMap.getColor("lblWorkspace.foreground")); // NOI18N
        lblWorkspace.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblWorkspace.setText(wildlog.utils.WildLogPaths.WILDLOG.getFullPath());
        lblWorkspace.setName("lblWorkspace"); // NOI18N

        jSeparator6.setBackground(resourceMap.getColor("jSeparator6.background")); // NOI18N
        jSeparator6.setForeground(resourceMap.getColor("jSeparator6.foreground")); // NOI18N
        jSeparator6.setName("jSeparator6"); // NOI18N

        javax.swing.GroupLayout tabHomeLayout = new javax.swing.GroupLayout(tabHome);
        tabHome.setLayout(tabHomeLayout);
        tabHomeLayout.setHorizontalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel11)
                .addGap(16, 16, 16)
                .addComponent(jLabel12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addContainerGap(858, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(512, Short.MAX_VALUE))
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(lblWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(520, Short.MAX_VALUE))
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(lblLocations)
                .addGap(18, 18, 18)
                .addComponent(lblVisits)
                .addGap(18, 18, 18)
                .addComponent(lblSightings)
                .addGap(18, 18, 18)
                .addComponent(lblCreatures)
                .addContainerGap(643, Short.MAX_VALUE))
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(502, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addContainerGap(875, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addContainerGap(813, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addContainerGap())
        );
        tabHomeLayout.setVerticalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(20, 20, 20)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel12)))
                .addGap(12, 12, 12)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocations)
                    .addComponent(lblVisits)
                    .addComponent(lblSightings)
                    .addComponent(lblCreatures))
                .addGap(11, 11, 11)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(lblWorkspace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPanel.addTab(resourceMap.getString("tabHome.TabConstraints.tabTitle"), tabHome); // NOI18N

        tabFoto.setBackground(resourceMap.getColor("tabFoto.background")); // NOI18N
        tabFoto.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabFoto.setName("tabFoto"); // NOI18N
        tabFoto.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabFoto.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabFotoComponentShown(evt);
            }
        });

        rdbBrowseLocation.setBackground(resourceMap.getColor("rdbBrowseLocation.background")); // NOI18N
        buttonGroup1.add(rdbBrowseLocation);
        rdbBrowseLocation.setText(resourceMap.getString("rdbBrowseLocation.text")); // NOI18N
        rdbBrowseLocation.setToolTipText(resourceMap.getString("rdbBrowseLocation.toolTipText")); // NOI18N
        rdbBrowseLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseLocation.setName("rdbBrowseLocation"); // NOI18N
        rdbBrowseLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseLocationItemStateChanged(evt);
            }
        });

        rdbBrowseElement.setBackground(resourceMap.getColor("rdbBrowseElement.background")); // NOI18N
        buttonGroup1.add(rdbBrowseElement);
        rdbBrowseElement.setText(resourceMap.getString("rdbBrowseElement.text")); // NOI18N
        rdbBrowseElement.setToolTipText(resourceMap.getString("rdbBrowseElement.toolTipText")); // NOI18N
        rdbBrowseElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseElement.setName("rdbBrowseElement"); // NOI18N
        rdbBrowseElement.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseElementItemStateChanged(evt);
            }
        });

        rdbBrowseDate.setBackground(resourceMap.getColor("rdbBrowseDate.background")); // NOI18N
        buttonGroup1.add(rdbBrowseDate);
        rdbBrowseDate.setText(resourceMap.getString("rdbBrowseDate.text")); // NOI18N
        rdbBrowseDate.setToolTipText(resourceMap.getString("rdbBrowseDate.toolTipText")); // NOI18N
        rdbBrowseDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseDate.setName("rdbBrowseDate"); // NOI18N
        rdbBrowseDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseDateItemStateChanged(evt);
            }
        });

        imgBrowsePhotos.setBackground(resourceMap.getColor("imgBrowsePhotos.background")); // NOI18N
        imgBrowsePhotos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        imgBrowsePhotos.setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
        imgBrowsePhotos.setName("imgBrowsePhotos"); // NOI18N

        javax.swing.GroupLayout imgBrowsePhotosLayout = new javax.swing.GroupLayout(imgBrowsePhotos);
        imgBrowsePhotos.setLayout(imgBrowsePhotosLayout);
        imgBrowsePhotosLayout.setHorizontalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );
        imgBrowsePhotosLayout.setVerticalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 508, Short.MAX_VALUE)
        );

        jScrollPane5.setName("jScrollPane5"); // NOI18N
        jScrollPane5.setPreferredSize(new java.awt.Dimension(230, 600));

        txtBrowseInfo.setContentType(resourceMap.getString("txtBrowseInfo.contentType")); // NOI18N
        txtBrowseInfo.setEditable(false);
        txtBrowseInfo.setText(resourceMap.getString("txtBrowseInfo.text")); // NOI18N
        txtBrowseInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtBrowseInfo.setName("txtBrowseInfo"); // NOI18N
        jScrollPane5.setViewportView(txtBrowseInfo);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treBrowsePhoto.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treBrowsePhoto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        treBrowsePhoto.setName("treBrowsePhoto"); // NOI18N
        treBrowsePhoto.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treBrowsePhotoValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(treBrowsePhoto);

        btnGoBrowseSelection.setBackground(resourceMap.getColor("btnGoBrowseSelection.background")); // NOI18N
        btnGoBrowseSelection.setIcon(resourceMap.getIcon("btnGoBrowseSelection.icon")); // NOI18N
        btnGoBrowseSelection.setText(resourceMap.getString("btnGoBrowseSelection.text")); // NOI18N
        btnGoBrowseSelection.setToolTipText(resourceMap.getString("btnGoBrowseSelection.toolTipText")); // NOI18N
        btnGoBrowseSelection.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoBrowseSelection.setFocusPainted(false);
        btnGoBrowseSelection.setName("btnGoBrowseSelection"); // NOI18N
        btnGoBrowseSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoBrowseSelectionActionPerformed(evt);
            }
        });

        btnZoomIn.setAction(imgBrowsePhotos.getZoomInAction());
        btnZoomIn.setBackground(resourceMap.getColor("btnZoomIn.background")); // NOI18N
        btnZoomIn.setIcon(resourceMap.getIcon("btnZoomIn.icon")); // NOI18N
        btnZoomIn.setText(resourceMap.getString("btnZoomIn.text")); // NOI18N
        btnZoomIn.setToolTipText(resourceMap.getString("btnZoomIn.toolTipText")); // NOI18N
        btnZoomIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomIn.setFocusPainted(false);
        btnZoomIn.setName("btnZoomIn"); // NOI18N

        btnZoomOut.setAction(imgBrowsePhotos.getZoomOutAction());
        btnZoomOut.setBackground(resourceMap.getColor("btnZoomOut.background")); // NOI18N
        btnZoomOut.setIcon(resourceMap.getIcon("btnZoomOut.icon")); // NOI18N
        btnZoomOut.setText(resourceMap.getString("btnZoomOut.text")); // NOI18N
        btnZoomOut.setToolTipText(resourceMap.getString("btnZoomOut.toolTipText")); // NOI18N
        btnZoomOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomOut.setFocusPainted(false);
        btnZoomOut.setName("btnZoomOut"); // NOI18N

        btnViewImage.setBackground(resourceMap.getColor("btnViewImage.background")); // NOI18N
        btnViewImage.setText(resourceMap.getString("btnViewImage.text")); // NOI18N
        btnViewImage.setToolTipText(resourceMap.getString("btnViewImage.toolTipText")); // NOI18N
        btnViewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewImage.setFocusPainted(false);
        btnViewImage.setName("btnViewImage"); // NOI18N
        btnViewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewImageActionPerformed(evt);
            }
        });

        btnBrowsePrev.setBackground(resourceMap.getColor("btnBrowsePrev.background")); // NOI18N
        btnBrowsePrev.setIcon(resourceMap.getIcon("btnBrowsePrev.icon")); // NOI18N
        btnBrowsePrev.setText(resourceMap.getString("btnBrowsePrev.text")); // NOI18N
        btnBrowsePrev.setToolTipText(resourceMap.getString("btnBrowsePrev.toolTipText")); // NOI18N
        btnBrowsePrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowsePrev.setFocusPainted(false);
        btnBrowsePrev.setName("btnBrowsePrev"); // NOI18N
        btnBrowsePrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowsePrevActionPerformed(evt);
            }
        });

        btnBrowseNext.setBackground(resourceMap.getColor("btnBrowseNext.background")); // NOI18N
        btnBrowseNext.setIcon(resourceMap.getIcon("btnBrowseNext.icon")); // NOI18N
        btnBrowseNext.setText(resourceMap.getString("btnBrowseNext.text")); // NOI18N
        btnBrowseNext.setToolTipText(resourceMap.getString("btnBrowseNext.toolTipText")); // NOI18N
        btnBrowseNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowseNext.setFocusPainted(false);
        btnBrowseNext.setName("btnBrowseNext"); // NOI18N
        btnBrowseNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseNextActionPerformed(evt);
            }
        });

        dtpStartDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpStartDate.setName("dtpStartDate"); // NOI18N

        dtpEndDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpEndDate.setName("dtpEndDate"); // NOI18N

        btnRefreshDates.setBackground(resourceMap.getColor("btnRefreshDates.background")); // NOI18N
        btnRefreshDates.setIcon(resourceMap.getIcon("btnRefreshDates.icon")); // NOI18N
        btnRefreshDates.setText(resourceMap.getString("btnRefreshDates.text")); // NOI18N
        btnRefreshDates.setToolTipText(resourceMap.getString("btnRefreshDates.toolTipText")); // NOI18N
        btnRefreshDates.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefreshDates.setFocusPainted(false);
        btnRefreshDates.setIconTextGap(0);
        btnRefreshDates.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnRefreshDates.setName("btnRefreshDates"); // NOI18N
        btnRefreshDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDatesActionPerformed(evt);
            }
        });

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        btnReport.setBackground(resourceMap.getColor("btnReport.background")); // NOI18N
        btnReport.setIcon(resourceMap.getIcon("btnReport.icon")); // NOI18N
        btnReport.setText(resourceMap.getString("btnReport.text")); // NOI18N
        btnReport.setToolTipText(resourceMap.getString("btnReport.toolTipText")); // NOI18N
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setFocusPainted(false);
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        btnDefault.setBackground(resourceMap.getColor("btnDefault.background")); // NOI18N
        btnDefault.setIcon(resourceMap.getIcon("btnDefault.icon")); // NOI18N
        btnDefault.setText(resourceMap.getString("btnDefault.text")); // NOI18N
        btnDefault.setToolTipText(resourceMap.getString("btnDefault.toolTipText")); // NOI18N
        btnDefault.setFocusPainted(false);
        btnDefault.setName("btnDefault"); // NOI18N
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });

        cmbElementTypesBrowseTab.setBackground(resourceMap.getColor("cmbElementTypesBrowseTab.background")); // NOI18N
        cmbElementTypesBrowseTab.setMaximumRowCount(9);
        cmbElementTypesBrowseTab.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementTypesBrowseTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementTypesBrowseTab.setEnabled(false);
        cmbElementTypesBrowseTab.setName("cmbElementTypesBrowseTab"); // NOI18N
        cmbElementTypesBrowseTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypesBrowseTabActionPerformed(evt);
            }
        });

        chkElementTypeBrowseTab.setBackground(resourceMap.getColor("chkElementTypeBrowseTab.background")); // NOI18N
        chkElementTypeBrowseTab.setText(resourceMap.getString("chkElementTypeBrowseTab.text")); // NOI18N
        chkElementTypeBrowseTab.setName("chkElementTypeBrowseTab"); // NOI18N
        chkElementTypeBrowseTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkElementTypeBrowseTabActionPerformed(evt);
            }
        });

        btnRotate.setAction(imgBrowsePhotos.getRotateCounterClockwiseAction());
        btnRotate.setBackground(resourceMap.getColor("btnRotate.background")); // NOI18N
        btnRotate.setIcon(resourceMap.getIcon("btnRotate.icon")); // NOI18N
        btnRotate.setText(resourceMap.getString("btnRotate.text")); // NOI18N
        btnRotate.setToolTipText(resourceMap.getString("btnRotate.toolTipText")); // NOI18N
        btnRotate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRotate.setFocusPainted(false);
        btnRotate.setName("btnRotate"); // NOI18N

        btnViewEXIF.setBackground(resourceMap.getColor("btnViewEXIF.background")); // NOI18N
        btnViewEXIF.setIcon(resourceMap.getIcon("btnViewEXIF.icon")); // NOI18N
        btnViewEXIF.setText(resourceMap.getString("btnViewEXIF.text")); // NOI18N
        btnViewEXIF.setToolTipText(resourceMap.getString("btnViewEXIF.toolTipText")); // NOI18N
        btnViewEXIF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewEXIF.setFocusPainted(false);
        btnViewEXIF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewEXIF.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnViewEXIF.setName("btnViewEXIF"); // NOI18N
        btnViewEXIF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewEXIFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabFotoLayout = new javax.swing.GroupLayout(tabFoto);
        tabFoto.setLayout(tabFotoLayout);
        tabFotoLayout.setHorizontalGroup(
            tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFotoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addComponent(rdbBrowseLocation)
                        .addGap(5, 5, 5)
                        .addComponent(rdbBrowseElement)
                        .addGap(1, 1, 1)
                        .addComponent(rdbBrowseDate))
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkElementTypeBrowseTab)
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnRefreshDates, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(btnBrowsePrev)
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(btnDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)
                        .addComponent(btnBrowseNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnViewImage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnViewEXIF, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRotate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imgBrowsePhotos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabFotoLayout.setVerticalGroup(
            tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabFotoLayout.createSequentialGroup()
                .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbBrowseLocation)
                            .addComponent(rdbBrowseElement)
                            .addComponent(rdbBrowseDate))
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnRefreshDates, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(chkElementTypeBrowseTab))
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabFotoLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabFotoLayout.createSequentialGroup()
                                .addGroup(tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBrowsePrev, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(tabFotoLayout.createSequentialGroup()
                                        .addComponent(btnDefault)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnBrowseNext, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(tabFotoLayout.createSequentialGroup()
                                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(btnViewImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tabFotoLayout.createSequentialGroup()
                                        .addComponent(btnViewEXIF, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tabFotoLayout.createSequentialGroup()
                                        .addComponent(btnRotate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(11, 11, 11)
                                .addComponent(imgBrowsePhotos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE))))
                .addContainerGap())
        );

        tabbedPanel.addTab(resourceMap.getString("tabFoto.TabConstraints.tabTitle"), tabFoto); // NOI18N

        tabLocation.setBackground(resourceMap.getColor("tabLocation.background")); // NOI18N
        tabLocation.setMaximumSize(new java.awt.Dimension(1000, 600));
        tabLocation.setMinimumSize(new java.awt.Dimension(1000, 600));
        tabLocation.setName("tabLocation"); // NOI18N
        tabLocation.setPreferredSize(new java.awt.Dimension(1000, 600));
        tabLocation.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabLocationComponentShown(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
        tblLocation.setMaximumSize(new java.awt.Dimension(300, 300));
        tblLocation.setMinimumSize(new java.awt.Dimension(300, 300));
        tblLocation.setSelectionBackground(resourceMap.getColor("tblLocation.selectionBackground")); // NOI18N
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocationMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblLocation);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(resourceMap.getColor("tblVisit.selectionBackground")); // NOI18N
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblVisit);

        btnGoLocation_LocTab.setBackground(resourceMap.getColor("btnGoLocation_LocTab.background")); // NOI18N
        btnGoLocation_LocTab.setIcon(resourceMap.getIcon("btnGoLocation_LocTab.icon")); // NOI18N
        btnGoLocation_LocTab.setText(resourceMap.getString("btnGoLocation_LocTab.text")); // NOI18N
        btnGoLocation_LocTab.setToolTipText(resourceMap.getString("btnGoLocation_LocTab.toolTipText")); // NOI18N
        btnGoLocation_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation_LocTab.setFocusPainted(false);
        btnGoLocation_LocTab.setName("btnGoLocation_LocTab"); // NOI18N
        btnGoLocation_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocation_LocTabActionPerformed(evt);
            }
        });

        btnGoElement_LocTab.setBackground(resourceMap.getColor("btnGoElement_LocTab.background")); // NOI18N
        btnGoElement_LocTab.setIcon(resourceMap.getIcon("btnGoElement_LocTab.icon")); // NOI18N
        btnGoElement_LocTab.setText(resourceMap.getString("btnGoElement_LocTab.text")); // NOI18N
        btnGoElement_LocTab.setToolTipText(resourceMap.getString("btnGoElement_LocTab.toolTipText")); // NOI18N
        btnGoElement_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement_LocTab.setFocusPainted(false);
        btnGoElement_LocTab.setName("btnGoElement_LocTab"); // NOI18N
        btnGoElement_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElement_LocTabActionPerformed(evt);
            }
        });

        btnAddLocation.setBackground(resourceMap.getColor("btnAddLocation.background")); // NOI18N
        btnAddLocation.setIcon(resourceMap.getIcon("btnAddLocation.icon")); // NOI18N
        btnAddLocation.setText(resourceMap.getString("btnAddLocation.text")); // NOI18N
        btnAddLocation.setToolTipText(resourceMap.getString("btnAddLocation.toolTipText")); // NOI18N
        btnAddLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddLocation.setFocusPainted(false);
        btnAddLocation.setName("btnAddLocation"); // NOI18N
        btnAddLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLocationActionPerformed(evt);
            }
        });

        btnDeleteLocation.setBackground(resourceMap.getColor("btnDeleteLocation.background")); // NOI18N
        btnDeleteLocation.setIcon(resourceMap.getIcon("btnDeleteLocation.icon")); // NOI18N
        btnDeleteLocation.setText(resourceMap.getString("btnDeleteLocation.text")); // NOI18N
        btnDeleteLocation.setToolTipText(resourceMap.getString("btnDeleteLocation.toolTipText")); // NOI18N
        btnDeleteLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteLocation.setFocusPainted(false);
        btnDeleteLocation.setName("btnDeleteLocation"); // NOI18N
        btnDeleteLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLocationActionPerformed(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblElement_LocTab.setAutoCreateRowSorter(true);
        tblElement_LocTab.setFont(resourceMap.getFont("tblElement_LocTab.font")); // NOI18N
        tblElement_LocTab.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
        tblElement_LocTab.setName("tblElement_LocTab"); // NOI18N
        tblElement_LocTab.setSelectionBackground(resourceMap.getColor("tblElement_LocTab.selectionBackground")); // NOI18N
        tblElement_LocTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElement_LocTabMouseClicked(evt);
            }
        });
        tblElement_LocTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElement_LocTabKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tblElement_LocTab);

        btnGoVisit_LocTab.setBackground(resourceMap.getColor("btnGoVisit_LocTab.background")); // NOI18N
        btnGoVisit_LocTab.setIcon(resourceMap.getIcon("btnGoVisit_LocTab.icon")); // NOI18N
        btnGoVisit_LocTab.setText(resourceMap.getString("btnGoVisit_LocTab.text")); // NOI18N
        btnGoVisit_LocTab.setToolTipText(resourceMap.getString("btnGoVisit_LocTab.toolTipText")); // NOI18N
        btnGoVisit_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoVisit_LocTab.setFocusPainted(false);
        btnGoVisit_LocTab.setName("btnGoVisit_LocTab"); // NOI18N
        btnGoVisit_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisit_LocTabActionPerformed(evt);
            }
        });

        lblImage_LocTab.setBackground(resourceMap.getColor("lblImage_LocTab.background")); // NOI18N
        lblImage_LocTab.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage_LocTab.setText(resourceMap.getString("lblImage_LocTab.text")); // NOI18N
        lblImage_LocTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage_LocTab.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage_LocTab.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage_LocTab.setName("lblImage_LocTab"); // NOI18N
        lblImage_LocTab.setOpaque(true);
        lblImage_LocTab.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage_LocTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImage_LocTabMouseReleased(evt);
            }
        });

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout tabLocationLayout = new javax.swing.GroupLayout(tabLocation);
        tabLocation.setLayout(tabLocationLayout);
        tabLocationLayout.setHorizontalGroup(
            tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabLocationLayout.createSequentialGroup()
                        .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoLocation_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE))
                    .addGroup(tabLocationLayout.createSequentialGroup()
                        .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(btnGoVisit_LocTab, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                            .addComponent(btnGoElement_LocTab, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addComponent(lblImage_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tabLocationLayout.setVerticalGroup(
            tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabLocationLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(13, 13, 13)
                        .addComponent(btnGoLocation_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabLocationLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGoVisit_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImage_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabLocationLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGoElement_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        tabbedPanel.addTab(resourceMap.getString("tabLocation.TabConstraints.tabTitle"), tabLocation); // NOI18N

        tabElement.setBackground(resourceMap.getColor("tabElement.background")); // NOI18N
        tabElement.setMaximumSize(new java.awt.Dimension(1000, 600));
        tabElement.setMinimumSize(new java.awt.Dimension(1000, 600));
        tabElement.setName("tabElement"); // NOI18N
        tabElement.setPreferredSize(new java.awt.Dimension(1000, 600));
        tabElement.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabElementComponentShown(evt);
            }
        });

        scrlElement.setName("scrlElement"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
        tblElement.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(resourceMap.getColor("tblElement.selectionBackground")); // NOI18N
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        scrlElement.setViewportView(tblElement);

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        btnGoElement.setBackground(resourceMap.getColor("btnGoElement.background")); // NOI18N
        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setFocusPainted(false);
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });

        btnAddElement.setBackground(resourceMap.getColor("btnAddElement.background")); // NOI18N
        btnAddElement.setIcon(resourceMap.getIcon("btnAddElement.icon")); // NOI18N
        btnAddElement.setText(resourceMap.getString("btnAddElement.text")); // NOI18N
        btnAddElement.setToolTipText(resourceMap.getString("btnAddElement.toolTipText")); // NOI18N
        btnAddElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddElement.setFocusPainted(false);
        btnAddElement.setName("btnAddElement"); // NOI18N
        btnAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddElementActionPerformed(evt);
            }
        });

        btnDeleteElement.setBackground(resourceMap.getColor("btnDeleteElement.background")); // NOI18N
        btnDeleteElement.setIcon(resourceMap.getIcon("btnDeleteElement.icon")); // NOI18N
        btnDeleteElement.setText(resourceMap.getString("btnDeleteElement.text")); // NOI18N
        btnDeleteElement.setToolTipText(resourceMap.getString("btnDeleteElement.toolTipText")); // NOI18N
        btnDeleteElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteElement.setFocusPainted(false);
        btnDeleteElement.setName("btnDeleteElement"); // NOI18N
        btnDeleteElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteElementActionPerformed(evt);
            }
        });

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tblLocation_EleTab.setAutoCreateRowSorter(true);
        tblLocation_EleTab.setFont(resourceMap.getFont("tblLocation_EleTab.font")); // NOI18N
        tblLocation_EleTab.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
        tblLocation_EleTab.setName("tblLocation_EleTab"); // NOI18N
        tblLocation_EleTab.setSelectionBackground(resourceMap.getColor("tblLocation_EleTab.selectionBackground")); // NOI18N
        tblLocation_EleTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocation_EleTabMouseClicked(evt);
            }
        });
        tblLocation_EleTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocation_EleTabKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(tblLocation_EleTab);

        cmbType.setMaximumRowCount(9);
        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbType.setName("cmbType"); // NOI18N
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeActionPerformed(evt);
            }
        });

        btnGoLocation.setBackground(resourceMap.getColor("btnGoLocation.background")); // NOI18N
        btnGoLocation.setIcon(resourceMap.getIcon("btnGoLocation.icon")); // NOI18N
        btnGoLocation.setText(resourceMap.getString("btnGoLocation.text")); // NOI18N
        btnGoLocation.setToolTipText(resourceMap.getString("btnGoLocation.toolTipText")); // NOI18N
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setFocusPainted(false);
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });

        lblImage.setBackground(resourceMap.getColor("lblImage.background")); // NOI18N
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setOpaque(true);
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setName("txtSearch"); // NOI18N

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout tabElementLayout = new javax.swing.GroupLayout(tabElement);
        tabElement.setLayout(tabElementLayout);
        tabElementLayout.setHorizontalGroup(
            tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabElementLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabElementLayout.createSequentialGroup()
                        .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrlElement, javax.swing.GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE))
                    .addGroup(tabElementLayout.createSequentialGroup()
                        .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabElementLayout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabElementLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)))
                        .addGap(10, 10, 10)
                        .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGoLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addComponent(jLabel6)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tabElementLayout.setVerticalGroup(
            tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabElementLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabElementLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(13, 13, 13)
                        .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrlElement, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabElementLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabElementLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(5, 5, 5)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(tabElementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        tabbedPanel.addTab(resourceMap.getString("tabElement.TabConstraints.tabTitle"), tabElement); // NOI18N

        mainPanel.add(tabbedPanel);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        workspaceMenu.setIcon(resourceMap.getIcon("workspaceMenu.icon")); // NOI18N
        workspaceMenu.setText(resourceMap.getString("workspaceMenu.text")); // NOI18N
        workspaceMenu.setName("workspaceMenu"); // NOI18N

        mnuChangeWorkspaceMenuItem.setIcon(resourceMap.getIcon("mnuChangeWorkspaceMenuItem.icon")); // NOI18N
        mnuChangeWorkspaceMenuItem.setText(resourceMap.getString("mnuChangeWorkspaceMenuItem.text")); // NOI18N
        mnuChangeWorkspaceMenuItem.setName("mnuChangeWorkspaceMenuItem"); // NOI18N
        mnuChangeWorkspaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuChangeWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuChangeWorkspaceMenuItem);

        mnuCleanWorkspace.setIcon(resourceMap.getIcon("mnuCleanWorkspace.icon")); // NOI18N
        mnuCleanWorkspace.setText(resourceMap.getString("mnuCleanWorkspace.text")); // NOI18N
        mnuCleanWorkspace.setName("mnuCleanWorkspace"); // NOI18N
        mnuCleanWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCleanWorkspaceActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuCleanWorkspace);

        fileMenu.add(workspaceMenu);

        helpMenu.setIcon(resourceMap.getIcon("helpMenu.icon")); // NOI18N
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        mnuAboutMenuItem.setIcon(resourceMap.getIcon("mnuAboutMenuItem.icon")); // NOI18N
        mnuAboutMenuItem.setText(resourceMap.getString("mnuAboutMenuItem.text")); // NOI18N
        mnuAboutMenuItem.setName("mnuAboutMenuItem"); // NOI18N
        mnuAboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(mnuAboutMenuItem);

        fileMenu.add(helpMenu);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setIcon(resourceMap.getIcon("exitMenuItem.icon")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        backupMenu.setText(resourceMap.getString("backupMenu.text")); // NOI18N
        backupMenu.setName("backupMenu"); // NOI18N

        mnuBackupMenuItem.setIcon(resourceMap.getIcon("mnuBackupMenuItem.icon")); // NOI18N
        mnuBackupMenuItem.setText(resourceMap.getString("mnuBackupMenuItem.text")); // NOI18N
        mnuBackupMenuItem.setName("mnuBackupMenuItem"); // NOI18N
        mnuBackupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBackupMenuItemActionPerformed(evt);
            }
        });
        backupMenu.add(mnuBackupMenuItem);

        menuBar.add(backupMenu);

        exportMenu.setText(resourceMap.getString("exportMenu.text")); // NOI18N

        csvExportMenuItem.setIcon(resourceMap.getIcon("csvExportMenuItem.icon")); // NOI18N
        csvExportMenuItem.setText(resourceMap.getString("csvExportMenuItem.text")); // NOI18N
        csvExportMenuItem.setToolTipText(resourceMap.getString("csvExportMenuItem.toolTipText")); // NOI18N
        csvExportMenuItem.setName("csvExportMenuItem"); // NOI18N
        csvExportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvExportMenuItemActionPerformed(evt);
            }
        });
        exportMenu.add(csvExportMenuItem);

        htmlExportMenuItem1.setIcon(resourceMap.getIcon("htmlExportMenuItem1.icon")); // NOI18N
        htmlExportMenuItem1.setText(resourceMap.getString("htmlExportMenuItem1.text")); // NOI18N
        htmlExportMenuItem1.setToolTipText(resourceMap.getString("htmlExportMenuItem1.toolTipText")); // NOI18N
        htmlExportMenuItem1.setName("htmlExportMenuItem1"); // NOI18N
        htmlExportMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                htmlExportMenuItem1ActionPerformed(evt);
            }
        });
        exportMenu.add(htmlExportMenuItem1);

        kmlExportMenuItem.setIcon(resourceMap.getIcon("kmlExportMenuItem.icon")); // NOI18N
        kmlExportMenuItem.setText(resourceMap.getString("kmlExportMenuItem.text")); // NOI18N
        kmlExportMenuItem.setToolTipText(resourceMap.getString("kmlExportMenuItem.toolTipText")); // NOI18N
        kmlExportMenuItem.setName("kmlExportMenuItem"); // NOI18N
        kmlExportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kmlExportMenuItemActionPerformed(evt);
            }
        });
        exportMenu.add(kmlExportMenuItem);

        menuBar.add(exportMenu);

        importMenu.setText(resourceMap.getString("importMenu.text")); // NOI18N
        importMenu.setName("importMenu"); // NOI18N

        csvImportMenuItem.setIcon(resourceMap.getIcon("csvImportMenuItem.icon")); // NOI18N
        csvImportMenuItem.setText(resourceMap.getString("csvImportMenuItem.text")); // NOI18N
        csvImportMenuItem.setToolTipText(resourceMap.getString("csvImportMenuItem.toolTipText")); // NOI18N
        csvImportMenuItem.setName("csvImportMenuItem"); // NOI18N
        csvImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvImportMenuItemActionPerformed(evt);
            }
        });
        importMenu.add(csvImportMenuItem);

        bulkImportMenuItem.setIcon(resourceMap.getIcon("bulkImportMenuItem.icon")); // NOI18N
        bulkImportMenuItem.setText(resourceMap.getString("bulkImportMenuItem.text")); // NOI18N
        bulkImportMenuItem.setToolTipText(resourceMap.getString("bulkImportMenuItem.toolTipText")); // NOI18N
        bulkImportMenuItem.setName("bulkImportMenuItem"); // NOI18N
        bulkImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bulkImportMenuItemActionPerformed(evt);
            }
        });
        importMenu.add(bulkImportMenuItem);

        menuBar.add(importMenu);

        advancedMenu.setText(resourceMap.getString("advancedMenu.text")); // NOI18N

        calcSunMoonMenuItem.setIcon(resourceMap.getIcon("calcSunMoonMenuItem.icon")); // NOI18N
        calcSunMoonMenuItem.setText(resourceMap.getString("calcSunMoonMenuItem.text")); // NOI18N
        calcSunMoonMenuItem.setName("calcSunMoonMenuItem"); // NOI18N
        calcSunMoonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcSunMoonMenuItemActionPerformed(evt);
            }
        });
        advancedMenu.add(calcSunMoonMenuItem);

        moveVisitsMenuItem.setIcon(resourceMap.getIcon("moveVisitsMenuItem.icon")); // NOI18N
        moveVisitsMenuItem.setText(resourceMap.getString("moveVisitsMenuItem.text")); // NOI18N
        moveVisitsMenuItem.setName("moveVisitsMenuItem"); // NOI18N
        moveVisitsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveVisitsMenuItemActionPerformed(evt);
            }
        });
        advancedMenu.add(moveVisitsMenuItem);

        linkElementsMenuItem.setIcon(resourceMap.getIcon("linkElementsMenuItem.icon")); // NOI18N
        linkElementsMenuItem.setText(resourceMap.getString("linkElementsMenuItem.text")); // NOI18N
        linkElementsMenuItem.setName("linkElementsMenuItem"); // NOI18N
        linkElementsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkElementsMenuItemActionPerformed(evt);
            }
        });
        advancedMenu.add(linkElementsMenuItem);

        menuBar.add(advancedMenu);

        settingsMenu.setText(resourceMap.getString("settingsMenu.text")); // NOI18N
        settingsMenu.setName("settingsMenu"); // NOI18N

        mappingMenu.setIcon(resourceMap.getIcon("mappingMenu.icon")); // NOI18N
        mappingMenu.setText(resourceMap.getString("mappingMenu.text")); // NOI18N
        mappingMenu.setName("mappingMenu"); // NOI18N

        chkMnuUseWMS.setSelected(app.getWildLogOptions().isIsOnlinemapTheDefault());
        chkMnuUseWMS.setText(resourceMap.getString("chkMnuUseWMS.text")); // NOI18N
        chkMnuUseWMS.setName("chkMnuUseWMS"); // NOI18N
        chkMnuUseWMS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUseWMSItemStateChanged(evt);
            }
        });
        mappingMenu.add(chkMnuUseWMS);

        mnuMapStartMenuItem.setText(resourceMap.getString("mnuMapStartMenuItem.text")); // NOI18N
        mnuMapStartMenuItem.setName("mnuMapStartMenuItem"); // NOI18N
        mnuMapStartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMapStartMenuItemActionPerformed(evt);
            }
        });
        mappingMenu.add(mnuMapStartMenuItem);

        settingsMenu.add(mappingMenu);

        slideshowMenu.setIcon(resourceMap.getIcon("slideshowMenu.icon")); // NOI18N
        slideshowMenu.setText(resourceMap.getString("slideshowMenu.text")); // NOI18N
        slideshowMenu.setName("slideshowMenu"); // NOI18N

        mnuSetSlideshowSpeed.setText(resourceMap.getString("mnuSetSlideshowSpeed.text")); // NOI18N
        mnuSetSlideshowSpeed.setName("mnuSetSlideshowSpeed"); // NOI18N
        mnuSetSlideshowSpeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSpeedActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSpeed);

        mnuSetSlideshowSize.setText(resourceMap.getString("mnuSetSlideshowSize.text")); // NOI18N
        mnuSetSlideshowSize.setName("mnuSetSlideshowSize"); // NOI18N
        mnuSetSlideshowSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSizeActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSize);

        settingsMenu.add(slideshowMenu);

        otherMenu.setIcon(resourceMap.getIcon("otherMenu.icon")); // NOI18N
        otherMenu.setText(resourceMap.getString("otherMenu.text")); // NOI18N
        otherMenu.setName("otherMenu"); // NOI18N

        mnuGPSInput.setText(resourceMap.getString("mnuGPSInput.text")); // NOI18N
        mnuGPSInput.setName("mnuGPSInput"); // NOI18N
        mnuGPSInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGPSInputActionPerformed(evt);
            }
        });
        otherMenu.add(mnuGPSInput);

        settingsMenu.add(otherMenu);

        menuBar.add(settingsMenu);

        extraMenu.setText(resourceMap.getString("extraMenu.text")); // NOI18N
        extraMenu.setName("extraMenu"); // NOI18N

        mnuExifMenuItem.setIcon(resourceMap.getIcon("mnuExifMenuItem.icon")); // NOI18N
        mnuExifMenuItem.setText(resourceMap.getString("mnuExifMenuItem.text")); // NOI18N
        mnuExifMenuItem.setName("mnuExifMenuItem"); // NOI18N
        mnuExifMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExifMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(mnuExifMenuItem);

        mnuCreateSlideshow.setIcon(resourceMap.getIcon("mnuCreateSlideshow.icon")); // NOI18N
        mnuCreateSlideshow.setText(resourceMap.getString("mnuCreateSlideshow.text")); // NOI18N
        mnuCreateSlideshow.setName("mnuCreateSlideshow"); // NOI18N
        mnuCreateSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateSlideshowActionPerformed(evt);
            }
        });
        extraMenu.add(mnuCreateSlideshow);

        mnuSunAndMoon.setIcon(resourceMap.getIcon("mnuSunAndMoon.icon")); // NOI18N
        mnuSunAndMoon.setText(resourceMap.getString("mnuSunAndMoon.text")); // NOI18N
        mnuSunAndMoon.setName("mnuSunAndMoon"); // NOI18N
        mnuSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSunAndMoonActionPerformed(evt);
            }
        });
        extraMenu.add(mnuSunAndMoon);

        subMenu1.setIcon(resourceMap.getIcon("subMenu1.icon")); // NOI18N
        subMenu1.setText(resourceMap.getString("subMenu1.text")); // NOI18N
        subMenu1.setName("subMenu1"); // NOI18N

        mnuDBConsole.setText(resourceMap.getString("mnuDBConsole.text")); // NOI18N
        mnuDBConsole.setToolTipText(resourceMap.getString("mnuDBConsole.toolTipText")); // NOI18N
        mnuDBConsole.setName("mnuDBConsole"); // NOI18N
        mnuDBConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDBConsoleActionPerformed(evt);
            }
        });
        subMenu1.add(mnuDBConsole);

        extraMenu.add(subMenu1);

        subMenu3.setIcon(resourceMap.getIcon("subMenu3.icon")); // NOI18N
        subMenu3.setText(resourceMap.getString("subMenu3.text")); // NOI18N
        subMenu3.setName("subMenu3"); // NOI18N

        mnuOpenMapApp.setText(resourceMap.getString("mnuOpenMapApp.text")); // NOI18N
        mnuOpenMapApp.setToolTipText(resourceMap.getString("mnuOpenMapApp.toolTipText")); // NOI18N
        mnuOpenMapApp.setName("mnuOpenMapApp"); // NOI18N
        mnuOpenMapApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenMapAppActionPerformed(evt);
            }
        });
        subMenu3.add(mnuOpenMapApp);

        extraMenu.add(subMenu3);

        menuBar.add(extraMenu);

        statusPanel.setBackground(resourceMap.getColor("statusPanel.background")); // NOI18N
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        statusMessageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusMessageLabel.setAlignmentY(0.0F);
        statusMessageLabel.setMaximumSize(new java.awt.Dimension(800, 20));
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusMessageLabel.setPreferredSize(new java.awt.Dimension(400, 20));
        statusPanel.add(statusMessageLabel);

        progressBar.setName("progressBar"); // NOI18N
        progressBar.setPreferredSize(new java.awt.Dimension(400, 16));
        statusPanel.add(progressBar);

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusAnimationLabel.setPreferredSize(new java.awt.Dimension(20, 20));
        statusPanel.add(statusAnimationLabel);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoVisit_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisit_LocTabActionPerformed
        if (tblLocation.getSelectedRow() != -1) {
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            int[] selectedRows = tblVisit.getSelectedRows();
            PanelVisit tempPanel = null;
            for (int t = 0; t < selectedRows.length; t++) {
                tempPanel = UtilPanelGenerator.getVisitPanel(tempLocation, (String)tblVisit.getValueAt(selectedRows[t], 0));
                UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
            }
        }
}//GEN-LAST:event_btnGoVisit_LocTabActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        int[] selectedRows = tblElement.getSelectedRows();
        PanelElement tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = UtilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
            UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
        }
}//GEN-LAST:event_btnGoElementActionPerformed

    private void btnAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddElementActionPerformed
        PanelElement tempPanel = UtilPanelGenerator.getNewElementPanel();
        UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
}//GEN-LAST:event_btnAddElementActionPerformed

    private void tabElementComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabElementComponentShown
        UtilTableGenerator.setupCompleteElementTable(tblElement, searchElement);
        tblLocation_EleTab.setModel(new DefaultTableModel(new String[]{"No Creature Selected"}, 0));
        lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
}//GEN-LAST:event_tabElementComponentShown

    private void btnDeleteElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteElementActionPerformed
        if (tblElement.getSelectedRowCount() > 0) {
            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                            "Are you sure you want to delete the Creature(s)?  This will delete all Sightings and photos linked to this Creature.",
                            "Delete Creature(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            });
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblElement.getSelectedRows();
                PanelElement tempPanel = null;
                for (int t = 0; t < selectedRows.length; t++) {
                    tempPanel = UtilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
                    tabbedPanel.remove(tempPanel);
                    app.getDBI().delete(new Element((String)tblElement.getValueAt(selectedRows[t], 0)));
                }
                tabElementComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteElementActionPerformed

    private void btnAddLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLocationActionPerformed
        PanelLocation tempPanel = UtilPanelGenerator.getNewLocationPanel();
        UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
    }//GEN-LAST:event_btnAddLocationActionPerformed

    private void tabLocationComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabLocationComponentShown
        UtilTableGenerator.setupCompleteLocationTable(tblLocation, searchLocation);
        tblVisit.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
        tblElement_LocTab.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
        lblImage_LocTab.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
    }//GEN-LAST:event_tabLocationComponentShown

    private void btnGoLocation_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocation_LocTabActionPerformed
        int[] selectedRows = tblLocation.getSelectedRows();
        PanelLocation tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = UtilPanelGenerator.getLocationPanel((String)tblLocation.getValueAt(selectedRows[t], 0));
            UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
        }
    }//GEN-LAST:event_btnGoLocation_LocTabActionPerformed

    private void cmbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTypeActionPerformed
        searchElement = new Element((ElementType)cmbType.getSelectedItem());
        UtilTableGenerator.setupCompleteElementTable(tblElement, searchElement);
        txtSearch.setText("");
        UtilTableGenerator.setupLocationsForElementTable(tblLocation_EleTab, new Element());
        lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
    }//GEN-LAST:event_cmbTypeActionPerformed

    private void btnGoElement_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElement_LocTabActionPerformed
        int[] selectedRows = tblElement_LocTab.getSelectedRows();
        PanelElement tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = UtilPanelGenerator.getElementPanel((String)tblElement_LocTab.getValueAt(selectedRows[t], 0));
            UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
        }
    }//GEN-LAST:event_btnGoElement_LocTabActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        int[] selectedRows = tblLocation_EleTab.getSelectedRows();
        PanelLocation tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = UtilPanelGenerator.getLocationPanel((String)tblLocation_EleTab.getValueAt(selectedRows[t], 0));
            UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
        }
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void btnDeleteLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLocationActionPerformed
        if (tblLocation.getSelectedRowCount() > 0) {
            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                            "Are you sure you want to delete the Location(s)? This will delete all Visits, Sightings and photos linked to this Location.",
                            "Delete Location(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            });
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblLocation.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(selectedRows[t], 0)));
                    Visit tempVisit = new Visit();
                    tempVisit.setLocationName(tempLocation.getName());
                    List<Visit> visits = app.getDBI().list(tempVisit);
                    for (int i = 0; i < visits.size(); i++) {
                        PanelVisit tempPanel = UtilPanelGenerator.getVisitPanel(tempLocation, visits.get(i).getName());
                        tabbedPanel.remove(tempPanel);
                    }
                    tabbedPanel.remove(UtilPanelGenerator.getLocationPanel(tempLocation.getName()));
                    app.getDBI().delete(tempLocation);
                }
                tabLocationComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteLocationActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            // Get Image
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + tempElement.getPrimaryName()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto("ELEMENT-" + tempElement.getPrimaryName(), 0, lblImage, 300, app);
            else
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
            // Get Locations
            UtilTableGenerator.setupLocationsForElementTable(tblLocation_EleTab, tempElement);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
            tblLocation_EleTab.setModel(new DefaultTableModel(new String[]{"No Creature Selected"}, 0));
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            // Get Image
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + tempLocation.getName()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto("LOCATION-" + tempLocation.getName(), 0, lblImage_LocTab, 300, app);
            else
                lblImage_LocTab.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
            // Get Visits
            UtilTableGenerator.setupShortVisitTable(tblVisit, tempLocation);
            // Get All Elements seen
            UtilTableGenerator.setupElementsForLocationTable(tblElement_LocTab, tempLocation);
        }
        else {
            lblImage_LocTab.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
            tblVisit.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
            tblElement_LocTab.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
        }
    }//GEN-LAST:event_tblLocationMouseReleased

    private void tabHomeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabHomeComponentShown
        lblLocations.setText("Locations: " + app.getDBI().list(new Location()).size());
        lblVisits.setText("Visits: " + app.getDBI().list(new Visit()).size());
        lblSightings.setText("Sightings: " + app.getDBI().list(new Sighting()).size());
        lblCreatures.setText("Creatures: " + app.getDBI().list(new Element()).size());
    }//GEN-LAST:event_tabHomeComponentShown

    private void tabFotoComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabFotoComponentShown
        dtpStartDate.setVisible(false);
        dtpEndDate.setVisible(false);
        btnRefreshDates.setVisible(false);
        btnReport.setVisible(false);
        cmbElementTypesBrowseTab.setVisible(false);
        chkElementTypeBrowseTab.setVisible(false);
        rdbBrowseLocationItemStateChanged(null);
        rdbBrowseElementItemStateChanged(null);
        rdbBrowseDateItemStateChanged(null);
        if (!rdbBrowseElement.isSelected() && !rdbBrowseLocation.isSelected() && !rdbBrowseDate.isSelected()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please select a category to browse");
            treBrowsePhoto.setModel(new DefaultTreeModel(root));
        }
        treBrowsePhoto.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treBrowsePhoto.setCellRenderer(new WildLogTreeCellRenderer(app));
        txtBrowseInfo.setText("");
        lblNumberOfImages.setText("");
        try {
            imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
            if (imgBrowsePhotos.getImage() != null) {
                if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                    imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
                else
                    imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_tabFotoComponentShown

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                UtilsFileProcessing.openFile("ELEMENT-" + tempElement.getPrimaryName(), 0, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblImage_LocTabMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage_LocTabMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            UtilsFileProcessing.openFile("LOCATION-" + tempLocation.getName(), 0, app);
        }
    }//GEN-LAST:event_lblImage_LocTabMouseReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoLocation_LocTabActionPerformed(null);
    }//GEN-LAST:event_tblLocationKeyPressed

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblLocationMouseReleased(null);
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoElementActionPerformed(null);
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void tblLocation_EleTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocation_EleTabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoLocationActionPerformed(null);
    }//GEN-LAST:event_tblLocation_EleTabKeyPressed

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoVisit_LocTabActionPerformed(null);
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblElement_LocTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElement_LocTabKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoElement_LocTabActionPerformed(null);
    }//GEN-LAST:event_tblElement_LocTabKeyPressed

    private void treBrowsePhotoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treBrowsePhotoValueChanged
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            btnReport.setVisible(false);
            txtBrowseInfo.setText("");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            imageIndex = 0;
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtBrowseInfo.setText(tempLocation.toHTML(false, false, app, UtilsHTML.ImageExportTypes.ForHTML)
                        .replace( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>", ""));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + tempLocation.getName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtBrowseInfo.setText(tempElement.toHTML(false, false, app, UtilsHTML.ImageExportTypes.ForHTML)
                        .replace( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>", ""));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + tempElement.getPrimaryName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtBrowseInfo.setText(tempVisit.toHTML(false, false, app, UtilsHTML.ImageExportTypes.ForHTML)
                        .replace( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>", ""));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("VISIT-" + tempVisit.getName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                txtBrowseInfo.setText(tempSighting.toHTML(false, false, app, UtilsHTML.ImageExportTypes.ForHTML)
                        .replace( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>", ""));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                setupFile(fotos);
            }
            else {
                setupFile(null);
            }
            if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
                btnReport.setVisible(true);
            }
            // Maak paar display issues reg
            txtBrowseInfo.getCaret().setDot(0);
        }
    }//GEN-LAST:event_treBrowsePhotoValueChanged

    private void rdbBrowseLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseLocationItemStateChanged
        if (rdbBrowseLocation.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            cmbElementTypesBrowseTab.setVisible(false);
            chkElementTypeBrowseTab.setVisible(false);
            btnReport.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='rgb(255,255,255)'></body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                lblNumberOfImages.setText("");
                // Scale image
                if (imgBrowsePhotos.getImage() != null) {
                    if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
                    else
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByLocation();
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
}//GEN-LAST:event_rdbBrowseLocationItemStateChanged

    private void rdbBrowseElementItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseElementItemStateChanged
        if (rdbBrowseElement.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            chkElementTypeBrowseTab.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(true);
            btnReport.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='rgb(255,255,255)'></body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                lblNumberOfImages.setText("");
                // Scale image
                if (imgBrowsePhotos.getImage() != null) {
                    if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
                    else
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByElement();
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_rdbBrowseElementItemStateChanged

    private void rdbBrowseDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseDateItemStateChanged
        if (rdbBrowseDate.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dtpStartDate.setVisible(true);
            dtpEndDate.setVisible(true);
            btnRefreshDates.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(false);
            chkElementTypeBrowseTab.setVisible(false);
            btnReport.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='rgb(255,255,255)'></body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                lblNumberOfImages.setText("");
                // Scale image
                if (imgBrowsePhotos.getImage() != null) {
                    if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
                    else
                        imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByDate();
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_rdbBrowseDateItemStateChanged

    private void btnGoBrowseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoBrowseSelectionActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelLocation tempPanel = UtilPanelGenerator.getLocationPanel(tempLocation.getName());
                UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelElement tempPanel = UtilPanelGenerator.getElementPanel(tempElement.getPrimaryName());
                UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelVisit tempPanel = UtilPanelGenerator.getVisitPanel(app.getDBI().find(new Location(tempVisit.getLocationName())), tempVisit.getName());
                UtilPanelGenerator.addPanelAsTab(tempPanel, tabbedPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                PanelSighting dialog = new PanelSighting(
                        app.getMainFrame(), "Edit an Existing Sighting",
                        tempSighting,
                        app.getDBI().find(new Location(tempSighting.getLocationName())),
                        app.getDBI().find(new Visit(tempSighting.getVisitName())),
                        app.getDBI().find(new Element(tempSighting.getElementName())),
                        this,
                        false, false, false);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnGoBrowseSelectionActionPerformed

    @Override
    public void refreshTableForSightings() {
        tabFotoComponentShown(null);
    }

    private void btnViewImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location temp = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile("LOCATION-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element temp = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile("ELEMENT-" + temp.getPrimaryName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit temp = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile("VISIT-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper temp = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile("SIGHTING-" + temp.getSighting().getSightingCounter(), imageIndex, app);
            }
        }
    }//GEN-LAST:event_btnViewImageActionPerformed

    private void btnBrowsePrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowsePrevActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + tempLocation.getName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + tempElement.getPrimaryName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("VISIT-" + tempVisit.getName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                loadPrevFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowsePrevActionPerformed

    private void btnBrowseNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseNextActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + tempLocation.getName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + tempElement.getPrimaryName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("VISIT-" + tempVisit.getName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                loadNextFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowseNextActionPerformed

    private void btnRefreshDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDatesActionPerformed
        rdbBrowseDateItemStateChanged(null);
    }//GEN-LAST:event_btnRefreshDatesActionPerformed

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocation_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoVisit_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void tblElement_LocTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElement_LocTabMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElement_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblElement_LocTabMouseClicked

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblLocation_EleTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocation_EleTabMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocation_EleTabMouseClicked

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                ReportingDialog dialog = new ReportingDialog(app.getMainFrame(), tempLocation, null, null, null, null);
                dialog.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                ReportingDialog dialog = new ReportingDialog(app.getMainFrame(), null, tempElement, null, null, null);
                dialog.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                ReportingDialog dialog = new ReportingDialog(app.getMainFrame(), null, null, tempVisit, null, null);
                dialog.setVisible(true);
            }
        }
        // TODO: Die sighting reports is bietjie kripties versteek, probeer dit dalk erns anders ook meer duidelik wys
        if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            ReportingDialog dialog = new ReportingDialog(app.getMainFrame(), null, null, null, dtpStartDate.getDate(), dtpEndDate.getDate());
            dialog.setVisible(true);
        }
}//GEN-LAST:event_btnReportActionPerformed

    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location temp = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage("LOCATION-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element temp = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage("ELEMENT-" + temp.getPrimaryName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit temp = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage("VISIT-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper temp = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage("SIGHTING-" + temp.getSighting().getSightingCounter(), imageIndex, app);
            }
            imageIndex--;
            btnBrowseNextActionPerformed(evt);
        }
    }//GEN-LAST:event_btnDefaultActionPerformed

    private void chkElementTypeBrowseTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkElementTypeBrowseTabActionPerformed
        searchElementBrowseTab = new Element();
        cmbElementTypesBrowseTab.setEnabled(chkElementTypeBrowseTab.isSelected());
        if (cmbElementTypesBrowseTab.isEnabled())
            searchElementBrowseTab.setType((ElementType)cmbElementTypesBrowseTab.getSelectedItem());
        browseByElement();
    }//GEN-LAST:event_chkElementTypeBrowseTabActionPerformed

    private void cmbElementTypesBrowseTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypesBrowseTabActionPerformed
        searchElementBrowseTab.setType((ElementType)cmbElementTypesBrowseTab.getSelectedItem());
        browseByElement();
    }//GEN-LAST:event_cmbElementTypesBrowseTabActionPerformed

    private void chkMnuUseWMSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseWMSItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setIsOnlinemapTheDefault(chkMnuUseWMS.isSelected());
        app.setWildLogOptions(options);
    }//GEN-LAST:event_chkMnuUseWMSItemStateChanged

    private void btnViewEXIFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewEXIFActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location temp = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup("LOCATION-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element temp = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup("ELEMENT-" + temp.getPrimaryName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit temp = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup("VISIT-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper temp = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup("SIGHTING-" + temp.getSighting().getSightingCounter(), imageIndex, app);
            }
        }
    }//GEN-LAST:event_btnViewEXIFActionPerformed

    private void mnuMapStartMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMapStartMenuItemActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        app.getMainFrame().getGlassPane().setVisible(true);
        String inputLat = JOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default Latitude to use for the map. (As decimal degrees, for example -33.4639)",
                options.getDefaultLatitude());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (inputLat != null) {
            try {
                options.setDefaultLatitude(Double.parseDouble(inputLat));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.getMainFrame().getGlassPane().setVisible(true);
        String inputLon = JOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default Longitude to use for the map. (As decimal degrees, for example 20.9562)",
                options.getDefaultLongitude());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (inputLon != null) {
            try {
                options.setDefaultLongitude(Double.parseDouble(inputLon));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptions(options);
    }//GEN-LAST:event_mnuMapStartMenuItemActionPerformed

    private void mnuExifMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExifMenuItemActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = UtilsFileProcessing.getExtension(f);
                if (extension != null) {
                    if (extension.equalsIgnoreCase(UtilsFileProcessing.jpeg) ||
                        extension.equalsIgnoreCase(UtilsFileProcessing.jpg)) {
                            return true;
                    }
                }
                return false;
            }
            @Override
            public String getDescription() {
                return "JPG Images";
            }
        });
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return fileChooser.showOpenDialog(app.getMainFrame());
                }
            });
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            UtilsDialog.showExifPopup(fileChooser.getSelectedFile());
        }
    }//GEN-LAST:event_mnuExifMenuItemActionPerformed

    private void mnuSetSlideshowSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetSlideshowSizeActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        app.getMainFrame().getGlassPane().setVisible(true);
        String inputFramerate = JOptionPane.showInputDialog(this.getComponent(),
                "Please specify the default frame size to use for the slideshows. \n (This can be any positive decimal value, for example 500)",
                options.getDefaultSlideshowSize());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (inputFramerate != null) {
            try {
                options.setDefaultSlideshowSize(Math.abs(Integer.parseInt(inputFramerate)));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptions(options);
    }//GEN-LAST:event_mnuSetSlideshowSizeActionPerformed

    private void mnuSetSlideshowSpeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetSlideshowSpeedActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        app.getMainFrame().getGlassPane().setVisible(true);
        String inputFramerate = JOptionPane.showInputDialog(this.getComponent(),
                "Please specify the default framerate to use for the slideshows. \n (This can be any positive decimal value, for example 1 or 0.3)",
                options.getDefaultSlideshowSpeed());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (inputFramerate != null) {
            try {
                options.setDefaultSlideshowSpeed(Math.abs(Float.parseFloat(inputFramerate)));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptions(options);
    }//GEN-LAST:event_mnuSetSlideshowSpeedActionPerformed

    private void mnuGPSInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGPSInputActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        app.getMainFrame().getGlassPane().setVisible(true);
        int latOption = JOptionPane.showOptionDialog(
                this.getComponent(),
                "Please select the Default Latitude to use for GPS input:",
                "Default GPS Input Latitude",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                Latitudes.values(),
                options.getDefaultInputLatitude());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (latOption != JOptionPane.CLOSED_OPTION) {
            options.setDefaultInputLatitude(Latitudes.values()[latOption]);
        }
        app.getMainFrame().getGlassPane().setVisible(true);
        int lonOption = JOptionPane.showOptionDialog(
                this.getComponent(),
                "Please select the Default Longitude to use for GPS input:",
                "Default GPS Input Longitude",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                Longitudes.values(),
                options.getDefaultInputLongitude());
        app.getMainFrame().getGlassPane().setVisible(false);
        if (lonOption != JOptionPane.CLOSED_OPTION) {
            options.setDefaultInputLongitude(Longitudes.values()[lonOption]);
        }
        app.setWildLogOptions(options);
    }//GEN-LAST:event_mnuGPSInputActionPerformed

    private void linkElementsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkElementsMenuItemActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your data (WildLog folder) before continuing. <br>Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MergeElementsDialog dialog = new MergeElementsDialog();
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_linkElementsMenuItemActionPerformed

    private void moveVisitsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveVisitsMenuItemActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your data (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MoveVisitDialog dialog = new MoveVisitDialog();
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_moveVisitsMenuItemActionPerformed

    private void calcSunMoonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcSunMoonMenuItemActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>Please backup your data before proceding. <br>"
                        + "This will replace the Sun and Moon information for all your Sightings with the auto generated values.</html>",
                        "Calculate Sun and Moon Information",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    messageTimer.stop();
                    setMessage("Starting the Sun and Moon Calculation");
                    setProgress(0);
                    tabbedPanel.setSelectedIndex(0);
                    while (tabbedPanel.getTabCount() > 4) {
                        tabbedPanel.remove(4);
                    }
                    List<Sighting> sightings = app.getDBI().list(new Sighting());
                    for (int t = 0; t < sightings.size(); t++) {
                        Sighting sighting = sightings.get(t);
                        sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                        if (!Latitudes.NONE.equals(sighting.getLatitude()) && !Longitudes.NONE.equals(sighting.getLongitude()) && !sighting.isTimeUnknown()) {
                            double lat = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                            double lon = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                            sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), lat, lon));
                            sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), lat, lon));
                            app.getDBI().createOrUpdate(sighting);
                        }
                        setProgress(0 + (int)((t/(double)sightings.size())*100));
                        setMessage("Sun and Moon Calculation: " + getProgress() + "%");
                    }
                    setProgress(100);
                    setMessage("Done with the Sun and Moon Calculation");
                    messageTimer.start();
                    return null;
                }
            });
        }
    }//GEN-LAST:event_calcSunMoonMenuItemActionPerformed

    private void bulkImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bulkImportMenuItemActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                BulkUploadPanel bulkUploadPanel = new BulkUploadPanel(this, null);
                UtilPanelGenerator.addPanelAsTab(bulkUploadPanel, tabbedPanel);
                return null;
            }
        });
    }//GEN-LAST:event_bulkImportMenuItemActionPerformed

    private void csvImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvImportMenuItemActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                messageTimer.stop();
                setMessage("Starting the CSV Import");
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select the directory with the CSV files to import");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            return fileChooser.showOpenDialog(app.getMainFrame());
                        }
                    });
                if (result == JFileChooser.APPROVE_OPTION) {
                    tabbedPanel.setSelectedIndex(0);
                    String path = fileChooser.getSelectedFile().getPath() + File.separatorChar;
                    app.getMainFrame().getGlassPane().setVisible(true);
                    String prefix = JOptionPane.showInputDialog(app.getMainFrame(),
                            "<html>Please provide a prefix to use for the imported data. "
                            + "<br>The prefix will be chosen to map the data to unique new records. "
                            + "<br>You should manually merge Creatures and move Visits afterwards.</html>",
                            "Import CSV Data", JOptionPane.QUESTION_MESSAGE);
                    if (prefix != null && !prefix.isEmpty()) {
                    app.getMainFrame().getGlassPane().setVisible(false);
                        if (!app.getDBI().doImportCSV(path, prefix)) {
                            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                @Override
                                public int showDialog() {
                                    JOptionPane.showMessageDialog(app.getMainFrame(),
                                            "Not all of the data could be successfully imported.",
                                            "Error Importing From CSV!", JOptionPane.ERROR_MESSAGE);
                                    return -1;
                                }
                            });
                        }
                    }
                }
                setMessage("Done with the CSV Import");
                messageTimer.start();
                return null;
            }
        });
    }//GEN-LAST:event_csvImportMenuItemActionPerformed

    private void mnuCreateSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateSlideshowActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Jpeg images", "jpg", "jpeg", "JPG", "JPEG"));
        fileChooser.setDialogTitle("Select the images to use for the slideshow...");
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return fileChooser.showOpenDialog(app.getMainFrame());
                }
            });
        if (result == JFileChooser.APPROVE_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Creating the Slideshow");
                    List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
                    List<String> fileNames = new ArrayList<String>(files.size());
                    for (File tempFile : files) {
                        fileNames.add(tempFile.getAbsolutePath());
                    }
                    fileChooser.setDialogTitle("Please select where to save the slideshow...");
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("movie.mov"));
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Slideshow movie", "mov"));
                    int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                return fileChooser.showSaveDialog(app.getMainFrame());
                            }
                        });
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // Now create the slideshow
                        setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                        String outputFile = fileChooser.getSelectedFile().getPath().substring(2);
                        UtilsMovies.generateSlideshow(fileNames, app, outputFile);
                        setMessage("Done with the Slideshow");
                    }
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuCreateSlideshowActionPerformed

    private void mnuDBConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDBConsoleActionPerformed
        UtilsFileProcessing.openFile(System.getProperty("user.dir") + "/lib/h2-1.3.168.jar");
    }//GEN-LAST:event_mnuDBConsoleActionPerformed

    private void mnuOpenMapAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenMapAppActionPerformed
        UtilsFileProcessing.openFile(System.getProperty("user.dir") + "/lib/openmap.jar");
    }//GEN-LAST:event_mnuOpenMapAppActionPerformed

    private void csvExportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvExportMenuItemActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                messageTimer.stop();
                setMessage("Starting the CSV Export");
                String path = WildLogPaths.WILDLOG_EXPORT_CSV.getFullPath();
                File tempFile = new File(path);
                tempFile.mkdirs();
                app.getDBI().doExportCSV(path);
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_CSV.getFullPath());
                setMessage("Done with the CSV Export");
                messageTimer.start();
                return null;
            }
        });
    }//GEN-LAST:event_csvExportMenuItemActionPerformed

    private void htmlExportMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_htmlExportMenuItem1ActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                messageTimer.stop();
                setMessage("Starting the HTML Export");
                setProgress(0);
                List<Element> listElements = app.getDBI().list(new Element());
                for (int t = 0; t < listElements.size(); t++) {
                    UtilsHTML.exportHTML(listElements.get(t), app);
                    setProgress(0 + (int)((t/(double)listElements.size())*50));
                    setMessage("HTML Export: " + getProgress() + "%");
                }
                setProgress(50);
                setMessage("HTML Export: " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().list(new Location());
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsHTML.exportHTML(listLocations.get(t), app);
                    setProgress(50 + (int)((t/(double)listLocations.size())*50));
                    setMessage("HTML Export: " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("HTML Export: " + getProgress());
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath());
                setMessage("Done with the HTML Export");
                messageTimer.start();
                return null;
            }
        });
    }//GEN-LAST:event_htmlExportMenuItem1ActionPerformed

    private void kmlExportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kmlExportMenuItemActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                messageTimer.stop();
                setMessage("Starting the KML Export");
                setProgress(0);
                // First do the HTML export to generate the Images in the right place
                List<Element> listElements = app.getDBI().list(new Element());
                for (int t = 0; t < listElements.size(); t++) {
                    UtilsHTML.exportHTML(listElements.get(t), app);
                    setProgress(0 + (int)((t/(double)listElements.size())*32));
                    setMessage("KML Export: " + getProgress() + "%");
                }
                List<Location> listLocations = app.getDBI().list(new Location());
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsHTML.exportHTML(listLocations.get(t), app);
                    setProgress(32 + (int)((t/(double)listElements.size())*32));
                    setMessage("KML Export: " + getProgress() + "%");
                }
                setProgress(65);
                setMessage("KML Export: " + getProgress() + "%");
                // Then do KML export
                String path = WildLogPaths.WILDLOG_EXPORT_KML.getFullPath();
                File tempFile = new File(path);
                tempFile.mkdirs();
                // Make sure icons exist in the KML folder
                UtilsKML.copyKmlIcons(app, path);
                // KML Stuff
                KmlGenerator kmlgen = new KmlGenerator();
                kmlgen.setKmlPath(path + "WildLogMarkers.kml");
                // Get entries for Sightings and Locations
                Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
                setProgress(70);
                setMessage("KML Export: " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().list(new Sighting());
                for (int t = 0; t < listSightings.size(); t++) {
                    String key = listSightings.get(t).getElementName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>());
                     }
                    entries.get(key).add(listSightings.get(t).toKML(t, app));
                    setProgress(70 + (int)((t/(double)listElements.size())*20));
                    setMessage("KML Export: " + getProgress() + "%");
                }
                // Locations
                for (int t = 0; t < listLocations.size(); t++) {
                    String key = listLocations.get(t).getName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>());
                     }
                    entries.get(key).add(listLocations.get(t).toKML(listSightings.size() + t, app));
                    setProgress(90 + (int)((t/(double)listElements.size())*10));
                    setMessage("KML Export: " + getProgress() + "%");
                }
                // Generate KML
                kmlgen.generateFile(entries, UtilsKML.getKmlStyles());
                // Try to open the Kml file
                UtilsFileProcessing.openFile(path + "WildLogMarkers.kml");
                setProgress(100);
                setMessage("Done with the KML Export");
                messageTimer.start();
                return null;
            }
        });
    }//GEN-LAST:event_kmlExportMenuItemActionPerformed

    private void mnuSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSunAndMoonActionPerformed
        SunMoonDialog dialog = new SunMoonDialog(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuSunAndMoonActionPerformed

    private void mnuBackupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupMenuItemActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                messageTimer.stop();
                setMessage("Starting the Database Backup");
                app.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS);
                setMessage("Done with the Database Backup");
                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "<html>The backup can be found in the 'WildLog\\Backup\\Backup (date)\\' folder. <br>(Note: This only backed up the database entries, the images and other files have to be backed up manually.)</html>",
                                "Backup Completed", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                });
                messageTimer.start();
                return null;
            }
        });
    }//GEN-LAST:event_mnuBackupMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        app.quit(evt);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void mnuAboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutMenuItemActionPerformed
        if (aboutBox == null) {
            aboutBox = new WildLogAboutBox();
        }
        WildLogApp.getApplication().show(aboutBox);
    }//GEN-LAST:event_mnuAboutMenuItemActionPerformed

    private void mnuChangeWorkspaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuChangeWorkspaceMenuItemActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the new or existing Workspace Folder.");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(app.getMainFrame());
            }
        });
        if (result == JFileChooser.APPROVE_OPTION) {
            // Write first
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(
                        WildLogPaths.concatPaths(System.getProperty("user.home"),"WildLog Settings","wildloghome")));
                String path = fileChooser.getSelectedFile().getPath();
                if (path.toLowerCase().endsWith(WildLogPaths.WILDLOG.toString().toLowerCase().substring(1, WildLogPaths.WILDLOG.toString().length() - 1))) {
                    // The name might be tricky to parse if it ends with WildLog so we have to do some extra checks...
                    // Because the user can selecte either c:\MyWildLog(\WildLog\Data) or c:\MyWildLog\WildLog(\Data)...
                    // I'll use the Data folder to test for the actual WildLog folder structure
                    File testFile = new File(path + WildLogPaths.WILDLOG_DATA.toString().replace(WildLogPaths.WILDLOG.toString(), File.separator));
                    if (testFile.exists() && testFile.isDirectory()) {
                        // I assume the user selected the WildLog folder and we need to strip it from the path
                        path = path.substring(0, path.length() - (WildLogPaths.WILDLOG.toString().length() - 1)) + File.separator;
                    }
                }
                writer.write(path);
                writer.flush();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                if (writer != null)
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
            }
            // Then try to read
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new FileReader(System.getProperty("user.home") + File.separator + "WildLog Settings" + File.separator + "wildloghome"));
                WildLogPaths.setWorkspacePrefix(reader.readLine());
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "Could not change the Workspace Folder.",
                                "Error!", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
            }
            finally {
                if (reader != null)
                    try {
                        reader.close();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
            }
            // Shutdown
            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(app.getMainFrame(),
                            "The Workspace Folder has been changed. Please restart the application.",
                            "Done!", JOptionPane.INFORMATION_MESSAGE);
                    return -1;
                }
            });
            app.quit(null);
        }
    }//GEN-LAST:event_mnuChangeWorkspaceMenuItemActionPerformed

    private void mnuCleanWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCleanWorkspaceActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is <b>HIGHLY</b> recommended to backup the entire WildLog folder before you continue! <br>"
                        + "This task will check that all links between the data and files are correct. <br>"
                        + "In addition all unnessasary files will be removed from the Workspace.</html>",
                        "Warning!",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            FileWriter feedback = null;
            try {
                feedback = new FileWriter(new File(WildLogPaths.concatPaths(WildLogPaths.WILDLOG.getFullPath()), "WorkspaceCleanupFeedback.txt"));
                // Lock the input/display and show busy message
                // Note: we never remove the Busy dialog and greyed out background since the app wil be restarted anyway when done
                tabbedPanel.setSelectedIndex(0);
                JDialog dialog = new JDialog(app.getMainFrame());
                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension(400, 50));
                panel.setBorder(new LineBorder(new Color(235, 90, 80), 3));
                JLabel label = new JLabel("<html>Busy cleaning workspace. <br>Please be patient and don't close the application until the process is finished.</html>");
                panel.add(label);
                dialog.add(panel);
                dialog.setModal(true);
                dialog.setUndecorated(true);
                dialog.pack();
                UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), dialog);
                UtilsDialog.setDialogToCenter(app.getMainFrame(), dialog);
                dialog.setVisible(true);
                // Check database files
                feedback.write("1) Make sure all files int he database are present.");
//                Files.m
                List<WildLogFile> files = app.getDBI().list(new WildLogFile());
                for (final WildLogFile file : files) {
    //                if (!new File(file.getFileLocation(true)).isFile()) {
                        UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
    //                            JOptionPane.showMessageDialog(app.getMainFrame(),
    //                                    "The file does not exist: " + file.getFileLocation(true),
    //                                    "Can't Find File!", JOptionPane.ERROR_MESSAGE);
                                return -1;
                            }
                        });
    //                }
                    if (!new File(file.getOriginalFotoLocation(true)).isFile()) {
                        UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                JOptionPane.showMessageDialog(app.getMainFrame(),
                                        "The file does not exist: " + file.getOriginalFotoLocation(true),
                                        "Can't Find File!", JOptionPane.ERROR_MESSAGE);
                                return -1;
                            }
                        });
                    }
                }
    //            // Delete temporary folders
    //            try {
    //                UtilsFileProcessing.deleteRecursive(new File(WildLogPaths.WILDLOG_EXPORT.getFullPath()));
    //            }
    //            catch (final IOException ex) {
    //                ex.printStackTrace(System.err);
    //                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
    //                    @Override
    //                    public int showDialog() {
    //                        JOptionPane.showMessageDialog(app.getMainFrame(),
    //                                ex.getMessage(),
    //                                "Can't Delete File!", JOptionPane.ERROR_MESSAGE);
    //                        return -1;
    //                    }
    //                });
    //            }
    //            // Check for unused empty folders
    //            try {
    //                UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(new File(WildLogPaths.WILDLOG_IMAGES.getFullPath()));
    //                UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(new File(WildLogPaths.WILDLOG_MOVIES.getFullPath()));
    //                UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(new File(WildLogPaths.WILDLOG_OTHER.getFullPath()));
    //            }
    //            catch (final IOException ex) {
    //                ex.printStackTrace(System.err);
    //                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
    //                    @Override
    //                    public int showDialog() {
    //                        JOptionPane.showMessageDialog(app.getMainFrame(),
    //                                ex.getMessage(),
    //                                "Can't Delete Folder!", JOptionPane.ERROR_MESSAGE);
    //                        return -1;
    //                    }
    //                });
    //            }
    //            app.getMainFrame().getGlassPane().setVisible(false);
    //            // Done
    //            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
    //                @Override
    //                public int showDialog() {
    //                    JOptionPane.showMessageDialog(app.getMainFrame(),
    //                            "Finished checking and cleaning the Workspace Folder. Please restart the application.",
    //                            "Done!", JOptionPane.INFORMATION_MESSAGE);
    //                    return -1;
    //                }
    //            });
    //            // Open the summary document
    //            UtilsFileProcessing.openFile(null);
    //            // Close the application to be safe (make sure no wierd references/paths are still used, etc.)
    //            app.quit(null);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                try {
                    feedback.flush();
                    feedback.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }//GEN-LAST:event_mnuCleanWorkspaceActionPerformed

    private void browseByLocation() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Location> locations = new ArrayList<Location>(app.getDBI().list(new Location()));
        Collections.sort(locations);
        for (Location tempLocation : locations) {
            DefaultMutableTreeNode tempLocationNode = new DefaultMutableTreeNode(tempLocation);
            root.add(tempLocationNode);
            Visit temp = new Visit();
            temp.setLocationName(tempLocation.getName());
            List<Visit> visits = app.getDBI().list(temp);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                DefaultMutableTreeNode tempVisitNode = new DefaultMutableTreeNode(tempVisit);
                tempLocationNode.add(tempVisitNode);
                Sighting tempSi = new Sighting();
                tempSi.setVisitName(tempVisit.getName());
                List<Sighting> sightings = app.getDBI().list(tempSi);
                Collections.sort(sightings);
                for (Sighting tempSighting : sightings) {
                    DefaultMutableTreeNode tempSightingNode = new DefaultMutableTreeNode(new SightingWrapper(tempSighting, true));
                    tempVisitNode.add(tempSightingNode);
                    tempSightingNode.add(new DefaultMutableTreeNode(app.getDBI().find(new Element(tempSighting.getElementName()))));
                }
            }
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void browseByElement() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        if (searchElementBrowseTab == null) searchElementBrowseTab = new Element();
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Element> elements = new ArrayList<Element>(app.getDBI().list(searchElementBrowseTab));
        Collections.sort(elements);
        for (Element tempElement : elements) {
            DefaultMutableTreeNode tempElementNode = new DefaultMutableTreeNode(tempElement);
            root.add(tempElementNode);
            Sighting templateSighting = new Sighting();
            templateSighting.setElementName(tempElement.getPrimaryName());
            // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
            List<Sighting> sightings = new ArrayList<Sighting>(app.getDBI().list(templateSighting));
            Collections.sort(sightings);
            for (Sighting tempSighting : sightings) {
                DefaultMutableTreeNode tempSightingNode = new DefaultMutableTreeNode(new SightingWrapper(tempSighting, false));
                tempElementNode.add(tempSightingNode);
                // Add Location and Element under the sighting node
                tempSightingNode.add(new DefaultMutableTreeNode(app.getDBI().find(new Location(tempSighting.getLocationName()))));
                tempSightingNode.add(new DefaultMutableTreeNode(app.getDBI().find(new Visit(tempSighting.getVisitName()))));
            }
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void browseByDate() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        if (dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
            List<Sighting> sightings = new ArrayList<Sighting>(app.getDBI().searchSightingOnDate(dtpStartDate.getDate(), dtpEndDate.getDate()));
            Collections.sort(sightings);
            for (Sighting tempSighting : sightings) {
                DefaultMutableTreeNode tempSightingNode = new DefaultMutableTreeNode(new SightingWrapper(tempSighting, true));
                root.add(tempSightingNode);
                DefaultMutableTreeNode tempLocationNode = new DefaultMutableTreeNode(app.getDBI().find(new Location(tempSighting.getLocationName())));
                tempSightingNode.add(tempLocationNode);
                DefaultMutableTreeNode tempElementNode = new DefaultMutableTreeNode(app.getDBI().find(new Element(tempSighting.getElementName())));
                tempSightingNode.add(tempElementNode);
            }
            btnReport.setVisible(true);
        }
        else {
            root.add(new DefaultMutableTreeNode("Please select dates first"));
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void loadPrevFile(List<WildLogFile> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex--;
            if (imageIndex < 0) imageIndex = inFotos.size() - 1;
            setupFile(inFotos);
        }
        else {
            noFiles();
        }
    }

    private void loadNextFile(List<WildLogFile> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex++;
            if (imageIndex >= inFotos.size()) imageIndex = 0;
            setupFile(inFotos);
        }
        else {
            noFiles();
        }
    }

    private void noFiles() {
        try {
            imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
            lblNumberOfImages.setText("0 of 0");
            imgBrowsePhotos.setToolTipText("");
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void setupFile(final List<WildLogFile> inFotos) {
        if (inFotos != null) {
            if (inFotos.size() > 0) {
                try {
                    lblNumberOfImages.setText(imageIndex+1 + " of " + inFotos.size());
                    if (inFotos.get(imageIndex).getFotoType().equals(WildLogFileType.IMAGE)) {
//                        int size = (int)imgBrowsePhotos.getSize().getWidth();
//                        if (imgBrowsePhotos.getSize().getHeight() > size)
//                            size = (int)imgBrowsePhotos.getSize().getHeight();
//                        imgBrowsePhotos.setImage(UtilsFileProcessing.getScaledIcon(new File(inFotos.get(imageIndex).getOriginalFotoLocation(true)), size).getImage());
                        imgBrowsePhotos.setImage(new File(inFotos.get(imageIndex).getOriginalFotoLocation(true)));
                    }
                    else
                    if (inFotos.get(imageIndex).getFotoType().equals(WildLogFileType.MOVIE))
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/Movie.png"));
                    else
                    if (inFotos.get(imageIndex).getFotoType().equals(WildLogFileType.OTHER))
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/OtherFile.png"));
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    imgBrowsePhotos.setToolTipText(inFotos.get(imageIndex).getFilename());
                }
            }
            else {
                try {
                    imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                    lblNumberOfImages.setText("0 of 0");
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    imgBrowsePhotos.setToolTipText("");
                }
            }
        }
        else {
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                imgBrowsePhotos.setToolTipText("");
            }
        }
        // Scale image
        if (imgBrowsePhotos.getImage() != null) {
            double scale = 1.0;
            int imageHeight = imgBrowsePhotos.getImage().getHeight(null);
            int imageWidth = imgBrowsePhotos.getImage().getWidth(null);
            double componentHeight = imgBrowsePhotos.getSize().getHeight();
            double componentWidth = imgBrowsePhotos.getSize().getWidth();
            if (imageHeight > 0 && imageWidth > 0) {
                if (imageHeight >= imageWidth) {
                    // Dealing with Portrait image
                    scale = componentHeight / (double)imageHeight;
                    if (imageWidth * scale > componentWidth)
                        scale = componentWidth / (double)imageWidth;
                }
                else {
                    // Dealing with Landscape image
                    scale = componentWidth / (double)imageWidth;
                    if (imageHeight * scale > componentHeight)
                        scale = componentHeight / (double)imageHeight;
                }
            }
            else {
                System.out.println("WARNING: Trying to get the size of an image before it is known...");
            }
            imgBrowsePhotos.setScale(scale);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu advancedMenu;
    private javax.swing.JMenu backupMenu;
    private javax.swing.JButton btnAddElement;
    private javax.swing.JButton btnAddLocation;
    private javax.swing.JButton btnBrowseNext;
    private javax.swing.JButton btnBrowsePrev;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnDeleteElement;
    private javax.swing.JButton btnDeleteLocation;
    private javax.swing.JButton btnGoBrowseSelection;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoElement_LocTab;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnGoLocation_LocTab;
    private javax.swing.JButton btnGoVisit_LocTab;
    private javax.swing.JButton btnRefreshDates;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnRotate;
    private javax.swing.JButton btnViewEXIF;
    private javax.swing.JButton btnViewImage;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JMenuItem bulkImportMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem calcSunMoonMenuItem;
    private javax.swing.JCheckBox chkElementTypeBrowseTab;
    private javax.swing.JCheckBoxMenuItem chkMnuUseWMS;
    private javax.swing.JComboBox cmbElementTypesBrowseTab;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JMenuItem csvExportMenuItem;
    private javax.swing.JMenuItem csvImportMenuItem;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu extraMenu;
    private javax.swing.JMenuItem htmlExportMenuItem1;
    private org.jdesktop.swingx.JXImageView imgBrowsePhotos;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JMenuItem kmlExportMenuItem;
    private javax.swing.JLabel lblCreatures;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblImage_LocTab;
    private javax.swing.JLabel lblLocations;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblSightings;
    private javax.swing.JLabel lblVisits;
    private javax.swing.JLabel lblWorkspace;
    private javax.swing.JMenuItem linkElementsMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mappingMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuBackupMenuItem;
    private javax.swing.JMenuItem mnuChangeWorkspaceMenuItem;
    private javax.swing.JMenuItem mnuCleanWorkspace;
    private javax.swing.JMenuItem mnuCreateSlideshow;
    private javax.swing.JMenuItem mnuDBConsole;
    private javax.swing.JMenuItem mnuExifMenuItem;
    private javax.swing.JMenuItem mnuGPSInput;
    private javax.swing.JMenuItem mnuMapStartMenuItem;
    private javax.swing.JMenuItem mnuOpenMapApp;
    private javax.swing.JMenuItem mnuSetSlideshowSize;
    private javax.swing.JMenuItem mnuSetSlideshowSpeed;
    private javax.swing.JMenuItem mnuSunAndMoon;
    private javax.swing.JMenuItem moveVisitsMenuItem;
    private javax.swing.JMenu otherMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdbBrowseDate;
    private javax.swing.JRadioButton rdbBrowseElement;
    private javax.swing.JRadioButton rdbBrowseLocation;
    private javax.swing.JScrollPane scrlElement;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JMenu slideshowMenu;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu subMenu1;
    private javax.swing.JMenu subMenu3;
    private javax.swing.JPanel tabElement;
    private javax.swing.JPanel tabFoto;
    private javax.swing.JPanel tabHome;
    private javax.swing.JPanel tabLocation;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblElement_LocTab;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblLocation_EleTab;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTree treBrowsePhoto;
    private javax.swing.JTextPane txtBrowseInfo;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JMenu workspaceMenu;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
