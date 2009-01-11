package wildlog.data.enums;


public enum Habitat {
    NEED_MORE_WORK("Need to improve this...", "Maybe have two habitat types..."),
    OTHER("Other", "Other"),
    NONE("None", "None");
    
    private final String text;
    private final String description;
    
    Habitat(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String description() {
        return description;
    }

}
