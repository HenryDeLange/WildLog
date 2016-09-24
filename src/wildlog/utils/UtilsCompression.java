package wildlog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import wildlog.WildLogApp;

public final class UtilsCompression {
    
    private UtilsCompression() {
    }

    public static void zipFolder(Path inZipFilePath, Path inSourceFolderPath) {
        byte[] buffer = new byte[1024];
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(inZipFilePath.toFile());
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                List<String> fileList = generateFileList(inSourceFolderPath, new ArrayList<String>(35), inSourceFolderPath);
                for (String fileString : fileList) {
                    if (!Paths.get(fileString).getFileName().toString().equals(inZipFilePath.getFileName().toString())) {
                        ZipEntry zipEntry = new ZipEntry(fileString);
                        zipOutputStream.putNextEntry(zipEntry);
                        try (FileInputStream fileInputStream = new FileInputStream(inSourceFolderPath + File.separator + fileString)) {
                            int len;
                            while ((len = fileInputStream.read(buffer)) > 0) {
                                zipOutputStream.write(buffer, 0, len);
                            }
                        }
                    }
                }
                zipOutputStream.closeEntry();
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
    }

    private static List<String> generateFileList(Path inFile, List<String> inFileList, Path inFileRoot) {
        // Traverse a directory and get all (only) files
	if (Files.isRegularFile(inFile)) {
            // Get a relative path for using in the zip by removing the first part of the path
            inFileList.add(inFile.toAbsolutePath().toString().substring(
                    inFileRoot.toAbsolutePath().toString().length() + 1, 
                    inFile.toAbsolutePath().toString().length()));
	}
        else
	if (Files.isDirectory(inFile)) {
            String[] subFile = inFile.toFile().list();
            for (String subFilename : subFile) {
                generateFileList(inFile.resolve(subFilename), inFileList, inFileRoot);
            }
	}
        return inFileList;
    }

}
