package wildlog.data.enums;


public enum WishRating {
    EXCEPTIONAL("Very High - Exceptional"),
    NEED_GOOD_SIGHTING("High - Need good sighting"),
    HIGH("High - Always great to see"),
    NORMAL("Normal - Nice to see"),
    LOW("Low - Commonly seen"),
    NONE("");

    private String text;

    WishRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WishRating getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(EXCEPTIONAL.text)) return EXCEPTIONAL;
        if (inText.equalsIgnoreCase(NEED_GOOD_SIGHTING.text)) return NEED_GOOD_SIGHTING;
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(NORMAL.text)) return NORMAL;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
