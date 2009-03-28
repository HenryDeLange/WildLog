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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jdesktop.application.Application;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.ui.util.UtilPanelGenerator;
import wildlog.ui.util.UtilTableGenerator;
import wildlog.ui.util.Utils;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.ui.panel.interfaces.PanelNeedsRefreshWhenSightingAdded;

/**
 *
 * @author  henry.delange
 */
public class PanelVisit extends javax.swing.JPanel implements PanelNeedsRefreshWhenSightingAdded {
    private Visit visit;
    private Location locationForVisit;
    private Sighting sighting;
    private JTabbedPane parent;
    private UtilTableGenerator utilTableGenerator;
    private UtilPanelGenerator utilPanelGenerator;
    private int imageIndex;
    private int imageSightingIndex;
    private WildLogApp app;
    
    /** Creates new form PanelVisit */
    public PanelVisit(Location inLocation, Visit inVisit) {
        app = (WildLogApp) Application.getInstance();
        locationForVisit = inLocation;
        visit = inVisit;
        sighting = new Sighting();
        //sighting.setLocation(locationForVisit);
        utilTableGenerator = new UtilTableGenerator();
        utilPanelGenerator = new UtilPanelGenerator();
        initComponents();
        imageIndex = 0;
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
        //app.getDBI().refresh(locationForVisit);
        formComponentShown(null);
    }
    
