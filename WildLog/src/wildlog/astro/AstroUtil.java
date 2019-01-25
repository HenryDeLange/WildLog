package wildlog.astro;

import java.util.Date;
import java.util.TimeZone;

/**
 * AstroUtil.java:
 * Contains a set of shared static functions that simplify astronomical
 * computations.
 *<br/>
 * Loosely based on code by Martin Minow.
 */
public final class AstroUtil {
    // Variables:
    private static final double msecPerDay = 86400000;
    private static final double javaEpochMJD = 40587.0;
    // Degrees -> Radians:  degree * DegRad
    // Radians -> Degrees:  radians / DegRad
    private static final double DegRad = (Math.PI / 180.0);
    
    
    private AstroUtil() {
    }

    // Public Methods:
    /**
     * Convert a Date to the equivalent Modified Julian Date. Note that
     * this will be referenced to the local timezone.
     * <br/>
     * <b>(This can't be used for historical dates)</b>
     * @param inDate - The Java date
     * @return modified Julian Date
     */
    public static double MJD(final Date inDate) {
        long localTime = inDate.getTime() + getSystemTimezone();
        double result = ((double) localTime) / msecPerDay;
        result += javaEpochMJD;
        return (result);
    }

    public static double sin(final double inValue) {
        return (Math.sin(inValue * DegRad));
    }

    public static double cos(final double inValue) {
        return (Math.cos(inValue * DegRad));
    }

    public static double acos(final double inValue) {
        return (Math.acos(inValue) / DegRad);
    }

    
    // Private Methods:
    /**
     * Return the system timezone in milliseconds, adjusted for Daylight
     * Savings Time if necessary. Use Java 1.1+ getRawOffset if it's available.
     * Get the offset between the local time and GMT. Offset is
     * the number of milliseconds to add to the local time to get GMT.
     */
    private static long getSystemTimezone() {
        // HENRY (Ek het SimpleTimeZone vervang met TimeZone)
        TimeZone tz = TimeZone.getDefault();
        long tzOffset = tz.getRawOffset();
        if (tz.inDaylightTime(new Date())) {
            tzOffset += 3600000;
        }
        return (tzOffset);
    }

}
