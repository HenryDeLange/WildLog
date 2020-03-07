package wildlog.maps.gpx;

import com.topografix.gpx._1._1.WptType;
import java.nio.file.Path;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.helpers.WLOptionPane;


public class UtilsGPX {

    private UtilsGPX() {
    }

    public static void populateGPSFromGpxFile(Path inGpxFile, String inTagName, DataObjectWithGPS inDataObjectWithGPS, JDialog inParent) {
        WptType waypoint = GpxReader.getSpecificWaypoint(inGpxFile, inTagName);
        if (waypoint != null && waypoint.getName() != null && waypoint.getName().equalsIgnoreCase(inTagName)) {
            // Get the lat
            if (waypoint.getLat().signum() >= 0) {
                inDataObjectWithGPS.setLatitude(Latitudes.NORTH);
            } else {
                inDataObjectWithGPS.setLatitude(Latitudes.SOUTH);
            }
            // Get the lon
            if (waypoint.getLon().signum() >= 0) {
                inDataObjectWithGPS.setLongitude(Longitudes.EAST);
            } else {
                inDataObjectWithGPS.setLongitude(Longitudes.WEST);
            }
            // Get the values
            double decimalLat = waypoint.getLat().doubleValue();
            double decimalLon = waypoint.getLon().doubleValue();
            inDataObjectWithGPS.setLatDegrees(UtilsGPS.getDegrees(Latitudes.NONE, decimalLat));
            inDataObjectWithGPS.setLonDegrees(UtilsGPS.getDegrees(Longitudes.NONE, decimalLon));
            inDataObjectWithGPS.setLatMinutes(UtilsGPS.getMinutes(decimalLat));
            inDataObjectWithGPS.setLonMinutes(UtilsGPS.getMinutes(decimalLon));
            inDataObjectWithGPS.setLatSeconds(UtilsGPS.getSeconds(decimalLat));
            inDataObjectWithGPS.setLonSeconds(UtilsGPS.getSeconds(decimalLon));
            // Get the accuracy (There doen't seem to be a standard value for accuracy in GPX 1.1). Assuming it's decent.
            inDataObjectWithGPS.setGPSAccuracy(GPSAccuracy.GOOD);
            inDataObjectWithGPS.setGPSAccuracyValue(GPSAccuracy.GOOD.getMaxMeters());
        }
        else {
            WLOptionPane.showMessageDialog(inParent, 
                    "Not GPX Waypoint could be found for: " + inTagName, 
                    "No Waypoint", JOptionPane.WARNING_MESSAGE);
        }
    }

}
