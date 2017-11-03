package wildlog.data.enums;


public enum GPSAccuracy {
    /** 1-5m */
    VERY_GOOD     ("Very Good (1-5m)", 5),
    /** 5-10m */
    GOOD          ("Good (5-10m)", 10),
    /** 10-100m */
    AVERAGE       ("Average (10-100m)", 100),
    /** 100-500m */
    BAD           ("Bad (100-500m)", 500),
    /** 500+m */
    TERRIBLE      ("Terrible (500m or more)", Integer.MAX_VALUE),
    /** ??m */
    EDUCATED_GUESS("Educated Guess", Integer.MAX_VALUE),
    UNKNOWN       ("Unknown", Integer.MAX_VALUE),
    NONE          ("", Integer.MAX_VALUE);

    private final String text;
    private final int meters;

    GPSAccuracy(String inText, int inMeters) {
        text = inText;
        meters = inMeters;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getMeters() {
        return meters;
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
