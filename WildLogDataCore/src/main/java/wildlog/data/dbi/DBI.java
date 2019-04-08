package wildlog.data.dbi;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.AdhocData;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.INaturalistLinkedData;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogUserTypes;


public interface DBI {

    public boolean initialize(boolean inCreateDefaultRecords) throws SQLException;
    public void close();

    public int countElements(String inPrimaryName, String inScientificName);
    public int countLocations(String inName);
    public int countVisits(String inName, long inLocationID);
    public int countSightings(long inID, long inElementID, long inLocationID, long inVisitID);
    public int countWildLogFiles(String inDBFilePath, String inWildLogFileID);
    public int countUsers();

    public <T extends ElementCore> T findElement(long inID, String inPrimaryName, Class<T> inReturnType);
    public <T extends LocationCore> T findLocation(long inID, String inName, Class<T> inReturnType);
    public <T extends VisitCore> T findVisit(long inID, String inName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends SightingCore> T findSighting(long inID, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends WildLogFileCore> T findWildLogFile(String inDBFilePath, String inWildLogFileID, WildLogFileType inWildLogFileType, Class<T> inReturnType);
    public <T extends WildLogOptions> T findWildLogOptions(Class<T> inReturnType);
    public <T extends AdhocData> T findAdhocData(String inFieldID, String inDataKey, Class<T> inReturnType);
    public <T extends INaturalistLinkedData> T findINaturalistLinkedData(long inWildLogID, long inINaturalistID, Class<T> inReturnType);
    public <T extends WildLogUser> T findUser(String inUsername, Class<T> inReturnType);

    public <T extends ElementCore> List<T> listElements(String inPrimaryName, String inScientificName, ElementType inElementType, Class<T> inReturnType);
    public <T extends LocationCore> List<T> listLocations(String inName, Class<T> inReturnType);
    public <T extends VisitCore> List<T> listVisits(String inName, long inLocationID, VisitType inVisitType, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends SightingCore> List<T> listSightings(long inElementID, long inLocationID, long inVisitID, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends WildLogFileCore> List<T> listWildLogFiles(String inWildLogFileID, WildLogFileType inWildLogFileType, Class<T> inReturnType);
    public <T extends AdhocData> List<T> listAdhocDatas(String inFieldID, Class<T> inReturnType);
    public <T extends INaturalistLinkedData> List<T> listINaturalistLinkedDatas(Class<T> inReturnType);
    public <T extends WildLogUser> List<T> listUsers(WildLogUserTypes inType, Class<T> inReturnType);

    public <T extends ElementCore> boolean createElement(T inElement, boolean inNewButKeepID);
    public <T extends LocationCore> boolean createLocation(T inLocation, boolean inNewButKeepID);
    public <T extends VisitCore> boolean createVisit(T inVisit, boolean inNewButKeepID);
    public <T extends SightingCore> boolean createSighting(T inSighting, boolean inNewButKeepID);
    public <T extends WildLogFileCore> boolean createWildLogFile(T inWildLogFile);
    public <T extends WildLogOptions> boolean createWildLogOptions(T inWildLogOptions);
    public <T extends AdhocData> boolean createAdhocData(T inAdhocData);
    public <T extends INaturalistLinkedData> boolean createINaturalistLinkedData(T inINaturalistLinkedData);
    public <T extends WildLogUser> boolean createUser(T inWildLogUser);
    
    public <T extends ElementCore> boolean updateElement(T inElement, String inOldName);
    public <T extends LocationCore> boolean updateLocation(T inLocation, String inOldName);
    public <T extends VisitCore> boolean updateVisit(T inVisit, String inOldName);
    public <T extends SightingCore> boolean updateSighting(T inSighting);
    public <T extends WildLogFileCore> boolean updateWildLogFile(T inWildLogFile);
    public <T extends WildLogOptions> boolean updateWildLogOptions(T inWildLogOptions);
    public <T extends AdhocData> boolean updateAdhocData(T inAdhocData);
    public <T extends INaturalistLinkedData> boolean updateINaturalistLinkedData(T inINaturalistLinkedData);
    public <T extends WildLogUser> boolean updateUser(T inWildLogUser);

    public boolean deleteElement(long inID);
    public boolean deleteLocation(long inID);
    public boolean deleteVisit(long inID);
    public boolean deleteSighting(long inID);
    public boolean deleteWildLogFile(String inDBFilePath);
    public boolean deleteAdhocData(String inFieldID, String inDataKey);
    public boolean deleteINaturalistLinkedData(long inWildLogID, long inINaturalistID);
    public boolean deleteUser(String inUsername);

    public <S extends SightingCore> List<S> searchSightings(List<Long> inActiveSightingIDs, Date inStartDate, Date inEndDate, 
            List<Long> inActiveLocations, List<Long> inActiveVisits, List<Long> inActiveElements, 
            boolean inIncludeCachedValues, Class<S> inReturnType);
    
    public <T extends LocationCount> List<T> queryLocationCountForElement(long inElementID, Class<T> inReturnType);

    public long generateID();
    
    public int activeSessionsCount();
    
}
