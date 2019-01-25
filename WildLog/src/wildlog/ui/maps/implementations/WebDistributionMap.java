package wildlog.ui.maps.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.wrappers.json.gbif.GBIFData;
import wildlog.data.wrappers.json.gbif.GBIFOccurence;
import wildlog.data.wrappers.json.inaturalist.INaturalistMapData;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.maps.LoadingWebMapDialog;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;
import wildlog.utils.WildLogPaths;


public class WebDistributionMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {SPECIES_DISTRIBUTION};
    private MapType activeMapType = MapType.SPECIES_DISTRIBUTION;
    private boolean showWorkspaceMap = true;
    private boolean showSightings = true;
    private final int PAGE_LIMIT_INATURALIST = 100;
    private List<INaturalistMapData> lstAllINaturalistResults = new ArrayList<>(0);
    private final int PAGE_LIMIT_GBIF = 150;
    private List<GBIFOccurence> lstAllGBIFResults = new ArrayList<>(0);
    private String scientificName;


    public WebDistributionMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Distribution Web Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        // Maps
        ToggleButton btnDistributionMap = new ToggleButton("Creature Distribution Map");
        btnDistributionMap.setToggleGroup(BUTTON_GROUP);
        btnDistributionMap.setCursor(Cursor.HAND);
        btnDistributionMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.SPECIES_DISTRIBUTION;
            }
        });
        lstCustomButtons.add(btnDistributionMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        CheckBox chkShowSightings = new CheckBox("Include Workspace Observations");
        chkShowSightings.setCursor(Cursor.HAND);
        chkShowSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showSightings = chkShowSightings.isSelected();
            }
        });
        chkShowSightings.setSelected(showSightings);
        lstCustomButtons.add(chkShowSightings);
        CheckBox chkShowWorkspaceMap = new CheckBox("Include Distribution Map");
        chkShowWorkspaceMap.setCursor(Cursor.HAND);
        chkShowWorkspaceMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showWorkspaceMap = chkShowWorkspaceMap.isSelected();
            }
        });
        
