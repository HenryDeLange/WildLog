package wildlog.ui.dialogs;

import org.apache.logging.log4j.Level;
import javax.swing.JDialog;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;

public class WildNoteAboutBox extends JDialog {

    public WildNoteAboutBox() {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WildNoteAboutBox]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
        javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel developerLabel = new javax.swing.JLabel();
        javax.swing.JLabel developerNameLabel = new javax.swing.JLabel();
        javax.swing.JLabel emailLabel = new javax.swing.JLabel();
        javax.swing.JLabel emailAddressLabel = new javax.swing.JLabel();
        javax.swing.JLabel emailLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel emailAddressLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel developerLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel developerNameLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About WildNote");
        setMinimumSize(new java.awt.Dimension(620, 240));
        setModal(true);
        setName("aboutBox"); // NOI18N

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+3));
        appTitleLabel.setText("About WildNote");
        appTitleLabel.setToolTipText("");
        appTitleLabel.setName("appTitleLabel"); // NOI18N

        appDescLabel.setText("<html>WildNote is a seperate software application developed by Henry de Lange for Android devices. Using WildNote you can easily capture Observations on the go and import the data into WildLog. For more information please visit the website.</html>");
        appDescLabel.setName("appDescLabel"); // NOI18N

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/AboutWildNote.gif"))); // NOI18N
        imageLabel.setName("imageLabel"); // NOI18N

        developerLabel.setFont(developerLabel.getFont().deriveFont(developerLabel.getFont().getStyle() | java.awt.Font.BOLD));
        developerLabel.setText("Copyright & Developer:");
        developerLabel.setName("developerLabel"); // NOI18N

        developerNameLabel.setText("Henry de Lange");
        developerNameLabel.setName("developerNameLabel"); // NOI18N

        emailLabel.setFont(emailLabel.getFont().deriveFont(emailLabel.getFont().getStyle() | java.awt.Font.BOLD));
        emailLabel.setText("Contact & Support Email:");
        emailLabel.setName("emailLabel"); // NOI18N

        emailAddressLabel.setText("support@mywild.co.za");
        emailAddressLabel.setName("emailAddressLabel"); // NOI18N

        emailLabel1.setFont(emailLabel1.getFont().deriveFont(emailLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        emailLabel1.setText("Websites:");
        emailLabel1.setName("emailLabel1"); // NOI18N

        emailAddressLabel1.setText("<html>http://www.mywild.co.za</html>");
        emailAddressLabel1.setName("emailAddressLabel1"); // NOI18N

        developerLabel1.setFont(developerLabel1.getFont().deriveFont(developerLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        developerLabel1.setText("Compatible Version:");
        developerLabel1.setName("developerLabel1"); // NOI18N

        developerNameLabel1.setText("1.3");
        developerNameLabel1.setName("developerNameLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appDescLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .addComponent(appTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailLabel)
                            .addComponent(developerLabel)
                            .addComponent(emailLabel1)
                            .addComponent(developerLabel1))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(developerNameLabel1)
                            .addComponent(emailAddressLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(developerNameLabel)
                            .addComponent(emailAddressLabel))))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(imageLabel)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(appTitleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(developerLabel1)
                            .addComponent(developerNameLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(developerNameLabel)
                            .addComponent(developerLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(emailLabel)
                            .addComponent(emailAddressLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(emailLabel1)
                            .addComponent(emailAddressLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
