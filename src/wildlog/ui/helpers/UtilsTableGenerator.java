package wildlog.ui.helpers;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.cellrenderers.DateCellRenderer;
import wildlog.ui.helpers.cellrenderers.DateTimeCellRenderer;
import wildlog.ui.helpers.cellrenderers.IconCellRenderer;
import wildlog.ui.helpers.cellrenderers.TextCellRenderer;
import wildlog.ui.helpers.cellrenderers.WildLogDataModelWrapperCellRenderer;
import wildlog.ui.helpers.cellrenderers.WildLogTableModel;
import wildlog.ui.helpers.cellrenderers.WildLogTableModelDataWrapper;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsImageProcessing;


public final class UtilsTableGenerator {
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_TableGenerator"));

    private UtilsTableGenerator() {
    }

    public static void setupElementTableLarge(final WildLogApp inApp, final JTable inTable, final Element inElement, String inFilterText) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String additionalNameColumn;
                if (WildLogApp.getApplication().getWildLogOptions().isUseScientificNames()) {
                    additionalNameColumn = "Scientific Name";
                }
                else {
                    additionalNameColumn = "Other Name";
                }
                // Setup column names
                String[] columnNames = {
                                        "",
                                        "Creature Name",
                                        additionalNameColumn,
                                        "Type",
                                        "Class",
                                        "Wish Rating",
                                        "Add Frequency"
                                        };
                // Load data from DB
                final List<Element> listElements = inApp.getDBI().list(inElement);
                if (!listElements.isEmpty()) {
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listElements.size());
                    // Setup new table data
                    final Object[][] data = new Object[listElements.size()][columnNames.length];
                    for (int t = 0; t < listElements.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                int i = 0;
                                Element tempElement = listElements.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][i++] = tempElement.getPrimaryName();
                                if (WildLogApp.getApplication().getWildLogOptions().isUseScientificNames()) {
                                    data[finalT][i++] = tempElement.getScientificName();
                                }
                                else {
                                    data[finalT][i++] = tempElement.getOtherName();
                                }
                                data[finalT][i++] = tempElement.getType();
                                data[finalT][i++] = tempElement.getFeedingClass();
                                data[finalT][i++] = tempElement.getWishListRating();
                                data[finalT][i++] = tempElement.getAddFrequency();
                                return null;
                            }
                        });
                    }
                    // Call the actions and wait for all to finish
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(220);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(240);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(170);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(320);
                    inTable.getColumnModel().getColumn(3).setMinWidth(60);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(70);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(80);
                    inTable.getColumnModel().getColumn(4).setMinWidth(90);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(110);
                    inTable.getColumnModel().getColumn(5).setMinWidth(140);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(155);
                    inTable.getColumnModel().getColumn(6).setMinWidth(80);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(90);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(95);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                    // FIXME: The code below is copied from UtilsUI.attachKeyListernerToFilterTableRows(). This is not ideal.
                    String[] oldSelection = UtilsTableGenerator.getSelectedRowIDs(inTable, 1);
                    TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)inTable.getRowSorter();
                    if (sorter == null) {
                        sorter = new TableRowSorter<>(inTable.getModel());
                    }
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(inFilterText), 1));
                    inTable.setRowSorter(sorter);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupElementTableSmall(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listElements.size());
                    // Setup new table data
                    final Object[][] data = new Object[listElements.size()][columnNames.length];
                    for (int t = 0; t < listElements.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                int i = 0;
                                Element tempElement = listElements.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][i++] = tempElement.getPrimaryName();
                                data[finalT][i++] = tempElement.getType();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(140);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(2).setMinWidth(50);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(60);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableLarge(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listLocations.size());
                    // Setup new table data
                    final Object[][] data = new Object[listLocations.size()][columnNames.length];
                    for (int t = 0; t < listLocations.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
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
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(180);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(200);
                    inTable.getColumnModel().getColumn(2).setMinWidth(90);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(3).setMinWidth(90);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(100);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(4).setMinWidth(100);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(5).setMinWidth(100);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(125);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableLarge(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listVisits.size());
                    // Setup new table data
                    final Object[][] data = new Object[listVisits.size()][columnNames.length];
                    for (int t = 0; t < listVisits.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
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
                                data[finalT][i++] = countElements.size();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(110);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(130);
                    inTable.getColumnModel().getColumn(2).setMinWidth(75);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(3).setMinWidth(75);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(85);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(4).setMinWidth(70);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(80);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(5).setMinWidth(65);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(85);
                    inTable.getColumnModel().getColumn(6).setMinWidth(60);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(70);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(80);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 3, 1, SortOrder.DESCENDING, SortOrder.DESCENDING, SortOrder.DESCENDING);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableSmallWithSightings(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listVisits.size());
                        // Setup new table data
                        final Object[][] data = new Object[listVisits.size()][columnNames.length];
                        for (int t = 0; t < listVisits.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    int i = 0;
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][i++] = tempVisit.getName();
                                    data[finalT][i++] = tempVisit.getStartDate();
                                    data[finalT][i++] = inApp.getDBI().count(new Sighting(null, null, tempVisit.getName()));
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setMinWidth(75);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(2).setMinWidth(80);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(90);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(90);
                        inTable.getColumnModel().getColumn(3).setMinWidth(35);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(45);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(85);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 1);
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
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listVisits.size());
                        // Setup new table data
                        final Object[][] data = new Object[listVisits.size()][columnNames.length];
                        for (int t = 0; t < listVisits.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    int i = 0;
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][i++] = tempVisit.getName();
                                    data[finalT][i++] = tempVisit.getStartDate();
                                    data[finalT][i++] = tempVisit.getType();
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setMinWidth(90);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(100);
                        inTable.getColumnModel().getColumn(2).setMinWidth(75);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(90);
                        inTable.getColumnModel().getColumn(3).setMinWidth(45);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(55);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(125);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 1);
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
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 6);
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
                if (inVisit != null) {
                    Sighting sighting = new Sighting();
                    sighting.setVisitName(inVisit.getName());
                    final List<Sighting> listSightings = inApp.getDBI().list(sighting);
                    if (!listSightings.isEmpty()) {
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listSightings.size());
                        // Setup new table data
                        final Object[][] data = new Object[listSightings.size()][columnNames.length];
                        for (int t = 0; t < listSightings.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
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
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, true);
                        inTable.getColumnModel().getColumn(1).setMinWidth(100);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMinWidth(100);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(115);
                        inTable.getColumnModel().getColumn(3).setMinWidth(50);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(60);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(4).setMinWidth(55);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(65);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(115);
                        inTable.getColumnModel().getColumn(5).setMinWidth(60);
                        inTable.getColumnModel().getColumn(5).setPreferredWidth(70);
                        inTable.getColumnModel().getColumn(5).setMaxWidth(80);
                        inTable.getColumnModel().getColumn(6).setMinWidth(25);
                        inTable.getColumnModel().getColumn(6).setPreferredWidth(35);
                        inTable.getColumnModel().getColumn(6).setMaxWidth(125);
                        inTable.getColumnModel().getColumn(7).setMinWidth(20);
                        inTable.getColumnModel().getColumn(7).setPreferredWidth(30);
                        inTable.getColumnModel().getColumn(7).setMaxWidth(35);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 6);
                    }
                    else {
                        inTable.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
                    }
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
                }
            }
        });
    }

    public static void setupElementsTableMediumForVisit(final WildLogApp inApp, final JTable inTable, final Visit inVisit) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listElements.size());
                        // Setup new table data
                        final Object[][] data = new Object[listElements.size()][columnNames.length];
                        for (int t = 0; t < listElements.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    int i = 0;
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][i++] = tempElement.getPrimaryName();
                                    data[finalT][i++] = tempElement.getType();
                                    data[finalT][i++] = tempElement.getFeedingClass();
                                    data[finalT][i++] = inApp.getDBI().count(new Sighting(tempElement.getPrimaryName(), null, inVisit.getName()));
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setMinWidth(90);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMinWidth(70);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                        inTable.getColumnModel().getColumn(3).setMinWidth(70);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(4).setMinWidth(65);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(85);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 4, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 1);
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
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listElements.size());
                        // Setup new table data
                        final Object[][] data = new Object[listElements.size()][columnNames.length];
                        for (int t = 0; t < listElements.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    int i = 0;
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][i++] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][i++] = tempElement.getPrimaryName();
                                    data[finalT][i++] = tempElement.getType();
                                    data[finalT][i++] = tempElement.getFeedingClass();
                                    data[finalT][i++] = inApp.getDBI().count(new Sighting(tempElement.getPrimaryName(), inLocation.getName(), null));
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, false);
                        inTable.getColumnModel().getColumn(1).setMinWidth(90);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMinWidth(70);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                        inTable.getColumnModel().getColumn(3).setMinWidth(70);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(4).setMinWidth(65);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(85);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 4, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 1);
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
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                final List<LocationCount> listLocationCounts = inApp.getDBI().queryLocationCountForElement(inElement, LocationCount.class);
                if (!listLocationCounts.isEmpty()) {
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listLocationCounts.size());
                    // Setup new table data
                    final Object[][] data = new Object[listLocationCounts.size()][columnNames.length];
                    for (int t = 0; t < listLocationCounts.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                int i = 0;
                                Location tempLocation = inApp.getDBI().find(new Location(listLocationCounts.get(finalT).getLocationName()));
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][i++] = tempLocation.getName();
                                data[finalT][i++] = listLocationCounts.get(finalT).getCount();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(105);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(125);
                    inTable.getColumnModel().getColumn(2).setMinWidth(65);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableSmall(final WildLogApp inApp, final JTable inTable, final Location inLocation) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 1);
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
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listLocations.size());
                    // Setup new table data
                    final Object[][] data = new Object[listLocations.size()][columnNames.length];
                    for (int t = 0; t < listLocations.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                int i = 0;
                                Location tempLocation = listLocations.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][i++] = tempLocation.getName();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(105);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(125);
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
     }

    public static void setupSightingsTableSmall(final WildLogApp inApp, final JTable inTable, final Element inElement) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs;
        if (inTable.getModel().getColumnCount() < 4) {
            // Note: If the location radio button was previously selected the passed in table will not have 4 columns.
            selectedRowIDs = new String[0];
        }
        else {
            selectedRowIDs = getSelectedRowIDs(inTable, 3);
        }
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
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listSightings.size());
                    // Setup new table data
                    final Object[][] data = new Object[listSightings.size()][columnNames.length + 1];
                    for (int t = 0; t < listSightings.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                int i = 0;
                                Sighting tempSighting = listSightings.get(finalT);
                                data[finalT][i++] = setupThumbnailIcon(inApp, tempSighting);
                                data[finalT][i++] = tempSighting.getLocationName();
                                data[finalT][i++] = tempSighting.getDate();
                                data[finalT][i++] = tempSighting.getSightingCounter();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false);
                    inTable.getColumnModel().getColumn(1).setMinWidth(115);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(135);
                    inTable.getColumnModel().getColumn(2).setMinWidth(65);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(75);
                    // Hide the field from the view, but I need it for loading the Sighting
                    inTable.removeColumn(inTable.getColumnModel().getColumn(3));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 3);
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
    
    public static void setupPreviousRowSelection(JTable inTable, String[] inSelectedRowIDs, int inCol) {
        if (inSelectedRowIDs.length > 0) {
            int found = 0;
            for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                for (String selectedRowID : inSelectedRowIDs) {
                    if (inTable.getModel().getValueAt(inTable.convertRowIndexToModel(t), inCol).toString().equals(selectedRowID)) {
                        inTable.getSelectionModel().addSelectionInterval(t, t);
                        inTable.scrollRectToVisible(inTable.getCellRect(t, 0, true));
                        int x;
                        int y;
                        if (inTable.getMousePosition() != null) {
                            x = inTable.getMousePosition().x;
                            y = inTable.getMousePosition().y;
                        }
                        else {
                            x = inTable.getX();
                            y = inTable.getY();
                        }
                        if (inSelectedRowIDs.length == 1) {
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                    new UtilsUI.GeneratedMouseEvent(inTable, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false));
                        }
                        found++;
                        break;
                    }
                }
                if (found == inSelectedRowIDs.length) {
                    break;
                }
            }
            if (found == 0) {
                // Need to clear the related tables
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new UtilsUI.GeneratedMouseEvent(inTable, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, -1, -1, 1, false));
            }
        }
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

    private static ImageIcon setupThumbnailIcon(WildLogApp inApp, DataObjectWithWildLogFile inDataObjectWithWildLogFile) {
        if (inApp.getWildLogOptions().isUseThumbnailTables()) {
            WildLogFile wildLogFile = inApp.getDBI().find(new WildLogFile(inDataObjectWithWildLogFile.getWildLogFileID()));
            if (wildLogFile != null) {
                return new ImageIcon(wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL).toString());
            }
            return UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.VERY_SMALL);
        }
        else {
            return null;
        }
    }

    private static void setupRenderersAndThumbnailRows(JTable inTable, boolean inShowDatesWithTime) {
        int primaryColumn;
        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
            inTable.setRowHeight(25);
            primaryColumn = 0;
        }
        else {
            inTable.setRowHeight(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0).setMinWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0).setMaxWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0).setCellRenderer(new IconCellRenderer(WildLogThumbnailSizes.VERY_SMALL.getSize()));
            primaryColumn = 1;
        }
        inTable.setDefaultRenderer(Object.class, new TextCellRenderer(primaryColumn));
        inTable.setDefaultRenderer(Integer.class, new TextCellRenderer(primaryColumn));
        inTable.setDefaultRenderer(Long.class, new TextCellRenderer(primaryColumn));
        inTable.setDefaultRenderer(WildLogTableModelDataWrapper.class, new WildLogDataModelWrapperCellRenderer());
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
    
    public static void setupColumnResizingListener(JTable inTable, int inCol) {
        inTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (inTable.getColumnModel().getColumnCount() > inCol) {
                            int otherColumnsMaxSize = 0;
                            for (int t = 0; t < inTable.getColumnModel().getColumnCount(); t++) {
                                if (t != inCol) {
                                    otherColumnsMaxSize = otherColumnsMaxSize + inTable.getColumnModel().getColumn(t).getMaxWidth();
                                }
                            }
                            if (inTable.getWidth() - otherColumnsMaxSize > inTable.getColumnModel().getColumn(inCol).getPreferredWidth()) {
                                inTable.getColumnModel().getColumn(inCol).setMaxWidth(inTable.getWidth() - otherColumnsMaxSize);
                            }
                            else {
                                inTable.getColumnModel().getColumn(inCol).setMaxWidth(inTable.getColumnModel().getColumn(inCol).getPreferredWidth() + inTable.getWidth()/4);
                            }
                            int[] selectedRows = inTable.getSelectedRows();
                            ((DefaultTableModel) inTable.getModel()).fireTableDataChanged();
                            for (int row : selectedRows) {
                                inTable.getSelectionModel().addSelectionInterval(row, row);
                            }
                        }
                    }
                });
            }
        });
    }
    
    public static String[] getSelectedRowIDs(JTable inTable, int inCol) {
        String[] selectedRowIDs = new String[inTable.getSelectedRowCount()];
        for (int t = 0; t < inTable.getSelectedRowCount(); t++) {
            selectedRowIDs[t] = inTable.getModel().getValueAt(inTable.convertRowIndexToModel(inTable.getSelectedRows()[t]), inCol).toString();
        }
        return selectedRowIDs;
    }

}
