package wildlog.ui.reports.implementations;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class SightingPropertiesChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType;
    private Chart displayedChart;
    private final ComboBox<String> cmbCategories;
    private final String[] options = new String[] {"Number of Individuals", "Sex", "Age", "Life Status", "Evidence", "Certainty", "Rating", "Info Tag", "Creature Type"};
    private Scene scene = null;
    
    
    public SightingPropertiesChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Observation Properties Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(2);
        // Pie charts
        lstCustomButtons.add(new Label("Pie Chart Categories:"));
        cmbCategories = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbCategories.setVisibleRowCount(10);
        cmbCategories.setCursor(Cursor.HAND);
        cmbCategories.getSelectionModel().clearSelection();
        cmbCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = null;
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
                    createReport(scene);
                }
            }
        });
        lstCustomButtons.add(cmbCategories);
    }

    @Override
    public void createReport(Scene inScene) {
        scene = inScene;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType != null) {
                    if (chartType.equals(ChartType.PIE_CHART)) {
                        displayedChart = createPieChart(lstData);
                    }
                    displayedChart.setBackground(Background.EMPTY);
                    inScene.setRoot(displayedChart);
                }
            }
        });
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        String title = "";
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            String categoryValue = null;
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[0])) {
                categoryValue = "Observed " + Integer.toString(sighting.getNumberOfElements()) + " individuals";
                title = "Number of Observations with the specified Number of Individuals";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
                categoryValue = UtilsData.stringFromObject(sighting.getSex());
                title = "Number of Observations with the specified Sex";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
                categoryValue = UtilsData.stringFromObject(sighting.getAge());
                title = "Number of Observations with the specified Age";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[3])) {
                categoryValue = UtilsData.stringFromObject(sighting.getLifeStatus());
                title = "Number of Observations with the specified Life Status";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[4])) {
                categoryValue = UtilsData.stringFromObject(sighting.getSightingEvidence());
                title = "Number of Observations with the specified Evidence";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[5])) {
                categoryValue = UtilsData.stringFromObject(sighting.getCertainty());
                title = "Number of Observations with the specified Certainty";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[6])) {
                categoryValue = UtilsData.stringFromObject(sighting.getViewRating());
                title = "Number of Observations with the specified Rating";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                categoryValue = UtilsData.stringFromObject(sighting.getTag());
                title = "Number of Observations with the specified Tag";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[8])) {
                Element element = WildLogApp.getApplication().getDBI().find(new Element(sighting.getElementName()));
                categoryValue = UtilsData.stringFromObject(element.getType());
                title = "Number of Observations per Creature Type";
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
            ReportDataWrapper dataWrapper = mapGroupedData.get(categoryValue);
            if (dataWrapper == null) {
                mapGroupedData.put(categoryValue, new ReportDataWrapper("", "", 1));
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
        return chart;
    }
    
}
