package wildlog.astro;

import java.util.Date;

/**
 * AstroTimes.java:
 * Abstract class to hold shared functionality for MoonTimes and SunTimes.
 * <br/>
 * Loosely based on code by Martin Minow.
 */
public abstract class AstroTimes {
    // Variables:
    protected static final int RA = 0;
    protected static final int DEC = 1;
    private static final char NO_CHAR = (char) 0x00;
// HENRY - Self by gesit van internet af... Dit mag dalk verkeerd wees...
private static final double P2 = 6.283185307;
private static final double CosEPS = 0.91748;
private static final double SinEPS = 0.39778;
private static final double ARC = 206264.8062;
    /**
     * ABOVE_HORIZON is returned for sun and moon calculations where the
     * astronomical object does not cross the horizon.
     */
    public static final double ABOVE_HORIZON = Double.POSITIVE_INFINITY;
    /**
     * BELOW_HORIZON is returned for sun and moon calculations where the
     * astronomical object does not cross the horizon.
     */
    public static final double BELOW_HORIZON = Double.NEGATIVE_INFINITY;
    /**
     * The sunrise and moonrise algorithms return values in a double[] vector 
     * where result[START{0}] is the time of sun/moon rise.
     */
    public static final int START = 0;
    /**
     * The sunrise and moonrise algorithms return values in a double[] vector 
     * where result[END{1}] is the value of sun/moon set.
     */
    public static final int END = 1;

    
    protected AstroTimes() {
    }

    // Public Methods:
    public static boolean isEvent(final double value) {
        return (value != ABOVE_HORIZON && value != BELOW_HORIZON);
    }

    
    // Protected Methods:
    /**
     * Compute the string corresponding to this rise/set.
     * @param inEventValue - The time of rise or set
     * @param inEventDescription - This will be prepended to the time.
     * (example "Sunrise"/"Moonrise" or "Sunset"/"Moonset")
     * @return The proper string: null string if no event.
     */
    protected static String convertToString(final double inEventValue, final String inEventDescription) {
        if (isEvent(inEventValue)) {
            return (inEventDescription + hm(inEventValue));
        } else {
            return ("");
        }
    }
    
    /**
     * Convert a fractional hour value to its string representation
     * @param inFractionalHour - A fractional hour.
     * @return The string representation (hours and minutes only)
     */
    protected static String hm(final double inFractionalHour) {
        if (inFractionalHour == ABOVE_HORIZON) {
            return ("above");
        }
        else
        if (inFractionalHour == BELOW_HORIZON) {
            return ("below");
        }
        else {
            int hour = (int) Math.floor(inFractionalHour);
            double tempValue = (inFractionalHour - (double) hour) * 60.0;
            int minute = (int) (tempValue + 0.5);
            if (minute >= 60) {
                ++hour;
                minute -= 60;
            }
            if (hour >= 24) {
                hour -= 24;
            }
            return format(hour, '0', 2, ':') + format(minute, '0', 2, NO_CHAR);
        }
    }

    /**
     * Format an long value.
     * @param	inValue - The value to format.
     * @param	inFillChar - The fill character, generally space.
     * @param	inFieldWidth - A fixed-size field width (includes the fraction part, if specified).
     * A positive fieldWidth right-justifies the result while a negative fieldWidth left-justifies it.
     * @param	inTrailer - A character to append to the result. Format.NO_CHAR does not append anything.
     * @return  The string representation.
     */
    protected static String format(final long inValue, final char inFillChar, final int inFieldWidth, final char inTrailer) {
        StringBuilder result = new StringBuilder(Long.toString(inValue));
        if (inFieldWidth < 0) {
            int fixedFieldWidth = (-inFieldWidth);
            while (result.length() < fixedFieldWidth) {
                result.append(inFillChar);
            }
        } else {
            while (result.length() < inFieldWidth) {
                result.insert(0, inFillChar);
            }
        }
        if (inTrailer != NO_CHAR) {
            result.append(inTrailer);
        }
        return (result.toString());
    }

    /**
     * This is the low-precision solar ephemeris, MiniSun, from Astronomy
     * on the Personal Computer, p. 39. It is accurate to about 1'.
     * @param inMJD
     * @parameter MJD - The Modified Julian Date for the actual time to be computed.
     * @return Returns values: result[0] = rightAscension, result[1] = declination
     */
    protected static double[] solarEphemeris(final double inMJD) {
        double T = (inMJD - 51544.5) / 36525.0;
        double M = P2 * FRAC(0.993133 + 99.997361 * T);
        double DL = 6893.0 * Math.sin(M) + 72.0 * Math.sin(M * 2.0);
        double L = P2 * FRAC(0.7859453 + M / P2 + (6191.2 * T + DL) / 1296e3);
        double SL = Math.sin(L);
        double X = Math.cos(L);
        double Y = CosEPS * SL;
        double Z = SinEPS * SL;
        double RHO = Math.sqrt(1.0 - Z * Z);
        double[] result = new double[2];
        result[DEC] = (360.0 / P2) * Math.atan2(Z, RHO);
        result[RA] = (48.0 / P2) * Math.atan2(Y, (X + RHO));
        if (result[RA] < 0.0) {
            result[RA] += 24.0;
        }
        return (result);
    }

