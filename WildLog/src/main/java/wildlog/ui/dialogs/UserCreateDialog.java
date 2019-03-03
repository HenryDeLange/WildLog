package wildlog.ui.dialogs;

import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogUser;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.encryption.PasswordEncryptor;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;


public class UserCreateDialog extends JDialog {
    private final WildLogApp app = WildLogApp.getApplication();

    public UserCreateDialog(JFrame inParent, boolean inForceOwner) {
        super(inParent, true);
        WildLogApp.LOGGER.log(Level.INFO, "[UserCreateDialog]");
        doSetup(inForceOwner);
        // Setup the default behavior (this is for JFrames)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
    }
    
    public UserCreateDialog(JDialog inParent, boolean inForceOwner) {
        super(inParent, true);
        doSetup(inForceOwner);
        // Setup the default behavior (this is for JFrames)
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
    }
    
    private void doSetup(boolean inForceOwner) {
        initComponents();
        pack();
        lblWorkspaceName.setText(WildLogApp.getApplication().getWildLogOptions().getWorkspaceName());
        cmbUserType.removeItem(WildLogUserTypes.WILDLOG_MASTER);
        cmbUserType.removeItem(WildLogUserTypes.NONE);
        if (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.ADMIN) {
            cmbUserType.removeItem(WildLogUserTypes.OWNER);
            cmbUserType.removeItem(WildLogUserTypes.ADMIN);
        }
        // If this is the owner then show a message first
        if (inForceOwner) {
            cmbUserType.setSelectedItem(WildLogUserTypes.OWNER);
            cmbUserType.setEnabled(false);
// FIXME: Op al die nuwe opups werk die glasspane nie reg nie...
            WLOptionPane.showMessageDialog(this.getParent(),
                    "<html>Please specify the Workspace Owner. This user will be able to create and remove all other users.</html>",
                    "Create Workspace Owner", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblWorkspaceName = new javax.swing.JLabel();
        cmbUserType = new javax.swing.JComboBox<>();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtPasswordConfirm = new javax.swing.JPasswordField();
        btnCreateUser = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create Workspace User");
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/WildLog Icon Selected.gif")).getImage());
        setModal(true);
        setResizable(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Username:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("New Password:");

        lblWorkspaceName.setText("<workspace name>");

        cmbUserType.setModel(new DefaultComboBoxModel(WildLogUserTypes.values()));
        cmbUserType.setSelectedItem(WildLogUserTypes.VOLUNTEER);
        cmbUserType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbUserType.setFocusable(false);

        btnCreateUser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCreateUser.setText("Create User");
        btnCreateUser.setToolTipText("Log into the Workspace.");
        btnCreateUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCreateUser.setFocusPainted(false);
        btnCreateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateUserActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Workspace:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Confirm Password:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("User Type:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Create Workspace User");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCreateUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblWorkspaceName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbUserType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtUsername)
                                    .addComponent(txtPasswordConfirm))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblWorkspaceName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPasswordConfirm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(btnCreateUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateUserActionPerformed
        // Validate the input
        if (txtUsername.getText() == null || txtUsername.getText().length() < 3) {
            WLOptionPane.showMessageDialog(this,
                    "Please specify a username that is at least 3 characters in length.",
                    "Invalid Username", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtPassword.getPassword() == null || txtPassword.getPassword().length < 8) {
            WLOptionPane.showMessageDialog(this,
                    "Please specify a password that is at least 8 characters in length.",
                    "Invalid Password", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Arrays.equals(txtPassword.getPassword(), txtPasswordConfirm.getPassword())) {
            WLOptionPane.showMessageDialog(this,
                    "Please make sure the password was typed in correctly.",
                    "Incorrect Password", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Make sure the WildLog Master user is present
        if (app.getDBI().listUsers(WildLogUserTypes.WILDLOG_MASTER, WildLogUser.class).isEmpty()) {
            app.getDBI().createUser(new WildLogUser(
                    "WILDLOGMASTER", 
                    "COouWeD9XsAas0x+pfEGLmph7s9T+Xoc1KNU3BxEe/46lPiYT/sviHfMKlzm3aE7L1sNqI2zk5axZ6Bv5oFpSQ==", 
                    WildLogUserTypes.WILDLOG_MASTER));
        }
        // Create the new user
        if (app.getDBI().createUser(new WildLogUser(
                txtUsername.getText(), 
                PasswordEncryptor.generateHashedPassword(txtPassword.getPassword()), 
                (WildLogUserTypes) cmbUserType.getSelectedItem()))) {
            // Close this dialog
            setVisible(false);
            dispose();
        }
        else {
            WLOptionPane.showMessageDialog(this,
                    "The user was not created. Make sure to specify a valid username and password.",
                    "Error: Could not create User!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateUserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateUser;
    private javax.swing.JComboBox<String> cmbUserType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblWorkspaceName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordConfirm;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}