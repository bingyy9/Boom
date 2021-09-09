package com.boom.android.util;

import android.content.Context;

public class PrefsUtil {
    private static final String TIME_DELAY_BEFORE_RECORDING = "time_delay_before_recording";
    private static final int DEFAULT_TIME_DELAY_BEFORE_RECORDING = 3;
    private static final String FILE_NAME_FORMAT = "file_name_format";
    private static final String DEFAULT_FILE_NAME_FORMAT = "yyyy_MM_dd_HH_mm_ss";

    public static String getTimeDelayBeforeRecording(Context context){
        return String.valueOf(Prefs.with(context).readInt(TIME_DELAY_BEFORE_RECORDING
                , DEFAULT_TIME_DELAY_BEFORE_RECORDING));
    }

    public static void setTimeDelayBeforeRecording(Context context, int seconds){
        Prefs.with(context).writeInt(TIME_DELAY_BEFORE_RECORDING, seconds);
    }

    public static String getFileNameFormat(Context context){
        return Prefs.with(context).read(FILE_NAME_FORMAT, DEFAULT_FILE_NAME_FORMAT);
    }
}
