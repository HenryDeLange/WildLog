

import java.awt.Color;
import java.awt.EventQueue;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import wildlog.data.dbi.DBI_JDBC;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.sync.azure.SyncAzure;
import wildlog.sync.azure.dataobjects.SyncBlobEntry;

public class BlobTest extends JFrame {
    private SyncAzure syncAzure = new SyncAzure(
            "DefaultEndpointsProtocol=https;AccountName=wildlogtest;AccountKey=HHpe/UN5isNNVth/tJ1+b9ZzIf0U9yL/rbnmzsp8Rjq1J2HQ+AKmm5VekWNbrLvueXjS3VojW7Ck9bJsRvtROA==;EndpointSuffix=core.windows.net", 
            "wildlogtest", "HHpe/UN5isNNVth/tJ1+b9ZzIf0U9yL/rbnmzsp8Rjq1J2HQ+AKmm5VekWNbrLvueXjS3VojW7Ck9bJsRvtROA==", 
            123L, DBI_JDBC.WILDLOG_DB_VERSION);

    public BlobTest() {
        initComponents();
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnUploadFile = new javax.swing.JButton();
        btnDownloadFile = new javax.swing.JButton();
        btnDeleteFile = new javax.swing.JButton();
        btnSyncListBlobsBatch = new javax.swing.JButton();
        btnSyncListParentsBatch = new javax.swing.JButton();
        btnSyncListChildrenBatch = new javax.swing.JButton();
        btnDeleteWorkspace = new javax.swing.JButton();
        btnUploadText = new javax.swing.JButton();
        btnDownloadText = new javax.swing.JButton();
        btnDeleteText = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Azure Sync Tester");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Azure Blob Sync Tester");

