package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.panels.PanelSighting;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class InfoBox extends JPanel {
    private static final SimpleDateFormat dateFormater = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
    private final WildLogApp app;
    private final BulkUploadSightingWrapper sightingWrapper;
    private final JLabel lblLocation;
    private final JTextField txtVisit;
    private final JTable table;


    public InfoBox(WildLogApp inApp, BulkUploadSightingWrapper inBulkUploadSightingWrapper, JLabel inLblLocation, JTextField inTxtVisit, JTable inTable) {
        app = inApp;
        lblLocation = inLblLocation;
        txtVisit = inTxtVisit;
        table = inTable;
        initComponents();
        sightingWrapper = inBulkUploadSightingWrapper;
        populateUI();
        sightingWrapper.setInfoBox(this);
    }

    public final void populateUI() {
        if (sightingWrapper.getDate() != null) {
            lblDate.setText(dateFormater.format(sightingWrapper.getDate()));
            lblTime.setText(timeFormater.format(sightingWrapper.getDate()));
        }
        lblElementName.setText(sightingWrapper.getElementName());
        lblLatitude.setText(UtilsGps.getLatitudeString(sightingWrapper));
        lblLongitude.setText(UtilsGps.getLongitudeString(sightingWrapper));
        lblImage.setIcon(sightingWrapper.getIcon());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        setBackground(new java.awt.Color(229, 241, 212));
        setMaximumSize(new java.awt.Dimension(250, 250));
        setMinimumSize(new java.awt.Dimension(250, 250));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(250, 250));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblElementName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementName.setText("Creature Name");
        lblElementName.setName("lblElementName"); // NOI18N
        add(lblElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 230, 20));

        jSeparator2.setName("jSeparator2"); // NOI18N
        add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 27, 230, 10));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Date:");
        jLabel2.setName("jLabel2"); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        lblDate.setText("01 Jan 2012");
        lblDate.setName("lblDate"); // NOI18N
        add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 70, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Time:");
        jLabel3.setName("jLabel3"); // NOI18N
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, -1, -1));

        lblTime.setText("11:11 pm");
        lblTime.setName("lblTime"); // NOI18N
        add(lblTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 50, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("GPS Latitude:");
        jLabel4.setName("jLabel4"); // NOI18N
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 53, -1, -1));

        lblLatitude.setText("33.123456");
        lblLatitude.setName("lblLatitude"); // NOI18N
        add(lblLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 53, 130, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("GPS Longitude:");
        jLabel10.setName("jLabel10"); // NOI18N
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        lblLongitude.setText("25.1234567");
        lblLongitude.setName("lblLongitude"); // NOI18N
        add(lblLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 130, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 47, 230, 10));

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
        add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 85, 150, 150));

        btnChooseCreature.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ElementList.gif"))); // NOI18N
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
        add(btnChooseCreature, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 85, 80, 50));

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
        add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 135, 80, 50));

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting.gif"))); // NOI18N
        btnEdit.setText("Edit");
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
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 185, 80, 50));
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        Location tempLocation = null;
        if (lblLocation.getText() != null && !lblLocation.getText().isEmpty()) {
            tempLocation = new Location(lblLocation.getText());
        }
        PanelSighting dialog = new PanelSighting(
                app,
                app.getMainFrame(), "Edit the Observation",
                sightingWrapper,
                tempLocation,
                new Visit(txtVisit.getText()),
                new Element(sightingWrapper.getElementName()),
                null,
                false, false, true);
        dialog.setVisible(true);
        // Update the UI
        table.getCellEditor().stopCellEditing();
        UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getElementName(), 0, lblImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
        sightingWrapper.setIcon(lblImage.getIcon());
        populateUI();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnChooseCreatureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCreatureActionPerformed
        ElementSelectionDialog dialog = new ElementSelectionDialog(app.getMainFrame(), true, app, sightingWrapper.getElementName());
        dialog.setVisible(true);
        // Set the label to the selected text
        table.getCellEditor().stopCellEditing();
        if (dialog.isSelectionMade() && dialog.getElementName() != null && dialog.getElementName().length() > 0) {
            sightingWrapper.setElementName(dialog.getElementName());
            sightingWrapper.setIcon(dialog.getElementIcon());
        }
    }//GEN-LAST:event_btnChooseCreatureActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getElementName(), 0, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app, app.getMainFrame(), sightingWrapper);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            table.getCellEditor().stopCellEditing();
            lblLatitude.setText(UtilsGps.getLatitudeString(sightingWrapper));
            lblLongitude.setText(UtilsGps.getLongitudeString(sightingWrapper));
        }
    }//GEN-LAST:event_btnGPSActionPerformed

    private void btnChooseCreatureMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChooseCreatureMouseReleased
        if (evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt)) {
            if (ElementSelectionDialog.getPreviousElement() != null && !ElementSelectionDialog.getPreviousElement().isEmpty()) {
                // Set the label to the selected text
                table.getCellEditor().stopCellEditing();
                sightingWrapper.setElementName(ElementSelectionDialog.getPreviousElement());
                UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + ElementSelectionDialog.getPreviousElement(), 0, lblImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
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
            UtilsGps.copyGpsBetweenDOs(sightingWrapper, temp);
            lblLatitude.setText(UtilsGps.getLatitudeString(sightingWrapper));
            lblLongitude.setText(UtilsGps.getLongitudeString(sightingWrapper));
        }
        else
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)table.getModel().getValueAt(table.getEditingRow(), 1);
            for (BulkUploadImageFileWrapper imageFileWrapper : listWrapper.getImageList()) {
                DataObjectWithGPS temp = UtilsImageProcessing.getExifGpsFromJpeg(imageFileWrapper.getFile());
                if (!UtilsGps.NO_GPS_POINT.equals(UtilsGps.getLatitudeString(temp))
                        && !UtilsGps.NO_GPS_POINT.equals(UtilsGps.getLongitudeString(temp))) {
                    table.getCellEditor().stopCellEditing();
                    UtilsGps.copyGpsBetweenDOs(sightingWrapper, temp);
                    lblLatitude.setText(UtilsGps.getLatitudeString(sightingWrapper));
                    lblLongitude.setText(UtilsGps.getLongitudeString(sightingWrapper));
                }
            }
        }
    }//GEN-LAST:event_btnGPSMouseReleased

    public void setRowBackground(Color inColor) {
        this.setBackground(inColor);
        btnChooseCreature.setBackground(inColor);
        btnEdit.setBackground(inColor);
        btnGPS.setBackground(inColor);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseCreature;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnGPS;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblElementName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLatitude;
    private javax.swing.JLabel lblLongitude;
    private javax.swing.JLabel lblTime;
    // End of variables declaration//GEN-END:variables
}
