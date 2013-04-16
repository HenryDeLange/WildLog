package wildlog.ui.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.utils.UtilsGps;


public final class UtilTableGenerator {

    // METHODS:
    public static void setupCompleteElementTable(WildLogApp inApp, JTable inTable, Element inElement) {
        // Load data
        String[] columnNames = {
                                "Creature Name",
                                "Other Name",
                                "Type",
                                "Class",
                                "Wish Rating",
                                "Add Frequency"
                                };
        List<Element> tempList = null;
        tempList = inApp.getDBI().list(inElement);

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
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(210);
            }
            else if (i == 1) {
                column.setPreferredWidth(180);
            }
            else if (i == 2) {
                column.setPreferredWidth(50);
            }
            else if (i == 3) {
                column.setPreferredWidth(40);
            }
            else if (i == 4) {
                column.setPreferredWidth(140);
            }
            else if (i == 5) {
                column.setPreferredWidth(60);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupShortElementTable(WildLogApp inApp, JTable inTable, Element inElement) {
        // Load data
        String[] columnNames = {
                                "Creature Name",
                                "Type",
                                "Class"
                                };
        List<Element> tempList = null;
        tempList = inApp.getDBI().list(inElement);

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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
                column = inTable.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(150);
                }
                else if (i == 1) {
                    column.setPreferredWidth(25);
                }
                else if (i == 2) {
                    column.setPreferredWidth(40);
                }
            }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupCompleteLocationTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Place Name",
                                "Rating",
                                "Wildlife Rating",
                                "Latitude",
                                "Longitude"
                                };
        List<Location> tempList = inApp.getDBI().list(inLocation);
        Object[][] tempTable = new Object[tempList.size()][6];
        for (int t = 0; t < tempList.size(); t++) {
            Location tempLocation = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempLocation.getName();
            tempTable[t][i++] = tempLocation.getRating();
            tempTable[t][i++] = tempLocation.getGameViewingRating();
            tempTable[t][i++] = UtilsGps.getLatitudeString(tempLocation);
            tempTable[t][i++] = UtilsGps.getLongitudeString(tempLocation);
        }
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(220);
            }
            else if (i == 1) {
                column.setPreferredWidth(30);
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
            else if (i == 3) {
                column.setPreferredWidth(25);
            }
            else if (i == 4) {
                column.setPreferredWidth(25);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupCompleteVisitTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Period Name",
                                "Start Date",
                                "End Date",
                                "Game Watching",
                                "Period Type",
                                "Observations"
                                };
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = inApp.getDBI().list(temp);
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
                tempTable[t][i++] = inApp.getDBI().list(tempSighting).size();
            }
        }
        else tempTable = new Object[0][0];
        // Create the model
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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(160);
            }
            else if (i == 1) {
                column.setPreferredWidth(45);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(45);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 3) {
                column.setPreferredWidth(75);
            }
            else if (i == 4) {
                column.setPreferredWidth(30);
            }
            else if (i == 5) {
                column.setPreferredWidth(30);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 1);
    }

    public static void setupShortVisitTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Period Name",
                                "Start Date",
                                "Observations"
                                };
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = inApp.getDBI().list(temp);
        Object[][] tempTable;
        if (tempList != null) {
            tempTable = new Object[tempList.size()][4];
            for (int t = 0; t < tempList.size(); t++) {
                Visit tempVisit = tempList.get(t);
                int i = 0;
                tempTable[t][i++] = tempVisit.getName();
                tempTable[t][i++] = tempVisit.getStartDate();
                Sighting tempSighting = new Sighting();
                tempSighting.setVisitName(tempVisit.getName());
                tempTable[t][i++] = inApp.getDBI().list(tempSighting).size();
            }
        }
        else tempTable = new Object[0][0];
        // Create the model
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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            }
            else if (i == 1) {
                column.setPreferredWidth(50);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(15);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 1);
    }

    public static void setupVeryShortVisitTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Name",
                                "Start Date",
                                "Period Type"
                                };
        Visit temp = new Visit();
        temp.setLocationName(inLocation.getName());
        List<Visit> tempList = inApp.getDBI().list(temp);
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
        // Create the model
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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            }
            else if (i == 1) {
                column.setPreferredWidth(45);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(25);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 1);
    }

    public static void setupCompleteSightingTable(WildLogApp inApp, JTable inTable, Visit inVisit) {
        // Load data
        String[] columnNames = {
                                "Creature Name",
                                "Date",
                                "View Rating",
                                "Certainty",
                                "Type",
                                "ID",
                                "GPS"
                                };
        Sighting temp = new Sighting();
        temp.setVisitName(inVisit.getName());
        List<Sighting> tempList = inApp.getDBI().list(temp);
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
                tempTable[t][i++] = inApp.getDBI().find(new Element(tempSighting.getElementName())).getType();
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
        // Create the model
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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(170);
            }
            else if (i == 1) {
                column.setPreferredWidth(65);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(55);
            }
            else if (i == 3) {
                column.setPreferredWidth(70);
            }
            else if (i == 4) {
                column.setPreferredWidth(35);
            }
            else if (i == 5) {
                column.setPreferredWidth(10);
            }
            else if (i == 6) {
                column.setPreferredWidth(10);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 1);
    }

    public static void setupElementsForVisitTable(WildLogApp inApp, JTable inTable, Visit inVisit) {
        // Load data
        String[] columnNames = {
                                "Creature Name",
                                "Type",
                                "Class"
                                };
        Sighting temp = new Sighting();
        temp.setVisitName(inVisit.getName());
        List<Sighting> allSightings = inApp.getDBI().list(temp);
        List<String> allElements = new ArrayList<String>();
        for (Sighting tempSighting : allSightings) {
            if (!allElements.contains(tempSighting.getElementName()))
                allElements.add(tempSighting.getElementName());
        }
        Object[][] tempTable = null;
        tempTable = new Object[allElements.size()][3];
        for (int t = 0; t < allElements.size(); t++) {
            Element tempElement = inApp.getDBI().find(new Element(allElements.get(t)));
            int i = 0;
            tempTable[t][i++] = tempElement.getPrimaryName();
            tempTable[t][i++] = tempElement.getType();
            tempTable[t][i++] = tempElement.getFeedingClass();
        }
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(110);
            }
            else if (i == 1) {
                column.setPreferredWidth(35);
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupElementsForLocationTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Creature Name",
                                "Type",
                                "Class"
                                };
        Sighting temp = new Sighting();
        if (inLocation.getName() != null)
            temp.setLocationName(inLocation.getName());
        List<Sighting> allSightings = inApp.getDBI().list(temp);
        List<String> allElements = new ArrayList<String>();
        for (Sighting tempSighting : allSightings) {
            if (!allElements.contains(tempSighting.getElementName()))
                allElements.add(tempSighting.getElementName());
        }
        Object[][] tempTable = null;
        tempTable = new Object[allElements.size()][3];
        for (int t = 0; t < allElements.size(); t++) {
            Element tempElement = inApp.getDBI().find(new Element(allElements.get(t)));
            int i = 0;
            tempTable[t][i++] = tempElement.getPrimaryName();
            tempTable[t][i++] = tempElement.getType();
            tempTable[t][i++] = tempElement.getFeedingClass();
        }
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(110);
            }
            else if (i == 1) {
                column.setPreferredWidth(35);
            }
            else if (i == 2) {
                column.setPreferredWidth(30);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupLocationsForElementTable(WildLogApp inApp, JTable inTable, Element inElement) {
        // Load data
        String[] columnNames = {
                                "Place Name",
                                "Wildlife Rating"
                                };
        List<String> allLocations = new ArrayList<String>();
        Object[][] tempTable = null;
        if (inElement != null) {
            Sighting temp = new Sighting();
            if (inElement.getPrimaryName() != null)
                temp.setElementName(inElement.getPrimaryName());
            List<Sighting> allSightings = inApp.getDBI().list(temp);
            for (int t = 0; t < allSightings.size(); t++) {
                if (!allLocations.contains(allSightings.get(t).getLocationName()))
                    allLocations.add(allSightings.get(t).getLocationName());
            }
            tempTable = new Object[allLocations.size()][3];
            for (int t = 0; t < allLocations.size(); t++) {
                Location tempLocation = inApp.getDBI().find(new Location(allLocations.get(t)));
                int i = 0;
                tempTable[t][i++] = tempLocation.getName();
                tempTable[t][i++] = tempLocation.getGameViewingRating();
            }
        }
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            }
            else if (i == 1) {
                column.setPreferredWidth(20);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupShortLocationTable(WildLogApp inApp, JTable inTable, Location inLocation) {
        // Load data
        String[] columnNames = {
                                "Place Name"
                                };
        List<Location> tempList = null;
        tempList = inApp.getDBI().list(inLocation);

        Object[][] tempTable = new Object[tempList.size()][2];
        for (int t = 0; t < tempList.size(); t++) {
            Location tempLocation = tempList.get(t);
            int i = 0;
            tempTable[t][i++] = tempLocation.getName();
        }
        // Create the model
        DefaultTableModel table = new DefaultTableModel(tempTable, columnNames) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 0);
    }

    public static void setupSightingsForElementTable(WildLogApp inApp, JTable inTable, Element inElement) {
        // Load data
        String[] columnNames = {
                                "Place Name",
                                "Date",
                                "ID"
                                };
        Sighting templateSighting = new Sighting();
        templateSighting.setElementName(inElement.getPrimaryName());
        List<Sighting> tempList = inApp.getDBI().list(templateSighting);
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
        // Create the model
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
        inTable.setModel(table);
        // Resize the columns
        TableColumn column = null;
        for (int i = 0; i < inTable.getColumnModel().getColumnCount(); i++) {
            column = inTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(135);
            }
            else if (i == 1) {
                column.setPreferredWidth(75);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(5);
            }
        }
        // Setup sorting
        setupRowSorter(inTable, 1);
    }


    private static void setupRowSorter(JTable inTable, int inColumn) {
        List<SortKey> tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(inColumn, SortOrder.ASCENDING));
        inTable.getRowSorter().setSortKeys(tempList);
    }

    /**
     * (From DefaultTableModel)
     * Returns a vector that contains the same objects as the array.
     * @param inArray  the array to be converted
     * @return  the new vector; if <code>anArray</code> is <code>null</code>,
     *                          returns <code>null</code>
     */
    public static Vector convertToVector(Object[] inArray) {
        if (inArray == null) {
            return null;
        }
        Vector<Object> vector = new Vector<Object>(inArray.length);
        for (Object object : inArray) {
            vector.addElement(object);
        }
        return vector;
    }

    /**
     * (From DefaultTableModel)
     * Returns a vector of vectors that contains the same objects as the array.
     * @param inArray  the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is
     *                          <code>null</code>, returns <code>null</code>
     */
    public static Vector convertToVector(Object[][] inArray) {
        if (inArray == null) {
            return null;
        }
        Vector<Vector> vector = new Vector<Vector>(inArray.length);
        for (Object[] objectArray : inArray) {
            vector.addElement(convertToVector(objectArray));
        }
        return vector;
    }

}
