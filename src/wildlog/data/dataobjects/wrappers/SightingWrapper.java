package wildlog.data.dataobjects.wrappers;

import wildlog.data.dataobjects.Sighting;

/**
 * This class wraps a Sighting object in order to return just the Creature name as the toString() value.
 * @author Henry
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
        if (isForLocation)
            return sighting.getElementName() + " (" + sighting.getDate().getDate() + "-" + (sighting.getDate().getMonth()+1) + "-" + (sighting.getDate().getYear()+1900) + ")";
        else
            return sighting.getLocationName() + " (" + sighting.getDate().getDate() + "-" + (sighting.getDate().getMonth()+1) + "-" + (sighting.getDate().getYear()+1900) + ")";
    }

    public Sighting getSighting() {
        return sighting;
    }

}
