package wildlog.utils;

import java.io.File;

public enum FilePaths {
    WILDLOG (File.separatorChar + "WildLog" + File.separatorChar),
    WILDLOG_DATA (File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar),
    WILDLOG_IMAGES (File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar),
    WILDLOG_MOVIES (File.separatorChar + "WildLog" + File.separatorChar + "Movies" + File.separatorChar),
    WILDLOG_OTHER (File.separatorChar + "WildLog" + File.separatorChar + "Other Uploads" + File.separatorChar),
    WILDLOG_BACKUPS (File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar),
    WILDLOG_BACKUPS_MONTHLY (File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar + "Auto" + File.separatorChar),
    WILDLOG_EXPORT (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar),
    WILDLOG_EXPORT_KML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML" + File.separatorChar),
    WILDLOG_EXPORT_CSV (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "CSV" + File.separatorChar),
    WILDLOG_EXPORT_HTML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar),
    WILDLOG_EXPORT_SLIDESHOW (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "Slideshow" + File.separatorChar),
    WILDLOG_SETTINGS (System.getProperty("user.home") + File.separatorChar + "WildLog Settings" + File.separatorChar)
    ;

    private static String currentWorkspacePrefix;
    private String path;
    
    private FilePaths(String inPath) {
        path = inPath;
    }

    /**
     * This sets the workspace's prefix that will be used. Can be relative/absolute.
     * @param inRoot 
     */
    public static void setWorkspacePrefix(String inPrefix) {
        if (inPrefix == null)
            currentWorkspacePrefix = getFullWorkspacePrefix(); // Dit sal 'n "mooi" waarde return
        else
            currentWorkspacePrefix = inPrefix;
    }
    
    /**
     * Get the WorkspacePrefix + the Path.
     * WARNING: Rather use relative paths to cater for different drive letters, etc.
     */
    public String getFullPath() {
        return concatPaths(getFullWorkspacePrefix(), path);
    }

    /**
     * Get only the Path. This is relative to the WildLog folder.
     */
    public String getRelativePath() {
        return path;
    }

    /**
     * This will return the full path of the Workspace Prefix.
     * WARNING: Some tweaking is done to the to make it behave nicely...
     */
    public static String getFullWorkspacePrefix() {
        if (currentWorkspacePrefix == null)
            currentWorkspacePrefix = File.listRoots()[0].getPath();
        if (currentWorkspacePrefix.charAt(0) != File.separatorChar && currentWorkspacePrefix.charAt(1) != ':')
            currentWorkspacePrefix = File.separator + currentWorkspacePrefix;
        return new File(currentWorkspacePrefix).getAbsolutePath();
    }
    
    /**
     * This will return the full path of the Workspace Prefix. It will start with a File.separatorChar.
     * WARNING: Some tweaking is done to the to make it behave nicely...
     */
    public static String getRelativeWorkspacePrefix() {
        return getFullWorkspacePrefix().substring(2);
    }
    
    /**
     * This method will concatenate two path segments and make sure that only one File.separatorChar is used.
     * WARNING: Nulls will return null and ""s will return any non-"" value.
     */
    // TODO: Verander die dalk om VarArgs te gebruik. bv concatPaths(String... input) sodat ek meer Strings op 'n slag kan instuur
    public static String concatPaths(String inPart1, String inPart2) {
        if (inPart1 == null || inPart2 == null)
            return null;
        if (inPart1.length() == 0 || inPart2.length() == 0)
            return inPart1 + inPart2;
        else
        if (inPart1.charAt(inPart1.length()-1) == File.separatorChar 
                && inPart2.charAt(0) == File.separatorChar)
            return inPart1 + inPart2.substring(1);
        else
        if (inPart1.charAt(inPart1.length()-1) != File.separatorChar 
                && inPart2.charAt(0) != File.separatorChar)
            return inPart1 + File.separatorChar + inPart2;
        else
            return inPart1 + inPart2;
    }

    @Override
    /**
     * Returns the Path (relative to the WildLog folder).
     */
    public String toString() {
        return getRelativePath();
    }

}
