package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum GPSAccuracy implements EnumWithID {
    /** 1-5m */
    VERY_GOOD     ((byte)  1, "Very Good (1-5m)",        Double.MIN_NORMAL, 5),
    /** 5-10m */
    GOOD          ((byte)  2, "Good (5-10m)",            5,                 10),
    /** 10-100m */
    AVERAGE       ((byte)  3, "Average (10-100m)",       10,                100),
    /** 100-500m */
    BAD           ((byte)  4, "Bad (100-500m)",          100,               500),
    /** 500+m */
    TERRIBLE      ((byte)  5, "Terrible (500m or more)", 500,               9999),
    /** ??m */
    EDUCATED_GUESS((byte)  6, "Educated Guess",          9999,              9999),
    UNKNOWN       ((byte) -1, "Unknown",                 0,                 0),
    NONE          ((byte)  0, "",                        0,                 0);

    private final byte id;
    private final String text;
    private final double minMeters;
    private final int maxMeters;
    
    GPSAccuracy(byte inID, String inText, double inMinMeters, int inMaxMeters) {
        id = inID;
        text = inText;
        minMeters = inMinMeters;
        maxMeters = inMaxMeters;
    }

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public byte getID() {
        return id;
    }

    public double getMinMeters() {
        return minMeters;
    }
    
    public int getMaxMeters() {
        return maxMeters;
    }

    public static GPSAccuracy getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= GPSAccuracy.values().length) {
            return NONE;
        }
        for (GPSAccuracy theEnum : GPSAccuracy.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static GPSAccuracy getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (GPSAccuracy theEnum : GPSAccuracy.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
