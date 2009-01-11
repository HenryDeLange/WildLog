package wildlog.data.enums;


public enum CateringType {
    SELF_CATERING("Self Catering"),
    NON_SELF_CATERING("Not Self Catering"),
    NO_CATERING("No Catering is Possible/Allowed"),
    NONE("None");
    
    private final String text;
    
    CateringType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
