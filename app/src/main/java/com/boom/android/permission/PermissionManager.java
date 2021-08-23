package com.boom.android.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.boom.utils.StringUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    public static final int STORAGE_REQUEST_CODE = 1000;
    public static final int AUDIO_REQUEST_CODE   = 1001;
    public static final int CAMERA_REQUEST_CODE   = 1002;
    public static final int ALERT_WINDOW_REQUEST_CODE   = 1003;

    public static void requestAllPermission(Context context){
        requestPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE);
        requestPermission(context, Manifest.permission.RECORD_AUDIO, AUDIO_REQUEST_CODE);
        requestPermission(context, Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
        requestPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW, ALERT_WINDOW_REQUEST_CODE);
    }

    public static void requestPermission(Context context, String permission, int requestCode){
        askPermission(context, permission, requestCode);
    }
    private static void askPermission(Context context, String permission, int requestCode){
        if (context == null){
            return;
        }

        if(StringUtils.isEmpty(permission)){
            return;
        }

        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {Manifest.permission.RECORD_AUDIO}, requestCode);
        }
    }
}
