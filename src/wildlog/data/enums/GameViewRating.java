package wildlog.data.enums;


public enum GameViewRating {
    GOOD("Good"),
    MEDIUM("Medium"),
    BAD("Bad"),
    NONE("None");
    
    private final String text;
    
    GameViewRating(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
