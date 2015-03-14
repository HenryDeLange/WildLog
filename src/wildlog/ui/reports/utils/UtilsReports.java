package wildlog.ui.reports.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.implementations.helpers.IntegerTickLabelFormatter;


public final class UtilsReports {
    public final static Map<String, String> COLOURS_DAY_NIGHT_TWILIGHT = new HashMap<>(6);
    static {
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.DAY.toString(), "#E7473F");
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.NIGHT.toString(), "#0F0427");
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.DAWN_OR_DUST.toString(), "#5A0F37");
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.ALWAYS.toString(), "#052114");
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.UNKNOWN.toString(), "#052114");
        COLOURS_DAY_NIGHT_TWILIGHT.put(ActiveTime.NONE.toString(), "#052114");
    }
    public final static Map<String, String> COLOURS_TIME_OF_DAY = new HashMap<>(15);
    static {
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_TWILIGHT.getText(),   "#8B2E5F");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_SUNRISE.getText(),    "#E7B153");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_EARLY.getText(),      "#E7A263");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.MORNING_MID.getText(),        "#FFA284");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.DAY_MID.getText(),            "#E7473F");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_MID.getText(),      "#E78263");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_LATE.getText(),     "#AA6F39");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_SUNSET.getText(),   "#AA8039");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.AFTERNOON_TWILIGHT.getText(), "#5A0F37");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_EARLY.getText(),        "#26144E");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_MID.getText(),          "#0F0427");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NIGHT_LATE.getText(),         "#452F74");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.UNKNOWN.getText(),            "#052114");
        COLOURS_TIME_OF_DAY.put(ActiveTimeSpesific.NONE.getText(),               "#052114");
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
    
    public static void displayLabelForDataOnTop(XYChart.Data<String, Number> inData) {
        final Node node = inData.getNode();
        final Text dataText = new Text(inData.getYValue() + "");
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
    
    public static void displayLabelForDataToRight(XYChart.Data<Number, String> inData) {
// FIXME: werk nie lekker nie...
        final Node node = inData.getNode();
        final Text dataText = new Text(inData.getXValue() + "");
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
    
    public static void setupNumberAxis(NumberAxis inNumAxism, boolean inSetSizeOnly) {
        inNumAxism.setTickLabelFont(Font.font(14));
        if (!inSetSizeOnly) {
            inNumAxism.setAutoRanging(true);
            inNumAxism.setTickLabelFormatter(new IntegerTickLabelFormatter());
        }
    }
    
    public static void setupCategoryAxis(CategoryAxis inCategoryAxis, int inNumberOfCategories, boolean inRotateText) {
        // Setup the label size based onthe number of entries
        if (inNumberOfCategories < 10) {
            inCategoryAxis.setTickLabelFont(Font.font(14));
        }
        else
        if (inNumberOfCategories < 20) {
            inCategoryAxis.setTickLabelFont(Font.font(12));
        }
        else {
            inCategoryAxis.setTickLabelFont(Font.font(10));
        }
        // Rotate the text if to many are being shown
        if (inRotateText && inNumberOfCategories > 30) {
            inCategoryAxis.setTickLabelRotation(-90);
        }
    }
    
//    public static PieChart createPieChartWithStyleIndexReset(ObservableList<PieChart.Data> inLstData) {
        // FIXME: FOKKEN BELAGLIKKE HACK: Ek moet reflection gebruik om te kry dat elke nuwe donnerse chart se stylesheet index weer by 0 begin, 
        // andersins begin dit die default kleure gebruik nadat ek 'n paar keer 'n pie chart laai...
//        PieChart chart = new PieChart();
//        try {
//            Class<PieChart> cls = PieChart.class;
//            Field field = cls.getDeclaredField("uniqueId");
//            field.setAccessible(true);
//            field.setInt(chart, 0);
//        } 
//        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        chart.setData(inLstData);
//        return chart;
//    }

    public static StackedBarChart createStackedBarChartWithStyleIndexBiggerThanEight(CategoryAxis inCategoryAxis, NumberAxis inNumberAxis,
            ObservableList<StackedBarChart.Series<String, Number>> inLstData) {
        // FIXME: FOKKEN BELAGLIKKE HACK: Die legend se style kleure word gehardcode na mod 8, nou moet ek met reflection dit fix...
        StackedBarChart chart = new StackedBarChart(inCategoryAxis, inNumberAxis, inLstData) {
            private int fixer = 0;
            @Override
            protected void seriesAdded(XYChart.Series series, int seriesIndex) {
                super.seriesAdded(series, seriesIndex);
                // Overwrite the default style that was added
                String fixedDefaultColorStyleClass = "data" + (fixer % 30);
                fixer++;
                try {
                    Class<StackedBarChart> cls = StackedBarChart.class;
                    Field field = cls.getDeclaredField("seriesDefaultColorMap");
                    field.setAccessible(true);
                    Map<XYChart.Series, String> seriesDefaultColorMap = (Map<XYChart.Series, String>) field.get(this);
                    seriesDefaultColorMap.put(series, fixedDefaultColorStyleClass);
                } 
                catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        };
        return chart;
    }
    
}
