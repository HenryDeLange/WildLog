package wildlog.data.dbi;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Province;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.SizeType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WishRating;

/**
 *
 * @author Henry
 */
public abstract class DBI_JDBC implements DBI {
    // Variables
    protected Connection conn;
    protected String createElementsTable;
    protected String createLocationsTable;
    protected String createVisitsTable;
    protected String createSightingsTable;
    protected String createFilesTable;
    protected String createWildLogTable;

    public DBI_JDBC() {

    }

    protected void init() {
        // Create tables
        createElementsTable = new StringBuilder("CREATE TABLE ELEMENTS ")
                            .append("(")
                            .append("   PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL,")
                            .append("   OTHERNAME varchar(150),")
                            .append("   SCIENTIFICNAME varchar(150),")
                            .append("   DESCRIPTION longvarchar,")
                            .append("   DISTRIBUTION longvarchar,")
                            .append("   NUTRITION longvarchar,")
                            .append("   WATERDEPENDANCE varchar(50),")
                            .append("   SIZEMALEMIN float(52),")
                            .append("   SIZEMALEMAX float(52),")
                            .append("   SIZEFEMALEMIN float(52),")
                            .append("   SIZEFEMALEMAX float(52),")
                            .append("   SIZEUNIT varchar(10),")
                            .append("   SIZETYPE varchar(50),")
                            .append("   WEIGHTMALEMIN float(52),")
                            .append("   WEIGHTMALEMAX float(52),")
                            .append("   WEIGHTFEMALEMIN float(52),")
                            .append("   WEIGHTFEMALEMAX float(52),")
                            .append("   WEIGHTUNIT varchar(10),")
                            .append("   BREEDINGDURATION varchar(50),")
                            .append("   BREEDINGNUMBER varchar(50),")
                            .append("   WISHLISTRATING varchar(50),")
                            .append("   DIAGNOSTICDESCRIPTION longvarchar,")
                            .append("   ACTIVETIME varchar(50),")
                            .append("   ENDANGEREDSTATUS varchar(35),")
                            .append("   BEHAVIOURDESCRIPTION longvarchar,")
                            .append("   ADDFREQUENCY varchar(50),")
                            .append("   ELEMENTTYPE varchar(50),")
                            .append("   FEEDINGCLASS varchar(50),")
                            .append("   LIFESPAN varchar(50),")
                            .append("   REFERENCEID varchar(50)")
                            .append(")").toString();
        createLocationsTable = new StringBuilder("CREATE TABLE LOCATIONS ")
                            .append("(")
                            .append("   NAME varchar(150) PRIMARY KEY NOT NULL,")
                            .append("   DESCRIPTION longvarchar,")
                            .append("   PROVINCE varchar(35),")
                            .append("   RATING varchar(50),")
                            .append("   GAMEVIEWINGRATING varchar(50),")
                            .append("   HABITATTYPE varchar(50),")
                            .append("   ACCOMMODATIONTYPE varchar(150),")
                            .append("   CATERING varchar(50),")
                            .append("   CONTACTNUMBERS varchar(50),")
                            .append("   WEBSITE varchar(100),")
                            .append("   EMAIL varchar(100),")
                            .append("   DIRECTIONS longvarchar,")
                            .append("   LATITUDEINDICATOR varchar(10),")
                            .append("   LATDEGREES int,")
                            .append("   LATMINUTES int,")
                            .append("   LATSECONDSFLOAT float(52),")
                            .append("   LONGITUDEINDICATOR varchar(10),")
                            .append("   LONDEGREES int,")
                            .append("   LONMINUTES int,")
                            .append("   LONSECONDSFLOAT float(52)")
                            .append(")").toString();
        createVisitsTable = new StringBuilder("CREATE TABLE VISITS ")
                            .append("(")
                            .append("   NAME varchar(150) PRIMARY KEY NOT NULL,")
                            .append("   STARTDATE date,")
                            .append("   ENDDATE date,")
                            .append("   DESCRIPTION longvarchar,")
                            .append("   GAMEWATCHINGINTENSITY varchar(50),")
                            .append("   VISITTYPE varchar(50),")
                            .append("   LOCATIONNAME varchar(150)")
                            .append(")").toString();
        createSightingsTable = new StringBuilder("CREATE TABLE SIGHTINGS ")
                            .append("(")
                            .append("   SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL,")
                            .append("   SIGHTINGDATE timestamp NOT NULL,")
                            .append("   ELEMENTNAME varchar(150) NOT NULL,")
                            .append("   LOCATIONNAME varchar(150) NOT NULL,")
                            .append("   VISITNAME varchar(150) NOT NULL,")
                            .append("   TIMEOFDAY varchar(50),")
                            .append("   WEATHER varchar(50),")
                            .append("   AREATYPE varchar(50),")
                            .append("   VIEWRATING varchar(50),")
                            .append("   CERTAINTY varchar(50),")
                            .append("   NUMBEROFELEMENTS int,")
                            .append("   DETAILS longvarchar,")
                            .append("   LATITUDEINDICATOR varchar(10),")
                            .append("   LATDEGREES int,")
                            .append("   LATMINUTES int,")
                            .append("   LATSECONDSFLOAT float(52),")
                            .append("   LONGITUDEINDICATOR varchar(10),")
                            .append("   LONDEGREES int,")
                            .append("   LONMINUTES int,")
                            .append("   LONSECONDSFLOAT float(52),")
                            .append("   SIGHTINGEVIDENCE varchar(50),")
                            .append("   MOONLIGHT varchar(50),")
                            .append("   MOONPHASE int")
                            .append(")").toString();
        createFilesTable = new StringBuilder("CREATE TABLE FILES ")
                            .append("(")
                            .append("   ID varchar(175),")
                            .append("   FILENAME varchar(255),")
                            .append("   FILEPATH varchar(500),")
                            .append("   ORIGINALPATH varchar(500),")
                            .append("   FILETYPE varchar(50),")
                            .append("   UPLOADDATE date,")
                            .append("   ISDEFAULT smallint")
                            .append(")").toString();
        createWildLogTable = new StringBuilder("CREATE TABLE WILDLOG ")
                            .append("(")
                            .append("   VERSION int DEFAULT 2,")
                            .append("   DEFAULTLATITUDE float(52) DEFAULT -28.7,")
                            .append("   DEFAULTLONGITUDE float(52) DEFAULT 24.7,")
                            .append("   DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5")
                            .append(")").toString();
    }

