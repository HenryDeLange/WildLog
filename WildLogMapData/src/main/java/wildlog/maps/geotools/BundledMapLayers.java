package wildlog.maps.geotools;

import java.nio.file.Path;
import java.nio.file.Paths;


public enum BundledMapLayers {
    ALTITUDE                        (Paths.get("Layers", "Altitude", "altitude.tif")),
    BASE_CITIES                     (Paths.get("Layers", "Base_Cities", "cities.shp")),
    BASE_LAKES                      (Paths.get("Layers", "Base_MajorLakes", "lakes.shp")),
    BASE_RIVERS                     (Paths.get("Layers", "Base_MajorRivers", "rivers.shp")),
    BASE_WORLD                      (Paths.get("Layers", "Base_World", "world.shp")),
    BIOMES_LOCAL                    (Paths.get("Layers", "Biomes_Local", "biomes_local.shp")),
    BIOMES_LOCAL_GROUPS             (Paths.get("Layers", "Biomes_Local_Groups", "biome_groups.shp")),
    BIOMES_WORLD                    (Paths.get("Layers", "Biomes_World", "biomes.shp")),
    CLIMATE_PRECIPITATION_AVERAGE   (Paths.get("Layers", "Climate_Precipitation_Average", "aridity.tif")),
    CLIMATE_PERCIPITATION_MONTHLY   (Paths.get("Layers", "Climate_Precipitation_Monthly")),
    CLIMATE_TEMPERATURE_MAX         (Paths.get("Layers", "Climate_Temperature_Max")),
    CLIMATE_TEMPERATURE_MEAN        (Paths.get("Layers", "Climate_Temperature_Mean")),
    CLIMATE_TEMPERATURE_MIN         (Paths.get("Layers", "Climate_Temperature_Min")),
    EARTH_HISTORIC_IDEAL            (Paths.get("Layers", "Earth_HistoricIdeal", "world_historic.tif")),
    EARTH_MODERN                    (Paths.get("Layers", "Earth_Modern", "world_modern.tif")),
    FARMING_CROPS                   (Paths.get("Layers", "Farming_Crops", "cropland.tif")),
    FORESTS                         (Paths.get("Layers", "Forests", "forests.tif")),
    HUMAN_INFLUENCE                 (Paths.get("Layers", "HumanInfluence", "humans_influence.tif")),
    HUMAN_POPULATION                (Paths.get("Layers", "HumanPopulation", "population.tif")),
    PROTECTED_AREAS_LOCAL_FORMAL    (Paths.get("Layers", "ProtectedAreas_Local", "Formal", "parks_formal.shp")),
    PROTECTED_AREAS_LOCAL_INFORMAL  (Paths.get("Layers", "ProtectedAreas_Local", "Informal", "parks_informal.shp")),
    PROTECTED_AREAS_WORLD           (Paths.get("Layers", "ProtectedAreas_World", "protected_areas.tif"));
    
    private final Path relativePath;

    private BundledMapLayers(Path inRelativePath) {
        relativePath = inRelativePath;
    }

    public Path getRelativePath() {
        return relativePath;
    }
    
    public Path getRelativePathForMonth(Months inMonth) {
        return getRelativePath().resolve(inMonth.getRelativePath());
    }
    
    public Path getRelativePathForMonth(int inMonth) {
        return getRelativePath().resolve(Months.values()[inMonth].getRelativePath());
    }
    
    public enum Months {
        M01_JAN(Paths.get("01Jan.tif")),
        M02_FEB(Paths.get("02Feb.tif")),
        M03_MAR(Paths.get("03Mar.tif")),
        M04_APR(Paths.get("04Apr.tif")),
        M05_MAY(Paths.get("05May.tif")),
        M06_JUN(Paths.get("06Jun.tif")),
        M07_JUL(Paths.get("07Jul.tif")),
        M08_AUG(Paths.get("08Aug.tif")),
        M09_SEP(Paths.get("09Sep.tif")),
        M10_OCT(Paths.get("10Oct.tif")),
        M11_NOV(Paths.get("11Nov.tif")),
        M12_DEC(Paths.get("12Dec.tif"));
        
        private final Path relativePath;

        private Months(Path inRelativePath) {
            relativePath = inRelativePath;
        }

        public Path getRelativePath() {
            return relativePath;
        }

    }
    
}
