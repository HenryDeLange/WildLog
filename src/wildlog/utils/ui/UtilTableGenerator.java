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
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public class UtilTableGenerator {
    private DBI dbi;
    
    // CONSTRUCTOR:
    public UtilTableGenerator() {
        WildLogApp app = (WildLogApp) Application.getInstance();
        dbi = app.getDBI();
    }
    
    // METHODS:
    public DefaultTableModel getCompleteElementTable(Element inElement) {
        String[] columnNames = {
                                "Primary Name",
                                "Other Name",
                                "Type",
                                "Class",
                                "Wish Rate",
                                "Add Freq"
                                };
        List<Element> tempList = null;
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

    public DefaultTableModel getShortElementTable(Element inElement) {
        String[] columnNames = {
                                "Primary Name",
                                "Type",
                                "Class"
                                };
        List<Element> tempList = null;
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
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = dbi.list(temp);
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
                Sighting tempSighting = new Sighting();
                tempSighting.setVisitName(tempVisit.getName());
                tempTable[t][i++] = dbi.list(tempSighting).size();
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
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = dbi.list(temp);
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][4];
            for (int t = 0; t < tempList.size(); t++) {
                Visit tempVisit = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempVisit.getName();
                tempTable[t][i++] = tempVisit.getStartDate();
                tempTable[t][i++] = tempVisit.getType();
                Sighting tempSighting = new Sighting();
                tempSighting.setVisitName(tempVisit.getName());
                tempTable[t][i++] = dbi.list(tempSighting).size();
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
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = dbi.list(temp);
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
                                "ID",
                                "GPS"
                                };
        Sighting temp = new Sighting();
        temp.setVisitName(inVisit.getName());
        List<Sighting> tempList = dbi.list(temp);
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][7];
            for (int t = 0; t < tempList.size(); t++) {
                Sighting tempSighting = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempSighting.getElementName();
                tempTable[t][i++] = tempSighting.getDate();
                tempTable[t][i++] = tempSighting.getViewRating();
                tempTable[t][i++] = tempSighting.getCertainty();
                tempTable[t][i++] = dbi.find(new Element(tempSighting.getElementName())).getType();
                tempTable[t][i++] = tempSighting.getSightingCounter();
                if (tempSighting.getLatitude() != null && tempSighting.getLongitude() != null)
                    if (!tempSighting.getLatitude().equals(Latitudes.NONE) && !tempSighting.getLongitude().equals(Longitudes.NONE))
                        tempTable[t][i++] = "Yes";
                    else tempTable[t][i++] = "";
                else
                    tempTable[t][i++] = "";
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
        Sighting temp = new Sighting();
        temp.setVisitName(inVisit.getName());
        List<Sighting> allSightings = dbi.list(temp);
        List<String> allElements = new ArrayList<String>();
        for (Sighting tempSighting : allSightings) {
            if (!allElements.contains(tempSighting.getElementName()))
                allElements.add(tempSighting.getElementName());
        }
        Object[][] tempTable = null;
        tempTable = new Object[allElements.size()][3];
        for (int t = 0; t < allElements.size(); t++) {
            Element tempElement = dbi.find(new Element(allElements.get(t)));
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

    public DefaultTableModel getElementsForLocationTable(Location inLocation) {
        String[] columnNames = {
                                "Primary Name",
                                "Type",
                                "Class"
                                };
        Sighting temp = new Sighting();
        temp.setLocationName(inLocation.getName());
        List<Sighting> allSightings = dbi.list(temp);
        List<String> allElements = new ArrayList<String>();
        for (Sighting tempSighting : allSightings) {
            if (!allElements.contains(tempSighting.getElementName()))
                allElements.add(tempSighting.getElementName());
        }
        Object[][] tempTable = null;
        tempTable = new Object[allElements.size()][3];
        for (int t = 0; t < allElements.size(); t++) {
            Element tempElement = dbi.find(new Element(allElements.get(t)));
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

    public DefaultTableModel getLocationsForElementTable(Element inElement) {
        String[] columnNames = {
                                "Name",
                                "Province",
                                "Wildlife"
                                };
        List<String> allLocations = new ArrayList<String>();
        Object[][] tempTable = null;
        if (inElement != null) {
            List<Sighting> allSightings = dbi.list(new Sighting());
            for (int t = 0; t < allSightings.size(); t++) {
                if (allSightings.get(t).getElementName().equals(inElement.getPrimaryName()))
                    if (!allLocations.contains(allSightings.get(t).getLocationName()))
                        allLocations.add(allSightings.get(t).getLocationName());
            }
            tempTable = new Object[allLocations.size()][3];
            for (int t = 0; t < allLocations.size(); t++) {
                Location tempLocation = dbi.find(new Location(allLocations.get(t)));
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
        templateSighting.setElementName(inElement.getPrimaryName());
        List<Sighting> tempList = dbi.list(templateSighting);
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][3];
            for (int t = 0; t < tempList.size(); t++) {
                Sighting tempSighting = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempSighting.getLocationName();
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
