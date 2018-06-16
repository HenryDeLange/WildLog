package wildlog.data.enums;


public enum GPSAccuracy {
    /** 1-5m */
    VERY_GOOD     ("Very Good (1-5m)", Double.MIN_NORMAL, 5),
    /** 5-10m */
    GOOD          ("Good (5-10m)", 5, 10),
    /** 10-100m */
    AVERAGE       ("Average (10-100m)", 10, 100),
    /** 100-500m */
    BAD           ("Bad (100-500m)", 100, 500),
    /** 500+m */
    TERRIBLE      ("Terrible (500m or more)", 500, 9999),
    /** ??m */
    EDUCATED_GUESS("Educated Guess", 9999, 9999),
    UNKNOWN       ("Unknown", 0, 0),
    NONE          ("", 0, 0);

    private final String text;
    private final double minMeters;
    private final int maxMeters;
    
    GPSAccuracy(String inText, double inMinMeters, int inMaxMeters) {
        text = inText;
        minMeters = inMinMeters;
        maxMeters = inMaxMeters;
    }

    @Override
    public String toString() {
        return text;
    }

    public double getMinMeters() {
        return minMeters;
    }
    
    public int getMaxMeters() {
        return maxMeters;
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
