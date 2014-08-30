package wildlog.ui.reports.implementations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.implementations.helpers.AbstractReport;


public class TimeOfDayAllChart extends AbstractReport<Sighting> {

    // TODO: options -> series per species/location/visit + more generic times (aand, dag, skemer). 
    // TODO: Die report kan dalk beter werk as nog een van die opsies vir TimeOfDayPerCreatureChart
    
    public TimeOfDayAllChart() {
        super("Day Categories (All Together)");
    }

    @Override
    public Chart createChart() {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(ActiveTimeSpesific.getEnumListAsString()));
        axisX.setTickLabelRotation(-90);
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        for (Sighting sighting : lstData) {
            allSightings.add(new BarChart.Data<String, Number>(sighting.getTimeOfDay().toString(), 1));
        }
        chartData.add(new BarChart.Series<String, Number>("Observations (" + allSightings.size() + ")", allSightings));
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
}
