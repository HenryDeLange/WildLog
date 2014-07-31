package wildlog.data.enums;

import java.util.ArrayList;
import java.util.List;


public enum AccommodationType {
    CAMPING("Camping"),
    SMALL_UNIT("Small Unit"),
    BIG_UNIT("Big Unit"),
    UNKNOWN("Unknown"),
    NONE("None");

    private final String text;

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
        List<AccommodationType> tempList = new ArrayList<AccommodationType>(AccommodationType.values().length);
        if (inText == null) {
            tempList.add(NONE);
        }
        for (AccommodationType theEnum : AccommodationType.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                tempList.add(theEnum);
            }
        }
        return tempList;
    }

}
