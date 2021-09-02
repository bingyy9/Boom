package com.boom.android.ui.videos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.videos.bean.VideoItem;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.cache.BitmapCacheUtils;
import com.boom.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.boom.android.ui.videos.bean.VideoItemDiffCallback.MODIFIED_TIME_UPDATED;
import static com.boom.android.ui.videos.bean.VideoItemDiffCallback.NAME_UPDATED;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private AdapterListener adapterListener;
    private List<VideoItem> mDataList ;
    public void setListener(AdapterListener listener){
        adapterListener = listener;
    }

    public VideoListAdapter(Context ctx){
        mContext = ctx;
        mDataList = new ArrayList<>();
    }

    public interface AdapterListener {
        void onRecycleItemSelected(String name);
    }

    public void setDataList(List<VideoItem> items){
        this.mDataList = items;
    }

    public List<VideoItem> getDataList(){
        return this.mDataList;
    }

    public int getSize(){
        return this.mDataList == null? 0: mDataList.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoItem videoItem = mDataList.get(position);
        if(!(holder instanceof ItemVH)){
            Dogger.i(Dogger.BOOM, "onBindUser not Item!!!", "VideoListAdapter", "onBindViewHolder");
            return;
        }

        ItemVH viewHolder = (ItemVH) holder;
        viewHolder.tvName.setText(videoItem.name.replace(BoomHelper.filePostfix, ""));
        if(!StringUtils.isEmpty(videoItem.lastModified)){
            viewHolder.tvLastModified.setText(videoItem.lastModified);
        }

        BitmapCacheUtils.getInstance().display(viewHolder.iFrame, videoItem.absolutePath);

        viewHolder.cardView.setOnClickListener((View v)->{
            if(adapterListener != null && viewHolder != null && viewHolder.tvName != null) {
                adapterListener.onRecycleItemSelected(viewHolder.tvName.getText().toString());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(holder == null){
            return;
        }
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ItemVH viewHolder = (ItemVH) holder;
            if(payloads.get(0) != null && payloads.get(0) instanceof Bundle){
                Bundle bundle = (Bundle) payloads.get(0);
                if(bundle != null) {
                    for(String key: bundle.keySet()){
                        switch (key){
                            case NAME_UPDATED:
                                String name = bundle.getString(NAME_UPDATED);
                                viewHolder.tvName.setText(name);
                                break;
                            case MODIFIED_TIME_UPDATED:
                                long time = bundle.getLong(MODIFIED_TIME_UPDATED);
                                viewHolder.tvLastModified.setText(DataUtils.formatDate(time));
                                break;
                        }
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataList == null? 0: mDataList.size();
    }

    protected class ItemVH extends RecyclerView.ViewHolder{
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.tv_last_modified_time)
        TextView tvLastModified;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.i_frame)
        ImageView iFrame;
        public ItemVH(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
