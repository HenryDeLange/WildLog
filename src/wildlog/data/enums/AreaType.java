package wildlog.data.enums;


public enum AreaType {
    OPEN("Out in the open"),
    THICKET("In thick cover"),
    WETLAND("In a wetland area"),
    RIVER("Near a river"),
    DAM("Near a dam"),
    CLIFF("Near a cliff"),
    MOUNTAIN("On a mountain"),
    SAND("On desert sand"),
    HUMANS("Near human structures"),
    NONE("None");
    
    
    private final String text;
    
    AreaType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }

}
