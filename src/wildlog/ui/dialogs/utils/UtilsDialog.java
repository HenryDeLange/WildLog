package wildlog.ui.dialogs.utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.JFXPanel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogFileType;
import wildlog.ui.reports.helpers.FilterProperties;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.utils.UtilsTime;


public final class UtilsDialog {

    private UtilsDialog() {
    }
    
    public static void setupGlassPaneOnMainFrame(JFrame inFrame) {
        // Setup the glassPane for modal popups
        JPanel glassPane = (JPanel)inFrame.getGlassPane();
        glassPane.setLayout(new BorderLayout());
        JPanel background = new JPanel();
        background.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
        glassPane.add(background, BorderLayout.CENTER);
        glassPane.addMouseListener(new MouseAdapter() {});
        glassPane.addKeyListener(new KeyAdapter() {});
    }

    public static void addModalBackgroundPanel(RootPaneContainer inParentContainer, Window inPopupWindow) {
        // Note: The actual background colour, etc. is set once in the WildLogApp class.
        final JPanel glassPane = (JPanel)inParentContainer.getGlassPane();
        glassPane.setVisible(true);
        inPopupWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                glassPane.setVisible(false);
            }
        });
    }

    public static void addModalBackgroundPanel(JDialog inParentContainer, Window inPopupWindow) {
        // Setup the glassPane for modal popups
        final JPanel glassPane = (JPanel)inParentContainer.getGlassPane();
        glassPane.setLayout(new BorderLayout());
        JPanel background = new JPanel();
        background.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
        glassPane.removeAll();
        glassPane.add(background, BorderLayout.CENTER);
        // The inWindow will be null if we just want to set the background on a dialog's own GlassPane (for JOptionPane popups).
        if (inPopupWindow != null) {
            glassPane.setVisible(true);
            // Setup the hiding of the pane
            inPopupWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    glassPane.setVisible(false);
                }
            });
        }
    }

    public static void setDialogToCenter(Component inParentComponent, Component inComponentToCenter) {
        Point point = inParentComponent.getLocation();
        inComponentToCenter.setLocation(
                point.x + (inParentComponent.getWidth() - inComponentToCenter.getWidth())/2,
                point.y + (inParentComponent.getHeight() - inComponentToCenter.getHeight())/2);
    }

    public static ActionListener addEscapeKeyListener(final JDialog inDialog) {
        ActionListener escListiner = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        inDialog.setVisible(false);
                        inDialog.dispose();
                    }
                };
        inDialog.getRootPane().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return escListiner;
    }

    public static void showExifPopup(final WildLogApp inApp, Path inFile) {
        if (inFile != null) {
            if (Files.exists(inFile)) {
                final JFrame frame = new JFrame("EXIF Meta Data: " + inFile);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/EXIF.png")).getImage());
                JTextPane txtPane = new JTextPane();
                txtPane.setContentType("text/html");
                txtPane.setEditable(false);
                String temp = "";
                try {
                    Metadata meta = JpegMetadataReader.readMetadata(inFile.toFile());
                    Iterator<Directory> directories = meta.getDirectories().iterator();
                    breakAllWhiles: while (directories.hasNext()) {
                        Directory directory = directories.next();
                        Collection<Tag> tags = directory.getTags();
                        for (Tag tag : tags) {
                            String name = tag.getTagName();
                            String description = tag.getDescription();
                            temp = temp + "<b>" + name + ":</b> " + description + "<br/>";
                        }
                    }
                    txtPane.setText(temp);
                    txtPane.setCaretPosition(0);
                    JScrollPane scroll = new JScrollPane(txtPane);
                    scroll.setPreferredSize(new Dimension(580, 750));
                    frame.getContentPane().add(scroll);
                    frame.pack();
                    frame.setLocationRelativeTo(inApp.getMainFrame());
                    ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.setVisible(false);
                            frame.dispose();
                        }
                    };
                    frame.getRootPane().registerKeyboardAction(
                            escListiner,
                            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                            JComponent.WHEN_IN_FOCUSED_WINDOW);
                    frame.setVisible(true);
                }
                catch (IOException ex) {
                    System.err.println("Error showing EXIF data for: " + inFile);
                    ex.printStackTrace(System.err);
                }
                catch (JpegProcessingException ex) {
                    System.err.println("Error showing EXIF data for: " + inFile);
                    ex.printStackTrace(System.err);
                    UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                    "Could not process the file.",
                                    "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                            return -1;
                        }
                    });
                }
            }
            else {
                System.err.println("Error showing EXIF data for: " + inFile);
                UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "Could not access the file.",
                                "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
            }
        }
        else {
            System.err.println("Error showing EXIF data for: " + inFile);
            UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Could not access the file.",
                            "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                    return -1;
                }
            });
        }
    }

    public static void showExifPopup(String inID, int inIndex, final WildLogApp inApp) {
        List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(inID));
        if (files.size() > 0) {
            if (WildLogFileType.IMAGE.equals(files.get(inIndex).getFileType())) {
                showExifPopup(inApp, files.get(inIndex).getAbsolutePath());
            }
            else {
                UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "Only image metadata can be shown.",
                                "Not An Image", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                });
            }
        }
    }

    public interface DialogWrapper {
        public int showDialog();
    }

    public static int showDialogBackgroundWrapper(RootPaneContainer inParentContainer, DialogWrapper inDialogWrapper) {
        // TODO: Maybe one day replace this method with a propper custom message/dialog class that will work in a similar way to the JOptionPane, but is setup to use the Glasspane, etc.
        if (inParentContainer != null) {
            inParentContainer.getGlassPane().setVisible(true);
        }
        int result = inDialogWrapper.showDialog();
        if (inParentContainer != null) {
            inParentContainer.getGlassPane().setVisible(false);
        }
        return result;
    }
    
    public static void doFiltering(final List<Sighting> lstOriginalData, List<Sighting> lstFilteredData, 
            List<Element> lstFilteredElements, List<Location> lstFilteredLocations, List<Visit> lstFilteredVisits,
            FilterProperties filterProperties, JLabel lblFilteredRecords, AbstractReport activeReport, JFXPanel jfxReportChartPanel) {
        // NOTE: Don't create a new ArrayList (clear existing instead), because the reports are holding on to the reference 
        //       and will be stuck with an old list otherwise. Easiest to just keep the reference constant than to try and 
        //       update the all reports everytime (the active report already gets updated explicitly).
        lstFilteredData.clear();
        // All filters need to be taken into account all the time, even if only one was changed the results must still fullfill the other filters...
        for (Sighting sighting : lstOriginalData) {
            // Check filtered Elements
            if (lstFilteredElements != null) {
                boolean found = false;
                for (Element element : lstFilteredElements) {
                    if (sighting.getElementName().equals(element.getPrimaryName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Locations
            if (lstFilteredLocations != null) {
                boolean found = false;
                for (Location location : lstFilteredLocations) {
                    if (sighting.getLocationName().equals(location.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Visits
            if (lstFilteredVisits != null) {
                boolean found = false;
                for (Visit visit : lstFilteredVisits) {
                    if (sighting.getVisitName().equals(visit.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            // Check filtered Sightings
            // TODO: mmm dit gaan dalk tricky wees...
            
            // Check filtered Properties
            if (filterProperties != null) {
                // Date
                if (filterProperties.getStartDate() != null) {
                    if (UtilsTime.getLocalDateFromDate(sighting.getDate()).isBefore(filterProperties.getStartDate())) {
                        continue;
                    }
                }
                if (filterProperties.getEndDate() != null) {
                    if (UtilsTime.getLocalDateFromDate(sighting.getDate()).isAfter(filterProperties.getEndDate())) {
                        continue;
                    }
                }
                // Time
                if (filterProperties.getStartTime() != null) {
                    if (UtilsTime.getLocalTimeFromDate(sighting.getDate()).isBefore(filterProperties.getStartTime())) {
                        continue;
                    }
                }
                if (filterProperties.getEndTime() != null) {
                    if (UtilsTime.getLocalTimeFromDate(sighting.getDate()).isAfter(filterProperties.getEndTime())) {
                        continue;
                    }
                }
                // Time of day
                if (filterProperties.getActiveTimes() != null) {
                    boolean found = false;
                    for (ActiveTimeSpesific activeTimeSpesific : filterProperties.getActiveTimes()) {
                        if (activeTimeSpesific.equals(sighting.getTimeOfDay())) {
                            found = true;
                            break;
                        }
                        if (activeTimeSpesific.equals(ActiveTimeSpesific.UNKNOWN)) {
                            if (sighting.getTimeOfDay() == null || ActiveTimeSpesific.NONE.equals(sighting.getTimeOfDay())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Moonlight
                if (filterProperties.getMoonlights() != null) {
                    boolean found = false;
                    for (Moonlight moonlight : filterProperties.getMoonlights()) {
                        if (moonlight.equals(sighting.getMoonlight())) {
                            found = true;
                            break;
                        }
                        if (moonlight.equals(Moonlight.UNKNOWN)) {
                            if (sighting.getMoonlight() == null || Moonlight.NONE.equals(sighting.getMoonlight())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Moonphase
                if (!(filterProperties.isMoonphaseIsLess() && filterProperties.isMoonphaseIsMore()) && sighting.getMoonPhase()  >= 0) {
                    boolean found = false;
                    if (filterProperties.getMoonphase() == sighting.getMoonPhase()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isMoonphaseIsLess() && sighting.getMoonPhase() < filterProperties.getMoonphase()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isMoonphaseIsMore() && sighting.getMoonPhase() > filterProperties.getMoonphase()) {
                        found = true;
                    }
                    if (!found) {
                        continue;
                    }
                }
                
                // Visit Type
                if (filterProperties.getVisitTypes() != null) {
                    boolean found = false;
                    Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                    for (VisitType visitType : filterProperties.getVisitTypes()) {
                        if (visit != null) {
                            if (visitType.equals(visit.getType())) {
                                found = true;
                                break;
                            }
                            if (visitType.equals(VisitType.UNKNOWN)) {
                                if (visit.getType() == null || VisitType.NONE.equals(visit.getType())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Evidence
                if (filterProperties.getEvidences() != null) {
                    boolean found = false;
                    for (SightingEvidence sightingEvidence : filterProperties.getEvidences()) {
                        if (sightingEvidence.equals(sighting.getSightingEvidence())) {
                            found = true;
                            break;
                        }
                        if (sightingEvidence.equals(SightingEvidence.UNKNOWN)) {
                            if (sighting.getSightingEvidence() == null || SightingEvidence.NONE.equals(sighting.getSightingEvidence())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Life Status
                if (filterProperties.getLifeStatuses() != null) {
                    boolean found = false;
                    for (LifeStatus lifeStatus : filterProperties.getLifeStatuses()) {
                        if (lifeStatus.equals(sighting.getLifeStatus())) {
                            found = true;
                            break;
                        }
                        if (lifeStatus.equals(LifeStatus.UNKNOWN)) {
                            if (sighting.getLifeStatus() == null || LifeStatus.NONE.equals(sighting.getLifeStatus())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Time Accuracy
                if (filterProperties.getTimeAccuracies() != null) {
                    boolean found = false;
                    for (TimeAccuracy timeAccuracy : filterProperties.getTimeAccuracies()) {
                        if (timeAccuracy.equals(sighting.getTimeAccuracy())) {
                            found = true;
                            break;
                        }
                        if (timeAccuracy.equals(TimeAccuracy.UNKNOWN)) {
                            if (sighting.getTimeAccuracy() == null || TimeAccuracy.NONE.equals(sighting.getTimeAccuracy())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Sighting Certianty
                if (filterProperties.getCertainties() != null) {
                    boolean found = false;
                    for (Certainty certainty : filterProperties.getCertainties()) {
                        if (certainty.equals(sighting.getCertainty())) {
                            found = true;
                            break;
                        }
                        if (certainty.equals(Certainty.UNKNOWN)) {
                            if (sighting.getCertainty() == null || Certainty.NONE.equals(sighting.getCertainty())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // GPS Certainty
                if (filterProperties.getGPSAccuracies() != null) {
                    boolean found = false;
                    for (GPSAccuracy gpsAccuracy : filterProperties.getGPSAccuracies()) {
                        if (gpsAccuracy.equals(sighting.getGPSAccuracy())) {
                            found = true;
                            break;
                        }
                        if (gpsAccuracy.equals(GPSAccuracy.UNKNOWN)) {
                            if (sighting.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(sighting.getGPSAccuracy())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Age
                if (filterProperties.getAges() != null) {
                    boolean found = false;
                    for (Age age : filterProperties.getAges()) {
                        if (age.equals(sighting.getAge())) {
                            found = true;
                            break;
                        }
                        if (age.equals(Age.UNKNOWN)) {
                            if (sighting.getAge() == null || Age.NONE.equals(sighting.getAge())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Sex
                if (filterProperties.getSexes() != null) {
                    boolean found = false;
                    for (Sex sex : filterProperties.getSexes()) {
                        if (sex.equals(sighting.getSex())) {
                            found = true;
                            break;
                        }
                        if (sex.equals(Sex.UNKNOWN)) {
                            if (sighting.getSex() == null || Sex.NONE.equals(sighting.getSex())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Number of individuals
                if (!(filterProperties.isNumberOfElementsIsLess()&& filterProperties.isNumberOfElementsIsMore()) && sighting.getNumberOfElements() >= 0) {
                    boolean found = false;
                    if (filterProperties.getNumberOfElements() == sighting.getNumberOfElements()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isNumberOfElementsIsLess() && sighting.getNumberOfElements() < filterProperties.getNumberOfElements()) {
                        found = true;
                    }
                    else
                    if (filterProperties.isNumberOfElementsIsMore() && sighting.getNumberOfElements() > filterProperties.getNumberOfElements()) {
                        found = true;
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Tag
                if (sighting.getTag() == null || sighting.getTag().trim().isEmpty()) {
                    if (!filterProperties.isIncludeEmptyTags()) {
                        continue;
                    }
                }
                else
                if (filterProperties.getTags() != null && !filterProperties.getTags().isEmpty()) {
                    boolean found = false;
                    for (String tag : filterProperties.getTags()) {
                        if (!tag.trim().isEmpty() && sighting.getTag().trim().toLowerCase().contains(tag.trim().toLowerCase())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
                // Element Type
                if (filterProperties.getElementTypes()!= null) {
                    boolean found = false;
                    Element element = WildLogApp.getApplication().getDBI().find(new Element(sighting.getElementName()));
                    for (ElementType elementType : filterProperties.getElementTypes()) {
                        if (element != null) {
                            if (elementType.equals(element.getType())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        continue;
                    }
                }
            }
            // If we haven't breaked from the for loop yet (aka continued to the next record), 
            // then this record can be added to the list
            lstFilteredData.add(sighting.cloneShallow());
        }
        lblFilteredRecords.setText(Integer.toString(lstFilteredData.size()));
        // Redraw the chart
        if (activeReport != null) {
            activeReport.setDataList(lstFilteredData);
            activeReport.createReport(jfxReportChartPanel.getScene());
        }
    }

}
