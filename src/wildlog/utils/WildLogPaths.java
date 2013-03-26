package wildlog.utils;

import java.io.File;

public enum WildLogPaths {
    WILDLOG (File.separatorChar + "WildLog" + File.separatorChar),
    WILDLOG_DATA (File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar),
    WILDLOG_FILES (File.separatorChar + "WildLog" + File.separatorChar + "Files" + File.separatorChar),
    WILDLOG_FILES_IMAGES (File.separatorChar + "WildLog" + File.separatorChar + "Files" + File.separatorChar + "Images" + File.separatorChar),
    WILDLOG_FILES_MOVIES (File.separatorChar + "WildLog" + File.separatorChar + "Files" + File.separatorChar + "Movies" + File.separatorChar),
    WILDLOG_FILES_OTHER (File.separatorChar + "WildLog" + File.separatorChar + "Files" + File.separatorChar + "Other" + File.separatorChar),
    WILDLOG_THUMBNAILS_IMAGES (File.separatorChar + "WildLog" + File.separatorChar + "Thumbnails" + File.separatorChar),
    WILDLOG_MAPS (File.separatorChar + "WildLog" + File.separatorChar + "Maps" + File.separatorChar),
    WILDLOG_BACKUPS (File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar),
    WILDLOG_BACKUPS_MONTHLY (File.separatorChar + "WildLog" + File.separatorChar + "Backup" + File.separatorChar + "Auto" + File.separatorChar),
    WILDLOG_EXPORT (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar),
    WILDLOG_EXPORT_KML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML" + File.separatorChar),
    WILDLOG_EXPORT_CSV (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "CSV" + File.separatorChar),
    WILDLOG_EXPORT_HTML (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar),
    WILDLOG_EXPORT_SLIDESHOW (File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "Slideshow" + File.separatorChar)
    ;

    private static String currentWorkspacePrefix;
    private String path;

    private WildLogPaths(String inPath) {
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
        // Sanitize the value to be consistent (never end in a File.sepperator, never include terminating wildlog, etc.)
        currentWorkspacePrefix = concatPaths(false, currentWorkspacePrefix, File.separator);
        if (currentWorkspacePrefix.charAt(0) != File.separatorChar && currentWorkspacePrefix.charAt(1) != ':')
            currentWorkspacePrefix = concatPaths(false, File.separator, currentWorkspacePrefix);
        if (currentWorkspacePrefix.toLowerCase().endsWith(WildLogPaths.WILDLOG.toString().toLowerCase())) {
            // The name might be tricky to parse when it ends with WildLog so we have to do some extra checks,
            // because the user can select either c:\MyWildLog(\WildLog\Data) or c:\MyWildLog\WildLog(\Data)...
            // I'll use the presence of the WildLog\Data folder to test for Workspaces.
            File testFile = new File(concatPaths(true, currentWorkspacePrefix, WildLogPaths.WILDLOG_DATA.toString().replace(WildLogPaths.WILDLOG.toString(), File.separator)));
            if (testFile.exists() && testFile.isDirectory() && testFile.canRead() && testFile.canWrite()) {
                // I assume the user selected the WildLog folder and we need to strip it from the worspace prefix's path
                currentWorkspacePrefix = concatPaths(false, currentWorkspacePrefix.substring(0, currentWorkspacePrefix.length() - WildLogPaths.WILDLOG.toString().length()), File.separator);
            }
        }
    }

    /**
     * Get the WorkspacePrefix + the Path.
     * WARNING: Rather use relative paths to cater for different drive letters, etc.
     */
    public String getFullPath() {
        return concatPaths(false, getFullWorkspacePrefix(), path);
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
     * Optionally the returned value will always end without a File.separatorChar.
     * WARNING: The presence of any NULL values will return null.
     *          Empty Strings will be ignored.
     *          Multiple File.seperatorChar's are possible/allowed next one another if present in the original parts.
     */
    public static String concatPaths(boolean inTrimFileSeparatorFromEnd, String... inPathParts) {
        String finalPath = "";
        for (String part : inPathParts) {
            if (part == null) {
                return null;
            }
            if (!part.isEmpty()) {
                if (finalPath.length() == 0) {
                    finalPath = part;
                    continue;
                }
                if (finalPath.charAt(finalPath.length()-1) == File.separatorChar
                        && part.charAt(0) == File.separatorChar) {
                    finalPath = finalPath + part.substring(1);
                    continue;
                }
                else
                if (finalPath.charAt(finalPath.length()-1) != File.separatorChar
                        && part.charAt(0) != File.separatorChar) {
                    finalPath = finalPath + File.separatorChar + part;
                    continue;
                }
                else {
                    finalPath = finalPath + part;
                    continue;
                }
            }
        }
        if (inTrimFileSeparatorFromEnd) {
            while (finalPath.length() > 1 && finalPath.endsWith(File.separator)) {
                finalPath = finalPath.substring(0, finalPath.lastIndexOf(File.separatorChar));
            }
        }
        return finalPath;
    }

    @Override
    /**
     * Returns the Path (relative to the WildLog folder).
     */
    public String toString() {
        return getRelativePath();
    }

    /**
     * This method will strip the first root part from the path.
     * WARNING: This method assumes that both the inPath and inRoot are
     * absolute proper paths.
     */
    public static String stripRootFromPath(String inPath, String inRoot) {
        if (inPath.toLowerCase().startsWith(inRoot.toLowerCase()))
            return inPath.substring(inRoot.length());
        else
            return inPath;
    }

}
