package wildlog.ui.panel;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeFormat;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.utils.AstroUtils;
import wildlog.utils.LatLonConverter;
import wildlog.utils.ui.SpinnerFixer;

/**
 *
 * @author  henry.delange
 */
public class PanelSighting extends javax.swing.JPanel {
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
    
    /** Creates new form PanelVisit */
    public PanelSighting(Sighting inSighting, Location inLocation, Visit inVisit, Element inElement, boolean inTreatAsNewSighting, boolean inDisableEditing) {
        sighting = inSighting;
        treatAsNewSighting = inTreatAsNewSighting;
        disableEditing = inDisableEditing;
        if (sighting != null) {
            // Initiate all objects
            app = (WildLogApp) Application.getInstance();
            location = inLocation;
            visit = inVisit;
//            oldVisit = inVisit;
            element = inElement;
            searchElement = new Element();
            searchLocation = new Location();
            imageIndex = 0;
            // Auto-generated code
            initComponents();
            // Setup Dropdown Boxes
//            if (location != null)
//                cmbSubArea.setModel(new DefaultComboBoxModel(location.getSubAreas().toArray()));
            UtilTableGenerator.setupShortElementTable(tblElement, new Element());
            UtilTableGenerator.setupShortLocationTable(tblLocation, new Location());
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Location"}, 0));
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
            if (location != null) {
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + location.getName()));
                if (fotos.size() > 0)
                    Utils.setupFoto("LOCATION-" + location.getName(), 0, lblLocationImage, 100, app);
                else
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            if (element != null) {
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
                if (fotos.size() > 0)
                    Utils.setupFoto("ELEMENT-" + element.getPrimaryName(), 0, lblElementImage, 100, app);
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            //Setup Tables
            tblElement.getTableHeader().setReorderingAllowed(false);
            tblLocation.getTableHeader().setReorderingAllowed(false);
            tblVisit.getTableHeader().setReorderingAllowed(false);
            // Setup default values for input fields
            if (treatAsNewSighting) {
                cmbCertainty.setSelectedItem(Certainty.SURE);
                cmbEvidence.setSelectedItem(SightingEvidence.SEEN);
                spnNumberOfElements.setValue(0);
                cmbViewRating.setSelectedItem(ViewRating.NORMAL);
                //cmbLatitude.setSelectedItem(Latitudes.SOUTH);
                spnLatDegrees.setValue(0);
                spnLatMinutes.setValue(0);
                spnLatSeconds.setValue(0.0f);
                //cmbLongitude.setSelectedItem(Longitudes.EAST);
                spnLonDegrees.setValue(0);
                spnLonMinutes.setValue(0);
                spnLonSeconds.setValue(0.0f);
                lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            }
            else {
                // Setup the Sighting info
                setupSightingInfo();
            }
        }
        // Lat Lon stuff
        spnLatDecimal.setValue(LatLonConverter.getDecimalDegree((Latitudes)cmbLatitude.getSelectedItem(), (Integer)spnLatDegrees.getValue(), (Integer)spnLatMinutes.getValue(), (Float)spnLatSeconds.getValue()));
        spnLonDecimal.setValue(LatLonConverter.getDecimalDegree((Longitudes)cmbLongitude.getSelectedItem(), (Integer)spnLonDegrees.getValue(), (Integer)spnLonMinutes.getValue(), (Float)spnLonSeconds.getValue()));
        rdbDMS.setSelected(true);

        SpinnerFixer.fixSelectAllForSpinners(spnNumberOfElements);
        SpinnerFixer.fixSelectAllForSpinners(spnHours);
        SpinnerFixer.fixSelectAllForSpinners(spnMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnLatDecimal);
        SpinnerFixer.fixSelectAllForSpinners(spnLatDegrees);
        SpinnerFixer.fixSelectAllForSpinners(spnLatMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnLatSeconds);
        SpinnerFixer.fixSelectAllForSpinners(spnLonDecimal);
        SpinnerFixer.fixSelectAllForSpinners(spnLonDegrees);
        SpinnerFixer.fixSelectAllForSpinners(spnLonMinutes);
        SpinnerFixer.fixSelectAllForSpinners(spnLonSeconds);
    }

