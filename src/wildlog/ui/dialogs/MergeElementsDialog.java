package wildlog.ui.dialogs;

import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Level;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class MergeElementsDialog extends JDialog {
    private final WildLogApp app;


    public MergeElementsDialog(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[MergeElementsDialog]");
        app = inApp;
        initComponents();
        loadLists();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstReplaceElement = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstKeepElement = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Merge Two Creatures");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Element.gif")).getImage());
        setMaximumSize(new java.awt.Dimension(665, 530));
        setMinimumSize(new java.awt.Dimension(665, 530));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(665, 530));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Select the two Creatures to merge:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Keep this Creature:");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Remove this Creature:");
        jLabel4.setName("jLabel4"); // NOI18N

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnConfirm.setToolTipText("Move the Observations from the selected Creature to the new Creature and then delete the initial Creature.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lstReplaceElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstReplaceElement.setName("lstReplaceElement"); // NOI18N
        lstReplaceElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        jScrollPane2.setViewportView(lstReplaceElement);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstKeepElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstKeepElement.setName("lstKeepElement"); // NOI18N
        lstKeepElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        jScrollPane1.setViewportView(lstKeepElement);

        jLabel5.setText("<html>All Observations from the first Creature will be changed to point to the second Creature, then the first Creature will be deleted.</html>");
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)))
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (lstReplaceElement.getSelectedIndex() >= 0 && lstKeepElement.getSelectedIndex() >= 0) {
            Element replaceElement = (Element)lstReplaceElement.getSelectedValue();
            Element keepElement = (Element)lstKeepElement.getSelectedValue();
            List<Sighting> sightings = app.getDBI().listSightings(0, replaceElement.getPrimaryName(), null, null, false, Sighting.class);
            for (Sighting tempSighting : sightings) {
                tempSighting.setElementName(keepElement.getPrimaryName());
                app.getDBI().updateSighting(tempSighting);
            }
            app.getDBI().deleteElement(replaceElement.getPrimaryName());
            // Close the window
            setVisible(false);
            dispose();
        }
        else {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(this,
                    "Please select two Creatures to merge.",
                    "Value Not Selected", JOptionPane.INFORMATION_MESSAGE);
            getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed


    // Private Methods
    private void loadLists() {
        List<Element> elements = app.getDBI().listElements(null, null, null, Element.class);
        Collections.sort(elements);
        DefaultListModel replaceModel = new DefaultListModel();
        DefaultListModel keepModel = new DefaultListModel();
        for (Element tempElement : elements) {
            replaceModel.addElement(tempElement);
            keepModel.addElement(tempElement);
        }

        lstReplaceElement.setModel(replaceModel);
        lstKeepElement.setModel(keepModel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstKeepElement;
    private javax.swing.JList lstReplaceElement;
    // End of variables declaration//GEN-END:variables

}
