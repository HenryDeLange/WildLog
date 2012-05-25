package wildlog.data.dbi;

import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.utils.FilePaths;


public interface DBI {
    public void close();
    public void doBackup(FilePaths inFolder);
    public void doExportCSV(String inPath);
    public void doImportCSV(String inPath, String inPrefix);
    
    public Element find(Element inElement);
    public Location find(Location inLocation);
    public Visit find(Visit inVisit);
    public Sighting find(Sighting inSighting);
    public WildLogOptions find(WildLogOptions inWildLogOptions);
    
    public List<Element> list(Element inElement);
    public List<Location> list(Location inLocation);
    public List<Visit> list(Visit inVisit);
    public List<Sighting> list(Sighting inSighting);
    public List<WildLogFile> list(WildLogFile inFoto);

    public List<Sighting> searchSightingOnDate(Date inStartDate, Date inEndDate);
    
    public boolean createOrUpdate(Element inElement, String inOldName);
    public boolean createOrUpdate(Location inLocation, String inOldName);
    public boolean createOrUpdate(Visit inVisit, String inOldName);
    public boolean createOrUpdate(Sighting inSighting);
    public boolean createOrUpdate(WildLogFile inFoto, boolean inUpdate);
    public boolean createOrUpdate(WildLogOptions inWildLogOptions);
    
    public boolean delete(Element inElement);
    public boolean delete(Location inLocation);
    public boolean delete(Visit inVisit);
    public boolean delete(Sighting inSighting);
    public boolean delete(WildLogFile inFoto);

}
