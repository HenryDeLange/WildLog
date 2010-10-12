package wildlog.data.enums;


public enum ActiveTime {
    DAY("Day"),
    NIGHT("Night"),
    ALWAYS("Always"),
    DAWN_OR_DUST("Dawn or Dust"),
    NONE("None");
    
    private String text;
    
    ActiveTime(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static ActiveTime getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(DAY.text)) return DAY;
        if (inText.equalsIgnoreCase(NIGHT.text)) return NIGHT;
        if (inText.equalsIgnoreCase(ALWAYS.text)) return ALWAYS;
        if (inText.equalsIgnoreCase(DAWN_OR_DUST.text)) return DAWN_OR_DUST;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
