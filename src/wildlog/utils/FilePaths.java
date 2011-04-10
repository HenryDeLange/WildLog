package wildlog.utils;

import java.io.File;

public enum FilePaths {
    WILDLOG (File.separatorChar + "WildLog" + File.separatorChar),
    WILDLOG_DATA (File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar),
    WILDLOG_IMAGES (File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar),
    WILDLOG_MOVIES (File.separatorChar + "WildLog" + File.separatorChar + "Movies" + File.separatorChar),
    WILDLOG_OTHER (File.separatorChar + "WildLog" + File.separatorChar + "Other Uploads" + File.separatorChar),
    WILDLOG_BACKUPS (File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar),
    WILDLOG_EXPORT (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar),
    WILDLOG_EXPORT_KML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML" + File.separatorChar),
    WILDLOG_EXPORT_CSV (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "CSV" + File.separatorChar),
    WILDLOG_EXPORT_HTML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar)
    ;

    private static String currentRoot;

    public static void setRoot(String inRoot) {
        currentRoot = inRoot;
        if (currentRoot.charAt(currentRoot.length()-1) == File.separatorChar)
            currentRoot = currentRoot.substring(0, currentRoot.length()-1);
    }
    
    private String path;

    public String getFullPath() {
        return currentRoot + path;
    }

    public String getRelativePath() {
        return path;
    }

    public static String getRoot() {
        return currentRoot;
    }

    private FilePaths(String inPath) {
        path = inPath;
    }

    @Override
    public String toString() {
        return path;
    }

}
