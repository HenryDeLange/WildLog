package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum Moonlight implements EnumWithID {
    MOON_SHINING((byte)  1, "Moon Shining"),
    NO_MOON     ((byte)  2, "No Moon"),
    UNKNOWN     ((byte) -1, "Unknown"),
    NONE        ((byte)  0, "");

    private final byte id;
    private final String text;

    Moonlight(byte inID, String inText) {
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

    public static Moonlight getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= Moonlight.values().length) {
            return NONE;
        }
        for (Moonlight theEnum : Moonlight.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static Moonlight getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Moonlight theEnum : Moonlight.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
