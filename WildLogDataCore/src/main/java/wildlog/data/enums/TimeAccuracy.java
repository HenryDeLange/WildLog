package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum TimeAccuracy implements EnumWithID {
    GOOD          ((byte)  1, "Good"),
    EDUCATED_GUESS((byte)  2, "Educated Guess"),
    BAD           ((byte)  3, "Bad"),
    UNKNOWN       ((byte) -1, "Unknown"),
    NONE          ((byte)  0, "");

    private final byte id;
    private final String text;

    TimeAccuracy(byte inID, String inText) {
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

    public static TimeAccuracy getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= TimeAccuracy.values().length) {
            return NONE;
        }
        for (TimeAccuracy theEnum : TimeAccuracy.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static TimeAccuracy getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (TimeAccuracy theEnum : TimeAccuracy.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

    /**
     * Checks whether the one of the values has been selected that indicates
     * that the time is accurate enough to use in calculations.
     * <br/>
     * GOOD and EDUCATED_GUESS will return true. BAD, UNKOWN and NONE rest will return false.
     * @return True if it is reasonably accurate, false otherwise.
     */
    public boolean isUsableTime() {
        if (GOOD.equals(this) || EDUCATED_GUESS.equals(this)) {
            return true;
        }
        return false;
    }
    
}
