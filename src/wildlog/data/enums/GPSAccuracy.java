package wildlog.data.enums;


public enum GPSAccuracy {
    /** 1-5m */
    VERY_GOOD     ("Very Good (1-5m)"),
    /** 5-10m */
    GOOD          ("Good (5-10m)"),
    /** 10-100m */
    AVERAGE       ("Average (10-100m)"),
    /** 100-500m */
    BAD           ("Bad (100-500m)"),
    /** 500+m */
    TERRIBLE      ("Terrible (500m or more)"),
    /** ??m */
    EDUCATED_GUESS("Educated Guess"),
    UNKNOWN       ("Unknown"),
    NONE          ("");

    private final String text;

    GPSAccuracy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
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
