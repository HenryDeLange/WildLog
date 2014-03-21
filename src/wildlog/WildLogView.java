package wildlog;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.application.TaskMonitor;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.data.utils.WildLogConstants;
import wildlog.html.utils.UtilsHTML;
import wildlog.mapping.kml.utils.UtilsKML;
import wildlog.mapping.utils.UtilsGps;
import wildlog.movies.utils.UtilsMovies;
import wildlog.ui.dialogs.MergeElementsDialog;
import wildlog.ui.dialogs.MergeLocationDialog;
import wildlog.ui.dialogs.MergeVisitDialog;
import wildlog.ui.dialogs.MoveVisitDialog;
import wildlog.ui.dialogs.SunMoonDialog;
import wildlog.ui.dialogs.WildLogAboutBox;
import wildlog.ui.dialogs.WildNoteAboutBox;
import wildlog.ui.dialogs.WorkspaceExportDialog;
import wildlog.ui.dialogs.WorkspaceImportDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.filters.CsvFilter;
import wildlog.ui.helpers.filters.WildNoteSyncFilter;
import wildlog.ui.panels.PanelTabBrowse;
import wildlog.ui.panels.PanelTabElements;
import wildlog.ui.panels.PanelTabLocations;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsCompression;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;

/**
 * The application's main frame.
 */
