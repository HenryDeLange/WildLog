package wildlog.ui.dialogs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import netscape.javascript.JSObject;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.maps.geotools.GeoToolsMapJavaFX;
import wildlog.maps.gpx.UtilsGPX;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.filters.GpxFilter;
import wildlog.ui.helpers.filters.ImageFilter;
import wildlog.ui.maps.implementations.PointMap;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


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
    private GeoToolsMapJavaFX map;
    private boolean dmsHasBeenLoaded = false;


    public GPSDialog(WildLogApp inApp, JFrame inParent, DataObjectWithGPS inDataObjectWithGPS) {
        super(inParent);
        System.out.println("[GPSDialog]");
        // Do the setup (this is where the shared setup happens)
        doSetup(inApp, inDataObjectWithGPS);
        // Setup the default behavior (this is for JFrames)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    public GPSDialog(WildLogApp inApp, JDialog inParent, DataObjectWithGPS inDataObjectWithGPS) {
        super(inParent);
        System.out.println("[GPSDialog]");
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
            setVisible(false);
            dispose();
        }
        // Initialize the auto generated code
        initComponents();
        // Hide stuff
        spnLatDecimal.setVisible(false);
        spnLonDecimal.setVisible(false);
        pnlMap.setVisible(false);
        pack();
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
        // Load the accuracy
        if (dataObjectWithGPS.getGPSAccuracy() != null && !GPSAccuracy.NONE.equals(dataObjectWithGPS.getGPSAccuracy())) {
            cmbAccuracy.setSelectedItem(dataObjectWithGPS.getGPSAccuracy());
        }
        else {
            cmbAccuracy.setSelectedItem(GPSAccuracy.AVERAGE);
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
        ((JTextComponent) spnLatDecimal.getEditor().getComponent(0)).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent inDocumentEvent) {
                toggleSign();
            }

            @Override
            public void removeUpdate(DocumentEvent inDocumentEvent) {
                toggleSign();
            }

            @Override
            public void insertUpdate(DocumentEvent inDocumentEvent) {
                toggleSign();
            }

            private void toggleSign() {
                if ((double) spnLatDecimal.getModel().getValue() < 0) {
                    tglSouth.setSelected(true);
                    tglNorth.setSelected(false);
                }
                else {
                    tglSouth.setSelected(false);
                    tglNorth.setSelected(true);
                }
            }
        });
        ((JTextComponent) spnLatDeg.getEditor().getComponent(0)).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleSign();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleSign();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleSign();
            }

            private void toggleSign() {
                if ((int) spnLatDeg.getModel().getValue() < 0) {
                    tglSouth.setSelected(true);
                    tglNorth.setSelected(false);
                }
                else {
                    tglSouth.setSelected(false);
                    tglNorth.setSelected(true);
                }
            }
        });
        ((JTextComponent) spnLonDecimal.getEditor().getComponent(0)).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleSign();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleSign();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleSign();
            }

            private void toggleSign() {
                if ((double) spnLonDecimal.getModel().getValue() < 0) {
                    tglEast.setSelected(false);
                    tglWest.setSelected(true);
                }
                else {
                    tglEast.setSelected(true);
                    tglWest.setSelected(false);
                }
            }
        });
        ((JTextComponent) spnLonDeg.getEditor().getComponent(0)).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleSign();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleSign();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleSign();
            }

            private void toggleSign() {
                if ((int) spnLonDeg.getModel().getValue() < 0) {
                    tglEast.setSelected(false);
                    tglWest.setSelected(true);
                }
                else {
                    tglEast.setSelected(true);
                    tglWest.setSelected(false);
                }
            }
        });
        // Dispose the map when the window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if (map != null) {
                    map.dispose();
                }
            }
        });
    }

    private void loadUIValues(DataObjectWithGPS inDataObjectWithGPS) {
        if (inDataObjectWithGPS != null) {
            uiLatitude = UtilsGPS.getDecimalDegree(
                    inDataObjectWithGPS.getLatitude(),
                    inDataObjectWithGPS.getLatDegrees(),
                    inDataObjectWithGPS.getLatMinutes(),
                    inDataObjectWithGPS.getLatSeconds());
            uiLongitude = UtilsGPS.getDecimalDegree(
                    inDataObjectWithGPS.getLongitude(),
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

        buttonGroupLatitude = new javax.swing.ButtonGroup();
        buttonGroupLongitude = new javax.swing.ButtonGroup();
        buttonGroupFormat = new javax.swing.ButtonGroup();
        btnSave = new javax.swing.JButton();
        btnUsePrevious = new javax.swing.JButton();
        btnUseRelated = new javax.swing.JButton();
        btnUseImage = new javax.swing.JButton();
        btnUseGPX = new javax.swing.JButton();
        tglDecimalDegrees = new javax.swing.JToggleButton();
        tglDegMinSec = new javax.swing.JToggleButton();
        btnRemoveGPS = new javax.swing.JButton();
        btnUseOnlineMap = new javax.swing.JButton();
        btnUseOfflineMap = new javax.swing.JButton();
        pnlMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cmbAccuracy = new javax.swing.JComboBox();
        spnLatDecimal = new javax.swing.JSpinner();
        spnLonSec = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spnLatMin = new javax.swing.JSpinner();
        spnLatSec = new javax.swing.JSpinner();
        tglWest = new javax.swing.JToggleButton();
        tglSouth = new javax.swing.JToggleButton();
        spnLatDeg = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        spnLonDeg = new javax.swing.JSpinner();
        tglEast = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        spnLonMin = new javax.swing.JSpinner();
        tglNorth = new javax.swing.JToggleButton();
        spnLonDecimal = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configure GPS Point");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/GPS.png")).getImage());
        setMinimumSize(new java.awt.Dimension(480, 325));
        setModal(true);
        setName("Form"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSave.setToolTipText("Confirm the GPS value.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.setFocusable(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUsePrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnUsePrevious.setText("Use Previous");
        btnUsePrevious.setToolTipText("Use the previously saved GPS point.");
        btnUsePrevious.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUsePrevious.setFocusPainted(false);
        btnUsePrevious.setFocusable(false);
        btnUsePrevious.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsePrevious.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUsePrevious.setName("btnUsePrevious"); // NOI18N
        btnUsePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsePreviousActionPerformed(evt);
            }
        });

        btnUseRelated.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/RelatedGPS.png"))); // NOI18N
        btnUseRelated.setText("Find Related");
        btnUseRelated.setToolTipText("Try to find a GPS point using the related Images or linked Place.");
        btnUseRelated.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseRelated.setFocusPainted(false);
        btnUseRelated.setFocusable(false);
        btnUseRelated.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseRelated.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseRelated.setName("btnUseRelated"); // NOI18N
        btnUseRelated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseRelatedActionPerformed(evt);
            }
        });

        btnUseImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF.png"))); // NOI18N
        btnUseImage.setText("<html><u>From Image</u></html>");
        btnUseImage.setToolTipText("Browse to an image with GPS data, or drag-and-drop the image onto this button.");
        btnUseImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseImage.setFocusPainted(false);
        btnUseImage.setFocusable(false);
        btnUseImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseImage.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseImage.setName("btnUseImage"); // NOI18N
        btnUseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseImageActionPerformed(evt);
            }
        });

        btnUseGPX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPX.png"))); // NOI18N
        btnUseGPX.setText("<html><u>From GPX</u></html>");
        btnUseGPX.setToolTipText("Browse to a GPX file, or drag-and-drop the file onto the button.");
        btnUseGPX.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseGPX.setFocusPainted(false);
        btnUseGPX.setFocusable(false);
        btnUseGPX.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUseGPX.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseGPX.setName("btnUseGPX"); // NOI18N
        btnUseGPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseGPXActionPerformed(evt);
            }
        });

        buttonGroupFormat.add(tglDecimalDegrees);
        tglDecimalDegrees.setText("Decimal-Degrees");
        tglDecimalDegrees.setToolTipText("Use the Decimal-Degrees format.");
        tglDecimalDegrees.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglDecimalDegrees.setFocusPainted(false);
        tglDecimalDegrees.setFocusable(false);
        tglDecimalDegrees.setMinimumSize(new java.awt.Dimension(160, 30));
        tglDecimalDegrees.setName("tglDecimalDegrees"); // NOI18N
        tglDecimalDegrees.setPreferredSize(new java.awt.Dimension(160, 30));
        tglDecimalDegrees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDecimalDegreesActionPerformed(evt);
            }
        });

        buttonGroupFormat.add(tglDegMinSec);
        tglDegMinSec.setText("Degrees-Minutes-Seconds");
        tglDegMinSec.setToolTipText("Use the Degrees, Minutes and Decimal-Seconds format.");
        tglDegMinSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglDegMinSec.setFocusPainted(false);
        tglDegMinSec.setFocusable(false);
        tglDegMinSec.setMinimumSize(new java.awt.Dimension(160, 30));
        tglDegMinSec.setName("tglDegMinSec"); // NOI18N
        tglDegMinSec.setPreferredSize(new java.awt.Dimension(160, 30));
        tglDegMinSec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDegMinSecActionPerformed(evt);
            }
        });

        btnRemoveGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnRemoveGPS.setText("Remove GPS");
        btnRemoveGPS.setToolTipText("Remove the GPS value.");
        btnRemoveGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveGPS.setFocusable(false);
        btnRemoveGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveGPS.setIconTextGap(6);
        btnRemoveGPS.setMargin(new java.awt.Insets(2, 8, 2, 4));
        btnRemoveGPS.setName("btnRemoveGPS"); // NOI18N
        btnRemoveGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveGPSActionPerformed(evt);
            }
        });

        btnUseOnlineMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnUseOnlineMap.setText("Show on Map (Online)");
        btnUseOnlineMap.setToolTipText("Show the GPS point on an online map. Dragging the marker on the map will update the GPS value.");
        btnUseOnlineMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseOnlineMap.setFocusPainted(false);
        btnUseOnlineMap.setFocusable(false);
        btnUseOnlineMap.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseOnlineMap.setName("btnUseOnlineMap"); // NOI18N
        btnUseOnlineMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseOnlineMapActionPerformed(evt);
            }
        });

        btnUseOfflineMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnUseOfflineMap.setText("Show on Map (Offline)");
        btnUseOfflineMap.setToolTipText("Show the GPS point on an offline map.");
        btnUseOfflineMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseOfflineMap.setFocusPainted(false);
        btnUseOfflineMap.setFocusable(false);
        btnUseOfflineMap.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnUseOfflineMap.setName("btnUseOfflineMap"); // NOI18N
        btnUseOfflineMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseOfflineMapActionPerformed(evt);
            }
        });

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setName("pnlMap"); // NOI18N
        pnlMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setName("jPanel1"); // NOI18N

        cmbAccuracy.setModel(new DefaultComboBoxModel(GPSAccuracy.values()));
        cmbAccuracy.setSelectedItem(GPSAccuracy.AVERAGE);
        cmbAccuracy.setFocusable(false);
        cmbAccuracy.setName("cmbAccuracy"); // NOI18N

        spnLatDecimal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLatDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, -90.0d, 90.0d, 0.01d));
        spnLatDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDecimal, "#.############"));
        spnLatDecimal.setName("spnLatDecimal"); // NOI18N

        spnLonSec.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLonSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 0.01d));
        spnLonSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonSec, "#.####"));
        spnLonSec.setName("spnLonSec"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Accuracy:");
        jLabel3.setName("jLabel3"); // NOI18N

        spnLatMin.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLatMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLatMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatMin, "#"));
        spnLatMin.setName("spnLatMin"); // NOI18N

        spnLatSec.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLatSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 0.01d));
        spnLatSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatSec, "#.####"));
        spnLatSec.setName("spnLatSec"); // NOI18N

        buttonGroupLongitude.add(tglWest);
        tglWest.setText(Longitudes.WEST.getText());
        tglWest.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglWest.setFocusPainted(false);
        tglWest.setFocusable(false);
        tglWest.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglWest.setName("tglWest"); // NOI18N
        tglWest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglWestActionPerformed(evt);
            }
        });

        buttonGroupLatitude.add(tglSouth);
        tglSouth.setText(Latitudes.SOUTH.getText());
        tglSouth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglSouth.setFocusPainted(false);
        tglSouth.setFocusable(false);
        tglSouth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglSouth.setName("tglSouth"); // NOI18N
        tglSouth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglSouthActionPerformed(evt);
            }
        });

        spnLatDeg.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLatDeg.setModel(new javax.swing.SpinnerNumberModel(0, -90, 90, 1));
        spnLatDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDeg, "#"));
        spnLatDeg.setName("spnLatDeg"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Latitude:");
        jLabel1.setName("jLabel1"); // NOI18N

        spnLonDeg.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLonDeg.setModel(new javax.swing.SpinnerNumberModel(0, -180, 180, 1));
        spnLonDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDeg, "#"));
        spnLonDeg.setName("spnLonDeg"); // NOI18N

        buttonGroupLongitude.add(tglEast);
        tglEast.setText(Longitudes.EAST.getText());
        tglEast.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglEast.setFocusPainted(false);
        tglEast.setFocusable(false);
        tglEast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglEast.setName("tglEast"); // NOI18N
        tglEast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglEastActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        spnLonMin.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLonMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLonMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonMin, "#"));
        spnLonMin.setName("spnLonMin"); // NOI18N

        buttonGroupLatitude.add(tglNorth);
        tglNorth.setText(Latitudes.NORTH.getText());
        tglNorth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tglNorth.setFocusPainted(false);
        tglNorth.setFocusable(false);
        tglNorth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglNorth.setName("tglNorth"); // NOI18N
        tglNorth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglNorthActionPerformed(evt);
            }
        });

        spnLonDecimal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        spnLonDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, -180.0d, 180.0d, 0.01d));
        spnLonDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDecimal, "#.############"));
        spnLonDecimal.setName("spnLonDecimal"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Logitude:");
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addComponent(cmbAccuracy, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tglNorth, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(tglEast, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tglSouth, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tglWest, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(spnLatDeg, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(spnLatMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(spnLatSec))
                            .addComponent(spnLatDecimal)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(spnLonSec))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(spnLonMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnLonDecimal)
                            .addComponent(spnLonDeg, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(56, 56, 56)))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tglNorth, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(tglEast, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tglSouth, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(tglWest, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnLatDeg, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLatMin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLatSec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLatDecimal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnLonSec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLonMin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLonDecimal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnLonDeg, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbAccuracy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnUseOnlineMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(btnUseOfflineMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tglDecimalDegrees, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tglDegMinSec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUsePrevious, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUseImage)
                            .addComponent(btnUseGPX)
                            .addComponent(btnUseRelated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRemoveGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tglDecimalDegrees, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tglDegMinSec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(btnUsePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnUseImage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnUseGPX, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnUseRelated, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnRemoveGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUseOnlineMap, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUseOfflineMap, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(pnlMap, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

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
            dataObjectWithGPS.setLatDegrees(UtilsGPS.getDegrees(Latitudes.NONE, latDecimalDegree));
            dataObjectWithGPS.setLonDegrees(UtilsGPS.getDegrees(Longitudes.NONE, lonDecimalDegree));
            dataObjectWithGPS.setLatMinutes(UtilsGPS.getMinutes(latDecimalDegree));
            dataObjectWithGPS.setLonMinutes(UtilsGPS.getMinutes(lonDecimalDegree));
            dataObjectWithGPS.setLatSeconds(UtilsGPS.getSeconds(latDecimalDegree));
            dataObjectWithGPS.setLonSeconds(UtilsGPS.getSeconds(lonDecimalDegree));
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
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tglDecimalDegreesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDecimalDegreesActionPerformed
        // Need this if to prevent loading the DMS info which hasn't been populated yet...
        if (dmsHasBeenLoaded) {
            loadValuesFromDMS();
            setupDD();
        }
    }//GEN-LAST:event_tglDecimalDegreesActionPerformed

    private void tglDegMinSecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDegMinSecActionPerformed
        loadValuesFromDD();
        setupDMS();
        dmsHasBeenLoaded = true;
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
        boolean showNothingFoundDialog = true;
        if (dataObjectWithGPS != null) {
            // Is a Sighting
            if (dataObjectWithGPS instanceof Sighting) {
                Sighting sighting = (Sighting) dataObjectWithGPS;
                if (sighting.getSightingCounter() != 0) {
                    getGlassPane().setVisible(true);
                    int option = JOptionPane.showOptionDialog(WildLogApp.getApplication().getMainFrame(), 
                                    "Please select where to search for GPS points.", 
                                    "Where to search?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                                    null, new String[]{"An external File", "Files already linked to this Observation"}, null);
                    if (option != JOptionPane.CLOSED_OPTION) {
                        if (option == 0) {
                            // Browse to an external file
                            showNothingFoundDialog = doFileBrowse();
                        }
                        else
                        if (option == 1) {
                            // Search in the linked Files
                            List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
                            for (WildLogFile wildLogFile : lstFiles) {
                                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                if (UtilsGPS.hasGPSData(temp)) {
                                    showNothingFoundDialog = false;
                                    loadUIValues(temp);
                                    if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        showNothingFoundDialog = false;
                    }
                    getGlassPane().setVisible(false);
                }
            }
            else
            // Is a Location
            if (dataObjectWithGPS instanceof Location) {
                Location location = (Location) dataObjectWithGPS;
                if (location.getName() != null && !location.getName().isEmpty()) {
                    getGlassPane().setVisible(true);
                    int option = JOptionPane.showOptionDialog(WildLogApp.getApplication().getMainFrame(), 
                                    "Please select where to search for GPS points.", 
                                    "Where to search?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                                    null, new String[]{"An external File", "Files already linked to this Place"}, null);
                    if (option != JOptionPane.CLOSED_OPTION) {
                        if (option == 0) {
                            // Browse to an external file
                            showNothingFoundDialog = doFileBrowse();
                        }
                        else
                        if (option == 1) {
                            // Search in the Files
                            List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(location.getWildLogFileID()));
                            for (WildLogFile wildLogFile : lstFiles) {
                                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                if (UtilsGPS.hasGPSData(temp)) {
                                    showNothingFoundDialog = false;
                                    loadUIValues(temp);
                                    if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        showNothingFoundDialog = false;
                    }
                    getGlassPane().setVisible(false);
                }
            }
        }
        if (showNothingFoundDialog) {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(), 
                    "No GPS point was found.", 
                    "No GPS Point", JOptionPane.INFORMATION_MESSAGE);
            getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnUseImageActionPerformed

    private boolean doFileBrowse() {
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
            if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
            return false;
        }
        return true;
    }
    
    private void btnUseOfflineMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseOfflineMapActionPerformed
        // Setup the map
        if (!pnlMap.isVisible()) {
            pnlMap.setVisible(true);
            pack();
            UtilsDialog.setDialogToCenter(getOwner(), this);
        }
        pnlMap.removeAll();
        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(pnlMap.getSize());
        pnlMap.add(jfxPanel, BorderLayout.CENTER);
        // Load the latest GPS location from the UI
        if (tglDecimalDegrees.isSelected()) {
            loadValuesFromDD();
        }
        else {
            loadValuesFromDMS();
        }
        // Show the map
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jfxPanel.setScene(new Scene(new VBox(new Label("Loading"))));
                createMapDefault(jfxPanel);
            }
        });
    }//GEN-LAST:event_btnUseOfflineMapActionPerformed

    private void btnRemoveGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveGPSActionPerformed
        UtilsGPS.copyGpsBetweenDOs(dataObjectWithGPS, new DataObjectWithGPS() {});
        selectionMade = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnRemoveGPSActionPerformed

    private void btnUseRelatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseRelatedActionPerformed
        boolean showNothingFoundDialog = true;
        if (dataObjectWithGPS != null) {
            // Is a Sighting
            if (dataObjectWithGPS instanceof Sighting) {
                Sighting sighting = (Sighting) dataObjectWithGPS;
                if (sighting.getSightingCounter() != 0) {
                    getGlassPane().setVisible(true);
                    int option = JOptionPane.showOptionDialog(WildLogApp.getApplication().getMainFrame(), 
                                    "Please select where to search for related GPS points.", 
                                    "Where to search?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                                    null, new String[]{"Files from this Observation", "GPS from the linked Place", "Files from the linked Place"}, null);
                    if (option != JOptionPane.CLOSED_OPTION) {
                        if (option == 0) {
                            // Search in the Files
                            List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
                            for (WildLogFile wildLogFile : lstFiles) {
                                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                if (UtilsGPS.hasGPSData(temp)) {
                                    showNothingFoundDialog = false;
                                    loadUIValues(temp);
                                    if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                    break;
                                }
                            }
                        }
                        else
                        if (option == 1) {
                            // Use the Location's GPS
                            Location location = app.getDBI().find(new Location(sighting.getLocationName()));
                            if (location != null && UtilsGPS.hasGPSData(location)) {
                                showNothingFoundDialog = false;
                                loadUIValues(location);
                                if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                    Toolkit.getDefaultToolkit().beep();
                                }
                            }
                        }
                        else
                        if (option == 2) {
                            // Use the Location's Files
                            Location location = app.getDBI().find(new Location(sighting.getLocationName()));
                            if (location != null) {
                                List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(location.getWildLogFileID()));
                                for (WildLogFile wildLogFile : lstFiles) {
                                    DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                    if (UtilsGPS.hasGPSData(temp)) {
                                        showNothingFoundDialog = false;
                                        loadUIValues(temp);
                                        if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                            Toolkit.getDefaultToolkit().beep();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        showNothingFoundDialog = false;
                    }
                    getGlassPane().setVisible(false);
                }
            }
            else
            // Is a Location
            if (dataObjectWithGPS instanceof Location) {
                Location location = (Location) dataObjectWithGPS;
                if (location.getName() != null && !location.getName().isEmpty()) {
                    getGlassPane().setVisible(true);
                    int option = JOptionPane.showOptionDialog(WildLogApp.getApplication().getMainFrame(), 
                                    "Please select where to search for related GPS points.", 
                                    "Where to search?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                                    null, new String[]{"Files from this Place", "GPS from linked Observations", "Files from linked Observations"}, null);
                    if (option != JOptionPane.CLOSED_OPTION) {
                        if (option == 0) {
                            // Search in the Files
                            List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(location.getWildLogFileID()));
                            for (WildLogFile wildLogFile : lstFiles) {
                                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                if (UtilsGPS.hasGPSData(temp)) {
                                    showNothingFoundDialog = false;
                                    loadUIValues(temp);
                                    if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                    break;
                                }
                            }
                        }
                        else
                        if (option == 1) {
                            // Use the Sightings' GPS
                            List<Sighting> lstSightings = app.getDBI().list(new Sighting(null, location.getName(), null), false);
                            for (Sighting sighting : lstSightings) {
                                if (UtilsGPS.hasGPSData(sighting)) {
                                    showNothingFoundDialog = false;
                                    loadUIValues(sighting);
                                    if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                }
                            }
                        }
                        else
                        if (option == 2) {
                            // Use the Location's Files
                            List<Sighting> lstSightings = app.getDBI().list(new Sighting(null, location.getName(), null), false);
                            for (Sighting sighting : lstSightings) {
                                List<WildLogFile> lstFiles = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
                                for (WildLogFile wildLogFile : lstFiles) {
                                    DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                                    if (UtilsGPS.hasGPSData(temp)) {
                                        showNothingFoundDialog = false;
                                        loadUIValues(temp);
                                        if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                                            Toolkit.getDefaultToolkit().beep();
                                        }
                                        break;
                                    }
                                }
                                if (showNothingFoundDialog) {
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        showNothingFoundDialog = false;
                    }
                    getGlassPane().setVisible(false);
                }
            }
        }
        if (showNothingFoundDialog) {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(), 
                    "No related GPS point was found.", 
                    "No GPS Point", JOptionPane.INFORMATION_MESSAGE);
            getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnUseRelatedActionPerformed

    private void btnUseOnlineMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseOnlineMapActionPerformed
        // Setup the map
        if (!pnlMap.isVisible()) {
            pnlMap.setVisible(true);
            pack();
            UtilsDialog.setDialogToCenter(getOwner(), this);
        }
        pnlMap.removeAll();
        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(pnlMap.getSize());
//        jfxPanel.setScene(new Scene(new VBox(new Label("Empty"))));
        pnlMap.add(jfxPanel, BorderLayout.CENTER);
        // Load the latest GPS location from the UI
        if (tglDecimalDegrees.isSelected()) {
            loadValuesFromDD();
        }
        else {
            loadValuesFromDMS();
        }
        // Show the map
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jfxPanel.setScene(new Scene(new VBox(new Label("Loading"))));
                jfxPanel.getScene().setRoot(createPointMapGoogle());
            }
        });
    }//GEN-LAST:event_btnUseOnlineMapActionPerformed

    private void tglNorthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglNorthActionPerformed
        if (tglDecimalDegrees.isSelected()) {
            spnLatDecimal.setValue(Math.abs((double) spnLatDecimal.getValue()));
        }
        else {
            spnLatDeg.setValue(Math.abs((int) spnLatDeg.getValue()));
        }
    }//GEN-LAST:event_tglNorthActionPerformed

    private void tglEastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglEastActionPerformed
        if (tglDecimalDegrees.isSelected()) {
            spnLonDecimal.setValue(Math.abs((double) spnLonDecimal.getValue()));
        }
        else {
            spnLonDeg.setValue(Math.abs((int) spnLonDeg.getValue()));
        }
    }//GEN-LAST:event_tglEastActionPerformed

    private void tglSouthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglSouthActionPerformed
        if (tglDecimalDegrees.isSelected()) {
            spnLatDecimal.setValue(-1 * Math.abs((double) spnLatDecimal.getValue()));
        }
        else {
            spnLatDeg.setValue(-1 * Math.abs((int) spnLatDeg.getValue()));
        }
    }//GEN-LAST:event_tglSouthActionPerformed

    private void tglWestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglWestActionPerformed
        if (tglDecimalDegrees.isSelected()) {
            spnLonDecimal.setValue(-1 * Math.abs((double) spnLonDecimal.getValue()));
        }
        else {
            spnLonDeg.setValue(-1 * Math.abs((int) spnLonDeg.getValue()));
        }
    }//GEN-LAST:event_tglWestActionPerformed

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
        Latitudes latitude = Latitudes.NONE;
        if (tglNorth.isSelected()) {
            latitude = Latitudes.NORTH;
        }
        else {
            latitude = Latitudes.SOUTH;
        }
        spnLatDeg.setValue(UtilsGPS.getDegrees(latitude, uiLatitude));
        spnLatMin.setValue(UtilsGPS.getMinutes(uiLatitude));
        spnLatSec.setValue(UtilsGPS.getSeconds(uiLatitude));
        Longitudes longitude = Longitudes.NONE;
        if (tglEast.isSelected()) {
            longitude = Longitudes.EAST;
        }
        else {
            longitude = Longitudes.WEST;
        }
        spnLonDeg.setValue(UtilsGPS.getDegrees(longitude, uiLongitude));
        spnLonMin.setValue(UtilsGPS.getMinutes(uiLongitude));
        spnLonSec.setValue(UtilsGPS.getSeconds(uiLongitude));
    }

    private void loadValuesFromDD() {
        uiLatitude = (double)spnLatDecimal.getValue();
        uiLongitude = (double)spnLonDecimal.getValue();
    }

    private void loadValuesFromDMS() {
        uiLatitude = UtilsGPS.getDecimalDegree(
            Latitudes.NONE,
            (int)spnLatDeg.getValue(),
            (int)spnLatMin.getValue(),
            (double)spnLatSec.getValue());
        uiLongitude = UtilsGPS.getDecimalDegree(
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
    
    private WebView createPointMapGoogle() {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        // Get the template file
        final char[] buffer = new char[4096];
        final StringBuilder builder = new StringBuilder(7500);
        try (Reader in = new BufferedReader(new InputStreamReader(PointMap.class.getResourceAsStream("resources/dragpoint_google.html"), "UTF-8"))) {
            int length = 0;
            while (length >= 0) {
                length = in.read(buffer, 0, buffer.length);
                if (length > 0) {
                    builder.append(buffer, 0, length);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_START___") + "//___MAP_CLICKABLE_DATA_POINTS_START___".length();
        int endIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50);
        gpsBuilder.append(gpsPointTemplate.replace("LatLng(-32, 25)", "LatLng(" + uiLatitude + "," + uiLongitude + ")"));
        gpsBuilder.append(System.lineSeparator());
        template = template.replace("//___MAP_CLICKABLE_DATA_POINTS_START___", "")
                           .replace("//___MAP_CLICKABLE_DATA_POINTS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        // Add the code to perform the call from the JavaScript when the marker is moved
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
              if (newState == State.SUCCEEDED) {
                  JSObject window = (JSObject) webEngine.executeScript("window");
                  window.setMember("JavaMethodExposer", new JavaMethodExposer());
              }
            }
        });
        
        return webView;
    }
    
    // NOTE: Lyk my hierdie moet 'n regte public class wees, anders werk dinge nie reg nie...
    public class JavaMethodExposer {
        public void updateGPS(Object inLat, Object inLon) {
            uiLatitude = (double) inLat;
            uiLongitude = (double) inLon;
            if (tglDecimalDegrees.isSelected()) {
                setupDD();
            }
            else {
                setupDMS();
            }
            if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    private void createMapDefault(JFXPanel inJFXPanel) {
        if (map != null) {
            // Clean up the previous map before creating the new one
            map.dispose();
        }
        map = new GeoToolsMapJavaFX(inJFXPanel, false);
        // Background layer
        try {
            GeoTiffReader reader = new GeoTiffReader(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath()
                    .resolve(BundledMapLayers.EARTH_MODERN.getRelativePath()).toFile());
            map.addLayer(new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader)));
        }
        catch (DataSourceException ex) {
            ex.printStackTrace(System.err);
        }
        // Point layer
        try {
            SimpleFeatureType type = DataUtilities.createType("WildLogPointType", "geom:Point,name:String,mydata:String");
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            builder.add(geometryFactory.createPoint(new Coordinate(uiLongitude, uiLatitude)));
            SimpleFeature feature = builder.buildFeature("GPS Point");
            collection.add(feature);
            Style pointStyle = GeoToolsLayerUtils.createPointStyle(new Color(80, 15, 5), new Color(175, 30, 20), 0.8, 0.5, 12);
            map.addLayer(new FeatureLayer(collection, pointStyle, "WildLogPointLayer"));
        }
        catch (SchemaException | FactoryRegistryException ex) {
            ex.printStackTrace(System.err);
        }
        // Reload the map to make sure the layers are added correctly, etc.
        map.reloadMap();
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveGPS;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUseGPX;
    private javax.swing.JButton btnUseImage;
    private javax.swing.JButton btnUseOfflineMap;
    private javax.swing.JButton btnUseOnlineMap;
    private javax.swing.JButton btnUsePrevious;
    private javax.swing.JButton btnUseRelated;
    private javax.swing.ButtonGroup buttonGroupFormat;
    private javax.swing.ButtonGroup buttonGroupLatitude;
    private javax.swing.ButtonGroup buttonGroupLongitude;
    private javax.swing.JComboBox cmbAccuracy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel pnlMap;
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
