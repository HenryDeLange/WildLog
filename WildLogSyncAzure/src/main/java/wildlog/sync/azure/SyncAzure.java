package wildlog.sync.azure;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.sync.azure.dataobjects.SyncBlobEntry;
import wildlog.sync.azure.dataobjects.SyncBlobMetadata;
import wildlog.sync.azure.dataobjects.SyncTableEntry;

/**
 * TIPS:
 *  - Moving an Azure Storage Account to a different region:
 *      https://docs.microsoft.com/en-us/azure/storage/common/storage-account-move?tabs=azure-portal
 *      Basies maak mens 'n nuwe account en copy dan die data oor met AzCopy (blobs, tussen servers) of StorageExplorer (tables, met die hand).
 *          ./azcopy copy 'https://<source-storage-account-name>.blob.core.windows.net/<SAS-token>' 'https://<destination-storage-account-name>.blob.core.windows.net/<SAS-token>' --recursive
 *  - Preferred setup:
 *      Performance/Access tier: Standard/Hot
 *      Replication: Locally-redundant storage (LRS)
 *      Account kind: StorageV2 (general purpose v2)
 */
public final class SyncAzure {
    private static final int BATCH_LIMIT = 50;
    private static final int URL_LIMIT = 15000; // Ek dog dis "32684 characters", maar ek kry steeds errors, so ek kies maar iets randomly kleiner (dalk word dit ge-BASE64 of iets?)
    private String storageConnectionString;
    private String accountName;
    private String accountKey;
    private long workspaceID;
    private int dbVersion;
    private Map<WildLogDataType, CloudTable> mapTables = new HashMap<>();
    private Map<WildLogDataType, BlobContainerClient> mapBlobContainers = new HashMap<>();
    
    public SyncAzure(String inStorageConnectionString, String inAccountName, String inAccountKey, long inWorkspaceID, int inDBVersion) {
        storageConnectionString = inStorageConnectionString;
        accountName = inAccountName;
        accountKey = inAccountKey;
        workspaceID = inWorkspaceID;
        dbVersion = inDBVersion;
    }
    
    // TABLE
    
