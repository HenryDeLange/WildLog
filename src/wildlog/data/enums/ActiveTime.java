package wildlog.data.enums;


public enum ActiveTime {
    DAY("Day"),
    NIGHT("Night"),
    ALWAYS("Always"),
    DAWN_OR_DUST("Dawn or Dust"),
    NONE("None");
    
    private final String text;
    
    ActiveTime(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
