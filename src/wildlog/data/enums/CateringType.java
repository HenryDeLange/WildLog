package wildlog.data.enums;


public enum CateringType {
    ONLY_SELF_CATERING("Only Self Catering"),
    ONLY_NON_CATERING("Only Not Self Catering"),
    ALL_CATERING("Both Self and Not Self Catering"),
    NO_CATERING("No Catering is Possible/Allowed"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    CateringType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static CateringType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (CateringType theEnum : CateringType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
