package wildlog.data.dataobjects.wrappers;

import java.util.Calendar;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;

/**
 * This class wraps a Sighting object in order to return just the Creature name as the toString() value.
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sighting.getDate());
        String dateString = " (" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR) + ")";
        if (isForLocation) {
            return sighting.getElementName() + dateString;
        }
        else {
            return sighting.getLocationName() + dateString;
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
    public String getIDField() {
        return sighting.getIDField();
    }

}
