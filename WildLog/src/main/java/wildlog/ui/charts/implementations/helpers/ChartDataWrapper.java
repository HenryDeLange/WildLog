package wildlog.ui.charts.implementations.helpers;


public class ChartDataWrapper {
    // Making variables public to make it a little easier (less code) to use this data wrapper
    public String key = "";
    public Object value = "";
    public int count;

    public ChartDataWrapper() {
    }

    public ChartDataWrapper(String inKey, Object inValue, int inCount) {
        key = inKey;
        value = inValue;
        count = inCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String inKey) {
        key = inKey;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object inValue) {
        value = inValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }
    
    public void increaseCount() {
        count++;
    }
}
