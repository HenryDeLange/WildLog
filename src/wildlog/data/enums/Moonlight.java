package wildlog.data.enums;


public enum Moonlight {
    MOON_SHINING("Moon Shining"),
    NO_MOON("No Moon"),
    UNKNOWN("Unknown"),
    NONE("");

    private String text;

    Moonlight(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Moonlight getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(MOON_SHINING.text)) return MOON_SHINING;
        if (inText.equalsIgnoreCase(NO_MOON.text)) return NO_MOON;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
