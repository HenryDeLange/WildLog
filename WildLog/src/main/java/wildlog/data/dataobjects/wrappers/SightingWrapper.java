package wildlog.data.dataobjects.wrappers;

import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsTime;

/**
 * This class wraps a Sighting object in order to return custom toString() values for the Browse Tab.
 */
public class SightingWrapper implements DataObjectWithWildLogFile, DataObjectWithHTML {
    // Variables
    private final Sighting sighting;
    private final boolean isForLocation;

    // Contructor
    public SightingWrapper(Sighting inSighting, boolean inIsForLocation) {
        sighting = inSighting;
        isForLocation = inIsForLocation;
    }

    // Methods
    @Override
    public String toString() {
        String dateString = UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(UtilsTime.getLocalDateFromDate(sighting.getDate()));
        if (isForLocation) {
            return dateString + " " + sighting.getCachedElementName();
        }
        else {
            return dateString + " " + sighting.getCachedLocationName();
        }
    }

    public Sighting getSighting() {
        return sighting;
    }

    @Override
    public String getWildLogFileID() {
        return sighting.getWildLogFileID();
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        return sighting.toHTML(inIsRecursive, inIncludeImages, inIsSummary, inApp, inExportType, inProgressbarTask);
    }

    @Override
    public String getExportPrefix() {
        return sighting.getExportPrefix();
    }

    @Override
    public String getDisplayName() {
        return sighting.getDisplayName();
    }

    @Override
    public int compareTo(Object inSighting) {
        return sighting.compareTo(inSighting);
    }
    
    @Override
    public long getIDField() {
        return sighting.getIDField();
    }

}
