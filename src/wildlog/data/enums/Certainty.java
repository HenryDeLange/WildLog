package wildlog.data.enums;


public enum Certainty {
    SURE    ("100%     (Sure)"),
    GOOD    ("90 - 99% (Good)"),
    BAD     ("70 - 90% (Bad)"),
    VERY_BAD(" 0 - 70% (Unsure)"),
    NONE("");

    private String text;

    Certainty(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Certainty getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(SURE.text)) return SURE;
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(BAD.text)) return BAD;
        if (inText.equalsIgnoreCase(VERY_BAD.text)) return VERY_BAD;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
