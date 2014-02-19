package wildlog.mapping.gpx;

import com.topografix.gpx._1._1.WptType;
import java.io.File;
import java.util.List;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.utils.UtilsGps;


public class UtilsGPX {

    public static void populateGPSFromGpxFile(File inGpxFile, String inTagName, DataObjectWithGPS inDataObjectWithGPS) {
        List<WptType> listWaypoints = GpxReader.getWaypoints(inGpxFile, inTagName);
        for(WptType waypoint : listWaypoints) {
            if (waypoint.getName() != null && waypoint.getName().equalsIgnoreCase(inTagName)) {
                // Get the lat
                if (waypoint.getLat().signum() >= 0)
                    inDataObjectWithGPS.setLatitude(Latitudes.NORTH);
                else
                    inDataObjectWithGPS.setLatitude(Latitudes.SOUTH);
                // Get the lon
                if (waypoint.getLon().signum() >= 0)
                    inDataObjectWithGPS.setLongitude(Longitudes.EAST);
                else
                    inDataObjectWithGPS.setLongitude(Longitudes.WEST);
                // Get the values
                double decimalLat = waypoint.getLat().doubleValue();
                double decimalLon = waypoint.getLon().doubleValue();
                inDataObjectWithGPS.setLatDegrees(UtilsGps.getDegrees(Latitudes.NONE, decimalLat));
                inDataObjectWithGPS.setLonDegrees(UtilsGps.getDegrees(Longitudes.NONE, decimalLon));
                inDataObjectWithGPS.setLatMinutes(UtilsGps.getMinutes(decimalLat));
                inDataObjectWithGPS.setLonMinutes(UtilsGps.getMinutes(decimalLon));
                inDataObjectWithGPS.setLatSeconds(UtilsGps.getSeconds(decimalLat));
                inDataObjectWithGPS.setLonSeconds(UtilsGps.getSeconds(decimalLon));
                // Get the accuracy (There doen't seem to be a standard value for accuracy in GPX 1.1). Assuming it's decent.
                inDataObjectWithGPS.setGPSAccuracy(GPSAccuracy.GOOD);
                break;
            }
        }
    }

}
