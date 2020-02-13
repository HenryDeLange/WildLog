package wildlog.ui.dialogs;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsTableGenerator;


public class ExtraDataDialog extends JDialog {
    private final long linkID;
    private final WildLogDataType linkType;

// TODO: Add to check and clean
// TODO: Add to sync
// TODO: Add to import/export
    
    public ExtraDataDialog(JFrame inParent, long inLinkID, WildLogDataType inLinkType) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[ExtraDataDialog]");
        linkID = inLinkID;
        linkType = inLinkType;
        // Auto generated code
        initComponents();
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        // Tell the table to stop the editing when focus is lost
        tblExtraData.putClientProperty("terminateEditOnFocusLost", true);
        // Load initial data
        UtilsTableGenerator.setupExtraDataTable(WildLogApp.getApplication(), tblExtraData, inLinkID, inLinkType);
        // Always add an empty row for new records
        setupEmptyRow();
        // Note: Using invokeLater because it needs to happen after the setupExtraDataTable has set the model on the table
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((DefaultTableModel) tblExtraData.getModel()).addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent inEvent) {
                        setupEmptyRow();
                    }
                });
                }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSave = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExtraData = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Extra Data");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Extra.png")).getImage());
        setMinimumSize(new java.awt.Dimension(400, 300));
        setModal(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSave.setToolTipText("Close this dialog.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        tblExtraData.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblExtraData.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblExtraData.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblExtraData);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        WildLogApp app = WildLogApp.getApplication();
        for (int r = 0; r < tblExtraData.getRowCount(); r++) {
            ExtraData extraData = new ExtraData(WildLogExtraDataFieldTypes.USER, linkID, linkType, 
                    (String) tblExtraData.getModel().getValueAt(r, 0), (String) tblExtraData.getModel().getValueAt(r, 1));
            extraData.setID((long) tblExtraData.getModel().getValueAt(r, 2));
            if (extraData.getDataKey().isEmpty()) {
                if (extraData.getID() != 0) {
                    app.getDBI().deleteExtraData(extraData.getID());
                }
            }
            else {
                if (extraData.getID() == 0) {
                    app.getDBI().createExtraData(extraData, false);
                }
                else {
                    ExtraData oldExtraData = app.getDBI().findExtraData(extraData.getID(), null, 0, null, ExtraData.class);
                    if (!extraData.getDataKey().equals(oldExtraData.getDataKey()) || !extraData.getDataValue().equals(oldExtraData.getDataValue())) {
                        app.getDBI().updateExtraData(extraData, false);
                    }
                }
            }
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void setupEmptyRow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean found = false;
                for (int r = 0; r < tblExtraData.getRowCount(); r++) {
                    if (((String) tblExtraData.getModel().getValueAt(r, 0)).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ((DefaultTableModel) tblExtraData.getModel()).addRow(new Object[] {"", "", 0L});
                }
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblExtraData;
    // End of variables declaration//GEN-END:variables
}
