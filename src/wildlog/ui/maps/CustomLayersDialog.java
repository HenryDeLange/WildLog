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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.filters.MapLayersFilter;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class CustomLayersDialog extends JDialog {
    private Map<String, Path> mapAllLayers = new HashMap<>(25);
    private List<Path> lstSelectedPaths = null;
    
    public CustomLayersDialog(JFrame inParent) {
        super(inParent);
        System.out.println("[CustomLayersDialog]");
        initComponents();
        // Setup the escape key
        final CustomLayersDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Position the dialog and setup modal background
        UtilsDialog.setDialogToCenter(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Set the initial empty model on the selected layers list
        lsbSelectedLayers.setModel(new DefaultListModel<>());
        // Load default layers
        for (BundledMapLayers layer : BundledMapLayers.values()) {
            Path path = WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(layer.getRelativePath());
            if (path.getFileName().toString().contains(".")) {
                mapAllLayers.put("[WILDLOG] " + layer.getRelativePath().getFileName().toString(), path);
            }
            else {
                for (int t = 0; t < 12; t++) {
                    mapAllLayers.put("[WILDLOG] " + layer.getRelativePath().getFileName().toString() 
                            + " - " + layer.getRelativePathForMonth(t).getFileName().toString(), path);
                }
            }
        }
        // Load Available Layers
        if (Files.exists(WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath())) {
            try {
                Files.walkFileTree(WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path inPath, BasicFileAttributes inAttributes) throws IOException {
                        String filename = inPath.getFileName().toString().toUpperCase();
                        if (filename.endsWith("SHP")) {
                            mapAllLayers.put("[CUSTOM] " + inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        else
                        if (filename.endsWith("TIF") || filename.endsWith("TIFF")) {
                            mapAllLayers.put("[CUSTOM] " + inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        // Load Species layers
        if (Files.exists(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath())) {
            try {
                Files.walkFileTree(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path inPath, BasicFileAttributes inAttributes) throws IOException {
                        String filename = inPath.getFileName().toString().toUpperCase();
                        if (filename.endsWith("SHP")) {
                            mapAllLayers.put("[SPECIES] " + inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        else
                        if (filename.endsWith("TIF") || filename.endsWith("TIFF")) {
                            mapAllLayers.put("[SPECIES] " + inPath.getFileName().toString(), inPath.toAbsolutePath());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        // Set the layers on the listbox
        List<String> keys = new ArrayList<>(mapAllLayers.keySet());
        Collections.sort(keys);
        lsbAllLayers.setModel(new DefaultComboBoxModel<>(keys.toArray(new String[keys.size()])));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        lsbAllLayers = new javax.swing.JList<>();
        bntAddToAllLayer = new javax.swing.JButton();
        btnRemoveFromAllLayer = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnSetAsSelectedLayer = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnMoveDown = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lsbSelectedLayers = new javax.swing.JList<>();
        btnMoveUp = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnRemoveFromSelectedLayer = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Custom Map Layers");
        setMinimumSize(new java.awt.Dimension(800, 560));
        setModal(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Custom Map Layers");

        jLabel2.setText("<html>Double-click a layer in the Available Layers list to add the layer to the Selected Layers list, or use the Set Active Layer button.<br/>Double-click a layer in the Active Layers list to remove it from the list, or use the Remove button.<br/>The selected layers can be ordered using the arrow buttons. The top layer in the list will be added to the map first (at the bottom).</html>");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lsbAllLayers.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lsbAllLayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsbAllLayers.setFocusable(false);
        lsbAllLayers.setSelectionBackground(new java.awt.Color(94, 130, 59));
        lsbAllLayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lsbAllLayersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lsbAllLayers);

        bntAddToAllLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add.gif"))); // NOI18N
        bntAddToAllLayer.setToolTipText("Add a new layer to the WorkSpace.");
        bntAddToAllLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bntAddToAllLayer.setFocusPainted(false);
        bntAddToAllLayer.setFocusable(false);
        bntAddToAllLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        bntAddToAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntAddToAllLayerActionPerformed(evt);
            }
        });

        btnRemoveFromAllLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnRemoveFromAllLayer.setToolTipText("Remove the selected layer from the WorkSpace.");
        btnRemoveFromAllLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveFromAllLayer.setFocusPainted(false);
        btnRemoveFromAllLayer.setFocusable(false);
        btnRemoveFromAllLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnRemoveFromAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromAllLayerActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Available Layers:");

        btnSetAsSelectedLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnSetAsSelectedLayer.setToolTipText("Add the selected layer to the list of active layers, to be displayed on the map.");
        btnSetAsSelectedLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetAsSelectedLayer.setFocusPainted(false);
        btnSetAsSelectedLayer.setFocusable(false);
        btnSetAsSelectedLayer.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnSetAsSelectedLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnSetAsSelectedLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetAsSelectedLayerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveFromAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSetAsSelectedLayer)
                            .addComponent(bntAddToAllLayer, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(bntAddToAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnRemoveFromAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(btnSetAsSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/down.png"))); // NOI18N
        btnMoveDown.setToolTipText("Move the selected layer down in the list.");
        btnMoveDown.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMoveDown.setFocusPainted(false);
        btnMoveDown.setFocusable(false);
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
            }
        });

        lsbSelectedLayers.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lsbSelectedLayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsbSelectedLayers.setFocusable(false);
        lsbSelectedLayers.setSelectionBackground(new java.awt.Color(94, 130, 59));
        lsbSelectedLayers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lsbSelectedLayersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lsbSelectedLayers);

        btnMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/up.png"))); // NOI18N
        btnMoveUp.setToolTipText("Move the selected layer up in the list.");
        btnMoveUp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMoveUp.setFocusPainted(false);
        btnMoveUp.setFocusable(false);
        btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Selected Layers:");

        btnRemoveFromSelectedLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnRemoveFromSelectedLayer.setToolTipText("Remove the selected layer from the list.");
        btnRemoveFromSelectedLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveFromSelectedLayer.setFocusPainted(false);
        btnRemoveFromSelectedLayer.setFocusable(false);
        btnRemoveFromSelectedLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromSelectedLayerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemoveFromSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(btnRemoveFromSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
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
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveUpActionPerformed
        int index = lsbSelectedLayers.getSelectedIndex();
        if (index > 0) {
            String item = lsbSelectedLayers.getSelectedValue();
            ((DefaultListModel) lsbSelectedLayers.getModel()).removeElementAt(index);
            ((DefaultListModel) lsbSelectedLayers.getModel()).insertElementAt(item, index - 1);
            lsbSelectedLayers.setSelectedIndex(index - 1);
        }
    }//GEN-LAST:event_btnMoveUpActionPerformed

    private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveDownActionPerformed
        int index = lsbSelectedLayers.getSelectedIndex();
        if (index > -1 && index < lsbSelectedLayers.getModel().getSize() - 1) {
            String item = lsbSelectedLayers.getSelectedValue();
            ((DefaultListModel) lsbSelectedLayers.getModel()).removeElementAt(index);
            ((DefaultListModel) lsbSelectedLayers.getModel()).insertElementAt(item, index + 1);
            lsbSelectedLayers.setSelectedIndex(index + 1);
        }
    }//GEN-LAST:event_btnMoveDownActionPerformed

    private void bntAddToAllLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntAddToAllLayerActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Select the Map Layer to import.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new MapLayersFilter());
        final CustomLayersDialog thisHandle = this;
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
                    destinationPath = WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath().resolve(filename);
                }
                else
                if (filename.toLowerCase().endsWith(".shp")) {
                    destinationPath = WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath()
                            .resolve(filename.substring(0, filename.lastIndexOf('.'))).resolve(filename);
                }
                // If the file does not yet exist copy it
                if (destinationPath != null && !Files.exists(destinationPath) && !mapAllLayers.containsKey("[CUSTOM] " + filename)) {
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
                // Add the layer to the hashmap nad reload the UI
                if (destinationPath != null) {
                    mapAllLayers.put("[CUSTOM] " + filename, destinationPath.toAbsolutePath());
                    List<String> keys = new ArrayList<>(mapAllLayers.keySet());
                    Collections.sort(keys);
                    lsbAllLayers.setModel(new DefaultComboBoxModel<>(keys.toArray(new String[keys.size()])));
                }
            }
        }
    }//GEN-LAST:event_bntAddToAllLayerActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        lstSelectedPaths = new ArrayList<>(lsbSelectedLayers.getModel().getSize());
        for (int t = 0; t < lsbSelectedLayers.getModel().getSize(); t++) {
            lstSelectedPaths.add(mapAllLayers.get(lsbSelectedLayers.getModel().getElementAt(t)));
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRemoveFromSelectedLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFromSelectedLayerActionPerformed
        int index = lsbSelectedLayers.getSelectedIndex();
        ((DefaultListModel) lsbSelectedLayers.getModel()).removeElement(lsbSelectedLayers.getSelectedValue());
        lsbSelectedLayers.setSelectedIndex(index);
    }//GEN-LAST:event_btnRemoveFromSelectedLayerActionPerformed

    private void btnRemoveFromAllLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFromAllLayerActionPerformed
        int index = lsbAllLayers.getSelectedIndex();
        String rowKey = lsbAllLayers.getModel().getElementAt(index);
        if (rowKey != null) {
            if (rowKey.startsWith("[CUSTOM]")) {
                try {
                    if (rowKey.toLowerCase().endsWith(".tif") || rowKey.toLowerCase().endsWith(".tiff")) {
                        // Delete the file
                        UtilsFileProcessing.deleteRecursive(mapAllLayers.get(rowKey).toFile());
                    }
                    else 
                    if (rowKey.toLowerCase().endsWith(".shp")) {
                        // Delete the files
                        UtilsFileProcessing.deleteRecursive(mapAllLayers.get(rowKey).getParent().toFile());
                    }
                    // Update the UI
                    ((DefaultComboBoxModel) lsbAllLayers.getModel()).removeElement(lsbAllLayers.getSelectedValue());
                    lsbAllLayers.setSelectedIndex(index);
                    ((DefaultListModel) lsbAllLayers.getModel()).removeElement(rowKey);
                    mapAllLayers.remove(rowKey);
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
                        "Only [CUSTOM] layers can be deleted from this dialog.",
                        "Can't Deleted Layer", JOptionPane.WARNING_MESSAGE);
                getGlassPane().setVisible(false);
            }
        }
    }//GEN-LAST:event_btnRemoveFromAllLayerActionPerformed

    private void btnSetAsSelectedLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetAsSelectedLayerActionPerformed
        if (!((DefaultListModel) lsbSelectedLayers.getModel()).contains(lsbAllLayers.getSelectedValue())) {
            ((DefaultListModel) lsbSelectedLayers.getModel()).addElement(lsbAllLayers.getSelectedValue());
        }
    }//GEN-LAST:event_btnSetAsSelectedLayerActionPerformed

    private void lsbAllLayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lsbAllLayersMouseClicked
        if (evt.getClickCount() == 2) {
            if (!((DefaultListModel) lsbSelectedLayers.getModel()).contains(lsbAllLayers.getSelectedValue())) {
                ((DefaultListModel) lsbSelectedLayers.getModel()).addElement(lsbAllLayers.getSelectedValue());
            }
            evt.consume();
        }
    }//GEN-LAST:event_lsbAllLayersMouseClicked

    private void lsbSelectedLayersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lsbSelectedLayersMouseClicked
        if (evt.getClickCount() == 2) {
            ((DefaultListModel) lsbSelectedLayers.getModel()).removeElement(lsbSelectedLayers.getSelectedValue());
            evt.consume();
        }
    }//GEN-LAST:event_lsbSelectedLayersMouseClicked

    public List<Path> getLstSelectedPaths() {
        return lstSelectedPaths;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntAddToAllLayer;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JButton btnRemoveFromAllLayer;
    private javax.swing.JButton btnRemoveFromSelectedLayer;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSetAsSelectedLayer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> lsbAllLayers;
    private javax.swing.JList<String> lsbSelectedLayers;
    // End of variables declaration//GEN-END:variables
}
