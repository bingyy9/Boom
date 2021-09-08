package com.boom.android.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FilesDirUtil {

    public static final String logFileName = "boom_record_logs.txt";
    public static final String backLogFileName = "lastLog.txt";

    public static final String cacheDir = "snapshot";

    public static final String recordDir = "record";

    public static File getLogFile(Context context){
        if(context == null){
            return null;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 有SD卡则使用SD - PS:没SD卡但是有外部存储器，会使用外部存储器
            // SD\Android\data\包名\files\Log\logs.txt
            ///storage/emulated/0/Android/data/com.boom.android/files/Log
            return new File(context.getExternalFilesDir("Log").getPath() + "/");
        } else {
            // 没有SD卡或者外部存储器，使用内部存储器
            // \data\data\包名\files\Log\logs.txt
            return new File(context.getFilesDir().getPath() + "/Log/");
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

    public static String getRecordFileDir(Context context){
        String recordPath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            recordPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + BoomHelper.getRecorderFolder()
                    + File.separator
                    + recordDir
                    + File.separator;
        } else {
            //\data\data\包名\files\record\
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
