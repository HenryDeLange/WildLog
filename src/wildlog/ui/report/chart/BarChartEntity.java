package wildlog.ui.report.chart;

import java.awt.Color;

/**
 *
 * @author DeLangeH
 */
public class BarChartEntity implements Comparable<BarChartEntity> {
    // Variables
    private Color color;
    private int value;
    private String description;
    private Object barName;

    // Constructor
    public BarChartEntity(Object inBarName, String inDescription, int inValue, Color inColor) {
        color = inColor;
        value = inValue;
        description = inDescription;
        barName = inBarName;
    }

    @Override
    public int compareTo(BarChartEntity inBarChartEntity) {
        if (barName == null || inBarChartEntity.getBarName() == null)
            return 0;
        if (barName instanceof Comparable && inBarChartEntity.getBarName() instanceof Comparable) {
            return ((Comparable)barName).compareTo(((Comparable)inBarChartEntity.getBarName()));
        }
        else return 0;
    }

    // Getters and Setters
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Object getBarName() {
        return barName;
    }

    public void setBarName(Object barName) {
        this.barName = barName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
