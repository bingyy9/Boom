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

    public static void appendFormattedNumber(final StringBuilder sb, final long number, final int step) {  //Change 1200 to 1200.00(step is 1), 1024 bytes to 1.00 KB(step is 1024)  //OK
        if (sb == null || number < 0 || step < 1) {
            return;
        }
        long temp1 = number * 100 / step, temp2 = temp1 % 100;
        sb.append(temp1 / 100).append('.').append(temp2 < 10 ? "0" : "").append(temp2);
    }

}
