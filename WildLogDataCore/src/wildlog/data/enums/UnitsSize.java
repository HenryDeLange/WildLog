package wildlog.data.enums;

public enum UnitsSize {
    METER("m"),
    CENTI_METER("cm"),
    FEET("ft"),
    INCHES("in"),
    NONE("");

    private final String text;

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
        if (inText == null) {
            return NONE;
        }
        for (UnitsSize theEnum : UnitsSize.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
