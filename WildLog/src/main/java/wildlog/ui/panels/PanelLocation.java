package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.data.utils.UtilsData;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.dialogs.SunMoonDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;


public class PanelLocation extends PanelCanSetupHeader {
    private final WildLogApp app;
    private int imageIndex;
    private Location locationWL; // Note: "location" is already used in this component... Have problem with getLocation()...
    private Location lastSavedLocation;
    private boolean isPopup = false;
    private PanelNeedsRefreshWhenDataChanges panelToRefresh;

    
    public PanelLocation(WildLogApp inApp, Location inLocation, boolean inIsPopup, PanelNeedsRefreshWhenDataChanges inPanelToRefresh) {
        app = inApp;
        locationWL = inLocation;
        isPopup = inIsPopup;
        panelToRefresh = inPanelToRefresh;
        lastSavedLocation = locationWL.cloneShallow();
        setupUI();
        if (inIsPopup) {
            btnAddVisit.setEnabled(false);
            btnBrowse.setEnabled(false);
            btnBulkImport.setEnabled(false);
            btnDeleteImage.setEnabled(false);
            btnDeleteVisit.setEnabled(false);
            btnGoElement.setEnabled(false);
            btnGoVisit.setEnabled(false);
            btnExport.setEnabled(false);
            btnMap.setEnabled(false);
            btnNextImage.setEnabled(false);
            btnPreviousImage.setEnabled(false);
            btnReport.setEnabled(false);
            btnSetMainImage.setEnabled(false);
            btnSlideshow.setEnabled(false);
            btnSunAndMoon.setEnabled(false);
            btnUploadImage.setEnabled(false);
            rdbLocation.setEnabled(false);
            rdbVisit.setEnabled(false);
        }
    }

