package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.TimeFormat;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.panels.inaturalist.dialogs.INatSightingDialog;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import static wildlog.ui.utils.UtilsUI.doClipboardCopy;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogFileExtentions;


public class PanelSighting extends JDialog implements PanelNeedsRefreshWhenDataChanges {
    private Location locationWL;
    private Visit visit;
    private Element element;
    private Sighting sighting;
    private int imageIndex;
    private WildLogApp app;
    private PanelNeedsRefreshWhenDataChanges panelToRefresh;
    private boolean treatAsNewSighting;
    private boolean disableEditing = false;
    private boolean bulkUploadMode = false;
    private boolean bulkEditMode = false;
    private TimeFormat prevTimeFormat;

    // Constructor
    /**
     * Create a new PanelSighting object. This panel can function in different ways
     * depending on the input provided as parameters to this constructor.
     * NOTE: The passed in Sighting should not ever be null.
     * For a new Sighting pass in a new object instead.
     * @param inApp
     * @param inOwner
     * @param inSighting - Should never be null.
     * @param inTitle
     * @param inLocation - Can be null if unknown.
     * @param inVisit - Can be null if unknown.
     * @param inElement - Can be null if unknown.
     * @param inPanelToRefresh - The object to call refresh on when the sighting is done processing.
     * @param inTreatAsNewSighting
     * @param inDisableEditing
     * @param inBulkUploadMode
     * @param inBulkEditMode
     */
    public PanelSighting(WildLogApp inApp, JFrame inOwner, String inTitle, Sighting inSighting, Location inLocation, Visit inVisit,
            Element inElement, PanelNeedsRefreshWhenDataChanges inPanelToRefresh,
            boolean inTreatAsNewSighting, boolean inDisableEditing, boolean inBulkUploadMode, boolean inBulkEditMode) {
        super(inOwner, inTitle);
        if (inSighting == null) {
            WildLogApp.LOGGER.log(Level.ERROR, "PanelSighting: The passed in Sighting is not allowed to be null.");
            this.closeThisDialog();
        }
        sighting = inSighting;
        panelToRefresh = inPanelToRefresh;
        treatAsNewSighting = inTreatAsNewSighting;
        disableEditing = inDisableEditing;
        bulkUploadMode = inBulkUploadMode;
        bulkEditMode = inBulkEditMode;
        app = inApp;
        locationWL = inLocation;
        visit = inVisit;
        element = inElement;
        imageIndex = 0;
        // Auto-generated code
        initComponents();
        // Setup Location and Element tables
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
        UtilsTableGenerator.setupLocationTableSmall(app, tblLocation, null);
        // Setup default values for tables
        final int columnToUse;
        if (app.getWildLogOptions().isUseThumbnailTables()) {
            columnToUse = 1;
        }
        else {
            columnToUse = 0;
        }
        if (locationWL != null) {
            // Wag eers vir die table om klaar te load voor ek iets probeer select
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < tblLocation.getRowCount(); t++) {
                        if (tblLocation.getValueAt(t, columnToUse).equals(locationWL.getName())) {
                            tblLocation.getSelectionModel().setSelectionInterval(t, t);
                            int scrollRow = t;
                            if (t < (tblLocation.getRowCount()) - 1) {
                                scrollRow = t + 1;
                            }
                            tblLocation.scrollRectToVisible(tblLocation.getCellRect(scrollRow, 0, true));
                            break;
                        }
                    }
                }
            });
        }
        if (element != null) {
            // Wag eers vir die table om klaar te load voor ek iets probeer select
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < tblElement.getRowCount(); t++) {
                        if (tblElement.getValueAt(t, columnToUse).equals(element.getPrimaryName())) {
                            tblElement.getSelectionModel().setSelectionInterval(t, t);
                            int scrollRow = t;
                            if (t < (tblElement.getRowCount()) - 1) {
                                scrollRow = t + 1;
                            }
                            tblElement.scrollRectToVisible(tblElement.getCellRect(scrollRow, 0, true));
                            break;
                        }
                    }
                }
            });
        }
        // Setup location and element images
        if (locationWL != null) {
            int fotoCount = app.getDBI().countWildLogFiles(0, locationWL.getWildLogFileID());
            if (fotoCount > 0) {
                UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), 0, lblLocationImage, WildLogThumbnailSizes.SMALL, app);
            }
            else {
                lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
            }
        }
        else {
            lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
        if (element != null) {
            int fotoCount = app.getDBI().countWildLogFiles(0, element.getWildLogFileID());
            if (fotoCount > 0) {
                UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, lblElementImage, WildLogThumbnailSizes.SMALL, app);
            }
            else {
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
            }
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
        // Setup visit table after the Location has been setup
        if (locationWL != null) {
            UtilsTableGenerator.setupVisitTableSmallWithType(app, tblVisit, locationWL.getID());
        }
        else {
            UtilsTableGenerator.setupVisitTableSmallWithType(app, tblVisit, 0);
        }
        // Select the visit
        if (visit != null) {
            // Wag eers vir die table om klaar te load voor ek iets probeer select
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < tblVisit.getRowCount(); t++) {
                        if (tblVisit.getValueAt(t, columnToUse).equals(visit.getName())) {
                            tblVisit.getSelectionModel().setSelectionInterval(t, t);
                            int scrollRow = t;
                            if (t < (tblVisit.getRowCount()) - 1) {
                                scrollRow = t + 1;
                            }
                            tblVisit.scrollRectToVisible(tblVisit.getCellRect(scrollRow, 0, true));
                            break;
                        }
                    }
                }
            });
        }
        //Setup Table ordering and sorting
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);
        // Lat Lon stuff
        txtLatitude.setText(UtilsGPS.getLatitudeString(sighting));
        txtLongitude.setText(UtilsGPS.getLatitudeString(sighting));
        // Spinners stuff
        SpinnerFixer.configureSpinners(spnNumberOfElements);
        SpinnerFixer.configureSpinners(spnHours);
        SpinnerFixer.configureSpinners(spnMinutes);
        SpinnerFixer.configureSpinners(spnSeconds);
        SpinnerFixer.configureSpinners(spnMoonPhase);
        SpinnerFixer.configureSpinners(spnTemperature);
        SpinnerFixer.configureSpinners(spnDurationMinutes);
        SpinnerFixer.configureSpinners(spnDurationSeconds);
        // Handle editable flags
        if (!disableEditing && !bulkUploadMode && !bulkEditMode) {
            // Only enable drag-and-drop if editing is allowed and not in bulk upload mode
            FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
                @Override
                public void filesDropped(List<File> inFiles) {
                    uploadImage(inFiles);
                }
            });
            // Attach clipboard
            UtilsUI.attachClipboardPopup(txtSearchLocation);
            // Setup searcher
            UtilsUI.attachKeyListernerToFilterTableRows(txtSearchLocation, tblLocation);
        }
        if (!disableEditing) {
            // Attach clipboard
            UtilsUI.attachClipboardPopup(txtSearch);
            UtilsUI.attachClipboardPopup(txtDetails);
            UtilsUI.attachClipboardPopup(txtTag);
            UtilsUI.attachClipboardPopup((JTextComponent)spnHours.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnMinutes.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnSeconds.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnMoonPhase.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnNumberOfElements.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnTemperature.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnDurationMinutes.getEditor().getComponent(0));
            UtilsUI.attachClipboardPopup((JTextComponent)spnDurationSeconds.getEditor().getComponent(0));
            // Setup searcher
            UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
        }
        // Setup the image count label
        setupNumberOfImages();
        // Setup default values for input fields
        if (treatAsNewSighting) {
            cmbCertainty.setSelectedItem(Certainty.SURE);
            cmbLifeStatus.setSelectedItem(LifeStatus.ALIVE);
            cmbEvidence.setSelectedItem(SightingEvidence.SEEN);
            cmbViewRating.setSelectedItem(ViewRating.NORMAL);
            cmbLifeStatus.setSelectedItem(LifeStatus.ALIVE);
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.UNKNOWN);
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
            spnNumberOfElements.setValue(0);
            spnMoonPhase.setValue(-1);
            spnTemperature.setValue(0.0);
        }
        else {
            // Setup the Sighting info
            setupSightingInfo();
        }
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        ActionListener escListiner = UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Hack to fix the wierd focus issue to get the ESC to work (related to the datepicker)
        this.setFocusable(true);
        dtpSightingDate.getEditor().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
        // Make dates pretty
        dtpSightingDate.getComponent(1).setBackground(sightingIncludes.getBackground());
        // Stel fokus op die Element search box, sodat mens maklik datelik kan begin soek
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtSearch.requestFocus();
            }
        });
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            btnINaturalist.setEnabled(false);
            btnINaturalist.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                // Volunteers aren't allowed to change the location or visit once it has been set
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (tblLocation.getSelectedRowCount() > 0) {
                            tblLocation.setEnabled(false);
                            txtSearchLocation.setEnabled(false);
                        }
                        if (tblVisit.getSelectedRowCount() > 0) {
                            tblVisit.setEnabled(false);
                        }
                    }
                });
            }
        }
    }

    private void uploadFiles(List<File> inFiles) {
        UtilsFileProcessing.performFileUpload(sighting,
                Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(sighting.toPath()),
                inFiles.toArray(new File[inFiles.size()]),
                new Runnable() {
                    @Override
                    public void run() {
                        imageIndex = 0;
                        // Load the first image
                        UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
                        // Update the label showing the number of images
                        setupNumberOfImages();
                        // Calculate duration
                        if ((int)spnDurationMinutes.getValue() == 0 && (double)spnDurationSeconds.getValue() == 0.0) {
                            btnCalculateDurationActionPerformed(null);
                        }
                        // Save all the changes and auto populated fields
                        btnUpdateSightingActionPerformed(null);
                    }
                }, 
                app, true, this, true, true);
    }

    private void setupSightingInfo() {
        // Display the ID
        lblSightingID.setText("<html><p align='center'>Observation ID: " + Long.toString(sighting.getID()) + "</p></html>");
        // Load the values from the Sighting object
        setUIFieldsFromSightingDate(sighting.getDate());
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        txtDetails.setText(sighting.getDetails());
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        spnNumberOfElements.setValue(sighting.getNumberOfElements());
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbWeather.setSelectedItem(sighting.getWeather());
        txtLatitude.setText(UtilsGPS.getLatitudeString(sighting));
        txtLongitude.setText(UtilsGPS.getLongitudeString(sighting));
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
        int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
    }

    private void setUIFieldsFromSightingDate(Date inDate) {
        dtpSightingDate.setDate(inDate);
        if (inDate != null) {
            LocalTime time = UtilsTime.getLocalTimeFromDate(inDate);
            spnHours.setValue(time.getHour());
            spnMinutes.setValue(time.getMinute());
            spnSeconds.setValue(time.getSecond());
            cmbTimeAccuracy.setSelectedItem(sighting.getTimeAccuracy());
        }
        else {
            spnHours.setValue(0);
            spnMinutes.setValue(0);
            spnSeconds.setValue(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sightingIncludes = new javax.swing.JPanel();
        lblElementImage = new javax.swing.JLabel();
        lblLocationImage = new javax.swing.JLabel();
        pnlImageControls = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        btnDeleteImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        pnlElementControls = new javax.swing.JPanel();
        lblElement = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        sclElement = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        cmbElementType = new javax.swing.JComboBox();
        btnAddNewElement = new javax.swing.JButton();
        pnlLocationControls = new javax.swing.JPanel();
        lblLocation = new javax.swing.JLabel();
        txtSearchLocation = new javax.swing.JTextField();
        sclLocation = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        btnAddNewLocation = new javax.swing.JButton();
        pnlVisitControls = new javax.swing.JPanel();
        lblVisit = new javax.swing.JLabel();
        sclVisit = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        btnAddNewVisit = new javax.swing.JButton();
        pnlSightingFields = new javax.swing.JPanel();
        lblTimeOfDayInfo = new javax.swing.JLabel();
        spnNumberOfElements = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cmbMoonlight = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        spnHours = new javax.swing.JSpinner();
        spnMinutes = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        cmbEvidence = new javax.swing.JComboBox();
        spnMoonPhase = new javax.swing.JSpinner();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        btnGPS = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        spnTemperature = new javax.swing.JSpinner();
        cmbTemperatureUnits = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cmbSex = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cmbLifeStatus = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        txtTag = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        spnDurationMinutes = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dtpSightingDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        spnDurationSeconds = new javax.swing.JSpinner();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cmbTimeAccuracy = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        cmbAge = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cmbTimeFormat = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetails = new javax.swing.JTextArea();
        cmbWeather = new javax.swing.JComboBox();
        cmbTimeOfDay = new javax.swing.JComboBox();
        cmbViewRating = new javax.swing.JComboBox();
        cmbCertainty = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        spnSeconds = new javax.swing.JSpinner();
        pnlButtons = new javax.swing.JPanel();
        lblSightingID = new javax.swing.JLabel();
        btnCalculateDuration = new javax.swing.JButton();
        btnCalculateSunAndMoon = new javax.swing.JButton();
        btnGetGPSFromImage = new javax.swing.JButton();
        btnGetDateFromImage = new javax.swing.JButton();
        btnUpdateSighting = new javax.swing.JButton();
        btnINaturalist = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Observation");
        setBackground(new java.awt.Color(208, 204, 181));
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(1005, 685));
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        sightingIncludes.setBackground(new java.awt.Color(208, 204, 181));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(985, 645));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(985, 645));

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

        pnlImageControls.setBackground(new java.awt.Color(208, 204, 181));
        pnlImageControls.setName("pnlImageControls"); // NOI18N

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

        btnDeleteImage.setBackground(new java.awt.Color(208, 204, 181));
        btnDeleteImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteImage.setText("Delete File");
        btnDeleteImage.setToolTipText("Delete the current file.");
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setIconTextGap(2);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        btnSetMainImage.setBackground(new java.awt.Color(208, 204, 181));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnSetMainImage.setText("Set as First");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Observation.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setIconTextGap(2);
        btnSetMainImage.setMargin(new java.awt.Insets(2, 1, 2, 1));
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });

        btnPreviousImage.setBackground(new java.awt.Color(208, 204, 181));
        btnPreviousImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImage.setToolTipText("Load previous file.");
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setEnabled( !bulkEditMode && !bulkUploadMode);
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });

        btnNextImage.setBackground(new java.awt.Color(208, 204, 181));
        btnNextImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImage.setToolTipText("Load next file.");
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setEnabled( !bulkEditMode && !bulkUploadMode);
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });

        btnUploadImage.setBackground(new java.awt.Color(208, 204, 181));
        btnUploadImage.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("<html><u>Upload Files</u></html>");
        btnUploadImage.setToolTipText("<html>Upload a file for this Observation. <br/>You can also drag-and-drop files onto the above box to upload it. <br/>(Note: Drag-and-drop only works on supported platforms.)</html>");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setIconTextGap(2);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        javax.swing.GroupLayout pnlImageControlsLayout = new javax.swing.GroupLayout(pnlImageControls);
        pnlImageControls.setLayout(pnlImageControlsLayout);
        pnlImageControlsLayout.setHorizontalGroup(
            pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageControlsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlImageControlsLayout.createSequentialGroup()
                        .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlImageControlsLayout.createSequentialGroup()
                                .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        pnlImageControlsLayout.setVerticalGroup(
            pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageControlsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlImageControlsLayout.createSequentialGroup()
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(pnlImageControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlImageControlsLayout.createSequentialGroup()
                                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        pnlElementControls.setBackground(new java.awt.Color(208, 204, 181));
        pnlElementControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlElementControls.setName("pnlElementControls"); // NOI18N
        pnlElementControls.setPreferredSize(new java.awt.Dimension(250, 55));

        lblElement.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblElement.setText("Creature:");
        lblElement.setToolTipText("It is required to fill in this field.");
        lblElement.setName("lblElement"); // NOI18N

        txtSearch.setEnabled(!disableEditing);
        txtSearch.setName("txtSearch"); // NOI18N

        sclElement.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclElement.setName("sclElement"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        sclElement.setViewportView(tblElement);

        cmbElementType.setMaximumRowCount(11);
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

        btnAddNewElement.setBackground(new java.awt.Color(208, 204, 181));
        btnAddNewElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAddNewElement.setToolTipText("Add new Creature.");
        btnAddNewElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddNewElement.setEnabled(!disableEditing);
        btnAddNewElement.setFocusPainted(false);
        btnAddNewElement.setFocusable(false);
        btnAddNewElement.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnAddNewElement.setName("btnAddNewElement"); // NOI18N
        btnAddNewElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewElementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlElementControlsLayout = new javax.swing.GroupLayout(pnlElementControls);
        pnlElementControls.setLayout(pnlElementControlsLayout);
        pnlElementControlsLayout.setHorizontalGroup(
            pnlElementControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlElementControlsLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlElementControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlElementControlsLayout.createSequentialGroup()
                        .addComponent(lblElement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddNewElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlElementControlsLayout.createSequentialGroup()
                        .addComponent(txtSearch)
                        .addGap(2, 2, 2)
                        .addComponent(cmbElementType, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sclElement, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        pnlElementControlsLayout.setVerticalGroup(
            pnlElementControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlElementControlsLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlElementControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddNewElement, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblElement))
                .addGap(3, 3, 3)
                .addGroup(pnlElementControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbElementType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(sclElement, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pnlLocationControls.setBackground(new java.awt.Color(208, 204, 181));
        pnlLocationControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlLocationControls.setName("pnlLocationControls"); // NOI18N
        pnlLocationControls.setPreferredSize(new java.awt.Dimension(300, 55));

        lblLocation.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblLocation.setText("Place:");
        lblLocation.setToolTipText("It is required to fill in this field.");
        lblLocation.setName("lblLocation"); // NOI18N

        txtSearchLocation.setEnabled(!disableEditing && !bulkUploadMode);
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N

        sclLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclLocation.setName("sclLocation"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        sclLocation.setViewportView(tblLocation);

        btnAddNewLocation.setBackground(new java.awt.Color(208, 204, 181));
        btnAddNewLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAddNewLocation.setToolTipText("Add new Place.");
        btnAddNewLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddNewLocation.setEnabled(!disableEditing && !bulkUploadMode);
        btnAddNewLocation.setFocusPainted(false);
        btnAddNewLocation.setFocusable(false);
        btnAddNewLocation.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnAddNewLocation.setName("btnAddNewLocation"); // NOI18N
        btnAddNewLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewLocationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLocationControlsLayout = new javax.swing.GroupLayout(pnlLocationControls);
        pnlLocationControls.setLayout(pnlLocationControlsLayout);
        pnlLocationControlsLayout.setHorizontalGroup(
            pnlLocationControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLocationControlsLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlLocationControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sclLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlLocationControlsLayout.createSequentialGroup()
                        .addComponent(lblLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddNewLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSearchLocation, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(3, 3, 3))
        );
        pnlLocationControlsLayout.setVerticalGroup(
            pnlLocationControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocationControlsLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlLocationControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddNewLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(txtSearchLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(sclLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pnlVisitControls.setBackground(new java.awt.Color(208, 204, 181));
        pnlVisitControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlVisitControls.setName("pnlVisitControls"); // NOI18N
        pnlVisitControls.setPreferredSize(new java.awt.Dimension(250, 273));

        lblVisit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblVisit.setText("Period:");
        lblVisit.setToolTipText("It is required to fill in this field.");
        lblVisit.setName("lblVisit"); // NOI18N

        sclVisit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        sclVisit.setName("sclVisit"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setEnabled(!disableEditing && !bulkUploadMode);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
        });
        sclVisit.setViewportView(tblVisit);

        btnAddNewVisit.setBackground(new java.awt.Color(208, 204, 181));
        btnAddNewVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAddNewVisit.setToolTipText("Add new Period.");
        btnAddNewVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddNewVisit.setEnabled(!disableEditing && !bulkUploadMode);
        btnAddNewVisit.setFocusPainted(false);
        btnAddNewVisit.setFocusable(false);
        btnAddNewVisit.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnAddNewVisit.setName("btnAddNewVisit"); // NOI18N
        btnAddNewVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewVisitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVisitControlsLayout = new javax.swing.GroupLayout(pnlVisitControls);
        pnlVisitControls.setLayout(pnlVisitControlsLayout);
        pnlVisitControlsLayout.setHorizontalGroup(
            pnlVisitControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVisitControlsLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlVisitControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sclVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlVisitControlsLayout.createSequentialGroup()
                        .addComponent(lblVisit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddNewVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3))
        );
        pnlVisitControlsLayout.setVerticalGroup(
            pnlVisitControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVisitControlsLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlVisitControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAddNewVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVisit))
                .addGap(3, 3, 3)
                .addComponent(sclVisit, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pnlSightingFields.setBackground(new java.awt.Color(208, 204, 181));
        pnlSightingFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlSightingFields.setName("pnlSightingFields"); // NOI18N

        lblTimeOfDayInfo.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lblTimeOfDayInfo.setForeground(new java.awt.Color(102, 102, 102));
        lblTimeOfDayInfo.setText("More Info...");
        lblTimeOfDayInfo.setToolTipText("Click here to see the definitions for each category.");
        lblTimeOfDayInfo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblTimeOfDayInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTimeOfDayInfo.setName("lblTimeOfDayInfo"); // NOI18N
        lblTimeOfDayInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblTimeOfDayInfoMousePressed(evt);
            }
        });

        spnNumberOfElements.setModel(new javax.swing.SpinnerNumberModel(0, 0, 2147483647, 1));
        spnNumberOfElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnNumberOfElements.setEditor(new javax.swing.JSpinner.NumberEditor(spnNumberOfElements, "#"));
        spnNumberOfElements.setEnabled(!disableEditing);
        spnNumberOfElements.setName("spnNumberOfElements"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Time:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText("Moon Phase:");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel5.setText("% Full");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel11.setText("Moonlight:");
        jLabel11.setName("jLabel11"); // NOI18N

        cmbMoonlight.setModel(new DefaultComboBoxModel(Moonlight.values()));
        cmbMoonlight.setSelectedItem(sighting.getMoonlight());
        cmbMoonlight.setEnabled(!disableEditing);
        cmbMoonlight.setFocusable(false);
        cmbMoonlight.setName("cmbMoonlight"); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("GPS:");
        jLabel18.setName("jLabel18"); // NOI18N

        spnHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnHours.setToolTipText("Hours");
        spnHours.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(144, 200, 99)));
        spnHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnHours.setEditor(new javax.swing.JSpinner.NumberEditor(spnHours, "0"));
        spnHours.setEnabled(!disableEditing);
        spnHours.setFocusable(false);
        spnHours.setName("spnHours"); // NOI18N
        spnHours.setPreferredSize(new java.awt.Dimension(35, 20));
        spnHours.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnHoursStateChanged(evt);
            }
        });

        spnMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnMinutes.setToolTipText("Minutes");
        spnMinutes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(144, 200, 99)));
        spnMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMinutes.setEditor(new javax.swing.JSpinner.NumberEditor(spnMinutes, "0"));
        spnMinutes.setEnabled(!disableEditing);
        spnMinutes.setFocusable(false);
        spnMinutes.setName("spnMinutes"); // NOI18N
        spnMinutes.setPreferredSize(new java.awt.Dimension(35, 20));
        spnMinutes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMinutesStateChanged(evt);
            }
        });

        jLabel4.setText("Evidence:");
        jLabel4.setName("jLabel4"); // NOI18N

        cmbEvidence.setMaximumRowCount(12);
        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setEnabled(!disableEditing);
        cmbEvidence.setFocusable(false);
        cmbEvidence.setName("cmbEvidence"); // NOI18N

        spnMoonPhase.setModel(new javax.swing.SpinnerNumberModel(0, -1, 100, 1));
        spnMoonPhase.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMoonPhase.setEditor(new javax.swing.JSpinner.NumberEditor(spnMoonPhase, "##"));
        spnMoonPhase.setEnabled(!disableEditing);
        spnMoonPhase.setName("spnMoonPhase"); // NOI18N

        txtLatitude.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(144, 200, 99)));
        txtLatitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N

        txtLongitude.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(144, 200, 99)));
        txtLongitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N

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

        jLabel7.setText("Temperature:");
        jLabel7.setName("jLabel7"); // NOI18N

        spnTemperature.setModel(new javax.swing.SpinnerNumberModel(0.0d, -130.0d, 140.0d, 1.0d));
        spnTemperature.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnTemperature.setEditor(new javax.swing.JSpinner.NumberEditor(spnTemperature, "###.##"));
        spnTemperature.setEnabled(!disableEditing);
        spnTemperature.setName("spnTemperature"); // NOI18N
        spnTemperature.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTemperatureStateChanged(evt);
            }
        });

        cmbTemperatureUnits.setModel(new DefaultComboBoxModel(UnitsTemperature.values()));
        cmbTemperatureUnits.setSelectedItem(sighting.getUnitsTemperature());
        cmbTemperatureUnits.setEnabled(!disableEditing);
        cmbTemperatureUnits.setFocusable(false);
        cmbTemperatureUnits.setName("cmbTemperatureUnits"); // NOI18N

        jLabel14.setText("Time of Day:");
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setText("Gender:");
        jLabel15.setName("jLabel15"); // NOI18N

        cmbSex.setModel(new DefaultComboBoxModel(Sex.values()));
        cmbSex.setSelectedItem(sighting.getSex());
        cmbSex.setEnabled(!disableEditing);
        cmbSex.setFocusable(false);
        cmbSex.setName("cmbSex"); // NOI18N

        jLabel16.setText("Life Status:");
        jLabel16.setName("jLabel16"); // NOI18N

        cmbLifeStatus.setModel(new DefaultComboBoxModel(LifeStatus.values()));
        cmbLifeStatus.setSelectedItem(sighting.getLifeStatus());
        cmbLifeStatus.setEnabled(!disableEditing);
        cmbLifeStatus.setFocusable(false);
        cmbLifeStatus.setName("cmbLifeStatus"); // NOI18N

        jLabel17.setText("Info Tag:");
        jLabel17.setName("jLabel17"); // NOI18N

        txtTag.setText(sighting.getTag());
        txtTag.setToolTipText("This field can be used to \"tag\" the Observation. For instance using it for the individual animal's ID, or a sub-location name, etc.");
        txtTag.setEnabled(!disableEditing);
        txtTag.setName("txtTag"); // NOI18N

        jLabel19.setText("Duration:");
        jLabel19.setName("jLabel19"); // NOI18N

        spnDurationMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1440, 1));
        spnDurationMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnDurationMinutes.setEditor(new javax.swing.JSpinner.NumberEditor(spnDurationMinutes, "0"));
        spnDurationMinutes.setEnabled(!disableEditing);
        spnDurationMinutes.setName("spnDurationMinutes"); // NOI18N

        jLabel20.setText("minutes");
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Date:");
        jLabel6.setToolTipText("It is required to fill in this field.");
        jLabel6.setName("jLabel6"); // NOI18N

        dtpSightingDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setEnabled(!disableEditing);
        dtpSightingDate.setFocusable(false);
        dtpSightingDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dtpSightingDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        dtpSightingDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dtpSightingDatePropertyChange(evt);
            }
        });

        jLabel8.setText("Weather:");
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText("Rating:");
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText("Individuals:");
        jLabel10.setName("jLabel10"); // NOI18N

        spnDurationSeconds.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.0d, 1.0d));
        spnDurationSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnDurationSeconds.setEditor(new javax.swing.JSpinner.NumberEditor(spnDurationSeconds, "0"));
        spnDurationSeconds.setEnabled(!disableEditing);
        spnDurationSeconds.setName("spnDurationSeconds"); // NOI18N

        jLabel21.setText("seconds");
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setText("Time Accuracy:");
        jLabel22.setName("jLabel22"); // NOI18N

        cmbTimeAccuracy.setModel(new DefaultComboBoxModel(TimeAccuracy.values()));
        cmbTimeAccuracy.setSelectedItem(sighting.getTimeAccuracy());
        cmbTimeAccuracy.setFocusable(false);
        cmbTimeAccuracy.setName("cmbTimeAccuracy"); // NOI18N

        jLabel23.setText("Age:");
        jLabel23.setName("jLabel23"); // NOI18N

        cmbAge.setMaximumRowCount(10);
        cmbAge.setModel(new DefaultComboBoxModel(Age.values()));
        cmbAge.setSelectedItem(sighting.getAge());
        cmbAge.setFocusable(false);
        cmbAge.setName("cmbAge"); // NOI18N

        jLabel12.setText("Certainty:");
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(":");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel13.setText("Details:");
        jLabel13.setName("jLabel13"); // NOI18N

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setEnabled(!disableEditing);
        cmbTimeFormat.setFocusable(false);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        cmbTimeFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTimeFormatActionPerformed(evt);
            }
        });

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

        cmbWeather.setMaximumRowCount(9);
        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setEnabled(!disableEditing);
        cmbWeather.setFocusable(false);
        cmbWeather.setName("cmbWeather"); // NOI18N

        cmbTimeOfDay.setMaximumRowCount(15);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setEnabled(!disableEditing);
        cmbTimeOfDay.setFocusable(false);
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setEnabled(!disableEditing);
        cmbViewRating.setFocusable(false);
        cmbViewRating.setName("cmbViewRating"); // NOI18N

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setEnabled(!disableEditing);
        cmbCertainty.setFocusable(false);
        cmbCertainty.setName("cmbCertainty"); // NOI18N

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(":");
        jLabel24.setName("jLabel24"); // NOI18N

        spnSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnSeconds.setToolTipText("Seconds");
        spnSeconds.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(144, 200, 99)));
        spnSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnSeconds.setEditor(new javax.swing.JSpinner.NumberEditor(spnSeconds, "0"));
        spnSeconds.setEnabled(!disableEditing);
        spnSeconds.setFocusable(false);
        spnSeconds.setName("spnSeconds"); // NOI18N
        spnSeconds.setPreferredSize(new java.awt.Dimension(35, 20));
        spnSeconds.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSecondsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlSightingFieldsLayout = new javax.swing.GroupLayout(pnlSightingFields);
        pnlSightingFields.setLayout(pnlSightingFieldsLayout);
        pnlSightingFieldsLayout.setHorizontalGroup(
            pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSightingFieldsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel18)
                    .addComponent(jLabel14)
                    .addComponent(jLabel3)
                    .addComponent(jLabel11)
                    .addComponent(jLabel8)
                    .addComponent(jLabel19)
                    .addComponent(jLabel7)
                    .addComponent(jLabel22))
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbWeather, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSightingFieldsLayout.createSequentialGroup()
                        .addComponent(spnTemperature)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTemperatureUnits, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmbMoonlight, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSightingFieldsLayout.createSequentialGroup()
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                                .addComponent(spnMoonPhase, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cmbTimeOfDay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addComponent(lblTimeOfDayInfo))
                    .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dtpSightingDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbTimeAccuracy, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1))
                    .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                                .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addGap(8, 8, 8)
                                .addComponent(spnDurationSeconds, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel21))
                            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addComponent(btnGPS))
                            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                                .addComponent(spnHours, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(spnSeconds, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(cmbTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel23)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jLabel9)
                    .addComponent(jLabel17)
                    .addComponent(jLabel13))
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTag)
                    .addComponent(cmbViewRating, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbCertainty, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbEvidence, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbLifeStatus, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbAge, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbSex, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(spnNumberOfElements))
                .addGap(5, 5, 5))
        );
        pnlSightingFieldsLayout.setVerticalGroup(
            pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dtpSightingDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spnNumberOfElements, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnSeconds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbTimeAccuracy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                                .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbTimeOfDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTimeOfDayInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnMoonPhase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbMoonlight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTemperatureUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbWeather, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnDurationSeconds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(22, Short.MAX_VALUE))
                    .addGroup(pnlSightingFieldsLayout.createSequentialGroup()
                        .addComponent(cmbAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbLifeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(pnlSightingFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbEvidence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(cmbCertainty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(cmbViewRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(txtTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2)
                        .addGap(5, 5, 5))))
        );

        pnlButtons.setBackground(new java.awt.Color(208, 204, 181));
        pnlButtons.setName("pnlButtons"); // NOI18N

        lblSightingID.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblSightingID.setForeground(new java.awt.Color(102, 102, 102));
        lblSightingID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingID.setToolTipText("Right-click to copy the Observation ID.");
        lblSightingID.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblSightingID.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        lblSightingID.setName("lblSightingID"); // NOI18N
        lblSightingID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSightingIDMouseReleased(evt);
            }
        });

        btnCalculateDuration.setBackground(new java.awt.Color(208, 204, 181));
        btnCalculateDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Duration_Small.png"))); // NOI18N
        btnCalculateDuration.setText("<html>Get Duration from Images</html>");
        btnCalculateDuration.setToolTipText("Attempt to calculate the Duration of the Observation based on times specified on the uploaded images.");
        btnCalculateDuration.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCalculateDuration.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnCalculateDuration.setFocusPainted(false);
        btnCalculateDuration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalculateDuration.setIconTextGap(8);
        btnCalculateDuration.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnCalculateDuration.setName("btnCalculateDuration"); // NOI18N
        btnCalculateDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateDurationActionPerformed(evt);
            }
        });

        btnCalculateSunAndMoon.setBackground(new java.awt.Color(208, 204, 181));
        btnCalculateSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon.gif"))); // NOI18N
        btnCalculateSunAndMoon.setText("<html>Calculate Time of Day and Moon Phase</html>");
        btnCalculateSunAndMoon.setToolTipText("Automatically calculate the Sun and Moon phase based on the date and GPS co-ordinates.");
        btnCalculateSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCalculateSunAndMoon.setEnabled(!disableEditing && !bulkEditMode);
        btnCalculateSunAndMoon.setFocusPainted(false);
        btnCalculateSunAndMoon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalculateSunAndMoon.setIconTextGap(8);
        btnCalculateSunAndMoon.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnCalculateSunAndMoon.setName("btnCalculateSunAndMoon"); // NOI18N
        btnCalculateSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateSunAndMoonActionPerformed(evt);
            }
        });

        btnGetGPSFromImage.setBackground(new java.awt.Color(208, 204, 181));
        btnGetGPSFromImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS_Small.png"))); // NOI18N
        btnGetGPSFromImage.setText("<html>Use GPS from Images</html>");
        btnGetGPSFromImage.setToolTipText("Attempt to load the GPS from the image's EXIF data.");
        btnGetGPSFromImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetGPSFromImage.setDoubleBuffered(true);
        btnGetGPSFromImage.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnGetGPSFromImage.setFocusPainted(false);
        btnGetGPSFromImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGetGPSFromImage.setIconTextGap(8);
        btnGetGPSFromImage.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnGetGPSFromImage.setName("btnGetGPSFromImage"); // NOI18N
        btnGetGPSFromImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetGPSFromImageActionPerformed(evt);
            }
        });

        btnGetDateFromImage.setBackground(new java.awt.Color(208, 204, 181));
        btnGetDateFromImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF_small.png"))); // NOI18N
        btnGetDateFromImage.setText("<html>Use Date from Images</html>");
        btnGetDateFromImage.setToolTipText("Attempt to load the date and time from the image's EXIF data.");
        btnGetDateFromImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetDateFromImage.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnGetDateFromImage.setFocusPainted(false);
        btnGetDateFromImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGetDateFromImage.setIconTextGap(8);
        btnGetDateFromImage.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnGetDateFromImage.setName("btnGetDateFromImage"); // NOI18N
        btnGetDateFromImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetDateFromImageActionPerformed(evt);
            }
        });

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

        btnINaturalist.setBackground(new java.awt.Color(208, 204, 181));
        btnINaturalist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist_small.png"))); // NOI18N
        btnINaturalist.setText("<html>Link with iNaturalist</html>");
        btnINaturalist.setToolTipText("Link this Observatin with an iNaturalist account.");
        btnINaturalist.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnINaturalist.setEnabled(!disableEditing && !bulkUploadMode && !bulkEditMode);
        btnINaturalist.setFocusPainted(false);
        btnINaturalist.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnINaturalist.setIconTextGap(8);
        btnINaturalist.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnINaturalist.setName("btnINaturalist"); // NOI18N
        btnINaturalist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnINaturalistActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCalculateDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalculateSunAndMoon, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetGPSFromImage, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetDateFromImage, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSightingID, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnINaturalist, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlButtonsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnUpdateSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnGetDateFromImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnGetGPSFromImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnCalculateSunAndMoon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnCalculateDuration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnINaturalist, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSightingID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout sightingIncludesLayout = new javax.swing.GroupLayout(sightingIncludes);
        sightingIncludes.setLayout(sightingIncludesLayout);
        sightingIncludesLayout.setHorizontalGroup(
            sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sightingIncludesLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sightingIncludesLayout.createSequentialGroup()
                        .addComponent(pnlElementControls, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlLocationControls, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlVisitControls, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLocationImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(sightingIncludesLayout.createSequentialGroup()
                        .addComponent(pnlSightingFields, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlImageControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        sightingIncludesLayout.setVerticalGroup(
            sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sightingIncludesLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sightingIncludesLayout.createSequentialGroup()
                        .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(sightingIncludesLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(pnlImageControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sightingIncludesLayout.createSequentialGroup()
                                .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(lblLocationImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlVisitControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(sightingIncludesLayout.createSequentialGroup()
                        .addComponent(pnlSightingFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(sightingIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlLocationControls, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                            .addComponent(pnlElementControls, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sightingIncludes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sightingIncludes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private boolean saveSighting() {
        // Set Location, Element and Visit
        if (locationWL != null) {
            sighting.setLocationID(locationWL.getID());
            sighting.setCachedLocationName(locationWL.getName());
        }
        if (element != null) {
            sighting.setElementID(element.getID());
            sighting.setCachedElementName(element.getPrimaryName());
        }
        if (visit != null) {
            sighting.setVisitID(visit.getID());
            sighting.setCachedVisitName(visit.getName());
        }
        // Set variables
        setupSightingDateFromUIFields();
        sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
        sighting.setDetails(txtDetails.getText());
        sighting.setSightingEvidence((SightingEvidence)cmbEvidence.getSelectedItem());
        if (spnNumberOfElements.getValue().toString().length() > 0) {
            try {
                sighting.setNumberOfElements(Integer.parseInt(spnNumberOfElements.getValue().toString()));
            }
            catch (NumberFormatException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
        sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
        sighting.setWeather((Weather)cmbWeather.getSelectedItem());
        if (spnTemperature.getValue().toString().length() > 0) {
            try {
                sighting.setTemperature(Double.parseDouble(spnTemperature.getValue().toString()));
            }
            catch (NumberFormatException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
        sighting.setTimeAccuracy((TimeAccuracy)cmbTimeAccuracy.getSelectedItem());
        sighting.setAge((Age)cmbAge.getSelectedItem());
        // NOTE: The GPS info is already set on the Sighting object by the GPS popup component
        // SAVE (Only save to DB if not in bulk upload mode)
        if (!bulkUploadMode && !bulkEditMode) {
            // Sanity check that the IDs are correct
            if (sighting.getElementID() <= 0 || sighting.getLocationID() <= 0 || sighting.getVisitID() <= 0) {
                WLOptionPane.showMessageDialog(this,
                        "The Observation could not be saved due to problematic data.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // Perform the database actions
            boolean result;
            if (sighting.getID() == 0) {
                result = app.getDBI().createSighting(sighting, false);
            }
            else {
                result = app.getDBI().updateSighting(sighting, false);
            }
            if (result == false) {
                WLOptionPane.showMessageDialog(this,
                        "The Observation could not be saved.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void closeThisDialog() {
        this.dispose();
    }

    private void uploadImage(final List<File> inFiles) {
        if (inFiles != null && inFiles.size() > 0) {
            // If the date hasn't been set yet, then try to load it from the first image.
            // Also try to find a file with GPS info (if no info has been set yet)
            // and update the Sun and Moon phase (if possible and not set yet).
            setupSightingDateFromUIFields();
            if (locationWL != null && element != null && visit != null) {
                // Gebruik die compareTo om die sortering te doen
                class ComparableFile implements Comparable<ComparableFile> {
                    public File originalFile;
                    @Override
                    public int compareTo(ComparableFile inComparableFile) {
                        if (inComparableFile != null && inComparableFile.originalFile != null && originalFile != null) {
                            Date date1 = UtilsImageProcessing.getDateFromFile(originalFile.toPath());
                            Date date2 = UtilsImageProcessing.getDateFromFile(inComparableFile.originalFile.toPath());
                            if (date1 != null && date2 != null) {
                                return date1.compareTo(date2);
                            }
                        }
                        return 0;
                    }
                }
                List<ComparableFile> compareFileList = new ArrayList<ComparableFile>(inFiles.size());
                for (File tempFile : inFiles) {
                    ComparableFile comparableFile = new ComparableFile();
                    comparableFile.originalFile = tempFile;
                    compareFileList.add(comparableFile);
                }
                Collections.sort(compareFileList);
                // Setup date
                if (sighting.getDate() == null) {
                    loadDateFromFile(compareFileList.get(0).originalFile.toPath());
                }
                // Setup GPS
                if (UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLongitudeString(sighting))
                        && UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLatitudeString(sighting))) {
                    for (ComparableFile comparableFile : compareFileList) {
                        if (WildLogFileExtentions.Images.isJPG(comparableFile.originalFile.toPath().toAbsolutePath())) {
                            DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(comparableFile.originalFile.toPath().toAbsolutePath());
                            if (temp != null) {
                                if (!UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLongitudeString(temp))
                                        && !UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLatitudeString(temp))) {
                                    UtilsGPS.copyGpsBetweenDOs(sighting, temp);
                                    txtLatitude.setText(UtilsGPS.getLatitudeString(sighting));
                                    txtLongitude.setText(UtilsGPS.getLongitudeString(sighting));
                                    break;
                                }
                            }
                        }
                    }
                }
                // Setup Sun and Moon
                if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())
                        || sighting.getMoonPhase() < 0 || sighting.getMoonlight() == null
                        || Moonlight.NONE.equals(sighting.getMoonlight()) || Moonlight.UNKNOWN.equals(sighting.getMoonlight())) {
                    btnCalculateSunAndMoonActionPerformed(null);
                }
            }
            // Try to save the sighting (to make sure all required fields are there and to get the SightingID)
            btnUpdateSightingActionPerformed(null);
            // Now upload the files
            if (locationWL != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                uploadFiles(inFiles);
            }
        }
    }

    private void loadDateFromFile(Path inPath) {
        if (inPath != null) {
            Date fileDate = UtilsImageProcessing.getDateFromFile(inPath);
            // Set the date
            if (fileDate != null) {
                sighting.setDate(fileDate);
                sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                cmbTimeFormat.setSelectedItem(TimeFormat.H24);
                setUIFieldsFromSightingDate(fileDate);
            }
        }
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if (panelToRefresh != null) {
            panelToRefresh.doTheRefresh(this);
        }
    }//GEN-LAST:event_formWindowClosed

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[PanelSighting-Save]");
        // Check the required fields' borders
        Color green = new Color(0,204,51);
        if (!bulkEditMode) {
            if (element == null) {
                sclElement.setBorder(new LineBorder(Color.RED, 2));
            }
            else {
                sclElement.setBorder(new LineBorder(green, 1));
            }
            if (locationWL == null && !bulkUploadMode) {
                sclLocation.setBorder(new LineBorder(Color.RED, 2));
            }
            else {
                sclLocation.setBorder(new LineBorder(green, 1));
            }
            if (visit == null && !bulkUploadMode) {
                sclVisit.setBorder(new LineBorder(Color.RED, 2));
            }
            else {
                sclVisit.setBorder(new LineBorder(green, 1));
            }
            if (dtpSightingDate.getDate() == null) {
                dtpSightingDate.setBorder(new LineBorder(Color.ORANGE, 2));
            }
            else {
                dtpSightingDate.setBorder(new LineBorder(green, 2));
            }
        }
        // Perform the save action
        if (element != null && (visit != null || bulkUploadMode) && (locationWL != null || bulkUploadMode) && dtpSightingDate.getDate() != null || bulkEditMode) {
            if (saveSighting()) {
                if (app.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                // Close the dialog - (Evt is null if the Image Upload, etc. methods call this method, then we don't want to close.)
                if (evt != null) {
                    closeThisDialog();
                }
            }
        }
        else {
            WLOptionPane.showMessageDialog(this,
                "Please fill in all of the required fields.",
                "Can't Save Observation", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateSightingActionPerformed

    private void btnGetDateFromImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetDateFromImageActionPerformed
        boolean hasRelevantFiles = false;
        List<WildLogFile> files = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
        if (files != null && files.size() > 0) {
            hasRelevantFiles = true;
            loadDateFromFile(files.get(imageIndex).getAbsolutePath());
        }
        if (!hasRelevantFiles) {
            if (evt != null) {
                WLOptionPane.showMessageDialog(this,
                    "Please upload some files and try again.",
                    "No Files Uploaded.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnGetDateFromImageActionPerformed

    private void btnGetGPSFromImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetGPSFromImageActionPerformed
        boolean hasRelevantFiles = false;
        List<WildLogFile> files = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
        if (files != null && files.size() > 0) {
            if (WildLogFileType.IMAGE.equals(files.get(imageIndex).getFileType())) {
                hasRelevantFiles = true;
                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(files.get(imageIndex).getAbsolutePath());
                if (!UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLongitudeString(temp))
                    && !UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLatitudeString(temp))) {
                    UtilsGPS.copyGpsBetweenDOs(sighting, temp);
                    txtLatitude.setText(UtilsGPS.getLatitudeString(sighting));
                    txtLongitude.setText(UtilsGPS.getLongitudeString(sighting));
                }
                else {
                    WLOptionPane.showMessageDialog(this,
                        "No GPS information could be found for the selected file.",
                        "No GPS Data Found.", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (!hasRelevantFiles) {
            WLOptionPane.showMessageDialog(this,
                "Please upload some image files and try again.",
                "No Files Uploaded.", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnGetGPSFromImageActionPerformed

    private void btnCalculateSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateSunAndMoonActionPerformed
        if (sighting.getDate() != null && cmbTimeAccuracy.getSelectedItem() != null
            && ((TimeAccuracy) cmbTimeAccuracy.getSelectedItem()).isUsableTime()) {
            // Try to save the sighting (to make sure all required fields are there and to get the Sighting Time)
            if (!bulkUploadMode && evt != null) { // Moenie save as dit 'n Bulk Import is nie, of as dit via 'n ander button/event geroep word nie
                btnUpdateSightingActionPerformed(null);
            }
            // Load the date into the sighting object from the fields
            setupSightingDateFromUIFields();
            // Process the fields that require a GPS
            if (sighting.getLatitude() != null && sighting.getLongitude() != null
                && !Latitudes.NONE.equals(sighting.getLatitude()) && !Longitudes.NONE.equals(sighting.getLongitude())
                && txtLatitude.getText() != null && !txtLatitude.getText().isEmpty() && !UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
                && txtLongitude.getText() != null && !txtLongitude.getText().isEmpty() && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
                double latitude = UtilsGPS.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                double longitude = UtilsGPS.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                // Sun
                cmbTimeOfDay.setSelectedItem(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                // Moon
                cmbMoonlight.setSelectedItem(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
            }
            else {
                // Handle it if there isn't a GPS to use
                // Only show the messages if the user clicked the button (automatic calls will use a null event)
                if (evt != null) {
                    // If the location has a GPS point, as whether it should be used for the calculation.
                    if (locationWL.getLatitude() != null && locationWL.getLongitude() != null
                        && !Latitudes.NONE.equals(locationWL.getLatitude()) && !Longitudes.NONE.equals(locationWL.getLongitude())
                        && !UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLatitudeString(locationWL))
                        && !UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLongitudeString(locationWL))) {
                        int result = WLOptionPane.showConfirmDialog(this,
                                "This Observation does not have a GPS point. Would you like to use the GPS point associated with the Place for the calculation?",
                                "Use The GPS Point From The Place?", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            double latitude = UtilsGPS.getDecimalDegree(locationWL.getLatitude(), locationWL.getLatDegrees(), locationWL.getLatMinutes(), locationWL.getLatSeconds());
                            double longitude = UtilsGPS.getDecimalDegree(locationWL.getLongitude(), locationWL.getLonDegrees(), locationWL.getLonMinutes(), locationWL.getLonSeconds());
                            // Sun
                            cmbTimeOfDay.setSelectedItem(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                            // Moon
                            cmbMoonlight.setSelectedItem(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
                        }
                    }
                    else {
                        WLOptionPane.showMessageDialog(this,
                            "Please make sure to first provide an accurate values for the GPS point.",
                            "Could not calculate the Sun and Moon information.", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            spnMoonPhase.setValue(AstroCalculator.getMoonPhase(sighting.getDate()));
        }
        else {
            // Only show the error if the user clicked the button
            if (evt != null) {
                WLOptionPane.showMessageDialog(this,
                    "Please make sure to first provide an accurate Date.",
                    "Could not calculate the Sun and Moon information.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCalculateSunAndMoonActionPerformed

    private void btnCalculateDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateDurationActionPerformed
        boolean hasRelevantFiles = false;
        // Get all Image files for this sighting
        List<WildLogFile> allFiles = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
        // Only use Images and Movies for the duration
        List<WildLogFile> files = new ArrayList<WildLogFile>(allFiles.size());
        for (WildLogFile wildLogFile : allFiles) {
            if (WildLogFileExtentions.Images.isKnownExtention(wildLogFile.getAbsolutePath())
                || WildLogFileExtentions.Movies.isKnownExtention(wildLogFile.getAbsolutePath())) {
                files.add(wildLogFile);
            }
        }
        if (!files.isEmpty()) {
            hasRelevantFiles = true;
            Collections.sort(files);
            Date startDate = UtilsImageProcessing.getDateFromFile(files.get(0).getAbsolutePath());
            Date endDate = UtilsImageProcessing.getDateFromFile(files.get(files.size()-1).getAbsolutePath());
            double difference = (endDate.getTime() - startDate.getTime())/1000;
            int minutes = (int)difference/60;
            double seconds = difference - minutes*60.0;
            spnDurationMinutes.setValue(minutes);
            spnDurationSeconds.setValue(seconds);
        }
        if (!hasRelevantFiles) {
            // Only show the error if the user clicked the button
            if (evt != null) {
                WLOptionPane.showMessageDialog(this,
                    "Please upload some image or movie files and try again.",
                    "No Files Uploaded.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCalculateDurationActionPerformed

    private void lblSightingIDMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSightingIDMouseReleased
        if ((evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt))) {
            JPopupMenu clipboardPopup = new JPopupMenu();
            // Build the copy popup
            JMenuItem copyItem = new JMenuItem("Copy Observation ID", new ImageIcon(WildLogApp.class.getResource("resources/icons/copy.png")));
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doClipboardCopy(Long.toString(sighting.getID()));
                }
            });
            clipboardPopup.add(copyItem);
            // Wrap up and show up the popup
            clipboardPopup.pack();
            clipboardPopup.show(evt.getComponent(), evt.getPoint().x, evt.getPoint().y);
            clipboardPopup.setVisible(true);
        }
    }//GEN-LAST:event_lblSightingIDMouseReleased

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        uploadImage(UtilsFileProcessing.showFileUploadDialog(app, this));
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(sighting.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateSightingActionPerformed(null);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(sighting.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
        btnUpdateSightingActionPerformed(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (sighting != null) {
            UtilsFileProcessing.openFile(sighting.getWildLogFileID(), imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblTimeOfDayInfoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTimeOfDayInfoMousePressed
        WLOptionPane.showMessageDialog(this,
                ActiveTimeSpesific.getCompleteDescription(),
                "Time of Day Definitions", JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_lblTimeOfDayInfoMousePressed

    private void btnAddNewVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewVisitActionPerformed
        if (locationWL != null) {
            JDialog popup = new JDialog(this, "Add New Period", true);
            ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/Visit.gif"));
            popup.setIconImage(icon.getImage());
            PanelVisit panel = new PanelVisit(app, locationWL, new Visit(), true, this);
            popup.add(panel);
            popup.setResizable(false);
            popup.pack();
            UtilsDialog.setDialogToCenter(this, popup);
            UtilsDialog.addModalBackgroundPanel(this, popup);
            UtilsDialog.addEscapeKeyListener(popup);
            popup.setVisible(true);
            popup.dispose();
        }
        else {
            WLOptionPane.showMessageDialog(this,
                    "Please select a Place first, and then try again to add a new Period.",
                    "No Place Selected", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnAddNewVisitActionPerformed

    private void btnAddNewLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewLocationActionPerformed
        JDialog popup = new JDialog(this, "Add New Place", true);
        ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/Location.gif"));
        popup.setIconImage(icon.getImage());
        PanelLocation panel = new PanelLocation(app, new Location(), true, this);
        popup.add(panel);
        popup.setResizable(false);
        popup.pack();
        UtilsDialog.setDialogToCenter(this, popup);
        UtilsDialog.addModalBackgroundPanel(this, popup);
        UtilsDialog.addEscapeKeyListener(popup);
        popup.setVisible(true);
        popup.dispose();
    }//GEN-LAST:event_btnAddNewLocationActionPerformed

    private void btnAddNewElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewElementActionPerformed
        JDialog popup = new JDialog(this, "Add New Creature", true);
        ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif"));
        popup.setIconImage(icon.getImage());
        PanelElement panel = new PanelElement(app, new Element(), true, this);
        popup.add(panel);
        popup.setResizable(false);
        popup.pack();
        UtilsDialog.setDialogToCenter(this, popup);
        UtilsDialog.addModalBackgroundPanel(this, popup);
        UtilsDialog.addModalBackgroundPanel(popup, null);
        UtilsDialog.addEscapeKeyListener(popup);
        popup.setVisible(true);
        popup.dispose();
    }//GEN-LAST:event_btnAddNewElementActionPerformed

    private void spnTemperatureStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTemperatureStateChanged
        if ((double)spnTemperature.getValue() >= 45
            && (cmbTemperatureUnits.getSelectedItem() == null || UnitsTemperature.NONE.equals(cmbTemperatureUnits.getSelectedItem()))) {
            cmbTemperatureUnits.setSelectedItem(UnitsTemperature.FAHRENHEIT);
        }
    }//GEN-LAST:event_spnTemperatureStateChanged

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        if (locationWL != null) {
            sighting.setLocationID(locationWL.getID());
        }
        else {
            sighting.setLocationID(0);
        }
        GPSDialog dialog = new GPSDialog(app, this, sighting);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(UtilsGPS.getLatitudeString(sighting));
            txtLongitude.setText(UtilsGPS.getLongitudeString(sighting));
            btnUpdateSightingActionPerformed(null);
        }
        // Setup Sun and Moon
        if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())
            || sighting.getMoonPhase() < 0 || sighting.getMoonlight() == null
            || Moonlight.NONE.equals(sighting.getMoonlight()) || Moonlight.UNKNOWN.equals(sighting.getMoonlight())) {
            btnCalculateSunAndMoonActionPerformed(null);
        }
        btnUpdateSighting.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void spnMinutesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMinutesStateChanged
        if (TimeAccuracy.UNKNOWN.equals(cmbTimeAccuracy.getSelectedItem())
            && cmbTimeAccuracy.isEnabled()) {
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.GOOD);
        }
        btnCalculateSunAndMoonActionPerformed(null);
    }//GEN-LAST:event_spnMinutesStateChanged

    private void spnHoursStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnHoursStateChanged
        if (TimeAccuracy.UNKNOWN.equals(cmbTimeAccuracy.getSelectedItem())
            && cmbTimeAccuracy.isEnabled()) {
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.GOOD);
        }
        btnCalculateSunAndMoonActionPerformed(null);
    }//GEN-LAST:event_spnHoursStateChanged

    private void cmbTimeFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTimeFormatActionPerformed
        UtilsTime.modeChanged(spnHours, spnMinutes, spnSeconds, cmbTimeFormat, prevTimeFormat);
        prevTimeFormat = (TimeFormat) cmbTimeFormat.getSelectedItem();
        if (cmbTimeFormat.getSelectedItem() == null || TimeFormat.UNKNOWN.equals(cmbTimeFormat.getSelectedItem())
            || TimeFormat.NONE.equals(cmbTimeFormat.getSelectedItem())) {
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.UNKNOWN);
            cmbTimeAccuracy.setEnabled(false);
        }
        else {
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.GOOD);
            cmbTimeAccuracy.setEnabled(true);
        }
    }//GEN-LAST:event_cmbTimeFormatActionPerformed

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (locationWL != null) {
            UtilsFileProcessing.openFile(locationWL.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (!bulkUploadMode) {
            if (tblVisit.getSelectedRowCount() == 1) {
                visit = app.getDBI().findVisit((Long) tblVisit.getModel().getValueAt(
                        tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 4), null, false, Visit.class);
            }
            else {
                visit = null;
            }
        }
    }//GEN-LAST:event_tblVisitMouseReleased

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblLocationMouseReleased(null);
        }
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
    }//GEN-LAST:event_tblLocationKeyPressed

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (!bulkUploadMode) {
            if (tblLocation.getSelectedRowCount() == 1) {
                locationWL = app.getDBI().findLocation((Long) tblLocation.getModel().getValueAt(
                        tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 2), null, Location.class);
                UtilsTableGenerator.setupVisitTableSmallWithType(app, tblVisit, locationWL.getID());
                btnAddNewVisit.setEnabled(true);
                visit = null;
                UtilsImageProcessing.setupFoto(locationWL.getWildLogFileID(), 0, lblLocationImage, WildLogThumbnailSizes.SMALL, app);
            }
            else {
                locationWL = null;
                UtilsTableGenerator.setupVisitTableSmallWithType(app, tblVisit, 0);
                btnAddNewVisit.setEnabled(false);
                visit = null;
                lblLocationImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
            }
        }
    }//GEN-LAST:event_tblLocationMouseReleased

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        if (sighting != null) {
            txtSearch.setText("");
            ElementType type = (ElementType) cmbElementType.getSelectedItem();
            if (!ElementType.NONE.equals(type)) {
                UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, type);
            }
            else {
                UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
            }
            // Clear Images
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
    }//GEN-LAST:event_cmbElementTypeActionPerformed

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (element != null) {
            UtilsFileProcessing.openFile(element.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblElementMouseReleased(null);
        }
    }//GEN-LAST:event_tblElementKeyReleased

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            element = app.getDBI().findElement((Long) tblElement.getModel().getValueAt(
                    tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3), null, Element.class);
            UtilsImageProcessing.setupFoto(element.getWildLogFileID(), 0, lblElementImage, WildLogThumbnailSizes.SMALL, app);
        }
        else {
            element = null;
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void dtpSightingDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dtpSightingDatePropertyChange
        if (evt.getPropertyName().equals("date")) {
            setupSightingDateFromUIFields();
            btnCalculateSunAndMoonActionPerformed(null);
        }
    }//GEN-LAST:event_dtpSightingDatePropertyChange

    private void spnSecondsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSecondsStateChanged
        if (TimeAccuracy.UNKNOWN.equals(cmbTimeAccuracy.getSelectedItem())
                && cmbTimeAccuracy.isEnabled()) {
            cmbTimeAccuracy.setSelectedItem(TimeAccuracy.GOOD);
        }
        btnCalculateSunAndMoonActionPerformed(null);
    }//GEN-LAST:event_spnSecondsStateChanged

    private void btnINaturalistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnINaturalistActionPerformed
        if (!bulkUploadMode) {
            btnUpdateSightingActionPerformed(null);
        }
        INatSightingDialog dialog = new INatSightingDialog(this, sighting);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnINaturalistActionPerformed

    private void setupNumberOfImages() {
        int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        }
        else {
            lblNumberOfImages.setText("0 of 0");
        }
        lblNumberOfImages.setToolTipText(lblNumberOfImages.getText());
    }

    private void setupSightingDateFromUIFields() {
        // Get the date from the datepicker
        Date date = dtpSightingDate.getDate();
        if (date != null) {
            sighting.setDate(UtilsTime.getDateFromUI(spnHours, spnMinutes, spnSeconds, cmbTimeFormat, date));
        }
        else {
            sighting.setDate(null);
        }
    }

    public Sighting getSighting() {
        return sighting;
    }

    @Override
    public void doTheRefresh(Object inIndicator) {
        if (inIndicator != null) {
            if (inIndicator instanceof PanelVisit) {
                tblLocationMouseReleased(null);
            }
            else
            if (inIndicator instanceof PanelLocation) {
                UtilsTableGenerator.setupLocationTableSmall(app, tblLocation, null);
                txtSearchLocation.setText("");
            }
            else
            if (inIndicator instanceof PanelElement) {
                UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
                txtSearch.setText("");
            }
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNewElement;
    private javax.swing.JButton btnAddNewLocation;
    private javax.swing.JButton btnAddNewVisit;
    private javax.swing.JButton btnCalculateDuration;
    private javax.swing.JButton btnCalculateSunAndMoon;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnGetDateFromImage;
    private javax.swing.JButton btnGetGPSFromImage;
    private javax.swing.JButton btnINaturalist;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbAge;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbEvidence;
    private javax.swing.JComboBox cmbLifeStatus;
    private javax.swing.JComboBox cmbMoonlight;
    private javax.swing.JComboBox cmbSex;
    private javax.swing.JComboBox cmbTemperatureUnits;
    private javax.swing.JComboBox cmbTimeAccuracy;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblElement;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblSightingID;
    private javax.swing.JLabel lblTimeOfDayInfo;
    private javax.swing.JLabel lblVisit;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlElementControls;
    private javax.swing.JPanel pnlImageControls;
    private javax.swing.JPanel pnlLocationControls;
    private javax.swing.JPanel pnlSightingFields;
    private javax.swing.JPanel pnlVisitControls;
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
    private javax.swing.JSpinner spnSeconds;
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
