package wildlog.data.dbi;

import java.nio.file.Path;
import wildlog.utils.WildLogPaths;


public interface WildLogDBI extends DBI {

    public void doBackup(WildLogPaths inFolder);
    public void doExportCSV(Path inPath);
    public boolean doImportCSV(Path inPath, String inPrefix, boolean includeWildLogFilesTable);

}
