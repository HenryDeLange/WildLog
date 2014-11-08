package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;


public class PieChartChangeListener<T extends Node> implements ChangeListener<Node> {
    private final String categoryString;
    private final int categoryInt;
    private final Map<String, String> mapChartColours;
    private final List<String> lstChartColours;

    public PieChartChangeListener(String inCategory, Map<String, String> inColours) {
        categoryString = inCategory;
        categoryInt = -1;
        mapChartColours = inColours;
        lstChartColours = null;
    }
    
    public PieChartChangeListener(int inCategory, List<String> inColours) {
        categoryString = null;
        categoryInt = inCategory;
        mapChartColours = null;
        lstChartColours = inColours;
    }
    
    @Override
    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
        if (newNode != null) {
            if (mapChartColours != null) {
                newNode.setStyle("-fx-pie-color: " + mapChartColours.get(categoryString));
            }
            else
            if (lstChartColours != null) {
                newNode.setStyle("-fx-pie-color: " + lstChartColours.get(categoryInt % lstChartColours.size()));
            }
        }
    }
}
