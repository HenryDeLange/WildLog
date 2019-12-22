package wildlog.data.enums.system;

import wildlog.data.enums.utils.EnumWithKey;


public enum WildLogFileType implements EnumWithKey {
    IMAGE("I", "Image"),
    MOVIE("M", "Movie"),
    OTHER("O", "Other"),
    NONE ("",  "None");

    private final String key;
    private final String description;

    WildLogFileType(String inKey, String inDescription) {
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

    public static WildLogFileType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogFileType theEnum : WildLogFileType.values()) {
            if (theEnum.key.equalsIgnoreCase(inText) || theEnum.description.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
