/*
 * ReportVisit.java is part of WildLog
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import wildlog.WildLogView;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.ViewRating;
import wildlog.ui.report.chart.BarChart;
import wildlog.ui.report.chart.BarChartEntity;

/**
 *
 * @author Henry
 */
public class ReportVisit extends javax.swing.JFrame {

    /** Creates new form ReportLocation */
    public ReportVisit(Visit inVisit) {
        initComponents();

        // Init report fields
        lblName.setText(inVisit.getName());
        lblVisitType.setText(inVisit.getType().toString());
        lblGameWatching.setText(inVisit.getGameWatchingIntensity().toString());
        lblNumberOfSightings.setText(Integer.toString(inVisit.getSightings().size()));
        Set<String> numOfElements = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        if (inVisit.getStartDate() != null)
            lblStartDate.setText(new SimpleDateFormat("dd MMM yyyy").format(inVisit.getStartDate()));
        else
            lblStartDate.setText("Unknown");
        if (inVisit.getEndDate() != null)
            lblEndDate.setText(new SimpleDateFormat("dd MMM yyyy").format(inVisit.getEndDate()));
        else
            lblEndDate.setText("Unknown");
        long diff = inVisit.getEndDate().getTime() - inVisit.getStartDate().getTime();
        int activeDays = (int)Math.ceil((double)diff/60/60/24/1000) + 1;
        lblActiveDays.setText(Integer.toString(activeDays));
        int verygood = 0;
        int good = 0;
        int normal = 0;
        int bad = 0;
        int verybad = 0;

        // Add Charts
        BarChart chartTime = new BarChart(550, 550);
        for (Sighting sighting : inVisit.getSightings()) {
            numOfElements.add(sighting.getElement().getPrimaryName());
            // Time
            if (sighting.getTimeOfDay() != null) {
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.EARLY_MORNING) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MORNING) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1,  lblMorning.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MIDDAY)) {
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1,  lblMidDay.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.LATE_AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1,  lblAfternoon.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), sighting.getTimeOfDay().name(), 1,  lblOther.getForeground()));
                }
            }
            else
                chartTime.addBar(new BarChartEntity(sighting.getElement().getPrimaryName(), ActiveTimeSpesific.NONE.name(), 1, lblOther.getForeground()));
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

        this.getContentPane().add(chartTime, new AbsoluteConstraints(0, 150, -1, -1));

        // Wrap up report fields
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        lblNumberOfCreatures.setText(Integer.toString(numOfElements.size()));
        DecimalFormat format = new DecimalFormat("#.###");
        lblSightingsPerDay.setText(format.format((double)inVisit.getSightings().size()/activeDays));
        lblCreaturesPerDay.setText(format.format((double)numOfElements.size()/activeDays));
        lblVeryGood.setText(Integer.toString(verygood));
        lblGood.setText(Integer.toString(good));
        lblNormal.setText(Integer.toString(normal));
        lblBad.setText(Integer.toString(bad));
        lblVeryBad.setText(Integer.toString(verybad));

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
        lblNight = new javax.swing.JLabel();
        lblMidDay = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblMorning = new javax.swing.JLabel();
        lblAfternoon = new javax.swing.JLabel();
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
        lblOther = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportVisit.class);
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
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        lblCreaturesPerDay.setText(resourceMap.getString("lblCreaturesPerDay.text")); // NOI18N
        lblCreaturesPerDay.setName("lblCreaturesPerDay"); // NOI18N
        getContentPane().add(lblCreaturesPerDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 70, -1, -1));

        jLabel3.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        getContentPane().add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, -1, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, -1, -1));

        lblVisitType.setText(resourceMap.getString("lblVisitType.text")); // NOI18N
        lblVisitType.setName("lblVisitType"); // NOI18N
        getContentPane().add(lblVisitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(482, 30, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(158, 90, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(363, 90, -1, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        lblStartDate.setText(resourceMap.getString("lblStartDate.text")); // NOI18N
        lblStartDate.setName("lblStartDate"); // NOI18N
        getContentPane().add(lblStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(78, 30, -1, -1));

        lblEndDate.setText(resourceMap.getString("lblEndDate.text")); // NOI18N
        lblEndDate.setName("lblEndDate"); // NOI18N
        getContentPane().add(lblEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 70, -1, -1));

        lblActiveDays.setText(resourceMap.getString("lblActiveDays.text")); // NOI18N
        lblActiveDays.setName("lblActiveDays"); // NOI18N
        getContentPane().add(lblActiveDays, new org.netbeans.lib.awtextra.AbsoluteConstraints(517, 70, -1, -1));

        lblNight.setFont(resourceMap.getFont("lblNight.font")); // NOI18N
        lblNight.setForeground(resourceMap.getColor("lblNight.foreground")); // NOI18N
        lblNight.setText(resourceMap.getString("lblNight.text")); // NOI18N
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 700, 50, -1));

        lblMidDay.setFont(resourceMap.getFont("lblMidDay.font")); // NOI18N
        lblMidDay.setForeground(resourceMap.getColor("lblMidDay.foreground")); // NOI18N
        lblMidDay.setText(resourceMap.getString("lblMidDay.text")); // NOI18N
        lblMidDay.setName("lblMidDay"); // NOI18N
        getContentPane().add(lblMidDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 700, -1, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, -1, -1));

        lblMorning.setFont(resourceMap.getFont("lblMorning.font")); // NOI18N
        lblMorning.setForeground(resourceMap.getColor("lblMorning.foreground")); // NOI18N
        lblMorning.setText(resourceMap.getString("lblMorning.text")); // NOI18N
        lblMorning.setName("lblMorning"); // NOI18N
        getContentPane().add(lblMorning, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 700, -1, -1));

        lblAfternoon.setFont(resourceMap.getFont("lblAfternoon.font")); // NOI18N
        lblAfternoon.setForeground(resourceMap.getColor("lblAfternoon.foreground")); // NOI18N
        lblAfternoon.setText(resourceMap.getString("lblAfternoon.text")); // NOI18N
        lblAfternoon.setName("lblAfternoon"); // NOI18N
        getContentPane().add(lblAfternoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 700, -1, -1));

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, -1, -1));

        lblGameWatching.setText(resourceMap.getString("lblGameWatching.text")); // NOI18N
        lblGameWatching.setName("lblGameWatching"); // NOI18N
        getContentPane().add(lblGameWatching, new org.netbeans.lib.awtextra.AbsoluteConstraints(517, 50, -1, -1));

        lblSightingsPerDay.setText(resourceMap.getString("lblSightingsPerDay.text")); // NOI18N
        lblSightingsPerDay.setName("lblSightingsPerDay"); // NOI18N
        getContentPane().add(lblSightingsPerDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 70, -1, -1));

        lblNumberOfCreatures.setText(resourceMap.getString("lblNumberOfCreatures.text")); // NOI18N
        lblNumberOfCreatures.setName("lblNumberOfCreatures"); // NOI18N
        getContentPane().add(lblNumberOfCreatures, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, -1, -1));

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, -1, -1));

        jLabel23.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        lblVeryGood.setText(resourceMap.getString("lblVeryGood.text")); // NOI18N
        lblVeryGood.setName("lblVeryGood"); // NOI18N
        getContentPane().add(lblVeryGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(131, 110, -1, -1));

        jLabel25.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, -1, -1));

        lblGood.setText(resourceMap.getString("lblGood.text")); // NOI18N
        lblGood.setName("lblGood"); // NOI18N
        getContentPane().add(lblGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(304, 110, -1, -1));

        jLabel27.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, -1, -1));

        lblNormal.setText(resourceMap.getString("lblNormal.text")); // NOI18N
        lblNormal.setName("lblNormal"); // NOI18N
        getContentPane().add(lblNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(523, 110, -1, -1));

        jLabel29.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        lblBad.setText(resourceMap.getString("lblBad.text")); // NOI18N
        lblBad.setName("lblBad"); // NOI18N
        getContentPane().add(lblBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 130, -1, -1));

        jLabel31.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, -1, -1));

        lblVeryBad.setText(resourceMap.getString("lblVeryBad.text")); // NOI18N
        lblVeryBad.setName("lblVeryBad"); // NOI18N
        getContentPane().add(lblVeryBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 130, -1, -1));

        lblOther.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblOther.setForeground(resourceMap.getColor("lblOther.foreground")); // NOI18N
        lblOther.setText(resourceMap.getString("lblOther.text")); // NOI18N
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 700, 50, -1));

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
            pj.setJobName("WildLog Visit Report");
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
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lblActiveDays;
    private javax.swing.JLabel lblAfternoon;
    private javax.swing.JLabel lblBad;
    private javax.swing.JLabel lblCreaturesPerDay;
    private javax.swing.JLabel lblDaySightings;
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
    private javax.swing.JMenu mnuPrint;
    // End of variables declaration//GEN-END:variables

}
