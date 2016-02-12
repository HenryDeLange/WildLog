package wildlog.ui.maps;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.dialogs.FilterDataListDialog;
import wildlog.ui.dialogs.FilterPropertiesDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.maps.implementations.ClimateMap;
import wildlog.ui.maps.implementations.CustomLayersMap;
import wildlog.ui.maps.implementations.DistributionMap;
import wildlog.ui.maps.implementations.EarthMap;
import wildlog.ui.maps.implementations.HeatMap;
import wildlog.ui.maps.implementations.LandStatusMap;
import wildlog.ui.maps.implementations.LegacyMap;
import wildlog.ui.maps.implementations.OtherMap;
import wildlog.ui.maps.implementations.PointMap;
import wildlog.ui.maps.implementations.WebDistributionMap;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.ui.reports.ReportExportDialog;
import wildlog.ui.reports.helpers.FilterProperties;


public class MapsBaseDialog extends JFrame {
    private final JFXPanel jfxMapPanel;
    private final JFXPanel jfxMapListPanel;
    private final List<Sighting> lstOriginalData;
    private final List<Sighting> lstFilteredData;
    private List<Element> lstFilteredElements;
    private List<Location> lstFilteredLocations;
    private List<Visit> lstFilteredVisits;
    private FilterProperties filterProperties = null;
    private AbstractMap activeMap = null;

    
    public MapsBaseDialog(String inTitle, List<Sighting> inSightings) {
        super(inTitle);
        System.out.println("[MapsBaseDialog]");
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
        jfxMapPanel = new JFXPanel();
        jfxMapPanel.setBackground(pnlMapArea.getBackground());
        pnlMapArea.add(jfxMapPanel, BorderLayout.CENTER);
        jfxMapListPanel = new JFXPanel();
        // Add the report types and sub reports to the accordian
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupReportList();
            }
        });
        // Dispose the map when the window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if (activeMap != null) {
                    activeMap.dispose();
                }
            }
        });
    }
    
    private void setupReportList() {
        // Setup the report buttons
        pnlMaps.add(jfxMapListPanel, BorderLayout.CENTER);
        AnchorPane anchorPaneForButtons = new AnchorPane();
        Scene sceneMapList = new Scene(anchorPaneForButtons);
        jfxMapListPanel.setScene(sceneMapList);
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
        Label lblInfo = new Label("Please select the map you would like to view from the list on the left.\n\n"
                + "You can filter the number of Observations that are used in the map by using the buttons in the Map Data Filters section.\n\n"
                + "Maps can be exported using the Export Map button.\n\n"
                + "Warning: \n"
                + "The maps may display incorrectly when there are too much data points on the map.\n"
                + "Displaying some maps with very large data points can make the application become unresponsive for a while, try to limit the amount of data displayed at a time.\n"
                + "Also be aware that this is not a full GIS solution and some large map layers may not display or render effeciently. "
                + "If you experience problems, please try to a smaller layer instead.");
        lblInfo.setPadding(new Insets(20));
        lblInfo.setFont(new Font(18));
        lblInfo.setWrapText(true);
        vbox.getChildren().add(lblInfo);
        Scene sceneCharts = new Scene(vbox);
        jfxMapPanel.setScene(sceneCharts);
        // Add default map description
        lblMapDescription.setText("Additional information for the selected map will be shown in this area.");
        // Setup the default reports
        List<AbstractMap<Sighting>> lstMaps = new ArrayList<>(10);
        lstMaps.add(new PointMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new EarthMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new DistributionMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new WebDistributionMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new HeatMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new ClimateMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new LandStatusMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new OtherMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new LegacyMap(lstFilteredData, lblMapDescription, this));
        lstMaps.add(new CustomLayersMap(lstFilteredData, lblMapDescription, this));
// TODO: Biomes map
        // Add the reports
        for (final AbstractMap<Sighting> map : lstMaps) {
            VBox vBox = new VBox(10);
            vBox.setFillWidth(true);
            TitledPane mapButton = new TitledPane(map.getMapButtonName(), vBox);
            for (Node node : map.getLstCustomButtons()) {
                node.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent inEvent) {
                        setActiveMap(map);
                        map.loadMap();
                    }
                });
                ((Control) node).setMaxWidth(500);
                if (node instanceof Labeled && !(node instanceof CheckBox) && !(node instanceof RadioButton)) {
                    ((Labeled) node).setAlignment(Pos.BASELINE_LEFT);
                }
                vBox.getChildren().add(node);
            }
            accordion.getPanes().add(mapButton);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlMapsAndFilters = new javax.swing.JPanel();
        pnlMaps = new javax.swing.JPanel();
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
        pnlMapArea = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        pnlMapDescription = new javax.swing.JPanel();
        lblMapDescription = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/WildLog Map Icon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(920, 600));

        jSplitPane1.setMinimumSize(new java.awt.Dimension(210, 450));

        pnlMapsAndFilters.setBackground(new java.awt.Color(172, 198, 183));
        pnlMapsAndFilters.setMinimumSize(new java.awt.Dimension(200, 500));
        pnlMapsAndFilters.setPreferredSize(new java.awt.Dimension(265, 500));

        pnlMaps.setBackground(new java.awt.Color(172, 198, 183));
        pnlMaps.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Map Types", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlMaps.setLayout(new java.awt.BorderLayout());

        pnlExport.setBackground(new java.awt.Color(172, 198, 183));
        pnlExport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Map Exports", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        bntExport.setBackground(new java.awt.Color(179, 198, 172));
        bntExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        bntExport.setText("Export Map");
        bntExport.setToolTipText("Export the shown map.");
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
                .addGap(2, 2, 2)
                .addComponent(bntExport, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pnlFilters.setBackground(new java.awt.Color(172, 198, 183));
        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Map Data Filters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

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

        jPanel4.setBackground(new java.awt.Color(172, 198, 183));
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addGap(5, 5, 5)
                .addComponent(btnFilterLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnFilterVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnFilterElement, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnFilterProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnResetFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnlMapsAndFiltersLayout = new javax.swing.GroupLayout(pnlMapsAndFilters);
        pnlMapsAndFilters.setLayout(pnlMapsAndFiltersLayout);
        pnlMapsAndFiltersLayout.setHorizontalGroup(
            pnlMapsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapsAndFiltersLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlMapsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlExport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFilters, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMaps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        pnlMapsAndFiltersLayout.setVerticalGroup(
            pnlMapsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapsAndFiltersLayout.createSequentialGroup()
                .addComponent(pnlMaps, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(pnlExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        jSplitPane1.setLeftComponent(pnlMapsAndFilters);

        pnlMapArea.setBackground(new java.awt.Color(255, 255, 255));
        pnlMapArea.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.BorderLayout(0, 2));

        pnlMapDescription.setBackground(new java.awt.Color(205, 230, 209));
        pnlMapDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Map Description", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        lblMapDescription.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout pnlMapDescriptionLayout = new javax.swing.GroupLayout(pnlMapDescription);
        pnlMapDescription.setLayout(pnlMapDescriptionLayout);
        pnlMapDescriptionLayout.setHorizontalGroup(
            pnlMapDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapDescriptionLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(lblMapDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        pnlMapDescriptionLayout.setVerticalGroup(
            pnlMapDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapDescriptionLayout.createSequentialGroup()
                .addComponent(lblMapDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );

        jPanel5.add(pnlMapDescription, java.awt.BorderLayout.CENTER);

        pnlMapArea.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(pnlMapArea);

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
        if (activeMap != null) {
            // The snapshot needs to be loaded from a JavaFX thread
            final JFrame parent = this;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        WritableImage writableImage = jfxMapPanel.getScene().snapshot(null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ReportExportDialog dialog = new ReportExportDialog(parent, bufferedImage, jfxMapPanel.getScene().getRoot(), 
                                        activeMap.getMapButtonName(), lstFilteredData, true);
                                dialog.setVisible(true);
                            }
                        });
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(System.err);
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
                    lblFilteredRecords, activeMap, jfxMapPanel);
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
                    lblFilteredRecords, activeMap, jfxMapPanel);
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
                    lblFilteredRecords, activeMap, jfxMapPanel);
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
                    lblFilteredRecords, activeMap, jfxMapPanel);
        }
    }//GEN-LAST:event_btnFilterPropertiesActionPerformed

    private void btnResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetFiltersActionPerformed
        lstFilteredElements = null;
        lstFilteredLocations = null;
        lstFilteredVisits = null;
        filterProperties = null;
        doFiltering(lstOriginalData, lstFilteredData, 
                lstFilteredElements, lstFilteredLocations, lstFilteredVisits, filterProperties, 
                lblFilteredRecords, activeMap, jfxMapPanel);
    }//GEN-LAST:event_btnResetFiltersActionPerformed
    
    private List<Sighting> getCopiedList(List<Sighting> inList) {
        List<Sighting> list = new ArrayList<>(inList.size());
        for (Sighting sighting : inList) {
            list.add(sighting.cloneShallow());
        }
        return list;
    }
    
    public void setActiveMap(AbstractMap inAbstractMap) {
        activeMap = inAbstractMap;
    }

    public AbstractMap getActiveMap() {
        return activeMap;
    }
    
    private void doFiltering(final List<Sighting> lstOriginalData, List<Sighting> lstFilteredData, 
            List<Element> lstFilteredElements, List<Location> lstFilteredLocations, List<Visit> lstFilteredVisits,
            FilterProperties filterProperties, JLabel lblFilteredRecords, AbstractMap activeMap, JFXPanel jfxReportChartPanel) {
        // NOTE: Don't create a new ArrayList (clear existing instead), because the reports are holding on to the reference 
        //       and will be stuck with an old list otherwise. Easiest to just keep the reference constant than to try and 
        //       update the all reports everytime (the active report already gets updated explicitly).
        lstFilteredData.clear();
        // All filters need to be taken into account all the time, even if only one was changed the results must still fullfill the other filters...
        for (Sighting sighting : lstOriginalData) {
            // Check filtered Elements
            if (lstFilteredElements != null) {
                boolean found = false;
                for (Element element : lstFilteredElements) {
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
            if (lstFilteredLocations != null) {
                boolean found = false;
                for (Location location : lstFilteredLocations) {
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
            if (lstFilteredVisits != null) {
                boolean found = false;
                for (Visit visit : lstFilteredVisits) {
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
            if (!FilterPropertiesDialog.checkFilterPropertiesMatch(filterProperties, sighting)) {
                continue;
            }
            // If we haven't breaked from the for loop yet (aka continued to the next record), 
            // then this record can be added to the list
            lstFilteredData.add(sighting.cloneShallow());
        }
        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));
        // Redraw the chart
        if (activeMap != null) {
            activeMap.setDataList(lstFilteredData);
            activeMap.createMap(jfxReportChartPanel.getScene());
        }
    }

    public JFXPanel getJFXMapPanel() {
        return jfxMapPanel;
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
    private javax.swing.JLabel lblMapDescription;
    private javax.swing.JLabel lblTotalRecords;
    private javax.swing.JPanel pnlExport;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlMapArea;
    private javax.swing.JPanel pnlMapDescription;
    private javax.swing.JPanel pnlMaps;
    private javax.swing.JPanel pnlMapsAndFilters;
    // End of variables declaration//GEN-END:variables
}
