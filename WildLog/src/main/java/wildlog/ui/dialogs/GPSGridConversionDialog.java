package wildlog.ui.dialogs;

import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;
import wildlog.WildLogApp;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.utils.UtilsUI;

public class GPSGridConversionDialog extends JFrame {

    
    public GPSGridConversionDialog() {
        initComponents();
        // Setup components
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        SpinnerFixer.configureSpinners(spnLatDecimal);
        SpinnerFixer.configureSpinners(spnLonDecimal);
        UtilsUI.attachClipboardPopup((JTextComponent)spnLatDecimal.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnLonDecimal.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup(txtPentad);
        UtilsUI.attachClipboardPopup(txtQDS);
        ((JFormattedTextField)spnLatDecimal.getEditor().getComponent(0)).setHorizontalAlignment(JFormattedTextField.CENTER);
        ((JFormattedTextField)spnLonDecimal.getEditor().getComponent(0)).setHorizontalAlignment(JFormattedTextField.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnFromGPS = new javax.swing.JButton();
        btnFromPentad = new javax.swing.JButton();
        btnFromQDS = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        spnLatDecimal = new javax.swing.JSpinner();
        spnLonDecimal = new javax.swing.JSpinner();
        txtPentad = new javax.swing.JTextField();
        txtQDS = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("GPS To Grid Converter");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/GPS.png")).getImage());

        btnFromGPS.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFromGPS.setText("Calculate using GPS");
        btnFromGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFromGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromGPSActionPerformed(evt);
            }
        });

