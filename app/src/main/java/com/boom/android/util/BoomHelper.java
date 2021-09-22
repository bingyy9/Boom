package com.boom.android.util;

import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import com.boom.android.BoomApplication;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class BoomHelper {
    public static final String filePostfix = ".mp4";

    public static boolean ensureDrawOverlayPermission(Context context) {
        boolean ensureOverlay;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Dogger.i(Dogger.BOOM, "ensureOverlay: " + true, "BoomHelper", "ensureDrawOverlayPermission");
            return true;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                ensureOverlay = result == declaredField2.getInt(cls);
                Dogger.i(Dogger.BOOM, "ensureOverlay: " + ensureOverlay, "BoomHelper", "ensureDrawOverlayPermission");
                return ensureOverlay;
            } catch (Exception e) {
                Dogger.e(Dogger.BOOM, "", "BoomHelper", "ensureDrawOverlayPermission", e);
                return false;
            }
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//                if (appOpsMgr == null)
//                    return false;
//                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
//                        .getPackageName());
//                ensureOverlay = (mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED);
//                Dogger.i(Dogger.BOOM, "ensureOverlay: " + ensureOverlay, "BoomHelper", "ensureDrawOverlayPermission");
//                return ensureOverlay;
//            } else {
                ensureOverlay = Settings.canDrawOverlays(context);
                Dogger.i(Dogger.BOOM, "ensureOverlay: " + ensureOverlay, "BoomHelper", "ensureDrawOverlayPermission");
                return ensureOverlay;
//            }
        }
    }

    public static String getApplicationName(){
        return BoomApplication.getInstance().getApplicationContext().getResources().getString(R.string.app_name);
    }

    public static boolean enableGoogleService(){
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(BoomApplication.getInstance());
        if(resultCode != ConnectionResult.SUCCESS) {
            Dogger.i(Dogger.BOOM, "google service: " + false, "BoomHelper", "validGoogleService");
            return false;
        } else {
            Dogger.i(Dogger.BOOM, "google service: " + true, "BoomHelper", "validGoogleService");
            return true;
        }
    }

    public static String getDeviceInfo() {
        return new StringBuilder().append("Application version = ").append(AndroidVersionManager.getVersion())
                .append("; Application build no. = ").append(AndroidVersionManager.getBuildNumber())
                .append("; Build.BOARD = ").append(Build.BOARD)
                .append("; Build.CPU_ABI = ").append(Build.CPU_ABI)
                .append("; Build.DISPLAY = ").append(Build.DISPLAY)
                .append("; Build.FINGERPRINT = ").append(Build.FINGERPRINT)
                .append("; Build.HOST = ").append(Build.HOST)
                .append("; Build.ID = ").append(Build.ID)
                .append("; Build.TAGS = ").append(Build.TAGS)
                .append("; Build.TIME = ").append(Build.TIME)
                .append("; Build.TYPE = ").append(Build.TYPE)
                .append("; Build.USER = ").append(Build.USER)
                .append("; Build.MODEL = ").append(Build.MODEL)
                .append("; Build.DEVICE = ").append(Build.DEVICE)
                .append("; Build.PRODUCT = ").append(Build.PRODUCT)
                .append("; Build.BRAND = ").append(Build.BRAND)
                .append("; Build.MANUFACTURER = ").append(Build.MANUFACTURER)
                .append("; Build.VERSION.SDK = ").append(Build.VERSION.SDK)
                .append("; Build.VERSION.RELEASE = ").append(Build.VERSION.RELEASE)
                .append("; Build.VERSION.CODENAME = ").append(Build.VERSION.CODENAME)
                .append("; PID = ").append(android.os.Process.myPid())
                .append("; TIMEZONE.ID = ").append(TimeZone.getDefault().getID())
                .append("; CPU.COUNT= ").append(AndroidHardwareUtils.getDeviceCpuCount())
                .append("; DEVICE.TOTALMEM =").append(AndroidHardwareUtils.getDeviceTotalMem(BoomApplication.getInstance()))
                .toString();
    }


}
