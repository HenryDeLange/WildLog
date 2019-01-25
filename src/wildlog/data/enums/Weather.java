package wildlog.data.enums;


public enum Weather {
    SUNNY("Sunny"),
    LIGHT_OVERCAST("Few clouds - Not raining"),
    HEAVY_OVERCAST("Many clouds - Not raining"),
    MIST("Mist"),
    LIGHT_RAIN("Light rain"),
    HEAVY_RAIN("Heavy rain"),
    OTHER("Other (Can specify in details)"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    Weather(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Weather getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Weather theEnum : Weather.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
