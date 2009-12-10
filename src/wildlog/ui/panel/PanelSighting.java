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
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.WildLogApp;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeFormat;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;

/**
 *
 * @author  henry.delange
 */
public class PanelSighting extends javax.swing.JPanel {
    private Location location;
    private Visit visit;
    private Visit oldVisit;
    private Element element;
    private Sighting sighting;
    private UtilTableGenerator utilTableGenerator;
    private Element searchElement;
    private Location searchLocation;
    private int imageIndex;
    private WildLogApp app;
    private PanelNeedsRefreshWhenSightingAdded panelToRefresh;
    private boolean treatAsNewSighting;
    private boolean disableEditing = false;
    
    /** Creates new form PanelVisit */
    public PanelSighting(Sighting inSighting, Location inLocation, Visit inVisit, Element inElement, boolean inTreatAsNewSighting, boolean inDisableEditing) {
        sighting = inSighting;
        treatAsNewSighting = inTreatAsNewSighting;
        disableEditing = inDisableEditing;
        if (sighting != null) {
            // Initiate all objects
            app = (WildLogApp) Application.getInstance();
            location = inLocation;
            visit = inVisit;
            oldVisit = inVisit;
            element = inElement;
            utilTableGenerator = new UtilTableGenerator();
            searchElement = new Element();
            searchLocation = new Location();
            imageIndex = 0;
            // Auto-generated code
            initComponents();
            // Setup Dropdown Boxes
            if (location != null)
                cmbSubArea.setModel(new DefaultComboBoxModel(location.getSubAreas().toArray()));
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
            tblLocation.getRowSorter().setSortKeys(tempList);
            // Setup default values for tables
            if (location != null) {
                int select = -1;
                for (int t = 0; t < tblLocation.getModel().getRowCount(); t++) {
                    if (tblLocation.getValueAt(t, 0).equals(location.getName()))
                        select = t;
                }
                if (select >= 0) {
                    tblLocation.getSelectionModel().setSelectionInterval(select, select);
                    if (select > 3)
                        tblLocation.scrollRectToVisible(tblLocation.getCellRect(select-3, 0, true));
                }
            }
            if (element != null) {
                int select = -1;
                for (int t = 0; t < tblElement.getModel().getRowCount(); t++) {
                    if (tblElement.getValueAt(t, 0).equals(element.getPrimaryName()))
                        select = t;
                }
                if (select >= 0) {
                    tblElement.getSelectionModel().setSelectionInterval(select, select);
                    if (select > 5)
                        tblElement.scrollRectToVisible(tblElement.getCellRect(select-5, 0, true));
                }
            }
            if (location != null && visit != null) {
                // Build the table
                tblVisit.setModel(utilTableGenerator.getVeryShortVisitTable(location));
                // Sort the table
                tblVisit.getRowSorter().setSortKeys(tempList);
                // Select the visit
                int select = -1;
                for (int t = 0; t < tblVisit.getModel().getRowCount(); t++) {
                    if (tblVisit.getValueAt(t, 0).equals(visit.getName()))
                        select = t;
                }
                if (select >= 0) {
                    tblVisit.getSelectionModel().setSelectionInterval(select, select);
                    if (select > 2)
                        tblVisit.scrollRectToVisible(tblVisit.getCellRect(select-2, 0, true));
                }
            }
            if (location != null) {
                if (location.getFotos().size() > 0)
                    Utils.setupFoto(location, 0, lblLocationImage, 100);
                else
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            if (element != null) {
                if (element.getFotos().size() > 0)
                    Utils.setupFoto(element, 0, lblElementImage, 100);
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            //Setup Tables
            tblElement.getTableHeader().setReorderingAllowed(false);
            tblLocation.getTableHeader().setReorderingAllowed(false);
            tblVisit.getTableHeader().setReorderingAllowed(false);
            resizeTalbes();
            // Setup default values for input fields
            if (treatAsNewSighting) {
                cmbCertainty.setSelectedItem(Certainty.SURE);
                cmbEvidence.setSelectedItem(SightingEvidence.SEEN);
                txtNumberOfElements.setText("");
                cmbViewRating.setSelectedItem(ViewRating.NORMAL);
                //cmbLatitude.setSelectedItem(Latitudes.SOUTH);
                txtLatDegrees.setText("");
                txtLatMinutes.setText("");
                txtLatSeconds.setText("");
                //cmbLongitude.setSelectedItem(Longitudes.EAST);
                txtLonDegrees.setText("");
                txtLonMinutes.setText("");
                txtLonSeconds.setText("");
            }
            else {
                // Setup the Sighting info
                setupSightingInfo();
            }
        }
    }

    public PanelSighting(Sighting inSighting, Location inLocation, Visit inVisit, Element inElement, PanelNeedsRefreshWhenSightingAdded inPanelToRefresh, boolean inTreatAsNewSighting, boolean inDisableEditing) {
        this(inSighting, inLocation, inVisit, inElement, inTreatAsNewSighting, inDisableEditing);
        panelToRefresh = inPanelToRefresh;
    }

    
    private void setupSightingInfo() {
        if (sighting != null) {
            dtpSightingDate.setDate(sighting.getDate());
            txtHours.setText("" + sighting.getDate().getHours());
            txtMinutes.setText("" + sighting.getDate().getMinutes());
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

            if (sighting.getFotos().size() > 0)
                Utils.setupFoto(sighting, imageIndex, lblImage, 300);
            else
                lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
            setupNumberOfImages();
        }
        else {
            System.out.println("No sighting provided...");
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
        btnUpdateSighting = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnUploadImage = new javax.swing.JButton();
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
        lblElementImage = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
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
        jLabel20 = new javax.swing.JLabel();
        cmbSubArea = new javax.swing.JComboBox();
        btnDeleteImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cmbEvidence = new javax.swing.JComboBox();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblVisit = new javax.swing.JTable();
        lblElement = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        txtSearchLocation = new javax.swing.JTextField();
        lblVisit = new javax.swing.JLabel();
        lblLocationImage = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnSearchLocation = new javax.swing.JButton();
        lblNumberOfImages = new javax.swing.JLabel();
        txtHours = new javax.swing.JTextField();
        txtMinutes = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmbTimeFormat = new javax.swing.JComboBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelSighting.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setMaximumSize(new java.awt.Dimension(965, 595));
        setMinimumSize(new java.awt.Dimension(965, 595));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(965, 595));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        sightingIncludes.setBackground(resourceMap.getColor("sightingIncludes.background")); // NOI18N
        sightingIncludes.setMaximumSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setMinimumSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setName("sightingIncludes"); // NOI18N
        sightingIncludes.setPreferredSize(new java.awt.Dimension(965, 575));
        sightingIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnUpdateSighting.setBackground(resourceMap.getColor("btnUpdateSighting.background")); // NOI18N
        btnUpdateSighting.setIcon(resourceMap.getIcon("btnUpdateSighting.icon")); // NOI18N
        btnUpdateSighting.setText(resourceMap.getString("btnUpdateSighting.text")); // NOI18N
        btnUpdateSighting.setToolTipText(resourceMap.getString("btnUpdateSighting.toolTipText")); // NOI18N
        btnUpdateSighting.setEnabled(!disableEditing);
        btnUpdateSighting.setName("btnUpdateSighting"); // NOI18N
        btnUpdateSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSightingActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUpdateSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 190, 110, 70));

        jSeparator8.setName("jSeparator8"); // NOI18N
        sightingIncludes.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setFont(resourceMap.getFont("tblElement.font")); // NOI18N
        tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement, false));
        tblElement.setEnabled(!disableEditing);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        jScrollPane13.setViewportView(tblElement);

        sightingIncludes.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 290, 310));

        jSeparator9.setName("jSeparator9"); // NOI18N
        sightingIncludes.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        sightingIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 360, 280, 10));

        btnPreviousImage.setBackground(resourceMap.getColor("btnPreviousImage.background")); // NOI18N
        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
        btnPreviousImage.setName("btnPreviousImage"); // NOI18N
        btnPreviousImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 300, 40, 50));

        btnNextImage.setBackground(resourceMap.getColor("btnNextImage.background")); // NOI18N
        btnNextImage.setIcon(resourceMap.getIcon("btnNextImage.icon")); // NOI18N
        btnNextImage.setText(resourceMap.getString("btnNextImage.text")); // NOI18N
        btnNextImage.setToolTipText(resourceMap.getString("btnNextImage.toolTipText")); // NOI18N
        btnNextImage.setName("btnNextImage"); // NOI18N
        btnNextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 300, 40, 50));

        btnUploadImage.setBackground(resourceMap.getColor("btnUploadImage.background")); // NOI18N
        btnUploadImage.setIcon(resourceMap.getIcon("btnUploadImage.icon")); // NOI18N
        btnUploadImage.setText(resourceMap.getString("btnUploadImage.text")); // NOI18N
        btnUploadImage.setToolTipText(resourceMap.getString("btnUploadImage.toolTipText")); // NOI18N
        btnUploadImage.setEnabled(!disableEditing);
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 300, 220, -1));

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        sightingIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 290, 30, -1));

        dtpSightingDate.setDate(sighting.getDate());
        dtpSightingDate.setEnabled(!disableEditing);
        dtpSightingDate.setName("dtpSightingDate"); // NOI18N
        sightingIncludes.add(dtpSightingDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(333, 290, 130, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        sightingIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 480, -1, -1));

        cmbAreaType.setMaximumRowCount(11);
        cmbAreaType.setModel(new DefaultComboBoxModel(AreaType.values()));
        cmbAreaType.setSelectedItem(sighting.getAreaType());
        cmbAreaType.setEnabled(!disableEditing);
        cmbAreaType.setName("cmbAreaType"); // NOI18N
        sightingIncludes.add(cmbAreaType, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 480, 260, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        sightingIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 450, -1, -1));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        sightingIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 510, -1, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        sightingIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 290, -1, -1));

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        sightingIncludes.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 540, -1, -1));

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        sightingIncludes.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 390, -1, -1));

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        sightingIncludes.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 420, -1, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDetails.setColumns(20);
        txtDetails.setFont(resourceMap.getFont("txtDetails.font")); // NOI18N
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(sighting.getDetails());
        txtDetails.setWrapStyleWord(true);
        txtDetails.setEnabled(!disableEditing);
        txtDetails.setName("txtDetails"); // NOI18N
        jScrollPane2.setViewportView(txtDetails);

        sightingIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 440, 300, 130));

        txtNumberOfElements.setText(String.valueOf(sighting.getNumberOfElements()));
        txtNumberOfElements.setEnabled(!disableEditing);
        txtNumberOfElements.setName("txtNumberOfElements"); // NOI18N
        txtNumberOfElements.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNumberOfElementsFocusGained(evt);
            }
        });
        sightingIncludes.add(txtNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 290, 80, -1));

        cmbWeather.setModel(new DefaultComboBoxModel(Weather.values()));
        cmbWeather.setSelectedItem(sighting.getWeather());
        cmbWeather.setEnabled(!disableEditing);
        cmbWeather.setName("cmbWeather"); // NOI18N
        sightingIncludes.add(cmbWeather, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 450, 260, -1));

        cmbTimeOfDay.setMaximumRowCount(9);
        cmbTimeOfDay.setModel(new DefaultComboBoxModel(ActiveTimeSpesific.values()));
        cmbTimeOfDay.setSelectedItem(sighting.getTimeOfDay());
        cmbTimeOfDay.setEnabled(!disableEditing);
        cmbTimeOfDay.setName("cmbTimeOfDay"); // NOI18N
        sightingIncludes.add(cmbTimeOfDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 540, 170, -1));

        cmbViewRating.setModel(new DefaultComboBoxModel(ViewRating.values()));
        cmbViewRating.setSelectedItem(sighting.getViewRating());
        cmbViewRating.setEnabled(!disableEditing);
        cmbViewRating.setName("cmbViewRating"); // NOI18N
        sightingIncludes.add(cmbViewRating, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 510, 260, -1));

        cmbCertainty.setModel(new DefaultComboBoxModel(Certainty.values()));
        cmbCertainty.setSelectedItem(sighting.getCertainty());
        cmbCertainty.setEnabled(!disableEditing);
        cmbCertainty.setName("cmbCertainty"); // NOI18N
        sightingIncludes.add(cmbCertainty, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 390, 260, -1));

        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setText(resourceMap.getString("lblElementImage.text")); // NOI18N
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblElementImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 180, -1, -1));

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, -1));

        chkElementTypeFilter.setBackground(resourceMap.getColor("chkElementTypeFilter.background")); // NOI18N
        chkElementTypeFilter.setEnabled(!disableEditing);
        chkElementTypeFilter.setName("chkElementTypeFilter"); // NOI18N
        chkElementTypeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkElementTypeFilterActionPerformed(evt);
            }
        });
        sightingIncludes.add(chkElementTypeFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, -1, 20));

        cmbElementType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbElementType.setEnabled(false);
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });
        sightingIncludes.add(cmbElementType, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 170, -1));

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        sightingIncludes.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 330, -1, 20));

        cmbLatitude.setModel(new DefaultComboBoxModel(Latitudes.values()));
        cmbLatitude.setSelectedIndex(2);
        cmbLatitude.setEnabled(!disableEditing);
        cmbLatitude.setName("cmbLatitude"); // NOI18N
        sightingIncludes.add(cmbLatitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, 90, -1));

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        sightingIncludes.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 330, -1, 20));

        cmbLongitude.setModel(new DefaultComboBoxModel(Longitudes.values()));
        cmbLongitude.setSelectedIndex(2);
        cmbLongitude.setEnabled(!disableEditing);
        cmbLongitude.setName("cmbLongitude"); // NOI18N
        sightingIncludes.add(cmbLongitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 330, 90, -1));

        txtLatDegrees.setText(Integer.toString(sighting.getLatDegrees()));
        txtLatDegrees.setEnabled(!disableEditing);
        txtLatDegrees.setName("txtLatDegrees"); // NOI18N
        txtLatDegrees.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatDegreesFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLatDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 360, 30, -1));

        txtLatMinutes.setText(Integer.toString(sighting.getLatMinutes()));
        txtLatMinutes.setEnabled(!disableEditing);
        txtLatMinutes.setName("txtLatMinutes"); // NOI18N
        txtLatMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatMinutesFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLatMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, 30, -1));

        txtLatSeconds.setText(Integer.toString(sighting.getLatSeconds()));
        txtLatSeconds.setEnabled(!disableEditing);
        txtLatSeconds.setName("txtLatSeconds"); // NOI18N
        txtLatSeconds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLatSecondsFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLatSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 360, 30, -1));

        txtLonDegrees.setText(Integer.toString(sighting.getLonDegrees()));
        txtLonDegrees.setEnabled(!disableEditing);
        txtLonDegrees.setName("txtLonDegrees"); // NOI18N
        txtLonDegrees.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonDegreesFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLonDegrees, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 360, 30, -1));

        txtLonMinutes.setText(Integer.toString(sighting.getLonMinutes()));
        txtLonMinutes.setEnabled(!disableEditing);
        txtLonMinutes.setName("txtLonMinutes"); // NOI18N
        txtLonMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonMinutesFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLonMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 360, 30, -1));

        txtLonSeconds.setText(Integer.toString(sighting.getLonSeconds()));
        txtLonSeconds.setEnabled(!disableEditing);
        txtLonSeconds.setName("txtLonSeconds"); // NOI18N
        txtLonSeconds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLonSecondsFocusGained(evt);
            }
        });
        sightingIncludes.add(txtLonSeconds, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 360, 30, -1));

        txtSearch.setText(resourceMap.getString("txtSearch.text")); // NOI18N
        txtSearch.setEnabled(!disableEditing);
        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        sightingIncludes.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 235, 200, -1));

        btnSearch.setBackground(resourceMap.getColor("btnSearch.background")); // NOI18N
        btnSearch.setIcon(resourceMap.getIcon("btnSearch.icon")); // NOI18N
        btnSearch.setText(resourceMap.getString("btnSearch.text")); // NOI18N
        btnSearch.setToolTipText(resourceMap.getString("btnSearch.toolTipText")); // NOI18N
        btnSearch.setEnabled(!disableEditing);
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 235, 90, -1));

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N
        sightingIncludes.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 370, -1, -1));

        cmbSubArea.setModel(new DefaultComboBoxModel());
        cmbSubArea.setSelectedItem(sighting.getSubArea());
        cmbSubArea.setEnabled(!disableEditing);
        cmbSubArea.setName("cmbSubArea"); // NOI18N
        sightingIncludes.add(cmbSubArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 390, 300, -1));

        btnDeleteImage.setBackground(resourceMap.getColor("btnDeleteImage.background")); // NOI18N
        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setEnabled(!disableEditing);
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 326, 90, -1));

        btnSetMainImage.setBackground(resourceMap.getColor("btnSetMainImage.background")); // NOI18N
        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setEnabled(!disableEditing);
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 326, 90, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        sightingIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 420, -1, -1));

        cmbEvidence.setModel(new DefaultComboBoxModel(SightingEvidence.values()));
        cmbEvidence.setSelectedItem(sighting.getSightingEvidence());
        cmbEvidence.setEnabled(!disableEditing);
        cmbEvidence.setName("cmbEvidence"); // NOI18N
        sightingIncludes.add(cmbEvidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 420, 260, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setFont(resourceMap.getFont("tblLocation.font")); // NOI18N
        tblLocation.setModel(utilTableGenerator.getShortLocationTable(searchLocation, false));
        tblLocation.setEnabled(!disableEditing);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblLocationMouseReleased(evt);
            }
        });
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        jScrollPane15.setViewportView(tblLocation);

        sightingIncludes.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 45, 290, 140));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        tblVisit.setAutoCreateRowSorter(true);
        tblVisit.setFont(resourceMap.getFont("tblVisit.font")); // NOI18N
        tblVisit.setModel(new DefaultTableModel());
        tblVisit.setEnabled(!disableEditing);
        tblVisit.setName("tblVisit"); // NOI18N
        tblVisit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVisit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblVisitMouseReleased(evt);
            }
        });
        jScrollPane14.setViewportView(tblVisit);

        sightingIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 320, 135));

        lblElement.setFont(resourceMap.getFont("lblElement.font")); // NOI18N
        lblElement.setText(resourceMap.getString("lblElement.text")); // NOI18N
        lblElement.setName("lblElement"); // NOI18N
        sightingIncludes.add(lblElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, -1, -1));

        lblLocation.setFont(resourceMap.getFont("lblLocation.font")); // NOI18N
        lblLocation.setText(resourceMap.getString("lblLocation.text")); // NOI18N
        lblLocation.setName("lblLocation"); // NOI18N
        sightingIncludes.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtSearchLocation.setText(resourceMap.getString("txtSearchLocation.text")); // NOI18N
        txtSearchLocation.setEnabled(!disableEditing);
        txtSearchLocation.setName("txtSearchLocation"); // NOI18N
        txtSearchLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchLocationKeyPressed(evt);
            }
        });
        sightingIncludes.add(txtSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 200, -1));

        lblVisit.setFont(resourceMap.getFont("lblVisit.font")); // NOI18N
        lblVisit.setText(resourceMap.getString("lblVisit.text")); // NOI18N
        lblVisit.setName("lblVisit"); // NOI18N
        sightingIncludes.add(lblVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, -1));

        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblLocationImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLocationImageMouseReleased(evt);
            }
        });
        sightingIncludes.add(lblLocationImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, -1, -1));

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        sightingIncludes.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 160, -1, -1));

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        sightingIncludes.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 160, -1, -1));

        btnSearchLocation.setBackground(resourceMap.getColor("btnSearchLocation.background")); // NOI18N
        btnSearchLocation.setIcon(resourceMap.getIcon("btnSearchLocation.icon")); // NOI18N
        btnSearchLocation.setText(resourceMap.getString("btnSearchLocation.text")); // NOI18N
        btnSearchLocation.setToolTipText(resourceMap.getString("btnSearchLocation.toolTipText")); // NOI18N
        btnSearchLocation.setEnabled(!disableEditing);
        btnSearchLocation.setName("btnSearchLocation"); // NOI18N
        btnSearchLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchLocationActionPerformed(evt);
            }
        });
        sightingIncludes.add(btnSearchLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 90, -1));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        sightingIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 330, 40, 20));

        txtHours.setEnabled(!disableEditing);
        txtHours.setName("txtHours"); // NOI18N
        txtHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHoursFocusGained(evt);
            }
        });
        sightingIncludes.add(txtHours, new org.netbeans.lib.awtextra.AbsoluteConstraints(532, 540, 20, -1));

        txtMinutes.setEnabled(!disableEditing);
        txtMinutes.setName("txtMinutes"); // NOI18N
        txtMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMinutesFocusGained(evt);
            }
        });
        sightingIncludes.add(txtMinutes, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 540, 20, -1));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        sightingIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(553, 539, 10, 20));

        cmbTimeFormat.setModel(new DefaultComboBoxModel(TimeFormat.values()));
        cmbTimeFormat.setSelectedIndex(0);
        cmbTimeFormat.setEnabled(!disableEditing);
        cmbTimeFormat.setName("cmbTimeFormat"); // NOI18N
        sightingIncludes.add(cmbTimeFormat, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 540, 40, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 965, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sightingIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSightingActionPerformed
        if (sighting != null) {
            // Reset the border colors
            lblElement.setBorder(null);
            lblLocation.setBorder(null);
            lblVisit.setBorder(null);
            dtpSightingDate.setBorder(null);
            if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                // Set Location and Element
                sighting.setLocation(location);
                sighting.setElement(element);

                // Set variables
                Date date = dtpSightingDate.getDate();
                try {
                    if (cmbTimeFormat.getSelectedItem().equals(TimeFormat.PM)) {
                        int tempHours = 12 + Integer.parseInt(txtHours.getText());
                        if (tempHours >= 24) date.setHours(tempHours - 12);
                        else date.setHours(tempHours);
                    }
                    else
                        date.setHours(Integer.parseInt(txtHours.getText()));
                    date.setMinutes(Integer.parseInt(txtMinutes.getText()));
                }
                catch (NumberFormatException e) {
                    date.setHours(0);
                    date.setMinutes(0);
                }
                sighting.setDate(date);
                sighting.setAreaType((AreaType)cmbAreaType.getSelectedItem());
                sighting.setCertainty((Certainty)cmbCertainty.getSelectedItem());
                sighting.setDetails(txtDetails.getText());
                sighting.setSightingEvidence((SightingEvidence)cmbEvidence.getSelectedItem());
                if (txtNumberOfElements.getText().length() > 0) {
                    try {
                        sighting.setNumberOfElements(Integer.parseInt(txtNumberOfElements.getText()));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                sighting.setTimeOfDay((ActiveTimeSpesific)cmbTimeOfDay.getSelectedItem());
                sighting.setViewRating((ViewRating)cmbViewRating.getSelectedItem());
                sighting.setWeather((Weather)cmbWeather.getSelectedItem());
                sighting.setSubArea((String)cmbSubArea.getSelectedItem());
                if (tblElement.getSelectedRowCount() > 0)
                    sighting.setElement(app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(),0))));
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
                    txtLatDegrees.setText("0");
                    txtLatMinutes.setText("0");
                    txtLatSeconds.setText("0");
                    txtLonDegrees.setText("0");
                    txtLonMinutes.setText("0");
                    txtLonSeconds.setText("0");
                }

                // Delete from old visit
                if (!visit.equals(oldVisit)) {
                    oldVisit.getSightings().remove(sighting);
                    app.getDBI().createOrUpdate(oldVisit);
                }

                // Setup new Visit
                if (visit.getSightings() != null) {
                    int index = visit.getSightings().indexOf(sighting);
                    if (index != -1)
                        visit.getSightings().set(index, sighting);
                    else
                        visit.getSightings().add(sighting);
                }
                else {
                    visit.setSightings(new ArrayList<Sighting>());
                    visit.getSightings().add(sighting);
                }

                // Add and Save the visit
                //if (app.getDBI().isSightingUnique(sighting) == true) {
                if (sighting.getSightingCounter() == 0) {
                    // Add new
                    app.getDBI().createOrUpdate(sighting);
                }
                if (!visit.getSightings().contains(sighting))
                    visit.getSightings().add(sighting);
                if (app.getDBI().createOrUpdate(visit) == true) {
                    // Premare to close dialog
                    if (panelToRefresh != null) {
                        panelToRefresh.refreshTableForSightings();
                    }
                    // Close the dialog - (Evt is null if the Image Upload calls save method...)
                    if (evt != null) {
                        JDialog dialog = (JDialog)getParent().getParent().getParent().getParent();
                        dialog.dispose();
                    }
                }
                else {
                    lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                }
                //}
                //else {
                //    lblElement.setBorder(new LineBorder(Color.RED, 3, true));
                //    lblLocation.setBorder(new LineBorder(Color.RED, 3, true));
                //    lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                //    dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
                //}

            }
            else {
                if (element == null)
                    lblElement.setBorder(new LineBorder(Color.RED, 3, true));
                else
                if (location == null)
                    lblLocation.setBorder(new LineBorder(Color.RED, 3, true));
                if (visit == null)
                    lblVisit.setBorder(new LineBorder(Color.RED, 3, true));
                if (dtpSightingDate.getDate() == null)
                    dtpSightingDate.setBorder(new LineBorder(Color.RED, 3, true));
            }
        }
}//GEN-LAST:event_btnUpdateSightingActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Does not get called
    }//GEN-LAST:event_formComponentShown

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        if (sighting != null) {
            btnUpdateSightingActionPerformed(null);
            if (location != null && element != null && visit != null && dtpSightingDate.getDate() != null) {
                imageIndex = Utils.uploadImage(sighting, "Sightings"+File.separatorChar+sighting.toString(), this, lblImage, 300);
                setupNumberOfImages();
                btnUpdateSightingActionPerformed(null);
            }
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.previousImage(sighting, imageIndex, lblImage, 300);
            setupNumberOfImages();
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (sighting != null) {
            if (tblElement.getSelectedRowCount() == 1) {
                element = app.getDBI().find(new Element((String)tblElement.getValueAt(tblElement.getSelectedRow(), 0)));
                if (element.getFotos().size() > 0)
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(element.getFotos().get(0).getFileLocation()), 100));
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
        }
    }//GEN-LAST:event_tblElementMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.nextImage(sighting, imageIndex, lblImage, 300);
            setupNumberOfImages();
        }
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.removeImage(sighting, imageIndex, lblImage, app.getDBI(), app.getClass().getResource("resources/images/NoImage.gif"), 300);
            setupNumberOfImages();
            btnUpdateSightingActionPerformed(null);
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (sighting != null) {
            imageIndex = Utils.setMainImage(sighting, imageIndex);
            setupNumberOfImages();
            btnUpdateSightingActionPerformed(null);
        }
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void tblVisitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVisitMouseReleased
        if (sighting != null) {
            if (tblVisit.getSelectedRowCount() == 1) {
                visit = app.getDBI().find(new Visit(tblVisit.getValueAt(tblVisit.getSelectedRow(), 0).toString()));
            }
        }
}//GEN-LAST:event_tblVisitMouseReleased

    private void tblLocationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseReleased
        if (sighting != null) {
            if (tblLocation.getSelectedRowCount() == 1) {
                location = app.getDBI().find(new Location(tblLocation.getValueAt(tblLocation.getSelectedRow(), 0).toString()));
                if (location != null)
                    cmbSubArea.setModel(new DefaultComboBoxModel(location.getSubAreas().toArray()));
                tblVisit.setModel(utilTableGenerator.getVeryShortVisitTable(location));
                visit = null;
                if (location.getFotos().size() > 0)
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(location.getFotos().get(0).getFileLocation()), 100));
                else
                    lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
            else {

            }
            resizeTalbes();
        }
}//GEN-LAST:event_tblLocationMouseReleased

    private void chkElementTypeFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkElementTypeFilterActionPerformed
        if (sighting != null) {
            searchElement = new Element();
            if (!cmbElementType.isEnabled())
                searchElement.setType((ElementType)cmbElementType.getSelectedItem());
            tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement, true));
            cmbElementType.setEnabled(!cmbElementType.isEnabled());
            txtSearch.setText("");
            // Setup table column sizes
            resizeTalbes();
            // Resort the table
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_chkElementTypeFilterActionPerformed

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        if (sighting != null) {
            searchElement = new Element((ElementType)cmbElementType.getSelectedItem());
            tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement, true));
            txtSearch.setText("");
            // Setup table column sizes
            resizeTalbes();
            // Resort the table
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_cmbElementTypeActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (sighting != null) {
            searchElement = new Element();
            if (chkElementTypeFilter.isSelected())
                searchElement.setType((ElementType)cmbElementType.getSelectedItem());
            if (txtSearch.getText() != null) {
                if (txtSearch.getText().length() > 0)
                    searchElement.setPrimaryName(txtSearch.getText());
            }
            tblElement.setModel(utilTableGenerator.getShortElementTable(searchElement, true));
            // Setup table column sizes
            resizeTalbes();
            // Resort the table
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblElement.getRowSorter().setSortKeys(tempList);
            // Clear Images
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearchLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchLocationActionPerformed
        if (sighting != null) {
            searchLocation = new Location();
            if (txtSearchLocation.getText() != null) {
                if (txtSearchLocation.getText().length() > 0)
                    searchLocation.setName(txtSearchLocation.getText());
            }
            tblLocation.setModel(utilTableGenerator.getShortLocationTable(searchLocation, true));
            // Setup table column sizes
            resizeTalbes();
            // Resort the table
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblLocation.getRowSorter().setSortKeys(tempList);
            // Clear Images
            lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
    }//GEN-LAST:event_btnSearchLocationActionPerformed

    private void txtNumberOfElementsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNumberOfElementsFocusGained
        if (sighting != null) {
            txtNumberOfElements.setSelectionStart(0);
            txtNumberOfElements.setSelectionEnd(txtNumberOfElements.getText().length());
        }
    }//GEN-LAST:event_txtNumberOfElementsFocusGained

    private void txtLatDegreesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatDegreesFocusGained
        if (sighting != null) {
            txtLatDegrees.setSelectionStart(0);
            txtLatDegrees.setSelectionEnd(txtLatDegrees.getText().length());
        }
    }//GEN-LAST:event_txtLatDegreesFocusGained

    private void txtLatMinutesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatMinutesFocusGained
        if (sighting != null) {
            txtLatMinutes.setSelectionStart(0);
            txtLatMinutes.setSelectionEnd(txtLatMinutes.getText().length());
        }
    }//GEN-LAST:event_txtLatMinutesFocusGained

    private void txtLatSecondsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLatSecondsFocusGained
        if (sighting != null) {
            txtLatSeconds.setSelectionStart(0);
            txtLatSeconds.setSelectionEnd(txtLatSeconds.getText().length());
        }
    }//GEN-LAST:event_txtLatSecondsFocusGained

    private void txtLonDegreesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonDegreesFocusGained
        if (sighting != null) {
            txtLonDegrees.setSelectionStart(0);
            txtLonDegrees.setSelectionEnd(txtLonDegrees.getText().length());
        }
    }//GEN-LAST:event_txtLonDegreesFocusGained

    private void txtLonMinutesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonMinutesFocusGained
        if (sighting != null) {
            txtLonMinutes.setSelectionStart(0);
            txtLonMinutes.setSelectionEnd(txtLonMinutes.getText().length());
        }
    }//GEN-LAST:event_txtLonMinutesFocusGained

    private void txtLonSecondsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLonSecondsFocusGained
        if (sighting != null) {
            txtLonSeconds.setSelectionStart(0);
            txtLonSeconds.setSelectionEnd(txtLonSeconds.getText().length());
        }
    }//GEN-LAST:event_txtLonSecondsFocusGained

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        if (sighting != null) {
            Utils.openImage(sighting, imageIndex);
        }
    }//GEN-LAST:event_lblImageMouseReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (location != null) {
            Utils.openImage(location, 0);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (element != null) {
            Utils.openImage(element, 0);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void txtHoursFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHoursFocusGained
        if (sighting != null) {
            txtHours.setSelectionStart(0);
            txtHours.setSelectionEnd(txtHours.getText().length());
        }
    }//GEN-LAST:event_txtHoursFocusGained

    private void txtMinutesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinutesFocusGained
        if (sighting != null) {
            txtMinutes.setSelectionStart(0);
            txtMinutes.setSelectionEnd(txtMinutes.getText().length());
        }
    }//GEN-LAST:event_txtMinutesFocusGained

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSearchActionPerformed(null);
    }//GEN-LAST:event_txtSearchKeyPressed

    private void txtSearchLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnSearchLocationActionPerformed(null);
    }//GEN-LAST:event_txtSearchLocationKeyPressed

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblLocationMouseReleased(null);
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void resizeTalbes() {
        if (sighting != null) {
            TableColumn column = null;
            for (int i = 0; i < tblElement.getColumnModel().getColumnCount(); i++) {
                column = tblElement.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(150);
                }
                else if (i == 1) {
                    column.setPreferredWidth(25);
                }
                else if (i == 2) {
                    column.setPreferredWidth(40);
                }
            }
            for (int i = 0; i < tblLocation.getColumnModel().getColumnCount(); i++) {
                column = tblLocation.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(150);
                }
                else if (i == 1) {
                    column.setPreferredWidth(30);
                }
            }
            for (int i = 0; i < tblVisit.getColumnModel().getColumnCount(); i++) {
                column = tblVisit.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(100);
                }
                else if (i == 1) {
                    column.setPreferredWidth(45);
                }
                else if (i == 2) {
                    column.setPreferredWidth(25);
                }
            }
        }
    }

    private void setupNumberOfImages() {
        if (sighting != null) {
            if (sighting.getFotos().size() > 0)
                lblNumberOfImages.setText(imageIndex+1 + " of " + sighting.getFotos().size());
            else
                lblNumberOfImages.setText("0 of 0");
        }
    }

    public boolean isDisableEditing() {
        return disableEditing;
    }

    public void setDisableEditing(boolean disableEditing) {
        this.disableEditing = disableEditing;
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
    private javax.swing.JComboBox cmbAreaType;
    private javax.swing.JComboBox cmbCertainty;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JComboBox cmbEvidence;
    private javax.swing.JComboBox cmbLatitude;
    private javax.swing.JComboBox cmbLongitude;
    private javax.swing.JComboBox cmbSubArea;
    private javax.swing.JComboBox cmbTimeFormat;
    private javax.swing.JComboBox cmbTimeOfDay;
    private javax.swing.JComboBox cmbViewRating;
    private javax.swing.JComboBox cmbWeather;
    private org.jdesktop.swingx.JXDatePicker dtpSightingDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblElement;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblVisit;
    private javax.swing.JPanel sightingIncludes;
    private javax.swing.JTable tblElement;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblVisit;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JTextField txtHours;
    private javax.swing.JTextField txtLatDegrees;
    private javax.swing.JTextField txtLatMinutes;
    private javax.swing.JTextField txtLatSeconds;
    private javax.swing.JTextField txtLonDegrees;
    private javax.swing.JTextField txtLonMinutes;
    private javax.swing.JTextField txtLonSeconds;
    private javax.swing.JTextField txtMinutes;
    private javax.swing.JTextField txtNumberOfElements;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearchLocation;
    // End of variables declaration//GEN-END:variables
    
}
