/*
 * PanelVisit.java
 *
 * Created on May 19, 2008, 3:02 PM
 */

package wildlog.ui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;
import wildlog.ui.util.UtilPanelGenerator;
import wildlog.ui.util.UtilTableGenerator;
import wildlog.ui.util.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.MapFrame;
import wildlog.ui.util.ImageFilter;
import wildlog.ui.util.ImagePreview;

/**
 *
 * @author  henry.delange
 */
public class PanelVisit extends javax.swing.JPanel {
    private Visit visit;
    private Location locationForVisit;
    private Sighting sighting;
    private JTabbedPane parent;
    private UtilTableGenerator utilTableGenerator;
    private UtilPanelGenerator utilPanelGenerator;
    private Element searchElement;
    private int imageIndex;
    private MapFrame mapFrame;
    private WildLogApp app;
    
    /** Creates new form PanelVisit */
    public PanelVisit(Location inLocation, Visit inVisit) {
        app = (WildLogApp) Application.getInstance();
        locationForVisit = inLocation;
        visit = inVisit;
        sighting = new Sighting();
        sighting.setLocation(locationForVisit);
        utilTableGenerator = new UtilTableGenerator();
        utilPanelGenerator = new UtilPanelGenerator();
        searchElement = new Element();
        initComponents();
        imageIndex = 0;
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) setupFotos(0);
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
        if (visit == null) return true;
        if (locationForVisit == null) return true;
        if (visit.getName() == null) return true;
        if (locationForVisit.getName() == null) return true;
        if (!visit.getName().equalsIgnoreCase(inPanel.getVisit().getName()) ||
            !locationForVisit.getName().equalsIgnoreCase(inPanel.getLocationForVisit().getName()))
                return false;
        return true;
    }
    
    public void setupTabHeader() {
        parent = (JTabbedPane) getParent();
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
            public void actionPerformed(ActionEvent e) {
                closeTab();
            }
        });
        tabHeader.add(btnClose);
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        parent.setTabComponentAt(parent.indexOfComponent(this), tabHeader);
    }
    
    public void closeTab() {
        parent = (JTabbedPane) getParent();
        if (parent != null) parent.remove(this);
    }
    
    private void refreshSightingInfo() {
        if (sighting != null) {
            WildLogApp app = (WildLogApp) Application.getInstance();
            dtpSightingDate.setDate(sighting.getDate());
            cmbAreaType.setSelectedItem(sighting.getAreaType());
            cmbCertainty.setSelectedItem(sighting.getCertainty());
            txtDetails.setText(sighting.getDetails());
            if (!sighting.getSubArea().equals(""))
                cmbSubArea.setSelectedItem(sighting.getSubArea());
            else
                cmbSubArea.setSelectedItem("None");
            if (sighting.getElement() != null) {
                lblElement.setText(sighting.getElement().getPrimaryName());
                if (sighting.getElement().getFotos() != null)
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getElement().getFotos().get(0).getFileLocation()), 150));
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            else {
                lblElement.setText("...No Element Recorded...");
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            txtNumberOfElements.setText(Integer.toString(sighting.getNumberOfElements()));
            cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
            cmbViewRating.setSelectedItem(sighting.getViewRating());
            cmbWeather.setSelectedItem(sighting.getWeather());

            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            if (sighting.getFotos() != null)
                if (sighting.getFotos().size() > 0 )
                    setupFotos(0);

            cmbLatitude.setSelectedItem(sighting.getLatitude());
            txtLatDegrees.setText(Integer.toString(sighting.getLatDegrees()));
            txtLatMinutes.setText(Integer.toString(sighting.getLatMinutes()));
            txtLatSeconds.setText(Integer.toString(sighting.getLatSeconds()));
            cmbLonitude.setSelectedItem(sighting.getLocation());
            txtLonDegrees.setText(Integer.toString(sighting.getLonDegrees()));
            txtLonMinutes.setText(Integer.toString(sighting.getLonMinutes()));
            txtLonSeconds.setText(Integer.toString(sighting.getLonSeconds()));

        }
        else {
            System.out.println("Sighting was not found - null");
            sighting = new Sighting();
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
        lblLocationName = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        btnGoElement = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        btnUpdate = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel54 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
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
        btnUpdateSighting = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnAddSighting = new javax.swing.JButton();
        btnDeleteSighting = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        dtpSightingDate = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        cmbAreaType = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDetails = new javax.swing.JTextArea();
        txtNumberOfElements = new javax.swing.JTextField();
        cmbWeather = new javax.swing.JComboBox();
        cmbTimeOfDay = new javax.swing.JComboBox();
        cmbViewRating = new javax.swing.JComboBox();
        cmbCertainty = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        lblElement = new javax.swing.JLabel();
        lblElementImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        chkElementTypeFilter = new javax.swing.JCheckBox();
        cmbElementType = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        cmbLatitude = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbLonitude = new javax.swing.JComboBox();
        txtLatDegrees = new javax.swing.JTextField();
        txtLatMinutes = new javax.swing.JTextField();
        txtLatSeconds = new javax.swing.JTextField();
        txtLonDegrees = new javax.swing.JTextField();
        txtLonMinutes = new javax.swing.JTextField();
        txtLonSeconds = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        chkSearchDirect = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        cmbSubArea = new javax.swing.JComboBox();
        btnDeleteImage = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        btnMap = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();

        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        visitIncludes.setMinimumSize(new java.awt.Dimension(1000, 700));
        visitIncludes.setName("visitIncludes"); // NOI18N
        visitIncludes.setPreferredSize(new java.awt.Dimension(1000, 700));
        visitIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
        lblVisitName.setFont(resourceMap.getFont("lblVisitName.font")); // NOI18N
        lblVisitName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitName.setText(visit.getName());
        lblVisitName.setName("lblVisitName"); // NOI18N
        visitIncludes.add(lblVisitName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 460, 20));

        lblLocationName.setFont(resourceMap.getFont("lblLocationName.font")); // NOI18N
        lblLocationName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationName.setText(locationForVisit.getName());
        lblLocationName.setName("lblLocationName"); // NOI18N
        visitIncludes.add(lblLocationName, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 180, 20));

        jSeparator8.setName("jSeparator8"); // NOI18N
        visitIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement));
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        jScrollPane13.setViewportView(tblElement);

        visitIncludes.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 420, 300, 130));

        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        visitIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 553, 300, 30));

        jLabel51.setFont(resourceMap.getFont("jLabel51.font")); // NOI18N
        jLabel51.setText(resourceMap.getString("jLabel51.text")); // NOI18N
        jLabel51.setName("jLabel51"); // NOI18N
        visitIncludes.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 370, -1, 20));

        jSeparator9.setName("jSeparator9"); // NOI18N
        visitIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        btnUpdate.setBackground(resourceMap.getColor("btnUpdate.background")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setText(resourceMap.getString("btnUpdate.text")); // NOI18N
        btnUpdate.setToolTipText(resourceMap.getString("btnUpdate.toolTipText")); // NOI18N
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 28, 110, 60));

        jLabel52.setText(resourceMap.getString("jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52"); // NOI18N
        visitIncludes.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 28, -1, -1));

        txtName.setBackground(resourceMap.getColor("txtName.background")); // NOI18N
        txtName.setText(visit.getName());
        txtName.setName("txtName"); // NOI18N
        visitIncludes.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 28, 510, -1));

        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N
        visitIncludes.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, -1, -1));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        visitIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, 310, -1));

        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N
        visitIncludes.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 130, -1, -1));

        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setName("cmbType"); // NOI18N
        visitIncludes.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 130, 150, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        visitIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 52, -1, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        visitIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 52, -1, -1));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        visitIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, -1, 20));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        visitIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, -1, -1));

        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N
        visitIncludes.add(cmbGameWatchIntensity, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 100, 150, -1));

        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("dtpStartDate.border.lineColor"), 3, true)); // NOI18N
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        visitIncludes.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 52, 200, -1));

        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("dtpEndDate.border.lineColor"), 3, true)); // NOI18N
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        visitIncludes.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 52, 200, -1));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        visitIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 695, 10));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        visitIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 360, 280, 10));

        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
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
        tblSightings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblSightings.setName("tblSightings"); // NOI18N
        tblSightings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblSightings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSightingsMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblSightings);

        visitIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 229, 340, 350));

        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        btnUpdateSighting.setBackground(resourceMap.getColor("btnUpdateSighting.background")); // NOI18N
        btnUpdateSighting.setIcon(resourceMap.getIcon("btnUpdateSighting.icon")); // NOI18N
        btnUpdateSighting.setText(resourceMap.getString("btnUpdateSighting.text")); // NOI18N
        btnUpdateSighting.setToolTipText(resourceMap.getString("btnUpdateSighting.toolTipText")); // NOI18N
        btnUpdateSighting.setName("btnUpdateSighting"); // NOI18N
        btnUpdateSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUpdateSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, 110, 60));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        visitIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, -1, -1));

        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, 100, -1));

        btnDeleteSighting.setIcon(resourceMap.getIcon("btnDeleteSighting.icon")); // NOI18N
        btnDeleteSighting.setText(resourceMap.getString("btnDeleteSighting.text")); // NOI18N
        btnDeleteSighting.setToolTipText(resourceMap.getString("btnDeleteSighting.toolTipText")); // NOI18N
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 100, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        visitIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 254, -1, -1));

        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("dtpSightingDate.border.lineColor"), 3, true)); // NOI18N
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        visitIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 254, 180, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        visitIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 360, -1, -1));

        cmbAreaType.setMaximumRowCount(10);
        cmbAreaType.setModel(new DefaultComboBoxModel(AreaType.values()));
        cmbAreaType.setSelectedItem(sighting.getAreaType());
        cmbAreaType.setName("cmbAreaType"); // NOI18N
        visitIncludes.add(cmbAreaType, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 360, 270, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        visitIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 385, -1, -1));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        visitIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 410, -1, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        visitIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 410, -1, -1));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        visitIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 335, -1, -1));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        visitIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 310, -1, -1));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        visitIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 440, -1, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDetails.setColumns(20);
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(sighting.getDetails());
        txtDetails.setWrapStyleWord(true);
        txtDetails.setName("txtDetails"); // NOI18N
        jScrollPane2.setViewportView(txtDetails);

        visitIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 456, 180, 70));

        txtNumberOfElements.setText(String.valueOf(sighting.getNumberOfElements()));
        txtNumberOfElements.setName("txtNumberOfElements"); // NOI18N
        visitIncludes.add(txtNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 410, 50, -1));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setName("cmbWeather"); // NOI18N
        visitIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 385, 270, -1));

        cmbTimeOfDay.setMaximumRowCount(9);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N
        visitIncludes.add(cmbTimeOfDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 335, 270, -1));

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setName("cmbViewRating"); // NOI18N
        visitIncludes.add(cmbViewRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 410, 100, -1));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        visitIncludes.add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 310, 270, -1));

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        visitIncludes.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, -1, -1));

        lblElement.setFont(resourceMap.getFont("lblElement.font")); // NOI18N
        lblElement.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElement.setText(resourceMap.getString("lblElement.text")); // NOI18N
        lblElement.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true)); // NOI18N
        lblElement.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblElement.setName("lblElement"); // NOI18N
        visitIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, 210, 30));

        lblElementImage.setText(resourceMap.getString("lblElementImage.text")); // NOI18N
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setPreferredSize(new java.awt.Dimension(150, 150));
        visitIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 435, -1, -1));

        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        visitIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        visitIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

        chkElementTypeFilter.setName("chkElementTypeFilter"); // NOI18N
        chkElementTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkElementTypeFilterActionPerformed(evt);
            }
        });
        visitIncludes.add(chkElementTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 370, -1, 20));

        cmbElementType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementType.setEnabled(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        visitIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 370, 170, -1));

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        visitIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 530, -1, 20));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        visitIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 530, 66, -1));

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        visitIncludes.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(443, 530, -1, 20));

        cmbLonitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLonitude.setSelectedIndex(2);
        cmbLonitude.setName("cmbLonitude"); // NOI18N
        visitIncludes.add(cmbLonitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 530, 66, -1));

        txtLatDegrees.setText(Integer.toString(sighting.getLatDegrees()));
        txtLatDegrees.setName("txtLatDegrees"); // NOI18N
        visitIncludes.add(txtLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(346, 560, 30, -1));

        txtLatMinutes.setText(Integer.toString(sighting.getLatMinutes()));
        txtLatMinutes.setName("txtLatMinutes"); // NOI18N
        visitIncludes.add(txtLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(376, 560, 30, -1));

        txtLatSeconds.setText(Integer.toString(sighting.getLatSeconds()));
        txtLatSeconds.setName("txtLatSeconds"); // NOI18N
        visitIncludes.add(txtLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 560, 30, -1));

        txtLonDegrees.setText(Integer.toString(sighting.getLonDegrees()));
        txtLonDegrees.setName("txtLonDegrees"); // NOI18N
        visitIncludes.add(txtLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(443, 560, 30, -1));

        txtLonMinutes.setText(Integer.toString(sighting.getLonMinutes()));
        txtLonMinutes.setName("txtLonMinutes"); // NOI18N
        visitIncludes.add(txtLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(473, 560, 30, -1));

        txtLonSeconds.setText(Integer.toString(sighting.getLonSeconds()));
        txtLonSeconds.setName("txtLonSeconds"); // NOI18N
        visitIncludes.add(txtLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(503, 560, 30, -1));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setName("txtSearch"); // NOI18N
        visitIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 393, 120, -1));

        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        visitIncludes.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 393, 70, -1));

        chkSearchDirect.setSelected(true);
        chkSearchDirect.setText(resourceMap.getString("chkSearchDirect.text")); // NOI18N
        chkSearchDirect.setName("chkSearchDirect"); // NOI18N
        visitIncludes.add(chkSearchDirect, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 393, -1, -1));

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        visitIncludes.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 285, -1, -1));

        cmbSubArea.setModel(new DefaultComboBoxModel(locationForVisit.getSubAreas().toArray()));
        cmbSubArea.setSelectedItem(sighting.getSubArea());
        cmbSubArea.setName("cmbSubArea"); // NOI18N
        visitIncludes.add(cmbSubArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 285, 270, -1));

        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 326, 100, -1));

        jSeparator4.setForeground(resourceMap.getColor("jSeparator4.foreground")); // NOI18N
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N
        visitIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 20, 30, 170));

        btnMap.setText(resourceMap.getString("btnMap.text")); // NOI18N
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 70, 80));

        btnSetMainImage.setIcon(null);
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 326, 100, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visitIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visitIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 593, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (txtName.getText().length() > 0 && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            lblVisitName.setText(txtName.getText());
            visit.setName(txtName.getText());
            visit.setStartDate(dtpStartDate.getDate());
            visit.setEndDate(dtpEndDate.getDate());
            visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
            visit.setType((VisitType)cmbType.getSelectedItem());
            visit.setDescription(txtDescription.getText());

            if (locationForVisit.getVisits() != null) {
                int index = locationForVisit.getVisits().indexOf(visit);
                if (index != -1) locationForVisit.getVisits().set(index, visit);
                else locationForVisit.getVisits().add(visit);
            }
            else {
                locationForVisit.setVisits(new ArrayList<Visit>());
                locationForVisit.getVisits().add(visit);
            }

            app.getDBI().createOrUpdate(locationForVisit);

            org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
            txtName.setBackground(resourceMap.getColor("txtName.background"));
            dtpStartDate.setBorder(new LineBorder(resourceMap.getColor("dtpStartDate.border.lineColor"), 3, true));
            dtpEndDate.setBorder(new LineBorder(resourceMap.getColor("dtpEndDate.border.lineColor"), 3, true));

            setupTabHeader();
        }
        else {
            txtName.setBackground(Color.RED);
            dtpStartDate.setBorder(new LineBorder(Color.RED, 3, true));
            dtpEndDate.setBorder(new LineBorder(Color.RED, 3, true));
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        if (txtName.getText().length() > 0 && dtpStartDate.getDate() != null && dtpEndDate.getDate() != null) {
            if ((sighting.getElement() != null || tblElement.getSelectedRow() >= 0) && dtpSightingDate.getDate() != null) {
                sighting.setDate(dtpSightingDate.getDate());
                sighting.setAreaType((AreaType)cmbAreaType.getSelectedItem());
                sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
                sighting.setDetails(txtDetails.getText());
                try {
                    sighting.setNumberOfElements(Integer.parseInt(txtNumberOfElements.getText()));
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
                sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
                sighting.setWeather((Weather)cmbWeather.getSelectedItem());
                sighting.setLocation(locationForVisit);
                sighting.setSubArea((String)cmbSubArea.getSelectedItem());
                if (tblElement.getSelectedRowCount() > 0)
                    sighting.setElement(app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(),0))));

                if (visit.getSightings() != null) {
                    int index = visit.getSightings().indexOf(sighting);
                    if (index != -1) visit.getSightings().set(index, sighting);
                    else visit.getSightings().add(sighting);
                }
                else {
                    visit.setSightings(new ArrayList<Sighting>());
                    visit.getSightings().add(sighting);
                }

                sighting.setLatitude((Latitudes)cmbLatitude.getSelectedItem());
                sighting.setLongitude((Longitudes)cmbLonitude.getSelectedItem());
                try {
                    sighting.setLatDegrees(Integer.parseInt(txtLatDegrees.getText()));
                    sighting.setLatMinutes(Integer.parseInt(txtLatMinutes.getText()));
                    sighting.setLatSeconds(Integer.parseInt(txtLatSeconds.getText()));
                    sighting.setLonDegrees(Integer.parseInt(txtLonDegrees.getText()));
                    sighting.setLonMinutes(Integer.parseInt(txtLonMinutes.getText()));
                    sighting.setLonSeconds(Integer.parseInt(txtLonSeconds.getText()));
                }
                catch (NumberFormatException e) {
                    System.out.println("Not a Number...");
                    txtLatDegrees.setText("0");
                    txtLatMinutes.setText("0");
                    txtLatSeconds.setText("0");
                    txtLonDegrees.setText("0");
                    txtLonMinutes.setText("0");
                    txtLonSeconds.setText("0");
                }

                // Use the Visits update method since both visit and sighitng nedds to be saved
                btnUpdateActionPerformed(evt);

                tblSightings.setModel(utilTableGenerator.getCompleteSightingTable(visit));

                org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
                txtName.setBackground(resourceMap.getColor("txtName.background"));
                dtpStartDate.setBorder(new LineBorder(resourceMap.getColor("dtpStartDate.border.lineColor"), 3, true));
                dtpEndDate.setBorder(new LineBorder(resourceMap.getColor("dtpEndDate.border.lineColor"), 3, true));
                lblElement.setBorder(new LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true));
                dtpSightingDate.setBorder(new LineBorder(resourceMap.getColor("dtpSightingDate.border.lineColor"), 3, true));

                refreshSightingInfo();
                setupTabHeader();
            }
            else {
                lblElement.setBorder(new LineBorder(Color.RED, 3, true));
                dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
            }
        }
        else {
            txtName.setBackground(Color.RED);
            dtpStartDate.setBorder(new LineBorder(Color.RED, 3, true));
            dtpEndDate.setBorder(new LineBorder(Color.RED, 3, true));
        }
}//GEN-LAST:event_btnUpdateSightingActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tblSightings.setModel(utilTableGenerator.getCompleteSightingTable(visit));
        tblElement.setModel(utilTableGenerator.getShortElementTable(new Element()));
        cmbSubArea.setModel(new DefaultComboBoxModel(locationForVisit.getSubAreas().toArray()));
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
        sighting = new Sighting();
    }//GEN-LAST:event_formComponentShown

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
        if (tblSightings.getSelectedRow() >= 0) {
            sighting = app.getDBI().find(new Sighting((Date)tblSightings.getValueAt(tblSightings.getSelectedRow(), 3), app.getDBI().find(new Element((String)tblSightings.getValueAt(tblSightings.getSelectedRow(), 1))) ,locationForVisit));
            visit.getSightings().remove(sighting);
            app.getDBI().delete(sighting);
            app.getDBI().createOrUpdate(visit);
            tblSightings.setModel(utilTableGenerator.getCompleteSightingTable(visit));
            sighting = new Sighting();
            refreshSightingInfo();
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        sighting = new Sighting();
        tblSightings.clearSelection();
        refreshSightingInfo();
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void chkElementTypeFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkElementTypeFilterActionPerformed
        searchElement = new Element();
        if (!cmbElementType.isEnabled())
            searchElement.setType((ElementType)cmbElementType.getSelectedItem());
        tblElement.setModel(utilTableGenerator.getCompleteElementTable(searchElement));
        cmbElementType.setEnabled(!cmbElementType.isEnabled());
        txtSearch.setText("");
}//GEN-LAST:event_chkElementTypeFilterActionPerformed

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        searchElement = new Element((ElementType)cmbElementType.getSelectedItem());
        tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement));
        txtSearch.setText("");
}//GEN-LAST:event_cmbElementTypeActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        int selectedRow = tblElement.getSelectedRow();
        if (selectedRow >= 0) {
            PanelElement tempPanel = utilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRow, 0));
            parent = (JTabbedPane) getParent();
            if (parent != null) {
                parent.add(tempPanel);
                parent.setSelectedComponent(tempPanel);
            }
            tempPanel.setupTabHeader();
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        int row = tblSightings.getSelectedRow();
        if (row >= 0) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new ImageFilter());
            fileChooser.setAccessory(new ImagePreview(fileChooser));
            int result = fileChooser.showOpenDialog(this);
            if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
                File fromFile = fileChooser.getSelectedFile();
                File toFile = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar + fromFile.getName());
                    FileInputStream fileInput = null;
                    FileOutputStream fileOutput = null;
                    try {
                        fileInput = new FileInputStream(fromFile);
                        fileOutput = new FileOutputStream(toFile);
                        byte[] tempBytes = new byte[(int)fromFile.length()];
                        fileInput.read(tempBytes);
                        fileOutput.write(tempBytes);
                        fileOutput.flush();
                        if (sighting.getFotos() == null) sighting.setFotos(new ArrayList<Foto>());
                        sighting.getFotos().add(new Foto(toFile.getName()));
                        setupFotos(sighting.getFotos().size() - 1);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        try {
                            fileInput.close();
                            fileOutput.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
            }
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            if (imageIndex > 0) setupFotos(--imageIndex);
            else {
                imageIndex = sighting.getFotos().size() - 1;
                setupFotos(imageIndex);
            }
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (sighting != null) {
            if (tblElement.getSelectedRowCount() == 1) {
                Element tempElement = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                sighting.setElement(tempElement);
                lblElement.setText(tempElement.getPrimaryName());
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
                if (tempElement.getFotos() != null)
                    if (tempElement.getFotos().size() > 0)
                        lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(tempElement.getFotos().get(0).getFileLocation()), 150));
            }
            else {
                sighting.setElement(null);
                lblElement.setText(null);
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        tblElement.clearSelection();
        if (tblSightings.getSelectedRowCount() == 1) {
            WildLogApp app = (WildLogApp) Application.getInstance();
            sighting = app.getDBI().find(new Sighting((Date)tblSightings.getValueAt(tblSightings.getSelectedRow(), 2), app.getDBI().find(new Element((String)tblSightings.getValueAt(tblSightings.getSelectedRow(), 0))) ,locationForVisit));
            refreshSightingInfo();
        }
        else {
            sighting = null;
        }
    }//GEN-LAST:event_tblSightingsMouseReleased

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchElement = new Element();
        if (chkElementTypeFilter.isSelected())
            searchElement.setType((ElementType)cmbType.getSelectedItem());

        if (chkSearchDirect.isSelected()) {
            searchElement.setPrimaryName(txtSearch.getText());
            tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement));
        }
        else {
            DefaultTableModel model = utilTableGenerator.getShortElementTable(searchElement);
            for (int t = 0; t < model.getRowCount(); t++) {
                if (!((String)model.getValueAt(t, 0)).contains(txtSearch.getText())) {
                    model.removeRow(t--);
                }
            }
            tblElement.setModel(model);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            if (imageIndex < sighting.getFotos().size() - 1) setupFotos(++imageIndex);
            else {
                imageIndex = 0;
                setupFotos(imageIndex);
            }
        }
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        mapFrame = new MapFrame(app.getDBI(), "WildLog Map - Visit: " + visit.getName());
        for (int t = 0; t < visit.getSightings().size(); t++) {
            if (visit.getSightings().get(t).getLatitude() != null && visit.getSightings().get(t).getLongitude() != null)
            if (!visit.getSightings().get(t).getLatitude().equals(Latitudes.NONE) && !visit.getSightings().get(t).getLongitude().equals(Longitudes.NONE)) {
                float lat = visit.getSightings().get(t).getLatDegrees();
                lat = lat + visit.getSightings().get(t).getLatMinutes()/60f;
                lat = lat + (visit.getSightings().get(t).getLatSeconds()/60f)/60f;
                if (visit.getSightings().get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = visit.getSightings().get(t).getLonDegrees();
                lon = lon + visit.getSightings().get(t).getLonMinutes()/60f;
                lon = lon + (visit.getSightings().get(t).getLonSeconds()/60f)/60f;
                if (visit.getSightings().get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                mapFrame.addPoint(lat, lon, new Color(70, 120, 190));
            }
        }
        mapFrame.showMap();
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        if (sighting != null) {
            if (sighting.getFotos() != null) {
                if (sighting.getFotos().size() > 0) {
                    Foto tempFoto = sighting.getFotos().get(imageIndex);
                    sighting.getFotos().remove(tempFoto);
                    app.getDBI().delete(tempFoto);
                    app.getDBI().createOrUpdate(sighting);
                    if (sighting.getFotos().size() >= 1) {
                        // Behave like moving back button was pressed
                        btnPreviousImageActionPerformed(evt);
                    }
                    else {
                        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
                    }
                }
            }
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            sighting.getFotos().add(0, sighting.getFotos().get(imageIndex++));
            sighting.getFotos().remove(imageIndex);
            imageIndex = 0;
        }
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void setupFotos(int inIndex) {
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getFotos().get(inIndex).getFileLocation()), 300));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JCheckBox chkElementTypeFilter;
    private javax.swing.JCheckBox chkSearchDirect;
    private javax.swing.JComboBox cmbAreaType;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbGameWatchIntensity;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLonitude;
    private javax.swing.JComboBox cmbSubArea;
    private javax.swing.JComboBox cmbTimeOfDay;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbViewRating;
    private javax.swing.JComboBox cmbWeather;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpSightingDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblElement;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocationName;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblSightings;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JTextField txtLatDegrees;
    private javax.swing.JTextField txtLatMinutes;
    private javax.swing.JTextField txtLatSeconds;
    private javax.swing.JTextField txtLonDegrees;
    private javax.swing.JTextField txtLonMinutes;
    private javax.swing.JTextField txtLonSeconds;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNumberOfElements;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JPanel visitIncludes;
    // End of variables declaration//GEN-END:variables
    
}
