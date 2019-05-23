package wildlog.sync.azure.dataobjects;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.Ignore;
import com.microsoft.azure.storage.table.TableServiceEntity;
import java.util.HashMap;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.WildLogDataType;


public class SyncTableEntry extends TableServiceEntity {
    private String type;
    private long auditTime;
    private long syncTime;
    private int dbVersion;
    private Object data;

    public SyncTableEntry() {
        super();
    }

    public SyncTableEntry(WildLogDataType inType, long inWorkspaceID, long inRecordID, long inAuditTime, long inSyncTime, int inDBVersion, DataObjectWithAudit inData) {
        super();
        if (inType != null) {
            type = inType.toString();
        }
        auditTime = inAuditTime;
        syncTime = inSyncTime;
        dbVersion = inDBVersion;
        data = inData;
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

    public int getDBVersion() {
        return dbVersion;
    }

    public void setDBVersion(int inDBVersion) {
        dbVersion = inDBVersion;
    }

    @Ignore
    public Object getData() {
        return data;
    }

    @Ignore
    public void setData(Object inData) {
        data = inData;
    }

    @Override
    public void readEntity(HashMap<String, EntityProperty> inProperties, OperationContext inOpContext)
            throws StorageException {
        // First read the fields for this class
        super.readEntity(inProperties, inOpContext);
        // Next read the fields for the data object
        if (type.equals(WildLogDataType.ELEMENT.getKey())) {
            if (dbVersion == 12) {
                data = readElementV12(inProperties);
            }
        }
        else
        if (type.equals(WildLogDataType.LOCATION.getKey())) {
            if (dbVersion == 12) {
                data = readLocationV12(inProperties);
            }
        }
        else
        if (type.equals(WildLogDataType.VISIT.getKey())) {
            data = new VisitCore();
        }
        else
        if (type.equals(WildLogDataType.SIGHTING.getKey())) {
            data = new SightingCore();
        }
        else
        if (type.equals(WildLogDataType.FILE.getKey())) {
            data = new WildLogFileCore();
        }
        else
        if (type.equals(WildLogDataType.WILDLOG_USER.getKey())) {
            data = new WildLogUser();
        }
        else {
            data = null;
        }
    }

    @Override
    public HashMap<String, EntityProperty> writeEntity(OperationContext opContext) 
            throws StorageException {
        // First write the fields from this class
        HashMap<String, EntityProperty> properties = super.writeEntity(opContext);
        // Next write the fields from the data object
        if (data != null) {
            writeLocationV12((LocationCore) data, properties);
        }
        return properties;
    }
    
    private ElementCore readElementV12(HashMap<String, EntityProperty> inProperties) {
        ElementCore tempData = new ElementCore();
        tempData.setID(inProperties.get("id").getValueAsLong());
        
        return tempData;
    }
    
    private LocationCore readLocationV12(HashMap<String, EntityProperty> inProperties) {
        LocationCore tempData = new LocationCore();
        tempData.setDescription(inProperties.get("description").getValueAsString());
        tempData.setHabitatType(inProperties.get("habitatType").getValueAsString());
        tempData.setGameViewingRating(GameViewRating.getEnumFromText(inProperties.get("gameViewingRating").getValueAsString()));
        
        return tempData;
    }
    
    private void writeLocationV12(LocationCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("habitatType", new EntityProperty(inData.getHabitatType()));
        inProperties.put("gameViewingRating", new EntityProperty(inData.getGameViewingRating().toString()));
        
    }

}
