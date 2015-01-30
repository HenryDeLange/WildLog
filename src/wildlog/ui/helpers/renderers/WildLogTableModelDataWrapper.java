package wildlog.ui.helpers.renderers;


public class WildLogTableModelDataWrapper implements Comparable<WildLogTableModelDataWrapper>{
    private String textValue;
    private Object dataValue;

    public WildLogTableModelDataWrapper() {
    }

    public WildLogTableModelDataWrapper(String inTextValue, Object inDataValue) {
        textValue = inTextValue;
        dataValue = inDataValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String inTextValue) {
        textValue = inTextValue;
    }

    public Object getDataValue() {
        return dataValue;
    }

    public void setDataValue(Object inDataValue) {
        dataValue = inDataValue;
    }

    @Override
    public int compareTo(WildLogTableModelDataWrapper inDataWrapper) {
        if (dataValue instanceof Comparable) {
            return ((Comparable) dataValue).compareTo(inDataWrapper.getDataValue());
        }
        return 0;
    }

}
