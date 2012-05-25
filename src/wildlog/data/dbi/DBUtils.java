package wildlog.data.dbi;

public class DBUtils {
    
    public static String limitLength(String inString, int inLength) {
        if (inString.trim().length() > inLength)
            return inString.trim().substring(0, inLength);
        else
            return inString.trim();
    }
    
    public static String sanitizeString(String inString) {
        if (inString != null)
            return inString.trim().replaceAll("'", "''");
        else
            return null;
    }
    
    public static String stringFromObject(Object inEnum) {
        if (inEnum == null)
            return null;
        else
            return inEnum.toString();
    }
}
