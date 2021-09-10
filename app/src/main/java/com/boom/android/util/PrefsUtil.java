package com.boom.android.util;

import android.content.Context;

public class PrefsUtil {
    private static final String TIME_DELAY_BEFORE_RECORDING = "time_delay_before_recording";
    private static final String FILE_NAME_FORMAT = "file_name_format";

    public static String getTimeDelayBeforeRecording(Context context){
        return String.valueOf(Prefs.with(context).readInt(TIME_DELAY_BEFORE_RECORDING
                , ConfigUtil.DEFAULT_TIME_DELAY_BEFORE_RECORDING));
    }

    public static void setTimeDelayBeforeRecording(Context context, int seconds){
        Prefs.with(context).writeInt(TIME_DELAY_BEFORE_RECORDING, seconds);
    }

    public static String getFileNameFormat(Context context){
        return Prefs.with(context).read(FILE_NAME_FORMAT, ConfigUtil.defaultFileNameFormat);
    }

    public static void setFileNameFormat(Context context, String value){
        Prefs.with(context).write(FILE_NAME_FORMAT, value);
    }
}
