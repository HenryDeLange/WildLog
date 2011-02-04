package wildlog.ui.report.helpers;

import java.awt.Color;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;
import wildlog.ui.report.chart.BarChart;
import wildlog.ui.report.chart.BarChartEntity;

/**
 *
 * @author Henry
 */
public class MoonReportHelper {

    public static void addMoonInfoToChart(BarChart inChart, Sighting inSighting, JLabel[] inLabels) {
//        if (inSighting.getMoonPhase() >= 0) {
//            if (inSighting.getMoonPhase() >= 0 && inSighting.getMoonPhase() <= 20)
//                inChart.addBar(new BarChartEntity("All 0-20%", "Nuut", 1, inLabels[0].getForeground()));
//            else
//            if (inSighting.getMoonPhase() > 20 && inSighting.getMoonPhase() <= 40)
//                inChart.addBar(new BarChartEntity("All 20-40%", "Kwart", 1, inLabels[0].getForeground()));
//            else
//            if (inSighting.getMoonPhase() > 40 && inSighting.getMoonPhase() <= 60)
//                inChart.addBar(new BarChartEntity("All 40-60%", "Half", 1, inLabels[0].getForeground()));
//            else
//            if (inSighting.getMoonPhase() > 60 && inSighting.getMoonPhase() <= 80)
//                inChart.addBar(new BarChartEntity("All 60-80%", "Driekwart", 1, inLabels[0].getForeground()));
//            else
//            if (inSighting.getMoonPhase() > 80 && inSighting.getMoonPhase() <= 100)
//                inChart.addBar(new BarChartEntity("All 80-100%", "Vol", 1, inLabels[0].getForeground()));
//            else
//                inChart.addBar(new BarChartEntity("All Unknown Phase", "Weetnie", 1, inLabels[4].getForeground()));
//        }
//        else
//            inChart.addBar(new BarChartEntity("All Unknown Phase", "Weetnie", 1, inLabels[4].getForeground()));

        if (inSighting.getMoonPhase() >= 0) {
            if (inSighting.getMoonPhase() >= 0 && inSighting.getMoonPhase() <= 20)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "0-20%", "Nuut", 1, getColor(inSighting, inLabels)));
            else
            if (inSighting.getMoonPhase() > 20 && inSighting.getMoonPhase() <= 40)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "20-40%", "Kwart", 1, getColor(inSighting, inLabels)));
            else
            if (inSighting.getMoonPhase() > 40 && inSighting.getMoonPhase() <= 60)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "40-60%", "Half", 1, getColor(inSighting, inLabels)));
            else
            if (inSighting.getMoonPhase() > 60 && inSighting.getMoonPhase() <= 80)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "60-80%", "Driekwart", 1, getColor(inSighting, inLabels)));
            else
            if (inSighting.getMoonPhase() > 80 && inSighting.getMoonPhase() <= 100)
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "80-100%", "Vol", 1, getColor(inSighting, inLabels)));
            else
                inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "Wierd Phase", "Weetnie", 1, getColor(inSighting, inLabels)));
        }
        else
            inChart.addBar(new BarChartEntity(getPrefix(inSighting) + "Wierd Phase", "Weetnie", 1, getColor(inSighting, inLabels)));
    }

    private static Color getColor(Sighting inSighting, JLabel[] inLables) {
        if (inSighting.getTimeOfDay() != null) {
            if (inSighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
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
        if (inSighting.getTimeOfDay() != null) {
            if (inSighting.getTimeOfDay().equals(ActiveTimeSpesific.DEEP_NIGHT)) {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return "Night ";
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return "Night ";
                    else
                        return "Night ";
                }
                else
                    return "Night ";
            }
            else
            if (inSighting.getTimeOfDay().equals(ActiveTimeSpesific.NONE)) {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return "No Time ";
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return "No Time ";
                    else
                        return "No Time ";
                }
                else
                    return "No Time ";
            }
            else {
                if (inSighting.getMoonlight() != null) {
                    if (inSighting.getMoonlight().equals(Moonlight.MOON_SHINING))
                        return "Day ";
                    else
                    if (inSighting.getMoonlight().equals(Moonlight.NO_MOON))
                        return "Day ";
                    else
                        return "Day ";
                }
                else
                    return "Day ";
            }
        }
        return "Unknown ";
    }
    
}