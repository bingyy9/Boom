package com.boom.android.util;

import android.os.Debug;

import com.boom.android.log.Dogger;
import com.boom.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @version Harley Liang code refactoring 05/09/2016 Do we need this function?
 */
public class AndroidMemoryMonitor {

    private static AndroidMemoryMonitor mInstance = new AndroidMemoryMonitor();

    private Timer mMemMonitorTimer;

    public static AndroidMemoryMonitor getInstance() {
        return mInstance;
    }

    public void startMemoryMonitor() {  //OK
        Dogger.i(Dogger.BOOM, "", "AndroidMemoryMonitor", "startMemoryMonitor");
        if (mMemMonitorTimer == null) {
            //
        } else {
            mMemMonitorTimer.cancel();
        }
        mMemMonitorTimer = new Timer();
        mMemMonitorTimer.schedule(new TimerTask() {
            public final void run() {
                traceMemoryUsage();  //TODO:flush here and enable this
            }
        }, 0, 10000L);
    }

    public void stopMemMonitor() {
        Dogger.i(Dogger.BOOM, "", "AndroidMemoryMonitor", "stopMemMonitor");
        if (mMemMonitorTimer == null) {
        } else {
            mMemMonitorTimer.cancel();
        }
    }

    public void traceMemoryUsage() {  //OK
        final long max = Runtime.getRuntime().maxMemory();
        final long heap = Runtime.getRuntime().totalMemory();
        final long free = Runtime.getRuntime().freeMemory();
        final long allocated = heap - free;
        final long nativeAlloc = Debug.getNativeHeapAllocatedSize();
        final long nativeFree = Debug.getNativeHeapFreeSize();
        final long nativeHeap = Debug.getNativeHeapSize();

        final StringBuilder sb = new StringBuilder(128);

        StringUtils.appendFormattedNumber(sb.append("Max="), max, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M, Heap="), heap, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M, Allocated="), allocated, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M, Free="), free, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M [nativeUsed="), nativeAlloc, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M, nativeFree="), nativeFree, 1048576);
        StringUtils.appendFormattedNumber(sb.append("M, nativeHeapSize="), nativeHeap, 1048576);

        sb.append("M]").append(" [FD:").append(getFDCount()).append(",Thread:").append(Thread.getAllStackTraces().keySet().size()).append("]");
        Dogger.i(Dogger.BOOM, "MemUsage: " + sb.toString(), "AndroidMemoryMonitor", "traceMemoryUsage");
    }

    private int getFDCount(){
        int count = 0;
        int id = android.os.Process.myPid();

        InputStream inputStream = null;
        try {
            Process p = Runtime.getRuntime().exec("ls /proc/"+id+"/fd");
            inputStream = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = in.readLine()) != null
                    && !line.equals("null")) {
                count++;
            }
        } catch (Throwable e) {
            Dogger.i(Dogger.BOOM, "", "AndroidMemoryMonitor", "getFDCount", e);
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (Throwable e) { ///NOSONAR
                    Dogger.i(Dogger.BOOM, "", "AndroidMemoryMonitor", "getFDCount", e);
                }
            }
        }

        return count;
    }
}
