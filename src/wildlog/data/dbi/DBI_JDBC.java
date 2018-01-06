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
import wildlog.data.dataobjects.AdhocData;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.INaturalistLinkedData;
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
    protected static final int WILDLOG_DB_VERSION = 10;
    // Tables
    protected static final String tableElements = "CREATE TABLE ELEMENTS (PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL, OTHERNAME varchar(150), SCIENTIFICNAME varchar(150), DESCRIPTION longvarchar, DISTRIBUTION longvarchar, NUTRITION longvarchar, WATERDEPENDANCE varchar(50), SIZEMALEMIN float(52), SIZEMALEMAX float(52), SIZEFEMALEMIN float(52), SIZEFEMALEMAX float(52), SIZEUNIT varchar(10), SIZETYPE varchar(50), WEIGHTMALEMIN float(52), WEIGHTMALEMAX float(52), WEIGHTFEMALEMIN float(52), WEIGHTFEMALEMAX float(52), WEIGHTUNIT varchar(10), BREEDINGDURATION varchar(50), BREEDINGNUMBER varchar(50), WISHLISTRATING varchar(50), DIAGNOSTICDESCRIPTION longvarchar, ACTIVETIME varchar(50), ENDANGEREDSTATUS varchar(35), BEHAVIOURDESCRIPTION longvarchar, ADDFREQUENCY varchar(50), ELEMENTTYPE varchar(50), FEEDINGCLASS varchar(50), LIFESPAN varchar(50), REFERENCEID varchar(50))";
    protected static final String tableLocations = "CREATE TABLE LOCATIONS (NAME varchar(150) PRIMARY KEY NOT NULL, DESCRIPTION longvarchar, RATING varchar(50), GAMEVIEWINGRATING varchar(50), HABITATTYPE longvarchar, ACCOMMODATIONTYPE varchar(150), CATERING varchar(50), CONTACTNUMBERS varchar(50), WEBSITE varchar(100), EMAIL varchar(100), DIRECTIONS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, GPSACCURACY varchar(50))";
    protected static final String tableVisits = "CREATE TABLE VISITS (NAME varchar(150) PRIMARY KEY NOT NULL, STARTDATE date, ENDDATE date, DESCRIPTION longvarchar, GAMEWATCHINGINTENSITY varchar(50), VISITTYPE varchar(50), LOCATIONNAME varchar(150))";
    protected static final String tableSightings = "CREATE TABLE SIGHTINGS (SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL, SIGHTINGDATE timestamp NOT NULL, ELEMENTNAME varchar(150) NOT NULL, LOCATIONNAME varchar(150) NOT NULL, VISITNAME varchar(150) NOT NULL, TIMEOFDAY varchar(50), WEATHER varchar(50), VIEWRATING varchar(50), CERTAINTY varchar(50), NUMBEROFELEMENTS int, DETAILS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, SIGHTINGEVIDENCE varchar(50), MOONLIGHT varchar(50), MOONPHASE int, TEMPERATURE double, TEMPERATUREUNIT varchar(15), LIFESTATUS varchar(15), SEX varchar(15), TAG longvarchar, DURATIONMINUTES int, DURATIONSECONDS double, GPSACCURACY varchar(50), TIMEACCURACY varchar(50), AGE varchar(50))";
    protected static final String tableFiles = "CREATE TABLE FILES (ID varchar(175), FILENAME varchar(255), ORIGINALPATH varchar(500), FILETYPE varchar(50), UPLOADDATE date, ISDEFAULT smallint, FILEDATE timestamp, FILESIZE bigint)";
    protected static final String tableWildLogOptions = "CREATE TABLE WILDLOG (VERSION int DEFAULT " + WILDLOG_DB_VERSION + ", DEFAULTLATITUDE double DEFAULT -28.7, DEFAULTLONGITUDE double DEFAULT 24.7, DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5, DEFAULTSLIDESHOWSIZE int DEFAULT 750, USETHUMBNAILTABLES smallint DEFAULT true, USETHUMBNAILBROWSE smallint DEFAULT false, ENABLESOUNDS smallint DEFAULT true, USESCIENTIFICNAMES smallint DEFAULT true, WORKSPACENAME varchar(50) DEFAULT 'WildLog Workspace', WORKSPACEID bigint DEFAULT 0, UPLOADLOGS smallint DEFAULT true, BUNDLEDPLAYERS smallint DEFAULT true, USEINDVCOUNTINPATH smallint DEFAULT false)";
    protected static final String tableAdhocData = "CREATE TABLE ADHOC (FIELDID varchar(150) NOT NULL, DATAKEY varchar(150) NOT NULL, DATAVALUE TEXT)";
    protected static final String tableINaturalistLinkedData = "CREATE TABLE INATURALIST (WILDLOGID bigint PRIMARY KEY NOT NULL, INATURALISTID bigint NOT NULL, INATURALISTDATA TEXT)";
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
    protected static final String findAdhocData = "SELECT * FROM ADHOC WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String findINaturalistLinkedData = "SELECT * FROM INATURALIST WHERE WILDLOGID = ? OR INATURALISTID = ?";
    // List
    protected static final String listLocation = "SELECT * FROM LOCATIONS";
    protected static final String listVisit = "SELECT * FROM VISITS";
    protected static final String listElement = "SELECT * FROM ELEMENTS";
    protected static final String listFile = "SELECT * FROM FILES";
    protected static final String listAdhocData = "SELECT * FROM ADHOC";
    protected static final String listINaturalistLinkedData = "SELECT * FROM INATURALIST";
    // Create
    protected static final String createLocation = "INSERT INTO LOCATIONS (NAME,DESCRIPTION,RATING,GAMEVIEWINGRATING,HABITATTYPE,ACCOMMODATIONTYPE,CATERING,CONTACTNUMBERS,WEBSITE,EMAIL,DIRECTIONS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS,GPSACCURACY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createVisit = "INSERT INTO VISITS (NAME,STARTDATE,ENDDATE,DESCRIPTION,GAMEWATCHINGINTENSITY,VISITTYPE,LOCATIONNAME) VALUES (?,?,?,?,?,?,?)";
    protected static final String createSighting = "INSERT INTO SIGHTINGS (SIGHTINGCOUNTER,SIGHTINGDATE,ELEMENTNAME,LOCATIONNAME,VISITNAME,TIMEOFDAY,WEATHER,VIEWRATING,CERTAINTY,NUMBEROFELEMENTS,DETAILS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDS,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDS,SIGHTINGEVIDENCE,MOONPHASE,MOONLIGHT,TEMPERATURE,TEMPERATUREUNIT,LIFESTATUS,SEX,TAG,DURATIONMINUTES,DURATIONSECONDS,GPSACCURACY,TIMEACCURACY,AGE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createElement = "INSERT INTO ELEMENTS (PRIMARYNAME,OTHERNAME,SCIENTIFICNAME,DESCRIPTION,DISTRIBUTION,NUTRITION,WATERDEPENDANCE,SIZEMALEMIN,SIZEMALEMAX,SIZEFEMALEMIN,SIZEFEMALEMAX,SIZEUNIT,SIZETYPE,WEIGHTMALEMIN,WEIGHTMALEMAX,WEIGHTFEMALEMIN,WEIGHTFEMALEMAX,WEIGHTUNIT,BREEDINGDURATION,BREEDINGNUMBER,WISHLISTRATING,DIAGNOSTICDESCRIPTION,ACTIVETIME,ENDANGEREDSTATUS,BEHAVIOURDESCRIPTION,ADDFREQUENCY,ELEMENTTYPE,FEEDINGCLASS,LIFESPAN,REFERENCEID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createFile = "INSERT INTO FILES (ID,FILENAME,ORIGINALPATH,FILETYPE,UPLOADDATE,ISDEFAULT,FILEDATE,FILESIZE) VALUES (?,?,?,?,?,?,?,?)";
    protected static final String createWildLogOptions = "INSERT INTO WILDLOG VALUES (DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, ?, DEFAULT, DEFAULT, DEFAULT)";
    protected static final String createAdhocData = "INSERT INTO ADHOC (FIELDID, DATAKEY, DATAVALUE) VALUES (?, ?, ?)";
    protected static final String createINaturalistLinkedData = "INSERT INTO INATURALIST (WILDLOGID, INATURALISTID, INATURALISTDATA) VALUES (?, ?, ?)";
    // Update
    protected static final String updateLocation = "UPDATE LOCATIONS SET NAME = ?, DESCRIPTION = ?, RATING = ?, GAMEVIEWINGRATING = ?, HABITATTYPE = ?, ACCOMMODATIONTYPE = ?, CATERING = ?, CONTACTNUMBERS = ?, WEBSITE = ?, EMAIL = ?, DIRECTIONS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, GPSACCURACY = ? WHERE NAME = ?";
    protected static final String updateVisit = "UPDATE VISITS SET NAME = ?, STARTDATE = ?, ENDDATE = ?, DESCRIPTION = ?, GAMEWATCHINGINTENSITY = ?, VISITTYPE = ?, LOCATIONNAME = ? WHERE NAME = ?";
    protected static final String updateSighting = "UPDATE SIGHTINGS SET SIGHTINGCOUNTER = ?, SIGHTINGDATE = ?, ELEMENTNAME = ?, LOCATIONNAME = ?, VISITNAME = ?, TIMEOFDAY = ?, WEATHER = ?, VIEWRATING = ?, CERTAINTY = ?, NUMBEROFELEMENTS = ?, DETAILS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, SIGHTINGEVIDENCE = ?, MOONPHASE = ?, MOONLIGHT = ?, TEMPERATURE = ?, TEMPERATUREUNIT = ?, LIFESTATUS = ?, SEX = ?, TAG = ?, DURATIONMINUTES = ?, DURATIONSECONDS = ?, GPSACCURACY = ?, TIMEACCURACY = ?, AGE = ? WHERE SIGHTINGCOUNTER = ?";
    protected static final String updateElement = "UPDATE ELEMENTS SET PRIMARYNAME = ?, OTHERNAME = ?, SCIENTIFICNAME = ?, DESCRIPTION = ?, DISTRIBUTION = ?, NUTRITION = ?, WATERDEPENDANCE = ?, SIZEMALEMIN = ?, SIZEMALEMAX = ?, SIZEFEMALEMIN = ?, SIZEFEMALEMAX = ?, SIZEUNIT = ?, SIZETYPE = ?, WEIGHTMALEMIN = ?, WEIGHTMALEMAX = ?, WEIGHTFEMALEMIN = ?, WEIGHTFEMALEMAX = ?, WEIGHTUNIT = ?, BREEDINGDURATION = ?, BREEDINGNUMBER = ?, WISHLISTRATING = ?, DIAGNOSTICDESCRIPTION = ?, ACTIVETIME = ?, ENDANGEREDSTATUS = ?, BEHAVIOURDESCRIPTION = ?, ADDFREQUENCY = ?, ELEMENTTYPE = ?, FEEDINGCLASS = ?, LIFESPAN = ?, REFERENCEID = ? WHERE PRIMARYNAME = ?";
    protected static final String updateFile = "UPDATE FILES SET ID = ?, FILENAME = ?, ORIGINALPATH = ?, FILETYPE = ?, UPLOADDATE = ?, ISDEFAULT = ?, FILEDATE = ?, FILESIZE = ? WHERE ORIGINALPATH = ?";
    protected static final String updateWildLogOptions = "UPDATE WILDLOG SET DEFAULTLATITUDE = ?, DEFAULTLONGITUDE = ?, DEFAULTSLIDESHOWSPEED = ?, DEFAULTSLIDESHOWSIZE = ?, USETHUMBNAILTABLES = ?, USETHUMBNAILBROWSE =?, ENABLESOUNDS = ?, USESCIENTIFICNAMES = ?, WORKSPACENAME = ?, WORKSPACEID = ?, UPLOADLOGS = ?, BUNDLEDPLAYERS = ?, USEINDVCOUNTINPATH = ?";
    protected static final String updateAdhocData = "UPDATE ADHOC SET FIELDID = ?, DATAKEY = ?, DATAVALUE = ? WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String updateINaturalistLinkedData = "UPDATE INATURALIST SET WILDLOGID = ?, INATURALISTID = ?, INATURALISTDATA = ? WHERE WILDLOGID = ? OR INATURALISTID = ?";
    // Delete
    protected static final String deleteLocation = "DELETE FROM LOCATIONS WHERE NAME = ?";
    protected static final String deleteVisit = "DELETE FROM VISITS WHERE NAME = ?";
    protected static final String deleteSighting = "DELETE FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?";
    protected static final String deleteElement = "DELETE FROM ELEMENTS WHERE PRIMARYNAME = ?";
    protected static final String deleteFile = "DELETE FROM FILES WHERE ORIGINALPATH = ?";
    protected static final String deleteAdhocData = "DELETE FROM ADHOC WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String deleteINaturalistLinkedData = "DELETE FROM INATURALIST WHERE WILDLOGID = ? OR INATURALISTID = ?";
    // Queries
    protected static final String queryLocationCountForElement = "select SIGHTINGS.LOCATIONNAME, count(*) cnt from SIGHTINGS where SIGHTINGS.ELEMENTNAME = ? group by SIGHTINGS.LOCATIONNAME order by cnt desc";
    // Variables
    protected Connection conn;

    public DBI_JDBC() {
    }

    @Override
    public boolean initialize(boolean inCreateDefaultRecords) throws SQLException {
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
                    createElement(new ElementCore("Unknown Creature"));
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
                    createLocation(new LocationCore("Some Place"));
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
                    createVisit(new VisitCore("Casual Observations", "Some Place"));
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
            results = conn.getMetaData().getTables(null, null, "ADHOC", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableAdhocData);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS FIELDID_DATAKEY ON ADHOC (FIELDID, DATAKEY)");
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "INATURALIST", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableINaturalistLinkedData);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS ID_LINKS ON INATURALIST (WILDLOGID, INATURALISTID)");
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "WILDLOG", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableWildLogOptions);
                closeStatement(state);
                if (inCreateDefaultRecords) {
                    createWildLogOptions(new WildLogOptions());
                }
                // Since this is the first time the database is being created, also update the cache size for H2 (might fail on other DBs)
                try {
                    state = conn.createStatement();
                    state.execute("SET CACHE_SIZE 32768");
                    closeStatement(state);
                }
                catch (SQLException sqle) {
                    // Don't worry too much if this failed
                    System.out.println("WARNING: Could not change database cache size... " + sqle.getMessage());
                }
                finally {
                    closeStatement(state);
                }
            }
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
            started = false;
            throw sqle;
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
    public int countElements(String inPrimaryName, String inScientificName) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countElement;
            if (inPrimaryName != null && UtilsData.sanitizeString(inPrimaryName).length() > 0) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inPrimaryName));
            }
            else
            if (inScientificName != null && UtilsData.sanitizeString(inScientificName).length() > 0) {
                sql = sql + " WHERE SCIENTIFICNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inScientificName));
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
    public int countLocations(String inName) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countLocation;
            if (inName != null && UtilsData.sanitizeString(inName).length() > 0) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
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
    public int countVisits(String inName, String inLocationName) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countVisit;
            if (inName != null && UtilsData.sanitizeString(inName).length() > 0) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else 
            if (inLocationName != null && UtilsData.sanitizeString(inLocationName).length() > 0) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocationName));
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
    public int countSightings(long inSightingCounter, String inElementName, String inLocationName, String inVisitName) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countSighting;
            if (inSightingCounter > 0) {
                sql = sql + " WHERE SIGHTINGCOUNTER = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inSightingCounter);
            }
            else
            if (inElementName != null && UtilsData.sanitizeString(inElementName).length() > 0 
                    && inLocationName != null && UtilsData.sanitizeString(inLocationName).length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ? AND LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
                state.setString(2, UtilsData.sanitizeString(inLocationName));
            }
            else
            if (inElementName != null && UtilsData.sanitizeString(inElementName).length() > 0 
                    && inVisitName != null && UtilsData.sanitizeString(inVisitName).length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
                state.setString(2, UtilsData.sanitizeString(inVisitName));
            }
            else
            if (inElementName != null && UtilsData.sanitizeString(inElementName).length() > 0) {
                sql = sql + " WHERE ELEMENTNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
            }
            else
            if (inLocationName != null && UtilsData.sanitizeString(inLocationName).length() > 0) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocationName));
            }
            else
            if (inVisitName != null && UtilsData.sanitizeString(inVisitName).length() > 0) {
                sql = sql + " WHERE VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inVisitName));
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
    public int countWildLogFiles(String inDBFilePath, String inWildLogFileID) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countFile;
            if (inDBFilePath != null && UtilsData.sanitizeString(inDBFilePath).length() > 0) {
                sql = sql + " WHERE ORIGINALPATH = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inDBFilePath.replace("\\", "/")));
            }
            else
            if (inWildLogFileID != null && UtilsData.sanitizeString(inWildLogFileID).length() > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFileID));
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
    public <T extends ElementCore> T findElement(String inPrimaryName, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempElement = null;
        try {
            state = conn.prepareStatement(findElement);
            state.setString(1, UtilsData.sanitizeString(inPrimaryName));
            results = state.executeQuery();
            if (results.next()) {
                tempElement = inReturnType.newInstance();
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
    public <T extends LocationCore> T findLocation(String inName, Class<T> inReturnType) {
        T tempLocation = null;
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            state = conn.prepareStatement(findLocation);
            state.setString(1, UtilsData.sanitizeString(inName));
            results = state.executeQuery();
            if (results.next()) {
                tempLocation = inReturnType.newInstance();
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
    public <T extends VisitCore> T findVisit(String inName, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempVisit = null;
        try {
            state = conn.prepareStatement(findVisit);
            state.setString(1, UtilsData.sanitizeString(inName));
            results = state.executeQuery();
            if (results.next()) {
                tempVisit = inReturnType.newInstance();
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
        if (inResults.getDate("STARTDATE") != null) {
            inVisit.setStartDate(new Date(inResults.getDate("STARTDATE").getTime()));
        }
        else {
            inVisit.setStartDate(null);
        }
        if (inResults.getDate("ENDDATE") != null) {
            inVisit.setEndDate(new Date(inResults.getDate("ENDDATE").getTime()));
        }
        else {
            inVisit.setEndDate(null);
        }
        inVisit.setDescription(inResults.getString("DESCRIPTION"));
        inVisit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(inResults.getString("GAMEWATCHINGINTENSITY")));
        inVisit.setType(VisitType.getEnumFromText(inResults.getString("VISITTYPE")));
        inVisit.setLocationName(inResults.getString("LOCATIONNAME"));
    }

    @Override
    public <T extends SightingCore> T findSighting(long inSightingCounter, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempSighting = null;
        try {
            state = conn.prepareStatement(findSighting);
            state.setLong(1, inSightingCounter);
            results = state.executeQuery();
            if (results.next()) {
                tempSighting = inReturnType.newInstance();
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
        if (inResults.getTimestamp("SIGHTINGDATE") != null) {
            inSighting.setDate(new Date(inResults.getTimestamp("SIGHTINGDATE").getTime()));
        }
        else {
            inSighting.setDate(null);
        }
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
    public <T extends WildLogFileCore> T findWildLogFile(String inDBFilePath, String inWildLogFileID, WildLogFileType inWildLogFileType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempFile = null;
        try {
            String sql = findFile;
            if (inDBFilePath != null) {
                sql = sql + " WHERE ORIGINALPATH = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH LIMIT 1";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inDBFilePath.replace("\\", "/")));
            }
            else
            if (inWildLogFileID != null) {
                sql = sql + " WHERE ID = ?";
                if (inWildLogFileType != null) {
                    sql = sql + " AND FILETYPE = ?";
                }
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH LIMIT 1";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFileID));
                if (inWildLogFileType != null) {
                    state.setString(2, UtilsData.stringFromObject(inWildLogFileType));
                }
            }
            if (state != null) {
                results = state.executeQuery();
                if (results.next()) {
                    tempFile = inReturnType.newInstance();
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
        inWildLogFile.setDBFilePath(inResults.getString("ORIGINALPATH").replace("\\", "/"));
        inWildLogFile.setFileType(WildLogFileType.getEnumFromText(inResults.getString("FILETYPE")));
        if (inResults.getDate("UPLOADDATE") != null) {
            inWildLogFile.setUploadDate(new Date(inResults.getDate("UPLOADDATE").getTime()));
        }
        else {
            inWildLogFile.setUploadDate(null);
        }
        inWildLogFile.setDefaultFile(inResults.getBoolean("ISDEFAULT"));
        if (inResults.getTimestamp("FILEDATE") != null) {
            inWildLogFile.setFileDate(new Date(inResults.getTimestamp("FILEDATE").getTime()));
        }
        else {
            inWildLogFile.setFileDate(null);
        }
        inWildLogFile.setFileSize(inResults.getLong("FILESIZE"));
    }

    @Override
    public <T extends WildLogOptions> T findWildLogOptions(Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempWildLogOptions = null;
        try {
            state = conn.prepareStatement(findWildLogOptions);
            results = state.executeQuery();
            if (results.next()) {
                tempWildLogOptions = inReturnType.newInstance();
                tempWildLogOptions.setDatabaseVersion(results.getInt("VERSION"));
                tempWildLogOptions.setDefaultLatitude(results.getDouble("DEFAULTLATITUDE"));
                tempWildLogOptions.setDefaultLongitude(results.getDouble("DEFAULTLONGITUDE"));
                tempWildLogOptions.setDefaultSlideshowSpeed(results.getFloat("DEFAULTSLIDESHOWSPEED"));
                tempWildLogOptions.setDefaultSlideshowSize(results.getInt("DEFAULTSLIDESHOWSIZE"));
                tempWildLogOptions.setUseThumbnailTables(results.getBoolean("USETHUMBNAILTABLES"));
                tempWildLogOptions.setUseThumnailBrowsing(results.getBoolean("USETHUMBNAILBROWSE"));
                tempWildLogOptions.setEnableSounds(results.getBoolean("ENABLESOUNDS"));
                tempWildLogOptions.setUseScientificNames(results.getBoolean("USESCIENTIFICNAMES"));
                tempWildLogOptions.setWorkspaceName(results.getString("WORKSPACENAME"));
                tempWildLogOptions.setWorkspaceID(results.getLong("WORKSPACEID"));
                tempWildLogOptions.setUploadLogs(results.getBoolean("UPLOADLOGS"));
                tempWildLogOptions.setBundledPlayers(results.getBoolean("BUNDLEDPLAYERS"));
                tempWildLogOptions.setUseIndividualsInSightingPath(results.getBoolean("USEINDVCOUNTINPATH"));
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
    public <T extends AdhocData> T findAdhocData(String inFieldID, String inDataKey, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T temp = null;
        try {
            state = conn.prepareStatement(findAdhocData);
            state.setString(1, UtilsData.sanitizeString(inFieldID));
            state.setString(2, UtilsData.sanitizeString(inDataKey));
            results = state.executeQuery();
            if (results.next()) {
                temp = inReturnType.newInstance();
                temp.setFieldID(results.getString("FIELDID"));
                temp.setDataKey(results.getString("DATAKEY"));
                temp.setDataValue(results.getString("DATAVALUE"));
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
        return temp;
    }

    @Override
    public <T extends ElementCore> List<T> listElements(String inPrimaryName, String inScientificName, ElementType inElementType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listElement;
            if (inPrimaryName != null && inPrimaryName.length() > 0 && inElementType == null) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inPrimaryName));
            }
            else
            if ((inPrimaryName == null || inPrimaryName.length() == 0) && inElementType != null) {
                sql = sql + " WHERE ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inElementType.toString());
            }
            else
            if (inPrimaryName != null && inPrimaryName.length() > 0 && inElementType != null) {
                sql = sql + " WHERE PRIMARYNAME = ? AND ELEMENTTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inPrimaryName));
                state.setString(2, inElementType.toString());
            }
            else
            if (inScientificName != null && inScientificName.length() > 0) {
                sql = sql + " WHERE SCIENTIFICNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inScientificName));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempElement = inReturnType.newInstance();
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
    public <T extends LocationCore> List<T> listLocations(String inName, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listLocation;
            if (inName != null) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempLocation = inReturnType.newInstance();
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
    public <T extends VisitCore> List<T> listVisits(String inName, String inLocationName, VisitType inVisitType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listVisit;
            if (inName != null) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else 
            if (inLocationName != null) {
                sql = sql + " WHERE LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocationName));
            }
            else 
            if (inVisitType != null) {
                sql = sql + " WHERE VISITTYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, inVisitType.toString());
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempVisit = inReturnType.newInstance();
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
    public <T extends SightingCore> List<T> listSightings(long inSightingCounter, String inElementName, 
            String inLocationName, String inVisitName, boolean inIncludeCachedValues, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = "SELECT SIGHTINGS.*";
            if (inIncludeCachedValues) {
                sql = sql + ", ELEMENTS.ELEMENTTYPE, VISITS.VISITTYPE, "
                          + " (SELECT COUNT(*) FROM INATURALIST INAT WHERE INAT.WILDLOGID = SIGHTINGS.SIGHTINGCOUNTER) INATCOUNT"
                          + " FROM SIGHTINGS"
                          + " LEFT JOIN ELEMENTS ON ELEMENTS.PRIMARYNAME = SIGHTINGS.ELEMENTNAME" 
                          + " LEFT JOIN VISITS ON VISITS.NAME = SIGHTINGS.VISITNAME";
            }
            else {
                sql = sql + " FROM SIGHTINGS";
            }
            if (inSightingCounter > 0) {
                sql = sql + " WHERE SIGHTINGCOUNTER = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inSightingCounter);
            }
            else
            if (inElementName != null && inLocationName != null && inVisitName != null) {
                sql = sql + " WHERE ELEMENTNAME = ? AND SIGHTINGS.LOCATIONNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
                state.setString(2, UtilsData.sanitizeString(inLocationName));
                state.setString(3, UtilsData.sanitizeString(inVisitName));
            }
            else
            if (inElementName != null && inLocationName != null) {
                sql = sql + " WHERE ELEMENTNAME = ? AND SIGHTINGS.LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
                state.setString(2, UtilsData.sanitizeString(inLocationName));
            }
            else
            if (inElementName != null && inVisitName != null) {
                sql = sql + " WHERE ELEMENTNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
                state.setString(2, UtilsData.sanitizeString(inVisitName));
            }
            else
            if (inLocationName != null && inVisitName != null) {
                sql = sql + " WHERE SIGHTINGS.LOCATIONNAME = ? AND VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocationName));
                state.setString(2, UtilsData.sanitizeString(inVisitName));
            }
            else
            if (inElementName != null) {
                sql = sql + " WHERE ELEMENTNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inElementName));
            }
            else
            if (inLocationName != null) {
                sql = sql + " WHERE SIGHTINGS.LOCATIONNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inLocationName));
            }
            else
            if (inVisitName != null) {
                sql = sql + " WHERE VISITNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inVisitName));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting);
                if (inIncludeCachedValues) {
                    tempSighting.setCachedElementType(ElementType.getEnumFromText(results.getString("ELEMENTTYPE")));
                    tempSighting.setCachedVisitType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                    if (results.getInt("INATCOUNT") == 0) {
                        tempSighting.setCachedLinkedToINaturalist(false);
                    }
                    else {
                        tempSighting.setCachedLinkedToINaturalist(true);
                    }
                }
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
    public <T extends WildLogFileCore> List<T> listWildLogFiles(String inWildLogFileID, WildLogFileType inWildLogFileType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listFile;
            if (inWildLogFileID != null && (inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFileID));
            }
            else
            if (inWildLogFileID != null && !(inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
                sql = sql + " WHERE ID = ? AND FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inWildLogFileID));
                state.setString(2, UtilsData.stringFromObject(inWildLogFileType));
            }
            else
            if (inWildLogFileID == null && !(inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
                sql = sql + " WHERE FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.stringFromObject(inWildLogFileType));
            }
            else {
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempFile = inReturnType.newInstance();
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
    public <T extends AdhocData> List<T> listAdhocDatas(String inFieldID, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listAdhocData;
            if (inFieldID != null && inFieldID.length() > 0) {
                sql = sql + " WHERE FIELDID = ?";
                sql = sql + " ORDER BY FIELDID, DATAKEY";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inFieldID));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T temp = inReturnType.newInstance();
                temp.setFieldID(results.getString("FIELDID"));
                temp.setDataKey(results.getString("DATAKEY"));
                temp.setDataValue(results.getString("DATAVALUE"));
                tempList.add(temp);
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
    public <T extends INaturalistLinkedData> List<T> listINaturalistLinkedDatas(Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listINaturalistLinkedData;
            state = conn.prepareStatement(sql);
            results = state.executeQuery();
            while (results.next()) {
                T temp = inReturnType.newInstance();
                temp.setWildlogID(results.getLong("WILDLOGID"));
                temp.setINaturalistID(results.getLong("INATURALISTID"));
                temp.setINaturalistData(results.getString("INATURALISTDATA"));
                tempList.add(temp);
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
    public <T extends ElementCore> boolean createElement(T inElement) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (countElements(inElement.getPrimaryName(), null) > 0) {
                System.err.println("Trying to save an Element using a name that already exists.... (" + inElement.getPrimaryName() + ")");
                return false;
            }
            // Insert
            state = conn.prepareStatement(createElement);
            maintainElement(state, inElement);
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
    public <T extends ElementCore> boolean updateElement(T inElement, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                if (countElements(inElement.getPrimaryName(), null) > 0) {
                    System.err.println("Trying to save an Element using a name that already exists.... (" + inElement.getPrimaryName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Check whether there was a name change or not.
            if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                // Update the Sightings
                List<SightingCore> lstSightings = listSightings(0, inOldName, null, null, false, SightingCore.class);
                for (SightingCore sighting : lstSightings) {
                    sighting.setElementName(inElement.getPrimaryName());
                    updateSighting(sighting);
                }
                // Update the Files
                List<WildLogFileCore> lstWildLogFiles = listWildLogFiles(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName), null, WildLogFileCore.class);
                for (WildLogFileCore wildLogFile : lstWildLogFiles) {
                    wildLogFile.setId(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inElement.getPrimaryName()), 150));
                    updateWildLogFile(wildLogFile);
                }
            }
            // Update
            state = conn.prepareStatement(updateElement);
            maintainElement(state, inElement);
            state.setString(31, UtilsData.sanitizeString(inOldName));
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

    private <T extends ElementCore> void maintainElement(PreparedStatement state, T inElement) throws SQLException {
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
    }

    @Override
    public <T extends LocationCore> boolean createLocation(T inLocation) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (countLocations(inLocation.getName()) > 0) {
                System.err.println("Trying to save an Location using a name that already exists.... (" + inLocation.getName() + ")");
                return false;
            }
            // Insert
            state = conn.prepareStatement(createLocation);
            maintainLocation(state, inLocation);
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
    public <T extends LocationCore> boolean updateLocation(T inLocation, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                if (countLocations(inLocation.getName()) > 0) {
                    System.err.println("Trying to save an Location using a name that already exists.... (" + inLocation.getName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Check whether there was a name change or not.
            if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                // Update the Sightings
                List<SightingCore> lstSightings = listSightings(0, null, inOldName, null, false, SightingCore.class);
                for (SightingCore sighting : lstSightings) {
                    sighting.setLocationName(inLocation.getName());
                    updateSighting(sighting);
                }
                // Update the Visits
                List<VisitCore> lstVisits = listVisits(null, inOldName, null, VisitCore.class);
                for (VisitCore visit : lstVisits) {
                    visit.setLocationName(inLocation.getName());
                    updateVisit(visit, visit.getName());
                }
                // Update the Files
                List<WildLogFileCore> lstWildLogFiles = listWildLogFiles(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName), null, WildLogFileCore.class);
                for (WildLogFileCore wildLogFile : lstWildLogFiles) {
                    wildLogFile.setId(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getName()), 150));
                    updateWildLogFile(wildLogFile);
                }
            }
            // Update
            state = conn.prepareStatement(updateLocation);
            maintainLocation(state, inLocation);
            state.setString(21, UtilsData.sanitizeString(inOldName));
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

    private <T extends LocationCore> void maintainLocation(PreparedStatement state, T inLocation) throws SQLException {
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
    }

    @Override
    public <T extends VisitCore> boolean createVisit(T inVisit) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (countVisits(inVisit.getName(), null) > 0) {
                System.err.println("Trying to save an Visit using a name that already exists.... (" + inVisit.getName() + ")");
                return false;
            }
            // Insert
            state = conn.prepareStatement(createVisit);
            maintainVisit(state, inVisit);
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
    public <T extends VisitCore> boolean updateVisit(T inVisit, String inOldName) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                if (countVisits(inVisit.getName(), null) > 0) {
                    System.err.println("Trying to save an Visit using a name that already exists.... (" + inVisit.getName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Update the related tables if the name changes
            VisitCore originalVisit;
            if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                originalVisit = findVisit(inOldName, inVisit.getClass());
                // Update the Sightings
                List<SightingCore> lstSightings = listSightings(0, null, null, inOldName, false, SightingCore.class);
                for (SightingCore sighting : lstSightings) {
                    sighting.setVisitName(inVisit.getName());
                    updateSighting(sighting);
                }
                // Update the Files
                List<WildLogFileCore> lstWildLogFiles = listWildLogFiles(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inOldName), null, WildLogFileCore.class);
                for (WildLogFileCore wildLogFile : lstWildLogFiles) {
                    wildLogFile.setId(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getName()), 150));
                    updateWildLogFile(wildLogFile);
                }
            }
            else {
                originalVisit = inVisit;
            }
            // This method should cascade and save the Sightings records when the Visit's LocationName changed (Visit was moved).
            if (!originalVisit.getLocationName().equals(inVisit.getLocationName())) {
                // Update the Sightings
                List<SightingCore> lstSightings = listSightings(0, null, null, inOldName, false, SightingCore.class);
                for (SightingCore sighting : lstSightings) {
                    sighting.setLocationName(inVisit.getLocationName());
                    updateSighting(sighting);
                }
            }
            // Update
            state = conn.prepareStatement(updateVisit);
            maintainVisit(state, inVisit);
            state.setString(8, UtilsData.sanitizeString(inOldName));
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

    private <T extends VisitCore> void maintainVisit(PreparedStatement state, T inVisit) throws SQLException {
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
    }

    @Override
    public <T extends SightingCore> boolean createSighting(T inSighting, boolean inNewButKeepID) {
        PreparedStatement state = null;
        PreparedStatement tempState = null;
        ResultSet results = null;
        try {
            if (!inNewButKeepID) {
                // Get the new ID
                tempState = conn.prepareStatement("SELECT COUNT(SIGHTINGCOUNTER) FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?");
                inSighting.setSightingCounter(generateID());
                tempState.setLong(1, inSighting.getSightingCounter());
                // Make sure it is unique (should almost always be, but let's be safe...)
                results = tempState.executeQuery();
                while (results.next() && results.getInt(1) > 0) {
                    // ID already used, try a new ID
                    inSighting.setSightingCounter(generateID());
                    tempState.setLong(1, inSighting.getSightingCounter());
                    results = tempState.executeQuery();
                }
            }
            // Insert
            state = conn.prepareStatement(createSighting);
            maintainSighting(state, inSighting);
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
    public <T extends SightingCore> boolean updateSighting(T inSighting) {
        PreparedStatement state = null;
        try {
            // Note: No need to update the files, because once a sighting has an ID it never changes
            // Update
            state = conn.prepareStatement(updateSighting);
            maintainSighting(state, inSighting);
            state.setLong(33, inSighting.getSightingCounter());
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

    private <T extends SightingCore> void maintainSighting(PreparedStatement state, T inSighting) throws SQLException {
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
    }

    @Override
    public <T extends WildLogFileCore> boolean createWildLogFile(T inWildLogFile) {
        PreparedStatement state = null;
        try {
            // Insert
            state = conn.prepareStatement(createFile);
            maintainWildLogFile(state, inWildLogFile);
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
    public <T extends WildLogFileCore> boolean updateWildLogFile(T inWildLogFile) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateFile);
            maintainWildLogFile(state, inWildLogFile);
            state.setString(9, UtilsData.sanitizeString(inWildLogFile.getDBFilePath()));
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

    private <T extends WildLogFileCore> void maintainWildLogFile(PreparedStatement state, T inWildLogFile) throws SQLException {
        state.setString(1, UtilsData.sanitizeString(inWildLogFile.getId()));
        state.setString(2, UtilsData.sanitizeString(inWildLogFile.getFilename()));
        state.setString(3, UtilsData.sanitizeString(inWildLogFile.getDBFilePath().replace("\\", "/")));
        state.setString(4, UtilsData.stringFromObject(inWildLogFile.getFileType()));
        if (inWildLogFile.getUploadDate() != null) {
            state.setDate(5, new java.sql.Date(inWildLogFile.getUploadDate().getTime()));
        }
        else {
            state.setDate(5, null);
        }
        state.setBoolean(6, inWildLogFile.isDefaultFile());
        if (inWildLogFile.getFileDate() != null) {
            state.setTimestamp(7, new Timestamp(inWildLogFile.getFileDate().getTime()));
        }
        else {
            state.setTimestamp(7, null);
        }
        state.setLong(8, inWildLogFile.getFileSize());
    }

    @Override
    public <T extends WildLogOptions> boolean createWildLogOptions(T inWildLogOptions) {
        PreparedStatement state = null;
        try {
            // Insert
            state = conn.prepareStatement(createWildLogOptions);
            // Use default values (except for the WorkspaceID)
            state.setLong(1, generateID());
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
    public <T extends WildLogOptions> boolean updateWildLogOptions(T inWildLogOptions) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateWildLogOptions);
            state.setDouble(1, inWildLogOptions.getDefaultLatitude());
            state.setDouble(2, inWildLogOptions.getDefaultLongitude());
            state.setFloat(3, inWildLogOptions.getDefaultSlideshowSpeed());
            state.setInt(4, inWildLogOptions.getDefaultSlideshowSize());
            state.setBoolean(5, inWildLogOptions.isUseThumbnailTables());
            state.setBoolean(6, inWildLogOptions.isUseThumnailBrowsing());
            state.setBoolean(7, inWildLogOptions.isEnableSounds());
            state.setBoolean(8, inWildLogOptions.isUseScientificNames());
            state.setString(9, inWildLogOptions.getWorkspaceName());
            state.setLong(10, inWildLogOptions.getWorkspaceID());
            state.setBoolean(11, inWildLogOptions.isUploadLogs());
            state.setBoolean(12, inWildLogOptions.isBundledPlayers());
            state.setBoolean(13, inWildLogOptions.isUseIndividualsInSightingPath());
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
    public <T extends AdhocData> boolean createAdhocData(T inAdhocData) {
        PreparedStatement state = null;
        try {
            //Insert
            state = conn.prepareStatement(createAdhocData);
            // Populate the values
            maintainAdhocData(state, inAdhocData);
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
    public <T extends AdhocData> boolean updateAdhocData(T inAdhocData) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateAdhocData);
            // Populate the values
            maintainAdhocData(state, inAdhocData);
            state.setString(4, inAdhocData.getFieldID());
            state.setString(5, inAdhocData.getDataKey());
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

    private <T extends AdhocData> void maintainAdhocData(PreparedStatement state, T inAdhocData) throws SQLException {
        state.setString(1, inAdhocData.getFieldID());
        state.setString(2, inAdhocData.getDataKey());
        state.setString(3, inAdhocData.getDataValue());
    }

    @Override
    public boolean deleteElement(String inPrimaryName) {
        PreparedStatement state = null;
        try {
            // Delete the ElementCore
            state = conn.prepareStatement(deleteElement);
            state.setString(1, UtilsData.sanitizeString(inPrimaryName));
            state.executeUpdate();
            // Delete all Sightings for this ElementCore
            List<SightingCore> sightingList = listSightings(0, inPrimaryName, null, null, false, SightingCore.class);
            for (SightingCore temp : sightingList) {
                deleteSighting(temp.getSightingCounter());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(ElementCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inPrimaryName), null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getDBFilePath());
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
    public boolean deleteLocation(String inName) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteLocation);
            state.setString(1, UtilsData.sanitizeString(inName));
            // Delete LocationCore
            state.executeUpdate();
            state.close();
            // Delete Visits for this LocationCore
            List<VisitCore> visitList = listVisits(null, inName, null, VisitCore.class);
            for (VisitCore temp : visitList) {
                deleteVisit(temp.getName());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(LocationCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inName), null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getDBFilePath());
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
    public boolean deleteVisit(String inName) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteVisit);
            state.setString(1, UtilsData.sanitizeString(inName));
            // Delete VisitCore
            state.executeUpdate();
            // Delete Sightings for this VisitCore
            List<SightingCore> sightingList = listSightings(0, null, null, inName, false, SightingCore.class);
            for (SightingCore temp : sightingList) {
                deleteSighting(temp.getSightingCounter());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(VisitCore.WILDLOGFILE_ID_PREFIX + UtilsData.sanitizeString(inName), null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getDBFilePath());
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
    public boolean deleteSighting(long inSightingCounter) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteSighting);
            state.setLong(1, inSightingCounter);
            // Delete Sightings
            state.executeUpdate();
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(SightingCore.WILDLOGFILE_ID_PREFIX + inSightingCounter, null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getDBFilePath());
            }
            // Delete any linked iNaturalist data
            deleteINaturalistLinkedData(inSightingCounter, 0);
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
    public boolean deleteWildLogFile(String inDBFilePath) {
        // Note: This method only deletes one file at a time from the database.
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteFile);
            state.setString(1, UtilsData.sanitizeString(inDBFilePath.replace("\\", "/")));
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
    
    @Override
    public boolean deleteAdhocData(String inFieldID, String inDataKey) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteAdhocData);
            state.setString(1, UtilsData.sanitizeString(inFieldID));
            state.setString(2, UtilsData.sanitizeString(inDataKey));
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
    public <S extends SightingCore, L extends LocationCore, V extends VisitCore, E extends ElementCore> 
        List<S> searchSightings(Date inStartDate, Date inEndDate, 
            List<L> inActiveLocations, List<V> inActiveVisits, List<E> inActiveElements, 
            boolean inIncludeCachedValues, Class<S> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<S> tempList = new ArrayList<S>();
        try {
            String sql = "SELECT SIGHTINGS.*";
            if (inIncludeCachedValues) {
                sql = sql + ", ELEMENTS.ELEMENTTYPE, VISITS.VISITTYPE, "
                          + " (SELECT COUNT(*) FROM INATURALIST INAT WHERE INAT.WILDLOGID = SIGHTINGS.SIGHTINGCOUNTER) INATCOUNT"
                          + " FROM SIGHTINGS"
                          + " LEFT JOIN ELEMENTS ON ELEMENTS.PRIMARYNAME = SIGHTINGS.ELEMENTNAME" 
                          + " LEFT JOIN VISITS ON VISITS.NAME = SIGHTINGS.VISITNAME";
            }
            else {
                sql = sql + " FROM SIGHTINGS";
            }
            // Build SQL
            String andKeyword = " WHERE";
            if (inStartDate != null) {
                sql = sql + andKeyword + " SIGHTINGDATE >= ?";
                andKeyword = " AND";
            }
            if (inEndDate != null) {
                sql = sql + andKeyword + " SIGHTINGDATE <= ?";
                andKeyword = " AND";
            }
            if (inActiveLocations != null && inActiveLocations.size() > 0) {
                sql = sql + andKeyword + " SIGHTINGS.LOCATIONNAME IN (";
                for (int t = 0; t < inActiveLocations.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            if (inActiveVisits != null && inActiveVisits.size() > 0) {
                sql = sql + andKeyword + " VISITNAME IN (";
                for (int t = 0; t < inActiveVisits.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            if (inActiveElements != null && inActiveElements.size() > 0) {
                sql = sql + andKeyword + " ELEMENTNAME IN (";
                for (int t = 0; t < inActiveElements.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            state = conn.prepareStatement(sql);
            // Add parameters
            int paramCounter = 1;
            if (inStartDate != null) {
                state.setTimestamp(paramCounter++, new Timestamp(inStartDate.getTime()));
            }
            if (inEndDate != null) {
                state.setTimestamp(paramCounter++, new Timestamp(inEndDate.getTime()));
            }
            if (inActiveLocations != null && inActiveLocations.size() > 0) {
                for (LocationCore activeLocation : inActiveLocations) {
                    state.setString(paramCounter++, activeLocation.getName());
                }
            }
            if (inActiveVisits != null && inActiveVisits.size() > 0) {
                for (VisitCore activeVisit : inActiveVisits) {
                    state.setString(paramCounter++, activeVisit.getName());
                }
            }
            if (inActiveElements != null && inActiveElements.size() > 0) {
                for (ElementCore activeElement : inActiveElements) {
                    state.setString(paramCounter++, activeElement.getPrimaryName());
                }
            }
            // Execute SQL
            results = state.executeQuery();
            // Load results
            while (results.next()) {
                S tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting);
                if (inIncludeCachedValues) {
                    tempSighting.setCachedElementType(ElementType.getEnumFromText(results.getString("ELEMENTTYPE")));
                    tempSighting.setCachedVisitType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                    if (results.getInt("INATCOUNT") == 0) {
                        tempSighting.setCachedLinkedToINaturalist(false);
                    }
                    else {
                        tempSighting.setCachedLinkedToINaturalist(true);
                    }
                }
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
    public <T extends LocationCount> List<T> queryLocationCountForElement(String inElementPrimaryName, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = queryLocationCountForElement;
            state = conn.prepareStatement(sql);
            if (inElementPrimaryName != null) {
                state.setString(1, inElementPrimaryName);
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
    
    @Override
    public long generateID() {
        return System.currentTimeMillis()*1000000L + randomGenerator.nextInt(999999);
    }

    @Override
    public <T extends INaturalistLinkedData> T findINaturalistLinkedData(long inWildLogID, long inINaturalistID, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T temp = null;
        try {
            state = conn.prepareStatement(findINaturalistLinkedData);
            state.setLong(1, inWildLogID);
            state.setLong(2, inINaturalistID);
            results = state.executeQuery();
            if (results.next()) {
                temp = inReturnType.newInstance();
                temp.setWildlogID(results.getLong("WILDLOGID"));
                temp.setINaturalistID(results.getLong("INATURALISTID"));
                temp.setINaturalistData(results.getString("INATURALISTDATA"));
            }
            if (results.next()) {
                temp = null;
                throw new Exception("More than one iNaturalist database records matched the parameters: "
                        + "WildLogID = " + inWildLogID + " | iNaturalistID = " + inINaturalistID);
            }
        }
        catch (SQLException ex) {
            System.err.println("More than one iNaturalist database records matched the parameters: "
                    + "WildLogID = " + inWildLogID + " | iNaturalistID = " + inINaturalistID);
            printSQLException(ex);
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return temp;
    }
    
    @Override
    public <T extends INaturalistLinkedData> boolean createINaturalistLinkedData(T inINaturalistLinkedData) {
        PreparedStatement state = null;
        try {
            //Insert
            state = conn.prepareStatement(createINaturalistLinkedData);
            // Populate the values
            maintainINaturalistLinkedData(state, inINaturalistLinkedData);
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            System.err.println("More than one iNaturalist database records matched the parameters: "
                    + "WildLogID = " + inINaturalistLinkedData.getWildlogID() + " | iNaturalistID = " + inINaturalistLinkedData.getINaturalistID());
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

    @Override
    public <T extends INaturalistLinkedData> boolean updateINaturalistLinkedData(T inINaturalistLinkedData) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateINaturalistLinkedData);
            // Populate the values
            maintainINaturalistLinkedData(state, inINaturalistLinkedData);
            state.setLong(4, inINaturalistLinkedData.getWildlogID());
            state.setLong(5, inINaturalistLinkedData.getINaturalistID());
            // Execute
            state.executeUpdate();
        }
        catch (SQLException ex) {
            System.err.println("More than one iNaturalist database records matched the parameters: "
                    + "WildLogID = " + inINaturalistLinkedData.getWildlogID() + " | iNaturalistID = " + inINaturalistLinkedData.getINaturalistID());
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }
    
    private <T extends INaturalistLinkedData> void maintainINaturalistLinkedData(PreparedStatement state, T inINaturalistLinkedData) throws SQLException {
        state.setLong(1, inINaturalistLinkedData.getWildlogID());
        state.setLong(2, inINaturalistLinkedData.getINaturalistID());
        state.setString(3, inINaturalistLinkedData.getINaturalistData());
    }

    @Override
    public boolean deleteINaturalistLinkedData(long inWildLogID, long inINaturalistID) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteINaturalistLinkedData);
            state.setLong(1, inWildLogID);
            state.setLong(2, inINaturalistID);
            state.executeUpdate();
        }
        catch (SQLException ex) {
            System.err.println("More than one iNaturalist database records matched the parameters: "
                    + "WildLogID = " + inWildLogID + " | iNaturalistID = " + inINaturalistID);
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatement(state);
        }
        return true;
    }

}
