package wildlog.ui.dialogs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.panels.PanelVisit;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


public class AdvancedDialog extends JDialog {
    private final WildLogApp app;
    private final Visit visit;
    private final PanelVisit panelVisit;


    public AdvancedDialog(WildLogApp inApp, Visit inVisit, PanelVisit inPanelVisit) {
        super(inApp.getMainFrame());
        System.out.println("[AdvancedDialog]");
        // Set passed in values
        app = inApp;
        visit = inVisit;
        panelVisit = inPanelVisit;
        // Auto generated code
        initComponents();
        // Pack
        pack();
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
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

        btnChecklist = new javax.swing.JButton();
        btnSetAllGPS = new javax.swing.JButton();
        btnSetSunAndMoon = new javax.swing.JButton();
        btnSetDuration = new javax.swing.JButton();
        btnCorrectTime = new javax.swing.JButton();
        btnDuplicateSightings = new javax.swing.JButton();
        btnMoveVisit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Advanced Options");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon Selected.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnChecklist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CheckList.png"))); // NOI18N
        btnChecklist.setText("Add Observations using a Checklist");
        btnChecklist.setToolTipText("Open a popup box to add Observations in a checklist format.");
        btnChecklist.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChecklist.setFocusPainted(false);
        btnChecklist.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChecklist.setIconTextGap(8);
        btnChecklist.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnChecklist.setMaximumSize(new java.awt.Dimension(375, 35));
        btnChecklist.setMinimumSize(new java.awt.Dimension(375, 35));
        btnChecklist.setName("btnChecklist"); // NOI18N
        btnChecklist.setPreferredSize(new java.awt.Dimension(375, 35));
        btnChecklist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecklistActionPerformed(evt);
            }
        });
        getContentPane().add(btnChecklist);

        btnSetAllGPS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GPS.png"))); // NOI18N
        btnSetAllGPS.setText("Set one GPS for all Observations (also recalculates Sun and Moon)");
        btnSetAllGPS.setToolTipText("All Observations for this Period will be assigned the specified GPS value. This will also update the Sun and Moon Phase.");
        btnSetAllGPS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetAllGPS.setFocusPainted(false);
        btnSetAllGPS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSetAllGPS.setIconTextGap(6);
        btnSetAllGPS.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnSetAllGPS.setMaximumSize(new java.awt.Dimension(375, 35));
        btnSetAllGPS.setMinimumSize(new java.awt.Dimension(375, 35));
        btnSetAllGPS.setName("btnSetAllGPS"); // NOI18N
        btnSetAllGPS.setPreferredSize(new java.awt.Dimension(375, 35));
        btnSetAllGPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetAllGPSActionPerformed(evt);
            }
        });
        getContentPane().add(btnSetAllGPS);

        btnSetSunAndMoon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/SunAndMoon_big.png"))); // NOI18N
        btnSetSunAndMoon.setText("Recalculate the Sun (Time of Day) and Moon Phase");
        btnSetSunAndMoon.setToolTipText("Automatically calculate the Sun and Moon Phase for all Observations based on the date and GPS position.");
        btnSetSunAndMoon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetSunAndMoon.setFocusPainted(false);
        btnSetSunAndMoon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSetSunAndMoon.setIconTextGap(6);
        btnSetSunAndMoon.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnSetSunAndMoon.setMaximumSize(new java.awt.Dimension(375, 35));
        btnSetSunAndMoon.setMinimumSize(new java.awt.Dimension(375, 35));
        btnSetSunAndMoon.setName("btnSetSunAndMoon"); // NOI18N
        btnSetSunAndMoon.setPreferredSize(new java.awt.Dimension(375, 35));
        btnSetSunAndMoon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetSunAndMoonActionPerformed(evt);
            }
        });
        getContentPane().add(btnSetSunAndMoon);

        btnSetDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Duration.png"))); // NOI18N
        btnSetDuration.setText("Recalculate the Duration");
        btnSetDuration.setToolTipText("Automatically calculate the duration for all Observations based on the dates of the uploaded files.");
        btnSetDuration.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetDuration.setFocusPainted(false);
        btnSetDuration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSetDuration.setIconTextGap(8);
        btnSetDuration.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnSetDuration.setMaximumSize(new java.awt.Dimension(375, 35));
        btnSetDuration.setMinimumSize(new java.awt.Dimension(375, 35));
        btnSetDuration.setName("btnSetDuration"); // NOI18N
        btnSetDuration.setPreferredSize(new java.awt.Dimension(375, 35));
        btnSetDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetDurationActionPerformed(evt);
            }
        });
        getContentPane().add(btnSetDuration);

        btnCorrectTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/AdjustTime.png"))); // NOI18N
        btnCorrectTime.setText("Adjust the Date and Time for all Observations");
        btnCorrectTime.setToolTipText("Automatically adjust the time of all Observations by the specified amount.");
        btnCorrectTime.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCorrectTime.setFocusPainted(false);
        btnCorrectTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCorrectTime.setIconTextGap(8);
        btnCorrectTime.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnCorrectTime.setMaximumSize(new java.awt.Dimension(375, 35));
        btnCorrectTime.setMinimumSize(new java.awt.Dimension(375, 35));
        btnCorrectTime.setName("btnCorrectTime"); // NOI18N
        btnCorrectTime.setPreferredSize(new java.awt.Dimension(375, 35));
        btnCorrectTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorrectTimeActionPerformed(evt);
            }
        });
        getContentPane().add(btnCorrectTime);

        btnDuplicateSightings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting.gif"))); // NOI18N
        btnDuplicateSightings.setText("Find possible duplicate Observations");
        btnDuplicateSightings.setToolTipText("Indentify possible duplicate Observations for for this Period.");
        btnDuplicateSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDuplicateSightings.setFocusPainted(false);
        btnDuplicateSightings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDuplicateSightings.setIconTextGap(8);
        btnDuplicateSightings.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnDuplicateSightings.setMaximumSize(new java.awt.Dimension(375, 35));
        btnDuplicateSightings.setMinimumSize(new java.awt.Dimension(375, 35));
        btnDuplicateSightings.setName("btnDuplicateSightings"); // NOI18N
        btnDuplicateSightings.setPreferredSize(new java.awt.Dimension(375, 35));
        btnDuplicateSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDuplicateSightingsActionPerformed(evt);
            }
        });
        getContentPane().add(btnDuplicateSightings);

        btnMoveVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/LocationList.gif"))); // NOI18N
        btnMoveVisit.setText("Move this Period to a different Place");
        btnMoveVisit.setToolTipText("Move this Period to a different Place.");
        btnMoveVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMoveVisit.setFocusPainted(false);
        btnMoveVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMoveVisit.setIconTextGap(6);
        btnMoveVisit.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnMoveVisit.setMaximumSize(new java.awt.Dimension(375, 35));
        btnMoveVisit.setMinimumSize(new java.awt.Dimension(375, 35));
        btnMoveVisit.setName("btnMoveVisit"); // NOI18N
        btnMoveVisit.setPreferredSize(new java.awt.Dimension(375, 35));
        btnMoveVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveVisitActionPerformed(evt);
            }
        });
        getContentPane().add(btnMoveVisit);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCorrectTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorrectTimeActionPerformed
        DateChangeDialog dialog = new DateChangeDialog(app, this, visit);
        dialog.setVisible(true);
        panelVisit.doTheRefresh(null);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnCorrectTimeActionPerformed

    private void btnMoveVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveVisitActionPerformed
        getGlassPane().setVisible(true);
        int result = JOptionPane.showConfirmDialog(this,
                        "<html>It is strongly recommended that you backup your Workspace (WildLog folder) before continuing. <br>"
                        + "Do you want to continue now?</html>",
                        "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            MoveVisitDialog dialog = new MoveVisitDialog(app, visit);
            dialog.setVisible(true);
        }
        panelVisit.setVisit(app.getDBI().find(visit));
        panelVisit.setLocationForVisit(app.getDBI().find(new Location(panelVisit.getVisit().getLocationName())));
        panelVisit.doTheRefresh(null);
        getGlassPane().setVisible(false);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnMoveVisitActionPerformed

    private void btnSetAllGPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetAllGPSActionPerformed
        Sighting tempSighting = new Sighting();
        GPSDialog dialog = new GPSDialog(app, this, tempSighting);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            List<Sighting> listSightings = app.getDBI().list(new Sighting(null, null, visit.getName()), false);
            for (Sighting sighting : listSightings) {
                sighting.setLatitude(tempSighting.getLatitude());
                sighting.setLatDegrees(tempSighting.getLatDegrees());
                sighting.setLatMinutes(tempSighting.getLatMinutes());
                sighting.setLatSeconds(tempSighting.getLatSeconds());
                sighting.setLongitude(tempSighting.getLongitude());
                sighting.setLonDegrees(tempSighting.getLonDegrees());
                sighting.setLonMinutes(tempSighting.getLonMinutes());
                sighting.setLonSeconds(tempSighting.getLonSeconds());
                // Because the sighting's GPS point changed I need to recalculate the Sun and Moon phase
                UtilsTime.calculateSunAndMoon(sighting);
                // Save the changes
                app.getDBI().createOrUpdate(sighting, false);
            }
            panelVisit.doTheRefresh(null);
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSetAllGPSActionPerformed

    private void btnSetDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDurationActionPerformed
        List<Sighting> listSightings = app.getDBI().list(new Sighting(null, null, visit.getName()), false);
        for (Sighting sighting : listSightings) {
            List<WildLogFile> files = app.getDBI().list(new WildLogFile(sighting.getWildLogFileID()));
            if (files != null && !files.isEmpty()) {
                Collections.sort(files);
                Date startDate = UtilsImageProcessing.getDateFromFile(files.get(0).getAbsolutePath());
                Date endDate = UtilsImageProcessing.getDateFromFile(files.get(files.size()-1).getAbsolutePath());
                double difference = (endDate.getTime() - startDate.getTime())/1000;
                int minutes = (int)difference/60;
                double seconds = difference - minutes*60.0;
                sighting.setDurationMinutes(minutes);
                sighting.setDurationSeconds(seconds);
            }
            // Save the changes
            app.getDBI().createOrUpdate(sighting, false);
        }
        panelVisit.doTheRefresh(null);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSetDurationActionPerformed

    private void btnDuplicateSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuplicateSightingsActionPerformed
        Path feedbackFile = WildLogPaths.getFullWorkspacePrefix().resolve("DetectDuplicatesFeedback.txt");
        PrintWriter feedback = null;
        try {
            feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
            List<Sighting> listSightings = app.getDBI().list(new Sighting(null, null, visit.getName()), false);
            feedback.println("Checking " + listSightings.size() + " Observations for similarities.");
            feedback.println("___________________________________________________________________________");
            List<Sighting> listSightingsToCompare = app.getDBI().list(new Sighting(null, null, visit.getName()), false);
            for (Sighting sighting : listSightings) {
                for (int t = listSightingsToCompare.size() - 1; t >= 0; t--) {
                    Sighting tempSighting = listSightingsToCompare.get(t);
                    if (sighting.getSightingCounter() == tempSighting.getSightingCounter()) {
                        listSightingsToCompare.remove(t);
                    }
                    else {
                        if (sighting.getElementName().equals(tempSighting.getElementName())
                                && sighting.getLocationName().equals(tempSighting.getLocationName())
                                && sighting.getVisitName().equals(tempSighting.getVisitName())) {
                            int rating = 0;
                            if (sighting.getDate().getTime() == tempSighting.getDate().getTime()) {
                                // The same time
                                rating = rating + 5;
                            }
                            else
                            if (sighting.getDate().getTime()/(600000) == tempSighting.getDate().getTime()/(600000)) {
                                // Only 600000 milliseconds (10 minutes) appart
                                rating = rating + 2;
                            }
                            if (UtilsGps.getLatitudeString(sighting).equals(UtilsGps.getLatitudeString(tempSighting))
                                    && UtilsGps.getLongitudeString(sighting).equals(UtilsGps.getLongitudeString(tempSighting))) {
                                // The same place
                                rating = rating + 5;
                            }
                            else
                            if (Math.abs((int)(UtilsGps.getLatDecimalDegree(sighting)*100) - (int)(UtilsGps.getLatDecimalDegree(tempSighting)*100)) <= 1
                                    && Math.abs((int)(UtilsGps.getLonDecimalDegree(sighting)*100) - (int)(UtilsGps.getLonDecimalDegree(tempSighting)*100)) <= 1) {
                                // GPS values close together
                                rating = rating + 2;
                            }
                            // Check rating
                            if (rating >= 10) {
                                feedback.println("EXTREMELY SIMILAR: " + sighting.getSightingCounter() + " and " + tempSighting.getSightingCounter());
                            }
                            else
                            if (rating >= 7) {
                                feedback.println("VERY SIMILAR     : " + sighting.getSightingCounter() + " and " + tempSighting.getSightingCounter());
                            }
                            else
                            if (rating >= 4) {
                                feedback.println("FAIRLY SIMILAR   : " + sighting.getSightingCounter() + " and " + tempSighting.getSightingCounter());
                            }
                            else
                            if (rating >= 2) {
                                feedback.println("SLIGHTLY SIMILAR : " + sighting.getSightingCounter() + " and " + tempSighting.getSightingCounter());
                            }
                        }
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            if (feedback != null) {
                feedback.flush();
                feedback.close();
            }
            // Open the summary document
            UtilsFileProcessing.openFile(feedbackFile);
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnDuplicateSightingsActionPerformed

    private void btnChecklistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecklistActionPerformed
        ChecklistDialog dialog = new ChecklistDialog(app, this,
                app.getDBI().find(new Location(panelVisit.getVisit().getLocationName())),
                visit, panelVisit);
        dialog.setVisible(true);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnChecklistActionPerformed

    private void btnSetSunAndMoonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetSunAndMoonActionPerformed
        List<Sighting> listSightings = app.getDBI().list(new Sighting(null, null, visit.getName()), false);
        for (Sighting sighting : listSightings) {
            // Recalculate the Sun and Moon phase
            UtilsTime.calculateSunAndMoon(sighting);
            app.getDBI().createOrUpdate(sighting, false);
        }
        panelVisit.doTheRefresh(null);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSetSunAndMoonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChecklist;
    private javax.swing.JButton btnCorrectTime;
    private javax.swing.JButton btnDuplicateSightings;
    private javax.swing.JButton btnMoveVisit;
    private javax.swing.JButton btnSetAllGPS;
    private javax.swing.JButton btnSetDuration;
    private javax.swing.JButton btnSetSunAndMoon;
    // End of variables declaration//GEN-END:variables
}
