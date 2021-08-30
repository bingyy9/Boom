package com.boom.android.ui.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.videos.bean.VideoItemInfo;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyVideosFragment extends Fragment implements VideoListAdapter.AdapterListener {
    View root;
    @BindView(R.id.video_list)
    RecyclerView videoListView;
    VideoListAdapter videoListAdapter;

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
        compositeDisposable.add(Observable.create(observableEmitter -> {
            List<VideoItemInfo> videoItems = buildVideoItems();
            observableEmitter.onNext(videoItems);
        }).observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.computation()).subscribe(videoItems -> {
            if(videoListAdapter != null) {
                videoListAdapter.setDataList((ArrayList<VideoItemInfo>) videoItems);
            }
            videoListAdapter.notifyDataSetChanged();
        }));
    }

    private List<VideoItemInfo> buildVideoItems(){
        List<VideoItemInfo> items = new ArrayList<>();

        List<File> files = FileUtil.listMp4FileSortByModifyTime(BoomHelper.getRecordDirectory());
        for(File file: files){
            if(!file.exists()){
                continue;
            }
            items.add(new VideoItemInfo(file.getName())
                    .setLastModified(BoomHelper.formatDate(file.lastModified())));
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
        updateView();
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


}
