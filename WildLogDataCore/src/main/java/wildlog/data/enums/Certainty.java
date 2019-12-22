package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum Certainty implements EnumWithID {
    SURE    ((byte)  1, "100%     (Sure)"),
    GOOD    ((byte)  2, "90 - 99% (Good)"),
    BAD     ((byte)  3, "70 - 90% (Bad)"),
    VERY_BAD((byte)  4, " 0 - 70% (Unsure)"),
    UNKNOWN ((byte) -1, "Unknown"),
    NONE    ((byte)  0, "");

    private final byte id;
    private final String text;

    Certainty(byte inID, String inText) {
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

    public static Certainty getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= Certainty.values().length) {
            return NONE;
        }
        for (Certainty theEnum : Certainty.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static Certainty getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Certainty theEnum : Certainty.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
