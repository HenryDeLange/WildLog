package wildlog.sync.azure.dataobjects;

import wildlog.data.enums.WildLogDataType;


public class SyncBlobEntry {
    private WildLogDataType dataType;
    private long workspaceID;
    private long parentID;
    private long recordID;

    public SyncBlobEntry() {
    }

    public SyncBlobEntry(WildLogDataType inDataType, long inWorkspaceID, long inParentID, long inRecordID) {
        super();
        dataType = inDataType;
        workspaceID = inWorkspaceID;
        parentID = inParentID;
        recordID = inRecordID;
    }
    
    public WildLogDataType getDataType() {
        return dataType;
    }

    public void setDataType(WildLogDataType inDataType) {
        dataType = inDataType;
    }
    
    public long getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(long inWorkspaceID) {
        workspaceID = inWorkspaceID;
    }

    public long getParentID() {
        return parentID;
    }

    public void setParentID(long inParentID) {
        parentID = inParentID;
    }

    public long getRecordID() {
        return recordID;
    }

    public void setRecordID(long inRecordID) {
        recordID = inRecordID;
    }
    
    @Override
    public String toString() {
        return "[" + dataType + "] " + workspaceID + "/" + parentID + "/" + recordID;
    }

}
