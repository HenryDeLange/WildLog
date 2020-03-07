package wildlog.inaturalist.queryobjects.enums;

/**
 * Specify the license the users have applied to their observations.
 */
public enum INaturalistLicense {
    /**
     * No license specified, observer withholds all rights to distribution.
     */
    none,
    /**
     * Any observation licensed, see the response for the license specified.
     */
    any,
    /**
     * Creative Commons Attribution License.
     */
    CC_BY,
    /**
     * Creative Commons Attribution-NonCommercial License.
     */
    CC_BY_NC,
    /**
     * Creative Commons Attribution-ShareAlike License.
     */
    CC_BY_SA,
    /**
     * Creative Commons Attribution-NoDerivs License.
     */
    CC_BY_ND,
    /**
     * Creative Commons Attribution-NonCommercial-ShareAlike License.
     */
    CC_BY_NC_SA,
    /**
     * Creative Commons Attribution-NonCommercial-NoDerivs License.
     */
    CC_BY_NC_ND;

    public String getAsINatString() {
        return name().replace('_', '-');
    }

    @Override
    public String toString() {
        return getAsINatString();
    }

}
