package wildlog.data.enums;


public enum GameViewRating {
    GOOD("Good"),
    MEDIUM("Medium"),
    BAD("Bad"),
    NONE("");

    private String text;

    GameViewRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static GameViewRating getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(MEDIUM.text)) return MEDIUM;
        if (inText.equalsIgnoreCase(BAD.text)) return BAD;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
