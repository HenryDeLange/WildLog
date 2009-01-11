package wildlog.data.enums;


public enum WaterDependancy {
    VERY_HIGH("Always close to water"),
    HIGH("Needs access to water"),
    OPPORTUNISTIC("Not needed, but will use it"),
    LOW("Not effected/interisted"),
    NONE("None");
    
    private final String text;
    
    WaterDependancy(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
