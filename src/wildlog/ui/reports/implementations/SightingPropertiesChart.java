package wildlog.ui.reports.implementations;

import java.lang.reflect.Field;
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
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class SightingPropertiesChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType;
    private Chart displayedChart;
    private final ComboBox<String> cmbCategories;
    private final String[] options = new String[] {"Number Observed", "Sex", "Age", "Life Status", "Evidence", "Certainty", "Rating", "Info Tag"};
    private Scene scene = null;
    
    
    public SightingPropertiesChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Observation Properties Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(2);
        // Pie charts
        lstCustomButtons.add(new Label("Pie Chart Categories:"));
        cmbCategories = new ComboBox<>(FXCollections.observableArrayList(options));
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
                    setupChartDescriptionLabel("<html>This collection of charts focuses on the ratio of Observations for the selected category.</html>");
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
            String category = null;
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[0])) {
                category = Integer.toString(sighting.getNumberOfElements()) + " Observations";
                title = "Number of Observations with the specified Number of Individuals";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
                category = UtilsData.stringFromObject(sighting.getSex());
                title = "Number of Observations with the specified Sex";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
                category = UtilsData.stringFromObject(sighting.getAge());
                title = "Number of Observations with the specified Age";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[3])) {
                category = UtilsData.stringFromObject(sighting.getLifeStatus());
                title = "Number of Observations with the specified Life Status";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[4])) {
                category = UtilsData.stringFromObject(sighting.getSightingEvidence());
                title = "Number of Observations with the specified Evidence";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[5])) {
                category = UtilsData.stringFromObject(sighting.getCertainty());
                title = "Number of Observations with the specified Certainty";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[6])) {
                category = UtilsData.stringFromObject(sighting.getViewRating());
                title = "Number of Observations with the specified Rating";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                category = UtilsData.stringFromObject(sighting.getTag());
                title = "Number of Observations with the specified Tag";
            }
            if (category == null || category.isEmpty()) {
                if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                    category = "None";
                }
                else {
                    category = "Unknown";
                }
            }
            ReportDataWrapper dataWrapper = mapGroupedData.get(category);
            if (dataWrapper == null) {
                mapGroupedData.put(category, new ReportDataWrapper("", "", 1));
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
//            data.nodeProperty().addListener(new PieChartChangeListener<>(chartData.size(), UtilsReports.COLOURS_30));
            chartData.add(data);
        }
        PieChart chart = new PieChart();
        // Set the stylesheet to use
        chart.getStylesheets().add("/wildlog/ui/reports/chart/styling/Charts.css");
        // FOKKEN BELAGLIKKE HACK: Ek moet reflection gebruik om te kry dat elke nuwe donnerse chart se stylesheet index weer by 0 begin, 
        // andersins begin dit die default kleure gebruik nadat ek 'n paar keer 'n ander chart select het...
// TODO: Die CSS stel die spesifieke data kleure, maar moet ek nie dalk eerder die default kleure stel nie, 
//      en dan sal die code hier onder dalk nie meer nodig wees nie??
        try {
            Class<PieChart> cls = PieChart.class;
            Field f = cls.getDeclaredField("uniqueId");
            f.setAccessible(true);
            f.setInt(chart, 0);
        } 
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        chart.setData(chartData);
//        chart.setLegendVisible(false);
        chart.setTitle(title);
        
// FIXME: Die online foorbeeld werk nie, die idee is om die text op die slice te wys as mens click
//        final Label caption = new Label("123");
//        caption.setTextFill(Color.DARKORANGE);
//        caption.setStyle("-fx-font: 24 arial;");
//        for (int t = 0; t < chartData.size(); t++) {
//            PieChart.Data data = chartData.get(t);
//            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        caption.setTranslateX(e.getSceneX());
//                        caption.setTranslateY(e.getSceneY());
//                        caption.setText(String.valueOf(data.getPieValue()) + "%");
//                     }
//                });
//        }
        return chart;
    }
    
}
