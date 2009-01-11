package wildlog.data.enums;


public enum GameWatchIntensity {
    VERY_HIGH("Heavily Focused"),
    HIGH("Focused"),
    MEDIUM("Lightly Focused"),
    LOW("Not Realy Focus"),
    VERY_LOW("No Focus"),
    NONE("None");
            
    private final String text;
    
    GameWatchIntensity(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
