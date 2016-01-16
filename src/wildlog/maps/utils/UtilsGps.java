package wildlog.maps.utils;

import java.text.DecimalFormat;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public final class UtilsGps {
    public static final String NO_GPS_POINT = "No GPS";
    public static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = new ThreadLocal<DecimalFormat>() {
         @Override
         protected DecimalFormat initialValue() {
             return new DecimalFormat("#.##########");
         } 
    };

    private UtilsGps() {
    }

// TODO: Maak 'n nuwe method hasGPSData(..) wat boolean return
    
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
                return -1*(inDegrees + (inMinutes + inSeconds/60.0)/60.0);
            }
            if (inLatitudes.equals(Latitudes.NORTH)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
            if (inLatitudes.equals(Latitudes.NONE)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
        }
        return 0;
    }

    public static double getDecimalDegree(Longitudes inLongitudes, int inDegrees, int inMinutes, double inSeconds) {
        if (inLongitudes != null) {
            if (inLongitudes.equals(Longitudes.EAST)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
            if (inLongitudes.equals(Longitudes.WEST)) {
                return -1*(inDegrees + (inMinutes + inSeconds/60.0)/60.0);
            }
            if (inLongitudes.equals(Longitudes.NONE)) {
                return inDegrees + (inMinutes + inSeconds/60.0)/60.0;
            }
        }
        return 0;
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
        return (double)((ddMinutes * 60.0 - minutes) * 60.0);
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

}