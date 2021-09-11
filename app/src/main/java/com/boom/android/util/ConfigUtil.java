package com.boom.android.util;

import com.boom.android.ui.adapter.repo.Resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigUtil {
    public static final int defaultTimeDelayBeforeRecording = 3;
    public static final int MAX_DELAY_BEFORE_RECORD_SECONDS = 20;

    public static final String defaultFileNameFormat = "yyyy_MM_dd_HH_mm_ss";
    public static List<String> fileNameFormats = new ArrayList<>(Arrays.asList(
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

    public static final int defaultFrameRate = 30;
    public static List<Integer> frameRates = new ArrayList<>(Arrays.asList(
            24, 25, 30, 48, 60, 90, 120
    ));

    public static final int defaultBitRate = 8;
    public static List<Integer> bitRates = new ArrayList<>(Arrays.asList(
            2, 4, 6, 8, 10, 12, 15, 16, 18, 20, 24, 30, 40, 50, 60
    ));

    public static final Resolution defaultResolution = new Resolution(1, 1, "0:0");
    public static List<Resolution> resolutions = new ArrayList<>(Arrays.asList(
            new Resolution(1, 1, "0:0")
            , new Resolution(720, 1680, "3:7")
            , new Resolution(720, 480, "3:2")
            , new Resolution(1200, 540, "20:9")
            , new Resolution(1280, 720, "16:9")
            , new Resolution(1280, 800, "8:5")
            , new Resolution(1400, 720, "2:1")
            , new Resolution(1600, 720, "20:9")
            , new Resolution(1728, 1080, "8:5")
            , new Resolution(1920, 760, "120:61")
            , new Resolution(1920, 960, "2:1")
            , new Resolution(1920, 1080, "16:9")
            , new Resolution(1920, 1200, "8:5")
            , new Resolution(2160, 1088, "135:68")
            , new Resolution(2400, 1080, "20:9")
            , new Resolution(2520, 1080, "7:3")
            , new Resolution(2560, 1600, "8:5")
    ));

    public static final int defalutAudioSampleRate = 44100;
    public static List<Integer> audioSampleRates = new ArrayList<>(Arrays.asList(
            44100, 48000
    ));

    public static final int defaultAudioBitrate = 128;
    public static List<Integer> audioBitrates = new ArrayList<>(Arrays.asList(
            64, 128, 256, 320
    ));

    public static final String defaultAudioChannel = "Mono";
    public static List<String> audioChannels = new ArrayList<>(Arrays.asList(
            "Mono", "Stereo"
    ));

    public static final boolean defaultRecordAudio = true;



}
