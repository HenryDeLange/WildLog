/*
 * PanelLocation.java
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
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Province;
import wildlog.ui.util.ImageFilter;
import wildlog.ui.util.ImagePreview;
import wildlog.ui.util.UtilPanelGenerator;
import wildlog.ui.util.UtilTableGenerator;
import wildlog.ui.util.Utils;
import wildlog.WildLogApp;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.MapFrame;


/**
 *
 * @author  henry.delange
 */
public class PanelLocation extends javax.swing.JPanel {
    // location is already used in this component... Have problem with getLocation()...
    private Location locationWL;
    private JTabbedPane parent;
    private int imageIndex;
    private UtilPanelGenerator utilPanelGenerator;
    private UtilTableGenerator utilTableGenerator;
    private MapFrame mapFrame;
    private WildLogApp app;
    
    /** Creates new form PanelLocation */
    public PanelLocation(Location inLocation) {
        app = (WildLogApp) Application.getInstance();
        locationWL = inLocation;
        utilPanelGenerator = new UtilPanelGenerator();
        utilTableGenerator = new UtilTableGenerator();
        initComponents();
        imageIndex = 0;
        if (locationWL.getFotos() != null && locationWL.getFotos().size() > 0) {
            setupFotos(0);
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        }
    }
    
    public void setLocationWL(Location inLocation) {
        locationWL = inLocation;
    }
    
    public Location getLocationWL() {
        return locationWL;
    }
    
    @Override
    public boolean equals(Object inObject) {
        if (getClass() != inObject.getClass()) return false;
        final PanelLocation inPanel = (PanelLocation) inObject;
        if (locationWL == null) return true;
        if (locationWL.getName() == null) return true;
        if (!locationWL.getName().equalsIgnoreCase(inPanel.getLocationWL().getName())) return false;
        return true;
    }
    