        btnUploadFile.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUploadFile.setText("Upload File");
        btnUploadFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadFileActionPerformed(evt);
            }
        });

        btnDownloadFile.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDownloadFile.setText("Download File");
        btnDownloadFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadFileActionPerformed(evt);
            }
        });

        btnDeleteFile.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDeleteFile.setText("Delete File");
        btnDeleteFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteFileActionPerformed(evt);
            }
        });

        btnSyncListBlobsBatch.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSyncListBlobsBatch.setText("Sync List (Blobs)");
        btnSyncListBlobsBatch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSyncListBlobsBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncListBlobsBatchActionPerformed(evt);
            }
        });

        btnSyncListParentsBatch.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSyncListParentsBatch.setText("Sync List (Parents)");
        btnSyncListParentsBatch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSyncListParentsBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncListParentsBatchActionPerformed(evt);
            }
        });

        btnSyncListChildrenBatch.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSyncListChildrenBatch.setText("Sync List (Children)");
        btnSyncListChildrenBatch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSyncListChildrenBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSyncListChildrenBatchActionPerformed(evt);
            }
        });

        btnDeleteWorkspace.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDeleteWorkspace.setText("Delete Workspace");
        btnDeleteWorkspace.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteWorkspaceActionPerformed(evt);
            }
        });

        btnUploadText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUploadText.setText("Upload Text");
        btnUploadText.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadTextActionPerformed(evt);
            }
        });

        btnDownloadText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDownloadText.setText("Download Text");
        btnDownloadText.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDownloadText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadTextActionPerformed(evt);
            }
        });

        btnDeleteText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDeleteText.setText("Delete Text");
        btnDeleteText.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSyncListBlobsBatch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSyncListParentsBatch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSyncListChildrenBatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeleteWorkspace, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnDeleteFile, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .addComponent(btnDownloadFile, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .addComponent(btnUploadFile, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnDownloadText, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .addComponent(btnDeleteText, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .addComponent(btnUploadText, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
                        .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUploadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUploadText, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnDownloadText, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteFile, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteText, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(btnDeleteWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnSyncListBlobsBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnSyncListParentsBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnSyncListChildrenBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadFileActionPerformed
        syncAzure.uploadFile(WildLogDataType.ELEMENT, new File("C:\\_temp\\mol.jpg").toPath(), 777L, Long.toString(555L), 
                "2019:07:25 14:27:00", "-33° 45' 0.62\"", "26° 31' 46.68\"");
        System.out.println("UPLOADED FILE");
    }//GEN-LAST:event_btnUploadFileActionPerformed

    private void btnDownloadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadFileActionPerformed
        syncAzure.downloadFile(WildLogDataType.ELEMENT, new File("C:\\_temp\\mol.jpg").toPath(), 777L, Long.toString(555L));
        System.out.println("DOWNLOADED FILE");
    }//GEN-LAST:event_btnDownloadFileActionPerformed

    private void btnDeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteFileActionPerformed
        syncAzure.deleteFileOrText(WildLogDataType.ELEMENT, Long.toString(123L) + "/" + Long.toString(777L) + "/" + Long.toString(55L) + ".jpg");
        System.out.println("DELETED FILE");
    }//GEN-LAST:event_btnDeleteFileActionPerformed

    private void btnSyncListBlobsBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSyncListBlobsBatchActionPerformed
        List<SyncBlobEntry> lstSyncBlobEntry = syncAzure.getSyncListFilesBatch(WildLogDataType.ELEMENT);
        System.out.println("SYNCLIST BLOBS BATCH: ");
        System.out.println(lstSyncBlobEntry.size());
        for (SyncBlobEntry entry : lstSyncBlobEntry) {
            System.out.println(entry);
        }
    }//GEN-LAST:event_btnSyncListBlobsBatchActionPerformed

    private void btnSyncListParentsBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSyncListParentsBatchActionPerformed
        List<SyncBlobEntry> lstSyncBlobEntry = syncAzure.getSyncListFileParentsBatch(WildLogDataType.ELEMENT);
        System.out.println("SYNCLIST PARENTS BATCH: ");
        System.out.println(lstSyncBlobEntry.size());
        for (SyncBlobEntry entry : lstSyncBlobEntry) {
            System.out.println(entry);
        }
    }//GEN-LAST:event_btnSyncListParentsBatchActionPerformed

    private void btnSyncListChildrenBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSyncListChildrenBatchActionPerformed
        List<SyncBlobEntry> lstSyncBlobEntry = syncAzure.getSyncListFileChildrenBatch(WildLogDataType.ELEMENT, 777L);
        System.out.println("SYNCLIST CHILDREN BATCH: ");
        System.out.println(lstSyncBlobEntry.size());
        for (SyncBlobEntry entry : lstSyncBlobEntry) {
            System.out.println(entry);
        }
    }//GEN-LAST:event_btnSyncListChildrenBatchActionPerformed

    private void btnDeleteWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteWorkspaceActionPerformed
        syncAzure.workspaceDeleteFiles(WildLogDataType.ELEMENT);
        System.out.println("DELETED WORKSPACE");
    }//GEN-LAST:event_btnDeleteWorkspaceActionPerformed

    private void btnUploadTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadTextActionPerformed
        syncAzure.uploadText(WildLogDataType.EXTRA, "test test test", 777L, Long.toString(888L));
        System.out.println("UPLOADED TEXT");
    }//GEN-LAST:event_btnUploadTextActionPerformed

    private void btnDownloadTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadTextActionPerformed
        String text = syncAzure.downloadText(WildLogDataType.EXTRA, 777L, Long.toString(888L));
        System.out.println("text = " + text);
        System.out.println("DOWNLOADED TEXT");
    }//GEN-LAST:event_btnDownloadTextActionPerformed

    private void btnDeleteTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTextActionPerformed
        syncAzure.deleteFileOrText(WildLogDataType.EXTRA, Long.toString(123L) + "/" + Long.toString(777L) + "/" + Long.toString(888L) + ".txt");
        System.out.println("DELETED TEXT");
    }//GEN-LAST:event_btnDeleteTextActionPerformed

    public static void main(String args[]) {
        // Setup the application
        try {
            // Set native Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Removes the dotted border around controls which is not consistent with Windows
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("ToggleButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("RadioButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Slider.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("ComboBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        // Launch the application
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BlobTest().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteFile;
    private javax.swing.JButton btnDeleteText;
    private javax.swing.JButton btnDeleteWorkspace;
    private javax.swing.JButton btnDownloadFile;
    private javax.swing.JButton btnDownloadText;
    private javax.swing.JButton btnSyncListBlobsBatch;
    private javax.swing.JButton btnSyncListChildrenBatch;
    private javax.swing.JButton btnSyncListParentsBatch;
    private javax.swing.JButton btnUploadFile;
    private javax.swing.JButton btnUploadText;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
