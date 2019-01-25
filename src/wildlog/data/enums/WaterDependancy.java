package wildlog.data.enums;


public enum WaterDependancy {
    VERY_HIGH("Always close to water"),
    HIGH("Needs access to water"),
    OPPORTUNISTIC("Not needed, but will use it"),
    LOW("Not effected/interisted"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    WaterDependancy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static WaterDependancy getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (WaterDependancy theEnum : WaterDependancy.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
