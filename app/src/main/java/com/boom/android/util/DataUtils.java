package com.boom.android.util;

import java.text.SimpleDateFormat;

public class DataUtils {
    public static String formatDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter == null? null: formatter.format(time);
    }
}
