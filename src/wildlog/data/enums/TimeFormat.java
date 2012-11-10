package wildlog.data.enums;


public enum TimeFormat {
    H24("24"),
    AM("AM"),
    PM("PM"),
    UNKNOWN("Unknown"),
    NONE("None");

    private String text;

    TimeFormat(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeFormat getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(H24.text)) return H24;
        if (inText.equalsIgnoreCase(AM.text)) return AM;
        if (inText.equalsIgnoreCase(PM.text)) return PM;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
