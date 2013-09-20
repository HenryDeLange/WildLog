package wildlog.data.enums;


public enum ActiveTimeSpesific {
    EARLY_MORNING("Early Morning - Before the sun"),
    MORNING("Morning - Just after the sun"),
    MID_MORNING("Mid Morning - Before mid day"),
    MIDDAY("Mid Day - Heat of the day"),
    MID_AFTERNOON("Mid Afternoon - After mid day"),
    AFTERNOON("Afternoon - Before sunset"),
    LATE_AFTERNOON("Late Afternoon - Just after sunset"),
    EARLY_NIGHT("Early Night - Afternoon Twilight"),
    DEEP_NIGHT("Night - Darkness of night"),
    LATE_NIGHT("Late Night - Morning Twilight"),
    NONE("");

    private String text;

    ActiveTimeSpesific(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ActiveTimeSpesific getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(EARLY_MORNING.text)) return EARLY_MORNING;
        if (inText.equalsIgnoreCase(MORNING.text)) return MORNING;
        if (inText.equalsIgnoreCase(MID_MORNING.text)) return MID_MORNING;
        if (inText.equalsIgnoreCase(MIDDAY.text)) return MIDDAY;
        if (inText.equalsIgnoreCase(MID_AFTERNOON.text)) return MID_AFTERNOON;
        if (inText.equalsIgnoreCase(AFTERNOON.text)) return AFTERNOON;
        if (inText.equalsIgnoreCase(LATE_AFTERNOON.text)) return LATE_AFTERNOON;
        if (inText.equalsIgnoreCase(EARLY_NIGHT.text)) return EARLY_NIGHT;
        if (inText.equalsIgnoreCase(DEEP_NIGHT.text)) return DEEP_NIGHT;
        if (inText.equalsIgnoreCase(LATE_NIGHT.text)) return LATE_NIGHT;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
