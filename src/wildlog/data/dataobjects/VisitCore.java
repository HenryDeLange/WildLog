package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.data.utils.UtilsData;


public class VisitCore implements DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "VISIT-";
    public static final String WILDLOG_FOLDER_PREFIX = "Periods";
    protected String name; // Used as index (ID)
    protected Date startDate;
    protected Date endDate;
    protected String description;
    protected GameWatchIntensity gameWatchingIntensity;
    protected VisitType type;
    protected String locationName;


    public VisitCore() {
    }

    public VisitCore(String inName) {
        name = inName;
    }

    public VisitCore(String inName, String inLocationName) {
        name = inName;
        locationName = inLocationName;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object inVisit) {
        if (inVisit != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            VisitCore compareVisit = (VisitCore) inVisit;
            if (name != null && compareVisit.getName() != null) {
                return(name.compareToIgnoreCase(compareVisit.getName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + name;
    }
    
    @Override
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    @Override
    public String getDisplayName() {
        return name;
    }
    
    @Override
    public String getIDField() {
        return name;
    }

    public boolean hasTheSameContent(VisitCore inVisit) {
        if (inVisit == null) {
            return false;
        }
        return UtilsData.isTheSame(this, inVisit)
                && UtilsData.isTheSame(getDescription(), inVisit.getDescription())
                && UtilsData.isTheSame(getEndDate(), inVisit.getEndDate())
                && UtilsData.isTheSame(getGameWatchingIntensity(), inVisit.getGameWatchingIntensity())
                && UtilsData.isTheSame(getLocationName(), inVisit.getLocationName())
                && UtilsData.isTheSame(getName(), inVisit.getName())
                && UtilsData.isTheSame(getStartDate(), inVisit.getStartDate())
                && UtilsData.isTheSame(getType(), inVisit.getType());
    }

     public <T extends VisitCore> T cloneShallow() {
        try {
            T visit = (T) this.getClass().newInstance();
            visit.setDescription(description);
            visit.setEndDate(endDate);
            visit.setGameWatchingIntensity(gameWatchingIntensity);
            visit.setLocationName(locationName);
            visit.setName(name);
            visit.setStartDate(startDate);
            visit.setType(type);
            return visit;
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public GameWatchIntensity getGameWatchingIntensity() {
        return gameWatchingIntensity;
    }

    public VisitType getType() {
        return type;
    }

    public void setName(String inName) {
        name = inName;
    }

    public void setStartDate(Date inStartDate) {
        startDate = inStartDate;
    }

    public void setEndDate(Date inEndDate) {
        endDate = inEndDate;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setGameWatchingIntensity(GameWatchIntensity inGameWatchingIntensity) {
        gameWatchingIntensity = inGameWatchingIntensity;
    }

    public void setType(VisitType inType) {
        type = inType;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

}