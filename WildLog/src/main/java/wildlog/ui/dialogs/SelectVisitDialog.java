package wildlog.ui.dialogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
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
import wildlog.data.enums.VisitType;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class SelectVisitDialog extends JDialog {
    private WildLogApp app;
    private boolean selectionMade = false;
    private long selectedVisitID;
    private String selectedVisitName;
    private List<VisitType> lstVisitTypes;


    public SelectVisitDialog(JFrame inParent, WildLogApp inApp, final long inLocationID, final long inVisitID, List<VisitType> inLstVisitTypes) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[SelectVisitDialog]");
        app = inApp;
        initComponents();
        lstVisitTypes = inLstVisitTypes;
        // Setup the escape key
        final SelectVisitDialog thisHandler = this;
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
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);
        // Setup the table
        UtilsTableGenerator.setupVisitTableSmallWithType(app, tblVisit, inLocationID, true, lstVisitTypes);
        // Load selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int t = 0; t < tblVisit.getRowCount(); t++) {
                    if ((long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(t), 4) == inVisitID) {
                        tblVisit.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblVisit.getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblVisit.scrollRectToVisible(tblVisit.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
            }
        });
        UtilsImageProcessing.setupFoto(inVisitID, 0, lblImageVisit, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
        // Pack and set it as the min size
        pack();
        setMinimumSize(getSize());
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
        lblImageVisit = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select a Period");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Visit.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(230, 228, 240));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Choose a Period:");
        jLabel2.setName("jLabel2"); // NOI18N

        lblImageVisit.setBackground(new java.awt.Color(0, 0, 0));
        lblImageVisit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageVisit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImageVisit.setName("lblImageVisit"); // NOI18N
        lblImageVisit.setOpaque(true);
        lblImageVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageVisitMouseReleased(evt);
            }
        });

        btnSelect.setBackground(new java.awt.Color(230, 237, 220));
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSelect.setToolTipText("Confirm the selected Period.");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblVisitKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblVisit);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblImageVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblImageVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public long getSelectedVisitID() {
        return selectedVisitID;
    }
    
    protected String getSelectedVisitName() {
        return selectedVisitName;
    }

    protected Icon getSelectedVisitIcon() {
        return lblImageVisit.getIcon();
    }

    private void lblImageVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageVisitMouseReleased
        if (!tblVisit.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile((long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 4), 0, app);
        }
    }//GEN-LAST:event_lblImageVisitMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (tblVisit.getSelectedRowCount() == 1) {
            selectionMade = true;
            selectedVisitID = (long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 4);
            selectedVisitName = (String) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 1);
            tblVisit.setBorder(null);
            setVisible(false);
            dispose();
        }
        else {
            tblVisit.setBorder(new LineBorder(Color.RED, 2));
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (!tblVisit.getSelectionModel().isSelectionEmpty()) {
            long selectedID = (long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(tblVisit.getSelectedRow()), 4);
            // Change the image
            UtilsImageProcessing.setupFoto(selectedID, 0, lblImageVisit, WildLogThumbnailSizes.S0150_MEDIUM_SMALL, app);
        }
        else {
            lblImageVisit.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0150_MEDIUM_SMALL));
        }
    }//GEN-LAST:event_tblVisitMouseReleased

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblVisitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyReleased
        tblVisitMouseReleased(null);
    }//GEN-LAST:event_tblVisitKeyReleased

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
    private javax.swing.JLabel lblImageVisit;
    private javax.swing.JTable tblVisit;
    // End of variables declaration//GEN-END:variables
}
