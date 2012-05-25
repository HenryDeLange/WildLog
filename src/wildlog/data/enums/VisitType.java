package wildlog.data.enums;


public enum VisitType {
    VACATION("Vacation"),
    REMOTE_CAMERA("Remote Camera"),
    BIRD_ATLASSING("Bird Atlassing"),
    DAY_VISIT("Day Visit"),
    OTHER("Other"),
    NONE("None");
    
    private String text;
    
    VisitType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static VisitType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(VACATION.text)) return VACATION;
        if (inText.equalsIgnoreCase(REMOTE_CAMERA.text)) return REMOTE_CAMERA;
        if (inText.equalsIgnoreCase(BIRD_ATLASSING.text)) return BIRD_ATLASSING;
        if (inText.equalsIgnoreCase(DAY_VISIT.text)) return DAY_VISIT;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
