package wildlog.inaturalist.responseobjects;


public class INaturalistIconicTaxon {
    private int id;
    private String name;
    private String rank;
    private int rank_level;
    private String ancestry;

    
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

    public String getRank() {
        return rank;
    }

    public void setRank(String inRank) {
        rank = inRank;
    }

    public int getRank_level() {
        return rank_level;
    }

    public void setRank_level(int inRank_level) {
        rank_level = inRank_level;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String inAncestry) {
        ancestry = inAncestry;
    }
    
}
