package wildlog.data.enums;

public enum UnitsTemperature {
    CELSIUS("C", "Celsius"),
    FAHRENHEIT("F", "Fahrenheit"),
    NONE("", "");

    private final String key;
    private final String text;

    UnitsTemperature(String inKey, String inText) {
        key = inKey;
        text = inText;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static UnitsTemperature getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (UnitsTemperature theEnum : UnitsTemperature.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
