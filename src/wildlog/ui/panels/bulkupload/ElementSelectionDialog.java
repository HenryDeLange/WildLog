package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class ElementSelectionDialog extends JDialog {
    private static String previousElement = "";
    private final WildLogApp app;
    private boolean selectionMade = false;
    private String selectedElementName;
    private Element searchElement;


    public ElementSelectionDialog(JFrame inParent, WildLogApp inApp, final String inSelectedElement) {
        super(inParent, true);
        app = inApp;
        initComponents();
        // Setup the escape key
        final ElementSelectionDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Position the dialog
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        // Atach listeners etc.
        UtilsUI.attachClipboardPopup(txtSearch);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
        // Setup the table
        searchElement = new Element();
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, searchElement);
        // Load selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        final int columnToUse;
        if (app.getWildLogOptions().isUseThumbnailTables()) {
            columnToUse = 1;
        }
        else {
            columnToUse = 0;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int t = 0; t < tblElement.getRowCount(); t++) {
                    if (tblElement.getValueAt(t, columnToUse).equals(inSelectedElement)) {
                        tblElement.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblElement.getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblElement.scrollRectToVisible(tblElement.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
            }
        });
        UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + inSelectedElement, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
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
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblElementImage = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        cmbElementType = new javax.swing.JComboBox();
        btnPreviousElement = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select a Creature");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Element.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(230, 237, 220));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 220, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Please choose a Creature:");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblElementImage.setBackground(new java.awt.Color(0, 0, 0));
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        jPanel1.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 140, 150, 150));

        btnSelect.setBackground(new java.awt.Color(230, 237, 220));
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSelect.setToolTipText("Confirm the selected Creature.");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setFocusPainted(false);
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jPanel1.add(btnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 150, 70));

        cmbElementType.setMaximumRowCount(9);
        cmbElementType.setModel(new DefaultComboBoxModel(ElementType.values()));
        cmbElementType.setSelectedItem(ElementType.NONE);
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setFocusable(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        jPanel1.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 120, -1));

        btnPreviousElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnPreviousElement.setText("Use Previous Creature");
        btnPreviousElement.setToolTipText("This will set the Creature to: " + previousElement);
        btnPreviousElement.setFocusPainted(false);
        btnPreviousElement.setFocusable(false);
        btnPreviousElement.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnPreviousElement.setName("btnPreviousElement"); // NOI18N
        btnPreviousElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousElementActionPerformed(evt);
            }
        });
        jPanel1.add(btnPreviousElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 150, 40));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
        jScrollPane2.setViewportView(tblElement);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 340, 510));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected String getElementName() {
        return selectedElementName;
    }

    protected Icon getElementIcon() {
        return lblElementImage.getIcon();
    }

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (!tblElement.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile(Element.WILDLOGFILE_ID_PREFIX + tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (tblElement.getSelectedRowCount() == 1) {
            selectionMade = true;
            selectedElementName = tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1).toString();
            previousElement = selectedElementName;
            tblElement.setBorder(null);
            dispose();
        }
        else {
            tblElement.setBorder(new LineBorder(Color.RED, 2));
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnPreviousElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousElementActionPerformed
        // Reload the entire table to make sure the element is there
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, new Element());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Select the previous element
                for (int t = 0; t < tblElement.getModel().getRowCount(); t++) {
                    if (tblElement.getValueAt(t, 1).equals(previousElement)) {
                        tblElement.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblElement.getModel().getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblElement.scrollRectToVisible(tblElement.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
                // Update the icon
                UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + previousElement, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
                // Do the select action
                btnSelectActionPerformed(null);
            }
        });
    }//GEN-LAST:event_btnPreviousElementActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtSearch.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (!tblElement.getSelectionModel().isSelectionEmpty()) {
            String selectedName = tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1).toString();
            // Change the image
            UtilsImageProcessing.setupFoto(Element.WILDLOGFILE_ID_PREFIX + selectedName, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_SMALL, app);
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        searchElement = new Element();
        ElementType type = (ElementType)cmbElementType.getSelectedItem();
        if (!ElementType.NONE.equals(type)) {
            searchElement.setType(type);
        }
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, searchElement);
        txtSearch.setText("");
        // Clear Images
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
    }//GEN-LAST:event_cmbElementTypeActionPerformed

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        tblElementMouseReleased(null);
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnSelectActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    public boolean isSelectionMade() {
        return selectionMade;
    }

    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    public static String getPreviousElement() {
        return previousElement;
    }

    public static void setPreviousElement(String inPreviousElement) {
        ElementSelectionDialog.previousElement = inPreviousElement;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPreviousElement;
    private javax.swing.JButton btnSelect;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JTable tblElement;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
