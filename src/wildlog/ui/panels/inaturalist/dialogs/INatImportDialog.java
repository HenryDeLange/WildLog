package wildlog.ui.panels.inaturalist.dialogs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.inaturalist.INatAPI;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsConcurency;


public class INatImportDialog extends JDialog {
    private final WildLogApp app = WildLogApp.getApplication();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final JsonParser PARSER = new JsonParser();


    public INatImportDialog(JFrame inParent) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[INatSightingDialog]");
        initComponents();
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
    }
    
    public INatImportDialog(JDialog inParent) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[INatSightingDialog]");
        initComponents();
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnViewWebsite = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnViewWebsite1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import iNaturalist Observations");
        setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/iNaturalist_small.png")).getImage());
        setMaximumSize(new java.awt.Dimension(800, 300));
        setMinimumSize(new java.awt.Dimension(600, 240));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(600, 260));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist.png"))); // NOI18N
        lblTitle.setText("Import iNaturalist Observations");

        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnOK.setToolTipText("Close the dialog.");
        btnOK.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOK.setFocusPainted(false);
        btnOK.setMaximumSize(null);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnViewWebsite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/iNaturalist.png"))); // NOI18N
        btnViewWebsite.setText("View Website");
        btnViewWebsite.setToolTipText("View the authenticated user on the iNaturalist website.");
        btnViewWebsite.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewWebsite.setFocusPainted(false);
        btnViewWebsite.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewWebsite.setMargin(new java.awt.Insets(2, 8, 2, 2));
        btnViewWebsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewWebsiteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlButtonsLayout = new javax.swing.GroupLayout(pnlButtons);
        pnlButtons.setLayout(pnlButtonsLayout);
        pnlButtonsLayout.setHorizontalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnViewWebsite, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnOK, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        pnlButtonsLayout.setVerticalGroup(
            pnlButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(btnViewWebsite, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("<html>This process will import iNaturalist observations of the authorized iNaturalist user. All observations that don't already have an associated <i>WildLog_ID</i> obseration field value will be imported. After the import the associated <i>WildLog_ID</i> observation field will be added to each iNaturalist observation.</html>");

        btnViewWebsite1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnViewWebsite1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/ShowGPS.png"))); // NOI18N
        btnViewWebsite1.setText("Import new iNaturalist Observations");
        btnViewWebsite1.setToolTipText("View the authenticated user on the iNaturalist website.");
        btnViewWebsite1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewWebsite1.setFocusPainted(false);
        btnViewWebsite1.setIconTextGap(8);
        btnViewWebsite1.setMargin(new java.awt.Insets(6, 20, 6, 20));
        btnViewWebsite1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewWebsite1ActionPerformed(evt);
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
                                .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                                .addGap(5, 5, 5))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnViewWebsite1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(btnViewWebsite1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewWebsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewWebsiteActionPerformed
        // Maak seker die Auth Token is OK
        if (WildLogApp.getINaturalistToken() == null || WildLogApp.getINaturalistToken().isEmpty()) {
            INatAuthTokenDialog dialog = new INatAuthTokenDialog(this);
            dialog.setVisible(true);
        }
        try {
            JsonElement jsonElement = INatAPI.getAuthenticatedUser(WildLogApp.getINaturalistToken());
            int userID = jsonElement.getAsJsonObject().get("id").getAsInt();
            if (userID > 0) {
                Desktop.getDesktop().browse(URI.create("https://www.inaturalist.org/users/" + userID));
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }//GEN-LAST:event_btnViewWebsiteActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnViewWebsite1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewWebsite1ActionPerformed
        UtilsConcurency.kickoffProgressbarTask(app, new ProgressbarTask(app) {
            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Starting the iNaturalist Import");
                
// TODO
                
                setMessage("Done with the iNaturalist import");
                return null;
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnViewWebsite1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnViewWebsite;
    private javax.swing.JButton btnViewWebsite1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlButtons;
    // End of variables declaration//GEN-END:variables
}