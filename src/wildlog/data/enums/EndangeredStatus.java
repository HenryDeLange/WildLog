package wildlog.data.enums;


public enum EndangeredStatus {
    EX("Ex", "Extinct"),
    EW("Ew", "Extinct in Wild"),
    CR("Cr", "Critically Endangered"),
    EN("En", "Endangered"),
    VU("Vu", "Vunerable"),
    NT("Nt", "Near threatened"),
    LC("Lc", "Least Concern"),
    NONE("", "None");
    
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

}
