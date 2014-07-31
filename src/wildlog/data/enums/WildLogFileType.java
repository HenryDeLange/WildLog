package wildlog.data.enums;


public enum WildLogFileType {
    IMAGE("Image"),
    MOVIE("Movie"),
    OTHER("Other"),
    NONE("None");

    private final String text;

    WildLogFileType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WildLogFileType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogFileType theEnum : WildLogFileType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
