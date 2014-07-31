package wildlog.ui.dialogs;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.gpx.UtilsGPX;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.filters.GpxFilter;
import wildlog.ui.helpers.filters.ImageFilter;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsImageProcessing;


public class GPSDialog extends JDialog {
    private static String lastFilePath = "";
    private static Latitudes prevLat;
    private static int prevLatDeg;
    private static int prevLatMin;
    private static double prevLatSec;
    private static Longitudes prevLon;
    private static int prevLonDeg;
    private static int prevLonMin;
    private static double prevLonSec;
    private static GPSAccuracy prevAccuracy;
    private WildLogApp app;
    private boolean selectionMade = false;
    private DataObjectWithGPS dataObjectWithGPS;
    private double uiLatitude = 0.0;
    private double uiLongitude = 0.0;


    public GPSDialog(WildLogApp inApp, JFrame inParent, DataObjectWithGPS inDataObjectWithGPS) {
        super(inParent);
        // Do the setup (this is where the shared setup happens)
        doSetup(inApp, inDataObjectWithGPS);
        // Setup the default behavior (this is for JFrames)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    public GPSDialog(WildLogApp inApp, JDialog inParent, DataObjectWithGPS inDataObjectWithGPS) {
        super(inParent);
        // Do the setup (this is where the shared setup happens)
        doSetup(inApp, inDataObjectWithGPS);
        // Setup the default behavior (this is for JDialogs)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    private void doSetup(WildLogApp inApp, DataObjectWithGPS inDataObjectWithGPS) {
        app = inApp;
        dataObjectWithGPS = inDataObjectWithGPS;
        if (dataObjectWithGPS == null) {
            dispose();
        }
        // Need to set a few settings on the content pane before continuing (for example size, background color, etc.)
        getContentPane().setPreferredSize(getPreferredSize());
        // Initialize the auto generated code
        initComponents();
        // Hide stuff
        spnLatDecimal.setVisible(false);
        spnLonDecimal.setVisible(false);
        // Fix the selection of the spinners
        SpinnerFixer.configureSpinners(spnLatDecimal);
        SpinnerFixer.configureSpinners(spnLatDeg);
        SpinnerFixer.configureSpinners(spnLatMin);
        SpinnerFixer.configureSpinners(spnLatSec);
        SpinnerFixer.configureSpinners(spnLonDecimal);
        SpinnerFixer.configureSpinners(spnLonDeg);
        SpinnerFixer.configureSpinners(spnLonMin);
        SpinnerFixer.configureSpinners(spnLonSec);
        // Setup the ui Lat and Lon
        // Get existing value from passed in dataObjectWithGPS
        loadUIValues(dataObjectWithGPS);
        // Load the defaults if no values were provided
        loadDefaultLatAndLon();
        // Load the accuracy
        if (dataObjectWithGPS.getGPSAccuracy() != null) {
            cmbAccuracy.setSelectedItem(dataObjectWithGPS.getGPSAccuracy());
        }
        else {
            cmbAccuracy.setSelectedItem(GPSAccuracy.GOOD);
        }
        // Setup the drag and drop on the butons
        FileDrop.SetupFileDrop(btnUseGPX, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() == 1) {
                    doGpxInput(inFiles.get(0).toPath());
                }
            }
        });
        FileDrop.SetupFileDrop(btnUseImage, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() == 1) {
                    loadUIValues(UtilsImageProcessing.getExifGpsFromJpeg(inFiles.get(0).toPath()));
                }
            }
        });
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Attach clipboard
        UtilsUI.attachClipboardPopup((JTextComponent)spnLatDecimal.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLatDeg.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLatMin.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLatSec.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLonDecimal.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLonDeg.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLonMin.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLonSec.getEditor().getComponent(0));
        // Set the toggle for the +/- buttons
        ((JTextComponent)spnLatDecimal.getEditor().getComponent(0)).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if (inEvent.getKeyChar() == '-') {
                    tglSouth.setSelected(true);
                    tglNorth.setSelected(false);
                }
                else
                if (inEvent.getKeyChar() == '+') {
                    tglSouth.setSelected(false);
                    tglNorth.setSelected(true);
                }
            }
        });
        ((JTextComponent)spnLatDeg.getEditor().getComponent(0)).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if (inEvent.getKeyChar() == '-') {
                    tglSouth.setSelected(true);
                    tglNorth.setSelected(false);
                }
                else
                if (inEvent.getKeyChar() == '+') {
                    tglSouth.setSelected(false);
                    tglNorth.setSelected(true);
                }
            }
        });
        ((JTextComponent)spnLonDeg.getEditor().getComponent(0)).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if (inEvent.getKeyChar() == '-') {
                    tglEast.setSelected(false);
                    tglWest.setSelected(true);
                }
                else
                if (inEvent.getKeyChar() == '+') {
                    tglEast.setSelected(true);
                    tglWest.setSelected(false);
                }
            }
        });
        ((JTextComponent)spnLonDecimal.getEditor().getComponent(0)).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if (inEvent.getKeyChar() == '-') {
                    tglEast.setSelected(false);
                    tglWest.setSelected(true);
                }
                else
                if (inEvent.getKeyChar() == '+') {
                    tglEast.setSelected(true);
                    tglWest.setSelected(false);
                }
            }
        });
        // Set focus
        btnSave.requestFocusInWindow();
    }

    private void loadDefaultLatAndLon() {
        Latitudes tempLat = dataObjectWithGPS.getLatitude();
        if (tempLat == null || Latitudes.NONE.equals(tempLat)) {
            tempLat = app.getWildLogOptions().getDefaultInputLatitude();
        }
        if (Latitudes.NORTH.equals(tempLat)) {
            tglNorth.setSelected(true);
        }
        else
        if (Latitudes.SOUTH.equals(tempLat)) {
            tglSouth.setSelected(true);
        }
        Longitudes tempLon = dataObjectWithGPS.getLongitude();
        if (tempLon == null || Longitudes.NONE.equals(tempLon)) {
            tempLon = app.getWildLogOptions().getDefaultInputLongitude();
        }
        if (Longitudes.EAST.equals(tempLon)) {
            tglEast.setSelected(true);
        }
        else
        if (Longitudes.WEST.equals(tempLon)) {
            tglWest.setSelected(true);
        }
    }

    private void loadUIValues(DataObjectWithGPS inDataObjectWithGPS) {
        if (inDataObjectWithGPS != null) {
            uiLatitude = UtilsGps.getDecimalDegree(
                    Latitudes.NONE,
                    inDataObjectWithGPS.getLatDegrees(),
                    inDataObjectWithGPS.getLatMinutes(),
                    inDataObjectWithGPS.getLatSeconds());
            uiLongitude = UtilsGps.getDecimalDegree(
                    Longitudes.NONE,
                    inDataObjectWithGPS.getLonDegrees(),
                    inDataObjectWithGPS.getLonMinutes(),
                    inDataObjectWithGPS.getLonSeconds());
            // Select the correct sign
            if (Latitudes.NORTH.equals(inDataObjectWithGPS.getLatitude())) {
                tglNorth.setSelected(true);
                tglSouth.setSelected(false);
            }
            else
            if (Latitudes.SOUTH.equals(inDataObjectWithGPS.getLatitude())) {
                tglNorth.setSelected(false);
                tglSouth.setSelected(true);
            }
            else {
                tglNorth.setSelected(false);
                tglSouth.setSelected(false);
            }
            if (Longitudes.EAST.equals(inDataObjectWithGPS.getLongitude())) {
                tglEast.setSelected(true);
                tglWest.setSelected(false);
            }
            else
            if (Longitudes.WEST.equals(inDataObjectWithGPS.getLongitude())) {
                tglEast.setSelected(false);
                tglWest.setSelected(true);
            }
            else {
                tglEast.setSelected(false);
                tglWest.setSelected(false);
            }
            // Setup the accuracy
            cmbAccuracy.setSelectedItem(inDataObjectWithGPS.getGPSAccuracy());
            // Populate the initial values into the spinners
            tglDecimalDegrees.setSelected(true);
            tglDecimalDegrees.requestFocus();
            tglDegMinSec.setSelected(false);
            setupDD();
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

        btnSave = new javax.swing.JButton();
        btnUsePrevious = new javax.swing.JButton();
        btnUseImage = new javax.swing.JButton();
        btnUseMap = new javax.swing.JButton();
        btnUseGPX = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        tglNorth = new javax.swing.JToggleButton();
        tglSouth = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        tglWest = new javax.swing.JToggleButton();
        tglEast = new javax.swing.JToggleButton();
        tglDecimalDegrees = new javax.swing.JToggleButton();
        tglDegMinSec = new javax.swing.JToggleButton();
        spnLatDeg = new javax.swing.JSpinner();
        spnLatMin = new javax.swing.JSpinner();
        spnLatSec = new javax.swing.JSpinner();
        spnLatDecimal = new javax.swing.JSpinner();
        spnLonDeg = new javax.swing.JSpinner();
        spnLonMin = new javax.swing.JSpinner();
        spnLonSec = new javax.swing.JSpinner();
        spnLonDecimal = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        cmbAccuracy = new javax.swing.JComboBox();
        btnRemoveGPS = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configure GPS Point");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/GPS.png")).getImage());
        setMinimumSize(new java.awt.Dimension(410, 210));
        setModal(true);
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(415, 295));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSave.setToolTipText("Confirm the GPS value.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 110, 60));

        btnUsePrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnUsePrevious.setText("Use Previous GPS");
        btnUsePrevious.setToolTipText("Use the previously saved GPS point.");
        btnUsePrevious.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUsePrevious.setFocusPainted(false);
        btnUsePrevious.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsePrevious.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUsePrevious.setName("btnUsePrevious"); // NOI18N
        btnUsePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsePreviousActionPerformed(evt);
            }
        });
        getContentPane().add(btnUsePrevious, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 130, 30));

        btnUseImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF.png"))); // NOI18N
        btnUseImage.setText("<html><u>Load from Image</u></html>");
        btnUseImage.setToolTipText("Browse to an image with GPS data, or drag-and-drop the image onto this button.");
        btnUseImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseImage.setFocusPainted(false);
        btnUseImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseImage.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseImage.setName("btnUseImage"); // NOI18N
        btnUseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseImageActionPerformed(evt);
            }
        });
        getContentPane().add(btnUseImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 130, 30));

        btnUseMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnUseMap.setText("Use Map");
        btnUseMap.setEnabled(false);
        btnUseMap.setFocusPainted(false);
        btnUseMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseMap.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseMap.setName("btnUseMap"); // NOI18N
        btnUseMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseMapActionPerformed(evt);
            }
        });
        getContentPane().add(btnUseMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 10, 130, 30));

        btnUseGPX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPX.png"))); // NOI18N
        btnUseGPX.setText("<html><u>Load From GPX</u></html>");
        btnUseGPX.setToolTipText("Browse to a GPX file, or drag-and-drop the file onto the button.");
        btnUseGPX.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseGPX.setFocusPainted(false);
        btnUseGPX.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseGPX.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseGPX.setName("btnUseGPX"); // NOI18N
        btnUseGPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseGPXActionPerformed(evt);
            }
        });
        getContentPane().add(btnUseGPX, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 45, 130, 30));

        jSeparator1.setName("jSeparator1"); // NOI18N
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 390, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Latitude:");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        tglNorth.setText(Latitudes.NORTH.getText());
        tglNorth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglNorth.setFocusPainted(false);
        tglNorth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglNorth.setName("tglNorth"); // NOI18N
        tglNorth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglNorthActionPerformed(evt);
            }
        });
        getContentPane().add(tglNorth, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 60, 30));

        tglSouth.setText(Latitudes.SOUTH.getText());
        tglSouth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglSouth.setFocusPainted(false);
        tglSouth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglSouth.setName("tglSouth"); // NOI18N
        tglSouth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglSouthActionPerformed(evt);
            }
        });
        getContentPane().add(tglSouth, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 60, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Logitude:");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        tglWest.setText(Longitudes.WEST.getText());
        tglWest.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglWest.setFocusPainted(false);
        tglWest.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglWest.setName("tglWest"); // NOI18N
        tglWest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglWestActionPerformed(evt);
            }
        });
        getContentPane().add(tglWest, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 60, 30));

        tglEast.setText(Longitudes.EAST.getText());
        tglEast.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglEast.setFocusPainted(false);
        tglEast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglEast.setName("tglEast"); // NOI18N
        tglEast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglEastActionPerformed(evt);
            }
        });
        getContentPane().add(tglEast, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 60, 30));

        tglDecimalDegrees.setText("DD");
        tglDecimalDegrees.setToolTipText("Decimal-Degrees");
        tglDecimalDegrees.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglDecimalDegrees.setFocusPainted(false);
        tglDecimalDegrees.setName("tglDecimalDegrees"); // NOI18N
        tglDecimalDegrees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDecimalDegreesActionPerformed(evt);
            }
        });
        getContentPane().add(tglDecimalDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, 60, 45));

        tglDegMinSec.setText("DMS");
        tglDegMinSec.setToolTipText("Degrees, Minutes and Decimal-Seconds");
        tglDegMinSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglDegMinSec.setFocusPainted(false);
        tglDegMinSec.setName("tglDegMinSec"); // NOI18N
        tglDegMinSec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDegMinSecActionPerformed(evt);
            }
        });
        getContentPane().add(tglDegMinSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 155, 60, 45));

        spnLatDeg.setModel(new javax.swing.SpinnerNumberModel(0, 0, 90, 1));
        spnLatDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDeg, "#"));
        spnLatDeg.setName("spnLatDeg"); // NOI18N
        getContentPane().add(spnLatDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 50, 30));

        spnLatMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLatMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatMin, "#"));
        spnLatMin.setName("spnLatMin"); // NOI18N
        getContentPane().add(spnLatMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 50, 30));

        spnLatSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 1.0E-4d));
        spnLatSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatSec, "#.####"));
        spnLatSec.setName("spnLatSec"); // NOI18N
        getContentPane().add(spnLatSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 70, 30));

        spnLatDecimal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        spnLatDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 90.0d, 1.0E-5d));
        spnLatDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDecimal, "#.############"));
        spnLatDecimal.setName("spnLatDecimal"); // NOI18N
        getContentPane().add(spnLatDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 190, 30));

        spnLonDeg.setModel(new javax.swing.SpinnerNumberModel(0, 0, 180, 1));
        spnLonDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDeg, "#"));
        spnLonDeg.setName("spnLonDeg"); // NOI18N
        getContentPane().add(spnLonDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 50, 30));

        spnLonMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLonMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonMin, "#"));
        spnLonMin.setName("spnLonMin"); // NOI18N
        getContentPane().add(spnLonMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 50, 30));

        spnLonSec.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        spnLonSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 1.0E-4d));
        spnLonSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonSec, "#.####"));
        spnLonSec.setName("spnLonSec"); // NOI18N
        getContentPane().add(spnLonSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 70, 30));

        spnLonDecimal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        spnLonDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 180.0d, 1.0E-5d));
        spnLonDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDecimal, "#.############"));
        spnLonDecimal.setName("spnLonDecimal"); // NOI18N
        getContentPane().add(spnLonDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 190, 30));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Accuracy:");
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        cmbAccuracy.setModel(new DefaultComboBoxModel(GPSAccuracy.values()));
        cmbAccuracy.setSelectedItem(GPSAccuracy.AVERAGE);
        cmbAccuracy.setName("cmbAccuracy"); // NOI18N
        getContentPane().add(cmbAccuracy, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 260, -1));

        btnRemoveGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnRemoveGPS.setText("Remove GPS");
        btnRemoveGPS.setToolTipText("Remove the GPS value.");
        btnRemoveGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveGPS.setName("btnRemoveGPS"); // NOI18N
        btnRemoveGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveGPSActionPerformed(evt);
            }
        });
        getContentPane().add(btnRemoveGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 230, 120, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Get lat and long enums
        if (tglNorth.isSelected()) {
            dataObjectWithGPS.setLatitude(Latitudes.NORTH);
        }
        else {
            dataObjectWithGPS.setLatitude(Latitudes.SOUTH);
        }
        if (tglEast.isSelected()) {
            dataObjectWithGPS.setLongitude(Longitudes.EAST);
        }
        else {
            dataObjectWithGPS.setLongitude(Longitudes.WEST);
        }
        // Get the number values
        if (tglDecimalDegrees.isSelected()) {
            // Use decimal degrees
            double latDecimalDegree = (double)spnLatDecimal.getValue();
            double lonDecimalDegree = (double)spnLonDecimal.getValue();
            dataObjectWithGPS.setLatDegrees(UtilsGps.getDegrees(Latitudes.NONE, latDecimalDegree));
            dataObjectWithGPS.setLonDegrees(UtilsGps.getDegrees(Longitudes.NONE, lonDecimalDegree));
            dataObjectWithGPS.setLatMinutes(UtilsGps.getMinutes(latDecimalDegree));
            dataObjectWithGPS.setLonMinutes(UtilsGps.getMinutes(lonDecimalDegree));
            dataObjectWithGPS.setLatSeconds(UtilsGps.getSeconds(latDecimalDegree));
            dataObjectWithGPS.setLonSeconds(UtilsGps.getSeconds(lonDecimalDegree));
        }
        else {
            // Use degrees minutes seconds
            dataObjectWithGPS.setLatDegrees((int)spnLatDeg.getValue());
            dataObjectWithGPS.setLonDegrees((int)spnLonDeg.getValue());
            dataObjectWithGPS.setLatMinutes((int)spnLatMin.getValue());
            dataObjectWithGPS.setLonMinutes((int)spnLonMin.getValue());
            dataObjectWithGPS.setLatSeconds((Double)spnLatSec.getValue());
            dataObjectWithGPS.setLonSeconds((Double)spnLonSec.getValue());
        }
        dataObjectWithGPS.setGPSAccuracy((GPSAccuracy)cmbAccuracy.getSelectedItem());
        selectionMade = true;
        // Now update the "previous" GPS value
        setPrevLat(dataObjectWithGPS.getLatitude());
        setPrevLatDeg(dataObjectWithGPS.getLatDegrees());
        setPrevLatMin(dataObjectWithGPS.getLatMinutes());
        setPrevLatSec(dataObjectWithGPS.getLatSeconds());
        setPrevLon(dataObjectWithGPS.getLongitude());
        setPrevLonDeg(dataObjectWithGPS.getLonDegrees());
        setPrevLonMin(dataObjectWithGPS.getLonMinutes());
        setPrevLonSec(dataObjectWithGPS.getLonSeconds());
        setPrevAccuracy(dataObjectWithGPS.getGPSAccuracy());
        // We are done, dispose this dialog
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tglNorthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglNorthActionPerformed
        if (tglNorth.isSelected()) {
            tglSouth.setSelected(false);
        }
        else {
            tglNorth.setSelected(true);
        }
    }//GEN-LAST:event_tglNorthActionPerformed

    private void tglSouthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglSouthActionPerformed
        if (tglSouth.isSelected()) {
            tglNorth.setSelected(false);
        }
        else {
            tglSouth.setSelected(true);
        }
    }//GEN-LAST:event_tglSouthActionPerformed

    private void tglWestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglWestActionPerformed
        if (tglWest.isSelected()) {
            tglEast.setSelected(false);
        }
        else {
            tglWest.setSelected(true);
        }
    }//GEN-LAST:event_tglWestActionPerformed

    private void tglEastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglEastActionPerformed
        if (tglEast.isSelected()) {
            tglWest.setSelected(false);
        }
        else {
            tglEast.setSelected(true);
        }
    }//GEN-LAST:event_tglEastActionPerformed

    private void tglDecimalDegreesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDecimalDegreesActionPerformed
        if (tglDecimalDegrees.isSelected()) {
            tglDegMinSec.setSelected(false);
            loadValuesFromDMS();
            setupDD();
        }
        else {
            tglDecimalDegrees.setSelected(true);
        }
    }//GEN-LAST:event_tglDecimalDegreesActionPerformed

    private void tglDegMinSecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDegMinSecActionPerformed
        if (tglDegMinSec.isSelected()) {
            tglDecimalDegrees.setSelected(false);
            loadValuesFromDD();
            setupDMS();
        }
        else {
            tglDegMinSec.setSelected(true);
        }
    }//GEN-LAST:event_tglDegMinSecActionPerformed

    private void btnUseGPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseGPXActionPerformed
        JFileChooser fileChooser;
        if (lastFilePath != null && lastFilePath.length() > 0) {
            fileChooser = new JFileChooser(lastFilePath);
        }
        else {
            fileChooser = new JFileChooser();
        }
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new GpxFilter());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        getGlassPane().setVisible(true);
        int result = fileChooser.showOpenDialog(this);
        getGlassPane().setVisible(false);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            Path file = fileChooser.getSelectedFile().toPath();
            lastFilePath = file.toAbsolutePath().toString();
            doGpxInput(file);
        }
    }//GEN-LAST:event_btnUseGPXActionPerformed

    private void btnUsePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsePreviousActionPerformed
        DataObjectWithGPS temp = new DataObjectWithGPS() {};
        temp.setLatitude(getPrevLat());
        temp.setLatDegrees(getPrevLatDeg());
        temp.setLatMinutes(getPrevLatMin());
        temp.setLatSeconds(getPrevLatSec());
        temp.setLongitude(getPrevLon());
        temp.setLonDegrees(getPrevLonDeg());
        temp.setLonMinutes(getPrevLonMin());
        temp.setLonSeconds(getPrevLonSec());
        temp.setGPSAccuracy(getPrevAccuracy());
        cmbAccuracy.setSelectedItem(temp.getGPSAccuracy());
        loadUIValues(temp);
        btnSaveActionPerformed(evt);
    }//GEN-LAST:event_btnUsePreviousActionPerformed

    private void btnUseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseImageActionPerformed
        JFileChooser fileChooser;
        if (lastFilePath != null && lastFilePath.length() > 0) {
            fileChooser = new JFileChooser(lastFilePath);
        }
        else {
            fileChooser = new JFileChooser();
        }
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        getGlassPane().setVisible(true);
        int result = fileChooser.showOpenDialog(this);
        getGlassPane().setVisible(false);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            Path file = fileChooser.getSelectedFile().toPath();
            lastFilePath = file.toAbsolutePath().toString();
            loadUIValues(UtilsImageProcessing.getExifGpsFromJpeg(file));
        }
    }//GEN-LAST:event_btnUseImageActionPerformed

    private void btnUseMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseMapActionPerformed
        // TODO: GPS point from map - Ek sal die deel later moet uitfigure en doen...
    }//GEN-LAST:event_btnUseMapActionPerformed

    private void btnRemoveGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveGPSActionPerformed
        UtilsGps.copyGpsBetweenDOs(dataObjectWithGPS, new DataObjectWithGPS() {});
        selectionMade = true;
        dispose();
    }//GEN-LAST:event_btnRemoveGPSActionPerformed

    private void doGpxInput(Path inFile) {
        getGlassPane().setVisible(true);
        String gpxValue = JOptionPane.showInputDialog(this,
                "<html><b>Please enter the GPX Waypoint's name. </b>"
                + "<br/> <i>(Use the full name, for example '001', including the 0's.)</i></html>",
                "Load Coordinates From GPX", JOptionPane.INFORMATION_MESSAGE);
        getGlassPane().setVisible(false);
        if (gpxValue != null) {
            DataObjectWithGPS temp = new DataObjectWithGPS() {};
            UtilsGPX.populateGPSFromGpxFile(inFile, gpxValue, temp, this);
            loadUIValues(temp);
        }
    }

    private void setupDD() {
        // Update UI
        spnLatDeg.setVisible(false);
        spnLatMin.setVisible(false);
        spnLatSec.setVisible(false);
        spnLatDecimal.setVisible(true);
        spnLonDeg.setVisible(false);
        spnLonMin.setVisible(false);
        spnLonSec.setVisible(false);
        spnLonDecimal.setVisible(true);
        // Setup values
        spnLatDecimal.setValue(uiLatitude);
        spnLonDecimal.setValue(uiLongitude);
    }

    private void setupDMS() {
        // Update UI
        spnLatDeg.setVisible(true);
        spnLatMin.setVisible(true);
        spnLatSec.setVisible(true);
        spnLatDecimal.setVisible(false);
        spnLonDeg.setVisible(true);
        spnLonMin.setVisible(true);
        spnLonSec.setVisible(true);
        spnLonDecimal.setVisible(false);
        // Setup values
        spnLatDeg.setValue(UtilsGps.getDegrees(Latitudes.NONE, uiLatitude));
        spnLatMin.setValue(UtilsGps.getMinutes(uiLatitude));
        spnLatSec.setValue(UtilsGps.getSeconds(uiLatitude));
        spnLonDeg.setValue(UtilsGps.getDegrees(Longitudes.NONE, uiLongitude));
        spnLonMin.setValue(UtilsGps.getMinutes(uiLongitude));
        spnLonSec.setValue(UtilsGps.getSeconds(uiLongitude));
    }

    private void loadValuesFromDD() {
        uiLatitude = (double)spnLatDecimal.getValue();
        uiLongitude = (double)spnLonDecimal.getValue();
    }

    private void loadValuesFromDMS() {
        uiLatitude = UtilsGps.getDecimalDegree(
            Latitudes.NONE,
            (int)spnLatDeg.getValue(),
            (int)spnLatMin.getValue(),
            (double)spnLatSec.getValue());
        uiLongitude = UtilsGps.getDecimalDegree(
            Longitudes.NONE,
            (int)spnLonDeg.getValue(),
            (int)spnLonMin.getValue(),
            (double)spnLonSec.getValue());
    }

    public boolean isSelectionMade() {
        return selectionMade;
    }

    // Getters and Setters
    public static Latitudes getPrevLat() {
        if (prevLat == null) {
            prevLat = Latitudes.NONE;
        }
        return prevLat;
    }

    public static void setPrevLat(Latitudes inPrevLat) {
        prevLat = inPrevLat;
    }

    public static int getPrevLatDeg() {
        return prevLatDeg;
    }

    public static void setPrevLatDeg(int inPrevLatDeg) {
        prevLatDeg = inPrevLatDeg;
    }

    public static int getPrevLatMin() {
        return prevLatMin;
    }

    public static void setPrevLatMin(int inPrevLatMin) {
        prevLatMin = inPrevLatMin;
    }

    public static double getPrevLatSec() {
        return prevLatSec;
    }

    public static void setPrevLatSec(double inPrevLatSec) {
        prevLatSec = inPrevLatSec;
    }

    public static Longitudes getPrevLon() {
        if (prevLon == null) {
            prevLon = Longitudes.NONE;
        }
        return prevLon;
    }

    public static void setPrevLon(Longitudes inPrevLon) {
        prevLon = inPrevLon;
    }

    public static int getPrevLonDeg() {
        return prevLonDeg;
    }

    public static void setPrevLonDeg(int inPrevLonDeg) {
        prevLonDeg = inPrevLonDeg;
    }

    public static int getPrevLonMin() {
        return prevLonMin;
    }

    public static void setPrevLonMin(int inPrevLonMin) {
        prevLonMin = inPrevLonMin;
    }

    public static double getPrevLonSec() {
        return prevLonSec;
    }

    public static void setPrevLonSec(double inPrevLonSec) {
        prevLonSec = inPrevLonSec;
    }

    public static GPSAccuracy getPrevAccuracy() {
        return prevAccuracy;
    }

    public static void setPrevAccuracy(GPSAccuracy inPrevAccuracy) {
        prevAccuracy = inPrevAccuracy;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveGPS;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUseGPX;
    private javax.swing.JButton btnUseImage;
    private javax.swing.JButton btnUseMap;
    private javax.swing.JButton btnUsePrevious;
    private javax.swing.JComboBox cmbAccuracy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner spnLatDecimal;
    private javax.swing.JSpinner spnLatDeg;
    private javax.swing.JSpinner spnLatMin;
    private javax.swing.JSpinner spnLatSec;
    private javax.swing.JSpinner spnLonDecimal;
    private javax.swing.JSpinner spnLonDeg;
    private javax.swing.JSpinner spnLonMin;
    private javax.swing.JSpinner spnLonSec;
    private javax.swing.JToggleButton tglDecimalDegrees;
    private javax.swing.JToggleButton tglDegMinSec;
    private javax.swing.JToggleButton tglEast;
    private javax.swing.JToggleButton tglNorth;
    private javax.swing.JToggleButton tglSouth;
    private javax.swing.JToggleButton tglWest;
    // End of variables declaration//GEN-END:variables
}
