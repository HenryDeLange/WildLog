package wildlog.data.enums;


public enum WildLogFileType {
    IMAGE("Image"),
    MOVIE("Movie"),
    OTHER("Other"),
    NONE("None");

    private String text;

    WildLogFileType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WildLogFileType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(IMAGE.text)) return IMAGE;
        if (inText.equalsIgnoreCase(MOVIE.text)) return MOVIE;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
