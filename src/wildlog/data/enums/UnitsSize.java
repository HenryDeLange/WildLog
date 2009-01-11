package wildlog.data.enums;

public enum UnitsSize {
    METER("m"),
    CENTI_METER("cm"),
    NONE("None");

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

}
