package wildlog.ui.panels.bulkupload.helpers;

import java.util.List;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;


public class BulkImportStashedTableData {
    private Sighting sighting;
    private int imageBoxSize;
    private List<FileData> lstFileData;

    public BulkImportStashedTableData() {
    }

    public BulkImportStashedTableData(Sighting inSighting, int inImageBoxSize, List<FileData> inLstFileData) {
        sighting = inSighting;
        imageBoxSize = inImageBoxSize;
        lstFileData = inLstFileData;
    }
    
    public Sighting getSighting() {
        return sighting;
    }

    public void setSighting(Sighting inSighting) {
        sighting = inSighting;
    }

    public int getImageBoxSize() {
        return imageBoxSize;
    }

    public void setImageBoxSize(int inImageBoxSize) {
        imageBoxSize = inImageBoxSize;
    }

    public List<FileData> getLstFileData() {
        return lstFileData;
    }

    public void setLstFileData(List<FileData> inLstFileData) {
        lstFileData = inLstFileData;
    }
    
    public static class FileData {
        public String path;
        public String date;
        public DataObjectWithGPS dataObjectWithGPS;
    }
    
}
