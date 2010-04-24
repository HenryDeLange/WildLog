/*
 * WildLogView.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import KmlGenerator.objects.KmlStyle;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.wrappers.SightingWrapper;
import wildlog.utils.UtilsHTML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.FotoType;
import wildlog.ui.panel.PanelElement;
import wildlog.ui.panel.PanelLocation;
import wildlog.ui.panel.PanelMergeElements;
import wildlog.ui.panel.PanelMoveVisit;
import wildlog.ui.panel.PanelSighting;
import wildlog.ui.panel.PanelVisit;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.report.ReportElement;
import wildlog.ui.report.ReportLocation;
import wildlog.ui.report.ReportSighting;
import wildlog.ui.report.ReportVisit;
import wildlog.utils.ui.DateCellRenderer;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.utils.ui.WildLogTreeCellRenderer;

/**
 * The application's main frame.
 */
public class WildLogView extends FrameView implements PanelNeedsRefreshWhenSightingAdded {
    
    // This section contains all the custom initializations that needs to happen...
    private UtilPanelGenerator utilPanelGenerator;
    private UtilTableGenerator utilTableGenerator;
    private WildLogApp app;
    private Element searchElement;
    private Location searchLocation;
    private int imageIndex = 0;
    
    private void init() {
        utilPanelGenerator = new UtilPanelGenerator();
        utilTableGenerator = new UtilTableGenerator();
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
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
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

        // Preventing the moving of table columns (this breaks the hardcoded place where the IDs are
        // expected for database lookup...
        tblElement.getTableHeader().setReorderingAllowed(false);
        tblElement_LocTab.getTableHeader().setReorderingAllowed(false);
        tblLocation.getTableHeader().setReorderingAllowed(false);
        tblLocation_EleTab.getTableHeader().setReorderingAllowed(false);
        tblVisit.getTableHeader().setReorderingAllowed(false);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = WildLogApp.getApplication().getMainFrame();
            aboutBox = new WildLogAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        WildLogApp.getApplication().show(aboutBox);
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
        tabFoto = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        rdbBrowseLocation = new javax.swing.JRadioButton();
        rdbBrowseElement = new javax.swing.JRadioButton();
        rdbBrowseDate = new javax.swing.JRadioButton();
        imgBrowsePhotos = new org.jdesktop.swingx.JXImageView();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtPhotoInformation = new javax.swing.JTextPane();
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
        ckbTypeFilter = new javax.swing.JCheckBox();
        lblImage = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnClearSearch = new javax.swing.JButton();
        lblSearchResults = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jSeparator3 = new javax.swing.JSeparator();
        backupMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        csvExportMenuItem = new javax.swing.JMenuItem();
        kmlExportMenuItem = new javax.swing.JMenuItem();
        htmlExportMenuItem1 = new javax.swing.JMenuItem();
        importMenu = new javax.swing.JMenu();
        csvImportMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        advancedMenu = new javax.swing.JMenu();
        moveVisitsMenuItem = new javax.swing.JMenuItem();
        linkElementsMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        mnuDBConsole = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonGroup1 = new javax.swing.ButtonGroup();

        mainPanel.setMaximumSize(new java.awt.Dimension(1000, 630));
        mainPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1000, 630));

        tabbedPanel.setMaximumSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabbedPanel.setName("tabbedPanel"); // NOI18N
        tabbedPanel.setPreferredSize(new java.awt.Dimension(1000, 630));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(WildLogView.class);
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
        tabHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        tabHome.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, -1));

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setForeground(resourceMap.getColor("jLabel11.foreground")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        tabHome.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel12.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        tabHome.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, -1, -1));

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setForeground(resourceMap.getColor("jLabel15.foreground")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        tabHome.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 10, -1, -1));

        jLabel3.setIcon(resourceMap.getIcon("jLabel3.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        tabHome.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 330, 160, -1));

        lblLocations.setForeground(resourceMap.getColor("lblLocations.foreground")); // NOI18N
        lblLocations.setText(resourceMap.getString("lblLocations.text")); // NOI18N
        lblLocations.setName("lblLocations"); // NOI18N
        tabHome.add(lblLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, -1, -1));

        lblVisits.setForeground(resourceMap.getColor("lblVisits.foreground")); // NOI18N
        lblVisits.setText(resourceMap.getString("lblVisits.text")); // NOI18N
        lblVisits.setName("lblVisits"); // NOI18N
        tabHome.add(lblVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 150, -1, -1));

        lblSightings.setForeground(resourceMap.getColor("lblSightings.foreground")); // NOI18N
        lblSightings.setText(resourceMap.getString("lblSightings.text")); // NOI18N
        lblSightings.setName("lblSightings"); // NOI18N
        tabHome.add(lblSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, -1, -1));

        lblCreatures.setForeground(resourceMap.getColor("lblCreatures.foreground")); // NOI18N
        lblCreatures.setText(resourceMap.getString("lblCreatures.text")); // NOI18N
        lblCreatures.setName("lblCreatures"); // NOI18N
        tabHome.add(lblCreatures, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 150, -1, -1));

        jSeparator5.setBackground(resourceMap.getColor("jSeparator5.background")); // NOI18N
        jSeparator5.setForeground(resourceMap.getColor("jSeparator5.foreground")); // NOI18N
        jSeparator5.setName("jSeparator5"); // NOI18N
        tabHome.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 340, 10));

        tabbedPanel.addTab(resourceMap.getString("tabHome.TabConstraints.tabTitle"), tabHome); // NOI18N

        tabFoto.setBackground(resourceMap.getColor("tabFoto.background")); // NOI18N
        tabFoto.setMaximumSize(new java.awt.Dimension(1000, 630));
        tabFoto.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabFoto.setName("tabFoto"); // NOI18N
        tabFoto.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabFoto.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabFotoComponentShown(evt);
            }
        });
        tabFoto.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        tabFoto.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        rdbBrowseLocation.setBackground(resourceMap.getColor("rdbBrowseLocation.background")); // NOI18N
        buttonGroup1.add(rdbBrowseLocation);
        rdbBrowseLocation.setText(resourceMap.getString("rdbBrowseLocation.text")); // NOI18N
        rdbBrowseLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseLocation.setName("rdbBrowseLocation"); // NOI18N
        rdbBrowseLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseLocationItemStateChanged(evt);
            }
        });
        tabFoto.add(rdbBrowseLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        rdbBrowseElement.setBackground(resourceMap.getColor("rdbBrowseElement.background")); // NOI18N
        buttonGroup1.add(rdbBrowseElement);
        rdbBrowseElement.setText(resourceMap.getString("rdbBrowseElement.text")); // NOI18N
        rdbBrowseElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseElement.setName("rdbBrowseElement"); // NOI18N
        rdbBrowseElement.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseElementItemStateChanged(evt);
            }
        });
        tabFoto.add(rdbBrowseElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, -1, -1));

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
        tabFoto.add(rdbBrowseDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, -1, -1));

        imgBrowsePhotos.setBackground(resourceMap.getColor("imgBrowsePhotos.background")); // NOI18N
        imgBrowsePhotos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        imgBrowsePhotos.setName("imgBrowsePhotos"); // NOI18N

        javax.swing.GroupLayout imgBrowsePhotosLayout = new javax.swing.GroupLayout(imgBrowsePhotos);
        imgBrowsePhotos.setLayout(imgBrowsePhotosLayout);
        imgBrowsePhotosLayout.setHorizontalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        imgBrowsePhotosLayout.setVerticalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );

        tabFoto.add(imgBrowsePhotos, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 80, 500, 500));

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtPhotoInformation.setContentType(resourceMap.getString("txtPhotoInformation.contentType")); // NOI18N
        txtPhotoInformation.setEditable(false);
        txtPhotoInformation.setText(resourceMap.getString("txtPhotoInformation.text")); // NOI18N
        txtPhotoInformation.setName("txtPhotoInformation"); // NOI18N
        jScrollPane5.setViewportView(txtPhotoInformation);

        tabFoto.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(268, 12, 240, 570));

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

        tabFoto.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 250, 460));

        btnGoBrowseSelection.setBackground(resourceMap.getColor("btnGoBrowseSelection.background")); // NOI18N
        btnGoBrowseSelection.setIcon(resourceMap.getIcon("btnGoBrowseSelection.icon")); // NOI18N
        btnGoBrowseSelection.setText(resourceMap.getString("btnGoBrowseSelection.text")); // NOI18N
        btnGoBrowseSelection.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoBrowseSelection.setName("btnGoBrowseSelection"); // NOI18N
        btnGoBrowseSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoBrowseSelectionActionPerformed(evt);
            }
        });
        tabFoto.add(btnGoBrowseSelection, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 553, 250, 30));

        btnZoomIn.setAction(imgBrowsePhotos.getZoomInAction());
        btnZoomIn.setBackground(resourceMap.getColor("btnZoomIn.background")); // NOI18N
        btnZoomIn.setIcon(resourceMap.getIcon("btnZoomIn.icon")); // NOI18N
        btnZoomIn.setText(resourceMap.getString("btnZoomIn.text")); // NOI18N
        btnZoomIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomIn.setName("btnZoomIn"); // NOI18N
        tabFoto.add(btnZoomIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 40, 80, 30));

        btnZoomOut.setAction(imgBrowsePhotos.getZoomOutAction());
        btnZoomOut.setBackground(resourceMap.getColor("btnZoomOut.background")); // NOI18N
        btnZoomOut.setIcon(resourceMap.getIcon("btnZoomOut.icon")); // NOI18N
        btnZoomOut.setText(resourceMap.getString("btnZoomOut.text")); // NOI18N
        btnZoomOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomOut.setName("btnZoomOut"); // NOI18N
        tabFoto.add(btnZoomOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 40, 80, 30));

        btnViewImage.setBackground(resourceMap.getColor("btnViewImage.background")); // NOI18N
        btnViewImage.setText(resourceMap.getString("btnViewImage.text")); // NOI18N
        btnViewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewImage.setName("btnViewImage"); // NOI18N
        btnViewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewImageActionPerformed(evt);
            }
        });
        tabFoto.add(btnViewImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 5, 170, 30));

        btnBrowsePrev.setBackground(resourceMap.getColor("btnBrowsePrev.background")); // NOI18N
        btnBrowsePrev.setIcon(resourceMap.getIcon("btnBrowsePrev.icon")); // NOI18N
        btnBrowsePrev.setText(resourceMap.getString("btnBrowsePrev.text")); // NOI18N
        btnBrowsePrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowsePrev.setName("btnBrowsePrev"); // NOI18N
        btnBrowsePrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowsePrevActionPerformed(evt);
            }
        });
        tabFoto.add(btnBrowsePrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 5, -1, 65));

        btnBrowseNext.setBackground(resourceMap.getColor("btnBrowseNext.background")); // NOI18N
        btnBrowseNext.setIcon(resourceMap.getIcon("btnBrowseNext.icon")); // NOI18N
        btnBrowseNext.setText(resourceMap.getString("btnBrowseNext.text")); // NOI18N
        btnBrowseNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowseNext.setName("btnBrowseNext"); // NOI18N
        btnBrowseNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseNextActionPerformed(evt);
            }
        });
        tabFoto.add(btnBrowseNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 5, -1, 65));

        dtpStartDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        tabFoto.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 100, -1));

        dtpEndDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        tabFoto.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 100, -1));

        btnRefreshDates.setBackground(resourceMap.getColor("btnRefreshDates.background")); // NOI18N
        btnRefreshDates.setIcon(resourceMap.getIcon("btnRefreshDates.icon")); // NOI18N
        btnRefreshDates.setText(resourceMap.getString("btnRefreshDates.text")); // NOI18N
        btnRefreshDates.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefreshDates.setName("btnRefreshDates"); // NOI18N
        btnRefreshDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDatesActionPerformed(evt);
            }
        });
        tabFoto.add(btnRefreshDates, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 55, 40, 30));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        tabFoto.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(578, 35, 80, 30));

        btnReport.setIcon(resourceMap.getIcon("btnReport.icon")); // NOI18N
        btnReport.setText(resourceMap.getString("btnReport.text")); // NOI18N
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        tabFoto.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 5, 100, 65));

        btnDefault.setIcon(resourceMap.getIcon("btnDefault.icon")); // NOI18N
        btnDefault.setText(resourceMap.getString("btnDefault.text")); // NOI18N
        btnDefault.setName("btnDefault"); // NOI18N
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });
        tabFoto.add(btnDefault, new org.netbeans.lib.awtextra.AbsoluteConstraints(573, 5, 90, -1));

        tabbedPanel.addTab(resourceMap.getString("tabFoto.TabConstraints.tabTitle"), tabFoto); // NOI18N

        tabLocation.setBackground(resourceMap.getColor("tabLocation.background")); // NOI18N
        tabLocation.setMaximumSize(new java.awt.Dimension(1000, 630));
        tabLocation.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabLocation.setName("tabLocation"); // NOI18N
        tabLocation.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabLocation.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabLocationComponentShown(evt);
            }
        });
        tabLocation.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setModel(utilTableGenerator.getCompleteLocationTable(searchLocation));
        tblLocation.setMaximumSize(new java.awt.Dimension(300, 300));
        tblLocation.setMinimumSize(new java.awt.Dimension(300, 300));
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

        tabLocation.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 860, 260));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        tabLocation.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 280, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        tabLocation.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 280, -1, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setModel(utilTableGenerator.getCompleteVisitTable(new Location()));
        tblVisit.setName("tblVisit"); // NOI18N
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

        tabLocation.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 300, 360, 250));

        btnGoLocation_LocTab.setBackground(resourceMap.getColor("btnGoLocation_LocTab.background")); // NOI18N
        btnGoLocation_LocTab.setIcon(resourceMap.getIcon("btnGoLocation_LocTab.icon")); // NOI18N
        btnGoLocation_LocTab.setText(resourceMap.getString("btnGoLocation_LocTab.text")); // NOI18N
        btnGoLocation_LocTab.setToolTipText(resourceMap.getString("btnGoLocation_LocTab.toolTipText")); // NOI18N
        btnGoLocation_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation_LocTab.setName("btnGoLocation_LocTab"); // NOI18N
        btnGoLocation_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocation_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoLocation_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, 80));

        btnGoElement_LocTab.setBackground(resourceMap.getColor("btnGoElement_LocTab.background")); // NOI18N
        btnGoElement_LocTab.setIcon(resourceMap.getIcon("btnGoElement_LocTab.icon")); // NOI18N
        btnGoElement_LocTab.setText(resourceMap.getString("btnGoElement_LocTab.text")); // NOI18N
        btnGoElement_LocTab.setToolTipText(resourceMap.getString("btnGoElement_LocTab.toolTipText")); // NOI18N
        btnGoElement_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement_LocTab.setName("btnGoElement_LocTab"); // NOI18N
        btnGoElement_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElement_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoElement_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 550, 300, 30));

        btnAddLocation.setBackground(resourceMap.getColor("btnAddLocation.background")); // NOI18N
        btnAddLocation.setIcon(resourceMap.getIcon("btnAddLocation.icon")); // NOI18N
        btnAddLocation.setText(resourceMap.getString("btnAddLocation.text")); // NOI18N
        btnAddLocation.setToolTipText(resourceMap.getString("btnAddLocation.toolTipText")); // NOI18N
        btnAddLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddLocation.setName("btnAddLocation"); // NOI18N
        btnAddLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLocationActionPerformed(evt);
            }
        });
        tabLocation.add(btnAddLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 130, 30));

        btnDeleteLocation.setBackground(resourceMap.getColor("btnDeleteLocation.background")); // NOI18N
        btnDeleteLocation.setIcon(resourceMap.getIcon("btnDeleteLocation.icon")); // NOI18N
        btnDeleteLocation.setText(resourceMap.getString("btnDeleteLocation.text")); // NOI18N
        btnDeleteLocation.setToolTipText(resourceMap.getString("btnDeleteLocation.toolTipText")); // NOI18N
        btnDeleteLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteLocation.setName("btnDeleteLocation"); // NOI18N
        btnDeleteLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLocationActionPerformed(evt);
            }
        });
        tabLocation.add(btnDeleteLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 130, 30));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblElement_LocTab.setAutoCreateRowSorter(true);
        tblElement_LocTab.setFont(resourceMap.getFont("tblElement_LocTab.font")); // NOI18N
        tblElement_LocTab.setModel(utilTableGenerator.getElementsForLocationTable(new Location()));
        tblElement_LocTab.setName("tblElement_LocTab"); // NOI18N
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

        tabLocation.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 300, 300, 250));

        btnGoVisit_LocTab.setBackground(resourceMap.getColor("btnGoVisit_LocTab.background")); // NOI18N
        btnGoVisit_LocTab.setIcon(resourceMap.getIcon("btnGoVisit_LocTab.icon")); // NOI18N
        btnGoVisit_LocTab.setText(resourceMap.getString("btnGoVisit_LocTab.text")); // NOI18N
        btnGoVisit_LocTab.setToolTipText(resourceMap.getString("btnGoVisit_LocTab.toolTipText")); // NOI18N
        btnGoVisit_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoVisit_LocTab.setName("btnGoVisit_LocTab"); // NOI18N
        btnGoVisit_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisit_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoVisit_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 360, 30));

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
        tabLocation.add(lblImage_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        tabLocation.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, -1));

        tabbedPanel.addTab(resourceMap.getString("tabLocation.TabConstraints.tabTitle"), tabLocation); // NOI18N

        tabElement.setBackground(resourceMap.getColor("tabElement.background")); // NOI18N
        tabElement.setMaximumSize(new java.awt.Dimension(1000, 630));
        tabElement.setMinimumSize(new java.awt.Dimension(1000, 630));
        tabElement.setName("tabElement"); // NOI18N
        tabElement.setPreferredSize(new java.awt.Dimension(1000, 630));
        tabElement.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tabElementComponentShown(evt);
            }
        });
        tabElement.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scrlElement.setName("scrlElement"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        tblElement.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblElement.setName("tblElement"); // NOI18N
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

        tabElement.add(scrlElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 860, 260));

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        tabElement.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 280, -1, -1));

        btnGoElement.setBackground(resourceMap.getColor("btnGoElement.background")); // NOI18N
        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        tabElement.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, 80));

        btnAddElement.setBackground(resourceMap.getColor("btnAddElement.background")); // NOI18N
        btnAddElement.setIcon(resourceMap.getIcon("btnAddElement.icon")); // NOI18N
        btnAddElement.setText(resourceMap.getString("btnAddElement.text")); // NOI18N
        btnAddElement.setToolTipText(resourceMap.getString("btnAddElement.toolTipText")); // NOI18N
        btnAddElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddElement.setName("btnAddElement"); // NOI18N
        btnAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddElementActionPerformed(evt);
            }
        });
        tabElement.add(btnAddElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 130, 30));

        btnDeleteElement.setBackground(resourceMap.getColor("btnDeleteElement.background")); // NOI18N
        btnDeleteElement.setIcon(resourceMap.getIcon("btnDeleteElement.icon")); // NOI18N
        btnDeleteElement.setText(resourceMap.getString("btnDeleteElement.text")); // NOI18N
        btnDeleteElement.setToolTipText(resourceMap.getString("btnDeleteElement.toolTipText")); // NOI18N
        btnDeleteElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteElement.setName("btnDeleteElement"); // NOI18N
        btnDeleteElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteElementActionPerformed(evt);
            }
        });
        tabElement.add(btnDeleteElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 130, 30));

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tblLocation_EleTab.setAutoCreateRowSorter(true);
        tblLocation_EleTab.setFont(resourceMap.getFont("tblLocation_EleTab.font")); // NOI18N
        tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(new Element()));
        tblLocation_EleTab.setName("tblLocation_EleTab"); // NOI18N
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

        tabElement.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 300, 330, 250));

        cmbType.setMaximumRowCount(9);
        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbType.setEnabled(false);
        cmbType.setName("cmbType"); // NOI18N
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeActionPerformed(evt);
            }
        });
        tabElement.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 330, 150, -1));

        btnGoLocation.setBackground(resourceMap.getColor("btnGoLocation.background")); // NOI18N
        btnGoLocation.setIcon(resourceMap.getIcon("btnGoLocation.icon")); // NOI18N
        btnGoLocation.setText(resourceMap.getString("btnGoLocation.text")); // NOI18N
        btnGoLocation.setToolTipText(resourceMap.getString("btnGoLocation.toolTipText")); // NOI18N
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });
        tabElement.add(btnGoLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 550, 330, 30));

        ckbTypeFilter.setBackground(resourceMap.getColor("ckbTypeFilter.background")); // NOI18N
        ckbTypeFilter.setText(resourceMap.getString("ckbTypeFilter.text")); // NOI18N
        ckbTypeFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ckbTypeFilter.setName("ckbTypeFilter"); // NOI18N
        ckbTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbTypeFilterActionPerformed(evt);
            }
        });
        tabElement.add(ckbTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 330, -1, -1));

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
        tabElement.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        tabElement.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, -1));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        tabElement.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 360, 320, -1));

        btnSearch.setBackground(resourceMap.getColor("btnSearch.background")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        tabElement.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 400, 150, 30));

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        tabElement.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 310, 280, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        tabElement.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 280, 340, -1));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        tabElement.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 300, 350, 10));

        btnClearSearch.setBackground(resourceMap.getColor("btnClearSearch.background")); // NOI18N
        btnClearSearch.setIcon(resourceMap.getIcon("btnClearSearch.icon")); // NOI18N
        btnClearSearch.setText(resourceMap.getString("btnClearSearch.text")); // NOI18N
        btnClearSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearSearch.setName("btnClearSearch"); // NOI18N
        btnClearSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSearchActionPerformed(evt);
            }
        });
        tabElement.add(btnClearSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 400, 150, 30));

        lblSearchResults.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSearchResults.setText(resourceMap.getString("lblSearchResults.text")); // NOI18N
        lblSearchResults.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblSearchResults.border.lineColor"))); // NOI18N
        lblSearchResults.setName("lblSearchResults"); // NOI18N
        tabElement.add(lblSearchResults, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 330, 130, 20));

        tabbedPanel.addTab(resourceMap.getString("tabElement.TabConstraints.tabTitle"), tabElement); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(tabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(tabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getActionMap(WildLogView.class, this);
        backupMenuItem.setAction(actionMap.get("backup")); // NOI18N
        backupMenuItem.setText(resourceMap.getString("backupMenuItem.text")); // NOI18N
        backupMenuItem.setName("backupMenuItem"); // NOI18N
        fileMenu.add(backupMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        exportMenu.setText(resourceMap.getString("exportMenu.text")); // NOI18N
        exportMenu.setName("exportMenu"); // NOI18N

        csvExportMenuItem.setAction(actionMap.get("exportToCSV")); // NOI18N
        csvExportMenuItem.setText(resourceMap.getString("csvExportMenuItem.text")); // NOI18N
        csvExportMenuItem.setName("csvExportMenuItem"); // NOI18N
        exportMenu.add(csvExportMenuItem);

        kmlExportMenuItem.setAction(actionMap.get("exportToKML")); // NOI18N
        kmlExportMenuItem.setText(resourceMap.getString("kmlExportMenuItem.text")); // NOI18N
        kmlExportMenuItem.setName("kmlExportMenuItem"); // NOI18N
        exportMenu.add(kmlExportMenuItem);

        htmlExportMenuItem1.setAction(actionMap.get("exportToHTML")); // NOI18N
        htmlExportMenuItem1.setText(resourceMap.getString("htmlExportMenuItem1.text")); // NOI18N
        htmlExportMenuItem1.setName("htmlExportMenuItem1"); // NOI18N
        exportMenu.add(htmlExportMenuItem1);

        menuBar.add(exportMenu);

        importMenu.setText(resourceMap.getString("importMenu.text")); // NOI18N
        importMenu.setName("importMenu"); // NOI18N

        csvImportMenuItem.setAction(actionMap.get("importFromCSV")); // NOI18N
        csvImportMenuItem.setText(resourceMap.getString("csvImportMenuItem.text")); // NOI18N
        csvImportMenuItem.setName("csvImportMenuItem"); // NOI18N
        importMenu.add(csvImportMenuItem);

        jSeparator4.setName("jSeparator4"); // NOI18N
        importMenu.add(jSeparator4);

        menuBar.add(importMenu);

        advancedMenu.setText(resourceMap.getString("advancedMenu.text")); // NOI18N
        advancedMenu.setName("advancedMenu"); // NOI18N

        moveVisitsMenuItem.setAction(actionMap.get("advancedMoveVisits")); // NOI18N
        moveVisitsMenuItem.setText(resourceMap.getString("moveVisitsMenuItem.text")); // NOI18N
        moveVisitsMenuItem.setName("moveVisitsMenuItem"); // NOI18N
        advancedMenu.add(moveVisitsMenuItem);

        linkElementsMenuItem.setAction(actionMap.get("advancedLinkElements")); // NOI18N
        linkElementsMenuItem.setText(resourceMap.getString("linkElementsMenuItem.text")); // NOI18N
        linkElementsMenuItem.setName("linkElementsMenuItem"); // NOI18N
        advancedMenu.add(linkElementsMenuItem);

        menuBar.add(advancedMenu);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        mnuDBConsole.setAction(actionMap.get("openDBConsole")); // NOI18N
        mnuDBConsole.setText(resourceMap.getString("mnuDBConsole.text")); // NOI18N
        mnuDBConsole.setName("mnuDBConsole"); // NOI18N
        jMenu1.add(mnuDBConsole);

        jMenu2.add(jMenu1);

        menuBar.add(jMenu2);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 852, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

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
                tempPanel = utilPanelGenerator.getVisitPanel(tempLocation, (String)tblVisit.getValueAt(selectedRows[t], 0));
                tabbedPanel.add(tempPanel);
                tempPanel.setupTabHeader();
            }
            if (tempPanel != null) tabbedPanel.setSelectedComponent(tempPanel);
        }
}//GEN-LAST:event_btnGoVisit_LocTabActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        int[] selectedRows = tblElement.getSelectedRows();
        PanelElement tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
            tabbedPanel.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) tabbedPanel.setSelectedComponent(tempPanel);
}//GEN-LAST:event_btnGoElementActionPerformed

    private void btnAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddElementActionPerformed
        PanelElement tempPanel = utilPanelGenerator.getNewElementPanel();
        tabbedPanel.add(tempPanel);
        tempPanel.setupTabHeader();
        tabbedPanel.setSelectedComponent(tempPanel);
}//GEN-LAST:event_btnAddElementActionPerformed

    private void tabElementComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabElementComponentShown
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        tblLocation_EleTab.setModel(new DefaultTableModel(new String[]{"No Creature Selected"}, 0));
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        // Setup the table column sizes
        resizeTalbes_Element();
        // Sort rows for Element
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement.getRowSorter().setSortKeys(tempList);
        lblSearchResults.setText("Found " + tblElement.getModel().getRowCount() + " Creatures");
}//GEN-LAST:event_tabElementComponentShown

    private void btnDeleteElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteElementActionPerformed
        if (tblElement.getSelectedRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(this.getComponent(), "Are you sure you want to delete the Creature(s)?", "Delete Creature(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblElement.getSelectedRows();
                PanelElement tempPanel = null;
                for (int t = 0; t < selectedRows.length; t++) {
                    tempPanel = utilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
                    tabbedPanel.remove(tempPanel);
                    app.getDBI().delete(new Element((String)tblElement.getValueAt(selectedRows[t], 0)));
                }
                tabElementComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteElementActionPerformed

    private void btnAddLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLocationActionPerformed
        PanelLocation tempPanel = utilPanelGenerator.getNewLocationPanel();
        tabbedPanel.add(tempPanel);
        tempPanel.setupTabHeader();
        tabbedPanel.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnAddLocationActionPerformed

    private void tabLocationComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabLocationComponentShown
        tblLocation.setModel(utilTableGenerator.getCompleteLocationTable(searchLocation));
        tblVisit.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
        tblElement_LocTab.setModel(new DefaultTableModel(new String[]{"No Location Selected"}, 0));
        lblImage_LocTab.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        // Setup Column width for tables:
        resizeTables_Location();
        // Sort location rows
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblLocation.getRowSorter().setSortKeys(tempList);
    }//GEN-LAST:event_tabLocationComponentShown

    private void btnGoLocation_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocation_LocTabActionPerformed
        int[] selectedRows = tblLocation.getSelectedRows();
        PanelLocation tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getLocationPanel((String)tblLocation.getValueAt(selectedRows[t], 0));
            tabbedPanel.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) tabbedPanel.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoLocation_LocTabActionPerformed

    private void ckbTypeFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbTypeFilterActionPerformed
        searchElement = new Element();
        if (!cmbType.isEnabled())
            searchElement.setType((ElementType)cmbType.getSelectedItem());
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        cmbType.setEnabled(!cmbType.isEnabled());
        txtSearch.setText("");
        // Setup table column sizes
        resizeTalbes_Element();
        // Sort rows for Element
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement.getRowSorter().setSortKeys(tempList);
        tblLocation_EleTab.setModel(new DefaultTableModel());
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        lblSearchResults.setText("Found " + tblElement.getModel().getRowCount() + " Creatures");
    }//GEN-LAST:event_ckbTypeFilterActionPerformed

    private void cmbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTypeActionPerformed
        searchElement = new Element((ElementType)cmbType.getSelectedItem());
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        txtSearch.setText("");
        // Setup talbe column sizes
        resizeTalbes_Element();
        // Sort rows for Element
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement.getRowSorter().setSortKeys(tempList);
        tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(new Element()));
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        lblSearchResults.setText("Found " + tblElement.getModel().getRowCount() + " Creatures");
    }//GEN-LAST:event_cmbTypeActionPerformed

    private void btnGoElement_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElement_LocTabActionPerformed
        int[] selectedRows = tblElement_LocTab.getSelectedRows();
        PanelElement tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getElementPanel((String)tblElement_LocTab.getValueAt(selectedRows[t], 0));
            tabbedPanel.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) tabbedPanel.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoElement_LocTabActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        int[] selectedRows = tblLocation_EleTab.getSelectedRows();
        PanelLocation tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getLocationPanel((String)tblLocation_EleTab.getValueAt(selectedRows[t], 0));
            tabbedPanel.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) tabbedPanel.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void btnDeleteLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLocationActionPerformed
        if (tblLocation.getSelectedRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(this.getComponent(), "Are you sure you want to delete the Location(s)?", "Delete Location(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblLocation.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(selectedRows[t], 0)));
                    Visit tempVisit = new Visit();
                    tempVisit.setLocationName(tempLocation.getName());
                    List<Visit> visits = app.getDBI().list(tempVisit);
                    for (int i = 0; i < visits.size(); i++) {
                        PanelVisit tempPanel = utilPanelGenerator.getVisitPanel(tempLocation, visits.get(i).getName());
                        tabbedPanel.remove(tempPanel);
                    }
                    tabbedPanel.remove(utilPanelGenerator.getLocationPanel(tempLocation.getName()));
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
            List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + tempElement.getPrimaryName()));
            if (fotos.size() > 0)
                Utils.setupFoto("ELEMENT-" + tempElement.getPrimaryName(), 0, lblImage, 300, app);
            else
                lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            // Get Locations
            tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(tempElement));
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(new Element()));
        }
        // Setup table column sizes
        resizeTalbes_Element();
        // Sort rows for locations
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblLocation_EleTab.getRowSorter().setSortKeys(tempList);
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            // Get Image
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + tempLocation.getName()));
            if (fotos.size() > 0)
                Utils.setupFoto("LOCATION-" + tempLocation.getName(), 0, lblImage_LocTab, 300, app);
            else
                lblImage_LocTab.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            // Get Visits
            tblVisit.setModel(utilTableGenerator.getShortVisitTable(tempLocation));
            // Get All Elements seen
            tblElement_LocTab.setModel(utilTableGenerator.getElementsForLocationTable(tempLocation));
        }
        else {
            lblImage_LocTab.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            tblVisit.setModel(utilTableGenerator.getShortVisitTable(new Location()));
            tblElement_LocTab.setModel(utilTableGenerator.getElementsForLocationTable(new Location()));
        }
        resizeTables_Location();
        // Sort rows for visits and elements
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement_LocTab.getRowSorter().setSortKeys(tempList);
        tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(1, SortOrder.ASCENDING));
        tblVisit.getRowSorter().setSortKeys(tempList);
    }//GEN-LAST:event_tblLocationMouseReleased

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchElement = new Element();
        if (ckbTypeFilter.isSelected())
            searchElement.setType((ElementType)cmbType.getSelectedItem());
        if (txtSearch.getText() != null) {
            if (txtSearch.getText().length() > 0)
                searchElement.setPrimaryName(txtSearch.getText());
        }
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        // Setup talbe column sizes
        resizeTalbes_Element();
        // Sort rows for Element
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement.getRowSorter().setSortKeys(tempList);
        // Reset the Image and Location table
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(new Element()));
        lblSearchResults.setText("Found " + tblElement.getModel().getRowCount() + " Creatures");
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tabHomeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabHomeComponentShown
        lblLocations.setText("Locations: " + app.getDBI().list(new Location()).size());
        lblVisits.setText("Visits: " + app.getDBI().list(new Visit()).size());
        lblSightings.setText("Sightings: " + app.getDBI().list(new Sighting()).size());
        lblCreatures.setText("Creatures: " + app.getDBI().list(new Element()).size());
    }//GEN-LAST:event_tabHomeComponentShown

    private void tabFotoComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabFotoComponentShown
