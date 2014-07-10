package wildlog.data.utils;

import java.util.Date;

public final class UtilsData {

    private UtilsData() {
    }

    public static String limitLength(String inString, int inLength) {
        if (inString == null) {
            return null;
        }
        else
        if (inString.trim().length() > inLength) {
            return inString.trim().substring(0, inLength);
        }
        else {
            return inString.trim();
        }
    }

    public static String sanitizeString(String inString) {
        if (inString != null) {
            // Ek dink nie ek het die ' check meer nodig nie want ek gebruik nou JDBC params
            return inString.trim()/*.replaceAll("'", "''")*/;
        }
        else {
            return null;
        }
    }

    public static String stringFromObject(Object inEnum) {
        if (inEnum == null) {
            return null;
        }
        else {
            return inEnum.toString();
        }
    }

    public static boolean checkCharacters(final String s) {
        if (s == null) {
            return false;
        }
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if (c == '\\') return false;
            if (c == '/') return false;
            if (c == '?') return false;
            if (c == '*') return false;
            if (c == '<') return false;
            if (c == '>') return false;
            if (c == '\"') return false;
            if (c == ':') return false;
            if (c == '|') return false;
        }
        return true;
    }

    /**
     * Vergelyk die twee Objects en kyk of hulle waarder eenders is. <br/>
     * <b>WARNING: Gebruik die toString() (case-sensitive) vir meeste classes.</b>
     * @param inObject1
     * @param inObject2
     * @return
     */
    public static boolean isTheSame(Object inObject1, Object inObject2) {
        if (inObject1 == null && inObject2 == null) {
            return true;
        }
        if ((inObject1 == null && inObject2 != null)) {
            return inObject2.toString().isEmpty();
        }
        if (inObject1 != null && inObject2 == null) {
            return inObject1.toString().isEmpty();
        }
        if (inObject1 != null && inObject2 != null) {
            if (inObject1 instanceof Date && inObject2 instanceof Date) {
                // Dates are sometimes java.util.Date and othertimes java.swl.Date and they have different toString() methods
                if (((Date) inObject1).getTime() == ((Date) inObject2).getTime()) {
                    return true;
                }
            }
            if (inObject1.toString() == null && inObject2.toString() == null) {
                return true;
            }
            if ((inObject1.toString() == null && inObject2.toString() != null) || (inObject1.toString() != null && inObject2.toString() == null)) {
                return false;
            }
            if (inObject1.toString().equals(inObject2.toString())) {
                return true;
            }
        }
        return false;
    }

}
