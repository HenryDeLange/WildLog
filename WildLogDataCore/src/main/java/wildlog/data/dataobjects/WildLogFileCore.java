package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogFileType;


public class WildLogFileCore extends DataObjectWithAudit {
    protected long id;
    protected String linkID; // The id should be in the format [WILDLOGFILE_ID_PREFIX + id] based on the parent data object
    protected String filename;
    protected String originalFileLocation; // This is used as the DB table ID
    protected Date uploadDate;
    protected WildLogFileType fileType;
    protected boolean defaultFile = false;
    protected Date fileDate;
    protected long fileSize;


    public WildLogFileCore() {
    }
    
    public WildLogFileCore(long inID, String inLinkID, String inFilename, String inOriginalFileLocation, 
            WildLogFileType inFileType, Date inUploadDate, Date inFileDate, long inFileSize) {
        id = inID;
        linkID = inLinkID;
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

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String inLinkID) {
        linkID = inLinkID;
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