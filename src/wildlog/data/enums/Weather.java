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

    private String text;

    Weather(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Weather getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(SUNNY.text)) return SUNNY;
        if (inText.equalsIgnoreCase(LIGHT_OVERCAST.text)) return LIGHT_OVERCAST;
        if (inText.equalsIgnoreCase(HEAVY_OVERCAST.text)) return HEAVY_OVERCAST;
        if (inText.equalsIgnoreCase(MIST.text)) return MIST;
        if (inText.equalsIgnoreCase(LIGHT_RAIN.text)) return LIGHT_RAIN;
        if (inText.equalsIgnoreCase(HEAVY_RAIN.text)) return HEAVY_RAIN;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
