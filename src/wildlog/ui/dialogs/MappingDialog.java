package wildlog.ui.dialogs;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.kml.utils.UtilsKML;
import wildlog.utils.WildLogPaths;
import wildlog.html.utils.UtilsHTML;
import wildlog.mapping.utils.UtilsMapGenerator;
import wildlog.utils.UtilsFileProcessing;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsConcurency;

// FIXME: Baie van hierdie logic herhaal op baie plekke, probeer dit sentraliseer...
public class MappingDialog extends JDialog {
    private WildLogApp app;
    private Location location;
    private Element element;
    private Visit visit;
    private Sighting sighting;

    public MappingDialog(Frame inParent,
            Location inLocationToMap,
            Element inElementToMap,
            Visit inVisit,
            Sighting inSighting) {
        super(inParent);
        // Set passed in values
        app = (WildLogApp) Application.getInstance();
        location = inLocationToMap;
        element = inElementToMap;
        visit = inVisit;
        sighting = inSighting;

        // Auto generated code
        initComponents();

        // Determine what buttons to show
        if (location == null) {
            btnViewLocation.setVisible(false);
            btnViewAllSightingsForLocation.setVisible(false);
        }
        if (element == null) {
            btnViewAllSightingsForElement.setVisible(false);
            btnViewDistributionMap.setVisible(false);
            btnViewSightingsAndDistributionMap.setVisible(false);
            btnViewDistributionMap.setVisible(false);
            btnViewSightingsAndDistributionMap.setVisible(false);
        }
        else {
            if (element.getScientificName() != null && !element.getScientificName().isEmpty()) {
                // FIXME: Case sensitivaty mag dalk 'n issue wees op ander opperating systems
                File file = new File(WildLogPaths.concatPaths(WildLogPaths.WILDLOG_MAPS.getFullPath(), element.getScientificName(), element.getScientificName() + ".shp"));
                if (!file.exists() || file.isDirectory()) {
                    btnViewDistributionMap.setVisible(false);
                    btnViewSightingsAndDistributionMap.setVisible(false);
                }
            }
            else {
                btnViewDistributionMap.setVisible(false);
                btnViewSightingsAndDistributionMap.setVisible(false);
            }
        }
        if (location == null && element == null && visit == null) {
            btnOpenKmlViewer.setVisible(false);
        }
        if (visit == null) {
            btnViewAllSightingsForVisit.setVisible(false);
        }
        if (sighting == null) {
            btnViewSingleSighting.setVisible(false);
        }

        // Pack
        pack();

        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton5 = new javax.swing.JButton();
        btnViewLocation = new javax.swing.JButton();
        btnViewSingleSighting = new javax.swing.JButton();
        btnViewAllSightingsForLocation = new javax.swing.JButton();
        btnViewAllSightingsForVisit = new javax.swing.JButton();
        btnViewAllSightingsForElement = new javax.swing.JButton();
        btnViewSightingsAndDistributionMap = new javax.swing.JButton();
        btnViewDistributionMap = new javax.swing.JButton();
        btnOpenKmlViewer = new javax.swing.JButton();

        jButton5.setText("jButton5");
        jButton5.setName("jButton5"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Available Maps");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Map_Small.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnViewLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewLocation.setText("View the Place on the Map");
        btnViewLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewLocation.setFocusPainted(false);
        btnViewLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewLocation.setIconTextGap(10);
        btnViewLocation.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewLocation.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewLocation.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewLocation.setName("btnViewLocation"); // NOI18N
        btnViewLocation.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewLocationActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewLocation);

        btnViewSingleSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewSingleSighting.setText("View the selected Observation");
        btnViewSingleSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewSingleSighting.setFocusPainted(false);
        btnViewSingleSighting.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewSingleSighting.setIconTextGap(10);
        btnViewSingleSighting.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewSingleSighting.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewSingleSighting.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewSingleSighting.setName("btnViewSingleSighting"); // NOI18N
        btnViewSingleSighting.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewSingleSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewSingleSightingActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewSingleSighting);

        btnViewAllSightingsForLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewAllSightingsForLocation.setText("View Observations at the Place");
        btnViewAllSightingsForLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewAllSightingsForLocation.setFocusPainted(false);
        btnViewAllSightingsForLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewAllSightingsForLocation.setIconTextGap(10);
        btnViewAllSightingsForLocation.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewAllSightingsForLocation.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForLocation.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForLocation.setName("btnViewAllSightingsForLocation"); // NOI18N
        btnViewAllSightingsForLocation.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAllSightingsForLocationActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewAllSightingsForLocation);

        btnViewAllSightingsForVisit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewAllSightingsForVisit.setText("View Observations during the Period");
        btnViewAllSightingsForVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewAllSightingsForVisit.setFocusPainted(false);
        btnViewAllSightingsForVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewAllSightingsForVisit.setIconTextGap(10);
        btnViewAllSightingsForVisit.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewAllSightingsForVisit.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForVisit.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForVisit.setName("btnViewAllSightingsForVisit"); // NOI18N
        btnViewAllSightingsForVisit.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAllSightingsForVisitActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewAllSightingsForVisit);

        btnViewAllSightingsForElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewAllSightingsForElement.setText("View Observations of the Creature");
        btnViewAllSightingsForElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewAllSightingsForElement.setFocusPainted(false);
        btnViewAllSightingsForElement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewAllSightingsForElement.setIconTextGap(10);
        btnViewAllSightingsForElement.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewAllSightingsForElement.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForElement.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForElement.setName("btnViewAllSightingsForElement"); // NOI18N
        btnViewAllSightingsForElement.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewAllSightingsForElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAllSightingsForElementActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewAllSightingsForElement);

        btnViewSightingsAndDistributionMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewSightingsAndDistributionMap.setText("View Observations + Distribution");
        btnViewSightingsAndDistributionMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewSightingsAndDistributionMap.setFocusPainted(false);
        btnViewSightingsAndDistributionMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewSightingsAndDistributionMap.setIconTextGap(10);
        btnViewSightingsAndDistributionMap.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewSightingsAndDistributionMap.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewSightingsAndDistributionMap.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewSightingsAndDistributionMap.setName("btnViewSightingsAndDistributionMap"); // NOI18N
        btnViewSightingsAndDistributionMap.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewSightingsAndDistributionMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewSightingsAndDistributionMapActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewSightingsAndDistributionMap);

        btnViewDistributionMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnViewDistributionMap.setText("View Distribution Map");
        btnViewDistributionMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewDistributionMap.setFocusPainted(false);
        btnViewDistributionMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewDistributionMap.setIconTextGap(10);
        btnViewDistributionMap.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnViewDistributionMap.setMaximumSize(new java.awt.Dimension(230, 35));
        btnViewDistributionMap.setMinimumSize(new java.awt.Dimension(230, 35));
        btnViewDistributionMap.setName("btnViewDistributionMap"); // NOI18N
        btnViewDistributionMap.setPreferredSize(new java.awt.Dimension(230, 35));
        btnViewDistributionMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDistributionMapActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewDistributionMap);

        btnOpenKmlViewer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Google Earth Icon.gif"))); // NOI18N
        btnOpenKmlViewer.setText("Open KML File");
        btnOpenKmlViewer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOpenKmlViewer.setFocusPainted(false);
        btnOpenKmlViewer.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenKmlViewer.setIconTextGap(11);
        btnOpenKmlViewer.setMargin(new java.awt.Insets(2, 11, 2, 8));
        btnOpenKmlViewer.setMaximumSize(new java.awt.Dimension(230, 35));
        btnOpenKmlViewer.setMinimumSize(new java.awt.Dimension(230, 35));
        btnOpenKmlViewer.setName("btnOpenKmlViewer"); // NOI18N
        btnOpenKmlViewer.setPreferredSize(new java.awt.Dimension(230, 35));
        btnOpenKmlViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenKmlViewerActionPerformed(evt);
            }
        });
        getContentPane().add(btnOpenKmlViewer);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewLocationActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, false);
        // Load points
        if (location.getLatitude() != null && location.getLongitude() != null)
            if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                double lat = location.getLatDegrees();
                lat = lat + location.getLatMinutes()/60.0;
                lat = lat + (location.getLatSeconds()/60.0)/60.0;
                if (location.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = location.getLonDegrees();
                lon = lon + location.getLonMinutes()/60.0;
                lon = lon + (location.getLonSeconds()/60.0)/60.0;
                if (location.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), location, app, false);
            }
        this.dispose();
        // Open Map
        if (app.getWildLogOptions().isIsOnlinemapTheDefault()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + location.getName());
            app.getMapOnline().setLocationRelativeTo(app.getMainFrame());
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + location.getName());
            app.getMapOffline().showMap();
        }
    }//GEN-LAST:event_btnViewLocationActionPerformed

    private void btnViewAllSightingsForLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAllSightingsForLocationActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, false);
        // Load points
        Sighting tempSighting = new Sighting();
        tempSighting.setLocationName(location.getName());
        List<Sighting> sightings = app.getDBI().list(tempSighting);
        for (int i = 0; i < sightings.size(); i++) {
            if (sightings.get(i).getLatitude() != null && sightings.get(i).getLongitude() != null) {
                if (!sightings.get(i).getLatitude().equals(Latitudes.NONE) && !sightings.get(i).getLongitude().equals(Longitudes.NONE)) {
                    double lat = sightings.get(i).getLatDegrees();
                    lat = lat + sightings.get(i).getLatMinutes()/60.0;
                    lat = lat + (sightings.get(i).getLatSeconds()/60.0)/60.0;
                    if (sightings.get(i).getLatitude().equals(Latitudes.SOUTH))
                        lat = -1 * lat;
                    double lon = sightings.get(i).getLonDegrees();
                    lon = lon + sightings.get(i).getLonMinutes()/60.0;
                    lon = lon + (sightings.get(i).getLonSeconds()/60.0)/60.0;
                    if (sightings.get(i).getLongitude().equals(Longitudes.WEST))
                        lon = -1 * lon;
                    UtilsMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sightings.get(i), app, false);
                }
            }
        }
        this.dispose();
        // Open Map
        if (app.getWildLogOptions().isIsOnlinemapTheDefault()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + location.getName() + " (Observations)");
            app.getMapOnline().setLocationRelativeTo(app.getMainFrame());
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + location.getName() + " (Observations)");
            app.getMapOffline().showMap();
        }
    }//GEN-LAST:event_btnViewAllSightingsForLocationActionPerformed

    private void btnViewAllSightingsForElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAllSightingsForElementActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, false);
        // Load points
        Sighting sigting = new Sighting();
        sigting.setElementName(element.getPrimaryName());
        List<Sighting> sightingList = app.getDBI().list(sigting);
        boolean foundPosition = false;
        for (int t = 0; t < sightingList.size(); t++) {
            foundPosition = false;
            if (sightingList.get(t).getLatitude() != null && sightingList.get(t).getLongitude() != null)
            if (!sightingList.get(t).getLatitude().equals(Latitudes.NONE) && !sightingList.get(t).getLongitude().equals(Longitudes.NONE)) {
                double lat = sightingList.get(t).getLatDegrees();
                lat = lat + sightingList.get(t).getLatMinutes()/60.0;
                lat = lat + (sightingList.get(t).getLatSeconds()/60.0)/60.0;
                if (sightingList.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = sightingList.get(t).getLonDegrees();
                lon = lon + sightingList.get(t).getLonMinutes()/60.0;
                lon = lon + (sightingList.get(t).getLonSeconds()/60.0)/60.0;
                if (sightingList.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sightingList.get(t), app, false);
                foundPosition = true;
            }
            // If the sighting did not have a position use the location's
            if (foundPosition == false) {
                Location tempLocation = app.getDBI().find(new Location(sightingList.get(t).getLocationName()));
                double lat = tempLocation.getLatDegrees();
                lat = lat + tempLocation.getLatMinutes()/60.0;
                lat = lat + (tempLocation.getLatSeconds()/60.0)/60.0;
                if (tempLocation.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = tempLocation.getLonDegrees();
                lon = lon + tempLocation.getLonMinutes()/60.0;
                lon = lon + (tempLocation.getLonSeconds()/60.0)/60.0;
                if (tempLocation.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), sightingList.get(t), app, false);
            }
        }
        this.dispose();
        // Open Map
        if (app.getWildLogOptions().isIsOnlinemapTheDefault()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + element.getPrimaryName());
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + element.getPrimaryName());
            app.getMapOffline().showMap();
        }
    }//GEN-LAST:event_btnViewAllSightingsForElementActionPerformed

    private void btnOpenKmlViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenKmlViewerActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                if (location != null) {
                    setMessage("Starting the KML Export");
                    setProgress(0);
                    // First export to HTML to create the images
                    setMessage("KML Export: Exporting images...");
                    UtilsHTML.exportHTML(location, app);
                    // Nou doen die KML deel
                    String path = WildLogPaths.WILDLOG_EXPORT_KML.getFullPath();
                    File tempFile = new File(path);
                    tempFile.mkdirs();
                    // Make sure icons exist in the KML folder
                    UtilsKML.copyKmlIcons(app, path);
                    // KML Stuff
                    KmlGenerator kmlgen = new KmlGenerator();
                    String finalPath = path + "WildLogMarkers - Place (" + location.getName() + ").kml";
                    kmlgen.setKmlPath(finalPath);
                    // Get entries for Sightings and Locations
                    Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
                    setProgress(5);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Sightings
                    Sighting tempSighting = new Sighting();
                    tempSighting.setLocationName(location.getName());
                    List<Sighting> listSightings = app.getDBI().list(tempSighting);
                    for (int t = 0; t < listSightings.size(); t++) {
                        String key = listSightings.get(t).getElementName();
                        if (!entries.containsKey(key)) {
                            entries.put(key, new ArrayList<KmlEntry>());
                         }
                        entries.get(key).add(listSightings.get(t).toKML(t, app));
                        setProgress(5 + (int)((t/(double)listSightings.size())*85));
                        setMessage("KML Export: " + getProgress() + "%");
                    }
                    // Locations
                    List<Location> listLocations = app.getDBI().list(new Location(location.getName()));
                    for (int t = 0; t < listLocations.size(); t++) {
                        String key = listLocations.get(t).getName();
                        if (!entries.containsKey(key)) {
                            entries.put(key, new ArrayList<KmlEntry>());
                         }
                        entries.get(key).add(listLocations.get(t).toKML(listSightings.size() + t, app));
                        setProgress(90 + (int)((t/(double)listLocations.size())*5));
                        setMessage("KML Export: " + getProgress() + "%");
                    }
                    setProgress(95);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Generate KML
                    kmlgen.generateFile(entries, UtilsKML.getKmlStyles());
                    // Try to open the Kml file
                    UtilsFileProcessing.openFile(finalPath);
                    setProgress(100);
                    setMessage("Done with the KML Export");
                }
                if (element != null) {
                    setMessage("Starting the KML Export");
                    setProgress(0);
                    // First export to HTML to create images
                    setMessage("KML Export: Exporting images...");
                    UtilsHTML.exportHTML(element, app);
                    // Nou doen die KML deel
                    String path = WildLogPaths.WILDLOG_EXPORT_KML.getFullPath();
                    File tempFile = new File(path);
                    tempFile.mkdirs();
                    // Make sure icons exist in the KML folder
                    UtilsKML.copyKmlIcons(app, path);
                    // KML Stuff
                    KmlGenerator kmlgen = new KmlGenerator();
                    String finalPath = path + "WildLogMarkers - Creature (" + element.getPrimaryName() + ").kml";
                    kmlgen.setKmlPath(finalPath);
                    // Get entries for Sightings and Locations
                    Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
                    setProgress(5);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Sightings
                    Sighting tempSighting = new Sighting();
                    tempSighting.setElementName(element.getPrimaryName());
                    List<Sighting> listSightings = app.getDBI().list(tempSighting);
                    for (int t = 0; t < listSightings.size(); t++) {
                        String key = listSightings.get(t).getLocationName();
                        if (!entries.containsKey(key)) {
                            entries.put(key, new ArrayList<KmlEntry>());
                         }
                        entries.get(key).add(listSightings.get(t).toKML(t, app));
                        setProgress(5 + (int)((t/(double)listSightings.size())*90));
                        setMessage("KML Export: " + getProgress() + "%");
                    }
                    setProgress(95);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Generate KML
                    kmlgen.generateFile(entries, UtilsKML.getKmlStyles());
                    // Try to open the Kml file
                    UtilsFileProcessing.openFile(finalPath);
                    setProgress(100);
                    setMessage("Done with the KML Export");
                }
                if (visit != null) {
                    setMessage("Starting the KML Export");
                    setProgress(0);
                    // First export to HTML to create the images
                    setMessage("KML Export: Exporting images...");
                    UtilsHTML.exportHTML(visit, app);
                    // Nou doen die KML deel
                    String path = WildLogPaths.WILDLOG_EXPORT_KML.getFullPath();
                    File tempFile = new File(path);
                    tempFile.mkdirs();
                    // Make sure icons exist in the KML folder
                    UtilsKML.copyKmlIcons(app, path);
                    // KML Stuff
                    KmlGenerator kmlgen = new KmlGenerator();
                    String finalPath = path + "WildLogMarkers - Period (" + visit.getName() + ").kml";
                    kmlgen.setKmlPath(finalPath);
                    // Get entries for Sightings and Locations
                    Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
                    setProgress(5);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Sightings
                    Sighting tempSighting = new Sighting();
                    tempSighting.setVisitName(visit.getName());
                    List<Sighting> listSightings = app.getDBI().list(tempSighting);
                    for (int t = 0; t < listSightings.size(); t++) {
                        String key = listSightings.get(t).getElementName();
                        if (!entries.containsKey(key)) {
                            entries.put(key, new ArrayList<KmlEntry>());
                         }
                        entries.get(key).add(listSightings.get(t).toKML(t, app));
                        setProgress(5 + (int)((t/(double)listSightings.size())*90));
                        setMessage("KML Export: " + getProgress() + "%");
                    }
                    setProgress(95);
                    setMessage("KML Export: " + getProgress() + "%");
                    // Location
                    String key = visit.getLocationName();
                    if (!entries.containsKey(key)) {
                        entries.put(key, new ArrayList<KmlEntry>());
                     }
                    entries.get(key).add(app.getDBI().find(new Location(visit.getLocationName())).toKML(listSightings.size()+1, app));
                    // Generate KML
                    kmlgen.generateFile(entries, UtilsKML.getKmlStyles());
                    // Try to open the Kml file
                    UtilsFileProcessing.openFile(finalPath);
                    setProgress(100);
                    setMessage("Done with the KML Export");
                }
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnOpenKmlViewerActionPerformed

    private void btnViewDistributionMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDistributionMapActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, true);
        File file = new File(WildLogPaths.concatPaths(WildLogPaths.WILDLOG_MAPS.getFullPath(), element.getScientificName(), element.getScientificName() + ".shp"));
        UtilsMapGenerator.addDistributionMap(app, file);
        this.dispose();
        // Open Map
        app.getMapOffline().changeTitle("WildLog Map - Offline: " + element.getPrimaryName() + " - Distribution");
        app.getMapOffline().showMap();
    }//GEN-LAST:event_btnViewDistributionMapActionPerformed

    private void btnViewSightingsAndDistributionMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewSightingsAndDistributionMapActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, true);
        // Load distribution map
        File file = new File(WildLogPaths.concatPaths(WildLogPaths.WILDLOG_MAPS.getFullPath(), element.getScientificName(), element.getScientificName() + ".shp"));
        UtilsMapGenerator.addDistributionMap(app, file);
        // Load points
        Sighting sigting = new Sighting();
        sigting.setElementName(element.getPrimaryName());
        List<Sighting> sightingList = app.getDBI().list(sigting);
        boolean foundPosition = false;
        for (int t = 0; t < sightingList.size(); t++) {
            foundPosition = false;
            if (sightingList.get(t).getLatitude() != null && sightingList.get(t).getLongitude() != null)
            if (!sightingList.get(t).getLatitude().equals(Latitudes.NONE) && !sightingList.get(t).getLongitude().equals(Longitudes.NONE)) {
                double lat = sightingList.get(t).getLatDegrees();
                lat = lat + sightingList.get(t).getLatMinutes()/60.0;
                lat = lat + (sightingList.get(t).getLatSeconds()/60.0)/60.0;
                if (sightingList.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = sightingList.get(t).getLonDegrees();
                lon = lon + sightingList.get(t).getLonMinutes()/60.0;
                lon = lon + (sightingList.get(t).getLonSeconds()/60.0)/60.0;
                if (sightingList.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sightingList.get(t), app, true);
                foundPosition = true;
            }
            // If the sighting did not have a position use the location's
            if (foundPosition == false) {
                Location tempLocation = app.getDBI().find(new Location(sightingList.get(t).getLocationName()));
                double lat = tempLocation.getLatDegrees();
                lat = lat + tempLocation.getLatMinutes()/60.0;
                lat = lat + (tempLocation.getLatSeconds()/60.0)/60.0;
                if (tempLocation.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = tempLocation.getLonDegrees();
                lon = lon + tempLocation.getLonMinutes()/60.0;
                lon = lon + (tempLocation.getLonSeconds()/60.0)/60.0;
                if (tempLocation.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), sightingList.get(t), app, true);
            }
        }
        this.dispose();
        // Open Map
        app.getMapOffline().changeTitle("WildLog Map - Offline: " + element.getPrimaryName() + " - Distribution");
        app.getMapOffline().showMap();
    }//GEN-LAST:event_btnViewSightingsAndDistributionMapActionPerformed

    private void btnViewAllSightingsForVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAllSightingsForVisitActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, false);
        // Load points
        Sighting tempSighting = new Sighting();
        tempSighting.setVisitName(visit.getName());
        List<Sighting> sightings = app.getDBI().list(tempSighting);
        for (int t = 0; t < sightings.size(); t++) {
            if (sightings.get(t).getLatitude() != null && sightings.get(t).getLongitude() != null)
            if (!sightings.get(t).getLatitude().equals(Latitudes.NONE) && !sightings.get(t).getLongitude().equals(Longitudes.NONE)) {
                double lat = sightings.get(t).getLatDegrees();
                lat = lat + sightings.get(t).getLatMinutes()/60.0;
                lat = lat + (sightings.get(t).getLatSeconds()/60.0)/60.0;
                if (sightings.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = sightings.get(t).getLonDegrees();
                lon = lon + sightings.get(t).getLonMinutes()/60.0;
                lon = lon + (sightings.get(t).getLonSeconds()/60.0)/60.0;
                if (sightings.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), sightings.get(t), app, false);
            }
        }
        this.dispose();
        // Open Map
        if (app.getWildLogOptions().isIsOnlinemapTheDefault()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + visit.getName());
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + visit.getName());
            app.getMapOffline().showMap();
        }
    }//GEN-LAST:event_btnViewAllSightingsForVisitActionPerformed

    private void btnViewSingleSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewSingleSightingActionPerformed
        // Clear old points
        UtilsMapGenerator.clearMap(app, false);
        // Load points
        if (sighting != null) {
            if (sighting.getLatitude() != null && sighting.getLongitude() != null)
            if (!sighting.getLatitude().equals(Latitudes.NONE) && !sighting.getLongitude().equals(Longitudes.NONE)) {
                double lat = sighting.getLatDegrees();
                lat = lat + sighting.getLatMinutes()/60.0;
                lat = lat + (sighting.getLatSeconds()/60.0)/60.0;
                if (sighting.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                double lon = sighting.getLonDegrees();
                lon = lon + sighting.getLonMinutes()/60.0;
                lon = lon + (sighting.getLonSeconds()/60.0)/60.0;
                if (sighting.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilsMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sighting, app, false);
            }
        }
        this.dispose();
        // Open Map
        if (app.getWildLogOptions().isIsOnlinemapTheDefault()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + visit.getName() + " (Observations)");
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + visit.getName() + " (Observations)");
            app.getMapOffline().showMap();
        }
    }//GEN-LAST:event_btnViewSingleSightingActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpenKmlViewer;
    private javax.swing.JButton btnViewAllSightingsForElement;
    private javax.swing.JButton btnViewAllSightingsForLocation;
    private javax.swing.JButton btnViewAllSightingsForVisit;
    private javax.swing.JButton btnViewDistributionMap;
    private javax.swing.JButton btnViewLocation;
    private javax.swing.JButton btnViewSightingsAndDistributionMap;
    private javax.swing.JButton btnViewSingleSighting;
    private javax.swing.JButton jButton5;
    // End of variables declaration//GEN-END:variables
}
