package wildlog.ui.dialogs;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.panels.PanelVisit;

public class AdvancedDialog extends JDialog {
    private WildLogApp app;
    private Visit visit;
    private PanelVisit panelVisit;

    public AdvancedDialog(WildLogApp inApp, Visit inVisit, PanelVisit inPanelVisit) {
        super(inApp.getMainFrame());
        // Set passed in values
        app = inApp;
        visit = inVisit;
        panelVisit = inPanelVisit;
        // Auto generated code
        initComponents();
        // Pack
        pack();
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCorrectTime = new javax.swing.JButton();
        btnMoveVisit = new javax.swing.JButton();
        btnCorrectTime1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Available Maps");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Map_Small.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnCorrectTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnCorrectTime.setText("Correct Date and Time");
        btnCorrectTime.setToolTipText("Automatically adjust the time of all Observations by the specified amount.");
        btnCorrectTime.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCorrectTime.setFocusPainted(false);
        btnCorrectTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCorrectTime.setIconTextGap(10);
        btnCorrectTime.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnCorrectTime.setMaximumSize(new java.awt.Dimension(250, 35));
        btnCorrectTime.setMinimumSize(new java.awt.Dimension(250, 35));
        btnCorrectTime.setName("btnCorrectTime"); // NOI18N
        btnCorrectTime.setPreferredSize(new java.awt.Dimension(250, 35));
        btnCorrectTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorrectTimeActionPerformed(evt);
            }
        });
        getContentPane().add(btnCorrectTime);

        btnMoveVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnMoveVisit.setText("Move Period to Different Place");
        btnMoveVisit.setToolTipText("Move this Period to a different Place.");
        btnMoveVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMoveVisit.setFocusPainted(false);
        btnMoveVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMoveVisit.setIconTextGap(10);
        btnMoveVisit.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnMoveVisit.setMaximumSize(new java.awt.Dimension(250, 35));
        btnMoveVisit.setMinimumSize(new java.awt.Dimension(250, 35));
        btnMoveVisit.setName("btnMoveVisit"); // NOI18N
        btnMoveVisit.setPreferredSize(new java.awt.Dimension(250, 35));
        btnMoveVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveVisitActionPerformed(evt);
            }
        });
        getContentPane().add(btnMoveVisit);

        btnCorrectTime1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnCorrectTime1.setText("Set the GPS position for all Observations");
        btnCorrectTime1.setToolTipText("");
        btnCorrectTime1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCorrectTime1.setFocusPainted(false);
        btnCorrectTime1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCorrectTime1.setIconTextGap(10);
        btnCorrectTime1.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnCorrectTime1.setMaximumSize(new java.awt.Dimension(250, 35));
        btnCorrectTime1.setMinimumSize(new java.awt.Dimension(250, 35));
        btnCorrectTime1.setName("btnCorrectTime1"); // NOI18N
        btnCorrectTime1.setPreferredSize(new java.awt.Dimension(250, 35));
        btnCorrectTime1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorrectTime1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnCorrectTime1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCorrectTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorrectTimeActionPerformed

    }//GEN-LAST:event_btnCorrectTimeActionPerformed

    private void btnMoveVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveVisitActionPerformed
        getGlassPane().setVisible(true);
        int result = JOptionPane.showConfirmDialog(this,
                        "<html>It is strongly recommended that you backup your data (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            MoveVisitDialog dialog = new MoveVisitDialog(app, visit);
            dialog.setVisible(true);
        }
        panelVisit.setVisit(app.getDBI().find(visit));
        panelVisit.setLocationForVisit(app.getDBI().find(new Location(panelVisit.getVisit().getLocationName())));
        panelVisit.doTheRefresh(null);
        getGlassPane().setVisible(false);
        dispose();
    }//GEN-LAST:event_btnMoveVisitActionPerformed

    private void btnCorrectTime1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorrectTime1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCorrectTime1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCorrectTime;
    private javax.swing.JButton btnCorrectTime1;
    private javax.swing.JButton btnMoveVisit;
    // End of variables declaration//GEN-END:variables
}
