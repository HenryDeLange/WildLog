package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [POST/observations] to indicate whether the user needs ID help.
 */
public enum INaturalistIdPlease {
    /**
     * Use 0 for No.
     */
    NO_0,
    /**
     * Use 1 for Yes.
     */
    YES_1;
    
    public String getAsINatString() {
        return name().substring(name().indexOf('_'));
    }

    @Override
    public String toString() {
        return getAsINatString();
    }
}
