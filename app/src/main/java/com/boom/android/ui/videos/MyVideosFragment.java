package com.boom.android.ui.videos;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boom.android.R;
import com.boom.android.VideoDetailActivity;
import com.boom.android.log.Dogger;
import com.boom.android.ui.videos.bean.VideoItem;
import com.boom.android.ui.videos.bean.VideoItemDiffCallback;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.FileSizeUtils;
import com.boom.android.util.FileUtils;
import com.boom.android.util.RecordHelper;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyVideosFragment extends Fragment implements VideoListAdapter.AdapterListener, IRecordModel.RecordEvtListener {
    View root;
    @BindView(R.id.video_list)
    RecyclerView videoListView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    VideoListAdapter videoListAdapter;

    Handler mHandler = new Handler();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static MyVideosFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        MyVideosFragment fragment = new MyVideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.my_videos_tab, container, false);
        ButterKnife.bind(this,root);
        initView();
        return root;
    }
    
    private void initView(){
        videoListAdapter = new VideoListAdapter(getActivity());
        videoListAdapter.setListener(this);
        videoListView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        videoListView.setAdapter(videoListAdapter);
    }

    private void updateView(boolean scrollToTop){
        Dogger.i(Dogger.BOOM, "", "MyVideosFragment", "updateView");
        compositeDisposable.add(Observable.create(observableEmitter -> {
            List<VideoItem> videoItems = buildVideoItems();
            VideoItemDiffCallback callback = new VideoItemDiffCallback(videoListAdapter.getDataList(), videoItems);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            if(videoListAdapter != null) {
                videoListAdapter.setDataList(videoItems);
            }
            observableEmitter.onNext(result);
        }).observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.computation()).subscribe(result -> {
            if(videoListAdapter != null && videoListAdapter.getItemCount() > 0){
                tvTip.setVisibility(View.GONE);
                videoListView.setVisibility(View.VISIBLE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                videoListView.setVisibility(View.GONE);
            }
            ((DiffUtil.DiffResult)result).dispatchUpdatesTo(videoListAdapter);
            if(scrollToTop && videoListView != null){
                videoListView.scrollToPosition(0);
            }
        }));
    }

    private List<VideoItem> buildVideoItems(){
        List<VideoItem> items = new ArrayList<>();
        List<File> files = FileUtils.listMp4FileSortByModifyTime(BoomHelper.getRecordDirectory());
        for(File file: files){
            if(!file.exists()){
                continue;
            }

            VideoItem videoItem = new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath());
            getVideoItemDetails(videoItem);
            getFileSize(videoItem);
            items.add(videoItem);
        }

        return items;
    }

    private void getVideoItemDetails(VideoItem videoItem){
        if(videoItem == null){
            return;
        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if(mmr == null){
            return;
        }
        mmr.setDataSource(videoItem.absolutePath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try{
            videoItem.duration = DataUtils.msecToTime(Integer.valueOf(duration));
        } catch (NumberFormatException e){
            Dogger.e(Dogger.BOOM, "", "MyVideosFragment", "getVideoItemDetails", e);
        }

        videoItem.width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        videoItem.height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
    }

    private void getFileSize(VideoItem videoItem){
        if(videoItem == null){
            return;
        }

        videoItem.size = FileSizeUtils.getAutoFileOrFilesSize(videoItem.absolutePath);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Dogger.i(Dogger.BOOM, "", "MyVideosFragment", "onResume");
        super.onResume();
        RecordHelper.registerRecordEventListner(this);
        updateView(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        RecordHelper.unregisterRecordEventListener(this);
    }

    @Override
    public void onDestroy() {
        Dogger.i(Dogger.BOOM, "", "MyVideosFragment", "onDestroy");
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onRecycleItemSelected(String name) {
        Dogger.i(Dogger.BOOM, "name: " + name, "MyVideosFragment", "onRecycleItemSelected");
        if(getActivity() == null){
            return;
        }

        VideoDetailActivity.start(getActivity(), name + BoomHelper.filePostfix);

//        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
//        if(supportFragmentManager == null){
//            return;
//        }
//
//        Fragment fragment =  supportFragmentManager.findFragmentByTag(VideoDetailFragment.TAG);
//        if(fragment != null) {
//            return;
//        }
//        VideoDetailFragment.newInstance(name).show(supportFragmentManager,VideoDetailFragment.TAG);
    }


    @Override
    public void onRecordEvt(RecordEvent evt) {
        if(evt == null){
            return;
        }

        if(evt.getType() == RecordEvent.RECORD_STOPPED){
            mHandler.postDelayed(()->{
                switch (evt.getType()) {
                    case RecordEvent.RECORD_STOPPED:
                        updateView(true);
//                        updateLatestVideo();
                        break;
                }
            }, 1500);
        }
    }

    private void updateLatestVideo(){
        if(videoListAdapter != null) {
            List<VideoItem> items = videoListAdapter.getDataList();
            if(items == null || items.size() == 0){
                return;
            }
            VideoItem item = items.get(0);
            List<File> files = FileUtils.listMp4FileSortByModifyTime(BoomHelper.getRecordDirectory());
            if (files != null && files.size() > 0) {
                File file = files.get(0);
                if(!file.exists()){
                    return;
                }
                //only add latest one
                if(item != null && StringUtils.contentEquals(item.name, file.getName())){
                    Dogger.i(Dogger.BOOM, "notifyItemChanged", "MyVideosFragment", "updateLatestVideo");
                    items.remove(0);
                    items.add(0, new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath()));
                    videoListAdapter.notifyItemChanged(0);
                } else if(item != null && !StringUtils.contentEquals(item.name, file.getName())){
                    Dogger.i(Dogger.BOOM, "notifyItemInserted", "MyVideosFragment", "updateLatestVideo");
                    items.add(new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath()));
                    videoListAdapter.notifyItemInserted(0);
                }
            }
            if(videoListView != null) {
                videoListView.scrollToPosition(0);
            }
        }
    }
}
