package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.utils.UtilsTime;


public class TextReports extends AbstractReport<Sighting> {
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private GridPane displayedReport;
    
    public TextReports() {
        super("Text Summaries", "<html>This is a collection text summaries.</html>");
        lstCustomButtons = new ArrayList<>(1);
        // Area/Line Chart
        JButton btnTextReport = new JButton("Data Summaries");
        btnTextReport.setFocusPainted(false);
        btnTextReport.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTextReport.setMargin(new Insets(2, 4, 2, 4));
        btnTextReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (displayedReport != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedReport.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnTextReport);
        
    }

    @Override
    public GridPane createReport() {
        displayedReport = new GridPane();
        displayedReport.setBackground(Background.EMPTY);
        displayedReport.setPadding(new javafx.geometry.Insets(15, 10, 15, 10));
        displayedReport.setHgap(7);
        displayedReport.setVgap(20);
        // Setup the data to show
        Map<String, String> mapDataTwos = new LinkedHashMap<>(10);
        Map<String, String> mapDataThrees = new LinkedHashMap<>(20);
        // Setup generic date
        Set<String> setElementNames = new HashSet<>();
        Set<String> setLocationNames = new HashSet<>();
        Set<String> setVisitNames = new HashSet<>();
        for (Sighting sighting : lstData) {
            setElementNames.add(sighting.getElementName());
            setLocationNames.add(sighting.getLocationName());
            setVisitNames.add(sighting.getVisitName());
        }
        // Dates
        if (!lstData.isEmpty()) {
            Collections.sort(lstData);
            mapDataTwos.put("First Observation: ", UtilsTime.WL_DATE_FORMATTER.format(lstData.get(0).getDate()));
            mapDataTwos.put("Last Observation: ", UtilsTime.WL_DATE_FORMATTER.format(lstData.get(lstData.size() - 1).getDate()));
            mapDataTwos.put("First Place: ", lstData.get(0).getLocationName());
            mapDataTwos.put("Last Place: ", lstData.get(lstData.size() - 1).getLocationName());
            mapDataTwos.put("First Creature: ", lstData.get(0).getElementName());
            mapDataTwos.put("Last Creature: ", lstData.get(lstData.size() - 1).getElementName());
        }
        // Number of Sightings
        mapDataThrees.put("Total Observations: ", Integer.toString(lstData.size()));
        // Max/Min Sightings of Element per Location
        Map<String, ReportDataWrapper> mapMinMax = new HashMap<>(setElementNames.size() * setLocationNames.size());
        for (Sighting sighting : lstData) {
            ReportDataWrapper countWrapper = mapMinMax.get(sighting.getElementName());
            if (countWrapper == null) {
                countWrapper = new ReportDataWrapper(sighting.getElementName(), null, 0);
                mapMinMax.put(sighting.getElementName(), countWrapper);
            }
            countWrapper.count++;
        }
        int min = mapMinMax.size();
        int ave = 0;
        int max = 0;
        for (ReportDataWrapper dataWrapper : mapMinMax.values()) {
            if (dataWrapper.count < min) {
                min = dataWrapper.count;
            }
            if (dataWrapper.count > max) {
                max = dataWrapper.count;
            }
        }
        mapDataThrees.put("Least Observations per Creature: ", Integer.toString(min));
        mapDataThrees.put("Most Observations per Creature: ", Integer.toString(max));
        // Number of Elements, Locations, Visits
        mapDataThrees.put("Number of Creatures: ", Integer.toString(setElementNames.size()));
        mapDataThrees.put("Number of Places: ", Integer.toString(setLocationNames.size()));
        mapDataThrees.put("Number of Periods: ", Integer.toString(setVisitNames.size()));
        // Elements per Location
        Map<String, Set<String>> mapCounts = new HashMap<>(setLocationNames.size());
        for (Sighting sighting : lstData) {
            Set<String> countSet = mapCounts.get(sighting.getLocationName());
            if (countSet == null) {
                countSet = new HashSet<>(setElementNames.size());
                mapCounts.put(sighting.getLocationName(), countSet);
            }
            countSet.add(sighting.getElementName());
        }
        min = lstData.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        mapDataThrees.put("Least Creatures per Place: ", Integer.toString(min));
        mapDataThrees.put("Average Creatures per Place: ", decimalFormat.format((double)ave / (double)setLocationNames.size()));
        mapDataThrees.put("Most Creatures per Place: ", Integer.toString(max));
        // Elements per Visit
        mapCounts = new HashMap<>(setVisitNames.size());
        for (Sighting sighting : lstData) {
            Set<String> countSet = mapCounts.get(sighting.getVisitName());
            if (countSet == null) {
                countSet = new HashSet<>(setElementNames.size());
                mapCounts.put(sighting.getVisitName(), countSet);
            }
            countSet.add(sighting.getElementName());
        }
        min = lstData.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        mapDataThrees.put("Least Creatures per Period: ", Integer.toString(min));
        mapDataThrees.put("Average Creatures per Period: ", decimalFormat.format((double)ave / (double)setVisitNames.size()));
        mapDataThrees.put("Most Creatures per Period: ", Integer.toString(max));
        // Visits per Location
        mapCounts = new HashMap<>(setLocationNames.size());
        for (Sighting sighting : lstData) {
            Set<String> countSet = mapCounts.get(sighting.getLocationName());
            if (countSet == null) {
                countSet = new HashSet<>(setVisitNames.size());
                mapCounts.put(sighting.getLocationName(), countSet);
            }
            countSet.add(sighting.getVisitName());
        }
        min = lstData.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        mapDataThrees.put("Least Periods per Place: ", Integer.toString(min));
        mapDataThrees.put("Average Periods per Place: ", decimalFormat.format((double)ave / (double)setLocationNames.size()));
        mapDataThrees.put("Most Periods per Place: ", Integer.toString(max));
        // Display the values
        GridPane gridPaneTwos = new GridPane();
        gridPaneTwos.setBackground(Background.EMPTY);
        gridPaneTwos.setHgap(displayedReport.getHgap());
        gridPaneTwos.setVgap(displayedReport.getVgap());
        int row = 0;
        int col = 0;
        for (String label : mapDataTwos.keySet()) {
            gridPaneTwos.add(new Label(label), col++, row);
            Label value = new Label(mapDataTwos.get(label));
            gridPaneTwos.add(value, col++, row);
            if (col > 4) {
                col = 0;
                row++;
            }
            else {
                // Insert some spacing
                gridPaneTwos.add(new Label("   "), col++, row);
            }
        }
        displayedReport.add(gridPaneTwos, 0, 0, 9, 1);
        row = 1;
        col = 0;
        for (String label : mapDataThrees.keySet()) {
            displayedReport.add(new Label(label), col++, row);
            Label value = new Label(mapDataThrees.get(label));
            if (value.getText().length() < 6) {
                displayedReport.add(value, col++, row);
            }
            else {
                displayedReport.add(value, col, row, 3, 1);
                col = col + 4;
            }
            if (col > 6) {
                col = 0;
                row++;
            }
            else {
                // Insert some spacing
                displayedReport.add(new Label("   "), col++, row);
            }
        }
        return displayedReport;
    }
    
}
