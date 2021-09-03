package com.boom.android.util.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.boom.android.util.BoomHelper;
import com.boom.android.util.HashUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LocalCacheUtils {

    private static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BoomHelper.getRecorderFolder() + "/" + "Cache";

    public Bitmap getBitmapFromLocal(String url) {
        String fileName;
        try {
            fileName = HashUtils.md5(url);
            File file = new File(CACHE_PATH, fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = HashUtils.md5(url);
            File file = new File(CACHE_PATH, fileName);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
