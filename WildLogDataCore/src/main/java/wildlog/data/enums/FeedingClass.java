package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum FeedingClass implements EnumWithID {
    CARNIVORE     ((byte)  1, "Carnivore"),
    HERBIVORE     ((byte)  2, "Herbivore"),
    OMNIVORE      ((byte)  3, "Omnivore"),
    PARASITE      ((byte)  4, "Parasite"),
    PHOTOSYNTHESYS((byte)  5, "Photo-Synthesis"),
    OTHER         ((byte)  6, "Other"),
    UNKNOWN       ((byte) -1, "Unknown"),
    NONE          ((byte)  0, "");

    private final byte id;
    private final String text;

    FeedingClass(byte inID, String inText) {
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

    public static FeedingClass getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= FeedingClass.values().length) {
            return NONE;
        }
        for (FeedingClass theEnum : FeedingClass.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static FeedingClass getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (FeedingClass theEnum : FeedingClass.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