    private CloudTableClient getTableClient() 
            throws InvalidKeyException, URISyntaxException {
        CloudStorageAccount storageAccount;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
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
    
    private CloudTable getTable(WildLogDataType inDataType)
            throws StorageException, IOException, InvalidKeyException, IllegalArgumentException, URISyntaxException, IllegalStateException {
        CloudTable table = mapTables.get(inDataType);
        if (table == null) {
            table = getTableClient().getTableReference(inDataType.getDescription().toLowerCase());
            if (table.createIfNotExists()) {
                System.out.println("Storage Table [" + inDataType.getDescription().toLowerCase() + "] was created.");
            }
            mapTables.put(inDataType, table);
        }
        return table;
    }
    
    public boolean uploadData(WildLogDataType inDataType, DataObjectWithAudit inData) {
        try {
            CloudTable cloudTable = getTable(inDataType);
            if (inData.getSyncIndicator() == 0) {
                inData.setSyncIndicator(new Date().getTime());
            }
            // If this is ExtraData with the bulk import table model, then it is likely too big for table storage and needs to be saved as a blob instead
            boolean blobSuccess = true;
            if (inDataType == WildLogDataType.EXTRA) {
                ExtraData extraData = (ExtraData) inData;
                if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                        && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                    String jsonData = extraData.getDataValue();
                    blobSuccess = uploadText(inDataType, jsonData, extraData.getLinkID(), Long.toString(extraData.getID()));
                    extraData.setDataValue(calculateFullBlobIDForText(extraData.getLinkID(), Long.toString(extraData.getID())));
                }
            }
            // Save the table data
            SyncTableEntry syncTableEntry = new SyncTableEntry(inDataType, workspaceID, inData.getID(), dbVersion, inData);
            cloudTable.execute(TableOperation.insertOrReplace(syncTableEntry));
            return blobSuccess && true;
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean uploadDataBatch(WildLogDataType inDataType, List<DataObjectWithAudit> inLstData) {
        try {
            CloudTable cloudTable = getTable(inDataType);
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
            boolean blobSuccess = true;
            int batchCounter = 0;
            final int MAX_RETRY_LIMIT = lstAllBatchDataChunks.size() * 5;
            while (batchCounter < lstAllBatchDataChunks.size() && batchCounter < MAX_RETRY_LIMIT) {
                try {
                    TableBatchOperation batchOperation = new TableBatchOperation();
                    for (DataObjectWithAudit data : lstAllBatchDataChunks.get(batchCounter)) {
                        if (data.getSyncIndicator() == 0) {
                            data.setSyncIndicator(new Date().getTime());
                        }
                        // If this is ExtraData with the bulk import table model, then it is likely too big for table storage and needs to be saved as a blob instead
                        if (inDataType == WildLogDataType.EXTRA) {
                            ExtraData extraData = (ExtraData) data;
                            if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                                    && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                                String jsonData = extraData.getDataValue();
                                blobSuccess = uploadText(inDataType, jsonData, extraData.getLinkID(), Long.toString(extraData.getID()));
                                extraData.setDataValue(calculateFullBlobIDForText(extraData.getLinkID(), Long.toString(extraData.getID())));
                            }
                        }
                        // Create the batch operation
                        SyncTableEntry syncTableEntry = new SyncTableEntry(inDataType, workspaceID, data.getID(), dbVersion, data);
                        batchOperation.insertOrReplace(syncTableEntry);
                    }
                    cloudTable.execute(batchOperation);
                    batchCounter++;
                }
                catch (StorageException ex) {
                    System.err.println("Azure ErrorCode: " + ex.getErrorCode());
                    System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
                    ex.printStackTrace(System.err);
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
            if (MAX_RETRY_LIMIT != 0 && batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The upload batch max retry limit was reached!!!");
                return false;
            }
            return true && blobSuccess;
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean deleteData(WildLogDataType inDataType, long inRecordID) {
        try {
            CloudTable cloudTable = getTable(inDataType);
            // Dis jammer, maar dit lyk my ek moet eers die waarde laai voor ek kan delete.
            // (Dit soek die "etag" waarde, so ek kan nie self 'n nuwe SyncTableEntry skep nie...)
            boolean blobSuccess = true;
            SyncTableEntry syncTableEntry = downloadData(inDataType, inRecordID);
            if (syncTableEntry != null) {
                cloudTable.execute(TableOperation.delete(syncTableEntry));
                // If this is ExtraData with the bulk import table model, then it also needs to delete the blob storage text
                if (inDataType == WildLogDataType.EXTRA) {
                    ExtraData extraData = (ExtraData) syncTableEntry.getData();
                    if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                            && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                        blobSuccess = deleteFileOrText(inDataType, calculateFullBlobIDForText(extraData.getLinkID(), Long.toString(extraData.getID())));
                    }
                }
            }
            return true && blobSuccess;
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean deleteDataBatch(WildLogDataType inDataType, List<Long> inLstRecordIDs) {
        try {
            CloudTable cloudTable = getTable(inDataType);
            // First load the data objects (can't delete on only the ID)
            List<SyncTableEntry> lstData = downloadDataBatch(inDataType, 0, inLstRecordIDs);
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
            boolean blobSuccess = true;
            int batchCounter = 0;
            final int MAX_RETRY_LIMIT = lstAllBatchDataChunks.size() * 5;
            while (batchCounter < lstAllBatchDataChunks.size() && batchCounter < MAX_RETRY_LIMIT) {
                try {
                    TableBatchOperation batchOperation = new TableBatchOperation();
                    for (SyncTableEntry syncTableEntry : lstAllBatchDataChunks.get(batchCounter)) {
                        // If this is ExtraData with the bulk import table model, then it also needs to delete the blob storage text
                        if (inDataType == WildLogDataType.EXTRA) {
                            ExtraData extraData = (ExtraData) syncTableEntry.getData();
                            if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                                    && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                                blobSuccess = deleteFileOrText(inDataType, calculateFullBlobIDForText(extraData.getLinkID(), Long.toString(extraData.getID())));
                            }
                        }
                        // Create the batch operation
                        batchOperation.delete(syncTableEntry);
                    }
                    cloudTable.execute(batchOperation);
                    batchCounter++;
                }
                catch (StorageException ex) {
                    System.err.println("Azure ErrorCode: " + ex.getErrorCode());
                    System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
                    ex.printStackTrace(System.err);
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
            if (MAX_RETRY_LIMIT != 0 && batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The delete batch max retry limit was reached!!!");
                return false;
            }
            return true && blobSuccess;
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public SyncTableEntry downloadData(WildLogDataType inDataType, long inRecordID) {
        try {
            CloudTable cloudTable = getTable(inDataType);
            SyncTableEntry syncTableEntry = cloudTable.execute(TableOperation.retrieve(
                    Long.toString(workspaceID), Long.toString(inRecordID), SyncTableEntry.class)).getResultAsType();
            // If this is ExtraData with the bulk import table model, then it also needs to fetch the blob storage text
            if (inDataType == WildLogDataType.EXTRA) {
                ExtraData extraData = (ExtraData) syncTableEntry.getData();
                if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                        && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                    String jsonText = downloadText(inDataType, extraData.getLinkID(), Long.toString(extraData.getID()));
                    extraData.setDataValue(jsonText);
                }
            }
            // Return the table data
            return syncTableEntry;
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public List<SyncTableEntry> downloadDataBatch(WildLogDataType inDataType, long inAfterTimestamp, List<Long> inLstRecordIDs) {
        // Limit the URL length by splitting long URLs into multiple calls
        List<List<Long>> lstAllBatchDataChunks = new ArrayList<>();
        if (inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
            final int RECORDID_STATEMENT_LENGTH = 37; // Estimated length of " or RowKey eq 9223372036854775807L"
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
                lstSyncTableEntries.addAll(downloadDataPerBatch(inDataType, inAfterTimestamp, lstRecordIDs));
            }
            return lstSyncTableEntries;
        }
        else {
            return downloadDataPerBatch(inDataType, inAfterTimestamp, null);
        }
    }

    private List<SyncTableEntry> downloadDataPerBatch(WildLogDataType inDataType, long inAfterTimestamp, List<Long> inLstRecordIDs) {
        List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
        try {
            CloudTable cloudTable = getTable(inDataType);
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
            if (workspaceID > 0 && inAfterTimestamp > 0 && inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
                String baseFilter = TableQuery.combineFilters(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID)),
                        TableQuery.Operators.AND,
                        TableQuery.generateFilterCondition("Timestamp", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, new Date(inAfterTimestamp)));
                query = query.where(TableQuery.combineFilters(baseFilter, TableQuery.Operators.AND, idFilter));
            }
            else
            if (workspaceID > 0 && inAfterTimestamp > 0) {
                query = query.where(TableQuery.combineFilters(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID)),
                        TableQuery.Operators.AND,
                        TableQuery.generateFilterCondition("Timestamp", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, new Date(inAfterTimestamp))));
            }
            else
            if (workspaceID > 0 && inLstRecordIDs != null && !inLstRecordIDs.isEmpty()) {
                String baseFilter = TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID));
                query = query.where(TableQuery.combineFilters(baseFilter, TableQuery.Operators.AND, idFilter));
            }
            else 
            if (workspaceID > 0) {
                query = query.where(TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID)));
            }
            else {
                if (workspaceID == 0 && inDataType != WildLogDataType.WILDLOG_OPTIONS) {
                    throw new Exception("ERROR: Only WildLog Options can be read without specifying the PartitionKey (WorkspaceID)!");
                }
            }
            // Make sure the query isn't too long
            if (query.getFilterString() != null && query.getFilterString().length() > URL_LIMIT) {
                int splitIndex = inLstRecordIDs.size() / 2;
                lstSyncTableEntries.addAll(downloadDataPerBatch(inDataType, inAfterTimestamp, inLstRecordIDs.subList(0, splitIndex)));
                lstSyncTableEntries.addAll(downloadDataPerBatch(inDataType, inAfterTimestamp, inLstRecordIDs.subList(splitIndex, inLstRecordIDs.size())));
            }
            else {
                // Run the query
                for (SyncTableEntry syncTableEntry : cloudTable.execute(query)) {
                    lstSyncTableEntries.add(syncTableEntry);
                    // If this is ExtraData with the bulk import table model, then it also needs to fetch the blob storage text
                    if (inDataType == WildLogDataType.EXTRA) {
                        ExtraData extraData = (ExtraData) syncTableEntry.getData();
                        if (extraData.getFieldType() == WildLogExtraDataFieldTypes.WILDLOG 
                                && ExtraData.EXTRA_KEY_IDS.WL_BULK_IMPORT_TABLE_MODEL.toString().equals(extraData.getDataKey())) {
                            String jsonText = downloadText(inDataType, extraData.getLinkID(), Long.toString(extraData.getID()));
                            extraData.setDataValue(jsonText);
                        }
                    }
                }
            }
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncTableEntries;
    }
    
    public boolean workspaceDeleteData(WildLogDataType inDataType) {
        return deleteDataBatch(inDataType, null);
    }
    
    // SYNC LIST - TABLE
    
    public List<SyncTableEntry> getSyncListDataBatch(WildLogDataType inDataType, long inAfterTimestamp) {
        List<SyncTableEntry> lstSyncTableEntries = new ArrayList<>();
        try {
            CloudTable cloudTable = getTable(inDataType);
            TableQuery<SyncTableEntry> query = TableQuery.from(SyncTableEntry.class);
            // Select the columns to return
            if (inDataType == WildLogDataType.FILE) {
                query = query.select(new String[] {"RowKey", "DBVersion", "DataType", "SyncIndicator", "AuditTime", "linkType", "linkID", "originalFileLocation"});
            }
            else
            if (inDataType == WildLogDataType.DELETE_LOG) {
                query = query.select(new String[] {"RowKey", "DBVersion", "DataType", "SyncIndicator", "AuditTime", "AuditUser", "type"});
            }
            else {
                query = query.select(new String[] {"RowKey", "DBVersion", "DataType", "SyncIndicator", "AuditTime"});
            }
            // Filter on the Workspace (PartitionKey) or last sync date (Timestamp)
            // NOTE: Using Timestamp (default Azure field) instead of SyncIndicator because for files the SyncIndicator is the size, not time.
            if (inAfterTimestamp > 0) {
                query = query.where(TableQuery.combineFilters(
                        TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID)),
                        TableQuery.Operators.AND,
                        TableQuery.generateFilterCondition("Timestamp", TableQuery.QueryComparisons.GREATER_THAN_OR_EQUAL, new Date(inAfterTimestamp))));
            }
            else {
                query = query.where(TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, Long.toString(workspaceID)));
            }
            // Get the list of results
            for (SyncTableEntry syncTableEntry : cloudTable.execute(query)) {
                lstSyncTableEntries.add(syncTableEntry);
            }
        }
        catch (StorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ExtendedErrorInformation: " + ex.getExtendedErrorInformation().getErrorMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncTableEntries;
    }
    
