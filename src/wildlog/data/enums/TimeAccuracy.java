package wildlog.data.enums;


public enum TimeAccuracy {
    GOOD          ("Good"),
    EDUCATED_GUESS("Educated Guess"),
    BAD           ("Bad"),
    UNKNOWN       ("Unknown"),
    NONE          ("");

    private final String text;

    TimeAccuracy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeAccuracy getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (TimeAccuracy theEnum : TimeAccuracy.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

    /**
     * Checks whether the one of the values has been selected that indicates
     * that the time is accurate enough to use in calculations.
     * <br/>
     * GOOD and EDUCATED_GUESS will return true. BAD, UNKOWN and NONE rest will return false.
     * @return True if it is reasonably accurate, false otherwise.
     */
    public boolean isUsableTime() {
        if (GOOD.equals(this) || EDUCATED_GUESS.equals(this)) {
            return true;
        }
        return false;
    }
}
