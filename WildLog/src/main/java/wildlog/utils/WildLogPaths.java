package wildlog.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;

/**
 * This enum centralizes all file path related logic for the application. <br>
 * General notes and recommended use: <br>
 *  - For relative paths all enums defined here will <b>start with a File Separator</b>. <br>
 *  - All paths defined here will <b>end without File Separators</b>.
 */
public enum WildLogPaths {
    /** WARNING: Remember that the settings folder is not in the WildLog workspace. This is not necessarily the active settings folder.*/
    DEFAUL_SETTINGS_FOLDER                (Paths.get(System.getProperty("user.home"), "WildLogSettings")),
    /** WARNING: Remember that this folder is not in the WildLog workspace, but points to the installation directory.*/
    OPEN_H2                               (Paths.get(System.getProperty("user.dir"), "lib", "h2-1.4.200.jar")),
    /** WARNING: Remember that this folder is not in the WildLog workspace, but points to the installation directory.*/
    OPEN_JPEGVIEW                         (Paths.get(System.getProperty("user.dir"), "JPEGView", "JPEGView.exe")),
    /** WARNING: Remember that this folder is not in the WildLog workspace, but points to the installation directory.*/
    OPEN_MEDIA_PLAYER_CLASSIC             (Paths.get(System.getProperty("user.dir"), "MPC-HC", "mpc-hc.exe")),
    /** WARNING: Don't use this value in "normal" code. It is only used to store the name of the Workspace.
     * The workspacePrefix already has this value appended. */
    DEFAULT_WORKSPACE_NAME                (Paths.get("WildLog")),
    /** WARNING: Don't use this value in "normal" code. It is only used to store the name of the database. */
    DEFAULT_DATABASE_NAME                 (Paths.get("wildlog")),
    // These are the values that can be reused.
    WILDLOG_WORKSPACE_INDICATOR           (Paths.get("workspace.wildlog")),
    WILDLOG_WORKSPACE_DATA_LICENSE        (Paths.get("WildLog_Workspace_Data_License.txt")),
    WILDLOG_DATA                          (Paths.get("Data")),
    WILDLOG_DATA_READONLY_INDICATOR       (Paths.get("Data", "DON'T MODIFY THESE FILES!.txt")),
    WILDLOG_FILES                         (Paths.get("Files")),
    WILDLOG_FILES_READONLY_INDICATOR      (Paths.get("Files", "DON'T MODIFY THESE FILES!.txt")),
    WILDLOG_FILES_IMAGES                  (Paths.get("Files", "Images")),
    WILDLOG_FILES_MOVIES                  (Paths.get("Files", "Movies")),
    WILDLOG_FILES_OTHER                   (Paths.get("Files", "Other")),
    WILDLOG_FILES_STASH                   (Paths.get("Files", "Stashed")),
    WILDLOG_LOST_FILES                    (Paths.get("LostFiles")),
    WILDLOG_THUMBNAILS                    (Paths.get("Thumbnails")),
    WILDLOG_MAPS                          (Paths.get("Maps")),
    WILDLOG_MAPS_CUSTOM                   (Paths.get("Maps", "Custom")),
    WILDLOG_MAPS_SPECIES                  (Paths.get("Maps", "Species")),
    WILDLOG_BACKUPS                       (Paths.get("Backup")),
    WILDLOG_BACKUPS_AUTO                  (Paths.get("Backup", "Auto")),
    WILDLOG_BACKUPS_DAILY                 (Paths.get("Backup", "Daily")),
    WILDLOG_BACKUPS_CHECK_AND_CLEAN       (Paths.get("Backup", "CheckAndClean")),
    WILDLOG_BACKUPS_UPGRADE               (Paths.get("Backup", "Upgrade")),
    WILDLOG_EXPORT                        (Paths.get("Export")),
    WILDLOG_EXPORT_KML                    (Paths.get("Export", "KML")),
    WILDLOG_EXPORT_KML_THUMBNAILS         (Paths.get("Export", "KML", "Thumbnails")),
    WILDLOG_EXPORT_CSV                    (Paths.get("Export", "CSV", "Complete")),
    WILDLOG_EXPORT_CSV_ALL                (Paths.get("Export", "CSV", "Complete", "All")),
    WILDLOG_EXPORT_CSV_BASIC              (Paths.get("Export", "CSV", "Basic")),
    WILDLOG_EXPORT_REPORTS                (Paths.get("Export", "Reports")),
    WILDLOG_EXPORT_CHARTS_PDF             (Paths.get("Export", "Charts", "PDF")),
    WILDLOG_EXPORT_CHARTS_PNG             (Paths.get("Export", "Charts", "Image")),
    WILDLOG_EXPORT_CHARTS_CSV             (Paths.get("Export", "Charts", "CSV")),
    WILDLOG_EXPORT_CHARTS_HTML            (Paths.get("Export", "Charts", "HTML")),
    WILDLOG_EXPORT_CHARTS_HTML_IMAGES     (Paths.get("Export", "Charts", "HTML", "Images")),
    WILDLOG_EXPORT_MAPS_PDF               (Paths.get("Export", "Maps", "PDF")),
    WILDLOG_EXPORT_MAPS_PNG               (Paths.get("Export", "Maps", "Image")),
    WILDLOG_EXPORT_MAPS_CSV               (Paths.get("Export", "Maps", "CSV")),
    WILDLOG_EXPORT_MAPS_HTML              (Paths.get("Export", "Maps", "HTML")),
    WILDLOG_EXPORT_MAPS_HTML_IMAGES       (Paths.get("Export", "Maps", "HTML", "Images")),
    WILDLOG_EXPORT_HTML                   (Paths.get("Export", "WebPage")),
    WILDLOG_EXPORT_HTML_BASIC             (Paths.get("Export", "WebPage", "Basic")),
    WILDLOG_EXPORT_HTML_BASIC_THUMBNAILS  (Paths.get("Export", "WebPage", "Basic", "Thumbnails")),
    WILDLOG_EXPORT_HTML_FANCY             (Paths.get("Export", "WebPage", "Advanced")),
    WILDLOG_EXPORT_HTML_FANCY_RESOURCES   (Paths.get("Export", "WebPage", "Advanced", "Scripts")),
    WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS  (Paths.get("Export", "WebPage", "Advanced", "Thumbnails")),
    WILDLOG_EXPORT_HTML_TEMPORARY         (Paths.get("Export", "WebPage", "Temporary")),
    WILDLOG_EXPORT_WILDNOTE_SYNC          (Paths.get("Export", "WildNoteSync")),
    WILDLOG_EXPORT_SLIDESHOW              (Paths.get("Export", "Slideshow")),
    WILDLOG_EXPORT_FILES                  (Paths.get("Export", "OriginalFiles")),
    WILDLOG_EXPORT_XML                    (Paths.get("Export", "XML")),
    WILDLOG_EXPORT_XLS                    (Paths.get("Export", "Excel")),
    WILDLOG_EXPORT_XLS_ALL                (Paths.get("Export", "Excel", "All")),
    WILDLOG_EXPORT_XLS_PAARL              (Paths.get("Export", "Excel", "Paarl Format")),
    WILDLOG_EXPORT_WORKSPACE              (Paths.get("Export", "Workspace")),
    WILDLOG_EXPORT_SYSTEM_MONITOR         (Paths.get("Export", "SystemMonitor")),
    WILDLOG_BUNDLED_APPLICATION           (Paths.get("WildLogApplication")),
    /** Folder for temporary files */
    WILDLOG_TEMP                          (Paths.get(System.getProperty("user.home"), "WildLogSettings", "Temp"));

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
            activeWorkspacePrefix = Paths.get(File.separator).resolve(DEFAULT_WORKSPACE_NAME.getRelativePath()).toAbsolutePath().normalize();
        }
        else {
            activeWorkspacePrefix = Paths.get(inPrefix).toAbsolutePath().normalize();
        }
        if (new File(File.separator).toPath().toAbsolutePath().normalize().equals(activeWorkspacePrefix)) {
            activeWorkspacePrefix = activeWorkspacePrefix.resolve(DEFAULT_WORKSPACE_NAME.getRelativePath()).toAbsolutePath().normalize();
            WildLogApp.LOGGER.log(Level.INFO, "Corrected wildloghome to not point to the main drive, but to a folder instead: " + activeWorkspacePrefix.toString());
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
     * @return 
     */
    @Override
    public String toString() {
        return getRelativePath().toString();
    }

}
