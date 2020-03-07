package wildlog.astro;

import java.util.Date;
import java.util.TimeZone;

/**
 * Compute the solar times of sunrise and sunset for a specified date and location.
 * 
 * <br/>
 * Loosely based on code by Martin Minow.
 * He based it on algorithms from "Astronomy on the Personal Computer" by Oliver Montenbruck
 * and Thomas Pfleger. Springer Verlag 1994. ISBN 3-540-57700-9.
 * This is a reasonably accurate and very robust procedure for sunrise that
 * will handle unusual cases, such as the one day in the year in arctic
 * latitudes that the sun rises, but does not set. It is, however, very
 * computationally-intensive.
 * 
 * <br/>
 * <b>NOTE: This can't be used for historical dates.</b>
 * <b>NOTE: This does not take the altitude into account.</b>
 * <b>NOTE: This does not take the time zone or daylight savings into account.</b>
 */
public final class SunTimes extends AstroTimes {
    /**
     * Sunrise and sunset times.
     */
    public static final double SUNRISE_AND_SET = -(50.0 / 60.0);
    /**
     * Just before sunrise and after sunset.
     */
    public static final double CIVIL_TWILIGHT = -6.0;
    /**
     * No visible horizon.
     */
    public static final double NAUTICAL_TWILIGHT = -12.0;
    /**
     * Fully night.
     */
    public static final double ASTRONOMICAL_TWILIGHT = -18.0;

    
    /**
     * This is a static class: the constructor should not be used...
     */
    private SunTimes() {
    }


    /**
     * Compute the time of sunrise and sunset for this date. This
     * uses an exhaustive search algorithm described in Astronomy on
     * the Personal Computer. Consequently, it is rather slow.
     * The times are returned in the observer's local time.Use one of the following values:
     * <pre>
     *  Horizon values:
     *	SUNRISE_AND_SET, CIVIL_TWILIGHT, NAUTICAL_TWILIGHT and ASTRONOMICAL_TWILIGHT
     * </pre>
     * @param   inDate
     * @param	inLatitude - The observer's latitude
     * @param   inLongitude - The observer's longitude
     * @param	inHorizonValue - The adopted true altitude of the horizon in degrees.
     * @return	double[] - double[START{0}] is the time of sunrise an double[END{1}] is the time of sunset
     */
    public static double[] calculateTimes(final Date inDate, final double inLatitude, final double inLongitude, final double inHorizonValue) {
        double ZONE = ((double) TimeZone.getDefault().getRawOffset()) / 86400000.0;
        double MJD = midnightMJD(inDate) - ZONE;
        double sinHorizon = AstroUtil.sin(inHorizonValue);
        double yMinus = sinAltitude(0.0, MJD, inLatitude, inLongitude) - sinHorizon;
        boolean aboveHorizon = (yMinus > 0.0);
        double rise = BELOW_HORIZON;
        double set = BELOW_HORIZON;
        if (aboveHorizon) {
            rise = ABOVE_HORIZON;
            set = ABOVE_HORIZON;
        }
        for (double hour = 1.0; hour <= 24.0; hour += 2.0) {
            double yThis = sinAltitude(hour, MJD, inLatitude, inLongitude) - sinHorizon;
            double yPlus = sinAltitude(hour + 1.0, MJD, inLatitude, inLongitude) - sinHorizon;
            /* ._________________________________________________________________.
             * | Quadratic interpolation through the three points:		|
             * | [-1, yMinus], [0, yThis], [+1, yNext]				|
             * | (These must not lie on a straight line.)			|
             * | Note: I've in-lined this as it returns several values.		|
             * ._________________________________________________________________.
             */
            double root1 = 0.0;
            double root2 = 0.0;
            int nRoots = 0;
            double A = (0.5 * (yMinus + yPlus)) - yThis;
            double B = (0.5 * (yPlus - yMinus));
            double C = yThis;
            double xExtreme = -B / (2.0 * A);
            double yExtreme = (A * xExtreme + B) * xExtreme + C;
            double discriminant = (B * B) - 4.0 * A * C;
            if (discriminant >= 0.0) {	/* Intersects x-axis? */
                double DX = 0.5 * Math.sqrt(discriminant) / Math.abs(A);
                root1 = xExtreme - DX;
                root2 = xExtreme + DX;
                if (Math.abs(root1) <= +1.0) {
                    nRoots++;
                }
                if (Math.abs(root2) <= +1.0) {
                    nRoots++;
                }
                if (root1 < -1.0) {
                    root1 = root2;
                }
            }
            /* .________________________________________________________________.
             * | Quadratic interpolation result:				|
             * | nRoots	  Number of roots found (0, 1, or 2)                    |
             * |	    If nRoots == zero, there is no event in this range.	|
             * | root1	  First root (nRoots >= 1)                              |
             * | root2	  Second root (nRoots == 2)				|
             * | yMinus	  Y-value at interpolation start. If < 0, root1 is	|
             * |	    a moonrise event.					|
             * | yExtreme Maximum value of y (nRoots == 2) -- this determines	|
             * |	    whether a 2-root event is a rise-set or a set-rise.	|
             * .________________________________________________________________.
             */
            switch (nRoots) {
                case 0:	// No root at this hour
                    break;
                case 1:	// Found either a rise or a set
                    if (yMinus < 0.0) {
                        rise = hour + root1;
                    } else {
                        set = hour + root1;
                    }
                    break;
                case 2:	// Found both a rise and a set
                    if (yExtreme < 0.0) {
                        rise = hour + root2;
                        set = hour + root1;
                    } else {
                        rise = hour + root1;
                        set = hour + root2;
                    }
                    break;
            }
            yMinus = yPlus;
            if (isEvent(rise) && isEvent(set)) {
                break;
            }
        }
        double result[] = new double[2];
        if (isEvent(rise)) {
            rise = mod(rise, 24.0);
        }
        if (isEvent(set)) {
            set = mod(set, 24.0);
        }
        result[START] = rise;
        result[END] = set;
        return (result);
    }

