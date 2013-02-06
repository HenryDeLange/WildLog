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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.chart.BarChartEntity;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class ReportVisitSightingsBySun extends javax.swing.JFrame {
    private boolean usePrimaryName = true;
    private Visit visit;
    private BarChart chartTime;
    private WildLogApp app;

    /** Creates new form ReportVisitSightingsBySun */
    public ReportVisitSightingsBySun(Visit inVisit, WildLogApp inApp) {
        app = inApp;
        visit = inVisit;

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
        jLabel1 = new javax.swing.JLabel();
        lblCreaturesPerDay = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblVisitType = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblStartDate = new javax.swing.JLabel();
        lblEndDate = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblActiveDays = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblGameWatching = new javax.swing.JLabel();
        lblSightingsPerDay = new javax.swing.JLabel();
        lblNumberOfCreatures = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
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
        scrReport = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        lblNight = new javax.swing.JLabel();
        lblDawn = new javax.swing.JLabel();
        lblMorning = new javax.swing.JLabel();
        lblMidDay = new javax.swing.JLabel();
        lblAfternoon = new javax.swing.JLabel();
        lblDusk = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintReport = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Period Report: " + visit.getName());
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(550, 750));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("...");
        lblName.setName("lblName"); // NOI18N
        getContentPane().add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("New Creatures per Day:");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        lblCreaturesPerDay.setText("Unknown");
        lblCreaturesPerDay.setName("lblCreaturesPerDay"); // NOI18N
        getContentPane().add(lblCreaturesPerDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, 60, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Observations per Day:");
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        lblNumberOfSightings.setText("Unknown");
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 40, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Period Type:");
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, -1, -1));

        lblVisitType.setText("Unknown");
        lblVisitType.setName("lblVisitType"); // NOI18N
        getContentPane().add(lblVisitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(502, 30, 90, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Number of Observations:");
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText("Unknown");
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 50, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Number of Creatures:");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        lblNightSightings.setText("Unknown");
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 80, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Start Date:");
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("End Date:");
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        lblStartDate.setText("Unknown");
        lblStartDate.setName("lblStartDate"); // NOI18N
        getContentPane().add(lblStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(78, 30, 120, -1));

        lblEndDate.setText("Unknown");
        lblEndDate.setName("lblEndDate"); // NOI18N
        getContentPane().add(lblEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 140, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Number of Days:");
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 70, -1, -1));

        lblActiveDays.setText("Unknown");
        lblActiveDays.setName("lblActiveDays"); // NOI18N
        getContentPane().add(lblActiveDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(517, 70, 80, -1));

        jLabel10.setText("Legend:");
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 705, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Game Watching:");
        jLabel19.setName("jLabel19"); // NOI18N
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, -1, -1));

        lblGameWatching.setText("Unknown");
        lblGameWatching.setName("lblGameWatching"); // NOI18N
        getContentPane().add(lblGameWatching, new org.netbeans.lib.awtextra.AbsoluteConstraints(517, 50, 80, -1));

        lblSightingsPerDay.setText("Unknown");
        lblSightingsPerDay.setName("lblSightingsPerDay"); // NOI18N
        getContentPane().add(lblSightingsPerDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 60, -1));

        lblNumberOfCreatures.setText("Unknown");
        lblNumberOfCreatures.setName("lblNumberOfCreatures"); // NOI18N
        getContentPane().add(lblNumberOfCreatures, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 70, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Day Observations:");
        jLabel21.setName("jLabel21"); // NOI18N
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Night Observations:");
        jLabel22.setName("jLabel22"); // NOI18N
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, -1, -1));

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
        getContentPane().add(lblGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 70, -1));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("Normal Observations:");
        jLabel27.setName("jLabel27"); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, -1, -1));

        lblNormal.setText("Unknown");
        lblNormal.setName("lblNormal"); // NOI18N
        getContentPane().add(lblNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, 50, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("Bad Observations:");
        jLabel29.setName("jLabel29"); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, -1, -1));

        lblBad.setText("Unknown");
        lblBad.setName("lblBad"); // NOI18N
        getContentPane().add(lblBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 80, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Very Bad Observations:");
        jLabel31.setName("jLabel31"); // NOI18N
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, -1, -1));

        lblVeryBad.setText("Unknown");
        lblVeryBad.setName("lblVeryBad"); // NOI18N
        getContentPane().add(lblVeryBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 110, 70, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 600, 550));

        lblNight.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblNight.setForeground(new java.awt.Color(93, 93, 93));
        lblNight.setText("NIGHT");
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 705, 60, -1));

        lblDawn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDawn.setForeground(new java.awt.Color(143, 120, 64));
        lblDawn.setText("DAWN");
        lblDawn.setName("lblDawn"); // NOI18N
        getContentPane().add(lblDawn, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 705, 60, -1));

        lblMorning.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMorning.setForeground(new java.awt.Color(194, 142, 63));
        lblMorning.setText("MORNING");
        lblMorning.setName("lblMorning"); // NOI18N
        getContentPane().add(lblMorning, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 705, 80, -1));

        lblMidDay.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblMidDay.setForeground(new java.awt.Color(185, 73, 13));
        lblMidDay.setText("MID DAY");
        lblMidDay.setName("lblMidDay"); // NOI18N
        getContentPane().add(lblMidDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 705, 70, -1));

        lblAfternoon.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblAfternoon.setForeground(new java.awt.Color(170, 89, 40));
        lblAfternoon.setText("AFTERNOON");
        lblAfternoon.setName("lblAfternoon"); // NOI18N
        getContentPane().add(lblAfternoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 705, 90, -1));

        lblDusk.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDusk.setForeground(new java.awt.Color(134, 98, 75));
        lblDusk.setText("DUSK");
        lblDusk.setName("lblDusk"); // NOI18N
        getContentPane().add(lblDusk, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 705, 50, -1));

        lblOther.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblOther.setForeground(new java.awt.Color(183, 187, 199));
        lblOther.setText("OTHER");
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 705, 60, -1));

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        mnuPrint.setText("Print");
        mnuPrint.setName("mnuPrint"); // NOI18N

        mnuPrintReport.setText("Print this Report");
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
            pj.setJobName("WildLog Period Report");
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
        lblName.setText(visit.getName());
        if (visit.getType() != null)
            lblVisitType.setText(visit.getType().toString());
        else
            lblVisitType.setText("Unknown");
        if (visit.getGameWatchingIntensity() != null)
            lblGameWatching.setText(visit.getGameWatchingIntensity().toString());
        else
            lblGameWatching.setText("Unknown");
        Sighting tempSighting = new Sighting();
        tempSighting.setVisitName(visit.getName());
        List<Sighting> sightings = app.getDBI().list(tempSighting);
        lblNumberOfSightings.setText(Integer.toString(sightings.size()));
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        if (visit.getStartDate() != null)
            lblStartDate.setText(new SimpleDateFormat("dd MMM yyyy").format(visit.getStartDate()));
        else
            lblStartDate.setText("Unknown");
        if (visit.getEndDate() != null)
            lblEndDate.setText(new SimpleDateFormat("dd MMM yyyy").format(visit.getEndDate()));
        else
            lblEndDate.setText("Unknown");
        long diff = 0;
        if (visit.getStartDate() != null && visit.getEndDate() != null)
            if (visit.getStartDate().before(visit.getEndDate()) || visit.getStartDate().equals(visit.getEndDate()))
                diff = visit.getEndDate().getTime() - visit.getStartDate().getTime();
        int activeDays = (int)Math.ceil((double)diff/60/60/24/1000) + 1;
        lblActiveDays.setText(Integer.toString(activeDays));
        int verygood = 0;
        int good = 0;
        int normal = 0;
        int bad = 0;
        int verybad = 0;

        // Add Charts
        if (chartTime != null)
            pnlScrollPane.remove(chartTime);
        chartTime = new BarChart(580, 550);
        for (Sighting sighting : sightings) {
            numOfElements.add(sighting.getElementName());
            String nameToUse = "";
            if (usePrimaryName)
                nameToUse = sighting.getElementName();
            else {
                Element tempElement = app.getDBI().find(new Element(sighting.getElementName()));
                if (tempElement.getOtherName() != null)
                    nameToUse = tempElement.getOtherName();
            }
            // Time
            if (sighting.getTimeOfDay() != null) {
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.EARLY_MORNING)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblDawn.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MORNING)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblMorning.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MIDDAY) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_MORNING)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblMidDay.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblAfternoon.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.LATE_AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblDusk.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    chartTime.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblOther.getForeground()));
                }
            }
            else
                chartTime.addBar(new BarChartEntity(nameToUse, ActiveTimeSpesific.NONE.name(), 1, lblOther.getForeground()));

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

        // Wrap up report fields
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        lblNumberOfCreatures.setText(Integer.toString(numOfElements.size()));
        DecimalFormat format = new DecimalFormat("#.###");
        lblSightingsPerDay.setText(format.format((double)sightings.size()/activeDays));
        lblCreaturesPerDay.setText(format.format((double)numOfElements.size()/activeDays));
        lblVeryGood.setText(Integer.toString(verygood));
        lblGood.setText(Integer.toString(good));
        lblNormal.setText(Integer.toString(normal));
        lblBad.setText(Integer.toString(bad));
        lblVeryBad.setText(Integer.toString(verybad));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblActiveDays;
    private javax.swing.JLabel lblAfternoon;
    private javax.swing.JLabel lblBad;
    private javax.swing.JLabel lblCreaturesPerDay;
    private javax.swing.JLabel lblDawn;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblDusk;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblGameWatching;
    private javax.swing.JLabel lblGood;
    private javax.swing.JLabel lblMidDay;
    private javax.swing.JLabel lblMorning;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNight;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNormal;
    private javax.swing.JLabel lblNumberOfCreatures;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblSightingsPerDay;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JLabel lblVeryBad;
    private javax.swing.JLabel lblVeryGood;
    private javax.swing.JLabel lblVisitType;
    private javax.swing.JMenu mnuExtra;
    private javax.swing.JMenuItem mnuName;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintReport;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
