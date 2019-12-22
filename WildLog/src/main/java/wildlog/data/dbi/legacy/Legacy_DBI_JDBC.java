package wildlog.data.dbi.legacy;

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
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;
import wildlog.data.utils.UtilsData;


public class Legacy_DBI_JDBC {
    protected final Random randomGenerator = new Random(System.nanoTime()); // ThreadLocalRandom is beter maar net in Java 7
    protected static final int WILDLOG_DB_VERSION = 11;
    public static final String tableElements = "CREATE TABLE ELEMENTS (PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL, OTHERNAME varchar(150), SCIENTIFICNAME varchar(150), DESCRIPTION longvarchar, DISTRIBUTION longvarchar, NUTRITION longvarchar, WATERDEPENDANCE varchar(50), SIZEMALEMIN float(52), SIZEMALEMAX float(52), SIZEFEMALEMIN float(52), SIZEFEMALEMAX float(52), SIZEUNIT varchar(10), SIZETYPE varchar(50), WEIGHTMALEMIN float(52), WEIGHTMALEMAX float(52), WEIGHTFEMALEMIN float(52), WEIGHTFEMALEMAX float(52), WEIGHTUNIT varchar(10), BREEDINGDURATION varchar(50), BREEDINGNUMBER varchar(50), WISHLISTRATING varchar(50), DIAGNOSTICDESCRIPTION longvarchar, ACTIVETIME varchar(50), ENDANGEREDSTATUS varchar(35), BEHAVIOURDESCRIPTION longvarchar, ADDFREQUENCY varchar(50), ELEMENTTYPE varchar(50), FEEDINGCLASS varchar(50), LIFESPAN varchar(50), REFERENCEID varchar(50))";
    public static final String tableLocations = "CREATE TABLE LOCATIONS (NAME varchar(150) PRIMARY KEY NOT NULL, DESCRIPTION longvarchar, RATING varchar(50), GAMEVIEWINGRATING varchar(50), HABITATTYPE longvarchar, ACCOMMODATIONTYPE varchar(150), CATERING varchar(50), CONTACTNUMBERS varchar(50), WEBSITE varchar(100), EMAIL varchar(100), DIRECTIONS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, GPSACCURACY varchar(50), GPSACCURACYVALUE double)";
    public static final String tableVisits = "CREATE TABLE VISITS (NAME varchar(150) PRIMARY KEY NOT NULL, STARTDATE date, ENDDATE date, DESCRIPTION longvarchar, GAMEWATCHINGINTENSITY varchar(50), VISITTYPE varchar(50), LOCATIONNAME varchar(150))";
    public static final String tableSightings = "CREATE TABLE SIGHTINGS (SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL, SIGHTINGDATE timestamp NOT NULL, ELEMENTNAME varchar(150) NOT NULL, LOCATIONNAME varchar(150) NOT NULL, VISITNAME varchar(150) NOT NULL, TIMEOFDAY varchar(50), WEATHER varchar(50), VIEWRATING varchar(50), CERTAINTY varchar(50), NUMBEROFELEMENTS int, DETAILS longvarchar, LATITUDEINDICATOR varchar(10), LATDEGREES int, LATMINUTES int, LATSECONDS double, LONGITUDEINDICATOR varchar(10), LONDEGREES int, LONMINUTES int, LONSECONDS double, SIGHTINGEVIDENCE varchar(50), MOONLIGHT varchar(50), MOONPHASE int, TEMPERATURE double, TEMPERATUREUNIT varchar(15), LIFESTATUS varchar(15), SEX varchar(15), TAG longvarchar, DURATIONMINUTES int, DURATIONSECONDS double, GPSACCURACY varchar(50), TIMEACCURACY varchar(50), AGE varchar(50), GPSACCURACYVALUE double)";
    public static final String tableFiles = "CREATE TABLE FILES (ID varchar(175), FILENAME varchar(255), ORIGINALPATH varchar(500), FILETYPE varchar(50), UPLOADDATE date, ISDEFAULT smallint, FILEDATE timestamp, FILESIZE bigint)";
    private static final String findSighting = "SELECT * FROM SIGHTINGS WHERE SIGHTINGCOUNTER = ?";
    private static final String findWildLogOptions = "SELECT * FROM WILDLOG";
    private static final String updateSighting = "UPDATE SIGHTINGS SET SIGHTINGCOUNTER = ?, SIGHTINGDATE = ?, ELEMENTNAME = ?, LOCATIONNAME = ?, VISITNAME = ?, TIMEOFDAY = ?, WEATHER = ?, VIEWRATING = ?, CERTAINTY = ?, NUMBEROFELEMENTS = ?, DETAILS = ?, LATITUDEINDICATOR = ?, LATDEGREES = ?, LATMINUTES = ?, LATSECONDS = ?, LONGITUDEINDICATOR = ?, LONDEGREES = ?, LONMINUTES = ?, LONSECONDS = ?, SIGHTINGEVIDENCE = ?, MOONPHASE = ?, MOONLIGHT = ?, TEMPERATURE = ?, TEMPERATUREUNIT = ?, LIFESTATUS = ?, SEX = ?, TAG = ?, DURATIONMINUTES = ?, DURATIONSECONDS = ?, GPSACCURACY = ?, GPSACCURACYVALUE = ?, TIMEACCURACY = ?, AGE = ? WHERE SIGHTINGCOUNTER = ?";
    private static final String updateWildLogOptions = "UPDATE WILDLOG SET DEFAULTLATITUDE = ?, DEFAULTLONGITUDE = ?, DEFAULTSLIDESHOWSPEED = ?, DEFAULTSLIDESHOWSIZE = ?, USETHUMBNAILTABLES = ?, USETHUMBNAILBROWSE =?, ENABLESOUNDS = ?, USESCIENTIFICNAMES = ?, WORKSPACENAME = ?, WORKSPACEID = ?, UPLOADLOGS = ?, BUNDLEDPLAYERS = ?, USEINDVCOUNTINPATH = ?";
    protected Connection conn;

