package com.boom.android.util.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

public class BitmapCacheUtils {
    private static BitmapCacheUtils mInstance;

    private NetCacheUtils mNetCacheUtils;
//    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private DiskLruCacheUtil mDiskLruCacheUtil;

    public static BitmapCacheUtils getInstance(){
        if(mInstance == null){
            mInstance = new BitmapCacheUtils();
        }
        return mInstance;
    }

    public BitmapCacheUtils(){
        mMemoryCacheUtils=new MemoryCacheUtils();
//        mLocalCacheUtils=new LocalCacheUtils();
        mDiskLruCacheUtil = new DiskLruCacheUtil();
        mNetCacheUtils=new NetCacheUtils(mDiskLruCacheUtil,mMemoryCacheUtils);
    }

    public void display(ImageView ivPic, String url) {
        ivPic.setImageResource(R.drawable.ic_placeholder);
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            Dogger.i(Dogger.BOOM, "Get Bitmap from memory", "BitmapUtils", "disPlay");
            return;
        }

        //本地缓存
        bitmap = mDiskLruCacheUtil.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            Dogger.i(Dogger.BOOM, "Get bitmap from local cache", "BitmapUtils", "disPlay");
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        mNetCacheUtils.getBitmapFromNet(ivPic,url);
    }
}
