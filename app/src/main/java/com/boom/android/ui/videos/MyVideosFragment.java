package com.boom.android.ui.videos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.videos.bean.VideoItem;
import com.boom.android.ui.videos.bean.VideoItemDiffCallback;
import com.boom.android.util.BitmapUtils;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.FileUtils;
import com.boom.android.util.RecordHelper;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        videoListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoListView.setAdapter(videoListAdapter);
    }

    private void updateView(){
        Dogger.i(Dogger.BOOM, "", "MyVideosFragment", "updateView");
        compositeDisposable.add(Observable.create(observableEmitter -> {
            List<VideoItem> videoItems = buildVideoItems();
            VideoItemDiffCallback callback = new VideoItemDiffCallback(videoListAdapter.getDataList(), videoItems);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            if(videoListAdapter != null) {
                videoListAdapter.setDataList((ArrayList<VideoItem>) videoItems);
            }
            observableEmitter.onNext(result);
        }).observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.computation()).subscribe(result -> {
            ((DiffUtil.DiffResult)result).dispatchUpdatesTo(videoListAdapter);
        }));
    }

    private List<VideoItem> buildVideoItems(){
        List<VideoItem> items = new ArrayList<>();

        List<File> files = FileUtils.listMp4FileSortByModifyTime(BoomHelper.getRecordDirectory());
        for(File file: files){
            if(!file.exists()){
                continue;
            }
            Bitmap iFrame = BitmapUtils.createVideoThumbnail(file.getAbsolutePath());
            if(iFrame != null) {
                items.add(new VideoItem(file.getName())
                        .setLastModified(DataUtils.formatDate(file.lastModified()))
                        .setiFrame(iFrame)
                        .setLastModifyTime(file.lastModified()));
            }
        }

        return items;
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
        updateView();
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
                        updateView();
                        break;
                }
            }, 2000);
        }
    }
}
