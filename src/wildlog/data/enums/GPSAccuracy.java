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

    private String text;

    GPSAccuracy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static GPSAccuracy getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(VERY_GOOD.text)) return VERY_GOOD;
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(AVERAGE.text)) return AVERAGE;
        if (inText.equalsIgnoreCase(BAD.text)) return BAD;
        if (inText.equalsIgnoreCase(TERRIBLE.text)) return TERRIBLE;
        if (inText.equalsIgnoreCase(EDUCATED_GUESS.text)) return EDUCATED_GUESS;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
