package wildlog.movies.utils;

import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.movies.jpegmovie.JpgToMovie;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class UtilsMovies {
    public static void generateSlideshow(List<String> inList, final WildLogApp inApp, String inOutputFilename) {
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
                UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "There was a problem generating the slideshow.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
            }
        }
        else {
            UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Can't generate slideshow if there aren't any images.",
                            "No Images", JOptionPane.INFORMATION_MESSAGE);
                    return -1;
                }
            });
        }
    }
}