    public PanelSighting(Sighting inSighting, Location inLocation, Visit inVisit, Element inElement, PanelNeedsRefreshWhenSightingAdded inPanelToRefresh, boolean inTreatAsNewSighting, boolean inDisableEditing) {
        this(inSighting, inLocation, inVisit, inElement, inTreatAsNewSighting, inDisableEditing);
        panelToRefresh = inPanelToRefresh;
    }

    
    private void setupSightingInfo() {
        if (sighting != null) {
            lblSightingID.setText("Sighting ID: " + Long.toString(sighting.getSightingCounter()));
            dtpSightingDate.setDate(sighting.getDate());
            spnHours.setValue(sighting.getDate().getHours());
            spnMinutes.setValue(sighting.getDate().getMinutes());
            cmbAreaType.setSelectedItem(sighting.getAreaType());
            cmbCertainty.setSelectedItem(sighting.getCertainty());
            txtDetails.setText(sighting.getDetails());
            cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
            spnNumberOfElements.setValue(sighting.getNumberOfElements());
            cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
            cmbViewRating.setSelectedItem(sighting.getViewRating());
            cmbWeather.setSelectedItem(sighting.getWeather());
            cmbLatitude.setSelectedItem(sighting.getLatitude());
            spnLatDegrees.setValue(sighting.getLatDegrees());
            spnLatMinutes.setValue(sighting.getLatMinutes());
            spnLatSeconds.setValue(sighting.getLatSecondsFloat());
            cmbLongitude.setSelectedItem(sighting.getLongitude());
            spnLonDegrees.setValue(sighting.getLonDegrees());
            spnLonMinutes.setValue(sighting.getLonMinutes());
            spnLonSeconds.setValue(sighting.getLonSecondsFloat());
            spnMoonPhase.setValue(sighting.getMoonPhase());
            cmbMoonlight.setSelectedItem(sighting.getMoonlight());

            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
            if (fotos.size() > 0)
                Utils.setupFoto("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
            else
                lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            setupNumberOfImages();
        }
        else {
            System.out.println("No sighting provided...");
        }
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
        jScrollPane13 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        dtpSightingDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        cmbAreaType = new javax.swing.JComboBox();
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
        chkElementTypeFilter = new javax.swing.JCheckBox();
        cmbElementType = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        cmbLatitude = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbLongitude = new javax.swing.JComboBox();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnDeleteImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cmbEvidence = new javax.swing.JComboBox();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        lblElement = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        txtSearchLocation = new javax.swing.JTextField();
        lblVisit = new javax.swing.JLabel();
        lblLocationImage = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnSearchLocation = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbTimeFormat = new javax.swing.JComboBox();
        rdbDMS = new javax.swing.JRadioButton();
        rdbDD = new javax.swing.JRadioButton();
        lblSightingID = new javax.swing.JLabel();
        btnUsePrevGPS = new javax.swing.JButton();
        btnUseLocationGPS = new javax.swing.JButton();
        spnNumberOfElements = new javax.swing.JSpinner();
        btnGetDateFromImage = new javax.swing.JButton();
        btnCalculateTimeCategory = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnCalculateMoonPhase = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        cmbMoonlight = new javax.swing.JComboBox();
        spnHours = new javax.swing.JSpinner();
        spnMinutes = new javax.swing.JSpinner();
        spnMoonPhase = new javax.swing.JSpinner();
        spnLatDegrees = new javax.swing.JSpinner();
        spnLatMinutes = new javax.swing.JSpinner();
        spnLatSeconds = new javax.swing.JSpinner();
        spnLatDecimal = new javax.swing.JSpinner();
        spnLonDegrees = new javax.swing.JSpinner();
        spnLonMinutes = new javax.swing.JSpinner();
        spnLonSeconds = new javax.swing.JSpinner();
        spnLonDecimal = new javax.swing.JSpinner();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelSighting.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setMaximumSize(new java.awt.Dimension(965, 595));
        setMinimumSize(new java.awt.Dimension(965, 595));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(965, 595));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        sightingIncludes.setBackground(resourceMap.getColor("sightingIncludes.background")); // NOI18N
        sightingIncludes.setMaximumSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnUpdateSighting.setBackground(resourceMap.getColor("btnUpdateSighting.background")); // NOI18N
        btnUpdateSighting.setIcon(resourceMap.getIcon("btnUpdateSighting.icon")); // NOI18N
        btnUpdateSighting.setText(resourceMap.getString("btnUpdateSighting.text")); // NOI18N
        btnUpdateSighting.setToolTipText(resourceMap.getString("btnUpdateSighting.toolTipText")); // NOI18N
        btnUpdateSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateSighting.setEnabled(!disableEditing);
        btnUpdateSighting.setName("btnUpdateSighting"); // NOI18N
        btnUpdateSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSightingActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUpdateSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 170, 110, 70));

        jSeparator8.setName("jSeparator8"); // NOI18N
        sightingIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setEnabled(!disableEditing);
        tblElement.setName("tblElement"); // NOI18N
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
        jScrollPane13.setViewportView(tblElement);

        sightingIncludes.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 290, 310));

        jSeparator9.setName("jSeparator9"); // NOI18N
        sightingIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        sightingIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 360, 280, 10));

        btnPreviousImage.setBackground(resourceMap.getColor("btnPreviousImage.background")); // NOI18N
        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 300, 40, 50));

        btnNextImage.setBackground(resourceMap.getColor("btnNextImage.background")); // NOI18N
        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 300, 40, 50));

        btnUploadImage.setBackground(resourceMap.getColor("btnUploadImage.background")); // NOI18N
        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setEnabled(!disableEditing);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 300, 220, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        sightingIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 260, 30, 20));

        dtpSightingDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setEnabled(!disableEditing);
        dtpSightingDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        sightingIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 260, 140, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        sightingIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 520, -1, 20));

        cmbAreaType.setMaximumRowCount(11);
        cmbAreaType.setModel(new DefaultComboBoxModel(AreaType.values()));
        cmbAreaType.setSelectedItem(sighting.getAreaType());
        cmbAreaType.setEnabled(!disableEditing);
        cmbAreaType.setName("cmbAreaType"); // NOI18N
        sightingIncludes.add(cmbAreaType, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 520, 260, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        sightingIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 490, -1, 20));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        sightingIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 550, -1, 20));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        sightingIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 320, -1, 20));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        sightingIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 430, -1, 20));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        sightingIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 420, -1, 20));

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

        sightingIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 440, 300, 130));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setEnabled(!disableEditing);
        cmbWeather.setName("cmbWeather"); // NOI18N
        sightingIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 490, 260, -1));

        cmbTimeOfDay.setMaximumRowCount(9);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setEnabled(!disableEditing);
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N
        sightingIncludes.add(cmbTimeOfDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 290, 155, 20));

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setEnabled(!disableEditing);
        cmbViewRating.setName("cmbViewRating"); // NOI18N
        sightingIncludes.add(cmbViewRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 550, 260, -1));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setEnabled(!disableEditing);
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        sightingIncludes.add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 430, 260, -1));

        lblElementImage.setBackground(resourceMap.getColor("lblElementImage.background")); // NOI18N
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setText(resourceMap.getString("lblElementImage.text")); // NOI18N
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 150, -1, -1));

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
        sightingIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, -1));

        chkElementTypeFilter.setBackground(resourceMap.getColor("chkElementTypeFilter.background")); // NOI18N
        chkElementTypeFilter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkElementTypeFilter.setEnabled(!disableEditing);
        chkElementTypeFilter.setName("chkElementTypeFilter"); // NOI18N
        chkElementTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkElementTypeFilterActionPerformed(evt);
            }
        });
        sightingIncludes.add(chkElementTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, -1, 20));

        cmbElementType.setMaximumRowCount(9);
        cmbElementType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setEnabled(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        sightingIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 170, -1));

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        sightingIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, -1, 20));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setEnabled(!disableEditing);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        cmbLatitude.setNextFocusableComponent(cmbLongitude);
        sightingIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 350, 80, -1));

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        sightingIncludes.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 370, -1, 20));

        cmbLongitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLongitude.setSelectedIndex(2);
        cmbLongitude.setEnabled(!disableEditing);
        cmbLongitude.setName("cmbLongitude"); // NOI18N
        sightingIncludes.add(cmbLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 370, 80, -1));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setEnabled(!disableEditing);
        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        sightingIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 235, 200, -1));

        btnSearch.setBackground(resourceMap.getColor("btnSearch.background")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setToolTipText(resourceMap.getString("btnSearch.toolTipText")); // NOI18N
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setEnabled(!disableEditing);
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 235, 90, -1));

        btnDeleteImage.setBackground(resourceMap.getColor("btnDeleteImage.background")); // NOI18N
        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setEnabled(!disableEditing);
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 326, 90, -1));

        btnSetMainImage.setBackground(resourceMap.getColor("btnSetMainImage.background")); // NOI18N
        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setEnabled(!disableEditing);
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 326, 90, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        sightingIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 460, -1, 20));

        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setEnabled(!disableEditing);
        cmbEvidence.setName("cmbEvidence"); // NOI18N
        sightingIncludes.add(cmbEvidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 460, 260, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setEnabled(!disableEditing);
        tblLocation.setName("tblLocation"); // NOI18N
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
        jScrollPane15.setViewportView(tblLocation);

        sightingIncludes.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 45, 290, 140));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setEnabled(!disableEditing);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        jScrollPane14.setViewportView(tblVisit);

        sightingIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 320, 110));

        lblElement.setFont(resourceMap.getFont("lblElement.font")); // NOI18N
        lblElement.setText(resourceMap.getString("lblElement.text")); // NOI18N
        lblElement.setName("lblElement"); // NOI18N
        sightingIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, -1, -1));

        lblLocation.setFont(resourceMap.getFont("lblLocation.font")); // NOI18N
        lblLocation.setText(resourceMap.getString("lblLocation.text")); // NOI18N
        lblLocation.setName("lblLocation"); // NOI18N
        sightingIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtSearchLocation.setText(resourceMap.getString("txtSearchLocation.text")); // NOI18N
        txtSearchLocation.setEnabled(!disableEditing);
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N
        txtSearchLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchLocationKeyPressed(evt);
            }
        });
        sightingIncludes.add(txtSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 200, -1));

        lblVisit.setFont(resourceMap.getFont("lblVisit.font")); // NOI18N
        lblVisit.setText(resourceMap.getString("lblVisit.text")); // NOI18N
        lblVisit.setName("lblVisit"); // NOI18N
        sightingIncludes.add(lblVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, -1));

        lblLocationImage.setBackground(resourceMap.getColor("lblLocationImage.background")); // NOI18N
        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLocationImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setOpaque(true);
        lblLocationImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblLocationImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLocationImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblLocationImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, -1, -1));

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        sightingIncludes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 135, -1, -1));

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        sightingIncludes.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 135, -1, -1));

        btnSearchLocation.setBackground(resourceMap.getColor("btnSearchLocation.background")); // NOI18N
        btnSearchLocation.setIcon(resourceMap.getIcon("btnSearchLocation.icon")); // NOI18N
        btnSearchLocation.setText(resourceMap.getString("btnSearchLocation.text")); // NOI18N
        btnSearchLocation.setToolTipText(resourceMap.getString("btnSearchLocation.toolTipText")); // NOI18N
        btnSearchLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchLocation.setEnabled(!disableEditing);
        btnSearchLocation.setName("btnSearchLocation"); // NOI18N
        btnSearchLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchLocationActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 90, -1));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        sightingIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 330, 40, 20));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        sightingIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 290, 10, 20));

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setEnabled(!disableEditing);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        sightingIncludes.add(cmbTimeFormat, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 290, 45, 20));

        rdbDMS.setBackground(resourceMap.getColor("rdbDMS.background")); // NOI18N
        buttonGroup1.add(rdbDMS);
        rdbDMS.setText(resourceMap.getString("rdbDMS.text")); // NOI18N
        rdbDMS.setToolTipText(resourceMap.getString("rdbDMS.toolTipText")); // NOI18N
        rdbDMS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbDMS.setName("rdbDMS"); // NOI18N
        rdbDMS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbDMSItemStateChanged(evt);
            }
        });
        sightingIncludes.add(rdbDMS, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 350, -1, -1));

        rdbDD.setBackground(resourceMap.getColor("rdbDD.background")); // NOI18N
        buttonGroup1.add(rdbDD);
        rdbDD.setText(resourceMap.getString("rdbDD.text")); // NOI18N
        rdbDD.setToolTipText(resourceMap.getString("rdbDD.toolTipText")); // NOI18N
        rdbDD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbDD.setName("rdbDD"); // NOI18N
        rdbDD.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbDDItemStateChanged(evt);
            }
        });
        sightingIncludes.add(rdbDD, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 370, -1, -1));

        lblSightingID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingID.setText(resourceMap.getString("lblSightingID.text")); // NOI18N
        lblSightingID.setName("lblSightingID"); // NOI18N
        sightingIncludes.add(lblSightingID, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 150, 100, 20));

        btnUsePrevGPS.setBackground(resourceMap.getColor("btnUsePrevGPS.background")); // NOI18N
        btnUsePrevGPS.setText(resourceMap.getString("btnUsePrevGPS.text")); // NOI18N
        btnUsePrevGPS.setToolTipText(resourceMap.getString("btnUsePrevGPS.toolTipText")); // NOI18N
        btnUsePrevGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUsePrevGPS.setName("btnUsePrevGPS"); // NOI18N
        btnUsePrevGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsePrevGPSActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUsePrevGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 393, 130, -1));

        btnUseLocationGPS.setBackground(resourceMap.getColor("btnUseLocationGPS.background")); // NOI18N
        btnUseLocationGPS.setText(resourceMap.getString("btnUseLocationGPS.text")); // NOI18N
        btnUseLocationGPS.setToolTipText(resourceMap.getString("btnUseLocationGPS.toolTipText")); // NOI18N
        btnUseLocationGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseLocationGPS.setName("btnUseLocationGPS"); // NOI18N
        btnUseLocationGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseLocationGPSActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUseLocationGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 393, 130, -1));

        spnNumberOfElements.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spnNumberOfElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnNumberOfElements.setName("spnNumberOfElements"); // NOI18N
        sightingIncludes.add(spnNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 320, 50, -1));

        btnGetDateFromImage.setBackground(resourceMap.getColor("btnGetDateFromImage.background")); // NOI18N
        btnGetDateFromImage.setText(resourceMap.getString("btnGetDateFromImage.text")); // NOI18N
        btnGetDateFromImage.setToolTipText(resourceMap.getString("btnGetDateFromImage.toolTipText")); // NOI18N
        btnGetDateFromImage.setName("btnGetDateFromImage"); // NOI18N
        btnGetDateFromImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetDateFromImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnGetDateFromImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 260, 140, -1));

        btnCalculateTimeCategory.setBackground(resourceMap.getColor("btnCalculateTimeCategory.background")); // NOI18N
        btnCalculateTimeCategory.setText(resourceMap.getString("btnCalculateTimeCategory.text")); // NOI18N
        btnCalculateTimeCategory.setToolTipText(resourceMap.getString("btnCalculateTimeCategory.toolTipText")); // NOI18N
        btnCalculateTimeCategory.setName("btnCalculateTimeCategory"); // NOI18N
        btnCalculateTimeCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateTimeCategoryActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnCalculateTimeCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 313, 155, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        sightingIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 290, -1, 20));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        sightingIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 370, -1, 20));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        sightingIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 370, -1, 20));

        btnCalculateMoonPhase.setBackground(resourceMap.getColor("btnCalculateMoonPhase.background")); // NOI18N
        btnCalculateMoonPhase.setText(resourceMap.getString("btnCalculateMoonPhase.text")); // NOI18N
        btnCalculateMoonPhase.setName("btnCalculateMoonPhase"); // NOI18N
        btnCalculateMoonPhase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateMoonPhaseActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnCalculateMoonPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 393, 300, -1));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        sightingIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(775, 370, -1, 20));

        cmbMoonlight.setModel(new DefaultComboBoxModel(Moonlight.values()));
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        cmbMoonlight.setName("cmbMoonlight"); // NOI18N
        sightingIncludes.add(cmbMoonlight, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 370, 100, -1));

        spnHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnHours.setName("spnHours"); // NOI18N
        sightingIncludes.add(spnHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(335, 290, 40, -1));

        spnMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMinutes.setName("spnMinutes"); // NOI18N
        sightingIncludes.add(spnMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 290, 40, -1));

        spnMoonPhase.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spnMoonPhase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMoonPhase.setName("spnMoonPhase"); // NOI18N
        sightingIncludes.add(spnMoonPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(692, 370, 40, -1));

        spnLatDegrees.setModel(new javax.swing.SpinnerNumberModel(0, 0, 90, 1));
        spnLatDegrees.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDegrees.setName("spnLatDegrees"); // NOI18N
        sightingIncludes.add(spnLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 350, 40, -1));

        spnLatMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 60, 1));
        spnLatMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatMinutes.setName("spnLatMinutes"); // NOI18N
        sightingIncludes.add(spnLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(479, 350, 40, -1));

        spnLatSeconds.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(60.0f), Float.valueOf(0.1f)));
        spnLatSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatSeconds.setName("spnLatSeconds"); // NOI18N
        spnLatSeconds.setValue(0.0f);
        sightingIncludes.add(spnLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(519, 350, 50, -1));

        spnLatDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 91.0d, 0.1d));
        spnLatDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDecimal.setName("spnLatDecimal"); // NOI18N
        sightingIncludes.add(spnLatDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 350, 130, -1));

        spnLonDegrees.setModel(new javax.swing.SpinnerNumberModel(0, 0, 180, 1));
        spnLonDegrees.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDegrees.setName("spnLonDegrees"); // NOI18N
        sightingIncludes.add(spnLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 370, 40, -1));

        spnLonMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 60, 1));
        spnLonMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonMinutes.setName("spnLonMinutes"); // NOI18N
        sightingIncludes.add(spnLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(479, 370, 40, -1));

        spnLonSeconds.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(60.0f), Float.valueOf(0.1f)));
        spnLonSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonSeconds.setName("spnLonSeconds"); // NOI18N
        sightingIncludes.add(spnLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(519, 370, 50, -1));

        spnLonDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 181.0d, 0.1d));
        spnLonDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDecimal.setName("spnLonDecimal"); // NOI18N
        sightingIncludes.add(spnLonDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 370, 130, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 965, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        if (sighting != null) {
            // Reset the border colors
            lblElement.setBorder(null);
            lblLocation.setBorder(null);
            lblVisit.setBorder(null);
            dtpSightingDate.setBorder(null);
            if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                // Set Location and Element
                sighting.setLocationName(location.getName());
                sighting.setElementName(element.getPrimaryName());
                sighting.setVisitName(visit.getName());

                // Set variables
                sighting.setDate(getDateFromFields());
                sighting.setAreaType((AreaType)cmbAreaType.getSelectedItem());
                sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
                sighting.setDetails(txtDetails.getText());
                sighting.setSightingEvidence((SightingEvidence)cmbEvidence.getSelectedItem());
                if (spnNumberOfElements.getValue().toString().length() > 0) {
                    try {
                        sighting.setNumberOfElements(Integer.parseInt(spnNumberOfElements.getValue().toString()));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
                sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
                sighting.setWeather((Weather)cmbWeather.getSelectedItem());
//                sighting.setSubArea((String)cmbSubArea.getSelectedItem());
                if (tblElement.getSelectedRowCount() > 0)
                    sighting.setElementName((String)tblElement.getValueAt(tblElement.getSelectedRow(),0));
                rdbDMS.setSelected(true);
                sighting.setLatitude((Latitudes)cmbLatitude.getSelectedItem());
                sighting.setLongitude((Longitudes)cmbLongitude.getSelectedItem());
                try {
                    sighting.setLatDegrees((Integer)spnLatDegrees.getValue());
                    sighting.setLatMinutes((Integer)spnLatMinutes.getValue());
                    sighting.setLatSecondsFloat((Float)spnLatSeconds.getValue());
                    sighting.setLonDegrees((Integer)spnLonDegrees.getValue());
                    sighting.setLonMinutes((Integer)spnLonMinutes.getValue());
                    sighting.setLonSecondsFloat((Float)spnLonSeconds.getValue());

                    if (evt != null) { // It will be null if the images are uploaded
                        // Save values on App to be able to reload them on next Sighting if button pressed
                        app.setPrevLat(sighting.getLatitude());
                        app.setPrevLatDeg((Integer)sighting.getLatDegrees());
                        app.setPrevLatMin((Integer)sighting.getLatMinutes());
                        app.setPrevLatSec((Float)sighting.getLatSecondsFloat());
                        app.setPrevLon(sighting.getLongitude());
                        app.setPrevLonDeg((Integer)sighting.getLonDegrees());
                        app.setPrevLonMin((Integer)sighting.getLonMinutes());
                        app.setPrevLonSec((Float)sighting.getLonSecondsFloat());
                    }
                }
                catch (NumberFormatException e) {
                    spnLatDegrees.setValue(0);
                    spnLatMinutes.setValue(0);
                    spnLatSeconds.setValue(0.0f);
                    spnLonDegrees.setValue(0);
                    spnLonMinutes.setValue(0);
                    spnLonSeconds.setValue(0.0f);
                }
                sighting.setMoonlight((Moonlight)cmbMoonlight.getSelectedItem());
                sighting.setMoonPhase((Integer)spnMoonPhase.getValue());

                // SAVE
                if (app.getDBI().createOrUpdate(sighting) == false) {
                    JOptionPane.showMessageDialog(this, "Could not save the Sighting", "Error Saving", JOptionPane.ERROR_MESSAGE);
                }

                // Premare to close dialog
                if (panelToRefresh != null) {
                    panelToRefresh.refreshTableForSightings();
                }
                // Close the dialog - (Evt is null if the Image Upload calls save method...)
                if (evt != null) {
                    JDialog dialog = (JDialog)getParent().getParent().getParent().getParent();
                    dialog.dispose();
                }
            }
            else {
                if (element == null)
                    lblElement.setBorder(new LineBorder(Color.RED, 3, true));
                else
                if (location == null)
                    lblLocation.setBorder(new LineBorder(Color.RED, 3, true));
                if (visit == null)
                    lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                if (dtpSightingDate.getDate() == null)
                    dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
            }
        }
}//GEN-LAST:event_btnUpdateSightingActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Does not get called
    }//GEN-LAST:event_formComponentShown

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        if (sighting != null) {
            boolean loadDate = false;
            if (dtpSightingDate.getDate() == null) {
                dtpSightingDate.setDate(Calendar.getInstance().getTime());
                loadDate = true;
            }
            btnUpdateSightingActionPerformed(null);
            if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                imageIndex = Utils.uploadImage("SIGHTING-" + sighting.getSightingCounter(), "Sightings"+File.separatorChar+sighting.toString(), this, lblImage, 300, app);
                setupNumberOfImages();
                if (loadDate) {
                    btnGetDateFromImageActionPerformed(null);
                }
                // Save
                btnUpdateSightingActionPerformed(null);
            }
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.previousImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
            setupNumberOfImages();
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (sighting != null) {
            if (tblElement.getSelectedRowCount() == 1) {
                element = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
                if (fotos.size() > 0)
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(fotos.get(0).getFileLocation()), 100));
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.nextImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, 300, app);
            setupNumberOfImages();
        }
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.removeImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, lblImage, app.getDBI(), app.getClass().getResource("resources/images/NoImage.gif"), 300, app);
            setupNumberOfImages();
            btnUpdateSightingActionPerformed(null);
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.setMainImage("SIGHTING-" + sighting.getSightingCounter(), imageIndex, app);
            setupNumberOfImages();
            btnUpdateSightingActionPerformed(null);
        }
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (sighting != null) {
            if (tblVisit.getSelectedRowCount() == 1) {
                visit = app.getDBI().find(new Visit(tblVisit.getValueAt(tblVisit.getSelectedRow(), 0).toString()));
            }
        }
}//GEN-LAST:event_tblVisitMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (sighting != null) {
            if (tblLocation.getSelectedRowCount() == 1) {
                location = app.getDBI().find(new Location(tblLocation.getValueAt(tblLocation.getSelectedRow(), 0).toString()));
//                if (location != null)
//                    cmbSubArea.setModel(new DefaultComboBoxModel(location.getSubAreas().toArray()));
                UtilTableGenerator.setupVeryShortVisitTable(tblVisit, location);
                visit = null;
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + location.getName()));
                if (fotos.size() > 0)
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(fotos.get(0).getFileLocation()), 100));
                else
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {

            }
        }
}//GEN-LAST:event_tblLocationMouseReleased

    private void chkElementTypeFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkElementTypeFilterActionPerformed
        if (sighting != null) {
            searchElement = new Element();
            if (!cmbElementType.isEnabled())
                searchElement.setType((ElementType)cmbElementType.getSelectedItem());
            UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
            cmbElementType.setEnabled(!cmbElementType.isEnabled());
            txtSearch.setText("");
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_chkElementTypeFilterActionPerformed

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        if (sighting != null) {
            searchElement = new Element((ElementType)cmbElementType.getSelectedItem());
            UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
            txtSearch.setText("");
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_cmbElementTypeActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (sighting != null) {
            searchElement = new Element();
            if (chkElementTypeFilter.isSelected())
                searchElement.setType((ElementType)cmbElementType.getSelectedItem());
            if (txtSearch.getText() != null) {
                if (txtSearch.getText().length() > 0)
                    searchElement.setPrimaryName(txtSearch.getText());
            }
            UtilTableGenerator.setupShortElementTable(tblElement, searchElement);
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearchLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchLocationActionPerformed
        if (sighting != null) {
            searchLocation = new Location();
            if (txtSearchLocation.getText() != null) {
                if (txtSearchLocation.getText().length() > 0)
                    searchLocation.setName(txtSearchLocation.getText());
            }
            UtilTableGenerator.setupShortLocationTable(tblLocation, searchLocation);
            // Clear Images
            lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            tblVisit.setModel(new DefaultTableModel(new String[]{"Select a Location"}, 0));
        }
    }//GEN-LAST:event_btnSearchLocationActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (sighting != null) {
            Utils.openFile("SIGHTING-" + sighting.getSightingCounter(), imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (location != null) {
            Utils.openFile("LOCATION-" + location.getName(), 0, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (element != null) {
            Utils.openFile("ELEMENT-" + element.getPrimaryName(), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSearchActionPerformed(null);
    }//GEN-LAST:event_txtSearchKeyPressed

    private void txtSearchLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSearchLocationActionPerformed(null);
    }//GEN-LAST:event_txtSearchLocationKeyPressed

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblLocationMouseReleased(null);
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void rdbDMSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbDMSItemStateChanged
        if (rdbDMS.isSelected()) {
            try {
                spnLatDegrees.setValue(Math.abs(LatLonConverter.getDegrees((Latitudes)cmbLatitude.getSelectedItem(), (Double)spnLatDecimal.getValue())));
                spnLonDegrees.setValue(Math.abs(LatLonConverter.getDegrees((Longitudes)cmbLongitude.getSelectedItem(), (Double)spnLonDecimal.getValue())));
                spnLatMinutes.setValue(LatLonConverter.getMinutes((Latitudes)cmbLatitude.getSelectedItem(),(Double) spnLatDecimal.getValue()));
                spnLonMinutes.setValue(LatLonConverter.getMinutes((Longitudes)cmbLongitude.getSelectedItem(), (Double)spnLonDecimal.getValue()));
                spnLatSeconds.setValue(LatLonConverter.getSeconds((Latitudes)cmbLatitude.getSelectedItem(), (Double)spnLatDecimal.getValue()));
                spnLonSeconds.setValue(LatLonConverter.getSeconds((Longitudes)cmbLongitude.getSelectedItem(), (Double)spnLonDecimal.getValue()));
                // If the parsing worked then continue
                spnLatDegrees.setVisible(true);
                spnLonDegrees.setVisible(true);
                spnLatMinutes.setVisible(true);
                spnLonMinutes.setVisible(true);
                spnLatSeconds.setVisible(true);
                spnLonSeconds.setVisible(true);
                spnLatDecimal.setVisible(false);
                spnLonDecimal.setVisible(false);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The input format should be <decimal>", "Wrong Number Format", JOptionPane.INFORMATION_MESSAGE);
                rdbDD.setSelected(true);
            }
        }
    }//GEN-LAST:event_rdbDMSItemStateChanged

    private void rdbDDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbDDItemStateChanged
        if (rdbDD.isSelected()) {
            try {
                spnLatDecimal.setValue(Math.abs(LatLonConverter.getDecimalDegree((Latitudes)cmbLatitude.getSelectedItem(), (Integer)spnLatDegrees.getValue(), (Integer)spnLatMinutes.getValue(), (Float)spnLatSeconds.getValue())));
                spnLonDecimal.setValue(Math.abs(LatLonConverter.getDecimalDegree((Longitudes)cmbLongitude.getSelectedItem(), (Integer)spnLonDegrees.getValue(), (Integer)spnLonMinutes.getValue(), (Float)spnLonSeconds.getValue())));
                // If the parsing worked then continue
                spnLatDegrees.setVisible(false);
                spnLonDegrees.setVisible(false);
                spnLatMinutes.setVisible(false);
                spnLonMinutes.setVisible(false);
                spnLatSeconds.setVisible(false);
                spnLonSeconds.setVisible(false);
                spnLatDecimal.setVisible(true);
                spnLonDecimal.setVisible(true);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The input format should be <integer> <integer> <decimal>", "Wrong Number Format", JOptionPane.INFORMATION_MESSAGE);
                rdbDMS.setSelected(true);
            }
        }
    }//GEN-LAST:event_rdbDDItemStateChanged

    private void btnUseLocationGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseLocationGPSActionPerformed
        if (location != null) {
            rdbDMS.setSelected(true);
            cmbLatitude.setSelectedItem(location.getLatitude());
            spnLatDegrees.setValue(location.getLatDegrees());
            spnLatMinutes.setValue(location.getLatMinutes());
            spnLatSeconds.setValue(location.getLatSecondsFloat());
            cmbLongitude.setSelectedItem(location.getLongitude());
            spnLonDegrees.setValue(location.getLonDegrees());
            spnLonMinutes.setValue(location.getLonMinutes());
            spnLonSeconds.setValue(location.getLonSecondsFloat());
        }
    }//GEN-LAST:event_btnUseLocationGPSActionPerformed

    private void btnUsePrevGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsePrevGPSActionPerformed
        rdbDMS.setSelected(true);
        cmbLatitude.setSelectedItem(app.getPrevLat());
        spnLatDegrees.setValue(app.getPrevLatDeg());
        spnLatMinutes.setValue(app.getPrevLatMin());
        spnLatSeconds.setValue(app.getPrevLatSec());
        cmbLongitude.setSelectedItem(app.getPrevLon());
        spnLonDegrees.setValue(app.getPrevLonDeg());
        spnLonMinutes.setValue(app.getPrevLonMin());
        spnLonSeconds.setValue(app.getPrevLonSec());
    }//GEN-LAST:event_btnUsePrevGPSActionPerformed

    private void btnGetDateFromImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetDateFromImageActionPerformed
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
        if (fotos.size() > 0) {
            WildLogFile tempFoto = fotos.get(imageIndex);
            try {
                Metadata meta = JpegMetadataReader.readMetadata(new File(tempFoto.getOriginalFotoLocation()));
                Iterator directories = meta.getDirectoryIterator();
                breakAllWhiles: while (directories.hasNext()) {
                    Directory directory = (Directory)directories.next();
                    Iterator tags = directory.getTagIterator();
                    while (tags.hasNext()) {
                        Tag tag = (Tag)tags.next();
                        if (tag.getTagName().equalsIgnoreCase("Date/Time Original")) {
                            try {
                                SimpleDateFormat f = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                                try {
                                    Date tempDate = f.parse(tag.getDescription());
                                    dtpSightingDate.setDate(tempDate);
                                    spnHours.setValue(tempDate.getHours());
                                    spnMinutes.setValue(tempDate.getMinutes());
                                    cmbTimeFormat.setSelectedIndex(0);
                                    btnCalculateTimeCategoryActionPerformed(null);
                                    break breakAllWhiles;
                                }
                                catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            catch (MetadataException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
            catch (JpegProcessingException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnGetDateFromImageActionPerformed

    private void btnCalculateTimeCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateTimeCategoryActionPerformed
        if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null 
                && !cmbLatitude.getSelectedItem().equals(Latitudes.NONE) && !cmbLongitude.getSelectedItem().equals(Longitudes.NONE)) {
            btnUpdateSightingActionPerformed(null);
            double latitude = LatLonConverter.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSecondsFloat());
            double longitude = LatLonConverter.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSecondsFloat());
            cmbTimeOfDay.setSelectedItem(AstroUtils.getSunCategory(getDateFromFields(), latitude, longitude));
        }
        else {
            JOptionPane.showMessageDialog(this, "Please make sure to first specify details for the Creature, Location, Visit and GPS co-ordinate fields.", "Could not calculate the time category.", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCalculateTimeCategoryActionPerformed

    private void btnCalculateMoonPhaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateMoonPhaseActionPerformed
        if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null
                && !cmbLatitude.getSelectedItem().equals(Latitudes.NONE) && !cmbLongitude.getSelectedItem().equals(Longitudes.NONE)) {
            btnUpdateSightingActionPerformed(null);
            double latitude = LatLonConverter.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSecondsFloat());
            double longitude = LatLonConverter.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSecondsFloat());
            spnMoonPhase.setValue(AstroUtils.getMoonPhase(sighting.getDate()));
            cmbMoonlight.setSelectedItem(AstroUtils.getMoonlight(sighting.getDate(), latitude, longitude));
        }
        else {
            JOptionPane.showMessageDialog(this, "Please make sure to first specify details for the Creature, Location, Visit and GPS co-ordinate fields.", "Could not calculate the moon information.", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCalculateMoonPhaseActionPerformed

    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    public boolean isDisableEditing() {
        return disableEditing;
    }

    public void setDisableEditing(boolean disableEditing) {
        this.disableEditing = disableEditing;
    }

    private Date getDateFromFields() {
        Date temp = dtpSightingDate.getDate();
        try {
            if (cmbTimeFormat.getSelectedItem().equals(TimeFormat.PM)) {
                int tempHours = 12 + (Integer)spnHours.getValue();
                if (tempHours >= 24) temp.setHours(tempHours - 12);
                else temp.setHours(tempHours);
            }
            else
                temp.setHours((Integer)spnHours.getValue());
            temp.setMinutes((Integer)spnMinutes.getValue());
        }
        catch (NumberFormatException e) {
            temp.setHours(0);
            temp.setMinutes(0);
        }
        return temp;
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculateMoonPhase;
    private javax.swing.JButton btnCalculateTimeCategory;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGetDateFromImage;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchLocation;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JButton btnUseLocationGPS;
    private javax.swing.JButton btnUsePrevGPS;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkElementTypeFilter;
    private javax.swing.JComboBox cmbAreaType;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbEvidence;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLongitude;
    private javax.swing.JComboBox cmbMoonlight;
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
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
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
    private javax.swing.JRadioButton rdbDD;
    private javax.swing.JRadioButton rdbDMS;
    private javax.swing.JPanel sightingIncludes;
    private javax.swing.JSpinner spnHours;
    private javax.swing.JSpinner spnLatDecimal;
    private javax.swing.JSpinner spnLatDegrees;
    private javax.swing.JSpinner spnLatMinutes;
    private javax.swing.JSpinner spnLatSeconds;
    private javax.swing.JSpinner spnLonDecimal;
    private javax.swing.JSpinner spnLonDegrees;
    private javax.swing.JSpinner spnLonMinutes;
    private javax.swing.JSpinner spnLonSeconds;
    private javax.swing.JSpinner spnMinutes;
    private javax.swing.JSpinner spnMoonPhase;
    private javax.swing.JSpinner spnNumberOfElements;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchLocation;
    // End of variables declaration//GEN-END:variables
    
}
