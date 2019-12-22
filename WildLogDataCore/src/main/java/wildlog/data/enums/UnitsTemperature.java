package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;

public enum UnitsTemperature implements EnumWithID {
    CELSIUS   ((byte) 1, "C", "Celsius"),
    FAHRENHEIT((byte) 2, "F", "Fahrenheit"),
    NONE      ((byte) 0, "",  "");

    private final byte id;
    private final String key;
    private final String text;

    UnitsTemperature(byte inID, String inKey, String inText) {
        id = inID;
        key = inKey;
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
    
    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public static UnitsTemperature getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= UnitsTemperature.values().length) {
            return NONE;
        }
        for (UnitsTemperature theEnum : UnitsTemperature.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static UnitsTemperature getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (UnitsTemperature theEnum : UnitsTemperature.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
