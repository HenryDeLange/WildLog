package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import mediautil.gen.Log;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;
import org.apache.logging.log4j.Level;
import org.jdesktop.swingx.painter.MattePainter;
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
import wildlog.data.enums.VisitType;
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.dialogs.CropDialog;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.LazyTreeNode;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.renderers.WildLogTreeCellRenderer;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;
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
// TODO: Not the sexiest code, but it seems to work now... Maybe someday I can waste more time on it and try to get rid of some of the lists I'm keeping track off...
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
        imageView.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        // Set some configuration for the tree browser
        treBrowsePhoto.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treBrowsePhoto.setCellRenderer(new WildLogTreeCellRenderer());
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtBrowseInfo, true);
        // Add hyperlink listener
        txtBrowseInfo.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                    }
                    catch (IOException | URISyntaxException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
        });
        // Make dates pretty
        dtpStartDate.getComponent(1).setBackground(getBackground());
        dtpEndDate.getComponent(1).setBackground(getBackground());
        // Hide UI elements on first creation to prevent some flickering
        dtpStartDate.setVisible(false);
        dtpEndDate.setVisible(false);
        btnRefreshDates.setVisible(false);
        cmbElementTypesBrowseTab.setVisible(false);
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                btnDeleteFile.setEnabled(false);
                btnDeleteFile.setVisible(false);
            }
        }
        // Note: Hack om rondom die NetBeans issue te kom waar hy nie die max width stel nie...
        // (Die het nie gehelp nie http://stackoverflow.com/questions/1922622/setting-maximum-width-using-matisse-gui-builder )
        //  Ek het die waardes in die ".form" file gaan verander, dan word dit gepopulate in die autogenerated code in en werk reg :)
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
        btnRotateRight = new javax.swing.JButton();
        btnViewImage = new javax.swing.JButton();
        btnRefreshDates = new javax.swing.JButton();
        scrTextArea = new javax.swing.JScrollPane();
        txtBrowseInfo = new javax.swing.JTextPane();
        btnDefault = new javax.swing.JButton();
        imageView = new org.jdesktop.swingx.JXImageView();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        btnGoBrowseSelection = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        treBrowsePhoto = new javax.swing.JTree();
        btnSetDefaultElementImage = new javax.swing.JButton();
        btnSetDefaultLocationImage = new javax.swing.JButton();
        btnSetDefaultVisitImage = new javax.swing.JButton();
        btnViewEXIF = new javax.swing.JButton();
        btnCopyImage = new javax.swing.JButton();
        btnAddFile = new javax.swing.JButton();
        btnDeleteFile = new javax.swing.JButton();
        pnlImageNav = new javax.swing.JPanel();
        lblNumberOfImages = new javax.swing.JLabel();
        btnBrowseNext = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        btnBrowsePrev = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();
        btnCrop = new javax.swing.JButton();
        btnRotateLeft = new javax.swing.JButton();

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
        rdbBrowseLocation.setFocusPainted(false);
        rdbBrowseLocation.setFocusable(false);
        rdbBrowseLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseLocationItemStateChanged(evt);
            }
        });

        btnRefreshBrowseTree.setBackground(new java.awt.Color(204, 213, 186));
        btnRefreshBrowseTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Refresh.png"))); // NOI18N
        btnRefreshBrowseTree.setText("Reload Tree");
        btnRefreshBrowseTree.setToolTipText("This will collapse all tree nodes and reload the tree.");
        btnRefreshBrowseTree.setFocusPainted(false);
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
        rdbBrowseElement.setFocusPainted(false);
        rdbBrowseElement.setFocusable(false);
        rdbBrowseElement.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseElementItemStateChanged(evt);
            }
        });

        cmbElementTypesBrowseTab.setMaximumRowCount(11);
        cmbElementTypesBrowseTab.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementTypesBrowseTab.setSelectedItem(ElementType.NONE);
        cmbElementTypesBrowseTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementTypesBrowseTab.setFocusable(false);
        cmbElementTypesBrowseTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypesBrowseTabActionPerformed(evt);
            }
        });

        dtpStartDate.setFocusable(false);
        dtpStartDate.setFormats(new SimpleDateFormat(UtilsTime.DEFAULT_WL_DATE_FORMAT_PATTERN));

        rdbBrowseDate.setBackground(new java.awt.Color(204, 213, 186));
        buttonGroup1.add(rdbBrowseDate);
        rdbBrowseDate.setText("By Date");
        rdbBrowseDate.setToolTipText("Sort the tree based on the Observations Dates.");
        rdbBrowseDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbBrowseDate.setFocusPainted(false);
        rdbBrowseDate.setFocusable(false);
        rdbBrowseDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbBrowseDateItemStateChanged(evt);
            }
        });

        btnRotateRight.setBackground(new java.awt.Color(204, 213, 186));
        btnRotateRight.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnRotateRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/RotateR.png"))); // NOI18N
        btnRotateRight.setToolTipText("Rotate the image clockwise.");
        btnRotateRight.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRotateRight.setFocusPainted(false);
        btnRotateRight.setFocusable(false);
        btnRotateRight.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRotateRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRotateRightActionPerformed(evt);
            }
        });

        btnViewImage.setBackground(new java.awt.Color(204, 213, 186));
        btnViewImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnViewImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/File_Small.png"))); // NOI18N
        btnViewImage.setText("Open");
        btnViewImage.setToolTipText("Ask the opperating system to open the original file (outside of WildLog).");
        btnViewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewImage.setFocusPainted(false);
        btnViewImage.setFocusable(false);
        btnViewImage.setIconTextGap(2);
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
        btnRefreshDates.setFocusable(false);
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

        btnDefault.setBackground(new java.awt.Color(204, 213, 186));
        btnDefault.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnDefault.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnDefault.setText("Set as First");
        btnDefault.setToolTipText("Set the current file as the default (first) file for the selected node in the tree.");
        btnDefault.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDefault.setFocusPainted(false);
        btnDefault.setFocusable(false);
        btnDefault.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });

        imageView.setBackground(new java.awt.Color(0, 0, 0));
        imageView.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        imageView.setBackgroundPainter(new MattePainter(Color.BLACK));

        javax.swing.GroupLayout imageViewLayout = new javax.swing.GroupLayout(imageView);
        imageView.setLayout(imageViewLayout);
        imageViewLayout.setHorizontalGroup(
            imageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        imageViewLayout.setVerticalGroup(
            imageViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        dtpEndDate.setFocusable(false);
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
        treBrowsePhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treBrowsePhotoMouseReleased(evt);
            }
        });
        treBrowsePhoto.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treBrowsePhotoValueChanged(evt);
            }
        });
        treBrowsePhoto.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
            }
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                treBrowsePhotoTreeWillExpand(evt);
            }
        });
        treBrowsePhoto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treBrowsePhotoKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(treBrowsePhoto);

        btnSetDefaultElementImage.setBackground(new java.awt.Color(204, 213, 186));
        btnSetDefaultElementImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSetDefaultElementImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnSetDefaultElementImage.setText("Set Creature File");
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
        btnSetDefaultLocationImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSetDefaultLocationImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Location.gif"))); // NOI18N
        btnSetDefaultLocationImage.setText("Set Place File");
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
        btnSetDefaultVisitImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSetDefaultVisitImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Visit.gif"))); // NOI18N
        btnSetDefaultVisitImage.setText("Set Period File");
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
        btnViewEXIF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF_small.png"))); // NOI18N
        btnViewEXIF.setText("EXIF");
        btnViewEXIF.setToolTipText("View the EXIF metadata for the image.");
        btnViewEXIF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewEXIF.setFocusPainted(false);
        btnViewEXIF.setFocusable(false);
        btnViewEXIF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewEXIF.setIconTextGap(2);
        btnViewEXIF.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnViewEXIF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewEXIFActionPerformed(evt);
            }
        });

        btnCopyImage.setBackground(new java.awt.Color(204, 213, 186));
        btnCopyImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnCopyImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/File_Small.png"))); // NOI18N
        btnCopyImage.setText("Copy");
        btnCopyImage.setToolTipText("Copy the original file to the opperating system's clipboard (can be pasted outside of WildLog).");
        btnCopyImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCopyImage.setFocusPainted(false);
        btnCopyImage.setFocusable(false);
        btnCopyImage.setIconTextGap(2);
        btnCopyImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCopyImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyImageActionPerformed(evt);
            }
        });

        btnAddFile.setBackground(new java.awt.Color(204, 213, 186));
        btnAddFile.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnAddFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAddFile.setText("Add");
        btnAddFile.setToolTipText("Upload a new file.");
        btnAddFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddFile.setFocusPainted(false);
        btnAddFile.setFocusable(false);
        btnAddFile.setIconTextGap(2);
        btnAddFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAddFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFileActionPerformed(evt);
            }
        });

        btnDeleteFile.setBackground(new java.awt.Color(204, 213, 186));
        btnDeleteFile.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnDeleteFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteFile.setText("Delete");
        btnDeleteFile.setToolTipText("Delete the current file.");
        btnDeleteFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteFile.setFocusPainted(false);
        btnDeleteFile.setFocusable(false);
        btnDeleteFile.setIconTextGap(2);
        btnDeleteFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteFileActionPerformed(evt);
            }
        });

        pnlImageNav.setBackground(new java.awt.Color(204, 213, 186));

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        btnBrowseNext.setBackground(new java.awt.Color(204, 213, 186));
        btnBrowseNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnBrowseNext.setToolTipText("Load the next file. (CTRL + RIGHT while navigating the tree.)");
        btnBrowseNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowseNext.setFocusPainted(false);
        btnBrowseNext.setFocusable(false);
        btnBrowseNext.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnBrowseNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseNextActionPerformed(evt);
            }
        });

        btnZoomOut.setBackground(new java.awt.Color(204, 213, 186));
        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ZoomOut.png"))); // NOI18N
        btnZoomOut.setText("Zoom");
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

        btnBrowsePrev.setBackground(new java.awt.Color(204, 213, 186));
        btnBrowsePrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnBrowsePrev.setToolTipText("Load the previous file. (CTRL + LEFT while navigating the tree.)");
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
        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ZoomIn.png"))); // NOI18N
        btnZoomIn.setText("Zoom");
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

        javax.swing.GroupLayout pnlImageNavLayout = new javax.swing.GroupLayout(pnlImageNav);
        pnlImageNav.setLayout(pnlImageNavLayout);
        pnlImageNavLayout.setHorizontalGroup(
            pnlImageNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageNavLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnBrowsePrev, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(lblNumberOfImages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(btnBrowseNext, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnZoomIn)
                .addGap(0, 0, 0)
                .addComponent(btnZoomOut)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlImageNavLayout.setVerticalGroup(
            pnlImageNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageNavLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlImageNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImageNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnBrowsePrev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBrowseNext, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlImageNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnZoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        btnCrop.setBackground(new java.awt.Color(204, 213, 186));
        btnCrop.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnCrop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Crop.png"))); // NOI18N
        btnCrop.setToolTipText("Rotate the image clockwise.");
        btnCrop.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCrop.setFocusPainted(false);
        btnCrop.setFocusable(false);
        btnCrop.setIconTextGap(0);
        btnCrop.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCropActionPerformed(evt);
            }
        });

        btnRotateLeft.setBackground(new java.awt.Color(204, 213, 186));
        btnRotateLeft.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnRotateLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/RotateL.png"))); // NOI18N
        btnRotateLeft.setToolTipText("Rotate the image anti-clockwise.");
        btnRotateLeft.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRotateLeft.setFocusPainted(false);
        btnRotateLeft.setFocusable(false);
        btnRotateLeft.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRotateLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRotateLeftActionPerformed(evt);
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
                            .addComponent(btnRefreshDates, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefreshBrowseTree, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(scrTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 185, 375)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnDefault)
                        .addGap(3, 3, 3)
                        .addComponent(btnSetDefaultElementImage)
                        .addGap(2, 2, 2)
                        .addComponent(btnSetDefaultLocationImage)
                        .addGap(2, 2, 2)
                        .addComponent(btnSetDefaultVisitImage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCrop)
                        .addGap(10, 10, 10)
                        .addComponent(btnRotateLeft)
                        .addGap(1, 1, 1)
                        .addComponent(btnRotateRight))
                    .addComponent(imageView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlImageNav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddFile, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnDeleteFile, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnViewEXIF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCopyImage)
                        .addGap(2, 2, 2)
                        .addComponent(btnViewImage)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSetDefaultElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSetDefaultLocationImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSetDefaultVisitImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnRotateRight, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCrop, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnRotateLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnViewImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCopyImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnViewEXIF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnAddFile, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDeleteFile, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addComponent(imageView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlImageNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
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
                                        .addGap(4, 4, 4)
                                        .addComponent(dtpEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(3, 3, 3)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                                .addGap(3, 3, 3))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbElementTypesBrowseTab, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dtpStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 358, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRefreshBrowseTree, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGoBrowseSelection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rdbBrowseLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseLocationItemStateChanged
        WildLogApp.LOGGER.log(Level.INFO, "[PanelTabBrowse-Locations]");
        if (rdbBrowseLocation.isSelected()) {
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(false);
            cmbElementTypesBrowseTab.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
        WildLogApp.LOGGER.log(Level.INFO, "[PanelTabBrowse-Elements]");
        if (rdbBrowseElement.isSelected()) {
            dtpStartDate.setVisible(false);
            dtpEndDate.setVisible(false);
            btnRefreshDates.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(true);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            browseByElement();
        }
    }//GEN-LAST:event_rdbBrowseElementItemStateChanged

    private void cmbElementTypesBrowseTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypesBrowseTabActionPerformed
        searchElementBrowseTab.setType((ElementType)cmbElementTypesBrowseTab.getSelectedItem());
        browseByElement();
    }//GEN-LAST:event_cmbElementTypesBrowseTabActionPerformed

    private void rdbBrowseDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbBrowseDateItemStateChanged
        WildLogApp.LOGGER.log(Level.INFO, "[PanelTabBrowse-Date]");
        if (rdbBrowseDate.isSelected()) {
            dtpStartDate.setVisible(true);
            dtpEndDate.setVisible(true);
            btnRefreshDates.setVisible(true);
            cmbElementTypesBrowseTab.setVisible(false);
            txtBrowseInfo.setText("<body bgcolor='#FFFFFF'>" + DEFAULT_TEXT + "</body>");
            try {
                imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            browseByDate();
        }
    }//GEN-LAST:event_rdbBrowseDateItemStateChanged

    private void btnRotateRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotateRightActionPerformed
        doRotate(false);
    }//GEN-LAST:event_btnRotateRightActionPerformed

    private void doRotate(boolean inDoFlip) throws HeadlessException {
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile tempNode = (DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().listWildLogFiles(tempNode.getWildLogFileID(), null, WildLogFile.class);
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        try {
                            app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            app.getMainFrame().getGlassPane().setVisible(true);
                            // Raise the Debug Level which is normally LEVEL_INFO. Only Error messages will be printed by MediaUtil.
                            Log.debugLevel = Log.LEVEL_ERROR;
                            // Initialize LLJTran and Read the entire Image including Appx markers
                            LLJTran lljTran = new LLJTran(wildLogFile.getAbsolutePath().toFile());
                            // If you pass the 2nd parameter as false, Exif information is not loaded and hence will not be written.
                            lljTran.read(LLJTran.READ_ALL, true);
                            lljTran.transform(LLJTran.ROT_90);
                            if (inDoFlip) {
                                lljTran.transform(LLJTran.FLIP_H);
                                lljTran.transform(LLJTran.FLIP_V);
                            }
                            // Get a new name for the file (because if the same name is used the ImageIcons don't get
                            // refreshed if they have been viewed already since Java chaches them)
                            WildLogFile newWildLogFile = new WildLogFile(wildLogFile.getID(), wildLogFile.getLinkID(), wildLogFile.getLinkType(),
                                    wildLogFile.getFilename(), wildLogFile.getDBFilePath(), wildLogFile.getFileType(), new Date(), null, -1);
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
                            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newWildLogFile.getAbsolutePath().toFile()))) {
                                lljTran.save(outputStream, LLJTran.OPT_WRITE_ALL);
                            }
                            // Delete old DB file enrty and save new one
                            app.getDBI().deleteWildLogFile(wildLogFile.getID());
                            newWildLogFile.setFileDate(UtilsImageProcessing.getDateFromFileDate(newWildLogFile.getAbsolutePath()));
                            newWildLogFile.setFileSize(Files.size(newWildLogFile.getAbsolutePath()));
                            app.getDBI().createWildLogFile(newWildLogFile, false);
                            // Cleanup
                            lljTran.freeMemory();
                            // Stop any future tasks that might be submitted to prefent us loading the file unnessesarily
                            Future future = submittedTasks.remove(wildLogFile.getAbsolutePath().toString());
                            if (future != null) {
                                future.cancel(true);
                            }
                            // Reload the image
                            preloadedImages.remove(wildLogFile.getAbsolutePath().toString());
                            setupFile(app.getDBI().listWildLogFiles(newWildLogFile.getLinkID(), null, WildLogFile.class));
                            // Recreate the thumbnails
                            // Maak net die kritiese thumbnails vooruit, want anders vat dinge te lank
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                            newWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_LARGE);
                        }
                        catch (LLJTranException | IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            WLOptionPane.showMessageDialog(app.getMainFrame(),
                                    "There was an unexpected error while trying to rotate the current image.",
                                    "Could Not Rotate Image", JOptionPane.ERROR_MESSAGE);
                        }
                        finally {
                            app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                            app.getMainFrame().getGlassPane().setVisible(false);
                        }
                    }
                    else {
                        WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "Currently WildLog can only rotate JPG images.",
                                "Not a JPG Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

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
        imageView.setScale(imageView.getScale()/1.5);
    }//GEN-LAST:event_btnZoomOutActionPerformed

    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
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
        }
    }//GEN-LAST:event_btnDefaultActionPerformed

    private void btnBrowseNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseNextActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                if (temp != null) {
                    List<WildLogFile> fotos = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
                    loadNextFile(fotos);
                }
            }
        }
    }//GEN-LAST:event_btnBrowseNextActionPerformed

    private void btnBrowsePrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowsePrevActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                if (temp != null) {
                    List<WildLogFile> fotos = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
                    loadPrevFile(fotos);
                }
            }
        }
    }//GEN-LAST:event_btnBrowsePrevActionPerformed

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
        imageView.setScale(imageView.getScale()*1.5);
    }//GEN-LAST:event_btnZoomInActionPerformed

    private void btnGoBrowseSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoBrowseSelectionActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempLocation.getID(), PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempElement.getID(), PanelCanSetupHeader.TabTypes.ELEMENT, tabbedPanel, null);
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                UtilsPanelGenerator.openPanelAsTab(app, tempVisit.getID(), PanelCanSetupHeader.TabTypes.VISIT, tabbedPanel,
                    app.getDBI().findLocation(tempVisit.getLocationID(), null, Location.class));
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                Sighting tempSighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Edit an Existing Sighting",
                    tempSighting,
                    app.getDBI().findLocation(tempSighting.getLocationID(), null, Location.class),
                    app.getDBI().findVisit(tempSighting.getVisitID(), null, false, Visit.class),
                    app.getDBI().findElement(tempSighting.getElementID(), null, Element.class),
                    this,
                    false, false, false, false);
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
                    .toHTML(false, false, false, app, UtilsHTMLExportTypes.ForHTML, null)
                    .replace("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>", ""));
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> files = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
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
                    Visit visit = (Visit) (DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                    if (VisitType.STASHED == visit.getType()) {
                        try {
                            imageView.setImage(app.getClass().getResource("resources/icons/Stash.png"));
                        }
                        catch (IOException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
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
                                        if (((DataObjectWithWildLogFile) tempNode.getUserObject()).getWildLogFileID() == 
                                                ((DataObjectWithWildLogFile) expandedNode.getUserObject()).getWildLogFileID()) {
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
                            if (((DataObjectWithWildLogFile) tempNode.getUserObject()).getWildLogFileID() == 
                                    ((DataObjectWithWildLogFile) selectedNode.getUserObject()).getWildLogFileID()) {
                                if (tempNode.getParent() != null && selectedNode.getParent() != null && 
                                        ((DefaultMutableTreeNode) tempNode.getParent()).getUserObject().toString()
                                        .equals(((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject().toString())) {
                                    if (((DefaultMutableTreeNode) tempNode.getParent()).getUserObject() instanceof DataObjectWithWildLogFile
                                        && ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                                        // Use the WildLogFileID to compare because it should be more unique than the toString()
                                        if (((DataObjectWithWildLogFile) ((DefaultMutableTreeNode) tempNode.getParent()).getUserObject()).getWildLogFileID() == 
                                                ((DataObjectWithWildLogFile) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject()).getWildLogFileID()) {
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
                                        tabBrowseHandle, true, false, false, false);
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
                                        app.getDBI().findLocation(((Visit) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getLocationID(), null, Location.class), 
                                        (Visit) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject(), 
                                        null, tabBrowseHandle, true, false, false, false);
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
                        if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                                || (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                                    && WildLogApp.WILDLOG_USER_TYPE != WildLogUserTypes.VOLUNTEER)) {
                            JMenuItem mnuAddLocation = new JMenuItem("Add Place", new ImageIcon(app.getClass().getResource("resources/icons/Add.gif")));
                            mnuAddLocation.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    UtilsPanelGenerator.openNewPanelAsTab(app, PanelCanSetupHeader.TabTypes.LOCATION, tabbedPanel, null);
                                }
                            });
                            popup.add(mnuAddLocation);
                        }
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
                        if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                            JMenuItem mnuDelete = new JMenuItem("Delete Place", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                            mnuDelete.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Place? This will delete all Periods, Observations and files linked to the Place as well.",
                                            "Delete Place?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    if (result == JOptionPane.YES_OPTION) {
                                        Location location = (Location) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                        UtilsPanelGenerator.removeOpenedTab(location.getID(), PanelCanSetupHeader.TabTypes.LOCATION, (JTabbedPane)getParent());
                                        app.getDBI().deleteLocation(location.getID());
                                        doTheRefresh(null);
                                    }
                                }
                            });
                            popup.add(mnuDelete);
                        }
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                        if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                            JMenuItem mnuDelete = new JMenuItem("Delete Creature", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                            mnuDelete.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Creature? This will delete all Observations and files linked to the Creature as well.",
                                            "Delete Creature?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    if (result == JOptionPane.YES_OPTION) {
                                        Element element = (Element) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                        UtilsPanelGenerator.removeOpenedTab(element.getID(), PanelCanSetupHeader.TabTypes.ELEMENT, (JTabbedPane)getParent());
                                        app.getDBI().deleteElement(element.getID());
                                        doTheRefresh(null);
                                    }
                                }
                            });
                            popup.add(mnuDelete);
                        }
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                        if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                            JMenuItem mnuDelete = new JMenuItem("Delete Period", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                            mnuDelete.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                            "Are you sure you want to delete this Period? This will delete all Observations and files linked to the Period as well.",
                                            "Delete Period?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                    if (result == JOptionPane.YES_OPTION) {
                                        Visit visit = (Visit) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                        UtilsPanelGenerator.removeOpenedTab(visit.getID(), PanelCanSetupHeader.TabTypes.VISIT, (JTabbedPane)getParent());
                                        app.getDBI().deleteVisit(visit.getID());
                                        doTheRefresh(null);
                                    }
                                }
                            });
                            popup.add(mnuDelete);
                        }
                    }
                    else
                    if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                        JMenuItem mnuDelete = new JMenuItem("Delete Observation", new ImageIcon(app.getClass().getResource("resources/icons/Delete.gif")));
                        mnuDelete.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                        "Are you sure you want to delete this Observation? This will delete all files linked to the Observation as well.",
                                        "Delete Observation?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                if (result == JOptionPane.YES_OPTION) {
                                    SightingWrapper sightingWrapper = (SightingWrapper) ((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    app.getDBI().deleteSighting(sightingWrapper.getSighting().getID());
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
                                Location location = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + location.getDisplayName(), 
                                        app.getDBI().listSightings(0, location.getID(), 0, true, Sighting.class), 0);
                                dialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                                Element element = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + element.getDisplayName(), 
                                        app.getDBI().listSightings(element.getID(), 0, 0, true, Sighting.class), 
                                        element.getID());
                                dialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                                Visit visit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + visit.getDisplayName(), 
                                        app.getDBI().listSightings(0, 0, visit.getID(), true, Sighting.class), 0);
                                dialog.setVisible(true);
                            }
                            else
                            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                                Sighting sighting = ((SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject()).getSighting();
                                List<Sighting> lstSightings = new ArrayList<>(1);
                                lstSightings.add(sighting);
                                MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + sighting.getDisplayName(), lstSightings, 0);
                                dialog.setVisible(true);
                            }
                        }
                    }
                });
                popup.add(mnuMap);
                // Report
                if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                        || (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER 
                            && WildLogApp.WILDLOG_USER_TYPE != WildLogUserTypes.VOLUNTEER)) {
                    JMenuItem mnuReport = new JMenuItem("Charts", new ImageIcon(app.getClass().getResource("resources/icons/Report_Small.png")));
                    mnuReport.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            boolean somethingToReportOn = false;
                            if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
                                if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Location) {
                                    Location tempLocation = (Location)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + tempLocation.getName(), 
                                            app.getDBI().listSightings(0, tempLocation.getID(), 0, true, Sighting.class));
                                    dialog.setVisible(true);
                                    somethingToReportOn = true;
                                }
                                else
                                if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Element) {
                                    Element tempElement = (Element)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + tempElement.getPrimaryName(), 
                                            app.getDBI().listSightings(tempElement.getID(), 0, 0, true, Sighting.class));
                                    dialog.setVisible(true);
                                    somethingToReportOn = true;
                                }
                                else
                                if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                                    Visit tempVisit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                                    ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + tempVisit.getName(), 
                                            app.getDBI().listSightings(0, 0, tempVisit.getID(), true, Sighting.class));
                                    dialog.setVisible(true);
                                    somethingToReportOn = true;
                                }
                            }
                            if (rdbBrowseDate.isSelected() && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
                                Date endDate = UtilsTime.getDateFromLocalDateTime(LocalDateTime.of(UtilsTime.getLocalDateFromDate(dtpEndDate.getDate()), LocalTime.MAX));
                                ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(dtpStartDate.getDate())) 
                                        + " to " + UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(dtpEndDate.getDate())), 
                                        app.getDBI().searchSightings(null, dtpStartDate.getDate(), endDate, null, null, null, false, Sighting.class));
                                dialog.setVisible(true);
                                somethingToReportOn = true;
                            }
                            if (somethingToReportOn == false) {
                                WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                        "Please select a Place, Period or Creature in the tree to the left, or specifiy a valid date range.",
                                        "No Chart Available", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    });
                    popup.add(mnuReport);
                }
                // Export
                if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
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
                }
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
                List<WildLogFile> listWildLogFile = app.getDBI().listWildLogFiles(sightingWrapper.getWildLogFileID(), null, WildLogFile.class);
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().listWildLogFiles(sightingWrapper.getSighting().getElementID(), null, WildLogFile.class);
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().updateWildLogFile(tempFile, false);
                        }
                        String uniqueIdentifier = "_" + Long.toString(System.currentTimeMillis()); // Om seker te maak die file word nie rename met die upload nie, sodat ek dit weer kan find om default te maak
                        UtilsFileProcessing.performFileUpload(app.getDBI().findElement(sightingWrapper.getSighting().getElementID(), null, Element.class),
                            Paths.get(Element.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getCachedElementName() + uniqueIdentifier), WildLogDataType.ELEMENT, 
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null, 
                            app, true, null, true, true);
                        WildLogFile uploadedWildLogFile = app.getDBI().findWildLogFile(0, 0, null, 
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                        Paths.get(Element.WILDLOG_FOLDER_PREFIX).resolve(
                                                sightingWrapper.getSighting().getCachedElementName() + uniqueIdentifier).resolve(
                                                        wildLogFile.getFilename())).toString(), 
                                WildLogFile.class);
                        if (uploadedWildLogFile != null) {
                            uploadedWildLogFile.setDefaultFile(true);
                            app.getDBI().updateWildLogFile(uploadedWildLogFile, false);
                        }
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "The first image for the Creature '" + sightingWrapper.getSighting().getCachedElementName() + "' was successfully changed.",
                                "Changed First Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultElementImageActionPerformed

    private void btnSetDefaultLocationImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultLocationImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper sightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().listWildLogFiles(sightingWrapper.getWildLogFileID(), null, WildLogFile.class);
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().listWildLogFiles(sightingWrapper.getSighting().getLocationID(), null, WildLogFile.class);
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().updateWildLogFile(tempFile, false);
                        }
                        String uniqueIdentifier = "_" + Long.toString(System.currentTimeMillis()); // Om seker te maak die file word nie rename met die upload nie, sodat ek dit weer kan find om default te maak
                        UtilsFileProcessing.performFileUpload(app.getDBI().findLocation(sightingWrapper.getSighting().getLocationID(), null, Location.class),
                            Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getCachedLocationName() + uniqueIdentifier), WildLogDataType.LOCATION, 
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null, 
                            app, true, null, true, true);
                        WildLogFile uploadedWildLogFile = app.getDBI().findWildLogFile(0, 0, null, 
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                        Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(
                                                sightingWrapper.getSighting().getCachedLocationName() + uniqueIdentifier).resolve(
                                                        wildLogFile.getFilename())).toString(),
                                WildLogFile.class);
                        if (uploadedWildLogFile != null) {
                            uploadedWildLogFile.setDefaultFile(true);
                            app.getDBI().updateWildLogFile(uploadedWildLogFile, false);
                        }
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "The first image for the Place '" + sightingWrapper.getSighting().getCachedLocationName() + "' was successfully changed.",
                                "Changed First Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            else
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof Visit) {
                Visit visit = (Visit)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().listWildLogFiles(visit.getWildLogFileID(), null, WildLogFile.class);
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().listWildLogFiles(visit.getLocationID(), null, WildLogFile.class);
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().updateWildLogFile(tempFile, false);
                        }
                        String uniqueIdentifier = "_" + Long.toString(System.currentTimeMillis()); // Om seker te maak die file word nie rename met die upload nie, sodat ek dit weer kan find om default te maak
                        UtilsFileProcessing.performFileUpload(app.getDBI().findLocation(visit.getLocationID(), null, Location.class),
                            Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(visit.getCachedLocationName() + uniqueIdentifier), WildLogDataType.LOCATION, 
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null, 
                            app, true, null, true, true);
                        WildLogFile uploadedWildLogFile = app.getDBI().findWildLogFile(0, 0, null, 
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                        Paths.get(Location.WILDLOG_FOLDER_PREFIX).resolve(
                                                visit.getCachedLocationName() + uniqueIdentifier).resolve(
                                                        wildLogFile.getFilename())).toString(),
                                WildLogFile.class);
                        if (uploadedWildLogFile != null) {
                            uploadedWildLogFile.setDefaultFile(true);
                            app.getDBI().updateWildLogFile(uploadedWildLogFile, false);
                        }
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "The first image for the Place '" + visit.getCachedLocationName() + "' was successfully changed.",
                                "Changed First Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultLocationImageActionPerformed

    private void btnSetDefaultVisitImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultVisitImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof SightingWrapper) {
                SightingWrapper sightingWrapper = (SightingWrapper)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> listWildLogFile = app.getDBI().listWildLogFiles(sightingWrapper.getWildLogFileID(), null, WildLogFile.class);
                if (listWildLogFile != null && !listWildLogFile.isEmpty() && listWildLogFile.size() > imageIndex) {
                    WildLogFile wildLogFile = listWildLogFile.get(imageIndex);
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                        List<WildLogFile> files = app.getDBI().listWildLogFiles(sightingWrapper.getSighting().getVisitID(), null, WildLogFile.class);
                        for (WildLogFile tempFile : files) {
                            tempFile.setDefaultFile(false);
                            app.getDBI().updateWildLogFile(tempFile, false);
                        }
                        String uniqueIdentifier = "_" + Long.toString(System.currentTimeMillis()); // Om seker te maak die file word nie rename met die upload nie, sodat ek dit weer kan find om default te maak
                        UtilsFileProcessing.performFileUpload(app.getDBI().findVisit(sightingWrapper.getSighting().getVisitID(), null, true, Visit.class),
                            Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(sightingWrapper.getSighting().getCachedVisitName() + uniqueIdentifier), WildLogDataType.VISIT, 
                            new File[] {wildLogFile.getAbsolutePath().toFile()},
                            null, 
                            app, true, null, true, true);
                        WildLogFile uploadedWildLogFile = app.getDBI().findWildLogFile(0, 0, null, 
                                WildLogPaths.WILDLOG_FILES_IMAGES.getRelativePath().resolve(
                                        Paths.get(Visit.WILDLOG_FOLDER_PREFIX).resolve(
                                                sightingWrapper.getSighting().getCachedVisitName() + uniqueIdentifier).resolve(
                                                        wildLogFile.getFilename())).toString(),
                                WildLogFile.class);
                        if (uploadedWildLogFile != null) {
                            uploadedWildLogFile.setDefaultFile(true);
                            app.getDBI().updateWildLogFile(uploadedWildLogFile, false);
                        }
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "The first image for the Period '" + sightingWrapper.getSighting().getCachedVisitName() + "' was successfully changed.",
                                "Changed First Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSetDefaultVisitImageActionPerformed

    private void treBrowsePhotoTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_treBrowsePhotoTreeWillExpand
        DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode) evt.getPath().getLastPathComponent());
        if (treeNode.getUserObject() instanceof Location) {
            if (rdbBrowseLocation.isSelected()) {
                List<Visit> visits = app.getDBI().listVisits(null, ((Location) treeNode.getUserObject()).getID(), null, true, Visit.class);
                Collections.sort(visits);
                for (Visit tempVisit : visits) {
                    LazyTreeNode lazyNode = new LazyTreeNode(tempVisit, (app.getDBI().countSightings(0, 0, tempVisit.getLocationID(), tempVisit.getID()) == 0));
                    treeNode.add(lazyNode);
                }
            }
        }
        else
        if (treeNode.getUserObject() instanceof Visit) {
            if (rdbBrowseLocation.isSelected()) {
                List<Sighting> sightings = app.getDBI().listSightings(0, ((Visit) treeNode.getUserObject()).getLocationID(), ((Visit) treeNode.getUserObject()).getID(), true, Sighting.class);
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
                List<Sighting> sightings = app.getDBI().listSightings(((Element) treeNode.getUserObject()).getID(), 0, 0, true, Sighting.class);
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
                treeNode.add(new LazyTreeNode(app.getDBI().findElement(((SightingWrapper) treeNode.getUserObject()).getSighting().getElementID(), null, Element.class), true));
            }
            else
            if (rdbBrowseElement.isSelected()) {
                treeNode.add(new LazyTreeNode(app.getDBI().findLocation(((SightingWrapper) treeNode.getUserObject()).getSighting().getLocationID(), null, Location.class), true));
                treeNode.add(new LazyTreeNode(app.getDBI().findVisit(((SightingWrapper) treeNode.getUserObject()).getSighting().getVisitID(), null, true, Visit.class), true));
            }
            else
            if (rdbBrowseDate.isSelected()) {
                treeNode.add(new LazyTreeNode(app.getDBI().findLocation(((SightingWrapper) treeNode.getUserObject()).getSighting().getLocationID(), null, Location.class), true));
                treeNode.add(new LazyTreeNode(app.getDBI().findVisit(((SightingWrapper) treeNode.getUserObject()).getSighting().getVisitID(), null, true, Visit.class), true));
                treeNode.add(new LazyTreeNode(app.getDBI().findElement(((SightingWrapper) treeNode.getUserObject()).getSighting().getElementID(), null, Element.class), true));
            }
        }
    }//GEN-LAST:event_treBrowsePhotoTreeWillExpand

    private void btnCopyImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyImageActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                List<WildLogFile> lstWildLogFiles = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
                if (!lstWildLogFiles.isEmpty()) {
                    final List<File> files = new ArrayList<>(1);
                    files.add(lstWildLogFiles.get(imageIndex).getAbsolutePath().toFile());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
                        @Override
                        public DataFlavor[] getTransferDataFlavors() {
                            return new DataFlavor[] { DataFlavor.javaFileListFlavor };
                        }

                        @Override
                        public boolean isDataFlavorSupported(DataFlavor flavor) {
                            return DataFlavor.javaFileListFlavor.equals(flavor);
                        }

                        @Override
                        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                            return files;
                        }
                    }, null);
                }
            }
        }
    }//GEN-LAST:event_btnCopyImageActionPerformed

    private void btnAddFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFileActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                String prefix;
                WildLogDataType linkType;
                if (temp instanceof Element) {
                    prefix = Element.WILDLOG_FOLDER_PREFIX;
                    linkType = WildLogDataType.ELEMENT;
                }
                else
                if (temp instanceof Location) {
                    prefix = Location.WILDLOG_FOLDER_PREFIX;
                    linkType = WildLogDataType.LOCATION;
                }
                else
                if (temp instanceof Visit) {
                    prefix = Visit.WILDLOG_FOLDER_PREFIX;
                    linkType = WildLogDataType.VISIT;
                }
                else
                if (temp instanceof SightingWrapper) {
                    prefix = Sighting.WILDLOG_FOLDER_PREFIX;
                    linkType = WildLogDataType.SIGHTING;
                }
                else {
                    prefix = "UNKNOWN";
                    linkType = WildLogDataType.NONE;
                }
                if (temp != null) {
                    List<File> files = UtilsFileProcessing.showFileUploadDialog(app, app.getMainFrame());
                    UtilsFileProcessing.performFileUpload(temp,
                        Paths.get(prefix), linkType, 
                        files.toArray(new File[files.size()]),
                        new Runnable() {
                            @Override
                            public void run() {
                                doTheRefresh(null);
                            }
                        }, 
                        app, true, null, true, true);
                }
            }
        }
    }//GEN-LAST:event_btnAddFileActionPerformed

    private void btnDeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteFileActionPerformed
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            return;
        }
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile) ((DefaultMutableTreeNode) treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                if (temp != null) {
                    List<WildLogFile> lstFiles = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
                    if (!lstFiles.isEmpty()) {
                        int result = WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                "Are you sure you want to delete the current File?",
                                "Delete File?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {
                            app.getDBI().deleteWildLogFile(lstFiles.get(imageIndex).getID());
                            doTheRefresh(null);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_btnDeleteFileActionPerformed

    private void btnCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCropActionPerformed
        if (treBrowsePhoto.getLastSelectedPathComponent() != null) {
            if (((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject() instanceof DataObjectWithWildLogFile) {
                DataObjectWithWildLogFile temp = (DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getLastSelectedPathComponent()).getUserObject();
                if (temp != null) {
                    List<WildLogFile> lstFiles = app.getDBI().listWildLogFiles(temp.getWildLogFileID(), null, WildLogFile.class);
                    if (!lstFiles.isEmpty()) {
                        WildLogFile wildLogFile = lstFiles.get(imageIndex);
                        if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType()) && WildLogFileExtentions.Images.isKnownExtention(wildLogFile.getAbsolutePath())) {
                            CropDialog dialog = new CropDialog(app.getMainFrame(), wildLogFile);
                            dialog.setVisible(true);
                            doTheRefresh(null);
                        }
                        else {
                            WLOptionPane.showConfirmDialog(app.getMainFrame(),
                                    "WildLog can only crop images.",
                                    "Not a JPG Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_btnCropActionPerformed

    private void btnRotateLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotateLeftActionPerformed
        doRotate(true);
    }//GEN-LAST:event_btnRotateLeftActionPerformed

    private void treBrowsePhotoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treBrowsePhotoKeyPressed
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            btnBrowseNextActionPerformed(null);
            evt.consume();
        }
        else
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_LEFT) {
            btnBrowsePrevActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_treBrowsePhotoKeyPressed

    private void browseByLocation() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        List<Location> locations = app.getDBI().listLocations(null, Location.class);
        Collections.sort(locations);
        for (Location tempLocation : locations) {
            LazyTreeNode lazyNode = new LazyTreeNode(tempLocation, (app.getDBI().countVisits(null, tempLocation.getID()) == 0));
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
        List<Element> elements = app.getDBI().listElements(searchElementBrowseTab.getPrimaryName(), null, searchElementBrowseTab.getType(), Element.class);
        Collections.sort(elements);
        for (Element tempElement : elements) {
            LazyTreeNode lazyNode = new LazyTreeNode(tempElement, (app.getDBI().countSightings(0, tempElement.getID(), 0, 0) == 0));
            root.add(lazyNode);
        }
        treBrowsePhoto.setModel(new DefaultTreeModel(root));
    }

    private void browseByDate() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog");
        if (dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            Date endDate = UtilsTime.getDateFromLocalDateTime(LocalDateTime.of(UtilsTime.getLocalDateFromDate(dtpEndDate.getDate()), LocalTime.MAX));
            List<Sighting> sightings = app.getDBI().searchSightings(null, dtpStartDate.getDate(), endDate, null, null, null, true, Sighting.class);
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
            imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
            lblNumberOfImages.setText("0 of 0");
            imageView.setToolTipText("");
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

    private void lookupCachedImage(final List<WildLogFile> inFiles) throws IOException {
        // Maak die finale grote twee keer groter om te help met die zoom, dan hoef ek nie weer images te load nie, en mens kan altyd dan original kyk vir ful resolution
        int size = (int) imageView.getSize().getWidth();
        if (size > (int) imageView.getSize().getHeight()) {
            size = (int) imageView.getSize().getHeight();
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
                imageView.setImage(app.getClass().getResource("resources/icons/Loading.png"));
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
                final List<WildLogFile> files = app.getDBI().listWildLogFiles(((DataObjectWithWildLogFile)tempNode.getUserObject()).getWildLogFileID(), null, WildLogFile.class);
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
                                            tempConcImage = UtilsImageProcessing.getScaledIcon(inFiles.get(inIndex).getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_LARGE), inFinalSize, true).getImage();
                                        }
                                        else {
                                            tempConcImage = UtilsImageProcessing.getScaledIcon(inFiles.get(inIndex).getAbsolutePath(), inFinalSize, true).getImage();
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
                        imageView.setImage(app.getClass().getResource("resources/icons/Movie.png"));
                        lookupCachedImage(inFotos);
                    }
                    else
                    if (inFotos.get(imageIndex).getFileType().equals(WildLogFileType.OTHER)) {
                        imageView.setImage(app.getClass().getResource("resources/icons/OtherFile.png"));
                        lookupCachedImage(inFotos);
                    }
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                finally {
                    imageView.setToolTipText(inFotos.get(imageIndex).getFilename());
                }
            }
            else {
                try {
                    imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                    lblNumberOfImages.setText("0 of 0");
                    lookupCachedImage(inFotos);
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                finally {
                    imageView.setToolTipText("");
                }
            }
        }
        else {
            try {
                imageView.setImage(app.getClass().getResource("resources/icons/NoFile.png"));
                lblNumberOfImages.setText("");
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            finally {
                imageView.setToolTipText("");
            }
        }
    }

    private void callbackReadyToLoadTheImage(Image inImage, Object inSelectedNode, int inImageIndex) {
        // Only display the image if it is the current active node.
        // (This is to prevent old node's files that finished after the active node from overwriting the displayed image.)
        if (inImageIndex == imageIndex && treBrowsePhoto.getLastSelectedPathComponent() == inSelectedNode) {
            imageView.setImage(inImage);
            double ratio;
            // Adjust the longest side's size
            int imageWidth = inImage.getWidth(null);
            int imageHeight = inImage.getHeight(null);
            if (imageHeight >= imageWidth) {
                // Portrait image
                if (imageHeight >= imageView.getHeight()) {
                    ratio = (double)imageHeight/imageView.getHeight();
                }
                else {
                    ratio = (double)imageView.getHeight()/imageHeight;
                }
            }
            else {
                // Landscape image
                if (imageWidth >= imageView.getWidth()) {
                    ratio = (double)imageWidth/imageView.getWidth();
                }
                else {
                    ratio = (double)imageView.getWidth()/imageWidth;
                }
            }
            // Check the shortest side's sizes to make sure they also fit into the display size (since it isn't a square)
            imageWidth = (int) (inImage.getWidth(null)/ratio);
            imageHeight = (int) (inImage.getHeight(null)/ratio);
            if (imageView.getHeight() >= imageWidth) {
                // Portrait image
                if (imageWidth >= imageView.getWidth()) {
                    ratio = ratio*(double)imageWidth/imageView.getWidth();
                }
            }
            else {
                // Landscape image
                if (imageHeight >= imageView.getHeight()) {
                    ratio = ratio*(double)imageHeight/imageView.getHeight();
                }
            }
            // Adjust the scale of the image to fit
            imageView.setScale(1/ratio);
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
            // Expand the Location
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        if (inVisit.getCachedLocationName().equals(treBrowsePhoto.getPathForRow(t + 1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t + 1));
                            break;
                        }
                    }
                }
            });
            // Now expand the Visit
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
    
    public void browseSelectedSighting(final Sighting inSighting) {
        if (inSighting != null) {
            rdbBrowseLocation.setSelected(true);
            tabbedPanel.setSelectedIndex(4);
            // Expand the Location
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        if (inSighting.getCachedLocationName().equals(treBrowsePhoto.getPathForRow(t + 1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t + 1));
                            break;
                        }
                    }
                }
            });
            // Now expand the Visit
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        if (inSighting.getCachedVisitName().equals(treBrowsePhoto.getPathForRow(t + 1).getLastPathComponent().toString())) {
                            treBrowsePhoto.expandPath(treBrowsePhoto.getPathForRow(t + 1));
                            treBrowsePhoto.scrollRowToVisible(t + 1);
                            treBrowsePhoto.setSelectionPath(treBrowsePhoto.getPathForRow(t + 1));
                            break;
                        }
                    }
                }
            });
            // Then select the Sighting
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < treBrowsePhoto.getRowCount() - 1; t++) {
                        // Use the WildLogFileID to compare because it should be more unique than the toString()
                        if (inSighting.getWildLogFileID() == ((DataObjectWithWildLogFile)((DefaultMutableTreeNode)treBrowsePhoto.getPathForRow(t + 1)
                                .getLastPathComponent()).getUserObject()).getWildLogFileID()) {
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
    private javax.swing.JButton btnAddFile;
    private javax.swing.JButton btnBrowseNext;
    private javax.swing.JButton btnBrowsePrev;
    private javax.swing.JButton btnCopyImage;
    private javax.swing.JButton btnCrop;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnDeleteFile;
    private javax.swing.JButton btnGoBrowseSelection;
    private javax.swing.JButton btnRefreshBrowseTree;
    private javax.swing.JButton btnRefreshDates;
    private javax.swing.JButton btnRotateLeft;
    private javax.swing.JButton btnRotateRight;
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
    private org.jdesktop.swingx.JXImageView imageView;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JPanel pnlImageNav;
    private javax.swing.JRadioButton rdbBrowseDate;
    private javax.swing.JRadioButton rdbBrowseElement;
    private javax.swing.JRadioButton rdbBrowseLocation;
    private javax.swing.JScrollPane scrTextArea;
    private javax.swing.JTree treBrowsePhoto;
    private javax.swing.JTextPane txtBrowseInfo;
    // End of variables declaration//GEN-END:variables
}