    private void refreshSightingInfo() {
        if (sighting != null) {
            if (sighting.getElement() != null) {
                if (sighting.getElement().getFotos() != null)
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getElement().getFotos().get(0).getFileLocation()), 150));
                else
                    lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            else {
                lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            }
            lblSightingsImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
            if (sighting.getFotos() != null)
                if (sighting.getFotos().size() > 0 )
                    setupFotosSightings(0);
        }
        else {
            lblElementImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
//            lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
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
        lblSightingsImage = new javax.swing.JLabel();
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

        setMaximumSize(new java.awt.Dimension(1005, 585));
        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1005, 585));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        visitIncludes.setMaximumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setMinimumSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setName("visitIncludes"); // NOI18N
        visitIncludes.setPreferredSize(new java.awt.Dimension(1005, 585));
        visitIncludes.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
        lblVisitName.setFont(resourceMap.getFont("lblVisitName.font")); // NOI18N
        lblVisitName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVisitName.setText(visit.getName() + " - [" + locationForVisit.getName() + "]");
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
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        visitIncludes.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 28, 110, 60));

        jLabel53.setText(resourceMap.getString("jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53"); // NOI18N
        visitIncludes.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, -1, -1));

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setFont(resourceMap.getFont("txtDescription.font")); // NOI18N
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setText(visit.getDescription());
        txtDescription.setWrapStyleWord(true);
        txtDescription.setName("txtDescription"); // NOI18N
        jScrollPane14.setViewportView(txtDescription);

        visitIncludes.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, 310, 90));

        jLabel54.setText(resourceMap.getString("jLabel54.text")); // NOI18N
        jLabel54.setName("jLabel54"); // NOI18N
        visitIncludes.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 140, -1, -1));

        cmbType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbType.setSelectedItem(visit.getType());
        cmbType.setName("cmbType"); // NOI18N
        visitIncludes.add(cmbType, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 160, 190, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        visitIncludes.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 52, -1, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        visitIncludes.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 52, -1, -1));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        visitIncludes.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, -1, -1));

        cmbGameWatchIntensity.setModel(new DefaultComboBoxModel(GameWatchIntensity.values()));
        cmbGameWatchIntensity.setSelectedItem(visit.getGameWatchingIntensity());
        cmbGameWatchIntensity.setName("cmbGameWatchIntensity"); // NOI18N
        visitIncludes.add(cmbGameWatchIntensity, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 110, 190, -1));

        dtpStartDate.setDate(visit.getStartDate());
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        visitIncludes.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 52, 200, -1));

        dtpEndDate.setDate(visit.getEndDate());
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        visitIncludes.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 52, 200, -1));

        jSeparator1.setForeground(resourceMap.getColor("jSeparator1.foreground")); // NOI18N
        jSeparator1.setName("jSeparator1"); // NOI18N
        visitIncludes.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 190, 695, 10));

        jSeparator2.setForeground(resourceMap.getColor("jSeparator2.foreground")); // NOI18N
        jSeparator2.setName("jSeparator2"); // NOI18N
        visitIncludes.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 360, 310, 10));

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

        visitIncludes.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 219, 580, 360));

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

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        visitIncludes.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 200, -1, -1));

        btnAddSighting.setIcon(resourceMap.getIcon("btnAddSighting.icon")); // NOI18N
        btnAddSighting.setText(resourceMap.getString("btnAddSighting.text")); // NOI18N
        btnAddSighting.setToolTipText(resourceMap.getString("btnAddSighting.toolTipText")); // NOI18N
        btnAddSighting.setName("btnAddSighting"); // NOI18N
        btnAddSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnAddSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 90, 30));

        btnDeleteSighting.setIcon(resourceMap.getIcon("btnDeleteSighting.icon")); // NOI18N
        btnDeleteSighting.setText(resourceMap.getString("btnDeleteSighting.text")); // NOI18N
        btnDeleteSighting.setToolTipText(resourceMap.getString("btnDeleteSighting.toolTipText")); // NOI18N
        btnDeleteSighting.setName("btnDeleteSighting"); // NOI18N
        btnDeleteSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnDeleteSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 90, 30));

        btnEditSighting.setIcon(resourceMap.getIcon("btnEditSighting.icon")); // NOI18N
        btnEditSighting.setText(resourceMap.getString("btnEditSighting.text")); // NOI18N
        btnEditSighting.setToolTipText(resourceMap.getString("btnEditSighting.toolTipText")); // NOI18N
        btnEditSighting.setName("btnEditSighting"); // NOI18N
        btnEditSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnEditSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 90, 50));

        lblSightingsImage.setText(resourceMap.getString("lblSightingsImage.text")); // NOI18N
        lblSightingsImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSightingsImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblSightingsImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblSightingsImage.setName("lblSightingsImage"); // NOI18N
        lblSightingsImage.setPreferredSize(new java.awt.Dimension(150, 150));
        visitIncludes.add(lblSightingsImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 390, -1, -1));

        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.setMaximumSize(new java.awt.Dimension(300, 300));
        lblImage.setMinimumSize(new java.awt.Dimension(300, 300));
        lblImage.setName("lblImage"); // NOI18N
        lblImage.setPreferredSize(new java.awt.Dimension(300, 300));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });
        visitIncludes.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, -1, -1));

        jSeparator3.setName("jSeparator3"); // NOI18N
        visitIncludes.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 22, 690, 10));

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
        visitIncludes.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 190, 30, 170));

        btnMapSighting.setIcon(resourceMap.getIcon("btnMapSighting.icon")); // NOI18N
        btnMapSighting.setText(resourceMap.getString("btnMapSighting.text")); // NOI18N
        btnMapSighting.setToolTipText(resourceMap.getString("btnMapSighting.toolTipText")); // NOI18N
        btnMapSighting.setName("btnMapSighting"); // NOI18N
        btnMapSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMapSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, 110, 40));

        btnSetMainImage.setIcon(resourceMap.getIcon("btnSetMainImage.icon")); // NOI18N
        btnSetMainImage.setText(resourceMap.getString("btnSetMainImage.text")); // NOI18N
        btnSetMainImage.setToolTipText(resourceMap.getString("btnSetMainImage.toolTipText")); // NOI18N
        btnSetMainImage.setName("btnSetMainImage"); // NOI18N
        btnSetMainImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMainImageActionPerformed(evt);
            }
        });
        visitIncludes.add(btnSetMainImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 326, 100, -1));

        btnGoElement.setIcon(resourceMap.getIcon("btnGoElement.icon")); // NOI18N
        btnGoElement.setText(resourceMap.getString("btnGoElement.text")); // NOI18N
        btnGoElement.setToolTipText(resourceMap.getString("btnGoElement.toolTipText")); // NOI18N
        btnGoElement.setName("btnGoElement"); // NOI18N
        btnGoElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoElementActionPerformed(evt);
            }
        });
        visitIncludes.add(btnGoElement, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 550, 150, 30));

        btnMapVisit.setIcon(resourceMap.getIcon("btnMapVisit.icon")); // NOI18N
        btnMapVisit.setText(resourceMap.getString("btnMapVisit.text")); // NOI18N
        btnMapVisit.setToolTipText(resourceMap.getString("btnMapVisit.toolTipText")); // NOI18N
        btnMapVisit.setName("btnMapVisit"); // NOI18N
        btnMapVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapVisitActionPerformed(evt);
            }
        });
        visitIncludes.add(btnMapVisit, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 95, 110, 40));

        lblNumberOfSightings.setFont(resourceMap.getFont("lblNumberOfSightings.font")); // NOI18N
        lblNumberOfSightings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNumberOfSightings.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfSightings.border.lineColor"))); // NOI18N
        lblNumberOfSightings.setName("lblNumberOfSightings"); // NOI18N
        visitIncludes.add(lblNumberOfSightings, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 195, 30, 20));

        btnPreviousImageSighting.setIcon(resourceMap.getIcon("btnPreviousImageSighting.icon")); // NOI18N
        btnPreviousImageSighting.setToolTipText(resourceMap.getString("btnPreviousImageSighting.toolTipText")); // NOI18N
        btnPreviousImageSighting.setName("btnPreviousImageSighting"); // NOI18N
        btnPreviousImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousImageSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnPreviousImageSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 550, 40, 30));

        btnNextImageSighting.setIcon(resourceMap.getIcon("btnNextImageSighting.icon")); // NOI18N
        btnNextImageSighting.setToolTipText(resourceMap.getString("btnNextImageSighting.toolTipText")); // NOI18N
        btnNextImageSighting.setName("btnNextImageSighting"); // NOI18N
        btnNextImageSighting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextImageSightingActionPerformed(evt);
            }
        });
        visitIncludes.add(btnNextImageSighting, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 550, 40, 30));

        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setMaximumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setMinimumSize(new java.awt.Dimension(150, 150));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setPreferredSize(new java.awt.Dimension(150, 150));
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
        lblNumberOfElements.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("lblNumberOfElements.border.lineColor"))); // NOI18N
        lblNumberOfElements.setName("lblNumberOfElements"); // NOI18N
        visitIncludes.add(lblNumberOfElements, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 1, 30, 20));

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
        if (txtName.getText().length() > 0) {
            String oldName = visit.getName();
            visit.setName(txtName.getText());
            visit.setStartDate(dtpStartDate.getDate());
            visit.setEndDate(dtpEndDate.getDate());
            visit.setGameWatchingIntensity((GameWatchIntensity)cmbGameWatchIntensity.getSelectedItem());
            visit.setType((VisitType)cmbType.getSelectedItem());
            visit.setDescription(txtDescription.getText());

            boolean canSave = false;
            if (locationForVisit.getVisits() == null)
                locationForVisit.setVisits(new ArrayList<Visit>());
            Visit tempVisit = app.getDBI().find(new Visit(visit.getName()));
            if (tempVisit == null) {
                int index = locationForVisit.getVisits().indexOf(visit);
                if (index != -1) locationForVisit.getVisits().set(index, visit);
                else locationForVisit.getVisits().add(visit);
                canSave = true;
            }
            else {
                if (tempVisit.equals(visit) && app.getDBI().list(new Visit(visit.getName())).size() == 1) {
                    int index = locationForVisit.getVisits().indexOf(visit);
                    if (index != -1) locationForVisit.getVisits().set(index, visit);
                    else locationForVisit.getVisits().add(visit);
                    canSave = true;
                }
                else {
                    txtName.setBackground(Color.RED);
                    visit.setName(oldName);
                    txtName.setText(txtName.getText() + "_not_unique");
                }
            }

            // Save the visit
            if (canSave) {
                if (app.getDBI().createOrUpdate(locationForVisit) == true) {
                    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(PanelVisit.class);
                    txtName.setBackground(resourceMap.getColor("txtName.background"));
                }
                else {
                    txtName.setBackground(Color.RED);
                    visit.setName(oldName);
                    txtName.setText(txtName.getText() + "_location_not_unique");
                }
            }

            lblVisitName.setText(txtName.getText() + " - [" + locationForVisit.getName() + "]");

            setupTabHeader();
        }
        else {
            txtName.setBackground(Color.RED);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tblSightings.setModel(utilTableGenerator.getCompleteSightingTable(visit));
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 300));
//        lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 150));
        sighting = null;
        lblNumberOfSightings.setText(Integer.toString(visit.getSightings().size()));
        List<Element> allElements = new ArrayList<Element>();
        for (int i = 0; i < visit.getSightings().size(); i++) {
            if (!allElements.contains(visit.getSightings().get(i).getElement()))
                allElements.add(visit.getSightings().get(i).getElement());
        }
        lblNumberOfElements.setText(Integer.toString(allElements.size()));
    }//GEN-LAST:event_formComponentShown

    private void btnDeleteSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSightingActionPerformed
        if (tblSightings.getSelectedRow() >= 0) {
            sighting = app.getDBI().find(new Sighting((Date)tblSightings.getValueAt(tblSightings.getSelectedRow(), 2), app.getDBI().find(new Element((String)tblSightings.getValueAt(tblSightings.getSelectedRow(), 0))), locationForVisit, (Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 3)));
            visit.getSightings().remove(sighting);
            //app.getDBI().delete(sighting);
            app.getDBI().createOrUpdate(visit);
            tblSightings.setModel(utilTableGenerator.getCompleteSightingTable(visit));
            sighting = null;
            refreshSightingInfo();
        }
    }//GEN-LAST:event_btnDeleteSightingActionPerformed

    private void btnAddSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSightingActionPerformed
        sighting = new Sighting();
        sighting.setLocation(locationForVisit);
        tblSightings.clearSelection();
        refreshSightingInfo();
        final JDialog dialog = new JDialog(app.getMainFrame(), "Add a New Sighting", true);
        dialog.setLayout(new AbsoluteLayout());
        dialog.setSize(965, 625);
        dialog.add(new PanelSighting(sighting, visit, this), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        dialog.setLocationRelativeTo(this);
        ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
        dialog.setIconImage(icon.getImage());
        dialog.setVisible(true);
    }//GEN-LAST:event_btnAddSightingActionPerformed

    private void btnEditSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSightingActionPerformed
        if (sighting != null) {
            tblSightings.clearSelection();
            refreshSightingInfo();
            final JDialog dialog = new JDialog(app.getMainFrame(), "Edit an Existing Sighting", true);
            dialog.setLayout(new AbsoluteLayout());
            dialog.setSize(965, 625);
            dialog.add(new PanelSighting(sighting, visit, this), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
            dialog.setLocationRelativeTo(this);
            ImageIcon icon = new ImageIcon(app.getClass().getResource("resources/icons/Sighting.gif"));
            dialog.setIconImage(icon.getImage());
            dialog.setVisible(true);
        }
}//GEN-LAST:event_btnEditSightingActionPerformed

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
        int row = tblSightings.getSelectedRow();
        if (row >= 0) {
            Utils.uploadImage(sighting, "Sightings"+File.separatorChar+sighting.toString(), this);
            setupFotos(sighting.getFotos().size() - 1);
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
        else {
            final JDialog dialog = new JDialog(new JFrame(), "Upload Image", true);
            dialog.setLayout(new AbsoluteLayout());
            JLabel label = new JLabel("Please select a Sighting first.");
            label.setSize(200, 20);
            dialog.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 4, 50, -1));
            JButton button = new JButton("Close");
            button.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    dialog.dispose();
                }
            });
            dialog.setSize(210, 84);
            dialog.add(button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 23, 200, -1));
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
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

    private void tblSightingsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSightingsMouseReleased
        if (tblSightings.getSelectedRow() >= 0) {
            sighting = app.getDBI().find(new Sighting(new Date(Date.parse((String)tblSightings.getValueAt(tblSightings.getSelectedRow(), 2))), app.getDBI().find(new Element((String)tblSightings.getValueAt(tblSightings.getSelectedRow(), 0))), locationForVisit, (Long)tblSightings.getValueAt(tblSightings.getSelectedRow(), 3)));
            refreshSightingInfo();
        }
        else {
            sighting = null;
        }
}//GEN-LAST:event_tblSightingsMouseReleased

    private void btnNextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            if (imageIndex < sighting.getFotos().size() - 1) setupFotos(++imageIndex);
            else {
                imageIndex = 0;
                setupFotos(imageIndex);
            }
        }
}//GEN-LAST:event_btnNextImageActionPerformed

    private void btnMapSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapSightingActionPerformed
        app.getMapFrame().clearPoints();
        if (sighting != null) {
            if (sighting.getLatitude() != null && sighting.getLongitude() != null)
            if (!sighting.getLatitude().equals(Latitudes.NONE) && !sighting.getLongitude().equals(Longitudes.NONE)) {
                float lat = sighting.getLatDegrees();
                lat = lat + sighting.getLatMinutes()/60f;
                lat = lat + (sighting.getLatSeconds()/60f)/60f;
                if (sighting.getLatitude().equals(Latitudes.SOUTH))
                    lat = -1 * lat;
                float lon = sighting.getLonDegrees();
                lon = lon + sighting.getLonMinutes()/60f;
                lon = lon + (sighting.getLonSeconds()/60f)/60f;
                if (sighting.getLongitude().equals(Longitudes.WEST))
                    lon = -1 * lon;
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
            }
            app.getMapFrame().changeTitle("WildLog Map - Sighting: " + sighting.getElement().getPrimaryName() + " - " + sighting.getLocation().getName() + sighting.getDate().getDate() + "-" + (sighting.getDate().getMonth()+1) + "-" + (sighting.getDate().getYear()+1900));
        }
        else {
            app.getMapFrame().changeTitle("WildLog Map - Sighting: ...");
        }
        app.getMapFrame().showMap();
}//GEN-LAST:event_btnMapSightingActionPerformed

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
                    // everything went well - saving
                    btnUpdateActionPerformed(evt);
                }
            }
        }
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void btnSetMainImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMainImageActionPerformed
        if (sighting.getFotos() != null && sighting.getFotos().size() > 0) {
            sighting.getFotos().add(0, sighting.getFotos().get(imageIndex++));
            sighting.getFotos().remove(imageIndex);
            imageIndex = 0;
            // everything went well - saving
            btnUpdateActionPerformed(evt);
        }
}//GEN-LAST:event_btnSetMainImageActionPerformed

    private void btnGoElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoElementActionPerformed
        if (sighting != null) {
            PanelElement tempPanel = utilPanelGenerator.getElementPanel(sighting.getElement().getPrimaryName());
            parent = (JTabbedPane) getParent();
            parent.add(tempPanel);
            tempPanel.setupTabHeader();
            parent.setSelectedComponent(tempPanel);
        }
    }//GEN-LAST:event_btnGoElementActionPerformed

    private void btnMapVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapVisitActionPerformed
        app.getMapFrame().clearPoints();
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
                app.getMapFrame().addPoint(lat, lon, new Color(70, 120, 190));
            }
        }
        app.getMapFrame().changeTitle("WildLog Map - Visit: " + visit.getName());
        app.getMapFrame().showMap();
}//GEN-LAST:event_btnMapVisitActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        if (System.getProperty("os.name").equals("Windows XP")) {
            try {
                if (sighting != null)
                    if (sighting.getFotos() != null)
                        if (sighting.getFotos().size() > 0)
                            Runtime.getRuntime().exec("cmd /c start " + sighting.getFotos().get(imageIndex).getOriginalFotoLocation());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_lblImageMouseClicked

    private void btnPreviousImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousImageSightingActionPerformed
//        if (visit.getFotos() != null && visit.getFotos().size() > 0) {
//            if (imageSighingIndex > 0) setupFotosSightings(--imageSighingIndex);
//            else {
//                imageSighingIndex = visit.getFotos().size() - 1;
//                setupFotos(imageSighingIndex);
//            }
//        }
}//GEN-LAST:event_btnPreviousImageSightingActionPerformed

    private void btnNextImageSightingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextImageSightingActionPerformed
