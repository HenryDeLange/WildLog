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

    private String text;

    Age(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Age getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(PARENT_OFFSPRING_MIX.text)) return PARENT_OFFSPRING_MIX;
        if (inText.equalsIgnoreCase(IMMATURE.text)) return IMMATURE;
        if (inText.equalsIgnoreCase(YOUNG_ADULT.text)) return YOUNG_ADULT;
        if (inText.equalsIgnoreCase(ADULT.text)) return ADULT;
        if (inText.equalsIgnoreCase(OLD.text)) return OLD;
        if (inText.equalsIgnoreCase(EGG_SEED_SPORE.text)) return EGG_SEED_SPORE;
        if (inText.equalsIgnoreCase(EARLY_LIFESTAGE.text)) return EARLY_LIFESTAGE;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(UNKNOWN.text)) return UNKNOWN;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }
}
