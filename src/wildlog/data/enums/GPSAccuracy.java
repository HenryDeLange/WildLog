package wildlog.data.enums;


public enum GPSAccuracy {
    GOOD          ("Good (1-10m)"),
    AVERAGE       ("Average (10-100m)"),
    BAD           ("Bad (100-500m)"),
    TERRIBLE      ("Terrible (500m or more)"),
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
