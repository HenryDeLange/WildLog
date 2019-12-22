package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum ViewRating implements EnumWithID {
    VERY_GOOD((byte) 1, "Very good"),
    GOOD     ((byte) 2, "Good"),
    NORMAL   ((byte) 3, "Normal"),
    BAD      ((byte) 4, "Bad"),
    VERY_BAD ((byte) 5, "Very bad"),
    NONE     ((byte) 0, "");

    private final byte id;
    private final String text;

    ViewRating(byte inID, String inText) {
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

    public static ViewRating getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= ViewRating.values().length) {
            return NONE;
        }
        for (ViewRating theEnum : ViewRating.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static ViewRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ViewRating theEnum : ViewRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
