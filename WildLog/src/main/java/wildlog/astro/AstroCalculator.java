package wildlog.astro;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;
import wildlog.utils.UtilsTime;


public class AstroCalculator {

    private AstroCalculator() {
    }

    public static ActiveTimeSpesific getSunCategory(Date inDate, double inLatitude, double inLongitude) {
        double[] day = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.SUNRISE_AND_SET);
        // Don't use Civil twilight, it is already too bright at this stage.
        //double[] twilight = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.CIVIL_TWILIGHT);
        // Use Nautical twilight as indicator of the onset of night. It is triggers when the horizon becomes distingiushable.
        double[] nearnight = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.NAUTICAL_TWILIGHT);
        // Don't use Astronomical twilight, it is too dark to distinguish from night.
        // double[] truenight = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.ASTRONOMICAL_TWILIGHT);
        //
        // Note: The algorithm will return the values as follows:
        //           Sunrise = When the disk of the sun becomes visible above the horizon.
        //           Sunset  = When the disk of the sun is nolonger visible above the horizon.
        //       There is thus a difference between the two time, the one shows the start of the event the other the end.
        //       Since it takes about 2+ minutes for the sun to move accross the horizon (longer at poles) 
        //       the start time will actually be earlier than the algorith time.
        LocalDateTime localDateTime = UtilsTime.getLocalDateTimeFromDate(inDate);
        double time = (double)(localDateTime.getHour() + (((double)localDateTime.getMinute()) / 60.0));
        if (day[SunTimes.START] == SunTimes.BELOW_HORIZON || day[SunTimes.END] == SunTimes.BELOW_HORIZON
                || day[SunTimes.START] == SunTimes.ABOVE_HORIZON || day[SunTimes.END] == SunTimes.ABOVE_HORIZON
                || nearnight[SunTimes.START] == SunTimes.BELOW_HORIZON || nearnight[SunTimes.END] == SunTimes.BELOW_HORIZON
                || nearnight[SunTimes.START] == SunTimes.ABOVE_HORIZON || nearnight[SunTimes.END] == SunTimes.ABOVE_HORIZON) {
            // Dealing with weird poler times...
// TODO: Eendag as dit relevant raak moet ek dalk al die corner-cases uitsorteer, maar vir nou is dit nie die tyd werd nie want ek sal dit seker nooit self nodig kry nie.
            if (day[SunTimes.START] == SunTimes.BELOW_HORIZON && day[SunTimes.END] == SunTimes.BELOW_HORIZON) {
                return ActiveTimeSpesific.NIGHT_MID;
            }
            else
            if (day[SunTimes.START] == SunTimes.ABOVE_HORIZON && day[SunTimes.END] == SunTimes.ABOVE_HORIZON) {
                return ActiveTimeSpesific.DAY_MID;
            }
            else
            if (day[SunTimes.START] != SunTimes.BELOW_HORIZON && day[SunTimes.START] != SunTimes.ABOVE_HORIZON && time >= day[SunTimes.START]
                    && day[SunTimes.END] == SunTimes.ABOVE_HORIZON && time <= day[SunTimes.END]) {
                return ActiveTimeSpesific.DAY_MID;
            }
            else
            if (day[SunTimes.START] == SunTimes.BELOW_HORIZON && time <= day[SunTimes.END]) {
                return ActiveTimeSpesific.NIGHT_MID;
            }
            else {
                if (nearnight[SunTimes.START] == SunTimes.BELOW_HORIZON || nearnight[SunTimes.END] == SunTimes.BELOW_HORIZON) {
                    if (time <= 12) {
                        return ActiveTimeSpesific.MORNING_TWILIGHT;
                    }
                    else {
                        return ActiveTimeSpesific.AFTERNOON_TWILIGHT;
                    }
                }
                else
                if (nearnight[SunTimes.START] == SunTimes.ABOVE_HORIZON || nearnight[SunTimes.END] == SunTimes.ABOVE_HORIZON) {
                    if (time <= 12) {
                        return ActiveTimeSpesific.MORNING_EARLY;
                    }
                    else {
                        return ActiveTimeSpesific.AFTERNOON_LATE;
                    }
                }
                else {
                    if (time <= 12) {
                        return ActiveTimeSpesific.MORNING_TWILIGHT;
                    }
                    else {
                        return ActiveTimeSpesific.AFTERNOON_TWILIGHT;
                    }
                }
            }
        }
        else {
            // Dealing with normal day-night cycle.
            double bufferNight = (nearnight[SunTimes.END] - day[SunTimes.END]) / 5;
            double bufferDay = bufferNight * 2;
            double paddedSunrise = day[SunTimes.START] - bufferNight;
            double paddedSenset = day[SunTimes.END] + bufferNight;
            if (time >= paddedSunrise && time <= paddedSenset) {
                // Dag
                if (time >= paddedSunrise && time <= (day[SunTimes.START] + bufferDay)) {
                    return ActiveTimeSpesific.MORNING_SUNRISE;
                }
                else
                if (time >= (day[SunTimes.END] - bufferDay) && time <= paddedSenset) {
                    return ActiveTimeSpesific.AFTERNOON_SUNSET;
                }
                else {
                    double period = ((day[SunTimes.END] - bufferDay) - (day[SunTimes.START] + bufferDay)) / 5;
                    double dayStart = day[SunTimes.START] + bufferDay;
                    if (time >= dayStart && time < (dayStart + period)) {
                        return ActiveTimeSpesific.MORNING_EARLY;
                    }
                    else
                    if (time >= (dayStart + period) && time < (dayStart + period*2)) {
                        return ActiveTimeSpesific.MORNING_MID;
                    }
                    else
                    if (time >= (dayStart + period*2) && time < (dayStart + period*3)) {
                        return ActiveTimeSpesific.DAY_MID;
                    }
                    else
                    if (time >= (dayStart + period*3) && time < (dayStart + period*4)) {
                        return ActiveTimeSpesific.AFTERNOON_MID;
                    }
                    else
                    if (time >= (dayStart + period*4) && time <= ((day[SunTimes.END] - bufferDay))) {
                        return ActiveTimeSpesific.AFTERNOON_LATE;
                    }
                }
            }
            else
            if (time >= nearnight[SunTimes.START] && time <= paddedSunrise) {
                // Oggend Skemer
                return ActiveTimeSpesific.MORNING_TWILIGHT;
            }
            else
            if (time <= nearnight[SunTimes.END] && time >= paddedSenset) {
                // Aand Skemer
                return ActiveTimeSpesific.AFTERNOON_TWILIGHT;
            }
            else
            if (time <= nearnight[SunTimes.START] || time >= nearnight[SunTimes.END]) {
                // Nag
                // Since night time overlaps two days (moving from 23:59 to 00:00) it makes the calculations a little trickier.
                // To help with this I change the night period to start at 00:00 for the comparisons.
                double nightDuration = (24 - nearnight[SunTimes.END] + nearnight[SunTimes.START]);
                double period = nightDuration / 3;
                double hoursOfNightTime;
                if (time > nearnight[SunTimes.END]) {
                    hoursOfNightTime = time - nearnight[SunTimes.END];
                }
                else {
                    hoursOfNightTime = time + (24 - nearnight[SunTimes.END]);
                }
                if (hoursOfNightTime >= 0 && hoursOfNightTime <= period) {
                    return ActiveTimeSpesific.NIGHT_EARLY;
                }
                else
                if (hoursOfNightTime >= period && hoursOfNightTime <= period*2) {
                    return ActiveTimeSpesific.NIGHT_MID;
                }
                else
                if (hoursOfNightTime >= period*2 && hoursOfNightTime <= nightDuration) {
                    return ActiveTimeSpesific.NIGHT_LATE;
                }
            }
        }
        return ActiveTimeSpesific.NONE;
    }

    public static int getMoonPhase(Date inDate) {
        return (int)(MoonPhase.calculatePhase(inDate) * 100);
    }

    public static Moonlight getMoonlight(Date inDate, double inLatitude, double inLongitude) {
        double[] values = MoonTimes.calculateTimes(inDate, inLatitude, inLongitude);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inDate);
        double time = (double)(calendar.get(Calendar.HOUR_OF_DAY) + (((double)calendar.get(Calendar.MINUTE)) / 60.0));
        if (values[MoonTimes.START] <= values[MoonTimes.END]) {
            if (time >= values[MoonTimes.START] && time <= values[MoonTimes.END]) {
                return Moonlight.MOON_SHINING;
            }
        }
        else {
            if (time >= values[MoonTimes.START]) {
                return Moonlight.MOON_SHINING;
            }
            else
            if (values[MoonTimes.START] == MoonTimes.ABOVE_HORIZON && time <= values[MoonTimes.END]) {
                return Moonlight.MOON_SHINING;
            }
        }
        return Moonlight.NO_MOON;
    }
    
