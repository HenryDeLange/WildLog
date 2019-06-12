package wildlog.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.data.utils.UtilsData;
import wildlog.ui.dialogs.ExportDialog;
import wildlog.ui.dialogs.IUCNInformationDialog;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.helpers.FileDrop;
import wildlog.ui.helpers.UtilsPanelGenerator;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;


public class PanelElement extends PanelCanSetupHeader implements PanelNeedsRefreshWhenDataChanges {
    private final WildLogApp app;
    private int imageIndex;
    private Element element;
    private Element lastSavedElement;
    private boolean isPopup = false;
    private PanelNeedsRefreshWhenDataChanges panelToRefresh;

    
    public PanelElement(WildLogApp inApp, Element inElement, boolean inIsPopup, PanelNeedsRefreshWhenDataChanges inPanelToRefresh) {
        app = inApp;
        element = inElement;
        isPopup = inIsPopup;
        panelToRefresh = inPanelToRefresh;
        lastSavedElement = element.cloneShallow();
        setupUI();
        if (inIsPopup) {
            btnAddSighting.setEnabled(false);
            btnBrowse.setEnabled(false);
            btnDeleteImage.setEnabled(false);
            btnGoLocation.setEnabled(false);
            btnExport.setEnabled(false);
            btnMap.setEnabled(false);
            btnNextImage.setEnabled(false);
            btnPreviousImage.setEnabled(false);
            btnReport.setEnabled(false);
            btnSetMainImage.setEnabled(false);
            btnSlideshow.setEnabled(false);
            btnUploadImage.setEnabled(false);
            rdbLocations.setEnabled(false);
            rdbSightings.setEnabled(false);
        }
    }

    public PanelElement(WildLogApp inApp, Element inElement) {
        app = inApp;
        element = inElement;
        lastSavedElement = element.cloneShallow();
        setupUI();
    }

