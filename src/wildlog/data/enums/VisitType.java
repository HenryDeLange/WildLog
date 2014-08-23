package wildlog.data.enums;


public enum VisitType {
    VACATION("Vacation"),
    REMOTE_CAMERA("Camera Trap"),
    BIRD_ATLASSING("Census, Atlas, etc."),
    DAY_VISIT("Day Visit"),
    INCIDENTAL("Incidental"),
    OTHER("Other"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    VisitType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static VisitType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (VisitType theEnum : VisitType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
