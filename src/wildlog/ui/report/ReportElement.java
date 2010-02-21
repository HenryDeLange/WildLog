/*
 * ReportElement.java is part of WildLog
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
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.ui.report.chart.BarChart;
import wildlog.ui.report.chart.BarChartEntity;

/**
 *
 * @author Henry
 */
public class ReportElement extends javax.swing.JFrame {

    /** Creates new form ReportElement */
    public ReportElement(Element inElement, WildLogApp inApp) {
        initComponents();

        // Init report fields
        lblName.setText(inElement.getPrimaryName());
        lblWishList.setText(inElement.getWishListRating().toString());
        lblAddFrequency.setText(inElement.getAddFrequency().toString());
        Sighting tempSighting = new Sighting();
        tempSighting.setElement(inElement);
        List<Sighting> sightings = inApp.getDBI().list(tempSighting);
        lblNumberOfSightings.setText(Integer.toString(sightings.size()));
        Set<String> numOfLocations = new HashSet<String>();
        int numDaySightings = 0;
        int numNightSightings = 0;
        Date firstDate = null;
        Date lastDate = null;
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

        // Visit Type
        Visit tempVisit = new Visit();
        tempVisit.getSightings().add(tempSighting);
        List<Visit> visits = inApp.getDBI().list(tempVisit);
        for (Visit visit : visits) {
            if (visit.getType() != null) {
                if (visit.getType().equals(VisitType.REMOTE_CAMERA))
                    remotecamera++;
                else
                if (visit.getType().equals(VisitType.DAY_VISIT))
                    dayvisit++;
                else
                if (visit.getType().equals(VisitType.VACATION))
                    vacation++;
                else
                if (visit.getType().equals(VisitType.BIRD_ATLASSING))
                    atlas++;
                else
                if (visit.getType().equals(VisitType.NONE))
                    other++;
            }
            else
                other++;
        }

        // Add Charts
        BarChart chartTime = new BarChart(550, 530);
        for (Sighting sighting : sightings) {
            // Locations
            numOfLocations.add(sighting.getLocation().getName());
            // Dates
            if (sighting.getDate() != null) {
                if (firstDate == null)
                    firstDate = sighting.getDate();
                else
                if (sighting.getDate().before(firstDate))
                    firstDate = sighting.getDate();
            }
            if (sighting.getDate() != null) {
                if (lastDate == null)
                    lastDate = sighting.getDate();
                else
                if (sighting.getDate().after(lastDate))
                    lastDate = sighting.getDate();
            }
            // Time
            if (sighting.getTimeOfDay() != null) {
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), sighting.getTimeOfDay().name(), 1, lblNight.getForeground()));
                    numNightSightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.EARLY_MORNING) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), sighting.getTimeOfDay().name(), 1,  lblMorning.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.MIDDAY) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.MID_MORNING)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), sighting.getTimeOfDay().name(), 1,  lblMidDay.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.AFTERNOON) || sighting.getTimeOfDay().equals(ActiveTimeSpesific.LATE_AFTERNOON)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), sighting.getTimeOfDay().name(), 1,  lblAfternoon.getForeground()));
                    numDaySightings++;
                }
                else
                if (sighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                    chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), sighting.getTimeOfDay().name(), 1,  lblOther.getForeground()));
                }
            }
            else
                chartTime.addBar(new BarChartEntity(sighting.getLocation().getName(), ActiveTimeSpesific.NONE.name(), 1, lblOther.getForeground()));
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

        this.getContentPane().add(chartTime, new AbsoluteConstraints(0, 170, -1, -1));

        // Wrap up report fields
        lblDaySightings.setText(Integer.toString(numDaySightings));
        lblNightSightings.setText(Integer.toString(numNightSightings));
        lblNumberOfLocations.setText(Integer.toString(numOfLocations.size()));
        lblFirstDate.setText(new SimpleDateFormat("dd MMM yyyy").format(firstDate));
        lblLastDate.setText(new SimpleDateFormat("dd MMM yyyy").format(lastDate));
        lblVeryGood.setText(Integer.toString(verygood));
        lblGood.setText(Integer.toString(good));
        lblNormal.setText(Integer.toString(normal));
        lblBad.setText(Integer.toString(bad));
        lblVeryBad.setText(Integer.toString(verybad));
        lblRemoteCamera.setText(Integer.toString(remotecamera));
        lblDayVisit.setText(Integer.toString(dayvisit));
        lblVacation.setText(Integer.toString(vacation));
        lblAtlasing.setText(Integer.toString(atlas));
        lblOtherVisit.setText(Integer.toString(other));

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
        lblDayVisit = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblAddFrequency = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblAtlasing = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblOtherVisit = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFirstDate = new javax.swing.JLabel();
        lblLastDate = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblVacation = new javax.swing.JLabel();
        lblNight = new javax.swing.JLabel();
        lblMidDay = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblMorning = new javax.swing.JLabel();
        lblAfternoon = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblWishList = new javax.swing.JLabel();
        lblRemoteCamera = new javax.swing.JLabel();
        lblNumberOfLocations = new javax.swing.JLabel();
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
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDaySightings = new javax.swing.JLabel();
        lblNightSightings = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuPrint = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(ReportElement.class);
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
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, -1, -1));

        lblDayVisit.setText(resourceMap.getString("lblDayVisit.text")); // NOI18N
        lblDayVisit.setName("lblDayVisit"); // NOI18N
        getContentPane().add(lblDayVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, -1, -1));

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
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, -1, -1));

        lblAddFrequency.setText(resourceMap.getString("lblAddFrequency.text")); // NOI18N
        lblAddFrequency.setName("lblAddFrequency"); // NOI18N
        getContentPane().add(lblAddFrequency, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, -1, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblAtlasing.setText(resourceMap.getString("lblAtlasing.text")); // NOI18N
        lblAtlasing.setName("lblAtlasing"); // NOI18N
        getContentPane().add(lblAtlasing, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        jLabel2.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, -1, -1));

        lblOtherVisit.setText(resourceMap.getString("lblOtherVisit.text")); // NOI18N
        lblOtherVisit.setName("lblOtherVisit"); // NOI18N
        getContentPane().add(lblOtherVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, -1, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel9.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, -1, -1));

        lblFirstDate.setText(resourceMap.getString("lblFirstDate.text")); // NOI18N
        lblFirstDate.setName("lblFirstDate"); // NOI18N
        getContentPane().add(lblFirstDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, -1, -1));

        lblLastDate.setText(resourceMap.getString("lblLastDate.text")); // NOI18N
        lblLastDate.setName("lblLastDate"); // NOI18N
        getContentPane().add(lblLastDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, -1, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, -1, -1));

        lblVacation.setText(resourceMap.getString("lblVacation.text")); // NOI18N
        lblVacation.setName("lblVacation"); // NOI18N
        getContentPane().add(lblVacation, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 70, -1, -1));

        lblNight.setFont(resourceMap.getFont("lblNight.font")); // NOI18N
        lblNight.setForeground(resourceMap.getColor("lblNight.foreground")); // NOI18N
        lblNight.setText(resourceMap.getString("lblNight.text")); // NOI18N
        lblNight.setName("lblNight"); // NOI18N
        getContentPane().add(lblNight, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 700, 60, -1));

        lblMidDay.setFont(resourceMap.getFont("lblMidDay.font")); // NOI18N
        lblMidDay.setForeground(resourceMap.getColor("lblMidDay.foreground")); // NOI18N
        lblMidDay.setText(resourceMap.getString("lblMidDay.text")); // NOI18N
        lblMidDay.setName("lblMidDay"); // NOI18N
        getContentPane().add(lblMidDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 700, 70, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, -1, -1));

        lblMorning.setFont(resourceMap.getFont("lblMorning.font")); // NOI18N
        lblMorning.setForeground(resourceMap.getColor("lblMorning.foreground")); // NOI18N
        lblMorning.setText(resourceMap.getString("lblMorning.text")); // NOI18N
        lblMorning.setName("lblMorning"); // NOI18N
        getContentPane().add(lblMorning, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 700, 70, -1));

        lblAfternoon.setFont(resourceMap.getFont("lblAfternoon.font")); // NOI18N
        lblAfternoon.setForeground(resourceMap.getColor("lblAfternoon.foreground")); // NOI18N
        lblAfternoon.setText(resourceMap.getString("lblAfternoon.text")); // NOI18N
        lblAfternoon.setName("lblAfternoon"); // NOI18N
        getContentPane().add(lblAfternoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 700, 90, -1));

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

        lblWishList.setText(resourceMap.getString("lblWishList.text")); // NOI18N
        lblWishList.setName("lblWishList"); // NOI18N
        getContentPane().add(lblWishList, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, -1, -1));

        lblRemoteCamera.setText(resourceMap.getString("lblRemoteCamera.text")); // NOI18N
        lblRemoteCamera.setName("lblRemoteCamera"); // NOI18N
        getContentPane().add(lblRemoteCamera, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, -1, -1));

        lblNumberOfLocations.setText(resourceMap.getString("lblNumberOfLocations.text")); // NOI18N
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        getContentPane().add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, -1, -1));

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, -1, -1));

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
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, -1, -1));

        lblGood.setText(resourceMap.getString("lblGood.text")); // NOI18N
        lblGood.setName("lblGood"); // NOI18N
        getContentPane().add(lblGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 110, -1, -1));

        jLabel27.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 110, -1, -1));

        lblNormal.setText(resourceMap.getString("lblNormal.text")); // NOI18N
        lblNormal.setName("lblNormal"); // NOI18N
        getContentPane().add(lblNormal, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 110, -1, -1));

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
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, -1));

        lblVeryBad.setText(resourceMap.getString("lblVeryBad.text")); // NOI18N
        lblVeryBad.setName("lblVeryBad"); // NOI18N
        getContentPane().add(lblVeryBad, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 130, -1, -1));

        lblOther.setFont(resourceMap.getFont("lblOther.font")); // NOI18N
        lblOther.setForeground(resourceMap.getColor("lblOther.foreground")); // NOI18N
        lblOther.setText(resourceMap.getString("lblOther.text")); // NOI18N
        lblOther.setName("lblOther"); // NOI18N
        getContentPane().add(lblOther, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 700, 70, -1));

        jLabel4.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, -1, -1));

        lblDaySightings.setText(resourceMap.getString("lblDaySightings.text")); // NOI18N
        lblDaySightings.setName("lblDaySightings"); // NOI18N
        getContentPane().add(lblDaySightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        lblNightSightings.setText(resourceMap.getString("lblNightSightings.text")); // NOI18N
        lblNightSightings.setName("lblNightSightings"); // NOI18N
        getContentPane().add(lblNightSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 150, -1, -1));

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
            Logger.getLogger(WildLogView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lblAddFrequency;
    private javax.swing.JLabel lblAfternoon;
    private javax.swing.JLabel lblAtlasing;
    private javax.swing.JLabel lblBad;
    private javax.swing.JLabel lblDaySightings;
    private javax.swing.JLabel lblDayVisit;
    private javax.swing.JLabel lblFirstDate;
    private javax.swing.JLabel lblGood;
    private javax.swing.JLabel lblLastDate;
    private javax.swing.JLabel lblMidDay;
    private javax.swing.JLabel lblMorning;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNight;
    private javax.swing.JLabel lblNightSightings;
    private javax.swing.JLabel lblNormal;
    private javax.swing.JLabel lblNumberOfLocations;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblOther;
    private javax.swing.JLabel lblOtherVisit;
    private javax.swing.JLabel lblRemoteCamera;
    private javax.swing.JLabel lblVacation;
    private javax.swing.JLabel lblVeryBad;
    private javax.swing.JLabel lblVeryGood;
    private javax.swing.JLabel lblWishList;
    private javax.swing.JMenu mnuPrint;
    // End of variables declaration//GEN-END:variables

}
