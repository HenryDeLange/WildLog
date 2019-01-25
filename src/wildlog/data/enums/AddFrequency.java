package wildlog.data.enums;


public enum AddFrequency {
    HIGH("90 - 100%", "Added almost always. (Each sighting is recorded)"),
    MEDIUM("75 - 90%", "Added frequently. (Most good sightings are added)"),
    LOW("40 - 75%", "Added infrequently. (Only the first sighting per visit)"),
    VERY_LOW("0 - 40%", "Very seldomly added. (Might be so common that it is not realy recorded)"),
    NONE("", "");

    private final String text;
    private final String description;

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
        if (inText == null) {
            return NONE;
        }
        for (AddFrequency theEnum : AddFrequency.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.description.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
