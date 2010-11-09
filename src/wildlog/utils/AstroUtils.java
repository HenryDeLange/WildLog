package wildlog.utils;

import astro.MoonPhase;
import astro.MoonTimes;
import astro.SunTimes;
import java.util.Date;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;

/**
 *
 * @author DeLangeH
 */
public final class AstroUtils {
    public static ActiveTimeSpesific getSunCategory(Date inDate, double inLatitude, double inLongitude) {
        double[] day = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.SUNRISE_AND_SET);
        //double[] twilight = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.CIVIL_TWILIGHT);
        double[] nearnight = SunTimes.calculateTimes(inDate, inLatitude, inLongitude, SunTimes.NAUTICAL_TWILIGHT);
//System.out.println(day[0] + "  " + day[1]);
//System.out.println(twilight[0] + "  " + twilight[1]);
//System.out.println(night[0] + "  " + night[1]);
        double time = (double)(inDate.getHours() + ((double)inDate.getMinutes() / 60));
//System.out.println(time);
        if (time >= day[SunTimes.RISE] && time <= day[SunTimes.SET]) {
            // Gedurende die dag
            double interval = (day[SunTimes.SET] - day[SunTimes.RISE])/5;
            if (time >= (day[SunTimes.RISE]) && time < (day[SunTimes.RISE] + interval))
                return ActiveTimeSpesific.MORNING;
            else
            if (time >= (day[SunTimes.RISE] + interval) && time < (day[SunTimes.RISE] + interval*2))
                return ActiveTimeSpesific.MID_MORNING;
            else
            if (time >= (day[SunTimes.RISE] + interval*2) && time < (day[SunTimes.RISE] + interval*3))
                return ActiveTimeSpesific.MIDDAY;
            else
            if (time >= (day[SunTimes.RISE] + interval*3) && time < (day[SunTimes.RISE] + interval*4))
                return ActiveTimeSpesific.MID_AFTERNOON;
            else
            if (time >= (day[SunTimes.RISE] + interval*4) && time <= (day[SunTimes.RISE] + interval*5))
                return ActiveTimeSpesific.AFTERNOON;
        }
        else
        if (time >= nearnight[SunTimes.RISE] && time < day[SunTimes.RISE]) {
            // Oggend
            return ActiveTimeSpesific.EARLY_MORNING;
        }
        else
        if (time <= nearnight[SunTimes.SET] && time > day[SunTimes.SET]) {
            // Aand
            return ActiveTimeSpesific.LATE_AFTERNOON;
        }
        else
        if (time <= nearnight[SunTimes.RISE] || time >= nearnight[SunTimes.SET]) {
            // Nag
            return ActiveTimeSpesific.DEEP_NIGHT;
        }
        return ActiveTimeSpesific.NONE;
    }

    public static int getMoonPhase(Date inDate) {
        return (int)(MoonPhase.calculatePhase(inDate) * 100);
    }

    public static Moonlight getMoonlight(Date inDate, double inLatitude, double inLongitude) {
        double[] values = MoonTimes.calculateTimes(inDate, inLatitude, inLongitude);
//System.out.println(values[0] + "  " + values[1]);
        double time = (double)(inDate.getHours() + ((double)inDate.getMinutes() / 60));
//System.out.println(time);
        if (values[MoonTimes.RISE] <= values[MoonTimes.SET]) {
            if (time >= values[MoonTimes.RISE] && time <= values[MoonTimes.SET])
                return Moonlight.MOON_SHINING;
        }
        else {
            if (time >= values[MoonTimes.RISE])
                return Moonlight.MOON_SHINING;
            else
            if (values[MoonTimes.RISE] == MoonTimes.ABOVE_HORIZON && time <= values[MoonTimes.SET])
                return Moonlight.MOON_SHINING;
        }
        return Moonlight.NO_MOON;
    }
}
