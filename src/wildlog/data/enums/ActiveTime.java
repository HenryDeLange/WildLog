package wildlog.data.enums;

import java.util.ArrayList;
import java.util.List;


public enum ActiveTime {
    DAY("Day"),
    NIGHT("Night"),
    ALWAYS("Always"),
    DAWN_OR_DUST("Twilight"),
    UNKNOWN("Unknown"),
    NONE("");

    private final String text;

    ActiveTime(String inText) {
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ActiveTime getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ActiveTime theEnum : ActiveTime.values()) {
            if (theEnum.text.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }
    
    public static ActiveTime getFromActiveTimeSpecific(ActiveTimeSpesific inActiveTimeSpesific) {
        if (inActiveTimeSpesific == null) {
            return NONE;
        }
        if (ActiveTimeSpesific.NIGHT_EARLY.equals(inActiveTimeSpesific) 
                || ActiveTimeSpesific.NIGHT_MID.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.NIGHT_LATE.equals(inActiveTimeSpesific)) {
            return NIGHT;
        }
        if (ActiveTimeSpesific.MORNING_SUNRISE.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.MORNING_EARLY.equals(inActiveTimeSpesific) 
                || ActiveTimeSpesific.MORNING_MID.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.DAY_MID.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.AFTERNOON_MID.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.AFTERNOON_LATE.equals(inActiveTimeSpesific)
                || ActiveTimeSpesific.AFTERNOON_SUNSET.equals(inActiveTimeSpesific)) {
            return DAY;
        }
        if (ActiveTimeSpesific.MORNING_TWILIGHT.equals(inActiveTimeSpesific) 
                || ActiveTimeSpesific.AFTERNOON_TWILIGHT.equals(inActiveTimeSpesific)) {
            return DAWN_OR_DUST;
        }
        return UNKNOWN;
    }
    
    public static List<String> getEnumListAsStringForReports() {
        List<String> tempList = new ArrayList<String>(ActiveTimeSpesific.values().length);
        for (ActiveTime theEnum : ActiveTime.values()) {
            if (!theEnum.equals(NONE) && !theEnum.equals(ALWAYS)) {
                tempList.add(theEnum.text);
            }
        }
        return tempList;
    }

}
