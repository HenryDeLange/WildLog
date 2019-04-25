package wildlog.data.enums;


public enum WildLogFileLinkType {
    ELEMENT("E"),
    LOCATION("L"),
    VISIT("V"),
    SIGHTING("S"),
    NONE("");

    private final String text;

    WildLogFileLinkType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WildLogFileLinkType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogFileLinkType theEnum : WildLogFileLinkType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
