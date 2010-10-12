package wildlog.utils.ui;

import java.io.File;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equalsIgnoreCase(Utils.gif) ||
                extension.equalsIgnoreCase(Utils.jpeg) ||
                extension.equalsIgnoreCase(Utils.jpg) ||
                extension.equalsIgnoreCase(Utils.png)) {
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
