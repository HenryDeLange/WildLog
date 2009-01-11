package wildlog.data.enums;

public enum Latitudes {
    NORTH("N", "North (+)"),
    SOUTH("S", "South (-)"),
    NONE("", "None");

    private final String key;
    private final String text;

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
}
