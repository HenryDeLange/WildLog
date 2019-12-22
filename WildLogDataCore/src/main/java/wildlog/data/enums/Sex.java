package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum Sex implements EnumWithID {
    MALE    ((byte)  1, "Male"),
    FEMALE  ((byte)  2, "Female"),
    MIXED   ((byte)  3, "Mixed"),
    UNKNOWN ((byte) -1, "Unknown"),
    NONE    ((byte)  0, "");

    private final byte id;
    private final String text;

    Sex(byte inID, String inText) {
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

    public static Sex getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= Sex.values().length) {
            return NONE;
        }
        for (Sex theEnum : Sex.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static Sex getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Sex theEnum : Sex.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
