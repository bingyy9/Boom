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
import com.boom.android.util.FilesDirUtil;
import com.boom.android.util.RecordHelper;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyVideosFragment extends Fragment implements VideoListAdapter.AdapterListener
        , IRecordModel.RecordEvtListener, SwipeRefreshLayout.OnRefreshListener {
    View root;
    @BindView(R.id.video_list)
    RecyclerView videoListView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    VideoListAdapter videoListAdapter;

    Handler mHandler = new Handler();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MediaMetadataRetriever mediaMetadataRetriever;

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
        mediaMetadataRetriever = new MediaMetadataRetriever();
        initView();
        return root;
    }
    
    private void initView(){
        videoListAdapter = new VideoListAdapter(getActivity());
        videoListAdapter.setListener(this);
        videoListView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        videoListView.setAdapter(videoListAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_normal);
        swipeRefreshLayout.setOnRefreshListener(this);
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
            videoListAdapter.notifyItemChanged(0);
            videoListAdapter.notifyItemChanged(1);
            if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }));
    }

    private List<VideoItem> buildVideoItems(){
        List<VideoItem> items = new ArrayList<>();
        List<String> recordFileReadDirs = FilesDirUtil.getRecordFileReadDirs(getActivity());
        if(recordFileReadDirs != null){
            for (String dir: recordFileReadDirs){
                List<File> files = FileUtils.listMp4FileSortByModifyTime(dir);
                for(File file: files){
                    if(!file.exists()){
                        continue;
                    }

                    VideoItem videoItem = new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath());
                    videoItem.lastModified = file.lastModified();
                    getVideoItemDetails(videoItem);
                    getFileSize(videoItem);
                    items.add(videoItem);
                }
            }
        }
        Collections.sort(items);
        return items;
    }

    private void getVideoItemDetails(VideoItem videoItem){
        if(videoItem == null){
            return;
        }

        mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(videoItem.absolutePath);
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (StringUtils.isEmpty(duration)) {
                videoItem.duration = null;
            } else {
                try {
                    videoItem.duration = DataUtils.msecToTime(Integer.valueOf(duration));
                } catch (NumberFormatException e) {
                    Dogger.e(Dogger.BOOM, "duration: " + duration, "MyVideosFragment", "getVideoItemDetails", e);
                }
            }

            videoItem.width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            videoItem.height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        } catch (Exception e){
            Dogger.e(Dogger.BOOM, "videItem : " + videoItem.absolutePath, "MyVideosFragment", "getVideoItemDetails", e);
        } finally {
            try {
                if(mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            } catch (RuntimeException ex) {
                Dogger.e(Dogger.BOOM, "", "NetCacheUtils", "createVideoThumbnail", ex);
            }
        }
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
        if(swipeRefreshLayout != null
                && videoListAdapter != null
                && videoListAdapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
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
    public void onRecycleItemSelected(VideoItem item) {
        if(getActivity() == null || item == null){
            return;
        }
        Dogger.i(Dogger.BOOM, "name: " + item.name, "MyVideosFragment", "onRecycleItemSelected");

        VideoDetailActivity.start(getActivity(), item);

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
                        if(swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                        updateView(true);
                        break;
                }
            }, 500);
        }
    }

    private void updateLatestVideo(){
//        if(videoListAdapter != null) {
//            List<VideoItem> items = videoListAdapter.getDataList();
//            if(items == null || items.size() == 0){
//                return;
//            }
//            VideoItem item = items.get(0);
//            List<File> files = FileUtils.listMp4FileSortByModifyTime(BoomHelper.getRecordDirectory());
//            if (files != null && files.size() > 0) {
//                File file = files.get(0);
//                if(!file.exists()){
//                    return;
//                }
//                //only add latest one
//                if(item != null && StringUtils.contentEquals(item.name, file.getName())){
//                    Dogger.i(Dogger.BOOM, "notifyItemChanged", "MyVideosFragment", "updateLatestVideo");
//                    items.remove(0);
//                    items.add(0, new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath()));
//                    videoListAdapter.notifyItemChanged(0);
//                } else if(item != null && !StringUtils.contentEquals(item.name, file.getName())){
//                    Dogger.i(Dogger.BOOM, "notifyItemInserted", "MyVideosFragment", "updateLatestVideo");
//                    items.add(new VideoItem(file.getName()).setAbsolutePath(file.getAbsolutePath()));
//                    videoListAdapter.notifyItemInserted(0);
//                }
//            }
//            if(videoListView != null) {
//                videoListView.scrollToPosition(0);
//            }
//        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        updateView(false);
    }
}
