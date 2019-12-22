package wildlog.data.enums;

import wildlog.data.enums.utils.EnumWithID;
import java.util.ArrayList;
import java.util.List;


public enum ActiveTime implements EnumWithID {
    DAY         ((byte)  1, "Day"),
    NIGHT       ((byte)  2, "Night"),
    ALWAYS      ((byte)  3, "Always"),
    DAWN_OR_DUST((byte)  4, "Twilight"),
    UNKNOWN     ((byte) -1, "Unknown"),
    NONE        ((byte)  0, "");

    private final byte id;
    private final String text;

    ActiveTime(byte inID, String inText) {
        id = inID;
        text = inText;
    }

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public byte getID() {
        return id;
    }

    public static ActiveTime getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= ActiveTime.values().length) {
            return NONE;
        }
        for (ActiveTime theEnum : ActiveTime.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
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
