package wildlog.data.enums;


public enum ViewRating {
    VERY_GOOD("Very good"),
    GOOD("Good"),
    NORMAL("Normal"),
    BAD("Bad"),
    VERY_BAD("Very bad"),
    NONE("None");
    
    private final String text;
    
    ViewRating(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