    /**
     * This is the low-precision lunar ephemeris, MiniMoon, from Astronomy
     * on the Personal Computer, p. 38. It is accurate to about 5'.
     * @param inMJD
     * @parameter actualMJD - The modified Julian Date for the actual time to be computed.
     * @return Returns values: result[0] = rightAscension, result[1] = declination
     */
    protected static double[] lunarEphemeris(final double inMJD) {
        double T = (inMJD - 51544.5) / 36525.0;
        double L0 = FRAC(0.606433 + 1336.855225 * T); // Mean longitude (revolutions)
        double L = P2 * FRAC(0.374897 + 1325.552410 * T); // Moon mean anomaly
        double LS = P2 * FRAC(0.993133 + 99.997361 * T); // SunTimes mean anomaly
        double D = P2 * FRAC(0.827361 + 1236.853086 * T); // Moon - SunTimes
        double F = P2 * FRAC(0.259086 + 1342.227825 * T); // mean latitude argument
        double DL = 22640 * Math.sin(L)
                - 4586 * Math.sin(L - 2 * D)
                + 2370 * Math.sin(2 * D)
                + 769 * Math.sin(2 * L)
                - 668 * Math.sin(LS)
                - 412 * Math.sin(2 * F)
                - 212 * Math.sin(2 * L - 2 * D)
                - 206 * Math.sin(L + LS - 2 * D)
                + 192 * Math.sin(L + 2 * D)
                - 165 * Math.sin(LS - 2 * D)
                - 125 * Math.sin(D)
                - 110 * Math.sin(L + LS)
                + 148 * Math.sin(L - LS)
                - 55 * Math.sin(2 * F - 2 * D);
        double S = F + (DL + 412 * Math.sin(2 * F) + 541 * Math.sin(LS)) / ARC;
        double H = F - 2 * D;
        double N = -526 * Math.sin(H)
                + 44 * Math.sin(L + H)
                - 31 * Math.sin(-L + H)
                - 23 * Math.sin(LS + H)
                + 11 * Math.sin(-LS + H)
                - 25 * Math.sin(-2 * L + F)
                + 21 * Math.sin(-L + F);
        double L_MOON = P2 * FRAC(L0 + DL / 1296.0E3); // L in radians
        double B_MOON = (18520.0 * Math.sin(S) + N) / ARC; // B in radians
        // Equatorial coordinates
        double CB = Math.cos(B_MOON);
        double X = CB * Math.cos(L_MOON);
        double V = CB * Math.sin(L_MOON);
        double W = Math.sin(B_MOON);
        double Y = CosEPS * V - SinEPS * W;
        double Z = SinEPS * V + CosEPS * W;
        double RHO = Math.sqrt(1.0 - Z * Z);
        double[] result = new double[2];
        result[DEC] = (360.0 / P2) * Math.atan(Z / RHO);
        result[RA] = (48.0 / P2) * Math.atan(Y / (X + RHO));
        if (result[RA] < 0.0) {
            result[RA] += 24.0;
        }
        return (result);
    }

    /**
     * Convert a Date at midnight of this day to the equivalent Modified
     * Julian Date. Note that this will be referenced to the local timezone.
     * <br/>
     * <b>(This can't be used for historical dates)</b>
     * @param inDate - The Java date
     * @return modified Julian Date
     */
    protected static double midnightMJD(final Date inDate) {
        double result = Math.floor(AstroUtil.MJD(inDate));
        return (result);
    }

    /**
     * Pascal fraction function. Rounds towards zero.
     * @param	value
     * @return	integer (rounded towards zero)
     */
    protected static double FRAC(final double value) {
        double result = value - TRUNC(value);
        if (result < 0.0) {
            result += 1.0;
        }
        return (result);
    }

    /**
     * Pascal truncate function. Returns the integer nearest
     * to zero. (This behaves differently than C/Java Math.floor()
     * for negative values.)
     * @param	inValue - The value to convert
     * @return	Integral value nearest zero
     * @see		java.Math.floor
     */
    protected static double TRUNC(final double inValue) {
        double result = Math.floor(Math.abs(inValue));
        if (inValue < 0.0) {
            result = (-result);
        }
        return (result);
    }

    /**
     * Compute Local Mean Sidereal Time (LMST).
     * (Note: While Astronomy on the Personal Computer reckons longitude
     * positive towards the West, this routine reckons it positive
     * towards the East.)
     * @param	inMJD - Modified Julian Day number
     * @param	inLongitude - Longitude in degrees, East is positive.
     * @return				The local mean sidereal time.
     * @see		Section 3.3 (p41) in Astronomy on the Personal Computer.
     */
    protected static double LMST(final double inMJD, final double inLongitude) {
        double MJD0 = Math.floor(inMJD);
        double UT = (inMJD - MJD0) * 24.0;
        double T = (MJD0 - 51544.5) / 36525.0;
        double GMST = 6.697374558 + 1.0027379093 * UT
                + (8640184.812866 + (0.093104 - 6.2E-6 * T) * T) * T / 3600.0;
        double LMST = 24.0 * FRAC((GMST + inLongitude / 15.0) / 24.0);
        return (LMST);
    }

    /*
     * Modulus function that always returns a positive value. For example,
     * AstroMath.mod(-3, 24) == 21
     */
    protected static double mod(final double inNumerator, final double inDenomenator) {
        double result = Math.IEEEremainder(inNumerator, inDenomenator);
        if (result < 0.0) {
            result += inDenomenator;
        }
        return (result);
    }

}
