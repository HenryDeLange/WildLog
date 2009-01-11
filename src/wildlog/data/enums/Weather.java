package wildlog.data.enums;


public enum Weather {
    SUNNY("Sunny"),
    LIGHT_OVERCAST("Few clouds - Not raining"),
    HEAVY_OVERCAST("Many clouds - Not raining"),
    MIST("Mist"),
    LIGHT_RAIN("Light rain"),
    HEAVY_RAIN("Heavy rain"),
    OTHER("Other (Can specify in details)"),
    NONE("None");
    
    private final String text;
    
    Weather(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
