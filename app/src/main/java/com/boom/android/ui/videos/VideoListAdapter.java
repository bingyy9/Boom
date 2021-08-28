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
import com.boom.android.ui.videos.bean.VideoItemInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private AdapterListener adapterListener;
    private List<VideoItemInfo> mDataList ;
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

    public void setDataList(ArrayList<VideoItemInfo> items){
        this.mDataList = items;
    }

    public List<VideoItemInfo> getDataList(){
        return this.mDataList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoItemInfo videoItem = mDataList.get(position);
        if(!(holder instanceof ItemVH)){
            Dogger.i(Dogger.BOOM, "onBindUser not Item!!!", "VideoListAdapter", "onBindViewHolder");
            return;
        }

        ItemVH viewHolder = (ItemVH) holder;
        viewHolder.tvName.setText(videoItem.name);
        viewHolder.cardView.setOnClickListener((View v)->{
            if(adapterListener != null && viewHolder != null && viewHolder.tvName != null) {
                adapterListener.onRecycleItemSelected(viewHolder.tvName.getText().toString());
            }
        });
//        userHolder.tvLastModifiedTime.setText(videoItem.lastModifiedDate);
//        userHolder.iFrame.setImageBitmap();

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
//        super.onBindViewHolder(holder, position, payloads);
        if(holder == null){
            return;
        }
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {

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
        TextView tvLastModifiedTime;
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
