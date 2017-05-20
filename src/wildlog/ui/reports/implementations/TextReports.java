package wildlog.ui.reports.implementations;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.utils.UtilsTime;


public class TextReports extends AbstractReport<Sighting> {
    private enum ChartType {DATA_SUMMARY, PERIOD_SUMMARY};
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private ChartType chartType = ChartType.PERIOD_SUMMARY;
    private Parent displayedReport;
    private boolean groupSimilarGPS = false;
    
    public TextReports(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Text Summaries", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Add the text report
        ToggleButton btnPaarlReport = new ToggleButton("Period Summary (Text)");
        btnPaarlReport.setToggleGroup(BUTTON_GROUP);
        btnPaarlReport.setCursor(Cursor.HAND);
        btnPaarlReport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PERIOD_SUMMARY;
                setupChartDescriptionLabel("<html>This is a summary of what was observed during a specific Period.</html>");
            }
        });
        lstCustomButtons.add(btnPaarlReport);
        ToggleButton btnTextReport = new ToggleButton("Data Summary (Text)");
        btnTextReport.setToggleGroup(BUTTON_GROUP);
        btnTextReport.setCursor(Cursor.HAND);
        btnTextReport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.DATA_SUMMARY;
                setupChartDescriptionLabel("<html>This is a collection text statistics.</html>");
            }
        });
        lstCustomButtons.add(btnTextReport);
        // Options
        lstCustomButtons.add(new Label("Chart Options:"));
        CheckBox chkGroupGPS = new CheckBox("Group Similar GPS Points");
        chkGroupGPS.setCursor(Cursor.HAND);
        chkGroupGPS.setSelected(false);
        chkGroupGPS.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                groupSimilarGPS = chkGroupGPS.isSelected();
            }
        });
        lstCustomButtons.add(chkGroupGPS);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedReport = null;
                if (chartType.equals(ChartType.PERIOD_SUMMARY)) {
                    setActiveSubCategoryTitle("Period Summary");
                    displayedReport = createPeriodReport(lstData);
                }
                else
                if (chartType.equals(ChartType.DATA_SUMMARY)) {
                    setActiveSubCategoryTitle("Data Summary");
                    displayedReport = createDataReport(lstData);
                }
                inScene.setRoot(displayedReport);
            }
        });
    }
    
    private Parent createPeriodReport(List<Sighting> inSightings) {
        // Load the data
        Map<String, VisitData> mapReportData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            VisitData data = mapReportData.get(sighting.getVisitName());
            if (data == null) {
                data = new VisitData();
                data.visitName = sighting.getVisitName();
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                if (visit != null) {
                    data.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                    data.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    data.gpsPoints = new HashSet<>();
                    data.totalSightings = 0;
                    data.mapElementsAndCount = new HashMap<>();
                }
                mapReportData.put(sighting.getVisitName(), data);
            }
            if (UtilsGPS.hasGPSData(sighting)) {
                Sighting tempSighting;
                if (groupSimilarGPS) {
                    tempSighting = sighting.cloneShallow();
                    tempSighting.setLatSeconds(0);
                    tempSighting.setLonSeconds(0);
                }
                else {
                    tempSighting = sighting;
                }
                data.gpsPoints.add(UtilsGPS.getLatitudeString(tempSighting) + System.lineSeparator() + UtilsGPS.getLongitudeString(tempSighting));
            }
            data.totalSightings = data.totalSightings + 1;
            data.mapElementsAndCount.put(sighting.getElementName(reportsBaseDialog.getOptionName()), 
                    data.mapElementsAndCount.getOrDefault(sighting.getElementName(reportsBaseDialog.getOptionName()), 0) + 1);
        }
        // Display the info
        List<VisitData> lstReportData = new ArrayList<>(mapReportData.values());
        Collections.sort(lstReportData);
        TableView<VisitData> table = new TableView<>();
        table.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsTextData.css");
        table.setEditable(false);
        table.setTableMenuButtonVisible(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<VisitData, String> colVisitName = new TableColumn<>("Period Name");
        colVisitName.setCellValueFactory(new PropertyValueFactory<>("visitName"));
        colVisitName.setStyle("-fx-alignment: center-left;");
        colVisitName.setMinWidth(135);
        colVisitName.setPrefWidth(180);
        colVisitName.setMaxWidth(325);
        table.getColumns().add(colVisitName);
        TableColumn<VisitData, LocalDate> colStartDate = new TableColumn<>("Start Date");
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStartDate.setStyle("-fx-alignment: center;");
        colStartDate.setCellFactory(new Callback<TableColumn<VisitData, LocalDate>, TableCell<VisitData, LocalDate>>() {
            public TableCell<VisitData, LocalDate> call(TableColumn<VisitData, LocalDate> inCol) {
                return new TextFieldTableCell<VisitData, LocalDate>(new StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate inDate) {
                        if (inDate != null) {
                            return UtilsTime.WL_DATE_FORMATTER.format(inDate);
                        }
                        return "";
                    }
                    @Override
                    public LocalDate fromString(String inString) {
                        return LocalDate.parse(inString, UtilsTime.WL_DATE_FORMATTER);
                    }
                });
            }
        });
        colStartDate.setMinWidth(75);
        colStartDate.setMaxWidth(85);
        table.getColumns().add(colStartDate);
        TableColumn<VisitData, LocalDate> colEndDate = new TableColumn<>("End Date");
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colEndDate.setStyle("-fx-alignment: center;");
        colEndDate.setCellFactory(new Callback<TableColumn<VisitData, LocalDate>, TableCell<VisitData, LocalDate>>() {
            public TableCell<VisitData, LocalDate> call(TableColumn<VisitData, LocalDate> inCol) {
                return new TextFieldTableCell<VisitData, LocalDate>(new StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate inDate) {
                        if (inDate != null) {
                            return UtilsTime.WL_DATE_FORMATTER.format(inDate);
                        }
                        return "";
                    }
                    @Override
                    public LocalDate fromString(String inString) {
                        return LocalDate.parse(inString, UtilsTime.WL_DATE_FORMATTER);
                    }
                });
            }
        });
        colEndDate.setMinWidth(75);
        colEndDate.setMaxWidth(85);
        table.getColumns().add(colEndDate);
        TableColumn<VisitData, Set<String>> colGPS = new TableColumn<>("GPS Positions");
        colGPS.setCellValueFactory(new PropertyValueFactory<>("gpsPoints"));
        colGPS.setStyle("-fx-alignment: center;");
        colGPS.setCellFactory(new Callback<TableColumn<VisitData, Set<String>>, TableCell<VisitData, Set<String>>>() {
            public TableCell<VisitData, Set<String>> call(TableColumn<VisitData, Set<String>> inCol) {
                return new TextFieldTableCell<VisitData, Set<String>>(new StringConverter<Set<String>>() {
                    @Override
                    public String toString(Set<String> inSet) {
                        StringBuilder builder = new StringBuilder(inSet.size() * 15);
                        List<String> lstValues = new ArrayList<>(inSet);
                        Collections.sort(lstValues);
                        int count = 0;
                        for (String value : lstValues) {
                            if (value != null && !value.isEmpty()) {
                                builder.append(value);
                                if (count < lstValues.size() - 1) {
                                    builder.append(System.lineSeparator());
                                }
                            }
                            count++;
                        }
                        return builder.toString();
                    }
                    @Override
                    public Set<String> fromString(String inString) {
                        return null;
                    }
                });
            }
        });
        colGPS.setMinWidth(100);
        colGPS.setMaxWidth(115);
        table.getColumns().add(colGPS);
        TableColumn<VisitData, Integer> colTotalSightings = new TableColumn<>("Observations");
        colTotalSightings.setCellValueFactory(new PropertyValueFactory<>("totalSightings"));
        colTotalSightings.setStyle("-fx-alignment: center;");
        colTotalSightings.setMinWidth(80);
        colTotalSightings.setPrefWidth(80);
        colTotalSightings.setMaxWidth(90);
        table.getColumns().add(colTotalSightings);
        TableColumn<VisitData, Map<String, Integer>> colTotalElements = new TableColumn<>("Creatures");
        colTotalElements.setCellValueFactory(new PropertyValueFactory<>("mapElementsAndCount"));
        colTotalElements.setStyle("-fx-alignment: center;");
        colTotalElements.setCellFactory(new Callback<TableColumn<VisitData, Map<String, Integer>>, TableCell<VisitData, Map<String, Integer>>>() {
            public TableCell<VisitData, Map<String, Integer>> call(TableColumn<VisitData, Map<String, Integer>> inCol) {
                return new TextFieldTableCell<VisitData, Map<String, Integer>>(new StringConverter<Map<String, Integer>>() {
                    @Override
                    public String toString(Map<String, Integer> inMap) {
                        return Integer.toString(inMap.size());
                    }
                    @Override
                    public Map<String, Integer> fromString(String inString) {
                        return null;
                    }
                });
            }
        });
        colTotalElements.setMinWidth(60);
        colTotalElements.setPrefWidth(65);
        colTotalElements.setMaxWidth(70);
        table.getColumns().add(colTotalElements);
        TableColumn<VisitData, Map<String, Integer>> colElements = new TableColumn<>("Details");
        colElements.setCellValueFactory(new PropertyValueFactory<>("mapElementsAndCount"));
        colElements.setStyle("-fx-alignment: center-left;");
        colElements.setCellFactory(new Callback<TableColumn<VisitData, Map<String, Integer>>, TableCell<VisitData, Map<String, Integer>>>() {
            public TableCell<VisitData, Map<String, Integer>> call(TableColumn<VisitData, Map<String, Integer>> inCol) {
                return new TextFieldTableCell<VisitData, Map<String, Integer>>(new StringConverter<Map<String, Integer>>() {
                    @Override
                    public String toString(Map<String, Integer> inMap) {
                        StringBuilder builder = new StringBuilder(inMap.size() * 15);
                        List<String> lstKeys = new ArrayList<>(inMap.keySet());
                        Collections.sort(lstKeys);
                        int count = 0;
                        for (String key : lstKeys) {
                            if (key != null && !key.isEmpty()) {
                                builder.append(key).append(" (").append(inMap.get(key)).append(")");
                                if (count < lstKeys.size() - 1) {
                                    builder.append(System.lineSeparator());
                                }
                            }
                            count++;
                        }
                        return builder.toString();
                    }
                    @Override
                    public Map<String, Integer> fromString(String inString) {
                        return null;
                    }
                });
            }
        });
        colElements.setMinWidth(100);
        colElements.setPrefWidth(150);
        table.getColumns().add(colElements);
        // Add items to table
        table.setItems(FXCollections.observableArrayList(lstReportData));
        // Setup the title and display the table
        Label title = new Label("Period Summary");
        title.setFont(Font.font(null, FontWeight.BOLD, 20));
        title.setPadding(new Insets(0, 0, 5, 0));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane borderPane = new BorderPane(table, title, null, null, null);
        borderPane.setStyle("-fx-background-color: white;");
        borderPane.setPadding(new Insets(5));
        return borderPane;
    }
    
    private Parent createDataReport(List<Sighting> inSightings) {
        // Setup the data to show
        List<DataPair> lstReportData = new ArrayList<>(30);
        // Setup generic date
        Set<String> setElementNames = new HashSet<>();
        Set<String> setLocationNames = new HashSet<>();
        Set<String> setVisitNames = new HashSet<>();
        for (Sighting sighting : inSightings) {
            setElementNames.add(sighting.getElementName(reportsBaseDialog.getOptionName()));
            setLocationNames.add(sighting.getLocationName());
            setVisitNames.add(sighting.getVisitName());
        }
        // Dates
        if (!inSightings.isEmpty()) {
            Collections.sort(inSightings);
            lstReportData.add(new DataPair("First Observation: ", UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(inSightings.get(0).getDate()))));
            lstReportData.add(new DataPair("Last Observation: ", UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(inSightings.get(inSightings.size() - 1).getDate()))));
            lstReportData.add(new DataPair("First Place: ", inSightings.get(0).getLocationName()));
            lstReportData.add(new DataPair("Last Place: ", inSightings.get(inSightings.size() - 1).getLocationName()));
            lstReportData.add(new DataPair("First Creature: ", inSightings.get(0).getElementName(reportsBaseDialog.getOptionName())));
            lstReportData.add(new DataPair("Last Creature: ", inSightings.get(inSightings.size() - 1).getElementName(reportsBaseDialog.getOptionName())));
        }
        // Number of Sightings
        lstReportData.add(new DataPair("Total Observations: ", Integer.toString(inSightings.size())));
        // Max/Min Sightings of Element per Location
        Map<String, ReportDataWrapper> mapMinMax = new HashMap<>(setElementNames.size() * setLocationNames.size());
        for (Sighting sighting : inSightings) {
            ReportDataWrapper countWrapper = mapMinMax.get(sighting.getElementName(reportsBaseDialog.getOptionName()));
            if (countWrapper == null) {
                countWrapper = new ReportDataWrapper(sighting.getElementName(reportsBaseDialog.getOptionName()), null, 0);
                mapMinMax.put(sighting.getElementName(reportsBaseDialog.getOptionName()), countWrapper);
            }
            countWrapper.count++;
        }
        int min = mapMinMax.size();
        int ave = 0;
        int max = 0;
        for (ReportDataWrapper dataWrapper : mapMinMax.values()) {
            if (dataWrapper.count < min) {
                min = dataWrapper.count;
            }
            if (dataWrapper.count > max) {
                max = dataWrapper.count;
            }
        }
        lstReportData.add(new DataPair("Least Observations per Creature: ", Integer.toString(min)));
        lstReportData.add(new DataPair("Most Observations per Creature: ", Integer.toString(max)));
        // Number of Elements, Locations, Visits
        lstReportData.add(new DataPair("Number of Creatures: ", Integer.toString(setElementNames.size())));
        lstReportData.add(new DataPair("Number of Places: ", Integer.toString(setLocationNames.size())));
        lstReportData.add(new DataPair("Number of Periods: ", Integer.toString(setVisitNames.size())));
        // Elements per Location
        Map<String, Set<String>> mapCounts = new HashMap<>(setLocationNames.size());
        for (Sighting sighting : inSightings) {
            Set<String> countSet = mapCounts.get(sighting.getLocationName());
            if (countSet == null) {
                countSet = new HashSet<>(setElementNames.size());
                mapCounts.put(sighting.getLocationName(), countSet);
            }
            countSet.add(sighting.getElementName(reportsBaseDialog.getOptionName()));
        }
        min = inSightings.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        lstReportData.add(new DataPair("Least Creatures per Place: ", Integer.toString(min)));
        lstReportData.add(new DataPair("Average Creatures per Place: ", decimalFormat.format((double)ave / (double)setLocationNames.size())));
        lstReportData.add(new DataPair("Most Creatures per Place: ", Integer.toString(max)));
        // Elements per Visit
        mapCounts = new HashMap<>(setVisitNames.size());
        for (Sighting sighting : inSightings) {
            Set<String> countSet = mapCounts.get(sighting.getVisitName());
            if (countSet == null) {
                countSet = new HashSet<>(setElementNames.size());
                mapCounts.put(sighting.getVisitName(), countSet);
            }
            countSet.add(sighting.getElementName(reportsBaseDialog.getOptionName()));
        }
        min = inSightings.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        lstReportData.add(new DataPair("Least Creatures per Period: ", Integer.toString(min)));
        lstReportData.add(new DataPair("Average Creatures per Period: ", decimalFormat.format((double)ave / (double)setVisitNames.size())));
        lstReportData.add(new DataPair("Most Creatures per Period: ", Integer.toString(max)));
        // Visits per Location
        mapCounts = new HashMap<>(setLocationNames.size());
        for (Sighting sighting : inSightings) {
            Set<String> countSet = mapCounts.get(sighting.getLocationName());
            if (countSet == null) {
                countSet = new HashSet<>(setVisitNames.size());
                mapCounts.put(sighting.getLocationName(), countSet);
            }
            countSet.add(sighting.getVisitName());
        }
        min = inSightings.size();
        ave = 0;
        max = 0;
        for (Set<String> countSet : mapCounts.values()) {
            if (countSet.size() < min) {
                min = countSet.size();
            }
            if (countSet.size() > max) {
                max = countSet.size();
            }
            ave = ave + countSet.size();
        }
        lstReportData.add(new DataPair("Least Periods per Place: ", Integer.toString(min)));
        lstReportData.add(new DataPair("Average Periods per Place: ", decimalFormat.format((double)ave / (double)setLocationNames.size())));
        lstReportData.add(new DataPair("Most Periods per Place: ", Integer.toString(max)));
        // Display the info
        TableView<DataPair> table = new TableView<>();
        table.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsTextData.css");
        table.setEditable(false);
        table.setTableMenuButtonVisible(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<DataPair, String> colKey = new TableColumn<>("Category");
        colKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        colKey.setMinWidth(135);
        colKey.setPrefWidth(180);
        table.getColumns().add(colKey);
        TableColumn<DataPair, String> colValue = new TableColumn<>("Value");
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colValue.setMinWidth(135);
        colValue.setPrefWidth(180);
        table.getColumns().add(colValue);
        // Add items to table
        table.setItems(FXCollections.observableArrayList(lstReportData));
        // Setup the title and display the table
        Label title = new Label("Data Summary");
        title.setFont(Font.font(null, FontWeight.BOLD, 20));
        title.setPadding(new Insets(0, 0, 5, 0));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane borderPane = new BorderPane(table, title, null, null, null);
        borderPane.setStyle("-fx-background-color: white;");
        borderPane.setPadding(new Insets(5));
        return borderPane;
    }
    
    public class VisitData implements Comparable<VisitData> {
        private String visitName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Set<String> gpsPoints;
        private int totalSightings;
        private Map<String, Integer> mapElementsAndCount;

        public VisitData() {
        }

        @Override
        public int compareTo(VisitData o) {
            return visitName.compareTo(o.visitName);
        }
        
        public String getVisitName() {
            return visitName;
        }

        public void setVisitName(String inVisitName) {
            visitName = inVisitName;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate inStartDate) {
            startDate = inStartDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate inEndDate) {
            endDate = inEndDate;
        }

        public Set<String> getGpsPoints() {
            return gpsPoints;
        }

        public void setGpsPoints(Set<String> inGpsPoints) {
            gpsPoints = inGpsPoints;
        }

        public int getTotalSightings() {
            return totalSightings;
        }

        public void setTotalSightings(int inTotalSightings) {
            totalSightings = inTotalSightings;
        }

        public Map<String, Integer> getMapElementsAndCount() {
            return mapElementsAndCount;
        }

        public void setMapElementsAndCount(Map<String, Integer> inMapElementsAndCount) {
            mapElementsAndCount = inMapElementsAndCount;
        }
    }
    
    public class DataPair {
        private String key;
        private String value;

        public DataPair() {
        }

        public DataPair(String inKey, String inValue) {
            key = inKey;
            value = inValue;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String inKey) {
            key = inKey;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String inValue) {
            value = inValue;
        }
    }
    
}
