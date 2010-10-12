package wildlog.utils.ui;

import java.io.File;
import javax.swing.filechooser.*;

public class MovieFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
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
