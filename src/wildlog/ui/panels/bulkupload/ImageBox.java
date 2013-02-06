package wildlog.ui.panels.bulkupload;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class ImageBox extends JPanel {
    private BulkUploadImageFileWrapper imageWrapper;
    private JTable table;

    /** Creates new form ImageBox */
    public ImageBox(BulkUploadImageFileWrapper inBulkUploadImageFileWrapper, JTable inTable) {
        initComponents();
        imageWrapper = inBulkUploadImageFileWrapper;
        table = inTable;
        populateUI();
        imageWrapper.setImageBox(this);
    }

    public final void populateUI() {
        // Setup the image label
        lblImage.setIcon(imageWrapper.getIcon());
        lblImage.setToolTipText(imageWrapper.getFile().getName());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblImage = new JLabel() {
            public Point getToolTipLocation(MouseEvent e) {
                Rectangle rectangle = getBounds();
                return new Point(rectangle.width, rectangle.y);
            }
        };
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnNewSighting = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();

        setBackground(new java.awt.Color(235, 246, 220));
        setName("Form"); // NOI18N
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setOpaque(true);
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });
        add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 200, 200));

        btnUp.setBackground(new java.awt.Color(235, 246, 220));
        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/up.png"))); // NOI18N
        btnUp.setToolTipText("Move the image UP to the Observation above.");
        btnUp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUp.setFocusPainted(false);
        btnUp.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnUp.setName("btnUp"); // NOI18N
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });
        add(btnUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 5, 30, 100));

        btnDown.setBackground(new java.awt.Color(235, 246, 220));
        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/down.png"))); // NOI18N
        btnDown.setToolTipText("Move the image DOWN to the Observation below.");
        btnDown.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDown.setFocusPainted(false);
        btnDown.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnDown.setName("btnDown"); // NOI18N
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });
        add(btnDown, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 105, 30, 100));

        btnRemove.setBackground(new java.awt.Color(235, 246, 220));
        btnRemove.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnRemove.setText("Remove");
        btnRemove.setToolTipText("Remove the image from this Observation.");
        btnRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemove.setFocusPainted(false);
        btnRemove.setIconTextGap(2);
        btnRemove.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        add(btnRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 205, 70, 30));

        btnNewSighting.setBackground(new java.awt.Color(235, 246, 220));
        btnNewSighting.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnNewSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting Small.gif"))); // NOI18N
        btnNewSighting.setText("Observation");
        btnNewSighting.setToolTipText("Move the image into its own NEW Observation.");
        btnNewSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewSighting.setFocusPainted(false);
        btnNewSighting.setIconTextGap(2);
        btnNewSighting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNewSighting.setName("btnNewSighting"); // NOI18N
        btnNewSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewSightingActionPerformed(evt);
            }
        });
        add(btnNewSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 205, 90, 30));

        btnClone.setBackground(new java.awt.Color(235, 246, 220));
        btnClone.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnClone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnClone.setText("Duplicate");
        btnClone.setToolTipText("Duplicate the image for this Observation.");
        btnClone.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClone.setFocusPainted(false);
        btnClone.setIconTextGap(2);
        btnClone.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClone.setName("btnClone"); // NOI18N
        btnClone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloneActionPerformed(evt);
            }
        });
        add(btnClone, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 205, 70, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(imageWrapper.getFile().getAbsolutePath());
    }//GEN-LAST:event_lblImageMouseReleased

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        if (table.getEditingRow() > 0) {
            moveImageToNewRow(-1);
        }
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        if (table.getEditingRow() < (table.getRowCount()-1)) {
            moveImageToNewRow(+1);
        }
    }//GEN-LAST:event_btnDownActionPerformed

    private void btnNewSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewSightingActionPerformed
        // Make sure to call stop editing after getting the row and col
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        table.getCellEditor().stopCellEditing();
        // Perform the add and remember to let the model know
        DefaultTableModel model = ((DefaultTableModel)table.getModel());
        BulkUploadSightingWrapper currentSightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(row, 0);
        BulkUploadSightingWrapper newSightingWrapper = new BulkUploadSightingWrapper(UtilsImageProcessing.getScaledIconForNoImage(150));
        newSightingWrapper.setDate(imageWrapper.getDate());
        BulkUploadImageListWrapper currentListWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, col);
        currentListWrapper.getImageList().remove(imageWrapper);
        if (currentListWrapper.getImageList().isEmpty()) {
            model.removeRow(row);
        }
        else {
            model.fireTableCellUpdated(row, col);
        }
        BulkUploadImageListWrapper newListWrapper = new BulkUploadImageListWrapper();
        newListWrapper.getImageList().add(imageWrapper);
        if (imageWrapper.getDate().before(currentSightingWrapper.getDate())) {
            model.insertRow(row, new Object[]{newSightingWrapper, newListWrapper});
        }
        else {
            model.insertRow(row + 1, new Object[]{newSightingWrapper, newListWrapper});
        }
    }//GEN-LAST:event_btnNewSightingActionPerformed

    private void btnCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloneActionPerformed
        // Make sure to call stop editing after getting the row and col
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        table.getCellEditor().stopCellEditing();
        // Perform the add and remember to let the model know
        DefaultTableModel model = ((DefaultTableModel)table.getModel());
        BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, col);
        listWrapper.getImageList().add(imageWrapper.getClone());
        model.fireTableCellUpdated(row, col);
    }//GEN-LAST:event_btnCloneActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // Make sure to call stop editing after getting the row and col
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        table.getCellEditor().stopCellEditing();
        // Remove the current image from the row
        DefaultTableModel model = ((DefaultTableModel)table.getModel());
        BulkUploadImageListWrapper currentListWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, col);
        currentListWrapper.getImageList().remove(imageWrapper);
        if (currentListWrapper.getImageList().isEmpty()) {
            model.removeRow(row);
        }
        else
            model.fireTableCellUpdated(row, col);
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void moveImageToNewRow(int inDelta) {
        // Make sure to call stop editing after getting the row and col
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        table.getCellEditor().stopCellEditing();
        // Perform the move and remember to let the model know
        DefaultTableModel model = ((DefaultTableModel)table.getModel());
        // Add the image to the new row
        BulkUploadImageListWrapper newListWrapper = (BulkUploadImageListWrapper)model.getValueAt(row + inDelta, col);
        newListWrapper.getImageList().add(imageWrapper);
        model.fireTableCellUpdated(row + inDelta, col);
        // Remove the current image from the row
        BulkUploadImageListWrapper currentListWrapper = (BulkUploadImageListWrapper)model.getValueAt(row, col);
        currentListWrapper.getImageList().remove(imageWrapper);
        if (currentListWrapper.getImageList().isEmpty()) {
            model.removeRow(row);
        }
        else
            model.fireTableCellUpdated(row, col);
    }

    public JTable getTable() {
        return table;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnNewSighting;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables
}
