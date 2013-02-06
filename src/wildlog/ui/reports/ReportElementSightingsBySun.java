package wildlog.ui.reports;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.chart.BarChartEntity;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class ReportElementSightingsBySun extends javax.swing.JFrame {
    private boolean usePrimaryName = true;
    private Element element;
    private BarChart chartTime;
    private WildLogApp app;

    /** Creates new form ReportElementSightingsBySun */
    public ReportElementSightingsBySun(Element inElement, WildLogApp inApp) {
        element = inElement;
        app = inApp;

        initComponents();

        // Setup the escape key
        final JFrame thisHandler = (JFrame)this;
        ActionListener escListiner = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.dispose();
                    }
                };
        thisHandler.getRootPane().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Position the dialog
        UtilsDialog.setDialogToCenter(app.getMainFrame(), thisHandler);

        doReport();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        lblNight = new javax.swing.JLabel();
        lblMidDay = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblMorning = new javax.swing.JLabel();
        lblAfternoon = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        lblDusk = new javax.swing.JLabel();
        scrReport = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        lblDawn = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblAddFrequency = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFirstDate = new javax.swing.JLabel();
        lblLastDate = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblWishList = new javax.swing.JLabel();
        lblNumberOfLocations = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblVeryGood = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblGood = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblNormal = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lblBad = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblVeryBad = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblFirstLocation = new javax.swing.JLabel();
        lblLastLocation = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalVisits = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintReport = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Creature Report: " + element.getPrimaryName());
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(550, 750));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 16));
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("...");
        lblName.setName("lblName"); // NOI18N
        getContentPane().add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 20));

        lblNight.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblNight.setForeground(new java.awt.Color(93, 93, 93));
        lblNight.setText("NIGHT");
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 705, 60, -1));

        lblMidDay.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblMidDay.setForeground(new java.awt.Color(185, 73, 13));
        lblMidDay.setText("MID DAY");
        lblMidDay.setName("lblMidDay"); // NOI18N
        getContentPane().add(lblMidDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 705, 70, -1));

        jLabel10.setText("Legend:");
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 705, -1, -1));

        lblMorning.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblMorning.setForeground(new java.awt.Color(194, 142, 63));
        lblMorning.setText("MORNING");
        lblMorning.setName("lblMorning"); // NOI18N
        getContentPane().add(lblMorning, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 705, 80, -1));

        lblAfternoon.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblAfternoon.setForeground(new java.awt.Color(170, 89, 40));
        lblAfternoon.setText("AFTERNOON");
        lblAfternoon.setName("lblAfternoon"); // NOI18N
        getContentPane().add(lblAfternoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 705, 90, -1));

        lblOther.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblOther.setForeground(new java.awt.Color(183, 187, 199));
        lblOther.setText("OTHER");
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 705, 60, -1));

        lblDusk.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblDusk.setForeground(new java.awt.Color(134, 98, 75));
        lblDusk.setText("DUSK");
        lblDusk.setName("lblDusk"); // NOI18N
        getContentPane().add(lblDusk, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 705, 50, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 600, 550));

        lblDawn.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblDawn.setForeground(new java.awt.Color(143, 120, 64));
        lblDawn.setText("DAWN");
        lblDawn.setName("lblDawn"); // NOI18N
        getContentPane().add(lblDawn, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 705, 60, -1));

        lblNumberOfSightings.setText("Unknown");
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 70, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Add Frequency:");
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, -1, -1));

        lblAddFrequency.setText("Unknown");
        lblAddFrequency.setName("lblAddFrequency"); // NOI18N
        getContentPane().add(lblAddFrequency, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 70, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Number of Observation:");
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Number of Places:");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 70, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("First Observation:");
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Last Observation:");
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblFirstDate.setText("Unknown");
        lblFirstDate.setName("lblFirstDate"); // NOI18N
        getContentPane().add(lblFirstDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 30, 100, -1));

        lblLastDate.setText("Unknown");
        lblLastDate.setName("lblLastDate"); // NOI18N
        getContentPane().add(lblLastDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 50, 100, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Wish List Rating:");
        jLabel19.setName("jLabel19"); // NOI18N
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        lblWishList.setText("Unknown");
        lblWishList.setName("lblWishList"); // NOI18N
        getContentPane().add(lblWishList, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 290, -1));

        lblNumberOfLocations.setText("Unknown");
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        getContentPane().add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 50, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Very Good Observations:");
        jLabel23.setName("jLabel23"); // NOI18N
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        lblVeryGood.setText("Unknown");
        lblVeryGood.setName("lblVeryGood"); // NOI18N
        getContentPane().add(lblVeryGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 50, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Good Observations:");
        jLabel25.setName("jLabel25"); // NOI18N
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        lblGood.setText("Unknown");
        lblGood.setName("lblGood"); // NOI18N
        getContentPane().add(lblGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 60, -1));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("Normal Observations:");
        jLabel27.setName("jLabel27"); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, -1, -1));

        lblNormal.setText("Unknown");
        lblNormal.setName("lblNormal"); // NOI18N
        getContentPane().add(lblNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 130, 50, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("Bad Observations:");
        jLabel29.setName("jLabel29"); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, -1, -1));

        lblBad.setText("Unknown");
        lblBad.setName("lblBad"); // NOI18N
        getContentPane().add(lblBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 70, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Very Bad Observations:");
        jLabel31.setName("jLabel31"); // NOI18N
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 110, -1, -1));

        lblVeryBad.setText("Unknown");
        lblVeryBad.setName("lblVeryBad"); // NOI18N
        getContentPane().add(lblVeryBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 110, 70, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Day Observations:");
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Night Observations:");
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, -1, -1));

        lblDaySightings.setText("Unknown");
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, 50, -1));

        lblNightSightings.setText("Unknown");
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 50, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("First Place:");
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Last Place:");
        jLabel13.setName("jLabel13"); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, -1, -1));

        lblFirstLocation.setText("Unknown");
        lblFirstLocation.setName("lblFirstLocation"); // NOI18N
        getContentPane().add(lblFirstLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(284, 30, 230, -1));

        lblLastLocation.setText("Unknown");
        lblLastLocation.setName("lblLastLocation"); // NOI18N
        getContentPane().add(lblLastLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(284, 50, 230, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Total Number of Periods:");
        jLabel14.setName("jLabel14"); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 70, -1, -1));

        lblTotalVisits.setText("Unknown");
        lblTotalVisits.setName("lblTotalVisits"); // NOI18N
        getContentPane().add(lblTotalVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 50, -1));

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        mnuPrint.setText("Print");
        mnuPrint.setName("mnuPrint"); // NOI18N

        mnuPrintReport.setName("mnuPrintReport"); // NOI18N
        mnuPrintReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintReportActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintReport);

        jMenuBar1.add(mnuPrint);

        mnuExtra.setText("Options");
        mnuExtra.setName("mnuExtra"); // NOI18N

        mnuName.setText("Switch Primary and Secondary Names");
        mnuName.setName("mnuName"); // NOI18N
        mnuName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNameActionPerformed(evt);
            }
        });
        mnuExtra.add(mnuName);

        jMenuBar1.add(mnuExtra);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintReportActionPerformed
        try {
            final JFrame frame = this;
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("WildLog Creature Report");
            pj.setCopies(1);
            PageFormat format = pj.defaultPage();
            format.setOrientation(PageFormat.PORTRAIT);
            pj.setPrintable(new Printable() {
                @Override
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    if (pageNum > 0) {
                        return Printable.NO_SUCH_PAGE;
                    }

                    frame.getContentPane().print(pg);

                    return Printable.PAGE_EXISTS;
                }
            });
            if (pj.printDialog() == false) {
                return;
            }
            pj.print();
        } catch (PrinterException ex) {
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_mnuPrintReportActionPerformed

    private void mnuNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNameActionPerformed
        usePrimaryName = ! usePrimaryName;
        doReport();
        // Re-Draw
        repaint();
        setVisible(true);
    }//GEN-LAST:event_mnuNameActionPerformed

    private void doReport() {
        // Init report fields
        if (usePrimaryName)
            lblName.setText(element.getPrimaryName());
        else
            lblName.setText(element.getOtherName());
        if (element.getWishListRating() != null)
            lblWishList.setText(element.getWishListRating().toString());
        else
            lblWishList.setText("Unknown");
        if (element.getAddFrequency() != null)
            lblAddFrequency.setText(element.getAddFrequency().toString());
        else
            lblAddFrequency.setText("Unknown");
        Sighting tempSighting = new Sighting();
        tempSighting.setElementName(element.getPrimaryName());
        List<Sighting> sightings = app.getDBI().list(tempSighting);
        lblNumberOfSightings.setText(Integer.toString(sightings.size()));
        Set<String> numOfLocations = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        Date firstDate = null;
        Date lastDate = null;
        String firstLocation = "Unknown";
        String lastLocation = "Unknown";
        int verygood = 0;
        int good = 0;
        int normal = 0;
        int bad = 0;
        int verybad = 0;
        int remotecamera = 0;
        int dayvisit = 0;
        int vacation = 0;
        int atlas = 0;
        int other = 0;

        Set<String> visits = new HashSet<String>();

        // Add Charts
        if (chartTime != null)
            pnlScrollPane.remove(chartTime);
        chartTime = new BarChart(580, 515);
        for (Sighting sighting : sightings) {
            visits.add(sighting.getVisitName());
            // Locations
            numOfLocations.add(sighting.getLocationName());
            // Dates
            if (sighting.getDate() != null) {
                if (firstDate == null) {
                    firstDate = sighting.getDate();
                    firstLocation = sighting.getLocationName();
                }
                else
                if (sighting.getDate().before(firstDate)) {
                    firstDate = sighting.getDate();
                    firstLocation = sighting.getLocationName();
                }
            }
            if (sighting.getDate() != null) {
                if (lastDate == null) {
                    lastDate = sighting.getDate();
                    lastLocation = sighting.getLocationName();
                }
                else
                if (sighting.getDate().after(lastDate)) {
                    lastDate = sighting.getDate();
                    lastLocation = sighting.getLocationName();
                }
            }
            // Time
            if (sighting.getTimeOfDay() != null) {
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.EARLY_MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblDawn.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblMorning.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MIDDAY) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblMidDay.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblAfternoon.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.LATE_AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblDusk.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1,  lblOther.getForeground()));
                }
            }
            else
                chartTime.addBar(new BarChartEntity(sighting.getLocationName(), ActiveTimeSpesific.NONE.name(), 1, lblOther.getForeground()));
            // Rating
            if (sighting.getViewRating().equals(ViewRating.VERY_GOOD))
                verygood++;
            else
            if (sighting.getViewRating().equals(ViewRating.GOOD))
                good++;
            else
            if (sighting.getViewRating().equals(ViewRating.NORMAL))
                normal++;
            else
            if (sighting.getViewRating().equals(ViewRating.BAD))
                bad++;
            else
            if (sighting.getViewRating().equals(ViewRating.VERY_BAD))
                verybad++;
        }

        pnlScrollPane.add(chartTime);
        chartTime.paintComponent(pnlScrollPane.getGraphics());
        pnlScrollPane.setPreferredSize(new Dimension(580, chartTime.getChartHeight()));

        lblTotalVisits.setText(Integer.toString(visits.size()));

        // Wrap up report fields
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        lblNumberOfLocations.setText(Integer.toString(numOfLocations.size()));
        if (firstDate != null)
            lblFirstDate.setText(new SimpleDateFormat("dd MMM yyyy").format(firstDate));
        if (lastDate != null)
            lblLastDate.setText(new SimpleDateFormat("dd MMM yyyy").format(lastDate));
        lblFirstLocation.setText(firstLocation);
        lblLastLocation.setText(lastLocation);
        lblVeryGood.setText(Integer.toString(verygood));
        lblGood.setText(Integer.toString(good));
        lblNormal.setText(Integer.toString(normal));
        lblBad.setText(Integer.toString(bad));
        lblVeryBad.setText(Integer.toString(verybad));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblAddFrequency;
    private javax.swing.JLabel lblAfternoon;
    private javax.swing.JLabel lblBad;
    private javax.swing.JLabel lblDawn;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblDusk;
    private javax.swing.JLabel lblFirstDate;
    private javax.swing.JLabel lblFirstLocation;
    private javax.swing.JLabel lblGood;
    private javax.swing.JLabel lblLastDate;
    private javax.swing.JLabel lblLastLocation;
    private javax.swing.JLabel lblMidDay;
    private javax.swing.JLabel lblMorning;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNight;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNormal;
    private javax.swing.JLabel lblNumberOfLocations;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblTotalVisits;
    private javax.swing.JLabel lblVeryBad;
    private javax.swing.JLabel lblVeryGood;
    private javax.swing.JLabel lblWishList;
    private javax.swing.JMenu mnuExtra;
    private javax.swing.JMenuItem mnuName;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintReport;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
