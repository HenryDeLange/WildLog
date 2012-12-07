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
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.helpers.MoonReportHelper;
import wildlog.ui.dialogs.utils.UtilsDialog;


// FIXME: Inheritance might be used to specialise the reports... or maybe better to just have unique ones...
public class ReportLocationSightingsByMoon extends JFrame {
    private boolean usePrimaryName = true;
    private Location location;
    private BarChart chartTime;
    private List<Visit> visits;
    private WildLogApp app;


    /** Creates new form ReportLocationSightingsByMoon */
    public ReportLocationSightingsByMoon(Location inLocation, WildLogApp inApp) {
        app = inApp;
        location = inLocation;

        initComponents();

        // FIXME: Hierdie escape key en dialogtocenter code herhaal baie, maak dalk een util method wat altwee doen...
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

        Visit tempVisit = new Visit();
        tempVisit.setLocationName(location.getName());
        visits = app.getDBI().list(tempVisit);
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
        lblNumberOfVisits = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblNumberOfElements = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFirstVisit = new javax.swing.JLabel();
        lblLastVisit = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblActiveDays = new javax.swing.JLabel();
        lblLegend = new javax.swing.JLabel();
        scrReport = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        lblMoonlightDay = new javax.swing.JLabel();
        lblNoMoonDay = new javax.swing.JLabel();
        lblMoonlightNight = new javax.swing.JLabel();
        lblNoMoonNight = new javax.swing.JLabel();
        lblOther = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintReport = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Moonphase Report: " + location.getName());
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportLocationSightingsByMoon.class);
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

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblNumberOfVisits.setText(resourceMap.getString("lblNumberOfVisits.text")); // NOI18N
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N
        getContentPane().add(lblNumberOfVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 90, -1));

        jLabel3.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 90, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, -1, -1));

        lblNumberOfElements.setText(resourceMap.getString("lblNumberOfElements.text")); // NOI18N
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        getContentPane().add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 30, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(158, 50, 50, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 50, 60, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        lblFirstVisit.setText(resourceMap.getString("lblFirstVisit.text")); // NOI18N
        lblFirstVisit.setName("lblFirstVisit"); // NOI18N
        getContentPane().add(lblFirstVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 90, -1));

        lblLastVisit.setText(resourceMap.getString("lblLastVisit.text")); // NOI18N
        lblLastVisit.setName("lblLastVisit"); // NOI18N
        getContentPane().add(lblLastVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 120, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 50, -1, -1));

        lblActiveDays.setText(resourceMap.getString("lblActiveDays.text")); // NOI18N
        lblActiveDays.setName("lblActiveDays"); // NOI18N
        getContentPane().add(lblActiveDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 50, 90, -1));

        lblLegend.setText(resourceMap.getString("lblLegend.text")); // NOI18N
        lblLegend.setName("lblLegend"); // NOI18N
        getContentPane().add(lblLegend, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 725, -1, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(resourceMap.getColor("pnlScrollPane.background")); // NOI18N
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 600, 630));

        lblMoonlightDay.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblMoonlightDay.setForeground(resourceMap.getColor("lblMoonlightDay.foreground")); // NOI18N
        lblMoonlightDay.setText(resourceMap.getString("lblMoonlightDay.text")); // NOI18N
        lblMoonlightDay.setName("lblMoonlightDay"); // NOI18N
        getContentPane().add(lblMoonlightDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 725, 140, -1));

        lblNoMoonDay.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblNoMoonDay.setForeground(resourceMap.getColor("lblNoMoonDay.foreground")); // NOI18N
        lblNoMoonDay.setText(resourceMap.getString("lblNoMoonDay.text")); // NOI18N
        lblNoMoonDay.setName("lblNoMoonDay"); // NOI18N
        getContentPane().add(lblNoMoonDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 725, 100, -1));

        lblMoonlightNight.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblMoonlightNight.setForeground(resourceMap.getColor("lblMoonlightNight.foreground")); // NOI18N
        lblMoonlightNight.setText(resourceMap.getString("lblMoonlightNight.text")); // NOI18N
        lblMoonlightNight.setName("lblMoonlightNight"); // NOI18N
        getContentPane().add(lblMoonlightNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 725, 130, -1));

        lblNoMoonNight.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblNoMoonNight.setForeground(resourceMap.getColor("lblNoMoonNight.foreground")); // NOI18N
        lblNoMoonNight.setText(resourceMap.getString("lblNoMoonNight.text")); // NOI18N
        lblNoMoonNight.setName("lblNoMoonNight"); // NOI18N
        getContentPane().add(lblNoMoonNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 725, 120, -1));

        lblOther.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblOther.setForeground(resourceMap.getColor("lblOther.foreground")); // NOI18N
        lblOther.setText(resourceMap.getString("lblOther.text")); // NOI18N
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 725, 50, -1));

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

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuPrintReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintReportActionPerformed
        try {
            final JFrame frame = this;
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("WildLog Location Report");
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

    private void doReport() {
        // Init report fields
        lblName.setText(location.getName());
        lblNumberOfVisits.setText(Integer.toString(visits.size()));
        int numOfSightings = 0;
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        Date firstDate = null;
        Date lastDate = null;
        int activeDays = 0;

        // Add Charts
        if (chartTime != null)
            pnlScrollPane.remove(chartTime);
        chartTime = new BarChart(580, 630);
        for (Visit visit : visits) {
            if (visit.getStartDate() != null) {
                if (firstDate == null)
                    firstDate = visit.getStartDate();
                else
                if (visit.getStartDate().before(firstDate))
                    firstDate = visit.getStartDate();
            }
            if (visit.getEndDate() != null) {
                if (lastDate == null)
                    lastDate = visit.getEndDate();
                else
                if (visit.getEndDate().after(lastDate))
                    lastDate = visit.getEndDate();
            }
            if (visit.getStartDate() != null && visit.getEndDate() != null) {
                if (visit.getStartDate().before(visit.getEndDate()) || visit.getStartDate().equals(visit.getEndDate())) {
                    long diff = visit.getEndDate().getTime() - visit.getStartDate().getTime();
                    activeDays = activeDays + (int)Math.ceil((double)diff/60/60/24/1000) + 1;
                }
            }
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(visit.getName());
            List<Sighting> sightings = app.getDBI().list(tempSighting);
            for (Sighting sighting : sightings) {
                numOfSightings++;
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
            }
        }

        pnlScrollPane.add(chartTime);
        chartTime.paintComponent(pnlScrollPane.getGraphics());
//        pnlScrollPane.setPreferredSize(new Dimension(chartTime.getChartWidth(), chartTime.getChartHeight()));
        pnlScrollPane.setPreferredSize(new Dimension(580, chartTime.getChartHeight()));

        // Wrap up report fields
        lblNumberOfSightings.setText(Integer.toString(numOfSightings));
        lblNumberOfElements.setText(Integer.toString(numOfElements.size()));
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        if (firstDate != null)
            lblFirstVisit.setText(new SimpleDateFormat("dd MMM yyyy").format(firstDate));
        else
            lblFirstVisit.setText("Unknown");
        if (lastDate != null)
            lblLastVisit.setText(new SimpleDateFormat("dd MMM yyyy").format(lastDate));
        else
            lblLastVisit.setText("Unknown");
        lblActiveDays.setText(Integer.toString(activeDays));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblActiveDays;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblFirstVisit;
    private javax.swing.JLabel lblLastVisit;
    private javax.swing.JLabel lblLegend;
    private javax.swing.JLabel lblMoonlightDay;
    private javax.swing.JLabel lblMoonlightNight;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNoMoonDay;
    private javax.swing.JLabel lblNoMoonNight;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JLabel lblOther;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintReport;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
