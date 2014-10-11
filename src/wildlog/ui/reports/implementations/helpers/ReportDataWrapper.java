package wildlog.ui.reports.implementations.helpers;


public class ReportDataWrapper {
    // Making variables public to make it a little easier (less code) to use this data wrapper
    public String key;
    public String value;
    public int count;

    public ReportDataWrapper() {
    }

    public ReportDataWrapper(String inKey, String inValue, int inCount) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String inValue) {
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
