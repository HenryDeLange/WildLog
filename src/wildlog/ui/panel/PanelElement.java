/*
 * PanelElement.java is part of WildLog
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
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.WishRating;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.report.ReportElement;
import wildlog.utils.UtilsHTML;


/**
 *
 * @author  henry.delange
 */
public class PanelElement extends javax.swing.JPanel implements PanelNeedsRefreshWhenSightingAdded {
    // Variables:
    private Element element;
    private JTabbedPane parent;
    private int imageIndex;
    private UtilPanelGenerator utilPanelGenerator;
    private UtilTableGenerator utilTableGenerator;
    private WildLogApp app;
    
    /** Creates new form PanelElement */
    public PanelElement(Element inElement) {
        app = (WildLogApp) Application.getInstance();
        element = inElement;
        utilPanelGenerator = new UtilPanelGenerator();
        utilTableGenerator = new UtilTableGenerator();
        initComponents();
        imageIndex = 0;
        List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + element.getPrimaryName()));
        if (fotos.size() > 0) {
            Utils.setupFoto("ELEMENT-" + element.getPrimaryName(), imageIndex, lblImage, 300, app);
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        }
        setupNumberOfImages();

        tblLocation.getTableHeader().setReorderingAllowed(false);
    }
    
    public Element getElement() {
        return element;
    }
    public void setElement(Element inElement) {
        element = inElement;
    }
    
    @Override
    public boolean equals(Object inObject) {
        if (getClass() != inObject.getClass()) return false;
        final PanelElement inPanel = (PanelElement) inObject;
        if (element == null && inPanel.getElement() == null) return true;
        if (element.getPrimaryName() == null && inPanel.getElement().getPrimaryName() == null) return true;
        if (element == null) return false;
        if (element.getPrimaryName() == null) return false;
        if (!element.getPrimaryName().equalsIgnoreCase(inPanel.getElement().getPrimaryName())) return false;
        return true;
    }
    
    public void setupTabHeader() {
        parent = (JTabbedPane) getParent();
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/Element.gif"))));
        if (element.getPrimaryName() != null) tabHeader.add(new JLabel(element.getPrimaryName() + " "));
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

    @Override
    public void refreshTableForSightings() {
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
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtPrimaryName = new javax.swing.JTextField();
        txtOtherName = new javax.swing.JTextField();
        txtScienceName = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JSeparator();
        btnPreviousImage = new javax.swing.JButton();
        btnNextImage = new javax.swing.JButton();
        btnSetMainImage = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        btnGoLocation = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jScrollPane17 = new javax.swing.JScrollPane();
        txtDiagnosticDescription = new javax.swing.JTextArea();
        jScrollPane18 = new javax.swing.JScrollPane();
        txtBehaviourDescription = new javax.swing.JTextArea();
        cmbType = new javax.swing.JComboBox();
        cmbWaterDependance = new javax.swing.JComboBox();
        cmbActiveTime = new javax.swing.JComboBox();
        txtSizeMaleMin = new javax.swing.JTextField();
        txtSizeFemaleMin = new javax.swing.JTextField();
        txtWeightMaleMin = new javax.swing.JTextField();
        txtWeightFemaleMin = new javax.swing.JTextField();
        txtLifespan = new javax.swing.JTextField();
        txtbreedingDuration = new javax.swing.JTextField();
        txtBreedingNumber = new javax.swing.JTextField();
        lblNumberOfSightings = new javax.swing.JLabel();
        lblElementName = new javax.swing.JLabel();
        cmbWishList = new javax.swing.JComboBox();
        cmbAddFrequency = new javax.swing.JComboBox();
        cmbEndangeredStatus = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        cmbFeedingClass = new javax.swing.JComboBox();
        btnUploadImage = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        cmbSizeUnits = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cmbWeightUnits = new javax.swing.JComboBox();
        btnDeleteImage = new javax.swing.JButton();
        btnMap = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNutrition = new javax.swing.JTextArea();
        btnAddSighting = new javax.swing.JButton();
        lblNumberOfLocations = new javax.swing.JLabel();
        lblNumberOfImages = new javax.swing.JLabel();
        rdbLocations = new javax.swing.JRadioButton();
        rdbSightings = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        txtReferenceID = new javax.swing.JTextField();
        btnHTML = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        txtSizeMaleMax = new javax.swing.JTextField();
        txtSizeFemaleMax = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtWeightMaleMax = new javax.swing.JTextField();
        txtWeightFemaleMax = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName(element.getPrimaryName());
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        elementIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setName("elementIncludes"); // NOI18N
        elementIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelElement.class);
        jLabel55.setText(resourceMap.getString("jLabel55.text")); // NOI18N
        jLabel55.setName("jLabel55"); // NOI18N
        elementIncludes.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 28, -1, -1));

        jLabel56.setText(resourceMap.getString("jLabel56.text")); // NOI18N
        jLabel56.setName("jLabel56"); // NOI18N
        elementIncludes.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, -1, -1));

        jLabel57.setText(resourceMap.getString("jLabel57.text")); // NOI18N
        jLabel57.setName("jLabel57"); // NOI18N
        elementIncludes.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 76, -1, -1));

        txtPrimaryName.setBackground(resourceMap.getColor("txtPrimaryName.background")); // NOI18N
        txtPrimaryName.setText(element.getPrimaryName());
        txtPrimaryName.setName("txtPrimaryName"); // NOI18N
        elementIncludes.add(txtPrimaryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 28, 490, -1));

        txtOtherName.setText(element.getOtherName());
        txtOtherName.setName("txtOtherName"); // NOI18N
        elementIncludes.add(txtOtherName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 52, 490, -1));

        txtScienceName.setText(element.getScientificName());
        txtScienceName.setName("txtScienceName"); // NOI18N
        elementIncludes.add(txtScienceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 76, 330, -1));

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
        elementIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 28, 110, 60));

        jSeparator10.setName("jSeparator10"); // NOI18N
        elementIncludes.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

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
        elementIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

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
        elementIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

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
        elementIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 326, 90, -1));

        jLabel58.setText(resourceMap.getString("jLabel58.text")); // NOI18N
        jLabel58.setName("jLabel58"); // NOI18N
        elementIncludes.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 361, -1, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setName("tblLocation"); // NOI18N
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

        elementIncludes.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 376, 290, 170));

        btnGoLocation.setIcon(resourceMap.getIcon("btnGoLocation.icon")); // NOI18N
        btnGoLocation.setText(resourceMap.getString("btnGoLocation.text")); // NOI18N
        btnGoLocation.setToolTipText(resourceMap.getString("btnGoLocation.toolTipText")); // NOI18N
        btnGoLocation.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });
        elementIncludes.add(btnGoLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(853, 548, -1, 30));

        jSeparator11.setName("jSeparator11"); // NOI18N
        elementIncludes.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel59.setText(resourceMap.getString("jLabel59.text")); // NOI18N
        jLabel59.setName("jLabel59"); // NOI18N
        elementIncludes.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, -1, -1));

        jLabel60.setText(resourceMap.getString("jLabel60.text")); // NOI18N
        jLabel60.setName("jLabel60"); // NOI18N
        elementIncludes.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));

        jLabel61.setText(resourceMap.getString("jLabel61.text")); // NOI18N
        jLabel61.setName("jLabel61"); // NOI18N
        elementIncludes.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 410, -1, -1));

        jLabel62.setText(resourceMap.getString("jLabel62.text")); // NOI18N
        jLabel62.setName("jLabel62"); // NOI18N
        elementIncludes.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 197, -1, -1));

        jLabel64.setText(resourceMap.getString("jLabel64.text")); // NOI18N
        jLabel64.setName("jLabel64"); // NOI18N
        elementIncludes.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 148, 80, -1));

        jLabel65.setFont(resourceMap.getFont("jLabel65.font")); // NOI18N
        jLabel65.setText(resourceMap.getString("jLabel65.text")); // NOI18N
        jLabel65.setName("jLabel65"); // NOI18N
        elementIncludes.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 0, -1, 20));

        jLabel66.setText(resourceMap.getString("jLabel66.text")); // NOI18N
        jLabel66.setName("jLabel66"); // NOI18N
        elementIncludes.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 124, 90, -1));

        jLabel67.setText(resourceMap.getString("jLabel67.text")); // NOI18N
        jLabel67.setName("jLabel67"); // NOI18N
        elementIncludes.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 124, 80, -1));

        jLabel68.setText(resourceMap.getString("jLabel68.text")); // NOI18N
        jLabel68.setName("jLabel68"); // NOI18N
        elementIncludes.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 172, -1, -1));

        jLabel69.setText(resourceMap.getString("jLabel69.text")); // NOI18N
        jLabel69.setName("jLabel69"); // NOI18N
        elementIncludes.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 230, -1, -1));

        jLabel70.setText(resourceMap.getString("jLabel70.text")); // NOI18N
        jLabel70.setName("jLabel70"); // NOI18N
        elementIncludes.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 80, -1));

        jLabel71.setText(resourceMap.getString("jLabel71.text")); // NOI18N
        jLabel71.setName("jLabel71"); // NOI18N
        elementIncludes.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, -1, -1));

        jLabel72.setText(resourceMap.getString("jLabel72.text")); // NOI18N
        jLabel72.setName("jLabel72"); // NOI18N
        elementIncludes.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 540, -1, -1));

        jLabel73.setText(resourceMap.getString("jLabel73.text")); // NOI18N
        jLabel73.setName("jLabel73"); // NOI18N
        elementIncludes.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 510, -1, -1));

        jLabel74.setText(resourceMap.getString("jLabel74.text")); // NOI18N
        jLabel74.setName("jLabel74"); // NOI18N
        elementIncludes.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 540, -1, -1));

        jLabel75.setText(resourceMap.getString("jLabel75.text")); // NOI18N
        jLabel75.setName("jLabel75"); // NOI18N
        elementIncludes.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 148, 80, -1));

        jLabel76.setText(resourceMap.getString("jLabel76.text")); // NOI18N
        jLabel76.setName("jLabel76"); // NOI18N
        elementIncludes.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 172, -1, -1));

        jLabel77.setText(resourceMap.getString("jLabel77.text")); // NOI18N
        jLabel77.setName("jLabel77"); // NOI18N
        elementIncludes.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 197, -1, -1));

        jScrollPane16.setName("jScrollPane16"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(resourceMap.getFont("txtDescription.font")); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(element.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane16.setViewportView(txtDescription);

        elementIncludes.add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 320, 590, -1));

        jScrollPane17.setName("jScrollPane17"); // NOI18N

        txtDiagnosticDescription.setColumns(20);
        txtDiagnosticDescription.setFont(resourceMap.getFont("txtDiagnosticDescription.font")); // NOI18N
        txtDiagnosticDescription.setLineWrap(true);
        txtDiagnosticDescription.setRows(5);
        txtDiagnosticDescription.setText(element.getDiagnosticDescription());
        txtDiagnosticDescription.setWrapStyleWord(true);
        txtDiagnosticDescription.setName("txtDiagnosticDescription"); // NOI18N
        jScrollPane17.setViewportView(txtDiagnosticDescription);

        elementIncludes.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 310, -1));

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        txtBehaviourDescription.setColumns(20);
        txtBehaviourDescription.setFont(resourceMap.getFont("txtBehaviourDescription.font")); // NOI18N
        txtBehaviourDescription.setLineWrap(true);
        txtBehaviourDescription.setRows(5);
        txtBehaviourDescription.setText(element.getBehaviourDescription());
        txtBehaviourDescription.setWrapStyleWord(true);
        txtBehaviourDescription.setName("txtBehaviourDescription"); // NOI18N
        jScrollPane18.setViewportView(txtBehaviourDescription);

        elementIncludes.add(jScrollPane18, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 410, 590, -1));

        cmbType.setMaximumRowCount(9);
        cmbType.setModel(new DefaultComboBoxModel(wildlog.data.enums.ElementType.values()));
        cmbType.setSelectedItem(element.getType());
        cmbType.setName("cmbType"); // NOI18N
        elementIncludes.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 220, -1));

        cmbWaterDependance.setModel(new DefaultComboBoxModel(wildlog.data.enums.WaterDependancy.values()));
        cmbWaterDependance.setSelectedItem(element.getWaterDependance());
        cmbWaterDependance.setName("cmbWaterDependance"); // NOI18N
        elementIncludes.add(cmbWaterDependance, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 172, 220, -1));

        cmbActiveTime.setModel(new DefaultComboBoxModel(wildlog.data.enums.ActiveTime.values()));
        cmbActiveTime.setSelectedItem(element.getActiveTime());
        cmbActiveTime.setName("cmbActiveTime"); // NOI18N
        elementIncludes.add(cmbActiveTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 148, 220, -1));

        txtSizeMaleMin.setText(Double.toString(element.getSizeMaleMin()));
        txtSizeMaleMin.setName("txtSizeMaleMin"); // NOI18N
        txtSizeMaleMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeMaleMinFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeMaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 510, 50, -1));

        txtSizeFemaleMin.setText(Double.toString(element.getSizeFemaleMin()));
        txtSizeFemaleMin.setName("txtSizeFemaleMin"); // NOI18N
        txtSizeFemaleMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeFemaleMinFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeFemaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 50, -1));

        txtWeightMaleMin.setText(Double.toString(element.getWeightMaleMin()));
        txtWeightMaleMin.setName("txtWeightMaleMin"); // NOI18N
        txtWeightMaleMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightMaleMinFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightMaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 510, 50, -1));

        txtWeightFemaleMin.setText(Double.toString(element.getWeightFemaleMin()));
        txtWeightFemaleMin.setName("txtWeightFemaleMin"); // NOI18N
        txtWeightFemaleMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightFemaleMinFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightFemaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 540, 50, -1));

        txtLifespan.setText(element.getLifespan());
        txtLifespan.setName("txtLifespan"); // NOI18N
        txtLifespan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLifespanFocusGained(evt);
            }
        });
        elementIncludes.add(txtLifespan, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 148, 150, -1));

        txtbreedingDuration.setText(element.getBreedingDuration());
        txtbreedingDuration.setName("txtbreedingDuration"); // NOI18N
        txtbreedingDuration.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtbreedingDurationFocusGained(evt);
            }
        });
        elementIncludes.add(txtbreedingDuration, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 172, 150, -1));

        txtBreedingNumber.setText(element.getBreedingNumber());
        txtBreedingNumber.setName("txtBreedingNumber"); // NOI18N
        txtBreedingNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBreedingNumberFocusGained(evt);
            }
        });
        elementIncludes.add(txtBreedingNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 197, 150, -1));

        lblNumberOfSightings.setFont(resourceMap.getFont("lblNumberOfSightings.font")); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setText(resourceMap.getString("lblNumberOfSightings.text")); // NOI18N
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfSightings.border.lineColor"))); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        elementIncludes.add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 1, 30, 20));

        lblElementName.setFont(resourceMap.getFont("lblElementName.font")); // NOI18N
        lblElementName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementName.setText(element.getPrimaryName());
        lblElementName.setName("lblElementName"); // NOI18N
        elementIncludes.add(lblElementName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 610, -1));

        cmbWishList.setModel(new DefaultComboBoxModel(wildlog.data.enums.WishRating.values()));
        cmbWishList.setSelectedItem(element.getWishListRating());
        cmbWishList.setName("cmbWishList"); // NOI18N
        elementIncludes.add(cmbWishList, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 124, 150, -1));

        cmbAddFrequency.setModel(new DefaultComboBoxModel(wildlog.data.enums.AddFrequency.values()));
        cmbAddFrequency.setSelectedItem(element.getAddFrequency());
        cmbAddFrequency.setName("cmbAddFrequency"); // NOI18N
        elementIncludes.add(cmbAddFrequency, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 124, 220, -1));

        cmbEndangeredStatus.setModel(new DefaultComboBoxModel(EndangeredStatus.values()));
        cmbEndangeredStatus.setSelectedItem(element.getEndangeredStatus());
        cmbEndangeredStatus.setName("cmbEndangeredStatus"); // NOI18N
        elementIncludes.add(cmbEndangeredStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 197, 220, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        elementIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 80, -1));

        cmbFeedingClass.setModel(new DefaultComboBoxModel(FeedingClass.values()));
        cmbFeedingClass.setSelectedItem(element.getFeedingClass());
        cmbFeedingClass.setName("cmbFeedingClass"); // NOI18N
        elementIncludes.add(cmbFeedingClass, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 100, 150, -1));

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
        elementIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        lblImage.setBackground(resourceMap.getColor("lblImage.background")); // NOI18N
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setOpaque(true);
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblImageMouseReleased(evt);
            }
        });
        elementIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        elementIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 361, 10, 220));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        elementIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 360, 300, 10));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        elementIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, 20));

        cmbSizeUnits.setModel(new DefaultComboBoxModel(UnitsSize.values()));
        cmbSizeUnits.setSelectedItem(element.getSizeUnit());
        cmbSizeUnits.setName("cmbSizeUnits"); // NOI18N
        elementIncludes.add(cmbSizeUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 530, 70, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        elementIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 530, -1, 20));

        cmbWeightUnits.setModel(new DefaultComboBoxModel(UnitsWeight.values()));
        cmbWeightUnits.setSelectedItem(element.getWeightUnit());
        cmbWeightUnits.setName("cmbWeightUnits"); // NOI18N
        elementIncludes.add(cmbWeightUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 530, 70, -1));

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
        elementIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 326, 90, -1));

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
        elementIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 133, 110, 40));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtNutrition.setColumns(20);
        txtNutrition.setFont(resourceMap.getFont("txtNutrition.font")); // NOI18N
        txtNutrition.setLineWrap(true);
        txtNutrition.setRows(3);
        txtNutrition.setText(element.getNutrition());
        txtNutrition.setWrapStyleWord(true);
        txtNutrition.setName("txtNutrition"); // NOI18N
        jScrollPane1.setViewportView(txtNutrition);

        elementIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 250, 270, 60));

        btnAddSighting.setFont(resourceMap.getFont("btnAddSighting.font")); // NOI18N
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
        elementIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 90, 110, 40));

        lblNumberOfLocations.setFont(resourceMap.getFont("lblNumberOfLocations.font")); // NOI18N
        lblNumberOfLocations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfLocations.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfLocations.border.lineColor"))); // NOI18N
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        elementIncludes.add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 550, 30, 20));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        elementIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 330, 40, 20));

        buttonGroup1.add(rdbLocations);
        rdbLocations.setSelected(true);
        rdbLocations.setText(resourceMap.getString("rdbLocations.text")); // NOI18N
        rdbLocations.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocations.setName("rdbLocations"); // NOI18N
        elementIncludes.add(rdbLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 550, -1, -1));

        buttonGroup1.add(rdbSightings);
        rdbSightings.setText(resourceMap.getString("rdbSightings.text")); // NOI18N
        rdbSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSightings.setName("rdbSightings"); // NOI18N
        rdbSightings.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbSightingsItemStateChanged(evt);
            }
        });
        elementIncludes.add(rdbSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 550, -1, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        elementIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 76, -1, -1));

        txtReferenceID.setText(element.getReferenceID());
        txtReferenceID.setName("txtReferenceID"); // NOI18N
        elementIncludes.add(txtReferenceID, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 76, 80, -1));

        btnHTML.setIcon(resourceMap.getIcon("btnHTML.icon")); // NOI18N
        btnHTML.setText(resourceMap.getString("btnHTML.text")); // NOI18N
        btnHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHTML.setName("btnHTML"); // NOI18N
        btnHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLActionPerformed(evt);
            }
        });
        elementIncludes.add(btnHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 218, 110, 30));

        btnReport.setIcon(resourceMap.getIcon("btnReport.icon")); // NOI18N
        btnReport.setText(resourceMap.getString("btnReport.text")); // NOI18N
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.setName("btnReport"); // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        elementIncludes.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 176, 110, 40));

        txtSizeMaleMax.setText(Double.toString(element.getSizeMaleMax()));
        txtSizeMaleMax.setName("txtSizeMaleMax"); // NOI18N
        txtSizeMaleMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeMaleMaxFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeMaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 50, -1));

        txtSizeFemaleMax.setText(Double.toString(element.getSizeFemaleMax()));
        txtSizeFemaleMax.setName("txtSizeFemaleMax"); // NOI18N
        txtSizeFemaleMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeFemaleMaxFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeFemaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 540, 50, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        elementIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 490, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        elementIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 490, -1, -1));

        txtWeightMaleMax.setText(Double.toString(element.getWeightMaleMax()));
        txtWeightMaleMax.setName("txtWeightMaleMax"); // NOI18N
        txtWeightMaleMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightMaleMaxFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightMaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 510, 50, 20));

        txtWeightFemaleMax.setText(Double.toString(element.getWeightFemaleMax()));
        txtWeightFemaleMax.setName("txtWeightFemaleMax"); // NOI18N
        txtWeightFemaleMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightFemaleMaxFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightFemaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 540, 50, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        elementIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 490, -1, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        elementIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 490, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(elementIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 1005, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(elementIncludes, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (Utils.checkCharacters(txtPrimaryName.getText().trim())) {
            if (txtPrimaryName.getText().length() > 0) {
                String oldName = element.getPrimaryName();
                element.setPrimaryName(txtPrimaryName.getText().trim()); // Used for indexing (ID)
                element.setOtherName(txtOtherName.getText());
                element.setScientificName(txtScienceName.getText());
                element.setReferenceID(txtReferenceID.getText());
                element.setDescription(txtDescription.getText());
                element.setNutrition(txtNutrition.getText());
                element.setWaterDependance((WaterDependancy)cmbWaterDependance.getSelectedItem());
                try {
                    element.setSizeMaleMin(Double.valueOf(txtSizeMaleMin.getText()));
                }
                catch (NumberFormatException e) {
                    txtSizeMaleMin.setText("");
                }
                try {
                    element.setSizeFemaleMin(Double.valueOf(txtSizeFemaleMin.getText()));
                }
                catch (NumberFormatException e) {
                    txtSizeFemaleMin.setText("");
                }
                try {
                    element.setSizeMaleMax(Double.valueOf(txtSizeMaleMax.getText()));
                }
                catch (NumberFormatException e) {
                    txtSizeMaleMax.setText("");
                }
                try {
                    element.setSizeFemaleMax(Double.valueOf(txtSizeFemaleMax.getText()));
                }
                catch (NumberFormatException e) {
                    txtSizeFemaleMax.setText("");
                }
                element.setBreedingNumber(txtBreedingNumber.getText());
                element.setSizeUnit((UnitsSize)cmbSizeUnits.getSelectedItem());
                element.setWeightUnit((UnitsWeight)cmbWeightUnits.getSelectedItem());
                try {
                    element.setWeightMaleMin(Double.valueOf(txtWeightMaleMin.getText()));
                }
                catch (NumberFormatException e) {
                    txtWeightMaleMin.setText("");
                }
                try {
                    element.setWeightFemaleMin(Double.valueOf(txtWeightFemaleMin.getText()));
                }
                catch (NumberFormatException e) {
                    txtWeightFemaleMin.setText("");
                }
                try {
                    element.setWeightMaleMax(Double.valueOf(txtWeightMaleMax.getText()));
                }
                catch (NumberFormatException e) {
                    txtWeightMaleMax.setText("");
                }
                try {
                    element.setWeightFemaleMax(Double.valueOf(txtWeightFemaleMax.getText()));
                }
                catch (NumberFormatException e) {
                    txtWeightFemaleMax.setText("");
                }
                element.setBreedingDuration(txtbreedingDuration.getText());
                element.setLifespan(txtLifespan.getText());
                element.setWishListRating((WishRating)cmbWishList.getSelectedItem());
                //element.setHabitat((Habitat)cmbHabitat.getSelectedItem());
                element.setDiagnosticDescription(txtDiagnosticDescription.getText());
                element.setActiveTime((ActiveTime)cmbActiveTime.getSelectedItem());
                element.setEndangeredStatus((EndangeredStatus)cmbEndangeredStatus.getSelectedItem());
                element.setBehaviourDescription(txtBehaviourDescription.getText());
                element.setAddFrequency((AddFrequency)cmbAddFrequency.getSelectedItem());
                element.setType((ElementType)cmbType.getSelectedItem());
                element.setFeedingClass((FeedingClass)cmbFeedingClass.getSelectedItem());

                // Save the element
                if (app.getDBI().createOrUpdate(element, oldName) == true) {
                    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelElement.class);
                    txtPrimaryName.setBackground(resourceMap.getColor("txtPrimaryName.background"));
                }
                else {
                    txtPrimaryName.setBackground(Color.RED);
                    element.setPrimaryName(oldName);
                    txtPrimaryName.setText(txtPrimaryName.getText() + "_not_unique");
                }

                lblElementName.setText(element.getPrimaryName());

                setupTabHeader();
            }
            else {
                txtPrimaryName.setBackground(Color.RED);
            }
        }
        else {
            txtPrimaryName.setText(txtPrimaryName.getText() + "_unsupported_character");
            txtPrimaryName.setBackground(Color.RED);
        }

}//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtPrimaryName.getBackground().equals(Color.RED)) {
            imageIndex = Utils.uploadImage("ELEMENT-" + element.getPrimaryName(), "Creatures"+File.separatorChar+element.getPrimaryName(), this, lblImage, 300, app);
            setupNumberOfImages();
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        imageIndex = Utils.previousImage("ELEMENT-" + element.getPrimaryName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        imageIndex = Utils.nextImage("ELEMENT-" + element.getPrimaryName(), imageIndex, lblImage, 300, app);
        setupNumberOfImages();
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        imageIndex = Utils.setMainImage("ELEMENT-" + element.getPrimaryName(), imageIndex, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        if (rdbLocations.isSelected()) {
            int[] selectedRows = tblLocation.getSelectedRows();
            PanelLocation tempPanel = null;
            for (int t = 0; t < selectedRows.length; t++) {
                tempPanel = utilPanelGenerator.getLocationPanel((String)tblLocation.getValueAt(selectedRows[t], 0));
                parent = (JTabbedPane) getParent();
                parent.add(tempPanel);
                tempPanel.setupTabHeader();
            }
            if (tempPanel != null) parent.setSelectedComponent(tempPanel);
        }
        else {
            if (tblLocation.getSelectedRowCount() == 1) {
                final JDialog dialog = new JDialog(app.getMainFrame(), "Edit an Existing Sighting", true);
                dialog.setLayout(new AbsoluteLayout());
                dialog.setSize(965, 625);
                Location location = app.getDBI().find(new Location((String)tblLocation.getValueAt(tblLocation.getSelectedRow(), 0)));
                Sighting sighting = app.getDBI().find(new Sighting((Long)tblLocation.getValueAt(tblLocation.getSelectedRow(), 2)));
                dialog.add(new PanelSighting(sighting, location, app.getDBI().find(new Visit(sighting.getVisitName())), element, this, false, false), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
                dialog.setLocationRelativeTo(this);
                ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
                dialog.setIconImage(icon.getImage());
                ActionListener escListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                };
                dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if (element.getPrimaryName() != null) {
            Sighting sighting = new Sighting();
            sighting.setElementName(element.getPrimaryName());
            lblNumberOfSightings.setText(Integer.toString(app.getDBI().list(sighting).size()));
        }
        else {
            lblNumberOfSightings.setText("0");
            lblNumberOfLocations.setText("0");
        }

        if (element.getPrimaryName() != null) {
            tblLocation.setModel(utilTableGenerator.getLocationsForElementTable(element));
            // Sort rows for Locations
            List tempList = new ArrayList<SortKey>(1);
            tempList.add(new SortKey(0, SortOrder.ASCENDING));
            tblLocation.getRowSorter().setSortKeys(tempList);
        }
        else
            tblLocation.setModel(new DefaultTableModel(new String[]{"No Locations"}, 0));
        // Setup table column sizes
        resizeTables();
        rdbLocations.setSelected(true);
        lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
    }//GEN-LAST:event_formComponentShown

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        app.getMapFrame().clearPoints();
        Sighting sigting = new Sighting();
        sigting.setElementName(element.getPrimaryName());
        List<Sighting> sightingList = app.getDBI().list(sigting);
        boolean foundPosition = false;
        for (int t = 0; t < sightingList.size(); t++) {
            foundPosition = false;
            if (sightingList.get(t).getLatitude() != null && sightingList.get(t).getLongitude() != null)
            if (!sightingList.get(t).getLatitude().equals(Latitudes.NONE) && !sightingList.get(t).getLongitude().equals(Longitudes.NONE)) {
                float lat = sightingList.get(t).getLatDegrees();
                lat = lat + sightingList.get(t).getLatMinutes()/60f;
                lat = lat + (sightingList.get(t).getLatSecondsFloat()/60f)/60f;
                if (sightingList.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sightingList.get(t).getLonDegrees();
                lon = lon + sightingList.get(t).getLonMinutes()/60f;
                lon = lon + (sightingList.get(t).getLonSecondsFloat()/60f)/60f;
                if (sightingList.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
                foundPosition = true;
            }
            // If the sighting did not have a position use the location's
            if (foundPosition == false) {
                Location location = app.getDBI().find(new Location(sightingList.get(t).getLocationName()));
                float lat = location.getLatDegrees();
                lat = lat + location.getLatMinutes()/60f;
                lat = lat + (location.getLatSecondsFloat()/60f)/60f;
                if (location.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = location.getLonDegrees();
                lon = lon + location.getLonMinutes()/60f;
                lon = lon + (location.getLonSecondsFloat()/60f)/60f;
                if (location.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
            }
        }
        app.getMapFrame().changeTitle("WildLog Map - Creature: " + element.getPrimaryName());
        app.getMapFrame().showMap();
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        imageIndex = Utils.removeImage("ELEMENT-" + element.getPrimaryName(), imageIndex, lblImage, app.getDBI(), app.getClass().getResource("resources/images/NoImage.gif"), 300, app);
        setupNumberOfImages();
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtPrimaryName.getBackground().equals(Color.RED)) {
            Sighting sighting = new Sighting();
            sighting.setElementName(element.getPrimaryName());
            final JDialog dialog = new JDialog(app.getMainFrame(), "Add a New Sighting", true);
            dialog.setLayout(new AbsoluteLayout());
            dialog.setSize(965, 625);
            dialog.add(new PanelSighting(sighting, null, null, element, this, true, false), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
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
        }
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void txtSizeMaleMinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeMaleMinFocusGained
        txtSizeMaleMin.setSelectionStart(0);
        txtSizeMaleMin.setSelectionEnd(txtSizeMaleMin.getText().length());
    }//GEN-LAST:event_txtSizeMaleMinFocusGained

    private void txtSizeFemaleMinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeFemaleMinFocusGained
        txtSizeFemaleMin.setSelectionStart(0);
        txtSizeFemaleMin.setSelectionEnd(txtSizeFemaleMin.getText().length());
    }//GEN-LAST:event_txtSizeFemaleMinFocusGained

    private void txtWeightMaleMinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightMaleMinFocusGained
        txtWeightMaleMin.setSelectionStart(0);
        txtWeightMaleMin.setSelectionEnd(txtWeightMaleMin.getText().length());
    }//GEN-LAST:event_txtWeightMaleMinFocusGained

    private void txtWeightFemaleMinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightFemaleMinFocusGained
        txtWeightFemaleMin.setSelectionStart(0);
        txtWeightFemaleMin.setSelectionEnd(txtWeightFemaleMin.getText().length());
    }//GEN-LAST:event_txtWeightFemaleMinFocusGained

    private void txtLifespanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLifespanFocusGained
        txtLifespan.setSelectionStart(0);
        txtLifespan.setSelectionEnd(txtLifespan.getText().length());
}//GEN-LAST:event_txtLifespanFocusGained

    private void txtbreedingDurationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtbreedingDurationFocusGained
        txtbreedingDuration.setSelectionStart(0);
        txtbreedingDuration.setSelectionEnd(txtbreedingDuration.getText().length());
    }//GEN-LAST:event_txtbreedingDurationFocusGained

    private void txtBreedingNumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBreedingNumberFocusGained
        txtBreedingNumber.setSelectionStart(0);
        txtBreedingNumber.setSelectionEnd(txtBreedingNumber.getText().length());
    }//GEN-LAST:event_txtBreedingNumberFocusGained

    private void lblImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseReleased
        Utils.openImage("ELEMENT-" + element.getPrimaryName(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoLocationActionPerformed(null);
    }//GEN-LAST:event_tblLocationKeyPressed

    private void rdbSightingsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbSightingsItemStateChanged
        if (rdbSightings.isSelected()) {
            if (element.getPrimaryName() != null) {
                tblLocation.setModel(utilTableGenerator.getSightingsForElementTable(element));
                // Sort rows for Locations
                List tempList = new ArrayList<SortKey>(1);
                tempList.add(new SortKey(0, SortOrder.ASCENDING));
                tblLocation.getRowSorter().setSortKeys(tempList);
            }
            else
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Sightings"}, 0));
        }
        else {
            if (element.getPrimaryName() != null) {
                tblLocation.setModel(utilTableGenerator.getLocationsForElementTable(element));
                // Sort rows for Locations
                List tempList = new ArrayList<SortKey>(1);
                tempList.add(new SortKey(0, SortOrder.ASCENDING));
                tblLocation.getRowSorter().setSortKeys(tempList);
            }
            else
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Locations"}, 0));
        }
        lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
        // Setup table column sizes
        resizeTables();
    }//GEN-LAST:event_rdbSightingsItemStateChanged

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        UtilsHTML.exportHTML(element, app);
        Utils.openFile(File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar + element.getPrimaryName() + ".html");
    }//GEN-LAST:event_btnHTMLActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (element.getPrimaryName() != null) {
            if (element.getPrimaryName().length() > 0) {
                JFrame report = new ReportElement(element, app);
                report.setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Report Icon.gif")).getImage());
                report.setPreferredSize(new Dimension(550, 750));
                report.setLocationRelativeTo(null);
                report.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnReportActionPerformed

    private void txtSizeMaleMaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeMaleMaxFocusGained
        txtSizeMaleMax.setSelectionStart(0);
        txtSizeMaleMax.setSelectionEnd(txtSizeMaleMax.getText().length());
    }//GEN-LAST:event_txtSizeMaleMaxFocusGained

    private void txtSizeFemaleMaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeFemaleMaxFocusGained
        txtSizeFemaleMax.setSelectionStart(0);
        txtSizeFemaleMax.setSelectionEnd(txtSizeFemaleMax.getText().length());
    }//GEN-LAST:event_txtSizeFemaleMaxFocusGained

    private void txtWeightMaleMaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightMaleMaxFocusGained
        txtWeightMaleMax.setSelectionStart(0);
        txtWeightMaleMax.setSelectionEnd(txtWeightMaleMax.getText().length());
    }//GEN-LAST:event_txtWeightMaleMaxFocusGained

    private void txtWeightFemaleMaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightFemaleMaxFocusGained
        txtWeightFemaleMax.setSelectionStart(0);
        txtWeightFemaleMax.setSelectionEnd(txtWeightFemaleMax.getText().length());
    }//GEN-LAST:event_txtWeightFemaleMaxFocusGained

    private void resizeTables() {
        if (rdbSightings.isSelected()) {
            TableColumn column = null;
            for (int i = 0; i < tblLocation.getColumnModel().getColumnCount(); i++) {
                column = tblLocation.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(135);
                }
                else if (i == 1) {
                    column.setPreferredWidth(75);
                }
                else if (i == 2) {
                    column.setPreferredWidth(5);
                }
            }
        }
        else {
            TableColumn column = null;
            for (int i = 0; i < tblLocation.getColumnModel().getColumnCount(); i++) {
                column = tblLocation.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(100);
                }
                else if (i == 1) {
                    column.setPreferredWidth(35);
                }
                else if (i == 2) {
                    column.setPreferredWidth(35);
                }
            }
        }
    }

    private void setupNumberOfImages() {
        List<Foto> fotos = app.getDBI().list(new Foto("ELEMENT-" + element.getPrimaryName()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnHTML;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbActiveTime;
    private javax.swing.JComboBox cmbAddFrequency;
    private javax.swing.JComboBox cmbEndangeredStatus;
    private javax.swing.JComboBox cmbFeedingClass;
    private javax.swing.JComboBox cmbSizeUnits;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbWaterDependance;
    private javax.swing.JComboBox cmbWeightUnits;
    private javax.swing.JComboBox cmbWishList;
    private javax.swing.JPanel elementIncludes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblElementName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblNumberOfImages;
    private javax.swing.JLabel lblNumberOfLocations;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JRadioButton rdbLocations;
    private javax.swing.JRadioButton rdbSightings;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextArea txtBehaviourDescription;
    private javax.swing.JTextField txtBreedingNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDiagnosticDescription;
    private javax.swing.JTextField txtLifespan;
    private javax.swing.JTextArea txtNutrition;
    private javax.swing.JTextField txtOtherName;
    private javax.swing.JTextField txtPrimaryName;
    private javax.swing.JTextField txtReferenceID;
    private javax.swing.JTextField txtScienceName;
    private javax.swing.JTextField txtSizeFemaleMax;
    private javax.swing.JTextField txtSizeFemaleMin;
    private javax.swing.JTextField txtSizeMaleMax;
    private javax.swing.JTextField txtSizeMaleMin;
    private javax.swing.JTextField txtWeightFemaleMax;
    private javax.swing.JTextField txtWeightFemaleMin;
    private javax.swing.JTextField txtWeightMaleMax;
    private javax.swing.JTextField txtWeightMaleMin;
    private javax.swing.JTextField txtbreedingDuration;
    // End of variables declaration//GEN-END:variables
    
}
