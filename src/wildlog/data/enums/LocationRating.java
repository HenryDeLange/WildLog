package wildlog.data.enums;


public enum LocationRating {
    HIGH("Very Nice"),
    NORMAL("Nice"),
    LOW("Bad"),
    NONE("None");

    private final String text;

    LocationRating(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

}
