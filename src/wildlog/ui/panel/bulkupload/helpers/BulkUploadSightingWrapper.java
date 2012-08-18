package wildlog.ui.panel.bulkupload.helpers;

import javax.swing.Icon;
import wildlog.data.dataobjects.Sighting;


public class BulkUploadSightingWrapper extends Sighting {
    private Icon icon;

    public BulkUploadSightingWrapper(Icon inIcon) {
        icon = inIcon;
        setElementName("<no name>");
//        setDate(new Date());
//        setLatitude(Latitudes.SOUTH);
//        setLatDegrees(25);
//        setLatMinutes(24);
//        setLatSecondsFloat(23.2f);
//        setLongitude(Longitudes.EAST);
//        setLatDegrees(15);
//        setLatMinutes(14);
//        setLatSecondsFloat(13.2f);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon inIcon) {
        icon = inIcon;
    }

}
