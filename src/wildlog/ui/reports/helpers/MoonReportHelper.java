package wildlog.ui.reports.helpers;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JLabel;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;
import wildlog.ui.reports.chart.BarChart;
import wildlog.ui.reports.chart.BarChartEntity;


public class MoonReportHelper {

    public static void addMoonInfoToChart(BarChart inChart, Sighting inSighting, JLabel[] inLabels) {
        if (inSighting.getMoonPhase() >= 0) {
            if (inSighting.getMoonPhase() >= 0 && inSighting.getMoonPhase() < 50)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "0-50%", "First Half", 1, getColor(inSighting, inLabels)));
            else
            if (inSighting.getMoonPhase() > 50 && inSighting.getMoonPhase() <= 100)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "50-100%", "Second Half", 1, getColor(inSighting, inLabels)));
            else
                // IF the moon is 50% then base it on wether the moon is growing or shrinking.
            if (inSighting.getMoonPhase() == 50) {
                LocalDateTime futureTime = LocalDateTime.ofInstant(inSighting.getDate().toInstant(), ZoneId.systemDefault()).plusDays(2);
                int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                if (testMoonphase >= 0 && testMoonphase < 50)
                    inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "0-50%", "First Half", 1, getColor(inSighting, inLabels)));
                else
                if (testMoonphase >50 && testMoonphase <= 100)
                    inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "50-100%", "Second Half", 1, getColor(inSighting, inLabels)));
                else
                    inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "Unknown", "Unknown", 1, getColor(inSighting, inLabels)));
            }
            else
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "Unknown", "Unknown", 1, getColor(inSighting, inLabels)));
        }
        else
            inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "Unknown", "Unknown", 1, getColor(inSighting, inLabels)));
    }

    private static Color getColor(Sighting inSighting, JLabel[] inLables) {
        if (inSighting.getTimeOfDay() != null) {
            if (inSighting.getTimeOfDay().equals(ActiveTimeSpesific.NIGHT_EARLY)
                    || inSighting.getTimeOfDay().equals(ActiveTimeSpesific.NIGHT_MID)
                    || inSighting.getTimeOfDay().equals(ActiveTimeSpesific.NIGHT_LATE)) {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return inLables[0].getForeground();
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return inLables[1].getForeground();
                    else
                        return inLables[4].getForeground();
                }
                else
                    return inLables[4].getForeground();
            }
            else
            if (inSighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return inLables[4].getForeground();
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return inLables[4].getForeground();
                    else
                        return inLables[4].getForeground();
                }
                else
                    return inLables[4].getForeground();
            }
            else {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return inLables[2].getForeground();
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return inLables[3].getForeground();
                    else
                        return inLables[4].getForeground();
                }
                else
                    return inLables[4].getForeground();
            }
        }
        return inLables[4].getForeground();
    }

    private static String getPrefix(Sighting inSighting) {
        return "Moon ";
    }

}