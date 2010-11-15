package wildlog.data.enums;


public enum Moonlight {
    MOON_SHINING("Moon Shining"),
    NO_MOON("No Moon"),
    NONE("None");

    private String text;

    Moonlight(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Moonlight getEnumFromText(String inText) {
        if (inText != null) {
            if (inText.equalsIgnoreCase(MOON_SHINING.text)) return MOON_SHINING;
            if (inText.equalsIgnoreCase(NO_MOON.text)) return NO_MOON;
            if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        }
        return NONE;
    }
}
