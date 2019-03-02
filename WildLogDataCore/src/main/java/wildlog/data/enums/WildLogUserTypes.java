package wildlog.data.enums;


public enum WildLogUserTypes {
    WILDLOG_MASTER("Master"),
    OWNER("Owner"),
    ADMIN("Admin"),
    VOLUNTEER("Volunteer"),
    NONE("");

    private final String text;

    WildLogUserTypes(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WildLogUserTypes getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WildLogUserTypes theEnum : WildLogUserTypes.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
}
