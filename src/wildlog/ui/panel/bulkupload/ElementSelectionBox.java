package wildlog.ui.panel.bulkupload;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.utils.ui.Utils;


public class ElementSelectionBox extends JDialog {
    private WildLogApp app;
    private boolean selectionMade = false;

    /** Creates new form ElementSelectionBox */
    public ElementSelectionBox(Frame inParent, boolean inIsModal, WildLogApp inApp, String inSelectedElement) {
        super(inParent, inIsModal);
        app = inApp;
        initComponents();
        List<Element> elements = app.getDBI().list(new Element());
        Collections.sort(elements);
        lstElements.setListData(elements.toArray());
        if (inSelectedElement != null && !inSelectedElement.isEmpty()) {
            txtElementName.setText(inSelectedElement);
            txtElementNameKeyReleased(null);
        }
        // Setup the escape key
        final ElementSelectionBox thisHandler = (ElementSelectionBox)this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Position the dialog
        Utils.setDialogToCenter(app.getMainFrame(), thisHandler);
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
        jLabel2 = new javax.swing.JLabel();
        txtElementName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstElements = new javax.swing.JList();
        lblElementImage = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ElementSelectionBox.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Element.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        txtElementName.setName("txtElementName"); // NOI18N
        txtElementName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtElementNameKeyReleased(evt);
            }
        });
        jPanel1.add(txtElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstElements.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstElements.setFocusable(false);
        lstElements.setName("lstElements"); // NOI18N
        lstElements.setSelectionBackground(resourceMap.getColor("lstElements.selectionBackground")); // NOI18N
        lstElements.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstElementsMouseClicked(evt);
            }
        });
        lstElements.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstElementsValueChanged(evt);
            }
        });
        lstElements.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstElementsKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(lstElements);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 250, 360));

        lblElementImage.setBackground(resourceMap.getColor("lblElementImage.background")); // NOI18N
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        jPanel1.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 150, 150));

        btnSelect.setBackground(resourceMap.getColor("btnSelect.background")); // NOI18N
        btnSelect.setIcon(resourceMap.getIcon("btnSelect.icon")); // NOI18N
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setFocusPainted(false);
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jPanel1.add(btnSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 150, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 430));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected String getElementName() {
        return txtElementName.getText();
    }

    protected Icon getElementIcon() {
        return lblElementImage.getIcon();
    }

    private void txtElementNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtElementNameKeyReleased
        for (int t = 0; t < lstElements.getModel().getSize(); t++) {
            if (lstElements.getModel().getElementAt(t).toString().equalsIgnoreCase(txtElementName.getText())) {
                lstElements.setSelectedIndex(t);
                if (t > 3) {
                    lstElements.scrollRectToVisible(lstElements.getCellBounds(t, t));
                }
                break;
            }
            else {
                lstElements.getSelectionModel().clearSelection();
            }
        }
    }//GEN-LAST:event_txtElementNameKeyReleased

    private void lstElementsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstElementsValueChanged
        if (!lstElements.getSelectionModel().isSelectionEmpty()) {
            String selectedName = lstElements.getSelectedValue().toString();
            // Change the location name
            txtElementName.setText(selectedName);
            // Change the image
            Utils.setupFoto("ELEMENT-" + selectedName, 0, lblElementImage, 150, app);
        }
        else {
            lblElementImage.setIcon(Utils.getScaledIconForNoImage(150));
        }
    }//GEN-LAST:event_lstElementsValueChanged

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (!lstElements.getSelectionModel().isSelectionEmpty()) {
            Utils.openFile("ELEMENT-" + lstElements.getSelectedValue().toString(), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        selectionMade = true;
        dispose();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void lstElementsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstElementsKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSelectActionPerformed(null);
    }//GEN-LAST:event_lstElementsKeyReleased

    private void lstElementsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstElementsMouseClicked
        if (evt.getClickCount() == 2) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_lstElementsMouseClicked

    public boolean isSelectionMade() {
        return selectionMade;
    }

    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JList lstElements;
    private javax.swing.JTextField txtElementName;
    // End of variables declaration//GEN-END:variables
}
