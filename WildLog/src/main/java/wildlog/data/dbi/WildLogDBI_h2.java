package wildlog.data.dbi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import org.h2.jdbc.JdbcSQLException;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.dbi.legacy.Legacy_DBI_JDBC;
import wildlog.data.dbi.legacy.Legacy_SightingCore;
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
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.BusyDialog;
import wildlog.ui.dialogs.WorkspaceImportDialog;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsCompression;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogPaths;


public class WildLogDBI_h2 extends DBI_JDBC implements WildLogDBI {
    private final String connectionURL;
    
    /**
     * Use this constructor to connect to the default Workspace database.
     * @throws Exception 
     */
    public WildLogDBI_h2(boolean inCreateDefaultData, boolean inH2AutoServer) throws Exception {
        this(WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath().resolve(WildLogPaths.DEFAULT_DATABASE_NAME.getRelativePath()).toString(), 
                inCreateDefaultData, inH2AutoServer);
    }

    /**
     * WARNING: Only use this constructor if you want to connect to a second H2 DB instance. The default
     * workspace database should use a constructor that does NOT specify the connection URL.
     */
    public WildLogDBI_h2(String inConnectionURL, boolean inCreateDefaultRecords, boolean inH2AutoServer) throws Exception {
        super();
        Statement state = null;
        ResultSet results = null;
        boolean started = true;
        connectionURL = inConnectionURL;
        try {
            setupConnection(inH2AutoServer);
            // Create table, indexes, default values, etc.
            started = super.initialize(inCreateDefaultRecords);
            // Check database version and perform updates if required.
            // This also creates the WildLogOptions row the first time
            doUpdates();
            // After the updates have completed, recreate the connection using the original inH2AutoServer value
            close();
            setupConnection(inH2AutoServer);
        }
        catch (ClassNotFoundException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "\nUnable to load the JDBC driver.");
            WildLogApp.LOGGER.log(Level.ERROR, "Please check your CLASSPATH.");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            started = false;
        }
        catch (InstantiationException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "\nUnable to instantiate the JDBC driver.");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            started = false;
        }
        catch (IllegalAccessException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "\nNot allowed to access the JDBC driver.");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            started = false;
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
            started = false;
        }
        catch (Exception ex) {
            started = false;
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        finally {
            closeStatementAndResultset(state, results);
            if (!started) {
                throw new Exception("Could not open WildLog database.");
            }
        }
    }
    
    private void setupConnection(boolean inH2AutoServer) 
            throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String connection = "jdbc:h2:" + connectionURL 
                + ";AUTOCOMMIT=ON;IGNORECASE=TRUE;QUERY_CACHE_SIZE=100";
        if (inH2AutoServer) {
            connection = connection + ";AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9229";
        }
        Class.forName("org.h2.Driver").newInstance();
        Properties props = new Properties();
        props.setProperty("USER", "wildlog");
        props.setProperty("PASSWORD", "wildlog");
        try {
            conn = DriverManager.getConnection(connection, props);
        }
        catch (JdbcSQLException ex) {
            WildLogApp.LOGGER.log(Level.INFO, "Could not connect to database, could be a very old version. "
                    + "Try to connect and update the database using the old username and password...");
            WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
            // Might be trying to use the wrong password, try again with old password and update it
            props = new Properties();
            conn = DriverManager.getConnection(connection, props);
            Statement state = conn.createStatement();
            state.execute("CREATE USER wildlog PASSWORD 'wildlog' ADMIN");
            state.close();
            WildLogApp.LOGGER.log(Level.INFO, "Database username and password updated.");
        }
    }
    
    
    @Override
    protected void setupAuditInfo(DataObjectWithAudit inDataObjectWithAudit) {
        inDataObjectWithAudit.setAuditTime(new Date().getTime());
        inDataObjectWithAudit.setAuditUser(WildLogApp.WILDLOG_USER_NAME);
    }

    @Override
    public void doBackup(Path inDestinationFolder) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Create the folders
            Files.createDirectories(inDestinationFolder);
            // Create a database file backup
            state.execute("BACKUP TO '" + inDestinationFolder.resolve(BACKUP_H2).toAbsolutePath().toString() + "'");
            // Create a SQL dump
            state.execute("SCRIPT DROP TO '" + inDestinationFolder.resolve(BACKUP_SQL).toAbsolutePath().toString() + "' COMPRESSION ZIP");
        }
        catch (SQLException ex) {
            printSQLException(ex);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
    public void doRestore(Path inSourceFolder) throws Exception {
        if (inSourceFolder.getFileName().toString().equalsIgnoreCase(BACKUP_H2)) {
            // Restore a database file backup
            // Maak eers die DB toe
            conn.createStatement().execute("SHUTDOWN"); // Die close call werk nie altyd vinnig genoeg nie, so ek stuur ook die command
            close();
            // Copy dan die nuwe file
            UtilsCompression.unzipFile(inSourceFolder, WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath());
        }
        else
        if (inSourceFolder.getFileName().toString().equalsIgnoreCase(BACKUP_SQL)) {
            // Restore a SQL dump
            Statement state = null;
            try {
                state = conn.createStatement();
                // NOTE: Voor v5 het die backups nie die drop statements in gehad nie, so die ou scripts gan nie automaties reg run nie...
                state.execute("RUNSCRIPT FROM '" + inSourceFolder.toAbsolutePath().toString() + "' COMPRESSION ZIP");
            }
            catch (SQLException ex) {
                printSQLException(ex);
                throw ex;
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
                    throw sqle;
                }
            }
        }
    }

    @Override
    public void doExportFullCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting, List<Sighting> inLstSightings) {
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
                if (inExportAll) {
                    inPath = inPath.resolve("Creatures.csv");
                }
                state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Locations
            if (inExportAll || inLocation != null) {
                sql = "SELECT * "
                        + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0)) LatDecDeg"
                        + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0)) LonDecDeg"
                        + " FROM LOCATIONS";
                if (inLocation != null && inLocation.getName() != null && !inLocation.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inLocation.getName().replaceAll("'", "''") + "''";
                }
                if (inExportAll) {
                    inPath = inPath.getParent().resolve("Places.csv");
                }
                state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Visits
            if (inExportAll || inVisit != null) {
                sql = "SELECT * FROM VISITS";
                if (inVisit != null && inVisit.getName() != null && !inVisit.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inVisit.getName().replaceAll("'", "''") + "''";
                }
                if (inExportAll) {
                    inPath = inPath.getParent().resolve("Periods.csv");
                }
                state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Sightings
            if (inExportAll || inSighting != null) {
                sql = "SELECT * "
                        + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0)) LatDecDeg"
                        + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0)) LonDecDeg"
                        + " FROM SIGHTINGS";
                if (inSighting != null && inSighting.getID() > 0) {
                    sql = sql + " WHERE ID = " +  inSighting.getID();
                }
                if (inExportAll) {
                    inPath = inPath.getParent().resolve("Observations.csv");
                }
                state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export List of Sightings
            if (inLstSightings != null && !inLstSightings.isEmpty()) {
                sql = "SELECT * "
                        + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0)) LatDecDeg"
                        + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0)) LonDecDeg"
                        + " FROM SIGHTINGS";
                sql = sql + " WHERE ID in (";
                for (Sighting tempSighting : inLstSightings) {
                    sql = sql + tempSighting.getID() + ",";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                state.execute("CALL CSVWRITE('" + inPath.toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Files
            if (inExportAll) {
                state.execute("CALL CSVWRITE('" + inPath.getParent().resolve("Files.csv").toAbsolutePath().toString() + "', 'SELECT * FROM FILES')");
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
                        " L.NAME AS PLACE, L.GPSACCURACY AS PLACE_GPS_ACCURACY, L.GPSACCURACYVALUE AS PLACE_GPS_ACCURACY_VALUE, " +
                        " ((CASE WHEN L.LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN L.LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (L.LatDEGREES + (L.LatMINUTES + L.LatSECONDS /60.0)/60.0)) AS PLACE_LATITUDE, " +
                        " ((CASE WHEN L.LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN L.LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (L.LonDEGREES + (L.LonMINUTES + L.LonSECONDS /60.0)/60.0)) AS PLACE_LONGITUDE, " +
                        " V.NAME AS PERIOD, V.VISITTYPE AS PERIOD_TYPE, V.STARTDATE AS PERIOD_START_DATE, V.ENDDATE AS PERIOD_END_DATE, V.DESCRIPTION AS PERIOD_DESCRIPTION, " +
                        " S.ID AS OBSERVATION, S.CERTAINTY, S.SIGHTINGEVIDENCE AS EVIDENCE, " +
                        " S.TIMEACCURACY AS TIME_ACCURACY, S.TIMEOFDAY AS TIME_OF_DAY, " +
                        " trunc(S.SIGHTINGDATE) OBSERVATION_DATE, cast(S.SIGHTINGDATE as time) OBSERVATION_TIME, " +
                        " S.GPSACCURACY AS OBSERVATION_GPS_ACCURACY, S.GPSACCURACYVALUE AS OBSERVATION_GPS_ACCURACY_VALUE, " +
                        " ((CASE WHEN S.LATITUDEINDICATOR like ''North (+)'' THEN +1 WHEN S.LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * (S.LatDEGREES + (S.LatMINUTES + S.LatSECONDS /60.0)/60.0)) AS OBSERVATION_LATITUDE, " +
                        " ((CASE WHEN S.LONGITUDEINDICATOR like ''East (+)'' THEN +1 WHEN S.LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * (S.LonDEGREES + (S.LonMINUTES + S.LonSECONDS /60.0)/60.0)) AS OBSERVATION_LONGITUDE, " +
                        " S.NUMBEROFELEMENTS AS NUMBER_OF_CREATURES, S.LIFESTATUS AS LIFE_STATUS, S.TAG, S.DETAILS " +
                        " FROM SIGHTINGS S " +
                        " LEFT JOIN ELEMENTS E ON S.ELEMENTID = E.ID " +
                        " LEFT JOIN LOCATIONS L ON S.LOCATIONID = L.ID " +
                        " LEFT JOIN VISITS V ON S.VISITID = V.ID ";
            String andIndicator = " WHERE ";
            if (inElement != null && inElement.getPrimaryName()!= null && !inElement.getPrimaryName().isEmpty()) {
                sql = sql + andIndicator + " S.ELEMENTID = ''" + inElement.getID() + "''";
                andIndicator = " AND ";
            }
            if (inLocation != null && inLocation.getName() != null && !inLocation.getName().isEmpty()) {
                sql = sql + andIndicator + " S.LOCATIONID = ''" + inLocation.getID() + "''";
                andIndicator = " AND ";
            }
            if (inVisit != null && inVisit.getName() != null && !inVisit.getName().isEmpty()) {
                sql = sql + andIndicator + " S.VISITID = ''" + inVisit.getID() + "''";
                andIndicator = " AND ";
            }
            if (inSighting != null && inSighting.getID()> 0) {
                sql = sql + andIndicator + " S.ID = " + inSighting.getID();
                andIndicator = " AND ";
            }
            if (inLstSightings != null && !inLstSightings.isEmpty()) {
                sql = sql + andIndicator + " S.ID IN (";
                for (Sighting tempSighting : inLstSightings) {
                    sql = sql + tempSighting.getID() + ",";
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
    public void doImportCSV(Path inCSVPath, boolean inAutoResolve, boolean includeWildLogFilesTable) {
        WorkspaceImportDialog importDialog = new WorkspaceImportDialog();
        Statement state = null;
        ResultSet results = null;
        try {
            Path feedbackFile = WildLogPaths.getFullWorkspacePrefix().resolve("CSVImportReport.txt");
            PrintWriter feedback = null;
            try {
                feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                feedback.println("---------------------------------------------");
                feedback.println("------------- CSV Import Report -------------");
                feedback.println("---------------------------------------------");
                feedback.println("");
                state = conn.createStatement();
                // Import Elements
                if (Files.exists(inCSVPath.resolve("Creatures.csv").toAbsolutePath())) {
                    results = state.executeQuery("CALL CSVREAD('" + inCSVPath.resolve("Creatures.csv").toAbsolutePath().toString() + "')");
                    while (results.next()) {
                        Element tempElement = new Element();
                        populateElement(results, tempElement);
                        importDialog.importElementRecord(tempElement, inAutoResolve, feedback);
                    }
                    results.close();
                }
                // Import Locations
                if (Files.exists(inCSVPath.resolve("Places.csv").toAbsolutePath())) {
                    results = state.executeQuery("CALL CSVREAD('" + inCSVPath.resolve("Places.csv").toAbsolutePath().toString() + "')");
                    while (results.next()) {
                        Location tempLocation = new Location();
                        populateLocation(results, tempLocation);
                        importDialog.importLocationRecord(tempLocation, inAutoResolve, feedback);
                    }
                    results.close();
                }
                // Import Visits
                if (Files.exists(inCSVPath.resolve("Periods.csv").toAbsolutePath())) {
                    results = state.executeQuery("CALL CSVREAD('" + inCSVPath.resolve("Periods.csv").toAbsolutePath().toString() + "')");
                    while (results.next()) {
                        Visit tempVisit = new Visit();
                        populateVisit(results, tempVisit, false);
                        importDialog.importVisitRecord(tempVisit, inAutoResolve, feedback);
                    }
                    results.close();
                }
                // Import Sightings
                if (Files.exists(inCSVPath.resolve("Observations.csv").toAbsolutePath())) {
                    results = state.executeQuery("CALL CSVREAD('" + inCSVPath.resolve("Observations.csv").toAbsolutePath().toString() + "')");
                    while (results.next()) {
                        Sighting tempSighting = new Sighting();
                        populateSighting(results, tempSighting, false);
                        importDialog.importSightingRecord(tempSighting, inAutoResolve, feedback);
                    }
                    results.close();
                }
                if (includeWildLogFilesTable) {
                    if (Files.exists(inCSVPath.resolve("Files.csv").toAbsolutePath())) {
                        results = state.executeQuery("CALL CSVREAD('" + inCSVPath.resolve("Files.csv").toAbsolutePath().toString() + "')");
                        while (results.next()) {
                            WildLogFile wildLogFile = new WildLogFile();
                            populateWildLogFile(results, wildLogFile);
                            createWildLogFile(wildLogFile, true);
                        }
                    }
                }
                importDialog.writeImportSummary(feedback);
            }
            catch (Exception ex) {
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("--------------- ERROR ----------------");
                    feedback.println(ex.toString());
                    feedback.println("--------------------------------------");
                    feedback.println("");
                }
                throw ex;
            }
            finally {
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("-------------- FINISHED --------------");
                    feedback.println("--------------------------------------");
                    feedback.println("");
                    feedback.flush();
                    feedback.close();
                    // Open the summary document
                    UtilsFileProcessing.openFile(feedbackFile);
                }
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                    "Could not import the CSV files.",
                    "Import CSV Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                    "Could not import the CSV files.",
                    "Import CSV Error", JOptionPane.ERROR_MESSAGE);
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
    public void doImportBasicCSV(Path inPath) {
        Statement state = null;
        ResultSet resultSet = null;
        try {
            Path feedbackFile = WildLogPaths.getFullWorkspacePrefix().resolve("BasicCSVImportReport.txt");
            PrintWriter feedback = null;
            try {
                int importElementCreated = 0;
                int importLocationCreated = 0;
                int importVisitCreated = 0;
                int importSightingCreated = 0;
                int importElementUpdated = 0;
                int importLocationUpdated = 0;
                int importVisitUpdated = 0;
                int importSightingUpdated = 0;
                feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                feedback.println("---------------------------------------------------");
                feedback.println("------------- Basic CSV Import Report -------------");
                feedback.println("---------------------------------------------------");
                feedback.println("");
                state = conn.createStatement();
                resultSet = state.executeQuery("CALL CSVREAD('" + inPath.toAbsolutePath().toString() + "')");
                while (resultSet.next()) {
                    // Import Elements
                    boolean isNew = false;
                    Element element = findElement(0, resultSet.getString("CREATURE").trim(), Element.class);
                    if (element == null) {
                        element = new Element();
                        element.setPrimaryName(resultSet.getString("CREATURE").trim());
                        isNew = true;
                    }
                    element.setScientificName(resultSet.getString("SCIENTIFIC_NAME"));
                    element.setType(ElementType.getEnumFromText(resultSet.getString("CREATURE_TYPE")));
                    if (isNew) {
                        createElement(element, false);
                        importElementCreated++;
                        feedback.println("Created Creature: " + element.getDisplayName() + " [" + element.getID() + "]");
                    }
                    else {
                        updateElement(element, element.getPrimaryName(), false);
                        importElementUpdated++;
                        feedback.println("Updated Creature: " + element.getDisplayName() + " [" + element.getID() + "]");
                    }
                    // Import Locations
                    isNew = false;
                    Location location = findLocation(0, resultSet.getString("PLACE").trim(), Location.class);
                    if (location == null) {
                        location = new Location();
                        location.setName(resultSet.getString("PLACE").trim());
                        isNew = true;
                    }
                    location.setGPSAccuracy(GPSAccuracy.getEnumFromText(resultSet.getString("PLACE_GPS_ACCURACY")));
                    location.setGPSAccuracyValue(resultSet.getDouble("PLACE_GPS_ACCURACY_VALUE"));
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
                    if (isNew) {
                        createLocation(location, false);
                        importLocationCreated++;
                        feedback.println("Created Place: " + location.getDisplayName() + " [" + location.getID() + "]");
                    }
                    else {
                        updateLocation(location, location.getName(), false);
                        importLocationUpdated++;
                        feedback.println("Updated Place: " + location.getDisplayName() + " [" + location.getID() + "]");
                    }
                    // Import Visits
                    isNew = false;
                    Visit visit = findVisit(0, resultSet.getString("PERIOD").trim(), false, Visit.class);
                    if (visit == null) {
                        visit = new Visit();
                        visit.setName(resultSet.getString("PERIOD").trim());
                        isNew = true;
                    }
                    visit.setLocationID(location.getID());
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
                    if (isNew) {
                        createVisit(visit, false);
                        importVisitCreated++;
                        feedback.println("Created Period: " + visit.getDisplayName() + " [" + visit.getID() + "]");
                    }
                    else {
                        updateVisit(visit, visit.getName(), false);
                        importVisitUpdated++;
                        feedback.println("Updated Period: " + visit.getDisplayName() + " [" + visit.getID() + "]");
                    }
                    // Import Sightings
                    isNew = false;
                    Sighting sighting = findSighting(resultSet.getLong("OBSERVATION"), false, Sighting.class);
                    if (sighting == null) {
                        sighting = new Sighting();
                        isNew = true;
                    }
                    sighting.setID(0); // Indicate a new Sighting ID needs to be created
                    sighting.setElementID(element.getID());
                    sighting.setLocationID(location.getID());
                    sighting.setVisitID(visit.getID());
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
                    sighting.setGPSAccuracyValue(resultSet.getDouble("OBSERVATION_GPS_ACCURACY_VALUE"));
                    lat = resultSet.getDouble("OBSERVATION_LATITUDE");
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
                    lon = resultSet.getDouble("OBSERVATION_LONGITUDE");
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
                    if (isNew) {
                        createSighting(sighting, false);
                        importSightingCreated++;
                        feedback.println("Created Observation: " + sighting.getDisplayName() + " [" + sighting.getID() + "]");
                    }
                    else {
                        createSighting(sighting, false);
                        importSightingUpdated++;
                        feedback.println("Updated Observation: " + sighting.getDisplayName() + " [" + sighting.getID() + "]");
                    }
                }
                int importCreates = importElementCreated + importLocationCreated + importVisitCreated + importSightingCreated;
                int importUpdates = importElementUpdated + importLocationUpdated + importVisitUpdated + importSightingUpdated;
                WildLogApp.LOGGER.log(Level.INFO, "Created {} and updated {} records successfully using the Basic CSV Import.", importCreates, importUpdates);
                feedback.println("");
                feedback.println("-------------- SUMMARY --------------");
                feedback.println("TOTAL CREATED            : " + importCreates);
                feedback.println("TOTAL UPDATED            : " + importUpdates);
                feedback.println("");
                feedback.println("Places created           : " + importLocationCreated);
                feedback.println("Places updated           : " + importLocationUpdated);
                feedback.println("Periods created          : " + importVisitCreated);
                feedback.println("Periods updated          : " + importVisitUpdated);
                feedback.println("Creatures created        : " + importElementCreated);
                feedback.println("Creatures updated        : " + importElementUpdated);
                feedback.println("Observations created     : " + importSightingCreated);
                feedback.println("Observations updated     : " + importSightingUpdated);
            }
            catch (Exception ex) {
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("--------------- ERROR ----------------");
                    feedback.println(ex.toString());
                    feedback.println("--------------------------------------");
                    feedback.println("");
                }
                throw ex;
            }
            finally {
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("-------------- FINISHED --------------");
                    feedback.println("--------------------------------------");
                    feedback.println("");
                    feedback.flush();
                    feedback.close();
                    // Open the summary document
                    UtilsFileProcessing.openFile(feedbackFile);
                }
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                    "Could not import the Basic CSV file.",
                    "Import Basic CSV Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                    "Could not import the Basic CSV file.",
                    "Import Basic CSV Error", JOptionPane.ERROR_MESSAGE);
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
                            success = success && updateElement(elementToSave, oldName, false);
                        }
                        else
                        if (!isExisting && inAddNewElements) {
                            success = success && createElement(elementToSave, false);
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
    public boolean deleteVisit(long inID) {
        // First check if this was visit with stashed files, if it was then delete the files firts
        Visit visit = findVisit(inID, null, false, Visit.class);
        try {
            if (VisitType.STASHED == visit.getType()) {
                UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName()).toFile());
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return super.deleteVisit(inID);
    }

    @Override
    public boolean deleteWildLogFile(long inID) {
        // Note: This method only deletes one file at a time, and all it's "default" thumbnails.
        // First, get the path of the original file (to delete it from the disk)
        String dbFilePath = findWildLogFile(inID, 0, null, null, WildLogFile.class).getDBFilePath();
        // Next, remove the database entry.
        super.deleteWildLogFile(inID);
        // Next, delete the original image
        try {
            Files.deleteIfExists(WildLogPaths.getFullWorkspacePrefix().resolve(dbFilePath).normalize().toAbsolutePath().normalize());
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        // Then, try to delete the "default/known" thumbnails.
        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
            try {
                // Note: Ek wil hier net die path kry, nie die thumbnail generate nie (so ek gebruik nie WildLogFile.getAbsoluteThumbnailPath() nie).
                Files.deleteIfExists(UtilsImageProcessing.calculateAbsoluteThumbnailPath(dbFilePath, size));
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        return true;
    }

    private void doUpdates() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Statement state = null;
        ResultSet results = null;
        try {
            BusyDialog busyDialog = new BusyDialog("WildLog Upgrade");
            state = conn.createStatement();
            results = state.executeQuery("SELECT * FROM WILDLOG");
            // If there isn't a row create one
            if (!results.next()) {
                createWildLogOptions();
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
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN UPLOADLOGS");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN BUNDLEDPLAYERS");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN USEINDVCOUNTINPATH");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN DEFAULTZOOM");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN ID");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN AUDITTIME");
                    state.execute("ALTER TABLE WILDLOG DROP COLUMN AUDITUSER");
                    // Set the version number to trigger the updates
                    state.executeUpdate("UPDATE WILDLOG SET VERSION=1");
                }
            }
            // Read the row
            boolean databaseAndApplicationInSync = false;
            boolean wasMajorUpgrade = false;
            boolean upgradeWasDone = false;
            boolean upgradeSuccess = true;
            int choice = JOptionPane.CANCEL_OPTION;
            for (int t = 0; t <= WILDLOG_DB_VERSION; t++) {
                if (!upgradeSuccess) {
                    WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                            "<html>There was an unexpected problem during the database upgrade!</html>",
                            "WildLog Upgrade Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                closeStatement(state);
                state = conn.createStatement();
                results = state.executeQuery("SELECT VERSION FROM WILDLOG");
                if (results.next()) {
                    int currentDBVersion = results.getInt("VERSION");
                    if (currentDBVersion > WILDLOG_DB_VERSION) {
                        // The application codebase is older than the database version, need to update the application first
                        databaseAndApplicationInSync = false;
                        break;
                    }
                    else {
                        // The database and application versions are in sync
                        if (currentDBVersion == WILDLOG_DB_VERSION) {
                            databaseAndApplicationInSync = true;
                            break;
                        }
                        else {
                            // The database will need to be upgraded
                            if (!upgradeWasDone) { // Only show the popup once
                                choice = WLOptionPane.showConfirmDialog(WildLogApp.getApplication().getMainFrame(),
                                        "<html>The Workspace at <b>" + WildLogPaths.getFullWorkspacePrefix().toString() + "</b> needs to be upgraded. "
                                                + "<br/>It is recommended to first make a manual backup (make a copy) of the Workspace before continuing, "
                                                + "in particular the WildLog\\Data and WildLog\\Files folders."
                                                + "<br/>Note that the upgrade can take a while to complete. WildLog will open automatically once the upgrade is complete."
                                                + "<br/><b>Press OK when you are ready to upgrade the Workspace.</b></html>",
                                        "Upgrade WildLog Database Structure", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                            }
                            if (choice == JOptionPane.OK_OPTION) {
                                if (!upgradeWasDone) {
                                    busyDialog.setVisible(true);
                                }
                                // If an upgrade is needed, then it may be best to close the server mode connection and reopen it in single mode instead, 
                                // otherwise the upgrade might start twice if people launch the app twice...
                                close();
                                setupConnection(false);
                                // Procede with the needed updates
                                if (currentDBVersion == 0) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v0 (before upgrade to 1)"));
                                    upgradeSuccess = doUpdate1();
                                }
                                else
                                if (currentDBVersion == 1) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v1 (before upgrade to 2)"));
                                    upgradeSuccess = doUpdate2();
                                }
                                else
                                if (currentDBVersion == 2) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v2 (before upgrade to 3)"));
                                    upgradeSuccess = doUpdate3();
                                }
                                else
                                if (currentDBVersion == 3) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v3 (before upgrade to 4)"));
                                    upgradeSuccess = doUpdate4();
                                }
                                else
                                if (currentDBVersion == 4) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v4 (before upgrade to 5)"));
                                    upgradeSuccess = doUpdate5();
                                }
                                else
                                if (currentDBVersion == 5) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v5 (before upgrade to 6)"));
                                    upgradeSuccess = doUpdate6();
                                    wasMajorUpgrade = true;
                                }
                                else
                                if (currentDBVersion == 6) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v6 (before upgrade to 7)"));
                                    upgradeSuccess = doUpdate7();
                                }
                                else
                                if (currentDBVersion == 7) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v7 (before upgrade to 8)"));
                                    upgradeSuccess = doUpdate8();
                                    wasMajorUpgrade = true; // Omdat die Files se folder struktuur verander het...
                                }
                                else
                                if (currentDBVersion == 8) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v8 (before upgrade to 9)"));
                                    upgradeSuccess = doUpdate9();
                                }
                                else
                                if (currentDBVersion == 9) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v9 (before upgrade to 10)"));
                                    upgradeSuccess = doUpdate10();
                                }
                                else
                                if (currentDBVersion == 10) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v10 (before upgrade to 11)"));
                                    upgradeSuccess = doUpdate11();
                                }
                                else
                                if (currentDBVersion == 11) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v11 (before upgrade to 12)"));
                                    upgradeSuccess = doUpdate12();
                                    wasMajorUpgrade = true; // Omdat die GUIDs by gekom het en baie koelomme verwyder was
                                }
                                else
                                if (currentDBVersion == 12) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v12 (before upgrade to 13)"));
                                    upgradeSuccess = doUpdate13();
                                }
                                else
                                if (currentDBVersion == 13) {
                                    doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v13 (before upgrade to 14)"));
                                    upgradeSuccess = doUpdate14();
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
            busyDialog.setVisible(false);
            busyDialog.dispose();
            if (databaseAndApplicationInSync) {
                if (upgradeWasDone) {
                    if (wasMajorUpgrade) {
                        WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                "<html>The Workspace has been <b>successfully upgraded</b> to be compatible with <b>WildLog v" + WildLogApp.WILDLOG_VERSION + "</b>. "
                                        + "<br/>Please consider running the <i>'Check and Clean the Workspace'</i> process as well. "
                                        + "<br/>(The feature is accessable from the 'Application' menu at the top of the window.)</html>",
                                "WildLog v" + WildLogApp.WILDLOG_VERSION + " - Major Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                "<html>The Workspace has been <b>successfully upgraded</b> to be compatible with <b>WildLog v" + WildLogApp.WILDLOG_VERSION + "</b>.</html>",
                                "WildLog v" + WildLogApp.WILDLOG_VERSION + " - Minor Upgrade Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            else {
                WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                        "<html>The database could not be successfully upgraded!"
                                + "<br/>Make sure that you are running the latest version of WildLog."
                                + "<br/>Confirm that the Workspace isn't already open by another WildLog instance."
                                + "<br/>It is possible that the datbase might be broken or corrupted. "
                                + "<br/>If this is the case you can restore a backup copy and try again, please consult the Manual for details."
                                + "<br/>Contact support@mywild.co.za if the problem persists.</html>",
                        "WildLog Upgrade Error", JOptionPane.ERROR_MESSAGE);
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
    private boolean doUpdate1() {
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
            state.execute(Legacy_DBI_JDBC.tableElements);
            state.execute(Legacy_DBI_JDBC.tableLocations);
            state.execute(Legacy_DBI_JDBC.tableVisits);
            state.execute(Legacy_DBI_JDBC.tableSightings);
            state.execute(Legacy_DBI_JDBC.tableFiles);
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 1");
        return true;
    }

    private boolean doUpdate2() {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 2");
        return true;
    }

    private boolean doUpdate3() {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 3");
        return true;
    }

    private boolean doUpdate4() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 4");
        // This update removes and alters some tables and adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            Legacy_DBI_JDBC legacy_DBI_JDBC = new Legacy_DBI_JDBC(conn);
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
                Legacy_SightingCore sighting = legacy_DBI_JDBC.findSighting(results.getLong("SIGHTINGCOUNTER"), Legacy_SightingCore.class);
                sighting.setTimeAccuracy(TimeAccuracy.UNKNOWN);
                legacy_DBI_JDBC.updateSighting(sighting);
            }
            results.close();
            results = state.executeQuery("SELECT SIGHTINGCOUNTER FROM SIGHTINGS WHERE UNKNOWNTIME = 0");
            while (results.next()) {
                Legacy_SightingCore sighting = legacy_DBI_JDBC.findSighting(results.getLong("SIGHTINGCOUNTER"), Legacy_SightingCore.class);
                sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                legacy_DBI_JDBC.updateSighting(sighting);
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
            List<Legacy_SightingCore> lstSightings = legacy_DBI_JDBC.listSightings(0, null, null, null, false, Legacy_SightingCore.class);
            for (Legacy_SightingCore sighting : lstSightings) {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 4");
        return true;
    }
    
    private boolean doUpdate5() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 5");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            Legacy_DBI_JDBC legacy_DBI_JDBC = new Legacy_DBI_JDBC(conn);
            state = conn.createStatement();
            // Make changes to WildLog Options
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USESCIENTIFICNAMES smallint DEFAULT true");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN WORKSPACENAME varchar(50) DEFAULT 'WildLog Workspace'");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN WORKSPACEID bigint DEFAULT 0");
            WildLogOptions options = legacy_DBI_JDBC.findWildLogOptions(WildLogOptions.class);
            options.setWorkspaceID(generateID());
            legacy_DBI_JDBC.updateWildLogOptions(options);
            // Recalculate all sun and moon phase info (the enums changed)
            List<Legacy_SightingCore> lstSightings = legacy_DBI_JDBC.listSightings(0, null, null, null, false, Legacy_SightingCore.class);
            for (Legacy_SightingCore sighting : lstSightings) {
                // Check if time is usable
                if (sighting.getDate() != null && sighting.getTimeAccuracy() != null && sighting.getTimeAccuracy().isUsableTime()) {
                    // Moon phase
                    sighting.setMoonPhase(AstroCalculator.getMoonPhase(sighting.getDate()));
                    // Check if GPS is usable
                    if (sighting.getLatitude() != null && !sighting.getLatitude().equals(Latitudes.NONE)
                            && sighting.getLongitude() != null && !sighting.getLongitude().equals(Longitudes.NONE)) {
                        double latitude = UtilsGPS.getDecimalDegree(sighting.getLatitude(), sighting.getLatDegrees(), sighting.getLatMinutes(), sighting.getLatSeconds());
                        double longitude = UtilsGPS.getDecimalDegree(sighting.getLongitude(), sighting.getLonDegrees(), sighting.getLonMinutes(), sighting.getLonSeconds());
                        // Sun Light
                        sighting.setTimeOfDay(AstroCalculator.getSunCategory(sighting.getDate(), latitude, longitude));
                        // Moon Light
                        sighting.setMoonlight(AstroCalculator.getMoonlight(sighting.getDate(), latitude, longitude));
                    }
                }
                legacy_DBI_JDBC.updateSighting(sighting);
            }
            // Increase the cache size slightly (doubled) in KB
            state.execute("SET CACHE_SIZE 32768");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=5");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 5");
        return true;
    }
    
    private boolean doUpdate6() {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 6");
        return true;
    }
    
    private boolean doUpdate7() {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 7");
        return true;
    }
    
    private boolean doUpdate8() {
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
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 8");
        return true;
    }
    
    private boolean doUpdate9() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 9");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the new columns
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USEINDVCOUNTINPATH smallint DEFAULT false");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=9");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 9");
        return true;
    }
    
    private boolean doUpdate10() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 10");
        // This update added the new INATURALIST table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // The INATURALIST is created automatically, if absent
            // There are no other changes...
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=10");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 10");
        return true;
    }
    
    private boolean doUpdate11() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 11");
        // This update added the new INATURALIST table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the Accuracy column to sightings and locations
            state.execute("ALTER TABLE LOCATIONS ADD COLUMN GPSACCURACYVALUE double DEFAULT 0");
            state.execute("ALTER TABLE SIGHTINGS ADD COLUMN GPSACCURACYVALUE double DEFAULT 0");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=11");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 11");
        return true;
    }
    
    private boolean doUpdate12() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 12");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Increase the cache size to 75MB (in KB)
            state.execute("SET CACHE_SIZE 76800");
            // Add the offline map zoom level
            state.execute("ALTER TABLE WILDLOG ADD COLUMN DEFAULTZOOM double DEFAULT 20.0");
            // Copy the data from the columns that will be deleted into the description field - to not loose it completely
            try {
                state.execute("UPDATE ELEMENTS \n"
                        + "SET DIAGNOSTICDESCRIPTION = CONCAT(DIAGNOSTICDESCRIPTION, \n"
                        + "SELECT UPGRADE_VALUES FROM (\n"
                        + "SELECT PRIMARYNAME UPGRADE_PRIMARYNAME, \n"
                        + "concat(\n"
                        + "'\n\n"
                        + "----- WildLog v6 Upgrade -----', \n"
                        + "case when (WATERDEPENDANCE is null or length(WATERDEPENDANCE) = 0) then '' else concat('\n"
                        + "WATERDEPENDANCE: ', WATERDEPENDANCE) end, \n"
                        + "case when (SIZEMALEMIN is null or SIZEMALEMIN = 0) then '' else concat('\n"
                        + "SIZEMALEMIN: ', SIZEMALEMIN) end, \n"
                        + "case when (SIZEMALEMAX is null or SIZEMALEMAX = 0) then '' else concat('\n"
                        + "SIZEMALEMAX: ', SIZEMALEMAX) end, \n"
                        + "case when (SIZEFEMALEMIN is null or SIZEFEMALEMIN = 0) then '' else concat('\n"
                        + "SIZEFEMALEMIN: ', SIZEFEMALEMIN) end, \n"
                        + "case when (SIZEFEMALEMAX is null or SIZEFEMALEMAX = 0) then '' else concat('\n"
                        + "SIZEFEMALEMAX: ', SIZEFEMALEMAX) end, \n"
                        + "case when (SIZEUNIT is null or length(SIZEUNIT) = 0) then '' else concat('\n"
                        + "SIZEUNIT: ', SIZEUNIT) end, \n"
                        + "case when (SIZETYPE is null or length(SIZETYPE) = 0) then '' else concat('\n"
                        + "SIZETYPE: ', SIZETYPE) end, \n"
                        + "case when (WEIGHTMALEMIN is null or WEIGHTMALEMIN = 0) then '' else concat('\n"
                        + "WEIGHTMALEMIN: ', WEIGHTMALEMIN) end, \n"
                        + "case when (WEIGHTMALEMAX is null or WEIGHTMALEMAX = 0) then '' else concat('\n"
                        + "WEIGHTMALEMAX: ', WEIGHTMALEMAX) end, \n"
                        + "case when (WEIGHTFEMALEMIN is null or WEIGHTFEMALEMIN = 0) then '' else concat('\n"
                        + "WEIGHTFEMALEMIN: ', WEIGHTFEMALEMIN) end, \n"
                        + "case when (WEIGHTFEMALEMAX is null or WEIGHTFEMALEMAX = 0) then '' else concat('\n"
                        + "WEIGHTFEMALEMAX: ', WEIGHTFEMALEMAX) end, \n"
                        + "case when (WEIGHTUNIT is null or length(WEIGHTUNIT) = 0) then '' else concat('\n"
                        + "WEIGHTUNIT: ', WEIGHTUNIT) end, \n"
                        + "case when (BREEDINGDURATION is null or length(BREEDINGDURATION) = 0) then '' else concat('\n"
                        + "BREEDINGDURATION: ', BREEDINGDURATION) end, \n"
                        + "case when (BREEDINGNUMBER is null or length(BREEDINGNUMBER) = 0) then '' else concat('\n"
                        + "BREEDINGNUMBER: ', BREEDINGNUMBER) end, \n"
                        + "case when (WISHLISTRATING is null or length(WISHLISTRATING) = 0) then '' else concat('\n"
                        + "WISHLISTRATING: ', WISHLISTRATING) end, \n"
                        + "case when (ACTIVETIME is null or length(ACTIVETIME) = 0) then '' else concat('\n"
                        + "ACTIVETIME: ', ACTIVETIME) end, \n"
                        + "case when (ADDFREQUENCY is null or length(ADDFREQUENCY) = 0) then '' else concat('\n"
                        + "ADDFREQUENCY: ', ADDFREQUENCY) end, \n"
                        + "case when (LIFESPAN is null or length(LIFESPAN) = 0) then '' else concat('\n"
                        + "LIFESPAN: ', LIFESPAN) end) UPGRADE_VALUES\n"
                        + "FROM ELEMENTS)\n"
                        + "WHERE UPGRADE_PRIMARYNAME = PRIMARYNAME\n"
                        + "AND LENGTH(UPGRADE_VALUES) > 40)");
            }
            catch (SQLException ex) {
                WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
            }
            try {
                state.execute("UPDATE LOCATIONS \n"
                        + "SET DESCRIPTION = CONCAT(DESCRIPTION, \n"
                        + "SELECT UPGRADE_VALUES FROM (\n"
                        + "SELECT NAME UPGRADE_NAME, \n"
                        + "concat(\n"
                        + "'\n\n"
                        + "----- WildLog v6 Upgrade -----', \n"
                        + "case when (ACCOMMODATIONTYPE is null or length(ACCOMMODATIONTYPE) < 5) then '' else concat('\n"
                        + "ACCOMMODATIONTYPE: ', ACCOMMODATIONTYPE) end, \n"
                        + "case when (CATERING is null or length(CATERING) = 0) then '' else concat('\n"
                        + "CATERING: ', CATERING) end, \n"
                        + "case when (CONTACTNUMBERS is null or length(CONTACTNUMBERS) = 0) then '' else concat('\n"
                        + "CONTACTNUMBERS: ', CONTACTNUMBERS) end, \n"
                        + "case when (WEBSITE is null or length(WEBSITE) = 0) then '' else concat('\n"
                        + "WEBSITE: ', WEBSITE) end, \n"
                        + "case when (EMAIL is null or length(EMAIL) = 0) then '' else concat('\n"
                        + "EMAIL: ', EMAIL) end, \n"
                        + "case when (DIRECTIONS is null or length(DIRECTIONS) = 0) then '' else concat('\n"
                        + "DIRECTIONS: ', DIRECTIONS) end) UPGRADE_VALUES\n"
                        + "FROM LOCATIONS)\n"
                        + "WHERE UPGRADE_NAME = NAME\n"
                        + "AND LENGTH(UPGRADE_VALUES) > 40)");
            }
            catch (SQLException ex) {
                WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
            }
            // Setup the new tables that use GUIDs and has Audit data
            // Rename old tables
            state.execute("ALTER TABLE ELEMENTS RENAME TO TEMP_ELEMENTS");
            state.execute("ALTER TABLE LOCATIONS RENAME TO TEMP_LOCATIONS");
            state.execute("ALTER TABLE VISITS RENAME TO TEMP_VISITS");
            state.execute("ALTER TABLE SIGHTINGS RENAME TO TEMP_SIGHTINGS");
            state.execute("ALTER TABLE FILES RENAME TO TEMP_FILES");
            // Create tables using the new structure
            initialize(false);
            // Copy data accross from old tables
            results = state.executeQuery("SELECT * FROM TEMP_ELEMENTS ORDER BY PRIMARYNAME");
            while (results.next()) {
                // Populate the record from the old table
                Element element = new Element();
                element.setPrimaryName(results.getString("PRIMARYNAME"));
                element.setOtherName(results.getString("OTHERNAME"));
                element.setScientificName(results.getString("SCIENTIFICNAME"));
                element.setDescription(results.getString("DESCRIPTION"));
                element.setDistribution(results.getString("DISTRIBUTION"));
                element.setNutrition(results.getString("NUTRITION"));
                element.setDiagnosticDescription(results.getString("DIAGNOSTICDESCRIPTION"));
                element.setEndangeredStatus(EndangeredStatus.getEnumFromText(results.getString("ENDANGEREDSTATUS")));
                element.setBehaviourDescription(results.getString("BEHAVIOURDESCRIPTION"));
                element.setType(ElementType.getEnumFromText(results.getString("ELEMENTTYPE")));
                element.setFeedingClass(FeedingClass.getEnumFromText(results.getString("FEEDINGCLASS")));
                element.setReferenceID(results.getString("REFERENCEID"));
                // Save the record to the new table
                createElement(element, false);
                // Force a silly 1 millisecond sleep to make SURE the new IDs are unique
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
            }
            closeResultset(results);
            results = state.executeQuery("SELECT * FROM TEMP_LOCATIONS ORDER BY NAME");
            while (results.next()) {
                // Populate the record from the old table
                Location location = new Location();
                location.setName(results.getString("NAME"));
                location.setDescription(results.getString("DESCRIPTION"));
                location.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                location.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                location.setHabitatType(results.getString("HABITATTYPE"));
                location.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                location.setLatDegrees(results.getInt("LATDEGREES"));
                location.setLatMinutes(results.getInt("LATMINUTES"));
                location.setLatSeconds(results.getDouble("LATSECONDS"));
                location.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                location.setLonDegrees(results.getInt("LONDEGREES"));
                location.setLonMinutes(results.getInt("LONMINUTES"));
                location.setLonSeconds(results.getDouble("LONSECONDS"));
                location.setGPSAccuracy(GPSAccuracy.getEnumFromText(results.getString("GPSACCURACY")));
                location.setGPSAccuracyValue(results.getDouble("GPSACCURACYVALUE"));
                // Save the record to the new table
                createLocation(location, false);
                // Force a silly 1 millisecond sleep to make SURE the new IDs are unique
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
            }
            closeResultset(results);
            results = state.executeQuery("SELECT * FROM TEMP_VISITS ORDER BY NAME");
            while (results.next()) {
                // Populate the record from the old table
                Visit visit = new Visit();
                visit.setName(results.getString("NAME"));
                if (results.getDate("STARTDATE") != null) {
                    visit.setStartDate(new Date(results.getDate("STARTDATE").getTime()));
                }
                else {
                    visit.setStartDate(null);
                }
                if (results.getDate("ENDDATE") != null) {
                    visit.setEndDate(new Date(results.getDate("ENDDATE").getTime()));
                }
                else {
                    visit.setEndDate(null);
                }
                visit.setDescription(results.getString("DESCRIPTION"));
                visit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(results.getString("GAMEWATCHINGINTENSITY")));
                visit.setType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                Location location = findLocation(0, results.getString("LOCATIONNAME"), Location.class);
                if (location != null) {
                    visit.setLocationID(location.getID());
                }
                else {
                    WildLogApp.LOGGER.log(Level.WARN, "Could not save the Visit [" + visit.getName() + "] "
                            + "because the linked Location [" + results.getString("LOCATIONNAME") + "] could not be found.");
                    continue;
                }
                // Save the record to the new table
                createVisit(visit, false);
                // Force a silly 1 millisecond sleep to make SURE the new IDs are unique
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
            }
            closeResultset(results);
            results = state.executeQuery("SELECT * FROM TEMP_SIGHTINGS ORDER BY ELEMENTNAME, LOCATIONNAME, VISITNAME");
            while (results.next()) {
                // Populate the record from the old table
                Sighting sighting = new Sighting();
                sighting.setID(results.getLong("SIGHTINGCOUNTER"));
                if (results.getTimestamp("SIGHTINGDATE") != null) {
                    sighting.setDate(new Date(results.getTimestamp("SIGHTINGDATE").getTime()));
                }
                else {
                    sighting.setDate(null);
                }
                Element element = findElement(0, results.getString("ELEMENTNAME"), Element.class);
                if (element != null) {
                    sighting.setElementID(element.getID());
                }
                else {
                    WildLogApp.LOGGER.log(Level.WARN, "Could not save the Sighting [" + sighting.getID() + "] "
                            + "because the linked Element [" + results.getString("ELEMENTNAME") + "] could not be found.");
                    continue;
                }
                Location location = findLocation(0, results.getString("LOCATIONNAME"), Location.class);
                if (location != null) {
                    sighting.setLocationID(location.getID());
                }
                else {
                    WildLogApp.LOGGER.log(Level.WARN, "Could not save Visit [" + sighting.getID() + "] "
                            + "because the linked Location [" + results.getString("LOCATIONNAME") + "] could not be found.");
                    continue;
                }
                Visit visit = findVisit(0, results.getString("VISITNAME"), false, Visit.class);
                if (visit != null) {
                    sighting.setVisitID(visit.getID());
                }
                else {
                    WildLogApp.LOGGER.log(Level.WARN, "Could not save the Sighting [" + sighting.getID() + "] "
                            + "because the linked Visit [" + results.getString("VISITNAME") + "] could not be found.");
                    continue;
                }
                sighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                sighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                sighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                sighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                sighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                sighting.setDetails(results.getString("DETAILS"));
                sighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                sighting.setLatDegrees(results.getInt("LATDEGREES"));
                sighting.setLatMinutes(results.getInt("LATMINUTES"));
                sighting.setLatSeconds(results.getDouble("LATSECONDS"));
                sighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                sighting.setLonDegrees(results.getInt("LONDEGREES"));
                sighting.setLonMinutes(results.getInt("LONMINUTES"));
                sighting.setLonSeconds(results.getDouble("LONSECONDS"));
                sighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                sighting.setMoonlight(Moonlight.getEnumFromText(results.getString("MOONLIGHT")));
                sighting.setMoonPhase(results.getInt("MOONPHASE"));
                sighting.setTemperature(results.getDouble("TEMPERATURE"));
                sighting.setUnitsTemperature(UnitsTemperature.getEnumFromText(results.getString("TEMPERATUREUNIT")));
                sighting.setLifeStatus(LifeStatus.getEnumFromText(results.getString("LIFESTATUS")));
                sighting.setSex(Sex.getEnumFromText(results.getString("SEX")));
                sighting.setTag(results.getString("TAG"));
                sighting.setDurationMinutes(results.getInt("DURATIONMINUTES"));
                sighting.setDurationSeconds(results.getDouble("DURATIONSECONDS"));
                sighting.setGPSAccuracy(GPSAccuracy.getEnumFromText(results.getString("GPSACCURACY")));
                sighting.setGPSAccuracyValue(results.getDouble("GPSACCURACYVALUE"));
                sighting.setTimeAccuracy(TimeAccuracy.getEnumFromText(results.getString("TIMEACCURACY")));
                sighting.setAge(Age.getEnumFromText(results.getString("AGE")));
                // Save the record to the new table
                setupAuditInfo(sighting);
                createSighting(sighting, true);
                // Force a silly 1 millisecond sleep to make SURE the new IDs are unique
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
            }
            closeResultset(results);
            results = state.executeQuery("SELECT * FROM TEMP_FILES ORDER BY ID, ORIGINALPATH");
            while (results.next()) {
                // Populate the record from the old table
                WildLogFile wildLogFile = new WildLogFile();
                String oldID = results.getString("ID");
                if (oldID.startsWith("E")) {
                    Element element = findElement(0, oldID.substring(oldID.indexOf('-') + 1), Element.class);
                    if (element != null) {
                        wildLogFile.setLinkID(element.getWildLogFileID());
                        wildLogFile.setLinkType(WildLogDataType.ELEMENT);
                    }
                    else {
                        WildLogApp.LOGGER.log(Level.WARN, "Could not save the File [" + results.getString("ORIGINALPATH") + "] "
                                + "because the linked Element for [" + oldID + "] could not be found.");
                        continue;
                    }
                }
                else
                if (oldID.startsWith("L")) {
                    Location location = findLocation(0, oldID.substring(oldID.indexOf('-') + 1), Location.class);
                    if (location != null) {
                        wildLogFile.setLinkID(location.getWildLogFileID());
                        wildLogFile.setLinkType(WildLogDataType.LOCATION);
                    }
                    else {
                        WildLogApp.LOGGER.log(Level.WARN, "Could not save the File [" + results.getString("ORIGINALPATH") + "] "
                                + "because the linked Location for [" + oldID + "] could not be found.");
                        continue;
                    }
                }
                else
                if (oldID.startsWith("V")) {
                    Visit visit = findVisit(0, oldID.substring(oldID.indexOf('-') + 1), false, Visit.class);
                    if (visit != null) {
                        wildLogFile.setLinkID(visit.getWildLogFileID());
                        wildLogFile.setLinkType(WildLogDataType.VISIT);
                    }
                    else {
                        WildLogApp.LOGGER.log(Level.WARN, "Could not save the File [" + results.getString("ORIGINALPATH") + "] "
                                + "because the linked Visit for [" + oldID + "] could not be found.");
                        continue;
                    }
                }
                else
                if (oldID.startsWith("S")) {
                    Sighting sighting = findSighting(Long.parseLong(oldID.substring(oldID.indexOf('-') + 1)), false, Sighting.class);
                    if (sighting != null) {
                        wildLogFile.setLinkID(sighting.getWildLogFileID());
                        wildLogFile.setLinkType(WildLogDataType.SIGHTING);
                    }
                    else {
                        WildLogApp.LOGGER.log(Level.WARN, "Could not save the File [" + results.getString("ORIGINALPATH") + "] "
                                + "because the linked Sighting for [" + oldID + "] could not be found.");
                        continue;
                    }
                }
                else {
                    WildLogApp.LOGGER.log(Level.WARN, "Could not save the File [" + results.getString("ORIGINALPATH") + "] "
                                + "because the linked record for [" + oldID + "] could not be found.");
                        continue;
                }
                wildLogFile.setFilename(results.getString("FILENAME"));
                wildLogFile.setDBFilePath(results.getString("ORIGINALPATH").replace("\\", "/"));
                wildLogFile.setFileType(WildLogFileType.getEnumFromText(results.getString("FILETYPE")));
                if (results.getDate("UPLOADDATE") != null) {
                    wildLogFile.setUploadDate(new Date(results.getDate("UPLOADDATE").getTime()));
                }
                else {
                    wildLogFile.setUploadDate(null);
                }
                wildLogFile.setDefaultFile(results.getBoolean("ISDEFAULT"));
                if (results.getTimestamp("FILEDATE") != null) {
                    wildLogFile.setFileDate(new Date(results.getTimestamp("FILEDATE").getTime()));
                }
                else {
                    wildLogFile.setFileDate(null);
                }
                wildLogFile.setFileSize(results.getLong("FILESIZE"));
                // Save the record to the new table
                createWildLogFile(wildLogFile, false);
                // Force a silly 1 millisecond sleep to make SURE the new IDs are unique
                try {
                    Thread.sleep(1L);
                }
                catch (InterruptedException ex) {
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
            }
            closeResultset(results);
            // Drop the old tables
            state.execute("DROP TABLE TEMP_ELEMENTS");
            state.execute("DROP TABLE TEMP_LOCATIONS");
            state.execute("DROP TABLE TEMP_VISITS");
            state.execute("DROP TABLE TEMP_SIGHTINGS");
            state.execute("DROP TABLE TEMP_FILES");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=12");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 12");
        return true;
    }
    
    private boolean doUpdate13() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 13");
        // This update fixes the column type of the foreign keys on the Sightings table
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Change from varchar to bigint
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN ELEMENTID SET DATA TYPE BIGINT");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN LOCATIONID SET DATA TYPE BIGINT");
            state.execute("ALTER TABLE SIGHTINGS ALTER COLUMN VISITID SET DATA TYPE BIGINT");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=13");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 13");
        return true;
    }
    
    private boolean doUpdate14() {
        WildLogApp.LOGGER.log(Level.INFO, "Starting update 14");
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Add the audit columns to the WildLog Options table (for syncing)
            state.execute("ALTER TABLE WILDLOG ADD COLUMN ID bigint DEFAULT 1 PRIMARY KEY NOT NULL");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN AUDITTIME bigint DEFAULT 0 NOT NULL");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN AUDITUSER varchar(150) DEFAULT '' NOT NULL");
            state.executeUpdate("UPDATE WILDLOG SET ID=WORKSPACEID");
            state.executeUpdate("UPDATE WILDLOG SET AUDITTIME=0");
            state.executeUpdate("UPDATE WILDLOG SET AUDITUSER='Update14'");
            // Get rid of the uniqueness of the indexes on the name columns
            state.execute("DROP INDEX IF EXISTS V12_ELEMENT_PRINAME");
            state.execute("CREATE INDEX IF NOT EXISTS V14_ELEMENT_PRINAME ON ELEMENTS (PRIMARYNAME)");
            state.execute("DROP INDEX IF EXISTS V12_LOCATION_NAME");
            state.execute("CREATE INDEX IF NOT EXISTS V14_LOCATION_NAME ON LOCATIONS (NAME)");
            state.execute("DROP INDEX IF EXISTS V12_VISIT_NAME");
            state.execute("CREATE INDEX IF NOT EXISTS V14_VISIT_NAME ON VISITS (NAME)");
            // Update the version number
            state.executeUpdate("UPDATE WILDLOG SET VERSION=14");
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
        }
        finally {
            closeStatementAndResultset(state, results);
        }
        WildLogApp.LOGGER.log(Level.INFO, "Finished update 14");
        return true;
    }

}