public final class WildLogView extends JFrame {
    private WildLogApp app;
    private PanelTabBrowse panelTabBrowse;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    public WildLogView(WildLogApp inApp) {
        app = inApp;
        // Call the generated code to build the GUI
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        int messageTimeout = 10000;
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = 30;
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = new ImageIcon(getClass().getResource("/wildlog/resources/busyicons/busy-icon" + i + ".png"));
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = new ImageIcon(getClass().getResource("/wildlog/resources/busyicons/idle-icon.png"));
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(app.getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                switch (propertyName) {
                    case "started":
                        if (!busyIconTimer.isRunning()) {
                            statusAnimationLabel.setIcon(busyIcons[0]);
                            busyIconIndex = 0;
                            busyIconTimer.start();
                        }
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(true);
                        messageTimer.stop();
                        break;
                    case "done":
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                        messageTimer.restart();
                        break;
                    case "message":
                        String text = (String)(evt.getNewValue());
                        statusMessageLabel.setText((text == null) ? "" : text);
                        messageTimer.stop();
                        break;
                    case "progress":
                        int value = (Integer)(evt.getNewValue());
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(value);
                        break;
                }
            }
        });
        // Setup the tab headers
        setupTabHeaderHome();
        setupTabHeaderBrowse();
        setupTabHeaderLocation();
        setupTabHeaderElement();
        // Set the minimum size of the frame
        this.setMinimumSize(new Dimension(1024, 705));
    }

    public void setupTabHeaderHome() {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon.gif"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel(""));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(0, "Home");
        tabbedPanel.setIconAt(0, icon);
        tabbedPanel.setTabComponentAt(0, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, 0);
    }

    public void setupTabHeaderBrowse() {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Browse.png"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Browse"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(1, "Browse");
        tabbedPanel.setIconAt(1, icon);
        tabbedPanel.setTabComponentAt(1, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, 1);
        // Setup content
        panelTabBrowse = new PanelTabBrowse(app, tabbedPanel);
        tabbedPanel.setComponentAt(1, panelTabBrowse);
    }

    public void setupTabHeaderLocation() {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/LocationList.gif"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Places"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(2, "Places");
        tabbedPanel.setIconAt(2, icon);
        tabbedPanel.setTabComponentAt(2, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, 2);
        // Setup content
        tabbedPanel.setComponentAt(2, new PanelTabLocations(app, tabbedPanel));
    }

    public void setupTabHeaderElement() {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/ElementList.gif"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Creatures"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(3, "Creatures");
        tabbedPanel.setIconAt(3, icon);
        tabbedPanel.setTabComponentAt(3, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, 3);
        // Setup content
        tabbedPanel.setComponentAt(3, new PanelTabElements(app, tabbedPanel));
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
        jLabel8 = new javax.swing.JLabel();
        tabBrowse = new javax.swing.JPanel();
        tabLocation = new javax.swing.JPanel();
        tabElement = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusAnimationLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        workspaceMenu = new javax.swing.JMenu();
        mnuChangeWorkspaceMenuItem = new javax.swing.JMenuItem();
        mnuCreateWorkspaceMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuCleanWorkspace = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem mnuExitApp = new javax.swing.JMenuItem();
        backupMenu = new javax.swing.JMenu();
        mnuBackupDatabase = new javax.swing.JMenuItem();
        mnuBackupWorkspace = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        mnuExportCSV = new javax.swing.JMenuItem();
        mnuExportHTML = new javax.swing.JMenuItem();
        mnuExportKML = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuExportWildNoteSync = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuExportWorkspace = new javax.swing.JMenuItem();
        importMenu = new javax.swing.JMenu();
        mnuImportCSV = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        btnImportIUCNList = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mnuImportWildNote = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        mnuImportWorkspace = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnuBulkImport = new javax.swing.JMenuItem();
        advancedMenu = new javax.swing.JMenu();
        mnuCalcSunMoon = new javax.swing.JMenuItem();
        mnuCalcDuration = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuMoveVisits = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuMergeLocations = new javax.swing.JMenuItem();
        mnuMergeVisit = new javax.swing.JMenuItem();
        mnuMergeElements = new javax.swing.JMenuItem();
        extraMenu = new javax.swing.JMenu();
        mnuExifMenuItem = new javax.swing.JMenuItem();
        mnuCreateSlideshow = new javax.swing.JMenuItem();
        mnuSunAndMoon = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        externalMenu = new javax.swing.JMenu();
        mnuDBConsole = new javax.swing.JMenuItem();
        mnuOpenMapApp = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        mappingMenu = new javax.swing.JMenu();
        chkMnuUseWMS = new javax.swing.JCheckBoxMenuItem();
        mnuMapStartMenuItem = new javax.swing.JMenuItem();
        otherMenu = new javax.swing.JMenu();
        mnuGPSInput = new javax.swing.JMenuItem();
        slideshowMenu = new javax.swing.JMenu();
        mnuSetSlideshowSpeed = new javax.swing.JMenuItem();
        mnuSetSlideshowSize = new javax.swing.JMenuItem();
        mnuPerformance = new javax.swing.JMenu();
        chkMnuUseIconTables = new javax.swing.JCheckBoxMenuItem();
        chkMnuBrowseWithThumbnails = new javax.swing.JCheckBoxMenuItem();
        mnuOther = new javax.swing.JMenu();
        chkMnuEnableSounds = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem mnuAboutWildLog = new javax.swing.JMenuItem();
        mnuAboutWildNote = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("WildLog v4.1");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon.gif")).getImage());

        mainPanel.setMaximumSize(new java.awt.Dimension(2500, 1300));
        mainPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(2500, 1300));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.LINE_AXIS));

        tabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPanel.setToolTipText("");
        tabbedPanel.setFocusable(false);
        tabbedPanel.setMaximumSize(new java.awt.Dimension(3500, 1800));
        tabbedPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.setName("tabbedPanel"); // NOI18N
        tabbedPanel.setPreferredSize(new java.awt.Dimension(1000, 630));

        tabHome.setBackground(new java.awt.Color(5, 26, 5));
        tabHome.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabHome.setName("tabHome"); // NOI18N
        tabHome.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabHome.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabHomeComponentShown(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(249, 250, 241));
        jLabel10.setText("Welcome to");
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(249, 245, 239));
        jLabel11.setText("WildLog");
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(237, 230, 221));
        jLabel12.setText("version 4.1");
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(186, 210, 159));
        jLabel15.setText("http://cameratrap.mywild.co.za");
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Feature 1.png"))); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblLocations.setForeground(new java.awt.Color(183, 195, 166));
        lblLocations.setText("Places:");
        lblLocations.setName("lblLocations"); // NOI18N

        lblVisits.setForeground(new java.awt.Color(183, 195, 166));
        lblVisits.setText("Periods:");
        lblVisits.setName("lblVisits"); // NOI18N

        lblSightings.setForeground(new java.awt.Color(183, 195, 166));
        lblSightings.setText("Observations:");
        lblSightings.setName("lblSightings"); // NOI18N

        lblCreatures.setForeground(new java.awt.Color(183, 195, 166));
        lblCreatures.setText("Creatures:");
        lblCreatures.setName("lblCreatures"); // NOI18N

        jSeparator5.setBackground(new java.awt.Color(57, 68, 43));
        jSeparator5.setForeground(new java.awt.Color(105, 123, 79));
        jSeparator5.setName("jSeparator5"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(107, 124, 89));
        jLabel5.setText("http://www.mywild.co.za");
        jLabel5.setName("jLabel5"); // NOI18N

        lblWorkspace.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblWorkspace.setForeground(new java.awt.Color(74, 87, 60));
        lblWorkspace.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWorkspace.setText("Active Workspace Folder: " + WildLogPaths.getFullWorkspacePrefix().toString());
        lblWorkspace.setName("lblWorkspace"); // NOI18N

        jSeparator6.setBackground(new java.awt.Color(163, 175, 148));
        jSeparator6.setForeground(new java.awt.Color(216, 227, 201));
        jSeparator6.setName("jSeparator6"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(115, 122, 107));
        jLabel8.setText("support@mywild.co.za");
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout tabHomeLayout = new javax.swing.GroupLayout(tabHome);
        tabHome.setLayout(tabHomeLayout);
        tabHomeLayout.setHorizontalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addContainerGap(846, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                        .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(jLabel11)
                                .addGap(16, 16, 16)
                                .addComponent(jLabel12))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                                .addGap(134, 134, 134)
                                .addComponent(lblLocations)
                                .addGap(18, 18, 18)
                                .addComponent(lblVisits)
                                .addGap(18, 18, 18)
                                .addComponent(lblCreatures)
                                .addGap(18, 18, 18)
                                .addComponent(lblSightings))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabHomeLayout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(lblWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, 870, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabHomeLayout.setVerticalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGap(2, 2, 2)
                .addComponent(lblWorkspace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPanel.addTab("Home", tabHome);

        tabBrowse.setBackground(new java.awt.Color(235, 233, 221));
        tabBrowse.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabBrowse.setName("tabBrowse"); // NOI18N
        tabBrowse.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.addTab("Browse All", tabBrowse);

        tabLocation.setBackground(new java.awt.Color(194, 207, 214));
        tabLocation.setMinimumSize(new java.awt.Dimension(1000, 600));
        tabLocation.setName("tabLocation"); // NOI18N
        tabLocation.setPreferredSize(new java.awt.Dimension(1000, 600));
        tabbedPanel.addTab("All Locations", tabLocation);

        tabElement.setBackground(new java.awt.Color(201, 218, 199));
        tabElement.setMinimumSize(new java.awt.Dimension(1000, 600));
        tabElement.setName("tabElement"); // NOI18N
        tabElement.setPreferredSize(new java.awt.Dimension(1000, 600));
        tabbedPanel.addTab("All Creatures", tabElement);

        mainPanel.add(tabbedPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        statusPanel.setBackground(new java.awt.Color(232, 238, 220));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.BorderLayout(10, 0));

        statusMessageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusMessageLabel.setAlignmentY(0.0F);
        statusMessageLabel.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        statusMessageLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusMessageLabel.setPreferredSize(new java.awt.Dimension(500, 20));
        statusPanel.add(statusMessageLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(232, 238, 220));
        jPanel1.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanel1.setMinimumSize(new java.awt.Dimension(50, 16));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout(5, 0));

        progressBar.setMaximumSize(new java.awt.Dimension(400, 20));
        progressBar.setMinimumSize(new java.awt.Dimension(50, 14));
        progressBar.setName("progressBar"); // NOI18N
        progressBar.setPreferredSize(new java.awt.Dimension(320, 14));
        jPanel1.add(progressBar, java.awt.BorderLayout.CENTER);

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setMaximumSize(new java.awt.Dimension(20, 20));
        statusAnimationLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusAnimationLabel.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(statusAnimationLabel, java.awt.BorderLayout.EAST);

        statusPanel.add(jPanel1, java.awt.BorderLayout.EAST);

        getContentPane().add(statusPanel, java.awt.BorderLayout.PAGE_END);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText("Application");
        fileMenu.setName("fileMenu"); // NOI18N

        workspaceMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        workspaceMenu.setText("Workspace");
        workspaceMenu.setName("workspaceMenu"); // NOI18N

        mnuChangeWorkspaceMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuChangeWorkspaceMenuItem.setText("Switch Active Workspace");
        mnuChangeWorkspaceMenuItem.setToolTipText("Select another Workspace to use.");
        mnuChangeWorkspaceMenuItem.setName("mnuChangeWorkspaceMenuItem"); // NOI18N
        mnuChangeWorkspaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuChangeWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuChangeWorkspaceMenuItem);

        mnuCreateWorkspaceMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuCreateWorkspaceMenuItem.setText("Create New Workspace");
        mnuCreateWorkspaceMenuItem.setToolTipText("Select a folder where a new Workspace will be created.");
        mnuCreateWorkspaceMenuItem.setName("mnuCreateWorkspaceMenuItem"); // NOI18N
        mnuCreateWorkspaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuCreateWorkspaceMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        workspaceMenu.add(jSeparator1);

        mnuCleanWorkspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        mnuCleanWorkspace.setText("Check and Clean the Workspace");
        mnuCleanWorkspace.setToolTipText("Make sure the Workspace is in good order and remove non-essential files.");
        mnuCleanWorkspace.setName("mnuCleanWorkspace"); // NOI18N
        mnuCleanWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCleanWorkspaceActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuCleanWorkspace);

        fileMenu.add(workspaceMenu);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        mnuExitApp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        mnuExitApp.setText("Exit WildLog");
        mnuExitApp.setToolTipText("Close the application.");
        mnuExitApp.setName("mnuExitApp"); // NOI18N
        mnuExitApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitAppActionPerformed(evt);
            }
        });
        fileMenu.add(mnuExitApp);

        menuBar.add(fileMenu);

        backupMenu.setText("Backup");
        backupMenu.setName("backupMenu"); // NOI18N

        mnuBackupDatabase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Data Icon.gif"))); // NOI18N
        mnuBackupDatabase.setText("Backup Database");
        mnuBackupDatabase.setToolTipText("<html>This makes a backup of the database. <br/><b>Note: This does not backup the files, only the database is backed up.</b> <br/>To backup the data and files it is recommended to make a manual copy of the entire Workspace folder, or use the Workspace Backup feature.</html>");
        mnuBackupDatabase.setName("mnuBackupDatabase"); // NOI18N
        mnuBackupDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBackupDatabaseActionPerformed(evt);
            }
        });
        backupMenu.add(mnuBackupDatabase);

        mnuBackupWorkspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuBackupWorkspace.setText("Backup Workspace");
        mnuBackupWorkspace.setToolTipText("Makes a backup of the Workspace using the Workspace Export feature.");
        mnuBackupWorkspace.setName("mnuBackupWorkspace"); // NOI18N
        mnuBackupWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBackupWorkspaceActionPerformed(evt);
            }
        });
        backupMenu.add(mnuBackupWorkspace);

        menuBar.add(backupMenu);

        exportMenu.setText("Export");

        mnuExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV Icon.gif"))); // NOI18N
        mnuExportCSV.setText("Export All to CSV");
        mnuExportCSV.setToolTipText("Export all data to CSV files. (Open in Excel, ArcGIS, etc.)");
        mnuExportCSV.setName("mnuExportCSV"); // NOI18N
        mnuExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportCSVActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportCSV);

        mnuExportHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        mnuExportHTML.setText("Export All to HTML");
        mnuExportHTML.setToolTipText("Export all data and linked thumbnails to HTML files. (Viewable in a web browser, etc.)");
        mnuExportHTML.setName("mnuExportHTML"); // NOI18N
        mnuExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportHTMLActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportHTML);

        mnuExportKML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Google Earth Icon.gif"))); // NOI18N
        mnuExportKML.setText("Export All to KML");
        mnuExportKML.setToolTipText("Export all data and linked thumbnails to a KML file. (Open in Google Earth, etc.)");
        mnuExportKML.setName("mnuExportKML"); // NOI18N
        mnuExportKML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportKMLActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportKML);

        jSeparator10.setName("jSeparator10"); // NOI18N
        exportMenu.add(jSeparator10);

        mnuExportWildNoteSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildNoteIcon.png"))); // NOI18N
        mnuExportWildNoteSync.setText("Export WildNote Sync File");
        mnuExportWildNoteSync.setToolTipText("Export the Creatures to a sync file that can be loaded in WildNote.");
        mnuExportWildNoteSync.setName("mnuExportWildNoteSync"); // NOI18N
        mnuExportWildNoteSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportWildNoteSyncActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportWildNoteSync);

        jSeparator9.setName("jSeparator9"); // NOI18N
        exportMenu.add(jSeparator9);

        mnuExportWorkspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuExportWorkspace.setText("Export to New Workspace");
        mnuExportWorkspace.setToolTipText("Export the specified data to a new WildLog Workspace.");
        mnuExportWorkspace.setName("mnuExportWorkspace"); // NOI18N
        mnuExportWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportWorkspaceActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportWorkspace);

        menuBar.add(exportMenu);

        importMenu.setText("Import");
        importMenu.setName("importMenu"); // NOI18N

        mnuImportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV Icon.gif"))); // NOI18N
        mnuImportCSV.setText("Import from CSV");
        mnuImportCSV.setToolTipText("<html>Import the data contained in the CSV files. <br/>All imported data will be prefixed by the provided value. <br/>(Note: This import uses the same format as files generated by the CSV Export.)</html>");
        mnuImportCSV.setName("mnuImportCSV"); // NOI18N
        mnuImportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportCSVActionPerformed(evt);
            }
        });
        importMenu.add(mnuImportCSV);

        jSeparator13.setName("jSeparator13"); // NOI18N
        importMenu.add(jSeparator13);

        btnImportIUCNList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/IUCN.gif"))); // NOI18N
        btnImportIUCNList.setText("Import IUCN Species List");
        btnImportIUCNList.setToolTipText("Import species names from a CSV file exported from the IUCN Red List site.");
        btnImportIUCNList.setName("btnImportIUCNList"); // NOI18N
        btnImportIUCNList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportIUCNListActionPerformed(evt);
            }
        });
        importMenu.add(btnImportIUCNList);

        jSeparator12.setName("jSeparator12"); // NOI18N
        importMenu.add(jSeparator12);

        mnuImportWildNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildNoteIcon.png"))); // NOI18N
        mnuImportWildNote.setText("Import WildNote Sync File");
        mnuImportWildNote.setToolTipText("Import a sync file that was exported from WildNote.");
        mnuImportWildNote.setName("mnuImportWildNote"); // NOI18N
        mnuImportWildNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportWildNoteActionPerformed(evt);
            }
        });
        importMenu.add(mnuImportWildNote);

        jSeparator11.setName("jSeparator11"); // NOI18N
        importMenu.add(jSeparator11);

        mnuImportWorkspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuImportWorkspace.setText("Import from Another Workspace");
        mnuImportWorkspace.setToolTipText("Import data and files from another WildLog Workspace.");
        mnuImportWorkspace.setName("mnuImportWorkspace"); // NOI18N
        mnuImportWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportWorkspaceActionPerformed(evt);
            }
        });
        importMenu.add(mnuImportWorkspace);

        jSeparator7.setName("jSeparator7"); // NOI18N
        importMenu.add(jSeparator7);

        mnuBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Bulk Import.png"))); // NOI18N
        mnuBulkImport.setText("Open a New Bulk Import Tab");
        mnuBulkImport.setToolTipText("Import multiple files at once using the Bulk Import feature.");
        mnuBulkImport.setName("mnuBulkImport"); // NOI18N
        mnuBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBulkImportActionPerformed(evt);
            }
        });
        importMenu.add(mnuBulkImport);

        menuBar.add(importMenu);

        advancedMenu.setText("Advanced");

        mnuCalcSunMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon.gif"))); // NOI18N
        mnuCalcSunMoon.setText("Calculate Sun and Moon information for all Observations");
        mnuCalcSunMoon.setToolTipText("WARNING: This action might recalculate and overwrite the Sun and Moon information for all Observations.");
        mnuCalcSunMoon.setName("mnuCalcSunMoon"); // NOI18N
        mnuCalcSunMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCalcSunMoonActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuCalcSunMoon);

        mnuCalcDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Duration.gif"))); // NOI18N
        mnuCalcDuration.setText("Calculate Duration for all Observations");
        mnuCalcDuration.setToolTipText("WARNING: This action might recalculate and overwrite the Duration information for all Observations.");
        mnuCalcDuration.setName("mnuCalcDuration"); // NOI18N
        mnuCalcDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCalcDurationActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuCalcDuration);

        jSeparator4.setName("jSeparator4"); // NOI18N
        advancedMenu.add(jSeparator4);

        mnuMoveVisits.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        mnuMoveVisits.setText("Move a Period to a different Place");
        mnuMoveVisits.setToolTipText("Move a Period from one Place to another, including all Observations during that Period.");
        mnuMoveVisits.setName("mnuMoveVisits"); // NOI18N
        mnuMoveVisits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMoveVisitsActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuMoveVisits);

        jSeparator8.setName("jSeparator8"); // NOI18N
        advancedMenu.add(jSeparator8);

        mnuMergeLocations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        mnuMergeLocations.setText("Merge one Place's Observations into another");
        mnuMergeLocations.setToolTipText("Move all Periods from one Place to another Place and then delete the initial Place.");
        mnuMergeLocations.setName("mnuMergeLocations"); // NOI18N
        mnuMergeLocations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMergeLocationsActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuMergeLocations);

        mnuMergeVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        mnuMergeVisit.setText("Merge one Period's Observations into another");
        mnuMergeVisit.setToolTipText("Move all Observations from one Period to another Period and then delete the initial Period.");
        mnuMergeVisit.setName("mnuMergeVisit"); // NOI18N
        mnuMergeVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMergeVisitActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuMergeVisit);

        mnuMergeElements.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        mnuMergeElements.setText("Merge one Creature's Observations into another");
        mnuMergeElements.setToolTipText("Move all Observations from one Creature to another Creature and then delete the initial Creature.");
        mnuMergeElements.setName("mnuMergeElements"); // NOI18N
        mnuMergeElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMergeElementsActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuMergeElements);

        menuBar.add(advancedMenu);

        extraMenu.setText("Extra");
        extraMenu.setName("extraMenu"); // NOI18N

        mnuExifMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF.png"))); // NOI18N
        mnuExifMenuItem.setText("Image EXIF Data Reader");
        mnuExifMenuItem.setToolTipText("Browse to any image on your computer and view the EXIF data.");
        mnuExifMenuItem.setName("mnuExifMenuItem"); // NOI18N
        mnuExifMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExifMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(mnuExifMenuItem);

        mnuCreateSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        mnuCreateSlideshow.setText("Create a Slideshow");
        mnuCreateSlideshow.setToolTipText("Create a slideshow using a folder of images anywhere on your computer.");
        mnuCreateSlideshow.setName("mnuCreateSlideshow"); // NOI18N
        mnuCreateSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateSlideshowActionPerformed(evt);
            }
        });
        extraMenu.add(mnuCreateSlideshow);

        mnuSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon_big.png"))); // NOI18N
        mnuSunAndMoon.setText("View Sun And Moon Phase");
        mnuSunAndMoon.setToolTipText("Opens up a Sun and Moon Phase dialog that can be used to determine the phases at any time and location.");
        mnuSunAndMoon.setName("mnuSunAndMoon"); // NOI18N
        mnuSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSunAndMoonActionPerformed(evt);
            }
        });
        extraMenu.add(mnuSunAndMoon);

        jSeparator3.setName("jSeparator3"); // NOI18N
        extraMenu.add(jSeparator3);

        externalMenu.setText("External Tools");
        externalMenu.setToolTipText("Easy access to some useful external (third party) tools bundled with this application.");
        externalMenu.setName("externalMenu"); // NOI18N

        mnuDBConsole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Data Icon.gif"))); // NOI18N
        mnuDBConsole.setText("Open H2 Database Console");
        mnuDBConsole.setToolTipText("Open the DB console bundled with the H2 database to access the database used by WildLog.");
        mnuDBConsole.setName("mnuDBConsole"); // NOI18N
        mnuDBConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDBConsoleActionPerformed(evt);
            }
        });
        externalMenu.add(mnuDBConsole);

        mnuOpenMapApp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Map Icon.gif"))); // NOI18N
        mnuOpenMapApp.setText("Open OpenMap GIS Software");
        mnuOpenMapApp.setToolTipText("Open the OpenMap application in standand alone mode.");
        mnuOpenMapApp.setName("mnuOpenMapApp"); // NOI18N
        mnuOpenMapApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenMapAppActionPerformed(evt);
            }
        });
        externalMenu.add(mnuOpenMapApp);

        extraMenu.add(externalMenu);

        menuBar.add(extraMenu);

        settingsMenu.setText("Settings");
        settingsMenu.setName("settingsMenu"); // NOI18N

        mappingMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        mappingMenu.setText("Map Settings");
        mappingMenu.setName("mappingMenu"); // NOI18N

        chkMnuUseWMS.setSelected(app.getWildLogOptions().isIsOnlinemapTheDefault());
        chkMnuUseWMS.setText("Use Online Map");
        chkMnuUseWMS.setToolTipText("Check to use the Online Map, uncheck to use the Offline Map instead.");
        chkMnuUseWMS.setName("chkMnuUseWMS"); // NOI18N
        chkMnuUseWMS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUseWMSItemStateChanged(evt);
            }
        });
        mappingMenu.add(chkMnuUseWMS);

        mnuMapStartMenuItem.setText("Set Map Start Location");
        mnuMapStartMenuItem.setToolTipText("Select the GPS location where the map will open at by default.");
        mnuMapStartMenuItem.setName("mnuMapStartMenuItem"); // NOI18N
        mnuMapStartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMapStartMenuItemActionPerformed(evt);
            }
        });
        mappingMenu.add(mnuMapStartMenuItem);

        settingsMenu.add(mappingMenu);

        otherMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        otherMenu.setText("GPS Input Defaults");
        otherMenu.setName("otherMenu"); // NOI18N

        mnuGPSInput.setText("Select Default GPS Hemispheres");
        mnuGPSInput.setToolTipText("Select the default values to use when adding new GPS points using the GPS Dialog.");
        mnuGPSInput.setName("mnuGPSInput"); // NOI18N
        mnuGPSInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGPSInputActionPerformed(evt);
            }
        });
        otherMenu.add(mnuGPSInput);

        settingsMenu.add(otherMenu);

        slideshowMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        slideshowMenu.setText("Slideshow Settings");
        slideshowMenu.setName("slideshowMenu"); // NOI18N

        mnuSetSlideshowSpeed.setText("Set Slideshow Speed");
        mnuSetSlideshowSpeed.setToolTipText("Set the framerate that will be used for all generated Slideshows.");
        mnuSetSlideshowSpeed.setName("mnuSetSlideshowSpeed"); // NOI18N
        mnuSetSlideshowSpeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSpeedActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSpeed);

        mnuSetSlideshowSize.setText("Set Slideshow Size");
        mnuSetSlideshowSize.setToolTipText("Set the size to which the images should be resized for the generated Slideshows.");
        mnuSetSlideshowSize.setName("mnuSetSlideshowSize"); // NOI18N
        mnuSetSlideshowSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSizeActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSize);

        settingsMenu.add(slideshowMenu);

        mnuPerformance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        mnuPerformance.setText("Performance Settings");
        mnuPerformance.setName("mnuPerformance"); // NOI18N

        chkMnuUseIconTables.setSelected(app.getWildLogOptions().isUseThumbnailTables());
        chkMnuUseIconTables.setText("Show Thumbnails On Tables");
        chkMnuUseIconTables.setToolTipText("Select this option to show thumbnails in the tables. Disabling this option will result in the tables loading much faster, but they won't show any thumbnails.");
        chkMnuUseIconTables.setName("chkMnuUseIconTables"); // NOI18N
        chkMnuUseIconTables.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUseIconTablesItemStateChanged(evt);
            }
        });
        mnuPerformance.add(chkMnuUseIconTables);

        chkMnuBrowseWithThumbnails.setSelected(app.getWildLogOptions().isUseThumnailBrowsing());
        chkMnuBrowseWithThumbnails.setText("Use Thumbnails On The Browse Tab");
        chkMnuBrowseWithThumbnails.setToolTipText("Select this option to use large thumbnails in the Browse Tab instead of the original files. Enabling this option should improve performance on the Browse Tab, but reduce the image quality.");
        chkMnuBrowseWithThumbnails.setName("chkMnuBrowseWithThumbnails"); // NOI18N
        chkMnuBrowseWithThumbnails.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuBrowseWithThumbnailsItemStateChanged(evt);
            }
        });
        mnuPerformance.add(chkMnuBrowseWithThumbnails);

        settingsMenu.add(mnuPerformance);

        mnuOther.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuOther.setText("Other Settings");
        mnuOther.setName("mnuOther"); // NOI18N

        chkMnuEnableSounds.setSelected(app.getWildLogOptions().isEnableSounds());
        chkMnuEnableSounds.setText("Enable Beep Sounds");
        chkMnuEnableSounds.setToolTipText("Select this option to enable the application to play a beep sounds in response to user input.");
        chkMnuEnableSounds.setName("chkMnuEnableSounds"); // NOI18N
        chkMnuEnableSounds.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuEnableSoundsItemStateChanged(evt);
            }
        });
        mnuOther.add(chkMnuEnableSounds);

        settingsMenu.add(mnuOther);

        menuBar.add(settingsMenu);

        helpMenu.setText("About");
        helpMenu.setName("helpMenu"); // NOI18N

        mnuAboutWildLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuAboutWildLog.setText("About WildLog");
        mnuAboutWildLog.setToolTipText("Display information about this version of WildLog.");
        mnuAboutWildLog.setName("mnuAboutWildLog"); // NOI18N
        mnuAboutWildLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutWildLogActionPerformed(evt);
            }
        });
        helpMenu.add(mnuAboutWildLog);

        mnuAboutWildNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildNoteIcon.png"))); // NOI18N
        mnuAboutWildNote.setText("About WildNote");
        mnuAboutWildNote.setToolTipText("More information about WildNote.");
        mnuAboutWildNote.setName("mnuAboutWildNote"); // NOI18N
        mnuAboutWildNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutWildNoteActionPerformed(evt);
            }
        });
        helpMenu.add(mnuAboutWildNote);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void tabHomeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabHomeComponentShown
        lblLocations.setText("Places: " + app.getDBI().count(new Location()));
        lblVisits.setText("Periods: " + app.getDBI().count(new Visit()));
        lblSightings.setText("Observations: " + app.getDBI().count(new Sighting()));
        lblCreatures.setText("Creatures: " + app.getDBI().count(new Element()));
    }//GEN-LAST:event_tabHomeComponentShown

    private void chkMnuUseWMSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseWMSItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setIsOnlinemapTheDefault(chkMnuUseWMS.isSelected());
        app.setWildLogOptions(options);
    }//GEN-LAST:event_chkMnuUseWMSItemStateChanged

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
            public boolean accept(File inFile) {
                if (inFile.isDirectory()) {
                    return true;
                }
                if (WildLogFileExtentions.Images.isJPG(inFile.toPath())) {
                        return true;
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
            UtilsDialog.showExifPopup(app, fileChooser.getSelectedFile());
        }
    }//GEN-LAST:event_mnuExifMenuItemActionPerformed

    private void mnuSetSlideshowSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetSlideshowSizeActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        app.getMainFrame().getGlassPane().setVisible(true);
        String inputFramerate = JOptionPane.showInputDialog(app.getMainFrame(),
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
        String inputFramerate = JOptionPane.showInputDialog(app.getMainFrame(),
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
        int latOption = JOptionPane.showOptionDialog(app.getMainFrame(),
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
        int lonOption = JOptionPane.showOptionDialog(app.getMainFrame(),
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

    private void mnuMergeElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeElementsActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MergeElementsDialog dialog = new MergeElementsDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeElementsActionPerformed

    private void mnuMoveVisitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMoveVisitsActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MoveVisitDialog dialog = new MoveVisitDialog(app, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMoveVisitsActionPerformed

    private void mnuCalcSunMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCalcSunMoonActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>Please <b>backup your Workspace</b> before proceding. <br>"
                        + "This will <u>replace</u> the Sun and Moon Information for your Observations with "
                        + "<u>auto generated values from the date, time and GPS information</u>.</html>",
                        "Calculate Sun and Moon Information",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            app.getMainFrame().getGlassPane().setVisible(true);
            final int choice = JOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Automatically Calculate Sun and Moon Information",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {"All Observations", "Only Obervations without Sun and Moon information"},
                    null);
            if (choice != JOptionPane.CLOSED_OPTION) {
                // Close all tabs and go to the home tab
                tabbedPanel.setSelectedIndex(0);
//                while (tabbedPanel.getTabCount() > 4) {
//                    tabbedPanel.remove(4);
//                }
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (choice == 0) {
                            // Update all Observations
                            setMessage("Starting the Sun and Moon Calculation");
                            setProgress(0);
                            List<Sighting> sightings = app.getDBI().list(new Sighting());
                            for (int t = 0; t < sightings.size(); t++) {
                                Sighting sighting = sightings.get(t);
                                if (sighting.getTimeAccuracy() != null && sighting.getTimeAccuracy().isUsableTime()) {
                                    if (sighting.getLatitude() != null && !Latitudes.NONE.equals(sighting.getLatitude())
                                            && sighting.getLongitude() != null && !Longitudes.NONE.equals(sighting.getLongitude())) {
                                        double lat = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                                        double lon = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                                        sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), lat, lon));
                                        sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), lat, lon));
                                    }
                                    sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                                    app.getDBI().createOrUpdate(sighting, false);
                                }
                                setProgress(0 + (int)((t/(double)sightings.size())*100));
                                setMessage("Sun and Moon Calculation: " + getProgress() + "%");
                            }
                            setProgress(100);
                            setMessage("Done with the Sun and Moon Calculation");
                        }
                        else
                        if (choice == 1) {
                            // Update only Observations without Sun and Moon phase
                            setMessage("Starting the Sun and Moon Calculation");
                            setProgress(0);
                            List<Sighting> sightings = app.getDBI().list(new Sighting());
                            for (int t = 0; t < sightings.size(); t++) {
                                Sighting sighting = sightings.get(t);
                                if (sighting.getTimeAccuracy() != null && sighting.getTimeAccuracy().isUsableTime()) {
                                    if (sighting.getMoonPhase() < 0) {
                                        sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                                    }
                                    if (sighting.getLatitude() != null && !Latitudes.NONE.equals(sighting.getLatitude())
                                            && sighting.getLongitude() != null && !Longitudes.NONE.equals(sighting.getLongitude())) {
                                        double lat = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                                        double lon = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                                        if (sighting.getMoonlight() == null || Moonlight.NONE.equals(sighting.getMoonlight()) || Moonlight.UNKNOWN.equals(sighting.getMoonlight())) {
                                            sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), lat, lon));
                                        }
                                        if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())) {
                                            sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), lat, lon));
                                        }
                                    }
                                    app.getDBI().createOrUpdate(sighting, false);
                                }
                                setProgress(0 + (int)((t/(double)sightings.size())*100));
                                setMessage("Sun and Moon Calculation: " + getProgress() + "%");
                            }
                            setProgress(100);
                            setMessage("Done with the Sun and Moon Calculation");
                        }
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                        app.getMainFrame().getGlassPane().setVisible(false);
                        return null;
                    }
                });
            }
            else {
                app.getMainFrame().getGlassPane().setVisible(false);
            }
        }
    }//GEN-LAST:event_mnuCalcSunMoonActionPerformed

    private void mnuBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBulkImportActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                UtilsPanelGenerator.openBulkUploadTab(new BulkUploadPanel(app, this, null, null), tabbedPanel);
                return null;
            }
        });
    }//GEN-LAST:event_mnuBulkImportActionPerformed

    private void mnuImportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportCSVActionPerformed
        tabbedPanel.setSelectedIndex(0);
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
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
                    Path path = fileChooser.getSelectedFile().toPath();
                    app.getMainFrame().getGlassPane().setVisible(true);
                    String prefix = JOptionPane.showInputDialog(app.getMainFrame(),
                            "<html>Please provide a prefix to use for the imported data. "
                            + "<br>The prefix will be used to map the imported data to new unique records. "
                            + "<br>You can manually merge Creatures and move Periods afterwards.</html>",
                            "Import CSV Data", JOptionPane.QUESTION_MESSAGE);
                    app.getMainFrame().getGlassPane().setVisible(false);
                    if (prefix != null && !prefix.isEmpty()) {
                        app.getMainFrame().getGlassPane().setVisible(true);
                        int choice = JOptionPane.showConfirmDialog(app.getMainFrame(),
                                "<html><b>Would you like to exclude the WildLog File references</b>? "
                                + "<br><br><hr>"
                                + "<br>Note: The CSV Import can not import the actual files, but only the database links "
                                + "<br>to the files. If you select NO, then you will have to manually copy the files into the correct folders."
                                + "<br><br><hr>"
                                + "<br>It is <b>strongly recommended</b> to select <b>YES</b> to prevent more than one record to be "
                                + "<br>linked to the same file. The import will fail if a duplicate link is attempted.</html>",
                                "Exclude Database File References", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        app.getMainFrame().getGlassPane().setVisible(false);
                        boolean excludeWildLogFiles = false;
                        if (choice == JOptionPane.YES_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                            excludeWildLogFiles = true;
                        }
                        boolean hasErrors = false;
                        try {
                            hasErrors = !app.getDBI().doImportCSV(path, prefix, !excludeWildLogFiles);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace(System.err);
                            hasErrors = true;
                        }
                        if (hasErrors) {
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
                tabHomeComponentShown(null);
                return null;
            }
        });
    }//GEN-LAST:event_mnuImportCSVActionPerformed

    private void mnuCreateSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateSlideshowActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG Images",
                WildLogFileExtentions.Images.JPG.getExtention().toLowerCase(), WildLogFileExtentions.Images.JPEG.getExtention().toLowerCase(),
                WildLogFileExtentions.Images.JPG.getExtention().toUpperCase(), WildLogFileExtentions.Images.JPG.getExtention().toUpperCase()));
        fileChooser.setDialogTitle("Select the JPG images to use for the Custom Slideshow...");
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return fileChooser.showOpenDialog(app.getMainFrame());
                }
            });
        if (result == JFileChooser.APPROVE_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Creating the Custom Slideshow");
                    List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
                    List<String> fileNames = new ArrayList<String>(files.size());
                    for (File tempFile : files) {
                        fileNames.add(tempFile.getAbsolutePath());
                    }
                    fileChooser.setDialogTitle("Please select where to save the Custom Slideshow...");
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("slideshow.mov"));
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Slideshow movie", "mov"));
                    int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                return fileChooser.showSaveDialog(app.getMainFrame());
                            }
                        });
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // Now create the slideshow
                        setMessage("Creating the Custom Slideshow (Busy writing the file, this may take a while.)");
                        UtilsMovies.generateSlideshow(fileNames, app, fileChooser.getSelectedFile().toPath());
                        setMessage("Done with the Custom Slideshow");
                    }
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuCreateSlideshowActionPerformed

    private void mnuDBConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDBConsoleActionPerformed
        UtilsFileProcessing.openFile(WildLogPaths.OPEN_H2.getRelativePath());
    }//GEN-LAST:event_mnuDBConsoleActionPerformed

    private void mnuOpenMapAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenMapAppActionPerformed
        UtilsFileProcessing.openFile(WildLogPaths.OPEN_OPENMAP.getRelativePath());
    }//GEN-LAST:event_mnuOpenMapAppActionPerformed

    private void mnuExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportCSVActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the CSV Export");
                Path path = WildLogPaths.WILDLOG_EXPORT_CSV.getAbsoluteFullPath();
                Files.createDirectories(path);
                app.getDBI().doExportCSV(path, true, null, null, null, null);
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_CSV.getAbsoluteFullPath());
                setMessage("Done with the CSV Export");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportCSVActionPerformed

    private void mnuExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportHTMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the HTML Export for All Records");
                setProgress(0);
                // Elements
                List<Element> listElements = app.getDBI().list(new Element());
                for (int t = 0; t < listElements.size(); t++) {
                    // TODO: Sal vinniger gaan as ek die multithreaded kan doen, maar dan moet ek weer die progressbar sync issue probeer fix...
                    UtilsHTML.exportHTML(listElements.get(t), app, null);
                    setProgress(0 + (int)((t/(double)listElements.size())*25));
                    setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                }
                setProgress(25);
                // Locations
                setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().list(new Location());
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsHTML.exportHTML(listLocations.get(t), app, null);
                    setProgress(25 + (int)((t/(double)listLocations.size())*25));
                    setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                }
                setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                // Visits
                List<Visit> listVisits = app.getDBI().list(new Visit());
                for (int t = 0; t < listVisits.size(); t++) {
                    UtilsHTML.exportHTML(listVisits.get(t), app, null);
                    setProgress(50 + (int)((t/(double)listVisits.size())*25));
                    setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                }
                setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().list(new Sighting());
                for (int t = 0; t < listSightings.size(); t++) {
                    UtilsHTML.exportHTML(listSightings.get(t), app, null);
                    setProgress(75 + (int)((t/(double)listSightings.size())*25));
                    setMessage("Creating the HTML Export for All Records " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("Creating the HTML Export for All Records " + getProgress());
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath());
                setMessage("Done with the HTML Export for All Records");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportHTMLActionPerformed

    private void mnuExportKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportKMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the KML Export for All Records");
                setProgress(0);
                setMessage("Creating the KML Export for All Records " + getProgress() + "%");
                // Make sure icons and folders exist
                Path iconPath = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(WildLogPaths.WildLogPathPrefixes.WILDLOG_SYSTEM_DUMP.toPath());
                Files.createDirectories(iconPath);
                UtilsKML.copyKmlIcons(iconPath);
                Path finalPath = WildLogPaths.WILDLOG_EXPORT_KML.getAbsoluteFullPath().resolve("AllRecords").resolve("WildLog.kml");
                Files.createDirectories(finalPath.getParent());
                // Generate the KML
                KmlGenerator kmlgen = new KmlGenerator();
                kmlgen.setKmlPath(finalPath.toString());
                // Get entries for Sightings and Locations
                Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>(200);
                setProgress(5);
                setMessage("Creating the KML Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().list(new Sighting());
                Collections.sort(listSightings);
                for (int t = 0; t < listSightings.size(); t++) {
                    String key = listSightings.get(t).getElementName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>(30));
                     }
                    entries.get(key).add(listSightings.get(t).toKML(t, app));
                    setProgress(5 + (int)((t/(double)listSightings.size())*80));
                    setMessage("Creating the KML Export for All Records " + getProgress() + "%");
                }
                // Locations
                List<Location> listLocations = app.getDBI().list(new Location());
                for (int t = 0; t < listLocations.size(); t++) {
                    String key = listLocations.get(t).getName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>(20));
                     }
                    // Note: Die ID moet aangaan waar die sightings gestop het
                    entries.get(key).add(listLocations.get(t).toKML(listSightings.size() + t, app));
                    setProgress(85 + (int)((t/(double)listLocations.size())*10));
                    setMessage("Creating the KML Export for All Records " + getProgress() + "%");
                }
                // Generate KML
                kmlgen.generateFile(entries, UtilsKML.getKmlStyles(iconPath));
                // Try to open the Kml file
                UtilsFileProcessing.openFile(finalPath);
                setProgress(100);
                setMessage("Done with the KML Export for All Records");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportKMLActionPerformed

    private void mnuSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSunAndMoonActionPerformed
        SunMoonDialog dialog = new SunMoonDialog(app, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuSunAndMoonActionPerformed

    private void mnuBackupDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupDatabaseActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
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
                return null;
            }
        });
    }//GEN-LAST:event_mnuBackupDatabaseActionPerformed

    private void mnuExitAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitAppActionPerformed
