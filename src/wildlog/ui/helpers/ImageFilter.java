package wildlog.ui.helpers;

import java.io.File;
import javax.swing.filechooser.*;
import wildlog.utils.UtilsFileProcessing;

public class ImageFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = UtilsFileProcessing.getExtension(f);
        if (extension != null) {
            if (extension.equalsIgnoreCase(UtilsFileProcessing.gif) ||
                extension.equalsIgnoreCase(UtilsFileProcessing.jpeg) ||
                extension.equalsIgnoreCase(UtilsFileProcessing.jpg) ||
                extension.equalsIgnoreCase(UtilsFileProcessing.png)) {
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Images";
    }
}
