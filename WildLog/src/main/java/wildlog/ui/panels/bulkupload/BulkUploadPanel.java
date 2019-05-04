package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogFileLinkType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.ZoomDialog;
import wildlog.ui.helpers.CustomMouseWheelScroller;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLFileChooser;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.filters.ImageFilter;
import wildlog.ui.panels.bulkupload.data.BulkUploadDataLoader;
import wildlog.ui.panels.bulkupload.data.BulkUploadDataWrapper;
import wildlog.ui.panels.bulkupload.editors.ImageBoxEditor;
import wildlog.ui.panels.bulkupload.editors.InfoBoxEditor;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.ui.panels.bulkupload.renderers.ImageBoxRenderer;
import wildlog.ui.panels.bulkupload.renderers.InfoBoxRenderer;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogPaths;


public class BulkUploadPanel extends PanelCanSetupHeader {
    public final static Color tableBackgroundColor1 = new Color(235, 246, 220);
    public final static Color tableBackgroundColor2 = new Color(195, 205, 180);
    private static String lastFilePath = "";
    private final WildLogApp app;
    private final CustomMouseWheelScroller mouseWheel;
    private final List<Path> lstVisitFiles;
    private final PanelNeedsRefreshWhenDataChanges panelToRefresh;
    final private Visit existingVisit;
    private Location selectedLocation;
    private List<Path> lstImportPaths = null;
    private boolean showAsTab = false;
    private VisitType originalVisitType = VisitType.UNKNOWN;
    private String originalVisitName = null;

    
// TODO: Add a button that does the "adjust date and time" popup for all observations
    
// TODO: Om ding vinniger te maak kan ek 'n HashMap hou van elke file se metadata (exif - basies net die gps + date?) wanneer ek dit die eerste keer lees, dan later as ek safe kan dit die cache gebruik in plaas van weer die files lees
    

