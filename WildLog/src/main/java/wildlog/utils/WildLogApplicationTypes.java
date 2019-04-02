package wildlog.utils;

public enum WildLogApplicationTypes {
    WILDLOG("MyWild Edition"),
    WILDLOG_WEI_ADMIN("WEI Admin"),
    WILDLOG_WEI_VOLUNTEER("WEI Volunteer");
    
    private final String edition;

    private WildLogApplicationTypes(String inEdition) {
        edition = inEdition;
    }

    public String getEdition() {
        return edition;
    }
    
}
