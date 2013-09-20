package wildlog.data.enums;


public enum CateringType {
    ONLY_SELF_CATERING("Only Self Catering"),
    ONLY_NON_CATERING("Only Not Self Catering"),
    ALL_CATERING("Both Self and Not Self Catering"),
    NO_CATERING("No Catering is Possible/Allowed"),
    UNKNOWN("Unknown"),
    NONE("");

    private String text;

    CateringType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static CateringType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(ONLY_SELF_CATERING.text)) return ONLY_SELF_CATERING;
        if (inText.equalsIgnoreCase(ONLY_NON_CATERING.text)) return ONLY_NON_CATERING;
        if (inText.equalsIgnoreCase(ALL_CATERING.text)) return ALL_CATERING;
        if (inText.equalsIgnoreCase(NO_CATERING.text)) return NO_CATERING;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