//        if (visit.getFotos() != null && visit.getFotos().size() > 0) {
//            if (imageSighingIndex < visit.getFotos().size() - 1) setupFotosSightings(++imageSighingIndex);
//            else {
//                imageSighingIndex = 0;
//                setupFotos(visit);
//            }
}//GEN-LAST:event_btnNextImageSightingActionPerformed

    private void setupFotos(int inIndex) {
        lblImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getFotos().get(inIndex).getFileLocation()), 300));
    }

    private void setupFotosSightings(int inIndex) {
//        lblSightingImage.setIcon(Utils.getScaledIcon(new ImageIcon(sighting.getFotos().get(inIndex).getFileLocation()), 150));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSighting;
    private javax.swing.JButton btnDeleteImage;
    private javax.swing.JButton btnDeleteSighting;
    private javax.swing.JButton btnEditSighting;
    private javax.swing.JButton btnGoElement;
    private javax.swing.JButton btnMapSighting;
    private javax.swing.JButton btnMapVisit;
    private javax.swing.JButton btnNextImage;
    private javax.swing.JButton btnNextImageSighting;
    private javax.swing.JButton btnPreviousImage;
    private javax.swing.JButton btnPreviousImageSighting;
    private javax.swing.JButton btnSetMainImage;
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
    private javax.swing.JLabel lblNumberOfSightings;
    private javax.swing.JLabel lblSightingsImage;
    private javax.swing.JLabel lblVisitName;
    private javax.swing.JTable tblSightings;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JPanel visitIncludes;
    // End of variables declaration//GEN-END:variables
    
}
