package wildlog.data.enums.utils;

public enum WildLogThumbnailSizes {
    /** 20px - For Icons */
    VERY_TINY(20),
    /** 25px - For Icons (larger) */
    TINY(25),
    /** 60px - For Photos (Tables) */
    VERY_SMALL(60),
    /** 100px - For Photos (Small Display) */
    SMALL(100),
    /** 128px - For Photos (WildNote Sync) */
    SYNC_EXPORT(128),
    /** 150px - For Photos (Medium Display) */
    MEDIUM_SMALL(150),
    /** 200px - For Photos (Bulk Import) */
    MEDIUM(200),
    /** 300px - For Photos (Large Display) */
    NORMAL(300),
    /** 500px - Not Used Currently */
    LARGE(500),
    /** 700px - For Photos (Browse, Export) */
    VERY_LARGE(700);

    private int size;

    private WildLogThumbnailSizes(int inSize) {
        size = inSize;
    }

    public int getSize() {
        return size;
    }

}
