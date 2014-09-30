package wildlog.data.dbi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.h2.jdbc.JdbcSQLException;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
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
                + ";AUTOCOMMIT=ON;IGNORECASE=TRUE", true);
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
                System.out.println("Could not connect to database, could be an old version. Try to connect and update the database using the old username and password...");
                ex.printStackTrace(System.out);
                // Might be trying to use the wrong password, try again with old password and update it
                props = new Properties();
                conn = DriverManager.getConnection(inConnectionURL, props);
                state = conn.createStatement();
                state.execute("CREATE USER wildlog PASSWORD 'wildlog' ADMIN");
                state.close();
                System.out.println("Database username and password updated.");
            }
            // Create table, indexes, etc.
            started = initialize(inCreateDefaultRecords);
            // Check database version and perform updates if required.
            // This also creates the WildLogOptions row the first time
            doUpdates();
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver.");
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
            started = false;
        }
        catch (InstantiationException ie) {
            System.err.println("\nUnable to instantiate the JDBC driver.");
            ie.printStackTrace(System.err);
            started = false;
        }
        catch (IllegalAccessException iae) {
            System.err.println("\nNot allowed to access the JDBC driver.");
            iae.printStackTrace(System.err);
            started = false;
        }
        catch (SQLException sqle) {
            printSQLException(sqle);
            started = false;
        }
        catch (Exception ex) {
            started = false;
            ex.printStackTrace(System.err);
        }
        finally {
            closeStatementAndResultset(state, results);
            if (!started) {
                throw new Exception("Could not open WildLog database.");
            }
        }
    }


    @Override
    public void doBackup(Path inFolder) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Backup
            File dirs = inFolder.resolve("Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")").toFile();
            dirs.mkdirs();
            // Create a database file backup
            state.execute("BACKUP TO '" + dirs.getPath() + File.separatorChar + "WildLog Backup - H2.zip'");
            // Create a SQL dump
            state.execute("SCRIPT TO '" + dirs.getPath() + File.separatorChar + "WildLog Backup - SQL.zip' COMPRESSION ZIP");
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
    public void doExportCSV(Path inPath, boolean inExportAll, Location inLocation, Visit inVisit, Element inElement, Sighting inSighting) {
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
                state.execute("CALL CSVWRITE('" + inPath.resolve("Elements.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Locations
            if (inExportAll || inLocation != null) {
                sql = "SELECT * FROM LOCATIONS";
                if (inLocation != null && inLocation.getName() != null && !inLocation.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inLocation.getName().replaceAll("'", "''") + "''";
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Locations.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Visits
            if (inExportAll || inVisit != null) {
                sql = "SELECT * FROM VISITS";
                if (inVisit != null && inVisit.getName() != null && !inVisit.getName().isEmpty()) {
                    sql = sql + " WHERE NAME = ''" + inVisit.getName().replaceAll("'", "''") + "''";
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Visits.csv").toAbsolutePath().toString() + "', '" + sql + "')");
            }
            // Export Sightings
            if (inExportAll || inSighting != null) {
                sql = "SELECT * "
                    + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1  WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0) LatDecDeg"
                    + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1  WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0) LonDecDeg"
                    + " FROM SIGHTINGS";
                if (inSighting != null && inSighting.getSightingCounter() > 0) {
                    sql = sql + " WHERE SIGHTINGCOUNTER = " +  inSighting.getSightingCounter();
                }
                state.execute("CALL CSVWRITE('" + inPath.resolve("Sightings.csv").toAbsolutePath().toString() + "', '" + sql + "')");
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
    public boolean doImportCSV(Path inPath, String inPrefix, boolean includeWildLogFilesTable) {
        Statement state = null;
        ResultSet results = null;
        boolean success = true;
        try {
            state = conn.createStatement();
            // Import Elements
            results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Elements.csv").toAbsolutePath().toString() + "')");
            while (results.next()) {
                Element tempElement = new Element();
                populateElement(results, tempElement);
                tempElement.setPrimaryName(inPrefix + results.getString("PRIMARYNAME"));
                success = success && createOrUpdate(tempElement, null);
            }
            // Import Locations
            results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Locations.csv").toAbsolutePath().toString() + "')");
            while (results.next()) {
                Location tempLocation = new Location();
                populateLocation(results, tempLocation);
                tempLocation.setName(inPrefix + results.getString("NAME"));
                success = success && createOrUpdate(tempLocation, null);
            }
            // Import Visits
            results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Visits.csv").toAbsolutePath().toString() + "')");
            while (results.next()) {
                Visit tempVisit = new Visit();
                populateVisit(results, tempVisit);
                tempVisit.setName(inPrefix + results.getString("NAME"));
                tempVisit.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                success = success && createOrUpdate(tempVisit, null);
            }
            // Import Sightings
            results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Sightings.csv").toAbsolutePath().toString() + "')");
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                populateSighting(results, tempSighting);
                tempSighting.setSightingCounter(0);
                tempSighting.setElementName(inPrefix + results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(inPrefix + results.getString("VISITNAME"));
                success = success && createOrUpdate(tempSighting, false);
            }
            if (includeWildLogFilesTable) {
                results = state.executeQuery("CALL CSVREAD('" + inPath.resolve("Files.csv").toAbsolutePath().toString() + "')");
                while (results.next()) {
                    WildLogFile wildLogFile = new WildLogFile();
                    populateWildLogFile(results, wildLogFile);
                    wildLogFile.setId(results.getString("ID").replaceFirst("-", "-" + inPrefix)); // 'location-loc1' becomes 'location-prefixloc1'
                    success = success && createOrUpdate(wildLogFile, false);
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
                Element searchElement = new Element();
                searchElement.setScientificName(sciName);
                // Twee verskillende Elements kan dieselfde spesie naam het (soos Blesbok en Bontebok)
                List<Element> lstElements = list(searchElement);
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
                                    // Twee verskillende species kan dieselfde common name het. (Append die scientific name agter aan.)
                                    if (count(elementToSave) > 0) {
                                        elementToSave.setPrimaryName(elementToSave.getPrimaryName() + " (" + sciName + ")");
                                        while (count(elementToSave) > 0) {
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
                            // Twee verskillende species kan dieselfde common name het. (Append die scientific name agter aan.)
                            if (count(elementToSave) > 0) {
                                elementToSave.setPrimaryName(elementToSave.getPrimaryName() + " (" + sciName + ")");
                                while (count(elementToSave) > 0) {
                                    elementToSave.setPrimaryName(elementToSave.getPrimaryName() + "_wl");
                                }
                            }
                        }
                        // Save the creature
                        if ((isExisting && inUpdateExistingElements) || (!isExisting && inAddNewElements)) {
                            success = success && createOrUpdate(elementToSave, oldName);
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
    public <T extends WildLogFileCore> boolean delete(T inWildLogFile) {
        // Note: This method only deletes one file at a time, and all it's "default" thumbnails.
        // First, remove the database entry.
        super.delete(inWildLogFile);
        // Next, delete the original image
        try {
            if (inWildLogFile instanceof WildLogFile) {
                Files.deleteIfExists(((WildLogFile) inWildLogFile).getAbsolutePath());
            }
            else {
                WildLogFile temp = new WildLogFile();
                temp.setDBFilePath(inWildLogFile.getDBFilePath());
                Files.deleteIfExists(temp.getAbsolutePath());
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        // Then, try to delete the "default/known" thumbnails.
        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
            try {
                // Note: Ek wil hier net die path kry, nie die thumbnail generate nie (so ek gebruik nie WildLogFile.getAbsoluteThumbnailPath() nie).
                if (inWildLogFile instanceof WildLogFile) {
                    Files.deleteIfExists(UtilsImageProcessing.calculateAbsoluteThumbnailPath((WildLogFile) inWildLogFile, size));
                }
                else {
                    WildLogFile temp = new WildLogFile();
                    temp.setDBFilePath(inWildLogFile.getDBFilePath());
                    Files.deleteIfExists(UtilsImageProcessing.calculateAbsoluteThumbnailPath(temp, size));
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
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
// FIXME: Error when opining a WildNote v1.1 export with WildLog v4.2: "Column count does not match"
                createOrUpdate(new WildLogOptions());
            }
            // Read the row
            boolean fullyUpdated = false;
            for (int t = 0; t <= WILDLOG_DB_VERSION; t++) {
                results = state.executeQuery("SELECT VERSION FROM WILDLOG");
                if (results.next()) {
                    if (results.getInt("VERSION") > WILDLOG_DB_VERSION) {
                        // The application codebase is older than the database version, need to update the application first
                        fullyUpdated = false;
                        break;
                    }
                    else
                    // Procede with teh expected updates
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
                        doBackup(WildLogPaths.WILDLOG_BACKUPS_UPGRADE.getAbsoluteFullPath().resolve("v4  (before upgrade to 5)"));
                        doUpdate5();
                    }
                    else
                    // The database and application versions are in sync
                    if (results.getInt("VERSION") == 5) {
                        fullyUpdated = true;
                        break;
                    }
                }
            }
            if (!fullyUpdated) {
                UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                "The database could not be fully updated. Make sure it is not in use or broken "
                                + "and that you are running the latest version of the application.",
                                "WildLog Error: Can't Initialize Database", JOptionPane.ERROR_MESSAGE);
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
        System.out.println("Finished update 3");
    }

    private void doUpdate4() {
        System.out.println("Starting update 4");
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
                Sighting sighting = find(new Sighting(results.getLong("SIGHTINGCOUNTER")));
                sighting.setTimeAccuracy(TimeAccuracy.UNKNOWN);
                createOrUpdate(sighting, false);
            }
            results.close();
            results = state.executeQuery("SELECT SIGHTINGCOUNTER FROM SIGHTINGS WHERE UNKNOWNTIME = 0");
            while (results.next()) {
                Sighting sighting = find(new Sighting(results.getLong("SIGHTINGCOUNTER")));
                sighting.setTimeAccuracy(TimeAccuracy.GOOD);
                createOrUpdate(sighting, false);
            }
            results.close();
            state.execute("ALTER TABLE SIGHTINGS DROP COLUMN UNKNOWNTIME");
            state.executeUpdate("UPDATE SIGHTINGS SET TIMEACCURACY = 'GOOD' WHERE TIMEACCURACY IS NULL OR TIMEACCURACY = ''");
            // Make changes to Files
            // NOTE: It is best to make sure that Clean Workspace in v3 has been run before upgrading
            state.executeUpdate("UPDATE FILES SET ORIGINALPATH = replace(regexp_replace(ORIGINALPATH, '\\\\','/'), '/WildLog/', '')");
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
            state.execute("CREATE UNIQUE INDEX IF NOT EXISTS FILE_ORGPATH ON FILES (ORIGINALPATH)");
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
            List<Sighting> listSightings = list(new Sighting());
            for (Sighting sighting : listSightings) {
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
        System.out.println("Finished update 4");
    }
    
    private void doUpdate5() {
        System.out.println("Starting update 5");
        // This update adds new wildlog options
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Make changes to WildLog Options
            state.execute("ALTER TABLE WILDLOG ADD COLUMN USESCIENTIFICNAMES smallint DEFAULT true");
            state.execute("ALTER TABLE WILDLOG ADD COLUMN WORKSPACENAME varchar(50) DEFAULT 'WildLog Workspace'");
            
// TODO: Update the sun and moon phase (recalculate it)
            
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
        System.out.println("Finished update 5");
    }

}
