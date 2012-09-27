package wildlog.ui.panel.bulkupload;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.VisitType;
import wildlog.ui.panel.bulkupload.data.BulkUploadDataLoader;
import wildlog.ui.panel.bulkupload.data.BulkUploadDataWrapper;
import wildlog.ui.panel.bulkupload.editors.ImageBoxEditor;
import wildlog.ui.panel.bulkupload.editors.InfoBoxEditor;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.ui.panel.bulkupload.renderers.ImageBoxRenderer;
import wildlog.ui.panel.bulkupload.renderers.InfoBoxRenderer;
import wildlog.ui.panel.interfaces.PanelCanSetupHeader;
import wildlog.utils.AstroUtils;
import wildlog.utils.LatLonConverter;
import wildlog.utils.ui.UtilPanelGenerator;
import wildlog.utils.ui.UtilTableGenerator;
import wildlog.utils.ui.Utils;


public class BulkUploadPanel extends PanelCanSetupHeader {
    public final static Color tableBackgroundColor1 = new Color(235, 246, 220);
    public final static Color tableBackgroundColor2 = new Color(215, 226, 200);


    /** Creates new form BulkUploadPanel */
    public BulkUploadPanel() {
        app = (WildLogApp) Application.getInstance();
        imageIndex = 0;
        // Init auto generated code
        initComponents();
        // Setup the Location list
        getLocationList();
        // Load the images
        loadImages();
        // "Hack" to make the buttons clickable when teh mouse scrolles over the cell (very performance intensive, but "better" now...)
        final JTable tableHandle = tblBulkImport;
        tblBulkImport.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tableHandle.rowAtPoint(e.getPoint());
                int col = tableHandle.columnAtPoint(e.getPoint());
                if (row != tableHandle.getEditingRow() || col != tableHandle.getEditingColumn()) {
                    tableHandle.editCellAt(row, col);
                }
            }
        });
    }

    @Override
    public void setupTabHeader() {
        JPanel tabHeader = new JPanel();
        tabHeader.add(new JLabel(new ImageIcon(app.getClass().getResource("resources/icons/LocationList.gif"))));
        tabHeader.add(new JLabel("Bulk Upload"));
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

    private void getLocationList() {
        List<Location> locations = app.getDBI().list(new Location());
        Collections.sort(locations);
        lstLocation.setListData(locations.toArray());
    }

    private void loadImages() {
        // Get the list of files from the folder to import from
        File rootFile = showFileChooser();

        // Setup the datamodel
        DefaultTableModel model = ((DefaultTableModel)tblBulkImport.getModel());
        model.getDataVector().clear();
        BulkUploadDataWrapper wrapper = BulkUploadDataLoader.genenrateTableData(rootFile, chkIncludeSubfolders.isSelected(), (Integer)spnInactivityTime.getValue());
        model.getDataVector().addAll(UtilTableGenerator.convertToVector(wrapper.getData()));
        model.fireTableDataChanged();

        // Setup the dates
        dtpStartDate.setDate(wrapper.getStartDate());
        dtpEndDate.setDate(wrapper.getEndDate());
    }

    private File showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a folder to import");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        // TODO: Maak dat mens die image files kan sien, maar net folders kan kies...
//        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.showOpenDialog(this.getParent());
        return fileChooser.getSelectedFile();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dtpStartDate = new org.jdesktop.swingx.JXDatePicker();
        dtpEndDate = new org.jdesktop.swingx.JXDatePicker();
        lblLocationImage = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        txtVisitName = new javax.swing.JTextField();
        chkShowInactiveTimes = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        spnInactivityTime = new javax.swing.JSpinner();
        btnReload = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        txtLocationName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstLocation = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        chkIncludeSubfolders = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        cmbVisitType = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBulkImport = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(1005, 585));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(wildlog.WildLogApp.class).getContext().getResourceMap(BulkUploadPanel.class);
        pnlTop.setBackground(resourceMap.getColor("pnlTop.background")); // NOI18N
        pnlTop.setMaximumSize(new java.awt.Dimension(1005, 130));
        pnlTop.setMinimumSize(new java.awt.Dimension(1005, 130));
        pnlTop.setName("pnlTop"); // NOI18N
        pnlTop.setPreferredSize(new java.awt.Dimension(1005, 130));
        pnlTop.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        pnlTop.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        pnlTop.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, -1, 20));

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        pnlTop.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, 20));

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        pnlTop.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, -1, 20));

        dtpStartDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpStartDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpStartDate.setName("dtpStartDate"); // NOI18N
        pnlTop.add(dtpStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 40, 140, -1));

        dtpEndDate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dtpEndDate.setFormats(new SimpleDateFormat("dd MMM yyyy"));
        dtpEndDate.setName("dtpEndDate"); // NOI18N
        pnlTop.add(dtpEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 40, 140, -1));

        lblLocationImage.setBackground(resourceMap.getColor("lblLocationImage.background")); // NOI18N
        lblLocationImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocationImage.setText(resourceMap.getString("lblLocationImage.text")); // NOI18N
        lblLocationImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocationImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLocationImage.setName("lblLocationImage"); // NOI18N
        lblLocationImage.setOpaque(true);
        lblLocationImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLocationImageMouseReleased(evt);
            }
        });
        pnlTop.add(lblLocationImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 35, 90, 90));

        btnUpdate.setBackground(resourceMap.getColor("btnUpdate.background")); // NOI18N
        btnUpdate.setIcon(resourceMap.getIcon("btnUpdate.icon")); // NOI18N
        btnUpdate.setToolTipText(resourceMap.getString("btnUpdate.toolTipText")); // NOI18N
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.setName("btnUpdate"); // NOI18N
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnlTop.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, 130, 80));

        txtVisitName.setBackground(resourceMap.getColor("txtVisitName.background")); // NOI18N
        txtVisitName.setText("Bulk Import - " + new SimpleDateFormat("dd MMM yyyy (HH'h'mm)").format(Calendar.getInstance().getTime()));
        txtVisitName.setName("txtVisitName"); // NOI18N
        pnlTop.add(txtVisitName, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 200, -1));

        chkShowInactiveTimes.setBackground(resourceMap.getColor("chkShowInactiveTimes.background")); // NOI18N
        chkShowInactiveTimes.setText(resourceMap.getString("chkShowInactiveTimes.text")); // NOI18N
        chkShowInactiveTimes.setName("chkShowInactiveTimes"); // NOI18N
        pnlTop.add(chkShowInactiveTimes, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 75, -1, -1));

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        pnlTop.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 100, -1, 20));

        spnInactivityTime.setModel(new javax.swing.SpinnerNumberModel(120, 1, 10000000, 1));
        spnInactivityTime.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spnInactivityTime.setName("spnInactivityTime"); // NOI18N
        pnlTop.add(spnInactivityTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 100, 60, -1));

        btnReload.setBackground(resourceMap.getColor("btnReload.background")); // NOI18N
        btnReload.setIcon(resourceMap.getIcon("btnReload.icon")); // NOI18N
        btnReload.setText(resourceMap.getString("btnReload.text")); // NOI18N
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.setName("btnReload"); // NOI18N
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });
        pnlTop.add(btnReload, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 95, 130, 30));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        pnlTop.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 100, -1, 20));

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setName("jLabel8"); // NOI18N
        pnlTop.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 80, 70, 40));

        jSeparator1.setName("jSeparator1"); // NOI18N
        pnlTop.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(422, 70, 440, 10));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N
        pnlTop.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 20, 120));

        jButton2.setBackground(resourceMap.getColor("jButton2.background")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setName("jButton2"); // NOI18N
        pnlTop.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 100, 30));

        txtLocationName.setBackground(resourceMap.getColor("txtLocationName.background")); // NOI18N
        txtLocationName.setText(resourceMap.getString("txtLocationName.text")); // NOI18N
        txtLocationName.setName("txtLocationName"); // NOI18N
        txtLocationName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLocationNameKeyReleased(evt);
            }
        });
        pnlTop.add(txtLocationName, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 310, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        lstLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstLocation.setName("lstLocation"); // NOI18N
        lstLocation.setSelectionBackground(resourceMap.getColor("lstLocation.selectionBackground")); // NOI18N
        lstLocation.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstLocationValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstLocation);

        pnlTop.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 190, 80));

        jButton3.setBackground(resourceMap.getColor("jButton3.background")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setName("jButton3"); // NOI18N
        pnlTop.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 100, 30));

        chkIncludeSubfolders.setBackground(resourceMap.getColor("chkIncludeSubfolders.background")); // NOI18N
        chkIncludeSubfolders.setSelected(true);
        chkIncludeSubfolders.setText(resourceMap.getString("chkIncludeSubfolders.text")); // NOI18N
        chkIncludeSubfolders.setName("chkIncludeSubfolders"); // NOI18N
        pnlTop.add(chkIncludeSubfolders, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 75, -1, -1));

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        pnlTop.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 10, 40, 20));

        cmbVisitType.setBackground(resourceMap.getColor("cmbVisitType.background")); // NOI18N
        cmbVisitType.setModel(new DefaultComboBoxModel(VisitType.values()));
        cmbVisitType.setSelectedItem(VisitType.OTHER);
        cmbVisitType.setName("cmbVisitType"); // NOI18N
        pnlTop.add(cmbVisitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 10, 120, -1));

        add(pnlTop, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblBulkImport.setBackground(resourceMap.getColor("tblBulkImport.background")); // NOI18N
        tblBulkImport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Sightings", "Images"
            }
        ));
        tblBulkImport.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblBulkImport.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblBulkImport.setFillsViewportHeight(true);
        tblBulkImport.setFocusable(false);
        tblBulkImport.setGridColor(resourceMap.getColor("tblBulkImport.gridColor")); // NOI18N
        tblBulkImport.setName("tblBulkImport"); // NOI18N
        tblBulkImport.setRowHeight(250);
        tblBulkImport.setSelectionBackground(resourceMap.getColor("tblBulkImport.selectionBackground")); // NOI18N
        tblBulkImport.setSelectionForeground(resourceMap.getColor("tblBulkImport.selectionForeground")); // NOI18N
        tblBulkImport.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBulkImport.getTableHeader().setResizingAllowed(false);
        tblBulkImport.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblBulkImport);
        tblBulkImport.getColumnModel().getColumn(0).setMinWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setPreferredWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setMaxWidth(240);
        tblBulkImport.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblBulkImport.columnModel.title0")); // NOI18N
        tblBulkImport.getColumnModel().getColumn(0).setCellEditor(new InfoBoxEditor(app, txtLocationName, txtVisitName));
        tblBulkImport.getColumnModel().getColumn(0).setCellRenderer(new InfoBoxRenderer(app, txtLocationName, txtVisitName));
        tblBulkImport.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblBulkImport.columnModel.title1")); // NOI18N
        tblBulkImport.getColumnModel().getColumn(1).setCellEditor(new ImageBoxEditor());
        tblBulkImport.getColumnModel().getColumn(1).setCellRenderer(new ImageBoxRenderer());

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lstLocationValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstLocationValueChanged
        if (!lstLocation.getSelectionModel().isSelectionEmpty()) {
            String selectedName = lstLocation.getSelectedValue().toString();
            // Change the location name
            txtLocationName.setText(selectedName);
            // Cahnge the image
            List<WildLogFile> fotos = app.getDBI().list(new WildLogFile("LOCATION-" + selectedName));
            if (fotos.size() > 0) {
                Utils.setupFoto("LOCATION-" + selectedName, imageIndex, lblLocationImage, 100, app);
            }
            else {
                lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
            }
        }
        else {
            lblLocationImage.setIcon(Utils.getScaledIcon(new ImageIcon(app.getClass().getResource("resources/images/NoImage.gif")), 100));
        }
    }//GEN-LAST:event_lstLocationValueChanged

    private void txtLocationNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocationNameKeyReleased
        for (int t = 0; t < lstLocation.getModel().getSize(); t++) {
            if (lstLocation.getModel().getElementAt(t).toString().equalsIgnoreCase(txtLocationName.getText())) {
                lstLocation.setSelectedIndex(t);
                break;
            }
            else
                lstLocation.getSelectionModel().clearSelection();
        }
    }//GEN-LAST:event_txtLocationNameKeyReleased

    private void lblLocationImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLocationImageMouseReleased
        if (!lstLocation.getSelectionModel().isSelectionEmpty()) {
            Utils.openFile("LOCATION-" + lstLocation.getSelectedValue().toString(), imageIndex, app);
        }
    }//GEN-LAST:event_lblLocationImageMouseReleased

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        loadImages();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // FIXME: Clean/refactor up this (and related sighting saving) code to be "robust" and re-useable...
        // TODO: Close the tab sooner (once validation is fine) and then use the progressbar to keep track of the saving
        if (txtLocationName.getText() != null && !txtLocationName.getText().isEmpty()
                && txtVisitName.getText() != null && !txtVisitName.getText().isEmpty()) {
            DefaultTableModel model = (DefaultTableModel)tblBulkImport.getModel();
            // Make sure all sightings have a creature set
            for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(0, 0);
                if (sightingWrapper.getElementName() == null || sightingWrapper.getElementName().isEmpty()) {
                    JOptionPane.showMessageDialog(this.getParent(), "Please select a Creature for each of the Sightings.", "Can't Save", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Process the Location
            Location location = app.getDBI().find(new Location(txtLocationName.getText()));
            if (location == null) {
                location = new Location(txtLocationName.getText());
                app.getDBI().createOrUpdate(location, null);
            }
            // Process the Visit
            Visit visit = new Visit(txtVisitName.getText());
            if (app.getDBI().find(visit) == null) {
                visit.setLocationName(location.getName());
                visit.setStartDate(dtpStartDate.getDate());
                visit.setEndDate(dtpEndDate.getDate());
                visit.setType((VisitType)cmbVisitType.getSelectedItem());
                app.getDBI().createOrUpdate(visit, null);
                // Processs the sightings
                for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
                    BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
                    // Check wether the Creature exists or not
                    if (app.getDBI().find(new Element(sightingWrapper.getElementName())) == null) {
                        app.getDBI().createOrUpdate(new Element(sightingWrapper.getElementName()), null);
                    }
                    // Continue processing the Sighting
                    sightingWrapper.setLocationName(location.getName());
                    sightingWrapper.setVisitName(visit.getName());
                    // If the sighting's GPS point is set then try to calculate Sun and Moon
                    if (sightingWrapper.getDate() != null
                            && sightingWrapper.getLatitude() != null && !sightingWrapper.getLatitude().equals(Latitudes.NONE)
                            && sightingWrapper.getLongitude() != null && !sightingWrapper.getLongitude().equals(Longitudes.NONE)) {
                        // Sun
                        double latitude = LatLonConverter.getDecimalDegree(sightingWrapper.getLatitude(), sightingWrapper.getLatDegrees(), sightingWrapper.getLatMinutes(), sightingWrapper.getLatSecondsFloat());
                        double longitude = LatLonConverter.getDecimalDegree(sightingWrapper.getLongitude(), sightingWrapper.getLonDegrees(), sightingWrapper.getLonMinutes(), sightingWrapper.getLonSecondsFloat());
                        sightingWrapper.setTimeOfDay(AstroUtils.getSunCategory(sightingWrapper.getDate(), latitude, longitude));
                        // Moon
                        sightingWrapper.setMoonPhase(AstroUtils.getMoonPhase(sightingWrapper.getDate()));
                        sightingWrapper.setMoonlight(AstroUtils.getMoonlight(sightingWrapper.getDate(), latitude, longitude));
                    }
                    // Save the sigting
                    app.getDBI().createOrUpdate(sightingWrapper);
                    // Save the corresponding images
                    BulkUploadImageListWrapper listWrapper = (BulkUploadImageListWrapper)model.getValueAt(rowCount, 1);
                    List<File> files = new ArrayList<File>(listWrapper.getImageList().size());
                    for (BulkUploadImageFileWrapper imageWrapper : listWrapper.getImageList()) {
                        files.add(imageWrapper.getFile());
                    }
                    Utils.performFileUpload(
                                "SIGHTING-" + sightingWrapper.getSightingCounter(),
                                "Sightings" + File.separatorChar + sightingWrapper.toString(),
                                files.toArray(new File[files.size()]),
                                null, 300, app);
                }
                // Done processing
                // Open the Visit and close the bulk upload tabs
                UtilPanelGenerator.addPanelAsTab(
                        UtilPanelGenerator.getVisitPanel(location, visit.getName()),
                        ((JTabbedPane)getParent()));
                closeTab();
            }
            else {
                JOptionPane.showMessageDialog(this.getParent(), "The Visit name is not unique, please specify another one.", "Can't Save", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this.getParent(), "Please provide a Location name and Visit name before saving.", "Can't Save", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Reload the locations as they might have changed
        getLocationList();
        txtLocationNameKeyReleased(null);
        // Re-check all species images already assigned since some might have changed
        DefaultTableModel model = ((DefaultTableModel)tblBulkImport.getModel());
        for (int rowCount = 0; rowCount < model.getRowCount(); rowCount++) {
            BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)model.getValueAt(rowCount, 0);
            if (sightingWrapper.getElementName() != null && !sightingWrapper.getElementName().isEmpty()) {
                List<Element> elements = app.getDBI().list(new Element());
                boolean foundElement = false;
                for (Element element : elements) {
                    if (sightingWrapper.getElementName().equalsIgnoreCase(element.getPrimaryName())) {
                        JLabel tempLabel = new JLabel();
                        Utils.setupFoto("ELEMENT-" + element.getPrimaryName(), 0, tempLabel, 150, app);
                        sightingWrapper.setIcon(tempLabel.getIcon());
                        foundElement = true;
                        break;
                    }
                }
                if (!foundElement) {
                    sightingWrapper.setIcon(Utils.getScaledIconForNoImage(150));
                }
            }
        }
    }//GEN-LAST:event_formComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chkIncludeSubfolders;
    private javax.swing.JCheckBox chkShowInactiveTimes;
    private javax.swing.JComboBox cmbVisitType;
    private org.jdesktop.swingx.JXDatePicker dtpEndDate;
    private org.jdesktop.swingx.JXDatePicker dtpStartDate;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblLocationImage;
    private javax.swing.JList lstLocation;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JSpinner spnInactivityTime;
    private javax.swing.JTable tblBulkImport;
    private javax.swing.JTextField txtLocationName;
    private javax.swing.JTextField txtVisitName;
    // End of variables declaration//GEN-END:variables
}
