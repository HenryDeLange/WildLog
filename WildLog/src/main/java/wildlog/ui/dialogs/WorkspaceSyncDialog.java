package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import org.apache.logging.log4j.Level;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.WildLogApp;
import wildlog.WildLogView;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogDeleteLog;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.encryption.TokenEncryptor;
import wildlog.sync.azure.SyncAzure;
import wildlog.sync.azure.dataobjects.SyncBlobEntry;
import wildlog.sync.azure.dataobjects.SyncTableEntry;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;


public class WorkspaceSyncDialog extends JDialog {
    private int syncDeleteUp = 0;
    private int syncDeleteDown = 0;
    private int syncDataUp = 0;
    private int syncDataDown = 0;
    private int syncFileUp = 0;
    private int syncFileDown = 0;
    private int syncFail = 0;


    public WorkspaceSyncDialog() {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceSyncDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Attache copy-paste right-clicks
        UtilsUI.attachClipboardPopup(txaSyncToken);
        // Default to the free token's configuration
        configureFreeToken();
    }
    
    private void configureFreeToken() {
        rdbModeBatch.setSelected(true);
        rdbModeSingle.setEnabled(false);
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(false);
        rdbSyncOriginalImages.setEnabled(false);
        cmbThumbnailSize.setEnabled(false);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.VERY_TINY);
        cmbThumbnailSize.removeItem(WildLogThumbnailSizes.SYNC_LIMIT);
        rdbSyncNoFiles.setSelected(true);
        rdbSyncAllFiles.setEnabled(false);
        rdbSyncImagesOnly.setEnabled(false);
    }
    
    private void configureBasicToken() {
        rdbModeBatch.setSelected(true);
        rdbModeSingle.setEnabled(true);
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(true);
        rdbSyncOriginalImages.setEnabled(false);
        cmbThumbnailSize.setEnabled(true);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.LARGE);
        cmbThumbnailSize.removeItem(WildLogThumbnailSizes.SYNC_LIMIT);
        rdbSyncImagesOnly.setSelected(true);
        rdbSyncAllFiles.setEnabled(false);
        rdbSyncImagesOnly.setEnabled(true);
    }
    
    private void configureFullToken() {
        rdbModeBatch.setSelected(true);
        rdbModeSingle.setEnabled(true);
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(true);
        rdbSyncOriginalImages.setEnabled(true);
        cmbThumbnailSize.setEnabled(true);
        cmbThumbnailSize.addItem(WildLogThumbnailSizes.SYNC_LIMIT);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.SYNC_LIMIT);
        rdbSyncImagesOnly.setSelected(true);
        rdbSyncAllFiles.setEnabled(true);
        rdbSyncImagesOnly.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpFiles = new javax.swing.ButtonGroup();
        grpImages = new javax.swing.ButtonGroup();
        grpMode = new javax.swing.ButtonGroup();
        jLabel4 = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        pnlSyncToken = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaSyncToken = new javax.swing.JTextArea();
        btnConfirmSyncToken = new javax.swing.JButton();
        pnlSyncOptions = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rdbSyncAllFiles = new javax.swing.JRadioButton();
        rdbSyncImagesOnly = new javax.swing.JRadioButton();
        rdbSyncNoFiles = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        rdbSyncOriginalImages = new javax.swing.JRadioButton();
        rdbSyncThumbnails = new javax.swing.JRadioButton();
        cmbThumbnailSize = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        rdbModeBatch = new javax.swing.JRadioButton();
        rdbModeSingle = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cloud Sync Workspace");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Sync.png")).getImage());
        setModal(true);
        setResizable(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Cloud Sync Workspace");

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnConfirm.setToolTipText("Sync the active Workspace with the data stored in the cloud.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        pnlSyncToken.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Token", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("<html><b>Please provide your <i>WildLog Cloud Sync Token</i> to enable WildLog to synchronise the data.</b> <br/><br/> If you don't have a <i>WildLog Cloud Sync Token</i> then please contact <u>support@mywild.co.za</> for a custom quotation. <br/><br/> Alternatively, you can use the limited free token by simply leaving the <i>WildLog Cloud Sync Token</i> field empty.</html>");

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
        btnConfirmSyncToken.setFocusPainted(false);
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
                .addComponent(jLabel6)
                .addGap(10, 10, 10)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pnlSyncOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Files:");

        grpFiles.add(rdbSyncAllFiles);
        rdbSyncAllFiles.setText("All Files");
        rdbSyncAllFiles.setToolTipText("Sync all files between the current Workspace and the cloud.");
        rdbSyncAllFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncAllFiles.setFocusPainted(false);
        rdbSyncAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncAllFilesActionPerformed(evt);
            }
        });

        grpFiles.add(rdbSyncImagesOnly);
        rdbSyncImagesOnly.setText("Images Only");
        rdbSyncImagesOnly.setToolTipText("Sync only images between the current Workspace and the cloud.");
        rdbSyncImagesOnly.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncImagesOnly.setFocusPainted(false);
        rdbSyncImagesOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncImagesOnlyActionPerformed(evt);
            }
        });

        grpFiles.add(rdbSyncNoFiles);
        rdbSyncNoFiles.setSelected(true);
        rdbSyncNoFiles.setText("No Files");
        rdbSyncNoFiles.setToolTipText("Don't sync any files between the current Workspace and the cloud.");
        rdbSyncNoFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncNoFiles.setFocusPainted(false);
        rdbSyncNoFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncNoFilesActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        grpImages.add(rdbSyncOriginalImages);
        rdbSyncOriginalImages.setText("Original Images");
        rdbSyncOriginalImages.setToolTipText("Sync a copy of the original linked images between the current Workspace and the cloud.");
        rdbSyncOriginalImages.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncOriginalImages.setFocusPainted(false);
        rdbSyncOriginalImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncOriginalImagesActionPerformed(evt);
            }
        });

        grpImages.add(rdbSyncThumbnails);
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setText("Thumbnail Images");
        rdbSyncThumbnails.setToolTipText("The images that are synced will be reduced in size, the original images will not be synced.");
        rdbSyncThumbnails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncThumbnails.setFocusPainted(false);
        rdbSyncThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncThumbnailsActionPerformed(evt);
            }
        });

        cmbThumbnailSize.setMaximumRowCount(15);
        cmbThumbnailSize.setModel(new DefaultComboBoxModel(WildLogThumbnailSizes.values()));
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.VERY_LARGE);
        cmbThumbnailSize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbThumbnailSize.setEnabled(false);
        cmbThumbnailSize.setFocusable(false);

        jLabel7.setText("px");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Mode:");

        grpMode.add(rdbModeBatch);
        rdbModeBatch.setSelected(true);
        rdbModeBatch.setText("Batch Mode");
        rdbModeBatch.setToolTipText("Perform the sync using batch operations.");
        rdbModeBatch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbModeBatch.setFocusPainted(false);

        grpMode.add(rdbModeSingle);
        rdbModeSingle.setText("Single Mode");
        rdbModeSingle.setToolTipText("Perform the sync using single operations for each record.");
        rdbModeSingle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbModeSingle.setFocusPainted(false);

        javax.swing.GroupLayout pnlSyncOptionsLayout = new javax.swing.GroupLayout(pnlSyncOptions);
        pnlSyncOptions.setLayout(pnlSyncOptionsLayout);
        pnlSyncOptionsLayout.setHorizontalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSyncOptionsLayout.createSequentialGroup()
                        .addComponent(jSeparator3)
                        .addGap(11, 11, 11))
                    .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                        .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel1)
                                .addGap(10, 10, 10)
                                .addComponent(rdbModeBatch)
                                .addGap(5, 5, 5)
                                .addComponent(rdbModeSingle))
                            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(10, 10, 10)
                                .addComponent(rdbSyncAllFiles)
                                .addGap(5, 5, 5)
                                .addComponent(rdbSyncImagesOnly)
                                .addGap(5, 5, 5)
                                .addComponent(rdbSyncNoFiles)
                                .addGap(10, 10, 10)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(rdbSyncOriginalImages)
                                .addGap(5, 5, 5)
                                .addComponent(rdbSyncThumbnails)
                                .addGap(5, 5, 5)
                                .addComponent(cmbThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel7)))
                        .addContainerGap())))
        );
        pnlSyncOptionsLayout.setVerticalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cmbThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbSyncOriginalImages)
                            .addComponent(rdbSyncThumbnails)
                            .addComponent(rdbSyncImagesOnly)
                            .addComponent(rdbSyncAllFiles)
                            .addComponent(rdbSyncNoFiles))))
                .addGap(5, 5, 5)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbModeBatch)
                    .addComponent(rdbModeSingle))
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
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(jLabel4)
                        .addGap(10, 10, 10)
                        .addComponent(pnlSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[SyncWorkspace]");
        // Get the sync keys from the token
        String syncToken;
        if (txaSyncToken.getText() == null || txaSyncToken.getText().isEmpty()) {
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
            syncToken = TokenEncryptor.decrypt(txaSyncToken.getText());
        }
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
                    "<html>You are currently using the limited free <i>WildLog Cloud Sync Token</i>. "
                            + "<br>This token can only be used with 1000 or less records and files are not synced."
                            + "<br>The token is subject to fair use and load restrictions."
                            + "<br>Contact <u>support@mywild.co.za</> for more details.</html>",
                    "Using Free Sync Token", JOptionPane.WARNING_MESSAGE);
            if ((WildLogApp.getApplication().getDBI().countDeleteLogs(null) 
                    + WildLogApp.getApplication().getDBI().countElements(null, null)
                    + WildLogApp.getApplication().getDBI().countLocations(null)
                    + WildLogApp.getApplication().getDBI().countSightings(0, 0, 0, 0)
                    + WildLogApp.getApplication().getDBI().countUsers()
                    + WildLogApp.getApplication().getDBI().countVisits(null, 0)) > 1000) {
                WLOptionPane.showMessageDialog(this,
                    "<html>This WildLog Workspace exceeds the restrictions of the limted free <i>WildLog Cloud Sync Token</i>. "
                            + "<br>Please contact <u>support@mywild.co.za</> for help.</html>",
                    "Free Token Exceeded!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        final SyncAzure syncAzure = new SyncAzure(syncTokenValues[3], syncTokenValues[1], syncTokenValues[2], 
                WildLogApp.getApplication().getWildLogOptions().getWorkspaceID(), WildLogApp.getApplication().getWildLogOptions().getDatabaseVersion());
        // Close this popup
        setVisible(false);
        dispose();
        // Start the sync process
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Close all tabs and go to the home tab
                WildLogApp.getApplication().getMainFrame().getTabbedPane().setSelectedIndex(0);
                while (WildLogApp.getApplication().getMainFrame().getTabbedPane().getTabCount() > WildLogView.STATIC_TAB_COUNT) {
                    WildLogApp.getApplication().getMainFrame().getTabbedPane().remove(WildLogView.STATIC_TAB_COUNT);
                }
                // Lock the input/display and show busy message
                // Note: we never remove the Busy dialog and greyed out background since the app will be restarted anyway when done (Don't use JDialog since it stops the code until the dialog is closed...)
                WildLogApp.getApplication().getMainFrame().getTabbedPane().setSelectedIndex(0);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JPanel panel = new JPanel(new AbsoluteLayout());
                        panel.setPreferredSize(new Dimension(400, 50));
                        panel.setBorder(new LineBorder(new Color(245, 80, 40), 3));
                        JLabel label = new JLabel("<html>Busy with Cloud Sync Workspace. Please be patient, this might take a while. <br/>"
                                + "Don't close the application until the process is finished.</html>");
                        label.setFont(new Font("Tahoma", Font.BOLD, 12));
                        label.setBorder(new LineBorder(new Color(195, 65, 20), 4));
                        panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.95f));
                        panel.add(label, new AbsoluteConstraints(410, 20, -1, -1));
                        panel.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
                        JPanel glassPane = (JPanel) WildLogApp.getApplication().getMainFrame().getGlassPane();
                        glassPane.removeAll();
                        glassPane.setLayout(new BorderLayout(100, 100));
                        glassPane.add(panel, BorderLayout.CENTER);
                        glassPane.addMouseListener(new MouseAdapter() {});
                        glassPane.addKeyListener(new KeyAdapter() {});
                        WildLogApp.getApplication().getMainFrame().setGlassPane(glassPane);
                        WildLogApp.getApplication().getMainFrame().getGlassPane().setVisible(true);
                        WildLogApp.getApplication().getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                });
                UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        long startTime = System.currentTimeMillis();
                        setProgress(0);
                        setMessage("Starting the Cloud Sync Workspace");
                        // Setup the report
                        Path feedbackFile = WildLogPaths.getFullWorkspacePrefix().resolve("CloudSyncWorkspaceReport.txt");
                        PrintWriter feedback = null;
                        try {
                            double adjustForNoFiles = 1;
                            if (rdbSyncNoFiles.isSelected()) {
                                adjustForNoFiles = 1.5;
                            }
                            feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                            feedback.println("-------------------------------------------------");
                            feedback.println("---------- Cloud Sync Workspace Report ----------");
                            feedback.println("-------------------------------------------------");
                            feedback.println("");
                            // SYNC - Delete Logs
                            setProgress((int) (1 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing DeleteLogs ... " + getProgress() + "%");
                            syncDeleteLogs(feedback, syncAzure, this, (int) (9 * adjustForNoFiles));
                            // SYNC - Data
                            setProgress((int) (10 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing Creatures ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.ELEMENT, this, (int) (10 * adjustForNoFiles));
                            setProgress((int) (20 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing Places ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.LOCATION, this, (int) (10 * adjustForNoFiles));
                            setProgress((int) (30 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing Periods ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.VISIT, this, (int) (10 * adjustForNoFiles));
                            setProgress((int) (40 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing Observations ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.SIGHTING, this, (int) (10 * adjustForNoFiles));
                            setProgress((int) (50 * adjustForNoFiles));
                            setMessage("Busy with the Cloud Sync Workspace - Syncing Users ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.WILDLOG_USER, this, (int) (10 * adjustForNoFiles));
                            // SYNC - Files
                            if (!rdbSyncNoFiles.isSelected()) {
                                setProgress(60);
                                setMessage("Busy with the Cloud Sync Workspace - Syncing Files ... " + getProgress() + "%");
                                syncFileRecords(feedback, syncAzure, this, 39);
                            }
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
                                feedback.println("Failed Sync Actions        : " + syncFail);
                                feedback.println("Synced DeleteLog Uploads   : " + syncDeleteUp);
                                feedback.println("Synced DeleteLog Downloads : " + syncDeleteDown);
                                feedback.println("Synced Data Uploads        : " + syncDataUp);
                                feedback.println("Synced Data Downloads      : " + syncDataDown);
                                feedback.println("Synced File Uploads        : " + syncFileUp);
                                feedback.println("Synced File Downloads      : " + syncFileDown);
                                feedback.println("");
                                feedback.println("--------------- DURATION ----------------");
                                long duration = System.currentTimeMillis() - startTime;
                                int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                                int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                                int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                                feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                                WildLogApp.LOGGER.log(Level.INFO, "Cloud Sync Duration: {} hours, {} minutes, {} seconds", hours, minutes, seconds);
                                feedback.println("");
                                feedback.println("--------------------------------------");
                                feedback.println("-------------- FINISHED --------------");
                                feedback.println("--------------------------------------");
                                feedback.println("");
                                feedback.flush();
                                feedback.close();
                                // Open the summary document
                                UtilsFileProcessing.openFile(feedbackFile);
                            }
                        }
                        setProgress(100);
                        setMessage("Done with the Cloud Sync Workspace");
                        return null;
                    }

                    @Override
                    protected void finished() {
                        super.finished();
                        // Using invokeLater because I hope the progressbar will have finished by then, otherwise the popup is shown
                        // that asks whether you want to close the application or not, and it's best to rather restart after the cleanup.
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // Close the application to be safe (make sure no wierd references/paths are still used, etc.)
                                WLOptionPane.showMessageDialog(null, 
                                        "The Cloud Sync Workspace process has completed. Please restart the application.", 
                                        "Completed Cloud Sync Workspace", WLOptionPane.INFORMATION_MESSAGE);
                                WildLogApp.getApplication().quit(null);
                            }
                        });
                    }
                });
            }
        });
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void rdbSyncAllFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncAllFilesActionPerformed
       if (rdbSyncAllFiles.isSelected()) {
           rdbSyncOriginalImages.setEnabled(true);
           rdbSyncThumbnails.setEnabled(true);
       }
    }//GEN-LAST:event_rdbSyncAllFilesActionPerformed

    private void rdbSyncImagesOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncImagesOnlyActionPerformed
        if (rdbSyncImagesOnly.isSelected()) {
           rdbSyncOriginalImages.setEnabled(true);
           rdbSyncThumbnails.setEnabled(true);
       }
    }//GEN-LAST:event_rdbSyncImagesOnlyActionPerformed

    private void rdbSyncNoFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncNoFilesActionPerformed
        if (rdbSyncNoFiles.isSelected()) {
           rdbSyncOriginalImages.setEnabled(false);
           rdbSyncThumbnails.setEnabled(false);
       }
    }//GEN-LAST:event_rdbSyncNoFilesActionPerformed

    private void rdbSyncOriginalImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncOriginalImagesActionPerformed
        if (rdbSyncOriginalImages.isSelected()) {
            cmbThumbnailSize.setEnabled(false);
        }
    }//GEN-LAST:event_rdbSyncOriginalImagesActionPerformed

    private void rdbSyncThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncThumbnailsActionPerformed
        if (rdbSyncThumbnails.isSelected()) {
            cmbThumbnailSize.setEnabled(true);
        }
    }//GEN-LAST:event_rdbSyncThumbnailsActionPerformed

    private void btnConfirmSyncTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmSyncTokenActionPerformed
        if (txaSyncToken.getText() == null || txaSyncToken.getText().isEmpty()) {
            configureFreeToken();
        }
        else {
            String syncToken = TokenEncryptor.decrypt(txaSyncToken.getText());
            if (syncToken == null || syncToken.isEmpty()) {
                WLOptionPane.showMessageDialog(this,
                        "<html>The provided <i>WildLog Cloud Sync Token</i> could not be read. "
                                + "<br>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                        "Invalid Sync Token!", JOptionPane.ERROR_MESSAGE);
            }
            String[] syncTokenValues = syncToken.split(" ");
            switch (syncTokenValues[0]) {
                case "FREE": configureFreeToken(); break;
                case "BASIC": configureBasicToken(); break;
                case "FULL": configureFullToken(); break;
                default: configureFreeToken(); break;
            }
        }
    }//GEN-LAST:event_btnConfirmSyncTokenActionPerformed

    private void logIfFailed(PrintWriter inFeedback, SyncAction inSyncAction, boolean inResult) {
        if (inResult) {
            inFeedback.println(inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.recordID + " " + inSyncAction.details);
        }
        else {
            inFeedback.println("SYNC_FAIL " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.recordID + " " + inSyncAction.details);
            WildLogApp.LOGGER.log(Level.ERROR, "Sync - Failed: " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.recordID + " " + inSyncAction.details + " " + inSyncAction.data);
            WildLogApp.LOGGER.log(Level.INFO, "Sync - Stacktrace:");
            new Exception().printStackTrace(System.out);
            syncFail++;
        }
    }
    
    private void syncDeleteLogs(PrintWriter inFeedback, SyncAzure inSyncAzure, ProgressbarTask inProgressbar, int inProgressStepSize) {
        
// FIXME: Klein probleem met die logging waar dit lyk soos flase positive errors wanneer 'n rekord geskep en dan delete was tussen twee sync aksies
        
        int baseProgress = inProgressbar.getProgress();
        List<SyncTableEntry> lstCloudEntries  = inSyncAzure.getSyncListDataBatch(WildLogDataType.DELETE_LOG, 0);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Delete Logs - Cloud Entries: " + lstCloudEntries.size());
        List<WildLogDeleteLog> lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listDeleteLogs(null, 0, WildLogDeleteLog.class);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Delete Logs - Workspace Entries: " + lstCloudEntries.size());
        // UP: Make sure cloud knows about new workspace records
        double totalActions = 0.0;
        Map<WildLogDataType, List<SyncAction>> mapActions = new HashMap<>();
        for (WildLogDeleteLog workspaceEntry : lstWorkspaceEntries) {
            boolean found = false;
            for (SyncTableEntry cloudEntry : lstCloudEntries) {
                if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                List<SyncAction> lstSyncActions = mapActions.get(workspaceEntry.getType());
                if(lstSyncActions == null) {
                    lstSyncActions = new ArrayList<>();
                    mapActions.put(workspaceEntry.getType(), lstSyncActions);
                }
                lstSyncActions.add(new SyncAction("", workspaceEntry.getType(), workspaceEntry.getID(), "", workspaceEntry));
                totalActions++;
            }
        }
        List<WildLogDataType> lstKeys = new ArrayList<>(mapActions.keySet());
        Collections.sort(lstKeys);
        for (WildLogDataType dataType : lstKeys) {
            if (rdbModeBatch.isSelected()) {
                List<SyncAction> lstSyncActions = mapActions.get(dataType);
                if (!lstSyncActions.isEmpty()) {
                    List<DataObjectWithAudit> lstRecords = new ArrayList<>(lstSyncActions.size());
                    List<Long> lstRecordIDs = new ArrayList<>(lstSyncActions.size());
                    for (SyncAction syncAction : lstSyncActions) {
                        lstRecords.add(syncAction.data);
                        lstRecordIDs.add(syncAction.recordID);
                    }
                    logIfFailed(inFeedback, new SyncAction("CLOUD_UPLOAD " + WildLogDataType.DELETE_LOG.getDescription() + " ", 
                            dataType, lstRecords.size(), Arrays.toString(lstRecords.toArray()), null), 
                            inSyncAzure.uploadDataBatch(WildLogDataType.DELETE_LOG, lstRecords));
                    logIfFailed(inFeedback, new SyncAction("CLOUD_DELETE", 
                            dataType, lstRecordIDs.size(), Arrays.toString(lstRecordIDs.toArray()), null),
                            inSyncAzure.deleteDataBatch(dataType, lstRecordIDs));
                    syncDeleteUp = syncDeleteUp + lstRecordIDs.size();
                }
                inProgressbar.setTaskProgress(inProgressbar.getProgress() + ((int) (((double) inProgressStepSize) / 2.0 / ((double) lstKeys.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
            else {
                double loopCount = 0.0;
                for (SyncAction syncAction : mapActions.get(dataType)) {
                    syncAction.command = "CLOUD_UPLOAD" + WildLogDataType.DELETE_LOG.getDescription() + " ";
                    logIfFailed(inFeedback, syncAction, inSyncAzure.uploadData(WildLogDataType.DELETE_LOG, syncAction.data));
                    syncAction.command = "CLOUD_DELETE";
                    logIfFailed(inFeedback, syncAction, inSyncAzure.deleteData(dataType, syncAction.recordID));
                    syncDeleteUp++;
                    inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 * loopCount++ / totalActions)));
                    inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
                }
            }
        }
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // DOWN: Make sure the workspace knows about new cloud records
        double loopCount = 0.0;
        for (SyncTableEntry cloudEntry : lstCloudEntries) {
            boolean found = false;
            for (WildLogDeleteLog workspaceEntry : lstWorkspaceEntries) {
                if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                SyncAction syncAction = new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.DELETE_LOG, 
                        cloudEntry.getRecordID(), "[" + cloudEntry.getWildLogDataType() + "]", null);
                logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createDeleteLog(
                        new WildLogDeleteLog(cloudEntry.getWildLogDataType(), cloudEntry.getRecordID())));
                syncAction.command = "WORKSPACE_DELETE" + WildLogDataType.DELETE_LOG.getDescription() + " ";
                syncAction.type = cloudEntry.getWildLogDataType();
                if(cloudEntry.getWildLogDataType() == WildLogDataType.ELEMENT) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteElement(syncAction.recordID));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.LOCATION) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteLocation(syncAction.recordID));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.VISIT) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteVisit(syncAction.recordID));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.SIGHTING) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteSighting(syncAction.recordID));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.WILDLOG_USER) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteUser(syncAction.recordID));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.FILE) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().deleteWildLogFile(syncAction.recordID));
                }
                else {
                    logIfFailed(inFeedback, syncAction, false);
                }
                syncDeleteDown++;
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 * loopCount++ / ((double) lstCloudEntries.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
    }
    
    private void syncDataRecords(PrintWriter inFeedback, SyncAzure inSyncAzure, WildLogDataType inDataType, ProgressbarTask inProgressbar, int inProgressStepSize) {
        int baseProgress = inProgressbar.getProgress();
        List<SyncTableEntry> lstCloudEntries  = inSyncAzure.getSyncListDataBatch(inDataType, 0);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + inDataType.getDescription() + " - Cloud Entries: " + lstCloudEntries.size());
        List<? extends DataObjectWithAudit> lstWorkspaceEntries;
        if(inDataType == WildLogDataType.ELEMENT) {
            lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listElements(null, null, null, Element.class);
        }
        else
        if(inDataType == WildLogDataType.LOCATION) {
            lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listLocations(null, Location.class);
        }
        else
        if(inDataType == WildLogDataType.VISIT) {
            lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listVisits(null, 0, null, false, Visit.class);
        }
        else
        if(inDataType == WildLogDataType.SIGHTING) {
            lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listSightings(0, 0, 0, false, Sighting.class);
        }
        else
        if(inDataType == WildLogDataType.WILDLOG_USER) {
            lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listUsers(null, WildLogUser.class);
        }
        else {
            lstWorkspaceEntries = null;
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + inDataType.getDescription() + " - Workspace Entries: " + lstWorkspaceEntries.size());
        // UP: Make sure cloud knows about new workspace records
        List<SyncAction> lstSyncActions = new ArrayList<>();
        for (DataObjectWithAudit workspaceEntry : lstWorkspaceEntries) {
            boolean found = false;
            boolean shouldBeSynced = false;
            for (SyncTableEntry cloudEntry : lstCloudEntries) {
                if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                    found = true;
                    if (workspaceEntry.getAuditTime() > cloudEntry.getData().getAuditTime()) {
                        shouldBeSynced = true;
                    }
                    break;
                }
            }
            if (!found || shouldBeSynced) {
                lstSyncActions.add(new SyncAction("CLOUD_UPLOAD", inDataType, workspaceEntry.getID(), "", workspaceEntry));
            }
        }
        if (!lstSyncActions.isEmpty()) {
            if (rdbModeBatch.isSelected()) {
                List<DataObjectWithAudit> lstRecords = new ArrayList<>(lstSyncActions.size());
                List<Long> lstRecordIDs = new ArrayList<>(lstSyncActions.size());
                for (SyncAction syncAction : lstSyncActions) {
                    lstRecords.add(syncAction.data);
                    lstRecordIDs.add(syncAction.recordID);
                }
                logIfFailed(inFeedback, new SyncAction("CLOUD_UPLOAD", inDataType, lstRecords.size(), Arrays.toString(lstRecordIDs.toArray()), null), 
                        inSyncAzure.uploadDataBatch(inDataType, lstRecords));
                syncDataUp = syncDataUp + lstRecords.size();
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0)));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
            else {
                double loopCount = 0.0;
                for (SyncAction syncAction : lstSyncActions) {
                    logIfFailed(inFeedback, syncAction, inSyncAzure.uploadData(inDataType, syncAction.data));
                    syncDataUp++;
                    inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 * loopCount++ / ((double) lstSyncActions.size()))));
                    inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
                }
            }
        }
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // DOWN: Make sure the workspace knows about new cloud records
        List<Long> lstWorkspaceCreateIDs = new ArrayList<>();
        List<Long> lstWorkspaceUpdateIDs = new ArrayList<>();
        for (SyncTableEntry cloudEntry : lstCloudEntries) {
            boolean found = false;
            boolean shouldBeUpdated = false;
            for (DataObjectWithAudit workspaceEntry : lstWorkspaceEntries) {
                if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                    found = true;
                    if (workspaceEntry.getAuditTime() < cloudEntry.getData().getAuditTime()) {
                        shouldBeUpdated = true;
                    }
                    break;
                }
            }
            if (!found) {
                lstWorkspaceCreateIDs.add(cloudEntry.getRecordID());
            }
            if (shouldBeUpdated) {
                lstWorkspaceUpdateIDs.add(cloudEntry.getRecordID());
            }
        }
        // For the new or out of sync records, load the full record from the cloud
        List<SyncTableEntry> lstWorkspaceCreateEntries = null;
        List<SyncTableEntry> lstWorkspaceUpdateEntries = null;
        if (rdbModeBatch.isSelected()) {
            if (!lstWorkspaceCreateIDs.isEmpty()) {
                lstWorkspaceCreateEntries = inSyncAzure.downloadDataBatch(inDataType, 0, lstWorkspaceCreateIDs);
            }
            if (!lstWorkspaceUpdateIDs.isEmpty()) {
                lstWorkspaceUpdateEntries = inSyncAzure.downloadDataBatch(inDataType, 0, lstWorkspaceUpdateIDs);
            }
        }
        else {
            lstWorkspaceCreateEntries = new ArrayList<>(lstWorkspaceCreateIDs.size());
            for (long recordId : lstWorkspaceCreateIDs) {
                lstWorkspaceCreateEntries.add(inSyncAzure.downloadData(inDataType, recordId));
            }
            lstWorkspaceUpdateEntries = new ArrayList<>(lstWorkspaceUpdateIDs.size());
            for (long recordId : lstWorkspaceUpdateIDs) {
                lstWorkspaceUpdateEntries.add(inSyncAzure.downloadData(inDataType, recordId));
            }
        }
        // Check the the downloads were successful
        if (!lstWorkspaceCreateIDs.isEmpty() && (lstWorkspaceCreateEntries == null || lstWorkspaceCreateEntries.size() != lstWorkspaceCreateIDs.size())) {
            logIfFailed(inFeedback, new SyncAction("CLOUD_DOWNLOAD", inDataType, lstWorkspaceCreateIDs.size(), Arrays.toString(lstWorkspaceCreateIDs.toArray()), null), false);
        }
        if (!lstWorkspaceUpdateIDs.isEmpty() && (lstWorkspaceUpdateEntries == null || lstWorkspaceUpdateEntries.size() != lstWorkspaceUpdateIDs.size())) {
            logIfFailed(inFeedback, new SyncAction("CLOUD_DOWNLOAD", inDataType, lstWorkspaceUpdateIDs.size(), Arrays.toString(lstWorkspaceUpdateIDs.toArray()), null), false);
        }
        // Create if not found
        if (lstWorkspaceCreateEntries != null) {
            double loopCount = 0.0;
            for (SyncTableEntry cloudEntry : lstWorkspaceCreateEntries) {
                SyncAction syncAction = new SyncAction("WORKSPACE_DOWNLOAD", cloudEntry.getWildLogDataType(), 
                        cloudEntry.getRecordID(), "NEW", cloudEntry.getData());
                if(cloudEntry.getWildLogDataType() == WildLogDataType.ELEMENT) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createElement((ElementCore) syncAction.data, true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.LOCATION) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createLocation((LocationCore) syncAction.data, true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.VISIT) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createVisit((VisitCore) syncAction.data, true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.SIGHTING) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createSighting((SightingCore) syncAction.data, true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.WILDLOG_USER) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createUser((WildLogUser) syncAction.data, true));
                }
                else {
                    logIfFailed(inFeedback, syncAction, false);
                }
                syncDataDown++;
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 / 2.0 * loopCount++ / ((double) lstWorkspaceCreateEntries.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 / 2.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // Update if outdated
        if (lstWorkspaceUpdateEntries != null) {
            double loopCount = 0.0;
            for (SyncTableEntry cloudEntry : lstWorkspaceUpdateEntries) {
                SyncAction syncAction = new SyncAction("WORKSPACE_DOWNLOAD", cloudEntry.getWildLogDataType(), 
                        cloudEntry.getRecordID(), "UPDATE", cloudEntry.getData());
                if(cloudEntry.getWildLogDataType() == WildLogDataType.ELEMENT) {
                    Element oldElement = WildLogApp.getApplication().getDBI().findElement(syncAction.recordID, null, Element.class);
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateElement((ElementCore) syncAction.data, oldElement.getPrimaryName(), true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.LOCATION) {
                    Location oldLocation = WildLogApp.getApplication().getDBI().findLocation(syncAction.recordID, null, Location.class);
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateLocation((LocationCore) syncAction.data, oldLocation.getName(), true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.VISIT) {
                    Visit oldVisit = WildLogApp.getApplication().getDBI().findVisit(syncAction.recordID, null, false, Visit.class);
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateVisit((VisitCore) syncAction.data, oldVisit.getName(), true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.SIGHTING) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateSighting((SightingCore) syncAction.data, true));
                }
                else
                if(cloudEntry.getWildLogDataType() == WildLogDataType.WILDLOG_USER) {
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateUser((WildLogUser) syncAction.data, true));
                }
                else {
                    logIfFailed(inFeedback, syncAction, false);
                }
                syncDataDown++;
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 2.0 / 2.0 * loopCount++ / ((double) lstWorkspaceUpdateEntries.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
    }
    
    private void syncFileRecords(PrintWriter inFeedback, SyncAzure inSyncAzure, ProgressbarTask inProgressbar, int inProgressStepSize) {
        int baseProgress = inProgressbar.getProgress();
        List<SyncTableEntry> lstCloudEntries  = inSyncAzure.getSyncListDataBatch(WildLogDataType.FILE, 0);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - All Cloud File Entries: " + lstCloudEntries.size());
        List<WildLogFile> lstWorkspaceEntries = WildLogApp.getApplication().getDBI().listWildLogFiles(-1, null, WildLogFile.class);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - All Workspace File Entries: " + lstWorkspaceEntries.size());
        // VERIFY: Make sure that the entries in the cloud table match with the cloud blobs
        List<SyncBlobEntry> lstCloudElementBlobs  = inSyncAzure.getSyncListFileBatch(WildLogDataType.ELEMENT);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - Element Cloud Blobs: " + lstCloudElementBlobs.size());
        List<SyncBlobEntry> lstCloudLocationBlobs  = inSyncAzure.getSyncListFileBatch(WildLogDataType.LOCATION);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - Location Cloud Blobs: " + lstCloudLocationBlobs.size());
        List<SyncBlobEntry> lstCloudVisitBlobs  = inSyncAzure.getSyncListFileBatch(WildLogDataType.VISIT);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - Visit Cloud Blobs: " + lstCloudVisitBlobs.size());
        List<SyncBlobEntry> lstCloudSightingBlobs  = inSyncAzure.getSyncListFileBatch(WildLogDataType.SIGHTING);
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - Sighting Cloud Blobs: " + lstCloudSightingBlobs.size());
        Map<WildLogDataType, List<SyncBlobEntry>> mapCloudBlobs = new HashMap<>();
        mapCloudBlobs.put(WildLogDataType.ELEMENT, lstCloudElementBlobs);
        mapCloudBlobs.put(WildLogDataType.LOCATION, lstCloudLocationBlobs);
        mapCloudBlobs.put(WildLogDataType.VISIT, lstCloudVisitBlobs);
        mapCloudBlobs.put(WildLogDataType.SIGHTING, lstCloudSightingBlobs);
        boolean reloadData = false;
        double loopCount = 0.0;
        for (SyncTableEntry cloudEntry : lstCloudEntries) {
            WildLogFile cloudWildLogFile = new WildLogFile((WildLogFileCore) cloudEntry.getData());
            boolean found = false;
            for (SyncBlobEntry blobEntry : mapCloudBlobs.getOrDefault(cloudWildLogFile.getLinkType(), new ArrayList<>(0))) {
                if (cloudEntry.getRecordID() == blobEntry.getRecordID()
                        && inSyncAzure.calculateFullBlobID(cloudWildLogFile.getLinkID(), cloudWildLogFile.getID(), getResizedFile(cloudWildLogFile))
                                .equals(blobEntry.getFullBlobID())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                logIfFailed(inFeedback, new SyncAction("SYNC_FIX CLOUD_DELETE", WildLogDataType.FILE, 
                        cloudEntry.getRecordID(), cloudWildLogFile.getLinkType().getDescription(), null), 
                        inSyncAzure.deleteData(WildLogDataType.FILE, cloudEntry.getRecordID()));
                reloadData = true;
            }
            inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0 * loopCount++ / ((double) lstCloudEntries.size()))));
            inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        }
        if (reloadData) {
            lstCloudEntries  = inSyncAzure.getSyncListDataBatch(WildLogDataType.FILE, 0);
            WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.FILE.getDescription() + " - All Cloud File Entries (Reloaded): " + lstCloudEntries.size());
        }
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        loopCount = 0.0;
        double totalBlobs = lstCloudElementBlobs.size() + lstCloudLocationBlobs.size() + lstCloudVisitBlobs.size() + lstCloudSightingBlobs.size();
        for (Map.Entry<WildLogDataType, List<SyncBlobEntry>> entry : mapCloudBlobs.entrySet()) {
            for (SyncBlobEntry blobEntry : entry.getValue()) {
                boolean found = false;
                for (SyncTableEntry cloudEntry : lstCloudEntries) {
                    WildLogFile cloudWildLogFile = new WildLogFile((WildLogFileCore) cloudEntry.getData());
                    if (cloudEntry.getRecordID() == blobEntry.getRecordID()
                            && inSyncAzure.calculateFullBlobID(cloudWildLogFile.getLinkID(), cloudWildLogFile.getID(), getResizedFile(cloudWildLogFile))
                                    .equals(blobEntry.getFullBlobID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    logIfFailed(inFeedback, new SyncAction("SYNC_FIX CLOUD_DELETE_FILE", WildLogDataType.FILE, 
                            blobEntry.getRecordID(), blobEntry.getDataType().getDescription(), null), 
                            inSyncAzure.deleteFile(blobEntry.getDataType(), blobEntry.getFullBlobID()));
                }
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0 * loopCount++ / totalBlobs)));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Step Completed: Verify Files");
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // UP: Make sure cloud knows about new workspace records
        List<SyncAction> lstSyncActions = new ArrayList<>();
        for (WildLogFile workspaceEntry : lstWorkspaceEntries) {
            if (rdbSyncAllFiles.isSelected() || (rdbSyncImagesOnly.isSelected() && WildLogFileExtentions.Images.isKnownExtention(workspaceEntry.getAbsolutePath()))) {
                if (!calculateFileSizeAsSyncIndicator(inFeedback, workspaceEntry)) {
                    continue;
                }
                boolean found = false;
                boolean shouldBeSynced = false;
                for (SyncTableEntry cloudEntry : lstCloudEntries) {
                    if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                        found = true;
                        if (areWorkspaceAndCloudFilesInSync(workspaceEntry, cloudEntry) > 0) {
                            shouldBeSynced = true;
                        }
                        break;
                    }
                }
                if (!found || shouldBeSynced) {
                    lstSyncActions.add(new SyncAction("CLOUD_UPLOAD", WildLogDataType.FILE, workspaceEntry.getID(), 
                            workspaceEntry.getLinkType().getDescription(), workspaceEntry));
                }
            }
        }
        if (!lstSyncActions.isEmpty()) {
            // Note: Files are always uploaded one at a time
            loopCount = 0.0;
            for (SyncAction syncAction : lstSyncActions) {
                WildLogFile wildLogFile = (WildLogFile) syncAction.data;
                syncAction.details = wildLogFile.getLinkType().getDescription() + "_FILE";
                logIfFailed(inFeedback, syncAction, inSyncAzure.uploadFile(wildLogFile.getLinkType(), getResizedFile(wildLogFile), wildLogFile.getLinkID(), wildLogFile.getID()));
                syncAction.details = wildLogFile.getLinkType().getDescription() + "_DATA";
                logIfFailed(inFeedback, syncAction, inSyncAzure.uploadData(WildLogDataType.FILE, syncAction.data));
                syncFileUp++;
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 * loopCount++ / ((double) lstSyncActions.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Step Completed: Upload Files");
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // DOWN: Make sure the workspace knows about new cloud records
        List<Long> lstWorkspaceCreateIDs = new ArrayList<>();
        List<Long> lstWorkspaceUpdateIDs = new ArrayList<>();
        for (SyncTableEntry cloudEntry : lstCloudEntries) {
            boolean found = false;
            boolean shouldBeUpdated = false;
            for (WildLogFile workspaceEntry : lstWorkspaceEntries) {
                if (workspaceEntry.getID() == cloudEntry.getRecordID()) {
                    if (!calculateFileSizeAsSyncIndicator(inFeedback, workspaceEntry)) {
                        continue;
                    }
                    found = true;
                    if (areWorkspaceAndCloudFilesInSync(workspaceEntry, cloudEntry) < 0) {
                        shouldBeUpdated = true;
                    }
                    break;
                }
            }
            if (!found) {
                lstWorkspaceCreateIDs.add(cloudEntry.getRecordID());
            }
            if (shouldBeUpdated) {
                lstWorkspaceUpdateIDs.add(cloudEntry.getRecordID());
            }
        }
        // For the new or out of sync records, load the full record from the cloud
        List<SyncTableEntry> lstWorkspaceCreateEntries = null;
        List<SyncTableEntry> lstWorkspaceUpdateEntries = null;
        if (rdbModeBatch.isSelected()) {
            if (!lstWorkspaceCreateIDs.isEmpty()) {
                lstWorkspaceCreateEntries = inSyncAzure.downloadDataBatch(WildLogDataType.FILE, 0, lstWorkspaceCreateIDs);
            }
            if (!lstWorkspaceUpdateIDs.isEmpty()) {
                lstWorkspaceUpdateEntries = inSyncAzure.downloadDataBatch(WildLogDataType.FILE, 0, lstWorkspaceUpdateIDs);
            }
        }
        else {
            lstWorkspaceCreateEntries = new ArrayList<>(lstWorkspaceCreateIDs.size());
            for (long recordId : lstWorkspaceCreateIDs) {
                lstWorkspaceCreateEntries.add(inSyncAzure.downloadData(WildLogDataType.FILE, recordId));
            }
            lstWorkspaceUpdateEntries = new ArrayList<>(lstWorkspaceUpdateIDs.size());
            for (long recordId : lstWorkspaceUpdateIDs) {
                lstWorkspaceUpdateEntries.add(inSyncAzure.downloadData(WildLogDataType.FILE, recordId));
            }
        }
        // Check the the downloads were successful
        if (!lstWorkspaceCreateIDs.isEmpty() && (lstWorkspaceCreateEntries == null || lstWorkspaceCreateEntries.size() != lstWorkspaceCreateIDs.size())) {
            logIfFailed(inFeedback, new SyncAction("CLOUD_DOWNLOAD", WildLogDataType.FILE, lstWorkspaceCreateIDs.size(), Arrays.toString(lstWorkspaceCreateIDs.toArray()), null), false);
        }
        if (!lstWorkspaceUpdateIDs.isEmpty() && (lstWorkspaceUpdateEntries == null || lstWorkspaceUpdateEntries.size() != lstWorkspaceUpdateIDs.size())) {
            logIfFailed(inFeedback, new SyncAction("CLOUD_DOWNLOAD", WildLogDataType.FILE, lstWorkspaceUpdateIDs.size(), Arrays.toString(lstWorkspaceUpdateIDs.toArray()), null), false);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Step Completed: Download Files Preperation");
        // Create if not found
        if (lstWorkspaceCreateEntries != null) {
            loopCount = 0.0;
            for (SyncTableEntry cloudEntry : lstWorkspaceCreateEntries) {
                WildLogFile cloudWildLogFile = new WildLogFile((WildLogFileCore) cloudEntry.getData());
                if (rdbSyncAllFiles.isSelected() || (rdbSyncImagesOnly.isSelected() 
                        && WildLogFileExtentions.Images.isKnownExtention(cloudWildLogFile.getAbsolutePath()))) {
                    // Don't overwrite existing files
                    if (Files.exists(cloudWildLogFile.getAbsolutePath())) {
                        // There is already a file on the disk with the same path, thus we need to rename this one
                        while (Files.exists(cloudWildLogFile.getAbsolutePath())) {
                            cloudWildLogFile.setDBFilePath(cloudWildLogFile.getRelativePath().getParent().resolve("wlsync_" + cloudWildLogFile.getRelativePath().getFileName()).toString());
                            WildLogApp.LOGGER.log(Level.INFO, "Renaming sync file before downloading: " + cloudWildLogFile.getAbsolutePath().toString());
                        }
                    }
                    // Download the file
                    SyncAction syncAction = new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, cloudWildLogFile.getID(), "", cloudWildLogFile);
                    syncAction.details = cloudWildLogFile.getLinkType().getDescription() + "_NEW_FILE";
                    logIfFailed(inFeedback, syncAction, inSyncAzure.downloadFile(cloudWildLogFile.getLinkType(), cloudWildLogFile.getAbsolutePath(), cloudWildLogFile.getLinkID(), cloudWildLogFile.getID()));
                    // Check the file size
                    long cloudFileSize = cloudWildLogFile.getSyncIndicator();
                    calculateFileSizeAsSyncIndicator(inFeedback, cloudWildLogFile); // Get the new file size
                    if (cloudFileSize != cloudWildLogFile.getSyncIndicator()) {
                        logIfFailed(inFeedback, new SyncAction("SYNC_FAIL", WildLogDataType.FILE, cloudWildLogFile.getID(), 
                                "[Incorrect Size: " + cloudFileSize + " != "+ Long.toString(cloudWildLogFile.getSyncIndicator()) + "]", cloudWildLogFile), false);
                        continue;
                    }
                    // Download and save the workspace data record if all seems correct
                    syncAction.details = cloudWildLogFile.getLinkType().getDescription() + "_NEW_DATA";
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().createWildLogFile(cloudWildLogFile, true));
                    syncFileDown++;
                }
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0 * loopCount++ / ((double) lstWorkspaceCreateEntries.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Step Completed: Download Files Create");
        inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0)));
        inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
        baseProgress = inProgressbar.getProgress();
        // Update if outdated
        if (lstWorkspaceUpdateEntries != null) {
            loopCount = 0.0;
            for (SyncTableEntry cloudEntry : lstWorkspaceUpdateEntries) {
                WildLogFile cloudWildLogFile = new WildLogFile((WildLogFileCore) cloudEntry.getData());
                if (rdbSyncAllFiles.isSelected() || (rdbSyncImagesOnly.isSelected() 
                        && WildLogFileExtentions.Images.isKnownExtention(cloudWildLogFile.getAbsolutePath()))) {
                    // Don't overwrite existing files, unless it is the same file that is being replaced
                    Path oldPathToDelete = null;
                    if (Files.exists(cloudWildLogFile.getAbsolutePath())) {
                        if (cloudWildLogFile.getID() != WildLogApp.getApplication().getDBI().findWildLogFile(0, 0, null, cloudWildLogFile.getDBFilePath(), WildLogFile.class).getID()) {
                            // There is already a different file on the disk with the same path, thus we need to rename this one
                            while (Files.exists(cloudWildLogFile.getAbsolutePath())) {
                                cloudWildLogFile.setDBFilePath(cloudWildLogFile.getRelativePath().getParent().resolve("wlsync_" + cloudWildLogFile.getRelativePath().getFileName()).toString());
                                WildLogApp.LOGGER.log(Level.INFO, "Renaming sync file before downloading: " + cloudWildLogFile.getAbsolutePath().toString());
                            }
                            // Get the path of the exisitng file that will be synced (to delete it afterwards, because the downloaded file will end up in a different path)
                            oldPathToDelete = WildLogApp.getApplication().getDBI().findWildLogFile(cloudWildLogFile.getID(), 0, null, null, WildLogFile.class).getAbsolutePath();
                        }
                    }
                    else {
                        // Get the path of the exisitng file that will be synced (to delete it afterwards, because the downloaded file will end up in a different path)
                        oldPathToDelete = WildLogApp.getApplication().getDBI().findWildLogFile(cloudWildLogFile.getID(), 0, null, null, WildLogFile.class).getAbsolutePath();
                    }
                    // Download the file
                    SyncAction syncAction = new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, cloudWildLogFile.getID(), "", cloudWildLogFile);
                    syncAction.details = cloudWildLogFile.getLinkType().getDescription() + "_UPDATE_FILE";
                    logIfFailed(inFeedback, syncAction, inSyncAzure.downloadFile(cloudWildLogFile.getLinkType(), cloudWildLogFile.getAbsolutePath(), cloudWildLogFile.getLinkID(), cloudWildLogFile.getID()));
                    // Check the file size
                    long cloudFileSize = cloudWildLogFile.getSyncIndicator();
                    calculateFileSizeAsSyncIndicator(inFeedback, cloudWildLogFile); // Get the new file size
                    if (cloudFileSize != cloudWildLogFile.getSyncIndicator()) {
                        logIfFailed(inFeedback, new SyncAction("SYNC_FAIL", WildLogDataType.FILE, cloudWildLogFile.getID(), 
                                "[Incorrect Size: " + cloudFileSize + " != "+ Long.toString(cloudWildLogFile.getSyncIndicator()) + "]", cloudWildLogFile), false);
                        continue;
                    }
                    // Download and save the workspace data record if all seems correct
                    syncAction.details = cloudWildLogFile.getLinkType().getDescription() + "_UPDATE_DATA";
                    logIfFailed(inFeedback, syncAction, WildLogApp.getApplication().getDBI().updateWildLogFile(cloudWildLogFile, true));
                    // Delete the old file (if is wasn't replaced)
                    if (oldPathToDelete != null) {
                        try {
                            Files.delete(oldPathToDelete);
                        }
                        catch (IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                    syncFileDown++;
                }
                inProgressbar.setTaskProgress(baseProgress + ((int) (((double) inProgressStepSize) / 3.0 / 2.0 * loopCount++ / ((double) lstWorkspaceUpdateEntries.size()))));
                inProgressbar.setMessage(inProgressbar.getMessage().substring(0, inProgressbar.getMessage().lastIndexOf(' ') + 1) + inProgressbar.getProgress() + "%");
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Step Completed: Download Files Update");
    }

    private Path getResizedFile(WildLogFile inWildLogFile) {
        if (rdbSyncThumbnails.isSelected()) {
            if (WildLogFileExtentions.Images.isKnownExtention(inWildLogFile.getAbsolutePath())) {
                return inWildLogFile.getAbsoluteThumbnailPath((WildLogThumbnailSizes) cmbThumbnailSize.getSelectedItem());
            }
        }
        return inWildLogFile.getAbsolutePath();
    }
    
    private boolean calculateFileSizeAsSyncIndicator(PrintWriter inFeedback, WildLogFile inWorkspaceFile) {
        try {
            inWorkspaceFile.setSyncIndicator(Files.size(getResizedFile(inWorkspaceFile)));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            logIfFailed(inFeedback, new SyncAction("SYNC_FAIL", WildLogDataType.FILE, inWorkspaceFile.getID(), Long.toString(inWorkspaceFile.getLinkID()), inWorkspaceFile), false);
            return false;
        }
        return true;
    }
    
    private int areWorkspaceAndCloudFilesInSync(WildLogFile inWorkspaceFile, SyncTableEntry inCloudFile) {
        WildLogFile cloudWildLogFile = new WildLogFile((WildLogFileCore) inCloudFile.getData());
        // Note: Thumbnails will always be JPG files
        if (WildLogFileExtentions.Images.isJPG(inWorkspaceFile.getAbsolutePath())
                && WildLogFileExtentions.Images.isJPG(cloudWildLogFile.getAbsolutePath())) {
            // Both are JPGs, so now compare the workspace file's size (or its previosuly calculated thumbnail size) to the cloud file's size
            return Long.compare(inWorkspaceFile.getSyncIndicator(), inCloudFile.getData().getSyncIndicator());
        }
        else {
            // If both files aren't JPGs, then the cloud file might still be a JPG (for example a thumbnail of a PNG)
            if (WildLogFileExtentions.Images.isJPG(cloudWildLogFile.getAbsolutePath())
                    && WildLogFileExtentions.Images.isKnownExtention(inWorkspaceFile.getAbsolutePath())) {
                // If the thumbnail option is selected then compare on thumbnail sizes
                if (rdbSyncThumbnails.isSelected()) {
                    // Compare the workspace file's size (it should have been previosuly calculated as a JPG thumbnail size) to the cloud file's size
                    return Long.compare(inWorkspaceFile.getSyncIndicator(), inCloudFile.getData().getSyncIndicator());
                }
                // Otherwise assume that the workspace file is the original, so it takes priority
                else {
                    return -1;
                }
            }
            // Note: It should not be possible to have the case where the cloud is not JPG, but the workspace is, however I'll code for it...
            else if (WildLogFileExtentions.Images.isJPG(inWorkspaceFile.getAbsolutePath())
                    && WildLogFileExtentions.Images.isKnownExtention(cloudWildLogFile.getAbsolutePath())) {
                // Choose the bigger file (since I don't know enough to determine which to use)
                return Long.compare(inWorkspaceFile.getSyncIndicator(), inCloudFile.getData().getSyncIndicator());
            }
        }
        // If none of the special above conditions were triggered, then assume these are other binary files and simply compare the size
        return Long.compare(inWorkspaceFile.getSyncIndicator(), inCloudFile.getData().getSyncIndicator());
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnConfirmSyncToken;
    private javax.swing.JComboBox<WildLogThumbnailSizes> cmbThumbnailSize;
    private javax.swing.ButtonGroup grpFiles;
    private javax.swing.ButtonGroup grpImages;
    private javax.swing.ButtonGroup grpMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel pnlSyncOptions;
    private javax.swing.JPanel pnlSyncToken;
    private javax.swing.JRadioButton rdbModeBatch;
    private javax.swing.JRadioButton rdbModeSingle;
    private javax.swing.JRadioButton rdbSyncAllFiles;
    private javax.swing.JRadioButton rdbSyncImagesOnly;
    private javax.swing.JRadioButton rdbSyncNoFiles;
    private javax.swing.JRadioButton rdbSyncOriginalImages;
    private javax.swing.JRadioButton rdbSyncThumbnails;
    private javax.swing.JTextArea txaSyncToken;
    // End of variables declaration//GEN-END:variables
}