    // BLOB
    
    private BlobContainerClient getBlobContainer(WildLogDataType inDataType) 
            throws InvalidKeyException, MalformedURLException {
        BlobContainerClient blobContainerClient = mapBlobContainers.get(inDataType);
        if (blobContainerClient == null) {
            // Setup the client
            StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
            blobContainerClient = new BlobContainerClientBuilder()
                    .endpoint("https://" + accountName + ".blob.core.windows.net")
                    .credential(credential)
                    .containerName(inDataType.getDescription().toLowerCase())
                    .buildClient();
            // Make sure the container has been created
            try {
                blobContainerClient.create();
                System.out.println("Blob Container [" + inDataType.getDescription().toLowerCase() + "] was created.");
            }
            catch (BlobStorageException ex) {
                if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                    System.err.println("Azure ErrorCode: " + ex.getErrorCode());
                    System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
                    ex.printStackTrace(System.err);
                    throw ex;
                }
            }
            // Store the reference for future re-use
            mapBlobContainers.put(inDataType, blobContainerClient);
        }
        return blobContainerClient;
    }
    
    public boolean uploadFile(WildLogDataType inDataType, Path inFilePath, long inParentID, String inRecordID, 
            String inDate, String inLatitude, String inLongitude) {
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            BlobClient blobClient = blobContainerClient.getBlobClient(calculateFullBlobID(inParentID, inRecordID, inFilePath));
            // Set the EXIF values on the blob
            Map<String, String> metadata = new HashMap<>();
            if (inDate != null && !inDate.trim().isEmpty()) {
                metadata.put("DateTime", inDate.replace(':', '.'));
            }
            if (inLatitude != null && !inLatitude.trim().isEmpty()) {
                metadata.put("GPSLatitude", inLatitude.replace('°', 'D').replace('\'', 'M').replace('"', 'S'));
            }
            if (inLongitude != null && !inLongitude.trim().isEmpty()) {
                metadata.put("GPSLongitude", inLongitude.replace('°', 'D').replace('\'', 'M').replace('"', 'S'));
            }
            // Upload the file
            try {
                blobClient.uploadFromFile(inFilePath.toString(), null, null, metadata, null, null, null);
            }
            catch (UncheckedIOException ex) {
                ex.printStackTrace(System.err);
            }
            return true;
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean uploadText(WildLogDataType inDataType, String inText, long inParentID, String inRecordID) {
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            BlobClient blobClient = blobContainerClient.getBlobClient(calculateFullBlobIDForText(inParentID, inRecordID));
            // Upload the text
            try (ByteArrayInputStream dataStream = new ByteArrayInputStream(inText.getBytes())) {
                blobClient.upload(dataStream, inText.length(), true);
            }
            catch (UncheckedIOException ex) {
                ex.printStackTrace(System.err);
            }
            return true;
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public SyncBlobMetadata downloadFile(WildLogDataType inDataType, Path inFilePath, long inParentID, String inRecordID) {
        SyncBlobMetadata syncBlobMetadata = new SyncBlobMetadata();
        String blobID = calculateFullBlobID(inParentID, inRecordID, inFilePath);
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobID);
            // Download the file
            if (!Files.exists(inFilePath)) {
                Files.createDirectories(inFilePath.getParent());
            }
            BlobProperties blobProperties;
            if (Files.exists(inFilePath)) {
                blobProperties = blobClient.downloadToFile(inFilePath.toString(), true);
            }
            else {
                blobProperties = blobClient.downloadToFile(inFilePath.toString());
            }
            // Set the EXIF values on the file
            syncBlobMetadata.setDatetime(blobProperties.getMetadata().get("DateTime"));
            syncBlobMetadata.setLatitude(blobProperties.getMetadata().get("GPSLatitude"));
            syncBlobMetadata.setLongitude(blobProperties.getMetadata().get("GPSLongitude"));
            syncBlobMetadata.setSuccess(true);
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            System.err.println("Failed Download: Blob ID = " + blobID);
            ex.printStackTrace(System.err);
        }
        return syncBlobMetadata;
    }
    
    public String downloadText(WildLogDataType inDataType, long inParentID, String inRecordID) {
        String blobID = calculateFullBlobIDForText(inParentID, inRecordID);
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobID);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                blobClient.download(outputStream);
                return new String(outputStream.toByteArray());
            }
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            System.err.println("Failed Download: Blob ID = " + blobID);
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    // Note:
    // RecordID is String want stashed files moet die oorspronklikke naam gebruik, 
    // maar normale files moet hulle Long ID gebruik.
    public String calculateFullBlobID(long inParentID, String inRecordID, Path inFilePath) {
        String blobPath = Long.toString(workspaceID) + "/"
                + Long.toString(inParentID) + "/"
                + inRecordID + inFilePath.getFileName().toString().substring(inFilePath.getFileName().toString().lastIndexOf('.'));
        if (blobPath.endsWith(".jpeg")) {
            blobPath = blobPath.substring(0, blobPath.length() - 4) + "jpg";
        }
        return blobPath.toLowerCase(); // Extentions might be uppercase or lowercase, so make all lowercase on the cloud
    }
    
    public String calculateFullBlobIDForText(long inParentID, String inRecordID) {
        String blobPath = Long.toString(workspaceID) + "/" + Long.toString(inParentID) + "/" + inRecordID + ".txt";
        return blobPath.toLowerCase();
    }
    
    public boolean deleteFileOrText(WildLogDataType inDataType, String inFullBlobName) {
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            BlobClient blobClient = blobContainerClient.getBlobClient(inFullBlobName);
            blobClient.delete();
            return true;
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean workspaceDeleteFiles(WildLogDataType inDataType) {
        try {
// TODO: Batch deleting werk nie lekker nie (soms werk dit en soms nie...)
//            // Setup the service client (note: not the container client)
//            StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
//            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
//                    .endpoint("https://" + accountName + ".blob.core.windows.net")
//                    .credential(credential)
//                    .buildClient();
//            BlobBatchClient blobBatchClient = new BlobBatchClientBuilder(blobServiceClient).buildClient();
//            List<SyncBlobEntry> lstBlobs = getSyncListFilesBatch(inDataType);
//            List<String> lstBlobURLs = new ArrayList<>(lstBlobs.size());
//            for (SyncBlobEntry syncBlobEntry : lstBlobs) {
//                lstBlobURLs.add(blobServiceClient.getBlobContainerClient(inDataType.getDescription().toLowerCase()).getBlobContainerUrl()
//                        + "/" + syncBlobEntry.getFullBlobID());
//            }
//            blobBatchClient.deleteBlobs(lstBlobURLs, DeleteSnapshotsOptionType.INCLUDE);
//            return true;
            // Delete maar die blobs een vir een...
            List<SyncBlobEntry> lstBlobs = getSyncListFilesBatch(inDataType);
            for (SyncBlobEntry syncBlobEntry : lstBlobs) {
                deleteFileOrText(inDataType, syncBlobEntry.getFullBlobID());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    // SYNC LIST - BLOB
    
    /**
     * Lists all the blobs in associated with the workspace
     */
    public List<SyncBlobEntry> getSyncListFilesBatch(WildLogDataType inDataType) {
        List<SyncBlobEntry> lstSyncBlobEntries = new ArrayList<>();
        try {
            BlobContainerClient blobContainerClient = getBlobContainer(inDataType);
            ListBlobsOptions options = new ListBlobsOptions();
            options.setPrefix(Long.toString(workspaceID));
            for (BlobItem blobItem : blobContainerClient.listBlobs(options, null)) {
                String[] namePieces = blobItem.getName().split("/");
                long recordsID = 0;
                String name = namePieces[2].substring(0, namePieces[2].lastIndexOf('.'));
                try {
                    recordsID = Long.parseLong(name);
                }
                catch (NumberFormatException ex) {
                    // Ignore (stashed files will not be numbers, but imported files of type WildLogFile will use IDs)
                }
                lstSyncBlobEntries.add(new SyncBlobEntry(
                        inDataType, 
                        Long.parseLong(namePieces[0]),
                        Long.parseLong(namePieces[1]), 
                        recordsID,
                        blobItem.getName()));
            }
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncBlobEntries;
    }
    
    /**
     * Lists all the parents (Elements, Locations, etc.) that have blobs, without needing to list all the blobs underneath each parent.
     */
    public List<SyncBlobEntry> getSyncListFileParentsBatch(WildLogDataType inDataType) {
        ArrayList<SyncBlobEntry> lstParents = new ArrayList<>();
        try {
// FIXME: Maybe rather use the blobContainerClient.listBlobsByHierarchy() ???
            // Setup the blob container
            CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
            CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(inDataType.getDescription().toLowerCase());
            cloudBlobContainer.createIfNotExists();
            // Get the folders
            CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(Long.toString(workspaceID));
            Iterable<ListBlobItem> blobItems = cloudBlobDirectory.listBlobs(null, false, null, null, null);
            for (ListBlobItem blobItem : blobItems) {
                String[] namePieces = blobItem.getUri().getPath().split("/");
                lstParents.add(new SyncBlobEntry(
                        inDataType, 
                        Long.parseLong(namePieces[2]),
                        Long.parseLong(namePieces[namePieces.length - 2]), 
                        Long.parseLong(namePieces[namePieces.length - 1]), 
                        blobItem.getUri().getPath()));
            }
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstParents;
    }
    
    /**
     * Lists all the blobs under the specified parent (Element, Location, etc.), without listing the blobs from other parents.
     */
    public List<SyncBlobEntry> getSyncListFileChildrenBatch(WildLogDataType inDataType, long inParentID) {
        ArrayList<SyncBlobEntry> lstParents = new ArrayList<>();
        try {
// FIXME: Maybe rather use the blobContainerClient.listBlobsByHierarchy() ???
            // Setup the blob container
            CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
            CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(inDataType.getDescription().toLowerCase());
            cloudBlobContainer.createIfNotExists();
            // Get the folders
            CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(Long.toString(workspaceID) + "/" + inParentID);
            Iterable<ListBlobItem> blobItems = cloudBlobDirectory.listBlobs(null, false, null, null, null);
            for (ListBlobItem blobItem : blobItems) {
                String[] namePieces = blobItem.getUri().getPath().split("/");
                long recordsID = 0;
                String name = namePieces[namePieces.length - 1].substring(0, namePieces[namePieces.length - 1].lastIndexOf('.'));
                try {
                    recordsID = Long.parseLong(name);
                }
                catch (NumberFormatException ex) {
                    // Ignore (stashed files will not be numbers, but imported files of type WildLogFile will use IDs)
                }
                lstParents.add(new SyncBlobEntry(
                        inDataType, 
                        Long.parseLong(namePieces[2]),
                        Long.parseLong(namePieces[namePieces.length - 2]), 
                        recordsID, 
                        blobItem.getUri().getPath()));
            }
        }
        catch (BlobStorageException ex) {
            System.err.println("Azure ErrorCode: " + ex.getErrorCode());
            System.err.println("Azure ServiceMessage: " + ex.getServiceMessage());
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstParents;
    }

    public long getWorkspaceID() {
        return workspaceID;
    }
    
}