    private void setupUI() {
        initComponents();
        // Setup images
        imageIndex = 0;
        int fotoCount = app.getDBI().countWildLogFiles(0, element.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
        setupNumberOfImages();
        // Setup the table
        tblLocation.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblLocation);
        // setup the file dropping
        if (!isPopup) {
            FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
                @Override
                public void filesDropped(List<File> inFiles) {
                    btnUpdateActionPerformed(null);
                    if (!txtPrimaryName.getBackground().equals(Color.RED)) {
                        uploadFiles(inFiles);
                    }
                }
            });
        }
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtPrimaryName);
        UtilsUI.attachClipboardPopup(txtScienceName);
        UtilsUI.attachClipboardPopup(txtOtherName);
        UtilsUI.attachClipboardPopup(txtBehaviourDescription);
        UtilsUI.attachClipboardPopup(txtDescription);
        UtilsUI.attachClipboardPopup(txtDiagnosticDescription);
        UtilsUI.attachClipboardPopup(txtDistribution);
        UtilsUI.attachClipboardPopup(txtNutrition);
        UtilsUI.attachClipboardPopup(txtReferenceID);
        // Setup info for tab headers
        tabTitle = element.getPrimaryName();
        tabID = element.getID();
        tabIconURL = app.getClass().getResource("resources/icons/Element.gif");
        // Scroll to the top of the text areas
        txtBehaviourDescription.setCaretPosition(0);
        txtDescription.setCaretPosition(0);
        txtDiagnosticDescription.setCaretPosition(0);
        txtDistribution.setCaretPosition(0);
        txtNutrition.setCaretPosition(0);
        // Enforce user access
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            btnExport.setEnabled(false);
            btnExport.setVisible(false);
            btnSlideshow.setEnabled(false);
            btnSlideshow.setVisible(false);
            if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER) {
                btnDeleteImage.setEnabled(false);
                btnDeleteImage.setVisible(false);
                btnReport.setEnabled(false);
                btnReport.setVisible(false);
            }
        }
    }

    private void uploadFiles(List<File> inFiles) {
        UtilsFileProcessing.performFileUpload(element,
                Paths.get(Element.WILDLOG_FOLDER_PREFIX).resolve(element.getPrimaryName()), WildLogDataType.ELEMENT, 
                inFiles.toArray(new File[inFiles.size()]),
                new Runnable() {
                    @Override
                    public void run() {
                        imageIndex = 0;
                        UtilsImageProcessing.setupFoto(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
                        setupNumberOfImages();
                        // everything went well - saving
                        btnUpdateActionPerformed(null);
                    }
                },
                app, true, null, true, true);
    }

    public Element getElement() {
        return element;
    }
    public void setElement(Element inElement) {
        element = inElement;
    }

    @Override
    public boolean closeTab() {
        btnUpdate.requestFocus();
        populateElementFromUI();
        if (lastSavedElement.hasTheSameContent(element)) {
            ((JTabbedPane)getParent()).remove(this);
             return true;
        }
        else {
            String name = element.getPrimaryName();
            if (name == null || name.isEmpty()) {
                name = "<New Creature>";
            }
            int result = WLOptionPane.showConfirmDialog(app.getMainFrame(), 
                    "Save before closing this tab for " + name + "?", 
                    "You have unsaved data", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                btnUpdateActionPerformed(null);
                if (element.getPrimaryName().trim().length() > 0 && UtilsData.checkCharacters(element.getPrimaryName().trim())) {
                    // Do the save action without closing the tab to show the error message
                    ((JTabbedPane)getParent()).remove(this);
                     return true;
                }
            }
            else
            if (result == JOptionPane.NO_OPTION) {
                ((JTabbedPane)getParent()).remove(this);
                 return true;
            }
        }
         return false;
    }

    @Override
    public void doTheRefresh(Object inIndicator) {
        formComponentShown(null);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        elementIncludes = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jLabel65 = new javax.swing.JLabel();
        lblNumberOfSightings = new javax.swing.JLabel();
        lblElementName = new javax.swing.JLabel();
        pnlFiles = new javax.swing.JPanel();
        btnSetMainImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        btnPreviousImage = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        btnDeleteImage = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        pnlButtons = new javax.swing.JPanel();
        btnAddSighting = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnSlideshow = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnMap = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        pnlNames = new javax.swing.JPanel();
        txtScienceName = new javax.swing.JTextField();
        txtReferenceID = new javax.swing.JTextField();
        txtOtherName = new javax.swing.JTextField();
        txtPrimaryName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        btnCheckIUCNOtherName = new javax.swing.JButton();
        btnINaturalist = new javax.swing.JButton();
        pnlInfo1 = new javax.swing.JPanel();
        cmbFeedingClass = new javax.swing.JComboBox();
        cmbEndangeredStatus = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        jLabel60 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        txtDiagnosticDescription = new javax.swing.JTextArea();
        pnlInfo2 = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        txtBehaviourDescription = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDistribution = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel61 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNutrition = new javax.swing.JTextArea();
        pnlTables = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        lblLocationsOrElements = new javax.swing.JLabel();
        rdbLocations = new javax.swing.JRadioButton();
        btnGoLocation = new javax.swing.JButton();
        lblNumberOfLocations = new javax.swing.JLabel();
        rdbSightings = new javax.swing.JRadioButton();

        setBackground(new java.awt.Color(227, 240, 227));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName(element.getPrimaryName());
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        elementIncludes.setBackground(new java.awt.Color(227, 240, 227));
        elementIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setName("elementIncludes"); // NOI18N
        elementIncludes.setPreferredSize(new java.awt.Dimension(1008, 585));

        jSeparator10.setName("jSeparator10"); // NOI18N

        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel65.setText("Observations:");
        jLabel65.setName("jLabel65"); // NOI18N

        lblNumberOfSightings.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N

        lblElementName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementName.setText(element.getPrimaryName());
        lblElementName.setName("lblElementName"); // NOI18N

        pnlFiles.setBackground(new java.awt.Color(227, 240, 227));
        pnlFiles.setName("pnlFiles"); // NOI18N

        btnSetMainImage.setBackground(new java.awt.Color(227, 240, 227));
        btnSetMainImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/DefaultImage.png"))); // NOI18N
        btnSetMainImage.setText("Set as First");
        btnSetMainImage.setToolTipText("Make this the default (first) file for the Creature.");
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setIconTextGap(2);
        btnSetMainImage.setMargin(new java.awt.Insets(2, 1, 2, 1));
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });

        btnNextImage.setBackground(new java.awt.Color(227, 240, 227));
        btnNextImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImage.setToolTipText("Load the next file.");
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });

        btnUploadImage.setBackground(new java.awt.Color(227, 240, 227));
        btnUploadImage.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UploadImage.png"))); // NOI18N
        btnUploadImage.setText("<html><u>Upload Files</u></html>");
        btnUploadImage.setToolTipText("<html>Upload a file for this Creature. <br/>You can also drag-and-drop files onto the above box to upload it. <br/>(Note: Drag-and-drop only works on supported platforms.)</html>");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setIconTextGap(2);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        lblNumberOfImages.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N

        btnPreviousImage.setBackground(new java.awt.Color(227, 240, 227));
        btnPreviousImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImage.setToolTipText("Load the previous file.");
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });

        lblImage.setBackground(new java.awt.Color(0, 0, 0));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setOpaque(true);
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });

        btnDeleteImage.setBackground(new java.awt.Color(227, 240, 227));
        btnDeleteImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete_Small.gif"))); // NOI18N
        btnDeleteImage.setText("Delete File");
        btnDeleteImage.setToolTipText("Delete the current image.");
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setIconTextGap(2);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout pnlFilesLayout = new javax.swing.GroupLayout(pnlFiles);
        pnlFiles.setLayout(pnlFilesLayout);
        pnlFilesLayout.setHorizontalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFilesLayout.createSequentialGroup()
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlFilesLayout.createSequentialGroup()
                            .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(jSeparator1)
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPreviousImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFilesLayout.createSequentialGroup()
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSetMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumberOfImages, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnNextImage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pnlButtons.setBackground(new java.awt.Color(227, 240, 227));
        pnlButtons.setMaximumSize(new java.awt.Dimension(120, 305));
        pnlButtons.setMinimumSize(new java.awt.Dimension(120, 305));
        pnlButtons.setName("pnlButtons"); // NOI18N
        pnlButtons.setPreferredSize(new java.awt.Dimension(120, 305));

        btnAddSighting.setBackground(new java.awt.Color(227, 240, 227));
        btnAddSighting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAddSighting.setText("Observation");
        btnAddSighting.setToolTipText("Add an Observation of this Creature.");
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setFocusPainted(false);
        btnAddSighting.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddSighting.setIconTextGap(5);
        btnAddSighting.setMargin(new java.awt.Insets(2, 8, 2, 4));
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 204, 51));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnUpdate.setToolTipText("Save and update the Creature.");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnSlideshow.setBackground(new java.awt.Color(227, 240, 227));
        btnSlideshow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Slideshow_Small.gif"))); // NOI18N
        btnSlideshow.setText("Slideshows");
        btnSlideshow.setToolTipText("View slideshow videos of linked images for this Creature.");
        btnSlideshow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSlideshow.setFocusPainted(false);
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });

        btnReport.setBackground(new java.awt.Color(227, 240, 227));
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Report_Small.png"))); // NOI18N
        btnReport.setText("Charts");
        btnReport.setToolTipText("View charts for this Creature.");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setFocusPainted(false);
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        btnMap.setBackground(new java.awt.Color(227, 240, 227));
        btnMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Map_Small.gif"))); // NOI18N
        btnMap.setText("Maps");
        btnMap.setToolTipText("Show maps for this Creature.");
        btnMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMap.setFocusPainted(false);
        btnMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMap.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });

        btnBrowse.setBackground(new java.awt.Color(227, 240, 227));
        btnBrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Browse.png"))); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.setToolTipText("Open the Browse tab and automatically select this Creature in the tree.");
        btnBrowse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBrowse.setFocusPainted(false);
        btnBrowse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrowse.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnBrowse.setName("btnBrowse"); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(227, 240, 227));
        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Export.png"))); // NOI18N
        btnExport.setText("Export");
        btnExport.setToolTipText("Show available exports for this Creature.");
        btnExport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport.setFocusPainted(false);
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExport.setMargin(new java.awt.Insets(2, 6, 2, 6));
        btnExport.setName("btnExport"); // NOI18N
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnAddSighting, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnMap, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE)
                .addComponent(btnSlideshow, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pnlNames.setBackground(new java.awt.Color(227, 240, 227));
        pnlNames.setName("pnlNames"); // NOI18N

        txtScienceName.setText(element.getScientificName());
        txtScienceName.setName("txtScienceName"); // NOI18N

        txtReferenceID.setText(element.getReferenceID());
        txtReferenceID.setName("txtReferenceID"); // NOI18N

        txtOtherName.setText(element.getOtherName());
        txtOtherName.setName("txtOtherName"); // NOI18N

        txtPrimaryName.setBackground(new java.awt.Color(204, 255, 204));
        txtPrimaryName.setText(element.getPrimaryName());
        txtPrimaryName.setName("txtPrimaryName"); // NOI18N

        jLabel2.setText("Reference ID:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel57.setText("Scientific Name:");
        jLabel57.setName("jLabel57"); // NOI18N

        jLabel55.setText("Primary Name:");
        jLabel55.setName("jLabel55"); // NOI18N

        jLabel56.setText("Other Name:");
        jLabel56.setName("jLabel56"); // NOI18N

        btnCheckIUCNOtherName.setBackground(new java.awt.Color(0, 0, 0));
        btnCheckIUCNOtherName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/IUCN.gif"))); // NOI18N
        btnCheckIUCNOtherName.setToolTipText("Try to load the main common name and threat category for this Creature from the IUCN web services.");
        btnCheckIUCNOtherName.setBorder(null);
        btnCheckIUCNOtherName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckIUCNOtherName.setFocusPainted(false);
        btnCheckIUCNOtherName.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCheckIUCNOtherName.setName("btnCheckIUCNOtherName"); // NOI18N
        btnCheckIUCNOtherName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckIUCNOtherNameActionPerformed(evt);
            }
        });

        btnINaturalist.setBackground(new java.awt.Color(255, 255, 255));
        btnINaturalist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist_white.png"))); // NOI18N
        btnINaturalist.setToolTipText("Try to view this Creature on the iNaturalist website.");
        btnINaturalist.setBorder(null);
        btnINaturalist.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnINaturalist.setFocusPainted(false);
        btnINaturalist.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnINaturalist.setName("btnINaturalist"); // NOI18N
        btnINaturalist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnINaturalistActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNamesLayout = new javax.swing.GroupLayout(pnlNames);
        pnlNames.setLayout(pnlNamesLayout);
        pnlNamesLayout.setHorizontalGroup(
            pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNamesLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlNamesLayout.createSequentialGroup()
                        .addComponent(txtScienceName)
                        .addGap(5, 5, 5)
                        .addComponent(btnCheckIUCNOtherName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(btnINaturalist, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReferenceID, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPrimaryName)
                    .addComponent(txtOtherName))
                .addGap(5, 5, 5))
        );
        pnlNamesLayout.setVerticalGroup(
            pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNamesLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrimaryName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOtherName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCheckIUCNOtherName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnINaturalist, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlNamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtScienceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtReferenceID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(5, 5, 5))
        );

        pnlInfo1.setBackground(new java.awt.Color(227, 240, 227));
        pnlInfo1.setName("pnlInfo1"); // NOI18N

        cmbFeedingClass.setModel(new DefaultComboBoxModel(FeedingClass.values()));
        cmbFeedingClass.setSelectedItem(element.getFeedingClass());
        cmbFeedingClass.setFocusable(false);
        cmbFeedingClass.setName("cmbFeedingClass"); // NOI18N

        cmbEndangeredStatus.setModel(new DefaultComboBoxModel(EndangeredStatus.values()));
        cmbEndangeredStatus.setSelectedItem(element.getEndangeredStatus());
        cmbEndangeredStatus.setFocusable(false);
        cmbEndangeredStatus.setName("cmbEndangeredStatus"); // NOI18N

        jLabel1.setText("Feeding Class:");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel70.setText("Identification:");
        jLabel70.setName("jLabel70"); // NOI18N

        jLabel62.setText("Threat Status:");
        jLabel62.setName("jLabel62"); // NOI18N

        cmbType.setMaximumRowCount(11);
        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setSelectedItem(element.getType());
        cmbType.setFocusable(false);
        cmbType.setName("cmbType"); // NOI18N

        jLabel60.setText("Creature Type:");
        jLabel60.setToolTipText("");
        jLabel60.setName("jLabel60"); // NOI18N

        jScrollPane17.setName("jScrollPane17"); // NOI18N

        txtDiagnosticDescription.setColumns(20);
        txtDiagnosticDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDiagnosticDescription.setLineWrap(true);
        txtDiagnosticDescription.setRows(5);
        txtDiagnosticDescription.setText(element.getDiagnosticDescription());
        txtDiagnosticDescription.setWrapStyleWord(true);
        txtDiagnosticDescription.setName("txtDiagnosticDescription"); // NOI18N
        jScrollPane17.setViewportView(txtDiagnosticDescription);

        javax.swing.GroupLayout pnlInfo1Layout = new javax.swing.GroupLayout(pnlInfo1);
        pnlInfo1.setLayout(pnlInfo1Layout);
        pnlInfo1Layout.setHorizontalGroup(
            pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfo1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane17)
                    .addComponent(cmbType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbFeedingClass, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbEndangeredStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        pnlInfo1Layout.setVerticalGroup(
            pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfo1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addGap(5, 5, 5)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFeedingClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbEndangeredStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(pnlInfo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane17))
                .addGap(8, 8, 8))
        );

        pnlInfo2.setBackground(new java.awt.Color(227, 240, 227));
        pnlInfo2.setName("pnlInfo2"); // NOI18N

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        txtBehaviourDescription.setColumns(20);
        txtBehaviourDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtBehaviourDescription.setLineWrap(true);
        txtBehaviourDescription.setRows(4);
        txtBehaviourDescription.setText(element.getBehaviourDescription());
        txtBehaviourDescription.setWrapStyleWord(true);
        txtBehaviourDescription.setName("txtBehaviourDescription"); // NOI18N
        jScrollPane18.setViewportView(txtBehaviourDescription);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDistribution.setColumns(20);
        txtDistribution.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDistribution.setLineWrap(true);
        txtDistribution.setRows(3);
        txtDistribution.setText(element.getDistribution());
        txtDistribution.setWrapStyleWord(true);
        txtDistribution.setName("txtDistribution"); // NOI18N
        jScrollPane2.setViewportView(txtDistribution);

        jScrollPane16.setName("jScrollPane16"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(4);
        txtDescription.setText(element.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane16.setViewportView(txtDescription);

        jLabel61.setText("Behaviour:");
        jLabel61.setName("jLabel61"); // NOI18N

        jLabel9.setText("Distribution:");
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel59.setText("Habitat:");
        jLabel59.setName("jLabel59"); // NOI18N

        jLabel69.setText("Food / Nutrition:");
        jLabel69.setName("jLabel69"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtNutrition.setColumns(20);
        txtNutrition.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtNutrition.setLineWrap(true);
        txtNutrition.setRows(3);
        txtNutrition.setText(element.getNutrition());
        txtNutrition.setWrapStyleWord(true);
        txtNutrition.setName("txtNutrition"); // NOI18N
        jScrollPane1.setViewportView(txtNutrition);

        javax.swing.GroupLayout pnlInfo2Layout = new javax.swing.GroupLayout(pnlInfo2);
        pnlInfo2.setLayout(pnlInfo2Layout);
        pnlInfo2Layout.setHorizontalGroup(
            pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfo2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInfo2Layout.createSequentialGroup()
                        .addComponent(jScrollPane16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(pnlInfo2Layout.createSequentialGroup()
                        .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(pnlInfo2Layout.createSequentialGroup()
                        .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)))
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                    .addGroup(pnlInfo2Layout.createSequentialGroup()
                        .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        pnlInfo2Layout.setVerticalGroup(
            pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInfo2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel59))
                .addGap(5, 5, 5)
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane16)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jLabel61))
                .addGap(5, 5, 5)
                .addGroup(pnlInfo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jScrollPane18))
                .addGap(0, 0, 0))
        );

        pnlTables.setBackground(new java.awt.Color(227, 240, 227));
        pnlTables.setName("pnlTables"); // NOI18N

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionBackground(new java.awt.Color(67, 97, 113));
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLocationMouseClicked(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblLocationKeyPressed(evt);
            }
        });
        jScrollPane15.setViewportView(tblLocation);

        lblLocationsOrElements.setText("Places/Observations:");
        lblLocationsOrElements.setName("lblLocationsOrElements"); // NOI18N

        rdbLocations.setBackground(new java.awt.Color(227, 240, 227));
        buttonGroup1.add(rdbLocations);
        rdbLocations.setSelected(true);
        rdbLocations.setText("Place");
        rdbLocations.setToolTipText("View all Places where this Creature has been observed.");
        rdbLocations.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocations.setFocusPainted(false);
        rdbLocations.setName("rdbLocations"); // NOI18N

        btnGoLocation.setBackground(new java.awt.Color(227, 240, 227));
        btnGoLocation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Go.gif"))); // NOI18N
        btnGoLocation.setToolTipText("Open a tab for the selected Place or view the selected Observation.");
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setFocusPainted(false);
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });

        lblNumberOfLocations.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNumberOfLocations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfLocations.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N

        rdbSightings.setBackground(new java.awt.Color(227, 240, 227));
        buttonGroup1.add(rdbSightings);
        rdbSightings.setText("Observations");
        rdbSightings.setToolTipText("View all Observations of this Creature.");
        rdbSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSightings.setFocusPainted(false);
        rdbSightings.setName("rdbSightings"); // NOI18N
        rdbSightings.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbSightingsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlTablesLayout = new javax.swing.GroupLayout(pnlTables);
        pnlTables.setLayout(pnlTablesLayout);
        pnlTablesLayout.setHorizontalGroup(
            pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablesLayout.createSequentialGroup()
                .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTablesLayout.createSequentialGroup()
                        .addComponent(lblNumberOfLocations, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(rdbLocations)
                        .addGap(5, 5, 5)
                        .addComponent(rdbSightings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblLocationsOrElements)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTablesLayout.setVerticalGroup(
            pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablesLayout.createSequentialGroup()
                .addComponent(lblLocationsOrElements)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblNumberOfLocations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGoLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdbLocations, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rdbSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout elementIncludesLayout = new javax.swing.GroupLayout(elementIncludes);
        elementIncludes.setLayout(elementIncludesLayout);
        elementIncludesLayout.setHorizontalGroup(
            elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(elementIncludesLayout.createSequentialGroup()
                .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(elementIncludesLayout.createSequentialGroup()
                        .addComponent(lblElementName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, elementIncludesLayout.createSequentialGroup()
                        .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlInfo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlNames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator10, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlInfo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(elementIncludesLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(pnlTables, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
        elementIncludesLayout.setVerticalGroup(
            elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, elementIncludesLayout.createSequentialGroup()
                .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(elementIncludesLayout.createSequentialGroup()
                        .addComponent(pnlFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(pnlTables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(elementIncludesLayout.createSequentialGroup()
                        .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblElementName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNumberOfSightings, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)
                        .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(elementIncludesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(elementIncludesLayout.createSequentialGroup()
                                .addComponent(pnlNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlInfo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(elementIncludesLayout.createSequentialGroup()
                                .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, Short.MAX_VALUE)))
                        .addGap(0, 0, 0)
                        .addComponent(pnlInfo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        add(elementIncludes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        WildLogApp.LOGGER.log(Level.INFO, "[PanelElement-Save]");
        if (UtilsData.checkCharacters(txtPrimaryName.getText().trim())) {
            if (txtPrimaryName.getText().length() > 0) {
                String oldName = lastSavedElement.getPrimaryName();
                populateElementFromUI();
                // Save the element
                boolean result;
                if (oldName == null || oldName.isEmpty()) {
                    result = app.getDBI().createElement(element, false);
                }
                else {
                    result = app.getDBI().updateElement(element, oldName, false);
                }
                if (result == true) {
                    txtPrimaryName.setBackground(new java.awt.Color(204, 255, 204));
                    txtPrimaryName.setText(element.getPrimaryName());
                    lastSavedElement = element.cloneShallow();
                    if (app.getWildLogOptions().isEnableSounds()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
                else {
                    txtPrimaryName.setBackground(Color.RED);
                    element.setPrimaryName(oldName);
                    txtPrimaryName.setText(oldName);
                    WLOptionPane.showMessageDialog(this, 
                            "The Creature could not be saved.", 
                            "Not Saved!", JOptionPane.ERROR_MESSAGE);
                }

                lblElementName.setText(element.getPrimaryName());
                tabTitle = element.getPrimaryName();
                tabID = element.getID();
                if (!isPopup) {
                    setupTabHeader(PanelCanSetupHeader.TabTypes.ELEMENT);
                }
                else {
                    Component component = getParent();
                    while (!(component instanceof JDialog)) {
                        component = component.getParent();
                    }
                    ((JDialog)component).dispose();
                    panelToRefresh.doTheRefresh(this);
                }
            }
            else {
                txtPrimaryName.setBackground(Color.RED);
                WLOptionPane.showMessageDialog(this, 
                        "Please provide a Primary Name before trying to save.", 
                        "Not Saved!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            txtPrimaryName.setBackground(Color.RED);
            WLOptionPane.showMessageDialog(this, 
                    "The Primary Name contains unsupported characters and could not be saved.", 
                    "Not Saved!", JOptionPane.ERROR_MESSAGE);
        }

}//GEN-LAST:event_btnUpdateActionPerformed

    private void populateElementFromUI() {
        element.setPrimaryName(UtilsData.limitLength(txtPrimaryName.getText(), 100)); // Used for indexing (ID)
        element.setOtherName(txtOtherName.getText());
        element.setScientificName(txtScienceName.getText());
        element.setReferenceID(txtReferenceID.getText());
        element.setDescription(txtDescription.getText());
        element.setDistribution(txtDistribution.getText());
        element.setNutrition(txtNutrition.getText());
        element.setDiagnosticDescription(txtDiagnosticDescription.getText());
        element.setEndangeredStatus((EndangeredStatus)cmbEndangeredStatus.getSelectedItem());
        element.setBehaviourDescription(txtBehaviourDescription.getText());
        element.setType((ElementType)cmbType.getSelectedItem());
        element.setFeedingClass((FeedingClass)cmbFeedingClass.getSelectedItem());
    }

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtPrimaryName.getBackground().equals(Color.RED)) {
            List<File> files = UtilsFileProcessing.showFileUploadDialog(app, app.getMainFrame());
            uploadFiles(files);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage(element.getWildLogFileID(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        if (!isPopup) {
            if (rdbLocations.isSelected()) {
                app.getMainFrame().getGlassPane().setVisible(true);
                app.getMainFrame().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int[] selectedRows = tblLocation.getSelectedRows();
                for (int t = 0; t < selectedRows.length; t++) {
                    UtilsPanelGenerator.openPanelAsTab(app, (Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(selectedRows[t]), 3), PanelCanSetupHeader.TabTypes.LOCATION, (JTabbedPane)getParent(), null);
                }
                app.getMainFrame().getGlassPane().setCursor(Cursor.getDefaultCursor());
                app.getMainFrame().getGlassPane().setVisible(false);
            }
            else {
                if (tblLocation.getSelectedRowCount() == 1) {
                    // Vir sightings moet ek die model gebruik om by die ID uit te kom want die column is remove van die view
                    Sighting sighting = app.getDBI().findSighting((Long) tblLocation.getModel().getValueAt(tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow()), 3), true, Sighting.class);
                    PanelSighting dialog = new PanelSighting(
                            app, app.getMainFrame(), "Edit an Existing Observation", sighting, 
                            app.getDBI().findLocation(sighting.getLocationID(), null, Location.class), 
                            app.getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class), 
                            element, this, false, false, false, false);
                    dialog.setVisible(true);
                }
                else {
                    WLOptionPane.showMessageDialog(app.getMainFrame(),
                            "Please choose one Observation to view.",
                            "Select Observation To View", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if (element.getPrimaryName() != null) {
            lblNumberOfSightings.setText(Integer.toString(app.getDBI().countSightings(0, element.getID(), 0, 0)));
        }
        else {
            lblNumberOfSightings.setText("0");
            lblNumberOfLocations.setText("0");
        }
        int fotoCount = app.getDBI().countWildLogFiles(0, element.getWildLogFileID());
        if (fotoCount > 0) {
            UtilsImageProcessing.setupFoto(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL));
        }
        setupNumberOfImages();
        if (rdbLocations.isSelected()) {
            UtilsTableGenerator.setupLocationsTableMedium(app, tblLocation, element.getID());
        }
        else {
            UtilsTableGenerator.setupSightingsTableSmall(app, tblLocation, element.getID());
        }
        tblLocation.setSelectionBackground(new Color(67,97,113));
        // Wait for the table to finish loading
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
            }
        });
        btnUpdate.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        if (element.getPrimaryName() != null && !element.getPrimaryName().isEmpty()) {
            MapsBaseDialog dialog = new MapsBaseDialog("WildLog Maps - " + element.getDisplayName(),
                    app.getDBI().listSightings(element.getID(), 0, 0, true, Sighting.class), 
                    element.getID());
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage(element.getWildLogFileID(), imageIndex, lblImage, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtPrimaryName.getBackground().equals(Color.RED)) {
            Sighting sighting = new Sighting();
            sighting.setElementID(element.getID());
            PanelSighting dialog = new PanelSighting(
                    app, app.getMainFrame(), "Add a New Observation",
                    sighting, null, null, element, this, true, false, false, false);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile(element.getWildLogFileID(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnGoLocationActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_tblLocationKeyPressed

    private void rdbSightingsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbSightingsItemStateChanged
        lblNumberOfLocations.setText("0");
        if (evt != null) {
            tblLocation.clearSelection();
        }
        if (rdbSightings.isSelected()) {
            lblLocationsOrElements.setText("Observations:");
            if (element.getPrimaryName() != null) {
                UtilsTableGenerator.setupSightingsTableSmall(app, tblLocation, element.getID());
                tblLocation.setSelectionBackground(new Color(125,120,93));
            }
            else {
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Observations"}, 0));
            }
        }
        else {
            lblLocationsOrElements.setText("Places:");
            UtilsTableGenerator.setupLocationsTableMedium(app, tblLocation, element.getID());
            tblLocation.setSelectionBackground(new Color(67,97,113));
        }
        // Wait for the table to finish loading
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
            }
        });
    }//GEN-LAST:event_rdbSightingsItemStateChanged

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        if (element.getPrimaryName() != null && !element.getPrimaryName().isEmpty()) {
            ExportDialog dialog = new ExportDialog(app, null, element, null, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (element.getPrimaryName() != null && !element.getPrimaryName().isEmpty()) {
            ChartsBaseDialog dialog = new ChartsBaseDialog("WildLog Charts - " + element.getPrimaryName(), 
                    app.getDBI().listSightings(element.getID(), 0, 0, true, Sighting.class));
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (element.getPrimaryName() != null && !element.getPrimaryName().isEmpty()) {
            SlideshowDialog dialog = new SlideshowDialog(app, null, null, element, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        if (element.getPrimaryName() != null && !element.getPrimaryName().isEmpty()) {
            app.getMainFrame().browseSelectedElement(element);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnCheckIUCNOtherNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckIUCNOtherNameActionPerformed
        if (txtScienceName.getText() != null && !txtScienceName.getText().trim().isEmpty()) {
            IUCNInformationDialog dialog = new IUCNInformationDialog(WildLogApp.getApplication().getMainFrame(), 
                    txtScienceName.getText().trim(), txtOtherName, cmbEndangeredStatus);
            dialog.setVisible(true);
        }
        else {
            WLOptionPane.showMessageDialog(getTopLevelAncestor(),
                    "Please provide a Scientific Name to use and then try again.",
                    "Scientific Name Required", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnCheckIUCNOtherNameActionPerformed

    private void btnINaturalistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnINaturalistActionPerformed
        if (txtScienceName.getText() != null && !txtScienceName.getText().trim().isEmpty()) {
            try {
                Desktop.getDesktop().browse(URI.create("https://www.inaturalist.org/taxa/search?q="
                        + URLEncoder.encode(txtScienceName.getText().trim(), "UTF-8").replace("+", "%20")));
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        else {
            WLOptionPane.showMessageDialog(getTopLevelAncestor(),
                    "Please provide a Scientific Name to use and then try again.",
                    "Scientific Name Required", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnINaturalistActionPerformed


    private void setupNumberOfImages() {
        int fotoCount = app.getDBI().countWildLogFiles(0, element.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotoCount);
        } 
        else {
            lblNumberOfImages.setText("0 of 0");
        }
        lblNumberOfImages.setToolTipText(lblNumberOfImages.getText());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCheckIUCNOtherName;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnINaturalist;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnSlideshow;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbEndangeredStatus;
    private javax.swing.JComboBox cmbFeedingClass;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JPanel elementIncludes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JLabel lblElementName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocationsOrElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfLocations;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JPanel pnlInfo1;
    private javax.swing.JPanel pnlInfo2;
    private javax.swing.JPanel pnlNames;
    private javax.swing.JPanel pnlTables;
    private javax.swing.JRadioButton rdbLocations;
    private javax.swing.JRadioButton rdbSightings;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextArea txtBehaviourDescription;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDiagnosticDescription;
    private javax.swing.JTextArea txtDistribution;
    private javax.swing.JTextArea txtNutrition;
    private javax.swing.JTextField txtOtherName;
    private javax.swing.JTextField txtPrimaryName;
    private javax.swing.JTextField txtReferenceID;
    private javax.swing.JTextField txtScienceName;
    // End of variables declaration//GEN-END:variables

}
