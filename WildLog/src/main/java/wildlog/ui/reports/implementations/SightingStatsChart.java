package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.HorizontalBarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportCountWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.utils.UtilsTime;


public class SightingStatsChart extends AbstractReport<Sighting> {
    private enum ChartType {NUMBER_PER_SIGHTING_CHART, SUBSEQUENT_CHART, SIGHTINGS_PER_DAY_CHART, /*ABUNDANCE_CHART, */FIRST_SIGHTING_CHART};
    private ChartType chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
    private final ComboBox<String> cmbOption;
// TODO: Nog opsies:
//        [Most Frequent (Mode)]
//        [Average (Mean)]
//        [Weighted Average (Weighted Mean)] -> The more time a value is present the more relevant it becomes
    private final String[] options = new String[] {"Minimum", "Average", "Maximum"};
      
    
    public SightingStatsChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Observation Statistics Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        // Bar charts
        ToggleButton btnSightingsPerDayBarChart = new ToggleButton("Observations per Day-Cycle");
        btnSightingsPerDayBarChart.setToggleGroup(BUTTON_GROUP);
        btnSightingsPerDayBarChart.setCursor(Cursor.HAND);
        btnSightingsPerDayBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                cmbOption.setDisable(false);
                chartType = ChartType.SIGHTINGS_PER_DAY_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a full day-night cycle for each of the listed Creatures. "
                        + "<br/>(A day-night cycle starts at dawn and continues throughout the day into the night and ends at the next dawn.)</html>");
            }
        });
        lstCustomButtons.add(btnSightingsPerDayBarChart);
        ToggleButton btnElementPerSightingBarChart = new ToggleButton("Number of Individuals");
        btnElementPerSightingBarChart.setToggleGroup(BUTTON_GROUP);
        btnElementPerSightingBarChart.setCursor(Cursor.HAND);
        btnElementPerSightingBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                cmbOption.setDisable(false);
                chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of individuals recorded in the Observation record for each of the listed Creatures."
                        + "<br/>Note: Only Observation records with data values are taken into account (the record must have more than 0 number of individuals recorded).</html>");
            }
        });
        lstCustomButtons.add(btnElementPerSightingBarChart);
        ToggleButton btnBarChartFirstSighting = new ToggleButton("Days to first Observation");
        btnBarChartFirstSighting.setToggleGroup(BUTTON_GROUP);
        btnBarChartFirstSighting.setCursor(Cursor.HAND);
        btnBarChartFirstSighting.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                cmbOption.setDisable(false);
                chartType = ChartType.FIRST_SIGHTING_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of days that passed, per Period, before the a Creature was first Observed.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartFirstSighting);
        ToggleButton btnSightingChanceBarChart = new ToggleButton("Subsequent Observations");
        btnSightingChanceBarChart.setToggleGroup(BUTTON_GROUP);
        btnSightingChanceBarChart.setCursor(Cursor.HAND);
        btnSightingChanceBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                cmbOption.setDisable(true);
                chartType = ChartType.SUBSEQUENT_CHART;
                setupChartDescriptionLabel("<html>This chart shows the percentage of full day-night cycles with more than one Observation for each of the listed Creatures. "
                        + "The Start Date of the earliest Period, otherwise the first Observation, will be used as the initial starting date."
                        + "<br/>(A day-night cycle starts at dawn and continues throughout the day into the night and ends at the next dawn.)</html>");
            }
        });
        lstCustomButtons.add(btnSightingChanceBarChart);
