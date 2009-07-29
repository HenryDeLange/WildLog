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

import CsvGenerator.CsvGenerator;
import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import KmlGenerator.objects.KmlStyle;
import java.awt.Color;
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
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.utils.UtilsHTML;
import wildlog.data.enums.ElementType;
import wildlog.ui.panel.PanelElement;
import wildlog.ui.panel.PanelLocation;
import wildlog.ui.panel.PanelVisit;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;

/**
 * The application's main frame.
 */
public class WildLogView extends FrameView {
    
    // This section contains all the custom initializations that needs to happen...
    private UtilPanelGenerator utilPanelGenerator;
    private UtilTableGenerator utilTableGenerator;
    private WildLogApp app;
    private Element searchElement;
    private Location searchLocation;
    
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

    public void setupTabHeaderFoto() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/FotoList.gif"))));
        tabHeader.add(new JLabel("Photos"));
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
        btnLocation = new javax.swing.JButton();
        btnAnimal = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnFotos = new javax.swing.JButton();
        btnFancyStuff = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tabFoto = new javax.swing.JPanel();
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
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        importMenu = new javax.swing.JMenu();
        wldImportMenuItem = new javax.swing.JMenuItem();
        csvImportMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        wldExportMenuItem = new javax.swing.JMenuItem();
        csvExportMenuItem = new javax.swing.JMenuItem();
        kmlExportMenuItem = new javax.swing.JMenuItem();
        htmlExportMenuItem1 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        backupMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

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

