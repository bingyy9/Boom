package com.boom.android.ui.videos.bean;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class VideoItem implements Parcelable {
    public String name;
    public String absolutePath;
    public String duration;
    public String width;
    public String height;
    public String size;

    public VideoItem(String name) {
        this.name = name;
    }

    protected VideoItem(Parcel in) {
        name = in.readString();
        absolutePath = in.readString();
        duration = in.readString();
        width = in.readString();
        height = in.readString();
        size = in.readString();
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

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
                && Objects.equals(size, videoItem.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, absolutePath, duration, width, height, size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(absolutePath);
        parcel.writeString(duration);
        parcel.writeString(width);
        parcel.writeString(height);
        parcel.writeString(size);
    }


}
