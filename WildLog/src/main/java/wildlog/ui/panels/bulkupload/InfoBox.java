package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.text.SimpleDateFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Sex;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.helpers.HorizontalSpinner;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.panels.PanelSighting;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class InfoBox extends JPanel {
    private static final SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
    private final WildLogApp app;
    private final BulkUploadSightingWrapper sightingWrapper;
    private final Location location;
    private final Visit visit;
    private final JTable table;
    


    public InfoBox(WildLogApp inApp, BulkUploadSightingWrapper inBulkUploadSightingWrapper, Location inLocation, Visit inVisit, JTable inTable) {
        app = inApp;
        location = inLocation;
        visit = inVisit;
        table = inTable;
        initComponents();
        sightingWrapper = inBulkUploadSightingWrapper;
        populateUI();
        sightingWrapper.setInfoBox(this);
        // Configure the spinner
        SpinnerFixer.configureSpinners(spnNumber);
        spnNumber.setUI(new HorizontalSpinner());
        JComponent spinEditor = spnNumber.getEditor();
        if (spinEditor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) spinEditor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
            spinnerEditor.getTextField().setBackground(new Color(225, 230, 215));
        }
    }

    public final void populateUI() {
        if (sightingWrapper.getDate() != null) {
            lblDate.setText(UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(sightingWrapper.getDate())));
            lblTime.setText(timeFormater.format(sightingWrapper.getDate()));
        }
        lblElementName.setText(sightingWrapper.getCachedElementName());
        lblLatitude.setText(UtilsGPS.getLatitudeString(sightingWrapper));
        lblLongitude.setText(UtilsGPS.getLongitudeString(sightingWrapper));
        lblImage.setIcon(sightingWrapper.getIcon());
        cmbSex.setSelectedItem(sightingWrapper.getSex());
        cmbCertainty.setSelectedItem(sightingWrapper.getCertainty());
        spnNumber.setValue(sightingWrapper.getNumberOfElements());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        lblElementName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblLatitude = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblLongitude = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblImage = new javax.swing.JLabel();
        btnChooseCreature = new javax.swing.JButton();
        btnGPS = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        lblCount = new javax.swing.JLabel();
        spnNumber = new javax.swing.JSpinner();
        cmbSex = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        cmbCertainty = new javax.swing.JComboBox<>();

        jSeparator3.setName("jSeparator3"); // NOI18N

        setBackground(new java.awt.Color(229, 241, 212));
        setMaximumSize(new java.awt.Dimension(250, 250));
        setMinimumSize(new java.awt.Dimension(250, 250));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(250, 250));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N
        add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 3, -1, 25));

        lblElementName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblElementName.setText("Creature Name");
        lblElementName.setName("lblElementName"); // NOI18N
        add(lblElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 3, 195, 20));

        jSeparator2.setName("jSeparator2"); // NOI18N
        add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 27, 230, 2));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Date:");
        jLabel2.setName("jLabel2"); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblDate.setText("01 Jan 2012");
        lblDate.setName("lblDate"); // NOI18N
        add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 30, 70, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Time:");
        jLabel3.setName("jLabel3"); // NOI18N
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 30, -1, -1));

        lblTime.setText("11:11 pm");
        lblTime.setName("lblTime"); // NOI18N
        add(lblTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 50, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Lat:");
        jLabel4.setName("jLabel4"); // NOI18N
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 53, -1, -1));

        lblLatitude.setText("33.123456");
        lblLatitude.setName("lblLatitude"); // NOI18N
        add(lblLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 53, 75, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Lon:");
        jLabel10.setName("jLabel10"); // NOI18N
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 53, -1, -1));

        lblLongitude.setText("25.1234567");
        lblLongitude.setName("lblLongitude"); // NOI18N
        add(lblLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 53, 75, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 47, 230, 2));

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setOpaque(true);
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });
        add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 80, 125, 125));

        btnChooseCreature.setBackground(new java.awt.Color(229, 241, 212));
        btnChooseCreature.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ElementList.png"))); // NOI18N
        btnChooseCreature.setText("<html><u>Creature</u></html>");
        btnChooseCreature.setToolTipText("Select a Creature for this Observation. You can RIGHT-CLICK to automatically select the previously saved Creature.");
        btnChooseCreature.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChooseCreature.setFocusPainted(false);
        btnChooseCreature.setFocusable(false);
        btnChooseCreature.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChooseCreature.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnChooseCreature.setName("btnChooseCreature"); // NOI18N
        btnChooseCreature.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnChooseCreatureMouseReleased(evt);
            }
        });
        btnChooseCreature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCreatureActionPerformed(evt);
            }
        });
        add(btnChooseCreature, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 85, 100, 45));

        btnGPS.setBackground(new java.awt.Color(229, 241, 212));
        btnGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPS.setText("<html><u>GPS</u></html>");
        btnGPS.setToolTipText("Select a GPS point for this Observation. You can RIGHT-CLICK to select the previously saved GPS point or MIDDLE-CLICK to load the GPS point from the images (if present).");
        btnGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPS.setFocusPainted(false);
        btnGPS.setFocusable(false);
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnGPSMouseReleased(evt);
            }
        });
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });
        add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 130, 100, 34));

        btnEdit.setBackground(new java.awt.Color(229, 241, 212));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting Small.gif"))); // NOI18N
        btnEdit.setText("Edit Observation");
        btnEdit.setToolTipText("Edit this Observation's details.");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.setFocusPainted(false);
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEdit.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 208, 125, 27));

        lblCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCount.setName("lblCount"); // NOI18N
        add(lblCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 30, 20));

        spnNumber.setModel(new javax.swing.SpinnerNumberModel(0, 0, 2147483642, 1));
        spnNumber.setToolTipText("Number of individuals.");
        spnNumber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnNumber.setFocusable(false);
        spnNumber.setName("spnNumber"); // NOI18N
        spnNumber.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnNumberStateChanged(evt);
            }
        });
        add(spnNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 213, 70, 22));

        cmbSex.setModel(new DefaultComboBoxModel(Sex.values()));
        cmbSex.setSelectedItem(Sex.NONE);
        cmbSex.setToolTipText("Gender of the creature.");
        cmbSex.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbSex.setFocusable(false);
        cmbSex.setName("cmbSex"); // NOI18N
        cmbSex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSexActionPerformed(evt);
            }
        });
        add(cmbSex, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 188, 70, 22));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("No.");
        jLabel5.setName("jLabel5"); // NOI18N
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 213, -1, 22));

        jSeparator5.setName("jSeparator5"); // NOI18N
        add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 72, 230, 2));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Sex");
        jLabel6.setName("jLabel6"); // NOI18N
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 188, -1, 22));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setToolTipText("Observation certainty.");
        cmbCertainty.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCertainty.setFocusable(false);
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        cmbCertainty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCertaintyActionPerformed(evt);
            }
        });
        add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 166, 100, 20));
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        PanelSighting dialog = new PanelSighting(
                app,
                app.getMainFrame(), "Edit the Observation",
                sightingWrapper,
                location,
                visit,
                app.getDBI().findElement(sightingWrapper.getElementID(), null, Element.class),
                null,
                false, false, true, false);
        dialog.setVisible(true);
        // Update the UI
        table.getCellEditor().stopCellEditing();
        UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getElementID(), 
                0, lblImage, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
        sightingWrapper.setIcon(lblImage.getIcon());
        populateUI();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnChooseCreatureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCreatureActionPerformed
        ElementSelectionDialog dialog = new ElementSelectionDialog(app.getMainFrame(), app, sightingWrapper.getElementID());
        dialog.setVisible(true);
        // Set the label to the selected text
        table.getCellEditor().stopCellEditing();
        if (dialog.isSelectionMade() && dialog.getSelectedElementID() > 0) {
            sightingWrapper.setElementID(dialog.getSelectedElementID());
            sightingWrapper.setCachedElementName(dialog.getSelectedElementName());
            sightingWrapper.setIcon(dialog.getSelectedElementIcon());
        }
    }//GEN-LAST:event_btnChooseCreatureActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getElementID(), 0, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app, app.getMainFrame(), sightingWrapper);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            table.getCellEditor().stopCellEditing();
            lblLatitude.setText(UtilsGPS.getLatitudeString(sightingWrapper));
            lblLongitude.setText(UtilsGPS.getLongitudeString(sightingWrapper));
        }
    }//GEN-LAST:event_btnGPSActionPerformed

    private void btnChooseCreatureMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChooseCreatureMouseReleased
        if (evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt)) {
            if (ElementSelectionDialog.getPreviousElementID() > 0) {
                // Set the label to the selected text
                table.getCellEditor().stopCellEditing();
                sightingWrapper.setElementID(ElementSelectionDialog.getPreviousElementID());
                sightingWrapper.setCachedElementName(ElementSelectionDialog.getPreviousElementName());
                UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + ElementSelectionDialog.getPreviousElementID(), 
                        0, lblImage, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
                sightingWrapper.setIcon(lblImage.getIcon());
                evt.consume();
            }
        }
    }//GEN-LAST:event_btnChooseCreatureMouseReleased

    private void btnGPSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGPSMouseReleased
        if (evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt)) {
            table.getCellEditor().stopCellEditing();
            DataObjectWithGPS temp = new DataObjectWithGPS() {};
            temp.setLatitude(GPSDialog.getPrevLat());
            temp.setLatDegrees(GPSDialog.getPrevLatDeg());
            temp.setLatMinutes(GPSDialog.getPrevLatMin());
            temp.setLatSeconds(GPSDialog.getPrevLatSec());
            temp.setLongitude(GPSDialog.getPrevLon());
            temp.setLonDegrees(GPSDialog.getPrevLonDeg());
            temp.setLonMinutes(GPSDialog.getPrevLonMin());
            temp.setLonSeconds(GPSDialog.getPrevLonSec());
            temp.setGPSAccuracy(GPSDialog.getPrevAccuracy());
            temp.setGPSAccuracyValue(GPSDialog.getPrevAccuracyValue());
            UtilsGPS.copyGpsBetweenDOs(sightingWrapper, temp);
            lblLatitude.setText(UtilsGPS.getLatitudeString(sightingWrapper));
            lblLongitude.setText(UtilsGPS.getLongitudeString(sightingWrapper));
        }
        else
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)table.getModel().getValueAt(table.getEditingRow(), 1);
            for (BulkUploadImageFileWrapper imageFileWrapper : listWrapper.getImageList()) {
                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(imageFileWrapper.getFile());
                if (!UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLatitudeString(temp))
                        && !UtilsGPS.NO_GPS_POINT.equals(UtilsGPS.getLongitudeString(temp))) {
                    table.getCellEditor().stopCellEditing();
                    UtilsGPS.copyGpsBetweenDOs(sightingWrapper, temp);
                    lblLatitude.setText(UtilsGPS.getLatitudeString(sightingWrapper));
                    lblLongitude.setText(UtilsGPS.getLongitudeString(sightingWrapper));
                }
            }
        }
    }//GEN-LAST:event_btnGPSMouseReleased

    private void cmbSexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSexActionPerformed
        sightingWrapper.setSex((Sex) cmbSex.getSelectedItem());
    }//GEN-LAST:event_cmbSexActionPerformed

    private void spnNumberStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnNumberStateChanged
        sightingWrapper.setNumberOfElements((int) spnNumber.getValue());
    }//GEN-LAST:event_spnNumberStateChanged

    private void cmbCertaintyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCertaintyActionPerformed
        sightingWrapper.setCertainty((Certainty) cmbCertainty.getSelectedItem());
    }//GEN-LAST:event_cmbCertaintyActionPerformed

    public void setRowBackground(Color inColor) {
        this.setBackground(inColor);
        btnChooseCreature.setBackground(inColor);
        btnEdit.setBackground(inColor);
        btnGPS.setBackground(inColor);
    }

    public JLabel getLblCount() {
        return lblCount;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseCreature;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnGPS;
    private javax.swing.JComboBox<String> cmbCertainty;
    private javax.swing.JComboBox<String> cmbSex;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblElementName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLatitude;
    private javax.swing.JLabel lblLongitude;
    private javax.swing.JLabel lblTime;
    private javax.swing.JSpinner spnNumber;
    // End of variables declaration//GEN-END:variables
}
