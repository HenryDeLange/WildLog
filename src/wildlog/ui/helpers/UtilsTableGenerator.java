package wildlog.ui.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.cellrenderers.DateCellRenderer;
import wildlog.ui.helpers.cellrenderers.DateTimeCellRenderer;
import wildlog.ui.helpers.cellrenderers.IconCellRenderer;
import wildlog.ui.helpers.cellrenderers.TextCellRenderer;
import wildlog.ui.helpers.cellrenderers.WildLogDataModelWrappertCellRenderer;
import wildlog.ui.helpers.cellrenderers.WildLogTableModel;
import wildlog.ui.helpers.cellrenderers.WildLogTableModelDataWrapper;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogThumbnailSizes;

public final class UtilsTableGenerator {

    private UtilsTableGenerator() {
    }

    public static void setupElementTableLarge(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        "Other Name",
                                        "Type",
                                        "Class",
                                        "Wish Rating",
                                        "Add Frequency"
                                        };
                // Load data from DB
                final List<Element> listElements = inApp.getDBI().list(inElement);
                if (!listElements.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listElements.size()][columnNames.length];
                    for (int t = 0; t < listElements.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Element tempElement = listElements.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][i++] = tempElement.getPrimaryName();
                                data[finalT][i++] = tempElement.getOtherName();
                                data[finalT][i++] = tempElement.getType();
                                data[finalT][i++] = tempElement.getFeedingClass();
                                data[finalT][i++] = tempElement.getWishListRating();
                                data[finalT][i++] = tempElement.getAddFrequency();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(240);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(170);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(70);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(80);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(110);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(155);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(90);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(95);
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupElementTableSmall(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        "Type"
                                        };
                // Load data from DB
                final List<Element> listElements = inApp.getDBI().list(inElement);
                if (!listElements.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listElements.size()][columnNames.length];
                    for (int t = 0; t < listElements.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Element tempElement = listElements.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][i++] = tempElement.getPrimaryName();
                                data[finalT][i++] = tempElement.getType();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(60);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableLarge(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Place Name",
                                        "Rating",
                                        "Wildlife Rating",
                                        "Latitude",
                                        "Longitude"
                                        };
                // Load data from DB
                final List<Location> listLocations = inApp.getDBI().list(inLocation);
                if (!listLocations.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listLocations.size()][columnNames.length];
                    for (int t = 0; t < listLocations.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Location tempLocation = listLocations.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][i++] = tempLocation.getName();
                                data[finalT][i++] = tempLocation.getRating();
                                data[finalT][i++] = tempLocation.getGameViewingRating();
                                data[finalT][i++] = new WildLogTableModelDataWrapper(
                                        UtilsGps.getLatitudeString(tempLocation), UtilsGps.getLatDecimalDegree(tempLocation));
                                data[finalT][i++] = new WildLogTableModelDataWrapper(
                                        UtilsGps.getLongitudeString(tempLocation), UtilsGps.getLonDecimalDegree(tempLocation));
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(200);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(125);
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableLarge(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Period Name",
                                        "Start Date",
                                        "End Date",
                                        "Period Type",
                                        "Observations",
                                        "Creatures"
                                        };
                // Load data from DB
                Visit temp = new Visit();
                temp.setLocationName(inLocation.getName());
                final List<Visit> listVisits = inApp.getDBI().list(temp);
                if (!listVisits.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listVisits.size()][columnNames.length];
                    for (int t = 0; t < listVisits.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Visit tempVisit = listVisits.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempVisit);
                                data[finalT][i++] = tempVisit.getName();
                                data[finalT][i++] = tempVisit.getStartDate();
                                data[finalT][i++] = tempVisit.getEndDate();
                                data[finalT][i++] = tempVisit.getType();
                                Sighting tempSighting = new Sighting();
                                tempSighting.setVisitName(tempVisit.getName());
                                List<Sighting> listSightings = inApp.getDBI().list(tempSighting);
                                data[finalT][i++] = listSightings.size();
                                Set<String> countElements = new HashSet<String>(listSightings.size()/2);
                                for (Sighting sighting : listSightings) {
                                    if (!countElements.contains(sighting.getElementName())) {
                                        countElements.add(sighting.getElementName());
                                    }
                                }
                                data[finalT][i++] = inApp.getDBI().list(tempSighting).size();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(130);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(80);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(85);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(70);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(80);
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 3, 1, SortOrder.DESCENDING, SortOrder.ASCENDING, SortOrder.ASCENDING);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableSmallWithSightings(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Period Name",
                                        "Start Date",
                                        "Observations"
                                        };
                // Load data from DB
                if (inLocation != null) {
                    Visit temp = new Visit();
                    temp.setLocationName(inLocation.getName());
                    final List<Visit> listVisits = inApp.getDBI().list(temp);
                    if (!listVisits.isEmpty()) {
                        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                        // Setup new table data
                        final Object[][] data = new Object[listVisits.size()][columnNames.length];
                        for (int t = 0; t < listVisits.size(); t++) {
                            final int finalT = t;
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    int i = 0;
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][i++] = tempVisit.getName();
                                    data[finalT][i++] = tempVisit.getStartDate();
                                    Sighting tempSighting = new Sighting();
                                    tempSighting.setVisitName(tempVisit.getName());
                                    data[finalT][i++] = inApp.getDBI().list(tempSighting).size();
                                }
                            });
                        }
                        UtilsConcurency.waitForExecutorToShutdown(executorService);
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(90);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(90);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(45);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(85);
                        // Setup default sorting
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                    }
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableSmallWithType(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Period Name",
                                        "Start Date",
                                        "Type"
                                        };
                // Load data from DB
                if (inLocation != null) {
                    Visit temp = new Visit();
                    temp.setLocationName(inLocation.getName());
                    final List<Visit> listVisits = inApp.getDBI().list(temp);
                    if (!listVisits.isEmpty()) {
                        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                        // Setup new table data
                        final Object[][] data = new Object[listVisits.size()][columnNames.length];
                        for (int t = 0; t < listVisits.size(); t++) {
                            final int finalT = t;
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    int i = 0;
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][i++] = tempVisit.getName();
                                    data[finalT][i++] = tempVisit.getStartDate();
                                    data[finalT][i++] = tempVisit.getType();
                                }
                            });
                        }
                        UtilsConcurency.waitForExecutorToShutdown(executorService);
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(100);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(90);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(55);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(125);
                        // Setup default sorting
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                    }
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"Please Select a Place"}, 0));
                }
            }
        });
    }

    public static void setupSightingTableLarge(final WildLogApp inApp, final JTable inTable, final Visit inVisit) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        "Date",
                                        "Evidence",
                                        "Certainty",
                                        "Type",
                                        "ID",
                                        "GPS"
                                        };
                // Load data from DB
                Sighting sighting = new Sighting();
                sighting.setVisitName(inVisit.getName());
                final List<Sighting> listSightings = inApp.getDBI().list(sighting);
                if (!listSightings.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listSightings.size()][columnNames.length];
                    for (int t = 0; t < listSightings.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Sighting tempSighting = listSightings.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempSighting);
                                data[finalT][i++] = tempSighting.getElementName();
                                data[finalT][i++] = tempSighting.getDate();
                                data[finalT][i++] = tempSighting.getSightingEvidence();
                                data[finalT][i++] = tempSighting.getCertainty();
                                data[finalT][i++] = inApp.getDBI().find(new Element(tempSighting.getElementName())).getType();
                                data[finalT][i++] = tempSighting.getSightingCounter();
                                if (tempSighting.getLatitude() != null && tempSighting.getLongitude() != null) {
                                    if (!tempSighting.getLatitude().equals(Latitudes.NONE) && !tempSighting.getLongitude().equals(Longitudes.NONE)) {
                                        data[finalT][i++] = "GPS";
                                    }
                                    else {
                                        data[finalT][i++] = "";
                                    }
                                }
                                else {
                                    data[finalT][i++] = "";
                                }
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(80);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(45);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(75);
                    inTable.getColumnModel().getColumn(7).setPreferredWidth(35);
                    inTable.getColumnModel().getColumn(7).setMaxWidth(35);
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
                }
            }
        });
    }

    public static void setupElementsTableMediumForVisit(final WildLogApp inApp, final JTable inTable, final Visit inVisit) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        "Type",
                                        "Class",
                                        "Observations"
                                        };
                // Load data from DB
                if (inVisit != null) {
                    Sighting temp = new Sighting();
                    temp.setVisitName(inVisit.getName());
                    List<Sighting> allSightings = inApp.getDBI().list(temp);
                    final List<String> listElements = new ArrayList<String>();
                    for (Sighting tempSighting : allSightings) {
                        if (!listElements.contains(tempSighting.getElementName())) {
                            listElements.add(tempSighting.getElementName());
                        }
                    }
                    if (!listElements.isEmpty()) {
                        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                        // Setup new table data
                        final Object[][] data = new Object[listElements.size()][columnNames.length];
                        for (int t = 0; t < listElements.size(); t++) {
                            final int finalT = t;
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    int i = 0;
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][i++] = tempElement.getPrimaryName();
                                    data[finalT][i++] = tempElement.getType();
                                    data[finalT][i++] = tempElement.getFeedingClass();
                                    Sighting tempSighting = new Sighting();
                                    tempSighting.setVisitName(inVisit.getName());
                                    tempSighting.setElementName(tempElement.getPrimaryName());
                                    data[finalT][i++] = inApp.getDBI().list(tempSighting).size();
                                }
                            });
                        }
                        UtilsConcurency.waitForExecutorToShutdown(executorService);
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(85);
                        // Setup default sorting
                        setupRowSorter(inTable, 4, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                    }
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupElementsTableMediumForLocation(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        "Type",
                                        "Class",
                                        "Observations"
                                        };
                // Load data from DB
                if (inLocation != null) {
                    Sighting temp = new Sighting();
                    temp.setLocationName(inLocation.getName());
                    List<Sighting> allSightings = inApp.getDBI().list(temp);
                    final List<String> listElements = new ArrayList<String>();
                    for (Sighting tempSighting : allSightings) {
                        if (!listElements.contains(tempSighting.getElementName())) {
                            listElements.add(tempSighting.getElementName());
                        }
                    }
                    if (!listElements.isEmpty()) {
                        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                        // Setup new table data
                        final Object[][] data = new Object[listElements.size()][columnNames.length];
                        for (int t = 0; t < listElements.size(); t++) {
                            final int finalT = t;
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    int i = 0;
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][i++] = tempElement.getPrimaryName();
                                    data[finalT][i++] = tempElement.getType();
                                    data[finalT][i++] = tempElement.getFeedingClass();
                                    Sighting tempSighting = new Sighting();
                                    tempSighting.setLocationName(inLocation.getName());
                                    tempSighting.setElementName(tempElement.getPrimaryName());
                                    data[finalT][i++] = inApp.getDBI().list(tempSighting).size();
                                }
                            });
                        }
                        UtilsConcurency.waitForExecutorToShutdown(executorService);
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(85);
                        // Setup default sorting
                        setupRowSorter(inTable, 4, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                    }
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupLocationsTableMedium(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Place Name",
                                        "Observations"
                                        };
                // Load data from DB
                final List<LocationCount> listLocationCounts = inApp.getDBI().queryLocationCountForElement(inElement);
                if (!listLocationCounts.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listLocationCounts.size()][columnNames.length];
                    for (int t = 0; t < listLocationCounts.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Location tempLocation = inApp.getDBI().find(new Location(listLocationCounts.get(finalT).getLocationName()));
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][i++] = tempLocation.getName();
                                data[finalT][i++] = listLocationCounts.get(finalT).getCount();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(125);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableSmall(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Place Name"
                                        };
                // Load data from DB
                final List<Location> listLocations = inApp.getDBI().list(inLocation);
                if (!listLocations.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listLocations.size()][columnNames.length];
                    for (int t = 0; t < listLocations.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Location tempLocation = listLocations.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][i++] = tempLocation.getName();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(125);
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
     }

    public static void setupSightingsTableSmall(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Place Name",
                                        "Date",
                                        "ID" // hidden field
                                        };
                // Load data from DB
                Sighting tempSighting = new Sighting();
                tempSighting.setElementName(inElement.getPrimaryName());
                final List<Sighting> listSightings = inApp.getDBI().list(tempSighting);
                if (!listSightings.isEmpty()) {
                    ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
                    // Setup new table data
                    final Object[][] data = new Object[listSightings.size()][columnNames.length + 1];
                    for (int t = 0; t < listSightings.size(); t++) {
                        final int finalT = t;
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                int i = 0;
                                Sighting tempSighting = listSightings.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempSighting);
                                data[finalT][i++] = tempSighting.getLocationName();
                                data[finalT][i++] = tempSighting.getDate();
                                data[finalT][i++] = tempSighting.getSightingCounter();
                            }
                        });
                    }
                    UtilsConcurency.waitForExecutorToShutdown(executorService);
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(135);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(75);
                    inTable.removeColumn(inTable.getColumnModel().getColumn(3));
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
                }
            }
        });
    }


    private static void setupRowSorter(JTable inTable, int inColumn) {
        List<SortKey> tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(inColumn, SortOrder.ASCENDING));
        inTable.getRowSorter().setSortKeys(tempList);
    }

    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, SortOrder inSortOrder1, SortOrder inSortOrder2) {
        List<SortKey> tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        inTable.getRowSorter().setSortKeys(tempList);
    }

    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, int inColumn3, SortOrder inSortOrder1, SortOrder inSortOrder2, SortOrder inSortOrder3) {
        List<SortKey> tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        tempList.add(new SortKey(inColumn3, inSortOrder3));
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

    private static void setupTableModel(JTable inTable, Object[][] inData, String[] inColumnNames) {
        inTable.setModel(new WildLogTableModel(inData, inColumnNames));
    }

    public static ImageIcon setupThumbnailIcon(WildLogApp inApp, DataObjectWithWildLogFile inDataObjectWithWildLogFile) {
        WildLogFile wildLogFile = inApp.getDBI().find(new WildLogFile(inDataObjectWithWildLogFile.getWildLogFileID()));
        if (wildLogFile != null) {
            return UtilsImageProcessing.getScaledIcon(
                    wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL),
                    WildLogThumbnailSizes.VERY_SMALL.getSize());
        }
        return UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.VERY_SMALL);
    }

    private static void setupRenderersAndThumbnailRows(JTable inTable, boolean inShowDatesWithTime) {
        inTable.setRowHeight(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
        inTable.getColumnModel().getColumn(0).setMinWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
        inTable.getColumnModel().getColumn(0).setMaxWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
        inTable.getColumnModel().getColumn(0).setCellRenderer(new IconCellRenderer(WildLogThumbnailSizes.VERY_SMALL.getSize()));
        inTable.setDefaultRenderer(Object.class, new TextCellRenderer(1));
        inTable.setDefaultRenderer(Integer.class, new TextCellRenderer(1));
        inTable.setDefaultRenderer(Long.class, new TextCellRenderer(1));
        inTable.setDefaultRenderer(WildLogTableModelDataWrapper.class, new WildLogDataModelWrappertCellRenderer());
        if (inShowDatesWithTime) {
            inTable.setDefaultRenderer(Date.class, new DateTimeCellRenderer());
        }
        else {
            inTable.setDefaultRenderer(Date.class, new DateCellRenderer());
        }
    }

    private static void setupLoadingHeader(JTable inTable) {
        // Setup header
        inTable.getTableHeader().setReorderingAllowed(false);
        ((DefaultTableCellRenderer) inTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        // Setup loading message
        inTable.setModel(new DefaultTableModel(new String[]{"Loading..."}, 0));
    }

}
