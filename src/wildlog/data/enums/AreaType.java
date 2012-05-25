package wildlog.data.enums;


public enum AreaType {
    OPEN("Out in the open"),
    THICKET("In thick cover"),
    LIGHT_COVER("In light cover"),
    WETLAND("In a wetland area"),
    RIVER("Near a river"),
    DAM("Near a dam"),
    CLIFF("Near a cliff"),
    MOUNTAIN("On a mountain"),
    SAND("On desert sand"),
    HUMANS("Near human structures"),
    NONE("None");
    
    
    private String text;
    
    AreaType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static AreaType getEnumFromText(String inText) {
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(OPEN.text)) return OPEN;
        if (inText.equalsIgnoreCase(THICKET.text)) return THICKET;
        if (inText.equalsIgnoreCase(LIGHT_COVER.text)) return LIGHT_COVER;
        if (inText.equalsIgnoreCase(WETLAND.text)) return WETLAND;
        if (inText.equalsIgnoreCase(RIVER.text)) return RIVER;
        if (inText.equalsIgnoreCase(DAM.text)) return DAM;
        if (inText.equalsIgnoreCase(CLIFF.text)) return CLIFF;
        if (inText.equalsIgnoreCase(MOUNTAIN.text)) return MOUNTAIN;
        if (inText.equalsIgnoreCase(SAND.text)) return SAND;
        if (inText.equalsIgnoreCase(HUMANS.text)) return HUMANS;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
