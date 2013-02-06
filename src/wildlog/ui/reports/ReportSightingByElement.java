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
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.chart.BarChartEntity;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class ReportSightingByElement extends javax.swing.JFrame {
    private boolean usePrimaryName = true;
    private Date startDate;
    private Date endDate;
    private BarChart chartTime;
    private WildLogApp app;


    /** Creates new form ReportSightingByElement */
    public ReportSightingByElement(Date inStartDate, Date inEndDate, WildLogApp inApp) {
        startDate = inStartDate;
        endDate = inEndDate;
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
        mnuPrintReport = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sighting Report: " + new SimpleDateFormat("dd MMM yyyy").format(startDate) + " to " + new SimpleDateFormat("dd MMM yyyy").format(endDate));
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportSightingByElement.class);
        jLabel3.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel3.setText("Number of Observations:");
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, -1, -1));

        lblNumberOfSightings.setText("Unknown");
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 100, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText("Number of Creatures:");
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        lblNumberOfElements.setText("Unknown");
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        getContentPane().add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 100, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText("Day Observations:");
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText("Unknown");
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 130, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText("Night Observations:");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblNightSightings.setText("Unknown");
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 130, -1));

        jLabel10.setText("Legend:");
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 705, -1, -1));

        scrReport.setBorder(null);
        scrReport.setName("scrReport"); // NOI18N

        pnlScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        pnlScrollPane.setName("pnlScrollPane"); // NOI18N
        pnlScrollPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        scrReport.setViewportView(pnlScrollPane);

        getContentPane().add(scrReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 600, 630));

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
            pj.setJobName("WildLog Observation Report");
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
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
    private javax.swing.JMenuItem mnuPrintReport;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JScrollPane scrReport;
    // End of variables declaration//GEN-END:variables

}
