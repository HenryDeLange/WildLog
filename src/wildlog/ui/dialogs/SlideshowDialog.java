package wildlog.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.WildLogPaths;
import wildlog.movies.utils.UtilsMovies;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;


public class SlideshowDialog extends JDialog {
    private WildLogApp app;
    private Visit visit;
    private Location location;
    private Element element;

    public SlideshowDialog(Visit inVisit, Location inLocation, Element inElement) {
        super();
        // Set passed in values
        app = (WildLogApp) Application.getInstance();
        visit = inVisit;
        location = inLocation;
        element = inElement;

        // Auto generated code
        initComponents();

        // Determine what buttons to show
        if (visit == null) {
            btnSlideshowVisit.setVisible(false);
            btnSlideshowVisitSightings.setVisible(false);
        }
        if (location == null) {
            btnSlideshowLocation.setVisible(false);
            btnSlideshowLocationSightings.setVisible(false);
        }
        if (element == null) {
            btnSlideshowElement.setVisible(false);
            btnSlideshowElementSightings.setVisible(false);
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
        btnSlideshowVisit = new javax.swing.JButton();
        btnSlideshowVisitSightings = new javax.swing.JButton();
        btnSlideshowLocation = new javax.swing.JButton();
        btnSlideshowLocationSightings = new javax.swing.JButton();
        btnSlideshowElement = new javax.swing.JButton();
        btnSlideshowElementSightings = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(SlideshowDialog.class);
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(new ImageIcon(app.getClass().getResource("resources/images/Slideshow.gif")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnSlideshowVisit.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowVisit.setText(resourceMap.getString("btnSlideshowVisit.text")); // NOI18N
        btnSlideshowVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowVisit.setFocusPainted(false);
        btnSlideshowVisit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowVisit.setIconTextGap(10);
        btnSlideshowVisit.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowVisit.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisit.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisit.setName("btnSlideshowVisit"); // NOI18N
        btnSlideshowVisit.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowVisitActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowVisit);

        btnSlideshowVisitSightings.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowVisitSightings.setText(resourceMap.getString("btnSlideshowVisitSightings.text")); // NOI18N
        btnSlideshowVisitSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowVisitSightings.setFocusPainted(false);
        btnSlideshowVisitSightings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowVisitSightings.setIconTextGap(10);
        btnSlideshowVisitSightings.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowVisitSightings.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisitSightings.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisitSightings.setName("btnSlideshowVisitSightings"); // NOI18N
        btnSlideshowVisitSightings.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowVisitSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowVisitSightingsActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowVisitSightings);

        btnSlideshowLocation.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowLocation.setText(resourceMap.getString("btnSlideshowLocation.text")); // NOI18N
        btnSlideshowLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowLocation.setFocusPainted(false);
        btnSlideshowLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowLocation.setIconTextGap(10);
        btnSlideshowLocation.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowLocation.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocation.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocation.setName("btnSlideshowLocation"); // NOI18N
        btnSlideshowLocation.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowLocationActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowLocation);

        btnSlideshowLocationSightings.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowLocationSightings.setText(resourceMap.getString("btnSlideshowLocationSightings.text")); // NOI18N
        btnSlideshowLocationSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowLocationSightings.setFocusPainted(false);
        btnSlideshowLocationSightings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowLocationSightings.setIconTextGap(10);
        btnSlideshowLocationSightings.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowLocationSightings.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocationSightings.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocationSightings.setName("btnSlideshowLocationSightings"); // NOI18N
        btnSlideshowLocationSightings.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowLocationSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowLocationSightingsActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowLocationSightings);

