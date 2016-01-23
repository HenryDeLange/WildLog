package wildlog.ui.maps.implementations.helpers;

import java.io.InputStream;
import java.nio.file.Path;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsMapGenerator {

    private UtilsMapGenerator() {
    }

     public static void copyMapLayers() {
        System.out.println("Start copying Map Layers");
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
        copyShapefiles(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL);
        copyShapefiles(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL);
        System.out.println("Done copying Map Layers");
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
    }
    
    private static void copySingleFile(Path inPath) {
        InputStream inputStream = BundledMapLayers.class.getResourceAsStream("/" + inPath.toString().replace('\\', '/'));
        if (inputStream != null) {
            UtilsFileProcessing.createFileFromStream(inputStream, WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inPath));
        }
        else {
            System.err.println("Problem copying Map Layer (can't find in JAR): " + inPath);
        }
    }

}
