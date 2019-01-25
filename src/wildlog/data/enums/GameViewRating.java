package wildlog.data.enums;


public enum GameViewRating {
    GOOD("Good"),
    MEDIUM("Medium"),
    BAD("Bad"),
    NONE("");

    private final String text;

    GameViewRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static GameViewRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (GameViewRating theEnum : GameViewRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
