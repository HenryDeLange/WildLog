package wildlog.ui.helpers.filters;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import wildlog.utils.UtilsFileProcessing;

public class GpxFilter extends FileFilter {

    @Override
    public boolean accept(File inFile) {
        if (inFile.isDirectory()) {
            return true;
        }
        String extension = UtilsFileProcessing.getExtension(inFile);
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
