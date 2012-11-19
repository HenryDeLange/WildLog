package wildlog.ui.panels.bulkupload.helpers;

import javax.swing.Icon;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.panels.bulkupload.InfoBox;


public class BulkUploadSightingWrapper extends Sighting {
    private Icon icon;
    private InfoBox infoBox;

    public BulkUploadSightingWrapper(Icon inIcon) {
        icon = inIcon;
//        setElementName("Unknown Creature");
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

    public InfoBox getInfoBox() {
        return infoBox;
    }

    public void setInfoBox(InfoBox inInfoBox) {
        infoBox = inInfoBox;
    }

}
