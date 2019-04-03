package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.HorizontalBarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.utils.UtilsTime;


public class RelationshipsChart extends AbstractReport<Sighting> {
    private enum ChartType {CHART_ELEMENTS};
    private ChartType chartType;
    private final CheckBox chkCompareVisits;
    private final ComboBox<String> cmbGPS;
    private final ComboBox<String> cmbCompareDates;
    private final ComboBox<String> cmbType;
    private final ComboBox<String> cmbCreature;
    
    
    public RelationshipsChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Relationship Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(8);
        ToggleButton btnPieChartElementTypes = new ToggleButton("Creature Associations");
        btnPieChartElementTypes.setToggleGroup(BUTTON_GROUP);
        btnPieChartElementTypes.setCursor(Cursor.HAND);
        btnPieChartElementTypes.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.CHART_ELEMENTS;
            }
        });
        lstCustomButtons.add(btnPieChartElementTypes);
        // Chart options
        
// TODO: Sit 'n beskrywing onder aan die chart self (en dalk in die titel) wat verduidelik hoe die opsies dinge beinvloed

        lstCustomButtons.add(new Label("Chart Options:"));
        chkCompareVisits = new CheckBox("Must be in the same Period");
        chkCompareVisits.setCursor(Cursor.HAND);
        chkCompareVisits.setSelected(true);
        lstCustomButtons.add(chkCompareVisits);
        cmbGPS = new ComboBox<>(FXCollections.observableArrayList(new String[] {
            "Ignore GPS coordinates", 
            "Compare exact GPS coordinates", 
            "Compare nearby GPS coordinates", 
            "Compare far away GPS coordinates"}));
        cmbGPS.setCursor(Cursor.HAND);
        cmbGPS.setVisibleRowCount(10);
        cmbGPS.getSelectionModel().clearSelection();
        cmbGPS.getSelectionModel().select(1);
        lstCustomButtons.add(cmbGPS);
        cmbCompareDates = new ComboBox<>(FXCollections.observableArrayList(new String[] {
            "Ignore Dates", 
            "Within 1 Day-Night Cycle", 
            "Within 2 Day-Night Cycles", 
            "Within 3 Day-Night Cycles", 
            "Within 7 Day-Night Cycles", 
            "Within 14 Day-Night Cycles"}));
        cmbCompareDates.setCursor(Cursor.HAND);
        cmbCompareDates.setVisibleRowCount(10);
        cmbCompareDates.getSelectionModel().clearSelection();
        cmbCompareDates.getSelectionModel().select(3);
        lstCustomButtons.add(cmbCompareDates);
        cmbType = new ComboBox<>(FXCollections.observableArrayList(new String[] {
            "Total Count", 
            "Total Count (favour exclusivity)", 
            "Related Percentage (minimum)", 
            "Related Percentage (average)", 
            "Related Percentage (maximum)",
            "One-Way Relationships"}));
        cmbType.setCursor(Cursor.HAND);
        cmbType.setVisibleRowCount(10);
        cmbType.getSelectionModel().clearSelection();
        cmbType.getSelectionModel().select(3);
        lstCustomButtons.add(cmbType);
        lstCustomButtons.add(new Label("Filtered Creature:"));
        Set<Long> addedElementNames = new HashSet<>();
        List<String> lstElements = new ArrayList<>();
        lstElements.add("");
        for (Sighting sighting : inLstData) {
            // Only add the name once per element
            if (addedElementNames.add(sighting.getElementID())) {
                lstElements.add(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            }
        }
        Collections.sort(lstElements);
        cmbCreature = new ComboBox<>(FXCollections.observableList(lstElements));
        cmbCreature.setCursor(Cursor.HAND);
        cmbCreature.setVisibleRowCount(10);
        cmbCreature.getSelectionModel().clearSelection();
        lstCustomButtons.add(cmbCreature);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.CHART_ELEMENTS)) {
            setActiveSubCategoryTitle("Creature Associations");
            String info = "<html>This chart shows the number of times two Creatures (species) where observed together.";
            // Visit
            if (chkCompareVisits.isSelected()) {
                info = info + " Only Observations from the same Period are compared to each other.";
            }
            else {
                info = info + " Observations from all Periods are compared to each other.";
            }
            // GPS
            if (cmbGPS.getSelectionModel().isSelected(0)) {
                info = info + " The GPS coordinates are not taken into account.";
            }
            else {
                info = info + " The GPS coordinates are taken into account, if no GPS coordinates are available then the Observation is ignored.";
            }
            // Dates
            if (cmbCompareDates.getSelectionModel().isSelected(0)) {
                info = info + " The Date and Time of Observations are not taken into account.";
            }
            else {
                info = info + " The number of day-night cycles between Observations are compared.";
            }
            // Type
            if (cmbType.getSelectionModel().isSelected(0)) {
                info = info + " <br/>Showing the total number of times two Creatures had related Observations.";
            }
            else
            if (cmbType.getSelectionModel().isSelected(1)) {
                info = info + " <br/><b>Showing:</b> The total number of times two Creatures had related Observations,"
                        + " adjusted by how exclusive the pairing is. The number will be reduced in relation to"
                        + " how often, on average, each Creature was also observed not this related pairing.";
            }
            else
            if (cmbType.getSelectionModel().isSelected(2)) {
                info = info + " <br/><b>Showing:</b> The percentage of Observations where one of the Creatures was related to the other,"
                        + " for the Creature in the pairing with the minimum related percentage.";
            }
            else
            if (cmbType.getSelectionModel().isSelected(3)) {
                info = info + " <br/><b>Showing:</b> The percentage of Observations where one of the Creatures was related to the other,"
                        + " for average related percentage of both Creatures.";
            }
            else
            if (cmbType.getSelectionModel().isSelected(4)) {
                info = info + " <br/><b>Showing:</b> The percentage of Observations where one of the Creatures was related to the other,"
                        + " for the Creature in the pairing with the maximum related percentage.";
            }
            else {
                info = info + " <br/><b>Showing:</b> The two related Creatures with emphasys on the extent to which"
                        + " there is a difference in how strongly the one Creature is related to the other Creature."
                        + " The higher the number the more one-sided the relationship is.";
            }
            info = info + "</html>";
            setupChartDescriptionLabel(info);
            displayedChart = createElementRelationshipChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createElementRelationshipChart(List<Sighting> inSightings) {
        // Get the filtered element's name
        String filterElement = null;
        if (cmbCreature.getSelectionModel().getSelectedIndex() > 0) {
            filterElement = cmbCreature.getSelectionModel().getSelectedItem();
        }
        // Calculate the values
        Set<String> elements = new HashSet<>();
        Map<String, ReportDataWrapper> mapChartData = new HashMap<>();
        Map<String, Integer> elementTotalCounts = new HashMap<>(2);
        Map<Long, String> cacheNames = new HashMap<>(20);
        int maxCount = 0;
        for (Sighting sighting1 : inSightings) {
            String elementName1 = cacheNames.get(sighting1.getElementID());
            if (elementName1 == null) {
                elementName1 = sighting1.getCachedElementName(reportsBaseDialog.getOptionName());
                cacheNames.put(sighting1.getElementID(), elementName1);
            }
            elements.add(elementName1);
            for (Sighting sighting2 : inSightings) {
                if (isRelated(sighting1, sighting2)) {
                    String elementName2 = cacheNames.get(sighting2.getElementID());
                    if (elementName2 == null) {
                        elementName2 = sighting2.getCachedElementName(reportsBaseDialog.getOptionName());
                        cacheNames.put(sighting2.getElementID(), elementName2);
                    }
                    String groupName;
                    if (elementName1.compareTo(elementName2) < 0) {
                        groupName = elementName1 + " - " + elementName2;
                    }
                    else {
                        groupName = elementName2 + " - " + elementName1;
                    }
                    ReportDataWrapper dataWrapper = mapChartData.get(groupName);
                    if (dataWrapper == null) {
                        BubbleInfo bubbleInfo = new BubbleInfo();
                        dataWrapper = new ReportDataWrapper(groupName, bubbleInfo, 0);
                        mapChartData.put(groupName, dataWrapper);
                    }
                    BubbleInfo bubbleInfo = (BubbleInfo) dataWrapper.value;
                    if (!bubbleInfo.processedSightings.contains(sighting1.getID())) {
                        // Keep track of the total number of pairs
                        dataWrapper.increaseCount();
                        bubbleInfo.processedSightings.add(sighting1.getID());
                        if (maxCount < dataWrapper.count) {
                            maxCount = dataWrapper.count;
                        }
                        // Keep track of the number of each Element in the pair
                        int counter = bubbleInfo.elementRatio.getOrDefault(elementName1, 0);
                        bubbleInfo.elementRatio.put(elementName1, counter + 1);
                    }
                    if (!bubbleInfo.processedSightings.contains(sighting2.getID())) {
                        // Keep track of the total number of pairs
                        dataWrapper.increaseCount();
                        bubbleInfo.processedSightings.add(sighting2.getID());
                        if (maxCount < dataWrapper.count) {
                            maxCount = dataWrapper.count;
                        }
                        // Keep track of the number of each Element in the pair
                        int counter = bubbleInfo.elementRatio.getOrDefault(elementName2, 0);
                        bubbleInfo.elementRatio.put(elementName2, counter + 1);
                    }
                }
            }
            // Update the total counts
            int total = elementTotalCounts.getOrDefault(elementName1, 0);
            elementTotalCounts.put(elementName1, total + 1);
        }
        // Get the data in the correct structure for the chart to use
        List<String> keys = new ArrayList<>(mapChartData.keySet());
        Collections.sort(keys);
        BarChart.Series<Number, String> series = new BarChart.Series<>();
        for (String key : keys) {
            if (filterElement != null && !key.contains(filterElement)) {
                // If a filtered element was provided, then skip any other keys
                continue;
            }
            ReportDataWrapper dataWrapper = mapChartData.get(key);
            if (dataWrapper != null && dataWrapper.count > 0) {
                int value = dataWrapper.count;
                if (!cmbType.getSelectionModel().isSelected(0)) {
                    BubbleInfo bubbleInfo = (BubbleInfo) dataWrapper.value;
                    List<String> elementPair = new ArrayList<>(bubbleInfo.elementRatio.keySet());
                    double count1 = bubbleInfo.elementRatio.get(elementPair.get(0));
                    double count2 = bubbleInfo.elementRatio.get(elementPair.get(1));
                    double total1 = elementTotalCounts.get(elementPair.get(0));
                    double total2 = elementTotalCounts.get(elementPair.get(1));
                    double ratio1 = count1 / total1;
                    double ratio2 = count2 / total2;
                    double averageRatio = (ratio1 + ratio2) / 2.0;
                    if (cmbType.getSelectionModel().isSelected(1)) {
                        value = (int) (dataWrapper.count * averageRatio);
                    }
                    else
                    if (cmbType.getSelectionModel().isSelected(2)) {
                        if (ratio1 < ratio2) {
                            value = (int) (ratio1 * 100);
                        }
                        else {
                            value = (int) (ratio2 * 100);
                        }
                    }
                    else
                    if (cmbType.getSelectionModel().isSelected(3)) {
                        value = (int) (averageRatio * 100);
                    }
                    else
                    if (cmbType.getSelectionModel().isSelected(4)) {
                        if (ratio1 > ratio2) {
                            value = (int) (ratio1 * 100);
                        }
                        else {
                            value = (int) (ratio2 * 100);
                        }
                    }
                    else
                    if (cmbType.getSelectionModel().isSelected(5)) {
                        value = (int) (Math.abs(ratio1 - ratio2) * 100);
                    }
                    else {
                        value = 0;
                    }
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>(value, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(mapChartData.size(), data));
                series.getData().add(data);
            }
        }
        // Sort the results
        Collections.sort(series.getData(), new Comparator<BarChart.Data<Number, String>>() {
            @Override
            public int compare(BarChart.Data<Number, String> inData1, BarChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData2.getYValue().compareTo(inData1.getYValue());
                }
                return compare;
            }
        });
        ObservableList<BarChart.Series<Number, String>> chartSeries = FXCollections.observableArrayList();
        chartSeries.add(series);
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapChartData.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, chartSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        if (cmbType.getSelectionModel().isSelected(0)) {
            chart.setTitle("Total Related Observations");
        }
        else
        if (cmbType.getSelectionModel().isSelected(1)) {
            chart.setTitle("Total Related Observations (Adjusted For Exclusivity)");
        }
        else
        if (cmbType.getSelectionModel().isSelected(2)) {
            chart.setTitle("Minimum Percentage Of Related Observations");
        }
        else
        if (cmbType.getSelectionModel().isSelected(3)) {
            chart.setTitle("Average Percentage Of Related Observations");
        }
        else
        if (cmbType.getSelectionModel().isSelected(4)) {
            chart.setTitle("Maximum Percentage Of Related Observations");
        }
        else {
            chart.setTitle("One-Way Relationships");
        }
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private boolean isRelated(Sighting inSighting1, Sighting inSighting2) {
        // Maak seker dis ander Element
        if (inSighting1.getElementID() == inSighting2.getElementID()) {
            return false;
        }
        // Check Visit
        if (chkCompareVisits.isSelected()) {
            if (inSighting1.getVisitID() != inSighting2.getVisitID()) {
                return false;
            }
        }
        // Check GPS
        if (!cmbGPS.getSelectionModel().isSelected(0)) {
            if (!UtilsGPS.hasGPSData(inSighting1) || !UtilsGPS.hasGPSData(inSighting1)) {
                return false;
            }
            double padding;
            if (cmbGPS.getSelectionModel().isSelected(2)) {
                padding = 0.0025;
            }
            else
            if (cmbGPS.getSelectionModel().isSelected(3)) {
                padding = 0.025;
            }
            else {
                padding = 0.0;
            }
            double inNorthEast_Lat = UtilsGPS.getLatDecimalDegree(inSighting1) + padding;
            double inNorthEast_Lon = UtilsGPS.getLonDecimalDegree(inSighting1) + padding;
            double inSouthWest_Lat = UtilsGPS.getLatDecimalDegree(inSighting1) - padding;
            double inSouthWest_Lon = UtilsGPS.getLonDecimalDegree(inSighting1) - padding;
            if (!UtilsGPS.isSightingInBox(inSighting2, inNorthEast_Lat, inNorthEast_Lon, inSouthWest_Lat, inSouthWest_Lon)) {
                return false;
            }
        }
        // Check Date
        if (!cmbCompareDates.getSelectionModel().isSelected(0)) {
            int requieredDays;
            if (cmbCompareDates.getSelectionModel().isSelected(1)) {
                requieredDays = 1;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(2)) {
                requieredDays = 2;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(3)) {
                requieredDays = 3;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(4)) {
                requieredDays = 7;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(5)) {
                requieredDays = 14;
            }
            else {
                requieredDays = Integer.MIN_VALUE;
            }
            long dayDifference = Math.abs(ChronoUnit.DAYS.between(getAdjustedDate(inSighting1), getAdjustedDate(inSighting2)));
            if (dayDifference > requieredDays){
                return false;
            }
        }
        // As alles reg was, dan is die Sightings verwant aan mekaar
        return true;
    }
    
    private LocalDate getAdjustedDate(Sighting inSighting) {
        LocalDateTime adjustedDate = UtilsTime.getLocalDateTimeFromDate(inSighting.getDate());
        // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
        if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(inSighting.getTimeOfDay()))) {
            if (adjustedDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && adjustedDate.toLocalTime().isBefore(LocalTime.NOON)) {
                adjustedDate = adjustedDate.minusDays(1);
            }
        }
        return adjustedDate.toLocalDate();
    }
    
    private class BubbleInfo {
        public Set<Long> processedSightings = new HashSet<>();
        public Map<String, Integer> elementRatio = new HashMap<>(2);
    }
    
}
