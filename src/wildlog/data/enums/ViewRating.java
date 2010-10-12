package wildlog.data.enums;


public enum ViewRating {
    VERY_GOOD("Very good"),
    GOOD("Good"),
    NORMAL("Normal"),
    BAD("Bad"),
    VERY_BAD("Very bad"),
    NONE("None");
    
    private String text;
    
    ViewRating(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static ViewRating getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(VERY_GOOD.text)) return VERY_GOOD;
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(NORMAL.text)) return NORMAL;
        if (inText.equalsIgnoreCase(BAD.text)) return BAD;
        if (inText.equalsIgnoreCase(VERY_BAD.text)) return VERY_BAD;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
