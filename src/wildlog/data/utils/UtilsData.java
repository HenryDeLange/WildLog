package wildlog.data.utils;

public class UtilsData {

    public static String limitLength(String inString, int inLength) {
        if (inString == null)
            return null;
        else
        if (inString.trim().length() > inLength)
            return inString.trim().substring(0, inLength);
        else
            return inString.trim();
    }

    public static String sanitizeString(String inString) {
        if (inString != null)
            // Ek dink nie ek het die ' check meer nodig nie want ek gebruik nou JDBC params
            return inString.trim()/*.replaceAll("'", "''")*/;
        else
            return null;
    }

    public static String stringFromObject(Object inEnum) {
        if (inEnum == null)
            return null;
        else
            return inEnum.toString();
    }

    public static boolean checkCharacters(final String s) {
        if (s == null) return false;
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if ((c >= 'a') && (c <= 'z')) continue; // Lowercase
            if ((c >= 'A') && (c <= 'Z')) continue; // Uppercase
            if ((c >= '0') && (c <= '9')) continue; // Numeric
            if (c == 'ê' || c == 'ë') continue;
            if (c == 'ô' || c == ' ') continue;
            // Check Characters
            if (c == '!') continue;
            if (c == '.' || c == ',') continue;
            if (c == '-' || c == '_') continue;
            if (c == '(' || c == ')') continue;
            if (c == '[' || c == ']') continue;
            if (c == '&' || c == '@') continue;
            if (c == '#' || c == ';') continue;
            if (c == '+' || c == '=') continue;
            if (c == '`' /*|| c == '\''*/) continue; // Die ' gee probleme met saving en file loading
            return false;
        }
        return true;
    }

}
