package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import wildlog.ui.reports.utils.UtilsReports;


public class BarChartChangeListener<T extends Node> implements ChangeListener<Node> {
    private final int barNumber;
    private final int totalNumberOfBars;
    private final XYChart.Data<String, Number> data;
    private final List<String> lstChartColours;

    public BarChartChangeListener(int inBarNumber, int inTotalNumberOfBars, XYChart.Data<String, Number> inData, List<String> inColours) {
        barNumber = inBarNumber;
        totalNumberOfBars = inTotalNumberOfBars;
        data = inData;
        lstChartColours = inColours;
    }
    
    @Override
    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
        if (newNode != null) {
            newNode.setStyle("-fx-bar-fill: " + lstChartColours.get(barNumber % lstChartColours.size()));
            // Add the total at the top
            if (totalNumberOfBars < 30) {
                UtilsReports.displayLabelForDataOnTop(data);
            }
        }
    }
}
