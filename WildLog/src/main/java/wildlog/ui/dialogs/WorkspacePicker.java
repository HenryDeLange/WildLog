package wildlog.ui.dialogs;

import javax.swing.JFrame;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;


public class WorkspacePicker extends JFrame {


    public WorkspacePicker() {
        WildLogApp.LOGGER.log(Level.INFO, "[WorkspacePicker]");
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbWorkspacePath = new javax.swing.JComboBox<>();
        btnChooseWorkspace = new javax.swing.JButton();
        btnCloudSync = new javax.swing.JButton();
        btnCloudSync1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WildLog Workspace Picker");

        jLabel3.setBackground(new java.awt.Color(0, 153, 0));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/About.gif"))); // NOI18N
        jLabel3.setOpaque(true);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("WildLog Workspace Picker");

        jLabel2.setBackground(new java.awt.Color(0, 153, 0));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/About.gif"))); // NOI18N
        jLabel2.setOpaque(true);

        cmbWorkspacePath.setEditable(true);
        cmbWorkspacePath.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cmbWorkspacePath.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbWorkspacePath.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cmbWorkspacePath.setFocusable(false);

        btnChooseWorkspace.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnChooseWorkspace.setText("OPEN / CREATE");
        btnChooseWorkspace.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChooseWorkspace.setFocusPainted(false);

        btnCloudSync.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCloudSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Sync.png"))); // NOI18N
        btnCloudSync.setText("Cloud Download");
        btnCloudSync.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloudSync.setFocusPainted(false);
        btnCloudSync.setMargin(new java.awt.Insets(2, 6, 2, 6));

        btnCloudSync1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnCloudSync1.setText("Browse");
        btnCloudSync1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloudSync1.setFocusPainted(false);
        btnCloudSync1.setMargin(new java.awt.Insets(2, 12, 2, 12));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCloudSync)
                                .addGap(10, 10, 10)
                                .addComponent(btnChooseWorkspace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(cmbWorkspacePath, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCloudSync1))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lblTitle)
                .addGap(5, 5, 5)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbWorkspacePath, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloudSync1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChooseWorkspace, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloudSync, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseWorkspace;
    private javax.swing.JButton btnCloudSync;
    private javax.swing.JButton btnCloudSync1;
    private javax.swing.JComboBox<String> cmbWorkspacePath;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables
}