    public BulkUploadPanel(WildLogApp inApp, ProgressbarTask inProgressbarTask, Location inLocation, Visit inExistingVisit, 
            List<Path> inlstImportPaths, PanelNeedsRefreshWhenDataChanges inPanelToRefresh) {
        WildLogApp.LOGGER.log(Level.INFO, "[BulkUploadPanel]");
        app = inApp;
        selectedLocation = inLocation;
        existingVisit = inExistingVisit;
        // Store the original visit type, because we need to know what it was when we do the saving
        if (existingVisit != null) {
            originalVisitType = existingVisit.getType();
            originalVisitName = existingVisit.getName();
        }
        panelToRefresh = inPanelToRefresh;
        if (inlstImportPaths != null) {
            lstImportPaths = inlstImportPaths;
        }
        lstVisitFiles = new ArrayList<Path>(3);
        // Init auto generated code
        initComponents();
        // Set table scroling to only one row at a time
        mouseWheel = new CustomMouseWheelScroller(scrTable);
        mouseWheel.install();
        // "Hack" to make the buttons clickable when the mouse scrolls over the cell (very performance intensive, but "better" now...)
        // of as mens die model clear deur 'n nuwe browse besigheid oop te maak...
        final JTable tableHandle = tblBulkImport;
        tblBulkImport.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent inEvent) {
                int row = tableHandle.rowAtPoint(inEvent.getPoint());
                int col = tableHandle.columnAtPoint(inEvent.getPoint());
                if (row != tableHandle.getEditingRow() || col != tableHandle.getEditingColumn()) {
                    try {
                        if (row >= 0 && row < tableHandle.getModel().getRowCount() && col >= 0 && col < tableHandle.getModel().getColumnCount()) {
                            tableHandle.editCellAt(row, col);
                        }
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
        });
        // Load the location detials
        if (selectedLocation != null) {
            lblLocation.setText(selectedLocation.getName());
            UtilsImageProcessing.setupFoto(selectedLocation.getWildLogFileID(), 0, lblImageLocation, WildLogThumbnailSizes.SMALL, app);
        }
        else {
            lblLocation.setText("");
            lblImageLocation.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
        // If this bulk import was called from the Visit then set the name and disable the fields
        if (existingVisit != null && existingVisit.getID() > 0) {
            // Location fields
            lblLocation.setEnabled(false);
            //lblImageLocation.setEnabled(false);
            btnSelectLocation.setEnabled(false);
            // Visit fields
            txtVisitName.setText(existingVisit.getName());
            if (VisitType.STASHED != existingVisit.getType()) {
                txtVisitName.setEnabled(false);
                cmbVisitType.setSelectedItem(existingVisit.getType());
                cmbVisitType.setEnabled(false);
            }
            else {
                cmbVisitType.setSelectedItem(VisitType.NONE);
            }
            dtpStartDate.setDate(existingVisit.getStartDate());
            dtpEndDate.setDate(existingVisit.getEndDate());
        }
        // Setup the tab's content
        setupTab(inProgressbarTask);
        // Setup the initial visit name
        setupVisitName();
        // Setup clipboard
        UtilsUI.attachClipboardPopup(txtVisitName);
        // Setup info for tab headers
        tabTitle = "Bulk Import";
        tabID = 0;
        tabIconURL = app.getClass().getResource("resources/icons/Bulk Import.png");
        // Spinner selection fix
        SpinnerFixer.configureSpinners(spnInactivityTime);
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(pnlTop.getBackground());
        dtpEndDate.getComponent(1).setBackground(pnlTop.getBackground());
    }

    public final void setupTab(ProgressbarTask inProgressbarTask) {
        inProgressbarTask.setMessage("Preparing the Bulk Import process...");
        inProgressbarTask.setTaskProgress(0);
        // Load the images
        loadImages(inProgressbarTask);
        inProgressbarTask.setTaskProgress(100);
        inProgressbarTask.setMessage("Finished preparing the Bulk Import");
    }

    @Override
    public boolean closeTab() {
        ((JTabbedPane)getParent()).remove(this);
        return true;
    }

    private void loadImages(ProgressbarTask inProgressbarTask) {
        // Get the list of files from the folder to import from
        if (lstImportPaths == null) {
            lstImportPaths = showFileChooser();
        }
        if (lstImportPaths != null) {
            // Setup the datamodel
            final DefaultTableModel model = ((DefaultTableModel) tblBulkImport.getModel());
            model.getDataVector().clear();
            model.fireTableDataChanged();
            BulkUploadDataWrapper wrapper = BulkUploadDataLoader.genenrateTableData(
                    lstImportPaths, chkIncludeSubfolders.isSelected(), (Integer)spnInactivityTime.getValue(), 
                    inProgressbarTask, lblFilesRead, app);
            if (wrapper != null) {
                model.getDataVector().addAll(UtilsTableGenerator.convertToVector(wrapper.getData()));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        model.fireTableDataChanged();
                        WildLogApp.LOGGER.log(Level.INFO, "BulkUploadPanel.loadImages() - The model has been loaded to the table (finished fireTableDataChanged)");
                        updateCountForFilesLinked();
                    }
                });
                // Setup the dates
                if (existingVisit != null && existingVisit.getID() > 0) {
                    if (dtpStartDate.getDate() == null || wrapper.getStartDate().before(dtpStartDate.getDate())) {
                        dtpStartDate.setDate(wrapper.getStartDate());
                    }
                    if (dtpEndDate.getDate() == null || wrapper.getStartDate().after(dtpEndDate.getDate())) {
                        dtpEndDate.setDate(wrapper.getEndDate());
                    }
                }
                else {
                    dtpStartDate.setDate(wrapper.getStartDate());
                    dtpEndDate.setDate(wrapper.getEndDate());
                }
                // Setup the recursive folder checkbox
                chkIncludeSubfolders.setSelected(wrapper.isRecursive());
                showAsTab = true;
            }
            else {
                showAsTab = false;
            }
        }
        else {
            showAsTab = false;
        }
    }
    
    private void setupVisitName() {
        if (existingVisit == null || existingVisit.getID() <= 0 || existingVisit.getType() == VisitType.STASHED) {
            if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN || 
                    WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                String locationName;
                if (selectedLocation != null && selectedLocation.getName() != null && !selectedLocation.getName().isEmpty()) {
                    locationName = selectedLocation.getName();
                }
                else {
                    locationName = "UnkownPlace";
                }
                LocalDate startDate;
                if (dtpStartDate.getDate() != null) {
                    startDate = UtilsTime.getLocalDateFromDate(dtpStartDate.getDate());
                }
                else {
                    startDate = LocalDate.now();
                }
                LocalDate endDate;
                if (dtpEndDate.getDate() != null) {
                    endDate = UtilsTime.getLocalDateFromDate(dtpEndDate.getDate());
                }
                else {
                    endDate = LocalDate.now();
                }
                txtVisitName.setText(UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(startDate) + "-" + UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(endDate)
                        + "_" + locationName);
            }
            else {
                if (txtVisitName.getText() == null || txtVisitName.getText().isEmpty()) {
                    txtVisitName.setText("Bulk Import - " + UtilsTime.WL_DATE_FORMATTER_FOR_VISIT_NAME.format(LocalDateTime.now()));
                }
            }
        }
    }

    private List<Path> showFileChooser() {
        WLFileChooser fileChooser;
        if (lastFilePath != null && lastFilePath.length() > 0) {
            fileChooser = new WLFileChooser(lastFilePath);
        }
        else {
            fileChooser = new WLFileChooser();
        }
        fileChooser.setDialogTitle("Select a folder to import");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new ImageFilter());
        int result = fileChooser.showOpenDialog(app.getMainFrame());
        if (result == JFileChooser.ERROR_OPTION || result != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFiles() == null) {
            return null;
        }
        else {
            lastFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            return UtilsFileProcessing.getPathsFromSelectedFile(fileChooser.getSelectedFiles());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        cmbVisitType = new javax.swing.JComboBox();
        txtVisitName = new javax.swing.JTextField();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel5 = new javax.swing.JLabel();
        lblVisitFiles = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        spnInactivityTime = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        btnReload = new javax.swing.JButton();
        chkIncludeSubfolders = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnSelectLocation = new javax.swing.JButton();
        lblLocation = new javax.swing.JLabel();
        lblImageLocation = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblFilesRead = new javax.swing.JLabel();
        lblFilesLinked = new javax.swing.JLabel();
        btnGPSForAll = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        scrTable = new javax.swing.JScrollPane();
        tblBulkImport = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        pnlTop.setBackground(new java.awt.Color(153, 180, 115));
        pnlTop.setMaximumSize(new java.awt.Dimension(1005, 130));
        pnlTop.setMinimumSize(new java.awt.Dimension(1005, 130));
        pnlTop.setName("pnlTop"); // NOI18N
        pnlTop.setPreferredSize(new java.awt.Dimension(1005, 130));

        jPanel2.setBackground(new java.awt.Color(153, 180, 115));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Period Name:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("End Date:");
        jLabel4.setName("jLabel4"); // NOI18N

        dtpEndDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        dtpEndDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dtpEndDatePropertyChange(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Start Date:");
        jLabel3.setName("jLabel3"); // NOI18N

        cmbVisitType.setMaximumRowCount(15);
        cmbVisitType.setModel(new DefaultComboBoxModel(VisitType.valuesForDroplist()));
        cmbVisitType.setSelectedItem(VisitType.OTHER);
        cmbVisitType.setFocusable(false);
        cmbVisitType.setName("cmbVisitType"); // NOI18N

        txtVisitName.setBackground(new java.awt.Color(204, 255, 204));
        txtVisitName.setName("txtVisitName"); // NOI18N

        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        dtpStartDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dtpStartDatePropertyChange(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Type:");
        jLabel5.setName("jLabel5"); // NOI18N

        lblVisitFiles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitFiles.setText("Files Moved To Period: 0");
        lblVisitFiles.setToolTipText("Click view the files that will be uploaded for the new Period. Right-click the Delete button to move files to the Period.");
        lblVisitFiles.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblVisitFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblVisitFiles.setName("lblVisitFiles"); // NOI18N
        lblVisitFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblVisitFilesMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(10, 10, 10)
                        .addComponent(txtVisitName)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5)
                        .addGap(8, 8, 8)
                        .addComponent(cmbVisitType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(22, 22, 22)
                        .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(lblVisitFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVisitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbVisitType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblVisitFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        jPanel3.setBackground(new java.awt.Color(153, 180, 115));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setText("Start new Observations after");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("seconds of inactivaty.");
        jLabel7.setName("jLabel7"); // NOI18N

        spnInactivityTime.setModel(new javax.swing.SpinnerNumberModel(120, 1, 10000000, 1));
        spnInactivityTime.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnInactivityTime.setName("spnInactivityTime"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Settings:");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setName("jLabel8"); // NOI18N

        btnReload.setBackground(new java.awt.Color(153, 180, 115));
        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnReload.setText("Reload Using New Settings");
        btnReload.setToolTipText("<html>Reload the Bulk Import using the provided settings. <br/>Warning: All changes will be lost.</html>");
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.setFocusPainted(false);
        btnReload.setFocusable(false);
        btnReload.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnReload.setName("btnReload"); // NOI18N
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        chkIncludeSubfolders.setBackground(new java.awt.Color(153, 180, 115));
        chkIncludeSubfolders.setText("Include Subfolders");
        chkIncludeSubfolders.setToolTipText("Select this checkbox and press the Reload button to also look in subfolder for files to include in the Bulk Import.");
        chkIncludeSubfolders.setFocusable(false);
        chkIncludeSubfolders.setName("chkIncludeSubfolders"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel8)
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(5, 5, 5)
                        .addComponent(spnInactivityTime, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel7))
                    .addComponent(chkIncludeSubfolders))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(btnReload)
                .addGap(5, 5, 5))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnInactivityTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addComponent(chkIncludeSubfolders))
                    .addComponent(btnReload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jPanel4.setBackground(new java.awt.Color(153, 180, 115));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Place Name:");
        jLabel1.setName("jLabel1"); // NOI18N

        btnSelectLocation.setBackground(new java.awt.Color(153, 180, 115));
        btnSelectLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/LocationList.gif"))); // NOI18N
        btnSelectLocation.setText("Choose Place");
        btnSelectLocation.setToolTipText("Select a Place to use for all Observation.");
        btnSelectLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelectLocation.setFocusPainted(false);
        btnSelectLocation.setFocusable(false);
        btnSelectLocation.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnSelectLocation.setName("btnSelectLocation"); // NOI18N
        btnSelectLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectLocationActionPerformed(evt);
            }
        });

        lblLocation.setBackground(new java.awt.Color(233, 239, 244));
        lblLocation.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 1, 1));
        lblLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        lblLocation.setName("lblLocation"); // NOI18N
        lblLocation.setOpaque(true);
        lblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblLocationMousePressed(evt);
            }
        });

        lblImageLocation.setBackground(new java.awt.Color(0, 0, 0));
        lblImageLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImageLocation.setName("lblImageLocation"); // NOI18N
        lblImageLocation.setOpaque(true);
        lblImageLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageLocationMouseReleased(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(153, 180, 115));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("File Count");
        jLabel10.setName("jLabel10"); // NOI18N

        lblFilesRead.setText("Read: 0");
        lblFilesRead.setName("lblFilesRead"); // NOI18N

        lblFilesLinked.setText("Linked: 0");
        lblFilesLinked.setName("lblFilesLinked"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(lblFilesRead)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(lblFilesLinked)
                .addContainerGap(37, Short.MAX_VALUE))
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFilesRead, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFilesLinked, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImageLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelectLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImageLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnSelectLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );

        btnGPSForAll.setBackground(new java.awt.Color(153, 180, 115));
        btnGPSForAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPSForAll.setText("<html>Set one GPS point for all Observations</html>");
        btnGPSForAll.setToolTipText("The specified GPS point will be applied to all currently defined Observations.");
        btnGPSForAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPSForAll.setFocusPainted(false);
        btnGPSForAll.setFocusable(false);
        btnGPSForAll.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPSForAll.setMargin(new java.awt.Insets(2, 4, 2, 1));
        btnGPSForAll.setName("btnGPSForAll"); // NOI18N
        btnGPSForAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSForAllActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 204, 0));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdate.setToolTipText("Save the defined Observations for the specified Period and Place.");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGPSForAll, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(btnGPSForAll)
                        .addGap(1, 1, 1)))
                .addGap(2, 2, 2))
        );

        add(pnlTop, java.awt.BorderLayout.PAGE_START);

        scrTable.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrTable.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrTable.setName("scrTable"); // NOI18N

        tblBulkImport.setBackground(new java.awt.Color(229, 241, 212));
        tblBulkImport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Observations", "Files"
            }
        ));
        tblBulkImport.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblBulkImport.setFillsViewportHeight(true);
        tblBulkImport.setFocusable(false);
        tblBulkImport.setGridColor(new java.awt.Color(134, 123, 78));
        tblBulkImport.setName("tblBulkImport"); // NOI18N
        tblBulkImport.setRowHeight(250);
        tblBulkImport.setSelectionBackground(new java.awt.Color(229, 241, 212));
        tblBulkImport.setSelectionForeground(new java.awt.Color(229, 241, 212));
        tblBulkImport.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBulkImport.getTableHeader().setResizingAllowed(false);
        tblBulkImport.getTableHeader().setReorderingAllowed(false);
        scrTable.setViewportView(tblBulkImport);
        if (tblBulkImport.getColumnModel().getColumnCount() > 0) {
            tblBulkImport.getColumnModel().getColumn(0).setMinWidth(240);
            tblBulkImport.getColumnModel().getColumn(0).setPreferredWidth(240);
            tblBulkImport.getColumnModel().getColumn(0).setMaxWidth(240);
            tblBulkImport.getColumnModel().getColumn(0).setCellEditor(new InfoBoxEditor(app, selectedLocation, existingVisit));
            tblBulkImport.getColumnModel().getColumn(0).setCellRenderer(new InfoBoxRenderer(app, selectedLocation, existingVisit));
            tblBulkImport.getColumnModel().getColumn(1).setCellEditor(new ImageBoxEditor());
            tblBulkImport.getColumnModel().getColumn(1).setCellRenderer(new ImageBoxRenderer());
        }

        add(scrTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        // Update die table om nie meer te edit nie
        if (tblBulkImport.getCellEditor() != null) {
            tblBulkImport.getCellEditor().cancelCellEditing();
        }
        tblBulkImport.clearSelection();
        // You can only use a task once, hence for the saving we need to create a new task
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                txtVisitName.requestFocus();
                // Disable die button sodat mens dit nie weer kan druk nie
                btnReload.setEnabled(false);
                btnGPSForAll.setEnabled(false);
                Border originalBorder = btnReload.getBorder();
                Timer timer = UtilsUI.doAnimationForFlashingBorder(btnReload.getBackground(), new Color(200, 220, 250), btnReload);
                btnUpdate.setEnabled(false);
                // Do the re-setting up
                setupTab(this);
                // Enable die button sodat mens dit weer kan druk
                btnReload.setEnabled(true);
                btnGPSForAll.setEnabled(true);
                timer.stop();
                btnReload.setBorder(originalBorder);
                btnUpdate.setEnabled(true);
                btnReload.requestFocus();
                return null;
            }
        });
    }//GEN-LAST:event_btnReloadActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // You can only use a task once, hence for the saving we need to create a new task
        final Container thisParentHandle = this.getParent();
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                WildLogApp.LOGGER.log(Level.INFO, "Starting BulkUploadPanel.btnUpdateActionPerformed() - The data will first be validated.");
                long time = System.currentTimeMillis();
                this.setTaskProgress(0);
                this.setMessage("Saving the Bulk Import: Validating...");
                // Make sure the location is OK
                if (selectedLocation != null && selectedLocation.getID() > 0 && txtVisitName.getText() != null && !txtVisitName.getText().isEmpty()) {
                    // Validate the visit is OK
                    Visit visit = app.getDBI().findVisit(0, txtVisitName.getText(), false, Visit.class);
                    if ((existingVisit == null || existingVisit.getID() == 0) && visit != null) {
                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                "The Period name is not unique, please specify another one.",
                                "Can't Save", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    if (visit != null && existingVisit != null && existingVisit.getID() != visit.getID()) {
                            WLOptionPane.showMessageDialog(app.getMainFrame(),
                                    "The existing Period could not be saved.",
                                    "Can't Save", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                    if (visit == null) {
                        visit = new Visit(0, txtVisitName.getText());
                        if (existingVisit != null && existingVisit.getID() > 0) {
                            visit.setID(existingVisit.getID());
                        }
                    }
                    visit.setLocationID(selectedLocation.getID());
                    visit.setCachedLocationName(app.getDBI().findLocation(visit.getLocationID(), null, Location.class).getName());
                    visit.setStartDate(dtpStartDate.getDate());
                    visit.setEndDate(dtpEndDate.getDate());
                    visit.setType((VisitType) cmbVisitType.getSelectedItem());
                    // Validate all sightings have a creature set
                    final DefaultTableModel model = (DefaultTableModel)tblBulkImport.getModel();
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        if (sightingWrapper.getElementID() == 0) {
                            final int finalRowCount = rowCount;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsUI.scrollTableRowToTop(tblBulkImport, finalRowCount);
                                }
                            });
                            WLOptionPane.showMessageDialog(app.getMainFrame(),
                                    "Please assign a Creature to each of the Observations.",
                                    "Can't Save", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                    }
                    // Validate the certainty is set
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        if (sightingWrapper.getCertainty() == null || sightingWrapper.getCertainty().equals(Certainty.NONE)) {
                            final int finalRowCount = rowCount;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    UtilsUI.scrollTableRowToTop(tblBulkImport, finalRowCount);
                                }
                            });
                            WLOptionPane.showMessageDialog(app.getMainFrame(),
                                    "Please assign a Certainty to each of the Observations.",
                                    "Can't Save", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                    }
                    // Validate (warning) if any sighting doesn't have a GPS coordinate
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        if (sightingWrapper.getLatitude() == null || Latitudes.NONE.equals(sightingWrapper.getLatitude())
                                || sightingWrapper.getLongitude() == null || Longitudes.NONE.equals(sightingWrapper.getLongitude())) {
                            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "There are Observations without GPS Coordinates. Are you sure you want to save?",
                                    "Missing GPS Coordinates", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                break;
                            }
                            else {
                                final int finalRowCount = rowCount;
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        UtilsUI.scrollTableRowToTop(tblBulkImport, finalRowCount);
                                    }
                                });
                                return null;
                            }
                        }
                    }
                    // Validate (warning) all Sightings fall inside the Visit's dates
                    LocalDate startDate;
                    if (visit.getStartDate() != null) {
                        startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                    }
                    else {
                        startDate = null;
                    }
                    LocalDate endDate;
                    if (visit.getEndDate() != null) {
                        endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    }
                    else {
                        endDate = null;
                    }
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sightingWrapper.getDate());
                        if ((startDate != null && sightingDate.isBefore(startDate))
                                || (endDate != null && sightingDate.isAfter(endDate))) {
                            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "There are Observations that fall outside of the specified date range of the Perios. Are you sure you want to save?",
                                    "Invalid Period Date Range", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                break;
                            }
                            else {
                                final int finalRowCount = rowCount;
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        tblBulkImport.scrollRectToVisible(tblBulkImport.getCellRect(finalRowCount, 1, true));
                                    }
                                });
                                return null;
                            }
                        }
                    }
                    // Everything seems fine, start saving and close the tab to prevent new edits
                    WildLogApp.LOGGER.log(Level.INFO, "Starting BulkUploadPanel.btnUpdateActionPerformed() - The data will be saved to the workspace.");
                    this.setTaskProgress(0);
                    this.setMessage("Saving the Bulk Import: Starting...");
                    closeTab();
                    // Save the Visit (before saving the sightings, because the Visit's ID is needed)
                    if (existingVisit == null || existingVisit.getID() == 0) {
                        app.getDBI().createVisit(visit, false);
                    }
                    else {
                        app.getDBI().updateVisit(visit, existingVisit.getName(), false);
                    }
                    // Save the images associated with the Visit
                    File[] visitFiles = new File[lstVisitFiles.size()];
                    for (int t = 0; t < lstVisitFiles.size(); t++) {
                        visitFiles[t] = lstVisitFiles.get(t).toFile();
                    }
                    UtilsFileProcessing.performFileUpload(visit,
                            Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(visit.getName()), WildLogFileLinkType.VISIT, 
                            visitFiles,
                            null, 
                            app, false, null, true, false);
                    this.setTaskProgress(1);
                    this.setMessage("Saving the Bulk Import: Saving the Period... " + this.getProgress() + "%");
                    // Processs the sightings
                    final Visit visitHandle = visit;
                    final Object saveSightingLock = new Object();
                    String executorServiceName = "WL_BulkImport(Save)";
                    ExecutorService executorService = Executors.newFixedThreadPool(app.getThreadCount(), new NamedThreadFactory(executorServiceName));
                    final AtomicInteger counter = new AtomicInteger();
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        final int row = rowCount;
                        final ProgressbarTask progressbarHandle = this;
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(row, 0);
                                // Continue processing the Sighting
                                sightingWrapper.setLocationID(visitHandle.getLocationID());
                                sightingWrapper.setCachedLocationName(visitHandle.getCachedLocationName());
                                sightingWrapper.setVisitID(visitHandle.getID());
                                sightingWrapper.setCachedVisitName(visitHandle.getName());
                                // If the sighting's Date and GPS point is set then try to calculate Sun and Moon
                                if (sightingWrapper.getDate() != null && sightingWrapper.getTimeAccuracy() != null && sightingWrapper.getTimeAccuracy().isUsableTime()) {
                                    if (sightingWrapper.getLatitude() != null && !sightingWrapper.getLatitude().equals(Latitudes.NONE)
                                            && sightingWrapper.getLongitude() != null && !sightingWrapper.getLongitude().equals(Longitudes.NONE)) {
                                        double latitude = UtilsGPS.getDecimalDegree(sightingWrapper.getLatitude(), sightingWrapper.getLatDegrees(), sightingWrapper.getLatMinutes(), sightingWrapper.getLatSeconds());
                                        double longitude = UtilsGPS.getDecimalDegree(sightingWrapper.getLongitude(), sightingWrapper.getLonDegrees(), sightingWrapper.getLonMinutes(), sightingWrapper.getLonSeconds());
                                        // Sun
                                        if (sightingWrapper.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sightingWrapper.getTimeOfDay())) {
                                            sightingWrapper.setTimeOfDay(AstroCalculator.getSunCategory(sightingWrapper.getDate(), latitude, longitude));
                                        }
                                        // Moon
                                        if (sightingWrapper.getMoonlight() == null || Moonlight.NONE.equals(sightingWrapper.getMoonlight()) || Moonlight.UNKNOWN.equals(sightingWrapper.getMoonlight())) {
                                            sightingWrapper.setMoonlight(AstroCalculator.getMoonlight(sightingWrapper.getDate(), latitude, longitude));
                                        }
                                    }
                                    if (sightingWrapper.getMoonPhase() < 0) {
                                        sightingWrapper.setMoonPhase(AstroCalculator.getMoonPhase(sightingWrapper.getDate()));
                                    }
                                }
                                // Get a list of all the images
                                BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, 1);
                                // Get a list of File objects from the BulkUploadImageFileWrapper and prepare for the duration calculations
                                Date startDate = listWrapper.getImageList().get(0).getDate();
                                Date endDate = listWrapper.getImageList().get(listWrapper.getImageList().size()-1).getDate();
                                List<File> files = new ArrayList<File>(listWrapper.getImageList().size());
                                boolean uploadListContainsDuplicates = true;
                                for (BulkUploadImageFileWrapper imageWrapper : listWrapper.getImageList()) {
                                    // Confirm the correct start and end times
                                    if (imageWrapper.getDate().getTime() < startDate.getTime()) {
                                        startDate = imageWrapper.getDate();
                                    }
                                    else
                                    if (imageWrapper.getDate().getTime() > endDate.getTime()) {
                                        endDate = imageWrapper.getDate();
                                    }
// TODO: Kyk of ek hierdie weer kan terug bring om te help met performance...
//                                    if (!uploadListContainsDuplicates) {
//                                        for (File testDuplicateFiles : files) {
//                                            if (testDuplicateFiles.equals(imageWrapper.getFile().toFile())) {
//                                                uploadListContainsDuplicates = true;
//                                                break;
//                                            }
//                                        }
//                                    }
                                    // Prepare file uploadlist
                                    files.add(imageWrapper.getFile().toFile());
                                }
                                // Determine the duration and build up the a list of File objects (to save later), if it wasn't set by hand already
                                if (sightingWrapper.getDurationMinutes() == 0 && sightingWrapper.getDurationSeconds() == 0) {
                                    if (sightingWrapper.getDurationMinutes() == 0 && sightingWrapper.getDurationSeconds() == 0.0) {
                                        double difference = (endDate.getTime() - startDate.getTime())/1000;
                                        int minutes = (int)difference/60;
                                        double seconds = difference - minutes*60.0;
                                        sightingWrapper.setDurationMinutes(minutes);
                                        sightingWrapper.setDurationSeconds(seconds);
                                    }
                                }
                                // Save the sigting
                                synchronized (saveSightingLock) {
                                    app.getDBI().createSighting(sightingWrapper, false);
                                }
                                // Save the corresponding images
                                UtilsFileProcessing.performFileUpload(sightingWrapper,
                                        Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.toPath()), WildLogFileLinkType.SIGHTING, 
                                        files.toArray(new File[files.size()]),
                                        null, 
                                        app, false, null, true, uploadListContainsDuplicates);
                                // Update the progress
                                try {
                                    progressbarHandle.setTaskProgress(counter.getAndIncrement(), 0, model.getRowCount());
                                    progressbarHandle.setMessage("Saving the Bulk Import: Busy... " + progressbarHandle.getProgress() + "%");
                                }
                                catch (Exception e) {
                                    WildLogApp.LOGGER.log(Level.ERROR, e.toString(), e);
                                }
                            }
                        });
                    }
                    long startTime = System.currentTimeMillis();
                    if (!UtilsConcurency.waitForExecutorToShutdown(executorService)) {
                        WLOptionPane.showMessageDialog(app.getMainFrame(),
                                "There was an unexpected problem while saving.",
                                "Problem Saving", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        long duration = System.currentTimeMillis() - startTime;
                        int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                        int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                        int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                        WildLogApp.LOGGER.log(Level.INFO, "ExecutorService {} took {} hours, {} minutes, {} seconds to save the sightings", 
                                new Object[]{executorServiceName, hours, minutes, seconds});
                    }
                    // Delete the stashed files
                    if (VisitType.STASHED == originalVisitType) {
                        this.setTaskProgress(99);
                        this.setMessage("Saving the Bulk Import: Deleting the stashed files... " + this.getProgress() + "%");
                        UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(originalVisitName).toFile());
                    }
                    // Saving is done, now open the visits's tab
                    if (existingVisit == null || existingVisit.getID() == 0) {
                        // Open the new tab
                        UtilsPanelGenerator.openPanelAsTab(app, visit.getID(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)thisParentHandle, selectedLocation);
                    }
                    else {
                        if (panelToRefresh != null) {
                            if (VisitType.STASHED == originalVisitType) {
                                // First close the old tab in case it was open
                                UtilsPanelGenerator.removeOpenedTab(visit.getID(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)thisParentHandle);
                                // Open a new tab
                                UtilsPanelGenerator.openPanelAsTab(app, visit.getID(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)thisParentHandle, selectedLocation);
                            }
                            else {
                                // Refresh the open tab
                                panelToRefresh.doTheRefresh(visit);
                            }
                        }
                    }
                    this.setMessage("Saving the Bulk Import: Finished");
                    this.setTaskProgress(100);
                }
                else {
                    WLOptionPane.showMessageDialog(app.getMainFrame(),
                            "Please provide a Place name and Period name before saving.",
                            "Can't Save", JOptionPane.ERROR_MESSAGE);
                }
                WildLogApp.LOGGER.log(Level.INFO, "Finished BulkUploadPanel.btnUpdateActionPerformed() - The process took {} seconds.", (System.currentTimeMillis() - time)/1000);
                return null;
            }
        });
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Reload the location (espesially the image) as it might have changed
        if (selectedLocation != null) {
            lblLocation.setText(selectedLocation.getName());
            UtilsImageProcessing.setupFoto(selectedLocation.getWildLogFileID(), 0, lblImageLocation, WildLogThumbnailSizes.SMALL, app);
        }
        else {
            lblLocation.setText("");
            lblImageLocation.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
        // Re-check all species images already assigned since some might have changed
        DefaultTableModel model = ((DefaultTableModel) tblBulkImport.getModel());
        for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
            BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper) model.getValueAt(rowCount, 0);
            if (sightingWrapper.getElementID() > 0) {
                Element element = app.getDBI().findElement(sightingWrapper.getElementID(), null, Element.class);
                if (element != null) {
                    JLabel tempLabel = new JLabel();
                    UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, tempLabel, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
                    sightingWrapper.setIcon(tempLabel.getIcon());
                    sightingWrapper.setCachedElementName(element.getPrimaryName());
                }
                else {
                    if (sightingWrapper.getElementID() == ElementSelectionDialog.getPreviousElementID()) {
                        ElementSelectionDialog.setPreviousElementID(0);
                    }
                    sightingWrapper.setElementID(0);
                    sightingWrapper.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_VERY_SMALL));
                    sightingWrapper.setCachedElementName("");
                }
                ((DefaultTableModel) tblBulkImport.getModel()).fireTableDataChanged();
            }
        }
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnGPSForAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSForAllActionPerformed
        Sighting tempSighting = new Sighting();
        if (selectedLocation != null) {
            tempSighting.setLocationID(selectedLocation.getID());
        }
        GPSDialog dialog = new GPSDialog(app, app.getMainFrame(), tempSighting);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            for (int t = 0; t < tblBulkImport.getModel().getRowCount(); t++) {
                BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)tblBulkImport.getModel().getValueAt(t, 0);
                sightingWrapper.setLatitude(tempSighting.getLatitude());
                sightingWrapper.setLatDegrees(tempSighting.getLatDegrees());
                sightingWrapper.setLatMinutes(tempSighting.getLatMinutes());
                sightingWrapper.setLatSeconds(tempSighting.getLatSeconds());
                sightingWrapper.setLongitude(tempSighting.getLongitude());
                sightingWrapper.setLonDegrees(tempSighting.getLonDegrees());
                sightingWrapper.setLonMinutes(tempSighting.getLonMinutes());
                sightingWrapper.setLonSeconds(tempSighting.getLonSeconds());
                sightingWrapper.setGPSAccuracy(tempSighting.getGPSAccuracy());
                sightingWrapper.setGPSAccuracyValue(tempSighting.getGPSAccuracyValue());
            }
        }
    }//GEN-LAST:event_btnGPSForAllActionPerformed

    private void btnSelectLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectLocationActionPerformed
        long locationID;
        if (selectedLocation != null) {
            locationID = selectedLocation.getID();
        }
        else {
            locationID = 0;
        }
        LocationSelectionDialog dialog = new LocationSelectionDialog(app.getMainFrame(), app, locationID);
        dialog.setVisible(true);
        if (dialog.isSelectionMade() && dialog.getSelectedLocationID() > 0) {
            selectedLocation = app.getDBI().findLocation(dialog.getSelectedLocationID(), null, Location.class);
            lblLocation.setText(selectedLocation.getName());
            UtilsImageProcessing.setupFoto(selectedLocation.getWildLogFileID(), 0, lblImageLocation, WildLogThumbnailSizes.SMALL, app);
            setupVisitName();
        }
    }//GEN-LAST:event_btnSelectLocationActionPerformed

    private void lblImageLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageLocationMouseReleased
        if (selectedLocation != null) {
            UtilsFileProcessing.openFile(selectedLocation.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblImageLocationMouseReleased

    private void lblLocationMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationMousePressed
        if (existingVisit == null || existingVisit.getID() == 0) {
            btnSelectLocationActionPerformed(null);
        }
    }//GEN-LAST:event_lblLocationMousePressed

    private void lblVisitFilesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblVisitFilesMouseReleased
        ZoomDialog dialog = new ZoomDialog(WildLogApp.getApplication().getMainFrame(), lstVisitFiles, 0);
        dialog.setVisible(true);
    }//GEN-LAST:event_lblVisitFilesMouseReleased

    private void dtpStartDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dtpStartDatePropertyChange
        setupVisitName();
    }//GEN-LAST:event_dtpStartDatePropertyChange

    private void dtpEndDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dtpEndDatePropertyChange
        setupVisitName();
    }//GEN-LAST:event_dtpEndDatePropertyChange

    public boolean isShowAsTab() {
        return showAsTab;
    }

    public void addVisitFile(Path inPath) {
        boolean found = false;
        for (Path path : lstVisitFiles) {
            if (path.equals(inPath)) {
                found = true;
                break;
            }
        }
        if (!found) {
            lstVisitFiles.add(inPath);
            lblVisitFiles.setText(lblVisitFiles.getText().substring(0, lblVisitFiles.getText().lastIndexOf(':') + 1) + " " + lstVisitFiles.size());
        }
    }
    
    public void updateCountForFilesLinked() {
        int fileCount = 0;
        DefaultTableModel model = ((DefaultTableModel) tblBulkImport.getModel());
        for (int row = 0; row < model.getRowCount(); row++) {
            BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, 1);
            fileCount = fileCount + listWrapper.getImageList().size();
        }
        lblFilesLinked.setText(lblFilesLinked.getText().substring(0, lblFilesLinked.getText().lastIndexOf(':') + 1) + " " + fileCount);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGPSForAll;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSelectLocation;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chkIncludeSubfolders;
    private javax.swing.JComboBox cmbVisitType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblFilesLinked;
    private javax.swing.JLabel lblFilesRead;
    private javax.swing.JLabel lblImageLocation;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblVisitFiles;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JSpinner spnInactivityTime;
    private javax.swing.JTable tblBulkImport;
    private javax.swing.JTextField txtVisitName;
    // End of variables declaration//GEN-END:variables
}
