package wildlog.ui.charts.implementations;

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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.charts.implementations.helpers.AbstractChart;
import wildlog.ui.charts.implementations.helpers.HorizontalBarChartChangeListener;
import wildlog.ui.charts.utils.UtilsCharts;


public class FileChart extends AbstractChart<Sighting> {
    private enum ChartType {BAR_CHART};
    private ChartType chartType;
    private Spinner<Integer> spnGap;
    
    
    public FileChart(List<Sighting> inLstData, JLabel inChartDescLabel, ChartsBaseDialog inReportsBaseDialog) {
        super("Time Gap Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Bar charts
        ToggleButton btnBarChartSightings = new ToggleButton("Observations by Time Gap");
        btnBarChartSightings.setToggleGroup(BUTTON_GROUP);
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature based on the gap between the time of Files. "
                        + "Files' times are only compared if they are linked to Observations that share the same Place.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        // Gap size
        lstCustomButtons.add(new Label("Inactivity gap (seconds)"));
        spnGap = new Spinner<>(0, Integer.MAX_VALUE, 120);
        spnGap.setCursor(Cursor.HAND);
        spnGap.setEditable(true);
        lstCustomButtons.add(spnGap);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.BAR_CHART)) {
            setActiveSubCategoryTitle("Gap_" + spnGap.getValue());
            displayedChart = createBarChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createBarChart(List<Sighting> inSightings) {
        // Get raw data
        Map<String, List<FileData>> mapElementFileData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            List<FileData> lstTimes = mapElementFileData.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (lstTimes == null) {
                lstTimes = new ArrayList<>();
                mapElementFileData.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), lstTimes);
            }
            List<WildLogFile> lstWildLogFiles = WildLogApp.getApplication().getDBI().listWildLogFiles(sighting.getID(), null, WildLogFile.class);
            for (WildLogFile wildLogFile : lstWildLogFiles) {
                lstTimes.add(new FileData(sighting.getLocationID(), wildLogFile.getFileDate().getTime()));
            }
        }
        // Get chart data
        int gapInMilliseconds = spnGap.getValue() * 1000;
        Map<String, Integer> mapData = new HashMap<>(mapElementFileData.size());
        for (Map.Entry<String, List<FileData>> entry : mapElementFileData.entrySet()) {
            int sightingCount = 0;
            Collections.sort(entry.getValue(), new Comparator<FileData>() {
                @Override
                public int compare(FileData inFileData1, FileData inFileData2) {
                    int result = Long.compare(inFileData1.locationId, inFileData2.locationId);
                    if (result == 0) {
                        result = Long.compare(inFileData1.time, inFileData2.time);
                    }
                    return result;
                }
            });
            long prevLocation = 0;
            long prevTime = 0;
            for (FileData fileData : entry.getValue()) {
                // Count the first (or maybe only) sighting at the location
                if (prevLocation != fileData.locationId) {
                    prevLocation = fileData.locationId;
                    sightingCount++;
                }
                else
                // Count the subsequent sightings
                if (fileData.time - prevTime >= gapInMilliseconds) {
                    sightingCount++;
                }
                prevTime = fileData.time;
            }
            mapData.put(entry.getKey(), sightingCount);
        }
        // Sort the keys
        ObservableList<BarChart.Data<Number, String>> lstChartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        // Add the click listener
        for (String key : keys) {
            BarChart.Data<Number, String> data = new BarChart.Data<>(mapData.get(key), key);
            data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(mapData.size(), data));
            lstChartData.add(data);
        }
        // Sort the results
        Collections.sort(lstChartData, new Comparator<BarChart.Data<Number, String>>() {
            @Override
            public int compare(BarChart.Data<Number, String> inData1, BarChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData2.getYValue().compareTo(inData1.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        ObservableList<BarChart.Series<Number, String>> lstChartSeries = FXCollections.observableArrayList();
        lstChartSeries.add(new BarChart.Series<Number, String>("Creatures (" + mapData.keySet().size() + ")", lstChartData));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapData.size(), false);
        BarChart<Number, String> chart = new BarChart<>(numAxis, catAxis, lstChartSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations per Creature.");
        UtilsCharts.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private class FileData {
        long locationId;
        long time;

        public FileData(long inLocationId, long inTime) {
            locationId = inLocationId;
            time = inTime;
        }
        
    }
    
}
