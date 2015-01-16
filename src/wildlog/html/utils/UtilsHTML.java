package wildlog.html.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsHTML {
    private static final DateTimeFormatter dateFormatWithTime = DateTimeFormatter.ofPattern("E, dd MMM yyyy (hh:mm a)");
    private static final DateTimeFormatter dateFormatWithoutTime = DateTimeFormatter.ofPattern("E, dd MMM yyyy");

    private UtilsHTML() {
    }

    //fixme: probleme as files ' in het
    public static String generateHTMLImages(WildLogFile inWildLogFile, UtilsHTMLExportTypes inExportType) {
        Path fromFile = inWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
        Path toFileAsRelativePath;
        if (UtilsHTMLExportTypes.ForHTML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else
        if (UtilsHTMLExportTypes.ForFancyHTML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else
        if (UtilsHTMLExportTypes.ForKML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_KML.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else {
            toFileAsRelativePath = inWildLogFile.getRelativePath();
        }
        // Generate HTML segment
        if (inExportType.equals(UtilsHTMLExportTypes.ForHTML)) {
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForFancyHTML)) {
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForKML)) {
            // Gebruik toLowerCase() want Google Earth herken nie die filenaam as 'n image as dit met hoofletter JPG eindig nie
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForMap)) {
            // Gebruik URI hier om in Windows en Linux reg te werk
            return "<img src=\"" + inWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL).toUri().toString() + "\"/>  ";
        }
        return "[image error]";
    }

    public static Path exportHTML(DataObjectWithHTML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the HTML Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".html");
        UtilsFileProcessing.createFileFromBytes(inDataObject.toHTML(true, true, inApp, UtilsHTMLExportTypes.ForHTML, inProgressbarTask).getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the HTML Export for '" + inDataObject.getDisplayName() + "' ");
        }
        return toFile;
    }

    public static String formatObjectAsString(Object inObject) {
        if (inObject == null) {
            return "";
        }
        else {
            return inObject.toString();
        }
    }

    public static String formatDateAsString(Date inDate, boolean inShowTime) {
        if (inDate != null) {
            if (inShowTime) {
                return dateFormatWithTime.format(UtilsTime.getLocalDateTimeFromDate(inDate));
            }
            else {
                return dateFormatWithoutTime.format(UtilsTime.getLocalDateFromDate(inDate));
            }
        }
        else {
            return "";
        }
    }

    public static void appendIfNotNullNorEmpty(StringBuilder inStringBuilder, String inKey, Object inValue, boolean... inAddBreakLine) {
        if (inValue != null) {
            String temp = UtilsHTML.formatObjectAsString(inValue);
            if (!temp.isEmpty()) {
                if (((inValue instanceof Enum) && "NONE".equalsIgnoreCase(temp)) || ((inValue instanceof List) && "[]".equalsIgnoreCase(temp))) {
                    // Don't print anything
                }
                else {
                    inStringBuilder.append(inKey).append(temp);
                    if (inAddBreakLine != null && inAddBreakLine.length == 1 && inAddBreakLine[0] == true) {
                        inStringBuilder.append("<br/>");
                    }
                }
            }
        }
    }


    public static void copyFancyHtmlResources(Path inDestinationPath) {
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/accordian.css"), inDestinationPath.resolve("accordian.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/accordian.js"), inDestinationPath.resolve("accordian.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/gradient_elements.css"), inDestinationPath.resolve("gradient_elements.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/gradient_locations.css"), inDestinationPath.resolve("gradient_locations.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/gradient_visits.css"), inDestinationPath.resolve("gradient_visits.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/lightbox.css"), inDestinationPath.resolve("lightbox.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/lightbox.js"), inDestinationPath.resolve("lightbox.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/maps.css"), inDestinationPath.resolve("maps.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/maps.js"), inDestinationPath.resolve("maps.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/sliderman.css"), inDestinationPath.resolve("sliderman.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/sliderman.js"), inDestinationPath.resolve("sliderman.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/bg.png"), inDestinationPath.resolve("bg.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/bullet.png"), inDestinationPath.resolve("bullet.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/bullet_active.png"), inDestinationPath.resolve("bullet_active.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/clear.gif"), inDestinationPath.resolve("clear.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/close.gif"), inDestinationPath.resolve("close.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/left.png"), inDestinationPath.resolve("left.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/loading.gif"), inDestinationPath.resolve("loading.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/overlay.png"), inDestinationPath.resolve("overlay.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("resources/html/images/right.png"), inDestinationPath.resolve("right.png"));
    }

    public static Path exportFancyHTML(DataObjectWithHTML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the HTML (Advanced) Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".html");
        UtilsFileProcessing.createFileFromBytes(inDataObject.toFancyHTML(inApp, inProgressbarTask).getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the HTML (Advanced) Export for '" + inDataObject.getDisplayName() + "' ");
        }
        return toFile;
    }

    public static final String FANCY_HTML_TEMPLATE =
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "<title>___INSERT_TITLE___</title>\n" +
            "<!-- Gradient background -->\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Scripts/___INSERT_GRADIENT_CSS___.css\" />\n" +
            "<!-- Accordian -->\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Scripts/accordian.css\" />\n" +
            "<script type=\"text/javascript\" src=\"../Scripts/accordian.js\"></script>\n" +
            "<!-- Sliderman -->\n" +
            "<script type=\"text/javascript\" src=\"../Scripts/sliderman.js\"></script>\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Scripts/sliderman.css\" />\n" +
            "<!-- Lightbox -->\n" +
            "<script type=\"text/javascript\" src=\"../Scripts/lightbox.js\"></script>\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Scripts/lightbox.css\" />\n" +
            "<!-- Maps -->\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Scripts/maps.css\" />\n" +
            "<script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=visualization\"></script> <!-- note the extra param in the URL for the heatmap -->\n" +
            "<script type=\"text/javascript\" src=\"../Scripts/maps.js\"></script>\n"
            + "___INSERT_SCRIPT_TO_LOAD_MAP_POINTS___ \n" +
            "</head>" +
            "<body onload=\"initAccordian();\"> <!-- initLightbox(); done in modified Sliderman.js -->\n" +
            "	<br/>\n" +
            "	<table style=\"margin-left:auto; margin-right:auto; width:850px; font:15px arial,sans-serif;\" border=\"1\">\n" +
            "		<tr style=\"width:850px\" align=\"center\">\n" +
            "			<td colspan=\"2\" >\n" +
            "				<h1>___INSERT_TABLE_HEADER___</h1>\n" +
            "			</td>\n" +
            "		</tr>\n" +
            "		<tr>\n" +
            "			<td>\n" +
            "				<div id=\"wrapper\">\n" +
            "					<div id=\"examples_outer\">\n" +
            "						<div id=\"slider_container_2\">\n" +
            "							<div id=\"SliderName_2\"  class=\"SliderName_2\">\n" +
            "								___INSERT_IMAGES___ \n" +
            "							</div>" +
            "                                                   <div class=\"c\"></div>\n" +
            "							<div id=\"SliderNameNavigation_2\"></div>\n" +
            "							<script type=\"text/javascript\">\n" +
            "								var demoSlider_2 = Sliderman.slider({container: 'SliderName_2', \n" +
            "										width: 300, \n" +
            "										height: 300, \n" +
            "										effects: 'fade',\n" +
            "									display: {\n" +
            "										autoplay: 7000,\n" +
            "										loading: {background: '#000000', opacity: 0.5, image: '../Scripts/loading.gif'},\n" +
            "										buttons: {hide: true, opacity: 1, prev: {className: 'SliderNamePrev_2', label: ''}, next: {className: 'SliderNameNext_2', label: ''}},\n" +
            "										description: {hide: true, background: '#000000', opacity: 0.4, height: 35, position: 'bottom'},navigation: {container: 'SliderNameNavigation_2', label: '<img src=\"../Scripts/clear.gif\" />'}\n" +
            "									}\n" +
            "								});\n" +
            "							</script>\n" +
            "						</div>\n" +
            "					</div>\n" +
            "				</div>\n" +
            "			</td>\n" +
            "			<td align=\"center\">\n" +
            "				<div id=\"map-canvas\"></div>\n" +
            "			</td>\n" +
            "		</tr>" +
            "           <tr>\n" +
            "			<td colspan=\"2\" >\n" +
            "				<div class=\"accordionItem\">\n" +
            "					<h2>Full Information</h2>\n" +
            "					<div>\n" +
            "						___INSERT_DETAILS___ \n" +
            "					</div>\n" +
            "				</div>\n" +
            "				<div class=\"accordionItem\">\n" +
            "					<h2>Observations</h2>\n" +
            "					<div>\n" +
            "						___INSERT_SIGHTINGS___ \n" +
            "					</div>\n" +
            "				</div>\n" +
            "			</td>\n" +
            "		</tr>\n" +
            "	</table>\n" +
            "</body>\n" +
            "</html>";

    public static final String FANCY_HTML_TEMPLATE_NESTED =
            "	<div class=\"accordionItem\">\n" +
            "       <h2>___INSERT_NESTED_HEADER___</h2>\n" +
            "       <div>\n" +
            "		 ___INSERT_NESTED_CONTENT___ \n" +
            "       </div>\n" +
            "	</div>";

}