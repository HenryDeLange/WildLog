package wildlog.ui.helpers;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.FilterPropertiesDialog;
import wildlog.ui.helpers.renderers.ButtonTableRenderer;
import wildlog.ui.helpers.renderers.DateCellRenderer;
import wildlog.ui.helpers.renderers.DateTimeCellRenderer;
import wildlog.ui.helpers.renderers.IconCellRenderer;
import wildlog.ui.helpers.renderers.SelectedIndicatorCellRenderer;
import wildlog.ui.helpers.renderers.TextCellRenderer;
import wildlog.ui.helpers.renderers.WildLogDataModelWrapperCellRenderer;
import wildlog.ui.helpers.renderers.WildLogTableModel;
import wildlog.ui.helpers.renderers.WildLogTableModelDataWrapper;
import wildlog.ui.helpers.renderers.editors.ButtonTableEditor;
import wildlog.ui.maps.implementations.helpers.DistributionLayerCellRenderer;
import wildlog.ui.reports.helpers.FilterProperties;
import wildlog.ui.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;


public final class UtilsTableGenerator {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_TableGenerator"));
    // NOTE: Ek kan dalk eendag 'n ConnectionPool gebruik vir elke table thread want hulle almal gebruik tans die een connection.
    //       Maar ek het 'n toets gedoen met 'n ConnectionPool en dit maak nog nie juis 'n groot verskil nie, so vir eenvoud hou ek dit soos dit nou is.

    private UtilsTableGenerator() {
    }

