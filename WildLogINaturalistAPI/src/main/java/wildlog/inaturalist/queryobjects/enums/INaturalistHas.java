package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [GET/observations] to filter on some properties. Can be used multiple times. <br/>
 * Example in URL: ?has[]=photos&has[]=geo
 */
public enum INaturalistHas {
    /**
     * Only show observations with photos.
     */
    photos,
    /**
     * Only show georeferenced observations.
     */
    geo,
    /**
     * Only show observations in need of ID help.
     */
    id_please;
}