        btnLocation.setText(resourceMap.getString("btnLocation.text")); // NOI18N
        btnLocation.setName("btnLocation"); // NOI18N
        btnLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocationActionPerformed(evt);
            }
        });
        tabHome.add(btnLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 270, 40));

        btnAnimal.setText(resourceMap.getString("btnAnimal.text")); // NOI18N
        btnAnimal.setName("btnAnimal"); // NOI18N
        btnAnimal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnimalActionPerformed(evt);
            }
        });
        tabHome.add(btnAnimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 210, 270, 40));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setForeground(resourceMap.getColor("jLabel3.foreground")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        tabHome.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, -1, -1));

        btnFotos.setBackground(resourceMap.getColor("btnFotos.background")); // NOI18N
        btnFotos.setText(resourceMap.getString("btnFotos.text")); // NOI18N
        btnFotos.setName("btnFotos"); // NOI18N
        btnFotos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFotosActionPerformed(evt);
            }
        });
        tabHome.add(btnFotos, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 210, 270, 40));

        btnFancyStuff.setBackground(resourceMap.getColor("btnFancyStuff.background")); // NOI18N
        btnFancyStuff.setText(resourceMap.getString("btnFancyStuff.text")); // NOI18N
        btnFancyStuff.setName("btnFancyStuff"); // NOI18N
        tabHome.add(btnFancyStuff, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 550, 170, 30));

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
        tabHome.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 20, -1, -1));

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setForeground(resourceMap.getColor("jLabel16.foreground")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        tabHome.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 40, -1, -1));

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

        javax.swing.GroupLayout tabFotoLayout = new javax.swing.GroupLayout(tabFoto);
        tabFoto.setLayout(tabFotoLayout);
        tabFotoLayout.setHorizontalGroup(
            tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1017, Short.MAX_VALUE)
        );
        tabFotoLayout.setVerticalGroup(
            tabFotoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

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
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblVisit);

        tabLocation.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 300, 360, 250));

        btnGoLocation_LocTab.setIcon(resourceMap.getIcon("btnGoLocation_LocTab.icon")); // NOI18N
        btnGoLocation_LocTab.setText(resourceMap.getString("btnGoLocation_LocTab.text")); // NOI18N
        btnGoLocation_LocTab.setToolTipText(resourceMap.getString("btnGoLocation_LocTab.toolTipText")); // NOI18N
        btnGoLocation_LocTab.setName("btnGoLocation_LocTab"); // NOI18N
        btnGoLocation_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocation_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoLocation_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, 80));

        btnGoElement_LocTab.setIcon(resourceMap.getIcon("btnGoElement_LocTab.icon")); // NOI18N
        btnGoElement_LocTab.setText(resourceMap.getString("btnGoElement_LocTab.text")); // NOI18N
        btnGoElement_LocTab.setToolTipText(resourceMap.getString("btnGoElement_LocTab.toolTipText")); // NOI18N
        btnGoElement_LocTab.setName("btnGoElement_LocTab"); // NOI18N
        btnGoElement_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElement_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoElement_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 550, 300, 30));

        btnAddLocation.setIcon(resourceMap.getIcon("btnAddLocation.icon")); // NOI18N
        btnAddLocation.setText(resourceMap.getString("btnAddLocation.text")); // NOI18N
        btnAddLocation.setToolTipText(resourceMap.getString("btnAddLocation.toolTipText")); // NOI18N
        btnAddLocation.setName("btnAddLocation"); // NOI18N
        btnAddLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLocationActionPerformed(evt);
            }
        });
        tabLocation.add(btnAddLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 130, 30));

        btnDeleteLocation.setIcon(resourceMap.getIcon("btnDeleteLocation.icon")); // NOI18N
        btnDeleteLocation.setText(resourceMap.getString("btnDeleteLocation.text")); // NOI18N
        btnDeleteLocation.setToolTipText(resourceMap.getString("btnDeleteLocation.toolTipText")); // NOI18N
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
        tblElement_LocTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElement_LocTabKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tblElement_LocTab);

        tabLocation.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 300, 300, 250));

        btnGoVisit_LocTab.setIcon(resourceMap.getIcon("btnGoVisit_LocTab.icon")); // NOI18N
        btnGoVisit_LocTab.setText(resourceMap.getString("btnGoVisit_LocTab.text")); // NOI18N
        btnGoVisit_LocTab.setToolTipText(resourceMap.getString("btnGoVisit_LocTab.toolTipText")); // NOI18N
        btnGoVisit_LocTab.setName("btnGoVisit_LocTab"); // NOI18N
        btnGoVisit_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisit_LocTabActionPerformed(evt);
            }
        });
        tabLocation.add(btnGoVisit_LocTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 550, 360, 30));

        lblImage_LocTab.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage_LocTab.setText(resourceMap.getString("lblImage_LocTab.text")); // NOI18N
        lblImage_LocTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage_LocTab.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage_LocTab.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage_LocTab.setName("lblImage_LocTab"); // NOI18N
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
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, false));
        tblElement.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
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

        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        tabElement.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 130, 80));

        btnAddElement.setIcon(resourceMap.getIcon("btnAddElement.icon")); // NOI18N
        btnAddElement.setText(resourceMap.getString("btnAddElement.text")); // NOI18N
        btnAddElement.setToolTipText(resourceMap.getString("btnAddElement.toolTipText")); // NOI18N
        btnAddElement.setName("btnAddElement"); // NOI18N
        btnAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddElementActionPerformed(evt);
            }
        });
        tabElement.add(btnAddElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 130, 30));

        btnDeleteElement.setIcon(resourceMap.getIcon("btnDeleteElement.icon")); // NOI18N
        btnDeleteElement.setText(resourceMap.getString("btnDeleteElement.text")); // NOI18N
        btnDeleteElement.setToolTipText(resourceMap.getString("btnDeleteElement.toolTipText")); // NOI18N
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
        tblLocation_EleTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocation_EleTabKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(tblLocation_EleTab);

        tabElement.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 300, 330, 250));

        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setEnabled(false);
        cmbType.setName("cmbType"); // NOI18N
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeActionPerformed(evt);
            }
        });
        tabElement.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 330, 170, -1));

        btnGoLocation.setIcon(resourceMap.getIcon("btnGoLocation.icon")); // NOI18N
        btnGoLocation.setText(resourceMap.getString("btnGoLocation.text")); // NOI18N
        btnGoLocation.setToolTipText(resourceMap.getString("btnGoLocation.toolTipText")); // NOI18N
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });
        tabElement.add(btnGoLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 550, 330, 30));

        ckbTypeFilter.setBackground(resourceMap.getColor("ckbTypeFilter.background")); // NOI18N
        ckbTypeFilter.setText(resourceMap.getString("ckbTypeFilter.text")); // NOI18N
        ckbTypeFilter.setName("ckbTypeFilter"); // NOI18N
        ckbTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbTypeFilterActionPerformed(evt);
            }
        });
        tabElement.add(ckbTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 330, -1, -1));

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
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
        tabElement.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 360, 320, -1));

        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
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

        btnClearSearch.setText(resourceMap.getString("btnClearSearch.text")); // NOI18N
        btnClearSearch.setName("btnClearSearch"); // NOI18N
        btnClearSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSearchActionPerformed(evt);
            }
        });
        tabElement.add(btnClearSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 400, 150, 30));

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

        importMenu.setText(resourceMap.getString("importMenu.text")); // NOI18N
        importMenu.setName("importMenu"); // NOI18N

        wldImportMenuItem.setText(resourceMap.getString("wldImportMenuItem.text")); // NOI18N
        wldImportMenuItem.setName("wldImportMenuItem"); // NOI18N
        importMenu.add(wldImportMenuItem);

        csvImportMenuItem.setText(resourceMap.getString("csvImportMenuItem.text")); // NOI18N
        csvImportMenuItem.setName("csvImportMenuItem"); // NOI18N
        importMenu.add(csvImportMenuItem);

        fileMenu.add(importMenu);

        exportMenu.setText(resourceMap.getString("exportMenu.text")); // NOI18N
        exportMenu.setName("exportMenu"); // NOI18N

        wldExportMenuItem.setText(resourceMap.getString("wldExportMenuItem.text")); // NOI18N
        wldExportMenuItem.setName("wldExportMenuItem"); // NOI18N
        exportMenu.add(wldExportMenuItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getActionMap(WildLogView.class, this);
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

        fileMenu.add(exportMenu);

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

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

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
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

    private void btnAnimalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnimalActionPerformed
        tabbedPanel.setSelectedIndex(3);
    }//GEN-LAST:event_btnAnimalActionPerformed

    private void btnLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocationActionPerformed
        tabbedPanel.setSelectedIndex(2);
    }//GEN-LAST:event_btnLocationActionPerformed

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
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, false));
        tblLocation_EleTab.setModel(utilTableGenerator.getLocationsForElementTable(searchElement));
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        // Setup the table column sizes
        resizeTalbes_Element();
        // Sort rows for Element
        List tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(0, SortOrder.ASCENDING));
        tblElement.getRowSorter().setSortKeys(tempList);
}//GEN-LAST:event_tabElementComponentShown

    private void btnDeleteElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteElementActionPerformed
        if (tblElement.getSelectedRowCount() > 0) {
            final JDialog dialog = new JDialog(new JFrame(), "Delete", true);
            dialog.setLayout(new AbsoluteLayout());
            JLabel text = new JLabel("Are you sure you want to delete the Creature(s)?");
            dialog.add(text, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 350, -1));
            JButton buttonYes = new JButton("Yes");
            buttonYes.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] selectedRows = tblElement.getSelectedRows();
                    PanelElement tempPanel = null;
                    for (int t = 0; t < selectedRows.length; t++) {
                        tempPanel = utilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
                        tabbedPanel.remove(tempPanel);
                        app.getDBI().delete(new Element((String)tblElement.getValueAt(selectedRows[t], 0)));
                    }
                    tabElementComponentShown(null);
                    dialog.dispose();
                }
            });
            dialog.add(buttonYes, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 25, 100, -1));
            JButton buttonNo = new JButton("No");
            buttonNo.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    dialog.dispose();
                }
            });
            dialog.add(buttonNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 25, 100, -1));
            dialog.setSize(255, 84);
            dialog.setLocationRelativeTo(this.getComponent());
            dialog.setVisible(true);
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
        tblVisit.setModel(utilTableGenerator.getShortVisitTable(searchLocation));
        tblElement_LocTab.setModel(utilTableGenerator.getElementsForLocationTable(searchLocation));
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
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, true));
        cmbType.setEnabled(!cmbType.isEnabled());
        txtSearch.setText("");
        // Setup table column sizes
        resizeTalbes_Element();
    }//GEN-LAST:event_ckbTypeFilterActionPerformed

    private void cmbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTypeActionPerformed
        searchElement = new Element((ElementType)cmbType.getSelectedItem());
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, true));
        txtSearch.setText("");
        // Setup talbe column sizes
        resizeTalbes_Element();
    }//GEN-LAST:event_cmbTypeActionPerformed

    private void btnFotosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFotosActionPerformed
        tabbedPanel.setSelectedIndex(1);
}//GEN-LAST:event_btnFotosActionPerformed

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
            final JDialog dialog = new JDialog(new JFrame(), "Delete", true);
            dialog.setLayout(new AbsoluteLayout());
            JLabel text = new JLabel("Are you sure you want to delete the Location(s)?");
            dialog.add(text, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 350, -1));
            JButton buttonYes = new JButton("Yes");
            buttonYes.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] selectedRows = tblLocation.getSelectedRows();
                    for (int t = 0; t < selectedRows.length; t++) {
                        Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(selectedRows[t], 0)));
                        if (tempLocation.getVisits() != null) {
                            for (int i = 0; i < tempLocation.getVisits().size(); i++) {
                                PanelVisit tempPanel = utilPanelGenerator.getVisitPanel(tempLocation, tempLocation.getVisits().get(i).getName());
                                tabbedPanel.remove(tempPanel);
                            }
                        }
                        tabbedPanel.remove(utilPanelGenerator.getLocationPanel(tempLocation.getName()));
                        app.getDBI().delete(tempLocation);
                    }
                    tabLocationComponentShown(null);
                    dialog.dispose();
                }
            });
            dialog.add(buttonYes, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 25, 100, -1));
            JButton buttonNo = new JButton("No");
            buttonNo.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    dialog.dispose();
                }
            });
            dialog.add(buttonNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 25, 100, -1));
            dialog.setSize(255, 84);
            dialog.setLocationRelativeTo(this.getComponent());
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnDeleteLocationActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            // Get Image
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
            if (tempElement.getFotos().size() > 0)
                Utils.setupFoto(tempElement, 0, lblImage, 300);
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
            if (tempLocation.getFotos().size() > 0)
                Utils.setupFoto(tempLocation, 0, lblImage_LocTab, 300);
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
        tblVisit.getRowSorter().setSortKeys(tempList);
        tblElement_LocTab.getRowSorter().setSortKeys(tempList);
    }//GEN-LAST:event_tblLocationMouseReleased

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchElement = new Element();
        if (ckbTypeFilter.isSelected())
            searchElement.setType((ElementType)cmbType.getSelectedItem());
        if (txtSearch.getText() != null) {
            if (txtSearch.getText().length() > 0)
                searchElement.setPrimaryName(txtSearch.getText());
        }
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, true));
        // Setup talbe column sizes
        resizeTalbes_Element();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tabHomeComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabHomeComponentShown
        
    }//GEN-LAST:event_tabHomeComponentShown

    private void tabFotoComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tabFotoComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tabFotoComponentShown

    private void btnClearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSearchActionPerformed
        ckbTypeFilter.setSelected(false);
        txtSearch.setText("");
        cmbType.setEnabled(false);
        searchElement = new Element();
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement, false));
        // Setup talbe column sizes
        resizeTalbes_Element();
    }//GEN-LAST:event_btnClearSearchActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                Utils.openImage(tempElement, 0);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblImage_LocTabMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage_LocTabMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            Location tempLocation = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
            Utils.openImage(tempLocation, 0);
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
                column.setPreferredWidth(35);
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
            else if (i == 3) {
                column.setPreferredWidth(18);
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
        app.getDBI().doBackup();
        getApplication().exit();
    }

    @Action
    public void exportToHTML() {
        List<Element> listElements = app.getDBI().list(new Element());
        for (int t = 0; t < listElements.size(); t++) {
            UtilsHTML.exportHTML(listElements.get(t));
        }
        List<Location> listLocations = app.getDBI().list(new Location());
        for (int t = 0; t < listLocations.size(); t++) {
            UtilsHTML.exportHTML(listLocations.get(t));
        }
    }

    @Action
    public void exportToKML() {
        // First do the HTML export to generate the Images in the right place
        exportToHTML();

        // Then do KML export
        String path = File.separatorChar + "WildLog" + File.separatorChar + "KML";
        File tempFile = new File(path);
        tempFile.mkdirs();
        KmlGenerator kmlgen = new KmlGenerator();
        kmlgen.setKmlPath(path + File.separatorChar + "WildLogMarkers.kml");

        List<KmlStyle> styles = new ArrayList<KmlStyle>();
        KmlStyle style1 = new KmlStyle();
        style1.setName("locationStyle");
        style1.setIconName("locationIcon");
        style1.setIconPath("http://maps.google.com/mapfiles/kml/pal3/icon31.png");
        styles.add(style1);
        KmlStyle style2 = new KmlStyle();
        style2.setName("elementStyle");
        style2.setIconName("elementIcon");
        style2.setIconPath("http://maps.google.com/mapfiles/kml/pal5/icon6.png");
        styles.add(style2);

        List<KmlEntry> entries = new ArrayList<KmlEntry>();
        // Sightings
        List<Sighting> listSightings = app.getDBI().list(new Sighting());
        for (int t = 0; t < listSightings.size(); t++) {
            entries.add(listSightings.get(t).toKML(t));
        }
        // Locations
        List<Location> listLocations = app.getDBI().list(new Location());
        for (int t = 0; t < listLocations.size(); t++) {
            entries.add(listLocations.get(t).toKML(listSightings.size() + t));
        }

        kmlgen.generateFile(entries, styles);

        if (System.getProperty("os.name").equals("Windows XP")) {
            try {
                String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", path + File.separatorChar + "WildLogMarkers.kml"};
                Runtime.getRuntime().exec(commands);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Action
    public void exportToCSV() {
        String path = File.separatorChar + "WildLog" + File.separatorChar + "CSV";
        File tempFile = new File(path);
        tempFile.mkdirs();
        // Locations
        CsvGenerator csvGenerator = new CsvGenerator(path + File.separatorChar + "Locations.csv");
        csvGenerator.addHeader("Name");
        csvGenerator.addHeader("Description");
        csvGenerator.addHeader("Province");
        csvGenerator.addHeader("Rating");
        csvGenerator.addHeader("Wildlife Viewing Rating");
        csvGenerator.addHeader("Habitat Type");
        csvGenerator.addHeader("Photos");
        csvGenerator.addHeader("Accommodation Type");
        csvGenerator.addHeader("Catering");
        csvGenerator.addHeader("Contact Number");
        csvGenerator.addHeader("Website");
        csvGenerator.addHeader("Email");
        csvGenerator.addHeader("Directions");
        csvGenerator.addHeader("Latitude Indicator");
        csvGenerator.addHeader("Latitude Degrees");
        csvGenerator.addHeader("Latitude Minutes");
        csvGenerator.addHeader("Latitude Seconds");
        csvGenerator.addHeader("Longitude Indicator");
        csvGenerator.addHeader("Longitude Degrees");
        csvGenerator.addHeader("Longitude Minutes");
        csvGenerator.addHeader("Longitude Seconds");
        csvGenerator.addHeader("Sub Areas");
        List<Location> listLocations = app.getDBI().list(new Location());
        for (int t = 0; t < listLocations.size(); t++) {
            listLocations.get(t).toCSV(csvGenerator);
        }
        csvGenerator.writeCSV();
        // Visits
        csvGenerator = new CsvGenerator(path + File.separatorChar + "Visits.csv");
        csvGenerator.addHeader("Name");
        csvGenerator.addHeader("Start Date");
        csvGenerator.addHeader("End Date");
        csvGenerator.addHeader("Description");
        csvGenerator.addHeader("Game Watching Intensity");
        //csvGenerator.addHeader("Sightings");
        csvGenerator.addHeader("Type");
        csvGenerator.addHeader("Photos");
        List<Visit> listVisits = app.getDBI().list(new Visit());
        for (int t = 0; t < listVisits.size(); t++) {
            listVisits.get(t).toCSV(csvGenerator);
        }
        csvGenerator.writeCSV();
        // Sightings
        csvGenerator = new CsvGenerator(path + File.separatorChar + "Sightings.csv");
        csvGenerator.addHeader("Date");
        csvGenerator.addHeader("Element Primary Name");
        csvGenerator.addHeader("Location Name");
        csvGenerator.addHeader("Time of Day");
        csvGenerator.addHeader("Weather");
        csvGenerator.addHeader("Area Type");
        csvGenerator.addHeader("View Rating");
        csvGenerator.addHeader("Certainty");
        csvGenerator.addHeader("Number of Creatures");
        csvGenerator.addHeader("Details");
        csvGenerator.addHeader("Photos");
        csvGenerator.addHeader("Latitude Indicator");
        csvGenerator.addHeader("Latitude Degrees");
        csvGenerator.addHeader("Latitude Minutes");
        csvGenerator.addHeader("Latitude Seconds");
        csvGenerator.addHeader("Longitude Indicator");
        csvGenerator.addHeader("Longitude Degree");
        csvGenerator.addHeader("Longitude Minutes");
        csvGenerator.addHeader("Longitude Seconds");
        csvGenerator.addHeader("Sub Area");
        csvGenerator.addHeader("Sighting Evidence");
        //csvGenerator.addHeader("Sighting Counter");
        List<Sighting> listSightings = app.getDBI().list(new Sighting());
        for (int t = 0; t < listSightings.size(); t++) {
            listSightings.get(t).toCSV(csvGenerator);
        }
        csvGenerator.writeCSV();
        // Elements
        csvGenerator = new CsvGenerator(path + File.separatorChar + "Creatures.csv");
        csvGenerator.addHeader("Primary Name");
        csvGenerator.addHeader("Other Name");
        csvGenerator.addHeader("Scientific Name");
        csvGenerator.addHeader("Description");
        csvGenerator.addHeader("Nutrition");
        csvGenerator.addHeader("Water Dependance");
        csvGenerator.addHeader("Average Male Size");
        csvGenerator.addHeader("Average Female Size");
        csvGenerator.addHeader("Size Unit");
        csvGenerator.addHeader("Average Male Weight");
        csvGenerator.addHeader("Average Female Weight");
        csvGenerator.addHeader("Weight Unit");
        csvGenerator.addHeader("Breeding Duration");
        csvGenerator.addHeader("Breeding Number");
        csvGenerator.addHeader("Breeding Age");
        csvGenerator.addHeader("Wish List Rating");
        csvGenerator.addHeader("Diagnostic Description");
        csvGenerator.addHeader("Active Time");
        csvGenerator.addHeader("Endangered Status");
        csvGenerator.addHeader("Behaviour Description");
        csvGenerator.addHeader("Add Frequency");
        csvGenerator.addHeader("Photos");
        csvGenerator.addHeader("Type");
        csvGenerator.addHeader("Feeding Class");
        csvGenerator.addHeader("Lifespan");
        List<Element> listElements = app.getDBI().list(new Element());
        for (int t = 0; t < listElements.size(); t++) {
            listElements.get(t).toCSV(csvGenerator);
        }
        csvGenerator.writeCSV();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem backupMenuItem;
    private javax.swing.JButton btnAddElement;
    private javax.swing.JButton btnAddLocation;
    private javax.swing.JButton btnAnimal;
    private javax.swing.JButton btnClearSearch;
    private javax.swing.JButton btnDeleteElement;
    private javax.swing.JButton btnDeleteLocation;
    private javax.swing.JButton btnFancyStuff;
    private javax.swing.JButton btnFotos;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoElement_LocTab;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnGoLocation_LocTab;
    private javax.swing.JButton btnGoVisit_LocTab;
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox ckbTypeFilter;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JMenuItem csvExportMenuItem;
    private javax.swing.JMenuItem csvImportMenuItem;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenuItem htmlExportMenuItem1;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenuItem kmlExportMenuItem;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblImage_LocTab;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
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
    private javax.swing.JTextField txtSearch;
    private javax.swing.JMenuItem wldExportMenuItem;
    private javax.swing.JMenuItem wldImportMenuItem;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
