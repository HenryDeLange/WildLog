/*
 * PanelLocation.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.ui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Application;
import wildlog.data.dataobjects.Location;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Province;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.ui.report.ReportLocation;
import wildlog.utils.LatLonConverter;
import wildlog.utils.UtilsHTML;
import wildlog.utils.ui.DateCellRenderer;


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
    private WildLogApp app;
    
    /** Creates new form PanelLocation */
    public PanelLocation(Location inLocation) {
        app = (WildLogApp) Application.getInstance();
        locationWL = inLocation;
        utilPanelGenerator = new UtilPanelGenerator();
        utilTableGenerator = new UtilTableGenerator();
        initComponents();
        imageIndex = 0;
        List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + locationWL.getName()));
        if (fotos.size() > 0) {
            Utils.setupFoto("LOCATION-" + locationWL.getName(), imageIndex, lblImage, 300, app);
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        }
        setupNumberOfImages();
        
        tblElement.getTableHeader().setReorderingAllowed(false);
        tblVisit.getTableHeader().setReorderingAllowed(false);
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
        if (locationWL == null && inPanel.getLocationWL() == null) return true;
        if (locationWL.getName() == null && inPanel.getLocationWL().getName() == null) return true;
        if (locationWL == null) return false;
        if (locationWL.getName() == null) return false;
        if (!locationWL.getName().equalsIgnoreCase(inPanel.getLocationWL().getName())) return false;
        return true;
    }
    
    public void setupTabHeader() {
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
            @Override
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
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
        txtLatMinutes = new javax.swing.JTextField();
        txtLatSeconds = new javax.swing.JTextField();
        txtLonDegrees = new javax.swing.JTextField();
        txtLonMinutes = new javax.swing.JTextField();
        txtLonSeconds = new javax.swing.JTextField();
        cmbLatitude = new javax.swing.JComboBox();
        cmbLongitude = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnDeleteImage = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblNumberOfVisits = new javax.swing.JLabel();
        btnMap = new javax.swing.JButton();
        btnMapSightings = new javax.swing.JButton();
        rdbLocation = new javax.swing.JRadioButton();
        rdbVisit = new javax.swing.JRadioButton();
        lblNumberOfElements = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        rdbDMS = new javax.swing.JRadioButton();
        rdbDD = new javax.swing.JRadioButton();
        txtLatDecimal = new javax.swing.JTextField();
        txtLonDecimal = new javax.swing.JTextField();
        btnHTML = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName(locationWL.getName());
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        locationIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        locationIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        locationIncludes.setName("locationIncludes"); // NOI18N
        locationIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));
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
        locationIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 630, 20));

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N
        locationIncludes.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 52, -1, -1));

        cmbProvince.setMaximumRowCount(10);
        cmbProvince.setModel(new DefaultComboBoxModel(Province.values()));
        cmbProvince.setSelectedItem(locationWL.getProvince());
        cmbProvince.setName("cmbProvince"); // NOI18N
        locationIncludes.add(cmbProvince, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 52, 170, -1));

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N
        locationIncludes.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, -1, -1));

        cmbRating.setModel(new DefaultComboBoxModel(LocationRating.values()));
        cmbRating.setSelectedItem(locationWL.getRating());
        cmbRating.setName("cmbRating"); // NOI18N
        locationIncludes.add(cmbRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 52, 160, -1));

        jSeparator6.setForeground(resourceMap.getColor("jSeparator6.foreground")); // NOI18N
        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setName("jSeparator6"); // NOI18N
        locationIncludes.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 361, 20, 220));

        cmbHabitat.setMaximumRowCount(13);
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
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(locationWL.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane10.setViewportView(txtDescription);

        locationIncludes.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 240, 90));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDirections.setColumns(20);
        txtDirections.setFont(resourceMap.getFont("txtDirections.font")); // NOI18N
        txtDirections.setLineWrap(true);
        txtDirections.setRows(5);
        txtDirections.setText(locationWL.getDirections());
        txtDirections.setWrapStyleWord(true);
        txtDirections.setName("txtDirections"); // NOI18N
        jScrollPane2.setViewportView(txtDirections);

        locationIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 200, 160, 140));

        txtWebsite.setText(locationWL.getWebsite());
        txtWebsite.setName("txtWebsite"); // NOI18N
        locationIncludes.add(txtWebsite, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 240, -1));

        txtLatDegrees.setText(Integer.toString(locationWL.getLatDegrees()));
        txtLatDegrees.setName("txtLatDegrees"); // NOI18N
        txtLatDegrees.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatDegreesFocusGained(evt);
            }
        });
        locationIncludes.add(txtLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, 30, -1));

        txtEmail.setText(locationWL.getEmail());
        txtEmail.setName("txtEmail"); // NOI18N
        txtEmail.setNextFocusableComponent(txtContactNumber);
        locationIncludes.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 224, 240, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lstAccommodationType.setModel(new DefaultComboBoxModel(AccommodationType.values()));
        lstAccommodationType.setName("lstAccommodationType"); // NOI18N
        lstAccommodationType.setSelectedIndices(selectedAccommodationTypes());
        lstAccommodationType.setSelectionBackground(resourceMap.getColor("lstAccommodationType.selectionBackground")); // NOI18N
        lstAccommodationType.setSelectionForeground(resourceMap.getColor("lstAccommodationType.selectionForeground")); // NOI18N
        lstAccommodationType.setVisibleRowCount(4);
        jScrollPane1.setViewportView(lstAccommodationType);

        locationIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 160, 90));

        txtContactNumber.setText(locationWL.getContactNumbers());
        txtContactNumber.setName("txtContactNumber"); // NOI18N
        locationIncludes.add(txtContactNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 248, 240, -1));

        cmbCatering.setModel(new DefaultComboBoxModel(CateringType.values()));
        cmbCatering.setSelectedItem(locationWL.getCatering());
        cmbCatering.setName("cmbCatering"); // NOI18N
        locationIncludes.add(cmbCatering, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 272, 240, -1));

        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N
        locationIncludes.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 76, -1, -1));

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N
        locationIncludes.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 52, -1, -1));

        jLabel42.setText(resourceMap.getString("jLabel42.text")); // NOI18N
        jLabel42.setName("jLabel42"); // NOI18N
        locationIncludes.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 76, -1, -1));

        jLabel44.setText(resourceMap.getString("jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N
        locationIncludes.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 361, -1, -1));

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        tblVisit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVisitKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblVisitKeyReleased(evt);
            }
        });
        jScrollPane12.setViewportView(tblVisit);

        locationIncludes.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 376, 590, 200));

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
        });
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
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnPreviousImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnSetMainImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        locationIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 326, 90, -1));

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
        locationIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

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
        locationIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        btnGoVisit.setIcon(resourceMap.getIcon("btnGoVisit.icon")); // NOI18N
        btnGoVisit.setText(resourceMap.getString("btnGoVisit.text")); // NOI18N
        btnGoVisit.setToolTipText(resourceMap.getString("btnGoVisit.toolTipText")); // NOI18N
        btnGoVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnAddVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnDeleteVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnGoElement.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        locationIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 548, 130, 30));

        jLabel48.setText(resourceMap.getString("jLabel48.text")); // NOI18N
        jLabel48.setName("jLabel48"); // NOI18N
        locationIncludes.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 28, -1, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        locationIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 90, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        locationIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 272, -1, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        locationIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 248, -1, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        locationIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 224, -1, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        locationIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        locationIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 200, -1, -1));

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
        locationIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator1.setName("jSeparator1"); // NOI18N
        locationIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 20));

        txtLatMinutes.setText(Integer.toString(locationWL.getLatMinutes()));
        txtLatMinutes.setName("txtLatMinutes"); // NOI18N
        txtLatMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatMinutesFocusGained(evt);
            }
        });
        locationIncludes.add(txtLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, 30, -1));

        txtLatSeconds.setText(Float.toString(locationWL.getLatSecondsFloat()));
        txtLatSeconds.setName("txtLatSeconds"); // NOI18N
        txtLatSeconds.setNextFocusableComponent(txtLonDegrees);
        txtLatSeconds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatSecondsFocusGained(evt);
            }
        });
        locationIncludes.add(txtLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 300, 50, -1));

        txtLonDegrees.setText(Integer.toString(locationWL.getLonDegrees()));
        txtLonDegrees.setName("txtLonDegrees"); // NOI18N
        txtLonDegrees.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonDegreesFocusGained(evt);
            }
        });
        locationIncludes.add(txtLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, 30, -1));

        txtLonMinutes.setText(Integer.toString(locationWL.getLonMinutes()));
        txtLonMinutes.setName("txtLonMinutes"); // NOI18N
        txtLonMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonMinutesFocusGained(evt);
            }
        });
        locationIncludes.add(txtLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 320, 30, -1));

        txtLonSeconds.setText(Float.toString(locationWL.getLonSecondsFloat()));
        txtLonSeconds.setName("txtLonSeconds"); // NOI18N
        txtLonSeconds.setNextFocusableComponent(txtEmail);
        txtLonSeconds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonSecondsFocusGained(evt);
            }
        });
        locationIncludes.add(txtLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 320, 50, -1));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        cmbLatitude.setNextFocusableComponent(cmbLongitude);
        locationIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, 90, -1));

        cmbLongitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLongitude.setSelectedIndex(2);
        cmbLongitude.setName("cmbLongitude"); // NOI18N
        cmbLongitude.setNextFocusableComponent(txtLatDegrees);
        locationIncludes.add(cmbLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 320, 90, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        locationIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, -1, 20));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        locationIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, 20));

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
        locationIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 326, 90, -1));

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        locationIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        lblNumberOfVisits.setFont(resourceMap.getFont("lblNumberOfVisits.font")); // NOI18N
        lblNumberOfVisits.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfVisits.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfVisits.border.lineColor"))); // NOI18N
        lblNumberOfVisits.setName("lblNumberOfVisits"); // NOI18N
        locationIncludes.add(lblNumberOfVisits, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 1, 30, 20));

        btnMap.setFont(resourceMap.getFont("btnMap.font")); // NOI18N
        btnMap.setIcon(resourceMap.getIcon("btnMap.icon")); // NOI18N
        btnMap.setText(resourceMap.getString("btnMap.text")); // NOI18N
        btnMap.setToolTipText(resourceMap.getString("btnMap.toolTipText")); // NOI18N
        btnMap.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });
        locationIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 110, 40));

        btnMapSightings.setFont(resourceMap.getFont("btnMapSightings.font")); // NOI18N
        btnMapSightings.setIcon(resourceMap.getIcon("btnMapSightings.icon")); // NOI18N
        btnMapSightings.setText(resourceMap.getString("btnMapSightings.text")); // NOI18N
        btnMapSightings.setToolTipText(resourceMap.getString("btnMapSightings.toolTipText")); // NOI18N
        btnMapSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapSightings.setName("btnMapSightings"); // NOI18N
        btnMapSightings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingsActionPerformed(evt);
            }
        });
        locationIncludes.add(btnMapSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 150, 110, 40));

        buttonGroup1.add(rdbLocation);
        rdbLocation.setSelected(true);
        rdbLocation.setText(resourceMap.getString("rdbLocation.text")); // NOI18N
        rdbLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocation.setName("rdbLocation"); // NOI18N
        rdbLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbLocationItemStateChanged(evt);
            }
        });
        locationIncludes.add(rdbLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 550, -1, -1));

        buttonGroup1.add(rdbVisit);
        rdbVisit.setText(resourceMap.getString("rdbVisit.text")); // NOI18N
        rdbVisit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbVisit.setName("rdbVisit"); // NOI18N
        locationIncludes.add(rdbVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 550, -1, -1));

        lblNumberOfElements.setFont(resourceMap.getFont("lblNumberOfElements.font")); // NOI18N
        lblNumberOfElements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfElements.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfElements.border.lineColor"))); // NOI18N
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        locationIncludes.add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 550, 30, 20));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        locationIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 330, 40, 20));

        buttonGroup2.add(rdbDMS);
        rdbDMS.setText(resourceMap.getString("rdbDMS.text")); // NOI18N
        rdbDMS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbDMS.setName("rdbDMS"); // NOI18N
        rdbDMS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbDMSItemStateChanged(evt);
            }
        });
        locationIncludes.add(rdbDMS, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 300, -1, -1));

        buttonGroup2.add(rdbDD);
        rdbDD.setText(resourceMap.getString("rdbDD.text")); // NOI18N
        rdbDD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbDD.setName("rdbDD"); // NOI18N
        rdbDD.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbDDItemStateChanged(evt);
            }
        });
        locationIncludes.add(rdbDD, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 320, -1, -1));

        txtLatDecimal.setText(resourceMap.getString("txtLatDecimal.text")); // NOI18N
        txtLatDecimal.setName("txtLatDecimal"); // NOI18N
        txtLatDecimal.setNextFocusableComponent(txtLonDecimal);
        txtLatDecimal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatDecimalFocusGained(evt);
            }
        });
        locationIncludes.add(txtLatDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, 130, -1));

        txtLonDecimal.setText(resourceMap.getString("txtLonDecimal.text")); // NOI18N
        txtLonDecimal.setName("txtLonDecimal"); // NOI18N
        txtLonDecimal.setNextFocusableComponent(txtEmail);
        txtLonDecimal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonDecimalFocusGained(evt);
            }
        });
        locationIncludes.add(txtLonDecimal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, 130, -1));

        btnHTML.setIcon(resourceMap.getIcon("btnHTML.icon")); // NOI18N
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });
        locationIncludes.add(btnHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 250, 110, 30));

        btnReport.setIcon(resourceMap.getIcon("btnReport.icon")); // NOI18N
        btnReport.setText(resourceMap.getString("btnReport.text")); // NOI18N
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        locationIncludes.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, 110, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(locationIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 1005, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(locationIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (Utils.checkCharacters(txtName.getText().trim())) {
            if (txtName.getText().length() > 0) {
                String oldName = locationWL.getName();
                locationWL.setName(txtName.getText().trim());
                locationWL.setLatitude((Latitudes)cmbLatitude.getSelectedItem());
                locationWL.setLongitude((Longitudes)cmbLongitude.getSelectedItem());
                rdbDMS.setSelected(true);
                try {
                    locationWL.setLatDegrees(Integer.parseInt(txtLatDegrees.getText()));
                    locationWL.setLatMinutes(Integer.parseInt(txtLatMinutes.getText()));
                    locationWL.setLatSecondsFloat(Float.parseFloat(txtLatSeconds.getText()));
                    locationWL.setLonDegrees(Integer.parseInt(txtLonDegrees.getText()));
                    locationWL.setLonMinutes(Integer.parseInt(txtLonMinutes.getText()));
                    locationWL.setLonSecondsFloat(Float.parseFloat(txtLonSeconds.getText()));
                }
                catch (NumberFormatException e) {
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

                // Save the location
                if (app.getDBI().createOrUpdate(locationWL, oldName) == true) {
                    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelLocation.class);
                    txtName.setBackground(resourceMap.getColor("txtName.background"));
                }
                else {
                    txtName.setBackground(Color.RED);
                    locationWL.setName(oldName);
                    txtName.setText(txtName.getText() + "_not_unique");
                }

                lblLocation.setText(locationWL.getName());

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

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            imageIndex = Utils.uploadImage("LOCATION-" + locationWL.getName(), "Locations"+File.separatorChar+locationWL.getName(), this, lblImage, 300, app);
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = Utils.previousImage("LOCATION-" + locationWL.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = Utils.nextImage("LOCATION-" + locationWL.getName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = Utils.setMainImage("LOCATION-" + locationWL.getName(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnAddVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVisitActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtName.getBackground().equals(Color.RED)) {
            PanelVisit tempPanel = utilPanelGenerator.getNewVisitPanel(locationWL);
            parent.add(tempPanel);
            tempPanel.setupTabHeader();
            parent.setSelectedComponent(tempPanel);
        }
    }//GEN-LAST:event_btnAddVisitActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if (locationWL.getLatitude() != null)
            cmbLatitude.setSelectedItem(locationWL.getLatitude());
        if (locationWL.getLongitude() != null)
            cmbLongitude.setSelectedItem(locationWL.getLongitude());
        
        rdbLocation.setSelected(true);
        //if (locationWL.getSubAreas().size() > 1) cmbSubAreas.setSelectedIndex(1);

        if (locationWL.getName() != null) {
            Visit tempVisit = new Visit();
            tempVisit.setLocationName(locationWL.getName());
            List<Visit> visits = app.getDBI().list(tempVisit);
            lblNumberOfVisits.setText(Integer.toString(visits.size()));
            tblVisit.setModel(utilTableGenerator.getCompleteVisitTable(locationWL));
            tblElement.setModel(utilTableGenerator.getElementsForLocationTable(locationWL));
            // Sort rows for visits
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
            tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(1, SortOrder.ASCENDING));
            tblVisit.getRowSorter().setSortKeys(tempList);
        }
        else {
            lblNumberOfVisits.setText("0");
            tblVisit.setModel(new DefaultTableModel(new String[]{"No Visits"}, 0));
            tblElement.setModel(new DefaultTableModel(new String[]{"No Creatures"}, 0));
        }
        // Setup table column sizes
        resizeTables();
        // Lat Lon stuff
        txtLatDecimal.setText(Double.toString(LatLonConverter.getDecimalDegree((Latitudes)cmbLatitude.getSelectedItem(), Integer.parseInt(txtLatDegrees.getText()), Integer.parseInt(txtLatMinutes.getText()), Float.parseFloat(txtLatSeconds.getText()))));
        txtLonDecimal.setText(Double.toString(LatLonConverter.getDecimalDegree((Longitudes)cmbLongitude.getSelectedItem(), Integer.parseInt(txtLonDegrees.getText()), Integer.parseInt(txtLonMinutes.getText()), Float.parseFloat(txtLonSeconds.getText()))));
        rdbDMS.setSelected(true);
        lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
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
        if (tblVisit.getSelectedRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the Visit(s)?", "Delete Visit(s)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                int[] selectedRows = tblVisit.getSelectedRows();
                PanelVisit tempPanel = null;
                for (int t = 0; t < selectedRows.length; t++) {
                    tempPanel = utilPanelGenerator.getVisitPanel(locationWL, (String)tblVisit.getValueAt(selectedRows[t], 0));
                    parent.remove(tempPanel);
                    app.getDBI().delete(tempPanel.getVisit());
                }
                formComponentShown(null);
            }
        }
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

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        app.getMapFrame().clearPoints();
        if (locationWL.getLatitude() != null && locationWL.getLongitude() != null)
            if (!locationWL.getLatitude().equals(Latitudes.NONE) && !locationWL.getLongitude().equals(Longitudes.NONE)) {
                float lat = locationWL.getLatDegrees();
                lat = lat + locationWL.getLatMinutes()/60f;
                lat = lat + (locationWL.getLatSecondsFloat()/60f)/60f;
                if (locationWL.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = locationWL.getLonDegrees();
                lon = lon + locationWL.getLonMinutes()/60f;
                lon = lon + (locationWL.getLonSecondsFloat()/60f)/60f;
                if (locationWL.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
            }
        app.getMapFrame().changeTitle("WildLog Map - Location: " + locationWL.getName());
        app.getMapFrame().showMap();
}//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = Utils.removeImage("LOCATION-" + locationWL.getName(), imageIndex, lblImage, app.getDBI(), app.getClass().getResource("resources/images/NoImage.gif"), 300, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnMapSightingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingsActionPerformed
        app.getMapFrame().clearPoints();
        Visit tempVisit = new Visit();
        tempVisit.setLocationName(locationWL.getName());
        List<Visit> visits = app.getDBI().list(tempVisit);
        if (visits != null) {
            for (int t = 0; t < visits.size(); t++) {
                Sighting tempSighting = new Sighting();
                tempSighting.setLocationName(locationWL.getName());
                List<Sighting> sightings = app.getDBI().list(tempSighting);
                for (int i = 0; i < sightings.size(); i++) {
                    if (sightings.get(i).getLatitude() != null && sightings.get(i).getLongitude() != null) {
                        if (!sightings.get(i).getLatitude().equals(Latitudes.NONE) && !sightings.get(i).getLongitude().equals(Longitudes.NONE)) {
                            float lat = sightings.get(i).getLatDegrees();
                            lat = lat + sightings.get(i).getLatMinutes()/60f;
                            lat = lat + (sightings.get(i).getLatSecondsFloat()/60f)/60f;
                            if (sightings.get(i).getLatitude().equals(Latitudes.SOUTH))
                                lat = -1 * lat;
                            float lon = sightings.get(i).getLonDegrees();
                            lon = lon + sightings.get(i).getLonMinutes()/60f;
                            lon = lon + (sightings.get(i).getLonSecondsFloat()/60f)/60f;
                            if (sightings.get(i).getLongitude().equals(Longitudes.WEST))
                                lon = -1 * lon;
                            app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
                        }
                    }
                }
            }

        }
        app.getMapFrame().changeTitle("WildLog Map - All Sightings at Location: " + locationWL.getName());
        app.getMapFrame().showMap();
    }//GEN-LAST:event_btnMapSightingsActionPerformed

    private void rdbLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbLocationItemStateChanged
        if (locationWL.getName() != null) {
            if (rdbLocation.isSelected()) {
                tblElement.setModel(utilTableGenerator.getElementsForLocationTable(locationWL));
            }
            else {
                if  (tblVisit.getSelectedRowCount() == 1) {
                    tblElement.setModel(utilTableGenerator.getElementsForVisitTable(app.getDBI().find(new Visit((String)tblVisit.getValueAt(tblVisit.getSelectedRow(), 0)))));
                }
                else tblElement.setModel(new DefaultTableModel(new String[]{"Please selected a Visit"}, 0));
            }
            lblNumberOfElements.setText(Integer.toString(tblElement.getRowCount()));
            // Setup table column sizes
            resizeTables();
            // Sort rows for Element
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
        }
        else {
            lblNumberOfElements.setText("0");
        }
    }//GEN-LAST:event_rdbLocationItemStateChanged

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        rdbLocationItemStateChanged(null);
    }//GEN-LAST:event_tblVisitMouseReleased

    private void txtLatDegreesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatDegreesFocusGained
        txtLatDegrees.setSelectionStart(0);
        txtLatDegrees.setSelectionEnd(txtLatDegrees.getText().length());
    }//GEN-LAST:event_txtLatDegreesFocusGained

    private void txtLatMinutesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatMinutesFocusGained
        txtLatMinutes.setSelectionStart(0);
        txtLatMinutes.setSelectionEnd(txtLatMinutes.getText().length());
    }//GEN-LAST:event_txtLatMinutesFocusGained

    private void txtLatSecondsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatSecondsFocusGained
        txtLatSeconds.setSelectionStart(0);
        txtLatSeconds.setSelectionEnd(txtLatSeconds.getText().length());
    }//GEN-LAST:event_txtLatSecondsFocusGained

    private void txtLonDegreesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonDegreesFocusGained
        txtLonDegrees.setSelectionStart(0);
        txtLonDegrees.setSelectionEnd(txtLonDegrees.getText().length());
    }//GEN-LAST:event_txtLonDegreesFocusGained

    private void txtLonMinutesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonMinutesFocusGained
        txtLonMinutes.setSelectionStart(0);
        txtLonMinutes.setSelectionEnd(txtLonMinutes.getText().length());
    }//GEN-LAST:event_txtLonMinutesFocusGained

    private void txtLonSecondsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonSecondsFocusGained
        txtLonSeconds.setSelectionStart(0);
        txtLonSeconds.setSelectionEnd(txtLonSeconds.getText().length());
    }//GEN-LAST:event_txtLonSecondsFocusGained

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        Utils.openFile("LOCATION-" + locationWL.getName(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void tblVisitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoVisitActionPerformed(null);
    }//GEN-LAST:event_tblVisitKeyPressed

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoElementActionPerformed(null);
    }//GEN-LAST:event_tblElementKeyPressed

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoElementActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblVisitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoVisitActionPerformed(null);
        }
    }//GEN-LAST:event_tblVisitMouseClicked

    private void rdbDDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbDDItemStateChanged
        if (rdbDD.isSelected()) {
            try {
                txtLatDecimal.setText(Double.toString(Math.abs(LatLonConverter.getDecimalDegree((Latitudes)cmbLatitude.getSelectedItem(), Integer.parseInt(txtLatDegrees.getText()), Integer.parseInt(txtLatMinutes.getText()), Float.parseFloat(txtLatSeconds.getText())))));
                txtLonDecimal.setText(Double.toString(Math.abs(LatLonConverter.getDecimalDegree((Longitudes)cmbLongitude.getSelectedItem(), Integer.parseInt(txtLonDegrees.getText()), Integer.parseInt(txtLonMinutes.getText()), Float.parseFloat(txtLonSeconds.getText())))));
                // If the parsing worked then continue
                txtLatDegrees.setVisible(false);
                txtLonDegrees.setVisible(false);
                txtLatMinutes.setVisible(false);
                txtLonMinutes.setVisible(false);
                txtLatSeconds.setVisible(false);
                txtLonSeconds.setVisible(false);
                txtLatDecimal.setVisible(true);
                txtLonDecimal.setVisible(true);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The input format should be <integer> <integer> <decimal>", "Wrong Number Format", JOptionPane.INFORMATION_MESSAGE);
                rdbDMS.setSelected(true);
            }
        }
    }//GEN-LAST:event_rdbDDItemStateChanged

    private void rdbDMSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbDMSItemStateChanged
        if (rdbDMS.isSelected()) {
            try {
                txtLatDegrees.setText(Integer.toString(Math.abs(LatLonConverter.getDegrees((Latitudes)cmbLatitude.getSelectedItem(), Double.parseDouble(txtLatDecimal.getText())))));
                txtLonDegrees.setText(Integer.toString(Math.abs(LatLonConverter.getDegrees((Longitudes)cmbLongitude.getSelectedItem(), Double.parseDouble(txtLonDecimal.getText())))));
                txtLatMinutes.setText(Integer.toString(LatLonConverter.getMinutes((Latitudes)cmbLatitude.getSelectedItem(), Double.parseDouble(txtLatDecimal.getText()))));
                txtLonMinutes.setText(Integer.toString(LatLonConverter.getMinutes((Longitudes)cmbLongitude.getSelectedItem(), Double.parseDouble(txtLonDecimal.getText()))));
                txtLatSeconds.setText(new DecimalFormat("#.###").format(LatLonConverter.getSeconds((Latitudes)cmbLatitude.getSelectedItem(), Double.parseDouble(txtLatDecimal.getText()))));
                txtLonSeconds.setText(new DecimalFormat("#.###").format(LatLonConverter.getSeconds((Longitudes)cmbLongitude.getSelectedItem(), Double.parseDouble(txtLonDecimal.getText()))));
                // If the parsing worked then continue
                txtLatDegrees.setVisible(true);
                txtLonDegrees.setVisible(true);
                txtLatMinutes.setVisible(true);
                txtLonMinutes.setVisible(true);
                txtLatSeconds.setVisible(true);
                txtLonSeconds.setVisible(true);
                txtLatDecimal.setVisible(false);
                txtLonDecimal.setVisible(false);
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The input format should be <decimal>", "Wrong Number Format", JOptionPane.INFORMATION_MESSAGE);
                rdbDD.setSelected(true);
            }
        }
    }//GEN-LAST:event_rdbDMSItemStateChanged

    private void txtLatDecimalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatDecimalFocusGained
        txtLatDecimal.setSelectionStart(0);
        txtLatDecimal.setSelectionEnd(txtLatDecimal.getText().length());
    }//GEN-LAST:event_txtLatDecimalFocusGained

    private void txtLonDecimalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonDecimalFocusGained
        txtLonDecimal.setSelectionStart(0);
        txtLonDecimal.setSelectionEnd(txtLonDecimal.getText().length());
    }//GEN-LAST:event_txtLonDecimalFocusGained

    private void tblVisitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVisitKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblVisitMouseReleased(null);
    }//GEN-LAST:event_tblVisitKeyReleased

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        Utils.openFile(UtilsHTML.exportHTML(locationWL, app));
}//GEN-LAST:event_btnHTMLActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (locationWL.getName() != null) {
            if (locationWL.getName().length() > 0) {
                JFrame report = new ReportLocation(locationWL, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnReportActionPerformed


    private void resizeTables() {
        TableColumn column = null;
        for (int i = 0; i < tblVisit.getColumnModel().getColumnCount(); i++) {
            column = tblVisit.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(160);
            }
            else if (i == 1) {
                column.setPreferredWidth(45);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 2) {
                column.setPreferredWidth(45);
                column.setCellRenderer(new DateCellRenderer());
            }
            else if (i == 3) {
                column.setPreferredWidth(75);
            }
            else if (i == 4) {
                column.setPreferredWidth(30);
            }
            else if (i == 5) {
                column.setPreferredWidth(30);
            }
        }
        for (int i = 0; i < tblElement.getColumnModel().getColumnCount(); i++) {
            column = tblElement.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            }
            else if (i == 1) {
                column.setPreferredWidth(50);
            }
            else if (i == 2) {
                column.setPreferredWidth(50);
            }
        }
    }

    private void setupNumberOfImages() {
        List<Foto> fotos = app.getDBI().list(new Foto("LOCATION-" + locationWL.getName()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVisit;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteVisit;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnGoVisit;
    private javax.swing.JButton btnHTML;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnMapSightings;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cmbCatering;
    private javax.swing.JComboBox cmbGameRating;
    private javax.swing.JComboBox cmbHabitat;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLongitude;
    private javax.swing.JComboBox cmbProvince;
    private javax.swing.JComboBox cmbRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel lblNumberOfElements;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfVisits;
    private javax.swing.JPanel locationIncludes;
    private javax.swing.JList lstAccommodationType;
    private javax.swing.JRadioButton rdbDD;
    private javax.swing.JRadioButton rdbDMS;
    private javax.swing.JRadioButton rdbLocation;
    private javax.swing.JRadioButton rdbVisit;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDirections;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLatDecimal;
    private javax.swing.JTextField txtLatDegrees;
    private javax.swing.JTextField txtLatMinutes;
    private javax.swing.JTextField txtLatSeconds;
    private javax.swing.JTextField txtLonDecimal;
    private javax.swing.JTextField txtLonDegrees;
    private javax.swing.JTextField txtLonMinutes;
    private javax.swing.JTextField txtLonSeconds;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtWebsite;
    // End of variables declaration//GEN-END:variables
    
}
