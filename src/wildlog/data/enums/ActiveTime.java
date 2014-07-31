package wildlog.data.enums;


public enum ActiveTime {
    DAY("Day"),
    NIGHT("Night"),
    ALWAYS("Always"),
    DAWN_OR_DUST("Dawn or Dust"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    ActiveTime(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ActiveTime getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ActiveTime theEnum : ActiveTime.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
