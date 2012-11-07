package wildlog.data.enums;


public enum SizeType {
    BODY("Body"),
    SHOULDER("Shoulder"),
    TOTAL_LENGTH("Total Length"),
    TOTAL_HEIGHT("Total Height"),
    NONE("None");

    private String text;

    SizeType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SizeType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(BODY.text)) return BODY;
        if (inText.equalsIgnoreCase(SHOULDER.text)) return SHOULDER;
        if (inText.equalsIgnoreCase(TOTAL_LENGTH.text)) return TOTAL_LENGTH;
        if (inText.equalsIgnoreCase(TOTAL_HEIGHT.text)) return TOTAL_HEIGHT;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
