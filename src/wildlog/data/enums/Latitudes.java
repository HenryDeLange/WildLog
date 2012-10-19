package wildlog.data.enums;

public enum Latitudes {
    NORTH("N", "North (+)", "+"),
    SOUTH("S", "South (-)", "-"),
    NONE("", "None", "");

    private String key;
    private String text;
    private String sign;

    Latitudes(String inKey, String inText, String inSign) {
        key = inKey;
        text = inText;
        sign = inSign;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getSign() {
        return sign;
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
