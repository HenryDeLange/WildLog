package wildlog.astro;

import java.util.Date;
import java.util.TimeZone;

/**
 * Compute the times of moonrise and moonset for a specified date and location.
 * 
 * <br/>
 * Loosely based on code by Martin Minow.
 * He based it on algorithms from "Astronomy on the Personal Computer" by
 * Oliver Montenbruck and Thomas Pfleger. Springer Verlag 1994.
 * ISBN 3-540-57700-9.
 * 
 * <br/>
 * <b>NOTE: This can't be used for historical dates.</b>
 * <b>NOTE: This does not take the altitude into account.</b>
 * <b>NOTE: This does not take the time zone or daylight savings into account.</b>
 */
public final class MoonTimes extends AstroTimes {
    private static final double MOONRISE = AstroUtil.sin(+8.0 / 60.0);

    /**
     * This is a static class: the constructor should not be used...
     */
    private MoonTimes() {
    }

    
    /**
     * Compute the times of moonrise and moonset for a specific date and location.
     * <br/>
     * There are days each month when the moon rises and does not set
     * (and vice-versa). 
     * Note the following special result values:
     * <pre>
     *	ABOVE_HORIZON		The moon does not set
     *	BELOW_HORIZON		The moon does not rise
     * </pre>
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return	double[] - double[START{0}] is the time of moonrise and double[END{1}] is the time of moonset
     */
    public static double[] calculateTimes(final Date inDate, final double inLatitude, final double inLongitude) {
        double DATE = midnightMJD(inDate);
        double ZONE = ((double) TimeZone.getDefault().getRawOffset()) / 86400000.0;
        DATE -= ZONE;
        double sinLatitude = AstroUtil.sin(inLatitude);
        double cosLatitude = AstroUtil.cos(inLatitude);
        double yMinus = sinAltitude(DATE, 0.0, inLongitude, cosLatitude, sinLatitude) - MOONRISE;
        boolean aboveHorizon = (yMinus > 0.0);
        double rise = BELOW_HORIZON;
        double set = BELOW_HORIZON;
        if (aboveHorizon) {
            rise = ABOVE_HORIZON;
            set = ABOVE_HORIZON;
        }
        for (double hour = 1.0; hour <= 24.0; hour += 2.0) {
            double yThis = sinAltitude(DATE, hour, inLongitude, cosLatitude, sinLatitude) - MOONRISE;
            double yPlus = sinAltitude(DATE, hour + 1.0, inLongitude, cosLatitude, sinLatitude) - MOONRISE;
            /* .________________________________________________________________.
             * | Quadratic interpolation through the three points:		|
             * | [-1, yMinus], [0, yThis], [+1, yNext]				|
             * | (These must not lie on a straight line.)			|
             * | Note: I've in-lined this as it returns several values.		|
             * .________________________________________________________________.
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
            if (discriminant >= 0.0) {	// Intersects x-axis?
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
             * | nRoots	   Number of roots found (0, 1, or 2)			|
             * |	     If nRoots == zero, there is no event in this range.|
             * | root1	   First root (nRoots >= 1)				|
             * | root2	   Second root (nRoots == 2)				|
             * | yMinus	   Y-value at interpolation start. If < 0, root1 is	|
             * |	     a moonrise event.					|
             * | yExtreme  Maximum value of y (nRoots == 2) -- this determines	|
             * |	     whether a 2-root event is a rise-set or a set-rise.|
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
        result[START] = rise;
        result[END] = set;
        return (result);
    }

    /**
     * Convenience method for MoonTimes.toString(double inRise, double inSet).
     * @param inRiseAndSet - The array from
     * @return String - Similar to MoonTimes.toString(double inRise, double inSet)
     */
    public static String toString(double[] inRiseAndSet) {
        return (toString(inRiseAndSet[START], inRiseAndSet[END]));
    }

    /**
     * Will print out the passed in times to a text String.
     * @param inRise - Moonrise time
     * @param inSet  - Moonset time
     * @return String - "Moonrise at XX:XX and Moonset at XX:XX"
     */
    public static String toString(final double inRise, final double inSet) {
        if (isEvent(inRise)) {
            if (isEvent(inSet)) {
                if (inRise <= inSet) {
                    return (convertToString(inRise, "Moonrise at ") + convertToString(inSet, " and Moonset at "));
                }
                else {
                    return (convertToString(inSet, "Moonset at ") + convertToString(inRise, " and Moonrise at "));
                }
            }
            else {
                return (convertToString(inRise, "Moonrise at "));
            }
        } 
        else
        if (isEvent(inSet)) {
            return (convertToString(inSet, "Moonset at "));
        }
        else {
            return ("");
        }
    }

    /**
     * Returns a String representation of the time when Moonrise will occur.
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getMoonrise(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude)[START], "");
    }

    /**
     * Returns a String representation of the time when Moonset will occur.
     * @param	inDate - Java date
     * @param	inLatitude - Observer's latitude (North is positive).
     * @param	inLongitude - Observer's longitude (East is positive).
     * @return  String - The time of the event
     */
    public static String getMoonset(final Date inDate, final double inLatitude, final double inLongitude) {
        return convertToString(calculateTimes(inDate, inLatitude, inLongitude)[END], "");
    }


    /**
     * Compute the sine of the altitude of the object for this date, hour
     * and location. cosLatitude and sinLatitude pre-compute the observer's
     * location.
     * @param	inMJD0		Modified Julian Date at midnight
     * @param	inHour		Hour past midnight.
     * @param	inLongitude	Observer's longitude, East is positive.
     * @param	inCosLatitude	Cosine(observer's latitude)
     * @param	inSinLatitude	Sine(observer's latitude)
     * @result	The sine of the object's altitude above the horizon.
     */
    private static double sinAltitude(final double inMJD0, final double inHour, final double inLongitude, final double inCosLatitude, final double inSinLatitude) {
        double MJD = inMJD0 + (inHour / 24.0);
        double[] moon = lunarEphemeris(MJD);
        double TAU = 15.0 * (LMST(MJD, inLongitude) - moon[RA]);
        double result = inSinLatitude * AstroUtil.sin(moon[DEC]) + inCosLatitude * AstroUtil.cos(moon[DEC]) * AstroUtil.cos(TAU);
        return (result);
    }

}
