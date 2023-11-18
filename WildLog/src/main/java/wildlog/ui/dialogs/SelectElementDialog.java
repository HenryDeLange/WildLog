package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.logging.log4j.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.ScrollableWrappedFlowLayout;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.panels.PanelElement;
import wildlog.ui.panels.helpers.SightingBox;
import wildlog.ui.panels.interfaces.PanelNeedsRefreshWhenDataChanges;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;


public class SelectElementDialog extends JDialog implements PanelNeedsRefreshWhenDataChanges {
    private static int GRID_LIMIT = 30;
    private static long previousElementID = 0;
    private static String previousElementName;
    private final WildLogApp app;
    private boolean selectionMade = false;
    private long selectedElementID;
    private String selectedElementName;
    private Dimension originalSize = null;
    private long activeLocationID;


    public SelectElementDialog(JFrame inParent, WildLogApp inApp, final long inSelectedElementID, final long inActiveLocationID) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[SelectElementDialog]");
        app = inApp;
        activeLocationID = inActiveLocationID;
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbElementType);
        // Setup the escape key
        final SelectElementDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Attach listeners etc.
        UtilsUI.attachClipboardPopup(txtSearch);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElement);
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, tblElement);
        // Load the UI
        setupUI(inSelectedElementID);
        // Position the dialog
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }

    private void setupUI(final long inSelectedElementID) {
        // Setup the table
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, null);
        // Load selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int t = 0; t < tblElement.getRowCount(); t++) {
                    if ((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(t), 3) == inSelectedElementID) {
                        tblElement.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblElement.getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblElement.scrollRectToVisible(tblElement.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
            }
        });
        UtilsImageProcessing.setupFoto(inSelectedElementID, 0, lblElementImage, WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL, app);
        if (inSelectedElementID > 0) {
            Element element = app.getDBI().findElement(inSelectedElementID, null, false, Element.class);
            if (element != null) {
                txtIdentification.setText(element.getDiagnosticDescription());
                txtIdentification.setCaretPosition(0);
            }
        }
        // Hide the info panel
        pnlInfo.setVisible(false);
        pack();
        if (originalSize == null) {
            originalSize = new Dimension(getPreferredSize().width - pnlInfo.getPreferredSize().width, getPreferredSize().height);
        }
        setMinimumSize(originalSize);
        setPreferredSize(originalSize);
        setSize(originalSize);
        // Refresh the grid
        generateInfoPanel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlWrapper = new javax.swing.JPanel();
        pnlMain = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblElementImage = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        cmbElementType = new javax.swing.JComboBox();
        btnPreviousElement = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblElement = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnToggleInfo = new javax.swing.JButton();
        pnlInfo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        txtIdentification = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        pnlGridView = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblCount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select a Creature");
        setBackground(new java.awt.Color(230, 237, 220));
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif")).getImage());
        setModal(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlWrapper.setBackground(new java.awt.Color(230, 237, 220));
        pnlWrapper.setMinimumSize(new java.awt.Dimension(500, 500));
        pnlWrapper.setName("pnlWrapper"); // NOI18N
        pnlWrapper.setPreferredSize(new java.awt.Dimension(945, 600));

        pnlMain.setBackground(new java.awt.Color(230, 237, 220));
        pnlMain.setMinimumSize(new java.awt.Dimension(556, 460));
        pnlMain.setName("pnlMain"); // NOI18N
        pnlMain.setPreferredSize(new java.awt.Dimension(556, 460));

        txtSearch.setName("txtSearch"); // NOI18N
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Choose a Creature:");
        jLabel2.setName("jLabel2"); // NOI18N

        lblElementImage.setBackground(new java.awt.Color(0, 0, 0));
        lblElementImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElementImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblElementImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblElementImage.setName("lblElementImage"); // NOI18N
        lblElementImage.setOpaque(true);
        lblElementImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblElementImageMouseReleased(evt);
            }
        });

        btnSelect.setBackground(new java.awt.Color(230, 237, 220));
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSelect.setToolTipText("Confirm the selected Creature.");
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        cmbElementType.setMaximumRowCount(11);
        cmbElementType.setModel(new DefaultComboBoxModel(ElementType.values()));
        cmbElementType.setSelectedItem(ElementType.NONE);
        cmbElementType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbElementType.setName("cmbElementType"); // NOI18N
        cmbElementType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbElementTypeActionPerformed(evt);
            }
        });

        btnPreviousElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Element.gif"))); // NOI18N
        btnPreviousElement.setText("Previous Creature");
        btnPreviousElement.setToolTipText("This will set the Creature to the previously selected Creature.");
        btnPreviousElement.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreviousElement.setName("btnPreviousElement"); // NOI18N
        btnPreviousElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousElementActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblElement.setAutoCreateRowSorter(true);
        tblElement.setName("tblElement"); // NOI18N
        tblElement.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElement.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElementMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElementMouseReleased(evt);
            }
        });
        tblElement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblElementKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElementKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblElement);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Add_Small.gif"))); // NOI18N
        btnAdd.setText("New Creature");
        btnAdd.setToolTipText("Create a new Creature.");
        btnAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnToggleInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnToggleInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnToggleInfo.setText("View Info");
        btnToggleInfo.setToolTipText("Show more information about the selected Creature.");
        btnToggleInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnToggleInfo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnToggleInfo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnToggleInfo.setName("btnToggleInfo"); // NOI18N
        btnToggleInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                        .addComponent(txtSearch)
                        .addGap(0, 0, 0)
                        .addComponent(cmbElementType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnToggleInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(btnPreviousElement, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(lblElementImage, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(btnSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbElementType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlMainLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnPreviousElement, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblElementImage, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnToggleInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );

        pnlInfo.setBackground(new java.awt.Color(230, 237, 220));
        pnlInfo.setName("pnlInfo"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Identification:");
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane21.setName("jScrollPane21"); // NOI18N

        txtIdentification.setEditable(false);
        txtIdentification.setColumns(20);
        txtIdentification.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtIdentification.setLineWrap(true);
        txtIdentification.setRows(5);
        txtIdentification.setWrapStyleWord(true);
        txtIdentification.setName("txtIdentification"); // NOI18N
        jScrollPane21.setViewportView(txtIdentification);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Files:");
        jLabel3.setName("jLabel3"); // NOI18N

        pnlGridView.setBackground(new java.awt.Color(0, 0, 0));
        pnlGridView.setName("pnlGridView"); // NOI18N
        pnlGridView.setLayout(new java.awt.BorderLayout());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Count:");
        jLabel4.setName("jLabel4"); // NOI18N

        lblCount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCount.setText("Loading...");
        lblCount.setName("lblCount"); // NOI18N

        javax.swing.GroupLayout pnlInfoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(pnlInfoLayout);
        pnlInfoLayout.setHorizontalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGridView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .addGroup(pnlInfoLayout.createSequentialGroup()
                        .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlInfoLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCount))
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlInfoLayout.setVerticalGroup(
            pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInfoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(pnlInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(2, 2, 2)
                .addComponent(pnlGridView, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout pnlWrapperLayout = new javax.swing.GroupLayout(pnlWrapper);
        pnlWrapper.setLayout(pnlWrapperLayout);
        pnlWrapperLayout.setHorizontalGroup(
            pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWrapperLayout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        pnlWrapperLayout.setVerticalGroup(
            pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pnlWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public long getSelectedElementID() {
        return selectedElementID;
    }

    public String getSelectedElementName() {
        return selectedElementName;
    }

    public Icon getSelectedElementIcon() {
        return lblElementImage.getIcon();
    }

    private void lblElementImageMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblElementImageMouseReleased
        if (!tblElement.getSelectionModel().isSelectionEmpty()) {
            UtilsFileProcessing.openFile((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3), 0, app);
        }
    }//GEN-LAST:event_lblElementImageMouseReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        if (tblElement.getSelectedRowCount() == 1) {
            selectionMade = true;
            selectedElementID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
            previousElementID = selectedElementID;
            selectedElementName = (String) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 1);
            previousElementName = selectedElementName;
            tblElement.setBorder(null);
            setVisible(false);
            dispose();
        }
        else {
            tblElement.setBorder(new LineBorder(Color.RED, 2));
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnPreviousElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousElementActionPerformed
        // Reload the entire table to make sure the element is there
        UtilsTableGenerator.setupElementTableSmall(app, tblElement, null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Select the previous element
                for (int t = 0; t < tblElement.getModel().getRowCount(); t++) {
                    if ((long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(t), 3) == previousElementID) {
                        tblElement.getSelectionModel().setSelectionInterval(t, t);
                        int scrollRow = t;
                        if (t < (tblElement.getModel().getRowCount()) - 1) {
                            scrollRow = t + 1;
                        }
                        tblElement.scrollRectToVisible(tblElement.getCellRect(scrollRow, 0, true));
                        break;
                    }
                }
                // Update the icon
                UtilsImageProcessing.setupFoto(previousElementID, 0, lblElementImage, WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL, app);
                Element element = app.getDBI().findElement(previousElementID, null, false, Element.class);
                if (element != null) {
                    txtIdentification.setText(element.getDiagnosticDescription());
                    txtIdentification.setCaretPosition(0);
                }
                // Do the select action
                btnSelectActionPerformed(null);
            }
        });
    }//GEN-LAST:event_btnPreviousElementActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtSearch.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void tblElementMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseReleased
        if (!tblElement.getSelectionModel().isSelectionEmpty()) {
            long selectedID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
            // Change the image
            UtilsImageProcessing.setupFoto(selectedID, 0, lblElementImage, WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL, app);
            Element element = app.getDBI().findElement(selectedID, null, false, Element.class);
            if (element != null) {
                txtIdentification.setText(element.getDiagnosticDescription());
                txtIdentification.setCaretPosition(0);
            }
        }
        else {
            lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL));
            txtIdentification.setText("");
        }
        // Refresh the grid
        generateInfoPanel();
    }//GEN-LAST:event_tblElementMouseReleased

    private void tblElementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElementMouseClicked
        if (evt.getClickCount() == 2) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementMouseClicked

    private void tblElementKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSelectActionPerformed(null);
        }
    }//GEN-LAST:event_tblElementKeyPressed

    private void cmbElementTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbElementTypeActionPerformed
        ElementType type = (ElementType) cmbElementType.getSelectedItem();
        if (!ElementType.NONE.equals(type)) {
            UtilsTableGenerator.setupElementTableSmall(app, tblElement, type);
        }
        else {
            UtilsTableGenerator.setupElementTableSmall(app, tblElement, null);
        }
        txtSearch.setText("");
        // Clear Images
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL));
        txtIdentification.setText("");
        // Refresh the grid
        generateInfoPanel();
    }//GEN-LAST:event_cmbElementTypeActionPerformed

    private void tblElementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElementKeyReleased
        tblElementMouseReleased(null);
    }//GEN-LAST:event_tblElementKeyReleased

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        tblElementMouseReleased(null);
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnSelectActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        JDialog popup = new JDialog(this, "Add New Creature", true);
        ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif"));
        popup.setIconImage(icon.getImage());
        PanelElement panel = new PanelElement(app, new Element(), true, this);
        popup.add(panel);
        popup.setResizable(false);
        popup.pack();
        UtilsDialog.setDialogToCenter(this, popup);
        UtilsDialog.addModalBackgroundPanel(this, popup);
        UtilsDialog.addModalBackgroundPanel(popup, null);
        UtilsDialog.addEscapeKeyListener(popup);
        popup.setVisible(true);
        popup.dispose();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnToggleInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleInfoActionPerformed
        pnlInfo.setVisible(!pnlInfo.isVisible());
        if (pnlInfo.isVisible()) {
            setPreferredSize(new Dimension(935, (int) getPreferredSize().getHeight()));
            generateInfoPanel();
        }
        else {
            pnlGridView.removeAll();
            pnlGridView.revalidate();
            pnlGridView.repaint();
            setPreferredSize(originalSize);
        }
        pack();
    }//GEN-LAST:event_btnToggleInfoActionPerformed

    @Override
    public void doTheRefresh(Object inIndicator) {
        txtSearch.setText("");
        cmbElementType.setSelectedItem(ElementType.NONE);
        lblElementImage.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0125_MEDIUM_VERY_SMALL));
        txtIdentification.setText("");
        // Refresh the grid
        generateInfoPanel();
        pack();
        // Refresh the UI
        setupUI(selectedElementID);
    }
    
    public boolean isSelectionMade() {
        return selectionMade;
    }

    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    public static long getPreviousElementID() {
        return previousElementID;
    }

    public static void setPreviousElementID(long inPreviousElementID) {
        previousElementID = inPreviousElementID;
    }

    public static String getPreviousElementName() {
        return previousElementName;
    }

    public static void setPreviousElementName(String inPreviousElementName) {
        previousElementName = inPreviousElementName;
    }
    
    private void generateInfoPanel() {
        if (pnlInfo.isVisible()) {
            // Get new counts
            lblCount.setText("");
            if (!tblElement.getSelectionModel().isSelectionEmpty()) {
                long selectedID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
                lblCount.setText(app.getDBI().countSightings(0L, selectedID, activeLocationID, 0L) + " Observations at " 
                        + app.getDBI().findLocation(activeLocationID, null, false, Location.class));
            }
            // Draw new grid
            pnlGridView.removeAll();
            pnlGridView.revalidate();
            pnlGridView.repaint();
            final JPanel pnlGrid = new JPanel();
            pnlGrid.setLayout(new ScrollableWrappedFlowLayout(FlowLayout.CENTER));
            JLabel lblTemp = new JLabel("");
            lblTemp.setForeground(Color.WHITE);
            lblTemp.setFont(lblTemp.getFont().deriveFont(18f));
            pnlGrid.add(lblTemp);
            pnlGrid.setBackground(Color.BLACK);
            JScrollPane scrGrid = new JScrollPane(pnlGrid, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrGrid.getVerticalScrollBar().setUnitIncrement(35);
            pnlGridView.add(scrGrid, BorderLayout.CENTER);
            pnlGridView.revalidate();
            pnlGridView.repaint();
            if (!tblElement.getSelectionModel().isSelectionEmpty()) {
                lblTemp.setText("Loading...");
                long selectedID = (long) tblElement.getModel().getValueAt(tblElement.convertRowIndexToModel(tblElement.getSelectedRow()), 3);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        List<Sighting> lstSightings = app.getDBI().listSightings(selectedID, 0, 0, false, Sighting.class);
                        Collections.sort(lstSightings, new Comparator<>() {
                            @Override
                            public int compare(Sighting inSighting1, Sighting inSighting2) {
                                if (inSighting1.getViewRating() == null || inSighting1.getViewRating() == ViewRating.NONE) {
                                    inSighting1.setViewRating(ViewRating.NORMAL);
                                }
                                if (inSighting2.getViewRating() == null || inSighting2.getViewRating() == ViewRating.NONE) {
                                    inSighting2.setViewRating(ViewRating.NORMAL);
                                }
                                int result = inSighting1.getViewRating().compareTo(inSighting2.getViewRating());
                                if (result == 0) {
                                    result = -1 * inSighting1.compareTo(inSighting2);
                                }
                                return result;
                            }
                        });
                        pnlGrid.removeAll();
                        if (!lstSightings.isEmpty()) {
                            ((ScrollableWrappedFlowLayout) pnlGrid.getLayout()).setAlignment(FlowLayout.LEFT);
                            int boxCount = 0;
                            breakLoop: for (Sighting sighting : lstSightings) {
                                List<WildLogFile> lstSightingFiles = app.getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
                                if (lstSightingFiles != null && !lstSightingFiles.isEmpty()) {
                                    for (int t = 0; t < lstSightingFiles.size(); t++) {
                                        generateGridBox(pnlGrid, sighting, t);
                                        boxCount++;
                                        if (boxCount >= GRID_LIMIT) {
                                            break breakLoop;
                                        }
                                    }
                                }
                            }
                        }
                        pnlGrid.revalidate();
                        pnlGrid.repaint();
                        pnlGridView.revalidate();
                        pnlGridView.repaint();
                    }
                });
            }
        }
    }
    
    private void generateGridBox(JPanel inPnlGrid, Sighting inSighting, int inFileIndex) {
        SightingBox sightingBox = new SightingBox(inSighting, inFileIndex, false);
        sightingBox.setBoxSize(WildLogThumbnailSizes.S0150_MEDIUM_SMALL);
        sightingBox.setSelectable(false);
        inPnlGrid.add(sightingBox);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnPreviousElement;
    private javax.swing.JButton btnSelect;
    private javax.swing.JButton btnToggleInfo;
    private javax.swing.JComboBox cmbElementType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblElementImage;
    private javax.swing.JPanel pnlGridView;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlWrapper;
    private javax.swing.JTable tblElement;
    private javax.swing.JTextArea txtIdentification;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
