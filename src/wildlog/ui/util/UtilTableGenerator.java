package wildlog.ui.util;

import java.util.ArrayList;
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
    public DefaultTableModel getCompleteElementTable(Element inElement) {
        String[] columnNames = {
                                "Primary Name",
                                "Other Name",
                                "End Stat",
                                "Type",
                                "Class",
                                "Wish Rate",
                                "Add Freq"
                                };
        List<Element> tempList = dbi.list(inElement);
        Object[][] tempTable = new Object[tempList.size()][7];
        for (int t = 0; t < tempList.size(); t++) {
            Element tempElement = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempElement.getPrimaryName();
            tempTable[t][i++] = tempElement.getOtherName();
            if (tempElement.getEndangeredStatus() != null)
                tempTable[t][i++] = tempElement.getEndangeredStatus().key();
            else i++;
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
        List<Element> tempList = dbi.list(inElement);
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
                                "Game Rating",
                                "Accommodation Type",
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
            tempTable = new Object[tempList.size()][6];
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
        };
        return table;
    }
    
    public DefaultTableModel getCompleteSightingTable(Visit inVisit) {
        String[] columnNames = {
                                "Primary Name",
                                "Element Type",
                                "Date",
                                "View Rating",
                                "Certainty",
                                };
        List<Sighting> tempList = inVisit.getSightings();
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][5];
            for (int t = 0; t < tempList.size(); t++) {
                Sighting tempSighting = tempList.get(t);
                int i = 0;
                if (tempSighting.getElement() != null) {
                    tempTable[t][i++] = tempSighting.getElement().getPrimaryName();
                    tempTable[t][i++] = tempSighting.getElement().getType();
                }
                else {
                    tempTable[t][i++] = null;
                    tempTable[t][i++] = null;
                    tempTable[t][i++] = null;
                }
                tempTable[t][i++] = tempSighting.getDate();
                tempTable[t][i++] = tempSighting.getViewRating();
                tempTable[t][i++] = tempSighting.getCertainty();
            }
        }
        else tempTable = new Object[0][0];
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
                                "Class",
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
                                "Rating",
                                "Game Rating"
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
            tempTable = new Object[allLocations.size()][4];
            for (int t = 0; t < allLocations.size(); t++) {
                Location tempLocation = allLocations.get(t);
                int i = 0;
                tempTable[t][i++] = tempLocation.getName();
                tempTable[t][i++] = tempLocation.getProvince();
                tempTable[t][i++] = tempLocation.getRating();
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

}
