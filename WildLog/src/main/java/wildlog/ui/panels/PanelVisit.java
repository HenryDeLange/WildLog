package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.data.enums.system.WildLogUserTypes;
import wildlog.data.utils.UtilsData;
import wildlog.ui.dialogs.AdvancedDialog;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.dialogs.ExtraDataDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;


public class PanelVisit extends PanelCanSetupHeader implements PanelNeedsRefreshWhenDataChanges {
    private final WildLogApp app;
    private int imageIndex;
    private Visit visit;
    private Visit lastSavedVisit;
    private Location locationForVisit;
    private Sighting sighting;
    private int imageSightingIndex;
    private boolean isPopup = false;
    private PanelNeedsRefreshWhenDataChanges panelToRefresh;

    
    public PanelVisit(WildLogApp inApp, Location inLocation, Visit inVisit, boolean inIsPopup, PanelNeedsRefreshWhenDataChanges inPanelToRefresh) {
        app = inApp;
        locationForVisit = inLocation;
        visit = inVisit;
        isPopup = inIsPopup;
        panelToRefresh = inPanelToRefresh;
        lastSavedVisit = visit.cloneShallow();
        sighting = new Sighting();
        setupUI();
        if (inIsPopup) {
            btnAddSighting.setEnabled(false);
            btnAdvanced.setEnabled(false);
            btnBrowse.setEnabled(false);
            btnExtraData.setEnabled(false);
            btnBulkImport.setEnabled(false);
            btnDeleteImage.setEnabled(false);
            btnDeleteSighting.setEnabled(false);
            btnEditSighting.setEnabled(false);
            btnGoElement.setEnabled(false);
            btnExport.setEnabled(false);
            btnMapSighting.setEnabled(false);
            btnNextImage.setEnabled(false);
            btnNextImageSighting.setEnabled(false);
            btnPreviousImage.setEnabled(false);
            btnPreviousImageSighting.setEnabled(false);
            btnReport.setEnabled(false);
            btnSetMainImage.setEnabled(false);
            btnSlideshow.setEnabled(false);
            btnUploadImage.setEnabled(false);
        }
    }