    public Legacy_DBI_JDBC(Connection inConn) {
        conn = inConn;
    }

    public <T extends Legacy_SightingCore> T findSighting(long inSightingCounter, Class<T> inReturnType) {
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

    protected <T extends Legacy_SightingCore> void populateSighting(ResultSet inResults, T inSighting) throws SQLException {
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
        inSighting.setGPSAccuracyValue(inResults.getDouble("GPSACCURACYVALUE"));
        inSighting.setTimeAccuracy(TimeAccuracy.getEnumFromText(inResults.getString("TIMEACCURACY")));
        inSighting.setAge(Age.getEnumFromText(inResults.getString("AGE")));
    }

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

    public <T extends Legacy_SightingCore> List<T> listSightings(long inSightingCounter, String inElementName, 
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

    public <T extends Legacy_SightingCore> boolean updateSighting(T inSighting) {
        PreparedStatement state = null;
        try {
            // Note: No need to update the files, because once a sighting has an ID it never changes
            // Update
            state = conn.prepareStatement(updateSighting);
            maintainSighting(state, inSighting);
            state.setLong(34, inSighting.getSightingCounter());
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

    private <T extends Legacy_SightingCore> void maintainSighting(PreparedStatement state, T inSighting) throws SQLException {
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
        state.setString(6, stringFromObject(inSighting.getTimeOfDay()));
        state.setString(7, stringFromObject(inSighting.getWeather()));
        state.setString(8, stringFromObject(inSighting.getViewRating()));
        state.setString(9, stringFromObject(inSighting.getCertainty()));
        state.setInt(10, inSighting.getNumberOfElements());
        state.setString(11, UtilsData.sanitizeString(inSighting.getDetails()));
        state.setString(12, stringFromObject(inSighting.getLatitude()));
        state.setInt(13, inSighting.getLatDegrees());
        state.setInt(14, inSighting.getLatMinutes());
        state.setDouble(15, inSighting.getLatSeconds());
        state.setString(16, stringFromObject(inSighting.getLongitude()));
        state.setInt(17, inSighting.getLonDegrees());
        state.setInt(18, inSighting.getLonMinutes());
        state.setDouble(19, inSighting.getLonSeconds());
        state.setString(20, stringFromObject(inSighting.getSightingEvidence()));
        state.setInt(21, inSighting.getMoonPhase());
        state.setString(22, stringFromObject(inSighting.getMoonlight()));
        state.setDouble(23, inSighting.getTemperature());
        state.setString(24, stringFromObject(inSighting.getUnitsTemperature()));
        state.setString(25, stringFromObject(inSighting.getLifeStatus()));
        state.setString(26, stringFromObject(inSighting.getSex()));
        state.setString(27, stringFromObject(inSighting.getTag()));
        state.setInt(28, inSighting.getDurationMinutes());
        state.setDouble(29, inSighting.getDurationSeconds());
        state.setString(30, stringFromObject(inSighting.getGPSAccuracy()));
        state.setDouble(31, inSighting.getGPSAccuracyValue());
        state.setString(32, stringFromObject(inSighting.getTimeAccuracy()));
        state.setString(33, stringFromObject(inSighting.getAge()));
    }

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

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param ex the SQLException from which to print details.
     */
    private void printSQLException(SQLException ex) {
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

    private void closeStatementAndResultset(Statement inStatement, ResultSet inResultSet) {
        closeStatement(inStatement);
        closeResultset(inResultSet);
    }

    private void closeStatement(Statement inStatement) {
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

    private void closeResultset(ResultSet inResultSet) {
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

    public long generateID() {
        return System.currentTimeMillis()*1000000L + randomGenerator.nextInt(999999);
    }
    
    public String stringFromObject(Object inEnum) {
        if (inEnum == null) {
            return null;
        }
        else {
            return inEnum.toString();
        }
    }

}
