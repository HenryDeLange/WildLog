package wildlog.ui.panels.bulkupload.helpers;

import javax.swing.Icon;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.panels.bulkupload.InfoBox;
import wildlog.utils.UtilsImageProcessing;


public class BulkUploadSightingWrapper extends Sighting {
    private Icon icon;
    private InfoBox infoBox;

    public BulkUploadSightingWrapper() {
    }

    public Icon getIcon() {
        if (icon == null) {
            icon = UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL);
        }
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
