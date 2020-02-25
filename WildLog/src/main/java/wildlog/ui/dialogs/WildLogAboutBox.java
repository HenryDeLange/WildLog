package wildlog.ui.dialogs;

import org.apache.logging.log4j.Level;
import javax.swing.JDialog;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.WildLogPaths;

public class WildLogAboutBox extends JDialog {

    public WildLogAboutBox(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WildLogAboutBox]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(inApp.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(inApp.getMainFrame(), this);
        pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel lblTitle = new javax.swing.JLabel();
        javax.swing.JLabel lblWorkspace = new javax.swing.JLabel();
        javax.swing.JLabel lblActiveWorkspace = new javax.swing.JLabel();
        javax.swing.JLabel lblVersion = new javax.swing.JLabel();
        javax.swing.JLabel lblActiveVersion = new javax.swing.JLabel();
        javax.swing.JLabel lblDescription = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel lblCopyright = new javax.swing.JLabel();
        javax.swing.JLabel lblDeveloper = new javax.swing.JLabel();
        javax.swing.JLabel lblEmail = new javax.swing.JLabel();
        javax.swing.JLabel lblEmailAddress = new javax.swing.JLabel();
        javax.swing.JLabel lblWebsite = new javax.swing.JLabel();
        javax.swing.JLabel lblWebsiteAddress = new javax.swing.JLabel();
        javax.swing.JLabel lblJava = new javax.swing.JLabel();
        javax.swing.JLabel lblActiveJava = new javax.swing.JLabel();
        javax.swing.JLabel lblSettings = new javax.swing.JLabel();
        javax.swing.JLabel lblActiveSettings = new javax.swing.JLabel();
        javax.swing.JLabel lblApplication = new javax.swing.JLabel();
        javax.swing.JLabel lblActiveApplication = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About WildLog");
        setMinimumSize(new java.awt.Dimension(740, 300));
        setModal(true);
        setName("aboutBox"); // NOI18N

        lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | java.awt.Font.BOLD, lblTitle.getFont().getSize()+3));
        lblTitle.setText("About WildLog");
        lblTitle.setToolTipText("");
        lblTitle.setName("lblTitle"); // NOI18N

        lblWorkspace.setFont(lblWorkspace.getFont().deriveFont(lblWorkspace.getFont().getStyle() | java.awt.Font.BOLD));
        lblWorkspace.setText("Active Workspace:");
        lblWorkspace.setName("lblWorkspace"); // NOI18N

        lblActiveWorkspace.setText(WildLogPaths.getFullWorkspacePrefix().toString());
        lblActiveWorkspace.setName("lblActiveWorkspace"); // NOI18N

        lblVersion.setFont(lblVersion.getFont().deriveFont(lblVersion.getFont().getStyle() | java.awt.Font.BOLD));
        lblVersion.setText("Product Version:");
        lblVersion.setName("lblVersion"); // NOI18N

        lblActiveVersion.setText(WildLogApp.WILDLOG_VERSION);
        lblActiveVersion.setName("lblActiveVersion"); // NOI18N

        lblDescription.setText("<html>Use at own risk. WildLog is developed by Henry de Lange.<br/>WildLog is a Java application and makes use of third party libraries to help with some of the functionality:  <br/>H2, SwingX, JMF, MetadataExtractor, MediaUtil, GeoTools, PDFBox, Apache POI, BalloonTip, Oshi, Gson, Humble Video and Azure. See the application folder for license details. <br/>(Note: The media applications JPEGView and Media Player Classic - Home Cinema can be used if bundled with WildLog.)</html>");
        lblDescription.setName("lblDescription"); // NOI18N

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/About.gif"))); // NOI18N
        imageLabel.setName("imageLabel"); // NOI18N

        lblCopyright.setFont(lblCopyright.getFont().deriveFont(lblCopyright.getFont().getStyle() | java.awt.Font.BOLD));
        lblCopyright.setText("Copyright & Developer:");
        lblCopyright.setName("lblCopyright"); // NOI18N

        lblDeveloper.setText("Henry de Lange");
        lblDeveloper.setName("lblDeveloper"); // NOI18N

        lblEmail.setFont(lblEmail.getFont().deriveFont(lblEmail.getFont().getStyle() | java.awt.Font.BOLD));
        lblEmail.setText("Contact & Support Email:");
        lblEmail.setName("lblEmail"); // NOI18N

        lblEmailAddress.setText("support@mywild.co.za");
        lblEmailAddress.setName("lblEmailAddress"); // NOI18N

        lblWebsite.setFont(lblWebsite.getFont().deriveFont(lblWebsite.getFont().getStyle() | java.awt.Font.BOLD));
        lblWebsite.setText("Website:");
        lblWebsite.setName("lblWebsite"); // NOI18N

        lblWebsiteAddress.setText("<html> http://www.mywild.co.za </html>");
        lblWebsiteAddress.setName("lblWebsiteAddress"); // NOI18N

        lblJava.setFont(lblJava.getFont().deriveFont(lblJava.getFont().getStyle() | java.awt.Font.BOLD));
        lblJava.setText("Active Java Runtime:");
        lblJava.setName("lblJava"); // NOI18N

        lblActiveJava.setText(System.getProperty("java.home"));
        lblActiveJava.setName("lblActiveJava"); // NOI18N

        lblSettings.setFont(lblSettings.getFont().deriveFont(lblSettings.getFont().getStyle() | java.awt.Font.BOLD));
        lblSettings.setText("Active Settings Folder:");
        lblSettings.setName("lblSettings"); // NOI18N

        lblActiveSettings.setText(WildLogApp.getACTIVE_WILDLOG_SETTINGS_FOLDER().toAbsolutePath().toString());
        lblActiveSettings.setName("lblActiveSettings"); // NOI18N

        lblApplication.setFont(lblApplication.getFont().deriveFont(lblApplication.getFont().getStyle() | java.awt.Font.BOLD));
        lblApplication.setText("Active Application Folder:");
        lblApplication.setName("lblApplication"); // NOI18N

        lblActiveApplication.setText(WildLogApp.getACTIVEWILDLOG_CODE_FOLDER().toAbsolutePath().toString());
        lblActiveApplication.setName("lblActiveApplication"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblWorkspace)
                                    .addComponent(lblVersion)
                                    .addComponent(lblEmail)
                                    .addComponent(lblCopyright)
                                    .addComponent(lblWebsite)
                                    .addComponent(lblJava)
                                    .addComponent(lblSettings))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblActiveVersion)
                                            .addComponent(lblDeveloper)
                                            .addComponent(lblEmailAddress)
                                            .addComponent(lblWebsiteAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblActiveJava)
                                            .addComponent(lblActiveApplication)))
                                    .addComponent(lblActiveWorkspace)
                                    .addComponent(lblActiveSettings)))
                            .addComponent(lblApplication))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTitle)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblWorkspace)
                            .addComponent(lblActiveWorkspace))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSettings)
                            .addComponent(lblActiveSettings))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblApplication)
                            .addComponent(lblActiveApplication))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblJava)
                            .addComponent(lblActiveJava))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblVersion)
                            .addComponent(lblActiveVersion))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDeveloper)
                            .addComponent(lblCopyright))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEmail)
                            .addComponent(lblEmailAddress))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblWebsite)
                            .addComponent(lblWebsiteAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
