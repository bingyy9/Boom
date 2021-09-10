package com.boom.android.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigUtil {
    public static final int DEFAULT_TIME_DELAY_BEFORE_RECORDING = 3;
    public static final int MAX_DELAY_BEFORE_RECORD_SECONDS = 20;

    public static final String defaultFileNameFormat = "yyyy_MM_dd_HH_mm_ss";
    public static List<String> fileNameFormat = new ArrayList<>(Arrays.asList(
            "yyyy_MM_dd_HH_mm_ss"
            , "yy_dd_MM_HH_mm_ss"
            , "yyyy_dd_MM_HH_mm_ss"
            , "dd_MM_yy_HH_mm_ss"
            , "dd_MM_yyyy_HH_mm_ss"
            , "MM_dd_yy_HH_mm_ss"
            , "MM_dd_yyyy_HH_mm_ss"
            , "yyMMdd_HHmmss"
            , "yyyyMMdd_HHmmss"
            , "yyddMM_HHmmss"
            , "yyyyddMM_HHmmss"
            , "ddMMyy_HHmmss"
            , "ddMMyyyy_HHmmss"
            , "MMddyy_HHmmss"
            , "MMddyyyy_HHmmss"
            , "yy-MM-dd_HH-mm-ss"
            , "yyyy-MM-dd_HH-mm-ss"
            , "yy-dd-MM_HH-mm-ss"
            , "yyyy-dd-MM_HH-mm-ss"
            , "dd-MM-yy_HH-mm-ss"
            , "dd-MM-yyyy_HH-mm-ss"
            , "MM-dd-yy_HH-mm-ss"
            , "MM-dd-yyyy_HH-mm-ss"
    ));

}
