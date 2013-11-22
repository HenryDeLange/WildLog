package wildlog.ui.dialogs;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dataobjects.wrappers.SightingWrapper;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.cellrenderers.WorkspaceTreeCellRenderer;
import wildlog.ui.helpers.cellrenderers.WorkspaceTreeDataWrapper;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class WorkspaceExportDialog extends javax.swing.JDialog {
    private WildLogApp app;


    public WorkspaceExportDialog(WildLogApp inApp) {
        app = inApp;
        initComponents();
        // Setup the tree
        treWorkspace.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treWorkspace.setCellRenderer(new WorkspaceTreeCellRenderer());
        treWorkspace.setToggleClickCount(0);
        // Load the tree
        loadLocationTree();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(this, null);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpFiles = new javax.swing.ButtonGroup();
        grpImages = new javax.swing.ButtonGroup();
        grpTreeOrder = new javax.swing.ButtonGroup();
        btnConfirm = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        treWorkspace = new javax.swing.JTree();
        jLabel2 = new javax.swing.JLabel();
        rdbExportAllFiles = new javax.swing.JRadioButton();
        rdbExportImagesOnly = new javax.swing.JRadioButton();
        rdbExportNoFiles = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        rdbExportOriginalImages = new javax.swing.JRadioButton();
        rdbExportThumbnails = new javax.swing.JRadioButton();
        chkReduceGPS = new javax.swing.JCheckBox();
        chkRemoveDescriptions = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        rdbOrderByLocation = new javax.swing.JRadioButton();
        rdbOrderByElement = new javax.swing.JRadioButton();
        rdbOrderByDate = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export Workspace");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon Small.gif")).getImage());
        setMaximumSize(new java.awt.Dimension(720, 650));
        setMinimumSize(new java.awt.Dimension(720, 650));
        setModal(true);

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnConfirm.setToolTipText("Export the selected records to a new Workspace.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setFocusPainted(false);
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treWorkspace.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treWorkspace.setFocusable(false);
        treWorkspace.setRequestFocusEnabled(false);
        treWorkspace.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treWorkspaceMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(treWorkspace);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Settings:");

        grpFiles.add(rdbExportAllFiles);
        rdbExportAllFiles.setSelected(true);
        rdbExportAllFiles.setText("Export All Files");
        rdbExportAllFiles.setFocusPainted(false);
        rdbExportAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbExportAllFilesActionPerformed(evt);
            }
        });

        grpFiles.add(rdbExportImagesOnly);
        rdbExportImagesOnly.setText("Export Images Only");
        rdbExportImagesOnly.setFocusPainted(false);
        rdbExportImagesOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbExportImagesOnlyActionPerformed(evt);
            }
        });

        grpFiles.add(rdbExportNoFiles);
        rdbExportNoFiles.setText("Export No Files");
        rdbExportNoFiles.setFocusPainted(false);
        rdbExportNoFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbExportNoFilesActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        grpImages.add(rdbExportOriginalImages);
        rdbExportOriginalImages.setSelected(true);
        rdbExportOriginalImages.setText("Original Images");
        rdbExportOriginalImages.setToolTipText("The new Workspace will contain a copy of all the linked files.");
        rdbExportOriginalImages.setFocusPainted(false);

        grpImages.add(rdbExportThumbnails);
        rdbExportThumbnails.setText("Thumbnails Only");
        rdbExportThumbnails.setToolTipText("The new Workspace will contain only images of reduced size, no other files.");
        rdbExportThumbnails.setFocusPainted(false);

        chkReduceGPS.setText("Reduced GPS Accuracy");
        chkReduceGPS.setFocusPainted(false);

        chkRemoveDescriptions.setText("Remove Descriptions");
        chkRemoveDescriptions.setFocusPainted(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Sort Order:");

        grpTreeOrder.add(rdbOrderByLocation);
        rdbOrderByLocation.setSelected(true);
        rdbOrderByLocation.setText("Order by Place");
        rdbOrderByLocation.setToolTipText("Order the tree nodes by Place, Period and then Creature.");
        rdbOrderByLocation.setFocusPainted(false);
        rdbOrderByLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbOrderByLocationActionPerformed(evt);
            }
        });

        grpTreeOrder.add(rdbOrderByElement);
        rdbOrderByElement.setText("Order by Creature");
        rdbOrderByElement.setToolTipText("Order the tree nodes by Creature, Place and then Period.");
        rdbOrderByElement.setFocusPainted(false);
        rdbOrderByElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbOrderByElementActionPerformed(evt);
            }
        });

        grpTreeOrder.add(rdbOrderByDate);
        rdbOrderByDate.setText("Order by Date");
        rdbOrderByDate.setToolTipText("Order the tree nodes by Year, Month, Day and then Time.");
        rdbOrderByDate.setEnabled(false);
        rdbOrderByDate.setFocusPainted(false);

        jLabel3.setText("<html><i>The records marked with the WildLog (W) icon will be exported. Hold down the Ctrl key to select only the records, without it's sub-records.</i></html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rdbOrderByLocation)
                        .addGap(10, 10, 10)
                        .addComponent(rdbOrderByElement)
                        .addGap(10, 10, 10)
                        .addComponent(rdbOrderByDate)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rdbExportAllFiles)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rdbExportImagesOnly)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rdbExportNoFiles)
                                        .addGap(15, 15, 15)
                                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(rdbExportOriginalImages)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rdbExportThumbnails))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(chkReduceGPS)
                                        .addGap(20, 20, 20)
                                        .addComponent(chkRemoveDescriptions)))
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rdbExportOriginalImages)
                                .addComponent(rdbExportThumbnails)
                                .addComponent(rdbExportImagesOnly)
                                .addComponent(rdbExportAllFiles)
                                .addComponent(rdbExportNoFiles))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkReduceGPS)
                            .addComponent(chkRemoveDescriptions))))
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbOrderByLocation)
                    .addComponent(rdbOrderByElement)
                    .addComponent(rdbOrderByDate))
                .addGap(3, 3, 3)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        try {
            final File destination = showFileChooser();
            // TODO: Toets dalk eendag dat die persoon nie 'n reeds bestaande Workspace probeer overwrite nie...
            if (destination != null) {
                UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        setProgress(1);
                        setMessage("Starting the Workspace Export%");
                        setVisible(false);
                        WildLogDBI newDBI = null;
                        try {
                            Path destinationWorkspace = destination.toPath().resolve(WildLogPaths.DEFAULT_WORKSPACE_NAME.getRelativePath());
                            newDBI = new WildLogDBI_h2("jdbc:h2:"
                                + (destinationWorkspace.resolve(WildLogPaths.WILDLOG_DATA.getRelativePath()).resolve(WildLogPaths.DEFAULT_DATABASE_NAME.getRelativePath())).toAbsolutePath()
                                + ";AUTOCOMMIT=ON;IGNORECASE=TRUE");
                            int totalNodes = getNumberOfNodes(treWorkspace.getModel());
                            setProgress(2);
                            setMessage("Workspace Export: " + getProgress() + "%");
                            saveSelectedChildren(newDBI, destinationWorkspace, (DefaultMutableTreeNode)treWorkspace.getModel().getRoot(), totalNodes, this, new ProgressCounter());
                            setProgress(100);
                            setMessage("Workspace Export: " + getProgress() + "%");
                        }
                        catch (Exception ex) {
                            throw ex;
                        }
                        finally {
                            dispose();
                            if (newDBI != null) {
                                newDBI.close();
                            }
                        }
                        setProgress(100);
                        setMessage("Done with the Workspace Export");
                        return null;
                    }
                });
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(app.getMainFrame(),
                                "Could not export the Workspace successfully.",
                                "Export Workspace Error", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
        }
    }//GEN-LAST:event_btnConfirmActionPerformed

    private class ProgressCounter {
        public int counter = 0;
    }

    private int getNumberOfNodes(TreeModel model) {
        return getNumberOfNodes(model, model.getRoot());
    }

    private int getNumberOfNodes(TreeModel model, Object node) {
        int count = 1;
        int nChildren = model.getChildCount(node);
        for (int i = 0; i < nChildren; i++) {
            count += getNumberOfNodes(model, model.getChild(node, i));
        }
        return count;
    }

    private File showFileChooser() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the destination folder for the export");
        fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(app.getMainFrame());
            }
        });
        if (result == JFileChooser.ERROR_OPTION || result != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFile() == null) {
            return null;
        }
        else {
            if (fileChooser.getSelectedFile().isDirectory()) {
                return fileChooser.getSelectedFile();
            }
            else {
                return fileChooser.getSelectedFile().getParentFile();
            }
        }
    }

    private void saveSelectedChildren(WildLogDBI inNewDBI, Path inDestinationWorkspace, DefaultMutableTreeNode inNode, int inTotalNodes, ProgressbarTask inProgressbarTask, ProgressCounter inCounter) {
        if (inNode.getUserObject() instanceof WorkspaceTreeDataWrapper) {
            WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) inNode.getUserObject();
            if (dataWrapper.isSelected()) {
                if (dataWrapper.getDataObject()instanceof Location) {
                    Location location = app.getDBI().find((Location) dataWrapper.getDataObject());
                    if (chkReduceGPS.isSelected()) {
                        location.setLatSeconds(0.0);
                        location.setLonSeconds(0.0);
                        location.setGPSAccuracy(GPSAccuracy.TERRIBLE);
                    }
                    if (chkRemoveDescriptions.isSelected()) {
                        location.setDescription("");
                    }
                    if (inNewDBI.find(location) == null) {
                        inNewDBI.createOrUpdate(location, null);
                        saveFiles(inNewDBI, inDestinationWorkspace, location);
                    }
                }
                else
                if (dataWrapper.getDataObject() instanceof Visit) {
                    Visit visit = app.getDBI().find((Visit) dataWrapper.getDataObject());
                    if (chkRemoveDescriptions.isSelected()) {
                        visit.setDescription("");
                    }
                    if (inNewDBI.find(visit) == null) {
                        inNewDBI.createOrUpdate(visit, null);
                        saveFiles(inNewDBI, inDestinationWorkspace, visit);
                    }
                }
                else
                if (dataWrapper.getDataObject() instanceof Element) {
                    Element element = app.getDBI().find((Element) dataWrapper.getDataObject());
                    if (inNewDBI.find(element) == null) {
                        inNewDBI.createOrUpdate(element, null);
                        saveFiles(inNewDBI, inDestinationWorkspace, element);
                    }
                }
                else
                if (dataWrapper.getDataObject() instanceof SightingWrapper) {
                    Sighting sighting = app.getDBI().find(((SightingWrapper) dataWrapper.getDataObject()).getSighting());
                    if (chkReduceGPS.isSelected()) {
                        sighting.setLatSeconds(0.0);
                        sighting.setLonSeconds(0.0);
                        sighting.setGPSAccuracy(GPSAccuracy.TERRIBLE);
                    }
                    if (chkRemoveDescriptions.isSelected()) {
                        sighting.setDetails("");
                    }
                    if (inNewDBI.find(sighting) == null) {
                        // Note: The sighting ID needs to be the same for the linked images to work...
                        inNewDBI.createOrUpdate(sighting, true);
                        saveFiles(inNewDBI, inDestinationWorkspace, sighting);
                    }
                }
            }
            inProgressbarTask.setTaskProgress(2 + (int)(inCounter.counter/(double)inTotalNodes*97));
            inProgressbarTask.setMessage("Workspace Export: " + inProgressbarTask.getProgress() + "%");
            inCounter.counter++;
        }
        for (int t = 0; t < treWorkspace.getModel().getChildCount(inNode); t++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treWorkspace.getModel().getChild(inNode, t);
            saveSelectedChildren(inNewDBI, inDestinationWorkspace, childNode, inTotalNodes, inProgressbarTask, inCounter);
        }
    }

    private void saveFiles(WildLogDBI inNewDBI, Path inDestinationWorkspace, DataObjectWithWildLogFile inDataObjectWithWildLogFile) {
        if (!rdbExportNoFiles.isSelected()) {
            WildLogFile tempWildLogFile = new WildLogFile(inDataObjectWithWildLogFile.getWildLogFileID());
            if (rdbExportImagesOnly.isSelected()) {
                tempWildLogFile.setFileType(WildLogFileType.IMAGE);
            }
            List<WildLogFile> listFiles = app.getDBI().list(tempWildLogFile);
            for (WildLogFile wildLogFile : listFiles) {
                if (inNewDBI.find(wildLogFile) == null) {
                    Path source;
                    Path destination;
                    if (rdbExportThumbnails.isSelected() && WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                        source = wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_LARGE);
                        destination = inDestinationWorkspace.resolve(wildLogFile.getRelativePath().getParent().resolve(source.getFileName()));
                    }
                    else {
                        source = wildLogFile.getAbsolutePath();
                        destination = inDestinationWorkspace.resolve(wildLogFile.getRelativePath());
                    }
                    UtilsFileProcessing.copyFile(source, destination, false, true);
                    wildLogFile.setDBFilePath(inDestinationWorkspace.relativize(destination).toString());
                    wildLogFile.setFilename(destination.getFileName().toString());
                    inNewDBI.createOrUpdate(wildLogFile, false);
                }
            }
        }
    }

    private void rdbExportAllFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbExportAllFilesActionPerformed
       if (rdbExportAllFiles.isSelected()) {
           rdbExportOriginalImages.setEnabled(true);
           rdbExportThumbnails.setEnabled(true);
       }
    }//GEN-LAST:event_rdbExportAllFilesActionPerformed

    private void rdbExportImagesOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbExportImagesOnlyActionPerformed
        if (rdbExportImagesOnly.isSelected()) {
           rdbExportOriginalImages.setEnabled(true);
           rdbExportThumbnails.setEnabled(true);
       }
    }//GEN-LAST:event_rdbExportImagesOnlyActionPerformed

    private void rdbExportNoFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbExportNoFilesActionPerformed
        if (rdbExportNoFiles.isSelected()) {
           rdbExportOriginalImages.setEnabled(false);
           rdbExportThumbnails.setEnabled(false);
       }
    }//GEN-LAST:event_rdbExportNoFilesActionPerformed

    private void treWorkspaceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treWorkspaceMousePressed
        // Gebruk die muis se posisie om die node te kry want die LastSelected node is verkeerd, vberal as mens die +/- gebruik om te expand/colapse...
        TreePath clickedPath = treWorkspace.getPathForLocation(evt.getX(), evt.getY());
        if (clickedPath != null && clickedPath.getLastPathComponent() != null) {
            if (((DefaultMutableTreeNode) clickedPath.getLastPathComponent()).getUserObject() instanceof WorkspaceTreeDataWrapper) {
                // Get the selected node and change the isSelected property
                WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) ((DefaultMutableTreeNode) clickedPath.getLastPathComponent()).getUserObject();
                dataWrapper.setIsSelected(!dataWrapper.isSelected());
                // Also update all sub nodes
                if (!evt.isControlDown() || !dataWrapper.isSelected()) {
                    selectChildren((DefaultMutableTreeNode) clickedPath.getLastPathComponent());
                }
                if (dataWrapper.isSelected()) {
                    selectParent((DefaultMutableTreeNode) clickedPath.getLastPathComponent());
                }
            }
            // Need to repaint the tree to make sure all icons are refreshed, otherwise Java does the "smart" thing and and caches painted nodes lower down.
            treWorkspace.repaint();
        }
    }//GEN-LAST:event_treWorkspaceMousePressed

    private void selectChildren(DefaultMutableTreeNode inParentNode) {
        for (int t = 0; t < treWorkspace.getModel().getChildCount(inParentNode); t++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treWorkspace.getModel().getChild(inParentNode, t);
            WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) childNode.getUserObject();
            dataWrapper.setIsSelected(((WorkspaceTreeDataWrapper) inParentNode.getUserObject()).isSelected());
            selectChildren(childNode);
        }
    }

    private void selectParent(DefaultMutableTreeNode inNode) {
        if (((DefaultMutableTreeNode) inNode.getParent()).getUserObject() instanceof WorkspaceTreeDataWrapper) {
            ((WorkspaceTreeDataWrapper) ((DefaultMutableTreeNode) inNode.getParent()).getUserObject()).setIsSelected(
                    ((WorkspaceTreeDataWrapper) inNode.getUserObject()).isSelected());
            selectParent((DefaultMutableTreeNode) inNode.getParent());
        }
    }

    private void rdbOrderByLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbOrderByLocationActionPerformed
        if (rdbOrderByLocation.isSelected()) {
            // Load the tree
            loadLocationTree();
        }
    }//GEN-LAST:event_rdbOrderByLocationActionPerformed

    private void rdbOrderByElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbOrderByElementActionPerformed
        if (rdbOrderByElement.isSelected()) {
            loadElementTree();
        }
    }//GEN-LAST:event_rdbOrderByElementActionPerformed

    private void loadLocationTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        List<Location> locations = new ArrayList<Location>(app.getDBI().list(new Location()));
        Map<String, DefaultMutableTreeNode> mapElements;
        Map<String, DefaultMutableTreeNode> mapVisits;
        Collections.sort(locations);
        for (Location location : locations) {
            mapElements = new HashMap<>(500);
            mapVisits = new HashMap<>(500);
            DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(location, false));
            root.add(locationNode);
            List<Sighting> sightings = app.getDBI().list(new Sighting(null, location.getName(), null));
            Collections.sort(sightings, new Comparator<Sighting>() {
                @Override
                public int compare(Sighting sighting1, Sighting sighting2) {
                    int result = sighting1.getVisitName().compareTo(sighting2.getVisitName());
                    if (result == 0) {
                        result = sighting1.getElementName().compareTo(sighting2.getElementName());
                        if (result == 0) {
                            result = sighting1.getDate().compareTo(sighting2.getDate());
                        }
                    }
                    return result;
                }
            });
            for (Sighting sighting : sightings) {
                DefaultMutableTreeNode visitNode = mapVisits.get(sighting.getVisitName());
                if (visitNode == null) {
                    visitNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new Visit(sighting.getVisitName()), false));
                    mapVisits.put(sighting.getVisitName(), visitNode);
                    // Clear die hashmap hier as 'n nuwe visit gelaai word (die sightings behoor volgens visit gesort te wees, so die visit sal nie weer verskyn nie.
                    mapElements.clear();
                }
                locationNode.add(visitNode);
                DefaultMutableTreeNode elementNode = mapElements.get(sighting.getElementName());
                if (elementNode == null) {
                    elementNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new Element(sighting.getElementName()), false));
                    mapElements.put(sighting.getElementName(), elementNode);
                }
                visitNode.add(elementNode);
                DefaultMutableTreeNode sightingNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new SightingWrapper(sighting, true), false));
                elementNode.add(sightingNode);
            }
        }
        treWorkspace.setModel(new DefaultTreeModel(root));
    }

    private void loadElementTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        List<Element> elements = new ArrayList<Element>(app.getDBI().list(new Element()));
        Map<String, DefaultMutableTreeNode> mapLocations;
        Map<String, DefaultMutableTreeNode> mapVisits;
        Collections.sort(elements);
        for (Element element : elements) {
            mapLocations = new HashMap<>(100);
            mapVisits = new HashMap<>(500);
            DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(element, false));
            root.add(elementNode);
            List<Sighting> sightings = app.getDBI().list(new Sighting(element.getPrimaryName(), null, null));
            Collections.sort(sightings, new Comparator<Sighting>() {
                @Override
                public int compare(Sighting sighting1, Sighting sighting2) {
                    int result = sighting1.getLocationName().compareTo(sighting2.getLocationName());
                    if (result == 0) {
                        result = sighting1.getVisitName().compareTo(sighting2.getVisitName());
                        if (result == 0) {
                            result = sighting1.getDate().compareTo(sighting2.getDate());
                        }
                    }
                    return result;
                }
            });
            for (Sighting sighting : sightings) {
                DefaultMutableTreeNode locationNode = mapLocations.get(sighting.getLocationName());
                if (locationNode == null) {
                    locationNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new Location(sighting.getLocationName()), false));
                    mapLocations.put(sighting.getLocationName(), locationNode);
                }
                elementNode.add(locationNode);
                DefaultMutableTreeNode visitNode = mapVisits.get(sighting.getVisitName());
                if (visitNode == null) {
                    visitNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new Visit(sighting.getVisitName()), false));
                    mapVisits.put(sighting.getVisitName(), visitNode);
                }
                locationNode.add(visitNode);
                DefaultMutableTreeNode sightingNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new SightingWrapper(sighting, true), false));
                visitNode.add(sightingNode);
            }
        }
        treWorkspace.setModel(new DefaultTreeModel(root));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JCheckBox chkReduceGPS;
    private javax.swing.JCheckBox chkRemoveDescriptions;
    private javax.swing.ButtonGroup grpFiles;
    private javax.swing.ButtonGroup grpImages;
    private javax.swing.ButtonGroup grpTreeOrder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rdbExportAllFiles;
    private javax.swing.JRadioButton rdbExportImagesOnly;
    private javax.swing.JRadioButton rdbExportNoFiles;
    private javax.swing.JRadioButton rdbExportOriginalImages;
    private javax.swing.JRadioButton rdbExportThumbnails;
    private javax.swing.JRadioButton rdbOrderByDate;
    private javax.swing.JRadioButton rdbOrderByElement;
    private javax.swing.JRadioButton rdbOrderByLocation;
    private javax.swing.JTree treWorkspace;
    // End of variables declaration//GEN-END:variables
}
