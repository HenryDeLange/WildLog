package wildlog.data.enums;

import java.util.ArrayList;
import java.util.List;


public enum AccommodationType {
    CAMPING("Camping"),
    SMALL_UNIT("Small Unit"),
    BIG_UNIT("Big Unit"),
    NONE("None");
    
    private String text;
    
    AccommodationType(String inText) {
        text = inText;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    public String test() {
        return text;
    }

    public static List<AccommodationType> getEnumFromText(String inText) {
        if (inText == null) inText = "";
        List<AccommodationType> tempList = new ArrayList<AccommodationType>(3);
        if (inText.contains(CAMPING.text)) tempList.add(CAMPING);
        if (inText.contains(SMALL_UNIT.text)) tempList.add(SMALL_UNIT);
        if (inText.contains(BIG_UNIT.text)) tempList.add(BIG_UNIT);
        if (inText.contains(NONE.text)) tempList.add(NONE);
        return tempList;
    }

}
