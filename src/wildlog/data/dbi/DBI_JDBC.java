package wildlog.data.dbi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.Age;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.GPSAccuracy;
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
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WishRating;
import wildlog.data.utils.UtilsData;


public abstract class DBI_JDBC implements DBI {
    protected final Random randomGenerator = new Random(System.nanoTime()); // ThreadLocalRandom is beter maar net in Java 7
    // Version
    protected static final int WILDLOG_DB_VERSION = 4;
    // Tables
    protected static final String tableElements = "CREATE TABLE ELEMENTS (PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL, OTHERNAME varchar(150), SCIENTIFICNAME varchar(150), DESCRIPTION longvarchar, DISTRIBUTION longvarchar, NUTRITION longvarchar, WATERDEPENDANCE varchar(50), SIZEMALEMIN float(52), SIZEMALEMAX float(52), SIZEFEMALEMIN float(52), SIZEFEMALEMAX float(52), SIZEUNIT varchar(10), SIZETYPE varchar(50), WEIGHTMALEMIN float(52), WEIGHTMALEMAX float(52), WEIGHTFEMALEMIN float(52), WEIGHTFEMALEMAX float(52), WEIGHTUNIT varchar(10), BREEDINGDURATION varchar(50), BREEDINGNUMBER varchar(50), WISHLISTRATING varchar(50), DIAGNOSTICDESCRIPTION longvarchar, ACTIVETIME varchar(50), ENDANGEREDSTATUS varchar(35), BEHAVIOURDESCRIPTION longvarchar, ADDFREQUENCY varchar(50), ELEMENTTYPE varchar(50), FEEDINGCLASS varchar(50), LIFESPAN varchar(50), REFERENCEID varchar(50))";
    protected static final String tableLocations = "CREATE TABLE LOCATIONS (NAME varchar(150) PRIMARY KEY NOT NULL, DESCRIPTION longvarchar, RATING varchar(50), GAMEVIEWINGRATING varchar(50), HABITATTYPE longvarchar, ACCOMMODATIONTYPE varchar(150), CATERING varchar(50), CONTACTNUMBERS varchar(50), WEBSITE varchar(100), EMAIL varchar(100), DIRECTIONS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, GPSACCURACY varchar(50))";
    protected static final String tableVisits = "CREATE TABLE VISITS (NAME varchar(150) PRIMARY KEY NOT NULL, STARTDATE date, ENDDATE date, DESCRIPTION longvarchar, GAMEWATCHINGINTENSITY varchar(50), VISITTYPE varchar(50), LOCATIONNAME varchar(150))";
    protected static final String tableSightings = "CREATE TABLE SIGHTINGS (SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL,   SIGHTINGDATE timestamp NOT NULL,   ELEMENTNAME varchar(150) NOT NULL, LOCATIONNAME varchar(150) NOT NULL, VISITNAME varchar(150) NOT NULL, TIMEOFDAY varchar(50), WEATHER varchar(50), VIEWRATING varchar(50), CERTAINTY varchar(50), NUMBEROFELEMENTS int, DETAILS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, SIGHTINGEVIDENCE varchar(50), MOONLIGHT varchar(50), MOONPHASE int, TEMPERATURE double, TEMPERATUREUNIT varchar(15), LIFESTATUS varchar(15), SEX varchar(15), TAG longvarchar, DURATIONMINUTES int, DURATIONSECONDS double, GPSACCURACY varchar(50), TIMEACCURACY varchar(50), AGE varchar(50))";
    protected static final String tableFiles = "CREATE TABLE FILES (ID varchar(175), FILENAME varchar(255), ORIGINALPATH varchar(500), FILETYPE varchar(50), UPLOADDATE date, ISDEFAULT smallint)";
    protected static final String tableWildLogOptions = "CREATE TABLE WILDLOG (VERSION int DEFAULT " + WILDLOG_DB_VERSION + ", DEFAULTLATITUDE double DEFAULT -28.7, DEFAULTLONGITUDE double DEFAULT 24.7, DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5, DEFAULTSLIDESHOWSIZE int DEFAULT 750, DEFAULTLATOPTION varchar(10) DEFAULT '', DEFAULTLONOPTION varchar(10) DEFAULT '', DEFAULTONLINEMAP smallint DEFAULT true, USETHUMBNAILTABLES smallint DEFAULT true, USETHUMBNAILBROWSE smallint DEFAULT false, ENABLESOUNDS smallint DEFAULT true)";
    // Count
    protected static final String countLocation = "SELECT count(*) FROM LOCATIONS";
    protected static final String countVisit = "SELECT count(*) FROM VISITS";
    protected static final String countSighting = "SELECT count(*) FROM SIGHTINGS";
    protected static final String countElement = "SELECT count(*) FROM ELEMENTS";
    protected static final String countFile = "SELECT count(*) FROM FILES";
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
    protected static final String createLocation = "INSERT INTO LOCATIONS (NAME,DESCRIPTION,RATING,GAMEVIEWINGRATING,HABITATTYPE,ACCOMMODATIONTYPE,CATERING,CONTACTNUMBERS,WEBSITE,EMAIL,DIRECTIONS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS,GPSACCURACY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createVisit = "INSERT INTO VISITS (NAME,STARTDATE,ENDDATE,DESCRIPTION,GAMEWATCHINGINTENSITY,VISITTYPE,LOCATIONNAME) VALUES (?,?,?,?,?,?,?)";
    protected static final String createSighting = "INSERT INTO SIGHTINGS (SIGHTINGCOUNTER,SIGHTINGDATE,ELEMENTNAME,LOCATIONNAME,VISITNAME,TIMEOFDAY,WEATHER,VIEWRATING,CERTAINTY,NUMBEROFELEMENTS,DETAILS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS,SIGHTINGEVIDENCE,MOONPHASE,MOONLIGHT,TEMPERATURE,TEMPERATUREUNIT,LIFESTATUS,SEX,TAG,DURATIONMINUTES,DURATIONSECONDS,GPSACCURACY,TIMEACCURACY,AGE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createElement = "INSERT INTO ELEMENTS (PRIMARYNAME,OTHERNAME,SCIENTIFICNAME,DESCRIPTION,DISTRIBUTION,NUTRITION,WATERDEPENDANCE,SIZEMALEMIN,SIZEMALEMAX,SIZEFEMALEMIN,SIZEFEMALEMAX,SIZEUNIT,SIZETYPE,WEIGHTMALEMIN,WEIGHTMALEMAX,WEIGHTFEMALEMIN,WEIGHTFEMALEMAX,WEIGHTUNIT,BREEDINGDURATION,BREEDINGNUMBER,WISHLISTRATING,DIAGNOSTICDESCRIPTION,ACTIVETIME,ENDANGEREDSTATUS,BEHAVIOURDESCRIPTION,ADDFREQUENCY,ELEMENTTYPE,FEEDINGCLASS,LIFESPAN,REFERENCEID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createFile = "INSERT INTO FILES (ID,FILENAME,ORIGINALPATH,FILETYPE,UPLOADDATE,ISDEFAULT) VALUES (?,?,?,?,?,?)";
    protected static final String createWildLogOptions = "INSERT INTO WILDLOG VALUES (DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT)";
    // Update
    protected static final String updateLocation = "UPDATE LOCATIONS SET NAME = ?, DESCRIPTION = ?, RATING = ?, GAMEVIEWINGRATING = ?, HABITATTYPE = ?, ACCOMMODATIONTYPE = ?, CATERING = ?, CONTACTNUMBERS = ?, WEBSITE = ?, EMAIL = ?, DIRECTIONS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, GPSACCURACY = ? WHERE NAME = ?";
    protected static final String updateVisit = "UPDATE VISITS SET NAME = ?, STARTDATE = ?, ENDDATE = ?, DESCRIPTION = ?, GAMEWATCHINGINTENSITY = ?, VISITTYPE = ?, LOCATIONNAME = ? WHERE NAME = ?";
    protected static final String updateSighting = "UPDATE SIGHTINGS SET SIGHTINGCOUNTER = ?, SIGHTINGDATE = ?, ELEMENTNAME = ?, LOCATIONNAME = ?, VISITNAME = ?, TIMEOFDAY = ?, WEATHER = ?, VIEWRATING = ?, CERTAINTY = ?, NUMBEROFELEMENTS = ?, DETAILS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, SIGHTINGEVIDENCE = ?, MOONPHASE = ?, MOONLIGHT = ?, TEMPERATURE = ?, TEMPERATUREUNIT = ?, LIFESTATUS = ?, SEX = ?, TAG = ?, DURATIONMINUTES = ?, DURATIONSECONDS = ?, GPSACCURACY = ?, TIMEACCURACY = ?, AGE = ? WHERE SIGHTINGCOUNTER = ?";
    protected static final String updateElement = "UPDATE ELEMENTS SET PRIMARYNAME = ?, OTHERNAME = ?, SCIENTIFICNAME = ?, DESCRIPTION = ?, DISTRIBUTION = ?, NUTRITION = ?, WATERDEPENDANCE = ?, SIZEMALEMIN = ?, SIZEMALEMAX = ?, SIZEFEMALEMIN = ?, SIZEFEMALEMAX = ?, SIZEUNIT = ?, SIZETYPE = ?, WEIGHTMALEMIN = ?, WEIGHTMALEMAX = ?, WEIGHTFEMALEMIN = ?, WEIGHTFEMALEMAX = ?, WEIGHTUNIT = ?, BREEDINGDURATION = ?, BREEDINGNUMBER = ?, WISHLISTRATING = ?, DIAGNOSTICDESCRIPTION = ?, ACTIVETIME = ?, ENDANGEREDSTATUS = ?, BEHAVIOURDESCRIPTION = ?, ADDFREQUENCY = ?, ELEMENTTYPE = ?, FEEDINGCLASS = ?, LIFESPAN = ?, REFERENCEID = ? WHERE PRIMARYNAME = ?";
    protected static final String updateFile = "UPDATE FILES SET ID = ?, FILENAME = ?, ORIGINALPATH = ?, FILETYPE = ?, UPLOADDATE = ?, ISDEFAULT = ? WHERE ORIGINALPATH = ?";
    protected static final String updateWildLogOptions = "UPDATE WILDLOG SET DEFAULTLATITUDE = ?, DEFAULTLONGITUDE = ?, DEFAULTSLIDESHOWSPEED = ?, DEFAULTSLIDESHOWSIZE = ?, DEFAULTLATOPTION = ?, DEFAULTLONOPTION = ?, DEFAULTONLINEMAP = ?, USETHUMBNAILTABLES = ?, USETHUMBNAILBROWSE =?, ENABLESOUNDS = ?";
    // Delete
    protected static final String deleteLocation = "DELETE FROM LOCATIONS WHERE NAME = ?";
    protected static final String deleteVisit = "DELETE FROM VISITS WHERE NAME = ?";
    protected static final String deleteSighting = "DELETE FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?";
    protected static final String deleteElement = "DELETE FROM ELEMENTS WHERE PRIMARYNAME = ?";
    protected static final String deleteFile = "DELETE FROM FILES WHERE ORIGINALPATH = ?";
    // Queries
    protected static final String queryLocationCountForElement = "select SIGHTINGS.LOCATIONNAME, count(*) cnt from SIGHTINGS where SIGHTINGS.ELEMENTNAME = ? group by SIGHTINGS.LOCATIONNAME order by cnt desc";
    // Variables
    protected Connection conn;

    public DBI_JDBC() {
    }

    @Override
    public boolean initialize(boolean inCreateDefaultRecords) {
        Statement state = null;
        ResultSet results = null;
        boolean started = true;
        try {
            // Create tables
            results = conn.getMetaData().getTables(null, null, "ELEMENTS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableElements);
                // Create other indexes
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS ELEMENT_PRINAME ON ELEMENTS (PRIMARYNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS ELEMENT_TYPE ON ELEMENTS (ELEMENTTYPE)");
                state.execute("CREATE INDEX IF NOT EXISTS ELEMENT_PRINAME_TYPE ON ELEMENTS (PRIMARYNAME, ELEMENTTYPE)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createOrUpdate(new ElementCore("Unknown Creature"), null);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "LOCATIONS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableLocations);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS LOCATION_NAME ON LOCATIONS (NAME)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createOrUpdate(new LocationCore("Some Place"), null);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "VISITS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableVisits);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS VISIT_NAME ON VISITS (NAME)");
                state.execute("CREATE INDEX IF NOT EXISTS VISIT_LOCATION ON VISITS (LOCATIONNAME)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createOrUpdate(new VisitCore("Casual Observations", "Some Place"), null);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "SIGHTINGS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableSightings);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS SIGHTING_CNT ON SIGHTINGS (SIGHTINGCOUNTER)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT ON SIGHTINGS (ELEMENTNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_LOCATION ON SIGHTINGS (LOCATIONNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_VISIT ON SIGHTINGS (VISITNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT_LOCATION ON SIGHTINGS (ELEMENTNAME, LOCATIONNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT_VISIT ON SIGHTINGS (ELEMENTNAME, VISITNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_DATE ON SIGHTINGS (SIGHTINGDATE)");
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "FILES", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableFiles);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS FILE_ORGPATH ON FILES (ORIGINALPATH)");
                state.execute("CREATE INDEX IF NOT EXISTS FILE_ID ON FILES (ID)");
                state.execute("CREATE INDEX IF NOT EXISTS FILE_FILETYPE ON FILES (FILETYPE)");
                state.execute("CREATE INDEX IF NOT EXISTS FILE_ID_DEFAULT ON FILES (ID, ISDEFAULT)");
                state.execute("CREATE INDEX IF NOT EXISTS FILE_ORGPATH_DEFAULT ON FILES (ORIGINALPATH, ISDEFAULT)");
                state.execute("CREATE INDEX IF NOT EXISTS FILE_ID_TYPE_DEFAULT ON FILES (ID, FILETYPE, ISDEFAULT)");
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "WILDLOG", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableWildLogOptions);
                closeStatement(state);
            }
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
            started = false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return started;
    }

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
    public <T extends ElementCore> int count(T inElement) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countElement;
            if (inElement.getPrimaryName() != null && inElement.getPrimaryName().length() > 0) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inElement.getPrimaryName());
            }
            else
            if (inElement.getScientificName() != null && inElement.getScientificName().length() > 0) {
                sql = sql + " WHERE SCIENTIFICNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getScientificName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                count = results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return count;
    }

    @Override
    public <T extends LocationCore> int count(T inLocation) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countLocation;
            if (inLocation.getName() != null && inLocation.getName().length() > 0) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inLocation.getName());
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                count = results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return count;
    }

    @Override
    public <T extends VisitCore> int count(T inVisit) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countVisit;
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
                count = results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return count;
    }

