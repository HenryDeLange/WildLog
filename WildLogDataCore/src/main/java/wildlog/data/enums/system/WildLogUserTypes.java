package wildlog.data.enums.system;


public enum WildLogUserTypes {
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
