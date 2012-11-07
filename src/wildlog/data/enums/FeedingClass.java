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
    NONE("None");

    private String text;

    FeedingClass(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FeedingClass getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(CARNIVORE.text)) return CARNIVORE;
        if (inText.equalsIgnoreCase(HERBIVORE.text)) return HERBIVORE;
        if (inText.equalsIgnoreCase(OMNIVORE.text)) return OMNIVORE;
        if (inText.equalsIgnoreCase(PHOTOSYNTHESYS.text)) return PHOTOSYNTHESYS;
        if (inText.equalsIgnoreCase(PARASITE.text)) return PARASITE;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
