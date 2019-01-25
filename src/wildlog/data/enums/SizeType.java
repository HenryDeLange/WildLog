package wildlog.data.enums;


public enum SizeType {
    BODY("Body"),
    SHOULDER("Shoulder"),
    TOTAL_LENGTH("Total Length"),
    TOTAL_HEIGHT("Total Height"),
    NONE("");

    private final String text;

    SizeType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SizeType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (SizeType theEnum : SizeType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
