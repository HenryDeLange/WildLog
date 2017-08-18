package wildlog.inaturalist.responseobjects;

public class INaturalistTaxon {
    private int id;
    private String name;
    private String rank;
    private String ancestry;
    private INaturalistCommonName common_name;

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

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    public INaturalistCommonName getCommon_name() {
        return common_name;
    }

    public void setCommon_name(INaturalistCommonName common_name) {
        this.common_name = common_name;
    }
    
}
