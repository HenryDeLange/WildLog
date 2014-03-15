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

    private String text;
    private String key;

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
        if (inText == null) inText = "";
        if (inText.equalsIgnoreCase(EX.key)) return EX;
        if (inText.equalsIgnoreCase(EX.text)) return EX;
        if (inText.equalsIgnoreCase(EW.key)) return EW;
        if (inText.equalsIgnoreCase(EW.text)) return EW;
        if (inText.equalsIgnoreCase(CR.key)) return CR;
        if (inText.equalsIgnoreCase(CR.text)) return CR;
        if (inText.equalsIgnoreCase(EN.key)) return EN;
        if (inText.equalsIgnoreCase(EN.text)) return EN;
        if (inText.equalsIgnoreCase(VU.key)) return VU;
        if (inText.equalsIgnoreCase(VU.text)) return VU;
        if (inText.equalsIgnoreCase(NT.key)) return NT;
        if (inText.equalsIgnoreCase(NT.text)) return NT;
        if (inText.equalsIgnoreCase(LC.key)) return LC;
        if (inText.equalsIgnoreCase(LC.text)) return LC;
        if (inText.equalsIgnoreCase(NONE.text)) return NONE;
        return NONE;
    }

}
