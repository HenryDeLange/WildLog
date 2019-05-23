package wildlog.sync.azure.dataobjects;

import com.microsoft.azure.storage.table.Ignore;
import com.microsoft.azure.storage.table.TableServiceEntity;
import wildlog.data.enums.WildLogDataType;


public class SyncDeleteRecord extends TableServiceEntity {
    private String type;
    private long auditTime;
    private long syncTime;

    public SyncDeleteRecord() {
        super();
    }

    public SyncDeleteRecord(WildLogDataType inType, long inWorkspaceID, long inRecordID, long inAuditTime, long inSyncTime) {
        super();
        if (inType != null) {
            type = inType.toString();
        }
        auditTime = inAuditTime;
        syncTime = inSyncTime;
        // Set the Azure table key fields
        partitionKey = Long.toString(inWorkspaceID);
        rowKey = Long.toString(inRecordID);
    }

    public String getType() {
        return type;
    }
    
    @Ignore
    public WildLogDataType getWildLogDataType() {
        return WildLogDataType.getEnumFromText(type);
    }

    public void setType(String inType) {
        type = inType;
    }
    
    @Ignore
    public void setWildLogDataType(WildLogDataType inType) {
        if (inType != null) {
            type = inType.toString();
        }
    }

    @Ignore
    public long getWorkspaceID() {
        return Long.parseLong(partitionKey);
    }

    @Ignore
    public void setWorkspaceID(long inWorkspaceID) {
        partitionKey = Long.toString(inWorkspaceID);
    }

    @Ignore
    public long getRecordID() {
        return Long.parseLong(rowKey);
    }

    @Ignore
    public void setRecordID(long inRecordID) {
        rowKey = Long.toString(inRecordID);
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
