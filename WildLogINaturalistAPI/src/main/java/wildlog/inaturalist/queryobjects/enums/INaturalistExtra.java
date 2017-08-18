package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [GET/observations] for the additional information.
 */
public enum INaturalistExtra {
    /**
     *  Returns observation field values.
     */
    fields,
    /**
     * ???Not documented???
     */
    identifications,
    /**
     * Returns info about the projects the observations have been added to.
     */
    projects;
}
