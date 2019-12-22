package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum Age implements EnumWithID {
    PARENT_OFFSPRING_MIX((byte)  1, "Parent with Offspring"),
    IMMATURE            ((byte)  2, "Immature Offspring"),
    YOUNG_ADULT         ((byte)  3, "Young Adult"),
    ADULT               ((byte)  4, "Mature Adult"),
    OLD                 ((byte)  5, "Old Adult"),
    EGG_SEED_SPORE      ((byte)  6, "Egg, Seed, Spore, etc."),
    EARLY_LIFESTAGE     ((byte)  7, "Early Lifestage"),
    OTHER               ((byte)  8, "Other"),
    UNKNOWN             ((byte) -1, "Unknown"),
    NONE                ((byte)  0, "");

    private final byte id;
    private final String text;

    Age(byte inID, String inText) {
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
    
    public static Age getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= Age.values().length) {
            return NONE;
        }
        for (Age theEnum : Age.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }

    public static Age getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Age theEnum : Age.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