// TODO: Skuif die chart na 'n ander group. Ek comment dit vir eers uit want dit dra tans niks by nie en moet nog verander om 'n "log curve" te gebruik...
//        ToggleButton btnBarChartRelativeAbundance = new ToggleButton("Relative Abundance (Bar)");
//        btnBarChartRelativeAbundance.setToggleGroup(BUTTON_GROUP);
//        btnBarChartRelativeAbundance.setCursor(Cursor.HAND);
//        btnBarChartRelativeAbundance.setOnAction(new EventHandler() {
//            @Override
//            public void handle(Event event) {
//                chartType = ChartType.ABUNDANCE_CHART;
//                setupChartDescriptionLabel("<html>This chart can be used as a basic analysis of Relative Species Abundance. It shows the number of Creatures with the specified number of total Observations.</html>");
//            }
//        });
//        lstCustomButtons.add(btnBarChartRelativeAbundance);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        cmbOption = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbOption.setCursor(Cursor.HAND);
        cmbOption.setVisibleRowCount(10);
        cmbOption.getSelectionModel().clearSelection();
        cmbOption.getSelectionModel().select(1);
        lstCustomButtons.add(cmbOption);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.NUMBER_PER_SIGHTING_CHART)) {
            setActiveSubCategoryTitle("Number of Individuals");
            displayedChart = createNumOfElementsPerSightingBarChart(lstData);
        }
        else
        if (chartType.equals(ChartType.SUBSEQUENT_CHART)) {
            setActiveSubCategoryTitle("Subsequent Observations");
            displayedChart = createSubsequentSightingBarChart(lstData);
        }
        else
        if (chartType.equals(ChartType.SIGHTINGS_PER_DAY_CHART)) {
            setActiveSubCategoryTitle("Observations per Day-Cycle");
            displayedChart = createSightingPerDayBarChart(lstData);
        }
