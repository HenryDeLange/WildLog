package wildlog.data.dbi;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.AdhocData;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.INaturalistLinkedData;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogDeleteLog;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dbi.queryobjects.ElementCount;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.data.enums.system.WildLogUserTypes;


public interface DBI {

    public boolean initialize(boolean inCreateDefaultRecords) throws SQLException;
    public void close();

    public int countElements(String inPrimaryName, String inScientificName);
    public int countLocations(String inName);
    public int countVisits(String inName, long inLocationID);
    public int countSightings(long inID, long inElementID, long inLocationID, long inVisitID);
    public int countWildLogFiles(long inID, long inLinkID);
    public int countUsers();
    public int countDeleteLogs(long inID, WildLogDataType inDataType);

    public <T extends ElementCore> T findElement(long inID, String inPrimaryName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends LocationCore> T findLocation(long inID, String inName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends VisitCore> T findVisit(long inID, String inName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends SightingCore> T findSighting(long inID, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends WildLogFileCore> T findWildLogFile(long inID, long inLinkID, WildLogFileType inWildLogFileType, String inDBFilePath, Class<T> inReturnType);
    public <T extends WildLogOptions> T findWildLogOptions(Class<T> inReturnType);
    public <T extends AdhocData> T findAdhocData(String inFieldID, String inDataKey, Class<T> inReturnType);
    public <T extends INaturalistLinkedData> T findINaturalistLinkedData(long inWildLogID, long inINaturalistID, Class<T> inReturnType);
    public <T extends WildLogUser> T findUser(long inID, String inUsername, Class<T> inReturnType);
    public <T extends WildLogDeleteLog> T findDeleteLog(long inID, Class<T> inReturnType);
    public <T extends ExtraData> T findExtraData(long inID, WildLogExtraDataFieldTypes inFieldType, long inLinkID, String inDataKey, Class<T> inReturnType);

    public <T extends ElementCore> List<T> listElements(String inPrimaryName, String inScientificName, ElementType inElementType, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends LocationCore> List<T> listLocations(String inName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends VisitCore> List<T> listVisits(String inName, long inLocationID, VisitType inVisitType, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends SightingCore> List<T> listSightings(long inElementID, long inLocationID, long inVisitID, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends WildLogFileCore> List<T> listWildLogFiles(long inLinkID, WildLogFileType inWildLogFileType, Class<T> inReturnType);
    public <T extends AdhocData> List<T> listAdhocDatas(String inFieldID, Class<T> inReturnType);
    public <T extends INaturalistLinkedData> List<T> listINaturalistLinkedDatas(Class<T> inReturnType);
    public <T extends WildLogUser> List<T> listUsers(WildLogUserTypes inType, Class<T> inReturnType);
    public <T extends WildLogDeleteLog> List<T> listDeleteLogs(WildLogDataType inDataType, long inAfterAuditTime, Class<T> inReturnType);
    public <T extends ExtraData> List<T> listExtraDatas(WildLogExtraDataFieldTypes inFieldType, long inLinkID, Class<T> inReturnType);

    public <T extends ElementCore> boolean createElement(T inElement, boolean inNewButUseOldAuditAndID);
    public <T extends LocationCore> boolean createLocation(T inLocation, boolean inNewButUseOldAuditAndID);
    public <T extends VisitCore> boolean createVisit(T inVisit, boolean inNewButUseOldAuditAndID);
    public <T extends SightingCore> boolean createSighting(T inSighting, boolean inNewButUseOldAuditAndID);
    public <T extends WildLogFileCore> boolean createWildLogFile(T inWildLogFile, boolean inNewButUseOldAuditAndID);
    public <T extends WildLogOptions> boolean createWildLogOptions();
    public <T extends AdhocData> boolean createAdhocData(T inAdhocData);
    public <T extends INaturalistLinkedData> boolean createINaturalistLinkedData(T inINaturalistLinkedData);
    public <T extends WildLogUser> boolean createUser(T inWildLogUser, boolean inNewButUseOldAuditAndID);
    public <T extends WildLogDeleteLog> boolean createDeleteLog(T inWildLogDeleteLog, boolean inNewButUseOldAudit);
    public <T extends ExtraData> boolean createExtraData(T inExtraData, boolean inNewButUseOldAudit);
    
    public <T extends ElementCore> boolean updateElement(T inElement, String inOldName, boolean inUseOldAudit);
    public <T extends LocationCore> boolean updateLocation(T inLocation, String inOldName, boolean inUseOldAudit);
    public <T extends VisitCore> boolean updateVisit(T inVisit, String inOldName, boolean inUseOldAudit);
    public <T extends SightingCore> boolean updateSighting(T inSighting, boolean inUseOldAudit);
    public <T extends WildLogFileCore> boolean updateWildLogFile(T inWildLogFile, boolean inUseOldAudit);
    public <T extends WildLogOptions> boolean updateWildLogOptions(T inWildLogOptions, boolean inUseOldAudit);
    public <T extends AdhocData> boolean updateAdhocData(T inAdhocData);
    public <T extends INaturalistLinkedData> boolean updateINaturalistLinkedData(T inINaturalistLinkedData);
    public <T extends WildLogUser> boolean updateUser(T inWildLogUser, boolean inUseOldAudit);
    public <T extends ExtraData> boolean updateExtraData(T inExtraData, boolean inUseOldAudit);

    public boolean deleteElement(long inID);
    public boolean deleteLocation(long inID);
    public boolean deleteVisit(long inID);
    public boolean deleteSighting(long inID);
    public boolean deleteWildLogFile(long inID);
    public boolean deleteAdhocData(String inFieldID, String inDataKey);
    public boolean deleteINaturalistLinkedData(long inWildLogID, long inINaturalistID);
    public boolean deleteUser(long inID);
    public boolean deleteExtraData(long inID);

    public <S extends SightingCore> List<S> searchSightings(List<Long> inActiveSightingIDs, Date inStartDate, Date inEndDate, 
            List<Long> inActiveLocations, List<Long> inActiveVisits, List<Long> inActiveElements, 
            boolean inIncludeCachedValues, Class<S> inReturnType);
    
    public <T extends LocationCount> List<T> queryLocationCountForElement(long inElementID, Class<T> inReturnType);
    public <T extends ElementCount> List<T> queryElementCountForLocation(long inLocationID, Class<T> inReturnType);
    public <T extends ElementCount> List<T> queryElementCountForVisit(long inVisitID, Class<T> inReturnType);
    public List<String> queryExtraDataUniqueDataKeys(WildLogExtraDataFieldTypes inFieldType, WildLogDataType inLinkType);

    public long generateID();
    
    public int activeSessionsCount();
    
}
