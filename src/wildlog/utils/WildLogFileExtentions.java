package wildlog.utils;

import java.nio.file.Path;


public class WildLogFileExtentions {
    public static enum Images {
        JPG("jpg"),
        JPEG("jepg"),
        GIF("gif"),
        TIF("tif"),
        TIFF("tiff"),
        BMP("bmp"),
        PNG("png");

        private String extention;

        private Images(String inExtention) {
            extention = inExtention;
        }

        /**
         * Returns the lowercase extention without a full-stop.
         * @return
         */
        public String getExtention() {
            return extention;
        }

        /**
         * Checks whether the extention provided matches any of the known extentions, ignoring case.
         * Extentions should not include the full-stop.
         * @param inExtention
         * @return
         */
        public static boolean isKnownExtention(String inExtention) {
            if (inExtention != null) {
                for (Images imagesEnum : values()) {
                    if (inExtention.equalsIgnoreCase(imagesEnum.getExtention())) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Checks whether the filename's extention of path provided matches any of the known extentions, ignoring case.
         * Extentions should not include the full-stop.
         * @param inPath
         * @return
         */
        public static boolean isKnownExtention(Path inPath) {
            String fileName = inPath.getFileName().toString();
            return isKnownExtention(fileName.substring(fileName.lastIndexOf('.') + 1));
        }

        public static boolean isJPG(Path inPath) {
            return inPath.getFileName().toString().toLowerCase().endsWith(WildLogFileExtentions.Images.JPG.getExtention())
                || inPath.getFileName().toString().toLowerCase().endsWith(WildLogFileExtentions.Images.JPEG.getExtention());
        }

    }

    public static enum Movies  {
        AVI("avi"),
        ASF("asf"),
        MPG("mpg"),
        MPEG("mpeg"),
        MOV("mov"),
        FLV("flv"),
        M4V("m4v"),
        WMV("wmv"),
        MP4("mp4");

        private String extention;

        private Movies(String inExtention) {
            extention = inExtention;
        }

        /**
         * Returns the lowercase extention without a full-stop.
         * @return
         */
        public String getExtention() {
            return extention;
        }

        /**
         * Checks whether the extention provided matches any of the known extentions, ignoring case.
         * Extentions should not include the full-stop.
         * @param inExtention
         * @return
         */
        public static boolean isKnownExtention(String inExtention) {
            if (inExtention != null) {
                for (Movies moviesEnum : values()) {
                    if (inExtention.equalsIgnoreCase(moviesEnum.getExtention())) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Checks whether the filename's extention of path provided matches any of the known extentions, ignoring case.
         * Extentions should not include the full-stop.
         * @param inPath
         * @return
         */
        public static boolean isKnownExtention(Path inPath) {
            String fileName = inPath.getFileName().toString();
            return isKnownExtention(fileName.substring(fileName.lastIndexOf('.') + 1));
        }
        
    }

}
