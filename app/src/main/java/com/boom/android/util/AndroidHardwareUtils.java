package com.boom.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.boom.android.log.Dogger;
import com.boom.utils.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class AndroidHardwareUtils {
    private static AtomicInteger cpuCount = new AtomicInteger(-1);
    private static AtomicLong memSize = new AtomicLong(-1);

    public static boolean isXiaomiDevice(){
        return StringUtils.stringStartsWith(Build.MANUFACTURER,"Xiaomi",true);
    }

    public static int getDeviceCpuCount() {
        int count = cpuCount.get();
        if (count > 0) {
            // already set, simply return
            return count;
        }

        int cpu_count = 0;
        class CpuFilter implements FilenameFilter {
            @Override
            public boolean accept(File fl, String name) {
                if (Pattern.matches("cpu[0-9]", name)) {
                    return true;
                }
                return false;
            }
        }
        cpu_count = FileAssistant.Companion.countFiles("/sys/devices/system/cpu",new CpuFilter());
        if(cpu_count > 0) {
            cpuCount.set(cpu_count);
        }
        return cpu_count;
    }

    public static long getDeviceTotalMem(Context context) {
        long memSz = memSize.get();
        if (memSz > 0) {
            return memSz;
        }
        memSz = 1024 * 1024 * 1024;
        if (context == null) {
            Dogger.i(Dogger.BOOM, "", "AndroidHardwareUtils", "getDeviceTotalMem");
            Dogger.i(Dogger.BOOM, "context is null, Device total memory size: " + 1024 * 1024 * 1024, "AndroidHardwareUtils", "getDeviceTotalMem");
            return memSz;
        }
        ActivityManager actManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        if (actManager != null) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem;
            //Logger.i(TAG, "Device total memory size: " + totalMemory);
            if (totalMemory > 0) {
                memSize.set(totalMemory);
            }
            return totalMemory;
        }

        // if cann't get meminfo, return 1GB
        Dogger.d(Dogger.BOOM, "get failed, Device total memory size: " + 1024 * 1024 * 1024, "AndroidHardwareUtils", "getDeviceTotalMem");
        return memSz;
    }
}
