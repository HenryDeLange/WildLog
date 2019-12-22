package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithKey;


public enum Longitudes implements EnumWithKey {
    /**
     * For (+) coordinates.
     */
    EAST("E", "East (+)", "+"),
    /**
     * For (-) coordinates.
     */
    WEST("W", "West (-)", "-"),
    NONE("",  "None",     "");

    private final String key;
    private final String text;
    private final String sign;

    Longitudes(String inKey, String inText, String inSign) {
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
    
    public static Longitudes getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Longitudes theEnum : Longitudes.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText) || theEnum.sign.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
