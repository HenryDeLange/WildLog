package wildlog.ui.panels;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.utils.UtilsFileProcessing;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeFormat;
import wildlog.data.enums.UnitsTemperature;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.astro.AstroCalculator;
import wildlog.ui.helpers.FileDrop;
import wildlog.mapping.utils.LatLonConverter;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsImageProcessing;


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
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Location"}, 0));
        }

        // Setup location and element images
        if (location != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + location.getName()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto("LOCATION-" + location.getName(), 0, lblLocationImage, 100, app);
            else
                lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
        else {
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
        if (element != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
            if (fotos.size() > 0)
                UtilsImageProcessing.setupFoto("ELEMENT-" + element.getPrimaryName(), 0, lblElementImage, 100, app);
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
        txtLatitude.setText(LatLonConverter.getLatitudeString(sighting));
        txtLongitude.setText(LatLonConverter.getLatitudeString(sighting));

        // Spinners stuff
        SpinnerFixer.fixSelectAllForSpinners(spnNumberOfElements);
        SpinnerFixer.fixSelectAllForSpinners(spnHours);
        SpinnerFixer.fixSelectAllForSpinners(spnMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnMoonPhase);
        SpinnerFixer.fixSelectAllForSpinners(spnTemperature);

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
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
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
    }

    private void setupSightingInfo() {
        // Display the ID
        lblSightingID.setText("Sighting ID: " + Long.toString(sighting.getSightingCounter()));
        // Load the values from the Sighting object
        setUIFieldsFromSightingDate();
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        txtDetails.setText(sighting.getDetails());
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        spnNumberOfElements.setValue(sighting.getNumberOfElements());
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbWeather.setSelectedItem(sighting.getWeather());
        txtLatitude.setText(LatLonConverter.getLatitudeString(sighting));
        txtLongitude.setText(LatLonConverter.getLongitudeString(sighting));
        spnMoonPhase.setValue(sighting.getMoonPhase());
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        spnTemperature.setValue(sighting.getTemperature());
        cmbTemperatureUnits.setSelectedItem(sighting.getUnitsTemperature());
        txtTag.setText(sighting.getTag());
        cmbLifeStatus.setSelectedItem(sighting.getLifeStatus());
        cmbSex.setSelectedItem(sighting.getSex());
        // Setup the sighting's image
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
        if (fotos.size() > 0)
            UtilsImageProcessing.setupFoto("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
        else
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
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
        cmbTimeFormat.setSelectedItem(TimeFormat.H24);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelSighting.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(955, 615));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sightingIncludes.setBackground(resourceMap.getColor("sightingIncludes.background")); // NOI18N
        sightingIncludes.setMaximumSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(950, 590));
        sightingIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnUpdateSighting.setBackground(resourceMap.getColor("btnUpdateSighting.background")); // NOI18N
        btnUpdateSighting.setIcon(resourceMap.getIcon("btnUpdateSighting.icon")); // NOI18N
        btnUpdateSighting.setText(resourceMap.getString("btnUpdateSighting.text")); // NOI18N
        btnUpdateSighting.setToolTipText(resourceMap.getString("btnUpdateSighting.toolTipText")); // NOI18N
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

        sclElement.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("sclElement.border.lineColor"))); // NOI18N
        sclElement.setName("sclElement"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setEnabled(!disableEditing);
        tblElement.setFocusable(false);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(resourceMap.getColor("tblElement.selectionBackground")); // NOI18N
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

        btnPreviousImage.setBackground(resourceMap.getColor("btnPreviousImage.background")); // NOI18N
        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 310, 40, 50));

        btnNextImage.setBackground(resourceMap.getColor("btnNextImage.background")); // NOI18N
        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 310, 40, 50));

        btnUploadImage.setBackground(resourceMap.getColor("btnUploadImage.background")); // NOI18N
        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
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

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setToolTipText(resourceMap.getString("jLabel6.toolTipText")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        sightingIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        dtpSightingDate.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("dtpSightingDate.border.lineColor"))); // NOI18N
        dtpSightingDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setEnabled(!disableEditing);
        dtpSightingDate.setFocusable(false);
        dtpSightingDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        sightingIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 150, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        sightingIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, 20));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        sightingIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, -1, 20));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        sightingIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, 20));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        sightingIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 130, -1, 20));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        sightingIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 190, -1, 20));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDetails.setColumns(20);
        txtDetails.setFont(resourceMap.getFont("txtDetails.font")); // NOI18N
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(sighting.getDetails());
        txtDetails.setWrapStyleWord(true);
        txtDetails.setEnabled(!disableEditing);
        txtDetails.setName("txtDetails"); // NOI18N
        jScrollPane2.setViewportView(txtDetails);

        sightingIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 200, 80));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setEnabled(!disableEditing);
        cmbWeather.setFocusable(false);
        cmbWeather.setName("cmbWeather"); // NOI18N
        sightingIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, 210, -1));

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

        lblElementImage.setBackground(resourceMap.getColor("lblElementImage.background")); // NOI18N
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setText(resourceMap.getString("lblElementImage.text")); // NOI18N
        lblElementImage.setToolTipText(resourceMap.getString("lblElementImage.toolTipText")); // NOI18N
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

        lblImage.setBackground(resourceMap.getColor("lblImage.background")); // NOI18N
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
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
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setEnabled(!disableEditing);
        cmbElementType.setFocusable(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        sightingIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 330, 130, -1));

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        sightingIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 20));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setEnabled(!disableEditing);
        txtSearch.setName("txtSearch"); // NOI18N
        sightingIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 150, 20));

        btnDeleteImage.setBackground(resourceMap.getColor("btnDeleteImage.background")); // NOI18N
        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
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

        btnSetMainImage.setBackground(resourceMap.getColor("btnSetMainImage.background")); // NOI18N
        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
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

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        sightingIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, -1, 20));

        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setEnabled(!disableEditing);
        cmbEvidence.setFocusable(false);
        cmbEvidence.setName("cmbEvidence"); // NOI18N
        sightingIncludes.add(cmbEvidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 100, 140, -1));

        sclLocation.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("sclLocation.border.lineColor"))); // NOI18N
        sclLocation.setName("sclLocation"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setEnabled(!disableEditing && !bulkUploadMode);
        tblLocation.setFocusable(false);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionBackground(resourceMap.getColor("tblLocation.selectionBackground")); // NOI18N
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

        sclVisit.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("sclVisit.border.lineColor"))); // NOI18N
        sclVisit.setName("sclVisit"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setEnabled(!disableEditing && !bulkUploadMode);
        tblVisit.setFocusable(false);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(resourceMap.getColor("tblVisit.selectionBackground")); // NOI18N
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        sclVisit.setViewportView(tblVisit);

        sightingIncludes.add(sclVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 390, 310, 190));

        lblElement.setFont(resourceMap.getFont("lblElement.font")); // NOI18N
        lblElement.setText(resourceMap.getString("lblElement.text")); // NOI18N
        lblElement.setToolTipText(resourceMap.getString("lblElement.toolTipText")); // NOI18N
        lblElement.setName("lblElement"); // NOI18N
        sightingIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, -1, -1));

        lblLocation.setFont(resourceMap.getFont("lblLocation.font")); // NOI18N
        lblLocation.setText(resourceMap.getString("lblLocation.text")); // NOI18N
        lblLocation.setToolTipText(resourceMap.getString("lblLocation.toolTipText")); // NOI18N
        lblLocation.setName("lblLocation"); // NOI18N
        sightingIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 310, -1, -1));

        txtSearchLocation.setText(resourceMap.getString("txtSearchLocation.text")); // NOI18N
        txtSearchLocation.setEnabled(!disableEditing && !bulkUploadMode);
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N
        sightingIncludes.add(txtSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 330, 210, 20));

        lblVisit.setFont(resourceMap.getFont("lblVisit.font")); // NOI18N
        lblVisit.setText(resourceMap.getString("lblVisit.text")); // NOI18N
        lblVisit.setToolTipText(resourceMap.getString("lblVisit.toolTipText")); // NOI18N
        lblVisit.setName("lblVisit"); // NOI18N
        sightingIncludes.add(lblVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 370, -1, -1));

        lblLocationImage.setBackground(resourceMap.getColor("lblLocationImage.background")); // NOI18N
        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setToolTipText(resourceMap.getString("lblLocationImage.toolTipText")); // NOI18N
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

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        sightingIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 335, 40, 20));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        sightingIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 10, 20));

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setEnabled(!disableEditing);
        cmbTimeFormat.setFocusable(false);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        sightingIncludes.add(cmbTimeFormat, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 110, 20));

        lblSightingID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingID.setText(resourceMap.getString("lblSightingID.text")); // NOI18N
        lblSightingID.setName("lblSightingID"); // NOI18N
        sightingIncludes.add(lblSightingID, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 340, 120, 20));

        spnNumberOfElements.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spnNumberOfElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnNumberOfElements.setEditor(new javax.swing.JSpinner.NumberEditor(spnNumberOfElements, "#"));
        spnNumberOfElements.setEnabled(!disableEditing);
        spnNumberOfElements.setName("spnNumberOfElements"); // NOI18N
        sightingIncludes.add(spnNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 70, -1));

        btnGetDateFromImage.setBackground(resourceMap.getColor("btnGetDateFromImage.background")); // NOI18N
        btnGetDateFromImage.setIcon(resourceMap.getIcon("btnGetDateFromImage.icon")); // NOI18N
        btnGetDateFromImage.setText(resourceMap.getString("btnGetDateFromImage.text")); // NOI18N
        btnGetDateFromImage.setToolTipText(resourceMap.getString("btnGetDateFromImage.toolTipText")); // NOI18N
        btnGetDateFromImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetDateFromImage.setEnabled(!disableEditing && !bulkUploadMode);
        btnGetDateFromImage.setFocusPainted(false);
        btnGetDateFromImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnGetDateFromImage.setName("btnGetDateFromImage"); // NOI18N
        btnGetDateFromImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetDateFromImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnGetDateFromImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 110, 50));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        sightingIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        sightingIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, 20));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        sightingIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 40, 20));

        btnCalculateSunAndMoon.setBackground(resourceMap.getColor("btnCalculateSunAndMoon.background")); // NOI18N
        btnCalculateSunAndMoon.setIcon(resourceMap.getIcon("btnCalculateSunAndMoon.icon")); // NOI18N
        btnCalculateSunAndMoon.setText(resourceMap.getString("btnCalculateSunAndMoon.text")); // NOI18N
        btnCalculateSunAndMoon.setToolTipText(resourceMap.getString("btnCalculateSunAndMoon.toolTipText")); // NOI18N
        btnCalculateSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCalculateSunAndMoon.setEnabled(!disableEditing);
        btnCalculateSunAndMoon.setFocusPainted(false);
        btnCalculateSunAndMoon.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCalculateSunAndMoon.setName("btnCalculateSunAndMoon"); // NOI18N
        btnCalculateSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateSunAndMoonActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnCalculateSunAndMoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 130, 110, 50));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        sightingIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, 20));

        cmbMoonlight.setModel(new DefaultComboBoxModel(Moonlight.values()));
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        cmbMoonlight.setEnabled(!disableEditing);
        cmbMoonlight.setFocusable(false);
        cmbMoonlight.setName("cmbMoonlight"); // NOI18N
        sightingIncludes.add(cmbMoonlight, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, 210, -1));

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
        sightingIncludes.add(spnMoonPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 67, -1));

        txtLatitude.setText(resourceMap.getString("txtLatitude.text")); // NOI18N
        txtLatitude.setDisabledTextColor(resourceMap.getColor("txtLatitude.disabledTextColor")); // NOI18N
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N
        sightingIncludes.add(txtLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 110, -1));

        txtLongitude.setText(resourceMap.getString("txtLongitude.text")); // NOI18N
        txtLongitude.setDisabledTextColor(resourceMap.getColor("txtLongitude.disabledTextColor")); // NOI18N
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N
        sightingIncludes.add(txtLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 110, -1));

        btnGPS.setBackground(resourceMap.getColor("btnGPS.background")); // NOI18N
        btnGPS.setIcon(resourceMap.getIcon("btnGPS.icon")); // NOI18N
        btnGPS.setText(resourceMap.getString("btnGPS.text")); // NOI18N
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
        sightingIncludes.add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 100, 40));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        sightingIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, 20));

        spnTemperature.setModel(new javax.swing.SpinnerNumberModel(0.0d, -130.0d, 140.0d, 1.0d));
        spnTemperature.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnTemperature.setEditor(new javax.swing.JSpinner.NumberEditor(spnTemperature, "###.##"));
        spnTemperature.setEnabled(!disableEditing);
        spnTemperature.setName("spnTemperature"); // NOI18N
        sightingIncludes.add(spnTemperature, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 210, 60, -1));

        cmbTemperatureUnits.setModel(new DefaultComboBoxModel(UnitsTemperature.values()));
        cmbTemperatureUnits.setSelectedItem(sighting.getUnitsTemperature());
        cmbTemperatureUnits.setEnabled(!disableEditing);
        cmbTemperatureUnits.setFocusable(false);
        cmbTemperatureUnits.setName("cmbTemperatureUnits"); // NOI18N
        sightingIncludes.add(cmbTemperatureUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 210, 140, -1));

        jSeparator3.setForeground(resourceMap.getColor("jSeparator3.foreground")); // NOI18N
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setName("jSeparator3"); // NOI18N
        sightingIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 300, 10, 62));

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        sightingIncludes.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 20));

        jSeparator4.setForeground(resourceMap.getColor("jSeparator4.foreground")); // NOI18N
        jSeparator4.setName("jSeparator4"); // NOI18N
        sightingIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 360, 425, 10));

        jSeparator5.setForeground(resourceMap.getColor("jSeparator5.foreground")); // NOI18N
        jSeparator5.setName("jSeparator5"); // NOI18N
        sightingIncludes.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 505, 10));

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        sightingIncludes.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, -1, 20));

        cmbSex.setModel(new DefaultComboBoxModel(Sex.values()));
        cmbSex.setSelectedItem(sighting.getSex());
        cmbSex.setEnabled(!disableEditing);
        cmbSex.setFocusable(false);
        cmbSex.setName("cmbSex"); // NOI18N
        sightingIncludes.add(cmbSex, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 140, -1));

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        sightingIncludes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, -1, 20));

        cmbLifeStatus.setModel(new DefaultComboBoxModel(LifeStatus.values()));
        cmbLifeStatus.setSelectedItem(sighting.getLifeStatus());
        cmbLifeStatus.setEnabled(!disableEditing);
        cmbLifeStatus.setFocusable(false);
        cmbLifeStatus.setName("cmbLifeStatus"); // NOI18N
        sightingIncludes.add(cmbLifeStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 140, -1));

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        sightingIncludes.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, 20));

        txtTag.setText(sighting.getTag());
        txtTag.setToolTipText(resourceMap.getString("txtTag.toolTipText")); // NOI18N
        txtTag.setEnabled(!disableEditing);
        txtTag.setName("txtTag"); // NOI18N
        sightingIncludes.add(txtTag, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 270, 210, -1));

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
            JOptionPane.showMessageDialog(this, "Please fill in all of the required fields.", "Can't Save Sighting", JOptionPane.WARNING_MESSAGE);
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
        // NOTE: The GPS info is already set on the Sighting object by the GPS popup component

        // SAVE (Only save to DB if not in bulk upload mode)
        if (!bulkUploadMode) {
            if (app.getDBI().createOrUpdate(sighting) == false) {
                JOptionPane.showMessageDialog(this, "Could not save the Sighting", "Error Saving", JOptionPane.ERROR_MESSAGE);
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
        if (inFiles.size() > 0) {
            // If the date hasn't been set yet, then try to load it from the first image
            setSightingDateFromUIFields();
            if (sighting.getDate() == null) {
                getDateFromImage(inFiles.get(0));
                btnCalculateSunAndMoonActionPerformed(null);
            }
            // Try to save the sighting (to make sure all required fields are there and to get the SightingID)
            btnUpdateSightingActionPerformed(null);
            // Now upload the files
            if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                if (inFiles == null) {
                    imageIndex = UtilsFileProcessing.uploadImage(
                            "SIGHTING-" + sighting.getSightingCounter(),
                            "Sightings" + File.separatorChar + sighting.toString(),
                            this, lblImage, 300, app);
                }
                else {
                    imageIndex = UtilsFileProcessing.uploadImage(
                            "SIGHTING-" + sighting.getSightingCounter(),
                            "Sightings" + File.separatorChar + sighting.toString(),
                            this, lblImage, 300, app,
                            inFiles);
                }
                // Update the label showingthe numebr of images
                setupNumberOfImages();
            }
        }
    }

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            element = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
            UtilsImageProcessing.setupFoto("ELEMENT-" + element.getPrimaryName(), 0, lblElementImage, 100, app);
        }
        else {
            element = null;
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
        btnUpdateSightingActionPerformed(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, app);
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
            UtilsImageProcessing.setupFoto("LOCATION-" + location.getName(), 0, lblLocationImage, 100, app);
        }
        else {
            location = null;
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Location"}, 0));
            visit = null;
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
}//GEN-LAST:event_tblLocationMouseReleased

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        if (sighting != null) {
            searchElement = new Element((ElementType)cmbElementType.getSelectedItem());
            UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
            txtSearch.setText("");
            // Clear Images
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(100));
        }
}//GEN-LAST:event_cmbElementTypeActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (sighting != null) {
            UtilsFileProcessing.openFile("SIGHTING-" + sighting.getSightingCounter(), imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (location != null) {
            UtilsFileProcessing.openFile("LOCATION-" + location.getName(), 0, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (element != null) {
            UtilsFileProcessing.openFile("ELEMENT-" + element.getPrimaryName(), 0, app);
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
        List<WildLogFile> files = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
        if (files != null && files.size() > 0)
            getDateFromImage(new File(files.get(imageIndex).getOriginalFotoLocation(true)));
    }//GEN-LAST:event_btnGetDateFromImageActionPerformed

    private void getDateFromImage(File inFile) {
        if (inFile != null) {
            if (new ImageFilter().accept(inFile)) {
                // Get the date form the image
                Date imageDate = UtilsImageProcessing.getExifDateFromJpeg(inFile);
                // Set the date
                if (imageDate != null) {
                    sighting.setDate(imageDate);
                    setUIFieldsFromSightingDate();
                }
            }
        }
    }

    private void btnCalculateSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateSunAndMoonActionPerformed
        if (sighting.getDate() != null && sighting.getLatitude() != null && sighting.getLongitude() != null
                && !Latitudes.NONE.equals(sighting.getLatitude()) && !Longitudes.NONE.equals(sighting.getLongitude())
                && txtLatitude.getText() != null && !txtLatitude.getText().isEmpty() && !LatLonConverter.NO_GPS_POINT.equals(txtLatitude.getText())
                && txtLongitude.getText() != null && !txtLongitude.getText().isEmpty() && !LatLonConverter.NO_GPS_POINT.equals(txtLongitude.getText())) {
            // Sun
            btnUpdateSightingActionPerformed(null);
            double latitude = LatLonConverter.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
            double longitude = LatLonConverter.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
            cmbTimeOfDay.setSelectedItem(AstroCalculator.getSunCategory(setSightingDateFromUIFields(), latitude, longitude));
            // Moon
            spnMoonPhase.setValue(AstroCalculator.getMoonPhase(sighting.getDate()));
            cmbMoonlight.setSelectedItem(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
        }
        else {
            // Only show the error if the user clicked the button
            if (evt != null)
                JOptionPane.showMessageDialog(this, "Please make sure to first specify details for the Creature, Location, Visit and GPS values.", "Could not calculate the Sun and Moon information.", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCalculateSunAndMoonActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app.getMainFrame(), true, sighting);
        UtilsDialog.setDialogToCenter(app.getMainFrame(), dialog);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(LatLonConverter.getLatitudeString(sighting));
            txtLongitude.setText(LatLonConverter.getLongitudeString(sighting));
        }
        btnUpdateSighting.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
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
    private javax.swing.JButton btnCalculateSunAndMoon;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnGetDateFromImage;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.ButtonGroup buttonGroup1;
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
    private javax.swing.JLabel jLabel2;
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
