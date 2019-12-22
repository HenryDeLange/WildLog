package wildlog.data.enums.system;

import wildlog.data.enums.utils.EnumWithKey;


public enum WildLogUserTypes implements EnumWithKey {
    WILDLOG_MASTER  ("M", "Master"),
    OWNER           ("O", "Owner"),
    ADMIN           ("A", "Admin"),
    STUDENT         ("S", "Student"),
    VOLUNTEER       ("V", "Volunteer"),
    NONE            ("",  "");

    private final String key;
    private final String description;

    WildLogUserTypes(String inKey, String inDescription) {
        key = inKey;
        description = inDescription;
    }

    @Override
    public String toString() {
        return description;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }

    public static WildLogUserTypes getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogUserTypes theEnum : WildLogUserTypes.values()) {
            if (theEnum.key.equalsIgnoreCase(inText) || theEnum.description.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
