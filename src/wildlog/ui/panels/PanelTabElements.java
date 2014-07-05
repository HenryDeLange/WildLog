package wildlog.ui.panels;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class PanelTabElements extends javax.swing.JPanel {
    private final WildLogApp app;
    private final JTabbedPane tabbedPanel;
    private Element searchElement;

    public PanelTabElements(WildLogApp inApp, JTabbedPane inTabbedPanel) {
        app = inApp;
        tabbedPanel = inTabbedPanel;
        searchElement = new Element();
        initComponents();
        // Add key listeners to table to allow the selection of rows based on key events.
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        // Add listner to auto resize columns.
        UtilsTableGenerator.setupColumnResizingListener(tblLocation, 1);
        UtilsTableGenerator.setupColumnResizingListener(tblElement, 1);
        // Add key listener for textfields to auto search the tables
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtSearch);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrlElement = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        btnGoElement = new javax.swing.JButton();
        btnAddElement = new javax.swing.JButton();
        btnDeleteElement = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        cmbType = new javax.swing.JComboBox();
        btnGoLocation = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(201, 218, 199));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        scrlElement.setViewportView(tblElement);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("List of Places where the selected Creature has been observed:");

        btnGoElement.setBackground(new java.awt.Color(206, 219, 206));
        btnGoElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoElement.setToolTipText("Open a tab for the selected Creature.");
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setFocusPainted(false);
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });

        btnAddElement.setBackground(new java.awt.Color(206, 219, 206));
        btnAddElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddElement.setToolTipText("Open a tab for a new Creature to be added.");
        btnAddElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddElement.setFocusPainted(false);
        btnAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddElementActionPerformed(evt);
            }
        });

        btnDeleteElement.setBackground(new java.awt.Color(206, 219, 206));
        btnDeleteElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteElement.setToolTipText("<html>Delete the selected Creature. <br/>This will delete all linked Observations and files as well.</html>");
        btnDeleteElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteElement.setFocusPainted(false);
        btnDeleteElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteElementActionPerformed(evt);
            }
        });

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocationMouseClicked(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(tblLocation);

        cmbType.setMaximumRowCount(9);
        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setSelectedItem(ElementType.NONE);
        cmbType.setFocusable(false);
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeActionPerformed(evt);
            }
        });

        btnGoLocation.setBackground(new java.awt.Color(206, 219, 206));
        btnGoLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoLocation.setToolTipText("Open a new tab for the selected Location.");
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setFocusPainted(false);
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setOpaque(true);
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("All Creatures:");

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Search for a Creature:");

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteElement, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrlElement))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGoLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(13, 13, 13)
                        .addComponent(btnGoElement, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteElement, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrlElement, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            if (!(evt instanceof UtilsUI.GeneratedMouseEvent)) {
                tblLocation.clearSelection();
            }
            // Get Image
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1)));
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempElement.getWildLogFileID()));
            if (fotos.size() > 0) {
                UtilsImageProcessing.setupFoto(tempElement.getWildLogFileID(), 0, lblImage, WildLogThumbnailSizes.NORMAL, app);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
            }
            // Get Locations
            UtilsTableGenerator.setupLocationsTableMedium(app, tblLocation, tempElement);
        }
        else {
            UtilsTableGenerator.setupLocationsTableMedium(app, tblLocation, null);
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
            if (tblElement.getSelectedRowCount() == 0) {
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Creature Selected"}, 0));
            }
            else {
                tblLocation.setModel(new DefaultTableModel(new String[]{"More Than One Creature Selected"}, 0));
            }
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoElementActionPerformed(null);
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnDeleteElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblElementMouseReleased(null);
        }
    }//GEN-LAST:event_tblElementKeyReleased

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        app.getMainFrame().getGlassPane().setVisible(true);
        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = tblElement.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilsPanelGenerator.openPanelAsTab(app, (String)(tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(selectedRows[t]), 1)),
                PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
        }
        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
        app.getMainFrame().getGlassPane().setVisible(false);
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddElementActionPerformed
        UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
    }//GEN-LAST:event_btnAddElementActionPerformed

    private void btnDeleteElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteElementActionPerformed
        if (tblElement.getSelectedRowCount() > 0) {
            int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                        "Are you sure you want to delete the Creature(s)?  This will delete all Observations and files linked to this Creature as well.",
                        "Delete Creature(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            });
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblElement.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    UtilsPanelGenerator.removeOpenedTab((String)tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(selectedRows[t]), 1), PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel);
                    app.getDBI().delete(new Element((String)tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(selectedRows[t]), 1)));
                }
                formComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteElementActionPerformed

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationKeyPressed

    private void cmbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTypeActionPerformed
        searchElement = new Element();
        txtSearch.setText("");
        ElementType type = (ElementType)cmbType.getSelectedItem();
        if (!ElementType.NONE.equals(type)) {
            searchElement.setType(type);
        }
        UtilsTableGenerator.setupElementTableLarge(app, tblElement, searchElement, txtSearch.getText());
        tblElementMouseReleased(null);
    }//GEN-LAST:event_cmbTypeActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        app.getMainFrame().getGlassPane().setVisible(true);
        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = tblLocation.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilsPanelGenerator.openPanelAsTab(app, (String)(tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(selectedRows[t]), 1)),
                PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
        }
        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
        app.getMainFrame().getGlassPane().setVisible(false);
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblElement.getSelectedRowCount() == 1) {
            Element tempElement = app.getDBI().find(new Element((String)tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1)));
            UtilsFileProcessing.openFile(tempElement.getWildLogFileID(), 0, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        UtilsTableGenerator.setupElementTableLarge(app, tblElement, searchElement, txtSearch.getText());
        lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
    }//GEN-LAST:event_formComponentShown

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnGoElementActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddElement;
    private javax.swing.JButton btnDeleteElement;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JScrollPane scrlElement;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
