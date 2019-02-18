package wildlog.ui.dialogs;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;


public class DateChangeDialog extends JDialog {
    private WildLogApp app;
    private final Visit visit;
    
    
    public DateChangeDialog(WildLogApp inApp, JFrame inParent, Visit inVisit) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[DateChangeDialog]");
        visit = inVisit;
        // Do the setup (this is where the shared setup happens)
        doSetup(inApp);
        // Setup the default behavior (this is for JFrames)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    public DateChangeDialog(WildLogApp inApp, JDialog inParent, Visit inVisit) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[DateChangeDialog]");
        visit = inVisit;
        // Do the setup (this is where the shared setup happens)
        doSetup(inApp);
        // Setup the default behavior (this is for JDialogs)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    private void doSetup(WildLogApp inApp) {
        app = inApp;
        // Need to set a few settings on the content pane before continuing (for example size, background color, etc.)
        getContentPane().setPreferredSize(getPreferredSize());
        // Initialize the auto generated code
        initComponents();
        // Setup the defualt start dates
        LocalDateTime now = LocalDateTime.now();
        dtpBadDate.setDate(UtilsTime.getDateFromLocalDateTime(now));
        dtpGoodDate.setDate(UtilsTime.getDateFromLocalDateTime(now));
        spnBadHours.setValue(now.getHour());
        spnBadMinutes.setValue(now.getMinute());
        spnBadSeconds.setValue(now.getSecond());
        spnGoodHours.setValue(now.getHour());
        spnGoodMinutes.setValue(now.getMinute());
        spnGoodSeconds.setValue(now.getSecond());
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Fix spinners
        SpinnerFixer.configureSpinners(spnBadHours);
        SpinnerFixer.configureSpinners(spnBadMinutes);
        SpinnerFixer.configureSpinners(spnBadSeconds);
        SpinnerFixer.configureSpinners(spnGoodHours);
        SpinnerFixer.configureSpinners(spnGoodMinutes);
        SpinnerFixer.configureSpinners(spnGoodSeconds);
        UtilsUI.attachClipboardPopup((JTextComponent)spnBadHours.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnBadMinutes.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnBadSeconds.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnGoodHours.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnGoodMinutes.getEditor().getComponent(0));
        UtilsUI.attachClipboardPopup((JTextComponent)spnGoodSeconds.getEditor().getComponent(0));
        // Set focus
        btnSave.requestFocusInWindow();
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
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dtpBadDate = new org.jdesktop.swingx.JXDatePicker();
        dtpGoodDate = new org.jdesktop.swingx.JXDatePicker();
        spnBadHours = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        spnBadMinutes = new javax.swing.JSpinner();
        spnBadSeconds = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        spnGoodHours = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        spnGoodMinutes = new javax.swing.JSpinner();
        spnGoodSeconds = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Adjust Date and Time");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif")).getImage());
        setMaximumSize(new java.awt.Dimension(410, 200));
        setMinimumSize(new java.awt.Dimension(410, 200));
        setModal(true);
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(410, 200));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSave.setToolTipText("Perform the date and time change on the Observations.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 110, 60));

        jLabel1.setText("<html><b>Specify an Incorrect Date and Correct Date.</b><br />The time difference between the Incorrect and Correct dates will be applied to Observations linked to the currently active Period.</html>");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 270, 60));

        jSeparator2.setName("jSeparator2"); // NOI18N
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 78, 390, 10));

        jLabel6.setText("Incorrect Date:");
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, 20));

        jLabel7.setText("Correct Date:");
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 20));

        dtpBadDate.setToolTipText("Date");
        dtpBadDate.setFocusable(false);
        dtpBadDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpBadDate.setName("dtpBadDate"); // NOI18N
        getContentPane().add(dtpBadDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 130, -1));

        dtpGoodDate.setToolTipText("Date");
        dtpGoodDate.setFocusable(false);
        dtpGoodDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpGoodDate.setName("dtpGoodDate"); // NOI18N
        getContentPane().add(dtpGoodDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 130, -1));

        spnBadHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnBadHours.setToolTipText("Hours");
        spnBadHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnBadHours.setFocusable(false);
        spnBadHours.setName("spnBadHours"); // NOI18N
        spnBadHours.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnBadHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(":");
        jLabel8.setFocusable(false);
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, -1, -1));

        spnBadMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnBadMinutes.setToolTipText("Minutes");
        spnBadMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnBadMinutes.setFocusable(false);
        spnBadMinutes.setName("spnBadMinutes"); // NOI18N
        spnBadMinutes.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnBadMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, -1, -1));

        spnBadSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnBadSeconds.setToolTipText("Seconds");
        spnBadSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnBadSeconds.setFocusable(false);
        spnBadSeconds.setName("spnBadSeconds"); // NOI18N
        spnBadSeconds.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnBadSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 90, -1, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(":");
        jLabel9.setFocusable(false);
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, -1, -1));

        spnGoodHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spnGoodHours.setToolTipText("Hours");
        spnGoodHours.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnGoodHours.setFocusable(false);
        spnGoodHours.setName("spnGoodHours"); // NOI18N
        spnGoodHours.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnGoodHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, -1, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(":");
        jLabel10.setFocusable(false);
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

        spnGoodMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnGoodMinutes.setToolTipText("Minutes");
        spnGoodMinutes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnGoodMinutes.setFocusable(false);
        spnGoodMinutes.setName("spnGoodMinutes"); // NOI18N
        spnGoodMinutes.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnGoodMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 130, -1, -1));

        spnGoodSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnGoodSeconds.setToolTipText("Seconds");
        spnGoodSeconds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnGoodSeconds.setFocusable(false);
        spnGoodSeconds.setName("spnGoodSeconds"); // NOI18N
        spnGoodSeconds.setPreferredSize(new java.awt.Dimension(35, 20));
        getContentPane().add(spnGoodSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, -1, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText(":");
        jLabel11.setFocusable(false);
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (dtpBadDate.getDate() == null || dtpGoodDate.getDate() == null) {
            WLOptionPane.showConfirmDialog(this,
                    "Please make sure to provide the necessary date and time information.",
                    "Provide Adjustment Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        }
        else {
            // Kry die verskil tussen die twee datums waarvolgens die Sightings geupdate moet word
            TemporalAmount dateDifference = Duration.between(loadBadDateTimeFromUI(), loadGoodDateTimeFromUI());
            // Update the Sightings
            List<Sighting> listSightings = app.getDBI().listSightings(0, null, null, visit.getName(), false, Sighting.class);
            for (Sighting sighting : listSightings) {
                LocalDateTime currentSightingDateTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
                LocalDateTime newSightingDateTime = currentSightingDateTime.plus(dateDifference);
                sighting.setDate(UtilsTime.getDateFromLocalDateTime(newSightingDateTime));
                // Because the sighting's date changed I need to recalculate the Sun and Moon phase
                UtilsTime.calculateSunAndMoon(sighting);
                // Save the changes
                app.getDBI().updateSighting(sighting);
            }
            // We are done, dispose this dialog
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private LocalDateTime loadBadDateTimeFromUI() {
        return UtilsTime.getLocalDateTimeFromDate(dtpBadDate.getDate())
                .withHour((int) spnBadHours.getValue())
                .withMinute((int) spnBadMinutes.getValue())
                .withSecond((int) spnBadSeconds.getValue());
    }
    
    private LocalDateTime loadGoodDateTimeFromUI() {
        return UtilsTime.getLocalDateTimeFromDate(dtpGoodDate.getDate())
                .withHour((int) spnGoodHours.getValue())
                .withMinute((int) spnGoodMinutes.getValue())
                .withSecond((int) spnGoodSeconds.getValue());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnSave;
    org.jdesktop.swingx.JXDatePicker dtpBadDate;
    org.jdesktop.swingx.JXDatePicker dtpGoodDate;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel10;
    javax.swing.JLabel jLabel11;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JLabel jLabel8;
    javax.swing.JLabel jLabel9;
    javax.swing.JSeparator jSeparator2;
    javax.swing.JSpinner spnBadHours;
    javax.swing.JSpinner spnBadMinutes;
    javax.swing.JSpinner spnBadSeconds;
    javax.swing.JSpinner spnGoodHours;
    javax.swing.JSpinner spnGoodMinutes;
    javax.swing.JSpinner spnGoodSeconds;
    // End of variables declaration//GEN-END:variables
}