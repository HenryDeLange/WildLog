/*
 * PanelVisit.java is part of WildLog
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.ui.util.UtilTableGenerator;
import wildlog.ui.util.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.util.ImageFilter;
import wildlog.ui.util.ImagePreview;

/**
 *
 * @author  henry.delange
 */
public class PanelSighting extends javax.swing.JPanel {
    private Visit visit;
    private Sighting sighting;
    //private JPanel owner;
    private UtilTableGenerator utilTableGenerator;
    private Element searchElement;
    private Location searchLocation;
    private int imageIndex;
    private WildLogApp app;
    private PanelNeedsRefreshWhenSightingAdded panelToRefresh;
    
    /** Creates new form PanelVisit */
    public PanelSighting(Sighting inSighting, Visit inVisit) {
        app = (WildLogApp) Application.getInstance();
        sighting = inSighting;
        visit = inVisit;
        utilTableGenerator = new UtilTableGenerator();
        searchElement = new Element();
        searchLocation = new Location();
        initComponents();
        if (sighting.getLocation() != null)
            cmbSubArea.setModel(new DefaultComboBoxModel(sighting.getLocation().getSubAreas().toArray()));
        imageIndex = 0;
        setupSightingInfo();
        tblElement.getTableHeader().setReorderingAllowed(false);
        tblLocation.getTableHeader().setReorderingAllowed(false);
        tblVisit.getTableHeader().setReorderingAllowed(false);
    }

    public PanelSighting(Sighting inSighting, Visit inVisit, PanelNeedsRefreshWhenSightingAdded inPanelToRefresh) {
        this(inSighting, inVisit);
        panelToRefresh = inPanelToRefresh;
    }
    
    /*
    @Override
    public boolean equals(Object inObject) {
        if (getClass() != inObject.getClass()) return false;
        final PanelSighting inPanel = (PanelSighting) inObject;
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
    */
    
