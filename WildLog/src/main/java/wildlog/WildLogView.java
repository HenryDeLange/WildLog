package wildlog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.logging.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdesktop.application.TaskMonitor;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dataobjects.wrappers.WildLogSystemFile;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.data.utils.WildLogConstants;
import wildlog.html.utils.UtilsHTML;
import wildlog.maps.kml.UtilsKML;
import wildlog.maps.kml.generator.KmlEntry;
import wildlog.maps.kml.generator.KmlGenerator;
import wildlog.maps.utils.UtilsGPS;
import wildlog.movies.gifmovie.AnimatedGIFWriter;
import wildlog.movies.utils.UtilsMovies;
import wildlog.ui.dialogs.GPSGridConversionDialog;
import wildlog.ui.dialogs.ImageResizeDialog;
import wildlog.ui.dialogs.MergeElementsDialog;
import wildlog.ui.dialogs.MergeLocationDialog;
import wildlog.ui.dialogs.MergeVisitDialog;
import wildlog.ui.dialogs.MoveVisitDialog;
import wildlog.ui.dialogs.SunMoonDialog;
import wildlog.ui.dialogs.SystemMonitorDialog;
import wildlog.ui.dialogs.WildLogAboutBox;
import wildlog.ui.dialogs.WildNoteAboutBox;
import wildlog.ui.dialogs.WorkspaceExportDialog;
import wildlog.ui.dialogs.WorkspaceImportDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.WLFileChooser;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.filters.CsvFilter;
import wildlog.ui.helpers.filters.WildNoteSyncFilter;
import wildlog.ui.helpers.filters.WorkspaceFilter;
import wildlog.ui.panels.PanelTabBrowse;
import wildlog.ui.panels.PanelTabElements;
import wildlog.ui.panels.PanelTabLocations;
import wildlog.ui.panels.PanelTabSightings;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.inaturalist.dialogs.INatAuthTokenDialog;
import wildlog.ui.panels.inaturalist.dialogs.INatImportDialog;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsCompression;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsExcel;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;
import wildlog.xml.utils.UtilsXML;

/**
 * The application's main frame.
 */
