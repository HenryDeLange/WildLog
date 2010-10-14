package wildlog.utils;

import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;

/**
 *
 * @author DeLange
 */
public final class LatLonConverter {

    public static double getDecimalDegree(Latitudes inLatitudes, int inDegrees, int inMinutes, float inSeconds) {
        if (inLatitudes != null) {
            if (inLatitudes.equals(Latitudes.SOUTH))
                return -1*(inDegrees + (inMinutes + inSeconds/60.0f)/60.0f);
            if (inLatitudes.equals(Latitudes.NORTH))
                return inDegrees + (inMinutes + inSeconds/60.0f)/60.0f;
        }
        return 0;
    }

    public static double getDecimalDegree(Longitudes inLongitudes, int inDegrees, int inMinutes, float inSeconds) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.EAST))
                return inDegrees + (inMinutes + inSeconds/60.0f)/60.0f;
            if (inLongitudes.equals(Longitudes.WEST))
                return -1*(inDegrees + (inMinutes + inSeconds/60.0f)/60.0f);
        }
        return 0;
    }

    public static int getDegrees(Latitudes inLatitudes, double inDecimalDegree) {
        if (inLatitudes != null) {
            if (inLatitudes.equals(Latitudes.SOUTH))
                return -1*(int)Math.abs(inDecimalDegree);
            if (inLatitudes.equals(Latitudes.NORTH))
                return (int)Math.abs(inDecimalDegree);
        }
        return 0;
    }

    public static int getMinutes(Latitudes inLatitudes, double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        return (int)(ddMinutes * 60);
    }

    public static float getSeconds(Latitudes inLatitudes, double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        double minutes = (int)(ddMinutes * 60);
        return (float)((ddMinutes * 60 - minutes) * 60.0);
    }

    public static int getDegrees(Longitudes inLongitudes, double inDecimalDegree) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.WEST))
                return -1*(int)Math.abs(inDecimalDegree);
            if (inLongitudes.equals(Longitudes.EAST))
                return (int)Math.abs(inDecimalDegree);
        }
        return 0;
    }

    public static int getMinutes(Longitudes inLongitudes, double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        return (int)(ddMinutes * 60);
    }

    public static float getSeconds(Longitudes inLongitudes, double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        double minutes = (int)(ddMinutes * 60);
        return (float)((ddMinutes * 60 - minutes) * 60.0);
    }

}
