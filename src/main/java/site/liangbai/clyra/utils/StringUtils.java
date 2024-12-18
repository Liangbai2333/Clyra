package site.liangbai.clyra.utils;

public class StringUtils {
    public static String removePrefix(String str, String prefix) {
        if (str != null && str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }

    public static String trimStart(String str) {
        return str.replaceFirst("^\\s+", "");
    }
}
