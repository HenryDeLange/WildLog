package wildlog.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;


public class MergeVisitDialog extends JDialog {
    private final WildLogApp app;


    public MergeVisitDialog(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[MergeVisitDialog]");
        app = inApp;
        initComponents();
        loadLists();
        // Weet nie hoekom die dialog 'n pack() nodig het nie...
        pack();
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
        chkMergeVisitFiles = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Merge Two Periods");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif")).getImage());
        setModal(true);
        setName("merge dialog"); // NOI18N
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

        chkMergeVisitFiles.setSelected(true);
        chkMergeVisitFiles.setText("Also merge the Files associated with the periods.");
        chkMergeVisitFiles.setToolTipText("If selected this will move the Files from the Period that will be removed to the Period that will be kept, otherwise the Period's Files will be deleted.");
        chkMergeVisitFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkMergeVisitFiles.setFocusPainted(false);
        chkMergeVisitFiles.setName("chkMergeVisitFiles"); // NOI18N

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
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkMergeVisitFiles))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(chkMergeVisitFiles))
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
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
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (lstFromVisit.getSelectedIndex() >= 0 && lstFromLocation.getSelectedIndex() >= 0 && lstToLocation.getSelectedIndex() >= 0 && lstToVisit.getSelectedIndex() >= 0) {
            // Update the Visit
            for (Visit tempFromVisit : (List<Visit>) lstFromVisit.getSelectedValuesList()) {
                // Update the sightings
                List<Sighting> sightings = app.getDBI().listSightings(0, 0, tempFromVisit.getID(), false, Sighting.class);
                for (Sighting sighting : sightings) {
                    sighting.setLocationID(((Location) lstToLocation.getSelectedValue()).getID());
                    sighting.setVisitID(((Visit) lstToVisit.getSelectedValue()).getID());
                    app.getDBI().updateSighting(sighting);
                }
                // Update the files
                if (chkMergeVisitFiles.isSelected()) {
                    List<WildLogFile> lstWildLogFiles = app.getDBI().listWildLogFiles(
                            Visit.WILDLOGFILE_ID_PREFIX + tempFromVisit, null, WildLogFile.class);
                    for (WildLogFile wildLogFile : lstWildLogFiles) {
                        wildLogFile.setId(Visit.WILDLOGFILE_ID_PREFIX + (String) lstToVisit.getSelectedValue());
                        app.getDBI().updateWildLogFile(wildLogFile);
                    }
                }
                // Delete the visit
                app.getDBI().deleteVisit(tempFromVisit.getID());
            }
            setVisible(false);
            dispose();
        }
        else {
            WLOptionPane.showMessageDialog(this,
                    "Please select a From Place and Period. Then select a To Place and Period.",
                    "Value Not Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void lstFromLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFromLocationValueChanged
        DefaultListModel<Visit> visitModel = new DefaultListModel<>();
        if (lstFromLocation.getSelectedIndex() >= 0) {
            Location tempLocation = (Location) lstFromLocation.getSelectedValue();
            List<Visit> visits = app.getDBI().listVisits(null, tempLocation.getID(), null, false, Visit.class);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                visitModel.addElement(tempVisit);
            }
        }
        lstFromVisit.setModel(visitModel);
    }//GEN-LAST:event_lstFromLocationValueChanged

    private void lstToLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstToLocationValueChanged
        DefaultListModel<Visit> visitModel = new DefaultListModel<>();
        if (lstToLocation.getSelectedIndex() >= 0) {
            Location tempLocation = (Location) lstToLocation.getSelectedValue();
            List<Visit> visits = app.getDBI().listVisits(null, tempLocation.getID(), null, false, Visit.class);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                visitModel.addElement(tempVisit);
            }
        }
        lstToVisit.setModel(visitModel);
    }//GEN-LAST:event_lstToLocationValueChanged


    // Private Methods
    private void loadLists() {
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Location> locations = new ArrayList<Location>(app.getDBI().listLocations(null, Location.class));
        Collections.sort(locations);
        DefaultListModel<Location> fromLocationModel = new DefaultListModel<>();
        DefaultListModel<Location> toLocationModel = new DefaultListModel<>();
        for (Location tempLocation : locations) {
            fromLocationModel.addElement(tempLocation);
            toLocationModel.addElement(tempLocation);
        }
        lstFromLocation.setModel(fromLocationModel);
        lstToLocation.setModel(toLocationModel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JCheckBox chkMergeVisitFiles;
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
