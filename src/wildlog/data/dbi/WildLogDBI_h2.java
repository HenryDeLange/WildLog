package wildlog.data.dbi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.h2.jdbc.JdbcSQLException;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


public class WildLogDBI_h2 extends DBI_JDBC implements WildLogDBI {

    /**
     * Use this constructor to connect to the default Workspace database.
     * @throws Exception 
     */
    public WildLogDBI_h2() throws Exception {
        this("jdbc:h2:"
                + WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath().resolve(WildLogPaths.DEFAULT_DATABASE_NAME.getRelativePath())
                + ";AUTOCOMMIT=ON;IGNORECASE=TRUE;QUERY_CACHE_SIZE=50", true);
    }

    /**
     * WARNING: Only use this constructor if you want to connect to a second H2 DB instance. The default
     * workspace database should use a constructor that does NOT specify the connection URL.
     * @param inConnectionURL
     * @param inCreateDefaultRecords
     * @throws java.lang.Exception
     */
    public WildLogDBI_h2(String inConnectionURL, boolean inCreateDefaultRecords) throws Exception {
        super();
        Statement state = null;
        ResultSet results = null;
        boolean started = true;
        try {
            Class.forName("org.h2.Driver").newInstance();
            Properties props = new Properties();
            props.setProperty("USER", "wildlog");
            props.setProperty("PASSWORD", "wildlog");
            try {
                conn = DriverManager.getConnection(inConnectionURL, props);
            }
            catch (JdbcSQLException ex) {
                WildLogApp.LOGGER.log(Level.INFO, "Could not connect to database, could be an old version. Try to connect and update the database using the old username and password...");
                WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
                // Might be trying to use the wrong password, try again with old password and update it
                props = new Properties();
                conn = DriverManager.getConnection(inConnectionURL, props);
                state = conn.createStatement();
                state.execute("CREATE USER wildlog PASSWORD 'wildlog' ADMIN");
                state.close();
                WildLogApp.LOGGER.log(Level.INFO, "Database username and password updated.");
                
// TODO: Hier kort 'n form van databse recovery s dinge skeep loop...
// DOEN MISKIEN IETS SOOS HIERDIE:
//   Verander die error popup om opsies te wys: "Open Different Workspace", "Try to restore from backup", Cancel"
//   Vir die restore opsie, delete (rename na _datestamp_broken) die stukkende DB en 
//   laat die user dan browse na 'n SQL dump (maak die file browser oop by the backups folder).
//   Maak dan 'n uwe DB (met ander password) en run die SCRIPT FROM command om die DB te laai.
//   Wanneer dit klaar is delete die temp username.

            }
            // Create table, indexes, etc.
            started = initialize(inCreateDefaultRecords);
            // Check database version and perform updates if required.
            // This also creates the WildLogOptions row the first time
            doUpdates();
        }
        catch (ClassNotFoundException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, "\nUnable to load the JDBC driver.");
            WildLogApp.LOGGER.log(Level.SEVERE, "Please check your CLASSPATH.");
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
            started = false;
        }
        catch (InstantiationException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, "\nUnable to instantiate the JDBC driver.");
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
            started = false;
        }
        catch (IllegalAccessException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, "\nNot allowed to access the JDBC driver.");
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
            started = false;
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
            started = false;
        }
        catch (Exception ex) {
            started = false;
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
        finally {
            closeStatementAndResultset(state, results);
            if (!started) {
                throw new Exception("Could not open WildLog database.");
            }
        }
    }


    @Override
    public void doBackup(Path inDestinationFolder) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Create the folders
            Files.createDirectories(inDestinationFolder);
            // Create a database file backup
            state.execute("BACKUP TO '" + inDestinationFolder.resolve("WildLog Backup - H2.zip").toAbsolutePath().toString() + "'");
            // Create a SQL dump
            state.execute("SCRIPT TO '" + inDestinationFolder.resolve("WildLog Backup - SQL.zip").toAbsolutePath().toString() + "' COMPRESSION ZIP");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
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
    }

    @Override
    public void doExportCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings) {
        Statement state = null;
        try {
            state = conn.createStatement();
            String sql;
            // Export Elements
            if (inExportAll || inElement != null) {
                sql = "SELECT * FROM ELEMENTS";
                if (inElement != null && inElement.getPrimaryName()!= null && !inElement.getPrimaryName().isEmpty()) {
                    sql = sql + " WHERE PRIMARYNAME = ''" + inElement.getPrimaryName().replaceAll("'", "''") + "''";
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Creatures.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Locations
            if (inExportAll || inLocation != null) {
                sql = "SELECT * FROM LOCATIONS";
                if (inLocation != null && inLocation.getName() != null && !inLocation.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inLocation.getName().replaceAll("'", "''") + "''";
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Places.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Visits
            if (inExportAll || inVisit != null) {
                sql = "SELECT * FROM VISITS";
                if (inVisit != null && inVisit.getName() != null && !inVisit.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inVisit.getName().replaceAll("'", "''") + "''";
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Periods.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Sightings
            if (inExportAll || inSighting != null) {
                sql = "SELECT * "
                    + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0)) LatDecDeg"
                    + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0)) LonDecDeg"
                    + " FROM SIGHTINGS";
                if (inSighting != null && inSighting.getSightingCounter() > 0) {
                    sql = sql + " WHERE SIGHTINGCOUNTER = " +  inSighting.getSightingCounter();
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Observations.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export List of Sightings
            if (inLstSightings != null && !inLstSightings.isEmpty()) {
                sql = "SELECT * "
                    + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0)) LatDecDeg"
                    + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0)) LonDecDeg"
                    + " FROM SIGHTINGS";
                sql = sql + " WHERE SIGHTINGCOUNTER in (";
                for (Sighting tempSighting : inLstSightings) {
                    sql = sql + tempSighting.getSightingCounter() + ",";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                state.execute("CALL CSVWRITE('" + inPath.resolve("Observations.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Files
            if (inExportAll) {
                state.execute("CALL CSVWRITE('" + inPath.resolve("Files.csv").toAbsolutePath().toString() + "', 'SELECT * FROM FILES')");
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
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
    }
    
    @Override
    public void doExportBasicCSV(Path inPath, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings) {
        Statement state = null;
        try {
            state = conn.createStatement();
            String sql = "SELECT " +
                        " E.PRIMARYNAME AS CREATURE, E.SCIENTIFICNAME AS SCIENTIFIC_NAME, E.ELEMENTTYPE AS CREATURE_TYPE, " +
                        " L.NAME AS PLACE, L.GPSACCURACY AS PLACE_GPS_ACCURACY, " +
                        " ((CASE WHEN L.LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN L.LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (L.LatDEGREES + (L.LatMINUTES + L.LatSECONDS /60.0)/60.0)) AS PLACE_LATITUDE, " +
                        " ((CASE WHEN L.LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN L.LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (L.LonDEGREES + (L.LonMINUTES + L.LonSECONDS /60.0)/60.0)) AS PLACE_LONGITUDE, " +
                        " V.NAME AS PERIOD, V.VISITTYPE AS PERIOD_TYPE, V.STARTDATE AS PERIOD_START_DATE, V.ENDDATE AS PERIOD_END_DATE, V.DESCRIPTION AS PERIOD_DESCRIPTION, " +
                        " S.SIGHTINGCOUNTER AS OBSERVATION, S.CERTAINTY, S.SIGHTINGEVIDENCE AS EVIDENCE, " +
                        " S.TIMEACCURACY AS TIME_ACCURACY, S.TIMEOFDAY AS TIME_OF_DAY, " +
                        " trunc(S.SIGHTINGDATE) OBSERVATION_DATE, cast(S.SIGHTINGDATE as time) OBSERVATION_TIME, " +
                        " S.GPSACCURACY AS OBSERVATION_GPS_ACCURACY, " +
                        " ((CASE WHEN S.LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN S.LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (S.LatDEGREES + (S.LatMINUTES + S.LatSECONDS /60.0)/60.0)) AS OBSERVATION_LATITUDE, " +
                        " ((CASE WHEN S.LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN S.LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (S.LonDEGREES + (S.LonMINUTES + S.LonSECONDS /60.0)/60.0)) AS OBSERVATION_LONGITUDE, " +
                        " S.NUMBEROFELEMENTS AS NUMBER_OF_CREATURES, S.LIFESTATUS AS LIFE_STATUS, S.TAG, S.DETAILS " +
                        " FROM SIGHTINGS S " +
                        " LEFT JOIN ELEMENTS E ON S.ELEMENTNAME = E.PRIMARYNAME " +
                        " LEFT JOIN LOCATIONS L ON S.LOCATIONNAME = L.NAME " +
                        " LEFT JOIN VISITS V ON S.VISITNAME = V.NAME ";
            String andIndicator = " WHERE ";
            if (inElement != null && inElement.getPrimaryName()!= null && !inElement.getPrimaryName().isEmpty()) {
                sql = sql + andIndicator + " S.ELEMENTNAME = ''" + inElement.getPrimaryName().replaceAll("'", "''") + "''";
                andIndicator = " AND ";
            }
            if (inLocation != null && inLocation.getName() != null && !inLocation.getName().isEmpty()) {
                sql = sql + andIndicator + " S.LOCATIONNAME = ''" + inLocation.getName().replaceAll("'", "''") + "''";
                andIndicator = " AND ";
            }
            if (inVisit != null && inVisit.getName() != null && !inVisit.getName().isEmpty()) {
                sql = sql + andIndicator + " S.VISITNAME = ''" + inVisit.getName().replaceAll("'", "''") + "''";
                andIndicator = " AND ";
            }
            if (inSighting != null && inSighting.getSightingCounter() > 0) {
                sql = sql + andIndicator + " S.SIGHTINGCOUNTER = " + inSighting.getSightingCounter();
                andIndicator = " AND ";
            }
            if (inLstSightings != null && !inLstSightings.isEmpty()) {
                sql = sql + andIndicator + " S.SIGHTINGCOUNTER IN (";
                for (Sighting tempSighting : inLstSightings) {
                    sql = sql + tempSighting.getSightingCounter() + ",";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
            }
            state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
        }
        catch (SQLException ex) {
            printSQLException(ex);
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
    }

    @Override
    public boolean doImportCSV(Path inPath, String inPrefix, boolean includeWildLogFilesTable) {
        Statement state = null;
        ResultSet results = null;
        boolean success = true;
        try {
            state = conn.createStatement();
            // Import Elements
            if (Files.exists(inPath.resolve("Creatures.csv").toAbsolutePath())) {
                results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Creatures.csv").toAbsolutePath().toString() + "')");
                while (results.next()) {
                    Element tempElement = new Element();
                    populateElement(results, tempElement);
                    tempElement.setPrimaryName(inPrefix + results.getString("PRIMARYNAME"));
                    success = success && createElement(tempElement);
                }
                results.close();
            }
            // Import Locations
            if (Files.exists(inPath.resolve("Places.csv").toAbsolutePath())) {
                results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Places.csv").toAbsolutePath().toString() + "')");
                while (results.next()) {
                    Location tempLocation = new Location();
                    populateLocation(results, tempLocation);
                    tempLocation.setName(inPrefix + results.getString("NAME"));
                    success = success && createLocation(tempLocation);
                }
                results.close();
            }
            // Import Visits
            if (Files.exists(inPath.resolve("Periods.csv").toAbsolutePath())) {
                results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Periods.csv").toAbsolutePath().toString() + "')");
                while (results.next()) {
                    Visit tempVisit = new Visit();
                    populateVisit(results, tempVisit);
                    tempVisit.setName(inPrefix + results.getString("NAME"));
                    tempVisit.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                    success = success && createVisit(tempVisit);
                }
                results.close();
            }
            // Import Sightings
            if (Files.exists(inPath.resolve("Observations.csv").toAbsolutePath())) {
                results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Observations.csv").toAbsolutePath().toString() + "')");
                while (results.next()) {
                    Sighting tempSighting = new Sighting();
                    populateSighting(results, tempSighting);
                    tempSighting.setSightingCounter(0);
                    tempSighting.setElementName(inPrefix + results.getString("ELEMENTNAME"));
                    tempSighting.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                    tempSighting.setVisitName(inPrefix + results.getString("VISITNAME"));
                    success = success && createSighting(tempSighting, false);
                }
                results.close();
            }
            if (includeWildLogFilesTable) {
                if (Files.exists(inPath.resolve("Files.csv").toAbsolutePath())) {
                    results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Files.csv").toAbsolutePath().toString() + "')");
                    while (results.next()) {
                        WildLogFile wildLogFile = new WildLogFile();
                        populateWildLogFile(results, wildLogFile);
                        wildLogFile.setId(results.getString("ID").replaceFirst("-", "-" + inPrefix)); // 'location-loc1' becomes 'location-prefixloc1'
                        success = success && createWildLogFile(wildLogFile);
                    }
                }
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
        return success;
    }
    
    @Override
    public boolean doImportBasicCSV(Path inPath, String inPrefix) {
        Statement state = null;
        ResultSet resultSet = null;
        boolean success = true;
        try {
            state = conn.createStatement();
            resultSet = state.executeQuery("CALL CSVREAD('" + inPath.toAbsolutePath().toString() + "')");
            while (resultSet.next() && success) {
                // Import Elements
                Element element = new Element();
                element.setPrimaryName(inPrefix + resultSet.getString("CREATURE"));
                element.setScientificName(resultSet.getString("SCIENTIFIC_NAME"));
                element.setType(ElementType.getEnumFromText(resultSet.getString("CREATURE_TYPE")));
                if (countElements(element.getPrimaryName(), null) == 0) {
                    success = success && createElement(element);
                }
                // Import Locations
                if (success) {
                    Location location = new Location();
                    location.setName(inPrefix + resultSet.getString("PLACE"));
                    location.setGPSAccuracy(GPSAccuracy.getEnumFromText(resultSet.getString("PLACE_GPS_ACCURACY")));
                    double lat = resultSet.getDouble("PLACE_LATITUDE");
                    if (lat != 0) {
                        Latitudes latitude;
                        if (lat < 0) {
                            latitude = Latitudes.SOUTH;
                        }
                        else {
                            latitude = Latitudes.NORTH;
                        }
                        location.setLatitude(latitude);
                        location.setLatDegrees(UtilsGPS.getDegrees(latitude, lat));
                        location.setLatMinutes(UtilsGPS.getMinutes(lat));
                        location.setLatSeconds(UtilsGPS.getSeconds(lat));
                    }
                    double lon = resultSet.getDouble("PLACE_LONGITUDE");
                    if (lon != 0) {
                        Longitudes longitude;
                        if (lon < 0) {
                            longitude = Longitudes.WEST;
                        }
                        else {
                            longitude = Longitudes.EAST;
                        }
                        location.setLongitude(longitude);
                        location.setLonDegrees(UtilsGPS.getDegrees(longitude, lon));
                        location.setLonMinutes(UtilsGPS.getMinutes(lon));
                        location.setLonSeconds(UtilsGPS.getSeconds(lon));
                    }
                    if (countLocations(location.getName()) == 0) {
                        success = success && createLocation(location);
                    }
                }
                // Import Visits
                if (success) {
                    Visit visit = new Visit();
                    visit.setName(inPrefix + resultSet.getString("PERIOD"));
                    visit.setLocationName(inPrefix + resultSet.getString("PLACE"));
                    visit.setType(VisitType.getEnumFromText(resultSet.getString("PERIOD_TYPE")));
                    if (resultSet.getDate("PERIOD_START_DATE") != null) {
                        visit.setStartDate(new Date(resultSet.getDate("PERIOD_START_DATE").getTime()));
                    }
                    else {
                        visit.setStartDate(null);
                    }
                    if (resultSet.getDate("PERIOD_END_DATE") != null) {
                        visit.setEndDate(new Date(resultSet.getDate("PERIOD_END_DATE").getTime()));
                    }
                    else {
                        visit.setEndDate(null);
                    }
                    visit.setDescription(resultSet.getString("PERIOD_DESCRIPTION"));
                    if (countVisits(visit.getName(), null) == 0) {
                        success = success && createVisit(visit);
                    }
                }
                // Import Sightings
                if (success) {
                    Sighting sighting = new Sighting();
                    sighting.setSightingCounter(0); // Indicate a new Sighting ID needs to be created
                    sighting.setElementName(inPrefix + resultSet.getString("CREATURE"));
                    sighting.setLocationName(inPrefix + resultSet.getString("PLACE"));
                    sighting.setVisitName(inPrefix + resultSet.getString("PERIOD"));
                    sighting.setCertainty(Certainty.getEnumFromText(resultSet.getString("CERTAINTY")));
                    sighting.setSightingEvidence(SightingEvidence.getEnumFromText(resultSet.getString("EVIDENCE")));
                    sighting.setTimeAccuracy(TimeAccuracy.getEnumFromText(resultSet.getString("TIME_ACCURACY")));
                    sighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(resultSet.getString("TIME_OF_DAY")));
                    if (resultSet.getTimestamp("OBSERVATION_DATE") != null) {
                        Timestamp timestamp = resultSet.getTimestamp("OBSERVATION_DATE");
                        if (timestamp != null) {
                            sighting.setDate(new Date(timestamp.getTime()));
                        }
                    }
                    else {
                        sighting.setDate(null);
                    }
                    if (resultSet.getTime("OBSERVATION_TIME") != null && sighting.getDate() != null) {
                        Time time = resultSet.getTime("OBSERVATION_TIME");
                        if (time != null) {
                            LocalDateTime localDateTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
                            sighting.setDate(UtilsTime.getDateFromLocalDateTime(localDateTime.with(time.toLocalTime())));
                        }
                    }
                    sighting.setGPSAccuracy(GPSAccuracy.getEnumFromText(resultSet.getString("OBSERVATION_GPS_ACCURACY")));
                    double lat = resultSet.getDouble("OBSERVATION_LATITUDE");
                    if (lat != 0) {
                        Latitudes latitude;
                        if (lat < 0) {
                            latitude = Latitudes.SOUTH;
                        }
                        else {
                            latitude = Latitudes.NORTH;
                        }
                        sighting.setLatitude(latitude);
                        sighting.setLatDegrees(UtilsGPS.getDegrees(latitude, lat));
                        sighting.setLatMinutes(UtilsGPS.getMinutes(lat));
                        sighting.setLatSeconds(UtilsGPS.getSeconds(lat));
                    }
                    double lon = resultSet.getDouble("OBSERVATION_LONGITUDE");
                    if (lon != 0) {
                        Longitudes longitude;
                        if (lon < 0) {
                            longitude = Longitudes.WEST;
                        }
                        else {
                            longitude = Longitudes.EAST;
                        }
                        sighting.setLongitude(longitude);
                        sighting.setLonDegrees(UtilsGPS.getDegrees(longitude, lon));
                        sighting.setLonMinutes(UtilsGPS.getMinutes(lon));
                        sighting.setLonSeconds(UtilsGPS.getSeconds(lon));
                    }
                    sighting.setNumberOfElements(resultSet.getInt("NUMBER_OF_CREATURES"));
                    sighting.setLifeStatus(LifeStatus.getEnumFromText(resultSet.getString("LIFE_STATUS")));
                    sighting.setTag(resultSet.getString("TAG"));
                    sighting.setDetails(resultSet.getString("DETAILS"));
                    success = success && createSighting(sighting, false);
                }
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            // ResultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
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
        return success;
    }

    @Override
    public boolean doImportIUCN(Path inPath, boolean inUpdatePrimaryName, boolean inAddNewElements, boolean inUpdateExistingElements) {
        Statement state = null;
        ResultSet results = null;
        boolean success = true;
        try {
            state = conn.createStatement();
            // Import Elements
            results = state.executeQuery("SELECT "
                    + "csv.\"Common names (Eng)\" EngName, "
                    + "(concat(csv.genus,' ',csv.species)) SciName, "
                    + "csv.Class ClassName, "
                    + "csv.\"Red List status\" IUCNStatus, "
                    + "csv.Synonyms SynName "
                    + "FROM csvread('" + inPath.toAbsolutePath().toString() + "') AS csv");
            while (results.next()) {
                String engName = getFirstPrimaryName(results.getString("EngName"));
                String sciName = results.getString("SciName").trim();
                // Twee verskillende Elements kan dieselfde spesie naam het (soos Blesbok en Bontebok)
                List<Element> lstElements = listElements(null, sciName, null, Element.class);
                Element elementToSave = null;
                boolean isExisting = false;
                String oldName = null;
                int counter = 0;
                do {
                    if (!lstElements.isEmpty()) {
                        isExisting = true;
                        elementToSave = lstElements.get(counter);
                        // Mark it as an update by giving the old name
                        oldName = elementToSave.getPrimaryName();
                        // Need to update the name fields
                        // IUCN has a comma sepperated list of common names. (For now I pick the first one.)
                        if (inUpdatePrimaryName) {
                            // Only update names if there are one Element with the same scientefic name
                            if (lstElements.size() == 1) {
                                // If primary name changed then the update will need to know the old name
                                if (!elementToSave.getPrimaryName().equalsIgnoreCase(engName)) {
                                    // Twee verskillende species kan dieselfde common name hê. (Append die scientific name agter aan.)
                                    if (countElements(elementToSave.getPrimaryName(), elementToSave.getScientificName()) > 0) {
                                        elementToSave.setPrimaryName(elementToSave.getPrimaryName() + " (" + sciName + ")");
                                        while (countElements(elementToSave.getPrimaryName(), elementToSave.getScientificName()) > 0) {
                                            elementToSave.setPrimaryName(elementToSave.getPrimaryName() + "_wl");
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (engName != null) {
                                elementToSave.setOtherName(engName);
                            }
                        }
                    }
                    else {
                        if (inAddNewElements) {
                            elementToSave = new Element();
                            elementToSave.setScientificName(sciName);
                            if (!inUpdatePrimaryName) {
                                elementToSave.setOtherName(engName);
                            }
                            // Since it's a new Element I MUST provide a Primary Name
                            if (engName != null) {
                                elementToSave.setPrimaryName(engName);
                            }
                            else {
                                elementToSave.setPrimaryName(sciName);
                            }
                        }
                    }
                    if (elementToSave != null) {
                        // Set IUCN status
                        elementToSave.setEndangeredStatus(EndangeredStatus.getEnumFromText(results.getString("IUCNStatus")));
                        // Also use the class to guess whether it is a mammal, bird, fish, etc.
                        if ("MAMMALIA".equalsIgnoreCase(results.getString("ClassName"))) {
                            elementToSave.setType(ElementType.MAMMAL);
                        }
                        else
                        if ("AVES".equalsIgnoreCase(results.getString("ClassName"))) {
                            elementToSave.setType(ElementType.BIRD);
                        }
                        else
                        if ("AMPHIBIA".equalsIgnoreCase(results.getString("ClassName"))) {
                            elementToSave.setType(ElementType.AMPHIBIAN);
                        }
                        else
                        if ("REPTILIA".equalsIgnoreCase(results.getString("ClassName"))) {
                            elementToSave.setType(ElementType.REPTILE);
                        }
                        if (!isExisting && inAddNewElements) {
                            // Twee verskillende species kan dieselfde common name hê. (Append die scientific name agter aan.)
                            if (countElements(elementToSave.getPrimaryName(), elementToSave.getScientificName()) > 0) {
                                elementToSave.setPrimaryName(elementToSave.getPrimaryName() + " (" + sciName + ")");
                                while (countElements(elementToSave.getPrimaryName(), elementToSave.getScientificName()) > 0) {
                                    elementToSave.setPrimaryName(elementToSave.getPrimaryName() + "_wl");
                                }
                            }
                        }
                        // Save the creature
                        if (isExisting && inUpdateExistingElements) {
                            success = success && updateElement(elementToSave, oldName);
                        }
                        else
                        if (!isExisting && inAddNewElements) {
                            success = success && createElement(elementToSave);
                        }
                    }
                    counter++;
                }
                while(counter < lstElements.size());
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
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
            // Statement
            try {
                if (state != null) {
                    state.close();
                }
            }
            catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return success;
    }

    private String getFirstPrimaryName(String inNames) throws SQLException {
        if (inNames == null) {
            return null;
        }
        String[] nameList = inNames.trim().split(",");
        if (nameList.length > 0) {
            return nameList[0].trim();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean deleteWildLogFile(String inDBFilePath) {
        // Note: This method only deletes one file at a time, and all it's "default" thumbnails.
        // First, remove the database entry.
        super.deleteWildLogFile(inDBFilePath);
        // Next, delete the original image
        try {
            Files.deleteIfExists(WildLogPaths.getFullWorkspacePrefix().resolve(inDBFilePath).normalize().toAbsolutePath());
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
        // Then, try to delete the "default/known" thumbnails.
        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
            try {
                // Note: Ek wil hier net die path kry, nie die thumbnail generate nie (so ek gebruik nie WildLogFile.getAbsoluteThumbnailPath() nie).
                Files.deleteIfExists(UtilsImageProcessing.calculateAbsoluteThumbnailPath(inDBFilePath, size));
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        return true;
    }

    private void doUpdates() {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            results = state.executeQuery("SELECT * FROM WILDLOG");
            // If there isn't a row create one
            if (!results.next()) {
                createWildLogOptions(new WildLogOptions());
                // Check whether this is a very old database (before the WildLogOptions existed)
                results = state.executeQuery("select count(*) from information_schema.columns where table_name = 'SIGHTINGS' and column_name = 'MOONLIGHT'");
                if (results.next() && results.getInt(1) < 1) {
                    // Drop the columns that will be re-added during the updates
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLATITUDE");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLONGITUDE");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLATOPTION");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLONOPTION");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTSLIDESHOWSPEED");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTSLIDESHOWSIZE");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTONLINEMAP");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN USETHUMBNAILTABLES");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN USETHUMBNAILBROWSE");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN ENABLESOUNDS");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN USESCIENTIFICNAMES");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN WORKSPACENAME");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN WORKSPACEID");
                    // Set the version number to trigger the updates
                    state.executeUpdate("UPDATE WILDLOG SET VERSION=1");
                }
            }
            // Read the row
            boolean databaseAndApplicationInSync = false;
            boolean wasMajorUpgrade = false;
            boolean upgradeWasDone = false;
            int choice = JOptionPane.CANCEL_OPTION;
            for (int t = 0; t <= WILDLOG_DB_VERSION; t++) {
                results = state.executeQuery("SELECT VERSION FROM WILDLOG");
                if (results.next()) {
                    if (results.getInt("VERSION") > WILDLOG_DB_VERSION) {
                        // The application codebase is older than the database version, need to update the application first
                        databaseAndApplicationInSync = false;
                        break;
                    }
                    else {
                        // The database and application versions are in sync
                        if (results.getInt("VERSION") == WILDLOG_DB_VERSION) {
                            databaseAndApplicationInSync = true;
                            break;
                        }
                        else {
                            // Only show the popup once
                            if (!upgradeWasDone) {
                                choice = UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return JOptionPane.showConfirmDialog(WildLogApp.getApplication().getMainFrame(),
                                                "<html>The Workspace at <b>" + WildLogPaths.getFullWorkspacePrefix().toString() + "</b> needs to be upgraded. "
                                                + "<br/>It is recommended to first make a manual backup (make a copy) of the Workspace before continuing, "
                                                + "in particular the WildLog\\Data and WildLog\\Files folders."
                                                + "<br/>Note that the upgrade can take a while to complete. WildLog will open automatically once the upgrade is complete."
                                                + "<br/><b>Press OK when you are ready to upgrade the Workspace.</b></html>",
                                                "Upgrade WildLog Database Structure", 
                                                JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                                    }
                                });
                            }
                            if (choice == JOptionPane.OK_OPTION) {
                                // Procede with the needed updates
                                if (results.getInt("VERSION") == 0) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v0 (before upgrade to 1)"));
                                    doUpdate1();
                                }
                                else
                                if (results.getInt("VERSION") == 1) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v1 (before upgrade to 2)"));
                                    doUpdate2();
                                }
                                else
                                if (results.getInt("VERSION") == 2) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v2 (before upgrade to 3)"));
                                    doUpdate3();
                                }
                                else
                                if (results.getInt("VERSION") == 3) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v3 (before upgrade to 4)"));
                                    doUpdate4();
                                }
                                else
                                if (results.getInt("VERSION") == 4) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v4 (before upgrade to 5)"));
                                    doUpdate5();
                                }
                                else
                                if (results.getInt("VERSION") == 5) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v5 (before upgrade to 6)"));
                                    doUpdate6();
                                    wasMajorUpgrade = true;
                                }
                                else
                                if (results.getInt("VERSION") == 6) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v6 (before upgrade to 7)"));
                                    doUpdate7();
                                }
                                else
                                if (results.getInt("VERSION") == 7) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v7 (before upgrade to 8)"));
                                    doUpdate8();
                                    wasMajorUpgrade = true; // Omdat die Files se folder struktuur verander het...
                                }
                                // Set the flag to indicate that an upgrade took place
                                upgradeWasDone = true;
                            }
                            else {
                                WildLogApp.getApplication().exit();
                            }
                        }
                    }
                }
            }
            if (databaseAndApplicationInSync) {
                if (upgradeWasDone) {
                    if (wasMajorUpgrade) {
                        UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                        "<html>The Workspace has been <b>successfully upgraded</b> to be compatible with <b>WildLog v" + WildLogApp.WILDLOG_VERSION + "</b>. "
                                            + "<br/>Please consider running the <i>'Check and Clean the Workspace'</i> process as well. "
                                            + "<br/>(The feature is accessable from the 'Application' menu at the top of the window.)</html>",
                                        "WildLog v" + WildLogApp.WILDLOG_VERSION + " - Major Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
                                return -1;
                            }
                        });
                    }
                    else {
                        UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                        "<html>The Workspace has been <b>successfully upgraded</b> to be compatible with <b>WildLog v" + WildLogApp.WILDLOG_VERSION + "</b>.</html>",
                                        "WildLog v" + WildLogApp.WILDLOG_VERSION + " - Minor Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
                                return -1;
                            }
                        });
                    }
                }
            }
            else {
                UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                "<html>The database could not be successfully updated!"
                                    + "<br/>Make sure that you are running the latest version of WildLog."
                                    + "<br/>Confirm that the Workspace isn't already open by another WildLog instance."
                                    + "<bt/>It is possible that the datbase might be broken or corrupted. "
                                    + "If this is the case you can restore a backup copy and try again, please consult the Manual for details."
                                    + "<br/>Contact support@mywild.co.za if the problmes persist.</html>",
                                "WildLog Upgrade Error", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
                WildLogApp.getApplication().exit();
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
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 1");
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
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 1");
    }

    private void doUpdate2() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 2");
        // This update adds a column to the options table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
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
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 2");
    }

    private void doUpdate3() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 3");
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
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTSLIDESHOWSPEED float(52) DEFAULT 1.5");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTSLIDESHOWSIZE int DEFAULT 750");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTONLINEMAP smallint DEFAULT true");
            // Change the Visit type Enum values
            state.executeUpdate("UPDATE VISITS SET VISITTYPE  = 'Camera Trap' WHERE VISITTYPE = 'Remote Camera'");
            state.executeUpdate("UPDATE VISITS SET VISITTYPE  = 'Census, Atlas, etc.' WHERE VISITTYPE = 'Bird Atlassing'");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=3");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 3");
    }

    private void doUpdate4() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 4");
        // This update removes and alters some tables and adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Make changes to Location
            state.execute("ALTER TABLE LOCATIONS ADD COLUMN GPSACCURACY varchar(50)");
            // Make changes to Sightings
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN GPSACCURACY varchar(50)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN TIMEACCURACY varchar(50)");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN AGE varchar(50)");
            // Migrate TimeAccuracy data
            results = state.executeQuery("SELECT SIGHTINGCOUNTER FROM SIGHTINGS WHERE UNKNOWNTIME = 1");
            while (results.next()) {
                Sighting sighting = findSighting(results.getLong("SIGHTINGCOUNTER"), Sighting.class);
                sighting.setTimeAccuracy(TimeAccuracy.UNKNOWN);
                updateSighting(sighting);
            }
            results.close();
            results = state.executeQuery("SELECT SIGHTINGCOUNTER FROM SIGHTINGS WHERE UNKNOWNTIME = 0");
            while (results.next()) {
                Sighting sighting = findSighting(results.getLong("SIGHTINGCOUNTER"), Sighting.class);
                sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                updateSighting(sighting);
            }
            results.close();
            state.execute("ALTER TABLE SIGHTINGS DROP COLUMN UNKNOWNTIME");
            state.executeUpdate("UPDATE SIGHTINGS SET TIMEACCURACY = 'GOOD' WHERE TIMEACCURACY IS NULL OR TIMEACCURACY = ''");
            // Make changes to Files
            // NOTE: It is best to make sure that Clean Workspace in v3 has been run before upgrading
            state.executeUpdate("UPDATE FILES SET ORIGINALPATH = replace(replace(regexp_replace(ORIGINALPATH, '\\\\','/'), '/WildLog/Files', 'Files'), 'WildLog/Files', 'Files')");
            // Create indexes
            state.execute("CREATE UNIQUE INDEX IF NOT EXISTS ELEMENT_PRINAME ON ELEMENTS (PRIMARYNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS ELEMENT_TYPE ON ELEMENTS (ELEMENTTYPE)");
            state.execute("CREATE INDEX IF NOT EXISTS ELEMENT_PRINAME_TYPE ON ELEMENTS (PRIMARYNAME, ELEMENTTYPE)");
            state.execute("CREATE UNIQUE INDEX IF NOT EXISTS LOCATION_NAME ON LOCATIONS (NAME)");
            state.execute("CREATE UNIQUE INDEX IF NOT EXISTS VISIT_NAME ON VISITS (NAME)");
            state.execute("CREATE INDEX IF NOT EXISTS VISIT_LOCATION ON VISITS (LOCATIONNAME)");
            state.execute("CREATE UNIQUE INDEX IF NOT EXISTS SIGHTING_CNT ON SIGHTINGS (SIGHTINGCOUNTER)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT ON SIGHTINGS (ELEMENTNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_LOCATION ON SIGHTINGS (LOCATIONNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_VISIT ON SIGHTINGS (VISITNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT_LOCATION ON SIGHTINGS (ELEMENTNAME, LOCATIONNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_ELEMENT_VISIT ON SIGHTINGS (ELEMENTNAME, VISITNAME)");
            state.execute("CREATE INDEX IF NOT EXISTS SIGHTING_DATE ON SIGHTINGS (SIGHTINGDATE)");
            try {
                state.execute("CREATE UNIQUE INDEX IF NOT EXISTS FILE_ORGPATH ON FILES (ORIGINALPATH)");
            }
            catch (SQLException ex) {
                WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
                // Try again to create the index, but settle for non-unique
                state.execute("CREATE INDEX IF NOT EXISTS FILE_ORGPATH ON FILES (ORIGINALPATH)");
            }
            state.execute("CREATE INDEX IF NOT EXISTS FILE_ID ON FILES (ID)");
            state.execute("CREATE INDEX IF NOT EXISTS FILE_FILETYPE ON FILES (FILETYPE)");
            state.execute("CREATE INDEX IF NOT EXISTS FILE_ID_DEFAULT ON FILES (ID, ISDEFAULT)");
            state.execute("CREATE INDEX IF NOT EXISTS FILE_ORGPATH_DEFAULT ON FILES (ORIGINALPATH, ISDEFAULT)");
            state.execute("CREATE INDEX IF NOT EXISTS FILE_ID_TYPE_DEFAULT ON FILES (ID, FILETYPE, ISDEFAULT)");
            // Make changes to Wildlog settings
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USETHUMBNAILTABLES smallint DEFAULT true");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USETHUMBNAILBROWSE smallint DEFAULT false");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN ENABLESOUNDS smallint DEFAULT true");
            // Update Sightings and WildLog files to use the new UUIDs
            List<Sighting> lstSightings = listSightings(0, null, null, null, false, Sighting.class);
            for (Sighting sighting : lstSightings) {
                long newID = sighting.getDate().getTime()*1000000L + randomGenerator.nextInt(999999);
                results = state.executeQuery("SELECT COUNT(SIGHTINGCOUNTER) FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + newID);
                while (results.next() && results.getInt(1) > 0) {
                    // ID already used, try a new ID
                    newID = System.currentTimeMillis()*1000000L + randomGenerator.nextInt(999999);
                    results = state.executeQuery("SELECT COUNT(SIGHTINGCOUNTER) FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + newID);
                }
                state.executeUpdate("UPDATE SIGHTINGS SET SIGHTINGCOUNTER = " + newID + " WHERE SIGHTINGCOUNTER = " + sighting.getSightingCounter());
                state.executeUpdate("UPDATE FILES SET ID = '" + new Sighting(newID).getWildLogFileID() + "' WHERE ID = '" + sighting.getWildLogFileID() + "'");
            }
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=4");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 4");
    }
    
    private void doUpdate5() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 5");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Make changes to WildLog Options
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USESCIENTIFICNAMES smallint DEFAULT true");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN WORKSPACENAME varchar(50) DEFAULT 'WildLog Workspace'");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN WORKSPACEID bigint DEFAULT 0");
            WildLogOptions options = findWildLogOptions(WildLogOptions.class);
            options.setWorkspaceID(generateID());
            updateWildLogOptions(options);
            // Recalculate all sun and moon phase info (the enums changed)
            List<Sighting> lstSightings = listSightings(0, null, null, null, false, Sighting.class);
            for (Sighting sighting : lstSightings) {
                UtilsTime.calculateSunAndMoon(sighting);
                updateSighting(sighting);
            }
            // Increase the cache size slightly (doubled)
            state.execute("SET CACHE_SIZE 32768");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=5");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 5");
    }
    
    private void doUpdate6() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 6");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Make changes to WildLog Options
            state.execute("ALTER TABLE FILES ADD COLUMN FILEDATE timestamp");
            state.execute("ALTER TABLE FILES ADD COLUMN FILESIZE bigint");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=6");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 6");
    }
    
    private void doUpdate7() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 7");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // NOTE: The new ADHOC table will automatically be created...
            // Delete the WildLog Options that are nolonger used
            state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTONLINEMAP");
            state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLATOPTION");
            state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTLONOPTION");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=7");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 7");
    }
    
    private void doUpdate8() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 8");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the new columns
            state.execute("ALTER TABLE WILDLOG ADD COLUMN UPLOADLOGS smallint DEFAULT true");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN BUNDLEDPLAYERS smallint DEFAULT true");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=8");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 8");
    }

}
