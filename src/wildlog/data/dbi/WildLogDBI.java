package wildlog.data.dbi;

import wildlog.utils.WildLogPaths;


public interface WildLogDBI extends DBI {

    public void doBackup(WildLogPaths inFolder);
    public void doExportCSV(String inPath);
    public boolean doImportCSV(String inPath, String inPrefix);

}
