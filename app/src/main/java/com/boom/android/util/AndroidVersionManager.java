package com.boom.android.util;

import android.content.Context;

import com.boom.android.BuildConfig;

public final class AndroidVersionManager {

    private static String mVersion = "";
    private static String mBuildNumber = "0";
    static {
        mVersion = BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE;
        mBuildNumber = String.valueOf(BuildConfig.VERSION_CODE);
    }

    public static String getVersion() {
        return mVersion;
    }

    public static String getBuildNumber() {
        return mBuildNumber;
    }

    public static void configVersion(final Context context) {
    }
}