package chat.utils;

public class MyStringUtils {

    public static boolean contains(String key, String line) {
        return !(line.indexOf(key + ":") < 0);
    }
    
    public static String get(String key, String line) {
        int index = line.indexOf(key + ":");
        int len = (key + ":").length();
        int endIndex = line.indexOf(",", index + len);
        return line.substring(index + len, endIndex == -1 ? line.length() - 1 : endIndex);
    }
}
