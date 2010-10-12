package wildlog.data.enums;

public enum UnitsWeight {
    KILOGRAM("kg"),
    GRAM("g"),
    NONE("None");

    private String text;

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
        if (inText.equalsIgnoreCase(KILOGRAM.text)) return KILOGRAM;
        if (inText.equalsIgnoreCase(GRAM.text)) return GRAM;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
