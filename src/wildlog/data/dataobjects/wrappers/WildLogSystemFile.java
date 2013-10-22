package wildlog.data.dataobjects.wrappers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


public class WildLogSystemFile extends WildLogFile{
    private Path originalPath;

    public WildLogSystemFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType) {
        super(inID, inFilename, inFilePath, inFileType);
        originalPath = WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath()
                .resolve(WildLogPaths.WildLogPathPrefixes.WILDLOG_SYSTEM_DUMP.toString())
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
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setDefaultFile(boolean defaultFile) {
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setFileType(WildLogFileType inFileType) {
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setFilename(String filename) {
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setId(String id) {
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

    @Override
    public void setUploadDate(Date inDate) {
        System.out.println("Don't try to change a WildLogSystemFile...");
    }

}
