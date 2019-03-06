package wildlog.html.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;


public final class UtilsHTML {

    
    private UtilsHTML() {
    }

    
    public static String generateHTMLImages(WildLogFile inWildLogFile, UtilsHTMLExportTypes inExportType) {
        Path fromFile = inWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
        Path toFileAsRelativePath;
        if (UtilsHTMLExportTypes.ForHTML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_BASIC_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML_BASIC.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else
        if (UtilsHTMLExportTypes.ForKML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
        if (inExportType.equals(UtilsHTMLExportTypes.ForKML)) {
            // Gebruik toLowerCase() want Google Earth herken nie die filenaam as 'n image as dit met hoofletter JPG eindig nie
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForMap)) {
            // Gebruik URI hier om in Windows en Linux reg te werk
            return "<img src='" + inWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL).toUri().toString() + "'/>  ";
        }
        return "[image error]";
    }

    public static Path exportHTML(DataObjectWithHTML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the HTML Basic Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML_BASIC.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".html");
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(1);
            inProgressbarTask.setMessage("Busy with the HTML Basic Export for '" + inDataObject.getDisplayName() + "' ");
        }
        UtilsFileProcessing.createFileFromBytes(inDataObject.toHTML(true, true, false, inApp, UtilsHTMLExportTypes.ForHTML, inProgressbarTask).getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the HTML Basic Export for '" + inDataObject.getDisplayName() + "' ");
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
                return UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS_AMPM.format(UtilsTime.getLocalDateTimeFromDate(inDate));
            }
            else {
                return UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateFromDate(inDate));
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
        // BXSLider
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/jquery.bxslider.min.css"), inDestinationPath.resolve("bxSlider/jquery.bxslider.min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/jquery.bxslider.min.js"), inDestinationPath.resolve("bxSlider/jquery.bxslider.min.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/images/bx_loader.gif"), inDestinationPath.resolve("bxSlider/images/bx_loader.gif"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/images/controls.png"), inDestinationPath.resolve("bxSlider/images/controls.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/vendor/jquery.easing.1.3.js"), inDestinationPath.resolve("bxSlider/vendor/jquery.easing.1.3.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/bxSlider/vendor/jquery.fitvids.js"), inDestinationPath.resolve("bxSlider/vendor/jquery.fitvids.js"));
        // FontAwesome
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/css/font-awesome.min.css"), inDestinationPath.resolve("font-awesome/css/font-awesome.min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/FontAwesome.otf"), inDestinationPath.resolve("font-awesome/fonts/FontAwesome.otf"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/fontawesome-webfont.eot"), inDestinationPath.resolve("font-awesome/fonts/fontawesome-webfont.eot"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/fontawesome-webfont.svg"), inDestinationPath.resolve("font-awesome/fonts/fontawesome-webfont.svg"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/fontawesome-webfont.ttf"), inDestinationPath.resolve("font-awesome/fonts/fontawesome-webfont.ttf"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/fontawesome-webfont.woff"), inDestinationPath.resolve("font-awesome/fonts/fontawesome-webfont.woff"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/fonts/fontawesome-webfont.woff2"), inDestinationPath.resolve("font-awesome/fonts/fontawesome-webfont.woff2"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/animated.less"), inDestinationPath.resolve("font-awesome/less/animated.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/bordered-pulled.less"), inDestinationPath.resolve("font-awesome/less/bordered-pulled.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/core.less"), inDestinationPath.resolve("font-awesome/less/core.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/fixed-width.less"), inDestinationPath.resolve("font-awesome/less/fixed-width.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/font-awesome.less"), inDestinationPath.resolve("font-awesome/less/font-awesome.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/icons.less"), inDestinationPath.resolve("font-awesome/less/icons.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/larger.less"), inDestinationPath.resolve("font-awesome/less/larger.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/list.less"), inDestinationPath.resolve("font-awesome/less/list.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/mixins.less"), inDestinationPath.resolve("font-awesome/less/mixins.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/path.less"), inDestinationPath.resolve("font-awesome/less/path.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/rotated-flipped.less"), inDestinationPath.resolve("font-awesome/less/rotated-flipped.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/stacked.less"), inDestinationPath.resolve("font-awesome/less/stacked.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/less/variables.less"), inDestinationPath.resolve("font-awesome/less/variables.less"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_animated.scss"), inDestinationPath.resolve("font-awesome/scss/_animated.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_bordered-pulled.scss"), inDestinationPath.resolve("font-awesome/scss/_bordered-pulled.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_core.scss"), inDestinationPath.resolve("font-awesome/scss/_core.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_fixed-width.scss"), inDestinationPath.resolve("font-awesome/scss/_fixed-width.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_icons.scss"), inDestinationPath.resolve("font-awesome/scss/_icons.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_larger.scss"), inDestinationPath.resolve("font-awesome/scss/_larger.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_list.scss"), inDestinationPath.resolve("font-awesome/scss/_list.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_mixins.scss"), inDestinationPath.resolve("font-awesome/scss/_mixins.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_path.scss"), inDestinationPath.resolve("font-awesome/scss/_path.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_rotated-flipped.scss"), inDestinationPath.resolve("font-awesome/scss/_rotated-flipped.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_stacked.scss"), inDestinationPath.resolve("font-awesome/scss/_stacked.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_variables.scss"), inDestinationPath.resolve("font-awesome/scss/_variables.scss"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/font-awesome/scss/_variables.scss"), inDestinationPath.resolve("font-awesome/scss/_variables.scss"));
        // Gradient
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/gradient/gradient.css"), inDestinationPath.resolve("gradient/gradient.css"));
        // JQUery
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/jquery/jquery-1.11.3.min.js"), inDestinationPath.resolve("jquery/jquery-1.11.3.min.js"));
        // JQuery UI
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/jqueryui/jquery-ui.min.css"), inDestinationPath.resolve("jqueryui/jquery-ui.min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/jqueryui/jquery-ui.min.js"), inDestinationPath.resolve("jqueryui/jquery-ui.min.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/jqueryui/jquery-ui.structure.min.css"), inDestinationPath.resolve("jqueryui/jquery-ui.structure.min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/jqueryui/jquery-ui.theme.min.css"), inDestinationPath.resolve("jqueryui/jquery-ui.theme.min.css"));
        // Prime UI
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/primeui-2.0-min.css"), inDestinationPath.resolve("primeui/primeui-2.0-min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/primeui-2.0-min.js"), inDestinationPath.resolve("primeui/primeui-2.0-min.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/css/core/core.css"), inDestinationPath.resolve("primeui/css/core/core.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/css/fieldset/fieldset.css"), inDestinationPath.resolve("primeui/css/fieldset/fieldset.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/js/core/core.js"), inDestinationPath.resolve("primeui/js/core/core.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/js/fieldset/fieldset.js"), inDestinationPath.resolve("primeui/js/fieldset/fieldset.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/plugins/cursorposition.js"), inDestinationPath.resolve("primeui/plugins/cursorposition.js"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/primeui/plugins/rangyinput.js"), inDestinationPath.resolve("primeui/plugins/rangyinput.js"));
        // Theme
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/jquery-ui.css"), inDestinationPath.resolve("theme/smoothness/jquery-ui.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/jquery-ui.min.css"), inDestinationPath.resolve("theme/smoothness/jquery-ui.min.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/theme.css"), inDestinationPath.resolve("theme/smoothness/theme.css"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_flat_0_aaaaaa_40x100.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_flat_0_aaaaaa_40x100.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_flat_75_ffffff_40x100.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_flat_75_ffffff_40x100.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_glass_55_fbf9ee_1x400.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_glass_55_fbf9ee_1x400.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_glass_65_ffffff_1x400.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_glass_65_ffffff_1x400.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_glass_75_dadada_1x400.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_glass_75_dadada_1x400.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_glass_75_e6e6e6_1x400.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_glass_75_e6e6e6_1x400.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_glass_95_fef1ec_1x400.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_glass_95_fef1ec_1x400.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-bg_highlight-soft_75_cccccc_1x100.png"), inDestinationPath.resolve("theme/smoothness/images/ui-bg_highlight-soft_75_cccccc_1x100.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-icons_2e83ff_256x240.png"), inDestinationPath.resolve("theme/smoothness/images/ui-icons_2e83ff_256x240.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-icons_222222_256x240.png"), inDestinationPath.resolve("theme/smoothness/images/ui-icons_222222_256x240.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-icons_454545_256x240.png"), inDestinationPath.resolve("theme/smoothness/images/ui-icons_454545_256x240.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-icons_888888_256x240.png"), inDestinationPath.resolve("theme/smoothness/images/ui-icons_888888_256x240.png"));
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/theme/smoothness/images/ui-icons_cd0a0a_256x240.png"), inDestinationPath.resolve("theme/smoothness/images/ui-icons_cd0a0a_256x240.png"));
        // OverlappingMarkerSpiderfier
        UtilsFileProcessing.createFileFromStream(WildLogApp.class.getResourceAsStream("html/fancy/Scripts/markerspider/oms.min.js"), inDestinationPath.resolve("markerspider/oms.min.js"));
    }

    public static Path exportFancyHTML(DataObjectWithHTML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the HTML Advanced Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        // Copy the scripts and stylesheets
        UtilsHTML.copyFancyHtmlResources(WildLogPaths.WILDLOG_EXPORT_HTML_FANCY_RESOURCES.getAbsoluteFullPath());
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(3);
            inProgressbarTask.setMessage("Busy with the HTML Advanced Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
        }
        // Get the template file
        final char[] buffer = new char[4096];
        final StringBuilder builder = new StringBuilder(7500);
        try (Reader in = new InputStreamReader(WildLogApp.class.getResourceAsStream("html/fancy/Template/wildlog.html"), "UTF-8")) {
            int length = 0;
            while (length >= 0) {
                length = in.read(buffer, 0, buffer.length);
                if (length > 0) {
                    builder.append(buffer, 0, length);
                }
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Replace the placeholders in the template with actual content
        template = template.replace("___THE_TITLE___", inDataObject.getDisplayName());
        template = template.replace("___THE_HEADING___", inDataObject.getDisplayName());
        template = template.replace("___INFORMATION_CONTENT___", inDataObject.toHTML(false, false, false, inApp, UtilsHTMLExportTypes.ForHTML, null));
        // Copy the thumbnails and setup the main slideshow and image list
        int mainSliderBeginIndex = template.indexOf("___MAIN_SLIDER_START___") + "___MAIN_SLIDER_START___".length();
        int mainSliderEndIndex = template.indexOf("___MAIN_SLIDER_END___");
        String mainSliderTemplate = template.substring(mainSliderBeginIndex, mainSliderEndIndex).trim();
        StringBuilder mainSlider = new StringBuilder(100);
        int imageListBeginIndex = template.indexOf("___MAIN_LIGHTBOX_START___") + "___MAIN_LIGHTBOX_START___".length();
        int imageListEndIndex = template.indexOf("___MAIN_LIGHTBOX_END___");
        String imageListTemplate = template.substring(imageListBeginIndex, imageListEndIndex).trim();
        StringBuilder imageList = new StringBuilder(200);
        List<WildLogFile> lstFiles = inApp.getDBI().listWildLogFiles(inDataObject.getWildLogFileID(), null, WildLogFile.class);
        for (int t = 0; t < lstFiles.size(); t++) {
            WildLogFile wildLogFile = lstFiles.get(t);
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS.getAbsoluteFullPath().resolve(wildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            Path fromFile = wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, true, true);
            // Get relative path
            Path thumbnailAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath().relativize(thumbnailPath);
            // Add to Main Slider
            if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                mainSlider.append(mainSliderTemplate.replace("ZZZ1-alt", wildLogFile.getId())
                                                    .replace("ZZZ1-title", wildLogFile.getFilename())
                                                    .replace("href=\"#bigImgZZZ1\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                    .replace("src=\"./ZZZ1.jpg\"", "src=\"../" + thumbnailAsRelativePath.toString() + "\""));
            }
            else {
                mainSlider.append(mainSliderTemplate.replace("ZZZ1-alt", wildLogFile.getId())
                                                    .replace("ZZZ1-title", wildLogFile.getFilename())
                                                    .replace("href=\"#bigImgZZZ1\"", "href=\"" + wildLogFile.getAbsolutePath() + "\"")
                                                    .replace("src=\"./ZZZ1.jpg\"", "src=\"../" + thumbnailAsRelativePath.toString() + "\""));
            }
            mainSlider.append(System.lineSeparator());
            // Add to full image list
            if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                imageList.append(imageListTemplate.replace("ZZZ1-alt1", wildLogFile.getId())
                                                  .replace("ZZZ1-alt2", wildLogFile.getId())
                                                  .replace("ZZZ1-title1", wildLogFile.getFilename())
                                                  .replace("ZZZ1-title2", wildLogFile.getFilename())
                                                  .replace("id=\"smallImgZZZ1\"", "id=\"smallImg" + wildLogFile.getDBFilePath() + "\"")
                                                  .replace("href=\"#bigImgZZZ1\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                  .replace("id=\"bigImgZZZ1\"", "id=\"bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                  .replace("href=\"#bigImgZZZ1a\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                  .replace("href=\"#bigImgZZZ1b\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                  .replace("src=\"./ZZZ1.jpg1\"", "src=\"../" + thumbnailAsRelativePath.toString() + "\"")
                                                  .replace("src=\"./ZZZ1.jpg2\"", "src=\"" + wildLogFile.getAbsolutePath() + "\""));
                imageList.append(System.lineSeparator());
            }
            // Update progress
            if (inProgressbarTask != null) {
                inProgressbarTask.setTaskProgress((3 + (int)((double)t/(double)lstFiles.size()*22.0)));
                inProgressbarTask.setMessage("Busy with the HTML Advanced Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
            }
        }
        // Set the HTML of the MainSlider
        template = template.replace("___MAIN_SLIDER_START___", "");
        template = template.replace("___MAIN_SLIDER_END___", "");
        template = template.replace(mainSliderTemplate, mainSlider.toString());
        // Set the HTML of the Image List Lightbox
        template = template.replace("___MAIN_LIGHTBOX_START___", "");
        template = template.replace("___MAIN_LIGHTBOX_END___", "");
        template = template.replace(imageListTemplate, imageList.toString());
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(25);
            inProgressbarTask.setMessage("Busy with the HTML Advanced Export for '" + inDataObject.getDisplayName() + "' " + inProgressbarTask.getProgress() + "%");
        }
        // Setup the Map and Related data
        List<Sighting> lstSightings;
        if (inDataObject instanceof Element) {
            lstSightings = inApp.getDBI().listSightings(0, inDataObject.getIDField(), null, null, false, Sighting.class);
        }
        else
        if (inDataObject instanceof Location) {
            lstSightings = inApp.getDBI().listSightings(0, null, inDataObject.getIDField(), null, false, Sighting.class);
        }
        else
        if (inDataObject instanceof Visit) {
            lstSightings = inApp.getDBI().listSightings(0, null, null, inDataObject.getIDField(), false, Sighting.class);
        }
        else {
            lstSightings = new ArrayList<>(3);
        }
        List<DataObjectWithHTML> lstRelatedData = new ArrayList<>(lstSightings);
        if (inDataObject instanceof Sighting) {
            lstRelatedData.add(inApp.getDBI().findLocation(((Sighting) inDataObject).getLocationName(), Location.class));
            lstRelatedData.add(inApp.getDBI().findVisit(((Sighting) inDataObject).getVisitName(), Visit.class));
            lstRelatedData.add(inApp.getDBI().findElement(((Sighting) inDataObject).getElementName(), Element.class));
        }
        int counter = 0;
        int mapBeginIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_START___") + "//___MAP_CLICKABLE_DATA_POINTS_START___".length();
        int mapEndIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_END___");
        String mapTemplate = template.substring(mapBeginIndex, mapEndIndex).trim();
        StringBuilder mapBuilder = new StringBuilder(1000 * lstRelatedData.size());
        int relatedBeginIndex = template.indexOf("<!--___REPEAT_RELATED_RECORDS_START___-->") + "<!--___REPEAT_RELATED_RECORDS_START___-->".length();
        int relatedEndIndex = template.indexOf("<!--___REPEAT_RELATED_RECORDS_END___-->");
        String relatedTemplate = template.substring(relatedBeginIndex, relatedEndIndex).trim();
        StringBuilder relatedBuilder = new StringBuilder(1500 * lstRelatedData.size());
        int sliderBeginIndex = template.indexOf("___SUB_SLIDER_START___") + "___SUB_SLIDER_START___".length();
        int sliderEndIndex = template.indexOf("___SUB_SLIDER_END___");
        String sliderTemplate = template.substring(sliderBeginIndex, sliderEndIndex).trim();
        int lightboxBeginIndex = template.indexOf("___SUB_LIGHTBOX_START___") + "___SUB_LIGHTBOX_START___".length();
        int lightboxEndIndex = template.indexOf("___SUB_LIGHTBOX_END___");
        String lightboxTemplate = template.substring(lightboxBeginIndex, lightboxEndIndex).trim();
        int subFieldBeginIndex = template.indexOf("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___") + "//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___".length();
        int subFieldEndIndex = template.indexOf("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_END___");
        String subFieldTemplate = template.substring(subFieldBeginIndex, subFieldEndIndex).trim();
        StringBuilder subFieldBuilder = new StringBuilder(200 * lstRelatedData.size());
        int sliderScriptBeginIndex = template.indexOf("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___") + "//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___".length();
        int sliderScriptEndIndex = template.indexOf("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_END___");
        String sliderScriptTemplate = template.substring(sliderScriptBeginIndex, sliderScriptEndIndex).trim();
        StringBuilder sliderScriptBuilder = new StringBuilder(250 * lstRelatedData.size());
        for (DataObjectWithHTML relatedData : lstRelatedData) {
            // JavaScript for related record fields
            subFieldBuilder.append(subFieldTemplate.replace("relatedRecordsFieldBoxZZZ", "relatedRecordsFieldBox" + relatedData.getIDField()));
            subFieldBuilder.append(System.lineSeparator());
            // JavaScript for related record sliders
            sliderScriptBuilder.append(sliderScriptTemplate.replace("subSliderZZZ", "subSlider" + relatedData.getIDField()));
            sliderScriptBuilder.append(System.lineSeparator());
            // Mapping Points
            if (relatedData instanceof DataObjectWithGPS) {
                if (UtilsGPS.getLatDecimalDegree((DataObjectWithGPS) relatedData) != 0 && UtilsGPS.getLonDecimalDegree((DataObjectWithGPS) relatedData) != 0) {
                    mapBuilder.append(mapTemplate.replace("var markerZZZ", "var marker" + relatedData.getIDField())
                                                 .replace("LatLng(44.5403, -78.5463)", "LatLng(" + UtilsGPS.getLatDecimalDegree((DataObjectWithGPS) relatedData) + "," + UtilsGPS.getLonDecimalDegree((DataObjectWithGPS) relatedData) + ")")
                                                 .replace("ZZZ-title", relatedData.getDisplayName().replaceAll("\"", "&quot;"))
                                                 .replace("markerZZZ.desc", "marker" + relatedData.getIDField() + ".desc")
                                                 .replace("ZZZ-content", relatedData.toHTML(false, false, true, inApp, UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                                 .replace("oms.addMarker(markerZZZ", "oms.addMarker(marker" + relatedData.getIDField())
                                                 .replace("bounds.extend(markerZZZ", "bounds.extend(marker" + relatedData.getIDField()));
                }
                mapBuilder.append(System.lineSeparator());
            }
            // Observation Info
            List<WildLogFile> lstSightingFiles = inApp.getDBI().listWildLogFiles(relatedData.getWildLogFileID(), null, WildLogFile.class);
            StringBuilder sliderBuilder = new StringBuilder(200 * lstSightingFiles.size());
            StringBuilder lightboxBuilder = new StringBuilder(200 * lstSightingFiles.size());
            if (lstSightingFiles.isEmpty()) {
                lstSightingFiles.add(WildLogSystemImages.NO_FILES.getWildLogFile());
            }
            for (WildLogFile wildLogFile : lstSightingFiles) {
                Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS.getAbsoluteFullPath().resolve(wildLogFile.getRelativePath().getParent());
                try {
                    Files.createDirectories(thumbnailFolder);
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                Path fromFile = wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM);
                Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
                UtilsFileProcessing.copyFile(fromFile, thumbnailPath, true, true);
                Path thumbnailAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath().relativize(thumbnailPath);
                if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                    sliderBuilder.append(sliderTemplate.replace("ZZZ2-alt", wildLogFile.getId())
                                                       .replace("ZZZ2-title", wildLogFile.getFilename())
                                                       .replace("href=\"#bigImgZZZ2\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                       .replace("src=\"./ZZZ2.jpg\"", "src=\"../" + thumbnailAsRelativePath.toString() + "\""));
                }
                else {
                    sliderBuilder.append(sliderTemplate.replace("ZZZ2-alt", wildLogFile.getId())
                                                       .replace("ZZZ2-title", wildLogFile.getFilename())
                                                       .replace("href=\"#bigImgZZZ2\"", "href=\"" + wildLogFile.getAbsolutePath() + "\"")
                                                       .replace("src=\"./ZZZ2.jpg\"", "src=\"../" + thumbnailAsRelativePath.toString() + "\""));
                }
                sliderBuilder.append(System.lineSeparator());
                if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                    lightboxBuilder.append(lightboxTemplate.replace("ZZZ2-alt", wildLogFile.getId())
                                                           .replace("ZZZ2-title", wildLogFile.getFilename())
                                                           .replace("id=\"bigImgZZZ2\"", "id=\"bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                           .replace("src=\"./ZZZ2.jpg\"", "src=\"" + wildLogFile.getAbsolutePath() + "\""));
                    lightboxBuilder.append(System.lineSeparator());
                }
            }
            // Set the HTML for the Slider and LightBox
            relatedBuilder.append(relatedTemplate.replace("subSliderZZZ", "subSlider" + relatedData.getIDField())
                                                 .replace("relatedRecordsFieldBoxZZZ", "relatedRecordsFieldBox" + relatedData.getIDField())
                                                 .replace("___RELATED_RECORD_NAME___", relatedData.getDisplayName())
                                                 .replace("___SUB_SLIDER_START___", "")
                                                 .replace("___SUB_SLIDER_END___", "")
                                                 .replace("___SUB_LIGHTBOX_START___", "")
                                                 .replace("___SUB_LIGHTBOX_END___", "")
                                                 .replace("___RELATED_INFORMATION_CONTENT___", relatedData.toHTML(false, false, false, inApp, UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                                 .replace(sliderTemplate, sliderBuilder.toString())
                                                 .replace(lightboxTemplate, lightboxBuilder.toString()));
            relatedBuilder.append(System.lineSeparator());
            // Update progress
            if (inProgressbarTask != null) {
                inProgressbarTask.setTaskProgress(25 + (int)(((double)counter/lstRelatedData.size())*(74)));
                inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                        + " " + inProgressbarTask.getProgress() + "%");
            }
            counter++;
        }
        // Set the HTML of the Javascript
        template = template.replace("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___", "")
                           .replace("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_END___", "")
                           .replace(subFieldTemplate, subFieldBuilder.toString())
                           .replace("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___", "")
                           .replace("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_END___", "")
                           .replace(sliderScriptTemplate, sliderScriptBuilder.toString());
        // Set the HTML of the Map
        template = template.replace("//___MAP_CLICKABLE_DATA_POINTS_START___", "")
                           .replace("//___MAP_CLICKABLE_DATA_POINTS_END___", "")
                           .replace(mapTemplate, mapBuilder.toString());
        // Set the HTML of the Observations
        template = template.replace("<!--___REPEAT_RELATED_RECORDS_START___-->", "")
                           .replace("<!--___REPEAT_RELATED_RECORDS_END___-->", "")
                           .replace(relatedTemplate, relatedBuilder.toString());
        // Write the final file
        Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".html");
        UtilsFileProcessing.createFileFromBytes(template.getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the HTML Advanced Export for '" + inDataObject.getDisplayName() + "' ");
        }
        return toFile;
    }

}