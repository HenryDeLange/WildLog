package wildlog.data.wrappers.json.iucn;


public class IUCNSpecies {
    private int published_year;
    private String category;
    private String main_common_name;

    public int getPublished_year() {
        return published_year;
    }

    public void setPublished_year(int inPublished_year) {
        this.published_year = inPublished_year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String inCategory) {
        this.category = inCategory;
    }
    
    public String getMain_common_name() {
        return main_common_name;
    }

    public void setMain_common_name(String inMain_common_name) {
        main_common_name = inMain_common_name;
    }

    @Override
    public String toString() {
        return super.toString() + "; published_year=" + published_year + "; category=" + category + "; main_common_name=" + main_common_name;
    }
    
}
