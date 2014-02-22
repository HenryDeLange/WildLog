package wildlog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// FIXME: Gaan deur die code en maak dit nice, soos dit is is dit 'n copy-hack van internet voorbeeld
public class UtilsCompression {

    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public static void zipIt(String zipFile, File sourceFolder){
        byte[] buffer = new byte[1024];
        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
//    	System.out.println("Output to Zip : " + zipFile);
            List<String> fileList = generateFileList(sourceFolder, new ArrayList<String>(), sourceFolder);
            for (String file : fileList) {
                if (!Paths.get(file).getFileName().toString().equals(Paths.get(zipFile).getFileName().toString())) {
//                    System.out.println("File Added : " + file);
                    ZipEntry ze = new ZipEntry(file);
                    zos.putNextEntry(ze);
                    FileInputStream in = new FileInputStream(sourceFolder + File.separator + file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                }
//            else {
//                System.out.println("File skipped : " + file);
//            }
            }
            zos.closeEntry();
            //remember close it
            zos.close();
//    	System.out.println("Done");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     * @param node file or directory
     */
    private static List<String> generateFileList(File node, List<String> fileList, File inSourceRoot){
    	//add file only
	if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), inSourceRoot));
	}
	if(node.isDirectory()){
            String[] subNote = node.list();
            for(String filename : subNote){
                    generateFileList(new File(node, filename), fileList, inSourceRoot);
            }
	}
        return fileList;
    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private static String generateZipEntry(String file, File inSourceRoot){
    	return file.substring(inSourceRoot.getAbsolutePath().length()+1, file.length());
    }

}
