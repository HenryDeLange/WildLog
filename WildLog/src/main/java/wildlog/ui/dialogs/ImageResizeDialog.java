package wildlog.ui.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.WildLogFileType;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.SpinnerFixer;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;


public class ImageResizeDialog extends JDialog {

    
    public ImageResizeDialog(Frame inParent) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[ImageResizeDialog]");
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbRating);
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
        SpinnerFixer.configureSpinners(spnSize);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Setup initial state
        lblPrimary.setVisible(false);
        lstPrimary.setVisible(false);
        lblSecondary.setVisible(false);
        lstSecondary.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnConfirm = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbRating = new javax.swing.JComboBox<>();
        scrSecondary = new javax.swing.JScrollPane();
        lstSecondary = new javax.swing.JList();
        rdbElements = new javax.swing.JRadioButton();
        rdbLocations = new javax.swing.JRadioButton();
        rdbVisits = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        scrPrimary = new javax.swing.JScrollPane();
        lstPrimary = new javax.swing.JList();
        lblPrimary = new javax.swing.JLabel();
        lblSecondary = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        spnSize = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        chkIncludeCategory = new javax.swing.JCheckBox();
        chkIncludeSightings = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Resize Images");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/EXIF_small.png")).getImage());
        setMinimumSize(new java.awt.Dimension(600, 480));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(600, 480));

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnConfirm.setToolTipText("Move the Observations from the selected Period to the new Period and then delete the initial Period.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("<html>Specify how the images should be resized.</html>");

        jLabel2.setText("Resize images with a rating of ");

        cmbRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbRating.setSelectedItem(ViewRating.VERY_GOOD);
        cmbRating.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lstSecondary.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lstSecondary.setFocusable(false);
        scrSecondary.setViewportView(lstSecondary);

        buttonGroup1.add(rdbElements);
        rdbElements.setText("Creatures");
        rdbElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbElements.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbElementsItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rdbLocations);
        rdbLocations.setText("Places");
        rdbLocations.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocations.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbLocationsItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rdbVisits);
        rdbVisits.setText("Periods");
        rdbVisits.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbVisits.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbVisitsItemStateChanged(evt);
            }
        });

        jLabel3.setText("and lower.");

        jLabel4.setText("Select the category to be resized:");

        lstPrimary.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lstPrimary.setFocusable(false);
        lstPrimary.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstPrimaryValueChanged(evt);
            }
        });
        scrPrimary.setViewportView(lstPrimary);

        lblPrimary.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblPrimary.setText("Creatures / Places");

        lblSecondary.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSecondary.setText("Periods");

        jLabel5.setText("Resize to ");

        spnSize.setModel(new javax.swing.SpinnerNumberModel(850, 0, 3072, 50));
        spnSize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel6.setText("px");

        chkIncludeCategory.setText("Resize Category Images");
        chkIncludeCategory.setToolTipText("If selected the images of the selected category will be resized.");
        chkIncludeCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        chkIncludeSightings.setSelected(true);
        chkIncludeSightings.setText("Resize Observations Images");
        chkIncludeSightings.setToolTipText("If selected the images of related Observations will also be resized.");
        chkIncludeSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPrimary)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnSize, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel6)
                        .addGap(25, 25, 25)
                        .addComponent(chkIncludeCategory)
                        .addGap(0, 0, 0)
                        .addComponent(chkIncludeSightings)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(scrPrimary, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSecondary)
                                    .addComponent(scrSecondary, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addComponent(jSeparator2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(5, 5, 5)
                                        .addComponent(cmbRating, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rdbElements)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rdbLocations)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rdbVisits))
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(rdbLocations)
                            .addComponent(rdbElements)
                            .addComponent(rdbVisits))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(spnSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(chkIncludeSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIncludeCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lblSecondary)
                        .addGap(3, 3, 3)
                        .addComponent(scrSecondary, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPrimary)
                        .addGap(3, 3, 3)
                        .addComponent(scrPrimary, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        List<WildLogFile> lstWildLogFiles = new ArrayList<>();
        List<Sighting> lstSightings = new ArrayList<>();
        if (rdbElements.isSelected() && !lstPrimary.getSelectionModel().isSelectionEmpty()) {
            for (Element element : (List<Element>) lstPrimary.getSelectedValuesList()) {
                if (chkIncludeCategory.isSelected()) {
                    lstWildLogFiles.addAll(WildLogApp.getApplication().getDBI().listWildLogFiles(
                            element.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class));
                }
                if (chkIncludeSightings.isSelected()) {
                    lstSightings.addAll(WildLogApp.getApplication().getDBI().listSightings(element.getID(), 0, 0, false, Sighting.class));
                }
            }
        }
        else
        if (rdbLocations.isSelected() && !lstPrimary.getSelectionModel().isSelectionEmpty()) {
            for (Location location : (List<Location>) lstPrimary.getSelectedValuesList()) {
                if (chkIncludeCategory.isSelected()) {
                    lstWildLogFiles.addAll(WildLogApp.getApplication().getDBI().listWildLogFiles(
                            location.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class));
                }
                if (chkIncludeSightings.isSelected()) {
                    lstSightings.addAll(WildLogApp.getApplication().getDBI().listSightings(0, 0, location.getID(), false, Sighting.class));
                }
            }
        }
        else
        if (rdbVisits.isSelected() && !lstSecondary.getSelectionModel().isSelectionEmpty()) {
            for (Visit visit : (List<Visit>) lstSecondary.getSelectedValuesList()) {
                if (chkIncludeCategory.isSelected()) {
                    lstWildLogFiles.addAll(WildLogApp.getApplication().getDBI().listWildLogFiles(
                            visit.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class));
                }
                if (chkIncludeSightings.isSelected()) {
                    lstSightings.addAll(WildLogApp.getApplication().getDBI().listSightings(0, 0, visit.getID(), false, Sighting.class));
                }
            }
        }
        // Check the rating
        if (chkIncludeSightings.isSelected()) {
            for (Sighting sighting : lstSightings) {
                if (checkRating(sighting)) {
                    lstWildLogFiles.addAll(WildLogApp.getApplication().getDBI().listWildLogFiles(
                            sighting.getWildLogFileID(), WildLogFileType.IMAGE, WildLogFile.class));
                }
            }
        }
        // Do the resize
        UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                WildLogApp.getApplication().getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                setProgress(1);
                setMessage("Starting resizing the images");
                double counter = 0.0;
                for (WildLogFile wildLogFile : lstWildLogFiles) {
                    try {
                        UtilsImageProcessing.resizeImage(wildLogFile, (int) spnSize.getValue());
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    setProgress(1 + (int) (counter++ / (double) lstWildLogFiles.size() * 98.0));
                    setMessage("Resize Images: " + getProgress() + "%");
                }
                setProgress(100);
                setMessage("Done resizing the images");
                WildLogApp.getApplication().getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                return null;
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnConfirmActionPerformed

    private boolean checkRating(Sighting inSighting) {
        if (ViewRating.NONE.equals((ViewRating) cmbRating.getSelectedItem())) {
            return true;
        }
        ViewRating viewRating;
        if (ViewRating.NONE.equals(inSighting.getViewRating())) {
            viewRating = ViewRating.NORMAL;
        }
        else {
            viewRating = inSighting.getViewRating();
        }
        boolean valid = false;
        for (ViewRating rating : ViewRating.values()) {
            if (rating.equals(viewRating)) {
                valid = true;
                break;
            }
            if (rating.equals((ViewRating) cmbRating.getSelectedItem())) {
                valid = false;
                break;
            }
        }
        return valid;
    }
    
    private void rdbElementsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbElementsItemStateChanged
        lblPrimary.setText("Creatures:");
        lblPrimary.setVisible(true);
        lstPrimary.setVisible(true);
        lstPrimary.setSelectionBackground(new Color(82, 115, 79));
        lblSecondary.setVisible(false);
        lstSecondary.setVisible(false);
        List<Element> lstElements = WildLogApp.getApplication().getDBI().listElements(null, null, null, Element.class);
        Collections.sort(lstElements);
        DefaultListModel model = new DefaultListModel();
        for (Element tempElement : lstElements) {
            model.addElement(tempElement);
        }
        lstPrimary.setModel(model);
    }//GEN-LAST:event_rdbElementsItemStateChanged

    private void rdbLocationsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbLocationsItemStateChanged
        lblPrimary.setText("Places:");
        lblPrimary.setVisible(true);
        lstPrimary.setVisible(true);
        lstPrimary.setSelectionBackground(new Color(67, 97, 113));
        lblSecondary.setVisible(false);
        lstSecondary.setVisible(false);
        List<Location> lstLocations = WildLogApp.getApplication().getDBI().listLocations(null, Location.class);
        Collections.sort(lstLocations);
        DefaultListModel model = new DefaultListModel();
        for (Location tempLocation : lstLocations) {
            model.addElement(tempLocation);
        }
        lstPrimary.setModel(model);
    }//GEN-LAST:event_rdbLocationsItemStateChanged

    private void rdbVisitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbVisitsItemStateChanged
        rdbLocationsItemStateChanged(null);
        lstSecondary.setModel(new DefaultListModel());
        lblSecondary.setVisible(true);
        lstSecondary.setVisible(true);
        lstSecondary.setSelectionBackground(new Color(96, 92, 116));
    }//GEN-LAST:event_rdbVisitsItemStateChanged

    private void lstPrimaryValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstPrimaryValueChanged
        if (rdbVisits.isSelected()) {
            List<Visit> lstVisits = new ArrayList<>();
            for (Location location : (List<Location>) lstPrimary.getSelectedValuesList()) {
                lstVisits.addAll(WildLogApp.getApplication().getDBI().listVisits(null, location.getID(), null, false, Visit.class));
            }
            Collections.sort(lstVisits);
            DefaultListModel model = new DefaultListModel();
            for (Visit tempVisit : lstVisits) {
                model.addElement(tempVisit);
            }
            lstSecondary.setModel(model);
        }
    }//GEN-LAST:event_lstPrimaryValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkIncludeCategory;
    private javax.swing.JCheckBox chkIncludeSightings;
    private javax.swing.JComboBox<String> cmbRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblPrimary;
    private javax.swing.JLabel lblSecondary;
    private javax.swing.JList lstPrimary;
    private javax.swing.JList lstSecondary;
    private javax.swing.JRadioButton rdbElements;
    private javax.swing.JRadioButton rdbLocations;
    private javax.swing.JRadioButton rdbVisits;
    private javax.swing.JScrollPane scrPrimary;
    private javax.swing.JScrollPane scrSecondary;
    private javax.swing.JSpinner spnSize;
    // End of variables declaration//GEN-END:variables
}
