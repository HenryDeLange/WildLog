package wildlog.ui.reports;

import com.pdfjet.A4;
import com.pdfjet.Box;
import com.pdfjet.Color;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.Image;
import com.pdfjet.ImageType;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.TextLine;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.VisitType;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.reports.helpers.FilterProperties;
import wildlog.ui.reports.implementations.DayAndNightChart;
import wildlog.ui.reports.implementations.ElementsChart;
import wildlog.ui.reports.implementations.LocationChart;
import wildlog.ui.reports.implementations.MoonphaseChart;
import wildlog.ui.reports.implementations.SightingStatsChart;
import wildlog.ui.reports.implementations.SpeciesAccumulationChart;
import wildlog.ui.reports.implementations.TextReports;
import wildlog.ui.reports.implementations.TimeOfDayChart;
import wildlog.ui.reports.implementations.TimelineChart;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class ReportsBaseDialog extends JFrame {
    private final JFXPanel jfxPanel;
    private final List<Sighting> lstOriginalData;
    private List<Sighting> lstFilteredData;
    private List<Element> lstFilteredElements;
    private List<Location> lstFilteredLocations;
    private List<Visit> lstFilteredVisits;
//    private List<Sighting> lstFilteredSightings;
    private FilterProperties filterProperties = null;

    public ReportsBaseDialog(String inTitle, List<Sighting> inSightings) {
        super(inTitle);
        Platform.setImplicitExit(false);
        jfxPanel = new JFXPanel();
        lstOriginalData = inSightings;
        // Setup the default reports
        List<AbstractReport<Sighting>> reports = new ArrayList<>(10);
        reports.add(new TimelineChart());
        reports.add(new TimeOfDayChart());
        reports.add(new MoonphaseChart());
        reports.add(new SpeciesAccumulationChart());
        reports.add(new DayAndNightChart());
        reports.add(new ElementsChart());
        reports.add(new LocationChart());
        reports.add(new SightingStatsChart());
        reports.add(new TextReports());
        init(reports);
    }
    
    public ReportsBaseDialog(String inTitle, List<Sighting> inSightings, List<AbstractReport<Sighting>> inLstReports) {
        super(inTitle);
        lstOriginalData = inSightings;
        Platform.setImplicitExit(false);
        jfxPanel = new JFXPanel();
        init(inLstReports);
    }

    private void init(List<AbstractReport<Sighting>> inLstReports) {
        lstFilteredData = getCopiedList(lstOriginalData);
        initComponents();
        jfxPanel.setBackground(pnlChartArea.getBackground());
        pnlChartArea.add(jfxPanel, BorderLayout.CENTER);
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.setupGlassPaneOnMainFrame(this);
        for (final AbstractReport<Sighting> report : inLstReports) {
            report.setChartOptionsPanel(pnlChartOptions);
            JToggleButton reportButton = new JToggleButton(report.getReportButtonName(), 
                    new ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.gif")), false);
            reportButton.setFocusPainted(false);
            reportButton.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
            reportButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            reportButton.setIconTextGap(8);
            reportButton.setMargin(new Insets(2, 4, 2, 4));
            reportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    report.setDataList(lstFilteredData);
                    report.setupReportOptionsPanel();
                    report.setChartDescriptionLabel(lblReportDescription);
                    report.setupChartDescriptionLabel();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxPanel.setScene(new Scene(report.createReport()));
                        }
                    });
                }
            });
            btnGroupForReportTypes.add(reportButton);
            pnlAvailableReports.add(reportButton);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupForReportTypes = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlReportsAndFilters = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlAvailableReports = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnFilterProperties = new javax.swing.JButton();
        btnFilterElement = new javax.swing.JButton();
        btnFilterLocation = new javax.swing.JButton();
        btnFilterVisit = new javax.swing.JButton();
        btnFilterSightings = new javax.swing.JButton();
        btnResetFilters = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblTotalRecords = new javax.swing.JLabel();
        lblFilteredRecords = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pnlChartArea = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        pnlChartOptions = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        pnlChartDescription = new javax.swing.JPanel();
        lblReportDescription = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/Report.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(920, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Types", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jScrollPane1.setBorder(null);

        pnlAvailableReports.setLayout(new org.jdesktop.swingx.VerticalLayout());
        jScrollPane1.setViewportView(pnlAvailableReports);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Export Options", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        jButton1.setText("Export Report");
        jButton1.setToolTipText("Export the report to PDF or PNG.");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Data Options", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        btnFilterProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnFilterProperties.setText("Filter on Properties");
        btnFilterProperties.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterProperties.setFocusPainted(false);
        btnFilterProperties.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterPropertiesActionPerformed(evt);
            }
        });

        btnFilterElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnFilterElement.setText("Filter by Creature");
        btnFilterElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterElement.setFocusPainted(false);
        btnFilterElement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterElementActionPerformed(evt);
            }
        });

        btnFilterLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnFilterLocation.setText("Filter by Place");
        btnFilterLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterLocation.setFocusPainted(false);
        btnFilterLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterLocationActionPerformed(evt);
            }
        });

        btnFilterVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnFilterVisit.setText("Filter by Period");
        btnFilterVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterVisit.setFocusPainted(false);
        btnFilterVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterVisitActionPerformed(evt);
            }
        });

        btnFilterSightings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sighting Small.gif"))); // NOI18N
        btnFilterSightings.setText("View Filtered Observations");
        btnFilterSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterSightings.setFocusPainted(false);
        btnFilterSightings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFilterSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterSightingsActionPerformed(evt);
            }
        });

        btnResetFilters.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnResetFilters.setText("Reset Active Data Filters");
        btnResetFilters.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetFilters.setFocusPainted(false);
        btnResetFilters.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResetFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetFiltersActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Selected Records:");

        lblTotalRecords.setText(Integer.toString(lstOriginalData.size()));

        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Total Records:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalRecords)
                    .addComponent(lblFilteredRecords))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblTotalRecords))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFilteredRecords)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnResetFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterElement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterSightings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFilterProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnFilterProperties)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFilterElement)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFilterLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFilterVisit)
                .addGap(10, 10, 10)
                .addComponent(btnFilterSightings)
                .addGap(10, 10, 10)
                .addComponent(btnResetFilters)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnlReportsAndFiltersLayout = new javax.swing.GroupLayout(pnlReportsAndFilters);
        pnlReportsAndFilters.setLayout(pnlReportsAndFiltersLayout);
        pnlReportsAndFiltersLayout.setHorizontalGroup(
            pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlReportsAndFiltersLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        pnlReportsAndFiltersLayout.setVerticalGroup(
            pnlReportsAndFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportsAndFiltersLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        jSplitPane1.setLeftComponent(pnlReportsAndFilters);

        pnlChartArea.setBackground(new java.awt.Color(255, 255, 255));
        pnlChartArea.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.BorderLayout(0, 2));

        pnlChartOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Display Options", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlChartOptions.setPreferredSize(new java.awt.Dimension(144, 50));
        org.jdesktop.swingx.HorizontalLayout horizontalLayout1 = new org.jdesktop.swingx.HorizontalLayout();
        horizontalLayout1.setGap(5);
        pnlChartOptions.setLayout(horizontalLayout1);

        jButton2.setText("Colors");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        pnlChartOptions.add(jButton2);

        jPanel5.add(pnlChartOptions, java.awt.BorderLayout.NORTH);

        pnlChartDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Report Description", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        lblReportDescription.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblReportDescription.setText("Select a Report Type to view from the list on the left.");

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

        pnlChartArea.add(jPanel5, java.awt.BorderLayout.NORTH);

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
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO: PDF, PNG en actual Print. Dalk ook sommer HTML.
        // TODO: Export ook die X en Y asse se data na CSV sodat mens maklik die data in Excel kan in trek en ander charts maak
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Files.createDirectories(WildLogPaths.WILDLOG_EXPORT_PDF.getAbsoluteFullPath());
                    PDF pdf = new PDF(new BufferedOutputStream(new FileOutputStream(WildLogPaths.WILDLOG_EXPORT_PDF.getAbsoluteFullPath().resolve("ReportToets.pdf").toFile())));
                    Page page = new Page(pdf, A4.LANDSCAPE);
                    Font font = new Font(pdf, CoreFont.HELVETICA);
                    // The snapshot needs to be loaded from a JavaFX thread
                    WritableImage writableImage = jfxPanel.getScene().snapshot(null);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ByteArrayOutputStream output = new ByteArrayOutputStream() {
                            @Override
                            public synchronized byte[] toByteArray() {
                                // Return the original array, not a copy (for performance benifits)
                                return this.buf;
                            }
                        };
//                    ImageIO.write(bufferedImage, "png", WildLogPaths.WILDLOG_EXPORT_PDF.getAbsoluteFullPath().resolve("ReportToets.png").toFile());
                    ImageIO.write(bufferedImage, "png", output);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray(), 0, output.size());
                    Image embeddedImage = new Image(pdf, inputStream, ImageType.PNG);
                    TextLine text = new TextLine(font, "Report XXX");
                    text.setLocation(90f, 30f);
                    text.drawOn(page);
                    embeddedImage.setLocation(90f, 40f);
                    // Convert the pixels (image) to points (page).
                    // Basically 1 point is 1/72 inch and we are assuming the default dpi is still 96 (72/96 = 0.75).
                    // Then scale it to the total page size wich is in points.
                    embeddedImage.scaleBy((bufferedImage.getWidth()*0.75)/page.getWidth());
                    
                    Box box = new Box();
                    box.setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
                    box.setColor(Color.black);
                    box.drawOn(page);
                    
                    embeddedImage.drawOn(page);
                    pdf.close();
                    UtilsFileProcessing.openFile(WildLogPaths.WILDLOG_EXPORT_PDF.getAbsoluteFullPath());
                }
                catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    // TODO: close, flush, etc.
                }
            }
        });
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnFilterElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterElementActionPerformed
        FilterDataListDialog<Element> dialog = new FilterDataListDialog<Element>(this, lstOriginalData, lstFilteredElements, Element.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredElements = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering();
        }
    }//GEN-LAST:event_btnFilterElementActionPerformed

    private void btnFilterLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterLocationActionPerformed
        FilterDataListDialog<Location> dialog = new FilterDataListDialog<Location>(this, lstOriginalData, lstFilteredLocations, Location.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredLocations = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering();
        }
    }//GEN-LAST:event_btnFilterLocationActionPerformed

    private void btnFilterVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterVisitActionPerformed
        FilterDataListDialog<Visit> dialog = new FilterDataListDialog<Visit>(this, lstOriginalData, lstFilteredVisits, Visit.class);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            lstFilteredVisits = dialog.getSelectedData();
            // Filter the original results using the provided values
            doFiltering();
        }
    }//GEN-LAST:event_btnFilterVisitActionPerformed

    private void btnFilterSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterSightingsActionPerformed
        FilterDataListDialog<Sighting> dialog = new FilterDataListDialog<Sighting>(this, lstOriginalData, lstFilteredData, Sighting.class);
        dialog.setVisible(true);
