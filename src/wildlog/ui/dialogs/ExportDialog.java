package wildlog.ui.dialogs;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.html.utils.UtilsHTML;
import wildlog.mapping.kml.utils.UtilsKML;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;


public class ExportDialog extends JDialog {
    private WildLogApp app;
    private Location location;
    private Element element;
    private Visit visit;
    private Sighting sighting;

    public ExportDialog(WildLogApp inApp, Location inLocation, Element inElement, Visit inVisit, Sighting inSighting) {
        super(inApp.getMainFrame());
        // Set passed in values
        app = inApp;
        location = inLocation;
        element = inElement;
        visit = inVisit;
        sighting = inSighting;
        // Auto generated code
        initComponents();
        // Determine what buttons to show
        // ... Show all options
        // Pack
        pack();
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(app.getMainFrame(), this);
        UtilsDialog.addModalBackgroundPanel(app.getMainFrame(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExportHTML = new javax.swing.JButton();
        btnExportKML = new javax.swing.JButton();
        btnExportCSV = new javax.swing.JButton();
        btnExportFiles = new javax.swing.JButton();
        btnExportWorkspace = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Available Exports");
        setIconImage(new ImageIcon(app.getClass().getResource("resources/icons/Export.png")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnExportHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        btnExportHTML.setText("Export as HTML");
        btnExportHTML.setToolTipText("Create a HTML web page for all relevant Observations and linked records. Can be viewed in a web browser.");
        btnExportHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportHTML.setFocusPainted(false);
        btnExportHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportHTML.setIconTextGap(10);
        btnExportHTML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportHTML.setMaximumSize(new java.awt.Dimension(230, 35));
        btnExportHTML.setMinimumSize(new java.awt.Dimension(230, 35));
        btnExportHTML.setName("btnExportHTML"); // NOI18N
        btnExportHTML.setPreferredSize(new java.awt.Dimension(230, 35));
        btnExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportHTMLActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportHTML);

        btnExportKML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Google Earth Icon.gif"))); // NOI18N
        btnExportKML.setText("Export as KML");
        btnExportKML.setToolTipText("Export a KML file for all relevant Observations and linked records. Can be opened in Google Earth, etc.");
        btnExportKML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportKML.setFocusPainted(false);
        btnExportKML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportKML.setIconTextGap(11);
        btnExportKML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportKML.setMaximumSize(new java.awt.Dimension(230, 35));
        btnExportKML.setMinimumSize(new java.awt.Dimension(230, 35));
        btnExportKML.setName("btnExportKML"); // NOI18N
        btnExportKML.setPreferredSize(new java.awt.Dimension(230, 35));
        btnExportKML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportKMLActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportKML);

        btnExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV Icon.gif"))); // NOI18N
        btnExportCSV.setText("Export as CSV");
        btnExportCSV.setToolTipText("Export a CSV file for all relevant Observations and linked records. Can be opened in Excel, etc.");
        btnExportCSV.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportCSV.setEnabled(false);
        btnExportCSV.setFocusPainted(false);
        btnExportCSV.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportCSV.setIconTextGap(10);
        btnExportCSV.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportCSV.setMaximumSize(new java.awt.Dimension(230, 35));
        btnExportCSV.setMinimumSize(new java.awt.Dimension(230, 35));
        btnExportCSV.setName("btnExportCSV"); // NOI18N
        btnExportCSV.setPreferredSize(new java.awt.Dimension(230, 35));
        btnExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCSVActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportCSV);

        btnExportFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/EXIF.png"))); // NOI18N
        btnExportFiles.setText("Export Files");
        btnExportFiles.setToolTipText("Save copies of all relevant files at the selected location.");
        btnExportFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportFiles.setEnabled(false);
        btnExportFiles.setFocusPainted(false);
        btnExportFiles.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportFiles.setIconTextGap(10);
        btnExportFiles.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportFiles.setMaximumSize(new java.awt.Dimension(230, 35));
        btnExportFiles.setMinimumSize(new java.awt.Dimension(230, 35));
        btnExportFiles.setName("btnExportFiles"); // NOI18N
        btnExportFiles.setPreferredSize(new java.awt.Dimension(230, 35));
        btnExportFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportFilesActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportFiles);

        btnExportWorkspace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon.gif"))); // NOI18N
        btnExportWorkspace.setText("Export to new Workspace");
        btnExportWorkspace.setToolTipText("Create a new Workspace containing only relevant Observations and linked records.");
        btnExportWorkspace.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportWorkspace.setEnabled(false);
        btnExportWorkspace.setFocusPainted(false);
        btnExportWorkspace.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportWorkspace.setIconTextGap(10);
        btnExportWorkspace.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportWorkspace.setMaximumSize(new java.awt.Dimension(230, 35));
        btnExportWorkspace.setMinimumSize(new java.awt.Dimension(230, 35));
        btnExportWorkspace.setName("btnExportWorkspace"); // NOI18N
        btnExportWorkspace.setPreferredSize(new java.awt.Dimension(230, 35));
        btnExportWorkspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportWorkspaceActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportWorkspace);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportHTMLActionPerformed
        if (element != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsFileProcessing.openFile(UtilsHTML.exportHTML(element, app, this));
                    return null;
                }
            });
        }
        if (location != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsFileProcessing.openFile(UtilsHTML.exportHTML(location, app, this));
                    return null;
                }
            });
        }
        if (visit != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsFileProcessing.openFile(UtilsHTML.exportHTML(visit, app, this));
                    return null;
                }
            });
        }
        if (sighting != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsFileProcessing.openFile(UtilsHTML.exportHTML(sighting, app, this));
                    return null;
                }
            });
        }
        dispose();
    }//GEN-LAST:event_btnExportHTMLActionPerformed

    private void btnExportFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportFilesActionPerformed

    }//GEN-LAST:event_btnExportFilesActionPerformed

    private void btnExportWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportWorkspaceActionPerformed

    }//GEN-LAST:event_btnExportWorkspaceActionPerformed

    private void btnExportKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportKMLActionPerformed
        if (location != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsKML.exportKML(location, this, app);
                    return null;
                }
            });
        }
        if (element != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsKML.exportKML(element, this, app);
                    return null;
                }
            });
        }
        if (visit != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsKML.exportKML(visit, this, app);
                    return null;
                }
            });
        }
        if (sighting != null) {
            UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsKML.exportKML(sighting, this, app);
                    return null;
                }
            });
        }
        dispose();
    }//GEN-LAST:event_btnExportKMLActionPerformed

    private void btnExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCSVActionPerformed

    }//GEN-LAST:event_btnExportCSVActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportCSV;
    private javax.swing.JButton btnExportFiles;
    private javax.swing.JButton btnExportHTML;
    private javax.swing.JButton btnExportKML;
    private javax.swing.JButton btnExportWorkspace;
    // End of variables declaration//GEN-END:variables
}
