package wildlog.inaturalist.responseobjects;


public class INaturalistCommonName {
    private int id;
    private String name;
    private boolean is_valid;
    private String lexicon;

    
    public int getId() {
        return id;
    }

    public void setId(int inId) {
        id = inId;
    }

    public String getName() {
        return name;
    }

    public void setName(String inName) {
        name = inName;
    }

    public boolean isIs_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean inIs_valid) {
        is_valid = inIs_valid;
    }

    public String getLexicon() {
        return lexicon;
    }

    public void setLexicon(String inLexicon) {
        lexicon = inLexicon;
    }
    
}
