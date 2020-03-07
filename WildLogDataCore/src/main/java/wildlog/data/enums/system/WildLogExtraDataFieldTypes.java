package wildlog.data.enums.system;

import wildlog.data.enums.utils.EnumWithKey;


public enum WildLogExtraDataFieldTypes implements EnumWithKey {
    WILDLOG("W", "WildLog"),
    USER("U", "User"),
    NONE ("",  "");

    private final String key;
    private final String description;

    WildLogExtraDataFieldTypes(String inKey, String inDescription) {
        key = inKey;
        description = inDescription;
    }

    @Override
    public String toString() {
        return key;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }

    public static WildLogExtraDataFieldTypes getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogExtraDataFieldTypes theEnum : WildLogExtraDataFieldTypes.values()) {
            if (theEnum.key.equalsIgnoreCase(inText) || theEnum.description.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
