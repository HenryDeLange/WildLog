package wildlog.sync.azure.dataobjects;

import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;


public class SyncBlobEntry {
    private WildLogDataType type;
    private long id;
    private WildLogFileType fileType;
    private long auditTime;
    private long syncTime;

    public SyncBlobEntry() {
    }

    public SyncBlobEntry(WildLogDataType inType, long inID, long inAuditTime, long inSyncTime) {
        type = inType;
        id = inID;
        auditTime = inAuditTime;
        syncTime = inSyncTime;
    }
    
    public WildLogDataType getType() {
        return type;
    }

    public void setType(WildLogDataType inType) {
        type = inType;
    }

    public long getID() {
        return id;
    }

    public void setID(long inID) {
        id = inID;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long inAuditTime) {
        auditTime = inAuditTime;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long inSyncTime) {
        syncTime = inSyncTime;
    }

}
