package wildlog.data.enums;


public enum ViewRating {
    VERY_GOOD("Very good"),
    GOOD("Good"),
    NORMAL("Normal"),
    BAD("Bad"),
    VERY_BAD("Very bad"),
    NONE("");

    private final String text;

    ViewRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ViewRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ViewRating theEnum : ViewRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