    @Override
    public <T extends SightingCore> int count(T inSighting) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countSighting;
            if (inSighting.getSightingCounter() > 0) {
                sql = sql + " WHERE SIGHTINGCOUNTER = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inSighting.getSightingCounter());
            }
            else
            if (inSighting.getElementName() != null && inSighting.getElementName().length() > 0 && inSighting.getLocationName() != null && inSighting.getLocationName().length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ? AND LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
                state.setString(2, UtilsData.sanitizeString(inSighting.getLocationName()));
            }
            else
            if (inSighting.getElementName() != null && inSighting.getElementName().length() > 0 && inSighting.getVisitName() != null && inSighting.getVisitName().length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
                state.setString(2, UtilsData.sanitizeString(inSighting.getVisitName()));
            }
            else
            if (inSighting.getElementName() != null && inSighting.getElementName().length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
            }
            else
            if (inSighting.getLocationName() != null && inSighting.getLocationName().length() > 0) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getLocationName()));
            }
            else
            if (inSighting.getVisitName() != null && inSighting.getVisitName().length() > 0) {
                sql = sql + " WHERE VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getVisitName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                count = results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return count;
    }

    @Override
    public <T extends WildLogFileCore> int count(T inWildLogFile) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countFile;
            if (inWildLogFile.getId() != null && inWildLogFile.getId().length() > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inWildLogFile.getId());
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                count = results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return count;
    }

    @Override
    public <T extends ElementCore> T find(T inElement) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempElement = null;
        try {
            state = conn.prepareStatement(findElement);
            state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            results = state.executeQuery();
            if (results.next()) {
                // TODO: Kyk of dit veilig is om dalk nie 'n nuwe instance te maak nie maar die een wat ingestuur word te gebruik. (geld vir Location, ens. ook)
                tempElement = (T) inElement.getClass().newInstance();
                populateElement(results, tempElement);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempElement;
    }

    protected <T extends ElementCore> void populateElement(ResultSet inResults, T inElement) throws SQLException {
        inElement.setPrimaryName(inResults.getString("PRIMARYNAME"));
        inElement.setOtherName(inResults.getString("OTHERNAME"));
        inElement.setScientificName(inResults.getString("SCIENTIFICNAME"));
        inElement.setDescription(inResults.getString("DESCRIPTION"));
        inElement.setDistribution(inResults.getString("DISTRIBUTION"));
        inElement.setNutrition(inResults.getString("NUTRITION"));
        inElement.setWaterDependance(WaterDependancy.getEnumFromText(inResults.getString("WATERDEPENDANCE")));
        inElement.setSizeMaleMin(inResults.getDouble("SIZEMALEMIN"));
        inElement.setSizeMaleMax(inResults.getDouble("SIZEMALEMAX"));
        inElement.setSizeFemaleMin(inResults.getDouble("SIZEFEMALEMIN"));
        inElement.setSizeFemaleMax(inResults.getDouble("SIZEFEMALEMAX"));
        inElement.setSizeUnit(UnitsSize.getEnumFromText(inResults.getString("SIZEUNIT")));
        inElement.setSizeType(SizeType.getEnumFromText(inResults.getString("SIZETYPE")));
        inElement.setWeightMaleMin(inResults.getDouble("WEIGHTMALEMIN"));
        inElement.setWeightMaleMax(inResults.getDouble("WEIGHTMALEMAX"));
        inElement.setWeightFemaleMin(inResults.getDouble("WEIGHTFEMALEMIN"));
        inElement.setWeightFemaleMax(inResults.getDouble("WEIGHTFEMALEMAX"));
        inElement.setWeightUnit(UnitsWeight.getEnumFromText(inResults.getString("WEIGHTUNIT")));
        inElement.setBreedingDuration(inResults.getString("BREEDINGDURATION"));
        inElement.setBreedingNumber(inResults.getString("BREEDINGNUMBER"));
        inElement.setWishListRating(WishRating.getEnumFromText(inResults.getString("WISHLISTRATING")));
        inElement.setDiagnosticDescription(inResults.getString("DIAGNOSTICDESCRIPTION"));
        inElement.setActiveTime(ActiveTime.getEnumFromText(inResults.getString("ACTIVETIME")));
        inElement.setEndangeredStatus(EndangeredStatus.getEnumFromText(inResults.getString("ENDANGEREDSTATUS")));
        inElement.setBehaviourDescription(inResults.getString("BEHAVIOURDESCRIPTION"));
        inElement.setAddFrequency(AddFrequency.getEnumFromText(inResults.getString("ADDFREQUENCY")));
        inElement.setType(ElementType.getEnumFromText(inResults.getString("ELEMENTTYPE")));
        inElement.setFeedingClass(FeedingClass.getEnumFromText(inResults.getString("FEEDINGCLASS")));
        inElement.setLifespan(inResults.getString("LIFESPAN"));
        inElement.setReferenceID(inResults.getString("REFERENCEID"));
    }

    @Override
    public <T extends LocationCore> T find(T inLocation) {
        T tempLocation = null;
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            state = conn.prepareStatement(findLocation);
            state.setString(1, UtilsData.sanitizeString(inLocation.getName()));
            results = state.executeQuery();
            if (results.next()) {
                tempLocation = (T) inLocation.getClass().newInstance();
                populateLocation(results, tempLocation);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempLocation;
    }

    protected <T extends LocationCore> void populateLocation(ResultSet inResults, T inLocation) throws SQLException {
        inLocation.setName(inResults.getString("NAME"));
        inLocation.setDescription(inResults.getString("DESCRIPTION"));
        inLocation.setRating(LocationRating.getEnumFromText(inResults.getString("RATING")));
        inLocation.setGameViewingRating(GameViewRating.getEnumFromText(inResults.getString("GAMEVIEWINGRATING")));
        inLocation.setHabitatType(inResults.getString("HABITATTYPE"));
        inLocation.setAccommodationType(AccommodationType.getEnumFromText(inResults.getString("ACCOMMODATIONTYPE")));
        inLocation.setCatering(CateringType.getEnumFromText(inResults.getString("CATERING")));
        inLocation.setContactNumbers(inResults.getString("CONTACTNUMBERS"));
        inLocation.setWebsite(inResults.getString("WEBSITE"));
        inLocation.setEmail(inResults.getString("EMAIL"));
        inLocation.setDirections(inResults.getString("DIRECTIONS"));
        inLocation.setLatitude(Latitudes.getEnumFromText(inResults.getString("LATITUDEINDICATOR")));
        inLocation.setLatDegrees(inResults.getInt("LATDEGREES"));
        inLocation.setLatMinutes(inResults.getInt("LATMINUTES"));
        inLocation.setLatSeconds(inResults.getDouble("LATSECONDS"));
        inLocation.setLongitude(Longitudes.getEnumFromText(inResults.getString("LONGITUDEINDICATOR")));
        inLocation.setLonDegrees(inResults.getInt("LONDEGREES"));
        inLocation.setLonMinutes(inResults.getInt("LONMINUTES"));
        inLocation.setLonSeconds(inResults.getDouble("LONSECONDS"));
        inLocation.setGPSAccuracy(GPSAccuracy.getEnumFromText(inResults.getString("GPSACCURACY")));
    }

    @Override
    public <T extends VisitCore> T find(T inVisit) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempVisit = null;
        try {
            state = conn.prepareStatement(findVisit);
            state.setString(1, UtilsData.sanitizeString(inVisit.getName()));
            results = state.executeQuery();
            if (results.next()) {
                tempVisit = (T) inVisit.getClass().newInstance();
                populateVisit(results, tempVisit);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempVisit;
    }

    protected <T extends VisitCore> void populateVisit(ResultSet inResults, T inVisit) throws SQLException {
        inVisit.setName(inResults.getString("NAME"));
        inVisit.setStartDate(inResults.getDate("STARTDATE"));
        inVisit.setEndDate(inResults.getDate("ENDDATE"));
        inVisit.setDescription(inResults.getString("DESCRIPTION"));
        inVisit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(inResults.getString("GAMEWATCHINGINTENSITY")));
        inVisit.setType(VisitType.getEnumFromText(inResults.getString("VISITTYPE")));
        inVisit.setLocationName(inResults.getString("LOCATIONNAME"));
    }

    @Override
    public <T extends SightingCore> T find(T inSighting) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempSighting = null;
        try {
            state = conn.prepareStatement(findSighting);
            state.setLong(1, inSighting.getSightingCounter());
            results = state.executeQuery();
            if (results.next()) {
                tempSighting = (T) inSighting.getClass().newInstance();
                populateSighting(results, tempSighting);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempSighting;
    }

    protected <T extends SightingCore> void populateSighting(ResultSet inResults, T inSighting) throws SQLException {
        inSighting.setSightingCounter(inResults.getLong("SIGHTINGCOUNTER"));
        inSighting.setDate(inResults.getTimestamp("SIGHTINGDATE"));
        inSighting.setElementName(inResults.getString("ELEMENTNAME"));
        inSighting.setLocationName(inResults.getString("LOCATIONNAME"));
        inSighting.setVisitName(inResults.getString("VISITNAME"));
        inSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(inResults.getString("TIMEOFDAY")));
        inSighting.setWeather(Weather.getEnumFromText(inResults.getString("WEATHER")));
        inSighting.setViewRating(ViewRating.getEnumFromText(inResults.getString("VIEWRATING")));
        inSighting.setCertainty(Certainty.getEnumFromText(inResults.getString("CERTAINTY")));
        inSighting.setNumberOfElements(inResults.getInt("NUMBEROFELEMENTS"));
        inSighting.setDetails(inResults.getString("DETAILS"));
        inSighting.setLatitude(Latitudes.getEnumFromText(inResults.getString("LATITUDEINDICATOR")));
        inSighting.setLatDegrees(inResults.getInt("LATDEGREES"));
        inSighting.setLatMinutes(inResults.getInt("LATMINUTES"));
        inSighting.setLatSeconds(inResults.getDouble("LATSECONDS"));
        inSighting.setLongitude(Longitudes.getEnumFromText(inResults.getString("LONGITUDEINDICATOR")));
        inSighting.setLonDegrees(inResults.getInt("LONDEGREES"));
        inSighting.setLonMinutes(inResults.getInt("LONMINUTES"));
        inSighting.setLonSeconds(inResults.getDouble("LONSECONDS"));
        inSighting.setSightingEvidence(SightingEvidence.getEnumFromText(inResults.getString("SIGHTINGEVIDENCE")));
        inSighting.setMoonlight(Moonlight.getEnumFromText(inResults.getString("MOONLIGHT")));
        inSighting.setMoonPhase(inResults.getInt("MOONPHASE"));
        inSighting.setTemperature(inResults.getDouble("TEMPERATURE"));
        inSighting.setUnitsTemperature(UnitsTemperature.getEnumFromText(inResults.getString("TEMPERATUREUNIT")));
        inSighting.setLifeStatus(LifeStatus.getEnumFromText(inResults.getString("LIFESTATUS")));
        inSighting.setSex(Sex.getEnumFromText(inResults.getString("SEX")));
        inSighting.setTag(inResults.getString("TAG"));
        inSighting.setDurationMinutes(inResults.getInt("DURATIONMINUTES"));
        inSighting.setDurationSeconds(inResults.getDouble("DURATIONSECONDS"));
        inSighting.setGPSAccuracy(GPSAccuracy.getEnumFromText(inResults.getString("GPSACCURACY")));
        inSighting.setTimeAccuracy(TimeAccuracy.getEnumFromText(inResults.getString("TIMEACCURACY")));
        inSighting.setAge(Age.getEnumFromText(inResults.getString("AGE")));
    }

    @Override
    public <T extends WildLogFileCore> T find(T inWildLogFile) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempFile = null;
        try {
            String sql = findFile;
            if (inWildLogFile.getDBFilePath() != null) {
                sql = sql + " WHERE ORIGINALPATH = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFile.getDBFilePath()));
            }
            else
            if (inWildLogFile.getId() != null) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFile.getId()));
            }
            if (state != null) {
                results = state.executeQuery();
                if (results.next()) {
                    tempFile = (T) inWildLogFile.getClass().newInstance();
                    populateWildLogFile(results, tempFile);
                }
            }
            else {
                System.err.println("WARNING: Null Statement in DBI_JDBC.java");
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempFile;
    }

    protected <T extends WildLogFileCore> void populateWildLogFile(ResultSet inResults, T inWildLogFile) throws SQLException {
        inWildLogFile.setId(inResults.getString("ID"));
        inWildLogFile.setFilename(inResults.getString("FILENAME"));
        inWildLogFile.setDBFilePath(inResults.getString("ORIGINALPATH"));
        inWildLogFile.setFileType(WildLogFileType.getEnumFromText(inResults.getString("FILETYPE")));
        inWildLogFile.setUploadDate(inResults.getDate("UPLOADDATE"));
        inWildLogFile.setDefaultFile(inResults.getBoolean("ISDEFAULT"));
    }

    @Override
    public <T extends WildLogOptions> T find(T inWildLogOptions) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempWildLogOptions = null;
        try {
            state = conn.prepareStatement(findWildLogOptions);
            results = state.executeQuery();
            if (results.next()) {
                tempWildLogOptions = (T) inWildLogOptions.getClass().newInstance();
                tempWildLogOptions.setDatabaseVersion(results.getInt("VERSION"));
                tempWildLogOptions.setDefaultLatitude(results.getDouble("DEFAULTLATITUDE"));
                tempWildLogOptions.setDefaultLongitude(results.getDouble("DEFAULTLONGITUDE"));
                tempWildLogOptions.setDefaultSlideshowSpeed(results.getFloat("DEFAULTSLIDESHOWSPEED"));
                tempWildLogOptions.setDefaultSlideshowSize(results.getInt("DEFAULTSLIDESHOWSIZE"));
                tempWildLogOptions.setDefaultInputLatitude(Latitudes.getEnumFromText(results.getString("DEFAULTLATOPTION")));
                tempWildLogOptions.setDefaultInputLongitude(Longitudes.getEnumFromText(results.getString("DEFAULTLONOPTION")));
                tempWildLogOptions.setIsOnlinemapTheDefault(results.getBoolean("DEFAULTONLINEMAP"));
                tempWildLogOptions.setUseThumbnailTables(results.getBoolean("USETHUMBNAILTABLES"));
                tempWildLogOptions.setUseThumnailBrowsing(results.getBoolean("USETHUMBNAILBROWSE"));
                tempWildLogOptions.setEnableSounds(results.getBoolean("ENABLESOUNDS"));
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempWildLogOptions;
    }

    @Override
    public <T extends ElementCore> List<T> list(T inElement) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listElement;
            if (inElement.getPrimaryName() != null && !inElement.getPrimaryName().isEmpty() && inElement.getType() == null) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            }
            else
            if ((inElement.getPrimaryName() == null || inElement.getPrimaryName().isEmpty()) && inElement.getType() != null) {
                sql = sql + " WHERE ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inElement.getType().toString());
            }
            else
            if (inElement.getPrimaryName() != null && !inElement.getPrimaryName().isEmpty() && inElement.getType() != null) {
                sql = sql + " WHERE PRIMARYNAME = ? AND ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
                state.setString(2, inElement.getType().toString());
            }
            else
            if (inElement.getScientificName() != null && !inElement.getScientificName().isEmpty()) {
                sql = sql + " WHERE SCIENTIFICNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElement.getScientificName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempElement = (T) inElement.getClass().newInstance();
                populateElement(results, tempElement);
                tempList.add(tempElement);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends LocationCore> List<T> list(T inLocation) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
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
                T tempLocation = (T) inLocation.getClass().newInstance();
                populateLocation(results, tempLocation);
                tempList.add(tempLocation);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends VisitCore> List<T> list(T inVisit) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
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
                T tempVisit = (T) inVisit.getClass().newInstance();
                populateVisit(results, tempVisit);
                tempList.add(tempVisit);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends SightingCore> List<T> list(T inSighting) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listSighting;
            if (inSighting.getSightingCounter() > 0) {
                sql = sql + " WHERE SIGHTINGCOUNTER = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inSighting.getSightingCounter());
            }
            else
            if (inSighting.getElementName() != null && inSighting.getLocationName() != null) {
                sql = sql + " WHERE ELEMENTNAME = ? AND LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
                state.setString(2, UtilsData.sanitizeString(inSighting.getLocationName()));
            }
            else
            if (inSighting.getElementName() != null && inSighting.getVisitName() != null) {
                sql = sql + " WHERE ELEMENTNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
                state.setString(2, UtilsData.sanitizeString(inSighting.getVisitName()));
            }
            else
            if (inSighting.getElementName() != null) {
                sql = sql + " WHERE ELEMENTNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getElementName()));
            }
            else
            if (inSighting.getLocationName() != null) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getLocationName()));
            }
            else
            if (inSighting.getVisitName() != null) {
                sql = sql + " WHERE VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inSighting.getVisitName()));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempSighting = (T) inSighting.getClass().newInstance();
                populateSighting(results, tempSighting);
                tempList.add(tempSighting);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends WildLogFileCore> List<T> list(T inWildLogFile) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listFile;
            if (inWildLogFile.getId() != null && (inWildLogFile.getFileType() == null || WildLogFileType.NONE.equals(inWildLogFile.getFileType()))) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFile.getId()));
            }
            else
            if (inWildLogFile.getId() != null && !(inWildLogFile.getFileType() == null || WildLogFileType.NONE.equals(inWildLogFile.getFileType()))) {
                sql = sql + " WHERE ID = ? AND FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFile.getId()));
                state.setString(2, UtilsData.stringFromObject(inWildLogFile.getFileType()));
            }
            else
            if (inWildLogFile.getId() == null && !(inWildLogFile.getFileType() == null || WildLogFileType.NONE.equals(inWildLogFile.getFileType()))) {
                sql = sql + " WHERE FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.stringFromObject(inWildLogFile.getFileType()));
            }
            else {
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempFile = (T) inWildLogFile.getClass().newInstance();
                populateWildLogFile(results, tempFile);
                tempList.add(tempFile);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends ElementCore> boolean createOrUpdate(T inElement, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                List<ElementCore> list = list(new ElementCore(UtilsData.sanitizeString(inElement.getPrimaryName())));
                if (!list.isEmpty()) {
                    System.err.println("Trying to save an Element using a name that already exists.... (" + inElement.getPrimaryName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Check whether it is an update or not
            if (inOldName != null) {
                // Check whether there was a name change or not.
                if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                    // Update the Sightings
                    SightingCore sighting = new SightingCore();
                    sighting.setElementName(inOldName);
                    List<SightingCore> sightings = list(sighting);
                    for (SightingCore temp : sightings) {
                        temp.setElementName(inElement.getPrimaryName());
                        createOrUpdate(temp, false);
                    }
                    // Update the Files
                    List<WildLogFileCore> wildLogFiles = list(new WildLogFileCore(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFileCore temp : wildLogFiles) {
                        temp.setId(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inElement.getPrimaryName()), 150));
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
    public <T extends LocationCore> boolean createOrUpdate(T inLocation, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                List<LocationCore> list = list(new LocationCore(UtilsData.sanitizeString(inLocation.getName())));
                if (!list.isEmpty()) {
                    return false;
                }
            }
            // Check whether it is an update or not
            if (inOldName != null) {
                // Check whether there was a name change or not.
                if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                    // Update the Sightings
                    SightingCore sighting = new SightingCore();
                    sighting.setLocationName(inOldName);
                    List<SightingCore> sightings = list(sighting);
                    for (SightingCore temp : sightings) {
                        temp.setLocationName(inLocation.getName());
                        createOrUpdate(temp, false);
                    }
                    // Update the Visits
                    List<VisitCore> visits = list(new VisitCore(null, inOldName));
                    for (VisitCore temp : visits) {
                        temp.setLocationName(inLocation.getName());
                        createOrUpdate(temp, temp.getName());
                    }
                    // Update the Files
                    List<WildLogFileCore> wildLogFiles = list(new WildLogFileCore(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFileCore temp : wildLogFiles) {
                        temp.setId(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getName()), 150));
                        createOrUpdate(temp, true);
                    }
                }
                // Update
                state = conn.prepareStatement(updateLocation);
                state.setString(21, UtilsData.sanitizeString(inOldName));
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
            state.setString(20, UtilsData.stringFromObject(inLocation.getGPSAccuracy()));
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
    public <T extends VisitCore> boolean createOrUpdate(T inVisit, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                List<VisitCore> list = list(new VisitCore(UtilsData.sanitizeString(inVisit.getName())));
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
                    SightingCore sighting = new SightingCore();
                    sighting.setVisitName(inOldName);
                    List<SightingCore> sightings = list(sighting);
                    for (SightingCore temp : sightings) {
                        temp.setVisitName(inVisit.getName());
                        createOrUpdate(temp, false);
                    }
                    // Update the Files
                    List<WildLogFileCore> wildLogFiles = list(new WildLogFileCore(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName)));
                    for (WildLogFileCore temp : wildLogFiles) {
                        temp.setId(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getName()), 150));
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
    public <T extends SightingCore> boolean createOrUpdate(T inSighting, boolean inNewButKeepID) {
        PreparedStatement state = null;
        Statement tempState = null;
        ResultSet results = null;
        boolean isUpdate = false;
        try {
            if (inSighting.getSightingCounter() > 0 && !inNewButKeepID) {
                // Note: No need to update the files, because once a sighting has an ID it never changes
                // Update
                state = conn.prepareStatement(updateSighting);
                isUpdate = true;
            }
            else {
                if (!inNewButKeepID) {
                    // Get the new ID
                    tempState = conn.createStatement();
                    inSighting.setSightingCounter(System.currentTimeMillis()*1000000L + randomGenerator.nextInt(999999));
                    // Make sure it is unique (should almost always be, but let's be safe...)
                    results = tempState.executeQuery("SELECT COUNT(SIGHTINGCOUNTER) FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter());
                    while (results.next() && results.getInt(1) > 0) {
                        // ID already used, try a new ID
                        inSighting.setSightingCounter(System.currentTimeMillis()*1000000L + randomGenerator.nextInt(999999));
                        results = tempState.executeQuery("SELECT COUNT(SIGHTINGCOUNTER) FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter());
                    }
                }
                // Insert
                state = conn.prepareStatement(createSighting);
            }
            // Populate the values
            state.setLong(1, inSighting.getSightingCounter());
            if (inSighting.getDate() != null) {
                state.setTimestamp(2, new Timestamp(inSighting.getDate().getTime()));
            }
            else {
                state.setTimestamp(2, null);
            }
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
            state.setInt(28, inSighting.getDurationMinutes());
            state.setDouble(29, inSighting.getDurationSeconds());
            state.setString(30, UtilsData.stringFromObject(inSighting.getGPSAccuracy()));
            state.setString(31, UtilsData.stringFromObject(inSighting.getTimeAccuracy()));
            state.setString(32, UtilsData.stringFromObject(inSighting.getAge()));
            if (isUpdate) {
                state.setLong(33, inSighting.getSightingCounter());
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
    public <T extends WildLogFileCore> boolean createOrUpdate(T inWildLogFile, boolean inUpdate) {
        PreparedStatement state = null;
        try {
            if (inUpdate) {
                state = conn.prepareStatement(updateFile);
            }
            else {
                state = conn.prepareStatement(createFile);
            }
            state.setString(1, UtilsData.sanitizeString(inWildLogFile.getId()));
            state.setString(2, UtilsData.sanitizeString(inWildLogFile.getFilename()));
            state.setString(3, UtilsData.sanitizeString(inWildLogFile.getDBFilePath()));
            state.setString(4, UtilsData.stringFromObject(inWildLogFile.getFileType()));
            if (inWildLogFile.getUploadDate() != null) {
                state.setDate(5, new java.sql.Date(inWildLogFile.getUploadDate().getTime()));
            }
            else {
                state.setDate(5, null);
            }
            if (inWildLogFile.isDefaultFile()) {
                state.setBoolean(6, true);
            }
            else {
                state.setBoolean(6, false);
            }
            if (inUpdate) {
                state.setString(7, UtilsData.sanitizeString(inWildLogFile.getDBFilePath()));
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
    public <T extends WildLogOptions> boolean createOrUpdate(T inWildLogOptions) {
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
                    prepState.setBoolean(8, inWildLogOptions.isUseThumbnailTables());
                    prepState.setBoolean(9, inWildLogOptions.isUseThumnailBrowsing());
                    prepState.setBoolean(10, inWildLogOptions.isEnableSounds());
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
    public <T extends ElementCore> boolean delete(T inElement) {
        PreparedStatement state = null;
        try {
            // Delete the ElementCore
            state = conn.prepareStatement(deleteElement);
            state.setString(1, UtilsData.sanitizeString(inElement.getPrimaryName()));
            state.executeUpdate();
            // Delete all Sightings for this ElementCore
            SightingCore sighting = new SightingCore();
            sighting.setElementName(inElement.getPrimaryName());
            List<SightingCore> sightingList = list(sighting);
            for (SightingCore temp : sightingList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFileCore file = new WildLogFileCore();
            file.setId(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inElement.getPrimaryName()));
            List<WildLogFileCore> fileList = list(file);
            for (WildLogFileCore temp : fileList) {
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
    public <T extends LocationCore> boolean delete(T inLocation) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteLocation);
            state.setString(1, UtilsData.sanitizeString(inLocation.getName()));
            // Delete LocationCore
            state.executeUpdate();
            state.close();
            // Delete Visits for this LocationCore
            VisitCore visit = new VisitCore();
            visit.setLocationName(inLocation.getName());
            List<VisitCore> visitList = list(visit);
            for (VisitCore temp : visitList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFileCore file = new WildLogFileCore();
            file.setId(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inLocation.getName()));
            List<WildLogFileCore> fileList = list(file);
            for (WildLogFileCore temp : fileList) {
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
    public <T extends VisitCore> boolean delete(T inVisit) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteVisit);
            state.setString(1, UtilsData.sanitizeString(inVisit.getName()));
            // Delete VisitCore
            state.executeUpdate();
            // Delete Sightings for this VisitCore
            SightingCore sighting = new SightingCore();
            sighting.setVisitName(inVisit.getName());
            List<SightingCore> sightingList = list(sighting);
            for (SightingCore temp : sightingList) {
                delete(temp);
            }
            // Delete Fotos
            WildLogFileCore file = new WildLogFileCore();
            file.setId(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inVisit.getName()));
            List<WildLogFileCore> fileList = list(file);
            for (WildLogFileCore temp : fileList) {
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
    public <T extends SightingCore> boolean delete(T inSighting) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteSighting);
            state.setLong(1, inSighting.getSightingCounter());
            // Delete Sightings
            state.executeUpdate();
            // Delete Fotos
            WildLogFileCore file = new WildLogFileCore();
            file.setId(SightingCore.WILDLOGFILE_ID_PREFIX + inSighting.getSightingCounter());
            List<WildLogFileCore> fileList = list(file);
            for (WildLogFileCore temp : fileList) {
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
    public <T extends WildLogFileCore> boolean delete(T inWildLogFile) {
        // Note: This method only deletes one file at a time from the database.
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteFile);
            state.setString(1, UtilsData.sanitizeString(inWildLogFile.getDBFilePath()));
            // Delete File from database
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

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param ex the SQLException from which to print details.
     */
    protected static void printSQLException(SQLException ex) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        SQLException nestedEx = ex;
        while (nestedEx != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + nestedEx.getSQLState());
            System.err.println("  Error Code: " + nestedEx.getErrorCode());
            System.err.println("  Message:    " + nestedEx.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            nestedEx.printStackTrace(System.err);
            nestedEx = nestedEx.getNextException();
        }
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

    @Override
    public <T extends SightingCore> List<T> searchSightingOnDate(Date inStartDate, Date inEndDate, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
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
                T tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting);
                tempList.add(tempSighting);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

    @Override
    public <T extends LocationCount, V extends ElementCore> List<T> queryLocationCountForElement(V inElement, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = queryLocationCountForElement;
            state = conn.prepareStatement(sql);
            if (inElement != null) {
                state.setString(1, inElement.getPrimaryName());
            }
            else {
                state.setString(1, null);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempLocationCount = inReturnType.newInstance();
                tempLocationCount.setLocationName(results.getString(1));
                tempLocationCount.setCount(results.getInt(2));
                tempList.add(tempLocationCount);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return tempList;
    }

}
