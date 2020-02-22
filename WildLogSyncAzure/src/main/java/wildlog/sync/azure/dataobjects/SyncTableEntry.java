package wildlog.sync.azure.dataobjects;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.Ignore;
import com.microsoft.azure.storage.table.TableServiceEntity;
import java.util.Date;
import java.util.HashMap;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogDeleteLog;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.data.enums.system.WildLogUserTypes;
import wildlog.data.utils.UtilsData;


public class SyncTableEntry extends TableServiceEntity {
    private static final EntityProperty EMPTY_STRING = new EntityProperty((String) null);
    private static final EntityProperty EMPTY_DATE = new EntityProperty((Date) null);
    private static final EntityProperty EMPTY_BYTE = new EntityProperty((byte) 0);
    private static final EntityProperty EMPTY_INT = new EntityProperty(0);
    private static final EntityProperty EMPTY_LONG = new EntityProperty(0L);
    private static final EntityProperty EMPTY_DOUBLE = new EntityProperty(0.0);
    private static final EntityProperty EMPTY_BOOLEAN = new EntityProperty(false);
    private String dataType;
    private int dbVersion;
    private DataObjectWithAudit data;

    
    public SyncTableEntry() {
        super();
    }

    public SyncTableEntry(WildLogDataType inDataType, long inWorkspaceID, long inRecordID, int inDBVersion, DataObjectWithAudit inData) {
        super();
        if (inDataType != null) {
            dataType = inDataType.toString();
        }
        dbVersion = inDBVersion;
        data = inData;
        // Set the Azure table key fields
        partitionKey = Long.toString(inWorkspaceID);
        rowKey = Long.toString(inRecordID);
    }
    
    
    public String getDataType() {
        return dataType;
    }
    
    @Ignore
    public WildLogDataType getWildLogDataType() {
        return WildLogDataType.getEnumFromText(dataType);
    }

    public void setDataType(String inDataType) {
        dataType = inDataType;
    }
    
