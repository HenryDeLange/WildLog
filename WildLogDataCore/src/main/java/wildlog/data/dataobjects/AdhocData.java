package wildlog.data.dataobjects;


/**
 * This class is used to store key-value pair data as needed. 
 * This is mostly used to store data used internally by the desktop application, 
 * which isn't essential to the core WildLog functionality.
 */
public class AdhocData {
    public static enum ADHOC_FIELD_IDS {FILTER_TEMPLATES, MAP_SHAPEFILES_DISTRIBUTION, MAP_SHAPEFILES_BASE, SYNC_TIMESTAMP, WORKSPACE_PRIMARY_COMPUTER};
    private String fieldID;
    private String dataKey;
    private String dataValue;

    
    public AdhocData() {
    }

    public AdhocData(String inFieldID, String inDataKey, String inDataValue) {
        fieldID = inFieldID;
        dataKey = inDataKey;
        dataValue = inDataValue;
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
