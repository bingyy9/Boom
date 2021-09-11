package com.boom.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.boom.android.BoomApplication;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.adapter.repo.Resolution;
import com.boom.android.ui.adapter.repo.SingleSelectBean;
import com.boom.android.ui.adapter.SingleSelectRecycleAdapter;
import com.boom.android.ui.videos.WrapContentLinearLayoutManager;
import com.boom.android.util.ConfigUtil;
import com.boom.android.util.PrefsUtil;
import com.boom.android.viewmodel.SettingsViewModel;
import com.boom.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SingleSelectDialog extends AppDialogFragment implements SingleSelectRecycleAdapter.Listener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn1)
    View cancelButton;

    SettingsViewModel settingsViewModel;
    SingleSelectRecycleAdapter adapter;
    int checkedIndex;

    public static DialogFragment newInstance(int type) {
        DialogFragment fragment = new SingleSelectDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View rootView = inflater.inflate(R.layout.dlg_single_select, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, rootView);
        settingsViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.AndroidViewModelFactory(BoomApplication.getInstance()))
                .get(SettingsViewModel.class);

        initView();
        initList();
        return rootView;
    }

    private void initView(){
        updateTitle();
        cancelButton.setOnClickListener((v)->{
            this.dismiss();
        });
    }

    private void updateTitle(){
        switch (type){
            case TYPE_FILE_NAME_FORMAT_SELECT:
                tvTitle.setText(getResources().getString(R.string.file_name_format));
                break;
            case TYPE_BITRATE:
                tvTitle.setText(getResources().getString(R.string.bitrate_value, String.valueOf(PrefsUtil.getVideoBitrate(getActivity()))));
                break;
            case TYPE_FRAME_RATE:
                tvTitle.setText(getResources().getString(R.string.frame_rate_value, String.valueOf(PrefsUtil.getVideoFrameRate(getActivity()))));
                break;
            case TYPE_RESOLUTION:
                tvTitle.setText(getResources().getString(R.string.resolution));
                break;
            case TYPE_AUDIO_BITRATE:
                tvTitle.setText(getResources().getString(R.string.audio_bitrate));
                break;
            case TYPE_AUDIO_SAMPLE_RATE:
                tvTitle.setText(getResources().getString(R.string.audio_sample_rate));
                break;
            case TYPE_AUDIO_CHANNEL:
                tvTitle.setText(getResources().getString(R.string.audio_channel));
                break;
        }
    }

    private void initList(){
        if(recyclerView == null){
            Dogger.i(Dogger.BOOM, "recyclerView is null", "SingleSelectDialog", "initList");
            return;
        }

        adapter = new SingleSelectRecycleAdapter(getActivity());
        adapter.setListener(this);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new RecycleViewDecoration(getActivity(), RecycleViewDecoration.VERTICAL_LIST));
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }
            @Override
            public void onViewDetachedFromWindow(View v) {
                if(recyclerView != null) {
                    recyclerView.clearFocus();
                }
            }
        });

        //the first time need to build data in UI thread, otherwise the UI will flash issue
        updateView();

    }

    private List<SingleSelectBean> buildListData() {
        List<SingleSelectBean> beans = new ArrayList<>();
        switch (type){
            case TYPE_FILE_NAME_FORMAT_SELECT:
                buildFileNameData(beans);
                break;
            case TYPE_BITRATE:
                buildBitrateData(beans);
                break;
            case TYPE_FRAME_RATE:
                buildFrameRateData(beans);
                break;
            case TYPE_RESOLUTION:
                buildResolutionData(beans);
                break;
            case TYPE_AUDIO_BITRATE:
                buildAudioBitrateData(beans);
                break;
            case TYPE_AUDIO_SAMPLE_RATE:
                buildAudioSampleRateData(beans);
                break;
            case TYPE_AUDIO_CHANNEL:
                buildAudioChannelData(beans);
                break;
        }
        return beans;
    }

    private void buildFileNameData(List<SingleSelectBean> beans){
        if (ConfigUtil.fileNameFormats == null || ConfigUtil.fileNameFormats.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.fileNameFormats.size(); i++) {
            boolean checked = StringUtils.contentEquals(ConfigUtil.fileNameFormats.get(i), PrefsUtil.getFileNameFormat(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.fileNameFormats.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildBitrateData(List<SingleSelectBean> beans){
        if (ConfigUtil.bitRates == null || ConfigUtil.bitRates.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.bitRates.size(); i++) {
            boolean checked = (ConfigUtil.bitRates.get(i) == PrefsUtil.getVideoBitrate(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.bitRates.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildFrameRateData(List<SingleSelectBean> beans){
        if (ConfigUtil.frameRates == null || ConfigUtil.frameRates.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.frameRates.size(); i++) {
            boolean checked = (ConfigUtil.frameRates.get(i) == PrefsUtil.getVideoFrameRate(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.frameRates.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildResolutionData(List<SingleSelectBean> beans){
        if (ConfigUtil.resolutions == null || ConfigUtil.resolutions.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.resolutions.size(); i++) {
            boolean checked = (ConfigUtil.resolutions.get(i).equals(PrefsUtil.getResolution(getActivity())));
            beans.add(new SingleSelectBean(ConfigUtil.resolutions.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildAudioBitrateData(List<SingleSelectBean> beans){
        if (ConfigUtil.audioBitrates == null || ConfigUtil.audioBitrates.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.audioBitrates.size(); i++) {
            boolean checked = (ConfigUtil.audioBitrates.get(i) == PrefsUtil.getAudioBitrate(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.audioBitrates.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildAudioSampleRateData(List<SingleSelectBean> beans){
        if (ConfigUtil.audioSampleRates == null || ConfigUtil.audioSampleRates.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.audioSampleRates.size(); i++) {
            boolean checked = (ConfigUtil.audioSampleRates.get(i) == PrefsUtil.getAudioSampleRate(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.audioSampleRates.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void buildAudioChannelData(List<SingleSelectBean> beans){
        if (ConfigUtil.audioChannels == null || ConfigUtil.audioChannels.size() == 0) {
            return;
        }

        for (int i = 0; i < ConfigUtil.audioChannels.size(); i++) {
            boolean checked = (ConfigUtil.audioChannels.get(i).equals(PrefsUtil.getAudioChannel(getActivity())));
            beans.add(new SingleSelectBean(ConfigUtil.audioChannels.get(i), checked));
            if (checked) {
                checkedIndex = i;
            }
        }
    }

    private void updateView(){
        List<SingleSelectBean> languages = buildListData();
        if(adapter != null && adapter.getDataList() != null && languages != null) {
            adapter.getDataList().clear();
            adapter.getDataList().addAll(languages);
            adapter.notifyDataSetChanged();
        }

        if(checkedIndex > 8){
            recyclerView.scrollToPosition(checkedIndex - 1);
        } else {
            //first screen can display, needn't scroll
        }
    }

    @Override
    public void onItemSelected(SingleSelectBean bean) {
        SettingsViewModel.PostType postType = SettingsViewModel.PostType.NONE;
        switch (type){
            case TYPE_FILE_NAME_FORMAT_SELECT:
                PrefsUtil.setFileNameFormat(BoomApplication.getInstance().getApplicationContext(), (String)bean.getValue());
                postType = SettingsViewModel.PostType.FILE_NAME_FORMAT;
                break;
            case TYPE_BITRATE:
                PrefsUtil.setVideoBitrate(BoomApplication.getInstance().getApplicationContext(), (Integer)bean.getValue());
                postType = SettingsViewModel.PostType.BITRATE;
                break;
            case TYPE_FRAME_RATE:
                PrefsUtil.setVideoFrameRate(BoomApplication.getInstance().getApplicationContext(), (Integer)bean.getValue());
                postType = SettingsViewModel.PostType.FRAME_RATE;
                break;
            case TYPE_RESOLUTION:
                PrefsUtil.setResolution(BoomApplication.getInstance().getApplicationContext(), (Resolution) bean.getValue());
                postType = SettingsViewModel.PostType.RESOLUTION;
                break;
            case TYPE_AUDIO_BITRATE:
                PrefsUtil.setAudioBitrate(BoomApplication.getInstance().getApplicationContext(), (Integer)bean.getValue());
                postType = SettingsViewModel.PostType.AUDIO_BITRATE;
                break;
            case TYPE_AUDIO_SAMPLE_RATE:
                PrefsUtil.setAudioSampleRate(BoomApplication.getInstance().getApplicationContext(), (Integer)bean.getValue());
                postType = SettingsViewModel.PostType.AUDIO_SAMPLE_RATE;
                break;
            case TYPE_AUDIO_CHANNEL:
                PrefsUtil.setAudioChannel(BoomApplication.getInstance().getApplicationContext(), (String) bean.getValue());
                postType = SettingsViewModel.PostType.AUDIO_CHANNEL;
                break;
        }
        if(settingsViewModel != null){
            settingsViewModel.postValueUpdated(postType);
        }
        this.dismiss();
    }
}
