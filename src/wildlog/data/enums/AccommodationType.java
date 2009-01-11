package wildlog.data.enums;


public enum AccommodationType {
    CAMPING("Camping"),
    SMALL_UNIT("Small Unit"),
    BIG_UNIT("Big Unit"),
    NONE("None");
    
    private final String text;
    
    AccommodationType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String test() {
        return text;
    }

}
