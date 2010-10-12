package wildlog.data.enums;


public enum AddFrequency {
    HIGH("90 - 100%", "Added almost always. (Each sighting is recorded)"),
    MEDIUM("75 - 90%", "Added frequently. (Most good sightings are added)"),
    LOW("40 - 75%", "Added infrequently. (Only the first sighting per visit)"),
    VERY_LOW("0 - 40%", "Very seldomly added. (Might be so common that it is not realy recorded)"),
    NONE("None", "None");
    
    private String text;
    private String description;

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    AddFrequency(String inText, String inDescription) {
        text = inText;
        description = inDescription;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static AddFrequency getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(HIGH.text)) return HIGH;
        if (inText.equalsIgnoreCase(MEDIUM.text)) return MEDIUM;
        if (inText.equalsIgnoreCase(LOW.text)) return LOW;
        if (inText.equalsIgnoreCase(VERY_LOW.text)) return VERY_LOW;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
