package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum SightingEvidence implements EnumWithID {
    SEEN            ((byte)  1, "Seen"),
    TRACK           ((byte)  2, "Tracks"),
    SCAT            ((byte)  3, "Scat"),
    HAIR_ETC        ((byte)  4, "Body Covering"),
    MARKING         ((byte)  5, "Marking"),
    HEARD           ((byte)  6, "Heard"),
    SMELL           ((byte)  7, "Smell"),
    OTHER_EVIDENCE  ((byte)  8, "Other Evidence"),
    UNKNOWN         ((byte) -1, "Unknown"),
    NONE            ((byte)  0, "");

    private final byte id;
    private final String text;

    SightingEvidence(byte inID, String inText) {
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

    public static SightingEvidence getEnumFromID(byte inID) {
        if (inID == 0 || inID < -1 || inID >= SightingEvidence.values().length) {
            return NONE;
        }
        for (SightingEvidence theEnum : SightingEvidence.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static SightingEvidence getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (SightingEvidence theEnum : SightingEvidence.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