    public PanelVisit(WildLogApp inApp, Location inLocation, Visit inVisit) {
        app = inApp;
        locationForVisit = inLocation;
        visit = inVisit;
        lastSavedVisit = visit.cloneShallow();
        sighting = new Sighting();
        setupUI();
        // Setup the drag and drop on the buttons
        final PanelVisit panelVisitHandle = this;
        FileDrop.SetupFileDrop(btnBulkImport, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() > 0) {
                    final List<Path> lstSelectedPaths = UtilsFileProcessing.getPathsFromSelectedFile(inFiles.toArray(new File[inFiles.size()]));
                    if (visit.getName() != null && !visit.getName().isEmpty()) {
                        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                            @Override
                            protected Object doInBackground() throws Exception {
                                UtilsPanelGenerator.openBulkUploadTab(
                                        new BulkUploadPanel(app, this, locationForVisit, visit, lstSelectedPaths, panelVisitHandle), (JTabbedPane) getParent());
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
        ComboBoxFixer.configureComboBoxes(cmbGameWatchIntensity);
        ComboBoxFixer.configureComboBoxes(cmbType);
        // Setup images
        imageIndex = 0;
        if (VisitType.STASHED != visit.getType()) {
            int fotoCount = app.getDBI().countWildLogFiles(0, visit.getWildLogFileID());
            if (fotoCount > 0) {
                UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
                lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
                lblNumberOfImages.setText("0 of 0");
            }
        }
        else {
            setupStashedImages();
        }
        imageSightingIndex = 0;
        // Setup the table
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblSightings);
        UtilsTableGenerator.setupColumnResizingListener(tblSightings, 1);
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
        UtilsUI.attachClipboardPopup(txtDescription);
        // Setup info for tab headers
        tabTitle = visit.getName();
        tabID = visit.getID();
        tabIconURL = WildLogApp.class.getResource("resources/icons/Visit.gif");
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(visitIncludes.getBackground());
        dtpEndDate.getComponent(1).setBackground(visitIncludes.getBackground());
        // Scroll to the top of the text areas
        txtDescription.setCaretPosition(0);
        // Show or hide fields based on application type
        if (!(WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER)) {
            btnAutoName.setVisible(false);
            lblTotalFiles.setVisible(false);
            lblTotalNumberOfFiles.setVisible(false);
        }
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            btnExport.setEnabled(false);
            btnExport.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                btnDeleteSighting.setEnabled(false);
                btnDeleteSighting.setVisible(false);
                btnDeleteImage.setEnabled(false);
                btnDeleteImage.setVisible(false);
                btnReport.setEnabled(false);
                btnReport.setVisible(false);
                btnSlideshow.setEnabled(false);
                btnSlideshow.setVisible(false);
                btnAdvanced.setEnabled(false);
                btnAdvanced.setVisible(false);
            }
        }
        // As dit 'n Stashed visit is dan moet ek die UI sodanig aanpas
        if (VisitType.STASHED == visit.getType()) {
            visitIncludes.setBackground(getBackground().darker());
            pnlInfo.setBackground(getBackground().darker());
            pnlFiles.setBackground(getBackground().darker());
            pnlButtonsRight.setBackground(getBackground().darker());
            pnlButtonsLeft.setBackground(getBackground().darker());
            pnlButtons.setBackground(getBackground().darker());
            btnPreviousImage.setBackground(getBackground().darker());
            btnNextImage.setBackground(getBackground().darker());
            btnUpdate.setBackground(getBackground().darker());
            btnBulkImport.setBackground(getBackground().darker());
            btnExtraData.setBackground(getBackground().darker());
            btnBrowse.setBackground(getBackground().darker());
            btnAutoName.setBackground(getBackground().darker());
            dtpStartDate.getComponent(1).setBackground(getBackground().darker());
            dtpEndDate.getComponent(1).setBackground(getBackground().darker());
            cmbType.setEnabled(false);
            cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
            cmbType.setSelectedItem(visit.getType());
            txtName.setEnabled(false);
            if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_ADMIN 
                    && WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                dtpStartDate.setEnabled(false);
                dtpEndDate.setEnabled(false);
            }
            cmbGameWatchIntensity.setEnabled(false);
            btnExport.setEnabled(false);
            btnExport.setVisible(false);
            btnDeleteSighting.setEnabled(false);
            btnDeleteSighting.setVisible(false);
            btnDeleteImage.setEnabled(false);
            btnDeleteImage.setVisible(false);
            btnReport.setEnabled(false);
            btnReport.setVisible(false);
            btnSlideshow.setEnabled(false);
            btnSlideshow.setVisible(false);
            btnAdvanced.setEnabled(false);
            btnAdvanced.setVisible(false);
            pnlSightings.setEnabled(false);
            pnlSightings.setVisible(false);
            btnAddSighting.setEnabled(false);
            btnAddSighting.setVisible(false);
            pnlSelectedFiles.setVisible(false);
            btnMapSighting.setEnabled(false);
            btnMapSighting.setVisible(false);
            btnUploadImage.setEnabled(false);
            btnUploadImage.setVisible(false);
            btnSetMainImage.setEnabled(false);
            btnSetMainImage.setVisible(false);
            lblElements.setVisible(false);
            lblNumberOfElements.setVisible(false);
        }
    }

    private void uploadFiles(List<File> inFiles) {
        UtilsFileProcessing.performFileUpload(visit,
                Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(locationForVisit.getName()).resolve(visit.getName()), WildLogDataType.VISIT, 
                inFiles.toArray(new File[inFiles.size()]),
                new Runnable() {
                    @Override
                    public void run() {
                        imageIndex = 0;
                        UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
                        setupNumberOfImages();
                        // everything went well - saving
                        btnUpdateActionPerformed(null);
                    }
                }, 
                app, true, null, true, true);
    }

    public void setVisit(Visit inVisit) {
        visit = inVisit;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setLocationForVisit(Location inLocation) {
        locationForVisit = inLocation;
    }

    public Location getLocationForVisit() {
        return locationForVisit;
    }

    @Override
    public boolean closeTab() {
        btnUpdate.requestFocus();
        populateVisitFromUI();
        if (lastSavedVisit.hasTheSameContent(visit)) {
            ((JTabbedPane)getParent()).remove(this);
            return true;
        }
        else {
            String name = visit.getName();
            if (name == null || name.isEmpty()) {
                name = "<New Period>";
            }
            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                    "Save before closing this tab for " + name + "?", 
                    "You have unsaved data", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                btnUpdateActionPerformed(null);
                if (visit.getName().trim().length() > 0 && UtilsData.checkCharacters(visit.getName().trim())) {
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

    @Override
    public void doTheRefresh(final Object inIndicator) {
        formComponentShown(null);
        if (inIndicator instanceof PanelSighting) {
            // If no row is selected, try to select the saved row (most likely a new entry)
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (tblSightings.getSelectedRowCount() == 0 && inIndicator != null) {
                        UtilsTableGenerator.setupPreviousRowSelection(tblSightings, 
                                new long[]{((PanelSighting) inIndicator).getSighting().getID()}, 6);
                    }
                }
            });
        }
        //else 
        //if (inIndicator instanceof PanelVisit) {
            // Most likly the delete button was pressed.
            // Don't select anything extra.
        //}
        else
        if (inIndicator instanceof Visit) {
            visit = (Visit) inIndicator;
            dtpStartDate.setDate(visit.getStartDate());
            dtpEndDate.setDate(visit.getEndDate());
        }
    }

    private void refreshSightingInfo() {
        if (sighting != null) {
            if (sighting.getElementID() > 0) {
                int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getElementID());
                if (fotoCount > 0) {
                    UtilsImageProcessing.setupFoto(sighting.getElementID(), 0, lblElementImage, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
                }
                else {
                    lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
                }
            }
            else {
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
            }
            imageSightingIndex = 0;
            int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getWildLogFileID());
            if (fotoCount > 0 ) {
                UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
            }
            else {
                lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
            }
            setupNumberOfSightingImages();
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
            lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
            lblNumberOfSightingImages.setText("");
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visitIncludes = new javax.swing.JPanel();
        lblVisitName = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lblElements = new javax.swing.JLabel();
        lblNumberOfElements = new javax.swing.JLabel();
        pnlFiles = new javax.swing.JPanel();
        btnUploadImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        btnDeleteImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnPreviousImage = new javax.swing.JButton();
        pnlButtons = new javax.swing.JPanel();
        pnlButtonsRight = new javax.swing.JPanel();
        btnBrowse = new javax.swing.JButton();
        btnBulkImport = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnExtraData = new javax.swing.JButton();
        pnlButtonsLeft = new javax.swing.JPanel();
        btnSlideshow = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnAdvanced = new javax.swing.JButton();
        btnMapSighting = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        pnlInfo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel54 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        cmbGameWatchIntensity = new javax.swing.JComboBox();
        jLabel53 = new javax.swing.JLabel();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        btnAutoName = new javax.swing.JButton();
        pnlSightings = new javax.swing.JPanel();
        btnEditSighting = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSightings = new javax.swing.JTable();
        btnDeleteSighting = new javax.swing.JButton();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnAddSighting = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        lblTotalNumberOfFiles = new javax.swing.JLabel();
        lblTotalFiles = new javax.swing.JLabel();
        pnlSelectedFiles = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblSightingImage = new javax.swing.JLabel();
        btnGoElement = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnNextImageSighting = new javax.swing.JButton();
        lblNumberOfSightingImages = new javax.swing.JLabel();
        btnPreviousImageSighting = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblElementImage = new javax.swing.JLabel();

        setBackground(new java.awt.Color(230, 228, 240));
        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        visitIncludes.setBackground(new java.awt.Color(230, 228, 240));
        visitIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setName("visitIncludes"); // NOI18N
        visitIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));

        lblVisitName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblVisitName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitName.setName("lblVisitName"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        lblElements.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        lblElements.setText("Creatures:");
        lblElements.setName("lblElements"); // NOI18N

        lblNumberOfElements.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N

        pnlFiles.setBackground(new java.awt.Color(230, 228, 240));
        pnlFiles.setName("pnlFiles"); // NOI18N

        btnUploadImage.setBackground(new java.awt.Color(228, 240, 237));
        btnUploadImage.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("<html><u>Upload Files</u></html>");
        btnUploadImage.setToolTipText("<html>Upload a file for this Period. <br/>You can also drag-and-drop files onto the above box to upload it. <br/>(Note: Drag-and-drop only works on supported platforms.)</html>");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setIconTextGap(2);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        btnNextImage.setBackground(new java.awt.Color(228, 240, 237));
        btnNextImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImage.setToolTipText("Load the next file.");
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });

        lblNumberOfImages.setBackground(new java.awt.Color(224, 239, 240));
        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

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

        btnDeleteImage.setBackground(new java.awt.Color(228, 240, 237));
        btnDeleteImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteImage.setText("Delete File");
        btnDeleteImage.setToolTipText("Delete the current file.");
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setIconTextGap(2);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        btnSetMainImage.setBackground(new java.awt.Color(228, 240, 237));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnSetMainImage.setText("Set as First");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Period.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setIconTextGap(2);
        btnSetMainImage.setMargin(new java.awt.Insets(2, 1, 2, 1));
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });

        btnPreviousImage.setBackground(new java.awt.Color(228, 240, 237));
        btnPreviousImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImage.setToolTipText("Load the previous file.");
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilesLayout = new javax.swing.GroupLayout(pnlFiles);
        pnlFiles.setLayout(pnlFilesLayout);
        pnlFilesLayout.setHorizontalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(265, 265, 265)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        pnlButtons.setBackground(new java.awt.Color(230, 228, 240));
        pnlButtons.setName("pnlButtons"); // NOI18N

        pnlButtonsRight.setBackground(new java.awt.Color(230, 228, 240));
        pnlButtonsRight.setName("pnlButtonsRight"); // NOI18N

        btnBrowse.setBackground(new java.awt.Color(230, 228, 240));
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.setToolTipText("Open the Browse tab and automatically select this Period in the tree.");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrowse.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        btnBulkImport.setBackground(new java.awt.Color(230, 228, 240));
        btnBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Bulk Import.png"))); // NOI18N
        btnBulkImport.setText("<html><u>Bulk Import</u></html>");
        btnBulkImport.setToolTipText("Open a Bulk Import tab for this Period. You can drag-and-drop a folder on the button to quickly start the Bulk Import process.");
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBulkImport.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 204, 0));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdate.setToolTipText("Save and update the Period.");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnExtraData.setBackground(new java.awt.Color(230, 228, 240));
        btnExtraData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Extra_Small.png"))); // NOI18N
        btnExtraData.setText("Extra Data");
        btnExtraData.setToolTipText("Link extra fields to this Period.");
        btnExtraData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExtraData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExtraData.setMargin(new java.awt.Insets(2, 6, 2, 4));
        btnExtraData.setName("btnExtraData"); // NOI18N
        btnExtraData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtraDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsRightLayout = new javax.swing.GroupLayout(pnlButtonsRight);
        pnlButtonsRight.setLayout(pnlButtonsRightLayout);
        pnlButtonsRightLayout.setHorizontalGroup(
            pnlButtonsRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsRightLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExtraData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlButtonsRightLayout.setVerticalGroup(
            pnlButtonsRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsRightLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnExtraData, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pnlButtonsLeft.setBackground(new java.awt.Color(230, 228, 240));
        pnlButtonsLeft.setName("pnlButtonsLeft"); // NOI18N

        btnSlideshow.setBackground(new java.awt.Color(230, 228, 240));
        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images for this Period.");
        btnSlideshow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });

        btnReport.setBackground(new java.awt.Color(230, 228, 240));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.png"))); // NOI18N
        btnReport.setText("Charts");
        btnReport.setToolTipText("View charts for this Period.");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 8, 2, 4));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        btnAdvanced.setBackground(new java.awt.Color(230, 228, 240));
        btnAdvanced.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        btnAdvanced.setText("Advanced");
        btnAdvanced.setToolTipText("View available advanced features.");
        btnAdvanced.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdvanced.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdvanced.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnAdvanced.setName("btnAdvanced"); // NOI18N
        btnAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdvancedActionPerformed(evt);
            }
        });

        btnMapSighting.setBackground(new java.awt.Color(230, 228, 240));
        btnMapSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMapSighting.setText("Maps");
        btnMapSighting.setToolTipText("Show maps for this Period.");
        btnMapSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapSighting.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMapSighting.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnMapSighting.setName("btnMapSighting"); // NOI18N
        btnMapSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(230, 228, 240));
        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        btnExport.setText("Export");
        btnExport.setToolTipText("Show available exports for this Period.");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExport.setName("btnExport"); // NOI18N
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLeftLayout = new javax.swing.GroupLayout(pnlButtonsLeft);
        pnlButtonsLeft.setLayout(pnlButtonsLeftLayout);
        pnlButtonsLeftLayout.setHorizontalGroup(
            pnlButtonsLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLeftLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMapSighting, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnSlideshow, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnAdvanced, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        pnlButtonsLeftLayout.setVerticalGroup(
            pnlButtonsLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLeftLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnMapSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnAdvanced, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pnlButtonsLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlButtonsRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlButtonsRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlButtonsLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        pnlInfo.setBackground(new java.awt.Color(230, 228, 240));
        pnlInfo.setName("pnlInfo"); // NOI18N

        jLabel2.setText("End Date:");
        jLabel2.setName("jLabel2"); // NOI18N

        txtName.setBackground(new java.awt.Color(204, 255, 204));
        txtName.setText(visit.getName());
        txtName.setName("txtName"); // NOI18N

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(4);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        jLabel4.setText("Period Type:");
        jLabel4.setName("jLabel4"); // NOI18N

        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpStartDate.setName("dtpStartDate"); // NOI18N

        jLabel54.setText("Game Watching:");
        jLabel54.setName("jLabel54"); // NOI18N

        cmbType.setMaximumRowCount(15);
        cmbType.setModel(new DefaultComboBoxModel(VisitType.valuesForDroplist()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbType.setName("cmbType"); // NOI18N

        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N

        jLabel53.setText("Notes:");
        jLabel53.setName("jLabel53"); // NOI18N

        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpEndDate.setName("dtpEndDate"); // NOI18N

        jLabel1.setText("Start Date:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel52.setText("Period Name:");
        jLabel52.setName("jLabel52"); // NOI18N

        btnAutoName.setBackground(new java.awt.Color(230, 228, 240));
        btnAutoName.setText("Auto Name");
        btnAutoName.setToolTipText("Automatically set the name based on the Place and dates.");
        btnAutoName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAutoName.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnAutoName.setName("btnAutoName"); // NOI18N
        btnAutoName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlInfoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel54)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addGap(40, 40, 40)
                                .addComponent(jScrollPane14))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlInfoLayout.createSequentialGroup()
                                        .addGap(71, 71, 71)
                                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                                    .addGroup(pnlInfoLayout.createSequentialGroup()
                                        .addComponent(jLabel52)
                                        .addGap(311, 311, 311)))
                                .addGap(0, 0, 0)
                                .addComponent(btnAutoName)))
                        .addGap(5, 5, 5))))
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel52)
                    .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAutoName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53)
                            .addComponent(jScrollPane14))))
                .addGap(10, 10, 10))
        );

        pnlSightings.setBackground(new java.awt.Color(230, 228, 240));
        pnlSightings.setName("pnlSightings"); // NOI18N

        btnEditSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnEditSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnEditSighting.setToolTipText("Open a popup box to edit the selected Observation.");
        btnEditSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditSighting.setName("btnEditSighting"); // NOI18N
        btnEditSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSightingActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblSightings.setAutoCreateRowSorter(true);
        tblSightings.setName("tblSightings"); // NOI18N
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
        jScrollPane1.setViewportView(tblSightings);

        btnDeleteSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnDeleteSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteSighting.setToolTipText("<html>Delete the selected Observation. <br/>This will delete all linked files as well.</html>");
        btnDeleteSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });

        lblNumberOfSightings.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Observations:");
        jLabel5.setName("jLabel5"); // NOI18N

        btnAddSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnAddSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddSighting.setToolTipText("Open a popup box to add a new Observation.");
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });

        jSeparator5.setName("jSeparator5"); // NOI18N

        lblTotalNumberOfFiles.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblTotalNumberOfFiles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalNumberOfFiles.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblTotalNumberOfFiles.setName("lblTotalNumberOfFiles"); // NOI18N

        lblTotalFiles.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalFiles.setText("Files:");
        lblTotalFiles.setName("lblTotalFiles"); // NOI18N

        javax.swing.GroupLayout pnlSightingsLayout = new javax.swing.GroupLayout(pnlSightings);
        pnlSightings.setLayout(pnlSightingsLayout);
        pnlSightingsLayout.setHorizontalGroup(
            pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSightingsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalNumberOfFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalFiles))
                .addGap(5, 5, 5)
                .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSightingsLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                .addGap(5, 5, 5))
            .addComponent(jSeparator5)
        );
        pnlSightingsLayout.setVerticalGroup(
            pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSightingsLayout.createSequentialGroup()
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSightingsLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(lblTotalNumberOfFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        pnlSelectedFiles.setBackground(new java.awt.Color(230, 228, 240));
        pnlSelectedFiles.setName("pnlSelectedFiles"); // NOI18N

        jLabel6.setText("Observation Files:");
        jLabel6.setName("jLabel6"); // NOI18N

        lblSightingImage.setBackground(new java.awt.Color(0, 0, 0));
        lblSightingImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSightingImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSightingImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblSightingImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblSightingImage.setName("lblSightingImage"); // NOI18N
        lblSightingImage.setOpaque(true);
        lblSightingImage.setPreferredSize(new java.awt.Dimension(150, 150));
        lblSightingImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSightingImageMouseReleased(evt);
            }
        });

        btnGoElement.setBackground(new java.awt.Color(228, 240, 237));
        btnGoElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoElement.setToolTipText("Open a tab for the Creature recorded during the selected Observation.");
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });

        jSeparator2.setName("jSeparator2"); // NOI18N

        btnNextImageSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnNextImageSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImageSighting.setToolTipText("Load the next file for the selected Observation.");
        btnNextImageSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImageSighting.setName("btnNextImageSighting"); // NOI18N
        btnNextImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageSightingActionPerformed(evt);
            }
        });

        lblNumberOfSightingImages.setBackground(new java.awt.Color(224, 239, 240));
        lblNumberOfSightingImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfSightingImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightingImages.setName("lblNumberOfSightingImages"); // NOI18N

        btnPreviousImageSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnPreviousImageSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImageSighting.setToolTipText("Load the previous file for the selected Observation.");
        btnPreviousImageSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImageSighting.setName("btnPreviousImageSighting"); // NOI18N
        btnPreviousImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageSightingActionPerformed(evt);
            }
        });

        jLabel7.setText("Creature:");
        jLabel7.setName("jLabel7"); // NOI18N

        lblElementImage.setBackground(new java.awt.Color(0, 0, 0));
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.setPreferredSize(new java.awt.Dimension(150, 150));
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlSelectedFilesLayout = new javax.swing.GroupLayout(pnlSelectedFiles);
        pnlSelectedFiles.setLayout(pnlSelectedFilesLayout);
        pnlSelectedFilesLayout.setHorizontalGroup(
            pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSelectedFilesLayout.createSequentialGroup()
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(lblSightingImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSelectedFilesLayout.createSequentialGroup()
                        .addComponent(btnPreviousImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfSightingImages, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNextImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlSelectedFilesLayout.setVerticalGroup(
            pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectedFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(3, 3, 3)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSightingImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfSightingImages, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout visitIncludesLayout = new javax.swing.GroupLayout(visitIncludes);
        visitIncludes.setLayout(visitIncludesLayout);
        visitIncludesLayout.setHorizontalGroup(
            visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(lblVisitName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(lblElements)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator3)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSightings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFiles, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSelectedFiles, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );
        visitIncludesLayout.setVerticalGroup(
            visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblVisitName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblElements, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addComponent(pnlSightings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(pnlSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 21, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );

        add(visitIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
        if (visit.getID() > 0) {
            UtilsTableGenerator.setupSightingTableLarge(app, tblSightings, visit.getID());
            List<Sighting> sightings = app.getDBI().listSightings(0, 0, visit.getID(), false, Sighting.class);
            lblNumberOfSightings.setText(Integer.toString(sightings.size()));
            setupNumberOfSightingImages();
            List<Long> allElements = new ArrayList<>();
            for (int i = 0; i < sightings.size(); i++) {
                if (!allElements.contains(sightings.get(i).getElementID())) {
                    allElements.add(sightings.get(i).getElementID());
                }
            }
            lblNumberOfElements.setText(Integer.toString(allElements.size()));
            if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN) {
                List<Sighting> lstSightings = app.getDBI().listSightings(0, 0, visit.getID(), false, Sighting.class);
                int totalFiles = 0;
                for (Sighting sighting : lstSightings) {
                    totalFiles = totalFiles + app.getDBI().countWildLogFiles(0, sighting.getID());
                }
                lblTotalNumberOfFiles.setText(Integer.toString(totalFiles));
            }
        }
        else {
            UtilsTableGenerator.setupSightingTableLarge(app, tblSightings, 0);
            lblNumberOfSightings.setText("0");
            lblNumberOfElements.setText("0");
            if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN) {
                lblTotalNumberOfFiles.setText("0");
            }
        }
        lblNumberOfSightings.setToolTipText(lblNumberOfSightings.getText());
        lblNumberOfElements.setToolTipText(lblNumberOfElements.getText());
        if (VisitType.STASHED != visit.getType()) {
            int fotoCount = app.getDBI().countWildLogFiles(0, visit.getWildLogFileID());
            if (fotoCount > 0) {
                UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            }
            setupNumberOfImages();
        }
        else {
            setupStashedImages();
        }
        if (visit.getName() != null) {
            lblVisitName.setText(visit.getName() + " - [" + locationForVisit.getName() + "]");
        }
        else {
            lblVisitName.setText(". . .  - [" + locationForVisit.getName() + "]");
        }
        // Scroll the table
        if (sighting != null) {
            int select = -1;
            for (int t = 0; t < tblSightings.getModel().getRowCount(); t++) {
                if ((Long)(tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(t), 6)) == sighting.getID()) {
                    select = t;
                    break;
                }
            }
            if (select >= 0) {
                tblSightings.scrollRectToVisible(tblSightings.getCellRect(select, 0, true));
            }
        }
        sighting = null;
        setupNumberOfSightingImages();
        refreshSightingInfo();
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
       // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                && WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
            return;
        }
        if (tblSightings.getSelectedRowCount() > 0) {
           int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                   "Are you sure you want to delete the selected Observation(s)? This will delete all files linked to the Observation(s) as well.",
                   "Delete Observations(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                for (int row : tblSightings.getSelectedRows())  {
                    app.getDBI().deleteSighting((Long)tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(row), 6));
                }
                sighting = null;
                refreshSightingInfo();
                doTheRefresh(this);
            }
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        btnUpdateActionPerformed(null);
        if (!txtName.getBackground().equals(Color.RED)) {
            sighting = new Sighting();
            sighting.setLocationID(locationForVisit.getID());
            tblSightings.clearSelection();
            refreshSightingInfo();
            PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Add a New Observation",
                    sighting, locationForVisit, visit, null, this, true, false, false, false);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void btnEditSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSightingActionPerformed
        if (!isPopup) {
            if (sighting != null) {
                PanelSighting dialog = new PanelSighting(
                        app, app.getMainFrame(), "Edit an Existing Observation",
                        sighting, locationForVisit, visit, app.getDBI().findElement(sighting.getElementID(), null, false, Element.class), 
                        this, false, false, false, false);
                dialog.setVisible(true);
                // Reset Sighting on this panel
                sighting = null;
                refreshSightingInfo();
            }
            else {
                WLOptionPane.showMessageDialog(getTopLevelAncestor(),
                        "Only one Observation can be viewed at a time. Please select one row in the table and try again.",
                        "Select One Observation", JOptionPane.WARNING_MESSAGE);
            }
        }
}//GEN-LAST:event_btnEditSightingActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(null);
        if (!txtName.getBackground().equals(Color.RED)) {
            List<File> files = UtilsFileProcessing.showFileUploadDialog(app, app.getMainFrame());
            uploadFiles(files);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (VisitType.STASHED != visit.getType()) {
            imageIndex = UtilsImageProcessing.previousImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
            setupNumberOfImages();
        }
        else {
            imageIndex--;
            setupStashedImages();
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRowCount() == 1) {
            sighting = app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                    tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 6), true, Sighting.class);
        }
        else {
            sighting = null;
        }
        refreshSightingInfo();
}//GEN-LAST:event_tblSightingsMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (VisitType.STASHED != visit.getType()) {
            imageIndex = UtilsImageProcessing.nextImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
            setupNumberOfImages();
        }
        else {
            imageIndex++;
            setupStashedImages();
        }
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(visit.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(null);
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (sighting != null) {
            UtilsPanelGenerator.openPanelAsTab(app, sighting.getElementID(), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent(), null);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (VisitType.STASHED != visit.getType()) {
            UtilsFileProcessing.openFile(visit.getWildLogFileID(), imageIndex, app);
        }
        else {
            Path stashPath = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName());
            String[] files = stashPath.toFile().list();
            UtilsFileProcessing.openFile(stashPath.resolve(files[imageIndex]));
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (sighting != null) {
            if (sighting.getElementID() > 0) {
                UtilsFileProcessing.openFile(app.getDBI().findElement(sighting.getElementID(), null, false, Element.class).getWildLogFileID(), 0, app);
            }
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void lblSightingImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSightingImageMouseReleased
        if (sighting != null) {
            UtilsFileProcessing.openFile(sighting.getWildLogFileID(), imageSightingIndex, app);
        }
    }//GEN-LAST:event_lblSightingImageMouseReleased

    private void tblSightingsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnDeleteSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsKeyPressed

    private void tblSightingsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblSightingsMouseReleased(null);
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnEditSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsKeyReleased

    private void tblSightingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseClicked
        if (evt.getClickCount() == 2 && tblSightings.getSelectedRowCount() == 1) {
            btnEditSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            List<Sighting> lstSightings = null;
            if (tblSightings.getSelectedRowCount() > 0) {
                lstSightings = new ArrayList<>(tblSightings.getSelectedRowCount());
                for (int row : tblSightings.getSelectedRows())  {
                    lstSightings.add(app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                            tblSightings.convertRowIndexToModel(row), 6), true, Sighting.class));
                }
            }
            ExportDialog dialog = new ExportDialog(app, null, null, visit, null, lstSightings);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            List<Sighting> lstSightings = null;
            if (tblSightings.getSelectedRowCount() > 0) {
                lstSightings = new ArrayList<>(tblSightings.getSelectedRowCount());
                for (int row : tblSightings.getSelectedRows())  {
                    lstSightings.add(app.getDBI().findSighting((Long) tblSightings.getModel().getValueAt(
                            tblSightings.convertRowIndexToModel(row), 6), true, Sighting.class));
                }
            }
            SlideshowDialog dialog = new SlideshowDialog(app, visit, null, null, lstSightings);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + visit.getDisplayName(), 
                    app.getDBI().listSightings(0, 0, visit.getID(), true, Sighting.class), 0);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapSightingActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + visit.getName(), 
                    app.getDBI().listSightings(0, 0, visit.getID(), true, Sighting.class));
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            app.getMainFrame().browseSelectedVisit(visit);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[PanelVisit-Save]");
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = lastSavedVisit.getName();
                populateVisitFromUI();
                // Validate
                Visit existingVisit = app.getDBI().findVisit(0, visit.getName(), false, Visit.class);
                if (existingVisit != null && existingVisit.getID() != visit.getID()) {
                    int choice = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                            "<html>The name is not unique."
                                    + "<br />Continue to save this Period using the duplicate name?</html>", 
                            "Warning: Duplicate Name?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                if (evt != null && visit.getStartDate() == null) {
                    int choice = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                            "<html>No Start Date was provided. The Start Date is used by a number of charts and maps."
                                    + "<br />Continue to save this Period without a Start Date?</html>", 
                            "Warning: Save empty Start Date?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                // Save the visit
                boolean result;
                if (oldName == null || oldName.isEmpty()) {
                    result = app.getDBI().createVisit(visit, false);
                }
                else {
                    result = app.getDBI().updateVisit(visit, oldName, false);
                }
                if (result == true) {
                    // If this is a stashed visit and the name changed, then rename the folder
                    if (visit.getType() == VisitType.STASHED && !visit.getName().equalsIgnoreCase(oldName)) {
                        try {
                            Path oldStashPath = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(oldName);
                            Path newStashPath = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName());
                            Files.move(oldStashPath, newStashPath);
                        }
                        catch (IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                    // Refresh the UI
                    txtName.setBackground(new Color(204, 255, 204));
                    txtName.setText(visit.getName());
                    lastSavedVisit = visit.cloneShallow();
                    if (app.getWildLogOptions().isEnableSounds()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
                else {
                    txtName.setBackground(Color.RED);
                    visit.setName(oldName);
                    txtName.setText(oldName);
                    WLOptionPane.showMessageDialog(getTopLevelAncestor(), 
                            "The Period could not be saved.", 
                            "Not Saved!", JOptionPane.ERROR_MESSAGE);
                }
                lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");
                tabTitle = visit.getName();
                tabID = visit.getID();
                if (!isPopup) {
                    setupTabHeader(PanelCanSetupHeader.TabTypes.VISIT);
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
                WLOptionPane.showMessageDialog(getTopLevelAncestor(), 
                        "Please provide a Period Name before trying to save.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            txtName.setBackground(Color.RED);
            WLOptionPane.showMessageDialog(getTopLevelAncestor(), 
                    "The Period Name contains unsupported characters and could not be saved.", 
                    "Not Saved!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAdvancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdvancedActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            AdvancedDialog dialog = new AdvancedDialog(app, visit, this);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnAdvancedActionPerformed

    private void btnBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkImportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            final PanelVisit panelVisitHandle = this;
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    if (VisitType.STASHED == visit.getType()) {
                        List<Path> lstPaths = UtilsFileProcessing.getPathsFromSelectedFile(
                                new File[] { WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName()).toFile() });
                        final List<Path> lstAllFiles = UtilsFileProcessing.getListOfFilesToImport(lstPaths, true);
                        
// TODO: Check for saved Extra Data to prepopulate the data
                        
                        UtilsPanelGenerator.openBulkUploadTab(
                                new BulkUploadPanel(app, this, locationForVisit, visit, lstAllFiles, panelVisitHandle), (JTabbedPane)getParent());
                    }
                    else {
                        UtilsPanelGenerator.openBulkUploadTab(
                                new BulkUploadPanel(app, this, locationForVisit, visit, null, panelVisitHandle), (JTabbedPane)getParent());
                    }
                    return null;
                }
            });
        }
    }//GEN-LAST:event_btnBulkImportActionPerformed

    private void btnAutoNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoNameActionPerformed
        generateVisitName();
    }//GEN-LAST:event_btnAutoNameActionPerformed

    private void btnExtraDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtraDataActionPerformed
        btnUpdateActionPerformed(null);
        if (visit != null && visit.getID() > 0) {
            ExtraDataDialog dialog = new ExtraDataDialog(app.getMainFrame(), visit.getID(), WildLogDataType.VISIT);
            dialog.setVisible(true);
        }
        else {
            if (app.getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_btnExtraDataActionPerformed

    private void populateVisitFromUI() {
        visit.setName(UtilsData.limitLength(txtName.getText(), 100));
        visit.setStartDate(dtpStartDate.getDate());
        visit.setEndDate(dtpEndDate.getDate());
        visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
        visit.setType((VisitType)cmbType.getSelectedItem());
        visit.setDescription(txtDescription.getText());
        visit.setLocationID(locationForVisit.getID());
    }

    private void setupNumberOfImages() {
        int fotoCount = app.getDBI().countWildLogFiles(0, visit.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        }
        else {
            lblNumberOfImages.setText("0 of 0");
        }
        lblNumberOfImages.setToolTipText(lblNumberOfImages.getText());
    }

    private void setupNumberOfSightingImages() {
        if (sighting != null) {
            int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getWildLogFileID());
            if (fotoCount > 0) {
                lblNumberOfSightingImages.setText(imageSightingIndex+1 + " of " + fotoCount);
            }
            else {
                lblNumberOfSightingImages.setText("0 of 0");
            }
        }
        lblNumberOfSightingImages.setToolTipText(lblNumberOfSightingImages.getText());
    }
    
    private void setupStashedImages() {
        Path stashPath = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName());
        String[] files = stashPath.toFile().list();
        if (files != null) {
            if (imageIndex < 0) {
                imageIndex = files.length - 1;
            }
            else
            if (imageIndex > (files.length - 1)) {
                imageIndex = 0;
            }
            if (files.length > 0) {
                Path filePath = stashPath.resolve(files[imageIndex]);
                if (WildLogFileExtentions.Images.isKnownExtention(filePath)) {
                    lblImage.setIcon(UtilsImageProcessing.getScaledIcon(filePath, WildLogThumbnailSizes.S0300_NORMAL.getSize(), true));
                }
                else
                if (WildLogFileExtentions.Movies.isKnownExtention(filePath)) {
                    lblImage.setIcon(UtilsImageProcessing.getScaledIconForMovies(WildLogThumbnailSizes.S0300_NORMAL));
                }
                else {
                    lblImage.setIcon(UtilsImageProcessing.getScaledIconForOtherFiles(WildLogThumbnailSizes.S0300_NORMAL));
                }
                lblNumberOfImages.setText((imageIndex + 1) + " of " + files.length);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
                lblNumberOfImages.setText("0 of 0");
            }
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            lblNumberOfImages.setText("0 of 0");
        }
    }
    
    private void generateVisitName() {
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN 
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
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
            txtName.setText(UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(startDate) 
                    + "-" + UtilsTime.WL_DATE_FORMATTER_FOR_VISITS_WEI.format(endDate)
                    + "_" + locationForVisit.getName());
            if (visit.getType() == VisitType.STASHED) {
                txtName.setText(txtName.getText() + " - File Stash");
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnAdvanced;
    private javax.swing.JButton btnAutoName;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnExtraData;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnMapSighting;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnNextImageSighting;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnPreviousImageSighting;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnSlideshow;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbGameWatchIntensity;
    private javax.swing.JComboBox cmbType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblElements;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfSightingImages;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblSightingImage;
    private javax.swing.JLabel lblTotalFiles;
    private javax.swing.JLabel lblTotalNumberOfFiles;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlButtonsLeft;
    private javax.swing.JPanel pnlButtonsRight;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlSelectedFiles;
    private javax.swing.JPanel pnlSightings;
    private javax.swing.JTable tblSightings;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JPanel visitIncludes;
    // End of variables declaration//GEN-END:variables
}
