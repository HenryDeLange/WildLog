package wildlog.ui.dialogs;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.UtilsTime;


public class WorkspaceImportConflictDialog extends JDialog {
    private ResolvedRecord selectedRecord = null;


    public WorkspaceImportConflictDialog(DataObjectWithHTML inImportRecord, DataObjectWithHTML inWorkspaceRecord) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceImportConflictDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Setup UI
        txpImport.setText(inImportRecord.toHTML(false, false, false, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForHTML, null)
                .replace("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>", ""));
        txpWorkspace.setText(inWorkspaceRecord.toHTML(false, false, false, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForHTML, null)
                .replace("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>", ""));
    }
    
    public WorkspaceImportConflictDialog(Path inImportRecord, Path inWorkspaceRecord) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceImportConflictDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Setup UI
        txpImport.insertIcon(UtilsImageProcessing.getScaledIcon(inImportRecord, 400, true));
        txpWorkspace.insertIcon(UtilsImageProcessing.getScaledIcon(inWorkspaceRecord, 400, true));
        try {
            txpImport.setToolTipText("Size: " + Files.size(inImportRecord));
            txpWorkspace.setToolTipText("Size: " + Files.size(inWorkspaceRecord));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        txpImport.setBackground(Color.BLACK);
        txpWorkspace.setBackground(Color.BLACK);
    }
    
    public WorkspaceImportConflictDialog(ExtraData inImportRecord, ExtraData inWorkspaceRecord) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceImportConflictDialog]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Setup UI
        txpImport.setText(extraDataToHTML(inImportRecord));
        txpWorkspace.setText(extraDataToHTML(inWorkspaceRecord));
    }
    
    private String extraDataToHTML(ExtraData inExtraData) {
        StringBuilder html = new StringBuilder("<head><title>ExtraData: ").append(inExtraData.getDataKey()).append("</title></head>");
        html.append("<body>");
        html.append("<table width='100%'>");
        html.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Field Type:</b><br/>", inExtraData.getFieldType(), true);
        html.append("<br/><hr/>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Key:</b><br/>", inExtraData.getDataKey(), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Value:</b><br/>", inExtraData.getDataValue(), true);
        html.append("<br/><hr/>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Link Type:</b><br/>", inExtraData.getLinkType(), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Link ID:</b><br/>", inExtraData.getLinkID(), true);
        html.append("<br/><hr/>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>ID:</b><br/>", inExtraData.getID(), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Audit Time:</b><br/>", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(
                UtilsTime.getLocalDateTimeFromMilliseconds(inExtraData.getAuditTime())), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Audit User:</b><br/>", inExtraData.getAuditUser(), true);
        html.append("</td></tr>");
        html.append("</table>");
        html.append("<br/>");
        html.append("</body>");
        return html.toString();
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
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        btnUseImport = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txpImport = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txpWorkspace = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        btnUseWorkspace = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Conflict");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small Selected.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(900, 650));
        setModal(true);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Choose how to resolve the conflict:");
        jLabel3.setName("jLabel3"); // NOI18N

        jSplitPane1.setDividerLocation(440);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        btnUseImport.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUseImport.setText("Choose the record being imported");
        btnUseImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseImport.setName("btnUseImport"); // NOI18N
        btnUseImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseImportActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txpImport.setEditable(false);
        txpImport.setContentType("text/html"); // NOI18N
        txpImport.setText("");
        txpImport.setName("txpImport"); // NOI18N
        jScrollPane1.setViewportView(txpImport);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Import Record");
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUseImport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(btnUseImport, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txpWorkspace.setEditable(false);
        txpWorkspace.setContentType("text/html"); // NOI18N
        txpWorkspace.setName("txpWorkspace"); // NOI18N
        jScrollPane3.setViewportView(txpWorkspace);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Workspace Record");
        jLabel2.setName("jLabel2"); // NOI18N

        btnUseWorkspace.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUseWorkspace.setText("Keep the record already in the Workspace");
        btnUseWorkspace.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUseWorkspace.setName("btnUseWorkspace"); // NOI18N
        btnUseWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseWorkspaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUseWorkspace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3)
                .addGap(5, 5, 5)
                .addComponent(btnUseWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE)
                        .addGap(5, 5, 5))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(jSplitPane1)
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUseImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseImportActionPerformed
        selectedRecord = ResolvedRecord.IMPORT;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnUseImportActionPerformed

    private void btnUseWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseWorkspaceActionPerformed
        selectedRecord = ResolvedRecord.WORKSPACE;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnUseWorkspaceActionPerformed

    public ResolvedRecord getSelectedRecord() {
        return selectedRecord;
    }
    
    public static enum ResolvedRecord {
        IMPORT, WORKSPACE;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUseImport;
    private javax.swing.JButton btnUseWorkspace;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextPane txpImport;
    private javax.swing.JTextPane txpWorkspace;
    // End of variables declaration//GEN-END:variables
}
