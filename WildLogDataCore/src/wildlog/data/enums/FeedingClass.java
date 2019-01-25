package wildlog.data.enums;


public enum FeedingClass {
    CARNIVORE("Carnivore"),
//    CARNIVORE_MAMMAL("Carnivore - Mostly Mammals"),
//    CARNIVORE_BIRD("Carnivore - Mostly Birds"),
//    CARNIVORE_INSECT("Carnivore - Mostly Insects, etc."),
//    CARNIVORE_FISH("Carnivore - Mostly Fish"),
//    CARNIVORE_SCAVENGER("Carnivore - Mostly Scavenger"),
    HERBIVORE("Herbivore"),
//    HERBIVORE_BROWSER("Herbivore - Mostly Browser"),
//    HERBIVORE_GRAZER("Herbivore - Mostly Grazer"),
//    HERBIVORE_ROOTS("Herbivore - Mostly Roots, etc."),
//    HERBIVORE_SEED("Herbivore - Mostly Seeds"),
//    HERBIVORE_NECTAR("Herbivore - Mostly Nectar, etc."),
    OMNIVORE("Omnivore"),
    PARASITE("Parasite"),
    PHOTOSYNTHESYS("Photo-Synthesis"),
    OTHER("Other"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    FeedingClass(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FeedingClass getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (FeedingClass theEnum : FeedingClass.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
