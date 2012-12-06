package wildlog.mapping.gpx;

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.WptType;
import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.utils.UtilsGps;


public class GpxReader {

    public static void populateGPSFromGpxFile(File inGpxFile, String inTagName, DataObjectWithGPS inDataObjectWithGPS) {
        GpxType gpx = null;
        try {
            JAXBContext jc = JAXBContext.newInstance("com.topografix.gpx._1._1");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<GpxType> root = (JAXBElement<GpxType>)unmarshaller.unmarshal(inGpxFile);
            gpx = root.getValue();
        }
        catch(JAXBException ex) {
           ex.printStackTrace(System.err);
        }
        if (gpx != null) {
            List<WptType> waypoints = gpx.getWpt();
            for(WptType waypoint : waypoints) {
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
                    break;
                }
            }
        }
    }

}
