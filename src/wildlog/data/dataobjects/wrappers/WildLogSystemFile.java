package wildlog.data.dataobjects.wrappers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.logging.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


public class WildLogSystemFile extends WildLogFile {
    public static final String WILDLOG_FOLDER_PREFIX  = "WildLogSystem";
    private final Path originalPath;

    public WildLogSystemFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType) {
        super(inID, inFilename, inFilePath, inFileType);
        originalPath = WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath()
                .resolve(WILDLOG_FOLDER_PREFIX)
                .resolve(inFilename).normalize().toAbsolutePath();
        // Make sure the file original file has been copied out of the JAR and is in the thumbnails folder.
        copyOriginalFileToThumbnails();
    }

    /**
     * Copy the original file from the JAR into the Thumbnails folder for further use.
     */
    private void copyOriginalFileToThumbnails() {
        if (!Files.exists(originalPath)) {
            InputStream templateStream = WildLogApp.class.getResourceAsStream(getDBFilePath());
            UtilsFileProcessing.createFileFromStream(templateStream, originalPath);
        }
    }

    @Override
    public Path getAbsolutePath() {
        // Make sure the file original file has been copied out of the JAR and is in the thumbnails folder.
        copyOriginalFileToThumbnails();
        return originalPath;
    }

    @Override
    public Path getRelativePath() {
        // Make sure the file original file has been copied out of the JAR and is in the thumbnails folder.
        copyOriginalFileToThumbnails();
        return WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath().relativize(originalPath).normalize();
    }

    @Override
    public Path getAbsoluteThumbnailPath(WildLogThumbnailSizes inSize) {
        // Make sure the file original file has been copied out of the JAR and is in the thumbnails folder.
        copyOriginalFileToThumbnails();
        return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(this, inSize);
    }

    @Override
    public String getDBFilePath() {
        return super.getDBFilePath();
    }

    @Override
    public String toHTML(UtilsHTMLExportTypes inExportType) {
        return super.toHTML(inExportType);
    }

    @Override
    public void setDBFilePath(String inOriginalFileAsRelativePathForWorkspace) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setDefaultFile(boolean defaultFile) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setFileType(WildLogFileType inFileType) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setFilename(String filename) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setId(String id) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setUploadDate(Date inDate) {
        WildLogApp.LOGGER.log(Level.WARNING, "Don't try to change a WildLogSystemFile...");
    }

}
