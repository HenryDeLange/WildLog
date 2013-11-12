package wildlog.data.dbi;

import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.queryobjects.LocationCount;


public interface DBI {

    public void close();

    public <T extends ElementCore> int count(T inElement);
    public <T extends LocationCore> int count(T inLocation);
    public <T extends VisitCore> int count(T inVisit);
    public <T extends SightingCore> int count(T inSighting);
    public <T extends WildLogFileCore> int count(T inWildLogFile);

    public <T extends ElementCore> T find(T inElement);
    public <T extends LocationCore> T find(T inLocation);
    public <T extends VisitCore> T find(T inVisit);
    public <T extends SightingCore> T find(T inSighting);
    public <T extends WildLogFileCore> T find(T inWildLogFile);
    public <T extends WildLogOptions> T find(T inWildLogOptions);

    public <T extends ElementCore> List<T> list(T inElement);
    public <T extends LocationCore> List<T> list(T inLocation);
    public <T extends VisitCore> List<T> list(T inVisit);
    public <T extends SightingCore> List<T> list(T inSighting);
    public <T extends WildLogFileCore> List<T> list(T inWildLogFile);

    public <T extends ElementCore> boolean createOrUpdate(T inElement, String inOldName);
    public <T extends LocationCore> boolean createOrUpdate(T inLocation, String inOldName);
    public <T extends VisitCore> boolean createOrUpdate(T inVisit, String inOldName);
    public <T extends SightingCore> boolean createOrUpdate(T inSighting);
    public <T extends WildLogFileCore> boolean createOrUpdate(T inFile, boolean inUpdate);
    public <T extends WildLogOptions> boolean createOrUpdate(T inWildLogOptions);

    public <T extends ElementCore> boolean delete(T inElement);
    public <T extends LocationCore> boolean delete(T inLocation);
    public <T extends VisitCore> boolean delete(T inVisit);
    public <T extends SightingCore> boolean delete(T inSighting);
    public <T extends WildLogFileCore> boolean delete(T inWildLogFile);

    public <T extends SightingCore> List<T> searchSightingOnDate(Date inStartDate, Date inEndDate, Class<T> inReturnType);
    public <T extends LocationCount, V extends ElementCore> List<T> queryLocationCountForElement(V inElement, Class<T> inReturnType);

}
