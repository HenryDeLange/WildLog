package wildlog.ui.reports;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.adhoc.FilterProperties;
import wildlog.ui.dialogs.ExportDialogForReportsAndMaps;
import wildlog.ui.dialogs.FilterDataListDialog;
import wildlog.ui.dialogs.FilterPropertiesDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.reports.implementations.DayAndNightChart;
import wildlog.ui.reports.implementations.DurationChart;
import wildlog.ui.reports.implementations.ElementsChart;
import wildlog.ui.reports.implementations.EventTimelineChart;
import wildlog.ui.reports.implementations.LocationChart;
import wildlog.ui.reports.implementations.MoonphaseChart;
import wildlog.ui.reports.implementations.SightingPropertiesChart;
import wildlog.ui.reports.implementations.SightingStatsChart;
import wildlog.ui.reports.implementations.SpeciesAccumulationChart;
import wildlog.ui.reports.implementations.TextReports;
import wildlog.ui.reports.implementations.TimeOfDayChart;
import wildlog.ui.reports.implementations.TimelineChart;
import wildlog.ui.reports.implementations.VisitChart;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ComboBoxToShowReports;


public class ReportsBaseDialog extends JFrame {
    private final JFXPanel jfxReportChartPanel;
    private final JFXPanel jfxReportListPanel;
    private final List<Sighting> lstOriginalData;
    private final List<Sighting> lstFilteredData;
    private List<Element> lstFilteredElements;
    private List<Location> lstFilteredLocations;
    private List<Visit> lstFilteredVisits;
    private FilterProperties filterProperties = null;
    private AbstractReport activeReport = null;

    
    public ReportsBaseDialog(String inTitle, List<Sighting> inSightings) {
        super(inTitle);
        WildLogApp.LOGGER.log(Level.INFO, "[ReportsBaseDialog]");
        lstOriginalData = inSightings;
        // Get a copy for the filter list
        lstFilteredData = getCopiedList(lstOriginalData);
        // Init the autogenerated UI code
        initComponents();
        // Initialise the rest of the screen 
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.setupGlassPaneOnMainFrame(this);
        UtilsDialog.addEscapeKeyListener(this);
        // Create the nested JavaFx panels
        jfxReportChartPanel = new JFXPanel();
        jfxReportChartPanel.setBackground(pnlChartArea.getBackground());
        pnlChartArea.add(jfxReportChartPanel, BorderLayout.CENTER);
        jfxReportListPanel = new JFXPanel();
        // Add the report types and sub reports to the accordian
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupReportList();
            }
        });
    }
    
    private void setupReportList() {
        // Setup the report buttons
        pnlReports.add(jfxReportListPanel, BorderLayout.CENTER);
        AnchorPane anchorPaneForButtons = new AnchorPane();
        Scene sceneReportList = new Scene(anchorPaneForButtons);
        jfxReportListPanel.setScene(sceneReportList);
        Accordion accordion = new Accordion();
        ScrollPane scrollPaneForButtons = new ScrollPane(accordion);
        scrollPaneForButtons.setFitToWidth(true);
        AnchorPane.setTopAnchor(scrollPaneForButtons, 0.0);
        AnchorPane.setBottomAnchor(scrollPaneForButtons, 0.0);
        AnchorPane.setLeftAnchor(scrollPaneForButtons, 0.0);
        AnchorPane.setRightAnchor(scrollPaneForButtons, 0.0);
        anchorPaneForButtons.getChildren().add(scrollPaneForButtons);
        // Setup the report panel (om een of ander rede mot die een tweede wees)
        VBox vbox = new VBox();
        // Workaround: Lyk my die snapshot werk beter as ek eers iets anders in die scene laai voor ek die charts laai...
        Label lblInfo = new Label("Please select the report you would like to view from the list on the left.\n\n"
                + "A description of the active report is provided at the bottom of the window.\n\n"
                + "You can filter the number of Observations that are used in the report by using the buttons in the Report Data Filters section.\n\n"
                + "Reports can be exported using the Export Report button.\n\n"
                + "You can click on the report to view the data values at the selected point.\n\n"
                + "Warning: \n"
                + "Some reports may display incorrectly when there is too much data to represent visually on the chart.\n"
                + "Using very large datasets with some reports can make the application become unresponsive for a while, try to reduce the amount of data displayed at a time.");
        lblInfo.setPadding(new Insets(20));
        lblInfo.setFont(new Font(18));
        lblInfo.setWrapText(true);
        vbox.getChildren().add(lblInfo);
        Scene sceneCharts = new Scene(vbox);
        sceneCharts.getStylesheets().add("wildlog/ui/reports/chart/styling/Charts.css");
        jfxReportChartPanel.setScene(sceneCharts);
        // Setup the default reports
        List<AbstractReport<Sighting>> reports = new ArrayList<>(13);
        reports.add(new ElementsChart(lstFilteredData, lblReportDescription));
        reports.add(new LocationChart(lstFilteredData, lblReportDescription));
        reports.add(new VisitChart(lstFilteredData, lblReportDescription));
        reports.add(new SightingPropertiesChart(lstFilteredData, lblReportDescription));
        reports.add(new DayAndNightChart(lstFilteredData, lblReportDescription));
        reports.add(new TimeOfDayChart(lstFilteredData, lblReportDescription));
        reports.add(new MoonphaseChart(lstFilteredData, lblReportDescription));
        reports.add(new DurationChart(lstFilteredData, lblReportDescription));
        reports.add(new TimelineChart(lstFilteredData, lblReportDescription));
        reports.add(new EventTimelineChart(lstFilteredData, lblReportDescription));
        reports.add(new SpeciesAccumulationChart(lstFilteredData, lblReportDescription));
        reports.add(new SightingStatsChart(lstFilteredData, lblReportDescription));
        reports.add(new TextReports(lstFilteredData, lblReportDescription));
        // Setup loading label
        final Label lblLoading = new Label("... LOADING ...");
        lblLoading.setPadding(new Insets(20));
        lblLoading.setFont(new Font(24));
        lblLoading.setTextAlignment(TextAlignment.CENTER);
        lblLoading.setAlignment(Pos.CENTER);
        // Add the reports
        for (final AbstractReport<Sighting> report : reports) {
            VBox vBox = new VBox(5);
            vBox.setFillWidth(true);
            TitledPane reportButton = new TitledPane(report.getReportCategoryTitle(), vBox);
            for (Node node : report.getLstCustomButtons()) {
                if (node instanceof ToggleButton && !(node instanceof RadioButton) || node instanceof ComboBoxToShowReports) {
                    node.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent inEvent) {
                            activeReport = report;
                            jfxReportChartPanel.getScene().setRoot(lblLoading);
                            activeReport.createReport(jfxReportChartPanel.getScene());
                        }
                    });
                }
                ((Control) node).setMaxWidth(500);
                if (node instanceof Labeled && !(node instanceof CheckBox) && !(node instanceof RadioButton)) {
                    ((Labeled) node).setAlignment(Pos.BASELINE_LEFT);
                }
                vBox.getChildren().add(node);
            }
            accordion.getPanes().add(reportButton);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlReportsAndFilters = new javax.swing.JPanel();
        pnlReports = new javax.swing.JPanel();
        pnlExport = new javax.swing.JPanel();
        bntExport = new javax.swing.JButton();
        pnlFilters = new javax.swing.JPanel();
        btnFilterProperties = new javax.swing.JButton();
        btnFilterElement = new javax.swing.JButton();
        btnFilterLocation = new javax.swing.JButton();
        btnFilterVisit = new javax.swing.JButton();
        btnResetFilters = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblTotalRecords = new javax.swing.JLabel();
        lblFilteredRecords = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pnlChartArea = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        pnlChartDescription = new javax.swing.JPanel();
        lblReportDescription = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/WildLog Report Icon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(920, 600));

        jSplitPane1.setMinimumSize(new java.awt.Dimension(210, 450));

        pnlReportsAndFilters.setBackground(new java.awt.Color(179, 198, 172));
        pnlReportsAndFilters.setMinimumSize(new java.awt.Dimension(200, 500));
        pnlReportsAndFilters.setPreferredSize(new java.awt.Dimension(265, 500));

        pnlReports.setBackground(new java.awt.Color(179, 198, 172));
        pnlReports.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Types", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlReports.setLayout(new java.awt.BorderLayout());

        pnlExport.setBackground(new java.awt.Color(179, 198, 172));
        pnlExport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Exports", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        bntExport.setBackground(new java.awt.Color(179, 198, 172));
        bntExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        bntExport.setText("Export Report");
        bntExport.setToolTipText("Export the active report to one of many export formats.");
        bntExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bntExport.setFocusPainted(false);
        bntExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bntExport.setIconTextGap(10);
        bntExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlExportLayout = new javax.swing.GroupLayout(pnlExport);
        pnlExport.setLayout(pnlExportLayout);
        pnlExportLayout.setHorizontalGroup(
            pnlExportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExportLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(bntExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        pnlExportLayout.setVerticalGroup(
            pnlExportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExportLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(bntExport, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pnlFilters.setBackground(new java.awt.Color(179, 198, 172));
        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Data Filters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        btnFilterProperties.setBackground(new java.awt.Color(179, 198, 172));
        btnFilterProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/FilterSightings.png"))); // NOI18N
        btnFilterProperties.setText("Filter on Properties");
        btnFilterProperties.setToolTipText("Filter the Observations according to the value of its data fields.");
        btnFilterProperties.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterProperties.setFocusPainted(false);
        btnFilterProperties.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterProperties.setIconTextGap(10);
        btnFilterProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterPropertiesActionPerformed(evt);
            }
        });

        btnFilterElement.setBackground(new java.awt.Color(179, 198, 172));
        btnFilterElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnFilterElement.setText("Filter by Creature");
        btnFilterElement.setToolTipText("Filter the Observations according to its Creature.");
        btnFilterElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterElement.setFocusPainted(false);
        btnFilterElement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterElement.setIconTextGap(10);
        btnFilterElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterElementActionPerformed(evt);
            }
        });

        btnFilterLocation.setBackground(new java.awt.Color(179, 198, 172));
        btnFilterLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnFilterLocation.setText("Filter by Place");
        btnFilterLocation.setToolTipText("Filter the Observations according to its Place.");
        btnFilterLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterLocation.setFocusPainted(false);
        btnFilterLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterLocation.setIconTextGap(10);
        btnFilterLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterLocationActionPerformed(evt);
            }
        });

        btnFilterVisit.setBackground(new java.awt.Color(179, 198, 172));
        btnFilterVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnFilterVisit.setText("Filter by Period");
        btnFilterVisit.setToolTipText("Filter the Observations according to its Period.");
        btnFilterVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterVisit.setFocusPainted(false);
        btnFilterVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterVisit.setIconTextGap(10);
        btnFilterVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterVisitActionPerformed(evt);
            }
        });

        btnResetFilters.setBackground(new java.awt.Color(179, 198, 172));
        btnResetFilters.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnResetFilters.setText("Reset Active Data Filters");
        btnResetFilters.setToolTipText("Remove all the active filters.");
        btnResetFilters.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetFilters.setFocusPainted(false);
        btnResetFilters.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResetFilters.setIconTextGap(10);
        btnResetFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetFiltersActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(192, 207, 186));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Total Observations:");

        lblTotalRecords.setText(Integer.toString(lstOriginalData.size()));

        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Selected Observations:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFilteredRecords, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalRecords, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblFilteredRecords))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lblTotalRecords))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnFilterLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterElement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnResetFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnFilterLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnFilterVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnFilterElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnFilterProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnResetFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout pnlReportsAndFiltersLayout = new javax.swing.GroupLayout(pnlReportsAndFilters);
        pnlReportsAndFilters.setLayout(pnlReportsAndFiltersLayout);
        pnlReportsAndFiltersLayout.setHorizontalGroup(
            pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlReportsAndFiltersLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlReports, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        pnlReportsAndFiltersLayout.setVerticalGroup(
            pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportsAndFiltersLayout.createSequentialGroup()
                .addComponent(pnlReports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(pnlExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        jSplitPane1.setLeftComponent(pnlReportsAndFilters);

        pnlChartArea.setBackground(new java.awt.Color(255, 255, 255));
        pnlChartArea.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.BorderLayout(0, 2));

        pnlChartDescription.setBackground(new java.awt.Color(213, 230, 205));
        pnlChartDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Description", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        lblReportDescription.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout pnlChartDescriptionLayout = new javax.swing.GroupLayout(pnlChartDescription);
        pnlChartDescription.setLayout(pnlChartDescriptionLayout);
        pnlChartDescriptionLayout.setHorizontalGroup(
            pnlChartDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChartDescriptionLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblReportDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        pnlChartDescriptionLayout.setVerticalGroup(
            pnlChartDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChartDescriptionLayout.createSequentialGroup()
                .addComponent(lblReportDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );

        jPanel5.add(pnlChartDescription, java.awt.BorderLayout.CENTER);

        pnlChartArea.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(pnlChartArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntExportActionPerformed
        if (activeReport != null) {
            // The snapshot needs to be loaded from a JavaFX thread
            final JFrame parent = this;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        WritableImage writableImage = jfxReportChartPanel.getScene().snapshot(null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ExportDialogForReportsAndMaps dialog = new ExportDialogForReportsAndMaps(parent, bufferedImage, 
                                        jfxReportChartPanel.getScene().getRoot(), 
                                        activeReport.getReportCategoryTitle() + " - " + activeReport.getActiveSubCategoryTitle() + " - ", 
                                        lstFilteredData, ExportDialogForReportsAndMaps.ExportType.REPORTS);
                                dialog.setVisible(true);
                            }
                        });
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            });
        }
    }//GEN-LAST:event_bntExportActionPerformed

    private void btnFilterElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterElementActionPerformed
        FilterDataListDialog<Element> dialog = new FilterDataListDialog<Element>(this, lstOriginalData, lstFilteredElements, Element.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredElements = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering(lstOriginalData, lstFilteredData, 
                    lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                    lblFilteredRecords, activeReport, jfxReportChartPanel);
        }
    }//GEN-LAST:event_btnFilterElementActionPerformed

    private void btnFilterLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterLocationActionPerformed
        FilterDataListDialog<Location> dialog = new FilterDataListDialog<Location>(this, lstOriginalData, lstFilteredLocations, Location.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredLocations = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering(lstOriginalData, lstFilteredData, 
                    lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                    lblFilteredRecords, activeReport, jfxReportChartPanel);
        }
    }//GEN-LAST:event_btnFilterLocationActionPerformed

    private void btnFilterVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterVisitActionPerformed
        FilterDataListDialog<Visit> dialog = new FilterDataListDialog<Visit>(this, lstOriginalData, lstFilteredVisits, Visit.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredVisits = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering(lstOriginalData, lstFilteredData, 
                    lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                    lblFilteredRecords, activeReport, jfxReportChartPanel);
        }
    }//GEN-LAST:event_btnFilterVisitActionPerformed

    private void btnFilterPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterPropertiesActionPerformed
        FilterPropertiesDialog<Sighting> dialog = new FilterPropertiesDialog<>(this, lstOriginalData, filterProperties);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            filterProperties = dialog.getSelectedFilterProperties();
            // Filter the original results using the provided values
            doFiltering(lstOriginalData, lstFilteredData, 
                    lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                    lblFilteredRecords, activeReport, jfxReportChartPanel);
        }
    }//GEN-LAST:event_btnFilterPropertiesActionPerformed

    private void btnResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetFiltersActionPerformed
        lstFilteredElements = null;
        lstFilteredLocations = null;
        lstFilteredVisits = null;
        filterProperties = null;
        doFiltering(lstOriginalData, lstFilteredData, 
                lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                lblFilteredRecords, activeReport, jfxReportChartPanel);
    }//GEN-LAST:event_btnResetFiltersActionPerformed
    
    private List<Sighting> getCopiedList(List<Sighting> inList) {
        List<Sighting> list = new ArrayList<>(inList.size());
        for (Sighting sighting : inList) {
            list.add(sighting.cloneShallow());
        }
        return list;
    }
    
    private void doFiltering(final List<Sighting> inLstOriginalData, List<Sighting> inLstFilteredData, 
            List<Element> inLstFilteredElements, List<Location> inLstFilteredLocations, List<Visit> inLstFilteredVisits,
            FilterProperties inLilterProperties, JLabel inLblFilteredRecords, AbstractReport inActiveReport, JFXPanel inJfxReportChartPanel) {
        // NOTE: Don't create a new ArrayList (clear existing instead), because the reports are holding on to the reference 
        //       and will be stuck with an old list otherwise. Easiest to just keep the reference constant than to try and 
        //       update all the reports everytime (the active report already gets updated explicitly).
        inLstFilteredData.clear();
        // All filters need to be taken into account all the time, even if only one was changed the results must still fullfill the other filters...
        for (Sighting sighting : inLstOriginalData) {
            // Check filtered Elements
            if (inLstFilteredElements != null) {
                boolean found = false;
                for (Element element : inLstFilteredElements) {
                    if (sighting.getElementName().equals(element.getPrimaryName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Locations
            if (inLstFilteredLocations != null) {
                boolean found = false;
                for (Location location : inLstFilteredLocations) {
                    if (sighting.getLocationName().equals(location.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Visits
            if (inLstFilteredVisits != null) {
                boolean found = false;
                for (Visit visit : inLstFilteredVisits) {
                    if (sighting.getVisitName().equals(visit.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Properties
            if (!FilterPropertiesDialog.checkFilterPropertiesMatch(inLilterProperties, sighting)) {
                continue;
            }
            // If we haven't breaked from the for loop yet (aka continued to the next record), 
            // then this record can be added to the list
            inLstFilteredData.add(sighting.cloneShallow());
        }
        inLblFilteredRecords.setText(Integer.toString(inLstFilteredData.size()));
        // Redraw the chart
        if (inActiveReport != null) {
            inActiveReport.setDataList(inLstFilteredData);
            inActiveReport.createReport(inJfxReportChartPanel.getScene());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntExport;
    private javax.swing.JButton btnFilterElement;
    private javax.swing.JButton btnFilterLocation;
    private javax.swing.JButton btnFilterProperties;
    private javax.swing.JButton btnFilterVisit;
    private javax.swing.JButton btnResetFilters;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblFilteredRecords;
    private javax.swing.JLabel lblReportDescription;
    private javax.swing.JLabel lblTotalRecords;
    private javax.swing.JPanel pnlChartArea;
    private javax.swing.JPanel pnlChartDescription;
    private javax.swing.JPanel pnlExport;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlReports;
    private javax.swing.JPanel pnlReportsAndFilters;
    // End of variables declaration//GEN-END:variables
}
