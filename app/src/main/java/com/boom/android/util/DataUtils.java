package com.boom.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    public static String formatDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter == null? null: formatter.format(time);
    }

    public static String getYear(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return formatter == null? null: formatter.format(date);
    }
}