//        if (dialog.isSelectionMade()) {
//            lstFilteredSightings = dialog.getSelectedData();
//            // Filter the original results using the provided values
//            doFiltering();
//        }
    }//GEN-LAST:event_btnFilterSightingsActionPerformed

    private void btnFilterPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterPropertiesActionPerformed
        FilterPropertiesDialog<Sighting> dialog = new FilterPropertiesDialog<>(this, lstOriginalData, filterProperties);
        dialog.setVisible(true);
        if (dialog.isSelectionMade()) {
            filterProperties = dialog.getSelectedFilterProperties();
            // Filter the original results using the provided values
            doFiltering();
        }
    }//GEN-LAST:event_btnFilterPropertiesActionPerformed

    private void btnResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetFiltersActionPerformed
        lstFilteredData = getCopiedList(lstOriginalData);
        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));
    }//GEN-LAST:event_btnResetFiltersActionPerformed

    private void doFiltering() {
        // All filters need to be taken into account all the time, even if only one was changed the results must still fullfill the other filters...
        lstFilteredData = new ArrayList<>(lstOriginalData.size());
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
            // Check filtered Sightings
            // TODO: mmm dit gaan dalk tricky wees...
            
            // Check filtered Properties
            if (filterProperties != null) {
                // Date
                if (filterProperties.getStartDate() != null) {
                    if (UtilsTime.getLocalDateFromDate(sighting.getDate()).isBefore(filterProperties.getStartDate())) {
                        continue;
                    }
                }
                if (filterProperties.getEndDate() != null) {
                    if (UtilsTime.getLocalDateFromDate(sighting.getDate()).isAfter(filterProperties.getEndDate())) {
                        continue;
                    }
                }
                // Time
                if (filterProperties.getStartTime() != null) {
                    if (UtilsTime.getLocalTimeFromDate(sighting.getDate()).isBefore(filterProperties.getStartTime())) {
                        continue;
                    }
                }
                if (filterProperties.getEndTime() != null) {
                    if (UtilsTime.getLocalTimeFromDate(sighting.getDate()).isAfter(filterProperties.getEndTime())) {
                        continue;
                    }
                }
                // Time of day
                if (filterProperties.getActiveTimes() != null) {
                    boolean found = false;
                    for (ActiveTimeSpesific activeTimeSpesific : filterProperties.getActiveTimes()) {
                        if (activeTimeSpesific.equals(sighting.getTimeOfDay())) {
                            found = true;
                            break;
                        }
                        if (activeTimeSpesific.equals(ActiveTimeSpesific.UNKNOWN)) {
                            if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Moonlight
                if (filterProperties.getMoonlights() != null) {
                    boolean found = false;
                    for (Moonlight moonlight : filterProperties.getMoonlights()) {
                        if (moonlight.equals(sighting.getMoonlight())) {
                            found = true;
                            break;
                        }
                        if (moonlight.equals(Moonlight.UNKNOWN)) {
                            if (sighting.getMoonlight() == null || Moonlight.NONE.equals(sighting.getMoonlight())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Moonphase
                if (!(filterProperties.isMoonphaseIsLess() && filterProperties.isMoonphaseIsMore()) && sighting.getMoonPhase()  >= 0) {
                    boolean found = false;
                    if (filterProperties.getMoonphase() == sighting.getMoonPhase()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isMoonphaseIsLess() && sighting.getMoonPhase() < filterProperties.getMoonphase()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isMoonphaseIsMore() && sighting.getMoonPhase() > filterProperties.getMoonphase()) {
                        found = true;
                    }
                    if (!found) {
                        continue;
                    }
                }
                
                // Visit Type
                if (filterProperties.getVisitTypes() != null) {
                    boolean found = false;
                    Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                    for (VisitType visitType : filterProperties.getVisitTypes()) {
                        if (visit != null) {
                            if (visitType.equals(visit.getType())) {
                                found = true;
                                break;
                            }
                            if (visitType.equals(VisitType.UNKNOWN)) {
                                if (visit.getType() == null || VisitType.NONE.equals(visit.getType())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Evidence
                if (filterProperties.getEvidences() != null) {
                    boolean found = false;
                    for (SightingEvidence sightingEvidence : filterProperties.getEvidences()) {
                        if (sightingEvidence.equals(sighting.getSightingEvidence())) {
                            found = true;
                            break;
                        }
                        if (sightingEvidence.equals(SightingEvidence.UNKNOWN)) {
                            if (sighting.getSightingEvidence() == null || SightingEvidence.NONE.equals(sighting.getSightingEvidence())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Life Status
                if (filterProperties.getLifeStatuses() != null) {
                    boolean found = false;
                    for (LifeStatus lifeStatus : filterProperties.getLifeStatuses()) {
                        if (lifeStatus.equals(sighting.getLifeStatus())) {
                            found = true;
                            break;
                        }
                        if (lifeStatus.equals(LifeStatus.UNKNOWN)) {
                            if (sighting.getLifeStatus() == null || LifeStatus.NONE.equals(sighting.getLifeStatus())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Time Accuracy
                if (filterProperties.getTimeAccuracies() != null) {
                    boolean found = false;
                    for (TimeAccuracy timeAccuracy : filterProperties.getTimeAccuracies()) {
                        if (timeAccuracy.equals(sighting.getTimeAccuracy())) {
                            found = true;
                            break;
                        }
                        if (timeAccuracy.equals(TimeAccuracy.UNKNOWN)) {
                            if (sighting.getTimeAccuracy() == null || TimeAccuracy.NONE.equals(sighting.getTimeAccuracy())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Sighting Certianty
                if (filterProperties.getCertainties() != null) {
                    boolean found = false;
                    for (Certainty certainty : filterProperties.getCertainties()) {
                        if (certainty.equals(sighting.getCertainty())) {
                            found = true;
                            break;
                        }
                        if (certainty.equals(Certainty.UNKNOWN)) {
                            if (sighting.getCertainty() == null || Certainty.NONE.equals(sighting.getCertainty())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // GPS Certainty
                if (filterProperties.getGPSAccuracies() != null) {
                    boolean found = false;
                    for (GPSAccuracy gpsAccuracy : filterProperties.getGPSAccuracies()) {
                        if (gpsAccuracy.equals(sighting.getGPSAccuracy())) {
                            found = true;
                            break;
                        }
                        if (gpsAccuracy.equals(GPSAccuracy.UNKNOWN)) {
                            if (sighting.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(sighting.getGPSAccuracy())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Age
                if (filterProperties.getAges() != null) {
                    boolean found = false;
                    for (Age age : filterProperties.getAges()) {
                        if (age.equals(sighting.getAge())) {
                            found = true;
                            break;
                        }
                        if (age.equals(Age.UNKNOWN)) {
                            if (sighting.getAge() == null || Age.NONE.equals(sighting.getAge())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Sex
                if (filterProperties.getSexes() != null) {
                    boolean found = false;
                    for (Sex sex : filterProperties.getSexes()) {
                        if (sex.equals(sighting.getSex())) {
                            found = true;
                            break;
                        }
                        if (sex.equals(Sex.UNKNOWN)) {
                            if (sighting.getSex() == null || Sex.NONE.equals(sighting.getSex())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Number of individuals
                if (!(filterProperties.isNumberOfElementsIsLess()&& filterProperties.isNumberOfElementsIsMore()) && sighting.getNumberOfElements() >= 0) {
                    boolean found = false;
                    if (filterProperties.getNumberOfElements() == sighting.getNumberOfElements()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isNumberOfElementsIsLess() && sighting.getNumberOfElements() < filterProperties.getNumberOfElements()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isNumberOfElementsIsMore() && sighting.getNumberOfElements() > filterProperties.getNumberOfElements()) {
                        found = true;
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Tag
                if (sighting.getTag() == null || sighting.getTag().trim().isEmpty()) {
                    if (!filterProperties.isIncludeEmptyTags()) {
                        continue;
                    }
                }
                else
                if (filterProperties.getTags() != null && !filterProperties.getTags().isEmpty()) {
                    boolean found = false;
                    for (String tag : filterProperties.getTags()) {
                        if (!tag.trim().isEmpty() && sighting.getTag().trim().toLowerCase().contains(tag.trim().toLowerCase())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Element Type
                if (filterProperties.getElementTypes()!= null) {
                    boolean found = false;
                    Element element = WildLogApp.getApplication().getDBI().find(new Element(sighting.getElementName()));
                    for (ElementType elementType : filterProperties.getElementTypes()) {
                        if (element != null) {
                            if (elementType.equals(element.getType())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
            }
            // If we haven't breaked from the for loop yet (aka continued to the next record), 
            // then this record can be added to the list
            lstFilteredData.add(sighting.cloneShallow());
        }
        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));
        // Redraw the chart
        for (Enumeration<AbstractButton> buttons = btnGroupForReportTypes.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                button.doClick();
            }
        }
    }
    
    private List<Sighting> getCopiedList(List<Sighting> inList) {
        List<Sighting> list = new ArrayList<>(inList.size());
        for (Sighting sighting : inList) {
            list.add(sighting.cloneShallow());
        }
        return list;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilterElement;
    private javax.swing.JButton btnFilterLocation;
    private javax.swing.JButton btnFilterProperties;
    private javax.swing.JButton btnFilterSightings;
    private javax.swing.JButton btnFilterVisit;
    private javax.swing.ButtonGroup btnGroupForReportTypes;
    private javax.swing.JButton btnResetFilters;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblFilteredRecords;
    private javax.swing.JLabel lblReportDescription;
    private javax.swing.JLabel lblTotalRecords;
    private javax.swing.JPanel pnlAvailableReports;
    private javax.swing.JPanel pnlChartArea;
    private javax.swing.JPanel pnlChartDescription;
    private javax.swing.JPanel pnlChartOptions;
    private javax.swing.JPanel pnlReportsAndFilters;
    // End of variables declaration//GEN-END:variables
}
