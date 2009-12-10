/*
 * UtilTableGenerator.java is part of WildLog
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

package wildlog.utils.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dbi.DBI;
import wildlog.WildLogApp;


public class UtilTableGenerator {
    private DBI dbi;
    
    // CONSTRUCTOR:
    public UtilTableGenerator() {
        WildLogApp app = (WildLogApp) Application.getInstance();
        dbi = app.getDBI();
    }
    
    // METHODS:
    public DefaultTableModel getCompleteElementTable(Element inElement, boolean inUseSearch) {
        String[] columnNames = {
                                "Primary Name",
                                "Other Name",
                                "Type",
                                "Class",
                                "Wish Rate",
                                "Add Freq"
                                };
        List<Element> tempList = null;
        if (inUseSearch) {
            if (inElement.getType() == null) {
                if (inElement.getPrimaryName() != null) {
                    tempList = dbi.searchElementOnPrimaryName(inElement.getPrimaryName());
                }
                else {
                    // Both where null so do normal listing
                    tempList = dbi.list(inElement);
                }
            }
            else {
                if (inElement.getPrimaryName() == null) {
                    tempList = dbi.searchElementOnType(inElement.getType());
                }
                else {
                    tempList = dbi.searchElementOnTypeAndPrimaryName(inElement.getType(), inElement.getPrimaryName());
                }
            }
        }
        else
            tempList = dbi.list(inElement);

        Object[][] tempTable = new Object[tempList.size()][6];
        for (int t = 0; t < tempList.size(); t++) {
            Element tempElement = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempElement.getPrimaryName();
            tempTable[t][i++] = tempElement.getOtherName();
            tempTable[t][i++] = tempElement.getType();
            tempTable[t][i++] = tempElement.getFeedingClass();
            tempTable[t][i++] = tempElement.getWishListRating();
            tempTable[t][i++] = tempElement.getAddFrequency();
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }

    public DefaultTableModel getShortElementTable(Element inElement, boolean inUseSearch) {
        String[] columnNames = {
                                "Primary Name",
                                "Type",
                                "Class"
                                };
        List<Element> tempList = null;
        if (inUseSearch) {
            if (inElement.getType() == null) {
                if (inElement.getPrimaryName() != null) {
                    tempList = dbi.searchElementOnPrimaryName(inElement.getPrimaryName());
                }
                else {
                    // Both where null so do normal listing
                    tempList = dbi.list(inElement);
                }
            }
            else {
                if (inElement.getPrimaryName() == null) {
                    tempList = dbi.searchElementOnType(inElement.getType());
                }
                else {
                    tempList = dbi.searchElementOnTypeAndPrimaryName(inElement.getType(), inElement.getPrimaryName());
                }
            }
        }
        else
            tempList = dbi.list(inElement);

        Object[][] tempTable = new Object[tempList.size()][4];
        for (int t = 0; t < tempList.size(); t++) {
            Element tempElement = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempElement.getPrimaryName();
            tempTable[t][i++] = tempElement.getType();
            tempTable[t][i++] = tempElement.getFeedingClass();
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }
    
    public DefaultTableModel getCompleteLocationTable(Location inLocation) {
        String[] columnNames = {
                                "Name",
                                "Province",
                                "Rating",
                                "Wildlife",
                                "Accommodation",
                                "Catering"
                                };
        List<Location> tempList = dbi.list(inLocation);
        Object[][] tempTable = new Object[tempList.size()][6];
        for (int t = 0; t < tempList.size(); t++) {
            Location tempLocation = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempLocation.getName();
            tempTable[t][i++] = tempLocation.getProvince();
            tempTable[t][i++] = tempLocation.getRating();
            tempTable[t][i++] = tempLocation.getGameViewingRating();
            tempTable[t][i++] = tempLocation.getAccommodationType();
            tempTable[t][i++] = tempLocation.getCatering();
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }
    
    public DefaultTableModel getCompleteVisitTable(Location inLocation) {
        String[] columnNames = {
                                "Name",
                                "Start Date",
                                "End Date",
                                "Game Watching",
                                "Visit Type",
                                "Sightings"
                                };
        List<Visit> tempList = inLocation.getVisits();
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][6];
            for (int t = 0; t < tempList.size(); t++) {
                Visit tempVisit = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempVisit.getName();
                tempTable[t][i++] = tempVisit.getStartDate();
                tempTable[t][i++] = tempVisit.getEndDate();
                tempTable[t][i++] = tempVisit.getGameWatchingIntensity();
                tempTable[t][i++] = tempVisit.getType();
                if (tempVisit.getSightings() != null)
                    tempTable[t][i++] = tempVisit.getSightings().size();
            }
        }
        else tempTable = new Object[0][0];
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1 || column == 2) {
                    return Date.class;
                }
                if (column == 5) {
                    return Integer.class;
                }
                return Object.class;
            }
        };
        return table;
    }

    public DefaultTableModel getShortVisitTable(Location inLocation) {
        String[] columnNames = {
                                "Name",
                                "Start Date",
                                "Visit Type",
                                "Sightings"
                                };
        List<Visit> tempList = inLocation.getVisits();
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][4];
            for (int t = 0; t < tempList.size(); t++) {
                Visit tempVisit = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempVisit.getName();
                tempTable[t][i++] = tempVisit.getStartDate();
                tempTable[t][i++] = tempVisit.getType();
                if (tempVisit.getSightings() != null)
                    tempTable[t][i++] = tempVisit.getSightings().size();
            }
        }
        else tempTable = new Object[0][0];
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Date.class;
                }
                if (column == 3) {
                    return Integer.class;
                }
                return Object.class;
            }
        };
        return table;
    }

    public DefaultTableModel getVeryShortVisitTable(Location inLocation) {
        String[] columnNames = {
                                "Name",
                                "Start Date",
                                "Visit Type"
                                };
        List<Visit> tempList = inLocation.getVisits();
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][3];
            for (int t = 0; t < tempList.size(); t++) {
                Visit tempVisit = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempVisit.getName();
                tempTable[t][i++] = tempVisit.getStartDate();
                tempTable[t][i++] = tempVisit.getType();
            }
        }
        else tempTable = new Object[0][0];
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Date.class;
                }
                return Object.class;
            }
        };
        return table;
    }
    
    public DefaultTableModel getCompleteSightingTable(Visit inVisit) {
        String[] columnNames = {
                                "Primary Name",
                                "Date",
                                "View Rating",
                                "Certainty",
                                "Type",
                                "ID"
                                };
        List<Sighting> tempList = inVisit.getSightings();
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][6];
            for (int t = 0; t < tempList.size(); t++) {
                Sighting tempSighting = tempList.get(t);
                int i = 0;
                if (tempSighting.getElement() != null) {
                    tempTable[t][i++] = tempSighting.getElement().getPrimaryName();
                }
                //else {
                //    tempTable[t][i++] = null;
                //    tempTable[t][i++] = null;
                //    tempTable[t][i++] = null;
                //
                tempTable[t][i++] = tempSighting.getDate();
                tempTable[t][i++] = tempSighting.getViewRating();
                tempTable[t][i++] = tempSighting.getCertainty();
                tempTable[t][i++] = tempSighting.getElement().getType();
                tempTable[t][i++] = tempSighting.getSightingCounter();
            }
        }
        else tempTable = new Object[0][0];
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Date.class;
                }
                return Object.class;
            }
        };
        return table;
    }

    public DefaultTableModel getElementsForVisitTable(Visit inVisit) {
        String[] columnNames = {
                                "Primary Name",
                                "Type",
                                "Class"
                                };
        List<Element> allElements = new ArrayList<Element>();
        Object[][] tempTable = null;
        if (inVisit != null && inVisit.getSightings() != null) {
            for (int i = 0; i < inVisit.getSightings().size(); i++) {
                if (!allElements.contains(inVisit.getSightings().get(i).getElement()))
                    allElements.add(inVisit.getSightings().get(i).getElement());
            }
            tempTable = new Object[allElements.size()][3];
            for (int t = 0; t < allElements.size(); t++) {
                Element tempElement = allElements.get(t);
                int i = 0;
                tempTable[t][i++] = tempElement.getPrimaryName();
                tempTable[t][i++] = tempElement.getType();
                tempTable[t][i++] = tempElement.getFeedingClass();
            }
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }

    public DefaultTableModel getElementsForLocationTable(Location inLocation) {
        String[] columnNames = {
                                "Primary Name",
                                "Type",
                                "Class"
                                };
        List<Element> allElements = new ArrayList<Element>();
        Object[][] tempTable = null;
        if (inLocation != null && inLocation.getVisits() != null) {
            for (int t = 0; t < inLocation.getVisits().size(); t++) {
                for (int i = 0; i < inLocation.getVisits().get(t).getSightings().size(); i++) {
                    if (!allElements.contains(inLocation.getVisits().get(t).getSightings().get(i).getElement()))
                        allElements.add(inLocation.getVisits().get(t).getSightings().get(i).getElement());
                }
            }
            tempTable = new Object[allElements.size()][3];
            for (int t = 0; t < allElements.size(); t++) {
                Element tempElement = allElements.get(t);
                int i = 0;
                tempTable[t][i++] = tempElement.getPrimaryName();
                tempTable[t][i++] = tempElement.getType();
                tempTable[t][i++] = tempElement.getFeedingClass();
            }
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }

    public DefaultTableModel getLocationsForElementTable(Element inElement) {
        String[] columnNames = {
                                "Name",
                                "Province",
                                "Wildlife"
                                };
        List<Location> allLocations = new ArrayList<Location>();
        Object[][] tempTable = null;
        if (inElement != null) {
            List<Sighting> allSightings = dbi.list(new Sighting());
            for (int t = 0; t < allSightings.size(); t++) {
                if (allSightings.get(t).getElement().equals(inElement))
                    if (!allLocations.contains(allSightings.get(t).getLocation()))
                        allLocations.add(allSightings.get(t).getLocation());
            }
            tempTable = new Object[allLocations.size()][3];
            for (int t = 0; t < allLocations.size(); t++) {
                Location tempLocation = allLocations.get(t);
                int i = 0;
                tempTable[t][i++] = tempLocation.getName();
                tempTable[t][i++] = tempLocation.getProvince();
                tempTable[t][i++] = tempLocation.getGameViewingRating();
            }
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }

    public DefaultTableModel getShortLocationTable(Location inLocation, boolean inUseSearch) {
        String[] columnNames = {
                                "Name",
                                "Province"
                                };
        List<Location> tempList = null;
        if (inUseSearch) {
            if (inLocation.getName() != null) {
                tempList = dbi.searchLocationOnName(inLocation.getName());
            }
            else {
                tempList = dbi.list(inLocation);
            }
        }
        else
            tempList = dbi.list(inLocation);
        
        Object[][] tempTable = new Object[tempList.size()][2];
        for (int t = 0; t < tempList.size(); t++) {
            Location tempLocation = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempLocation.getName();
            tempTable[t][i++] = tempLocation.getProvince();
        }
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        return table;
    }

    public DefaultTableModel getSightingsForElementTable(Element inElement) {
        String[] columnNames = {
                                "Location",
                                "Date",
                                "ID"
                                };
        Sighting templateSighting = new Sighting();
        templateSighting.setElement(inElement);
        List<Sighting> tempList = dbi.list(templateSighting);
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][3];
            for (int t = 0; t < tempList.size(); t++) {
                Sighting tempSighting = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempSighting.getLocation().getName();
                tempTable[t][i++] = tempSighting.getDate();
                tempTable[t][i++] = tempSighting.getSightingCounter();
            }
        }
        else tempTable = new Object[0][0];
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Date.class;
                }
                return Object.class;
            }
        };
        return table;
    }

    // Private Methods
/* Not used anymore
    private String getMonth(int inMonth) {
        if (inMonth == 0) return "Jan";
        if (inMonth == 1) return "Feb";
        if (inMonth == 2) return "Mar";
        if (inMonth == 3) return "Apr";
        if (inMonth == 4) return "May";
        if (inMonth == 5) return "Jun";
        if (inMonth == 6) return "Jul";
        if (inMonth == 7) return "Aug";
        if (inMonth == 8) return "Sep";
        if (inMonth == 9) return "Oct";
        if (inMonth == 10) return "Nov";
        if (inMonth == 11) return "Dec";
        return "";
    }
*/
}