    public PanelLocation(WildLogApp inApp, Location inLocation) {
        app = inApp;
        locationWL = inLocation;
        lastSavedLocation = locationWL.cloneShallow();
        setupUI();
        // Setup the drag and drop on the buttons
        FileDrop.SetupFileDrop(btnBulkImport, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() > 0) {
                    final List<Path> lstSelectedPaths = UtilsFileProcessing.getPathsFromSelectedFile(inFiles.toArray(new File[inFiles.size()]));
                    if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
                        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                            @Override
                            protected Object doInBackground() throws Exception {
                                UtilsPanelGenerator.openBulkUploadTab(
                                        new BulkUploadPanel(app, this, locationWL, null, lstSelectedPaths, null), 
                                        (JTabbedPane)getParent());
                                return null;
                            }
                        });
                    }
                }
            }
        });
    }

    private void setupUI() {
        initComponents();
        // Setup images
        imageIndex = 0;
        int fotoCount = app.getDBI().countWildLogFiles(null, locationWL.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
        setupNumberOfImages();
        // Setup the tables
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);
        UtilsTableGenerator.setupColumnResizingListener(tblVisit, 1);
        // setup the file dropping
        if (!isPopup) {
            FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
                @Override
                public void filesDropped(List<File> inFiles) {
                    btnUpdateActionPerformed(null);
                    if (!txtName.getBackground().equals(Color.RED)) {
                        uploadFiles(inFiles);
                    }
                }
            });
        }
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtName);
        UtilsUI.attachClipboardPopup(txtHabitatType);
        UtilsUI.attachClipboardPopup(txtDescription);
        // Setup info for tab headers
        tabTitle = locationWL.getName();
        tabID = locationWL.getID();
        tabIconURL = app.getClass().getResource("resources/icons/Location.gif");
        // Scroll to the top of the text areas
        txtHabitatType.setCaretPosition(0);
        txtDescription.setCaretPosition(0);
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            btnDeleteVisit.setEnabled(false);
            btnDeleteVisit.setVisible(false);
            pnlSubButtons2.setVisible(false);
            btnExport.setEnabled(false);
            btnExport.setVisible(false);
            btnSlideshow.setEnabled(false);
            btnSlideshow.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                btnDeleteImage.setEnabled(false);
                btnDeleteImage.setVisible(false);
                btnGPS.setEnabled(false);
                btnGPS.setVisible(false);
                pnlSubButtons1.setVisible(false);
                btnReport.setEnabled(false);
                btnReport.setVisible(false);
            }
        }
    }

    private void uploadFiles(List<File> inFiles) {
        UtilsFileProcessing.performFileUpload(locationWL,
                Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(locationWL.getName()),
                inFiles.toArray(new File[inFiles.size()]),
                new Runnable() {
                    @Override
                    public void run() {
                        imageIndex = 0;
                        UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
                        setupNumberOfImages();
                        // everything went well - saving
                        btnUpdateActionPerformed(null);
                    }
                }, 
                app, true, null, true, true);
    }

    public void setLocationWL(Location inLocation) {
        locationWL = inLocation;
    }

    public Location getLocationWL() {
        return locationWL;
    }

    @Override
    public boolean closeTab() {
        btnUpdate.requestFocus();
        populateLocationFromUI();
        if (lastSavedLocation.hasTheSameContent(locationWL)) {
            ((JTabbedPane)getParent()).remove(this);
             return true;
        }
        else {
            String name = locationWL.getName();
            if (name ==null || name.isEmpty()) {
                name = "<New Place>";
            }
            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                    "Save before closing this tab for " + name + "?", 
                    "You have unsaved data", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                btnUpdateActionPerformed(null);
                if (locationWL.getName().trim().length() > 0 && UtilsData.checkCharacters(locationWL.getName().trim())) {
                    // Do the save action without closing the tab to show the error message
                    ((JTabbedPane)getParent()).remove(this);
                     return true;
                }
            }
            else
            if (result == JOptionPane.NO_OPTION) {
                ((JTabbedPane)getParent()).remove(this);
                 return true;
            }
        }
         return false;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        locationIncludes = new javax.swing.JPanel();
        lblLocation = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        pnlFiles = new javax.swing.JPanel();
        btnSetMainImage = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        btnUploadImage = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        btnNextImage = new javax.swing.JButton();
        btnPreviousImage = new javax.swing.JButton();
        btnDeleteImage = new javax.swing.JButton();
        pnlInfo = new javax.swing.JPanel();
        btnGPS = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        cmbRating = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        cmbGameRating = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        txtLongitude = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JTextField();
        jScrollPane13 = new javax.swing.JScrollPane();
        txtHabitatType = new javax.swing.JTextArea();
        pnlButtons = new javax.swing.JPanel();
        btnMap = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        btnBulkImport = new javax.swing.JButton();
        btnSunAndMoon = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        pnlSubButtons1 = new javax.swing.JPanel();
        btnReport = new javax.swing.JButton();
        pnlSubButtons2 = new javax.swing.JPanel();
        btnSlideshow = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        pnlVisit = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        btnDeleteVisit = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        btnGoVisit = new javax.swing.JButton();
        btnAddVisit = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        lblNumberOfVisits = new javax.swing.JLabel();
        pnlTables = new javax.swing.JPanel();
        rdbVisit = new javax.swing.JRadioButton();
        rdbLocation = new javax.swing.JRadioButton();
        jLabel44 = new javax.swing.JLabel();
        btnGoElement = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        lblNumberOfElements = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(233, 239, 244));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName(locationWL.getName());
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        locationIncludes.setBackground(new java.awt.Color(233, 239, 244));
        locationIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        locationIncludes.setName("locationIncludes"); // NOI18N
        locationIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));

        lblLocation.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocation.setText(locationWL.getName());
        lblLocation.setName("lblLocation"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Observations:");
        jLabel7.setName("jLabel7"); // NOI18N

        lblNumberOfSightings.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N

        pnlFiles.setBackground(new java.awt.Color(233, 239, 244));
        pnlFiles.setName("pnlFiles"); // NOI18N

        btnSetMainImage.setBackground(new java.awt.Color(233, 239, 244));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnSetMainImage.setText("Set as First");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Place.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setIconTextGap(2);
        btnSetMainImage.setMargin(new java.awt.Insets(2, 1, 2, 1));
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        btnUploadImage.setBackground(new java.awt.Color(233, 239, 244));
        btnUploadImage.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("<html><u>Upload Files</u></html>");
        btnUploadImage.setToolTipText("<html>Upload a file for this Place. <br/>You can also drag-and-drop files onto the above box to upload it. <br/>(Note: Drag-and-drop only works on supported platforms.)</html>");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setIconTextGap(2);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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

        btnNextImage.setBackground(new java.awt.Color(233, 239, 244));
        btnNextImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImage.setToolTipText("Load the next file.");
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });

        btnPreviousImage.setBackground(new java.awt.Color(233, 239, 244));
        btnPreviousImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImage.setToolTipText("Load the previous file.");
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });

        btnDeleteImage.setBackground(new java.awt.Color(233, 239, 244));
        btnDeleteImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteImage.setText("Delete File");
        btnDeleteImage.setToolTipText("Delete the current file.");
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setIconTextGap(2);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilesLayout = new javax.swing.GroupLayout(pnlFiles);
        pnlFiles.setLayout(pnlFilesLayout);
        pnlFilesLayout.setHorizontalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlFilesLayout.createSequentialGroup()
                                .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        pnlInfo.setBackground(new java.awt.Color(233, 239, 244));
        pnlInfo.setName("pnlInfo"); // NOI18N

        btnGPS.setBackground(new java.awt.Color(233, 239, 244));
        btnGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPS.setText("Change GPS");
        btnGPS.setToolTipText("Select a new GPS value for this Place.");
        btnGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPS.setFocusPainted(false);
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });

        jLabel41.setText("General Rating:");
        jLabel41.setName("jLabel41"); // NOI18N

        cmbRating.setModel(new DefaultComboBoxModel(LocationRating.values()));
        cmbRating.setSelectedItem(locationWL.getRating());
        cmbRating.setFocusable(false);
        cmbRating.setName("cmbRating"); // NOI18N

        jLabel42.setText("Wildlife Rating:");
        jLabel42.setName("jLabel42"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(locationWL.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane10.setViewportView(txtDescription);

        cmbGameRating.setModel(new DefaultComboBoxModel(GameViewRating.values()));
        cmbGameRating.setSelectedItem(locationWL.getGameViewingRating());
        cmbGameRating.setFocusable(false);
        cmbGameRating.setName("cmbGameRating"); // NOI18N

        jLabel48.setText("Place Name:");
        jLabel48.setName("jLabel48"); // NOI18N

        txtLongitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLongitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N

        jLabel10.setText("GPS:");
        jLabel10.setName("jLabel10"); // NOI18N

        txtName.setBackground(new java.awt.Color(204, 255, 204));
        txtName.setText(locationWL.getName());
        txtName.setName("txtName"); // NOI18N

        jLabel36.setText("Description:");
        jLabel36.setName("jLabel36"); // NOI18N

        jLabel40.setText("Habitat Type:");
        jLabel40.setName("jLabel40"); // NOI18N

        txtLatitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLatitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        txtHabitatType.setColumns(20);
        txtHabitatType.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtHabitatType.setLineWrap(true);
        txtHabitatType.setRows(5);
        txtHabitatType.setText(locationWL.getHabitatType());
        txtHabitatType.setWrapStyleWord(true);
        txtHabitatType.setName("txtHabitatType"); // NOI18N
        jScrollPane13.setViewportView(txtHabitatType);

        javax.swing.GroupLayout pnlInfoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(txtName))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jScrollPane10))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jScrollPane13)))
                        .addGap(5, 5, 5))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(btnGPS)
                        .addGap(10, 10, 10)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbGameRating, 0, 100, Short.MAX_VALUE)
                            .addComponent(cmbRating, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(43, Short.MAX_VALUE))))
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtName)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtLatitude, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlInfoLayout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel42)
                                    .addComponent(cmbGameRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(11, 11, 11)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(108, 108, 108))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jScrollPane13)
                                .addGap(5, 5, 5))))))
        );

        pnlButtons.setBackground(new java.awt.Color(233, 239, 244));
        pnlButtons.setMaximumSize(new java.awt.Dimension(120, 345));
        pnlButtons.setMinimumSize(new java.awt.Dimension(120, 345));
        pnlButtons.setName("pnlButtons"); // NOI18N
        pnlButtons.setPreferredSize(new java.awt.Dimension(120, 345));

        btnMap.setBackground(new java.awt.Color(233, 239, 244));
        btnMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMap.setText("Maps");
        btnMap.setToolTipText("Show maps for this Place.");
        btnMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMap.setFocusPainted(false);
        btnMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMap.setMargin(new java.awt.Insets(2, 6, 2, 8));
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });

        btnBrowse.setBackground(new java.awt.Color(233, 239, 244));
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.setToolTipText("Open the Browse tab and automatically select this Place in the tree.");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.setFocusPainted(false);
        btnBrowse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrowse.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        btnBulkImport.setBackground(new java.awt.Color(233, 239, 244));
        btnBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Bulk Import.png"))); // NOI18N
        btnBulkImport.setText("<html><u>Bulk Import</u></html>");
        btnBulkImport.setToolTipText("Open a Bulk Import tab for this Place. You can drag-and-drop a folder on the button to quickly start the Bulk Import process.");
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setFocusPainted(false);
        btnBulkImport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBulkImport.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });

        btnSunAndMoon.setBackground(new java.awt.Color(233, 239, 244));
        btnSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon_big.png"))); // NOI18N
        btnSunAndMoon.setText("Sun / Moon");
        btnSunAndMoon.setToolTipText("Show the Sun and Moon information for this Place.");
        btnSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSunAndMoon.setFocusPainted(false);
        btnSunAndMoon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSunAndMoon.setMargin(new java.awt.Insets(2, 6, 2, 2));
        btnSunAndMoon.setName("btnSunAndMoon"); // NOI18N
        btnSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSunAndMoonActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 204, 51));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdate.setToolTipText("Save and update the Place.");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        pnlSubButtons1.setBackground(new java.awt.Color(233, 239, 244));
        pnlSubButtons1.setName("pnlSubButtons1"); // NOI18N

        btnReport.setBackground(new java.awt.Color(233, 239, 244));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.png"))); // NOI18N
        btnReport.setText("Charts");
        btnReport.setToolTipText("View charts for this Place.");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setFocusPainted(false);
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 6, 2, 4));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSubButtons1Layout = new javax.swing.GroupLayout(pnlSubButtons1);
        pnlSubButtons1.setLayout(pnlSubButtons1Layout);
        pnlSubButtons1Layout.setHorizontalGroup(
            pnlSubButtons1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubButtons1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlSubButtons1Layout.setVerticalGroup(
            pnlSubButtons1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubButtons1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pnlSubButtons2.setBackground(new java.awt.Color(233, 239, 244));
        pnlSubButtons2.setMaximumSize(new java.awt.Dimension(110, 85));
        pnlSubButtons2.setName("pnlSubButtons2"); // NOI18N
        pnlSubButtons2.setPreferredSize(new java.awt.Dimension(110, 80));

        btnSlideshow.setBackground(new java.awt.Color(233, 239, 244));
        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images for this Place.");
        btnSlideshow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshow.setFocusPainted(false);
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 6, 2, 8));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(233, 239, 244));
        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        btnExport.setText("Export");
        btnExport.setToolTipText("Show available exports for this Place.");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.setFocusPainted(false);
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExport.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnExport.setName("btnExport"); // NOI18N
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSubButtons2Layout = new javax.swing.GroupLayout(pnlSubButtons2);
        pnlSubButtons2.setLayout(pnlSubButtons2Layout);
        pnlSubButtons2Layout.setHorizontalGroup(
            pnlSubButtons2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubButtons2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlSubButtons2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        pnlSubButtons2Layout.setVerticalGroup(
            pnlSubButtons2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubButtons2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSubButtons2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSunAndMoon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnMap, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(btnBulkImport, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(pnlSubButtons1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlSubButtons1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(pnlSubButtons2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnSunAndMoon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        pnlVisit.setBackground(new java.awt.Color(233, 239, 244));
        pnlVisit.setName("pnlVisit"); // NOI18N

        jSeparator7.setForeground(new java.awt.Color(134, 134, 134));
        jSeparator7.setName("jSeparator7"); // NOI18N

        btnDeleteVisit.setBackground(new java.awt.Color(233, 239, 244));
        btnDeleteVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteVisit.setToolTipText("<html>Delete the selected Period. <br/>This will delete all linked Observations and files as well.</html>");
        btnDeleteVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteVisit.setFocusPainted(false);
        btnDeleteVisit.setName("btnDeleteVisit"); // NOI18N
        btnDeleteVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVisitActionPerformed(evt);
            }
        });

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblVisitKeyReleased(evt);
            }
        });
        jScrollPane12.setViewportView(tblVisit);

        btnGoVisit.setBackground(new java.awt.Color(233, 239, 244));
        btnGoVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoVisit.setToolTipText("Open a tab for the selected Period.");
        btnGoVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoVisit.setFocusPainted(false);
        btnGoVisit.setName("btnGoVisit"); // NOI18N
        btnGoVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisitActionPerformed(evt);
            }
        });

        btnAddVisit.setBackground(new java.awt.Color(233, 239, 244));
        btnAddVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddVisit.setToolTipText("Open a tab to add a new Period.");
        btnAddVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddVisit.setFocusPainted(false);
        btnAddVisit.setName("btnAddVisit"); // NOI18N
        btnAddVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVisitActionPerformed(evt);
            }
        });

        jLabel45.setText("Periods:");
        jLabel45.setName("jLabel45"); // NOI18N

        lblNumberOfVisits.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfVisits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfVisits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N

        javax.swing.GroupLayout pnlVisitLayout = new javax.swing.GroupLayout(pnlVisit);
        pnlVisit.setLayout(pnlVisitLayout);
        pnlVisitLayout.setHorizontalGroup(
            pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVisitLayout.createSequentialGroup()
                .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVisitLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnGoVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnAddVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnDeleteVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlVisitLayout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblNumberOfVisits, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE))))
                .addGap(5, 5, 5))
        );
        pnlVisitLayout.setVerticalGroup(
            pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVisitLayout.createSequentialGroup()
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfVisits, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(pnlVisitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVisitLayout.createSequentialGroup()
                        .addComponent(btnGoVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnAddVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnDeleteVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        pnlTables.setBackground(new java.awt.Color(233, 239, 244));
        pnlTables.setName("pnlTables"); // NOI18N

        rdbVisit.setBackground(new java.awt.Color(233, 239, 244));
        buttonGroup1.add(rdbVisit);
        rdbVisit.setText("Period");
        rdbVisit.setToolTipText("View all Creatures observed during the selected Period.");
        rdbVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbVisit.setFocusPainted(false);
        rdbVisit.setName("rdbVisit"); // NOI18N

        rdbLocation.setBackground(new java.awt.Color(233, 239, 244));
        buttonGroup1.add(rdbLocation);
        rdbLocation.setSelected(true);
        rdbLocation.setText("Place");
        rdbLocation.setToolTipText("View all Creatures observed at this Place.");
        rdbLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocation.setFocusPainted(false);
        rdbLocation.setName("rdbLocation"); // NOI18N
        rdbLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbLocationItemStateChanged(evt);
            }
        });

        jLabel44.setText("Creatures:");
        jLabel44.setName("jLabel44"); // NOI18N

        btnGoElement.setBackground(new java.awt.Color(233, 239, 244));
        btnGoElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoElement.setToolTipText("Open a tab for the selected Creature.");
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setFocusPainted(false);
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
        });
        jScrollPane11.setViewportView(tblElement);

        lblNumberOfElements.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N

        javax.swing.GroupLayout pnlTablesLayout = new javax.swing.GroupLayout(pnlTables);
        pnlTables.setLayout(pnlTablesLayout);
        pnlTablesLayout.setHorizontalGroup(
            pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablesLayout.createSequentialGroup()
                .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlTablesLayout.createSequentialGroup()
                        .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(rdbLocation)
                        .addGap(5, 5, 5)
                        .addComponent(rdbVisit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel44)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 1, Short.MAX_VALUE))
        );
        pnlTablesLayout.setVerticalGroup(
            pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel44)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdbLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rdbVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        jSeparator2.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator2.setName("jSeparator2"); // NOI18N

        javax.swing.GroupLayout locationIncludesLayout = new javax.swing.GroupLayout(locationIncludes);
        locationIncludes.setLayout(locationIncludesLayout);
        locationIncludesLayout.setHorizontalGroup(
            locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addComponent(lblLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1))
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator2)))
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        locationIncludesLayout.setVerticalGroup(
            locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(pnlTables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addComponent(pnlVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        add(locationIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtLatitude.setText(UtilsGPS.getLatitudeString(locationWL));
        txtLongitude.setText(UtilsGPS.getLongitudeString(locationWL));
        if (locationWL.getName() != null) {
            lblNumberOfSightings.setText(Integer.toString(app.getDBI().countSightings(0, 0, locationWL.getID(), 0)));
            lblNumberOfVisits.setText(Integer.toString(app.getDBI().countVisits(null, locationWL.getID())));
            if (rdbLocation.isSelected()) {
                UtilsTableGenerator.setupElementsTableMediumForLocation(app, tblElement, locationWL.getID());
            }
            // Note: If the visit radio button was selected, then setting up the visit table below will create the mouse event
            UtilsTableGenerator.setupVisitTableLarge(app, tblVisit, locationWL.getID());
        }
        else {
            lblNumberOfSightings.setText("0");
            lblNumberOfVisits.setText("0");
        }
        int fotoCount = app.getDBI().countWildLogFiles(null, locationWL.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
        setupNumberOfImages();
        // Wait for the table to finish loading
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
            }
        });
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkImportActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsPanelGenerator.openBulkUploadTab(new BulkUploadPanel(app, this, locationWL, null, null, null), (JTabbedPane)getParent());
                    return null;
                }
            });
        }
    }//GEN-LAST:event_btnBulkImportActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            SlideshowDialog dialog = new SlideshowDialog(app, null, locationWL, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog;
        if (!isPopup) {
            dialog = new GPSDialog(app, app.getMainFrame(), locationWL);
        }
        else {
            dialog = new GPSDialog(app, (JDialog) this.getParent().getParent().getParent().getParent(), locationWL);
        }
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(UtilsGPS.getLatitudeString(locationWL));
            txtLongitude.setText(UtilsGPS.getLongitudeString(locationWL));
            btnUpdateActionPerformed(null);
        }
        btnUpdate.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void btnSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSunAndMoonActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            SunMoonDialog dialog = new SunMoonDialog(app, locationWL);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSunAndMoonActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            ExportDialog dialog = new ExportDialog(app, locationWL, null, null, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Charts - " + locationWL.getName(), 
                    app.getDBI().listSightings(0, locationWL.getID(), 0, true, Sighting.class));
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void rdbLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbLocationItemStateChanged
        lblNumberOfElements.setText("0");
        if (locationWL.getName() != null) {
            if (evt != null) {
                tblElement.clearSelection();
            }
            if (rdbLocation.isSelected()) {
                UtilsTableGenerator.setupElementsTableMediumForLocation(app, tblElement, locationWL.getID());
            }
            else {
                if  (tblVisit.getSelectedRowCount() == 1) {
                    UtilsTableGenerator.setupElementsTableMediumForVisit(app, tblElement, (Long) tblVisit.getModel().getValueAt(
                            tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 7));
                }
                else {
                    if (tblVisit.getSelectedRowCount() == 0) {
                        tblElement.setModel(new DefaultTableModel(new String[]{"Please Selected a Period"}, 0));
                    }
                    else {
                        tblElement.setModel(new DefaultTableModel(new String[]{"More Than One Period Selected"}, 0));
                    }
                }
            }
            // Wait for the table to finish loading
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
                }
            });
        }
    }//GEN-LAST:event_rdbLocationItemStateChanged

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + locationWL.getDisplayName(), 
                    app.getDBI().listSightings(0, locationWL.getID(), 0, true, Sighting.class), 0);;
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                && WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
            return;
        }
        imageIndex = UtilsImageProcessing.removeImage(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(locationWL.getWildLogFileID(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (!isPopup) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int[] selectedRows = tblElement.getSelectedRows();
            for (int t = 0; t < selectedRows.length; t++) {
                UtilsPanelGenerator.openPanelAsTab(app, (Long) tblElement.getModel().getValueAt(
                        tblElement.convertRowIndexToModel(selectedRows[t]), 4), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent(), null);
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnDeleteVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVisitActionPerformed
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            return;
        }
        if (tblVisit.getSelectedRowCount() > 0) {
            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                    "Are you sure you want to delete the Period(s)? This will delete all Observations and files linked to the Period(s) as well.",
                    "Delete Period(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblVisit.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    long visitID = (Long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(selectedRows[t]), 7);
                    UtilsPanelGenerator.removeOpenedTab(visitID, PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent());
                    app.getDBI().deleteVisit(visitID);
                }
                formComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteVisitActionPerformed

    private void btnAddVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVisitActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent(), locationWL);
        }
    }//GEN-LAST:event_btnAddVisitActionPerformed

    private void btnGoVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisitActionPerformed
        if (!isPopup) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int[] selectedRows = tblVisit.getSelectedRows();
            for (int t = 0; t < selectedRows.length; t++) {
                UtilsPanelGenerator.openPanelAsTab(app, (Long) tblVisit.getModel().getValueAt(
                        tblVisit.convertRowIndexToModel(selectedRows[t]), 7), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent(), locationWL);
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoVisitActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            List<File> files = UtilsFileProcessing.showFileUploadDialog(app, app.getMainFrame());
            uploadFiles(files);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(locationWL.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(locationWL.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[PanelLocation-Save]");
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = lastSavedLocation.getName();
                populateLocationFromUI();
                // Save the location
                boolean result;
                if (oldName == null || oldName.isEmpty()) {
                    result = app.getDBI().createLocation(locationWL, false);
                }
                else {
                    result = app.getDBI().updateLocation(locationWL, oldName);
                }
                if (result == true) {
                    txtName.setBackground(new java.awt.Color(204, 255, 204));
                    txtName.setText(locationWL.getName());
                    lastSavedLocation = locationWL.cloneShallow();
                    if (app.getWildLogOptions().isEnableSounds()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
                else {
                    txtName.setBackground(Color.RED);
                    locationWL.setName(oldName);
                    txtName.setText(oldName);
                    WLOptionPane.showMessageDialog(this, 
                            "The Place could not be saved.", 
                            "Not Saved!", JOptionPane.ERROR_MESSAGE);
                }
                lblLocation.setText(locationWL.getName());
                tabTitle = locationWL.getName();
                tabID = locationWL.getID();
                if (!isPopup) {
                    setupTabHeader(PanelCanSetupHeader.TabTypes.LOCATION);
                }
                else {
                    Component component = getParent();
                    while (!(component instanceof JDialog)) {
                        component = component.getParent();
                    }
                    ((JDialog)component).dispose();
                    panelToRefresh.doTheRefresh(this);
                }
            }
            else {
                txtName.setBackground(Color.RED);
                WLOptionPane.showMessageDialog(this, 
                        "Please provide a Place Name before trying to save.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            txtName.setBackground(Color.RED);
            WLOptionPane.showMessageDialog(this, 
                    "The Place Name contains unsupported characters and could not be saved.", 
                    "Not Saved!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void populateLocationFromUI() {
        locationWL.setName(UtilsData.limitLength(txtName.getText(), 100));
        locationWL.setHabitatType(txtHabitatType.getText());
        locationWL.setDescription(txtDescription.getText());
        locationWL.setRating((LocationRating)cmbRating.getSelectedItem());
        locationWL.setGameViewingRating((GameViewRating)cmbGameRating.getSelectedItem());
        // NOTE: The GPS info is already set on the Location object by the GPS popup component
    }

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoElementActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblVisitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblVisitMouseReleased(null);
        }
    }//GEN-LAST:event_tblVisitKeyReleased

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoVisitActionPerformed(null);
            evt.consume();
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnDeleteVisitActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoVisitActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (rdbVisit.isSelected()) {
            if (!(evt instanceof UtilsUI.GeneratedMouseEvent)) {
                tblElement.clearSelection();
            }
            rdbLocationItemStateChanged(null);
        }
    }//GEN-LAST:event_tblVisitMouseReleased

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            app.getMainFrame().browseSelectedLocation(locationWL);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void setupNumberOfImages() {
        int fotoCount = app.getDBI().countWildLogFiles(null, locationWL.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        }
        else {
            lblNumberOfImages.setText("0 of 0");
        }
        lblNumberOfImages.setToolTipText(lblNumberOfImages.getText());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVisit;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteVisit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoVisit;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnSlideshow;
    private javax.swing.JButton btnSunAndMoon;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbGameRating;
    private javax.swing.JComboBox cmbRating;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JPanel locationIncludes;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlSubButtons1;
    private javax.swing.JPanel pnlSubButtons2;
    private javax.swing.JPanel pnlTables;
    private javax.swing.JPanel pnlVisit;
    private javax.swing.JRadioButton rdbLocation;
    private javax.swing.JRadioButton rdbVisit;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtHabitatType;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables

}
