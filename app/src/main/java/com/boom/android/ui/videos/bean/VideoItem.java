package com.boom.android.ui.videos.bean;


import android.graphics.Bitmap;

import java.util.Objects;

public class VideoItem {
    public String name;
    public String absolutePath;
    public String duration;
    public String width;
    public String height;
    public String size;
    public Bitmap iFrame;

    public VideoItem(String name) {
        this.name = name;
    }

    public VideoItem setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoItem videoItem = (VideoItem) o;
        return Objects.equals(name, videoItem.name)
                && Objects.equals(absolutePath, videoItem.absolutePath)
                && Objects.equals(duration, videoItem.duration)
                && Objects.equals(width, videoItem.width)
                && Objects.equals(height, videoItem.height)
                && Objects.equals(size, videoItem.size)
                && Objects.equals(iFrame, videoItem.iFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, absolutePath, duration, width, height, size, iFrame);
    }
}
