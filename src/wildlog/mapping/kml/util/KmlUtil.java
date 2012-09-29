package wildlog.mapping.kml.util;

import KmlGenerator.objects.KmlStyle;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.utils.ui.Utils;

/**
 *
 * @author Henry
 */
public final class KmlUtil {

    public static List<KmlStyle> getKmlStyles() {
        List<KmlStyle> styles = new ArrayList<KmlStyle>();
        KmlStyle tempStyle = new KmlStyle();
        tempStyle.setName("locationStyle");
        tempStyle.setIconName("locationIcon");
        tempStyle.setIconPath("Location.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalCarnivoreStyle");
        tempStyle.setIconName("animalCarnivoreIcon");
        tempStyle.setIconPath("AnimalCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalHerbivoreStyle");
        tempStyle.setIconName("animalHerbivoreIcon");
        tempStyle.setIconPath("AnimalHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOmnivoreStyle");
        tempStyle.setIconName("animalOmnivoreIcon");
        tempStyle.setIconPath("AnimalOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("animalOtherStyle");
        tempStyle.setIconName("animalOtherIcon");
        tempStyle.setIconPath("AnimalOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdCarnivoreStyle");
        tempStyle.setIconName("birdCarnivoreIcon");
        tempStyle.setIconPath("BirdCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdHerbivoreStyle");
        tempStyle.setIconName("birdHerbivoreIcon");
        tempStyle.setIconPath("BirdHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOmnivoreStyle");
        tempStyle.setIconName("birdOmnivoreIcon");
        tempStyle.setIconPath("BirdOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("birdOtherStyle");
        tempStyle.setIconName("birdOtherIcon");
        tempStyle.setIconPath("BirdOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("plantStyle");
        tempStyle.setIconName("plantIcon");
        tempStyle.setIconPath("Plant.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("plantOtherStyle");
        tempStyle.setIconName("plantOtherIcon");
        tempStyle.setIconPath("PlantOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("amphibianStyle");
        tempStyle.setIconName("amphibianIcon");
        tempStyle.setIconPath("Amphibian.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("amphibianOtherStyle");
        tempStyle.setIconName("amphibianOtherIcon");
        tempStyle.setIconPath("AmphibianOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishCarnivoreStyle");
        tempStyle.setIconName("fishCarnivoreIcon");
        tempStyle.setIconPath("FishCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishHerbivoreStyle");
        tempStyle.setIconName("fishHerbivoreIcon");
        tempStyle.setIconPath("FishHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishOmnivoreStyle");
        tempStyle.setIconName("fishOmnivoreIcon");
        tempStyle.setIconPath("FishOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("fishOtherStyle");
        tempStyle.setIconName("fishOtherIcon");
        tempStyle.setIconPath("FishOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectCarnivoreStyle");
        tempStyle.setIconName("insectCarnivoreIcon");
        tempStyle.setIconPath("InsectCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectHerbivoreStyle");
        tempStyle.setIconName("insectHerbivoreIcon");
        tempStyle.setIconPath("InsectHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectOmnivoreStyle");
        tempStyle.setIconName("insectOmnivoreIcon");
        tempStyle.setIconPath("InsectOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("insectOtherStyle");
        tempStyle.setIconName("insectOtherIcon");
        tempStyle.setIconPath("InsectOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileCarnivoreStyle");
        tempStyle.setIconName("reptileCarnivoreIcon");
        tempStyle.setIconPath("ReptileCarnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileHerbivoreStyle");
        tempStyle.setIconName("reptileHerbivoreIcon");
        tempStyle.setIconPath("ReptileHerbivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileOmnivoreStyle");
        tempStyle.setIconName("reptileOmnivoreIcon");
        tempStyle.setIconPath("ReptileOmnivore.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("reptileOtherStyle");
        tempStyle.setIconName("reptileOtherIcon");
        tempStyle.setIconPath("ReptileOther.gif");
        styles.add(tempStyle);
        tempStyle = new KmlStyle();
        tempStyle.setName("otherStyle");
        tempStyle.setIconName("otherIcon");
        tempStyle.setIconPath("Other.gif");
        styles.add(tempStyle);
        return styles;
    }

    public static void copyKmlIcons(WildLogApp inApp, String inPath) {
        try {
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/AnimalCarnivore.gif").toURI()), new File(inPath + "AnimalCarnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/AnimalHerbivore.gif").toURI()), new File(inPath + "AnimalHerbivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/AnimalOmnivore.gif").toURI()), new File(inPath + "AnimalOmnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/AnimalOther.gif").toURI()), new File(inPath + "AnimalOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/BirdCarnivore.gif").toURI()), new File(inPath + "BirdCarnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/BirdHerbivore.gif").toURI()), new File(inPath + "BirdHerbivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/BirdOmnivore.gif").toURI()), new File(inPath + "BirdOmnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/BirdOther.gif").toURI()), new File(inPath + "BirdOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/Plant.gif").toURI()), new File(inPath + "Plant.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/PlantOther.gif").toURI()), new File(inPath + "PlantOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/Amphibian.gif").toURI()), new File(inPath + "Amphibian.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/AmphibianOther.gif").toURI()), new File(inPath + "AmphibianOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/FishCarnivore.gif").toURI()), new File(inPath + "FishCarnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/FishHerbivore.gif").toURI()), new File(inPath + "FishHerbivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/FishOmnivore.gif").toURI()), new File(inPath + "FishOmnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/FishOther.gif").toURI()), new File(inPath + "FishOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/InsectCarnivore.gif").toURI()), new File(inPath + "InsectCarnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/InsectHerbivore.gif").toURI()), new File(inPath + "InsectHerbivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/InsectOmnivore.gif").toURI()), new File(inPath + "InsectOmnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/InsectOther.gif").toURI()), new File(inPath + "InsectOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/ReptileCarnivore.gif").toURI()), new File(inPath + "ReptileCarnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/ReptileHerbivore.gif").toURI()), new File(inPath + "ReptileHerbivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/ReptileOmnivore.gif").toURI()), new File(inPath + "ReptileOmnivore.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/ReptileOther.gif").toURI()), new File(inPath + "ReptileOther.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/Other.gif").toURI()), new File(inPath + "Other.gif"));
            Utils.copyFile(new File(inApp.getClass().getResource("resources/mapping/Location.gif").toURI()), new File(inPath + "Location.gif"));
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
