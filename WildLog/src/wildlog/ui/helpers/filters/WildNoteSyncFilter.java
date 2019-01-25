package wildlog.ui.helpers.filters;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import javax.swing.filechooser.FileFilter;
import wildlog.data.utils.WildLogConstants;

public class WildNoteSyncFilter extends FileFilter implements FilenameFilter {

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
        return "WildNote Sync";
    }

    @Override
    public boolean accept(File inFileDirectory, String inName) {
        return (WildLogConstants.WILDNOTE_SYNC_DATABASE + ".mv.db").equalsIgnoreCase(inName)
                || (WildLogConstants.WILDNOTE_SYNC_DATABASE + ".h2.db").equalsIgnoreCase(inName);
    }

}
