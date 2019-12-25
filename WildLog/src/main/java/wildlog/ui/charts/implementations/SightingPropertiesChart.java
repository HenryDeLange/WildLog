package wildlog.ui.charts.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.charts.implementations.helpers.AbstractChart;
import wildlog.ui.charts.implementations.helpers.ComboBoxToShowCharts;
import wildlog.ui.charts.implementations.helpers.ChartDataWrapper;
import wildlog.ui.charts.utils.UtilsCharts;


public class SightingPropertiesChart extends AbstractChart<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType;
    private final ComboBoxToShowCharts<String> cmbCategories;
    private final String[] options = new String[] {"Number of Individuals", "Gender", "Age", "Life Status", 
        "Evidence", "Certainty", "Rating", "Info Tag", "Creature Type", "Has GPS"};
    private Scene scene = null;
    
    
    public SightingPropertiesChart(List<Sighting> inLstData, JLabel inChartDescLabel, ChartsBaseDialog inReportsBaseDialog) {
        super("Observation Properties Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(2);
        // Hidden ToggleButton to use (used to unselect the other toggle buttons)
        ToggleButton btnHidden = new ToggleButton();
        btnHidden.setToggleGroup(BUTTON_GROUP);
        // Pie charts
        lstCustomButtons.add(new Label("Observation Categories:"));
        cmbCategories = new ComboBoxToShowCharts<>();
        cmbCategories.setItems(FXCollections.observableArrayList(options));
        cmbCategories.setVisibleRowCount(10);
        cmbCategories.setCursor(Cursor.HAND);
        cmbCategories.getSelectionModel().clearSelection();
        cmbCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = null;
                btnHidden.setSelected(true);
            }
        });
        // Adding this second listener here to trigger the chart when an already selected item is clicked again.
        // This addresses the problem when navigating to a different chart and then coming back to this chart. 
        // The setOnAction() method does not trigger the already selected item.
        cmbCategories.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> inObservable, Boolean inWasShowing, Boolean inIsShowing) {
                if (!inIsShowing && !cmbCategories.getSelectionModel().isEmpty()) {
                    chartType = ChartType.PIE_CHART;
                    setupChartDescriptionLabel("<html>These charts will display a pie chart with the number of Observations for each of the entries of the selected category.</html>");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            createReport(scene);
                            // Add the watermark overlay
                            reportsBaseDialog.applyWatermark();
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(cmbCategories);
    }

    @Override
    public void createReport(Scene inScene) {
        scene = inScene;
        displayedChart = null;
        if (chartType != null) {
            if (chartType.equals(ChartType.PIE_CHART)) {
                setActiveSubCategoryTitle(cmbCategories.getSelectionModel().getSelectedItem());
                displayedChart = createPieChart(lstData);
            }
            displayedChart.setBackground(Background.EMPTY);
            inScene.setRoot(displayedChart);
        }
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        String title = "";
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[0])) {
            title = "Number of Observations with the specified Number of Individuals";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
            title = "Number of Observations with the specified Gender";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
            title = "Number of Observations with the specified Age";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[3])) {
            title = "Number of Observations with the specified Life Status";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[4])) {
            title = "Number of Observations with the specified Evidence";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[5])) {
            title = "Number of Observations with the specified Certainty";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[6])) {
            title = "Number of Observations with the specified Rating";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
            title = "Number of Observations with the specified Tag";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[8])) {
            title = "Number of Observations per Creature Type";
        }
        else
        if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[9])) {
            title = "Number of Observations with GPS Coordinates";
        }
        Map<String, ChartDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            String categoryValue = null;
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[0])) {
                categoryValue = "Observed " + Integer.toString(sighting.getNumberOfElements()) + " individuals";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getSex());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getAge());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[3])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getLifeStatus());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[4])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getSightingEvidence());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[5])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getCertainty());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[6])) {
                categoryValue = UtilsCharts.stringFromEnum(sighting.getViewRating());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                categoryValue = sighting.getTag();
                if (categoryValue == null) {
                    categoryValue = "";
                }
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[8])) {
                Element element = WildLogApp.getApplication().getDBI().findElement(sighting.getElementID(), null, false, Element.class);
                categoryValue = UtilsCharts.stringFromEnum(element.getType());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[9])) {
                if (UtilsGPS.hasGPSData(sighting)) {
                    categoryValue = "Has GPS";
                }
                else {
                    categoryValue = "No GPS";
                }
            }
            // Group records with unknown category values
            if (categoryValue == null || categoryValue.isEmpty()) {
                if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                    categoryValue = "None";
                }
                else {
                    categoryValue = "Unknown";
                }
            }
            ChartDataWrapper dataWrapper = mapGroupedData.get(categoryValue);
            if (dataWrapper == null) {
                mapGroupedData.put(categoryValue, new ChartDataWrapper("", "", 1));
            }
            else {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            PieChart.Data data = new PieChart.Data(key + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount());
            chartData.add(data);
        }
        PieChart chart = new PieChart(chartData);
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle(title);
        UtilsCharts.setupChartTooltips(chart);
        return chart;
    }
    
}
