package wildlog.data.enums;

public enum Longitudes {
    EAST("E", "East (+)"),
    WEST("W", "West (-)"),
    NONE("", "None");

    private String key;
    private String text;

    Longitudes(String inKey, String inText) {
        key = inKey;
        text = inText;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Longitudes getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(EAST.text)) return EAST;
        if (inText.equalsIgnoreCase(EAST.key)) return EAST;
        if (inText.equalsIgnoreCase(WEST.text)) return WEST;
        if (inText.equalsIgnoreCase(WEST.key)) return WEST;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        if (inText.equalsIgnoreCase(NONE.key)) return NONE;
        return NONE;
    }
}
