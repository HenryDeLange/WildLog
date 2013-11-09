package wildlog.data.enums.utils;

public enum WildLogThumbnailSizes {
    /** 20px */
    VERY_TINY(20),
    /** 25px */
    TINY(25),
    /** 60px */
    VERY_SMALL(60),
    /** 100px */
    SMALL(100),
    /** 150px */
    MEDIUM_SMALL(150),
    /** 200px */
    MEDIUM(200),
    /** 300px */
    NORMAL(300),
    /** 500px */
    LARGE(500),
    /** 850px */
    VERY_LARGE(850),
    /** 128px */
    SYNC_EXPORT(128);

    private int size;

    private WildLogThumbnailSizes(int inSize) {
        size = inSize;
    }

    public int getSize() {
        return size;
    }

}
