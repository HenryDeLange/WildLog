package wildlog.data.enums;


public enum Province {
    GAUTENG("Gauteng"),
    WESTERN_CAPE("Western Cape"),
    EASTERN_CAPE("Eastern Cape"),
    KWAZULU_NATAL("Kwazulu Natal"),
    LIMPOPO("Limpopo"),
    MAPUMALANGA("Mapumalanga"),
    NORTH_WEST("North West"),
    FREESTATE("Freestate"),
    NORTHERN_CAPE("North Cape"),
    NONE("None");
    
    private final String text;
    
    Province(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
