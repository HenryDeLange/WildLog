package wildlog.data.dbi;

import java.nio.file.Path;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.utils.WildLogPaths;


public interface WildLogDBI extends DBI {

    public void doBackup(WildLogPaths inFolder);
    public void doExportCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting);
    public boolean doImportCSV(Path inPath, String inPrefix, boolean includeWildLogFilesTable);
    public boolean doImportIUCN(Path inPath, boolean inUpdatePrimaryName, boolean inAddNewElements, boolean inUpdateExistingElements);

}
