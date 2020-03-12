package wildlog.ui.charts.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.utils.EnumWithID;
import wildlog.ui.charts.implementations.helpers.IntegerTickLabelFormatter;
import wildlog.utils.UtilsTime;


public final class UtilsCharts {
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
    
    
    private UtilsCharts() {
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
    
// FIXME: Die nommers kap soms af... Ek kan dit teoreties binne die bar sit, maar ek dink dit lyk beter buite dit... Die afkap pla nie erg nie...
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
    
// FIXME: Die nommers kap soms af... Ek kan dit teoreties binne die bar sit, maar ek dink dit lyk beter buite dit... Die afkap pla nie erg nie...
    public static void displayLabelForDataToRight(XYChart.Data<Number, String> inData) {
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
                dataText.setX(Math.round(bounds.getWidth() + dataText.prefWidth(-1) * 0.5));
                dataText.setY(Math.round(bounds.getMaxY() - bounds.getHeight() / 2 + dataText.prefHeight(-1) / 2));
            }
        });
    }
    
    public static void setupNumberAxis(NumberAxis inNumAxism, boolean inSetSizeOnly) {
        inNumAxism.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
        if (!inSetSizeOnly) {
            inNumAxism.setAutoRanging(true);
            inNumAxism.setTickLabelFormatter(new IntegerTickLabelFormatter());
        }
    }
    
    public static void setupCategoryAxis(CategoryAxis inCategoryAxis, int inNumberOfCategories, boolean inRotateText) {
        // Setup the label size based onthe number of entries
        if (inNumberOfCategories < 10) {
            inCategoryAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 15));
        }
        else
        if (inNumberOfCategories < 20) {
            inCategoryAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 13));
        }
        else
        if (inNumberOfCategories > 40) {
            inCategoryAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 9));
        }
        else {
            inCategoryAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 11));
        }
        // Rotate the text if to many are being shown
//        if (inRotateText && inNumberOfCategories > 30) {
//            inCategoryAxis.setTickLabelRotation(-90);
//        }
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

