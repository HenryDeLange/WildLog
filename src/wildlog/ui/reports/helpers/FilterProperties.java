package wildlog.ui.reports.helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.VisitType;


public class FilterProperties {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Certainty> certainties;
    private List<TimeAccuracy> timeAccuracies;
    private List<GPSAccuracy> GPSAccuracies;
    private List<VisitType> visitTypes;
    private List<SightingEvidence> evidences;
    private List<LifeStatus> lifeStatuses;
    private List<ActiveTimeSpesific> activeTimes;
    private List<Moonlight> moonlights;
    private List<Age> ages;
    private List<Sex> sexes;
    private int moonphase;
    private boolean moonphaseIsLess;
    private boolean moonphaseIsMore;
    private int numberOfElements;
    private boolean numberOfElementsIsLess;
    private boolean numberOfElementsIsMore;
    private List<String> tags;
    private boolean includeEmptyTags;

    public FilterProperties() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate inStartDate) {
        startDate = inStartDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate inEndDate) {
        endDate = inEndDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime inStartTime) {
        startTime = inStartTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime inEndTime) {
        endTime = inEndTime;
    }

    public List<Certainty> getCertainties() {
        return certainties;
    }

    public void setCertainties(List<Certainty> inCertainties) {
        certainties = inCertainties;
    }

    public List<TimeAccuracy> getTimeAccuracies() {
        return timeAccuracies;
    }

    public void setTimeAccuracies(List<TimeAccuracy> inTimeAccuracies) {
        timeAccuracies = inTimeAccuracies;
    }

    public List<GPSAccuracy> getGPSAccuracies() {
        return GPSAccuracies;
    }

    public void setGPSAccuracies(List<GPSAccuracy> inGPSAccuracies) {
        GPSAccuracies = inGPSAccuracies;
    }

    public List<VisitType> getVisitTypes() {
        return visitTypes;
    }

    public void setVisitTypes(List<VisitType> inVisitTypes) {
        visitTypes = inVisitTypes;
    }

    public List<SightingEvidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<SightingEvidence> inEvidences) {
        evidences = inEvidences;
    }

    public List<LifeStatus> getLifeStatuses() {
        return lifeStatuses;
    }

    public void setLifeStatuses(List<LifeStatus> inLifeStatuses) {
        lifeStatuses = inLifeStatuses;
    }

    public List<ActiveTimeSpesific> getActiveTimes() {
        return activeTimes;
    }

    public void setActiveTimes(List<ActiveTimeSpesific> inActiveTimes) {
        activeTimes = inActiveTimes;
    }

    public List<Moonlight> getMoonlights() {
        return moonlights;
    }

    public void setMoonlights(List<Moonlight> inMoonlights) {
        moonlights = inMoonlights;
    }

    public List<Age> getAges() {
        return ages;
    }

    public void setAges(List<Age> inAges) {
        ages = inAges;
    }

    public List<Sex> getSexes() {
        return sexes;
    }

    public void setSexes(List<Sex> inSexes) {
        sexes = inSexes;
    }

    public int getMoonphase() {
        return moonphase;
    }

    public void setMoonphase(int inMoonphase) {
        moonphase = inMoonphase;
    }
    
    public boolean isMoonphaseIsLess() {
        return moonphaseIsLess;
    }

    public void setMoonphaseIsLess(boolean inMoonphaseIsLess) {
        moonphaseIsLess = inMoonphaseIsLess;
    }

    public boolean isMoonphaseIsMore() {
        return moonphaseIsMore;
    }

    public void setMoonphaseIsMore(boolean inMoonphaseIsMore) {
        moonphaseIsMore = inMoonphaseIsMore;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int inNumberOfElements) {
        numberOfElements = inNumberOfElements;
    }

    public boolean isNumberOfElementsIsLess() {
        return numberOfElementsIsLess;
    }

    public void setNumberOfElementsIsLess(boolean inNumberOfElementsIsLess) {
        numberOfElementsIsLess = inNumberOfElementsIsLess;
    }

    public boolean isNumberOfElementsIsMore() {
        return numberOfElementsIsMore;
    }

    public void setNumberOfElementsIsMore(boolean inNumberOfElementsIsMore) {
        numberOfElementsIsMore = inNumberOfElementsIsMore;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> inTags) {
        tags = inTags;
    }

    public boolean isIncludeEmptyTags() {
        return includeEmptyTags;
    }

    public void setIncludeEmptyTags(boolean inIncludeEmptyTags) {
        includeEmptyTags = inIncludeEmptyTags;
    }

}
