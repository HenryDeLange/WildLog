package wildlog.sync.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogDataType;
import wildlog.sync.azure.dataobjects.SyncTableEntry;

public final class UtilsSync {
    
    private UtilsSync() {
    }
    
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
    
    public static boolean uploadFile() {
        return true;
    }
    
    public static boolean uploadData(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, int inDBVersion, DataObjectWithAudit inData) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            SyncTableEntry syncTableEntry = new SyncTableEntry(inType, inWorkspaceID, inData.getID(), inData.getAuditTime(), System.currentTimeMillis(), inDBVersion, inData);
            cloudTable.execute(TableOperation.insertOrMerge(syncTableEntry));
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return true;
    }
    
    public static boolean uploadDeleteFile() {
        return true;
    }
    
    public static boolean uploadDeleteData() {
        return true;
    }
    
    public static boolean downloadFile() {
        return true;
    }
    
    public static SyncTableEntry downloadData(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, long inRecordID, int inDBVersion) {
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
    
}
