package wildlog.movies.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.movies.jpegmovie.JpgToMovie;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogPaths;


public class UtilsMovies {

    private UtilsMovies() {
    }

    /**
     * Kry 'n lys van paths wat ooreenstem met die WildLogFile ID.
     * @param inApp
     * @param inWildLogFile
     * @return List<String> - Sal nie null wees nie.
     */
    public static List<String> getFilePaths(WildLogApp inApp, WildLogFile inWildLogFile) {
        List<WildLogFile> files = inApp.getDBI().list(inWildLogFile);
        List<String> slideshowList = new ArrayList<String>(files.size());
        for (WildLogFile tempFile : files) {
            if (WildLogFileType.IMAGE.equals(tempFile.getFileType())) {
                // Only using JPGs because otherwise it might break the video
                if (WildLogFileExtentions.Images.isJPG(tempFile.getAbsolutePath())) {
                    slideshowList.add(tempFile.getAbsolutePath().toString());
                }
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
            ex.printStackTrace(System.err);
        }
        // Now create the slideshow
        JpgToMovie jpgToMovie = new JpgToMovie();
        if (inFilePaths.size() > 0) {
            Path finalPath = Paths.get(File.separator).resolve(inOutputFilename.toAbsolutePath().getRoot().relativize(
                    inOutputFilename.toAbsolutePath())).normalize();
            // FIXME: ek dink ek moet die drive letter en dubbel punt strip van die path voor die storie sal werk...
            if (jpgToMovie.createMovieFromJpgs(
                    inApp.getWildLogOptions().getDefaultSlideshowSize(),
                    inApp.getWildLogOptions().getDefaultSlideshowSpeed(),
                    inFilePaths, finalPath.toString())) {
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
            // FIXME: Die gekleurde background wys nie vir die dialogs nie want die SLideshowDialog popup verwyder dit wanneer dit dispose word. Dis tricky om te fix want die storie gebreur in verskillende threads ook nog...
            UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Can't generate Slideshow if there aren't any JPG images.",
                            "No Images to Process", JOptionPane.INFORMATION_MESSAGE);
                    return -1;
                }
            });
        }
    }

}