//        // Making the frame not visible (or calling dispose on it) hopefully prevents this error: java.lang.InterruptedException at java.lang.Object.wait(Native Method)
//        this.setVisible(false);
        app.quit(evt);
    }//GEN-LAST:event_mnuExitAppActionPerformed

    private void mnuAboutWildLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutWildLogActionPerformed
        JDialog aboutBox = new WildLogAboutBox(app);
        aboutBox.setVisible(true);
    }//GEN-LAST:event_mnuAboutWildLogActionPerformed

    private void mnuChangeWorkspaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuChangeWorkspaceMenuItemActionPerformed
        if (WildLogApp.configureWildLogHomeBasedOnFileBrowser(app.getMainFrame(), false)) {
            // Write first
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(WildLogApp.getACTIVE_WILDLOG_SETTINGS_FOLDER().resolve("wildloghome").toFile()));
                writer.write(WildLogPaths.getFullWorkspacePrefix().toString());
                writer.flush();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            // Shutdown
            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(app.getMainFrame(),
                            "The WildLog Workspace has been changed. Please restart the application.",
                            "Done!", JOptionPane.INFORMATION_MESSAGE);
                    return -1;
                }
            });
            // Making the frame not visible (or calling dispose on it) hopefully prevents this error: java.lang.InterruptedException at java.lang.Object.wait(Native Method)
            this.setVisible(false);
            app.quit(null);
        }
    }//GEN-LAST:event_mnuChangeWorkspaceMenuItemActionPerformed

    private void mnuCleanWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCleanWorkspaceActionPerformed
        // Popup 'n warning om te se alle programme wat WL data dalk oop het moet toe gemaak word sodat ek die files kan delete of move.
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is <b>HIGHLY recommended to backup the entire WildLog Workspace folder</b> before continuing! <br>"
                        + "Please <b>close any other applications</b> that might be accessing WildLog files. <br>"
                        + "Note that WildLog will be automatically closed when the cleanup is finished. <br>"
                        + "This task will check that all links between the data and files are correct. <br>"
                        + "In addition all unnessasary files will be removed from the Workspace. </html>",
                        "Warning!",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            // Close all tabs and go to the home tab
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            // Lock the input/display and show busy message
            // Note: we never remove the Busy dialog and greyed out background since the app wil be restarted anyway when done (Don't use JDialog since it stops the code until the dialog is closed...)
            tabbedPanel.setSelectedIndex(0);
            JPanel panel = new JPanel(new AbsoluteLayout());
            panel.setPreferredSize(new Dimension(400, 50));
            panel.setBorder(new LineBorder(new Color(245, 80, 40), 3));
            JLabel label = new JLabel("<html>Busy cleaning Workspace. Please be patient, this might take a while. <br>"
                    + "Don't close the application until the process is finished.</html>");
            label.setFont(new Font("Tahoma", Font.BOLD, 12));
            label.setBorder(new LineBorder(new Color(195, 65, 20), 4));
            panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.95f));
            panel.add(label, new AbsoluteConstraints(310, 20, -1, -1));
            panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
            JPanel glassPane = (JPanel) app.getMainFrame().getGlassPane();
            glassPane.removeAll();
            glassPane.setLayout(new BorderLayout(100, 100));
            glassPane.add(panel, BorderLayout.CENTER);
            glassPane.addMouseListener(new MouseAdapter() {});
            glassPane.addKeyListener(new KeyAdapter() {});
            app.getMainFrame().setGlassPane(glassPane);
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Start the process in another thread to allow the UI to update correctly and use the progressbar for feedback
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    long startTime = System.currentTimeMillis();
                    setProgress(0);
                    setMessage("Workspace Cleanup starting...");
                    // Setup the feedback file
                    Path feedbackFile = WildLogPaths.getFullWorkspacePrefix().resolve("WorkspaceCleanupFeedback.txt");
                    PrintWriter feedback = null;
                    // Start cleanup
                    try {
                        feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                        feedback.println("------------------------------------------------");
                        feedback.println("---------- STARTING WORKSPACE CLEANUP ----------");
                        feedback.println("------------------------------------------------");
                        feedback.println("");
                        // Create a final reference to the feedback writer for use in inner classes, etc.
                        final PrintWriter finalHandleFeedback = feedback;
                        // Setup helper classes (Op hierdie stadium wil ek al die code op een plek hou, ek kan dit later in Util methods in skuif of iets...)
                        class CleanupCounter {
                            public int counter = 0;
                        }
                        class CleanupHelper {
                            private void doTheMove(Path inExpectedPath, Path inExpectedPrefix, WildLogFile inWildLogFile, final CleanupCounter fileCount) {
                                Path shouldBePath = inExpectedPath.resolve(inExpectedPrefix);
                                Path currentPath = inWildLogFile.getAbsolutePath().getParent();
                                if (!shouldBePath.equals(currentPath)) {
                                    finalHandleFeedback.println("ERROR:     Incorrect path: " + currentPath);
                                    finalHandleFeedback.println("+RESOLVED: Moved the file to the correct path: " + shouldBePath);
                                    // "Re-upload" the file to the correct location
                                    UtilsFileProcessing.performFileUpload(
                                            inWildLogFile.getId(),
                                            inExpectedPrefix,
                                            new File[]{inWildLogFile.getAbsolutePath().toFile()},
                                            null, WildLogThumbnailSizes.NORMAL, app, false, null, false);
                                    // Delete the wrong entry
                                    app.getDBI().delete(inWildLogFile);
                                    fileCount.counter++;
                                }
                            }
                            public void moveFilesToCorrectFolders(WildLogFile inWildLogFile, Path inPrefix, final CleanupCounter fileCount) {
                                // Check to make sure the parent paths are correct, if not then move the file to the correct place and add a new DB entry before deleting the old one
                                // Maak seker alle DB paths is relative (nie absolute nie) en begin met propper WL roots
                                if (WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
                                    doTheMove(
                                            WildLogPaths.WILDLOG_FILES_IMAGES.getAbsoluteFullPath(),
                                            inPrefix,
                                            inWildLogFile,
                                            fileCount);
                                }
                                else
                                if (WildLogFileType.MOVIE.equals(inWildLogFile.getFileType())) {
                                    doTheMove(
                                            WildLogPaths.WILDLOG_FILES_MOVIES.getAbsoluteFullPath(),
                                            inPrefix,
                                            inWildLogFile,
                                            fileCount);
                                }
                                else {
                                    doTheMove(
                                            WildLogPaths.WILDLOG_FILES_OTHER.getAbsoluteFullPath(),
                                            inPrefix,
                                            inWildLogFile,
                                            fileCount);
                                }
                            }
                            public void checkDiskFilesAreInDB(WildLogPaths inWildLogPaths, final CleanupCounter fileCount, final int inFileProcessCounter, final int inTotalToIncrease) throws IOException {
                                final int baseProgress = getProgress();
                                final CleanupCounter counter = new CleanupCounter();
                                Files.walkFileTree(inWildLogPaths.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                        if (attrs.isRegularFile()) {
                                            // Kyk in DB of die path bestaan, as dit nie daar is nie delete die file
                                            WildLogFile wildLogFile = new WildLogFile();
                                            wildLogFile.setDBFilePath(WildLogPaths.getFullWorkspacePrefix().relativize(file).toString());
                                            if (app.getDBI().find(wildLogFile) == null) {
                                                finalHandleFeedback.println("ERROR:     File in Workspace not present in the database: " + wildLogFile.getAbsolutePath());
                                                finalHandleFeedback.println("+RESOLVED: Deleted the file from the database: " + wildLogFile.getDBFilePath());
                                                Files.deleteIfExists(file);
                                                fileCount.counter++;
                                            }
                                        }
                                        // Assuming there are more or less as many files left as what was processed in step 1
                                        if (counter.counter < inFileProcessCounter) {
                                            counter.counter++;
                                        }
                                        setProgress(baseProgress + (int)(counter.counter/(double)inFileProcessCounter*inTotalToIncrease));
                                        setMessage("Cleanup Step 2: Validate that the Workspace files are in the database... " + getProgress() + "%");
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                            }
                        }
                        final CleanupHelper cleanupHelper = new CleanupHelper();
                        setProgress(1);
                        // ---------------------1---------------------
                        // First check database files
                        // Maak seker alle files in die tabel wys na 'n location/element/ens wat bestaan (geen "floaters" mag teenwoordig wees nie)
                        setMessage("Cleanup Step 1: Validate database references to the files in the Workspace... " + getProgress() + "%");
                        finalHandleFeedback.println("** Starting Workspace Cleanup: " + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
                        finalHandleFeedback.println("1) Make sure the File records in the database contain valid values and correctly link to existing data and Workspace files.");
                        List<WildLogFile> allFiles = app.getDBI().list(new WildLogFile());
                        int filesWithoutID = 0;
                        int filesWithoutPath = 0;
                        int filesNotOnDisk = 0;
                        int filesWithMissingData = 0;
                        int filesWithBadType = 0;
                        int filesWithBadID = 0;
                        CleanupCounter filesMoved = new CleanupCounter();
                        int fileProcessCounter = 0;
                        int countImages = 0;
                        int countMovies = 0;
                        int countOther = 0;
                        for (WildLogFile wildLogFile : allFiles) {
                            // Check the WildLogFile's content
                            if (wildLogFile.getId() == null) {
                                finalHandleFeedback.println("ERROR:     File record without an ID. FilePath: " + wildLogFile.getRelativePath());
                                finalHandleFeedback.println("+RESOLVED: Tried to delete the database file record and file on disk.");
                                app.getDBI().delete(wildLogFile);
                                filesWithoutID++;
                                continue;
                            }
                            if (wildLogFile.getDBFilePath() == null) {
                                finalHandleFeedback.println("ERROR:     File path missing from database record. FileID: " + wildLogFile.getId());
                                finalHandleFeedback.println("+RESOLVED: Deleted the database file record.");
                                app.getDBI().delete(wildLogFile);
                                filesWithoutPath++;
                                continue;
                            }
                            if (!Files.exists(wildLogFile.getAbsolutePath(), LinkOption.NOFOLLOW_LINKS)) {
                                finalHandleFeedback.println("ERROR:     File record in the database can't be found on disk. FilePath: " + wildLogFile.getRelativePath());
                                finalHandleFeedback.println("+RESOLVED: Deleted the database file record.");
                                app.getDBI().delete(wildLogFile);
                                filesNotOnDisk++;
                                continue;
                            }
                            if (wildLogFile.getFilename() == null || wildLogFile.getFilename().isEmpty() || wildLogFile.getUploadDate() == null) {
                                finalHandleFeedback.println("WARNING:    Database file record missing data. FilePath: " + wildLogFile.getAbsolutePath()
                                        + ", Filename: " + wildLogFile.getFilename()
                                        + ", UploadDate: " + wildLogFile.getUploadDate());
                                finalHandleFeedback.println("-UNRESOLVED: No action taken...");
                                filesWithMissingData++;
                            }
                            if (wildLogFile.getFileType() == null || WildLogFileType.NONE.equals(WildLogFileType.getEnumFromText(wildLogFile.getFileType().toString()))) {
                                finalHandleFeedback.println("ERROR:     Unknown FileType of database file record. FilePath: " + wildLogFile.getRelativePath()
                                        + ", FileType: " + wildLogFile.getFileType());
                                finalHandleFeedback.println("+RESOLVED: Changed FileType to " + WildLogFileType.OTHER + ".");
                                wildLogFile.setFileType(WildLogFileType.OTHER);
                                app.getDBI().createOrUpdate(wildLogFile, true);
                                filesWithBadType++;
                            }
                            // Check the WildLogFile's linkage
                            // FIXME: Ek skiem ek kan hierdie net een keer roep met die generic interfaces, ens.
                            if (wildLogFile.getId().startsWith(Element.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Element temp = app.getDBI().find(new Element(wildLogFile.getId().substring(Element.WILDLOGFILE_ID_PREFIX.length())));
                                if (temp == null) {
                                    finalHandleFeedback.println("ERROR:     Could not find linked Creature for this file record. FilePath: " + wildLogFile.getRelativePath()
                                            + ", ID: " + wildLogFile.getId() + ", CreatureName Used: " + wildLogFile.getId().substring(Element.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().delete(wildLogFile);
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(
                                        wildLogFile,
                                        Paths.get(WildLogPaths.WildLogPathPrefixes.PREFIX_ELEMENT.toString(), temp.getPrimaryName()),
                                        filesMoved);
                            }
                            else if (wildLogFile.getId().startsWith(Visit.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Visit temp = app.getDBI().find(new Visit(wildLogFile.getId().substring(Visit.WILDLOGFILE_ID_PREFIX.length())));
                                if (temp == null) {
                                    finalHandleFeedback.println("ERROR:     Could not find linked Period for this file record. FilePath: " + wildLogFile.getRelativePath()
                                            + ", ID: " + wildLogFile.getId() + ", PeriodName Used: " + wildLogFile.getId().substring(Visit.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file databse record and file from disk.");
                                    app.getDBI().delete(wildLogFile);
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(
                                        wildLogFile,
                                        Paths.get(WildLogPaths.WildLogPathPrefixes.PREFIX_VISIT.toString(), temp.getLocationName(), temp.getName()),
                                        filesMoved);
                            }
                            else if (wildLogFile.getId().startsWith(Location.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Location temp = app.getDBI().find(new Location(wildLogFile.getId().substring(Location.WILDLOGFILE_ID_PREFIX.length())));
                                if (temp == null) {
                                    finalHandleFeedback.println("ERROR:     Could not find linked Place for this file. FilePath: " + wildLogFile.getRelativePath()
                                            + ", ID: " + wildLogFile.getId() + ", PlaceName Used: " + wildLogFile.getId().substring(Location.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().delete(wildLogFile);
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(
                                        wildLogFile,
                                        Paths.get(WildLogPaths.WildLogPathPrefixes.PREFIX_LOCATION.toString(), temp.getName()),
                                        filesMoved);
                            }
                            else if (wildLogFile.getId().startsWith(Sighting.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                Sighting temp = null;
                                try {
                                    temp = app.getDBI().find(new Sighting(Long.parseLong(wildLogFile.getId().substring(Sighting.WILDLOGFILE_ID_PREFIX.length()))));
                                }
                                catch (NumberFormatException ex) {
                                    finalHandleFeedback.println("ERROR:       Can't get linked Observation's ID.");
                                    finalHandleFeedback.println("-UNRESOLVED: Try to continue to delete the file.");
                                }
                                if (temp == null) {
                                    finalHandleFeedback.println("ERROR:     Could not find linked Observation for this file. FilePath: " + wildLogFile.getRelativePath()
                                            + ", ID: " + wildLogFile.getId() + ", ObservationID Used: " + wildLogFile.getId().substring(Sighting.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().delete(wildLogFile);
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(
                                        wildLogFile,
                                        Paths.get(WildLogPaths.WildLogPathPrefixes.PREFIX_SIGHTING.toString()).resolve(temp.toPath()),
                                        filesMoved);
                            }
                            else {
                                finalHandleFeedback.println("ERROR:     File ID is not correctly formatted.");
                                finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                app.getDBI().delete(wildLogFile);
                                filesWithBadID++;
                            }
                            fileProcessCounter++;
                            setProgress(1 + (int)(fileProcessCounter/(double)allFiles.size()*19));
                            setMessage("Cleanup Step 1: Validate database references to the files in the Workspace... " + getProgress() + "%");
                            if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                                countImages++;
                            }
                            else
                            if (WildLogFileType.MOVIE.equals(wildLogFile.getFileType())) {
                                countMovies++;
                            }
                            else
                            if (WildLogFileType.OTHER.equals(wildLogFile.getFileType())) {
                                countOther++;
                            }
                        }
                        setProgress(20);

                        // ---------------------2---------------------
                        setMessage("Cleanup Step 2: Validate that the Workspace files are in the database... " + getProgress() + "%");
                        finalHandleFeedback.println("2) Make sure the files in the Workspace are present in the database.");
                        // Secondly check the files on disk
                        CleanupCounter filesNotInDB = new CleanupCounter();
                        try {
                            // Kyk of al die files op die hardeskyf in die database bestaan
                            cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_IMAGES, filesNotInDB, countImages, (int)(countImages/(double)fileProcessCounter*20));
                            cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_MOVIES, filesNotInDB, countMovies, (int)(countMovies/(double)fileProcessCounter*20));
                            cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_OTHER, filesNotInDB, countOther, (int)(countOther/(double)fileProcessCounter*20));
                        }
                        catch (IOException ex) {
                            ex.printStackTrace(System.err);
                            finalHandleFeedback.println("ERROR:       Could not check all files on disk.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(40);

                        // ---------------------3---------------------
                        // As alles klaar is delete alle lee en temporary folders
                        setMessage("Cleanup Step 3: Delete empty folders in WildLog\\Files\\... " + getProgress() + "%");
                        finalHandleFeedback.println("3) Delete all empty folders in the Workspace's Images, Movies and Other files folders.");
                        try {
                            UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(WildLogPaths.WILDLOG_FILES.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            ex.printStackTrace(System.err);
                            finalHandleFeedback.println("ERROR:       Could not delete all empty folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(45);

                        // ---------------------4---------------------
                        // Delete alle temporary/onnodige files en folders
                        setMessage("Cleanup Step 4: Delete exports and thumbnails... " + getProgress() + "%");
                        finalHandleFeedback.println("4) Delete all exports and thumbnails (since these files can be recreated from within WildLog).");
                        try {
                            UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            ex.printStackTrace(System.err);
                            finalHandleFeedback.println("ERROR:       Could not delete export folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(55);
                        setMessage("Cleanup Step 4: Delete exports and thumbnails... " + getProgress() + "%");
                        try {
                            UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            ex.printStackTrace(System.err);
                            finalHandleFeedback.println("ERROR:       Could not delete thumbnail folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(65);

                        // ---------------------5---------------------
                        // Check the rest of the data for inconsistencies(all Locations, Visits, Creatures and Observations link correctly)
                        setMessage("Cleanup Step 5: Check links between records in the database... " + getProgress() + "%");
                        finalHandleFeedback.println("5) Make sure Places, Periods, Creatures and Observations all have correct links to each other.");
                        List<Visit> allVisits = app.getDBI().list(new Visit());
                        int badDataLinks = 0;
                        int countVisits = 0;
                        for (Visit visit : allVisits) {
                            Location temp = app.getDBI().find(new Location(visit.getLocationName()));
                            if (temp == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("ERROR: Could not find link between Period and Place. "
                                        + "Period: " + visit.getName() + ", Place: " + visit.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Period to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().find(new Location("WildLog_lost_and_found"));
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newLocation, null);
                                }
                                visit.setLocationName("WildLog_lost_and_found");
                                // Still an issue with sightings not going to point to the correct place... (handled in the code below)
                                app.getDBI().createOrUpdate(visit, visit.getName());
                            }
                            countVisits++;
                            setProgress(65 + (int)(countVisits/(double)allVisits.size()*3));
                            setMessage("Cleanup Step 5: Check links between records in the database... " + getProgress() + "%");
                        }
                        List<Sighting> allSightings = app.getDBI().list(new Sighting());
                        int countSightings = 0;
                        for (Sighting sighting : allSightings) {
                            // Check Location
                            Location tempLocation = app.getDBI().find(new Location(sighting.getLocationName()));
                            if (tempLocation == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("ERROR: Could not find link between Observation and Place. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Place: " + sighting.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().find(new Location("WildLog_lost_and_found"));
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newLocation, null);
                                }
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().createOrUpdate(sighting, false);
                            }
                            // Check Element
                            Element tempElement = app.getDBI().find(new Element(sighting.getElementName()));
                            if (tempElement == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("ERROR: Could not find link between Observation and Creature. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Creature: " + sighting.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Creature called 'WildLog_lost_and_found'.");
                                Element newElement = app.getDBI().find(new Element("WildLog_lost_and_found"));
                                if (newElement == null) {
                                    newElement = new Element("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newElement, null);
                                }
                                sighting.setElementName("WildLog_lost_and_found");
                                app.getDBI().createOrUpdate(sighting, false);
                            }
                            // Check Visit
                            Visit tempVisit = app.getDBI().find(new Visit(sighting.getVisitName()));
                            if (tempVisit == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("ERROR: Could not find link between Observation and Period. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Period: " + sighting.getVisitName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Period called 'WildLog_lost_and_found'.");
                                // Visit
                                Visit newVisit = app.getDBI().find(new Visit("WildLog_lost_and_found"));
                                if (newVisit == null) {
                                    newVisit = new Visit("WildLog_lost_and_found");
                                    newVisit.setLocationName("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newVisit, null);
                                }
                                sighting.setVisitName("WildLog_lost_and_found");
                                // Location
                                Location newLocation = app.getDBI().find(new Location("WildLog_lost_and_found"));
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newLocation, null);
                                }
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().createOrUpdate(sighting, false);
                            }
                            // Make sure the Sighting is using a legitimate Location-Visit pair
                            Visit checkSightingVisit = app.getDBI().find(new Visit(sighting.getVisitName()));
                            if (!checkSightingVisit.getLocationName().equalsIgnoreCase(sighting.getLocationName())) {
                                badDataLinks++;
                                finalHandleFeedback.println("ERROR: The Observation and Period references different Places. "
                                        + "Observation: " + sighting.getLocationName() + ", Period: " + checkSightingVisit.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation and Period to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().find(new Location("WildLog_lost_and_found"));
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createOrUpdate(newLocation, null);
                                }
                                // Update sighting
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().createOrUpdate(sighting, false);
                                // Update visit
                                checkSightingVisit.setLocationName("WildLog_lost_and_found");
                                app.getDBI().createOrUpdate(checkSightingVisit, checkSightingVisit.getName());
                            }
                            countSightings++;
                            setProgress(68 + (int)(countSightings/(double)allSightings.size()*7));
                            setMessage("Cleanup Step 5: Check links between records in the database... " + getProgress() + "%");
                        }
                        setProgress(75);

                        // ---------------------6---------------------
                        // Re-create die default thumbnails
                        setMessage("Cleanup Step 6: (Optional) Recreating default thumbnails... " + getProgress() + "%");
                        finalHandleFeedback.println("6) Recreate the default thumbnails for all images.");
                        final List<WildLogFile> listFiles = app.getDBI().list(new WildLogFile());
                        ExecutorService executorService = Executors.newFixedThreadPool(app.getThreadCount(), new NamedThreadFactory("WL_CleanWorkspace"));
                        final CleanupCounter countThumbnails = new CleanupCounter();
                        for (final WildLogFile wildLogFile : listFiles) {
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                                        if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
//                                            for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
//                                                wildLogFile.getAbsoluteThumbnailPath(size);
//                                            }
                                            // Maak net die nodigste thumbnails, want anders vat dinge donners lank
                                            wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                                            wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                                            wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                                            wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                                        }
                                    }
                                    // Not going to bother with synchornization here, since it's just the progress bar
                                    countThumbnails.counter++;
                                    setProgress(75 + (int)(countThumbnails.counter/(double)listFiles.size()*24));
                                    setMessage("Cleanup Step 6: (Optional) Recreating default thumbnails... " + getProgress() + "%");
                                }
                            });
                        }
                        for (WildLogSystemImages systemFile : WildLogSystemImages.values()) {
                            for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                                systemFile.getWildLogFile().getAbsoluteThumbnailPath(size);
                            }
                        }
                        // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), becaue this might take much-much longer
                        executorService.shutdown();
                        if (!executorService.awaitTermination(2, TimeUnit.DAYS)) {
                            finalHandleFeedback.println("ERROR:       Processing the thumbnails took too long.");
                            finalHandleFeedback.println("-UNRESOLVED: Thumbnails can be created on demand by the application.");
                        }
                        setProgress(99);

                        // ---------------------?---------------------
                        // Scan through the entire folder and delete all non-wildlog files and folders (remember to keep Maps, Backup and the feedback file)
                        // TODO: Maybe delete all non-wildlog files during cleanup

                        finalHandleFeedback.println("** Finished Workspace Cleanup: " + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("+++++++++++++++++++ SUMMARY ++++++++++++++++++++");
                        finalHandleFeedback.println("File on disk moved to new folder: " + filesMoved.counter);
                        finalHandleFeedback.println("Database file record had no reference to a file on disk: " + filesWithoutPath);
                        finalHandleFeedback.println("Database file record had a reference to a file on disk, but the file was not found: " + filesNotOnDisk);
                        finalHandleFeedback.println("Database file record had no ID: " + filesWithoutID);
                        finalHandleFeedback.println("Database file record had incorrect ID: " + filesWithBadID);
                        finalHandleFeedback.println("Database file record had incorrect type: " + filesWithBadType);
                        finalHandleFeedback.println("Database file record was missing non-essential data: " + filesWithMissingData);
                        finalHandleFeedback.println("Workspace file not found in the database: " + filesNotInDB.counter);
                        finalHandleFeedback.println("Incorrect links between database records: " + badDataLinks);
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("+++++++++++++++++++ DURATION +++++++++++++++++++");
                        long duration = System.currentTimeMillis() - startTime;
                        int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                        int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                        int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                        feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                        setMessage("Finished Workspace Cleanup...");
                        setProgress(100);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                        if (feedback != null) {
                            feedback.println("ERROR:       An exception occured while cleaning the Workspace!!");
                            feedback.println("-UNRESOLVED: Unexpected error... " + ex.getMessage());
                        }
                    }
                    finally {
                        if (feedback != null) {
                            feedback.println("");
                            feedback.println("------------------------------------------------");
                            feedback.println("---------- FINISHED WORKSPACE CLEANUP ----------");
                            feedback.println("------------------------------------------------");
                            feedback.println("");
                            feedback.flush();
                            feedback.close();
                        }
                    }
                    // Open the summary document
                    UtilsFileProcessing.openFile(feedbackFile);
                    return null;
                }
                @Override
                protected void finished() {
                    super.finished();
                    // Using invokeLater because I hope the progressbar will have finished by then, otherwise the popup is shown
                    // that asks wether you want to clos ethe application or not, and it's best to rather restart after the cleanup.
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close the application to be safe (make sure no wierd references/paths are still used, etc.)
                            app.quit(null);
                        }
                    });
                }
            });
        }
    }//GEN-LAST:event_mnuCleanWorkspaceActionPerformed

    private void mnuCalcDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCalcDurationActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>Please <b>backup your Workspace</b> before proceding. <br>"
                        + "This will <u>replace</u> the Duration information for all Observations with "
                        + "<u>auto generated values from the uploaded images</u>.</html>",
                        "Calculate Observation Duration",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            app.getMainFrame().getGlassPane().setVisible(true);
            final int choice = JOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Automatically Calculate Duration",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {"All Observations", "Only Obervations without a Duration"},
                    null);
            if (choice != JOptionPane.CLOSED_OPTION) {
                // Close all tabs and go to the home tab
                tabbedPanel.setSelectedIndex(0);
//                while (tabbedPanel.getTabCount() > 4) {
//                    tabbedPanel.remove(4);
//                }
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (choice == 0) {
                            // Update all observations
                            setMessage("Starting the Duration Calculation");
                            setProgress(0);
                            List<Sighting> sightingList = app.getDBI().list(new Sighting());
                            for (int t = 0; t < sightingList.size(); t++) {
                                Sighting sighting = sightingList.get(t);
                                WildLogFile searchFile = new WildLogFile(sighting.getWildLogFileID());
                                // USing only images here since it is more reliable (and safer to automate) than movies
                                searchFile.setFileType(WildLogFileType.IMAGE);
                                List<WildLogFile> files = app.getDBI().list(searchFile);
                                if (!files.isEmpty()) {
                                    Collections.sort(files);
                                    Date startDate = UtilsImageProcessing.getDateFromImage(files.get(0).getAbsolutePath());
                                    Date endDate = UtilsImageProcessing.getDateFromImage(files.get(files.size()-1).getAbsolutePath());
                                    double difference = (endDate.getTime() - startDate.getTime())/1000;
                                    int minutes = (int)difference/60;
                                    double seconds = difference - minutes*60.0;
                                    sighting.setDurationMinutes(minutes);
                                    sighting.setDurationSeconds((double)seconds);
                                    app.getDBI().createOrUpdate(sighting, false);
                                }
                                setProgress(0 + (int)((t/(double)sightingList.size())*100));
                                setMessage("Duration Calculation: " + getProgress() + "%");
                            }
                            setProgress(100);
                            setMessage("Done with the Duration Calculation");
                        }
                        else
                        if (choice == 1) {
                            // Update only observations that don't have a Duration yet
                            // Update all observations
                            setMessage("Starting the Duration Calculation");
                            setProgress(0);
                            List<Sighting> sightingList = app.getDBI().list(new Sighting());
                            for (int t = 0; t < sightingList.size(); t++) {
                                Sighting sighting = sightingList.get(t);
                                if (sighting.getDurationMinutes() == 0 && sighting.getDurationSeconds() == 0.0) {
                                    WildLogFile searchFile = new WildLogFile(sighting.getWildLogFileID());
                                    searchFile.setFileType(WildLogFileType.IMAGE);
                                    List<WildLogFile> files = app.getDBI().list(searchFile);
                                    if (!files.isEmpty()) {
                                        Collections.sort(files);
                                        Date startDate = UtilsImageProcessing.getDateFromImage(files.get(0).getAbsolutePath());
                                        Date endDate = UtilsImageProcessing.getDateFromImage(files.get(files.size()-1).getAbsolutePath());
                                        double difference = (endDate.getTime() - startDate.getTime())/1000;
                                        int minutes = (int)difference/60;
                                        double seconds = difference - minutes*60.0;
                                        sighting.setDurationMinutes(minutes);
                                        sighting.setDurationSeconds((double)seconds);
                                        app.getDBI().createOrUpdate(sighting, false);
                                    }
                                }
                                setProgress(0 + (int)((t/(double)sightingList.size())*100));
                                setMessage("Duration Calculation: " + getProgress() + "%");
                            }
                            setProgress(100);
                            setMessage("Done with the Duration Calculation");
                        }
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                        app.getMainFrame().getGlassPane().setVisible(false);
                        return null;
                    }
                });
            }
            else {
                app.getMainFrame().getGlassPane().setVisible(false);
            }
        }
    }//GEN-LAST:event_mnuCalcDurationActionPerformed

    private void mnuExportWildNoteSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportWildNoteSyncActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Export WildNote Sync " + getProgress() + "%");
                WildLogDBI syncDBI = null;
                try {
                    // Make sure the old files are deleted
                    Files.createDirectories(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath());
                    UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().toFile());
                    Path syncDatabase = WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().resolve(WildLogConstants.WILDNOTE_SYNC_DATABASE);
                    // Setup export DB
                    setTaskProgress(10);
                    setMessage("Export WildNote Sync " + getProgress() + "%");
                    syncDBI = new WildLogDBI_h2("jdbc:h2:" + syncDatabase + ";AUTOCOMMIT=ON;IGNORECASE=TRUE", false);
                    // Export the elements
                    List<Element> listElements = app.getDBI().list(new Element());
                    setTaskProgress(20);
                    setMessage("Export WildNote Sync " + getProgress() + "%");
                    int counter = 0;
                    for (Element element : listElements) {
                        // Save the element to the new DB
                        syncDBI.createOrUpdate(element, null);
                        // Copy the files
                        WildLogFile wildLogFile = app.getDBI().find(new WildLogFile(element.getWildLogFileID()));
                        if (wildLogFile != null) {
                            // Android kan nie die zip handle as die folders of files snaakse name het met - of _ in nie...
                            wildLogFile.setFilename(UtilsFileProcessing.getAlphaNumericVersion(element.getPrimaryName() + ".jpg"));
                            Path targetFile = syncDatabase.getParent().resolve(WildLogPaths.WildLogPathPrefixes.PREFIX_ELEMENT.toPath()).resolve(wildLogFile.getFilename());
//                            UtilsFileProcessing.copyFile(wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SYNC_EXPORT),
//                                    targetFile, false, false);
                            // Need to create a new image that have power of two dimentions
                            ImageIcon thumbnail = new ImageIcon(wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SYNC_EXPORT).toString());
                            try {
                                // Make the folder
                                Files.createDirectories(targetFile);
                                // Create the image to save
                                BufferedImage bufferedImage = new BufferedImage(WildLogThumbnailSizes.SYNC_EXPORT.getSize(), WildLogThumbnailSizes.SYNC_EXPORT.getSize(), BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics2D = bufferedImage.createGraphics();
                                graphics2D.drawImage(thumbnail.getImage(),
                                        (WildLogThumbnailSizes.SYNC_EXPORT.getSize() - thumbnail.getIconWidth())/2,
                                        (WildLogThumbnailSizes.SYNC_EXPORT.getSize() - thumbnail.getIconHeight())/2,
                                        Color.BLACK, null);
                                // Hardcoding all thumbnails to be JPG (even originally PNG images)
                                ImageIO.write(bufferedImage, "jpg", targetFile.toFile());
                                graphics2D.dispose();
                            }
                            catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                            // Create the WildLogFile entry inthe DB
                            syncDBI.createOrUpdate(wildLogFile, false);
                        }
                        setProgress(20 + (int)(counter++/(double)listElements.size()*70));
                        setMessage("Export WildNote Sync " + getProgress() + "%");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    if (syncDBI != null) {
                        syncDBI.close();
                    }
                }
                setProgress(90);
                setMessage("Export WildNote Sync " + getProgress() + "%");
                // Zip the content to make copying it accross easier
                UtilsCompression.zipIt(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().resolve("WildNoteSync.zip").toString(),
                        WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().toFile());
//                    // TODO: Delete everthing except for the zip
//                    UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath()
//                            .resolve(WildLogPaths.WildLogPathPrefixes.PREFIX_ELEMENT.toPath()).toFile());
                setProgress(100);
                setMessage("Export WildNote Sync Done");
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath());
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportWildNoteSyncActionPerformed

    private void mnuMergeLocationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeLocationsActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MergeLocationDialog dialog = new MergeLocationDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeLocationsActionPerformed

    private void mnuMergeVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeVisitActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
        });
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > 4) {
                tabbedPanel.remove(4);
            }
            MergeVisitDialog dialog = new MergeVisitDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeVisitActionPerformed

    private void mnuImportWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportWorkspaceActionPerformed
        tabbedPanel.setSelectedIndex(0);
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>It is <b><u>very strongly</u></b> recommended that you <b><u>backup your Workspace</u></b> (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            });
        if (result == JOptionPane.OK_OPTION) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select the Workspace folder to import");
            fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return fileChooser.showOpenDialog(app.getMainFrame());
                }
            });
            if (result != JFileChooser.ERROR_OPTION && result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
                Path selectedPath;
                if (fileChooser.getSelectedFile().isDirectory()) {
                    selectedPath = fileChooser.getSelectedFile().toPath();
                }
                else {
                    selectedPath = fileChooser.getSelectedFile().getParentFile().toPath();
                }
                // Make sure it's a valid workspace that was selected
                if (!selectedPath.endsWith(WildLogPaths.DEFAULT_WORKSPACE_NAME.getRelativePath())) {
                    selectedPath = selectedPath.resolve(WildLogPaths.DEFAULT_WORKSPACE_NAME.getRelativePath());
                }
                if (Files.exists(selectedPath.resolve(WildLogPaths.WILDLOG_DATA.getRelativePath()))) {
                    // Open the import window
                    WorkspaceImportDialog dialog = new WorkspaceImportDialog(app, selectedPath);
                    dialog.setVisible(true);
                }
                else {
                    UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "The selected folder is not a valid existing WildLog Workspace.",
                                    "Incorrect Workspace Selected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                            }
                        });
                }
            }
        }
    }//GEN-LAST:event_mnuImportWorkspaceActionPerformed

    private void mnuExportWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportWorkspaceActionPerformed
        WorkspaceExportDialog dialog = new WorkspaceExportDialog(app);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuExportWorkspaceActionPerformed

    private void mnuImportWildNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportWildNoteActionPerformed
        final int IMAGE_LINK_INTERVAL = 120000;
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Please select the WildNote Sync export file to use.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new WildNoteSyncFilter());
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(app.getMainFrame());
            }
        });
        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            tabbedPanel.setSelectedIndex(0);
            // Start the import
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setProgress(0);
                    setMessage("Starting Import WildNote Sync...");
                    // Get linked images
                    int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            return JOptionPane.showConfirmDialog(app.getMainFrame(), "<html>WildLog can automatically try to "
                                    + "link the files (photos and movies) in a folder to the imported WildNote Observations "
                                    + "using the date and time."
                                    + "<br>Note: Since this is an automated process the results need to be manually verified when "
                                    + "there are multiple Observations and files being imported with similar dates and times."
                                    + "<br>Would you like to specify a folder to use?",
                                    "Link Files?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        }
                    });
                    final Map<Long, List<File>> mapFilesToLink;
                    if (result == JOptionPane.YES_OPTION) {
                        setMessage("Import WildNote Sync: Scanning folder...");
                        final JFileChooser folderChooser = new JFileChooser();
                        folderChooser.setAcceptAllFileFilterUsed(false);
                        folderChooser.setMultiSelectionEnabled(false);
                        folderChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                        folderChooser.setDialogTitle("Please select the folder to use.");
                        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                return folderChooser.showOpenDialog(app.getMainFrame());
                            }
                        });
                        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
                            mapFilesToLink = new HashMap<Long, List<File>>(folderChooser.getSelectedFile().listFiles().length);
                            int t = 0;
                            for (File file : folderChooser.getSelectedFile().listFiles()) {
                                Date fileDate = UtilsImageProcessing.getDateFromFile(file.toPath());
                                List<File> lstFiles = mapFilesToLink.get(fileDate.getTime()/IMAGE_LINK_INTERVAL);
                                if (lstFiles == null) {
                                    lstFiles = new ArrayList<File>(3);
                                    mapFilesToLink.put(fileDate.getTime()/IMAGE_LINK_INTERVAL, lstFiles);
                                }
                                lstFiles.add(file);
                                setProgress((int)(t++/(double)folderChooser.getSelectedFile().listFiles().length*10));
                                setMessage("Import WildNote Sync " + getProgress() + "%");
                            }
                        }
                        else {
                            mapFilesToLink = null;
                        }
                    }
                    else {
                        mapFilesToLink = null;
                    }
                    // Start the DB import
                    WildLogDBI syncDBI = null;
                    try {
                        // Setup export DB
                        setTaskProgress(10);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        syncDBI = new WildLogDBI_h2("jdbc:h2:" + fileChooser.getSelectedFile().toPath().toAbsolutePath().getParent()
                                .resolve(WildLogConstants.WILDNOTE_SYNC_DATABASE).toString()
                                    + ";AUTOCOMMIT=ON;IGNORECASE=TRUE", false);
                        setTaskProgress(11);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        // Setup the Location
                        Location wildNoteLocation = app.getDBI().find(new Location(WildLogConstants.WILDNOTE_LOCATION_NAME));
                        if (wildNoteLocation == null) {
                            wildNoteLocation = new Location(WildLogConstants.WILDNOTE_LOCATION_NAME);
                            app.getDBI().createOrUpdate(wildNoteLocation, null);
                        }
                        setTaskProgress(13);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        // Setup the Visit
                        Visit tempVisit = new Visit(WildLogConstants.WILDNOTE_VISIT_NAME + " - " + new SimpleDateFormat("dd MMM yyyy (HH'h'mm)").format(Calendar.getInstance().getTime()),
                                WildLogConstants.WILDNOTE_LOCATION_NAME);
                        while (app.getDBI().count(tempVisit) > 0) {
                            tempVisit = new Visit(tempVisit.getName() + "_wl", tempVisit.getLocationName());
                        }
                        app.getDBI().createOrUpdate(tempVisit, null);
                        setTaskProgress(15);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        // Import the Elements
                        List<Element> listElements = syncDBI.list(new Element());
                        for (int t = 0; t < listElements.size(); t++) {
                            Element element = listElements.get(t);
                            if (app.getDBI().find(element) == null) {
                                app.getDBI().createOrUpdate(element, null);
                            }
                            setTaskProgress(15 + (int)(t/(double)listElements.size()*10));
                            setMessage("Import WildNote Sync " + getProgress() + "%");
                        }
                        setTaskProgress(25);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        // Import the Sightings
                        List<Sighting> listSightings = syncDBI.list(new Sighting());
                        for (int t = 0; t < listSightings.size(); t++) {
                            Sighting sighting = listSightings.get(t);
                            sighting.setVisitName(tempVisit.getName());
                            sighting.setSightingCounter(0);
                            sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                            // Calculate the "auto" fields (sun, moon, etc.)
                            if (sighting.getCertainty() == null || Certainty.NONE.equals(Certainty.getEnumFromText(sighting.getCertainty().toString()))) {
                                sighting.setCertainty(Certainty.SURE);
                            }
                            if (sighting.getDate() != null) {
                                if (sighting.getLatitude() != null && sighting.getLongitude() != null
                                        && !Latitudes.NONE.equals(sighting.getLatitude()) && !Longitudes.NONE.equals(sighting.getLongitude())) {
                                    double latitude = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                                    double longitude = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                                    // Sun
                                    sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                                    // Moon
                                    sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
                                }
                                sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                            }
                            app.getDBI().createOrUpdate(sighting, false);
                            // Check if there are any images to link
                            // TODO: Ek kan ook in die toekoms die "HasFoto" checkbox op WildNote gebruik om die linking meer akkuraat te maak...
                            if (mapFilesToLink != null && mapFilesToLink.get(sighting.getDate().getTime()/IMAGE_LINK_INTERVAL) != null) {
                                List<File> lstFiles = mapFilesToLink.get(sighting.getDate().getTime()/IMAGE_LINK_INTERVAL);
                                UtilsFileProcessing.performFileUpload(
                                        sighting.getWildLogFileID(),
                                        WildLogPaths.WildLogPathPrefixes.PREFIX_SIGHTING.toPath().resolve(sighting.toPath()),
                                        lstFiles.toArray(new File[lstFiles.size()]),
                                        null,
                                        WildLogThumbnailSizes.NORMAL,
                                        app, false, null, true);
                            }
                            setTaskProgress(25 + (int)(t/(double)listSightings.size()*70));
                            setMessage("Import WildNote Sync " + getProgress() + "%");
                        }
                        setTaskProgress(95);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                        UtilsPanelGenerator.openPanelAsTab(app, tempVisit.getName(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel, wildNoteLocation);
                        setTaskProgress(97);
                        setMessage("Import WildNote Sync " + getProgress() + "%");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                    finally {
                        if (syncDBI != null) {
                            syncDBI.close();
                        }
                    }
                    setProgress(100);
                    setMessage("Import WildNote Sync Done");
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuImportWildNoteActionPerformed

    private void mnuBackupWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupWorkspaceActionPerformed
        WorkspaceExportDialog dialog = new WorkspaceExportDialog(app);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuBackupWorkspaceActionPerformed

    private void chkMnuUseIconTablesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseIconTablesItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseThumbnailTables(chkMnuUseIconTables.isSelected());
        app.setWildLogOptions(options);
        tabbedPanel.setSelectedIndex(0);
    }//GEN-LAST:event_chkMnuUseIconTablesItemStateChanged

    private void chkMnuBrowseWithThumbnailsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuBrowseWithThumbnailsItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseThumnailBrowsing(chkMnuBrowseWithThumbnails.isSelected());
        app.setWildLogOptions(options);
        tabbedPanel.setSelectedIndex(0);
    }//GEN-LAST:event_chkMnuBrowseWithThumbnailsItemStateChanged

    private void chkMnuEnableSoundsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuEnableSoundsItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setEnableSounds(chkMnuEnableSounds.isSelected());
        app.setWildLogOptions(options);
    }//GEN-LAST:event_chkMnuEnableSoundsItemStateChanged

    private void mnuAboutWildNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutWildNoteActionPerformed
        JDialog aboutBox = new WildNoteAboutBox();
        aboutBox.setVisible(true);
    }//GEN-LAST:event_mnuAboutWildNoteActionPerformed

    private void mnuCreateWorkspaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateWorkspaceMenuItemActionPerformed
        if (WildLogApp.configureWildLogHomeBasedOnFileBrowser(app.getMainFrame(), false)) {
            // Write first
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(WildLogApp.getACTIVE_WILDLOG_SETTINGS_FOLDER().resolve("wildloghome").toFile()));
                writer.write(WildLogPaths.getFullWorkspacePrefix().toString());
                writer.flush();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            // Shutdown
            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(app.getMainFrame(),
                            "The WildLog Workspace has been created. Please restart the application.",
                            "Done!", JOptionPane.INFORMATION_MESSAGE);
                    return -1;
                }
            });
            // Making the frame not visible (or calling dispose on it) hopefully prevents this error: java.lang.InterruptedException at java.lang.Object.wait(Native Method)
            this.setVisible(false);
            app.quit(null);
        }
    }//GEN-LAST:event_mnuCreateWorkspaceMenuItemActionPerformed

    private void btnImportIUCNListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportIUCNListActionPerformed
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "<html>This will <u>replace</u> the names and threat status for the Creatures in this Workspace."
                        + "<br>Creatures are treated as <u>the same when their scientific name match</u>. "
                        + "<br>New Creatures may be added where matches weren't found."
                        + "<br>It is recommended to backup the Workspace's database before proceding."
                        + "<br><b>Warning: If you continue all open tabs will be closed automatically.</b></html>",
                        "Import IUCN Species Names",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        });
        if (result == JOptionPane.OK_OPTION) {
            app.getMainFrame().getGlassPane().setVisible(true);
            final int choiceForReplacing = JOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Import IUCN Species Names",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] {"Only Add New Creatures", "Only Update Existing Creatures", "Add New and Update Existing Creatures"},
                    null);
            app.getMainFrame().getGlassPane().setVisible(false);
            if (choiceForReplacing != JOptionPane.CLOSED_OPTION) {
                app.getMainFrame().getGlassPane().setVisible(true);
                final int choiceForName = JOptionPane.showOptionDialog(app.getMainFrame(),
                        "Please select what name should be modified:",
                        "Import IUCN Species Names",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] {"Primary Names", "Other Names"},
                        null);
                app.getMainFrame().getGlassPane().setVisible(false);
                if (choiceForName != JOptionPane.CLOSED_OPTION) {
                    // Close all tabs and go to the home tab
                    tabbedPanel.setSelectedIndex(0);
                    while (tabbedPanel.getTabCount() > 4) {
                        tabbedPanel.remove(4);
                    }
                    UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                        @Override
                        protected Object doInBackground() throws Exception {
                            setMessage("Starting the IUCN Import");
                            final JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Select the CSV file to import from IUCN");
                            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            fileChooser.setFileFilter(new CsvFilter());
                            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return fileChooser.showOpenDialog(app.getMainFrame());
                                    }
                                });
                            if (result == JFileChooser.APPROVE_OPTION) {
                                tabbedPanel.setSelectedIndex(0);
                                Path importFile = fileChooser.getSelectedFile().toPath();
                                boolean hasErrors = false;
                                try {
                                    boolean updatePrimaryName = false;
                                    if (choiceForName == 0) {
                                        updatePrimaryName = true;
                                    }
                                    boolean addNewElements = false;
                                    if (choiceForReplacing == 0 || choiceForReplacing == 2) {
                                        addNewElements = true;
                                    }
                                    boolean updateExistingElements = false;
                                    if (choiceForReplacing == 1 || choiceForReplacing == 2) {
                                        updateExistingElements = true;
                                    }
                                    hasErrors = !app.getDBI().doImportIUCN(importFile, updatePrimaryName, addNewElements, updateExistingElements);
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                    hasErrors = true;
                                }
                                if (hasErrors) {
                                    UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                        @Override
                                        public int showDialog() {
                                            JOptionPane.showMessageDialog(app.getMainFrame(),
                                                    "Not all of the data could be successfully imported.",
                                                    "Error Importing IUCN Species Names!", JOptionPane.ERROR_MESSAGE);
                                            return -1;
                                        }
                                    });
                                }
                            }
                            setMessage("Done with the IUCN Import");
                            tabHomeComponentShown(null);
                            return null;
                        }
                    });
                }
            }
        }
    }//GEN-LAST:event_btnImportIUCNListActionPerformed

    public void browseSelectedElement(Element inElement) {
        panelTabBrowse.browseSelectedElement(inElement);
    }

    public void browseSelectedLocation(Location inLocation) {
        panelTabBrowse.browseSelectedLocation(inLocation);
    }

    public void browseSelectedVisit(Visit inVisit) {
        panelTabBrowse.browseSelectedVisit(inVisit);
    }

    public boolean closeAllTabs() {
        boolean closeStatus = true;
        while ((tabbedPanel.getTabCount() > 4) && (closeStatus)) {
            tabbedPanel.setSelectedIndex(4);
            PanelCanSetupHeader tab = (PanelCanSetupHeader) tabbedPanel.getComponentAt(4);
            closeStatus = tab.closeTab();
        }
        return closeStatus;
    }

    public void refreshHomeTab() {
        tabHomeComponentShown(null);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPanel;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu advancedMenu;
    private javax.swing.JMenu backupMenu;
    private javax.swing.JMenuItem btnImportIUCNList;
    private javax.swing.JCheckBoxMenuItem chkMnuBrowseWithThumbnails;
    private javax.swing.JCheckBoxMenuItem chkMnuEnableSounds;
    private javax.swing.JCheckBoxMenuItem chkMnuUseIconTables;
    private javax.swing.JCheckBoxMenuItem chkMnuUseWMS;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu externalMenu;
    private javax.swing.JMenu extraMenu;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JLabel lblCreatures;
    private javax.swing.JLabel lblLocations;
    private javax.swing.JLabel lblSightings;
    private javax.swing.JLabel lblVisits;
    private javax.swing.JLabel lblWorkspace;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mappingMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuAboutWildNote;
    private javax.swing.JMenuItem mnuBackupDatabase;
    private javax.swing.JMenuItem mnuBackupWorkspace;
    private javax.swing.JMenuItem mnuBulkImport;
    private javax.swing.JMenuItem mnuCalcDuration;
    private javax.swing.JMenuItem mnuCalcSunMoon;
    private javax.swing.JMenuItem mnuChangeWorkspaceMenuItem;
    private javax.swing.JMenuItem mnuCleanWorkspace;
    private javax.swing.JMenuItem mnuCreateSlideshow;
    private javax.swing.JMenuItem mnuCreateWorkspaceMenuItem;
    private javax.swing.JMenuItem mnuDBConsole;
    private javax.swing.JMenuItem mnuExifMenuItem;
    private javax.swing.JMenuItem mnuExportCSV;
    private javax.swing.JMenuItem mnuExportHTML;
    private javax.swing.JMenuItem mnuExportKML;
    private javax.swing.JMenuItem mnuExportWildNoteSync;
    private javax.swing.JMenuItem mnuExportWorkspace;
    private javax.swing.JMenuItem mnuGPSInput;
    private javax.swing.JMenuItem mnuImportCSV;
    private javax.swing.JMenuItem mnuImportWildNote;
    private javax.swing.JMenuItem mnuImportWorkspace;
    private javax.swing.JMenuItem mnuMapStartMenuItem;
    private javax.swing.JMenuItem mnuMergeElements;
    private javax.swing.JMenuItem mnuMergeLocations;
    private javax.swing.JMenuItem mnuMergeVisit;
    private javax.swing.JMenuItem mnuMoveVisits;
    private javax.swing.JMenuItem mnuOpenMapApp;
    private javax.swing.JMenu mnuOther;
    private javax.swing.JMenu mnuPerformance;
    private javax.swing.JMenuItem mnuSetSlideshowSize;
    private javax.swing.JMenuItem mnuSetSlideshowSpeed;
    private javax.swing.JMenuItem mnuSunAndMoon;
    private javax.swing.JMenu otherMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JMenu slideshowMenu;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel tabBrowse;
    private javax.swing.JPanel tabElement;
    private javax.swing.JPanel tabHome;
    private javax.swing.JPanel tabLocation;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JMenu workspaceMenu;
    // End of variables declaration//GEN-END:variables
}
