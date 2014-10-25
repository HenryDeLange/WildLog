package wildlog.ui.reports.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import wildlog.data.enums.ActiveTimeSpesific;


public final class UtilsReports {
    public final static String COLOURS_1 = "#799E35";
    public final static List<String> COLOURS_3 = new ArrayList<>(3);
    static {
        COLOURS_3.add("#ADEB65");
        COLOURS_3.add("#D59299");
        COLOURS_3.add("#8884CA");
    }
    public final static List<String> COLOURS_DAY_NIGHT_TWILIGHT = new ArrayList<>(3);
    static {
        COLOURS_DAY_NIGHT_TWILIGHT.add("#ADEB65");
        COLOURS_DAY_NIGHT_TWILIGHT.add("#D59299");
        COLOURS_DAY_NIGHT_TWILIGHT.add("#8884CA");
    }
    public final static Map<String, String> COLOURS_TIME_OF_DAY = new HashMap<>(15);
    static {
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_TWILIGHT.getText(),   "#8B2E5F"); // Morning Twilight
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_SUNRISE.getText(),    "#E7B153"); // Morning Sunrise
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_EARLY.getText(),      "#E7A263"); // Early Morning
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_MID.getText(),        "#FFA284"); // Mid Morning
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.DAY_MID.getText(),            "#E7473F"); // Mid Day
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_MID.getText(),      "#E78263"); // Mid Afternoon
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_LATE.getText(),     "#AA6F39"); // Late Afternoon
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_SUNSET.getText(),   "#AA8039"); // Afternoon Sunset
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_TWILIGHT.getText(), "#5A0F37"); // Afternoon Twilight
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_EARLY.getText(),        "#26144E"); // Early Night
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_MID.getText(),          "#0F0427"); // Mid Night
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_LATE.getText(),         "#452F74"); // Late Night
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.UNKNOWN.getText(),            "#052114"); // Unknown
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NONE.getText(),               "#052114"); // None
    }
    public final static List<String> COLOURS_30 = new ArrayList<>(30);
    static {
        COLOURS_30.add("#ADEB65");
        COLOURS_30.add("#D59299");
        COLOURS_30.add("#8884CA");
        COLOURS_30.add("#8EC74C");
        COLOURS_30.add("#BD6069");
        COLOURS_30.add("#59559D");
        COLOURS_30.add("#6B9A33");
        COLOURS_30.add("#A43741");
        COLOURS_30.add("#363377");
        COLOURS_30.add("#496D1F");
        COLOURS_30.add("#1C1950");
        COLOURS_30.add("#29400F");
        COLOURS_30.add("#73000B");
        COLOURS_30.add("#0A0829");
        COLOURS_30.add("#993366");
        COLOURS_30.add("#F3FC97");
        COLOURS_30.add("#FFC66E");
        COLOURS_30.add("#618D84");
        COLOURS_30.add("#D4DE67");
        COLOURS_30.add("#DCA654");
        COLOURS_30.add("#407D71");
        COLOURS_30.add("#9EA738");
        COLOURS_30.add("#AA7D39");
        COLOURS_30.add("#246D5E");
        COLOURS_30.add("#697017");
        COLOURS_30.add("#785622");
        COLOURS_30.add("#0F5C4D");
        COLOURS_30.add("#353A04");
        COLOURS_30.add("#463111");
        COLOURS_30.add("#004C3D");
    }

    
    private UtilsReports() {
    }
    
    /**
     * Remove the empty entries from the Enums, to not display then in the Report Filter.
     * @param inList
     * @return 
     */
    public static Enum[] removeEmptyEntries(Enum[] inList) {
        Enum[] array = new Enum[inList.length];
        int counter = 0;
        for (Enum temp : inList) {
            if (!temp.toString().trim().isEmpty()) {
                array[counter++] = temp;
            }
        }
        return Arrays.copyOf(array, counter);
    }
    
    public static void displayLabelForDataOnTop(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        dataText.setStyle("-fx-fill: #99AA88;");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });
        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
                dataText.setLayoutY(Math.round(bounds.getMinY() - dataText.prefHeight(-1) * 0.5));
            }
        });
    }
    
    public static void displayLabelForDataToRight(XYChart.Data<Number, String> data) {
// FIXME: werk nie lekker nie...
        final Node node = data.getNode();
        final Text dataText = new Text(data.getXValue() + "");
        dataText.setStyle("-fx-fill: #99AA88;");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });
        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(Math.round(bounds.getMaxX() + 5));
                dataText.setLayoutY(Math.round(bounds.getMinY() - bounds.getHeight() / 2 - dataText.prefHeight(-1) / 2 ));
            }
        });
    }
    
}
