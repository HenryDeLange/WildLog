package wildlog.ui.dialogs;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.encryption.TokenEncryptor;
import wildlog.sync.azure.SyncAzure;
import wildlog.sync.azure.dataobjects.SyncTableEntry;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.renderers.WorkspaceTreeDataWrapper;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogPaths;

// TODO: Refactor! Baie van die code is basies 90% 'n copy-paste uit die cloud sync of workspace export...

public class WorkspaceCloudImportDialog extends JDialog {
    private final WildLogApp app;
    private int syncDataDown = 0;
    private int syncFail = 0;


    public WorkspaceCloudImportDialog(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceCloudImportDialog]");
        app = inApp;
        initComponents();
        // Set minimum size
        UtilsDialog.setupMinimumSize(this);
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Attach Clipboards
        UtilsUI.attachClipboardPopup(txaSyncToken, false, true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpFiles = new javax.swing.ButtonGroup();
        grpImages = new javax.swing.ButtonGroup();
        btnConfirm = new javax.swing.JButton();
        pnlSyncToken = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaSyncToken = new javax.swing.JTextArea();
        btnConfirmSyncToken = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pnlSyncOptions = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstWorkspaces = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import From A Cloud Workspace");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small.gif")).getImage());
        setModal(true);

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnConfirm.setToolTipText("Export the selected records to a new Workspace.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        pnlSyncToken.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Token", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("<html><b>Please provide your <i>WildLog Cloud Sync Token</i> to enable WildLog to synchronise the data.</b><br/> If you don't have a <i>WildLog Cloud Sync Token</i> then please contact <u>support@mywild.co.za</> for a custom quotation.</html>");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Sync Token:");

        txaSyncToken.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txaSyncToken.setLineWrap(true);
        txaSyncToken.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaSyncToken);

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
                    .addComponent(jScrollPane2)
                    .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncTokenLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel6)
                    .addComponent(jSeparator4))
                .addGap(5, 5, 5))
        );
        pnlSyncTokenLayout.setVerticalGroup(
            pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncTokenLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("<html>Please select the Cloud Workspace from which to import the ReStashed data associated with Stashed Periods.</html>");

        pnlSyncOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Synced Workspaces", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lstWorkspaces.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lstWorkspaces.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstWorkspaces.setSelectionBackground(new java.awt.Color(56, 87, 9));
        lstWorkspaces.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstWorkspacesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lstWorkspaces);

        javax.swing.GroupLayout pnlSyncOptionsLayout = new javax.swing.GroupLayout(pnlSyncOptions);
        pnlSyncOptions.setLayout(pnlSyncOptionsLayout);
        pnlSyncOptionsLayout.setHorizontalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3)
                .addGap(5, 5, 5))
        );
        pnlSyncOptionsLayout.setVerticalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnlSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[ImportCloudWorkspace]");
        // Get the sync keys from the token
        String syncToken = getSyncToken();
        // Validate the token
        if (syncToken == null || syncToken.isEmpty() || syncToken.split(" ").length != 4) {
            WLOptionPane.showMessageDialog(this,
                    "<html>The provided <i>WildLog Cloud Sync Token</i> could not be read. "
                            + "<br>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                    "Invalid Sync Token!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Parse the token to get the necessary values
        String[] syncTokenValues = syncToken.split(" ");
        WildLogApp.LOGGER.log(Level.INFO, "Sync Mode: {}", syncTokenValues[0]);
        WildLogApp.LOGGER.log(Level.INFO, "Sync Account: {}", syncTokenValues[1]);
        if (syncTokenValues[0].equals("FREE")) {
            WLOptionPane.showMessageDialog(this,
                    "<html>This feature is not available using the limited free <i>WildLog Cloud Sync Token</i>. "
                            + "<br>Please contact <u>support@mywild.co.za</> for help.</html>",
                    "Free Token Exceeded!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validate selection
        if (lstWorkspaces.getSelectionModel().isSelectionEmpty()
                || lstWorkspaces.getSelectedValue().equals("Loading...")) {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please select a Cloud Workspace to import from.</html>",
                    "Invalid Selection!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Setup Azure Sync
        long workspaceID = Long.parseLong(lstWorkspaces.getSelectedValue().substring(0, lstWorkspaces.getSelectedValue().indexOf(" - ")).trim());
        final SyncAzure syncAzure = new SyncAzure(syncTokenValues[3], syncTokenValues[1], syncTokenValues[2], 
                workspaceID, WildLogApp.getApplication().getWildLogOptions().getDatabaseVersion());
        // Close this popup
        setVisible(false);
        dispose();
        // Start the sync process
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        long startTime = System.currentTimeMillis();
                        setProgress(0);
                        setMessage("Starting the Cloud Import");
                        WildLogApp.LOGGER.log(Level.INFO, "Starting the Cloud Import: {}", lstWorkspaces.getSelectedValue());
                        // Setup the report
                        Path feedbackFile = null;
                        PrintWriter feedback = null;
                        try {
                            Files.createDirectories(WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath());
                            feedbackFile = WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath().resolve(
                                    "CloudImportWorkspaceReport_" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ".txt");
                            feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                            feedback.println("-------------------------------------------------");
                            feedback.println("-------------- Cloud Import Report --------------");
                            feedback.println("-------------------------------------------------");
                            feedback.println("");
                            // Get selected records
                            setProgress(1);
                            setMessage("Busy with the Cloud Import - Calculating ... " + getProgress() + "%");
                            List<SyncTableEntry> lstCloudElements = syncAzure.downloadDataBatch(WildLogDataType.ELEMENT, 0, null);
                            List<SyncTableEntry> lstCloudExtraData = syncAzure.downloadDataBatch(WildLogDataType.EXTRA, 0, null);
                            List<Element> lstElements = app.getDBI().listElements(null, null, null, false, Element.class);
                            List<ExtraData> lstExtraData = app.getDBI().listExtraDatas(WildLogExtraDataFieldTypes.WILDLOG, 0, ExtraData.class);
                            setProgress(5);
                            setMessage("Busy with the Cloud Import - Processing ... " + getProgress() + "%");
                            for (SyncTableEntry cloudElement : lstCloudElements) {
                                boolean found = false;
                                for (Element element : lstElements) {
                                    if (cloudElement.getData().getID() == element.getID()) {
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    logIfFailed(feedback, new SyncAction("CLOUD_DOWNLOAD", WildLogDataType.ELEMENT, 
                                            cloudElement.getRecordID(), ((Element) cloudElement.getData()).getPrimaryName(), cloudElement.getData()), 
                                            app.getDBI().createElement((Element) cloudElement.getData(), true));
                                    syncDataDown++;
                                }
                            }
                            setProgress(50);
                            setMessage("Busy with the Cloud Import - Processing ... " + getProgress() + "%");
                            for (SyncTableEntry cloudExtraData : lstCloudExtraData) {
                                boolean found = false;
                                boolean shouldImport = false;
                                for (ExtraData extraData : lstExtraData) {
                                    if (cloudExtraData.getData().getID() == extraData.getID()) {
                                        found = true;
                                        if (cloudExtraData.getData().getAuditTime() > extraData.getAuditTime()) {
                                            shouldImport = true;
                                        }
                                    }
                                }
                                if (!found || shouldImport) {
                                    ExtraData importExtraData = (ExtraData) cloudExtraData.getData();
                                    ExtraData possibleConflict = app.getDBI().findExtraData(-1, WildLogExtraDataFieldTypes.WILDLOG, 
                                            importExtraData.getLinkID(), importExtraData.getDataKey(), ExtraData.class);
                                    if (possibleConflict == null) {
                                        logIfFailed(feedback, new SyncAction("CLOUD_DOWNLOAD", WildLogDataType.EXTRA, 
                                            cloudExtraData.getRecordID(), Long.toString(((ExtraData) cloudExtraData.getData()).getLinkID()), cloudExtraData.getData()), 
                                            app.getDBI().createExtraData(importExtraData, true));
                                        syncDataDown++;
                                    }
                                    else
                                    if (importExtraData.getAuditTime() > possibleConflict.getAuditTime()) {
                                        logIfFailed(feedback, new SyncAction("CLOUD_DOWNLOAD", WildLogDataType.EXTRA, 
                                            cloudExtraData.getRecordID(), Long.toString(((ExtraData) cloudExtraData.getData()).getLinkID()), cloudExtraData.getData()), 
                                            app.getDBI().updateExtraData(importExtraData, true));
                                        syncDataDown++;
                                    }
                                }
                            }
                            setProgress(99);
                        }
                        // Finish the report
                        catch (Exception ex) {
                            if (feedback != null) {
                                feedback.println("");
                                feedback.println("--------------------------------------");
                                feedback.println("--------------- ERROR ----------------");
                                feedback.println(ex.toString());
                                feedback.println("--------------------------------------");
                                feedback.println("");
                            }
                            syncFail++;
                            throw ex;
                        }
                        finally {
                            if (feedback != null) {
                                feedback.println("");
                                feedback.println("--------------- SUMMARY ----------------");
                                feedback.println("");
                                feedback.println("Failed Sync Actions   : " + syncFail);
                                feedback.println("");
                                feedback.println("Data Downloads        : " + syncDataDown);
                                feedback.println("");
                                feedback.println("--------------- DURATION ----------------");
                                feedback.println("");
                                long duration = System.currentTimeMillis() - startTime;
                                int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                                int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                                int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                                feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                                feedback.println("");
                                feedback.println("--------------------------------------");
                                feedback.println("-------------- FINISHED --------------");
                                feedback.println("--------------------------------------");
                                feedback.println("");
                                feedback.flush();
                                feedback.close();
                                // Open the summary document
                                UtilsFileProcessing.openFile(feedbackFile);
                                // Print summary to the logs
                                WildLogApp.LOGGER.log(Level.INFO, "Failed Sync Actions          : {}", syncFail);
                                WildLogApp.LOGGER.log(Level.INFO, "Synced Data Downloads        : {}", syncDataDown);
                                WildLogApp.LOGGER.log(Level.INFO, "Cloud Import Duration: {} hours, {} minutes, {} seconds", hours, minutes, seconds);
                            }
                        }
                        setProgress(100);
                        setMessage("Done with the Cloud Import");
                        return null;
                    }

                    @Override
                    protected void finished() {
                        super.finished();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                        "<html>The Cloud Import process has completed."
                                                + "<br/><br/><hr/>"
                                                + "<br/>Workspace                : " + lstWorkspaces.getSelectedValue()
                                                + "<br/><br/><hr/>"
                                                + "<br/><b>Failed Sync Actions   : " + syncFail + "</b>"
                                                + "<br/>"
                                                + "<br/>Data Downloads           : " + syncDataDown
                                                + "<br/><br/><hr/><br/></html>",
                                        "Completed Cloud Import", WLOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                });
            }
        });
    }//GEN-LAST:event_btnConfirmActionPerformed
    
    private void btnConfirmSyncTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmSyncTokenActionPerformed
        if (txaSyncToken.getText() != null && !txaSyncToken.getText().trim().isEmpty()) {
            // Parse the token
            String syncToken = null;
            try {
                syncToken = TokenEncryptor.decrypt(txaSyncToken.getText().trim());
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

    private String getSyncToken() {
        // Get the sync keys from the token
        String syncToken;
        if (txaSyncToken.getText() == null || txaSyncToken.getText().trim().isEmpty()) {
            final char[] buffer = new char[450];
            final StringBuilder builder = new StringBuilder(450);
            try (Reader in = new BufferedReader(new InputStreamReader(WildLogApp.class.getResourceAsStream("sync/FreeSyncToken"), "UTF-8"))) {
                int length = 0;
                while (length >= 0) {
                    length = in.read(buffer, 0, buffer.length);
                    if (length > 0) {
                        builder.append(buffer, 0, length);
                    }
                }
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            syncToken = TokenEncryptor.decrypt(builder.toString());
        }
        else {
            syncToken = TokenEncryptor.decrypt(txaSyncToken.getText().trim());
        }
        return syncToken;
    }
    
    public void logIfFailed(PrintWriter inFeedback, SyncAction inSyncAction, boolean inResult) {
        if (inResult) {
            inFeedback.println(inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID);
        }
        else {
            inFeedback.println("SYNC_FAIL " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID);
            WildLogApp.LOGGER.log(Level.ERROR, "Sync - Failed: " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID + " " + inSyncAction.data);
            WildLogApp.LOGGER.log(Level.INFO, "Sync - Stacktrace:");
            new Exception("Sync Failure").printStackTrace(System.out);
            syncFail++;
        }
    }
    
    private int getNumberOfSelectedNodes(TreeModel inModel, DefaultMutableTreeNode inNode) {
        int count = 0;
        if (inNode.getUserObject() instanceof WorkspaceTreeDataWrapper) {
            WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) inNode.getUserObject();
            if (dataWrapper.isSelected()) {
                count = 1;
            }
        }
        int numberOfChildren = inModel.getChildCount(inNode);
        for (int i = 0; i < numberOfChildren; i++) {
            count += getNumberOfSelectedNodes(inModel, (DefaultMutableTreeNode) inModel.getChild(inNode, i));
        }
        return count;
    }
    
    private class SyncAction {
        private String command;
        private WildLogDataType type;
        private long recordID;
        private String details;
        private DataObjectWithAudit data;

        private SyncAction(String inCommand, WildLogDataType inType, long inRecordID, String inDetails, DataObjectWithAudit inData) {
            command = inCommand;
            type = inType;
            recordID = inRecordID;
            details = inDetails;
            data = inData;
        }
    }
    
    private class ProgressCounter {
        public int counter = 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnConfirmSyncToken;
    private javax.swing.ButtonGroup grpFiles;
    private javax.swing.ButtonGroup grpImages;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JList<String> lstWorkspaces;
    private javax.swing.JPanel pnlSyncOptions;
    private javax.swing.JPanel pnlSyncToken;
    private javax.swing.JTextArea txaSyncToken;
    // End of variables declaration//GEN-END:variables
}