package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithKey;


public enum Latitudes implements EnumWithKey {
    /**
     * For (+) coordinates.
     */
    NORTH("N", "North (+)", "+"),
    /**
     * For (-) coordinates.
     */
    SOUTH("S", "South (-)", "-"),
    NONE ("",  "None",      "");

    private final String key;
    private final String text;
    private final String sign;

    Latitudes(String inKey, String inText, String inSign) {
        key = inKey;
        text = inText;
        sign = inSign;
    }

    @Override
    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Latitudes getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Latitudes theEnum : Latitudes.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText) || theEnum.sign.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
