package com.boom.android.ui.videos.bean;

import android.os.Bundle;
import com.boom.utils.StringUtils;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class VideoItemDiffCallback extends DiffUtil.Callback {
    public static final String NAME_UPDATED = "NAME_UPDATED";
    public static final String DURATION_UPDATED = "DURATION_UPDATED";
    public static final String RESOLUTION_WIDTH = "RESOLUTION_WIDTH";
    public static final String RESOLUTION_HEIGHT = "RESOLUTION_HEIGHT";
    public static final String SIZE_UPDATED = "SIZE_UPDATED";

    private List<VideoItem> current;
    private List<VideoItem> next;

    public VideoItemDiffCallback(List<VideoItem> current, List<VideoItem> next) {
        this.current = current;
        this.next = next;
    }

    @Override
    public int getOldListSize() {
        return current == null? 0 : current.size();
    }

    @Override
    public int getNewListSize() {
        return next == null? 0 : next.size();
    }

    //check if the same item
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if(current == null || next == null){
            return false;
        }

        VideoItem currentItem = current.get(oldItemPosition);
        VideoItem nextItem = next.get(newItemPosition);

        return (currentItem != null && nextItem != null
                && StringUtils.contentEquals(currentItem.name, nextItem.name));
    }

    //only above method return true, it's the same item, then it will check if the same content
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if(current == null || next == null){
            return false;
        }

        VideoItem currentItem = current.get(oldItemPosition);
        VideoItem nextItem = next.get(newItemPosition);
        return (currentItem != null && nextItem != null && currentItem.equals(nextItem));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        VideoItem currentItem = current.get(oldItemPosition);
        VideoItem nextItem = next.get(newItemPosition);

        Bundle bundle = new Bundle();
        if ((currentItem != null && nextItem !=null)
               && !StringUtils.contentEquals(currentItem.name, nextItem.name)) {
            bundle.putString(NAME_UPDATED, nextItem.name);
        }

        if((currentItem != null && nextItem !=null)
                && currentItem.duration != nextItem.duration){
            bundle.putString(DURATION_UPDATED, nextItem.duration);
        }

        if((currentItem != null && nextItem !=null)
                && (currentItem.width != nextItem.width
                    || currentItem.height != nextItem.height)){
            bundle.putString(RESOLUTION_HEIGHT, nextItem.height);
            bundle.putString(RESOLUTION_WIDTH, nextItem.width);
        }

        if((currentItem != null && nextItem !=null)
                && currentItem.size != nextItem.size){
            bundle.putString(SIZE_UPDATED, nextItem.size);
        }

        return bundle;
    }
}
