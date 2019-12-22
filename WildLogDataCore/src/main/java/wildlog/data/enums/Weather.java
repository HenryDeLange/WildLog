package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum Weather implements EnumWithID {
    SUNNY           ((byte)  1, "Sunny"),
    LIGHT_OVERCAST  ((byte)  2, "Few clouds - Not raining"),
    HEAVY_OVERCAST  ((byte)  3, "Many clouds - Not raining"),
    MIST            ((byte)  4, "Mist"),
    LIGHT_RAIN      ((byte)  5, "Light rain"),
    HEAVY_RAIN      ((byte)  6, "Heavy rain"),
    OTHER           ((byte)  7, "Other (Can specify in details)"),
    UNKNOWN         ((byte) -1, "Unknown"),
    NONE            ((byte)  0, "");

    private final byte id;
    private final String text;

    Weather(byte inID, String inText) {
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

    public static Weather getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= Weather.values().length) {
            return NONE;
        }
        for (Weather theEnum : Weather.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static Weather getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Weather theEnum : Weather.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
