package wildlog.data.enums;

public enum Longitudes {
    EAST("E", "East (+)"),
    WEST("W", "West (-)"),
    NONE("", "None");

    private final String key;
    private final String text;

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
}
