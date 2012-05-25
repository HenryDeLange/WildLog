package wildlog.data.enums;


public enum WaterDependancy {
    VERY_HIGH("Always close to water"),
    HIGH("Needs access to water"),
    OPPORTUNISTIC("Not needed, but will use it"),
    LOW("Not effected/interisted"),
    NONE("None");
    
    private String text;
    
    WaterDependancy(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static WaterDependancy getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(VERY_HIGH.text)) return VERY_HIGH;
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(OPPORTUNISTIC.text)) return OPPORTUNISTIC;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
