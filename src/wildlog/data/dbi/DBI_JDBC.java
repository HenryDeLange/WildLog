/*
 * DBI_JDBC.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.data.dbi;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.FotoType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Province;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.Weather;
import wildlog.data.enums.WishRating;

/**
 *
 * @author Henry
 */
public abstract class DBI_JDBC implements DBI {
    // Variables
    protected Connection conn;
    protected String createElementsTable;
    protected String createLocationsTable;
    protected String createVisitsTable;
    protected String createSightingsTable;
    protected String createFotosTable;

    public DBI_JDBC() {
        
    }

    protected void init() {
        // Create tables
        createElementsTable = "CREATE TABLE ELEMENTS "+
                            "("+
                            "   PRIMARYNAME varchar(150) PRIMARY KEY NOT NULL,"+
                            "   OTHERNAME varchar(150),"+
                            "   SCIENTIFICNAME varchar(150),"+
                            "   DESCRIPTION longvarchar,"+
                            "   NUTRITION longvarchar,"+
                            "   WATERDEPENDANCE varchar(50),"+
                            "   SIZEMALEMIN float(52),"+
                            "   SIZEMALEMAX float(52),"+
                            "   SIZEFEMALEMIN float(52),"+
                            "   SIZEFEMALEMAX float(52),"+
                            "   SIZEUNIT varchar(10),"+
                            "   WEIGHTMALEMIN float(52),"+
                            "   WEIGHTMALEMAX float(52),"+
                            "   WEIGHTFEMALEMIN float(52),"+
                            "   WEIGHTFEMALEMAX float(52),"+
                            "   WEIGHTUNIT varchar(10),"+
                            "   BREEDINGDURATION varchar(50),"+
                            "   BREEDINGNUMBER varchar(50),"+
                            "   WISHLISTRATING varchar(50),"+
                            "   DIAGNOSTICDESCRIPTION longvarchar,"+
                            "   ACTIVETIME varchar(50),"+
                            "   ENDANGEREDSTATUS varchar(35),"+
                            "   BEHAVIOURDESCRIPTION longvarchar,"+
                            "   ADDFREQUENCY varchar(50),"+
                            "   ELEMENTTYPE varchar(50),"+
                            "   FEEDINGCLASS varchar(50),"+
                            "   LIFESPAN varchar(50),"+
                            "   REFERENCEID varchar(50)"+
                            ")";
        createLocationsTable = "CREATE TABLE LOCATIONS "+
                            "("+
                            "   NAME varchar(150) PRIMARY KEY NOT NULL,"+
                            "   DESCRIPTION longvarchar,"+
                            "   PROVINCE varchar(35),"+
                            "   RATING varchar(50),"+
                            "   GAMEVIEWINGRATING varchar(50),"+
                            "   HABITATTYPE varchar(50),"+
                            "   ACCOMMODATIONTYPE varchar(150),"+
                            "   CATERING varchar(50),"+
                            "   CONTACTNUMBERS varchar(50),"+
                            "   WEBSITE varchar(100),"+
                            "   EMAIL varchar(100),"+
                            "   DIRECTIONS longvarchar,"+
                            "   LATITUDEINDICATOR varchar(10),"+
                            "   LATDEGREES int,"+
                            "   LATMINUTES int,"+
                            "   LATSECONDSFLOAT float(52),"+
                            "   LONGITUDEINDICATOR varchar(10),"+
                            "   LONDEGREES float(52),"+
                            "   LONMINUTES float(52),"+
                            "   LONSECONDSFLOAT float(52)"+
                            ")";
        createVisitsTable = "CREATE TABLE VISITS "+
                            "("+
                            "   NAME varchar(150) PRIMARY KEY NOT NULL,"+
                            "   STARTDATE date,"+
                            "   ENDDATE date,"+
                            "   DESCRIPTION longvarchar,"+
                            "   GAMEWATCHINGINTENSITY varchar(50),"+
                            "   VISITTYPE varchar(50),"+
                            "   LOCATIONNAME varchar(150)"+
                            ")";
        createSightingsTable = "CREATE TABLE SIGHTINGS "+
                            "("+
                            "   SIGHTINGCOUNTER bigint PRIMARY KEY NOT NULL,"+
                            "   SIGHTINGDATE date NOT NULL,"+
                            "   ELEMENTNAME varchar(150) NOT NULL,"+
                            "   LOCATIONNAME varchar(150) NOT NULL,"+
                            "   VISITNAME varchar(150) NOT NULL,"+
                            "   TIMEOFDAY varchar(50),"+
                            "   WEATHER varchar(50),"+
                            "   AREATYPE varchar(50),"+
                            "   VIEWRATING varchar(50),"+
                            "   CERTAINTY varchar(50),"+
                            "   NUMBEROFELEMENTS int,"+
                            "   DETAILS longvarchar,"+
                            "   LATITUDEINDICATOR varchar(10),"+
                            "   LATDEGREES int,"+
                            "   LATMINUTES int,"+
                            "   LATSECONDSFLOAT float(52),"+
                            "   LONGITUDEINDICATOR varchar(10),"+
                            "   LONDEGREES int,"+
                            "   LONMINUTES int,"+
                            "   LONSECONDSFLOAT float(52),"+
                            "   SUBAREA varchar(50),"+
                            "   SIGHTINGEVIDENCE varchar(50)"+
                            ")";
        createFotosTable = "CREATE TABLE FILES "+
                            "("+
                            "   ID varchar(175),"+
                            "   FILENAME varchar(255),"+
                            "   FILEPATH varchar(500),"+
                            "   ORIGINALPATH varchar(500),"+
                            "   FILETYPE varchar(50),"+
                            "   UPLOADDATE date,"+
                            "   ISDEFAULT smallint"+
                            ")";
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
    public void doBackup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    @Override
//    public void exportWLD(boolean inIncludeThumbnails) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void importWLD() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public Element find(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        Element tempElement = null;
        try {
            String sql = "SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName() + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempElement = new Element();
                tempElement.setPrimaryName(results.getString("PRIMARYNAME"));
                tempElement.setOtherName(results.getString("OTHERNAME"));
                tempElement.setScientificName(results.getString("SCIENTIFICNAME"));
                tempElement.setDescription(results.getString("DESCRIPTION"));
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
            }

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
        return tempElement;
    }

