package wildlog.movies.utils;

import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.movies.jpegmovie.JpgToMovie;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class UtilsMovies {
    public static void generateSlideshow(List<String> inList, WildLogApp inApp, String inOutputFilename) {
        // Now create the slideshow
        File tempFile = new File(WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath());
        tempFile.mkdirs();
        JpgToMovie jpgToMovie = new JpgToMovie();
        if (inList.size() > 0) {
            if (jpgToMovie.createMovieFromJpgs(inApp.getWildLogOptions().getDefaultSlideshowSize(), inApp.getWildLogOptions().getDefaultSlideshowSpeed(), inList, inOutputFilename)) {
                // Lastly launch the file
                UtilsFileProcessing.openFile(inOutputFilename);
            }
            else {
                JOptionPane.showMessageDialog(null, "There was a problem generating the slideshow.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Can't generate slideshow if there aren't any images.", "No Images", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
