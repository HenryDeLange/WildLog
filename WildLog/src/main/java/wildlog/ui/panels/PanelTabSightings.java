package wildlog.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.adhoc.FilterProperties;
import wildlog.data.dataobjects.interfaces.DataObjectBasicInfo;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.data.enums.system.WildLogUserTypes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.FilterDataListDialog;
import wildlog.ui.dialogs.FilterGPSDialog;
import wildlog.ui.dialogs.FilterPropertiesDialog;
import wildlog.ui.helpers.ScrollableWrappedFlowLayout;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.panels.helpers.SightingBox;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogApplicationTypes;


public class PanelTabSightings extends JPanel implements PanelNeedsRefreshWhenDataChanges {
    private final WildLogApp app;
    private final JTabbedPane tabbedPanel;
    private List<Sighting> lstOriginalData;
    private List<Long> lstFilteredElements;
    private List<Long> lstFilteredLocations;
    private List<Long> lstFilteredVisits;
    private FilterProperties filterProperties;
    private int imageIndex;
    private double northEast_Latitude;
    private double northEast_Longitude;
    private double southWest_Latitude;
    private double southWest_Longitude;
    private enum LayoutType {TABLE, GRID};
    private LayoutType activeLayout = LayoutType.TABLE;

    public PanelTabSightings(WildLogApp inApp, JTabbedPane inTabbedPanel) {
        app = inApp;
        tabbedPanel = inTabbedPanel;
        setupDefaultFilters();
        // Continue loading the components
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbGridSize);
        lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
        // Add key listeners to table to allow the selection of rows based on key events.
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblSightings);
        // Add listner to auto resize columns.
        UtilsTableGenerator.setupColumnResizingListener(tblSightings, 1);
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            btnBulkEditSighting.setEnabled(false);
            btnBulkEditSighting.setVisible(false);
            btnExport.setEnabled(false);
            btnExport.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                pnlFeatures.setEnabled(false);
                pnlFeatures.setVisible(false);
                btnDeleteSighting.setEnabled(false);
                btnDeleteSighting.setVisible(false);
            }
        }
        // Setup the grid sizes
        DefaultComboBoxModel<WildLogThumbnailSizes> model = new DefaultComboBoxModel<>();
        model.addElement(WildLogThumbnailSizes.S0100_SMALL);
        model.addElement(WildLogThumbnailSizes.S0200_MEDIUM);
        model.addElement(WildLogThumbnailSizes.S0300_NORMAL);
        cmbGridSize.setModel(model);
        cmbGridSize.setSelectedItem(SightingBox.DEFAULT_SIZE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel7 = new javax.swing.JLabel();
        pnlLayoutView = new javax.swing.JPanel();
        scrSightings = new javax.swing.JScrollPane();
        tblSightings = new javax.swing.JTable();
        btnGoSighting = new javax.swing.JButton();
        btnAddSighting = new javax.swing.JButton();
        btnDeleteSighting = new javax.swing.JButton();
        btnBulkEditSighting = new javax.swing.JButton();
        pnlViews = new javax.swing.JPanel();
        btnGoLocation = new javax.swing.JButton();
        btnGoVisit = new javax.swing.JButton();
        btnGoElement = new javax.swing.JButton();
        btnGoBrowse = new javax.swing.JButton();
        pnlFeatures = new javax.swing.JPanel();
        btnMap = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        pnlFilters = new javax.swing.JPanel();
        btnFilterLocation = new javax.swing.JButton();
        btnFilterVisit = new javax.swing.JButton();
        btnFilterElements = new javax.swing.JButton();
        btnFilterProperties = new javax.swing.JButton();
        btnFilterMap = new javax.swing.JButton();
        btnResetFilters = new javax.swing.JButton();
        lblFilterDetails = new javax.swing.JLabel();
        pnlGridOptions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbGridSize = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        rdbLayoutTable = new javax.swing.JRadioButton();
        rdbLayoutGridSightings = new javax.swing.JRadioButton();
        rdbLayoutGridFiles = new javax.swing.JRadioButton();
        pnlImage = new javax.swing.JPanel();
        btnNextFile = new javax.swing.JButton();
        btnPrevFile = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();

        setBackground(new java.awt.Color(235, 233, 221));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Observations List:");

        pnlLayoutView.setBackground(new java.awt.Color(235, 233, 221));
        pnlLayoutView.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(198, 192, 158)));
        pnlLayoutView.setLayout(new java.awt.BorderLayout());

        tblSightings.setAutoCreateRowSorter(true);
        tblSightings.setMaximumSize(new java.awt.Dimension(300, 300));
        tblSightings.setMinimumSize(new java.awt.Dimension(300, 300));
        tblSightings.setSelectionBackground(new java.awt.Color(125, 120, 93));
        tblSightings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSightingsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSightingsMouseReleased(evt);
            }
        });
        tblSightings.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSightingsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSightingsKeyReleased(evt);
            }
        });
        scrSightings.setViewportView(tblSightings);

        pnlLayoutView.add(scrSightings, java.awt.BorderLayout.CENTER);

        btnGoSighting.setBackground(new java.awt.Color(235, 233, 221));
        btnGoSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoSighting.setToolTipText("Open a tab for the selected Observation.");
        btnGoSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoSightingActionPerformed(evt);
            }
        });

        btnAddSighting.setBackground(new java.awt.Color(235, 233, 221));
        btnAddSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddSighting.setToolTipText("Open a popup box to add a new Observation.");
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });

        btnDeleteSighting.setBackground(new java.awt.Color(235, 233, 221));
        btnDeleteSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteSighting.setToolTipText("<html>Delete the selected Observation. <br/>This will delete all linked files as well.</html>");
        btnDeleteSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });

        btnBulkEditSighting.setBackground(new java.awt.Color(235, 233, 221));
        btnBulkEditSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting.gif"))); // NOI18N
        btnBulkEditSighting.setText("Bulk Edit");
        btnBulkEditSighting.setToolTipText("Open a popup box to edit all of the selected Observations at once.");
        btnBulkEditSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkEditSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkEditSightingActionPerformed(evt);
            }
        });

        pnlViews.setBackground(new java.awt.Color(235, 233, 221));
        pnlViews.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "View", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(51, 51, 51))); // NOI18N

        btnGoLocation.setBackground(new java.awt.Color(235, 233, 221));
        btnGoLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnGoLocation.setText("View  Place");
        btnGoLocation.setToolTipText("Open a tab for the selected Place.");
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGoLocation.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });

        btnGoVisit.setBackground(new java.awt.Color(235, 233, 221));
        btnGoVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnGoVisit.setText("View Period");
        btnGoVisit.setToolTipText("Open a tab for the selected Period.");
        btnGoVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGoVisit.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnGoVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisitActionPerformed(evt);
            }
        });

        btnGoElement.setBackground(new java.awt.Color(235, 233, 221));
        btnGoElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnGoElement.setText("View Creature");
        btnGoElement.setToolTipText("Open a tab for the selected Creature.");
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGoElement.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });

        btnGoBrowse.setBackground(new java.awt.Color(235, 233, 221));
        btnGoBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnGoBrowse.setText("Browse");
        btnGoBrowse.setToolTipText("Open a tab for the selected Period.");
        btnGoBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoBrowse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGoBrowse.setIconTextGap(6);
        btnGoBrowse.setMargin(new java.awt.Insets(2, 4, 2, 8));
        btnGoBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlViewsLayout = new javax.swing.GroupLayout(pnlViews);
        pnlViews.setLayout(pnlViewsLayout);
        pnlViewsLayout.setHorizontalGroup(
            pnlViewsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlViewsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGoElement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGoLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGoVisit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGoBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnlViewsLayout.setVerticalGroup(
            pnlViewsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewsLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnGoVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnGoBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        pnlFeatures.setBackground(new java.awt.Color(235, 233, 221));
        pnlFeatures.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Features", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(51, 51, 51))); // NOI18N

        btnMap.setBackground(new java.awt.Color(235, 233, 221));
        btnMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMap.setText("Maps");
        btnMap.setToolTipText("Show available maps for these Observations.");
        btnMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMap.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(235, 233, 221));
        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        btnExport.setText("Export");
        btnExport.setToolTipText("Show available exports for these Observations.");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnReport.setBackground(new java.awt.Color(235, 233, 221));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.png"))); // NOI18N
        btnReport.setText("Charts");
        btnReport.setToolTipText("Show available charts for these Observations.");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFeaturesLayout = new javax.swing.GroupLayout(pnlFeatures);
        pnlFeatures.setLayout(pnlFeaturesLayout);
        pnlFeaturesLayout.setHorizontalGroup(
            pnlFeaturesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFeaturesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlFeaturesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnExport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(btnMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        pnlFeaturesLayout.setVerticalGroup(
            pnlFeaturesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFeaturesLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pnlFilters.setBackground(new java.awt.Color(235, 233, 221));
        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Observations Filters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N

        btnFilterLocation.setBackground(new java.awt.Color(235, 233, 221));
        btnFilterLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnFilterLocation.setText("Filter by Place");
        btnFilterLocation.setToolTipText("Filter the Observations according to its Place.");
        btnFilterLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterLocation.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnFilterLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterLocationActionPerformed(evt);
            }
        });

        btnFilterVisit.setBackground(new java.awt.Color(235, 233, 221));
        btnFilterVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnFilterVisit.setText("Filter by Period");
        btnFilterVisit.setToolTipText("Filter the Observations according to its Period.");
        btnFilterVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterVisit.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnFilterVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterVisitActionPerformed(evt);
            }
        });

        btnFilterElements.setBackground(new java.awt.Color(235, 233, 221));
        btnFilterElements.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnFilterElements.setText("Filter by Creature");
        btnFilterElements.setToolTipText("Filter the Observations according to its Creature.");
        btnFilterElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterElements.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterElements.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnFilterElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterElementsActionPerformed(evt);
            }
        });

        btnFilterProperties.setBackground(new java.awt.Color(235, 233, 221));
        btnFilterProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/FilterSightings.png"))); // NOI18N
        btnFilterProperties.setText("Filter on Properties");
        btnFilterProperties.setToolTipText("Filter the Observations according to the value of its data fields.");
        btnFilterProperties.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterProperties.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterProperties.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnFilterProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterPropertiesActionPerformed(evt);
            }
        });

        btnFilterMap.setBackground(new java.awt.Color(235, 233, 221));
        btnFilterMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnFilterMap.setText("Filter by Map");
        btnFilterMap.setToolTipText("Filter the Observations according to an area on a map.");
        btnFilterMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterMap.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnFilterMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterMapActionPerformed(evt);
            }
        });

        btnResetFilters.setBackground(new java.awt.Color(235, 233, 221));
        btnResetFilters.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnResetFilters.setText("Reset all filters");
        btnResetFilters.setToolTipText("Remove all the active filters.");
        btnResetFilters.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetFilters.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResetFilters.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnResetFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetFiltersActionPerformed(evt);
            }
        });

        lblFilterDetails.setText("<html>Loading filters...</html>");
        lblFilterDetails.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnFilterLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFilterProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnFilterVisit, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                            .addComponent(btnFilterMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnResetFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFilterElements, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .addComponent(lblFilterDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFilterLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterElements, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFilterProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterMap, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(lblFilterDetails)
                .addGap(5, 5, 5))
        );

        pnlGridOptions.setBackground(new java.awt.Color(235, 233, 221));
        pnlGridOptions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("px");

        cmbGridSize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbGridSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGridSizeActionPerformed(evt);
            }
        });

        jLabel2.setText("Grid Size:");

        javax.swing.GroupLayout pnlGridOptionsLayout = new javax.swing.GroupLayout(pnlGridOptions);
        pnlGridOptions.setLayout(pnlGridOptionsLayout);
        pnlGridOptionsLayout.setHorizontalGroup(
            pnlGridOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGridOptionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(cmbGridSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGridOptionsLayout.setVerticalGroup(
            pnlGridOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGridOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlGridOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbGridSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5))
        );

        rdbLayoutTable.setBackground(new java.awt.Color(235, 233, 221));
        buttonGroup1.add(rdbLayoutTable);
        rdbLayoutTable.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rdbLayoutTable.setSelected(true);
        rdbLayoutTable.setText("Table View");
        rdbLayoutTable.setToolTipText("Show the results in as rows in a table.");
        rdbLayoutTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLayoutTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLayoutTableActionPerformed(evt);
            }
        });

        rdbLayoutGridSightings.setBackground(new java.awt.Color(235, 233, 221));
        buttonGroup1.add(rdbLayoutGridSightings);
        rdbLayoutGridSightings.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rdbLayoutGridSightings.setText("Grid View (Observations)");
        rdbLayoutGridSightings.setToolTipText("Show the results as images in a grid.");
        rdbLayoutGridSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLayoutGridSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLayoutGridSightingsActionPerformed(evt);
            }
        });

        rdbLayoutGridFiles.setBackground(new java.awt.Color(235, 233, 221));
        buttonGroup1.add(rdbLayoutGridFiles);
        rdbLayoutGridFiles.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rdbLayoutGridFiles.setText("Grid View (Files)");
        rdbLayoutGridFiles.setToolTipText("Show the results as images in a grid.");
        rdbLayoutGridFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLayoutGridFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLayoutGridFilesActionPerformed(evt);
            }
        });

        btnNextFile.setBackground(new java.awt.Color(235, 233, 221));
        btnNextFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextFile.setToolTipText("Load next file.");
        btnNextFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNextFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextFileActionPerformed(evt);
            }
        });

        btnPrevFile.setBackground(new java.awt.Color(235, 233, 221));
        btnPrevFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPrevFile.setToolTipText("Load previous file.");
        btnPrevFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrevFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrevFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevFileActionPerformed(evt);
            }
        });

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setOpaque(true);
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlImageLayout = new javax.swing.GroupLayout(pnlImage);
        pnlImage.setLayout(pnlImageLayout);
        pnlImageLayout.setHorizontalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnPrevFile)
                .addGap(0, 0, 0)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNextFile)
                .addGap(0, 0, 0))
        );
        pnlImageLayout.setVerticalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPrevFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNextFile, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlViews, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFeatures, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteSighting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddSighting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGoSighting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(btnBulkEditSighting, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(pnlGridOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rdbLayoutTable)
                                .addGap(15, 15, 15)
                                .addComponent(rdbLayoutGridSightings)
                                .addGap(15, 15, 15)
                                .addComponent(rdbLayoutGridFiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(pnlImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(pnlLayoutView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlLayoutView, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(pnlGridOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdbLayoutTable)
                                    .addComponent(rdbLayoutGridSightings)
                                    .addComponent(rdbLayoutGridFiles)))
                            .addComponent(pnlImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(10, 10, 10)
                        .addComponent(btnGoSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnBulkEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(pnlViews, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlFeatures, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblSightingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsMouseClicked

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRowCount() == 1 && tblSightings.getModel().getColumnCount() > 8) {
            long sightingID = (Long) tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 8);
            int fotoCount = app.getDBI().countWildLogFiles(0, sightingID);
            if (fotoCount > 0 ) {
                imageIndex = 0;
                UtilsImageProcessing.setupFoto(sightingID, imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            }
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
        }
    }//GEN-LAST:event_tblSightingsMouseReleased

    private void tblSightingsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            btnGoSightingActionPerformed(null);
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnDeleteSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsKeyPressed

    private void tblSightingsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            imageIndex = 0;
            tblSightingsMouseReleased(null);
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            btnNextFileActionPerformed(null);
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnPrevFileActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsKeyReleased

    private void btnGoSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoSightingActionPerformed
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(activeLayout, true);
        if (lstSelectedSightings.size() == 1) {
            Sighting sighting = app.getDBI().findSighting(lstSelectedSightings.get(0).getID(), true, Sighting.class);
            Element element = app.getDBI().findElement(lstSelectedSightings.get(0).getElementID(), null, false, Element.class);
            Location location = app.getDBI().findLocation(lstSelectedSightings.get(0).getLocationID(), null, false, Location.class);
            Visit visit = app.getDBI().findVisit(lstSelectedSightings.get(0).getVisitID(), null, false, Visit.class);
            PanelSighting dialog = new PanelSighting(app, app.getMainFrame(), "Edit an Existing Observation",
                    sighting, location, visit, element, this, false, false, false, false);
            dialog.setVisible(true);
        }
        else {
            WLOptionPane.showMessageDialog(app.getMainFrame(),
                    "Only one Observation can be viewed at a time. Please select one row in the table and try again.",
                    "Select One Observation", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnGoSightingActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(activeLayout, true);
        if (lstSelectedSightings.size() >= 1) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Sighting sighting : lstSelectedSightings) {
                UtilsPanelGenerator.openPanelAsTab(app, sighting.getElementID(), PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        PanelSighting dialog = new PanelSighting(
                app, app.getMainFrame(), "Add a New Observation",
                new Sighting(), null, null, null, this, true, false, false, false);
        dialog.setVisible(true);
        doTheRefresh(this);
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(activeLayout, true);
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            if (WildLogApp.WILDLOG_USER_TYPE != WildLogUserTypes.VOLUNTEER) {
                if (lstSelectedSightings.size() > 1) {
                    WLOptionPane.showMessageDialog(app.getMainFrame(),
                        "Only one Observation can be deleted at a time. Please select one row in the table and try again.",
                        "Select One Observation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            else {
                return;
            }
        }
        if (lstSelectedSightings.size() > 0) {
           int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                   "Are you sure you want to delete the selected Observation(s)? This will delete all files linked to the Observation(s) as well.",
                   "Delete Observations(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                for (Sighting sighting : lstSelectedSightings)  {
                    app.getDBI().deleteSighting(sighting.getID());
                }
                doTheRefresh(this);
            }
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(activeLayout, true);
        if (lstSelectedSightings.size() >= 1) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Sighting sighting : lstSelectedSightings) {
                UtilsPanelGenerator.openPanelAsTab(app, sighting.getLocationID(), PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblSightings.getSelectedRowCount() == 1) {
            long tempSightingID = (Long) tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 8);
            UtilsFileProcessing.openFile(tempSightingID, imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

// FIXME: Daar is steeds 'n probleem as nuwe data objects ge-add/delete was, want dan is hulle nie in hierdie lyste nie...

        // Setup full lists for the first time if they were null
        if (lstFilteredLocations == null) {
            lstFilteredLocations = generateIDList(app.getDBI().listLocations(null, false, Location.class));
        }
        if (lstFilteredVisits == null) {
            lstFilteredVisits = generateIDList(app.getDBI().listVisits(null, 0, null, true, Visit.class));
        }
        if (lstFilteredElements == null) {
            lstFilteredElements = generateIDList(app.getDBI().listElements(null, null, null, false, Element.class));
        }
        // Load the view
        reloadUI(activeLayout);
    }//GEN-LAST:event_formComponentShown

    @Override
    public void doTheRefresh(Object inIndicator) {
        formComponentShown(null);
    }
    
    private void btnGoVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisitActionPerformed
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(activeLayout, true);
        if (lstSelectedSightings.size() >= 1) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Sighting sighting : lstSelectedSightings) {
                UtilsPanelGenerator.openPanelAsTab(app, sighting.getVisitID(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel, 
                        app.getDBI().findLocation(sighting.getLocationID(), null, false, Location.class));
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoVisitActionPerformed

    private void btnFilterPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterPropertiesActionPerformed
        showFilterDialog();
    }//GEN-LAST:event_btnFilterPropertiesActionPerformed

    public void showFilterDialog() {
        FilterPropertiesDialog<Sighting> dialog = new FilterPropertiesDialog<>(app.getMainFrame(), lstOriginalData, filterProperties);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            filterProperties = dialog.getSelectedFilterProperties();
            // Filter the original results using the provided values
            reloadUI(activeLayout);
        }
    }
    
    private void btnFilterElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterElementsActionPerformed
        FilterDataListDialog<Element> dialog = new FilterDataListDialog<Element>(app.getMainFrame(), 
                app.getDBI().listElements(null, null, null, false, Element.class), lstFilteredElements);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredElements = dialog.getSelectedData();
            // Filter the original results using the provided values
            reloadUI(activeLayout);
        }
    }//GEN-LAST:event_btnFilterElementsActionPerformed

    private void btnFilterLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterLocationActionPerformed
        FilterDataListDialog<Location> dialog = new FilterDataListDialog<Location>(app.getMainFrame(), 
                app.getDBI().listLocations(null, false, Location.class), lstFilteredLocations);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredLocations = dialog.getSelectedData();
            // Filter the original results using the provided values
            reloadUI(activeLayout);
        }
    }//GEN-LAST:event_btnFilterLocationActionPerformed

    private void btnFilterVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterVisitActionPerformed
        List<Visit> lstVisitsWithSelectedLocations = new ArrayList<>(lstFilteredLocations.size() * 5);
        for (long location : lstFilteredLocations) {
            lstVisitsWithSelectedLocations.addAll(app.getDBI().listVisits(null, location, null, true, Visit.class));
        }
        FilterDataListDialog<Visit> dialog = new FilterDataListDialog<Visit>(app.getMainFrame(), 
                lstVisitsWithSelectedLocations, lstFilteredVisits);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredVisits = dialog.getSelectedData();
            // Filter the original results using the provided values
            reloadUI(activeLayout);
        }
    }//GEN-LAST:event_btnFilterVisitActionPerformed

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        List<Sighting> lstSightingsToUse = getListOfSelectedSightings(activeLayout, false);
        if (!lstSightingsToUse.isEmpty()) {
            MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - Observations", lstSightingsToUse, 0);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        List<Sighting> lstSightingsToUse = getListOfSelectedSightings(activeLayout, false);
        if (!lstSightingsToUse.isEmpty()) {
            ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - Observations", lstSightingsToUse);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        List<Sighting> lstSightingsToUse = getListOfSelectedSightings(activeLayout, false);
        if (!lstSightingsToUse.isEmpty()) {
            ExportDialog dialog = new ExportDialog(app, null, null, null, null, lstSightingsToUse);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetFiltersActionPerformed
        setupDefaultFilters();
        reloadUI(activeLayout);
    }//GEN-LAST:event_btnResetFiltersActionPerformed

    private void btnGoBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoBrowseActionPerformed
        if (tblSightings.getSelectedRowCount() == 1) {
            app.getMainFrame().browseSelectedSighting(app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                    tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 8), true, Sighting.class));
        }
    }//GEN-LAST:event_btnGoBrowseActionPerformed

    private void btnPrevFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevFileActionPerformed
        if (tblSightings.getSelectedRowCount() == 1) {
            long sightingID = (Long) tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 8);
            imageIndex = UtilsImageProcessing.previousImage(sightingID, imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
        }
    }//GEN-LAST:event_btnPrevFileActionPerformed

    private void btnNextFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextFileActionPerformed
        if (tblSightings.getSelectedRowCount() == 1) {
            long sightingID = (Long) tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 8);
            imageIndex = UtilsImageProcessing.nextImage(sightingID, imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
        }
    }//GEN-LAST:event_btnNextFileActionPerformed

    private void btnFilterMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterMapActionPerformed
        FilterGPSDialog dialog = new FilterGPSDialog(app.getMainFrame());
        dialog.setVisible(true);
        northEast_Latitude = dialog.getNorthEast_Latitude();
        northEast_Longitude = dialog.getNorthEast_Longitude();
        southWest_Latitude = dialog.getSouthWest_Latitude();
        southWest_Longitude = dialog.getSouthWest_Longitude();
        reloadUI(activeLayout);
    }//GEN-LAST:event_btnFilterMapActionPerformed

    private void btnBulkEditSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkEditSightingActionPerformed
        List<Sighting> lstSightingsToUse = getListOfSelectedSightings(activeLayout, false);
        if (!lstSightingsToUse.isEmpty()) {
            Sighting bulkSighting = new Sighting();
            PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Bulk Edit Observations",
                    bulkSighting, null, null, null, this, false, false, false, true);
            dialog.setVisible(true);
            for (Sighting sighting : lstSightingsToUse) {
                if (bulkSighting.getDate() != null) {
                    sighting.setDate(bulkSighting.getDate());
                }
                if (bulkSighting.getTimeAccuracy() != null && !TimeAccuracy.NONE.equals(bulkSighting.getTimeAccuracy())) {
                    sighting.setTimeAccuracy(bulkSighting.getTimeAccuracy());
                }
                if (UtilsGPS.hasGPSData(bulkSighting)) {
                    UtilsGPS.copyGpsBetweenDOs(sighting, bulkSighting);
                }
                if (bulkSighting.getTimeOfDay() != null && !ActiveTimeSpesific.NONE.equals(bulkSighting.getTimeOfDay())) {
                    sighting.setTimeOfDay(bulkSighting.getTimeOfDay());
                }
                if (bulkSighting.getMoonPhase() >= 0) {
                    sighting.setMoonPhase(bulkSighting.getMoonPhase());
                }
                if (bulkSighting.getMoonlight() != null && !Moonlight.NONE.equals(bulkSighting.getMoonlight())) {
                    sighting.setMoonlight(bulkSighting.getMoonlight());
                }
                if (bulkSighting.getTemperature() >= 0) {
                    sighting.setTemperature(bulkSighting.getTemperature());
                }
                if (bulkSighting.getWeather() != null && !Weather.NONE.equals(bulkSighting.getWeather())) {
                    sighting.setWeather(bulkSighting.getWeather());
                }
                if (bulkSighting.getDurationMinutes() >= 0) {
                    sighting.setDurationMinutes(bulkSighting.getDurationMinutes());
                }
                if (bulkSighting.getDurationSeconds() >= 0) {
                    sighting.setDurationSeconds(bulkSighting.getDurationSeconds());
                }
                if (bulkSighting.getNumberOfElements() >= 0) {
                    sighting.setNumberOfElements(bulkSighting.getNumberOfElements());
                }
                if (bulkSighting.getSex() != null && !Sex.NONE.equals(bulkSighting.getSex())) {
                    sighting.setSex(bulkSighting.getSex());
                }
                if (bulkSighting.getAge() != null && !Age.NONE.equals(bulkSighting.getAge())) {
                    sighting.setAge(bulkSighting.getAge());
                }
                if (bulkSighting.getLifeStatus() != null && !LifeStatus.NONE.equals(bulkSighting.getLifeStatus())) {
                    sighting.setLifeStatus(bulkSighting.getLifeStatus());
                }
                if (bulkSighting.getSightingEvidence() != null && !SightingEvidence.NONE.equals(bulkSighting.getSightingEvidence())) {
                    sighting.setSightingEvidence(bulkSighting.getSightingEvidence());
                }
                if (bulkSighting.getCertainty() != null && !Certainty.NONE.equals(bulkSighting.getCertainty())) {
                    sighting.setCertainty(bulkSighting.getCertainty());
                }
                if (bulkSighting.getViewRating() != null && !ViewRating.NONE.equals(bulkSighting.getViewRating())) {
                    sighting.setViewRating(bulkSighting.getViewRating());
                }
                if (bulkSighting.getTag() != null && !bulkSighting.getTag().isEmpty()) {
                    sighting.setTag(bulkSighting.getTag());
                }
                if (bulkSighting.getDetails() != null && !bulkSighting.getDetails().isEmpty()) {
                    sighting.setDetails(bulkSighting.getDetails());
                }
                if (bulkSighting.getElementID() > 0) {
                    sighting.setElementID(bulkSighting.getElementID());
                }
                if (bulkSighting.getLocationID() > 0) {
                    sighting.setLocationID(bulkSighting.getLocationID());
                }
                if (bulkSighting.getVisitID() > 0) {
                    sighting.setVisitID(bulkSighting.getVisitID());
                }
                app.getDBI().updateSighting(sighting, false);
            }
            doTheRefresh(this);
        }
    }//GEN-LAST:event_btnBulkEditSightingActionPerformed

    private void rdbLayoutTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLayoutTableActionPerformed
        reloadUI(activeLayout);
        activeLayout = LayoutType.TABLE;
    }//GEN-LAST:event_rdbLayoutTableActionPerformed

    private void rdbLayoutGridSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLayoutGridSightingsActionPerformed
        reloadUI(activeLayout);
        activeLayout = LayoutType.GRID;
    }//GEN-LAST:event_rdbLayoutGridSightingsActionPerformed

    private void rdbLayoutGridFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLayoutGridFilesActionPerformed
        reloadUI(activeLayout);
        activeLayout = LayoutType.GRID;
    }//GEN-LAST:event_rdbLayoutGridFilesActionPerformed

    private void cmbGridSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGridSizeActionPerformed
        reloadUI(activeLayout);
    }//GEN-LAST:event_cmbGridSizeActionPerformed

    private List<Sighting> getListOfSelectedSightings(LayoutType inLayoutType, boolean inUseOnlySelectedSightings) {
        List<Sighting> lstSelectedSightings = new ArrayList<>(0);
        if (inLayoutType == LayoutType.TABLE) {
            if (tblSightings.getColumnCount() != 1) {
                int result = 1;
                if (!inUseOnlySelectedSightings) {
                result = WLOptionPane.showOptionDialog(app.getMainFrame(),
                        "Please select which subset of Observations should be used.",
                        "Which Observations to use?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                        null, new String[]{
                            "All active Observations (" + tblSightings.getRowCount() + ")", 
                            "Only selected Observations (" + tblSightings.getSelectedRowCount() + ")"}, 
                        null);
                }
                if (result != JOptionPane.CLOSED_OPTION) {
                    if (result == 0) {
                        // Use all Sightings
                        lstSelectedSightings = new ArrayList<>(tblSightings.getRowCount());
                        for (int row = 0; row < tblSightings.getModel().getRowCount(); row++) {
                            Sighting sighting = app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 8), true, Sighting.class);
                            sighting.setCachedVisitType((VisitType) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 4));
                            sighting.setCachedElementType((ElementType) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 5));
                            lstSelectedSightings.add(sighting);
                        }
                    }
                    else {
                        // Use selected Sightings
                        lstSelectedSightings = new ArrayList<>(tblSightings.getSelectedRowCount());
                        for (int row : tblSightings.getSelectedRows()) {
                            Sighting sighting = app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 8), true, Sighting.class);
                            sighting.setCachedVisitType((VisitType) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 4));
                            sighting.setCachedElementType((ElementType) tblSightings.getModel().getValueAt(
                                    tblSightings.convertRowIndexToModel(row), 5));
                            lstSelectedSightings.add(sighting);
                        }
                    }
                }
                else {
                    lstSelectedSightings = new ArrayList<>(0);
                }
            }
        }
        else
        if (inLayoutType == LayoutType.GRID) {
            Set<Long> processedIDs = new HashSet<>();
            for (Component componenet : ((JPanel) ((JScrollPane) pnlLayoutView.getComponent(0)).getViewport().getComponent(0)).getComponents()) {
                if (componenet instanceof SightingBox) {
                    SightingBox sightingBox = (SightingBox) componenet;
                    if (sightingBox.isSelected()) {
                        if (processedIDs.add(sightingBox.getSighting().getID())) {
                            lstSelectedSightings.add(sightingBox.getSighting());
                        }
                    }
                }
            }
        }
        return lstSelectedSightings;
    }
    
    private void setupDefaultFilters() {
        // Setup new FilterProperties
        filterProperties = new FilterProperties();
        FilterPropertiesDialog.setDefaultValues(true, filterProperties);
        // Also reset the filter lists
        lstFilteredLocations = generateIDList(app.getDBI().listLocations(null, false, Location.class));
        lstFilteredVisits = generateIDList(app.getDBI().listVisits(null, 0, null, true, Visit.class));
        lstFilteredElements = generateIDList(app.getDBI().listElements(null, null, null, false, Element.class));
        // Reset the GPS box
        northEast_Latitude = 0.0;
        northEast_Longitude = 0.0;
        southWest_Latitude = 0.0;
        southWest_Longitude = 0.0;
    }
    
    private <T extends DataObjectBasicInfo> List<Long> generateIDList(List<T> inLstDataObjects) {
        List<Long> lstIDs = new ArrayList<>(inLstDataObjects.size());
        for (DataObjectBasicInfo data : inLstDataObjects) {
            lstIDs.add(data.getIDField());
        }
        return lstIDs;
    }
    
    private void reloadUI(LayoutType inPrevLayoutType) {
        // Get the currently selected sightings
        List<Sighting> lstSelectedSightings = getListOfSelectedSightings(inPrevLayoutType, true);
        // Reload the UI
        if (rdbLayoutTable.isSelected()) {
            pnlImage.setVisible(true);
            pnlFilters.setVisible(true);
            pnlGridOptions.setVisible(false);
            pnlLayoutView.removeAll();
            pnlLayoutView.revalidate();
            pnlLayoutView.repaint();
            pnlLayoutView.add(scrSightings, BorderLayout.CENTER);
            // Load the table
            UtilsTableGenerator.setupSightingTableForMainTab(app, tblSightings, lblFilterDetails, 
                    filterProperties, lstFilteredLocations, lstFilteredVisits, lstFilteredElements, 
                    northEast_Latitude, northEast_Longitude, southWest_Latitude, southWest_Longitude);
            // Refresh the image
            tblSightingsMouseReleased(null);
        }
        else
        if (rdbLayoutGridSightings.isSelected() || rdbLayoutGridFiles.isSelected()) {
            pnlImage.setVisible(false);
            pnlFilters.setVisible(false);
            pnlGridOptions.setVisible(true);
            // Clear the old table (to hopefully free up its resources)
            tblSightings.setModel(new DefaultTableModel(new String[]{"inactive"}, 0));
            pnlLayoutView.removeAll();
            pnlLayoutView.revalidate();
            pnlLayoutView.repaint();
            // Setup the new grid
            final JPanel pnlGrid = new JPanel();
            pnlGrid.setLayout(new ScrollableWrappedFlowLayout(FlowLayout.CENTER));
            JLabel lblTemp = new JLabel("Loading...");
            lblTemp.setForeground(Color.WHITE);
            lblTemp.setFont(lblTemp.getFont().deriveFont(18f));
            pnlGrid.add(lblTemp);
            pnlGrid.setBackground(Color.BLACK);
            JScrollPane scrGrid = new JScrollPane(pnlGrid, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrGrid.getVerticalScrollBar().setUnitIncrement(35);
            pnlLayoutView.add(scrGrid, BorderLayout.CENTER);
            pnlLayoutView.revalidate();
            pnlLayoutView.repaint();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LocalDateTime startDateTime;
                    if (filterProperties.getStartDate() != null) {
                        startDateTime = LocalDateTime.of(filterProperties.getStartDate(), LocalTime.MIN);
                    }
                    else {
                        startDateTime = null;
                    }
                    LocalDateTime endDateTime;
                    if (filterProperties.getEndDate() != null) {
                        endDateTime = LocalDateTime.of(filterProperties.getEndDate(), LocalTime.MAX);
                    }
                    else {
                        endDateTime = null;
                    }
                    List<Sighting> lstSightings = app.getDBI().searchSightings(filterProperties.getSightingIDs(), 
                                UtilsTime.getDateFromLocalDateTime(startDateTime), UtilsTime.getDateFromLocalDateTime(endDateTime), 
                                lstFilteredLocations, lstFilteredVisits, lstFilteredElements, true, Sighting.class);
                    Collections.sort(lstSightings);
                    pnlGrid.removeAll();
                    if (!lstSightings.isEmpty()) {
                        ((ScrollableWrappedFlowLayout) pnlGrid.getLayout()).setAlignment(FlowLayout.LEFT);
                        for (Sighting sighting : lstSightings) {
                            if (rdbLayoutGridSightings.isSelected()) {
                                generateGridBox(pnlGrid, sighting, 0);
                            }
                            else
                            if (rdbLayoutGridFiles.isSelected()) {
                                List<WildLogFile> lstSightingFiles = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
                                if (lstSightingFiles != null && !lstSightingFiles.isEmpty()) {
                                    for (int t = 0; t < lstSightingFiles.size(); t++) {
                                        generateGridBox(pnlGrid, sighting, t);
                                    }
                                }
                                else {
                                    generateGridBox(pnlGrid, sighting, 0);
                                }
                            }
                        }
                    }
                    else {
                        JLabel lblTemp = new JLabel("No Observations were found that match the currently active filters.");
                        lblTemp.setForeground(Color.WHITE);
                        lblTemp.setFont(lblTemp.getFont().deriveFont(18f));
                        pnlGrid.add(lblTemp);
                    }
                    pnlGrid.revalidate();
                    pnlGrid.repaint();
                    pnlLayoutView.revalidate();
                    pnlLayoutView.repaint();
                }
            });
        }
        // After the table / grid has been loaded, then reselect the sightings
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Reselect the previously selected sightings
                if (rdbLayoutTable.isSelected()) {
                    if (!lstSelectedSightings.isEmpty() && tblSightings.getColumnCount() != 1) {
                        long[] selectedRowIDs = new long[lstSelectedSightings.size()];
                        for (int t = 0; t < lstSelectedSightings.size(); t++) {
                            selectedRowIDs[t] = lstSelectedSightings.get(t).getID();
                        }
                        UtilsTableGenerator.setupPreviousRowSelection(tblSightings, selectedRowIDs, 8);
                    }
                }
                else
                if (rdbLayoutGridSightings.isSelected() || rdbLayoutGridFiles.isSelected()) {
                    for (Sighting sighting : lstSelectedSightings) {
                        for (Component componenet : ((JPanel) ((JScrollPane) pnlLayoutView.getComponent(0)).getViewport().getComponent(0)).getComponents()) {
                            if (componenet instanceof SightingBox) {
                                SightingBox sightingBox = (SightingBox) componenet;
                                if (sighting.getID() == sightingBox.getSighting().getID()) {
                                    sightingBox.toggleSelection();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void generateGridBox(JPanel inPnlGrid, Sighting inSighting, int inFileIndex) {
        SightingBox sightingBox = new SightingBox(inSighting, inFileIndex, rdbLayoutGridFiles.isSelected());
        sightingBox.setBoxSize((WildLogThumbnailSizes) cmbGridSize.getSelectedItem());
        inPnlGrid.add(sightingBox);
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnBulkEditSighting;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFilterElements;
    private javax.swing.JButton btnFilterLocation;
    private javax.swing.JButton btnFilterMap;
    private javax.swing.JButton btnFilterProperties;
    private javax.swing.JButton btnFilterVisit;
    private javax.swing.JButton btnGoBrowse;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnGoSighting;
    private javax.swing.JButton btnGoVisit;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextFile;
    private javax.swing.JButton btnPrevFile;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnResetFilters;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<WildLogThumbnailSizes> cmbGridSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblFilterDetails;
    private javax.swing.JLabel lblImage;
    private javax.swing.JPanel pnlFeatures;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlGridOptions;
    private javax.swing.JPanel pnlImage;
    private javax.swing.JPanel pnlLayoutView;
    private javax.swing.JPanel pnlViews;
    private javax.swing.JRadioButton rdbLayoutGridFiles;
    private javax.swing.JRadioButton rdbLayoutGridSightings;
    private javax.swing.JRadioButton rdbLayoutTable;
    private javax.swing.JScrollPane scrSightings;
    private javax.swing.JTable tblSightings;
    // End of variables declaration//GEN-END:variables
}
