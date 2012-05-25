package wildlog.data.enums;


public enum LocationRating {
    HIGH("Very Nice"),
    NORMAL("Nice"),
    DECENT("Decent"),
    LOW("Bad"),
    NONE("None");

    private String text;

    LocationRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LocationRating getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(NORMAL.text)) return NORMAL;
        if (inText.equalsIgnoreCase(DECENT.text)) return DECENT;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
