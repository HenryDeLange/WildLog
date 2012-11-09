package wildlog.utils.ui;

import java.io.File;
import javax.swing.filechooser.*;

public class GpxFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
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
