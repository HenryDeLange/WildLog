package wildlog.data.enums;


public enum Sex {
    MALE("Male"),
    FEMALE("Female"),
    MIXED("Mixed"),
    UNKNOWN("Unknown"),
    NONE("");

    private String text;

    Sex(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Sex getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(MALE.text)) return MALE;
        if (inText.equalsIgnoreCase(FEMALE.text)) return FEMALE;
        if (inText.equalsIgnoreCase(MIXED.text)) return MIXED;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
