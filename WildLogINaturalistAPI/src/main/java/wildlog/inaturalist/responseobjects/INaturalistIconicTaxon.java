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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getRank_level() {
        return rank_level;
    }

    public void setRank_level(int rank_level) {
        this.rank_level = rank_level;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }
    
}