// TODO: Nie seker of hierdie hack nog werk in Java 13 nie?
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
                catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        };
        return chart;
    }
    
    public static void setupChartTooltips(XYChart inChart, boolean inValueAxisIsY, boolean inFormatLongAsDate) {
        setupChartTooltips(inChart, inValueAxisIsY, inFormatLongAsDate, null, false, true, false, -1);
    }
    
    public static void setupChartTooltips(XYChart inChart, boolean inValueAxisIsY, boolean inFormatLongAsDate, boolean inIsStackedBarchart) {
        setupChartTooltips(inChart, inValueAxisIsY, inFormatLongAsDate, null, inIsStackedBarchart, true, false, -1);
    }
    
    public static void setupChartTooltips(XYChart inChart, boolean inValueAxisIsY, boolean inFormatLongAsDate, boolean inIsStackedBarchart, 
            boolean inShowExtraData, boolean inShowSeriesName, int inDataIndex) {
        setupChartTooltips(inChart, inValueAxisIsY, inFormatLongAsDate, null, inIsStackedBarchart, inShowExtraData, inShowSeriesName, inDataIndex);
    }
    
    private static void setupChartTooltips(XYChart inChart, boolean inValueAxisIsY, boolean inFormatLongAsDate, 
            Map<Integer, String> inMapAxisToNames, boolean inIsStackedBarchart, boolean inShowExtraData, boolean inShowSeriesName, int inDataIndex) {
        // Get the node that will be at the top (the one being clicked) for each value
        Map<String, Node> mapTopNode = new HashMap<>();
        Map<String, String> mapTopTooltip = new HashMap<>();
        for (XYChart.Series<Object, Object> series : (List<XYChart.Series<Object, Object>>) inChart.getData()) {
            List<XYChart.Data<Object, Object>> lstData;
            if (inDataIndex < 0) {
                lstData = series.getData();
            }
            else {
                lstData = new ArrayList<>(1);
                lstData.add(series.getData().get(inDataIndex));
            }
            for (XYChart.Data<Object, Object> data : lstData) {
                String xyKey = data.getXValue().toString() + "_" + data.getYValue().toString();
                if (inIsStackedBarchart) {
                    xyKey = xyKey + "_" + data.getExtraValue();
                }
                // Set top node
                mapTopNode.put(xyKey, data.getNode());
                // Generate tooltip
                if (data.getNode() == mapTopNode.get(xyKey)) {
                    String text = "";
                    if (inShowSeriesName) {
                        text = text + series.getName() + System.lineSeparator();
                    }
                    if (inShowExtraData && data.getExtraValue() != null && !data.getExtraValue().toString().isEmpty()) {
                        // If the list of Element names get too long split it into multiple lines
                        text = text + data.getExtraValue().toString();
                        final int LINE_LIMIT = 100;
                        if (text.length() > LINE_LIMIT) {
                            StringBuilder builder = new StringBuilder(text.length() + 30);
                            int currentLineLength = 0;
                            for (String entry : text.split(",", -1)) {
                                if (currentLineLength > LINE_LIMIT) {
                                    builder.append(System.lineSeparator()).append("   ");
                                    currentLineLength = 0;
                                }
                                builder.append(entry).append(",");
                                currentLineLength = currentLineLength + entry.length() + 2;
                            }
                            text = builder.toString();
                            if (text.endsWith(",")) {
                                text = text.substring(0, text.length() - 2);
                            }
                        }
                        text = text + System.lineSeparator();
                    }
                    String name;
                    String value;
                    if (inValueAxisIsY) {
                        name = data.getXValue().toString();
                        value = data.getYValue().toString();
                    }
                    else {
                        name = data.getYValue().toString();
                        value = data.getXValue().toString();
                    }
                    if (inFormatLongAsDate) {
                        name = UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(UtilsTime.getLocalDateTimeFromDate(new Date(Long.parseLong(name))));
                    }
                    if (inMapAxisToNames == null) {
                        text = text + name + System.lineSeparator();
                        text = text + "[Value = " + value + "]";
                    }
                    else {
                        name = inMapAxisToNames.get(Integer.parseInt(name));
                        value = inMapAxisToNames.get(Integer.parseInt(value));
                        text = name + " and " + value + System.lineSeparator() + "[Value = " + text.trim() + "]";
                    }
                    String oldText = mapTopTooltip.get(xyKey);
                    if (oldText != null && !oldText.isEmpty() && !text.isEmpty()) {
                        text = oldText + System.lineSeparator() + "-------------" + System.lineSeparator() + text;
                    }
                    if (!text.isEmpty()) {
                        mapTopTooltip.put(xyKey, text);
                    }
                }
            }
            // For Line / Area Charts make the area fill transparent to clicks (otherwise the first points can't be clicked)
            if (series.getNode() != null) {
                series.getNode().setMouseTransparent(true);
            }
        }
        // Setup the tooltips on the top node
        for (Map.Entry<String, Node> entry : mapTopNode.entrySet()) {
            entry.getValue().setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent inEvent) {
                    Tooltip tooltip = new Tooltip(mapTopTooltip.get(entry.getKey()));
                    tooltip.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
                    tooltip.setAutoHide(true);
                    tooltip.show((Node) inEvent.getSource(), inEvent.getScreenX(), inEvent.getScreenY());
                }
            });
            entry.getValue().setCursor(Cursor.HAND);
        }
    }
    
    public static void setupChartTooltips(PieChart inChart) {
        for (PieChart.Data data : inChart.getData()) {
            data.getNode().setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent inEvent) {
                    String text = data.getName() + System.lineSeparator() + "[Value = " + data.getPieValue() + "]";
                    Tooltip tooltip = new Tooltip(text);
                    tooltip.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
                    tooltip.setAutoHide(true);
                    tooltip.show((Node) inEvent.getSource(), inEvent.getScreenX(), inEvent.getScreenY());
                }
            });
            data.getNode().setCursor(Cursor.HAND);
        }
    }
    
    public static String getNumberWithZero(int inNumber) {
        if (inNumber < 10) {
            return "0" + inNumber;
        }
        return Integer.toString(inNumber);
    }
    
    public static String stringFromEnum(EnumWithID inEnum) {
        if (inEnum == null) {
            return null;
        }
        else {
            return inEnum.toString();
        }
    }
    
}