//        if (!buttonGroup1.isSelected(rdbBrowseLocation.getModel()) && !buttonGroup1.isSelected(rdbBrowseElement.getModel()) && !buttonGroup1.isSelected(rdbBrowseDate.getModel()))
//            rdbBrowseLocation.setSelected(true);
        
        dtpStartDate.setVisible(false);
        dtpEndDate.setVisible(false);
        btnRefreshDates.setVisible(false);
        btnReport.setVisible(false);

        rdbBrowseLocationItemStateChanged(null);
        rdbBrowseElementItemStateChanged(null);
        rdbBrowseDateItemStateChanged(null);

        if (!rdbBrowseElement.isSelected() && !rdbBrowseLocation.isSelected() && !rdbBrowseDate.isSelected()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please select a category to browse");
            treBrowsePhoto.setModel(new DefaultTreeModel(root));
        }
        treBrowsePhoto.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treBrowsePhoto.setCellRenderer(new WildLogTreeCellRenderer(app));

        txtPhotoInformation.setText("");
        lblNumberOfImages.setText("");
        try {
            imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
            if (imgBrowsePhotos.getImage() != null) {
                if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                    imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
                else
                    imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
            }
        }
        catch (IOException ex) {
            Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tabFotoComponentShown

    private void btnClearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSearchActionPerformed
        ckbTypeFilter.setSelected(false);
        txtSearch.setText("");
        cmbType.setEnabled(false);
        searchElement = new Element();
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        // Reset everything
        tabElementComponentShown(null);
    }//GEN-LAST:event_btnClearSearchActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                Utils.openFile("ELEMENT-" + tempElement.getPrimaryName(), 0, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblImage_LocTabMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage_LocTabMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            Utils.openFile("LOCATION-" + tempLocation.getName(), 0, app);
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
            txtPhotoInformation.setText("");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
            imageIndex = 0;
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtPhotoInformation.setText(tempLocation.toHTML(false, false, app));
                List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + tempLocation.getName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtPhotoInformation.setText(tempElement.toHTML(false, false, app));
                List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + tempElement.getPrimaryName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                txtPhotoInformation.setText(tempVisit.toHTML(false, false, app));
                List<Foto> fotos = app.getDBI().list(new Foto("VISIT-" + tempVisit.getName()));
                setupFile(fotos);
                btnReport.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                txtPhotoInformation.setText(tempSighting.toHTML(false, false, app));
                List<Foto> fotos = app.getDBI().list(new Foto("SIGHTING-" + tempSighting.getSightingCounter()));
                setupFile(fotos);
            }
            else {
                setupFile(null);
            }
            if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
                btnReport.setVisible(true);
            }
            // Maak paar display issues reg
            txtPhotoInformation.getCaret().setDot(0);
        }
    }//GEN-LAST:event_treBrowsePhotoValueChanged

    private void rdbBrowseLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseLocationItemStateChanged
        if (rdbBrowseLocation.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnReport.setVisible(false);
            txtPhotoInformation.setText("");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
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
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
            browseByLocation();
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
}//GEN-LAST:event_rdbBrowseLocationItemStateChanged

    private void rdbBrowseElementItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseElementItemStateChanged
        if (rdbBrowseElement.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnReport.setVisible(false);
            txtPhotoInformation.setText("");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
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
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
            browseByElement();
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_rdbBrowseElementItemStateChanged

    private void rdbBrowseDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseDateItemStateChanged
        if (rdbBrowseDate.isSelected()) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnReport.setVisible(false);
            txtPhotoInformation.setText("");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
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
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
            browseByDate();
            dtpStartDate.setVisible(true);
            dtpEndDate.setVisible(true);
            btnRefreshDates.setVisible(true);
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_rdbBrowseDateItemStateChanged

    private void btnGoBrowseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoBrowseSelectionActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelLocation tempPanel = utilPanelGenerator.getLocationPanel(tempLocation.getName());
                tabbedPanel.add(tempPanel);
                tempPanel.setupTabHeader();
                tabbedPanel.setSelectedComponent(tempPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelElement tempPanel = utilPanelGenerator.getElementPanel(tempElement.getPrimaryName());
                tabbedPanel.add(tempPanel);
                tempPanel.setupTabHeader();
                tabbedPanel.setSelectedComponent(tempPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                PanelVisit tempPanel = utilPanelGenerator.getVisitPanel(app.getDBI().find(new Location(tempVisit.getLocationName())), tempVisit.getName());
                tabbedPanel.add(tempPanel);
                tempPanel.setupTabHeader();
                tabbedPanel.setSelectedComponent(tempPanel);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                final JDialog dialog = new JDialog(app.getMainFrame(), "View an Existing Sighting", true);
                dialog.setLayout(new AbsoluteLayout());
                dialog.setSize(965, 625);
                dialog.add(new PanelSighting(tempSighting, app.getDBI().find(new Location(tempSighting.getLocationName())), app.getDBI().find(new Visit(tempSighting.getVisitName())), app.getDBI().find(new Element(tempSighting.getElementName())), this, false, false), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                dialog.setLocationRelativeTo(this.getComponent());
                ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
                dialog.setIconImage(icon.getImage());
                ActionListener escListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        tabFotoComponentShown(null);
                        dialog.dispose();
                    }
                };
                dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
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
                Utils.openFile("LOCATION-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element temp = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                Utils.openFile("ELEMENT-" + temp.getPrimaryName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit temp = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                Utils.openFile("VISIT-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper temp = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                Utils.openFile("SIGHTING-" + temp.getSighting().getSightingCounter(), imageIndex, app);
            }
        }
    }//GEN-LAST:event_btnViewImageActionPerformed

    private void btnBrowsePrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowsePrevActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + tempLocation.getName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + tempElement.getPrimaryName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("VISIT-" + tempVisit.getName()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<Foto> fotos = app.getDBI().list(new Foto("SIGHTING-" + tempSighting.getSightingCounter()));
                loadPrevFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowsePrevActionPerformed

    private void btnBrowseNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseNextActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + tempLocation.getName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + tempElement.getPrimaryName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<Foto> fotos = app.getDBI().list(new Foto("VISIT-" + tempVisit.getName()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<Foto> fotos = app.getDBI().list(new Foto("SIGHTING-" + tempSighting.getSightingCounter()));
                loadNextFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowseNextActionPerformed

    private void btnRefreshDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDatesActionPerformed
        rdbBrowseDateItemStateChanged(null);
    }//GEN-LAST:event_btnRefreshDatesActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSearchActionPerformed(null);
    }//GEN-LAST:event_txtSearchKeyPressed

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
                JFrame report = new ReportLocation(tempLocation, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                JFrame report = new ReportElement(tempElement, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                JFrame report = new ReportVisit(tempVisit, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
        }
        if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            JFrame report = new ReportSighting(dtpStartDate.getDate(), dtpEndDate.getDate(), app);
            report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
            report.setPreferredSize(new Dimension(550, 750));
            report.setLocationRelativeTo(null);
            report.setVisible(true);
        }
}//GEN-LAST:event_btnReportActionPerformed

    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location temp = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = Utils.setMainImage("LOCATION-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element temp = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = Utils.setMainImage("ELEMENT-" + temp.getPrimaryName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit temp = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = Utils.setMainImage("VISIT-" + temp.getName(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper temp = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = Utils.setMainImage("SIGHTING-" + temp.getSighting().getSightingCounter(), imageIndex, app);
            }
            imageIndex--;
            btnBrowseNextActionPerformed(evt);
        }
    }//GEN-LAST:event_btnDefaultActionPerformed

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
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Element> elements = new ArrayList<Element>(app.getDBI().list(new Element()));
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
                tempSightingNode.add(new DefaultMutableTreeNode(app.getDBI().find(new Location(tempSighting.getLocationName()))));

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

    private void resizeTables_Location() {
        TableColumn column = null;
        for (int i = 0; i < tblLocation.getColumnModel().getColumnCount(); i++) {
            column = tblLocation.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(200);
            }
            else if (i == 1) {
                column.setPreferredWidth(40);
            }
            else if (i == 2) {
                column.setPreferredWidth(22);
            }
            else if (i == 3) {
                column.setPreferredWidth(22);
            }
            else if (i == 4) {
                column.setPreferredWidth(100);
            }
            else if (i == 5) {
                column.setPreferredWidth(140);
            }
        }
        for (int i = 0; i < tblVisit.getColumnModel().getColumnCount(); i++) {
            column = tblVisit.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(110);
            }
            else if (i == 1) {
                column.setPreferredWidth(40);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
            else if (i == 3) {
                column.setPreferredWidth(13);
            }
        }
        for (int i = 0; i < tblElement_LocTab.getColumnModel().getColumnCount(); i++) {
            column = tblElement_LocTab.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            }
            else if (i == 1) {
                column.setPreferredWidth(50);
            }
            else if (i == 2) {
                column.setPreferredWidth(50);
            }
        }
    }

    private void resizeTalbes_Element() {
        TableColumn column = null;
        for (int i = 0; i < tblElement.getColumnModel().getColumnCount(); i++) {
            column = tblElement.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(200);
            }
            else if (i == 1) {
                column.setPreferredWidth(180);
            }
            else if (i == 2) {
                column.setPreferredWidth(50);
            }
            else if (i == 3) {
                column.setPreferredWidth(50);
            }
            else if (i == 4) {
                column.setPreferredWidth(150);
            }
            else if (i == 5) {
                column.setPreferredWidth(80);
            }
        }
        for (int i = 0; i < tblLocation_EleTab.getColumnModel().getColumnCount(); i++) {
            column = tblLocation_EleTab.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(110);
            }
            else if (i == 1) {
                column.setPreferredWidth(35);
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
        }
    }

    @Action
    public void backup() {
        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        app.getDBI().doBackup();
        JOptionPane.showMessageDialog(this.getComponent(), "The backup can be found in the 'WildLog\\Backup\\Backup (date)\\' folder.", "Backup Completed", JOptionPane.INFORMATION_MESSAGE);
        //getApplication().exit();
        this.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    @Action
    public void exportToHTML() {
        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        List<Element> listElements = app.getDBI().list(new Element());
        for (int t = 0; t < listElements.size(); t++) {
            UtilsHTML.exportHTML(listElements.get(t), app);
        }
        List<Location> listLocations = app.getDBI().list(new Location());
        for (int t = 0; t < listLocations.size(); t++) {
            UtilsHTML.exportHTML(listLocations.get(t), app);
        }
        this.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    @Action
    public void exportToKML() {
        // First do the HTML export to generate the Images in the right place
        exportToHTML();

        // Then do KML export
        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String path = File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML";
        File tempFile = new File(path);
        tempFile.mkdirs();
        // Make sure icons exist in the KML folder
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/AnimalCarnivore.gif"), new File(path + File.separatorChar + "AnimalCarnivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/AnimalHerbivore.gif"), new File(path + File.separatorChar + "AnimalHerbivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/AnimalOmnivore.gif"), new File(path + File.separatorChar + "AnimalOmnivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/AnimalOtherFile.gif"), new File(path + File.separatorChar + "AnimalOtherFile.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/BirdCarnivore.gif"), new File(path + File.separatorChar + "BirdCarnivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/BirdHerbivore.gif"), new File(path + File.separatorChar + "BirdHerbivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/BirdOmnivore.gif"), new File(path + File.separatorChar + "BirdOmnivore.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/BirdOtherFile.gif"), new File(path + File.separatorChar + "BirdOtherFile.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/Plant.gif"), new File(path + File.separatorChar + "Plant.gif"));
        Utils.copyFile(app.getClass().getResourceAsStream("resources/mapping/Location.gif"), new File(path + File.separatorChar + "Location.gif"));
        // KML Stuff
        KmlGenerator kmlgen = new KmlGenerator();
        kmlgen.setKmlPath(path + File.separatorChar + "WildLogMarkers.kml");

        List<KmlStyle> styles = new ArrayList<KmlStyle>();
        KmlStyle tempStyle = new KmlStyle();
        tempStyle.setName("locationStyle");
        tempStyle.setIconName("locationIcon");
        tempStyle.setIconPath("Location.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalCarnivoreStyle");
        tempStyle.setIconName("animalCarnivoreIcon");
        tempStyle.setIconPath("AnimalCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalHerbivoreStyle");
        tempStyle.setIconName("animalHerbivoreIcon");
        tempStyle.setIconPath("AnimalHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOmnivoreStyle");
        tempStyle.setIconName("animalOmnivoreIcon");
        tempStyle.setIconPath("AnimalOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOtherStyle");
        tempStyle.setIconName("animalOtherIcon");
        tempStyle.setIconPath("AnimalOtherFile.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdCarnivoreStyle");
        tempStyle.setIconName("birdCarnivoreIcon");
        tempStyle.setIconPath("BirdCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdHerbivoreStyle");
        tempStyle.setIconName("birdHerbivoreIcon");
        tempStyle.setIconPath("BirdHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOmnivoreStyle");
        tempStyle.setIconName("birdOmnivoreIcon");
        tempStyle.setIconPath("BirdOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOtherStyle");
        tempStyle.setIconName("birdOtherIcon");
        tempStyle.setIconPath("BirdOtherFile.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("plantStyle");
        tempStyle.setIconName("plantIcon");
        tempStyle.setIconPath("Plant.gif");
        styles.add(tempStyle);

        List<KmlEntry> entries = new ArrayList<KmlEntry>();
        // Sightings
        List<Sighting> listSightings = app.getDBI().list(new Sighting());
        for (int t = 0; t < listSightings.size(); t++) {
            entries.add(listSightings.get(t).toKML(t, app));
        }
        // Locations
        List<Location> listLocations = app.getDBI().list(new Location());
        for (int t = 0; t < listLocations.size(); t++) {
            entries.add(listLocations.get(t).toKML(listSightings.size() + t, app));
        }

        kmlgen.generateFile(entries, styles);

        Utils.openFile(path + File.separatorChar + "WildLogMarkers.kml");
        
        this.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    @Action
    public void exportToCSV() {
        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String path = File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "CSV";
        File tempFile = new File(path);
        tempFile.mkdirs();
        app.getDBI().doExportCSV(path + File.separatorChar);
        this.getComponent().setCursor(Cursor.getDefaultCursor());
    }

//    @Action
//    public void exportToWld() {
//        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        app.getDBI().exportWLD(true);
//        this.getComponent().setCursor(Cursor.getDefaultCursor());
//    }

//    @Action
//    public void importFromWLD() {
//        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        app.getDBI().importWLD();
//        tabbedPanel.setSelectedIndex(0);
//        this.getComponent().setCursor(Cursor.getDefaultCursor());
//    }
//
//    @Action
//    public void exportToWLDWithoutImages() {
//        this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        app.getDBI().exportWLD(false);
//        this.getComponent().setCursor(Cursor.getDefaultCursor());
//    }

    @Action
    public void advancedLinkElements() {
        tabbedPanel.setSelectedIndex(0);
        while (tabbedPanel.getTabCount() > 4) {
            tabbedPanel.remove(4);
        }
        final JDialog dialog = new JDialog(app.getMainFrame(), "Link Creatures", true);
        dialog.setLayout(new AbsoluteLayout());
        dialog.setSize(790, 540);
        dialog.add(new PanelMergeElements(), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        dialog.setLocationRelativeTo(tabbedPanel);
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Element.gif"));
        dialog.setIconImage(icon.getImage());
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };
        dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setVisible(true);
    }

    @Action
    public void advancedMoveVisits() {
        tabbedPanel.setSelectedIndex(0);
        while (tabbedPanel.getTabCount() > 4) {
            tabbedPanel.remove(4);
        }
        final JDialog dialog = new JDialog(app.getMainFrame(), "Move Visits", true);
        dialog.setLayout(new AbsoluteLayout());
        dialog.setSize(750, 560);
        dialog.add(new PanelMoveVisit(), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        dialog.setLocationRelativeTo(tabbedPanel);
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif"));
        dialog.setIconImage(icon.getImage());
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };
        dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setVisible(true);
    }

    @Action
    public void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the directory with CSV files");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this.getComponent()) == JFileChooser.APPROVE_OPTION) {
            this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String path = fileChooser.getSelectedFile().getPath() + File.separatorChar;
            String prefix = JOptionPane.showInputDialog(this.getComponent(), "Provide a prefix to use for the imported data.", "Import CSV Data", JOptionPane.PLAIN_MESSAGE);
            app.getDBI().doImportCSV(path, prefix);
            this.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }

    @Action
    public void openDBConsole() {
        Utils.openFile(System.getProperty("user.dir") + "/lib/h2-1.2.133.jar");
    }

    private void loadPrevFile(List<Foto> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex--;
            if (imageIndex < 0) imageIndex = inFotos.size() - 1;
            setupFile(inFotos);
        }
        else {
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
                lblNumberOfImages.setText("0 of 0");
                imgBrowsePhotos.setToolTipText("");
            }
            catch (IOException ex) {
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadNextFile(List<Foto> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex++;
            if (imageIndex >= inFotos.size()) imageIndex = 0;
            setupFile(inFotos);
        }
        else {
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
                lblNumberOfImages.setText("0 of 0");
                imgBrowsePhotos.setToolTipText("");
            }
            catch (IOException ex) {
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setupFile(List<Foto> inFotos) {
        if (inFotos != null) {
            if (inFotos.size() > 0) {
                try {
                    lblNumberOfImages.setText(imageIndex+1 + " of " + inFotos.size());
                    if (inFotos.get(imageIndex).getFotoType().equals(FotoType.IMAGE))
                        imgBrowsePhotos.setImage(new File(inFotos.get(imageIndex).getOriginalFotoLocation()));
                    else
                    if (inFotos.get(imageIndex).getFotoType().equals(FotoType.MOVIE))
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/Movie.gif"));
                    else
                    if (inFotos.get(imageIndex).getFotoType().equals(FotoType.OTHER))
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/OtherFile.gif"));
                }
                catch (IOException ex) {
                    Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    imgBrowsePhotos.setToolTipText(inFotos.get(imageIndex).getFilename());
                }
            }
            else {
                try {
                    imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
                    lblNumberOfImages.setText("0 of 0");
                }
                catch (IOException ex) {
                    Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    imgBrowsePhotos.setToolTipText("");
                }
            }
        }
        else {
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/images/NoImage.gif"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                imgBrowsePhotos.setToolTipText("");
            }
        }
        // Scale image
        if (imgBrowsePhotos.getImage() != null) {
            if (imgBrowsePhotos.getImage().getHeight(null) >= imgBrowsePhotos.getImage().getWidth(null))
                imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getHeight(null));
            else
                imgBrowsePhotos.setScale(500.0/imgBrowsePhotos.getImage().getWidth(null));
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu advancedMenu;
    private javax.swing.JMenuItem backupMenuItem;
    private javax.swing.JButton btnAddElement;
    private javax.swing.JButton btnAddLocation;
    private javax.swing.JButton btnBrowseNext;
    private javax.swing.JButton btnBrowsePrev;
    private javax.swing.JButton btnClearSearch;
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
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewImage;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox ckbTypeFilter;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JMenuItem csvExportMenuItem;
    private javax.swing.JMenuItem csvImportMenuItem;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JMenu exportMenu;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JMenuItem kmlExportMenuItem;
    private javax.swing.JLabel lblCreatures;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblImage_LocTab;
    private javax.swing.JLabel lblLocations;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblSearchResults;
    private javax.swing.JLabel lblSightings;
    private javax.swing.JLabel lblVisits;
    private javax.swing.JMenuItem linkElementsMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuDBConsole;
    private javax.swing.JMenuItem moveVisitsMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton rdbBrowseDate;
    private javax.swing.JRadioButton rdbBrowseElement;
    private javax.swing.JRadioButton rdbBrowseLocation;
    private javax.swing.JScrollPane scrlElement;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
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
    private javax.swing.JTextPane txtPhotoInformation;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

}
