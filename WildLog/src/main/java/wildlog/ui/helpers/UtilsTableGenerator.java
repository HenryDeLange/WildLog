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
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.dataobjects.adhoc.FilterProperties;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dbi.queryobjects.LocationCount;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
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
import wildlog.utils.UtilsTime;
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

    public static void setupElementTableLarge(final WildLogApp inApp, final JTable inTable, final String inElementPrimaryName, final ElementType inElementType, final String inFilterText) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 6);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Element> listElements = inApp.getDBI().listElements(inElementPrimaryName, null, inElementType, Element.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempElement.getWildLogFileID());
                                data[finalT][1] = tempElement.getPrimaryName();
                                if (WildLogApp.getApplication().getWildLogOptions().isUseScientificNames()) {
                                    data[finalT][2] = tempElement.getScientificName();
                                }
                                else {
                                    data[finalT][2] = tempElement.getOtherName();
                                }
                                data[finalT][3] = tempElement.getType();
                                data[finalT][4] = tempElement.getFeedingClass();
                                data[finalT][5] = inApp.getDBI().countSightings(0, tempElement.getID(), 0, 0);
                                data[finalT][6] = tempElement.getID();
                                return null;
                            }
                        });
                    }
                    // Call the actions and wait for all to finish
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    inTable.getColumnModel().getColumn(5).setMinWidth(80);
                    inTable.getColumnModel().getColumn(5).setPreferredWidth(90);
                    inTable.getColumnModel().getColumn(5).setMaxWidth(95);
                    inTable.removeColumn(inTable.getColumnModel().getColumn(6));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 1);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Since the table model has changed, the row sorter needs to be setup again
