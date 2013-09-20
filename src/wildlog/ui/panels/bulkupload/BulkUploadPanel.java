package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.VisitType;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.CustomMouseWheelScroller;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.UtilPanelGenerator;
import wildlog.ui.helpers.UtilTableGenerator;
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
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogThumbnailSizes;


public class BulkUploadPanel extends PanelCanSetupHeader {
    private int imageIndex;
    private WildLogApp app;
    private CustomMouseWheelScroller mouseWheel;
    private File importPath = null;
    private static String lastFilePath = "";
    public final static Color tableBackgroundColor1 = new Color(235, 246, 220);
    public final static Color tableBackgroundColor2 = new Color(195, 205, 180);


    /** Creates new form BulkUploadPanel */
    public BulkUploadPanel(WildLogApp inApp, ProgressbarTask inProgressbarTask, String inLocationName) {
        app = inApp;
        imageIndex = 0;
        // Init auto generated code
        initComponents();
        // Set table scroling to only one row at a time
        mouseWheel = new CustomMouseWheelScroller(scrTable);
        mouseWheel.install();
        // "Hack" to make the buttons clickable when teh mouse scrolles over the cell (very performance intensive, but "better" now...) of as mens die model clear deur 'n nuwe browse besigheid oop te maak...
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
                        ex.printStackTrace(System.err);
                    }
                }
            }
        });
        txtLocationName.setText(inLocationName);
        // Setup the tab's content
        lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        setupTab(inProgressbarTask);
        // Pre-select the location if present
        if (inLocationName != null && !inLocationName.isEmpty()) {
            txtLocationNameKeyReleased(null);
        }
        // Setup clipboard
        UtilsUI.attachClipboardPopup(txtLocationName);
        UtilsUI.attachClipboardPopup(txtVisitName);

        // Setup info for tab headers
        tabTitle = "Bulk Import";
        tabIconURL = app.getClass().getResource("resources/icons/Bulk Import.png");

        // Spinner selection fix
        SpinnerFixer.fixSelectAllForSpinners(spnInactivityTime);
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(pnlTop.getBackground());
        dtpEndDate.getComponent(1).setBackground(pnlTop.getBackground());
    }

    public final void setupTab(ProgressbarTask inProgressbarTask) {
        inProgressbarTask.setMessage("Preparing the Bulk Import process...");
        inProgressbarTask.setTaskProgress(0);
        // Setup the Location list
        getLocationList();
        // Load the images
        loadImages(inProgressbarTask);
        inProgressbarTask.setTaskProgress(100);
        inProgressbarTask.setMessage("Finished preparing the Bulk Import");
    }

    @Override
    public void closeTab() {
        ((JTabbedPane)getParent()).remove(this);
    }

    private void getLocationList() {
        List<Location> locations = app.getDBI().list(new Location());
        Collections.sort(locations);
        lstLocation.setListData(locations.toArray());
        txtLocationNameKeyReleased(null);
    }

    private void loadImages(ProgressbarTask inProgressbarTask) {
        // Get the list of files from the folder to import from
        if (importPath == null) {
            importPath = showFileChooser();
        }
        // Setup the datamodel
        DefaultTableModel model = ((DefaultTableModel)tblBulkImport.getModel());
        model.getDataVector().clear();
        model.fireTableDataChanged();
        BulkUploadDataWrapper wrapper = BulkUploadDataLoader.genenrateTableData(importPath, chkIncludeSubfolders.isSelected(), (Integer)spnInactivityTime.getValue(), inProgressbarTask, app);
        model.getDataVector().addAll(UtilTableGenerator.convertToVector(wrapper.getData()));
        model.fireTableDataChanged();
        // Setup the dates
        dtpStartDate.setDate(wrapper.getStartDate());
        dtpEndDate.setDate(wrapper.getEndDate());
    }

    private File showFileChooser() {
        final JFileChooser fileChooser;
        if (lastFilePath != null && lastFilePath.length() > 0) {
            fileChooser = new JFileChooser(lastFilePath);
        }
        else {
            fileChooser = new JFileChooser();
        }
        fileChooser.setDialogTitle("Select a folder to import");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new ImageFilter());
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(app.getMainFrame());
            }
        });
        if (result == JFileChooser.ERROR_OPTION || result != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFile() == null) {
            return null;
        }
        else {
            lastFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (fileChooser.getSelectedFile().isDirectory()) {
                return fileChooser.getSelectedFile();
            }
            else {
                return fileChooser.getSelectedFile().getParentFile();
            }
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        lblLocationImage = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        txtVisitName = new javax.swing.JTextField();
        chkShowInactiveTimes = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        spnInactivityTime = new javax.swing.JSpinner();
        btnReload = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        txtLocationName = new javax.swing.JTextField();
        scrLocation = new javax.swing.JScrollPane();
        lstLocation = new javax.swing.JList();
        btnGPSForAll = new javax.swing.JButton();
        chkIncludeSubfolders = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        cmbVisitType = new javax.swing.JComboBox();
        jSeparator3 = new javax.swing.JSeparator();
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Place Name:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Period Name:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Start Date:");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("End Date:");
        jLabel4.setName("jLabel4"); // NOI18N

        dtpStartDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpStartDate.setName("dtpStartDate"); // NOI18N

        dtpEndDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpEndDate.setName("dtpEndDate"); // NOI18N

        lblLocationImage.setBackground(new java.awt.Color(0, 0, 0));
        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setOpaque(true);
        lblLocationImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLocationImageMouseReleased(evt);
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

        txtVisitName.setBackground(new java.awt.Color(204, 255, 204));
        txtVisitName.setText("Bulk Import - " + new SimpleDateFormat("dd MMM yyyy (HH'h'mm)").format(Calendar.getInstance().getTime()));
        txtVisitName.setName("txtVisitName"); // NOI18N

        chkShowInactiveTimes.setBackground(new java.awt.Color(153, 180, 115));
        chkShowInactiveTimes.setText("Show inactive periods between sightings");
        chkShowInactiveTimes.setEnabled(false);
        chkShowInactiveTimes.setFocusable(false);
        chkShowInactiveTimes.setName("chkShowInactiveTimes"); // NOI18N

        jLabel6.setText("Start new sightings after");
        jLabel6.setName("jLabel6"); // NOI18N

        spnInactivityTime.setModel(new javax.swing.SpinnerNumberModel(120, 1, 10000000, 1));
        spnInactivityTime.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnInactivityTime.setName("spnInactivityTime"); // NOI18N

        btnReload.setBackground(new java.awt.Color(153, 180, 115));
        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnReload.setText("Reload");
        btnReload.setToolTipText("<html>Reload the Bulk Import using the provided settings. <br/>Warning: All changes will be lost.</html>");
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.setFocusPainted(false);
        btnReload.setName("btnReload"); // NOI18N
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        jLabel7.setText("seconds of inactivaty.");
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Settings:");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setName("jLabel8"); // NOI18N

        jSeparator1.setBackground(new java.awt.Color(196, 220, 172));
        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setBackground(new java.awt.Color(196, 220, 172));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N

        txtLocationName.setBackground(new java.awt.Color(204, 255, 204));
        txtLocationName.setName("txtLocationName"); // NOI18N
        txtLocationName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLocationNameKeyReleased(evt);
            }
        });

        scrLocation.setName("scrLocation"); // NOI18N

        lstLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstLocation.setName("lstLocation"); // NOI18N
        lstLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        lstLocation.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstLocationValueChanged(evt);
            }
        });
        scrLocation.setViewportView(lstLocation);

        btnGPSForAll.setBackground(new java.awt.Color(153, 180, 115));
        btnGPSForAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPSForAll.setText("<html>Set one GPS point for all Observations</html>");
        btnGPSForAll.setToolTipText("The specified GPS point will be applied to all currently defined Observations.");
        btnGPSForAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPSForAll.setFocusPainted(false);
        btnGPSForAll.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPSForAll.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnGPSForAll.setName("btnGPSForAll"); // NOI18N
        btnGPSForAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSForAllActionPerformed(evt);
            }
        });

        chkIncludeSubfolders.setBackground(new java.awt.Color(153, 180, 115));
        chkIncludeSubfolders.setText("Include Subfolders");
        chkIncludeSubfolders.setToolTipText("Select this checkbox and press the Reload button to also look in subfolder for files to include in the Bulk Import.");
        chkIncludeSubfolders.setFocusable(false);
        chkIncludeSubfolders.setName("chkIncludeSubfolders"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Type:");
        jLabel5.setName("jLabel5"); // NOI18N

        cmbVisitType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbVisitType.setSelectedItem(VisitType.OTHER);
        cmbVisitType.setFocusable(false);
        cmbVisitType.setName("cmbVisitType"); // NOI18N

        jSeparator3.setBackground(new java.awt.Color(196, 220, 172));
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setName("jSeparator3"); // NOI18N

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(7, 7, 7)
                        .addComponent(txtLocationName))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addComponent(scrLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(lblLocationImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnGPSForAll, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addComponent(chkShowInactiveTimes))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jSeparator1)
                        .addGap(4, 4, 4))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(chkIncludeSubfolders))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addComponent(spnInactivityTime, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jLabel6))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(275, 275, 275)
                        .addComponent(jLabel7))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTopLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(txtVisitName)
                                .addGap(9, 9, 9)
                                .addComponent(jLabel5)
                                .addGap(10, 10, 10)
                                .addComponent(cmbVisitType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlTopLayout.createSequentialGroup()
                                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlTopLayout.createSequentialGroup()
                                        .addGap(80, 80, 80)
                                        .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlTopLayout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel2))
                                    .addGroup(pnlTopLayout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel3))
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 23, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLocationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(scrLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblLocationImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(btnGPSForAll, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(chkShowInactiveTimes))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(chkIncludeSubfolders))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(spnInactivityTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVisitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbVisitType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                "Observations", "Images"
            }
        ));
        tblBulkImport.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        tblBulkImport.getColumnModel().getColumn(0).setMinWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setPreferredWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setMaxWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setCellEditor(new InfoBoxEditor(app, txtLocationName, txtVisitName));
        tblBulkImport.getColumnModel().getColumn(0).setCellRenderer(new InfoBoxRenderer(app, txtLocationName, txtVisitName));
        tblBulkImport.getColumnModel().getColumn(1).setCellEditor(new ImageBoxEditor());
        tblBulkImport.getColumnModel().getColumn(1).setCellRenderer(new ImageBoxRenderer());

        add(scrTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lstLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstLocationValueChanged
        if (!lstLocation.getSelectionModel().isSelectionEmpty()) {
            String selectedName = lstLocation.getSelectedValue().toString();
            // Change the location name
            txtLocationName.setText(selectedName);
            // Cahnge the image
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(Location.WILDLOGFILE_ID_PREFIX + selectedName));
            if (fotos.size() > 0) {
                UtilsImageProcessing.setupFoto(Location.WILDLOGFILE_ID_PREFIX + selectedName, imageIndex, lblLocationImage, WildLogThumbnailSizes.SMALL, app);
            }
            else {
                lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
            }
        }
        else {
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
    }//GEN-LAST:event_lstLocationValueChanged

    private void txtLocationNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocationNameKeyReleased
        if (evt == null || !evt.isActionKey()) {
            for (int t = 0; t < lstLocation.getModel().getSize(); t++) {
                if (lstLocation.getModel().getElementAt(t).toString().equalsIgnoreCase(txtLocationName.getText())) {
                    lstLocation.setSelectedIndex(t);
                    lstLocation.scrollRectToVisible(lstLocation.getCellBounds(t, t));
                    break;
                }
                else {
                    lstLocation.getSelectionModel().clearSelection();
                }
            }
        }
    }//GEN-LAST:event_txtLocationNameKeyReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (!lstLocation.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile(Location.WILDLOGFILE_ID_PREFIX + lstLocation.getSelectedValue().toString(), imageIndex, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

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
                // Make sure the location is OK
                if (txtLocationName.getText() != null && !txtLocationName.getText().isEmpty()
                        && txtVisitName.getText() != null && !txtVisitName.getText().isEmpty()) {
                    // Make sure the visit is OK
                    final Visit visit = new Visit(txtVisitName.getText());
                    if (app.getDBI().find(visit) != null) {
                        UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                JOptionPane.showMessageDialog(app.getMainFrame(),
                                        "The Period name is not unique, please specify another one.",
                                        "Can't Save", JOptionPane.ERROR_MESSAGE);
                                return -1;
                            }
                        });
                        return null;
                    }
                    // Make sure all sightings have a creature set
                    final DefaultTableModel model = (DefaultTableModel)tblBulkImport.getModel();
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        if (sightingWrapper.getElementName() == null || sightingWrapper.getElementName().isEmpty()) {
                            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                @Override
                                public int showDialog() {
                                    JOptionPane.showMessageDialog(app.getMainFrame(),
                                            "Please assign a Creature to each of the Observations.",
                                            "Can't Save", JOptionPane.ERROR_MESSAGE);
                                    return -1;
                                }
                            });
                            return null;
                        }
                    }
                    // Show warning if any sighting doesn't have a GPS coordinate
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                        if (sightingWrapper.getLatitude() == null || Latitudes.NONE.equals(sightingWrapper.getLatitude())
                                || sightingWrapper.getLongitude() == null || Longitudes.NONE.equals(sightingWrapper.getLongitude())) {
                            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                @Override
                                public int showDialog() {
                                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "There are Observations without GPS Coordinates. Are you sure you want to save?",
                                            "Missing GPS Coordinates", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                }
                            });
                            if (result == JOptionPane.YES_OPTION) {
                                break;
                            }
                            else {
                                return null;
                            }
                        }
                    }
                    // Everything seems fine, start saving and close the tab to prevent new edits
                    this.setMessage("Saving the Bulk Import: Starting...");
                    this.setTaskProgress(0);
                    closeTab();
                    // Process the Location
                    Location location = app.getDBI().find(new Location(txtLocationName.getText()));
                    if (location == null) {
                        location = new Location(txtLocationName.getText());
                        app.getDBI().createOrUpdate(location, null);
                    }
                    // Process the Visit
                    visit.setLocationName(location.getName());
                    visit.setStartDate(dtpStartDate.getDate());
                    visit.setEndDate(dtpEndDate.getDate());
                    visit.setType((VisitType)cmbVisitType.getSelectedItem());
                    app.getDBI().createOrUpdate(visit, null);
                    // Processs the sightings
                    final Location locationHandle = location;
                    final Object saveElementLock = new Object();
                    final Object saveSightingLock = new Object();
                    ExecutorService executorService = Executors.newFixedThreadPool(app.getThreadCount());
                    for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                        final int counter = rowCount;
                        final ProgressbarTask progressbarHandle = this;
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(counter, 0);
                                // Check wether the Creature exists or not
                                synchronized (saveElementLock) {
                                    if (app.getDBI().find(new Element(sightingWrapper.getElementName())) == null) {
                                            app.getDBI().createOrUpdate(new Element(sightingWrapper.getElementName()), null);
                                    }
                                }
                                // Continue processing the Sighting
                                sightingWrapper.setLocationName(locationHandle.getName());
                                sightingWrapper.setVisitName(visit.getName());
                                // Makje sure the default moonphase is set to -1 (for when the code below doesn't overwrite it)
                                sightingWrapper.setMoonPhase(-1);
                                // If the sighting's GPS point is set then try to calculate Sun and Moon
                                if (sightingWrapper.getDate() != null
                                        && sightingWrapper.getLatitude() != null && !sightingWrapper.getLatitude().equals(Latitudes.NONE)
                                        && sightingWrapper.getLongitude() != null && !sightingWrapper.getLongitude().equals(Longitudes.NONE)) {
                                    // Sun
                                    double latitude = UtilsGps.getDecimalDegree(sightingWrapper.getLatitude(), sightingWrapper.getLatDegrees(), sightingWrapper.getLatMinutes(), sightingWrapper.getLatSeconds());
                                    double longitude = UtilsGps.getDecimalDegree(sightingWrapper.getLongitude(), sightingWrapper.getLonDegrees(), sightingWrapper.getLonMinutes(), sightingWrapper.getLonSeconds());
                                    sightingWrapper.setTimeOfDay(AstroCalculator.getSunCategory(sightingWrapper.getDate(), latitude, longitude));
                                    // Moon
                                    sightingWrapper.setMoonPhase(AstroCalculator.getMoonPhase(sightingWrapper.getDate()));
                                    sightingWrapper.setMoonlight(AstroCalculator.getMoonlight(sightingWrapper.getDate(), latitude, longitude));
                                }
                                // Get a list of all the images
                                BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)model.getValueAt(counter, 1);
                                // Determine the duration and build up the a list of File objects (to save later)
                                Date startDate = listWrapper.getImageList().get(0).getDate();
                                Date endDate = listWrapper.getImageList().get(listWrapper.getImageList().size()-1).getDate();
                                List<File> files = new ArrayList<File>(listWrapper.getImageList().size());
                                for (BulkUploadImageFileWrapper imageWrapper : listWrapper.getImageList()) {
                                    files.add(imageWrapper.getFile().toFile());
                                    if (imageWrapper.getDate().getTime() < startDate.getTime()) {
                                        startDate = imageWrapper.getDate();
                                    }
                                    else
                                    if (imageWrapper.getDate().getTime() > endDate.getTime()) {
                                        endDate = imageWrapper.getDate();
                                    }
                                }
                                if (sightingWrapper.getDurationMinutes() == 0 && sightingWrapper.getDurationSeconds() == 0.0) {
                                    double difference = (endDate.getTime() - startDate.getTime())/1000;
                                    int minutes = (int)difference/60;
                                    double seconds = difference - minutes*60.0;
                                    sightingWrapper.setDurationMinutes(minutes);
                                    sightingWrapper.setDurationSeconds(seconds);
                                }
                                // Save the sigting
                                synchronized (saveSightingLock) {
                                    app.getDBI().createOrUpdate(sightingWrapper);
                                }
                                // Save the corresponding images
                                UtilsFileProcessing.performFileUpload(
                                        sightingWrapper.getWildLogFileID(),
                                        WildLogPaths.WildLogPathPrefixes.PREFIX_SIGHTING.toPath().resolve(sightingWrapper.toPath()),
                                        files.toArray(new File[files.size()]),
                                        null, WildLogThumbnailSizes.NORMAL,
                                        app);
                                // Update the progress
                                try {
                                    progressbarHandle.setTaskProgress(counter, 0, model.getRowCount());
                                }
                                catch (Exception e) {
                                    e.printStackTrace(System.out);
                                }
                            }
                        });
                    }
                    if (!UtilsConcurency.waitForExecutorToShutdown(executorService)) {
                        UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                JOptionPane.showMessageDialog(app.getMainFrame(),
                                        "There was an unexpected problem while saving.",
                                        "Problem Saving", JOptionPane.ERROR_MESSAGE);
                                return -1;
                            }
                        });
                    }
                    // Saving is done, now open the visits's tab
                    this.setMessage("Saving the Bulk Import: Finished");
                    this.setTaskProgress(100);
                    UtilPanelGenerator.openPanelAsTab(app, visit.getName(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)thisParentHandle, location);
                }
                else {
                    UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            JOptionPane.showMessageDialog(app.getMainFrame(),
                                    "Please provide a Place name and Period name before saving.",
                                    "Can't Save", JOptionPane.ERROR_MESSAGE);
                            return -1;
                        }
                    });
                }
                return null;
            }
        });
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Reload the locations as they might have changed
        getLocationList();
        txtLocationNameKeyReleased(null);
        // Re-check all species images already assigned since some might have changed
        DefaultTableModel model = ((DefaultTableModel)tblBulkImport.getModel());
        for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
            BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
            if (sightingWrapper.getElementName() != null && !sightingWrapper.getElementName().isEmpty()) {
                List<Element> elements = app.getDBI().list(new Element());
                boolean foundElement = false;
                for (Element element : elements) {
                    if (sightingWrapper.getElementName().equalsIgnoreCase(element.getPrimaryName())) {
                        JLabel tempLabel = new JLabel();
                        UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, tempLabel, WildLogThumbnailSizes.MEDIUM_SMALL, app);
                        sightingWrapper.setIcon(tempLabel.getIcon());
                        foundElement = true;
                        break;
                    }
                }
                if (!foundElement) {
                    sightingWrapper.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
                }
            }
        }
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnGPSForAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSForAllActionPerformed
        Sighting tempSighting = new Sighting();
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
            }
        }
    }//GEN-LAST:event_btnGPSForAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGPSForAll;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chkIncludeSubfolders;
    private javax.swing.JCheckBox chkShowInactiveTimes;
    private javax.swing.JComboBox cmbVisitType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JList lstLocation;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane scrLocation;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JSpinner spnInactivityTime;
    private javax.swing.JTable tblBulkImport;
    private javax.swing.JTextField txtLocationName;
    private javax.swing.JTextField txtVisitName;
    // End of variables declaration//GEN-END:variables
}
