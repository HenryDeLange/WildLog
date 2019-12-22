package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum LifeStatus implements EnumWithID {
    ALIVE   ((byte)  1, "Alive"),
    DEAD    ((byte)  2, "Dead"),
    UNKNOWN ((byte) -1, "Unknown"),
    NONE    ((byte)  0, "");

    private final byte id;
    private final String text;

    LifeStatus(byte inID, String inText) {
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

    public static LifeStatus getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= LifeStatus.values().length) {
            return NONE;
        }
        for (LifeStatus theEnum : LifeStatus.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static LifeStatus getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (LifeStatus theEnum : LifeStatus.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
