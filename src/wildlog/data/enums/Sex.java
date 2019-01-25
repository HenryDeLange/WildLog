package wildlog.data.enums;


public enum Sex {
    MALE("Male"),
    FEMALE("Female"),
    MIXED("Mixed"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    Sex(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Sex getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Sex theEnum : Sex.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
