package wildlog.ui.charts.implementations.helpers;


public class ChartCountWrapper {
    // Making variables public to make it a little easier (less code) to use this data wrapper
    public double max;
    public double min;
    public double total;
    public double count;
    public Object value;

    public ChartCountWrapper() {
    }

    public ChartCountWrapper(int inMax, int inMin, int inTotal, int inCount) {
        max = inMax;
        min = inMin;
        total = inTotal;
        count = inCount;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double inMax) {
        max = inMax;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double inMin) {
        min = inMin;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double inTotal) {
        total = inTotal;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double inCount) {
        count = inCount;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object inValue) {
        value = inValue;
    }
    
}