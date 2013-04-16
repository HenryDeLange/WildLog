package wildlog.ui.panels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.data.utils.UtilsData;
import wildlog.html.utils.UtilsHTML;
import wildlog.ui.dialogs.ChecklistDialog;
import wildlog.ui.dialogs.MappingDialog;
import wildlog.ui.dialogs.ReportingDialog;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilPanelGenerator;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogPrefixes;

/**
 *
 * @author  henry.delange
 */
public class PanelVisit extends PanelCanSetupHeader implements PanelNeedsRefreshWhenSightingAdded {
    private int imageIndex;
    private WildLogApp app;
    private Visit visit;
    private Location locationForVisit;
    private Sighting sighting;
    private int imageSightingIndex;

    /** Creates new form PanelVisit */
    public PanelVisit(WildLogApp inApp, Location inLocation, Visit inVisit) {
        app = inApp;
        locationForVisit = inLocation;
        visit = inVisit;
        sighting = new Sighting();
        //sighting.setLocation(locationForVisit);
        initComponents();
        imageIndex = 0;
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(visit.getWildLogFileID()));
        if (fotos.size() > 0) {
            UtilsImageProcessing.setupFoto(visit.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM));
            lblNumberOfImages.setText("0 of 0");
        }
        imageSightingIndex = 0;
        //if (sighting.getFotos() != null && sighting.getFotos().size() > 0) setupFotos(0);
        // Setup the table
        tblSightings.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblSightings);

        // setup the file dropping
        FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                btnUpdateActionPerformed(null);
                if (!txtName.getBackground().equals(Color.RED)) {
                    imageIndex = UtilsFileProcessing.uploadFilesUsingList(visit.getWildLogFileID(),
                            WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_VISIT.toString(), locationForVisit.getName(), visit.getName()),
                            null, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app, inFiles);
                    setupNumberOfImages();
                    // everything went well - saving
                    btnUpdateActionPerformed(null);
                }
            }
        });

        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtName);
        UtilsUI.attachClipboardPopup(txtDescription);

        // Setup info for tab headers
        tabLabel = visit.getName();
        tabIconURL = app.getClass().getResource("resources/icons/Visit.gif");
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(visitIncludes.getBackground());
        dtpEndDate.getComponent(1).setBackground(visitIncludes.getBackground());
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
    public boolean equals(Object inObject) {
        if (getClass() != inObject.getClass()) return false;
        final PanelVisit inPanel = (PanelVisit) inObject;
        if (visit == null && inPanel.getVisit() == null) return true;
        if (visit.getName() == null && inPanel.getVisit().getName() == null) return true;
        if (visit == null) return false;
        if (locationForVisit == null) return true;
        if (visit.getName() == null) return false;
        if (locationForVisit.getName() == null) return true;
        if (!visit.getName().equalsIgnoreCase(inPanel.getVisit().getName()) ||
            !locationForVisit.getName().equalsIgnoreCase(inPanel.getLocationForVisit().getName()))
                return false;
        return true;
    }

    @Override
    public void closeTab() {
        ((JTabbedPane)getParent()).remove(this);
    }

    @Override
    public void refreshTableForSightings() {
        //app.getDBI().refresh(locationForVisit);
        formComponentShown(null);
    }

    private void refreshSightingInfo() {
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                Element tempElement = app.getDBI().find(new Element(sighting.getElementName()));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempElement.getWildLogFileID()));
                if (fotos.size() > 0)
                    UtilsImageProcessing.setupFoto(tempElement.getWildLogFileID(), 0, lblElementImage, 150, app);
                else
                    lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            else {
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            imageSightingIndex = 0;
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
            if (fotos.size() > 0 ) {
                UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, 150, app);
            }
            else {
                lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            setupNumberOfSightingImages();
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
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
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel52 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbGameWatchIntensity = new javax.swing.JComboBox();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSightings = new javax.swing.JTable();
        btnUploadImage = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnAddSighting = new javax.swing.JButton();
        btnDeleteSighting = new javax.swing.JButton();
        btnEditSighting = new javax.swing.JButton();
        lblSightingImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        btnDeleteImage = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        btnMapSighting = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnGoElement = new javax.swing.JButton();
        lblNumberOfSightings = new javax.swing.JLabel();
        btnPreviousImageSighting = new javax.swing.JButton();
        btnNextImageSighting = new javax.swing.JButton();
        lblElementImage = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblNumberOfElements = new javax.swing.JLabel();
        lblNumberOfSightingImages = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        btnChecklist = new javax.swing.JButton();
        btnHTML = new javax.swing.JButton();
        btnSlideshow = new javax.swing.JButton();

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

        jSeparator8.setName("jSeparator8"); // NOI18N

        jSeparator9.setName("jSeparator9"); // NOI18N

        jLabel52.setText("Period Name:");
        jLabel52.setName("jLabel52"); // NOI18N

        txtName.setBackground(new java.awt.Color(204, 255, 204));
        txtName.setText(visit.getName());
        txtName.setName("txtName"); // NOI18N

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

        jLabel53.setText("Description:");
        jLabel53.setName("jLabel53"); // NOI18N

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(4);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        jLabel54.setText("Type of Visit:");
        jLabel54.setName("jLabel54"); // NOI18N

        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setFocusable(false);
        cmbType.setName("cmbType"); // NOI18N

        jLabel1.setText("Start Date:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText("End Date:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel4.setText("Game Watching:");
        jLabel4.setName("jLabel4"); // NOI18N

        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setFocusable(false);
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N

        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpStartDate.setName("dtpStartDate"); // NOI18N

        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpEndDate.setName("dtpEndDate"); // NOI18N

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setName("jSeparator2"); // NOI18N

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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblSightings.setAutoCreateRowSorter(true);
        tblSightings.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSightings.setName("tblSightings"); // NOI18N
        tblSightings.setSelectionBackground(new java.awt.Color(125, 120, 93));
        tblSightings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSightings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSightingsMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSightingsMouseClicked(evt);
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

        btnUploadImage.setBackground(new java.awt.Color(228, 240, 237));
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("Upload File");
        btnUploadImage.setToolTipText("Upload a file for this Period. You can also drag and drop files onto the above box to upload it.");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Observations during this Period:");
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

        btnDeleteSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnDeleteSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteSighting.setToolTipText("Delete the selected Observation. This will delete all linked files as well.");
        btnDeleteSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSighting.setFocusPainted(false);
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });

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

        jSeparator3.setName("jSeparator3"); // NOI18N

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

        jSeparator4.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N

        btnMapSighting.setBackground(new java.awt.Color(228, 240, 237));
        btnMapSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMapSighting.setText("View Maps");
        btnMapSighting.setToolTipText("Show available maps for this Period.");
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

        btnSetMainImage.setBackground(new java.awt.Color(228, 240, 237));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.gif"))); // NOI18N
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

        lblNumberOfSightings.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N

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

        jLabel6.setText("The Observation's linked Files:");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("Creature observed:");
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel8.setText("Creatures:");
        jLabel8.setName("jLabel8"); // NOI18N

        lblNumberOfElements.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N

        lblNumberOfSightingImages.setBackground(new java.awt.Color(224, 239, 240));
        lblNumberOfSightingImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfSightingImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightingImages.setName("lblNumberOfSightingImages"); // NOI18N

        lblNumberOfImages.setBackground(new java.awt.Color(224, 239, 240));
        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        btnReport.setBackground(new java.awt.Color(228, 240, 237));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.gif"))); // NOI18N
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

        btnChecklist.setBackground(new java.awt.Color(228, 240, 237));
        btnChecklist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CheckList.png"))); // NOI18N
        btnChecklist.setText("Add Checklist");
        btnChecklist.setToolTipText("Open a popup box to add Observations in a checklist format.");
        btnChecklist.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChecklist.setFocusPainted(false);
        btnChecklist.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChecklist.setIconTextGap(5);
        btnChecklist.setMargin(new java.awt.Insets(2, 10, 2, 4));
        btnChecklist.setName("btnChecklist"); // NOI18N
        btnChecklist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecklistActionPerformed(evt);
            }
        });

        btnHTML.setBackground(new java.awt.Color(228, 240, 237));
        btnHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        btnHTML.setText("View HTML");
        btnHTML.setToolTipText("View the HTML export for this Period.");
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setFocusPainted(false);
        btnHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHTML.setIconTextGap(5);
        btnHTML.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });

        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images.");
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

        javax.swing.GroupLayout visitIncludesLayout = new javax.swing.GroupLayout(visitIncludes);
        visitIncludes.setLayout(visitIncludesLayout);
        visitIncludesLayout.setHorizontalGroup(
            visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblVisitName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25)
                        .addComponent(jLabel8)
                        .addGap(2, 2, 2)
                        .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel53)
                        .addGap(8, 8, 8)
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, visitIncludesLayout.createSequentialGroup()
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(visitIncludesLayout.createSequentialGroup()
                                        .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel54))
                                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                                .addGap(104, 104, 104)
                                                .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(jScrollPane14))
                                .addGap(10, 10, 10)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnMapSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnChecklist, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jSeparator3)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addComponent(jLabel4))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(319, 319, 319)
                        .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel52))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel5))
                    .addComponent(jSeparator1)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addComponent(jScrollPane1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, visitIncludesLayout.createSequentialGroup()
                                .addGap(650, 650, 650)
                                .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)))
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(270, 270, 270)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(65, 65, 65)
                        .addComponent(jLabel6))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblSightingImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnPreviousImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfSightingImages, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnNextImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2))
                .addGap(5, 5, 5))
        );
        visitIncludesLayout.setVerticalGroup(
            visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVisitName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(1, 1, 1)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel52))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(visitIncludesLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbGameWatchIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(visitIncludesLayout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(visitIncludesLayout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(visitIncludesLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(8, 8, 8)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel53)))
                            .addGroup(visitIncludesLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnChecklist, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnMapSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(16, 16, 16)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnEditSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                .addGap(5, 5, 5))
            .addGroup(visitIncludesLayout.createSequentialGroup()
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(btnSetMainImage))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(330, 330, 330)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(326, 326, 326)
                        .addComponent(btnDeleteImage))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(188, 188, 188)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(visitIncludesLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(btnUploadImage))
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(6, 6, 6)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSightingImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(visitIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfSightingImages, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextImageSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        add(visitIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = visit.getName();
                visit.setName(UtilsData.limitLength(txtName.getText(), 100));
                visit.setStartDate(dtpStartDate.getDate());
                visit.setEndDate(dtpEndDate.getDate());
                visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
                visit.setType((VisitType)cmbType.getSelectedItem());
                visit.setDescription(txtDescription.getText());
                visit.setLocationName(locationForVisit.getName());

                // Save the visit
                if (app.getDBI().createOrUpdate(visit, oldName) == true) {
                    txtName.setBackground(new java.awt.Color(204, 255, 204));
                    txtName.setText(visit.getName());
                }
                else {
                    txtName.setBackground(Color.RED);
                    visit.setName(oldName);
                    txtName.setText(txtName.getText() + "_not_unique");
                }

                lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");

                tabLabel = visit.getName();
                setupTabHeader();
            }
            else {
                txtName.setBackground(Color.RED);
            }
        }
        else {
            txtName.setText(txtName.getText() + "_unsupported_character");
            txtName.setBackground(Color.RED);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
        if (visit.getName() != null) {
            UtilTableGenerator.setupCompleteSightingTable(app, tblSightings, visit);
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(visit.getName());
            List<Sighting> sightings = app.getDBI().list(tempSighting);
            lblNumberOfSightings.setText(Integer.toString(sightings.size()));
            setupNumberOfSightingImages();
            List<String> allElements = new ArrayList<String>();
            for (int i = 0; i < sightings.size(); i++) {
                if (!allElements.contains(sightings.get(i).getElementName()))
                    allElements.add(sightings.get(i).getElementName());
            }
            lblNumberOfElements.setText(Integer.toString(allElements.size()));
        }
        else {
            tblSightings.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
            lblNumberOfSightings.setText("0");
            lblNumberOfElements.setText("0");
        }

        if (visit.getName() != null)
            lblVisitName.setText(visit.getName() + " - [" + locationForVisit.getName() + "]");
        else
            lblVisitName.setText(". . .  - [" + locationForVisit.getName() + "]");
        // Scroll the table
        if (sighting != null) {
            int select = -1;
            for (int t = 0; t < tblSightings.getModel().getRowCount(); t++) {
                if ((Long)(tblSightings.getValueAt(t, 5)) == sighting.getSightingCounter())
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
           int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                "Are you sure you want to delete the selected Observation(s)? This will delete all files linked to the Observation(s) as well.",
                                "Delete Observations(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    }
           });
            if (result == JOptionPane.YES_OPTION) {
                sighting = app.getDBI().find(new Sighting((Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 5)));
                app.getDBI().delete(sighting);
                sighting = null;
                refreshSightingInfo();
                refreshTableForSightings();
            }
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        btnUpdateActionPerformed(evt);
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
        if (sighting != null) {
            tblSightings.clearSelection();
            PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Edit an Existing Observation",
                    sighting, locationForVisit, visit, app.getDBI().find(new Element(sighting.getElementName())), this, false, false, false);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
}//GEN-LAST:event_btnEditSightingActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            imageIndex = UtilsFileProcessing.uploadFileUsingDialog(visit.getWildLogFileID(),
                    WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_VISIT.toString(), locationForVisit.getName(), visit.getName()),
                    this, lblImage, 300, app);
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(visit.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRow() >= 0) {
            sighting = app.getDBI().find(new Sighting((Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 5)));
        }
        else {
            sighting = null;
        }
        refreshSightingInfo();
}//GEN-LAST:event_tblSightingsMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(visit.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            MappingDialog dialog = new MappingDialog(app, null, null, visit, sighting);
            dialog.setVisible(true);
        }
}//GEN-LAST:event_btnMapSightingActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(visit.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(visit.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (sighting != null) {
            PanelElement tempPanel = UtilPanelGenerator.getElementPanel(app, sighting.getElementName());
            UtilPanelGenerator.addPanelAsTab(tempPanel, (JTabbedPane)getParent());
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(visit.getWildLogFileID(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                UtilsFileProcessing.openFile(app.getDBI().find(new Element(sighting.getElementName())).getWildLogFileID(), 0, app);
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
    }//GEN-LAST:event_tblSightingsKeyPressed

    private void tblSightingsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblSightingsMouseReleased(null);
        else
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnEditSightingActionPerformed(null);
    }//GEN-LAST:event_tblSightingsKeyReleased

    private void tblSightingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseClicked
        if (evt.getClickCount() == 2) {
            btnEditSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsMouseClicked

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ReportingDialog dialog = new ReportingDialog(app, null, null, visit, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnChecklistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecklistActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ChecklistDialog dialog = new ChecklistDialog(app, locationForVisit, visit, this);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnChecklistActionPerformed

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the HTML Export");
                UtilsFileProcessing.openFile(UtilsHTML.exportHTML(visit, app));
                setMessage("Done with the HTML Export");
                return null;
            }
        });
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            SlideshowDialog dialog = new SlideshowDialog(app, visit, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed


    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(visit.getWildLogFileID()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    private void setupNumberOfSightingImages() {
        if (sighting != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
            if (fotos.size() > 0)
                lblNumberOfSightingImages.setText(imageSightingIndex+1 + " of " + fotos.size());
            else
                lblNumberOfSightingImages.setText("0 of 0");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnChecklist;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnGoElement;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfSightingImages;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblSightingImage;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JTable tblSightings;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JPanel visitIncludes;
    // End of variables declaration//GEN-END:variables

}
