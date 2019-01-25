package wildlog.data.enums;


public enum GameWatchIntensity {
    VERY_HIGH("Heavily Focused"),
    HIGH("Focused"),
    MEDIUM("Lightly Focused"),
    LOW("Not Realy Focused"),
    VERY_LOW("No Focus"),
    NONE("");

    private final String text;

    GameWatchIntensity(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static GameWatchIntensity getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (GameWatchIntensity theEnum : GameWatchIntensity.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
