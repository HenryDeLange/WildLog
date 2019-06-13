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
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;


public class MoveVisitDialog extends JDialog {
    private final WildLogApp app;

    
    public MoveVisitDialog(WildLogApp inApp, Visit inVisit) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[MoveVisitDialog]");
        app = inApp;
        initComponents();
        loadLists();
        // If a visit was specified, select and lock the UI
        if (inVisit != null) {
            // Select the location
            for (int t = 0; t < lstFromLocation.getModel().getSize(); t++) {
                if (((Location) lstFromLocation.getModel().getElementAt(t)).getID() == inVisit.getLocationID()) {
                    lstFromLocation.setSelectedValue(lstFromLocation.getModel().getElementAt(t), true);
                    lstFromLocation.setEnabled(false);
                    break;
                }
            }
            // Select the visit
            lstFromLocationValueChanged(null);
            for (int t = 0; t < lstVisit.getModel().getSize(); t++) {
                if (((Visit) lstVisit.getModel().getElementAt(t)).getID() == inVisit.getID()) {
                    lstVisit.setSelectedValue(lstVisit.getModel().getElementAt(t), true);
                    lstVisit.setEnabled(false);
                    break;
                }
            }
        }
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
        lstVisit = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstToLocation = new javax.swing.JList();
        btnConfirm = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Move A Period To A New Place");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(750, 550));
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Select the Place you want to move the Period from:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Select the Place you want to move the Period to:");
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

        lstVisit.setName("lstVisit"); // NOI18N
        lstVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        jScrollPane2.setViewportView(lstVisit);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        lstToLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstToLocation.setName("lstToLocation"); // NOI18N
        lstToLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        jScrollPane3.setViewportView(lstToLocation);

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnConfirm.setToolTipText("Move the selected Period to the new Place.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setName("btnConfirm"); // NOI18N
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Select the Period to be moved:");
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (lstVisit.getSelectedIndex() >= 0 && lstFromLocation.getSelectedIndex() >= 0 && lstToLocation.getSelectedIndex() >= 0) {
            Location tempLocation = (Location) lstToLocation.getSelectedValue();
            // Update the Visit
            for (Visit tempVisit : (List<Visit>) lstVisit.getSelectedValuesList()) {
                tempVisit.setLocationID(tempLocation.getID());
                app.getDBI().updateVisit(tempVisit, tempVisit.getName(), false);
                // Update the sightings
                List<Sighting> sightings = app.getDBI().listSightings(0, 0, tempVisit.getID(), false, Sighting.class);
                for (Sighting tempSighting : sightings) {
                    tempSighting.setLocationID(tempLocation.getID());
                    tempSighting.setVisitID(tempVisit.getID());
                    app.getDBI().updateSighting(tempSighting, false);
                }
            }
            setVisible(false);
            dispose();
        }
        else {
            WLOptionPane.showMessageDialog(this,
                    "Please select a From Place and Period. Then select a To Place.",
                    "Value Not Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void lstFromLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFromLocationValueChanged
        DefaultListModel<Visit> visitModel = new DefaultListModel<>();
        if (lstFromLocation.getSelectedIndex() >= 0) {
            Location tempLocation = (Location)lstFromLocation.getSelectedValue();
            List<Visit> visits = app.getDBI().listVisits(null, tempLocation.getID(), null, false, Visit.class);
            Collections.sort(visits);
            for (Visit tempVisit : visits) {
                visitModel.addElement(tempVisit);
            }
        }
        lstVisit.setModel(visitModel);
    }//GEN-LAST:event_lstFromLocationValueChanged


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
        lstFromLocationValueChanged(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstFromLocation;
    private javax.swing.JList lstToLocation;
    private javax.swing.JList lstVisit;
    // End of variables declaration//GEN-END:variables
}
