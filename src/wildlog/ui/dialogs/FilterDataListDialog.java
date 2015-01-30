package wildlog.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.CtrlClickSelectionModel;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.utils.UtilsUI;


public class FilterDataListDialog<T extends DataObjectWithWildLogFile> extends JDialog {
    private boolean selectionMade = false;
    private DataObjectWithWildLogFile typeInstance;
    private List<T> lstSelectedValues;

// TODO: Die class (en veral die constructors en weird generics) kan doen met review/cleanup...
    
    /**
     * Use this constructor when the input is derived from a list of Sightings. 
     * (For example used from the reports popup.)
     */
    public FilterDataListDialog(JFrame inParent, List<Sighting> inLstOriginalData, List<T> inLstOldSelectedData, Class<T> inClassType) {
        super(inParent);
        initComponents();
        // Do the initial shared setup
        doSharedSetup(inParent);
        // Load table content
        try {
            typeInstance = inClassType.newInstance();
        }
        catch (IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        if (typeInstance instanceof Element) {
            Set<String> setOriginalData = new HashSet<>();
            for (Sighting sighting : inLstOriginalData) {
                setOriginalData.add(sighting.getElementName());
            }
            List<Element> originalData = new ArrayList<>(setOriginalData.size());
            for (String temp : setOriginalData) {
                originalData.add(new Element(temp));
            }
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, originalData);
        }
        else
        if (typeInstance instanceof Location) {
            Set<String> setOriginalData = new HashSet<>();
            for (Sighting sighting : inLstOriginalData) {
                setOriginalData.add(sighting.getLocationName());
            }
            List<Location> originalData = new ArrayList<>(setOriginalData.size());
            for (String temp : setOriginalData) {
                originalData.add(new Location(temp));
            }
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, originalData);
        }
        else
        if (typeInstance instanceof Visit) {
            Set<String> setOriginalData = new HashSet<>();
            for (Sighting sighting : inLstOriginalData) {
                setOriginalData.add(sighting.getVisitName());
            }
            List<Visit> originalData = new ArrayList<>(setOriginalData.size());
            for (String temp : setOriginalData) {
                originalData.add(new Visit(temp));
            }
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, originalData);
        }
        else
        if (typeInstance instanceof Sighting) {
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, inLstOriginalData);
        }
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                lblTotalSelected.setText("Selected Records: " + table.getSelectedRowCount());
            }
        });
        // Wrap up the shared setup
        doSharedSetup_post(inLstOldSelectedData);
    }
    
    /**
     * Use this constructor when working with a known set of Locations, Visits or Elements. 
     * (For example from the All Sightings tab.)
     */
    public FilterDataListDialog(JFrame inParent, List<T> inLstOriginalData, List<T> inLstOldSelectedData) {
        super(inParent);
        // Do the initial shared setup
        doSharedSetup(inParent);
        // Load table content
        if (inLstOriginalData != null && !inLstOriginalData.isEmpty()) {
            typeInstance = inLstOriginalData.get(0);
        }
        else {
            typeInstance = null;
        }
        if (typeInstance instanceof Element) {
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, (List<Element>) inLstOriginalData);
        }
        else
        if (typeInstance instanceof Location) {
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, (List<Location>) inLstOriginalData);
        }
        else
        if (typeInstance instanceof Visit) {
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, (List<Visit>) inLstOriginalData);
        }
        else
        if (typeInstance instanceof Sighting) {
            UtilsTableGenerator.setupFilterTable(WildLogApp.getApplication(), table, inLstOriginalData);
        }
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                lblTotalSelected.setText("Selected Records: " + table.getSelectedRowCount());
            }
        });
        // Wrap up the shared setup
        doSharedSetup_post(inLstOldSelectedData);
    }

    private void doSharedSetup_post(List<T> inLstOldSelectedData) {
        // Setup previously selected values
        // Wag eers vir die table om klaar te load voor ek iets probeer select
        final int indexOfID;
        if (typeInstance instanceof Sighting) {
            indexOfID = 7;
            btnSelect.setEnabled(false);
            btnClearAll.setEnabled(false);
            btnSelectAll.setEnabled(false);
            table.setEnabled(false);
        }
        else {
            indexOfID = 2;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (inLstOldSelectedData != null) {
                    for (int t = 0; t < table.getModel().getRowCount(); t++) {
                        for (DataObjectWithWildLogFile dataObject : inLstOldSelectedData) {
                            if (table.getModel().getValueAt(table.convertRowIndexToModel(t), indexOfID).toString().equals(dataObject.getIDField())) {
                                table.getSelectionModel().addSelectionInterval(t, t);
                                break;
                            }
                        }
                    }
                }
                else {
                    for (int t = 0; t < table.getModel().getRowCount(); t++) {
                        table.getSelectionModel().addSelectionInterval(t, t);
                    }
                }
                lblTotalSelected.setText("Selected Records: " + table.getSelectedRowCount());
                lblTotalOriginal.setText("Total Records: " + table.getRowCount());
            }
        });
        // Add key listener for textfields to auto search the tables
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch, table, 2);
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtSearch);
    }
    
    private void doSharedSetup(JFrame inParent) {
        initComponents();
        // Setup the escape key
        final FilterDataListDialog<T> thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setSelectionMade(false);
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Position the dialog
        UtilsDialog.setDialogToCenter(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(inParent, thisHandler);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSearch = new javax.swing.JTextField();
        btnSelect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblTotalOriginal = new javax.swing.JLabel();
        lblTotalSelected = new javax.swing.JLabel();
        btnSelectAll = new javax.swing.JButton();
        btnClearAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Filter Data Selection");
        setMinimumSize(new java.awt.Dimension(550, 300));
        setModal(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Update.png"))); // NOI18N
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setFocusPainted(false);
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Select the data records that should be included:");

        table.setAutoCreateRowSorter(true);
        table.setBackground(new java.awt.Color(230, 226, 224));
        table.setForeground(new java.awt.Color(176, 153, 145));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setSelectionBackground(new java.awt.Color(193, 209, 179));
        table.setSelectionForeground(new java.awt.Color(23, 38, 4));
        table.setSelectionModel(new CtrlClickSelectionModel());
        jScrollPane2.setViewportView(table);

        lblTotalOriginal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalOriginal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTotalSelected.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalSelected.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSelectAll.setText("Select All");
        btnSelectAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelectAll.setFocusPainted(false);
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
            }
        });

        btnClearAll.setText("Clear All");
        btnClearAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearAll.setFocusPainted(false);
        btnClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtSearch)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(lblTotalOriginal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalSelected, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(btnSelectAll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSelectAll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearAll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadRowIDForType(int inRow) throws NumberFormatException {
        int indexOfID;
        if (typeInstance instanceof Sighting) {
            indexOfID = 7;
        }
        else {
            indexOfID = 2;
        }
        String selectedID = table.getModel().getValueAt(table.convertRowIndexToModel(inRow), indexOfID).toString();
        if (typeInstance instanceof Element) {
            ((Element) typeInstance).setPrimaryName(selectedID);
        }
        else
        if (typeInstance instanceof Location) {
            ((Location) typeInstance).setName(selectedID);
        }
        else
        if (typeInstance instanceof Visit) {
            ((Visit) typeInstance).setName(selectedID);
        }
        else
        if (typeInstance instanceof Sighting) {
            ((Sighting) typeInstance).setSightingCounter(Long.parseLong(selectedID));
        }
    }

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        selectionMade = true;
        lstSelectedValues = new ArrayList<>(table.getSelectedRowCount());
        int[] selectedRows = table.getSelectedRows();
        for (int row : selectedRows) {
            try {
                typeInstance = typeInstance.getClass().newInstance();
                loadRowIDForType(row);
                lstSelectedValues.add((T) typeInstance);
            }
            catch (IllegalAccessException | InstantiationException ex) {
                ex.printStackTrace(System.err);
            }
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        for (int t = 0; t < table.getRowCount(); t++) {
            table.getSelectionModel().addSelectionInterval(t, t);
        }
    }//GEN-LAST:event_btnSelectAllActionPerformed

    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllActionPerformed
        table.clearSelection();
    }//GEN-LAST:event_btnClearAllActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && table.getSelectedRowCount() == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    btnSelectActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        txtSearch.requestFocus();
    }//GEN-LAST:event_formComponentShown

    public boolean isSelectionMade() {
        return selectionMade;
    }
    
    public void setSelectionMade(boolean inSelectionMade) {
        selectionMade = inSelectionMade;
    }

    public List<T> getSelectedData() {
        return lstSelectedValues;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearAll;
    private javax.swing.JButton btnSelect;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotalOriginal;
    private javax.swing.JLabel lblTotalSelected;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
