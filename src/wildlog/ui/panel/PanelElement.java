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
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Foto;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.WishRating;
import wildlog.ui.util.UtilPanelGenerator;
import wildlog.ui.util.UtilTableGenerator;
import wildlog.ui.util.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;


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
        if (element.getFotos() != null && element.getFotos().size() > 0) {
            setupFotos(0);
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        }
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
        if (element == null) return true;
        if (element.getPrimaryName() == null) return true;
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
    
    private void setupFotos(int inIndex) {
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(element.getFotos().get(inIndex).getFileLocation()), 300));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        txtSizeMale = new javax.swing.JTextField();
        txtSizeFemale = new javax.swing.JTextField();
        txtWeightMale = new javax.swing.JTextField();
        txtWeightFemale = new javax.swing.JTextField();
        txtBreedingAge = new javax.swing.JTextField();
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
        elementIncludes.add(txtScienceName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 76, 490, -1));

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
        elementIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 28, 110, 60));

        jSeparator10.setName("jSeparator10"); // NOI18N
        elementIncludes.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

        btnPreviousImage.setIcon(resourceMap.getIcon("btnPreviousImage.icon")); // NOI18N
        btnPreviousImage.setText(resourceMap.getString("btnPreviousImage.text")); // NOI18N
        btnPreviousImage.setToolTipText(resourceMap.getString("btnPreviousImage.toolTipText")); // NOI18N
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
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        elementIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 326, 100, -1));

        jLabel58.setText(resourceMap.getString("jLabel58.text")); // NOI18N
        jLabel58.setName("jLabel58"); // NOI18N
        elementIncludes.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 361, -1, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setModel(utilTableGenerator.getLocationsForElementTable(element));
        tblLocation.setName("tblLocation"); // NOI18N
        jScrollPane15.setViewportView(tblLocation);

        elementIncludes.add(jScrollPane15, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 376, 290, 170));

        btnGoLocation.setIcon(resourceMap.getIcon("btnGoLocation.icon")); // NOI18N
        btnGoLocation.setText(resourceMap.getString("btnGoLocation.text")); // NOI18N
        btnGoLocation.setToolTipText(resourceMap.getString("btnGoLocation.toolTipText")); // NOI18N
        btnGoLocation.setName("btnGoLocation"); // NOI18N
        btnGoLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLocationActionPerformed(evt);
            }
        });
        elementIncludes.add(btnGoLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 548, 250, 30));

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
        elementIncludes.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 500, -1, -1));

        jLabel72.setText(resourceMap.getString("jLabel72.text")); // NOI18N
        jLabel72.setName("jLabel72"); // NOI18N
        elementIncludes.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, -1, -1));

        jLabel73.setText(resourceMap.getString("jLabel73.text")); // NOI18N
        jLabel73.setName("jLabel73"); // NOI18N
        elementIncludes.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 500, -1, -1));

        jLabel74.setText(resourceMap.getString("jLabel74.text")); // NOI18N
        jLabel74.setName("jLabel74"); // NOI18N
        elementIncludes.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 520, -1, -1));

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

        txtSizeMale.setText(Double.toString(element.getSizeMaleAverage()));
        txtSizeMale.setName("txtSizeMale"); // NOI18N
        txtSizeMale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeMaleFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeMale, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 496, 110, -1));

        txtSizeFemale.setText(Double.toString(element.getSizeFemaleAverage()));
        txtSizeFemale.setName("txtSizeFemale"); // NOI18N
        txtSizeFemale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSizeFemaleFocusGained(evt);
            }
        });
        elementIncludes.add(txtSizeFemale, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 520, 110, -1));

        txtWeightMale.setText(Double.toString(element.getWeightMaleAverage()));
        txtWeightMale.setName("txtWeightMale"); // NOI18N
        txtWeightMale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightMaleFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightMale, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 496, 110, -1));

        txtWeightFemale.setText(Double.toString(element.getWeightFemaleAverage()));
        txtWeightFemale.setName("txtWeightFemale"); // NOI18N
        txtWeightFemale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtWeightFemaleFocusGained(evt);
            }
        });
        elementIncludes.add(txtWeightFemale, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 520, 110, -1));

        txtBreedingAge.setText(element.getBreedingAge());
        txtBreedingAge.setName("txtBreedingAge"); // NOI18N
        txtBreedingAge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBreedingAgeFocusGained(evt);
            }
        });
        elementIncludes.add(txtBreedingAge, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 148, 150, -1));

        txtbreedingDuration.setText(element.getBreedingDuration());
        txtbreedingDuration.setName("txtbreedingDuration"); // NOI18N
        txtbreedingDuration.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtbreedingDurationFocusGained(evt);
            }
        });
        elementIncludes.add(txtbreedingDuration, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 172, 150, -1));

        txtBreedingNumber.setText(Double.toString(element.getBreedingNumber()));
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
        btnUploadImage.setName("btnUploadImage"); // NOI18N
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        elementIncludes.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 220, -1));

        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
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
        elementIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 510, -1, 20));

        cmbSizeUnits.setModel(new DefaultComboBoxModel(UnitsSize.values()));
        cmbSizeUnits.setName("cmbSizeUnits"); // NOI18N
        elementIncludes.add(cmbSizeUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 510, 70, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        elementIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 510, -1, 20));

        cmbWeightUnits.setModel(new DefaultComboBoxModel(UnitsWeight.values()));
        cmbWeightUnits.setName("cmbWeightUnits"); // NOI18N
        elementIncludes.add(cmbWeightUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 510, 70, -1));

        btnDeleteImage.setIcon(resourceMap.getIcon("btnDeleteImage.icon")); // NOI18N
        btnDeleteImage.setText(resourceMap.getString("btnDeleteImage.text")); // NOI18N
        btnDeleteImage.setToolTipText(resourceMap.getString("btnDeleteImage.toolTipText")); // NOI18N
        btnDeleteImage.setName("btnDeleteImage"); // NOI18N
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });
        elementIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 326, 100, -1));

        btnMap.setFont(resourceMap.getFont("btnMap.font")); // NOI18N
        btnMap.setIcon(resourceMap.getIcon("btnMap.icon")); // NOI18N
        btnMap.setText(resourceMap.getString("btnMap.text")); // NOI18N
        btnMap.setToolTipText(resourceMap.getString("btnMap.toolTipText")); // NOI18N
        btnMap.setName("btnMap"); // NOI18N
        btnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapActionPerformed(evt);
            }
        });
        elementIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 110, 40));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtNutrition.setColumns(20);
        txtNutrition.setFont(resourceMap.getFont("txtNutrition.font")); // NOI18N
        txtNutrition.setRows(5);
        txtNutrition.setText(element.getNutrition());
        txtNutrition.setWrapStyleWord(true);
        txtNutrition.setName("txtNutrition"); // NOI18N
        jScrollPane1.setViewportView(txtNutrition);

        elementIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 250, 270, 60));

        btnAddSighting.setFont(resourceMap.getFont("btnAddSighting.font")); // NOI18N
        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        elementIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 150, 110, 40));

        lblNumberOfLocations.setFont(resourceMap.getFont("lblNumberOfLocations.font")); // NOI18N
        lblNumberOfLocations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfLocations.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfLocations.border.lineColor"))); // NOI18N
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        elementIncludes.add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 550, 30, 20));

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
        if (txtPrimaryName.getText().length() > 0) {
            String oldName = element.getPrimaryName();
            element.setPrimaryName(txtPrimaryName.getText()); // Used for indexing (ID)
            element.setOtherName(txtOtherName.getText());
            element.setScientificName(txtScienceName.getText());
            element.setDescription(txtDescription.getText());
            element.setNutrition(txtNutrition.getText());
            element.setWaterDependance((WaterDependancy)cmbWaterDependance.getSelectedItem());
            try {
                element.setSizeMaleAverage(Double.valueOf(txtSizeMale.getText()));
            }
            catch (NumberFormatException e) {
                System.out.println("Not a Number...");
                txtSizeMale.setText("");
            }
            try {
                element.setSizeFemaleAverage(Double.valueOf(txtSizeFemale.getText()));
            }
            catch (NumberFormatException e) {
                System.out.println("Not a Number...");
                txtSizeFemale.setText("");
            }
            try {
                element.setBreedingNumber(Double.valueOf(txtBreedingNumber.getText()));
            }
            catch (NumberFormatException e) {
                System.out.println("Not a Number...");
                txtBreedingNumber.setText("");
            }
            element.setSizeUnit((UnitsSize)cmbSizeUnits.getSelectedItem());
            element.setWeightUnit((UnitsWeight)cmbWeightUnits.getSelectedItem());
            try {
                element.setWeightMaleAverage(Double.valueOf(txtWeightMale.getText()));
            }
            catch (NumberFormatException e) {
                System.out.println("Not a Number...");
                txtWeightMale.setText("");
            }
            try {
                element.setWeightFemaleAverage(Double.valueOf(txtWeightFemale.getText()));
            }
            catch (NumberFormatException e) {
                System.out.println("Not a Number...");
                txtWeightFemale.setText("");
            }
            element.setBreedingDuration(txtbreedingDuration.getText());
            element.setBreedingAge(txtBreedingAge.getText());
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
            if (app.getDBI().createOrUpdate(element) == true) {
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
}//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        btnUpdateActionPerformed(evt);
        if (!txtPrimaryName.getBackground().equals(Color.RED)) {
            Utils.uploadImage(element, "Creatures"+File.separatorChar+element.getPrimaryName(), this);
            setupFotos(element.getFotos().size() - 1);
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnUploadImageActionPerformed

    private void btnPreviousImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageActionPerformed
        if (element.getFotos() != null && element.getFotos().size() > 0) {
            if (imageIndex > 0) setupFotos(--imageIndex);
            else {
                imageIndex = element.getFotos().size() - 1;
                setupFotos(imageIndex);
            }
        }
    }//GEN-LAST:event_btnPreviousImageActionPerformed

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (element.getFotos() != null && element.getFotos().size() > 0) {
            if (imageIndex < element.getFotos().size() - 1) setupFotos(++imageIndex);
            else {
               imageIndex = 0;
                setupFotos(imageIndex);
            }
        }
    }//GEN-LAST:event_btnNextImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (element.getFotos() != null && element.getFotos().size() > 0) {
            element.getFotos().add(0, element.getFotos().get(imageIndex++));
            element.getFotos().remove(imageIndex);
            imageIndex = 0;
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
    }//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLocationActionPerformed
        int[] selectedRows = tblLocation.getSelectedRows();
        PanelLocation tempPanel = null;
        for (int t = 0; t < selectedRows.length; t++) {
            tempPanel = utilPanelGenerator.getLocationPanel((String)tblLocation.getValueAt(selectedRows[t], 0));
            parent = (JTabbedPane) getParent();
            parent.add(tempPanel);
            tempPanel.setupTabHeader();
        }
        if (tempPanel != null) parent.setSelectedComponent(tempPanel);
    }//GEN-LAST:event_btnGoLocationActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tblLocation.setModel(utilTableGenerator.getLocationsForElementTable(element));
        if (element.getPrimaryName() != null) {
            Sighting sighting = new Sighting();
            sighting.setElement(element);
            lblNumberOfSightings.setText(Integer.toString(app.getDBI().list(sighting).size()));
            lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
        }
        else {
            lblNumberOfSightings.setText("0");
            lblNumberOfLocations.setText("0");
        }
    }//GEN-LAST:event_formComponentShown

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        app.getMapFrame().clearPoints();
        Sighting sigting = new Sighting();
        sigting.setElement(element);
        List<Sighting> sightingList = app.getDBI().list(sigting);
        boolean foundPosition = false;
        for (int t = 0; t < sightingList.size(); t++) {
            foundPosition = false;
            if (sightingList.get(t).getLatitude() != null && sightingList.get(t).getLongitude() != null)
            if (!sightingList.get(t).getLatitude().equals(Latitudes.NONE) && !sightingList.get(t).getLongitude().equals(Longitudes.NONE)) {
                float lat = sightingList.get(t).getLatDegrees();
                lat = lat + sightingList.get(t).getLatMinutes()/60f;
                lat = lat + (sightingList.get(t).getLatSeconds()/60f)/60f;
                if (sightingList.get(t).getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sightingList.get(t).getLonDegrees();
                lon = lon + sightingList.get(t).getLonMinutes()/60f;
                lon = lon + (sightingList.get(t).getLonSeconds()/60f)/60f;
                if (sightingList.get(t).getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
                foundPosition = true;
            }
            // If the sighting did not have a position use the location's
            if (foundPosition == false) {
                float lat = sightingList.get(t).getLocation().getLatDegrees();
                lat = lat + sightingList.get(t).getLocation().getLatMinutes()/60f;
                lat = lat + (sightingList.get(t).getLocation().getLatSeconds()/60f)/60f;
                if (sightingList.get(t).getLocation().getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sightingList.get(t).getLocation().getLonDegrees();
                lon = lon + sightingList.get(t).getLocation().getLonMinutes()/60f;
                lon = lon + (sightingList.get(t).getLocation().getLonSeconds()/60f)/60f;
                if (sightingList.get(t).getLocation().getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
            }
        }
        app.getMapFrame().changeTitle("WildLog Map - Creature: " + element.getPrimaryName());
        app.getMapFrame().showMap();
    }//GEN-LAST:event_btnMapActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        if (element.getFotos() != null) {
            if (element.getFotos().size() > 0) {
                Foto tempFoto = element.getFotos().get(imageIndex);
                element.getFotos().remove(tempFoto);
                app.getDBI().delete(tempFoto);
                app.getDBI().createOrUpdate(element);
                if (element.getFotos().size() >= 1) {
                    // Behave like moving back button was pressed
                    btnPreviousImageActionPerformed(evt);
                }
                else {
                    lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
                }
                // everything went well - saving
                btnUpdateActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        Sighting sighting = new Sighting();
        sighting.setElement(element);
        final JDialog dialog = new JDialog(app.getMainFrame(), "Add a New Sighting", true);
        dialog.setLayout(new AbsoluteLayout());
        dialog.setSize(965, 625);
        dialog.add(new PanelSighting(sighting, null, this), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        dialog.setLocationRelativeTo(this);
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
        dialog.setIconImage(icon.getImage());
        dialog.setVisible(true);
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        if (System.getProperty("os.name").equals("Windows XP")) {
            try {
                if (element.getFotos() != null)
                    if (element.getFotos().size() > 0)
                        Runtime.getRuntime().exec("cmd /c start " + element.getFotos().get(imageIndex).getOriginalFotoLocation());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_lblImageMouseClicked

    private void txtSizeMaleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeMaleFocusGained
        txtSizeMale.setSelectionStart(0);
        txtSizeMale.setSelectionEnd(txtSizeMale.getText().length());
    }//GEN-LAST:event_txtSizeMaleFocusGained

    private void txtSizeFemaleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSizeFemaleFocusGained
        txtSizeFemale.setSelectionStart(0);
        txtSizeFemale.setSelectionEnd(txtSizeFemale.getText().length());
    }//GEN-LAST:event_txtSizeFemaleFocusGained

    private void txtWeightMaleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightMaleFocusGained
        txtWeightMale.setSelectionStart(0);
        txtWeightMale.setSelectionEnd(txtWeightMale.getText().length());
    }//GEN-LAST:event_txtWeightMaleFocusGained

    private void txtWeightFemaleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWeightFemaleFocusGained
        txtWeightFemale.setSelectionStart(0);
        txtWeightFemale.setSelectionEnd(txtWeightFemale.getText().length());
    }//GEN-LAST:event_txtWeightFemaleFocusGained

    private void txtBreedingAgeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBreedingAgeFocusGained
        txtBreedingAge.setSelectionStart(0);
        txtBreedingAge.setSelectionEnd(txtBreedingAge.getText().length());
    }//GEN-LAST:event_txtBreedingAgeFocusGained

    private void txtbreedingDurationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtbreedingDurationFocusGained
        txtbreedingDuration.setSelectionStart(0);
        txtbreedingDuration.setSelectionEnd(txtbreedingDuration.getText().length());
    }//GEN-LAST:event_txtbreedingDurationFocusGained

    private void txtBreedingNumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBreedingNumberFocusGained
        txtBreedingNumber.setSelectionStart(0);
        txtBreedingNumber.setSelectionEnd(txtBreedingNumber.getText().length());
    }//GEN-LAST:event_txtBreedingNumberFocusGained
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnMap;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnSetMainImage;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
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
    private javax.swing.JLabel lblNumberOfLocations;
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextArea txtBehaviourDescription;
    private javax.swing.JTextField txtBreedingAge;
    private javax.swing.JTextField txtBreedingNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDiagnosticDescription;
    private javax.swing.JTextArea txtNutrition;
    private javax.swing.JTextField txtOtherName;
    private javax.swing.JTextField txtPrimaryName;
    private javax.swing.JTextField txtScienceName;
    private javax.swing.JTextField txtSizeFemale;
    private javax.swing.JTextField txtSizeMale;
    private javax.swing.JTextField txtWeightFemale;
    private javax.swing.JTextField txtWeightMale;
    private javax.swing.JTextField txtbreedingDuration;
    // End of variables declaration//GEN-END:variables
    
}
