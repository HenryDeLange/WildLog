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

    private final String text;

    ElementType(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ElementType getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ElementType theEnum : ElementType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
