package wildlog.maps.utils;

import java.text.DecimalFormat;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public final class UtilsGPS {
    public static final String NO_GPS_POINT = "No GPS";
    public static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = new ThreadLocal<DecimalFormat>() {
         @Override
         protected DecimalFormat initialValue() {
             return new DecimalFormat("#.##########");
         } 
    };

    private UtilsGPS() {
    }

    public static double getLatDecimalDegree(DataObjectWithGPS inDataObjectWithGPS) {
        return getDecimalDegree(
                inDataObjectWithGPS.getLatitude(),
                inDataObjectWithGPS.getLatDegrees(),
                inDataObjectWithGPS.getLatMinutes(),
                inDataObjectWithGPS.getLatSeconds());
    }

    public static double getLonDecimalDegree(DataObjectWithGPS inDataObjectWithGPS) {
        return getDecimalDegree(
                inDataObjectWithGPS.getLongitude(),
                inDataObjectWithGPS.getLonDegrees(),
                inDataObjectWithGPS.getLonMinutes(),
                inDataObjectWithGPS.getLonSeconds());
    }

    public static double getDecimalDegree(Latitudes inLatitudes, int inDegrees, int inMinutes, double inSeconds) {
        if (inLatitudes != null) {
            if (inLatitudes.equals(Latitudes.SOUTH)) {
                return -1.0*(inDegrees + (inMinutes + inSeconds/60.0)/60.0);
            }
            if (inLatitudes.equals(Latitudes.NORTH)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
            if (inLatitudes.equals(Latitudes.NONE)) {
                if (inDegrees < 0.0) {
                    return -1.0*(Math.abs(inDegrees) + (inMinutes + inSeconds/60.0)/60.0);
                }
                else {
                    return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
                }
            }
        }
        return 0.0;
    }

    public static double getDecimalDegree(Longitudes inLongitudes, int inDegrees, int inMinutes, double inSeconds) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.EAST)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
            if (inLongitudes.equals(Longitudes.WEST)) {
                return -1.0*(inDegrees + (inMinutes + inSeconds/60.0)/60.0);
            }
            if (inLongitudes.equals(Longitudes.NONE)) {
                if (inDegrees < 0.0) {
                    return -1.0*(Math.abs(inDegrees) + (inMinutes + inSeconds/60.0)/60.0);
                }
                else {
                    return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
                }
            }
        }
        return 0.0;
    }

    public static int getDegrees(Latitudes inLatitudes, double inDecimalDegree) {
        if (inLatitudes != null) {
            if (inLatitudes.equals(Latitudes.SOUTH)) {
                return -1*(int)Math.abs(inDecimalDegree);
            }
            if (inLatitudes.equals(Latitudes.NORTH)) {
                return (int)Math.abs(inDecimalDegree);
            }
            if (inLatitudes.equals(Latitudes.NONE)) {
                return (int)Math.abs(inDecimalDegree);
            }
        }
        return 0;
    }

    public static int getMinutes(double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        return (int)(ddMinutes * 60);
    }

    public static double getSeconds(double inDecimalDegree) {
        double degrees = (int)Math.abs(inDecimalDegree);
        double ddMinutes = (Math.abs(inDecimalDegree) - Math.abs(degrees));
        double minutes = (int)(ddMinutes * 60);
        return (ddMinutes * 60.0 - minutes) * 60.0;
    }

    public static int getDegrees(Longitudes inLongitudes, double inDecimalDegree) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.WEST)) {
                return -1*(int)Math.abs(inDecimalDegree);
            }
            if (inLongitudes.equals(Longitudes.EAST)) {
                return (int)Math.abs(inDecimalDegree);
            }
            if (inLongitudes.equals(Longitudes.NONE)) {
                return (int)Math.abs(inDecimalDegree);
            }
        }
        return 0;
    }

    public static String getLatitudeString(DataObjectWithGPS inDataObjectWithGPS) {
        if (inDataObjectWithGPS.getLatitude() != null && !Latitudes.NONE.equals(inDataObjectWithGPS.getLatitude())) {
            return inDataObjectWithGPS.getLatitude().getKey() + " " + DECIMAL_FORMAT.get().format(
                    getDecimalDegree(
                        Latitudes.NONE,
                        inDataObjectWithGPS.getLatDegrees(),
                        inDataObjectWithGPS.getLatMinutes(),
                        inDataObjectWithGPS.getLatSeconds()));
        }
        else {
            return NO_GPS_POINT;
        }
    }

    public static String getLongitudeString(DataObjectWithGPS inDataObjectWithGPS) {
        if (inDataObjectWithGPS.getLongitude() != null && !Longitudes.NONE.equals(inDataObjectWithGPS.getLongitude())) {
            return inDataObjectWithGPS.getLongitude().getKey() + " " + DECIMAL_FORMAT.get().format(
                    getDecimalDegree(
                        Longitudes.NONE,
                        inDataObjectWithGPS.getLonDegrees(),
                        inDataObjectWithGPS.getLonMinutes(),
                        inDataObjectWithGPS.getLonSeconds()));
        }
        else {
            return NO_GPS_POINT;
        }
    }

    public static void copyGpsBetweenDOs(DataObjectWithGPS inToGpsDO, DataObjectWithGPS inFromGpsDO) {
        if (inFromGpsDO != null && inToGpsDO != null) {
            inToGpsDO.setLatitude(inFromGpsDO.getLatitude());
            inToGpsDO.setLatDegrees(inFromGpsDO.getLatDegrees());
            inToGpsDO.setLatMinutes(inFromGpsDO.getLatMinutes());
            inToGpsDO.setLatSeconds(inFromGpsDO.getLatSeconds());
            inToGpsDO.setLongitude(inFromGpsDO.getLongitude());
            inToGpsDO.setLonDegrees(inFromGpsDO.getLonDegrees());
            inToGpsDO.setLonMinutes(inFromGpsDO.getLonMinutes());
            inToGpsDO.setLonSeconds(inFromGpsDO.getLonSeconds());
        }
    }
    
    public static boolean hasGPSData(DataObjectWithGPS inDataObject) {
        return inDataObject != null && getLatDecimalDegree(inDataObject) != 0 && getLonDecimalDegree(inDataObject) != 0;
    }
    
    public static boolean isSightingInBox(DataObjectWithGPS inDataObject, double inNorthEast_Lat, double inNorthEast_Lon, double inSouthWest_Lat, double inSouthWest_Lon) {
        if (!UtilsGPS.hasGPSData(inDataObject)) {
            return false;
        }
        // Adjust all the GPS values to make sure they are positive numbers, then it is simpler to do the comparisons
        double lat = getLatDecimalDegree(inDataObject) + 1000;
        double lon = getLonDecimalDegree(inDataObject) + 1000;
        if ((lat <= (inNorthEast_Lat + 1000)) && (lat >= (inSouthWest_Lat + 1000))
                && (lon <= (inNorthEast_Lon + 1000)) && (lon >= (inSouthWest_Lon + 1000))) {
            return true;
        }
        return false;
    }

}
