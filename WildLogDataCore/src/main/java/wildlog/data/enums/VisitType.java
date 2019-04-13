package wildlog.data.enums;


public enum VisitType {
    VACATION("Vacation"),
    REMOTE_CAMERA("Camera Trap"),
    MICROSCOPE("Microscope"),
    MOTHLIGHT("Moth Light"),
    DAY_VISIT("Day Visit"),
    INCIDENTAL("Incidental"),
    ATLASSING("Census, Atlas, etc."),
    OTHER("Other"),
    UNKNOWN("Unknown"),
    STASHED("Stashed Files"),
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
    
    public static VisitType[] valuesForDroplist() {
        VisitType[] values = new VisitType[values().length - 1];
        int i = 0;
        for (VisitType visitType : values()) {
            if (visitType != STASHED) {
                values[i++] = visitType;
            }
        }
        return values;
    }

}