public final class WildLogView extends JFrame {
    private final int STATIC_TAB_COUNT = 5;
    private final WildLogApp app;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private PanelTabBrowse panelTabBrowse;

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
        setupTabHeaderLocation(1);
        setupTabHeaderElement(2);
        setupTabHeaderSightings(3);
        setupTabHeaderBrowse(4);
        // Set the minimum size of the frame
        setMinimumSize(new Dimension(1024, 705));
    }

    private void setupTabHeaderHome() {
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

    private void setupTabHeaderBrowse(int inIndex) {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Browse.png"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Browse"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(inIndex, "Browse");
        tabbedPanel.setIconAt(inIndex, icon);
        tabbedPanel.setTabComponentAt(inIndex, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, inIndex);
        // Setup content
        panelTabBrowse = new PanelTabBrowse(app, tabbedPanel);
        tabbedPanel.setComponentAt(inIndex, panelTabBrowse);
    }

    private void setupTabHeaderLocation(int inIndex) {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/LocationList.gif"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Places"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(inIndex, "Places");
        tabbedPanel.setIconAt(inIndex, icon);
        tabbedPanel.setTabComponentAt(inIndex, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, inIndex);
        // Setup content
        tabbedPanel.setComponentAt(inIndex, new PanelTabLocations(app, tabbedPanel));
    }

    private void setupTabHeaderElement(int inIndex) {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/ElementList.png"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Creatures"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(inIndex, "Creatures");
        tabbedPanel.setIconAt(inIndex, icon);
        tabbedPanel.setTabComponentAt(inIndex, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, inIndex);
        // Setup content
        tabbedPanel.setComponentAt(inIndex, new PanelTabElements(app, tabbedPanel));
    }
    
    private void setupTabHeaderSightings(int inIndex) {
        JPanel tabHeader = new JPanel();
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/SightingList.png"));
        tabHeader.add(new JLabel(icon));
        tabHeader.add(new JLabel("Observations"));
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        tabbedPanel.setTitleAt(inIndex, "Observations");
        tabbedPanel.setIconAt(inIndex, icon);
        tabbedPanel.setTabComponentAt(inIndex, tabHeader);
        UtilsUI.attachMouseScrollToTabs(tabbedPanel, tabHeader, inIndex);
        // Setup content
        tabbedPanel.setComponentAt(inIndex, new PanelTabSightings(app, tabbedPanel));
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
        lblBlog = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblLocations = new javax.swing.JLabel();
        lblVisits = new javax.swing.JLabel();
        lblSightings = new javax.swing.JLabel();
        lblCreatures = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        lblMyWild = new javax.swing.JLabel();
        lblWorkspaceName = new javax.swing.JLabel();
        lblWorkspacePath = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        lblEmail = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblSettingsPath = new javax.swing.JLabel();
        lblSwitchWorkspace = new javax.swing.JLabel();
        tabLocation = new javax.swing.JPanel();
        tabElement = new javax.swing.JPanel();
        tabSightings = new javax.swing.JPanel();
        tabBrowse = new javax.swing.JPanel();
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
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        mnuChangeWorkspaceName = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuCleanWorkspace = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem mnuExitApp = new javax.swing.JMenuItem();
        backupMenu = new javax.swing.JMenu();
        mnuBackupDatabase = new javax.swing.JMenuItem();
        mnuBackupRestore = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        mnuBackupWorkspace = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        mnuExportExcelBasic = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JPopupMenu.Separator();
        mnuExportCSVBasic = new javax.swing.JMenuItem();
        mnuExportCSV = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        mnuExportHTML = new javax.swing.JMenuItem();
        mnuExportHTMLAdvanced = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        mnuExportXML = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        mnuExportKML = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuExportWildNoteSync = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuExportWorkspace = new javax.swing.JMenuItem();
        importMenu = new javax.swing.JMenu();
        mnuImportCSVBasic = new javax.swing.JMenuItem();
        mnuImportCSV = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        btnImportIUCNList = new javax.swing.JMenuItem();
        jSeparator25 = new javax.swing.JPopupMenu.Separator();
        mnuImportINaturalist = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mnuImportWildNote = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        mnuImportWorkspace = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnuBulkImport = new javax.swing.JMenuItem();
        advancedMenu = new javax.swing.JMenu();
        mnuSwitchElementNames = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mnuCalcSunMoon = new javax.swing.JMenuItem();
        mnuCalcDuration = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuMoveVisits = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuMergeLocations = new javax.swing.JMenuItem();
        mnuMergeVisit = new javax.swing.JMenuItem();
        mnuMergeElements = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        mnuReduceImagesSize = new javax.swing.JMenuItem();
        jSeparator24 = new javax.swing.JPopupMenu.Separator();
        mnuINaturalistToken = new javax.swing.JMenuItem();
        extraMenu = new javax.swing.JMenu();
        mnuExifMenuItem = new javax.swing.JMenuItem();
        mnuConvertCoordinates = new javax.swing.JMenuItem();
        mnuCreateSlideshow = new javax.swing.JMenuItem();
        mnuCreateGIF = new javax.swing.JMenuItem();
        mnuSunAndMoon = new javax.swing.JMenuItem();
        mnuSystemMonitor = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        externalMenu = new javax.swing.JMenu();
        mnuDBConsole = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        mappingMenu = new javax.swing.JMenu();
        mnuMapStartMenuItem = new javax.swing.JMenuItem();
        slideshowMenu = new javax.swing.JMenu();
        mnuSetSlideshowSize = new javax.swing.JMenuItem();
        mnuSetSlideshowSpeed = new javax.swing.JMenuItem();
        mnuPerformance = new javax.swing.JMenu();
        chkMnuUseIconTables = new javax.swing.JCheckBoxMenuItem();
        chkMnuBrowseWithThumbnails = new javax.swing.JCheckBoxMenuItem();
        mnuOther = new javax.swing.JMenu();
        chkMnuUseScienteficName = new javax.swing.JCheckBoxMenuItem();
        chkMnuEnableSounds = new javax.swing.JCheckBoxMenuItem();
        chkMnuIncludeCountInSightingPath = new javax.swing.JCheckBoxMenuItem();
        chkMnuUploadLogs = new javax.swing.JCheckBoxMenuItem();
        chkMnuUseBundledMediaViewers = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        mnuAboutWildNote = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        mnuUserGuide = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        mnuCheckUpdates = new javax.swing.JMenuItem();
        javax.swing.JMenuItem mnuAboutWildLog = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(app.getWildLogOptions().getWorkspaceName() + " -- WildLog v" + WildLogApp.WILDLOG_VERSION);
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
        jLabel12.setText("version " + WildLogApp.WILDLOG_VERSION);
        jLabel12.setName("jLabel12"); // NOI18N

        lblBlog.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        lblBlog.setForeground(new java.awt.Color(186, 210, 159));
        lblBlog.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBlog.setText("http://cameratrap.mywild.co.za ");
        lblBlog.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblBlog.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblBlog.setName("lblBlog"); // NOI18N
        lblBlog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblBlogMousePressed(evt);
            }
        });

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

        lblMyWild.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        lblMyWild.setForeground(new java.awt.Color(107, 124, 89));
        lblMyWild.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMyWild.setText("http://www.mywild.co.za ");
        lblMyWild.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMyWild.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblMyWild.setName("lblMyWild"); // NOI18N
        lblMyWild.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblMyWildMousePressed(evt);
            }
        });

        lblWorkspaceName.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblWorkspaceName.setForeground(new java.awt.Color(147, 169, 121));
        lblWorkspaceName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWorkspaceName.setText("...Workspace Name...");
        lblWorkspaceName.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 63, 32)), "Active Workspace Name", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10), new java.awt.Color(107, 133, 90))); // NOI18N
        lblWorkspaceName.setName("lblWorkspaceName"); // NOI18N

        lblWorkspacePath.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblWorkspacePath.setForeground(new java.awt.Color(66, 78, 52));
        lblWorkspacePath.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblWorkspacePath.setText(WildLogPaths.getFullWorkspacePrefix().toString());
        lblWorkspacePath.setName("lblWorkspacePath"); // NOI18N

        jSeparator6.setBackground(new java.awt.Color(163, 175, 148));
        jSeparator6.setForeground(new java.awt.Color(216, 227, 201));
        jSeparator6.setName("jSeparator6"); // NOI18N

        lblEmail.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(115, 122, 107));
        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("support@mywild.co.za ");
        lblEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblEmail.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblEmail.setName("lblEmail"); // NOI18N
        lblEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblEmailMousePressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(66, 78, 52));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("<html><u>Active Workspace Folder:</u></html>");
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(66, 78, 52));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("<html><u>Active Settings Folder:</u></html>");
        jLabel23.setName("jLabel23"); // NOI18N

        lblSettingsPath.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblSettingsPath.setForeground(new java.awt.Color(66, 78, 52));
        lblSettingsPath.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSettingsPath.setText(WildLogApp.getACTIVE_WILDLOG_SETTINGS_FOLDER().normalize().toAbsolutePath().toString());
        lblSettingsPath.setName("lblSettingsPath"); // NOI18N

        lblSwitchWorkspace.setForeground(new java.awt.Color(71, 87, 53));
        lblSwitchWorkspace.setText("<html>Click <u>here</u> to select a different Workspace...</html>");
        lblSwitchWorkspace.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSwitchWorkspace.setName("lblSwitchWorkspace"); // NOI18N
        lblSwitchWorkspace.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblSwitchWorkspaceMousePressed(evt);
            }
        });

        javax.swing.GroupLayout tabHomeLayout = new javax.swing.GroupLayout(tabHome);
        tabHome.setLayout(tabHomeLayout);
        tabHomeLayout.setHorizontalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSettingsPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblWorkspacePath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel23))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(50, 50, 50)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabHomeLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBlog)
                    .addComponent(lblMyWild, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblWorkspaceName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel11)
                        .addGap(16, 16, 16)
                        .addComponent(jLabel12))
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(lblLocations)
                        .addGap(18, 18, 18)
                        .addComponent(lblVisits)
                        .addGap(18, 18, 18)
                        .addComponent(lblCreatures)
                        .addGap(18, 18, 18)
                        .addComponent(lblSightings))
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(lblSwitchWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(160, 495, Short.MAX_VALUE))
        );
        tabHomeLayout.setVerticalGroup(
            tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBlog)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMyWild)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEmail)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblWorkspaceName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSwitchWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(tabHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabHomeLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblWorkspacePath)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSettingsPath)
                        .addGap(25, 25, 25))))
        );

        tabbedPanel.addTab("Home", tabHome);

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

        tabSightings.setBackground(new java.awt.Color(235, 233, 221));
        tabSightings.setMinimumSize(new java.awt.Dimension(1000, 600));
        tabSightings.setName("tabSightings"); // NOI18N
        tabSightings.setPreferredSize(new java.awt.Dimension(1000, 600));
        tabbedPanel.addTab("All Sightings", tabSightings);

        tabBrowse.setBackground(new java.awt.Color(204, 213, 186));
        tabBrowse.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabBrowse.setName("tabBrowse"); // NOI18N
        tabBrowse.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.addTab("Browse All", tabBrowse);

        mainPanel.add(tabbedPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        statusPanel.setBackground(new java.awt.Color(212, 217, 201));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.BorderLayout(10, 0));

        statusMessageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusMessageLabel.setAlignmentY(0.0F);
        statusMessageLabel.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        statusMessageLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusMessageLabel.setPreferredSize(new java.awt.Dimension(500, 20));
        statusPanel.add(statusMessageLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(212, 217, 201));
        jPanel1.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanel1.setMinimumSize(new java.awt.Dimension(50, 16));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.BorderLayout(5, 0));

        progressBar.setBackground(new java.awt.Color(204, 213, 186));
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
        fileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fileMenuMenuSelected(evt);
            }
        });

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

        jSeparator14.setName("jSeparator14"); // NOI18N
        workspaceMenu.add(jSeparator14);

        mnuChangeWorkspaceName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuChangeWorkspaceName.setText("Rename Active Workspace");
        mnuChangeWorkspaceName.setToolTipText("Change the name associated with this Workspace.");
        mnuChangeWorkspaceName.setName("mnuChangeWorkspaceName"); // NOI18N
        mnuChangeWorkspaceName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuChangeWorkspaceNameActionPerformed(evt);
            }
        });
        workspaceMenu.add(mnuChangeWorkspaceName);

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
        backupMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                backupMenuMenuSelected(evt);
            }
        });

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

        mnuBackupRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Data Icon.gif"))); // NOI18N
        mnuBackupRestore.setText("Restore Database");
        mnuBackupRestore.setToolTipText("Restore a previously backed-up database.");
        mnuBackupRestore.setName("mnuBackupRestore"); // NOI18N
        mnuBackupRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBackupRestoreActionPerformed(evt);
            }
        });
        backupMenu.add(mnuBackupRestore);

        jSeparator22.setName("jSeparator22"); // NOI18N
        backupMenu.add(jSeparator22);

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
        exportMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                exportMenuMenuSelected(evt);
            }
        });

        mnuExportExcelBasic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Excel.png"))); // NOI18N
        mnuExportExcelBasic.setText("Export All to Excel (Basic format)");
        mnuExportExcelBasic.setToolTipText("Export all data to Excel files using the Basic format.");
        mnuExportExcelBasic.setName("mnuExportExcelBasic"); // NOI18N
        mnuExportExcelBasic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportExcelBasicActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportExcelBasic);

        jSeparator23.setName("jSeparator23"); // NOI18N
        exportMenu.add(jSeparator23);

        mnuExportCSVBasic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV.png"))); // NOI18N
        mnuExportCSVBasic.setText("Export All to CSV (Basic format)");
        mnuExportCSVBasic.setToolTipText("Export all data to CSV files using the Basic format. (Open in Excel, ArcGIS, etc.)");
        mnuExportCSVBasic.setName("mnuExportCSVBasic"); // NOI18N
        mnuExportCSVBasic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportCSVBasicActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportCSVBasic);

        mnuExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV.png"))); // NOI18N
        mnuExportCSV.setText("Export All to CSV (WildLog format)");
        mnuExportCSV.setToolTipText("Export all data to CSV files. (Open in Excel, ArcGIS, etc.)");
        mnuExportCSV.setName("mnuExportCSV"); // NOI18N
        mnuExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportCSVActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportCSV);

        jSeparator18.setName("jSeparator18"); // NOI18N
        exportMenu.add(jSeparator18);

        mnuExportHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        mnuExportHTML.setText("Export All to Web Page (Basic)");
        mnuExportHTML.setToolTipText("Export all data and linked thumbnails to HTML files. (Viewable in a web browser, etc.)");
        mnuExportHTML.setName("mnuExportHTML"); // NOI18N
        mnuExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportHTMLActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportHTML);

        mnuExportHTMLAdvanced.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        mnuExportHTMLAdvanced.setText("Export All to Web Page (Advanced)");
        mnuExportHTMLAdvanced.setToolTipText("Export all data and linked thumbnails to HTML files. (Viewable in a web browser, etc.)");
        mnuExportHTMLAdvanced.setName("mnuExportHTMLAdvanced"); // NOI18N
        mnuExportHTMLAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportHTMLAdvancedActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportHTMLAdvanced);

        jSeparator19.setName("jSeparator19"); // NOI18N
        exportMenu.add(jSeparator19);

        mnuExportXML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/XML.png"))); // NOI18N
        mnuExportXML.setText("Export All to XML");
        mnuExportXML.setToolTipText("Export all data to XML files. (Open in text editor, web browser, etc.)");
        mnuExportXML.setName("mnuExportXML"); // NOI18N
        mnuExportXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportXMLActionPerformed(evt);
            }
        });
        exportMenu.add(mnuExportXML);

        jSeparator20.setName("jSeparator20"); // NOI18N
        exportMenu.add(jSeparator20);

        mnuExportKML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GoogleEarth.png"))); // NOI18N
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
        importMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                importMenuMenuSelected(evt);
            }
        });

        mnuImportCSVBasic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV.png"))); // NOI18N
        mnuImportCSVBasic.setText("Import from CSV (Basic format)");
        mnuImportCSVBasic.setToolTipText("<html>Import the data contained in the CSV files. <br/>All imported data will be prefixed by the provided value. <br/>(Note: This import uses the same format as files generated by the CSV Basic Export.)</html>");
        mnuImportCSVBasic.setName("mnuImportCSVBasic"); // NOI18N
        mnuImportCSVBasic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportCSVBasicActionPerformed(evt);
            }
        });
        importMenu.add(mnuImportCSVBasic);

        mnuImportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV.png"))); // NOI18N
        mnuImportCSV.setText("Import from CSV (WildLog format)");
        mnuImportCSV.setToolTipText("<html>Import the data contained in the CSV files. <br/>All imported data will be prefixed by the provided value. <br/>(Note: This import uses the same format as files generated by the CSV WildLog Export.)</html>");
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

        jSeparator25.setName("jSeparator25"); // NOI18N
        importMenu.add(jSeparator25);

        mnuImportINaturalist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist_white.png"))); // NOI18N
        mnuImportINaturalist.setText("Import iNaturalist Observations");
        mnuImportINaturalist.setToolTipText("Import observations from iNaturalist.");
        mnuImportINaturalist.setName("mnuImportINaturalist"); // NOI18N
        mnuImportINaturalist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportINaturalistActionPerformed(evt);
            }
        });
        importMenu.add(mnuImportINaturalist);

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
        advancedMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                advancedMenuMenuSelected(evt);
            }
        });

        mnuSwitchElementNames.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        mnuSwitchElementNames.setText("Switch Primary, Other and Scientific names of Creatures");
        mnuSwitchElementNames.setToolTipText("Switch Primary, Other and Scientific names of all Creatures in the Workspace.");
        mnuSwitchElementNames.setName("mnuSwitchElementNames"); // NOI18N
        mnuSwitchElementNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSwitchElementNamesActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuSwitchElementNames);

        jSeparator15.setName("jSeparator15"); // NOI18N
        advancedMenu.add(jSeparator15);

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

        mnuCalcDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Duration_Small.png"))); // NOI18N
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

        jSeparator21.setName("jSeparator21"); // NOI18N
        advancedMenu.add(jSeparator21);

        mnuReduceImagesSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Image_Small.png"))); // NOI18N
        mnuReduceImagesSize.setText("Reduce the size of Images");
        mnuReduceImagesSize.setToolTipText("Reduce the resolution of certain images to reduce the overall size of the Workspace.");
        mnuReduceImagesSize.setName("mnuReduceImagesSize"); // NOI18N
        mnuReduceImagesSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuReduceImagesSizeActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuReduceImagesSize);

        jSeparator24.setName("jSeparator24"); // NOI18N
        advancedMenu.add(jSeparator24);

        mnuINaturalistToken.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist_white.png"))); // NOI18N
        mnuINaturalistToken.setText("Setup iNaturalist Authorization");
        mnuINaturalistToken.setToolTipText("Configure the iNaturalist Authorization Token for this WildLog session.");
        mnuINaturalistToken.setName("mnuINaturalistToken"); // NOI18N
        mnuINaturalistToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuINaturalistTokenActionPerformed(evt);
            }
        });
        advancedMenu.add(mnuINaturalistToken);

        menuBar.add(advancedMenu);

        extraMenu.setText("Extra");
        extraMenu.setName("extraMenu"); // NOI18N
        extraMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                extraMenuMenuSelected(evt);
            }
        });

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

        mnuConvertCoordinates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        mnuConvertCoordinates.setText("Convert GPS / Pentad / QDGC");
        mnuConvertCoordinates.setToolTipText("Convert between GPS, Pentad and QDS coordinates.");
        mnuConvertCoordinates.setName("mnuConvertCoordinates"); // NOI18N
        mnuConvertCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConvertCoordinatesActionPerformed(evt);
            }
        });
        extraMenu.add(mnuConvertCoordinates);

        mnuCreateSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        mnuCreateSlideshow.setText("Create a JPEG Movie");
        mnuCreateSlideshow.setToolTipText("Create a JPEG Movie slideshow using a folder of images anywhere on your computer.");
        mnuCreateSlideshow.setName("mnuCreateSlideshow"); // NOI18N
        mnuCreateSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateSlideshowActionPerformed(evt);
            }
        });
        extraMenu.add(mnuCreateSlideshow);

        mnuCreateGIF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GIF_Small.png"))); // NOI18N
        mnuCreateGIF.setText("Create an Animated GIF");
        mnuCreateGIF.setToolTipText("Create an animated GIF slideshow using a folder of images anywhere on your computer.");
        mnuCreateGIF.setName("mnuCreateGIF"); // NOI18N
        mnuCreateGIF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateGIFActionPerformed(evt);
            }
        });
        extraMenu.add(mnuCreateGIF);

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

        mnuSystemMonitor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        mnuSystemMonitor.setText("Open System Monitor");
        mnuSystemMonitor.setToolTipText("Opens up a Sun and Moon Phase dialog that can be used to determine the phases at any time and location.");
        mnuSystemMonitor.setName("mnuSystemMonitor"); // NOI18N
        mnuSystemMonitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSystemMonitorActionPerformed(evt);
            }
        });
        extraMenu.add(mnuSystemMonitor);

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

        extraMenu.add(externalMenu);

        menuBar.add(extraMenu);

        settingsMenu.setText("Settings");
        settingsMenu.setName("settingsMenu"); // NOI18N
        settingsMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                settingsMenuMenuSelected(evt);
            }
        });

        mappingMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        mappingMenu.setText("Map Settings");
        mappingMenu.setName("mappingMenu"); // NOI18N

        mnuMapStartMenuItem.setText("Set Legacy Map Start Location");
        mnuMapStartMenuItem.setToolTipText("Select the GPS location where the Legacy Online Map will open at by default.");
        mnuMapStartMenuItem.setName("mnuMapStartMenuItem"); // NOI18N
        mnuMapStartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMapStartMenuItemActionPerformed(evt);
            }
        });
        mappingMenu.add(mnuMapStartMenuItem);

        settingsMenu.add(mappingMenu);

        slideshowMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        slideshowMenu.setText("Slideshow Settings");
        slideshowMenu.setName("slideshowMenu"); // NOI18N

        mnuSetSlideshowSize.setText("Set Slideshow Image Size");
        mnuSetSlideshowSize.setToolTipText("Set the size to which the images should be resized for the generated Slideshows.");
        mnuSetSlideshowSize.setName("mnuSetSlideshowSize"); // NOI18N
        mnuSetSlideshowSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSizeActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSize);

        mnuSetSlideshowSpeed.setText("Set Slideshow Speed");
        mnuSetSlideshowSpeed.setToolTipText("Set the framerate that will be used for generated Slideshows (where applicable).");
        mnuSetSlideshowSpeed.setName("mnuSetSlideshowSpeed"); // NOI18N
        mnuSetSlideshowSpeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSetSlideshowSpeedActionPerformed(evt);
            }
        });
        slideshowMenu.add(mnuSetSlideshowSpeed);

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

        chkMnuUseScienteficName.setSelected(app.getWildLogOptions().isUseScientificNames());
        chkMnuUseScienteficName.setText("Use Scientific Name On Tables");
        chkMnuUseScienteficName.setToolTipText("Select this option to show the Scientific Name for Creatures in the tables instead of the Other Name.");
        chkMnuUseScienteficName.setName("chkMnuUseScienteficName"); // NOI18N
        chkMnuUseScienteficName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUseScienteficNameItemStateChanged(evt);
            }
        });
        mnuOther.add(chkMnuUseScienteficName);

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

        chkMnuIncludeCountInSightingPath.setSelected(app.getWildLogOptions().isUseIndividualsInSightingPath());
        chkMnuIncludeCountInSightingPath.setText("Include Number of Individuals in the File Path");
        chkMnuIncludeCountInSightingPath.setToolTipText("This option will change the way in which the Files are stored and exported. Select this option to group all Files for Observations (from the same Period) into folders based on the number of individuals observed.");
        chkMnuIncludeCountInSightingPath.setName("chkMnuIncludeCountInSightingPath"); // NOI18N
        chkMnuIncludeCountInSightingPath.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuIncludeCountInSightingPathItemStateChanged(evt);
            }
        });
        mnuOther.add(chkMnuIncludeCountInSightingPath);

        chkMnuUploadLogs.setSelected(app.getWildLogOptions().isUploadLogs());
        chkMnuUploadLogs.setText("Automatically Upload Error Logs");
        chkMnuUploadLogs.setToolTipText("If this option is selected then WildLog will periodically attempt to upload the error log to a web server. The log will be used to identify bugs and areas in need of improvement.");
        chkMnuUploadLogs.setName("chkMnuUploadLogs"); // NOI18N
        chkMnuUploadLogs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUploadLogsItemStateChanged(evt);
            }
        });
        mnuOther.add(chkMnuUploadLogs);

        chkMnuUseBundledMediaViewers.setSelected(app.getWildLogOptions().isBundledPlayers());
        chkMnuUseBundledMediaViewers.setText("Use Bundled Media Viewers (if available)");
        chkMnuUseBundledMediaViewers.setToolTipText("If this option is selected WildLog will try to use the bundled media players to open supported file types instead (if available).");
        chkMnuUseBundledMediaViewers.setName("chkMnuUseBundledMediaViewers"); // NOI18N
        chkMnuUseBundledMediaViewers.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkMnuUseBundledMediaViewersItemStateChanged(evt);
            }
        });
        mnuOther.add(chkMnuUseBundledMediaViewers);

        settingsMenu.add(mnuOther);

        menuBar.add(settingsMenu);

        helpMenu.setText("Help");
        helpMenu.setName("helpMenu"); // NOI18N
        helpMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                helpMenuMenuSelected(evt);
            }
        });

        mnuAboutWildNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildNoteIcon.png"))); // NOI18N
        mnuAboutWildNote.setText("Information about WildNote");
        mnuAboutWildNote.setToolTipText("More information about WildNote.");
        mnuAboutWildNote.setName("mnuAboutWildNote"); // NOI18N
        mnuAboutWildNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutWildNoteActionPerformed(evt);
            }
        });
        helpMenu.add(mnuAboutWildNote);

        jSeparator16.setName("jSeparator16"); // NOI18N
        helpMenu.add(jSeparator16);

        mnuUserGuide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        mnuUserGuide.setText("User Guide (PDF)");
        mnuUserGuide.setToolTipText("Opens the WildLog User Guide, or a link to a website where it can be downloaded.");
        mnuUserGuide.setName("mnuUserGuide"); // NOI18N
        mnuUserGuide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUserGuideActionPerformed(evt);
            }
        });
        helpMenu.add(mnuUserGuide);

        jSeparator17.setName("jSeparator17"); // NOI18N
        helpMenu.add(jSeparator17);

        mnuCheckUpdates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        mnuCheckUpdates.setText("Check for Updates");
        mnuCheckUpdates.setToolTipText("Check online whether there is a newer version of WildLog available.");
        mnuCheckUpdates.setName("mnuCheckUpdates"); // NOI18N
        mnuCheckUpdates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCheckUpdatesActionPerformed(evt);
            }
        });
        helpMenu.add(mnuCheckUpdates);

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

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void tabHomeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabHomeComponentShown
        lblLocations.setText("Places: " + app.getDBI().countLocations(null));
        lblVisits.setText("Periods: " + app.getDBI().countVisits(null, null));
        lblSightings.setText("Observations: " + app.getDBI().countSightings(0, null, null, null));
        lblCreatures.setText("Creatures: " + app.getDBI().countElements(null, null));
        lblWorkspaceName.setText(app.getWildLogOptions().getWorkspaceName());
    }//GEN-LAST:event_tabHomeComponentShown

    private void mnuMapStartMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMapStartMenuItemActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        String inputLat = (String) WLOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default Latitude to use for the map. (As decimal degrees, for example -33.4639)",
                "Default Latitude", JOptionPane.QUESTION_MESSAGE,  
                null, null, options.getDefaultLatitude());
        if (inputLat != null) {
            try {
                options.setDefaultLatitude(Double.parseDouble(inputLat));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        String inputLon = (String) WLOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default Longitude to use for the map. (As decimal degrees, for example 20.9562)",
                "Default Longitude", JOptionPane.QUESTION_MESSAGE,  
                null, null, options.getDefaultLongitude());
        if (inputLon != null) {
            try {
                options.setDefaultLongitude(Double.parseDouble(inputLon));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_mnuMapStartMenuItemActionPerformed

    private void mnuExifMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExifMenuItemActionPerformed
        WLFileChooser fileChooser = new WLFileChooser();
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
        int result = fileChooser.showOpenDialog(app.getMainFrame());
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            UtilsDialog.showExifPopup(app, fileChooser.getSelectedFile().toPath());
        }
    }//GEN-LAST:event_mnuExifMenuItemActionPerformed

    private void mnuSetSlideshowSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetSlideshowSizeActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        String inputFramerate = WLOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default frame size to use for the slideshows. \n (This can be any positive decimal value, for example 500)",
                options.getDefaultSlideshowSize());
        if (inputFramerate != null) {
            try {
                options.setDefaultSlideshowSize(Math.abs(Integer.parseInt(inputFramerate)));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_mnuSetSlideshowSizeActionPerformed

    private void mnuSetSlideshowSpeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSetSlideshowSpeedActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        String inputFramerate = WLOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the default framerate (in frames per second) to use for the slideshows. \n "
                        + "This can be any positive decimal value, for example 1 or 0.3 frames per second.",
                options.getDefaultSlideshowSpeed());
        if (inputFramerate != null) {
            try {
                options.setDefaultSlideshowSpeed(Math.abs(Float.parseFloat(inputFramerate)));
            }
            catch (NumberFormatException e) {
                // Do Nothing
            }
        }
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_mnuSetSlideshowSpeedActionPerformed

    private void mnuMergeElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeElementsActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            MergeElementsDialog dialog = new MergeElementsDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeElementsActionPerformed

    private void mnuMoveVisitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMoveVisitsActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            MoveVisitDialog dialog = new MoveVisitDialog(app, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMoveVisitsActionPerformed

    private void mnuCalcSunMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCalcSunMoonActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>Please <b>backup your Workspace</b> before proceding. <br>"
                        + "This will <u>replace</u> the Sun and Moon Information for your Observations with "
                        + "<u>auto generated values from the date, time and GPS information</u>.</html>",
                "Calculate Sun and Moon Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            final int choice = WLOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Automatically Calculate Sun and Moon Information", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[] {
                        "All Observations", 
                        "Only Obervations without Sun and Moon information"
                    }, null);
            if (choice != JOptionPane.CLOSED_OPTION) {
                // Close all tabs and go to the home tab
                tabbedPanel.setSelectedIndex(0);
//                while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
//                    tabbedPanel.remove(STATIC_TAB_COUNT);
//                }
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        app.getMainFrame().getGlassPane().setVisible(true);
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (choice == 0) {
                            // Update all Observations
                            setMessage("Starting the Sun and Moon Calculation");
                            setProgress(0);
                            List<Sighting> sightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                            for (int t = 0; t < sightings.size(); t++) {
                                Sighting sighting = sightings.get(t);
                                UtilsTime.calculateSunAndMoon(sighting);
                                app.getDBI().updateSighting(sighting);
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
                            List<Sighting> sightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                            for (int t = 0; t < sightings.size(); t++) {
                                Sighting sighting = sightings.get(t);
                                if (sighting.getTimeAccuracy() != null && sighting.getTimeAccuracy().isUsableTime()) {
                                    if (sighting.getMoonPhase() < 0) {
                                        sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                                    }
                                    if (sighting.getLatitude() != null && !Latitudes.NONE.equals(sighting.getLatitude())
                                            && sighting.getLongitude() != null && !Longitudes.NONE.equals(sighting.getLongitude())) {
                                        double lat = UtilsGPS.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                                        double lon = UtilsGPS.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                                        if (sighting.getMoonlight() == null || Moonlight.NONE.equals(sighting.getMoonlight()) || Moonlight.UNKNOWN.equals(sighting.getMoonlight())) {
                                            sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), lat, lon));
                                        }
                                        if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())) {
                                            sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), lat, lon));
                                        }
                                    }
                                    app.getDBI().updateSighting(sighting);
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
        }
    }//GEN-LAST:event_mnuCalcSunMoonActionPerformed

    private void mnuBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBulkImportActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                UtilsPanelGenerator.openBulkUploadTab(new BulkUploadPanel(app, this, null, null, null, null), tabbedPanel);
                return null;
            }
        });
    }//GEN-LAST:event_mnuBulkImportActionPerformed

    private void mnuImportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportCSVActionPerformed
        tabbedPanel.setSelectedIndex(0);
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you first <b><u>backup your WildLog Database</u></b> before continuing. <br>"
                        + "Press OK when you are ready to start the import process.</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Starting the CSV WildLog Import");
                    WLFileChooser fileChooser = new WLFileChooser();
                    fileChooser.setDialogTitle("Select the directory with the CSV files to import");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showOpenDialog(app.getMainFrame());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        tabbedPanel.setSelectedIndex(0);
                        Path path = fileChooser.getSelectedFile().toPath();
                        String prefix = WLOptionPane.showInputDialog(app.getMainFrame(),
                                "<html>Please provide a prefix to use for the imported data. "
                                        + "<br>The prefix will be used to map the imported data to new unique records. "
                                        + "<br>You can manually merge Creatures and move Periods afterwards.</html>",
                                "Import CSV Data", JOptionPane.QUESTION_MESSAGE);
                        if (prefix != null) {
                            int choice = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "<html><b>Would you like to <u>exclude</u> the WildLog File references</b>? "
                                    + "<br><br><hr>"
                                    + "<br>Note: The CSV Import can not import the actual files, but only the database links "
                                    + "<br>to the files. If you select NO, then you will have to manually copy the files into the correct folders."
                                    + "<br><br><hr>"
                                    + "<br>It is <b>strongly recommended</b> to select <b>YES</b> to prevent more than one record to be "
                                    + "<br>linked to the same file. The import will fail if a duplicate link is attempted.</html>",
                                    "Exclude Database File References", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            boolean excludeWildLogFiles = false;
                            if (choice == JOptionPane.YES_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                                excludeWildLogFiles = true;
                            }
                            boolean hasErrors = false;
                            try {
                                hasErrors = !app.getDBI().doImportCSV(path, prefix, !excludeWildLogFiles);
                            }
                            catch (Exception ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                hasErrors = true;
                            }
                            if (hasErrors) {
                                WLOptionPane.showMessageDialog(app.getMainFrame(),
                                        "Not all of the data could be successfully imported.",
                                        "Error Importing From CSV!", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    setMessage("Done with the CSV WildLog Import");
                    tabHomeComponentShown(null);
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuImportCSVActionPerformed

    private void mnuCreateSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateSlideshowActionPerformed
        WLFileChooser fileChooser = new WLFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG Images",
                WildLogFileExtentions.Images.JPG.getExtention().toLowerCase(), WildLogFileExtentions.Images.JPEG.getExtention().toLowerCase(),
                WildLogFileExtentions.Images.JPG.getExtention().toUpperCase(), WildLogFileExtentions.Images.JPG.getExtention().toUpperCase()));
        fileChooser.setDialogTitle("Select the JPG images to use for the Custom Slideshow...");
        int result = fileChooser.showOpenDialog(app.getMainFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Starting the Custom Slideshow");
                    List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
                    List<String> fileNames = new ArrayList<String>(files.size());
                    for (File tempFile : files) {
                        fileNames.add(tempFile.getAbsolutePath());
                    }
                    fileChooser.setDialogTitle("Please select where to save the Custom Slideshow...");
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("slideshow.mov"));
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Slideshow movie", "mov"));
                    int result = fileChooser.showSaveDialog(app.getMainFrame());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // Now create the slideshow
                        setMessage("Busy with the Custom Slideshow (this may take a while)");
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

    private void mnuExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportCSVActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Starting the CSV WildLog Export");
                Path path = WildLogPaths.WILDLOG_EXPORT_CSV_ALL.getAbsoluteFullPath();
                Files.createDirectories(path);
                setProgress(0);
                setMessage("Busy with the CSV WildLog Export");
                app.getDBI().doExportCSV(path, true, null, null, null, null, null);
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_CSV_ALL.getAbsoluteFullPath());
                setProgress(100);
                setMessage("Done with the CSV WildLog Export");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportCSVActionPerformed

    private void mnuExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportHTMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the HTML Basic Export for All Records");
                setProgress(0);
                // Elements
                List<Element> listElements = app.getDBI().listElements(null, null, null, Element.class);
                for (int t = 0; t < listElements.size(); t++) {
                    UtilsHTML.exportHTML(listElements.get(t), app, null);
                    setProgress(0 + (int)((t/(double)listElements.size())*25));
                    setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                }
                setProgress(25);
                // Locations
                setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().listLocations(null, Location.class);
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsHTML.exportHTML(listLocations.get(t), app, null);
                    setProgress(25 + (int)((t/(double)listLocations.size())*25));
                    setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                // Visits
                List<Visit> listVisits = app.getDBI().listVisits(null, null, null, Visit.class);
                for (int t = 0; t < listVisits.size(); t++) {
                    UtilsHTML.exportHTML(listVisits.get(t), app, null);
                    setProgress(50 + (int)((t/(double)listVisits.size())*25));
                    setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                for (int t = 0; t < listSightings.size(); t++) {
                    UtilsHTML.exportHTML(listSightings.get(t), app, null);
                    setProgress(75 + (int)((t/(double)listSightings.size())*25));
                    setMessage("Busy with the HTML Basic Export for All Records " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("Busy with the HTML Basic Export for All Records " + getProgress());
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_HTML_BASIC.getAbsoluteFullPath());
                setMessage("Done with the HTML Basic Export for All Records");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportHTMLActionPerformed

    private void mnuExportKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportKMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the KML Export for All Records");
                setProgress(0);
                setMessage("Busy with the KML Export for All Records " + getProgress() + "%");
                // Make sure icons and folders exist
                Path iconPath = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(WildLogSystemFile.WILDLOG_FOLDER_PREFIX);
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
                setMessage("Busy with the KML Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                Collections.sort(listSightings);
                for (int t = 0; t < listSightings.size(); t++) {
                    String key = listSightings.get(t).getElementName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>(30));
                     }
                    entries.get(key).add(listSightings.get(t).toKML(t, app));
                    setProgress(5 + (int)((t/(double)listSightings.size())*80));
                    setMessage("Busy with the KML Export for All Records " + getProgress() + "%");
                }
                // Locations
                List<Location> listLocations = app.getDBI().listLocations(null, Location.class);
                for (int t = 0; t < listLocations.size(); t++) {
                    String key = listLocations.get(t).getName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>(20));
                     }
                    // Note: Die ID moet aangaan waar die sightings gestop het
                    entries.get(key).add(listLocations.get(t).toKML(listSightings.size() + t, app));
                    setProgress(85 + (int)((t/(double)listLocations.size())*10));
                    setMessage("Busy with the KML Export for All Records " + getProgress() + "%");
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
                app.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath()
                        .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(LocalDateTime.now()) + ")"));
                setMessage("Done with the Database Backup");
                WLOptionPane.showMessageDialog(app.getMainFrame(),
                        "<html>The backup can be found in the 'WildLog\\Backup\\Backup (date)\\' folder. <br>(Note: This only backed up the database entries, the images and other files have to be backed up manually.)</html>",
                        "Backup Completed", JOptionPane.INFORMATION_MESSAGE);
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
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
            // Shutdown
            WLOptionPane.showMessageDialog(app.getMainFrame(),
                    "The WildLog Workspace has been changed to: " + System.lineSeparator() 
                    + WildLogPaths.getFullWorkspacePrefix().toString() + System.lineSeparator()
                    + "Please restart the application.",
                    "Workspace Changed!", JOptionPane.INFORMATION_MESSAGE);
