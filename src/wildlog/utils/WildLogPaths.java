package wildlog.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This enum centralizes all file path related logic for the application. <br/>
 * General notes and recommended use: <br/>
 *  - For relative paths all enums defined here will <b>start with a File Separator</b>. <br/>
 *  - All paths defined here will <b>end without File Separators</b>.
 */
public enum WildLogPaths {
    /** WARNING: Remember that the settings folder is not in the WildLog workspace. This is not necessarily the active settings folder.*/
    DEFAUL_SETTINGS_FOLDER                (Paths.get(System.getProperty("user.home"), "WildLogSettings")),
    /** WARNING: Remember that this folder is not in the WildLog workspace, but points to the installation directory.*/
    OPEN_H2                               (Paths.get(System.getProperty("user.dir"), "lib", "h2-1.4.185.jar")),
    /** WARNING: Remember that this folder is not in the WildLog workspace, but points to the installation directory.*/
    OPEN_OPENMAP                          (Paths.get(System.getProperty("user.dir"), "lib", "openmap.jar")),
    /** WARNING: Don't use this value in "normal" code. It is only used to store the name of the Workspace.
     * The workspacePrefix already has this value appended. */
    DEFAULT_WORKSPACE_NAME                (Paths.get("WildLog")),
    /** WARNING: Don't use this value in "normal" code. It is only used to store the name of the database name. */
    DEFAULT_DATABASE_NAME                 (Paths.get("wildlog")),
    // These are the values that can be reused.
    WILDLOG_DATA                          (Paths.get("Data")),
    WILDLOG_FILES                         (Paths.get("Files")),
    WILDLOG_FILES_IMAGES                  (Paths.get("Files", "Images")),
    WILDLOG_FILES_MOVIES                  (Paths.get("Files", "Movies")),
    WILDLOG_FILES_OTHER                   (Paths.get("Files", "Other")),
    WILDLOG_THUMBNAILS                    (Paths.get("Thumbnails")),
    WILDLOG_MAPS                          (Paths.get("Maps")),
    WILDLOG_BACKUPS                       (Paths.get("Backup")),
    WILDLOG_BACKUPS_MONTHLY               (Paths.get("Backup", "Auto")),
    WILDLOG_BACKUPS_UPGRADE               (Paths.get("Backup", "Upgrade")),
    WILDLOG_TEMP                          (Paths.get(System.getProperty("user.home"), "WildLogSettings", "Temp")),
    WILDLOG_EXPORT                        (Paths.get("Export")),
    WILDLOG_EXPORT_KML                    (Paths.get("Export", "KML")),
    WILDLOG_EXPORT_KML_THUMBNAILS         (Paths.get("Export", "KML", "Thumbnails")),
    WILDLOG_EXPORT_CSV                    (Paths.get("Export", "Spreadsheet", "Complete")),
    WILDLOG_EXPORT_CSV_BASIC              (Paths.get("Export", "Spreadsheet", "Basic")),
    WILDLOG_EXPORT_REPORTS_PDF            (Paths.get("Export", "Reports", "PDF")),
    WILDLOG_EXPORT_REPORTS_PNG            (Paths.get("Export", "Reports", "Image")),
    WILDLOG_EXPORT_REPORTS_CSV            (Paths.get("Export", "Reports", "Spreadsheet")),
    WILDLOG_EXPORT_REPORTS_HTML           (Paths.get("Export", "Reports", "HTML")),
    WILDLOG_EXPORT_REPORTS_HTML_IMAGES    (Paths.get("Export", "Reports", "HTML", "Images")),
    WILDLOG_EXPORT_HTML                   (Paths.get("Export", "HTML")),
    WILDLOG_EXPORT_HTML_THUMBNAILS        (Paths.get("Export", "HTML", "Thumbnails")),
    WILDLOG_EXPORT_HTML_FANCY             (Paths.get("Export", "HTML(Advanced)")),
    WILDLOG_EXPORT_HTML_FANCY_RESOURCES   (Paths.get("Export", "HTML(Advanced)", "Scripts")),
    WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS  (Paths.get("Export", "HTML(Advanced)", "Thumbnails")),
    WILDLOG_EXPORT_WILDNOTE_SYNC          (Paths.get("Export", "WildNoteSync")),
    WILDLOG_EXPORT_SLIDESHOW              (Paths.get("Export", "Slideshow")),
    WILDLOG_EXPORT_FILES                  (Paths.get("Export", "OriginalFiles")),
    WILDLOG_EXPORT_XML                    (Paths.get("Export", "XML")),
    WILDLOG_EXPORT_WORKSPACE              (Paths.get("Export", "Workspace")),
    WILDLOG_BUNDLED_APPLICATION           (Paths.get("WildLogApplication"));

    private static Path activeWorkspacePrefix;
    private final Path path;

    private WildLogPaths(Path inPath) {
        path = inPath.normalize();
    }

    /**
     * This sets the workspace's prefix that will be used. Can be relative/absolute.
     * @param inPrefix
     */
    public static void setWorkspacePrefix(String inPrefix) {
        if (inPrefix == null || inPrefix.isEmpty()) {
            activeWorkspacePrefix = Paths.get(File.separator).toAbsolutePath().normalize();
        }
        else {
            activeWorkspacePrefix = Paths.get(inPrefix).toAbsolutePath().normalize();
        }
        // Add the WildLog folder to the prefix if it was not selected
        if (!activeWorkspacePrefix.endsWith(DEFAULT_WORKSPACE_NAME.getRelativePath())) {
            activeWorkspacePrefix = activeWorkspacePrefix.resolve(DEFAULT_WORKSPACE_NAME.getRelativePath()).normalize();
        }
    }

    /**
     * Get the absolute path of WorkspacePrefix + path.
     * WARNING: Rather use relative paths to cater for different drive letters, etc.
     * @return
     */
    public Path getAbsoluteFullPath() {
        return activeWorkspacePrefix.resolve(path).toAbsolutePath().normalize();
    }

    /**
     * Get only the Path. (For DEFAULT_WORKSPACE_NAME paths this is relative to the WildLog folder,
     * <b>excluding the WildLog folder</b> in the path.)
     * @return
     */
    public Path getRelativePath() {
        return path.normalize();
    }

    /**
     * This will return the full absolute path of the Workspace Prefix.
     * This value will include the 'WildLog' part.
     * @return
     */
    public static Path getFullWorkspacePrefix() {
        return activeWorkspacePrefix.toAbsolutePath().normalize();
    }

    /**
     * Returns the Path (relative to the WildLog folder).
     */
    @Override
    public String toString() {
        return getRelativePath().toString();
    }

}
