package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum LocationRating implements EnumWithID {
    HIGH    ((byte) 1, "Very Nice"),
    NORMAL  ((byte) 2, "Nice"),
    DECENT  ((byte) 3, "Decent"),
    LOW     ((byte) 4, "Bad"),
    NONE    ((byte) 0, "");

    private final byte id;
    private final String text;

    LocationRating(byte inID, String inText) {
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

    public static LocationRating getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= LocationRating.values().length) {
            return NONE;
        }
        for (LocationRating theEnum : LocationRating.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static LocationRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (LocationRating theEnum : LocationRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
