package wildlog.data.enums;

public enum UnitsWeight {
    KILOGRAM("kg"),
    GRAM("g"),
    NONE("None");

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

}
