package wildlog.ui.panels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.data.utils.UtilsData;
import wildlog.html.utils.UtilsHTML;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.MappingDialog;
import wildlog.ui.dialogs.ReportingDialog;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.dialogs.SunMoonDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilPanelGenerator;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogPrefixes;


public class PanelLocation extends PanelCanSetupHeader {
    private int imageIndex;
    private WildLogApp app;
    private Location locationWL; // Note: "location" is already used in this component... Have problem with getLocation()...


    /** Creates new form PanelLocation */
    public PanelLocation(WildLogApp inApp, Location inLocation) {
        app = inApp;
        locationWL = inLocation;
        initComponents();
        imageIndex = 0;
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(locationWL.getWildLogFileID()));
        if (fotos.size() > 0) {
            UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM));
        }
        setupNumberOfImages();

        // Setup the tables
        tblElement.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        tblVisit.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);

        // setup the file dropping
        FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                btnUpdateActionPerformed(null);
                if (!txtName.getBackground().equals(Color.RED)) {
                    imageIndex = UtilsFileProcessing.uploadFilesUsingList(locationWL.getWildLogFileID(),
                            WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_LOCATION.toString(), locationWL.getName()),
                            lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app,
                            inFiles);
                    setupNumberOfImages();
                    // everything went well - saving
                    btnUpdateActionPerformed(null);
                }
            }
        });

        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtName);
        UtilsUI.attachClipboardPopup(txtContactNumber);
        UtilsUI.attachClipboardPopup(txtHabitat);
        UtilsUI.attachClipboardPopup(txtDescription);
        UtilsUI.attachClipboardPopup(txtDirections);
        UtilsUI.attachClipboardPopup(txtEmail);
        UtilsUI.attachClipboardPopup(txtWebsite);

        // Setup info for tab headers
        tabTitle = locationWL.getName();
        tabIconURL = app.getClass().getResource("resources/icons/Location.gif");
    }

    public void setLocationWL(Location inLocation) {
        locationWL = inLocation;
    }

    public Location getLocationWL() {
        return locationWL;
    }

    @Override
    public void closeTab() {
        ((JTabbedPane)getParent()).remove(this);
    }

    // Need to look again later at listbox and how I use it...
    // Esspecially how I set the selected values...
    private int[] selectedAccommodationTypes() {
        if (locationWL.getAccommodationType() == null) return new int[0];
        int[] index = new int[locationWL.getAccommodationType().size()];
        int i = 0;
        for (int t = 0; t < AccommodationType.values().length; t++) {
            AccommodationType tempType = AccommodationType.values()[t];
            for (AccommodationType baaa : locationWL.getAccommodationType()) {
                if (baaa.test().equals(tempType.test())) index[i++] = t;
            }
        }
        return index;
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
        txtName = new javax.swing.JTextField();
        lblLocation = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        cmbRating = new javax.swing.JComboBox();
        jSeparator6 = new javax.swing.JSeparator();
        cmbGameRating = new javax.swing.JComboBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDirections = new javax.swing.JTextArea();
        txtWebsite = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstAccommodationType = new javax.swing.JList();
        txtContactNumber = new javax.swing.JTextField();
        cmbCatering = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel45 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnPreviousImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        btnGoVisit = new javax.swing.JButton();
        btnAddVisit = new javax.swing.JButton();
        btnDeleteVisit = new javax.swing.JButton();
        btnGoElement = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        btnDeleteImage = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblNumberOfVisits = new javax.swing.JLabel();
        btnMap = new javax.swing.JButton();
        rdbLocation = new javax.swing.JRadioButton();
        rdbVisit = new javax.swing.JRadioButton();
        lblNumberOfElements = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        btnHTML = new javax.swing.JButton();
        btnSunAndMoon = new javax.swing.JButton();
        btnGPS = new javax.swing.JButton();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtHabitat = new javax.swing.JTextPane();
        btnSlideshow = new javax.swing.JButton();
        btnBulkImport = new javax.swing.JButton();

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

        txtName.setBackground(new java.awt.Color(204, 255, 204));
        txtName.setText(locationWL.getName());
        txtName.setName("txtName"); // NOI18N

        lblLocation.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocation.setText(locationWL.getName());
        lblLocation.setName("lblLocation"); // NOI18N

        jLabel36.setText("Description:");
        jLabel36.setName("jLabel36"); // NOI18N

        cmbRating.setModel(new DefaultComboBoxModel(LocationRating.values()));
        cmbRating.setSelectedItem(locationWL.getRating());
        cmbRating.setFocusable(false);
        cmbRating.setName("cmbRating"); // NOI18N

        jSeparator6.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setName("jSeparator6"); // NOI18N

        cmbGameRating.setModel(new DefaultComboBoxModel(GameViewRating.values()));
        cmbGameRating.setSelectedItem(locationWL.getGameViewingRating());
        cmbGameRating.setFocusable(false);
        cmbGameRating.setName("cmbGameRating"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(locationWL.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane10.setViewportView(txtDescription);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDirections.setColumns(20);
        txtDirections.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDirections.setLineWrap(true);
        txtDirections.setRows(5);
        txtDirections.setText(locationWL.getDirections());
        txtDirections.setWrapStyleWord(true);
        txtDirections.setName("txtDirections"); // NOI18N
        jScrollPane2.setViewportView(txtDirections);

        txtWebsite.setText(locationWL.getWebsite());
        txtWebsite.setName("txtWebsite"); // NOI18N

        txtEmail.setText(locationWL.getEmail());
        txtEmail.setName("txtEmail"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstAccommodationType.setModel(new DefaultComboBoxModel(AccommodationType.values()));
        lstAccommodationType.setFocusable(false);
        lstAccommodationType.setName("lstAccommodationType"); // NOI18N
        lstAccommodationType.setSelectedIndices(selectedAccommodationTypes());
        lstAccommodationType.setSelectionBackground(new java.awt.Color(195, 223, 223));
        lstAccommodationType.setSelectionForeground(new java.awt.Color(0, 0, 0));
        lstAccommodationType.setVisibleRowCount(4);
        jScrollPane1.setViewportView(lstAccommodationType);

        txtContactNumber.setText(locationWL.getContactNumbers());
        txtContactNumber.setName("txtContactNumber"); // NOI18N

        cmbCatering.setModel(new DefaultComboBoxModel(CateringType.values()));
        cmbCatering.setSelectedItem(locationWL.getCatering());
        cmbCatering.setFocusable(false);
        cmbCatering.setName("cmbCatering"); // NOI18N

        jLabel40.setText("Habitat Type:");
        jLabel40.setName("jLabel40"); // NOI18N

        jLabel41.setText("General Rating:");
        jLabel41.setName("jLabel41"); // NOI18N

        jLabel42.setText("Wildlife Rating:");
        jLabel42.setName("jLabel42"); // NOI18N

        jLabel44.setText("List of all Creatures seen:");
        jLabel44.setName("jLabel44"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
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

        jSeparator7.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator7.setName("jSeparator7"); // NOI18N

        jLabel45.setText("Periods at this Place:");
        jLabel45.setName("jLabel45"); // NOI18N

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

        btnSetMainImage.setBackground(new java.awt.Color(233, 239, 244));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.gif"))); // NOI18N
        btnSetMainImage.setText("Default");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Place.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
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

        btnUploadImage.setBackground(new java.awt.Color(233, 239, 244));
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("Upload File");
        btnUploadImage.setToolTipText("Upload a file for this Place. You can also drag and drop files onto the above box to upload it.");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

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

        btnDeleteVisit.setBackground(new java.awt.Color(233, 239, 244));
        btnDeleteVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteVisit.setToolTipText("Delete the selected Period.");
        btnDeleteVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteVisit.setFocusPainted(false);
        btnDeleteVisit.setName("btnDeleteVisit"); // NOI18N
        btnDeleteVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVisitActionPerformed(evt);
            }
        });

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

        jLabel48.setText("Place Name:");
        jLabel48.setName("jLabel48"); // NOI18N

        jLabel1.setText("Accommodation:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText("Catering:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText("Phone No.:");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText("Email Address:");
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText("Web Site:");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText("Directions:");
        jLabel6.setName("jLabel6"); // NOI18N

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

        jSeparator1.setName("jSeparator1"); // NOI18N

        jLabel10.setText("GPS:");
        jLabel10.setName("jLabel10"); // NOI18N

        btnDeleteImage.setBackground(new java.awt.Color(233, 239, 244));
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

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel7.setText("Period:");
        jLabel7.setName("jLabel7"); // NOI18N

        lblNumberOfVisits.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfVisits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N

        btnMap.setBackground(new java.awt.Color(233, 239, 244));
        btnMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMap.setText("View Maps");
        btnMap.setToolTipText("Show available maps for this Place.");
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

        rdbVisit.setBackground(new java.awt.Color(233, 239, 244));
        buttonGroup1.add(rdbVisit);
        rdbVisit.setText("Period");
        rdbVisit.setToolTipText("View all Creatures observed during the selected Period.");
        rdbVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbVisit.setFocusPainted(false);
        rdbVisit.setName("rdbVisit"); // NOI18N

        lblNumberOfElements.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        btnReport.setBackground(new java.awt.Color(233, 239, 244));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.gif"))); // NOI18N
        btnReport.setText("View Reports");
        btnReport.setToolTipText("View reports for this Place.");
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

        btnHTML.setBackground(new java.awt.Color(233, 239, 244));
        btnHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        btnHTML.setText("View HTML");
        btnHTML.setToolTipText("View the HTML export for this Place.");
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setFocusPainted(false);
        btnHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHTML.setIconTextGap(5);
        btnHTML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });

        btnSunAndMoon.setBackground(new java.awt.Color(233, 239, 244));
        btnSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon_big.png"))); // NOI18N
        btnSunAndMoon.setText("Sun and Moon");
        btnSunAndMoon.setToolTipText("Show the Sun and Moon information for this Place.");
        btnSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSunAndMoon.setFocusPainted(false);
        btnSunAndMoon.setIconTextGap(5);
        btnSunAndMoon.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnSunAndMoon.setName("btnSunAndMoon"); // NOI18N
        btnSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSunAndMoonActionPerformed(evt);
            }
        });

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

        txtLatitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLatitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N

        txtLongitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLongitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtHabitat.setText(locationWL.getHabitatType());
        txtHabitat.setName("txtHabitat"); // NOI18N
        jScrollPane3.setViewportView(txtHabitat);

        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images.");
        btnSlideshow.setFocusPainted(false);
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 6, 2, 8));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });

        btnBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Bulk Import.png"))); // NOI18N
        btnBulkImport.setText("Bulk Import");
        btnBulkImport.setToolTipText("Open a Bulk Import tab to add a new Period to this Place.");
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setFocusPainted(false);
        btnBulkImport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBulkImport.setIconTextGap(5);
        btnBulkImport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout locationIncludesLayout = new javax.swing.GroupLayout(locationIncludes);
        locationIncludes.setLayout(locationIncludesLayout);
        locationIncludesLayout.setHorizontalGroup(
            locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel45)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(lblLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel7)
                                .addGap(4, 4, 4)
                                .addComponent(lblNumberOfVisits, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(locationIncludesLayout.createSequentialGroup()
                                            .addGap(85, 85, 85)
                                            .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(locationIncludesLayout.createSequentialGroup()
                                            .addGap(225, 225, 225)
                                            .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(locationIncludesLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(locationIncludesLayout.createSequentialGroup()
                                            .addGap(85, 85, 85)
                                            .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                                .addComponent(jLabel42)
                                                .addGap(7, 7, 7)
                                                .addComponent(cmbGameRating, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                                .addComponent(jLabel41)
                                                .addGap(4, 4, 4)
                                                .addComponent(cmbRating, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel6))
                                        .addGap(4, 4, 4)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGap(120, 120, 120))))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addComponent(jLabel48)
                                        .addGap(16, 16, 16)
                                        .addComponent(txtName)
                                        .addGap(10, 10, 10)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(btnSunAndMoon, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(btnUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel36)
                                            .addComponent(jLabel40)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2))
                                        .addGap(5, 5, 5)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbCatering, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtWebsite, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(10, 10, 10)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, locationIncludesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGoVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(160, 160, 160)
                                        .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(rdbVisit))
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(120, 120, 120)
                                        .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(rdbLocation)))
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator7))
                .addGap(5, 5, 5))
        );
        locationIncludesLayout.setVerticalGroup(
            locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblNumberOfVisits, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(1, 1, 1)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel48))))
                                .addGap(4, 4, 4)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(cmbRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel41))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel42)
                                            .addComponent(cmbGameRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(8, 8, 8)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addGap(76, 76, 76)
                                .addComponent(jLabel40)
                                .addGap(51, 51, 51)
                                .addComponent(jLabel5)
                                .addGap(11, 11, 11)
                                .addComponent(jLabel4)
                                .addGap(11, 11, 11)
                                .addComponent(jLabel3)
                                .addGap(11, 11, 11)
                                .addComponent(jLabel2))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(btnHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(76, 76, 76)
                                        .addComponent(jLabel6))))
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane1)
                                    .addComponent(jScrollPane10))
                                .addGap(5, 5, 5)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(txtWebsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(txtContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(cmbCatering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(btnUploadImage)
                                .addGap(3, 3, 3)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSetMainImage)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnDeleteImage)))
                            .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSunAndMoon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumberOfElements, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdbLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(locationIncludesLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addGap(5, 5, 5)
                                .addGroup(locationIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(locationIncludesLayout.createSequentialGroup()
                                        .addComponent(btnGoVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(btnAddVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addComponent(jSeparator6)
                            .addGroup(locationIncludesLayout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addGap(155, 155, 155)
                                .addComponent(btnDeleteVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)))))
                .addGap(5, 5, 5))
            .addGroup(locationIncludesLayout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(btnBulkImport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(locationIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtLatitude.setText(UtilsGps.getLatitudeString(locationWL));
        txtLongitude.setText(UtilsGps.getLongitudeString(locationWL));

        rdbLocation.setSelected(true);
        //if (locationWL.getSubAreas().size() > 1) cmbSubAreas.setSelectedIndex(1);

        if (locationWL.getName() != null) {
            Visit tempVisit = new Visit();
            tempVisit.setLocationName(locationWL.getName());
            List<Visit> visits = app.getDBI().list(tempVisit);
            lblNumberOfVisits.setText(Integer.toString(visits.size()));
            UtilTableGenerator.setupCompleteVisitTable(app, tblVisit, locationWL);
            UtilTableGenerator.setupElementsForLocationTable(app, tblElement, locationWL);
        }
        else {
            lblNumberOfVisits.setText("0");
            tblVisit.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
            tblElement.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
        }

        lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkImportActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                UtilPanelGenerator.openBulkUploadTab(new BulkUploadPanel(app, this, locationWL.getName()), (JTabbedPane)getParent());
                return null;
            }
        });
    }//GEN-LAST:event_btnBulkImportActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            SlideshowDialog dialog = new SlideshowDialog(app, null, locationWL, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app, app.getMainFrame(), locationWL);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(UtilsGps.getLatitudeString(locationWL));
            txtLongitude.setText(UtilsGps.getLongitudeString(locationWL));
        }
        btnUpdate.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void btnSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSunAndMoonActionPerformed
        SunMoonDialog dialog = new SunMoonDialog(app, locationWL);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnSunAndMoonActionPerformed

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the HTML Export");
                UtilsFileProcessing.openFile(UtilsHTML.exportHTML(locationWL, app));
                setMessage("Done with the HTML Export");
                return null;
            }
        });
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            ReportingDialog dialog = new ReportingDialog(app, locationWL, null, null, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void rdbLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbLocationItemStateChanged
        if (locationWL.getName() != null) {
            if (rdbLocation.isSelected()) {
                UtilTableGenerator.setupElementsForLocationTable(app, tblElement, locationWL);
            }
            else {
                if  (tblVisit.getSelectedRowCount() == 1) {
                    UtilTableGenerator.setupElementsForVisitTable(app, tblElement, app.getDBI().find(new Visit((String)tblVisit.getValueAt(tblVisit.getSelectedRow(), 0))));
                }
                else tblElement.setModel(new DefaultTableModel(new String[]{"Please Selected a Period"}, 0));
            }
            lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
        }
        else {
            lblNumberOfElements.setText("0");
        }
    }//GEN-LAST:event_rdbLocationItemStateChanged

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        if (locationWL.getName() != null && !locationWL.getName().isEmpty()) {
            MappingDialog dialog = new MappingDialog(app, locationWL, null, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(locationWL.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(locationWL.getWildLogFileID(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        int[] selectedRows = tblElement.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilPanelGenerator.openPanelAsTab(app, (String)tblElement.getValueAt(selectedRows[t], 0), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent(), null);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnDeleteVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVisitActionPerformed
        if (tblVisit.getSelectedRowCount() > 0) {
            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "Are you sure you want to delete the Period(s)? This will delete all Observations and files linked to the Period(s) as well.",
                        "Delete Period(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            });
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblVisit.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    Visit visit = app.getDBI().find(new Visit((String)tblVisit.getValueAt(selectedRows[t], 0)));
                    UtilPanelGenerator.removeOpenedTab(visit.getName(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent());
                    app.getDBI().delete(visit);
                }
                formComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteVisitActionPerformed

    private void btnAddVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVisitActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            UtilPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent(), locationWL);
        }
    }//GEN-LAST:event_btnAddVisitActionPerformed

    private void btnGoVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisitActionPerformed
        int[] selectedRows = tblVisit.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilPanelGenerator.openPanelAsTab(app, (String)tblVisit.getValueAt(selectedRows[t], 0), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent(), locationWL);
        }
    }//GEN-LAST:event_btnGoVisitActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            imageIndex = UtilsFileProcessing.uploadFilesUsingList(locationWL.getWildLogFileID(),
                WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_LOCATION.toString(), locationWL.getName()),
                lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app,
                UtilsFileProcessing.showFileUploadDialog(app));
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(locationWL.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(locationWL.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(locationWL.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = locationWL.getName();
                locationWL.setName(UtilsData.limitLength(txtName.getText(), 100));
                locationWL.setHabitatType(txtHabitat.getText());
                locationWL.setDescription(txtDescription.getText());
                locationWL.setRating((LocationRating)cmbRating.getSelectedItem());
                locationWL.setCatering((CateringType)cmbCatering.getSelectedItem());
                locationWL.setGameViewingRating((GameViewRating)cmbGameRating.getSelectedItem());
                locationWL.setContactNumbers(txtContactNumber.getText());
                locationWL.setEmail(txtEmail.getText());
                locationWL.setWebsite(txtWebsite.getText());
                locationWL.setDirections(txtDirections.getText());
                locationWL.setAccommodationType(lstAccommodationType.getSelectedValuesList());
                // NOTE: The GPS info is already set on the Location object by the GPS popup component

                // Save the location
                if (app.getDBI().createOrUpdate(locationWL, oldName) == true) {
                    txtName.setBackground(new java.awt.Color(204, 255, 204));
                    txtName.setText(locationWL.getName());
                }
                else {
                    txtName.setBackground(Color.RED);
                    locationWL.setName(oldName);
                    txtName.setText(txtName.getText() + "_not_unique");
                }

                lblLocation.setText(locationWL.getName());

                tabTitle = locationWL.getName();
                setupTabHeader(PanelCanSetupHeader.TabTypes.LOCATION);
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

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btnGoElementActionPerformed(null);
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblVisitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
        tblVisitMouseReleased(null);
    }//GEN-LAST:event_tblVisitKeyReleased

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        btnGoVisitActionPerformed(null);
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoVisitActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        rdbLocationItemStateChanged(null);
    }//GEN-LAST:event_tblVisitMouseReleased

    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(locationWL.getWildLogFileID()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVisit;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteVisit;
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoVisit;
    private javax.swing.JButton btnHTML;
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
    private javax.swing.JComboBox cmbCatering;
    private javax.swing.JComboBox cmbGameRating;
    private javax.swing.JComboBox cmbRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JPanel locationIncludes;
    private javax.swing.JList lstAccommodationType;
    private javax.swing.JRadioButton rdbLocation;
    private javax.swing.JRadioButton rdbVisit;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDirections;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextPane txtHabitat;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtWebsite;
    // End of variables declaration//GEN-END:variables

}