    private void setupSightingInfo() {
        if (sighting != null) {
            dtpSightingDate.setDate(sighting.getDate());
            cmbAreaType.setSelectedItem(sighting.getAreaType());
            cmbCertainty.setSelectedItem(sighting.getCertainty());
            txtDetails.setText(sighting.getDetails());
            cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
            if (!sighting.getSubArea().equals(""))
                cmbSubArea.setSelectedItem(sighting.getSubArea());
            else
                cmbSubArea.setSelectedItem("None");
            txtNumberOfElements.setText(Integer.toString(sighting.getNumberOfElements()));
            cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
            cmbViewRating.setSelectedItem(sighting.getViewRating());
            cmbWeather.setSelectedItem(sighting.getWeather());
            cmbLatitude.setSelectedItem(sighting.getLatitude());
            txtLatDegrees.setText(Integer.toString(sighting.getLatDegrees()));
            txtLatMinutes.setText(Integer.toString(sighting.getLatMinutes()));
            txtLatSeconds.setText(Integer.toString(sighting.getLatSeconds()));
            cmbLongitude.setSelectedItem(sighting.getLongitude());
            txtLonDegrees.setText(Integer.toString(sighting.getLonDegrees()));
            txtLonMinutes.setText(Integer.toString(sighting.getLonMinutes()));
            txtLonSeconds.setText(Integer.toString(sighting.getLonSeconds()));
            lblCounter.setText(Long.toString(sighting.getSightingCounter()));

            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            if (sighting.getFotos() != null)
                if (sighting.getFotos().size() > 0)
                    setupFotos(0);

            if (sighting.getElement() != null) {
                lblElementName.setText(sighting.getElement().getPrimaryName());
                if (sighting.getElement().getFotos() != null)
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getElement().getFotos().get(0).getFileLocation()), 100));
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblElementName.setText("...");
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }

            if (sighting.getLocation() != null) {
                lblLocationName.setText(sighting.getLocation().getName());
                if (sighting.getLocation().getFotos() != null)
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getLocation().getFotos().get(0).getFileLocation()), 100));
                else
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblLocationName.setText("...");
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }

            if (visit != null)
                lblVisitName.setText(visit.getName());
            else
                lblVisitName.setText("...");
        }
        else {
            System.out.println("Sighting was not found - null");
            sighting = new Sighting();
            visit.getSightings().add(sighting);
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sightingIncludes = new javax.swing.JPanel();
        lblVisitName = new javax.swing.JLabel();
        lblElementName = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
        btnUpdateSighting = new javax.swing.JButton();
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
        lblElementImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        chkElementTypeFilter = new javax.swing.JCheckBox();
        cmbElementType = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        cmbLatitude = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmbLongitude = new javax.swing.JComboBox();
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
        btnSetMainImage = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cmbEvidence = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        lblLocationName = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        lblElement = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        txtSearchLocation = new javax.swing.JTextField();
        btnSearchLocation = new javax.swing.JButton();
        lblVisit = new javax.swing.JLabel();
        lblLocationImage = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        chkSearchLocationDirect = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        lblCounter = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1020, 630));
        setMinimumSize(new java.awt.Dimension(1020, 630));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1020, 630));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        sightingIncludes.setMaximumSize(new java.awt.Dimension(1020, 630));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(1020, 630));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(1020, 630));
        sightingIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelSighting.class);
        lblVisitName.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        lblVisitName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitName.setText(resourceMap.getString("lblVisitName.text")); // NOI18N
        lblVisitName.setName("lblVisitName"); // NOI18N
        sightingIncludes.add(lblVisitName, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 0, 160, 20));

        lblElementName.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementName.setText(resourceMap.getString("lblElementName.text")); // NOI18N
        lblElementName.setName("lblElementName"); // NOI18N
        sightingIncludes.add(lblElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 180, 20));

        jSeparator8.setName("jSeparator8"); // NOI18N
        sightingIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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

        sightingIncludes.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 290, 320));

        jSeparator9.setName("jSeparator9"); // NOI18N
        sightingIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        sightingIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, -1, 20));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        sightingIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 695, 10));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        sightingIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 360, 280, 10));

        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

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
        sightingIncludes.add(btnUpdateSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 30, 110, 60));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        sightingIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, 30, -1));

        dtpSightingDate.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("dtpSightingDate.border.lineColor"), 3, true)); // NOI18N
        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        sightingIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 445, 270, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        sightingIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 540, -1, -1));

        cmbAreaType.setMaximumRowCount(11);
        cmbAreaType.setModel(new DefaultComboBoxModel(AreaType.values()));
        cmbAreaType.setSelectedItem(sighting.getAreaType());
        cmbAreaType.setName("cmbAreaType"); // NOI18N
        sightingIncludes.add(cmbAreaType, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 540, 270, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        sightingIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 480, -1, -1));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        sightingIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 510, -1, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        sightingIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 510, -1, -1));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        sightingIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, -1, -1));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        sightingIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, -1, -1));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        sightingIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 430, -1, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDetails.setColumns(20);
        txtDetails.setFont(resourceMap.getFont("txtDetails.font")); // NOI18N
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(sighting.getDetails());
        txtDetails.setWrapStyleWord(true);
        txtDetails.setName("txtDetails"); // NOI18N
        jScrollPane2.setViewportView(txtDetails);

        sightingIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 450, 280, 120));

        txtNumberOfElements.setText(String.valueOf(sighting.getNumberOfElements()));
        txtNumberOfElements.setName("txtNumberOfElements"); // NOI18N
        sightingIncludes.add(txtNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 510, 90, -1));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setName("cmbWeather"); // NOI18N
        sightingIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 480, 270, -1));

        cmbTimeOfDay.setMaximumRowCount(9);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N
        sightingIncludes.add(cmbTimeOfDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 510, 270, -1));

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setName("cmbViewRating"); // NOI18N
        sightingIncludes.add(cmbViewRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 510, 100, -1));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        sightingIncludes.add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 480, 270, -1));

        jLabel14.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        sightingIncludes.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 20));

        lblElementImage.setText(resourceMap.getString("lblElementImage.text")); // NOI18N
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setPreferredSize(new java.awt.Dimension(100, 100));
        sightingIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 330, -1, -1));

        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        sightingIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        sightingIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

        chkElementTypeFilter.setName("chkElementTypeFilter"); // NOI18N
        chkElementTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkElementTypeFilterActionPerformed(evt);
            }
        });
        sightingIncludes.add(chkElementTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 20));

        cmbElementType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementType.setEnabled(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        sightingIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 170, -1));

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        sightingIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 370, -1, 20));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        sightingIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 370, 70, -1));

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        sightingIncludes.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 370, -1, 20));

        cmbLongitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLongitude.setSelectedIndex(2);
        cmbLongitude.setName("cmbLongitude"); // NOI18N
        sightingIncludes.add(cmbLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 370, 70, -1));

        txtLatDegrees.setText(Integer.toString(sighting.getLatDegrees()));
        txtLatDegrees.setName("txtLatDegrees"); // NOI18N
        sightingIncludes.add(txtLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 400, 30, -1));

        txtLatMinutes.setText(Integer.toString(sighting.getLatMinutes()));
        txtLatMinutes.setName("txtLatMinutes"); // NOI18N
        sightingIncludes.add(txtLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 400, 30, -1));

        txtLatSeconds.setText(Integer.toString(sighting.getLatSeconds()));
        txtLatSeconds.setName("txtLatSeconds"); // NOI18N
        sightingIncludes.add(txtLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 400, 30, -1));

        txtLonDegrees.setText(Integer.toString(sighting.getLonDegrees()));
        txtLonDegrees.setName("txtLonDegrees"); // NOI18N
        sightingIncludes.add(txtLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 400, 30, -1));

        txtLonMinutes.setText(Integer.toString(sighting.getLonMinutes()));
        txtLonMinutes.setName("txtLonMinutes"); // NOI18N
        sightingIncludes.add(txtLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 400, 30, -1));

        txtLonSeconds.setText(Integer.toString(sighting.getLonSeconds()));
        txtLonSeconds.setName("txtLonSeconds"); // NOI18N
        sightingIncludes.add(txtLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 400, 30, -1));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setName("txtSearch"); // NOI18N
        sightingIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 200, -1));

        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 80, -1));

        chkSearchDirect.setSelected(true);
        chkSearchDirect.setText(resourceMap.getString("chkSearchDirect.text")); // NOI18N
        chkSearchDirect.setName("chkSearchDirect"); // NOI18N
        sightingIncludes.add(chkSearchDirect, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, -1, -1));

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        sightingIncludes.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 450, -1, -1));

        cmbSubArea.setModel(new DefaultComboBoxModel());
        cmbSubArea.setSelectedItem(sighting.getSubArea());
        cmbSubArea.setName("cmbSubArea"); // NOI18N
        sightingIncludes.add(cmbSubArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, 270, -1));

        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 326, 100, -1));

        jSeparator4.setForeground(resourceMap.getColor("jSeparator4.foreground")); // NOI18N
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setName("jSeparator4"); // NOI18N
        sightingIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 20, 30, 420));

        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 326, 100, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        sightingIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 540, -1, -1));

        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setName("cmbEvidence"); // NOI18N
        sightingIncludes.add(cmbEvidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 540, 270, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        sightingIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 0, 30, 20));

        lblLocationName.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        lblLocationName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationName.setName("lblLocationName"); // NOI18N
        sightingIncludes.add(lblLocationName, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 180, 20));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setModel(utilTableGenerator.getShortLocationTable(searchLocation));
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        jScrollPane15.setViewportView(tblLocation);

        sightingIncludes.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 250, 160));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setModel(new DefaultTableModel());
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        jScrollPane14.setViewportView(tblVisit);

        sightingIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 270, 250, 160));

        lblElement.setText(resourceMap.getString("lblElement.text")); // NOI18N
        lblElement.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true)); // NOI18N
        lblElement.setName("lblElement"); // NOI18N
        sightingIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        lblLocation.setText(resourceMap.getString("lblLocation.text")); // NOI18N
        lblLocation.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("lblLocation.border.lineColor"), 3, true)); // NOI18N
        lblLocation.setName("lblLocation"); // NOI18N
        sightingIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, -1, -1));

        txtSearchLocation.setText(resourceMap.getString("txtSearchLocation.text")); // NOI18N
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N
        sightingIncludes.add(txtSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 160, -1));

        btnSearchLocation.setText(resourceMap.getString("btnSearchLocation.text")); // NOI18N
        btnSearchLocation.setName("btnSearchLocation"); // NOI18N
        btnSearchLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchLocationActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 80, -1));

        lblVisit.setText(resourceMap.getString("lblVisit.text")); // NOI18N
        lblVisit.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("lblVisit.border.lineColor"), 3, true)); // NOI18N
        lblVisit.setName("lblVisit"); // NOI18N
        sightingIncludes.add(lblVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 250, -1, -1));

        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setPreferredSize(new java.awt.Dimension(100, 100));
        sightingIncludes.add(lblLocationImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, -1, -1));

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        sightingIncludes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 120, -1, -1));

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        sightingIncludes.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 310, -1, -1));

        chkSearchLocationDirect.setSelected(true);
        chkSearchLocationDirect.setText(resourceMap.getString("chkSearchLocationDirect.text")); // NOI18N
        chkSearchLocationDirect.setName("chkSearchLocationDirect"); // NOI18N
        sightingIncludes.add(chkSearchLocationDirect, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, -1, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        sightingIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 580, -1, 20));

        jSeparator5.setName("jSeparator5"); // NOI18N
        sightingIncludes.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 570, 350, 10));

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setName("jSeparator6"); // NOI18N
        sightingIncludes.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 570, 10, 40));

        lblCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCounter.setText(resourceMap.getString("lblCounter.text")); // NOI18N
        lblCounter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblCounter.setName("lblCounter"); // NOI18N
        sightingIncludes.add(lblCounter, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 580, 90, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        if (sighting.getLocation() != null && sighting.getElement() != null && visit != null && dtpSightingDate.getDate() != null) {
            sighting.setDate(dtpSightingDate.getDate());
            sighting.setAreaType((AreaType)cmbAreaType.getSelectedItem());
            sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
            sighting.setDetails(txtDetails.getText());
            sighting.setSightingEvidence((SightingEvidence)cmbEvidence.getSelectedItem());
            try {
                sighting.setNumberOfElements(Integer.parseInt(txtNumberOfElements.getText()));
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
            sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
            sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
            sighting.setWeather((Weather)cmbWeather.getSelectedItem());
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
            sighting.setLongitude((Longitudes)cmbLongitude.getSelectedItem());
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

            // Add and Save the visit
            if (app.getDBI().isSightingUnique(sighting) == true) {
                if (!visit.getSightings().contains(sighting)) visit.getSightings().add(sighting);
                if (app.getDBI().createOrUpdate(visit) == true) {
                    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelSighting.class);
                    lblElement.setBorder(new LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true));
                    lblLocation.setBorder(new LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true));
                    lblVisit.setBorder(new LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true));
                    dtpSightingDate.setBorder(new LineBorder(resourceMap.getColor("lblElement.border.lineColor"), 3, true));
                    // Premare to close dialog
                    if (panelToRefresh != null) {
                        panelToRefresh.refreshTableForSightings();
                    }
                    // Close the dialog
                    JDialog dialog = (JDialog)getParent().getParent().getParent().getParent();
                    dialog.dispose();
                }
                else {
                    lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                    lblVisitName.setText(visit.getName() + "_not_unique");
                }
            }
            else {
                lblElement.setBorder(new LineBorder(Color.RED, 3, true));
                lblLocation.setBorder(new LineBorder(Color.RED, 3, true));
                lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
            }

            lblElementName.setText(sighting.getElement().getPrimaryName());
            lblLocationName.setText(sighting.getLocation().getName());
            lblVisitName.setText(visit.getName());
        }
        else {
            if (sighting.getElement() == null)
                lblElement.setBorder(new LineBorder(Color.RED, 3, true));
            if (sighting.getLocation() == null)
                lblLocation.setBorder(new LineBorder(Color.RED, 3, true));
            if (visit == null)
                lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
            if (dtpSightingDate.getDate() == null)
                dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
        }
}//GEN-LAST:event_btnUpdateSightingActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        int result = fileChooser.showOpenDialog(this);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            File fromFile = fileChooser.getSelectedFile();
            File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar + "Sightings" + File.separatorChar + sighting.toString());
            toDir.mkdirs();
            File toFile = new File(toDir.getAbsolutePath() + File.separatorChar  + fromFile.getName());
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
                sighting.getFotos().add(new Foto(toFile.getName(), toFile.getAbsolutePath()));
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
                sighting.setElement(app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0))));
                lblElementName.setText(sighting.getElement().getPrimaryName());
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
                if (sighting.getElement().getFotos() != null)
                    if (sighting.getElement().getFotos().size() > 0)
                        lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getElement().getFotos().get(0).getFileLocation()), 100));
            }
            else {
                sighting.setElement(null);
                lblElementName.setText("...");
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            if (imageIndex < sighting.getFotos().size() - 1) setupFotos(++imageIndex);
            else {
                imageIndex = 0;
                setupFotos(imageIndex);
            }
        }
}//GEN-LAST:event_btnNextImageActionPerformed

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

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (tblVisit.getSelectedRow() >= 0) {
            visit = app.getDBI().find(new Visit(tblVisit.getValueAt(tblVisit.getSelectedRow(), 0).toString()));
            lblVisitName.setText(visit.getName());
        }
        else {
            lblVisitName.setText("...");
        }
}//GEN-LAST:event_tblVisitMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (tblLocation.getSelectedRow() >= 0) {
            sighting.setLocation(app.getDBI().find(new Location(tblLocation.getValueAt(tblLocation.getSelectedRow(), 0).toString())));
            tblVisit.setModel(utilTableGenerator.getShortVisitTable(sighting.getLocation()));
            lblLocationName.setText(sighting.getLocation().getName());
            visit = null;
            lblVisitName.setText("...");
            if (sighting.getLocation().getFotos() != null)
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getLocation().getFotos().get(0).getFileLocation()), 100));
            else
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
        else {
            lblLocationName.setText("...");
        }
}//GEN-LAST:event_tblLocationMouseReleased

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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchElement = new Element();
        if (chkElementTypeFilter.isSelected())
            searchElement.setType((ElementType)cmbElementType.getSelectedItem());

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

    private void btnSearchLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchLocationActionPerformed
        searchLocation = new Location();
        if (chkSearchLocationDirect.isSelected()) {
            searchLocation.setName(txtSearchLocation.getText());
            tblLocation.setModel(utilTableGenerator.getShortLocationTable(searchLocation));
        }
        else {
            DefaultTableModel model = utilTableGenerator.getShortLocationTable(searchLocation);
            for (int t = 0; t < model.getRowCount(); t++) {
                if (!((String)model.getValueAt(t, 0)).contains(txtSearchLocation.getText())) {
                    model.removeRow(t--);
                }
            }
            tblLocation.setModel(model);
        }
    }//GEN-LAST:event_btnSearchLocationActionPerformed

    private void setupFotos(int inIndex) {
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getFotos().get(inIndex).getFileLocation()), 300));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchLocation;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdateSighting;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JCheckBox chkElementTypeFilter;
    private javax.swing.JCheckBox chkSearchDirect;
    private javax.swing.JCheckBox chkSearchLocationDirect;
    private javax.swing.JComboBox cmbAreaType;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbEvidence;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLongitude;
    private javax.swing.JComboBox cmbSubArea;
    private javax.swing.JComboBox cmbTimeOfDay;
    private javax.swing.JComboBox cmbViewRating;
    private javax.swing.JComboBox cmbWeather;
    private org.jdesktop.swingx.JXDatePicker dtpSightingDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblCounter;
    private javax.swing.JLabel lblElement;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblElementName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JLabel lblLocationName;
    private javax.swing.JLabel lblVisit;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JPanel sightingIncludes;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JTextField txtLatDegrees;
    private javax.swing.JTextField txtLatMinutes;
    private javax.swing.JTextField txtLatSeconds;
    private javax.swing.JTextField txtLonDegrees;
    private javax.swing.JTextField txtLonMinutes;
    private javax.swing.JTextField txtLonSeconds;
    private javax.swing.JTextField txtNumberOfElements;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchLocation;
    // End of variables declaration//GEN-END:variables
    
}
