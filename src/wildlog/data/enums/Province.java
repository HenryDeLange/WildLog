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
    
    private String text;
    
    Province(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static Province getEnumFromText(String inText) {
        if (inText.equalsIgnoreCase(GAUTENG.text)) return GAUTENG;
        if (inText.equalsIgnoreCase(WESTERN_CAPE.text)) return WESTERN_CAPE;
        if (inText.equalsIgnoreCase(EASTERN_CAPE.text)) return EASTERN_CAPE;
        if (inText.equalsIgnoreCase(KWAZULU_NATAL.text)) return KWAZULU_NATAL;
        if (inText.equalsIgnoreCase(LIMPOPO.text)) return LIMPOPO;
        if (inText.equalsIgnoreCase(MAPUMALANGA.text)) return MAPUMALANGA;
        if (inText.equalsIgnoreCase(NORTH_WEST.text)) return NORTH_WEST;
        if (inText.equalsIgnoreCase(FREESTATE.text)) return FREESTATE;
        if (inText.equalsIgnoreCase(NORTHERN_CAPE.text)) return NORTHERN_CAPE;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
