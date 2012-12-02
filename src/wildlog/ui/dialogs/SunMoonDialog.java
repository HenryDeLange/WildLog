package wildlog.ui.dialogs;

import astro.MoonTimes;
import astro.SunTimes;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.mapping.utils.LatLonConverter;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class SunMoonDialog extends JDialog {
    private DataObjectWithGPS dataObjectWithGPS;
    private WildLogApp app;

    /** Creates new form GPSDialog */
    public SunMoonDialog(DataObjectWithGPS inDataObjectWithGPS) {
        super();
        app = (WildLogApp)Application.getInstance();

        // Initialize the auto generated code
        initComponents();

        // Create a new dataObjectWithGPS, or clone the incomming one
        // Moennie die oorspronklikke gebruik nie want ek wil nie die GPS punt laat verander nie
        dataObjectWithGPS = new DataObjectWithGPS() {};
        if (inDataObjectWithGPS != null) {
            dataObjectWithGPS.setLatitude(inDataObjectWithGPS.getLatitude());
            dataObjectWithGPS.setLatDegrees(inDataObjectWithGPS.getLatDegrees());
            dataObjectWithGPS.setLatMinutes(inDataObjectWithGPS.getLatMinutes());
            dataObjectWithGPS.setLatSeconds(inDataObjectWithGPS.getLatSeconds());
            dataObjectWithGPS.setLongitude(inDataObjectWithGPS.getLongitude());
            dataObjectWithGPS.setLonDegrees(inDataObjectWithGPS.getLonDegrees());
            dataObjectWithGPS.setLonMinutes(inDataObjectWithGPS.getLonMinutes());
            dataObjectWithGPS.setLonSeconds(inDataObjectWithGPS.getLonSeconds());
        }
        // Populate the initial UI values
        txtLatitude.setText(LatLonConverter.getLatitudeString(dataObjectWithGPS));
        txtLongitude.setText(LatLonConverter.getLongitudeString(dataObjectWithGPS));
        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!LatLonConverter.NO_GPS_POINT.equals(txtLatitude.getText())
            && !LatLonConverter.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }

        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        ActionListener escListener = UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Hack to fix the wierd focus issue to get the ESC to work (related to the datepicker)
        this.setFocusable(true);
        dtpDate.getEditor().registerKeyboardAction(
                escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
    }

    private void populateUI() {
        if (!LatLonConverter.NO_GPS_POINT.equals(txtLatitude.getText())
                && !LatLonConverter.NO_GPS_POINT.equals(txtLongitude.getText())) {
            double lat = LatLonConverter.getLatDecimalDegree(dataObjectWithGPS);
            double lon = LatLonConverter.getLonDecimalDegree(dataObjectWithGPS);
            if (dtpDate.getDate() != null) {
                // Sun
                lblSunlight.setText(AstroCalculator.getSunCategory(dtpDate.getDate(), lat, lon).toString());
                lblDawn.setText(SunTimes.getDawn(dtpDate.getDate(), lat, lon));
                lblSunrise.setText(SunTimes.getSunrise(dtpDate.getDate(), lat, lon));
                lblSunset.setText(SunTimes.getSunset(dtpDate.getDate(), lat, lon));
                lblDusk.setText(SunTimes.getDusk(dtpDate.getDate(), lat, lon));
                // Moon
                lblMoonPhase.setText(AstroCalculator.getMoonPhase(dtpDate.getDate()) + " % Full ");
                lblMoonlight.setText(AstroCalculator.getMoonlight(dtpDate.getDate(), lat, lon).toString());
                lblMoonrise.setText(MoonTimes.getMoonrise(dtpDate.getDate(), lat, lon));
                lblMoonset.setText(MoonTimes.getMoonset(dtpDate.getDate(), lat, lon));
            }
            else {
                getGlassPane().setVisible(true);
                JOptionPane.showMessageDialog(app.getMainFrame(), "Please select a valid date.", "Date Error", JOptionPane.ERROR_MESSAGE);
                getGlassPane().setVisible(false);
            }
        }
        else {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(app.getMainFrame(), "Please select a valid GPS point.", "GPS Error", JOptionPane.ERROR_MESSAGE);
            getGlassPane().setVisible(false);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dtpDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel10 = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        btnGPS = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblSunlight = new javax.swing.JLabel();
        lblDawn = new javax.swing.JLabel();
        lblSunrise = new javax.swing.JLabel();
        lblSunset = new javax.swing.JLabel();
        lblDusk = new javax.swing.JLabel();
        lblMoonlight = new javax.swing.JLabel();
        lblMoonPhase = new javax.swing.JLabel();
        lblMoonrise = new javax.swing.JLabel();
        lblMoonset = new javax.swing.JLabel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(SunMoonDialog.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/SunAndMoon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(295, 300));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        dtpDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpDate.setDate(Calendar.getInstance().getTime());
        dtpDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpDate.setName("dtpDate"); // NOI18N
        dtpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtpDateActionPerformed(evt);
            }
        });
        getContentPane().add(dtpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 135, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        txtLatitude.setBackground(resourceMap.getColor("txtLatitude.background")); // NOI18N
        txtLatitude.setDisabledTextColor(resourceMap.getColor("txtLatitude.disabledTextColor")); // NOI18N
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N
        getContentPane().add(txtLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 135, -1));

        txtLongitude.setBackground(resourceMap.getColor("txtLongitude.background")); // NOI18N
        txtLongitude.setDisabledTextColor(resourceMap.getColor("txtLongitude.disabledTextColor")); // NOI18N
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N
        getContentPane().add(txtLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 135, -1));

        btnRefresh.setIcon(resourceMap.getIcon("btnRefresh.icon")); // NOI18N
        btnRefresh.setText(resourceMap.getString("btnRefresh.text")); // NOI18N
        btnRefresh.setFocusPainted(false);
        btnRefresh.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefresh.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnRefresh.setName("btnRefresh"); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 100, -1));

        btnGPS.setBackground(resourceMap.getColor("btnGPS.background")); // NOI18N
        btnGPS.setIcon(resourceMap.getIcon("btnGPS.icon")); // NOI18N
        btnGPS.setText(resourceMap.getString("btnGPS.text")); // NOI18N
        btnGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPS.setFocusPainted(false);
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });
        getContentPane().add(btnGPS, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 100, 40));

        jSeparator1.setName("jSeparator1"); // NOI18N
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 270, 10));

        jSeparator2.setName("jSeparator2"); // NOI18N
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 270, 10));

        jSeparator3.setName("jSeparator3"); // NOI18N
        getContentPane().add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 270, 10));

        jLabel4.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel4.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel5.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel7.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jLabel11.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel11.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel12.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, -1, -1));

        jLabel13.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel13.setForeground(resourceMap.getColor("jLabel9.foreground")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        lblSunlight.setText(resourceMap.getString("lblSunlight.text")); // NOI18N
        lblSunlight.setName("lblSunlight"); // NOI18N
        getContentPane().add(lblSunlight, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, -1, -1));

        lblDawn.setText(resourceMap.getString("lblDawn.text")); // NOI18N
        lblDawn.setName("lblDawn"); // NOI18N
        getContentPane().add(lblDawn, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, -1, -1));

        lblSunrise.setText(resourceMap.getString("lblSunrise.text")); // NOI18N
        lblSunrise.setName("lblSunrise"); // NOI18N
        getContentPane().add(lblSunrise, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, -1, -1));

        lblSunset.setText(resourceMap.getString("lblSunset.text")); // NOI18N
        lblSunset.setName("lblSunset"); // NOI18N
        getContentPane().add(lblSunset, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 160, -1, -1));

        lblDusk.setText(resourceMap.getString("lblDusk.text")); // NOI18N
        lblDusk.setName("lblDusk"); // NOI18N
        getContentPane().add(lblDusk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, -1, -1));

        lblMoonlight.setText(resourceMap.getString("lblMoonlight.text")); // NOI18N
        lblMoonlight.setName("lblMoonlight"); // NOI18N
        getContentPane().add(lblMoonlight, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, -1, -1));

        lblMoonPhase.setText(resourceMap.getString("lblMoonPhase.text")); // NOI18N
        lblMoonPhase.setName("lblMoonPhase"); // NOI18N
        getContentPane().add(lblMoonPhase, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, -1, -1));

        lblMoonrise.setText(resourceMap.getString("lblMoonrise.text")); // NOI18N
        lblMoonrise.setName("lblMoonrise"); // NOI18N
        getContentPane().add(lblMoonrise, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 250, -1, -1));

        lblMoonset.setText(resourceMap.getString("lblMoonset.text")); // NOI18N
        lblMoonset.setName("lblMoonset"); // NOI18N
        getContentPane().add(lblMoonset, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 270, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        populateUI();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(this, dataObjectWithGPS);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            txtLatitude.setText(LatLonConverter.getLatitudeString(dataObjectWithGPS));
            txtLongitude.setText(LatLonConverter.getLongitudeString(dataObjectWithGPS));
            populateUI();
        }
        btnRefresh.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void dtpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dtpDateActionPerformed
        populateUI();
    }//GEN-LAST:event_dtpDateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnRefresh;
    private org.jdesktop.swingx.JXDatePicker dtpDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblDawn;
    private javax.swing.JLabel lblDusk;
    private javax.swing.JLabel lblMoonPhase;
    private javax.swing.JLabel lblMoonlight;
    private javax.swing.JLabel lblMoonrise;
    private javax.swing.JLabel lblMoonset;
    private javax.swing.JLabel lblSunlight;
    private javax.swing.JLabel lblSunrise;
    private javax.swing.JLabel lblSunset;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    // End of variables declaration//GEN-END:variables
}
