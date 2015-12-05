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


public class CustomMapLayersDialog extends JDialog {
    private Map<String, Path> mapAllLayers = new HashMap<>(25);
    private List<Path> lstSelectedPaths = null;
    
    public CustomMapLayersDialog(JFrame inParent) {
        super(inParent);
        initComponents();
        // Set the initial empty model on the selected layers list
        lsbSelectedLayers.setModel(new DefaultListModel<>());
        // Setup the escape key
        final CustomMapLayersDialog thisHandler = this;
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
        // Load Species layers
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
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Custom Map Layers");
        setMinimumSize(new java.awt.Dimension(800, 560));
        setModal(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Custom Map Layers");

        jLabel2.setText("<html>Double-click a layer in the Available Layers list to add the layer to the Active Layers list, or use the Set Active Layer button.<br/>Double-click a layer in the Active Layers list to remove it from the list, or use the Remove button.</html>");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lsbAllLayers.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
        bntAddToAllLayer.setText("Add new Layer");
        bntAddToAllLayer.setToolTipText("Add new map layers to the list and WorkSpace.");
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
        btnRemoveFromAllLayer.setText("Remove Layer");
        btnRemoveFromAllLayer.setToolTipText("Remove the selected layer from the list and delete the files from the WorkSpace.");
        btnRemoveFromAllLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveFromAllLayer.setFocusPainted(false);
        btnRemoveFromAllLayer.setFocusable(false);
        btnRemoveFromAllLayer.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnRemoveFromAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromAllLayerActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Available Layers:");

        btnSetAsSelectedLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnSetAsSelectedLayer.setText("Set Active Layer");
        btnSetAsSelectedLayer.setToolTipText("Add the selected layer to the list of active layers.");
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
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bntAddToAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRemoveFromAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSetAsSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSetAsSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bntAddToAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemoveFromAllLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addContainerGap())
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

        lsbSelectedLayers.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Active Layers:");

        btnRemoveFromSelectedLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnRemoveFromSelectedLayer.setToolTipText("Remove the selected layer from the list.");
        btnRemoveFromSelectedLayer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveFromSelectedLayer.setFocusPainted(false);
        btnRemoveFromSelectedLayer.setFocusable(false);
        btnRemoveFromSelectedLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromSelectedLayerActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Bottom");
        jLabel5.setToolTipText("Layers further up this list will be displayed below the other layers on the map.");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Top");
        jLabel6.setToolTipText("Layers further down this list will be displayed on top of the previous layers on the map.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 116, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnRemoveFromSelectedLayer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel5)
                        .addGap(5, 5, 5)
                        .addComponent(btnMoveUp, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnMoveDown, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel6)
                        .addGap(40, 40, 40)
                        .addComponent(btnRemoveFromSelectedLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addGap(5, 5, 5))
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
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        final CustomMapLayersDialog thisHandle = this;
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
                if (sourcePath.getFileName().toString().toLowerCase().endsWith(".tif") 
                        || sourcePath.getFileName().toString().toLowerCase().endsWith(".tiff")) {
                    Path destinationPath = WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath().resolve(sourcePath.getFileName());
                    if (!Files.exists(destinationPath) && !mapAllLayers.containsKey("[CUSTOM] " + destinationPath.getFileName().toString())) {
                        // Copy the new GeoTiff layer
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath, false, false);
                        // Add the layer to the map
                        mapAllLayers.put("[CUSTOM] " + destinationPath.getFileName().toString(), destinationPath.toAbsolutePath());
                        // Reload the list
                        List<String> keys = new ArrayList<>(mapAllLayers.keySet());
                        Collections.sort(keys);
                        lsbAllLayers.setModel(new DefaultComboBoxModel<>(keys.toArray(new String[keys.size()])));
                    }
                    else {
                        getGlassPane().setVisible(true);
                        JOptionPane.showMessageDialog(this,
                                "A similar layer already exists in this Workspace. Please rename the new layer and try again.",
                                "Duplicate Layer", JOptionPane.ERROR_MESSAGE);
                        getGlassPane().setVisible(false);
                    }
                }
                else
                if (sourcePath.getFileName().toString().toLowerCase().endsWith(".shp")) {
                    String filename = sourcePath.getFileName().toString();
                    Path destinationPath = WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath()
                            .resolve(filename.substring(0, filename.lastIndexOf('.'))).resolve(filename);
                    if (!Files.exists(destinationPath) && !mapAllLayers.containsKey("[CUSTOM] " + destinationPath.getFileName().toString())) {
                        // Copy the new Shapefile layer
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath, false, false);
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath.getParent().resolve(filename.substring(0, filename.lastIndexOf('.')) + ".dbf"), false, false);
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath.getParent().resolve(filename.substring(0, filename.lastIndexOf('.')) + ".prj"), false, false);
                        UtilsFileProcessing.copyFile(sourcePath, destinationPath.getParent().resolve(filename.substring(0, filename.lastIndexOf('.')) + ".shx"), false, false);
                        // Add the layer to the map
                        mapAllLayers.put("[CUSTOM] " + destinationPath.getFileName().toString(), destinationPath.toAbsolutePath());
                        // Reload the list
                        List<String> keys = new ArrayList<>(mapAllLayers.keySet());
                        Collections.sort(keys);
                        lsbAllLayers.setModel(new DefaultComboBoxModel<>(keys.toArray(new String[keys.size()])));
                    }
                    else {
                        getGlassPane().setVisible(true);
                        JOptionPane.showMessageDialog(this,
                                "A similar layer already exists in this Workspace. Please rename the new layer and try again.",
                                "Duplicate Layer", JOptionPane.ERROR_MESSAGE);
                        getGlassPane().setVisible(false);
                    }
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
        if (rowKey != null && rowKey.startsWith("[CUSTOM]")) {
            if (rowKey.toLowerCase().endsWith(".tif") || rowKey.toLowerCase().endsWith(".tiff")) {
                try {
                    // Delete the file
                    UtilsFileProcessing.deleteRecursive(mapAllLayers.get(rowKey).toFile());
                    // Update the UI
                    ((DefaultComboBoxModel) lsbAllLayers.getModel()).removeElement(lsbAllLayers.getSelectedValue());
                    lsbAllLayers.setSelectedIndex(index);
                    ((DefaultListModel) lsbSelectedLayers.getModel()).removeElement(rowKey);
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
            else
            if (rowKey.toLowerCase().endsWith(".shp")) {
                try {
                    // Delete the files
                    UtilsFileProcessing.deleteRecursive(mapAllLayers.get(rowKey).getParent().toFile());
                    // Update the UI
                    ((DefaultComboBoxModel) lsbAllLayers.getModel()).removeElement(lsbAllLayers.getSelectedValue());
                    lsbAllLayers.setSelectedIndex(index);
                    ((DefaultListModel) lsbSelectedLayers.getModel()).removeElement(rowKey);
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
        }
        else {
            getGlassPane().setVisible(true);
            JOptionPane.showMessageDialog(this,
                    "Only [CUSTOM] layers can be deleted from this dialog.",
                    "Can't Deleted Layer", JOptionPane.WARNING_MESSAGE);
            getGlassPane().setVisible(false);
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> lsbAllLayers;
    private javax.swing.JList<String> lsbSelectedLayers;
    // End of variables declaration//GEN-END:variables
}
