package wildlog.ui.maps.implementations.helpers;

import java.io.InputStream;
import java.nio.file.Path;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsMaps {

    private UtilsMaps() {
    }

     public static void copyMapLayers() {
        WildLogApp.LOGGER.log(Level.INFO, "Start copying Map Layers");
        // GeoTiffs
        copyGeoTiff(BundledMapLayers.ALTITUDE);
        copyGeoTiff(BundledMapLayers.CLIMATE_PRECIPITATION_AVERAGE);
        copyGeoTiffMonths(BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY);
        copyGeoTiffMonths(BundledMapLayers.CLIMATE_TEMPERATURE_MAX);
        copyGeoTiffMonths(BundledMapLayers.CLIMATE_TEMPERATURE_MEAN);
        copyGeoTiffMonths(BundledMapLayers.CLIMATE_TEMPERATURE_MIN);
        copyGeoTiff(BundledMapLayers.EARTH_HISTORIC_IDEAL);
        copyGeoTiff(BundledMapLayers.EARTH_MODERN);
        copyGeoTiff(BundledMapLayers.FARMING_CROPS);
        copyGeoTiff(BundledMapLayers.FORESTS);
        copyGeoTiff(BundledMapLayers.HUMAN_INFLUENCE);
        copyGeoTiff(BundledMapLayers.HUMAN_POPULATION);
        copyGeoTiff(BundledMapLayers.PROTECTED_AREAS_WORLD);
        // Shapefiles
        copyShapefiles(BundledMapLayers.BASE_CITIES);
        copyShapefiles(BundledMapLayers.BASE_LAKES);
        copyShapefiles(BundledMapLayers.BASE_RIVERS);
        copyShapefiles(BundledMapLayers.BASE_WORLD);
        copyShapefiles(BundledMapLayers.BIOMES_LOCAL);
        copyShapefiles(BundledMapLayers.BIOMES_LOCAL_GROUPS);
        copyShapefiles(BundledMapLayers.BIOMES_WORLD);
        copyShapefiles(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL);
        copyShapefiles(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL);
        WildLogApp.LOGGER.log(Level.INFO, "Done copying Map Layers");
    }
    
    private static void copyGeoTiff(BundledMapLayers inBundledMapLayers) {
        copySingleFile(inBundledMapLayers.getRelativePath());
    }
    
    private static void copyGeoTiffMonths(BundledMapLayers inBundledMapLayers) {
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(0));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(1));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(2));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(3));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(4));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(5));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(6));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(7));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(8));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(9));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(10));
        copySingleFile(inBundledMapLayers.getRelativePathForMonth(11));
    }
    
    private static void copyShapefiles(BundledMapLayers inBundledMapLayers) {
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".dbf"));
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".prj"));
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".shp"));
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".shx"));
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".sld"));
        copySingleFile(inBundledMapLayers.getRelativePath().getParent().resolve(
            inBundledMapLayers.getRelativePath().getFileName().toString()
                .substring(0, inBundledMapLayers.getRelativePath().getFileName().toString().lastIndexOf('.')) + ".qix"));
    }
    
    private static void copySingleFile(Path inPath) {
        InputStream inputStream = BundledMapLayers.class.getResourceAsStream("/" + inPath.toString().replace('\\', '/'));
        if (inputStream != null) {
            UtilsFileProcessing.createFileFromStream(inputStream, WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inPath));
        }
        else {
            if (!inPath.getFileName().toString().endsWith(".sld")) {
                WildLogApp.LOGGER.log(Level.ERROR, "Problem copying Map Layer (can't find in JAR): {}", inPath);
            }
            else {
                WildLogApp.LOGGER.log(Level.WARN, "No default style found for Map Layer: {}", inPath);
            }
        }
    }
    
    /**
     * Hierdie replace behoort baie vinniger te wees as die String.replace() method van Java wat onderliggend regular expressions gebruik.
     */
    public static String replace(String inText, String inOldString, String inNewString) {
        if (inText != null) {
            int foundIndex = inText.indexOf(inOldString, 0);
            if (foundIndex >= 0) {
                char[] sourceArray = inText.toCharArray();
                StringBuilder builder = new StringBuilder(sourceArray.length);
                int startIndex = 0;
                // Replace all the occurences
                do {
                    builder.append(sourceArray, startIndex, foundIndex - startIndex)
                           .append(inNewString);
                    startIndex = foundIndex + inOldString.length();
                    foundIndex = inText.indexOf(inOldString, startIndex);
                }
                while (foundIndex > 0);
                // Add the last part
                builder.append(sourceArray, startIndex, sourceArray.length - startIndex);
                return builder.toString();
            }
            else {
                return inText;
            }
        }
        return null;
    }

}
