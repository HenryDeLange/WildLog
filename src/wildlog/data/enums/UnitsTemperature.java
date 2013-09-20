package wildlog.data.enums;

public enum UnitsTemperature {
    CELSIUS("C", "Celsius"),
    FAHRENHEIT("F", "Fahrenheit"),
    NONE("", "");

    private String key;
    private String text;

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
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(CELSIUS.text)) return CELSIUS;
        if (inText.equalsIgnoreCase(CELSIUS.key)) return CELSIUS;
        if (inText.equalsIgnoreCase(FAHRENHEIT.text)) return FAHRENHEIT;
        if (inText.equalsIgnoreCase(FAHRENHEIT.key)) return FAHRENHEIT;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
