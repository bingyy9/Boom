package com.boom.android.util;

import android.os.Build;

import com.boom.utils.StringUtils;

public class AndroidHardwareUtils {
    public static boolean isXiaomiDevice(){
        return StringUtils.stringStartsWith(Build.MANUFACTURER,"Xiaomi",true);
    }
}
