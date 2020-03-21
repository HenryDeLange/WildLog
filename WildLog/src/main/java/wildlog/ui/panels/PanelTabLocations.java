package wildlog.ui.panels;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.data.enums.system.WildLogUserTypes;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;


public class PanelTabLocations extends JPanel {
    private final WildLogApp app;
    private final JTabbedPane tabbedPanel;

    public PanelTabLocations(WildLogApp inApp, JTabbedPane inTabbedPanel) {
        app = inApp;
        tabbedPanel = inTabbedPanel;
        initComponents();
        // Add key listeners to table to allow the selection of rows based on key events.
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblVisit);
        // Add listner to auto resize columns.
        UtilsTableGenerator.setupColumnResizingListener(tblLocation, 1);
        UtilsTableGenerator.setupColumnResizingListener(tblElement, 1);
        UtilsTableGenerator.setupColumnResizingListener(tblVisit, 1);
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_REMOTE) {
            btnDeleteLocation.setEnabled(false);
            btnDeleteLocation.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                btnAddLocation.setEnabled(false);
                btnAddLocation.setVisible(false);
            }
        }
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_REMOTE) {
            btnAddLocation.setEnabled(false);
            btnAddLocation.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        btnGoLocation_LocTab = new javax.swing.JButton();
        btnAddLocation = new javax.swing.JButton();
        btnDeleteLocation = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        btnGoVisit_LocTab = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        btnGoElement_LocTab = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(194, 207, 214));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocationMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblLocation);

        btnGoLocation_LocTab.setBackground(new java.awt.Color(194, 207, 214));
        btnGoLocation_LocTab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoLocation_LocTab.setToolTipText("Open a tab for the selected Place.");
        btnGoLocation_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocation_LocTabActionPerformed(evt);
            }
        });

        btnAddLocation.setBackground(new java.awt.Color(194, 207, 214));
        btnAddLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        btnAddLocation.setToolTipText("Open a tab for a new Place to be added.");
        btnAddLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLocationActionPerformed(evt);
            }
        });

        btnDeleteLocation.setBackground(new java.awt.Color(194, 207, 214));
        btnDeleteLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnDeleteLocation.setToolTipText("<html>Delete the selected Place. <br/>This will delete all linked Periods, Observations and files as well.</html>");
        btnDeleteLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLocationActionPerformed(evt);
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

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("All Places:");

        jPanel1.setBackground(new java.awt.Color(194, 207, 214));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(200, 220, 223), new java.awt.Color(168, 179, 182)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Periods:");

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tblElement);

        btnGoVisit_LocTab.setBackground(new java.awt.Color(194, 207, 214));
        btnGoVisit_LocTab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoVisit_LocTab.setToolTipText("Open a tab for the selected Period.");
        btnGoVisit_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoVisit_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisit_LocTabActionPerformed(evt);
            }
        });

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setSelectionBackground(new java.awt.Color(96, 92, 116));
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblVisit);

        btnGoElement_LocTab.setBackground(new java.awt.Color(194, 207, 214));
        btnGoElement_LocTab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoElement_LocTab.setToolTipText("Open a tab for the selected Creature.");
        btnGoElement_LocTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement_LocTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElement_LocTabActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Creatures:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(btnGoVisit_LocTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnGoElement_LocTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(btnGoVisit_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(btnGoElement_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoLocation_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(13, 13, 13)
                        .addComponent(btnGoLocation_LocTab, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnAddLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnDeleteLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocation_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            if (!(evt instanceof UtilsUI.GeneratedMouseEvent)) {
                tblVisit.clearSelection();
                tblElement.clearSelection();
            }
            // Get Image
            long tempLocationID = (Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 7);
            int fotoCount = app.getDBI().countWildLogFiles(0, tempLocationID);
            if (fotoCount > 0) {
                UtilsImageProcessing.setupFoto(tempLocationID, 0, lblImage, WildLogThumbnailSizes.S0300_NORMAL, app);
            }
            else {
                lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            }
            UtilsTableGenerator.setupVisitTableSmallWithSightings(app, tblVisit, tempLocationID);
            UtilsTableGenerator.setupElementsTableMediumForLocation(app, tblElement, tempLocationID);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            UtilsTableGenerator.setupVisitTableSmallWithSightings(app, tblVisit, 0);
            UtilsTableGenerator.setupElementsTableMediumForLocation(app, tblElement, 0);
            if (tblLocation.getSelectedRowCount() == 0) {
                tblVisit.setModel(new DefaultTableModel(new String[]{"No Place Selected"}, 0));
                tblElement.setModel(new DefaultTableModel(new String[]{"No Place Selected"}, 0));
            }
            else {
                tblVisit.setModel(new DefaultTableModel(new String[]{"More Than One Place Selected"}, 0));
                tblElement.setModel(new DefaultTableModel(new String[]{"More Than One Place Selected"}, 0));
            }
        }
    }//GEN-LAST:event_tblLocationMouseReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoLocation_LocTabActionPerformed(null);
            evt.consume();
        }
        else
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnDeleteLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationKeyPressed

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblLocationMouseReleased(null);
        }
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoVisit_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoVisit_LocTabActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_tblVisitKeyPressed

    private void btnGoLocation_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocation_LocTabActionPerformed
        app.getMainFrame().getGlassPane().setVisible(true);
        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = tblLocation.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilsPanelGenerator.openPanelAsTab(app, (Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(selectedRows[t]), 7),
                PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
        }
        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
        app.getMainFrame().getGlassPane().setVisible(false);
    }//GEN-LAST:event_btnGoLocation_LocTabActionPerformed

    private void btnGoElement_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElement_LocTabActionPerformed
        app.getMainFrame().getGlassPane().setVisible(true);
        app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = tblElement.getSelectedRows();
        for (int t = 0; t < selectedRows.length; t++) {
            UtilsPanelGenerator.openPanelAsTab(app, (Long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(selectedRows[t]), 4),
                PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
        }
        app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
        app.getMainFrame().getGlassPane().setVisible(false);
    }//GEN-LAST:event_btnGoElement_LocTabActionPerformed

    private void btnAddLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLocationActionPerformed
        UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
    }//GEN-LAST:event_btnAddLocationActionPerformed

    private void btnDeleteLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLocationActionPerformed
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_REMOTE) {
            return;
        }
        if (tblLocation.getSelectedRowCount() > 0) {
            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                    "Are you sure you want to delete the Place(s)? This will delete all Periods, Observations and files linked to this Place(s) as well.",
                    "Delete Place(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblLocation.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    long locationID = (Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(selectedRows[t]), 7);
                    List<Visit> visits = app.getDBI().listVisits(null, locationID, null, false, Visit.class);
                    for (int i = 0; i < visits.size(); i++) {
                        UtilsPanelGenerator.removeOpenedTab(visits.get(i).getID(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel);
                    }
                    // Daar kan steeds tabs wees wat nuwe visits het vir die location wat nog nie gesave was nie wat ek ook moet toe maak...
                    // Build up list and delete on components not index to prevent index issues when removing
                    List<PanelCanSetupHeader.HeaderPanel> tabsToRemove = new ArrayList<>(tabbedPanel.getTabCount());
                    for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
                        if (tabbedPanel.getTabComponentAt(i) instanceof PanelCanSetupHeader.HeaderPanel) {
                            PanelCanSetupHeader.HeaderPanel headerPanel = (PanelCanSetupHeader.HeaderPanel)tabbedPanel.getTabComponentAt(i);
                            if (headerPanel.getTabType() != null && PanelCanSetupHeader.TabTypes.VISIT.equals(headerPanel.getTabType())) {
                                PanelVisit panelVisit = (PanelVisit)headerPanel.getParentPanel();
                                if (panelVisit.getLocationForVisit() != null && locationID == panelVisit.getLocationForVisit().getID()) {
                                    tabsToRemove.add(headerPanel);
                                }
                            }
                        }
                    }
                    for (PanelCanSetupHeader.HeaderPanel headerPanel : tabsToRemove) {
                        tabbedPanel.remove(headerPanel.getParentPanel());
                    }
                    // Remove die loation se eie tab
                    UtilsPanelGenerator.removeOpenedTab(locationID, PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel);
                    app.getDBI().deleteLocation(locationID);
                }
                formComponentShown(null);
            }
        }
    }//GEN-LAST:event_btnDeleteLocationActionPerformed

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElement_LocTabActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoElement_LocTabActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void btnGoVisit_LocTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisit_LocTabActionPerformed
        if (tblLocation.getSelectedRow() != -1) {
            app.getMainFrame().getGlassPane().setVisible(true);
            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Location tempLocation = app.getDBI().findLocation((Long) tblLocation.getModel().getValueAt(
                    tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 7), null, false, Location.class);
            int[] selectedRows = tblVisit.getSelectedRows();
            for (int t = 0; t < selectedRows.length; t++) {
                UtilsPanelGenerator.openPanelAsTab(app, (Long) tblVisit.getModel().getValueAt(tblVisit.convertRowIndexToModel(selectedRows[t]), 5),
                    PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel, tempLocation);
            }
            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
            app.getMainFrame().getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnGoVisit_LocTabActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (tblLocation.getSelectedRowCount() == 1) {
            long tempLocationID = (Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 7);
            UtilsFileProcessing.openFile(tempLocationID, 0, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
        UtilsTableGenerator.setupLocationTableLarge(app, tblLocation, null);
    }//GEN-LAST:event_formComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddLocation;
    private javax.swing.JButton btnDeleteLocation;
    private javax.swing.JButton btnGoElement_LocTab;
    private javax.swing.JButton btnGoLocation_LocTab;
    private javax.swing.JButton btnGoVisit_LocTab;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblImage;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblVisit;
    // End of variables declaration//GEN-END:variables
}
