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

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String inAncestry) {
        ancestry = inAncestry;
    }

    public INaturalistCommonName getCommon_name() {
        return common_name;
    }

    public void setCommon_name(INaturalistCommonName inCommon_name) {
        common_name = inCommon_name;
    }
    
}
