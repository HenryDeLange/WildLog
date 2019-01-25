package wildlog.data.dbi;

import java.nio.file.Path;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;


public interface WildLogDBI extends DBI {
    public final static String BACKUP_H2 = "WildLog Backup - H2.zip";
    public final static String BACKUP_SQL = "WildLog Backup - SQL.zip";

    public void doBackup(Path inDestinationFolder);
    public void doRestore(Path inSourceFolder);
    public void doExportCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings);
    public void doExportBasicCSV(Path inPath, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings);
    public boolean doImportCSV(Path inPath, String inPrefix, boolean includeWildLogFilesTable);
    public boolean doImportBasicCSV(Path inPath, String inPrefix);
    public boolean doImportIUCN(Path inPath, boolean inUpdatePrimaryName, boolean inAddNewElements, boolean inUpdateExistingElements);

}
