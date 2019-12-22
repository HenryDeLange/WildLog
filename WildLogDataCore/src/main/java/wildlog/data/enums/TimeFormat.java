package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum TimeFormat implements EnumWithID {
    H24     ((byte)  1, "24"),
    AM      ((byte)  2, "AM"),
    PM      ((byte)  3, "PM"),
    UNKNOWN ((byte) -1, "Unknown"),
    NONE    ((byte)  0, "");

    private final byte id;
    private final String text;

    TimeFormat(byte inID, String inText) {
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

    public static TimeFormat getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= TimeFormat.values().length) {
            return NONE;
        }
        for (TimeFormat theEnum : TimeFormat.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static TimeFormat getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (TimeFormat theEnum : TimeFormat.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
