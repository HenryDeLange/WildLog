package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum GameViewRating implements EnumWithID {
    GOOD  ((byte) 1, "Good"),
    MEDIUM((byte) 2, "Medium"),
    BAD   ((byte) 3, "Bad"),
    NONE  ((byte) 0, "");

    private final byte id;
    private final String text;

    GameViewRating(byte inID, String inText) {
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

    public static GameViewRating getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= GameViewRating.values().length) {
            return NONE;
        }
        for (GameViewRating theEnum : GameViewRating.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static GameViewRating getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (GameViewRating theEnum : GameViewRating.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
