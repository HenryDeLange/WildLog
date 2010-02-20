/*
 * ReportLocation.java is part of WildLog
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import wildlog.WildLogView;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.VisitType;
import wildlog.ui.report.chart.BarChart;
import wildlog.ui.report.chart.BarChartEntity;

/**
 *
 * @author Henry
 */
public class ReportLocation extends javax.swing.JFrame {

    /** Creates new form ReportLocation */
    public ReportLocation(Location inLocation) {
        initComponents();

        // Init report fields
        lblName.setText(inLocation.getName());
        lblNumberOfVisits.setText(Integer.toString(inLocation.getVisits().size()));
        int numOfSightings = 0;
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        Date firstDate = null;
        Date lastDate = null;
        int activeDays = 0;

        // Add Charts
        BarChart chartTime = new BarChart(280, 600);
        BarChart chartType = new BarChart(280, 600);
        for (Visit visit : inLocation.getVisits()) {
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
                long diff = visit.getEndDate().getTime() - visit.getStartDate().getTime();
                activeDays = activeDays + (int)Math.ceil((double)diff/60/60/24/1000) + 1;
            }
            for (Sighting sighting : visit.getSightings()) {
                numOfSightings++;
                numOfElements.add(sighting.getElement().getPrimaryName());
                // Time
                if (sighting.getTimeOfDay() != null) {
                    if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                        chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1, Color.GRAY));
                        numNightSightings++;
                    }
                    else
                    if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                        chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1,  new Color(214,210,166)));
                    }
                    else {
                        chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1, Color.ORANGE));
                        numDaySightings++;
                    }
                }
                else
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), ActiveTimeSpesific.NONE.name(), 1, new Color(214,210,166)));
                // Type
                if (visit.getType() != null) {
                    if (visit.getType().equals(VisitType.REMOTE_CAMERA))
                        chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), visit.getType().name(), 1, new Color(90,157,46)));
                    else
                    if (visit.getType().equals(VisitType.DAY_VISIT))
                        chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), visit.getType().name(), 1, new Color(60,170,193)));
                    else
                    if (visit.getType().equals(VisitType.VACATION))
                        chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), visit.getType().name(), 1, new Color(24,134,166)));
                    else
                    if (visit.getType().equals(VisitType.BIRD_ATLASSING))
                        chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), visit.getType().name(), 1, new Color(208,163,78)));
                    else
                        chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), visit.getType().name(), 1, new Color(214,210,166)));
                }
                else
                    chartType.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), VisitType.NONE.name(), 1, new Color(214,210,166)));
            }
        }

        this.getContentPane().add(chartTime, new AbsoluteConstraints(0, 100, -1, -1));
        this.getContentPane().add(chartType, new AbsoluteConstraints(290, 100, -1, -1));

        // Wrap up report fields
        lblNumberOfSightings.setText(Integer.toString(numOfSightings));
        lblNumberOfElements.setText(Integer.toString(numOfElements.size()));
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        lblFirstVisit.setText(new SimpleDateFormat("dd MMM yyyy").format(firstDate));
        lblLastVisit.setText(new SimpleDateFormat("dd MMM yyyy").format(lastDate));
        lblActiveDays.setText(Integer.toString(activeDays));

        // Setup Frame Look and Feel
        this.getContentPane().setBackground(Color.WHITE);
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
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportLocation.class);
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

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblNumberOfVisits.setText(resourceMap.getString("lblNumberOfVisits.text")); // NOI18N
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N
        getContentPane().add(lblNumberOfVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

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
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(158, 50, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 50, -1, -1));

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
        getContentPane().add(lblFirstVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, -1, -1));

        lblLastVisit.setText(resourceMap.getString("lblLastVisit.text")); // NOI18N
        lblLastVisit.setName("lblLastVisit"); // NOI18N
        getContentPane().add(lblLastVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, -1, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 50, -1, -1));

        lblActiveDays.setText(resourceMap.getString("lblActiveDays.text")); // NOI18N
        lblActiveDays.setName("lblActiveDays"); // NOI18N
        getContentPane().add(lblActiveDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 50, -1, -1));

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 700, 50, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 700, -1, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, -1, -1));

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setForeground(resourceMap.getColor("jLabel11.foreground")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 700, -1, -1));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 700, -1, -1));

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setForeground(resourceMap.getColor("jLabel14.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 700, -1, -1));

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setForeground(resourceMap.getColor("jLabel15.foreground")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 700, -1, -1));

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setForeground(resourceMap.getColor("jLabel16.foreground")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 720, -1, -1));

        jLabel17.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel17.setForeground(resourceMap.getColor("jLabel17.foreground")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 700, -1, -1));

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 720, 50, -1));

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

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
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
            Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
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
    // End of variables declaration//GEN-END:variables

}
