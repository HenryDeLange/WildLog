package wildlog.data.enums;

public enum UnitsWeight {
    KILOGRAM("kg"),
    GRAM("g"),
    OUNCES("os"),
    POUNDS("pd"),
    NONE("");

    private final String text;

    UnitsWeight(String inText) {
        text = inText;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static UnitsWeight getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (UnitsWeight theEnum : UnitsWeight.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
