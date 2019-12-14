package wildlog.ui.maps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;


public class LegendDialog extends JDialog {
    private Map<String, Color> mapLegends;
    private VBox vBox;

    public LegendDialog(JFrame inParent, Map<String, Color> inMapLegends) {
        // NOTE: This JDialog can't be modal because it breaks the nested JavaFX stuff...
        //       See http://stackoverflow.com/questions/29264385/javafx-swing-window-does-not-open-after-update-to-java-1-8u40
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[LegendDialog]");
        initComponents();
        mapLegends = inMapLegends;
        // Setup the escape key
        final LegendDialog thisHandler = this;
        thisHandler.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        thisHandler.setVisible(false);
                        thisHandler.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Position the dialog and setup modal background
        UtilsDialog.setDialogToCenter(inParent, thisHandler);
        UtilsDialog.addModalBackgroundPanel(inParent, thisHandler);
        // Setup the legend
        JFXPanel jfxLegendPanel = new JFXPanel();
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane);
        vBox = new VBox(5);
        vBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        anchorPane.getChildren().add(scrollPane);
        jfxLegendPanel.setScene(scene);
        List<String> lstLayerNames = new ArrayList<>(mapLegends.keySet());
        Collections.sort(lstLayerNames);
        vBox.getChildren().add(new Separator());
        for (String layer : lstLayerNames) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(0, 5, 0, 10));
            Color color = mapLegends.get(layer);
            ColorPicker colorPicker = new ColorPicker(new javafx.scene.paint.Color(
                    color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0, color.getAlpha()/255.0));
            colorPicker.setPrefWidth(70);
            colorPicker.setPrefHeight(40);
            colorPicker.getStylesheets().add("wildlog/ui/maps/styling/LegendDialog.css");
            row.getChildren().add(colorPicker);
            Label label = new Label(layer);
            label.setFont(Font.font(12));
            label.setPrefHeight(40);
            row.getChildren().add(label);
            vBox.getChildren().add(row);
            vBox.getChildren().add(new Separator());
        }
        pnlLegendContent.add(jfxLegendPanel, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pnlLegendContent = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Map Legend");
        setMinimumSize(new java.awt.Dimension(550, 290));
        setPreferredSize(new java.awt.Dimension(550, 455));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Map Legend:");

        jLabel2.setText("<html>To change the colour of a Layer on the Map, select a new colour and press Save.<br/><b>Note:</b> Currently only shapefile Layers can be recoloured.<br/><b>Note:</b> If the shapefile has a SLD style then it will be used (can't be recoloured).</html>");

        pnlLegendContent.setLayout(new java.awt.BorderLayout());

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnSave.setToolTipText("Close this dialog and confirm the active layers to be displayed on the map.");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlLegendContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addComponent(pnlLegendContent, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        for (Node row : vBox.getChildren()) {
            if (row instanceof HBox) {
                ColorPicker colorPicker = (ColorPicker) ((HBox) row).getChildren().get(0);
                Label label = (Label) ((HBox) row).getChildren().get(1);
                javafx.scene.paint.Color color = colorPicker.getValue();
                mapLegends.put(label.getText(), new Color(
                        (int)(color.getRed()*255.0), (int)(color.getGreen()*255.0), (int)(color.getBlue()*255.0), (int)(color.getOpacity()*255.0)));
            }
        }
        setVisible(false);
        dispose();
        // Need to manually reload the map now, because this JDialog can't be modal (otherwise the nestes JFXPanel doesn't work as expected)
        ((MapsBaseDialog) getParent()).getActiveMap().loadMap();
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnSave;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JPanel pnlLegendContent;
    // End of variables declaration//GEN-END:variables
}
