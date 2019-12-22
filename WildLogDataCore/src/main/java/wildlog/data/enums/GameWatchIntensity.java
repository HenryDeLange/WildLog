package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum GameWatchIntensity implements EnumWithID {
    VERY_HIGH((byte) 1, "Heavily Focused"),
    HIGH     ((byte) 2, "Focused"),
    MEDIUM   ((byte) 3, "Lightly Focused"),
    LOW      ((byte) 4, "Not Realy Focused"),
    VERY_LOW ((byte) 5, "No Focus"),
    NONE     ((byte) 0, "");

    private final byte id;
    private final String text;

    GameWatchIntensity(byte inID, String inText) {
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

    public static GameWatchIntensity getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= GameWatchIntensity.values().length) {
            return NONE;
        }
        for (GameWatchIntensity theEnum : GameWatchIntensity.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static GameWatchIntensity getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (GameWatchIntensity theEnum : GameWatchIntensity.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
