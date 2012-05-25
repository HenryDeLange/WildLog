package wildlog.data.enums;

public enum Latitudes {
    NORTH("N", "North (+)"),
    SOUTH("S", "South (-)"),
    NONE("", "None");

    private String key;
    private String text;

    Latitudes(String inKey, String inText) {
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

    public static Latitudes getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(NORTH.text)) return NORTH;
        if (inText.equalsIgnoreCase(NORTH.key)) return NORTH;
        if (inText.equalsIgnoreCase(SOUTH.text)) return SOUTH;
        if (inText.equalsIgnoreCase(SOUTH.key)) return SOUTH;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        if (inText.equalsIgnoreCase(NONE.key)) return NONE;
        return NONE;
    }
}
