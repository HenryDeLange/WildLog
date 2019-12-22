package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum ElementType implements EnumWithID {
    MAMMAL      ((byte)  1, "Mammal"),
    BIRD        ((byte)  2, "Bird"),
    REPTILE     ((byte)  3, "Reptile"),
    AMPHIBIAN   ((byte)  4, "Amphibian"),
    FISH        ((byte)  5, "Fish"),
    INSECT      ((byte)  6, "Insect"),
    PLANT       ((byte)  7, "Plant"),
    OTHER       ((byte)  8, "Other"),
    UNKNOWN     ((byte) -1, "Unknown"),
    NONE        ((byte)  0, "");

    private final byte id;
    private final String text;

    ElementType(byte inID, String inText) {
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
    
    public static ElementType getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= ElementType.values().length) {
            return NONE;
        }
        for (ElementType theEnum : ElementType.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }

    public static ElementType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ElementType theEnum : ElementType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
