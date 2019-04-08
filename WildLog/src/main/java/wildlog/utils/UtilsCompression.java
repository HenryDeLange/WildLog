package wildlog.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.Level;
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
                        try (FileInputStream fileInputStream = new FileInputStream(inSourceFolderPath.resolve(fileString).toFile())) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
    
    public static void unzipFile(Path inSourceZIP, Path inDestinationFolder) {
// NOTE: Ek het hierdie nog net getoets met die backups se ZIPs wat net een file in het...
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(inSourceZIP.toFile())));
            byte[] buffer = new byte[1024];
            int count;
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                // Make the folders
                if (zipEntry.isDirectory()) {
                    Path folder = inDestinationFolder.resolve(zipEntry.getName());
                    Files.createDirectories(folder);
                }
                else {
                    Path folder = inDestinationFolder.resolve(zipEntry.getName());
                    Files.createDirectories(folder.getParent());
                }
                // Write the file
                Path destinationFile = inDestinationFolder.resolve(zipEntry.getName());
                Files.deleteIfExists(destinationFile);
                try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile.toFile())) {
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                }
                // Prepare for next entry
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        }
    }

}
