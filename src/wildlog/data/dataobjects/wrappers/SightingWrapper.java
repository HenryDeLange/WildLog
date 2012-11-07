package wildlog.data.dataobjects.wrappers;

import java.util.Calendar;
import wildlog.data.dataobjects.Sighting;

/**
 * This class wraps a Sighting object in order to return just the Creature name as the toString() value.
 */
public class SightingWrapper {
    // Variables
    private Sighting sighting;
    private boolean isForLocation;

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
        if (isForLocation)
            return sighting.getElementName() + dateString;
        else
            return sighting.getLocationName() + dateString;
    }

    public Sighting getSighting() {
        return sighting;
    }

}
