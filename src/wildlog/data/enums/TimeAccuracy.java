package wildlog.data.enums;


public enum TimeAccuracy {
    GOOD          ("Good"),
    EDUCATED_GUESS("Educated Guess"),
    BAD           ("Bad"),
    UNKNOWN       ("Unknown"),
    NONE          ("");

    private String text;

    TimeAccuracy(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeAccuracy getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(GOOD.text)) return GOOD;
        if (inText.equalsIgnoreCase(EDUCATED_GUESS.text)) return EDUCATED_GUESS;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
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
