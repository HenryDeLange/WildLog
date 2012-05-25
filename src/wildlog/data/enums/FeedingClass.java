package wildlog.data.enums;


public enum FeedingClass {
    CARNIVORE("Carnivore"),
    HERBIVORE("Herbivore"),
    OMNIVORE("Omnivore"),
    PHOTOSYNTHESYS("Photo-Synthesis"),
    PARASITE("Parasite"),
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
