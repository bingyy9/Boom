package com.boom.android.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesDirUtil {

    public static final String logFileName = "boom_record_logs.txt";
    public static final String backLogFileName = "lastLog.txt";
    public static final String cacheDir = "snapshot";
    public static final String logDir = "log";
    public static final String recordDir = "record";
    private static boolean debugHasSDCard = false;

    public static File getLogFile(Context context){
        if(context == null){
            return null;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && debugHasSDCard) {
            // 有SD卡则使用SD - PS:没SD卡但是有外部存储器，会使用外部存储器
            // SD\Android\data\包名\files\Log\logs.txt
            ///storage/emulated/0/Android/data/com.boom.android/files/Log
            return new File(context.getExternalFilesDir(logDir).getPath() + File.separator);
        } else {
            // 没有SD卡或者外部存储器，使用内部存储器
            // \data\data\包名\files\Log\logs.txt
            return new File(context.getFilesDir().getPath() + File.separator + logDir + File.separator);
        }
    }

    public static File getSnapshotCacheFile(Context context){
        if(context == null){
            return null;
        }
        //判断 SD 卡是否存在，从而获取不同的缓存地址
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            // /storage/emulated/0/Android/data/com.boom.android/cache
            return new File(context.getExternalCacheDir().getPath() + File.separator + cacheDir);
        } else {
            return new File(context.getCacheDir().getPath() + File.separator + cacheDir);
        }
    }

    public static String getCacheZipLogPath(Context context){
        return context == null? null: context.getCacheDir().getAbsolutePath() + "/boom-trace.zip";
    }

    public static String getRecordFileWriteDir(Context context){
        String recordPath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && debugHasSDCard) {
            // /storage/emulated/0/Android/data/com.boom.android/files + /record/
            recordPath = context.getExternalFilesDir(recordDir).getPath()
                    + File.separator;
        } else {
            // /data/user/0/com.boom.android/files/record/
            recordPath = context.getFilesDir().getPath()
                   + File.separator
                   + recordDir
                   + File.separator;
        }
        if(ensureFileExist(recordPath)){
            return recordPath;
        } else {
            return null;
        }
    }

    public static List<String> getRecordFileReadDirs(Context context){
        List<String> dirs = new ArrayList();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirs.add(context.getExternalFilesDir(recordDir).getPath()
                    + File.separator);
        }

        //\data\data\包名\files\record\
        dirs.add(context.getFilesDir().getPath()
                + File.separator
                + recordDir
                + File.separator);

        return dirs;
    }

    private static boolean ensureFileExist(String rootDir){
        File file = new File(rootDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        return true;
    }
}
