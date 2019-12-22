package wildlog.ui.panels.bulkupload;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import org.apache.logging.log4j.Level;
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
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.panels.PanelElement;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class ElementSelectionDialog extends JDialog implements PanelNeedsRefreshWhenDataChanges {
    private static long previousElementID = 0;
    private static String previousElementName;
    private final WildLogApp app;
    private boolean selectionMade = false;
    private long selectedElementID;
    private String selectedElementName;


    public ElementSelectionDialog(JFrame inParent, WildLogApp inApp, final long inSelectedElementID) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[ElementSelectionDialog]");
        app = inApp;
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbElementType);
        // Setup the escape key
        final ElementSelectionDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Position the dialog
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        // Attach listeners etc.
        UtilsUI.attachClipboardPopup(txtSearch);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
        // Load the UI
        setupUI(inSelectedElementID);
    }

    private void setupUI(final long inSelectedElementID) {
        // Setup the table
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
        // Load selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int t = 0; t < tblElement.getRowCount(); t++) {
                    if ((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(t), 3) == inSelectedElementID) {
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
        UtilsImageProcessing.setupFoto(inSelectedElementID, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
        if (inSelectedElementID > 0) {
            Element element = app.getDBI().findElement(inSelectedElementID, null, Element.class);
            if (element != null) {
                txtIdentification.setText(element.getDiagnosticDescription());
                txtIdentification.setCaretPosition(0);
            }
        }
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
        btnAdd = new javax.swing.JButton();
        jScrollPane21 = new javax.swing.JScrollPane();
        txtIdentification = new javax.swing.JTextArea();

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
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 230, -1));

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
        jPanel1.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 185, 125, 125));

        btnSelect.setBackground(new java.awt.Color(230, 237, 220));
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSelect.setToolTipText("Confirm the selected Creature.");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jPanel1.add(btnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 10, 125, 70));

        cmbElementType.setMaximumRowCount(11);
        cmbElementType.setModel(new DefaultComboBoxModel(ElementType.values()));
        cmbElementType.setSelectedItem(ElementType.NONE);
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        jPanel1.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, 135, -1));

        btnPreviousElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnPreviousElement.setText("Previous Creature");
        btnPreviousElement.setToolTipText("This will set the Creature to the previously selected Creature.");
        btnPreviousElement.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreviousElement.setName("btnPreviousElement"); // NOI18N
        btnPreviousElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousElementActionPerformed(evt);
            }
        });
        jPanel1.add(btnPreviousElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 90, 125, 40));

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

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 365, 510));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAdd.setText("New Creature");
        btnAdd.setToolTipText("Create a new Creature.");
        btnAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 140, 125, 30));

        jScrollPane21.setName("jScrollPane21"); // NOI18N

        txtIdentification.setColumns(20);
        txtIdentification.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtIdentification.setLineWrap(true);
        txtIdentification.setRows(5);
        txtIdentification.setWrapStyleWord(true);
        txtIdentification.setName("txtIdentification"); // NOI18N
        jScrollPane21.setViewportView(txtIdentification);

        jPanel1.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 320, 125, 240));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected long getSelectedElementID() {
        return selectedElementID;
    }

    public String getSelectedElementName() {
        return selectedElementName;
    }

    protected Icon getSelectedElementIcon() {
        return lblElementImage.getIcon();
    }

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (!tblElement.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (tblElement.getSelectedRowCount() == 1) {
            selectionMade = true;
            selectedElementID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
            previousElementID = selectedElementID;
            selectedElementName = (String) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1);
            previousElementName = selectedElementName;
            tblElement.setBorder(null);
            setVisible(false);
            dispose();
        }
        else {
            tblElement.setBorder(new LineBorder(Color.RED, 2));
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnPreviousElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousElementActionPerformed
        // Reload the entire table to make sure the element is there
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Select the previous element
                for (int t = 0; t < tblElement.getModel().getRowCount(); t++) {
                    if ((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(t), 3) == previousElementID) {
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
                UtilsImageProcessing.setupFoto(previousElementID, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
                Element element = app.getDBI().findElement(previousElementID, null, Element.class);
                if (element != null) {
                    txtIdentification.setText(element.getDiagnosticDescription());
                    txtIdentification.setCaretPosition(0);
                }
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
            long selectedID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
            // Change the image
            UtilsImageProcessing.setupFoto(selectedID, 0, lblElementImage, WildLogThumbnailSizes.MEDIUM_VERY_SMALL, app);
            Element element = app.getDBI().findElement(selectedID, null, Element.class);
            if (element != null) {
                txtIdentification.setText(element.getDiagnosticDescription());
                txtIdentification.setCaretPosition(0);
            }
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_VERY_SMALL));
            txtIdentification.setText("");
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
        ElementType type = (ElementType) cmbElementType.getSelectedItem();
        if (!ElementType.NONE.equals(type)) {
            UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, type);
        }
        else {
            UtilsTableGenerator.setupElementTableSmall(app, tblElement, null, null);
        }
        txtSearch.setText("");
        // Clear Images
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        txtIdentification.setText("");
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

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        JDialog popup = new JDialog(this, "Add New Creature", true);
        ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif"));
        popup.setIconImage(icon.getImage());
        PanelElement panel = new PanelElement(app, new Element(), true, this);
        popup.add(panel);
        popup.setResizable(false);
        popup.pack();
        UtilsDialog.setDialogToCenter(this, popup);
        UtilsDialog.addModalBackgroundPanel(this, popup);
        UtilsDialog.addModalBackgroundPanel(popup, null);
        UtilsDialog.addEscapeKeyListener(popup);
        popup.setVisible(true);
        popup.dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    @Override
    public void doTheRefresh(Object inIndicator) {
        txtSearch.setText("");
        cmbElementType.setSelectedItem(ElementType.NONE);
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.SMALL));
        txtIdentification.setText("");
        setupUI(selectedElementID);
    }
    
    public boolean isSelectionMade() {
        return selectionMade;
    }

    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    public static long getPreviousElementID() {
        return previousElementID;
    }

    public static void setPreviousElementID(long inPreviousElementID) {
        previousElementID = inPreviousElementID;
    }

    public static String getPreviousElementName() {
        return previousElementName;
    }

    public static void setPreviousElementName(String inPreviousElementName) {
        previousElementName = inPreviousElementName;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnPreviousElement;
    private javax.swing.JButton btnSelect;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JTable tblElement;
    private javax.swing.JTextArea txtIdentification;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