// TODO: Laai ook die spesie layers direk vanaf IUCN se web services? 
//       (Ek het dit probeer maar lyk my dis 'n geneuk... Sal ook 'n token nodig hê, wat ek seker nie sal kry nie want ek wag nog vir die ander token...)
        
        chkShowWorkspaceMap.setSelected(showWorkspaceMap);
        lstCustomButtons.add(chkShowWorkspaceMap);
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.SPECIES_DISTRIBUTION)) {
            setActiveSubCategoryTitle("Creature Distribution Map");
            // Get the scientific name
            String elementName = null;
            for (Sighting sighting : lstData) {
                if (elementName == null) {
                    elementName = sighting.getElementName();
                    continue;
                }
                if (!elementName.equalsIgnoreCase(sighting.getElementName())) {
                    // The list contains more than one Creature
                    elementName = null;
                    break;
                }
            }
            if (elementName != null) {
                String newScientificName = WildLogApp.getApplication().getDBI().findElement(elementName, Element.class).getScientificName();
                if (newScientificName != null && !newScientificName.isEmpty()) {
                    // Add base layer
                    map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
                    // Add selected distribution layers from workspace
                    if (showWorkspaceMap) {
                        if (Files.exists(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath())) {
                            try {
                                Files.walkFileTree(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult visitFile(Path inPath, BasicFileAttributes inAttributes) throws IOException {
                                        String filename = inPath.getFileName().toString().toLowerCase();
                                        if (filename.substring(0, filename.lastIndexOf('.')).equalsIgnoreCase(newScientificName) && Files.isRegularFile(inPath)) {
                                            if (filename.endsWith(".tif") || filename.endsWith(".tiff")) {
                                                try {
                                                    GeoTiffReader reader = new GeoTiffReader(inPath.toFile());
                                                    Layer gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader), 
                                                            inPath.getFileName().toString());
                                                    map.addLayer(gridLayer);
                                                    return FileVisitResult.TERMINATE;
                                                }
                                                catch (DataSourceException ex) {
                                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                                }
                                            }
                                            else
                                            if (filename.endsWith(".shp")) {
                                                try {
                                                    FileDataStore shapeStore = FileDataStoreFinder.getDataStore(inPath.toFile());
                                                    SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                                                    Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(
                                                            shapeSource, new Color(150, 50, 30), new Color(232, 60, 19), 0.3, 0.25), 
                                                            inPath.getFileName().toString());
                                                    map.addLayer(shapelayer);
                                                    return FileVisitResult.TERMINATE;
                                                }
                                                catch (IOException ex) {
                                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                                }
                                            }
                                        }
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                            }
                            catch (IOException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            }
                        }
                    }
                    // Add web datasets
                    getGBIFData(newScientificName);
                    getINaturalistData(newScientificName);
                    // Set the active scientific name to the new one
                    scientificName = newScientificName;
                }
                else {
                    WLOptionPane.showMessageDialog(mapsBaseDialog,
                            "<html>The data for this map is loaded based on the Scientific Name."
                                    + "<br/>Please make sure that the active Creature has a Scientific Name associated with it.</html>",
                            "Scientific Name not found", JOptionPane.WARNING_MESSAGE);
                }
                // Add countries
                if (showCountries) {
                    map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
                }
                // Add Sightings
                if (showSightings) {
                    map.addLayer(getLayerForSightings(lstData));
                }
                // Setup the info panel
                setupChartDescriptionLabel("<html>This map shows observation records for <b><u>" + scientificName + "</u></b> from the following online datasets:"
                    + "<br/> - For <b>iNaturalist</b> <u>" + lstAllINaturalistResults.size() + " records</u> are shown in Teal. "
                    + "<br/> - For <b>GBIF</b> <u>" + lstAllGBIFResults.size() + " records</u> are shown in Purple.</html>");
            }
            else {
                // Add base layer (to prevent the map from showing an empty screen which messes up the zoom)
                map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
                WLOptionPane.showMessageDialog(mapsBaseDialog,
                        "<html>The data for this map can only be loaded for one Creature at a time."
                                + "<br/>Please make sure to filter the Observations to only use one Creature.</html>",
                        "Select a single Creature", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void getINaturalistData(String inScientificName) {
        LoadingWebMapDialog dialog = new LoadingWebMapDialog(mapsBaseDialog, "iNaturalist");
        try {
            dialog.setVisible(true);
            // Check whether the data should be fetched fro the web URL
            if ((scientificName == null && inScientificName != null) || (scientificName != null && !scientificName.equalsIgnoreCase(inScientificName))) {
                int requestPage = 0;
                int totalEntries = 0;
                lstAllINaturalistResults = new ArrayList<>(PAGE_LIMIT_INATURALIST);
                do {
                    requestPage = requestPage + 1; // Note: Increase first because page 0 and 1 seems to return the same info
                    URL url = new URL("https://www.inaturalist.org/observations.json?taxon_name="
                            + URLEncoder.encode(inScientificName, "UTF-8").replace("+", "%20")
                            + "&has[]=geo&page=" + requestPage + "&per_page=" + PAGE_LIMIT_INATURALIST);
                    URLConnection urlConnection = url.openConnection();
                    Map<String, List<String>> mapHTTPHeaders = urlConnection.getHeaderFields();
                    Set<Map.Entry<String, List<String>>> setHTTPHeaderEntries = mapHTTPHeaders.entrySet();
                    for (Map.Entry<String, List<String>> headerEntry : setHTTPHeaderEntries) {
                        if ("X-Total-Entries".equalsIgnoreCase(headerEntry.getKey())) {
                            List<String> headerValues = headerEntry.getValue();
                            for (String value : headerValues) {
                                try {
                                    totalEntries = Integer.parseInt(value);
                                    break;
                                }
                                catch (NumberFormatException ex) {
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                }
                            }
                            break;
                        }
                    }
                    InputStream inputStream = urlConnection.getInputStream();
                    List<INaturalistMapData> lstResults = new Gson().fromJson(new InputStreamReader(inputStream, "UTF-8"), 
                            new TypeToken<List<INaturalistMapData>>(){}.getType());
                    lstAllINaturalistResults.addAll(lstResults);
                    dialog.updateInfoText("Loaded " + lstAllINaturalistResults.size() + " of " + totalEntries + " records.");
                }
                while (!dialog.isStopLoading() && (requestPage * PAGE_LIMIT_INATURALIST) < totalEntries);
            }
            // Add the layer
            try {
                SimpleFeatureType type = DataUtilities.createType("iNaturalistPointType", "geom:Point,name:String,mydata:String");
                SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
                DefaultFeatureCollection collection = new DefaultFeatureCollection();
                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                for (INaturalistMapData data : lstAllINaturalistResults) {
                    builder.add(geometryFactory.createPoint(new Coordinate(data.getLongitude(), data.getLatitude())));
                    SimpleFeature feature = builder.buildFeature(Long.toString(data.getId()), new Object[] {data.getId()});
                    collection.add(feature);
                }
                Style pointStyle = GeoToolsLayerUtils.createPointStyle(new Color(0, 40, 40), new Color(0, 90, 90), 0.8, 0.5, 14);
                FeatureLayer pointLayer = new FeatureLayer(collection, pointStyle, "iNaturalistLayer");
                map.addLayer(pointLayer);
            }
            catch (SchemaException | FactoryRegistryException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        catch (IOException | JsonIOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        finally {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
    
    private void getGBIFData(String inScientificName) {
        LoadingWebMapDialog dialog = new LoadingWebMapDialog(mapsBaseDialog, "GBIF");
        try {
            dialog.setVisible(true);
            // Check whether the data should be fetched fro the web URL
            if ((scientificName == null && inScientificName != null) || (scientificName != null && !scientificName.equalsIgnoreCase(inScientificName))) {
                int requestPage = 0;
                int totalEntries = 0;
                lstAllGBIFResults = new ArrayList<>(PAGE_LIMIT_GBIF);
                do {
                    URL url = new URL("http://api.gbif.org/v1/occurrence/search?scientificName="
                            + URLEncoder.encode(inScientificName, "UTF-8").replace("+", "%20")
                            + "&hasCoordinate=true&hasGeospatialIssue=false&offset=" + (requestPage * PAGE_LIMIT_GBIF) + "&limit=" + PAGE_LIMIT_GBIF);
                    GBIFData gbifResult = new Gson().fromJson(new InputStreamReader(url.openStream(), "UTF-8"), GBIFData.class);
                    totalEntries = gbifResult.getCount();
                    lstAllGBIFResults.addAll(gbifResult.getResults());
                    dialog.updateInfoText("Loaded " + lstAllGBIFResults.size() + " of " + totalEntries + " records.");
                    requestPage = requestPage + 1; // Note: Increase at the end because the initial offset must be 0
                }
                while (!dialog.isStopLoading() && (requestPage * PAGE_LIMIT_GBIF) < totalEntries);
            }
            // Add the layer
            try {
                SimpleFeatureType type = DataUtilities.createType("GBIFPointType", "geom:Point,name:String,mydata:String");
                SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
                DefaultFeatureCollection collection = new DefaultFeatureCollection();
                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                for (GBIFOccurence data : lstAllGBIFResults) {
                    builder.add(geometryFactory.createPoint(new Coordinate(data.getDecimalLongitude(), data.getDecimalLatitude())));
                    SimpleFeature feature = builder.buildFeature(Long.toString(data.getKey()), new Object[] {data.getKey()});
                    collection.add(feature);
                }
                Style pointStyle = GeoToolsLayerUtils.createPointStyle(new Color(40, 0, 40), new Color(90, 0, 90), 0.8, 0.5, 14);
                FeatureLayer pointLayer = new FeatureLayer(collection, pointStyle, "GBIFLayer");
                map.addLayer(pointLayer);
            }
            catch (SchemaException | FactoryRegistryException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        catch (IOException | JsonIOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        finally {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
    
}
