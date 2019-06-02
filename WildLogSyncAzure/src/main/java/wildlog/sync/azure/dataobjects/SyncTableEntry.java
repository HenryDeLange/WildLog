package wildlog.sync.azure.dataobjects;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.Ignore;
import com.microsoft.azure.storage.table.TableServiceEntity;
import java.util.Date;
import java.util.HashMap;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogDeleteLog;
import wildlog.data.dataobjects.WildLogFileCore;
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
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.data.utils.UtilsData;


public class SyncTableEntry extends TableServiceEntity {
    private static final EntityProperty EMPTY_STRING = new EntityProperty((String) null);
    private static final EntityProperty EMPTY_DATE = new EntityProperty((Date) null);
    private static final EntityProperty EMPTY_INT = new EntityProperty(0);
    private static final EntityProperty EMPTY_LONG = new EntityProperty(0L);
    private static final EntityProperty EMPTY_DOUBLE = new EntityProperty(0.0);
    private static final EntityProperty EMPTY_BOOLEAN = new EntityProperty(false);
    private String dataType;
    private long syncTime;
    private int dbVersion;
    private DataObjectWithAudit data;

    
    public SyncTableEntry() {
        super();
    }

    public SyncTableEntry(WildLogDataType inDataType, long inWorkspaceID, long inRecordID, long inSyncTime, int inDBVersion, DataObjectWithAudit inData) {
        super();
        if (inDataType != null) {
            dataType = inDataType.toString();
        }
        syncTime = inSyncTime;
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
    public DataObjectWithAudit getData() {
        return data;
    }

    @Ignore
    public void setData(DataObjectWithAudit inData) {
        data = inData;
    }

    @Override
    public String toString() {
        return "[" + dataType + "] (" + dbVersion + ") " + partitionKey + "/" + rowKey + " <" + syncTime + "> : " + data;
    }

    // READ PROPERTIES:
    
    @Override
    public void readEntity(HashMap<String, EntityProperty> inProperties, OperationContext inOpContext)
            throws StorageException {
        // First read the fields for this class
        super.readEntity(inProperties, inOpContext);
        // Next read the fields for the data object
        if (dataType.equals(WildLogDataType.ELEMENT.getKey())) {
            if (dbVersion == 12) {
                data = readElementV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.LOCATION.getKey())) {
            if (dbVersion == 12) {
                data = readLocationV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.VISIT.getKey())) {
            if (dbVersion == 12) {
                data = readVisitV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.SIGHTING.getKey())) {
            if (dbVersion == 12) {
                data = readSightingV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.FILE.getKey())) {
            if (dbVersion == 12) {
                data = readFileV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.WILDLOG_USER.getKey())) {
            if (dbVersion == 12) {
                data = readUserV12(inProperties);
            }
        }
        else
        if (dataType.equals(WildLogDataType.DELETE_LOG.getKey())) {
            if (dbVersion == 12) {
                data = readDeleteLogV12(inProperties);
            }
        }
        else {
            data = null;
        }
    }
    
    // READ PROPERTIES - V12:
    
    private ElementCore readElementV12(HashMap<String, EntityProperty> inProperties) {
        ElementCore tempData = new ElementCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setPrimaryName(inProperties.getOrDefault("primaryName", EMPTY_STRING).getValueAsString());
        tempData.setOtherName(inProperties.getOrDefault("otherName", EMPTY_STRING).getValueAsString());
        tempData.setScientificName(inProperties.getOrDefault("scientificName", EMPTY_STRING).getValueAsString());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setDistribution(inProperties.getOrDefault("distribution", EMPTY_STRING).getValueAsString());
        tempData.setNutrition(inProperties.getOrDefault("nutrition", EMPTY_STRING).getValueAsString());
        tempData.setDiagnosticDescription(inProperties.getOrDefault("diagnosticDescription", EMPTY_STRING).getValueAsString());
        tempData.setEndangeredStatus(EndangeredStatus.getEnumFromText(inProperties.getOrDefault("endangeredStatus", EMPTY_STRING).getValueAsString()));
        tempData.setBehaviourDescription(inProperties.getOrDefault("behaviourDescription", EMPTY_STRING).getValueAsString());
        tempData.setType(ElementType.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setFeedingClass(FeedingClass.getEnumFromText(inProperties.getOrDefault("feedingClass", EMPTY_STRING).getValueAsString()));
        tempData.setReferenceID(inProperties.getOrDefault("referenceID", EMPTY_STRING).getValueAsString());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        return tempData;
    }
    
    private LocationCore readLocationV12(HashMap<String, EntityProperty> inProperties) {
        LocationCore tempData = new LocationCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setName(inProperties.getOrDefault("name", EMPTY_STRING).getValueAsString());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setRating(LocationRating.getEnumFromText(inProperties.getOrDefault("rating", EMPTY_STRING).getValueAsString()));
        tempData.setGameViewingRating(GameViewRating.getEnumFromText(inProperties.getOrDefault("gameViewingRating", EMPTY_STRING).getValueAsString()));
        tempData.setHabitatType(inProperties.getOrDefault("habitatType", EMPTY_STRING).getValueAsString());
        tempData.setLatitude(Latitudes.getEnumFromText(inProperties.getOrDefault("latitude", EMPTY_STRING).getValueAsString()));
        tempData.setLatDegrees(inProperties.getOrDefault("latDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLatMinutes(inProperties.getOrDefault("latMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLatSeconds(inProperties.getOrDefault("latSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setLongitude(Longitudes.getEnumFromText(inProperties.getOrDefault("longitude", EMPTY_STRING).getValueAsString()));
        tempData.setLonDegrees(inProperties.getOrDefault("lonDegrees", EMPTY_INT).getValueAsInteger());
        tempData.setLonMinutes(inProperties.getOrDefault("lonMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setLonSeconds(inProperties.getOrDefault("lonSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setGPSAccuracy(GPSAccuracy.getEnumFromText(inProperties.getOrDefault("gpsAccuracy", EMPTY_STRING).getValueAsString()));
        tempData.setGPSAccuracyValue(inProperties.getOrDefault("gpsAccuracyValue", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        return tempData;
    }
    
    private VisitCore readVisitV12(HashMap<String, EntityProperty> inProperties) {
        VisitCore tempData = new VisitCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setName(inProperties.getOrDefault("name", EMPTY_STRING).getValueAsString());
        tempData.setStartDate(inProperties.getOrDefault("startDate", EMPTY_DATE).getValueAsDate());
        tempData.setEndDate(inProperties.getOrDefault("endDate", EMPTY_DATE).getValueAsDate());
        tempData.setDescription(inProperties.getOrDefault("description", EMPTY_STRING).getValueAsString());
        tempData.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(inProperties.getOrDefault("gameWatchingIntensity", EMPTY_STRING).getValueAsString()));
        tempData.setType(VisitType.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setLocationID(inProperties.getOrDefault("locationID", EMPTY_LONG).getValueAsLong());
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        return tempData;
    }
    
    private SightingCore readSightingV12(HashMap<String, EntityProperty> inProperties) {
        SightingCore tempData = new SightingCore();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setDate(inProperties.getOrDefault("date", EMPTY_DATE).getValueAsDate());
        tempData.setElementID(inProperties.getOrDefault("elementID", EMPTY_LONG).getValueAsLong());
        tempData.setLocationID(inProperties.getOrDefault("locationID", EMPTY_LONG).getValueAsLong());
        tempData.setVisitID(inProperties.getOrDefault("visitID", EMPTY_LONG).getValueAsLong());
        tempData.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(inProperties.getOrDefault("timeOfDay", EMPTY_STRING).getValueAsString()));
        tempData.setWeather(Weather.getEnumFromText(inProperties.getOrDefault("weather", EMPTY_STRING).getValueAsString()));
        tempData.setViewRating(ViewRating.getEnumFromText(inProperties.getOrDefault("viewRating", EMPTY_STRING).getValueAsString()));
        tempData.setCertainty(Certainty.getEnumFromText(inProperties.getOrDefault("certainty", EMPTY_STRING).getValueAsString()));
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
        tempData.setSightingEvidence(SightingEvidence.getEnumFromText(inProperties.getOrDefault("sightingEvidence", EMPTY_STRING).getValueAsString()));
        tempData.setMoonlight(Moonlight.getEnumFromText(inProperties.getOrDefault("moonlight", EMPTY_STRING).getValueAsString()));
        tempData.setMoonPhase(inProperties.getOrDefault("moonPhase", EMPTY_INT).getValueAsInteger());
        tempData.setTemperature(inProperties.getOrDefault("temperature", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setUnitsTemperature(UnitsTemperature.getEnumFromText(inProperties.getOrDefault("unitsTemperature", EMPTY_STRING).getValueAsString()));
        tempData.setLifeStatus(LifeStatus.getEnumFromText(inProperties.getOrDefault("lifeStatus", EMPTY_STRING).getValueAsString()));
        tempData.setSex(Sex.getEnumFromText(inProperties.getOrDefault("sex", EMPTY_STRING).getValueAsString()));
        tempData.setTag(inProperties.getOrDefault("tag", EMPTY_STRING).getValueAsString());
        tempData.setDurationMinutes(inProperties.getOrDefault("durationMinutes", EMPTY_INT).getValueAsInteger());
        tempData.setDurationSeconds(inProperties.getOrDefault("durationSeconds", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setGPSAccuracy(GPSAccuracy.getEnumFromText(inProperties.getOrDefault("gpsAccuracy", EMPTY_STRING).getValueAsString()));
        tempData.setGPSAccuracyValue(inProperties.getOrDefault("gpsAccuracyValue", EMPTY_DOUBLE).getValueAsDouble());
        tempData.setTimeAccuracy(TimeAccuracy.getEnumFromText(inProperties.getOrDefault("timeAccuracy", EMPTY_STRING).getValueAsString()));
        tempData.setAge(Age.getEnumFromText(inProperties.getOrDefault("age", EMPTY_STRING).getValueAsString()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        return tempData;
    }
    
    private WildLogFileCore readFileV12(HashMap<String, EntityProperty> inProperties) {
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
        return tempData;
    }
    
    private WildLogUser readUserV12(HashMap<String, EntityProperty> inProperties) {
        WildLogUser tempData = new WildLogUser();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setUsername(inProperties.getOrDefault("username", EMPTY_STRING).getValueAsString());
        tempData.setPassword(inProperties.getOrDefault("password", EMPTY_STRING).getValueAsString());
        tempData.setType(WildLogUserTypes.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
        return tempData;
    }
    
    private WildLogDeleteLog readDeleteLogV12(HashMap<String, EntityProperty> inProperties) {
        WildLogDeleteLog tempData = new WildLogDeleteLog();
        tempData.setID(Long.parseLong(rowKey));
        tempData.setType(WildLogDataType.getEnumFromText(inProperties.getOrDefault("type", EMPTY_STRING).getValueAsString()));
        tempData.setAuditTime(inProperties.getOrDefault("AuditTime", EMPTY_LONG).getValueAsLong());
        tempData.setAuditUser(inProperties.getOrDefault("AuditUser", EMPTY_STRING).getValueAsString());
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
                if (dbVersion == 12) {
                    writeElementV12((ElementCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.LOCATION.getKey())) {
                if (dbVersion == 12) {
                    writeLocationV12((LocationCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.VISIT.getKey())) {
                if (dbVersion == 12) {
                    writeVisitV12((VisitCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.SIGHTING.getKey())) {
                if (dbVersion == 12) {
                    writeSightingV12((SightingCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.FILE.getKey())) {
                if (dbVersion == 12) {
                    writeFileV12((WildLogFileCore) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.WILDLOG_USER.getKey())) {
                if (dbVersion == 12) {
                    writeUserV12((WildLogUser) data, properties);
                }
            }
            else
            if (dataType.equals(WildLogDataType.DELETE_LOG.getKey())) {
                if (dbVersion == 12) {
                    writeDeleteLogV12((WildLogDeleteLog) data, properties);
                }
            }
        }
        return properties;
    }
    
    // WRITE PROPERTIES - V12:
    
    private void writeElementV12(ElementCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("primaryName", new EntityProperty(inData.getPrimaryName()));
        inProperties.put("otherName", new EntityProperty(inData.getOtherName()));
        inProperties.put("scientificName", new EntityProperty(inData.getScientificName()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("distribution", new EntityProperty(inData.getDistribution()));
        inProperties.put("nutrition", new EntityProperty(inData.getNutrition()));
        inProperties.put("diagnosticDescription", new EntityProperty(inData.getDiagnosticDescription()));
        inProperties.put("endangeredStatus", new EntityProperty(UtilsData.stringFromObject(inData.getEndangeredStatus())));
        inProperties.put("behaviourDescription", new EntityProperty(inData.getBehaviourDescription()));
        inProperties.put("type", new EntityProperty(UtilsData.stringFromObject(inData.getType())));
        inProperties.put("feedingClass", new EntityProperty(UtilsData.stringFromObject(inData.getFeedingClass())));
        inProperties.put("referenceID", new EntityProperty(inData.getReferenceID()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeLocationV12(LocationCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("name", new EntityProperty(inData.getName()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("rating", new EntityProperty(UtilsData.stringFromObject(inData.getRating())));
        inProperties.put("gameViewingRating", new EntityProperty(UtilsData.stringFromObject(inData.getGameViewingRating())));
        inProperties.put("habitatType", new EntityProperty(inData.getHabitatType()));
        inProperties.put("latitude", new EntityProperty(UtilsData.stringFromObject(inData.getLatitude())));
        inProperties.put("latDegrees", new EntityProperty(inData.getLatDegrees()));
        inProperties.put("latMinutes", new EntityProperty(inData.getLatMinutes()));
        inProperties.put("latSeconds", new EntityProperty(inData.getLatSeconds()));
        inProperties.put("longitude", new EntityProperty(UtilsData.stringFromObject(inData.getLongitude())));
        inProperties.put("lonDegrees", new EntityProperty(inData.getLonDegrees()));
        inProperties.put("lonMinutes", new EntityProperty(inData.getLonMinutes()));
        inProperties.put("lonSeconds", new EntityProperty(inData.getLonSeconds()));
        inProperties.put("gpsAccuracy", new EntityProperty(UtilsData.stringFromObject(inData.getGPSAccuracy())));
        inProperties.put("gpsAccuracyValue", new EntityProperty(inData.getGPSAccuracyValue()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeVisitV12(VisitCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("name", new EntityProperty(inData.getName()));
        inProperties.put("startDate", new EntityProperty(inData.getStartDate()));
        inProperties.put("endDate", new EntityProperty(inData.getEndDate()));
        inProperties.put("description", new EntityProperty(inData.getDescription()));
        inProperties.put("gameWatchingIntensity", new EntityProperty(UtilsData.stringFromObject(inData.getGameWatchingIntensity())));
        inProperties.put("type", new EntityProperty(UtilsData.stringFromObject(inData.getType())));
        inProperties.put("locationID", new EntityProperty(inData.getLocationID()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeSightingV12(SightingCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("date", new EntityProperty(inData.getDate()));
        inProperties.put("elementID", new EntityProperty(inData.getElementID()));
        inProperties.put("locationID", new EntityProperty(inData.getLocationID()));
        inProperties.put("visitID", new EntityProperty(inData.getVisitID()));
        inProperties.put("timeOfDay", new EntityProperty(UtilsData.stringFromObject(inData.getTimeOfDay())));
        inProperties.put("weather", new EntityProperty(UtilsData.stringFromObject(inData.getWeather())));
        inProperties.put("viewRating", new EntityProperty(UtilsData.stringFromObject(inData.getViewRating())));
        inProperties.put("certainty", new EntityProperty(UtilsData.stringFromObject(inData.getCertainty())));
        inProperties.put("numberOfElements", new EntityProperty(inData.getNumberOfElements()));
        inProperties.put("details", new EntityProperty(inData.getDetails()));
        inProperties.put("latitude", new EntityProperty(UtilsData.stringFromObject(inData.getLatitude())));
        inProperties.put("latDegrees", new EntityProperty(inData.getLatDegrees()));
        inProperties.put("latMinutes", new EntityProperty(inData.getLatMinutes()));
        inProperties.put("latSeconds", new EntityProperty(inData.getLatSeconds()));
        inProperties.put("longitude", new EntityProperty(UtilsData.stringFromObject(inData.getLongitude())));
        inProperties.put("lonDegrees", new EntityProperty(inData.getLonDegrees()));
        inProperties.put("lonMinutes", new EntityProperty(inData.getLonMinutes()));
        inProperties.put("lonSeconds", new EntityProperty(inData.getLonSeconds()));
        inProperties.put("sightingEvidence", new EntityProperty(UtilsData.stringFromObject(inData.getSightingEvidence())));
        inProperties.put("moonlight", new EntityProperty(UtilsData.stringFromObject(inData.getMoonlight())));
        inProperties.put("moonPhase", new EntityProperty(inData.getMoonPhase()));
        inProperties.put("temperature", new EntityProperty(inData.getTemperature()));
        inProperties.put("unitsTemperature", new EntityProperty(UtilsData.stringFromObject(inData.getUnitsTemperature())));
        inProperties.put("lifeStatus", new EntityProperty(UtilsData.stringFromObject(inData.getLifeStatus())));
        inProperties.put("sex", new EntityProperty(UtilsData.stringFromObject(inData.getSex())));
        inProperties.put("tag", new EntityProperty(inData.getTag()));
        inProperties.put("durationMinutes", new EntityProperty(inData.getDurationMinutes()));
        inProperties.put("durationSeconds", new EntityProperty(inData.getDurationSeconds()));
        inProperties.put("gpsAccuracy", new EntityProperty(UtilsData.stringFromObject(inData.getGPSAccuracy())));
        inProperties.put("gpsAccuracyValue", new EntityProperty(inData.getGPSAccuracyValue()));
        inProperties.put("timeAccuracy", new EntityProperty(UtilsData.stringFromObject(inData.getTimeAccuracy())));
        inProperties.put("age", new EntityProperty(UtilsData.stringFromObject(inData.getAge())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeFileV12(WildLogFileCore inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("linkID", new EntityProperty(inData.getLinkID()));
        inProperties.put("linkType", new EntityProperty(UtilsData.stringFromObject(inData.getLinkType())));
        inProperties.put("filename", new EntityProperty(inData.getFilename()));
        inProperties.put("originalFileLocation", new EntityProperty(inData.getDBFilePath()));
        inProperties.put("uploadDate", new EntityProperty(inData.getUploadDate()));
        inProperties.put("fileType", new EntityProperty(UtilsData.stringFromObject(inData.getFileType())));
        inProperties.put("defaultFile", new EntityProperty(inData.isDefaultFile()));
        inProperties.put("fileDate", new EntityProperty(inData.getFileDate()));
        inProperties.put("fileSize", new EntityProperty(inData.getFileSize()));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeUserV12(WildLogUser inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("username", new EntityProperty(inData.getUsername()));
        inProperties.put("password", new EntityProperty(inData.getPassword()));
        inProperties.put("type", new EntityProperty(UtilsData.stringFromObject(inData.getType())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }
    
    private void writeDeleteLogV12(WildLogDeleteLog inData, HashMap<String, EntityProperty> inProperties) {
        inProperties.put("type", new EntityProperty(UtilsData.stringFromObject(inData.getType())));
        inProperties.put("AuditTime", new EntityProperty(inData.getAuditTime()));
        inProperties.put("AuditUser", new EntityProperty(inData.getAuditUser()));
    }

}
