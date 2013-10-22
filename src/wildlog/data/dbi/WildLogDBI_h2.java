package wildlog.data.dbi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import static wildlog.data.dbi.DBI_JDBC.WILDLOG_DB_VERSION;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
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
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;


public class WildLogDBI_h2 extends DBI_JDBC implements WildLogDBI {

    public WildLogDBI_h2(final WildLogApp inApp) throws Exception {
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
                conn = DriverManager.getConnection("jdbc:h2:" + WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath().resolve("wildlog") + ";AUTOCOMMIT=ON;IGNORECASE=TRUE", props);
            }
            catch (JdbcSQLException ex) {
                System.out.println("Could not connect to database, could be an old version. Try to connect and update the database using the old username and password...");
                ex.printStackTrace(System.out);
                // Might be trying to use the wrong password, try again with old password and update it
                props = new Properties();
                conn = DriverManager.getConnection("jdbc:h2:" + WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath().resolve("wildlog") + ";AUTOCOMMIT=ON;IGNORECASE=TRUE", props);
                state = conn.createStatement();
                state.execute("CREATE USER wildlog PASSWORD 'wildlog' ADMIN");
                state.close();
                System.out.println("Database username and password updated.");
            }

            // Create tables
            // TODO: EK kan ook 'n H2 "if not exists" command gebruik in die query in plaas van die story aangesien ek basies net H2 support as DB...
            results = conn.getMetaData().getTables(null, null, "ELEMENTS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableElements);
            }
            results = conn.getMetaData().getTables(null, null, "LOCATIONS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableLocations);
            }
            results = conn.getMetaData().getTables(null, null, "VISITS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableVisits);
            }
            results = conn.getMetaData().getTables(null, null, "SIGHTINGS", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableSightings);
            }
            results = conn.getMetaData().getTables(null, null, "FILES", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableFiles);
            }
            results = conn.getMetaData().getTables(null, null, "WILDLOG", null);
            state = conn.createStatement();
            if (!results.next()) {
                state.execute(tableWildLogOptions);
            }

            doUpdates(inApp); // This also creates the WildLogOptions row the first time
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver org.apache.derby.jdbc.EmbeddedDriver");
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
            started = false;
        }
        catch (InstantiationException ie) {
            System.err.println("\nUnable to instantiate the JDBC driver org.apache.derby.jdbc.EmbeddedDriver");
            ie.printStackTrace(System.err);
            started = false;
        }
        catch (IllegalAccessException iae) {
            System.err.println("\nNot allowed to access the JDBC driver org.apache.derby.jdbc.EmbeddedDriver");
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
    public void doBackup(WildLogPaths inFolder) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Backup
            File dirs = new File(inFolder.getAbsoluteFullPath() + "Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")");
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
    public void doExportCSV(String inPath) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Export Elements
            state.execute("CALL CSVWRITE('" + inPath + "Elements.csv', 'SELECT * FROM ELEMENTS')");
            // Export Locations
            state.execute("CALL CSVWRITE('" + inPath + "Locations.csv', 'SELECT * FROM LOCATIONS')");
            // Export Visits
            state.execute("CALL CSVWRITE('" + inPath + "Visits.csv', 'SELECT * FROM VISITS')");
            // Export Sightings
            state.execute("CALL CSVWRITE('" + inPath + "Sightings.csv', "
                    + "'SELECT * "
                    + ", ((CASE WHEN LATITUDEINDICATOR like ''North (+)'' THEN +1  WHEN LATITUDEINDICATOR like ''South (-)'' THEN -1 END) * LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0) LatDecDeg"
                    + ", ((CASE WHEN LONGITUDEINDICATOR like ''East (+)'' THEN +1  WHEN LONGITUDEINDICATOR like ''West (-)'' THEN -1 END) * LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0) LonDecDeg"
                    + " FROM SIGHTINGS')");
            // Export Files
            state.execute("CALL CSVWRITE('" + inPath + "Files.csv', 'SELECT * FROM FILES')");
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
    public boolean doImportCSV(String inPath, String inPrefix) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Import Elements
            results = state.executeQuery("CALL CSVREAD('" + inPath + "Elements.csv')");
            while (results.next()) {
                Element tempElement = new Element();
                tempElement.setPrimaryName(inPrefix + results.getString("PRIMARYNAME"));
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
                createOrUpdate(tempElement, null);
            }
            // Import Locations
            results = state.executeQuery("CALL CSVREAD('" + inPath + "Locations.csv')");
            while (results.next()) {
                Location tempLocation = new Location();
                tempLocation.setName(inPrefix + results.getString("NAME"));
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
                tempLocation.setLatSeconds(results.getFloat("LATSECONDS"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSeconds(results.getFloat("LONSECONDS"));
                createOrUpdate(tempLocation, null);
            }
            // Import Visits
            results = state.executeQuery("CALL CSVREAD('" + inPath + "Visits.csv')");
            while (results.next()) {
                Visit tempVisit = new Visit();
                tempVisit.setName(inPrefix + results.getString("NAME"));
                tempVisit.setStartDate(results.getDate("STARTDATE"));
                tempVisit.setEndDate(results.getDate("ENDDATE"));
                tempVisit.setDescription(results.getString("DESCRIPTION"));
                tempVisit.setGameWatchingIntensity(GameWatchIntensity.getEnumFromText(results.getString("GAMEWATCHINGINTENSITY")));
                tempVisit.setType(VisitType.getEnumFromText(results.getString("VISITTYPE")));
                tempVisit.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                createOrUpdate(tempVisit, null);
            }
            // Import Sightings
            results = state.executeQuery("CALL CSVREAD('" + inPath + "Sightings.csv')");
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting.setSightingCounter(0);
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(inPrefix + results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(inPrefix + results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(inPrefix + results.getString("VISITNAME"));
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
                createOrUpdate(tempSighting);
            }
            // TODO: CSV Import of Files
//            results = state.executeQuery("CALL CSVREAD('" + inPath + "Files.csv')");
//            while (results.next()) {
//                Foto tempFoto = new Foto();
//                tempFoto.setId(results.getString("ID").replaceFirst("-", "-" + inPrefix)); // 'location-loc1' becomes 'location-prefixloc1'
//                tempFoto.setFilename(results.getString("FILENAME"));
//                tempFoto.setFileLocation(results.getString("FILEPATH"));
//                tempFoto.setOriginalFotoLocation(results.getString("ORIGINALPATH"));
//                tempFoto.setFotoType(FotoType.getEnumFromText(results.getString("FILETYPE")));
//                tempFoto.setDate(results.getDate("UPLOADDATE"));
//                tempFoto.setDefaultFile(results.getBoolean("ISDEFAULT"));
//                createOrUpdate(tempFoto, false);
//            }
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
    public <T extends WildLogFileCore> boolean delete(T inWildLogFile) {
        // Note: This method only deletes one file at a time, and all it's "default" thumbnails.
        // First, remove the database entry.
        super.delete(inWildLogFile);
        // Then, try to delete the "default/known" thumbnails.
        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
            try {
                // Note: Ek wil hier net die path kry, nie die thumbnail generate nie (so ek gebruik nie WildLogFile.getAbsoluteThumbnailPath() nie).
                Files.deleteIfExists(UtilsImageProcessing.calculateAbsoluteThumbnailPath((WildLogFile) inWildLogFile, size));
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return true;
    }

    public void doUpdates(final WildLogApp inApp) {
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
                UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "The database could not be fully updated. Make sure it is not in use or broken"
                                + "and that you are running the latest version of the application.",
                                "WildLog Error: Can't Initialize Database", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
                inApp.exit();
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
            state.execute("UPDATE VISITS SET VISITTYPE  = 'Camera Trap' WHERE VISITTYPE = 'Remote Camera'");
            state.execute("UPDATE VISITS SET VISITTYPE  = 'Census, Atlas, etc.' WHERE VISITTYPE = 'Bird Atlassing'");
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

}