    @Ignore
    public void setWildLogDataType(WildLogDataType inDataType) {
        if (inDataType != null) {
            dataType = inDataType.toString();
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

    public int getDBVersion() {
        return dbVersion;
    }

    public void setDBVersion(int inDBVersion) {
        dbVersion = inDBVersion;
    }

    @Ignore
    public DataObjectWithAudit getData() {
        return data;
    }

    @Ignore
    public void setData(DataObjectWithAudit inData) {
        data = inData;
    }

    @Override
    public String toString() {
        return "[" + dataType + "] (" + dbVersion + ") " + partitionKey + "/" + rowKey + " : " + data;
    }

    // READ PROPERTIES:
    
    @Override
    public void readEntity(HashMap<String, EntityProperty> inProperties, OperationContext inOpContext)
            throws StorageException {
        // First read the fields for this class
        super.readEntity(inProperties, inOpContext);
        // Next read the fields for the data object
        if (dataType.equals(WildLogDataType.ELEMENT.getKey())) {
            if (dbVersion >= 14) {
                data = readElementV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.LOCATION.getKey())) {
            if (dbVersion >= 14) {
                data = readLocationV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.VISIT.getKey())) {
            if (dbVersion >= 14) {
                data = readVisitV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.SIGHTING.getKey())) {
            if (dbVersion >= 14) {
                data = readSightingV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.FILE.getKey())) {
            if (dbVersion >= 14) {
                data = readFileV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.WILDLOG_USER.getKey())) {
            if (dbVersion >= 14) {
                data = readUserV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.DELETE_LOG.getKey())) {
            if (dbVersion >= 14) {
                data = readDeleteLogV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.WILDLOG_OPTIONS.getKey())) {
            if (dbVersion >= 14) {
                data = readOptionsV14(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.EXTRA.getKey())) {
            if (dbVersion >= 15) {
                data = readExtraDataV15(inProperties);
            }
        }
        else {
            data = null;
        }
    }
    
    private ElementCore readElementV14(HashMap<String, EntityProperty> inProperties) {
        ElementCore tempData = new ElementCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setPrimaryName(inProperties.getOrDefault("primaryName", EMPTY_STRING).getValueAsString());
        tempData.setOtherName(inProperties.getOrDefault("otherName", EMPTY_STRING).getValueAsString());
        tempData.setScientificName(inProperties.getOrDefault("scientificName", EMPTY_STRING).getValueAsString());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setDistribution(inProperties.getOrDefault("distribution", EMPTY_STRING).getValueAsString());
        tempData.setNutrition(inProperties.getOrDefault("nutrition", EMPTY_STRING).getValueAsString());
        tempData.setDiagnosticDescription(inProperties.getOrDefault("diagnosticDescription", EMPTY_STRING).getValueAsString());
        tempData.setEndangeredStatus(EndangeredStatus.getEnumFromID((byte) inProperties.getOrDefault("endangeredStatus", EMPTY_BYTE).getValueAsInteger()));
        tempData.setBehaviourDescription(inProperties.getOrDefault("behaviourDescription", EMPTY_STRING).getValueAsString());
        tempData.setType(ElementType.getEnumFromID((byte) inProperties.getOrDefault("type", EMPTY_BYTE).getValueAsInteger()));
        tempData.setFeedingClass(FeedingClass.getEnumFromID((byte) inProperties.getOrDefault("feedingClass", EMPTY_BYTE).getValueAsInteger()));
        tempData.setReferenceID(inProperties.getOrDefault("referenceID", EMPTY_STRING).getValueAsString());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private LocationCore readLocationV14(HashMap<String, EntityProperty> inProperties) {
        LocationCore tempData = new LocationCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setName(inProperties.getOrDefault("name", EMPTY_STRING).getValueAsString());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setRating(LocationRating.getEnumFromID((byte) inProperties.getOrDefault("rating", EMPTY_BYTE).getValueAsInteger()));
        tempData.setGameViewingRating(GameViewRating.getEnumFromID((byte) inProperties.getOrDefault("gameViewingRating", EMPTY_BYTE).getValueAsInteger()));
        tempData.setHabitatType(inProperties.getOrDefault("habitatType", EMPTY_STRING).getValueAsString());
        tempData.setLatitude(Latitudes.getEnumFromText(inProperties.getOrDefault("latitude", EMPTY_STRING).getValueAsString()));
        tempData.setLatDegrees(inProperties.getOrDefault("latDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLatMinutes(inProperties.getOrDefault("latMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLatSeconds(inProperties.getOrDefault("latSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setLongitude(Longitudes.getEnumFromText(inProperties.getOrDefault("longitude", EMPTY_STRING).getValueAsString()));
        tempData.setLonDegrees(inProperties.getOrDefault("lonDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLonMinutes(inProperties.getOrDefault("lonMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLonSeconds(inProperties.getOrDefault("lonSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setGPSAccuracy(GPSAccuracy.getEnumFromID((byte) inProperties.getOrDefault("gpsAccuracy", EMPTY_BYTE).getValueAsInteger()));
        tempData.setGPSAccuracyValue(inProperties.getOrDefault("gpsAccuracyValue", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private VisitCore readVisitV14(HashMap<String, EntityProperty> inProperties) {
        VisitCore tempData = new VisitCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setName(inProperties.getOrDefault("name", EMPTY_STRING).getValueAsString());
        tempData.setStartDate(inProperties.getOrDefault("startDate", EMPTY_DATE).getValueAsDate());
        tempData.setEndDate(inProperties.getOrDefault("endDate", EMPTY_DATE).getValueAsDate());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setGameWatchingIntensity(GameWatchIntensity.getEnumFromID((byte) inProperties.getOrDefault("gameWatchingIntensity", EMPTY_BYTE).getValueAsInteger()));
        tempData.setType(VisitType.getEnumFromID((byte) inProperties.getOrDefault("type", EMPTY_BYTE).getValueAsInteger()));
        tempData.setLocationID(inProperties.getOrDefault("locationID", EMPTY_LONG).getValueAsLong());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private SightingCore readSightingV14(HashMap<String, EntityProperty> inProperties) {
        SightingCore tempData = new SightingCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setDate(inProperties.getOrDefault("date", EMPTY_DATE).getValueAsDate());
        tempData.setElementID(inProperties.getOrDefault("elementID", EMPTY_LONG).getValueAsLong());
        tempData.setLocationID(inProperties.getOrDefault("locationID", EMPTY_LONG).getValueAsLong());
        tempData.setVisitID(inProperties.getOrDefault("visitID", EMPTY_LONG).getValueAsLong());
        tempData.setTimeOfDay(ActiveTimeSpesific.getEnumFromID((byte) inProperties.getOrDefault("timeOfDay", EMPTY_BYTE).getValueAsInteger()));
        tempData.setWeather(Weather.getEnumFromID((byte) inProperties.getOrDefault("weather", EMPTY_BYTE).getValueAsInteger()));
        tempData.setViewRating(ViewRating.getEnumFromID((byte) inProperties.getOrDefault("viewRating", EMPTY_BYTE).getValueAsInteger()));
        tempData.setCertainty(Certainty.getEnumFromID((byte) inProperties.getOrDefault("certainty", EMPTY_BYTE).getValueAsInteger()));
        tempData.setNumberOfElements(inProperties.getOrDefault("numberOfElements", EMPTY_INT).getValueAsInteger());
        tempData.setDetails(inProperties.getOrDefault("details", EMPTY_STRING).getValueAsString());
        tempData.setLatitude(Latitudes.getEnumFromText(inProperties.getOrDefault("latitude", EMPTY_STRING).getValueAsString()));
        tempData.setLatDegrees(inProperties.getOrDefault("latDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLatMinutes(inProperties.getOrDefault("latMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLatSeconds(inProperties.getOrDefault("latSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setLongitude(Longitudes.getEnumFromText(inProperties.getOrDefault("longitude", EMPTY_STRING).getValueAsString()));
        tempData.setLonDegrees(inProperties.getOrDefault("lonDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLonMinutes(inProperties.getOrDefault("lonMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLonSeconds(inProperties.getOrDefault("lonSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setSightingEvidence(SightingEvidence.getEnumFromID((byte) inProperties.getOrDefault("sightingEvidence", EMPTY_BYTE).getValueAsInteger()));
        tempData.setMoonlight(Moonlight.getEnumFromID((byte) inProperties.getOrDefault("moonlight", EMPTY_BYTE).getValueAsInteger()));
        tempData.setMoonPhase(inProperties.getOrDefault("moonPhase", EMPTY_INT).getValueAsInteger());
        tempData.setTemperature(inProperties.getOrDefault("temperature", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setUnitsTemperature(UnitsTemperature.getEnumFromID((byte) inProperties.getOrDefault("unitsTemperature", EMPTY_BYTE).getValueAsInteger()));
        tempData.setLifeStatus(LifeStatus.getEnumFromID((byte) inProperties.getOrDefault("lifeStatus", EMPTY_BYTE).getValueAsInteger()));
        tempData.setSex(Sex.getEnumFromID((byte) inProperties.getOrDefault("sex", EMPTY_BYTE).getValueAsInteger()));
        tempData.setTag(inProperties.getOrDefault("tag", EMPTY_STRING).getValueAsString());
        tempData.setDurationMinutes(inProperties.getOrDefault("durationMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setDurationSeconds(inProperties.getOrDefault("durationSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setGPSAccuracy(GPSAccuracy.getEnumFromID((byte) inProperties.getOrDefault("gpsAccuracy", EMPTY_BYTE).getValueAsInteger()));
        tempData.setGPSAccuracyValue(inProperties.getOrDefault("gpsAccuracyValue", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setTimeAccuracy(TimeAccuracy.getEnumFromID((byte) inProperties.getOrDefault("timeAccuracy", EMPTY_BYTE).getValueAsInteger()));
        tempData.setAge(Age.getEnumFromID((byte) inProperties.getOrDefault("age", EMPTY_BYTE).getValueAsInteger()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private WildLogFileCore readFileV14(HashMap<String, EntityProperty> inProperties) {
        WildLogFileCore tempData = new WildLogFileCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setLinkID(inProperties.getOrDefault("linkID", EMPTY_LONG).getValueAsLong());
        tempData.setLinkType(WildLogDataType.getEnumFromText(inProperties.getOrDefault("linkType", EMPTY_STRING).getValueAsString()));
        tempData.setFilename(inProperties.getOrDefault("filename", EMPTY_STRING).getValueAsString());
        tempData.setDBFilePath(inProperties.getOrDefault("originalFileLocation", EMPTY_STRING).getValueAsString());
        tempData.setUploadDate(inProperties.getOrDefault("uploadDate", EMPTY_DATE).getValueAsDate());
        tempData.setFileType(WildLogFileType.getEnumFromText(inProperties.getOrDefault("fileType", EMPTY_STRING).getValueAsString()));
        tempData.setDefaultFile(inProperties.getOrDefault("defaultFile", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setFileDate(inProperties.getOrDefault("fileDate", EMPTY_DATE).getValueAsDate());
        tempData.setFileSize(inProperties.getOrDefault("fileSize", EMPTY_LONG).getValueAsLong());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private WildLogUser readUserV14(HashMap<String, EntityProperty> inProperties) {
        WildLogUser tempData = new WildLogUser();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setUsername(inProperties.getOrDefault("username", EMPTY_STRING).getValueAsString());
        tempData.setPassword(inProperties.getOrDefault("password", EMPTY_STRING).getValueAsString());
        tempData.setType(WildLogUserTypes.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private WildLogDeleteLog readDeleteLogV14(HashMap<String, EntityProperty> inProperties) {
        WildLogDeleteLog tempData = new WildLogDeleteLog();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setType(WildLogDataType.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private WildLogOptions readOptionsV14(HashMap<String, EntityProperty> inProperties) {
        WildLogOptions tempData = new WildLogOptions();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setWorkspaceID(inProperties.getOrDefault("workspaceID", EMPTY_LONG).getValueAsLong());
        tempData.setWorkspaceName(inProperties.getOrDefault("workspaceName", EMPTY_STRING).getValueAsString());
        tempData.setDatabaseVersion(inProperties.getOrDefault("databaseVersion", EMPTY_INT).getValueAsInteger());
        tempData.setDefaultLatitude(inProperties.getOrDefault("defaultLatitude", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setDefaultLongitude(inProperties.getOrDefault("defaultLongitude", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setDefaultZoom(inProperties.getOrDefault("defaultZoom", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setDefaultSlideshowSpeed((float) inProperties.getOrDefault("defaultSlideshowSpeed", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setDefaultSlideshowSize(inProperties.getOrDefault("defaultSlideshowSize", EMPTY_INT).getValueAsInteger());
        tempData.setBundledPlayers(inProperties.getOrDefault("bundledPlayers", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setEnableSounds(inProperties.getOrDefault("enableSounds", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setUploadLogs(inProperties.getOrDefault("uploadLogs", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setUseIndividualsInSightingPath(inProperties.getOrDefault("useIndividualsInSightingPath", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setUseScientificNames(inProperties.getOrDefault("useScientificNames", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setUseThumbnailTables(inProperties.getOrDefault("useThumbnailTables", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setUseThumnailBrowsing(inProperties.getOrDefault("useThumnailBrowsing", EMPTY_BOOLEAN).getValueAsBoolean());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    private ExtraData readExtraDataV15(HashMap<String, EntityProperty> inProperties) {
        ExtraData tempData = new ExtraData();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setFieldType(WildLogExtraDataFieldTypes.getEnumFromText(inProperties.getOrDefault("fieldType", EMPTY_STRING).getValueAsString()));
        tempData.setLinkID(inProperties.getOrDefault("linkID", EMPTY_LONG).getValueAsLong());
        tempData.setLinkType(WildLogDataType.getEnumFromText(inProperties.getOrDefault("linkType", EMPTY_STRING).getValueAsString()));
        tempData.setDataKey(inProperties.getOrDefault("dataKey", EMPTY_STRING).getValueAsString());
        tempData.setDataValue(inProperties.getOrDefault("dataValue", EMPTY_STRING).getValueAsString());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        tempData.setSyncIndicator(inProperties.getOrDefault("SyncIndicator", EMPTY_LONG).getValueAsLong());
        return tempData;
    }
    
    // WRITE PROPERTIES:
    
    @Override
    public HashMap<String, EntityProperty> writeEntity(OperationContext opContext) 
            throws StorageException {
        // First write the fields from this class
        HashMap<String, EntityProperty> properties = super.writeEntity(opContext);
        // Next write the fields from the data object
        if (data != null) {
            if (dataType.equals(WildLogDataType.ELEMENT.getKey())) {
                if (dbVersion >= 14) {
                    writeElementV14((ElementCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.LOCATION.getKey())) {
                if (dbVersion >= 14) {
                    writeLocationV14((LocationCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.VISIT.getKey())) {
                if (dbVersion >= 14) {
                    writeVisitV14((VisitCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.SIGHTING.getKey())) {
                if (dbVersion >= 14) {
                    writeSightingV14((SightingCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.FILE.getKey())) {
                if (dbVersion >= 14) {
                    writeFileV14((WildLogFileCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.WILDLOG_USER.getKey())) {
                if (dbVersion >= 14) {
                    writeUserV14((WildLogUser) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.DELETE_LOG.getKey())) {
                if (dbVersion >= 14) {
                    writeDeleteLogV14((WildLogDeleteLog) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.WILDLOG_OPTIONS.getKey())) {
                if (dbVersion >= 14) {
                    writeOptionsV14((WildLogOptions) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.EXTRA.getKey())) {
                if (dbVersion >= 15) {
                    writeExtraDataV15((ExtraData) data, properties);
                }
            }
        }
        return properties;
    }
    
    private void writeElementV14(ElementCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("primaryName", new EntityProperty(inData.getPrimaryName()));
        inProperties.put("otherName", new EntityProperty(inData.getOtherName()));
        inProperties.put("scientificName", new EntityProperty(inData.getScientificName()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("distribution", new EntityProperty(inData.getDistribution()));
        inProperties.put("nutrition", new EntityProperty(inData.getNutrition()));
        inProperties.put("diagnosticDescription", new EntityProperty(inData.getDiagnosticDescription()));
        inProperties.put("endangeredStatus", new EntityProperty(UtilsData.getIDFromEnum(inData.getEndangeredStatus())));
        inProperties.put("behaviourDescription", new EntityProperty(inData.getBehaviourDescription()));
        inProperties.put("type", new EntityProperty(UtilsData.getIDFromEnum(inData.getType())));
        inProperties.put("feedingClass", new EntityProperty(UtilsData.getIDFromEnum(inData.getFeedingClass())));
        inProperties.put("referenceID", new EntityProperty(inData.getReferenceID()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeLocationV14(LocationCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("name", new EntityProperty(inData.getName()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("rating", new EntityProperty(UtilsData.getIDFromEnum(inData.getRating())));
        inProperties.put("gameViewingRating", new EntityProperty(UtilsData.getIDFromEnum(inData.getGameViewingRating())));
        inProperties.put("habitatType", new EntityProperty(inData.getHabitatType()));
        inProperties.put("latitude", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLatitude())));
        inProperties.put("latDegrees", new EntityProperty(inData.getLatDegrees()));
        inProperties.put("latMinutes", new EntityProperty(inData.getLatMinutes()));
        inProperties.put("latSeconds", new EntityProperty(inData.getLatSeconds()));
        inProperties.put("longitude", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLongitude())));
        inProperties.put("lonDegrees", new EntityProperty(inData.getLonDegrees()));
        inProperties.put("lonMinutes", new EntityProperty(inData.getLonMinutes()));
        inProperties.put("lonSeconds", new EntityProperty(inData.getLonSeconds()));
        inProperties.put("gpsAccuracy", new EntityProperty(UtilsData.getIDFromEnum(inData.getGPSAccuracy())));
        inProperties.put("gpsAccuracyValue", new EntityProperty(inData.getGPSAccuracyValue()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeVisitV14(VisitCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("name", new EntityProperty(inData.getName()));
        inProperties.put("startDate", new EntityProperty(inData.getStartDate()));
        inProperties.put("endDate", new EntityProperty(inData.getEndDate()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("gameWatchingIntensity", new EntityProperty(UtilsData.getIDFromEnum(inData.getGameWatchingIntensity())));
        inProperties.put("type", new EntityProperty(UtilsData.getIDFromEnum(inData.getType())));
        inProperties.put("locationID", new EntityProperty(inData.getLocationID()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeSightingV14(SightingCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("date", new EntityProperty(inData.getDate()));
        inProperties.put("elementID", new EntityProperty(inData.getElementID()));
        inProperties.put("locationID", new EntityProperty(inData.getLocationID()));
        inProperties.put("visitID", new EntityProperty(inData.getVisitID()));
        inProperties.put("timeOfDay", new EntityProperty(UtilsData.getIDFromEnum(inData.getTimeOfDay())));
        inProperties.put("weather", new EntityProperty(UtilsData.getIDFromEnum(inData.getWeather())));
        inProperties.put("viewRating", new EntityProperty(UtilsData.getIDFromEnum(inData.getViewRating())));
        inProperties.put("certainty", new EntityProperty(UtilsData.getIDFromEnum(inData.getCertainty())));
        inProperties.put("numberOfElements", new EntityProperty(inData.getNumberOfElements()));
        inProperties.put("details", new EntityProperty(inData.getDetails()));
        inProperties.put("latitude", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLatitude())));
        inProperties.put("latDegrees", new EntityProperty(inData.getLatDegrees()));
        inProperties.put("latMinutes", new EntityProperty(inData.getLatMinutes()));
        inProperties.put("latSeconds", new EntityProperty(inData.getLatSeconds()));
        inProperties.put("longitude", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLongitude())));
        inProperties.put("lonDegrees", new EntityProperty(inData.getLonDegrees()));
        inProperties.put("lonMinutes", new EntityProperty(inData.getLonMinutes()));
        inProperties.put("lonSeconds", new EntityProperty(inData.getLonSeconds()));
        inProperties.put("sightingEvidence", new EntityProperty(UtilsData.getIDFromEnum(inData.getSightingEvidence())));
        inProperties.put("moonlight", new EntityProperty(UtilsData.getIDFromEnum(inData.getMoonlight())));
        inProperties.put("moonPhase", new EntityProperty(inData.getMoonPhase()));
        inProperties.put("temperature", new EntityProperty(inData.getTemperature()));
        inProperties.put("unitsTemperature", new EntityProperty(UtilsData.getIDFromEnum(inData.getUnitsTemperature())));
        inProperties.put("lifeStatus", new EntityProperty(UtilsData.getIDFromEnum(inData.getLifeStatus())));
        inProperties.put("sex", new EntityProperty(UtilsData.getIDFromEnum(inData.getSex())));
        inProperties.put("tag", new EntityProperty(inData.getTag()));
        inProperties.put("durationMinutes", new EntityProperty(inData.getDurationMinutes()));
        inProperties.put("durationSeconds", new EntityProperty(inData.getDurationSeconds()));
        inProperties.put("gpsAccuracy", new EntityProperty(UtilsData.getIDFromEnum(inData.getGPSAccuracy())));
        inProperties.put("gpsAccuracyValue", new EntityProperty(inData.getGPSAccuracyValue()));
        inProperties.put("timeAccuracy", new EntityProperty(UtilsData.getIDFromEnum(inData.getTimeAccuracy())));
        inProperties.put("age", new EntityProperty(UtilsData.getIDFromEnum(inData.getAge())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeFileV14(WildLogFileCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("linkID", new EntityProperty(inData.getLinkID()));
        inProperties.put("linkType", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLinkType())));
        inProperties.put("filename", new EntityProperty(inData.getFilename()));
        inProperties.put("originalFileLocation", new EntityProperty(inData.getDBFilePath()));
        inProperties.put("uploadDate", new EntityProperty(inData.getUploadDate()));
        inProperties.put("fileType", new EntityProperty(UtilsData.getKeyFromEnum(inData.getFileType())));
        inProperties.put("defaultFile", new EntityProperty(inData.isDefaultFile()));
        inProperties.put("fileDate", new EntityProperty(inData.getFileDate()));
        inProperties.put("fileSize", new EntityProperty(inData.getFileSize()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeUserV14(WildLogUser inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("username", new EntityProperty(inData.getUsername()));
        inProperties.put("password", new EntityProperty(inData.getPassword()));
        inProperties.put("type", new EntityProperty(UtilsData.getKeyFromEnum(inData.getType())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeDeleteLogV14(WildLogDeleteLog inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("type", new EntityProperty(UtilsData.getKeyFromEnum(inData.getType())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeOptionsV14(WildLogOptions inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("workspaceID", new EntityProperty(inData.getWorkspaceID()));
        inProperties.put("workspaceName", new EntityProperty(inData.getWorkspaceName()));
        inProperties.put("databaseVersion", new EntityProperty(inData.getDatabaseVersion()));
        inProperties.put("defaultLatitude", new EntityProperty(inData.getDefaultLatitude()));
        inProperties.put("defaultLongitude", new EntityProperty(inData.getDefaultLongitude()));
        inProperties.put("defaultZoom", new EntityProperty(inData.getDefaultZoom()));
        inProperties.put("defaultSlideshowSpeed", new EntityProperty(inData.getDefaultSlideshowSpeed()));
        inProperties.put("defaultSlideshowSize", new EntityProperty(inData.getDefaultSlideshowSize()));
        inProperties.put("bundledPlayers", new EntityProperty(inData.isBundledPlayers()));
        inProperties.put("enableSounds", new EntityProperty(inData.isEnableSounds()));
        inProperties.put("uploadLogs", new EntityProperty(inData.isUploadLogs()));
        inProperties.put("useIndividualsInSightingPath", new EntityProperty(inData.isUseIndividualsInSightingPath()));
        inProperties.put("useScientificNames", new EntityProperty(inData.isUseScientificNames()));
        inProperties.put("useThumbnailTables", new EntityProperty(inData.isUseThumbnailTables()));
        inProperties.put("useThumnailBrowsing", new EntityProperty(inData.isUseThumnailBrowsing()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }
    
    private void writeExtraDataV15(ExtraData inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("fieldType", new EntityProperty(UtilsData.getKeyFromEnum(inData.getFieldType())));
        inProperties.put("linkID", new EntityProperty(inData.getLinkID()));
        inProperties.put("linkType", new EntityProperty(UtilsData.getKeyFromEnum(inData.getLinkType())));
        inProperties.put("dataKey", new EntityProperty(inData.getDataKey()));
        inProperties.put("dataValue", new EntityProperty(inData.getDataValue()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
        inProperties.put("SyncIndicator", new EntityProperty(inData.getSyncIndicator()));
    }

}
