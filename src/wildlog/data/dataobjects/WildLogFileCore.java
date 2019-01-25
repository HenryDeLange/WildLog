package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.enums.WildLogFileType;


public class WildLogFileCore {
    protected String id; // The id should be in the format: location-kruger or creature-rooibok
    protected String filename;
    protected String originalFileLocation; // This is used as the DB table ID
    protected Date uploadDate;
    protected WildLogFileType fileType;
    protected boolean defaultFile = false;
    protected Date fileDate;
    protected long fileSize;


    public WildLogFileCore() {
    }
    
    public WildLogFileCore(String inID, String inFilename, String inOriginalFileLocation, WildLogFileType inFileType, Date inUploadDate, Date inFileDate, long inFileSize) {
        id = inID;
        filename = inFilename;
        originalFileLocation = inOriginalFileLocation;
        uploadDate = inUploadDate;
        fileType = inFileType;
        fileDate = inFileDate;
        fileSize = inFileSize;
    }

    
    @Override
    public String toString() {
        return "[WildLogFile=" + originalFileLocation + "]";
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getDBFilePath() {
        return originalFileLocation;
    }

    public WildLogFileType getFileType() {
        return fileType;
    }

    public void setUploadDate(Date inDate) {
        uploadDate = inDate;
    }

    public void setDBFilePath(String inFilePathFromDB) {
        originalFileLocation = inFilePathFromDB;
    }

    public void setFileType(WildLogFileType inFileType) {
        fileType = inFileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String inId) {
        id = inId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String inFilename) {
        filename = inFilename;
    }

    public boolean isDefaultFile() {
        return defaultFile;
    }

    public void setDefaultFile(boolean inDefaultFile) {
        defaultFile = inDefaultFile;
    }

    public Date getFileDate() {
        return fileDate;
    }

    public void setFileDate(Date inFileDate) {
        fileDate = inFileDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long inFileSize) {
        fileSize = inFileSize;
    }

}