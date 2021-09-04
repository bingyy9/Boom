package com.boom.android.util.cache;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.boom.android.log.Dogger;
import java.util.Hashtable;

public class NetCacheUtils {
//    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private DiskLruCacheUtil mDiskLruCacheUtil;
    private MediaMetadataRetriever mediaMetadataRetriever;

    public NetCacheUtils(DiskLruCacheUtil diskLruCacheUtil, MemoryCacheUtils memoryCacheUtils) {
        mDiskLruCacheUtil = diskLruCacheUtil;
        mMemoryCacheUtils = memoryCacheUtils;
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public void getBitmapFromNet(ImageView ivPic, String url) {
        new BitmapTask().execute(ivPic, url);
    }

    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView ivPic;
        private String url;

        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (ImageView) params[0];
            url = (String) params[1];
            return downLoadBitmap(url);
        }

        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ivPic.setImageBitmap(result);
                Dogger.i(Dogger.BOOM, "Get bitmap from network", "BitmapTask", "onPostExecute");

                //从网络获取图片后,保存至本地缓存
                mDiskLruCacheUtil.setBitmapToLocal(url, result);
                //保存至内存中
                mMemoryCacheUtils.setBitmapToMemory(url, result);
            } else {
                Dogger.e(Dogger.BOOM, "Get bitmap from network null, url" + url, "BitmapTask", "onPostExecute");
            }
        }
    }

    private Bitmap downLoadBitmap(String url) {
        return createVideoThumbnail(url);
    }

    public Bitmap createVideoThumbnail(String filePath) {
        mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            if (filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("widevine://")) {
                mediaMetadataRetriever.setDataSource(filePath, new Hashtable<String, String>());
            } else {
                mediaMetadataRetriever.setDataSource(filePath);
            }
            bitmap = mediaMetadataRetriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //retriever.getFrameAtTime(-1);
        } catch (Exception ex) {
            Dogger.e(Dogger.BOOM, "", "NetCacheUtils", "createVideoThumbnail", ex);
        } finally {
            try {
                if(mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            } catch (RuntimeException ex) {
                Dogger.e(Dogger.BOOM, "", "NetCacheUtils", "createVideoThumbnail", ex);
            }
        }

        return bitmap;
    }
}
