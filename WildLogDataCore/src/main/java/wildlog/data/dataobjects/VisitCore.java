package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.data.utils.UtilsData;


public class VisitCore extends DataObjectWithAudit implements DataObjectWithWildLogFile {
    public static final String WILDLOG_FOLDER_PREFIX = "Periods";
    protected String name; // Must be unique
    protected Date startDate;
    protected Date endDate;
    protected String description;
    protected GameWatchIntensity gameWatchingIntensity;
    protected VisitType type;
    protected long locationID;
    // Adding some extra fields that can optionally be cached for performance reasons
    protected String cachedLocationName;


    public VisitCore() {
    }

    public VisitCore(long inID, String inName) {
        id = inID;
        name = inName;
    }

    public VisitCore(long inID, String inName, long inLocationID) {
        id = inID;
        name = inName;
        locationID = inLocationID;
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
    public long getWildLogFileID() {
        return id;
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
    public long getIDField() {
        return id;
    }

    public boolean hasTheSameContent(VisitCore inVisit) {
        if (inVisit == null) {
            return false;
        }
        return UtilsData.isTheSame(getID(), inVisit.getID())
                && UtilsData.isTheSame(getDescription(), inVisit.getDescription())
                && UtilsData.isTheSame(getEndDate(), inVisit.getEndDate())
                && UtilsData.isTheSame(getGameWatchingIntensity(), inVisit.getGameWatchingIntensity())
                && UtilsData.isTheSame(getLocationID(), inVisit.getLocationID())
                && UtilsData.isTheSame(getName(), inVisit.getName())
                && UtilsData.isTheSame(getStartDate(), inVisit.getStartDate())
                && UtilsData.isTheSame(getType(), inVisit.getType())
                && UtilsData.isTheSame(getAuditTime(), inVisit.getAuditTime())
                && UtilsData.isTheSame(getAuditUser(), inVisit.getAuditUser());
    }

     public <T extends VisitCore> T cloneShallow() {
        try {
            T visit = (T) this.getClass().newInstance();
            visit.setID(id);
            visit.setDescription(description);
            visit.setEndDate(endDate);
            visit.setGameWatchingIntensity(gameWatchingIntensity);
            visit.setLocationID(locationID);
            visit.setCachedLocationName(cachedLocationName);
            visit.setName(name);
            visit.setStartDate(startDate);
            visit.setType(type);
            visit.setAuditTime(auditTime);
            visit.setAuditUser(auditUser);
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

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long inLocationID) {
        locationID = inLocationID;
    }

    public String getCachedLocationName() {
        return cachedLocationName;
    }

    public void setCachedLocationName(String inCachedLocationName) {
        cachedLocationName = inCachedLocationName;
    }

}