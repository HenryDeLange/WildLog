package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import org.apache.logging.log4j.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import wildlog.WildLogApp;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class LocationSelectionDialog extends JDialog {
    private WildLogApp app;
    private boolean selectionMade = false;
    private long selectedLocationID;
    private String selectedLocationName;


    public LocationSelectionDialog(JFrame inParent, WildLogApp inApp, final long inSelectedLocationID) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[LocationSelectionDialog]");
        app = inApp;
        initComponents();
        // Setup the escape key
        final LocationSelectionDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Position the dialog
        UtilsDialog.setDialogToCenter(app.getMainFrame(), thisHandler);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), thisHandler);
        // Attach listeners etc.
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        // Setup the table
        UtilsTableGenerator.setupLocationTableSmall(app, tblLocation, null);
        // Load selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int t = 0; t < tblLocation.getRowCount(); t++) {
                    if ((long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(t), 2) == inSelectedLocationID) {
                        tblLocation.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblLocation.getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblLocation.scrollRectToVisible(tblLocation.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
            }
        });
        UtilsImageProcessing.setupFoto(inSelectedLocationID, 0, lblImageLocation, WildLogThumbnailSizes.MEDIUM_SMALL, app);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblImageLocation = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select a Place");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Location.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(233, 239, 244));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Please choose a Place:");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblImageLocation.setBackground(new java.awt.Color(0, 0, 0));
        lblImageLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImageLocation.setName("lblImageLocation"); // NOI18N
        lblImageLocation.setOpaque(true);
        lblImageLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageLocationMouseReleased(evt);
            }
        });
        jPanel1.add(lblImageLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 150, 150));

        btnSelect.setBackground(new java.awt.Color(230, 237, 220));
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSelect.setToolTipText("Confirm the selected Place.");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jPanel1.add(btnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 150, 70));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocationMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblLocation);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 340, 530));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public long getSelectedLocationID() {
        return selectedLocationID;
    }
    
    protected String getSelectedLocationName() {
        return selectedLocationName;
    }

    protected Icon getSelectedLocationIcon() {
        return lblImageLocation.getIcon();
    }

    private void lblImageLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageLocationMouseReleased
        if (!tblLocation.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile((long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 2), 0, app);
        }
    }//GEN-LAST:event_lblImageLocationMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (tblLocation.getSelectedRowCount() == 1) {
            selectionMade = true;
            selectedLocationID = (long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 2);
            selectedLocationName = (String) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 1);
            tblLocation.setBorder(null);
            setVisible(false);
            dispose();
        }
        else {
            tblLocation.setBorder(new LineBorder(Color.RED, 2));
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (!tblLocation.getSelectionModel().isSelectionEmpty()) {
            long selectedID = (long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 2);
            // Change the image
            UtilsImageProcessing.setupFoto(selectedID, 0, lblImageLocation, WildLogThumbnailSizes.MEDIUM_SMALL, app);
        }
        else {
            lblImageLocation.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
        }
    }//GEN-LAST:event_tblLocationMouseReleased

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationKeyPressed

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        tblLocationMouseReleased(null);
    }//GEN-LAST:event_tblLocationKeyReleased

    public boolean isSelectionMade() {
        return selectionMade;
    }

    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImageLocation;
    private javax.swing.JTable tblLocation;
    // End of variables declaration//GEN-END:variables
}
