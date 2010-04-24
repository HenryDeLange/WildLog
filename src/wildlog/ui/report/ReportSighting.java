/*
 * ReportSighting.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.ui.report;

import java.awt.Color;
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
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import wildlog.WildLogApp;
import wildlog.WildLogView;
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
    private Date startDate;
    private Date endDate;
    private BarChart chartElements;
    private BarChart chartLocations;
    private WildLogApp app;


    /** Creates new form ReportSighting */
    public ReportSighting(Date inStartDate, Date inEndDate, WildLogApp inApp) {
        initComponents();

        startDate = inStartDate;
        endDate = inEndDate;
        app = inApp;
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
        lblNight = new javax.swing.JLabel();
        lblDay = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblOther1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblDay1 = new javax.swing.JLabel();
        lblNight1 = new javax.swing.JLabel();
        lblOther2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuExtra = new javax.swing.JMenu();
        mnuName = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportSighting.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
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
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, -1, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        lblNumberOfElements.setText(resourceMap.getString("lblNumberOfElements.text")); // NOI18N
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        getContentPane().add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 50, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, -1));

        lblNight.setFont(resourceMap.getFont("lblNight.font")); // NOI18N
        lblNight.setForeground(resourceMap.getColor("lblNight.foreground")); // NOI18N
        lblNight.setText(resourceMap.getString("lblNight.text")); // NOI18N
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 700, 60, -1));

        lblDay.setFont(resourceMap.getFont("lblDay.font")); // NOI18N
        lblDay.setForeground(resourceMap.getColor("lblDay.foreground")); // NOI18N
        lblDay.setText(resourceMap.getString("lblDay.text")); // NOI18N
        lblDay.setName("lblDay"); // NOI18N
        getContentPane().add(lblDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 700, 40, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, -1, -1));

        lblOther1.setFont(resourceMap.getFont("lblOther1.font")); // NOI18N
        lblOther1.setForeground(resourceMap.getColor("lblOther1.foreground")); // NOI18N
        lblOther1.setText(resourceMap.getString("lblOther1.text")); // NOI18N
        lblOther1.setName("lblOther1"); // NOI18N
        getContentPane().add(lblOther1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 700, 70, -1));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 700, -1, -1));

        lblDay1.setFont(resourceMap.getFont("lblDay1.font")); // NOI18N
        lblDay1.setForeground(resourceMap.getColor("lblDay1.foreground")); // NOI18N
        lblDay1.setText(resourceMap.getString("lblDay1.text")); // NOI18N
        lblDay1.setName("lblDay1"); // NOI18N
        getContentPane().add(lblDay1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 700, 40, -1));

        lblNight1.setFont(resourceMap.getFont("lblNight1.font")); // NOI18N
        lblNight1.setForeground(resourceMap.getColor("lblNight1.foreground")); // NOI18N
        lblNight1.setText(resourceMap.getString("lblNight1.text")); // NOI18N
        lblNight1.setName("lblNight1"); // NOI18N
        getContentPane().add(lblNight1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 700, 50, -1));

        lblOther2.setFont(resourceMap.getFont("lblOther2.font")); // NOI18N
        lblOther2.setForeground(resourceMap.getColor("lblOther2.foreground")); // NOI18N
        lblOther2.setText(resourceMap.getString("lblOther2.text")); // NOI18N
        lblOther2.setName("lblOther2"); // NOI18N
        getContentPane().add(lblOther2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 700, 70, -1));

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
        if (chartElements != null)
            this.getContentPane().remove(chartElements);
        if (chartLocations != null)
            this.getContentPane().remove(chartLocations);
        chartElements = new BarChart(280, 600);
        chartLocations = new BarChart(290, 600);
        List<Sighting> sightings = app.getDBI().searchSightingOnDate(startDate, endDate);
        for (Sighting sighting : sightings) {
            numOfElements.add(sighting.getElementName());
            String nameToUse = "";
            if (usePrimaryName)
                nameToUse = sighting.getElementName();
            else
                nameToUse = sighting.getElementName();
            // Time
            if (sighting.getTimeOfDay() != null) {
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                    chartElements.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    chartLocations.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    chartElements.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1,  lblOther1.getForeground()));
                    chartLocations.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1, lblOther1.getForeground()));
                }
                else {
                    chartElements.addBar(new BarChartEntity(nameToUse, sighting.getTimeOfDay().name(), 1, lblDay.getForeground()));
                    chartLocations.addBar(new BarChartEntity(sighting.getLocationName(), sighting.getTimeOfDay().name(), 1, lblDay.getForeground()));
                    numDaySightings++;
                }
            }
            else
                chartElements.addBar(new BarChartEntity(nameToUse, ActiveTimeSpesific.NONE.name(), 1, lblOther1.getForeground()));
        }

        this.getContentPane().add(chartElements, new AbsoluteConstraints(0, 100, -1, -1));
        this.getContentPane().add(chartLocations, new AbsoluteConstraints(290, 100, -1, -1));

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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblDay1;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNight;
    private javax.swing.JLabel lblNight1;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblOther1;
    private javax.swing.JLabel lblOther2;
    private javax.swing.JMenu mnuExtra;
    private javax.swing.JMenuItem mnuName;
    private javax.swing.JMenu mnuPrint;
    // End of variables declaration//GEN-END:variables

}