package wildlog.data.enums;


public enum LocationRating {
    HIGH("Very Nice"),
    NORMAL("Nice"),
    DECENT("Decent"),
    LOW("Bad"),
    NONE("");

    private final String text;

    LocationRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LocationRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (LocationRating theEnum : LocationRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
