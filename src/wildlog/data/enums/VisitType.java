package wildlog.data.enums;


public enum VisitType {
    VACATION("Vacation"),
    REMOTE_CAMERA("Remote Camera"),
    BIRD_ATLASSING("Bird Atlassing"),
    OTHER("Other"),
    NONE("None");
    
    private final String text;
    
    VisitType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
