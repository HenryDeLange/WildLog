package wildlog.data.wrappers.json.gbif;


public class GBIFOccurence {
    private long key;
    private double decimalLongitude;
    private double decimalLatitude;

    public long getKey() {
        return key;
    }

    public void setKey(long inKey) {
        key = inKey;
    }

    public double getDecimalLongitude() {
        return decimalLongitude;
    }

    public void setDecimalLongitude(double inDecimalLongitude) {
        decimalLongitude = inDecimalLongitude;
    }

    public double getDecimalLatitude() {
        return decimalLatitude;
    }

    public void setDecimalLatitude(double inDecimalLatitude) {
        decimalLatitude = inDecimalLatitude;
    }

    @Override
    public String toString() {
        return super.toString() + "; key=" + key + "; decimalLongitude=" + decimalLongitude + "; decimalLatitude=" + decimalLatitude;
    }
    
}
