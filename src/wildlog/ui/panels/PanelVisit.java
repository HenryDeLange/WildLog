package wildlog.ui.panels;

import wildlog.ui.dialogs.ChecklistDialog;
import wildlog.ui.dialogs.MappingDialog;
import wildlog.ui.dialogs.ReportingDialog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.ui.helpers.UtilPanelGenerator;
import wildlog.ui.helpers.UtilTableGenerator;
import wildlog.utils.UtilsFileProcessing;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.utils.UtilsData;
import wildlog.data.enums.WildLogFileType;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.helpers.FileDrop;
import wildlog.utils.WildLogPaths;
import wildlog.html.utils.UtilsHTML;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.movies.utils.UtilsMovies;
import wildlog.ui.dialogs.SlideshowDialog;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;

/**
 *
 * @author  henry.delange
 */
public class PanelVisit extends PanelCanSetupHeader implements PanelNeedsRefreshWhenSightingAdded {
    private Visit visit;
    private Location locationForVisit;
    private Sighting sighting;
    private int imageSightingIndex;

    /** Creates new form PanelVisit */
    public PanelVisit(Location inLocation, Visit inVisit) {
        app = (WildLogApp) Application.getInstance();
        locationForVisit = inLocation;
        visit = inVisit;
        sighting = new Sighting();
        //sighting.setLocation(locationForVisit);
        initComponents();
        imageIndex = 0;
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("VISIT-" + visit.getName()));
        if (fotos.size() > 0) {
            UtilsImageProcessing.setupFoto("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        }
        else {
            lblImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(300));
            lblNumberOfImages.setText("0 of 0");
        }
        imageSightingIndex = 0;
        //if (sighting.getFotos() != null && sighting.getFotos().size() > 0) setupFotos(0);
        // Setup the table
        tblSightings.getTableHeader().setReorderingAllowed(false);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblSightings);

