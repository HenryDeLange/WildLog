package wildlog.data.enums;


public enum TimeFormat {
    H24("24"),
    AM("AM"),
    PM("PM"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    TimeFormat(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeFormat getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (TimeFormat theEnum : TimeFormat.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
