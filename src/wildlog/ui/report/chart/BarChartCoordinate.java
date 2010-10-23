package wildlog.ui.report.chart;

/**
 *
 * @author DeLangeH
 */
public final class BarChartCoordinate {
    // Variables
    private int x;
    private int y;

    // Constructor
    public BarChartCoordinate(int inX, int inY) {
        x = inX;
        y = inY;
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
