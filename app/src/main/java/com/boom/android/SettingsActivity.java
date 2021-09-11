package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boom.android.log.Dogger;
import com.boom.android.ui.adapter.repo.Resolution;
import com.boom.android.ui.dialog.AppDialogFragment;
import com.boom.android.ui.dialog.InputDialog;
import com.boom.android.ui.dialog.SingleSelectDialog;
import com.boom.android.util.FilesDirUtil;
import com.boom.android.util.Prefs;
import com.boom.android.util.PrefsUtil;
import com.boom.android.util.WindowUtils;
import com.boom.android.viewmodel.SettingsViewModel;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.layout_time_delay)
    View layoutTimeDelay;
    @BindView(R.id.layout_file_format)
    View layoutFileNameFormat;
    @BindView(R.id.layout_resolution)
    View layoutResolution;
    @BindView(R.id.layout_frame_rate)
    View layoutFrameRate;
    @BindView(R.id.layout_bitrate)
    View layoutBitrate;
    @BindView(R.id.layout_audio_bitrate)
    View layoutAudioBitrate;
    @BindView(R.id.layout_audio_channel)
    View layoutAudioChannel;
    @BindView(R.id.layout_audio_sample_rate)
    View layoutAudioSampleRate;
    @BindView(R.id.layout_record_audio)
    View layoutRecordAudio;

    @BindView(R.id.tv_current_delay_recording)
    TextView tvDelayRecording;
    @BindView(R.id.tv_file_name_format_value)
    TextView tvFileFormat;
    @BindView(R.id.tv_resolution_value)
    TextView tvResolution;
    @BindView(R.id.tv_bitrate)
    TextView tvBitrate;
    @BindView(R.id.tv_frame_rate)
    TextView tvFrameRate;
    @BindView(R.id.tv_storage_desc)
    TextView tvStorageLocation;
    @BindView(R.id.tv_audio_bitrate)
    TextView tvAudioBitrate;
    @BindView(R.id.tv_audio_sample_rate_desc)
    TextView tvAudioSampleRate;
    @BindView(R.id.tv_audio_channel_desc)
    TextView tvAudioChannel;
    @BindView(R.id.sc_record_audio)
    SwitchCompat scRecordAudio;


    SettingsViewModel settingsViewModel;

    public static void start(Context context) {
        if(context == null){
            return;
        }

        Intent intent = new Intent(context, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        settingsViewModel = new ViewModelProvider(SettingsActivity.this, new ViewModelProvider.AndroidViewModelFactory((this).getApplication()))
                .get(SettingsViewModel.class);
        initView();
    }

    private void initView() {
        layoutTimeDelay.setOnClickListener(this);
        layoutFileNameFormat.setOnClickListener(this);
        layoutResolution.setOnClickListener(this);
        layoutBitrate.setOnClickListener(this);
        layoutFrameRate.setOnClickListener(this);
        layoutAudioBitrate.setOnClickListener(this);
        layoutAudioSampleRate.setOnClickListener(this);
        layoutAudioChannel.setOnClickListener(this);
        layoutRecordAudio.setOnClickListener(this);
        settingsViewModel.getTimeDelayBeforeRecording().observe(this, (Boolean b)-> updateDelayRecording());
        settingsViewModel.getFileNameFormatUpdated().observe(this, (Boolean b)-> updateFileFormate());
        settingsViewModel.getBitrateUpdated().observe(this, (Boolean b)-> updateBitRate());
        settingsViewModel.getFrameRateUpdated().observe(this, (Boolean b)-> updateFrameRate());
        settingsViewModel.getResolutionUpdated().observe(this, (Boolean b)-> updateResolution());
        settingsViewModel.getAudioBitrateUpdated().observe(this, (Boolean b)-> updateAudioBitrate());
        settingsViewModel.getAudioSampleRateUpdated().observe(this, (Boolean b)-> updateAudioSampleRate());
        settingsViewModel.getAudioChannelUpdated().observe(this, (Boolean b)-> updateAudioChannel());

        scRecordAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefsUtil.setRecordAudio(this, isChecked);
        });
    }

    private void updateView(){
        updateDelayRecording();
        updateFileFormate();
        updateFrameRate();
        updateBitRate();
        updateResolution();
        updateStorage();
        updateAudioBitrate();
        updateAudioSampleRate();
        updateAudioChannel();
        updateRecordAudio();
    }

    private void updateDelayRecording(){
        tvDelayRecording.setText(this.getResources().getString(R.string.current_time,
                PrefsUtil.getTimeDelayBeforeRecording(this)));
    }

    private void updateFileFormate(){
        tvFileFormat.setText(PrefsUtil.getFileNameFormat(this));
    }

    private void updateFrameRate(){
        tvFrameRate.setText(this.getResources().getString(R.string.frame_rate_value, String.valueOf(PrefsUtil.getVideoFrameRate(this))));
    }

    private void updateBitRate(){
        tvBitrate.setText(this.getResources().getString(R.string.bitrate_value, String.valueOf(PrefsUtil.getVideoBitrate(this))));
    }

    private void updateResolution(){
        Resolution resolution = PrefsUtil.getResolution(this);
        if(resolution != null && tvResolution != null){
            if(resolution.getWidth() == 1){
                tvResolution.setText(BoomApplication.getInstance().getApplicationContext().getResources()
                        .getString(R.string.use_screen_resolution_short
                                , String.valueOf(WindowUtils.getScreenWidth(this))
                                , String.valueOf(WindowUtils.getScreenHeight(this))));
            } else {
                tvResolution.setText(this.getResources().getString(R.string.resolution_value, String.valueOf(resolution.getWidth()), String.valueOf(resolution.getHeight()), resolution.getRate()));
            }
        }
    }

    private void updateStorage(){
        tvStorageLocation.setText(FilesDirUtil.getRecordFileWriteDir(this));
    }

    private void updateAudioBitrate(){
        tvAudioBitrate.setText(this.getResources().getString(R.string.audio_bitrate_with_value, String.valueOf(PrefsUtil.getAudioBitrate(this))));
    }

    private void updateAudioSampleRate(){
        tvAudioSampleRate.setText(this.getResources().getString(R.string.audio_sample_rate_value, String.valueOf(PrefsUtil.getAudioSampleRate(this))));
    }

    private void updateRecordAudio(){
        scRecordAudio.setChecked(PrefsUtil.isRecordAudio(this));
    }

    private void updateAudioChannel(){
        tvAudioChannel.setText(this.getResources().getString(R.string.audio_channel_with_value, PrefsUtil.getAudioChannel(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dogger.i(Dogger.BOOM, "", "SettingsActivity", "onResume");
        updateView();
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }
        switch (view.getId()){
            case R.id.layout_time_delay:
                displayDialog(AppDialogFragment.TYPE_TIME_DELAY_BEFORE_RECORD);
                break;
            case R.id.layout_file_format:
                displayDialog(AppDialogFragment.TYPE_FILE_NAME_FORMAT_SELECT);
                break;
            case R.id.layout_resolution:
                displayDialog(AppDialogFragment.TYPE_RESOLUTION);
                break;
            case R.id.layout_frame_rate:
                displayDialog(AppDialogFragment.TYPE_FRAME_RATE);
                break;
            case R.id.layout_bitrate:
                displayDialog(AppDialogFragment.TYPE_BITRATE);
                break;
            case R.id.layout_audio_bitrate:
                displayDialog(AppDialogFragment.TYPE_AUDIO_BITRATE);
                break;
            case R.id.layout_audio_sample_rate:
                displayDialog(AppDialogFragment.TYPE_AUDIO_SAMPLE_RATE);
                break;
            case R.id.layout_audio_channel:
                displayDialog(AppDialogFragment.TYPE_AUDIO_CHANNEL);
                break;
            case R.id.layout_record_audio:
                PrefsUtil.setRecordAudio(this, !PrefsUtil.isRecordAudio(this));
                updateRecordAudio();
                break;
        }
    }

    private void displayDialog(int type){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        DialogFragment prev = (DialogFragment)fragmentManager.findFragmentByTag(AppDialogFragment.TAG);
        if (prev != null) {
            prev.dismiss();
        }

        switch (type){
            case AppDialogFragment.TYPE_TIME_DELAY_BEFORE_RECORD:
                InputDialog.newInstance(type).show(fragmentManager.beginTransaction(), AppDialogFragment.TAG);
                break;
            case AppDialogFragment.TYPE_FILE_NAME_FORMAT_SELECT:
            case AppDialogFragment.TYPE_RESOLUTION:
            case AppDialogFragment.TYPE_FRAME_RATE:
            case AppDialogFragment.TYPE_BITRATE:
            case AppDialogFragment.TYPE_AUDIO_BITRATE:
            case AppDialogFragment.TYPE_AUDIO_CHANNEL:
            case AppDialogFragment.TYPE_AUDIO_SAMPLE_RATE:
                SingleSelectDialog.newInstance(type).show(fragmentManager.beginTransaction(), AppDialogFragment.TAG);
                break;

        }
    }
}