        btnFromPentad.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFromPentad.setText("Calculate using Pentad");
        btnFromPentad.setToolTipText("A Pentad is a block of 5 minutes latitude and 5 minutes longitude.");
        btnFromPentad.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFromPentad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromPentadActionPerformed(evt);
            }
        });

        btnFromQDS.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFromQDS.setText("Calculate using QDGC");
        btnFromQDS.setToolTipText("A QDS splits each block of 1 degree latitude and 1 degree longitude into 4 quarters.");
        btnFromQDS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFromQDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFromQDSActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("GPS Longitude:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Convert GPS, Pentad and QDS locations from one format to another.");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("GPS Latitude:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Pentad:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("QDGC:");

        spnLatDecimal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        spnLatDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, -90.0d, 90.0d, 0.01d));
        spnLatDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLatDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLatDecimal, "#.############"));

        spnLonDecimal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        spnLonDecimal.setModel(new javax.swing.SpinnerNumberModel(0.0d, -180.0d, 180.0d, 0.01d));
        spnLonDecimal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnLonDecimal.setEditor(new javax.swing.JSpinner.NumberEditor(spnLonDecimal, "#.############"));

        txtPentad.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPentad.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtQDS.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtQDS.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtQDS, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                    .addComponent(spnLatDecimal, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                    .addComponent(spnLonDecimal, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPentad, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnFromGPS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFromPentad, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(btnFromQDS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnFromGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnFromPentad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPentad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnFromQDS, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQDS, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spnLatDecimal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(spnLonDecimal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFromGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromGPSActionPerformed
        try {
            double lat = (double) spnLatDecimal.getValue();
            int latMins = UtilsGPS.getMinutes(lat);
            double lon = (double) spnLonDecimal.getValue();
            int lonMins = UtilsGPS.getMinutes(lon);
            txtPentad.setText(calculatePentad(lat, latMins, lon, lonMins));
            txtQDS.setText(calculateQDS(lat, lon, latMins, lonMins));
        }
        catch (Exception ex) {
            // Ignore the actual exception
            txtPentad.setText("");
            txtQDS.setText("");
        }
    }//GEN-LAST:event_btnFromGPSActionPerformed

    private void btnFromPentadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromPentadActionPerformed
        try {
            double lat = UtilsGPS.getDecimalDegree(Latitudes.NONE, Integer.parseInt(txtPentad.getText().substring(0, 2)), Integer.parseInt(txtPentad.getText().substring(2, 4)) + 2, 30);
            spnLatDecimal.setValue(lat);
            double lon = UtilsGPS.getDecimalDegree(Longitudes.NONE, Integer.parseInt(txtPentad.getText().substring(5, 7)), Integer.parseInt(txtPentad.getText().substring(7, 9)) + 2, 30);
            spnLonDecimal.setValue(lon);
            int latMins = UtilsGPS.getMinutes(lat);
            int lonMins = UtilsGPS.getMinutes(lon);
            txtQDS.setText(calculateQDS(lat, lon, latMins, lonMins));
        }
        catch (Exception ex) {
            // Ignore the actual exception
            spnLatDecimal.setValue(0);
            spnLonDecimal.setValue(0);
            txtQDS.setText("");
        }
    }//GEN-LAST:event_btnFromPentadActionPerformed

    private void btnFromQDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFromQDSActionPerformed
        txtQDS.setText(txtQDS.getText().toUpperCase());
        try {
            double lat = UtilsGPS.getDecimalDegree(Latitudes.NONE, Integer.parseInt(txtQDS.getText().substring(0, 2)),
                getQDSGridLat(txtQDS.getText().substring(4, 5), 30) + getQDSGridLat(txtQDS.getText().substring(5, 6), 15) + 7, 30);
            spnLatDecimal.setValue(lat);
            double lon = UtilsGPS.getDecimalDegree(Longitudes.NONE, Integer.parseInt(txtQDS.getText().substring(2, 4)),
                getQDSGridLon(txtQDS.getText().substring(4, 5), 30) + getQDSGridLon(txtQDS.getText().substring(5, 6), 15) + 7, 30);
            spnLonDecimal.setValue(lon);
            int latMins = UtilsGPS.getMinutes(lat);
            int lonMins = UtilsGPS.getMinutes(lon);
            txtPentad.setText(calculatePentad(lat, latMins, lon, lonMins));
        }
        catch (Exception ex) {
            // Ignore the actual exception
            spnLatDecimal.setValue(0);
            spnLonDecimal.setValue(0);
            txtPentad.setText("");
        }
    }//GEN-LAST:event_btnFromQDSActionPerformed

    private String getQDSGridLetter(int inLatMins, int inLonMins, int inLatRange, int inLonRange) {
        if (inLatMins < inLatRange) {
            if (inLonMins < inLonRange) {
                return "A";
            }
            else {
                return "B";
            }
        }
        else {
            if (inLonMins < inLonRange) {
                return "C";
            }
            else {
                return "D";
            }
        }
    }
    
    private int getQDSGridLat(String inLatLetter, int inLatRange) {
        switch (inLatLetter) {
            case "A":
            case "B":
                return 0;
            case "C":
            case "D":
                return inLatRange;
            default:
                return 0;
        }
    }
    
    private int getQDSGridLon(String inLonLetter, int inLonRange) {
        switch (inLonLetter) {
            case "A":
            case "C":
                return 0;
            case "B":
            case "D":
                return inLonRange;
            default:
                return 0;
        }
    }
    
    private String calculatePentad(double inLat, int inLatMins, double inLon, int inLonMins) {
        // A Pentad is a block of 5 minutes latitude and 5 minutes longitude.
        return padZero(Math.abs((int) inLat)) + padZero(inLatMins - inLatMins % 5) 
                + "_" + padZero(Math.abs((int) inLon)) + padZero(inLonMins - inLonMins % 5);
    }
    
    private String calculateQDS(double lat, double lon, int latMins, int lonMins) {
        // A QDS is a block of 1 degree latitude and 1 degree longitude split into quarters (repeatedly). https://en.wikipedia.org/wiki/QDGC
        String qds = padZero(Math.abs((int) lat)) + padZero(Math.abs((int) lon));
        qds = qds + getQDSGridLetter(latMins, lonMins, 30, 30) + getQDSGridLetter(latMins, lonMins, (latMins / 30) * 30 + 15, (lonMins / 30) * 30 + 15);
        return qds;
    }
    
    private String padZero(int inNumber) {
        if (inNumber < 10) {
            return "0" + inNumber;
        }
        return Integer.toString(inNumber);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFromGPS;
    private javax.swing.JButton btnFromPentad;
    private javax.swing.JButton btnFromQDS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSpinner spnLatDecimal;
    private javax.swing.JSpinner spnLonDecimal;
    private javax.swing.JTextField txtPentad;
    private javax.swing.JTextField txtQDS;
    // End of variables declaration//GEN-END:variables
}
