package com.boom.android.util.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.boom.android.BoomApplication;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.HashUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.os.Environment.isExternalStorageRemovable;

public class LocalCacheUtils {
    private String mCachePath;
    LocalCacheUtils(){
        mCachePath = BoomApplication.getInstance().getApplicationContext().getCacheDir().getPath();
        Dogger.d(Dogger.BOOM, "cachePath: " + mCachePath, "LocalCacheUtils", "getBitmapFromLocal");
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||!isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }


    public Bitmap getBitmapFromLocal(String url) {
        String fileName;
        try {
            fileName = HashUtils.md5(url);
            File file = new File(mCachePath, fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "", "LocalCacheUtils", "getBitmapFromLocal", e);
        }
        return null;
    }

    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = HashUtils.md5(url);
            File file = new File(mCachePath, fileName);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "", "LocalCacheUtils", "setBitmapToLocal", e);
        }

    }
}
