package com.boom.android.ui.videos.bean;

import android.graphics.Bitmap;

public class VideoItemInfo {
    public String uuid;
    public String name;
    public String lastModified;
    public Bitmap iFrame;

    public VideoItemInfo(String name) {
        this.name = name;
    }

    public VideoItemInfo setLastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public VideoItemInfo setiFrame(Bitmap iFrame) {
        this.iFrame = iFrame;
        return this;
    }
}
