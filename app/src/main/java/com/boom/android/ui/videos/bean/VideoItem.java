package com.boom.android.ui.videos.bean;

import android.graphics.Bitmap;

import java.util.Objects;

public class VideoItem {
    public long lastModifyTime;
    public String name;
    public String lastModified;
    public Bitmap iFrame;

    public VideoItem(String name) {
        this.name = name;
    }

    public VideoItem setLastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public VideoItem setiFrame(Bitmap iFrame) {
        this.iFrame = iFrame;
        return this;
    }

    public VideoItem setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoItem videoItem = (VideoItem) o;
        return lastModifyTime == videoItem.lastModifyTime
                && Objects.equals(name, videoItem.name)
                && Objects.equals(lastModified, videoItem.lastModified)
                && Objects.equals(iFrame, videoItem.iFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastModifyTime, name, lastModified, iFrame);
    }
}
