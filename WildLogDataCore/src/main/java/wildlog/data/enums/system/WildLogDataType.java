package wildlog.data.enums.system;

import wildlog.data.enums.utils.EnumWithKey;


public enum WildLogDataType implements EnumWithKey {
    ELEMENT         ("E", "ELEMENTS"),
    LOCATION        ("L", "LOCATIONS"),
    VISIT           ("V", "VISITS"),
    SIGHTING        ("S", "SIGHTINGS"),
    FILE            ("F", "FILES"),
    STASH           ("T", "STASH"),
    EXTRA           ("X", "EXTRA"),
    ADHOC           ("A", "ADHOC"),
    WILDLOG_USER    ("U", "USERS"),
    WILDLOG_OPTIONS ("W", "OPTIONS"),
    DELETE_LOG      ("D", "DELETELOG"),
    NONE            ("",  "");

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

    @Override
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }

    public static WildLogDataType getEnumFromText(String inValue) {
        if (inValue == null) {
            return NONE;
        }
        for (WildLogDataType theEnum : WildLogDataType.values()) {
            if (theEnum.key.equalsIgnoreCase(inValue) || theEnum.description.equalsIgnoreCase(inValue)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
