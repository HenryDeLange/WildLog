package wildlog.ui.helpers.filters;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import javax.swing.filechooser.FileFilter;
import wildlog.utils.WildLogFileExtentions;

public class ImageFilter extends FileFilter implements FilenameFilter {

    @Override
    public boolean accept(File inFile) {
        if (inFile.isDirectory()) {
            return true;
        }
        Path path = inFile.toPath();
        return accept(path.getParent().toFile(), path.getFileName().toString());
    }

    @Override
    public String getDescription() {
        return "Images";
    }

    @Override
    public boolean accept(File inFileDirectory, String inName) {
        return WildLogFileExtentions.Images.isKnownExtention(inName.substring(inName.lastIndexOf('.') + 1));
    }

}
