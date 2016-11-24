package wildlog.data.dbi;

import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.AdhocData;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogFileType;


public interface DBI {

    public boolean initialize(boolean inCreateDefaultRecords);
    public void close();

    public int countElements(String inPrimaryName, String inScientificName);
    public int countLocations(String inName);
    public int countVisits(String inName, String inLocationName);
    public int countSightings(long inSightingCounter, String inElementName, String inLocationName, String inVisitName);
    public int countWildLogFiles(String inDBFilePath, String inWildLogFileID);

    public <T extends ElementCore> T findElement(String inPrimaryName, Class<T> inReturnType);
    public <T extends LocationCore> T findLocation(String inName, Class<T> inReturnType);
    public <T extends VisitCore> T findVisit(String inName, Class<T> inReturnType);
    public <T extends SightingCore> T findSighting(long inSightingCounter, Class<T> inReturnType);
    public <T extends WildLogFileCore> T findWildLogFile(String inDBFilePath, String inWildLogFileID, Class<T> inReturnType);
    public <T extends WildLogOptions> T findWildLogOptions(Class<T> inReturnType);
    public <T extends AdhocData> T findAdhocData(String inFieldID, String inDataKey, Class<T> inReturnType);

    public <T extends ElementCore> List<T> listElements(String inPrimaryName, String inScientificName, ElementType inElementType, Class<T> inReturnType);
    public <T extends LocationCore> List<T> listLocations(String inName, Class<T> inReturnType);
    public <T extends VisitCore> List<T> listVisits(String inName, String inLocationName, VisitType inVisitType, Class<T> inReturnType);
    public <T extends SightingCore> List<T> listSightings(long inSightingCounter, String inElementName, 
            String inLocationName, String inVisitName, boolean inIncludeCachedValues, Class<T> inReturnType);
    public <T extends WildLogFileCore> List<T> listWildLogFiles(String inWildLogFileID, WildLogFileType inWildLogFileType, Class<T> inReturnType);
    public <T extends AdhocData> List<T> listAdhocDatas(String inFieldID, Class<T> inReturnType);

// TODO: Split these methods into propper create and update versions...
    
    public <T extends ElementCore> boolean createOrUpdate(T inElement, String inOldName);
    public <T extends LocationCore> boolean createOrUpdate(T inLocation, String inOldName);
    public <T extends VisitCore> boolean createOrUpdate(T inVisit, String inOldName);
    public <T extends SightingCore> boolean createOrUpdate(T inSighting, boolean inNewButKeepID);
    public <T extends WildLogFileCore> boolean createOrUpdate(T inFile, boolean inUpdate);
    public <T extends WildLogOptions> boolean createOrUpdate(T inWildLogOptions);
    public <T extends AdhocData> boolean createOrUpdate(T inAdhocData);

    public boolean deleteElement(String inPrimaryName);
    public boolean deleteLocation(String inName);
    public boolean deleteVisit(String inName);
    public boolean deleteSighting(long inSightingCounter);
    public boolean deleteWildLogFile(String inDBFilePath);
    public boolean deleteAdhocData(String inFieldID, String inDataKey);

    public <S extends SightingCore, L extends LocationCore, V extends VisitCore, E extends ElementCore> List<S> 
        searchSightings(Date inStartDate, Date inEndDate, List<L> inActiveLocations, List<V> inActiveVisits, List<E> inActiveElements, 
                boolean inIncludeCachedValues, Class<S> inReturnType);
    
    public <T extends LocationCount> List<T> queryLocationCountForElement(String inElementPrimaryName, Class<T> inReturnType);

    public long generateID();
    
}