        // setup the file dropping
        FileDrop.SetupFileDrop(lblImage, false, new FileDrop.Listener() {
            @Override
            public void filesDropped(List<File> inFiles) {
                btnUpdateActionPerformed(null);
                if (!txtName.getBackground().equals(Color.RED)) {
                    imageIndex = UtilsFileProcessing.uploadImage("VISIT-" + visit.getName(), "Visits"+File.separatorChar+locationForVisit.getName()+File.separatorChar+visit.getName(), null, lblImage, 300, app, inFiles);
                    setupNumberOfImages();
                    // everything went well - saving
                    btnUpdateActionPerformed(null);
                }
            }
        });

        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtName);
        UtilsUI.attachClipboardPopup(txtDescription);
    }

    public void setVisit(Visit inVisit) {
        visit = inVisit;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setLocationForVisit(Location inLocation) {
        locationForVisit = inLocation;
    }

    public Location getLocationForVisit() {
        return locationForVisit;
    }

    @Override
    public boolean equals(Object inObject) {
        if (getClass() != inObject.getClass()) return false;
        final PanelVisit inPanel = (PanelVisit) inObject;
        if (visit == null && inPanel.getVisit() == null) return true;
        if (visit.getName() == null && inPanel.getVisit().getName() == null) return true;
        if (visit == null) return false;
        if (locationForVisit == null) return true;
        if (visit.getName() == null) return false;
        if (locationForVisit.getName() == null) return true;
        if (!visit.getName().equalsIgnoreCase(inPanel.getVisit().getName()) ||
            !locationForVisit.getName().equalsIgnoreCase(inPanel.getLocationForVisit().getName()))
                return false;
        return true;
    }

    @Override
    public void setupTabHeader() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif"))));
        if (visit.getName() != null) tabHeader.add(new JLabel(visit.getName() + " "));
        else tabHeader.add(new JLabel("[new] "));
        JButton btnClose = new JButton();
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(12, 12));
        btnClose.setBackground(new Color(255, 000, 000));
        btnClose.setToolTipText("Close");
        btnClose.setIcon(new ImageIcon(app.getClass().getResource("resources/icons/Close.gif")));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeTab();
            }
        });
        tabHeader.add(btnClose);
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        ((JTabbedPane)getParent()).setTabComponentAt(((JTabbedPane)getParent()).indexOfComponent(this), tabHeader);
    }

    public void closeTab() {
        ((JTabbedPane)getParent()).remove(this);
    }

    @Override
    public void refreshTableForSightings() {
        //app.getDBI().refresh(locationForVisit);
        formComponentShown(null);
    }

    private void refreshSightingInfo() {
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                Element temp = app.getDBI().find(new Element(sighting.getElementName()));
                List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + temp.getPrimaryName()));
                if (fotos.size() > 0)
                    UtilsImageProcessing.setupFoto("ELEMENT-" + temp.getPrimaryName(), 0, lblElementImage, 150, app);
                else
                    lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            else {
                lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            imageSightingIndex = 0;
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
            if (fotos.size() > 0 ) {
                UtilsImageProcessing.setupFoto("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            }
            else {
                lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            }
            setupNumberOfSightingImages();
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
            lblNumberOfSightingImages.setText("");
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visitIncludes = new javax.swing.JPanel();
        lblVisitName = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel52 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbGameWatchIntensity = new javax.swing.JComboBox();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSightings = new javax.swing.JTable();
        btnUploadImage = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnAddSighting = new javax.swing.JButton();
        btnDeleteSighting = new javax.swing.JButton();
        btnEditSighting = new javax.swing.JButton();
        lblSightingImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        btnDeleteImage = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        btnMapSighting = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnGoElement = new javax.swing.JButton();
        lblNumberOfSightings = new javax.swing.JLabel();
        btnPreviousImageSighting = new javax.swing.JButton();
        btnNextImageSighting = new javax.swing.JButton();
        lblElementImage = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblNumberOfElements = new javax.swing.JLabel();
        lblNumberOfSightingImages = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        btnChecklist = new javax.swing.JButton();
        btnHTML = new javax.swing.JButton();
        btnSlideshow = new javax.swing.JButton();
        btnBulkImport = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        visitIncludes.setBackground(resourceMap.getColor("visitIncludes.background")); // NOI18N
        visitIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setName("visitIncludes"); // NOI18N
        visitIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblVisitName.setFont(resourceMap.getFont("lblVisitName.font")); // NOI18N
        lblVisitName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitName.setText(resourceMap.getString("lblVisitName.text")); // NOI18N
        lblVisitName.setName("lblVisitName"); // NOI18N
        visitIncludes.add(lblVisitName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 20));

        jSeparator8.setName("jSeparator8"); // NOI18N
        visitIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jSeparator9.setName("jSeparator9"); // NOI18N
        visitIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel52.setText(resourceMap.getString("jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52"); // NOI18N
        visitIncludes.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 28, -1, -1));

        txtName.setBackground(resourceMap.getColor("txtName.background")); // NOI18N
        txtName.setText(visit.getName());
        txtName.setName("txtName"); // NOI18N
        visitIncludes.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 28, 500, -1));

        btnUpdate.setBackground(resourceMap.getColor("btnUpdate.background")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setText(resourceMap.getString("btnUpdate.text")); // NOI18N
        btnUpdate.setToolTipText(resourceMap.getString("btnUpdate.toolTipText")); // NOI18N
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 25, 110, 60));

        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N
        visitIncludes.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(resourceMap.getFont("txtDescription.font")); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(4);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        visitIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 240, 75));

        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N
        visitIncludes.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, -1, 20));

        cmbType.setBackground(resourceMap.getColor("cmbType.background")); // NOI18N
        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setFocusable(false);
        cmbType.setName("cmbType"); // NOI18N
        visitIncludes.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 130, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        visitIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, -1, 20));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        visitIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 20));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        visitIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 52, -1, 20));

        cmbGameWatchIntensity.setBackground(resourceMap.getColor("cmbGameWatchIntensity.background")); // NOI18N
        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setFocusable(false);
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N
        visitIncludes.add(cmbGameWatchIntensity, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 52, 130, -1));

        dtpStartDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        visitIncludes.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 52, 140, -1));

        dtpEndDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        visitIncludes.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 140, -1));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        visitIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 690, 10));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        visitIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 360, 315, 10));

        btnPreviousImage.setBackground(resourceMap.getColor("btnPreviousImage.background")); // NOI18N
        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImage.setFocusPainted(false);
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

        btnNextImage.setBackground(resourceMap.getColor("btnNextImage.background")); // NOI18N
        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImage.setFocusPainted(false);
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblSightings.setAutoCreateRowSorter(true);
        tblSightings.setFont(resourceMap.getFont("tblSightings.font")); // NOI18N
        tblSightings.setFocusable(false);
        tblSightings.setName("tblSightings"); // NOI18N
        tblSightings.setSelectionBackground(resourceMap.getColor("tblSightings.selectionBackground")); // NOI18N
        tblSightings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSightings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSightingsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSightingsMouseReleased(evt);
            }
        });
        tblSightings.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSightingsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSightingsKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblSightings);

        visitIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 219, 570, 360));

        btnUploadImage.setBackground(resourceMap.getColor("btnUploadImage.background")); // NOI18N
        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        visitIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, -1, -1));

        btnAddSighting.setBackground(resourceMap.getColor("btnAddSighting.background")); // NOI18N
        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setFocusPainted(false);
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 300, 100, 30));

        btnDeleteSighting.setBackground(resourceMap.getColor("btnDeleteSighting.background")); // NOI18N
        btnDeleteSighting.setIcon(resourceMap.getIcon("btnDeleteSighting.icon")); // NOI18N
        btnDeleteSighting.setText(resourceMap.getString("btnDeleteSighting.text")); // NOI18N
        btnDeleteSighting.setToolTipText(resourceMap.getString("btnDeleteSighting.toolTipText")); // NOI18N
        btnDeleteSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSighting.setFocusPainted(false);
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 350, 100, 30));

        btnEditSighting.setBackground(resourceMap.getColor("btnEditSighting.background")); // NOI18N
        btnEditSighting.setIcon(resourceMap.getIcon("btnEditSighting.icon")); // NOI18N
        btnEditSighting.setText(resourceMap.getString("btnEditSighting.text")); // NOI18N
        btnEditSighting.setToolTipText(resourceMap.getString("btnEditSighting.toolTipText")); // NOI18N
        btnEditSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditSighting.setFocusPainted(false);
        btnEditSighting.setName("btnEditSighting"); // NOI18N
        btnEditSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnEditSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 220, 100, 60));

        lblSightingImage.setBackground(resourceMap.getColor("lblSightingImage.background")); // NOI18N
        lblSightingImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSightingImage.setText(resourceMap.getString("lblSightingImage.text")); // NOI18N
        lblSightingImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSightingImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSightingImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblSightingImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblSightingImage.setName("lblSightingImage"); // NOI18N
        lblSightingImage.setOpaque(true);
        lblSightingImage.setPreferredSize(new java.awt.Dimension(150, 150));
        lblSightingImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSightingImageMouseReleased(evt);
            }
        });
        visitIncludes.add(lblSightingImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 390, -1, -1));

        lblImage.setBackground(resourceMap.getColor("lblImage.background")); // NOI18N
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
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
        visitIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        visitIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

        btnDeleteImage.setBackground(resourceMap.getColor("btnDeleteImage.background")); // NOI18N
        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteImage.setFocusPainted(false);
        btnDeleteImage.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 326, 90, -1));

        jSeparator4.setForeground(resourceMap.getColor("jSeparator4.foreground")); // NOI18N
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N
        visitIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 190, 30, 170));

        btnMapSighting.setBackground(resourceMap.getColor("btnMapSighting.background")); // NOI18N
        btnMapSighting.setIcon(resourceMap.getIcon("btnMapSighting.icon")); // NOI18N
        btnMapSighting.setText(resourceMap.getString("btnMapSighting.text")); // NOI18N
        btnMapSighting.setToolTipText(resourceMap.getString("btnMapSighting.toolTipText")); // NOI18N
        btnMapSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapSighting.setFocusPainted(false);
        btnMapSighting.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMapSighting.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnMapSighting.setName("btnMapSighting"); // NOI18N
        btnMapSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMapSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 150, 110, 35));

        btnSetMainImage.setBackground(resourceMap.getColor("btnSetMainImage.background")); // NOI18N
        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setFocusPainted(false);
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 326, 90, -1));

        btnGoElement.setBackground(resourceMap.getColor("btnGoElement.background")); // NOI18N
        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setFocusPainted(false);
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        visitIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 550, 150, 30));

        lblNumberOfSightings.setFont(resourceMap.getFont("lblNumberOfSightings.font")); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfSightings.border.lineColor"))); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        visitIncludes.add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 195, 30, 20));

        btnPreviousImageSighting.setBackground(resourceMap.getColor("btnPreviousImageSighting.background")); // NOI18N
        btnPreviousImageSighting.setIcon(resourceMap.getIcon("btnPreviousImageSighting.icon")); // NOI18N
        btnPreviousImageSighting.setToolTipText(resourceMap.getString("btnPreviousImageSighting.toolTipText")); // NOI18N
        btnPreviousImageSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPreviousImageSighting.setFocusPainted(false);
        btnPreviousImageSighting.setName("btnPreviousImageSighting"); // NOI18N
        btnPreviousImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnPreviousImageSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 550, 40, 30));

        btnNextImageSighting.setBackground(resourceMap.getColor("btnNextImageSighting.background")); // NOI18N
        btnNextImageSighting.setIcon(resourceMap.getIcon("btnNextImageSighting.icon")); // NOI18N
        btnNextImageSighting.setToolTipText(resourceMap.getString("btnNextImageSighting.toolTipText")); // NOI18N
        btnNextImageSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextImageSighting.setFocusPainted(false);
        btnNextImageSighting.setName("btnNextImageSighting"); // NOI18N
        btnNextImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnNextImageSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 550, 40, 30));

        lblElementImage.setBackground(resourceMap.getColor("lblElementImage.background")); // NOI18N
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.setPreferredSize(new java.awt.Dimension(150, 150));
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        visitIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 390, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        visitIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 370, -1, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        visitIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 370, -1, -1));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        visitIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 0, 60, 20));

        lblNumberOfElements.setFont(resourceMap.getFont("lblNumberOfElements.font")); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        visitIncludes.add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 1, 30, 20));

        lblNumberOfSightingImages.setBackground(resourceMap.getColor("lblNumberOfSightingImages.background")); // NOI18N
        lblNumberOfSightingImages.setFont(resourceMap.getFont("lblNumberOfSightingImages.font")); // NOI18N
        lblNumberOfSightingImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightingImages.setText(resourceMap.getString("lblNumberOfSightingImages.text")); // NOI18N
        lblNumberOfSightingImages.setName("lblNumberOfSightingImages"); // NOI18N
        visitIncludes.add(lblNumberOfSightingImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 550, 70, 30));

        lblNumberOfImages.setBackground(resourceMap.getColor("lblNumberOfImages.background")); // NOI18N
        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        visitIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 330, 40, 20));

        btnReport.setBackground(resourceMap.getColor("btnReport.background")); // NOI18N
        btnReport.setIcon(resourceMap.getIcon("btnReport.icon")); // NOI18N
        btnReport.setText(resourceMap.getString("btnReport.text")); // NOI18N
        btnReport.setToolTipText(resourceMap.getString("btnReport.toolTipText")); // NOI18N
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setFocusPainted(false);
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setMargin(new java.awt.Insets(2, 8, 2, 4));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        visitIncludes.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 110, 110, 35));

        btnChecklist.setBackground(resourceMap.getColor("btnChecklist.background")); // NOI18N
        btnChecklist.setIcon(resourceMap.getIcon("btnChecklist.icon")); // NOI18N
        btnChecklist.setText(resourceMap.getString("btnChecklist.text")); // NOI18N
        btnChecklist.setToolTipText(resourceMap.getString("btnChecklist.toolTipText")); // NOI18N
        btnChecklist.setFocusPainted(false);
        btnChecklist.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChecklist.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnChecklist.setName("btnChecklist"); // NOI18N
        btnChecklist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecklistActionPerformed(evt);
            }
        });
        visitIncludes.add(btnChecklist, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, 110, 33));

        btnHTML.setBackground(resourceMap.getColor("btnHTML.background")); // NOI18N
        btnHTML.setIcon(resourceMap.getIcon("btnHTML.icon")); // NOI18N
        btnHTML.setText(resourceMap.getString("btnHTML.text")); // NOI18N
        btnHTML.setToolTipText(resourceMap.getString("btnHTML.toolTipText")); // NOI18N
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setFocusPainted(false);
        btnHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHTML.setIconTextGap(5);
        btnHTML.setMargin(new java.awt.Insets(2, 10, 2, 8));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });
        visitIncludes.add(btnHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, 110, 35));

        btnSlideshow.setIcon(resourceMap.getIcon("btnSlideshow.icon")); // NOI18N
        btnSlideshow.setText(resourceMap.getString("btnSlideshow.text")); // NOI18N
        btnSlideshow.setFocusPainted(false);
        btnSlideshow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSlideshow.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnSlideshow.setName("btnSlideshow"); // NOI18N
        btnSlideshow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlideshowActionPerformed(evt);
            }
        });
        visitIncludes.add(btnSlideshow, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 150, 110, 35));

        btnBulkImport.setIcon(resourceMap.getIcon("btnBulkImport.icon")); // NOI18N
        btnBulkImport.setText(resourceMap.getString("btnBulkImport.text")); // NOI18N
        btnBulkImport.setToolTipText(resourceMap.getString("btnBulkImport.toolTipText")); // NOI18N
        btnBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBulkImport.setFocusPainted(false);
        btnBulkImport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBulkImport.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnBulkImport.setName("btnBulkImport"); // NOI18N
        btnBulkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulkImportActionPerformed(evt);
            }
        });
        visitIncludes.add(btnBulkImport, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 150, 110, 35));

        add(visitIncludes);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (UtilsData.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = visit.getName();
                visit.setName(UtilsData.limitLength(txtName.getText(), 100));
                visit.setStartDate(dtpStartDate.getDate());
                visit.setEndDate(dtpEndDate.getDate());
                visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
                visit.setType((VisitType)cmbType.getSelectedItem());
                visit.setDescription(txtDescription.getText());
                visit.setLocationName(locationForVisit.getName());

                // Save the visit
                if (app.getDBI().createOrUpdate(visit, oldName) == true) {
                    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
                    txtName.setBackground(resourceMap.getColor("txtName.background"));
                    txtName.setText(visit.getName());
                }
                else {
                    txtName.setBackground(Color.RED);
                    visit.setName(oldName);
                    txtName.setText(txtName.getText() + "_not_unique");
                }

                lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");

                setupTabHeader();
            }
            else {
                txtName.setBackground(Color.RED);
            }
        }
        else {
            txtName.setText(txtName.getText() + "_unsupported_character");
            txtName.setBackground(Color.RED);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblSightingImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoImage(150));
        if (visit.getName() != null) {
            UtilTableGenerator.setupCompleteSightingTable(tblSightings, visit);
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(visit.getName());
            List<Sighting> sightings = app.getDBI().list(tempSighting);
            lblNumberOfSightings.setText(Integer.toString(sightings.size()));
            setupNumberOfSightingImages();
            List<String> allElements = new ArrayList<String>();
            for (int i = 0; i < sightings.size(); i++) {
                if (!allElements.contains(sightings.get(i).getElementName()))
                    allElements.add(sightings.get(i).getElementName());
            }
            lblNumberOfElements.setText(Integer.toString(allElements.size()));
        }
        else {
            tblSightings.setModel(new DefaultTableModel(new String[]{"No Sightings"}, 0));
            lblNumberOfSightings.setText("0");
            lblNumberOfElements.setText("0");
        }

        if (visit.getName() != null)
            lblVisitName.setText(visit.getName() + " - [" + locationForVisit.getName() + "]");
        else
            lblVisitName.setText(". . .  - [" + locationForVisit.getName() + "]");
        // Scroll the table
        if (sighting != null) {
            int select = -1;
            for (int t = 0; t < tblSightings.getModel().getRowCount(); t++) {
                if ((Long)(tblSightings.getValueAt(t, 5)) == sighting.getSightingCounter())
                {
                    select = t;
                    break;
                }
            }
            if (select >= 0) {
//                tblSightings.getSelectionModel().setSelectionInterval(select, select);
                tblSightings.scrollRectToVisible(tblSightings.getCellRect(select, 0, true));
            }
        }
        sighting = null;
        setupNumberOfSightingImages();
        refreshSightingInfo();
    }//GEN-LAST:event_formComponentShown

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
       if (tblSightings.getSelectedRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the Sighting(s)?", "Delete Sighting(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                sighting = app.getDBI().find(new Sighting((Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 5)));
                app.getDBI().delete(sighting);
                sighting = null;
                refreshSightingInfo();
                refreshTableForSightings();
            }
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            sighting = new Sighting();
            sighting.setLocationName(locationForVisit.getName());
            tblSightings.clearSelection();
            refreshSightingInfo();
            PanelSighting dialog = new PanelSighting(
                    app.getMainFrame(), "Add a New Sighting",
                    sighting, locationForVisit, visit, null, this, true, false, false);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void btnEditSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSightingActionPerformed
        if (sighting != null) {
            tblSightings.clearSelection();
            PanelSighting dialog = new PanelSighting(
                    app.getMainFrame(), "Edit an Existing Sighting",
                    sighting, locationForVisit, visit, app.getDBI().find(new Element(sighting.getElementName())), this, false, false, false);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
}//GEN-LAST:event_btnEditSightingActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            imageIndex = UtilsFileProcessing.uploadImage("VISIT-" + visit.getName(), "Visits"+File.separatorChar+locationForVisit.getName()+File.separatorChar+visit.getName(), this, lblImage, 300, app);
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = UtilsImageProcessing.previousImage("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRow() >= 0) {
            sighting = app.getDBI().find(new Sighting((Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 5)));
            refreshSightingInfo();
        }
        else {
            sighting = null;
        }
}//GEN-LAST:event_tblSightingsMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = UtilsImageProcessing.nextImage("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            MappingDialog dialog = new MappingDialog(app.getMainFrame(),
                    null, null, visit, sighting);
            dialog.setVisible(true);
        }
}//GEN-LAST:event_btnMapSightingActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = UtilsImageProcessing.removeImage("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = UtilsImageProcessing.setMainImage("VISIT-" + visit.getName(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (sighting != null) {
            PanelElement tempPanel = UtilPanelGenerator.getElementPanel(sighting.getElementName());
            UtilPanelGenerator.addPanelAsTab(tempPanel, (JTabbedPane)getParent());
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.previousImage("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = UtilsImageProcessing.nextImage("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        UtilsFileProcessing.openFile("VISIT-" + visit.getName(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                UtilsFileProcessing.openFile("ELEMENT-" + app.getDBI().find(new Element(sighting.getElementName())).getPrimaryName(), 0, app);
            }
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void lblSightingImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSightingImageMouseReleased
        if (sighting != null) {
            UtilsFileProcessing.openFile("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, app);
        }
    }//GEN-LAST:event_lblSightingImageMouseReleased

    private void tblSightingsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
    }//GEN-LAST:event_tblSightingsKeyPressed

    private void tblSightingsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSightingsKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblSightingsMouseReleased(null);
        else
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnEditSightingActionPerformed(null);
    }//GEN-LAST:event_tblSightingsKeyReleased

    private void tblSightingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseClicked
        if (evt.getClickCount() == 2) {
            btnEditSightingActionPerformed(null);
        }
    }//GEN-LAST:event_tblSightingsMouseClicked

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ReportingDialog dialog = new ReportingDialog(app.getMainFrame(), null, null, visit, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnChecklistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecklistActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            ChecklistDialog dialog = new ChecklistDialog(locationForVisit, visit, this);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnChecklistActionPerformed

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        UtilsFileProcessing.openFile(UtilsHTML.exportHTML(visit, app));
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void btnSlideshowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlideshowActionPerformed
        if (visit.getName() != null && !visit.getName().isEmpty()) {
            SlideshowDialog dialog = new SlideshowDialog(visit, null, null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnSlideshowActionPerformed

    private void btnBulkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulkImportActionPerformed
         UtilsConcurency.kickoffProgressbarTask(new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                BulkUploadPanel bulkUploadPanel = new BulkUploadPanel(this, visit.getName());
                UtilPanelGenerator.addPanelAsTab(bulkUploadPanel, (JTabbedPane)getParent());
                return null;
            }
        });
    }//GEN-LAST:event_btnBulkImportActionPerformed


    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("VISIT-" + visit.getName()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    private void setupNumberOfSightingImages() {
        if (sighting != null) {
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
            if (fotos.size() > 0)
                lblNumberOfSightingImages.setText(imageSightingIndex+1 + " of " + fotos.size());
            else
                lblNumberOfSightingImages.setText("0 of 0");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnBulkImport;
    private javax.swing.JButton btnChecklist;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnHTML;
    private javax.swing.JButton btnMapSighting;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnNextImageSighting;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnPreviousImageSighting;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnSlideshow;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbGameWatchIntensity;
    private javax.swing.JComboBox cmbType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfSightingImages;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblSightingImage;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JTable tblSightings;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JPanel visitIncludes;
    // End of variables declaration//GEN-END:variables

}