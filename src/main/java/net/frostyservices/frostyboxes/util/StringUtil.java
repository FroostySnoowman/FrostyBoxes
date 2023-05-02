package net.frostyservices.frostyboxes.util;

public class StringUtil {

    public static String convertToSnakeCase(String string) {
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            String c = string.substring(i, i + 1);
            if (c.equals(c.toUpperCase())) {
                resultString.append("_");
            }
            resultString.append(c.toLowerCase());
        }
        return resultString.toString();
    }
}