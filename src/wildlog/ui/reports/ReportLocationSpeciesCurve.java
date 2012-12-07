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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.chart.BarChartEntity;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class ReportLocationSpeciesCurve extends JFrame {
    private boolean usePrimaryName = true;
    private Location location;
    private BarChart chartSpecies;
    private List<Visit> visits;
    private WildLogApp app;


    /** Creates new form ReportLocation */
    public ReportLocationSpeciesCurve(Location inLocation, WildLogApp inApp) {
        app = inApp;
        location = inLocation;

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
        scrReport = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintReport = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Species Curve Report: " + location.getName());
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportLocationSpeciesCurve.class);
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

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(resourceMap.getColor("pnlScrollPane.background")); // NOI18N
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 600, 630));

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
        class ReportData implements Comparable<ReportData> {
            public Date dateAsDay;
            public int creatureCount;
            public String name;

            public ReportData(Date inDateAsDay, int inCreatureCount, String inName) {
                dateAsDay = inDateAsDay;
                creatureCount = inCreatureCount;
                name = inName;
            }

            @Override
            public int compareTo(ReportData inReportData) {
                return this.dateAsDay.compareTo(inReportData.dateAsDay);
            }
        }

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
        if (chartSpecies != null)
            pnlScrollPane.remove(chartSpecies);
        chartSpecies = new BarChart(580, 630);
        // Get a sorted list of all visits with dates
        List<Visit> sortedVisits = new ArrayList<Visit>(visits.size());
        for (Visit visit : visits) {
            if (visit.getStartDate() != null && visit.getEndDate() != null) {
                if (visit.getStartDate().before(visit.getEndDate()) || visit.getStartDate().equals(visit.getEndDate())) {
                    if (sortedVisits.isEmpty()) {
                        sortedVisits.add(visit);
                    }
                    else {
                        boolean added = false;
                        for (int i = 0; i < sortedVisits.size(); i++) {
                            if (visit.getStartDate().before(sortedVisits.get(i).getStartDate())) {
                                sortedVisits.add(i, visit);
                                added = true;
                                break;
                            }
                        }
                        if (added == false) {
                            sortedVisits.add(visit);
                        }
                    }
                    long diff = visit.getEndDate().getTime() - visit.getStartDate().getTime();
                    activeDays = activeDays + (int)Math.ceil((double)diff/60/60/24/1000) + 1;
                }
            }
        }
        if (!sortedVisits.isEmpty()) {
            firstDate = sortedVisits.get(0).getStartDate();
            lastDate = sortedVisits.get(sortedVisits.size()-1).getEndDate();

            List<ReportData> tempData = new ArrayList<ReportData>();
            for (Visit visit : visits) {
                Sighting tempSighting = new Sighting();
                tempSighting.setVisitName(visit.getName());
                List<Sighting> sightings = app.getDBI().list(tempSighting);
                Collections.sort(sightings);
                for (Sighting sighting : sightings) {
                    numOfSightings++;
                    numOfElements.add(sighting.getElementName());
                    //if (!numOfElements.contains(sighting.getElement().getPrimaryName()))
                    //    chartSpecies.addBar(new BarChartEntity(sighting.getElement().getPrimaryName() + "-" + sighting.getDate(), visit.getName(), (numOfElements.size()), Color.yellow));
                    // Get a Date object containing only the day part, not the time.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sighting.getDate());
                    Calendar dayCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    tempData.add(new ReportData(
                            dayCalendar.getTime(),
                            numOfElements.size(),
                            sighting.getElementName()));

                    if (sighting.getTimeOfDay() != null) {
                        if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                            numNightSightings++;
                        }
                        else
                        if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                            // Do nothing
                        }
                        else {
                            numDaySightings++;
                        }
                    }
                }
            }

            List<ReportData> tempDataCore = new ArrayList<ReportData>(numOfElements.size());
            Collections.sort(tempData);
            Set<String> tempSet = new HashSet<String>(numOfElements.size());
            for (ReportData temp : tempData) {
                if (!tempSet.contains(temp.name)) {
                    tempSet.add(temp.name);
                    tempDataCore.add(new ReportData(temp.dateAsDay, tempSet.size(), ""));
                }
            }

            final double ROWS = 25.0;
            int interval = 1;
            int days = (int)((lastDate.getTime() - firstDate.getTime())/60/60/24/1000);
            if (days > ROWS)
                interval = (int)Math.ceil(days/ROWS);
            int count = 0;
            Map<Date, Integer> finalChartData = new HashMap<Date, Integer>((int)ROWS);
            int maxCreatures = 0;
            for (int t = 0; t <= days; t++) {
                for (ReportData data : tempDataCore) {
                    if (data.dateAsDay.equals(new Date(firstDate.getTime() + (long)t*60*60*24*1000))) {
                        maxCreatures = data.creatureCount;
                    }
                }
                if (count >= interval) {
                    finalChartData.put(new Date(firstDate.getTime() + (long)t*60*60*24*1000), maxCreatures);
                    count = 0;
                }
                count++;
            }
            if (count < interval && count > 0) {
                finalChartData.put(lastDate, maxCreatures);
            }

            for (Date temp : finalChartData.keySet()) {
                chartSpecies.addBar(new BarChartEntity(temp, "", finalChartData.get(temp), new Color(125, 198, 48)));
            }

        }
        else {
            firstDate = null;
            lastDate = null;
        }

        pnlScrollPane.add(chartSpecies);
        chartSpecies.paintComponent(pnlScrollPane.getGraphics());
        pnlScrollPane.setPreferredSize(new Dimension(580, chartSpecies.getChartHeight()));

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
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintReport;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
