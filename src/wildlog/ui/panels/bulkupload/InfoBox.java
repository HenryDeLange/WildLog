package wildlog.ui.panels.bulkupload;

import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.dialogs.GPSDialog;
import wildlog.ui.panels.PanelSighting;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.mapping.utils.LatLonConverter;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class InfoBox extends JPanel {
    private static final SimpleDateFormat dateFormater = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
    private WildLogApp app;
    private BulkUploadSightingWrapper sightingWrapper;
    private JTextField txtLocation;
    private JTextField txtVisit;
    private JTable table;

    /** Creates new form InfoBox */
    public InfoBox(WildLogApp inApp, BulkUploadSightingWrapper inBulkUploadSightingWrapper, JTextField inTxtLocation, JTextField inTxtVisit, JTable inTable) {
        app = inApp;
        txtLocation = inTxtLocation;
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
        lblLatitude.setText(Double.toString(
                LatLonConverter.getDecimalDegree(
                    sightingWrapper.getLatitude(),
                    sightingWrapper.getLatDegrees(),
                    sightingWrapper.getLatMinutes(),
                    sightingWrapper.getLatSeconds())));
        lblLongitude.setText(Double.toString(
                LatLonConverter.getDecimalDegree(
                    sightingWrapper.getLongitude(),
                    sightingWrapper.getLonDegrees(),
                    sightingWrapper.getLonMinutes(),
                    sightingWrapper.getLonSeconds())));
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(InfoBox.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setMaximumSize(new java.awt.Dimension(250, 250));
        setMinimumSize(new java.awt.Dimension(250, 250));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(250, 250));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblElementName.setFont(resourceMap.getFont("lblElementName.font")); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementName.setText(resourceMap.getString("lblElementName.text")); // NOI18N
        lblElementName.setName("lblElementName"); // NOI18N
        add(lblElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 230, 20));

        jSeparator2.setName("jSeparator2"); // NOI18N
        add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 27, 230, 10));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        lblDate.setText(resourceMap.getString("lblDate.text")); // NOI18N
        lblDate.setName("lblDate"); // NOI18N
        add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 70, -1));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, -1, -1));

        lblTime.setText(resourceMap.getString("lblTime.text")); // NOI18N
        lblTime.setName("lblTime"); // NOI18N
        add(lblTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 50, -1));

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 53, -1, -1));

        lblLatitude.setText(resourceMap.getString("lblLatitude.text")); // NOI18N
        lblLatitude.setName("lblLatitude"); // NOI18N
        add(lblLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 53, 130, -1));

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        lblLongitude.setText(resourceMap.getString("lblLongitude.text")); // NOI18N
        lblLongitude.setName("lblLongitude"); // NOI18N
        add(lblLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 130, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 47, 230, 10));

        lblImage.setBackground(resourceMap.getColor("lblImage.background")); // NOI18N
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

        btnChooseCreature.setBackground(resourceMap.getColor("btnChooseCreature.background")); // NOI18N
        btnChooseCreature.setIcon(resourceMap.getIcon("btnChooseCreature.icon")); // NOI18N
        btnChooseCreature.setText(resourceMap.getString("btnChooseCreature.text")); // NOI18N
        btnChooseCreature.setToolTipText(resourceMap.getString("btnChooseCreature.toolTipText")); // NOI18N
        btnChooseCreature.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChooseCreature.setFocusPainted(false);
        btnChooseCreature.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChooseCreature.setMargin(new java.awt.Insets(2, 4, 2, 2));
        btnChooseCreature.setName("btnChooseCreature"); // NOI18N
        btnChooseCreature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCreatureActionPerformed(evt);
            }
        });
        add(btnChooseCreature, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 85, 80, 50));

        btnGPS.setBackground(resourceMap.getColor("btnGPS.background")); // NOI18N
        btnGPS.setIcon(resourceMap.getIcon("btnGPS.icon")); // NOI18N
        btnGPS.setText(resourceMap.getString("btnGPS.text")); // NOI18N
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });
        add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 135, 80, 50));

        btnEdit.setBackground(resourceMap.getColor("btnEdit.background")); // NOI18N
        btnEdit.setIcon(resourceMap.getIcon("btnEdit.icon")); // NOI18N
        btnEdit.setText(resourceMap.getString("btnEdit.text")); // NOI18N
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.setFocusPainted(false);
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
        PanelSighting dialog = new PanelSighting(
                app.getMainFrame(), "Edit the Sighting",
                sightingWrapper,
                new Location(txtLocation.getText()),
                new Visit(txtVisit.getText()),
                new Element(sightingWrapper.getElementName()),
                null,
                false, false, true);
        dialog.setVisible(true);
        // Update the UI
        table.getCellEditor().stopCellEditing();
        UtilsImageProcessing.setupFoto("ELEMENT-" + sightingWrapper.getElementName(), 0, lblImage, 150, app);
        sightingWrapper.setIcon(lblImage.getIcon());
        populateUI();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnChooseCreatureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCreatureActionPerformed
        final ElementSelectionBox dialog = new ElementSelectionBox(app.getMainFrame(), true, app, sightingWrapper.getElementName());
        dialog.setVisible(true);
        // Set the label to the selected text
        table.getCellEditor().stopCellEditing();
        if (dialog.isSelectionMade() && dialog.getElementName() != null && dialog.getElementName().length() > 0) {
            sightingWrapper.setElementName(dialog.getElementName());
            sightingWrapper.setIcon(dialog.getElementIcon());
        }
    }//GEN-LAST:event_btnChooseCreatureActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile("ELEMENT-" + sightingWrapper.getElementName(), 0, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app.getMainFrame(), true, sightingWrapper);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lblLatitude.setText(LatLonConverter.getLatitudeString(sightingWrapper));
            lblLongitude.setText(LatLonConverter.getLongitudeString(sightingWrapper));
        }
    }//GEN-LAST:event_btnGPSActionPerformed

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
