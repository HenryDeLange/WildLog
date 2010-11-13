package wildlog.ui.panel;

import KmlGenerator.KmlGenerator;
import KmlGenerator.objects.KmlEntry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
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
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SizeType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.mapping.kml.util.KmlUtil;
import wildlog.ui.panel.interfaces.PanelCanSetupHeader;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;
import wildlog.ui.report.ReportElement;
import wildlog.utils.UtilsHTML;
import wildlog.utils.ui.UtilMapGenerator;


/**
 *
 * @author  henry.delange
 */
public class PanelElement extends PanelCanSetupHeader implements PanelNeedsRefreshWhenSightingAdded {
    // Variables:
    private Element element;
    
    /** Creates new form PanelElement */
    public PanelElement(Element inElement) {
        app = (WildLogApp) Application.getInstance();
        element = inElement;
        initComponents();
        imageIndex = 0;
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
        if (fotos.size() > 0) {
            Utils.setupFoto("ELEMENT-" + element.getPrimaryName(), imageIndex, lblImage, 300, app);
        }
        else {
            lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
        }
        setupNumberOfImages();

        tblLocation.getTableHeader().setReorderingAllowed(false);


        fixSelectAllForSpinners(spnSizeMaleMin);
        fixSelectAllForSpinners(spnSizeMaleMax);
        fixSelectAllForSpinners(spnSizeFemaleMin);
        fixSelectAllForSpinners(spnSizeFemaleMax);
        fixSelectAllForSpinners(spnWeightMaleMin);
        fixSelectAllForSpinners(spnWeightMaleMax);
        fixSelectAllForSpinners(spnWeightFemaleMin);
        fixSelectAllForSpinners(spnWeightFemaleMax);
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
    
    @Override
    public void setupTabHeader() {
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
        ((JTabbedPane)getParent()).setTabComponentAt(((JTabbedPane)getParent()).indexOfComponent(this), tabHeader);
    }
    
    private void closeTab() {
        ((JTabbedPane)getParent()).remove(this);
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
        btnReport = new javax.swing.JButton();
        btnHTML = new javax.swing.JButton();
        btnKML = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDistribution = new javax.swing.JTextArea();
        spnSizeMaleMin = new javax.swing.JSpinner();
        spnSizeMaleMax = new javax.swing.JSpinner();
        spnSizeFemaleMin = new javax.swing.JSpinner();
        spnSizeFemaleMax = new javax.swing.JSpinner();
        spnWeightMaleMin = new javax.swing.JSpinner();
        spnWeightMaleMax = new javax.swing.JSpinner();
        spnWeightFemaleMin = new javax.swing.JSpinner();
        spnWeightFemaleMax = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        cmbSizeType = new javax.swing.JComboBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelElement.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName(element.getPrimaryName());
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        elementIncludes.setBackground(resourceMap.getColor("elementIncludes.background")); // NOI18N
        elementIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setName("elementIncludes"); // NOI18N
        elementIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));
        elementIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        elementIncludes.add(btnPreviousImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 40, 50));

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
        elementIncludes.add(btnNextImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 300, 40, 50));

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
        elementIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 326, 90, -1));

        jLabel58.setText(resourceMap.getString("jLabel58.text")); // NOI18N
        jLabel58.setName("jLabel58"); // NOI18N
        elementIncludes.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 361, -1, -1));

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tblLocation.setAutoCreateRowSorter(true);
        tblLocation.setName("tblLocation"); // NOI18N
        tblLocation.setSelectionBackground(resourceMap.getColor("tblLocation.selectionBackground")); // NOI18N
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

        btnGoLocation.setBackground(resourceMap.getColor("btnGoLocation.background")); // NOI18N
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
        elementIncludes.add(btnGoLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 550, -1, 30));

        jSeparator11.setName("jSeparator11"); // NOI18N
        elementIncludes.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel59.setText(resourceMap.getString("jLabel59.text")); // NOI18N
        jLabel59.setName("jLabel59"); // NOI18N
        elementIncludes.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, -1));

        jLabel60.setText(resourceMap.getString("jLabel60.text")); // NOI18N
        jLabel60.setName("jLabel60"); // NOI18N
        elementIncludes.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jLabel61.setText(resourceMap.getString("jLabel61.text")); // NOI18N
        jLabel61.setName("jLabel61"); // NOI18N
        elementIncludes.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, -1, -1));

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
        elementIncludes.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 410, -1, -1));

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

        elementIncludes.add(jScrollPane16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 320, 310, 80));

        jScrollPane17.setName("jScrollPane17"); // NOI18N

        txtDiagnosticDescription.setColumns(20);
        txtDiagnosticDescription.setFont(resourceMap.getFont("txtDiagnosticDescription.font")); // NOI18N
        txtDiagnosticDescription.setLineWrap(true);
        txtDiagnosticDescription.setRows(5);
        txtDiagnosticDescription.setText(element.getDiagnosticDescription());
        txtDiagnosticDescription.setWrapStyleWord(true);
        txtDiagnosticDescription.setName("txtDiagnosticDescription"); // NOI18N
        jScrollPane17.setViewportView(txtDiagnosticDescription);

        elementIncludes.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 470, 80));

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        txtBehaviourDescription.setColumns(20);
        txtBehaviourDescription.setFont(resourceMap.getFont("txtBehaviourDescription.font")); // NOI18N
        txtBehaviourDescription.setLineWrap(true);
        txtBehaviourDescription.setRows(5);
        txtBehaviourDescription.setText(element.getBehaviourDescription());
        txtBehaviourDescription.setWrapStyleWord(true);
        txtBehaviourDescription.setName("txtBehaviourDescription"); // NOI18N
        jScrollPane18.setViewportView(txtBehaviourDescription);

        elementIncludes.add(jScrollPane18, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 410, 310, 80));

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
        elementIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 510, -1, 20));

        cmbSizeUnits.setModel(new DefaultComboBoxModel(UnitsSize.values()));
        cmbSizeUnits.setSelectedItem(element.getSizeUnit());
        cmbSizeUnits.setName("cmbSizeUnits"); // NOI18N
        elementIncludes.add(cmbSizeUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 510, 70, -1));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        elementIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 530, -1, 20));

        cmbWeightUnits.setModel(new DefaultComboBoxModel(UnitsWeight.values()));
        cmbWeightUnits.setSelectedItem(element.getWeightUnit());
        cmbWeightUnits.setName("cmbWeightUnits"); // NOI18N
        elementIncludes.add(cmbWeightUnits, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 530, 70, -1));

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
        elementIncludes.add(btnDeleteImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 326, 90, -1));

        btnMap.setBackground(resourceMap.getColor("btnMap.background")); // NOI18N
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
        elementIncludes.add(btnMap, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 150, 110, 40));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtNutrition.setColumns(20);
        txtNutrition.setFont(resourceMap.getFont("txtNutrition.font")); // NOI18N
        txtNutrition.setLineWrap(true);
        txtNutrition.setRows(3);
        txtNutrition.setText(element.getNutrition());
        txtNutrition.setWrapStyleWord(true);
        txtNutrition.setName("txtNutrition"); // NOI18N
        jScrollPane1.setViewportView(txtNutrition);

        elementIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 430, 270, 60));

        btnAddSighting.setBackground(resourceMap.getColor("btnAddSighting.background")); // NOI18N
        btnAddSighting.setFont(resourceMap.getFont("btnAddSighting.font")); // NOI18N
        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSighting.setMargin(new java.awt.Insets(2, 10, 2, 10));
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        elementIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 110, 40));

        lblNumberOfLocations.setFont(resourceMap.getFont("lblNumberOfLocations.font")); // NOI18N
        lblNumberOfLocations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfLocations.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfLocations.border.lineColor"))); // NOI18N
        lblNumberOfLocations.setName("lblNumberOfLocations"); // NOI18N
        elementIncludes.add(lblNumberOfLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 550, 30, 30));

        lblNumberOfImages.setFont(resourceMap.getFont("lblNumberOfImages.font")); // NOI18N
        lblNumberOfImages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfImages.setText(resourceMap.getString("lblNumberOfImages.text")); // NOI18N
        lblNumberOfImages.setName("lblNumberOfImages"); // NOI18N
        elementIncludes.add(lblNumberOfImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 330, 40, 20));

        rdbLocations.setBackground(resourceMap.getColor("rdbLocations.background")); // NOI18N
        buttonGroup1.add(rdbLocations);
        rdbLocations.setSelected(true);
        rdbLocations.setText(resourceMap.getString("rdbLocations.text")); // NOI18N
        rdbLocations.setToolTipText(resourceMap.getString("rdbLocations.toolTipText")); // NOI18N
        rdbLocations.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbLocations.setName("rdbLocations"); // NOI18N
        elementIncludes.add(rdbLocations, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 550, -1, 30));

        rdbSightings.setBackground(resourceMap.getColor("rdbSightings.background")); // NOI18N
        buttonGroup1.add(rdbSightings);
        rdbSightings.setText(resourceMap.getString("rdbSightings.text")); // NOI18N
        rdbSightings.setToolTipText(resourceMap.getString("rdbSightings.toolTipText")); // NOI18N
        rdbSightings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rdbSightings.setName("rdbSightings"); // NOI18N
        rdbSightings.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdbSightingsItemStateChanged(evt);
            }
        });
        elementIncludes.add(rdbSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 550, -1, 30));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        elementIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 76, -1, -1));

        txtReferenceID.setText(element.getReferenceID());
        txtReferenceID.setName("txtReferenceID"); // NOI18N
        elementIncludes.add(txtReferenceID, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 76, 80, -1));

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
        elementIncludes.add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, 110, 40));

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
        elementIncludes.add(btnHTML, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 250, 110, 30));

        btnKML.setBackground(resourceMap.getColor("btnKML.background")); // NOI18N
        btnKML.setIcon(resourceMap.getIcon("btnKML.icon")); // NOI18N
        btnKML.setText(resourceMap.getString("btnKML.text")); // NOI18N
        btnKML.setToolTipText(resourceMap.getString("btnKML.toolTipText")); // NOI18N
        btnKML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKML.setName("btnKML"); // NOI18N
        btnKML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKMLActionPerformed(evt);
            }
        });
        elementIncludes.add(btnKML, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 290, 110, 30));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        elementIncludes.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 495, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        elementIncludes.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 495, -1, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        elementIncludes.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 495, -1, -1));

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        elementIncludes.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 495, -1, -1));

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        elementIncludes.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 320, -1, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDistribution.setColumns(20);
        txtDistribution.setFont(resourceMap.getFont("txtDistribution.font")); // NOI18N
        txtDistribution.setLineWrap(true);
        txtDistribution.setRows(3);
        txtDistribution.setText(element.getDistribution());
        txtDistribution.setWrapStyleWord(true);
        txtDistribution.setName("txtDistribution"); // NOI18N
        jScrollPane2.setViewportView(txtDistribution);

        elementIncludes.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 340, 270, 60));

        spnSizeMaleMin.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnSizeMaleMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnSizeMaleMin.setName("spnSizeMaleMin"); // NOI18N
        spnSizeMaleMin.setValue(element.getSizeMaleMin());
        elementIncludes.add(spnSizeMaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 510, 50, -1));

        spnSizeMaleMax.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnSizeMaleMax.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnSizeMaleMax.setName("spnSizeMaleMax"); // NOI18N
        spnSizeMaleMax.setValue(element.getSizeMaleMax());
        elementIncludes.add(spnSizeMaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 50, -1));

        spnSizeFemaleMin.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnSizeFemaleMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnSizeFemaleMin.setName("spnSizeFemaleMin"); // NOI18N
        spnSizeFemaleMin.setValue(element.getSizeFemaleMin());
        elementIncludes.add(spnSizeFemaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 50, -1));

        spnSizeFemaleMax.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnSizeFemaleMax.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnSizeFemaleMax.setName("spnSizeFemaleMax"); // NOI18N
        spnSizeFemaleMax.setValue(element.getSizeFemaleMax());
        elementIncludes.add(spnSizeFemaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 540, 50, -1));

        spnWeightMaleMin.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnWeightMaleMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnWeightMaleMin.setName("spnWeightMaleMin"); // NOI18N
        spnWeightMaleMin.setValue(element.getWeightMaleMin());
        elementIncludes.add(spnWeightMaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 510, 50, -1));

        spnWeightMaleMax.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnWeightMaleMax.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnWeightMaleMax.setName("spnWeightMaleMax"); // NOI18N
        spnWeightMaleMax.setValue(element.getWeightMaleMax());
        elementIncludes.add(spnWeightMaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 510, 50, -1));

        spnWeightFemaleMin.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnWeightFemaleMin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnWeightFemaleMin.setName("spnWeightFemaleMin"); // NOI18N
        spnWeightFemaleMin.setValue(element.getWeightFemaleMin());
        elementIncludes.add(spnWeightFemaleMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 540, 50, -1));

        spnWeightFemaleMax.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), Double.valueOf(0.0d), null, Double.valueOf(0.1d)));
        spnWeightFemaleMax.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnWeightFemaleMax.setName("spnWeightFemaleMax"); // NOI18N
        spnWeightFemaleMax.setValue(element.getWeightFemaleMax());
        elementIncludes.add(spnWeightFemaleMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 540, 50, -1));

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        elementIncludes.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 540, 40, 20));

        cmbSizeType.setModel(new DefaultComboBoxModel(SizeType.values()));
        cmbSizeType.setSelectedItem(element.getSizeType());
        cmbSizeType.setName("cmbSizeType"); // NOI18N
        elementIncludes.add(cmbSizeType, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 540, 110, -1));

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
                element.setDistribution(txtDistribution.getText());
                element.setNutrition(txtNutrition.getText());
                element.setWaterDependance((WaterDependancy)cmbWaterDependance.getSelectedItem());
                try {
                    element.setSizeMaleMin(Double.valueOf(spnSizeMaleMin.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnSizeMaleMin.setValue("");
                }
                try {
                    element.setSizeFemaleMin(Double.valueOf(spnSizeFemaleMin.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnSizeFemaleMin.setValue("");
                }
                try {
                    element.setSizeMaleMax(Double.valueOf(spnSizeMaleMax.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnSizeMaleMax.setValue("");
                }
                try {
                    element.setSizeFemaleMax(Double.valueOf(spnSizeFemaleMax.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnSizeFemaleMax.setValue("");
                }
                element.setBreedingNumber(txtBreedingNumber.getText());
                element.setSizeUnit((UnitsSize)cmbSizeUnits.getSelectedItem());
                element.setSizeType((SizeType)cmbSizeType.getSelectedItem());
                element.setWeightUnit((UnitsWeight)cmbWeightUnits.getSelectedItem());
                try {
                    element.setWeightMaleMin(Double.valueOf(spnWeightMaleMin.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnWeightMaleMin.setValue("");
                }
                try {
                    element.setWeightFemaleMin(Double.valueOf(spnWeightFemaleMin.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnWeightFemaleMin.setValue("");
                }
                try {
                    element.setWeightMaleMax(Double.valueOf(spnWeightMaleMax.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnWeightMaleMax.setValue("");
                }
                try {
                    element.setWeightFemaleMax(Double.valueOf(spnWeightFemaleMax.getValue().toString()));
                }
                catch (NumberFormatException e) {
                    spnWeightFemaleMax.setValue("");
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
                tempPanel = UtilPanelGenerator.getLocationPanel((String)tblLocation.getValueAt(selectedRows[t], 0));
                UtilPanelGenerator.addPanelAsTab(tempPanel, (JTabbedPane)getParent());
//                parent.add(tempPanel);
//                tempPanel.setupTabHeader();
            }
//            if (tempPanel != null) parent.setSelectedComponent(tempPanel);
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
            else {
                JOptionPane.showMessageDialog(this, "Sorry you can't view multiple Sightings at once.", "Viewing Multiple Sightings", JOptionPane.WARNING_MESSAGE);
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
            UtilTableGenerator.setupLocationsForElementTable(tblLocation, element);
        }
        else
            tblLocation.setModel(new DefaultTableModel(new String[]{"No Locations"}, 0));
        rdbLocations.setSelected(true);
        lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
    }//GEN-LAST:event_formComponentShown

    private void btnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapActionPerformed
        // Clear old points
        UtilMapGenerator.clearMap(app);
        
        // Load points
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
                UtilMapGenerator.addPoint(lat, lon, new Color(230, 90, 50), sightingList.get(t), app);
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
                UtilMapGenerator.addPoint(lat, lon, new Color(230, 190, 50), sightingList.get(t), app);
            }
        }
        
        // Open Map
        if (app.isUseOnlineMap()) {
            app.getMapOnline().setTitle("WildLog Map - Online: " + element.getPrimaryName());
            app.getMapOnline().setLocationRelativeTo(this);
            app.getMapOnline().showMap(Color.yellow);
        }
        else {
            app.getMapOffline().changeTitle("WildLog Map - Offline: " + element.getPrimaryName());
            app.getMapOffline().showMap();
        }
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
        Utils.openFile("ELEMENT-" + element.getPrimaryName(), imageIndex, app);
    }//GEN-LAST:event_lblImageMouseReleased

    private void tblLocationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnGoLocationActionPerformed(null);
    }//GEN-LAST:event_tblLocationKeyPressed

    private void rdbSightingsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdbSightingsItemStateChanged
        if (rdbSightings.isSelected()) {
            if (element.getPrimaryName() != null) {
                UtilTableGenerator.setupSightingsForElementTable(tblLocation, element);
                tblLocation.setSelectionBackground(new Color(125,120,93));
            }
            else
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Sightings"}, 0));
        }
        else {
            if (element.getPrimaryName() != null) {
                UtilTableGenerator.setupLocationsForElementTable(tblLocation, element);
                tblLocation.setSelectionBackground(new Color(67,97,113));
            }
            else
                tblLocation.setModel(new DefaultTableModel(new String[]{"No Locations"}, 0));
        }
        lblNumberOfLocations.setText(Integer.toString(tblLocation.getRowCount()));
    }//GEN-LAST:event_rdbSightingsItemStateChanged

    private void tblLocationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLocationMouseClicked
        if (evt.getClickCount() == 2) {
            btnGoLocationActionPerformed(null);
        }
    }//GEN-LAST:event_tblLocationMouseClicked

    private void btnHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLActionPerformed
        Utils.openFile(UtilsHTML.exportHTML(element, app));
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

    private void btnKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKMLActionPerformed
        // First export to HTML
        UtilsHTML.exportHTML(element, app);
        // Nou doen die KML deel
        String path = File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "KML";
        File tempFile = new File(path);
        tempFile.mkdirs();
        // Make sure icons exist in the KML folder
        KmlUtil.copyKmlIcons(app, path);
        // KML Stuff
        KmlGenerator kmlgen = new KmlGenerator();
        String finalPath = path + File.separatorChar + "WildLogMarkers - Creature (" + element.getPrimaryName() + ").kml";
        kmlgen.setKmlPath(finalPath);
        // Get entries for Sightings and Locations
        Map<String, List<KmlEntry>> entries = new HashMap<String, List<KmlEntry>>();
        // Sightings
        Sighting tempSighting = new Sighting();
        tempSighting.setElementName(element.getPrimaryName());
        List<Sighting> listSightings = app.getDBI().list(tempSighting);
        for (int t = 0; t < listSightings.size(); t++) {
            String key = listSightings.get(t).getLocationName();
            if (!entries.containsKey(key)) {
                entries.put(key, new ArrayList<KmlEntry>());
             }
            entries.get(key).add(listSightings.get(t).toKML(t, app));
        }
        // Generate KML
        kmlgen.generateFile(entries, KmlUtil.getKmlStyles());
        // Try to open the Kml file
        Utils.openFile(finalPath);
    }//GEN-LAST:event_btnKMLActionPerformed


    private void setupNumberOfImages() {
        List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("ELEMENT-" + element.getPrimaryName()));
        if (fotos.size() > 0)
            lblNumberOfImages.setText(imageIndex+1 + " of " + fotos.size());
        else
            lblNumberOfImages.setText("0 of 0");
    }

    private void fixSelectAllForSpinners(JSpinner inSpinner) {
        // Fix die bug met spinners se selection
        ((JSpinner.NumberEditor)inSpinner.getEditor()).getTextField().addFocusListener(
            new FocusAdapter() {
            @Override
                public void focusGained(FocusEvent e) {
                    if (e.getSource() instanceof JTextComponent) {
                        final JTextComponent textComponent=((JTextComponent)e.getSource());
                        SwingUtilities.invokeLater(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        textComponent.selectAll();
                                    }
                                }
                        );
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                }
            }
        );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnGoLocation;
    private javax.swing.JButton btnHTML;
    private javax.swing.JButton btnKML;
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
    private javax.swing.JComboBox cmbSizeType;
    private javax.swing.JComboBox cmbSizeUnits;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbWaterDependance;
    private javax.swing.JComboBox cmbWeightUnits;
    private javax.swing.JComboBox cmbWishList;
    private javax.swing.JPanel elementIncludes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane2;
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
    private javax.swing.JSpinner spnSizeFemaleMax;
    private javax.swing.JSpinner spnSizeFemaleMin;
    private javax.swing.JSpinner spnSizeMaleMax;
    private javax.swing.JSpinner spnSizeMaleMin;
    private javax.swing.JSpinner spnWeightFemaleMax;
    private javax.swing.JSpinner spnWeightFemaleMin;
    private javax.swing.JSpinner spnWeightMaleMax;
    private javax.swing.JSpinner spnWeightMaleMin;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTextArea txtBehaviourDescription;
    private javax.swing.JTextField txtBreedingNumber;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextArea txtDiagnosticDescription;
    private javax.swing.JTextArea txtDistribution;
    private javax.swing.JTextField txtLifespan;
    private javax.swing.JTextArea txtNutrition;
    private javax.swing.JTextField txtOtherName;
    private javax.swing.JTextField txtPrimaryName;
    private javax.swing.JTextField txtReferenceID;
    private javax.swing.JTextField txtScienceName;
    private javax.swing.JTextField txtbreedingDuration;
    // End of variables declaration//GEN-END:variables
    
}