    public void setupTabHeader() {
        WildLogApp app = (WildLogApp) Application.getInstance();
        parent = (JTabbedPane) getParent();
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/Location.gif"))));
        if (locationWL.getName() != null) tabHeader.add(new JLabel(locationWL.getName() + " "));
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
    
    private void setupFotos(int inIndex) {
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(locationWL.getFotos().get(inIndex).getFileLocation()), 300));
    }
    
    // Need to look again later at listbox and how I use it...
    // Esspecially how I set the selected values...
    private int[] selectedAccommodationTypes() {
        if (locationWL.getAccommodationType() == null) return new int[0];
        int[] index = new int[locationWL.getAccommodationType().size()];
        int i = 0;
        for (int t = 0; t < AccommodationType.values().length; t++) {
            AccommodationType tempType = AccommodationType.values()[t];
            for (AccommodationType baaa : locationWL.getAccommodationType()) {
                if (baaa.test().equals(tempType.test())) index[i++] = t;
            }
        }
        return index;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        locationIncludes = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        lblLocation = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cmbProvince = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        cmbRating = new javax.swing.JComboBox();
        jSeparator6 = new javax.swing.JSeparator();
        cmbHabitat = new javax.swing.JComboBox();
        cmbGameRating = new javax.swing.JComboBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDirections = new javax.swing.JTextArea();
        txtWebsite = new javax.swing.JTextField();
        txtLatDegrees = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstAccommodationType = new javax.swing.JList();
        txtContactNumber = new javax.swing.JTextField();
        cmbCatering = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel45 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnPreviousImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        btnGoVisit = new javax.swing.JButton();
        btnAddVisit = new javax.swing.JButton();
        btnDeleteVisit = new javax.swing.JButton();
        btnGoElement = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        txtLatMinutes = new javax.swing.JTextField();
        txtLatSeconds = new javax.swing.JTextField();
        txtLonDegrees = new javax.swing.JTextField();
        txtLonMinutes = new javax.swing.JTextField();
        txtLonSeconds = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cmbLatitude = new javax.swing.JComboBox();
        cmbLonitude = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cmbSubAreas = new javax.swing.JComboBox();
        btnAddSubArea = new javax.swing.JButton();
        btnRemoveSubArea = new javax.swing.JButton();
        btnDeleteImage = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblNumberOfVisits = new javax.swing.JLabel();
        btnMap = new javax.swing.JButton();

        setName(locationWL.getName());
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        locationIncludes.setName("locationIncludes"); // NOI18N
        locationIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelLocation.class);
        txtName.setBackground(resourceMap.getColor("txtName.background")); // NOI18N
        txtName.setText(locationWL.getName());
        txtName.setName("txtName"); // NOI18N
        locationIncludes.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 28, 490, -1));

        lblLocation.setFont(resourceMap.getFont("lblLocation.font")); // NOI18N
        lblLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocation.setText(locationWL.getName());
        lblLocation.setName("lblLocation"); // NOI18N
        locationIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 610, 20));

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N
        locationIncludes.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 52, -1, -1));

        cmbProvince.setMaximumRowCount(10);
        cmbProvince.setModel(new DefaultComboBoxModel(Province.values()));
        cmbProvince.setSelectedItem(locationWL.getProvince());
        cmbProvince.setName("cmbProvince"); // NOI18N
        locationIncludes.add(cmbProvince, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 52, 170, -1));

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N
        locationIncludes.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        cmbRating.setModel(new DefaultComboBoxModel(LocationRating.values()));
        cmbRating.setSelectedItem(locationWL.getRating());
        cmbRating.setName("cmbRating"); // NOI18N
        locationIncludes.add(cmbRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 52, 160, -1));

        jSeparator6.setForeground(resourceMap.getColor("jSeparator6.foreground")); // NOI18N
        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setName("jSeparator6"); // NOI18N
        locationIncludes.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 361, 20, 220));

        cmbHabitat.setModel(new DefaultComboBoxModel(Habitat.values()));
        cmbHabitat.setSelectedItem(locationWL.getHabitatType());
        cmbHabitat.setName("cmbHabitat"); // NOI18N
        locationIncludes.add(cmbHabitat, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 76, 170, -1));

        cmbGameRating.setModel(new DefaultComboBoxModel(GameViewRating.values()));
        cmbGameRating.setSelectedItem(locationWL.getGameViewingRating());
        cmbGameRating.setName("cmbGameRating"); // NOI18N
        locationIncludes.add(cmbGameRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 76, 160, -1));

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(locationWL.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane10.setViewportView(txtDescription);

        locationIncludes.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 240, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDirections.setColumns(20);
        txtDirections.setLineWrap(true);
        txtDirections.setRows(5);
        txtDirections.setText(locationWL.getDirections());
        txtDirections.setWrapStyleWord(true);
        txtDirections.setName("txtDirections"); // NOI18N
        jScrollPane2.setViewportView(txtDirections);

        locationIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 280, -1));

        txtWebsite.setText(locationWL.getWebsite());
        txtWebsite.setName("txtWebsite"); // NOI18N
        locationIncludes.add(txtWebsite, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 240, -1));

        txtLatDegrees.setText(Integer.toString(locationWL.getLatDegrees()));
        txtLatDegrees.setName("txtLatDegrees"); // NOI18N
        locationIncludes.add(txtLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 224, 30, -1));

        txtEmail.setText(locationWL.getEmail());
        txtEmail.setName("txtEmail"); // NOI18N
        locationIncludes.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 224, 240, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstAccommodationType.setModel(new DefaultComboBoxModel(AccommodationType.values()));
        lstAccommodationType.setName("lstAccommodationType"); // NOI18N
        lstAccommodationType.setSelectedIndices(selectedAccommodationTypes());
        lstAccommodationType.setVisibleRowCount(4);
        jScrollPane1.setViewportView(lstAccommodationType);

        locationIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 248, 200, 80));

        txtContactNumber.setText(locationWL.getContactNumbers());
        txtContactNumber.setName("txtContactNumber"); // NOI18N
        locationIncludes.add(txtContactNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, 240, -1));

        cmbCatering.setModel(new DefaultComboBoxModel(CateringType.values()));
        cmbCatering.setSelectedItem(locationWL.getCatering());
        cmbCatering.setName("cmbCatering"); // NOI18N
        locationIncludes.add(cmbCatering, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 274, 240, -1));

        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N
        locationIncludes.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 76, -1, -1));

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N
        locationIncludes.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

        jLabel42.setText(resourceMap.getString("jLabel42.text")); // NOI18N
        jLabel42.setName("jLabel42"); // NOI18N
        locationIncludes.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, -1, -1));

        jLabel44.setText(resourceMap.getString("jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N
        locationIncludes.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 361, -1, -1));

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblVisit.setName("tblVisit"); // NOI18N
        jScrollPane12.setViewportView(tblVisit);

        locationIncludes.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 376, 590, 200));

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setModel(utilTableGenerator.getElementsForLocationTable(locationWL));
        tblElement.setName("tblElement"); // NOI18N
        jScrollPane11.setViewportView(tblElement);

        locationIncludes.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 376, 290, 170));

        jSeparator7.setForeground(resourceMap.getColor("jSeparator7.foreground")); // NOI18N
        jSeparator7.setName("jSeparator7"); // NOI18N
        locationIncludes.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 1000, 10));

        jLabel45.setText(resourceMap.getString("jLabel45.text")); // NOI18N
        jLabel45.setName("jLabel45"); // NOI18N
        locationIncludes.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 361, -1, -1));

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
        locationIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 28, 110, 60));

        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 326, 100, -1));

        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        btnGoVisit.setIcon(resourceMap.getIcon("btnGoVisit.icon")); // NOI18N
        btnGoVisit.setText(resourceMap.getString("btnGoVisit.text")); // NOI18N
        btnGoVisit.setToolTipText(resourceMap.getString("btnGoVisit.toolTipText")); // NOI18N
        btnGoVisit.setName("btnGoVisit"); // NOI18N
        btnGoVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoVisitActionPerformed(evt);
            }
        });
        locationIncludes.add(btnGoVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 90, 80));

        btnAddVisit.setIcon(resourceMap.getIcon("btnAddVisit.icon")); // NOI18N
        btnAddVisit.setText(resourceMap.getString("btnAddVisit.text")); // NOI18N
        btnAddVisit.setToolTipText(resourceMap.getString("btnAddVisit.toolTipText")); // NOI18N
        btnAddVisit.setName("btnAddVisit"); // NOI18N
        btnAddVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVisitActionPerformed(evt);
            }
        });
        locationIncludes.add(btnAddVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 490, 90, 30));

        btnDeleteVisit.setIcon(resourceMap.getIcon("btnDeleteVisit.icon")); // NOI18N
        btnDeleteVisit.setText(resourceMap.getString("btnDeleteVisit.text")); // NOI18N
        btnDeleteVisit.setToolTipText(resourceMap.getString("btnDeleteVisit.toolTipText")); // NOI18N
        btnDeleteVisit.setName("btnDeleteVisit"); // NOI18N
        btnDeleteVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVisitActionPerformed(evt);
            }
        });
        locationIncludes.add(btnDeleteVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 548, 90, 30));

        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        locationIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 548, 290, 30));

        jLabel48.setText(resourceMap.getString("jLabel48.text")); // NOI18N
        jLabel48.setName("jLabel48"); // NOI18N
        locationIncludes.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 28, -1, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        locationIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 250, -1, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        locationIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 274, -1, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        locationIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 248, -1, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        locationIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 224, -1, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        locationIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        locationIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, -1, -1));

        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        locationIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        locationIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 20));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        locationIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 298, -1, -1));

        txtLatMinutes.setText(Integer.toString(locationWL.getLatMinutes()));
        txtLatMinutes.setName("txtLatMinutes"); // NOI18N
        locationIncludes.add(txtLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 224, 30, -1));

        txtLatSeconds.setText(Integer.toString(locationWL.getLatSeconds()));
        txtLatSeconds.setName("txtLatSeconds"); // NOI18N
        locationIncludes.add(txtLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 224, 30, -1));

        txtLonDegrees.setText(Integer.toString(locationWL.getLonDegrees()));
        txtLonDegrees.setName("txtLonDegrees"); // NOI18N
        locationIncludes.add(txtLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 224, 30, -1));

        txtLonMinutes.setText(Integer.toString(locationWL.getLonMinutes()));
        txtLonMinutes.setName("txtLonMinutes"); // NOI18N
        locationIncludes.add(txtLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 224, 30, -1));

        txtLonSeconds.setText(Integer.toString(locationWL.getLonSeconds()));
        txtLonSeconds.setName("txtLonSeconds"); // NOI18N
        locationIncludes.add(txtLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 224, 30, -1));

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        locationIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 200, -1, -1));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        locationIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 200, 90, -1));

        cmbLonitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLonitude.setSelectedIndex(2);
        cmbLonitude.setName("cmbLonitude"); // NOI18N
        locationIncludes.add(cmbLonitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 200, 90, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        locationIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 200, -1, 20));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        locationIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, -1, 20));

        cmbSubAreas.setMaximumRowCount(15);
        cmbSubAreas.setModel(new DefaultComboBoxModel(locationWL.getSubAreas().toArray()));
        cmbSubAreas.setName("cmbSubAreas"); // NOI18N
        locationIncludes.add(cmbSubAreas, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 298, 240, -1));

        btnAddSubArea.setIcon(resourceMap.getIcon("btnAddSubArea.icon")); // NOI18N
        btnAddSubArea.setText(resourceMap.getString("btnAddSubArea.text")); // NOI18N
        btnAddSubArea.setToolTipText(resourceMap.getString("btnAddSubArea.toolTipText")); // NOI18N
        btnAddSubArea.setName("btnAddSubArea"); // NOI18N
        btnAddSubArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSubAreaActionPerformed(evt);
            }
        });
        locationIncludes.add(btnAddSubArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, 120, -1));

        btnRemoveSubArea.setIcon(resourceMap.getIcon("btnRemoveSubArea.icon")); // NOI18N
        btnRemoveSubArea.setText(resourceMap.getString("btnRemoveSubArea.text")); // NOI18N
        btnRemoveSubArea.setToolTipText(resourceMap.getString("btnRemoveSubArea.toolTipText")); // NOI18N
        btnRemoveSubArea.setName("btnRemoveSubArea"); // NOI18N
        btnRemoveSubArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveSubAreaActionPerformed(evt);
            }
        });
        locationIncludes.add(btnRemoveSubArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 320, 120, -1));

        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 326, 100, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        locationIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 0, -1, 20));

        lblNumberOfVisits.setFont(resourceMap.getFont("lblNumberOfVisits.font")); // NOI18N
        lblNumberOfVisits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfVisits.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfVisits.border.lineColor"))); // NOI18N
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N
        locationIncludes.add(lblNumberOfVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 1, 40, 20));

        btnMap.setText(resourceMap.getString("btnMap.text")); // NOI18N
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });
        locationIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 248, 70, 80));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(locationIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(locationIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (txtName.getText().length() > 0) {
            locationWL.setName(txtName.getText());
            locationWL.setLatitude((Latitudes)cmbLatitude.getSelectedItem());
            locationWL.setLongitude((Longitudes)cmbLonitude.getSelectedItem());
            try {
                locationWL.setLatDegrees(Integer.parseInt(txtLatDegrees.getText()));
                locationWL.setLatMinutes(Integer.parseInt(txtLatMinutes.getText()));
                locationWL.setLatSeconds(Integer.parseInt(txtLatSeconds.getText()));
                locationWL.setLonDegrees(Integer.parseInt(txtLonDegrees.getText()));
                locationWL.setLonMinutes(Integer.parseInt(txtLonMinutes.getText()));
                locationWL.setLonSeconds(Integer.parseInt(txtLonSeconds.getText()));
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
            locationWL.setDescription(txtDescription.getText());
            locationWL.setProvince((Province)cmbProvince.getSelectedItem());
            locationWL.setRating((LocationRating)cmbRating.getSelectedItem());
            locationWL.setCatering((CateringType)cmbCatering.getSelectedItem());
            locationWL.setHabitatType((Habitat)cmbHabitat.getSelectedItem());
            locationWL.setGameViewingRating((GameViewRating)cmbGameRating.getSelectedItem());
            locationWL.setContactNumbers(txtContactNumber.getText());
            locationWL.setEmail(txtEmail.getText());
            locationWL.setWebsite(txtWebsite.getText());
            locationWL.setDirections(txtDirections.getText());
            Object[] tempArray = lstAccommodationType.getSelectedValues();
            List<AccommodationType> tempList = new ArrayList<AccommodationType>(tempArray.length);
            for (Object tempObject : tempArray) {
                tempList.add((AccommodationType)tempObject);
            }
            locationWL.setAccommodationType(tempList);

            lblLocation.setText(locationWL.getName());

            app.getDBI().createOrUpdate(locationWL);

            org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelLocation.class);
            txtName.setBackground(resourceMap.getColor("txtName.background"));

            setupTabHeader();
        }
        else {
            txtName.setBackground(Color.RED);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
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
                    if (locationWL.getFotos() == null) locationWL.setFotos(new ArrayList<Foto>());
                    locationWL.getFotos().add(new Foto(toFile.getName()));
                    setupFotos(locationWL.getFotos().size() - 1);
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
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (locationWL.getFotos() != null && locationWL.getFotos().size() > 0) {
            if (imageIndex > 0) setupFotos(--imageIndex);
            else {
                imageIndex = locationWL.getFotos().size() - 1;
                setupFotos(imageIndex);
            }
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (locationWL.getFotos() != null && locationWL.getFotos().size() > 0) {
            if (imageIndex < locationWL.getFotos().size() - 1) setupFotos(++imageIndex);
            else {
               imageIndex = 0;
                setupFotos(imageIndex);
            }
        }
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (locationWL.getFotos() != null && locationWL.getFotos().size() > 0) {
            locationWL.getFotos().add(0, locationWL.getFotos().get(imageIndex++));
            locationWL.getFotos().remove(imageIndex);
            imageIndex = 0;
        }
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnAddVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVisitActionPerformed
        PanelVisit tempPanel = utilPanelGenerator.getNewVisitPanel(locationWL);
        parent.add(tempPanel);
        tempPanel.setupTabHeader();
        parent.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnAddVisitActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tblVisit.setModel(utilTableGenerator.getCompleteVisitTable(locationWL));
        tblElement.setModel(utilTableGenerator.getElementsForLocationTable(locationWL));
        if (locationWL.getLatitude() != null)
            cmbLatitude.setSelectedItem(locationWL.getLatitude());
        if (locationWL.getLongitude() != null)
            cmbLonitude.setSelectedItem(locationWL.getLongitude());
        if (locationWL.getVisits() != null)
            lblNumberOfVisits.setText(Integer.toString(locationWL.getVisits().size()));
        else
            lblNumberOfVisits.setText("0");
        if (locationWL.getSubAreas().size() > 1) cmbSubAreas.setSelectedIndex(1);
    }//GEN-LAST:event_formComponentShown

    private void btnGoVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoVisitActionPerformed
        int[] selectedRows = tblVisit.getSelectedRows();
        PanelVisit tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getVisitPanel(locationWL, (String)tblVisit.getValueAt(selectedRows[t], 0));
            parent.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) parent.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoVisitActionPerformed

    private void btnDeleteVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVisitActionPerformed
        int[] selectedRows = tblVisit.getSelectedRows();
        PanelVisit tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getVisitPanel(locationWL, (String)tblVisit.getValueAt(selectedRows[t], 0));
            parent.remove(tempPanel);
            locationWL.getVisits().remove(tempPanel.getVisit());
            app.getDBI().createOrUpdate(locationWL);
            app.getDBI().delete(tempPanel.getVisit());
        }
        tblVisit.setModel(utilTableGenerator.getCompleteVisitTable(locationWL));
    }//GEN-LAST:event_btnDeleteVisitActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        int[] selectedRows = tblElement.getSelectedRows();
        PanelElement tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getElementPanel((String)tblElement.getValueAt(selectedRows[t], 0));
            parent = (JTabbedPane) getParent();
            parent.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) parent.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnAddSubAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSubAreaActionPerformed
        final JDialog dialog = new JDialog(new JFrame(), "Please enter:", true);
        dialog.setLayout(new AbsoluteLayout());
        JLabel label = new JLabel("Sub Area:");
        label.setSize(40, 20);
        dialog.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 4, 50, -1));
        final JTextField textfield = new JTextField();
        textfield.setSize(120, 20);
        dialog.add(textfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 2, 150, -1));
        JButton button = new JButton("Add");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationWL.getSubAreas().add(textfield.getText());
                dialog.dispose();
            }
        });
        dialog.setSize(210, 84);
        dialog.add(button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 23, 200, -1));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        cmbSubAreas.setModel(new DefaultComboBoxModel(locationWL.getSubAreas().toArray()));
        btnUpdateActionPerformed(null);
}//GEN-LAST:event_btnAddSubAreaActionPerformed

    private void btnRemoveSubAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSubAreaActionPerformed
        locationWL.getSubAreas().remove(cmbSubAreas.getSelectedItem());
        // Remove from all sightings
        if (locationWL.getVisits() != null) {
            for (int t = 0; t < locationWL.getVisits().size(); t++) {
                for (int i = 0; i < locationWL.getVisits().get(t).getSightings().size(); i++) {
                    if (locationWL.getVisits().get(t).getSightings().get(i).getSubArea().equals(cmbSubAreas.getSelectedItem())) {
                        locationWL.getVisits().get(t).getSightings().get(i).setSubArea("");
                    }
                }
            }
        }
        cmbSubAreas.setModel(new DefaultComboBoxModel(locationWL.getSubAreas().toArray()));
        btnUpdateActionPerformed(null);
    }//GEN-LAST:event_btnRemoveSubAreaActionPerformed

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        mapFrame = new MapFrame(app.getDBI(), "WildLog Map - Location: " + locationWL.getName());
        if (locationWL.getLatitude() != null && locationWL.getLongitude() != null)
            if (!locationWL.getLatitude().equals(Latitudes.NONE) && !locationWL.getLongitude().equals(Longitudes.NONE)) {
                float lat = locationWL.getLatDegrees();
                lat = lat + locationWL.getLatMinutes()/60f;
                lat = lat + (locationWL.getLatSeconds()/60f)/60f;
                if (locationWL.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = locationWL.getLonDegrees();
                lon = lon + locationWL.getLonMinutes()/60f;
                lon = lon + (locationWL.getLonSeconds()/60f)/60f;
                if (locationWL.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                mapFrame.addPoint(lat, lon, new Color(70, 120, 190));
            }
        mapFrame.showMap();
}//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        if (locationWL.getFotos() != null) {
            if (locationWL.getFotos().size() > 0) {
                Foto tempFoto = locationWL.getFotos().get(imageIndex);
                locationWL.getFotos().remove(tempFoto);
                app.getDBI().delete(tempFoto);
                app.getDBI().createOrUpdate(locationWL);
                if (locationWL.getFotos().size() >= 1) {
                    // Behave like moving back button was pressed
                    btnPreviousImageActionPerformed(evt);
                }
                else {
                    lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
                }
            }
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSubArea;
    private javax.swing.JButton btnAddVisit;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteVisit;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoVisit;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnRemoveSubArea;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox cmbCatering;
    private javax.swing.JComboBox cmbGameRating;
    private javax.swing.JComboBox cmbHabitat;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLonitude;
    private javax.swing.JComboBox cmbProvince;
    private javax.swing.JComboBox cmbRating;
    private javax.swing.JComboBox cmbSubAreas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JPanel locationIncludes;
    private javax.swing.JList lstAccommodationType;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDirections;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLatDegrees;
    private javax.swing.JTextField txtLatMinutes;
    private javax.swing.JTextField txtLatSeconds;
    private javax.swing.JTextField txtLonDegrees;
    private javax.swing.JTextField txtLonMinutes;
    private javax.swing.JTextField txtLonSeconds;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtWebsite;
    // End of variables declaration//GEN-END:variables
    
}
