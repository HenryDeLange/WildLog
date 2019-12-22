package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;


public enum EndangeredStatus implements EnumWithID {
    EX  ((byte) 1, "Ex", "Extinct"),
    EW  ((byte) 2, "Ew", "Extinct in Wild"),
    CR  ((byte) 3, "Cr", "Critically Endangered"),
    EN  ((byte) 4, "En", "Endangered"),
    VU  ((byte) 5, "Vu", "Vunerable"),
    NT  ((byte) 6, "Nt", "Near Threatened"),
    LC  ((byte) 7, "Lc", "Least Concern"),
    NONE((byte) 0, "",   "");

    private final byte id;
    private final String text;
    private final String key;

    EndangeredStatus(byte inID, String inKey, String inText) {
        id = inID;
        text = inText;
        key = inKey;
    }

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public byte getID() {
        return id;
    }

    public String key() {
        return key;
    }
    
    public static EndangeredStatus getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= EndangeredStatus.values().length) {
            return NONE;
        }
        for (EndangeredStatus theEnum : EndangeredStatus.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }

    public static EndangeredStatus getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (EndangeredStatus theEnum : EndangeredStatus.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
