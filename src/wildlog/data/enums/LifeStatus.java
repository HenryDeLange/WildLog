package wildlog.data.enums;


public enum LifeStatus {
    ALIVE("Alive"),
    DEAD("Dead"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    LifeStatus(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LifeStatus getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (LifeStatus theEnum : LifeStatus.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
