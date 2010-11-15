package wildlog.ui.report;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import wildlog.WildLogApp;
import wildlog.WildLogView;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.report.chart.BarChart;
import wildlog.ui.report.chart.BarChartEntity;

/**
 *
 * @author Henry
 */
public class ReportSighting extends javax.swing.JFrame {
    private boolean usePrimaryName = true;
    private boolean viewReport1 = true;
    private boolean viewReport2 = false;
    private Date startDate;
    private Date endDate;
    private BarChart chartTime;
    private WildLogApp app;


    /** Creates new form ReportSighting */
    public ReportSighting(Date inStartDate, Date inEndDate, WildLogApp inApp) {
        startDate = inStartDate;
        endDate = inEndDate;
        app = inApp;
        
        initComponents();

        doReport1();
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
        jLabel3 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblNumberOfElements = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
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
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuReports = new javax.swing.JMenu();
        mnuSightingsByElements = new javax.swing.JMenuItem();
        mnuSightingsByLocations = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sighting Report: " + new SimpleDateFormat("dd MMM yyyy").format(startDate) + " to " + new SimpleDateFormat("dd MMM yyyy").format(endDate));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportSighting.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setForeground(resourceMap.getColor("Form.foreground")); // NOI18N
        setMinimumSize(new java.awt.Dimension(550, 750));
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblName.setFont(resourceMap.getFont("lblName.font")); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        getContentPane().add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 20));

        jLabel3.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, -1, -1));

        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 30, 100, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 50, -1, -1));

        lblNumberOfElements.setText(resourceMap.getString("lblNumberOfElements.text")); // NOI18N
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        getContentPane().add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 100, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 130, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 130, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 705, -1, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(resourceMap.getColor("pnlScrollPane.background")); // NOI18N
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 600, 630));

        lblNight.setFont(resourceMap.getFont("lblNight.font")); // NOI18N
        lblNight.setForeground(resourceMap.getColor("lblNight.foreground")); // NOI18N
        lblNight.setText(resourceMap.getString("lblNight.text")); // NOI18N
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 705, 60, -1));

        lblDawn.setFont(resourceMap.getFont("lblDawn.font")); // NOI18N
        lblDawn.setForeground(resourceMap.getColor("lblDawn.foreground")); // NOI18N
        lblDawn.setText(resourceMap.getString("lblDawn.text")); // NOI18N
        lblDawn.setName("lblDawn"); // NOI18N
        getContentPane().add(lblDawn, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 705, 60, -1));

        lblMorning.setFont(resourceMap.getFont("lblMorning.font")); // NOI18N
        lblMorning.setForeground(resourceMap.getColor("lblMorning.foreground")); // NOI18N
        lblMorning.setText(resourceMap.getString("lblMorning.text")); // NOI18N
        lblMorning.setName("lblMorning"); // NOI18N
        getContentPane().add(lblMorning, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 705, 80, -1));

        lblMidDay.setFont(resourceMap.getFont("lblMidDay.font")); // NOI18N
        lblMidDay.setForeground(resourceMap.getColor("lblMidDay.foreground")); // NOI18N
        lblMidDay.setText(resourceMap.getString("lblMidDay.text")); // NOI18N
        lblMidDay.setName("lblMidDay"); // NOI18N
        getContentPane().add(lblMidDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 705, 70, -1));

        lblAfternoon.setFont(resourceMap.getFont("lblAfternoon.font")); // NOI18N
        lblAfternoon.setForeground(resourceMap.getColor("lblAfternoon.foreground")); // NOI18N
        lblAfternoon.setText(resourceMap.getString("lblAfternoon.text")); // NOI18N
        lblAfternoon.setName("lblAfternoon"); // NOI18N
        getContentPane().add(lblAfternoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 705, 90, -1));

        lblDusk.setFont(resourceMap.getFont("lblDusk.font")); // NOI18N
        lblDusk.setForeground(resourceMap.getColor("lblDusk.foreground")); // NOI18N
        lblDusk.setText(resourceMap.getString("lblDusk.text")); // NOI18N
        lblDusk.setName("lblDusk"); // NOI18N
        getContentPane().add(lblDusk, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 705, 50, -1));

        lblOther.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblOther.setForeground(resourceMap.getColor("lblOther.foreground")); // NOI18N
        lblOther.setText(resourceMap.getString("lblOther.text")); // NOI18N
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 705, 60, -1));

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        mnuPrint.setText(resourceMap.getString("mnuPrint.text")); // NOI18N
        mnuPrint.setName("mnuPrint"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuPrint.add(jMenuItem1);

        jMenuBar1.add(mnuPrint);

        mnuReports.setText(resourceMap.getString("mnuReports.text")); // NOI18N
        mnuReports.setName("mnuReports"); // NOI18N

        mnuSightingsByElements.setText(resourceMap.getString("mnuSightingsByElements.text")); // NOI18N
        mnuSightingsByElements.setName("mnuSightingsByElements"); // NOI18N
        mnuSightingsByElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSightingsByElementsActionPerformed(evt);
            }
        });
        mnuReports.add(mnuSightingsByElements);

        mnuSightingsByLocations.setText(resourceMap.getString("mnuSightingsByLocations.text")); // NOI18N
        mnuSightingsByLocations.setName("mnuSightingsByLocations"); // NOI18N
        mnuSightingsByLocations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSightingsByLocationsActionPerformed(evt);
            }
        });
        mnuReports.add(mnuSightingsByLocations);

        jMenuBar1.add(mnuReports);

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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            final JFrame frame = this;
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("WildLog Sighting Report");
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
            Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mnuNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNameActionPerformed
        usePrimaryName = ! usePrimaryName;
        if (viewReport1) {
            doReport1();
            // Re-Draw
            repaint();
            setVisible(true);
        }
        else
        if (viewReport2) {
            doReport2();
            // Re-Draw
            repaint();
            setVisible(true);
        }
    }//GEN-LAST:event_mnuNameActionPerformed

    private void mnuSightingsByElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSightingsByElementsActionPerformed
        viewReport1 = true;
        viewReport2 = false;
        usePrimaryName = true;
        doReport1();
        // Re-Draw
        repaint();
        setVisible(true);
    }//GEN-LAST:event_mnuSightingsByElementsActionPerformed

    private void mnuSightingsByLocationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSightingsByLocationsActionPerformed
        viewReport1 = false;
        viewReport2 = true;
        usePrimaryName = true;
        doReport2();
        // Re-Draw
        repaint();
        setVisible(true);
    }//GEN-LAST:event_mnuSightingsByLocationsActionPerformed

    private void doReport1() {
        // Init report fields
        lblName.setText("Sightings for " + new SimpleDateFormat("dd MMM yyyy").format(startDate) + " to " + new SimpleDateFormat("dd MMM yyyy").format(endDate));
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;

        // Add Charts
        if (chartTime != null)
            pnlScrollPane.remove(chartTime);
        chartTime = new BarChart(580, 630);
        List<Sighting> sightings = app.getDBI().searchSightingOnDate(startDate, endDate);
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
        }

        pnlScrollPane.add(chartTime);
        chartTime.paintComponent(pnlScrollPane.getGraphics());
        pnlScrollPane.setPreferredSize(new Dimension(580, chartTime.getChartHeight()));

        // Wrap up report fields
        lblNumberOfSightings.setText(Integer.toString(sightings.size()));
        lblNumberOfElements.setText(Integer.toString(numOfElements.size()));
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
    }

    private void doReport2() {
        // Init report fields
        lblName.setText("Sightings for " + new SimpleDateFormat("dd MMM yyyy").format(startDate) + " to " + new SimpleDateFormat("dd MMM yyyy").format(endDate));
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;

        // Add Charts
        if (chartTime != null)
            pnlScrollPane.remove(chartTime);
        chartTime = new BarChart(580, 630);
        List<Sighting> sightings = app.getDBI().searchSightingOnDate(startDate, endDate);
        for (Sighting sighting : sightings) {
            numOfElements.add(sighting.getElementName());
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
        }

        pnlScrollPane.add(chartTime);
        chartTime.paintComponent(pnlScrollPane.getGraphics());
        pnlScrollPane.setPreferredSize(new Dimension(580, chartTime.getChartHeight()));

        // Wrap up report fields
        lblNumberOfSightings.setText(Integer.toString(sightings.size()));
        lblNumberOfElements.setText(Integer.toString(numOfElements.size()));
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lblAfternoon;
    private javax.swing.JLabel lblDawn;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblDusk;
    private javax.swing.JLabel lblMidDay;
    private javax.swing.JLabel lblMorning;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNight;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblOther;
    private javax.swing.JMenu mnuExtra;
    private javax.swing.JMenuItem mnuName;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenu mnuReports;
    private javax.swing.JMenuItem mnuSightingsByElements;
    private javax.swing.JMenuItem mnuSightingsByLocations;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
