package wildlog.ui.reports.implementations.helpers;

import java.util.Map;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;


public class CustomColourBarChartChangeListener<T extends Node> extends BarChartChangeListener<T> {
    private Map<String, String> mapColors;
    private String key;
    
    public CustomColourBarChartChangeListener(int inTotalNumberOfBars, XYChart.Data<String, Number> inData, Map<String, String> inColors, String inKey) {
        super(inTotalNumberOfBars, inData);
        mapColors = inColors;
        key = inKey;
    }
    
    @Override
    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
        super.changed(ov, oldNode, newNode);
        if (newNode != null) {
            // Add the colors (this is done like this because I want to change the colour of each bar in a single series
            newNode.setStyle("-fx-bar-fill: " + mapColors.getOrDefault(key, "#648A54"));
        }
    }
    
}