//            // Making the frame not visible (or calling dispose on it) hopefully prevents this error: java.lang.InterruptedException at java.lang.Object.wait(Native Method)
//            this.setVisible(false);
            app.quit(evt);
        }
    }//GEN-LAST:event_mnuChangeWorkspaceMenuItemActionPerformed

    private void mnuCleanWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCleanWorkspaceActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[CleanWorkspace]");
        // Popup 'n warning om te se alle programme wat WL data dalk oop het moet toe gemaak word sodat ek die files kan delete of move.
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is <b>HIGHLY recommended to backup the entire WildLog Workspace folder</b> before continuing! <br>"
                        + "Please <b>close any other applications</b> that might be accessing any of the Files in the WildLog Workspace. <br>"
                        + "Note that WildLog will be automatically closed when the cleanup is finished. <br>"
                        + "This task will check that all links between the data and files are correct. <br>"
                        + "In addition all unnessasary files will be removed from the Workspace. </html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            final int recreateThumbnailsResult = WLOptionPane.showOptionDialog(app.getMainFrame(),
                    "<html>Would you like to also recreate all cached image thumbnails?"
                            + "<br/>Recreating the thumbnails can improve system performance and might correct thumbnails that are displaying incorrectly."
                            + "<br/>Warning: This step is optional but recommended. It can take very long to complete."
                            + "<br/>If this step is skipped the existing thumbnails will be used and new ones will be created dynamically as needed.</html>",
                    "Recreate Thumbnails?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, new String[] {
                        "Recreate only essential thumbnails (Recommended)",
                        "Recreate all thumbnails", 
                        "Don't recreate any thumbnails"
                    }, null);
            // Close all tabs and go to the home tab
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            // Lock the input/display and show busy message
            // Note: we never remove the Busy dialog and greyed out background since the app will be restarted anyway when done (Don't use JDialog since it stops the code until the dialog is closed...)
            tabbedPanel.setSelectedIndex(0);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JPanel panel = new JPanel(new AbsoluteLayout());
                    panel.setPreferredSize(new Dimension(400, 50));
                    panel.setBorder(new LineBorder(new Color(245, 80, 40), 3));
                    JLabel label = new JLabel("<html>Busy cleaning Workspace. Please be patient, this might take a while. <br/>"
                            + "Don't close the application until the process is finished.</html>");
                    label.setFont(new Font("Tahoma", Font.BOLD, 12));
                    label.setBorder(new LineBorder(new Color(195, 65, 20), 4));
                    panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.95f));
                    panel.add(label, new AbsoluteConstraints(410, 20, -1, -1));
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
                }
            });
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
                        feedback.println("     (See the end of the file for a summary)    ");
                        feedback.println("------------------------------------------------");
                        feedback.println("");
                        // Create a final reference to the feedback writer for use in inner classes, etc.
                        final PrintWriter finalHandleFeedback = feedback;
                        // Setup helper classes (Op hierdie stadium wil ek al die code op een plek hou, ek kan dit later in Util methods in skuif of iets...)
                        class CleanupCounter {
                            public int counter = 0;
                        }
                        class CleanupHelper {
                            private void doTheMove(DataObjectWithWildLogFile inDAOWithFile, Path inExpectedPath, Path inExpectedPrefix, WildLogFile inWildLogFile, final CleanupCounter fileCount) {
                                Path shouldBePath = inExpectedPath.resolve(inExpectedPrefix);
                                Path currentPath = inWildLogFile.getAbsolutePath().getParent();
                                boolean renameBasedOnSighting = false;
                                String fileName = inWildLogFile.getRelativePath().getFileName().toString();
                                if (inDAOWithFile instanceof Sighting) {
                                    String currentName = fileName.substring(0, fileName.lastIndexOf('.'));
                                    // Remove the sequence number (if present) and original filename from the name,
                                    // to make sure we compare the expected names without the sequence number interfering
                                    int indexSeq = currentName.lastIndexOf(UtilsFileProcessing.INDICATOR_SEQ);
                                    if (indexSeq > 0) {
                                        currentName = currentName.substring(0, indexSeq);
                                    }
                                    int indexFile = currentName.lastIndexOf(UtilsFileProcessing.INDICATOR_FILE);
                                    if (indexFile > 0) {
                                        currentName = currentName.substring(0, indexFile);
                                    }
                                    // Get the dates to use for the expected name
                                    LocalDateTime fileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(inWildLogFile.getAbsolutePath()));
                                    List<WildLogFile> lstGroupedFiles = app.getDBI().listWildLogFiles(inDAOWithFile.getWildLogFileID(), null, WildLogFile.class);
                                    LocalDateTime firstFileDate = null;
                                    for (WildLogFile wildLogFile : lstGroupedFiles) {
                                        LocalDateTime groupedFileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(wildLogFile.getAbsolutePath()));
                                        if (firstFileDate == null || (groupedFileDate != null && groupedFileDate.isBefore(firstFileDate))) {
                                            firstFileDate = groupedFileDate;
                                        }
                                    }
                                    // Compare the actual and expected names
                                    if (!((Sighting)inDAOWithFile).getCustomFileName(firstFileDate, fileDate).equals(currentName)) {
                                        renameBasedOnSighting = true;
                                        fileName = ((Sighting)inDAOWithFile).getCustomFileName(firstFileDate, fileDate) + fileName.substring(fileName.lastIndexOf('.'));
                                    }
                                }
                                if (!shouldBePath.equals(currentPath) || renameBasedOnSighting) {
                                    finalHandleFeedback.println("PROBLEM:   Incorrect or outdated file path   : " + inWildLogFile.getAbsolutePath());
                                    finalHandleFeedback.println("+RESOLVED: Moved the file to the correct path: " + shouldBePath.resolve(fileName));
                                    // "Re-upload" the file to the correct location
                                    UtilsFileProcessing.performFileUpload(
                                            inDAOWithFile,
                                            inExpectedPrefix,
                                            new File[]{inWildLogFile.getAbsolutePath().toFile()},
                                            null, 
                                            app, false, null, false, true);
                                    // Delete the wrong entry
                                    app.getDBI().deleteWildLogFile(inWildLogFile.getDBFilePath());
                                    fileCount.counter++;
                                }
                            }
                            public void moveFilesToCorrectFolders(DataObjectWithWildLogFile inDAOWithFile, WildLogFile inWildLogFile, Path inPrefix, final CleanupCounter fileCount) {
                                // Check to make sure the parent paths are correct, if not then move the file to the correct place and add a new DB entry before deleting the old one
                                // Maak seker alle DB paths is relative (nie absolute nie) en begin met propper WL roots
                                if (WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
                                    doTheMove(inDAOWithFile,
                                            WildLogPaths.WILDLOG_FILES_IMAGES.getAbsoluteFullPath(),
                                            inPrefix,
                                            inWildLogFile,
                                            fileCount);
                                }
                                else
                                if (WildLogFileType.MOVIE.equals(inWildLogFile.getFileType())) {
                                    doTheMove(inDAOWithFile,
                                            WildLogPaths.WILDLOG_FILES_MOVIES.getAbsoluteFullPath(),
                                            inPrefix,
                                            inWildLogFile,
                                            fileCount);
                                }
                                else {
                                    doTheMove(inDAOWithFile,
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
                                    public FileVisitResult visitFile(Path originalFile, BasicFileAttributes attrs) throws IOException {
                                        if (attrs.isRegularFile()) {
                                            // Kyk in DB of die path bestaan, as dit nie daar is nie delete die file
// TODO: As die file bestaan maar nie in die DB link nie kan mens dalk probeer "slim" wees en die link afly deur ander info soos size, date en die path...
                                            WildLogFile wildLogFile = new WildLogFile();
                                            wildLogFile.setDBFilePath(WildLogPaths.getFullWorkspacePrefix().relativize(originalFile).toString());
                                            if (app.getDBI().findWildLogFile(wildLogFile.getDBFilePath(), null, null, WildLogFile.class) == null) {
                                                finalHandleFeedback.println("PROBLEM:   File in Workspace not present in the database: " + wildLogFile.getAbsolutePath());
                                                finalHandleFeedback.println("+RESOLVED: Moved the file from the Workspace to the LostFiles folder: " + wildLogFile.getDBFilePath());
                                                // Move the file to the LostFiles folder (don't delete, because we might want the file back to re-upload, etc.) 
                                                Path destination = WildLogPaths.WILDLOG_LOST_FILES.getAbsoluteFullPath().resolve(WildLogPaths.getFullWorkspacePrefix().relativize(originalFile));
                                                while (Files.exists(destination)) {
                                                    destination = destination.getParent().resolve("wl_" + destination.getFileName());
                                                }
                                                UtilsFileProcessing.copyFile(originalFile, destination, false, true);
                                                UtilsFileProcessing.deleteRecursive(originalFile.toFile());
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
                        // Do a quick DB backup
                        app.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS_CHECK_AND_CLEAN.getAbsoluteFullPath()
                                .resolve(UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now())));
                        setProgress(1);
                        // ---------------------1---------------------
                        // First check database files
                        // Maak seker alle files in die tabel wys na 'n location/element/ens wat bestaan (geen "floaters" mag teenwoordig wees nie)
                        setMessage("Cleanup Step 1: Validate database references to the files in the Workspace... " + getProgress() + "%");
                        finalHandleFeedback.println("** Starting Workspace Cleanup: " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("1) Make sure the File records in the database contain valid values and correctly link to existing data and Workspace files.");
                        List<WildLogFile> allFiles = app.getDBI().listWildLogFiles(null, null, WildLogFile.class);
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
                                finalHandleFeedback.println("PROBLEM:   File record without an ID. FilePath: " + wildLogFile.getDBFilePath());
                                finalHandleFeedback.println("+RESOLVED: Tried to delete the database file record and file on disk.");
                                app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                filesWithoutID++;
                                continue;
                            }
                            if (wildLogFile.getDBFilePath() == null) {
                                finalHandleFeedback.println("PROBLEM:   File path missing from database record. FileID: " + wildLogFile.getId());
                                finalHandleFeedback.println("+RESOLVED: Deleted the database file record.");
                                app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                filesWithoutPath++;
                                continue;
                            }
                            if (!Files.exists(wildLogFile.getAbsolutePath())) {
                                finalHandleFeedback.println("PROBLEM:   File record in the database can't be found on disk. FilePath: " + wildLogFile.getDBFilePath());
                                finalHandleFeedback.println("+RESOLVED: Deleted the database file record.");
                                app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
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
                                finalHandleFeedback.println("PROBLEM:   Unknown FileType of database file record. FilePath: " + wildLogFile.getDBFilePath()
                                        + ", FileType: " + wildLogFile.getFileType());
                                finalHandleFeedback.println("+RESOLVED: Changed FileType to " + WildLogFileType.OTHER + ".");
                                wildLogFile.setFileType(WildLogFileType.OTHER);
                                app.getDBI().updateWildLogFile(wildLogFile);
                                filesWithBadType++;
                            }
                            // Check the WildLogFile's linkage
                            if (wildLogFile.getId().startsWith(Element.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Element temp = app.getDBI().findElement(wildLogFile.getId().substring(Element.WILDLOGFILE_ID_PREFIX.length()), Element.class);
                                if (temp == null) {
                                    finalHandleFeedback.println("PROBLEM:   Could not find linked Creature for this file record. FilePath: " + wildLogFile.getDBFilePath()
                                            + ", ID: " + wildLogFile.getId() + ", CreatureName Used: " + wildLogFile.getId().substring(Element.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(temp, 
                                        wildLogFile,
                                        Paths.get(Element.WILDLOG_FOLDER_PREFIX, temp.getPrimaryName()).normalize(),
                                        filesMoved);
                            }
                            else 
                            if (wildLogFile.getId().startsWith(Visit.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Visit temp = app.getDBI().findVisit(wildLogFile.getId().substring(Visit.WILDLOGFILE_ID_PREFIX.length()), Visit.class);
                                if (temp == null) {
                                    finalHandleFeedback.println("PROBLEM:   Could not find linked Period for this file record. FilePath: " + wildLogFile.getDBFilePath()
                                            + ", ID: " + wildLogFile.getId() + ", PeriodName Used: " + wildLogFile.getId().substring(Visit.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file databse record and file from disk.");
                                    app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(temp, 
                                        wildLogFile,
                                        Paths.get(Visit.WILDLOG_FOLDER_PREFIX, temp.getLocationName(), temp.getName()).normalize(),
                                        filesMoved);
                            }
                            else 
                            if (wildLogFile.getId().startsWith(Location.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                final Location temp = app.getDBI().findLocation(wildLogFile.getId().substring(Location.WILDLOGFILE_ID_PREFIX.length()), Location.class);
                                if (temp == null) {
                                    finalHandleFeedback.println("PROBLEM:   Could not find linked Place for this file. FilePath: " + wildLogFile.getDBFilePath()
                                            + ", ID: " + wildLogFile.getId() + ", PlaceName Used: " + wildLogFile.getId().substring(Location.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(temp, 
                                        wildLogFile,
                                        Paths.get(Location.WILDLOG_FOLDER_PREFIX, temp.getName()).normalize(),
                                        filesMoved);
                            }
                            else 
                            if (wildLogFile.getId().startsWith(Sighting.WILDLOGFILE_ID_PREFIX)) {
                                // Make sure it is linked
                                Sighting temp = null;
                                try {
                                    temp = app.getDBI().findSighting(Long.parseLong(wildLogFile.getId().substring(Sighting.WILDLOGFILE_ID_PREFIX.length())), Sighting.class);
                                }
                                catch (NumberFormatException ex) {
                                    finalHandleFeedback.println("PROBLEM:     Can't get linked Observation's ID.");
                                    finalHandleFeedback.println("-UNRESOLVED: Try to continue to delete the file.");
                                }
                                if (temp == null) {
                                    finalHandleFeedback.println("PROBLEM:   Could not find linked Observation for this file. FilePath: " + wildLogFile.getDBFilePath()
                                            + ", ID: " + wildLogFile.getId() + ", ObservationID Used: " + wildLogFile.getId().substring(Sighting.WILDLOGFILE_ID_PREFIX.length()));
                                    finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                    app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
                                    filesWithBadID++;
                                    continue;
                                }
                                // Make sure the file path is correct
                                cleanupHelper.moveFilesToCorrectFolders(temp, 
                                        wildLogFile,
                                        Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(temp.toPath()).normalize(),
                                        filesMoved);
                            }
                            else {
                                finalHandleFeedback.println("PROBLEM:   File ID is not correctly formatted.");
                                finalHandleFeedback.println("+RESOLVED: Deleted the file database record and file from disk.");
                                app.getDBI().deleteWildLogFile(wildLogFile.getDBFilePath());
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
// TODO: If a file wasn't found then try to use the existing name and FileDate and FileSize to guess the link (or is it better just to move the files to the lost folder???)
                        setProgress(20);

                        // ---------------------2---------------------
                        setMessage("Cleanup Step 2: Validate that the Workspace files are in the database... " + getProgress() + "%");
                        finalHandleFeedback.println("");
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
                            WildLogApp.LOGGER.log(Level.ERROR, "Could not check all files on disk.");
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            finalHandleFeedback.println("PROBLEM:     Could not check all files on disk.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(40);

                        // ---------------------3---------------------
                        // By this time the file link should be "trusted" enough to use it to update inconsistent FileDate and FileSize values (not just empty ones)
                        int filesWithIncorrectDate = 0;
                        int filesWithIncorrectSize = 0;
                        setMessage("Cleanup Step 3: Check the file size and dates... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("3) Compare the size and modified date of the files on disk to the values stored in the database.");
                        allFiles = app.getDBI().listWildLogFiles(null, null, WildLogFile.class);
                        fileProcessCounter = 0;
                        for (WildLogFile wildLogFile : allFiles) {
                            LocalDateTime actualFileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFileDate(wildLogFile.getAbsolutePath()));
                            long actualFileSize = Files.size(wildLogFile.getAbsolutePath());
                            if (actualFileDate != null) {
                                if (wildLogFile.getFileDate() == null || !actualFileDate.isEqual(UtilsTime.getLocalDateTimeFromDate(wildLogFile.getFileDate()))) {
                                    finalHandleFeedback.println("PROBLEM:   The Modified Date of the file on disk is not the same as the value stored in the database. "
                                            + "FilePath: " + wildLogFile.getAbsolutePath());
                                    String oldDate;
                                    if (wildLogFile.getFileDate() == null) {
                                        oldDate = "null";
                                    }
                                    else {
                                        oldDate = UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(UtilsTime.getLocalDateTimeFromDate(wildLogFile.getFileDate()));
                                    }
                                    finalHandleFeedback.println("+RESOLVED: Updated the database Modified Date value of the file record. "
                                            + "Changed " + oldDate
                                            + " to " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(actualFileDate));
                                    wildLogFile.setFileDate(UtilsTime.getDateFromLocalDateTime(actualFileDate));
                                    app.getDBI().updateWildLogFile(wildLogFile);
                                    filesWithIncorrectDate++;
                                }
                            }
                            if (actualFileSize > 0) {
                                if (actualFileSize != wildLogFile.getFileSize()) {
                                    finalHandleFeedback.println("PROBLEM:   The File Size of the file on disk is not the same as the value stored in the database. "
                                            + "FilePath: " + wildLogFile.getAbsolutePath());
                                    finalHandleFeedback.println("+RESOLVED: Updated the database File Size value of the file record. "
                                            + "Changed " + wildLogFile.getFileSize() + " to " + actualFileSize);
                                    wildLogFile.setFileSize(actualFileSize);
                                    app.getDBI().updateWildLogFile(wildLogFile);
                                    filesWithIncorrectSize++;
                                }
                            }
                            setProgress(40 + (int)(fileProcessCounter++/(double)allFiles.size()*12));
                            setMessage("Cleanup Step 3: Check the file size and dates... " + getProgress() + "%");
                        }
                        setProgress(52);
                        
                        // ---------------------4---------------------
                        // As alles klaar is delete alle lee en temporary folders
                        setMessage("Cleanup Step 4: Delete empty folders in WildLog\\Files\\... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("4) Delete all empty folders in the Workspace's Images, Movies and Other files folders.");
                        try {
                            UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(WildLogPaths.WILDLOG_FILES.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, "Could not delete all empty folders.");
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            finalHandleFeedback.println("PROBLEM:     Could not delete all empty folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(52);

                        // ---------------------5---------------------
                        // Delete alle temporary/onnodige files en folders
                        setMessage("Cleanup Step 5: Delete exports and thumbnails... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("5) Delete all exports and thumbnails (since these files can be recreated from within WildLog).");
                        try {
                            UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, "Could not delete export folders.");
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            finalHandleFeedback.println("PROBLEM:     Could not delete export folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(55);
                        setMessage("Cleanup Step 5: Delete exports and thumbnails... " + getProgress() + "%");
                        try {
                            UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath().toFile());
                        }
                        catch (final IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, "Could not delete thumbnail folders.");
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            finalHandleFeedback.println("PROBLEM:     Could not delete thumbnail folders.");
                            finalHandleFeedback.println("-UNRESOLVED: Unexpected error accessing file...");
                        }
                        setProgress(65);

                        // ---------------------6---------------------
                        // Check the rest of the data for inconsistencies (all Locations, Visits, Creatures and Observations link correctly)
                        setMessage("Cleanup Step 6: Check links between records in the database... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("6) Make sure Places, Periods, Creatures and Observations all have correct links to each other.");
                        // Check Visits
                        List<Visit> allVisits = app.getDBI().listVisits(null, null, null, Visit.class);
                        int badDataLinks = 0;
                        int countVisits = 0;
                        for (Visit visit : allVisits) {
                            // Validate the Visit to Location link
                            Location temp = app.getDBI().findLocation(visit.getLocationName(), Location.class);
                            if (temp == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:   Could not find link between Period and Place. "
                                        + "Period: " + visit.getName() + ", Place: " + visit.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Period to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().findLocation("WildLog_lost_and_found", Location.class);
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createLocation(newLocation);
                                }
                                visit.setLocationName("WildLog_lost_and_found");
                                // Still an issue with sightings not going to point to the correct place... (handled in the code below)
                                app.getDBI().updateVisit(visit, visit.getName());
                            }
                            countVisits++;
                            setProgress(65 + (int)(countVisits/(double)allVisits.size()*3));
                            setMessage("Cleanup Step 6: Check links between records in the database... " + getProgress() + "%");
                        }
                        // Check Sightings
                        List<Sighting> allSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                        int countSightings = 0;
                        for (Sighting sighting : allSightings) {
                            // Validate the Sighting to Location link
                            Location tempLocation = app.getDBI().findLocation(sighting.getLocationName(), Location.class);
                            if (tempLocation == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:   Could not find link between Observation and Place. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Place: " + sighting.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().findLocation("WildLog_lost_and_found", Location.class);
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createLocation(newLocation);
                                }
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().updateSighting(sighting);
                            }
                            // Validate the Sighting to Element link
                            Element tempElement = app.getDBI().findElement(sighting.getElementName(), Element.class);
                            if (tempElement == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:   Could not find link between Observation and Creature. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Creature: " + sighting.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Creature called 'WildLog_lost_and_found'.");
                                Element newElement = app.getDBI().findElement("WildLog_lost_and_found", Element.class);
                                if (newElement == null) {
                                    newElement = new Element("WildLog_lost_and_found");
                                    app.getDBI().createElement(newElement);
                                }
                                sighting.setElementName("WildLog_lost_and_found");
                                app.getDBI().updateSighting(sighting);
                            }
                            // Validate the Sighting to Visit link
                            Visit tempVisit = app.getDBI().findVisit(sighting.getVisitName(), Visit.class);
                            if (tempVisit == null) {
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:   Could not find link between Observation and Period. "
                                        + "Observation: " + sighting.getSightingCounter() + ", Period: " + sighting.getVisitName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation to a new Period called 'WildLog_lost_and_found'.");
                                // Visit
                                Visit newVisit = app.getDBI().findVisit("WildLog_lost_and_found", Visit.class);
                                if (newVisit == null) {
                                    newVisit = new Visit("WildLog_lost_and_found");
                                    newVisit.setLocationName("WildLog_lost_and_found");
                                    app.getDBI().createVisit(newVisit);
                                }
                                sighting.setVisitName("WildLog_lost_and_found");
                                // Location
                                Location newLocation = app.getDBI().findLocation("WildLog_lost_and_found", Location.class);
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createLocation(newLocation);
                                }
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().updateSighting(sighting);
                            }
                            // Make sure the Sighting is using a legitimate Location-Visit pair
                            Visit checkSightingVisit = app.getDBI().findVisit(sighting.getVisitName(), Visit.class);
                            if (!checkSightingVisit.getLocationName().equalsIgnoreCase(sighting.getLocationName())) {
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:   The Observation and Period references different Places. "
                                        + "Observation: " + sighting.getLocationName() + ", Period: " + checkSightingVisit.getLocationName());
                                finalHandleFeedback.println("+RESOLVED: Moved Observation and Period to a new Place called 'WildLog_lost_and_found'.");
                                Location newLocation = app.getDBI().findLocation("WildLog_lost_and_found", Location.class);
                                if (newLocation == null) {
                                    newLocation = new Location("WildLog_lost_and_found");
                                    app.getDBI().createLocation(newLocation);
                                }
                                // Update sighting
                                sighting.setLocationName("WildLog_lost_and_found");
                                app.getDBI().updateSighting(sighting);
                                // Update visit
                                checkSightingVisit.setLocationName("WildLog_lost_and_found");
                                app.getDBI().updateVisit(checkSightingVisit, checkSightingVisit.getName());
                            }
                            countSightings++;
                            setProgress(68 + (int)(countSightings/(double)allSightings.size()*4));
                            setMessage("Cleanup Step 6: Check links between records in the database... " + getProgress() + "%");
                        }
                        setProgress(72);
                        
                        // ---------------------7---------------------
                        // Check GPS Accuracy
                        setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("7) Check the GPS Accuracy values.");
                        allSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                        int countGPSAccuracy = 0;
                        int badGPSAccuracy = 0;
                        for (Sighting sighting : allSightings) {
                            if ((sighting.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(sighting.getGPSAccuracy()))
                                    && sighting.getLatitude() != null && !Latitudes.NONE.equals(sighting.getLatitude())
                                    && sighting.getLongitude() != null && !Longitudes.NONE.equals(sighting.getLongitude())) {
                                sighting.setGPSAccuracy(GPSAccuracy.AVERAGE);
                                sighting.setGPSAccuracyValue(GPSAccuracy.AVERAGE.getMaxMeters());
                                app.getDBI().updateSighting(sighting);
                                badGPSAccuracy++;
                                finalHandleFeedback.println("PROBLEM:   GPS information found without GPS Accuracy for Observation (" + sighting.getSightingCounter() + ").");
                                finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy to a default value of AVERAGE.");
                            }
                            else {
                                if ((sighting.getGPSAccuracy() != null && !GPSAccuracy.NONE.equals(sighting.getGPSAccuracy()))
                                        && (sighting.getLatitude() == null || Latitudes.NONE.equals(sighting.getLatitude())
                                        || sighting.getLongitude() == null || Longitudes.NONE.equals(sighting.getLongitude()))) {
                                    sighting.setGPSAccuracy(GPSAccuracy.NONE);
                                    sighting.setGPSAccuracyValue(GPSAccuracy.NONE.getMaxMeters());
                                    app.getDBI().updateSighting(sighting);
                                    badGPSAccuracy++;
                                    finalHandleFeedback.println("PROBLEM:   GPS Accuracy information found without GPS location for Observation (" + sighting.getSightingCounter() + ").");
                                    finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy to a value of NONE.");
                                }
                            }
                            if (sighting.getGPSAccuracy() != null && (sighting.getGPSAccuracyValue() < sighting.getGPSAccuracy().getMinMeters()
                                    || sighting.getGPSAccuracyValue() > sighting.getGPSAccuracy().getMaxMeters())) {
                                sighting.setGPSAccuracyValue(sighting.getGPSAccuracy().getMaxMeters());
                                app.getDBI().updateSighting(sighting);
                                badGPSAccuracy++;
                                finalHandleFeedback.println("PROBLEM:   GPS Accuracy category information found with a GPS Accuracy Value outside its bounds for Observation (" + sighting.getSightingCounter() + ").");
                                finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy Value to the maximum value associated with the GPS Accuracy category.");
                            }
                            countGPSAccuracy++;
                            setProgress(72 + (int)(countGPSAccuracy/(double)allSightings.size()*2));
                            setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + getProgress() + "%");
                        }
                        List<Location> allLocations = app.getDBI().listLocations(null, Location.class);
                        countGPSAccuracy = 0;
                        for (Location location : allLocations) {
                            if ((location.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(location.getGPSAccuracy()))
                                    && location.getLatitude() != null && !Latitudes.NONE.equals(location.getLatitude())
                                    && location.getLongitude() != null && !Longitudes.NONE.equals(location.getLongitude())) {
                                location.setGPSAccuracy(GPSAccuracy.AVERAGE);
                                location.setGPSAccuracyValue(GPSAccuracy.AVERAGE.getMaxMeters());
                                app.getDBI().updateLocation(location, location.getName());
                                badGPSAccuracy++;
                                finalHandleFeedback.println("PROBLEM:   GPS information found without GPS Accuracy for Place (" + location.getName() + ").");
                                finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy to a default value of AVERAGE.");
                            }
                            else {
                                if ((location.getGPSAccuracy() != null && !GPSAccuracy.NONE.equals(location.getGPSAccuracy()))
                                        && (location.getLatitude() == null || Latitudes.NONE.equals(location.getLatitude())
                                        || location.getLongitude() == null || Longitudes.NONE.equals(location.getLongitude()))) {
                                    location.setGPSAccuracy(GPSAccuracy.NONE);
                                    location.setGPSAccuracyValue(GPSAccuracy.NONE.getMaxMeters());
                                    app.getDBI().updateLocation(location, location.getName());
                                    badGPSAccuracy++;
                                    finalHandleFeedback.println("PROBLEM:   GPS Accuracy information found without GPS location for Place (" + location.getName() + ").");
                                    finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy to a value of NONE.");
                                }
                            }
                            if (location.getGPSAccuracy() != null && (location.getGPSAccuracyValue() < location.getGPSAccuracy().getMinMeters()
                                    || location.getGPSAccuracyValue() > location.getGPSAccuracy().getMaxMeters())) {
                                location.setGPSAccuracyValue(location.getGPSAccuracy().getMaxMeters());
                                app.getDBI().updateLocation(location, location.getName());
                                badGPSAccuracy++;
                                finalHandleFeedback.println("PROBLEM:   GPS Accuracy category information found with a GPS Accuracy Value outside its bounds for Place (" + location.getName() + ").");
                                finalHandleFeedback.println("+RESOLVED: Set the GPS Accuracy Value to the maximum value associated with the GPS Accuracy category.");
                            }
                            countGPSAccuracy++;
                            setProgress(72 + (int)(countGPSAccuracy/(double)allSightings.size()*2));
                            setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + getProgress() + "%");
                        }
                        setProgress(76);
                        
                        // ---------------------8---------------------
                        // Checks Visit dates
                        setMessage("Cleanup Step 8: Check the Period and linked Observation  date ranges... " + getProgress() + "%");
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("8) Check the Period and linked Observation date ranges.");
                        allSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                        int badVisitDates = 0;
                        int linkCount = 0;
                        Set<String> processedVisits = new HashSet<>();
                        for (Sighting sighting : allSightings) {
                            if (processedVisits.add(sighting.getVisitName())) {
                                Visit visit = app.getDBI().findVisit(sighting.getVisitName(), Visit.class);
                                if (visit.getStartDate() == null) {
                                    finalHandleFeedback.println("WARNING:   The Period (" + sighting.getVisitName() + ") does not have a Start Date.");
                                    finalHandleFeedback.println("+UNRESOLVED: It is recommended for all Periods to have atleast a Start Date. The dates are used by the Reports and Maps.");
                                    badVisitDates++;
                                }
                                if (visit.getStartDate() != null && visit.getEndDate() != null && visit.getEndDate().before(visit.getStartDate())) {
                                    finalHandleFeedback.println("WARNING:   The Period (" + sighting.getVisitName() + ") has an End Date that is before the Start Date.");
                                    finalHandleFeedback.println("+UNRESOLVED: It is recommended for all Periods to have a valid date range.");
                                    badVisitDates++;
                                }
                                LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
                                if ((visit.getStartDate() != null && sightingDate.isBefore(UtilsTime.getLocalDateFromDate(visit.getStartDate()))) 
                                        || (visit.getEndDate() != null && sightingDate.isAfter(UtilsTime.getLocalDateFromDate(visit.getEndDate())))) {
                                    finalHandleFeedback.println("WARNING:   The date for Observation (" + sighting.getSightingCounter() + ") does not fall within the dates from Period (" + sighting.getVisitName() + ").");
                                    finalHandleFeedback.println("+UNRESOLVED: It is recommended for all Observations to use dates that fall within the Start date and End Date of the linked Period.");
                                    badVisitDates++;
                                }
                            }
                            linkCount++;
                            setProgress(76 + (int)(linkCount/(double)allSightings.size()*2));
                            setMessage("Cleanup Step 8: Check the Period and linked Observation  date ranges... " + getProgress() + "%");
                        }
                        
                        // ---------------------9---------------------
                        // Re-create die default thumbnails
                        if (recreateThumbnailsResult == 0) {
                            // Recreate essential thumbnails
                            setMessage("Cleanup Step 9: (Optional) Recreating essential default thumbnails... " + getProgress() + "%");
                            finalHandleFeedback.println("");
                            finalHandleFeedback.println("9) Recreate the default thumbnails for essential images.");
                            List<WildLogFile> listFiles = new ArrayList<>(128);
                            List<Element> lstElements = app.getDBI().listElements(null, null, null, Element.class);
                            for (Element element : lstElements) {
                                WildLogFile image = app.getDBI().findWildLogFile(null, element.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class);
                                if (image != null) {
                                    listFiles.add(image);
                                }
                            }
                            List<Location> lstLocation = app.getDBI().listLocations(null, Location.class);
                            for (Location location : lstLocation) {
                                WildLogFile image = app.getDBI().findWildLogFile(null, location.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class);
                                if (image != null) {
                                    listFiles.add(image);
                                }
                            }
                            List<Visit> lstVisit = app.getDBI().listVisits(null, null, null, Visit.class);
                            for (Visit visit : lstVisit) {
                                WildLogFile image = app.getDBI().findWildLogFile(null, visit.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class);
                                if (image != null) {
                                    listFiles.add(image);
                                }
                            }
                            ExecutorService executorService = Executors.newFixedThreadPool(app.getThreadCount(), new NamedThreadFactory("WL_CleanWorkspace"));
                            final CleanupCounter countThumbnails = new CleanupCounter();
                            for (final WildLogFile wildLogFile : listFiles) {
                                executorService.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Maak net die nodigste thumbnails, want anders vat dinge donners lank
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                                        // Not going to bother with synchornization here, since it's just the progress bar
                                        countThumbnails.counter++;
                                        setProgress(78 + (int)(countThumbnails.counter/(double)listFiles.size()*21));
                                        setMessage("Cleanup Step 9: (Optional) Recreating essential default thumbnails... " + getProgress() + "%");
                                    }
                                });
                            }
                            for (WildLogSystemImages systemFile : WildLogSystemImages.values()) {
                                for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                                    systemFile.getWildLogFile().getAbsoluteThumbnailPath(size);
                                }
                            }
                            // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
                            executorService.shutdown();
                            if (!executorService.awaitTermination(2, TimeUnit.DAYS)) {
                                finalHandleFeedback.println("PROBLEM:     Processing the thumbnails took too long.");
                                finalHandleFeedback.println("-UNRESOLVED: Thumbnails can be created on demand by the application.");
                            }
                            setProgress(99);
                        }
                        else 
                        if (recreateThumbnailsResult == 1) {
                            // Recreate all thumbnails
                            setMessage("Cleanup Step 9: (Optional) Recreating all default thumbnails... " + getProgress() + "%");
                            finalHandleFeedback.println("");
                            finalHandleFeedback.println("9) Recreate the default thumbnails for all images.");
                            List<WildLogFile> listFiles =app.getDBI().listWildLogFiles(null, WildLogFileType.IMAGE, WildLogFile.class);
                            // Sort the files by name to put the related location, element, visit and sighting images close to one another, 
                            // otherwise the loading from disk actually gets worse because the files are all over the place.
                            // This way the read-ahead will get more hits and be faster.
                            Collections.sort(listFiles, new Comparator<WildLogFileCore>() {
                                @Override
                                public int compare(WildLogFileCore wlFile1, WildLogFileCore wlFile2) {
                                    String prioritisedID1 = wlFile1.getId();
                                    String prioritisedID2 = wlFile2.getId();
                                    // Maak seker Visits word voor Sightings gedoen
                                    if (prioritisedID1.charAt(0) == 'V') {
                                        prioritisedID1 = "LZZZ" + prioritisedID1;
                                    }
                                    if (prioritisedID2.charAt(0) == 'V') {
                                        prioritisedID2 = "LZZZ" + prioritisedID2;
                                    }
                                    return prioritisedID1.compareTo(prioritisedID2);
                                }
                            });
                            ExecutorService executorService = Executors.newFixedThreadPool(app.getThreadCount(), new NamedThreadFactory("WL_CleanWorkspace"));
                            final CleanupCounter countThumbnails = new CleanupCounter();
                            for (final WildLogFile wildLogFile : listFiles) {
                                executorService.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Maak net die nodigste thumbnails, want anders vat dinge donners lank
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                                        // Not going to bother with synchornization here, since it's just the progress bar
                                        countThumbnails.counter++;
                                        setProgress(78 + (int)(countThumbnails.counter/(double)listFiles.size()*21));
                                        setMessage("Cleanup Step 9: (Optional) Recreating all default thumbnails... " + getProgress() + "%");
                                    }
                                });
                            }
                            for (WildLogSystemImages systemFile : WildLogSystemImages.values()) {
                                for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                                    systemFile.getWildLogFile().getAbsoluteThumbnailPath(size);
                                }
                            }
                            // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
                            executorService.shutdown();
                            if (!executorService.awaitTermination(2, TimeUnit.DAYS)) {
                                finalHandleFeedback.println("PROBLEM:     Processing the thumbnails took too long.");
                                finalHandleFeedback.println("-UNRESOLVED: Thumbnails can be created on demand by the application.");
                            }
                            setProgress(99);
                        }
                        else {
                            setProgress(99);
                            setMessage("Cleanup Step 9: (Optional) Recreating all default thumbnails... SKIPPED " + getProgress() + "%");
                            finalHandleFeedback.println("9) Recreate the default thumbnails for all images. SKIPPED");
                        }

                        // ---------------------?---------------------
                        // Recreate the database (to optimise file size and indexes)
                        // TODO: Recreate the database. See http://www.h2database.com/html/features.html#compacting
//                        public static void compact(String dir, String dbName,
//        String user, String password) throws Exception {
//    String url = "jdbc:h2:" + dir + "/" + dbName;
//    String file = "data/test.sql";
//    Script.execute(url, user, password, file);
//    DeleteDbFiles.execute(dir, dbName, true);
//    RunScript.execute(url, user, password, file, null, false);
//}
                        
                        // ---------------------?---------------------
                        // Scan through the entire folder and delete all non-wildlog files and folders (remember to keep Maps, Backup and the feedback file)
// TODO: Maybe delete all non-wildlog files from the rest of the Workspace during cleanup?

                        // --------------------- DONE ---------------------
                        // Write out the summary
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("** Finished Workspace Cleanup: " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("+++++++++++++++++++ SUMMARY ++++++++++++++++++++");
                        finalHandleFeedback.println("Files on disk moved to new folder: " + filesMoved.counter);
                        finalHandleFeedback.println("Database file records with no reference to a file on disk: " + filesWithoutPath);
                        finalHandleFeedback.println("Database file records with a reference to a file on disk, but the file was not found: " + filesNotOnDisk);
                        finalHandleFeedback.println("Database file records with no ID: " + filesWithoutID);
                        finalHandleFeedback.println("Database file records with incorrect ID: " + filesWithBadID);
                        finalHandleFeedback.println("Database file records with incorrect type: " + filesWithBadType);
                        finalHandleFeedback.println("Database file records with missing non-essential data: " + filesWithMissingData);
                        finalHandleFeedback.println("Workspace files not found in the database: " + filesNotInDB.counter);
                        finalHandleFeedback.println("Database file records with an incorrect File Modified Date stored in the database: " + filesWithIncorrectDate);
                        finalHandleFeedback.println("Database file records with an incorrect File Size stored in the database: " + filesWithIncorrectSize);
                        finalHandleFeedback.println("Incorrect links between database records: " + badDataLinks);
                        finalHandleFeedback.println("Records with incorrect GPS Accuracy: " + badGPSAccuracy);
                        finalHandleFeedback.println("Periods with problematic Start Date and End Date values: " + badVisitDates);
                        finalHandleFeedback.println("");
                        finalHandleFeedback.println("+++++++++++++++++++ DURATION +++++++++++++++++++");
                        long duration = System.currentTimeMillis() - startTime;
                        int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                        int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                        int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                        feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                        // Print info to logs aswell (for upload)
                        WildLogApp.LOGGER.log(Level.INFO, "+++++++++++++++++++ SUMMARY ++++++++++++++++++++");
                        WildLogApp.LOGGER.log(Level.INFO, "Files on disk moved to new folder: {}", filesMoved.counter);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with no reference to a file on disk: {}", filesWithoutPath);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with a reference to a file on disk, but the file was not found: {}", filesNotOnDisk);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with no ID: {}", filesWithoutID);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with incorrect ID: {}", filesWithBadID);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with incorrect type: {}", filesWithBadType);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with missing non-essential data: {}", filesWithMissingData);
                        WildLogApp.LOGGER.log(Level.INFO, "Workspace files not found in the database: {}", filesNotInDB.counter);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with an incorrect File Modified Date: {}", filesWithIncorrectDate);
                        WildLogApp.LOGGER.log(Level.INFO, "Database file records with an incorrect File Size: {}", filesWithIncorrectSize);
                        WildLogApp.LOGGER.log(Level.INFO, "Incorrect links between database records: {}", badDataLinks);
                        WildLogApp.LOGGER.log(Level.INFO, "Records with incorrect GPS Accuracy: {}", badGPSAccuracy);
                        WildLogApp.LOGGER.log(Level.INFO, "Periods with problematic Start Date and End Date values: {}", badVisitDates);
                        WildLogApp.LOGGER.log(Level.INFO, "+++++++++++++++++++ DURATION +++++++++++++++++++");
                        WildLogApp.LOGGER.log(Level.INFO, "{} hours, {} minutes, {} seconds", new Object[]{hours, minutes, seconds});
                        setMessage("Finished Workspace Cleanup...");
                        setProgress(100);
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, "An exception occured while cleaning the Workspace!!");
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        if (feedback != null) {
                            feedback.println("PROBLEM:     An exception occured while cleaning the Workspace!!");
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
                    // that asks whether you want to close the application or not, and it's best to rather restart after the cleanup.
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
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>Please <b>backup your Workspace</b> before proceding. <br>"
                        + "This will <u>replace</u> the Duration information for all Observations with "
                        + "<u>auto generated values from the uploaded images</u>.</html>",
                "Calculate Observation Duration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            final int choice = WLOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Automatically Calculate Duration", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[] {
                        "All Observations", 
                        "Only Obervations without a Duration"
                    }, null);
            if (choice != JOptionPane.CLOSED_OPTION) {
                // Close all tabs and go to the home tab
                tabbedPanel.setSelectedIndex(0);
//                while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
//                    tabbedPanel.remove(STATIC_TAB_COUNT);
//                }
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        app.getMainFrame().getGlassPane().setVisible(true);
                        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (choice == 0) {
                            // Update all observations
                            setMessage("Starting the Duration Calculation");
                            setProgress(0);
                            List<Sighting> sightingList = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                            for (int t = 0; t < sightingList.size(); t++) {
                                Sighting sighting = sightingList.get(t);
                                // Using only images here since it is more reliable (and safer to automate) than movies
                                List<WildLogFile> files = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class);
                                if (!files.isEmpty()) {
                                    Collections.sort(files);
                                    Date startDate = UtilsImageProcessing.getDateFromImage(files.get(0).getAbsolutePath());
                                    Date endDate = UtilsImageProcessing.getDateFromImage(files.get(files.size()-1).getAbsolutePath());
                                    double difference = (endDate.getTime() - startDate.getTime())/1000;
                                    int minutes = (int)difference/60;
                                    double seconds = difference - minutes*60.0;
                                    sighting.setDurationMinutes(minutes);
                                    sighting.setDurationSeconds((double)seconds);
                                    app.getDBI().updateSighting(sighting);
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
                            List<Sighting> sightingList = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                            for (int t = 0; t < sightingList.size(); t++) {
                                Sighting sighting = sightingList.get(t);
                                if (sighting.getDurationMinutes() == 0 && sighting.getDurationSeconds() == 0.0) {
                                    // Using only images here since it is more reliable (and safer to automate) than movies
                                    List<WildLogFile> files = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class);
                                    if (!files.isEmpty()) {
                                        Collections.sort(files);
                                        Date startDate = UtilsImageProcessing.getDateFromImage(files.get(0).getAbsolutePath());
                                        Date endDate = UtilsImageProcessing.getDateFromImage(files.get(files.size()-1).getAbsolutePath());
                                        double difference = (endDate.getTime() - startDate.getTime())/1000;
                                        int minutes = (int)difference/60;
                                        double seconds = difference - minutes*60.0;
                                        sighting.setDurationMinutes(minutes);
                                        sighting.setDurationSeconds((double)seconds);
                                        app.getDBI().updateSighting(sighting);
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
        }
    }//GEN-LAST:event_mnuCalcDurationActionPerformed

    private void mnuExportWildNoteSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportWildNoteSyncActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Starting the Export of the WildNote Sync File " + getProgress() + "%");
                WildLogDBI syncDBI = null;
                try {
                    // Make sure the old files are deleted
                    Files.createDirectories(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath());
                    UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().toFile());
                    Path syncDatabase = WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().resolve(WildLogConstants.WILDNOTE_SYNC_DATABASE);
                    // Setup export DB
                    setTaskProgress(10);
                    setMessage("Busy with the Export of the WildNote Sync File " + getProgress() + "%");
                    syncDBI = new WildLogDBI_h2(syncDatabase.toAbsolutePath().toString(), false);
                    // Export the elements
                    List<Element> listElements = app.getDBI().listElements(null, null, null, Element.class);
                    setTaskProgress(20);
                    setMessage("Busy with the Export of the WildNote Sync File " + getProgress() + "%");
                    int counter = 0;
                    for (Element element : listElements) {
                        // Save the element to the new DB
                        syncDBI.createElement(element);
                        // Copy the files
                        WildLogFile wildLogFile = app.getDBI().findWildLogFile(null, element.getWildLogFileID(), null, WildLogFile.class);
                        if (wildLogFile != null) {
                            try {
                                // Android kan nie die zip handle as die folders of files snaakse name het met - of _ in nie...
                                wildLogFile.setFilename(UtilsFileProcessing.getAlphaNumericVersion(element.getPrimaryName() + ".jpg"));
                                Path targetFile = syncDatabase.getParent().resolve(Element.WILDLOG_FOLDER_PREFIX).resolve(wildLogFile.getFilename());
//                                UtilsFileProcessing.copyFile(wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SYNC_EXPORT),
//                                        targetFile, false, false);
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
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                }
                                // Create the WildLogFile entry in the DB
                                syncDBI.createWildLogFile(wildLogFile);
                            }
                            catch  (Exception ex) {
                                // Moenie stop as iets skeef geloop het met een van die files nie, doen steeds die res...
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            }
                        }
                        setProgress(20 + (int)(counter++/(double)listElements.size()*70));
                        setMessage("Busy with the Export of the WildNote Sync File " + getProgress() + "%");
                    }
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                finally {
                    if (syncDBI != null) {
                        syncDBI.close();
                    }
                }
                setProgress(90);
                setMessage("Busy with the Export of the WildNote Sync File " + getProgress() + "%");
                // Zip the content to make copying it accross easier
                UtilsCompression.zipFolder(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath().resolve("WildNoteSync.zip"),
                        WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath());
// TODO: Delete everthing except for the zip
//                    UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath()
//                            .resolve(WildLogPaths.WildLogPathPrefixes.PREFIX_ELEMENT.toPath()).toFile());
                setProgress(100);
                setMessage("Done with the Export of the WildNote Sync File");
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_WILDNOTE_SYNC.getAbsoluteFullPath());
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportWildNoteSyncActionPerformed

    private void mnuMergeLocationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeLocationsActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            MergeLocationDialog dialog = new MergeLocationDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeLocationsActionPerformed

    private void mnuMergeVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMergeVisitActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            MergeVisitDialog dialog = new MergeVisitDialog(app);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuMergeVisitActionPerformed

    private void mnuImportWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportWorkspaceActionPerformed
        tabbedPanel.setSelectedIndex(0);
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is <b><u>very strongly</u></b> recommended that you <b><u>backup your Workspace</u></b> (WildLog folder) before continuing. <br>"
                        + "Press OK when you are ready to start the import process.</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            WLFileChooser fileChooser = new WLFileChooser();
            fileChooser.setDialogTitle("Select the Workspace folder to import");
            fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileFilter(new WorkspaceFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setMultiSelectionEnabled(false);
            result = fileChooser.showOpenDialog(app.getMainFrame());
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
                    WLOptionPane.showConfirmDialog(app.getMainFrame(),
                            "The selected folder is not a valid existing WildLog Workspace.",
                            "Incorrect Workspace Selected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_mnuImportWorkspaceActionPerformed

    private void mnuExportWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportWorkspaceActionPerformed
        WorkspaceExportDialog dialog = new WorkspaceExportDialog(app, 
                WildLogPaths.WILDLOG_EXPORT_WORKSPACE.getAbsoluteFullPath()
                    .resolve(WildLogPaths.DEFAULT_WORKSPACE_NAME.getRelativePath()
                    .resolve(UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()))),
                null);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuExportWorkspaceActionPerformed

    private void mnuImportWildNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportWildNoteActionPerformed
        final int IMAGE_LINK_INTERVAL = 120000;
        WLFileChooser fileChooser = new WLFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Please select the WildNote Sync export file to use.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new WildNoteSyncFilter());
        int result = fileChooser.showOpenDialog(app.getMainFrame());
        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            tabbedPanel.setSelectedIndex(0);
            // Start the import
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setProgress(0);
                    setMessage("Starting the Import of the WildNote Sync File");
                    // Get linked images
                    int result = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                            "<html>WildLog can automatically try to "
                                    + "link the files (photos and movies) in a folder to the imported WildNote Observations "
                                    + "using the date and time."
                                    + "<br>Note: Since this is an automated process the results need to be manually verified when "
                                    + "there are multiple Observations and files being imported with similar dates and times."
                                    + "<br>Would you like to specify a folder to use?",
                            "Link Files?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    final Map<Long, List<File>> mapFilesToLink;
                    if (result == JOptionPane.YES_OPTION) {
                        setMessage("Busy with the Import of the WildNote Sync File (scanning folder)");
                        WLFileChooser folderChooser = new WLFileChooser();
                        folderChooser.setAcceptAllFileFilterUsed(false);
                        folderChooser.setMultiSelectionEnabled(false);
                        folderChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                        folderChooser.setDialogTitle("Please select the folder to use.");
                        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        result = folderChooser.showOpenDialog(app.getMainFrame());
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
                                setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
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
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        syncDBI = new WildLogDBI_h2(fileChooser.getSelectedFile().toPath().toAbsolutePath().getParent().resolve(
                                WildLogConstants.WILDNOTE_SYNC_DATABASE).toString(), false);
                        setTaskProgress(11);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        // Setup the Location
                        Location wildNoteLocation = app.getDBI().findLocation(WildLogConstants.WILDNOTE_LOCATION_NAME, Location.class);
                        if (wildNoteLocation == null) {
                            wildNoteLocation = new Location(WildLogConstants.WILDNOTE_LOCATION_NAME);
                            app.getDBI().createLocation(wildNoteLocation);
                        }
                        setTaskProgress(13);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        // Setup the Visit
                        Visit tempVisit = new Visit(WildLogConstants.WILDNOTE_VISIT_NAME + " - " + UtilsTime.WL_DATE_FORMATTER_FOR_VISIT_NAME.format(LocalDateTime.now()),
                                WildLogConstants.WILDNOTE_LOCATION_NAME);
                        while (app.getDBI().countVisits(tempVisit.getName(), null) > 0) {
                            tempVisit = new Visit(tempVisit.getName() + "_wl", tempVisit.getLocationName());
                        }
                        app.getDBI().createVisit(tempVisit);
                        setTaskProgress(15);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        // Import the Elements
                        List<Element> listElements = syncDBI.listElements(null, null, null, Element.class);
                        for (int t = 0; t < listElements.size(); t++) {
                            Element element = listElements.get(t);
                            if (app.getDBI().findElement(element.getPrimaryName(), Element.class) == null) {
                                app.getDBI().createElement(element);
                            }
                            setTaskProgress(15 + (int)(t/(double)listElements.size()*10));
                            setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        }
                        setTaskProgress(25);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        // Import the Sightings
                        List<Sighting> listSightings = syncDBI.listSightings(0, null, null, null, false, Sighting.class);
                        for (int t = 0; t < listSightings.size(); t++) {
                            Sighting sighting = listSightings.get(t);
                            sighting.setVisitName(tempVisit.getName());
                            sighting.setSightingCounter(0);
                            sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                            // Calculate the "auto" fields (sun, moon, etc.)
                            if (sighting.getCertainty() == null || Certainty.NONE.equals(Certainty.getEnumFromText(sighting.getCertainty().toString()))) {
                                sighting.setCertainty(Certainty.SURE);
                            }
                            UtilsTime.calculateSunAndMoon(sighting);
                            app.getDBI().createSighting(sighting, false);
                            // Check if there are any images to link
// TODO: Ek kan ook in die toekoms die "HasFoto" checkbox op WildNote gebruik om die linking meer akkuraat te maak...
                            if (mapFilesToLink != null && mapFilesToLink.get(sighting.getDate().getTime()/IMAGE_LINK_INTERVAL) != null) {
                                List<File> lstFiles = mapFilesToLink.get(sighting.getDate().getTime()/IMAGE_LINK_INTERVAL);
                                UtilsFileProcessing.performFileUpload(sighting,
                                        Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(sighting.toPath()),
                                        lstFiles.toArray(new File[lstFiles.size()]),
                                        null, 
                                        app, false, null, true, true);
                            }
                            setTaskProgress(25 + (int)(t/(double)listSightings.size()*70));
                            setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        }
                        setTaskProgress(95);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                        UtilsPanelGenerator.openPanelAsTab(app, tempVisit.getName(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel, wildNoteLocation);
                        setTaskProgress(97);
                        setMessage("Busy with the Import of the WildNote Sync File " + getProgress() + "%");
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    finally {
                        if (syncDBI != null) {
                            syncDBI.close();
                        }
                    }
                    setProgress(100);
                    setMessage("Done with the Import of the WildNote Sync File");
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuImportWildNoteActionPerformed

    private void mnuBackupWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupWorkspaceActionPerformed
        WorkspaceExportDialog dialog = new WorkspaceExportDialog(app, 
                WildLogPaths.WILDLOG_EXPORT_WORKSPACE.getAbsoluteFullPath()
                    .resolve(WildLogPaths.WILDLOG_BACKUPS.getRelativePath()
                    .resolve(UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()))),
                null);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuBackupWorkspaceActionPerformed

    private void chkMnuUseIconTablesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseIconTablesItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseThumbnailTables(chkMnuUseIconTables.isSelected());
        app.setWildLogOptionsAndSave(options);
        tabbedPanel.setSelectedIndex(0);
    }//GEN-LAST:event_chkMnuUseIconTablesItemStateChanged

    private void chkMnuBrowseWithThumbnailsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuBrowseWithThumbnailsItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseThumnailBrowsing(chkMnuBrowseWithThumbnails.isSelected());
        app.setWildLogOptionsAndSave(options);
        tabbedPanel.setSelectedIndex(0);
    }//GEN-LAST:event_chkMnuBrowseWithThumbnailsItemStateChanged

    private void chkMnuEnableSoundsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuEnableSoundsItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setEnableSounds(chkMnuEnableSounds.isSelected());
        app.setWildLogOptionsAndSave(options);
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
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
            // Shutdown
            WLOptionPane.showMessageDialog(app.getMainFrame(),
                    "The WildLog Workspace has been created. Please restart the application.",
                    "Workspace Created!", JOptionPane.INFORMATION_MESSAGE);
