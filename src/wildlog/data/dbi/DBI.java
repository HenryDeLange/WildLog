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


public interface DBI {

    public boolean initialize(boolean inCreateDefaultRecords);
    public void close();

    public int countElements(String inPrimaryName, String inScientificName);
    public int countLocations(String inName);
    public int countVisits(String inName, String inLocationName);
    public int countSightings(long inSightingCounter, String inElementName, String inLocationName, String inVisitName);
    public int countWildLogFiles(String inDBFilePath, String inWildLogFileID);

    public <T extends ElementCore> T find(T inElement);
    public <T extends LocationCore> T find(T inLocation);
    public <T extends VisitCore> T find(T inVisit);
    public <T extends SightingCore> T find(T inSighting);
    public <T extends WildLogFileCore> T find(T inWildLogFile);
    public <T extends WildLogOptions> T find(T inWildLogOptions);
    public <T extends AdhocData> T find(T inAdhocData);

    public <T extends ElementCore> List<T> list(T inElement);
    public <T extends LocationCore> List<T> list(T inLocation);
    public <T extends VisitCore> List<T> list(T inVisit);
    public <T extends SightingCore> List<T> list(T inSighting, boolean inIncludeCachedValues);
    public <T extends WildLogFileCore> List<T> list(T inWildLogFile);
    public <T extends AdhocData> List<T> list(T inAdhocData);

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
    
    public <T extends LocationCount, V extends ElementCore> List<T> queryLocationCountForElement(V inElement, Class<T> inReturnType);

    public long generateID();
    
}
