package wildlog.data.enums;


public enum SightingEvidence {
    SEEN("Seen"),
    TRACK("Tracks"),
    SCAT("Scat"),
    HAIR_ETC("Body Covering"),
    MARKING("Marking"),
    HEARD("Heard"),
    SMELL("Smell"),
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
