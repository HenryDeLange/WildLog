package wildlog.data.enums;


public enum ElementType {
    MAMMAL("Mammal"),
    BIRD("Bird"),
    REPTILE("Reptile"),
    AMPHIBIAN("Amphibian"),
    FISH("Fish"),
    INSECT("Insect"),
    PLANT("Plant"),
    OTHER("Other"),
    NONE("");

    private String text;

    ElementType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public void fix(String inText) {
        text = inText;
    }

    public static ElementType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(MAMMAL.text)) return MAMMAL;
        if (inText.equalsIgnoreCase(BIRD.text)) return BIRD;
        if (inText.equalsIgnoreCase(REPTILE.text)) return REPTILE;
        if (inText.equalsIgnoreCase(AMPHIBIAN.text)) return AMPHIBIAN;
        if (inText.equalsIgnoreCase(FISH.text)) return FISH;
        if (inText.equalsIgnoreCase(INSECT.text)) return INSECT;
        if (inText.equalsIgnoreCase(PLANT.text)) return PLANT;
        if (inText.equalsIgnoreCase(OTHER.text)) return OTHER;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
