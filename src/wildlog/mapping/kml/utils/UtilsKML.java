package wildlog.mapping.kml.utils;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import KmlGenerator.objects.KmlStyle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.interfaces.DataObjectBasicInfo;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsKML {

    private UtilsKML() {
    }

    public static List<KmlStyle> getKmlStyles(Path inIconPath) {
        List<KmlStyle> styles = new ArrayList<KmlStyle>();
        KmlStyle tempStyle = new KmlStyle();
        tempStyle.setName("locationStyle");
        tempStyle.setIconName("locationIcon");
        tempStyle.setIconPath(inIconPath.resolve("Location.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalCarnivoreStyle");
        tempStyle.setIconName("animalCarnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("AnimalCarnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalHerbivoreStyle");
        tempStyle.setIconName("animalHerbivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("AnimalHerbivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOmnivoreStyle");
        tempStyle.setIconName("animalOmnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("AnimalOmnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOtherStyle");
        tempStyle.setIconName("animalOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("AnimalOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdCarnivoreStyle");
        tempStyle.setIconName("birdCarnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("BirdCarnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdHerbivoreStyle");
        tempStyle.setIconName("birdHerbivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("BirdHerbivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOmnivoreStyle");
        tempStyle.setIconName("birdOmnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("BirdOmnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOtherStyle");
        tempStyle.setIconName("birdOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("BirdOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("plantStyle");
        tempStyle.setIconName("plantIcon");
        tempStyle.setIconPath(inIconPath.resolve("Plant.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("plantOtherStyle");
        tempStyle.setIconName("plantOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("PlantOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("amphibianStyle");
        tempStyle.setIconName("amphibianIcon");
        tempStyle.setIconPath(inIconPath.resolve("Amphibian.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("amphibianOtherStyle");
        tempStyle.setIconName("amphibianOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("AmphibianOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishCarnivoreStyle");
        tempStyle.setIconName("fishCarnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("FishCarnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishHerbivoreStyle");
        tempStyle.setIconName("fishHerbivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("FishHerbivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishOmnivoreStyle");
        tempStyle.setIconName("fishOmnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("FishOmnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishOtherStyle");
        tempStyle.setIconName("fishOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("FishOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectCarnivoreStyle");
        tempStyle.setIconName("insectCarnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("InsectCarnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectHerbivoreStyle");
        tempStyle.setIconName("insectHerbivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("InsectHerbivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectOmnivoreStyle");
        tempStyle.setIconName("insectOmnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("InsectOmnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectOtherStyle");
        tempStyle.setIconName("insectOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("InsectOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileCarnivoreStyle");
        tempStyle.setIconName("reptileCarnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("ReptileCarnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileHerbivoreStyle");
        tempStyle.setIconName("reptileHerbivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("ReptileHerbivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileOmnivoreStyle");
        tempStyle.setIconName("reptileOmnivoreIcon");
        tempStyle.setIconPath(inIconPath.resolve("ReptileOmnivore.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileOtherStyle");
        tempStyle.setIconName("reptileOtherIcon");
        tempStyle.setIconPath(inIconPath.resolve("ReptileOther.gif").toString());
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("otherStyle");
        tempStyle.setIconName("otherIcon");
        tempStyle.setIconPath(inIconPath.resolve("Other.gif").toString());
        styles.add(tempStyle);
        return styles;
    }

    public static void copyKmlIcons(Path inIconPath) {
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/AnimalCarnivore.gif"), inIconPath.resolve("AnimalCarnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/AnimalHerbivore.gif"), inIconPath.resolve("AnimalHerbivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/AnimalOmnivore.gif"), inIconPath.resolve("AnimalOmnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/AnimalOther.gif"), inIconPath.resolve("AnimalOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/BirdCarnivore.gif"), inIconPath.resolve("BirdCarnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/BirdHerbivore.gif"), inIconPath.resolve("BirdHerbivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/BirdOmnivore.gif"), inIconPath.resolve("BirdOmnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/BirdOther.gif"), inIconPath.resolve("BirdOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/Plant.gif"), inIconPath.resolve("Plant.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/PlantOther.gif"), inIconPath.resolve("PlantOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/Amphibian.gif"), inIconPath.resolve("Amphibian.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/AmphibianOther.gif"), inIconPath.resolve("AmphibianOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/FishCarnivore.gif"), inIconPath.resolve("FishCarnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/FishHerbivore.gif"), inIconPath.resolve("FishHerbivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/FishOmnivore.gif"), inIconPath.resolve("FishOmnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/FishOther.gif"), inIconPath.resolve("FishOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/InsectCarnivore.gif"), inIconPath.resolve("InsectCarnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/InsectHerbivore.gif"), inIconPath.resolve("InsectHerbivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/InsectOmnivore.gif"), inIconPath.resolve("InsectOmnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/InsectOther.gif"), inIconPath.resolve("InsectOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/ReptileCarnivore.gif"), inIconPath.resolve("ReptileCarnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/ReptileHerbivore.gif"), inIconPath.resolve("ReptileHerbivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/ReptileOmnivore.gif"), inIconPath.resolve("ReptileOmnivore.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/ReptileOther.gif"), inIconPath.resolve("ReptileOther.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/Other.gif"), inIconPath.resolve("Other.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/mapping/Location.gif"), inIconPath.resolve("Location.gif"));
    }

    public static void exportKML(DataObjectBasicInfo inDataObject, ProgressbarTask inProgressbarTask, WildLogApp inApp) throws IOException {
        inProgressbarTask.setMessage("Creating the KML Export for '" + inDataObject.getDisplayName() + "'");
        inProgressbarTask.setTaskProgress(0);
        // Make sure all folders, thumbnails and icons exist
        Path iconPath = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(WildLogPaths.WildLogPathPrefixes.WILDLOG_SYSTEM_DUMP.toPath());
        Files.createDirectories(iconPath);
        UtilsKML.copyKmlIcons(iconPath);
        Path finalPath = WildLogPaths.WILDLOG_EXPORT_KML.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".kml");
        Files.createDirectories(finalPath.getParent());
        // Generate KML entries
        KmlGenerator kmlgen = new KmlGenerator();
        kmlgen.setKmlPath(finalPath.toString());
        // Get entries for Sightings and Locations
        boolean groupByLocationName = false;
        if (inDataObject instanceof Element) {
            groupByLocationName = true;
        }
        inProgressbarTask.setTaskProgress(5);
        inProgressbarTask.setMessage("Creating the KML Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
        // Add Sighting entries
        Sighting tempSighting = new Sighting();
        if (inDataObject instanceof Location) {
            tempSighting.setLocationName(inDataObject.getDisplayName());
        }
        else
        if (inDataObject instanceof Element) {
            tempSighting.setElementName(inDataObject.getDisplayName());
        }
        else
        if (inDataObject instanceof Visit) {
            tempSighting.setVisitName(inDataObject.getDisplayName());
        }
        List<Sighting> listSightings = inApp.getDBI().list(tempSighting);
        Collections.sort(listSightings);
        Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>(50);
        for (int t = 0; t < listSightings.size(); t++) {
            String key;
            if (!groupByLocationName) {
                key = listSightings.get(t).getElementName();
            }
            else{
                key = listSightings.get(t).getLocationName();
            }
            if (!entries.containsKey(key)) {
                entries.put(key, new ArrayList<KmlEntry>(20));
             }
            entries.get(key).add(listSightings.get(t).toKML(t, inApp));
            inProgressbarTask.setTaskProgress(5 + (int)((t/(double)listSightings.size())*85));
            inProgressbarTask.setMessage("Creating the KML Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
        }
        if (!groupByLocationName) {
            // Add Locations entries
            Location searchLocation;
            if (inDataObject instanceof Location) {
                searchLocation = (Location) inDataObject;
            }
            else
            if (inDataObject instanceof Visit) {
                searchLocation = new Location(((Visit) inDataObject).getLocationName());
            }
            else {
                searchLocation = new Location();
            }
            List<Location> listLocations = inApp.getDBI().list(searchLocation);
            Collections.sort(listLocations);
            for (int t = 0; t < listLocations.size(); t++) {
                String key = listLocations.get(t).getName();
                if (!entries.containsKey(key)) {
                    entries.put(key, new ArrayList<KmlEntry>(1));
                 }
                entries.get(key).add(listLocations.get(t).toKML(listSightings.size() + t, inApp));
                inProgressbarTask.setTaskProgress(90 + (int)((t/(double)listLocations.size())*5));
                inProgressbarTask.setMessage("Creating the KML Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
            }
        }
        inProgressbarTask.setTaskProgress(95);
        inProgressbarTask.setMessage("Creating the KML Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
        // Generate KML
        kmlgen.generateFile(entries, UtilsKML.getKmlStyles(iconPath));
        // Try to open the Kml file
        UtilsFileProcessing.openFile(finalPath);
        inProgressbarTask.setTaskProgress(100);
        inProgressbarTask.setMessage("Done with the KML Export for '" + inDataObject.getDisplayName() + "'");
    }

}