// FIXME: The code below is copied from UtilsUI.attachKeyListernerToFilterTableRows(). This is not ideal, but OK for now...
                    TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)inTable.getRowSorter();
                    if (sorter == null) {
                        sorter = new TableRowSorter<>(inTable.getModel());
                    }
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(inFilterText), 1));
                    inTable.setRowSorter(sorter);
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 6);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupElementTableSmall(final WildLogApp inApp, final JTable inTable, final String inElementPrimaryName, final ElementType inElementType) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 3);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Element> listElements = inApp.getDBI().listElements(inElementPrimaryName, null, inElementType, Element.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempElement.getWildLogFileID());
                                data[finalT][1] = tempElement.getPrimaryName();
                                data[finalT][2] = tempElement.getType();
                                data[finalT][3] = tempElement.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    inTable.removeColumn(inTable.getColumnModel().getColumn(3));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 1);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 3);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableLarge(final WildLogApp inApp, final JTable inTable, final String inLocationName) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 7);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Location> listLocations = inApp.getDBI().listLocations(inLocationName, Location.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation.getWildLogFileID());
                                data[finalT][1] = tempLocation.getName();
                                data[finalT][2] = tempLocation.getRating();
                                data[finalT][3] = tempLocation.getGameViewingRating();
                                data[finalT][4] = new WildLogTableModelDataWrapper(
                                        UtilsGPS.getLatitudeString(tempLocation), 
                                        UtilsGPS.getLatDecimalDegree(tempLocation));
                                data[finalT][5] = new WildLogTableModelDataWrapper(
                                        UtilsGPS.getLongitudeString(tempLocation), 
                                        UtilsGPS.getLonDecimalDegree(tempLocation));
                                data[finalT][6] = inApp.getDBI().countSightings(0, 0, tempLocation.getID(), 0);
                                data[finalT][7] = tempLocation.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    inTable.removeColumn(inTable.getColumnModel().getColumn(7));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 1);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 7);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableLarge(final WildLogApp inApp, final JTable inTable, final long inLocationID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 7);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Creatures",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Visit> listVisits = inApp.getDBI().listVisits(null, inLocationID, null, false, Visit.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempVisit.getWildLogFileID());
                                data[finalT][1] = tempVisit.getName();
                                data[finalT][2] = tempVisit.getStartDate();
                                data[finalT][3] = tempVisit.getEndDate();
                                data[finalT][4] = tempVisit.getType();
                                List<Sighting> listSightings = inApp.getDBI().listSightings(0, 0, tempVisit.getID(), false, Sighting.class);
                                data[finalT][5] = listSightings.size();
                                Set<Long> countElements = new HashSet<>(listSightings.size()/3);
                                for (Sighting sighting : listSightings) {
                                    if (!countElements.contains(sighting.getElementID())) {
                                        countElements.add(sighting.getElementID());
                                    }
                                }
                                data[finalT][6] = countElements.size();
                                data[finalT][7] = tempVisit.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    inTable.removeColumn(inTable.getColumnModel().getColumn(7));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 2, 3, 1, SortOrder.DESCENDING, SortOrder.DESCENDING, SortOrder.DESCENDING);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 7);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Periods"}, 0));
                }
            }
        });
    }

    public static void setupVisitTableSmallWithSightings(final WildLogApp inApp, final JTable inTable, final long inLocationID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 4);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                if (inLocationID > 0) {
                    final List<Visit> listVisits = inApp.getDBI().listVisits(null, inLocationID, null, false, Visit.class);
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
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempVisit.getWildLogFileID());
                                    data[finalT][1] = tempVisit.getName();
                                    data[finalT][2] = tempVisit.getStartDate();
                                    data[finalT][3] = inApp.getDBI().countSightings(0, 0, 0, tempVisit.getID());
                                    data[finalT][4] = tempVisit.getID();
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(4));
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                            setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        }
                        else {
                            inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                        }
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 4);
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

    public static void setupVisitTableSmallWithType(final WildLogApp inApp, final JTable inTable, final long inLocationID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 4);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Type",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                if (inLocationID > 0) {
                    final List<Visit> listVisits = inApp.getDBI().listVisits(null, inLocationID, null, false, Visit.class);
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
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempVisit.getWildLogFileID());
                                    data[finalT][1] = tempVisit.getName();
                                    data[finalT][2] = tempVisit.getStartDate();
                                    data[finalT][3] = tempVisit.getType();
                                    data[finalT][4] = tempVisit.getID();
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(4));
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                            setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        }
                        else {
                            inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                        }
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 4);
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

    public static void setupSightingTableLarge(final WildLogApp inApp, final JTable inTable, final long inVisitID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 6);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Info"
                                        };
                // Load data from DB
                if (inVisitID > 0) {
                    final List<Sighting> listSightings = inApp.getDBI().listSightings(0, 0, inVisitID, true, Sighting.class);
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
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempSighting.getWildLogFileID());
                                    data[finalT][1] = tempSighting.getCachedElementName();
                                    data[finalT][2] = tempSighting.getDate();
                                    data[finalT][3] = tempSighting.getSightingEvidence();
                                    data[finalT][4] = tempSighting.getCertainty();
                                    data[finalT][5] = inApp.getDBI().findElement(tempSighting.getElementID(), null, Element.class).getType();
                                    data[finalT][6] = tempSighting.getID();
                                    if (tempSighting.isCachedLinkedToINaturalist()) {
                                        data[finalT][7] = new ImageIcon(WildLogApp.class.getResource("resources/icons/iNaturalist.png"));
                                    }
                                    else {
                                        if (UtilsGPS.hasGPSData(tempSighting)) {
                                            data[finalT][7] = new ImageIcon(WildLogApp.class.getResource("resources/icons/GPS_word.png"));
                                        }
                                        else {
                                            data[finalT][7] = new ImageIcon();
                                        }
                                    }
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                        // Create the new model
                        setupTableModel(inTable, data, columnNames);
                        // Setup the default renderers, column and row sizes etc.
                        setupRenderersAndThumbnailRows(inTable, true, false, 0);
                        inTable.getColumnModel().getColumn(7).setCellRenderer(
                                new IconCellRenderer(WildLogThumbnailSizes.VERY_TINY.getSize(), true));
                        // Setup the column sizes
                        inTable.getColumnModel().getColumn(1).setMinWidth(100);
                        inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                        inTable.getColumnModel().getColumn(2).setMinWidth(100);
                        inTable.getColumnModel().getColumn(2).setPreferredWidth(125);
                        inTable.getColumnModel().getColumn(2).setMaxWidth(135);
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
                        inTable.getColumnModel().getColumn(6).setMaxWidth(145);
                        inTable.getColumnModel().getColumn(7).setMinWidth(20);
                        inTable.getColumnModel().getColumn(7).setPreferredWidth(30);
                        inTable.getColumnModel().getColumn(7).setMaxWidth(35);
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                            setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        }
                        else {
                            inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                        }
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
            final FilterProperties inFilterProperties, 
            final List<Long> inActiveLocations, final List<Long> inActiveVisits, final List<Long> inActiveElements, 
            final double inNorthEast_Lat, final double inNorthEast_Lon, final double inSouthWest_Lat, final double inSouthWest_Lon) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 8);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "ID", // Sighting
                                        "Info",
                                        "ElementID", // Hidden
                                        "LocationID", // Hidden
                                        "VisitID" // Hidden
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
                final List<Sighting> listSightings = inApp.getDBI().searchSightings(inFilterProperties.getSightingIDs(), 
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
                                    rowData[1] = tempSighting.getCachedElementName();
                                    rowData[2] = tempSighting.getCachedLocationName();
                                    rowData[3] = tempSighting.getCachedVisitName();
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
                                    rowData[8] = tempSighting.getID();
                                    if (tempSighting.isCachedLinkedToINaturalist()) {
                                        rowData[9] = new ImageIcon(WildLogApp.class.getResource("resources/icons/iNaturalist.png"));
                                    }
                                    else {
                                        if (UtilsGPS.hasGPSData(tempSighting)) {
                                            rowData[9] = new ImageIcon(WildLogApp.class.getResource("resources/icons/GPS_word.png"));
                                        }
                                        else {
                                            rowData[9] = new ImageIcon();
                                        }
                                    }
                                    rowData[10] = tempSighting.getElementID();
                                    rowData[11] = tempSighting.getLocationID();
                                    rowData[12] = tempSighting.getVisitID();
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
                    inTable.setDefaultRenderer(String.class, new TextCellRenderer(0, 1, 2, 3));
                    inTable.getColumnModel().getColumn(9).setCellRenderer(
                            new IconCellRenderer(WildLogThumbnailSizes.VERY_TINY.getSize(), true));
                    // Continue to setup the column sizes
                    inTable.getColumnModel().getColumn(0).setMinWidth(110);
                    inTable.getColumnModel().getColumn(0).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(0).setMaxWidth(120);
                    inTable.getColumnModel().getColumn(1).setMinWidth(100);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(110);
                    inTable.getColumnModel().getColumn(1).setMaxWidth(250);
                    inTable.getColumnModel().getColumn(2).setMinWidth(90);
                    inTable.getColumnModel().getColumn(2).setPreferredWidth(125);
                    inTable.getColumnModel().getColumn(2).setMaxWidth(280);
                    inTable.getColumnModel().getColumn(3).setMinWidth(110);
                    inTable.getColumnModel().getColumn(3).setPreferredWidth(115);
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
                    // Remove in reverse order (andersins is 12 in posisie 10 teen die einde nie)
                    inTable.removeColumn(inTable.getColumnModel().getColumn(12));
                    inTable.removeColumn(inTable.getColumnModel().getColumn(11));
                    inTable.removeColumn(inTable.getColumnModel().getColumn(10));
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 0, 1, 2, 3, SortOrder.DESCENDING, SortOrder.ASCENDING, SortOrder.ASCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 8);
                }
                if (inTable.getModel().getRowCount() == 0) {
                    inTable.setRowHeight(50);
                    inTable.setModel(new DefaultTableModel(new String[]{"No Observations were found that match the currently active filters."}, 1));
                    inTable.getModel().setValueAt("Filter on Properties. (Click here to open the Filter Properties dialog "
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
                            String text = "<html>Showing " + rowCount + " (of " + inApp.getDBI().countSightings(0, 0, 0, 0) + ") Observations.";
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
                                    + " " + inActiveLocations.size() + " (of " + inApp.getDBI().countLocations(null) + ") Places"
                                    + ", " + inActiveVisits.size() + " (of " + inApp.getDBI().countVisits(null, 0) + ") Periods "
                                    + " and " + inActiveElements.size() + " (of " + inApp.getDBI().countElements(null, null) + ") Creatures.";
                            // Other
                            text = text + "<br/>Additional Observation properties may also be active.</html>";
                            inLblFilterDetails.setText(text);
                        }
                    });
            }
        });
    }

    public static void setupElementsTableMediumForVisit(final WildLogApp inApp, final JTable inTable, final long inVisitID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 4);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                if (inVisitID > 0) {
                    List<Sighting> allSightings = inApp.getDBI().listSightings(0, 0, inVisitID, false, Sighting.class);
                    final List<Long> listElements = new ArrayList<>();
                    for (Sighting tempSighting : allSightings) {
                        if (!listElements.contains(tempSighting.getElementID())) {
                            listElements.add(tempSighting.getElementID());
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
                                    Element tempElement = inApp.getDBI().findElement(listElements.get(finalT), null, Element.class);
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempElement.getWildLogFileID());
                                    data[finalT][1] = tempElement.getPrimaryName();
                                    data[finalT][2] = tempElement.getType();
                                    data[finalT][3] = inApp.getDBI().countSightings(0, tempElement.getID(), 0, inVisitID);
                                    data[finalT][4] = tempElement.getID();
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(4));
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                            setupRowSorter(inTable, 3, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        }
                        else {
                            inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                        }
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 4);
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

    public static void setupElementsTableMediumForLocation(final WildLogApp inApp, final JTable inTable, final long inLocationID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 4);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                if (inLocationID > 0) {
                    List<Sighting> allSightings = inApp.getDBI().listSightings(0, inLocationID, 0, false, Sighting.class);
                    final List<Long> listElements = new ArrayList<>();
                    for (Sighting tempSighting : allSightings) {
                        if (!listElements.contains(tempSighting.getElementID())) {
                            listElements.add(tempSighting.getElementID());
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
                                    Element tempElement = inApp.getDBI().findElement(listElements.get(finalT), null, Element.class);
                                    data[finalT][0] = setupThumbnailIcon(inApp, tempElement.getWildLogFileID());
                                    data[finalT][1] = tempElement.getPrimaryName();
                                    data[finalT][2] = tempElement.getType();
                                    data[finalT][3] = inApp.getDBI().countSightings(0, tempElement.getID(), inLocationID, 0);
                                    data[finalT][4] = tempElement.getID();
                                    return null;
                                }
                            });
                        }
                        try {
                            executorService.invokeAll(listCallables);
                        }
                        catch (InterruptedException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(4));
                        if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                            inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                        }
                        // Setup default sorting
                        if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                            setupRowSorter(inTable, 3, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                        }
                        else {
                            inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                        }
                        // Setup row selection
                        setupPreviousRowSelection(inTable, selectedRowIDs, 4);
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

    public static void setupLocationsTableMedium(final WildLogApp inApp, final JTable inTable, final long inElementID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 3);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "Observations",
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<LocationCount> listLocationCounts = inApp.getDBI().queryLocationCountForElement(inElementID, LocationCount.class);
                if (!listLocationCounts.isEmpty()) {
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listLocationCounts.size());
                    // Setup new table data
                    final Object[][] data = new Object[listLocationCounts.size()][columnNames.length];
                    for (int t = 0; t < listLocationCounts.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Location tempLocation = inApp.getDBI().findLocation(listLocationCounts.get(finalT).getLocationID(), null, Location.class);
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation.getWildLogFileID());
                                data[finalT][1] = tempLocation.getName();
                                data[finalT][2] = listLocationCounts.get(finalT).getCount();
                                data[finalT][3] = tempLocation.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    inTable.removeColumn(inTable.getColumnModel().getColumn(3));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 3);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
    }

    public static void setupLocationTableSmall(final WildLogApp inApp, final JTable inTable, final String inLocationName) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs = getSelectedRowIDs(inTable, 2);
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Location> listLocations = inApp.getDBI().listLocations(inLocationName, Location.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempLocation.getWildLogFileID());
                                data[finalT][1] = tempLocation.getName();
                                data[finalT][2] = tempLocation.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    setupRenderersAndThumbnailRows(inTable, false, false, 0);
                    inTable.getColumnModel().getColumn(1).setMinWidth(105);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(125);
                    inTable.removeColumn(inTable.getColumnModel().getColumn(2));
                    if (!WildLogApp.getApplication().getWildLogOptions().isUseThumbnailTables()) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(0));
                    }
                    // Setup default sorting
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 1);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
                    // Setup row selection
                    setupPreviousRowSelection(inTable, selectedRowIDs, 2);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Places"}, 0));
                }
            }
        });
     }

    public static void setupSightingsTableSmall(final WildLogApp inApp, final JTable inTable, final long inElementID) {
        // Deterimine the row IDs of the previously selected rows.
        final long[] selectedRowIDs;
        if (inTable.getModel().getColumnCount() < 4) {
            // Note: If the location radio button was previously selected the passed in table will not have 4 columns.
            selectedRowIDs = new long[0];
        }
        else {
            selectedRowIDs = getSelectedRowIDs(inTable, 3);
        }
        final List<? extends SortKey> lstPreviousSortKeys = inTable.getRowSorter().getSortKeys();
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
                                        "ID" // Hidden
                                        };
                // Load data from DB
                final List<Sighting> listSightings = inApp.getDBI().listSightings(inElementID, 0, 0, true, Sighting.class);
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
                                data[finalT][0] = setupThumbnailIcon(inApp, tempSighting.getWildLogFileID());
                                data[finalT][1] = tempSighting.getCachedLocationName();
                                data[finalT][2] = tempSighting.getDate();
                                data[finalT][3] = tempSighting.getID();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    if (lstPreviousSortKeys == null || lstPreviousSortKeys.isEmpty()) {
                        setupRowSorter(inTable, 2, 1, SortOrder.DESCENDING, SortOrder.ASCENDING);
                    }
                    else {
                        inTable.getRowSorter().setSortKeys(lstPreviousSortKeys);
                    }
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
        List<SortKey> tempList = new ArrayList<>(1);
        tempList.add(new SortKey(inColumn, SortOrder.ASCENDING));
        inTable.getRowSorter().setSortKeys(tempList);
    }

    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, SortOrder inSortOrder1, SortOrder inSortOrder2) {
        List<SortKey> tempList = new ArrayList<>(2);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        inTable.getRowSorter().setSortKeys(tempList);
    }

    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, int inColumn3, SortOrder inSortOrder1, SortOrder inSortOrder2, SortOrder inSortOrder3) {
        List<SortKey> tempList = new ArrayList<>(3);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        tempList.add(new SortKey(inColumn3, inSortOrder3));
        inTable.getRowSorter().setSortKeys(tempList);
    }
    
    private static void setupRowSorter(JTable inTable, int inColumn1, int inColumn2, int inColumn3, int inColumn4, SortOrder inSortOrder1, SortOrder inSortOrder2, SortOrder inSortOrder3, SortOrder inSortOrder4) {
        List<SortKey> tempList = new ArrayList<>(4);
        tempList.add(new SortKey(inColumn1, inSortOrder1));
        tempList.add(new SortKey(inColumn2, inSortOrder2));
        tempList.add(new SortKey(inColumn3, inSortOrder3));
        tempList.add(new SortKey(inColumn4, inSortOrder4));
        inTable.getRowSorter().setSortKeys(tempList);
    }
    
    public static void setupPreviousRowSelection(JTable inTable, long[] inSelectedRowIDs, int inColWithID) {
        if (inSelectedRowIDs.length > 0) {
            int found = 0;
            for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                for (long selectedRowID : inSelectedRowIDs) {
                    if ((long) inTable.getModel().getValueAt(inTable.convertRowIndexToModel(t), inColWithID) == selectedRowID) {
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

    private static ImageIcon setupThumbnailIcon(WildLogApp inApp, String inWildLogFileID) {
        if (inApp.getWildLogOptions().isUseThumbnailTables()) {
            WildLogFile wildLogFile = inApp.getDBI().findWildLogFile(0, inWildLogFileID, null, null, WildLogFile.class);
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
    
    public static long[] getSelectedRowIDs(JTable inTable, int inCol) {
        long[] selectedRowIDs = new long[inTable.getSelectedRowCount()];
        if (inTable.getModel().getColumnCount() > inCol) {
            for (int t = 0; t < inTable.getSelectedRowCount(); t++) {
                selectedRowIDs[t] = (long) inTable.getModel().getValueAt(inTable.convertRowIndexToModel(inTable.getSelectedRows()[t]), inCol);
            }
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
                                                    "Creature Type",
                                                    "ID" // Hidden
                                                    };
                    }
                    else
                    if (inList.get(0) instanceof Location) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Place Name",
                                                    "ID" // Hidden
                                                    };
                    }
                    else
                    if (inList.get(0) instanceof Visit) {
                        columnNames = new String[] {
                                                    "", "",
                                                    "Period Name",
                                                    "Place Name",
                                                    "Start Date",
                                                    "Period Type",
                                                    "ID" // Hidden
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
                                data[finalT][1] = setupThumbnailIcon(inApp, dataObject.getWildLogFileID());
                                data[finalT][2] = dataObject.getDisplayName();
                                if (dataObject instanceof Element) {
                                    Element element = (Element) dataObject;
                                    data[finalT][3] = element.getType();
                                    data[finalT][4] = element.getID();
                                }
                                else
                                if (dataObject instanceof Location) {
                                    Location location = (Location) dataObject;
                                    data[finalT][3] = location.getID();
                                }
                                else
                                if (dataObject instanceof Visit) {
                                    Visit visit = (Visit) dataObject;
                                    data[finalT][3] = visit.getCachedLocationName();
                                    data[finalT][4] = visit.getStartDate();
                                    data[finalT][5] = visit.getType();
                                    data[finalT][6] = visit.getID();
                                }
                                else
                                if (dataObject instanceof Sighting) {
                                    Sighting sighting = inApp.getDBI().findSighting(((Sighting) dataObject).getID(), true, Sighting.class);
                                    data[finalT][2] = sighting.getCachedElementName();
                                    data[finalT][3] = sighting.getCachedLocationName();
                                    data[finalT][4] = sighting.getDate();
                                    data[finalT][5] = sighting.getCachedVisitName();
                                    data[finalT][6] = sighting.getCertainty();
                                    data[finalT][7] = sighting.getID();
                                }
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(4));
                    }
                    else
                    if (inList.get(0) instanceof Location) {
                        inTable.removeColumn(inTable.getColumnModel().getColumn(3));
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
                        inTable.removeColumn(inTable.getColumnModel().getColumn(6));
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
                final Object removeLock = new Object();
                List<Element> lstElementData = WildLogApp.getApplication().getDBI().listElements(null, null, null, Element.class);
                Set<String> localSetOfLayers = new HashSet<>(inSetAvailableLayers); // Need to make a copy because removing from the orignal will effect the HashMap's keys
                if (lstElementData != null && !lstElementData.isEmpty() && !localSetOfLayers.isEmpty()) {
                    // Setup column names
                    String[] columnNames = new String[] {
                                                    "Creature Name",
                                                    "Scientific Name", 
                                                    "Layer Name", 
                                                    "Type"
                                                    };
                    Collection<Callable<Object>> listCallables = new ArrayList<>(lstElementData.size());
                    // Setup new table data
                    final Object[][] data = new Object[lstElementData.size()][columnNames.length];
                    Collections.sort(lstElementData);
                    for (int t = 0; t < lstElementData.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                Element dataObject = lstElementData.get(finalT);
                                data[finalT][0] = dataObject.getDisplayName();
                                data[finalT][1] = dataObject.getScientificName();
                                data[finalT][2] = "";
                                synchronized(removeLock) { // Needs to be synchronized, otherwise the HashSet gets confused
                                    for (String layer : localSetOfLayers) {
                                        if (layer.substring(0, layer.lastIndexOf('.')).equalsIgnoreCase(dataObject.getScientificName())) {
                                            data[finalT][2] = layer;
                                            // Remove the layer from the set, because the layers that remain is later added as unlinked
                                            localSetOfLayers.remove(layer);
                                            break;
                                        }
                                    }
                                }
                                data[finalT][3] = dataObject.getType();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Add the layers that wasn't linked yet
                    for (String layer : localSetOfLayers) {
                        ((WildLogTableModel) inTable.getModel()).addRow(new Object[] {"", "", layer, ""});
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
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Data"}, 0));
                }
            }
        });
     }
    
    public static void setupUsersTable(final WildLogApp inApp, final JTable inTable) {
        // Setup header
        setupLoadingHeader(inTable);
        // Load the table content
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Setup column names
                String[] columnNames = {
                                        "Username",
                                        "Type"
                                        };
                // Load data from DB
                final List<WildLogUser> listUsers = inApp.getDBI().listUsers(null, WildLogUser.class);
                if (!listUsers.isEmpty()) {
                    Collection<Callable<Object>> listCallables = new ArrayList<>(listUsers.size());
                    // Setup new table data
                    final Object[][] data = new Object[listUsers.size()][columnNames.length + 1];
                    for (int t = 0; t < listUsers.size(); t++) {
                        final int finalT = t;
                        listCallables.add(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                WildLogUser tempUser = listUsers.get(finalT);
                                data[finalT][0] = tempUser.getUsername();
                                data[finalT][1] = tempUser.getType();
                                return null;
                            }
                        });
                    }
                    try {
                        executorService.invokeAll(listCallables);
                    }
                    catch (InterruptedException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    // Create the new model
                    setupTableModel(inTable, data, columnNames);
                    // Setup the column and row sizes etc.
                    inTable.getColumnModel().getColumn(0).setMinWidth(100);
                    inTable.getColumnModel().getColumn(0).setPreferredWidth(150);
                    inTable.getColumnModel().getColumn(1).setMinWidth(100);
                    inTable.getColumnModel().getColumn(1).setPreferredWidth(150);
                    // Setup default sorting
                    setupRowSorter(inTable, 1, 0, SortOrder.ASCENDING, SortOrder.ASCENDING);
                }
                else {
                    inTable.setModel(new DefaultTableModel(new String[]{"No Users"}, 0));
                }
            }
        });
    }
    
}
