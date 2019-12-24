package wildlog.data.dbi;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;


public interface WildLogDBI extends DBI {
    public final static String BACKUP_H2 = "WildLog Backup - H2.zip";
    public final static String BACKUP_SQL = "WildLog Backup - SQL.zip";

    public void doCompact() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;
    public void doBackup(Path inDestinationFolder);
    public void doRestore(Path inSourceFolder) throws Exception;
    public void doExportFullCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings);
    public void doExportBasicCSV(Path inPath, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings);
    public void doImportCSV(Path inCSVPath, boolean inAutoResolve, boolean includeWildLogFilesTable);
    public void doImportBasicCSV(Path inPath);
    public boolean doImportIUCN(Path inPath, boolean inUpdatePrimaryName, boolean inAddNewElements, boolean inUpdateExistingElements);

}
