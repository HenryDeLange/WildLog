package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum VisitType implements EnumWithID {
    VACATION        ((byte)  1, "Vacation"),
    REMOTE_CAMERA   ((byte)  2, "Camera Trap"),
    MICROSCOPE      ((byte)  3, "Microscope"),
    MOTHLIGHT       ((byte)  4, "Moth Light"),
    DAY_VISIT       ((byte)  5, "Day Visit"),
    INCIDENTAL      ((byte)  6, "Incidental"),
    ATLASSING       ((byte)  7, "Census, Atlas, etc."),
    OTHER           ((byte)  8, "Other"),
    STASHED         ((byte)  9, "Stashed Files"),
    UNKNOWN         ((byte) -1, "Unknown"),
    NONE            ((byte)  0, "");

    private final byte id;
    private final String text;

    VisitType(byte inID, String inText) {
        id = inID;
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public byte getID() {
        return id;
    }

    public static VisitType getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= VisitType.values().length) {
            return NONE;
        }
        for (VisitType theEnum : VisitType.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
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
