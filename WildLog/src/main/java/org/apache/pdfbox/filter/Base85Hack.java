package org.apache.pdfbox.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;

/**
 * Exposing the PDFBox implementation of a Base85 encoder. 
 * The code below is form: https://stackoverflow.com/questions/7845261/base85-aka-ascii85-java-projects
 */
public class Base85Hack {
    
    public static byte[] decode(String inAscii85) {
        ArrayList<Byte> list = new ArrayList<>();
        ByteArrayInputStream in_byte = null;
        try {
            in_byte = new ByteArrayInputStream(inAscii85.getBytes("ascii"));
        }
        catch (UnsupportedEncodingException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        ASCII85InputStream in_ascii = new ASCII85InputStream(in_byte);
        try {
            int r;
            while ((r = in_ascii.read()) != -1) {
                list.add((byte) r);
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static String encode(byte[] bytes) {
        ByteArrayOutputStream out_byte = new ByteArrayOutputStream();
        ASCII85OutputStream out_ascii = new ASCII85OutputStream(out_byte);
        try {
            out_ascii.write(bytes);
            out_ascii.flush();
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String res = "";
        try {
            res = out_byte.toString("ascii");
        }
        catch (UnsupportedEncodingException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return res;
    }

}
