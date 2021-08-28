package com.boom.android.ui.videos.bean;

import android.graphics.Bitmap;

public class VideoItemInfo {
    public String uuid;
    public String name;
    public long lastModifiedDate;
    public Bitmap iFrame;

    public VideoItemInfo(String name) {
        this.name = name;
    }
}
