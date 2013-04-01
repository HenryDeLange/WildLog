package wildlog.data.dbi;

import wildlog.data.utils.UtilsData;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
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
import wildlog.data.enums.CateringType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.SizeType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WishRating;
import wildlog.ui.dialogs.utils.UtilsDialog;

public abstract class DBI_JDBC implements DBI {
    // Variables
    protected static final int WILDLOG_DB_VERSION = 3;
    protected Connection conn;

    protected static final String tableElements = "CREATE TABLE ELEMENTS (PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL, OTHERNAME varchar(150), SCIENTIFICNAME varchar(150), DESCRIPTION longvarchar, DISTRIBUTION longvarchar, NUTRITION longvarchar, WATERDEPENDANCE varchar(50), SIZEMALEMIN float(52), SIZEMALEMAX float(52), SIZEFEMALEMIN float(52), SIZEFEMALEMAX float(52), SIZEUNIT varchar(10), SIZETYPE varchar(50), WEIGHTMALEMIN float(52), WEIGHTMALEMAX float(52), WEIGHTFEMALEMIN float(52), WEIGHTFEMALEMAX float(52), WEIGHTUNIT varchar(10), BREEDINGDURATION varchar(50), BREEDINGNUMBER varchar(50), WISHLISTRATING varchar(50), DIAGNOSTICDESCRIPTION longvarchar, ACTIVETIME varchar(50), ENDANGEREDSTATUS varchar(35), BEHAVIOURDESCRIPTION longvarchar, ADDFREQUENCY varchar(50), ELEMENTTYPE varchar(50), FEEDINGCLASS varchar(50), LIFESPAN varchar(50), REFERENCEID varchar(50))";
    protected static final String tableLocations = "CREATE TABLE LOCATIONS (NAME varchar(150) PRIMARY KEY NOT NULL, DESCRIPTION longvarchar, RATING varchar(50), GAMEVIEWINGRATING varchar(50), HABITATTYPE longvarchar, ACCOMMODATIONTYPE varchar(150), CATERING varchar(50), CONTACTNUMBERS varchar(50), WEBSITE varchar(100), EMAIL varchar(100), DIRECTIONS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double)";
    protected static final String tableVisits = "CREATE TABLE VISITS (NAME varchar(150) PRIMARY KEY NOT NULL, STARTDATE date, ENDDATE date, DESCRIPTION longvarchar, GAMEWATCHINGINTENSITY varchar(50), VISITTYPE varchar(50), LOCATIONNAME varchar(150))";
    protected static final String tableSightings = "CREATE TABLE SIGHTINGS (SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL,   SIGHTINGDATE timestamp NOT NULL,   ELEMENTNAME varchar(150) NOT NULL, LOCATIONNAME varchar(150) NOT NULL, VISITNAME varchar(150) NOT NULL, TIMEOFDAY varchar(50), WEATHER varchar(50), VIEWRATING varchar(50), CERTAINTY varchar(50), NUMBEROFELEMENTS int, DETAILS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, SIGHTINGEVIDENCE varchar(50), MOONLIGHT varchar(50), MOONPHASE int, TEMPERATURE double, TEMPERATUREUNIT varchar(15), LIFESTATUS varchar(15), SEX varchar(15), TAG longvarchar, UNKNOWNTIME smallint, DURATIONMINUTES int, DURATIONSECONDS double)";
    protected static final String tableFiles = "CREATE TABLE FILES (ID varchar(175), FILENAME varchar(255), ORIGINALPATH varchar(500), FILETYPE varchar(50), UPLOADDATE date, ISDEFAULT smallint)";
    protected static final String tableWildLogOptions = "CREATE TABLE WILDLOG (VERSION int DEFAULT " + WILDLOG_DB_VERSION + ", DEFAULTLATITUDE double DEFAULT -28.7, DEFAULTLONGITUDE double DEFAULT 24.7, DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5, DEFAULTSLIDESHOWSIZE int DEFAULT 750, DEFAULTLATOPTION varchar(10) DEFAULT '', DEFAULTLONOPTION varchar(10) DEFAULT '', DEFAULTONLINEMAP smallint DEFAULT true)";
    // Find
    protected static final String findLocation = "SELECT * FROM LOCATIONS WHERE NAME = ?";
    protected static final String findVisit = "SELECT * FROM VISITS WHERE NAME = ?";
    protected static final String findSighting = "SELECT * FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?";
    protected static final String findElement = "SELECT * FROM ELEMENTS WHERE PRIMARYNAME = ?";
    protected static final String findFile = "SELECT * FROM FILES";
    protected static final String findWildLogOptions = "SELECT * FROM WILDLOG";
    // List
    protected static final String listLocation = "SELECT * FROM LOCATIONS";
    protected static final String listVisit = "SELECT * FROM VISITS";
    protected static final String listSighting = "SELECT * FROM SIGHTINGS";
    protected static final String listElement = "SELECT * FROM ELEMENTS";
    protected static final String listFile = "SELECT * FROM FILES";
    // Create
    protected static final String createLocation = "INSERT INTO LOCATIONS (NAME,DESCRIPTION,RATING,GAMEVIEWINGRATING,HABITATTYPE,ACCOMMODATIONTYPE,CATERING,CONTACTNUMBERS,WEBSITE,EMAIL,DIRECTIONS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createVisit = "INSERT INTO VISITS (NAME,STARTDATE,ENDDATE,DESCRIPTION,GAMEWATCHINGINTENSITY,VISITTYPE,LOCATIONNAME) VALUES (?,?,?,?,?,?,?)";
    protected static final String createSighting = "INSERT INTO SIGHTINGS (SIGHTINGCOUNTER,SIGHTINGDATE,ELEMENTNAME,LOCATIONNAME,VISITNAME,TIMEOFDAY,WEATHER,VIEWRATING,CERTAINTY,NUMBEROFELEMENTS,DETAILS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS,SIGHTINGEVIDENCE,MOONPHASE,MOONLIGHT,TEMPERATURE,TEMPERATUREUNIT,LIFESTATUS,SEX,TAG,UNKNOWNTIME,DURATIONMINUTES,DURATIONSECONDS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createElement = "INSERT INTO ELEMENTS (PRIMARYNAME,OTHERNAME,SCIENTIFICNAME,DESCRIPTION,DISTRIBUTION,NUTRITION,WATERDEPENDANCE,SIZEMALEMIN,SIZEMALEMAX,SIZEFEMALEMIN,SIZEFEMALEMAX,SIZEUNIT,SIZETYPE,WEIGHTMALEMIN,WEIGHTMALEMAX,WEIGHTFEMALEMIN,WEIGHTFEMALEMAX,WEIGHTUNIT,BREEDINGDURATION,BREEDINGNUMBER,WISHLISTRATING,DIAGNOSTICDESCRIPTION,ACTIVETIME,ENDANGEREDSTATUS,BEHAVIOURDESCRIPTION,ADDFREQUENCY,ELEMENTTYPE,FEEDINGCLASS,LIFESPAN,REFERENCEID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createFile = "INSERT INTO FILES (ID,FILENAME,ORIGINALPATH,FILETYPE,UPLOADDATE,ISDEFAULT) VALUES (?,?,?,?,?,?)";
    protected static final String createWildLogOptions = "INSERT INTO WILDLOG VALUES (DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT)";
    // Update
    protected static final String updateLocation = "UPDATE LOCATIONS SET NAME = ?, DESCRIPTION = ?, RATING = ?, GAMEVIEWINGRATING = ?, HABITATTYPE = ?, ACCOMMODATIONTYPE = ?, CATERING = ?, CONTACTNUMBERS = ?, WEBSITE = ?, EMAIL = ?, DIRECTIONS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ? WHERE NAME = ?";
    protected static final String updateVisit = "UPDATE VISITS SET NAME = ?, STARTDATE = ?, ENDDATE = ?, DESCRIPTION = ?, GAMEWATCHINGINTENSITY = ?, VISITTYPE = ?, LOCATIONNAME = ? WHERE NAME = ?";
    protected static final String updateSighting = "UPDATE SIGHTINGS SET SIGHTINGCOUNTER = ?, SIGHTINGDATE = ?, ELEMENTNAME = ?, LOCATIONNAME = ?, VISITNAME = ?, TIMEOFDAY = ?, WEATHER = ?, VIEWRATING = ?, CERTAINTY = ?, NUMBEROFELEMENTS = ?, DETAILS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, SIGHTINGEVIDENCE = ?, MOONPHASE = ?, MOONLIGHT = ?, TEMPERATURE = ?, TEMPERATUREUNIT = ?, LIFESTATUS = ?, SEX = ?, TAG = ?, UNKNOWNTIME = ?, DURATIONMINUTES = ?, DURATIONSECONDS = ? WHERE SIGHTINGCOUNTER = ?";
    protected static final String updateElement = "UPDATE ELEMENTS SET PRIMARYNAME = ?, OTHERNAME = ?, SCIENTIFICNAME = ?, DESCRIPTION = ?, DISTRIBUTION = ?, NUTRITION = ?, WATERDEPENDANCE = ?, SIZEMALEMIN = ?, SIZEMALEMAX = ?, SIZEFEMALEMIN = ?, SIZEFEMALEMAX = ?, SIZEUNIT = ?, SIZETYPE = ?, WEIGHTMALEMIN = ?, WEIGHTMALEMAX = ?, WEIGHTFEMALEMIN = ?, WEIGHTFEMALEMAX = ?, WEIGHTUNIT = ?, BREEDINGDURATION = ?, BREEDINGNUMBER = ?, WISHLISTRATING = ?, DIAGNOSTICDESCRIPTION = ?, ACTIVETIME = ?, ENDANGEREDSTATUS = ?, BEHAVIOURDESCRIPTION = ?, ADDFREQUENCY = ?, ELEMENTTYPE = ?, FEEDINGCLASS = ?, LIFESPAN = ?, REFERENCEID = ? WHERE PRIMARYNAME = ?";
    protected static final String updateFile = "UPDATE FILES SET ID = ?, FILENAME = ?, ORIGINALPATH = ?, FILETYPE = ?, UPLOADDATE = ?, ISDEFAULT = ? WHERE ORIGINALPATH = ?";
    protected static final String updateWildLogOptions = "UPDATE WILDLOG SET DEFAULTLATITUDE = ?, DEFAULTLONGITUDE = ?, DEFAULTSLIDESHOWSPEED = ?, DEFAULTSLIDESHOWSIZE = ?, DEFAULTLATOPTION = ?, DEFAULTLONOPTION = ?, DEFAULTONLINEMAP = ?";
    // Delete
    protected static final String deleteLocation = "DELETE FROM LOCATIONS WHERE NAME = ?";
    protected static final String deleteVisit = "DELETE FROM VISITS WHERE NAME = ?";
    protected static final String deleteSighting = "DELETE FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?";
    protected static final String deleteElement = "DELETE FROM ELEMENTS WHERE PRIMARYNAME = ?";
    protected static final String deleteFile = "DELETE FROM FILES WHERE ORIGINALPATH = ?";

    public DBI_JDBC() {
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
        PreparedStatement state = null;
        ResultSet results = null;
        Element tempElement = null;
        try {
            state = conn.prepareStatement(findElement);
            state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            results = state.executeQuery();
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
            closeStatementAndResultset(state, results);
        }
        return tempElement;
    }

    @Override
    public Location find(Location inLocation) {
        Location tempLocation = null;
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            state = conn.prepareStatement(findLocation);
            state.setString(1, UtilsData.sanitizeString(inLocation.getName()));
            results = state.executeQuery();
            if (results.next()) {
                tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(results.getString("HABITATTYPE"));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSeconds(results.getDouble("LATSECONDS"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSeconds(results.getDouble("LONSECONDS"));
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempLocation;
    }

    @Override
    public Visit find(Visit inVisit) {
        PreparedStatement state = null;
        ResultSet results = null;
        Visit tempVisit = null;
        try {
            state = conn.prepareStatement(findVisit);
            state.setString(1, UtilsData.sanitizeString(inVisit.getName()));
            results = state.executeQuery();
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
            closeStatementAndResultset(state, results);
        }
        return tempVisit;
    }

    @Override
    public Sighting find(Sighting inSighting) {
        PreparedStatement state = null;
        ResultSet results = null;
        Sighting tempSighting = null;
        try {
            state = conn.prepareStatement(findSighting);
            state.setLong(1, inSighting.getSightingCounter());
            results = state.executeQuery();
            if (results.next()) {
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSeconds(results.getDouble("LATSECONDS"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSeconds(results.getDouble("LONSECONDS"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempSighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                tempSighting.setMoonPhase(results.getInt("MOONPHASE"));
                tempSighting.setTemperature(results.getDouble("TEMPERATURE"));
                tempSighting.setUnitsTemperature(UnitsTemperature.getEnumFromText(results.getString("TEMPERATUREUNIT")));
                tempSighting.setLifeStatus(LifeStatus.getEnumFromText(results.getString("LIFESTATUS")));
                tempSighting.setSex(Sex.getEnumFromText(results.getString("SEX")));
                tempSighting.setTag(results.getString("TAG"));
                tempSighting.setTimeUnknown(results.getBoolean("UNKNOWNTIME"));
                tempSighting.setDurationMinutes(results.getInt("DURATIONMINUTES"));
                tempSighting.setDurationSeconds(results.getDouble("DURATIONSECONDS"));
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempSighting;
    }

    @Override
    public WildLogFile find(WildLogFile inFile) {
        PreparedStatement state = null;
        ResultSet results = null;
        WildLogFile tempFile = null;
        try {
            String sql = findFile;
            if (inFile.getFilePath(false) != null) {
                sql = sql + " WHERE ORIGINALPATH = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inFile.getFilePath(false)));
            }
            else
            if (inFile.getId() != null) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inFile.getId()));
            }
            results = state.executeQuery();
            if (results.next()) {
                tempFile = new WildLogFile();
                tempFile.setId(results.getString("ID"));
                tempFile.setFilename(results.getString("FILENAME"));
                tempFile.setFilePath(results.getString("ORIGINALPATH"));
                tempFile.setFileType(WildLogFileType.getEnumFromText(results.getString("FILETYPE")));
                tempFile.setUploadDate(results.getDate("UPLOADDATE"));
                tempFile.setDefaultFile(results.getBoolean("ISDEFAULT"));
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempFile;
    }

    @Override
    public WildLogOptions find(WildLogOptions inWildLogOptions) {
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            state = conn.prepareStatement(findWildLogOptions);
            results = state.executeQuery();
            if (results.next()) {
                inWildLogOptions.setDatabaseVersion(results.getInt("VERSION"));
                inWildLogOptions.setDefaultLatitude(results.getDouble("DEFAULTLATITUDE"));
                inWildLogOptions.setDefaultLongitude(results.getDouble("DEFAULTLONGITUDE"));
                inWildLogOptions.setDefaultSlideshowSpeed(results.getFloat("DEFAULTSLIDESHOWSPEED"));
                inWildLogOptions.setDefaultSlideshowSize(results.getInt("DEFAULTSLIDESHOWSIZE"));
                inWildLogOptions.setDefaultInputLatitude(Latitudes.getEnumFromText(results.getString("DEFAULTLATOPTION")));
                inWildLogOptions.setDefaultInputLongitude(Longitudes.getEnumFromText(results.getString("DEFAULTLONOPTION")));
                inWildLogOptions.setIsOnlinemapTheDefault(results.getBoolean("DEFAULTONLINEMAP"));
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return inWildLogOptions;
    }

    @Override
    public List<Element> list(Element inElement) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<Element> tempList = new ArrayList<Element>();
        try {
            String sql = listElement;
            if (inElement.getPrimaryName() != null && inElement.getType() == null) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            }
            else if (inElement.getPrimaryName() == null && inElement.getType() != null) {
                sql = sql + " WHERE ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inElement.getType().toString());
            }
            else if (inElement.getPrimaryName() != null && inElement.getType() != null) {
                sql = sql + " WHERE PRIMARYNAME = ? AND ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
                state.setString(2, inElement.getType().toString());
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
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
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public List<Location> list(Location inLocation) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<Location> tempList = new ArrayList<Location>();
        try {
            String sql = listLocation;
            if (inLocation.getName() != null) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocation.getName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                Location tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(results.getString("HABITATTYPE"));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSeconds(results.getDouble("LATSECONDS"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSeconds(results.getDouble("LONSECONDS"));
                tempList.add(tempLocation);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public List<Visit> list(Visit inVisit) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<Visit> tempList = new ArrayList<Visit>();
        try {
            String sql = listVisit;
            if (inVisit.getName() != null) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inVisit.getName()));
            }
            else if (inVisit.getLocationName() != null) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inVisit.getLocationName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
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
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public List<Sighting> list(Sighting inSighting) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = listSighting;
            if (inSighting.getSightingCounter() > 0) {
                sql = sql + " WHERE SIGHTINGCOUNTER = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inSighting.getSightingCounter());
            }
            else if (inSighting.getElementName() != null) {
                sql = sql + " WHERE ELEMENTNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
            }
            else if (inSighting.getLocationName() != null) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getLocationName()));
            }
            else if (inSighting.getVisitName() != null) {
                sql = sql + " WHERE VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getVisitName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSeconds(results.getDouble("LATSECONDS"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSeconds(results.getDouble("LONSECONDS"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempSighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                tempSighting.setMoonPhase(results.getInt("MOONPHASE"));
                tempSighting.setTemperature(results.getDouble("TEMPERATURE"));
                tempSighting.setUnitsTemperature(UnitsTemperature.getEnumFromText(results.getString("TEMPERATUREUNIT")));
                tempSighting.setLifeStatus(LifeStatus.getEnumFromText(results.getString("LIFESTATUS")));
                tempSighting.setSex(Sex.getEnumFromText(results.getString("SEX")));
                tempSighting.setTag(results.getString("TAG"));
                tempSighting.setTimeUnknown(results.getBoolean("UNKNOWNTIME"));
                tempSighting.setDurationMinutes(results.getInt("DURATIONMINUTES"));
                tempSighting.setDurationSeconds(results.getDouble("DURATIONSECONDS"));
                tempList.add(tempSighting);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public List<WildLogFile> list(WildLogFile inFile) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<WildLogFile> tempList = new ArrayList<WildLogFile>();
        try {
            String sql = listFile;
            if (inFile.getId() != null) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inFile.getId()));
            }
            else {
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                WildLogFile tempFoto = new WildLogFile();
                tempFoto.setId(results.getString("ID"));
                tempFoto.setFilename(results.getString("FILENAME"));
                tempFoto.setFilePath(results.getString("ORIGINALPATH"));
                tempFoto.setFileType(WildLogFileType.getEnumFromText(results.getString("FILETYPE")));
                tempFoto.setUploadDate(results.getDate("UPLOADDATE"));
                tempFoto.setDefaultFile(results.getBoolean("ISDEFAULT"));
                tempList.add(tempFoto);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public List<Sighting> searchSightingOnDate(Date inStartDate, Date inEndDate) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = "SELECT * FROM SIGHTINGS";
            if (inStartDate != null && inEndDate != null) {
                sql = sql + " WHERE SIGHTINGDATE > ? AND SIGHTINGDATE < ?";
                state = conn.prepareStatement(sql);
                state.setDate(1, new java.sql.Date(inStartDate.getTime()));
                state.setDate(2, new java.sql.Date(inEndDate.getTime()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSeconds(results.getDouble("LATSECONDS"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSeconds(results.getDouble("LONSECONDS"));
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
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public boolean createOrUpdate(Element inElement, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                List<Element> list = list(new Element(UtilsData.sanitizeString(inElement.getPrimaryName())));
                if (!list.isEmpty()) {
                    return false;
                }
            }
            // Check whether it is an update or not
            if (inOldName != null) {
                // Check whether there was a name change or not.
                if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                    // Update the Sightings
                    Sighting sighting = new Sighting();
                    sighting.setElementName(inOldName);
                    List<Sighting> sightings = list(sighting);
                    for (Sighting temp : sightings) {
                        temp.setElementName(inElement.getPrimaryName());
                        createOrUpdate(temp);
                    }
                    // Update the Files
                    List<WildLogFile> wildLogFiles = list(new WildLogFile("ELEMENT-" + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFile temp : wildLogFiles) {
                        temp.setId("ELEMENT-" + UtilsData.limitLength(UtilsData.sanitizeString(inElement.getPrimaryName()), 150));
                        createOrUpdate(temp, true);
                    }
                }
                // Update
                state = conn.prepareStatement(updateElement);
                state.setString(31, UtilsData.sanitizeString(inOldName));
            }
            else {
                // Insert
                state = conn.prepareStatement(createElement);
            }
            state.setString(1, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getPrimaryName()), 150));
            state.setString(2, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getOtherName()), 150));
            state.setString(3, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getScientificName()), 150));
            state.setString(4, UtilsData.sanitizeString(inElement.getDescription()));
            state.setString(5, UtilsData.sanitizeString(inElement.getDistribution()));
            state.setString(6, UtilsData.sanitizeString(inElement.getNutrition()));
            state.setString(7, UtilsData.stringFromObject(inElement.getWaterDependance()));
            state.setDouble(8, inElement.getSizeMaleMin());
            state.setDouble(9, inElement.getSizeMaleMax());
            state.setDouble(10, inElement.getSizeFemaleMin());
            state.setDouble(11, inElement.getSizeFemaleMax());
            state.setString(12, UtilsData.stringFromObject(inElement.getSizeUnit()));
            state.setString(13, UtilsData.stringFromObject(inElement.getSizeType()));
            state.setDouble(14, inElement.getWeightMaleMin());
            state.setDouble(15, inElement.getWeightMaleMax());
            state.setDouble(16, inElement.getWeightFemaleMin());
            state.setDouble(17, inElement.getWeightFemaleMax());
            state.setString(18, UtilsData.stringFromObject(inElement.getWeightUnit()));
            state.setString(19, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getBreedingDuration()), 50));
            state.setString(20, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getBreedingNumber()), 50));
            state.setString(21, UtilsData.stringFromObject(inElement.getWishListRating()));
            state.setString(22, UtilsData.sanitizeString(inElement.getDiagnosticDescription()));
            state.setString(23, UtilsData.stringFromObject(inElement.getActiveTime()));
            state.setString(24, UtilsData.stringFromObject(inElement.getEndangeredStatus()));
            state.setString(25, UtilsData.sanitizeString(inElement.getBehaviourDescription()));
            state.setString(26, UtilsData.stringFromObject(inElement.getAddFrequency()));
            state.setString(27, UtilsData.stringFromObject(inElement.getType()));
            state.setString(28, UtilsData.stringFromObject(inElement.getFeedingClass()));
            state.setString(29, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getLifespan()), 50));
            state.setString(30, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getReferenceID()), 50));
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;

    }

    @Override
    public boolean createOrUpdate(Location inLocation, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                List<Location> list = list(new Location(UtilsData.sanitizeString(inLocation.getName())));
                if (!list.isEmpty()) {
                    return false;
                }
            }
            // Check whether it is an update or not
            if (inOldName != null) {
                // Check whether there was a name change or not.
                if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                    // Update the Sightings
                    Sighting sighting = new Sighting();
                    sighting.setLocationName(inOldName);
                    List<Sighting> sightings = list(sighting);
                    for (Sighting temp : sightings) {
                        temp.setLocationName(inLocation.getName());
                        createOrUpdate(temp);
                    }
                    // Update the Visits
                    List<Visit> visits = list(new Visit(inOldName));
                    for (Visit temp : visits) {
                        temp.setLocationName(inLocation.getName());
                        createOrUpdate(temp, temp.getName());
                    }
                    // Update the Files
                    List<WildLogFile> wildLogFiles = list(new WildLogFile("LOCATION-" + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFile temp : wildLogFiles) {
                        temp.setId("LOCATION-" + UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getName()), 150));
                        createOrUpdate(temp, true);
                    }
                }
                // Update
                state = conn.prepareStatement(updateLocation);
                state.setString(20, UtilsData.sanitizeString(inOldName));
            }
            else {
                // Insert
                state = conn.prepareStatement(createLocation);
            }
            state.setString(1, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getName()), 150));
            state.setString(2, UtilsData.sanitizeString(inLocation.getDescription()));
            state.setString(3, UtilsData.stringFromObject(inLocation.getRating()));
            state.setString(4, UtilsData.stringFromObject(inLocation.getGameViewingRating()));
            state.setString(5, UtilsData.stringFromObject(inLocation.getHabitatType()));
            state.setString(6, UtilsData.stringFromObject(inLocation.getAccommodationType()));
            state.setString(7, UtilsData.stringFromObject(inLocation.getCatering()));
            state.setString(8, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getContactNumbers()), 50));
            state.setString(9, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getWebsite()), 100));
            state.setString(10, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getEmail()), 100));
            state.setString(11, UtilsData.sanitizeString(inLocation.getDirections()));
            state.setString(12, UtilsData.stringFromObject(inLocation.getLatitude()));
            state.setInt(13, inLocation.getLatDegrees());
            state.setInt(14, inLocation.getLatMinutes());
            state.setDouble(15, inLocation.getLatSeconds());
            state.setString(16, UtilsData.stringFromObject(inLocation.getLongitude()));
            state.setInt(17, inLocation.getLonDegrees());
            state.setInt(18, inLocation.getLonMinutes());
            state.setDouble(19, inLocation.getLonSeconds());
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(Visit inVisit, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                List<Visit> list = list(new Visit(UtilsData.sanitizeString(inVisit.getName())));
                if (!list.isEmpty()) {
                    return false;
                }
            }
            // Check whether to update or create it.
            if (inOldName != null) {
                // FIXME: What happens if a visit is moved to a new location? We need to also check that if the location name changes on the visit the sightings change as well
                // Update the related tables if the name cahnges
                if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                    // Update the Sightings
                    Sighting sighting = new Sighting();
                    sighting.setVisitName(inOldName);
                    List<Sighting> sightings = list(sighting);
                    for (Sighting temp : sightings) {
                        temp.setVisitName(inVisit.getName());
                        createOrUpdate(temp);
                    }
                    // Update the Files
                    List<WildLogFile> wildLogFiles = list(new WildLogFile("VISIT-" + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFile temp : wildLogFiles) {
                        temp.setId("VISIT-" + UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getName()), 150));
                        createOrUpdate(temp, true);
                    }
                }
                // Update
                state = conn.prepareStatement(updateVisit);
                state.setString(8, UtilsData.sanitizeString(inOldName));
            }
            else {
                // Insert
                state = conn.prepareStatement(createVisit);
            }
            state.setString(1, UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getName()), 150));
            if (inVisit.getStartDate() != null) {
                state.setDate(2, new java.sql.Date(inVisit.getStartDate().getTime()));
            }
            else {
                state.setDate(2, null);
            }
            if (inVisit.getEndDate() != null) {
                state.setDate(3, new java.sql.Date(inVisit.getEndDate().getTime()));
            }
            else {
                state.setDate(3, null);
            }
            state.setString(4, UtilsData.sanitizeString(inVisit.getDescription()));
            state.setString(5, UtilsData.stringFromObject(inVisit.getGameWatchingIntensity()));
            state.setString(6, UtilsData.stringFromObject(inVisit.getType()));
            state.setString(7, UtilsData.sanitizeString(inVisit.getLocationName()));
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(Sighting inSighting) {
        PreparedStatement state = null;
        Statement tempState = null;
        ResultSet results = null;
        boolean isUpdate = false;
        try {
            if (inSighting.getSightingCounter() > 0) {
                // Note: No need to update the files, because once a sighting has an ID it never changes
                // Update
                state = conn.prepareStatement(updateSighting);
                isUpdate = true;
            }
            else {
                // Get the max ID and then run the query.
                tempState = conn.createStatement();
                results = tempState.executeQuery("SELECT MAX(SIGHTINGCOUNTER) FROM SIGHTINGS");
                if (results.next()) {
                    long sightingCounter = results.getLong(1);
                    sightingCounter++;
                    // Need to set the counter on the sighting for images to upload correctly and the below code to work
                    inSighting.setSightingCounter(sightingCounter);
                }
                else {
                    return false;
                }
                // Insert
                state = conn.prepareStatement(createSighting);
            }
            // Populate the values
            state.setLong(1, inSighting.getSightingCounter());
            if (inSighting.getDate() != null)
            	state.setTimestamp(2, new Timestamp(inSighting.getDate().getTime()));
            else
            	state.setTimestamp(2, null);
            state.setString(3, UtilsData.sanitizeString(inSighting.getElementName()));
            state.setString(4, UtilsData.sanitizeString(inSighting.getLocationName()));
            state.setString(5, UtilsData.sanitizeString(inSighting.getVisitName()));
            state.setString(6, UtilsData.stringFromObject(inSighting.getTimeOfDay()));
            state.setString(7, UtilsData.stringFromObject(inSighting.getWeather()));
            state.setString(8, UtilsData.stringFromObject(inSighting.getViewRating()));
            state.setString(9, UtilsData.stringFromObject(inSighting.getCertainty()));
            state.setInt(10, inSighting.getNumberOfElements());
            state.setString(11, UtilsData.sanitizeString(inSighting.getDetails()));
            state.setString(12, UtilsData.stringFromObject(inSighting.getLatitude()));
            state.setInt(13, inSighting.getLatDegrees());
            state.setInt(14, inSighting.getLatMinutes());
            state.setDouble(15, inSighting.getLatSeconds());
            state.setString(16, UtilsData.stringFromObject(inSighting.getLongitude()));
            state.setInt(17, inSighting.getLonDegrees());
            state.setInt(18, inSighting.getLonMinutes());
            state.setDouble(19, inSighting.getLonSeconds());
            state.setString(20, UtilsData.stringFromObject(inSighting.getSightingEvidence()));
            state.setInt(21, inSighting.getMoonPhase());
            state.setString(22, UtilsData.stringFromObject(inSighting.getMoonlight()));
            state.setDouble(23, inSighting.getTemperature());
            state.setString(24, UtilsData.stringFromObject(inSighting.getUnitsTemperature()));
            state.setString(25, UtilsData.stringFromObject(inSighting.getLifeStatus()));
            state.setString(26, UtilsData.stringFromObject(inSighting.getSex()));
            state.setString(27, UtilsData.stringFromObject(inSighting.getTag()));
            state.setBoolean(28, inSighting.isTimeUnknown());
            state.setInt(29, inSighting.getDurationMinutes());
            state.setDouble(30, inSighting.getDurationSeconds());

            if (isUpdate) {
                state.setLong(31, inSighting.getSightingCounter());
            }
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(tempState, results);
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(WildLogFile inFile, boolean inUpdate) {
        PreparedStatement state = null;
        try {
            if (inUpdate) {
                state = conn.prepareStatement(updateFile);
            }
            else {
                state = conn.prepareStatement(createFile);
            }
            state.setString(1, UtilsData.sanitizeString(inFile.getId()));
            state.setString(2, UtilsData.sanitizeString(inFile.getFilename()));
            state.setString(3, UtilsData.sanitizeString(inFile.getFilePath(false)));
            state.setString(4, UtilsData.stringFromObject(inFile.getFileType()));
            if (inFile.getUploadDate() != null) {
                state.setDate(5, new java.sql.Date(inFile.getUploadDate().getTime()));
            }
            else {
                state.setDate(5, null);
            }
            if (inFile.isDefaultFile()) {
                state.setBoolean(6, true);
            }
            else {
                state.setBoolean(6, false);
            }
            if (inUpdate) {
                state.setString(7, UtilsData.sanitizeString(inFile.getFilePath(false)));
            }
            state.executeUpdate();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean createOrUpdate(WildLogOptions inWildLogOptions) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            results = state.executeQuery(findWildLogOptions);
            if (!results.next()) {
                // Insert
                PreparedStatement prepState = null;
                try {
                    prepState = conn.prepareStatement(createWildLogOptions);
                    prepState.executeUpdate();
                }
                catch (SQLException ex) {
                    printSQLException(ex);
                    return false;
                }
                finally {
                    closeStatement(prepState);
                }
            }
            else {
                // Update
                PreparedStatement prepState = null;
                try {
                    prepState = conn.prepareStatement(updateWildLogOptions);
                    prepState.setDouble(1, inWildLogOptions.getDefaultLatitude());
                    prepState.setDouble(2, inWildLogOptions.getDefaultLongitude());
                    prepState.setFloat(3, inWildLogOptions.getDefaultSlideshowSpeed());
                    prepState.setInt(4, inWildLogOptions.getDefaultSlideshowSize());
                    prepState.setString(5, UtilsData.stringFromObject(inWildLogOptions.getDefaultInputLatitude()));
                    prepState.setString(6, UtilsData.stringFromObject(inWildLogOptions.getDefaultInputLongitude()));
                    prepState.setBoolean(7, inWildLogOptions.isIsOnlinemapTheDefault());
                    prepState.executeUpdate();
                }
                catch (SQLException ex) {
                    printSQLException(ex);
                    return false;
                }
                finally {
                    closeStatement(prepState);
                }
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return true;
    }

    @Override
    public boolean delete(Element inElement) {
        PreparedStatement state = null;
        try {
            // Delete the Element
            state = conn.prepareStatement(deleteElement);
            state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            state.executeUpdate();
            // Delete all Sightings for this Element
            Sighting sighting = new Sighting();
            sighting.setElementName(inElement.getPrimaryName());
            List<Sighting> sightingList = list(sighting);
            for (Sighting temp : sightingList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFile file = new WildLogFile();
            file.setId("ELEMENT-" + UtilsData.sanitizeString(inElement.getPrimaryName()));
            List<WildLogFile> fileList = list(file);
            for (WildLogFile temp : fileList) {
                delete(temp);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean delete(Location inLocation) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteLocation);
            state.setString(1, UtilsData.sanitizeString(inLocation.getName()));
            // Delete Location
            state.executeUpdate();
            state.close();
            // Delete Visits for this Location
            Visit visit = new Visit();
            visit.setLocationName(inLocation.getName());
            List<Visit> visitList = list(visit);
            for (Visit temp : visitList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFile file = new WildLogFile();
            file.setId("LOCATION-" + UtilsData.sanitizeString(inLocation.getName()));
            List<WildLogFile> fileList = list(file);
            for (WildLogFile temp : fileList) {
                delete(temp);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean delete(Visit inVisit) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteVisit);
            state.setString(1, UtilsData.sanitizeString(inVisit.getName()));
            // Delete Visit
            state.executeUpdate();
            // Delete Sightings for this Visit
            Sighting sighting = new Sighting();
            sighting.setVisitName(inVisit.getName());
            List<Sighting> sightingList = list(sighting);
            for (Sighting temp : sightingList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFile file = new WildLogFile();
            file.setId("VISIT-" + UtilsData.sanitizeString(inVisit.getName()));
            List<WildLogFile> fileList = list(file);
            for (WildLogFile temp : fileList) {
                delete(temp);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean delete(Sighting inSighting) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteSighting);
            state.setLong(1, inSighting.getSightingCounter());
            // Delete Sightings
            state.executeUpdate();
            // Delete Fotos
            WildLogFile file = new WildLogFile();
            file.setId("SIGHTING-" + inSighting.getSightingCounter());
            List<WildLogFile> fileList = list(file);
            for (WildLogFile temp : fileList) {
                delete(temp);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public boolean delete(WildLogFile inFoto) {
        // Note: this method only deletes one file at a time
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteFile);
            state.setString(1, UtilsData.sanitizeString(inFoto.getFilePath(false)));
            // Delete File from database
            state.executeUpdate();
            // Delete the file on the PC
            File tempFile = new File(inFoto.getFilePath(true));
            tempFile.delete();
            // NOTE: Huidiglik gaan dit die thumbnails agter los, dis OK vir nou want mens kan alle thumbnails delete soos mens wil, maar sal nice wees om dit ook skoon te maak
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    protected static void printSQLException(SQLException e) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null) {
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
            boolean fullyUpdated = false;
            for (int t = 0; t <= WILDLOG_DB_VERSION; t++) {
                results = state.executeQuery("SELECT VERSION FROM WILDLOG");
                if (results.next()) {
                    if (results.getInt("VERSION") == 0) {
                        doUpdate1();
                    }
                    if (results.getInt("VERSION") == 1) {
                        doUpdate2();
                    }
                    if (results.getInt("VERSION") == 2) {
                        doUpdate3();
                    }
                    if (results.getInt("VERSION") == 3) {
                        fullyUpdated = true;
                        break;
                    }
                }
            }
            if (!fullyUpdated) {
                final WildLogApp app = (WildLogApp)Application.getInstance();
                UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "The database could not be fully updated. Make sure it is not in use or broken"
                                + "and that you are running the latest version of the application.",
                                "WildLog Error: Can't Initialize Database", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
                Application.getInstance().exit();
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
    }

    // Private update methods
    private void doUpdate1() {
        System.out.println("Starting update 1");
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
            state.execute(tableElements);
            state.execute(tableLocations);
            state.execute(tableVisits);
            state.execute(tableSightings);
            state.execute(tableFiles);
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
            closeStatementAndResultset(state, results);
        }
        System.out.println("Finished update 1");
    }

    private void doUpdate2() {
        System.out.println("Starting update 2");
        // This update adds a column to the options table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the column with default value etc.
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5");
            // Extra stuff om van v1 na v3 te gaan (stuff vir pa se data)
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTLATITUDE double DEFAULT -28.7");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTLONGITUDE double DEFAULT 24.7");
            state.execute("ALTER TABLE ELEMENTS ADD COLUMN SIZETYPE varchar(50)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN MOONLIGHT varchar(50)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN MOONPHASE int");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=2");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        System.out.println("Finished update 2");
    }

    private void doUpdate3() {
        System.out.println("Starting update 3");
        // This update removes and alters some tables and adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Make changes to Location
            state.execute("ALTER TABLE LOCATIONS DROP COLUMN PROVINCE");
            state.execute("ALTER TABLE LOCATIONS ALTER COLUMN HABITATTYPE  longvarchar");
            state.execute("ALTER TABLE LOCATIONS ALTER COLUMN LATSECONDSFLOAT double");
            state.execute("ALTER TABLE LOCATIONS ALTER COLUMN LATSECONDSFLOAT RENAME TO LATSECONDS");
            state.execute("ALTER TABLE LOCATIONS ALTER COLUMN LONSECONDSFLOAT double");
            state.execute("ALTER TABLE LOCATIONS ALTER COLUMN LONSECONDSFLOAT RENAME TO LONSECONDS");
            // Make changes to Sightings
            state.execute("ALTER TABLE SIGHTINGS DROP COLUMN AREATYPE");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN LATSECONDSFLOAT double");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN LATSECONDSFLOAT RENAME TO LATSECONDS");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN LONSECONDSFLOAT double");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN LONSECONDSFLOAT RENAME TO LONSECONDS");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN TEMPERATURE double");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN TEMPERATUREUNIT varchar(15)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN LIFESTATUS varchar(15)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN SEX varchar(15)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN TAG longvarchar");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN UNKNOWNTIME smallint");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN DURATIONMINUTES int");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN DURATIONSECONDS double");
            // Make changes to Files
            state.execute("ALTER TABLE FILES DROP COLUMN FILEPATH");
            // Make changes to Wildlog settings
            state.execute("ALTER TABLE WILDLOG ALTER COLUMN DEFAULTLATITUDE double");
            state.execute("ALTER TABLE WILDLOG ALTER COLUMN DEFAULTLONGITUDE double");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTLATOPTION varchar(10) DEFAULT 'South'");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTLONOPTION varchar(10) DEFAULT 'East'");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTSLIDESHOWSIZE int DEFAULT 750");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTONLINEMAP smallint DEFAULT true");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=3");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        System.out.println("Finished update 3");
    }

    protected static void closeStatementAndResultset(Statement inStatement, ResultSet inResultSet) {
        closeStatement(inStatement);
        closeResultset(inResultSet);
    }

    protected static void closeStatement(Statement inStatement) {
        // PreparedStatement
        try {
            if (inStatement != null) {
                inStatement.close();
                inStatement = null;
            }
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }

    protected static void closeResultset(ResultSet inResultSet) {
        // ResultSet
        try {
            if (inResultSet != null) {
                inResultSet.close();
                inResultSet = null;
            }
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
        }
    }
}
