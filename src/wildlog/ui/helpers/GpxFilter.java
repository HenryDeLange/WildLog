package wildlog.ui.helpers;

import java.io.File;
import javax.swing.filechooser.*;
import wildlog.utils.UtilsFileProcessing;

public class GpxFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = UtilsFileProcessing.getExtension(f);
        if (extension != null) {
            if (extension.equalsIgnoreCase("gpx")) {
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "GPX";
    }
}
