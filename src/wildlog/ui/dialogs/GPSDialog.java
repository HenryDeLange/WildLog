package wildlog.ui.dialogs;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.Dialog;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.gpx.GpxReader;
import wildlog.ui.helpers.FileDrop;
import wildlog.mapping.utils.LatLonConverter;
import wildlog.ui.helpers.GpxFilter;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class GPSDialog extends JDialog {
    private static String lastFilePath = "";
    private boolean selectionMade = false;
    private DataObjectWithGPS dataObjectWithGPS;
    private double uiLatitude = 0.0;
    private double uiLongitude = 0.0;
    private WildLogApp app;


    public GPSDialog(JFrame parent, boolean modal, DataObjectWithGPS inDataObjectWithGPS) {
        super(parent, modal);
        // Do the setup
        doSetup(inDataObjectWithGPS);
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(parent, this);
        UtilsDialog.addModalBackgroundPanel(parent, this);
    }

    public GPSDialog(Dialog parent, DataObjectWithGPS inDataObjectWithGPS) {
        super(parent);
        // Do the setup
        doSetup(inDataObjectWithGPS);
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(parent, this);
        app.getMainFrame().getGlassPane().setVisible(true);
    }

    private void doSetup(DataObjectWithGPS inDataObjectWithGPS) {
        app = (WildLogApp)Application.getInstance();
        dataObjectWithGPS = inDataObjectWithGPS;
        if (dataObjectWithGPS == null)
            dispose();
        // Need to set a few settings onthe content pane before continuing (for example size, background color, etc.)
        getContentPane().setPreferredSize(new Dimension(410, 210));
        // Initialize the auto generated code
        initComponents();
        // Hide stuff
        spnLatDecimal.setVisible(false);
        spnLonDecimal.setVisible(false);
        // Fix the selection of the spinners
        SpinnerFixer.fixSelectAllForSpinners(spnLatDecimal);
        SpinnerFixer.fixSelectAllForSpinners(spnLatDeg);
        SpinnerFixer.fixSelectAllForSpinners(spnLatMin);
        SpinnerFixer.fixSelectAllForSpinners(spnLatSec);
        SpinnerFixer.fixSelectAllForSpinners(spnLonDecimal);
        SpinnerFixer.fixSelectAllForSpinners(spnLonDeg);
        SpinnerFixer.fixSelectAllForSpinners(spnLonMin);
        SpinnerFixer.fixSelectAllForSpinners(spnLonSec);
        // Setup the ui Lat and Lon
        Latitudes tempLat = dataObjectWithGPS.getLatitude();
        if (tempLat == null || Latitudes.NONE.equals(tempLat))
            tempLat = app.getWildLogOptions().getDefaultInputLatitude();
        if (Latitudes.NORTH.equals(dataObjectWithGPS.getLatitude())) {
            tglNorth.setSelected(true);
        }
        else
        if (Latitudes.SOUTH.equals(dataObjectWithGPS.getLatitude())) {
            tglSouth.setSelected(true);
        }
        Longitudes tempLon = dataObjectWithGPS.getLongitude();
        if (tempLon == null || Longitudes.NONE.equals(tempLon))
            tempLon = app.getWildLogOptions().getDefaultInputLongitude();
        if (Longitudes.EAST.equals(tempLon)) {
            tglEast.setSelected(true);
        }
        else
        if (Longitudes.WEST.equals(tempLon)) {
            tglWest.setSelected(true);
        }
        // Get existing value from passed in dataObjectWithGPS
        loadUIValues(dataObjectWithGPS);
        // Setup the drag and drop on the butons
        FileDrop.SetupFileDrop(btnUseGPX, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() > 0) {
                    doGpxInput(inFiles.get(0));
                }
            }
        });
        FileDrop.SetupFileDrop(btnUseImage, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                if (inFiles != null && inFiles.size() > 0) {
                    doExifInput(inFiles.get(0));
                }
            }
        });
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
    }

    private void loadUIValues(DataObjectWithGPS inDataObjectWithGPS) {
        uiLatitude = LatLonConverter.getDecimalDegree(
                Latitudes.NONE,
                inDataObjectWithGPS.getLatDegrees(),
                inDataObjectWithGPS.getLatMinutes(),
                inDataObjectWithGPS.getLatSeconds());
        uiLongitude = LatLonConverter.getDecimalDegree(
                Longitudes.NONE,
                inDataObjectWithGPS.getLonDegrees(),
                inDataObjectWithGPS.getLonMinutes(),
                inDataObjectWithGPS.getLonSeconds());
        // Populate the initial values into the spinners
        tglDecimalDegrees.setSelected(true);
        setupDD();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnUsePrevious = new javax.swing.JButton();
        btnUseImage = new javax.swing.JButton();
        btnUseMap = new javax.swing.JButton();
        btnUseGPX = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(GPSDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/GPS.png")).getImage());
        setMinimumSize(new java.awt.Dimension(410, 210));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnUsePrevious.setIcon(resourceMap.getIcon("btnUsePrevious.icon")); // NOI18N
        btnUsePrevious.setText(resourceMap.getString("btnUsePrevious.text")); // NOI18N
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

        btnUseImage.setIcon(resourceMap.getIcon("btnUseImage.icon")); // NOI18N
        btnUseImage.setText(resourceMap.getString("btnUseImage.text")); // NOI18N
        btnUseImage.setToolTipText(resourceMap.getString("btnUseImage.toolTipText")); // NOI18N
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

        btnUseMap.setIcon(resourceMap.getIcon("btnUseMap.icon")); // NOI18N
        btnUseMap.setText(resourceMap.getString("btnUseMap.text")); // NOI18N
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

        btnUseGPX.setIcon(resourceMap.getIcon("btnUseGPX.icon")); // NOI18N
        btnUseGPX.setText(resourceMap.getString("btnUseGPX.text")); // NOI18N
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

        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setFocusPainted(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 110, 60));

        jSeparator1.setName("jSeparator1"); // NOI18N
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 390, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        tglNorth.setText(Latitudes.NORTH.getText());
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
        tglSouth.setFocusPainted(false);
        tglSouth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglSouth.setName("tglSouth"); // NOI18N
        tglSouth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglSouthActionPerformed(evt);
            }
        });
        getContentPane().add(tglSouth, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 60, 30));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        tglWest.setText(Longitudes.WEST.getText());
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
        tglEast.setFocusPainted(false);
        tglEast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        tglEast.setName("tglEast"); // NOI18N
        tglEast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglEastActionPerformed(evt);
            }
        });
        getContentPane().add(tglEast, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 60, 30));

        tglDecimalDegrees.setText(resourceMap.getString("tglDecimalDegrees.text")); // NOI18N
        tglDecimalDegrees.setToolTipText(resourceMap.getString("tglDecimalDegrees.toolTipText")); // NOI18N
        tglDecimalDegrees.setFocusPainted(false);
        tglDecimalDegrees.setName("tglDecimalDegrees"); // NOI18N
        tglDecimalDegrees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDecimalDegreesActionPerformed(evt);
            }
        });
        getContentPane().add(tglDecimalDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, 60, 45));

        tglDegMinSec.setText(resourceMap.getString("tglDegMinSec.text")); // NOI18N
        tglDegMinSec.setToolTipText(resourceMap.getString("tglDegMinSec.toolTipText")); // NOI18N
        tglDegMinSec.setFocusPainted(false);
        tglDegMinSec.setName("tglDegMinSec"); // NOI18N
        tglDegMinSec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDegMinSecActionPerformed(evt);
            }
        });
        getContentPane().add(tglDegMinSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 155, 60, 45));

        spnLatDeg.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLatDeg.setModel(new javax.swing.SpinnerNumberModel(0, -90, 90, 1));
        spnLatDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDeg, "#"));
        spnLatDeg.setName("spnLatDeg"); // NOI18N
        getContentPane().add(spnLatDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 50, 30));

        spnLatMin.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLatMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLatMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatMin, "#"));
        spnLatMin.setName("spnLatMin"); // NOI18N
        getContentPane().add(spnLatMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 50, 30));

        spnLatSec.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLatSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 1.0E-4d));
        spnLatSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatSec, "#.####"));
        spnLatSec.setName("spnLatSec"); // NOI18N
        getContentPane().add(spnLatSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 70, 30));

        spnLatDecimal.setFont(resourceMap.getFont("spnLatDecimal.font")); // NOI18N
        spnLatDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 90.0d, 1.0E-5d));
        spnLatDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDecimal, "#.############"));
        spnLatDecimal.setName("spnLatDecimal"); // NOI18N
        getContentPane().add(spnLatDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, 190, 30));

        spnLonDeg.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLonDeg.setModel(new javax.swing.SpinnerNumberModel(0, 0, 180, 1));
        spnLonDeg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDeg.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDeg, "#"));
        spnLonDeg.setName("spnLonDeg"); // NOI18N
        getContentPane().add(spnLonDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 50, 30));

        spnLonMin.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLonMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLonMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonMin.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonMin, "#"));
        spnLonMin.setName("spnLonMin"); // NOI18N
        getContentPane().add(spnLonMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 50, 30));

        spnLonSec.setFont(resourceMap.getFont("spnLonSec.font")); // NOI18N
        spnLonSec.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 59.999999999d, 1.0E-4d));
        spnLonSec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonSec.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonSec, "#.####"));
        spnLonSec.setName("spnLonSec"); // NOI18N
        getContentPane().add(spnLonSec, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 70, 30));

        spnLonDecimal.setFont(resourceMap.getFont("spnLonDecimal.font")); // NOI18N
        spnLonDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 180.0d, 1.0E-5d));
        spnLonDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDecimal, "#.############"));
        spnLonDecimal.setName("spnLonDecimal"); // NOI18N
        getContentPane().add(spnLonDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 190, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Get lat and long enums
        if (tglNorth.isSelected())
            dataObjectWithGPS.setLatitude(Latitudes.NORTH);
        else
            dataObjectWithGPS.setLatitude(Latitudes.SOUTH);
        if (tglEast.isSelected())
            dataObjectWithGPS.setLongitude(Longitudes.EAST);
        else
            dataObjectWithGPS.setLongitude(Longitudes.WEST);
        // Get the number values
        if (tglDecimalDegrees.isSelected()) {
            // Use decimal degrees
            double latDecimalDegree = (double)spnLatDecimal.getValue();
            double lonDecimalDegree = (double)spnLonDecimal.getValue();
            dataObjectWithGPS.setLatDegrees(LatLonConverter.getDegrees(Latitudes.NONE, latDecimalDegree));
            dataObjectWithGPS.setLonDegrees(LatLonConverter.getDegrees(Longitudes.NONE, lonDecimalDegree));
            dataObjectWithGPS.setLatMinutes(LatLonConverter.getMinutes(latDecimalDegree));
            dataObjectWithGPS.setLonMinutes(LatLonConverter.getMinutes(lonDecimalDegree));
            dataObjectWithGPS.setLatSeconds(LatLonConverter.getSeconds(latDecimalDegree));
            dataObjectWithGPS.setLonSeconds(LatLonConverter.getSeconds(lonDecimalDegree));
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
        selectionMade = true;
        // Now update the "previous" GPS value
        app.setPrevLat(dataObjectWithGPS.getLatitude());
        app.setPrevLatDeg(dataObjectWithGPS.getLatDegrees());
        app.setPrevLatMin(dataObjectWithGPS.getLatMinutes());
        app.setPrevLatSec(dataObjectWithGPS.getLatSeconds());
        app.setPrevLon(dataObjectWithGPS.getLongitude());
        app.setPrevLonDeg(dataObjectWithGPS.getLonDegrees());
        app.setPrevLonMin(dataObjectWithGPS.getLonMinutes());
        app.setPrevLonSec(dataObjectWithGPS.getLonSeconds());
        // We are done, dispose this dialog
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tglNorthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglNorthActionPerformed
        if (tglNorth.isSelected())
            tglSouth.setSelected(false);
        else
            tglNorth.setSelected(true);
    }//GEN-LAST:event_tglNorthActionPerformed

    private void tglSouthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglSouthActionPerformed
        if (tglSouth.isSelected())
            tglNorth.setSelected(false);
        else
            tglSouth.setSelected(true);
    }//GEN-LAST:event_tglSouthActionPerformed

    private void tglWestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglWestActionPerformed
        if (tglWest.isSelected())
            tglEast.setSelected(false);
        else
            tglWest.setSelected(true);
    }//GEN-LAST:event_tglWestActionPerformed

    private void tglEastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglEastActionPerformed
        if (tglEast.isSelected())
            tglWest.setSelected(false);
        else
            tglEast.setSelected(true);
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
        if (lastFilePath != null && lastFilePath.length() > 0)
            fileChooser = new JFileChooser(lastFilePath);
        else
            fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new GpxFilter());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        int result = fileChooser.showOpenDialog(this);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            File file = fileChooser.getSelectedFile();
            lastFilePath = file.getAbsolutePath();
            doGpxInput(file);
        }
    }//GEN-LAST:event_btnUseGPXActionPerformed

    private void btnUsePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsePreviousActionPerformed
        DataObjectWithGPS temp = new DataObjectWithGPS() {};
        temp.setLatitude(app.getPrevLat());
        temp.setLatDegrees(app.getPrevLatDeg());
        temp.setLatMinutes(app.getPrevLatMin());
        temp.setLatSeconds(app.getPrevLatSec());
        temp.setLongitude(app.getPrevLon());
        temp.setLonDegrees(app.getPrevLonDeg());
        temp.setLonMinutes(app.getPrevLonMin());
        temp.setLonSeconds(app.getPrevLonSec());
        loadUIValues(temp);
    }//GEN-LAST:event_btnUsePreviousActionPerformed

    private void btnUseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseImageActionPerformed
        JFileChooser fileChooser;
        if (lastFilePath != null && lastFilePath.length() > 0)
            fileChooser = new JFileChooser(lastFilePath);
        else
            fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        int result = fileChooser.showOpenDialog(this);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            File file = fileChooser.getSelectedFile();
            lastFilePath = file.getAbsolutePath();
            doExifInput(file);
        }
    }//GEN-LAST:event_btnUseImageActionPerformed

    private void btnUseMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseMapActionPerformed
        // TODO: GPS point from map - Ek sal die deel later moet uitfigure en doen...
        JOptionPane.showConfirmDialog(this, "TODO", "TODO", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_btnUseMapActionPerformed

    private void doGpxInput(File inFile) {
        String gpxValue = JOptionPane.showInputDialog(this, "Please enter the GPX Waypoint's name:", "Load Coordinates From GPX", JOptionPane.INFORMATION_MESSAGE);
        if (gpxValue != null) {
            DataObjectWithGPS temp = new DataObjectWithGPS() {};
            GpxReader.populateGPSFromGpxFile(inFile, gpxValue, temp);
            loadUIValues(temp);
        }
    }

    private void doExifInput(File inFile) {
        try {
            DataObjectWithGPS tempDO = new DataObjectWithGPS() {};
            Metadata metadata = JpegMetadataReader.readMetadata(inFile);
            Iterator<Directory> directories = metadata.getDirectories().iterator();
            while (directories.hasNext()) {
                Directory directory = (Directory)directories.next();
                Collection<Tag> tags = directory.getTags();
                for (Tag tag : tags) {
                    try {
                        if (tag.getTagName().equalsIgnoreCase("GPS Latitude Ref")) {
                            // Voorbeeld S
                            tempDO.setLatitude(Latitudes.getEnumFromText(tag.getDescription()));
                        }
                        else
                        if (tag.getTagName().equalsIgnoreCase("GPS Longitude Ref")) {
                            // Voorbeeld E
                            tempDO.setLatitude(Latitudes.getEnumFromText(tag.getDescription()));
                        }
                        else
                        if (tag.getTagName().equalsIgnoreCase("GPS Latitude")) {
                            // Voorbeeld -33°44'57.0"
                            String temp = tag.getDescription();
                            tempDO.setLatDegrees((int)Double.parseDouble(temp.substring(0, temp.indexOf("°")).trim()));
                            tempDO.setLatMinutes((int)Double.parseDouble(temp.substring(temp.indexOf("°")+1, temp.indexOf("'")).trim()));
                            tempDO.setLatSeconds(Double.parseDouble(temp.substring(temp.indexOf("'")+1, temp.indexOf("\"")).trim()));
                        }
                        else
                        if (tag.getTagName().equalsIgnoreCase("GPS Longitude")) {
                            // Voorbeeld 26°28'7.0"
                            String temp = tag.getDescription();
                            tempDO.setLonDegrees((int)Double.parseDouble(temp.substring(0, temp.indexOf("°")).trim()));
                            tempDO.setLonMinutes((int)Double.parseDouble(temp.substring(temp.indexOf("°")+1, temp.indexOf("'")).trim()));
                            tempDO.setLonSeconds(Double.parseDouble(temp.substring(temp.indexOf("'")+1, temp.indexOf("\"")).trim()));
                        }
                    }
                    catch (NumberFormatException ex) {
                        System.err.println("Could not parse GPS info from image EXIF data: " + tag.getTagName() + " = " + tag.getDescription());
                    }
                }
            }
            loadUIValues(tempDO);
        }
        catch (JpegProcessingException | IOException ex) {
            ex.printStackTrace(System.err);
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
        spnLatDeg.setValue(LatLonConverter.getDegrees(Latitudes.NONE, uiLatitude));
        spnLatMin.setValue(LatLonConverter.getMinutes(uiLatitude));
        spnLatSec.setValue(LatLonConverter.getSeconds(uiLatitude));
        spnLonDeg.setValue(LatLonConverter.getDegrees(Longitudes.NONE, uiLongitude));
        spnLonMin.setValue(LatLonConverter.getMinutes(uiLongitude));
        spnLonSec.setValue(LatLonConverter.getSeconds(uiLongitude));
    }

    private void loadValuesFromDD() {
        uiLatitude = (double)spnLatDecimal.getValue();
        uiLongitude = (double)spnLonDecimal.getValue();
    }

    private void loadValuesFromDMS() {
        uiLatitude = LatLonConverter.getDecimalDegree(
            Latitudes.NONE,
            (int)spnLatDeg.getValue(),
            (int)spnLatMin.getValue(),
            (double)spnLatSec.getValue());
        uiLongitude = LatLonConverter.getDecimalDegree(
            Longitudes.NONE,
            (int)spnLonDeg.getValue(),
            (int)spnLonMin.getValue(),
            (double)spnLonSec.getValue());
    }

    public boolean isSelectionMade() {
        return selectionMade;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUseGPX;
    private javax.swing.JButton btnUseImage;
    private javax.swing.JButton btnUseMap;
    private javax.swing.JButton btnUsePrevious;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
