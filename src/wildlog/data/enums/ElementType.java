package wildlog.data.enums;


public enum ElementType {
    ANIMAL("Animal"),
    BIRD("Bird"),
    PLANT("Plant"),
    OTHER("Other");
    
    private final String text;
    
    ElementType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
}
