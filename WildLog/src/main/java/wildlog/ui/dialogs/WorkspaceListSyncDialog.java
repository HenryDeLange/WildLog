package wildlog.ui.dialogs;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.encryption.TokenEncryptor;
import wildlog.sync.azure.SyncAzure;
import wildlog.sync.azure.dataobjects.SyncTableEntry;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.utils.UtilsUI;


public class WorkspaceListSyncDialog extends JDialog {
    private long workspaceID = 0;


    public WorkspaceListSyncDialog(JDialog inParent) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceListSyncDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Attache copy-paste right-clicks
        UtilsUI.attachClipboardPopup(txaSyncToken);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        pnlSyncToken = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaSyncToken = new javax.swing.JTextArea();
        btnConfirmSyncToken = new javax.swing.JButton();
        pnlSyncOptions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstWorkspaces = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List Synced Cloud Workspaces");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Sync.png")).getImage());
        setMinimumSize(new java.awt.Dimension(850, 510));
        setModal(true);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("List Synced Cloud Workspaces");

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnConfirm.setToolTipText("Sync the active Workspace with the data stored in the cloud.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setEnabled(false);
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        pnlSyncToken.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Token", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("<html><b>Please provide your <i>WildLog Cloud Sync Token</i> to enable WildLog to find previously synchronise workspaces.</b> <br/><br/> If you don't have a <i>WildLog Cloud Sync Token</i> then please contact <u>support@mywild.co.za</> for a custom quotation.</html>");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Sync Token:");

        txaSyncToken.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txaSyncToken.setLineWrap(true);
        txaSyncToken.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaSyncToken);

        btnConfirmSyncToken.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnConfirmSyncToken.setText("Confirm Token");
        btnConfirmSyncToken.setToolTipText("Validates the token and then enables the available features.");
        btnConfirmSyncToken.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirmSyncToken.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnConfirmSyncToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmSyncTokenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSyncTokenLayout = new javax.swing.GroupLayout(pnlSyncToken);
        pnlSyncToken.setLayout(pnlSyncTokenLayout);
        pnlSyncTokenLayout.setHorizontalGroup(
            pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSyncTokenLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncTokenLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel6)
                    .addComponent(jSeparator4))
                .addGap(5, 5, 5))
        );
        pnlSyncTokenLayout.setVerticalGroup(
            pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncTokenLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pnlSyncOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Synced Workspaces", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lstWorkspaces.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lstWorkspaces.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstWorkspaces.setSelectionBackground(new java.awt.Color(56, 87, 9));
        lstWorkspaces.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstWorkspacesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstWorkspaces);

        javax.swing.GroupLayout pnlSyncOptionsLayout = new javax.swing.GroupLayout(pnlSyncOptions);
        pnlSyncOptions.setLayout(pnlSyncOptionsLayout);
        pnlSyncOptionsLayout.setHorizontalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2)
                .addGap(5, 5, 5))
        );
        pnlSyncOptionsLayout.setVerticalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)))
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(130, 130, 130))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (!lstWorkspaces.getSelectionModel().isSelectionEmpty()
                && !lstWorkspaces.getSelectedValue().equals("Loading...")) {
            workspaceID = Long.parseLong(lstWorkspaces.getSelectedValue().substring(0, lstWorkspaces.getSelectedValue().indexOf(" - ")).trim());
        }
        // Close this popup
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnConfirmSyncTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmSyncTokenActionPerformed
        if (txaSyncToken.getText() != null && !txaSyncToken.getText().isEmpty()) {
            // Parse the token
            String syncToken = null;
            try {
                syncToken = TokenEncryptor.decrypt(txaSyncToken.getText());
            }
            catch (Exception ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            if (syncToken == null || syncToken.isEmpty()) {
                WLOptionPane.showMessageDialog(this,
                        "<html>The provided <i>WildLog Cloud Sync Token</i> could not be read. "
                                + "<br>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                        "Invalid Sync Token!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Get a list of WildLogOptions entries (one per synced workspace)
            String[] syncTokenValues = syncToken.split(" ");
            SyncAzure syncAzure = new SyncAzure(syncTokenValues[3], syncTokenValues[1], syncTokenValues[2], 0, 0);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Show loading message
                    lstWorkspaces.setModel(new DefaultComboBoxModel<>());
                    ((DefaultComboBoxModel) lstWorkspaces.getModel()).addElement("Loading...");
                    // Load the data from Azure
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<SyncTableEntry> lstWorkspaceOptions = syncAzure.downloadDataBatch(WildLogDataType.WILDLOG_OPTIONS, 0, null);
                                // Show the workspaces on the UI
                                lstWorkspaces.setModel(new DefaultComboBoxModel<>());
                                for (SyncTableEntry syncEntry : lstWorkspaceOptions) {
                                    ((DefaultComboBoxModel) lstWorkspaces.getModel()).addElement(
                                            Long.toString(syncEntry.getWorkspaceID()) + " - " +((WildLogOptions) syncEntry.getData()).getWorkspaceName());
                                }
                            }
                            catch (Exception ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                lstWorkspaces.setModel(new DefaultComboBoxModel<>());
                                ((DefaultComboBoxModel) lstWorkspaces.getModel()).addElement("Unable to load...");
                            }
                        }
                    });
                }
            });
        }
        else {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                    "Invalid Sync Token!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmSyncTokenActionPerformed

    private void lstWorkspacesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstWorkspacesValueChanged
        btnConfirm.setEnabled(!lstWorkspaces.getSelectionModel().isSelectionEmpty());
    }//GEN-LAST:event_lstWorkspacesValueChanged

    public long getWorkspaceID() {
        return workspaceID;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnConfirmSyncToken;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList<String> lstWorkspaces;
    private javax.swing.JPanel pnlSyncOptions;
    private javax.swing.JPanel pnlSyncToken;
    private javax.swing.JTextArea txaSyncToken;
    // End of variables declaration//GEN-END:variables
}