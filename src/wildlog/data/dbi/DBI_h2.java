package wildlog.data.dbi;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
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
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WishRating;
import wildlog.utils.FilePaths;


public class DBI_h2 extends DBI_JDBC {
    // Constructor
    public DBI_h2() {
        super();
        Statement state = null;
        ResultSet results = null;
        boolean started = true;
        try {
            Class.forName("org.h2.Driver").newInstance();
            Properties props = new Properties();
            props.setProperty("USER", "wildlog");
            props.setProperty("PASSWORD", "wildlog");
            conn = DriverManager.getConnection("jdbc:h2:" + FilePaths.WILDLOG_DATA.getFullPath() + "wildlog;AUTOCOMMIT=ON;IGNORECASE=TRUE", props);

            // Create tables
            // FIXME: EK kan ook 'n H2 "if not exists" command gebruik in die query in plaas van die story...
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

            super.doUpdates(); // This also creates the WildLogOptions row the first time
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
        finally {
            closeStatementAndResultset(state, results);
            if (!started) {
                JOptionPane.showMessageDialog(null, "The database could not be opened. Make sure it is not in use or broken.", "WildLog Error: Initialize Database", JOptionPane.ERROR_MESSAGE);
                Application.getInstance().exit();
            }
        }
    }

    @Override
    public void doBackup(FilePaths inFolder) {
        Statement state = null;
        try {
            state = conn.createStatement();
            // Backup
            File dirs = new File(inFolder.getFullPath() + "Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")");
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
                    + ", ((CASE WHEN LATITUDEINDICATOR like 'North (+)' THEN +1  WHEN LATITUDEINDICATOR like 'South (-)' THEN -1 END) * LatDEGREES + (LatMINUTES + LatSECONDS /60.0)/60.0) LatDecDeg"
                    + ", ((CASE WHEN LONGITUDEINDICATOR like 'East (+)' THEN +1  WHEN LONGITUDEINDICATOR like 'West (-)' THEN -1 END) * LonDEGREES + (LonMINUTES + LonSECONDS /60.0)/60.0) LonDecDeg"
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
    public void doImportCSV(String inPath, String inPrefix) {
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
                tempLocation.setLatSeconds(results.getFloat("LATSECONDSFLOAT"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSeconds(results.getFloat("LONSECONDSFLOAT"));

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
                tempSighting.setLatSeconds(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSeconds(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));

                createOrUpdate(tempSighting);
            }
            // TODO: CSV Import of Files
//            results = state.executeQuery("CALL CSVREAD('" + inPath + "Files.csv')");
//            while (results.next()) {
//                Foto tempFoto = new Foto();
//
//                tempFoto.setId(results.getString("ID").replaceFirst("-", "-" + inPrefix)); // 'location-loc1' becomes 'location-prefixloc1'
//                tempFoto.setFilename(results.getString("FILENAME"));
//                tempFoto.setFileLocation(results.getString("FILEPATH"));
//                tempFoto.setOriginalFotoLocation(results.getString("ORIGINALPATH"));
//                tempFoto.setFotoType(FotoType.getEnumFromText(results.getString("FILETYPE")));
//                tempFoto.setDate(results.getDate("UPLOADDATE"));
//                tempFoto.setDefaultFile(results.getBoolean("ISDEFAULT"));
//
//                createOrUpdate(tempFoto, false);
//            }
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

}
