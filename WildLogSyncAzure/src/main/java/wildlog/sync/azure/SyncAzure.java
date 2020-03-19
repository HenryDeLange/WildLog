package wildlog.sync.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.DownloadResponse;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.blob.ListBlobsOptions;
import com.microsoft.azure.storage.blob.Metadata;
import com.microsoft.azure.storage.blob.PipelineOptions;
import com.microsoft.azure.storage.blob.ServiceURL;
import com.microsoft.azure.storage.blob.SharedKeyCredentials;
import com.microsoft.azure.storage.blob.StorageURL;
import com.microsoft.azure.storage.blob.TransferManager;
import com.microsoft.azure.storage.blob.TransferManagerUploadToBlockBlobOptions;
import com.microsoft.azure.storage.blob.models.BlobItem;
import com.microsoft.azure.storage.blob.models.ContainerListBlobFlatSegmentResponse;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.rest.v2.RestException;
import io.reactivex.Single;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.sync.azure.dataobjects.SyncBlobEntry;
import wildlog.sync.azure.dataobjects.SyncBlobMetadata;
import wildlog.sync.azure.dataobjects.SyncTableEntry;

/**
 * TIPS:
 *  - Moving an Azure Storage Account to a different region:
 *      https://docs.microsoft.com/en-us/azure/storage/common/storage-account-move?tabs=azure-portal
 *      Basies maak mens 'n nuwe account en copy dan die data oor met AzCopy (blobs, tussen servers) of StorageExplorer (tables, met die hand).
 *          ./azcopy copy 'https://<source-storage-account-name>.blob.core.windows.net/<SAS-token>' 'https://<destination-storage-account-name>.blob.core.windows.net/' --recursive
 *  - Preferred setup:
 *      Performance/Access tier: Standard/Hot
 *      Replication: Locally-redundant storage (LRS)
 *      Account kind: StorageV2 (general purpose v2)
 */
public final class SyncAzure {
    private static final int BATCH_LIMIT = 50;
    private static final int URL_LIMIT = 15000; // Ek dog dis "32684 characters", maar ek kry steeds errors, so ek kies maar iets randomly kleiner
    private static final int MAX_BLOB_SIZE = 1*1024*1024; // bytes (1MB)
    private static final int MAX_BLOB_UPLOAD_SIZE = 5*1024*1024; // bytes (5MB)
    private String storageConnectionString;
    private String accountName;
    private String accountKey;
    private long workspaceID;
    private int dbVersion;
    private Map<WildLogDataType, CloudTable> mapTables = new HashMap<>();
    private Map<WildLogDataType, ContainerURL> mapContainers = new HashMap<>();
    
    public SyncAzure(String inStorageConnectionString, String inAccountName, String inAccountKey, long inWorkspaceID, int inDBVersion) {
        storageConnectionString = inStorageConnectionString;
        accountName = inAccountName;
        accountKey = inAccountKey;
        workspaceID = inWorkspaceID;
        dbVersion = inDBVersion;
    }
    
