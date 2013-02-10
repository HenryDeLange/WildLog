package wildlog.utils;


public enum WildLogPrefixes {
    WILDLOG_PREFIXES_ELEMENT ("Creatures"),
    WILDLOG_PREFIXES_LOCATION ("Places"),
    WILDLOG_PREFIXES_VISIT ("Periods"),
    WILDLOG_PREFIXES_SIGHTING ("Observations")
    ;

    private String value;

    WildLogPrefixes (String inString) {
        value = inString;
    }

    @Override
    public String toString() {
        return value;
    }

}