        btnSlideshowElement.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowElement.setText(resourceMap.getString("btnSlideshowElement.text")); // NOI18N
        btnSlideshowElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowElement.setFocusPainted(false);
        btnSlideshowElement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowElement.setIconTextGap(10);
        btnSlideshowElement.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowElement.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowElement.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowElement.setName("btnSlideshowElement"); // NOI18N
        btnSlideshowElement.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowElementActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowElement);

        btnSlideshowElementSightings.setIcon(resourceMap.getIcon("btnSlideshowLocation.icon")); // NOI18N
        btnSlideshowElementSightings.setText(resourceMap.getString("btnSlideshowElementSightings.text")); // NOI18N
        btnSlideshowElementSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshowElementSightings.setFocusPainted(false);
        btnSlideshowElementSightings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshowElementSightings.setIconTextGap(10);
        btnSlideshowElementSightings.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshowElementSightings.setMaximumSize(new java.awt.Dimension(240, 35));
        btnSlideshowElementSightings.setMinimumSize(new java.awt.Dimension(240, 35));
        btnSlideshowElementSightings.setName("btnSlideshowElementSightings"); // NOI18N
        btnSlideshowElementSightings.setPreferredSize(new java.awt.Dimension(240, 35));
        btnSlideshowElementSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowElementSightingsActionPerformed(evt);
            }
        });
        getContentPane().add(btnSlideshowElementSightings);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSlideshowVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowVisitActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                List<WildLogFile> files = app.getDBI().list(new WildLogFile("VISIT-" + visit.getName()));
                for (WildLogFile tempFile : files) {
                    if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                        if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpg") ||
                            tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpeg")) {
                            slideshowList.add(tempFile.getOriginalFotoLocation(true));
                        }
                    }
                }
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + visit.getName() + ".mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowVisitActionPerformed

    private void btnSlideshowVisitSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowVisitSightingsActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                Sighting temp = new Sighting();
                temp.setVisitName(visit.getName());
                List<Sighting> sightingList = app.getDBI().list(temp);
                Collections.sort(sightingList);
                for (int t = 0; t < sightingList.size(); t++) {
                    Sighting tempSighting = sightingList.get(t);
                    List<WildLogFile> files = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                    for (WildLogFile tempFile : files) {
                        if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                            // Only using JPGs because otherwise it might break the video
                            if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpg) ||
                                tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpeg)) {
                                slideshowList.add(tempFile.getOriginalFotoLocation(true));
                            }
                        }
                    }
                }
                // Now create the slideshow
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + visit.getName() + "_Sightings.mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowVisitSightingsActionPerformed

    private void btnSlideshowLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowLocationActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                List<WildLogFile> files = app.getDBI().list(new WildLogFile("LOCATION-" + location.getName()));
                for (WildLogFile tempFile : files) {
                    if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                        if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpg") ||
                            tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpeg")) {
                            slideshowList.add(tempFile.getOriginalFotoLocation(true));
                        }
                    }
                }
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + location.getName() + ".mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowLocationActionPerformed

    private void btnSlideshowLocationSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowLocationSightingsActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                Sighting temp = new Sighting();
                temp.setLocationName(location.getName());
                List<Sighting> sightingList = app.getDBI().list(temp);
                Collections.sort(sightingList);
                for (int t = 0; t < sightingList.size(); t++) {
                    Sighting tempSighting = sightingList.get(t);
                    List<WildLogFile> files = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                    for (WildLogFile tempFile : files) {
                        if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                            // Only using JPGs because otherwise it might break the video
                            if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpg) ||
                                tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpeg)) {
                                slideshowList.add(tempFile.getOriginalFotoLocation(true));
                            }
                        }
                    }
                }
                // Now create the slideshow
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + location.getName() + "_Sightings.mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowLocationSightingsActionPerformed

    private void btnSlideshowElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowElementActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                List<WildLogFile> files = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
                for (WildLogFile tempFile : files) {
                    if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                        if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpg") ||
                            tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith("jpeg")) {
                            slideshowList.add(tempFile.getOriginalFotoLocation(true));
                        }
                    }
                }
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + element.getPrimaryName() + ".mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowElementActionPerformed

    private void btnSlideshowElementSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowElementSightingsActionPerformed
        UtilsConcurency.kickoffProgressbarTask(new Task(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Creating the Slideshow");
                List<String> slideshowList = new ArrayList<String>();
                Sighting temp = new Sighting();
                temp.setElementName(element.getPrimaryName());
                List<Sighting> sightingList = app.getDBI().list(temp);
                Collections.sort(sightingList);
                for (int t = 0; t < sightingList.size(); t++) {
                    Sighting tempSighting = sightingList.get(t);
                    List<WildLogFile> files = app.getDBI().list(new WildLogFile("SIGHTING-" + tempSighting.getSightingCounter()));
                    for (WildLogFile tempFile : files) {
                        if (WildLogFileType.IMAGE.equals(tempFile.getFotoType())) {
                            // Only using JPGs because otherwise it might break the video
                            if (tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpg) ||
                                tempFile.getOriginalFotoLocation(true).toLowerCase().endsWith(UtilsFileProcessing.jpeg)) {
                                slideshowList.add(tempFile.getOriginalFotoLocation(true));
                            }
                        }
                    }
                }
                // Now create the slideshow
                setMessage("Creating the Slideshow: (writing the file, this may take a while...)");
                UtilsMovies.generateSlideshow(slideshowList, app, WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath().substring(2) + element.getPrimaryName() + "_Sightings.mov");
                setMessage("Done with the Slideshow");
                return null;
            }
        });
        this.dispose();
    }//GEN-LAST:event_btnSlideshowElementSightingsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSlideshowElement;
    private javax.swing.JButton btnSlideshowElementSightings;
    private javax.swing.JButton btnSlideshowLocation;
    private javax.swing.JButton btnSlideshowLocationSightings;
    private javax.swing.JButton btnSlideshowVisit;
    private javax.swing.JButton btnSlideshowVisitSightings;
    private javax.swing.JButton jButton5;
    // End of variables declaration//GEN-END:variables
}
