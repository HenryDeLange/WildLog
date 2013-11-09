package wildlog.data.enums;


public enum TimeAccuracy {
    GOOD          ("Good"),
    EDUCATED_GUESS("Educated Guess"),
    UNKNOWN       ("Unknown"),
    NONE          ("");

    private String text;

    TimeAccuracy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeAccuracy getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(EDUCATED_GUESS.text)) return EDUCATED_GUESS;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
