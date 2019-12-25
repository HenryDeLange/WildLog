package wildlog.ui.dialogs;

import javax.swing.ImageIcon;
import org.apache.logging.log4j.Level;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.bulkupload.LocationSelectionDialog;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;

public class WelcomeDialog extends JDialog {

    public WelcomeDialog() {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WelcomeDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel lblTitle = new javax.swing.JLabel();
        btnBulkImport = new javax.swing.JButton();
        btnStash = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Getting Started");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/wei/WEI-square-20px.png")).getImage());
        setMinimumSize(new java.awt.Dimension(800, 250));
        setModal(true);
        setName("aboutBox"); // NOI18N

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Getting Started");
        lblTitle.setToolTipText("");
        lblTitle.setName("lblTitle"); // NOI18N

        btnBulkImport.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBulkImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Process.png"))); // NOI18N
        btnBulkImport.setText("<html>Process new files by starting a new <b>Bulk Import</b> process");
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });

        btnStash.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/StashButton.png"))); // NOI18N
        btnStash.setText("<html>Quickly <b>Stash</b> the files to be processed later</html>");
        btnStash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStash.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStash.setName("btnStash"); // NOI18N
        btnStash.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStashActionPerformed(evt);
            }
        });

        btnBrowse.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Explore.png"))); // NOI18N
        btnBrowse.setText("<html>Be free to explore WildLog on your own</html>");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnStash, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(btnBulkImport, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTitle)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBulkImport)
                    .addComponent(btnBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addComponent(btnStash))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStashActionPerformed
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UtilsFileProcessing.doStashFiles();
            }
        });
    }//GEN-LAST:event_btnStashActionPerformed

    private void btnBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkImportActionPerformed
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LocationSelectionDialog locationDialog = new LocationSelectionDialog(WildLogApp.getApplication().getMainFrame(), WildLogApp.getApplication(), 0);
                locationDialog.setVisible(true);
                if (locationDialog.isSelectionMade()) {
                    UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                        @Override
                        protected Object doInBackground() throws Exception {
                            UtilsPanelGenerator.openBulkUploadTab(new BulkUploadPanel(WildLogApp.getApplication(), this, 
                                    WildLogApp.getApplication().getDBI().findLocation(locationDialog.getSelectedLocationID(), null, false, Location.class), 
                                    null, null, null), WildLogApp.getApplication().getMainFrame().getTabbedPane());
                            return null;
                        }
                    });
                }
            }
        });
    }//GEN-LAST:event_btnBulkImportActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        setVisible(false);
        dispose();
        WildLogApp.getApplication().getMainFrame().getTabbedPane().setSelectedIndex(4);
    }//GEN-LAST:event_btnBrowseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnStash;
    // End of variables declaration//GEN-END:variables
}
