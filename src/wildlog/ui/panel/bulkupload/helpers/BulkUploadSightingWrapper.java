package wildlog.ui.panel.bulkupload.helpers;

import java.util.Date;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public class BulkUploadSightingWrapper extends Sighting {
    private String imagePath;

    public BulkUploadSightingWrapper(String inImagePath) {
        imagePath = inImagePath;
        setElementName("baaa");
        setDate(new Date());
        setLatitude(Latitudes.SOUTH);
        setLatDegrees(25);
        setLatMinutes(24);
        setLatSecondsFloat(23.2f);
        setLongitude(Longitudes.EAST);
        setLatDegrees(15);
        setLatMinutes(14);
        setLatSecondsFloat(13.2f);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String inImagePath) {
        imagePath = inImagePath;
    }

}
