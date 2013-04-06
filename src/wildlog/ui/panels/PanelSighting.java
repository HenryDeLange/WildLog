package wildlog.ui.panels;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeFormat;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WildLogFileType;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogPrefixes;


public class PanelSighting extends JDialog {
    private Location location;
    private Visit visit;
    //private Visit oldVisit;
    private Element element;
    private Sighting sighting;
    private Element searchElement;
    private Location searchLocation;
    private int imageIndex;
    private WildLogApp app;
    private PanelNeedsRefreshWhenSightingAdded panelToRefresh;
    private boolean treatAsNewSighting;
    private boolean disableEditing = false;
    private boolean bulkUploadMode = false;

    // Constructor
    /**
     * Create a new PanelSighting object. This panel can function in different ways
     * depending on the input provided as parameters to this constructor.
     * NOTE: The passed in Sighting should not ever be null.
     * For a new Sighting pass in a new object instead.
     * @param inSighting - Should never be null.
     * @param inLocation - Can be null if unknown.
     * @param inVisit - Can be null if unknown.
     * @param inElement - Can be null if unknown.
     * @param inPanelToRefresh - The object to call refresh on when the sighting is done processing.
     * @param inTreatAsNewSighting
     * @param inDisableEditing
     * @param inBulkUploadMode
     */
    public PanelSighting(JFrame inOwner, String inTitle, Sighting inSighting, Location inLocation, Visit inVisit,
            Element inElement, PanelNeedsRefreshWhenSightingAdded inPanelToRefresh,
            boolean inTreatAsNewSighting, boolean inDisableEditing, boolean inBulkUploadMode) {
        super(inOwner, inTitle);
        if (inSighting == null) {
            System.err.println("PanelSighting: The passed in Sighting is not allowed to be null.");
            this.closeThisDialog();
        }
        sighting = inSighting;
        panelToRefresh = inPanelToRefresh;
        treatAsNewSighting = inTreatAsNewSighting;
        disableEditing = inDisableEditing;
        bulkUploadMode = inBulkUploadMode;
        app = (WildLogApp) Application.getInstance();
        location = inLocation;
        visit = inVisit;
        element = inElement;
        searchElement = new Element();
        searchLocation = new Location();
        imageIndex = 0;
        // Auto-generated code
        initComponents();

        // Setup tables
        UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
        UtilTableGenerator.setupShortLocationTable(tblLocation, searchLocation);

        // Setup default values for tables
        if (location != null) {
            int select = -1;
            for (int t = 0; t < tblLocation.getModel().getRowCount(); t++) {
                if (tblLocation.getValueAt(t, 0).equals(location.getName()))
                    select = t;
            }
            if (select >= 0) {
                tblLocation.getSelectionModel().setSelectionInterval(select, select);
                if (select > 3)
                    tblLocation.scrollRectToVisible(tblLocation.getCellRect(select-3, 0, true));
            }
        }
        if (element != null) {
            int select = -1;
            for (int t = 0; t < tblElement.getModel().getRowCount(); t++) {
                if (tblElement.getValueAt(t, 0).equals(element.getPrimaryName()))
                    select = t;
            }
            if (select >= 0) {
                tblElement.getSelectionModel().setSelectionInterval(select, select);
                if (select > 5)
                    tblElement.scrollRectToVisible(tblElement.getCellRect(select-5, 0, true));
            }
        }
        if (location != null && visit != null) {
            // Build the table
            UtilTableGenerator.setupVeryShortVisitTable(tblVisit, location);
            // Select the visit
            int select = -1;
            for (int t = 0; t < tblVisit.getModel().getRowCount(); t++) {
                if (tblVisit.getValueAt(t, 0).equals(visit.getName()))
                    select = t;
            }
            if (select >= 0) {
                tblVisit.getSelectionModel().setSelectionInterval(select, select);
                if (select > 2)
                    tblVisit.scrollRectToVisible(tblVisit.getCellRect(select-2, 0, true));
            }
        }
        else {
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Place"}, 0));
        }

        // Setup location and element images
        if (location != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(location.getWildLogFileID()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto(location.getWildLogFileID(), 0, lblLocationImage, 100, app);
            else
                lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
        else {
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
        if (element != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(element.getWildLogFileID()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, lblElementImage, 100, app);
            else
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }

        //Setup Table ordering and sorting
        tblElement.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        tblLocation.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        tblVisit.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);

        // Lat Lon stuff
        txtLatitude.setText(UtilsGps.getLatitudeString(sighting));
        txtLongitude.setText(UtilsGps.getLatitudeString(sighting));

        // Spinners stuff
        SpinnerFixer.fixSelectAllForSpinners(spnNumberOfElements);
        SpinnerFixer.fixSelectAllForSpinners(spnHours);
        SpinnerFixer.fixSelectAllForSpinners(spnMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnMoonPhase);
        SpinnerFixer.fixSelectAllForSpinners(spnTemperature);
        SpinnerFixer.fixSelectAllForSpinners(spnDurationMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnDurationSeconds);

        if (!disableEditing && !bulkUploadMode) {
            // Only enable drag-and-drop if editing is allowed and not in bulk upload mode
            FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
                @Override
                public void filesDropped(List<File> inFiles) {
                    uploadImage(inFiles);
                }
            });

            // Attach clipboard
            UtilsUI.attachClipboardPopup(txtSearch);
            UtilsUI.attachClipboardPopup(txtSearchLocation);
            UtilsUI.attachClipboardPopup(txtDetails);
            UtilsUI.attachClipboardPopup((JTextComponent)spnHours.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnMinutes.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnMoonPhase.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnNumberOfElements.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnTemperature.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnDurationMinutes.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnDurationSeconds.getEditor().getComponent(0));

            // Setup searcher
            UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
            UtilsUI.attachKeyListernerToFilterTableRows(txtSearchLocation, tblLocation);
        }

        // Setup the image count label
        setupNumberOfImages();

        // Setup default values for input fields
        if (treatAsNewSighting) {
            cmbCertainty.setSelectedItem(Certainty.SURE);
            cmbEvidence.setSelectedItem(SightingEvidence.SEEN);
            cmbViewRating.setSelectedItem(ViewRating.NORMAL);
            cmbLifeStatus.setSelectedItem(LifeStatus.ALIVE);
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM));
            spnNumberOfElements.setValue(0);
            spnMoonPhase.setValue(-1);
            spnTemperature.setValue(0);
        }
        else {
            // Setup the Sighting info
            setupSightingInfo();
        }

        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        ActionListener escListiner = UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        // Hack to fix the wierd focus issue to get the ESC to work (related to the datepicker)
        this.setFocusable(true);
        dtpSightingDate.getEditor().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        // Make dates pretty
        dtpSightingDate.getComponent(1).setBackground(sightingIncludes.getBackground());
    }

    private void setupSightingInfo() {
        // Display the ID
        lblSightingID.setText("Observation ID: " + Long.toString(sighting.getSightingCounter()));
        // Load the values from the Sighting object
        setUIFieldsFromSightingDate();
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        txtDetails.setText(sighting.getDetails());
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        spnNumberOfElements.setValue(sighting.getNumberOfElements());
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbWeather.setSelectedItem(sighting.getWeather());
        txtLatitude.setText(UtilsGps.getLatitudeString(sighting));
        txtLongitude.setText(UtilsGps.getLongitudeString(sighting));
        spnMoonPhase.setValue(sighting.getMoonPhase());
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        spnTemperature.setValue(sighting.getTemperature());
        cmbTemperatureUnits.setSelectedItem(sighting.getUnitsTemperature());
        txtTag.setText(sighting.getTag());
        cmbLifeStatus.setSelectedItem(sighting.getLifeStatus());
        cmbSex.setSelectedItem(sighting.getSex());
        spnDurationMinutes.setValue(sighting.getDurationMinutes());
        spnDurationSeconds.setValue(sighting.getDurationSeconds());
        // Setup the sighting's image
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
        if (fotos.size() > 0)
            UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        else
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM));
    }

    private void setUIFieldsFromSightingDate() {
        dtpSightingDate.setDate(sighting.getDate());
        if (sighting.getDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sighting.getDate());
            spnHours.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            spnMinutes.setValue(calendar.get(Calendar.MINUTE));
        }
        else {
            spnHours.setValue(0);
            spnMinutes.setValue(0);
        }
        if (!sighting.isTimeUnknown())
            cmbTimeFormat.setSelectedItem(TimeFormat.H24);
        else
            cmbTimeFormat.setSelectedItem(TimeFormat.UNKNOWN);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sightingIncludes = new javax.swing.JPanel();
        btnUpdateSighting = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        sclElement = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator9 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        dtpSightingDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetails = new javax.swing.JTextArea();
        cmbWeather = new javax.swing.JComboBox();
        cmbTimeOfDay = new javax.swing.JComboBox();
        cmbViewRating = new javax.swing.JComboBox();
        cmbCertainty = new javax.swing.JComboBox();
        lblElementImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        cmbElementType = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnDeleteImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cmbEvidence = new javax.swing.JComboBox();
        sclLocation = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        sclVisit = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        lblElement = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        txtSearchLocation = new javax.swing.JTextField();
        lblVisit = new javax.swing.JLabel();
        lblLocationImage = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbTimeFormat = new javax.swing.JComboBox();
        lblSightingID = new javax.swing.JLabel();
        spnNumberOfElements = new javax.swing.JSpinner();
        btnGetDateFromImage = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnCalculateSunAndMoon = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        cmbMoonlight = new javax.swing.JComboBox();
        spnHours = new javax.swing.JSpinner();
        spnMinutes = new javax.swing.JSpinner();
        spnMoonPhase = new javax.swing.JSpinner();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        btnGPS = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        spnTemperature = new javax.swing.JSpinner();
        cmbTemperatureUnits = new javax.swing.JComboBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        cmbSex = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cmbLifeStatus = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        txtTag = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        spnDurationMinutes = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        spnDurationSeconds = new javax.swing.JSpinner();
        jLabel21 = new javax.swing.JLabel();
        btnCalculateDuration = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Observation");
        setBackground(new java.awt.Color(208, 204, 181));
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(955, 615));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sightingIncludes.setBackground(new java.awt.Color(208, 204, 181));
        sightingIncludes.setMaximumSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnUpdateSighting.setBackground(new java.awt.Color(0, 204, 51));
        btnUpdateSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdateSighting.setToolTipText("Save and update the Observation.");
        btnUpdateSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateSighting.setEnabled(!disableEditing);
        btnUpdateSighting.setFocusPainted(false);
        btnUpdateSighting.setName("btnUpdateSighting"); // NOI18N
        btnUpdateSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSightingActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUpdateSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 110, 60));

        jSeparator8.setName("jSeparator8"); // NOI18N
        sightingIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        sclElement.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclElement.setName("sclElement"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblElement.setEnabled(!disableEditing);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        sclElement.setViewportView(tblElement);

        sightingIncludes.add(sclElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 280, 230));

        jSeparator9.setName("jSeparator9"); // NOI18N
        sightingIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        btnPreviousImage.setBackground(new java.awt.Color(208, 204, 181));
        btnPreviousImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImage.setToolTipText("Load previous file.");
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 310, 40, 50));

        btnNextImage.setBackground(new java.awt.Color(208, 204, 181));
        btnNextImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImage.setToolTipText("Load next file.");
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 310, 40, 50));

        btnUploadImage.setBackground(new java.awt.Color(208, 204, 181));
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("Upload File");
        btnUploadImage.setToolTipText("Upload a file for this Observation. You can also drag and drop files onto the above box to upload it.");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setEnabled(!disableEditing && !bulkUploadMode);
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 310, 220, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Date:");
        jLabel6.setToolTipText("It is required to fill in this field.");
        jLabel6.setName("jLabel6"); // NOI18N
        sightingIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        dtpSightingDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setEnabled(!disableEditing);
        dtpSightingDate.setFocusable(false);
        dtpSightingDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        sightingIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 150, -1));

        jLabel8.setText("Weather:");
        jLabel8.setName("jLabel8"); // NOI18N
        sightingIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, 20));

        jLabel9.setText("Rating:");
        jLabel9.setName("jLabel9"); // NOI18N
        sightingIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, -1, 20));

        jLabel10.setText("Number:");
        jLabel10.setName("jLabel10"); // NOI18N
        sightingIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, 20));

        jLabel12.setText("Certainty:");
        jLabel12.setName("jLabel12"); // NOI18N
        sightingIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 130, -1, 20));

        jLabel13.setText("Details:");
        jLabel13.setName("jLabel13"); // NOI18N
        sightingIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 220, -1, 20));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDetails.setColumns(20);
        txtDetails.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(sighting.getDetails());
        txtDetails.setWrapStyleWord(true);
        txtDetails.setEnabled(!disableEditing);
        txtDetails.setName("txtDetails"); // NOI18N
        jScrollPane2.setViewportView(txtDetails);

        sightingIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 220, 260, 70));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setEnabled(!disableEditing);
        cmbWeather.setFocusable(false);
        cmbWeather.setName("cmbWeather"); // NOI18N
        sightingIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 270, 210, -1));

        cmbTimeOfDay.setMaximumRowCount(9);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setEnabled(!disableEditing);
        cmbTimeOfDay.setFocusable(false);
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N
        sightingIncludes.add(cmbTimeOfDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 210, 20));

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setEnabled(!disableEditing);
        cmbViewRating.setFocusable(false);
        cmbViewRating.setName("cmbViewRating"); // NOI18N
        sightingIncludes.add(cmbViewRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 160, 140, -1));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setEnabled(!disableEditing);
        cmbCertainty.setFocusable(false);
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        sightingIncludes.add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 130, 140, -1));

        lblElementImage.setBackground(new java.awt.Color(0, 0, 0));
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setToolTipText("The default image of the selected Creature.");
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 370, -1, -1));

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
        sightingIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, -1));

        cmbElementType.setMaximumRowCount(9);
        cmbElementType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementType.setSelectedItem(ElementType.NONE);
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setEnabled(!disableEditing);
        cmbElementType.setFocusable(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        sightingIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 330, 90, -1));

        jLabel18.setText("GPS:");
        jLabel18.setName("jLabel18"); // NOI18N
        sightingIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 20));

        txtSearch.setEnabled(!disableEditing);
        txtSearch.setName("txtSearch"); // NOI18N
        sightingIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 190, 20));

        btnDeleteImage.setBackground(new java.awt.Color(208, 204, 181));
        btnDeleteImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteImage.setText("Delete File");
        btnDeleteImage.setToolTipText("Delete the current file.");
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setEnabled(!disableEditing && !bulkUploadMode);
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 335, 90, -1));

        btnSetMainImage.setBackground(new java.awt.Color(208, 204, 181));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.gif"))); // NOI18N
        btnSetMainImage.setText("Default");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Observation.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setEnabled(!disableEditing && !bulkUploadMode);
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 335, 90, -1));

        jLabel4.setText("Evidence:");
        jLabel4.setName("jLabel4"); // NOI18N
        sightingIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, -1, 20));

        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setEnabled(!disableEditing);
        cmbEvidence.setFocusable(false);
        cmbEvidence.setName("cmbEvidence"); // NOI18N
        sightingIncludes.add(cmbEvidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 100, 140, -1));

        sclLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclLocation.setName("sclLocation"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblLocation.setEnabled(!disableEditing && !bulkUploadMode);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        tblLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        sclLocation.setViewportView(tblLocation);

        sightingIncludes.add(sclLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, 210, 230));

        sclVisit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclVisit.setName("sclVisit"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblVisit.setEnabled(!disableEditing && !bulkUploadMode);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        sclVisit.setViewportView(tblVisit);

        sightingIncludes.add(sclVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 390, 310, 190));

        lblElement.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblElement.setText("Creature:");
        lblElement.setToolTipText("It is required to fill in this field.");
        lblElement.setName("lblElement"); // NOI18N
        sightingIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        lblLocation.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblLocation.setText("Place:");
        lblLocation.setToolTipText("It is required to fill in this field.");
        lblLocation.setName("lblLocation"); // NOI18N
        sightingIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 310, -1, -1));

        txtSearchLocation.setEnabled(!disableEditing && !bulkUploadMode);
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N
        sightingIncludes.add(txtSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 330, 210, 20));

        lblVisit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblVisit.setText("Period:");
        lblVisit.setToolTipText("It is required to fill in this field.");
        lblVisit.setName("lblVisit"); // NOI18N
        sightingIncludes.add(lblVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 370, -1, -1));

        lblLocationImage.setBackground(new java.awt.Color(0, 0, 0));
        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setToolTipText("The default image of the selected Place.");
        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setOpaque(true);
        lblLocationImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblLocationImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLocationImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblLocationImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 480, -1, -1));

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        sightingIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 335, 40, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(":");
        jLabel1.setName("jLabel1"); // NOI18N
        sightingIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 10, 20));

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setEnabled(!disableEditing);
        cmbTimeFormat.setFocusable(false);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        sightingIncludes.add(cmbTimeFormat, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 110, 20));

        lblSightingID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingID.setName("lblSightingID"); // NOI18N
        sightingIncludes.add(lblSightingID, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 340, 120, 20));

        spnNumberOfElements.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spnNumberOfElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnNumberOfElements.setEditor(new javax.swing.JSpinner.NumberEditor(spnNumberOfElements, "#"));
        spnNumberOfElements.setEnabled(!disableEditing);
        spnNumberOfElements.setName("spnNumberOfElements"); // NOI18N
        sightingIncludes.add(spnNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 70, -1));

        btnGetDateFromImage.setBackground(new java.awt.Color(208, 204, 181));
        btnGetDateFromImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF_small.png"))); // NOI18N
        btnGetDateFromImage.setText("<html>Load <b>Date</b> from <b>Image </b>EXIF</html>");
        btnGetDateFromImage.setToolTipText("Attempt to load the date from the image's EXIF data.");
        btnGetDateFromImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetDateFromImage.setEnabled(!disableEditing && !bulkUploadMode);
        btnGetDateFromImage.setFocusPainted(false);
        btnGetDateFromImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGetDateFromImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnGetDateFromImage.setName("btnGetDateFromImage"); // NOI18N
        btnGetDateFromImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetDateFromImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnGetDateFromImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 75, 110, 40));

        jLabel2.setText("Time:");
        jLabel2.setName("jLabel2"); // NOI18N
        sightingIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        jLabel3.setText("Moon Phase:");
        jLabel3.setName("jLabel3"); // NOI18N
        sightingIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, 20));

        jLabel5.setText("% Full");
        jLabel5.setName("jLabel5"); // NOI18N
        sightingIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 180, 40, 20));

        btnCalculateSunAndMoon.setBackground(new java.awt.Color(208, 204, 181));
        btnCalculateSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon.gif"))); // NOI18N
        btnCalculateSunAndMoon.setText("<html>Calculate <b>Time of Day</b> and <b> Moon Phase</b></html>");
        btnCalculateSunAndMoon.setToolTipText("Automatically calculate the Sun and Moon phase based on the date and GPS co-ordinates.");
        btnCalculateSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCalculateSunAndMoon.setEnabled(!disableEditing);
        btnCalculateSunAndMoon.setFocusPainted(false);
        btnCalculateSunAndMoon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalculateSunAndMoon.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCalculateSunAndMoon.setName("btnCalculateSunAndMoon"); // NOI18N
        btnCalculateSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateSunAndMoonActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnCalculateSunAndMoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, 110, 45));

        jLabel11.setText("Moonlight:");
        jLabel11.setName("jLabel11"); // NOI18N
        sightingIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, 20));

        cmbMoonlight.setModel(new DefaultComboBoxModel(Moonlight.values()));
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        cmbMoonlight.setEnabled(!disableEditing);
        cmbMoonlight.setFocusable(false);
        cmbMoonlight.setName("cmbMoonlight"); // NOI18N
        sightingIncludes.add(cmbMoonlight, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 210, 210, -1));

        spnHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnHours.setEditor(new javax.swing.JSpinner.NumberEditor(spnHours, "##"));
        spnHours.setEnabled(!disableEditing);
        spnHours.setFocusable(false);
        spnHours.setName("spnHours"); // NOI18N
        sightingIncludes.add(spnHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 40, -1));

        spnMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMinutes.setEditor(new javax.swing.JSpinner.NumberEditor(spnMinutes, "##"));
        spnMinutes.setEnabled(!disableEditing);
        spnMinutes.setFocusable(false);
        spnMinutes.setName("spnMinutes"); // NOI18N
        sightingIncludes.add(spnMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 40, -1));

        spnMoonPhase.setModel(new javax.swing.SpinnerNumberModel(0, -1, 100, 1));
        spnMoonPhase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMoonPhase.setEditor(new javax.swing.JSpinner.NumberEditor(spnMoonPhase, "##"));
        spnMoonPhase.setEnabled(!disableEditing);
        spnMoonPhase.setName("spnMoonPhase"); // NOI18N
        sightingIncludes.add(spnMoonPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, 67, -1));

        txtLatitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N
        sightingIncludes.add(txtLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 105, -1));

        txtLongitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N
        sightingIncludes.add(txtLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 105, -1));

        btnGPS.setBackground(new java.awt.Color(208, 204, 181));
        btnGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPS.setText("Change GPS");
        btnGPS.setToolTipText("Select a new GPS value for this Observation.");
        btnGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPS.setEnabled(!disableEditing);
        btnGPS.setFocusPainted(false);
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 100, 40));

        jLabel7.setText("Temperature:");
        jLabel7.setName("jLabel7"); // NOI18N
        sightingIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, 20));

        spnTemperature.setModel(new javax.swing.SpinnerNumberModel(0.0d, -130.0d, 140.0d, 1.0d));
        spnTemperature.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnTemperature.setEditor(new javax.swing.JSpinner.NumberEditor(spnTemperature, "###.##"));
        spnTemperature.setEnabled(!disableEditing);
        spnTemperature.setName("spnTemperature"); // NOI18N
        sightingIncludes.add(spnTemperature, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 60, -1));

        cmbTemperatureUnits.setModel(new DefaultComboBoxModel(UnitsTemperature.values()));
        cmbTemperatureUnits.setSelectedItem(sighting.getUnitsTemperature());
        cmbTemperatureUnits.setEnabled(!disableEditing);
        cmbTemperatureUnits.setFocusable(false);
        cmbTemperatureUnits.setName("cmbTemperatureUnits"); // NOI18N
        sightingIncludes.add(cmbTemperatureUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 240, 140, -1));

        jSeparator3.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setName("jSeparator3"); // NOI18N
        sightingIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 305, 10, 62));

        jLabel14.setText("Time of day:");
        jLabel14.setName("jLabel14"); // NOI18N
        sightingIncludes.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 20));

        jSeparator4.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator4.setName("jSeparator4"); // NOI18N
        sightingIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 365, 425, 10));

        jSeparator5.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator5.setName("jSeparator5"); // NOI18N
        sightingIncludes.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 305, 505, 10));

        jLabel15.setText("Sex:");
        jLabel15.setName("jLabel15"); // NOI18N
        sightingIncludes.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, -1, 20));

        cmbSex.setModel(new DefaultComboBoxModel(Sex.values()));
        cmbSex.setSelectedItem(sighting.getSex());
        cmbSex.setEnabled(!disableEditing);
        cmbSex.setFocusable(false);
        cmbSex.setName("cmbSex"); // NOI18N
        sightingIncludes.add(cmbSex, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 140, -1));

        jLabel16.setText("Status:");
        jLabel16.setName("jLabel16"); // NOI18N
        sightingIncludes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, -1, 20));

        cmbLifeStatus.setModel(new DefaultComboBoxModel(LifeStatus.values()));
        cmbLifeStatus.setSelectedItem(sighting.getLifeStatus());
        cmbLifeStatus.setEnabled(!disableEditing);
        cmbLifeStatus.setFocusable(false);
        cmbLifeStatus.setName("cmbLifeStatus"); // NOI18N
        sightingIncludes.add(cmbLifeStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 140, -1));

        jLabel17.setText("Info  Tag:");
        jLabel17.setName("jLabel17"); // NOI18N
        sightingIncludes.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 190, -1, 20));

        txtTag.setText(sighting.getTag());
        txtTag.setToolTipText("This field can be used to \"tag\" the sighting. For instance using it for the individual animal's ID, or a sub-location name, etc.");
        txtTag.setEnabled(!disableEditing);
        txtTag.setName("txtTag"); // NOI18N
        sightingIncludes.add(txtTag, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 190, 140, -1));

        jLabel19.setText("Duration:");
        jLabel19.setName("jLabel19"); // NOI18N
        sightingIncludes.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 20));

        spnDurationMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1440, 1));
        spnDurationMinutes.setEnabled(!disableEditing);
        spnDurationMinutes.setName("spnDurationMinutes"); // NOI18N
        sightingIncludes.add(spnDurationMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 50, -1));

        jLabel20.setText("minutes");
        jLabel20.setName("jLabel20"); // NOI18N
        sightingIncludes.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 100, -1, 20));

        spnDurationSeconds.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 60.0d, 1.0d));
        spnDurationSeconds.setEnabled(!disableEditing);
        spnDurationSeconds.setName("spnDurationSeconds"); // NOI18N
        sightingIncludes.add(spnDurationSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 50, -1));

        jLabel21.setText("seconds");
        jLabel21.setName("jLabel21"); // NOI18N
        sightingIncludes.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 100, -1, 20));

        btnCalculateDuration.setBackground(new java.awt.Color(208, 204, 181));
        btnCalculateDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Duration.gif"))); // NOI18N
        btnCalculateDuration.setText("<html>Calculate <b>Duration</b> from <b>Image</b> EXIF</html>");
        btnCalculateDuration.setToolTipText("Attempt to calculate the Duration of the Observation based on times specified on the uploaded images.");
        btnCalculateDuration.setEnabled(!disableEditing && !bulkUploadMode);
        btnCalculateDuration.setFocusPainted(false);
        btnCalculateDuration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalculateDuration.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCalculateDuration.setName("btnCalculateDuration"); // NOI18N
        btnCalculateDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateDurationActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnCalculateDuration, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 170, 110, 45));

        getContentPane().add(sightingIncludes, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        // Check the required fields' borders
        Color green = new Color(0,204,51);
        if (element == null)
            sclElement.setBorder(new LineBorder(Color.RED, 2));
        else
            sclElement.setBorder(new LineBorder(green, 1));
        if (location == null)
            sclLocation.setBorder(new LineBorder(Color.RED, 2));
        else
            sclLocation.setBorder(new LineBorder(green, 1));
        if (visit == null)
            sclVisit.setBorder(new LineBorder(Color.RED, 2));
        else
            sclVisit.setBorder(new LineBorder(green, 1));
        if (dtpSightingDate.getDate() == null)
            dtpSightingDate.setBorder(new LineBorder(Color.RED, 2));
        else
            dtpSightingDate.setBorder(new LineBorder(green, 2));
        // Perform the save action
        if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
            if (saveSighting()) {
                // (Evt is null if the Image Upload calls save method and the dialog shouldn't be closed)
                // Premare to close dialog
                if (panelToRefresh != null) {
                    panelToRefresh.refreshTableForSightings();
                }
                // Close the dialog - (Evt is null if the Image Upload, etc. methods call this method, then we don't want to close.)
                if (evt != null) {
                    closeThisDialog();
                }
            }
        }
        else {
            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(app.getMainFrame(),
                            "Please fill in all of the required fields.",
                            "Can't Save Observation", JOptionPane.ERROR_MESSAGE);
                    return -1;
                }
            });
        }
}//GEN-LAST:event_btnUpdateSightingActionPerformed

    private boolean saveSighting() {
        // Set Location, Element and Visit
        sighting.setLocationName(location.getName());
        sighting.setElementName(element.getPrimaryName());
        sighting.setVisitName(visit.getName());

        // Set variables
        sighting.setDate(setSightingDateFromUIFields());
        if (TimeFormat.NONE.equals(cmbTimeFormat.getSelectedItem()) || TimeFormat.UNKNOWN.equals(cmbTimeFormat.getSelectedItem()))
            sighting.setTimeUnknown(true);
        else
            sighting.setTimeUnknown(false);
        sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
        sighting.setDetails(txtDetails.getText());
        sighting.setSightingEvidence((SightingEvidence)cmbEvidence.getSelectedItem());
        if (spnNumberOfElements.getValue().toString().length() > 0) {
            try {
                sighting.setNumberOfElements(Integer.parseInt(spnNumberOfElements.getValue().toString()));
            }
            catch (NumberFormatException e) {
                e.printStackTrace(System.err);
            }
        }
        sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
        sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
        sighting.setWeather((Weather)cmbWeather.getSelectedItem());
        if (spnTemperature.getValue().toString().length() > 0) {
            try {
                sighting.setTemperature(Double.parseDouble(spnTemperature.getValue().toString()));
            }
            catch (NumberFormatException e) {
                e.printStackTrace(System.err);
            }
        }
        sighting.setUnitsTemperature((UnitsTemperature)cmbTemperatureUnits.getSelectedItem());
        sighting.setMoonlight((Moonlight)cmbMoonlight.getSelectedItem());
        sighting.setMoonPhase((Integer)spnMoonPhase.getValue());
        sighting.setTag(txtTag.getText());
        sighting.setLifeStatus((LifeStatus)cmbLifeStatus.getSelectedItem());
        sighting.setSex((Sex)cmbSex.getSelectedItem());
        sighting.setDurationMinutes(Integer.parseInt(spnDurationMinutes.getValue().toString()));
        sighting.setDurationSeconds(Double.parseDouble(spnDurationSeconds.getValue().toString()));

        // NOTE: The GPS info is already set on the Sighting object by the GPS popup component

        // SAVE (Only save to DB if not in bulk upload mode)
        if (!bulkUploadMode) {
            if (app.getDBI().createOrUpdate(sighting) == false) {
                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "Could not save the Observation.",
                                "Error Saving", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
                return false;
            }
        }

        return true;
    }

    private void closeThisDialog() {
        this.dispose();
    }

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        uploadImage(null);
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void uploadImage(final List<File> inFiles) {
        if (inFiles != null && inFiles.size() > 0) {
            // If the date hasn't been set yet, then try to load it from the first image
            setSightingDateFromUIFields();
            if (sighting.getDate() == null) {
                getDateFromImage(inFiles.get(0));
                btnCalculateSunAndMoonActionPerformed(null);
            }
        }
        // Try to save the sighting (to make sure all required fields are there and to get the SightingID)
        btnUpdateSightingActionPerformed(null);
        // Now upload the files
        if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
            if (inFiles == null) {
                imageIndex = UtilsFileProcessing.uploadFileUsingDialog(
                        sighting.getWildLogFileID(),
                        WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_SIGHTING.toString(), sighting.toString()),
                        this, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
            }
            else {
                imageIndex = UtilsFileProcessing.uploadFilesUsingList(
                        sighting.getWildLogFileID(),
                        WildLogPaths.concatPaths(true, WildLogPrefixes.WILDLOG_PREFIXES_SIGHTING.toString(), sighting.toString()),
                        this, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app,
                        inFiles);
            }
            // Update the label showingthe numebr of images
            setupNumberOfImages();
        }
    }

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            element = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
            UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, lblElementImage, 100, app);
        }
        else {
            element = null;
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(sighting.getWildLogFileID(), imageIndex, lblImage, UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM, app);
        setupNumberOfImages();
        btnUpdateSightingActionPerformed(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(sighting.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateSightingActionPerformed(null);
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (tblVisit.getSelectedRowCount() == 1) {
            visit = app.getDBI().find(new Visit(tblVisit.getValueAt(tblVisit.getSelectedRow(), 0).toString()));
        }
        else {
            visit = null;
        }
}//GEN-LAST:event_tblVisitMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            location = app.getDBI().find(new Location(tblLocation.getValueAt(tblLocation.getSelectedRow(), 0).toString()));
            UtilTableGenerator.setupVeryShortVisitTable(tblVisit, location);
            visit = null;
            UtilsImageProcessing.setupFoto(location.getWildLogFileID(), 0, lblLocationImage, 100, app);
        }
        else {
            location = null;
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Place"}, 0));
            visit = null;
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
}//GEN-LAST:event_tblLocationMouseReleased

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        if (sighting != null) {
            searchElement = new Element();
            ElementType type = (ElementType)cmbElementType.getSelectedItem();
            if (!ElementType.NONE.equals(type)) {
                searchElement.setType(type);
            }
            UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
            txtSearch.setText("");
            // Clear Images
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
}//GEN-LAST:event_cmbElementTypeActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (sighting != null) {
            UtilsFileProcessing.openFile(sighting.getWildLogFileID(), imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (location != null) {
            UtilsFileProcessing.openFile(location.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (element != null) {
            UtilsFileProcessing.openFile(element.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblLocationMouseReleased(null);
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void btnGetDateFromImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetDateFromImageActionPerformed
        List<WildLogFile> files = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
        if (files != null && files.size() > 0)
            getDateFromImage(new File(files.get(imageIndex).getFilePath(true)));
    }//GEN-LAST:event_btnGetDateFromImageActionPerformed

    private void getDateFromImage(File inFile) {
        if (inFile != null) {
            if (new ImageFilter().accept(inFile)) {
                // Get the date form the image
                Date imageDate = UtilsImageProcessing.getDateFromImage(inFile);
                // Set the date
                if (imageDate != null) {
                    sighting.setDate(imageDate);
                    setUIFieldsFromSightingDate();
                }
            }
        }
    }

    private void btnCalculateSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateSunAndMoonActionPerformed
        if (sighting.getDate() != null
                && sighting.getLatitude() != null && sighting.getLongitude() != null
                && !Latitudes.NONE.equals(sighting.getLatitude()) && !Longitudes.NONE.equals(sighting.getLongitude())
                && txtLatitude.getText() != null && !txtLatitude.getText().isEmpty() && !UtilsGps.NO_GPS_POINT.equals(txtLatitude.getText())
                && txtLongitude.getText() != null && !txtLongitude.getText().isEmpty() && !UtilsGps.NO_GPS_POINT.equals(txtLongitude.getText())) {
            // Try to save the sighting (to make sure all required fields are there and to get the Sighting Time)
            btnUpdateSightingActionPerformed(null);
            if (!sighting.isTimeUnknown()) {
                double latitude = UtilsGps.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                double longitude = UtilsGps.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                // Sun
                cmbTimeOfDay.setSelectedItem(AstroCalculator.getSunCategory(setSightingDateFromUIFields(), latitude, longitude));
                // Moon
                spnMoonPhase.setValue(AstroCalculator.getMoonPhase(sighting.getDate()));
                cmbMoonlight.setSelectedItem(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
            }
        }
        else {
            // Only show the error if the user clicked the button
            if (evt != null) {
                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "Please make sure to first provide values for the Creature, Place, Period and GPS point.",
                                "Could not calculate the Sun and Moon information.", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
            }
        }
    }//GEN-LAST:event_btnCalculateSunAndMoonActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(this, sighting);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(UtilsGps.getLatitudeString(sighting));
            txtLongitude.setText(UtilsGps.getLongitudeString(sighting));
        }
        btnUpdateSighting.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void btnCalculateDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateDurationActionPerformed
        // Get all Image files for this sighting
        WildLogFile searchFile = new WildLogFile(sighting.getWildLogFileID());
        searchFile.setFileType(WildLogFileType.IMAGE);
        List<WildLogFile> files = app.getDBI().list(searchFile);
        if (!files.isEmpty()) {
            Collections.sort(files);
            Date startDate = UtilsImageProcessing.getDateFromImage(new File(files.get(0).getFilePath(true)));
            Date endDate = UtilsImageProcessing.getDateFromImage(new File(files.get(files.size()-1).getFilePath(true)));
            double difference = (endDate.getTime() - startDate.getTime())/1000;
            int minutes = (int)difference/60;
            double seconds = difference - minutes*60.0;
            spnDurationMinutes.setValue(minutes);
            spnDurationSeconds.setValue((double)seconds);
        }
    }//GEN-LAST:event_btnCalculateDurationActionPerformed

    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    private Date setSightingDateFromUIFields() {
        // Get the date from the datepicker
        Date date = dtpSightingDate.getDate();
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            try {
                // Hours
                if (TimeFormat.PM.equals(cmbTimeFormat.getSelectedItem())) {
                    int tempHours = 12 + (Integer)spnHours.getValue();
                    if (tempHours >= 24) calendar.set(Calendar.HOUR_OF_DAY, tempHours - 12);
                    else calendar.set(Calendar.HOUR_OF_DAY, tempHours);
                }
                else
                    calendar.set(Calendar.HOUR_OF_DAY, (Integer)spnHours.getValue());
                // Minutes
                calendar.set(Calendar.MINUTE, (Integer)spnMinutes.getValue());
            }
            catch (NumberFormatException ex) {
                calendar.set(Calendar.HOUR_OF_DAY, -1);
                calendar.set(Calendar.MINUTE, -1);
                cmbTimeFormat.setSelectedItem(TimeFormat.NONE);
            }
            sighting.setDate(calendar.getTime());
        }
        else {
            sighting.setDate(null);
        }
        return sighting.getDate();
    }

    public Sighting getSighting() {
        return sighting;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculateDuration;
    private javax.swing.JButton btnCalculateSunAndMoon;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnGetDateFromImage;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbEvidence;
    private javax.swing.JComboBox cmbLifeStatus;
    private javax.swing.JComboBox cmbMoonlight;
    private javax.swing.JComboBox cmbSex;
    private javax.swing.JComboBox cmbTemperatureUnits;
    private javax.swing.JComboBox cmbTimeFormat;
    private javax.swing.JComboBox cmbTimeOfDay;
    private javax.swing.JComboBox cmbViewRating;
    private javax.swing.JComboBox cmbWeather;
    private org.jdesktop.swingx.JXDatePicker dtpSightingDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblElement;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblSightingID;
    private javax.swing.JLabel lblVisit;
    private javax.swing.JScrollPane sclElement;
    private javax.swing.JScrollPane sclLocation;
    private javax.swing.JScrollPane sclVisit;
    private javax.swing.JPanel sightingIncludes;
    private javax.swing.JSpinner spnDurationMinutes;
    private javax.swing.JSpinner spnDurationSeconds;
    private javax.swing.JSpinner spnHours;
    private javax.swing.JSpinner spnMinutes;
    private javax.swing.JSpinner spnMoonPhase;
    private javax.swing.JSpinner spnNumberOfElements;
    private javax.swing.JSpinner spnTemperature;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchLocation;
    private javax.swing.JTextField txtTag;
    // End of variables declaration//GEN-END:variables

}
