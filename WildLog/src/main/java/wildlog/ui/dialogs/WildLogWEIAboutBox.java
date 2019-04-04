package wildlog.ui.dialogs;

import org.apache.logging.log4j.Level;
import javax.swing.JDialog;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;

public class WildLogWEIAboutBox extends JDialog {

    public WildLogWEIAboutBox(WildLogApp inApp) {
        super();
        WildLogApp.LOGGER.log(Level.INFO, "[WildLogAboutBox]");
        initComponents();
        // Setup the default behavior
        UtilsDialog.setDialogToCenter(inApp.getMainFrame(), this);
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.addModalBackgroundPanel(inApp.getMainFrame(), this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel lblTitle = new javax.swing.JLabel();
        javax.swing.JLabel lblDescription = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel lblWebsite = new javax.swing.JLabel();
        javax.swing.JLabel lblWebsiteAddress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About WEI");
        setModal(true);
        setName("aboutBox"); // NOI18N
        setResizable(false);

        lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | java.awt.Font.BOLD, lblTitle.getFont().getSize()+3));
        lblTitle.setText("About WildLog for WEI");
        lblTitle.setToolTipText("");
        lblTitle.setName("lblTitle"); // NOI18N

        lblDescription.setText("<html>This version of WildLog has been specifically designed to tailor to WEI's need. It includes all the usual functionality of WildLog, but with a number of enhancements such as improved reporting and user management.</html>");
        lblDescription.setName("lblDescription"); // NOI18N

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/wei/WEI-square-125px.png"))); // NOI18N
        imageLabel.setName("imageLabel"); // NOI18N

        lblWebsite.setFont(lblWebsite.getFont().deriveFont(lblWebsite.getFont().getStyle() | java.awt.Font.BOLD));
        lblWebsite.setText("Website:");
        lblWebsite.setName("lblWebsite"); // NOI18N

        lblWebsiteAddress.setText("<html>http://wei.org.za</html>");
        lblWebsiteAddress.setName("lblWebsiteAddress"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblWebsite)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(lblWebsiteAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imageLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(5, 5, 5)
                        .addComponent(lblDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblWebsite)
                            .addComponent(lblWebsiteAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(13, 13, 13))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}