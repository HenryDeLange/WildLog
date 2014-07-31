package wildlog.data.enums;


public enum Moonlight {
    MOON_SHINING("Moon Shining"),
    NO_MOON("No Moon"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    Moonlight(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Moonlight getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Moonlight theEnum : Moonlight.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
