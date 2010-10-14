package wildlog.utils.ui;

import java.awt.Color;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;

/**
 *
 * @author Henry
 */
public final class UtilMapGenerator {

    public static void addPoint(final Float inLatitude, final Float inLongitude, final Color inColor, DataObjectWithHTML inObjectWithHTML, WildLogApp inApp) {
        if (inApp.isUseOnlineMap()) {
            inApp.getMapOnline().getPointLayer().addPoint(inLatitude, inLongitude, inColor, inObjectWithHTML, inApp);
        }
        else {
            inApp.getMapOffline().addPoint(inLatitude, inLongitude, inColor);
        }
    }

    public static void clearMap(WildLogApp inApp) {
        if (inApp.isUseOnlineMap()) {
            inApp.getMapOnline().getPointLayer().clearPoints();
            inApp.clearOnlinemap();
        }
        else {
            inApp.getMapOffline().clearPoints();
        }
    }

}
