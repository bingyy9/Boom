package com.boom.android.ui.adapter.repo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.boom.android.BoomApplication;
import com.boom.android.R;
import com.boom.android.SettingsActivity;
import com.boom.android.log.Dogger;
import com.boom.android.util.PrefsUtil;
import com.boom.android.viewmodel.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleSelectRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<SingleSelectBean> mDataList;
    SettingsViewModel settingsViewModel;
    Listener mListener;

    public interface Listener {
        void onItemSelected(SingleSelectBean bean);
    }

    public void setListener(Listener listener){
        this.mListener = listener;
    }

    public SingleSelectRecycleAdapter(Context ctx){
        mContext = ctx;
        mDataList = new ArrayList<>();
        settingsViewModel = new ViewModelProvider((SettingsActivity) ctx, new ViewModelProvider.AndroidViewModelFactory(BoomApplication.getInstance()))
                .get(SettingsViewModel.class);
    }

    public void setDataList(ArrayList<SingleSelectBean> items){
        this.mDataList = items;
    }

    public List<SingleSelectBean> getDataList(){
        return this.mDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.dlg_single_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SingleSelectBean bean = mDataList.get(position);
        if(!(holder instanceof ItemVH)){
            Dogger.i(Dogger.BOOM, "onBindUser not Item!!!", "SingleSelectRecycleAdapter", "onBindViewHolder");
            return;
        }

        ItemVH userHolder = (ItemVH) holder;
        userHolder.tvValue.setText(bean.getValue());
        userHolder.ivChecked.setVisibility(bean.getChecked()? View.VISIBLE :View.GONE);

        userHolder.container.setOnClickListener((v) ->{
            PrefsUtil.setFileNameFormat(BoomApplication.getInstance().getApplicationContext(), bean.getValue());
            if(settingsViewModel != null){
                settingsViewModel.postValueUpdated(SettingsViewModel.PostType.FILE_NAME_FORMAT);
            }
            if(mListener != null){
                mListener.onItemSelected(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null? 0: mDataList.size();
    }

    protected class ItemVH extends RecyclerView.ViewHolder{
        @BindView(R.id.cl_container)
        View container;
        @BindView(R.id.tv_value)
        TextView tvValue;
        @BindView(R.id.iv_checked)
        ImageView ivChecked;
        public ItemVH(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}