package wildlog.utils;

import wildlog.data.dataobjects.wrappers.WildLogSystemFile;
import wildlog.data.enums.WildLogFileLinkType;
import wildlog.data.enums.WildLogFileType;

public enum WildLogSystemImages {
    BROKEN_FILES(new WildLogSystemFile(0, 0, WildLogFileLinkType.NONE, "BrokenFile.png", "resources/icons/BrokenFile.png", WildLogFileType.IMAGE)),
    NO_FILES(new WildLogSystemFile(0, 0, WildLogFileLinkType.NONE, "NoFile.png", "resources/icons/NoFile.png", WildLogFileType.IMAGE)),
    MOVIES(new WildLogSystemFile(0, 0, WildLogFileLinkType.NONE, "Movie.png", "resources/icons/Movie.png", WildLogFileType.IMAGE)),
    OTHER_FILES(new WildLogSystemFile(0, 0, WildLogFileLinkType.NONE, "OtherFile.png", "resources/icons/OtherFile.png", WildLogFileType.IMAGE)),
    STASH(new WildLogSystemFile(0, 0, WildLogFileLinkType.NONE, "Stash.png", "resources/icons/Stash.png", WildLogFileType.IMAGE));

    private WildLogSystemFile wildLogFile;

    private WildLogSystemImages(WildLogSystemFile inWildLogFile) {
        wildLogFile = inWildLogFile;
    }

    public WildLogSystemFile getWildLogFile() {
        return wildLogFile;
    }

}
