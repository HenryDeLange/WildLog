package wildlog.ui.panels.inaturalist.dialogs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.INaturalistLinkedData;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.WildLogDataType;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.inaturalist.INatAPI;
import wildlog.inaturalist.queryobjects.INaturalistAddObservation;
import wildlog.inaturalist.queryobjects.INaturalistUpdateObservation;
import wildlog.inaturalist.queryobjects.enums.INaturalistGeoprivacy;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.dialogs.CropDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.panels.inaturalist.helpers.INatProgressbarTask;
import wildlog.utils.UtilsTime;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsCompression;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;


public class INatSightingDialog extends JDialog {
    private static final JsonParser PARSER = new JsonParser();
    private final WildLogApp app = WildLogApp.getApplication();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Sighting sighting;
    private static INatProgressbarTask iNatTask = null;
    private INaturalistLinkedData linkedData = null;
    private int imageCounterWL = 0;
    private int imageCounterINat = 0;
    
    
    public INatSightingDialog(JFrame inParent, Sighting inSighting) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[INatSightingDialog]");
        sighting = inSighting;
        initComponents();
        setupUI();
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        UtilsUI.attachClipboardPopup(txtInfo, true);
    }
    
    public INatSightingDialog(JDialog inParent, Sighting inSighting) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[INatSightingDialog]");
        sighting = inSighting;
        initComponents();
        setupUI();
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
        UtilsUI.attachClipboardPopup(txtInfo, true);
    }

    private void setupUI() {
        if (sighting.getID() == 0) {
            WLOptionPane.showMessageDialog(this,
                    "<html>Please first save this WildLog Observation before uploading it to iNaturalist.</html>",
                    "Unsaved WildLog Observation", WLOptionPane.ERROR_MESSAGE);
            setVisible(false);
            dispose();
            return;
        }
        linkedData = app.getDBI().findINaturalistLinkedData(sighting.getID(), 0, INaturalistLinkedData.class);
        lblWildLogID.setText(Long.toString(sighting.getID()));
        if (linkedData != null && linkedData.getINaturalistData() != null && !linkedData.getINaturalistData().isEmpty()) {
            if (rdbSummary.isSelected()) {
                try {
                    JsonObject jsonObs = PARSER.parse(UtilsCompression.decompress(linkedData.getINaturalistData())).getAsJsonObject();
                    StringBuilder builder =new StringBuilder(256);
                    builder.append("Species Guess: ");
                    builder.append(jsonObs.get("species_guess").getAsString()).append(System.lineSeparator());
                    builder.append(System.lineSeparator());
                    builder.append("Date and Time: ");
                    builder.append(jsonObs.get("observed_on_string").getAsString()).append(System.lineSeparator());
                    builder.append(System.lineSeparator());
                    builder.append("Photos: ");
                    builder.append(jsonObs.get("observation_photos_count").getAsString()).append(System.lineSeparator());
                    builder.append("Comments: ");
                    builder.append(jsonObs.get("comments_count").getAsString()).append(System.lineSeparator());
                    builder.append("Identifications: ");
                    builder.append(jsonObs.get("identifications_count").getAsString()).append(System.lineSeparator());
                    builder.append("Geoprivacy: ");
                    if (jsonObs.get("geoprivacy") != null) {
                        builder.append(jsonObs.get("geoprivacy").getAsString()).append(System.lineSeparator());
                    }
                    else {
                        builder.append("Open (not provided)").append(System.lineSeparator());
                    }
                    txtInfo.setText(builder.toString());
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    txtInfo.setText("Could not generate the summary. Please use the 'View All Data' option instead.");
                }
            }
            else {
                txtInfo.setText(UtilsCompression.decompress(linkedData.getINaturalistData()));
            }
            txtInfo.setCaretPosition(0);
            imageCounterINat = 0;
            loadINaturalistImage();
        }
        else {
            linkedData = new INaturalistLinkedData(sighting.getID(), 0, null);
            txtInfo.setText("");
            imageCounterINat = 0;
            lblImageINat.setIcon(new ImageIcon(
                    WildLogSystemImages.NO_FILES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL).toString()));
            setupNumberOfImagesINat(0);
            
        }
        lblINaturalistID.setText(Long.toString(linkedData.getINaturalistID()));
        UtilsImageProcessing.setupFoto(sighting.getWildLogFileID(), imageCounterWL, lblImageWL, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImagesWL();
    }

    private void loadINaturalistImage() {
        try {
            getGlassPane().setVisible(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JsonElement jsonElement = PARSER.parse(UtilsCompression.decompress(linkedData.getINaturalistData()));
            JsonArray photos = jsonElement.getAsJsonObject().get("observation_photos").getAsJsonArray();
            if (photos.size() > 0) {
                if (imageCounterINat >= photos.size()) {
                    imageCounterINat = 0;
                }
                if (imageCounterINat < 0) {
                    imageCounterINat = photos.size() - 1;
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lblImageINat.setIcon(new ImageIcon(new URL(photos.get(imageCounterINat).getAsJsonObject()
                                    .get("photo").getAsJsonObject()
                                    .get("small_url").getAsString())));
                            setupNumberOfImagesINat(photos.size());
                        }
                        catch (MalformedURLException ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                });
            }
            else {
                setupNumberOfImagesINat(0);
            }
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            lblImageINat.setIcon(new ImageIcon(
                    WildLogSystemImages.BROKEN_FILES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL).toString()));
            setupNumberOfImagesINat(0);
        }
        finally {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (app.getWildLogOptions().isEnableSounds()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    getGlassPane().setCursor(Cursor.getDefaultCursor());
                    getGlassPane().setVisible(false);
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

        buttonGroupInfo = new javax.swing.ButtonGroup();
        buttonGroupGPS = new javax.swing.ButtonGroup();
        lblTitle = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnViewWebsite = new javax.swing.JButton();
        btnRemoveFromINat = new javax.swing.JButton();
        rdbGPSOpen = new javax.swing.JRadioButton();
        rdbGPSObscured = new javax.swing.JRadioButton();
        rdbGPSPrivate = new javax.swing.JRadioButton();
        btnUnlinkFromWildLog = new javax.swing.JButton();
        pnlIDs = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblWildLogID = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblINaturalistID = new javax.swing.JLabel();
        pnlData = new javax.swing.JPanel();
        scrTextArea = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextPane();
        btnDownload = new javax.swing.JButton();
        btnUploadData = new javax.swing.JButton();
        rdbSummary = new javax.swing.JRadioButton();
        rdbAllInfo = new javax.swing.JRadioButton();
        pnlImages = new javax.swing.JPanel();
        pnlWildLogImages = new javax.swing.JPanel();
        btnPreviousImageWL = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        lblImageWL = new javax.swing.JLabel();
        lblNumberOfImagesWL = new javax.swing.JLabel();
        btnNextImageWL = new javax.swing.JButton();
        btnUploadCroppedImage = new javax.swing.JButton();
        pnlINatImages = new javax.swing.JPanel();
        btnNextImageINat = new javax.swing.JButton();
        lblNumberOfImagesINat = new javax.swing.JLabel();
        btnDownloadImage = new javax.swing.JButton();
        lblImageINat = new javax.swing.JLabel();
        btnPreviousImageINat = new javax.swing.JButton();
        btnRefreshImages = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("iNaturalist Observation Details");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/iNaturalist_small.png")).getImage());
        setMaximumSize(new java.awt.Dimension(980, 800));
        setMinimumSize(new java.awt.Dimension(680, 700));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(720, 700));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist.png"))); // NOI18N
        lblTitle.setText("iNaturalist Observation Details");

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnOK.setToolTipText("Close the dialog.");
        btnOK.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnViewWebsite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist.png"))); // NOI18N
        btnViewWebsite.setText("View Website");
        btnViewWebsite.setToolTipText("View this Observation on the iNaturalist website.");
        btnViewWebsite.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewWebsite.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewWebsite.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnViewWebsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewWebsiteActionPerformed(evt);
            }
        });

        btnRemoveFromINat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnRemoveFromINat.setText("<html>Delete from iNaturalist</html>");
        btnRemoveFromINat.setToolTipText("Delete this Observation from iNaturalist.");
        btnRemoveFromINat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoveFromINat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveFromINat.setMargin(new java.awt.Insets(2, 6, 2, 2));
        btnRemoveFromINat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFromINatActionPerformed(evt);
            }
        });

        buttonGroupGPS.add(rdbGPSOpen);
        rdbGPSOpen.setSelected(true);
        rdbGPSOpen.setText("Open GPS");
        rdbGPSOpen.setToolTipText("Upload the GPS position with open access in iNaturalist.");
        rdbGPSOpen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        buttonGroupGPS.add(rdbGPSObscured);
        rdbGPSObscured.setText("Obscured GPS");
        rdbGPSObscured.setToolTipText("Upload the GPS position with obscured access in iNaturalist.");
        rdbGPSObscured.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        buttonGroupGPS.add(rdbGPSPrivate);
        rdbGPSPrivate.setText("Private GPS");
        rdbGPSPrivate.setToolTipText("Upload the GPS position with private access in iNaturalist.");
        rdbGPSPrivate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnUnlinkFromWildLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Delete.gif"))); // NOI18N
        btnUnlinkFromWildLog.setText("<html>Unlink from WildLog</html>");
        btnUnlinkFromWildLog.setToolTipText("Unlink this Observation from iNaturalist.");
        btnUnlinkFromWildLog.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUnlinkFromWildLog.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUnlinkFromWildLog.setMargin(new java.awt.Insets(2, 6, 2, 2));
        btnUnlinkFromWildLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnlinkFromWildLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnViewWebsite, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(btnOK, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(btnRemoveFromINat, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(btnUnlinkFromWildLog, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rdbGPSOpen))
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rdbGPSObscured))
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rdbGPSPrivate)))
                .addGap(0, 0, 0))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnViewWebsite, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnUnlinkFromWildLog, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(btnRemoveFromINat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(rdbGPSOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbGPSObscured, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbGPSPrivate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlIDs.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("WildLog ID:");

        lblWildLogID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblWildLogID.setText("Unknown...");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("iNaturalist ID:");

        lblINaturalistID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblINaturalistID.setText("Not linked...");

        javax.swing.GroupLayout pnlIDsLayout = new javax.swing.GroupLayout(pnlIDs);
        pnlIDs.setLayout(pnlIDsLayout);
        pnlIDsLayout.setHorizontalGroup(
            pnlIDsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIDsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(lblWildLogID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(10, 10, 10)
                .addComponent(lblINaturalistID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlIDsLayout.setVerticalGroup(
            pnlIDsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIDsLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlIDsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblWildLogID)
                    .addComponent(jLabel2)
                    .addComponent(lblINaturalistID))
                .addGap(3, 3, 3))
        );

        pnlData.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "iNaturalist Information:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        scrTextArea.setMaximumSize(new java.awt.Dimension(375, 400));
        scrTextArea.setMinimumSize(new java.awt.Dimension(185, 23));

        txtInfo.setEditable(false);
        txtInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        scrTextArea.setViewportView(txtInfo);

        btnDownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ShowGPS.png"))); // NOI18N
        btnDownload.setText("Download Data");
        btnDownload.setToolTipText("Download the latest data for this Observation from iNaturalist.");
        btnDownload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDownload.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDownload.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        btnUploadData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UpdateGPS.png"))); // NOI18N
        btnUploadData.setText("Upload Data");
        btnUploadData.setToolTipText("Upload this Observation's data to iNaturalist.");
        btnUploadData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUploadData.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnUploadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadDataActionPerformed(evt);
            }
        });

        buttonGroupInfo.add(rdbSummary);
        rdbSummary.setSelected(true);
        rdbSummary.setText("Show Summary");
        rdbSummary.setToolTipText("Show a summary of the iNaturalist data.");
        rdbSummary.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSummary.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbSummaryItemStateChanged(evt);
            }
        });

        buttonGroupInfo.add(rdbAllInfo);
        rdbAllInfo.setText("Show All Data");
        rdbAllInfo.setToolTipText("Show all the data recieved from iNaturalist.");
        rdbAllInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pnlDataLayout = new javax.swing.GroupLayout(pnlData);
        pnlData.setLayout(pnlDataLayout);
        pnlDataLayout.setHorizontalGroup(
            pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(scrTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUploadData, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(btnDownload, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbAllInfo)
                            .addComponent(rdbSummary))))
                .addGap(5, 5, 5))
        );
        pnlDataLayout.setVerticalGroup(
            pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDataLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlDataLayout.createSequentialGroup()
                        .addComponent(btnUploadData, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(rdbSummary)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdbAllInfo)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(3, 3, 3))
        );

        pnlWildLogImages.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "WildLog Images", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        btnPreviousImageWL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImageWL.setToolTipText("Load previous WildLog Image.");
        btnPreviousImageWL.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImageWL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageWLActionPerformed(evt);
            }
        });

        btnUploadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UpdateGPS.png"))); // NOI18N
        btnUploadImage.setText("Upload Original");
        btnUploadImage.setToolTipText("Upload this image to iNaturalist.");
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });

        lblImageWL.setBackground(new java.awt.Color(0, 0, 0));
        lblImageWL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageWL.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImageWL.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImageWL.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImageWL.setOpaque(true);
        lblImageWL.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImageWL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageWLMouseReleased(evt);
            }
        });

        lblNumberOfImagesWL.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImagesWL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImagesWL.setText("No Images");
        lblNumberOfImagesWL.setToolTipText("");

        btnNextImageWL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImageWL.setToolTipText("Load next WildLog Image.");
        btnNextImageWL.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImageWL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageWLActionPerformed(evt);
            }
        });

        btnUploadCroppedImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/UpdateGPS.png"))); // NOI18N
        btnUploadCroppedImage.setText("Upload Cropped");
        btnUploadCroppedImage.setToolTipText("Opens a popup to select a crop of this image which will then be uploaded to iNaturalist.");
        btnUploadCroppedImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadCroppedImage.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnUploadCroppedImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadCroppedImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlWildLogImagesLayout = new javax.swing.GroupLayout(pnlWildLogImages);
        pnlWildLogImages.setLayout(pnlWildLogImagesLayout);
        pnlWildLogImagesLayout.setHorizontalGroup(
            pnlWildLogImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWildLogImagesLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlWildLogImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlWildLogImagesLayout.createSequentialGroup()
                        .addComponent(btnPreviousImageWL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblNumberOfImagesWL, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnNextImageWL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImageWL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlWildLogImagesLayout.createSequentialGroup()
                        .addComponent(btnUploadImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(btnUploadCroppedImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(3, 3, 3))
        );
        pnlWildLogImagesLayout.setVerticalGroup(
            pnlWildLogImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWildLogImagesLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlWildLogImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUploadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUploadCroppedImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblImageWL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(pnlWildLogImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPreviousImageWL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfImagesWL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextImageWL, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        pnlINatImages.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "iNaturalist Images", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        btnNextImageINat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNextImageINat.setToolTipText("Load next iNaturalist Image.");
        btnNextImageINat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImageINat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageINatActionPerformed(evt);
            }
        });

        lblNumberOfImagesINat.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblNumberOfImagesINat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImagesINat.setText("Loading...");
        lblNumberOfImagesINat.setToolTipText("");

        btnDownloadImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ShowGPS.png"))); // NOI18N
        btnDownloadImage.setText("Download Image");
        btnDownloadImage.setToolTipText("Download the displayed image from iNaturalist and add it to the WildLog Observation as a Workspace file.");
        btnDownloadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDownloadImage.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnDownloadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadImageActionPerformed(evt);
            }
        });

        lblImageINat.setBackground(new java.awt.Color(0, 0, 0));
        lblImageINat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageINat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImageINat.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImageINat.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImageINat.setOpaque(true);
        lblImageINat.setPreferredSize(new java.awt.Dimension(300, 300));

        btnPreviousImageINat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPreviousImageINat.setToolTipText("Load previous iNaturalist Image.");
        btnPreviousImageINat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImageINat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageINatActionPerformed(evt);
            }
        });

        btnRefreshImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ShowGPS.png"))); // NOI18N
        btnRefreshImages.setText("Refresh Images");
        btnRefreshImages.setToolTipText("Download the latest details for this Observation from iNaturalist.");
        btnRefreshImages.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefreshImages.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnRefreshImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshImagesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlINatImagesLayout = new javax.swing.GroupLayout(pnlINatImages);
        pnlINatImages.setLayout(pnlINatImagesLayout);
        pnlINatImagesLayout.setHorizontalGroup(
            pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlINatImagesLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlINatImagesLayout.createSequentialGroup()
                        .addComponent(btnRefreshImages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(btnDownloadImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlINatImagesLayout.createSequentialGroup()
                            .addComponent(btnPreviousImageINat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(lblNumberOfImagesINat, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(btnNextImageINat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblImageINat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        pnlINatImagesLayout.setVerticalGroup(
            pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlINatImagesLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDownloadImage, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefreshImages, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblImageINat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(pnlINatImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNextImageINat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousImageINat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNumberOfImagesINat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout pnlImagesLayout = new javax.swing.GroupLayout(pnlImages);
        pnlImages.setLayout(pnlImagesLayout);
        pnlImagesLayout.setHorizontalGroup(
            pnlImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImagesLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(pnlWildLogImages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(pnlINatImages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        pnlImagesLayout.setVerticalGroup(
            pnlImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImagesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlImagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlWildLogImages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlINatImages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlImages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlIDs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(3, 3, 3)
                        .addComponent(pnlIDs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(pnlData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(pnlImages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewWebsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewWebsiteActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            try {
                Desktop.getDesktop().browse(URI.create("https://www.inaturalist.org/observations/" + linkedData.getINaturalistID()));
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnViewWebsiteActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnUploadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadDataActionPerformed
        if (!UtilsGPS.hasGPSData(sighting)) {
            WLOptionPane.showMessageDialog(this,
                    "<html>WildLog Observation without GPS coordinates cannot be uploaded to iNaturalist.</html>",
                    "Incompatible WildLog Observation", WLOptionPane.ERROR_MESSAGE);
            return;
        }
        Element element = app.getDBI().findElement(sighting.getElementID(), null, Element.class);
        if (element.getScientificName() == null || element.getScientificName().isEmpty()) {
            WLOptionPane.showMessageDialog(this,
                    "<html>WildLog Observation without a Scientific Name cannot be uploaded to iNaturalist.</html>",
                    "Incompatible WildLog Observation", WLOptionPane.ERROR_MESSAGE);
            return;
        }
        // Maak seker die Auth Token is OK
        if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
            INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
            dialog.setVisible(true);
            if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                return;
            }
        }
        try {
            getGlassPane().setVisible(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Skep die nuwe rekord om op te laai
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    long oldINaturalistID = linkedData.getINaturalistID();
                    INaturalistAddObservation iNatObservation;
                    if (oldINaturalistID == 0) {
                        iNatObservation = new INaturalistAddObservation();
                    }
                    else {
                        iNatObservation = new INaturalistUpdateObservation();
                    }
                    iNatObservation.setSpecies_guess(element.getScientificName());
// FIXME: Stel ook die timezone hier want anders default iNat dit soms na snaakse plekke... 
//        (Maar hoe kry ek die regte waardes??? Tans werk ek deur die regte default timezone te stel op die website...)
                    iNatObservation.setObserved_on_string(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).atZone(ZoneId.systemDefault()));
                    iNatObservation.setLatitude(UtilsGPS.getLatDecimalDegree(sighting));
                    iNatObservation.setLongitude(UtilsGPS.getLonDecimalDegree(sighting));
                    if (sighting.getGPSAccuracyValue() != GPSAccuracy.NONE.getMinMeters()) {
                        iNatObservation.setPositional_accuracy(sighting.getGPSAccuracyValue());
                    }
                    else {
                        if (sighting.getGPSAccuracy() != null && sighting.getGPSAccuracy().getMinMeters() != GPSAccuracy.NONE.getMinMeters()) {
                            iNatObservation.setPositional_accuracy(sighting.getGPSAccuracy().getMaxMeters());
                        }
                    }
                    if (rdbGPSOpen.isSelected()) {
                        iNatObservation.setGeoprivacy(INaturalistGeoprivacy.open);
                    }
                    else
                    if (rdbGPSObscured.isSelected()) {
                        iNatObservation.setGeoprivacy(INaturalistGeoprivacy.obscured);
                    }
                    else
                    if (rdbGPSPrivate.isSelected()) {
                        iNatObservation.setGeoprivacy(INaturalistGeoprivacy._private);
                    }
                    // Stel die "WildLog_ID" (iNaturalist Observation Field = https://www.inaturalist.org/observation_fields/7112)
                    iNatObservation.setObservation_field_values(new HashMap<>(1));
                    iNatObservation.getObservation_field_values().put("7112", Long.toString(sighting.getID()));
                    // Roep iNaturalist
                    JsonElement jsonElement;
                    if (oldINaturalistID == 0) {
                        jsonElement = INatAPI.createObservation(iNatObservation, WildLogApp.getINaturalistToken());
                    }
                    else {
                        INaturalistUpdateObservation updateObservation = (INaturalistUpdateObservation) iNatObservation;
                        updateObservation.setId(oldINaturalistID);
                        jsonElement = INatAPI.updateObservation(updateObservation, WildLogApp.getINaturalistToken());
                    }
                    // Save die inligting in WildLog
                    linkedData = new INaturalistLinkedData(sighting.getID(), 
                            jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsLong(), 
                            UtilsCompression.compress(GSON.toJson(jsonElement)));
                    if (oldINaturalistID == 0) {
                        app.getDBI().createINaturalistLinkedData(linkedData);
                    }
                    else {
                        app.getDBI().updateINaturalistLinkedData(linkedData);
                    }
                }
            });
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(this,
                    "<html>The WildLog Observation was not uploaded to iNaturalist.</html>",
                    "Upload Error", WLOptionPane.ERROR_MESSAGE);
        }
        finally {
            // Opdateer die UI en kry die volledige nuutste WildLog linked data
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (app.getWildLogOptions().isEnableSounds()) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    getGlassPane().setCursor(Cursor.getDefaultCursor());
                    getGlassPane().setVisible(false);
                    btnDownloadActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_btnUploadDataActionPerformed

    private void btnRemoveFromINatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFromINatActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            int result = WLOptionPane.showConfirmDialog(this, 
                    "Remove this Observation from iNaturalist?", 
                    "Remove from iNaturalist?", 
                    WLOptionPane.YES_NO_CANCEL_OPTION, WLOptionPane.WARNING_MESSAGE);
            if (result == WLOptionPane.YES_OPTION) {
                // Maak seker die Auth Token is OK
                if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                    INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
                    dialog.setVisible(true);
                    if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                        return;
                    }
                }
                try {
                    getGlassPane().setVisible(true);
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // Roep iNaturalist
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            INatAPI.deleteObservation(linkedData.getINaturalistID(), WildLogApp.getINaturalistToken());
                            // Verwysder die inligting in WildLog
                            app.getDBI().deleteINaturalistLinkedData(sighting.getID(), 0);
                        }
                    });
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(this,
                            "<html>The Observation was not successfully deleted from iNaturalist.</html>",
                            "Delete Error", WLOptionPane.ERROR_MESSAGE);
                }
                finally {
                    // Opdateer die UI
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (app.getWildLogOptions().isEnableSounds()) {
                                Toolkit.getDefaultToolkit().beep();
                            }
                            getGlassPane().setCursor(Cursor.getDefaultCursor());
                            getGlassPane().setVisible(false);
                            setupUI();
                        }
                    });
                }
            }
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnRemoveFromINatActionPerformed

    private void lblImageWLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageWLMouseReleased
        UtilsFileProcessing.openFile(sighting.getWildLogFileID(), imageCounterWL, app);
    }//GEN-LAST:event_lblImageWLMouseReleased

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            try {
                getGlassPane().setVisible(true);
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Roep iNaturalist
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JsonElement jsonElement = INatAPI.getObservation(linkedData.getINaturalistID());
                        // Save die inligting in WildLog
                        linkedData = new INaturalistLinkedData(sighting.getID(), 
                                jsonElement.getAsJsonObject().get("id").getAsLong(), 
                                UtilsCompression.compress(GSON.toJson(jsonElement)));
                        app.getDBI().updateINaturalistLinkedData(linkedData);
                    }
                });
            }
            catch (Exception ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                WLOptionPane.showMessageDialog(this,
                        "<html>The Observation was not downloaded from iNaturalist.</html>",
                        "Download Error", WLOptionPane.ERROR_MESSAGE);
            }
            finally {
                // Opdateer die UI
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        getGlassPane().setCursor(Cursor.getDefaultCursor());
                        getGlassPane().setVisible(false);
                        setupUI();
                    }
                });
            }
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void btnPreviousImageWLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageWLActionPerformed
        imageCounterWL = UtilsImageProcessing.previousImage(sighting.getWildLogFileID(), imageCounterWL, lblImageWL, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImagesWL();
    }//GEN-LAST:event_btnPreviousImageWLActionPerformed

    private void btnNextImageWLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageWLActionPerformed
        imageCounterWL = UtilsImageProcessing.nextImage(sighting.getWildLogFileID(), imageCounterWL, lblImageWL, WildLogThumbnailSizes.NORMAL, app);
        setupNumberOfImagesWL();
    }//GEN-LAST:event_btnNextImageWLActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            // Maak seker die Auth Token is OK
            if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
                dialog.setVisible(true);
                if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                    return;
                }
            }
            getGlassPane().setVisible(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Stuur die file na iNaturalist
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    List<WildLogFile> lstWildLogFiles = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
                    if (lstWildLogFiles != null && !lstWildLogFiles.isEmpty() && imageCounterWL < lstWildLogFiles.size() 
                            && WildLogFileType.IMAGE.equals(lstWildLogFiles.get(imageCounterWL).getFileType())) {
                        sumbitNewINatTask(linkedData.getINaturalistID(), lstWildLogFiles.get(imageCounterWL).getAbsolutePath(), WildLogApp.getINaturalistToken(), false);
                    }
                    else {
                        WLOptionPane.showMessageDialog(INatSightingDialog.this,
                                "<html>Please select an <i>image</i> file linked to this Observation to be uploaded to iNaturalist.</html>",
                                "Can't Upload", WLOptionPane.WARNING_MESSAGE);
                    }
                    // Opdateer die UI en kry die volledige nuutste WildLog linked data
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getGlassPane().setCursor(Cursor.getDefaultCursor());
                            getGlassPane().setVisible(false);
                        }
                    });
                }
            });
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnDownloadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadImageActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            try {
                getGlassPane().setVisible(true);
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Kry die foto vanaf iNaturalist
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JsonElement jsonElement = PARSER.parse(UtilsCompression.decompress(linkedData.getINaturalistData()));
                        JsonArray photos = jsonElement.getAsJsonObject().get("observation_photos").getAsJsonArray();
                        if (photos.size() > 0 && imageCounterINat < photos.size()) {
//                            String photoURL = "https://static.inaturalist.org/photos/" 
//                                    + photos.get(imageCounterINat).getAsJsonObject().get("photo").getAsJsonObject().get("id").getAsString() 
//                                    + "/original.jpg";
                            String photoURL = photos.get(imageCounterINat).getAsJsonObject().get("photo").getAsJsonObject()
                                    .get("large_url").getAsString().replace("large", "original");
                            final Path tempFile = WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath().resolve(System.currentTimeMillis() + ".jpg");
                            try {
                                UtilsFileProcessing.createFileFromStream(new BufferedInputStream(new URL(photoURL).openStream()), tempFile);
                                UtilsFileProcessing.performFileUpload(sighting, Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(sighting.toPath()), WildLogDataType.SIGHTING, 
                                        new File[] {tempFile.toFile()}, new Runnable() {
                                    @Override
                                    public void run() {
                                        // Delete die tydelikke file
                                        try {
                                            Files.delete(tempFile);
                                        }
                                        catch (IOException ex) {
                                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                        }
                                        // Laai die nuwe inligitng op die UI
                                        setupUI();
                                    }
                                }, app, false, INatSightingDialog.this, true, false);
                            }
                            catch (IOException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                WLOptionPane.showMessageDialog(INatSightingDialog.this,
                                        "<html>The photo was not downloaded from iNaturalist.</html>",
                                        "Download Error", WLOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else {
                            setupNumberOfImagesINat(0);
                        }
                    }
                });
            }
            catch (Exception ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                WLOptionPane.showMessageDialog(this,
                        "<html>The photo was not downloaded from iNaturalist.</html>",
                        "Download Error", WLOptionPane.ERROR_MESSAGE);
            }
            finally {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (app.getWildLogOptions().isEnableSounds()) {
                            Toolkit.getDefaultToolkit().beep();
                        }
                        getGlassPane().setCursor(Cursor.getDefaultCursor());
                        getGlassPane().setVisible(false);
                    }
                });
            }
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnDownloadImageActionPerformed

    private void btnPreviousImageINatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageINatActionPerformed
        imageCounterINat--;
        loadINaturalistImage();
    }//GEN-LAST:event_btnPreviousImageINatActionPerformed

    private void btnNextImageINatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageINatActionPerformed
        imageCounterINat++;
        loadINaturalistImage();
    }//GEN-LAST:event_btnNextImageINatActionPerformed

    private void rdbSummaryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbSummaryItemStateChanged
        setupUI();
    }//GEN-LAST:event_rdbSummaryItemStateChanged

    private void btnUnlinkFromWildLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnlinkFromWildLogActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            int result = WLOptionPane.showConfirmDialog(this, 
                    "Delete the this Observation's link data from the Workspace and remove the link field from iNaturalist?", 
                    "Unlink from iNaturalist?", 
                    WLOptionPane.YES_NO_CANCEL_OPTION, WLOptionPane.WARNING_MESSAGE);
            if (result == WLOptionPane.YES_OPTION) {
                // Maak seker die Auth Token is OK
                if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                    INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
                    dialog.setVisible(true);
                    if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                        return;
                    }
                }
                try {
                    getGlassPane().setVisible(true);
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // Roep iNaturalist
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            int observationFieldID = 0;
                            JsonObject iNatFullObs = INatAPI.getObservation(linkedData.getINaturalistID()).getAsJsonObject();
                            JsonElement observationFieldValues = iNatFullObs.get("observation_field_values");
                            if (observationFieldValues != null) {
                                JsonArray arrayObsFieldValues = observationFieldValues.getAsJsonArray();
                                for (int i = 0; i < arrayObsFieldValues.size(); i++) {
                                    // Soek vir "WildLog_ID" (iNaturalist Observation Field = https://www.inaturalist.org/observation_fields/7112)
                                    if (arrayObsFieldValues.get(i).getAsJsonObject().get("observation_field_id").getAsInt() == 7112) {
                                        observationFieldID = arrayObsFieldValues.get(i).getAsJsonObject().get("id").getAsInt();
                                        break;
                                    }
                                }
                            }
                            if (observationFieldID != 0) {
                                INatAPI.removeObservationFieldValue(observationFieldID, WildLogApp.getINaturalistToken());
                            }
                            // Verwysder die inligting in WildLog
                            app.getDBI().deleteINaturalistLinkedData(sighting.getID(), 0);
                        }
                    });
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(this,
                            "<html>The Observation was not successfully unlinked from iNaturalist.</html>",
                            "Unlink Error", WLOptionPane.ERROR_MESSAGE);
                }
                finally {
                    // Opdateer die UI
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (app.getWildLogOptions().isEnableSounds()) {
                                Toolkit.getDefaultToolkit().beep();
                            }
                            getGlassPane().setCursor(Cursor.getDefaultCursor());
                            getGlassPane().setVisible(false);
                            setupUI();
                        }
                    });
                }
            }
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnUnlinkFromWildLogActionPerformed

    private void btnUploadCroppedImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadCroppedImageActionPerformed
        if (linkedData.getINaturalistID() != 0) {
            // Maak seker die Auth Token is OK
            if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
                dialog.setVisible(true);
                if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
                    return;
                }
            }
            getGlassPane().setVisible(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Stuur die file na iNaturalist
            List<WildLogFile> lstWildLogFiles = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
            if (lstWildLogFiles != null && !lstWildLogFiles.isEmpty() && imageCounterWL < lstWildLogFiles.size() 
                    && WildLogFileType.IMAGE.equals(lstWildLogFiles.get(imageCounterWL).getFileType())) {
                CropDialog dialog = new CropDialog(INatSightingDialog.this, lstWildLogFiles.get(imageCounterWL));
                final Path tempFile = WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath().resolve(System.currentTimeMillis() + ".jpg");
                try {
                    Files.createDirectories(WildLogPaths.WILDLOG_TEMP.getAbsoluteFullPath());
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                dialog.setINaturalistUploadFile(tempFile);
                dialog.setVisible(true);
                sumbitNewINatTask(linkedData.getINaturalistID(), tempFile, WildLogApp.getINaturalistToken(), true);
            }
            else {
                WLOptionPane.showMessageDialog(INatSightingDialog.this,
                        "<html>Please select an <i>image</i> file linked to this Observation to be uploaded to iNaturalist.</html>",
                        "Can't Upload", WLOptionPane.WARNING_MESSAGE);
            }
            // Opdateer die UI en kry die volledige nuutste WildLog linked data
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getGlassPane().setCursor(Cursor.getDefaultCursor());
                    getGlassPane().setVisible(false);
                }
            });
        }
        else {
            showMessageForNoINatID();
        }
    }//GEN-LAST:event_btnUploadCroppedImageActionPerformed

    private void btnRefreshImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshImagesActionPerformed
        btnDownloadActionPerformed(evt);
    }//GEN-LAST:event_btnRefreshImagesActionPerformed

    public void showMessageForNoINatID() throws HeadlessException {
        WLOptionPane.showMessageDialog(this,
                "<html>The iNaturalist ID was not found. "
                        + "<br />Please first upload the Observation's data to iNaturalist.</html>",
                "No iNaturalist ID", WLOptionPane.ERROR_MESSAGE);
    }
    
    private void setupNumberOfImagesWL() {
        int fotoCount = app.getDBI().countWildLogFiles(0, sighting.getWildLogFileID());
        if (fotoCount > 0) {
            lblNumberOfImagesWL.setText(imageCounterWL + 1 + " of " + fotoCount);
        }
        else {
            lblNumberOfImagesWL.setText("0 of 0");
        }
    }
    
    private void setupNumberOfImagesINat(int inNumberOfImages) {
        if (inNumberOfImages > 0) {
            lblNumberOfImagesINat.setText(imageCounterINat + 1 + " of " + inNumberOfImages);
        }
        else {
            lblNumberOfImagesINat.setText("0 of 0");
        }
    }
    
    private void sumbitNewINatTask(long inINatID, Path inFile, String inINatToken, boolean inDeleteFile) {
        if (iNatTask == null || iNatTask.isDone()) {
            // Create a new task
            iNatTask = new INatProgressbarTask(app);
            iNatTask.submitTask(inINatID, inFile, inINatToken, inDeleteFile);
            UtilsConcurency.kickoffProgressbarTask(app, iNatTask);
        }
        else {
            // Update the existing task to add one more item to its queue
            iNatTask.submitTask(inINatID, inFile, inINatToken, inDeleteFile);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnDownloadImage;
    private javax.swing.JButton btnNextImageINat;
    private javax.swing.JButton btnNextImageWL;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnPreviousImageINat;
    private javax.swing.JButton btnPreviousImageWL;
    private javax.swing.JButton btnRefreshImages;
    private javax.swing.JButton btnRemoveFromINat;
    private javax.swing.JButton btnUnlinkFromWildLog;
    private javax.swing.JButton btnUploadCroppedImage;
    private javax.swing.JButton btnUploadData;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JButton btnViewWebsite;
    private javax.swing.ButtonGroup buttonGroupGPS;
    private javax.swing.ButtonGroup buttonGroupInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblINaturalistID;
    private javax.swing.JLabel lblImageINat;
    private javax.swing.JLabel lblImageWL;
    private javax.swing.JLabel lblNumberOfImagesINat;
    private javax.swing.JLabel lblNumberOfImagesWL;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWildLogID;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlIDs;
    private javax.swing.JPanel pnlINatImages;
    private javax.swing.JPanel pnlImages;
    private javax.swing.JPanel pnlWildLogImages;
    private javax.swing.JRadioButton rdbAllInfo;
    private javax.swing.JRadioButton rdbGPSObscured;
    private javax.swing.JRadioButton rdbGPSOpen;
    private javax.swing.JRadioButton rdbGPSPrivate;
    private javax.swing.JRadioButton rdbSummary;
    private javax.swing.JScrollPane scrTextArea;
    private javax.swing.JTextPane txtInfo;
    // End of variables declaration//GEN-END:variables
}
