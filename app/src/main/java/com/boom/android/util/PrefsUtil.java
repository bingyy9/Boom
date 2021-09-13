package com.boom.android.util;

import android.content.Context;

import com.boom.android.ui.adapter.repo.Resolution;
import com.boom.utils.StringUtils;

public class PrefsUtil {
    private static final String TIME_DELAY_BEFORE_RECORDING = "time_delay_before_recording";
    private static final String FILE_NAME_FORMAT = "file_name_format";
    private static final String VIDEO_FRAME_RATE = "video_frame_rate";
    private static final String VIDEO_BITRATE = "video_bitrate";
    private static final String RESOLUTION = "resolution";
    private static final String AUDIO_SAMPLE_RATE = "audio_sample_rate";
    private static final String AUDIO_BITRATE = "audio_bitrate";
    private static final String AUDIO_CHANNEL = "audio_channel";
    private static final String RECORD_AUDIO = "record_audio";
    private static final String CAMERA_ID = "camera_id";


    public static String getTimeDelayBeforeRecording(Context context){
        return String.valueOf(Prefs.with(context).readInt(TIME_DELAY_BEFORE_RECORDING
                , ConfigUtil.defaultTimeDelayBeforeRecording));
    }

    public static void setTimeDelayBeforeRecording(Context context, int seconds){
        Prefs.with(context).writeInt(TIME_DELAY_BEFORE_RECORDING, seconds);
    }

    public static int getVideoFrameRate(Context context){
        return Prefs.with(context).readInt(VIDEO_FRAME_RATE, ConfigUtil.defaultFrameRate);
    }

    public static void setVideoFrameRate(Context context, int frameRate){
        Prefs.with(context).writeInt(VIDEO_FRAME_RATE, frameRate);
    }

    public static int getVideoBitrate(Context context){
        return Prefs.with(context).readInt(VIDEO_BITRATE, ConfigUtil.defaultBitRate);
    }

    public static void setVideoBitrate(Context context, int bitrate){
        Prefs.with(context).writeInt(VIDEO_BITRATE, bitrate);
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

    public static int getAudioBitrate(Context context){
        return Prefs.with(context).readInt(AUDIO_BITRATE, ConfigUtil.defaultAudioBitrate);
    }

    public static void setAudioBitrate(Context context, int bitrate){
        Prefs.with(context).writeInt(AUDIO_BITRATE, bitrate);
    }

    public static int getAudioSampleRate(Context context){
        return Prefs.with(context).readInt(AUDIO_SAMPLE_RATE, ConfigUtil.defalutAudioSampleRate);
    }

    public static void setAudioSampleRate(Context context, int sampleRate){
        Prefs.with(context).writeInt(AUDIO_SAMPLE_RATE, sampleRate);
    }

    public static String getAudioChannel(Context context){
        return Prefs.with(context).read(AUDIO_CHANNEL, ConfigUtil.defaultAudioChannel);
    }

    public static int getAudioChannelInt(Context context){
        if(StringUtils.contentEquals(Prefs.with(context).read(AUDIO_CHANNEL, ConfigUtil.defaultAudioChannel), ConfigUtil.defaultAudioChannel)){
            return 1;
        } else {
            return 2;
        }
    }

    public static void setAudioChannel(Context context, String value){
        Prefs.with(context).write(AUDIO_CHANNEL, value);
    }

    public static boolean isRecordAudio(Context context){
        return Prefs.with(context).readBoolean(RECORD_AUDIO, ConfigUtil.defaultRecordAudio);
    }

    public static void setRecordAudio(Context context, boolean b){
        Prefs.with(context).writeBoolean(RECORD_AUDIO, b);
    }

    public static String getCameraId(Context context){
        return Prefs.with(context).read(CAMERA_ID, ConfigUtil.getInstance().defaultCameraId);
    }

    public static int getCameraIInt(Context context){
        String cameraId = Prefs.with(context).read(CAMERA_ID, ConfigUtil.getInstance().defaultCameraId);
        if(StringUtils.contentEquals(cameraId, "1")){
            return 1;  //front
        } else {
            return 0; //rear camera
        }
    }

    public static void setCameraId(Context context, String id){
        Prefs.with(context).write(CAMERA_ID, id);
    }


}
