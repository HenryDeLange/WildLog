package wildlog.ui.helpers;

import java.io.File;
import javax.swing.filechooser.*;
import wildlog.utils.UtilsFileProcessing;

public class MovieFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = UtilsFileProcessing.getExtension(f);
        if (extension != null) {
            if (extension.equalsIgnoreCase("avi") ||
                extension.equalsIgnoreCase("asf") ||
                extension.equalsIgnoreCase("mpg") ||
                extension.equalsIgnoreCase("mpeg")) {
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Movies";
    }
}
