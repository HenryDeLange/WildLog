package wildlog.data.enums;


public enum ActiveTimeSpesific {
    EARLY_MORNING("Early Morning - Before the sun"),
    MORNING("Morning - Just after the sun"),
    MID_MORNING("Mid Morning - Before mid day"),
    MIDDAY("Mid Day - Heat of the day"),
    MID_AFTERNOON("Mid Afternoon - After mid day"),
    AFTERNOON("Afternoon - Before sunset"),
    LATE_AFTERNOON("Late Afternoon - Just after sunset"),
    DEEP_NIGHT("Night - Darkness of night"),
    NONE("None");

    private final String text;

    ActiveTimeSpesific(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

}
