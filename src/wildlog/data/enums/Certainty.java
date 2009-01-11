package wildlog.data.enums;


public enum Certainty {
    SURE    ("100%     (Sure)"),
    GOOD    ("90 - 99% (Good)"),
    BAD     ("70 - 90% (Bad)"),
    VERY_BAD(" 0 - 70% (Unsure)"),
    NONE("None");
    
    private final String text;
    
    Certainty(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
