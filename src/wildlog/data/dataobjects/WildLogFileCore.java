package wildlog.data.dataobjects;

import java.util.Calendar;
import java.util.Date;
import wildlog.data.enums.WildLogFileType;


public class WildLogFileCore {
    protected String id; // The id should be in the format: location-kruger or creature-rooibok
    protected String filename;
    protected String originalFileLocation; // This is used as the DB table ID
    protected Date uploadDate;
    protected WildLogFileType fileType;
    protected boolean defaultFile = false;


    public WildLogFileCore() {
    }

    public WildLogFileCore(String inID) {
        id = inID;
    }

    public WildLogFileCore(String inID, String inFilename, String inFilePath, WildLogFileType inFileType) {
        this(inID, inFilename, inFilePath, inFileType, Calendar.getInstance().getTime());
    }

    public WildLogFileCore(String inID, String inFilename, String inFilePath, WildLogFileType inFileType, Date inUploadDate) {
        id = inID;
        filename = inFilename;
        originalFileLocation = inFilePath;
        uploadDate = inUploadDate;
        fileType = inFileType;
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

}