package wildlog.astro;

import java.util.Date;
/**
 * MoonPhase.java:
 * Compute the phase of the moon for a specified date.
 * <br/>
 * Loosely based on code by Martin Minow.
 * He based it on algorithms from "Practical Astronomy With Your Calculator" by Patrick
 * Duffett-Smith, Second Edition, Cambridge University Press, 1981 and MoonTool
 * <br/>
 * <b>(This can't be used for historical dates)</b>
 */
public final class MoonPhase {
    // Variables:
    private static final double epoch = 2444238.5 - 2400000.5;
    private static final double elonge = 278.833540;
    private static final double elongp = 282.596403;
    private static final double eccent = 0.016718;
    private static final double mmlong = 64.975464;
    private static final double mmlongp = 349.383063;
    private static final double kEpsilon = 1.0e-6;
    private static double DegRad = Math.PI / 180.0;

    // Constructors:
    /**
     * This is a static class: the constructor should not be used...
     */
    public MoonPhase() {
    }


    // Public methods:
    /**
     * Calculate the phase of the moon for the given date.
     * @param inDate - The given java date
     * @return The fraction of the moon that is full. Note that this value
     * still needs to be multiplied by 100 to get the percentage.
     */
    public static double calculatePhase(final Date inDate) {
        double MJD = AstroUtil.MJD(inDate);
        double Day = MJD - epoch;
        double N = fixAngle((360.0 / 365.2422) * Day);
        double M = fixAngle(N + elonge - elongp);
        double Ec = kepler(M, eccent);
        Ec = Math.sqrt((1.0 + eccent) / (1.0 - eccent)) * Math.tan(Ec / 2.0);
        Ec = 2.0 * Math.atan(Ec) / DegRad;
        double Lambdasun = fixAngle(Ec + elongp);
        double ml = fixAngle(13.1763966 * Day + mmlong);
        double MM = fixAngle(ml - 0.1114041 * Day - mmlongp);
        double Ev = 1.2739 * AstroUtil.sin(2.0 * (ml - Lambdasun) - MM);
        double Ae = 0.1858 * AstroUtil.sin(M);
        double A3 = 0.37 * AstroUtil.sin(M);
        double MmP = MM + Ev - Ae - A3;
        double mEc = 6.2886 * AstroUtil.sin(MmP);
        double A4 = 0.214 * AstroUtil.sin(2 * MmP);
        double lP = ml + Ev + mEc - Ae + A4;
        double V = 0.6583 * AstroUtil.sin(2 * (lP - Lambdasun));
        double lPP = lP + V;
        double MoonAgeDegrees = lPP - Lambdasun;
        double MoonPhase = (1.0 - AstroUtil.cos(MoonAgeDegrees)) / 2.0;
        return MoonPhase;
    }

    public static String toString(final double inPhase) {
        return "Moonphase: " + ((int) (inPhase * 100.0)) + "% full";
    }

    // Private methods:
    private static double kepler(final double inM, final double inEcc) {
        double tempM = inM;
        tempM *= DegRad;
        double e = tempM;
        for (double delta = 1.0; Math.abs(delta) > kEpsilon;) {
            delta = e - inEcc * Math.sin(e) - tempM;
            e -= delta / (1 - inEcc * Math.cos(e));
        }
        return (e);
    }

    private static double fixAngle(final double inAngle) {
        double result = inAngle - 360.0 * (Math.floor(inAngle / 360.0));
        return (result);
    }

}
