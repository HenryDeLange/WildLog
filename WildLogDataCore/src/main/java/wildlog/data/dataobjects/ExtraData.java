package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;


/**
 * This class is used to store key-value pair data that is linked to an Element, Location, etc. and is audited (can be synced). 
 * This is mostly used to allow the user to add additional attributes to a data record.
 * It can also be used to link internal system attributes, such as saving in progress Bulk Imports.
 */
public class ExtraData extends DataObjectWithAudit {
    public static enum EXTRA_FIELD_IDS {WILDLOG, USER};
    public static enum EXTRA_KEY_IDS {WL_BULK_IMPORT_TABLE_MODEL, WL_BULK_IMPORT_VISIT_FILES};
    private WildLogExtraDataFieldTypes fieldType;
    private long linkID; // The ID of the linked data object (Element, Location, etc.)
    private WildLogDataType linkType;
    private String dataKey;
    private String dataValue;

    
    public ExtraData() {
    }

    public ExtraData(WildLogExtraDataFieldTypes inFieldType, long inLinkID, WildLogDataType inLinkType, String inDataKey, String inDataValue) {
        fieldType = inFieldType;
        linkID = inLinkID;
        linkType = inLinkType;
        dataKey = inDataKey;
        dataValue = inDataValue;
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
    
    public WildLogExtraDataFieldTypes getFieldType() {
        return fieldType;
    }

    public void setFieldType(WildLogExtraDataFieldTypes inFieldType) {
        fieldType = inFieldType;
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

    @Override
    public String toString() {
        return "[ID: " + id + "; fieldType: " + fieldType + "; linkID: " + linkID + "; linkType: " + linkType + "; dataKey: " + dataKey + "; dataValue: " + dataValue + "]";
    }

}
