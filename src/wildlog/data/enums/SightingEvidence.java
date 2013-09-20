package wildlog.data.enums;


public enum SightingEvidence {
    SEEN("Seen"),
    HEARD("Heard"),
    OTHER_EVIDENCE("Other Evidence"),
    NONE("");


    private String text;

    SightingEvidence(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SightingEvidence getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(SEEN.text)) return SEEN;
        if (inText.equalsIgnoreCase(HEARD.text)) return HEARD;
        if (inText.equalsIgnoreCase(OTHER_EVIDENCE.text)) return OTHER_EVIDENCE;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
