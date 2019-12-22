package wildlog.data.enums;

import java.util.ArrayList;
import java.util.List;
import wildlog.data.enums.utils.EnumWithID;


public enum ActiveTimeSpesific implements EnumWithID {
    MORNING_TWILIGHT    ((byte)  1, "Morning Twilight",     "After dark night and before good light."),
    MORNING_SUNRISE     ((byte)  2, "Morning Sunrise",      "From good light to when sunrise ends."),
    MORNING_EARLY       ((byte)  3, "Early Morning",        "When sunrise ends and before mid morning starts."),
    MORNING_MID         ((byte)  4, "Mid Morning",          "Before mid day."),
    DAY_MID             ((byte)  5, "Mid Day",              "Middel of the day."),
    AFTERNOON_MID       ((byte)  6, "Mid Afternoon",        "After mid day."),
    AFTERNOON_LATE      ((byte)  7, "Late Afternoon",       "After mid afternoon ends and when sunset starts."),
    AFTERNOON_SUNSET    ((byte)  8, "Afternoon Sunset",     "From sunset starts to when good light ends."),
    AFTERNOON_TWILIGHT  ((byte)  9, "Afternoon Twilight",   "After good light and before dark night."),
    NIGHT_EARLY         ((byte) 10, "Early Night",          "After distinguishable horizon and before mid night starts."),
    NIGHT_MID           ((byte) 11, "Mid Night",            "Middel of the night."),
    NIGHT_LATE          ((byte) 12, "Late Night",           "After mid night ends and before distinguishable horizon."),
    UNKNOWN             ((byte) -1, "Unknown",              "The time category is unknown."),
    NONE                ((byte)  0, "",                     "None Selected.");

    private final byte id;
    private final String text;
    private final String description;

    ActiveTimeSpesific(byte inID, String inText, String inDescription) {
        id = inID;
        text = inText;
        description = inDescription;
    }

    @Override
    public String toString() {
        return text;
    }
    
    @Override
    public byte getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }
    
    public static ActiveTimeSpesific getEnumFromID(byte inID) {
        if (inID <= 0 || inID >= ActiveTimeSpesific.values().length) {
            return NONE;
        }
        for (ActiveTimeSpesific theEnum : ActiveTimeSpesific.values()) {
            if (theEnum.id == inID) {
                return theEnum;
            }
        }
        return NONE;
    }

    public static ActiveTimeSpesific getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (ActiveTimeSpesific theEnum : ActiveTimeSpesific.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.description.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

    public static String getCompleteDescription() {
        String description = "<html>";
        for (ActiveTimeSpesific activeTimeSpesific : ActiveTimeSpesific.values()) {
            if (activeTimeSpesific != NONE) {
                description = description + "<b>" + activeTimeSpesific.text + ":</b> " + activeTimeSpesific.description + "<br/>";
            }
        }
        description = description + "</html>";
        return description;
    }
    
    public static List<String> getEnumListAsString() {
        List<String> tempList = new ArrayList<String>(ActiveTimeSpesific.values().length);
        for (ActiveTimeSpesific theEnum : ActiveTimeSpesific.values()) {
            if (!theEnum.equals(NONE)) {
                tempList.add(theEnum.text);
            }
            else {
                tempList.add(theEnum.description);
            }
        }
        return tempList;
    }
    
}
