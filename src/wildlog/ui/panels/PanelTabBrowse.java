package wildlog.ui.panels;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import mediautil.gen.Log;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dataobjects.wrappers.SightingWrapper;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.MappingDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.LazyTreeNode;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.renderers.WildLogTreeCellRenderer;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;


public class PanelTabBrowse extends JPanel implements PanelNeedsRefreshWhenDataChanges {
    private static final String DEFAULT_TEXT = "<html><div style='font-size:9px;font-family:verdana;'><i>"
                        + "Select one of the categories at the top of the tree to browse by. "
                        + "<br><br>Left-click on a tree node to select it. "
                        + "<br><br>Double-click or click the +/- icons to expand/collapse the tree node."
                        + "<br><br>Right-click on a tree node for additional actions. "
                        + "</i></div></html>";
    private final int CACHE_LIMIT_FOR_SELECTED_NODE = 5;
    private final int CACHE_LIMIT_FOR_NEIGHBOURING_NODES = 3;
// TODO: Not the sexiest code, but it seems to work now... Maybe oneday I can waste more time on it and try to get rid of some of the lists I'm keeping track off...
    private Map<String, Image> preloadedImages = new HashMap<>(CACHE_LIMIT_FOR_SELECTED_NODE + CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
    private final Map<String, Future> submittedTasks = new HashMap<>(CACHE_LIMIT_FOR_SELECTED_NODE + CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
    private Set<String> preloadedImageNames = new HashSet<>(CACHE_LIMIT_FOR_SELECTED_NODE + CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
    private final ExecutorService executorService = Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_BrowsePrefetcher"));
    private Enumeration<TreePath> previousExpandedTreeNodes;
    private TreePath previousSelectedTreeNode;
    private final WildLogApp app;
    private final JTabbedPane tabbedPanel;
    private Element searchElementBrowseTab;
    private int imageIndex = 0;


    public PanelTabBrowse(WildLogApp inApp, JTabbedPane inTabbedPanel) {
        app = inApp;
        tabbedPanel = inTabbedPanel;
        initComponents();
        // Set some configuration for the tree browser
        treBrowsePhoto.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treBrowsePhoto.setCellRenderer(new WildLogTreeCellRenderer());
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtBrowseInfo, true);
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(getBackground());
        dtpEndDate.getComponent(1).setBackground(getBackground());
        // Hide UI elements on first creation to prevent some flickering
        dtpStartDate.setVisible(false);
        dtpEndDate.setVisible(false);
        btnRefreshDates.setVisible(false);
        cmbElementTypesBrowseTab.setVisible(false);
        // Note: Hack om rondom die NetBeans issue te kom waar hy nie die max width stel nie...
        // (Die het nie gehelp nie http://stackoverflow.com/questions/1922622/setting-maximum-width-using-matisse-gui-builder )
        //  Ek het die waardes die /from file gaan verander, dan word dit gepopulate in die autogenerated code in en werk reg :)
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        rdbBrowseLocation = new javax.swing.JRadioButton();
        btnRefreshBrowseTree = new javax.swing.JButton();
        rdbBrowseElement = new javax.swing.JRadioButton();
        cmbElementTypesBrowseTab = new javax.swing.JComboBox();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        rdbBrowseDate = new javax.swing.JRadioButton();
        btnRotate = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        btnViewImage = new javax.swing.JButton();
        btnRefreshDates = new javax.swing.JButton();
        scrTextArea = new javax.swing.JScrollPane();
        txtBrowseInfo = new javax.swing.JTextPane();
        btnZoomOut = new javax.swing.JButton();
        btnDefault = new javax.swing.JButton();
        btnBrowseNext = new javax.swing.JButton();
        btnBrowsePrev = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();
        imgBrowsePhotos = new org.jdesktop.swingx.JXImageView();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        btnGoBrowseSelection = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        treBrowsePhoto = new javax.swing.JTree();
        btnSetDefaultElementImage = new javax.swing.JButton();
        btnSetDefaultLocationImage = new javax.swing.JButton();
        btnSetDefaultVisitImage = new javax.swing.JButton();
        btnViewEXIF = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 213, 186));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        rdbBrowseLocation.setBackground(new java.awt.Color(204, 213, 186));
        buttonGroup1.add(rdbBrowseLocation);
        rdbBrowseLocation.setText("By Places");
        rdbBrowseLocation.setToolTipText("Sort the tree according to Places.");
        rdbBrowseLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseLocationItemStateChanged(evt);
            }
        });

        btnRefreshBrowseTree.setBackground(new java.awt.Color(204, 213, 186));
        btnRefreshBrowseTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnRefreshBrowseTree.setText("Reload Tree");
        btnRefreshBrowseTree.setToolTipText("This will collapse all tree nodes and reload the tree.");
        btnRefreshBrowseTree.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnRefreshBrowseTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshBrowseTreeActionPerformed(evt);
            }
        });

        rdbBrowseElement.setBackground(new java.awt.Color(204, 213, 186));
        buttonGroup1.add(rdbBrowseElement);
        rdbBrowseElement.setText("By Creatures");
        rdbBrowseElement.setToolTipText("Sort the tree according to Creatures.");
        rdbBrowseElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseElement.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseElementItemStateChanged(evt);
            }
        });

        cmbElementTypesBrowseTab.setMaximumRowCount(9);
        cmbElementTypesBrowseTab.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementTypesBrowseTab.setSelectedItem(ElementType.NONE);
        cmbElementTypesBrowseTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementTypesBrowseTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypesBrowseTabActionPerformed(evt);
            }
        });

        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));

        rdbBrowseDate.setBackground(new java.awt.Color(204, 213, 186));
        buttonGroup1.add(rdbBrowseDate);
        rdbBrowseDate.setText("By Date");
        rdbBrowseDate.setToolTipText("Sort the tree based on the Observations Dates.");
        rdbBrowseDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseDateItemStateChanged(evt);
            }
        });

        btnRotate.setBackground(new java.awt.Color(204, 213, 186));
        btnRotate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnRotate.setText("Rotate Image");
        btnRotate.setToolTipText("Rotate the image counter clockwise.");
        btnRotate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRotate.setFocusPainted(false);
        btnRotate.setFocusable(false);
        btnRotate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRotate.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRotate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRotateActionPerformed(evt);
            }
        });

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        btnViewImage.setBackground(new java.awt.Color(204, 213, 186));
        btnViewImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/File_Small.png"))); // NOI18N
        btnViewImage.setText("Open Original File");
        btnViewImage.setToolTipText("Ask the opperating system to open the original file (outside of WildLog).");
        btnViewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewImage.setFocusPainted(false);
        btnViewImage.setFocusable(false);
        btnViewImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnViewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewImageActionPerformed(evt);
            }
        });

        btnRefreshDates.setBackground(new java.awt.Color(204, 213, 186));
        btnRefreshDates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnRefreshDates.setToolTipText("Refresh the tree based on the provided values.");
        btnRefreshDates.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefreshDates.setFocusPainted(false);
        btnRefreshDates.setIconTextGap(0);
        btnRefreshDates.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnRefreshDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDatesActionPerformed(evt);
            }
        });

        scrTextArea.setMaximumSize(new java.awt.Dimension(375, 32767));
        scrTextArea.setMinimumSize(new java.awt.Dimension(185, 23));

        txtBrowseInfo.setEditable(false);
        txtBrowseInfo.setContentType("text/html"); // NOI18N
        txtBrowseInfo.setText("");
        txtBrowseInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        scrTextArea.setViewportView(txtBrowseInfo);

        btnZoomOut.setBackground(new java.awt.Color(204, 213, 186));
        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnZoomOut.setText("Zoom Out");
        btnZoomOut.setToolTipText("Zoom the image out.");
        btnZoomOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomOut.setFocusPainted(false);
        btnZoomOut.setFocusable(false);
        btnZoomOut.setIconTextGap(2);
        btnZoomOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });

        btnDefault.setBackground(new java.awt.Color(204, 213, 186));
        btnDefault.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnDefault.setText("Default File");
        btnDefault.setToolTipText("Set the current file as the default (first) file for the selected node in the tree.");
        btnDefault.setFocusPainted(false);
        btnDefault.setFocusable(false);
        btnDefault.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });

        btnBrowseNext.setBackground(new java.awt.Color(204, 213, 186));
        btnBrowseNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnBrowseNext.setToolTipText("Load the next file.");
        btnBrowseNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowseNext.setFocusPainted(false);
        btnBrowseNext.setFocusable(false);
        btnBrowseNext.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnBrowseNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseNextActionPerformed(evt);
            }
        });

        btnBrowsePrev.setBackground(new java.awt.Color(204, 213, 186));
        btnBrowsePrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnBrowsePrev.setToolTipText("Load the previous file.");
        btnBrowsePrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowsePrev.setFocusPainted(false);
        btnBrowsePrev.setFocusable(false);
        btnBrowsePrev.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnBrowsePrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowsePrevActionPerformed(evt);
            }
        });

        btnZoomIn.setBackground(new java.awt.Color(204, 213, 186));
        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnZoomIn.setText("Zoom In");
        btnZoomIn.setToolTipText("Zoom the image in.");
        btnZoomIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnZoomIn.setFocusPainted(false);
        btnZoomIn.setFocusable(false);
        btnZoomIn.setIconTextGap(2);
        btnZoomIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });

        imgBrowsePhotos.setBackground(new java.awt.Color(0, 0, 0));
        imgBrowsePhotos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout imgBrowsePhotosLayout = new javax.swing.GroupLayout(imgBrowsePhotos);
        imgBrowsePhotos.setLayout(imgBrowsePhotosLayout);
        imgBrowsePhotosLayout.setHorizontalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        imgBrowsePhotosLayout.setVerticalGroup(
            imgBrowsePhotosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        dtpEndDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));

        btnGoBrowseSelection.setBackground(new java.awt.Color(204, 213, 186));
        btnGoBrowseSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoBrowseSelection.setToolTipText("Open the selected tree node for editing.");
        btnGoBrowseSelection.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoBrowseSelection.setFocusPainted(false);
        btnGoBrowseSelection.setFocusable(false);
        btnGoBrowseSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoBrowseSelectionActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treBrowsePhoto.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treBrowsePhoto.setToolTipText("");
        treBrowsePhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treBrowsePhotoMouseReleased(evt);
            }
        });
        treBrowsePhoto.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
            }
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                treBrowsePhotoTreeWillExpand(evt);
            }
        });
        treBrowsePhoto.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treBrowsePhotoValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(treBrowsePhoto);

        btnSetDefaultElementImage.setBackground(new java.awt.Color(204, 213, 186));
        btnSetDefaultElementImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnSetDefaultElementImage.setText("Default Creature File");
        btnSetDefaultElementImage.setToolTipText("Set this file as the default for the related Creature.");
        btnSetDefaultElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetDefaultElementImage.setFocusPainted(false);
        btnSetDefaultElementImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSetDefaultElementImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetDefaultElementImageActionPerformed(evt);
            }
        });

        btnSetDefaultLocationImage.setBackground(new java.awt.Color(204, 213, 186));
        btnSetDefaultLocationImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnSetDefaultLocationImage.setText("Default Place File");
        btnSetDefaultLocationImage.setToolTipText("Set this file as the default for the related Place.");
        btnSetDefaultLocationImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetDefaultLocationImage.setFocusPainted(false);
        btnSetDefaultLocationImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSetDefaultLocationImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetDefaultLocationImageActionPerformed(evt);
            }
        });

        btnSetDefaultVisitImage.setBackground(new java.awt.Color(204, 213, 186));
        btnSetDefaultVisitImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnSetDefaultVisitImage.setText("Default Period File");
        btnSetDefaultVisitImage.setToolTipText("Set this file as the default for the related Period.");
        btnSetDefaultVisitImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetDefaultVisitImage.setFocusPainted(false);
        btnSetDefaultVisitImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSetDefaultVisitImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetDefaultVisitImageActionPerformed(evt);
            }
        });

        btnViewEXIF.setBackground(new java.awt.Color(204, 213, 186));
        btnViewEXIF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF.png"))); // NOI18N
        btnViewEXIF.setText("Image Metadata");
        btnViewEXIF.setToolTipText("View the EXIF metadata for the image.");
        btnViewEXIF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewEXIF.setFocusPainted(false);
        btnViewEXIF.setFocusable(false);
        btnViewEXIF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewEXIF.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnViewEXIF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewEXIFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rdbBrowseLocation)
                        .addGap(5, 5, 5)
                        .addComponent(rdbBrowseElement)
                        .addGap(5, 5, 5)
                        .addComponent(rdbBrowseDate))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRefreshDates, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefreshBrowseTree, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(scrTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 242, 375)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBrowsePrev)
                                .addGap(1, 1, 1)
                                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addComponent(btnBrowseNext)
                                .addGap(4, 4, 4)
                                .addComponent(btnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(btnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(btnRotate))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnDefault)
                                .addGap(3, 3, 3)
                                .addComponent(btnSetDefaultElementImage)
                                .addGap(2, 2, 2)
                                .addComponent(btnSetDefaultLocationImage)
                                .addGap(2, 2, 2)
                                .addComponent(btnSetDefaultVisitImage)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnViewImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnViewEXIF)))
                    .addComponent(imgBrowsePhotos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnViewImage)
                                .addGap(3, 3, 3)
                                .addComponent(btnViewEXIF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnDefault)
                                    .addComponent(btnSetDefaultElementImage)
                                    .addComponent(btnSetDefaultLocationImage)
                                    .addComponent(btnSetDefaultVisitImage))
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnBrowsePrev, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                        .addComponent(btnBrowseNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnRotate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(2, 2, 2)
                        .addComponent(imgBrowsePhotos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(scrTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbBrowseLocation)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(rdbBrowseElement)
                                        .addComponent(rdbBrowseDate)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnRefreshDates, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(3, 3, 3)
                                .addComponent(jScrollPane4)
                                .addGap(3, 3, 3))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 365, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRefreshBrowseTree, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(3, 3, 3))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rdbBrowseLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseLocationItemStateChanged
        if (rdbBrowseLocation.isSelected()) {
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            cmbElementTypesBrowseTab.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByLocation();
        }
    }//GEN-LAST:event_rdbBrowseLocationItemStateChanged

    private void btnRefreshBrowseTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshBrowseTreeActionPerformed
        rdbBrowseLocationItemStateChanged(null);
        rdbBrowseElementItemStateChanged(null);
        rdbBrowseDateItemStateChanged(null);
    }//GEN-LAST:event_btnRefreshBrowseTreeActionPerformed

    private void rdbBrowseElementItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseElementItemStateChanged
        if (rdbBrowseElement.isSelected()) {
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(true);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByElement();
        }
    }//GEN-LAST:event_rdbBrowseElementItemStateChanged

    private void cmbElementTypesBrowseTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypesBrowseTabActionPerformed
        searchElementBrowseTab.setType((ElementType)cmbElementTypesBrowseTab.getSelectedItem());
        browseByElement();
    }//GEN-LAST:event_cmbElementTypesBrowseTabActionPerformed

    private void rdbBrowseDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseDateItemStateChanged
        if (rdbBrowseDate.isSelected()) {
            dtpStartDate.setVisible(true);
            dtpEndDate.setVisible(true);
            btnRefreshDates.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            browseByDate();
        }
    }//GEN-LAST:event_rdbBrowseDateItemStateChanged

    private void btnRotateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotateActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile tempNode = (DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().list(new WildLogFile(tempNode.getWildLogFileID()));
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        try {
                            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            app.getMainFrame().getGlassPane().setVisible(true);
                            // Raise the Debug Level which is normally LEVEL_INFO. Only Warning messages will be printed by MediaUtil.
                            Log.debugLevel = Log.LEVEL_WARNING;
                            // Initialize LLJTran and Read the entire Image including Appx markers
                            LLJTran llj = new LLJTran(wildLogFile.getAbsolutePath().toFile());
                            // If you pass the 2nd parameter as false, Exif information is not loaded and hence will not be written.
                            llj.read(LLJTran.READ_ALL, true);
                            // Transform the image using default options along with transformation of the Orientation tags.
                            int options = LLJTran.OPT_DEFAULTS | LLJTran.OPT_XFORM_ORIENTATION;
                            llj.transform(LLJTran.ROT_90, options);
                            // Save the Vertical mirror of the Transformed image without Exif header.
                            if (!Files.exists(WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath())) {
                                Files.createDirectories(WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath());
                            }
                            OutputStream out = new BufferedOutputStream(new FileOutputStream(WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath().resolve("vmirror.jpg").toFile()));
                            // Turn off OPT_WRITE_APPXS flag to Skip writing Exif.
                            options = LLJTran.OPT_DEFAULTS & ~LLJTran.OPT_WRITE_APPXS;
                            // Save with vertical transformation without changing the llj image.
                            llj.transform(out, LLJTran.FLIP_V, options);
                            out.close();
                            // Get a new name for the file (because if the same name is used the ImageIcons don't get refreshed if they have been viewed already since Java chaches them)
                            WildLogFile newWildLogFile = new WildLogFile(wildLogFile.getId(), wildLogFile.getFilename(), wildLogFile.getDBFilePath(), wildLogFile.getFileType());
                            newWildLogFile.setDefaultFile(wildLogFile.isDefaultFile());
                            int shortenedAttempt = 0;
                            while (Files.exists(newWildLogFile.getAbsolutePath())) {
                                String newFilename = wildLogFile.getRelativePath().getFileName().toString();
                                newFilename = newFilename.substring(0, newFilename.lastIndexOf('.')) + "_r.jpg";
                                if (newFilename.endsWith("_r_r_r_r.jpg")) {
                                    if (shortenedAttempt == 0) {
                                        newFilename = newFilename.replace("_r_r_r_r.jpg", ".jpg");
                                    }
                                    else {
                                        newFilename = newFilename.replace("_r_r_r_r.jpg", "_" + shortenedAttempt + "_r.jpg");
                                    }
                                    shortenedAttempt++;
                                }
                                newWildLogFile.setDBFilePath(newWildLogFile.getRelativePath().getParent().resolve(newFilename).toString());
                                newWildLogFile.setFilename(newFilename);
                            }
                            // Save the Image which is already transformed as specified by the input transformation earlier, along with the Exif header.
                            out = new BufferedOutputStream(new FileOutputStream(newWildLogFile.getAbsolutePath().toFile()));
                            llj.save(out, LLJTran.OPT_WRITE_ALL);
                            out.close();
                            // Delete old DB file enrty and save new one
                            app.getDBI().delete(wildLogFile);
                            app.getDBI().createOrUpdate(newWildLogFile, false);
                            // Cleanup
                            llj.freeMemory();
                            // Stop any future tasks that might be submitted to prefent us loading the file unnessesarily
                            Future future = submittedTasks.remove(wildLogFile.getAbsolutePath().toString());
                            if (future != null) {
                                future.cancel(true);
                            }
                            // Reload the image
                            preloadedImages.remove(wildLogFile.getAbsolutePath().toString());
                            setupFile(app.getDBI().list(newWildLogFile));
                            // Recreate the thumbnails
                            // Maak net die kritiese thumbnails vooruit, want anders vat dinge te lank
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_LARGE);
                        }
                        catch (LLJTranException | IOException ex) {
                            ex.printStackTrace(System.err);
                            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                @Override
                                public int showDialog() {
                                    JOptionPane.showMessageDialog(app.getMainFrame(),
                                        "There was an unexpected error while trying to rotate the current image.",
                                        "Could Not Rotate Image", JOptionPane.ERROR_MESSAGE);
                                    return 0;
                                }
                            });
                        }
                        finally {
                            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                            app.getMainFrame().getGlassPane().setVisible(false);
                        }
                    }
                    else {
                        UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "Currently WildLog can only rotate JPG images.",
                                    "Not a JPG Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                }
            }
        }
    }//GEN-LAST:event_btnRotateActionPerformed

    private void btnViewEXIFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewEXIFActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup(tempLocation.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup(tempElement.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup(tempVisit.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper tempSightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsDialog.showExifPopup(tempSightingWrapper.getSighting().getWildLogFileID(), imageIndex, app);
            }
        }
    }//GEN-LAST:event_btnViewEXIFActionPerformed

    private void btnViewImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile(tempLocation.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile(tempElement.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile(tempVisit.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper tempSightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsFileProcessing.openFile(tempSightingWrapper.getSighting().getWildLogFileID(), imageIndex, app);
            }
        }
    }//GEN-LAST:event_btnViewImageActionPerformed

    private void btnRefreshDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDatesActionPerformed
        if (rdbBrowseDate.isSelected()) {
            rdbBrowseDateItemStateChanged(null);
        }
        else
        // 'n "Klein hack" om te kry dat die dropdown reg wys... Dit het skielik begin verkeer resize (dalk die NB 7.3 upgrade of die nuwe JDK...)
        if (rdbBrowseElement.isSelected()) {
            cmbElementTypesBrowseTabActionPerformed(null);
        }
    }//GEN-LAST:event_btnRefreshDatesActionPerformed

    private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomOutActionPerformed
        imgBrowsePhotos.setScale(imgBrowsePhotos.getScale()/1.5);
    }//GEN-LAST:event_btnZoomOutActionPerformed

    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage(tempLocation.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage(tempElement.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage(tempVisit.getWildLogFileID(), imageIndex, app);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper tempSightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                imageIndex = UtilsImageProcessing.setMainImage(tempSightingWrapper.getSighting().getWildLogFileID(), imageIndex, app);
            }
            imageIndex--;
            btnBrowseNextActionPerformed(evt);
        }
    }//GEN-LAST:event_btnDefaultActionPerformed

    private void btnBrowseNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseNextActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempLocation.getWildLogFileID()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempElement.getWildLogFileID()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempVisit.getWildLogFileID()));
                loadNextFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempSighting.getWildLogFileID()));
                loadNextFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowseNextActionPerformed

    private void btnBrowsePrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowsePrevActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempLocation.getWildLogFileID()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempElement.getWildLogFileID()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempVisit.getWildLogFileID()));
                loadPrevFile(fotos);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile(tempSighting.getWildLogFileID()));
                loadPrevFile(fotos);
            }
        }
    }//GEN-LAST:event_btnBrowsePrevActionPerformed

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
        imgBrowsePhotos.setScale(imgBrowsePhotos.getScale()*1.5);
    }//GEN-LAST:event_btnZoomInActionPerformed

    private void btnGoBrowseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoBrowseSelectionActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempLocation.getName(), PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempElement.getPrimaryName(), PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempVisit.getName(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel,
                    app.getDBI().find(new Location(tempVisit.getLocationName())));
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Edit an Existing Sighting",
                    tempSighting,
                    app.getDBI().find(new Location(tempSighting.getLocationName())),
                    app.getDBI().find(new Visit(tempSighting.getVisitName())),
                    app.getDBI().find(new Element(tempSighting.getElementName())),
                    this,
                    false, false, false);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnGoBrowseSelectionActionPerformed

    private void treBrowsePhotoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treBrowsePhotoValueChanged
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            imageIndex = 0;
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithHTML
                && ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                txtBrowseInfo.setText(((DataObjectWithHTML)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject())
                    .toHTML(false, false, app, UtilsHTMLExportTypes.ForHTML, null)
                    .replace("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>", ""));
                List<WildLogFile> files = app.getDBI().list(new WildLogFile(
                        ((DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject())
                                .getWildLogFileID()));
                setupFile(files);
                if ((DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                    btnSetDefaultElementImage.setEnabled(true);
                    btnSetDefaultLocationImage.setEnabled(true);
                    btnSetDefaultVisitImage.setEnabled(true);
                }
                else
                if ((DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                    btnSetDefaultElementImage.setEnabled(false);
                    btnSetDefaultLocationImage.setEnabled(true);
                    btnSetDefaultVisitImage.setEnabled(false);
                }
                else {
                    btnSetDefaultElementImage.setEnabled(false);
                    btnSetDefaultLocationImage.setEnabled(false);
                    btnSetDefaultVisitImage.setEnabled(false);
                }
            }
            else {
                txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
                setupFile(null);
                btnSetDefaultElementImage.setEnabled(false);
                btnSetDefaultLocationImage.setEnabled(false);
                btnSetDefaultVisitImage.setEnabled(false);
            }
            // Maak paar display issues reg
            txtBrowseInfo.getCaret().setDot(0);
        }
        else {
            btnSetDefaultElementImage.setEnabled(false);
            btnSetDefaultLocationImage.setEnabled(false);
            btnSetDefaultVisitImage.setEnabled(false);
        }
    }//GEN-LAST:event_treBrowsePhotoValueChanged

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if (!rdbBrowseElement.isSelected() && !rdbBrowseLocation.isSelected() && !rdbBrowseDate.isSelected()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please select a category above to browse...");
            treBrowsePhoto.setModel(new DefaultTreeModel(root));
            treBrowsePhoto.setSelectionRow(0);
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            cmbElementTypesBrowseTab.setVisible(false);
        }
        else {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Loading...");
            treBrowsePhoto.setModel(new DefaultTreeModel(root));
            btnRefreshBrowseTreeActionPerformed(null);
        }
        // Expand tree nodes
        if (previousExpandedTreeNodes != null) {
            while (previousExpandedTreeNodes.hasMoreElements()) {
                TreePath expandedTreePath = previousExpandedTreeNodes.nextElement();
                for (Object object : expandedTreePath.getPath()) {
                    DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) object;
                    for (int t = 0; t < treBrowsePhoto.getRowCount(); t++) {
                        TreePath tempTreePath = treBrowsePhoto.getPathForRow(t);
                        DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) tempTreePath.getLastPathComponent();
                        if (tempNode.getLevel() == expandedNode.getLevel()) {
                            if (tempNode.getParent() != null && expandedNode.getParent() != null && 
                                    ((DefaultMutableTreeNode) tempNode.getParent()).getUserObject().toString()
                                    .equals(((DefaultMutableTreeNode) expandedNode.getParent()).getUserObject().toString())) {
                                if (tempNode.getUserObject().toString().equals(expandedNode.getUserObject().toString())) {
                                    if (tempNode.getUserObject() instanceof DataObjectWithWildLogFile
                                            && expandedNode.getUserObject() instanceof DataObjectWithWildLogFile) {
                                        // Use the WildLogFileID to compare because it should be more unique than the toString()
                                        if (((DataObjectWithWildLogFile) tempNode.getUserObject()).getWildLogFileID().equals(
                                                ((DataObjectWithWildLogFile) expandedNode.getUserObject()).getWildLogFileID())) {
                                            treBrowsePhoto.expandPath(tempTreePath);
                                            break;
                                        }
                                    }
                                    else {
                                        // Probabily the root node
                                        treBrowsePhoto.expandPath(tempTreePath);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Select tree node
        if (previousSelectedTreeNode != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) previousSelectedTreeNode.getLastPathComponent();
            for (int t = 0; t < treBrowsePhoto.getRowCount(); t++) {
                TreePath tempTreePath = treBrowsePhoto.getPathForRow(t);
                DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) tempTreePath.getLastPathComponent();
                if (tempNode.getLevel() == selectedNode.getLevel()) {
                    if (tempNode.getUserObject().toString().equals(selectedNode.getUserObject().toString())) {
                        if (tempNode.getUserObject() instanceof DataObjectWithWildLogFile
                                && selectedNode.getUserObject() instanceof DataObjectWithWildLogFile) {
                            // Use the WildLogFileID to compare because it should be more unique than the toString()
                            if (((DataObjectWithWildLogFile) tempNode.getUserObject()).getWildLogFileID().equals(
                                    ((DataObjectWithWildLogFile) selectedNode.getUserObject()).getWildLogFileID())) {
                                if (tempNode.getParent() != null && selectedNode.getParent() != null && 
                                        ((DefaultMutableTreeNode) tempNode.getParent()).getUserObject().toString()
                                        .equals(((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject().toString())) {
                                    if (((DefaultMutableTreeNode) tempNode.getParent()).getUserObject() instanceof DataObjectWithWildLogFile
                                        && ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                                        // Use the WildLogFileID to compare because it should be more unique than the toString()
                                        if (((DataObjectWithWildLogFile) ((DefaultMutableTreeNode) tempNode.getParent()).getUserObject()).getWildLogFileID().equals(
                                                ((DataObjectWithWildLogFile) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject()).getWildLogFileID())) {
                                            treBrowsePhoto.setSelectionPath(tempTreePath);
                                            break;
                                        }
                                    }
                                    else {
                                        // Root node is parent
                                        treBrowsePhoto.setSelectionPath(tempTreePath);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        previousExpandedTreeNodes = treBrowsePhoto.getExpandedDescendants(treBrowsePhoto.getPathForRow(0));
        previousSelectedTreeNode = treBrowsePhoto.getSelectionPath();
    }//GEN-LAST:event_formComponentHidden

    private void treBrowsePhotoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treBrowsePhotoMouseReleased
        if ((evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt))
                && (rdbBrowseElement.isSelected() || rdbBrowseLocation.isSelected() || rdbBrowseDate.isSelected())) {
            treBrowsePhoto.setSelectionPath(treBrowsePhoto.getPathForLocation(evt.getX(), evt.getY()));
            if (!treBrowsePhoto.isSelectionEmpty()) {
                JPopupMenu popup = new JPopupMenu();
                // View
                JMenuItem mnuView = new JMenuItem("View", new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon.gif")));
                mnuView.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnGoBrowseSelectionActionPerformed(null);
                    }
                });
                popup.add(mnuView);
                // Add
                if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                        JMenuItem mnuAdd = new JMenuItem("Add Period", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                        mnuAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel, 
                                        (Location) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject());
                            }
                        });
                        popup.add(mnuAdd);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                        final PanelTabBrowse tabBrowseHandle = this;
                        JMenuItem mnuAdd = new JMenuItem("Add Observation", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                        mnuAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                PanelSighting dialog = new PanelSighting(
                                        app, app.getMainFrame(), "Add a New Observation",
                                        new Sighting(), null, null, 
                                        (Element) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject(), 
                                        tabBrowseHandle, true, false, false);
                                dialog.setVisible(true);
                            }
                        });
                        popup.add(mnuAdd);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                        final PanelTabBrowse tabBrowseHandle = this;
                        JMenuItem mnuAdd = new JMenuItem("Add Observation", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                        mnuAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                PanelSighting dialog = new PanelSighting(
                                        app, app.getMainFrame(), "Add a New Observation",
                                        new Sighting(), 
                                        app.getDBI().find(new Location(((Visit) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getLocationName())), 
                                        (Visit) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject(), 
                                        null, tabBrowseHandle, true, false, false);
                                dialog.setVisible(true);
                            }
                        });
                        popup.add(mnuAdd);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                        // Nothing to add
                    }
                    else {
                        // Most likely the root WildLog folder
                        JMenuItem mnuAddLocation = new JMenuItem("Add Place", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                        mnuAddLocation.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
                            }
                        });
                        popup.add(mnuAddLocation);
                        JMenuItem mnuAddElement = new JMenuItem("Add Creature", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                        mnuAddElement.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
                            }
                        });
                        popup.add(mnuAddElement);
                    }
                }
                // Delete
                if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                        JMenuItem mnuDelete = new JMenuItem("Delete Place", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                        mnuDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Place? This will delete all Periods, Observations and files linked to the Place as well.",
                                            "Delete Place", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                                if (result == JOptionPane.YES_OPTION) {
                                    Location location = (Location) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    UtilsPanelGenerator.removeOpenedTab(location.getName(), PanelCanSetupHeader.TabTypes.LOCATION, (JTabbedPane)getParent());
                                    app.getDBI().delete(location);
                                    doTheRefresh(null);
                                }
                            }
                        });
                        popup.add(mnuDelete);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                        JMenuItem mnuDelete = new JMenuItem("Delete Creature", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                        mnuDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Creature? This will delete all Observations and files linked to the Creature as well.",
                                            "Delete Creature", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                                if (result == JOptionPane.YES_OPTION) {
                                    Element element = (Element) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    UtilsPanelGenerator.removeOpenedTab(element.getPrimaryName(), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent());
                                    app.getDBI().delete(element);
                                    doTheRefresh(null);
                                }
                            }
                        });
                        popup.add(mnuDelete);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                        JMenuItem mnuDelete = new JMenuItem("Delete Period", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                        mnuDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Period? This will delete all Observations and files linked to the Period as well.",
                                            "Delete Period", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                                if (result == JOptionPane.YES_OPTION) {
                                    Visit visit = (Visit) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    UtilsPanelGenerator.removeOpenedTab(visit.getName(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent());
                                    app.getDBI().delete(visit);
                                    doTheRefresh(null);
                                }
                            }
                        });
                        popup.add(mnuDelete);
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                        JMenuItem mnuDelete = new JMenuItem("Delete Observation", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                        mnuDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                    @Override
                                    public int showDialog() {
                                        return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Observation? This will delete all files linked to the Observation as well.",
                                            "Delete Observation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                                if (result == JOptionPane.YES_OPTION) {
                                    SightingWrapper sightingWrapper = (SightingWrapper) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    app.getDBI().delete(sightingWrapper.getSighting());
                                    doTheRefresh(null);
                                }
                            }
                        });
                        popup.add(mnuDelete);
                    }
                }
                // Map
                JMenuItem mnuMap = new JMenuItem("Map", new ImageIcon(app.getClass().getResource("resources/icons/Map_Small.gif")));
                mnuMap.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MappingDialog mappingDialog = new MappingDialog(app, tempLocation, null, null, null, null);
                                mappingDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MappingDialog mappingDialog = new MappingDialog(app, null, tempElement, null, null, null);
                                mappingDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MappingDialog mappingDialog = new MappingDialog(app, null, null, tempVisit, null, null);
                                mappingDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                                MappingDialog mappingDialog = new MappingDialog(app, null, null, null, tempSighting, null);
                                mappingDialog.setVisible(true);
                            }
                        }
                    }
                });
                popup.add(mnuMap);
                // Report
                JMenuItem mnuReport = new JMenuItem("Report", new ImageIcon(app.getClass().getResource("resources/icons/Report_Small.gif")));
                mnuReport.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean somethingToReportOn = false;
                        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Reports - " + tempLocation.getName(), 
                                        app.getDBI().list(new Sighting(null, tempLocation.getName(), null), true));
                                dialog.setVisible(true);
                                somethingToReportOn = true;
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Reports - " + tempElement.getPrimaryName(), 
                                        app.getDBI().list(new Sighting(tempElement.getPrimaryName(), null, null), true));
                                dialog.setVisible(true);
                                somethingToReportOn = true;
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Reports - " + tempVisit.getName(), 
                                        app.getDBI().list(new Sighting(null, null, tempVisit.getName()), true));
                                dialog.setVisible(true);
                                somethingToReportOn = true;
                            }
                        }
                        if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
                            ReportsBaseDialog dialog = new ReportsBaseDialog("WildLog Reports - " + UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(dtpStartDate.getDate())) 
                                    + " to " + UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(dtpEndDate.getDate())), 
                                    app.getDBI().searchSightings(dtpStartDate.getDate(), dtpEndDate.getDate(), null, null, null, false, Sighting.class));
                            dialog.setVisible(true);
                            somethingToReportOn = true;
                        }
                        if (somethingToReportOn == false) {
                            UtilsDialog.showDialogBackgroundWrapper(app.getMainFrame(), new UtilsDialog.DialogWrapper() {
                                @Override
                                public int showDialog() {
                                    return JOptionPane.showConfirmDialog(app.getMainFrame(),
                                        "Please select a Place, Period or Creature in the tree to the left, or specifiy a valid date range.",
                                        "No Report Available",
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                }
                            });
                        }
                    }
                });
                popup.add(mnuReport);
                // Export
                JMenuItem mnuExport = new JMenuItem("Export", new ImageIcon(app.getClass().getResource("resources/icons/Export.png")));
                mnuExport.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ExportDialog exportDialog = new ExportDialog(app, tempLocation, null, null, null, null);
                                exportDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ExportDialog exportDialog = new ExportDialog(app, null, tempElement, null, null, null);
                                exportDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                ExportDialog exportDialog = new ExportDialog(app, null, null, tempVisit, null, null);
                                exportDialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                                ExportDialog exportDialog = new ExportDialog(app, null, null, null, tempSighting, null);
                                exportDialog.setVisible(true);
                            }
                        }
                    }
                });
                popup.add(mnuExport);
                // Wrap up and show up the popup
                popup.pack();
                popup.show(evt.getComponent(), evt.getPoint().x, evt.getPoint().y);
                popup.setVisible(true);
            }
        }
    }//GEN-LAST:event_treBrowsePhotoMouseReleased

    private void btnSetDefaultElementImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultElementImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper sightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().list(new WildLogFile(sightingWrapper.getWildLogFileID()));
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().list(new WildLogFile(Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getElementName()));
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().createOrUpdate(tempFile, true);
                        }
                        UtilsFileProcessing.performFileUpload(
                            Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getElementName(),
                            Paths.get(Element.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getElementName()),
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null,
                            WildLogThumbnailSizes.NORMAL,
                            app, true, null, true, false);
                        WildLogFile newWildLogFile = app.getDBI().find(new WildLogFile(
                                Element.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getElementName(), wildLogFile.getFilename(),
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                    Paths.get(Element.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getElementName())
                                    .resolve(wildLogFile.getFilename())).toString(),
                                WildLogFileType.IMAGE));
                        newWildLogFile.setDefaultFile(true);
                        app.getDBI().createOrUpdate(newWildLogFile, true);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultElementImageActionPerformed

    private void btnSetDefaultLocationImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultLocationImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper sightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().list(new WildLogFile(sightingWrapper.getWildLogFileID()));
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().list(new WildLogFile(Location.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getLocationName()));
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().createOrUpdate(tempFile, true);
                        }
                        UtilsFileProcessing.performFileUpload(
                            Location.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getLocationName(),
                            Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getLocationName()),
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null,
                            WildLogThumbnailSizes.NORMAL,
                            app, true, null, true, false);
                        WildLogFile newWildLogFile = app.getDBI().find(new WildLogFile(
                                Location.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getLocationName(), wildLogFile.getFilename(),
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                    Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getLocationName())
                                    .resolve(wildLogFile.getFilename())).toString(),
                                WildLogFileType.IMAGE));
                        newWildLogFile.setDefaultFile(true);
                        app.getDBI().createOrUpdate(newWildLogFile, true);
                    }
                }
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit visit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().list(new WildLogFile(visit.getWildLogFileID()));
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().list(new WildLogFile(Location.WILDLOGFILE_ID_PREFIX + visit.getLocationName()));
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().createOrUpdate(tempFile, true);
                        }
                        UtilsFileProcessing.performFileUpload(
                            Location.WILDLOGFILE_ID_PREFIX + visit.getLocationName(),
                            Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(visit.getLocationName()),
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null,
                            WildLogThumbnailSizes.NORMAL,
                            app, true, null, true, false);
                        WildLogFile newWildLogFile = app.getDBI().find(new WildLogFile(
                                Location.WILDLOGFILE_ID_PREFIX + visit.getLocationName(), wildLogFile.getFilename(),
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                    Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(visit.getLocationName())
                                    .resolve(wildLogFile.getFilename())).toString(),
                                WildLogFileType.IMAGE));
                        newWildLogFile.setDefaultFile(true);
                        app.getDBI().createOrUpdate(newWildLogFile, true);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultLocationImageActionPerformed

    private void btnSetDefaultVisitImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultVisitImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper sightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().list(new WildLogFile(sightingWrapper.getWildLogFileID()));
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().list(new WildLogFile(Visit.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getVisitName()));
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().createOrUpdate(tempFile, true);
                        }
                        UtilsFileProcessing.performFileUpload(
                            Visit.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getVisitName(),
                            Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getVisitName()),
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null,
                            WildLogThumbnailSizes.NORMAL,
                            app, true, null, true, false);
                        WildLogFile newWildLogFile = app.getDBI().find(new WildLogFile(
                                Visit.WILDLOGFILE_ID_PREFIX + sightingWrapper.getSighting().getVisitName(), wildLogFile.getFilename(),
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                    Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getVisitName())
                                    .resolve(wildLogFile.getFilename())).toString(),
                                WildLogFileType.IMAGE));
                        newWildLogFile.setDefaultFile(true);
                        app.getDBI().createOrUpdate(newWildLogFile, true);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultVisitImageActionPerformed

    private void treBrowsePhotoTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_treBrowsePhotoTreeWillExpand
        DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode) evt.getPath().getLastPathComponent());
        if (treeNode.getUserObject() instanceof Location) {
            if (rdbBrowseLocation.isSelected()) {
                List<Visit> visits = app.getDBI().list(new Visit(null, ((Location) treeNode.getUserObject()).getName()));
                Collections.sort(visits);
                for (Visit tempVisit : visits) {
                    LazyTreeNode lazyNode = new LazyTreeNode(tempVisit, (app.getDBI().count(new Sighting(null, tempVisit.getLocationName(), tempVisit.getName())) == 0));
                    treeNode.add(lazyNode);
                }
            }
        }
        else
        if (treeNode.getUserObject() instanceof Visit) {
            if (rdbBrowseLocation.isSelected()) {
                List<Sighting> sightings = app.getDBI().list(new Sighting(null, ((Visit) treeNode.getUserObject()).getLocationName(), ((Visit) treeNode.getUserObject()).getName()), false);
                Collections.sort(sightings);
                for (Sighting tempSighting : sightings) {
                    LazyTreeNode lazyNode = new LazyTreeNode(new SightingWrapper(tempSighting, true), false);
                    treeNode.add(lazyNode);
                }
            }
        }
        else
        if (treeNode.getUserObject() instanceof Element) {
            if (rdbBrowseElement.isSelected()) {
                List<Sighting> sightings = app.getDBI().list(new Sighting(((Element) treeNode.getUserObject()).getPrimaryName(), null, null), false);
                Collections.sort(sightings);
                for (Sighting tempSighting : sightings) {
                    LazyTreeNode lazyNode = new LazyTreeNode(new SightingWrapper(tempSighting, false), false);
                    treeNode.add(lazyNode);
                }
            }
        }
        else
        if (treeNode.getUserObject() instanceof SightingWrapper) {
            if (rdbBrowseLocation.isSelected()) {
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Element(((SightingWrapper) treeNode.getUserObject()).getSighting().getElementName())), true));
            }
            else
            if (rdbBrowseElement.isSelected()) {
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Location(((SightingWrapper) treeNode.getUserObject()).getSighting().getLocationName())), true));
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Visit(((SightingWrapper) treeNode.getUserObject()).getSighting().getVisitName())), true));
            }
            else
            if (rdbBrowseDate.isSelected()) {
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Location(((SightingWrapper) treeNode.getUserObject()).getSighting().getLocationName())), true));
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Visit(((SightingWrapper) treeNode.getUserObject()).getSighting().getVisitName())), true));
                treeNode.add(new LazyTreeNode(app.getDBI().find(new Element(((SightingWrapper) treeNode.getUserObject()).getSighting().getElementName())), true));
            }
        }
    }//GEN-LAST:event_treBrowsePhotoTreeWillExpand

    private void browseByLocation() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        List<Location> locations = app.getDBI().list(new Location());
        Collections.sort(locations);
        for (Location tempLocation : locations) {
            LazyTreeNode lazyNode = new LazyTreeNode(tempLocation, (app.getDBI().count(new Visit(null, tempLocation.getName())) == 0));
            root.add(lazyNode);
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void browseByElement() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        if (searchElementBrowseTab == null) {
            searchElementBrowseTab = new Element();
        }
        if (ElementType.NONE.equals(searchElementBrowseTab.getType())) {
            searchElementBrowseTab.setType(null);
        }
        List<Element> elements = app.getDBI().list(searchElementBrowseTab);
        Collections.sort(elements);
        for (Element tempElement : elements) {
            LazyTreeNode lazyNode = new LazyTreeNode(tempElement, (app.getDBI().count(new Sighting(tempElement.getPrimaryName(), null, null)) == 0));
            root.add(lazyNode);
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void browseByDate() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        if (dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            List<Sighting> sightings = app.getDBI().searchSightings(dtpStartDate.getDate(), dtpEndDate.getDate(), null, null, null, false, Sighting.class);
            if (sightings.isEmpty()) {
                DefaultMutableTreeNode lazyNode = new DefaultMutableTreeNode("No Observations found.");
                root.add(lazyNode);
            }
            else {
                Collections.sort(sightings);
                for (Sighting tempSighting : sightings) {
                    LazyTreeNode lazyNode = new LazyTreeNode(new SightingWrapper(tempSighting, true), false);
                    root.add(lazyNode);
                }
            }
        }
        else {
            root = new DefaultMutableTreeNode("Select a start and end date.");
            root.add(new DefaultMutableTreeNode("Then press the Refresh button."));
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void loadPrevFile(List<WildLogFile> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex--;
            if (imageIndex < 0) {
                if (app.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                imageIndex = inFotos.size() - 1;
            }
            setupFile(inFotos);
        }
        else {
            noFiles();
        }
    }

    private void loadNextFile(List<WildLogFile> inFotos) {
        if (inFotos.size() > imageIndex) {
            imageIndex++;
            if (imageIndex >= inFotos.size()) {
                if (app.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                imageIndex = 0;
            }
            setupFile(inFotos);
        }
        else {
            noFiles();
        }
    }

    private void noFiles() {
        try {
            imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
            lblNumberOfImages.setText("0 of 0");
            imgBrowsePhotos.setToolTipText("");
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void lookupCachedImage(final List<WildLogFile> inFiles) throws IOException {
        // Maak die finale grote twee keer groter om te help met die zoom, dan hoef ek nie weer images te load nie, en mens kan altyd dan original kyk vir ful resolution
        int size = (int) imgBrowsePhotos.getSize().getWidth();
        if (size > (int) imgBrowsePhotos.getSize().getHeight()) {
            size = (int) imgBrowsePhotos.getSize().getHeight();
        }
        final int finalSize = size*2;
        final Map<String, Image> newPreloadedImages = new HashMap<>(CACHE_LIMIT_FOR_SELECTED_NODE + CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
        final Set<String> newRequestedImages = new HashSet<>(CACHE_LIMIT_FOR_SELECTED_NODE + CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent();
        // 1) Cache die volgende en vorige fotos vir die huidige node.
        if (inFiles != null && !inFiles.isEmpty() && WildLogFileType.IMAGE.equals(inFiles.get(imageIndex).getFileType())) {
            int startIndex = 0;
            if (inFiles.size() > CACHE_LIMIT_FOR_SELECTED_NODE) {
                startIndex = imageIndex - CACHE_LIMIT_FOR_SELECTED_NODE/2;
            }
            if (startIndex < 0) {
                startIndex = 0;
            }
            String tempKey = inFiles.get(imageIndex).getAbsolutePath().toString();
            if (!preloadedImages.containsKey(tempKey)) {
                // The image will be loaded, so setup the loading screen so long.
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/Loading.png"));
            }
            int t = startIndex;
            for (; t < inFiles.size() && t < startIndex + CACHE_LIMIT_FOR_SELECTED_NODE; t++) {
                doConcurrentLoad(newPreloadedImages, newRequestedImages, inFiles, t, selectedNode, finalSize);
            }
            // Kyk of ek dit moet begin wrap
            if (t == inFiles.size() && inFiles.size() > CACHE_LIMIT_FOR_SELECTED_NODE && t < startIndex + CACHE_LIMIT_FOR_SELECTED_NODE) {
                for (int i = 0; i <= startIndex + CACHE_LIMIT_FOR_SELECTED_NODE - t - i; i++) {
                    doConcurrentLoad(newPreloadedImages, newRequestedImages, inFiles, i, selectedNode, finalSize);
                }
            }
        }
        // 2) Cache die volgende en vorige node
        final int selectedRow = treBrowsePhoto.getSelectionRows()[0];
        List<Integer> preloadNodeRows = new ArrayList<Integer>(CACHE_LIMIT_FOR_NEIGHBOURING_NODES);
        for (int x = 0; x < (CACHE_LIMIT_FOR_NEIGHBOURING_NODES/2); x++) {
            if ((selectedRow - x - 1) >= 0) {
                preloadNodeRows.add(selectedRow - x - 1);
            }
        }
        for (int y = 0; y <= (CACHE_LIMIT_FOR_NEIGHBOURING_NODES - preloadNodeRows.size()); y++) {
            if ((selectedRow + y + 1) < treBrowsePhoto.getRowCount()) {
                preloadNodeRows.add(selectedRow + y + 1);
            }
        }
        for (int row : preloadNodeRows) {
            final DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode)treBrowsePhoto.getPathForRow(row).getLastPathComponent();
            if (tempNode.getUserObject() instanceof DataObjectWithWildLogFile) {
                final List<WildLogFile> files = app.getDBI().list(new WildLogFile(((DataObjectWithWildLogFile)tempNode.getUserObject()).getWildLogFileID()));
                doConcurrentLoad(newPreloadedImages, newRequestedImages, files, 0, tempNode, finalSize);
            }
        }
        // refresh die map
        // Stop any future tasks for images no longer in the cache that might be submitted to prefent us loading the file unnessesarily
        for (String imagesInOldCache : preloadedImageNames) {
            boolean found = false;
            for (String imagesInNewCache : newRequestedImages) {
                if (imagesInOldCache.equals(imagesInNewCache)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Future future = submittedTasks.remove(imagesInOldCache);
                if (future != null) {
                    future.cancel(false);
                }
            }
        }
        List<String> tasksToRemove = new ArrayList<String>(submittedTasks.size());
        for (String taskKey : submittedTasks.keySet()) {
            Future future = submittedTasks.get(taskKey);
            if (future.isDone()) {
                tasksToRemove.add(taskKey);
            }
            else
            if (future.isCancelled()) {
                tasksToRemove.add(taskKey);
            }
        }
        for (String taskKey : tasksToRemove) {
            submittedTasks.remove(taskKey);
        }
        preloadedImages.clear();
        preloadedImages = newPreloadedImages;
        preloadedImageNames = newRequestedImages;
    }

    private void doConcurrentLoad(final Map<String, Image> inNewPreloadedImages, final Set<String> inNewRequestedImages, final List<WildLogFile> inFiles, final int inIndex, final DefaultMutableTreeNode inNode, final int inFinalSize) throws IOException {
        if (!inFiles.isEmpty()) {
            if (WildLogFileType.IMAGE.equals(inFiles.get(inIndex).getFileType())) {
                final String tempKey = inFiles.get(inIndex).getAbsolutePath().toString();
                Image tempImage = preloadedImages.get(tempKey);
                if (tempImage != null) {
                    // Load die image in altwee maps sodat al die verskillende "contains" reg werk...
                    inNewPreloadedImages.put(tempKey, tempImage);
                    preloadedImages.put(tempKey, tempImage);
                    callbackReadyToLoadTheImage(tempImage, inNode, inIndex);
                }
                else {
                    if (!submittedTasks.containsKey(tempKey)) {
                        submittedTasks.put(tempKey, executorService.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        Image tempConcImage;
                                        if (WildLogApp.getApplication().getWildLogOptions().isUseThumnailBrowsing()) {
                                            tempConcImage = UtilsImageProcessing.getScaledIcon(inFiles.get(inIndex).getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_LARGE), inFinalSize).getImage();
                                        }
                                        else {
                                            tempConcImage = UtilsImageProcessing.getScaledIcon(inFiles.get(inIndex).getAbsolutePath(), inFinalSize).getImage();
                                        }
                                        // Load die image in altwee maps sodat al die verskillende "contains" reg werk...
                                        inNewPreloadedImages.put(tempKey, tempConcImage);
                                        preloadedImages.put(tempKey, tempConcImage);
                                        callbackReadyToLoadTheImage(tempConcImage, inNode, inIndex);
                                    }
                                })
                            );
                    }
                }
                inNewRequestedImages.add(tempKey);
            }
        }
    }

    private void setupFile(final List<WildLogFile> inFotos) {
        if (inFotos != null) {
            if (inFotos.size() > 0) {
                try {
                    lblNumberOfImages.setText(imageIndex+1 + " of " + inFotos.size());
                    if (inFotos.get(imageIndex).getFileType().equals(WildLogFileType.IMAGE)) {
                        lookupCachedImage(inFotos);
                    }
                    else
                    if (inFotos.get(imageIndex).getFileType().equals(WildLogFileType.MOVIE)) {
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/Movie.png"));
                        lookupCachedImage(inFotos);
                    }
                    else
                    if (inFotos.get(imageIndex).getFileType().equals(WildLogFileType.OTHER)) {
                        imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/OtherFile.png"));
                        lookupCachedImage(inFotos);
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    imgBrowsePhotos.setToolTipText(inFotos.get(imageIndex).getFilename());
                }
            }
            else {
                try {
                    imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                    lblNumberOfImages.setText("0 of 0");
                    lookupCachedImage(inFotos);
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                finally {
                    imgBrowsePhotos.setToolTipText("");
                }
            }
        }
        else {
            try {
                imgBrowsePhotos.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                imgBrowsePhotos.setToolTipText("");
            }
        }
    }

    private void callbackReadyToLoadTheImage(Image inImage, Object inSelectedNode, int inImageIndex) {
        // Only display the image if it is the current active node.
        // (This is to prevent old node's files that finished after the active node from overwriting the displayed image.)
        if (inImageIndex == imageIndex && treBrowsePhoto.getLastSelectedPathComponent() == inSelectedNode) {
            imgBrowsePhotos.setImage(inImage);
            double ratio;
            // Adjust the longest side's size
            int imageWidth = inImage.getWidth(null);
            int imageHeight = inImage.getHeight(null);
            if (imageHeight >= imageWidth) {
                // Portrait image
                if (imageHeight >= imgBrowsePhotos.getHeight()) {
                    ratio = (double)imageHeight/imgBrowsePhotos.getHeight();
                }
                else {
                    ratio = (double)imgBrowsePhotos.getHeight()/imageHeight;
                }
            }
            else {
                // Landscape image
                if (imageWidth >= imgBrowsePhotos.getWidth()) {
                    ratio = (double)imageWidth/imgBrowsePhotos.getWidth();
                }
                else {
                    ratio = (double)imgBrowsePhotos.getWidth()/imageWidth;
                }
            }
            // Check the shortest side's sizes to make sure they also fit into the display size (since it isn't a square)
            imageWidth = (int) (inImage.getWidth(null)/ratio);
            imageHeight = (int) (inImage.getHeight(null)/ratio);
            if (imgBrowsePhotos.getHeight() >= imageWidth) {
                // Portrait image
                if (imageWidth >= imgBrowsePhotos.getWidth()) {
                    ratio = ratio*(double)imageWidth/imgBrowsePhotos.getWidth();
                }
            }
            else {
                // Landscape image
                if (imageHeight >= imgBrowsePhotos.getHeight()) {
                    ratio = ratio*(double)imageHeight/imgBrowsePhotos.getHeight();
                }
            }
            // Adjust the scale of the image to fit
            imgBrowsePhotos.setScale(1/ratio);
        }
    }

    public void browseSelectedElement(final Element inElement) {
        if (inElement != null) {
            rdbBrowseElement.setSelected(true);
            cmbElementTypesBrowseTab.setSelectedIndex(-1);
            tabbedPanel.setSelectedIndex(4);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount()-1; t++) {
                        if (inElement.getPrimaryName().equals(treBrowsePhoto.getPathForRow(t+1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t+1));
                            treBrowsePhoto.scrollRowToVisible(t+1);
                            treBrowsePhoto.setSelectionPath(treBrowsePhoto.getPathForRow(t+1));
                            break;
                        }
                    }
                }
            });
        }
    }

    public void browseSelectedLocation(final Location inLocation) {
        if (inLocation != null) {
            rdbBrowseLocation.setSelected(true);
            tabbedPanel.setSelectedIndex(4);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount()-1; t++) {
                        if (inLocation.getName().equals(treBrowsePhoto.getPathForRow(t+1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t+1));
                            treBrowsePhoto.scrollRowToVisible(t+1);
                            treBrowsePhoto.setSelectionPath(treBrowsePhoto.getPathForRow(t+1));
                            break;
                        }
                    }
                }
            });
        }
    }

    public void browseSelectedVisit(final Visit inVisit) {
        if (inVisit != null) {
            rdbBrowseLocation.setSelected(true);
            tabbedPanel.setSelectedIndex(4);
            // Expand location
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        if (inVisit.getLocationName().equals(treBrowsePhoto.getPathForRow(t + 1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t + 1));
                            break;
                        }
                    }
                }
            });
            // Now expand visit
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        if (inVisit.getName().equals(treBrowsePhoto.getPathForRow(t + 1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t + 1));
                            treBrowsePhoto.scrollRowToVisible(t + 1);
                            treBrowsePhoto.setSelectionPath(treBrowsePhoto.getPathForRow(t + 1));
                            break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void doTheRefresh(Object inIndicator) {
        formComponentHidden(null);
        formComponentShown(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseNext;
    private javax.swing.JButton btnBrowsePrev;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnGoBrowseSelection;
    private javax.swing.JButton btnRefreshBrowseTree;
    private javax.swing.JButton btnRefreshDates;
    private javax.swing.JButton btnRotate;
    private javax.swing.JButton btnSetDefaultElementImage;
    private javax.swing.JButton btnSetDefaultLocationImage;
    private javax.swing.JButton btnSetDefaultVisitImage;
    private javax.swing.JButton btnViewEXIF;
    private javax.swing.JButton btnViewImage;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbElementTypesBrowseTab;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private org.jdesktop.swingx.JXImageView imgBrowsePhotos;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JRadioButton rdbBrowseDate;
    private javax.swing.JRadioButton rdbBrowseElement;
    private javax.swing.JRadioButton rdbBrowseLocation;
    private javax.swing.JScrollPane scrTextArea;
    private javax.swing.JTree treBrowsePhoto;
    private javax.swing.JTextPane txtBrowseInfo;
    // End of variables declaration//GEN-END:variables
}
