package wildlog.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.ExtraData;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dataobjects.interfaces.DataObjectWithAudit;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.dataobjects.wrappers.SightingWrapper;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.encryption.TokenEncryptor;
import wildlog.maps.utils.UtilsGPS;
import wildlog.sync.azure.SyncAzure;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.renderers.WorkspaceTreeCellRenderer;
import wildlog.ui.helpers.renderers.WorkspaceTreeDataWrapper;
import wildlog.ui.utils.UtilsUI;
import static wildlog.ui.utils.UtilsUI.doClipboardCopy;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;

// TODO: Refactor! Baie van die code is basies 90% 'n copy-paste uit die cloud sync of workspace export...

public class WorkspaceCloudExportDialog extends JDialog {
    private final WildLogApp app;
    private int syncDataUp = 0;
    private AtomicInteger syncFileUp = new AtomicInteger(0);
    private AtomicInteger syncStashUp = new AtomicInteger(0);
    private int syncFail = 0;


    public WorkspaceCloudExportDialog(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspaceCloudExportDialog]");
        app = inApp;
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbThumbnailSize);
        // Setup the tree
        treWorkspace.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treWorkspace.setCellRenderer(new WorkspaceTreeCellRenderer());
        treWorkspace.setToggleClickCount(0);
        // Load the tree
        loadLocationTree();
        // Set minimum size
        UtilsDialog.setupMinimumSize(this);
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the glasspane on this dialog as well for the JOptionPane's
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Attach Clipboards
        UtilsUI.attachClipboardPopup(txaSyncToken, false, true);
        UtilsUI.attachClipboardPopup(txtWorkspaceName);
        // Get the Workspace ID for the new cloud workspace
        lblWorkspaceID.setText(Long.toString(app.getDBI().generateID()));
        // Default to the free token's configuration
        configureFreeToken();
        // Setup user specific defaults
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_REMOTE) {
            chkIncludeAllElements.setSelected(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpFiles = new javax.swing.ButtonGroup();
        grpImages = new javax.swing.ButtonGroup();
        btnConfirm = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        treWorkspace = new javax.swing.JTree();
        jLabel3 = new javax.swing.JLabel();
        pnlSyncOptions = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rdbSyncAllFiles = new javax.swing.JRadioButton();
        rdbSyncJpegOnly = new javax.swing.JRadioButton();
        rdbSyncNoFiles = new javax.swing.JRadioButton();
        rdbSyncOriginalImages = new javax.swing.JRadioButton();
        rdbSyncThumbnails = new javax.swing.JRadioButton();
        cmbThumbnailSize = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        chkIncludeAllElements = new javax.swing.JCheckBox();
        pnlSyncToken = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaSyncToken = new javax.swing.JTextArea();
        btnConfirmSyncToken = new javax.swing.JButton();
        pnlWorkspace = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblWorkspaceID = new javax.swing.JLabel();
        txtWorkspaceName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export To A New  Cloud Workspace");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small.gif")).getImage());
        setModal(true);

        btnConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnConfirm.setToolTipText("Export the selected records to a new Workspace.");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treWorkspace.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treWorkspace.setRequestFocusEnabled(false);
        treWorkspace.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treWorkspaceMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(treWorkspace);

        jLabel3.setText("<html><i>The records marked with the WildLog (W) icon will be exported. Hold down the Ctrl key to select only the record, without it's sub-records.</i></html>");

        pnlSyncOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Content:");

        grpFiles.add(rdbSyncAllFiles);
        rdbSyncAllFiles.setText("Data and Files (all files)");
        rdbSyncAllFiles.setToolTipText("Sync all files between the current Workspace and the cloud.");
        rdbSyncAllFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncAllFilesActionPerformed(evt);
            }
        });

        grpFiles.add(rdbSyncJpegOnly);
        rdbSyncJpegOnly.setText("Data and Images (JPEG only)");
        rdbSyncJpegOnly.setToolTipText("Sync only images between the current Workspace and the cloud.");
        rdbSyncJpegOnly.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncJpegOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncJpegOnlyActionPerformed(evt);
            }
        });

        grpFiles.add(rdbSyncNoFiles);
        rdbSyncNoFiles.setText("Only Data (no files)");
        rdbSyncNoFiles.setToolTipText("Don't sync any files between the current Workspace and the cloud.");
        rdbSyncNoFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncNoFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncNoFilesActionPerformed(evt);
            }
        });

        grpImages.add(rdbSyncOriginalImages);
        rdbSyncOriginalImages.setText("Original Images");
        rdbSyncOriginalImages.setToolTipText("Sync a copy of the original linked images between the current Workspace and the cloud.");
        rdbSyncOriginalImages.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncOriginalImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncOriginalImagesActionPerformed(evt);
            }
        });

        grpImages.add(rdbSyncThumbnails);
        rdbSyncThumbnails.setText("Thumbnail Images");
        rdbSyncThumbnails.setToolTipText("The images that are synced will be reduced in size, the original images will not be synced.");
        rdbSyncThumbnails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSyncThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbSyncThumbnailsActionPerformed(evt);
            }
        });

        cmbThumbnailSize.setMaximumRowCount(15);
        cmbThumbnailSize.setModel(new DefaultComboBoxModel(WildLogThumbnailSizes.values()));
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.S0300_NORMAL);
        cmbThumbnailSize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbThumbnailSize.setEnabled(false);

        jLabel7.setText("px");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Image Upload Size:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Reference Data:");

        chkIncludeAllElements.setText("Include All Creatures");
        chkIncludeAllElements.setToolTipText("If selected, in addition to the selected Creatures, all remaining Creatures (but not their Observations) will also be exported to the new Workspace.");
        chkIncludeAllElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pnlSyncOptionsLayout = new javax.swing.GroupLayout(pnlSyncOptions);
        pnlSyncOptions.setLayout(pnlSyncOptionsLayout);
        pnlSyncOptionsLayout.setHorizontalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbSyncOriginalImages)
                        .addGap(5, 5, 5)
                        .addComponent(rdbSyncThumbnails)
                        .addGap(5, 5, 5)
                        .addComponent(cmbThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSyncOptionsLayout.createSequentialGroup()
                        .addComponent(jSeparator3)
                        .addGap(11, 11, 11))
                    .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                        .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkIncludeAllElements))
                            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(10, 10, 10)
                                .addComponent(rdbSyncAllFiles)
                                .addGap(5, 5, 5)
                                .addComponent(rdbSyncJpegOnly)
                                .addGap(5, 5, 5)
                                .addComponent(rdbSyncNoFiles)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlSyncOptionsLayout.setVerticalGroup(
            pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncOptionsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdbSyncJpegOnly)
                        .addComponent(rdbSyncAllFiles)
                        .addComponent(rdbSyncNoFiles)))
                .addGap(5, 5, 5)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbSyncOriginalImages)
                            .addComponent(rdbSyncThumbnails))))
                .addGap(5, 5, 5)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(pnlSyncOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(chkIncludeAllElements))
                .addGap(5, 5, 5))
        );

        pnlSyncToken.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sync Token", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("<html><b>Please provide your <i>WildLog Cloud Sync Token</i> to enable WildLog to synchronise the data.</b><br/> If you don't have a <i>WildLog Cloud Sync Token</i> then please contact <u>support@mywild.co.za</> for a custom quotation.</html>");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Sync Token:");

        txaSyncToken.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txaSyncToken.setLineWrap(true);
        txaSyncToken.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaSyncToken);

        btnConfirmSyncToken.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnConfirmSyncToken.setText("Confirm Token");
        btnConfirmSyncToken.setToolTipText("Validates the token and then enables the available features.");
        btnConfirmSyncToken.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirmSyncToken.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnConfirmSyncToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmSyncTokenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSyncTokenLayout = new javax.swing.GroupLayout(pnlSyncToken);
        pnlSyncToken.setLayout(pnlSyncTokenLayout);
        pnlSyncTokenLayout.setHorizontalGroup(
            pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSyncTokenLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlSyncTokenLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel6)
                    .addComponent(jSeparator4))
                .addGap(5, 5, 5))
        );
        pnlSyncTokenLayout.setVerticalGroup(
            pnlSyncTokenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSyncTokenLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(btnConfirmSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pnlWorkspace.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cloud Workspace", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Workspace ID:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Workspace Name:");

        lblWorkspaceID.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblWorkspaceID.setText("...");
        lblWorkspaceID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblWorkspaceIDMouseReleased(evt);
            }
        });

        txtWorkspaceName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout pnlWorkspaceLayout = new javax.swing.GroupLayout(pnlWorkspace);
        pnlWorkspace.setLayout(pnlWorkspaceLayout);
        pnlWorkspaceLayout.setHorizontalGroup(
            pnlWorkspaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWorkspaceLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlWorkspaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWorkspaceLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(31, 31, 31)
                        .addComponent(lblWorkspaceID))
                    .addGroup(pnlWorkspaceLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(10, 10, 10)
                        .addComponent(txtWorkspaceName)))
                .addGap(5, 5, 5))
        );
        pnlWorkspaceLayout.setVerticalGroup(
            pnlWorkspaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWorkspaceLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlWorkspaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWorkspaceID))
                .addGap(5, 5, 5)
                .addGroup(pnlWorkspaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtWorkspaceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pnlSyncToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlSyncOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlWorkspace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSyncToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(pnlSyncOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[ExportCloudWorkspace]");
        // Get the sync keys from the token
        String syncToken = getSyncToken();
        // Validate the token
        if (syncToken == null || syncToken.isEmpty() || syncToken.split(" ").length != 4) {
            WLOptionPane.showMessageDialog(this,
                    "<html>The provided <i>WildLog Cloud Sync Token</i> could not be read. "
                            + "<br>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                    "Invalid Sync Token!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Parse the token to get the necessary values
        String[] syncTokenValues = syncToken.split(" ");
        WildLogApp.LOGGER.log(Level.INFO, "Sync Mode: {}", syncTokenValues[0]);
        WildLogApp.LOGGER.log(Level.INFO, "Sync Account: {}", syncTokenValues[1]);
        if (syncTokenValues[0].equals("FREE")) {
            WLOptionPane.showMessageDialog(this,
                "<html>This feature is not available using the limited free <i>WildLog Cloud Sync Token</i>. "
                        + "<br>Please contact <u>support@mywild.co.za</> for help.</html>",
                "Free Token Exceeded!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final SyncAzure syncAzure = new SyncAzure(syncTokenValues[3], syncTokenValues[1], syncTokenValues[2], 
                Long.parseLong(lblWorkspaceID.getText()), WildLogApp.getApplication().getWildLogOptions().getDatabaseVersion());
        // Validate workspace name
        if (txtWorkspaceName.getText() == null || txtWorkspaceName.getText().trim().isEmpty()) {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please provide a valid Workspace Name.</html>",
                    "Invalid Workspace Name!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validate selection
        int totalSelectedNodes = getNumberOfSelectedNodes(treWorkspace.getModel(), (DefaultMutableTreeNode) treWorkspace.getModel().getRoot());
        if (totalSelectedNodes == 0) {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please select records to be exported.</html>",
                    "No Records Selected!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Close this popup
        setVisible(false);
        dispose();
        // Start the sync process
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                    @Override
                    protected Object doInBackground() throws Exception {
                        long startTime = System.currentTimeMillis();
                        setProgress(0);
                        setMessage("Starting the Cloud Export");
                        WildLogApp.LOGGER.log(Level.INFO, "Starting the Cloud Export: {} - {}", lblWorkspaceID.getText(), txtWorkspaceName.getText());
                        // Setup the report
                        Path feedbackFile = null;
                        PrintWriter feedback = null;
                        try {
                            Files.createDirectories(WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath());
                            feedbackFile = WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath().resolve(
                                    "CloudExportWorkspaceReport_" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ".txt");
                            feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                            feedback.println("-------------------------------------------------");
                            feedback.println("-------------- Cloud Export Report --------------");
                            feedback.println("-------------------------------------------------");
                            feedback.println("");
                            // Get selected records
                            setProgress(1);
                            setMessage("Busy with the Cloud Export - Calculating ... " + getProgress() + "%");
                            setProgress(2);
                            Set<Location> locations = new HashSet<>();
                            Set<Visit> visits = new HashSet<>();
                            Set<Element> elements = new HashSet<>();
                            Set<Sighting> sightings = new HashSet<>();
                            if (chkIncludeAllElements.isSelected()) {
                                List<Element> lstAllElements = app.getDBI().listElements(null, null, null, false, Element.class);
                                for (Element element : lstAllElements) {
                                    elements.add(element);
                                }
                            }
                            getRecordsToExport((DefaultMutableTreeNode) treWorkspace.getModel().getRoot(), totalSelectedNodes, this, new ProgressCounter(),
                                    locations, visits, elements, sightings);
                            Set<WildLogOptions> wildLogOptions = new HashSet<>(1);
                            WildLogOptions workspaceWildLogOptions = app.getDBI().findWildLogOptions(WildLogOptions.class);
                            workspaceWildLogOptions.setWorkspaceID(Long.parseLong(lblWorkspaceID.getText()));
                            workspaceWildLogOptions.setWorkspaceName(txtWorkspaceName.getText());
                            wildLogOptions.add(workspaceWildLogOptions);
                            // SYNC - Data
                            setProgress(10);
                            setMessage("Busy with the Cloud Export - Syncing Options ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.WILDLOG_OPTIONS, wildLogOptions);
                            setProgress(11);
                            setMessage("Busy with the Cloud Export - Syncing Creatures ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.ELEMENT, elements);
                            setProgress(20);
                            setMessage("Busy with the Cloud Export - Syncing Places ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.LOCATION, locations);
                            setProgress(25);
                            setMessage("Busy with the Cloud Export - Syncing Periods ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.VISIT, visits);
                            setProgress(35);
                            setMessage("Busy with the Cloud Export - Syncing Observations ... " + getProgress() + "%");
                            syncDataRecords(feedback, syncAzure, WildLogDataType.SIGHTING, sightings);
                            // SYNC - Files
                            if (!rdbSyncNoFiles.isSelected()) {
                                List<WildLogFile> lstWildLogFiles = new ArrayList<>();
                                for (Element element : elements) {
                                    lstWildLogFiles.addAll(app.getDBI().listWildLogFiles(element.getID(), null, WildLogFile.class));
                                }
                                for (Location location : locations) {
                                    lstWildLogFiles.addAll(app.getDBI().listWildLogFiles(location.getID(), null, WildLogFile.class));
                                }
                                for (Visit visit : visits) {
                                    lstWildLogFiles.addAll(app.getDBI().listWildLogFiles(visit.getID(), null, WildLogFile.class));
                                }
                                for (Sighting sighting : sightings) {
                                    lstWildLogFiles.addAll(app.getDBI().listWildLogFiles(sighting.getID(), null, WildLogFile.class));
                                }
                                setProgress(50);
                                setMessage("Busy with the Cloud Export - Syncing Files ... " + getProgress() + "%");
                                syncFileRecords(feedback, syncAzure, this, 20, lstWildLogFiles);
                                setProgress(70);
                                setMessage("Busy with the Cloud Export - Syncing Stashed Files ... " + getProgress() + "%");
                                syncStashedFileRecords(feedback, syncAzure, this, 29, visits);
                            }
                        }
                        // Finish the report
                        catch (Exception ex) {
                            if (feedback != null) {
                                feedback.println("");
                                feedback.println("--------------------------------------");
                                feedback.println("--------------- ERROR ----------------");
                                feedback.println(ex.toString());
                                feedback.println("--------------------------------------");
                                feedback.println("");
                            }
                            syncFail++;
                            throw ex;
                        }
                        finally {
                            if (feedback != null) {
                                feedback.println("");
                                feedback.println("--------------- SUMMARY ----------------");
                                feedback.println("");
                                feedback.println("Failed Sync Actions   : " + syncFail);
                                feedback.println("");
                                feedback.println("Data Uploads          : " + syncDataUp);
                                feedback.println("File Uploads          : " + syncFileUp.get());
                                feedback.println("Stash Uploads         : " + syncStashUp.get());
                                feedback.println("");
                                feedback.println("--------------- DURATION ----------------");
                                feedback.println("");
                                long duration = System.currentTimeMillis() - startTime;
                                int hours = (int) (((double) duration)/(1000.0*60.0*60.0));
                                int minutes = (int) (((double) duration - (hours*60*60*1000))/(1000.0*60.0));
                                int seconds = (int) (((double) duration - (hours*60*60*1000) - (minutes*60*1000))/(1000.0));
                                feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                                feedback.println("");
                                feedback.println("--------------------------------------");
                                feedback.println("-------------- FINISHED --------------");
                                feedback.println("--------------------------------------");
                                feedback.println("");
                                feedback.flush();
                                feedback.close();
                                // Open the summary document
                                if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_REMOTE) {
                                    UtilsFileProcessing.openFile(feedbackFile);
                                }
                                // Print summary to the logs
                                WildLogApp.LOGGER.log(Level.INFO, "Failed Sync Actions          : {}", syncFail);
                                WildLogApp.LOGGER.log(Level.INFO, "Synced Data Uploads          : {}", syncDataUp);
                                WildLogApp.LOGGER.log(Level.INFO, "Synced File Uploads          : {}", syncFileUp.get());
                                WildLogApp.LOGGER.log(Level.INFO, "Synced Stash Uploads         : {}", syncStashUp.get());
                                WildLogApp.LOGGER.log(Level.INFO, "Cloud Export Duration: {} hours, {} minutes, {} seconds", hours, minutes, seconds);
                            }
                        }
                        setProgress(100);
                        setMessage("Done with the Cloud Export");
                        return null;
                    }

                    @Override
                    protected void finished() {
                        super.finished();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                                        "<html>The Cloud Export process has completed."
                                                + "<br/><br/><hr/>"
                                                + "<br/>Workspace ID          : " + lblWorkspaceID.getText()
                                                + "<br/>Workspace Name        : " + txtWorkspaceName.getText()
                                                + "<br/><br/><hr/>"
                                                + "<br/><b>Failed Sync Actions   : " + syncFail + "</b>"
                                                + "<br/>"
                                                + "<br/>Data Uploads          : " + syncDataUp
                                                + "<br/>File Uploads          : " + syncFileUp.get()
                                                + "<br/>Stash Uploads         : " + syncStashUp.get()
                                                + "<br/><br/><hr/><br/></html>",
                                        "Completed Cloud Export", WLOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                });
            }
        });
    }//GEN-LAST:event_btnConfirmActionPerformed

    private void getRecordsToExport(DefaultMutableTreeNode inNode, int inTotalNodes, ProgressbarTask inProgressbarTask, ProgressCounter inCounter, 
            Set<Location> inLocations, Set<Visit> inVisits, Set<Element> inElements, Set<Sighting> inSightings) {
        if (inNode.getUserObject() instanceof WorkspaceTreeDataWrapper) {
            WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) inNode.getUserObject();
            if (dataWrapper.isSelected()) {
                if (dataWrapper.getDataObject() instanceof Location) {
                    Location location = app.getDBI().findLocation(((Location) dataWrapper.getDataObject()).getID(), null, false, Location.class);
                    inLocations.add(location);
                }
                else
                if ( dataWrapper.getDataObject() instanceof Visit) {
                    Visit visit = app.getDBI().findVisit(((Visit) dataWrapper.getDataObject()).getID(), null, true, Visit.class);
                    inVisits.add(visit);
                }
                else
                if (dataWrapper.getDataObject() instanceof Element) {
                    Element element = app.getDBI().findElement(((Element) dataWrapper.getDataObject()).getID(), null, false, Element.class);
                    inElements.add(element);
                }
                else
                if (dataWrapper.getDataObject() instanceof SightingWrapper) {
                    Sighting sighting = app.getDBI().findSighting((((SightingWrapper) dataWrapper.getDataObject()).getSighting()).getID(), true, Sighting.class);
                    inSightings.add(sighting);
                }
                inCounter.counter++;
            }
            inProgressbarTask.setTaskProgress(2 + (int)(((double)inCounter.counter/(double) inTotalNodes) * 8.0));
            inProgressbarTask.setMessage("Busy with the Cloud Export - Calculating ... " + inProgressbarTask.getProgress() + "%");
        }
        for (int t = 0; t < treWorkspace.getModel().getChildCount(inNode); t++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treWorkspace.getModel().getChild(inNode, t);
            getRecordsToExport(childNode, inTotalNodes, inProgressbarTask, inCounter, inLocations, inVisits, inElements, inSightings);
        }
    }
    
    private void syncDataRecords(PrintWriter inFeedback, SyncAzure inSyncAzure, WildLogDataType inDataType, Set<? extends DataObjectWithAudit> inWorkspaceEntries) {
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + inDataType.getDescription() + " - Workspace Entries: " + inWorkspaceEntries.size());
        // UP: Upload the workspace records
        List<SyncAction> lstSyncActions = new ArrayList<>();
        List<SyncAction> lstExtraDataSyncActions = new ArrayList<>();
        for (DataObjectWithAudit workspaceEntry : inWorkspaceEntries) {
            lstSyncActions.add(new SyncAction("CLOUD_UPLOAD", inDataType, workspaceEntry.getID(), "", workspaceEntry));
            // As daar ExtraData is dan moet dit ook gesync word
            if (workspaceEntry instanceof DataObjectWithWildLogFile) {
                List<ExtraData> lstExtraDatas = app.getDBI().listExtraDatas(null, workspaceEntry.getID(), ExtraData.class);
                for (ExtraData extraData : lstExtraDatas) {
                    lstExtraDataSyncActions.add(new SyncAction("CLOUD_UPLOAD", WildLogDataType.EXTRA, extraData.getID(), "", extraData));
                }
            }
        }
        if (!lstSyncActions.isEmpty()) {
            List<DataObjectWithAudit> lstRecords = new ArrayList<>(lstSyncActions.size());
            List<Long> lstRecordIDs = new ArrayList<>(lstSyncActions.size());
            for (SyncAction syncAction : lstSyncActions) {
                lstRecords.add(syncAction.data);
                lstRecordIDs.add(syncAction.recordID);
            }
            logIfFailed(inFeedback, new SyncAction("CLOUD_UPLOAD", inDataType, lstRecords.size(), Arrays.toString(lstRecordIDs.toArray()), null), 
                    inSyncAzure.uploadDataBatch(inDataType, lstRecords));
            syncDataUp = syncDataUp + lstRecords.size();
        }
        if (!lstExtraDataSyncActions.isEmpty()) {
            List<DataObjectWithAudit> lstRecords = new ArrayList<>(lstExtraDataSyncActions.size());
            List<Long> lstRecordIDs = new ArrayList<>(lstExtraDataSyncActions.size());
            for (SyncAction syncAction : lstExtraDataSyncActions) {
                lstRecords.add(syncAction.data);
                lstRecordIDs.add(syncAction.recordID);
            }
            logIfFailed(inFeedback, new SyncAction("CLOUD_UPLOAD", WildLogDataType.EXTRA, lstRecords.size(), Arrays.toString(lstRecordIDs.toArray()), null), 
                    inSyncAzure.uploadDataBatch(WildLogDataType.EXTRA, lstRecords));
            syncDataUp = syncDataUp + lstRecords.size();
        }
    }
    
    private void syncFileRecords(PrintWriter inFeedback, SyncAzure inSyncAzure, ProgressbarTask inProgressbar, int inProgressStepSize,
            List<WildLogFile> inLstWildLogFiles) {
        int baseProgress = inProgressbar.getProgress();
        // UP: Get the sync actions
        WildLogApp.LOGGER.log(Level.INFO, "Sync - Upload Files: " + inLstWildLogFiles.size());
        List<SyncAction> lstSyncActions = new ArrayList<>();
        for (WildLogFile workspaceEntry : inLstWildLogFiles) {
            if (rdbSyncAllFiles.isSelected() || (rdbSyncJpegOnly.isSelected() && WildLogFileExtentions.Images.isJPG(workspaceEntry.getAbsolutePath()))) {
                if (!calculateResizedFileSizeAsSyncIndicator(inFeedback, workspaceEntry)) {
                    continue;
                }
                lstSyncActions.add(new SyncAction("CLOUD_UPLOAD", WildLogDataType.FILE, workspaceEntry.getID(), 
                        workspaceEntry.getLinkType().getDescription(), workspaceEntry));
            }
        }
        // Upload the files
        if (!lstSyncActions.isEmpty()) {
            // Note: Files are always uploaded one at a time (but can be multi-threaded)
            ExecutorService syncExecutor = Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_Sync(Upload)"));
            AtomicInteger threadLoopCount = new AtomicInteger(0);
            final int threadBaseProgress = baseProgress;
            final double threadTotalSize = lstSyncActions.size();
            for (SyncAction syncAction : lstSyncActions) {
                syncExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        WildLogFile wildLogFile = (WildLogFile) syncAction.data;
                        syncAction.details = wildLogFile.getLinkType().getDescription() + "_FILE";
                        uploadWildLogFile(inFeedback, inSyncAzure, syncAction, wildLogFile, false);
                        syncAction.details = wildLogFile.getLinkType().getDescription() + "_DATA";
                        logIfFailed(inFeedback, syncAction, inSyncAzure.uploadData(WildLogDataType.FILE, syncAction.data));
                        syncFileUp.incrementAndGet();
                        inProgressbar.setTaskProgress(threadBaseProgress + ((int) (((double) inProgressStepSize) 
                                * (double) threadLoopCount.incrementAndGet() / threadTotalSize)));
                        inProgressbar.setMessage("Busy with the Cloud Export - Syncing Files ... " + inProgressbar.getProgress() + "%");
                    }
                });
            }
            // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
            try {
                syncExecutor.shutdown();
                if (!syncExecutor.awaitTermination(3, TimeUnit.DAYS)) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Sync - ExecutorService shutdown timeout!!!");
                    logIfFailed(inFeedback, new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, 0, "TIMEOUT", null), false);
                }
            }
            catch (InterruptedException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, "Sync - ExecutorService shutdown failed!!!");
                logIfFailed(inFeedback, new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, 0, "ERROR", null), false);
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
    }

    private void uploadWildLogFile(PrintWriter inFeedback, SyncAzure inSyncAzure, SyncAction syncAction, WildLogFile wildLogFile, boolean isStashedFile) {
        String date = "";
        String latitude = "";
        String longitude = "";
        try {
            date = UtilsTime.EXIF_DATE_FORMAT.format(UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromWildLogFile(wildLogFile)));
            if (WildLogFileExtentions.Images.isJPG(wildLogFile.getAbsolutePath())) {
                DataObjectWithGPS gps = UtilsImageProcessing.getExifGpsFromJpeg(wildLogFile.getAbsolutePath());
                if (gps != null && UtilsGPS.hasGPSData(gps)) {
                    latitude = gps.getLatDegrees() + "° " + gps.getLatMinutes() + "' " + Double.toString(gps.getLatSeconds()).replace(',', '.') + "\"";
                    longitude = gps.getLonDegrees() + "° " + gps.getLonMinutes() + "' " + Double.toString(gps.getLonSeconds()).replace(',', '.') + "\"";
                }
            }
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String recordID;
        if (!isStashedFile) {
            recordID = Long.toString(wildLogFile.getID());
        }
        else {
            String filename = wildLogFile.getRelativePath().getFileName().toString();
            recordID = filename.substring(0, filename.lastIndexOf('.'));
        }
        logIfFailed(inFeedback, syncAction,
                inSyncAzure.uploadFile(wildLogFile.getLinkType(), getResizedFilePath(wildLogFile), wildLogFile.getLinkID(), 
                        recordID, date, latitude, longitude));
    }
    
    private Path getResizedFilePath(WildLogFile inWildLogFile) {
        if (rdbSyncThumbnails.isSelected() && WildLogFileExtentions.Images.isJPG(inWildLogFile.getAbsolutePath())) {
            // Don't use a thumbnail if the original is smaller than the thumbnail
            WildLogThumbnailSizes thumbnailSizes = (WildLogThumbnailSizes) cmbThumbnailSize.getSelectedItem();
            int imageWidth = 0;
            int imageHeight = 0;
            ImageReader imageReader = null;
            FileImageInputStream inputStream = null;
            try {
                inputStream = new FileImageInputStream(inWildLogFile.getAbsolutePath().toFile());
                Iterator<ImageReader> imageReaderList = ImageIO.getImageReaders(inputStream);
                imageReader = imageReaderList.next();
                imageReader.setInput(inputStream);
                imageWidth = imageReader.getWidth(imageReader.getMinIndex());
                imageHeight = imageReader.getHeight(imageReader.getMinIndex());
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            finally {
                if (imageReader != null) {
                    imageReader.dispose();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
            if (thumbnailSizes.getSize() < Math.max(imageWidth, imageHeight)) {
                return inWildLogFile.getAbsoluteThumbnailPath(thumbnailSizes);
            }
        }
        return inWildLogFile.getAbsolutePath();
    }
    
    private boolean calculateResizedFileSizeAsSyncIndicator(PrintWriter inFeedback, WildLogFile inWorkspaceFile) {
        try {
            inWorkspaceFile.setSyncIndicator(Files.size(getResizedFilePath(inWorkspaceFile)));
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            logIfFailed(inFeedback, new SyncAction("ERROR", WildLogDataType.FILE, inWorkspaceFile.getID(), Long.toString(inWorkspaceFile.getLinkID()), inWorkspaceFile), false);
            return false;
        }
        return true;
    }
    
    private void syncStashedFileRecords(PrintWriter inFeedback, SyncAzure inSyncAzure, ProgressbarTask inProgressbar, int inProgressStepSize,
            Set<Visit> inVisits) {
        int baseProgress = inProgressbar.getProgress();
        List<Visit> lstWorkspaceStashedVisits = new ArrayList<>();
        for (Visit visit : inVisits) {
            if (visit.getType() == VisitType.STASHED) {
                lstWorkspaceStashedVisits.add(visit);
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "Sync - " + WildLogDataType.STASH.getDescription() + " - All Workspace Stashed Visits: " + lstWorkspaceStashedVisits.size());
        // UP: Get the sync actions
        List<SyncAction> lstSyncActions = new ArrayList<>();
        for (Visit workspaceVisit : lstWorkspaceStashedVisits) {
            Path stashPath = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(workspaceVisit.getName());
            List<Path> lstAllFiles = UtilsFileProcessing.getListOfFilesToImport(stashPath.toFile(), false);
            // Note: Die stashed files sal klaar alles in een folder wees
            int counter = 0;
            for (Path sourcePath : lstAllFiles) {
                WildLogFile wrapperWildLogFile = new WildLogFile();
                wrapperWildLogFile.setID(counter++);
                wrapperWildLogFile.setLinkID(workspaceVisit.getID());
                wrapperWildLogFile.setLinkType(WildLogDataType.STASH);
                if (WildLogFileExtentions.Images.isKnownExtention(sourcePath)) {
                    wrapperWildLogFile.setFileType(WildLogFileType.IMAGE);
                }
                else
                if (WildLogFileExtentions.Movies.isKnownExtention(sourcePath)) {
                    wrapperWildLogFile.setFileType(WildLogFileType.MOVIE);
                }
                else {
                    // Only movies or images can be stashed
                    continue;
                }
                wrapperWildLogFile.setDBFilePath(WildLogPaths.getFullWorkspacePrefix().relativize(sourcePath).toString());
                lstSyncActions.add(new SyncAction("CLOUD_UPLOAD", WildLogDataType.STASH, wrapperWildLogFile.getID(), 
                        Long.toString(workspaceVisit.getID()), wrapperWildLogFile));
            }
        }
        // Upload the files
        if (!lstSyncActions.isEmpty()) {
            // Note: Files are always uploaded one at a time (but can be multi-threaded)
            ExecutorService syncExecutor = Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_Sync(Upload-Stash)"));
            AtomicInteger threadLoopCount = new AtomicInteger(0);
            final int threadBaseProgress = baseProgress;
            final double threadTotalSize = lstSyncActions.size();
            for (SyncAction syncAction : lstSyncActions) {
                syncExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        WildLogFile wildLogFile = (WildLogFile) syncAction.data;
                        uploadWildLogFile(inFeedback, inSyncAzure, syncAction, wildLogFile, true);
                        syncStashUp.incrementAndGet();
                        inProgressbar.setTaskProgress(threadBaseProgress + ((int) (((double) inProgressStepSize) 
                                * (double) threadLoopCount.incrementAndGet() / threadTotalSize)));
                        inProgressbar.setMessage("Busy with the Cloud Export - Syncing Stashed Files ... " + inProgressbar.getProgress() + "%");
                    }
                });
            }
            // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
            try {
                syncExecutor.shutdown();
                if (!syncExecutor.awaitTermination(3, TimeUnit.DAYS)) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Sync - ExecutorService shutdown timeout!!!");
                    logIfFailed(inFeedback, new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, 0, "TIMEOUT", null), false);
                }
            }
            catch (InterruptedException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, "Sync - ExecutorService shutdown failed!!!");
                logIfFailed(inFeedback, new SyncAction("WORKSPACE_DOWNLOAD", WildLogDataType.FILE, 0, "ERROR", null), false);
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
    }
    
    private void treWorkspaceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treWorkspaceMousePressed
        // Gebruk die muis se posisie om die node te kry want die LastSelected node is verkeerd, veral as mens die +/- gebruik om te expand/colapse...
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

    private void rdbSyncAllFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncAllFilesActionPerformed
        if (rdbSyncAllFiles.isSelected()) {
            rdbSyncOriginalImages.setEnabled(true);
            rdbSyncThumbnails.setEnabled(true);
        }
    }//GEN-LAST:event_rdbSyncAllFilesActionPerformed

    private void rdbSyncJpegOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncJpegOnlyActionPerformed
        if (rdbSyncJpegOnly.isSelected()) {
            rdbSyncOriginalImages.setEnabled(true);
            rdbSyncThumbnails.setEnabled(true);
        }
    }//GEN-LAST:event_rdbSyncJpegOnlyActionPerformed

    private void rdbSyncNoFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncNoFilesActionPerformed
        if (rdbSyncNoFiles.isSelected()) {
            rdbSyncOriginalImages.setEnabled(false);
            rdbSyncThumbnails.setEnabled(false);
        }
    }//GEN-LAST:event_rdbSyncNoFilesActionPerformed

    private void rdbSyncOriginalImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncOriginalImagesActionPerformed
        if (rdbSyncOriginalImages.isSelected()) {
            cmbThumbnailSize.setEnabled(false);
        }
    }//GEN-LAST:event_rdbSyncOriginalImagesActionPerformed

    private void rdbSyncThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbSyncThumbnailsActionPerformed
        if (rdbSyncThumbnails.isSelected()) {
            cmbThumbnailSize.setEnabled(true);
        }
    }//GEN-LAST:event_rdbSyncThumbnailsActionPerformed

    private void btnConfirmSyncTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmSyncTokenActionPerformed
        if (txaSyncToken.getText() == null || txaSyncToken.getText().trim().isEmpty()) {
            configureFreeToken();
        }
        else {
            String syncToken = null;
            try {
                syncToken = TokenEncryptor.decrypt(txaSyncToken.getText().trim());
            }
            catch (Exception ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            if (syncToken == null || syncToken.isEmpty()) {
                WLOptionPane.showMessageDialog(this,
                    "<html>The provided <i>WildLog Cloud Sync Token</i> could not be read. "
                    + "<br>Please provide a valid <i>WildLog Cloud Sync Token</i>, or contact <u>support@mywild.co.za</> for help.</html>",
                    "Invalid Sync Token!", JOptionPane.ERROR_MESSAGE);
            }
            // Setup the UI
            String[] syncTokenValues = syncToken.split(" ");
            switch (syncTokenValues[0]) {
                case "FREE": configureFreeToken(); break;
                case "BASIC": configureBasicToken(); break;
                case "FULL": configureFullToken(); break;
                default: configureFreeToken(); break;
            }
        }
    }//GEN-LAST:event_btnConfirmSyncTokenActionPerformed

    private void lblWorkspaceIDMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWorkspaceIDMouseReleased
        if ((evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt))) {
            JPopupMenu clipboardPopup = new JPopupMenu();
            // Build the copy popup
            JMenuItem copyItem = new JMenuItem("Copy Workspace ID", new ImageIcon(WildLogApp.class.getResource("resources/icons/copy.png")));
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doClipboardCopy(lblWorkspaceID.getText());
                }
            });
            clipboardPopup.add(copyItem);
            // Wrap up and show up the popup
            clipboardPopup.pack();
            clipboardPopup.show(evt.getComponent(), evt.getPoint().x, evt.getPoint().y);
            clipboardPopup.setVisible(true);
        }
    }//GEN-LAST:event_lblWorkspaceIDMouseReleased

    private String getSyncToken() {
        // Get the sync keys from the token
        String syncToken;
        if (txaSyncToken.getText() == null || txaSyncToken.getText().trim().isEmpty()) {
            final char[] buffer = new char[450];
            final StringBuilder builder = new StringBuilder(450);
            try (Reader in = new BufferedReader(new InputStreamReader(WildLogApp.class.getResourceAsStream("sync/FreeSyncToken"), "UTF-8"))) {
                int length = 0;
                while (length >= 0) {
                    length = in.read(buffer, 0, buffer.length);
                    if (length > 0) {
                        builder.append(buffer, 0, length);
                    }
                }
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            syncToken = TokenEncryptor.decrypt(builder.toString());
        }
        else {
            syncToken = TokenEncryptor.decrypt(txaSyncToken.getText().trim());
        }
        return syncToken;
    }
    
    private void configureFreeToken() {
        // OK button
        btnConfirm.setEnabled(false);
        // Content
        rdbSyncNoFiles.setSelected(true);
        rdbSyncAllFiles.setEnabled(false);
        rdbSyncJpegOnly.setEnabled(false);
        rdbSyncNoFiles.setEnabled(false);
        // Size
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(false);
        rdbSyncOriginalImages.setEnabled(false);
        cmbThumbnailSize.setEnabled(false);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.S0020_VERY_TINY);
        cmbThumbnailSize.removeItem(WildLogThumbnailSizes.S2048_SYNC_LIMIT);
    }
    
    private void configureBasicToken() {
        // OK button
        btnConfirm.setEnabled(true);
        // Content
        rdbSyncJpegOnly.setSelected(true);
        rdbSyncAllFiles.setEnabled(false);
        rdbSyncJpegOnly.setEnabled(true);
        rdbSyncNoFiles.setEnabled(true);
        // Size
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(false);
        rdbSyncOriginalImages.setEnabled(false);
        cmbThumbnailSize.setEnabled(true);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.S0256_SYNC_EXPORT);
        cmbThumbnailSize.removeItem(WildLogThumbnailSizes.S2048_SYNC_LIMIT);
    }
    
    private void configureFullToken() {
        // OK button
        btnConfirm.setEnabled(true);
        // Content
        rdbSyncJpegOnly.setSelected(true);
        rdbSyncAllFiles.setEnabled(true);
        rdbSyncJpegOnly.setEnabled(true);
        rdbSyncNoFiles.setEnabled(true);
        // Size
        rdbSyncThumbnails.setSelected(true);
        rdbSyncThumbnails.setEnabled(true);
        rdbSyncOriginalImages.setEnabled(true);
        cmbThumbnailSize.setEnabled(true);
        cmbThumbnailSize.setSelectedItem(WildLogThumbnailSizes.S0512_LARGE);
        cmbThumbnailSize.removeItem(WildLogThumbnailSizes.S2048_SYNC_LIMIT); // Removing just to be sure it's not added twice (below)
        cmbThumbnailSize.addItem(WildLogThumbnailSizes.S2048_SYNC_LIMIT);
    }
    
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

    private void loadLocationTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("WildLog Workspace");
        List<Location> locations = app.getDBI().listLocations(null, false, Location.class);
        Map<Long, DefaultMutableTreeNode> mapElements;
        Map<Long, DefaultMutableTreeNode> mapVisits;
        Collections.sort(locations);
        for (Location location : locations) {
            mapElements = new HashMap<>(500);
            mapVisits = new HashMap<>(500);
            DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(location, false));
            root.add(locationNode);
            List<Sighting> sightings = app.getDBI().listSightings(0, location.getID(), 0, true, Sighting.class);
            Collections.sort(sightings, new Comparator<Sighting>() {
                @Override
                public int compare(Sighting sighting1, Sighting sighting2) {
                    int result = sighting1.getCachedVisitName().compareTo(sighting2.getCachedVisitName());
                    if (result == 0) {
                        result = sighting1.getCachedElementName().compareTo(sighting2.getCachedElementName());
                        if (result == 0) {
                            result = sighting1.getDate().compareTo(sighting2.getDate());
                        }
                    }
                    return result;
                }
            });
            for (Sighting sighting : sightings) {
                DefaultMutableTreeNode visitNode = mapVisits.get(sighting.getVisitID());
                if (visitNode == null) {
                    visitNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(app.getDBI().findVisit(sighting.getVisitID(), null, true, Visit.class), false));
                    mapVisits.put(sighting.getVisitID(), visitNode);
                    // Clear die hashmap hier as 'n nuwe visit gelaai word (die sightings behoort volgens visit gesort te wees, so die visit sal nie weer verskyn nie.
                    mapElements.clear();
                }
                locationNode.add(visitNode);
                DefaultMutableTreeNode elementNode = mapElements.get(sighting.getElementID());
                if (elementNode == null) {
                    elementNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(app.getDBI().findElement(sighting.getElementID(), null, false, Element.class), false));
                    mapElements.put(sighting.getElementID(), elementNode);
                }
                visitNode.add(elementNode);
                DefaultMutableTreeNode sightingNode = new DefaultMutableTreeNode(new WorkspaceTreeDataWrapper(new SightingWrapper(sighting, true), false));
                elementNode.add(sightingNode);
            }
            // Also add stashed visits
            List<Visit> lstStashedVisits = app.getDBI().listVisits(null, location.getID(), VisitType.STASHED, false, Visit.class);
            for (Visit stashedVisit : lstStashedVisits) {
                DefaultMutableTreeNode stashedVisitNode = new DefaultMutableTreeNode(
                        new WorkspaceTreeDataWrapper(app.getDBI().findVisit(stashedVisit.getID(), null, true, Visit.class), false));
                locationNode.add(stashedVisitNode);
            }
        }
        treWorkspace.setModel(new DefaultTreeModel(root));
    }
    
    public void logIfFailed(PrintWriter inFeedback, SyncAction inSyncAction, boolean inResult) {
        if (inResult) {
            inFeedback.println(inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID);
        }
        else {
            inFeedback.println("SYNC_FAIL " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID);
            WildLogApp.LOGGER.log(Level.ERROR, "Sync - Failed: " + inSyncAction.command + " " + inSyncAction.type.getDescription() + " " + inSyncAction.details + " " + inSyncAction.recordID + " " + inSyncAction.data);
            WildLogApp.LOGGER.log(Level.INFO, "Sync - Stacktrace:");
            new Exception("Sync Failure").printStackTrace(System.out);
            syncFail++;
        }
    }
    
    private int getNumberOfSelectedNodes(TreeModel inModel, DefaultMutableTreeNode inNode) {
        int count = 0;
        if (inNode.getUserObject() instanceof WorkspaceTreeDataWrapper) {
            WorkspaceTreeDataWrapper dataWrapper = (WorkspaceTreeDataWrapper) inNode.getUserObject();
            if (dataWrapper.isSelected()) {
                count = 1;
            }
        }
        int numberOfChildren = inModel.getChildCount(inNode);
        for (int i = 0; i < numberOfChildren; i++) {
            count += getNumberOfSelectedNodes(inModel, (DefaultMutableTreeNode) inModel.getChild(inNode, i));
        }
        return count;
    }
    
    private class SyncAction {
        private String command;
        private WildLogDataType type;
        private long recordID;
        private String details;
        private DataObjectWithAudit data;

        private SyncAction(String inCommand, WildLogDataType inType, long inRecordID, String inDetails, DataObjectWithAudit inData) {
            command = inCommand;
            type = inType;
            recordID = inRecordID;
            details = inDetails;
            data = inData;
        }
    }
    
    private class ProgressCounter {
        public int counter = 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnConfirmSyncToken;
    private javax.swing.JCheckBox chkIncludeAllElements;
    private javax.swing.JComboBox<WildLogThumbnailSizes> cmbThumbnailSize;
    private javax.swing.ButtonGroup grpFiles;
    private javax.swing.ButtonGroup grpImages;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblWorkspaceID;
    private javax.swing.JPanel pnlSyncOptions;
    private javax.swing.JPanel pnlSyncToken;
    private javax.swing.JPanel pnlWorkspace;
    private javax.swing.JRadioButton rdbSyncAllFiles;
    private javax.swing.JRadioButton rdbSyncJpegOnly;
    private javax.swing.JRadioButton rdbSyncNoFiles;
    private javax.swing.JRadioButton rdbSyncOriginalImages;
    private javax.swing.JRadioButton rdbSyncThumbnails;
    private javax.swing.JTree treWorkspace;
    private javax.swing.JTextArea txaSyncToken;
    private javax.swing.JTextField txtWorkspaceName;
    // End of variables declaration//GEN-END:variables
}
