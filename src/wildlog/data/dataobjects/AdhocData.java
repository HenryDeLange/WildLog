package wildlog.data.dataobjects;


/**
 * This class is used to store key-value pair data as needed. 
 * This is mostly used to store data used internally by the desktop application, 
 * which isn't essential to the core WildLog functionality.
 */
public class AdhocData {
    public static enum ADHOC_FIELD_IDS {FILTER_TEMPLATES, MAP_SHAPEFILES};
    private String fieldID;
    private String dataKey;
    private String dataValue;

    
    public AdhocData() {
    }

    public AdhocData(String fieldID, String dataKey, String dataValue) {
        this.fieldID = fieldID;
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    
    public String getFieldID() {
        return fieldID;
    }

    public void setFieldID(String inFieldID) {
        fieldID = inFieldID;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String inDataKey) {
        dataKey = inDataKey;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String inDataValue) {
        dataValue = inDataValue;
    }

}
