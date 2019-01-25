package wildlog.data.enums;


public enum Certainty {
    SURE    ("100%     (Sure)"),
    GOOD    ("90 - 99% (Good)"),
    BAD     ("70 - 90% (Bad)"),
    VERY_BAD(" 0 - 70% (Unsure)"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    Certainty(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Certainty getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Certainty theEnum : Certainty.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