   // Methods
    @Override
    public void close() {
        // Close Connection
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    @Override
    public Element find(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        Element tempElement = null;
        try {
            String sql = "SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName().replaceAll("'", "''") + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempElement = new Element();
                tempElement.setPrimaryName(results.getString("PRIMARYNAME"));
                tempElement.setOtherName(results.getString("OTHERNAME"));
                tempElement.setScientificName(results.getString("SCIENTIFICNAME"));
                tempElement.setDescription(results.getString("DESCRIPTION"));
                tempElement.setDistribution(results.getString("DISTRIBUTION"));
                tempElement.setNutrition(results.getString("NUTRITION"));
                tempElement.setWaterDependance(WaterDependancy.getEnumFromText(results.getString("WATERDEPENDANCE")));
                tempElement.setSizeMaleMin(results.getDouble("SIZEMALEMIN"));
                tempElement.setSizeMaleMax(results.getDouble("SIZEMALEMAX"));
                tempElement.setSizeFemaleMin(results.getDouble("SIZEFEMALEMIN"));
                tempElement.setSizeFemaleMax(results.getDouble("SIZEFEMALEMAX"));
                tempElement.setSizeUnit(UnitsSize.getEnumFromText(results.getString("SIZEUNIT")));
                tempElement.setSizeType(SizeType.getEnumFromText(results.getString("SIZETYPE")));
                tempElement.setWeightMaleMin(results.getDouble("WEIGHTMALEMIN"));
                tempElement.setWeightMaleMax(results.getDouble("WEIGHTMALEMAX"));
                tempElement.setWeightFemaleMin(results.getDouble("WEIGHTFEMALEMIN"));
                tempElement.setWeightFemaleMax(results.getDouble("WEIGHTFEMALEMAX"));
                tempElement.setWeightUnit(UnitsWeight.getEnumFromText(results.getString("WEIGHTUNIT")));
                tempElement.setBreedingDuration(results.getString("BREEDINGDURATION"));
                tempElement.setBreedingNumber(results.getString("BREEDINGNUMBER"));
                tempElement.setWishListRating(WishRating.getEnumFromText(results.getString("WISHLISTRATING")));
                tempElement.setDiagnosticDescription(results.getString("DIAGNOSTICDESCRIPTION"));
                tempElement.setActiveTime(ActiveTime.getEnumFromText(results.getString("ACTIVETIME")));
                tempElement.setEndangeredStatus(EndangeredStatus.getEnumFromText(results.getString("ENDANGEREDSTATUS")));
                tempElement.setBehaviourDescription(results.getString("BEHAVIOURDESCRIPTION"));
                tempElement.setAddFrequency(AddFrequency.getEnumFromText(results.getString("ADDFREQUENCY")));
                tempElement.setType(ElementType.getEnumFromText(results.getString("ELEMENTTYPE")));
                tempElement.setFeedingClass(FeedingClass.getEnumFromText(results.getString("FEEDINGCLASS")));
                tempElement.setLifespan(results.getString("LIFESPAN"));
                tempElement.setReferenceID(results.getString("REFERENCEID"));
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempElement;
    }

    @Override
    public Location find(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        Location tempLocation = null;
        try {
            String sql = "SELECT * FROM LOCATIONS WHERE NAME = '" + inLocation.getName().replaceAll("'", "''") + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setProvince(Province.getEnumFromText(results.getString("PROVINCE")));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(Habitat.getEnumFromText(results.getString("HABITATTYPE")));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempLocation;
    }

    @Override
    public Visit find(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        Visit tempVisit = null;
        try {
            String sql = "SELECT * FROM VISITS WHERE NAME = '" + inVisit.getName().replaceAll("'", "''") + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempVisit = new Visit();
                tempVisit.setName(results.getString("NAME"));
                tempVisit.setStartDate(results.getDate("STARTDATE"));
                tempVisit.setEndDate(results.getDate("ENDDATE"));
                tempVisit.setDescription(results.getString("DESCRIPTION"));
                tempVisit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(results.getString("GAMEWATCHINGINTENSITY")));
                tempVisit.setType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                tempVisit.setLocationName(results.getString("LOCATIONNAME"));
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempVisit;
    }

    @Override
    public Sighting find(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        Sighting tempSighting = null;
        try {
            String sql = "SELECT * FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempSighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                tempSighting.setMoonPhase(results.getInt("MOONPHASE"));
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempSighting;
    }

    @Override
    public WildLogOptions find(WildLogOptions inWildLogOptions) {
        Statement state = null;
        ResultSet results = null;
        try {
            String sql = "SELECT * FROM WILDLOG";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                inWildLogOptions.setDatabaseVersion(results.getInt("VERSION"));
                inWildLogOptions.setDefaultLatitude(results.getDouble("DEFAULTLATITUDE"));
                inWildLogOptions.setDefaultLongitude(results.getDouble("DEFAULTLONGITUDE"));
                inWildLogOptions.setDefaultSlideshowSpeed(results.getFloat("DEFAULTSLIDESHOWSPEED"));
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return inWildLogOptions;
    }

    @Override
    public List<Element> list(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        List<Element> tempList = new ArrayList<Element>();
        try {
            String sql = "SELECT * FROM ELEMENTS";
            if (inElement.getPrimaryName() != null && inElement.getType() == null)
                sql = sql + " WHERE PRIMARYNAME like '%" + inElement.getPrimaryName().replaceAll("'", "''") + "%'";
            else
            if (inElement.getPrimaryName() == null && inElement.getType() != null)
                sql = sql + " WHERE ELEMENTTYPE = '" + inElement.getType() + "'";
            else
            if (inElement.getPrimaryName() != null && inElement.getType() != null)
                sql = sql + " WHERE PRIMARYNAME like '%" + inElement.getPrimaryName().replaceAll("'", "''") + "%' AND ELEMENTTYPE = '" + inElement.getType() + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Element tempElement = new Element();
                tempElement.setPrimaryName(results.getString("PRIMARYNAME"));
                tempElement.setOtherName(results.getString("OTHERNAME"));
                tempElement.setScientificName(results.getString("SCIENTIFICNAME"));
                tempElement.setDescription(results.getString("DESCRIPTION"));
                tempElement.setDistribution(results.getString("DISTRIBUTION"));
                tempElement.setNutrition(results.getString("NUTRITION"));
                tempElement.setWaterDependance(WaterDependancy.getEnumFromText(results.getString("WATERDEPENDANCE")));
                tempElement.setSizeMaleMin(results.getDouble("SIZEMALEMIN"));
                tempElement.setSizeMaleMax(results.getDouble("SIZEMALEMAX"));
                tempElement.setSizeFemaleMin(results.getDouble("SIZEFEMALEMIN"));
                tempElement.setSizeFemaleMax(results.getDouble("SIZEFEMALEMAX"));
                tempElement.setSizeUnit(UnitsSize.getEnumFromText(results.getString("SIZEUNIT")));
                tempElement.setSizeType(SizeType.getEnumFromText(results.getString("SIZETYPE")));
                tempElement.setWeightMaleMin(results.getDouble("WEIGHTMALEMIN"));
                tempElement.setWeightMaleMax(results.getDouble("WEIGHTMALEMAX"));
                tempElement.setWeightFemaleMin(results.getDouble("WEIGHTFEMALEMIN"));
                tempElement.setWeightFemaleMax(results.getDouble("WEIGHTFEMALEMAX"));
                tempElement.setWeightUnit(UnitsWeight.getEnumFromText(results.getString("WEIGHTUNIT")));
                tempElement.setBreedingDuration(results.getString("BREEDINGDURATION"));
                tempElement.setBreedingNumber(results.getString("BREEDINGNUMBER"));
                tempElement.setWishListRating(WishRating.getEnumFromText(results.getString("WISHLISTRATING")));
                tempElement.setDiagnosticDescription(results.getString("DIAGNOSTICDESCRIPTION"));
                tempElement.setActiveTime(ActiveTime.getEnumFromText(results.getString("ACTIVETIME")));
                tempElement.setEndangeredStatus(EndangeredStatus.getEnumFromText(results.getString("ENDANGEREDSTATUS")));
                tempElement.setBehaviourDescription(results.getString("BEHAVIOURDESCRIPTION"));
                tempElement.setAddFrequency(AddFrequency.getEnumFromText(results.getString("ADDFREQUENCY")));
                tempElement.setType(ElementType.getEnumFromText(results.getString("ELEMENTTYPE")));
                tempElement.setFeedingClass(FeedingClass.getEnumFromText(results.getString("FEEDINGCLASS")));
                tempElement.setLifespan(results.getString("LIFESPAN"));
                tempElement.setReferenceID(results.getString("REFERENCEID"));
                tempList.add(tempElement);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public List<Location> list(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        List<Location> tempList = new ArrayList<Location>();
        try {
            String sql = "SELECT * FROM LOCATIONS";
            if (inLocation.getName() != null)
                sql = sql + " WHERE NAME like '%" + inLocation.getName().replaceAll("'", "''") + "%'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Location tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setProvince(Province.getEnumFromText(results.getString("PROVINCE")));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(Habitat.getEnumFromText(results.getString("HABITATTYPE")));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempList.add(tempLocation);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public List<Visit> list(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        List<Visit> tempList = new ArrayList<Visit>();
        try {
            String sql = "SELECT * FROM VISITS";
            if (inVisit.getName() != null)
                sql = sql + " WHERE NAME = '" + inVisit.getName().replaceAll("'", "''") + "'";
            else
            if (inVisit.getLocationName() != null)
                sql = sql + " WHERE LOCATIONNAME = '" + inVisit.getLocationName().replaceAll("'", "''") + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Visit tempVisit = new Visit();
                tempVisit.setName(results.getString("NAME"));
                tempVisit.setStartDate(results.getDate("STARTDATE"));
                tempVisit.setEndDate(results.getDate("ENDDATE"));
                tempVisit.setDescription(results.getString("DESCRIPTION"));
                tempVisit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(results.getString("GAMEWATCHINGINTENSITY")));
                tempVisit.setType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                tempVisit.setLocationName(results.getString("LOCATIONNAME"));
                tempList.add(tempVisit);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public List<Sighting> list(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = "SELECT * FROM SIGHTINGS";
            if (inSighting.getSightingCounter() > 0)
                sql = sql + " WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "";
            else
            if (inSighting.getElementName() != null)
                sql = sql + " WHERE ELEMENTNAME = '" + inSighting.getElementName().replaceAll("'", "''") + "'";
            else
            if (inSighting.getLocationName() != null)
                sql = sql + " WHERE LOCATIONNAME = '" + inSighting.getLocationName().replaceAll("'", "''") + "'";
            else
            if (inSighting.getVisitName() != null)
                sql = sql + " WHERE VISITNAME = '" + inSighting.getVisitName().replaceAll("'", "''") + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempSighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                tempSighting.setMoonPhase(results.getInt("MOONPHASE"));
                tempList.add(tempSighting);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public List<WildLogFile> list(WildLogFile inFile) {
// FIXME: This call might be starting to give performance issues...
        Statement state = null;
        ResultSet results = null;
        List<WildLogFile> tempList = new ArrayList<WildLogFile>();
        try {
            String sql = "SELECT * FROM FILES";
            if (inFile.getId() != null)
                sql = sql + " WHERE ID = '" + inFile.getId().replaceAll("'", "''") + "'";
            sql = sql + " ORDER BY ISDEFAULT desc, FILEPATH";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                WildLogFile tempFoto = new WildLogFile();
                tempFoto.setId(results.getString("ID"));
                tempFoto.setFilename(results.getString("FILENAME"));
                tempFoto.setFileLocation(results.getString("FILEPATH"));
                tempFoto.setOriginalFotoLocation(results.getString("ORIGINALPATH"));
                tempFoto.setFotoType(WildLogFileType.getEnumFromText(results.getString("FILETYPE")));
                tempFoto.setDate(results.getDate("UPLOADDATE"));
                tempFoto.setDefaultFile(results.getBoolean("ISDEFAULT"));
                tempList.add(tempFoto);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public List<Sighting> searchSightingOnDate(Date inStartDate, Date inEndDate) {
        Statement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = "SELECT * FROM SIGHTINGS";
            if (inStartDate != null && inEndDate != null)
                sql = sql + " WHERE SIGHTINGDATE > '" + new java.sql.Date(inStartDate.getTime()) + "' AND SIGHTINGDATE < '" + new java.sql.Date(inEndDate.getTime()) + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempSighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                tempSighting.setMoonPhase(results.getInt("MOONPHASE"));
                tempList.add(tempSighting);
            }

        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return tempList;
    }

    @Override
    public boolean createOrUpdate(Element inElement, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + limitLength(inElement.getPrimaryName().replaceAll("'", "''"), 150) + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET ELEMENTNAME = '" + limitLength(inElement.getPrimaryName().replaceAll("'", "''"), 150) + "' WHERE ELEMENTNAME = '" + inOldName.replaceAll("'", "''") + "'");
                        state.execute("UPDATE FILES SET ID = '" + "ELEMENT-" + limitLength(inElement.getPrimaryName().replaceAll("'", "''"), 150) + "' WHERE ID = '" + "ELEMENT-" + inOldName.replaceAll("'", "''") + "'");
                    }
                }
                // Update
                StringBuilder sql = new StringBuilder("UPDATE ELEMENTS SET ")
                    .append("PRIMARYNAME = '").append(limitLength(inElement.getPrimaryName().replaceAll("'", "''"), 150)).append("', ")
                    .append("OTHERNAME = '").append(limitLength(inElement.getOtherName().replaceAll("'", "''"), 150)).append("', ")
                    .append("SCIENTIFICNAME = '").append(limitLength(inElement.getScientificName().replaceAll("'", "''"), 150)).append("', ")
                    .append("DESCRIPTION = '").append(inElement.getDescription().replaceAll("'", "''")).append("', ")
                    .append("DISTRIBUTION = '").append(inElement.getDistribution().replaceAll("'", "''")).append("', ")
                    .append("NUTRITION = '").append(inElement.getNutrition().replaceAll("'", "''")).append("', ")
                    .append("WATERDEPENDANCE = '").append(inElement.getWaterDependance()).append("', ")
                    .append("SIZEMALEMIN = ").append(inElement.getSizeMaleMin()).append(", ")
                    .append("SIZEMALEMAX = ").append(inElement.getSizeMaleMax()).append(", ")
                    .append("SIZEFEMALEMIN = ").append(inElement.getSizeFemaleMin()).append(", ")
                    .append("SIZEFEMALEMAX = ").append(inElement.getSizeFemaleMax()).append(", ")
                    .append("SIZEUNIT = '").append(inElement.getSizeUnit()).append("', ")
                    .append("SIZETYPE = '").append(inElement.getSizeType()).append("', ")
                    .append("WEIGHTMALEMIN = ").append(inElement.getWeightMaleMin()).append(", ")
                    .append("WEIGHTMALEMAX = ").append(inElement.getWeightMaleMax()).append(", ")
                    .append("WEIGHTFEMALEMIN = ").append(inElement.getWeightFemaleMin()).append(", ")
                    .append("WEIGHTFEMALEMAX = ").append(inElement.getWeightFemaleMax()).append(", ")
                    .append("WEIGHTUNIT = '").append(inElement.getWeightUnit()).append("', ")
                    .append("BREEDINGDURATION = '").append(limitLength(inElement.getBreedingDuration().replaceAll("'", "''"), 50)).append("', ")
                    .append("BREEDINGNUMBER = '").append(limitLength(inElement.getBreedingNumber().replaceAll("'", "''"), 50)).append("', ")
                    .append("WISHLISTRATING = '").append(inElement.getWishListRating()).append("', ")
                    .append("DIAGNOSTICDESCRIPTION = '").append(inElement.getDiagnosticDescription().replaceAll("'", "''")).append("', ")
                    .append("ACTIVETIME = '").append(inElement.getActiveTime()).append("', ")
                    .append("ENDANGEREDSTATUS = '").append(inElement.getEndangeredStatus()).append("', ")
                    .append("BEHAVIOURDESCRIPTION = '").append(inElement.getBehaviourDescription().replaceAll("'", "''")).append("', ")
                    .append("ADDFREQUENCY = '").append(inElement.getAddFrequency()).append("', ")
                    .append("ELEMENTTYPE = '").append(inElement.getType()).append("', ")
                    .append("FEEDINGCLASS = '").append(inElement.getFeedingClass()).append("', ")
                    .append("LIFESPAN = '").append(limitLength(inElement.getLifespan().replaceAll("'", "''"), 50)).append("', ")
                    .append("REFERENCEID = '").append(limitLength(inElement.getReferenceID().replaceAll("'", "''"), 50)).append("'")
                    .append(" WHERE PRIMARYNAME = '").append(inOldName.replaceAll("'", "''")).append("'");
                state.executeUpdate(sql.toString());
            }
            else {
                results = state.executeQuery("SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName().replaceAll("'", "''") + "'");
                if (results.next())
                    return false;
                // Insert
                StringBuilder sql = new StringBuilder("INSERT INTO ELEMENTS (PRIMARYNAME,OTHERNAME,SCIENTIFICNAME,DESCRIPTION,DISTRIBUTION,NUTRITION,WATERDEPENDANCE,SIZEMALEMIN,SIZEMALEMAX,SIZEFEMALEMIN,SIZEFEMALEMAX,SIZEUNIT,SIZETYPE,WEIGHTMALEMIN,WEIGHTMALEMAX,WEIGHTFEMALEMIN,WEIGHTFEMALEMAX,WEIGHTUNIT,BREEDINGDURATION,BREEDINGNUMBER,WISHLISTRATING,DIAGNOSTICDESCRIPTION,ACTIVETIME,ENDANGEREDSTATUS,BEHAVIOURDESCRIPTION,ADDFREQUENCY,ELEMENTTYPE,FEEDINGCLASS,LIFESPAN,REFERENCEID) VALUES (")
                    .append("'").append(limitLength(inElement.getPrimaryName().replaceAll("'", "''"), 150)).append("', ")
                    .append("'").append(limitLength(inElement.getOtherName().replaceAll("'", "''"), 150)).append("', ")
                    .append("'").append(limitLength(inElement.getScientificName().replaceAll("'", "''"), 150)).append("', ")
                    .append("'").append(inElement.getDescription().replaceAll("'", "''")).append("', ")
                    .append("'").append(inElement.getDistribution().replaceAll("'", "''")).append("', ")
                    .append("'").append(inElement.getNutrition().replaceAll("'", "''")).append("', ")
                    .append("'").append(inElement.getWaterDependance()).append("', ")
                    .append("").append(inElement.getSizeMaleMin()).append(", ")
                    .append("").append(inElement.getSizeMaleMax()).append(", ")
                    .append("").append(inElement.getSizeFemaleMin()).append(", ")
                    .append("").append(inElement.getSizeFemaleMax()).append(", ")
                    .append("'").append(inElement.getSizeUnit()).append("', ")
                    .append("'").append(inElement.getSizeType()).append("', ")
                    .append("").append(inElement.getWeightMaleMin()).append(", ")
                    .append("").append(inElement.getWeightMaleMax()).append(", ")
                    .append("").append(inElement.getWeightFemaleMin()).append(", ")
                    .append("").append(inElement.getWeightFemaleMax()).append(", ")
                    .append("'").append(inElement.getWeightUnit()).append("', ")
                    .append("'").append(limitLength(inElement.getBreedingDuration().replaceAll("'", "''"), 50)).append("', ")
                    .append("'").append(limitLength(inElement.getBreedingNumber().replaceAll("'", "''"), 50)).append("', ")
                    .append("'").append(inElement.getWishListRating()).append("', ")
                    .append("'").append(inElement.getDiagnosticDescription().replaceAll("'", "''")).append("', ")
                    .append("'").append(inElement.getActiveTime()).append("', ")
                    .append("'").append(inElement.getEndangeredStatus()).append("', ")
                    .append("'").append(inElement.getBehaviourDescription().replaceAll("'", "''")).append("', ")
                    .append("'").append(inElement.getAddFrequency()).append("', ")
                    .append("'").append(inElement.getType()).append("', ")
                    .append("'").append(inElement.getFeedingClass()).append("', ")
                    .append("'").append(limitLength(inElement.getLifespan().replaceAll("'", "''"), 50)).append("', ")
                    .append("'").append(limitLength(inElement.getReferenceID().replaceAll("'", "''"), 50)).append("'")
                    .append(")");
                state.execute(sql.toString());
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(Location inLocation, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM LOCATIONS WHERE NAME = '" + limitLength(inLocation.getName().replaceAll("'", "''"), 150) + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET LOCATIONNAME = '" + limitLength(inLocation.getName().replaceAll("'", "''"), 150) + "' WHERE LOCATIONNAME = '" + inOldName.replaceAll("'", "''") + "'");
                        state.execute("UPDATE VISITS SET LOCATIONNAME = '" + limitLength(inLocation.getName().replaceAll("'", "''"), 150) + "' WHERE LOCATIONNAME = '" + inOldName.replaceAll("'", "''") + "'");
                        state.execute("UPDATE FILES SET ID = '" + "LOCATION-" + limitLength(inLocation.getName().replaceAll("'", "''"), 150) + "' WHERE ID = '" + "LOCATION-" + inOldName.replaceAll("'", "''") + "'");
                    }
                }
                // Update
                StringBuilder sql = new StringBuilder("UPDATE LOCATIONS SET ")
                    .append("NAME = '").append(limitLength(inLocation.getName().replaceAll("'", "''"), 150)).append("', ")
                    .append("DESCRIPTION = '").append(inLocation.getDescription().replaceAll("'", "''")).append("', ")
                    .append("PROVINCE = '").append(inLocation.getProvince()).append("', ")
                    .append("RATING = '").append(inLocation.getRating()).append("', ")
                    .append("GAMEVIEWINGRATING = '").append(inLocation.getGameViewingRating()).append("', ")
                    .append("HABITATTYPE = '").append(inLocation.getHabitatType()).append("', ")
                    .append("ACCOMMODATIONTYPE = '").append(inLocation.getAccommodationType()).append("', ")
                    .append("CATERING = '").append(inLocation.getCatering()).append("', ")
                    .append("CONTACTNUMBERS = '").append(limitLength(inLocation.getContactNumbers().replaceAll("'", "''"), 50)).append("', ")
                    .append("WEBSITE = '").append(limitLength(inLocation.getWebsite().replaceAll("'", "''"), 100)).append("', ")
                    .append("EMAIL = '").append(limitLength(inLocation.getEmail().replaceAll("'", "''"), 100)).append("', ")
                    .append("DIRECTIONS = '").append(inLocation.getDirections().replaceAll("'", "''")).append("', ")
                    .append("LATITUDEINDICATOR = '").append(inLocation.getLatitude()).append("', ")
                    .append("LATDEGREES = ").append(inLocation.getLatDegrees()).append(", ")
                    .append("LATMINUTES = ").append(inLocation.getLatMinutes()).append(", ")
                    .append("LATSECONDSFLOAT = ").append(inLocation.getLatSecondsFloat()).append(", ")
                    .append("LONGITUDEINDICATOR = '").append(inLocation.getLongitude()).append("', ")
                    .append("LONDEGREES = ").append(inLocation.getLonDegrees()).append(", ")
                    .append("LONMINUTES = ").append(inLocation.getLonMinutes()).append(", ")
                    .append("LONSECONDSFLOAT = ").append(inLocation.getLonSecondsFloat()).append("")
                    .append(" WHERE NAME = '").append(inOldName.replaceAll("'", "''")).append("'");
                state.executeUpdate(sql.toString());
            }
            else {
                results = state.executeQuery("SELECT * FROM LOCATIONS WHERE NAME = '" + inLocation.getName().replaceAll("'", "''") + "'");
                if (results.next())
                    return false;
                // Insert
                StringBuilder sql = new StringBuilder("INSERT INTO LOCATIONS (NAME,DESCRIPTION,PROVINCE,RATING,GAMEVIEWINGRATING,HABITATTYPE,ACCOMMODATIONTYPE,CATERING,CONTACTNUMBERS,WEBSITE,EMAIL,DIRECTIONS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDSFLOAT,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDSFLOAT) VALUES (")
                    .append("'").append(limitLength(inLocation.getName().replaceAll("'", "''"), 150)).append("', ")
                    .append("'").append(inLocation.getDescription().replaceAll("'", "''")).append("', ")
                    .append("'").append(inLocation.getProvince()).append("', ")
                    .append("'").append(inLocation.getRating()).append("', ")
                    .append("'").append(inLocation.getGameViewingRating()).append("', ")
                    .append("'").append(inLocation.getHabitatType()).append("', ")
                    .append("'").append(inLocation.getAccommodationType()).append("', ")
                    .append("'").append(inLocation.getCatering()).append("', ")
                    .append("'").append(limitLength(inLocation.getContactNumbers().replaceAll("'", "''"), 50)).append("', ")
                    .append("'").append(limitLength(inLocation.getWebsite().replaceAll("'", "''"), 100)).append("', ")
                    .append("'").append(limitLength(inLocation.getEmail().replaceAll("'", "''"), 100)).append("', ")
                    .append("'").append(inLocation.getDirections().replaceAll("'", "''")).append("', ")
                    .append("'").append(inLocation.getLatitude()).append("', ")
                    .append("").append(inLocation.getLatDegrees()).append(", ")
                    .append("").append(inLocation.getLatMinutes()).append(", ")
                    .append("").append(inLocation.getLatSecondsFloat()).append(", ")
                    .append("'").append(inLocation.getLongitude()).append("', ")
                    .append("").append(inLocation.getLonDegrees()).append(", ")
                    .append("").append(inLocation.getLonMinutes()).append(", ")
                    .append("").append(inLocation.getLonSecondsFloat()).append("")
                    .append(")");
                state.execute(sql.toString());
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(Visit inVisit, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM VISITS WHERE NAME = '" + limitLength(inVisit.getName().replaceAll("'", "''"), 150) + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET VISITNAME = '" + limitLength(inVisit.getName().replaceAll("'", "''"), 150) + "' WHERE VISITNAME = '" + inOldName.replaceAll("'", "''") + "'");
                        state.execute("UPDATE FILES SET ID = '" + "VISIT-" + limitLength(inVisit.getName().replaceAll("'", "''"), 150) + "' WHERE ID = '" + "VISIT-" + inOldName.replaceAll("'", "''") + "'");
                    }
                }
                // Update
                StringBuilder sql = new StringBuilder("UPDATE VISITS SET ")
                    .append("NAME = '").append(limitLength(inVisit.getName().replaceAll("'", "''"), 150)).append("', ");
                if (inVisit.getStartDate() != null)
                    sql.append("STARTDATE = '").append(new java.sql.Date(inVisit.getStartDate().getTime())).append("', ");
                else
                    sql.append("STARTDATE = null, ");
                if (inVisit.getEndDate() != null)
                    sql.append("ENDDATE = '").append(new java.sql.Date(inVisit.getEndDate().getTime())).append("', ");
                else
                    sql.append("ENDDATE = null, ");
                sql.append("DESCRIPTION = '").append(inVisit.getDescription().replaceAll("'", "''")).append("', ")
                    .append("GAMEWATCHINGINTENSITY = '").append(inVisit.getGameWatchingIntensity()).append("', ")
                    .append("VISITTYPE = '").append(inVisit.getType()).append("', ")
                    .append("LOCATIONNAME = '").append(inVisit.getLocationName().replaceAll("'", "''")).append("'")
                    .append(" WHERE NAME = '").append(inOldName.replaceAll("'", "''")).append("'");
                state.executeUpdate(sql.toString());
            }
            else {
                results = state.executeQuery("SELECT * FROM VISITS WHERE NAME = '" + inVisit.getName().replaceAll("'", "''") + "'");
                if (results.next())
                    return false;
                // Insert
                StringBuilder sql = new StringBuilder("INSERT INTO VISITS (NAME,STARTDATE,ENDDATE,DESCRIPTION,GAMEWATCHINGINTENSITY,VISITTYPE,LOCATIONNAME) VALUES (")
                    .append("'").append(limitLength(inVisit.getName().replaceAll("'", "''"), 150)).append("', ");
                if (inVisit.getStartDate() != null)
                    sql.append("'").append(new java.sql.Date(inVisit.getStartDate().getTime())).append("', ");
                else
                    sql.append("null, ");
                if (inVisit.getEndDate() != null)
                    sql.append("'").append(new java.sql.Date(inVisit.getEndDate().getTime())).append("', ");
                else
                    sql.append("null, ");
                sql.append("'").append(inVisit.getDescription().replaceAll("'", "''")).append("', ")
                    .append("'").append(inVisit.getGameWatchingIntensity()).append("', ")
                    .append("'").append(inVisit.getType()).append("', ")
                    .append("'").append(inVisit.getLocationName().replaceAll("'", "''")).append("'")
                    .append(")");
                state.execute(sql.toString());
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inSighting.getSightingCounter() > 0) {
                // Update
                StringBuilder sql = new StringBuilder("UPDATE SIGHTINGS SET ")
                    .append("SIGHTINGCOUNTER = ").append(inSighting.getSightingCounter()).append(", ");
                if (inSighting.getDate() != null)
                    sql.append("SIGHTINGDATE = '").append(new java.sql.Timestamp(inSighting.getDate().getTime())).append("', ");
                else
                    sql.append("STARTDATE = null, ");
                sql.append("ELEMENTNAME = '").append(inSighting.getElementName().replaceAll("'", "''")).append("', ")
                    .append("LOCATIONNAME = '").append(inSighting.getLocationName().replaceAll("'", "''")).append("', ")
                    .append("VISITNAME = '").append(inSighting.getVisitName().replaceAll("'", "''")).append("', ")
                    .append("TIMEOFDAY = '").append(inSighting.getTimeOfDay()).append("', ")
                    .append("WEATHER = '").append(inSighting.getWeather()).append("', ")
                    .append("AREATYPE = '").append(inSighting.getAreaType()).append("', ")
                    .append("VIEWRATING = '").append(inSighting.getViewRating()).append("', ")
                    .append("CERTAINTY = '").append(inSighting.getCertainty()).append("', ")
                    .append("NUMBEROFELEMENTS = ").append(inSighting.getNumberOfElements()).append(", ")
                    .append("DETAILS = '").append(inSighting.getDetails().replaceAll("'", "''")).append("', ")
                    .append("LATITUDEINDICATOR = '").append(inSighting.getLatitude()).append("', ")
                    .append("LATDEGREES = ").append(inSighting.getLatDegrees()).append(", ")
                    .append("LATMINUTES = ").append(inSighting.getLatMinutes()).append(", ")
                    .append("LATSECONDSFLOAT = ").append(inSighting.getLatSecondsFloat()).append(", ")
                    .append("LONGITUDEINDICATOR = '").append(inSighting.getLongitude()).append("', ")
                    .append("LONDEGREES = ").append(inSighting.getLonDegrees()).append(", ")
                    .append("LONMINUTES = ").append(inSighting.getLonMinutes()).append(", ")
                    .append("LONSECONDSFLOAT = ").append(inSighting.getLonSecondsFloat()).append(", ")
                    .append("SIGHTINGEVIDENCE = '").append(inSighting.getSightingEvidence()).append("', ")
                    .append("MOONPHASE = ").append(inSighting.getMoonPhase()).append(", ")
                    .append("MOONLIGHT = '").append(inSighting.getMoonlight()).append("'")
                    .append(" WHERE SIGHTINGCOUNTER = ").append(inSighting.getSightingCounter()).append("");
                state.executeUpdate(sql.toString());
            }
            else {
                results = state.executeQuery("SELECT MAX(SIGHTINGCOUNTER) FROM SIGHTINGS");
                if (results.next()) {
                    long sightingCounter = results.getLong(1);
                    sightingCounter++;
                    inSighting.setSightingCounter(sightingCounter); // Need to set it for images to upload coorectly
                    // Insert
                    StringBuilder sql = new StringBuilder("INSERT INTO SIGHTINGS (SIGHTINGCOUNTER,SIGHTINGDATE,ELEMENTNAME,LOCATIONNAME,VISITNAME,TIMEOFDAY,WEATHER,AREATYPE,VIEWRATING,CERTAINTY,NUMBEROFELEMENTS,DETAILS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDSFLOAT,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDSFLOAT,SIGHTINGEVIDENCE,MOONPHASE,MOONLIGHT) VALUES (")
                        .append("").append(inSighting.getSightingCounter()).append(", ");
                    if (inSighting.getDate() != null)
                        sql.append("'").append(new java.sql.Timestamp(inSighting.getDate().getTime())).append("', ");
                    else
                        sql.append("null, ");
                    sql.append("'").append(inSighting.getElementName().replaceAll("'", "''")).append("', ")
                        .append("'").append(inSighting.getLocationName().replaceAll("'", "''")).append("', ")
                        .append("'").append(inSighting.getVisitName().replaceAll("'", "''")).append("', ")
                        .append("'").append(inSighting.getTimeOfDay()).append("', ")
                        .append("'").append(inSighting.getWeather()).append("', ")
                        .append("'").append(inSighting.getAreaType()).append("', ")
                        .append("'").append(inSighting.getViewRating()).append("', ")
                        .append("'").append(inSighting.getCertainty()).append("', ")
                        .append("").append(inSighting.getNumberOfElements()).append(", ")
                        .append("'").append(inSighting.getDetails().replaceAll("'", "''")).append("', ")
                        .append("'").append(inSighting.getLatitude()).append("', ")
                        .append("").append(inSighting.getLatDegrees()).append(", ")
                        .append("").append(inSighting.getLatMinutes()).append(", ")
                        .append("").append(inSighting.getLatSecondsFloat()).append(", ")
                        .append("'").append(inSighting.getLongitude()).append("', ")
                        .append("").append(inSighting.getLonDegrees()).append(", ")
                        .append("").append(inSighting.getLonMinutes()).append(", ")
                        .append("").append(inSighting.getLonSecondsFloat()).append(", ")
                        .append("'").append(inSighting.getSightingEvidence()).append("', ")
                        .append("").append(inSighting.getMoonPhase()).append(", ")
                        .append("'").append(inSighting.getMoonlight()).append("'")
                        .append(")");
                    state.execute(sql.toString());
                }
                else
                    return false;
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(WildLogFile inFoto, boolean inUpdate) {
        Statement state = null;
        try {
            state = conn.createStatement();
            if (inUpdate) {
                StringBuilder sql = new StringBuilder("UPDATE FILES SET ")
                    .append("ID = '").append(inFoto.getId().replaceAll("'", "''")).append("', ")
                    .append("FILENAME = '").append(inFoto.getFilename().replaceAll("'", "''")).append("', ")
                    .append("FILEPATH = '").append(inFoto.getFileLocation(false).replaceAll("'", "''")).append("', ")
                    .append("ORIGINALPATH = '").append(inFoto.getOriginalFotoLocation(false).replaceAll("'", "''")).append("', ")
                    .append("FILETYPE = '").append(inFoto.getFotoType()).append("',");
                if (inFoto.getDate() != null)
                    sql.append("UPLOADDATE = '").append(new java.sql.Date(inFoto.getDate().getTime())).append("',");
                else
                    sql.append("UPLOADDATE = null,");
                if (inFoto.isDefaultFile())
                    sql.append("ISDEFAULT = 1");
                else
                    sql.append("ISDEFAULT = 0");
                sql.append("WHERE ORIGINALPATH = '").append(inFoto.getOriginalFotoLocation(false)).append("'");
                state.execute(sql.toString());
            }
            else {
                // Insert
                StringBuilder sql = new StringBuilder("INSERT INTO FILES (ID,FILENAME,FILEPATH,ORIGINALPATH,FILETYPE,UPLOADDATE,ISDEFAULT) VALUES (")
                    .append("'").append(inFoto.getId().replaceAll("'", "''")).append("', ")
                    .append("'").append(inFoto.getFilename().replaceAll("'", "''")).append("', ")
                    .append("'").append(inFoto.getFileLocation(false).replaceAll("'", "''")).append("', ")
                    .append("'").append(inFoto.getOriginalFotoLocation(false).replaceAll("'", "''")).append("', ")
                    .append("'").append(inFoto.getFotoType()).append("',");
                if (inFoto.getDate() != null)
                    sql.append("'").append(new java.sql.Date(inFoto.getDate().getTime())).append("',");
                else
                    sql.append("null,");
                if (inFoto.isDefaultFile())
                    sql.append("1");
                else
                    sql.append("0");
                sql.append(")");
                state.execute(sql.toString());
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(WildLogOptions inWildLogOptions) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            results = state.executeQuery("SELECT * FROM WILDLOG");
            if (results.next()) {
                // Update
                StringBuilder sql = new StringBuilder("UPDATE WILDLOG SET ")
                    .append("VERSION = ").append(inWildLogOptions.getDatabaseVersion()).append(", ")
                    .append("DEFAULTLATITUDE = ").append(inWildLogOptions.getDefaultLatitude()).append(", ")
                    .append("DEFAULTLONGITUDE = ").append(inWildLogOptions.getDefaultLongitude()).append(", ")
                    .append("DEFAULTSLIDESHOWSPEED = ").append(inWildLogOptions.getDefaultSlideshowSpeed()).append("");
                state.executeUpdate(sql.toString());
            }
            else {
                // Insert
                StringBuilder sql = new StringBuilder("INSERT INTO WILDLOG VALUES (DEFAULT, DEFAULT, DEFAULT, DEFAULT)");
                state.execute(sql.toString());
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean delete(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        try {
            // Delete the Element
            state = conn.createStatement();
            state.executeUpdate("DELETE FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName().replaceAll("'", "''") + "'");
            // Delete all Sightings for this element
            results = state.executeQuery("SELECT * FROM SIGHTINGS WHERE ELEMENTNAME = '" + inElement.getPrimaryName().replaceAll("'", "''") + "'");
            while (results.next()) {
                Sighting sighting = new Sighting(results.getLong("SIGHTINGCOUNTER"));
                delete(sighting);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'ELEMENT-" + inElement.getPrimaryName().replaceAll("'", "''") + "'");
            while (results.next()) {
                WildLogFile file = new WildLogFile(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), WildLogFileType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean delete(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Location
            state.executeUpdate("DELETE FROM LOCATIONS WHERE NAME = '" + inLocation.getName().replaceAll("'", "''") + "'");
            // Delete Visits for this location
            results = state.executeQuery("SELECT * FROM VISITS WHERE LOCATIONNAME = '" + inLocation.getName().replaceAll("'", "''") + "'");
            while (results.next()) {
                Visit visit = new Visit(results.getString("NAME"));
                delete(visit);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'LOCATION-" + inLocation.getName().replaceAll("'", "''") + "'");
            while (results.next()) {
                WildLogFile file = new WildLogFile(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), WildLogFileType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean delete(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Visit
            state.executeUpdate("DELETE FROM VISITS WHERE NAME = '" + inVisit.getName().replaceAll("'", "''") + "'");
            // Delete Sightings for this visit
            results = state.executeQuery("SELECT * FROM SIGHTINGS WHERE VISITNAME = '" + inVisit.getName().replaceAll("'", "''") + "'");
            while (results.next()) {
                Sighting sighting = new Sighting(results.getLong("SIGHTINGCOUNTER"));
                delete(sighting);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'VISIT-" + inVisit.getName().replaceAll("'", "''") + "'");
            while (results.next()) {
                WildLogFile file = new WildLogFile(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), WildLogFileType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean delete(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Sightings
            state.executeUpdate("DELETE FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "");
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'SIGHTING-" + inSighting.getSightingCounter() + "'");
            while (results.next()) {
                WildLogFile file = new WildLogFile(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), WildLogFileType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }

    @Override
    public boolean delete(WildLogFile inFoto) {
        // Note: this method only deletes one file at a time
        Statement state = null;
        try {
            state = conn.createStatement();
            // Delete File from database - Note: Do not use FilePath, because it is not unique
            state.executeUpdate("DELETE FROM FILES WHERE ORIGINALPATH = '" + inFoto.getOriginalFotoLocation(false).replaceAll("'", "''") + "'");
            // Delete the file on the PC
            File tempFile = new File(inFoto.getFileLocation(true));
            tempFile.delete();
            tempFile = new File(inFoto.getOriginalFotoLocation(true));
            tempFile.delete();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return true;
    }


    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    protected static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }

    protected void doUpdates() {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            results = state.executeQuery("SELECT * FROM WILDLOG");
            // If there isn't a row create one
            if (!results.next()) {
                createOrUpdate(new WildLogOptions());
            }
            // Read the row
            results = state.executeQuery("SELECT VERSION FROM WILDLOG");
            while (results.next()) {
                if (results.getInt("VERSION") == 0)
                    doUpdate1();
                if (results.getInt("VERSION") == 1)
                    doUpdate2();
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
    }


    // Private update methods
    private void doUpdate1() {
        // This update recreates the tables with IGNORECASE enabled
        // This update might only be relevant for H2, but it is the main
        // supported database...
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Set Ignorecase - Also done when the connection is opened in H2
            state.execute("SET IGNORECASE TRUE");
            // Rename old tables
            state.execute("ALTER TABLE ELEMENTS RENAME TO TEMP_ELEMENTS");
            state.execute("ALTER TABLE LOCATIONS RENAME TO TEMP_LOCATIONS");
            state.execute("ALTER TABLE VISITS RENAME TO TEMP_VISITS");
            state.execute("ALTER TABLE SIGHTINGS RENAME TO TEMP_SIGHTINGS");
            state.execute("ALTER TABLE FILES RENAME TO TEMP_FILES");
            // Create new tables
            state.execute(createElementsTable);
            state.execute(createLocationsTable);
            state.execute(createVisitsTable);
            state.execute(createSightingsTable);
            state.execute(createFilesTable);
            // Copy data accross from old tables
            state.executeUpdate("INSERT INTO ELEMENTS SELECT PRIMARYNAME, OTHERNAME, SCIENTIFICNAME, DESCRIPTION, DISTRIBUTION, NUTRITION, WATERDEPENDANCE, SIZEMALEMIN, SIZEMALEMAX , SIZEFEMALEMIN, SIZEFEMALEMAX, SIZEUNIT, WEIGHTMALEMIN, WEIGHTMALEMAX, WEIGHTFEMALEMIN, WEIGHTFEMALEMAX, WEIGHTUNIT, BREEDINGDURATION, BREEDINGNUMBER, WISHLISTRATING, DIAGNOSTICDESCRIPTION, ACTIVETIME, ENDANGEREDSTATUS, BEHAVIOURDESCRIPTION, ADDFREQUENCY, ELEMENTTYPE, FEEDINGCLASS, LIFESPAN, REFERENCEID FROM TEMP_ELEMENTS");
            state.executeUpdate("INSERT INTO LOCATIONS SELECT * FROM TEMP_LOCATIONS");
            state.executeUpdate("INSERT INTO VISITS SELECT * FROM TEMP_VISITS");
            state.executeUpdate("INSERT INTO SIGHTINGS SELECT * FROM TEMP_SIGHTINGS");
            state.executeUpdate("INSERT INTO FILES SELECT * FROM TEMP_FILES");
            // Drop the old tables
            state.execute("DROP TABLE TEMP_ELEMENTS");
            state.execute("DROP TABLE TEMP_LOCATIONS");
            state.execute("DROP TABLE TEMP_VISITS");
            state.execute("DROP TABLE TEMP_SIGHTINGS");
            state.execute("DROP TABLE TEMP_FILES");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=1");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
    }
    
    private void doUpdate2() {
        // This update adds a column to the options table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the column with default value etc.
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=2");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            // ResultSet
            try {
                if (results != null) {
                    results.close();
                    results = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
    }

    @Override
    public String limitLength(String inString, int inLength) {
        if (inString.trim().length() > inLength)
            return inString.trim().substring(0, inLength);
        else
            return inString.trim();
    }

}