//    /**
//     * Used for testing.
//     * @param args 
//     */
//    public static void main(String[] args) {
//        System.out.println("-------------------------------------------WINTER-------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  01*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 6-1, 25, hours, mins), -33.9, 18.6);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------SOMER--------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  01*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 12-1, 25, hours, mins), -33.9, 18.6);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------ALLES--------------------------------------------");
//        Map<ActiveTimeSpesific, Integer> count = new HashMap<>(24*60);
//        for (int t = 0; t < 24; t++) {
//            int hours = t;
//            for (int i = 0; i < 60; i++) {
//                int mins = i;
//                ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 4-1, 25, hours, mins), -33.9, 18.6);
//                String hrs = ""+hours;
//                if (hours < 10) {
//                    hrs = "0" + hrs;
//                }
//                String mns = ""+mins;
//                if (mins < 10) {
//                    mns = "0" + mns;
//                }
//                System.out.println(hrs + ":" + mns + "  -> " + activeTimeSpesific);
//                if (count.containsKey(activeTimeSpesific)) {
//                    count.put(activeTimeSpesific, count.get(activeTimeSpesific) + 1);
//                }
//                else {
//                    count.put(activeTimeSpesific, 1);
//                }
//            }
//        }
//        System.out.println("...........................................");
//        System.out.println(ActiveTimeSpesific.MORNING_TWILIGHT.getText() + " = " + count.get(ActiveTimeSpesific.MORNING_TWILIGHT));
//        System.out.println(ActiveTimeSpesific.MORNING_SUNRISE.getText() + " = " + count.get(ActiveTimeSpesific.MORNING_SUNRISE));
//        System.out.println(ActiveTimeSpesific.MORNING_EARLY.getText() + " = " + count.get(ActiveTimeSpesific.MORNING_EARLY));
//        System.out.println(ActiveTimeSpesific.MORNING_MID.getText() + " = " + count.get(ActiveTimeSpesific.MORNING_MID));
//        System.out.println(ActiveTimeSpesific.DAY_MID.getText() + " = " + count.get(ActiveTimeSpesific.DAY_MID));
//        System.out.println(ActiveTimeSpesific.AFTERNOON_MID.getText() + " = " + count.get(ActiveTimeSpesific.AFTERNOON_MID));
//        System.out.println(ActiveTimeSpesific.AFTERNOON_LATE.getText() + " = " + count.get(ActiveTimeSpesific.AFTERNOON_LATE));
//        System.out.println(ActiveTimeSpesific.AFTERNOON_SUNSET.getText() + " = " + count.get(ActiveTimeSpesific.AFTERNOON_SUNSET));
//        System.out.println(ActiveTimeSpesific.AFTERNOON_TWILIGHT.getText() + " = " + count.get(ActiveTimeSpesific.AFTERNOON_TWILIGHT));
//        System.out.println(ActiveTimeSpesific.NIGHT_EARLY.getText() + " = " + count.get(ActiveTimeSpesific.NIGHT_EARLY));
//        System.out.println(ActiveTimeSpesific.NIGHT_MID.getText() + " = " + count.get(ActiveTimeSpesific.NIGHT_MID));
//        System.out.println(ActiveTimeSpesific.NIGHT_LATE.getText() + " = " + count.get(ActiveTimeSpesific.NIGHT_LATE));
//        System.out.println(ActiveTimeSpesific.NONE.getText() + " = " + count.get(ActiveTimeSpesific.NONE));
//        System.out.println("-------------------------------------------POLE SOMER--------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  01*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 6-1, 25, hours, mins), 81.9, 45.6);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------POLE WINTER--------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  1*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 12-1, 25, hours, mins), 81.9, 45.6);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------POLE LENTE---------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  1*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 4-1, 14, hours, mins), 79.7481, 41.9856);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------POLE HERFS---------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  1*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 11-1, 21, hours, mins), 79.2942, 43.3919);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------POLE ANDER1---------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  1*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 9-1, 21, hours, mins), 79.2942, -43.3919);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//        System.out.println("-------------------------------------------POLE ANDER2---------------------------------------------");
//        for (int t = 0; t < 24; t++) {
//            int hours =  1*t;
//            int mins = 30;
//            ActiveTimeSpesific activeTimeSpesific = getSunCategory(new Date(2014, 12-1, 25, hours, mins), 68.2942, -143.3919);
//            System.out.println(hours + ":" + mins + "  -> " + activeTimeSpesific);
//        }
//    }

}
