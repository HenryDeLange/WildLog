package wildlog.data.enums;


public enum Age {
    PARENT_OFFSPRING_MIX("Parent with Offspring"),
    IMMATURE("Immature Offspring"),
    YOUNG_ADULT("Young Adult"),
    ADULT("Mature Adult"),
    OLD("Old Adult"),
    EGG_SEED_SPORE("Egg, Seed, Spore, etc."),
    EARLY_LIFESTAGE("Early Lifestage"),
    OTHER("Other"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    Age(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Age getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (Age theEnum : Age.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
}