//            // Making the frame not visible (or calling dispose on it) hopefully prevents this error: java.lang.InterruptedException at java.lang.Object.wait(Native Method)
//            this.setVisible(false);
            app.quit(evt);
        }
    }//GEN-LAST:event_mnuCreateWorkspaceMenuItemActionPerformed

    private void btnImportIUCNListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportIUCNListActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>This will <u>replace</u> the names and threat status for the Creatures in this Workspace."
                        + "<br>Creatures are treated as <u>the same when their scientific name match</u>. "
                        + "<br>New Creatures may be added where matches weren't found."
                        + "<br>It is recommended to backup the Workspace's database before proceding."
                        + "<br><b>Warning: If you continue all open tabs will be closed automatically.</b></html>",
                "Import IUCN Species Names", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            final int choiceForReplacing = WLOptionPane.showOptionDialog(app.getMainFrame(),
                    "Please select what records should be modified:",
                    "Import IUCN Species Names", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[] {
                        "Only Add New Creatures", 
                        "Only Update Existing Creatures", 
                        "Add New and Update Existing Creatures"
                    }, null);
            if (choiceForReplacing != JOptionPane.CLOSED_OPTION) {
                final int choiceForName = WLOptionPane.showOptionDialog(app.getMainFrame(),
                        "Please select what name should be modified:",
                        "Import IUCN Species Names", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[] {
                            "Primary Names", 
                            "Other Names"
                        }, null);
                if (choiceForName != JOptionPane.CLOSED_OPTION) {
                    // Close all tabs and go to the home tab
                    tabbedPanel.setSelectedIndex(0);
                    while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                        tabbedPanel.remove(STATIC_TAB_COUNT);
                    }
                    UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                        @Override
                        protected Object doInBackground() throws Exception {
                            setMessage("Starting the IUCN Import");
                            WLFileChooser fileChooser = new WLFileChooser();
                            fileChooser.setDialogTitle("Select the CSV file to import from IUCN");
                            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            fileChooser.setFileFilter(new CsvFilter());
                            int result = fileChooser.showOpenDialog(app.getMainFrame());
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
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                    hasErrors = true;
                                }
                                if (hasErrors) {
                                    WLOptionPane.showMessageDialog(app.getMainFrame(),
                                            "Not all of the data could be successfully imported.",
                                            "Error Importing IUCN Species Names!", JOptionPane.ERROR_MESSAGE);
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

    private void chkMnuUseScienteficNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseScienteficNameItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseScientificNames(chkMnuUseScienteficName.isSelected());
        app.setWildLogOptionsAndSave(options);
        tabbedPanel.setSelectedIndex(0);
    }//GEN-LAST:event_chkMnuUseScienteficNameItemStateChanged

    private void mnuChangeWorkspaceNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuChangeWorkspaceNameActionPerformed
        WildLogOptions options = app.getWildLogOptions();
        String oldName = options.getWorkspaceName();
        String userInput = (String) WLOptionPane.showInputDialog(app.getMainFrame(),
                "Please specify the new Workspace name:",
                "Workspace Name", JOptionPane.QUESTION_MESSAGE,  
                null, null, options.getWorkspaceName());
        if (userInput != null && !userInput.trim().isEmpty()) {
            options.setWorkspaceName(userInput.trim());
            if (options.getWorkspaceName().length() > 50) {
                options.setWorkspaceName(userInput.substring(0, 47) + "...");
            }
        }
        app.setWildLogOptionsAndSave(options);
        // Refresh the UI
        if (tabbedPanel.getSelectedIndex() == 0) {
            tabHomeComponentShown(null);
        }
        app.getMainFrame().setTitle(app.getMainFrame().getTitle().replace(oldName, options.getWorkspaceName()));
    }//GEN-LAST:event_mnuChangeWorkspaceNameActionPerformed

    private void mnuSwitchElementNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSwitchElementNamesActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your WildLog Database before continuing. <br>Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tabbedPanel.setSelectedIndex(0);
            while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                tabbedPanel.remove(STATIC_TAB_COUNT);
            }
            // Display dialog to select which names should be switched
            int option = WLOptionPane.showOptionDialog(app.getMainFrame(), 
                    "<html>Select the name field that should be switched with the Primary Name field.</html>", 
                    "Which name do you want to use?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, new String[] {
                        "Other Name", 
                        "Scientific Name"
                    }, null);
            if (option != JOptionPane.CLOSED_OPTION) {
                List<Element> lstElements = app.getDBI().listElements(null, null, null, Element.class);
                for (Element element : lstElements) {
                    String oldName = element.getPrimaryName();
                    if (option == 0) {
                        if (element.getOtherName() != null && !element.getOtherName().isEmpty()) {
                            element.setPrimaryName(element.getOtherName());
                            element.setOtherName(oldName);
                        }
                    }
                    else
                    if (option == 1) {
                        if (element.getScientificName()!= null && !element.getScientificName().isEmpty()) {
                            element.setPrimaryName(element.getScientificName());
                            element.setScientificName(oldName);
                        }
                    }
                    app.getDBI().updateElement(element, oldName);
                }
            }
        }
    }//GEN-LAST:event_mnuSwitchElementNamesActionPerformed

    private void mnuExportXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportXMLActionPerformed
