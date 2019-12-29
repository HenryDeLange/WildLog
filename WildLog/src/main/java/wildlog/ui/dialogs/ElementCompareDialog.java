package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ComboBoxFixer;
import wildlog.ui.helpers.ScrollableWrappedFlowLayout;
import wildlog.ui.helpers.UtilsTableGenerator;
import wildlog.ui.panels.helpers.SightingBox;
import wildlog.ui.utils.UtilsUI;


public class ElementCompareDialog extends JFrame {


    public ElementCompareDialog(Element inElement) {
        WildLogApp.LOGGER.log(Level.INFO, "[ElementCompareDialog]");
        initComponents();
        ComboBoxFixer.configureComboBoxes(cmbGridSize);
        // Initialise the rest of the screen 
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.setupGlassPaneOnMainFrame(this);
        UtilsDialog.addEscapeKeyListener(this);
        // Attach clipboard
        UtilsUI.attachClipboardPopup(txtSearch1);
        UtilsUI.attachClipboardPopup(txtSearch2);
        UtilsUI.attachClipboardPopup(txaIdentification1, true, false);
        UtilsUI.attachClipboardPopup(txaIdentification2, true, false);
        // Load the tables
        UtilsTableGenerator.setupElementTableSmall(WildLogApp.getApplication(), tblElements1, null);
        UtilsTableGenerator.setupElementTableSmall(WildLogApp.getApplication(), tblElements2, null);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElements1);
        UtilsUI.attachKeyListernerToSelectKeyedRows(tblElements2);
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch1, tblElements1);
        UtilsUI.attachKeyListernerToFilterTableRows(txtSearch2, tblElements2);
        // Setup the grid sizes
        DefaultComboBoxModel<WildLogThumbnailSizes> model = new DefaultComboBoxModel<>();
        model.addElement(WildLogThumbnailSizes.S0100_SMALL);
        model.addElement(WildLogThumbnailSizes.S0200_MEDIUM);
        model.addElement(WildLogThumbnailSizes.S0300_NORMAL);
        cmbGridSize.setModel(model);
        cmbGridSize.setSelectedItem(SightingBox.DEFAULT_SIZE);
        // Pre-select the element (if provided)
        if (inElement != null) {
            // Wag eers vir die table om klaar te load voor ek iets probeer select
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < tblElements1.getRowCount(); t++) {
                        if ((long) tblElements1.getModel().getValueAt(t, 3) == inElement.getID()) {
                            int row = tblElements1.convertRowIndexToView(t);
                            tblElements1.getSelectionModel().setSelectionInterval(row, row);
                            if (row < (tblElements1.getRowCount() - 1)) {
                                row = row + 1;
                            }
                            tblElements1.scrollRectToVisible(tblElements1.getCellRect(row, 0, true));
                            break;
                        }
                    }
                }
            });
        }
        // Refresh the UI to show the initial values
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshUI(tblElements1, lblElement1, txaIdentification1, pnlGridView1);
                refreshUI(tblElements2, lblElement2, txaIdentification2, pnlGridView2);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlWrapper = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbGridSize = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        pnlLeft = new javax.swing.JPanel();
        txtSearch1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblElements1 = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        lblElement1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaIdentification1 = new javax.swing.JTextArea();
        pnlGridView1 = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        txtSearch2 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblElements2 = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        lblElement2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaIdentification2 = new javax.swing.JTextArea();
        pnlGridView2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compare Creatures");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/ElementList.png")).getImage());
        setMinimumSize(new java.awt.Dimension(960, 650));
        setPreferredSize(new java.awt.Dimension(900, 650));

        pnlWrapper.setBackground(new java.awt.Color(231, 240, 223));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setText("Creature Comparison");

        jLabel1.setText("Grid Size:");

        cmbGridSize.setSelectedItem(WildLogThumbnailSizes.S0200_MEDIUM);
        cmbGridSize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbGridSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGridSizeActionPerformed(evt);
            }
        });

        jLabel2.setText("px");

        pnlLeft.setBackground(new java.awt.Color(231, 240, 223));
        pnlLeft.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlLeft.setPreferredSize(new java.awt.Dimension(5000, 600));

        tblElements1.setAutoCreateRowSorter(true);
        tblElements1.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElements1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElements1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElements1MouseReleased(evt);
            }
        });
        tblElements1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElements1KeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblElements1);

        lblElement1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblElement1.setText("Creature1");

        txaIdentification1.setEditable(false);
        txaIdentification1.setColumns(20);
        txaIdentification1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaIdentification1.setLineWrap(true);
        txaIdentification1.setRows(5);
        txaIdentification1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaIdentification1);

        pnlGridView1.setBackground(new java.awt.Color(0, 0, 0));
        pnlGridView1.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLeftLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlGridView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlLeftLayout.createSequentialGroup()
                        .addComponent(lblElement1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3)
                    .addComponent(txtSearch1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(5, 5, 5))
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLeftLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lblElement1)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlGridView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pnlRight.setBackground(new java.awt.Color(231, 240, 223));
        pnlRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlRight.setPreferredSize(new java.awt.Dimension(5000, 600));

        tblElements2.setAutoCreateRowSorter(true);
        tblElements2.setSelectionBackground(new java.awt.Color(82, 115, 79));
        tblElements2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblElements2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElements2MouseReleased(evt);
            }
        });
        tblElements2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElements2KeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblElements2);

        lblElement2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblElement2.setText("Creature2");

        txaIdentification2.setEditable(false);
        txaIdentification2.setColumns(20);
        txaIdentification2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaIdentification2.setLineWrap(true);
        txaIdentification2.setRows(5);
        txaIdentification2.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaIdentification2);

        pnlGridView2.setBackground(new java.awt.Color(0, 0, 0));
        pnlGridView2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRightLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlGridView2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlRightLayout.createSequentialGroup()
                        .addComponent(lblElement2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(5, 5, 5))
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lblElement2)
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pnlGridView2, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout pnlWrapperLayout = new javax.swing.GroupLayout(pnlWrapper);
        pnlWrapper.setLayout(pnlWrapperLayout);
        pnlWrapperLayout.setHorizontalGroup(
            pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWrapperLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWrapperLayout.createSequentialGroup()
                        .addComponent(pnlLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                        .addGap(5, 5, 5)
                        .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(pnlWrapperLayout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(cmbGridSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel2)
                        .addGap(10, 10, 10))))
        );
        pnlWrapperLayout.setVerticalGroup(
            pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlWrapperLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(cmbGridSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlWrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .addComponent(pnlLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        getContentPane().add(pnlWrapper, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbGridSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGridSizeActionPerformed
        refreshUI(tblElements1, lblElement1, txaIdentification1, pnlGridView1);
        refreshUI(tblElements2, lblElement2, txaIdentification2, pnlGridView2);
    }//GEN-LAST:event_cmbGridSizeActionPerformed

    private void tblElements1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElements1KeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblElements1MouseReleased(null);
        }
    }//GEN-LAST:event_tblElements1KeyReleased

    private void tblElements1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElements1MouseReleased
        refreshUI(tblElements1, lblElement1, txaIdentification1, pnlGridView1);
    }//GEN-LAST:event_tblElements1MouseReleased

    private void tblElements2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElements2KeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            tblElements2MouseReleased(null);
        }
    }//GEN-LAST:event_tblElements2KeyReleased

    private void tblElements2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElements2MouseReleased
        refreshUI(tblElements2, lblElement2, txaIdentification2, pnlGridView2);
    }//GEN-LAST:event_tblElements2MouseReleased

    private void refreshUI(JTable inTable, JLabel inName, JTextArea inIdentification, JPanel inGrid) {
        Element element;
        if (inTable.getSelectedRowCount() == 1) {
            element = WildLogApp.getApplication().getDBI().findElement((Long) inTable.getModel().getValueAt(
                    inTable.convertRowIndexToModel(inTable.getSelectedRow()), 3), null, false, Element.class);
        }
        else {
            element = null;
        }
        if (element != null) {
            inName.setText(element.getPrimaryName());
            inIdentification.setText(element.getDiagnosticDescription());
            generateGrid(inGrid, element.getID());
        }
        else {
            inName.setText("Please select a Creature to compare...");
            inIdentification.setText("");
            inGrid.removeAll();
        }
    }
    
    private void generateGrid(JPanel inGridView, long inElementID) {
        inGridView.removeAll();
        inGridView.revalidate();
        inGridView.repaint();
        final JPanel pnlGrid = new JPanel();
        pnlGrid.setLayout(new ScrollableWrappedFlowLayout(FlowLayout.CENTER));
        JLabel lblTemp = new JLabel("");
        lblTemp.setForeground(Color.WHITE);
        lblTemp.setFont(lblTemp.getFont().deriveFont(18f));
        pnlGrid.add(lblTemp);
        pnlGrid.setBackground(Color.BLACK);
        JScrollPane scrGrid = new JScrollPane(pnlGrid, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrGrid.getVerticalScrollBar().setUnitIncrement(35);
        inGridView.add(scrGrid, BorderLayout.CENTER);
        inGridView.revalidate();
        inGridView.repaint();
        lblTemp.setText("Loading...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<Sighting> lstSightings = WildLogApp.getApplication().getDBI().listSightings(inElementID, 0, 0, false, Sighting.class);
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
// TODO: Add a pagenator or something, to not have to load ALL files at once...
//                        int boxCount = 0;
                    breakLoop: for (Sighting sighting : lstSightings) {
                        List<WildLogFile> lstSightingFiles = WildLogApp.getApplication().getDBI().listWildLogFiles(sighting.getWildLogFileID(), null, WildLogFile.class);
                        if (lstSightingFiles != null && !lstSightingFiles.isEmpty()) {
                            for (int t = 0; t < lstSightingFiles.size(); t++) {
                                generateGridBox(pnlGrid, sighting, t);
//                                    boxCount++;
//                                    if (boxCount >= GRID_LIMIT) {
//                                        break breakLoop;
//                                    }
                            }
                        }
                    }
                }
                pnlGrid.revalidate();
                pnlGrid.repaint();
                inGridView.revalidate();
                inGridView.repaint();
            }
        });
    }
    
    private void generateGridBox(JPanel inPnlGrid, Sighting inSighting, int inFileIndex) {
        SightingBox sightingBox = new SightingBox(inSighting, inFileIndex, false);
        sightingBox.setBoxSize((WildLogThumbnailSizes) cmbGridSize.getSelectedItem());
        sightingBox.setSelectable(false);
        inPnlGrid.add(sightingBox);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<WildLogThumbnailSizes> cmbGridSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblElement1;
    private javax.swing.JLabel lblElement2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlGridView1;
    private javax.swing.JPanel pnlGridView2;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JPanel pnlWrapper;
    private javax.swing.JTable tblElements1;
    private javax.swing.JTable tblElements2;
    private javax.swing.JTextArea txaIdentification1;
    private javax.swing.JTextArea txaIdentification2;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch2;
    // End of variables declaration//GEN-END:variables
}
