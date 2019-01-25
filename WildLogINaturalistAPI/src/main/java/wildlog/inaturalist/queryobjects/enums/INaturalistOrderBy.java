package wildlog.inaturalist.queryobjects.enums;

/**
 * Used to for [GET/observations] for the field to sort by.
 */
public enum INaturalistOrderBy {
    /**
     * Order by the date observed.
     */
    observed_on,
    /**
     * Order by the date added to iNaturalist.
     */
    date_added;
}