// TODO: Maak dalk eendag 'n popup wat vra of dit een file of baie moet wees. En ook of die Files ingesluit moet word of nie. Maar ek gaan nie juis self binnekort dit nodig he nie...
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Starting the XML Export for All Records");
                // Elements
                List<Element> listElements = app.getDBI().listElements(null, null, null, Element.class);
                for (int t = 0; t < listElements.size(); t++) {
                    UtilsXML.exportXML(listElements.get(t), app, null, false);
                    setProgress(0 + (int)((t/(double)listElements.size())*25));
                    setMessage("Busy with the XML Export for All Records " + getProgress() + "%");
                }
                // Locations
                setMessage("Busy with the XML Export for All Records " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().listLocations(null, Location.class);
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsXML.exportXML(listLocations.get(t), app, null, false);
                    setProgress(25 + (int)((t/(double)listLocations.size())*25));
                    setMessage("Busy with the HTML Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the XML Export for All Records " + getProgress() + "%");
                // Visits
                List<Visit> listVisits = app.getDBI().listVisits(null, null, null, Visit.class);
                for (int t = 0; t < listVisits.size(); t++) {
                    UtilsXML.exportXML(listVisits.get(t), app, null, false);
                    setProgress(50 + (int)((t/(double)listVisits.size())*25));
                    setMessage("Busy with the HTML Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the XML Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                for (int t = 0; t < listSightings.size(); t++) {
                    UtilsXML.exportXML(listSightings.get(t), app, null, false);
                    setProgress(75 + (int)((t/(double)listSightings.size())*25));
                    setMessage("Busy with the XML Export for All Records " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("Done with the XML Export for All Records");
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_XML.getAbsoluteFullPath());
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportXMLActionPerformed

    private void mnuCreateGIFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateGIFActionPerformed
        WLFileChooser fileChooser = new WLFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPG Images",
                WildLogFileExtentions.Images.JPG.getExtention().toLowerCase(), WildLogFileExtentions.Images.JPEG.getExtention().toLowerCase(),
                WildLogFileExtentions.Images.JPG.getExtention().toUpperCase(), WildLogFileExtentions.Images.JPG.getExtention().toUpperCase()));
        fileChooser.setDialogTitle("Select the JPG images to use for the Custom Animated GIF...");
        int result = fileChooser.showOpenDialog(app.getMainFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Creating the Custom Animated GIF");
                    List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
                    List<String> fileNames = new ArrayList<String>(files.size());
                    for (File tempFile : files) {
                        fileNames.add(tempFile.getAbsolutePath());
                    }
                    fileChooser.setDialogTitle("Please select where to save the Custom Animated GIF...");
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("animated_gif.gif"));
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Animated GIF", "gif"));
                    int result = fileChooser.showSaveDialog(app.getMainFrame());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        setProgress(1);
                        setMessage("Creating the Custom Animated GIF " + getProgress() + "%");
                        // Now create the GIF
                        if (!fileNames.isEmpty()) {
                            Path outputPath = fileChooser.getSelectedFile().toPath();
                            Files.createDirectories(outputPath.getParent());
                            ImageOutputStream output = null;
                            try {
                                output = new FileImageOutputStream(outputPath.toFile());
                                int thumbnailSize = app.getWildLogOptions().getDefaultSlideshowSize();
                                ImageIcon image = UtilsImageProcessing.getScaledIcon(WildLogSystemImages.MOVIES.getWildLogFile().getAbsolutePath(), thumbnailSize, false);
                                BufferedImage bufferedImage = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics2D = bufferedImage.createGraphics();
                                graphics2D.drawImage(image.getImage(), 
                                            (thumbnailSize - image.getIconWidth())/2, 
                                            (thumbnailSize - image.getIconHeight())/2, 
                                            image.getIconWidth(), 
                                            image.getIconHeight(), 
                                            Color.BLACK, null);
                                int timeBetweenFrames = (int) (1000.0 / ((double) app.getWildLogOptions().getDefaultSlideshowSpeed()));
                                AnimatedGIFWriter gifWriter = new AnimatedGIFWriter(output, bufferedImage.getType(), timeBetweenFrames, true);
                                gifWriter.writeToGIF(bufferedImage);
                                setProgress(2);
                                setMessage("Creating the Custom Animated GIF " + getProgress() + "%");
                                for (int t = 0; t < fileNames.size(); t++) {
                                    image = UtilsImageProcessing.getScaledIcon(Paths.get(fileNames.get(t)), thumbnailSize, true);
                                    bufferedImage = new BufferedImage(thumbnailSize, thumbnailSize, BufferedImage.TYPE_INT_RGB);
                                    graphics2D = bufferedImage.createGraphics();
                                    graphics2D.drawImage(image.getImage(), 
                                            (thumbnailSize - image.getIconWidth())/2, 
                                            (thumbnailSize - image.getIconHeight())/2, 
                                            image.getIconWidth(), 
                                            image.getIconHeight(), 
                                            Color.BLACK, null);
                                    gifWriter.writeToGIF(bufferedImage);
                                    setProgress(2 + (int)((((double)t)/((double)fileNames.size()))*97));
                                    setMessage("Creating the Custom Animated GIF " + getProgress() + "%");
                                }
                                gifWriter.finishGIF();
                            }
                            catch (IOException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            }
                            finally {
                                if (output != null) {
                                    try {
                                        output.flush();
                                    }
                                    catch (IOException ex) {
                                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                    }
                                    try {
                                        output.close();
                                    }
                                    catch (IOException ex) {
                                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                    }
                                }
                            }
                            UtilsFileProcessing.openFile(outputPath);
                        }
                    }
                    setProgress(100);
                    setMessage("Done with the Custom Animated GIF");
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuCreateGIFActionPerformed

    private void exportMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_exportMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[ExportMenu]");
    }//GEN-LAST:event_exportMenuMenuSelected

    private void backupMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_backupMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[BackupMenu]");
    }//GEN-LAST:event_backupMenuMenuSelected

    private void fileMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_fileMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[ApplicationMenu]");
    }//GEN-LAST:event_fileMenuMenuSelected

    private void importMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_importMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[ImportMenu]");
    }//GEN-LAST:event_importMenuMenuSelected

    private void advancedMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_advancedMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[AdvancedMenu]");
    }//GEN-LAST:event_advancedMenuMenuSelected

    private void extraMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_extraMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[ExtraMenu]");
    }//GEN-LAST:event_extraMenuMenuSelected

    private void settingsMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_settingsMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[SettingsMenu]");
    }//GEN-LAST:event_settingsMenuMenuSelected

    private void helpMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_helpMenuMenuSelected
        WildLogApp.LOGGER.log(Level.INFO, "[HelpMenu]");
    }//GEN-LAST:event_helpMenuMenuSelected

    private void mnuUserGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUserGuideActionPerformed
        Path userGuide = WildLogApp.getACTIVEWILDLOG_CODE_FOLDER().getParent().resolve("documentation").resolve("WildLog - User Guide.pdf");
        if (Files.exists(userGuide)) {
            UtilsFileProcessing.openFile(userGuide);
        }
        else {
            // Show message with download link
            JLabel label = new JLabel();
            Font font = label.getFont();
            String style = "font-family:" + font.getFamily() + ";font-weight:normal" + ";font-size:" + font.getSize() + "pt;";
            JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">"
                    + "To download the WildLog User Guide go to <a href=\"http://software.mywild.co.za/p/download-wildlog.html\">http://software.mywild.co.za/p/download-wildlog.html</a>"
                    + " or visit <a href=\"http://www.mywild.co.za\">http://www.mywild.co.za</a> for more information."
                    + "</body></html>");
            editorPane.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent inHyperlinkEvent) {
                        if (inHyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                            try {
                                Desktop.getDesktop().browse(inHyperlinkEvent.getURL().toURI());
                            }
                            catch (IOException | URISyntaxException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            }
                        }
                    }
                });
            editorPane.setEditable(false);
            editorPane.setBackground(label.getBackground());
            WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(), 
                    editorPane, 
                    "WildLog User Guide", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_mnuUserGuideActionPerformed

    private void mnuCheckUpdatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCheckUpdatesActionPerformed
        ExecutorService executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("WL_CheckForUpdates"));
        // Try to check the latest version
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String latestVersion = app.checkForUpdates();
                // The checkForUpdates() call will show a popup if the versions are out of sync
                if (WildLogApp.WILDLOG_VERSION.equalsIgnoreCase(latestVersion)) {
                    WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(), 
                        "You are using the latest official release of WildLog (v" + latestVersion + ").", 
                        "WildLog is up to date", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        executor.shutdown();
    }//GEN-LAST:event_mnuCheckUpdatesActionPerformed

    private void lblBlogMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBlogMousePressed
        try {
            Desktop.getDesktop().browse(URI.create(lblBlog.getText().trim()));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }//GEN-LAST:event_lblBlogMousePressed

    private void lblMyWildMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMyWildMousePressed
        try {
            Desktop.getDesktop().browse(URI.create(lblMyWild.getText().trim()));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }//GEN-LAST:event_lblMyWildMousePressed

    private void lblEmailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEmailMousePressed
        try {
            Desktop.getDesktop().mail(URI.create("mailto:" + lblEmail.getText().trim()));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }//GEN-LAST:event_lblEmailMousePressed

    private void mnuImportCSVBasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportCSVBasicActionPerformed
        tabbedPanel.setSelectedIndex(0);
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you first <b><u>backup your WildLog Database</u></b> before continuing. <br>"
                        + "Press OK when you are ready to start the import process.</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Starting the CSV Basic Import");
                    WLFileChooser fileChooser = new WLFileChooser();
                    fileChooser.setDialogTitle("Select the CSV file to import");
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setFileFilter(new CsvFilter());
                    int result = fileChooser.showOpenDialog(app.getMainFrame());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        tabbedPanel.setSelectedIndex(0);
                        Path path = fileChooser.getSelectedFile().toPath();
                        String prefix = WLOptionPane.showInputDialog(app.getMainFrame(),
                                "<html>Please provide a prefix to use for the imported data. "
                                        + "<br>The prefix will be used to map the imported data to new unique records. "
                                        + "<br>You can manually merge Creatures and move Periods afterwards.</html>",
                                "Import CSV Data", JOptionPane.QUESTION_MESSAGE);
                        if (prefix != null) {
                            boolean hasErrors = false;
                            try {
                                hasErrors = !app.getDBI().doImportBasicCSV(path, prefix);
                            }
                            catch (Exception ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                hasErrors = true;
                            }
                            if (hasErrors) {
                                WLOptionPane.showMessageDialog(app.getMainFrame(),
                                        "Not all of the data could be successfully imported.",
                                        "Error Importing From CSV!", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    setMessage("Done with the CSV Basic Import");
                    tabHomeComponentShown(null);
                    return null;
                }
            });
        }
    }//GEN-LAST:event_mnuImportCSVBasicActionPerformed

    private void mnuExportCSVBasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportCSVBasicActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Starting the CSV Basic Export");
                Path path = WildLogPaths.WILDLOG_EXPORT_CSV_BASIC.getAbsoluteFullPath();
                Files.createDirectories(path);
                setProgress(0);
                setMessage("Busy with the CSV Basic Export");
                // Elements
                List<Element> listElements = app.getDBI().listElements(null, null, null, Element.class);
                for (int t = 0; t < listElements.size(); t++) {
                    Element element = listElements.get(t);
                    Path tempPath = path.resolve(Element.WILDLOG_FOLDER_PREFIX).resolve(element.getDisplayName() + ".csv");
                    Files.createDirectories(tempPath.getParent());
                    app.getDBI().doExportBasicCSV(tempPath, null, null, element, null, null);
                    setProgress(0 + (int)((t/(double)listElements.size())*25));
                    setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                }
                setProgress(25);
                // Locations
                setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().listLocations(null, Location.class);
                for (int t = 0; t < listLocations.size(); t++) {
                    Location location = listLocations.get(t);
                    Path tempPath = path.resolve(Location.WILDLOG_FOLDER_PREFIX).resolve(location.getDisplayName() + ".csv");
                    Files.createDirectories(tempPath.getParent());
                    app.getDBI().doExportBasicCSV(tempPath, location, null, null, null, null);
                    setProgress(25 + (int)((t/(double)listLocations.size())*25));
                    setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                // Visits
                List<Visit> listVisits = app.getDBI().listVisits(null, null, null, Visit.class);
                for (int t = 0; t < listVisits.size(); t++) {
                    Visit visit = listVisits.get(t);
                    Path tempPath = path.resolve(Visit.WILDLOG_FOLDER_PREFIX).resolve(visit.getDisplayName() + ".csv");
                    Files.createDirectories(tempPath.getParent());
                    app.getDBI().doExportBasicCSV(tempPath, null, visit, null, null, null);
                    setProgress(50 + (int)((t/(double)listVisits.size())*25));
                    setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the CSV Basic Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                Path tempPath = path.resolve(Sighting.WILDLOG_FOLDER_PREFIX).resolve("AllObservations.csv");
                Files.createDirectories(tempPath.getParent());
                app.getDBI().doExportBasicCSV(tempPath, null, null, null, null, listSightings);
                setProgress(100);
                setMessage("Done with the CSV Basic Export");
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_CSV_BASIC.getAbsoluteFullPath());
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportCSVBasicActionPerformed

    private void mnuExportHTMLAdvancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportHTMLAdvancedActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the HTML Advanced Export for All Records");
                setProgress(0);
                // Elements
                List<Element> listElements = app.getDBI().listElements(null, null, null, Element.class);
                for (int t = 0; t < listElements.size(); t++) {
                    UtilsHTML.exportFancyHTML(listElements.get(t), app, null);
                    setProgress(0 + (int)((t/(double)listElements.size())*25));
                    setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                }
                setProgress(25);
                // Locations
                setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                List<Location> listLocations = app.getDBI().listLocations(null, Location.class);
                for (int t = 0; t < listLocations.size(); t++) {
                    UtilsHTML.exportFancyHTML(listLocations.get(t), app, null);
                    setProgress(25 + (int)((t/(double)listLocations.size())*25));
                    setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                // Visits
                List<Visit> listVisits = app.getDBI().listVisits(null, null, null, Visit.class);
                for (int t = 0; t < listVisits.size(); t++) {
                    UtilsHTML.exportFancyHTML(listVisits.get(t), app, null);
                    setProgress(50 + (int)((t/(double)listVisits.size())*25));
                    setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                }
                setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                // Sightings
                List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                for (int t = 0; t < listSightings.size(); t++) {
                    UtilsHTML.exportFancyHTML(listSightings.get(t), app, null);
                    setProgress(75 + (int)((t/(double)listSightings.size())*25));
                    setMessage("Busy with the HTML Advanced Export for All Records " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("Busy with the HTML Advanced Export for All Records " + getProgress());
                UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath());
                setMessage("Done with the HTML Advanced Export for All Records");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportHTMLAdvancedActionPerformed

    private void lblSwitchWorkspaceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSwitchWorkspaceMousePressed
        mnuChangeWorkspaceMenuItemActionPerformed(null);
    }//GEN-LAST:event_lblSwitchWorkspaceMousePressed

    private void chkMnuUploadLogsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUploadLogsItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUploadLogs(chkMnuUploadLogs.isSelected());
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_chkMnuUploadLogsItemStateChanged

    private void chkMnuUseBundledMediaViewersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuUseBundledMediaViewersItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setBundledPlayers(chkMnuUseBundledMediaViewers.isSelected());
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_chkMnuUseBundledMediaViewersItemStateChanged

    private void mnuReduceImagesSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuReduceImagesSizeActionPerformed
        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>Do you want to continue now?</html>",
                "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ImageResizeDialog dialog = new ImageResizeDialog(app.getMainFrame());
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuReduceImagesSizeActionPerformed

    private void mnuConvertCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConvertCoordinatesActionPerformed
        GPSGridConversionDialog dialog = new GPSGridConversionDialog();
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuConvertCoordinatesActionPerformed

    private void mnuBackupRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupRestoreActionPerformed
        int option = WLOptionPane.showOptionDialog(app.getMainFrame(), 
                "<html>Select the type of backup you want to restore form."
                + "<br/><b>Warning:</b>Restoring a database backup will overwrite recent changes.</html>", 
                "Restore Database", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                null, new String[] {
                    "H2 Database File (Recommended)", 
                    "SQL Script File"
                }, null);
        if (option != JOptionPane.CLOSED_OPTION) {
            WLFileChooser fileChooser = new WLFileChooser(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath().toFile());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (option == 0) {
                // Use H2
                fileChooser.setDialogTitle("Select the H2 Database file to restore");
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File inFile) {
                        if (inFile.isDirectory()) {
                            return true;
                        }
                        return inFile.getName().equalsIgnoreCase(WildLogDBI.BACKUP_H2);
                    }

                    @Override
                    public String getDescription() {
                        return "WildLog Backup H2 Database File";
                    }
                });
            }
            else {
                // Use SQL
                fileChooser.setDialogTitle("Select the SQL Script File to restore");
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File inFile) {
                        if (inFile.isDirectory()) {
                            return true;
                        }
                        return inFile.getName().equalsIgnoreCase(WildLogDBI.BACKUP_SQL);
                    }

                    @Override
                    public String getDescription() {
                        return "WildLog Backup SQL Script File";
                    }
                });
            }
            int result = fileChooser.showOpenDialog(app.getMainFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                // Close all tabs and go to the home tab
                tabbedPanel.setSelectedIndex(0);
                while (tabbedPanel.getTabCount() > STATIC_TAB_COUNT) {
                    tabbedPanel.remove(STATIC_TAB_COUNT);
                }
                // Lock the input/display and show busy message
                // Note: we never remove the Busy dialog and greyed out background since the app will be restarted anyway when done (Don't use JDialog since it stops the code until the dialog is closed...)
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JPanel panel = new JPanel(new AbsoluteLayout());
                        panel.setPreferredSize(new Dimension(400, 50));
                        panel.setBorder(new LineBorder(new Color(245, 80, 40), 3));
                        JLabel label = new JLabel("<html>Busy restoring the database....</html>");
                        label.setFont(new Font("Tahoma", Font.BOLD, 12));
                        label.setBorder(new LineBorder(new Color(195, 65, 20), 4));
                        panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.95f));
                        panel.add(label, new AbsoluteConstraints(410, 20, -1, -1));
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
                    }
                });
                // Start the process in another thread to allow the UI to update correctly and use the progressbar for feedback
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        app.getDBI().doRestore(fileChooser.getSelectedFile().toPath());
                        return null;
                    }

                    @Override
                    protected void finished() {
                        super.finished();
                        // Using invokeLater because I hope the progressbar will have finished by then, otherwise the popup is shown
                        // that asks whether you want to close the application or not, and it's best to rather restart afterwards.
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // Close the application to be safe (make sure no wierd references/paths are still used, etc.)
                                WLOptionPane.showMessageDialog(app.getMainFrame(),
                                        "The active database was restored to a previous state. Please restart the application.",
                                        "Backup Restored", JOptionPane.INFORMATION_MESSAGE);
                                app.quit(null);
                            }
                        });
                    }
                });
            }
        }
    }//GEN-LAST:event_mnuBackupRestoreActionPerformed

    private void chkMnuIncludeCountInSightingPathItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMnuIncludeCountInSightingPathItemStateChanged
        WildLogOptions options = app.getWildLogOptions();
        options.setUseIndividualsInSightingPath(chkMnuIncludeCountInSightingPath.isSelected());
        app.setWildLogOptionsAndSave(options);
    }//GEN-LAST:event_chkMnuIncludeCountInSightingPathItemStateChanged

    private void mnuExportExcelBasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportExcelBasicActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the Excel Export");
                setTaskProgress(1);
                setMessage("Busy with the Excel Export... " + getProgress() + "%");
                Path path;
                List<Sighting> lstSightingsToUse = app.getDBI().listSightings(0, null, null, null, false, Sighting.class);
                setTaskProgress(3);
                setMessage("Busy with the Excel Export... " + getProgress() + "%");
                path = WildLogPaths.WILDLOG_EXPORT_XLS_ALL.getAbsoluteFullPath().resolve("WildLog_Observations.xls");
                Files.createDirectories(path.getParent());
                // Create workbook and sheet
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("WildLog - All Observations");
                // Setup header row
                int rowCount = 0;
                UtilsExcel.exportSightingToExcelHeader(sheet, rowCount++);
                setTaskProgress(5);
                setMessage("Busy with the Excel Export... " + getProgress() + "%");
                // Add the Sightings
                int counter = 0;
                for (Sighting tempSighting : lstSightingsToUse) {
                    UtilsExcel.exportSightingToExcel(sheet, rowCount++, tempSighting, app);
                    // Update progress
                    setTaskProgress(5 + (int)((counter++/(double)lstSightingsToUse.size())*90));
                    setMessage("Busy with the Excel Export... " + getProgress() + "%");
                }
                // Write the last visit's register file (sightings)
                setTaskProgress(95);
                setMessage("Busy with the Excel Export (writing the file)... " + getProgress() + "%");
                try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                    workbook.write(out);
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                // Open the folder
                UtilsFileProcessing.openFile(path);
                setTaskProgress(100);
                setMessage("Done with the Excel Export");
                return null;
            }
        });
    }//GEN-LAST:event_mnuExportExcelBasicActionPerformed

    private void mnuINaturalistTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuINaturalistTokenActionPerformed
        INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuINaturalistTokenActionPerformed

    private void mnuImportINaturalistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportINaturalistActionPerformed
        tabbedPanel.setSelectedIndex(0);
        INatImportDialog dialog = new INatImportDialog(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuImportINaturalistActionPerformed

    private void mnuSystemMonitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSystemMonitorActionPerformed
        SystemMonitorDialog dialog = new SystemMonitorDialog();
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuSystemMonitorActionPerformed

    public void browseSelectedElement(Element inElement) {
        panelTabBrowse.browseSelectedElement(inElement);
    }

    public void browseSelectedLocation(Location inLocation) {
        panelTabBrowse.browseSelectedLocation(inLocation);
    }

    public void browseSelectedVisit(Visit inVisit) {
        panelTabBrowse.browseSelectedVisit(inVisit);
    }
    
    public void browseSelectedSighting(Sighting inSighting) {
        panelTabBrowse.browseSelectedSighting(inSighting);
    }

    public boolean closeAllTabs() {
        boolean closeStatus = true;
        while ((tabbedPanel.getTabCount() > STATIC_TAB_COUNT) && (closeStatus)) {
            tabbedPanel.setSelectedIndex(STATIC_TAB_COUNT);
            PanelCanSetupHeader tab = (PanelCanSetupHeader) tabbedPanel.getComponentAt(STATIC_TAB_COUNT);
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
    private javax.swing.JCheckBoxMenuItem chkMnuIncludeCountInSightingPath;
    private javax.swing.JCheckBoxMenuItem chkMnuUploadLogs;
    private javax.swing.JCheckBoxMenuItem chkMnuUseBundledMediaViewers;
    private javax.swing.JCheckBoxMenuItem chkMnuUseIconTables;
    private javax.swing.JCheckBoxMenuItem chkMnuUseScienteficName;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu externalMenu;
    private javax.swing.JMenu extraMenu;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator24;
    private javax.swing.JPopupMenu.Separator jSeparator25;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JLabel lblBlog;
    private javax.swing.JLabel lblCreatures;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLocations;
    private javax.swing.JLabel lblMyWild;
    private javax.swing.JLabel lblSettingsPath;
    private javax.swing.JLabel lblSightings;
    private javax.swing.JLabel lblSwitchWorkspace;
    private javax.swing.JLabel lblVisits;
    private javax.swing.JLabel lblWorkspaceName;
    private javax.swing.JLabel lblWorkspacePath;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mappingMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuAboutWildNote;
    private javax.swing.JMenuItem mnuBackupDatabase;
    private javax.swing.JMenuItem mnuBackupRestore;
    private javax.swing.JMenuItem mnuBackupWorkspace;
    private javax.swing.JMenuItem mnuBulkImport;
    private javax.swing.JMenuItem mnuCalcDuration;
    private javax.swing.JMenuItem mnuCalcSunMoon;
    private javax.swing.JMenuItem mnuChangeWorkspaceMenuItem;
    private javax.swing.JMenuItem mnuChangeWorkspaceName;
    private javax.swing.JMenuItem mnuCheckUpdates;
    private javax.swing.JMenuItem mnuCleanWorkspace;
    private javax.swing.JMenuItem mnuConvertCoordinates;
    private javax.swing.JMenuItem mnuCreateGIF;
    private javax.swing.JMenuItem mnuCreateSlideshow;
    private javax.swing.JMenuItem mnuCreateWorkspaceMenuItem;
    private javax.swing.JMenuItem mnuDBConsole;
    private javax.swing.JMenuItem mnuExifMenuItem;
    private javax.swing.JMenuItem mnuExportCSV;
    private javax.swing.JMenuItem mnuExportCSVBasic;
    private javax.swing.JMenuItem mnuExportExcelBasic;
    private javax.swing.JMenuItem mnuExportHTML;
    private javax.swing.JMenuItem mnuExportHTMLAdvanced;
    private javax.swing.JMenuItem mnuExportKML;
    private javax.swing.JMenuItem mnuExportWildNoteSync;
    private javax.swing.JMenuItem mnuExportWorkspace;
    private javax.swing.JMenuItem mnuExportXML;
    private javax.swing.JMenuItem mnuINaturalistToken;
    private javax.swing.JMenuItem mnuImportCSV;
    private javax.swing.JMenuItem mnuImportCSVBasic;
    private javax.swing.JMenuItem mnuImportINaturalist;
    private javax.swing.JMenuItem mnuImportWildNote;
    private javax.swing.JMenuItem mnuImportWorkspace;
    private javax.swing.JMenuItem mnuMapStartMenuItem;
    private javax.swing.JMenuItem mnuMergeElements;
    private javax.swing.JMenuItem mnuMergeLocations;
    private javax.swing.JMenuItem mnuMergeVisit;
    private javax.swing.JMenuItem mnuMoveVisits;
    private javax.swing.JMenu mnuOther;
    private javax.swing.JMenu mnuPerformance;
    private javax.swing.JMenuItem mnuReduceImagesSize;
    private javax.swing.JMenuItem mnuSetSlideshowSize;
    private javax.swing.JMenuItem mnuSetSlideshowSpeed;
    private javax.swing.JMenuItem mnuSunAndMoon;
    private javax.swing.JMenuItem mnuSwitchElementNames;
    private javax.swing.JMenuItem mnuSystemMonitor;
    private javax.swing.JMenuItem mnuUserGuide;
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
    private javax.swing.JPanel tabSightings;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JMenu workspaceMenu;
    // End of variables declaration//GEN-END:variables
}