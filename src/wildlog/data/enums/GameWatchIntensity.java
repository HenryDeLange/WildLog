package wildlog.data.enums;


public enum GameWatchIntensity {
    VERY_HIGH("Heavily Focused"),
    HIGH("Focused"),
    MEDIUM("Lightly Focused"),
    LOW("Not Realy Focused"),
    VERY_LOW("No Focus"),
    NONE("None");
            
    private String text;
    
    GameWatchIntensity(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static GameWatchIntensity getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(VERY_HIGH.text)) return VERY_HIGH;
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(MEDIUM.text)) return MEDIUM;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(VERY_LOW.text)) return VERY_LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