//        else
//        if (chartType.equals(ChartType.ABUNDANCE_CHART)) {
//            setActiveSubCategoryTitle("Relative Abundance (Bar)");
//            displayedChart = createAbundanceBarChart(lstData);
//        }
        else
        if (chartType.equals(ChartType.FIRST_SIGHTING_CHART)) {
            setActiveSubCategoryTitle("Days to first Observation");
            displayedChart = createFirstSightingBarChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createNumOfElementsPerSightingBarChart(List<Sighting> inSightings) {
        // Get the data structured
        Map<String, ReportCountWrapper> mapElemNum = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportCountWrapper countWrapper = mapElemNum.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (countWrapper == null) {
                countWrapper = new ReportCountWrapper(0, 0, 0, 0);
                mapElemNum.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), countWrapper);
                countWrapper.min = Double.MAX_VALUE;
            }
            if (sighting.getNumberOfElements() > 0) {
                countWrapper.total++;
            }
            countWrapper.count = countWrapper.count + sighting.getNumberOfElements();
            if (sighting.getNumberOfElements() > countWrapper.max) {
                countWrapper.max = sighting.getNumberOfElements();
            }
            if (sighting.getNumberOfElements() < countWrapper.min) {
                countWrapper.min = sighting.getNumberOfElements();
            }
        }
        // Create the series
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemNum.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if (cmbOption.getSelectionModel().isSelected(0)) { // Min
                double minValue = mapElemNum.get(key).min;
                if (minValue == Double.MAX_VALUE) {
                    minValue = 0;
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>(minValue, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(1)) { // Ave
                BarChart.Data<Number, String> data = new BarChart.Data<>(
                        Math.round((mapElemNum.get(key).count/mapElemNum.get(key).total)*100.0)/100.0, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(2)) { // Max
                BarChart.Data<Number, String> data = new BarChart.Data<>(mapElemNum.get(key).max, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
        }
        // Sort the results
        Collections.sort(lstCountForElements, new Comparator<XYChart.Data<Number, String>>() {
            @Override
            public int compare(XYChart.Data<Number, String> inData1, XYChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getYValue().compareTo(inData2.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis();
        String indicator = "";
        if (cmbOption.getSelectionModel().isSelected(0)) {
            indicator = "Minimum ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(1)) {
            indicator = "Average ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(2)) {
            indicator = "Maximum ";
        }
        UtilsReports.setupNumberAxis(numAxis, true);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapElemNum.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, lstFinalChartDataSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle(indicator + "number of individuals seen per Observation");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private Chart createSightingPerDayBarChart(List<Sighting> inSightings) {
        // Count how many sightings are on the same day/night (remember that the night streches over two calendar days)
        // Get the data structured
        Map<String, Map<LocalDate, ReportCountWrapper>> mapElemCount = new HashMap<>();
        for (Sighting sighting : inSightings) {
            LocalDateTime currentDate = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (mapCount == null) {
                mapCount = new HashMap<>();
                mapElemCount.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), mapCount);
            }
            if (ActiveTime.DAY.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Days are easier because the dates simply need to be on the same day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
            else
            if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
                LocalDate adjustedDate = currentDate.toLocalDate();
                if (currentDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && currentDate.toLocalTime().isBefore(LocalTime.NOON)) {
                    adjustedDate = currentDate.toLocalDate().minusDays(1);
                }
                ReportCountWrapper countWrapper = mapCount.get(adjustedDate);
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(adjustedDate, countWrapper);
                }
                countWrapper.total++;
            }
            else {
                // For twilight or anything else it is assumed that it will still be on the same calendar day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
        }
        // Create the series
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemCount.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            double maxSightingsPerDay = 0;
            double minSightingsPerDay = Double.MAX_VALUE;
            double totalNumberOfSightings = 0;
            double totalNumberOfDaysSighted = 0;
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(key);
            for (LocalDate localDate : mapCount.keySet()) {
                double total = mapCount.get(localDate).total;
                if (maxSightingsPerDay < total) {
                    maxSightingsPerDay = total;
                }
                if (minSightingsPerDay > total) {
                    minSightingsPerDay = total;
                }
                totalNumberOfSightings = totalNumberOfSightings + total;
                totalNumberOfDaysSighted++;
            }
            if (cmbOption.getSelectionModel().isSelected(0)) { // Min
                if (minSightingsPerDay == Double.MAX_VALUE) {
                    minSightingsPerDay = 0;
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>(minSightingsPerDay, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(1)) { // Ave
                BarChart.Data<Number, String> data = new BarChart.Data<>(
                        Math.round((totalNumberOfSightings/totalNumberOfDaysSighted)*100.0)/100.0, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(2)) { // Max
                BarChart.Data<Number, String> data = new BarChart.Data<>(maxSightingsPerDay, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
        }
        // Sort the results
        Collections.sort(lstCountForElements, new Comparator<XYChart.Data<Number, String>>() {
            @Override
            public int compare(XYChart.Data<Number, String> inData1, XYChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getYValue().compareTo(inData2.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis();
        String indicator = "";
        if (cmbOption.getSelectionModel().isSelected(0)) {
            indicator = "Minimum ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(1)) {
            indicator = "Average ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(2)) {
            indicator = "Maximum ";
        }
        UtilsReports.setupNumberAxis(numAxis, true);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapElemCount.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, lstFinalChartDataSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle(indicator + "number of Observations per day-night cycle");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private Chart createSubsequentSightingBarChart(List<Sighting> inSightings) {
        // Count how many sightings are on the same day/night (remember that the night streches over two calendar days)
        // Get the data structured
        Map<String, Map<LocalDate, ReportCountWrapper>> mapElemCount = new HashMap<>();
        for (Sighting sighting : inSightings) {
            LocalDateTime currentDate = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (mapCount == null) {
                mapCount = new HashMap<>();
                mapElemCount.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), mapCount);
            }
            if (ActiveTime.DAY.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Days are easier because the dates simply need to be on the same day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
            else
            if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
                LocalDate adjustedDate = currentDate.toLocalDate();
                if (currentDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && currentDate.toLocalTime().isBefore(LocalTime.NOON)) {
                    adjustedDate = currentDate.toLocalDate().minusDays(1);
                }
                ReportCountWrapper countWrapper = mapCount.get(adjustedDate);
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(adjustedDate, countWrapper);
                }
                countWrapper.total++;
            }
            else {
                // For twilight or anything else it is assumed that it will still be on the same calendar day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
        }
        // Create the series
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemCount.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            double maxSightingsPerDay = 0;
            double totalNumberOfDaysSighted = 0;
            double daysWithMultipleSightings = 0;
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(key);
            for (LocalDate localDate : mapCount.keySet()) {
                double total = mapCount.get(localDate).total;
                if (maxSightingsPerDay < total) {
                    maxSightingsPerDay = total;
                }
                if (total >= 2) {
                    daysWithMultipleSightings++;
                }
                totalNumberOfDaysSighted++;
            }
            BarChart.Data<Number, String> data = new BarChart.Data<>(
                    Math.round((daysWithMultipleSightings/totalNumberOfDaysSighted)*100.0), key);
            data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
            lstCountForElements.add(data);
        }
        // Sort the results
        Collections.sort(lstCountForElements, new Comparator<XYChart.Data<Number, String>>() {
            @Override
            public int compare(XYChart.Data<Number, String> inData1, XYChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getYValue().compareTo(inData2.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis(0, 100, 10);
        numAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Integer.toString(((Double)object).intValue()) + "%";
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string.substring(0, string.length() - 1));
            }
        });
        UtilsReports.setupNumberAxis(numAxis, true);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapElemCount.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, lstFinalChartDataSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Percentage of day-night cycles with multiple Observations");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
//    private Chart createAbundanceBarChart(List<Sighting> inSightings) {
//        // Count the number of Sightings for each Element
//        Map<String, ReportDataWrapper> mapElementSightingCount = new HashMap<>();
//        for (Sighting sighting : inSightings) {
//            ReportDataWrapper dataWrapper = mapElementSightingCount.get(sighting.getElementName(reportsBaseDialog.getOptionName()));
//            if (dataWrapper == null) {
//                dataWrapper = new ReportDataWrapper(sighting.getElementName(reportsBaseDialog.getOptionName()), null, 0);
//                mapElementSightingCount.put(sighting.getElementName(reportsBaseDialog.getOptionName()), dataWrapper);
//            }
//            dataWrapper.increaseCount();
//        }
//        // Group similar Sighting coutns together
//        int maxCount = 0;
//        Map<Integer, ReportDataWrapper> mapGroupedCounts = new HashMap<>();
//        for (ReportDataWrapper elementDataWrapper : mapElementSightingCount.values()) {
//            ReportDataWrapper groupedDataWrapper = mapGroupedCounts.get(elementDataWrapper.count);
//            if (groupedDataWrapper == null) {
//                groupedDataWrapper = new ReportDataWrapper(Integer.toString(elementDataWrapper.count), elementDataWrapper.key, 0);
//                mapGroupedCounts.put(elementDataWrapper.count, groupedDataWrapper);
//            }
//            else {
//                groupedDataWrapper.value = groupedDataWrapper.value + ", " + elementDataWrapper.key;
//            }
//            groupedDataWrapper.increaseCount();
//            if (maxCount < elementDataWrapper.count) {
//                maxCount = elementDataWrapper.count;
//            }
//        }
//        // Create the series
//        ObservableList<BarChart.Series<String, Number>> lstFinalChartDataSeries = FXCollections.observableArrayList();
//        ObservableList<BarChart.Data<String, Number>> lstSeriesData = FXCollections.observableArrayList();
//        for (int count = 1; count <= maxCount; count++) {
//            ReportDataWrapper dataWrapper = mapGroupedCounts.get(count);
//            if (dataWrapper != null) {
//                // Add the data to the chart
//// TODO: Ek dink hulle gebruik Log of iets om die groter getalle saam te groepeer, sodat daar nie sulke groot gate is nie...
//                lstSeriesData.add(new BarChart.Data<String, Number>(count + " Obs", dataWrapper.count, dataWrapper.value));
//            }
//            else {
//                lstSeriesData.add(new BarChart.Data<String, Number>(count + " Obs", 0, "<No Data>"));
//            }
//        }
//        // Sort the results
//        Collections.sort(lstSeriesData, new Comparator<XYChart.Data<String, Number>>() {
//            @Override
//            public int compare(XYChart.Data<String, Number> inData1, XYChart.Data<String, Number> inData2) {
//                return Integer.compare(Integer.parseInt(inData1.getXValue().substring(0, inData1.getXValue().indexOf(' '))), 
//                                       Integer.parseInt(inData2.getXValue().substring(0, inData2.getXValue().indexOf(' '))));
//            }
//        });
//        // Add the results to the final series
//        lstFinalChartDataSeries.add(new BarChart.Series<String, Number>("Observations (" + inSightings.size() + ")", lstSeriesData));
//        // Setup the axis and chart
//        NumberAxis numAxis = new NumberAxis();
//        numAxis.setLabel("Number of Creatures (with the specified Observations count)");
//        UtilsReports.setupNumberAxis(numAxis, true);
//        CategoryAxis catAxis = new CategoryAxis();
//        catAxis.setLabel("Number of Observations (per Creature)");
//        UtilsReports.setupCategoryAxis(catAxis, mapGroupedCounts.size(), false);
//        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, lstFinalChartDataSeries);
//        chart.getStyleClass().add("wl-bar-single-color");
//        chart.setLegendVisible(false);
//        chart.setTitle("Number of Creatures grouped by the specified number of Observations");
//        UtilsReports.setupChartTooltips(chart, true, false);
//        return chart;
//    }
    
    private Chart createFirstSightingBarChart(List<Sighting> inSightings) {
        // Remember that the night streches over two calendar days
        // Get the starting dates
        Map<Long, LocalDate> mapVisitDates = new HashMap<>();
        LocalDate startDate = null; 
        for (Sighting sighting : inSightings) {
            LocalDate possibleStartDate = mapVisitDates.get(sighting.getVisitID());
            if (possibleStartDate == null && !mapVisitDates.containsKey(sighting.getVisitID())) {
                possibleStartDate = UtilsTime.getLocalDateFromDate(
                        WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class).getStartDate());
                mapVisitDates.put(sighting.getVisitID(), possibleStartDate);
            }
            // If no date was found then use the Sighting's date
            if (possibleStartDate == null) {
                possibleStartDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            }
            // Kies die vroegste datum
            if (startDate == null || possibleStartDate.isBefore(startDate)) {
                startDate = possibleStartDate;
            }
        }
        // Get the data structured
        Map<String, ReportCountWrapper> mapElemDate = new HashMap<>();
        for (Sighting sighting : inSightings) {
            LocalDateTime currentDate = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
            LocalDate adjustedDate = currentDate.toLocalDate();
            if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
                adjustedDate = currentDate.toLocalDate();
                if (currentDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && currentDate.toLocalTime().isBefore(LocalTime.NOON)) {
                    adjustedDate = currentDate.toLocalDate().minusDays(1);
                }
            }
            // Set the min/max days
            ReportCountWrapper wrapper = mapElemDate.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (wrapper == null) {
                wrapper = new ReportCountWrapper();
                wrapper.min = Double.MAX_VALUE;
                wrapper.max = 0;
                mapElemDate.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), wrapper);
            }
            LocalDate visitDate = mapVisitDates.get(sighting.getVisitID());
            if (visitDate == null) {
                visitDate = startDate;
            }
            long days = ChronoUnit.DAYS.between(visitDate, adjustedDate);
            if (wrapper.min > days) {
                wrapper.min = days;
                if (cmbOption.getSelectionModel().isSelected(0)) {
                    wrapper.value = sighting.getCachedVisitName() + " [Start = " + visitDate + "] [Observed = " + adjustedDate + "]";
                }
            }
            if (wrapper.max < days) {
                wrapper.max = days;
                if (cmbOption.getSelectionModel().isSelected(2)) {
                    wrapper.value = sighting.getCachedVisitName() + " [Start = " + visitDate + "] [Observed = " + adjustedDate + "]";
                }
            }
        }
        // Create the series
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemDate.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            ReportCountWrapper wrapper = mapElemDate.get(key);
            if (cmbOption.getSelectionModel().isSelected(0)) { // Min
                double minValue = wrapper.min;
                if (minValue == Double.MAX_VALUE) {
                    minValue = 0;
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>(minValue, key, wrapper.value);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(1)) { // Ave
                double minValue = wrapper.min;
                if (minValue == Double.MAX_VALUE) {
                    minValue = 0;
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>((minValue + wrapper.max)/2, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
            else
            if (cmbOption.getSelectionModel().isSelected(2)) { // Max
                BarChart.Data<Number, String> data = new BarChart.Data<>(wrapper.max, key, wrapper.value);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(keys.size(), data));
                lstCountForElements.add(data);
            }
        }
        // Sort the results
        Collections.sort(lstCountForElements, new Comparator<XYChart.Data<Number, String>>() {
            @Override
            public int compare(XYChart.Data<Number, String> inData1, XYChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getYValue().compareTo(inData2.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis();
        String indicator = "";
        if (cmbOption.getSelectionModel().isSelected(0)) {
            indicator = "Minimum ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(1)) {
            indicator = "Average ";
        }
        else
        if (cmbOption.getSelectionModel().isSelected(2)) {
            indicator = "Maximum ";
        }
        UtilsReports.setupNumberAxis(numAxis, true);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, 0, false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, lstFinalChartDataSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle(indicator + "days before first Observation per Period");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }

}
