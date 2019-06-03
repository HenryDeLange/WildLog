package wildlog.data.enums;

public enum WildLogThumbnailSizes {
    /** 20px - For Icons */
    VERY_TINY(20),
    /** 25px - For Icons (larger) */
    TINY(25),
    /** 60px - For Photos (Tables) */
    VERY_SMALL(60),
    /** 100px - For Photos (Small Display) */
    SMALL(100),
    /** 125px - For Photos (Bulk Import Info Box) */
    MEDIUM_VERY_SMALL(125),
    /** 150px - For Photos (Medium Display) */
    MEDIUM_SMALL(150),
    /** 200px - For Photos (Bulk Import) */
    MEDIUM(200),
    /** 256px - For Photos (WildNote Sync) */
    SYNC_EXPORT(256),
    /** 300px - For Photos (Large Display) */
    NORMAL(300),
    /** 512px - Not Used Currently */
    LARGE(512),
    /** 700px - For Photos (Browse, Export) */
    VERY_LARGE(700),
    /** 875px - For Photos (Cropping) */
    VERY_VERY_LARGE(875),
    /** 1024px - For Photos (Export) */
    EXTRA_LARGE(1024),
    /** 2048px - For Photos (Max size to be uploaded to iNaturalist / Azure) */
    SYNC_LIMIT(2048);

    private final int size;

    private WildLogThumbnailSizes(int inSize) {
        size = inSize;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return size + "px";
    }

}
