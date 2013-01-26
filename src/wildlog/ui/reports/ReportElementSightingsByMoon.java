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
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.helpers.MoonReportHelper;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class ReportElementSightingsByMoon extends javax.swing.JFrame {
    private boolean usePrimaryName = true;
    private Element element;
    private BarChart chartTime;
    private WildLogApp app;

    /** Creates new form ReportElement */
    public ReportElementSightingsByMoon(Element inElement, WildLogApp inApp) {
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
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblAddFrequency = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFirstDate = new javax.swing.JLabel();
        lblLastDate = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
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
        lblOther = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblFirstLocation = new javax.swing.JLabel();
        lblLastLocation = new javax.swing.JLabel();
        scrReport = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalVisits = new javax.swing.JLabel();
        lblMoonlightDay = new javax.swing.JLabel();
        lblNoMoonDay = new javax.swing.JLabel();
        lblMoonlightNight = new javax.swing.JLabel();
        lblNoMoonNight = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintReport = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Creature Report: " + element.getPrimaryName());
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportElementSightingsByMoon.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setForeground(resourceMap.getColor("Form.foreground")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(550, 750));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblName.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        getContentPane().add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 20));

        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 70, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, -1, -1));

        lblAddFrequency.setText(resourceMap.getString("lblAddFrequency.text")); // NOI18N
        lblAddFrequency.setName("lblAddFrequency"); // NOI18N
        getContentPane().add(lblAddFrequency, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 70, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 70, -1, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblFirstDate.setText(resourceMap.getString("lblFirstDate.text")); // NOI18N
        lblFirstDate.setName("lblFirstDate"); // NOI18N
        getContentPane().add(lblFirstDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 30, 100, -1));

        lblLastDate.setText(resourceMap.getString("lblLastDate.text")); // NOI18N
        lblLastDate.setName("lblLastDate"); // NOI18N
        getContentPane().add(lblLastDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 50, 100, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 705, -1, -1));

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        lblWishList.setText(resourceMap.getString("lblWishList.text")); // NOI18N
        lblWishList.setName("lblWishList"); // NOI18N
        getContentPane().add(lblWishList, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 290, -1));

        lblNumberOfLocations.setText(resourceMap.getString("lblNumberOfLocations.text")); // NOI18N
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        getContentPane().add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 50, -1));

        jLabel23.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        lblVeryGood.setText(resourceMap.getString("lblVeryGood.text")); // NOI18N
        lblVeryGood.setName("lblVeryGood"); // NOI18N
        getContentPane().add(lblVeryGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 50, -1));

        jLabel25.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        lblGood.setText(resourceMap.getString("lblGood.text")); // NOI18N
        lblGood.setName("lblGood"); // NOI18N
        getContentPane().add(lblGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 60, -1));

        jLabel27.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, -1, -1));

        lblNormal.setText(resourceMap.getString("lblNormal.text")); // NOI18N
        lblNormal.setName("lblNormal"); // NOI18N
        getContentPane().add(lblNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 130, 50, -1));

        jLabel29.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, -1, -1));

        lblBad.setText(resourceMap.getString("lblBad.text")); // NOI18N
        lblBad.setName("lblBad"); // NOI18N
        getContentPane().add(lblBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 70, -1));

        jLabel31.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 110, -1, -1));

        lblVeryBad.setText(resourceMap.getString("lblVeryBad.text")); // NOI18N
        lblVeryBad.setName("lblVeryBad"); // NOI18N
        getContentPane().add(lblVeryBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 110, 70, -1));

        lblOther.setFont(resourceMap.getFont("lblMoonlightNight.font")); // NOI18N
        lblOther.setForeground(resourceMap.getColor("lblOther.foreground")); // NOI18N
        lblOther.setText(resourceMap.getString("lblOther.text")); // NOI18N
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 705, 50, -1));

        jLabel4.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, -1, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, 50, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 50, -1));

        jLabel11.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, -1, -1));

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, -1, -1));

        lblFirstLocation.setText(resourceMap.getString("lblFirstLocation.text")); // NOI18N
        lblFirstLocation.setName("lblFirstLocation"); // NOI18N
        getContentPane().add(lblFirstLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(284, 30, 230, -1));

        lblLastLocation.setText(resourceMap.getString("lblLastLocation.text")); // NOI18N
        lblLastLocation.setName("lblLastLocation"); // NOI18N
        getContentPane().add(lblLastLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(284, 50, 230, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(resourceMap.getColor("pnlScrollPane.background")); // NOI18N
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 600, 550));

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 70, -1, -1));

        lblTotalVisits.setText(resourceMap.getString("lblTotalVisits.text")); // NOI18N
        lblTotalVisits.setName("lblTotalVisits"); // NOI18N
        getContentPane().add(lblTotalVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 50, -1));

        lblMoonlightDay.setFont(resourceMap.getFont("lblMoonlightNight.font")); // NOI18N
        lblMoonlightDay.setForeground(resourceMap.getColor("lblMoonlightDay.foreground")); // NOI18N
        lblMoonlightDay.setText(resourceMap.getString("lblMoonlightDay.text")); // NOI18N
        lblMoonlightDay.setName("lblMoonlightDay"); // NOI18N
        getContentPane().add(lblMoonlightDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 705, 140, -1));

        lblNoMoonDay.setFont(resourceMap.getFont("lblMoonlightNight.font")); // NOI18N
        lblNoMoonDay.setForeground(resourceMap.getColor("lblNoMoonDay.foreground")); // NOI18N
        lblNoMoonDay.setText(resourceMap.getString("lblNoMoonDay.text")); // NOI18N
        lblNoMoonDay.setName("lblNoMoonDay"); // NOI18N
        getContentPane().add(lblNoMoonDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 705, 100, -1));

        lblMoonlightNight.setFont(resourceMap.getFont("lblMoonlightNight.font")); // NOI18N
        lblMoonlightNight.setForeground(resourceMap.getColor("lblMoonlightNight.foreground")); // NOI18N
        lblMoonlightNight.setText(resourceMap.getString("lblMoonlightNight.text")); // NOI18N
        lblMoonlightNight.setName("lblMoonlightNight"); // NOI18N
        getContentPane().add(lblMoonlightNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 705, 130, -1));

        lblNoMoonNight.setFont(resourceMap.getFont("lblMoonlightNight.font")); // NOI18N
        lblNoMoonNight.setForeground(resourceMap.getColor("lblNoMoonNight.foreground")); // NOI18N
        lblNoMoonNight.setText(resourceMap.getString("lblNoMoonNight.text")); // NOI18N
        lblNoMoonNight.setName("lblNoMoonNight"); // NOI18N
        getContentPane().add(lblNoMoonNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 705, 120, -1));

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        mnuPrint.setText(resourceMap.getString("mnuPrint.text")); // NOI18N
        mnuPrint.setName("mnuPrint"); // NOI18N

        mnuPrintReport.setText(resourceMap.getString("mnuPrintReport.text")); // NOI18N
        mnuPrintReport.setName("mnuPrintReport"); // NOI18N
        mnuPrintReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintReportActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintReport);

        jMenuBar1.add(mnuPrint);

        mnuExtra.setText(resourceMap.getString("mnuExtra.text")); // NOI18N
        mnuExtra.setName("mnuExtra"); // NOI18N

        mnuName.setText(resourceMap.getString("mnuName.text")); // NOI18N
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
                    // Night
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    // Do nothing
                }
                else {
                    // Day
                    numDaySightings++;
                }
            }
            // Moon
            MoonReportHelper.addMoonInfoToChart(chartTime, sighting, new JLabel[]{lblMoonlightNight, lblNoMoonNight, lblMoonlightDay, lblNoMoonDay, lblOther});
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
    private javax.swing.JLabel lblBad;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblFirstDate;
    private javax.swing.JLabel lblFirstLocation;
    private javax.swing.JLabel lblGood;
    private javax.swing.JLabel lblLastDate;
    private javax.swing.JLabel lblLastLocation;
    private javax.swing.JLabel lblMoonlightDay;
    private javax.swing.JLabel lblMoonlightNight;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNoMoonDay;
    private javax.swing.JLabel lblNoMoonNight;
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
