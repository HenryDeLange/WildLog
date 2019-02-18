package wildlog.data.enums;


public enum WishRating {
    EXCEPTIONAL("Very High - Exceptional"),
    NEED_GOOD_SIGHTING("High - Need good sighting"),
    HIGH("High - Always great to see"),
    NORMAL("Normal - Nice to see"),
    LOW("Low - Commonly seen"),
    NONE("");

    private final String text;

    WishRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WishRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WishRating theEnum : WishRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}