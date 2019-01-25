package wildlog.inaturalist.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class UtilsINaturalist {
    
    
    private UtilsINaturalist() {
    }
    
    
    /**
     * Makes sure the String is safe to be used in a URL.
     */
    public static String forURL(Object inString) {
        try {
            return URLEncoder.encode(inString.toString(), "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException ex) {
            // Log the error, but then try to use the String as is, maybe it works
            ex.printStackTrace(System.err);
            return inString.toString();
        }
    }
    
}
