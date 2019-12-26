package wildlog.data.enums.system;


public enum WildLogThumbnailSizes {
    /** 20px - For Icons */
    S0020_VERY_TINY(20),
    /** 25px - For Icons (larger) */
    S0025_TINY(25),
    /** 60px - For Photos (Tables) */
    S0060_VERY_SMALL(60),
    /** 100px - For Photos (Small Display) */
    S0100_SMALL(100),
    /** 125px - For Photos (Bulk Import Info Box) */
    S0125_MEDIUM_VERY_SMALL(125),
    /** 150px - For Photos (Medium Display) */
    S0150_MEDIUM_SMALL(150),
    /** 200px - For Photos (Bulk Import) */
    S0200_MEDIUM(200),
    /** 256px - For Photos (WildNote Sync) */
    S0256_SYNC_EXPORT(256),
    /** 300px - For Photos (Large Display) */
    S0300_NORMAL(300),
    /** 512px - Not Used Currently */
    S0512_LARGE(512),
    /** 700px - For Photos (Browse, Export) */
    S0700_VERY_LARGE(700),
    /** 875px - For Photos (Cropping) */
    S0875_VERY_VERY_LARGE(875),
    /** 1024px - For Photos (Export) */
    S1024_EXTRA_LARGE(1024),
    /** 2048px - For Photos (Max size to be uploaded to iNaturalist / Azure) */
    S2048_SYNC_LIMIT(2048);

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
