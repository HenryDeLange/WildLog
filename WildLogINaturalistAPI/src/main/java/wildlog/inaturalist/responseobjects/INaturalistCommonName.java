package wildlog.inaturalist.responseobjects;

public class INaturalistCommonName {
    private int id;
    private String name;
    private boolean is_valid;
    private String lexicon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }

    public String getLexicon() {
        return lexicon;
    }

    public void setLexicon(String lexicon) {
        this.lexicon = lexicon;
    }
    
}
