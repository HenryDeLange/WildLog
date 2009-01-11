package wildlog.data.enums;


public enum WishRating {
    HIGH("Always great to see"),
    NORMAL("Nice to see"),
    LOW("Commonly seen"),
    NONE("None");
    
    private final String text;
    
    WishRating(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
