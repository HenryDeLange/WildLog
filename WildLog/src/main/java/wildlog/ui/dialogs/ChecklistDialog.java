package wildlog.ui.dialogs;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.border.LineBorder;
import org.jdesktop.swingx.sort.ListSortController;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;


public class ChecklistDialog extends JDialog {
    private WildLogApp app;
    private final PanelNeedsRefreshWhenDataChanges panelToRefresh;
    private final Location location;
    private final Visit visit;

    
    public ChecklistDialog(WildLogApp inApp, JDialog inParent, Location inLocation, Visit inVisit, PanelNeedsRefreshWhenDataChanges inPanelToRefresh) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[ChecklistDialog]");
        app = inApp;
        initComponents();
        loadElementList();
        location = inLocation;
        visit = inVisit;
        panelToRefresh = inPanelToRefresh;

        DefaultListModel sightedModel = new DefaultListModel();
        lstSightedCreatures.setModel(sightedModel);

        // Setup search button
        UtilsUI.attachClipboardPopup(txtSearch);

        // Setup the default behavior
        UtilsDialog.setDialogToCenter(inParent, this);
        ActionListener escListiner = UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        // Hack to fix the wierd focus issue to get the ESC to work (related to the datepicker)
        this.setFocusable(true);
        dtpDate.getEditor().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);
        // Make dates pretty
        dtpDate.getComponent(1).setBackground(this.getBackground());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstFromCreatures = new org.jdesktop.swingx.JXList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSightedCreatures = new javax.swing.JList();
        btnConfirm = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cmbElementType = new javax.swing.JComboBox();
        txtSearch = new javax.swing.JTextField();
        dtpDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Observations (CheckList)");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/CheckList.png")).getImage());
        setMinimumSize(new java.awt.Dimension(760, 550));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Dubble click the Creature to be added as an Observation:");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstFromCreatures.setModel(new DefaultListModel());
        lstFromCreatures.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFromCreatures.setToolTipText("");
        lstFromCreatures.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lstFromCreatures.setName("lstFromCreatures"); // NOI18N
        lstFromCreatures.setSelectionBackground(new java.awt.Color(82, 115, 79));
        lstFromCreatures.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstFromCreaturesMouseClicked(evt);
            }
        });
        lstFromCreatures.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstFromCreaturesKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(lstFromCreatures);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 340, 450));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lstSightedCreatures.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        lstSightedCreatures.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lstSightedCreatures.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstSightedCreatures.setName("lstSightedCreatures"); // NOI18N
        lstSightedCreatures.setSelectionBackground(new java.awt.Color(82, 115, 79));
        lstSightedCreatures.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSightedCreaturesMouseClicked(evt);
            }
        });
        lstSightedCreatures.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstSightedCreaturesKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(lstSightedCreatures);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 140, 350, 370));

        btnConfirm.setBackground(new java.awt.Color(0, 204, 51));
        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnConfirm.setToolTipText("Save the Observations of the selected Creatures for this date.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setName("btnConfirm"); // NOI18N
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });
        getContentPane().add(btnConfirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 110, 60));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Dubble click to remove the Creature from the Observations.");
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, -1));

        cmbElementType.setMaximumRowCount(10);
        cmbElementType.setModel(new DefaultComboBoxModel(ElementType.values()));
        cmbElementType.setSelectedItem(ElementType.NONE);
        cmbElementType.setFocusable(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        getContentPane().add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 120, -1));

        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 210, -1));

        dtpDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51)));
        dtpDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));
        dtpDate.setName("dtpDate"); // NOI18N
        getContentPane().add(dtpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, 140, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("The date to use for all Observations:");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 110, -1, 20));
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (dtpDate.getDate() != null) {
            for (int t = 0; t < lstSightedCreatures.getModel().getSize(); t++) {
                Element element = (Element) lstSightedCreatures.getModel().getElementAt(t);
                Sighting tempSighting = new Sighting();
                tempSighting.setElementID(element.getID());
                tempSighting.setLocationID(location.getID());
                tempSighting.setVisitID(visit.getID());
                tempSighting.setDate(dtpDate.getDate());
                tempSighting.setCertainty(Certainty.SURE);
                app.getDBI().createSighting(tempSighting, false);
            }

            if (panelToRefresh != null) {
                panelToRefresh.doTheRefresh(null);
            }
            // Close the dialog - (Evt is null if the Image Upload calls save method...)
            if (evt != null) {
                setVisible(false);
                dispose();
            }
        }
        else {
            dtpDate.setBorder(new LineBorder(Color.RED));
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void lstFromCreaturesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstFromCreaturesMouseClicked
        if (evt == null || evt.getClickCount() == 2) {
            Element temp = (Element) lstFromCreatures.getSelectedValue();
            DefaultListModel model = (DefaultListModel)lstSightedCreatures.getModel();
            if (temp != null) {
                if (!model.contains(temp)) {
                    model.addElement(temp);
                }
            }
        }
    }//GEN-LAST:event_lstFromCreaturesMouseClicked

    private void lstSightedCreaturesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSightedCreaturesMouseClicked
        if (evt == null || evt.getClickCount() == 2) {
            if (lstSightedCreatures.getSelectedIndex() >= 0) {
                Element temp = (Element) lstSightedCreatures.getSelectedValue();
                DefaultListModel model = (DefaultListModel)lstSightedCreatures.getModel();
                if (model.contains(temp)) {
                    model.removeElement(temp);
                }
            }
        }
    }//GEN-LAST:event_lstSightedCreaturesMouseClicked

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        txtSearchKeyReleased(null);
        loadElementList();
    }//GEN-LAST:event_cmbElementTypeActionPerformed

    private void lstFromCreaturesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstFromCreaturesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            lstFromCreaturesMouseClicked(null);
    }//GEN-LAST:event_lstFromCreaturesKeyPressed

    private void lstSightedCreaturesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSightedCreaturesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            lstSightedCreaturesMouseClicked(null);
    }//GEN-LAST:event_lstSightedCreaturesKeyPressed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (evt == null || evt.getKeyChar() == KeyEvent.VK_ESCAPE) {
           txtSearch.setText("");
        }
        ListSortController sorter = (ListSortController) lstFromCreatures.getRowSorter();
        if (sorter == null) {
            sorter = new ListSortController(lstFromCreatures.getModel());
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtSearch.getText()));
        lstFromCreatures.setRowSorter(sorter);
    }//GEN-LAST:event_txtSearchKeyReleased

    // Private Methods
    private void loadElementList() {
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        ElementType elementType = (ElementType) cmbElementType.getSelectedItem();
        if (ElementType.NONE.equals(elementType)) {
            elementType = null;
        }
        String elementName = null;
        if (txtSearch.getText().length() > 0) {
            elementName = txtSearch.getText();
        }
        List<Element> elements = new ArrayList<Element>(app.getDBI().listElements(elementName, null, elementType, Element.class));
        Collections.sort(elements);
        DefaultListModel model = (DefaultListModel)lstFromCreatures.getModel();
        model.clear();
        for (Element tempElement : elements) {
            model.addElement(tempElement);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JComboBox cmbElementType;
    private org.jdesktop.swingx.JXDatePicker dtpDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXList lstFromCreatures;
    private javax.swing.JList lstSightedCreatures;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
