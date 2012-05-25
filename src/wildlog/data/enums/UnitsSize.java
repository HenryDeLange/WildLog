package wildlog.data.enums;

public enum UnitsSize {
    METER("m"),
    CENTI_METER("cm"),
    NONE("None");

    private String text;

    UnitsSize(String inText) {
        text = inText;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static UnitsSize getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(METER.text)) return METER;
        if (inText.equalsIgnoreCase(CENTI_METER.text)) return CENTI_METER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
