package wildlog.utils;

import wildlog.data.dataobjects.wrappers.WildLogSystemFile;
import wildlog.data.enums.WildLogFileType;

public enum WildLogSystemImages {
    BROKEN_FILES(new WildLogSystemFile(0, "WildLog-BrokenFile", "BrokenFile.png", "resources/icons/BrokenFile.png", WildLogFileType.IMAGE)),
    NO_FILES(new WildLogSystemFile(0, "WildLog-NoFile", "NoFile.png", "resources/icons/NoFile.png", WildLogFileType.IMAGE)),
    MOVIES(new WildLogSystemFile(0, "WildLog-Movie", "Movie.png", "resources/icons/Movie.png", WildLogFileType.IMAGE)),
    OTHER_FILES(new WildLogSystemFile(0, "WildLog-OtherFile", "OtherFile.png", "resources/icons/OtherFile.png", WildLogFileType.IMAGE));

    private WildLogSystemFile wildLogFile;

    private WildLogSystemImages(WildLogSystemFile inWildLogFile) {
        wildLogFile = inWildLogFile;
    }

    public WildLogSystemFile getWildLogFile() {
        return wildLogFile;
    }

}
