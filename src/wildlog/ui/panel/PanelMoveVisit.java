/*
 * PanelMoveVisit.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.ui.panel;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;

/**
 *
 * @author Delangeh
 */
public class PanelMoveVisit extends javax.swing.JPanel {
     // Variables:
    private WildLogApp app;

    /** Creates new form PanelMoveVisit */
    public PanelMoveVisit() {
        initComponents();
        app = (WildLogApp) Application.getInstance();
        loadLists();
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

        setMaximumSize(new java.awt.Dimension(750, 520));
        setMinimumSize(new java.awt.Dimension(750, 520));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(750, 520));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelMoveVisit.class);
        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstFromLocation.setName("lstFromLocation"); // NOI18N
        lstFromLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstFromLocationMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(lstFromLocation);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 340, 230));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lstVisit.setName("lstVisit"); // NOI18N
        jScrollPane2.setViewportView(lstVisit);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, 370, 230));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        lstToLocation.setName("lstToLocation"); // NOI18N
        jScrollPane3.setViewportView(lstToLocation);

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 340, 220));

        btnConfirm.setIcon(resourceMap.getIcon("btnConfirm.icon")); // NOI18N
        btnConfirm.setText(resourceMap.getString("btnConfirm.text")); // NOI18N
        btnConfirm.setName("btnConfirm"); // NOI18N
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });
        add(btnConfirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 340, 160, 90));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void lstFromLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstFromLocationMouseReleased
        DefaultListModel visitModel = new DefaultListModel();
        if (lstFromLocation.getSelectedIndex() >= 0) {
            Location tempLocation = (Location)lstFromLocation.getSelectedValue();
            Visit temp = new Visit();
            temp.setLocationName(tempLocation.getName());
            List<Visit> visits = app.getDBI().list(temp);
            Collections.sort(visits);
            for (Visit tempVisit : visits)
                visitModel.addElement(tempVisit);
        }
        lstVisit.setModel(visitModel);
    }//GEN-LAST:event_lstFromLocationMouseReleased

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        if (lstVisit.getSelectedIndex() >= 0 && lstFromLocation.getSelectedIndex() >= 0 && lstToLocation.getSelectedIndex() >= 0) {
            if (JOptionPane.showConfirmDialog(null, "It is strongly recommended that you backup your data (WildLog folder). Do you want to continue now?", "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Location tempLocation = (Location)lstToLocation.getSelectedValue();
                // Update the Visit
                Visit tempVisit = (Visit)lstVisit.getSelectedValue();
                tempVisit.setLocationName(tempLocation.getName());
                app.getDBI().createOrUpdate(tempVisit, tempVisit.getName());
                // Update the sightings
                Sighting temp = new Sighting();
                temp.setVisitName(tempVisit.getName());
                List<Sighting> sightings = app.getDBI().list(temp);
                for (Sighting tempSighting : sightings) {
                    tempSighting.setLocationName(tempLocation.getName());
                    tempSighting.setVisitName(tempVisit.getName());
                    app.getDBI().createOrUpdate(tempSighting);
                }
                // Reload the lists
                loadLists();
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Please select a From Location, Visit and To Location.", "Value Not Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed


    // Private Methods
    private void loadLists() {
        // Need to wrap in ArrayList because of java.lang.UnsupportedOperationException
        List<Location> locations = new ArrayList<Location>(app.getDBI().list(new Location()));
        Collections.sort(locations);
        DefaultListModel fromLocationModel = new DefaultListModel();
        DefaultListModel toLocationModel = new DefaultListModel();
        for (Location tempLocation : locations) {
            fromLocationModel.addElement(tempLocation);
            toLocationModel.addElement(tempLocation);
        }
        lstFromLocation.setModel(fromLocationModel);
        lstToLocation.setModel(toLocationModel);
        lstFromLocationMouseReleased(null);
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
