package wildlog.mapping.utils;

import java.awt.Color;
import java.io.File;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;


public final class UtilsMapGenerator {

    public static void addPoint(final double inLatitude, final double inLongitude, final Color inColor, DataObjectWithHTML inObjectWithHTML, WildLogApp inApp, boolean inIsDistributionMap) {
        if (!inApp.getWildLogOptions().isIsOnlinemapTheDefault() || inIsDistributionMap) {
            inApp.getMapOffline().addPoint(inLatitude, inLongitude, inColor);
        }
        else {
            inApp.getMapOnline().getPointLayer().addPoint(inLatitude, inLongitude, inColor, inObjectWithHTML, inApp);
        }
    }

    public static void clearMap(WildLogApp inApp, boolean inIsDistributionMap) {
        if (!inApp.getWildLogOptions().isIsOnlinemapTheDefault() || inIsDistributionMap) {
            inApp.getMapOffline().clearDistributionMap();
            inApp.getMapOffline().clearPoints();
        }
        else {
            inApp.getMapOnline().getPointLayer().clearPoints();
            inApp.clearOnlinemap();
        }
    }

    public static void addDistributionMap(WildLogApp inApp, File inFile) {
        inApp.getMapOffline().clearDistributionMap();
        inApp.getMapOffline().addDistributionMap(inFile);
    }

}