    @Override
    public Location find(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        Location tempLocation = null;
        try {
            String sql = "SELECT * FROM LOCATIONS WHERE NAME = '" + inLocation.getName() + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setProvince(Province.getEnumFromText(results.getString("PROVINCE")));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(Habitat.getEnumFromText(results.getString("HABITATTYPE")));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                //tempLocation.setSubAreas(results.getString("SUBAREAS"));
            }

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
        return tempLocation;
    }

    @Override
    public Visit find(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        Visit tempVisit = null;
        try {
            String sql = "SELECT * FROM VISITS WHERE NAME = '" + inVisit.getName() + "'";
            state = conn.createStatement();
            results = state.executeQuery(sql);
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
        return tempVisit;
    }

    @Override
    public Sighting find(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        Sighting tempSighting = null;
        try {
            String sql = "SELECT * FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "";
            state = conn.createStatement();
            results = state.executeQuery(sql);
            if (results.next()) {
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSubArea(results.getString("SUBAREA"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
            }

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
        return tempSighting;
    }

//    @Override
//    public Foto find(Foto inFoto) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    public MapPoint find(MapPoint inMapPoint) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public List<Element> list(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        List<Element> tempList = new ArrayList<Element>();
        try {
            String sql = "SELECT * FROM ELEMENTS";
            if (inElement.getPrimaryName() != null && inElement.getType() == null)
                sql = sql + " WHERE PRIMARYNAME like '%" + inElement.getPrimaryName() + "%'";
            else
            if (inElement.getPrimaryName() == null && inElement.getType() != null)
                sql = sql + " WHERE ELEMENTTYPE = '" + inElement.getType() + "'";
            else
            if (inElement.getPrimaryName() != null && inElement.getType() != null)
                sql = sql + " WHERE PRIMARYNAME like '%" + inElement.getPrimaryName() + "%' AND ELEMENTTYPE = '" + inElement.getType() + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Element tempElement = new Element();
                tempElement.setPrimaryName(results.getString("PRIMARYNAME"));
                tempElement.setOtherName(results.getString("OTHERNAME"));
                tempElement.setScientificName(results.getString("SCIENTIFICNAME"));
                tempElement.setDescription(results.getString("DESCRIPTION"));
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
                tempList.add(tempElement);
            }

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
        return tempList;
    }

    @Override
    public List<Location> list(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        List<Location> tempList = new ArrayList<Location>();
        try {
            String sql = "SELECT * FROM LOCATIONS";
            if (inLocation.getName() != null)
                sql = sql + " WHERE NAME like '%" + inLocation.getName() + "%'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Location tempLocation = new Location();
                tempLocation.setName(results.getString("NAME"));
                tempLocation.setDescription(results.getString("DESCRIPTION"));
                tempLocation.setProvince(Province.getEnumFromText(results.getString("PROVINCE")));
                tempLocation.setRating(LocationRating.getEnumFromText(results.getString("RATING")));
                tempLocation.setGameViewingRating(GameViewRating.getEnumFromText(results.getString("GAMEVIEWINGRATING")));
                tempLocation.setHabitatType(Habitat.getEnumFromText(results.getString("HABITATTYPE")));
                tempLocation.setAccommodationType(AccommodationType.getEnumFromText(results.getString("ACCOMMODATIONTYPE")));
                tempLocation.setCatering(CateringType.getEnumFromText(results.getString("CATERING")));
                tempLocation.setContactNumbers(results.getString("CONTACTNUMBERS"));
                tempLocation.setWebsite(results.getString("WEBSITE"));
                tempLocation.setEmail(results.getString("EMAIL"));
                tempLocation.setDirections(results.getString("DIRECTIONS"));
                tempLocation.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempLocation.setLatDegrees(results.getInt("LATDEGREES"));
                tempLocation.setLatMinutes(results.getInt("LATMINUTES"));
                tempLocation.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempLocation.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempLocation.setLonDegrees(results.getInt("LONDEGREES"));
                tempLocation.setLonMinutes(results.getInt("LONMINUTES"));
                tempLocation.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                //tempLocation.setSubAreas(results.getString("SUBAREAS"));
                tempList.add(tempLocation);
            }

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
        return tempList;
    }

    @Override
    public List<Visit> list(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        List<Visit> tempList = new ArrayList<Visit>();
        try {
            String sql = "SELECT * FROM VISITS";
            if (inVisit.getName() != null)
                sql = sql + " WHERE NAME = '" + inVisit.getName() + "'";
            else
            if (inVisit.getLocationName() != null)
                sql = sql + " WHERE LOCATIONNAME = '" + inVisit.getLocationName() + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
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
        return tempList;
    }

    @Override
    public List<Sighting> list(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = "SELECT * FROM SIGHTINGS";
            if (inSighting.getSightingCounter() > 0)
                sql = sql + " WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "";
            else
            if (inSighting.getElementName() != null)
                sql = sql + " WHERE ELEMENTNAME = '" + inSighting.getElementName() + "'";
            else
            if (inSighting.getLocationName() != null)
                sql = sql + " WHERE LOCATIONNAME = '" + inSighting.getLocationName() + "'";
            else
            if (inSighting.getVisitName() != null)
                sql = sql + " WHERE VISITNAME = '" + inSighting.getVisitName() + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSubArea(results.getString("SUBAREA"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempList.add(tempSighting);
            }

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
        return tempList;
    }

    @Override
    public List<Foto> list(Foto inFoto) {
        Statement state = null;
        ResultSet results = null;
        List<Foto> tempList = new ArrayList<Foto>();
        try {
            String sql = "SELECT * FROM FILES";
            if (inFoto.getId() != null)
                sql = sql + " WHERE ID = '" + inFoto.getId() + "'";
            sql = sql + " ORDER BY ISDEFAULT desc, FILEPATH";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Foto tempFoto = new Foto();
                tempFoto.setId(results.getString("ID"));
                tempFoto.setFilename(results.getString("FILENAME"));
                tempFoto.setFileLocation(results.getString("FILEPATH"));
                tempFoto.setOriginalFotoLocation(results.getString("ORIGINALPATH"));
                tempFoto.setFotoType(FotoType.getEnumFromText(results.getString("FILETYPE")));
                tempFoto.setDate(results.getDate("UPLOADDATE"));
                tempFoto.setDefaultFile(results.getBoolean("ISDEFAULT"));
                tempList.add(tempFoto);
            }

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
        return tempList;
    }
//
//    @Override
//    public List<MapPoint> list(MapPoint inMapPoint) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    public List<Element> searchElementOnType(ElementType inType) {
//        return list(new Element(inType));
//    }
//
//    @Override
//    public List<Element> searchElementOnPrimaryName(String inPrimaryName) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public List<Element> searchElementOnTypeAndPrimaryName(ElementType inType, String inPrimaryString) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    public List<Location> searchLocationOnName(String inName) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public List<Sighting> searchSightingOnDate(Date inStartDate, Date inEndDate) {
        Statement state = null;
        ResultSet results = null;
        List<Sighting> tempList = new ArrayList<Sighting>();
        try {
            String sql = "SELECT * FROM SIGHTINGS";
            if (inStartDate != null && inEndDate != null)
                sql = sql + " WHERE SIGHTINGDATE > '" + new java.sql.Date(inStartDate.getTime()) + "' AND SIGHTINGDATE < '" + new java.sql.Date(inEndDate.getTime()) + "'";

            state = conn.createStatement();
            results = state.executeQuery(sql);
            while (results.next()) {
                Sighting tempSighting = new Sighting();
                tempSighting = new Sighting();
                tempSighting.setSightingCounter(results.getLong("SIGHTINGCOUNTER"));
                tempSighting.setDate(results.getTimestamp("SIGHTINGDATE"));
                tempSighting.setElementName(results.getString("ELEMENTNAME"));
                tempSighting.setLocationName(results.getString("LOCATIONNAME"));
                tempSighting.setVisitName(results.getString("VISITNAME"));
                tempSighting.setTimeOfDay(ActiveTimeSpesific.getEnumFromText(results.getString("TIMEOFDAY")));
                tempSighting.setWeather(Weather.getEnumFromText(results.getString("WEATHER")));
                tempSighting.setAreaType(AreaType.getEnumFromText(results.getString("AREATYPE")));
                tempSighting.setViewRating(ViewRating.getEnumFromText(results.getString("VIEWRATING")));
                tempSighting.setCertainty(Certainty.getEnumFromText(results.getString("CERTAINTY")));
                tempSighting.setNumberOfElements(results.getInt("NUMBEROFELEMENTS"));
                tempSighting.setDetails(results.getString("DETAILS"));
                tempSighting.setLatitude(Latitudes.getEnumFromText(results.getString("LATITUDEINDICATOR")));
                tempSighting.setLatDegrees(results.getInt("LATDEGREES"));
                tempSighting.setLatMinutes(results.getInt("LATMINUTES"));
                tempSighting.setLatSecondsFloat(results.getFloat("LATSECONDSFLOAT"));
                tempSighting.setLongitude(Longitudes.getEnumFromText(results.getString("LONGITUDEINDICATOR")));
                tempSighting.setLonDegrees(results.getInt("LONDEGREES"));
                tempSighting.setLonMinutes(results.getInt("LONMINUTES"));
                tempSighting.setLonSecondsFloat(results.getFloat("LONSECONDSFLOAT"));
                tempSighting.setSubArea(results.getString("SUBAREA"));
                tempSighting.setSightingEvidence(SightingEvidence.getEnumFromText(results.getString("SIGHTINGEVIDENCE")));
                tempList.add(tempSighting);
            }

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
        return tempList;
    }

    @Override
    public boolean createOrUpdate(Element inElement, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inElement.getPrimaryName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName() + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET ELEMENTNAME = '" + inElement.getPrimaryName() + "' WHERE ELEMENTNAME = '" + inOldName + "'");
                    }
                }
                // Update
                String sql = "UPDATE ELEMENTS SET ";
                sql = sql + "PRIMARYNAME = '" + inElement.getPrimaryName() + "', ";
                sql = sql + "OTHERNAME = '" + inElement.getOtherName() + "', ";
                sql = sql + "SCIENTIFICNAME = '" + inElement.getScientificName() + "', ";
                sql = sql + "DESCRIPTION = '" + inElement.getDescription() + "', ";
                sql = sql + "NUTRITION = '" + inElement.getNutrition() + "', ";
                sql = sql + "WATERDEPENDANCE = '" + inElement.getWaterDependance() + "', ";
                sql = sql + "SIZEMALEMIN = " + inElement.getSizeMaleMin() + ", ";
                sql = sql + "SIZEMALEMAX = " + inElement.getSizeMaleMax() + ", ";
                sql = sql + "SIZEFEMALEMIN = " + inElement.getSizeFemaleMin() + ", ";
                sql = sql + "SIZEFEMALEMAX = " + inElement.getSizeFemaleMax() + ", ";
                sql = sql + "SIZEUNIT = '" + inElement.getSizeUnit() + "', ";
                sql = sql + "WEIGHTMALEMIN = " + inElement.getWeightMaleMin() + ", ";
                sql = sql + "WEIGHTMALEMAX = " + inElement.getWeightMaleMax() + ", ";
                sql = sql + "WEIGHTFEMALEMIN = " + inElement.getWeightFemaleMin() + ", ";
                sql = sql + "WEIGHTFEMALEMAX = " + inElement.getWeightFemaleMax() + ", ";
                sql = sql + "WEIGHTUNIT = '" + inElement.getWeightUnit() + "', ";
                sql = sql + "BREEDINGDURATION = '" + inElement.getBreedingDuration() + "', ";
                sql = sql + "BREEDINGNUMBER = '" + inElement.getBreedingNumber() + "', ";
                sql = sql + "WISHLISTRATING = '" + inElement.getWishListRating() + "', ";
                sql = sql + "DIAGNOSTICDESCRIPTION = '" + inElement.getDiagnosticDescription() + "', ";
                sql = sql + "ACTIVETIME = '" + inElement.getActiveTime() + "', ";
                sql = sql + "ENDANGEREDSTATUS = '" + inElement.getEndangeredStatus() + "', ";
                sql = sql + "BEHAVIOURDESCRIPTION = '" + inElement.getBehaviourDescription() + "', ";
                sql = sql + "ADDFREQUENCY = '" + inElement.getAddFrequency() + "', ";
                sql = sql + "ELEMENTTYPE = '" + inElement.getType() + "', ";
                sql = sql + "FEEDINGCLASS = '" + inElement.getFeedingClass() + "', ";
                sql = sql + "LIFESPAN = '" + inElement.getLifespan() + "', ";
                sql = sql + "REFERENCEID = '" + inElement.getReferenceID() + "'";
                sql = sql + " WHERE PRIMARYNAME = '" + inOldName + "'";
                state.executeUpdate(sql);
            }
            else {
                results = state.executeQuery("SELECT * FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName() + "'");
                if (results.next())
                    return false;
                // Insert
                String sql = "INSERT INTO ELEMENTS (PRIMARYNAME,OTHERNAME,SCIENTIFICNAME,DESCRIPTION,NUTRITION,WATERDEPENDANCE,SIZEMALEMIN,SIZEMALEMAX,SIZEFEMALEMIN,SIZEFEMALEMAX,SIZEUNIT,WEIGHTMALEMIN,WEIGHTMALEMAX,WEIGHTFEMALEMIN,WEIGHTFEMALEMAX,WEIGHTUNIT,BREEDINGDURATION,BREEDINGNUMBER,WISHLISTRATING,DIAGNOSTICDESCRIPTION,ACTIVETIME,ENDANGEREDSTATUS,BEHAVIOURDESCRIPTION,ADDFREQUENCY,ELEMENTTYPE,FEEDINGCLASS,LIFESPAN,REFERENCEID) VALUES (";
                sql = sql + "'" + inElement.getPrimaryName() + "', ";
                sql = sql + "'" + inElement.getOtherName() + "', ";
                sql = sql + "'" + inElement.getScientificName() + "', ";
                sql = sql + "'" + inElement.getDescription() + "', ";
                sql = sql + "'" + inElement.getNutrition() + "', ";
                sql = sql + "'" + inElement.getWaterDependance() + "', ";
                sql = sql + "" + inElement.getSizeMaleMin() + ", ";
                sql = sql + "" + inElement.getSizeMaleMax() + ", ";
                sql = sql + "" + inElement.getSizeFemaleMin() + ", ";
                sql = sql + "" + inElement.getSizeFemaleMax() + ", ";
                sql = sql + "'" + inElement.getSizeUnit() + "', ";
                sql = sql + "" + inElement.getWeightMaleMin() + ", ";
                sql = sql + "" + inElement.getWeightMaleMax() + ", ";
                sql = sql + "" + inElement.getWeightFemaleMin() + ", ";
                sql = sql + "" + inElement.getWeightFemaleMax() + ", ";
                sql = sql + "'" + inElement.getWeightUnit() + "', ";
                sql = sql + "'" + inElement.getBreedingDuration() + "', ";
                sql = sql + "'" + inElement.getBreedingNumber() + "', ";
                sql = sql + "'" + inElement.getWishListRating() + "', ";
                sql = sql + "'" + inElement.getDiagnosticDescription() + "', ";
                sql = sql + "'" + inElement.getActiveTime() + "', ";
                sql = sql + "'" + inElement.getEndangeredStatus() + "', ";
                sql = sql + "'" + inElement.getBehaviourDescription() + "', ";
                sql = sql + "'" + inElement.getAddFrequency() + "', ";
                sql = sql + "'" + inElement.getType() + "', ";
                sql = sql + "'" + inElement.getFeedingClass() + "', ";
                sql = sql + "'" + inElement.getLifespan() + "', ";
                sql = sql + "'" + inElement.getReferenceID() + "'";
                sql = sql + ")";
                state.execute(sql);
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
        return true;
    }

    @Override
    public boolean createOrUpdate(Location inLocation, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inLocation.getName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM LOCATIONS WHERE NAME = '" + inLocation.getName() + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET LOCATIONNAME = '" + inLocation.getName() + "' WHERE LOCATIONNAME = '" + inOldName + "'");
                    }
                }
                // Update
                String sql = "UPDATE LOCATIONS SET ";
                sql = sql + "NAME = '" + inLocation.getName() + "', ";
                sql = sql + "DESCRIPTION = '" + inLocation.getDescription() + "', ";
                sql = sql + "PROVINCE = '" + inLocation.getProvince() + "', ";
                sql = sql + "RATING = '" + inLocation.getRating() + "', ";
                sql = sql + "GAMEVIEWINGRATING = '" + inLocation.getGameViewingRating() + "', ";
                sql = sql + "HABITATTYPE = '" + inLocation.getHabitatType() + "', ";
                sql = sql + "ACCOMMODATIONTYPE = '" + inLocation.getAccommodationType() + "', ";
                sql = sql + "CATERING = '" + inLocation.getCatering() + "', ";
                sql = sql + "CONTACTNUMBERS = '" + inLocation.getContactNumbers() + "', ";
                sql = sql + "WEBSITE = '" + inLocation.getWebsite() + "', ";
                sql = sql + "EMAIL = '" + inLocation.getEmail() + "', ";
                sql = sql + "DIRECTIONS = '" + inLocation.getDirections() + "', ";
                sql = sql + "LATITUDEINDICATOR = '" + inLocation.getLatitude() + "', ";
                sql = sql + "LATDEGREES = " + inLocation.getLatDegrees() + ", ";
                sql = sql + "LATMINUTES = " + inLocation.getLatMinutes() + ", ";
                sql = sql + "LATSECONDSFLOAT = " + inLocation.getLatSecondsFloat() + ", ";
                sql = sql + "LONGITUDEINDICATOR = '" + inLocation.getLongitude() + "', ";
                sql = sql + "LONDEGREES = " + inLocation.getLonDegrees() + ", ";
                sql = sql + "LONMINUTES = " + inLocation.getLonMinutes() + ", ";
                sql = sql + "LONSECONDSFLOAT = " + inLocation.getLonSecondsFloat() + "";
//                sql = sql + "SUBAREAS = '" + inLocation.getSubAreas() + "'";
                sql = sql + " WHERE NAME = '" + inOldName + "'";
                state.executeUpdate(sql);
            }
            else {
                results = state.executeQuery("SELECT * FROM LOCATIONS WHERE NAME = '" + inLocation.getName() + "'");
                if (results.next())
                    return false;
                // Insert
                String sql = "INSERT INTO LOCATIONS (NAME,DESCRIPTION,PROVINCE,RATING,GAMEVIEWINGRATING,HABITATTYPE,ACCOMMODATIONTYPE,CATERING,CONTACTNUMBERS,WEBSITE,EMAIL,DIRECTIONS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDSFLOAT,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDSFLOAT) VALUES (";
                sql = sql + "'" + inLocation.getName() + "', ";
                sql = sql + "'" + inLocation.getDescription() + "', ";
                sql = sql + "'" + inLocation.getProvince() + "', ";
                sql = sql + "'" + inLocation.getRating() + "', ";
                sql = sql + "'" + inLocation.getGameViewingRating() + "', ";
                sql = sql + "'" + inLocation.getHabitatType() + "', ";
                sql = sql + "'" + inLocation.getAccommodationType() + "', ";
                sql = sql + "'" + inLocation.getCatering() + "', ";
                sql = sql + "'" + inLocation.getContactNumbers() + "', ";
                sql = sql + "'" + inLocation.getWebsite() + "', ";
                sql = sql + "'" + inLocation.getEmail() + "', ";
                sql = sql + "'" + inLocation.getDirections() + "', ";
                sql = sql + "'" + inLocation.getLatitude() + "', ";
                sql = sql + "" + inLocation.getLatDegrees() + ", ";
                sql = sql + "" + inLocation.getLatMinutes() + ", ";
                sql = sql + "" + inLocation.getLatSecondsFloat() + ", ";
                sql = sql + "'" + inLocation.getLongitude() + "', ";
                sql = sql + "" + inLocation.getLonDegrees() + ", ";
                sql = sql + "" + inLocation.getLonMinutes() + ", ";
                sql = sql + "" + inLocation.getLonSecondsFloat() + "";
//                sql = sql + "'" + inLocation.getSubAreas() + "' ";
                sql = sql + ")";
                state.execute(sql);
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
        return true;
    }

    @Override
    public boolean createOrUpdate(Visit inVisit, String inOldName) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inOldName != null) {
                if (!inVisit.getName().equalsIgnoreCase(inOldName)) {
                    results = state.executeQuery("SELECT * FROM VISITS WHERE NAME = '" + inVisit.getName() + "'");
                    if (results.next()) {
                        return false;
                    }
                    else {
                        state.execute("UPDATE SIGHTINGS SET VISITNAME = '" + inVisit.getName() + "' WHERE VISITNAME = '" + inOldName + "'");
                    }
                }
                // Update
                String sql = "UPDATE VISITS SET ";
                sql = sql + "NAME = '" + inVisit.getName() + "', ";
                if (inVisit.getStartDate() != null)
                    sql = sql + "STARTDATE = '" + new java.sql.Date(inVisit.getStartDate().getTime()) + "', ";
                else
                    sql = sql + "STARTDATE = null, ";
                if (inVisit.getEndDate() != null)
                    sql = sql + "ENDDATE = '" + new java.sql.Date(inVisit.getEndDate().getTime()) + "', ";
                else
                    sql = sql + "ENDDATE = null, ";
                sql = sql + "DESCRIPTION = '" + inVisit.getDescription() + "', ";
                sql = sql + "GAMEWATCHINGINTENSITY = '" + inVisit.getGameWatchingIntensity() + "', ";
                sql = sql + "VISITTYPE = '" + inVisit.getType() + "', ";
                sql = sql + "LOCATIONNAME = '" + inVisit.getLocationName() + "'";
                sql = sql + " WHERE NAME = '" + inOldName + "'";
                state.executeUpdate(sql);
            }
            else {
                results = state.executeQuery("SELECT * FROM VISITS WHERE NAME = '" + inVisit.getName() + "'");
                if (results.next())
                    return false;
                // Insert
                String sql = "INSERT INTO VISITS (NAME,STARTDATE,ENDDATE,DESCRIPTION,GAMEWATCHINGINTENSITY,VISITTYPE,LOCATIONNAME) VALUES (";
                sql = sql + "'" + inVisit.getName() + "', ";
                if (inVisit.getStartDate() != null)
                    sql = sql + "'" + new java.sql.Date(inVisit.getStartDate().getTime()) + "', ";
                else
                    sql = sql + "null, ";
                if (inVisit.getEndDate() != null)
                    sql = sql + "'" + new java.sql.Date(inVisit.getEndDate().getTime()) + "', ";
                else
                    sql = sql + "null, ";
                sql = sql + "'" + inVisit.getDescription() + "', ";
                sql = sql + "'" + inVisit.getGameWatchingIntensity() + "', ";
                sql = sql + "'" + inVisit.getType() + "', ";
                sql = sql + "'" + inVisit.getLocationName() + "'";
                sql = sql + ")";
                state.execute(sql);
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
        return true;
    }

    @Override
    public boolean createOrUpdate(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            if (inSighting.getSightingCounter() > 0) {
                // Update
                String sql = "UPDATE SIGHTINGS SET ";
                sql = sql + "SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + ", ";
                if (inSighting.getDate() != null)
                    sql = sql + "SIGHTINGDATE = '" + new java.sql.Timestamp(inSighting.getDate().getTime()) + "', ";
                else
                    sql = sql + "STARTDATE = null, ";
                sql = sql + "ELEMENTNAME = '" + inSighting.getElementName() + "', ";
                sql = sql + "LOCATIONNAME = '" + inSighting.getLocationName() + "', ";
                sql = sql + "VISITNAME = '" + inSighting.getVisitName() + "', ";
                sql = sql + "TIMEOFDAY = '" + inSighting.getTimeOfDay() + "', ";
                sql = sql + "WEATHER = '" + inSighting.getWeather() + "', ";
                sql = sql + "AREATYPE = '" + inSighting.getAreaType() + "', ";
                sql = sql + "VIEWRATING = '" + inSighting.getViewRating() + "', ";
                sql = sql + "CERTAINTY = '" + inSighting.getCertainty() + "', ";
                sql = sql + "NUMBEROFELEMENTS = " + inSighting.getNumberOfElements() + ", ";
                sql = sql + "DETAILS = '" + inSighting.getDetails() + "', ";
                sql = sql + "LATITUDEINDICATOR = '" + inSighting.getLatitude() + "', ";
                sql = sql + "LATDEGREES = " + inSighting.getLatDegrees() + ", ";
                sql = sql + "LATMINUTES = " + inSighting.getLatMinutes() + ", ";
                sql = sql + "LATSECONDSFLOAT = " + inSighting.getLatSecondsFloat() + ", ";
                sql = sql + "LONGITUDEINDICATOR = '" + inSighting.getLongitude() + "', ";
                sql = sql + "LONDEGREES = " + inSighting.getLonDegrees() + ", ";
                sql = sql + "LONMINUTES = " + inSighting.getLonMinutes() + ", ";
                sql = sql + "LONSECONDSFLOAT = " + inSighting.getLonSecondsFloat() + ", ";
                sql = sql + "SUBAREA = '" + inSighting.getSubArea() + "', ";
                sql = sql + "SIGHTINGEVIDENCE = '" + inSighting.getSightingEvidence() + "'";
                sql = sql + " WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "";
                state.executeUpdate(sql);
            }
            else {
                results = state.executeQuery("SELECT MAX(SIGHTINGCOUNTER) FROM SIGHTINGS");
                if (results.next()) {
                    long sightingCounter = results.getLong(1);
                    sightingCounter++;
                    inSighting.setSightingCounter(sightingCounter); // Need to set it for images to upload coorectly
                    // Insert
                    String sql = "INSERT INTO SIGHTINGS (SIGHTINGCOUNTER,SIGHTINGDATE,ELEMENTNAME,LOCATIONNAME,VISITNAME,TIMEOFDAY,WEATHER,AREATYPE,VIEWRATING,CERTAINTY,NUMBEROFELEMENTS,DETAILS,LATITUDEINDICATOR,LATDEGREES,LATMINUTES,LATSECONDSFLOAT,LONGITUDEINDICATOR,LONDEGREES,LONMINUTES,LONSECONDSFLOAT,SUBAREA,SIGHTINGEVIDENCE) VALUES (";
                    sql = sql + "" + inSighting.getSightingCounter() + ", ";
                    if (inSighting.getDate() != null)
                        sql = sql + "'" + new java.sql.Timestamp(inSighting.getDate().getTime()) + "', ";
                    else
                        sql = sql + "null, ";
                    sql = sql + "'" + inSighting.getElementName() + "', ";
                    sql = sql + "'" + inSighting.getLocationName() + "', ";
                    sql = sql + "'" + inSighting.getVisitName() + "', ";
                    sql = sql + "'" + inSighting.getTimeOfDay() + "', ";
                    sql = sql + "'" + inSighting.getWeather() + "', ";
                    sql = sql + "'" + inSighting.getAreaType() + "', ";
                    sql = sql + "'" + inSighting.getViewRating() + "', ";
                    sql = sql + "'" + inSighting.getCertainty() + "', ";
                    sql = sql + "" + inSighting.getNumberOfElements() + ", ";
                    sql = sql + "'" + inSighting.getDetails() + "', ";
                    sql = sql + "'" + inSighting.getLatitude() + "', ";
                    sql = sql + "" + inSighting.getLatDegrees() + ", ";
                    sql = sql + "" + inSighting.getLatMinutes() + ", ";
                    sql = sql + "" + inSighting.getLatSecondsFloat() + ", ";
                    sql = sql + "'" + inSighting.getLongitude() + "', ";
                    sql = sql + "" + inSighting.getLonDegrees() + ", ";
                    sql = sql + "" + inSighting.getLonMinutes() + ", ";
                    sql = sql + "" + inSighting.getLonSecondsFloat() + ", ";
                    sql = sql + "'" + inSighting.getSubArea() + "', ";
                    sql = sql + "'" + inSighting.getSightingEvidence() + "'";
                    sql = sql + ")";
                    state.execute(sql);
                }
                else
                    return false;
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
        return true;
    }

    @Override
    public boolean createOrUpdate(Foto inFoto, boolean inUpdate) {
        // Note: mens kan nie fotos update nie
        Statement state = null;
        try {
            state = conn.createStatement();
            if (inUpdate) {
                String sql = "UPDATE FILES SET ";
                sql = sql + "ID = '" + inFoto.getId() + "', ";
                sql = sql + "FILENAME = '" + inFoto.getFilename() + "', ";
                sql = sql + "FILEPATH = '" + inFoto.getFileLocation() + "', ";
                sql = sql + "ORIGINALPATH = '" + inFoto.getOriginalFotoLocation() + "', ";
                sql = sql + "FILETYPE = '" + inFoto.getFotoType() + "',";
                if (inFoto.getDate() != null)
                    sql = sql + "UPLOADDATE = '" + new java.sql.Date(inFoto.getDate().getTime()) + "',";
                else
                    sql = sql + "UPLOADDATE = null,";
                if (inFoto.isDefaultFile())
                    sql = sql + "ISDEFAULT = 1";
                else
                    sql = sql + "ISDEFAULT = 0";
                sql =sql + " WHERE FILEPATH = '" + inFoto.getFileLocation() + "'";
                state.execute(sql);
            }
            else {
                // Insert
                String sql = "INSERT INTO FILES (ID,FILENAME,FILEPATH,ORIGINALPATH,FILETYPE,UPLOADDATE,ISDEFAULT) VALUES (";
                sql = sql + "'" + inFoto.getId() + "', ";
                sql = sql + "'" + inFoto.getFilename() + "', ";
                sql = sql + "'" + inFoto.getFileLocation() + "', ";
                sql = sql + "'" + inFoto.getOriginalFotoLocation() + "', ";
                sql = sql + "'" + inFoto.getFotoType() + "',";
                if (inFoto.getDate() != null)
                    sql = sql + "'" + new java.sql.Date(inFoto.getDate().getTime()) + "',";
                else
                    sql = sql + "null,";
                if (inFoto.isDefaultFile())
                    sql = sql + "1";
                else
                    sql = sql + "0";
                sql = sql + ")";
                state.execute(sql);
            }
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
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
        return true;
    }

//    @Override
//    public boolean createOrUpdate(MapPoint inMapPoint) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public boolean delete(Element inElement) {
        Statement state = null;
        ResultSet results = null;
        try {
            // Delete the Element
            state = conn.createStatement();
            state.executeUpdate("DELETE FROM ELEMENTS WHERE PRIMARYNAME = '" + inElement.getPrimaryName() + "'");
            // Delete all Sightings for this element
            results = state.executeQuery("SELECT * FROM SIGHTINGS WHERE ELEMENTNAME = '" + inElement.getPrimaryName() + "'");
            while (results.next()) {
                Sighting sighting = new Sighting(results.getLong("SIGHTINGCOUNTER"));
                delete(sighting);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'ELEMENT-" + inElement.getPrimaryName() + "'");
            while (results.next()) {
                Foto file = new Foto(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), FotoType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
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
        return true;
    }

    @Override
    public boolean delete(Location inLocation) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Location
            state.executeUpdate("DELETE FROM LOCATIONS WHERE NAME = '" + inLocation.getName() + "'");
            // Delete Visits for this location
            results = state.executeQuery("SELECT * FROM VISITS WHERE LOCATIONNAME = '" + inLocation.getName() + "'");
            while (results.next()) {
                Visit visit = new Visit(results.getString("NAME"));
                delete(visit);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'LOCATION-" + inLocation.getName() + "'");
            while (results.next()) {
                Foto file = new Foto(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), FotoType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
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
        return true;
    }

    @Override
    public boolean delete(Visit inVisit) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Visit
            state.executeUpdate("DELETE FROM VISITS WHERE NAME = '" + inVisit.getName() + "'");
            // Delete Sightings for this visit
            results = state.executeQuery("SELECT * FROM SIGHTINGS WHERE VISITNAME = '" + inVisit.getName() + "'");
            while (results.next()) {
                Sighting sighting = new Sighting(results.getLong("SIGHTINGCOUNTER"));
                delete(sighting);
            }
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'VISIT-" + inVisit.getName() + "'");
            while (results.next()) {
                Foto file = new Foto(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), FotoType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
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
        return true;
    }

    @Override
    public boolean delete(Sighting inSighting) {
        Statement state = null;
        ResultSet results = null;
        try {
            state = conn.createStatement();
            // Delete Sightings
            state.executeUpdate("DELETE FROM SIGHTINGS WHERE SIGHTINGCOUNTER = " + inSighting.getSightingCounter() + "");
            // Delete Fotos
            results = state.executeQuery("SELECT * FROM FILES WHERE ID = 'SIGHTING-" + inSighting.getSightingCounter() + "'");
            while (results.next()) {
                Foto file = new Foto(results.getString("ID"), results.getString("FILENAME"), results.getString("FILEPATH"), results.getString("ORIGINALPATH"), FotoType.getEnumFromText(results.getString("FILETYPE")), results.getDate("UPLOADDATE"));
                delete(file);
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
        return true;
    }

    @Override
    public boolean delete(Foto inFoto) {
        // Note: this method only deletes one file at a time
        Statement state = null;
        try {
            state = conn.createStatement();
            // Delete File from database
            state.executeUpdate("DELETE FROM FILES WHERE FILEPATH = '" + inFoto.getFileLocation() + "'");
            // Delete the file on the PC
            File tempFile = new File(inFoto.getFileLocation());
            tempFile.delete();
            tempFile = new File(inFoto.getOriginalFotoLocation());
            tempFile.delete();
        }
        catch (SQLException ex) {
            printSQLException(ex);
            return false;
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
        return true;
    }


//    @Override
//    public boolean delete(MapPoint inMapPoint) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    protected static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }


}
