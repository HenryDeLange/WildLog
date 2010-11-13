package wildlog.ui.panel;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.kml.util.KmlUtil;
import wildlog.ui.panel.interfaces.PanelCanSetupHeader;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.report.ReportVisit;
import wildlog.utils.UtilsHTML;
import wildlog.utils.ui.UtilMapGenerator;

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
            Utils.setupFoto("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            lblNumberOfImages.setText("0 of 0");
        }
        imageSightingIndex = 0;
        //if (sighting.getFotos() != null && sighting.getFotos().size() > 0) setupFotos(0);
        tblSightings.getTableHeader().setReorderingAllowed(false);
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
                    Utils.setupFoto("ELEMENT-" + temp.getPrimaryName(), 0, lblElementImage, 150, app);
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            else {
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            imageSightingIndex = 0;
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("SIGHTING-" + sighting.getSightingCounter()));
            if (fotos.size() > 0 ) {
                Utils.setupFoto("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            }
            else {
                lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            setupNumberOfSightingImages();
        }
        else {
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
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
        btnMapVisit = new javax.swing.JButton();
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
        jButton1 = new javax.swing.JButton();

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
        visitIncludes.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 28, -1, -1));

        txtName.setBackground(resourceMap.getColor("txtName.background")); // NOI18N
        txtName.setText(visit.getName());
        txtName.setName("txtName"); // NOI18N
        visitIncludes.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 28, 510, -1));

        btnUpdate.setBackground(resourceMap.getColor("btnUpdate.background")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setText(resourceMap.getString("btnUpdate.text")); // NOI18N
        btnUpdate.setToolTipText(resourceMap.getString("btnUpdate.toolTipText")); // NOI18N
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 25, 110, 60));

        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N
        visitIncludes.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, -1, -1));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(resourceMap.getFont("txtDescription.font")); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        visitIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 115, 390, 70));

        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N
        visitIncludes.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, -1, 20));

        cmbType.setBackground(resourceMap.getColor("cmbType.background")); // NOI18N
        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setName("cmbType"); // NOI18N
        visitIncludes.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 130, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        visitIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 52, -1, 20));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        visitIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, -1, 20));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        visitIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 52, -1, 20));

        cmbGameWatchIntensity.setBackground(resourceMap.getColor("cmbGameWatchIntensity.background")); // NOI18N
        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N
        visitIncludes.add(cmbGameWatchIntensity, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 52, 130, -1));

        dtpStartDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        visitIncludes.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 52, 140, -1));

        dtpEndDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        visitIncludes.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 140, -1));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        visitIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 695, 10));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        visitIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 360, 310, 10));

        btnPreviousImage.setBackground(resourceMap.getColor("btnPreviousImage.background")); // NOI18N
        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        visitIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 219, 580, 360));

        btnUploadImage.setBackground(resourceMap.getColor("btnUploadImage.background")); // NOI18N
        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        visitIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 200, -1, -1));

        btnAddSighting.setBackground(resourceMap.getColor("btnAddSighting.background")); // NOI18N
        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 90, 30));

        btnDeleteSighting.setBackground(resourceMap.getColor("btnDeleteSighting.background")); // NOI18N
        btnDeleteSighting.setIcon(resourceMap.getIcon("btnDeleteSighting.icon")); // NOI18N
        btnDeleteSighting.setText(resourceMap.getString("btnDeleteSighting.text")); // NOI18N
        btnDeleteSighting.setToolTipText(resourceMap.getString("btnDeleteSighting.toolTipText")); // NOI18N
        btnDeleteSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 90, 30));

        btnEditSighting.setBackground(resourceMap.getColor("btnEditSighting.background")); // NOI18N
        btnEditSighting.setIcon(resourceMap.getIcon("btnEditSighting.icon")); // NOI18N
        btnEditSighting.setText(resourceMap.getString("btnEditSighting.text")); // NOI18N
        btnEditSighting.setToolTipText(resourceMap.getString("btnEditSighting.toolTipText")); // NOI18N
        btnEditSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditSighting.setName("btnEditSighting"); // NOI18N
        btnEditSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnEditSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 90, 50));

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
        visitIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 190, 30, 170));

        btnMapSighting.setBackground(resourceMap.getColor("btnMapSighting.background")); // NOI18N
        btnMapSighting.setIcon(resourceMap.getIcon("btnMapSighting.icon")); // NOI18N
        btnMapSighting.setText(resourceMap.getString("btnMapSighting.text")); // NOI18N
        btnMapSighting.setToolTipText(resourceMap.getString("btnMapSighting.toolTipText")); // NOI18N
        btnMapSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapSighting.setName("btnMapSighting"); // NOI18N
        btnMapSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMapSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 120, 110, 33));

        btnSetMainImage.setBackground(resourceMap.getColor("btnSetMainImage.background")); // NOI18N
        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        visitIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 550, 150, 30));

        btnMapVisit.setBackground(resourceMap.getColor("btnMapVisit.background")); // NOI18N
        btnMapVisit.setIcon(resourceMap.getIcon("btnMapVisit.icon")); // NOI18N
        btnMapVisit.setText(resourceMap.getString("btnMapVisit.text")); // NOI18N
        btnMapVisit.setToolTipText(resourceMap.getString("btnMapVisit.toolTipText")); // NOI18N
        btnMapVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapVisit.setName("btnMapVisit"); // NOI18N
        btnMapVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapVisitActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMapVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 85, 110, 33));

        lblNumberOfSightings.setFont(resourceMap.getFont("lblNumberOfSightings.font")); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfSightings.border.lineColor"))); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        visitIncludes.add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 195, 30, 20));

        btnPreviousImageSighting.setBackground(resourceMap.getColor("btnPreviousImageSighting.background")); // NOI18N
        btnPreviousImageSighting.setIcon(resourceMap.getIcon("btnPreviousImageSighting.icon")); // NOI18N
        btnPreviousImageSighting.setToolTipText(resourceMap.getString("btnPreviousImageSighting.toolTipText")); // NOI18N
        btnPreviousImageSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        visitIncludes.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 155, 110, 33));

        btnChecklist.setBackground(resourceMap.getColor("btnChecklist.background")); // NOI18N
        btnChecklist.setFont(resourceMap.getFont("btnChecklist.font")); // NOI18N
        btnChecklist.setIcon(resourceMap.getIcon("btnChecklist.icon")); // NOI18N
        btnChecklist.setText(resourceMap.getString("btnChecklist.text")); // NOI18N
        btnChecklist.setToolTipText(resourceMap.getString("btnChecklist.toolTipText")); // NOI18N
        btnChecklist.setMargin(new java.awt.Insets(2, 5, 2, 5));
        btnChecklist.setName("btnChecklist"); // NOI18N
        btnChecklist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChecklistActionPerformed(evt);
            }
        });
        visitIncludes.add(btnChecklist, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 155, 110, 33));

        btnHTML.setBackground(resourceMap.getColor("btnHTML.background")); // NOI18N
        btnHTML.setIcon(resourceMap.getIcon("btnHTML.icon")); // NOI18N
        btnHTML.setText(resourceMap.getString("btnHTML.text")); // NOI18N
        btnHTML.setToolTipText(resourceMap.getString("btnHTML.toolTipText")); // NOI18N
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });
        visitIncludes.add(btnHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 85, 110, 33));

        jButton1.setBackground(resourceMap.getColor("jButton1.background")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        visitIncludes.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, 110, 33));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(visitIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 1005, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visitIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (Utils.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = visit.getName();
                visit.setName(txtName.getText().trim());
                visit.setStartDate(dtpStartDate.getDate());
                visit.setEndDate(dtpEndDate.getDate());
                visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
                visit.setType((VisitType)cmbType.getSelectedItem());
                visit.setDescription(txtDescription.getText());
                visit.setLocationName(locationForVisit.getName());

//                boolean canSave = false;
//                if (locationForVisit.getVisits() == null)
//                    locationForVisit.setVisits(new ArrayList<Visit>());
//                Visit tempVisit = app.getDBI().find(new Visit(visit.getName()));
//                if (tempVisit == null) {
//                    int index = locationForVisit.getVisits().indexOf(visit);
//                    if (index != -1) locationForVisit.getVisits().set(index, visit);
//                    else locationForVisit.getVisits().add(visit);
//                    canSave = true;
//                }
//                else {
//                    if (tempVisit.equals(visit) && app.getDBI().list(new Visit(visit.getName())).size() == 1) {
//                        int index = locationForVisit.getVisits().indexOf(visit);
//                        if (index != -1) locationForVisit.getVisits().set(index, visit);
//                        else locationForVisit.getVisits().add(visit);
//                        canSave = true;
//                    }
//                    else {
//                        txtName.setBackground(Color.RED);
//                        visit.setName(oldName);
//                        txtName.setText(txtName.getText() + "_not_unique");
//                    }
//                }
//
//                // Save the visit
//                if (canSave) {
                    if (app.getDBI().createOrUpdate(visit, oldName) == true) {
                        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
                        txtName.setBackground(resourceMap.getColor("txtName.background"));
                    }
                    else {
                        txtName.setBackground(Color.RED);
                        visit.setName(oldName);
                        txtName.setText(txtName.getText() + "_not_unique");
                    }
//                }

                lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");

                setupTabHeader();
            }
            else {
                txtName.setBackground(Color.RED);
            }
        }
        else {
            txtName.setText(txtName.getText() + "_unsupported_chracter");
            txtName.setBackground(Color.RED);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
        lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
        sighting = null;
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
            final JDialog dialog = new JDialog(app.getMainFrame(), "Add a New Sighting", true);
            dialog.setLayout(new AbsoluteLayout());
            dialog.setSize(965, 625);
            dialog.add(new PanelSighting(sighting, locationForVisit, visit, null, this, true, false), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            dialog.setLocationRelativeTo(this);
            ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
            dialog.setIconImage(icon.getImage());
            ActionListener escListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshTableForSightings();
                    dialog.dispose();
                }
            };
            dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void btnEditSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSightingActionPerformed
        if (sighting != null) {
            tblSightings.clearSelection();
            final JDialog dialog = new JDialog(app.getMainFrame(), "Edit an Existing Sighting", true);
            dialog.setLayout(new AbsoluteLayout());
            dialog.setSize(965, 625);
            dialog.add(new PanelSighting(sighting, locationForVisit, visit, app.getDBI().find(new Element(sighting.getElementName())), this, false, false), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            dialog.setLocationRelativeTo(this);
            ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
            dialog.setIconImage(icon.getImage());
            ActionListener escListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshTableForSightings();
                    dialog.dispose();
                }
            };
            dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            dialog.setVisible(true);
            // Reset Sighting on this panel
            sighting = null;
            refreshSightingInfo();
        }
}//GEN-LAST:event_btnEditSightingActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            imageIndex = Utils.uploadImage("VISIT-" + visit.getName(), "Visits"+File.separatorChar+locationForVisit.getName()+File.separatorChar+visit.getName(), this, lblImage, 300, app);
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = Utils.previousImage("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
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
        imageIndex = Utils.nextImage("VISIT-" + visit.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        // Clear old points
        UtilMapGenerator.clearMap(app);

        // Load points
        if (sighting != null) {
            if (sighting.getLatitude() != null && sighting.getLongitude() != null)
            if (!sighting.getLatitude().equals(Latitudes.NONE) && !sighting.getLongitude().equals(Longitudes.NONE)) {
                float lat = sighting.getLatDegrees();
                lat = lat + sighting.getLatMinutes()/60f;
                lat = lat + (sighting.getLatSecondsFloat()/60f)/60f;
                if (sighting.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sighting.getLonDegrees();
                lon = lon + sighting.getLonMinutes()/60f;
                lon = lon + (sighting.getLonSecondsFloat()/60f)/60f;
                if (sighting.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sighting, app);
            }
        }

        // Open Map
        if (app.isUseOnlineMap()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + visit.getName() + " (Sightings)");
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + visit.getName() + " (Sightings)");
            app.getMapOffline().showMap();
        }
}//GEN-LAST:event_btnMapSightingActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = Utils.removeImage("VISIT-" + visit.getName(), imageIndex, lblImage, app.getDBI(), app.getClass().getResource("resources/images/NoImage.gif"), 300, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = Utils.setMainImage("VISIT-" + visit.getName(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (sighting != null) {
            PanelElement tempPanel = UtilPanelGenerator.getElementPanel(sighting.getElementName());
            UtilPanelGenerator.addPanelAsTab(tempPanel, (JTabbedPane)getParent());
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnMapVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapVisitActionPerformed
        // Clear old points
        UtilMapGenerator.clearMap(app);

        // Load points
        Sighting tempSighting = new Sighting();
        tempSighting.setVisitName(visit.getName());
        List<Sighting> sightings = app.getDBI().list(tempSighting);
        for (int t = 0; t < sightings.size(); t++) {
            if (sightings.get(t).getLatitude() != null && sightings.get(t).getLongitude() != null)
            if (!sightings.get(t).getLatitude().equals(Latitudes.NONE) && !sightings.get(t).getLongitude().equals(Longitudes.NONE)) {
                float lat = sightings.get(t).getLatDegrees();
                lat = lat + sightings.get(t).getLatMinutes()/60f;
                lat = lat + (sightings.get(t).getLatSecondsFloat()/60f)/60f;
                if (sightings.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sightings.get(t).getLonDegrees();
                lon = lon + sightings.get(t).getLonMinutes()/60f;
                lon = lon + (sightings.get(t).getLonSecondsFloat()/60f)/60f;
                if (sightings.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                UtilMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), sightings.get(t), app);
            }
        }

        // Open Map
        if (app.isUseOnlineMap()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + visit.getName());
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + visit.getName());
            app.getMapOffline().showMap();
        }
}//GEN-LAST:event_btnMapVisitActionPerformed

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = Utils.previousImage("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
        if (sighting != null) {
            imageSightingIndex = Utils.nextImage("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, lblSightingImage, 150, app);
            setupNumberOfSightingImages();
        }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (System.getProperty("os.name").equals("Windows XP")) {
            Utils.openFile("VISIT-" + visit.getName(), imageIndex, app);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (sighting != null) {
            if (sighting.getElementName() != null) {
                Utils.openFile("ELEMENT-" + app.getDBI().find(new Element(sighting.getElementName())).getPrimaryName(), 0, app);
            }
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void lblSightingImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSightingImageMouseReleased
        if (sighting != null) {
            Utils.openFile("SIGHTING-" + sighting.getSightingCounter(), imageSightingIndex, app);
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
        if (visit.getName() != null) {
            if (visit.getName().length() > 0) {
                JFrame report = new ReportVisit(visit, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnChecklistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChecklistActionPerformed
        if (visit.getName() != null) {
            if (visit.getName().length() > 0) {
                final JDialog dialog = new JDialog(app.getMainFrame(), "Add New Sightings", true);
                dialog.setSize(760, 555);
                dialog.add(new PanelChecklist(locationForVisit, visit, this));
                dialog.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif")).getImage());
                ActionListener escListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        refreshTableForSightings();
                        dialog.dispose();
                    }
                };
                dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnChecklistActionPerformed

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        Utils.openFile(UtilsHTML.exportHTML(visit, app));
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // First export to HTML
        UtilsHTML.exportHTML(visit, app);
        // Nou doen die KML deel
        String path = File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML";
        File tempFile = new File(path);
        tempFile.mkdirs();
        // Make sure icons exist in the KML folder
        KmlUtil.copyKmlIcons(app, path);
        // KML Stuff
        KmlGenerator kmlgen = new KmlGenerator();
        String finalPath = path + File.separatorChar + "WildLogMarkers - Visit (" + visit.getName() + ").kml";
        kmlgen.setKmlPath(finalPath);
        // Get entries for Sightings and Locations
        Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
        // Sightings
        Sighting tempSighting = new Sighting();
        tempSighting.setVisitName(visit.getName());
        List<Sighting> listSightings = app.getDBI().list(tempSighting);
        for (int t = 0; t < listSightings.size(); t++) {
            String key = listSightings.get(t).getElementName();
            if (!entries.containsKey(key)) {
                entries.put(key, new ArrayList<KmlEntry>());
             }
            entries.get(key).add(listSightings.get(t).toKML(t, app));
        }
        // Location
        String key = visit.getLocationName();
        if (!entries.containsKey(key)) {
            entries.put(key, new ArrayList<KmlEntry>());
         }
        entries.get(key).add(app.getDBI().find(new Location(visit.getLocationName())).toKML(listSightings.size()+1, app));
        // Generate KML
        kmlgen.generateFile(entries, KmlUtil.getKmlStyles());
        // Try to open the Kml file
        Utils.openFile(finalPath);
    }//GEN-LAST:event_jButton1ActionPerformed


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

    private Date parseDate(String inDate) {
        Date date = new Date(inDate);
        return date;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnChecklist;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnHTML;
    private javax.swing.JButton btnMapSighting;
    private javax.swing.JButton btnMapVisit;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnNextImageSighting;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnPreviousImageSighting;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbGameWatchIntensity;
    private javax.swing.JComboBox cmbType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JButton jButton1;
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
