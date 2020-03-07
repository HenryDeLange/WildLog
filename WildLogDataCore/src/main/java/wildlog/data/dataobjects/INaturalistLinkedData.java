package wildlog.data.dataobjects;


public class INaturalistLinkedData {
    private long wildlogID;
    private long iNaturalistID;
    private String iNaturalistData;

    
    public INaturalistLinkedData() {
    }

    public INaturalistLinkedData(long inWildlogID, long inINaturalistID, String inINaturalistData) {
        wildlogID = inWildlogID;
        iNaturalistID = inINaturalistID;
        iNaturalistData = inINaturalistData;
    }

    
    public long getWildlogID() {
        return wildlogID;
    }

    public void setWildlogID(long inWildlogID) {
        wildlogID = inWildlogID;
    }

    public long getINaturalistID() {
        return iNaturalistID;
    }

    public void setINaturalistID(long inINaturalistID) {
        iNaturalistID = inINaturalistID;
    }

    public String getINaturalistData() {
        return iNaturalistData;
    }

    public void setINaturalistData(String inINaturalistData) {
        iNaturalistData = inINaturalistData;
    }
    
}
