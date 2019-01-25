package wildlog.data.enums;


public enum EndangeredStatus {
    EX("Ex", "Extinct"),
    EW("Ew", "Extinct in Wild"),
    CR("Cr", "Critically Endangered"),
    EN("En", "Endangered"),
    VU("Vu", "Vunerable"),
    NT("Nt", "Near Threatened"),
    LC("Lc", "Least Concern"),
    NONE("", "");

    private final String text;
    private final String key;

    EndangeredStatus(String inKey, String inText) {
        text = inText;
        key = inKey;
    }

    @Override
    public String toString() {
        return text;
    }

    public String key() {
        return key;
    }

    public static EndangeredStatus getEnumFromText(String inText) {
        if (inText == null) {
            return NONE;
        }
        for (EndangeredStatus theEnum : EndangeredStatus.values()) {
            if (theEnum.text.equalsIgnoreCase(inText) || theEnum.key.equalsIgnoreCase(inText)) {
                return theEnum;
            }
        }
        return NONE;
    }

}
