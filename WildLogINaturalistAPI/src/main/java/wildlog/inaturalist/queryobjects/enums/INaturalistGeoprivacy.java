package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [POST/observations] for the geoprivacy field.
 */
public enum INaturalistGeoprivacy {
    /**
     * The position is visible to the public.
     */
    open,
    /**
     * The precise position is hidden from the public.
     */
    obscured, 
    /**
     * The positions is hidden from the public.
     */
    _private;
    
    public String getAsINatString() {
        return name().replace("_", "");
    }

    @Override
    public String toString() {
        return getAsINatString();
    }
}
