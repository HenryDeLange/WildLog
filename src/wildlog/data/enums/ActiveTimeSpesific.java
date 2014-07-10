package wildlog.data.enums;


public enum ActiveTimeSpesific {
    MORNING_TWILIGHT(   "Morning Twilight",     "After dark night and before good light."),
    MORNING_SUNRISE(    "Morning Sunrise",      "From good light to when sunrise ends."),
    MORNING_EARLY(      "Early Morning",        "When sunrise ends and before mid morning starts."),
    MORNING_MID(        "Mid Morning",          "Before mid day."),
    DAY_MID(            "Mid Day",              "Middel of the day."),
    AFTERNOON_MID(      "Mid Afternoon",        "After mid day."),
    AFTERNOON_LATE(     "Late Afternoon",       "After mid afternoon ends and when sunset starts."),
    AFTERNOON_SUNSET(   "Afternoon Sunset",     "From sunset starts to when good light ends."),
    AFTERNOON_TWILIGHT( "Afternoon Twilight",   "After good light and before dark night."),
    NIGHT_EARLY(        "Early Night",          "After distinguishable horizon and before mid night starts."),
    NIGHT_MID(          "Mid Night",            "Middel of the night."),
    NIGHT_LATE(         "Late Night",           "After mid night ends and before distinguishable horizon."),
    NONE(               "",                     "Unknown");

    private final String text;
    private final String description;

    ActiveTimeSpesific(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

// TODO: Maak hierdie 'n generic inherited method
    public static ActiveTimeSpesific getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ActiveTimeSpesific activeTimeSpesific : ActiveTimeSpesific.values()) {
            if (inText.equalsIgnoreCase(activeTimeSpesific.text) 
                    || inText.equalsIgnoreCase(activeTimeSpesific.description)) {
                return activeTimeSpesific;
            }
        }
        return NONE;
    }

    public static String getCompleteDescription() {
        String description = "<html>";
        for (ActiveTimeSpesific activeTimeSpesific : ActiveTimeSpesific.values()) {
            if (activeTimeSpesific != NONE) {
                description = description + "<b>" + activeTimeSpesific.text + ":</b> " + activeTimeSpesific.description + "<br/>";
            }
        }
        description = description + "</html>";
        return description;
    }
    
}
