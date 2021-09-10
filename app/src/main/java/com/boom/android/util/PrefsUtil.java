package com.boom.android.util;

import android.content.Context;

import com.boom.android.ui.adapter.repo.Resolution;

public class PrefsUtil {
    private static final String TIME_DELAY_BEFORE_RECORDING = "time_delay_before_recording";
    private static final String FILE_NAME_FORMAT = "file_name_format";
    private static final String FRAME_RATE = "frame_rate";
    private static final String BITRATE = "bitrate";
    private static final String RESOLUTION = "resolution";

    public static String getTimeDelayBeforeRecording(Context context){
        return String.valueOf(Prefs.with(context).readInt(TIME_DELAY_BEFORE_RECORDING
                , ConfigUtil.defaultTimeDelayBeforeRecording));
    }

    public static void setTimeDelayBeforeRecording(Context context, int seconds){
        Prefs.with(context).writeInt(TIME_DELAY_BEFORE_RECORDING, seconds);
    }

    public static int getFrameRate(Context context){
        return Prefs.with(context).readInt(FRAME_RATE, ConfigUtil.defaultFrameRate);
    }

    public static void setFrameRate(Context context, int frameRate){
        Prefs.with(context).writeInt(FRAME_RATE, frameRate);
    }

    public static int getBitrate(Context context){
        return Prefs.with(context).readInt(BITRATE, ConfigUtil.defaultBitRate);
    }

    public static void setBitrate(Context context, int bitrate){
        Prefs.with(context).writeInt(BITRATE, bitrate);
    }



    public static String getFileNameFormat(Context context){
        return Prefs.with(context).read(FILE_NAME_FORMAT, ConfigUtil.defaultFileNameFormat);
    }

    public static void setFileNameFormat(Context context, String value){
        Prefs.with(context).write(FILE_NAME_FORMAT, value);
    }

    public static Resolution getResolution(Context context){
        String value = Prefs.with(context).read(RESOLUTION, "");
        Resolution resolution = new Resolution(value);
        return resolution;
    }

    public static void setResolution(Context context, Resolution resolution){
        if(resolution == null){
            return;
        }
        Prefs.with(context).write(RESOLUTION, resolution.save());
    }

}
