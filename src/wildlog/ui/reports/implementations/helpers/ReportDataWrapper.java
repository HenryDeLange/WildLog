package wildlog.ui.reports.implementations.helpers;


public class ReportDataWrapper {
    // Making variables public to make it a little easier (less code) to use this data wrapper
    public String key;
    public String value;
    public int count;

    public ReportDataWrapper() {
    }

    public ReportDataWrapper(String key, String value, int count) {
        this.key = key;
        this.value = value;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public void increaseCount() {
        count++;
    }
}
