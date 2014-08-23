package wildlog.data.enums;


public enum SightingEvidence {
    SEEN("Seen"),
    HEARD("Heard"),
    OTHER_EVIDENCE("Other Evidence"),
    UNKNOWN("Unknown"),
    NONE("");


    private final String text;

    SightingEvidence(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SightingEvidence getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (SightingEvidence theEnum : SightingEvidence.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
