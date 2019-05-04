package wildlog.data.dbi;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.AdhocData;
import wildlog.data.dataobjects.ElementCore;
import wildlog.data.dataobjects.INaturalistLinkedData;
import wildlog.data.dataobjects.LocationCore;
import wildlog.data.dataobjects.SightingCore;
import wildlog.data.dataobjects.VisitCore;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
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
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WildLogFileLinkType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.data.utils.UtilsData;


public abstract class DBI_JDBC implements DBI {
    protected SecureRandom randomGenerator;
    // Version
    protected static final int WILDLOG_DB_VERSION = 12;
    // Tables
    protected static final String tableElements = "CREATE TABLE ELEMENTS ("
            + "ID bigint PRIMARY KEY NOT NULL, "
            + "PRIMARYNAME varchar(150) NOT NULL, "
            + "OTHERNAME varchar(150), "
            + "SCIENTIFICNAME varchar(150), "
            + "DESCRIPTION longvarchar, "
            + "DISTRIBUTION longvarchar, "
            + "NUTRITION longvarchar, "
            + "DIAGNOSTICDESCRIPTION longvarchar, "
            + "ENDANGEREDSTATUS varchar(35), "
            + "BEHAVIOURDESCRIPTION longvarchar, "
            + "ELEMENTTYPE varchar(50), "
            + "FEEDINGCLASS varchar(50), "
            + "REFERENCEID varchar(50), "
            + "AUDITTIME bigint NOT NULL, "
            + "AUDITUSER varchar(150) NOT NULL)";
    protected static final String tableLocations = "CREATE TABLE LOCATIONS ("
            + "ID bigint PRIMARY KEY NOT NULL, "
            + "NAME varchar(150) NOT NULL, "
            + "DESCRIPTION longvarchar, "
            + "RATING varchar(50), "
            + "GAMEVIEWINGRATING varchar(50), "
            + "HABITATTYPE longvarchar, "
            + "LATITUDEINDICATOR varchar(10), "
            + "LATDEGREES int, "
            + "LATMINUTES int, "
            + "LATSECONDS double, "
            + "LONGITUDEINDICATOR varchar(10), "
            + "LONDEGREES int, "
            + "LONMINUTES int, "
            + "LONSECONDS double, "
            + "GPSACCURACY varchar(50), "
            + "GPSACCURACYVALUE double, "
            + "AUDITTIME bigint NOT NULL, "
            + "AUDITUSER varchar(150) NOT NULL)";
    protected static final String tableVisits = "CREATE TABLE VISITS ("
            + "ID bigint PRIMARY KEY NOT NULL, "
            + "NAME varchar(150) NOT NULL, "
            + "STARTDATE date, "
            + "ENDDATE date, "
            + "DESCRIPTION longvarchar, "
            + "GAMEWATCHINGINTENSITY varchar(50), "
            + "VISITTYPE varchar(50), "
            + "LOCATIONID bigint NOT NULL, "
            + "AUDITTIME bigint NOT NULL, "
            + "AUDITUSER varchar(150) NOT NULL)";
    protected static final String tableSightings = "CREATE TABLE SIGHTINGS ("
            + "ID bigint PRIMARY KEY NOT NULL, "
            + "SIGHTINGDATE timestamp NOT NULL, "
            + "ELEMENTID varchar(150) NOT NULL, "
            + "LOCATIONID varchar(150) NOT NULL, "
            + "VISITID varchar(150) NOT NULL, "
            + "TIMEOFDAY varchar(50), "
            + "WEATHER varchar(50), "
            + "VIEWRATING varchar(50), "
            + "CERTAINTY varchar(50), "
            + "NUMBEROFELEMENTS int, "
            + "DETAILS longvarchar, "
            + "LATITUDEINDICATOR varchar(10), "
            + "LATDEGREES int, "
            + "LATMINUTES int, "
            + "LATSECONDS double, "
            + "LONGITUDEINDICATOR varchar(10), "
            + "LONDEGREES int, "
            + "LONMINUTES int, "
            + "LONSECONDS double, "
            + "SIGHTINGEVIDENCE varchar(50), "
            + "MOONLIGHT varchar(50), "
            + "MOONPHASE int, "
            + "TEMPERATURE double, "
            + "TEMPERATUREUNIT varchar(15), "
            + "LIFESTATUS varchar(15), "
            + "SEX varchar(15), "
            + "TAG longvarchar, "
            + "DURATIONMINUTES int, "
            + "DURATIONSECONDS double, "
            + "GPSACCURACY varchar(50), "
            + "TIMEACCURACY varchar(50), "
            + "AGE varchar(50), "
            + "GPSACCURACYVALUE double, "
            + "AUDITTIME bigint NOT NULL, "
            + "AUDITUSER varchar(150) NOT NULL)";
    protected static final String tableFiles = "CREATE TABLE FILES ("
            + "ID bigint PRIMARY KEY NOT NULL, "
            + "LINKID bigint NOT NULL, "
            + "LINKTYPE varchar(1) NOT NULL, "
            + "FILENAME varchar(255), "
            + "ORIGINALPATH varchar(500) NOT NULL, "
            + "FILETYPE varchar(50), "
            + "UPLOADDATE date, "
            + "ISDEFAULT smallint, "
            + "FILEDATE timestamp, "
            + "FILESIZE bigint, "
            + "AUDITTIME bigint NOT NULL, "
            + "AUDITUSER varchar(150) NOT NULL)";
    protected static final String tableWildLogOptions = "CREATE TABLE WILDLOG ("
            + "VERSION int DEFAULT " + WILDLOG_DB_VERSION + ", "
            + "DEFAULTLATITUDE double DEFAULT -28.2, "
            + "DEFAULTLONGITUDE double DEFAULT 24.7, "
            + "DEFAULTZOOM double DEFAULT 20.0, "
            + "DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5, "
            + "DEFAULTSLIDESHOWSIZE int DEFAULT 750, "
            + "USETHUMBNAILTABLES smallint DEFAULT true, "
            + "USETHUMBNAILBROWSE smallint DEFAULT false, "
            + "ENABLESOUNDS smallint DEFAULT true, "
            + "USESCIENTIFICNAMES smallint DEFAULT true, "
            + "WORKSPACENAME varchar(50) DEFAULT 'WildLog Workspace', "
            + "WORKSPACEID bigint DEFAULT 0, "
            + "UPLOADLOGS smallint DEFAULT true, "
            + "BUNDLEDPLAYERS smallint DEFAULT true, "
            + "USEINDVCOUNTINPATH smallint DEFAULT false)";
    protected static final String tableAdhocData = "CREATE TABLE ADHOC ("
            + "FIELDID varchar(150) NOT NULL, "
            + "DATAKEY varchar(150) NOT NULL, "
            + "DATAVALUE TEXT)";
    protected static final String tableINaturalistLinkedData = "CREATE TABLE INATURALIST ("
            + "WILDLOGID bigint PRIMARY KEY NOT NULL, "
            + "INATURALISTID bigint NOT NULL, "
            + "INATURALISTDATA TEXT)";
    protected static final String tableUsers = "CREATE TABLE WILDLOGUSERS ("
            + "USERNAME varchar(150) PRIMARY KEY NOT NULL, "
            + "PASSWORD varchar(512) NOT NULL, "
            + "TYPE varchar(50) NOT NULL)";
    // Count
    protected static final String countLocation = "SELECT count(*) FROM LOCATIONS";
    protected static final String countVisit = "SELECT count(*) FROM VISITS";
    protected static final String countSighting = "SELECT count(*) FROM SIGHTINGS";
    protected static final String countElement = "SELECT count(*) FROM ELEMENTS";
    protected static final String countFile = "SELECT count(*) FROM FILES";
    protected static final String countUsers = "SELECT count(*) FROM WILDLOGUSERS";
    // Find
    protected static final String findLocation = "SELECT * FROM LOCATIONS";
    protected static final String findVisit = "SELECT * FROM VISITS";
    protected static final String findVisitWithCached = "SELECT VISITS.*, LOCATIONS.NAME LOCATIONNAME"
            + " FROM VISITS"
            + " LEFT JOIN LOCATIONS ON LOCATIONID = LOCATIONS.ID";
    protected static final String findSighting = "SELECT * FROM SIGHTINGS";
    protected static final String findSightingWithCached = "SELECT SIGHTINGS.*, ELEMENTS.PRIMARYNAME ELEMENTNAME, LOCATIONS.NAME LOCATIONNAME, VISITS.NAME VISITNAME,"
            + " ELEMENTS.ELEMENTTYPE, VISITS.VISITTYPE,"
            + " (SELECT COUNT(*) FROM INATURALIST INAT WHERE INAT.WILDLOGID = SIGHTINGS.ID) INATCOUNT"
            + " FROM SIGHTINGS"
            + " LEFT JOIN ELEMENTS ON ELEMENTID = ELEMENTS.ID"
            + " LEFT JOIN LOCATIONS ON LOCATIONID = LOCATIONS.ID"
            + " LEFT JOIN VISITS ON VISITID = VISITS.ID";
    protected static final String findElement = "SELECT * FROM ELEMENTS";
    protected static final String findFile = "SELECT * FROM FILES";
    protected static final String findWildLogOptions = "SELECT * FROM WILDLOG";
    protected static final String findAdhocData = "SELECT * FROM ADHOC "
            + "WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String findINaturalistLinkedData = "SELECT * FROM INATURALIST "
            + "WHERE WILDLOGID = ? OR INATURALISTID = ?";
    protected static final String findUser = "SELECT * FROM WILDLOGUSERS "
            + "WHERE USERNAME = ?";
    // List
    protected static final String listLocation = "SELECT * FROM LOCATIONS";
    protected static final String listVisit = "SELECT * FROM VISITS";
    protected static final String listVisitsWithCached = "SELECT VISITS.*, LOCATIONS.NAME LOCATIONNAME"
            + " FROM VISITS"
            + " LEFT JOIN LOCATIONS ON LOCATIONID = LOCATIONS.ID";
    protected static final String listElement = "SELECT * FROM ELEMENTS";
    protected static final String listFile = "SELECT * FROM FILES";
    protected static final String listAdhocData = "SELECT * FROM ADHOC";
    protected static final String listINaturalistLinkedData = "SELECT * FROM INATURALIST";
    protected static final String listUsers = "SELECT * FROM WILDLOGUSERS";
    // Create
    protected static final String createLocation = "INSERT INTO LOCATIONS ("
            + "ID, "
            + "NAME, "
            + "DESCRIPTION, "
            + "RATING, "
            + "GAMEVIEWINGRATING, "
            + "HABITATTYPE, "
            + "LATITUDEINDICATOR, "
            + "LATDEGREES, "
            + "LATMINUTES, "
            + "LATSECONDS, "
            + "LONGITUDEINDICATOR, "
            + "LONDEGREES, "
            + "LONMINUTES, "
            + "LONSECONDS, "
            + "GPSACCURACY, "
            + "GPSACCURACYVALUE,"
            + "AUDITTIME, "
            + "AUDITUSER) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createVisit = "INSERT INTO VISITS ("
            + "ID, "
            + "NAME, "
            + "STARTDATE, "
            + "ENDDATE, "
            + "DESCRIPTION, "
            + "GAMEWATCHINGINTENSITY, "
            + "VISITTYPE, "
            + "LOCATIONID, "
            + "AUDITTIME, "
            + "AUDITUSER) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?)";
    protected static final String createSighting = "INSERT INTO SIGHTINGS ("
            + "ID, "
            + "SIGHTINGDATE, "
            + "ELEMENTID, "
            + "LOCATIONID, "
            + "VISITID, "
            + "TIMEOFDAY, "
            + "WEATHER, "
            + "VIEWRATING, "
            + "CERTAINTY, "
            + "NUMBEROFELEMENTS, "
            + "DETAILS, "
            + "LATITUDEINDICATOR, "
            + "LATDEGREES, "
            + "LATMINUTES, "
            + "LATSECONDS, "
            + "LONGITUDEINDICATOR, "
            + "LONDEGREES, "
            + "LONMINUTES, "
            + "LONSECONDS, "
            + "SIGHTINGEVIDENCE, "
            + "MOONPHASE, "
            + "MOONLIGHT, "
            + "TEMPERATURE, "
            + "TEMPERATUREUNIT, "
            + "LIFESTATUS, "
            + "SEX, "
            + "TAG, "
            + "DURATIONMINUTES, "
            + "DURATIONSECONDS, "
            + "GPSACCURACY, "
            + "GPSACCURACYVALUE, "
            + "TIMEACCURACY, "
            + "AGE, "
            + "AUDITTIME, "
            + "AUDITUSER) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createElement = "INSERT INTO ELEMENTS ("
            + "ID, "
            + "PRIMARYNAME, "
            + "OTHERNAME, "
            + "SCIENTIFICNAME, "
            + "DESCRIPTION, "
            + "DISTRIBUTION, "
            + "NUTRITION, "
            + "DIAGNOSTICDESCRIPTION, "
            + "ENDANGEREDSTATUS, "
            + "BEHAVIOURDESCRIPTION, "
            + "ELEMENTTYPE, "
            + "FEEDINGCLASS, "
            + "REFERENCEID, "
            + "AUDITTIME, "
            + "AUDITUSER) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createFile = "INSERT INTO FILES ("
            + "ID, "
            + "LINKID, "
            + "LINKTYPE, "
            + "FILENAME, "
            + "ORIGINALPATH, "
            + "FILETYPE, "
            + "UPLOADDATE, "
            + "ISDEFAULT, "
            + "FILEDATE, "
            + "FILESIZE, "
            + "AUDITTIME, "
            + "AUDITUSER) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String createWildLogOptions = "INSERT INTO WILDLOG VALUES ("
            + "DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, ?, DEFAULT, DEFAULT, DEFAULT)";
    protected static final String createAdhocData = "INSERT INTO ADHOC ("
            + "FIELDID, "
            + "DATAKEY, "
            + "DATAVALUE) "
            + "VALUES (?, ?, ?)";
    protected static final String createINaturalistLinkedData = "INSERT INTO INATURALIST ("
            + "WILDLOGID, "
            + "INATURALISTID, "
            + "INATURALISTDATA) "
            + "VALUES (?, ?, ?)";
    protected static final String createUser = "INSERT INTO WILDLOGUSERS ("
            + "USERNAME, "
            + "PASSWORD, "
            + "TYPE) "
            + "VALUES (?, ?, ?)";
    // Update
    protected static final String updateLocation = "UPDATE LOCATIONS SET "
            + "ID = ?, "
            + "NAME = ?, "
            + "DESCRIPTION = ?, "
            + "RATING = ?, "
            + "GAMEVIEWINGRATING = ?, "
            + "HABITATTYPE = ?, "
            + "LATITUDEINDICATOR = ?, "
            + "LATDEGREES = ?, "
            + "LATMINUTES = ?, "
            + "LATSECONDS = ?, "
            + "LONGITUDEINDICATOR = ?, "
            + "LONDEGREES = ?, "
            + "LONMINUTES = ?, "
            + "LONSECONDS = ?, "
            + "GPSACCURACY = ?, "
            + "GPSACCURACYVALUE = ?, "
            + "AUDITTIME = ?, "
            + "AUDITUSER = ? "
            + "WHERE ID = ?";
    protected static final String updateVisit = "UPDATE VISITS SET "
            + "ID = ?, "
            + "NAME = ?, "
            + "STARTDATE = ?, "
            + "ENDDATE = ?, "
            + "DESCRIPTION = ?, "
            + "GAMEWATCHINGINTENSITY = ?, "
            + "VISITTYPE = ?, "
            + "LOCATIONID = ?, "
            + "AUDITTIME = ?, "
            + "AUDITUSER = ? "
            + "WHERE ID = ?";
    protected static final String updateSighting = "UPDATE SIGHTINGS SET "
            + "ID = ?, "
            + "SIGHTINGDATE = ?, "
            + "ELEMENTID = ?, "
            + "LOCATIONID = ?, "
            + "VISITID = ?, "
            + "TIMEOFDAY = ?, "
            + "WEATHER = ?, "
            + "VIEWRATING = ?, "
            + "CERTAINTY = ?, "
            + "NUMBEROFELEMENTS = ?, "
            + "DETAILS = ?, "
            + "LATITUDEINDICATOR = ?, "
            + "LATDEGREES = ?, "
            + "LATMINUTES = ?, "
            + "LATSECONDS = ?, "
            + "LONGITUDEINDICATOR = ?, "
            + "LONDEGREES = ?, "
            + "LONMINUTES = ?, "
            + "LONSECONDS = ?, "
            + "SIGHTINGEVIDENCE = ?, "
            + "MOONPHASE = ?, "
            + "MOONLIGHT = ?, "
            + "TEMPERATURE = ?, "
            + "TEMPERATUREUNIT = ?, "
            + "LIFESTATUS = ?, "
            + "SEX = ?, "
            + "TAG = ?, "
            + "DURATIONMINUTES = ?, "
            + "DURATIONSECONDS = ?, "
            + "GPSACCURACY = ?, "
            + "GPSACCURACYVALUE = ?, "
            + "TIMEACCURACY = ?, "
            + "AGE = ?, "
            + "AUDITTIME = ?, "
            + "AUDITUSER = ?"
            + "WHERE ID = ?";
    protected static final String updateElement = "UPDATE ELEMENTS SET "
            + "ID = ?, "
            + "PRIMARYNAME = ?, "
            + "OTHERNAME = ?, "
            + "SCIENTIFICNAME = ?, "
            + "DESCRIPTION = ?, "
            + "DISTRIBUTION = ?, "
            + "NUTRITION = ?, "
            + "DIAGNOSTICDESCRIPTION = ?, "
            + "ENDANGEREDSTATUS = ?, "
            + "BEHAVIOURDESCRIPTION = ?, "
            + "ELEMENTTYPE = ?, "
            + "FEEDINGCLASS = ?, "
            + "REFERENCEID = ?, "
            + "AUDITTIME = ?, "
            + "AUDITUSER = ? "
            + "WHERE ID = ?";
    protected static final String updateFile = "UPDATE FILES SET "
            + "ID = ?, "
            + "LINKID = ?, "
            + "LINKTYPE = ?, "
            + "FILENAME = ?, "
            + "ORIGINALPATH = ?, "
            + "FILETYPE = ?, "
            + "UPLOADDATE = ?, "
            + "ISDEFAULT = ?, "
            + "FILEDATE = ?, "
            + "FILESIZE = ?, "
            + "AUDITTIME = ?, "
            + "AUDITUSER = ? "
            + "WHERE ID = ?";
    protected static final String updateWildLogOptions = "UPDATE WILDLOG SET "
            + "DEFAULTLATITUDE = ?, "
            + "DEFAULTLONGITUDE = ?, "
            + "DEFAULTZOOM = ?, "
            + "DEFAULTSLIDESHOWSPEED = ?, "
            + "DEFAULTSLIDESHOWSIZE = ?, "
            + "USETHUMBNAILTABLES = ?, "
            + "USETHUMBNAILBROWSE =?, "
            + "ENABLESOUNDS = ?, "
            + "USESCIENTIFICNAMES = ?, "
            + "WORKSPACENAME = ?, "
            + "WORKSPACEID = ?, "
            + "UPLOADLOGS = ?, "
            + "BUNDLEDPLAYERS = ?, "
            + "USEINDVCOUNTINPATH = ?";
    protected static final String updateAdhocData = "UPDATE ADHOC SET "
            + "FIELDID = ?, "
            + "DATAKEY = ?, "
            + "DATAVALUE = ? "
            + "WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String updateINaturalistLinkedData = "UPDATE INATURALIST SET "
            + "WILDLOGID = ?, "
            + "INATURALISTID = ?, "
            + "INATURALISTDATA = ? "
            + "WHERE WILDLOGID = ? OR INATURALISTID = ?";
    protected static final String updateUser = "UPDATE WILDLOGUSERS SET "
            + "USERNAME = ?, "
            + "PASSWORD = ?, "
            + "TYPE = ? "
            + "WHERE USERNAME = ?";
    // Delete
    protected static final String deleteLocation = "DELETE FROM LOCATIONS "
            + "WHERE ID = ?";
    protected static final String deleteVisit = "DELETE FROM VISITS "
            + "WHERE ID = ?";
    protected static final String deleteSighting = "DELETE FROM SIGHTINGS "
            + "WHERE ID = ?";
    protected static final String deleteElement = "DELETE FROM ELEMENTS "
            + "WHERE ID = ?";
    protected static final String deleteFile = "DELETE FROM FILES "
            + "WHERE ID = ?";
    protected static final String deleteAdhocData = "DELETE FROM ADHOC "
            + "WHERE FIELDID = ? AND DATAKEY = ?";
    protected static final String deleteINaturalistLinkedData = "DELETE "
            + "FROM INATURALIST WHERE WILDLOGID = ? OR INATURALISTID = ?";
    protected static final String deleteUser = "DELETE FROM WILDLOGUSERS "
            + "WHERE USERNAME = ?";
    // Queries
    protected static final String queryLocationCountForElement = "select SIGHTINGS.LOCATIONID, count(*) cnt "
            + "from SIGHTINGS where SIGHTINGS.ELEMENTID = ? group by SIGHTINGS.LOCATIONID order by cnt desc";
    // Monitor
    protected static final String activeSessionsCount = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SESSIONS";
    // Variables
    protected Connection conn;

    public DBI_JDBC() {
        try {
            randomGenerator = SecureRandom.getInstance("SHA1PRNG"); // Need to specify, otherwise it is INSANELY SLOW on Ubuntu
        }
        catch (NoSuchAlgorithmException ex) {
            randomGenerator = new SecureRandom();
        }
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
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS V12_ELEMENT_PRINAME ON ELEMENTS (PRIMARYNAME)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_ELEMENT_TYPE ON ELEMENTS (ELEMENTTYPE)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_ELEMENT_PRINAME_TYPE ON ELEMENTS (PRIMARYNAME, ELEMENTTYPE)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createElement(new ElementCore(0, "Unknown Creature"), false);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "LOCATIONS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableLocations);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS V12_LOCATION_NAME ON LOCATIONS (NAME)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createLocation(new LocationCore(0, "Some Place"), false);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "VISITS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableVisits);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS V12_VISIT_NAME ON VISITS (NAME)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_VISIT_LOCATION ON VISITS (LOCATIONID)");
                // Create default entry
                if (inCreateDefaultRecords) {
                    createVisit(new VisitCore(0, "Casual Observations", findLocation(0, "Some Place", LocationCore.class).getID()), false);
                }
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "SIGHTINGS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableSightings);
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_ELEMENT ON SIGHTINGS (ELEMENTID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_LOCATION ON SIGHTINGS (LOCATIONID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_VISIT ON SIGHTINGS (VISITID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_ELEMENT_LOCATION ON SIGHTINGS (ELEMENTID, LOCATIONID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_ELEMENT_VISIT ON SIGHTINGS (ELEMENTID, VISITID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_SIGHTING_DATE ON SIGHTINGS (SIGHTINGDATE)");
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "FILES", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableFiles);
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS V12_FILE_ORGPATH ON FILES (ORIGINALPATH)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_FILE_LINKID ON FILES (LINKID)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_FILE_FILETYPE ON FILES (FILETYPE)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_FILE_ID_DEFAULT ON FILES (LINKID, ISDEFAULT)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_FILE_ORGPATH_DEFAULT ON FILES (ORIGINALPATH, ISDEFAULT)");
                state.execute("CREATE INDEX IF NOT EXISTS V12_FILE_ID_TYPE_DEFAULT ON FILES (LINKID, FILETYPE, ISDEFAULT)");
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
            results = conn.getMetaData().getTables(null, null, "WILDLOGUSERS", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableUsers);
                closeStatement(state);
            }
            closeResultset(results);
            results = conn.getMetaData().getTables(null, null, "WILDLOG", null);
            if (!results.next()) {
                state = conn.createStatement();
                state.execute(tableWildLogOptions);
                closeStatement(state);
                if (inCreateDefaultRecords) {
                    createWildLogOptions();
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
    public int countVisits(String inName, long inLocationID) {
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
            if (inLocationID > 0) {
                sql = sql + " WHERE LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLocationID);
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
    public int countSightings(long inID, long inElementID, long inLocationID, long inVisitID) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countSighting;
            if (inID > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inElementID > 0 && inLocationID > 0) {
                sql = sql + " WHERE ELEMENTID = ? AND LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
                state.setLong(2, inLocationID);
            }
            else
            if (inElementID > 0 && inVisitID > 0) {
                sql = sql + " WHERE ELEMENTID = ? AND VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
                state.setLong(2, inVisitID);
            }
            else
            if (inElementID > 0) {
                sql = sql + " WHERE ELEMENTID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
            }
            else
            if (inLocationID > 0) {
                sql = sql + " WHERE LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLocationID);
            }
            else
            if (inVisitID > 0) {
                sql = sql + " WHERE VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inVisitID);
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
    public int countWildLogFiles(long inID, long inLinkID) {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countFile;
            if (inID > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inLinkID > 0) {
                sql = sql + " WHERE LINKID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLinkID);
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
    public int countUsers() {
        PreparedStatement state = null;
        ResultSet results = null;
        int count = 0;
        try {
            String sql = countUsers;
            state = conn.prepareStatement(sql);
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
    public <T extends ElementCore> T findElement(long inID, String inPrimaryName, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempElement = null;
        try {
            String sql = findElement;
            if (inID > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inPrimaryName != null && UtilsData.sanitizeString(inPrimaryName).length() > 0) {
                sql = sql + " WHERE PRIMARYNAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inPrimaryName));
            }
            else {
                return null;
            }
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
        inElement.setID(inResults.getLong("ID"));
        inElement.setPrimaryName(inResults.getString("PRIMARYNAME"));
        inElement.setOtherName(inResults.getString("OTHERNAME"));
        inElement.setScientificName(inResults.getString("SCIENTIFICNAME"));
        inElement.setDescription(inResults.getString("DESCRIPTION"));
        inElement.setDistribution(inResults.getString("DISTRIBUTION"));
        inElement.setNutrition(inResults.getString("NUTRITION"));
        inElement.setDiagnosticDescription(inResults.getString("DIAGNOSTICDESCRIPTION"));
        inElement.setEndangeredStatus(EndangeredStatus.getEnumFromText(inResults.getString("ENDANGEREDSTATUS")));
        inElement.setBehaviourDescription(inResults.getString("BEHAVIOURDESCRIPTION"));
        inElement.setType(ElementType.getEnumFromText(inResults.getString("ELEMENTTYPE")));
        inElement.setFeedingClass(FeedingClass.getEnumFromText(inResults.getString("FEEDINGCLASS")));
        inElement.setReferenceID(inResults.getString("REFERENCEID"));
        inElement.setAuditTime(inResults.getLong("AUDITTIME"));
        inElement.setAuditUser(inResults.getString("AUDITUSER"));
    }

    @Override
    public <T extends LocationCore> T findLocation(long inID, String inName, Class<T> inReturnType) {
        T tempLocation = null;
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            String sql = findLocation;
            if (inID > 0) {
                sql = sql + " WHERE ID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inName != null && UtilsData.sanitizeString(inName).length() > 0) {
                sql = sql + " WHERE NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else {
                return null;
            }
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
        inLocation.setID(inResults.getLong("ID"));
        inLocation.setName(inResults.getString("NAME"));
        inLocation.setDescription(inResults.getString("DESCRIPTION"));
        inLocation.setRating(LocationRating.getEnumFromText(inResults.getString("RATING")));
        inLocation.setGameViewingRating(GameViewRating.getEnumFromText(inResults.getString("GAMEVIEWINGRATING")));
        inLocation.setHabitatType(inResults.getString("HABITATTYPE"));
        inLocation.setLatitude(Latitudes.getEnumFromText(inResults.getString("LATITUDEINDICATOR")));
        inLocation.setLatDegrees(inResults.getInt("LATDEGREES"));
        inLocation.setLatMinutes(inResults.getInt("LATMINUTES"));
        inLocation.setLatSeconds(inResults.getDouble("LATSECONDS"));
        inLocation.setLongitude(Longitudes.getEnumFromText(inResults.getString("LONGITUDEINDICATOR")));
        inLocation.setLonDegrees(inResults.getInt("LONDEGREES"));
        inLocation.setLonMinutes(inResults.getInt("LONMINUTES"));
        inLocation.setLonSeconds(inResults.getDouble("LONSECONDS"));
        inLocation.setGPSAccuracy(GPSAccuracy.getEnumFromText(inResults.getString("GPSACCURACY")));
        inLocation.setGPSAccuracyValue(inResults.getDouble("GPSACCURACYVALUE"));
        inLocation.setAuditTime(inResults.getLong("AUDITTIME"));
        inLocation.setAuditUser(inResults.getString("AUDITUSER"));
    }

    @Override
    public <T extends VisitCore> T findVisit(long inID, String inName, boolean inIncludeCachedValues, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempVisit = null;
        try {
            String sql;
            if (inIncludeCachedValues) {
                sql = findVisitWithCached;
            }
            else {
                sql = findVisit;
            }
            if (inID > 0) {
                sql = sql + " WHERE VISITS.ID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inName != null && UtilsData.sanitizeString(inName).length() > 0) {
                sql = sql + " WHERE VISITS.NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else {
                return null;
            }
            results = state.executeQuery();
            if (results.next()) {
                tempVisit = inReturnType.newInstance();
                populateVisit(results, tempVisit, inIncludeCachedValues);
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

    protected <T extends VisitCore> void populateVisit(ResultSet inResults, T inVisit, boolean inIncludeCachedValues) throws SQLException {
        inVisit.setID(inResults.getLong("ID"));
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
        inVisit.setLocationID(inResults.getLong("LOCATIONID"));
        inVisit.setAuditTime(inResults.getLong("AUDITTIME"));
        inVisit.setAuditUser(inResults.getString("AUDITUSER"));
        if (inIncludeCachedValues) {
            inVisit.setCachedLocationName(inResults.getString("LOCATIONNAME"));
        }
    }

    @Override
    public <T extends SightingCore> T findSighting(long inID, boolean inIncludeCachedValues, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempSighting = null;
        try {
            String sql;
            if (inIncludeCachedValues) {
                sql = findSightingWithCached;
            }
            else {
                sql = findSighting;
            }
            sql = sql + " WHERE SIGHTINGS.ID = ?";
            state = conn.prepareStatement(sql);
            state.setLong(1, inID);
            results = state.executeQuery();
            if (results.next()) {
                tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting, inIncludeCachedValues);
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

    protected <T extends SightingCore> void populateSighting(ResultSet inResults, T inSighting, boolean inIncludeCachedValues) throws SQLException {
        inSighting.setID(inResults.getLong("ID"));
        if (inResults.getTimestamp("SIGHTINGDATE") != null) {
            inSighting.setDate(new Date(inResults.getTimestamp("SIGHTINGDATE").getTime()));
        }
        else {
            inSighting.setDate(null);
        }
        inSighting.setElementID(inResults.getLong("ELEMENTID"));
        inSighting.setLocationID(inResults.getLong("LOCATIONID"));
        inSighting.setVisitID(inResults.getLong("VISITID"));
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
        inSighting.setGPSAccuracyValue(inResults.getDouble("GPSACCURACYVALUE"));
        inSighting.setTimeAccuracy(TimeAccuracy.getEnumFromText(inResults.getString("TIMEACCURACY")));
        inSighting.setAge(Age.getEnumFromText(inResults.getString("AGE")));
        inSighting.setAuditTime(inResults.getLong("AUDITTIME"));
        inSighting.setAuditUser(inResults.getString("AUDITUSER"));
        if (inIncludeCachedValues) {
            inSighting.setCachedElementName(inResults.getString("ELEMENTNAME"));
            inSighting.setCachedLocationName(inResults.getString("LOCATIONNAME"));
            inSighting.setCachedVisitName(inResults.getString("VISITNAME"));
            inSighting.setCachedElementType(ElementType.getEnumFromText(inResults.getString("ELEMENTTYPE")));
            inSighting.setCachedVisitType(VisitType.getEnumFromText(inResults.getString("VISITTYPE")));
            if (inResults.getInt("INATCOUNT") == 0) {
                inSighting.setCachedLinkedToINaturalist(false);
            }
            else {
                inSighting.setCachedLinkedToINaturalist(true);
            }
        }
    }

    @Override
    public <T extends WildLogFileCore> T findWildLogFile(long inID, long inLinkID, WildLogFileType inWildLogFileType, String inDBFilePath, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T tempFile = null;
        try {
            String sql = findFile;
            if (inID > 0) {
                sql = sql + " WHERE ID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setLong(1, inID);
            }
            else
            if (inLinkID > 0) {
                sql = sql + " WHERE LINKID = ?";
                if (inWildLogFileType != null) {
                    sql = sql + " AND FILETYPE = ?";
                }
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH LIMIT 1";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLinkID);
                if (inWildLogFileType != null) {
                    state.setString(2, UtilsData.stringFromObject(inWildLogFileType));
                }
            }
            else
            if (inWildLogFileType != null) {
                sql = sql + " WHERE FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH LIMIT 1";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.stringFromObject(inWildLogFileType));
            }
            else
            if (inDBFilePath != null && !inDBFilePath.isEmpty()) {
                sql = sql + " WHERE ORIGINALPATH = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inDBFilePath).replace("\\", "/"));
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
        inWildLogFile.setID(inResults.getLong("ID"));
        inWildLogFile.setLinkID(inResults.getLong("LINKID"));
        inWildLogFile.setLinkType(WildLogFileLinkType.getEnumFromText(inResults.getString("LINKTYPE")));
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
        inWildLogFile.setAuditTime(inResults.getLong("AUDITTIME"));
        inWildLogFile.setAuditUser(inResults.getString("AUDITUSER"));
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
                tempWildLogOptions.setDefaultZoom(results.getDouble("DEFAULTZOOM"));
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
    public <T extends VisitCore> List<T> listVisits(String inName, long inLocationID, VisitType inVisitType, boolean inIncludeCachedValues, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listVisit;
            if (inIncludeCachedValues) {
                sql = listVisitsWithCached;
            }
            else {
                sql = listVisit;
            }
            if (inName != null) {
                sql = sql + " WHERE VISITS.NAME = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.sanitizeString(inName));
            }
            else 
            if (inLocationID > 0) {
                sql = sql + " WHERE LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLocationID);
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
                populateVisit(results, tempVisit, inIncludeCachedValues);
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
    public <T extends SightingCore> List<T> listSightings(long inElementID, long inLocationID, long inVisitID, boolean inIncludeCachedValues, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql;
            if (inIncludeCachedValues) {
                sql = findSightingWithCached;
            }
            else {
                sql = findSighting;
            }
            if (inElementID > 0 && inLocationID > 0 && inVisitID > 0) {
                sql = sql + " WHERE ELEMENTID = ? AND SIGHTINGS.LOCATIONID = ? AND VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
                state.setLong(2, inLocationID);
                state.setLong(3, inVisitID);
            }
            else
            if (inElementID > 0 && inLocationID > 0) {
                sql = sql + " WHERE ELEMENTID = ? AND SIGHTINGS.LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
                state.setLong(2, inLocationID);
            }
            else
            if (inElementID > 0 && inVisitID > 0) {
                sql = sql + " WHERE ELEMENTID = ? AND VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
                state.setLong(2, inVisitID);
            }
            else
            if (inLocationID > 0 && inVisitID > 0) {
                sql = sql + " WHERE SIGHTINGS.LOCATIONID = ? AND VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLocationID);
                state.setLong(2, inVisitID);
            }
            else
            if (inElementID > 0) {
                sql = sql + " WHERE ELEMENTID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inElementID);
            }
            else
            if (inLocationID > 0) {
                sql = sql + " WHERE SIGHTINGS.LOCATIONID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLocationID);
            }
            else
            if (inVisitID > 0) {
                sql = sql + " WHERE VISITID = ?";
                state = conn.prepareStatement(sql);
                state.setLong(1, inVisitID);
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting, inIncludeCachedValues);
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
    public <T extends WildLogFileCore> List<T> listWildLogFiles(long inLinkID, WildLogFileType inWildLogFileType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listFile;
            if (inLinkID >= 0 && (inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
                sql = sql + " WHERE LINKID = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLinkID);
            }
            else
            if (inLinkID >= 0 && !(inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
                sql = sql + " WHERE LINKID = ? AND FILETYPE = ?";
                sql = sql + " ORDER BY ISDEFAULT desc, ORIGINALPATH";
                state = conn.prepareStatement(sql);
                state.setLong(1, inLinkID);
                state.setString(2, UtilsData.stringFromObject(inWildLogFileType));
            }
            else
            if (inLinkID == -1 && !(inWildLogFileType == null || WildLogFileType.NONE.equals(inWildLogFileType))) {
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
    public <T extends WildLogUser> List<T> listUsers(WildLogUserTypes inType, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = listUsers;
            if (inType != null) {
                sql = sql + " WHERE TYPE = ?";
                state = conn.prepareStatement(sql);
                state.setString(1, UtilsData.stringFromObject(inType));
            }
            else {
                state = conn.prepareStatement(sql);
            }
            results = state.executeQuery();
            while (results.next()) {
                T temp = inReturnType.newInstance();
                temp.setUsername(results.getString("USERNAME"));
                temp.setPassword(results.getString("PASSWORD"));
                temp.setType(WildLogUserTypes.getEnumFromText(results.getString("TYPE")));
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
    public <T extends ElementCore> boolean createElement(T inElement, boolean inNewButUseOldAuditAndID) {
        PreparedStatement state = null;
        PreparedStatement tempState = null;
        ResultSet results = null;
        try {
            // Make sure the name isn't already used
            if (countElements(inElement.getPrimaryName(), null) > 0) {
                System.err.println("Trying to save an Element using a name that already exists.... (" + inElement.getPrimaryName() + ")");
                return false;
            }
            // Get the new ID
            if (!inNewButUseOldAuditAndID) {
                inElement.setID(generateID());
            }
            // Insert
            state = conn.prepareStatement(createElement);
            maintainElement(state, inElement, inNewButUseOldAuditAndID);
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
    public <T extends ElementCore> boolean updateElement(T inElement, String inOldName, boolean inUseOldAudit) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                if (countElements(inElement.getPrimaryName(), null) > 0) {
                    System.err.println("Trying to save an Element using a name that already exists.... (" + inElement.getPrimaryName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Update
            state = conn.prepareStatement(updateElement);
            maintainElement(state, inElement, inUseOldAudit);
            state.setLong(16, inElement.getID());
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

    private <T extends ElementCore> void maintainElement(PreparedStatement state, T inElement, boolean inUseOldAudit) throws SQLException {
        state.setLong(1, inElement.getID());
        state.setString(2, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getPrimaryName()), 150));
        state.setString(3, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getOtherName()), 150));
        state.setString(4, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getScientificName()), 150));
        state.setString(5, UtilsData.sanitizeString(inElement.getDescription()));
        state.setString(6, UtilsData.sanitizeString(inElement.getDistribution()));
        state.setString(7, UtilsData.sanitizeString(inElement.getNutrition()));
        state.setString(8, UtilsData.sanitizeString(inElement.getDiagnosticDescription()));
        state.setString(9, UtilsData.stringFromObject(inElement.getEndangeredStatus()));
        state.setString(10, UtilsData.sanitizeString(inElement.getBehaviourDescription()));
        state.setString(11, UtilsData.stringFromObject(inElement.getType()));
        state.setString(12, UtilsData.stringFromObject(inElement.getFeedingClass()));
        state.setString(13, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getReferenceID()), 50));
        if (!inUseOldAudit) {
            setupAuditInfo(inElement);
        }
        state.setLong(14, inElement.getAuditTime());
        state.setString(15, UtilsData.limitLength(UtilsData.sanitizeString(inElement.getAuditUser()), 150));
    }

    @Override
    public <T extends LocationCore> boolean createLocation(T inLocation, boolean inNewButUseOldAuditAndID) {
        PreparedStatement state = null;
        PreparedStatement tempState = null;
        ResultSet results = null;
        try {
            // Make sure the name isn't already used
            if (countLocations(inLocation.getName()) > 0) {
                System.err.println("Trying to save an Location using a name that already exists.... (" + inLocation.getName() + ")");
                return false;
            }
            // Get the new ID
            if (!inNewButUseOldAuditAndID) {
                inLocation.setID(generateID());
            }
            // Insert
            state = conn.prepareStatement(createLocation);
            maintainLocation(state, inLocation, inNewButUseOldAuditAndID);
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
    public <T extends LocationCore> boolean updateLocation(T inLocation, String inOldName, boolean inUseOldAudit) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                if (countLocations(inLocation.getName()) > 0) {
                    System.err.println("Trying to save an Location using a name that already exists.... (" + inLocation.getName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Update
            state = conn.prepareStatement(updateLocation);
            maintainLocation(state, inLocation, inUseOldAudit);
            state.setLong(19,inLocation.getID());
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

    private <T extends LocationCore> void maintainLocation(PreparedStatement state, T inLocation, boolean inUseOldAudit) throws SQLException {
        state.setLong(1, inLocation.getID());
        state.setString(2, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getName()), 150));
        state.setString(3, UtilsData.sanitizeString(inLocation.getDescription()));
        state.setString(4, UtilsData.stringFromObject(inLocation.getRating()));
        state.setString(5, UtilsData.stringFromObject(inLocation.getGameViewingRating()));
        state.setString(6, UtilsData.stringFromObject(inLocation.getHabitatType()));
        state.setString(7, UtilsData.stringFromObject(inLocation.getLatitude()));
        state.setInt(8, inLocation.getLatDegrees());
        state.setInt(9, inLocation.getLatMinutes());
        state.setDouble(10, inLocation.getLatSeconds());
        state.setString(11, UtilsData.stringFromObject(inLocation.getLongitude()));
        state.setInt(12, inLocation.getLonDegrees());
        state.setInt(13, inLocation.getLonMinutes());
        state.setDouble(14, inLocation.getLonSeconds());
        state.setString(15, UtilsData.stringFromObject(inLocation.getGPSAccuracy()));
        state.setDouble(16, inLocation.getGPSAccuracyValue());
        if (!inUseOldAudit) {
            setupAuditInfo(inLocation);
        }
        state.setLong(17, inLocation.getAuditTime());
        state.setString(18, UtilsData.limitLength(UtilsData.sanitizeString(inLocation.getAuditUser()), 150));
    }

    @Override
    public <T extends VisitCore> boolean createVisit(T inVisit, boolean inNewButUseOldAuditAndID) {
        PreparedStatement state = null;
        PreparedStatement tempState = null;
        ResultSet results = null;
        try {
            // Make sure the name isn't already used
            if (countVisits(inVisit.getName(), 0) > 0) {
                System.err.println("Trying to save an Visit using a name that already exists.... (" + inVisit.getName() + ")");
                return false;
            }
            // Get the new ID
            if (!inNewButUseOldAuditAndID) {
                inVisit.setID(generateID());
            }
            // Insert
            state = conn.prepareStatement(createVisit);
            maintainVisit(state, inVisit, inNewButUseOldAuditAndID);
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
    public <T extends VisitCore> boolean updateVisit(T inVisit, String inOldName, boolean inUseOldAudit) {
        PreparedStatement state = null;
        try {
            // Make sure the name isn't already used
            if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                if (countVisits(inVisit.getName(), 0) > 0) {
                    System.err.println("Trying to save an Visit using a name that already exists.... (" + inVisit.getName() + " | " + inOldName + ")");
                    return false;
                }
            }
            // Update
            state = conn.prepareStatement(updateVisit);
            maintainVisit(state, inVisit, inUseOldAudit);
            state.setLong(11, inVisit.getID());
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

    private <T extends VisitCore> void maintainVisit(PreparedStatement state, T inVisit, boolean inUseOldAudit) throws SQLException {
        state.setLong(1, inVisit.getID());
        state.setString(2, UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getName()), 150));
        if (inVisit.getStartDate() != null) {
            state.setDate(3, new java.sql.Date(inVisit.getStartDate().getTime()));
        }
        else {
            state.setDate(3, null);
        }
        if (inVisit.getEndDate() != null) {
            state.setDate(4, new java.sql.Date(inVisit.getEndDate().getTime()));
        }
        else {
            state.setDate(4, null);
        }
        state.setString(5, UtilsData.sanitizeString(inVisit.getDescription()));
        state.setString(6, UtilsData.stringFromObject(inVisit.getGameWatchingIntensity()));
        state.setString(7, UtilsData.stringFromObject(inVisit.getType()));
        state.setLong(8, inVisit.getLocationID());
        if (!inUseOldAudit) {
            setupAuditInfo(inVisit);
        }
        state.setLong(9, inVisit.getAuditTime());
        state.setString(10, UtilsData.limitLength(UtilsData.sanitizeString(inVisit.getAuditUser()), 150));
    }

    @Override
    public <T extends SightingCore> boolean createSighting(T inSighting, boolean inNewButUseOldAuditAndID) {
        PreparedStatement state = null;
        PreparedStatement tempState = null;
        ResultSet results = null;
        try {
            // Get the new ID
            if (!inNewButUseOldAuditAndID) {
                inSighting.setID(generateID());
            }
            // Insert
            state = conn.prepareStatement(createSighting);
            maintainSighting(state, inSighting, inNewButUseOldAuditAndID);
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
    public <T extends SightingCore> boolean updateSighting(T inSighting, boolean inUseOldAudit) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateSighting);
            maintainSighting(state, inSighting, inUseOldAudit);
            state.setLong(36, inSighting.getID());
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

    private <T extends SightingCore> void maintainSighting(PreparedStatement state, T inSighting, boolean inUseOldAudit) throws SQLException {
        // Populate the values
        state.setLong(1, inSighting.getID());
        if (inSighting.getDate() != null) {
            state.setTimestamp(2, new Timestamp(inSighting.getDate().getTime()));
        }
        else {
            state.setTimestamp(2, null);
        }
        state.setLong(3, inSighting.getElementID());
        state.setLong(4, inSighting.getLocationID());
        state.setLong(5, inSighting.getVisitID());
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
        state.setDouble(31, inSighting.getGPSAccuracyValue());
        state.setString(32, UtilsData.stringFromObject(inSighting.getTimeAccuracy()));
        state.setString(33, UtilsData.stringFromObject(inSighting.getAge()));
        if (!inUseOldAudit) {
            setupAuditInfo(inSighting);
        }
        state.setLong(34, inSighting.getAuditTime());
        state.setString(35, UtilsData.limitLength(UtilsData.sanitizeString(inSighting.getAuditUser()), 150));
    }

    @Override
    public <T extends WildLogFileCore> boolean createWildLogFile(T inWildLogFile, boolean inNewButUseOldAuditAndID) {
        PreparedStatement state = null;
        try {
            // Insert
            state = conn.prepareStatement(createFile);
            // Get the new ID
            if (!inNewButUseOldAuditAndID) {
                inWildLogFile.setID(generateID());
            }
            maintainWildLogFile(state, inWildLogFile, inNewButUseOldAuditAndID);
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
    public <T extends WildLogFileCore> boolean updateWildLogFile(T inWildLogFile, boolean inUseOldAudit) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateFile);
            maintainWildLogFile(state, inWildLogFile, inUseOldAudit);
            state.setLong(13, inWildLogFile.getID());
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

    private <T extends WildLogFileCore> void maintainWildLogFile(PreparedStatement state, T inWildLogFile, boolean inUseOldAudit) throws SQLException {
        state.setLong(1, inWildLogFile.getID());
        state.setLong(2, inWildLogFile.getLinkID());
        state.setString(3, UtilsData.stringFromObject(inWildLogFile.getLinkType()));
        state.setString(4, UtilsData.sanitizeString(inWildLogFile.getFilename()));
        state.setString(5, UtilsData.sanitizeString(inWildLogFile.getDBFilePath().replace("\\", "/")));
        state.setString(6, UtilsData.stringFromObject(inWildLogFile.getFileType()));
        if (inWildLogFile.getUploadDate() != null) {
            state.setDate(7, new java.sql.Date(inWildLogFile.getUploadDate().getTime()));
        }
        else {
            state.setDate(7, null);
        }
        state.setBoolean(8, inWildLogFile.isDefaultFile());
        if (inWildLogFile.getFileDate() != null) {
            state.setTimestamp(9, new Timestamp(inWildLogFile.getFileDate().getTime()));
        }
        else {
            state.setTimestamp(9, null);
        }
        state.setLong(10, inWildLogFile.getFileSize());
        if (!inUseOldAudit) {
            setupAuditInfo(inWildLogFile);
        }
        state.setLong(11, inWildLogFile.getAuditTime());
        state.setString(12, UtilsData.limitLength(UtilsData.sanitizeString(inWildLogFile.getAuditUser()), 150));
    }

    @Override
    public <T extends WildLogOptions> boolean createWildLogOptions() {
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
            state.setDouble(3, inWildLogOptions.getDefaultZoom());
            state.setFloat(4, inWildLogOptions.getDefaultSlideshowSpeed());
            state.setInt(5, inWildLogOptions.getDefaultSlideshowSize());
            state.setBoolean(6, inWildLogOptions.isUseThumbnailTables());
            state.setBoolean(7, inWildLogOptions.isUseThumnailBrowsing());
            state.setBoolean(8, inWildLogOptions.isEnableSounds());
            state.setBoolean(9, inWildLogOptions.isUseScientificNames());
            state.setString(10, inWildLogOptions.getWorkspaceName());
            state.setLong(11, inWildLogOptions.getWorkspaceID());
            state.setBoolean(12, inWildLogOptions.isUploadLogs());
            state.setBoolean(13, inWildLogOptions.isBundledPlayers());
            state.setBoolean(14, inWildLogOptions.isUseIndividualsInSightingPath());
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
    public boolean deleteElement(long inID) {
        PreparedStatement state = null;
        try {
            // Delete the ElementCore
            state = conn.prepareStatement(deleteElement);
            state.setLong(1, inID);
            state.executeUpdate();
            // Delete all Sightings for this ElementCore
            List<SightingCore> sightingList = listSightings(inID, 0, 0, false, SightingCore.class);
            for (SightingCore temp : sightingList) {
                deleteSighting(temp.getID());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(inID, null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getID());
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
    public boolean deleteLocation(long inID) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteLocation);
            state.setLong(1, inID);
            // Delete LocationCore
            state.executeUpdate();
            state.close();
            // Delete Visits for this LocationCore
            List<VisitCore> visitList = listVisits(null, inID, null, false, VisitCore.class);
            for (VisitCore temp : visitList) {
                deleteVisit(temp.getID());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(inID, null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getID());
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
    public boolean deleteVisit(long inID) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteVisit);
            state.setLong(1, inID);
            // Delete VisitCore
            state.executeUpdate();
            // Delete Sightings for this VisitCore
            List<SightingCore> sightingList = listSightings(0, 0, inID, false, SightingCore.class);
            for (SightingCore temp : sightingList) {
                deleteSighting(temp.getID());
            }
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(inID, null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getID());
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
    public boolean deleteSighting(long inID) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteSighting);
            state.setLong(1, inID);
            // Delete Sightings
            state.executeUpdate();
            // Delete Fotos
            List<WildLogFileCore> fileList = listWildLogFiles(inID, null, WildLogFileCore.class);
            for (WildLogFileCore temp : fileList) {
                deleteWildLogFile(temp.getID());
            }
            // Delete any linked iNaturalist data
            deleteINaturalistLinkedData(inID, 0);
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
    public boolean deleteWildLogFile(long inID) {
        // Note: This method only deletes one file at a time from the database.
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteFile);
            state.setLong(1, inID);
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
    protected void printSQLException(SQLException ex) {
        // Unwraps the entire exception chain to unveil the real cause of the Exception.
        SQLException nestedEx = ex;
        while (nestedEx != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + nestedEx.getSQLState());
            System.err.println("  Error Code: " + nestedEx.getErrorCode());
            System.err.println("  Message:    " + nestedEx.getMessage());
            nestedEx.printStackTrace(System.err);
            nestedEx = nestedEx.getNextException();
        }
    }

    protected void closeStatementAndResultset(Statement inStatement, ResultSet inResultSet) {
        closeStatement(inStatement);
        closeResultset(inResultSet);
    }

    protected void closeStatement(Statement inStatement) {
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

    protected void closeResultset(ResultSet inResultSet) {
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
    public <S extends SightingCore> List<S> searchSightings(List<Long> inActiveSightingIDs, Date inStartDate, Date inEndDate, 
            List<Long> inActiveLocations, List<Long> inActiveVisits, List<Long> inActiveElements, 
            boolean inIncludeCachedValues, Class<S> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<S> tempList = new ArrayList<S>();
        try {
            String sql;
            if (inIncludeCachedValues) {
                sql = findSightingWithCached;
            }
            else {
                sql = findSighting;
            }
            // Build SQL
            String andKeyword = " WHERE";
            if (inActiveSightingIDs != null && inActiveSightingIDs.size() > 0) {
                sql = sql + andKeyword + " SIGHTINGS.ID IN (";
                for (int t = 0; t < inActiveSightingIDs.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            if (inStartDate != null) {
                sql = sql + andKeyword + " SIGHTINGDATE >= ?";
                andKeyword = " AND";
            }
            if (inEndDate != null) {
                sql = sql + andKeyword + " SIGHTINGDATE <= ?";
                andKeyword = " AND";
            }
            if (inActiveLocations != null && inActiveLocations.size() > 0) {
                sql = sql + andKeyword + " SIGHTINGS.LOCATIONID IN (";
                for (int t = 0; t < inActiveLocations.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            if (inActiveVisits != null && inActiveVisits.size() > 0) {
                sql = sql + andKeyword + " VISITID IN (";
                for (int t = 0; t < inActiveVisits.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            if (inActiveElements != null && inActiveElements.size() > 0) {
                sql = sql + andKeyword + " ELEMENTID IN (";
                for (int t = 0; t < inActiveElements.size(); t++) {
                    sql = sql + "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                andKeyword = " AND";
            }
            state = conn.prepareStatement(sql);
            // Add parameters
            int paramCounter = 1;
            if (inActiveSightingIDs != null && inActiveSightingIDs.size() > 0) {
                for (Long sightingID : inActiveSightingIDs) {
                    state.setLong(paramCounter++, sightingID);
                }
            }
            if (inStartDate != null) {
                state.setTimestamp(paramCounter++, new Timestamp(inStartDate.getTime()));
            }
            if (inEndDate != null) {
                state.setTimestamp(paramCounter++, new Timestamp(inEndDate.getTime()));
            }
            if (inActiveLocations != null && inActiveLocations.size() > 0) {
                for (long activeLocation : inActiveLocations) {
                    state.setLong(paramCounter++, activeLocation);
                }
            }
            if (inActiveVisits != null && inActiveVisits.size() > 0) {
                for (long activeVisit : inActiveVisits) {
                    state.setLong(paramCounter++, activeVisit);
                }
            }
            if (inActiveElements != null && inActiveElements.size() > 0) {
                for (long activeElement : inActiveElements) {
                    state.setLong(paramCounter++, activeElement);
                }
            }
            // Execute SQL
            results = state.executeQuery();
            // Load results
            while (results.next()) {
                S tempSighting = inReturnType.newInstance();
                populateSighting(results, tempSighting, inIncludeCachedValues);
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
    public <T extends LocationCount> List<T> queryLocationCountForElement(long inElementID, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        List<T> tempList = new ArrayList<T>();
        try {
            String sql = queryLocationCountForElement;
            state = conn.prepareStatement(sql);
            if (inElementID > 0) {
                state.setLong(1, inElementID);
            }
            else {
                state.setLong(1, 0);
            }
            results = state.executeQuery();
            while (results.next()) {
                T tempLocationCount = inReturnType.newInstance();
                tempLocationCount.setLocationID(results.getLong(1));
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
//        return UUID.randomUUID().getMostSignificantBits();
        // Gebruik die mees betekenisvolle gedeelte van die tyd, en dan 'n random getal.
        // Dan draai die getal om, sodat die ID vinniger uniek raak (vir indekse) andersins begin almal met byna dieselfde waardes.
        // VErvang dan die laaste drie geyalle met 'n unieke getal.
        return Long.parseLong(Long.toString(randomGenerator.nextInt(100000)) + new StringBuilder(Long.toString(System.currentTimeMillis())).reverse().toString()) 
                / 1000L * 1000L + randomGenerator.nextInt(1000);
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
    
    @Override
    public <T extends WildLogUser> T findUser(String inUsername, Class<T> inReturnType) {
        PreparedStatement state = null;
        ResultSet results = null;
        T temp = null;
        try {
            state = conn.prepareStatement(findUser);
            state.setString(1, inUsername);
            results = state.executeQuery();
            if (results.next()) {
                temp = inReturnType.newInstance();
                temp.setUsername(results.getString("USERNAME"));
                temp.setPassword(results.getString("PASSWORD"));
                temp.setType(WildLogUserTypes.getEnumFromText(results.getString("TYPE")));
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
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return temp;
    }
    
    @Override
    public <T extends WildLogUser> boolean createUser(T inWildLogUser) {
        PreparedStatement state = null;
        try {
            //Insert
            state = conn.prepareStatement(createUser);
            // Populate the values
            maintainUser(state, inWildLogUser);
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
    public <T extends WildLogUser> boolean updateUser(T inWildLogUser) {
        PreparedStatement state = null;
        try {
            // Update
            state = conn.prepareStatement(updateUser);
            // Populate the values
            maintainUser(state, inWildLogUser);
            state.setString(4, inWildLogUser.getUsername());
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
    
    private <T extends WildLogUser> void maintainUser(PreparedStatement state, T inWildLogUser) throws SQLException {
        state.setString(1, UtilsData.limitLength(inWildLogUser.getUsername(), 150));
        state.setString(2, inWildLogUser.getPassword());
        state.setString(3, UtilsData.stringFromObject(inWildLogUser.getType()));
    }

    @Override
    public boolean deleteUser(String inUsername) {
        PreparedStatement state = null;
        try {
            state = conn.prepareStatement(deleteUser);
            state.setString(1, inUsername);
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
    public int activeSessionsCount() {
        PreparedStatement state = null;
        ResultSet results = null;
        try {
            String sql = activeSessionsCount;
            state = conn.prepareStatement(sql);
            results = state.executeQuery();
            if (results.next()) {
                return results.getInt(1);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        return 0;
    }
    
    protected abstract void setupAuditInfo(DataObjectWithAudit inDataObjectWithAudit);

}
