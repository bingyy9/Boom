package com.boom.utils;

public class StringUtils {
    public static boolean isEmpty(final String string) {
        return (string == null || KotlinUtil.trim(string).length() == 0);
    }

    public static boolean contentEquals(final String string1, final String string2) {
        if (isEmpty(string1) || isEmpty(string2)) {
            return false;
        }

        return KotlinUtil.trim(string1).equals(KotlinUtil.trim(string2));
    }

    public static boolean stringStartsWith(final String string1, final String string2, final boolean ignoreCase) {  //OKOK
        if (string1 == null || string2 == null) {
            return false;
        }
        if (ignoreCase) {
            return string1.toLowerCase().startsWith(string2.toLowerCase());
        } else {
            return string1.startsWith(string2);
        }
    }

}
