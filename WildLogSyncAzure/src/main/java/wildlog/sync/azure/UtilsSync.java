package wildlog.sync.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
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
    private static final int BATCH_LIMIT = 50;
    private static final int URL_LIMIT = 30000;
    
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
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            // Batches are limited to less than 100 operations and must have under 4 MB total payload.
            // If the batch fails, then try again with a smaller number of operations.
            List<List<DataObjectWithAudit>> lstAllBatchDataChunks = new ArrayList<>((inLstData.size() / 95) + 1);
            int dataCounter = 0;
            for (DataObjectWithAudit data : inLstData) {
                if (dataCounter % BATCH_LIMIT == 0) {
                    List<DataObjectWithAudit> lstSingleBatchChunk = new ArrayList<>(BATCH_LIMIT);
                    lstAllBatchDataChunks.add(lstSingleBatchChunk);
                }
                lstAllBatchDataChunks.get(dataCounter / BATCH_LIMIT).add(data);
                dataCounter++;
            }
            int batchCounter = 0;
            final int MAX_RETRY_LIMIT = lstAllBatchDataChunks.size() * 5;
            while (batchCounter < lstAllBatchDataChunks.size() && batchCounter < MAX_RETRY_LIMIT) {
                try {
                    TableBatchOperation batchOperation = new TableBatchOperation();
                    for (DataObjectWithAudit data : lstAllBatchDataChunks.get(batchCounter)) {
                        SyncTableEntry syncTableEntry = new SyncTableEntry(inType, inWorkspaceID, data.getID(), System.currentTimeMillis(), inDBVersion, data);
                        batchOperation.insertOrReplace(syncTableEntry);
                    }
                    cloudTable.execute(batchOperation);
                    batchCounter++;
                }
                catch (StorageException ex) {
                    ex.printStackTrace(System.out);
                    System.out.println(">>> The upload batch " + batchCounter + " might be too big, it will be split in half...");
                    List<DataObjectWithAudit> lstProblematicBatch = lstAllBatchDataChunks.get(batchCounter);
                    if (lstProblematicBatch.size() > 2) {
                        lstAllBatchDataChunks.set(batchCounter, lstProblematicBatch.subList(0, lstProblematicBatch.size() / 2));
                        lstAllBatchDataChunks.add(lstProblematicBatch.subList(lstProblematicBatch.size() / 2, lstProblematicBatch.size()));
                    }
                    else {
                        System.err.println(">>> The upload batch " + batchCounter + " can't be made any smaller!!!");
                        throw ex;
                    }
                }
            }
            if (batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The upload batch max retry limit was reached!!!");
                return false;
            }
            return true;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
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
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            // First load the data objects (can't delete on only the ID)
            List<SyncTableEntry> lstData = downloadDataBatch(inStorageConnectionString, inType, inWorkspaceID, 0, inLstRecordIDs);
            // Batches are limited to less than 100 operations and must have under 4 MB total payload.
            // If the batch fails, then try again with a smaller number of operations.
            List<List<SyncTableEntry>> lstAllBatchDataChunks = new ArrayList<>((lstData.size() / 95) + 1);
            int dataCounter = 0;
            for (SyncTableEntry syncTableEntry : lstData) {
                if (dataCounter % BATCH_LIMIT == 0) {
                    List<SyncTableEntry> lstSingleBatchChunk = new ArrayList<>(BATCH_LIMIT);
                    lstAllBatchDataChunks.add(lstSingleBatchChunk);
                }
                lstAllBatchDataChunks.get(dataCounter / BATCH_LIMIT).add(syncTableEntry);
                dataCounter++;
            }
            int batchCounter = 0;
            final int MAX_RETRY_LIMIT = lstAllBatchDataChunks.size() * 5;
            while (batchCounter < lstAllBatchDataChunks.size() && batchCounter < MAX_RETRY_LIMIT) {
                try {
                    TableBatchOperation batchOperation = new TableBatchOperation();
                    for (SyncTableEntry syncTableEntry : lstAllBatchDataChunks.get(batchCounter)) {
                        batchOperation.delete(syncTableEntry);
                    }
                    cloudTable.execute(batchOperation);
                    batchCounter++;
                }
                catch (StorageException ex) {
                    ex.printStackTrace(System.out);
                    System.out.println(">>> The delete batch " + batchCounter + " might be too big, it will be split in half...");
                    List<SyncTableEntry> lstProblematicBatch = lstAllBatchDataChunks.get(batchCounter);
                    if (lstProblematicBatch.size() > 2) {
                        lstAllBatchDataChunks.set(batchCounter, lstProblematicBatch.subList(0, lstProblematicBatch.size() / 2));
                        lstAllBatchDataChunks.add(lstProblematicBatch.subList(lstProblematicBatch.size() / 2, lstProblematicBatch.size()));
                    }
                    else {
                        System.err.println(">>> The delete batch " + batchCounter + " can't be made any smaller!!!");
                        throw ex;
                    }
                }
            }
            if (batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The delete batch max retry limit was reached!!!");
                return false;
            }
            return true;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
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
    
    public static List<SyncTableEntry> downloadDataBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, 
            long inAfterSyncTime, List<Long> inLstRecordIDs) {
        // Limit the URL length to max 32684 characters by splitting long URLs into multiple calls
        List<List<Long>> lstAllBatchDataChunks = new ArrayList<>();
        if (inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
            final int RECORDID_STATEMENT_LENGTH = 35; // Estimated length of " or RowKey eq 9223372036854775807L"
            final int MAX_RECORDIDS_PER_CALL = URL_LIMIT / RECORDID_STATEMENT_LENGTH;
            int idCounter = 0;
            for (Long recordID : inLstRecordIDs) {
                if (idCounter % MAX_RECORDIDS_PER_CALL == 0) {
                    List<Long> lstSingleBatchChunk = new ArrayList<>(MAX_RECORDIDS_PER_CALL);
                    lstAllBatchDataChunks.add(lstSingleBatchChunk);
                }
                lstAllBatchDataChunks.get(idCounter / MAX_RECORDIDS_PER_CALL).add(recordID);
                idCounter++;
            }
            List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
            for (List<Long> lstRecordIDs : lstAllBatchDataChunks) {
                lstSyncTableEntries.addAll(downloadDataSingleBatch(inStorageConnectionString, inType, inWorkspaceID, inAfterSyncTime, lstRecordIDs));
            }
            return lstSyncTableEntries;
        }
        else {
            return downloadDataSingleBatch(inStorageConnectionString, inType, inWorkspaceID, inAfterSyncTime, null);
        }
    }

    private static List<SyncTableEntry> downloadDataSingleBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID, 
            long inAfterSyncTime, List<Long> inLstRecordIDs) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            TableQuery<SyncTableEntry> query = TableQuery.from(SyncTableEntry.class);
            // Create the id filter (if present)
            String idFilter = null;
            if (inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
                for (long recordID : inLstRecordIDs) {
                    if (idFilter == null) {
                        idFilter = TableQuery.generateFilterCondition("RowKey", TableQuery.QueryComparisons.EQUAL, Long.toString(recordID));
                    }
                    else {
                        idFilter = idFilter + " " + TableQuery.Operators.OR + " " 
                                + TableQuery.generateFilterCondition("RowKey", TableQuery.QueryComparisons.EQUAL, Long.toString(recordID));
                    }
                }
            }
            // Build the combined filters
            if (inAfterSyncTime > 0 && inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
                String baseFilter = TableQuery.combineFilters(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)),
                        TableQuery.Operators.AND,
                        TableQuery.generateFilterCondition("SyncTime", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, inAfterSyncTime));
                query = query.where(TableQuery.combineFilters(baseFilter, TableQuery.Operators.AND, idFilter));
            }
            else
            if (inAfterSyncTime > 0) {
                query = query.where(TableQuery.combineFilters(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)),
                        TableQuery.Operators.AND,
                        TableQuery.generateFilterCondition("SyncTime", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, inAfterSyncTime)));
            }
            else
            if (inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
                String baseFilter = TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID));
                query = query.where(TableQuery.combineFilters(baseFilter, TableQuery.Operators.AND, idFilter));
            }
            else {
                query = query.where(TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)));
            }
            // Run the query
            List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
            for (SyncTableEntry syncTableEntry : cloudTable.execute(query)) {
                lstSyncTableEntries.add(syncTableEntry);
            }
            return lstSyncTableEntries;
        }
        catch (StorageException | IOException | InvalidKeyException | IllegalArgumentException | URISyntaxException | IllegalStateException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static List<SyncTableEntry> getSyncListDataBatch(String inStorageConnectionString, WildLogDataType inType, long inWorkspaceID) {
        try {
            CloudTableClient cloudTableClient = getTableClient(inStorageConnectionString);
            CloudTable cloudTable = getTable(cloudTableClient, inType.getDescription());
            TableQuery<SyncTableEntry> query = TableQuery.from(SyncTableEntry.class)
                    .where(TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(inWorkspaceID)))
                    .select(new String[] {"RowKey", "DBVersion", "DataType", "SyncTime", "AuditTime"});
            List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
            for (SyncTableEntry syncTableEntry : cloudTable.execute(query)) {
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