    // DATA
    
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
            SyncTableEntry syncTableEntry = new SyncTableEntry(inDataType, workspaceID, inData.getID(), dbVersion, inData);
            cloudTable.execute(TableOperation.insertOrReplace(syncTableEntry));
            return true;
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
            int batchCounter = 0;
            final int MAX_RETRY_LIMIT = lstAllBatchDataChunks.size() * 5;
            while (batchCounter < lstAllBatchDataChunks.size() && batchCounter < MAX_RETRY_LIMIT) {
                try {
                    TableBatchOperation batchOperation = new TableBatchOperation();
                    for (DataObjectWithAudit data : lstAllBatchDataChunks.get(batchCounter)) {
                        if (data.getSyncIndicator() == 0) {
                            data.setSyncIndicator(new Date().getTime());
                        }
                        SyncTableEntry syncTableEntry = new SyncTableEntry(inDataType, workspaceID, data.getID(), dbVersion, data);
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
            if (MAX_RETRY_LIMIT != 0 && batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The upload batch max retry limit was reached!!!");
                return false;
            }
            return true;
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
            SyncTableEntry syncTableEntry = downloadData(inDataType, inRecordID);
            if (syncTableEntry != null) {
                cloudTable.execute(TableOperation.delete(syncTableEntry));
            }
            return true;
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
            if (MAX_RETRY_LIMIT != 0 && batchCounter >= MAX_RETRY_LIMIT) {
                System.err.println("The delete batch max retry limit was reached!!!");
                return false;
            }
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public SyncTableEntry downloadData(WildLogDataType inDataType, long inRecordID) {
        try {
            CloudTable cloudTable = getTable(inDataType);
            return cloudTable.execute(TableOperation.retrieve(
                    Long.toString(workspaceID), Long.toString(inRecordID), SyncTableEntry.class)).getResultAsType();
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
System.out.println("Downloading table data: " + inLstRecordIDs.size() + " records");
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
else {
System.out.println("Downloading table data: " + null + " records");
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
if (query.getFilterString() != null) {
System.out.println("Submitting download query for table data: " + query.getFilterString().length() + " query length");
}
else {
System.out.println("Submitting download query for table data: " + null + " query length");
}
            // Make sure the query isn't too long
            if (query.getFilterString() != null && query.getFilterString().length() > URL_LIMIT) {
System.out.println("SPLITTING");
                int splitIndex = inLstRecordIDs.size() / 2;
                lstSyncTableEntries.addAll(downloadDataPerBatch(inDataType, inAfterTimestamp, inLstRecordIDs.subList(0, splitIndex)));
                lstSyncTableEntries.addAll(downloadDataPerBatch(inDataType, inAfterTimestamp, inLstRecordIDs.subList(splitIndex, inLstRecordIDs.size())));
            }
            else {
                // Run the query
                for (SyncTableEntry syncTableEntry : cloudTable.execute(query)) {
                    lstSyncTableEntries.add(syncTableEntry);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncTableEntries;
    }
    
    // SYNC LIST - DATA
    
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
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncTableEntries;
    }
    
    // FILES
    
    private ContainerURL getContainerURL(WildLogDataType inDataType) 
            throws InvalidKeyException, MalformedURLException {
        ContainerURL containerURL = mapContainers.get(inDataType);
        if (containerURL == null) {
            SharedKeyCredentials sharedKeyCredentials = new SharedKeyCredentials(accountName, accountKey);
            ServiceURL serviceURL = new ServiceURL(new URL("https://" + accountName + ".blob.core.windows.net"), 
                    StorageURL.createPipeline(sharedKeyCredentials, new PipelineOptions()));
            containerURL = serviceURL.createContainerURL(inDataType.getDescription().toLowerCase());
            try {
                containerURL.create(null, null, null).blockingGet();
                System.out.println("Blob Container [" + inDataType.getDescription().toLowerCase() + "] was created.");
            }
            catch (Exception ex){
                if (ex instanceof RestException && ((RestException)ex).response().statusCode() != 409) {
                    throw ex;
                }
            }
            mapContainers.put(inDataType, containerURL);
        }
        return containerURL;
    }
    
    public boolean uploadFile(WildLogDataType inDataType, Path inFilePath, long inParentID, long inRecordID, 
            String inDate, String inLatitude, String inLongitude) {
        try {
            ContainerURL container = getContainerURL(inDataType);
            BlockBlobURL blockBlob = container.createBlockBlobURL(calculateFullBlobID(inParentID, inRecordID, inFilePath));
            // Set the EXIF values on the blob
            Metadata metadata = new Metadata();
            if (inDate != null && !inDate.trim().isEmpty()) {
                metadata.put("DateTime", inDate.replace(':', '.'));
            }
            if (inLatitude != null && !inLatitude.trim().isEmpty()) {
                metadata.put("GPSLatitude", inLatitude.replace('°', 'D').replace('\'', 'M').replace('"', 'S'));
            }
            if (inLongitude != null && !inLongitude.trim().isEmpty()) {
                metadata.put("GPSLongitude", inLongitude.replace('°', 'D').replace('\'', 'M').replace('"', 'S'));
            }
            blockBlob.setMetadata(metadata);
            TransferManagerUploadToBlockBlobOptions blobOptions = new TransferManagerUploadToBlockBlobOptions(null, null, metadata, null, null);
            // Upload the file
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(inFilePath);
            try {
                TransferManager.uploadFileToBlockBlob(fileChannel, blockBlob, MAX_BLOB_SIZE, MAX_BLOB_UPLOAD_SIZE, blobOptions).blockingGet();
            }
            finally {
                fileChannel.close();
            }
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    public SyncBlobMetadata downloadFile(WildLogDataType inDataType, Path inFilePath, long inParentID, long inRecordID) {
        SyncBlobMetadata syncBlobMetadata = new SyncBlobMetadata();
        String blobID = calculateFullBlobID(inParentID, inRecordID, inFilePath);
        try {
            ContainerURL container = getContainerURL(inDataType);
            BlockBlobURL blockBlob = container.createBlockBlobURL(blobID);
            if (!Files.exists(inFilePath)) {
                Files.createDirectories(inFilePath.getParent());
                Files.createFile(inFilePath);
            }
            // Download the file
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(inFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            try {
                TransferManager.downloadBlobToFile(fileChannel, blockBlob, null, null).blockingGet();
            }
            finally {
                fileChannel.close();
            }
            // Set the EXIF values on the file
            DownloadResponse downloadResponse = blockBlob.download().blockingGet();
            syncBlobMetadata.setDatetime(downloadResponse.headers().metadata().get("DateTime"));
            syncBlobMetadata.setLatitude(downloadResponse.headers().metadata().get("GPSLatitude"));
            syncBlobMetadata.setLongitude(downloadResponse.headers().metadata().get("GPSLongitude"));
            syncBlobMetadata.setSuccess(true);
        }
        catch (Exception ex) {
            System.err.println("Failed Download: Blob ID = " + blobID);
            ex.printStackTrace(System.err);
        }
        return syncBlobMetadata;
    }
    
    public String calculateFullBlobID(long inParentID, long inRecordID, Path inFilePath) {
        String blobPath = Long.toString(workspaceID) + "/"
                + Long.toString(inParentID) + "/"
                + Long.toString(inRecordID) + inFilePath.getFileName().toString().substring(inFilePath.getFileName().toString().lastIndexOf('.'));
        if (blobPath.endsWith(".jpeg")) {
            blobPath = blobPath.substring(0, blobPath.length() - 4) + "jpg";
        }
        return blobPath.toLowerCase(); // Extentions might be uppercase or lowercase, so make all lowercase on the cloud
    }
    
    public boolean deleteFile(WildLogDataType inDataType, String inFullBlobName) {
        try {
            ContainerURL container = getContainerURL(inDataType);
            BlockBlobURL blockBlob = container.createBlockBlobURL(inFullBlobName);
            blockBlob.delete(null, null, null).blockingGet();
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
    // SYNC LIST - FILES
    
    public List<SyncBlobEntry> getSyncListFilesBatch(WildLogDataType inDataType) {
        List<SyncBlobEntry> lstSyncBlobEntries = new ArrayList<>();
        try {
            ContainerURL container = getContainerURL(inDataType);
            ListBlobsOptions options = new ListBlobsOptions();
            options.withMaxResults(100);
            options.withPrefix(Long.toString(workspaceID));
            container.listBlobsFlatSegment(null, options, null)
                    .flatMap(containerListBlobFlatSegmentResponse ->
                            listAllBlobs(container, containerListBlobFlatSegmentResponse, options, inDataType, lstSyncBlobEntries))
                    .blockingGet();
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstSyncBlobEntries;
    }

    private Single<ContainerListBlobFlatSegmentResponse> listAllBlobs(ContainerURL inContainer, ContainerListBlobFlatSegmentResponse inResponse, 
            ListBlobsOptions inOptions, WildLogDataType inDataType, List<SyncBlobEntry> inLstSyncBlobEntries) {
        if (inResponse.body().segment() != null) {
            for (BlobItem blobItem : inResponse.body().segment().blobItems()) {
                String[] namePieces = blobItem.name().split("/");
                long recordsID = 0;
                String name = namePieces[2].substring(0, namePieces[2].lastIndexOf('.'));
                try {
                    recordsID = Long.parseLong(name);
                }
                catch (NumberFormatException ex) {
                    // Ignore (stashed files will not be numbers, but imported files of type WildLogFile will use IDs)
                }
                inLstSyncBlobEntries.add(new SyncBlobEntry(
                        inDataType, 
                        Long.parseLong(namePieces[0]),
                        Long.parseLong(namePieces[1]), 
                        recordsID,
                        blobItem.name()));
            }
        }
        // If there is not another segment, return this response as the final response
        if (inResponse.body().nextMarker() == null) {
            return Single.just(inResponse);
        }
        else {
            // IMPORTANT:
            // ListBlobsFlatSegment returns the start of the next segment
            // You MUST use this to get the next segment
            String nextMarker = inResponse.body().nextMarker();
            return inContainer.listBlobsFlatSegment(nextMarker, inOptions, null)
                    .flatMap(containersListBlobFlatSegmentResponse ->
                            listAllBlobs(inContainer, containersListBlobFlatSegmentResponse, inOptions, inDataType, inLstSyncBlobEntries));
        }
    }
    
    public List<SyncBlobEntry> getSyncListFileParentsBatch(WildLogDataType inDataType) {
        ArrayList<SyncBlobEntry> lstParents = new ArrayList<>();
        try {
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
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstParents;
    }
    
    public List<SyncBlobEntry> getSyncListFileChildrenBatch(WildLogDataType inDataType, long inParentID) {
        ArrayList<SyncBlobEntry> lstParents = new ArrayList<>();
        try {
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
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return lstParents;
    }
    
}
