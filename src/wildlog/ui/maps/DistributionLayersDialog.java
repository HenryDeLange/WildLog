package wildlog.ui.maps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.filters.MapLayersFilter;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class DistributionLayersDialog extends JDialog {
    private Map<String, Path> mapAllLayers = new HashMap<>(25);
    private List<Path> lstSelectedPaths = null;
    private final String selectedSpecies;
    
    public DistributionLayersDialog(JFrame inParent, String inSelectedSpecies) {
        super(inParent);
        selectedSpecies = inSelectedSpecies;
        initComponents();
        // Setup the escape key
        final DistributionLayersDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Position the dialog
        UtilsDialog.setDialogToCenter(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Load Species layers
        if (Files.exists(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath())) {
            try {
                Files.walkFileTree(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path inPath, BasicFileAttributes inAttributes) throws IOException {
                        String filename = inPath.getFileName().toString().toUpperCase();
                        if (filename.endsWith("SHP")) {
                            mapAllLayers.put(inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        else
                        if (filename.endsWith("TIF") || filename.endsWith("TIFF")) {
                            mapAllLayers.put(inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        // Load the layers table
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblSpeciesLayers);
        UtilsTableGenerator.setupDistributionMapLayerTable(WildLogApp.getApplication(), tblSpeciesLayers, mapAllLayers.keySet());
        // If we are dealing with only one species, then automatically select it
        if (selectedSpecies != null && !selectedSpecies.trim().isEmpty()) {
            // Wag eers vir die table om klaar te load voor ek iets probeer select
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < tblSpeciesLayers.getRowCount(); t++) {
                        if (tblSpeciesLayers.getValueAt(t, 1).equals(selectedSpecies)) {
                            tblSpeciesLayers.getSelectionModel().setSelectionInterval(t, t);
                            int scrollRow = t;
                            if (t < (tblSpeciesLayers.getRowCount()) - 1) {
                                scrollRow = t + 1;
                            }
                            tblSpeciesLayers.scrollRectToVisible(tblSpeciesLayers.getCellRect(scrollRow, 0, true));
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        bntAddSpeciesLayer = new javax.swing.JButton();
        btnRemoveSpeciesLayer = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblSpeciesLayers = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Species Distribution Layers");
        setMinimumSize(new java.awt.Dimension(800, 560));
        setModal(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Species Distribution Layers");

        jLabel2.setText("<html>Select the Distribution Layers to use for the map (maximum of 5 at once).<br/>WildLog will automatically link distribution maps in the Workspace to the scientific names of know Creatures.<br/>Layers marked in green is sucessfully linked to a Creature, layers in orange or red could not be linked.<br/>Use the Add and Remove buttons below to manage the layers, or manually copy the files to the 'Maps\\Species' folder in the Workspace.</html>");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        bntAddSpeciesLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        bntAddSpeciesLayer.setToolTipText("Add a new layer to the WorkSpace.");
        bntAddSpeciesLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bntAddSpeciesLayer.setFocusPainted(false);
        bntAddSpeciesLayer.setFocusable(false);
        bntAddSpeciesLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        bntAddSpeciesLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntAddSpeciesLayerActionPerformed(evt);
            }
        });

        btnRemoveSpeciesLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnRemoveSpeciesLayer.setToolTipText("Remove the selected layer from the WorkSpace.");
        btnRemoveSpeciesLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveSpeciesLayer.setFocusPainted(false);
        btnRemoveSpeciesLayer.setFocusable(false);
        btnRemoveSpeciesLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnRemoveSpeciesLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSpeciesLayerActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Species Distribution Layers:");

        tblSpeciesLayers.setAutoCreateRowSorter(true);
        tblSpeciesLayers.setBackground(new java.awt.Color(248, 244, 244));
        tblSpeciesLayers.setFocusable(false);
        tblSpeciesLayers.setSelectionBackground(new java.awt.Color(66, 81, 43));
        jScrollPane12.setViewportView(tblSpeciesLayers);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveSpeciesLayer, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(bntAddSpeciesLayer, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(bntAddSpeciesLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnRemoveSpeciesLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(405, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane12)
                        .addGap(5, 5, 5))))
        );

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSave.setToolTipText("Close this dialog and confirm the active layers to be displayed on the map.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntAddSpeciesLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntAddSpeciesLayerActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Select the Map Layer to import.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new MapLayersFilter());
        final DistributionLayersDialog thisHandle = this;
        int result = UtilsDialog.showDialogBackgroundWrapper(this, new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(thisHandle);
            }
        });
        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            File file = fileChooser.getSelectedFile();
            if (Files.exists(file.toPath())) {
                Path sourcePath = file.toPath();
                String filename = sourcePath.getFileName().toString();
                // Get the destination path
                Path destinationPath = null;
                if (filename.toLowerCase().endsWith(".tif") || filename.toLowerCase().endsWith(".tiff")) {
                    destinationPath = WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath().resolve(filename);
                }
                else
                if (filename.toLowerCase().endsWith(".shp")) {
                    destinationPath = WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath()
                            .resolve(filename.substring(0, filename.lastIndexOf('.'))).resolve(filename);
                }
                // If the file does not yet exist copy it
                if (destinationPath != null && !Files.exists(destinationPath) && !mapAllLayers.containsKey(filename)) {
                    if (filename.toLowerCase().endsWith(".tif") || filename.toLowerCase().endsWith(".tiff")) {
                        // Copy the new GeoTiff layer
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath, false, false);
                    }
                    else
                    if (filename.toLowerCase().endsWith(".shp")) {
                        // Copy the new Shapefile layer
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath, false, false);
                        String shapefilename = filename.substring(0, filename.lastIndexOf('.')) + ".dbf";
                        UtilsFileProcessing.copyFile(sourcePath.getParent().resolve(shapefilename), destinationPath.getParent().resolve(shapefilename), false, false);
                        shapefilename = filename.substring(0, filename.lastIndexOf('.')) + ".prj";
                        UtilsFileProcessing.copyFile(sourcePath.getParent().resolve(shapefilename), destinationPath.getParent().resolve(shapefilename), false, false);
                        shapefilename = filename.substring(0, filename.lastIndexOf('.')) + ".shx";
                        UtilsFileProcessing.copyFile(sourcePath.getParent().resolve(shapefilename), destinationPath.getParent().resolve(shapefilename), false, false);
                    }
                }
                else {
                    getGlassPane().setVisible(true);
                    JOptionPane.showMessageDialog(this,
                            "A similar layer already exists in this Workspace. Please rename the new layer and try again.",
                            "Duplicate Layer", JOptionPane.ERROR_MESSAGE);
                    getGlassPane().setVisible(false);
                }
                // Add the layer to the hashmap and reload the UI
                if (destinationPath != null) {
                    mapAllLayers.put(filename, destinationPath.toAbsolutePath());
                    UtilsTableGenerator.setupDistributionMapLayerTable(WildLogApp.getApplication(), tblSpeciesLayers, mapAllLayers.keySet());
                }
            }
        }
    }//GEN-LAST:event_bntAddSpeciesLayerActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (tblSpeciesLayers.getSelectedRowCount() <= 5) {
            lstSelectedPaths = new ArrayList<>(tblSpeciesLayers.getSelectedRowCount());
            int[] selectedRows = tblSpeciesLayers.getSelectedRows();
            for (int t = 0; t < selectedRows.length; t++) {
                Path path = mapAllLayers.get(tblSpeciesLayers.getModel().getValueAt(tblSpeciesLayers.convertRowIndexToModel(selectedRows[t]), 0));
                if (path != null) {
                    lstSelectedPaths.add(path);
                }
            }
            setVisible(false);
            dispose();
        }
        else {
            getGlassPane().setVisible(true);
                    JOptionPane.showMessageDialog(this,
                            "Please select only 5 (or fewer) Distribution Layers.",
                            "Too Many Layers Selected", JOptionPane.WARNING_MESSAGE);
                    getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRemoveSpeciesLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSpeciesLayerActionPerformed
        if (tblSpeciesLayers.getSelectedRowCount() == 1) {
            String layerName = tblSpeciesLayers.getModel().getValueAt(tblSpeciesLayers.convertRowIndexToModel(tblSpeciesLayers.getSelectedRow()), 0).toString();
            if (layerName != null && !layerName.trim().isEmpty()) {
                try {
                    if (layerName.toLowerCase().endsWith(".tif") || layerName.toLowerCase().endsWith(".tiff")) {
                        // Delete the file
                        UtilsFileProcessing.deleteRecursive(mapAllLayers.get(layerName).toFile());
                    }
                    else 
                    if (layerName.toLowerCase().endsWith(".shp")) {
                        // Delete the files
                        UtilsFileProcessing.deleteRecursive(mapAllLayers.get(layerName).getParent().toFile());
                    }
                    // Update the UI
                    mapAllLayers.remove(layerName);
                    UtilsTableGenerator.setupDistributionMapLayerTable(WildLogApp.getApplication(), tblSpeciesLayers, mapAllLayers.keySet());
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                    getGlassPane().setVisible(true);
                    JOptionPane.showMessageDialog(this,
                            "The layer could not be deleted successfully. Please make sure the file isn't in use and try again, or delete it manually.",
                            "Could Not Delete", JOptionPane.ERROR_MESSAGE);
                    getGlassPane().setVisible(false);
                }
            }
            else {
                getGlassPane().setVisible(true);
                JOptionPane.showMessageDialog(this,
                        "Please select an existing layer to be deleted.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
                getGlassPane().setVisible(false);
            }
        }
        else {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(this,
                    "Please select one layer to be deleted at a time.",
                    "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            getGlassPane().setVisible(false);
        }
    }//GEN-LAST:event_btnRemoveSpeciesLayerActionPerformed

    public List<Path> getLstSelectedPaths() {
        return lstSelectedPaths;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntAddSpeciesLayer;
    private javax.swing.JButton btnRemoveSpeciesLayer;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JTable tblSpeciesLayers;
    // End of variables declaration//GEN-END:variables
}