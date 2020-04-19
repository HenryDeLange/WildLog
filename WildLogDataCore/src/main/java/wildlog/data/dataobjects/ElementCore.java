package wildlog.data.dataobjects;

import java.util.Objects;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.utils.UtilsData;


public class ElementCore extends DataObjectWithAudit implements DataObjectWithWildLogFile {
    public static final String WILDLOG_FOLDER_PREFIX = "Creatures";
    protected String primaryName;
    protected String otherName;
    protected String scientificName;
    protected String description;
    protected String nutrition;
    protected String diagnosticDescription;
    protected EndangeredStatus endangeredStatus;
    protected String behaviourDescription;
    protected ElementType type;
    protected FeedingClass feedingClass;
    protected String referenceID;
    protected String distribution;
    // Adding some extra fields that can optionally be cached for performance reasons
    protected int cachedSightingCount;


    public ElementCore() {
    }
    
    public ElementCore(long inID, String inPrimaryName) {
        id = inID;
        primaryName = inPrimaryName;
    }

    @Override
    public String toString() {
        return primaryName;
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject == null || !(inObject instanceof ElementCore)) {
            return false;
        }
        return id == ((ElementCore) inObject).getID();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 71 * hash + Objects.hashCode(primaryName);
        return hash;
    }

    @Override
    public int compareTo(Object inElement) {
        if (inElement != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            ElementCore compareElement = (ElementCore) inElement;
            if (primaryName != null && compareElement.getPrimaryName() != null) {
                return(primaryName.compareToIgnoreCase(compareElement.getPrimaryName()));
            }
        }
        return 0;
    }

    @Override
    public long getWildLogFileID() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return primaryName;
    }
    
    @Override
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    @Override
    public long getIDField() {
        return id;
    }

    public boolean hasTheSameContent(ElementCore inElement) {
        if (inElement == null) {
            return false;
        }
        return UtilsData.isTheSame(getID(), inElement.getID())
                && UtilsData.isTheSame(getBehaviourDescription(), inElement.getBehaviourDescription())
                && UtilsData.isTheSame(getDescription(), inElement.getDescription())
                && UtilsData.isTheSame(getDiagnosticDescription(), inElement.getDiagnosticDescription())
                && UtilsData.isTheSame(getDistribution(), inElement.getDistribution())
                && UtilsData.isTheSame(getEndangeredStatus(), inElement.getEndangeredStatus())
                && UtilsData.isTheSame(getFeedingClass(), inElement.getFeedingClass())
                && UtilsData.isTheSame(getNutrition(), inElement.getNutrition())
                && UtilsData.isTheSame(getOtherName(), inElement.getOtherName())
                && UtilsData.isTheSame(getPrimaryName(), inElement.getPrimaryName())
                && UtilsData.isTheSame(getReferenceID(), inElement.getReferenceID())
                && UtilsData.isTheSame(getScientificName(), inElement.getScientificName())
                && UtilsData.isTheSame(getType(), inElement.getType())
                && UtilsData.isTheSame(getAuditTime(), inElement.getAuditTime())
                && UtilsData.isTheSame(getAuditUser(), inElement.getAuditUser());
    }

    public <T extends ElementCore> T cloneShallow() {
        try {
            T element = (T) this.getClass().newInstance();
            element.setID(id);
            element.setBehaviourDescription(behaviourDescription);
            element.setDescription(description);
            element.setDiagnosticDescription(diagnosticDescription);
            element.setDistribution(distribution);
            element.setEndangeredStatus(endangeredStatus);
            element.setFeedingClass(feedingClass);
            element.setNutrition(nutrition);
            element.setOtherName(otherName);
            element.setPrimaryName(primaryName);
            element.setReferenceID(referenceID);
            element.setScientificName(scientificName);
            element.setType(type);
            element.setAuditTime(auditTime);
            element.setAuditUser(auditUser);
            return element;
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public String getDescription() {
        return description;
    }

    public String getNutrition() {
        return nutrition;
    }

    public String getDiagnosticDescription() {
        return diagnosticDescription;
    }

    public EndangeredStatus getEndangeredStatus() {
        return endangeredStatus;
    }

    public String getBehaviourDescription() {
        return behaviourDescription;
    }

    public ElementType getType() {
        return type;
    }

    public FeedingClass getFeedingClass() {
        return feedingClass;
    }

    public void setPrimaryName(String inPrimaryName) {
        primaryName = inPrimaryName;
    }

    public void setOtherName(String inOtherName) {
        otherName = inOtherName;
    }

    public void setScientificName(String inScientificName) {
        scientificName = inScientificName;
    }

    public void setReferenceID(String inReferenceID) {
        referenceID = inReferenceID;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setNutrition(String inNutrition) {
        nutrition = inNutrition;
    }

    public void setDiagnosticDescription(String inDiagnosticDescription) {
        diagnosticDescription = inDiagnosticDescription;
    }

    public void setEndangeredStatus(EndangeredStatus inEndangeredStatus) {
        endangeredStatus = inEndangeredStatus;
    }

    public void setBehaviourDescription(String inBehaviourDescription) {
        behaviourDescription = inBehaviourDescription;
    }

    public void setType(ElementType inType) {
        type = inType;
    }

    public void setFeedingClass(FeedingClass inFeedingClass) {
        feedingClass = inFeedingClass;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String inDistribution) {
        distribution = inDistribution;
    }

    public int getCachedSightingCount() {
        return cachedSightingCount;
    }

    public void setCachedSightingCount(int inCachedSightingCount) {
        cachedSightingCount = inCachedSightingCount;
    }

}