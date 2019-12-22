package wildlog.movies.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.movies.jpegmovie.JpgToMovie;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;


public final class UtilsMovies {

    private UtilsMovies() {
    }

    /**
     * Kry 'n lys van paths wat ooreenstem met die WildLogFile ID. (Slegs JPGs word gekies.)
     * @param inApp
     * @param inWildLogFileID
     * @param inWildLogFileType
     * @return List<String> - Sal nie null wees nie.
     */
    public static List<String> getFilePaths(WildLogApp inApp, long inWildLogFileID, WildLogFileType inWildLogFileType) {
        List<WildLogFile> files = inApp.getDBI().listWildLogFiles(inWildLogFileID, inWildLogFileType, WildLogFile.class);
        List<String> slideshowList = new ArrayList<String>(files.size());
        for (WildLogFile tempFile : files) {
            // Only using JPGs because otherwise it might break the video
            if (WildLogFileExtentions.Images.isJPG(tempFile.getAbsolutePath())) {
                slideshowList.add(tempFile.getAbsolutePath().toString());
            }
        }
        return slideshowList;
    }

    /**
     * Maak die SlideShow van die lys paths na JPGs wat in gestuur word.
     * Ongelukkig weet ek nie hoelank dit gaan vat nie so die ProgressBar kan nie vordering wys nie.
     * @param inFilePaths
     * @param inApp
     * @param inOutputFilename
     */
    public static void generateSlideshow(List<String> inFilePaths, final WildLogApp inApp, Path inOutputFilename) {
        // Make sure the folder exists
        try {
            Files.createDirectories(WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getAbsoluteFullPath());
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        // Now create the slideshow
        JpgToMovie jpgToMovie = new JpgToMovie();
        if (inFilePaths.size() > 0) {
            if (jpgToMovie.createMovieFromJpgs(
                    inApp.getWildLogOptions().getDefaultSlideshowSize(),
                    inApp.getWildLogOptions().getDefaultSlideshowSpeed(),
                    inFilePaths, inOutputFilename.toAbsolutePath().toString())) {
                // Lastly launch the file
                UtilsFileProcessing.openFile(inOutputFilename);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "There was a problem generating the slideshow.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Can't generate Slideshow if there aren't any JPG images.",
                            "No Images to Process", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

}
