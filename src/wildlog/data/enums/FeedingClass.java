package wildlog.data.enums;


public enum FeedingClass {
    CARNIVORE("Carnivore"),
    HERBIVORE("Herbivore"),
    OMNIVORE("Omnivore"),
    PHOTOSYNTHESYS("Photo-Synthesis"),
    PARASITE("Parasite"),
    NONE("None");
    
    private final String text;
    
    FeedingClass(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