    public static void setupElementTableLarge(final WildLogApp inApp, final JTable inTable, final Element inElement, final String inFilterText) {
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
                                        "Observations"
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
                                Element tempElement = listElements.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][1] = tempElement.getPrimaryName();
                                if (WildLogApp.getApplication().getWildLogOptions().isUseScientificNames()) {
                                    data[finalT][2] = tempElement.getScientificName();
                                }
                                else {
                                    data[finalT][2] = tempElement.getOtherName();
                                }
                                data[finalT][3] = tempElement.getType();
                                data[finalT][4] = tempElement.getFeedingClass();
                                data[finalT][5] = tempElement.getWishListRating();
                                data[finalT][6] = inApp.getDBI().count(new Sighting(tempElement.getPrimaryName(), null, null));
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                    // Since the table model has changed, the row sorter needs to be setup again
// FIXME: The code below is copied from UtilsUI.attachKeyListernerToFilterTableRows(). This is not ideal, but OK for now...
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
                                Element tempElement = listElements.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempElement);
                                data[finalT][1] = tempElement.getPrimaryName();
                                data[finalT][2] = tempElement.getType();
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                                        "Longitude",
                                        "Observations"
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
                                Location tempLocation = listLocations.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][1] = tempLocation.getName();
                                data[finalT][2] = tempLocation.getRating();
                                data[finalT][3] = tempLocation.getGameViewingRating();
                                data[finalT][4] = new WildLogTableModelDataWrapper(
                                        UtilsGPS.getLatitudeString(tempLocation), 
                                        UtilsGPS.getLatDecimalDegree(tempLocation));
                                data[finalT][5] = new WildLogTableModelDataWrapper(
                                        UtilsGPS.getLongitudeString(tempLocation), 
                                        UtilsGPS.getLonDecimalDegree(tempLocation));
                                data[finalT][6] = inApp.getDBI().count(new Sighting(null, tempLocation.getName(), null));
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                    inTable.getColumnModel().getColumn(6).setMinWidth(80);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(90);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(95);
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
                                Visit tempVisit = listVisits.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempVisit);
                                data[finalT][1] = tempVisit.getName();
                                data[finalT][2] = tempVisit.getStartDate();
                                data[finalT][3] = tempVisit.getEndDate();
                                data[finalT][4] = tempVisit.getType();
                                Sighting tempSighting = new Sighting();
                                tempSighting.setVisitName(tempVisit.getName());
                                List<Sighting> listSightings = inApp.getDBI().list(tempSighting, false);
                                data[finalT][5] = listSightings.size();
                                Set<String> countElements = new HashSet<String>(listSightings.size()/2);
                                for (Sighting sighting : listSightings) {
                                    if (!countElements.contains(sighting.getElementName())) {
                                        countElements.add(sighting.getElementName());
                                    }
                                }
                                data[finalT][6] = countElements.size();
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][1] = tempVisit.getName();
                                    data[finalT][2] = tempVisit.getStartDate();
                                    data[finalT][3] = inApp.getDBI().count(new Sighting(null, null, tempVisit.getName()));
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
                        setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                                    Visit tempVisit = listVisits.get(finalT);
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempVisit);
                                    data[finalT][1] = tempVisit.getName();
                                    data[finalT][2] = tempVisit.getStartDate();
                                    data[finalT][3] = tempVisit.getType();
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
                        setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                    final List<Sighting> listSightings = inApp.getDBI().list(sighting, false);
                    if (!listSightings.isEmpty()) {
                        Collection<Callable<Object>> listCallables = new ArrayList<>(listSightings.size());
                        // Setup new table data
                        final Object[][] data = new Object[listSightings.size()][columnNames.length];
                        for (int t = 0; t < listSightings.size(); t++) {
                            final int finalT = t;
                            listCallables.add(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    Sighting tempSighting = listSightings.get(finalT);
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempSighting);
                                    data[finalT][1] = tempSighting.getElementName();
                                    data[finalT][2] = tempSighting.getDate();
                                    data[finalT][3] = tempSighting.getSightingEvidence();
                                    data[finalT][4] = tempSighting.getCertainty();
                                    data[finalT][5] = inApp.getDBI().find(new Element(tempSighting.getElementName())).getType();
                                    data[finalT][6] = tempSighting.getSightingCounter();
                                    if (tempSighting.getLatitude() != null && tempSighting.getLongitude() != null) {
                                        if (!tempSighting.getLatitude().equals(Latitudes.NONE) && !tempSighting.getLongitude().equals(Longitudes.NONE)) {
                                            data[finalT][7] = "GPS";
                                        }
                                        else {
                                            data[finalT][7] = "";
                                        }
                                    }
                                    else {
                                        data[finalT][7] = "";
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
                        setupRenderersAndThumbnailRows(inTable, true, false, 0);
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
    
    public static void setupSightingTableForMainTab(final WildLogApp inApp, final JTable inTable, final JLabel inLblFilterDetails,
            final FilterProperties inFilterProperties, final List<Location> inActiveLocations, 
            final List<Visit> inActiveVisits, final List<Element> inActiveElements, 
            final double inNorthEast_Lat, final double inNorthEast_Lon, final double inSouthWest_Lat, final double inSouthWest_Lon) {
        // Deterimine the row IDs of the previously selected rows.
        final String[] selectedRowIDs = getSelectedRowIDs(inTable, 8);
        // Setup header
        setupLoadingHeader(inTable);
        inLblFilterDetails.setText("<html><br/>Loading filters...<br/><br/></html>");
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "Date",
                                        "Creature",
                                        "Place",
                                        "Period",
                                        "Period Type",
                                        "Creature Type",
                                        "Certainty",
                                        "Tag",
                                        "ID",
                                        "GPS"
                                        };
                // Load data from DB (filtering on date and lists)
                LocalDateTime startDateTime;
                if (inFilterProperties.getStartDate() != null) {
                    startDateTime = LocalDateTime.of(inFilterProperties.getStartDate(), LocalTime.MIN);
                }
                else {
                    startDateTime = null;
                }
                LocalDateTime endDateTime;
                if (inFilterProperties.getEndDate() != null) {
                    endDateTime = LocalDateTime.of(inFilterProperties.getEndDate(), LocalTime.MAX);
                }
                else {
                    endDateTime = null;
                }
                final List<Sighting> listSightings = inApp.getDBI().searchSightings(
                        UtilsTime.getDateFromLocalDateTime(startDateTime), UtilsTime.getDateFromLocalDateTime(endDateTime), 
                        inActiveLocations, inActiveVisits, inActiveElements, true, Sighting.class);
                if (!listSightings.isEmpty()) {
                    Collection<Callable<Object>> listCallables = Collections.synchronizedList(new ArrayList<>(listSightings.size()));
                    // Setup new table data
                    final List<Object[]> data = new ArrayList<>(listSightings.size());
                    for (int t = 0; t < listSightings.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Sighting tempSighting = listSightings.get(finalT);
                                // Do filtering on GPS point
                                boolean excludeBecauseOfGPS = false;
                                if (inNorthEast_Lat != 0.0 || inNorthEast_Lon != 0.0 || inSouthWest_Lat != 0.0 || inSouthWest_Lon != 0.0) {
                                    if (!UtilsGPS.isSightingInBox(tempSighting, inNorthEast_Lat, inNorthEast_Lon, inSouthWest_Lat, inSouthWest_Lon)) {
                                        // Skip this record because it was not inside the provided GPS box
                                        excludeBecauseOfGPS = true;
                                    }
                                }
                                // Do additional filtering on Sighting properties
                                if (!excludeBecauseOfGPS && FilterPropertiesDialog.checkFilterPropertiesMatch(inFilterProperties, tempSighting)) {
                                    // If all filters passed, then continue to add the row to the data list
                                    Object[] rowData = new Object[columnNames.length];
                                    rowData[0] = tempSighting.getDate();
                                    rowData[1] = tempSighting.getElementName();
                                    rowData[2] = tempSighting.getLocationName();
                                    rowData[3] = tempSighting.getVisitName();
                                    rowData[4] = tempSighting.getCachedVisitType();
                                    rowData[5] = tempSighting.getCachedElementType();
                                    if (tempSighting.getCertainty() != null && !Certainty.NONE.equals(tempSighting.getCertainty())
                                            && !Certainty.UNKNOWN.equals(tempSighting.getCertainty())) {
                                        String temp = tempSighting.getCertainty().toString();
                                        rowData[6] = temp.substring(temp.indexOf('(') + 1, temp.length() - 1);
                                    }
                                    else {
                                        rowData[6] = tempSighting.getCertainty();
                                    }
                                    rowData[7] = tempSighting.getTag();
                                    rowData[8] = tempSighting.getSightingCounter();
                                    if (tempSighting.getLatitude() != null && tempSighting.getLongitude() != null) {
                                        if (!tempSighting.getLatitude().equals(Latitudes.NONE) && !tempSighting.getLongitude().equals(Longitudes.NONE)) {
                                            rowData[9] = "GPS";
                                        }
                                        else {
                                            rowData[9] = "";
                                        }
                                    }
                                    else {
                                        rowData[9] = "";
                                    }
                                    data.add(rowData);
                                }
                                return null;
                            }
                        });
                    }
                    // This Sighting table is slightly different and uses filters. Wait for the table data to finish loading.
                    UtilsConcurency.waitForExecutorToRunTasks(executorService, listCallables);
                    // Create the new model
                    Object[][] tableData = new Object[data.size()][columnNames.length];
                    for(int row = 0; row < data.size(); row++) {
                        System.arraycopy(data.get(row), 0, tableData[row], 0, data.get(row).length);
                    }
                    setupTableModel(inTable, tableData, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, true, true, 0);
                    // Set a different TextCellRenderer to left align some rows
                    inTable.setDefaultRenderer(Object.class, new TextCellRenderer(0, 1, 2, 3));
                    // Continue to setup the column sizes
                    inTable.getColumnModel().getColumn(0).setMinWidth(110);
                    inTable.getColumnModel().getColumn(0).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(0).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(1).setMinWidth(100);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(1).setMaxWidth(250);
                    inTable.getColumnModel().getColumn(2).setMinWidth(100);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(135);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(280);
                    inTable.getColumnModel().getColumn(3).setMinWidth(120);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(125);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(290);
                    inTable.getColumnModel().getColumn(4).setMinWidth(55);
                    inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                    inTable.getColumnModel().getColumn(4).setMaxWidth(90);
                    inTable.getColumnModel().getColumn(5).setMinWidth(65);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(80);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(95);
                    inTable.getColumnModel().getColumn(6).setMinWidth(50);
                    inTable.getColumnModel().getColumn(6).setPreferredWidth(55);
                    inTable.getColumnModel().getColumn(6).setMaxWidth(70);
                    inTable.getColumnModel().getColumn(7).setMinWidth(35);
                    inTable.getColumnModel().getColumn(7).setPreferredWidth(45);
                    inTable.getColumnModel().getColumn(7).setMaxWidth(175);
                    inTable.getColumnModel().getColumn(8).setMinWidth(20);
                    inTable.getColumnModel().getColumn(8).setPreferredWidth(20);
                    inTable.getColumnModel().getColumn(8).setMaxWidth(125);
                    inTable.getColumnModel().getColumn(9).setMinWidth(25);
                    inTable.getColumnModel().getColumn(9).setPreferredWidth(30);
                    inTable.getColumnModel().getColumn(9).setMaxWidth(35);
                    // Setup default sorting
                    setupRowSorter(inTable, 0, 1, 2, 3, SortOrder.DESCENDING, SortOrder.ASCENDING, SortOrder.ASCENDING, SortOrder.ASCENDING);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 8);
                }
                if (inTable.getModel().getRowCount() == 0) {
                    inTable.setRowHeight(50);
                    inTable.setModel(new DefaultTableModel(new String[]{"No Observations were found that match the currently active filters."}, 1));
                    inTable.getModel().setValueAt("Filter Observations. (Click here to open the Filter Observations dialog "
                            + "or use the buttons below to refine the criteria.)", 0, 0);
                    inTable.setDefaultRenderer(Object.class, new ButtonTableRenderer());
                    inTable.setDefaultEditor(Object.class, new ButtonTableEditor());
                }
                // Need to wait for the table to finish loading before updating the label
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            int rowCount = inTable.getModel().getRowCount();
                            if (inTable.getRowHeight() == 50) {
                                rowCount = 0;
                            }
                            // Total count
                            String text = "<html>Showing " + rowCount + " (of " + inApp.getDBI().count(new Sighting()) + ") Observations.";
                            // Date range
                            if (inFilterProperties.getStartDate() != null || inFilterProperties.getEndDate() != null) {
                                text = text + "<br/>Filtering on all Observations ";
                            }
                            if (inFilterProperties.getStartDate() != null) {
                                text = text + "starting from " + inFilterProperties.getStartDate().format(UtilsTime.WL_DATE_FORMATTER);
                            }
                            if (inFilterProperties.getStartDate() != null && inFilterProperties.getEndDate() != null) {
                                text = text + " and ";
                            }
                            if (inFilterProperties.getEndDate() != null) {
                                text = text + "before " + inFilterProperties.getEndDate().format(UtilsTime.WL_DATE_FORMATTER);
                            }
                            if (inFilterProperties.getStartDate() != null || inFilterProperties.getEndDate() != null) {
                                text = text + ".";
                            }
                            // Map
                            if (inNorthEast_Lat != 0.0 || inNorthEast_Lon != 0.0 || inSouthWest_Lat != 0.0 || inSouthWest_Lon != 0.0) {
                                text = text + "<br/>Filtering on GPS coordinates.";
                            }
                            // Location, Visit and Creature filters
                            text = text + "<br/>The current filters are using"
                                    + " " + inActiveLocations.size() + " (of " + inApp.getDBI().count(new Location()) + ") Places"
                                    + ", " + inActiveVisits.size() + " (of " + inApp.getDBI().count(new Visit()) + ") Periods "
                                    + " and " + inActiveElements.size() + " (of " + inApp.getDBI().count(new Element()) + ") Creatures.";
                            // Other
                            text = text + "<br/>Additional Observation properties may also be active.</html>";
                            inLblFilterDetails.setText(text);
                        }
                    });
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
                    List<Sighting> allSightings = inApp.getDBI().list(temp, false);
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
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][1] = tempElement.getPrimaryName();
                                    data[finalT][2] = tempElement.getType();
                                    data[finalT][3] = tempElement.getFeedingClass();
                                    data[finalT][4] = inApp.getDBI().count(new Sighting(tempElement.getPrimaryName(), null, inVisit.getName()));
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
                        setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                                        "Observations"
                                        };
                // Load data from DB
                if (inLocation != null) {
                    Sighting temp = new Sighting();
                    temp.setLocationName(inLocation.getName());
                    List<Sighting> allSightings = inApp.getDBI().list(temp, false);
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
                                    Element tempElement = inApp.getDBI().find(new Element(listElements.get(finalT)));
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempElement);
                                    data[finalT][1] = tempElement.getPrimaryName();
                                    data[finalT][2] = tempElement.getType();
                                    data[finalT][3] = inApp.getDBI().count(new Sighting(tempElement.getPrimaryName(), inLocation.getName(), null));
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
                        setupRenderersAndThumbnailRows(inTable, false, false, 0);
                        inTable.getColumnModel().getColumn(1).setMinWidth(90);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMinWidth(70);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(80);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(85);
                        inTable.getColumnModel().getColumn(3).setMinWidth(65);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(85);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        setupRowSorter(inTable, 3, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
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
                                Location tempLocation = inApp.getDBI().find(new Location(listLocationCounts.get(finalT).getLocationName()));
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][1] = tempLocation.getName();
                                data[finalT][2] = listLocationCounts.get(finalT).getCount();
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                                Location tempLocation = listLocations.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation);
                                data[finalT][1] = tempLocation.getName();
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
                final List<Sighting> listSightings = inApp.getDBI().list(tempSighting, false);
                if (!listSightings.isEmpty()) {
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listSightings.size());
                    // Setup new table data
                    final Object[][] data = new Object[listSightings.size()][columnNames.length + 1];
                    for (int t = 0; t < listSightings.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Sighting tempSighting = listSightings.get(finalT);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempSighting);
                                data[finalT][1] = tempSighting.getLocationName();
                                data[finalT][2] = tempSighting.getDate();
                                data[finalT][3] = tempSighting.getSightingCounter();
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
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
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
    
    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, int inColumn3, int inColumn4, SortOrder inSortOrder1, SortOrder inSortOrder2, SortOrder inSortOrder3, SortOrder inSortOrder4) {
        List<SortKey> tempList = new ArrayList<SortKey>(1);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        tempList.add(new SortKey(inColumn3, inSortOrder3));
        tempList.add(new SortKey(inColumn4, inSortOrder4));
        inTable.getRowSorter().setSortKeys(tempList);
    }
    
    public static void setupPreviousRowSelection(JTable inTable, String[] inSelectedRowIDs, int inColWithID) {
        if (inSelectedRowIDs.length > 0) {
            int found = 0;
            for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                for (String selectedRowID : inSelectedRowIDs) {
                    if (inTable.getModel().getValueAt(inTable.convertRowIndexToModel(t), inColWithID).toString().equals(selectedRowID)) {
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

    private static void setupRenderersAndThumbnailRows(JTable inTable, boolean inShowDatesWithTime, boolean inHideThumbnails, int inIconOffset) {
        int primaryColumn;
        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables() || inHideThumbnails) {
            inTable.setRowHeight(25);
            primaryColumn = 0 + inIconOffset;
        }
        else {
            inTable.setRowHeight(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0 + inIconOffset).setMinWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0 + inIconOffset).setMaxWidth(WildLogThumbnailSizes.VERY_SMALL.getSize() + 4);
            inTable.getColumnModel().getColumn(0 + inIconOffset).setCellRenderer(new IconCellRenderer(WildLogThumbnailSizes.VERY_SMALL.getSize()));
            primaryColumn = 1 + inIconOffset;
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
    
    public static void setupColumnResizingListener(final JTable inTable, final int inCol) {
        inTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int colToUse = inCol;
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            colToUse = colToUse - 1;
                        }
                        if (inTable.getColumnModel().getColumnCount() > colToUse) {
                            int otherColumnsMaxSize = 0;
                            for (int t = 0; t < inTable.getColumnModel().getColumnCount(); t++) {
                                if (t != colToUse) {
                                    otherColumnsMaxSize = otherColumnsMaxSize + inTable.getColumnModel().getColumn(t).getMaxWidth();
                                }
                            }
                            if (inTable.getWidth() - otherColumnsMaxSize > inTable.getColumnModel().getColumn(colToUse).getPreferredWidth()) {
                                inTable.getColumnModel().getColumn(colToUse).setMaxWidth(inTable.getWidth() - otherColumnsMaxSize);
                            }
                            else {
                                inTable.getColumnModel().getColumn(colToUse).setMaxWidth(inTable.getColumnModel().getColumn(colToUse).getPreferredWidth() + inTable.getWidth()/4);
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
    
    public static <T extends DataObjectWithWildLogFile> void setupFilterTable(final WildLogApp inApp, final JTable inTable, final List<T> inList) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (inList != null && !inList.isEmpty()) {
                    // Setup column names
                    String[] columnNames;
                    if (inList.get(0) instanceof Element) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Creature Name",
                                                    "Creature Type"
                                                    };
                    }
                    else
                    if (inList.get(0) instanceof Location) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Place Name"
                                                    };
                    }
                    else
                    if (inList.get(0) instanceof Visit) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Period Name",
                                                    "Place Name",
                                                    "Start Date",
                                                    "Period Type"
                                                    };
                    }
                    else
                    if (inList.get(0) instanceof Sighting) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Creature Name",
                                                    "Place Name",
                                                    "Date",
                                                    "Period Name",
                                                    "Certainty",
                                                    "ID"
                                                    };
                    }
                    else {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Unknown Data"
                                                    };
                    }
                    Collection<Callable<Object>> listCallables = new ArrayList<>(inList.size());
                    Collections.sort(inList);
                    // Setup new table data
                    final Object[][] data = new Object[inList.size()][columnNames.length];
                    for (int t = 0; t < inList.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                DataObjectWithWildLogFile dataObject = inList.get(finalT);
                                data[finalT][0] = true;
                                data[finalT][1] = setupThumbnailIcon(inApp, dataObject);
                                data[finalT][2] = dataObject.getDisplayName();
                                if (dataObject instanceof Element) {
                                    data[finalT][3] = inApp.getDBI().find((Element)dataObject).getType();
                                }
                                else
                                if (dataObject instanceof Location) {
                                    // Nothing extra needs loading...
                                }
                                else
                                if (dataObject instanceof Visit) {
                                    Visit visit = inApp.getDBI().find((Visit)dataObject);
                                    data[finalT][3] = visit.getLocationName();
                                    data[finalT][4] = visit.getStartDate();
                                    data[finalT][5] = visit.getType();
                                }
                                else
                                if (dataObject instanceof Sighting) {
                                    Sighting sighting = inApp.getDBI().find((Sighting)dataObject);
                                    data[finalT][2] = sighting.getElementName();
                                    data[finalT][3] = sighting.getLocationName();
                                    data[finalT][4] = sighting.getDate();
                                    data[finalT][5] = sighting.getVisitName();
                                    data[finalT][6] = sighting.getCertainty();
                                    data[finalT][7] = sighting.getSightingCounter();
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
                    inTable.setDefaultRenderer(Boolean.class, new SelectedIndicatorCellRenderer());
                    inTable.getColumnModel().getColumn(0).setMinWidth(25);
                    inTable.getColumnModel().getColumn(0).setPreferredWidth(25);
                    inTable.getColumnModel().getColumn(0).setMaxWidth(25);
                    setupRenderersAndThumbnailRows(inTable, false, false, 1);
                    inTable.getColumnModel().getColumn(2).setMinWidth(135);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(155);
                    
                    if (inList.get(0) instanceof Element) {
                        inTable.getColumnModel().getColumn(3).setMinWidth(85);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(125);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(135);
                    }
                    else
                    if (inList.get(0) instanceof Location) {
                        // No exstra columns...
                    }
                    else
                    if (inList.get(0) instanceof Visit) {
                        inTable.getColumnModel().getColumn(3).setMinWidth(85);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(135);
                        inTable.getColumnModel().getColumn(3).setMaxWidth(155);
                        inTable.getColumnModel().getColumn(4).setMinWidth(75);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(4).setMaxWidth(105);
                        inTable.getColumnModel().getColumn(5).setMinWidth(75);
                        inTable.getColumnModel().getColumn(5).setPreferredWidth(85);
                        inTable.getColumnModel().getColumn(5).setMaxWidth(115);
                    }
                    else
                    if (inList.get(0) instanceof Sighting) {
                        inTable.getColumnModel().getColumn(3).setMinWidth(75);
                        inTable.getColumnModel().getColumn(3).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(4).setMinWidth(75);
                        inTable.getColumnModel().getColumn(4).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(5).setMinWidth(75);
                        inTable.getColumnModel().getColumn(5).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(6).setMinWidth(75);
                        inTable.getColumnModel().getColumn(6).setPreferredWidth(75);
                        inTable.getColumnModel().getColumn(7).setMinWidth(75);
                        inTable.getColumnModel().getColumn(7).setPreferredWidth(75);
                    }
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(1));
                    }
                    // Setup default sorting
                    setupRowSorter(inTable, 2);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Data"}, 0));
                }
            }
        });
     }
    
    public static void setupDistributionMapLayerTable(final WildLogApp inApp, final JTable inTable, Set<String> inSetAvailableLayers) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<Element> lstElementData = WildLogApp.getApplication().getDBI().list(new Element());
                Set<String> localSetOfLayers = new HashSet<>(inSetAvailableLayers); // Need to make a copy because removing from the orignal will effect the HashMap's keys
                if (lstElementData != null && !lstElementData.isEmpty() && localSetOfLayers != null && !localSetOfLayers.isEmpty()) {
                    // Setup column names
                    String[] columnNames = new String[] {
                                                    "Layer Name", 
                                                    "Scientific Name", 
                                                    "Creature Name",
                                                    "Type"
                                                    };
                    Collection<Callable<Object>> listCallables = new ArrayList<>(lstElementData.size());
                    // Setup new table data
                    final Object[][] data = new Object[lstElementData.size()][columnNames.length];
                    for (int t = 0; t < lstElementData.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Element dataObject = lstElementData.get(finalT);
                                data[finalT][0] = "";
                                for (String layer : localSetOfLayers) {
                                    if (layer.substring(0, layer.lastIndexOf('.')).equalsIgnoreCase(dataObject.getScientificName())) {
                                        data[finalT][0] = layer;
                                        localSetOfLayers.remove(layer);
                                        break;
                                    }
                                }
                                data[finalT][1] = dataObject.getScientificName();
                                data[finalT][2] = dataObject.getDisplayName();
                                data[finalT][3] = dataObject.getType();
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
                    // Add the layers that wasn't linked yet
                    for (String layer : localSetOfLayers) {
                        ((WildLogTableModel) inTable.getModel()).addRow(new Object[] {layer, "", ""});
                    }
                    // Set the cutom renderer
                    inTable.setDefaultRenderer(Object.class, new DistributionLayerCellRenderer(0, 0, 1, 2));
                    // Setup the column and row sizes etc.
                    inTable.setRowHeight(20);
                    inTable.getColumnModel().getColumn(0).setMinWidth(50);
                    inTable.getColumnModel().getColumn(0).setPreferredWidth(95);
                    inTable.getColumnModel().getColumn(1).setMinWidth(50);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(95);
                    inTable.getColumnModel().getColumn(2).setMinWidth(50);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(95);
                    inTable.getColumnModel().getColumn(3).setMinWidth(50);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(60);
                    inTable.getColumnModel().getColumn(3).setMaxWidth(70);
                    // Setup default sorting
                    setupRowSorter(inTable, 1);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Data"}, 0));
                }
            }
        });
     }
}
