package wildlog.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogPaths;


public final class ReportOccupanyModelForR extends JDialog {
    private static final String EXTRADATA_COVARIATE_PREFIX = "covariate.";
    private String reportTitle;
    
    public ReportOccupanyModelForR(String inTitle) {
        super(WildLogApp.getApplication().getMainFrame());
        reportTitle = inTitle;
        initComponents();
        pack();
        setMinimumSize(getSize());
        ComboBoxFixer.configureComboBoxes(cmbCertainty);
        SpinnerFixer.configureSpinners(spnInterval);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        // Set the default dates
        dtpStartDate.setDate(UtilsTime.getDateFromLocalDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())));
        dtpEndDate.setDate(UtilsTime.getDateFromLocalDate(LocalDate.now().with(TemporalAdjusters.lastDayOfYear())));
        // Left align die text in die spinner, om by die ander velde te pas
        JComponent editor = spnInterval.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnProcess = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spnInterval = new javax.swing.JSpinner();
        cmbCertainty = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("R Occupancy Model");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif")).getImage());
        setModal(true);

        btnProcess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnProcess.setToolTipText("Generate the Occupancy Model files for each Creature.");
        btnProcess.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("R Occupancy Model");

        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Start Date:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("End Date:");

        dtpEndDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Intervals:");

        jLabel5.setText("<html>This report will generate a CSV files containing occupancy model data for each Creature.</html>");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Certainty:");

        spnInterval.setModel(new javax.swing.SpinnerNumberModel(4, 0, 365, 1));
        spnInterval.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnInterval.setEditor(new javax.swing.JSpinner.NumberEditor(spnInterval, "#"));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(Certainty.GOOD);
        cmbCertainty.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel7.setText("<html><b>Place Covariates:</b>\n<br/>Can be linked to Places as Extra Data fields.\n<br/>For example:\n<br/> - covariate.road.length.adj\n<br/> - covariate.av.area.adj\n</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dtpEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dtpStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spnInterval)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cmbCertainty, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spnInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbCertainty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed
        // Validation
        if (dtpStartDate.getDate() == null || dtpEndDate.getDate() == null
                || UtilsTime.getLocalDateFromDate(dtpStartDate.getDate()).isAfter(UtilsTime.getLocalDateFromDate(dtpEndDate.getDate()))) {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please specify valid start and end dates.</html>",
                    "Invalid Report Parameters", WLOptionPane.WARNING_MESSAGE);
            return;
        }
        UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the Report: " + reportTitle);
                setTaskProgress(1);
                Path reportFolder = null;
                // Start cleanup
                try {
                    // Setup the feedback file
                    reportFolder = WildLogPaths.WILDLOG_EXPORT_REPORTS.getAbsoluteFullPath().resolve(reportTitle);
                    // Make sure the folder is empty
                    try {
                        UtilsFileProcessing.deleteRecursive(reportFolder.toFile());
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                "<html>The report could not be created. "
                                        + "<br/>If the files are already open in another application, please close it and try again.</html>",
                                "Report Error", WLOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    Files.createDirectories(reportFolder);
                    // Load the data
                    setTaskProgress(1);
                    setMessage("Busy with the Report: " + reportTitle + " - Loading Data... " + getProgress() + "%");
                    List<Sighting> lstSightings = WildLogApp.getApplication().getDBI().searchSightings(
                            null, dtpStartDate.getDate(), dtpEndDate.getDate(), null, null, null, true, Sighting.class);
                    // Process the data
                    setTaskProgress(10);
                    setMessage("Busy with the Report: " + reportTitle + " - Processing Data... " + getProgress() + "%");
                    LocalDate startDate = UtilsTime.getLocalDateFromDate(dtpStartDate.getDate());
                    int intervals = (int) spnInterval.getValue();
                    int totalDays = (int) ChronoUnit.DAYS.between(startDate, UtilsTime.getLocalDateFromDate(dtpEndDate.getDate())) + 1; // +1 om die eerste dag ook te tel
                    int daysPerInterval = totalDays / intervals;
                    int elementCount = WildLogApp.getApplication().getDBI().countElements(null, null);
                    Map<ElementData, Map<LocationData, OccupancyData>> mapAllData = new HashMap<>(elementCount);
                    Map<Long, ElementData> mapCachedElementData = new HashMap<>(elementCount);
                    List<Location> lstAllLocations = WildLogApp.getApplication().getDBI().listLocations(null, false, Location.class);
                    Map<Long, LocationData> mapCachedLocationData = new HashMap<>(lstAllLocations.size());
                    Set<String> setLocationCoveriates = new TreeSet<>();
                    for (Location location : lstAllLocations) {
                        mapCachedLocationData.computeIfAbsent(location.getID(), (key) -> {
                                LocationData newLocationData = new LocationData();
                                newLocationData.id = location.getID();
                                newLocationData.name = location.getName();
                                List<ExtraData> lstAllExtraData = WildLogApp.getApplication().getDBI().listExtraDatas(
                                        WildLogExtraDataFieldTypes.USER, location.getID(), ExtraData.class);
                                newLocationData.mapExtraData = new HashMap<>(2);
                                if (lstAllExtraData != null && !lstAllExtraData.isEmpty()) {
                                    for (ExtraData extraData : lstAllExtraData) {
                                        if (extraData.getDataKey().startsWith(EXTRADATA_COVARIATE_PREFIX)) {
                                            newLocationData.mapExtraData.put(extraData.getDataKey(), extraData);
                                            setLocationCoveriates.add(extraData.getDataKey());
                                        }
                                    }
                                }
                                return newLocationData;
                            });
                        setTaskProgress(10 + (int) (((double) mapCachedLocationData.size() / (double) lstAllLocations.size()) * 5.0));
                        setMessage("Busy with the Report: " + reportTitle + " - Processing Data... " + getProgress() + "%");
                    }
                    setTaskProgress(15);
                    setMessage("Busy with the Report: " + reportTitle + " - Processing Data... " + getProgress() + "%");
                    List<Certainty> lstCertainties = Arrays.asList(Certainty.values());
                    for (Sighting sighting : lstSightings) {
                        // Make sure the Certianty is valid
                        if (lstCertainties.indexOf(sighting.getCertainty()) <= lstCertainties.indexOf(cmbCertainty.getSelectedItem())) {
                            // Get (and set, if new) the ElementData for this Sighting
                            ElementData elementData = mapCachedElementData.computeIfAbsent(sighting.getElementID(), (key) -> {
                                // Create new ElementData
                                ElementData newElementData = new ElementData();
                                newElementData.id = sighting.getElementID();
                                newElementData.name = sighting.getCachedElementName();
                                return newElementData;
                            });
                            // Get (and set, if new) the LocationData and OccupancyData mapping for the specific Element
                            Map<LocationData, OccupancyData> mapDataPerElement = mapAllData.computeIfAbsent(elementData, (key) -> {
                                // Create new LocationData and OccupancyData mapping
                                Map<LocationData, OccupancyData> newMapDataPerElement = new HashMap<>(lstAllLocations.size());
                                // Create new empty LocationData entries for all Locations in the database
                                for (LocationData locationData : mapCachedLocationData.values()) {
                                    newMapDataPerElement.computeIfAbsent(locationData, (locationKey) -> {
                                        // Create new OccupancyData for the Location
                                        OccupancyData newOccupancyData = new OccupancyData();
                                        // Create the initial 0 value for each interval's presence value
                                        newOccupancyData.lstPressences = new ArrayList<>(intervals);
                                        for (int i = 0; i < intervals; i++) {
                                            newOccupancyData.lstPressences.add(0);
                                        }
                                        return newOccupancyData;
                                    });
                                }
                                return newMapDataPerElement;
                            });
                            // Get the LocationData for this Sighting
                            LocationData locationData = mapCachedLocationData.get(sighting.getLocationID());
                            // Get the OccupancyData for this Sighting's Location
                            OccupancyData occupancyData = mapDataPerElement.get(locationData);
                            // Populate the presence values
                            long sightingDaysFromStart = ChronoUnit.DAYS.between(startDate, UtilsTime.getLocalDateFromDate(sighting.getDate()));
                            int sightingInterval = (int) sightingDaysFromStart / daysPerInterval;
                            occupancyData.lstPressences.set(sightingInterval, occupancyData.lstPressences.get(sightingInterval) + 1);
                        }
                    }
                    // Write the files
                    setTaskProgress(75);
                    setMessage("Busy with the Report: " + reportTitle + " - Writing Data... " + getProgress() + "%");
                    double counter = 0.0;
                    for (Map.Entry<ElementData, Map<LocationData, OccupancyData>> elementEntry : mapAllData.entrySet()) {
                        try (PrintWriter feedback = new PrintWriter(
                                new FileWriter(reportFolder.resolve(elementEntry.getKey().name + ".csv").toFile()), true)) {
                            // Write the header
                            feedback.print("site");
                            for (int i = 0; i < intervals; i++) {
                                feedback.print(",present." + (i + 1));
                            }
                            for (String locationCovariate : setLocationCoveriates) {
                                feedback.print("," + locationCovariate);
                            }
                            for (int i = 0; i < intervals; i++) {
                                feedback.print(",average." + (i + 1));
                            }
                            for (int i = 0; i < intervals; i++) {
                                feedback.print(",total." + (i + 1));
                            }
                            feedback.println();
                            // Write the data
                            List<LocationData> lstSortedLocationData = new ArrayList<>(elementEntry.getValue().keySet());
                            Collections.sort(lstSortedLocationData);
                            for (LocationData locationData : lstSortedLocationData) {
                                OccupancyData occupancyData = elementEntry.getValue().get(locationData);
                                feedback.print(locationData.name);
                                for (int i = 0; i < intervals; i++) {
                                    if (occupancyData.lstPressences.get(i) == 0) {
                                        feedback.print(",0");
                                    }
                                    else {
                                        feedback.print(",1");
                                    }
                                }
                                for (String locationCovariate : setLocationCoveriates) {
                                    if (locationData.mapExtraData.get(locationCovariate) != null) {
                                        feedback.print("," + locationData.mapExtraData.get(locationCovariate).getDataValue());
                                    }
                                    else {
                                        feedback.print(",0");
                                    }
                                }
                                for (int i = 0; i < intervals; i++) {
                                    feedback.print("," + (Math.round(((double) occupancyData.lstPressences.get(i)) / (double) daysPerInterval * 1000.0)) /1000.0);
                                }
                                for (int i = 0; i < intervals; i++) {
                                    feedback.print("," + occupancyData.lstPressences.get(i));
                                }
                                feedback.println();
                            }
                            // Flush, just to be sure
                            feedback.flush();
                        }
                        catch (IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                    setTaskProgress(75 + (int) ((counter++ / (double) mapAllData.size()) * 24.0));
                    setMessage("Busy with the Report: " + reportTitle + " - Writing Data... " + getProgress() + "%");
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                // Open the report folder
                if (reportFolder != null) {
                    setTaskProgress(99);
                    setMessage("Busy with the Report: " + reportTitle + " - Opening Report Folder... " + getProgress() + "%");
                    UtilsFileProcessing.openFile(reportFolder);
                }
                setTaskProgress(100);
                setMessage("Done with the Report: " + reportTitle);
                return null;
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnProcessActionPerformed

    private class ElementData {
        long id;
        String name;
        
        @Override
        public boolean equals(Object inLocationData) {
            if (inLocationData instanceof LocationData) {
                return id == ((LocationData) inLocationData).id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
            return hash;
        }
        
    }
    
    private class LocationData implements Comparable<LocationData> {
        long id;
        String name;
        Map<String, ExtraData> mapExtraData;

        @Override
        public boolean equals(Object inLocationData) {
            if (inLocationData instanceof LocationData) {
                return id == ((LocationData) inLocationData).id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (int) (this.id ^ (this.id >>> 32));
            return hash;
        }

        @Override
        public int compareTo(LocationData inLocationData) {
            return name.compareTo(inLocationData.name);
        }
        
    }
    
    private class OccupancyData {
        List<Integer> lstPressences;
        //List<Integer> lstCovariates;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProcess;
    private javax.swing.JComboBox<String> cmbCertainty;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner spnInterval;
    // End of variables declaration//GEN-END:variables
}