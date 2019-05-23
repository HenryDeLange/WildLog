package wildlog.data.enums;


public enum WildLogDataType {
    ELEMENT("E", "ELEMENTS"),
    LOCATION("L", "LOCATIONS"),
    VISIT("V", "VISITS"),
    SIGHTING("S", "SIGHTINGS"),
    FILE("F", "FILES"),
    //EXTRA("X", "EXTRA"), // TODO: To be used for the planned custom key-value data that can be linked to the core DTOs
    ADHOC("A", "ADHOC"),
    WILDLOG_USER("U", "USERS"),
    WILDLOG_OPTIONS("W", "OPTIONS"),
    DELETE_LOG("D", "DELETE_LOG"),
    NONE("", "");

    private final String key;
    private final String description;

    WildLogDataType(String inText, String inDescription) {
        key = inText;
        description = inDescription;
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }

    public static WildLogDataType getEnumFromText(String inKey) {
        if (inKey == null) {
            return NONE;
        }
        for (WildLogDataType theEnum : WildLogDataType.values()) {
            if (theEnum.key.equalsIgnoreCase(inKey)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
