package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;


public class WildLogFileCore extends DataObjectWithAudit {
    protected long id;
    protected long linkID; // The ID of the linked data object (Element, Location, etc.)
    protected WildLogDataType linkType;
    protected String filename;
    protected String originalFileLocation; // This is used as the DB table ID
    protected Date uploadDate;
    protected WildLogFileType fileType;
    protected boolean defaultFile = false;
    protected Date fileDate;
    protected long fileSize;


    public WildLogFileCore() {
    }
    
    public WildLogFileCore(long inID, long inLinkID, WildLogDataType inLinkType, String inFilename, String inOriginalFileLocation, 
            WildLogFileType inFileType, Date inUploadDate, Date inFileDate, long inFileSize) {
        id = inID;
        linkID = inLinkID;
        linkType = inLinkType;
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

    public long getLinkID() {
        return linkID;
    }

    public void setLinkID(long inLinkID) {
        linkID = inLinkID;
    }

    public WildLogDataType getLinkType() {
        return linkType;
    }

    public void setLinkType(WildLogDataType inLinkType) {
        linkType = inLinkType;
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