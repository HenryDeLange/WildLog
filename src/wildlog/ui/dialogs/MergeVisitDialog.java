package wildlog.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class MergeVisitDialog extends JDialog {
    private WildLogApp app;


    public MergeVisitDialog(WildLogApp inApp) {
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
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstFromLocation = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstFromVisit = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstToLocation = new javax.swing.JList();
        btnConfirm = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstToVisit = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Merge Two Periods");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif")).getImage());
        setMaximumSize(new java.awt.Dimension(740, 600));
        setMinimumSize(new java.awt.Dimension(740, 600));
        setModal(true);
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(740, 600));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Select the Place you want to merge the Period from:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Select the Place you want to merge the Period to:");
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstFromLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFromLocation.setName("lstFromLocation"); // NOI18N
        lstFromLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        lstFromLocation.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFromLocationValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstFromLocation);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lstFromVisit.setName("lstFromVisit"); // NOI18N
        lstFromVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        jScrollPane2.setViewportView(lstFromVisit);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        lstToLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstToLocation.setName("lstToLocation"); // NOI18N
        lstToLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        lstToLocation.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstToLocationValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lstToLocation);

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnConfirm.setToolTipText("Move the Observations from the selected Period to the new Period and then delete the initial Period.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setName("btnConfirm"); // NOI18N
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Remove this Period(s):");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Keep this Period:");
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        lstToVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstToVisit.setName("lstToVisit"); // NOI18N
        lstToVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        jScrollPane4.setViewportView(lstToVisit);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Select the two Peroids to merge:");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText("<html>All Observations from the first Period will be changed to point to the second Period, then the first Period will be deleted.</html>");
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2))
                                .addGap(10, 10, 10))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (lstFromVisit.getSelectedIndex() >= 0 && lstFromLocation.getSelectedIndex() >= 0 && lstToLocation.getSelectedIndex() >= 0 && lstToVisit.getSelectedIndex() >= 0) {
            // Update the Visit
            for (String tempFromVisitName : (List<String>) lstFromVisit.getSelectedValuesList()) {
                Visit tempFromVisit = app.getDBI().find(new Visit(tempFromVisitName));
                // Update the sightings
                Sighting tempSighting = new Sighting();
                tempSighting.setVisitName(tempFromVisit.getName());
                List<Sighting> sightings = app.getDBI().list(tempSighting);
                for (Sighting sighting : sightings) {
                    sighting.setLocationName((String)lstToLocation.getSelectedValue());
                    sighting.setVisitName((String)lstToVisit.getSelectedValue());
                    app.getDBI().createOrUpdate(sighting, false);
                }
                app.getDBI().delete(tempFromVisit);
            }
            setVisible(false);
            dispose();
        }
        else {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(this,
                    "Please select a From Place and Period. Then select a To Place and Period.",
                    "Value Not Selected", JOptionPane.INFORMATION_MESSAGE);
            getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void lstFromLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFromLocationValueChanged
        DefaultListModel<String> visitModel = new DefaultListModel<String>();
        if (lstFromLocation.getSelectedIndex() >= 0) {
            String tempLocation = (String)lstFromLocation.getSelectedValue();
            Visit temp = new Visit();
            temp.setLocationName(tempLocation);
            List<Visit> visits = app.getDBI().list(temp);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                visitModel.addElement(tempVisit.getName());
            }
        }
        lstFromVisit.setModel(visitModel);
    }//GEN-LAST:event_lstFromLocationValueChanged

    private void lstToLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstToLocationValueChanged
        DefaultListModel<String> visitModel = new DefaultListModel<String>();
        if (lstToLocation.getSelectedIndex() >= 0) {
            String tempLocation = (String)lstToLocation.getSelectedValue();
            Visit temp = new Visit();
            temp.setLocationName(tempLocation);
            List<Visit> visits = app.getDBI().list(temp);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                visitModel.addElement(tempVisit.getName());
            }
        }
        lstToVisit.setModel(visitModel);
    }//GEN-LAST:event_lstToLocationValueChanged


    // Private Methods
    private void loadLists() {
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Location> locations = new ArrayList<Location>(app.getDBI().list(new Location()));
        Collections.sort(locations);
        DefaultListModel<String> fromLocationModel = new DefaultListModel<String>();
        DefaultListModel<String> toLocationModel = new DefaultListModel<String>();
        for (Location tempLocation : locations) {
            fromLocationModel.addElement(tempLocation.getName());
            toLocationModel.addElement(tempLocation.getName());
        }
        lstFromLocation.setModel(fromLocationModel);
        lstToLocation.setModel(toLocationModel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList lstFromLocation;
    private javax.swing.JList lstFromVisit;
    private javax.swing.JList lstToLocation;
    private javax.swing.JList lstToVisit;
    // End of variables declaration//GEN-END:variables
}
