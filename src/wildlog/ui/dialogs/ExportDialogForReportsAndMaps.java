package wildlog.ui.dialogs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.html.utils.UtilsHTML;
import wildlog.maps.kml.UtilsKML;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class ExportDialogForReportsAndMaps extends JDialog {
    public static enum ExportType {REPORTS, MAPS};
    private final BufferedImage bufferedImage;
    private final Node node;
    private final String name;
    private final List<Sighting> lstSightings;
    private ExportType type;
    
    
    public ExportDialogForReportsAndMaps(JFrame inParent, BufferedImage inImage, Node inNode, String inName, List<Sighting> inLstSightings, ExportType inType) {
        super(inParent);
        WildLogApp.LOGGER.log(Level.INFO, "[ExportDialogForReportsAndMaps]");
        // Set passed in values
        bufferedImage = inImage;
        node = inNode;
        name = inName;
        lstSightings = inLstSightings;
        type = inType;
        // Auto generated code
        initComponents();
        // Hide the KML export for reports
        if (ExportType.REPORTS.equals(type)) {
            btnExportKML.setVisible(false);
        }
        // Pack
        pack();
        // Setup the default behavior
        UtilsDialog.addEscapeKeyListener(this);
        UtilsDialog.setDialogToCenter(inParent, this);
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        UtilsDialog.addModalBackgroundPanel(this, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExportImage = new javax.swing.JButton();
        btnExportHTML = new javax.swing.JButton();
        btnExportCSV = new javax.swing.JButton();
        btnExportPDF = new javax.swing.JButton();
        btnExportKML = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export Formats");
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/Export.png")).getImage());
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        btnExportImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Image.png"))); // NOI18N
        btnExportImage.setText("Export as Image (Recommended)");
        btnExportImage.setToolTipText("Create a PNG image file of the active report or map. This is the recommened way to export a report or map.");
        btnExportImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportImage.setFocusPainted(false);
        btnExportImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportImage.setIconTextGap(10);
        btnExportImage.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportImage.setMaximumSize(new java.awt.Dimension(260, 35));
        btnExportImage.setMinimumSize(new java.awt.Dimension(260, 35));
        btnExportImage.setName("btnExportImage"); // NOI18N
        btnExportImage.setPreferredSize(new java.awt.Dimension(260, 35));
        btnExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportImageActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportImage);

        btnExportHTML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/HTML Icon.gif"))); // NOI18N
        btnExportHTML.setText("Export as Offline Webpage");
        btnExportHTML.setToolTipText("Create a basic HTML web page that can be viewed offline to show theObservations used by the active report or map.");
        btnExportHTML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportHTML.setFocusPainted(false);
        btnExportHTML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportHTML.setIconTextGap(10);
        btnExportHTML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportHTML.setMaximumSize(new java.awt.Dimension(260, 35));
        btnExportHTML.setMinimumSize(new java.awt.Dimension(260, 35));
        btnExportHTML.setName("btnExportHTML"); // NOI18N
        btnExportHTML.setPreferredSize(new java.awt.Dimension(260, 35));
        btnExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportHTMLActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportHTML);

        btnExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/CSV.png"))); // NOI18N
        btnExportCSV.setText("Export as Spreadsheet");
        btnExportCSV.setToolTipText("Create a CSV file of all relevant Observations used by this report or map. Can be opened in Excel, etc.");
        btnExportCSV.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportCSV.setFocusPainted(false);
        btnExportCSV.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportCSV.setIconTextGap(10);
        btnExportCSV.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportCSV.setMaximumSize(new java.awt.Dimension(260, 35));
        btnExportCSV.setMinimumSize(new java.awt.Dimension(260, 35));
        btnExportCSV.setName("btnExportCSV"); // NOI18N
        btnExportCSV.setPreferredSize(new java.awt.Dimension(260, 35));
        btnExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCSVActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportCSV);

        btnExportPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/PDF.png"))); // NOI18N
        btnExportPDF.setText("Export as PDF");
        btnExportPDF.setToolTipText("Create a PDF file of the active report or map.");
        btnExportPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportPDF.setFocusPainted(false);
        btnExportPDF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportPDF.setIconTextGap(10);
        btnExportPDF.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportPDF.setMaximumSize(new java.awt.Dimension(260, 35));
        btnExportPDF.setMinimumSize(new java.awt.Dimension(260, 35));
        btnExportPDF.setName("btnExportPDF"); // NOI18N
        btnExportPDF.setPreferredSize(new java.awt.Dimension(260, 35));
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportPDF);

        btnExportKML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/GoogleEarth.png"))); // NOI18N
        btnExportKML.setText("Export as KML");
        btnExportKML.setToolTipText("Export a KML file for all relevant Observations and linked records. Can be opened in Google Earth, etc.");
        btnExportKML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportKML.setFocusPainted(false);
        btnExportKML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnExportKML.setIconTextGap(11);
        btnExportKML.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnExportKML.setMaximumSize(new java.awt.Dimension(260, 35));
        btnExportKML.setMinimumSize(new java.awt.Dimension(260, 35));
        btnExportKML.setName("btnExportKML"); // NOI18N
        btnExportKML.setPreferredSize(new java.awt.Dimension(260, 35));
        btnExportKML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportKMLActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportKML);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/WildLog Icon Selected.gif"))); // NOI18N
        btnPrint.setText("Print the Report");
        btnPrint.setToolTipText("Try to print the report or map using your default installed printer.");
        btnPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrint.setFocusPainted(false);
        btnPrint.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrint.setIconTextGap(10);
        btnPrint.setMargin(new java.awt.Insets(2, 8, 2, 8));
        btnPrint.setMaximumSize(new java.awt.Dimension(260, 35));
        btnPrint.setMinimumSize(new java.awt.Dimension(260, 35));
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.setPreferredSize(new java.awt.Dimension(260, 35));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        getContentPane().add(btnPrint);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCSVActionPerformed
        UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Exporting Report CSV for '" + name + "'");
                Path root;
                if (ExportType.REPORTS.equals(type)) {
                    root = WildLogPaths.WILDLOG_EXPORT_REPORTS_CSV.getAbsoluteFullPath();
                }
                else {
                    root = WildLogPaths.WILDLOG_EXPORT_MAPS_CSV.getAbsoluteFullPath();
                }
                Path filePath = root.resolve(name + " (" + System.currentTimeMillis() + ").csv");
                Files.createDirectories(filePath.getParent());
                WildLogApp.getApplication().getDBI().doExportCSV(filePath, false, null, null, null, null, lstSightings);
                UtilsFileProcessing.openFile(filePath);
                setProgress(100);
                setMessage("Done Exporting Report CSV for '" + name + "'");
                return null;
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnExportCSVActionPerformed

    private void btnExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportImageActionPerformed
        if (bufferedImage != null) {
            UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                @Override
                protected Object doInBackground() throws Exception {
                    setProgress(0);
                    setMessage("Exporting Report Image for '" + name + "'");
                    Path root;
                    if (ExportType.REPORTS.equals(type)) {
                        root = WildLogPaths.WILDLOG_EXPORT_REPORTS_PNG.getAbsoluteFullPath();
                    }
                    else {
                        root = WildLogPaths.WILDLOG_EXPORT_MAPS_PNG.getAbsoluteFullPath();
                    }
                    Path filePath = root.resolve(name + " (" + System.currentTimeMillis() + ").png");
                    Files.createDirectories(filePath.getParent());
                    ImageIO.write(bufferedImage, "png", filePath.toFile());
                    UtilsFileProcessing.openFile(filePath);
                    setProgress(100);
                    setMessage("Done Exporting Report Image for '" + name + "'");
                    return null;
                }
            });
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnExportImageActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed
        if (bufferedImage != null) {
            UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                @Override
                protected Object doInBackground() throws Exception {
                    setProgress(0);
                    setMessage("Exporting Report PDF for '" + name + "'");
                    PDDocument doc = null;
                    Path root;
                    if (ExportType.REPORTS.equals(type)) {
                        root = WildLogPaths.WILDLOG_EXPORT_REPORTS_PDF.getAbsoluteFullPath();
                    }
                    else {
                        root = WildLogPaths.WILDLOG_EXPORT_MAPS_PDF.getAbsoluteFullPath();
                    }
                    Path pdfPath = root.resolve(name + " (" + System.currentTimeMillis() + ").pdf");
                    Files.createDirectories(pdfPath.getParent());
                    try {
                        doc = new PDDocument();
                        // Make the PDF the size of the image, no rescaling (it can be done by the user when printing)
                        PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
                        doc.addPage(page);
                        try (PDPageContentStream content = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                            PDImageXObject pdfImage = LosslessFactory.createFromImage(doc, bufferedImage);
                            content.drawImage(pdfImage, 0, 0);
                        }
                    }
                    catch (IOException ex){
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    finally {
                        try {
                            //save and close
                            if (doc != null) {
                                doc.save(pdfPath.toFile());
                                doc.close();
                            }
                        }
                        catch (Exception ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                    UtilsFileProcessing.openFile(pdfPath);
                    setProgress(100);
                    setMessage("Done Exporting Report PDF for '" + name + "'");
                    return null;
                }
            });
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnExportPDFActionPerformed

//    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
//        int original_width = imgSize.width;
//        int original_height = imgSize.height;
//        int bound_width = boundary.width;
//        int bound_height = boundary.height;
//        int new_width = original_width;
//        int new_height = original_height;
//        // first check if we need to scale width
//        if (original_width > bound_width) {
//            // scale width to fit
//            new_width = bound_width;
//            // scale height to maintain aspect ratio
//            new_height = (new_width * original_height) / original_width;
//        }
//        // then check if we need to scale even with the new height
//        if (new_height > bound_height) {
//            // scale height to fit instead
//            new_height = bound_height;
//            // scale width to maintain aspect ratio
//            new_width = (new_height * original_width) / original_height;
//        }
//        return new Dimension(new_width, new_height);
//    }
    
    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        WLOptionPane.showMessageDialog(this, 
                "For best results: "
                        + "\n1) Resize the window containing the report to be as small as possible before printing. "
                        + "\n2) Use landscape orientation. "
                        + "\n3) Use as small a print margin as possible (10mm / 1cm should be fine).", 
                "Print Tip", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
        dispose();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            if (printerJob.showPageSetupDialog(null)) {
                if (printerJob.printPage(node)) {
                    printerJob.endJob();
                }
                else {
                    WLOptionPane.showMessageDialog(this, "The print job failed.", "Print Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                WLOptionPane.showMessageDialog(this, "Could not setup the printer.", "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            WLOptionPane.showMessageDialog(this, "No printer was found.", "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportHTMLActionPerformed
        UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                setProgress(0);
                setMessage("Exporting Report HTML for '" + name + "'");
                // Create the image
                Path rootImage;
                if (ExportType.REPORTS.equals(type)) {
                    rootImage = WildLogPaths.WILDLOG_EXPORT_REPORTS_HTML_IMAGES.getAbsoluteFullPath();
                }
                else {
                    rootImage = WildLogPaths.WILDLOG_EXPORT_MAPS_HTML_IMAGES.getAbsoluteFullPath();
                }
                Path imagePath = rootImage.resolve(name + " (" + System.currentTimeMillis() + ").png");
                Files.createDirectories(imagePath.getParent());
                ImageIO.write(bufferedImage, "png", imagePath.toFile());
                // Create the HTML content
                Path rootHtml;
                if (ExportType.REPORTS.equals(type)) {
                    rootHtml = WildLogPaths.WILDLOG_EXPORT_REPORTS_HTML.getAbsoluteFullPath();
                }
                else {
                    rootHtml = WildLogPaths.WILDLOG_EXPORT_MAPS_HTML.getAbsoluteFullPath();
                }
                Path filePath = rootHtml.resolve(name + " (" + System.currentTimeMillis() + ").html");
                Files.createDirectories(filePath.getParent());
                final StringBuilder html = new StringBuilder(5000);
                html.append("<html><head><title>").append(name).append("</title></head><body style='font-family:sans-serif;'>");
                html.append("<H1 align='center'>").append(name).append("</H1><hr/>");
                html.append("<table border='0' width='100%'><tr><td style=\"text-align: center; vertical-align: middle;\">");
                html.append("<img src='").append(filePath.relativize(imagePath).toString().replace("..", ".")).append("' />");
                html.append("</tr></td></table>");
                html.append("<br/><hr/><br/>");
                html.append("<table border='1' width='100%'>");
                html.append("<tr>");
                html.append("<td><b>ID</b></td>");
                html.append("<td><b>Date</b></td>");
                html.append("<td><b>Creature</b></td>");
                html.append("<td><b>Place</b></td>");
                html.append("<td><b>Period</b></td>");
                html.append("</tr>");
                for (Sighting inSighting : lstSightings) {
                    html.append("<tr>");
                    html.append("<td>").append(inSighting.getSightingCounter()).append("</td>");
                    html.append("<td>").append(UtilsHTML.formatDateAsString(inSighting.getDate(), true)).append("</td>");
                    html.append("<td>").append(inSighting.getElementName()).append("</td>");
                    html.append("<td>").append(inSighting.getLocationName()).append("</td>");
                    html.append("<td>").append(inSighting.getVisitName()).append("</td>");
                    html.append("</tr>");
                }
                html.append("</table border='1'>");
                html.append("</body></html>");
                UtilsFileProcessing.createFileFromBytes(html.toString().getBytes(), filePath);
                // Open the file
                UtilsFileProcessing.openFile(filePath);
                setProgress(100);
                setMessage("Done Exporting Report HTML for '" + name + "'");
                return null;
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnExportHTMLActionPerformed

    private void btnExportKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportKMLActionPerformed
        if (lstSightings != null && !lstSightings.isEmpty()) {
            UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), new ProgressbarTask(WildLogApp.getApplication()) {
                @Override
                protected Object doInBackground() throws Exception {
                    UtilsKML.exportKML(null, lstSightings, Sighting.WILDLOG_FOLDER_PREFIX, "Observations", this, WildLogApp.getApplication(), true);
                    return null;
                }
            });
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnExportKMLActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportCSV;
    private javax.swing.JButton btnExportHTML;
    private javax.swing.JButton btnExportImage;
    private javax.swing.JButton btnExportKML;
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnPrint;
    // End of variables declaration//GEN-END:variables
}
