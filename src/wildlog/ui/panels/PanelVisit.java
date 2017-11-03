package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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
import wildlog.data.enums.WildLogThumbnailSizes;
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
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


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
            btnGoLocation.setEnabled(false);
            btnBulkImport.setEnabled(false);
            btnDeleteImage.setEnabled(false);
            btnDeleteSighting.setEnabled(false);
            btnEditSighting.setEnabled(false);
            btnGoElement.setEnabled(false);
            btnHTML.setEnabled(false);
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
                                        new BulkUploadPanel(app, this, visit.getLocationName(), visit.getName(), lstSelectedPaths, panelVisitHandle), 
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
        int fotoCount = app.getDBI().countWildLogFiles(null, visit.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
            lblNumberOfImages.setText("0 of 0");
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
        tabIconURL = app.getClass().getResource("resources/icons/Visit.gif");
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(visitIncludes.getBackground());
        dtpEndDate.getComponent(1).setBackground(visitIncludes.getBackground());
    }

    private void uploadFiles(List<File> inFiles) {
        UtilsFileProcessing.performFileUpload(visit,
                Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(locationForVisit.getName()).resolve(visit.getName()),
                inFiles.toArray(new File[inFiles.size()]),
                new Runnable() {
                    @Override
                    public void run() {
                        imageIndex = 0;
                        UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
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
            if (name ==null || name.isEmpty()) {
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
                                new String[]{Long.toString(((PanelSighting) inIndicator).getSighting().getSightingCounter())}, 6);
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
            if (sighting.getElementName() != null) {
                int fotoCount = app.getDBI().countWildLogFiles(null, Element.WILDLOGFILE_ID_PREFIX + sighting.getElementName());
                if (fotoCount > 0) {
                    UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + sighting.getElementName(), 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
                }
                else {
                    lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
                }
            }
            else {
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
            }
            imageSightingIndex = 0;
            int fotoCount = app.getDBI().countWildLogFiles(null, sighting.getWildLogFileID());
            if (fotoCount > 0 ) {
                UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
            }
            else {
                lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
            }
            setupNumberOfSightingImages();
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
            lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
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
        jLabel8 = new javax.swing.JLabel();
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
        btnUpdate = new javax.swing.JButton();
        btnGoLocation = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnMapSighting = new javax.swing.JButton();
        btnHTML = new javax.swing.JButton();
        btnAdvanced = new javax.swing.JButton();
        btnBulkImport = new javax.swing.JButton();
        btnSlideshow = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
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
        pnlSightings = new javax.swing.JPanel();
        btnEditSighting = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSightings = new javax.swing.JTable();
        btnDeleteSighting = new javax.swing.JButton();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnAddSighting = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
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

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel8.setText("Creatures:");
        jLabel8.setName("jLabel8"); // NOI18N

        lblNumberOfElements.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N

        pnlFiles.setBackground(new java.awt.Color(230, 228, 240));
        pnlFiles.setName("pnlFiles"); // NOI18N

        btnUploadImage.setBackground(new java.awt.Color(228, 240, 237));
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("<html><u>Upload Files</u></html>");
        btnUploadImage.setToolTipText("<html>Upload a file for this Period. <br/>You can also drag-and-drop files onto the above box to upload it. <br/>(Note: Drag-and-drop only works on supported platforms.)</html>");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
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
        btnNextImage.setFocusPainted(false);
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
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        btnSetMainImage.setBackground(new java.awt.Color(228, 240, 237));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnSetMainImage.setText("Default");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Period.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setFocusPainted(false);
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
        btnPreviousImage.setFocusPainted(false);
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
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(btnSetMainImage))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(330, 330, 330)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(btnDeleteImage))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        pnlButtons.setBackground(new java.awt.Color(230, 228, 240));
        pnlButtons.setName("pnlButtons"); // NOI18N

        btnUpdate.setBackground(new java.awt.Color(0, 204, 0));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdate.setToolTipText("Save and update the Period.");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnGoLocation.setBackground(new java.awt.Color(230, 228, 240));
        btnGoLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnGoLocation.setText("View Place");
        btnGoLocation.setToolTipText("Open the tab of the the Place linked to this Period.");
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setFocusPainted(false);
        btnGoLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGoLocation.setIconTextGap(5);
        btnGoLocation.setMargin(new java.awt.Insets(2, 10, 2, 4));
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });

        btnReport.setBackground(new java.awt.Color(230, 228, 240));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.png"))); // NOI18N
        btnReport.setText("View Reports");
        btnReport.setToolTipText("View reports for this Period.");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setFocusPainted(false);
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 8, 2, 4));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        btnMapSighting.setBackground(new java.awt.Color(230, 228, 240));
        btnMapSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMapSighting.setText("View Maps");
        btnMapSighting.setToolTipText("Show maps for this Period.");
        btnMapSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapSighting.setFocusPainted(false);
        btnMapSighting.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMapSighting.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnMapSighting.setName("btnMapSighting"); // NOI18N
        btnMapSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingActionPerformed(evt);
            }
        });

        btnHTML.setBackground(new java.awt.Color(230, 228, 240));
        btnHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        btnHTML.setText("Export");
        btnHTML.setToolTipText("Show available exports for this Period.");
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setFocusPainted(false);
        btnHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHTML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });

        btnAdvanced.setBackground(new java.awt.Color(230, 228, 240));
        btnAdvanced.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        btnAdvanced.setText("Advanced");
        btnAdvanced.setToolTipText("View available advanced features.");
        btnAdvanced.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdvanced.setFocusPainted(false);
        btnAdvanced.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAdvanced.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnAdvanced.setName("btnAdvanced"); // NOI18N
        btnAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdvancedActionPerformed(evt);
            }
        });

        btnBulkImport.setBackground(new java.awt.Color(230, 228, 240));
        btnBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Bulk Import.png"))); // NOI18N
        btnBulkImport.setText("<html><u>Bulk Import</u></html>");
        btnBulkImport.setToolTipText("Open a Bulk Import tab for this Period. You can drag-and-drop a folder on the button to quickly start the Bulk Import process.");
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setFocusPainted(false);
        btnBulkImport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBulkImport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });

        btnSlideshow.setBackground(new java.awt.Color(230, 228, 240));
        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images for this Period.");
        btnSlideshow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshow.setFocusPainted(false);
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });

        btnBrowse.setBackground(new java.awt.Color(230, 228, 240));
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.setToolTipText("Open the Browse tab and automatically select this Period in the tree.");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.setFocusPainted(false);
        btnBrowse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrowse.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnMapSighting, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                        .addComponent(btnReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                        .addComponent(btnSlideshow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnBulkImport, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdvanced, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBrowse, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnAdvanced, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addComponent(btnMapSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
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

        jLabel4.setText("Game Watching:");
        jLabel4.setName("jLabel4"); // NOI18N

        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpStartDate.setName("dtpStartDate"); // NOI18N

        jLabel54.setText("Period Type:");
        jLabel54.setName("jLabel54"); // NOI18N

        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setFocusable(false);
        cmbType.setName("cmbType"); // NOI18N

        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setFocusable(false);
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N

        jLabel53.setText("Description:");
        jLabel53.setName("jLabel53"); // NOI18N

        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpEndDate.setName("dtpEndDate"); // NOI18N

        jLabel1.setText("Start Date:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel52.setText("Period Name:");
        jLabel52.setName("jLabel52"); // NOI18N

        javax.swing.GroupLayout pnlInfoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addGap(12, 12, 12)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlInfoLayout.createSequentialGroup()
                                        .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel4))
                                    .addGroup(pnlInfoLayout.createSequentialGroup()
                                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel54)))
                                .addGap(5, 5, 5)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel52))
                .addGap(5, 5, 5))
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel52)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53)
                            .addComponent(jScrollPane14))))
                .addGap(5, 5, 5))
        );

        pnlSightings.setBackground(new java.awt.Color(230, 228, 240));
        pnlSightings.setName("pnlSightings"); // NOI18N

        btnEditSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnEditSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnEditSighting.setToolTipText("Open a popup box to edit the selected Observation.");
        btnEditSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditSighting.setFocusPainted(false);
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
        btnDeleteSighting.setFocusPainted(false);
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
        jLabel5.setText("List Observations during this Period:");
        jLabel5.setName("jLabel5"); // NOI18N

        btnAddSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnAddSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddSighting.setToolTipText("Open a popup box to add a new Observation.");
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setFocusPainted(false);
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });

        jSeparator5.setName("jSeparator5"); // NOI18N

        javax.swing.GroupLayout pnlSightingsLayout = new javax.swing.GroupLayout(pnlSightings);
        pnlSightings.setLayout(pnlSightingsLayout);
        pnlSightingsLayout.setHorizontalGroup(
            pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSightingsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSightingsLayout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSightingsLayout.createSequentialGroup()
                        .addGroup(pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
            .addComponent(jSeparator5)
        );
        pnlSightingsLayout.setVerticalGroup(
            pnlSightingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSightingsLayout.createSequentialGroup()
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
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
                        .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        pnlSelectedFiles.setBackground(new java.awt.Color(230, 228, 240));
        pnlSelectedFiles.setName("pnlSelectedFiles"); // NOI18N

        jLabel6.setText("The Observation's linked Files:");
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
        btnGoElement.setFocusPainted(false);
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
        btnNextImageSighting.setFocusPainted(false);
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
        btnPreviousImageSighting.setFocusPainted(false);
        btnPreviousImageSighting.setName("btnPreviousImageSighting"); // NOI18N
        btnPreviousImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageSightingActionPerformed(evt);
            }
        });

        jLabel7.setText("Creature observed:");
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
                .addGap(5, 5, 5)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(3, 3, 3)
                .addGroup(pnlSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSightingImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
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
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addComponent(lblVisitName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel8)
                                .addGap(0, 0, 0)
                                .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator3)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(pnlSightings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        visitIncludesLayout.setVerticalGroup(
            visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblVisitName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(pnlSightings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(visitIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
        if (visit.getName() != null) {
            UtilsTableGenerator.setupSightingTableLarge(app, tblSightings, visit.getName());
            List<Sighting> sightings = app.getDBI().listSightings(0, null, null, visit.getName(), false, Sighting.class);
            lblNumberOfSightings.setText(Integer.toString(sightings.size()));
            setupNumberOfSightingImages();
            List<String> allElements = new ArrayList<String>();
            for (int i = 0; i < sightings.size(); i++) {
                if (!allElements.contains(sightings.get(i).getElementName())) {
                    allElements.add(sightings.get(i).getElementName());
                }
            }
            lblNumberOfElements.setText(Integer.toString(allElements.size()));
        }
        else {
            UtilsTableGenerator.setupSightingTableLarge(app, tblSightings, null);
            lblNumberOfSightings.setText("0");
            lblNumberOfElements.setText("0");
        }
        int fotoCount = app.getDBI().countWildLogFiles(null, visit.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
        setupNumberOfImages();
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
                if ((Long)(tblSightings.getModel().getValueAt(tblSightings.convertRowIndexToModel(t), 6)) == sighting.getSightingCounter())
                {
                    select = t;
                    break;
                }
            }
            if (select >= 0) {
//                tblSightings.getSelectionModel().setSelectionInterval(select, select);
                tblSightings.scrollRectToVisible(tblSightings.getCellRect(select, 0, true));
            }
        }
        sighting = null;
        setupNumberOfSightingImages();
        refreshSightingInfo();
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
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
            sighting.setLocationName(locationForVisit.getName());
            tblSightings.clearSelection();
            refreshSightingInfo();
            PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Add a New Observation",
                    sighting, locationForVisit, visit, null, this, true, false, false);
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
                        sighting, locationForVisit, visit, app.getDBI().findElement(sighting.getElementName(), Element.class), this, false, false, false);
                dialog.setVisible(true);
                // Reset Sighting on this panel
                sighting = null;
                refreshSightingInfo();
            }
            else {
                WLOptionPane.showMessageDialog(app.getMainFrame(),
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
        imageIndex = UtilsImageProcessing.previousImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRowCount() == 1) {
            sighting = app.getDBI().findSighting((Long)tblSightings.getModel().getValueAt(
                    tblSightings.convertRowIndexToModel(tblSightings.getSelectedRow()), 6), Sighting.class);
        }
        else {
            sighting = null;
        }
        refreshSightingInfo();
}//GEN-LAST:event_tblSightingsMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(visit.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
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
            UtilsPanelGenerator.openPanelAsTab(app, sighting.getElementName(), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent(), null);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(visit.getWildLogFileID(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                UtilsFileProcessing.openFile(app.getDBI().findElement(sighting.getElementName(), Element.class).getWildLogFileID(), 0, app);
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

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            List<Sighting> lstSightings = null;
            if (tblSightings.getSelectedRowCount() > 0) {
                lstSightings = new ArrayList<>(tblSightings.getSelectedRowCount());
                for (int row : tblSightings.getSelectedRows())  {
                    lstSightings.add(app.getDBI().findSighting((Long)tblSightings.getModel().getValueAt(
                            tblSightings.convertRowIndexToModel(row), 6), Sighting.class));
                }
            }
            ExportDialog dialog = new ExportDialog(app, null, null, visit, null, lstSightings);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            List<Sighting> lstSightings = null;
            if (tblSightings.getSelectedRowCount() > 0) {
                lstSightings = new ArrayList<>(tblSightings.getSelectedRowCount());
                for (int row : tblSightings.getSelectedRows())  {
                    lstSightings.add(app.getDBI().findSighting((Long)tblSightings.getModel().getValueAt(
                            tblSightings.convertRowIndexToModel(row), 6), Sighting.class));
                }
            }
            SlideshowDialog dialog = new SlideshowDialog(app, visit, null, null, lstSightings);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + visit.getDisplayName(), 
                    app.getDBI().listSightings(0, null, null, visit.getName(), true, Sighting.class));
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapSightingActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Reports - " + visit.getName(), 
                    app.getDBI().listSightings(0, null, null, visit.getName(), true, Sighting.class));
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            app.getMainFrame().browseSelectedVisit(visit);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        UtilsPanelGenerator.openPanelAsTab(app, locationForVisit.getName(), PanelCanSetupHeader.TabTypes.LOCATION, (JTabbedPane)getParent(), null);
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[PanelVisit-Save]");
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = lastSavedVisit.getName();
                populateVisitFromUI();
                // Waarsku as die daar nie minstens 'n begin datum is nie
                if (evt != null && visit.getStartDate() == null) {
                    int result = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                            "<html>No Start Date was provided. The Start Date is used by a number of reports and maps."
                                    + "<br />Continue to save this Period without a Start Date?</html>", 
                            "Warning: Save empty Start Date?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                // Save the visit
                boolean result;
                if (oldName == null || oldName.isEmpty()) {
                    result = app.getDBI().createVisit(visit);
                }
                else {
                    result = app.getDBI().updateVisit(visit, oldName);
                }
                if (result == true) {
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
                    WLOptionPane.showMessageDialog(this, 
                            "The Period could not be saved.", 
                            "Not Saved!", JOptionPane.ERROR_MESSAGE);
                }

                lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");

                tabTitle = visit.getName();
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
                WLOptionPane.showMessageDialog(this, 
                        "Please provide a Period Name before trying to save.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            txtName.setBackground(Color.RED);
            WLOptionPane.showMessageDialog(this, 
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
                    UtilsPanelGenerator.openBulkUploadTab(
                            new BulkUploadPanel(app, this, visit.getLocationName(), visit.getName(), null, panelVisitHandle), (JTabbedPane)getParent());
                    return null;
                }
            });
        }
    }//GEN-LAST:event_btnBulkImportActionPerformed

    private void populateVisitFromUI() {
        visit.setName(UtilsData.limitLength(txtName.getText(), 100));
        visit.setStartDate(dtpStartDate.getDate());
        visit.setEndDate(dtpEndDate.getDate());
        visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
        visit.setType((VisitType)cmbType.getSelectedItem());
        visit.setDescription(txtDescription.getText());
        visit.setLocationName(locationForVisit.getName());
    }

    private void setupNumberOfImages() {
        int fotoCount = app.getDBI().countWildLogFiles(null, visit.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        }
        else {
            lblNumberOfImages.setText("0 of 0");
        }
    }

    private void setupNumberOfSightingImages() {
        if (sighting != null) {
            int fotoCount = app.getDBI().countWildLogFiles(null, sighting.getWildLogFileID());
            if (fotoCount > 0) {
                lblNumberOfSightingImages.setText(imageSightingIndex+1 + " of " + fotoCount);
            }
            else {
                lblNumberOfSightingImages.setText("0 of 0");
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnAdvanced;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnHTML;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfSightingImages;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblSightingImage;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JPanel pnlButtons;
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
