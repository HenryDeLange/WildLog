package wildlog.ui.dialogs;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.astro.MoonTimes;
import wildlog.astro.SunTimes;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.TimeFormat;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;


public class SunMoonDialog extends JDialog {
    private final DataObjectWithGPS dataObjectWithGPS;
    private final WildLogApp app;
    private Date date;
    private TimeFormat prevTimeFormat;

    /** Creates new form GPSDialog */
    public SunMoonDialog(WildLogApp inApp, DataObjectWithGPS inDataObjectWithGPS) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[SunMoonDialog]");
        app = inApp;
        // Initialize the auto generated code
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbTimeFormat);
        // Create a new dataObjectWithGPS, or clone the incomming one
        // Moennie die oorspronklikke gebruik nie want ek wil nie die GPS punt laat verander nie
        dataObjectWithGPS = new DataObjectWithGPS() {};
        if (inDataObjectWithGPS != null) {
            UtilsGPS.copyGpsBetweenDOs(dataObjectWithGPS, inDataObjectWithGPS);
        }
        // Populate the initial UI values
        txtLatitude.setText(UtilsGPS.getLatitudeString(dataObjectWithGPS));
        txtLongitude.setText(UtilsGPS.getLongitudeString(dataObjectWithGPS));

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
        // Make date pretty
        dtpDate.getComponent(1).setBackground(this.getBackground());
        // Spinners stuff
        SpinnerFixer.configureSpinners(spnHours);
        SpinnerFixer.configureSpinners(spnMinutes);
        // Attach clipboard
        UtilsUI.attachClipboardPopup((JTextComponent)spnHours.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnMinutes.getEditor().getComponent(0));

        // Setup the initial date
        date = new Date(Calendar.getInstance().getTimeInMillis());
        dtpDate.setDate(date);
        spnHours.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        spnMinutes.setValue(Calendar.getInstance().get(Calendar.MINUTE));
        updateTimeFromUI();

        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
            && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }
    }

    private void populateUI() {
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
                && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            double lat = UtilsGPS.getLatDecimalDegree(dataObjectWithGPS);
            double lon = UtilsGPS.getLonDecimalDegree(dataObjectWithGPS);
            if (date != null) {
                // Sun
                lblSunlight.setText(AstroCalculator.getSunCategory(date, lat, lon).toString());
                lblDawn.setText(SunTimes.getDawn(date, lat, lon));
                lblSunrise.setText(SunTimes.getSunrise(date, lat, lon));
                lblSunset.setText(SunTimes.getSunset(date, lat, lon));
                lblDusk.setText(SunTimes.getDusk(date, lat, lon));
                // Moon
                lblMoonPhase.setText(AstroCalculator.getMoonPhase(date) + " % Full ");
                lblMoonlight.setText(AstroCalculator.getMoonlight(date, lat, lon).toString());
                lblMoonrise.setText(MoonTimes.getMoonrise(date, lat, lon));
                lblMoonset.setText(MoonTimes.getMoonset(date, lat, lon));
            }
            else {
                WLOptionPane.showMessageDialog(this, "Please select a valid date.", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            WLOptionPane.showMessageDialog(this, "Please select a valid GPS point.", "GPS Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel1 = new javax.swing.JLabel();
        spnHours = new javax.swing.JSpinner();
        spnMinutes = new javax.swing.JSpinner();
        cmbTimeFormat = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("View Sun and Moon Phase");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/SunAndMoon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(295, 300));
        setModal(true);
        setName("Form"); // NOI18N

        jLabel3.setText("Date:");
        jLabel3.setName("jLabel3"); // NOI18N

        dtpDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpDate.setName("dtpDate"); // NOI18N
        dtpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtpDateActionPerformed(evt);
            }
        });

        jLabel10.setText("GPS:");
        jLabel10.setName("jLabel10"); // NOI18N

        txtLatitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLatitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLatitude.setEnabled(false);
        txtLatitude.setName("txtLatitude"); // NOI18N

        txtLongitude.setBackground(new java.awt.Color(212, 222, 230));
        txtLongitude.setDisabledTextColor(new java.awt.Color(23, 23, 23));
        txtLongitude.setEnabled(false);
        txtLongitude.setName("txtLongitude"); // NOI18N

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setToolTipText("Recalculate the Sun and Moon Phase based on the specified values.");
        btnRefresh.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefresh.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnRefresh.setName("btnRefresh"); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnGPS.setBackground(new java.awt.Color(233, 239, 244));
        btnGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnGPS.setText("Change GPS");
        btnGPS.setToolTipText("Select a GPS location to use for the calculation.");
        btnGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGPS.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnGPS.setName("btnGPS"); // NOI18N
        btnGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPSActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 51, 102));
        jLabel4.setText("Moon Phase:");
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 102));
        jLabel5.setText("Moonlight:");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 102));
        jLabel6.setText("Moonrise:");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 102));
        jLabel7.setText("Moonset:");
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(156, 78, 0));
        jLabel8.setText("Sunlight:");
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(156, 78, 0));
        jLabel9.setText("Dawn:");
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(156, 78, 0));
        jLabel11.setText("Sunrise:");
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(156, 78, 0));
        jLabel12.setText("Sunset:");
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(156, 78, 0));
        jLabel13.setText("Dusk:");
        jLabel13.setName("jLabel13"); // NOI18N

        lblSunlight.setText("...");
        lblSunlight.setName("lblSunlight"); // NOI18N

        lblDawn.setText("...");
        lblDawn.setName("lblDawn"); // NOI18N

        lblSunrise.setText("...");
        lblSunrise.setName("lblSunrise"); // NOI18N

        lblSunset.setText("...");
        lblSunset.setName("lblSunset"); // NOI18N

        lblDusk.setText("...");
        lblDusk.setName("lblDusk"); // NOI18N

        lblMoonlight.setText("...");
        lblMoonlight.setName("lblMoonlight"); // NOI18N

        lblMoonPhase.setText("...");
        lblMoonPhase.setName("lblMoonPhase"); // NOI18N

        lblMoonrise.setText("...");
        lblMoonrise.setName("lblMoonrise"); // NOI18N

        lblMoonset.setText("...");
        lblMoonset.setName("lblMoonset"); // NOI18N

        jLabel1.setText("Time:");
        jLabel1.setName("jLabel1"); // NOI18N

        spnHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnHours.setEditor(new javax.swing.JSpinner.NumberEditor(spnHours, "0"));
        spnHours.setName("spnHours"); // NOI18N
        spnHours.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnHoursStateChanged(evt);
            }
        });

        spnMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnMinutes.setEditor(new javax.swing.JSpinner.NumberEditor(spnMinutes, "0"));
        spnMinutes.setName("spnMinutes"); // NOI18N
        spnMinutes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMinutesStateChanged(evt);
            }
        });

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        cmbTimeFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTimeFormatActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(":");
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(8, 8, 8)
                        .addComponent(dtpDate, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(9, 9, 9)
                        .addComponent(spnHours, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(cmbTimeFormat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLatitude)
                            .addComponent(txtLongitude))
                        .addGap(5, 5, 5)
                        .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(14, 14, 14)
                        .addComponent(lblSunlight))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(31, 31, 31)
                        .addComponent(lblDawn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(21, 21, 21)
                        .addComponent(lblSunrise))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(23, 23, 23)
                        .addComponent(lblSunset))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(36, 36, 36)
                        .addComponent(lblDusk))
                    .addComponent(jSeparator3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(33, 33, 33)
                        .addComponent(lblMoonlight))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(21, 21, 21)
                        .addComponent(lblMoonPhase))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(40, 40, 40)
                        .addComponent(lblMoonrise))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(42, 42, 42)
                        .addComponent(lblMoonset))
                    .addComponent(jSeparator1))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dtpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(lblSunlight))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(lblDawn))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(lblSunrise))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(lblSunset))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(lblDusk))
                .addGap(5, 5, 5)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(lblMoonlight))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(lblMoonPhase))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(lblMoonrise))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(lblMoonset))
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        populateUI();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPSActionPerformed
        GPSDialog dialog = new GPSDialog(app, this, dataObjectWithGPS);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txtLatitude.setText(UtilsGPS.getLatitudeString(dataObjectWithGPS));
                    txtLongitude.setText(UtilsGPS.getLongitudeString(dataObjectWithGPS));
                    populateUI();
                }
            });
        }
        btnRefresh.requestFocus();
    }//GEN-LAST:event_btnGPSActionPerformed

    private void dtpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dtpDateActionPerformed
        date = dtpDate.getDate();
        updateTimeFromUI();
        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
            && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }
    }//GEN-LAST:event_dtpDateActionPerformed

    private void spnHoursStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnHoursStateChanged
        updateTimeFromUI();
        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
            && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }
    }//GEN-LAST:event_spnHoursStateChanged

    private void spnMinutesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMinutesStateChanged
        updateTimeFromUI();
        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
            && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }
    }//GEN-LAST:event_spnMinutesStateChanged

    private void cmbTimeFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTimeFormatActionPerformed
        UtilsTime.modeChanged(spnHours, spnMinutes, null, cmbTimeFormat, prevTimeFormat);
        prevTimeFormat = (TimeFormat) cmbTimeFormat.getSelectedItem();
        updateTimeFromUI();
        // If a GPS is provided then do the loading, otherwise skip it to avoid the error
        if (!UtilsGPS.NO_GPS_POINT.equals(txtLatitude.getText())
            && !UtilsGPS.NO_GPS_POINT.equals(txtLongitude.getText())) {
            populateUI();
        }
    }//GEN-LAST:event_cmbTimeFormatActionPerformed

    private void updateTimeFromUI() {
        if (date != null) {
            date = UtilsTime.getDateFromUI(spnHours, spnMinutes, null, cmbTimeFormat, date);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGPS;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox cmbTimeFormat;
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
    private javax.swing.JSpinner spnHours;
    private javax.swing.JSpinner spnMinutes;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    // End of variables declaration//GEN-END:variables
}