    /**
     * Convenience method for SunTimes.toString(double inRise, double inSet).
     * @param inRiseAndSet - The array from
     * @return String - Similar to SunTimes.toString(double inRise, double inSet)
     */
    public static String toString(final double[] inRiseAndSet) {
        return (toString(inRiseAndSet[START], inRiseAndSet[END]));
    }

    /**
     * Will print out the passed in times to a text String.
     * @param inRise - Moonrise time
     * @param inSet  - Moonset time
     * @return String - "Sunrise at XX:XX and Sunset at XX:XX"
     */
    public static String toString(final double inRise, final double inSet) {
        StringBuilder text = new StringBuilder(27);
        if (inRise == ABOVE_HORIZON && inSet == ABOVE_HORIZON) {
            text.append("Sun is above horizon all day");
        }
        else
        if (inRise == BELOW_HORIZON && inSet == BELOW_HORIZON) {
            text.append("Sun does not rise today");
        }
        else {
            if (isEvent(inRise)) {
                text.append(convertToString(inRise, "Sunrise at "));
                if (isEvent(inSet)) {
                    text.append(convertToString(inSet, " and Sunset at "));
                }
                else {
                    text.append(" and Sun does not set");
                }
            }
            else {
                text.append(convertToString(inSet, "Sunset at "));
            }
        }
        return (text.toString());
    }

    /**
     * Returns a String representation of the time when Sunrise will occur.
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getSunrise(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude, SUNRISE_AND_SET)[START], "");
    }

    /**
     * Returns a String representation of the time when Sunset will occur.
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getSunset(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude, SUNRISE_AND_SET)[END], "");
    }

    /**
     * Returns a String representation of the time when dawn will occur.
     * (This value represents Civil Twilight.)
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getDawn(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude, CIVIL_TWILIGHT)[START], "");
    }

    /**
     * Returns a String representation of the time when dusk will occur.
     * (This value represents Civil Twilight.)
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getDusk(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude, CIVIL_TWILIGHT)[END], "");
    }


    /**
     * Compute the sine of the altitude of the object for this date, hour,
     * and location.
     * @param	inHour - Hour past midnight (for the current MJD)
     * @param	inMJD0		Modified Julian Date at midnight
     * @param	inLatitude 	Observer's latitude, North is positive.
     * @param	inLongitude	Observer's longitude, East is positive.
     * @result	The sine of the object's altitude above the horizon.
     */
    private static double sinAltitude(final double inHour, final double inMJD, final double inLatitude, final double inLongitude) {
        double mjd = inMJD + (inHour / 24.0);
        double[] sun = solarEphemeris(mjd);
        double TAU = 15.0 * (LMST(mjd, inLongitude) - sun[RA]);
        double result = AstroUtil.sin(inLatitude) * AstroUtil.sin(sun[DEC]) + (AstroUtil.cos(inLatitude) * AstroUtil.cos(sun[DEC]) * AstroUtil.cos(TAU));
        return (result);
    }

}
