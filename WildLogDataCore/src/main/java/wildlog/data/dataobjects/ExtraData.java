package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;


/**
 * This class is used to store key-value pair data that is linked to an Element, Location, etc. and is audited (can be synced). 
 * This is mostly used to allow the user to add additional attributes to a data record.
 * It can also be used to link internal system attributes, such as saving in progress Bulk Imports.
 */
public class ExtraData extends DataObjectWithAudit {
    public static enum EXTRA_FIELD_IDS {WILDLOG, USER};
    public static enum EXTRA_KEY_IDS {BULK_IMPORT};
    private long linkID; // The ID of the linked data object (Element, Location, etc.)
    private WildLogDataType linkType;
    private String fieldID;
    private String dataKey;
    private String dataValue;

    
    public ExtraData() {
    }

    public ExtraData(long inLinkID, WildLogDataType inLinkType, String inFieldID, String inDataKey, String inDataValue) {
        fieldID = inFieldID;
        dataKey = inDataKey;
        dataValue = inDataValue;
        linkID = inLinkID;
        linkType = inLinkType;
    }

    
    public long getLinkID() {
        return linkID;
    }

    public void setLinkID(long inLinkID) {
        linkID = inLinkID;
    }

    public WildLogDataType getLinkType() {
        return linkType;
    }

    public void setLinkType(WildLogDataType inLinkType) {
        linkType = inLinkType;
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
