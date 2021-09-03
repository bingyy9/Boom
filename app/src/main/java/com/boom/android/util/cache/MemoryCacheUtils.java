package com.boom.android.util.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.boom.android.log.Dogger;
import com.boom.android.util.HashUtils;

import java.lang.ref.SoftReference;

public class MemoryCacheUtils {
    // private HashMap<String,Bitmap> mMemoryCache=new HashMap<>();//1.因为强引用,容易造成内存溢出，所以考虑使用下面弱引用的方法
    // private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new HashMap<>();//2.因为在Android2.3+后,系统会优先考虑回收弱引用对象,官方提出使用LruCache
    private LruCache<String, SoftReference<Bitmap>> mMemoryCache;

    public MemoryCacheUtils(){
        long maxMemory = Runtime.getRuntime().maxMemory()/8;//得到手机最大允许内存的1/8,即超过指定内存,则开始回收
        //需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
        mMemoryCache=new LruCache<String, SoftReference<Bitmap>>((int) maxMemory){
            //用于计算每个条目的大小
            @Override
            protected int sizeOf(String key, SoftReference<Bitmap> bitmapSoftReference) {
                if(bitmapSoftReference != null){
                    int byteCount = bitmapSoftReference.get().getByteCount();
                    return byteCount;
                }
                return 0;
            }
        };

    }

    public Bitmap getBitmapFromMemory(String url) {
        //Bitmap bitmap = mMemoryCache.get(url);//1.强引用方法
//        SoftReference<Bitmap> bitmap = mMemoryCache.get(url);
//        return bitmap;
        SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
        if (bitmapSoftReference != null) {
            return bitmapSoftReference.get();
        }
        return null;
    }

    public void setBitmapToMemory(String url, Bitmap bitmap) {
        //mMemoryCache.put(url, bitmap);//1.强引用方法
            /*2.弱引用方法
            mMemoryCache.put(url, new SoftReference<>(bitmap));
            */
        mMemoryCache.put(url,new SoftReference<Bitmap>(bitmap));
    }

    public void removeCache(String url) {
        mMemoryCache.remove(url);
    }
}
