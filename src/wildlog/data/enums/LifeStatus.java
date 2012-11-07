package wildlog.data.enums;


public enum LifeStatus {
    ALIVE("Alive"),
    DEAD("Dead"),
    UNKNOWN("Unknown"),
    NONE("None");

    private String text;

    LifeStatus(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LifeStatus getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(ALIVE.text)) return ALIVE;
        if (inText.equalsIgnoreCase(DEAD.text)) return DEAD;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
