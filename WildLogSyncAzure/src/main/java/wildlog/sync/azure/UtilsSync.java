package wildlog.sync.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogDataType;
import wildlog.sync.azure.dataobjects.SyncTableEntry;

public final class UtilsSync {
    
    private UtilsSync() {
    }
    
    // TABLES:
    
    private static CloudTableClient getTableClient(String inStorageConnectionString) 
            throws InvalidKeyException, URISyntaxException {
        CloudStorageAccount storageAccount;
        try {
            storageAccount = CloudStorageAccount.parse(inStorageConnectionString);
        }
        catch (URISyntaxException e) {
            System.err.println("Connection string specifies an invalid URI.");
            System.err.println("Please confirm the connection string is in the Azure connection string format.");
            throw e;
        }
        catch (InvalidKeyException e) {
            System.err.println("Connection string specifies an invalid key.");
            System.err.println("Please confirm the AccountName and AccountKey in the connection string are valid.");
            throw e;
        }
        return storageAccount.createCloudTableClient();
    }
    
    private static CloudTable getTable(CloudTableClient inTableClient, String inTableName)
            throws StorageException, IOException, InvalidKeyException, IllegalArgumentException, URISyntaxException, IllegalStateException {
        CloudTable table = inTableClient.getTableReference(inTableName);
        if (table.createIfNotExists()) {
            System.out.println("Table [" + inTableName + "] was created.");
        }
        return table;
    }
    
    // DATA:
    
    public static boolean uploadData(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, int inDBVersion, DataObjectWithAudit inData) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            SyncTableEntry syncTableEntry = new SyncTableEntry(inType, inWorkspaceID, inData.getID(), System.currentTimeMillis(), inDBVersion, inData);
            cloudTable.execute(TableOperation.insertOrReplace(syncTableEntry));
            return true;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public static boolean uploadDataBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, int inDBVersion, List<DataObjectWithAudit> inLstData) {
        
        return false;
    }
    
    public static boolean deleteData(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, long inRecordID) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            // Dis jammer, maar dit lyk my ek moet eers die waarde laai voor ek kan delete.
            // (Dit soek die "etag" waarde, so ek kan nie self 'n nuwe SyncTableEntry skep nie...)
            cloudTable.execute(TableOperation.delete(downloadData(inStorageConnectionString, inType, inWorkspaceID, inRecordID)));
            return true;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public static boolean deleteDataBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, List<Long> inLstRecordIDs) {
        
        return false;
    }
    
    public static SyncTableEntry downloadData(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, long inRecordID) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            return cloudTable.execute(TableOperation.retrieve(
                    Long.toString(inWorkspaceID), Long.toString(inRecordID), SyncTableEntry.class)).getResultAsType();
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static List<SyncTableEntry> downloadDataBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, long inAfterSyncTime) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            TableQuery<SyncTableEntry> rangeQuery = TableQuery.from(SyncTableEntry.class);
            if (inAfterSyncTime > 0) {
                rangeQuery.where(
                        TableQuery.combineFilters(
                                TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)),
                                TableQuery.Operators.AND,
                                TableQuery.generateFilterCondition("syncTime", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, inAfterSyncTime)));
            }
            else {
                rangeQuery.where(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)));
            }
            List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
            for (SyncTableEntry syncTableEntry : cloudTable.execute(rangeQuery)) {
                lstSyncTableEntries.add(syncTableEntry);
            }
            return lstSyncTableEntries;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    // FILES:
    
    public static boolean uploadFile() {
        return true;
    }
    
    public static boolean downloadFile() {
        return true;
    }
    
}
