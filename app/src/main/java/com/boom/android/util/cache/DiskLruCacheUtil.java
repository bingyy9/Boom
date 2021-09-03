package com.boom.android.util.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.boom.android.BoomApplication;
import com.boom.android.log.Dogger;
import com.boom.android.util.HashUtils;
import com.boom.utils.StringUtils;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiskLruCacheUtil {
    private DiskLruCache diskLruCache;
    private DiskLruCache.Snapshot snapshot;
    private DiskLruCache.Editor editor;

    public DiskLruCacheUtil(){
        try {
            File cacheFile = getDiskCacheDir(BoomApplication.getInstance().getApplicationContext(), "snapshot");
            if (!cacheFile.exists()) {
                cacheFile.mkdirs();
            }
            diskLruCache = DiskLruCache.open(cacheFile, getAppVersion(BoomApplication.getInstance().getApplicationContext()), 1,
                    10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        //判断 SD 卡是否存在，从而获取不同的缓存地址
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public Bitmap getBitmapFromLocal(String url) {
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            String key = hashKeyForDisk(url);
            //获取 snapshot 对象
            snapshot = diskLruCache.get(key);
            //如果为空，则需要从网络下载图片，并存入缓存
            //从 snapshot 中获取 bitmap
            if (snapshot != null) {
                in = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(in);
            }
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "", "DiskLruCacheUtil", "getBitmapFromLocal", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String key = HashUtils.md5(url);
            key = ensureKey(key);
            //获取 snapshot 对象
            snapshot = diskLruCache.get(key);
            //如果为空，则需要从网络下载图片，并存入缓存
            if (snapshot == null) {
                editor = diskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (cacheBitmapToStream(bitmap, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                //同步文件记录
                diskLruCache.flush();
                //下载完成后，重新获取 snapshot 对象
                snapshot = diskLruCache.get(key);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private String ensureKey(String input){
        if(StringUtils.isEmpty(input)){
            return null;
        }
        int MAX_LENGTH = 64;
        String str = input.length() > MAX_LENGTH ? input.substring(0, MAX_LENGTH) : input;
        return str.toLowerCase().replaceAll("[^a-z0-9_-]","_");
    }

    private boolean cacheBitmapToStream(Bitmap bitmap, OutputStream outputStream) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(Bitmap2InputStream(bitmap));
            out = new BufferedOutputStream(outputStream);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    private String hashKeyForDisk(String url) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = byteToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
